/*
 * Created on May 16, 2006
 *
 */

package mil.af.rl.jcat.bayesnet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import mil.af.rl.jcat.exceptions.GraphLoopException;
import mil.af.rl.jcat.plan.AbstractPlan;
import mil.af.rl.jcat.plan.Event;
import mil.af.rl.jcat.plan.Mechanism;
import mil.af.rl.jcat.plan.PlanItem;
import mil.af.rl.jcat.plan.ResourceAllocation;
import mil.af.rl.jcat.processlibrary.Library;
import mil.af.rl.jcat.exceptions.SignalException;
import mil.af.rl.jcat.processlibrary.SignalType;
import mil.af.rl.jcat.util.Guid;
import mil.af.rl.jcat.util.MaskedFloat;
import mil.af.rl.jcat.util.MultiMap;

import org.apache.log4j.Logger;


/**
 * This Class constructs A BayesNet out of information out of AbstractPlan and ProcessLibrary.
 * Any loading of information out of AbstractPlan Takes Place Here
 * @author craig
 *
 *
 */
public class JCatBayesBuilder implements BayesBuilder
{

	private int timeSpan;
	private AbstractPlan plan;
	private Library lib;
	private ArrayList<NetNode> netNodes = new ArrayList<NetNode>();
	private static Logger logger = Logger.getLogger(JCatBayesBuilder.class);

	public JCatBayesBuilder(AbstractPlan plan, Library lib)
	{
		super();
		this.plan = plan;
		this.lib = lib;
	}

	public BayesNet buildBayesNet(int timeSpan) throws GraphLoopException, SignalException
	{
		this.timeSpan = timeSpan;
		buildNetNodes();
		buildAdders();
		//consolidateEffects();
		netNodes = topoSortNetNodes();
		//printNodeDepths();
		this.BuildCPTs();
		//exportToGenie(new File("NewBayesNetStructureTest" + ".xdsl"));  <--method inside BayesNet now
		return new BayesNet(this.netNodes);
	}

	private void printNodeDepths()
	{
		for(NetNode node : netNodes)
		{
			logger.info("Name: " + node.getName() + "   Depth " + node.depth + " ClosesLoop: " + node.closesLoop);
		}

	}

	private void buildAdders()
	{
		ArrayList<NetNode> adders = new ArrayList<NetNode>();
		HashMap<Guid, ArrayList<NetNode>> adderSigs = new HashMap<Guid, ArrayList<NetNode>>();;
		Iterator<NetNode> i = this.netNodes.iterator();
		while(i.hasNext())
		{
			NetNode node = i.next();
			if(node.type != NodeType.Process)
				continue;
			adderSigs.clear();
			ArrayList<NetNode> inputs = new ArrayList<NetNode>();
			inputs.addAll(node.causes);
			inputs.addAll(node.inhibitors);
			Iterator<NetNode> j = inputs.iterator();
			while(j.hasNext())
			{
				NetNode jNode = j.next();
				if(adderSigs.get(jNode.getLibID()) == null)
				{
					ArrayList<NetNode> nodes = new ArrayList<NetNode>();
					nodes.add(jNode);
					adderSigs.put(jNode.getLibID(), nodes);
				}
				else
				{
					adderSigs.get(jNode.getLibID()).add(jNode);
				}
			}
			if(adderSigs.size() == 0)
				continue;
			Iterator<Guid> k = adderSigs.keySet().iterator();
			while(k.hasNext())
			{
				ArrayList<NetNode> sigList = adderSigs.get(k.next());
				if(sigList.size() <= 1)
					continue;
				NetNode adder = new NetNode(sigList.get(0).getLibID(), NodeType.Adder);
				adder.setName(sigList.get(0).getName() + " " + adder.getName());
				adders.add(adder);
				//Remove the Causes from the Node
				Iterator<NetNode> l = sigList.iterator();
				while(l.hasNext())
				{
					NetNode adderSig = l.next();
					//Remove the effect from the node and add the adder
					adderSig.removeEffect(node);
					adderSig.addEffect(adder);
					//Swap the causes and inhibitors to the adder
					if(node.removeCause(adderSig))
					{
						adder.addCause(adderSig);
					}
					else
					{
						node.removeInhibitor(adderSig);
						adder.addInhbitor(adderSig);
					}
				}
				//Hook up the adder
				node.addCause(adder);
				adder.addEffect(node);
			}

		}
		//Add All the Adders to the netnodes
		netNodes.addAll(adders);
	}

