package negotiation.dcopframework.exec;

import java.util.ArrayList;
import java.util.HashMap;

import negotiation.dcopframework.algo.Algorithm;
import negotiation.dcopframework.daj.Node;
import negotiation.dcopframework.dcop.Graph;
import negotiation.dcopframework.dcop.Helper;
import dima.introspectionbasedagents.services.DuplicateCompetenceException;
import dima.introspectionbasedagents.services.UnInstanciedCompetenceException;
import dima.introspectionbasedagents.shells.APIAgent;

public class DCOPLaborantin extends APIAgent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6744985687665461189L;
	private final Graph g;
	private final Algorithm algo;
	private HashMap<Integer, Node> nodeMap;

	private final  int cycles;

	private final  int numberMessages;
	private final  int sizeofMessages;
	public  int numberEval;
	private final  int numberConflicts;
	private final  int wastedCycles;

	private final  int[] quality;
	private final  int[] msgsize;
	private final  int[] nummsg;

	private final  int[] nEval;
	private final  int[] nConflicts;
	private final  int[] wCycles;

	private final  int grouping;

	private final  boolean s;
	private final int ws;

	private  int activetnodeT;
	private  double totalLockReq;
	private  int[] groupK;

	public ArrayList<Stats> allstats;

	public DCOPLaborantin(final String newId,
			final String filename, final int cycles, final int kort, final Algorithm a, final boolean isWin, final boolean s, final int ws)
					throws UnInstanciedCompetenceException,	DuplicateCompetenceException {
		super(newId);

		this.numberMessages = 0;
		this.sizeofMessages = 0;

		this.numberEval = 0;
		this.numberConflicts = 0;
		this.wastedCycles = 0;

		this.g = new Graph(filename);
		//algo = Algorithm.MGM1;
		this.grouping = kort;
		this.algo = a;
		this.quality = new int[cycles];
		this.msgsize = new int[cycles];
		this.nummsg = new int[cycles];
		this.nEval = new int[cycles];
		this.nConflicts = new int[cycles];
		this.wCycles = new int[cycles];

		Helper.app = this;
		this.cycles = cycles;

		this.s = s;
		this.ws = ws;
	}


}
