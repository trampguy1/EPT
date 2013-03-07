package mil.af.rl.jcat.bayesnet;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import mil.af.rl.jcat.util.Guid;

import mil.af.rl.jcat.plan.AbstractPlan;
import mil.af.rl.jcat.plan.PlanItem;
 
public abstract class Policy implements Serializable{

	abstract int orders(NetNode node, HashMap<NetNode, LikelihoodNodeData> nodeData, int slice, int resourcedCPTIndex);
	abstract void reset();
	abstract String getTypeName();
	
	protected ArrayList<Guid> itemMember = new ArrayList<Guid>();
	volatile protected ArrayList<NetNode> nodeMember = new ArrayList<NetNode>();
	public enum policyType{
		FirstComeFirstServe,
		SingleAssignment;
	}
	
	public Policy(){
	}
	public Policy(Element policy) {
		Iterator m = policy.selectNodes("//member").iterator();
		for(;m.hasNext();){
			Element member = (Element)m.next();
			addMember(new Guid(member.attributeValue("guid")));
		}
	}


	public static void parsePolicies(Element policies, AbstractPlan plan){
		if(policies == null)return; // nothing to parse!
		ArrayList<Policy> activePolicies= plan.activePolicies;
		Iterator p = policies.selectNodes("//policy").iterator();
		for(;p.hasNext();){
			Element policy = (Element)p.next();
			String type = policy.attributeValue("policy_type");
			Policy createdPolicy = null;
			if(type.compareTo("FirstComeFirstServe") == 0){
				createdPolicy = new FirstComeFirstServe(policy);
				activePolicies.add(createdPolicy);
			}
			else if(type.compareTo("MostEffectiveSingleResource") == 0){
				createdPolicy = new MostEffectiveSingleResource(policy);
				activePolicies.add(createdPolicy);
			}
			for(Guid gEv: createdPolicy.itemMember){
				PlanItem ev = plan.getItem(gEv);
				ev.setGroup(createdPolicy);
			}
		}
	}

	
	public Element XMLYourself(Element policies){
		Element policy = DocumentHelper.createElement("policy");
		policy.addAttribute("policy_type", getTypeName());
		for(Guid g: itemMember){
			Element mem = DocumentHelper.createElement("member");
			policy.add(mem);
			mem.addAttribute("guid", g.toString());
		}
		policies.add(policy);
		return policies;
	}

	public void addMember(Guid newMember){
		itemMember.add(newMember);
	}
	public void addMember(NetNode newMember){
		nodeMember.add(newMember);
	}
	public ArrayList<Guid> getItemMembers(){
		return itemMember;
	}
	public ArrayList<NetNode> getNodeMembers(){
		return nodeMember;
	}
	public boolean isMember(NetNode candidate){
		return itemMember.contains(candidate);
	}
}
