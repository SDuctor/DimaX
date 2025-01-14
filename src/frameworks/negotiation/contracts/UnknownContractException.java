package frameworks.negotiation.contracts;


public class UnknownContractException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 7034033947098338528L;
	final ContractIdentifier id;

	public UnknownContractException(final ContractIdentifier id) {
		super(id.toString());
		this.id = id;
	}

	public ContractIdentifier getId() {
		return this.id;
	}
}