package vieux.myDCOP.algo;


public class ValueMsg extends Message {
	int id;
	int value;
	int ttl;

	public ValueMsg() {
		super();
	}

	public ValueMsg(final Variable v, final int t) {
		super();
		this.id = v.id;
		this.value = v.value;
		// if (value == -1)
		// System.out.println("Surprise");
		this.ttl = t;
	}

	public String getText() {
		return "ID " + this.id + ";VALUE " + this.value + ";TTL " + this.ttl;
	}

	public ValueMsg forward() {
		final ValueMsg msg = new ValueMsg();
		msg.id = this.id;
		msg.value = this.value;
		msg.ttl = this.ttl - 1;
		return msg;
	}

	public int getSize() {
		return 13;
	}
}