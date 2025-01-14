package frameworks.faulttolerance.dcop.algo.topt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import dima.introspectionbasedagents.modules.faults.Assert;

import frameworks.faulttolerance.dcop.DCOPFactory;
import frameworks.faulttolerance.dcop.algo.BasicAlgorithm;
import frameworks.faulttolerance.dcop.algo.LockingBasicAlgorithm;
import frameworks.faulttolerance.dcop.algo.TerminateMessage;
import frameworks.faulttolerance.dcop.daj.Channel;
import frameworks.faulttolerance.dcop.daj.Message;
import frameworks.faulttolerance.dcop.daj.Program;
<<<<<<< HEAD
<<<<<<< HEAD
import frameworks.faulttolerance.dcop.dcop.AbstractConstraint;
import frameworks.faulttolerance.dcop.dcop.Helper;
import frameworks.faulttolerance.dcop.dcop.AbstractVariable;
=======
=======
>>>>>>> dcopX
import frameworks.faulttolerance.dcop.dcop.MemFreeConstraint;
import frameworks.faulttolerance.dcop.dcop.CPUFreeConstraint;
import frameworks.faulttolerance.dcop.dcop.Helper;
import frameworks.faulttolerance.dcop.dcop.ReplicationVariable;
<<<<<<< HEAD
>>>>>>> dcopX
=======
>>>>>>> dcopX
import frameworks.faulttolerance.dcop.exec.DCOPApplication;
import frameworks.faulttolerance.dcop.exec.Stats;
import frameworks.negotiation.rationality.AgentState;

public class AlgoKOptAPO extends LockingBasicAlgorithm {

	int k;

