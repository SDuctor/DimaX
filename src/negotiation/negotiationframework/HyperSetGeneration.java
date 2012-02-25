package negotiation.negotiationframework;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public abstract class HyperSetGeneration<T> {

	private final Collection<Collection<T>>  hyperset;
	
	/*
	 * 
	 */
	
	public HyperSetGeneration(Collection<T> elems){
		hyperset = generateHyperSet(elems);
		filter(hyperset);
	}
	
	public Collection<Collection<T>> getHyperset() {
		return hyperset;
	}
	
	/*
	 * 
	 */
	
	public abstract boolean toKeep(Collection<T> elem);

	/*
	 * 
	 */
	
	private  Collection<Collection<T>> generateHyperSet(Collection<T> elems){

		final Collection<Collection<T>> result =
				new ArrayList<Collection<T>>();
		final Collection<Collection<T>> toAdd =
				new ArrayList<Collection<T>>();

		for (final T singleton : elems) {
			final List<T> a = new ArrayList<T>();
			a.add(singleton);
			toAdd.add(a);//on ajoute le contrat singleton

			//on ajoute tous les précédent ensemble enrichi avec le singleton 
			for (final Collection<T> alloc : result){
				final List<T> a2= new ArrayList<T>();
				a2.addAll(alloc);
				a2.add(singleton);
				toAdd.add(a2);
			}

			result.addAll(toAdd);
			toAdd.clear();
		}

		return result;
	}


	private  void filter(Collection<Collection<T>> hyperset){
		final Iterator<Collection<T>> r = hyperset.iterator();
		while (r.hasNext())
			if (!toKeep(r.next()))
				r.remove();
		
	}
}
