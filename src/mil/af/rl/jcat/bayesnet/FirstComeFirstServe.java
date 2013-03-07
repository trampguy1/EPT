/**
 *
 */
package mil.af.rl.jcat.bayesnet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import org.dom4j.Element;

import mil.af.rl.jcat.plan.PlanItem;

/**
 * @author lemmerj
 *
 */
public class FirstComeFirstServe extends SingleAssignmentPerThreat {
	final static private String typeName = "FirstComeFirstServe";
	TreeMap<NetNode, Integer> assignments;
	
	public FirstComeFirstServe(){
		
	}
	public FirstComeFirstServe(Element policy) {
		super(policy);
	}
	public int orders(NetNode node, HashMap<NetNode, LikelihoodNodeData> nodeData, int slice, int resourcedCPTIndex){
		// but we don't need assignments right here, we will only check to see if another member of the group has already happened
		boolean oneMemberHasOccurred = false;
		for(NetNode  mem : node.policy.getNodeMembers()){
			if(nodeData.get(mem).state[slice] > 0){
				oneMemberHasOccurred = true;
			}
		}
		if(oneMemberHasOccurred){
			return 1;
		}else{
			return resourcedCPTIndex;
		}
	}
	private void makeAssignments(){
	// this, tragically, assumes there is only one threat in the current token
	}
	public void reset(){
		this.nodeMember = new ArrayList<NetNode>();
	}
	protected String getTypeName(){
		return typeName;
	}
	public Element XMLYourself(Element policies) {
		// no data to XMLize
		super.XMLYourself(policies);
		return policies;
	}
}
