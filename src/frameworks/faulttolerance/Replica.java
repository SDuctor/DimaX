package frameworks.faulttolerance;

import java.util.Random;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.Competence;
import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.information.ObservationService;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import frameworks.experimentation.ExperimentationResults;
import frameworks.experimentation.ObservingSelfService;
import frameworks.faulttolerance.experimentation.ReplicationExperimentationParameters;
import frameworks.faulttolerance.experimentation.ReplicationResultAgent;
import frameworks.faulttolerance.faulsimulation.FaultObservationService;
import frameworks.faulttolerance.negotiatingagent.ReplicaState;
import frameworks.faulttolerance.negotiatingagent.ReplicationCandidature;
import frameworks.negotiation.SimpleNegotiatingAgent;
import frameworks.negotiation.contracts.AbstractContractTransition;
import frameworks.negotiation.protocoles.AbstractCommunicationProtocol;
import frameworks.negotiation.protocoles.AbstractCommunicationProtocol.ProposerCore;
import frameworks.negotiation.protocoles.AbstractCommunicationProtocol.SelectionCore;
import frameworks.negotiation.protocoles.status.StatusAgent;
import frameworks.negotiation.rationality.RationalCore;

public abstract class Replica<Contract extends AbstractContractTransition>
extends SimpleNegotiatingAgent<ReplicaState, Contract> {
	private static final long serialVersionUID = 4986143017976368579L;

	//
	// Fields
	//

	private final boolean dynamicCrticity;

	//	public boolean replicate = true;

	@Competence
	ObservingSelfService mySelfObservationService = new ObservingSelfService() {

		/**
		 *
		 */
		private static final long serialVersionUID = 6123670961531677514L;

		@Override
		protected ExperimentationResults generateMyResults() {
			ReplicationResultAgent myInfo;
			if (Replica.this instanceof StatusAgent) {
				myInfo = new ReplicationResultAgent(
						Replica.this.getMyCurrentState(),
						Replica.this.getCreationTime(),
						((StatusAgent) Replica.this).getMyStatus());
			} else {
				myInfo = new ReplicationResultAgent(
						Replica.this.getMyCurrentState(),
						Replica.this.getCreationTime());
			}
			return myInfo;
		}
	};

//	@Competence
//	FaultObservationService myFaultAwareService =
//	new FaultObservationService() {
//
//		public ReplicationCandidature generateDestructionContract(final AgentIdentifier id){
//			return Replica.this.generateDestructionContract(id);
//		}
//
//		@Override
//		public void endSimulation() {
//			mySelfObservationService.endSimulation();			
//		}
//	};

	//
	// Constructor
	//



	public Replica(
			final AgentIdentifier id,
			final ReplicaState myState,
			final RationalCore myRationality,
			final SelectionCore participantCore,
			final ProposerCore proposerCore,
			final ObservationService myInformation,
			final AbstractCommunicationProtocol protocol,
			final boolean dynamicCriticity)
					throws CompetenceException {
		super(id, myState, myRationality, participantCore, proposerCore, myInformation, protocol);
		this.myStateType = ReplicaState.class;
		this.dynamicCrticity=dynamicCriticity;
	}


	public abstract ReplicationCandidature generateDestructionContract(final AgentIdentifier id);
	public abstract ReplicationCandidature generateCreationContract(final AgentIdentifier id);
	
	@StepComposant(ticker=ReplicationExperimentationParameters._criticity_update_frequency)
	public void updateMyCriticity() {
		if (this.dynamicCrticity){
			final Random r = new Random();
			if (r.nextDouble() <= ReplicationExperimentationParameters._criticityVariationProba) {// On
				// met a jour
				final int signe = r.nextBoolean() ? 1 : -1;
				final Double newCriticity = Math
						.min(1.,
								Math.max(
										ReplicationExperimentationParameters._criticityMin,
										this.getMyCurrentState().getMyCriticity()
										+ signe
										* r.nextDouble()
										* ReplicationExperimentationParameters._criticityVariationAmplitude));
				this.logWarning("Updating my criticity", LogService.onNone);
				this.setNewState(
						new ReplicaState(
								this.getMyCurrentState(),
								newCriticity));
			}
		}
	}

	//allow to continue to receive messages
	@Override
	public void tryToResumeActivity(){
		super.tryToResumeActivity();
		this.mySelfObservationService.tryToResumeActivity();
	}
}





//
// Accessors
//

