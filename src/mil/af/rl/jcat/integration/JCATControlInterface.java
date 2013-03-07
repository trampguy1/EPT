package mil.af.rl.jcat.integration;

import java.util.List;
import java.util.Map;

import mil.af.rl.jcat.exceptions.DuplicateNameException;
import mil.af.rl.jcat.exceptions.UnknownGUIDException;
import mil.af.rl.jcat.plan.Event;
import mil.af.rl.jcat.plan.Mechanism;
import mil.af.rl.jcat.plan.PlanItem;
import mil.af.rl.jcat.util.Guid;



public interface JCATControlInterface
{

//	public HashMap<Guid, AbstractPlan> getPlans();

//	public AbstractPlan getPlan(Guid planID);

//	public Guid createPlan(Guid planid) throws DuplicateNameException;
	
//	public void addPlan(AbstractPlan plan) throws DuplicateNameException;

//	public void removePlan(Guid planID);
	
//	public Guid openPlan(File planFile) throws MalformedURLException, DocumentException, FileNotFoundException, DuplicateNameException;

//	public Guid openPlan(Document doc) throws DuplicateNameException, DocumentException;
	
	public Map<Guid, String> getAllEvents(Guid planID);

	public Map<Guid, String> getAllMechanisms(Guid planID);

//	public void buildPlan(AbstractPlan plan, int timeline) throws Exception;
	
	public int getSampleCount(Guid planID);

	public boolean buildPlan(Guid planID, int time) throws UnknownGUIDException, Exception;

	public void stopSampler(Guid planID);

	public void addAbsoluteEvidence(Guid planID, Guid itemID, int time, double probability) throws UnknownGUIDException;

	public void addSensorEvidence(Guid planID, Guid itemID, int time, boolean wasTrue, double FAR, double MDR) throws UnknownGUIDException;

	public void setSingleElicitedValue(Guid planID, Guid eventID, Guid mechanismID, float probability, int protocol) throws UnknownGUIDException;

	public void setSingleElicitedValue(Guid planID, Guid eventID, List<Guid> mechanismIDs, float probability, int protocol) throws UnknownGUIDException;

//	public void addEvent(Event item, Guid planID) throws UnknownGUIDException;
	
//	public void addMechanism(Mechanism item, int type, Guid planID) throws UnknownGUIDException;

//	public void removePlanItem(PlanItem item, Guid planID) throws UnknownGUIDException;

	public void schedulePlanItem(Guid planID, Guid itemID, int time, float probability) throws UnknownGUIDException;

	public double[] getPredicted(Guid planID, Guid itemID) throws UnknownGUIDException, Exception;

	public double[] getInferred(Guid planID, Guid itemID) throws UnknownGUIDException, Exception;
}
