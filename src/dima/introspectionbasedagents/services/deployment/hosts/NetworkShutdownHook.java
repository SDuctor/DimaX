package dima.introspectionbasedagents.services.deployment.hosts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.jdom.JDOMException;

import dima.introspectionbasedagents.services.communicating.remoteexecution.SSHExecutor;


public class NetworkShutdownHook extends Thread{

	//
	// Fields
	//

	String destructionCommand = "killall -9 java; killall -9 java_vm";
	Collection<RemoteHostInfo> hosts;

	//
	// Constructor
	//

	public NetworkShutdownHook(final Collection<RemoteHostInfo> hosts) {
		super();
		this.hosts = hosts;
	}

	public NetworkShutdownHook(final HostsPark machines) {
		this.hosts = new ArrayList<RemoteHostInfo>();
		this.hosts.addAll(machines.getAllHosts());
		this.hosts.add(machines.getNameServer());
	}

	public NetworkShutdownHook(final String exitCommand,
			final Collection<RemoteHostInfo> hosts) {
		super();
		this.destructionCommand = exitCommand;
		this.hosts = hosts;
	}

	//
	// Accessors
	//

	public Collection<RemoteHostInfo> getAllHosts(){
		return this.hosts;
	}

	//
	// Methods
	//

	/**
	 * Self Destruction Using Thread hook
	 * Détruit les processus java lancés par ssh lorsque le systeme recoit le signal d'extinction
	 * @author Ductor Sylvain
	 */
	public void activate(){

		// Adding ShutdownHook : Stop automatically all the remote JVM at the end
		// of the application
		Runtime.getRuntime().addShutdownHook(this);
	}


	@Override
	public void run() {
		this.destroyAllMachines();
	};

	//
	// Main
	//

	/**
	 * Kills all the machines of the xml
	 * @param args
	 * @throws JDOMException
	 * @throws IOException
	 */
	public static void main(final String[] args) throws JDOMException,
	IOException {
		if (!(args.length>0)){
			System.err.println("No machine xml path given");
			System.exit(-1);
		}
		new NetworkShutdownHook(new HostsPark(args[0])).destroyAllMachines();;
		System.exit(-1);
	}

	//
	// Primitives
	//

	private void destroyAllMachines(){
		System.out.println("\n\n EXITING!!!!!");

		for (final RemoteHostInfo h : this.getAllHosts()) {
			if (!h.getUrl().equals(LocalHost.getUrl())) {
				try {
					System.out.println("\n *** Connecting "+h+" ...");
					new SSHExecutor(h).execute(this.destructionCommand);
					System.out.println("   ---> Application on "+h+" successfully destroyed");
					//					h.disconnect();
				} catch (final Exception e) {
					System.err.println("Application on "+h+" has not been destroyed");
					e.printStackTrace();
				}
			}
		}
	}
}
