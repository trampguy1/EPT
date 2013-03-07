/*
 * SOAClientExample
 * A Java client example for use with the JCAT Web Services package
 *
 */

package mil.af.rl.jcat.integration.soa;

import java.util.Map;

import mil.af.rl.jcat.util.Guid;


public class SOAClientExample
{

	public SOAClientExample()
	{
		//ID of Bagdad sample plan to use in this client example
		String samplePlanID = "ba9c3fa9-a99b-4b64-a542-05415e2a6dbf";
		
		// get an instance of soa.Control to use (communicate with a jcws server
		Control client = Control.getInstance(); // mil.af.rl.jcat.integration.soa.Control is the main functional class for JCAT WS
		// client.debugOutput = true; // enable some debug output if you wish

		try{
			
			Map<Guid, String> plans = client.getPlans(); // available plans from the server
			Guid planID = null;
			for(Guid pID : plans.keySet())
				if(pID.toString().equals(samplePlanID))
					planID = pID;
			if(planID == null) // the server doesnt have the sample plan loaded
			{
				System.err.println("The sample plan does not appear to be loaded on the server!  Can't run this example.");
				return;
			}
			
			// if you wish, get the IDs for trigger events of this plan
			// these are root nodes in the plan which you would typically schedule
			// Map<Guid, String> trigs = client.getTriggers(planID);
			
			// for this example we will try 2 different COAs, one will envolve running
			// a rapid troop deployment, other will be calculated slow deployment
			// so here we will use known guids obtained from the jcws website:
			// Heavy Armor Movement   b1f0bf10-b2c6-4eb3-9627-0a9d11a201b5 for COA 1
			//   and
			// SF Deployement   9c342fc7-66ad-4826-8b1c-caca2758023f for COA 2
			
			// now schedule the Event for the first COA at time 1 with the given probability
			client.schedulePlanItem(planID, new Guid("b1f0bf10-b2c6-4eb3-9627-0a9d11a201b5"), 1, .98f);
			
			// now try building the model for 90 time slices
			boolean built = client.buildPlan(planID, 90);
			System.out.println("Plan built: " + built + ", waiting for samples...");
			
			// give the sampler time to run, alternatively use getSampleCount to determine how long you'd like to wait
			Thread.sleep(10000);
			System.out.println("Sample count:  "+client.getSampleCount(planID));
			
			// 9927fb4d-dd11-44b8-83d2-59eb32fbbe7f is the guid for Reduce Casualty Node
			// get the predicted probability output for this Event
			double[] profile = client.getPredicted(planID, new Guid("9927fb4d-dd11-44b8-83d2-59eb32fbbe7f"));
			
			System.out.println("Heavy Armor COA - Reduce Casualty Node:");
			for(int time=0; time<Math.min(10, profile.length); time++) // just print the first 10 as an example 
			{
				System.out.println("\t Time: " + time + "   probability: " + profile[time]);
			}
			
			// remove any schedules from the trigger Events
			client.clearAllTriggerSchedules(planID);
			
			// schedule the Event for the second COA at time 1 with the given probability
			client.schedulePlanItem(planID, new Guid("9c342fc7-66ad-4826-8b1c-caca2758023f"), 1, .98f);
			
			// re-build the model
			built = client.buildPlan(planID, 90);
			System.out.println("Plan built: " + built + ", waiting for samples...");
			
			Thread.sleep(10000);
			System.out.println("Sample count:  "+client.getSampleCount(planID));
			
			profile = client.getPredicted(planID, new Guid("9927fb4d-dd11-44b8-83d2-59eb32fbbe7f"));
			
			System.out.println("Special Forces COA - Reduce Casualty Node:");
			for(int time=0; time<Math.min(10, profile.length); time++) // just print the first 10 as an example 
			{
				System.out.println("\t Time: " + time + "   probability: " + profile[time]);
			}
			
			// be nice and stop the sampler if we don't need it anymore
			client.stopSampler(planID);
			
			// session with server will remain active for continued use (control will refresh the connection periodically)
			// to allow the session to expire naturally (aside from terminating this program) use stopKeepAlive 
			// client.stopKeepAlive();
			
			// or optionally (if your all done) be really nice and let the server know it can immediatly dispose this session
			client.shutdown();
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	
	public static void main(String[] main)
	{
		
		new SOAClientExample();
		
	}

}
