/*
 * APIClientExample
 * 
 * This is an example of how to use JCAT functionality in a client app.
 */
package mil.af.rl.jcat.integration.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;

import org.dom4j.DocumentException;

import mil.af.rl.jcat.exceptions.DuplicateNameException;
import mil.af.rl.jcat.exceptions.UnknownGUIDException;
import mil.af.rl.jcat.plan.Event;
import mil.af.rl.jcat.plan.Mechanism;
import mil.af.rl.jcat.processlibrary.SignalType;
import mil.af.rl.jcat.util.Guid;


public class APIClientExample
{

	public static void main(String[] args)
	{
		Control control = Control.getInstance(); // mil.af.rl.jcat.integration.api.Control is the main functional class for the JCAT API
		
		try{
			
			File planFile = new File("civil_war.jcat"); //<-- CHANGE TO PROPER PATH
			
			// First, use an instance of Control to open a JCAT plan, this will add it to the
			// active plan registry and return the ID associated with the plan
			Guid plan_id = control.openPlan(planFile);
			
			// Alternatively you may create a new empty plan to work with
			// Guid plan_id = control.newPlan();
	
			// Now we can build and sample the plan
			System.out.println("Plan loaded, building/sampling...");
			control.buildPlan(plan_id, 10);
			
			// Chill out for a bit to allow some samples to collect
			Thread.sleep(5000);
			System.out.println("Samples collected: "+control.getSampleCount(plan_id)+" \n");
	
			// Get and print resulting predicted probabilities for all Events in the plan
			for(Guid eID : control.getAllEvents(plan_id).keySet()) //<-- for Mechanisms use getAllMechanisms()
			{
				Event e = (Event)control.getItem(plan_id, eID);
				System.out.println("Event: " + e.getName());
				System.out.println("\t Causes: " + e.getCauses());
				System.out.println("\t Inhibitors: " + e.getInhibitors());
				double[] prob = control.getPredicted(plan_id, e.getGuid()); //<-- for inferred probabilities use getInferred()
				for(int x=0; x<prob.length; x++)
					System.out.println("\t Predicted probability (time: "+x+"): " + prob[x]);
			}
			
			// Stop the sampler for now
			// Note:  each plan will contain a BayesNet once the plan is build with buildPlan() and
			// the BayesNet contains its own sampling thread which can be stopped at any time
			control.stopSampler(plan_id);
	
			// Now lets create a new event, followed by a mechanism to connect it to an existing event
			// Both strings are abitrary and optional identifiers, you may use empty string for either
			Event genocideEvent = new Event(new Guid(), "Genocide", "Sunnis");
			// Add the new Event to the Plan
			control.addEvent(genocideEvent, plan_id);
	
			// Now we will get and existing event (Civil War) from the plan, we need to know the Events GUID
			// The client is responsible for keeping track of these IDs, here we have one hardcoded for the civil_war example
			Guid civilWarID = new Guid("439abfdd-9b3d-4285-a74e-db87062801bc");
			Event civilWarEvent = (Event) control.getPlan(plan_id).getItem(civilWarID);
			
			// Now create the mechanism that will connect the new Genocide Event to Civil War
			Mechanism revengeMech = new Mechanism(new Guid(), "Revenge Killings", genocideEvent, civilWarEvent);
			// Add the new Mechanism to the Plan, specifying which type of effect it will have on the 'to' Event
			// In JCAT a mechanism may either cause or inhibit the occurance of an Event
			// Use SignalType.CAUSAL or SignalType.INHIBITING
			control.addMechanism(revengeMech, SignalType.CAUSAL, plan_id);
	
			// To elicit a probability for a particular signal in an Event do the following 
			// - the Event and Mechanism must exist in the Plan
			// - probability value should be between 0 and 1
			// - for protocol use SignalType.RNOR (Recursive-Noise-OR) as it is currently the only implemented protocol in JCAT
			control.setSingleElicitedValue(plan_id, civilWarEvent.getGuid(), revengeMech.getGuid(), .22f, SignalType.RNOR);
			
			// Now we can build the Plan again and see the new probabilities
			System.out.println("\n\nBuilding/Sampling with new items...");
			control.buildPlan(plan_id, 10);
			
			// Chill out for a bit to allow some samples to collect
			Thread.sleep(5000);
			System.out.println("Samples collected: "+control.getSampleCount(plan_id)+" \n");
	
			// Get and print resulting predicted probabilities for all Events in the plan
			for(Guid eID : control.getAllEvents(plan_id).keySet()) //<-- for Mechanisms use getAllMechanisms()
			{
				Event e = (Event)control.getItem(plan_id, eID);
				System.out.println("Event: " + e.getName());
				System.out.println("\t Causes: " + e.getCauses());
				System.out.println("\t Inhibitors: " + e.getInhibitors());
				double[] prob = control.getPredicted(plan_id, e.getGuid()); //<-- for inferred probabilities use getInferred()
				for(int x=0; x<prob.length; x++)
					System.out.println("\t Predicted probability (time: "+x+"): " + prob[x]);
			}
			
			// Stop the sampler
			control.stopSampler(plan_id);
			
			// Optionally use Control to write the Plan to file in the XML format of a typical JCAT file 
			// If you choose to write "false" graphical information with the file you will be able to open you Plan file in JCAT 
			control.savePlan(plan_id, new File(planFile.getPath() + "_api"), true, true);
			
			System.out.println("\nPlan saved to new file!");
			
		}catch(MalformedURLException exc)
		{
			System.err.println("MalformedURLException caught:  ");
			exc.printStackTrace();
		}
		catch(FileNotFoundException exc)
		{
			System.err.println("FileNotFoundException caught:  ");
			exc.printStackTrace();
		}
		catch(DocumentException exc)
		{
			System.err.println("DocumentException caught:  "+exc.getMessage());
			exc.printStackTrace();
		}
		catch(DuplicateNameException exc)
		{
			System.err.println("DuplicateNameException caught:  "+exc.getMessage());
			exc.printStackTrace();
		}
		catch(UnknownGUIDException exc)
		{
			System.err.println("UnknownGUIDException caught:  "+exc.getMessage());
			exc.printStackTrace();
		}
		catch(InterruptedException exc)
		{
			System.err.println("InterruptedException caught:  ");
			exc.printStackTrace();
		}
		catch(Exception exc)
		{
			System.err.println("Exception caught:  "+exc.getMessage());
			exc.printStackTrace();
		}
	}
}
