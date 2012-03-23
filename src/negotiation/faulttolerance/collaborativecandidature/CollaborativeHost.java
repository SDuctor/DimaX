package negotiation.faulttolerance.collaborativecandidature;

import negotiation.faulttolerance.experimentation.ReplicationResultHost;
import negotiation.faulttolerance.faulsimulation.FaultObservationService;
import negotiation.faulttolerance.faulsimulation.HostDisponibilityComputer;
import negotiation.faulttolerance.negotiatingagent.HostCore;
import negotiation.faulttolerance.negotiatingagent.HostState;
import negotiation.faulttolerance.negotiatingagent.ReplicationCandidature;
import negotiation.faulttolerance.negotiatingagent.ReplicationSpecification;
import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.contracts.InformedCandidature;
import negotiation.negotiationframework.contracts.ResourceIdentifier;
import negotiation.negotiationframework.protocoles.collaborative.InformedCandidatureRationality;
import negotiation.negotiationframework.protocoles.collaborative.OneDeciderCommunicationProtocol;
import negotiation.negotiationframework.protocoles.collaborative.ResourceInformedCandidatureContractTrunk;
import negotiation.negotiationframework.protocoles.collaborative.ResourceInformedProposerCore;
import negotiation.negotiationframework.protocoles.collaborative.ResourceInformedSelectionCore;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.Competence;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.information.SimpleObservationService;
import framework.experimentation.ExperimentationResults;
import framework.experimentation.ObservingSelfService;

public class CollaborativeHost
extends	SimpleNegotiatingAgent<ReplicationSpecification, HostState, InformedCandidature<ReplicationCandidature,ReplicationSpecification>>
{
	private static final long serialVersionUID = -8478683967125467116L;

	//
	// Fields
	//

	@Competence
	ObservingSelfService mySelfObservationService = new ObservingSelfService() {

		/**
		 *
		 */
		private static final long serialVersionUID = -6008018665463786541L;

		@Override
		protected ExperimentationResults generateMyResults() {
			return new ReplicationResultHost(
					CollaborativeHost.this.getMyCurrentState(),
					CollaborativeHost.this.getCreationTime());
		}
	};

	@Competence
	public
	FaultObservationService<ReplicationSpecification, HostState,  InformedCandidature<ReplicationCandidature,ReplicationSpecification>> myFaultAwareService =
	new FaultObservationService<ReplicationSpecification, HostState,  InformedCandidature<ReplicationCandidature,ReplicationSpecification>>() {

		/**
		 *
		 */
		private static final long serialVersionUID = -5530153574167669156L;

		@Override
		protected void resetMyState() {
			CollaborativeHost.this.setNewState(new HostState((ResourceIdentifier) this.getIdentifier(),
					CollaborativeHost.this.getMyCurrentState().getProcChargeMax(),
					CollaborativeHost.this.getMyCurrentState().getMemChargeMax(),
					CollaborativeHost.this.getMyCurrentState().getLambda(),this.getMyAgent().nextStateCounter));
			//			this.resetMyUptime();
		}

		@Override
		protected void resetMyUptime() {

			assert 1<0:"Host.this.getMyCurrentState().resetUptime()";
		}

	};

	//
	// Constructor
	//


	public CollaborativeHost(
			final ResourceIdentifier myId,
			final double hostMaxProc, final double hostMaxMem,
			final double lambda,
			final String socialWelfare,
			final HostDisponibilityComputer myDispoInfo)
					throws CompetenceException {
		super(
				myId,
				new HostState(myId,hostMaxProc, hostMaxMem,lambda,-1),
				new InformedCandidatureRationality(new HostCore(socialWelfare),false),
				new ResourceInformedSelectionCore(){
					/**
					 * 
					 */
					private static final long serialVersionUID = -1578866978817500691L;

					@Override
					protected InformedCandidature generateDestructionContract(final AgentIdentifier id) {
						return new InformedCandidature(new ReplicationCandidature(myId,id,false,false));
					}
				},//new GreedyBasicSelectionCore(true, false),//
				new ResourceInformedProposerCore(),
				new SimpleObservationService(),
				new OneDeciderCommunicationProtocol( new ResourceInformedCandidatureContractTrunk(), true) );
		this.getMyProtocol().getContracts().setMyAgent(this);
	}
}
