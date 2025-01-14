package frameworks.negotiation;

public class NegotiationParameters {

	//
	// Negotiation Tickers
	//

	public static final long _timeToCollect =1000;//500;//
	public static final long _initiatorPropositionFrequency = -1;//(long) (ExperimentationProtocol._timeToCollect*0.5);//(long)
	// public static final long _initiator_analysisFrequency = (long) (_timeToCollect*2);
	public static final long _contractExpirationTime = Long.MAX_VALUE;//10000;//20 * ReplicationExperimentationProtocol._timeToCollect;

	public static final long opinionDiffusionFrequency = NegotiationParameters._timeToCollect/2;

	/**
	 * Clés statiques
	 */

	//Protocoles
	public final static String key4mirrorProto = "mirror protocol";
	public final static String key4CentralisedstatusProto = "Centralised status protocol";
	public final static String key4statusProto = "status protocol";
	public final static String key4multiLatProto = "multi lateral protocol";

	//Selection algorithms
	public final static String key4greedySelect = "greedy select";
	public final static String key4rouletteWheelSelect = "roolette wheel select";
	public final static String key4randomSelect = "random select";
	public final static String key4OptSelect = "opt select";
	public static final String key4BetterSelect = "better select";

}
