package mil.af.rl.jcat.MMC;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Random;

public class Proof {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Proof theProof = new Proof();
		/**************
		for(int e = 0; e < 4; e++){
			for(int c = 0; c < 3; c++){
				theProof.buildModel(e, c);
				//theProof.startProofAlgorithm(100000);
				theProof.startCombinedAlgorithm(1000000);
			}
		}
		***********/
		theProof.buildModel(0, 1);
		theProof.startCombinedAlgorithm(100000);
		theProof.buildModel(1, 1);
		theProof.startCombinedAlgorithm(100000);
		theProof.buildModel(2, 1);
		theProof.startCombinedAlgorithm(100000);
		
		theProof.buildModel(3, 3);
		theProof.startCombinedAlgorithm(100000);
		theProof.buildModel(4, 3);
		theProof.startCombinedAlgorithm(100000);
		theProof.buildModel(5, 3);
		theProof.startCombinedAlgorithm(100000);
	}

	DecimalFormat probFormat = new DecimalFormat();

	private void buildModel(int EPTindex, int CPTindex) {
		probFormat.setMaximumFractionDigits(3);
		ArrayList<Node> emptyList = new ArrayList<Node>();
		nodes.retainAll(emptyList);

		// add the nodes in topo order
/*****
		// this will be the model in the paper
		nodes.add(new Node("a", 0));
		nodes.add(new Node("b", 1));
		nodes.add(new Node("x", 2));
		nodes.add(new Node("y", 3));

		mechanisms.add(new Mechanism(nodes.get(0), nodes.get(2), 0.1)); // a causes x: .1
		mechanisms.add(new Mechanism(nodes.get(0), nodes.get(3), 0.3)); // a causes y: .3
		mechanisms.add(new Mechanism(nodes.get(1), nodes.get(2), 0.2)); // b causes x: .2
		mechanisms.add(new Mechanism(nodes.get(1), nodes.get(3), 0.4)); // b causes y: .4
		mechanisms.add(new Mechanism(nodes.get(3), nodes.get(4), 0.7)); // y causes z: .7
******/
		// this is Ed's model with the probabilities scaled up
		nodes.add(new Node("a", 0));
		nodes.add(new Node("b", 1));
		nodes.add(new Node("x", 2));
		nodes.add(new Node("y", 3));
		//nodes.add(new Node("z", 4));
		// My revised numbers from Eddies TIM, revised so probabilities seem more normal
		mechanisms.add(new Mechanism(nodes.get(0), nodes.get(2), 0.4)); // a causes x: .4
		mechanisms.add(new Mechanism(nodes.get(0), nodes.get(3), 0.5)); // a causes y: .5
		mechanisms.add(new Mechanism(nodes.get(1), nodes.get(3), .5));
		double EPT[][] ={
					{.3, .5, .8, 1.0},/*indep*/ 
					{.5, .5, .6, 1.0},/*max cor*/ 
					{.1, .5, 1.0, 1.0},/* min cor/disjoint*/
					{.29, .29, .6, 1.0},/*split uncertainty*/
					{.173,.289,.716,1.00},/*split uncertaity but overall still .5*/
					{0, .289, .889, 1.0},// split uncertainty minimally correlated					
					{.289, .289, .6, 1.0}//split uncertainty maximally correlated					
					};
		double CPT[][] = {{0, .5, .5, .75}, // all uncertainty about 'a' and 'b' causing y
						{0, 1, .5, 1}, // a's uncertainty is its EPT; b's uncertainty is here
					
						{0, 1, .5, .8}, // 'b' inhibts 'y' and (only)its uncertainty is here
						{0,.71,.5,.755},// split uncertainty, still .5, causal indep given a (doesn't make sense in physical model
						};
		//double[] EPTa ={.3, .5, .8, 1.0}; //{.3, .2, .3, .2}j;  {.3, .4, .5, .2}m //indep
		//double[] EPTa = {.5, .5, .6, 1.0}; //{.5, 0 , .1, .4}j; {1, .4, .5, .4}m// maximally correlated
		//double[] EPTa = {.1, .5, 1.0, 1.0}; //{.1, .4, .5, 0}//disjoint
		//double[] EPTa ={.29, .29, .6, 1.0}; //{.29, 0, .31, .4}; //for splitting the uncertainty of a causing y
		nodes.get(0).setEPT(EPT[EPTindex]);
		

		//double[] EPTb = {0.5, 1.0};
		//nodes.get(1).setEPT(EPTb);
		//System.out.println("EPT(b) = " + printdoubleArray(EPTb, probFormat));
		
		//double[] CPTx = {0, .4};
		//nodes.get(2).setCPT(CPTx);
		//System.out.println("CPT(x) = " + printdoubleArray(CPTx, probFormat));

		//double[] CPTy = {0, .5, .5, .75}; // all uncertainty about 'a' and 'b'
		//double[] CPTy = {0, 1, .5, 1}; // a's uncertainty is its EPT; b's uncertainty is here
		//double[] CPTy = {0, 1, .5, .8}; // 'b' inhibts 'y' and its uncertainty is here
		//double[] CPTy = {0, .71, .5, .865}; //CPT is noisyOR while p(a->y) is split
		nodes.get(3).setCPT(CPT[CPTindex]);

		//		mechanisms.add(new Mechanism(nodes.get(2), nodes.get(4), 0.7)); // x causes z: .7
		//double[] EPTx = {.3, .7};
		//nodes.get(2).setEPT(EPTx);

/****** Eddie's example from the TIM.
		mechanisms.add(new Mechanism(nodes.get(0), nodes.get(2), 0.04)); // a causes x: .04
		mechanisms.add(new Mechanism(nodes.get(0), nodes.get(3), 0.05)); // a causes y: .05
		// mechanisms.double[] EPTa = {.95, 0.0, 0.01, 0.04}; // maximally correlated
		double[] EPTa = {.912, 0.038, 0.048, 0.002}; // indep
		nodes.get(0).setEPT(EPTa);
		mechanisms.add(new Mechanism(nodes.get(1), nodes.get(3), 0.04)); // b causes y: .04
		double[] EPTb = {.96, .04};
		nodes.get(1).setEPT(EPTb);
		mechanisms.add(new Mechanism(nodes.get(2), nodes.get(4), 0.7)); // x causes z: .7
		double[] EPTx = {.3, .7};
		nodes.get(2).setEPT(EPTx);
******/
		System.out.println();
		System.out.println("EPT(a) = " + printCumulative(nodes.get(0).ept, probFormat) + printJoint(nodes.get(0).ept, probFormat) + printMarginal(nodes.get(0).ept, probFormat));
		System.out.println("CPT(y) = " + printCumulative(nodes.get(3).cpt, probFormat));
	}

	private void startProofAlgorithm(long sampleSize) {
		proofAlgorithm(nodes, mechanisms, sampleSize);
	}
	
