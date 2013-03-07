package mil.af.rl.jcat.MMC;

import java.lang.Math;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.TreeMap;
import java.io.IOException;
import java.io.PrintStream;

import com.sun.xml.internal.fastinfoset.util.StringArray;
//import com.sun.tools.javac.code.Attribute.Array;

/**
 * @author john
 *
 */
public class EPT implements EffectsProbabilityTable{

	/**
	 * @param args
	 */

/*	public static void main(String[] args) {
		double edgeValues[] = {.6,.7};
		EPT theEPT = new EPT();
		theEPT.createConditionallyIndependentCummulativeEPT(edgeValues);
		double[] marginalConstraints = {1.0, .6, .7, .42, .5, .3, .35, .21, 1.0, .6, .7, .42, .5, .3, .35, .21};		
		theEPT.createMarginallyConstrainedCummulativeEPT(4, marginalConstraints);
		}		
*/	
	int oldJ = 1;  
	double oldCv = 0.0;
	int[] eventConstraintOrder;
	int constraintCount;
	int effectCount = 0;
	ArrayList<String> effectName = new ArrayList<String>();
	int activeEffectCount = 0; // i.e. the depth to which the recursion must go
	int specifiedMarginals[][];
	int maps[][];
	TreeMap<Integer, mmc>Range = new TreeMap<Integer, mmc>();
	TreeMap<Integer, Double> value = new TreeMap<Integer, Double>(); // This to pre-specify marginal probability values
	double jpd[] = {1.0}; // jpd will hold the joint probability distribution during computation but will be convert to the cumulative distribution for return
	double marg[] = null;  // holds the ixPD version of jpd
	double cum[] = null; //holds the cumulative version 
	double[] pearsonCor = null;
	double[] baseProb = null;
	mmc treeNode[] = null; 	// both MMC and ALC are double recursive thus forming a binary tree of calls. The mmc's are values associate with each node of this activation tree.
							// mmc's cannot be local to the MMC activation's because the mmc data must be saved for ALC calls
	int sizePD = 0;
	int error = 0;
	double lastRead = 0.0; // returning a second parameter from a method
	int[] activationOrder = {}; // the order of the bits to flip on recursion
	int specifiedActivations = 0; 
	
	private double[] computeBaseProbabilities(){
		int n = effectCount;
		marg = getMarginalDistribution();
		baseProb = new double[n];
		for(int j = 0; j < n; j++){
			baseProb[j] = marg[(1<<j)];
		}
		return baseProb;
	}
	
	private double denom(int[] subset){
		double denom = 1;
		for(int j = 0; j < subset.length; j++){
			denom *= baseProb[subset[j]]*(1-baseProb[subset[j]]);
		}
		return Math.sqrt(denom);
	}
	
	double[] jpdMarg(int[] subset){
		double [] jpdMarg = new double[(1<<subset.length)];
		for(int j = 0; j < this.jpd.length; j++){
			int q = 0;
			for(int k = 0; k < subset.length; k++){
				if((j & (1 << subset[k])) != 0){
					q |= (1 << k);
				}
			}
			jpdMarg[q] += jpd[j];
		}
		return jpdMarg;
	}
	
	
	private int[] extractSubset(int d) {
		int subsetSize = 0;
		for(int k = 0; k < effectCount; k++){
			if(((1<<k)&d)!=0)subsetSize++;
		}
		int[] subset = new int[subsetSize];
		int nextEntry = 0;
		for(int k = 0; k < effectCount; k++){
			if(((1<<k)&d)!=0)subset[nextEntry++] = k;
		}
		return subset;
	}
	
