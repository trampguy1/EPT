/*
 * Created on May 11, 2006
 *
 */

package mil.af.rl.jcat.bayesnet;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import mil.af.rl.jcat.bayesnet.explaination.ExplanationData;
import mil.af.rl.jcat.util.Guid;


public class BayesNet
{

	private HashMap<Guid, NetNode> netNodes = new HashMap<Guid, NetNode>();
	private Sampler sampler = null;
	private static Logger logger = Logger.getLogger(BayesNet.class);

	public BayesNet(ArrayList<NetNode> netNodes)
	{
		super();
		mapNetNodes(netNodes);
		initializeBayesNet(netNodes);
	}

	private void mapNetNodes(ArrayList<NetNode> nodes)
	{
		for(NetNode node : nodes)
		{
			netNodes.put(node.getPlanID(), node);
		}

	}

	private void initializeBayesNet(ArrayList<NetNode> sortedNodes)
	{
		boolean resourced = false;
		for(NetNode n : sortedNodes)
		{
			if(n.resources != null)
			{
				resourced = true;
				break;
			}
		}
		if(resourced)
		{
			sampler = new ResourcedLHSampler(sortedNodes);
		}
		else
		{
			sampler = new LikelihoodSampler(sortedNodes);
		}
	}

	/**
	 * Starts the sampler contained in this BayesNet with the given time length
	 * @param timeSpan
	 */
	public void sampleDistribution(int timeSpan)
	{
		sampler.sampleDistribution(timeSpan);
	}

	/**
	 * Returns the number of samples currently drawn from the sampler
	 * 
	 * @return int sampleCount
	 */
	public int getSampleCount()
	{
		return sampler.getSampleCount();
	}

	/**
	 * Returns the number of slices we are sampling over
	 * 
	 * @return int sliceCount
	 */
	public int getTimespan()
	{
		return sampler.getTimespan();
	}

	protected NetNode getNetNode(Guid nodeID)
	{
		return this.netNodes.get(nodeID);
	}

	/**
	 * Determines whether the internal sampler is currently running
	 * @return
	 */
	public boolean isSampling()
	{
		return sampler.isSampling();
	}

	public boolean isSamplerPaused()
	{
		return sampler.isSamplerPaused();
	}

	/**
	 * Pause the sampling thread contained in this BayesNet
	 * @return
	 */
	public boolean pauseSampler()
	{
		return sampler.pauseSampler();
	}

	/**
	 * Un-pause the sampling thread contained in this BayesNet
	 * @return
	 */
	public boolean unpauseSampler()
	{
		return sampler.unpauseSampler();
	}

	/**
	 * Stop the sampling thread contained in this BayesNet
	 * @return
	 */
	public boolean killSampler()
	{
		return sampler.killSampler();
	}

	public double[] getInferredProbs(Guid planID)
	{
		return sampler.getInferredProbs(getNetNode(planID));
	}

	public double[] getPredictedProbs(Guid planID)
	{
		return sampler.getPredictedProbs(getNetNode(planID));
	}

	/**
	 * Get the sampler being used for this Bayes Network
	 * @return
	 */
	public Sampler getSampler()
	{
		return sampler;
	}

	public ExplanationData getWhyData(Guid guid)
	{
		return sampler.getWhyData(getNetNode(guid));
	}

	public void initializeExplanation(Guid nodeID, int time)
	{
		sampler.initializeExplanation(getNetNode(nodeID), time);
	}

	public void initializeParentExplaination(Guid childNode, Guid parent)
	{
		sampler.initializeParentExplaination(getNetNode(childNode), getNetNode(parent));
	}

	public List<NetNode> getExplainNodes(Guid planid)
	{
		return getNetNode(planid).getExplainNodes();
	}

	/**
	 * Exports our bayes net as a format that can be read by GENIE to check
	 * structure for consistency against our theorey
	 *
	 * @author Craig McNamara
	 */
	public void exportToGenie(java.io.File outFile)
	{
		//Create Document and Document Writer
		Document doc = DocumentHelper.createDocument();
		//Create Heading for Genie file
		Element root = doc.addElement("smile").addAttribute("version", "1.0").addAttribute("id", "Network1").addAttribute("numsamples", "1000");
		//Create XML element to represent CPT's
		Element cpts = root.addElement("nodes");
		//Output the List of nodes and their names
		Element xtensions = root.addElement("extensions");
		Element genie = xtensions.addElement("genie").addAttribute("version", "1.0").addAttribute("name", "Network1").addAttribute("faultnameformat", "nodestate");

		Collection<NetNode> nodes = netNodes.values();
		for(NetNode node : nodes)
		{
			this.convertCPTsToXml(cpts, node);
			this.convertNodesToXml(genie, node);
		}

		try
		{
			FileWriter out = new FileWriter(outFile);//"NewBayesNetStructureTest" + ".xdsl");
			OutputFormat outformat = OutputFormat.createPrettyPrint();
			XMLWriter writer = new XMLWriter(out, outformat);
			writer.write(doc);
			writer.flush();
			writer.close();
		}catch(Exception e)
		{
			logger.info("exportToGenie - Error exporting genie xdsl file: " + e.getMessage());
		}

	}

