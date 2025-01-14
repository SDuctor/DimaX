package frameworks.negotiation.opinion;

import java.util.Collection;


import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.modules.aggregator.AbstractCompensativeAggregation;
import dima.introspectionbasedagents.modules.aggregator.AbstractMinMaxAggregation;
import dima.introspectionbasedagents.services.information.NoInformationAvailableException;
import dima.introspectionbasedagents.services.information.ObservationService;
import dima.introspectionbasedagents.services.information.ObservationService.Information;

public interface OpinionService
extends ObservationService{

//	public <Info extends Information> Opinion<Info> getOpinion(
//			Class<Info> informationType, AgentIdentifier agentId) throws NoInformationAvailableException;

	public <Info extends Information> Opinion<Info> getGlobalOpinion(
			Class<Info> myInfoType) throws NoInformationAvailableException, NoOpinionHandlerException;
	// public <Info extends Information> void collectInformation(Class<Info>
	// informationType);
	//
	// public <Info extends Information> void receiveInformation(Info o);
	//
	// public <Info extends Information> void collectOpinion(Class<Info>
	// informationType);
	//
	// public <Info extends Information> void receiveOpinion(Opinion<Info> o);



	/*
	 *
	 */

	public interface Opinion<Info extends Information> extends Information {

		public Collection<AgentIdentifier> getAggregatedAgents();

		public boolean isCertain();

		/*
		 *
		 */

		/**
		 * @return represent the heterogeneity of collected agent state
		 */
		public Double getOpinionDispersion();

		/*
		 *
		 */

		/**
		 * @return the minimum time an agent has changed its state
		 */
		public Long getMinInformationDynamicity();

		/**
		 * @return the maximum time an agent has changed its state
		 */
		public Long getMaxInformationDynamicity();

		Info getMaxInfo();

		Info getMeanInfo();

		Info getMinInfo();

	}
}

//
// public interface OpinionService
// extends InformationService{
//
// public <Info extends Information> Info getBelief(Class<Info> informationType,
// AgentIdentifier id)
// throws NoInformationAvailableException;
//
// public <Info extends Information> Float getBeliefConfidence(Info information)
// throws NoInformationAvailableException;
//
// public <Info extends Information> Info getMyOpinion(Class<Info>
// informationType)
// throws NoInformationAvailableException ;
//
// public <Info extends Information> void collectInformation(Class<Info>
// informationType);
//
// }