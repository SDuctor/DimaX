package negotiation.negotiationframework.protocoles;

import java.util.ArrayList;

import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.AbstractContractTransition;
import negotiation.negotiationframework.contracts.ContractTrunk;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;

public class ReverseCFPProtocol <
ActionSpec extends AbstractActionSpecification,
State extends ActionSpec,
Contract extends AbstractContractTransition<ActionSpec>>
extends AbstractCommunicationProtocol<ActionSpec, State, Contract>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4843844714006615468L;

	public ReverseCFPProtocol(
			final SimpleNegotiatingAgent<ActionSpec, State, Contract> a,
			final ContractTrunk<Contract, ActionSpec, State> contracts)
					throws UnrespectedCompetenceSyntaxException {
		super(a, contracts);
	}

	@Override
	protected void answerAccepted(final ArrayList<Contract> toAccept) {
		final ArrayList<Contract> initiator = new ArrayList<Contract>();
		final ArrayList<Contract> participant = new ArrayList<Contract>();

		this.separateInitiator(toAccept, initiator, participant);

		assert AbstractCommunicationProtocol.allRequestable(initiator, this.getContracts());

		this.confirmContract(initiator, Receivers.NotInitiatingParticipant);
		this.acceptContract(participant, Receivers.Initiator);
	}

	@Override
	protected void answerRejected(final ArrayList<Contract> toReject) {
		final ArrayList<Contract> initiator = new ArrayList<Contract>();
		final ArrayList<Contract> participant = new ArrayList<Contract>();

		this.cancelContract(initiator, Receivers.NotInitiatingParticipant);
		this.rejectContract(participant, Receivers.Initiator);
	}

	@Override
	protected void putOnWait(final ArrayList<Contract> toPutOnWait) {
		// Do nothing
	}

	/*
	 * 
	 */

	private void separateInitiator(final ArrayList<Contract> all, final ArrayList<Contract> initiator, final ArrayList<Contract> participant){
		assert initiator.isEmpty();
		assert participant.isEmpty();

		for (final Contract a : all) {
			if (a.getInitiator().equals(this.getMyAgent().getIdentifier())) {
				initiator.add(a);
			} else {
				participant.add(a);
			}
		}
	}

}







//// @role(NegotiationParticipant.class)
//@StepComposant(ticker = ReplicationExperimentationProtocol._timeToCollect)
//void answer() {
//	if (isActive())
//		if (!this.getContracts().isEmpty()) {
//
//			//
//			// Selecting contracts
//			//
//
//			// logMonologue("What do I have?"+contracts.getOnWaitContracts());
//			final ContractTrunk<Contract, ActionSpec, State> selectedContracts = this
//					.getMyAgent().getMySelectionCore().select(this.getContracts());
//
//			//
//			// Cleaning contracts
//			//
//
//			this.cleanContracts();
//			//				final Collection<Contract> allSelectedContracts = selectedContracts
//			//						.getAllContracts();
//			//				for (final Contract c : this.cleaned)
//			//					try {
//			//						if (allSelectedContracts.contains(c))
//			//							throw new RuntimeException(
//			//									"can not negotiate an expired contracts!!");
//			//					} catch (final RuntimeException e) {
//			//						// c'est bon c'��tait pas le mm
//			//					}
//			this.cleaned.clear();
//			// logMonologue("What I select?"+(answers.isEmpty()?"NUTTIN' >=[":answers));
//			// this.contracts.clearRejected();
//			// contracts.removeAll(expired);
//
//			//
//			// Answering
//			//
//			for (final Contract contract : selectedContracts.getContractsAcceptedBy(this.getMyAgent().getIdentifier()))
//				if (contract.getInitiator().equals(this.getMyAgent().getIdentifier())) {// if im initiator
//					if (this.getContracts().getRequestableContracts().contains(contract)){//if the contract is consensual
//						this.requestContract(contract);
//					}
//				} else
//					this.acceptContract(contract);
//			for (final Contract contract : selectedContracts.getContractsRejectedBy(this.getMyAgent().getIdentifier()))
//				if (contract.getInitiator().equals(this.getMyAgent().getIdentifier())){
//					this.cancelContract(contract);
//				}else{// Participant Answering
//					this.rejectContract(contract);
//				}
//		}
//}