	private void consolidateEffects()
	{
		ArrayList<NetNode> consolidators = new ArrayList<NetNode>();
		HashMap<Guid, ArrayList<NetNode>> consSigs = new HashMap<Guid, ArrayList<NetNode>>(4);
		Iterator<NetNode> i = netNodes.iterator();
		while(i.hasNext())
		{
			NetNode node = i.next();
			if(node.type != NodeType.Process)
				continue;
			consSigs.clear();
			for(int j = 0; j < node.effects.size(); j++)
			{
				NetNode jNode = node.effects.get(j);
				if(consSigs.get(jNode.getLibID()) == null)
				{
					ArrayList<NetNode> nodes = new ArrayList<NetNode>();
					nodes.add(jNode);
					consSigs.put(jNode.getLibID(), nodes);
				}
				else
				{
					consSigs.get(jNode.getLibID()).add(jNode);
				}
			}
			if(consSigs.size() == 0)
				continue;
			Iterator<Guid> k = consSigs.keySet().iterator();
			while(k.hasNext())
			{
				ArrayList<NetNode> sigList = consSigs.get(k.next());
				if(sigList.size() <= 1)
					continue;
				NetNode consolidator = new NetNode(sigList.get(0).getLibID(), NodeType.Consolidator);
				consolidators.add(consolidator);
				//Redo the hookups to make a consolidator
				Iterator<NetNode> l = sigList.iterator();
				while(l.hasNext())
				{
					NetNode consSig = l.next();
					//Remove the effect from the node and put it on the consolidator
					consSig.removeCause(node);
					consSig.addCause(consolidator);
					node.removeEffect(consSig);
					consolidator.addEffect(consSig);
				}
				node.addEffect(consolidator);
				consolidator.addCause(node);
			}
		}
		//Add all consolidators to the netNodes
		netNodes.addAll(consolidators);
	}

	public String getNetworkName()
	{
		return "Method not implemented";
	}

