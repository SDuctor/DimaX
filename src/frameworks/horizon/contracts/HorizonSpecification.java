package frameworks.horizon.contracts;

import java.util.Collection;
import java.util.Date;
import java.util.Map;


import dima.introspectionbasedagents.modules.aggregator.AbstractCompensativeAggregation;
import dima.introspectionbasedagents.services.information.ObservationService.Information;
import frameworks.negotiation.contracts.AbstractActionSpecif;

/**
 * Horizon contract specification classes must implement this interface.
 * 
 * @author Vincent Letard
 */
public abstract class HorizonSpecification implements AbstractActionSpecif {

	/**
	 * Serial version identifier.
	 */
	private static final long serialVersionUID = 2180105313398969970L;

	/**
	 * Time of creation of the object.
	 */
	private final Long creationTime;

	/**
	 * Creates a new object of type HorizonSpecification.
	 */
	public HorizonSpecification() {
		this.creationTime = new Date().getTime();
	}

	/**
	 * Returns the creation time of the object.
	 */
	@Override
	public Long getCreationTime() {
		return this.creationTime;
	}

	/**
	 * Returns the age of the object.
	 */
	@Override
	public long getUptime() {
		return new Date().getTime() - this.creationTime;
	}

	/**
	 * Tests whether this object is newer than the provided Information, basing
	 * on creation times. Returns a negative value, 0 or positive value if the
	 * object is respectively younger, same aged, older.
	 */
	@Override
	public int isNewerThan(final Information that) {
		if (that instanceof HorizonSpecification) {
			return (int) (this.creationTime - ((HorizonSpecification) that).creationTime);
		} else {
			throw new IllegalArgumentException();
		}
	}

	public HorizonSpecification clone(){
		return this;
	}

}