	public static void  main(String[] args){
		EPT test = new EPT();
		test.effectCount = 2;
		double[] testDist = {.12, .18, .28, .42}; // indep
		//double[] testDist = {.12, .18, .0, .3, .08, .12, .08, .12}; //a, b max when no c
		//double[] testDist = {.12, .18, .12, .18, .08, .12, .08, .12}; //indep
		//double[] testDist = {.2, 0, 0, .8}; //cor = 1.

		test.jpd = testDist;
		test.sizePD = (1 << test.effectCount);
		test.computePearsonCorrelations();
	}
	private double[] computePearsonCorrelations(){
		pearsonCor = new double[(1 << effectCount)];
		pearsonCor[0] = Double.NaN;
		
		double[] p = computeBaseProbabilities();
				
		for(int d = 1; d < (1 << effectCount); d++){// compute the pearsonCor for each subset of powerset of the set of events
			int[] subset = extractSubset(d);
			double[] jpdMarg = jpdMarg(subset);// for computing the expectation for the current subset
			double denom = denom(subset);
			// take expectation
			double expectation = 0;
			for(int j = 0; j < (1 << subset.length); j++){// take the expectation of the subset
				// this loop processes the power set of the the jth subset of the subset
				double term = 1;
				for(int k=0; k < subset.length; k++){
					if((j & (1 << k)) != 0){
						term *= (1- p[subset[k]]);
					}else{
						term *= -p[subset[k]];
					}
				}
				term *= jpdMarg[j];
				expectation += term;
			}
			pearsonCor[d] = expectation / denom;
		}
		return pearsonCor;
	}
	
	public double[] getPearsonCorrelations(){
		return computePearsonCorrelations();
	}

	public void addEffect(String event){
		effectName.add(event);
	}
	
	public double[] createConditionallyIndependentCummulativeEPT(
			double[] edgeValues) {
		effectCount = edgeValues.length;
		sizePD = (1 << effectCount); //sizePD is 2 raised to the effectCount power
		// allocate the jpd and initialize it so only the only the universal event happens
		expandJPD();

		// check the input values, the edge values (i.e. 1st order marginal constraints). Values in the interval [0, 1.0] are OK
		for(int e = 0; e < effectCount; e++){
			if(edgeValues[e] < 0.0 || edgeValues[e] > 1.0){
				// throw an exception
			}
		}

		int k= 0;
		for(activeEffectCount = 1; activeEffectCount <= effectCount; activeEffectCount++){
			int curSizePD = (1 << activeEffectCount);
			for(int j = (curSizePD >> 1);  j < curSizePD; j = (j << 1), k++){// this loop prevents ALC from allocating probability mass to events not yet initialized, thereby assuring initial independence among the events
				// the following two method calls are my dissertation in action!
				MMC(j, 0, 0);
				ALC(j, 0, edgeValues[k] - treeNode[0].cv, 0);
			}
		}
		accumulateAndMarginalize();
		return jpd;
	}
	private double[] accumulateAndMarginalize() {
		cum = new double[jpd.length];
		cum[0] = jpd[0];
		for(int j = 1; j < jpd.length; j++){// make it cumulative
			cum[j] += cum[j-1] + jpd[j];;
		}
		return getMarginalDistribution();
	}

	public double[] getMarginalDistribution() {
		marg = new double[sizePD];
		for(int j = 0; j < sizePD; j++){
			marg[j] = jpd[j];
			for(int m = j + 1; m < sizePD; m++){
				if((m&j) == j){
					marg[j] += jpd[m];
				}
			}
		}
		return marg;
	}
	
	private void resetJPD(){
		effectName.clear();
		effectCount = 0;
		jpd = new double[1];
		jpd[0] = 1.0;
	}
	
	private void expandJPD() {
		effectCount = effectName.size();
		sizePD = (1 << effectCount);
		double[] jpd1 = new double[sizePD];
		for(int j = 0; j < jpd.length; j++){
			jpd1[j] = jpd[j];
		}
		jpd = jpd1;
		activationOrder = new int[effectCount];
		activeEffectCount = 0; 
		this.specifiedActivations = 0; 
		// allocate the array of tree nodes; treeNode[0] will always be the root of the tree of MMC and ALC calls.
		// treeNodes will correspond to the activation tree of MMC so that
		// if i is the index of a tree node, the index corresponding to the high 'j' value of two recursive calls will be (2*i) + 1
		// and the index for the lower 'j' call will be (2*1) + 2. See, for example, implementation of MMC.
		// The below could reuse the already allocate mmc array but I don't think that is worth the worry
		treeNode = new mmc[(sizePD << 1) - 1];
		for(int j = 0; j < (sizePD << 1) - 1; j++){
			treeNode[j] = new mmc();
		}
	}
	public EPT(){
		}
	
