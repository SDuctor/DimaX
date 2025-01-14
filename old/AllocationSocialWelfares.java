package negotiation.negotiationframework.rationality;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.AbstractContractTransition;
import negotiation.negotiationframework.contracts.AbstractContractTransition.IncompleteContractException;
import negotiation.negotiationframework.contracts.ReallocationContract;
import negotiation.negotiationframework.rationality.SocialChoiceFunctions.UtilitaristEvaluator;
import dima.basicagentcomponents.AgentIdentifier;
import dima.support.GimaObject;

public abstract class AllocationSocialWelfares<
ActionSpec extends AbstractActionSpecification,
Contract extends AbstractContractTransition<ActionSpec>> extends GimaObject{
	private static final long serialVersionUID = 5135268337671313960L;

	public final  String socialWelfare;
	//	public final CompetentComponent myAgent;

	public final static String log_socialWelfareOrdering="social welfare ordering";

	//
	//
	//

	public AllocationSocialWelfares(
			//			final CompetentComponent myAgent,
			final String socialWelfare){
		this.socialWelfare=socialWelfare;
		//		this.myAgent = myAgent;
	}

	//
	// Abstract Method
	//

	public abstract Comparator<ActionSpec> getComparator();

	public abstract UtilitaristEvaluator<ActionSpec> getUtilitaristEvaluator();

	//
	// Methods
	//

	public double getUtility(final Collection<Contract> cs){

		try {
			final Collection<ActionSpec> as = this.cleanStates(ReallocationContract.getResultingAllocation(cs));

			if (this.socialWelfare.equals(SocialChoiceFunctions.key4leximinSocialWelfare)) {
				return SocialChoiceFunctions.getMinValue(as,  this.getComparator(), this.getUtilitaristEvaluator());
			} else if (this.socialWelfare.equals(SocialChoiceFunctions.key4NashSocialWelfare)) {
				return SocialChoiceFunctions.getNashValue(as, this.getUtilitaristEvaluator());
			} else if (this.socialWelfare.equals(SocialChoiceFunctions.key4UtilitaristSocialWelfare)) {
				return SocialChoiceFunctions.getUtilitaristValue(as, this.getUtilitaristEvaluator());
			} else {
				throw new RuntimeException("impossible key for social welfare is : "+this.socialWelfare);
			}

		} catch (final IncompleteContractException e) {
			throw new RuntimeException();
		}
	}

	public int getSocialPreference(
			final Collection<Contract> c1,
			final Collection<Contract> c2) {

		try {
			final Map<AgentIdentifier, ActionSpec> initialStates =ReallocationContract.getInitialStates(c1, c2);

			final Collection<ActionSpec> s1 =
					this.cleanStates(ReallocationContract.getResultingAllocation(initialStates, c1));
			final Collection<ActionSpec> s2 =
					this.cleanStates(ReallocationContract.getResultingAllocation(initialStates, c2));

			assert s1.size()==s2.size();

			if (this.socialWelfare.equals(SocialChoiceFunctions.key4leximinSocialWelfare)){
				//			this.myAgent.logMonologue("comparing : \n"+c1+"\n"+c2+"\n"+s1+"\n"+s2,AllocationSocialWelfares.log_socialWelfareOrdering);
				final int pref = SocialChoiceFunctions.leximinWelfare(s1, s2, this.getComparator());
				//			this.myAgent.logMonologue("result is " +pref,AllocationSocialWelfares.log_socialWelfareOrdering);
				return pref;
			} else if (this.socialWelfare.equals(SocialChoiceFunctions.key4NashSocialWelfare)) {
				return SocialChoiceFunctions.nashWelfare(s1, s2, this.getUtilitaristEvaluator());
			} else if (this.socialWelfare.equals(SocialChoiceFunctions.key4UtilitaristSocialWelfare)) {
				return SocialChoiceFunctions.utilitaristWelfare(s1, s2, this.getUtilitaristEvaluator());
			} else {
				throw new RuntimeException("impossible key for social welfare is : "+this.socialWelfare);
			}
		} catch (final IncompleteContractException e) {
			throw new RuntimeException();
		}
	}

	protected Collection<ActionSpec> cleanStates(
			final Collection<ActionSpec> res) {
		return res;
	}
}






//try {
//	meAsMap.put(id, c.computeResultingState(meAsMap.get(id)));
//} catch (RuntimeException e) {
//	//					System.err.println("yyyyyyyyoooooooooooooo "+id+" "+c+" \n **** all alloc : "+alloc
//	//							+"\n **** current result : "+meAsMap+" ------ "+c.getAllParticipants());
//	getResultingAllocationFACTIS(initialStates,alloc);
//	throw e;
//} -->//public static <ActionSpec extends AbstractActionSpecification,Contract extends AbstractContractTransition<ActionSpec>>
//Collection<ActionSpec> getResultingAllocationFACTIS(
//		Map<AgentIdentifier, ActionSpec> initialStates,
//		Collection<Contract> alloc){
//	Map<AgentIdentifier, ActionSpec> meAsMap =
//			new HashMap<AgentIdentifier, ActionSpec>();
//	meAsMap.putAll(initialStates);
//	System.err.println("yyyyyyyyoooooooooooooo ");
//	System.err.flush();
//	System.err.println("\n\n\n\n**********************\n\n");
//	System.err.flush();
//	System.out.println("initial!!! :\n"+meAsMap);
//	System.out.flush();
//	for (Contract c : alloc){
//		System.out.println("\n anlysing : \n *"+c);
//		for (AgentIdentifier id : c.getAllParticipants())
//			try {
//				System.out.flush();
//				System.out.println(" ---> paticipant "+id);
//				System.out.flush();
//				System.out.println(" ---> c spec = "+c.getSpecificationOf(id));
//				System.out.flush();
//				System.out.println("\n initially :"+meAsMap.get(id));
//				System.out.flush();
//				System.out.println("\n finally :"+c.computeResultingState(meAsMap.get(id)));
//				System.out.flush();
//				meAsMap.put(id, c.computeResultingState(meAsMap.get(id)));
//			} catch (RuntimeException e) {
//				//					System.err.println("yyyyyyyyoooooooooooooo "+id+" "+c+" \n **** all alloc : "+alloc
//				//							+"\n **** current result : "+meAsMap+" ------ "+c.getAllParticipants());
//
//				throw e;
//			}
//	}
//
//	System.err.println("\n\n\n\n**********************\n\n");
//	return meAsMap.values();
//}