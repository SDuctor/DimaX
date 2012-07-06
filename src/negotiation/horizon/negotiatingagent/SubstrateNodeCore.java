package negotiation.horizon.negotiatingagent;

import java.util.Collection;

import negotiation.horizon.SubstrateNode;
import negotiation.horizon.contracts.HorizonContract;
import negotiation.horizon.contracts.SubstrateNodeSpecification;
import negotiation.negotiationframework.contracts.ReallocationContract;
import negotiation.negotiationframework.contracts.AbstractContractTransition.IncompleteContractException;
import negotiation.negotiationframework.rationality.RationalCore;
import negotiation.negotiationframework.rationality.SimpleRationalAgent;
import negotiation.negotiationframework.rationality.SocialChoiceFunction.SocialChoiceType;
import dima.introspectionbasedagents.services.BasicAgentCompetence;

/**
 * This class is the RationalCore of the SubstrateNode, it provides means to
 * evaluate preferences on the allocations and to execute them.
 * 
 * @author Vincent Letard
 */
public class SubstrateNodeCore
	extends
	BasicAgentCompetence<SimpleRationalAgent<SubstrateNodeState, HorizonContract>>
	implements RationalCore<SubstrateNodeState, HorizonContract> {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = -4617793988428190194L;

    /**
     * The referent object to evaluate preferences.
     */
    private final HorizonPreferenceFunction myChoiceFunction;

    /**
     * @param socialWelfare
     *            The type of of ordering of utilities.
     */
    public SubstrateNodeCore(final SocialChoiceType socialWelfare) {
	this.myChoiceFunction = new HorizonPreferenceFunction(socialWelfare);
    }

    /**
     * SubstrateNode of this SubstrateNodeCore.
     */
    @Override
    public SubstrateNode getMyAgent() {
	return (SubstrateNode) super.getMyAgent();
    }

    /**
     * Identifier of the SubstrateNode of this SubstrateNodeCore.
     */
    @Override
    public SubstrateNodeIdentifier getIdentifier() {
	return (SubstrateNodeIdentifier) super.getIdentifier();
    }

    /**
     * Sets the current specification (measured level of service) to the
     * specified contract.
     */
    @Override
    public void setMySpecif(final SubstrateNodeState s, final HorizonContract c) {
	c.setSpecification(new SubstrateNodeSpecification(this.getIdentifier(),
		this.getMyAgent().myMeasureHandler.performMeasures()));
    }

    /**
     * This method relies on the HorizonPreferenceFunction to give a utility
     * evaluation for the specified collection of contracts.
     */
    @Override
    public Double evaluatePreference(Collection<HorizonContract> cs) {
	return this.myChoiceFunction.getUtility(cs);
    }

    /**
     * Apply all changes pending with the specified collection of contracts to
     * the SubstrateNode of this object.
     */
    @Override
    public void execute(Collection<HorizonContract> contracts) {
	try {
	    this.getMyAgent().setNewState(
		    ReallocationContract.computeResultingState(this
			    .getMyAgent().getMyCurrentState(), contracts));
	} catch (IncompleteContractException e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * This method relies on the HorizonPreferenceFunction to match the two
     * specified collections of contracts.
     */
    @Override
    public int getAllocationPreference(final SubstrateNodeState s,
	    final Collection<HorizonContract> c1,
	    final Collection<HorizonContract> c2) {
	return this.myChoiceFunction.getSocialPreference(c1, c2);
    }
}