	public static EPT newEPT(){
		return new EPT();
	}
	public mmc MMC(int j, int depth, int nodeIndex){
		mmc node = treeNode[nodeIndex];
		mmc lowNode = null;
		mmc highNode = null;
		if(depth == effectCount){
			node.baseValue(j, jpd);
		}else{
			if((j & (1<<depth)) != 0){// its and AND node
				lowNode = MMC((j^(1<<depth)), depth + 1, (nodeIndex<<1) + 2);
				highNode = MMC(j, depth + 1, (nodeIndex << 1) + 1 );
				node.andCombine(lowNode, highNode);
			}else{// its an OR node
				lowNode = MMC(j, depth+1, (nodeIndex<<1) + 2);
				highNode = MMC(j | (1 << depth), depth+1, (nodeIndex << 1) + 1);
				node.orCombine(lowNode, highNode);
			}
		}
		return node;

	}
	public mmc MMC(int j, int depth, int[] map, int nodeIndex){
		mmc node = treeNode[nodeIndex];
		node.eventIndex = j;
		node.depth = depth;
		mmc lowNode = null;
		mmc highNode = null;
		if(depth == effectCount){
			node.baseValue(j, jpd);
		}else{
			if((j & (1<<map[depth])) != 0){// its and AND node
				lowNode = MMC((j^(1<<map[depth])), depth + 1, map,(nodeIndex<<1) + 1);
				highNode = MMC(j, depth + 1, map,(nodeIndex << 1) + 2 );
				node.andCombine(lowNode, highNode);
			}else{// its an OR node
				lowNode = MMC(j, depth+1, map, (nodeIndex<<1) + 1);
				highNode = MMC(j | (1 << map[depth]), depth+1, map, (nodeIndex << 1) + 2);
				node.orCombine(lowNode, highNode);
			}
		}
		return node;
	}
	public void ALC(int j, int depth, double change, int nodeIndex){
		if(depth == effectCount){
			jpd[j] += change;
		}
		else{
			if((j & (1<<depth)) != 0){
				ALC(j ^ (1 << depth), depth +1, -change, (nodeIndex << 1) + 2);
				ALC(j, depth + 1, change, (nodeIndex << 1) + 1);
			}else {
				mmc lowNode = treeNode[(nodeIndex << 1) + 2];
				mmc highNode = treeNode[(nodeIndex << 1) + 1];
				// the following rule provides effect independence when there are no pairwise or higher constraints, possibly always independence given constraints
				double leftChange = change >= 0.0 ? change * (lowNode.up/(lowNode.up + highNode.up))
													: change * (lowNode.down / (lowNode.down + highNode.down));
				ALC(j, depth +1, leftChange, (nodeIndex << 1) + 1);
				ALC(j | (1 << depth), depth +1, change - leftChange, (nodeIndex << 1) + 2);
			}
		}
	}
	public void ALC(int j, int depth, double change, int[] map, int nodeIndex){
		mmc node = treeNode[nodeIndex];
		if(depth == effectCount){
			jpd[j] += change;
			if(jpd[j] < 0.0){
				error += 1;
			}
		}
		else{
			if((j & (1<<map[depth])) != 0){
				ALC(j ^ (1 << map[depth]), depth +1, -change, map, (nodeIndex << 1) + 1);
				ALC(j, depth + 1, change, map, (nodeIndex << 1) + 2);
			}else {
				mmc lowNode = treeNode[(nodeIndex << 1) + 1];
				mmc highNode = treeNode[(nodeIndex << 1) + 2];
				// the following rule provides effect independence when there are no pairwise or higher constraints, possibly always independence given constraints
				double leftChange = change >= 0.0 ? change * (lowNode.up/(lowNode.up + highNode.up))
													: change * (lowNode.down / (lowNode.down + highNode.down));
				ALC(j, depth +1, leftChange, map, (nodeIndex << 1) + 1);
				ALC(j | (1 << map[depth]), depth +1, change - leftChange, map, (nodeIndex << 1) + 2);
			}
		}		
	}
	
private double[] permute(double jpd[],int map[]){
	double[] permutation = new double[jpd.length];
	
	for(int j =0; j < jpd.length; j++){
		int permIndex = j;
		for(int k = 0; k < map.length; k++){
			if((j & (1 << k)) !=  0)permIndex += (1 << map[k]); 
		}
		permutation[permIndex] = jpd[j];
	}
	return permutation;
}

private double[] unPermute(double[] permutation, int map[]){
	double[] jpd = new double [permutation.length];
	for(int p = 0; p < map.length; p++){
		int uPermIndex = 0;
		for(int k = 0; k < map.length; k++){
			if((p & (1 << map[k])) != 0) uPermIndex += (1 << k);
		}
		jpd[uPermIndex] = permutation[p];
	}
	return jpd;
}
	
