package mil.af.rl.jcat.bayesnet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.log4j.Logger;

import mil.af.rl.jcat.bayesnet.LikelihoodSampler;;

public class ResourcedLHSampler extends LikelihoodSampler {
	static final long serialVersionUID = 1;
	protected int[] ensembleCount = null;
	protected int ensembleSampleSize = 0;
	private TreeMap<NetNode, List> ensembleCounts = null;  //stored accumulated counts (to show the user)
	private TreeMap<NetNode, float[]> resourceCounts = null; //for showing the user resouce status at each time
	private int countsTaken;
	private static Logger logger = Logger.getLogger(ResourcedLHSampler.class);


	ResourcedLHSampler(ArrayList<NetNode> topoSortedNodes){
		super(topoSortedNodes);
	}
	public void initializeSamplerData()
	{
		this.sampleCount = 0;
		this.nodeData = new ResourcedLHNodeData [sortedNodes.length];
		ensembleCounts = new TreeMap<NetNode, List>();
		resourceCounts = new TreeMap<NetNode, float[]>();

		for(int i = 0; i < sortedNodes.length; i ++){
			ResourcedLHNodeData data = new ResourcedLHNodeData();
			data.nodeName = sortedNodes[i].toString();
			data.accumulatedLikelihood = new double[timespan];
			data.occurrenceCount = new int[timespan];
			data.state = new int[timespan];
			data.inferredState = new int[timespan];
			data.resourceTrack = new int[timespan];
			nodeData[i] = data;
			dataIndex.put(sortedNodes[i], data);
			
			if(sortedNodes[i].getType() == NodeType.Adder) //don't include adders in resource counts stuff
				continue;
			if(sortedNodes[i].resources != null){
				resourceCounts.put(sortedNodes[i], new float[timespan]);
			}else{// this is some experimental stuff: trying to clarify the similarities and differences between resourc counts and event counts
				resourceCounts.put(sortedNodes[i], new float[timespan]);
			}
		}
	}

	private int computeEnsembleSize(){
		/* This is going to compute the max # of threat (tokens?) which will be generated any one type of threat resource
		 * This in turn assumes we are going to allow any one ensemble member (token) to contain more than one threat as
		 * long as they are different types
		 */
		int maxThreats = 0;
		for( NetNode n : this.sortedNodes){
			int total = 0;
			if(n.resources != null && n.resources.isThreat()){
				ThreatResource tr = (ThreatResource)(n.resources);
				for(int count : tr.getInitialInventory().values()){
					total += count;
				}
			}
			if(total > maxThreats){
				maxThreats = total;
			}
		}
		return maxThreats;
	}

