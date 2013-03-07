/**
 * 
 */
package mil.af.rl.jcat.bayesnet;

import java.io.Serializable;

/**
 * @author john
 *
 */
public class SimpleResource implements Serializable {
	static final int COULD_NOT_ALLOCATE = -1;

	int initialAllocation = 0; // this is to have some values available before there is a gui; it will probably go away as time goes on
	int onHand = 0; // how many are left at this time in the plan
	int currentAllocation = 0;
	boolean isContingent = true; // expending the resource is contingent on whether the event succedes or (in the case of signals) if the parent succeeds (i.e. event becomes true)
	
	SimpleResource(){
	}
	
	SimpleResource(int initialAllocation){
		this.initialAllocation = initialAllocation;
		ResetResource();
	}
	
	public void ResetResource(){ // called when event is activated; can be called on each major simulation cycle
		onHand = initialAllocation;
		currentAllocation = 0;
	}
	
	boolean allocateResource(int amountReq, int slice, int[] resourceEnabled){// this form should not be used except for things which might be threat resources
		return allocateResource(amountReq, slice, resourceEnabled[slice] > 0);
	}
	boolean allocateResource(int amountReq, int slice, boolean resourcesEnabled){
		//TODO: Logically, this should be refactored so the resource itself doesn't need to know about states
		if(resourcesEnabled && amountReq <= onHand){// resource is 'enabled' & the resource is not depleted
			currentAllocation = amountReq;
			return true;
		}else{
			currentAllocation = COULD_NOT_ALLOCATE;
			return false;
		}
	}
	
	boolean expend(){
		if(currentAllocation != COULD_NOT_ALLOCATE && currentAllocation > 0){
			onHand -= currentAllocation;
			currentAllocation = 0;
			return true;
		}else{
			return false;
		}
	}
		
	boolean deAllocate(){
		// return value lets the caller know whether or not there actually had been an allocation
		boolean retVal = currentAllocation > 0;
		currentAllocation = 0;
		return retVal;
	}
	
	boolean ensembleElementCreated(){
		return false;
	}
	
	public boolean isThreat(){
		return false;
	}

	public int getOnHand(int time) {
		return this.onHand;
	}
}