	public double[] createMarginallyConstrainedCummulativeEPT(int effectCount, double[] marginalConstraints) {
		int map[][] = {{0}, {1,0}, {2,1},{3,2}};
		sizePD = marginalConstraints.length; //sizePD is 2 raised to the effectCount power
		if((1 << effectCount) == sizePD){
			expandJPD();
			for(activeEffectCount = 1; activeEffectCount <= effectCount; activeEffectCount++){
				/* a reordering of the previous effects can take place here on every iteration
				 * This will impact the number of iterations in the next loop: it may be cursz is 5 but we
				 * 				only want to do, say, 3 loops
				*/
				int curMap[] = map[activeEffectCount - 1];
				int curSizePD = (1 << activeEffectCount);
				for(int j = (curSizePD >> 1);  j < curSizePD; j++){// this loop prevents ALC from allocating probability mass to events not yet initialized, thereby assuring initial independence among the events
					// the following two method calls are my dissertation in action!
					mmc range = MMC(j, 0, curMap, 0);
					double change = marginalConstraints[j] - range.cv;
					if(change >= -range.down && change <= range.up){
						ALC(j, 0, change, curMap, 0);
					}else{
						// throw an exception: constraint is not feasible given previous constraints
					}
				}
			}
			accumulateAndMarginalize();
		}else{
			// throw an exception: The number of effects and the number of marginal constraints are not consistent
		}
		return jpd;
	}
/*	
	public static void  main(String[] args){  
		EPT e = new EPT();
		e.analyzeEPT(e.createEPT());
	}
*/

	private double analyzeEPT(double[] jpd) {
		double sum = 0;
		for(int j = 0; j < jpd.length; j++){
			sum += jpd[j];
		}
		double[] IEjpd = new double[jpd.length]; 
		for(int k = 0; k < IEjpd.length; k++){
			for(int j = k; j < jpd.length; j++){
				if((j & k) == k){
					IEjpd[k] += jpd[j];
				}
			}
		}
		return sum;
	}
	private double nextInput(boolean newVariable, int newConstraint){
		double marginalProb = -1.0;
		if(newVariable){
			effectCount += 1;
			// getName
			double[] expandedJpd = new double[this.jpd.length * 2];
			for(int j = 0; j < jpd.length; j++){
				expandedJpd[j] = jpd[j];
			}
			jpd = expandedJpd;
			// get new priority order
		}
		// get next margprob
		return marginalProb;
	}
	private void loadInputs1(){
		effectCount = 3; // 3 variables in the EPT
		int [][] correlations = {{1}, {2,3},{4,6,7}};
		specifiedMarginals = correlations;// to transfer class variable
		/*
		 * correlations are the (index of) the known marginal probabilities
		 * thus we are saying we know {{p(a)}, {p(b), p(a,b)}, {p(c),p(a,c}, p(a,b,c)}}
		 * There (known) values are:
		 */
		value.put(1, .5);
		value.put(2, .6);
		value.put(3,.1);
		value.put(4, .6);
		value.put(6, .4);
		
		
		int[][] constraintOrders = {{0},{0,1}, {1,0,2}};  
		maps = constraintOrders;// to transfer to class variable
		/*
		 * The order in which to expand the evaluation tree (done correctly, this prevents 
		 * defaulted values from being used as constraints
		 */
	}
	
