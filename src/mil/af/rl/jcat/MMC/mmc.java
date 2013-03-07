package mil.af.rl.jcat.MMC;

public class mmc {
	public double down = 0.0;
	public double up = 0.0;
	public double cv = 0.0;
	int nodeIndex = 0;
	public int eventIndex = 0;
	int depth =0;

	mmc(){
		}
	public mmc(int nodeIndex){
		this.nodeIndex = nodeIndex;
	}

	mmc andCombine(mmc lowj, mmc highj){
		up = java.lang.Math.min(highj.up, lowj.down);
		down = java.lang.Math.min(highj.down,  lowj.up);
		cv = highj.cv;
		return this;
	}
	mmc orCombine(mmc lowj, mmc highj){
		up = highj.up + lowj.up;
		down = highj.down + lowj.down;
		cv = highj.cv + lowj.cv;
		return this;
	}
	mmc baseValue(int j, double jpd[]){
		down = jpd[j];
		up = 1.0 - jpd[j];
		cv = jpd[j];
		return this;
	}
	mmc mappedBaseValue(int j, double jpd[], int perm[]/*mutation*/, int permSize){
		int permJ = 0;
		for(int k=0; k < permSize; k++){
			if((j & (1 << k)) == 1)
			{
				permJ ^= (1 << perm[k]);
			}
		}
		return baseValue(permJ, jpd);
	}
}
