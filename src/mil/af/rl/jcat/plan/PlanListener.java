package mil.af.rl.jcat.plan;

import java.util.EventListener;



public interface PlanListener extends EventListener
{
	
	public void coaListChanged(PlanEvent event);
	
	public void activeCOAChanged(PlanEvent event);
	
	public void itemListChanged(PlanEvent event);
	
	public void colorSchemeChanged(PlanEvent event);
	
	public void documentationChanged(PlanEvent event);
	
	public void bayesNetBuilt(PlanEvent event);
	
	public void saved(PlanEvent event);
	
	public void opened(PlanEvent event);
	
	public void wasModified(PlanEvent event);
}
