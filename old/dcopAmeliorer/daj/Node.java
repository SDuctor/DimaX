// ----------------------------------------------------------------------------
// $Id: Node.java,v 1.4 1997/11/14 16:38:59 schreine Exp schreine $
// components of network
//
// (c) 1997, Wolfgang Schreiner <Wolfgang.Schreiner@risc.uni-linz.ac.at>
// http://www.risc.uni-linz.ac.at/software/daj
// ----------------------------------------------------------------------------
package examples.dcopAmeliorer.daj;

import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.annotations.ProactivityInitialisation;
import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.shells.BasicCompetentAgent;
import examples.dcopAmeliorer.algo.BasicAlgorithm;


public class Node extends BasicCompetentAgent {

	/**
	 *
	 */
	private static final long serialVersionUID = -1745198966122356287L;
	// channels to receive messages from respectively send messages to
	private final InChannelSet in = new InChannelSet();
	private final OutChannelSet out = new OutChannelSet();
	private Program program; // program that node executes
	private int switches; // number of context switches occurred so far

	// --------------------------------------------------------------------------
	// create node with `prog` to execute in network `net`
	// --------------------------------------------------------------------------
	public Node(final Program prog) throws CompetenceException {
		super(new NodeIdentifier(((BasicAlgorithm)prog).getID()));
		this.init(prog);
	}

	// --------------------------------------------------------------------------
	// initialize node with `prog` to execute in network `net`
	// --------------------------------------------------------------------------
	private void init(final Program prog) {
		this.program = prog;
		this.program.setNode(this);
		this.switches = 0;
	}


	@Override
	public NodeIdentifier getIdentifier() {
		return (NodeIdentifier) super.getIdentifier();
	}



	// --------------------------------------------------------------------------
	// increase number of switches
	// --------------------------------------------------------------------------
	public void incSwitches() {
		this.switches++;
	}

	// --------------------------------------------------------------------------
	// get number of switches
	// --------------------------------------------------------------------------
	public int getSwitches() {
		return this.switches;
	}

	//
	// Behavior
	//

	@ProactivityInitialisation
	public void initialisation(){
		((BasicAlgorithm)this.program).initialisation();
	}

	// --------------------------------------------------------------------------
	// executed when node starts execution
	// --------------------------------------------------------------------------
	@StepComposant
	public void run() {
		this.program.main();
	}

	@MessageHandler
	public void receiveMessage(final DcopMessage m){
		this.in.getChannel(m.getSender().asInt()).write(m);
	}
	// --------------------------------------------------------------------------
	// add `channel` to set of inchannels
	// --------------------------------------------------------------------------
	public void inChannel(final Channel channel) {
		this.in.addChannel(channel);
	}

	// --------------------------------------------------------------------------
	// add `channel` to set of outchannels
	// --------------------------------------------------------------------------
	public void outChannel(final Channel channel) {
		this.out.addChannel(channel);
	}

	// --------------------------------------------------------------------------
	// status text displayed for node
	// --------------------------------------------------------------------------
	public String getText() {
		return this.program.getText();
	}

	// --------------------------------------------------------------------------
	// program executed by node
	// --------------------------------------------------------------------------
	public Program getProgram() {
		return this.program;
	}

	// --------------------------------------------------------------------------
	// set of in channels
	// --------------------------------------------------------------------------
	public InChannelSet getIn() {
		return this.in;
	}

	// --------------------------------------------------------------------------
	// set of out channels
	// --------------------------------------------------------------------------
	public OutChannelSet getOut() {
		return this.out;
	}
}