	public void buildNetNodes()
	{
		for(Policy p : plan.activePolicies)
		{
			p.reset();
		}
		HashMap<Guid, NetNode> events = new HashMap<Guid, NetNode>();
		Iterator<Event> i = plan.getAllEvents().iterator();
		//Make the events. They need to be present before making mechanism nodes.
		while(i.hasNext())
		{
			Event ev = i.next();
			NetNode node = new NetNode(ev.getGuid(), ev.getProcessGuid(), NodeType.Process);
			loadTemporalData(node);
			events.put(node.getPlanID(), node);
			node.setName(ev.getName());
			node.setDescription(ev.getLabel());
			node.setResourceUseType(ev.getResourceUseType());
			if(node.getResourceUseType() == NetNode.RESOURCE_USE_OR)
			{
				//I am hacking this so I can get to writing Jeff Plaxton the response he is waiting for.
				//I need to make this WORK NOW, not delay him while I straighten this terminology out
				node.setResourceUseType(NetNode.HIGHEST_PROB_USE);
			}
			node.setPolicy(ev.inPolicy);
			this.netNodes.add(node);
		}

		Iterator<Mechanism> m = plan.getAllMechanisms().iterator();
		//Go through the events again and hook up mechanism nodes -CM
		while(m.hasNext())
		{
			Mechanism mech = m.next();
			NetNode mechNode = new NetNode(mech.getGuid(), mech.getSignalGuid(), mech.isConsolidator() ? NodeType.Consolidator : NodeType.Signal);
			this.netNodes.add(mechNode);
			mechNode.closesLoop = mech.isLoopCloser();
			mechNode.setName(mech.getName());

			NetNode fromEvent = events.get(mech.getFromEvent());
			fromEvent.addEffect(mechNode);
			mechNode.addCause(fromEvent);
			Iterator<Guid> toEvents = mech.getInfluencedEvents().iterator();
			while(toEvents.hasNext())
			{
				NetNode toEvent = events.get(toEvents.next());
				Event planNode = (Event) plan.getItem(toEvent.getPlanID());

				Iterator<Guid> it = planNode.getCauses().iterator();
				while(it.hasNext())
				{
					// shouldn't this be a .equals()??, yes it should be, == broke shit when collaborating, since object references are not valid
					// correction made by MikeD here and in inhibitor loop below
					if(it.next().equals(mechNode.getPlanID()))
						toEvent.addCause(mechNode);
				}

				it = planNode.getInhibitors().iterator();
				while(it.hasNext())
				{
					if(it.next().equals(mechNode.getPlanID()))
						toEvent.addInhbitor(mechNode);
				}
				mechNode.addEffect(toEvent);
			}

			loadTemporalData(mechNode);

			/*NetNode node = events.get(ev.getGuid());
			 //Handle the causes
			 for(Iterator<Guid> j = ev.getCauses().iterator(); j.hasNext(); )
			 {
			 Guid g = j.next();
			 Mechanism mech = (Mechanism)plan.getItem(g);
			 NetNode mechNode = new NetNode(g, mech.getSignalGuid(), NodeType.Signal);
			 loadTemporalData(mechNode);
			 mechNode.closesLoop = mech.isLoopCloser();
			 mechNode.setName(mech.getName());
			 mechNode.addCause(events.get(mech.getFromEvent()));
			 mechNode.addEffect(events.get(mech.getToEvent(0)));
			 //Hook up the causes and effects to the event nodes -CM
			 node.addCause(mechNode);
			 events.get(mech.getFromEvent()).addEffect(mechNode);
			 this.netNodes.add(mechNode);
			 
			 }      */
		}
	}

	public void loadTemporalData(NetNode node)
	{
		PlanItem item = plan.getItem(node.getPlanID());

		node.schedule = loadSchedule(item);
		node.evidence = loadEvidence(item);
		node.continuationProbability = item.getContinuation();
		node.nodeEffectSliceDelay = item.getDelay();
		node.persistence = item.getPersistence();
		node.resources = null;

		HashMap<Guid, ResourceAllocation> resources = item.getResources();
		MultiMap<Integer, ResourceAllocation> threats = item.getThreatResources();
		//use the first element only right now... put a check here for if theres no resources
		if(resources != null && resources.size() >= 1)
		{
			ResourceAllocation firstResource = (ResourceAllocation) resources.values().toArray()[0];
			node.resources = new SimpleResource(firstResource.getAllocated());
			node.inferredResources = new SimpleResource(firstResource.getAllocated());
			node.resources.isContingent = firstResource.isContingent();
			node.inferredResources.isContingent = firstResource.isContingent();
		}
		if(threats != null && threats.size() >= 1)
		{
			// note that currently you cannot have both
			//TODO:  THIS NEEDS TO BE FINISHED STILL BY CRAIG

			TreeMap<Integer, Integer> tempTResMap = new TreeMap<Integer, Integer>();
			Iterator threatKeys = threats.keySet().iterator();
			while(threatKeys.hasNext())
			{
				Integer tResTime = (Integer) threatKeys.next();
				List<ResourceAllocation> tResList = (List<ResourceAllocation>) threats.get(tResTime);

				//THIS NEEDS TO BE FINISHED TO USE ALL (NOT JUST FIRST) ALLOCATION AT EACH TIME
				tempTResMap.put(tResTime, tResList.get(0).getAllocated());
			}

			node.resources = new ThreatResource(tempTResMap);
			node.inferredResources = new ThreatResource(tempTResMap);
		}
		if(node.persistence < 1)
		{
			//To account for conversion error
			node.persistence = 1;
		}
		if(item.getItemType() == PlanItem.EVENT)
		{
			Event ev = (Event) item;
			node.leak = new float[timeSpan];
			for(int j = 0; j < timeSpan; j++)
			{// this is just a hack to support
				// early debugging
				node.leak[j] = ev.getLeak();
			}
		}
	}

