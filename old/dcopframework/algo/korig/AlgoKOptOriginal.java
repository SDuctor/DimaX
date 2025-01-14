package negotiation.dcopframework.algo.korig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import negotiation.dcopframework.algo.BasicAlgorithm;
import negotiation.dcopframework.algo.TerminateMessage;
import negotiation.dcopframework.daj.Channel;
import negotiation.dcopframework.daj.Message;
import negotiation.dcopframework.dcop.Constraint;
import negotiation.dcopframework.dcop.Graph;
import negotiation.dcopframework.dcop.Helper;
import negotiation.dcopframework.dcop.Variable;

public class AlgoKOptOriginal extends BasicAlgorithm {

	int k;

	HashMap<Integer, LocalInfo> localInfoMap;
	HashMap<Integer, GainInfo> gainInfoMap;
	HashMap<Integer, CommitInfo> commitInfoMap;

	HashSet<Integer> localInfoSet;
	int localInfoCounter = 0;
	HashSet<Integer> gainInfoSet;
	int gainInfoCounter = 0;
	HashSet<Integer> commitInfoSet;
	int commitInfoCounter = 0;

	ArrayList<Message> buffer;

	int state;

	int sync;

	Graph korigView;
	Variable korigSelf;

	public AlgoKOptOriginal(final Variable v, final int kk) {
		super(v);
		this.k = kk;
		this.self.value = Helper.random.nextInt(this.self.domain);
		this.init();
	}

	private void init() {
		if (this.self.id == 0) {
			System.out.println("STARTOVER");
		}
		this.buffer = new ArrayList<Message>();
		this.localInfoMap = new HashMap<Integer, LocalInfo>();
		this.gainInfoMap = new HashMap<Integer, GainInfo>();
		this.commitInfoMap = new HashMap<Integer, CommitInfo>();
		this.localInfoSet = new HashSet<Integer>();
		this.gainInfoSet = new HashSet<Integer>();
		this.commitInfoSet = new HashSet<Integer>();
		this.localInfoCounter = 0;
		this.gainInfoCounter = 0;
		this.commitInfoCounter = 0;
		this.state = 0;
		for (final Constraint c : this.self.neighbors) {
			c.getNeighbor(this.self).value = -1;
		}
		this.korigView = new Graph();
		for (final Variable v : this.view.varMap.values()) {
			this.korigView.varMap.put(v.id, new Variable(v.id, v.domain, this.korigView));
		}
		this.korigSelf = this.korigView.varMap.get(this.self.id);
		for (final Constraint c : this.view.conList) {
			final Constraint cc = new Constraint(this.korigView.getVar(c.first.id),
					this.korigView.getVar(c.second.id));
			this.korigView.conList.add(cc);
			for (int i = 0; i < c.d1; i++) {
				for (int j = 0; j < c.d2; j++) {
					cc.f[i][j] = c.f[i][j];
				}
			}
		}
		this.sync = 0;
	}

	private void processMsg(final Message msg) {
		if (msg instanceof KorigLocalMsg) {
			final KorigLocalMsg lmsg = (KorigLocalMsg) msg;
			if (!this.localInfoSet.contains(lmsg.id)) {
				this.localInfoSet.add(lmsg.id);
				for (final LocalInfo l : lmsg.map.values()) {
					this.localInfoMap.put(l.id, l);
				}
				if (this.localInfoSet.size() == this.self.neighbors.size()) {
					this.localInfoSet.clear();
					this.localInfoCounter++;
					if (this.localInfoCounter < this.k / 2 + 1) {
						this.out().broadcast(
								new KorigLocalMsg(this.self.id, this.localInfoMap));
					}
				}
			} else {
				this.buffer.add(msg);
			}

		} else if (msg instanceof KorigGainMsg) {
			final KorigGainMsg gmsg = (KorigGainMsg) msg;
			if (!this.gainInfoSet.contains(gmsg.id)) {
				this.gainInfoSet.add(gmsg.id);
				for (final GainInfo g : gmsg.map.values()) {
					this.gainInfoMap.put(g.id, g);
				}
				if (this.gainInfoSet.size() == this.self.neighbors.size()) {
					this.gainInfoSet.clear();
					this.gainInfoCounter++;
					if (this.gainInfoCounter < this.k / 2 + 1) {
						this.out().broadcast(new KorigGainMsg(this.self.id, this.gainInfoMap));
					}
				}
			} else {
				this.buffer.add(msg);
			}

		} else if (msg instanceof KorigCommitMsg) {
			final KorigCommitMsg cmsg = (KorigCommitMsg) msg;
			if (!this.commitInfoSet.contains(cmsg.id)) {
				this.commitInfoSet.add(cmsg.id);
				for (final CommitInfo g : cmsg.map.values()) {
					this.commitInfoMap.put(g.id, g);
				}
				if (this.commitInfoSet.size() == this.self.neighbors.size()) {
					this.commitInfoSet.clear();
					this.commitInfoCounter++;
					if (this.commitInfoCounter < this.k + 1) {
						this.out().broadcast(
								new KorigCommitMsg(this.self.id, this.commitInfoMap));
					}
				}
			} else {
				this.buffer.add(msg);
			}

		}
	}