//	public void setNewState(final ReplicaState s) {
//		for (HostState h : s.getMyHosts()){
//			getMyInformation().add(h);
//		}
//		super.setNewState(s);
//	}

//	public boolean IReplicate() {
//		return this.replicate;
//	}
//
//	public void setIReplicate(final boolean replicate) {
//		this.replicate = replicate;
//	}
//
//	@StepComposant()
//	@Transient
//	public boolean setReplication() {
//		if (this.getMyInformation().getKnownAgents().isEmpty())
//			this.replicate = false;
//
//		// logMonologue("agents i know : "+this.getKnownAgents());
//		// if (IReplicate())
//		// logMonologue("yeeeeeeeeeeaaaaaaaaaaaahhhhhhhhhhhhh      iii replicatre!!!!!!!!!!!!!!!!!!!!!!"+((CandidatureReplicaCoreWithStatus)myCore).getMyStatus());
//
//		return true;
//	}

//	@Override
//	public void setNewState(final ReplicaState s) {
//		super.setNewState(s);
//	}




/*
 *
 */

//this.setNewState(new ReplicaState(id, criticity, procCharge, memCharge,new HashSet<HostState>(),-1));
//	@Override
//	public ContractTrunk<ReplicationCandidature> select(
//			final ContractTrunk<ReplicationCandidature> cs) {
//
//		if (this.myCore instanceof CandidatureReplicaCoreWithMinInfo)
//			((CandidatureReplicaCoreWithMinInfo) this.myCore)
//					.setMinKnowRelia(cs);
//
//		return super.select(cs);
//	}

//	@Override
//	public void execute(final ReplicationCandidature c) {
//		// notify(new ReliabilityUpdate());
//		ReplicaState previousState = getMyCurrentState();
//		super.execute(c);
////		logMonologue("i have been replicated by "+c.getResource());// : \n previous stae : "+previousState+"\n new state : "+getMyCurrentState());
//	}

//	@MessageHandler
//	@NotificationEnvelope(SimpleObservationService.informationObservationKey)
//	public <Info extends Information> void receiveInformation(
//			final NotificationMessage<Information> o) {
//		logMonologue("yophoi");
//		if (o.getNotification() instanceof HostState
//				&& getMyCurrentState().getMyResourceIdentifiers().contains(
//						((HostState) o.getNotification()).getMyAgentIdentifier())){
//			ReplicaState r = new ReplicaState(getMyCurrentState(), (HostState) o.getNotification(), getMyCurrentState().getCreationTime());
//			r = new ReplicaState(r, (HostState) o.getNotification(), getMyCurrentState().getCreationTime());
//			setNewState(r);
//		}
//	}

// }
// @Override
// protected EndInfo getMyEndNotif() {
// return new AgentEndInfo(getMyCurrentState(),getCreationTime());
// }



//	public Double getCharge(final ResourceIdentifier r)  {
//		try {
//			return this.getMyInformation().getInformation(HostState.class, r).getMyCharge();
//		} catch (final NoInformationAvailableException e) {
//			throw new RuntimeException("muahahahaha tant pis pour ta gueule!!!!!!!!!!!!!!!!!!!!!!");
//		}
//	}


/*
 *
 *
 *
 *
 *
 *
 *
 */

//
// public class ReliabilityUpdate implements Serializable{
//
// /**
// *
// */
// private static final long serialVersionUID = -1158730596156778825L;
// private final AgentIdentifier id;
// private final Double relia;
//
//
//
// public ReliabilityUpdate() {
// super();
// this.id = NegotiatingReplica.this.getIdentifier();
// this.relia = NegotiatingReplica.this.getMyCurrentState().getMyReliability();
// if (this.relia==null) throw new RuntimeException("aaaaaaaaaaa");
// }
//
// public Double getRelia() {
// return this.relia;
// }
//
// public AgentIdentifier getIdentifier() {
// return this.id;
// }
//
// @Override
// public String toString(){
// return "identif "+this.id+", relia "+this.relia;
// }
// }

// public AgentStateStatus getMyStateStatus(){
// return CandidatureReplicaCore.this.getStatus(this);
// }

// @Override
// public void reset() {
// this.myReplicas.clear();
// this.resetUptime();
// }

