/*
 * AutoSaveAgent - saves plan files to a temp location in the background for crash recovery of unsaved plans
 * Author:  MikeyD
 */
package mil.af.rl.jcat.util;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import mil.af.rl.jcat.gui.CatView;
import mil.af.rl.jcat.gui.MainFrm;

public class AutoSaveAgent extends Timer
{
	private int saveDelay = 600000; //(10 min default);
	Task theTask = null;
	private boolean running;
	private static Logger logger = Logger.getLogger(AutoSaveAgent.class);
	
	
	public AutoSaveAgent(boolean autoStart, int delay)
	{
		super("AutoSave", true);
		setDelay(delay);
		if(autoStart)
			start();
	}
	
	public void start()
	{
		if(running)
			stop();
		scheduleAtFixedRate(theTask = new Task(), 5000, saveDelay);
		running = true;
	}
	
	public void stop()
	{
		if(theTask != null)
			theTask.cancel();
		purge();
		running = false;
	}

	public void setDelay(int delay)
	{
		saveDelay = delay*60*1000;
		if(running)
		{
			stop();
			start();
		}
	}
	
	//eeeww an inner class
	private class Task extends TimerTask
	{
		public void run()
		{
			try{
				// TODO: so autosave only saves the active plan, might want to do all open plans
				CatView currentView = MainFrm.getInstance().getActiveView(); 
				if(currentView != null) //no plan open
					currentView.autoSave();
			}catch(NullPointerException exc){
				logger.debug("autoSave-thread - autosave failed this try possibly due to jwb editing taking place");
			} // when autosave happens at exact time an event or mech is being
			// created because jwb attachment isn't there yet possibly..oh well
		}
	}
}
