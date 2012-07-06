package negotiation.negotiationframework.protocoles.collaborative;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import com.sun.org.apache.bcel.internal.generic.NEWARRAY;

import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.ContractTrunk;
import negotiation.negotiationframework.contracts.InformedCandidature;
import negotiation.negotiationframework.contracts.MatchingCandidature;
import negotiation.negotiationframework.contracts.ReallocationContract;
import negotiation.negotiationframework.contracts.UnknownContractException;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol;
import dimaxx.tools.mappedcollections.HashedHashSet;
import dimaxx.tools.mappedcollections.HashedTreeSet;

public class ResourceInformedCandidatureContractTrunk<
Contract extends MatchingCandidature<ActionSpec>,
ActionSpec extends AbstractActionSpecification,
PersonalState extends ActionSpec>
extends ContractTrunk<InformedCandidature<Contract, ActionSpec>, ActionSpec, PersonalState>{
	private static final long serialVersionUID = -5058077493662331641L;

	/**
	 *
	 */

	//	private final ContractTrunk<ReallocationContract<Contract, ActionSpec>> myLocalOptimisations;
	HashedTreeSet<InformedCandidature<Contract, ActionSpec>, ReallocationContract<Contract, ActionSpec>> upgradingContracts;
	final Collection<InformedCandidature<Contract, ActionSpec>> toCancel=
			new ArrayList<InformedCandidature<Contract,ActionSpec>>();

	/*
	 *
	 */

	public void setMyAgent(final SimpleNegotiatingAgent<ActionSpec, PersonalState, InformedCandidature<Contract, ActionSpec>> agent){		
		super.setMyAgent(agent);
		upgradingContracts=
				new HashedTreeSet<InformedCandidature<Contract, ActionSpec>, ReallocationContract<Contract,ActionSpec>>(
						((InformedCandidatureRationality<ActionSpec, PersonalState, Contract>) this.getMyAgent().
								getMyCore()).getReferenceAllocationComparator());
	}

	//
	// Methods
	//


	@Override
	public Collection<InformedCandidature<Contract, ActionSpec>> getLockedContracts(){
		Collection<InformedCandidature<Contract, ActionSpec>>  lock = new ArrayList<InformedCandidature<Contract,ActionSpec>>(upgradingContracts.keySet());
		lock.addAll(toCancel);
		return lock;
	}

	public Collection<InformedCandidature<Contract, ActionSpec>> getContractToCancel() {
		return toCancel;
	}

	public void addReallocContract(final ReallocationContract<Contract, ActionSpec> realloc){
		assert this.containsAllKey(realloc.getIdentifiers()):this+"\n ---> "+realloc;
		for (final Contract c : realloc) {
			try {
				this.upgradingContracts.add(this.getContract(c.getIdentifier()),realloc);
			} catch (final UnknownContractException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/*
	 *
	 */

	public ReallocationContract<Contract, ActionSpec> getBestReallocationContract(
			final InformedCandidature<Contract, ActionSpec> c){
		if (this.upgradingContracts.get(c).isEmpty()) {
			return null;
		} else {
			return this.upgradingContracts.get(c).last();
		}
	}

	public ReallocationContract<Contract, ActionSpec> getBestRequestableReallocationContract() {
		ReallocationContract<Contract, ActionSpec> finalValue = null;
		Comparator<ReallocationContract<Contract, ActionSpec>> myComp = 
				((InformedCandidatureRationality<ActionSpec, PersonalState, Contract>) this.getMyAgent().
						getMyCore()).getReferenceAllocationComparator();
		for (final InformedCandidature<Contract, ActionSpec> key : this.upgradingContracts.keySet()){
			Iterator<ReallocationContract<Contract, ActionSpec>> itValue = this.upgradingContracts.get(key).descendingIterator();
			while (itValue.hasNext()){
				ReallocationContract<Contract, ActionSpec> sol = itValue.next();
				if (this.isRequestable(sol)){
					if (finalValue==null)
						finalValue=sol;
					else {
						finalValue = myComp.compare(sol,finalValue)>0?sol:finalValue;
					}
					break;
				}
			}
		}
		return finalValue;
	}






	@Deprecated //couteux
	public Collection<ReallocationContract<Contract, ActionSpec>> getReallocationContracts(){
		return this.upgradingContracts.getAllValues();
	}

	public boolean hasReallocationContracts(){
		return !this.upgradingContracts.isEmpty();
	}

	//
	// Primitive
	//

	private boolean isRequestable(final ReallocationContract<Contract, ActionSpec> r){
		assert this.containsAllKey(r.getIdentifiers());
		for (final Contract c : r){
			assert this.getAllContracts().contains(c);
			try {
				if (!c.isMatchingCreation()) {
					if (!this.isRequestable(this.getContract(c.getIdentifier()))) {
						//						logMonologue(r + " is not requestable !! =( because of "+c, AbstractCommunicationProtocol.log_selectionStep);
						return false;
					}
				}
			} catch (final UnknownContractException e) {
				throw new RuntimeException();
			}
		}
		this.logMonologue("CONTRACT TRUNK say \n"+r + "\n ----------------------------------- is requestable yoooouhouuu!! =)", AbstractCommunicationProtocol.log_selectionStep);
		return true;
	}

	//
	// Overrided
	//


	@Override
	public void remove(final  InformedCandidature<Contract, ActionSpec> c) {
		super.remove(c);
		if (toCancel.contains(c))
			toCancel.remove(c);
		final Collection<ReallocationContract<Contract, ActionSpec>> toRemove =
				new ArrayList<ReallocationContract<Contract,ActionSpec>>();
		toRemove.addAll(this.upgradingContracts.get(c));
		for (final ReallocationContract<Contract, ActionSpec> r : toRemove) {
			Collection<InformedCandidature<Contract, ActionSpec>> concernedKeys =
					this.upgradingContracts.removeAvalue(r);
			//adding lost key to cancel
			for (InformedCandidature<Contract, ActionSpec> k : concernedKeys){
				if (k.getInitiator().equals(getMyAgentIdentifier()) && !upgradingContracts.containsKey(k)){
					assert !k.isMatchingCreation();
					toCancel.add(k);
				}
			}

		}
	}

	@Override
	public String toString(){
		return super.toString()+"\n current upgrading contract are : \n "+this.upgradingContracts;
	}
}

//		final LinkedList<ReallocationContract<Contract, ActionSpec>> upCont =
//				new LinkedList<ReallocationContract<Contract, ActionSpec>>(this.upgradingContracts.get(c));
//		if (upCont.isEmpty()) {
//			return null;
//		} else {
//			return Collections.max(upCont,pref);
//		}
//	}
//	@Deprecated //non optimiser
//	public ReallocationContract<Contract, ActionSpec> getBestRequestableReallocationContract(
//			final Comparator<Collection<Contract>> pref){
//		final LinkedList<ReallocationContract<Contract, ActionSpec>> upCont =
//				new LinkedList<ReallocationContract<Contract, ActionSpec>>(this.upgradingContracts.getAllValues());
//		Iterator<ReallocationContract<Contract, ActionSpec>> itUpCont = upCont.iterator();
//		while (itUpCont.hasNext()){
//			if (!this.isRequestable(itUpCont.next()))
//				itUpCont.remove();
//		}
//		try {
//			return Collections.max(upCont,pref);
//		}	catch (NoSuchElementException e){
//			return null;
//		}
//	}
//@Deprecated //non optimiser
//public ReallocationContract<Contract, ActionSpec> getBestRequestableReallocationContract(
//		final Comparator<Collection<Contract>> pref){
//	final LinkedList<ReallocationContract<Contract, ActionSpec>> upCont =
//			new LinkedList<ReallocationContract<Contract, ActionSpec>>(this.upgradingContracts.getAllValues());
//	Iterator<ReallocationContract<Contract, ActionSpec>> itUpCont = upCont.iterator();
//	while (itUpCont.hasNext()){
//		if (!this.isRequestable(itUpCont.next()))
//			itUpCont.remove();
//	}
//	try {
//		return Collections.max(upCont,pref);
//	}	catch (NoSuchElementException e){
//		return null;
//	}
//}

//		Collections.sort(upCont,pref);
//		while (!upCont.isEmpty()) {
//			if (this.isRequestable(upCont.getFirst())) {
//				return upCont.getFirst();
//			} else {
//				upCont.pop();
//			}
//		}
//		return null;

//@Override
//public void addContract(final InformedCandidature<Contract, ActionSpec> c){
//	super.addContract(c);
//	if (getMyAgent().Iaccept(getMyAgent().getMyCurrentState(), c)){
////		addAcceptation(getMyAgentIdentifier(), c);
//		this.upgradingContracts.add(c,new ReallocationContract<Contract, ActionSpec>(c.getInitiator(), c.getCandidature()));
//	}
//}


//
//@Override
//public void addRejection(final AgentIdentifier id,
//		final InformedCandidature<Contract, ActionSpec> c) {
//	super.addRejection(id, c);
////	for (ReallocationContract<Contract, ActionSpec> r : upgradingContracts.get(c)){
////		//			System.err.println("yooooooooooo111111111111111111111111111"+r.getIdentifiers());
////		upgradingContracts.removeAvalue(r);
////	}
//}


//public void addReallocContract(final ReallocationContract<Contract, ActionSpec> realloc){
//	assert this.containsAllKey(realloc.getIdentifiers());
//	//		if (!this.myLocalOptimisations.contains(realloc.getIdentifier()))
//	this.myLocalOptimisations.addContract(realloc);
//	for (final Contract c : realloc)
//		try {
//			this.getContract(c.getIdentifier()).getPossibleContracts().add(realloc);
//		} catch (final UnknownContractException e) {
//			throw new RuntimeException(e);
//		}
//}
//	//
//	// Overrided
//	//
//
//	/*
//	 *
//	 */
//
//
//	@Override
//	public void addContract(final InformedCandidature<Contract, ActionSpec> c) {
//
//	}
//
//
//
//
//
//
//
//
//
//
//
//
//
//	@Override
//	public void addContract(final InformedCandidature<Contract, ActionSpec> c) {
//		for (final ReallocationContract<Contract, ActionSpec> r : c.getPossibleContracts()){
//			this.myLocalOptimisations.addContract(r);
//		}
//		super.addContract(c);
//		for (final ReallocationContract<Contract, ActionSpec> r : c.getPossibleContracts()){
//			for (final Contract c2 : r)
//				if (!c2.equals(c))
//					try {
//						this.getContract(c2.getIdentifier()).getPossibleContracts().add(r);
//					} catch (final UnknownContractException e) {
//						throw new RuntimeException(e);
//					}
//		}
//	}
//
//	@Override
//	public void addAcceptation(final AgentIdentifier id,
//			final InformedCandidature<Contract, ActionSpec> c) {
//		for (final ReallocationContract<Contract, ActionSpec> r : c.getPossibleContracts()){
//			this.myLocalOptimisations.addAcceptation(id,r);
//		}
//		super.addAcceptation(id, c);
//	}
//
//	@Override
//	public void addRejection(final AgentIdentifier id,
//			final InformedCandidature<Contract, ActionSpec> c) {
//		for (final ReallocationContract<Contract, ActionSpec> r : c.getPossibleContracts()){
//			this.myLocalOptimisations.remove(r);
//		}
//		super.addRejection(id, c);
//	}
//
//	@Override
//	public void removeRejection(final AgentIdentifier id, final InformedCandidature<Contract, ActionSpec> c) {
//		for (final ReallocationContract<Contract, ActionSpec> r : c.getPossibleContracts()){
//			this.myLocalOptimisations.removeRejection(id,r);
//		}
//		super.removeRejection(id, c);
//	}
//
//	/*
//	 *
//	 */
//
//	@Override
//	public void remove(final  InformedCandidature<Contract, ActionSpec> c) {
//		for (final ReallocationContract<Contract, ActionSpec> r :this.myLocalOptimisations.getAllContracts()){
//			if (r.getAllParticipants().contains(c.getAgent()))
//				this.myLocalOptimisations.remove(r);
//		}
//
//		//		for (final ReallocationContract<Contract, ActionSpec> r : c.getPossibleContracts()){
//		//			this.myLocalOptimisations.remove(r);
//		//		}
//		super.remove(c);
//	}
//
//
//	@Override
//	public void clear() {
//		super.clear();
//		this.myLocalOptimisations.clear();
//	}
//}



//public ReallocationContract<Contract, ActionSpec> getBestRequestable(
//		final InformedCandidature<Contract, ActionSpec> c,
//		final Comparator<Collection<Contract>> pref){
//	final Iterator<ReallocationContract<Contract, ActionSpec>> itPossible =
//			c.getPossibleContracts().iterator();
//	assert itPossible.hasNext():"initialisé!!";
//	ReallocationContract<Contract, ActionSpec> max = itPossible.next();
//	for (final ReallocationContract<Contract, ActionSpec> r : c.getPossibleContracts()){
//		final ReallocationContract<Contract, ActionSpec> neo = itPossible.next();
//		max = pref.compare(neo, max)>1?neo:max;
//	}
//	return max;
//}
//@Override
//public boolean isRequestable(final InformedCandidature<Contract, ActionSpec> c) {
//	for (final ReallocationContract<Contract, ActionSpec> realloc : c.getPossibleContracts())
//		if (this.myLocalOptimisations.isRequestable(realloc)){
//			return true;
//		}
//	return false;
//}

//@Override
//public boolean isAFailure(final InformedCandidature<Contract, ActionSpec> c) {
//	if (super.isAFailure(c))
//		return true;
//	else {
//		boolean everyOneFailed = true;
//		for (final ReallocationContract<Contract, ActionSpec> realloc : c.getPossibleContracts()){
//			if (!this.myLocalOptimisations.isAFailure(realloc))
//				everyOneFailed=false;
//		}
//		return everyOneFailed;
//	}
//}
//			for (final Contract c2 : r)
//				try {
//					this.getContract(c2.getIdentifier()).getPossibleContracts().remove(r);
//				} catch (UnknownContractException e) {
//					throw new RuntimeException(e.toString()+" \n realloc "+r+"\n base "+this);
//				}