//
// public boolean Iaccept(final ReplicaState s, final ReplicationCandidature c)
// {
// return super.Iaccept(s,c) &&
// (
// (s.getMyReliability()>minKnownReliability &&
// getMyResultingState(s,c).getMyReliability()>minKnownReliability)
// ||
// (s.getMyReliability()<minKnownReliability &&
// getMyResultingState(s,c).getMyReliability()>s.getMyReliability())
// );
// }
// public boolean Iaccept(final ReplicaState s, final
// Collection<ReplicationCandidature> c) {
// return super.Iaccept(s,c) &&
// (
// (s.getMyReliability()>=minKnownReliability &&
// getMyResultingState(s,c).getMyReliability()>minKnownReliability)
// ||
// (s.getMyReliability()<minKnownReliability &&
// getMyResultingState(s,c).getMyReliability()>s.getMyReliability())
// );
// }

// private Double computeDisponibility(
// final Collection<ResourceIdentifier> replicas) {
// // final Collection<Double> hosts = new ArrayList<Double>();
// // for (final ResourceIdentifier r : replicas){
// // try{
// // hosts.add(ReplicaCore.this.specifs.get(r).getDisponibility());
// // } catch (NullPointerException e){
// //
// logMonologue(specifs+"\n --->"+getMyAgent().getMyCurrentState().myReplicas);
// // throw e;
// // }
// // }
//
// return getMyAgent().getDisponibility(replicas);
// }
// @Override
// public ReplicaState clone(){
// final Set<ResourceIdentifier> reps = new HashSet<ResourceIdentifier>();
// reps.addAll(this.getMyReplicas());
// return new ReplicaState(this, reps);
// }

// boolean iveSarted=false;
//
// Behavior
//

/*
 * Notification
 */

/*
 *
 */

/*
 *
 */

// @StepComposant(ticker=1000)
// @Transient
// public boolean start(){
// appliHasStarted=true;
// return true;
// }

/*
 *
 */

/*
 *
 */

//
// /*
// *
// *
// *
// *
// *
// *
// */
//
//
// public class AgentInfo extends GimaObject
// implements Comparable<AgentInfo>{
//
// /**
// *
// */
// private static final long serialVersionUID = -5530129809027522515L;
// public final AgentIdentifier id;
// public final Double reliability;
// public final long uptime;
// public final boolean maxReplication;
// public final Double myProcCharge;
// public final Double myMemCharge;
// public final AgentStateStatus myStatus;
// public final Double criticity;
// public final Boolean iAmDead;
//
//
//
//
// public AgentInfo() {
// super();
// this.id = NegotiatingReplica.this.getIdentifier();
// this.reliability =
// NegotiatingReplica.this.getMyCurrentState().getMyReliability();
// this.uptime = NegotiatingReplica.this.getMyCurrentState().getUptime();
// this.maxReplication =
// NegotiatingReplica.this.getMyCurrentState().getMyReplicas().containsAll(NegotiatingReplica.this.getKnownAgents());
// this.myProcCharge=NegotiatingReplica.this.getMyCurrentState().getMyProcCharge();
// this.myMemCharge=NegotiatingReplica.this.getMyCurrentState().getMyMemCharge();
// this.myStatus =
// NegotiatingReplica.this.getMyCurrentState().getMyStateStatus();
// this.criticity=NegotiatingReplica.this.getMyCurrentState().getMyCriticity();
// this.iAmDead=NegotiatingReplica.this.iAMDead;
// }
//
//
//
// public AgentIdentifier getMyAgentIdentifier() {
// return this.id;
// }
//
//
// @Override
// public int hashCode(){
// return this.getMyAgentIdentifier().hashCode();
// }
//
// @Override
// public boolean equals(final Object o){
// if (o instanceof AgentInfo) {
// final AgentInfo that = (AgentInfo) o;
// return that.getMyAgentIdentifier().equals(this.getMyAgentIdentifier());
// } else
// return false;
// }
//
//
//
// public Double getMyReliability() {
// return this.reliability;
// }
//
//
//
// @Override
// public int compareTo(final AgentInfo that) {
// if (this.maxReplication==true && that.maxReplication==true)
// return 0;
// else if (this.maxReplication==true && that.maxReplication==false)
// return 1;
// else if (this.maxReplication==false && that.maxReplication==true)
// return -1;
// else
// return this.reliability.compareTo(that.reliability);
// }
//
//
//
// public Double getMyProcCharge() {
// return this.myProcCharge;
// }
//
//
//
// public Double getMyMemCharge() {
// return this.myMemCharge;
// }
// }