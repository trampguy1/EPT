package mil.af.rl.jcat.bayesnet;

import java.io.Serializable;
import java.util.*;

public class ThreatResource extends SimpleResource implements Serializable{
	
	//	these maps need to be changed to allow the mutlimap with multiple allocations at 1 time (when craigs stuff is done)
	private TreeMap<Integer, Integer> initialInventory; 
	private TreeMap<Integer, Integer> inventory;
	int currentAllocationTime = 0;
	
	public TreeMap<Integer, Integer> getInitialInventory(){
		return initialInventory;
	}

	public ThreatResource(TreeMap<Integer, Integer> supplySchedule){
		initialInventory = supplySchedule;
		this.ResetResource();
		initialAllocation = 0;
	}
	public ThreatResource() {
		super();
		inventory = null;
		initialInventory = null;
	}

	//Should this constructor exist?  whats its purpose? -MD
	public ThreatResource(int initialAllocation) {
		super(initialAllocation);
		inventory = null;
		initialInventory = null;
	}

	/**
	 * @deprecated Use {@link #allocateResource(int,int,boolean)} instead
	 */
	
	/** I think this got put in by the refactor stuff
	@Override
	boolean allocateResource(int amountReq, int slice, int[] stateHistory) {
		return allocateResource(amountReq, slice, stateHistory);
	}
	*/

	@Override
	boolean allocateResource(int amountReq, int slice, int resourcesEnabled[]) {
		/*
		 * Threat resources are used to DEFINE tokens in the ensemble: there can be at most 
		 * one resource of a given type per token. Only building models will prove
		 * whether or not this is a useful concept for threats and tokens!!
		 */
		boolean notAllocatedInThisToken = true;
		for(int j = 0; j < slice && j < resourcesEnabled.length; j++){
			if(resourcesEnabled[j] != 0){
				notAllocatedInThisToken = false;
				break;
			}
		}
		/*
		 * I think this is assuming that there can only be one active allocation at a time.
		 * This might not be true and could be trouble later.
		 */
		if(notAllocatedInThisToken){
			if(inventory == null){
				return super.allocateResource(amountReq, slice, resourcesEnabled[slice] > 0);
			}else{
				Integer availableNow = inventory.get(slice);
				if(availableNow != null && availableNow >= amountReq){
					currentAllocationTime = slice;
					currentAllocation = amountReq;
					return true;
				}else{
					currentAllocationTime = SimpleResource.COULD_NOT_ALLOCATE;
					currentAllocation = SimpleResource.COULD_NOT_ALLOCATE;
					return false;
				}
			}		
		}else{
			currentAllocationTime = SimpleResource.COULD_NOT_ALLOCATE;
			currentAllocation = SimpleResource.COULD_NOT_ALLOCATE;
			return false;
		}	
	}
	
	@Override
	boolean deAllocate() {
		boolean retVal = super.deAllocate();
		if(inventory != null){
			this.currentAllocationTime = 0;
		}
		return retVal;
	}

	@Override
	boolean expend() {
		boolean retVal = false;
		if(currentAllocation != SimpleResource.COULD_NOT_ALLOCATE){
			retVal = true;
			int oldAmt = this.inventory.get(this.currentAllocationTime);
			this.inventory.put(currentAllocationTime, oldAmt - this.currentAllocation);
		}
		return retVal;
	}
	@Override
	public void ResetResource() {
		super.ResetResource();
		this.inventory = ((TreeMap<Integer, Integer>)(initialInventory.clone()));
	}
	@Override
	public int getOnHand(int time){
		int retVal = 0;
		Integer oh = inventory.get(time);
		if(oh != null){
			retVal = oh;
		}
		return retVal;
	}
	boolean ensembleElementCreated(){
		return true;
	}
	public boolean isThreat(){
		return true;
	}
}
