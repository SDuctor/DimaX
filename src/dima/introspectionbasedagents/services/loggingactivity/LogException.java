package dima.introspectionbasedagents.services.loggingactivity;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.deployment.hosts.LocalHost;

public class LogException extends LogNotification {

	private static final long serialVersionUID = -2335142989408051767L;

	String text;
	Throwable e = null;
	final String host = LocalHost.getUrl();

	public LogException(final AgentIdentifier id,final String text) {
		super(id);
		this.text = text;
	}

	public LogException(final AgentIdentifier id,final String text, final Throwable e) {
		super(id);
		this.text = text;
		this.e = e;
	}

	//
	// Accessors
	//

	/**
	 * @return the exception
	 */
	protected Throwable getException() {
		return this.e;
	}

	//
	// Methods
	//

	@Override
	public String generateLogToScreen() {
		return "\n\n**** NEW EXCEPTION FROM AGENT " + this.getCaller() + " :" + "\n"
				//		+ (this.e == null ? "" : this.e )
				+ "** On Host" + this.host
				+ "(" + this.date.toString() + " - " + this.date.getTime()
				+ "):\n" + this.text;
	}

	@Override
	public String generateLogToWrite() {
		return "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!EXCEPTION ** FROM AGENT " + this.getCaller()
				+ (this.e == null ? "" : this.e )
				+" On Host" + this.host + "(" + this.date.toString()
				+ " - " + this.date.getTime() + "):\n" + this.text;
	}
}
