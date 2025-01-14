package frameworks.faulttolerance.dcop.algo.topt;

import frameworks.faulttolerance.dcop.daj.Message;

public class LockMsg extends Message {
	int gid;
	double val;
	int attempt;
	TreeNode node;
	boolean lock;

	public LockMsg() {
		super();
	}

	public LockMsg(int i, double v, int a, TreeNode n, boolean l) {
		gid = i;
		val = v;
		attempt = a;
		node = n;
		lock = l;
	}

	public String getText() {
		return (lock ? "LOCK " : "UNLOCK ") + gid + "\n";
	}

	public int getSize() {
		return 6;
		// return 6 + 8 * node.getSize();
	}
}