	/**
	 * @param genie
	 */
	private void convertNodesToXml(Element genie, NetNode node)
	{
		Element gnode = genie.addElement("node").addAttribute("id", node.getGenieID());
		gnode.addElement("name").addText(node.getName() + node.getDescription());
		gnode.addElement("interior").addAttribute("color", "e5f6f7");
		gnode.addElement("outline").addAttribute("color", "000080");
		gnode.addElement("font").addAttribute("color", "000000").addAttribute("name", "Arial").addAttribute("size", "8");
		gnode.addElement("position").addText("400 200 550 300");
	}

	/**
	 * Converst our cpt's to an XML format that can be read by GENIE
	 *
	 * @author Craig McNamara
	 */
	private void convertCPTsToXml(Element cpts, NetNode node)
	{
		Element cpt = cpts.addElement("cpt").addAttribute("id", node.getGenieID());
		cpt.addElement("state").addAttribute("id", "True");
		cpt.addElement("state").addAttribute("id", "False");
		String strParents = new String();
		ArrayList<NetNode> nodes = new ArrayList<NetNode>();
		nodes.addAll(node.causes);
		nodes.addAll(node.inhibitors);
		if(nodes.size() > 0)
		{
			for(int j = nodes.size() - 1; j >= 0; j--)
			{
				//convertCPTsToXml(cpts, node.causes[j]);
				strParents += nodes.get(j).getGenieID() + " ";
			}
			//Sets Parents in appropriate element
			Element parents = cpt.addElement("parents");
			parents.addText(strParents.trim());
			//System.out.println("Bling :" + node.causes.length);
		}
		//Fill this in with Probs
		Element probs = cpt.addElement("probabilities");
		//Insert probabilities in probs tag
		String sCpt = new String();
		Vector<Float> genieCPT = combineCauseAndInhibitForGenieCPT(node);
		if(genieCPT.size() != 0)
			for(int k = genieCPT.size() - 1; k >= 0; k--)
			{
				if(genieCPT.elementAt(k) == null)
				{
					sCpt += 0.5 + " ";
					continue;
				}
				sCpt = sCpt + ((Float) genieCPT.elementAt(k)) + " ";

			}
		else
			sCpt = ".9 .1";
		probs.addText(sCpt);
	}
	
	 /**
	 * Combine a NetNodes CPTs for use in GENIE
	 *
	 */
	protected Vector<Float> combineCauseAndInhibitForGenieCPT(NetNode node)
	{
		//If we have no inhibitors just return the causal CPT and dont waste time.
		//The Causal CPT even with no inputs will always be 1,0 -CM 6/13/06
		if(node.inhibitingCPT.size() == 0)
			return node.causalCPT;

		Vector<Float> genieCPT = new Vector<Float>();
		int genieSize = (1 << (node.causes.size() + node.inhibitors.size() + 1)); // plus 1 to make room for the event itself
		genieCPT.setSize(genieSize);

		//System.out.println("Genie CPT:");
		for(int k = 0; k < node.inhibitingCPT.size(); k += 2)
		{
			for(int i = 1; i < node.causalCPT.size(); i += 2)
			{
				// probabilty that event is caused (occurs) multiplied by the
				// probability that it is NOT inhibited
				// notice that the index, k, ends in zero, and the index, i,
				// ends in one
				float pOccurs = ((Float) node.causalCPT.elementAt(i)).floatValue() * ((Float) node.inhibitingCPT.elementAt(k)).floatValue();
				int genieIndex = (k << node.causes.size()) + i; // puts the
				// inhibiting bits in high order; relys on low order bit of k to
				// be zero so that there
				// will be room for i
				genieCPT.setElementAt(new Float(pOccurs), genieIndex);
				genieCPT.setElementAt(new Float((float) 1.0 - pOccurs), genieIndex - 1);
			}
		}
		return genieCPT;
	}

}
