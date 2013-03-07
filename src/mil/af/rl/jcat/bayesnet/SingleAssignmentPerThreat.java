/**
 *
 */
package mil.af.rl.jcat.bayesnet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import org.dom4j.Element;

import mil.af.rl.jcat.bayesnet.Policy.policyType;
import mil.af.rl.jcat.plan.PlanItem;
import mil.af.rl.jcat.util.Guid;

/**
 * @author john
 *
 */
public class SingleAssignmentPerThreat extends Policy {
	static final long serialVersionUID = 1;
	final private static String typeName = "Single Assignment";
	policyType itsType = policyType.SingleAssignment;
	
	public SingleAssignmentPerThreat(){
		
	}
	public SingleAssignmentPerThreat(Element policy) {
		super(policy);
	}
	public int orders(NetNode node, HashMap<NetNode, LikelihoodNodeData> nodeData, int slice, int resourcedCPTIndex) {
		// returns the CPTindex to be used for sampling node
		return 1;
	}
	public void resetNodeMembers(){
		nodeMember = new ArrayList<NetNode>();
	}
	public void reset(){
	}
	public Element XMLYourself(Element policies) {
		super.XMLYourself(policies);
		return policies;
	}
	protected String getTypeName(){
		return typeName;
	}
	public Policy.policyType sam(){
		return policyType.SingleAssignment;
	}
	public void test(policyType type){
		switch(type){
			case SingleAssignment:
			break;
		}
	}
}
