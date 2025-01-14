package frameworks.faulttolerance.dcop.algo;

import java.util.HashSet;
import frameworks.faulttolerance.dcop.daj.Channel;
import frameworks.faulttolerance.dcop.daj.Message;
<<<<<<< HEAD
<<<<<<< HEAD
import frameworks.faulttolerance.dcop.dcop.AbstractConstraint;
import frameworks.faulttolerance.dcop.dcop.Helper;
import frameworks.faulttolerance.dcop.dcop.AbstractVariable;
=======
import frameworks.faulttolerance.dcop.dcop.MemFreeConstraint;
import frameworks.faulttolerance.dcop.dcop.Helper;
import frameworks.faulttolerance.dcop.dcop.ReplicationVariable;
>>>>>>> dcopX
=======
import frameworks.faulttolerance.dcop.dcop.MemFreeConstraint;
import frameworks.faulttolerance.dcop.dcop.Helper;
import frameworks.faulttolerance.dcop.dcop.ReplicationVariable;
>>>>>>> dcopX

public class AlgoMGM1 extends BasicAlgorithm {

	int bestVal;

	HashSet<Integer> acceptSet;

<<<<<<< HEAD
<<<<<<< HEAD
	public AlgoMGM1(AbstractVariable v) {
=======
	public AlgoMGM1(ReplicationVariable v) {
>>>>>>> dcopX
=======
	public AlgoMGM1(ReplicationVariable v) {
>>>>>>> dcopX
		super(v);
		bestVal = -1;
		acceptSet = new HashSet<Integer>();
	}

	@Override
	protected void main() {
		self.setValue(self.getInitialValue());
		out().broadcast(createValueMsg());
		while (true) {

			if (lock == -1 && bestVal != -1 && bestVal != self.getValue()
					&& getTime() > reLockTime) {
				lock = self.id;
				acceptSet.clear();
				out().broadcast(createLockMsg());
			}

			int index = in().select(1);
			if (index != -1) {
				done = false;
				int sender = ((AlgoMGM1) ((Channel) in(index)).getSender()
						.getProgram()).getID();
				Message msg = in(index).receive();

				if (msg instanceof MGM1ValueMsg) {
					MGM1ValueMsg vmsg = (MGM1ValueMsg) msg;
					if (view.varMap.get(sender).getValue() != vmsg.value) {
						view.varMap.get(sender).setValue(vmsg.value);
						bestVal = checkView();
					}
				} else if (msg instanceof MGM1LockMsg) {
					MGM1LockMsg lmsg = (MGM1LockMsg) msg;
					if (lmsg.lock) {
						if (lock == -1) {
							lock = sender;
							out(index).send(createAcceptMsg());
						} else
							out(index).send(createDenyMsg());
					} else {
						if (lock == sender)
							lock = -1;
					}
				} else if (msg instanceof MGM1ResponseMsg) {
					if (lock == self.id) {
						MGM1ResponseMsg rmsg = (MGM1ResponseMsg) msg;
						if (rmsg.accept)
							acceptSet.add(sender);
						else {
							lock = -1;
							out().broadcast(createUnLockMsg());
							reLockTime = getTime()
									+ Helper.random.nextInt(reLockInterval);
						}
						if (acceptSet.size() == self.getNeighbors().size()) {
							lock = -1;
							System.out.println("" + self.id + ":\t"
									+ self.getValue() + " -> " + bestVal);
							out().broadcast(createUnLockMsg());
							self.setValue(bestVal);
							out().broadcast(createValueMsg());
						}
					}
				}
			} else {
				if (bestVal == self.getValue()) {
					done = true;
					if (checkStable())
						break;
				} else
					done = false;
				yield();
			}
		}
	}

	int checkView() {
		int best = 0;
		int val = -1;
		for (int i = 0; i < self.getDomain(); i++) {
			int sum = 0;
<<<<<<< HEAD
<<<<<<< HEAD
			for (AbstractConstraint c : self.neighbors) {
				AbstractVariable n = c.getNeighbor(self);
				if (n.value == -1)
					return -1;
				if (self == c.getFirst())
					sum += c.f[i][n.value];
=======
=======
>>>>>>> dcopX
			for (MemFreeConstraint c : self.getNeighbors()) {
				ReplicationVariable n = c.getNeighbor(self);
				if (n.getValue() == -1)
					return -1;
				if (self == c.first)
					sum += c.evaluate(i,n.getValue());
>>>>>>> dcopX
				else
					sum += c.evaluate(n.getValue(),i);
			}
			if (sum > best) {
				best = sum;
				val = i;
			}
		}
		return val;
	}

	public String getText() {
		return "ID: " + self.id + "\nVal: " + self.getValue() + "\nBest: " + bestVal
				+ "\nNextLock: " + reLockTime;
	}

	MGM1ValueMsg createValueMsg() {
		return new MGM1ValueMsg(self.getValue());
	}

	MGM1LockMsg createLockMsg() {
		return new MGM1LockMsg(true);
	}

	MGM1LockMsg createUnLockMsg() {
		return new MGM1LockMsg(false);
	}

	MGM1ResponseMsg createAcceptMsg() {
		return new MGM1ResponseMsg(true);
	}

	MGM1ResponseMsg createDenyMsg() {
		return new MGM1ResponseMsg(false);
	}	
}

class MGM1ValueMsg extends Message {
	int value;

	public MGM1ValueMsg(int v) {
		super();
		value = v;
	}

	public String getText() {
		return ("VALUE " + value);
	}
}

class MGM1LockMsg extends Message {
	boolean lock;

	public MGM1LockMsg(boolean l) {
		super();
		lock = l;
	}

	public String getText() {
		return (lock ? "LOCK" : "UNLOCK");
	}
}

class MGM1ResponseMsg extends Message {
	boolean accept;

	public MGM1ResponseMsg(boolean a) {
		super();
		accept = a;
	}

	public String getText() {
		return (accept ? "ACCEPT" : "DENY");
	}
}
