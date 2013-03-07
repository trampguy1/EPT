package mil.af.rl.jcat.integration.soa.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.http.HttpSession;

import mil.af.rl.jcat.integration.api.Control;
import mil.af.rl.jcat.plan.AbstractPlan;
import mil.af.rl.jcat.util.Guid;


public final class SessionManager implements Runnable
{

	private static SessionManager instance;
	private HashMap<HttpSession, Control> controllers = new HashMap<HttpSession, Control>();
	private boolean running = false;
	private long cleanupDelay = 1000 * 60; // 60 sec
	private int sessionTimeout = 60 * 30; // 30 min
	
	
	public static SessionManager getInstance()
	{
		if(instance == null)
			instance = new SessionManager();

		return instance;
	}

	private SessionManager()
	{
		running = true;
		new Thread(this).start();
	}

	public Control createController(HttpSession session)
	{
		Control newControl = new Control(); // not a singleton
		controllers.put(session, newControl);
		
		session.setMaxInactiveInterval(sessionTimeout);

		return newControl;
	}

	public void destroyController(HttpSession session)
	{
		Control controller = getController(session);
		if(controller != null)
		{
			for(AbstractPlan p : controller.getPlans().values())
				p.cleanup(); // sampler will be killed here
			controller.getPlans().clear();
		}
		controllers.remove(session);
	}

	public Control getController(HttpSession session)
	{
		return controllers.get(session);
	}

	public Set<HttpSession> getSessions()
	{
		return controllers.keySet();
	}
	
	public void removeExpired()
	{
		ArrayList<HttpSession> toRemove = new ArrayList<HttpSession>();
		for(HttpSession s : controllers.keySet())
			if(SessionManager.isExpired(s))
				toRemove.add(s);
		for(HttpSession s : toRemove)
		{
			destroyController(s);
			try{
				s.invalidate();
			}catch(IllegalStateException exc){
				//dun care
			}
		}
	}
	
	public void setTimeout(int minutes)
	{
		sessionTimeout = minutes * 60;
	}
	
	public void destroy()
	{
		running = false;
		
		for(HttpSession s : SessionManager.getInstance().getSessions())
			SessionManager.getInstance().destroyController(s);		
	}
	
	
	public void run()
	{
		while(running)
		{
			try{
				// removed expired controls
				removeExpired();
				
				Thread.sleep(cleanupDelay);	
			}catch(InterruptedException exc){
				// who cares
			}
			
		}
	}
	
	public static boolean isExpired(HttpSession session)
	{
		try{
			if(session.getLastAccessedTime() + (session.getMaxInactiveInterval() * 1000) < System.currentTimeMillis())
				return true;
			else
				return false;
		}catch(IllegalStateException exc){
			return true;
		}
	}

	
}
