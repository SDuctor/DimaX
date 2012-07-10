package negotiation.negotiationframework.selection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.contracts.AbstractActionSpecif;
import negotiation.negotiationframework.contracts.AbstractContractTransition;
import negotiation.negotiationframework.contracts.ContractTrunk;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.SelectionCore;
import negotiation.negotiationframework.rationality.AgentState;
import negotiation.negotiationframework.selection.GreedySelectionModule.GreedySelectionType;
import dima.introspectionbasedagents.services.BasicAgentCompetence;

/**
 * Selection Cores must extenbds this class to be coherent with the roles. They
 * must implements the methods select(..) and use the primitives
 * validateContract and dismissContract to inform of the decisions
 *
 * @author Sylvain Ductor
 *
 * @param <PersonalState>
 * @param <Contract>
 * @param <ActionSpec>
 */
public class SimpleSelectionCore<
Agent extends SimpleNegotiatingAgent<PersonalState, Contract>,
PersonalState extends AgentState,
Contract extends AbstractContractTransition>
extends
BasicAgentCompetence<Agent>
implements SelectionCore<Agent,PersonalState, Contract> {
	private static final long serialVersionUID = -6733096733805658072L;

	GreedySelectionModule selectionModule;

	private final boolean fuseInitiatorNparticipant;//separate creation and destruction in mirror
	private final boolean considerOnWait;//cette variable n'a pas de sens puisque elle amene l'agent a accepter des contrat en imaginant que les siens ont été accepté!!

	public SimpleSelectionCore(
			final boolean fuseInitiatorNparticipant,
			final boolean considerOnWait,
			final GreedySelectionType itType) {
		super();
		this.fuseInitiatorNparticipant = fuseInitiatorNparticipant;
		this.considerOnWait = considerOnWait;
		this.selectionModule=new GreedySelectionModule<PersonalState, Contract>(itType);
	}
	@Override
	public void setMyAgent(final Agent ag)  {
		super.setMyAgent(ag);
		selectionModule.setMyAgent(ag);
	}
	

	//
	// Abstract
	//

	//return contract to validate
	protected Collection<Contract> selection(
			final PersonalState currentState,
			final Collection<Contract> contractsToExplore){
		return this.selectionModule.greedySelection(currentState, contractsToExplore);
	}
	
	//
	//
	//

	@Override
	public void select(
			final ContractTrunk<Contract> given,
			final Collection<Contract> toAccept,
			final Collection<Contract> toReject,
			final Collection<Contract> toPutOnWait) {
		final Collection<Contract> initiatorContractToExplore = given.getInitiatorRequestableContracts();
		final Collection<Contract> participantContractToExplore = given.getParticipantOnWaitContracts();
		final Collection<Contract> initiatorOnWaitContract = given.getInitiatorOnWaitContracts();
		final Collection<Contract> participantAlreadyAccepted = given.getParticipantAlreadyAcceptedContracts();
		final Collection<Contract> rejected = given.getFailedContracts();//			

		// Verification de la consistance
		assert this.getMyAgent().getMyCurrentState().isValid():
			"what the  (1)!!!!!!"+ this.getMyAgent().getMyCurrentState();

		// Mis à jour de l'état si tous les agents ayant été accepter
		// confirmaient :
		final PersonalState currentState = this.getMyAgent()
				.getMyResultingState(
						this.getMyAgent().getMyCurrentState(),
						participantAlreadyAccepted);
		// Verification de la consistance
		assert currentState.isValid():
			"what the  (2)!!!!!!" + currentState+"\n ACCEPTED \n"+participantAlreadyAccepted+"\n GIVEN \n"+given;

		toReject.addAll(initiatorContractToExplore);
		toReject.addAll(participantContractToExplore);
		toPutOnWait.addAll(initiatorOnWaitContract);
		toPutOnWait.addAll(participantAlreadyAccepted);
		assert given.getFailedContracts().isEmpty();

		if (this.fuseInitiatorNparticipant) {

			final List<Contract> contractsToExplore = new ArrayList<Contract>();
			contractsToExplore.addAll(initiatorContractToExplore);
			contractsToExplore.addAll(participantContractToExplore);
			if (this.considerOnWait) {
				contractsToExplore.addAll(initiatorOnWaitContract);
			}

			toAccept.addAll(this.selection(currentState, contractsToExplore));

		} else {

			toAccept.addAll(this.selection(currentState, participantContractToExplore));

			if (this.considerOnWait) {
				initiatorContractToExplore.addAll(initiatorOnWaitContract);
			}
			toAccept.addAll(this.selection(currentState,initiatorContractToExplore));
		}

		toReject.removeAll(toAccept);
//		assert validityVerification(given, toAccept, toReject);
	}
	

	//
	// Primitive
	//

	
	private boolean validityVerification(
			final ContractTrunk<Contract> given,
			final Collection<Contract> accepted,
			final Collection<Contract> notAccepted) {
		//		logMonologue("accepeted "+accepted+" refused "+notAccepted, LogService.onBoth);

		// verification de validit�� d'appel
		final Collection<Contract> test = new ArrayList<Contract>();
		test.addAll(accepted);
		test.addAll(notAccepted);
		//		test.addAll(onWait);

		final Collection<Contract> allContracts = new ArrayList<Contract>();
		allContracts.addAll(given.getInitiatorRequestableContracts());
		allContracts.addAll(given.getParticipantOnWaitContracts());
//		allContracts.addAll(given.getInitiatorOnWaitContracts());
		if (!test.containsAll(allContracts)) {
			throw new RuntimeException(
					"mauvaise implementation du selection core (1)");
		}
		if (!allContracts.containsAll(accepted)) {
			throw new RuntimeException(
					"mauvaise implementation du selection core (2)\n all contracts : "
							+ allContracts
							+ "\n accepted : "+accepted);
		}
		for (final Contract c : notAccepted) {
			if (!allContracts.contains(c) && !given.getOnWaitContracts().contains(c)) {
				throw new RuntimeException(
						"mauvaise implementation du selection core (3)");
			}
			if (accepted.contains(c)) {
				throw new RuntimeException(
						"mauvaise implementation du selection core (4)");
			}
		}
		return true;
	}
}
		//
		//		this.setAnswer(currentState,toAccept, toReject);
		
		
		
		
		
		
		
		
		
		
		

		//		for (Contract c : given.getAllContracts()){
		//			if (c instanceof DestructionOrder){
		////				logMonologue("destructing! "+c+"\n my state is "+getMyAgent().getMyCurrentState());
		//				given.addAcceptation(getIdentifier(), c);
		//			}
		//		}