	HashSet<Integer> activeSet;	
	public HashMap<Integer, DPOPTreeNode> localTreeMap;
	HashSet<HashSet<Integer>> localTreeSet;
	DPOPTreeNode lockingNode;
	int gIdCounter;

<<<<<<< HEAD
<<<<<<< HEAD
	public AlgoKOptAPO(AbstractVariable v, int kk) {
=======
	public AlgoKOptAPO(ReplicationVariable v, int kk) {
>>>>>>> dcopX
=======
	public AlgoKOptAPO(ReplicationVariable v, int kk) {
>>>>>>> dcopX
		super(v, true, kk);
		k = kk;
		init();
	}

<<<<<<< HEAD
<<<<<<< HEAD
	public AlgoKOptAPO(AbstractVariable v, int kk, boolean s, int ws) {
=======
	public AlgoKOptAPO(ReplicationVariable v, int kk, boolean s, int ws) {
>>>>>>> dcopX
=======
	public AlgoKOptAPO(ReplicationVariable v, int kk, boolean s, int ws) {
>>>>>>> dcopX
		super(v, s, ws);
		k = kk;
		init();
	}

	protected void init() {
		self.setValue(self.getInitialValue());
		localTreeSet = new HashSet<HashSet<Integer>>();
		activeSet = new HashSet<Integer>();
		localTreeMap = new HashMap<Integer, DPOPTreeNode>();
		gIdCounter = 0;
	}

	private void unlock() {
		if (lockBase < 16)
			lockBase <<= 1;
		reLockTime = getTime()
				+ Helper.random.nextInt(reLockInterval * lockBase);
		removeLock(self.id);
		waiting = false;
		for (TreeNode n : lockingNode.children) {
			out(outChannelMap.get(n.id)).send(
					new LockMsg(lockingNode.id, lockingNode.reward, attempt, n,
							false));
		}
		lockingNode = null;
		DCOPApplication app = (DCOPApplication) this.node.getNetwork()
				.getApplication();
		app.numberConflicts++;
	}

	@Override
	protected void main() {
		// TODO Auto-generated method stub

		for (int i = 0; i < in().getSize(); i++) {
			Program p = ((Channel) in(i)).getSender().getProgram();
			if (p instanceof BasicAlgorithm) {
				int id = ((BasicAlgorithm) p).getID();
				inChannelMap.put(id, i);
			}
		}

		for (int i = 0; i < out().getSize(); i++) {
			Program p = ((Channel) out(i)).getReceiver().getProgram();
			if (p instanceof BasicAlgorithm) {
				int id = ((BasicAlgorithm) p).getID();
				outChannelMap.put(id, i);
			}
		}

		if (k > 1)
			out().broadcast(new LocalConstraintMsg(self, k / 2));
		changed = true;
		out().broadcast(new ValueMsg(self, k / 2 + 1));

		// HashMap<Integer, Integer> valTTLMap = new HashMap<Integer,
		// Integer>();
		HashMap<Integer, Integer> conTTLMap = new HashMap<Integer, Integer>();

		while (true) {
			int index = in().select(1);
			if (index != -1) {

				done = false;

				Message msg = in(index).receive(1);
				if (msg == null)
					yield();

				int sender = -1;
				Program p = ((Channel) in(index)).getSender().getProgram();
				if (p instanceof BasicAlgorithm) {
					sender = ((BasicAlgorithm) p).getID();
				}

				if (msg instanceof ValueMsg) {
					ValueMsg vmsg = (ValueMsg) msg;
<<<<<<< HEAD
<<<<<<< HEAD
					AbstractVariable v = view.varMap.get(vmsg.id);
=======
					ReplicationVariable v = view.varMap.get(vmsg.id);
>>>>>>> dcopX
=======
					ReplicationVariable v = view.varMap.get(vmsg.id);
>>>>>>> dcopX
					assert v != null;
					if (vmsg.ttl > 1)
						out().broadcast(vmsg.forward());
					if (v.getValue() != vmsg.value) {
						v.setValue(vmsg.value);
						// TODO
						if (waiting) {
							TreeNode node = lockingNode.find(v.id);
							if (node != null)
								unlock();
						}

						for (DPOPTreeNode root : localTreeMap.values()) {
							TreeNode node = root.find(v.id);
							if (node != null && node.fixed) {
								computeOpt(root);
								if (checkGroup(root))
									activeSet.add(root.gid);
								else
									activeSet.remove(root.gid);
							}
						}
					}
				} else if (msg instanceof LocalConstraintMsg) {
					LocalConstraintMsg lmsg = (LocalConstraintMsg) msg;
					Integer lastTTL = conTTLMap.get(lmsg.id);
					if (lastTTL == null) {
						conTTLMap.put(lmsg.id, lmsg.ttl);
<<<<<<< HEAD
<<<<<<< HEAD
						AbstractVariable v = view.varMap.get(lmsg.id);
						if (v == null) {
							v = new AbstractVariable(lmsg.id, lmsg.domain, view);
=======
						ReplicationVariable v = view.varMap.get(lmsg.id);
						if (v == null) {
							v = DCOPFactory.constructVariable(lmsg.id, lmsg.domain, lmsg.state, view.getSocialWelfare());
>>>>>>> dcopX
=======
						ReplicationVariable v = view.varMap.get(lmsg.id);
						if (v == null) {
							v = DCOPFactory.constructVariable(lmsg.id, lmsg.domain, lmsg.state, view.getSocialWelfare());
>>>>>>> dcopX
							view.varMap.put(v.id, v);
						}
						v.fixed = false;
						for (double[] enc : lmsg.data) {
							if (enc[0] == v.id) {
<<<<<<< HEAD
<<<<<<< HEAD
								AbstractVariable n = view.varMap.get((int)enc[2]);
								if (n == null) {
									n = new AbstractVariable((int)enc[2], (int)enc[3], view);
=======
=======
>>>>>>> dcopX
								ReplicationVariable n = view.varMap.get((int)enc[2]);
								if (n == null) {
									assert Assert.Imply(!DCOPFactory.isClassical(),lmsg.dataStates!=null);
									AgentState s = null;
									if (!DCOPFactory.isClassical())
										s = lmsg.dataStates.get((int)enc[2]);
									n = DCOPFactory.constructVariable((int)enc[2], (int)enc[3], s,view.getSocialWelfare());
<<<<<<< HEAD
>>>>>>> dcopX
=======
>>>>>>> dcopX
									if (lmsg.ttl <= 1)
										n.fixed = true;
									view.varMap.put(n.id, n);
								}
								if (!v.hasNeighbor(n.id)) {
<<<<<<< HEAD
<<<<<<< HEAD
									AbstractConstraint c = new AbstractConstraint(v, n);
=======
									MemFreeConstraint c = DCOPFactory.constructConstraint(v, n);
>>>>>>> dcopX
=======
									MemFreeConstraint c = DCOPFactory.constructConstraint(v, n);
>>>>>>> dcopX
									view.conList.add(c);
									if (DCOPFactory.isClassical()){
										for (int i = 0; i < c.d1; i++)
											for (int j = 0; j < c.d2; j++) {
												((CPUFreeConstraint)c).f[i][j] = enc[4 + i * c.d2 + j];
											}
										((CPUFreeConstraint)c).cache();
									}
								}
							} else {
<<<<<<< HEAD
<<<<<<< HEAD
								AbstractVariable n = view.varMap.get((int)enc[0]);
								if (n == null) {
									n = new AbstractVariable((int)enc[0], (int)enc[1], view);
=======
=======
>>>>>>> dcopX
								ReplicationVariable n = view.varMap.get((int)enc[0]);
								if (n == null) {
									AgentState s = null;
									if (!DCOPFactory.isClassical())
										s = lmsg.dataStates.get((int)enc[0]);
									n = DCOPFactory.constructVariable((int)enc[0], (int)enc[1], s,view.getSocialWelfare());
<<<<<<< HEAD
>>>>>>> dcopX
=======
>>>>>>> dcopX
									if (lmsg.ttl <= 1)
										n.fixed = true;
									view.varMap.put(n.id, n);
								}
								if (!v.hasNeighbor(n.id)) {
<<<<<<< HEAD
<<<<<<< HEAD
									AbstractConstraint c = new AbstractConstraint(n, v);
=======
									MemFreeConstraint c = DCOPFactory.constructConstraint(n, v);
>>>>>>> dcopX
=======
									MemFreeConstraint c = DCOPFactory.constructConstraint(n, v);
>>>>>>> dcopX
									view.conList.add(c);
									if (DCOPFactory.isClassical()){
										for (int i = 0; i < c.d1; i++)
											for (int j = 0; j < c.d2; j++) {
												((CPUFreeConstraint)c).f[i][j] = enc[4 + i * c.d2 + j];
											}
										((CPUFreeConstraint)c).cache();
									}
								}
							}
						}
						if (lmsg.ttl > 1)
							out().broadcast(lmsg.forward());
					} else if (lastTTL < lmsg.ttl) {
						conTTLMap.put(lmsg.id, lmsg.ttl);
						out().broadcast(lmsg.forward());
					}
				} else if (msg instanceof LockMsg) {
					LockMsg lkmsg = (LockMsg) msg;
					TreeNode root = lkmsg.node;
					if (lkmsg.lock) {
						if (!root.mark) {
							out(outChannelMap.get(sender)).send(
									new ResponseMsg(self.id, lkmsg.attempt,
											root.parent, true));
							for (TreeNode n : root.children) {
								out(outChannelMap.get(n.id)).send(
										new LockMsg(lkmsg.gid, lkmsg.val,
												lkmsg.attempt, n, true));
							}
						} else {
							if (lockVal == root.value) {
								Integer att = lockSet.get(lkmsg.gid);
								if (att == null || att < lkmsg.attempt) {
									lockSet.put(lkmsg.gid, lkmsg.attempt);
									out(outChannelMap.get(sender)).send(
											new ResponseMsg(self.id,
													lkmsg.attempt, root.parent,
													true));
									for (TreeNode n : root.children) {
										out(outChannelMap.get(n.id))
										.send(
												new LockMsg(lkmsg.gid,
														lkmsg.val,
														lkmsg.attempt,
														n, true));
									}
								}
							} else if (lockVal == -1) {
								Integer att = lockSet.get(lkmsg.gid);
								if (att == null || att < lkmsg.attempt) {
									LockMsg l = lockMap.get(lkmsg.gid);
									if (l == null || l.attempt < lkmsg.attempt) {
										if (lockMap.isEmpty())
											lockMsgTimer = getTime();
										lockMap.put(lkmsg.gid, lkmsg);
									}
								}
								for (TreeNode n : root.children) {
									out(outChannelMap.get(n.id)).send(
											new LockMsg(lkmsg.gid, lkmsg.val,
													lkmsg.attempt, n, true));
								}
							} else {
								out(outChannelMap.get(sender)).send(
										new ResponseMsg(self.id, lkmsg.attempt,
												root.parent, false));
							}
						}
					} else {
						Integer att = lockSet.get(lkmsg.gid);
						LockMsg l = lockMap.get(lkmsg.gid);
						if ((att != null && att <= lkmsg.attempt)
								|| (l != null && l.attempt <= lkmsg.attempt)) {
							removeLock(lkmsg.gid);
						}
						for (TreeNode n : root.children) {
							out(outChannelMap.get(n.id)).send(
									new LockMsg(lkmsg.gid, lkmsg.val,
											lkmsg.attempt, n, false));
						}
					}
				} else if (msg instanceof ResponseMsg) {
					ResponseMsg rmsg = (ResponseMsg) msg;
					TreeNode root = rmsg.node;
					if (root.parent == null) {
						if (waiting && lockingNode == root
								&& rmsg.attempt == attempt) {
							if (!rmsg.accept) {
								unlock();
							} else {
								acceptSet.add(rmsg.id);
								if (acceptSet.size() >= lockingNode.getSize()) {
									/////////////store the stats////////////
									Stats st = new Stats();
									double pregain = view.evaluate();
									view.backup();
<<<<<<< HEAD
<<<<<<< HEAD
									for (AbstractVariable v : view.varMap.values()) {
=======
									for (ReplicationVariable v : view.varMap.values()) {
>>>>>>> dcopX
=======
									for (ReplicationVariable v : view.varMap.values()) {
>>>>>>> dcopX
										TreeNode node = lockingNode.find(v.id);
										if (node != null)
											v.setValue(node.value);
									}
									st.gain = view.evaluate() - pregain; 
									view.recover();

									st.varChanged = 0;
<<<<<<< HEAD
<<<<<<< HEAD
									for (AbstractVariable v : view.varMap.values()) {
=======
									for (ReplicationVariable v : view.varMap.values()) {
>>>>>>> dcopX
=======
									for (ReplicationVariable v : view.varMap.values()) {
>>>>>>> dcopX
										TreeNode node = lockingNode.find(v.id);
										if (node != null && v.getValue() != node.value)
											st.varChanged++;
									}
									st.attempts = attempt - preAttempt;
									int present = getTime();
									st.cycles = present - preCycles; 
									st.varLocked = lockingNode.getMarkedNodeSize();
									st.maxLockedDistance = lockingNode.maxdistanceMarkedNode();
									preAttempt = attempt;
									statList.add(st);																
									/////////////////////////////////////////

									lockBase = 1;
									if (self.getValue() != lockVal) {
										System.out.println(self.id + " "
												+ self.id + " " + self.getValue()
												+ "->" + lockVal);
										self.setValue(lockVal);
										out().broadcast(
												new ValueMsg(self, k / 2 + 1));
									}
									waiting = false;
									removeLock(self.id);
									for (TreeNode n : root.children) {
										out(outChannelMap.get(n.id)).send(
												new CommitMsg(self.id, attempt,
														n));
									}
									reLockTime = getTime() + k + 1
											+ +Helper.random.nextInt(k + 1);
								}
							}
						}
					} else {
						out(outChannelMap.get(root.parent.id)).send(
								new ResponseMsg(rmsg.id, rmsg.attempt,
										root.parent, rmsg.accept));
					}
				} else if (msg instanceof CommitMsg) {
					CommitMsg cmsg = (CommitMsg) msg;
					TreeNode root = cmsg.node;
					for (TreeNode n : root.children) {
						out(outChannelMap.get(n.id)).send(
								new CommitMsg(cmsg.gid, cmsg.attempt, n));
					}
					Integer att = lockSet.get(cmsg.gid);
					if (att == null || att != cmsg.attempt) {
						continue;
					}
					if (self.getValue() != lockVal) {
						System.out.println(cmsg.gid + " " + self.id + " "
								+ self.getValue() + "->" + lockVal);
						self.setValue(lockVal);
						out().broadcast(new ValueMsg(self, k / 2 + 1));
					}
					removeLock(cmsg.gid);
				} else if (msg instanceof TerminateMessage) {
					break;
				}
			} else {
				if (this.getTime() > k + 1 && changed) {
					changed = false;
					constructTrees();
					for (DPOPTreeNode root : localTreeMap.values()) {
						computeOpt(root);
						if (checkGroup(root))
							activeSet.add(root.gid);
						else
							activeSet.remove(root.gid);
					}
				}

				if (!lockMap.isEmpty()
						&& getTime() - lockMsgTimer >= windowsize) {
					//output lockmap size
					nlockReq += lockMap.size();
					/////////////	
					double comp = Double.MIN_VALUE;
					int id = Integer.MAX_VALUE;
					LockMsg best = null;
					for (LockMsg msg : lockMap.values()) {
						if (msg.val > comp || (msg.val == comp && msg.gid < id)) {
							comp = msg.val;
							id = msg.gid;
							best = msg;
						}
					}
					lockVal = best.node.value;
					lockSet.put(best.gid, best.attempt);
					if (best.node.parent != null) {
						out(outChannelMap.get(best.node.parent.id)).send(
								new ResponseMsg(self.id, best.attempt,
										best.node.parent, true));
					} else {
						acceptSet.add(self.id);
						if (acceptSet.size() >= lockingNode.getSize()) {
							/////////////store the stats////////////
							Stats st = new Stats();
							double pregain = view.evaluate();
							view.backup();
<<<<<<< HEAD
<<<<<<< HEAD
							for (AbstractVariable v : view.varMap.values()) {
=======
							for (ReplicationVariable v : view.varMap.values()) {
>>>>>>> dcopX
=======
							for (ReplicationVariable v : view.varMap.values()) {
>>>>>>> dcopX
								TreeNode node = lockingNode.find(v.id);
								if (node != null)
									v.setValue(node.value);
							}
							st.gain = view.evaluate() - pregain; 
							view.recover();

							st.varChanged = 0;
<<<<<<< HEAD
<<<<<<< HEAD
							for (AbstractVariable v : view.varMap.values()) {
=======
							for (ReplicationVariable v : view.varMap.values()) {
>>>>>>> dcopX
=======
							for (ReplicationVariable v : view.varMap.values()) {
>>>>>>> dcopX
								TreeNode node = lockingNode.find(v.id);
								if (node != null && v.getValue() != node.value)
									st.varChanged++;
							}
							st.attempts = attempt - preAttempt;

							int present = getTime();
							st.cycles = present - preCycles; 

							st.varLocked = lockingNode.getMarkedNodeSize();
							st.maxLockedDistance = lockingNode.maxdistanceMarkedNode();
							preAttempt = attempt;
							statList.add(st);																
							/////////////////////////////////////////					
							lockBase = 1;
							if (self.getValue() != lockVal) {
								System.out.println(self.id + " " + self.id
										+ " " + self.getValue() + "->" + lockVal);
								self.setValue(lockVal);
								out().broadcast(new ValueMsg(self, k / 2 + 1));
							}
							waiting = false;
							removeLock(self.id);
							for (TreeNode n : lockingNode.children) {
								out(outChannelMap.get(n.id)).send(
										new CommitMsg(self.id, attempt, n));
							}
							reLockTime = getTime() + k + 1
									+ +Helper.random.nextInt(k + 1);
						}
					}

					for (LockMsg msg : lockMap.values()) {
						if (msg.gid == best.gid)
							continue;
						if (msg.node.parent != null) {
							if (msg.node.value == lockVal) {
								lockSet.put(msg.gid, msg.attempt);
								out(outChannelMap.get(msg.node.parent.id))
								.send(
										new ResponseMsg(self.id,
												msg.attempt,
												msg.node.parent, true));
							} else {
								out(outChannelMap.get(msg.node.parent.id))
								.send(
										new ResponseMsg(self.id,
												msg.attempt,
												msg.node.parent, false));
							}
						} else {
							waiting = false;
							lockingNode = null;
							for (TreeNode n : msg.node.children) {
								out(outChannelMap.get(n.id)).send(
										new LockMsg(self.id,
												view.varMap.size(), attempt, n,
												false));
							}
						}
					}
					lockMap.clear();
				}

				if (!waiting && getTime() > reLockTime && !activeSet.isEmpty()) {
					if (getTime() > k + 1) {
						// double p = 1.0 / activeSet.size();
						done = true;
						DPOPTreeNode best = null;
						double gain = 0;
						for (DPOPTreeNode root : localTreeMap.values()) {
							if (activeSet.contains(root.gid)) {
								if (checkGroup(root)) {
									if (root.reward > gain) {
										gain = root.reward;
										best = root;
									}
								} else
									activeSet.remove(root.gid);
							}
						}
						if (best != null) {
							done = false;
							lock(best);
						}
					}
				} else if (!waiting && !lockSet.isEmpty()
						&& getTime() <= reLockTime && !activeSet.isEmpty()) {
					DCOPApplication app = (DCOPApplication) this.node
							.getNetwork().getApplication();
					app.wastedCycles++;
				}
				if (this.getTime() > k + 1 && activeSet.isEmpty())
					done = true;
				yield();
			}
		}
	}

	private boolean checkGroup(DPOPTreeNode root) {
		double orgReward = view.evaluate();
		view.backup();
		ArrayList<DPOPTreeNode> queue = new ArrayList<DPOPTreeNode>();
		queue.add(root);
		while (!queue.isEmpty()) {
			DPOPTreeNode p = queue.remove(0);
			view.varMap.get(p.id).setValue(p.value);
			for (TreeNode n : p.children) {
				DPOPTreeNode dn = (DPOPTreeNode) n;
				queue.add(dn);
			}
		}
		double newReward = view.evaluate();
		view.recover();
		root.reward = newReward - orgReward;
		return newReward > orgReward;
	}

	private boolean computeOpt(DPOPTreeNode root) {
<<<<<<< HEAD
<<<<<<< HEAD
		for (AbstractVariable v : view.varMap.values())
=======
		for (ReplicationVariable v : view.varMap.values()){
>>>>>>> dcopX
=======
		for (ReplicationVariable v : view.varMap.values()){
>>>>>>> dcopX
			v.fixed = true;
		}
		ArrayList<DPOPTreeNode> queue = new ArrayList<DPOPTreeNode>();
		queue.add(root);
		while (!queue.isEmpty()) {
			DPOPTreeNode p = queue.remove(0);
			view.varMap.get(p.id).fixed = p.fixed;
			for (TreeNode n : p.children) {
				DPOPTreeNode dn = (DPOPTreeNode) n;
				queue.add(dn);
			}
		}
		HashMap<Integer, Integer> sol = view.solve();
		HashSet<Integer> set = new HashSet<Integer>();
<<<<<<< HEAD
<<<<<<< HEAD
		for (AbstractVariable v : view.varMap.values()) {
			if (v.value != sol.get(v.id)) {
				set.add(v.id);
				for (AbstractConstraint c : v.neighbors) {
=======
=======
>>>>>>> dcopX
		for (ReplicationVariable v : view.varMap.values()) {
			if (v.getValue() != sol.get(v.id)) {
				set.add(v.id);
				for (MemFreeConstraint c : v.getNeighbors()) {
<<<<<<< HEAD
>>>>>>> dcopX
=======
>>>>>>> dcopX
					set.add(c.getNeighbor(v).id);
				}
			}
		}
		queue.add(root);
		boolean f = false;
		while (!queue.isEmpty()) {
			DPOPTreeNode p = queue.remove(0);
			// if (set.contains(p.id))
				p.mark = true;
				if (p.value != sol.get(p.id)) {
					f = true;
				}
				p.value = sol.get(p.id);
				for (TreeNode n : p.children) {
					DPOPTreeNode dn = (DPOPTreeNode) n;
					queue.add(dn);
				}
		}

		if (!subsetlocking)
			root.markAll();
		return f;
	}
	
	private void lock(DPOPTreeNode root) {
		attempt++;
		acceptSet.clear();
		lockingNode = root;
		waiting = true;

		if (lockMap.isEmpty())
			lockMsgTimer = getTime();
		lockMap.put(self.id, new LockMsg(self.id, root.reward, attempt,
				lockingNode, true));
		for (TreeNode n : root.children) {
			out(outChannelMap.get(n.id)).send(
					new LockMsg(self.id, root.reward, attempt, n, true));
		}

	}

	private void constructTrees() {
		// TODO Auto-generated method stub
		HashMap<Integer, Integer> minDis = new HashMap<Integer, Integer>();
		int maxId = 0;
<<<<<<< HEAD
<<<<<<< HEAD
		for (AbstractVariable v : view.varMap.values())
			if (v.id > maxId)
				maxId = v.id;
		maxId++;
		for (AbstractVariable v : view.varMap.values()) {
			if (v.fixed)
				continue;
			ArrayList<AbstractVariable> queue = new ArrayList<AbstractVariable>();
			queue.add(v);
			minDis.put(v.id * maxId + v.id, 0);
			HashSet<Integer> visited = new HashSet<Integer>();
			visited.add(v.id);
			while (!queue.isEmpty()) {
				AbstractVariable var = queue.remove(0);
				for (AbstractConstraint c : var.neighbors) {
					AbstractVariable n = c.getNeighbor(var);
					if (n.fixed)
=======
=======
>>>>>>> dcopX
		for (ReplicationVariable v : view.varMap.values())
			if (v.id > maxId)
				maxId = v.id;
				maxId++;
				for (ReplicationVariable v : view.varMap.values()) {
					if (v.fixed)
<<<<<<< HEAD
>>>>>>> dcopX
=======
>>>>>>> dcopX
						continue;
					ArrayList<ReplicationVariable> queue = new ArrayList<ReplicationVariable>();
					queue.add(v);
					minDis.put(v.id * maxId + v.id, 0);
					HashSet<Integer> visited = new HashSet<Integer>();
					visited.add(v.id);
					while (!queue.isEmpty()) {
						ReplicationVariable var = queue.remove(0);
						for (MemFreeConstraint c : var.getNeighbors()) {
							ReplicationVariable n = c.getNeighbor(var);
							if (n.fixed)
								continue;
							if (!visited.contains(n.id)) {
								queue.add(n);
								int depth = minDis.get(v.id * maxId + var.id);
								visited.add(n.id);
								minDis.put(v.id * maxId + n.id, depth + 1);
							}
						}
					}
				}

<<<<<<< HEAD
<<<<<<< HEAD
		HashSet<Integer> pSet = new HashSet<Integer>();
		HashSet<Integer> cSet = new HashSet<Integer>();
		for (AbstractConstraint c : self.neighbors) {
			AbstractVariable n = c.getNeighbor(self);
			if (!n.fixed)
				cSet.add(n.id);
		}
		this.enumerate(pSet, cSet, minDis, maxId);
=======
=======
>>>>>>> dcopX
				HashSet<Integer> pSet = new HashSet<Integer>();
				HashSet<Integer> cSet = new HashSet<Integer>();
				for (MemFreeConstraint c : self.getNeighbors()) {
					ReplicationVariable n = c.getNeighbor(self);
					if (!n.fixed)
						cSet.add(n.id);
				}
				this.enumerate(pSet, cSet, minDis, maxId);
<<<<<<< HEAD
>>>>>>> dcopX
=======
>>>>>>> dcopX
	}

	private void enumerate(HashSet<Integer> pSet, HashSet<Integer> cSet,
			HashMap<Integer, Integer> minDis, int maxId) {
		if (pSet.size() == k - 1) {
			boolean f = true;
			int d = 0;
			for (Integer i : pSet) {
				int dis = minDis.get(self.id * maxId + i);
				if (dis > d)
					d = dis;
			}
			for (Integer i : pSet) {
				int tmp = minDis.get(i * maxId + self.id);
				for (Integer j : pSet) {
					if (j == i)
						continue;
					int dis = minDis.get(i * maxId + j);
					if (dis > tmp)
						tmp = dis;
				}
				if (tmp < d || (tmp == d && i < self.id)) {
					f = false;
					break;
				}
			}
			if (f) {
				for (HashSet<Integer> tSet : localTreeSet) {
					if (tSet.containsAll(pSet))
						return;
				}
				HashSet<Integer> visited = new HashSet<Integer>();
				DPOPTreeNode root = new DPOPTreeNode(self.id,
						(gIdCounter << 12) + self.id, 0, false, null);
				visited.add(self.id);
				ArrayList<DPOPTreeNode> queue = new ArrayList<DPOPTreeNode>();
				queue.add(root);
				int count = 0;
				while (!queue.isEmpty()) {
					DPOPTreeNode node = queue.remove(0);
<<<<<<< HEAD
<<<<<<< HEAD
					AbstractVariable v = view.varMap.get(node.id);
					for (AbstractConstraint c : v.neighbors) {
						AbstractVariable n = c.getNeighbor(v);
=======
					ReplicationVariable v = view.varMap.get(node.id);
					for (MemFreeConstraint c : v.getNeighbors()) {
						ReplicationVariable n = c.getNeighbor(v);
>>>>>>> dcopX
=======
					ReplicationVariable v = view.varMap.get(node.id);
					for (MemFreeConstraint c : v.getNeighbors()) {
						ReplicationVariable n = c.getNeighbor(v);
>>>>>>> dcopX
						if (visited.contains(n.id))
							continue;
						visited.add(n.id);
						if (!pSet.contains(n.id)) {
							DPOPTreeNode child = new DPOPTreeNode(n.id,
									node.gid, n.getValue(), true, node);
						} else {
							DPOPTreeNode child = new DPOPTreeNode(n.id,
									node.gid, n.getValue(), false, node);
							count++;
							queue.add(child);
						}
					}
				}
				localTreeMap.put(root.gid, root);
				HashSet<Integer> treeSet = new HashSet<Integer>();
				treeSet.addAll(pSet);
				localTreeSet.add(treeSet);
				gIdCounter++;
			}
			return;
		}

		// int max = 0;
		// for (Integer i : pSet)
		// if (max < i)
		// max = i;

		int[] list = new int[cSet.size()];
		{
			int j = 0;
			for (Integer i : cSet) {
				list[j] = i;
				j++;
			}
		}

		for (int i = 0; i < list.length; i++) {
			int j = list[i];
			if (j == self.id)
				continue;
			pSet.add(j);
			HashSet<Integer> add = new HashSet<Integer>();
<<<<<<< HEAD
<<<<<<< HEAD
			AbstractVariable v = view.getVar(j);
			for (AbstractConstraint c : v.neighbors) {
				AbstractVariable n = c.getNeighbor(v);
=======
			ReplicationVariable v = view.getVar(j);
			for (MemFreeConstraint c : v.getNeighbors()) {
				ReplicationVariable n = c.getNeighbor(v);
>>>>>>> dcopX
=======
			ReplicationVariable v = view.getVar(j);
			for (MemFreeConstraint c : v.getNeighbors()) {
				ReplicationVariable n = c.getNeighbor(v);
>>>>>>> dcopX
				if (!n.fixed && !pSet.contains(n.id) && !cSet.contains(n.id))
					add.add(n.id);
			}
			cSet.addAll(add);
			cSet.remove(j);
			this.enumerate(pSet, cSet, minDis, maxId);
			cSet.removeAll(add);
			pSet.remove(j);
		}
	}

	public String getText() {
		String val = "";
<<<<<<< HEAD
<<<<<<< HEAD
		for (AbstractVariable v : view.varMap.values()) {
			val += v.id + "  " + v.value + " " + (v.fixed ? "F" : "") + "\n";
=======
		for (ReplicationVariable v : view.varMap.values()) {
			val += v.id + "  " + v.getValue() + " " + (v.fixed ? "F" : "") + "\n";
>>>>>>> dcopX
=======
		for (ReplicationVariable v : view.varMap.values()) {
			val += v.id + "  " + v.getValue() + " " + (v.fixed ? "F" : "") + "\n";
>>>>>>> dcopX
		}
		val += "LockSet: ";
		for (Integer i : lockSet.values()) {
			val += i + " ";
		}
		val += "\n";

		val += "AcceptSet: ";
		for (Integer i : acceptSet) {
			val += i + " ";
		}
		val += "\n";

		return val + "ID: " + self.id + "\nVal: " + self.getValue() + "\nLockVal: "
		+ lockVal + "\nNextLock: " + reLockTime
		+ (done ? "\nDONE" : "");
	}
}