	private void loadInputs(){
		effectCount = 4;
		int[][] correlations = {{1}, {2,3}, {4,6}, {8, 12}};
		specifiedMarginals = correlations;
		int[][] constraintOrders = {{0}, {0,1}, {1,0,2}, {2,0,1,3}};
		maps = constraintOrders;
		value.put(1, 0.6);
		value.put(2, .7);
		value.put(3, .42);
		value.put(4, .5);
		value.put(6, .35);
		value.put(8, .4);
		value.put(12 , .25);
	}
	
	private String listEffectNames(int probIndex){
		String list = "";
		boolean firstItem = true;
		for(int j = 0; j < this.effectCount; j++){
			if((probIndex & (1 << j)) != 0){
				if(!firstItem){
					list = list.concat(",");
				}
				list = list.concat(this.effectName.get(j));
				firstItem = false;
			}
		}
		return list;
	}
	
	private int elicitValue(mmc range, int curProb){
		double min = range.cv - range.down;
		double max = range.cv + range.up;
		do{
			byte[] buf = new byte[20];
			String currentGroup = listEffectNames(curProb);
			if(currentGroup == null) break;
			System.out.print(min + " <= [p(" + currentGroup + ")="  + range.cv + "] <= " + max + ":");
			try{
				System.in.read(buf, 0, buf.length);
				lastRead =new Double(new String(buf)).doubleValue();
			}catch(Exception e){
				System.err.print("Oh my, not the right format for a double!\n");
				lastRead = -2;
			}
		}while((lastRead > max) || (lastRead < min));
		return curProb;
	}
	
	private int nextProbIndex(int lastIndex, int[] eventConstraintOrder){
		int forDebugging = eventConstraintOrder.length;
		if(lastIndex == 0){// this for initialization of the very first effect (only)
			return (1 << eventConstraintOrder[0]);
		}else{// add 1 to last index
			int j = 0;
			for(; j < eventConstraintOrder.length; j++){
				lastIndex ^= (1 << eventConstraintOrder[j]);	// flip jth bit			
				if((lastIndex & (1 << eventConstraintOrder[j])) != 0){
					break;// no need to carry to next position
				}
			}
			if(((activeEffectCount+1 != effectCount) &&
					((lastIndex & (1 << eventConstraintOrder[activeEffectCount])) != 0))//we are starting on effects we want to default 
					|| (j == eventConstraintOrder.length))// we are done with the latest variable
			{
				return -1;
			}
			return lastIndex;
		}
	}
	
	private int nextConstraintIndex(int curIndex){
		int j = 0;
		for(; j < constraintCount; j++){
			curIndex ^= (1 << eventConstraintOrder[j]);	// flip jth bit			
			if((curIndex & (1 << eventConstraintOrder[j])) != 0){
				break;// no need to carry to next position
			}
		}
		return j < constraintCount ? curIndex : -1;
	}
	
