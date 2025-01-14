package negotiation.negotiationframework.exploration.strategic.exploration;

import java.util.Collection;

import negotiation.negotiationframework.contracts.AbstractActionSpecif;
import negotiation.negotiationframework.contracts.AbstractContractTransition;
import dima.basicagentcomponents.AgentIdentifier;
import dima.basicinterfaces.DimaComponentInterface;
import dima.introspectionbasedagents.services.information.NoInformationAvailableException;

public interface AbstractStrategicExplorationModule
<Contract extends AbstractContractTransition<ActionSpec>,
ActionSpec extends AbstractActionSpecif>
extends DimaComponentInterface{//ProposerCore<PersonalState, Action, ActionSpec>{

	Contract getNextContractToPropose(
			AbstractStrategicEvaluationModule<Contract, ActionSpec> myComparator,
			final Collection<AgentIdentifier> knownAgents,
			final Collection<String> knownActions )
					throws NoInformationAvailableException;

}