	private HashMap loadEvidence(PlanItem item)
	{
		HashMap<Integer, Object> sliceEvidence = null;
		HashMap timeEvidence = item.getEvidence();
		if(timeEvidence.size() == 0)
			return null; //there is no evidence
		sliceEvidence = new HashMap<Integer, Object>();
		// Convert from time base to slice base
		for(Iterator i = timeEvidence.entrySet().iterator(); i.hasNext();)
		{
			Map.Entry ent = (Map.Entry) i.next();
			int newEntrySlice = ((Integer) ent.getKey()).intValue();
			// need check for conflicts in here
			sliceEvidence.put(new Integer(newEntrySlice), ent.getValue());
		}
		return sliceEvidence;
	}

	private float[] loadSchedule(PlanItem item)
	{
		float schedule[] = null;
		Map sched = null;
		sched = item.getSchedule();
		if(sched != null && !sched.isEmpty())
		{
			schedule = new float[timeSpan];
			for(int j = 0; j < timeSpan; j++)
				schedule[j] = -1.0f; // that is, not scheduled
			for(Iterator i = sched.entrySet().iterator(); i.hasNext();)
			{
				Object oentry = i.next();

				Map.Entry entry = (Map.Entry) oentry;
				int time = ((Integer) entry.getKey()).intValue();
				float prob = ((MaskedFloat) entry.getValue()).floatValue();
				if(time < schedule.length)
				{
					schedule[time] = prob;
				}
			}
		}
		return schedule;
	}

	public ArrayList<NetNode> topoSortNetNodes() throws GraphLoopException
	{
		// set all nodes to depth 0
		for(int j = 0; j < netNodes.size(); j++)
		{
			((NetNode) netNodes.get(j)).setDepth(0);
		}
		boolean changes = true;
		int maxDepth = 0;
		while(changes && maxDepth < netNodes.size())
		{
			changes = false;
			// set each node to a depth one greater than the depth of its
			// currently deepest parent
			for(int j = 0; j < netNodes.size(); j++)
			{
				NetNode currentNode = (NetNode) netNodes.get(j);
				if(checkNodeDepth(currentNode))
				{// the depth changed
					changes = true;
					int thisNodeDepth = currentNode.getDepth();
					if(thisNodeDepth > maxDepth)
					{
						maxDepth = thisNodeDepth;
					}
				}
			}
		}
		//TODO make this a better guess after learning some graph theory
		//Even if we get a real loop the depth will be much larger when we try to count it
		//So allowing a depth greater than this shouldn't cause too much trouble
		logger.info("Max Depth: " + maxDepth + "  " + "NetNodes: " + netNodes.size());
		if(maxDepth > netNodes.size() * 2)
		{
			logger.warn("There is a loop in this graph");
			throw new GraphLoopException("There is a loop in this graph so the Bayes Net cannot be built");
		}
		else
		{
			int currentDepth = 0;
			int nextSortedEntry = 0;
			ArrayList<NetNode> sortedNodes = new ArrayList<NetNode>(netNodes.size());
			//System.out.println(" ");
			while(currentDepth <= maxDepth)
			{
				for(int j = 0; j < netNodes.size(); j++)
				{
					NetNode currentNode = ((NetNode) netNodes.get(j));
					if(currentNode.getDepth() == currentDepth)
					{
						sortedNodes.add(nextSortedEntry++, currentNode);
						if(currentNode.depth == 0 && currentNode.getType() == NodeType.Signal)
						{
							logger.warn("topoSortNetNodes - problem: there's a signal at depth 0");
							throw new GraphLoopException("Disaster has struck, we " + "have a signal at the top of our graph");
						}
					}
				}
				currentDepth += 1;
			}
			return sortedNodes;
		}

	}

