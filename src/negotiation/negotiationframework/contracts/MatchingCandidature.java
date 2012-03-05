package negotiation.negotiationframework.contracts;

import dima.basicagentcomponents.AgentIdentifier;

public abstract class MatchingCandidature<
ActionSpec extends AbstractActionSpecification>
extends ContractTransition<ActionSpec> {

	/**
	 *
	 */
	private static final long serialVersionUID = -298715657421429671L;
	protected Boolean creation = null;


	/*
	 *
	 */

	public MatchingCandidature(final AgentIdentifier intiator,
			final AgentIdentifier a, final ResourceIdentifier r,
			final long validityTime) {
		super(intiator,
				//				"matching",
				new AgentIdentifier[] { a, r },
				//				new Object[] {},
				validityTime);
	}

	// public MatchingCandidature(AgentIdentifier intiator,
	// final AgentIdentifier a,final ResourceIdentifier r,
	// final boolean creation,
	// final long validityTime) {
	// super(intiator, "matching",new AgentIdentifier[]{a,r},new
	// Object[]{},validityTime);
	// this.creation = creation;
	// this.validityTime=validityTime;
	// }
	/*
	 *
	 */
	public AgentIdentifier getAgent() {
		return this.getAllParticipants().get(0);
	}

	public ResourceIdentifier getResource() {
		return (ResourceIdentifier) this.getAllParticipants().get(1);
	}

	public boolean isMatchingCreation() {
		return this.creation;
	}

	public Boolean getCreation() {
		return this.creation;
	}

	public void setCreation(final Boolean creation) {
		this.creation = creation;
	}

	public boolean isInitiallyValid() throws IncompleteContractException{
		boolean agentContainsResource = 
				getSpecificationOf(getAgent()).getMyResourceIdentifiers().contains(getResource());
		boolean ressourceContainsAgent = 
				getSpecificationOf(getResource()).getMyResourceIdentifiers().contains(getAgent());
		assert  (agentContainsResource && ressourceContainsAgent) || (!agentContainsResource && !ressourceContainsAgent):
			"incoherent states:\n"+getSpecificationOf(getAgent())+":\n"+getSpecificationOf(getResource());
		assert (isMatchingCreation()?!agentContainsResource:agentContainsResource):" creation impossible : creation?"
		+isMatchingCreation()+"incoherent states:\n"+getSpecificationOf(getAgent())+":\n"+getSpecificationOf(getResource());

		return super.isInitiallyValid();
	}

	/*
	 *
	 */

	@Override
	public String toString() {
		String agentSpecif, hostSpecif, agentResult, hostResult,isviable;

		try {
			agentSpecif = this.getSpecificationOf(this.getAgent()).toString();
			//			agentResult = this.computeResultingState(getAgent()).toString();
		} catch (negotiation.negotiationframework.contracts.AbstractContractTransition.IncompleteContractException e) {
			agentSpecif = "unavailable";
			//			agentResult = "unavailable";
		}
		try {
			hostSpecif = this.getSpecificationOf(this.getResource()).toString();
			//			hostResult = this.computeResultingState(getResource()).toString();
		} catch (negotiation.negotiationframework.contracts.AbstractContractTransition.IncompleteContractException e) {
			hostSpecif = "unavailable";
			//			hostResult = "unavailable";
		}
//		try {
//			isviable= isViable()+"";
//		} catch (negotiation.negotiationframework.contracts.AbstractContractTransition.IncompleteContractException e) {
//			isviable="unavailable";
//		}

		return this.getIdentifier() + " -> ("
		+ (this.creation ? "create" : "destruct") + ")"
		//		+",consensual?"+isConsensual()+"("+agentHasAccepted+","+resourceHasAccepted+")";
		+"\n  -----> agent specif : "+agentSpecif//+" --> resulting in "+agentResult
		+"\n  -----> host specif : "+hostSpecif//+" --> resulting in "+hostResult
//		+"\n ***************** isViable? "+isviable
		;

	}
}

/*
 *
 */

// private Boolean agentHasAccepted=null;
// private Boolean resourceHasAccepted=null;
// if (creation){
// this.agentHasAccepted=true;
// this.resourceHasAccepted=false;
// } else {
// this.agentHasAccepted=false;
// this.resourceHasAccepted=true;
// }

// @Override
// public void setAccepted(final AgentIdentifier id) {
// if (id.equals(this.getAgent()))//&&agentHasAccepted==null
// this.agentHasAccepted=true;
// else if (id.equals(this.getResource()))//&&resourceHasAccepted==null
// this.resourceHasAccepted=true;
// else
// throw new
// RuntimeException(id+" : I should not receive it ("+id+","+agentHasAccepted+","+resourceHasAccepted+")"+this);
// }
//
// @Override
// public void setRejected(AgentIdentifier id) {
// if (id.equals(this.getAgent()))//&&agentHasAccepted==null
// this.agentHasAccepted=false;
// else if (id.equals(this.getResource()))//&&resourceHasAccepted==null
// this.resourceHasAccepted=false;
// else
// throw new RuntimeException(id+" : I should not receive it "+this);
// }
//
// @Override
// public boolean isConsensual() {
// if (this.agentHasAccepted!=null && this.resourceHasAccepted!=null)
// return this.agentHasAccepted && this.resourceHasAccepted;
// else
// return false;
// }
//
// @Override
// public boolean isAFailure() {
// if (this.agentHasAccepted!=null && this.resourceHasAccepted!=null)
// return this.agentHasAccepted==false || this.resourceHasAccepted==false;
// else
// return false;
// }

// public Boolean getHostAnswer(){
// return resourceHasAccepted;
// }
//
// public Boolean getAgentAnswer(){
// return agentHasAccepted;
// }