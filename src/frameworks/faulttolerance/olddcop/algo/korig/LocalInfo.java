package frameworks.faulttolerance.olddcop.algo.korig;

import java.util.ArrayList;
import java.util.HashMap;

import frameworks.faulttolerance.olddcop.DCOPFactory;
import frameworks.faulttolerance.olddcop.dcop.MemFreeConstraint;
import frameworks.faulttolerance.olddcop.dcop.ReplicationVariable;
import frameworks.negotiation.rationality.AgentState;

public class LocalInfo {
	int id;
	public AgentState state;
	int value;
	int domain;
	ArrayList<double[]> data;
	HashMap<Integer,AgentState>dataStates;
	HashMap<Integer, Integer> valMap;

	public LocalInfo(ReplicationVariable v) {
		id = v.id;
		domain = v.getDomain();
		state = v.getState();
		value = v.getValue();
		data = new ArrayList<double[]>();
		if (!DCOPFactory.isClassical())
			dataStates=new HashMap<Integer, AgentState>();
		valMap = new HashMap<Integer, Integer>();
		for (MemFreeConstraint c : v.getNeighbors()) {
			ReplicationVariable n = c.getNeighbor(v);
			valMap.put(n.id, n.getValue());
			data.add(c.encode());

			if (!DCOPFactory.isClassical()){
				dataStates.put(c.first.id, c.first.getState());
				dataStates.put(c.second.id, c.second.getState());
			}
		}
	}

	public int getSize() {
		int size = 0;
		for (double[] array : data)
			size += array.length * 4;
		return 12 + size + valMap.size() * 4;
	}
}