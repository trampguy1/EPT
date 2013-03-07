/**
 * 
 */
package mil.af.rl.jcat.bayesnet;

import java.util.HashMap;

import org.dom4j.Element;

/**
 * @author john
 *
 */
public class MostEffectiveSingleResource extends Policy {

	/* (non-Javadoc)
	 * @see mil.af.rl.jcat.bayesnet.Policy#getTypeName()
	 */
	public MostEffectiveSingleResource(){
		super();
	}
	public MostEffectiveSingleResource(Element policy) {
		super(policy);
	}
	@Override
	String getTypeName() {
		return "MostEffectiveSingleResource";
	}

	/* (non-Javadoc)
	 * @see mil.af.rl.jcat.bayesnet.Policy#orders(mil.af.rl.jcat.bayesnet.NetNode, java.util.HashMap, int, int)
	 */
	@Override
	int orders(NetNode node, HashMap<NetNode, LikelihoodNodeData> nodeData,
			int slice, int resourcedCPTIndex) {
		return ResourcedLHSampler.selecteHPResourcedCause(node, slice, resourcedCPTIndex);
	}

	/* (non-Javadoc)
	 * @see mil.af.rl.jcat.bayesnet.Policy#reset()
	 */
	@Override
	void reset() {

	}

}
