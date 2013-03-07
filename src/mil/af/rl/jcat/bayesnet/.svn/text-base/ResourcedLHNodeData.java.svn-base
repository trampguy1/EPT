package mil.af.rl.jcat.bayesnet;

import java.util.ArrayList;

public class ResourcedLHNodeData extends LikelihoodNodeData {
	
	ArrayList<int[]> ensemble = new ArrayList<int[]>();
	public int resourceTrack[]; 
	
	public ResourcedLHNodeData() {
		super();
		ensemble = new ArrayList<int[]>();
	}

	public int[] getSample(int sample, int size) {
		if(sample < ensemble.size()){
			return ensemble.get(sample);
		}else{
			return addSample(size);
		}
	}
	public int[] addSample(int sliceCount){
		int [] state = new int[sliceCount];
		for(int j = 0; j < sliceCount; j++){
			state[j] = 0;
		}
		ensemble.add(state);
		return state; 
	}
}