package dima.introspectionbasedagents.modules.computingFuzzyLogic.implicator;

import dima.introspectionbasedagents.modules.computingFuzzyLogic.controller.FuzzySubSet;

/**
 *
 * @author Sylvain Ductor
 */
public class Mamdami implements Implicateur {

	/**
	 *
	 */
	private static final long serialVersionUID = -4422858200459246795L;

	public Mamdami() {
	}

	@Override
	public FuzzySubSet r(final FuzzySubSet premisse,
			final FuzzySubSet conclusion) {
		return premisse.sousEnsembleInter(conclusion);
	}
}