	@Override
	protected void main() {

		this.out().broadcast(new KorigValueMsg(this.self));

		while (true) {
			final int index = this.in().select(1);
			if (index != -1) {

				this.done = false;

				final Message msg = this.in(index).receive(1);
				if (msg == null) {
					continue;
				}

				final int sender = ((Channel) this.in(index)).getSender();

				if (msg instanceof TerminateMessage) {
					break;
				} else if (msg instanceof KorigValueMsg) {
					final KorigValueMsg vmsg = (KorigValueMsg) msg;
					this.view.varMap.get(sender).value = vmsg.value;
					boolean f = true;
					for (final Constraint c : this.self.neighbors) {
						if (c.getNeighbor(this.self).value == -1) {
							f = false;
							break;
						}
					}
					if (f) {
						// state++;
						if (this.k > 1) {
							final LocalInfo l = new LocalInfo(this.self);
							this.localInfoMap.put(this.self.id, l);
							this.out().broadcast(
									new KorigLocalMsg(this.self.id, this.localInfoMap));
						}
						this.localInfoCounter++;
					}
				} else {
					this.processMsg(msg);
				}
			} else {
				if (!this.buffer.isEmpty()) {
					final ArrayList<Message> tmp = new ArrayList<Message>();
					tmp.addAll(this.buffer);
					this.buffer.clear();
					for (final Message msg : tmp) {
						this.processMsg(msg);
					}
				}

				if (this.localInfoCounter == this.k / 2 + 1) {
					this.localInfoCounter = 0;
					for (final Variable v : this.view.varMap.values()) {
						this.korigView.varMap.get(v.id).value = v.value;
					}

					for (final LocalInfo l : this.localInfoMap.values()) {
						Variable v = this.korigView.varMap.get(l.id);
						if (v == null) {
							v = new Variable(l.id, l.domain, this.korigView);
							this.korigView.varMap.put(v.id, v);
						}
						for (final int[] enc : l.data) {
							if (enc[0] == v.id) {
								Variable n = this.korigView.varMap.get(enc[2]);
								if (n == null) {
									n = new Variable(enc[2], enc[3], this.korigView);
									this.korigView.varMap.put(n.id, n);
								}
								if (!v.hasNeighbor(n.id)) {
									final Constraint c = new Constraint(v, n);
									this.korigView.conList.add(c);
									for (int i = 0; i < c.d1; i++) {
										for (int j = 0; j < c.d2; j++) {
											c.f[i][j] = enc[4 + i * c.d2 + j];
										}
									}
								}
							} else {
								Variable n = this.korigView.varMap.get(enc[0]);
								if (n == null) {
									n = new Variable(enc[0], enc[1], this.korigView);
									this.korigView.varMap.put(n.id, n);
								}
								if (!v.hasNeighbor(n.id)) {
									final Constraint c = new Constraint(n, v);
									this.korigView.conList.add(c);
									for (int i = 0; i < c.d1; i++) {
										for (int j = 0; j < c.d2; j++) {
											c.f[i][j] = enc[4 + i * c.d2 + j];
										}
									}
								}
							}
						}
						for (final Integer i : l.valMap.keySet()) {
							this.korigView.varMap.get(i).value = l.valMap.get(i);
						}
					}

					final HashSet<Integer> kgroup = new HashSet<Integer>();
					final ArrayList<Integer> cList = new ArrayList<Integer>();
					kgroup.add(this.korigSelf.id);
					for (final Constraint c : this.korigSelf.neighbors) {
						cList.add(c.getNeighbor(this.korigSelf).id);
					}
					while (kgroup.size() < this.k) {
						if (cList.isEmpty()) {
							break;
						}
						final int idx = cList.remove(Helper.random.nextInt(cList
								.size()));
						kgroup.add(idx);
						final Variable v = this.korigView.getVar(idx);
						for (final Constraint c : v.neighbors) {
							final int nid = c.getNeighbor(v).id;
							if (this.localInfoMap.containsKey(nid)
									&& !cList.contains(nid)
									&& !kgroup.contains(nid)) {
								cList.add(nid);
							}
						}
					}

					for (final Variable v : this.korigView.varMap.values()) {
						v.fixed = true;
					}
					for (final Integer i : kgroup) {
						this.korigView.getVar(i).fixed = false;
					}
					final HashMap<Integer, Integer> sol = this.korigView.DPOPSolve();
					final int gain = this.korigView.evaluate(sol) - this.korigView.evaluate();
					final HashMap<Integer, Integer> vMap = new HashMap<Integer, Integer>();
					for (final Integer i : kgroup) {
						final Variable v = this.korigView.getVar(i);
						vMap.put(v.id, sol.get(v.id));
						for (final Constraint c : v.neighbors) {
							final Variable n = c.getNeighbor(v);
							vMap.put(n.id, sol.get(n.id));
						}
					}
					final GainInfo g = new GainInfo(this.korigSelf.id, gain, vMap);
					this.gainInfoMap.put(g.id, g);
					this.out().broadcast(new KorigGainMsg(this.self.id, this.gainInfoMap));
				}

				if (this.gainInfoCounter == this.k / 2 + 1) {
					this.gainInfoCounter = 0;
					int maxGain = 0;
					int leader = -1;
					for (final GainInfo g : this.gainInfoMap.values()) {
						if (g.valMap.containsKey(this.self.id)) {
							if (g.gain > maxGain) {
								maxGain = g.gain;
								leader = g.id;
							}
						}
					}
					final CommitInfo c = new CommitInfo(this.self.id, leader);
					this.commitInfoMap.put(this.self.id, c);
					this.out().broadcast(new KorigCommitMsg(this.self.id, this.commitInfoMap));
				}

				if (this.commitInfoCounter == this.k + 1) {
					this.commitInfoCounter = 0;
					final int leader = this.commitInfoMap.get(this.self.id).leader;
					if (leader != -1) {
						boolean f = true;
						for (final Integer i : this.gainInfoMap.get(leader).valMap
								.keySet()) {
							if (this.commitInfoMap.get(i).leader != leader) {
								f = false;
								break;
							}
						}
						if (f) {
							final int gain = this.gainInfoMap.get(leader).gain;
							final int newValue = this.gainInfoMap.get(leader).valMap
									.get(this.self.id);
							if (this.self.value != newValue) {
								System.out.println(leader + " " + this.self.id + " "
										+ this.self.value + "->" + newValue + " : "
										+ gain);
							}
							this.self.value = newValue;
						}
					}
					this.init();
					this.out().broadcast(new KorigValueMsg(this.self));
				}
			}
		}
	}
}

