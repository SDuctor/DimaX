// ----------------------------------------------------------------------------
// $Id: Node.java,v 1.4 1997/11/14 16:38:59 schreine Exp schreine $
// components of network
//
// (c) 1997, Wolfgang Schreiner <Wolfgang.Schreiner@risc.uni-linz.ac.at>
// http://www.risc.uni-linz.ac.at/software/daj
// ----------------------------------------------------------------------------
package examples.dcop_old.daj;

import java.io.Serializable;


import darx.DarxCommInterface;
import darx.DarxException;
import darx.DarxTask;
import darx.RemoteTask;
import dima.basicagentcomponents.AgentIdentifier;
import dima.basicagentcomponents.AgentName;
import dima.introspectionbasedagents.annotations.Competence;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.annotations.ProactivityInitialisation;
import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import dima.introspectionbasedagents.shells.BasicCompetentAgent;
import dimaxx.experimentation.ExperimentationResults;
import dimaxx.experimentation.ObservingSelfService;
import examples.dcop_old.algo.BasicAlgorithm;
import examples.dcop_old.api.DcopExperimentationResult;


public class Node extends BasicCompetentAgent {

	// channels to receive messages from respectively send messages to
	private InChannelSet in = new InChannelSet(this);
	private OutChannelSet out = new OutChannelSet(this);
	BasicAlgorithm program; // program that node executes
	private int switches; // number of context switches occurred so far

	@Competence
	ObservingSelfService mySelfObservationService = new ObservingSelfService() {
		private static final long serialVersionUID = 6123670961531677514L;

		@Override
		protected ExperimentationResults generateMyResults() {
			return new DcopExperimentationResult(
					Node.this.getIdentifier(),
					Node.this.getCreationTime(),
					Node.this.program.isStable(),
					Node.this.program.getValue());
		}
	};

	// --------------------------------------------------------------------------
	// create node with `prog` to execute in network `net`
	// --------------------------------------------------------------------------
	public Node(String name, BasicAlgorithm prog) throws CompetenceException {
		super(new NodeIdentifier(((BasicAlgorithm) prog).getID()));
		program = prog;
		program.setNode(this);
		switches = 0;
		assert getIdentifier().getAsInt().equals(((BasicAlgorithm) prog).getID());
	}

	// --------------------------------------------------------------------------
	// increase number of switches
	// --------------------------------------------------------------------------
	public void incSwitches() {
		logMonologue("hey", LogService.onScreen);
		switches++;
	}

	// --------------------------------------------------------------------------
	// get number of switches
	// --------------------------------------------------------------------------
	public int getSwitches() {
		return switches;
	}

	// --------------------------------------------------------------------------
	// executed when node starts execution
	// --------------------------------------------------------------------------
	@ProactivityInitialisation
	public void init(){
		program.initialisation();
	}
	
	@StepComposant
	public void exec() {
		program.main();
	}
		
	@MessageHandler
	public void receiveDcopMessage(DCOPMessage m){
//		assert m.getReceiver().equals(getIdentifier());
		((Channel)in.getChannel(m.getSender())).addMessage(m);
	}	
	// --------------------------------------------------------------------------
	// add `channel` to set of inchannels
	// --------------------------------------------------------------------------
	public void inChannel(Channel channel) {
		assert ((BasicAlgorithm)this.program).view.varMap.get(channel.getNeighbor())!=null;
		in.addChannel(channel);
	}

	// --------------------------------------------------------------------------
	// add `channel` to set of outchannels
	// --------------------------------------------------------------------------
	public void outChannel(Channel channel) {
		assert ((BasicAlgorithm)this.program).view.varMap.get(channel.getNeighbor())!=null;
		out.addChannel(channel);
	}

	// --------------------------------------------------------------------------
	// return visual representation of node
	// --------------------------------------------------------------------------
//	@deprecated
//	public Integer getID(){
//		return ((BasicAlgorithm) getProgram()).getID();
//	}
	
	public NodeIdentifier getIdentifier(){
		return (NodeIdentifier) super.getIdentifier();
	}

	public NodeIdentifier getId(){
		return (NodeIdentifier) super.getId();
	}
	// --------------------------------------------------------------------------
	// status text displayed for node
	// --------------------------------------------------------------------------
	public String getText() {
		return program.getText();
	}

	// --------------------------------------------------------------------------
	// program executed by node
	// --------------------------------------------------------------------------
	public Program getProgram() {
		return program;
	}

	// --------------------------------------------------------------------------
	// set of in channels
	// --------------------------------------------------------------------------
	public InChannelSet getIn() {
		return in;
	}

	// --------------------------------------------------------------------------
	// set of out channels
	// --------------------------------------------------------------------------
	public OutChannelSet getOut() {
		return out;
	}
}

//
	// Subclass
	//


//	class DarxNode extends DarxTask{
//
//		protected DarxNode() {
//			super(getID().toString());
//		}
//
//		protected  DarxCommInterface comm;
//
//		public DarxCommInterface getComm() {
//			return comm;
//		}
//
//		//
//		// Primitive
//		//
//		
//		public void sendAsyncMessage(Integer i, final Message m) {
//
//			final AgentIdentifier id = new AgentName(i.toString());
//			RemoteTask remote = null;
//			try {
//				remote = findTask(id.toString());
//			}
//			catch(final DarxException e) {
//				System.out.println("Getting " + id + " from nameserver failed : " + e);
//				return;
//			}
//
//			if(remote != null)
//				getComm().sendAsyncMessage(remote, (Serializable) m);
//			else
//				throw new RuntimeException(this+" Echec de l'envoi du message"+m);
//		}
//		
////		public Object sendSyncMessage(Integer i, final Message m) {
////
////			final AgentIdentifier id = new AgentName(i.toString());
////			RemoteTask remote = null;
////			try {
////				remote = findTask(id.toString());
////			}
////			catch(final DarxException e) {
////				System.out.println("Getting " + id + " from nameserver failed : " + e);
////				return null;
////			}
////
////			if(remote != null)
////				return getComm().sendSyncMessage(remote, (Serializable) m);
////			else
////				throw new RuntimeException(this+" Echec de l'envoi du message"+m);
////
////		}	
//		
//		/*
//		 * Message Handling
//		 */
//
//		/**
//		 * Put the message
//		 * received from DarX in the agent mailbox
//		 *
//		 * @param msg
//		 *            the message, that should be cast in Message
//		 * @see Message
//		 */
//		@Override
//		public void receiveAsyncMessage(final Object msg) {
//				if (msg instanceof Message)
//					((Channel) in.getChannel(((Message) msg).getSender())).addMessage((Message) msg); 
//				else
//					LogService.writeException(this, msg+" is not a message : can not be added to mail box!");
//		}
//
////		/**
////		 * UNIMPLEMENTED : Execute the task and return the results
////		 *
////		 * @param msg
////		 *            the message, that should be cast in Message
////		 * @see Message
////		 */
////		@Override
////		public Serializable receiveSyncMessage(final Object msg) {
////			this.receiveAsyncMessage(msg);
////			return null;
////		}
////		
//}
