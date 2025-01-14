package vieux.dcop_old.api;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;


import org.jdom.JDOMException;

import vieux.dcopAmeliorer.algo.Algorithm;
import vieux.dcopAmeliorer.algo.BasicAlgorithm;
import vieux.dcopAmeliorer.algo.korig.AlgoKOptOriginal;
import vieux.dcopAmeliorer.algo.topt.AlgoKOptAPO;
import vieux.dcopAmeliorer.algo.topt.AlgoTOptAPO;
import vieux.dcopAmeliorer.daj.Channel;
import vieux.dcopAmeliorer.daj.Node;
import vieux.dcopAmeliorer.dcop.Constraint;
import vieux.dcopAmeliorer.dcop.Graph;
import vieux.dcopAmeliorer.dcop.Variable;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basicagentcomponents.AgentName;
import dima.introspectionbasedagents.kernel.BasicCompetentAgent;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.deployment.server.HostIdentifier;
import dima.introspectionbasedagents.services.launch.APIAgent.APILauncherModule;
import frameworks.experimentation.ExperimentationParameters;
import frameworks.experimentation.Experimentator;
import frameworks.experimentation.IfailedException;
import frameworks.experimentation.Laborantin;
import frameworks.experimentation.Laborantin.NotEnoughMachinesException;

public class DCOPExperimentationParameters extends ExperimentationParameters<Laborantin>{

	//
	// Fields
	//

	/**
	 *
	 */
	private static final long serialVersionUID = 3047754057128427836L;

	String filename;

	Algorithm algo;
	int grouping;

	public static Graph g;
	public static HashMap<Integer, Node> nodeMap;


	//
	// Constructor
	//

	public DCOPExperimentationParameters(
			final AgentIdentifier experimentatorId,
			final String resultPath,
			final String filename,
			final int grouping,
			final String algo) {
		super(experimentatorId, resultPath);
		this.filename = filename;
		this.grouping = grouping;

		this.algo = Algorithm.KOPTAPO;
		if (algo.equalsIgnoreCase("TOPT")) {
			this.algo = Algorithm.TOPTAPO;
		} else if (algo.equalsIgnoreCase("KOriginal")) {
			this.algo = Algorithm.KOPTORIG;
		}
	}

	//
	// Accessors
	//

	@Override
	public Integer getMaxNumberOfAgent(final HostIdentifier h) {
		return Integer.MAX_VALUE;
	}

	//
	// Protocol
	//

	@Override
	public LinkedList<ExperimentationParameters<Laborantin>> generateSimulation() {
		final LinkedList<ExperimentationParameters<Laborantin>> expPs = new LinkedList<ExperimentationParameters<Laborantin>>();
		expPs.add(new DCOPExperimentationParameters(this.experimentatorId, this.filename, "conf/1.dcop", 1, "TOPT"));
		return expPs;
	}

	//
	// Instanciation
	//

	@Override
	public void initiateParameters() {

		DCOPExperimentationParameters.g = new Graph(this.filename);
		DCOPExperimentationParameters.nodeMap = new HashMap<Integer, Node>();
	}



	@Override
	protected Collection<? extends BasicCompetentAgent> instanciateAgents() throws CompetenceException {

		for (final Variable v : DCOPExperimentationParameters.g.varMap.values()) {
			final Node node = new Node(this.getAlgo(v));
			DCOPExperimentationParameters.nodeMap.put(v.id, node);
			//			Channel.link(controller, node);
		}

		for (final Constraint c : DCOPExperimentationParameters.g.conList) {
			final Node first = DCOPExperimentationParameters.nodeMap.get(c.first.id);
			final Node second = DCOPExperimentationParameters.nodeMap.get(c.second.id);
			Channel.link(first, second);
		}

		for (final Node ag : DCOPExperimentationParameters.nodeMap.values()){
			((GlobalDCOPObservation)this.getMyAgent().getObservingService()).appIsStable.put(ag.getIdentifier(), false);
		}

		assert this.verification();
		return DCOPExperimentationParameters.nodeMap.values();
	}


	private BasicAlgorithm getAlgo(final Variable v) {
		//		return new AlgoMGM1(v);
		switch(this.algo){
		case TOPTAPO:
			return new AlgoTOptAPO(v, this.grouping);
		case KOPTAPO:
			return new AlgoKOptAPO(v, this.grouping);
		case KOPTORIG:
			return new AlgoKOptOriginal(v, this.grouping);
		default: return null;
		}
	}

	public boolean verification(){

		for (final Constraint c : DCOPExperimentationParameters.g.conList){
			assert Graph.constraintExist(DCOPExperimentationParameters.g, c.first.id, c.second.id);

			assert ((BasicAlgorithm) DCOPExperimentationParameters.nodeMap.get(c.first.id).getProgram()).view.varMap.get(c.second.id)!=null;
			assert ((BasicAlgorithm) DCOPExperimentationParameters.nodeMap.get(c.second.id).getProgram()).view.varMap.get(c.first.id)!=null;

			assert DCOPExperimentationParameters.nodeMap.get(c.first.id).getIn().getChannel(c.second.id)!=null;
			assert DCOPExperimentationParameters.nodeMap.get(c.second.id).getIn().getChannel(c.first.id)!=null;
			assert DCOPExperimentationParameters.nodeMap.get(c.first.id).getOut().getChannel(c.second.id)!=null;
			assert DCOPExperimentationParameters.nodeMap.get(c.second.id).getOut().getChannel(c.first.id)!=null;
		}

		for (final BasicCompetentAgent a : DCOPExperimentationParameters.nodeMap.values()){
			final Node n = (Node) a;
			for (final Channel c : n.getIn().getChannels()){
				assert c.getOwner().equals(n);
				assert Graph.constraintExist(DCOPExperimentationParameters.g, c.getOwner().getIdentifier().asInt(), c.getNeighbor().asInt());
				assert ((BasicAlgorithm)n.getProgram()).view.varMap.containsKey(c.getNeighbor().asInt());
			}
			for (final Channel c : n.getOut().getChannels()){
				assert c.getOwner().equals(n);
				assert Graph.constraintExist(DCOPExperimentationParameters.g, c.getOwner().getIdentifier().asInt(), c.getNeighbor().asInt());
				assert ((BasicAlgorithm)n.getProgram()).view.varMap.containsKey(c.getNeighbor().asInt());
			}
		}

		return true;
	}

	//
	// Main
	//

	public static void main(final String[] args) throws CompetenceException, IllegalArgumentException, IllegalAccessException, JDOMException, IOException, NotEnoughMachinesException, IfailedException{
		final Experimentator exp = new Experimentator(new DCOPExperimentationParameters(new AgentName("ziDcopExperimentator"),"dcopResult","1.dcop", 1, "TOPT"), null, 1);
		exp.run(args);
	}

	@Override
	public Laborantin createLaborantin(final APILauncherModule api) throws CompetenceException,
	IfailedException, NotEnoughMachinesException {
		return new Laborantin(this,new GlobalDCOPObservation(this),api);
	}

	@Override
	public int compareTo(final Object arg0) {
		return 0;
	}

}
