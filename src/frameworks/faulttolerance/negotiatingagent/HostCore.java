package frameworks.faulttolerance.negotiatingagent;

import java.util.ArrayList;
import java.util.Collection;

import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.services.darxkernel.ReplicationHandler;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import frameworks.negotiation.contracts.AbstractContractTransition.IncompleteContractException;
import frameworks.negotiation.rationality.AgentState;
import frameworks.negotiation.rationality.AltruistRationalCore;
import frameworks.negotiation.rationality.RationalCore;
import frameworks.negotiation.rationality.SimpleRationalAgent;
import frameworks.negotiation.rationality.SocialChoiceFunction;
import frameworks.negotiation.rationality.SocialChoiceFunction.SocialChoiceType;

/**
 * This class contains the core evaluation, decision and execution methods of an
 * host
 *
 * @author Sylvain Ductor
 *
 * @param <Contract>
 */
public class HostCore
extends	BasicAgentCompetence<SimpleRationalAgent<HostState, ReplicationCandidature>>
implements RationalCore<SimpleRationalAgent<HostState, ReplicationCandidature>,HostState, ReplicationCandidature> {
	private static final long serialVersionUID = -179565544489478368L;

	private final ReplicationSocialOptimisation myOptimiser;
	final boolean observeResourceChanges;
	final boolean memorizeRessourceState;

	//
	// Constructor
	//

	public HostCore(final SocialChoiceType socialWelfare, final boolean observeResourceChanges, final boolean memorizeRessourceState) {
		this.myOptimiser = new ReplicationSocialOptimisation(socialWelfare);
		this.observeResourceChanges=observeResourceChanges;
		this.memorizeRessourceState=memorizeRessourceState;
	}

	public boolean iObserveMyRessourceChanges() {
		return this.observeResourceChanges;
	}
	public boolean iMemorizeMyRessourceState() {
		return this.memorizeRessourceState;
	}
	public void handleResourceInformation(final AgentState c, boolean creation){
		if (this.iMemorizeMyRessourceState()) {
			if (creation) {
				this.getMyAgent().getMyInformation().add(c);
			} else {
				this.getMyAgent().getMyInformation().remove(c);
			}
		}
		if (this.iObserveMyRessourceChanges()) {
			if (creation) {
				this.observe(c.getMyAgentIdentifier(), SimpleRationalAgent.stateChangementObservation);
			} else {
				this.stopObservation(c.getMyAgentIdentifier(), SimpleRationalAgent.stateChangementObservation);
			}
		}
	}

	//
	// Methods
	//

	@Override
	public int getAllocationPreference(
			final Collection<ReplicationCandidature> c1,
			final Collection<ReplicationCandidature> c2) {
		//La mise a jour des spec actualise les contrats mais ne modifie pas l'ordre!!!
		//		for (final ReplicationCandidature c : c1) {
		//			c.setInitialState(getMyAgent().getMyCurrentState());
		//		}
		//		for (final ReplicationCandidature c : c2) {
		//			c.setInitialState(getMyAgent().getMyCurrentState());
		//
		//		}
		AltruistRationalCore.verifyStateConsistency(this.getMyAgent(), c1, c2);
		final int pref = this.myOptimiser.getSocialPreference(c1, c2);
		this.logMonologue(
				"Preference : "+pref+" for \n "+c1+"\n"+c2,
				SocialChoiceFunction.log_socialWelfareOrdering);
		return pref;
	}



	@Override
	public void execute(final Collection<ReplicationCandidature> cs) {
		try {
			//			assert ContractTransition.allViable(cs):cs;//this.getMyAgent().respectRights(c);
			//		logMonologue(
			//				"executing "+c+" from state "
			//		+this.getMyAgent().getMyCurrentState()
			//		+" to state "+c.computeResultingState(
			//						this.getMyAgent().getMyCurrentState()));

			/*
			 *
			 */
			if (!cs.isEmpty()){
				final Collection<ReplicationCandidature> creation = new ArrayList<ReplicationCandidature>();
				final Collection<ReplicationCandidature> destruction = new ArrayList<ReplicationCandidature>();

				for (final ReplicationCandidature c : cs) {
					if (c.isMatchingCreation()) {
						creation.add(c);
					} else {
						destruction.add(c);
					}
				}
				HostState newState = this.getMyAgent().getMyCurrentState();
				for (final ReplicationCandidature c : destruction){
					this.handleResourceInformation(c.getAgentInitialState(),false);//the state is maybe not the actual one but  valid with regard to the couple (host,agent)
					ReplicationHandler.killReplica(c.getAgent());
					this.logMonologue( "  ->I have killed " + c.getAgent(),LogService.onBoth);
					newState = c.computeResultingState(newState);
				}

				for (final ReplicationCandidature c : creation){
					this.handleResourceInformation(c.getAgentResultingState(),true);//the state is maybe not the actual one but  valid with regard to the couple (host,agent)
					ReplicationHandler.replicate(c.getAgent());
					this.logMonologue( "  ->I have replicated " + c.getAgent(),LogService.onBoth);
					newState = c.computeResultingState(newState);
				}
				this.getMyAgent().setNewState(newState);
			}
		} catch (final IncompleteContractException e) {
			throw new RuntimeException();
		}
	}



	@Override
	public void setMySpecif(
			final HostState s,
			final ReplicationCandidature c) {
		//		return new NoActionSpec();
	}

	@Override
	public Double evaluatePreference(final Collection<ReplicationCandidature> cs) {
		return this.myOptimiser.getUtility(cs);
	}
}
//