private void startEPTAlgorithm(int sampleSize) {
		analyzeSamples(EPTAlgorithm(nodes, mechanisms, sampleSize));
		
	}

private void startCombinedAlgorithm(int sampleSize){
	estimateDistributions(combinedSampling(nodes, mechanisms, sampleSize), 8);
}


	Random random = new Random();

	ArrayList<Sample> initializeSampleSet(int omegaEventCount, int eventCount, long sampleSize){
		ArrayList<Sample> set = new ArrayList<Sample>(eventCount);
		double[] omega = {0.0, 0, 0, 1.0};// EPT is in cumulative form; in the main sampler it is in density form
		for(int sampleCount = 0; sampleCount < sampleSize; sampleCount++){
			double p = random.nextDouble();
			int omegaSize = (1 << omegaEventCount);
			int j = 0;
			for(; j < omegaSize; j++){
				if(p <= omega[j])break;
			}
			Sample sample = new Sample(eventCount);
			for(int k = 0; k < omegaEventCount; k++){
				if((j&(1<<k))!= 0){
					sample.event[k] = true; // this is NoisyOR 'cause it might have been true all ready
				}
			}
			set.add(sample);
		}
		return set;
	}
	ArrayList<Mechanism> mechanisms = new ArrayList<Mechanism>();
	ArrayList<Node> nodes = new ArrayList<Node>();
	Proof(){
	}
	public class Sample{
		boolean[] event = null;
		Sample(int n){event = new boolean[(int) n];}
		long jointEventIndex(){
			long index=0;
			long bit = 1;
			for(boolean e: event){
				if(e){
					index |= bit;
				}
				bit = (bit <<1);
			}
			return index;
		}
		void modifySampleToAccountForExpandedCPT(Mechanism mechanism, double rand){
			if(event[mechanism.cause.index]&&rand<=mechanism.p){
				event[mechanism.effect.index]=true;
			}
		}
		void modifySampleToAccountForExpandedEPT(Mechanism mechanism, double rand){
			if(event[mechanism.cause.index]&&rand<=mechanism.p){
				event[mechanism.effect.index]=true;
			}
		}
		boolean b(long j, long i){
			return ((1<<i)&j)!=0;
		}
		public String  toString(){
			String rtn = "";
			for(boolean truth: event){
				if(truth){
					rtn += "1 ";
				}
				else{
					rtn += "0 ";
				}
			}
			return rtn;
		}
	}
	public class Node{
		int index = 0;
		String name = "";
		double [] cpt = null;
		double [] ept = null;
		ArrayList<Node> effect = new ArrayList<Node>();
		ArrayList<Node> cause = new ArrayList<Node>();
		ArrayList<Mechanism> input = new ArrayList<Mechanism>();
		ArrayList<Mechanism> output = new ArrayList<Mechanism>();
		Node(String name, int index){
			this.name = name;
			this.index = index;
			printCPT();
			printEPT();
		}
		void printCPT() {/*
			System.out.print("CPT: ");
			if(cpt!= null)printTable(cpt);
		*/}
		void printEPT(){/*
			System.out.print("EPT: ");
			if(ept != null)printTable(ept);
		*/}
		void printTable(double[] table){
			System.out.print(name);
			DecimalFormat ff = new DecimalFormat("#.###");
			for(double prob: table){
				System.out.print("\t\t" + ff.format(prob) + " ");
			}
			System.out.println();
		}
		public void augmentCPT(double p, Node cause){
			double oldCPT[] = cpt;
			int oldCPTlength = oldCPT.length;
			cpt = new double[oldCPTlength * 2];
			for(int k = 0; k < oldCPTlength; k++){
				cpt[k] = oldCPT[k];
				cpt[k + oldCPTlength] = (1.0 - (1.0 - oldCPT[k]) * (1.0 - p));// if the cause is active, an there an additional p chance of causation noisyORed with previous independent chances
			}
			printCPT();
		}
		public void augmentEPT(double p, Node effect){
			this.effect.add(effect);
			double[] oldEPT = ept;
			int oldEPTlength = oldEPT.length;
			ept = new double[oldEPTlength * 2];
			for(int k = 0; k < oldEPTlength; k++){// if the newly  added cause does not occur the existing sample remains valid  because no sampling of the new EPT would have taken planc
				ept[k] = oldEPT[k] * (1.0 - p);
				ept[k + oldEPTlength] = oldEPT[k] * p;
			}
			//If the newly added cause is active, there is now an additional p probality that the effect will be caused and NoisyORed with the previous sampling result
			printEPT();
		}
		public void changeEdgeProbInEPT(int edgeBitPos, double newProb){
			// get the current probability
			double currentProb = 0;
			for(int j = 0; j<ept.length; j++){
				if((j & (1<<edgeBitPos)) != 0){
					currentProb += ept[j];
				}
			}
			// adjust to newProb
			for(int k = 0; k<ept.length; k++){
				double delta = 0;
				if((k & (1<<edgeBitPos)) != 0){
					if(currentProb != 0){
						delta = ept[k]*newProb/currentProb -ept[k];
					}else{
						delta = newProb;
					}
					ept[k] += delta;
					ept[k^(1<<edgeBitPos)] -= delta;
				}
			}			
		}
		public boolean makeEdgeUncertainInEpt(int edgeBitPos, double newProb){
			// This assumes (but checks) the edge is currently certain , i.e. the edge probability is 1.0, but changes it to new prob
			double oldProb = 0;
			for(int j = 0; j < ept.length; j++){
				if((j & (1<<edgeBitPos)) != 0){
					oldProb += ept[j];
					ept[j] *= newProb;
					ept[j^(1<<edgeBitPos)] += oldProb *(1 - newProb);
				}
			}
			return oldProb >= .999;
		}
		public void makeEdgeUncertain(int edgeBitPos,double newProb){// version for paper; assumes old edge prob was 1.0
			for(int j = 0; j < ept.length; j++){// j indexes all subsets A; 
				if((j & (1<<edgeBitPos)) != 0){// makes sure the subset corresponding to j contains the effect we want to make uncertain
					ept[j^(1<<edgeBitPos)] += ept[j] * (1 - newProb);
					ept[j] *= newProb;
					
				}
			}
		}
		public void makeEdgeUncertainInCPT(int edgeBitPos, double newProb){
			for(int j = 0; j < cpt.length; j++){
				if((j & (1<<edgeBitPos)) != 0){// makes sure the subset corresponding to j contains the effect we want to make uncertain
					cpt[j] = 1 - ((1.0 - cpt[j^(1<<edgeBitPos)]) * (1- newProb));
				}				
			}
		}
		public void setEPT(double [] theEPT){
			ept = theEPT;
		}
		public void setCPT(double[] theCPT){
			cpt = theCPT;
		}
	}
	public class Mechanism{
		Node cause;
		Node effect;
		boolean active;
		double p;
		Mechanism(Node a, Node x, double p){
			this.cause=a; 
			this.effect = x; 
			this.p = p;
			this.active = false;
			cause.effect.add(effect);
			effect.cause.add(cause);
			cause.output.add(this);
			effect.input.add(this);
			};
		public void addMechanismToCPT_EPT(Mechanism m){
			cause.augmentEPT(p, effect); // one iteration of step 2, EPT modification
			effect.augmentCPT(p, cause); // one iteration of step 3, CPT modification
		}
	}


	/**
	 * @param nodeEvents
	 * @param topoSortedEdgeMechanism
	 * @param sampleSize
	 */
	public void proofAlgorithm(ArrayList<Node> nodeEvents, ArrayList<Mechanism> topoSortedEdgeMechanism, long sampleSize){
		// two sample sets are initialzed indentically with all events in all samples set to false
		ArrayList<Sample> CPTsamples = initializeSampleSet(2, nodeEvents.size(), sampleSize); // includes setting the state of all uncaused events
		ArrayList<Sample> EPTsamples = initializeSampleSet(2, nodeEvents.size(), sampleSize); // ditto
		for(Mechanism mechanism: topoSortedEdgeMechanism){
			// the sample sets are equal at this point
			mechanism.addMechanismToCPT_EPT(mechanism); // one iteration of the while loop in "Constructing Equivalent CPT and EPT Models"; the CPT's and EPT's end up being constructed as in the paper
			for(Sample sample: CPTsamples){sample.modifySampleToAccountForExpandedCPT(mechanism, random.nextDouble());}
			for(Sample sample: EPTsamples){sample.modifySampleToAccountForExpandedEPT(mechanism, random.nextDouble());}
		}
/*At this point, the CPTs and EPTs have all been built according to the edge probability construction.
 * AND samples have been taken which reflect the full CPTs and EPTs
 * BUT the samples have been taken using "virtual sampling"
 * SO we analyze the samples to empirically verify that virtual sampling is equivalent (under current assumptions) to
 * actual sampling.
 */		//double EPTprob = analyzeSamples(EPTsamples);
		//double CPTprob = analyzeSamples(CPTsamples);
		double regEPTProb = analyzeSamples(EPTAlgorithm(nodeEvents, topoSortedEdgeMechanism, sampleSize));//EPTs and CPTs were built earlier in this method
	}

	public ArrayList<Sample> EPTAlgorithm(ArrayList<Node> nodeEvents, ArrayList<Mechanism> topoSortedEdgeMechanism, long sampleSize){
		ArrayList<Sample> EPTSamples = initializeSampleSet(2, nodeEvents.size(), sampleSize);
		for(Sample sample: EPTSamples){
			for(Node event: nodeEvents){
				if(sample.event[event.index]){// if the nodeEvent didn't happen in this sample, it isn't going to cause anything
					double p = random.nextDouble();
					double cumulativeEPT = event.ept[0];// this EPT is stored in density form, unlike the omega sampler which is in cumulative form
					int j = 0;
					for(; cumulativeEPT < p && j < event.ept.length; j++){
						cumulativeEPT += event.ept[j+1];
					}
					if(j < event.ept.length){// to make sure the the last loop did not terminate because j got too big
						for(int i = 0; i < event.effect.size(); i++){
							if(sample.b(j, i)){// it is one of the things that is going to be caused
								sample.event[event.effect.get(i).index] = true;//noisyOR: doesn't make any difference if it was already true
							}
						}
					}
				}
			}
		}
		return EPTSamples;
	}
	
	ArrayList<Sample> combinedSampling(ArrayList <Node> nodeEvents, ArrayList<Mechanism>topoSortedEdgeMechanism, long sampleSize){
		ArrayList<Sample> combinedSamples = initializeSampleSet(2, nodeEvents.size(), sampleSize);
		for(Sample s: combinedSamples){
			for(Mechanism m: topoSortedEdgeMechanism){
				m.active = false;
			}
			for(Node n: nodeEvents){
				int cptIndex = 0;
				int bit = 1;
				for(Mechanism m: n.input){
					if(m.active){
						cptIndex |= bit;
					}
					bit = (bit << 1);
				}
				if(n.cpt != null){
					if(random.nextDouble() <= n.cpt[cptIndex]){
						s.event[n.index]= true;
					}else{
						s.event[n.index]= false;// this should be defaulted from initialization
					}
				}else if(cptIndex > 0){// at least one of the causes happened, ...
					s.event[n.index] = true; // and absence of a CPT declares node to be "Noisy OR"
				}
				if(s.event[n.index]){
					// the EPT part: only if the current event has happened so lets sample its EPT
					int j = 0;// need to preserve what 'j' is when the loop terminates
					if(n.ept != null){
						double p = random.nextDouble();
						for(; j < n.ept.length; j++){
							if(p <= n.ept[j])break; // assumes cumulative form of EPT
						}
						if(n.ept.length <= j)
						{//WTF?? throw some sort of exception or log it, or warn the user or do something!
						}
					}else{
						j = Integer.MAX_VALUE; // no EPT so all the edges are activated
					}
					for(int k = 0; k < n.output.size(); k++){
						if((j&(1<<k)) != 0){
							n.output.get(k).active = true;
						}
					}
				}// end of ept processing
			}// end of node loop
		}// end of sample loop
	return combinedSamples;
	}


	private double analyzeSamples(ArrayList<Sample> Samples) {
		boolean keepgoing = true;
		long evidence = 0; //y occurs
		long target = 4;//x'
		double posteriorProb = 0.0;
		while(keepgoing){
			long count = 0;
			long evidenceCount = 0;
			//long evidence = 9; // a & y occurs
			for(Sample S: Samples){
				if(sampleMatchesEventMask(S, evidence)){// rejection sampling
					evidenceCount +=1;
					if(sampleMatchesEventMask(S, target)){
						count += 1;
					}else{
						int sam = 0;
						sam = sam +1;
					}
				}
			}
			keepgoing = false;
			posteriorProb = (float)count / evidenceCount;
		}
		return (0.0);
	}
	
	private boolean estimateDistributions(ArrayList<Sample> samples, long evidenceEventIndex){
		int node_eventCount = samples.get(0).event.length;
		long[] mpd = new long[(1<<node_eventCount)];
		long[] jpd = new long[(1<<node_eventCount)];
		long[] mpdPrime = new long[(1<<node_eventCount)];
		long[] jpdPrime = new long[(1<<node_eventCount)];
		long sampleCount = 0;
		long evidenceCount = 0;
		for(Sample s: samples){
			sampleCount += 1;
			long index = s.jointEventIndex();
			jpd[(int)index] += 1;
			for(long j = 0; j <= index; j++)if((index&j)==j) mpd[(int)j] += 1;
			if((evidenceEventIndex&index)== evidenceEventIndex){
				evidenceCount += 1;
				jpdPrime[(int)index] +=1;
				for(long j = 0; j <= index; j++)if((index&j)==j) mpdPrime[(int)j] += 1;
			}			
		}
		
		System.out.println();
		System.out.println("evidenceEventIndex = 0x" + Long.toHexString(evidenceEventIndex>>2));
		System.out.println("index"+ '\t'+ "mpd" +  '\t' + "mpdPrime");
		for(int j = 0; j < mpd.length; j+=4){
			System.out.println(probFormat.format(j>>2) + '\t' + probFormat.format((double)mpd[j]/sampleCount) + '\t' + probFormat.format((double)mpdPrime[j]/evidenceCount));
		}
		System.out.println();
		System.out.println("Count = " + sampleCount + ", evidenceCount = " + evidenceCount);
		return true;
	}
	
	/**
	 * @deprecated Use {@link #printdoubleArrayCumulative(double[],NumberFormat)} instead
	 * @deprecated Use {@link #printCumulative(double[],NumberFormat)} instead
	 */
	

	/**
	 * @deprecated Use {@link #printCumulative(double[],NumberFormat)} instead
	 */
	
	private String printCumulative(double[] cumulative, NumberFormat probFormat){
		String retVal = "";
		retVal +=("{");
		if(cumulative != null){
			for(double v: cumulative){
				retVal += (probFormat.format(v) + " ");
			}
		}
		retVal += ("}");
		return retVal;
	}
	
	private String printMarginal(double [] cumulative, NumberFormat probFormat){
		String retVal = " {";
		double [] marg = new double[cumulative.length];// depending on this being zeroed
		for(int mIndex = 0; mIndex < marg.length; mIndex++){
			double subtotal = 0;
			for(int vIndex = 0; vIndex < cumulative.length; vIndex++){
				if((vIndex & mIndex) == mIndex){
					marg[mIndex] += cumulative[vIndex]- subtotal;
				}
				subtotal = cumulative[vIndex];
			}
			retVal += (probFormat.format(marg[mIndex]) + " ");
		}
		retVal += "}";
		return retVal;
	}
	
	private String printJoint(double[]cumulative, NumberFormat probFormat){
		String retVal = "";
		double partialSum = 0.0;
		retVal +=("{");
		if(cumulative != null){
			for(double v: cumulative){
				retVal += (probFormat.format(v - partialSum) + " ");
				partialSum = v;
			}
		}
		retVal += ("}");
		return retVal;
	}

	private boolean sampleMatchesEventMask(Sample S, long mask) {
		boolean matches = true;
		for(int i = 0; i < S.event.length; i++){
			if(S.b(mask, i)&&S.event[i]==false){
				matches =false;
				break;
			}
		}
		return matches;
	}
}