	public boolean checkNodeDepth(NetNode node)
	{
		boolean retVal = false;

		for(int j = 0; j < node.causes.size(); j++)
		{
			if(!((NetNode) node.causes.get(j)).closesLoop)
			{
				int parentDepth = ((NetNode) node.causes.get(j)).getDepth();
				if(parentDepth >= node.getDepth())
				{
					retVal = true;
					node.setDepth(parentDepth + 1);
				}
			}
		}
		for(int j = 0; j < node.inhibitors.size(); j++)
		{
			if(!((NetNode) node.inhibitors.get(j)).closesLoop)
			{
				int parentDepth = ((NetNode) node.inhibitors.get(j)).getDepth();
				if(parentDepth >= node.getDepth())
				{
					retVal = true;
					node.setDepth(parentDepth + 1);
				}
			}
			else
				continue;
		}
		return retVal;
	}

	public void BuildCPTs() throws SignalException
	{
		Iterator<NetNode> i = netNodes.iterator();
		while(i.hasNext())
		{
			this.BuildCPTs(i.next());
		}
	}

	public void BuildCPTs(NetNode node) throws SignalException
	{
		node.causalCPT = new Vector<Float>(); //will be sized later
		Vector<Guid> causalOrder = new Vector<Guid>();// so this might be a little big
		node.inhibitingCPT = new Vector<Float>(); //will be sized later
		Vector<Guid> inhibitingOrder = new Vector<Guid>();// so this might be a little big

		if(node.type == NodeType.Process)
		{
			Vector<Guid> causalSignals = getLibraryIDs(node.causes);
			lib.getCPT(node.getLibID(), causalSignals, node.causalCPT, causalOrder, SignalType.CAUSAL);
			sortModeNodes(node.causes, causalOrder);

			Vector<Guid> inhibitingSignals = getLibraryIDs(node.inhibitors);
			lib.getCPT(node.getLibID(), inhibitingSignals, node.inhibitingCPT, inhibitingOrder, SignalType.INHIBITING);
			sortModeNodes(node.inhibitors, inhibitingOrder);

		}
		else if(node.type == NodeType.Adder)
		{// this test must before the isSignal test
			//System.out.println("CPT for Adder Node = " + signalID);
			Vector<Guid> causalSignals = getLibraryIDs(node.causes);
			lib.getSignalAdder(node.getLibID(), causalSignals, node.causalCPT, causalOrder);

			Vector<Guid> inhibitingSignals = getLibraryIDs(node.inhibitors);
			lib.getSignalAdder(node.getLibID(), inhibitingSignals, node.inhibitingCPT, inhibitingOrder);

		}
		else if((node.type == NodeType.Signal) || (node.type == NodeType.Consolidator))
		{
			// TODO getSignalCPT is hardcoded. We need to fix this
			lib.getSignalCPT(node.getLibID(), null, node.causalCPT, causalOrder);
			lib.getSignalInhibitingCPT(node.getLibID(), node.inhibitingCPT);
		}
	}

	/**
	 * Returns the collection of Library IDs from and arraylist of netnodes
	 * @param items
	 * @return
	 */
	private Vector<Guid> getLibraryIDs(ArrayList<NetNode> items)
	{
		Vector<Guid> sigs = new Vector<Guid>(items.size());
		for(int i = 0; i < items.size(); i++)
			sigs.add(items.get(i).getLibID());
		return sigs;
	}

	private void sortModeNodes(ArrayList<NetNode> name, Vector<Guid> ordering)
	{
		if(ordering.size() == 0)
			return;
		ArrayList<NetNode> sortedModeNodes = new ArrayList<NetNode>(ordering.size());
		Iterator<Guid> i = ordering.iterator();
		while(i.hasNext())
		{
			Guid nextInOrder = i.next();
			Iterator<NetNode> j = name.iterator();
			while(j.hasNext())
			{
				NetNode next = j.next();
				if(next.getLibID() == nextInOrder)
				{
					sortedModeNodes.add(next);
					break;
				}
			}
		}
		name.clear();
		name.addAll(sortedModeNodes);
	}
}