//		this.select(given.getInitiatorRequestableContracts(),
//				given.getParticipantOnWaitContracts(),
//				given.getInitiatorOnWaitContracts(),
//				given.getParticipantAlreadyAcceptedContracts(),
//				given.getFailedContracts());
		// if (!(this.getMyAgent().getIdentifier() instanceof
		// ResourceIdentifier) &&
		// !given.getParticipantOnWaitContracts().isEmpty())
		// this.logMonologue("AbstractSElectionCore : MyState"+this.getMyAgent().getMyCurrentState()+"\nGiven :"+given+"\n Returned : "+this.returned);


//	/**
//	 * Use setAnswer() method to set the results
//	 * @param initiatorContractToExplore
//	 * @param participantContractToExplore
//	 * @param initiatorOnWaitContract
//	 * @param alreadyAccepted
//	 * @param rejected
//	 */
//	protected void select(final Collection<Contract> initiatorContractToExplore,
//			final Collection<Contract> participantContractToExplore,
//			final Collection<Contract> initiatorOnWaitContract,
//			final Collection<Contract> participantAlreadyAccepted,
//			final Collection<Contract> rejected) {
//
//	}

	// private boolean lastFull=false;

	//	protected void setAnswer(final PersonalState currentState,
	//			final Collection<Contract> accepted,
	//			final Collection<Contract> rejected) {
	//
	//		//		assert	this.validityVerification(accepted, rejected);
	//
	//		// ACCEPTATION
	//		for (final Contract c : accepted) {
	//			this.returned.addContract(c);
	//			this.returned.addAcceptation(this.getMyAgent().getIdentifier(), c);
	//		}
	//
	//		// REFUS
	//		for (final Contract c : rejected) {
	//			this.returned.addContract(c);
	//			this.returned.addRejection(this.getMyAgent().getIdentifier(), c);
	//		}
	//
	////		this.logMonologue("Setting my answer "+this.returned, CommunicationProtocol.log_selectionStep);
	////		this.notify(new IllAnswer<PersonalState, Contract>(this.returned, currentState));
	////		this.logMonologue("After being delaed by relevant services "+this.returned, CommunicationProtocol.log_selectionStep);
	//	}

	//	private void checkImFull(final Collection<Contract> rejected) {
	//		final Collection<Contract> allContracts = new ArrayList<Contract>();
	//		allContracts.addAll(this.given.getInitiatorConsensualContracts());
	//		allContracts.addAll(this.given.getParticipantOnWaitContracts());
	//
	//		// envoie d'info
	//		if (!this.given.getParticipantOnWaitContracts().isEmpty()
	//				&& rejected.containsAll(allContracts)){
	////			logMonologue("fulll!");
	//			this.notify(new ImFull(rejected));
	//		}
	//	}



//protected abstract void select(List<Contract> initiatorContractToExplore,
//		List<Contract> participantContractToExplore,
//		List<Contract> initiatorOnWaitContract,
//		List<Contract> alreadyAccepted, List<Contract> rejected);
