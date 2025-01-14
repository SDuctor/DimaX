package dima.introspectionbasedagents.modules.computingFuzzyLogic.defuzzyficator;

import dima.introspectionbasedagents.modules.computingFuzzyLogic.controller.FuzzySubSet;

/**
 *
 * @author Sylvain Ductor
 */
public class MaxDesMax implements Defuzzificateur {

	/**
	 *
	 */
	private static final long serialVersionUID = -1755798938501138800L;

	@Override
	public double defuzz(final FuzzySubSet a) {
		return a.valeurMax().getX();
	}

}
