package dima.introspectionbasedagents.services.deployment.hosts;

import org.jdom.Element;

import dima.introspectionbasedagents.services.communicating.remoteexecution.SSHExecutor;
import dima.introspectionbasedagents.services.communicating.remoteexecution.SSHInfo;
import dima.introspectionbasedagents.services.deployment.server.HostIdentifier;

/**
 * This class contains all the information required to connect a host via ssh from the xml
 * 
 *
 * @author Sylvain Ductor
 */
public class RemoteHostInfo extends SSHInfo {

	private static final long serialVersionUID = -4743173675870897744L;
	private String groupID;

	public RemoteHostInfo(final String group) {
		super(null,null);
		this.setGroupID(group);
	}

	//
	// Accessors
	//

	public void setAdress(final Element host){
		url = host.getAttributeValue("ip");
		this.port= new Integer(host.getAttributeValue("port"));
	}

	public void setSSH(final Element ssh){
		this.user=ssh.getAttributeValue("user")!=null?
				ssh.getAttributeValue("user"):this.user;
				this.privateKeyPath=ssh.getAttributeValue("keyPath")!=null?
						ssh.getAttributeValue("keyPath"):this.privateKeyPath;
						this.knownHostsPath=ssh.getAttributeValue("knownHostsPath")!=null?
								ssh.getAttributeValue("knownHostsPath"):this.knownHostsPath;
								this.dir=ssh.getAttributeValue("dir")!=null?ssh.getAttributeValue("dir"):this.dir;
								this.gateUrl=ssh.getAttributeValue("dir")!=null?ssh.getAttributeValue("gate"):null;
	}

	/**
	 * @return the gateUrl or null if no gate is needed
	 */
	protected String getGate() {
		return this.gateUrl;
	}

	/**
	 * @return the dimaxDir
	 */
	public String getDimaxDir() {
		return this.dir;
	}


	/**
	 *
	 * @return The directory where the xml and dtd files are located (currently
	 *         for the hosts lists and the log parameters)
	 */
	public String getConfigurationDir() {
		return this.dir + "conf/";
	}

	//
	// Methods
	//

	public HostIdentifier generateHostIdentifier(){
		return new HostIdentifier(this.url, this.port);
	}


	//
	// Hash key
	//

	public boolean equal(final Object o){
		if (o instanceof HostIdentifier) {
			return ((HostIdentifier) o).getUrl().equals(this.getUrl())
					&&((HostIdentifier) o).getPort().equals(this.getPort());
		} else {
			return false;
		}
	}

	public int hash(){
		return new String(this.getUrl()+":"+this.getPort()).hashCode();
	}

	@Override
	public String toString(){
		return new String(this.getUrl()+":"+this.getPort());
	}

	public void setGroupID(final String groupID) {
		this.groupID = groupID;
	}

	public String getGroupID() {
		return this.groupID;
	}
}