	public mmc MMCALCReset(){// I think is deprecated and has never been tested.
		resetJPD();
		//Printf("I am over here trying to reinitialize the EPT object");
		System.out.println("I am over here trying to reinitialize the EPT object");

		/* copied from object initialization*/
		oldJ = 1;  
		oldCv = 0.0;
		eventConstraintOrder = null;
		effectCount = 0;
		effectName = new ArrayList<String>();
		activeEffectCount = 0; // i.e. the depth to which the recursion must go
		specifiedMarginals = null;
		maps = null;
		Range = new TreeMap<Integer, mmc>();
		value = new TreeMap<Integer, Double>(); // This to pre-specify marginal probability values
		// see reset jpd jpd = {1.0}; // jpd will hold the joint probability distribution during computation but will be convert to the cumulative distribution for return
		marg = null;  // holds the ixPD version of jpd
		cum = null; //holds the cumulative version 
		pearsonCor = null;
		baseProb = null;
		treeNode = null; 	// both MMC and ALC are double recursive thus forming a binary tree of calls. The mmc's are values associate with each node of this activation tree.
								// mmc's cannot be local to the MMC activation's because the mmc data must be saved for ALC calls
		
		
		sizePD = 0;
		error = 0;
		lastRead = 0.0; // returning a second parameter from a method
		activationOrder = new int[0]; // the order of the bits to flip on recursion
		specifiedActivations = 0; 
		/* end of copying*/
		
		//stuff for working withgwt client
		mmc result = new mmc();
		result.eventIndex = 0;
		return result;
		}
	
	public  mmc MMCALC(String name, int correlates[], int cCount){// first step in expansion
		// for first step in expanding EPT to include another effect
		effectName.add(name);
		expandJPD();
		eventConstraintOrder = correlates;
		constraintCount = cCount;
		int probIndex = (1 << (effectCount-1));
		mmc result = MMC(probIndex, 0, eventConstraintOrder, 0);
		oldJ = probIndex;
		return result; 
	}

	public mmc MMCALC(float desiredValue){// ?first step in setting a desired value and computing the 'next' range
		double change = desiredValue - oldCv;
		if(oldJ > 0)ALC(oldJ, 0, change, eventConstraintOrder, 0);
		int j = nextConstraintIndex(oldJ);
		oldJ = j;
		double res[] = new double[4];
		mmc result = null;
		if(j >= 0){
			result = MMC(j, 0, eventConstraintOrder, 0);
			res[0] = result.cv;
			oldCv = result.cv;
			res[1] = result.down;
			res[2] = result.up;
			res[3] = j;
		}else{
			oldCv = 0.0;
			result = new mmc();
			result.eventIndex = j;
		}
		return result;
	}
	
	public double[] createEPT(){// not used for cloud stuff
		while(moreEffects()){
//			this.loadInputs1();// these are the inputs: this implementation does no IO.
//			this.initializeComputation();
			expandJPD();

//			int[]  eventConstraintOrder = maps[k];
			int[] eventConstraintOrder = correlatedEvents(effectCount - 1);
			int probIndex = (1 << (effectCount - 1)) - 1;
			while(true){
				probIndex = nextProbIndex(probIndex, eventConstraintOrder);
				if(probIndex < 0)break;
//			for(int probIndex = (indexLim >> 1); probIndex < indexLim; probIndex++){
				mmc range = MMC(probIndex,0, eventConstraintOrder, 0);
				int nextProb = elicitValue(range, probIndex);
				if(this.lastRead >= 0.0){
					double chg = lastRead - range.cv;
					if(chg >= -range.down && chg <= range.up){
						ALC(probIndex, 0, chg, eventConstraintOrder, 0);
					}else{// the value for this constraint is outside its feasible range
						// what SHOULD we do here?  That is a GOOD question!
						int sam = 1;// anyway, we can catch it with the debugger
						sam -=1;
					}
				}else{
					// accept the default value
				}
				double[] marg = this.accumulateAndMarginalize();
				@SuppressWarnings("unused")
				double[] sam = marg;
			}
		}
		// take ur CAC
		return jpd;
	}
	private int[] correlatedEvents(int newEventIndex) {//not for cloud
		int[] defaultedEffect = new int[47];
		int defaultCount = 0;
		this.activeEffectCount = 0;
		try{
			byte[] buf = new byte[50];
			for(int j = 0; j < effectCount - 1; j++){
				System.out.print("Correlate the new variable with " + effectName.get(j) + "? (y/n)"); 
				
				System.in.read(buf);
				if(buf[0] == 'y'){
					activationOrder[this.activeEffectCount++] = j;
					specifiedActivations += 1;
				}else{
					defaultedEffect[defaultCount++] = j;
				}
			}
		}catch(IOException e){
			System.err.print("Huh?\n");
		}
		int k = specifiedActivations;
		for(int j = 0; j < defaultCount; j++){
			activationOrder[k++] = defaultedEffect[j];
		}
		activationOrder[effectCount - 1] = newEventIndex;
		return activationOrder;
	}
	