	public void run()
	{
		int tokenCount = computeEnsembleSize();
		countsTaken = 0;
		ensembleSampleSize = 0;
		while(keepSampling == true)
		{
			stats.resetLikelihood();
			for(int time  = 0; time < this.timespan; time++){
				trackResourceAvailability(time);
				for(int token = 0;  token < tokenCount; token++){// for each token (threat or threat combination)
					for(int i = 0; i < sortedNodes.length; i++){// switch to the next token, i.e. the next threat (or threat combination)
						nodeData[i].state = ((ResourcedLHNodeData)nodeData[i]).getSample(token, timespan);
					}
					for(int i = 0; i < sortedNodes.length; i++){// process the current token through the causal structure at this time
						this.takeSliceSample(sortedNodes[i], nodeData[i], time, this.timespan);
					}
				}
			}
			sampleCount += tokenCount;
			ensembleSampleSize += 1;
//			logger.debug("Ensemble Size = " + tokenCount);
			for(int k = 0; k < sortedNodes.length; k++)
			{
				NetNode node = this.sortedNodes[k];
				ResourcedLHNodeData data = ((ResourcedLHNodeData)this.dataIndex.get(node));
				this.ensembleCount = new int[timespan];// assuming these are zeroed when allocated
				this.accumulateSliceResults(data);
//				this.printEnsembleTotals(node, ensembleCount);
				storeEnsembleTotals(node, ensembleCount);

				if(node.resources != null){
//					System.out.print(node.toString() + " ");
//					for(int av : data.resourceTrack){
//						System.out.print(av + " ");
//					}
//					System.out.println();
					float[] currentAverages = this.resourceCounts.get(node);
					for(int j = 0; j < currentAverages.length; j++){
						currentAverages[j] = (((ensembleSampleSize - 1) * currentAverages[j]) + data.resourceTrack[j]) / ensembleSampleSize;
						data.resourceTrack[j] = 0;
					}
					node.resources.ResetResource();
				}else{// this is some experimental stuff: trying to understand the similarities and differences between resource counts and event counts
						// if this goes, the corresponding stuff in 'initializeSamplerData' should also go.
					float[] currentCounts = this.resourceCounts.get(node);
					List eventCounts = this.ensembleCounts.get(node);
					if(eventCounts != null && currentCounts != null){
						try{
							for(int m = 0; m < currentCounts.length; m++){
								// why aren't these the same?currentCounts[m] = (Float)eventCounts.get(m + 1 ); //+ 1 cause Mikey has the node in '0'
								currentCounts[m] = ((float)data.occurrenceCount[m] )/ ensembleSampleSize;
							}
						}
						catch(Exception e){
							logger.warn("run - exception occured:", e);
						}
					}
				}
				data.ensemble.clear();
				data.zeroState();
			}
            
			stats.updateModelStatistics();
			while(this.isPaused)
			{
				try
				{
					wait();
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	private void trackResourceAvailability(int time) {
		for(int j = 0; j < sortedNodes.length; j++){
			NetNode nn = sortedNodes[j];
			if(nn.resources != null){
				ResourcedLHNodeData nd = (ResourcedLHNodeData)nodeData[j];
				nd.resourceTrack[time] += nn.resources.getOnHand(time);
			}
		}
	}
	
	private void storeEnsembleTotals(NetNode node, int[] ensembleCount)
	{
		countsTaken++;
		//only store an entry for each netnode one time for right now
//		if(ensembleCounts.size() <= sortedNodes.length)
//		{
			Vector ensembleVect = new Vector();
			// putting the node in the Vector is done so that this data can easily be used in a table
			ensembleVect.add(node);

			for(int s = 0; s < timespan; s++)
			{
				ensembleVect.add(new Float(ensembleCount[s]));
			}

			if(ensembleCounts.containsKey(node))
				averageCounts(ensembleVect, (Vector)ensembleCounts.get(node));

			ensembleCounts.put(node, ensembleVect);
//		}
	}

	/**
	 * Average the float data inside d2 into d1
	 * @param d1
	 * @param d2
	 * @return d1 with the data from d2 averaged in
	 */
	private Vector averageCounts(Vector<Float> d1, Vector<Float> d2)
	{
		//skip the first cause its the node name, yeh I know weird but it can be changed later
		for(int x=1; x<d1.size(); x++)
		{
			//average the 2 numberz and store it back in d1
			if(x < d2.size())
			{
				float oldAvg = d1.get(x).floatValue() * (countsTaken - 1);
				float newAvg = (oldAvg + d2.get(x).floatValue()) / (float)countsTaken;
				d1.set(x, new Float(newAvg));
			}
		}

		return d1;
	}

	public TreeMap<NetNode, float[]> getLeakerCounts()
	{
		return resourceCounts;
	}

	public TreeMap<NetNode, List> getEnsembleCounts()
	{
		return ensembleCounts;
	}

	private void printEnsembleTotals(NetNode node, int[] ensembleCount2) {
		if(true/*node.type == NodeType.Process*/){
			for(int s = 0; s < timespan; s++){
				System.out.print(ensembleCount[s] + "\t");
			}
			System.out.print(node.toString());
			System.out.println();
		}
	}

	public void accumulateSliceResults(ResourcedLHNodeData data){
		for(int e = 0; e < data.ensemble.size(); e++){
			data.state = data.ensemble.get(e);
			// for prediction we always accumulate '1'

			for(int j = 0; j < this.timespan; j++){
				if(data.state[j] > 0){
					data.occurrenceCount[j] += 1; // accumulate over all samples
					this.ensembleCount[j] += 1; // accumulate over just the last ensemble of samples
				}
				if(data.inferredState[j] > 0){
					data.accumulatedLikelihood[j] += stats.predLikelihood;
				}else{
					data.accumulatedLikelihood[j] += 0.0;// for debuggins
				}
			}
		}

	}


	synchronized public void takeSliceSample(NetNode node, LikelihoodNodeData nodeData, int slice, int sliceLimit)
	{
		// here is new concept (Oct 29, 2006) that can be justified on causal grounds: if the node has already happend (this must have happened because of persistence),
		// then don't sample again.  This way we avoid messing with resources
		// this method is now critically dependent on persistence being layed into the future when the event initially occurs.
		// this means you can't inhibit something that is persisting, but we havn't been doint that anyway (to date)
		if(nodeData.state[slice] != 0 ){// must have persisted from a previous time
			for(NetNode c: node.causes){
				if(c.resources != null){
					dataIndex.get(c).state[slice] = 0;// indicate the resources were not used
													//even though they might have been enabled.
													// Remember that to use resoureces, they
													// must intially be enabled, but, if they
													// are NOT used their state is set to 0
				}
			}
			return;
		}

		double occurrenceProb = 0.0;
		double inferredProb = 0.0;
		if (node.schedule != null && node.schedule[slice] > 0.0f)
		{ // if it is 0.0 then this not is not scheduled at this time slice
			// scheduling the node is like setting its prior.
			// the block deals only with scheduled nodes
			inferredProb = occurrenceProb = node.schedule[slice];
			if(node.resources != null){
				node.resources.allocateResource(1, slice, nodeData.state);// curently, this is where threat resources get allocated
			}
			// this is Pearle's notion of intervention rather than the
			// pure concept of simply scheduling

		} else
		{
			// this block deals with UNscheduled nodes, nodes that depend on
			// their causes and leak for their occurrence
			float currentLeak = node.leak == null ? (float) 0.0 : node.leak[slice];
			float [] causalProbs = getSampleEventProbabilityFactor(slice, node, node.causalCPT, node.causes);
			float [] inhibitingProbs = getSampleEventProbabilityFactor(slice, node, node.inhibitingCPT, node.inhibitors);

			// make computations for prediction
			float notInhibitingProb = ((float) 1.0) - inhibitingProbs[0];
				occurrenceProb = (1.0 - (1.0 - currentLeak) * (1.0 - causalProbs[0]))* notInhibitingProb;

			// make computations for inference
			float inferredNotInhibitingProb = ((float) 1.0)- inhibitingProbs[1];
			inferredProb = (1.0 - (1.0 - currentLeak) * (1.0 - causalProbs[1])) * inferredNotInhibitingProb;
		}

		// now we are set up to actually draw the sample
		int newEventSlice = slice + node.nodeEffectSliceDelay;
		/***********************************************************************
		 * Semantic Bug!!**************** this condition should not be tested if
		 * there is evidence!!!!!!!!!!********
		 **********************************************************************/
		if (newEventSlice < sliceLimit)// this introduces a bug in late stage
			// evidence!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		{
			double draw = random.nextDouble();
			int originalPriorState = nodeData.state[newEventSlice]; // for use below in
			// computation of
			// prior evidence
			// likelihood
			int extendedPersistence = extendMinimumPersistence(node.persistence, node.continuationProbability);
			if (occurrenceProb >= draw)
			{
				updateNodeState(nodeData.state, newEventSlice, extendedPersistence,
						sliceLimit);
				updateResources(node, NetNode.ResourceSet.Predicted, ResourceAction.EXPEND, slice, occurrenceProb);
			} else
			{
				// lets assume all the slice states are set to 0 before beginning sampling state[slice] = 0 or that they have some entry due to persistence: so do nothing here;
				// but we do need to release the resources allocated for this event
				updateResources(node, NetNode.ResourceSet.Predicted, ResourceAction.DEALLOCATE, slice, occurrenceProb);
			}

			// Draw sample for the inferred state
			double iDraw = random.nextDouble();
			Evidence theEvidence = null;
			if (node.evidence != null /* && inferredProb > 0.0 */)
			{
				theEvidence = searchForEvidenceDuringPersistence(node, newEventSlice, node.persistence);
			}
			if (theEvidence != null)
			{
				// there may be some evidence for this node during the extended
				// persistence starting at this time slice
				// therfore we are at an 'observable' event, an event for which
				// there may evidence
				if (originalPriorState == 0)
				{// if != 0 the event persited from a previous slice and I do
					// no think it should further effect the likelihood of this
					// sample
					stats.updatePriorLikelihood(occurrenceProb >= draw ? occurrenceProb
							: 1.0 - occurrenceProb);// needs to be thought more about!
					if (processEvidence(theEvidence, inferredProb, nodeData.accumulatedLikelihood[slice]))
					{
						updateNodeState(nodeData.inferredState, newEventSlice,
								extendedPersistence, sliceLimit);
						updateResources(node, NetNode.ResourceSet.Inferred, ResourceAction.EXPEND, slice, occurrenceProb);
					} else
					{
						nodeData.inferredState[slice] = 0;
						// it should have been initialized to zero, but we are
						// belt and suspender people
						updateResources(node, NetNode.ResourceSet.Inferred, ResourceAction.DEALLOCATE, slice, occurrenceProb);
					}
				}
			} else
				// there is no evidence at all for this node, so certainly none at
				// this slice!
				if (inferredProb > iDraw)
				{
					updateNodeState(nodeData.inferredState, newEventSlice, extendedPersistence, sliceLimit);
				} else
				{
					// the inferredState should be all set up either from
					// initialization or from persistence.
				}
		}
		if (nodeData.whyData != null)
		{
			nodeData.whyData.recordSample(node, this.dataIndex, slice);
		}
	}


    private int getResourcedCausalState(int slice, int[] sliceOccurrence, SimpleResource resources){
        int retVal = 0;
        try{
            retVal = sliceOccurrence[slice];
        }catch(Exception e){
        	logger.error("getResourcedCausalState - Error getting node state:  "+e.getMessage());
        }
        if(resources != null){// I don't see anymore why I don't want this to be a process?????
            if(!resources.allocateResource(1, slice, sliceOccurrence[slice] > 0)){
                retVal = 0;
                sliceOccurrence[slice] = 0;
            }
        }
        return retVal;
}

    public void updateResources(NetNode node, NetNode.ResourceSet setType, ResourceAction how, int slice, double eventProb){
    	// to enable contingent resources, signal resource are updated only when there effects updated
		SimpleResource res = node.getResources(setType);
		if(node.type == NodeType.Process){
			//TODO: this block may not belong here anymore because of changes to 'getProbability' or something like that
			if(res != null){
				LikelihoodNodeData data = dataIndex.get(node);
				allocDealloc(slice, data, res, how, eventProb);
			}
			updateModeResources(slice, node.causes, setType, how, eventProb);
			updateModeResources(slice, node.inhibitors, setType, how, eventProb);
    	}
    }

    private void updateModeResources(int slice, ArrayList<NetNode> modeMembers, NetNode.ResourceSet setType, ResourceAction how, double eventProb){
    	for(int j = 0; j < modeMembers.size(); j++){
    		if(modeMembers.get(j).type != NodeType.Process){
	    		LikelihoodNodeData data = this.dataIndex.get(modeMembers.get(j));
	    		SimpleResource res = modeMembers.get(j).getResources(setType);
	    		allocDealloc(slice, data, res, how, eventProb);
    		}
    	}
    }

    private void allocDealloc(int slice, LikelihoodNodeData data, SimpleResource res, ResourceAction how, double eventProb) {
		if(res != null){
			if(!res.isContingent && eventProb > 0.001){
				//if it is NOT contingent on success, expend it anyway
				// unless the whole event has not been triggered as indicated by the operant occurrence prob having been 'almost' zero
				how = ResourceAction.EXPEND;
			}
			switch(how){
			case EXPEND:
				if(res.expend()){
					data.state[slice] = 1;
				}
				else{
					data.state[slice] = 0;
				}
				break;
			case DEALLOCATE:
				res.deAllocate();
				/************
				 * setting state to zero cause of failure to use a resource is not a good idea (see previous comment)
				if( ! res.deAllocate()){
					data.state[slice] = 0;
				}else{// this is experimentatal to see if plots will appear to make more sense
					data.state[slice] = 0;
				}
				**********/
				break;
			}
		}
	}
	protected float [] getSampleEventProbabilityFactor(int slice, NetNode node, Vector CPT, ArrayList<NetNode> modeNodes)
    {
		/*
		 * What kind of node are we dealing with here:
		 * Consolidators seem like they behave like signal nodes (guaranteed to have a single cause)
		 * Adders seem like they behave like process nodes
		 * But I think neither consolidators nor adders should ever have resources associated with them (potentially multiple causes
		 */
        float [] eventProbs = {0.0f, 0.0f}; // {predicted_prob, inferred_prob}
        if(node.type == NodeType.Process || node.type == NodeType.Adder){
        	// both these types have edges visible in the GUI to which the modeler might add resources
	        if(node.resources != null && CPT == node.causalCPT && !node.resources.allocateResource(1, slice, true /*I'm thinking resources on Process are always enabled!?*/)){
	        		// this node requires a resource which is apparently exhausted
	        		// note that this 'if' statement implies that inhibition cannot depend on resources stored at the process node itself.
	        		//TODO:  Figure out what to do if this is called to get inhibiting probs: ?check to see CPT or modeNodes belogs to inhibiting, or what?
	        }else if (modeNodes != null && modeNodes.size() > 0){
	        	int cptIndex = 1; // we are interested in the probability of this event indexed by '1'
	        	int infCptIndex = 1; // ditto
	        	// event OCCURRING

	        	// Let's do some effect/resource (target/weapon) pairing  if appropriate
	        	boolean resourcesInvolved = false;
	        	for (int j = 0; j < modeNodes.size(); j++){
	        		NetNode currentModeNode = modeNodes.get(j);
	        		int retState = getResourcedCausalState(slice, dataIndex.get(currentModeNode).state, currentModeNode.resources);
	        		if (retState != 0){// we know (thanks to the topo sort driving this that the jth cause has
	        							// already been sampled; because of getCausalState we know it has logically occurred and has resources available to support occurrence on 'node'
	        			cptIndex |= (1 << (j + 1));// the "+ 1" is because the 0th position is for the event itself
	        			if(currentModeNode.resources != null){
	        				resourcesInvolved = true;
	        			}
	        		}
	        		if(getResourcedCausalState(slice, dataIndex.get(currentModeNode).inferredState, currentModeNode.inferredResources) != 0){
	        			//TODO make this work for inference to
	        			infCptIndex |= (1 << (j + 1)); // "+ 1" for j is because 0th position for the event itself
	        		}
	        	}

	        	//TODO:  Use check if NetNode.getResourceUseType() == NetNode.RESOURCE_USE_AND or NetNode.RESOURCE_USE_OR
	        	if(resourcesInvolved  || node.getResourceUseType() == NetNode.POLICY_TABLE/* && node.getResourceUseType() == NetNode.RESOURCE_USE_OR*/)
	        	{
	        		int oldIndex = cptIndex;
	        		switch (node.getResourceUseType()){
	        		case NetNode.USER_PRIORITY_USE:
        				cptIndex = this.selectResourceFromUserPriority(null, modeNodes, CPT, cptIndex);
	        			break;
	        		case NetNode.HIGHEST_PROB_USE:
	        		case NetNode.RESOURCE_USE_OR:// at least
	        			if(node.policy == null){
	        				cptIndex = selectHighestProbabilityResource(slice, modeNodes, CPT, cptIndex);
	        			}
	        			else{
	        				cptIndex = node.policy.orders(node, dataIndex, slice, oldIndex);
	        			}
	        			break;
	        		case NetNode.POLICY_TABLE:
	        			cptIndex = node.policy.orders(node, dataIndex, slice, oldIndex);
	        			break;
	        		case NetNode.RESOURCE_USE_AND:
	        		default:
	        			break;
	        		}
	        	}
	        	// now the cptIndex points to the CPT entry we want, since the bits
	        	// in it which are one tell us which of the causes have occurred
	        	eventProbs[0] = ((Float)(CPT.get(cptIndex))).floatValue();
	        	eventProbs[1] = ((Float)CPT.get(infCptIndex)).floatValue();
	        }
        }else{
        	// we are dealing with a Signal or a Consolidator
        	// a Signal corresponds to a single GUI edge, but a Consolidator has multiple visible edges, even though is "really" only a single edge
        	//TODO What do we do if a user adds resources to the multiple "consolidator" edges, or, how can we prevent this from happening?
        	//TODO This should probably be refactored: the loop here is the same as the one above except it doesn't use 'getCausalState' which attempts to allocate resources on the modeNodes
            if(CPT == node.getCausalCPT()){ // we don't model inhibition on Signals (or Consolidators)
            	int cptIndex = 1;
	        	int infCptIndex = 1;
	        	for(int j = 0; j < modeNodes.size(); j++){
	        		NetNode currentModeNode = modeNodes.get(j);
	                LikelihoodNodeData modeNodeData = dataIndex.get(currentModeNode);
	                if(modeNodeData.state[slice] > 0){
	                	cptIndex |= (1 << (j + 1));
	                }
	                if(modeNodeData.inferredState[slice] > 0){
	                	infCptIndex |= (1 << (j+1));
	                }
	        	}
	        	eventProbs[0] = ((Float)(CPT.get(cptIndex))).floatValue();
	        	eventProbs[1] = ((Float)CPT.get(infCptIndex)).floatValue();
        	}
        }
        return eventProbs;
    }
	/**
	 * This kludge of static functions is so I can experiment with doing the highest prob stuff within the 'policy' architecture
	 * @param node
	 * @param slice
	 * @param originalCptIndex
	 * @return
	 */
	static public int selecteHPResourcedCause(NetNode node, int slice, int originalCptIndex){
		return selectHighestProbabilityResource(slice, node.causes, node.causalCPT, originalCptIndex);
	}
	static public int selecteHPResourcedInhibit(NetNode node, int slice, int originalCptIndex){
		return selectHighestProbabilityResource(slice, node.inhibitors, node.causalCPT, originalCptIndex);
	}
	/**
	 * Selects the resource that provides the highest probability of occurrence
	 * @param slice
	 * @param modeNodes
	 * @param CPT
	 * @param originalCptIndex
	 * @return the cpt index of the node with resources to make the event occur
	 */
	static private int selectHighestProbabilityResource(int slice, ArrayList<NetNode> modeNodes, Vector CPT, int originalCptIndex) {
		// select best resource;
		// currently we are making the assumption here that child of these causes is of type 'OR Resources'
		// hopefully this notion of resource effect pairing can be generalized someday
		// like we probably need to do AND resources right away

		// find the best single (the 'OR' assumption) resource (defined here as the one with highest probability of causing the effect
		int noResourcesIndex = originalCptIndex;
		for(int i = 1; i <= modeNodes.size(); i++){// remove All the resourced causes until we find the most effective single resource
			NetNode modeNode = modeNodes.get(i - 1);
			if(modeNode.resources != null){
				int shift = ~(1 << i);
				noResourcesIndex &= shift;
			}
		}
		float highestProb = 0.0f;
		int bestResourceModeNodeIndex = -1;
		for(int k = 1; k <= modeNodes.size(); k++){// look for the resourced cause most likely to succeed, given the context of the other non-resourced causes which have also happened
			int mask = (1 << k);
			int maskedResult = originalCptIndex & mask;
			if((maskedResult) != 0){
				NetNode modeNode = modeNodes.get(k - 1);
				if(modeNode.resources != null){
					float resourceProb = (Float)CPT.get(noResourcesIndex | (1 << k));
					if(resourceProb > highestProb){
						highestProb = resourceProb;
						bestResourceModeNodeIndex = k -1;
					}
				}
			}
		}
		int modifiedCptIndex = originalCptIndex;
		if(bestResourceModeNodeIndex != -1){
			modifiedCptIndex = noResourcesIndex | (1 << (bestResourceModeNodeIndex + 1));// put the best resource back in
			for(int j = 0; j < modeNodes.size(); j++){ // deallocate all the other resourced causes
				NetNode aCause = modeNodes.get(j);
				if(j != bestResourceModeNodeIndex && aCause.resources != null){// the deallocates causes which might not have been allocated anyway, but I think that is OK
					aCause.resources.deAllocate();
					/******
					 * Strictly experimental!!!!!
					 * The experiment seems to have failed! : In the new consolidators, the keeps nodes deeper in the topo sort from using the resources
		            dataIndex.get(aCause).state[slice] = 0; //this may be confusing the event "being available" with the event "resource used"
		            /******
		             * End of experiment; Note this depends critically on persistence having been layed out "into the future".
		             * If persistence were computed step by step, then this resource could never, ever be used again in the future.
		             *********/

				}
			}
		} 
		return modifiedCptIndex;
	}
	
	/**
	 * Selects the node with resources available bases on a user supplied priority Queue
	 * @param priorityQueue Ordered list of netNodes to consume resources from
	 * @param modeNodes NetNodes of a particular modeSet
	 * @param CPT Conditional Probability Table
	 * @param originalCptIndex causal CPT index
	 * @return CPT index of the mode node to use
	 */
	private int selectResourceFromUserPriority(ArrayList<NetNode> priorityQueue, ArrayList<NetNode> modeNodes, Vector CPT, int originalCptIndex)
	{
		int modifiedIndex = 1;
		int priorityIndex = 0;
		priorityQueue = (ArrayList<NetNode>) modeNodes.clone();
		priorityQueue.remove(0);
		priorityQueue.remove(1);
		//Select the node that is to provide the resources
		NetNode candidatePriorityNode = null;
		NetNode priorityNode = null;
		Iterator<NetNode> i = priorityQueue.iterator();
		while(i.hasNext())
		{
			candidatePriorityNode = i.next();
			if(candidatePriorityNode.resources != null && candidatePriorityNode.resources.currentAllocation != SimpleResource.COULD_NOT_ALLOCATE)
			{				
				priorityNode = candidatePriorityNode;
				break;
			}
		}
		if(priorityNode != null){
			//Compute the CPT index
			for(int k = 1; k <= modeNodes.size(); k++)				
			{
				NetNode modeNode = modeNodes.get(k-1);
				if(priorityNode.compareTo(modeNode) == 0)
				{
					priorityIndex = (1 << k);
					break;
				}
			}
			
			//Combine index of the event itself happening and the index of the highest priority modenode.
			modifiedIndex = modifiedIndex | priorityIndex;
			int deAllocateIndex = (originalCptIndex ^modifiedIndex);				
			for(int j = modeNodes.size(); j > 0; j--)
			{		
				int highestIndex = (1<< j);
				if((deAllocateIndex ^ highestIndex) < deAllocateIndex && deAllocateIndex != 0)
				{
					NetNode modeNode = modeNodes.get(j-1);
					if(modeNode.resources != null)
						modeNode.resources.deAllocate();
				}				
				deAllocateIndex = (deAllocateIndex ^ highestIndex);
			}		
			//Deallocate all resources from nodes that aren't chosen
			return modifiedIndex;
		}else{
			//TODO: Is this what we want??
			return originalCptIndex;
		}
	}
}
