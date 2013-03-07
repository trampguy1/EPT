package mil.af.rl.jcat.integration.api;


import java.io.File;
import java.rmi.RemoteException;
import java.util.Vector;

import mil.af.rl.jcat.plan.Event;
import mil.af.rl.jcat.plan.Mechanism;
import mil.af.rl.jcat.processlibrary.SignalType;
import mil.af.rl.jcat.util.Guid;
import mil.af.rl.jcat.exceptions.DuplicateNameException;
import mil.af.rl.jcat.exceptions.MissingRequiredFileException;
import mil.af.rl.jcat.exceptions.NoSuchNameException;


public class DogBarkingExample
{

	public static void main(String[] args)
	{
		try
		{
			Control control = Control.getInstance();

			Guid plan_id = control.createPlan(new Guid());

			Event famOutEv = new Event(new Guid(), "Family Out", "");
			Event bowelProbEv = new Event(new Guid(), "Bowel Problem", "Dog");
			Event dogOutEv = new Event(new Guid(), "Dog Outside", "");
			Event lightOnEv = new Event(new Guid(), "Lights On", "");
			Event hearBarkEv = new Event(new Guid(), "Hear Bark", "Dog");

			control.addEvent(famOutEv, plan_id);
			control.addEvent(bowelProbEv, plan_id);
			control.addEvent(dogOutEv, plan_id);
			control.addEvent(lightOnEv, plan_id);
			control.addEvent(hearBarkEv, plan_id);

			Mechanism famToLightMech = new Mechanism(new Guid(), "gone", lightOnEv, famOutEv);
			Mechanism famToDogMech = new Mechanism(new Guid(), "gone dog", dogOutEv, famOutEv);
			Mechanism bowelToDogMech = new Mechanism(new Guid(), "gond", dogOutEv, bowelProbEv);
			Mechanism dogToHearMech = new Mechanism(new Guid(), "gond", hearBarkEv, dogOutEv);

			control.addMechanism(famToLightMech, SignalType.CAUSAL, plan_id);
			control.addMechanism(famToDogMech, SignalType.CAUSAL, plan_id);
			control.addMechanism(bowelToDogMech, SignalType.CAUSAL, plan_id);
			control.addMechanism(dogToHearMech, SignalType.CAUSAL, plan_id);

			//schedule fam out
			control.schedulePlanItem(plan_id, famOutEv.getGuid(), 0, .15f);

			//leak for bowel problem
			bowelProbEv.setLeak(.01f);

			//set probs for lights on
			control.setSingleElicitedValue(plan_id, lightOnEv.getGuid(), famToLightMech.getGuid(), .6f, SignalType.RNOR);
			lightOnEv.setLeak(.05f);

			//set probls for dog out
			//fam out alone
			control.setSingleElicitedValue(plan_id, dogOutEv.getGuid(), famToDogMech.getGuid(), .9f, SignalType.RNOR);
			//bowel prob alone
			control.setSingleElicitedValue(plan_id, dogOutEv.getGuid(), bowelToDogMech.getGuid(), .97f, SignalType.RNOR);
			//fam and bowel
			Vector<Guid> group = new Vector<Guid>();
			group.add(bowelToDogMech.getGuid());
			group.add(famToDogMech.getGuid());
			control.setSingleElicitedValue(plan_id, dogOutEv.getGuid(), group, .99f, SignalType.RNOR);
			//neither (leak)
			dogOutEv.setLeak(.3f);

			//set probs for hear bark
			control.setSingleElicitedValue(plan_id, hearBarkEv.getGuid(), dogToHearMech.getGuid(), .7f, SignalType.RNOR);
			hearBarkEv.setLeak(.01f);

			//Build the net / Start the sampler
			System.out.println("Building net / starting sampler...");
			control.buildPlan(plan_id, 1); //this example has no temporal aspect so just use plan length of 1
			Thread.sleep(6000);

			//Print out some resulting probs that we're interested in
			System.out.println("Probability Dog Out:  " + control.getPredicted(plan_id, dogOutEv.getGuid())[0]); //time 0 for this example
			System.out.println("Probability Family Out:  " + control.getPredicted(plan_id, famOutEv.getGuid())[0]); //time 0 for this example

			control.addSensorEvidence(plan_id, hearBarkEv.getGuid(), 0, true, .25f, .05f);
			//control.addAbsoluteEvidence(plan_id, hearBarkEv.getGuid(), 0, .99f);
			control.addAbsoluteEvidence(plan_id, lightOnEv.getGuid(), 0, .99f);

			control.buildPlan(plan_id, 1);
			Thread.sleep(6000);

			System.out.println("Probability Family Out (Predicted):  " + control.getPredicted(plan_id, famOutEv.getGuid())[0]); //time 0 for this example
			System.out.println("Inferred Probability Family Out (After evidence):  " + control.getInferred(plan_id, famOutEv.getGuid())[0]); //time 0 for this example

			control.savePlan(plan_id, new File("dog_barking_api.jcat"), true, true);

			//stop sampling just for good measure
			control.stopSampler(plan_id);

		}catch(RemoteException e)
		{
			e.printStackTrace();
		}catch(DuplicateNameException e)
		{
			e.printStackTrace();
		}catch(MissingRequiredFileException e)
		{
			e.printStackTrace();
		}catch(NoSuchNameException e)
		{
			e.printStackTrace();
		}catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