	public ArrayList<String> anotherEffect(String newEvent){// ??
		this.effectName.add(newEvent);
		this.effectCount = effectName.size(); // is this really necessary
		return this.effectName;
	}
	public ArrayList<String> getEffectsList(){// ??
		return this.effectName;
	}
	private Boolean moreEffects() {// not for cloud
		try{
			byte[] buf = new byte[50];
			System.out.print("Add a new effect?\n");
			int ans =System.in.read();
			System.in.read(buf);
			if((char)ans == 'n'){
				return false;
			}else{
				System.out.println("New effect name:\n ");
				System.in.read(buf);
				this.effectName.add(new String(buf).trim());
				return true;
			}
		}catch(IOException e){
			System.err.print("WTF??!!!\n");
		}
		return false;
	}

// This stuff to support a paper and is not ever executed in this environment
	static void monteCarloSample(Object event, Object sample){};
	public void simpleSampling(ArrayList sample, ArrayList topoSortedEventList){
		int N = sample.size();
		int eventCount = topoSortedEventList.size();
		for(int n = 0; n< N; n++){
			Object currentSample = sample.get(n);
			for(int e = 0; e < eventCount; e++){
				monteCarloSample(topoSortedEventList.get(e), currentSample);
			}
		}
	}
	public void invertedSampling(ArrayList sample, ArrayList topoSortedEventList){
		int N = sample.size();
		int eventCount = topoSortedEventList.size();
		for(int e = 0; e < eventCount; e++){
			Object currentEvent = topoSortedEventList.get(e);
			for(int n = 0; n < N; n++){
				monteCarloSample(currentEvent, sample.get(n));
			}
		}
	}

	static double random(){return 0.5;};
	static void addMechanismToCPT_EPT(Mechanism mechanism){};
	ArrayList<Sample> initializeSampleSet(long n){ArrayList<Sample> set = new ArrayList<Sample>((int) n); for(Sample sample: set){sample = new Sample(n);} return set;};
	public class Sample{
		double[] sample = null;
		Sample(long n){sample = new double[(int) n]; sample[0]= 1.0;}
		void modifySampleSetToAccountForExpandedCPT(Mechanism mechanism){if(sample[mechanism.a.index]==1&&random()<=mechanism.p)sample[mechanism.x.index]=1;}
		void modifySampleSetToAccountForExpandedEPT(Mechanism mechanism){if(sample[mechanism.a.index]==1&&random()<=mechanism.p)sample[mechanism.x.index]=1;}
		boolean b(long j, long i){return ((1<<i)&j)==1;}
	}
	public class Node{
		int index;
	}
	public class Mechanism{
		Node a;
		Node x;
		double p;
	}


	public void proofAlgorithm(ArrayList<Node> nodeEvents, ArrayList<Mechanism> topoSortedEdgeMechanism){
		ArrayList<Sample> CPTsamples = initializeSampleSet((1 << nodeEvents.size()));
		ArrayList<Sample> EPTsamples = initializeSampleSet((1 << nodeEvents.size()));
		for(Mechanism mechanism: topoSortedEdgeMechanism){
			addMechanismToCPT_EPT(mechanism); // one iteration of the while loop in "Constructing Equivalent CPT and EPT Models"
			for(Sample sample: CPTsamples){sample.modifySampleSetToAccountForExpandedCPT(mechanism);}
			for(Sample sample: EPTsamples){sample.modifySampleSetToAccountForExpandedEPT(mechanism);}
		}
	}
}