//public void executeFirstRep(
//		final ReplicationCandidature c,
//		final SimpleRationalAgent ag) {
//	assert this.getMyAgent().respectRights(c);
//
//	this.getMyAgent().setNewState(
//			c.computeResultingState(
//					this.getMyAgent().getMyCurrentState()));
//	this.getMyAgent().getMyInformation().add(c.getAgentResultingState());
//
//	/*
//	 *
//	 */
//
//	if (c.isMatchingCreation()) {
//		ag.addObserver(this.getMyAgent().getIdentifier(),
//				SimpleObservationService.informationObservationKey);
//		ReplicationHandler.replicate(c.getAgent());
//		this.logMonologue(c.getResource() + "  ->I have initially replicated "
//				+ c.getAgent(),LogService.onBoth);
//	} else
//		throw new RuntimeException();
//
//}

//
//@Override
//public HostState getMyResultingState(final HostState fromState,
//		final ReplicationCandidature c) {
//	if (c == null)
//		this.logException("contrat null!!!!!");
//	else if (c.getAgentSpecification() == null)
//		this.logException("un defined agent!!!");
//	final ReplicaState replica = c.getAgentSpecification();
//	final boolean creation = c.isMatchingCreation();
//	if (replica == null)
//		throw new NullPointerException();
//
//	if (fromState.Ihost(replica.getMyAgentIdentifier())) {
//		if (creation == false) {
//			final HostState h = new HostState(fromState, replica);
//			if (!h.Ihost(replica.getMyAgentIdentifier()))
//				return h;
//			else
//				throw new RuntimeException("error while predicting " + c
//						+ "\n   _-> from state :" + fromState
//						+ "\n  _-> result state " + h);
//
//		} else {
//			this.getMyAgent().sendMessage(
//					replica.getMyAgentIdentifier(),
//					new ShowYourPocket(this.getMyAgent().getIdentifier(),
//							"hostcore:getmyresultingstate"));
//			throw new RuntimeException(
//					"oohhhhhhhhhhhhhhhhh  =( ALREADY CREATED"
//							+ replica
//							+ "\n ----> current state"
//							+ this
//							+ ((SimpleNegotiatingAgent) this.getMyAgent())
//							.getMyProtocol().getContracts());
//		}
//
//	} else {
//		if (creation == true) {
//
//			final HostState h = new HostState(fromState, replica);
//			if (h.Ihost(replica.getMyAgentIdentifier()))
//				return h;
//			else
//				throw new RuntimeException("error while predicting " + c
//						+ "\n   _-> from state :" + fromState
//						+ "\n  _-> result state " + h);
//		} else {
//			this.getMyAgent().sendMessage(
//					replica.getMyAgentIdentifier(),
//					new ShowYourPocket(this.getMyAgent().getIdentifier(),
//							"hostcore:getmyresultingstate"));
//			throw new RuntimeException(
//					"ooohhhhhhhhhhhhhhhhh  =( CAN NOT DESTRUCT " + replica
//					+ "\n ----> current state" + this);
//		}
//	}
//}


// if
// (getMyAgent().getIdentifier().toString().equals("#HOST_MANAGER##simu_0#HostManager_1:77"))
// logMonologue("!!!!!!!!!!!!!!!!!!!!executing "+c+" my state "+getMyAgent().getMyCurrentState());
// this.stopObservation(c.getAgent(), AgentInfo.class);
// getMyAgent().getMyCurrentState().myReplicatedAgents.remove(c.getAgent());
// this.observe(c.getAgent(), AgentInfo.class);
// getMyAgent().getMyCurrentState().myReplicatedAgents.add(c.getAgent());
// if
// (getMyAgent().getIdentifier().toString().equals("#HOST_MANAGER##simu_0#HostManager_1:77"))
// logMonologue("new state "+getMyAgent().getMyCurrentState());

// /*
// *
// */
//
// public void predictUpdate(final AgentIdentifier agent) {
// if (this.myState.getMyReplicatedAgents().contains(agent))
// this.myState.freeReplica(agent);
// else
// allocateReplica(this.myAgent.getMyInformation().getBelievedState(agent));
// }
//
// public void update(final AgentIdentifier agent) {
// try {
// if (this.myState.getMyReplicatedAgents().contains(agent))
// this.myState.freeReplica(agent);
// else
// allocateReplica(this.myAgent.getMyInformation().getAgentState(agent));
// } catch (final MissingInformationException e) {
// this.myAgent.getMyInformation().obtainInformation(e);
// this.myAgent.retryWhen(
// this.myAgent.getMyInformation(),
// "hasInformation",
// new Object[]{e}, new Object[]{agent});
// }
// }
//
// public void reset() {
// final Collection<AgentIdentifier> agentToRemove = new
// ArrayList<AgentIdentifier>();
// agentToRemove.addAll(this.myState.getMyReplicatedAgents());
// for (final AgentIdentifier r : agentToRemove)
// this.myState.freeReplica(r);
// }
//
//
// public void allocateReplica(final AgentIdentifier replica){
// this.myReplicatedAgents.add(replica);
// this.procCurrentCharge+= getMreplica.getProcCharge();
// this.memCurrentCharge+=replica.getMemCharge();
// }
//
// public void freeReplica(final AgentIdentifier agentIdentifier){
// final ReplicaState agentState =
// this.myReplicatedAgents.remove(agentIdentifier);
// this.procCurrentCharge-= agentState.getProcCharge();
// this.memCurrentCharge-=agentState.getMemCharge();
// }