class KorigValueMsg extends Message {
	int id;
	int value;

	public KorigValueMsg(final Variable v) {
		this.value = v.value;
	}

	@Override
	public int getSize() {
		return 9;
	}
}

class KorigLocalMsg extends Message {
	int id;
	HashMap<Integer, LocalInfo> map;

	public KorigLocalMsg(final int i, final HashMap<Integer, LocalInfo> m) {
		this.id = i;
		this.map = new HashMap<Integer, LocalInfo>();
		for (final LocalInfo l : m.values()) {
			this.map.put(l.id, l);
		}
	}

	@Override
	public int getSize() {
		int s = 0;
		for (final LocalInfo l : this.map.values()) {
			s += l.getSize();
		}
		return s + 5;
	}
}

class KorigGainMsg extends Message {
	int id;
	HashMap<Integer, GainInfo> map;

	public KorigGainMsg(final int i, final HashMap<Integer, GainInfo> m) {
		this.id = i;
		this.map = new HashMap<Integer, GainInfo>();
		for (final GainInfo l : m.values()) {
			this.map.put(l.id, l);
		}
	}

	@Override
	public int getSize() {
		int s = 0;
		for (final GainInfo l : this.map.values()) {
			s += l.getSize();
		}
		return s + 5;
	}
}

class KorigCommitMsg extends Message {
	int id;
	HashMap<Integer, CommitInfo> map;

	public KorigCommitMsg(final int i, final HashMap<Integer, CommitInfo> m) {
		this.id = i;
		this.map = new HashMap<Integer, CommitInfo>();
		for (final CommitInfo l : m.values()) {
			this.map.put(l.id, l);
		}
	}

	@Override
	public int getSize() {
		return this.map.size() * 8 + 5;
	}
}