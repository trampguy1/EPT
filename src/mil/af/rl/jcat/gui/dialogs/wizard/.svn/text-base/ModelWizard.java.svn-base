/*
 * Created on Sep 15, 2005
 * Author: MikeyD
 */
package mil.af.rl.jcat.gui.dialogs.wizard;

import java.awt.Frame;
import java.awt.Point;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import com.c3i.jwb.JWBController;
import com.c3i.jwb.JWBLine;
import com.c3i.jwb.JWBShape;
import com.c3i.jwb.JWBUID;
import com.c3i.jwb.shapes.JWBRoundedRectangle;

import mil.af.rl.jcat.control.Control;
import mil.af.rl.jcat.control.PlanArgument;
import mil.af.rl.jcat.control.RemSignalArg;
import mil.af.rl.jcat.exceptions.PlotException;
import mil.af.rl.jcat.gui.MainFrm;
import mil.af.rl.jcat.plan.AbstractPlan;
import mil.af.rl.jcat.plan.Event;
import mil.af.rl.jcat.plan.Mechanism;
import mil.af.rl.jcat.plan.PlanItem;
import mil.af.rl.jcat.processlibrary.Signal;
import mil.af.rl.jcat.processlibrary.SignalType;
import mil.af.rl.jcat.util.Guid;
import mil.af.rl.jcat.util.MaskedFloat;

public class ModelWizard extends WizardDialog
{
	private static final long serialVersionUID = 1L;
	private JWBController controller;
	private AbstractPlan plan;
	private JWBShape from;
	private JWBShape to;
	private ArrowWindow arrow;
	private Frame parent;
	private static Logger logger = Logger.getLogger(ModelWizard.class);
	
	// eventual goal:
	// try to add a syntax checker to ensure proper causal modeling from users input
	
	private static String[] titles = 
	{
		"Welcome to the JCAT model assistant!", //0
		"Start a new model",
		"Create an event node", //2
		"Create another event",
		"Create a mechanism",  //4
		"Assign timing",
		"Sample the model",  //6
		"Plot an event",
		
		"Finished!" //end
	};
	
	private static String[] messages = 
	{
		"This wizard will guide you through creating a causal model using this powerful tool. \n\n" +
		"The yellow arrow will indicate key points along the way.",  //0
		
		"The wizard will now start a new model for you.  You can do this later by clicking on the New Model icon in " +
		"the top toolbar or by using the File menu.",
		
		"You can create an Event by using the Model toolbar to the right.  Simply click the new Event button " +
		"and then click somewhere on the canvas.  The wizard will create and Event for you with the name given " +
		"below.  A name of an Event should always describe something that could happen (an event that could take " +
		"place in the 'real world').",  //2
		
		"Now you must create another event which would be caused by the first.  This will be the event " +
		"which we wish to do the causal analysis on.  We want to find out the likelihood of this event " +
		"occurring at various times.",
		
		"Now its time to connect these two events with a mechanism.  You could do this by using the mechanism " +
		"tool on the model toolbar to the right, directly below the event button.  The mechanism will contain " +
		"a reusable signal which stands for the effect an event has on the other event which it is connected to." +
		"",  //4
		
		"Because JCAT is a temporal based tool, there is now the timing of your events to consider.  In this case " +
		"we will set some timing and scheduling on the first event.  Time for your model is based on arbitrary time " +
		"ticks which can represent any length of time you wish.  To set timing you would right click on the event " +
		"and select Timing from the menu.  For more information on timing click the more info button.  " +
		"For now we will set your event to occur at time tick four with a 60% likelihood.",
		
		"That's all you need to do to see some results from a very simplistic causal model.  Now we will " +
		"start the Bayes sampler which will compute the necessary data.  To start the sampler you would " +
		"click on the start sampler button on the model toolbar.",  //6
		
		"Now that the sampler is running we can plot any or all of the events in the model on the graph to analyze " +
		"the results.  To plot an event you would simply double click on it and the probabilities profile graph will " +
		"be updated.",
		
		"That's it, you've created a simple causal model. \n\nTo save your model click on the Save button located on " +
		"the top toolbar or use the File menu.  To continue your venture in learning all that is JCAT, try out the " +
		"JCAT Tutorial located in the Help menu." //end
	};
	
	Vector[] options =
	{
		initOptions(0),  //0
		initOptions(1),
		initOptions(2),  //2
		initOptions(3),
		initOptions(4),  //4
		initOptions(5),
		initOptions(6),  //6
		initOptions(7),
		initOptions(8)  //end
	};
	
	
	//create options for each page
	public Vector initOptions(int x)
	{
		Vector opts = new Vector();
		
		if(x == 2 || x == 3 || x == 4)
		{
			opts.add(new JTextField());
			if(x != 4)
			{
				JButton examp = new JButton("More Info");
				examp.setActionCommand("event");
				examp.addActionListener(new MoreInfoBox(this));
				opts.add(examp);
			}
			else
			{
				JButton examp = new JButton("More Info");
				examp.setActionCommand("mechanism");
				examp.addActionListener(new MoreInfoBox(this));
				opts.add(examp);
			}
		}
		
		else if(x == 5)
		{
			JButton timingInfo = new JButton("More info");
			timingInfo.setActionCommand("timing");
			timingInfo.addActionListener(new MoreInfoBox(this));
			opts.add(timingInfo);
		}
		return opts;
	}
	
	public ModelWizard(java.awt.Frame prnt)
	{
		super(prnt, new java.awt.Dimension(590, 335), titles, messages, false, "Model Wizard");
		//setTitles(titles);
		//setMessages(messages);
		setModal(true);
		setOptions(options);
		parent = prnt;
		
		arrow = new ArrowWindow(this.getClass().getClassLoader().getResource("wiz_arrow.png"), parent);
		
		setVisible(true);
	}
	
	//perform functions for the current page
	public void nextPressed()
	{
		if(getCurrentTitle().equals(titles[1]))
		{
			MainFrm.getInstance().createDocument(); //AbstractPlan.STANDARD_DEFAULTS_SET);
			controller = Control.getInstance().getController(MainFrm.getInstance().getSelectedPlan());
			plan = Control.getInstance().getPlan(MainFrm.getInstance().getSelectedPlan());
		}
			
		else if(getCurrentTitle().equals(titles[2])) //event1
			from = createEvent(100, 250);
		else if(getCurrentTitle().equals(titles[3])) //event2
			to = createEvent(100, 50);
		else if(getCurrentTitle().equals(titles[4]))  //mech
			createMech();
		else if(getCurrentTitle().equals(titles[5]))  //timing
			createTiming();
		else if(getCurrentTitle().equals(titles[6]))  //sample
			startSample();
		else if(getCurrentTitle().equals(titles[7]))  //plot
			plotEvent();
	}

	public void backPressed()
	{
		
	}

	public void cancelPressed()
	{
		dispose();
	}

	public void pageChanged(int currentPage)
	{
		if(currentPage == 1) //new plan
			arrow.pointTo((JButton)MainFrm.getInstance().getFileToolBar().getButtons()[0], 90, false);
		else if(currentPage == 2 || currentPage == 3)  //event
			arrow.pointTo((JButton)MainFrm.getInstance().getCatToolBar().getButtons()[1], 0, false);
		else if(currentPage == 4)  //mech
			arrow.pointTo((JButton)MainFrm.getInstance().getCatToolBar().getButtons()[2], 0, false);
		else if(currentPage == 6)  // sample
			arrow.pointTo((JButton)MainFrm.getInstance().getCatToolBar().getButtons()[4], 0, false);
		else if(currentPage == 7)  //shape plot
		{
			Point panelLoc = MainFrm.getInstance().getActiveView().getPanel().getLocationOnScreen();
			arrow.pointTo(new Point(panelLoc.x+to.getLocation().x-arrow.getSize().width, panelLoc.y+to.getLocation().y+10), 0);
		}
		else if(currentPage == 8)
			arrow.pointTo((JButton)MainFrm.getInstance().getFileToolBar().getButtons()[2], 90, false);
		else
			arrow.setVisible(false);
	}
	
	public JWBShape createEvent(int x, int y)
	{
		String firstOptText = ((JTextField)getCurrentOptions().firstElement()).getText();		
		try{
			JWBRoundedRectangle shape = new JWBRoundedRectangle(new java.awt.Point(x,y), 80, 80, new JWBUID());
			Event event = new Event(new Guid(), firstOptText, "Undefined", plan.getLibrary().createProcess(plan.getDefaultProbSet(), -1));
			shape.setText(firstOptText);
			shape.setAttachment(event.getGuid());
//			plan.addItem(event, shape.getUID());
			
			controller.putShape(shape);
			PlanArgument arg = new PlanArgument(PlanArgument.ITEM_ADD, event, shape.getUID());
			arg.setIsAutomated(true);
			controller.foreignUpdate(arg);
			
			return shape;
		}catch(Exception exc){
			logger.error("createEvent - Error creating new event:  "+exc.getMessage());
			return null;
		}
	}
	
	private void createMech()
	{
		String firstOptText = ((JTextField)getCurrentOptions().firstElement()).getText();
		try{
			//create a new signal and mechanism
			Signal signal = new Signal(new Guid(), firstOptText);
			Mechanism mech = new Mechanism(new Guid(), firstOptText, (Event)plan.getItem((Guid)to.getAttachment()), (Event)plan.getItem((Guid)from.getAttachment()), signal.getSignalID());
			mech.setLoopCloser(false);
//			ArrayList<Guid> guids = new ArrayList<Guid>(1);
//			guids.add(signal.getSignalID());
            
			//tell controller about (adds things to the library)
//			RemSignalArg effectArg = new RemSignalArg(RemSignalArg.ADD_EFFECT, guids, ((Event)plan.getItem((Guid)from.getAttachment())).getProcessGuid());
//			RemSignalArg causeArg = new RemSignalArg(RemSignalArg.ADD_CAUSE, guids, ((Event)plan.getItem((Guid)to.getAttachment())).getProcessGuid());
//			RemSignalArg signalArg = new RemSignalArg(RemSignalArg.ADD,signal);
//			controller.foreignUpdate(signalArg);
//			controller.foreignUpdate(effectArg);
//			controller.foreignUpdate(causeArg);
			
			//put it on the whiteboard as a shape
			JWBLine newLine = new JWBLine(from, to, new JWBUID(), false);
//			newLine.setAttachment(mech.getGuid());
//			controller.putShape(newLine);
			
			//put it in the abstractplan as a planitem
//			controller.foreignUpdate(new PlanArgument(PlanArgument.ITEM_ADD, mech, newLine.getUID()));
			//use control to handle the mech stuff, it does errthing... how handy
			Control.getInstance().addMechanism(newLine, mech, signal, SignalType.CAUSAL, controller, plan);
			
		}catch(Exception exc){
			logger.warn("createMech - Error creating a mechanism:  "+exc.getMessage());
		}
	}

	private void createTiming()
	{
		TreeMap schedule = new TreeMap();
		schedule.put(new Integer(4), MaskedFloat.getMaskedValue(.6f));
		PlanItem item = plan.getItem((Guid)from.getAttachment());
		item.setSchedule(schedule);
//		from.addMarkup('S');
		
		try{
			controller.foreignUpdate(new PlanArgument(PlanArgument.ITEM_UPDATE, item, false));
		}catch(RemoteException exc){
			logger.warn("createTiming - RemoteExc creating timing:  "+exc.getMessage());
		}
	}

	private void startSample()
	{
		try{
			plan.buildBayesNet(10);
			// this may be lame but couldn't think of anything else todo
			// this is the only reference to MainFrm import
			if(parent instanceof MainFrm)
				((MainFrm)parent).startUpdateThread();
			
		}catch(Exception exc){
			logger.warn("startSample - Error starting sampler or status timer:  "+exc.getMessage());
		}		
	}

	private void plotEvent()
	{
		PlanItem item = plan.getItem((Guid)to.getAttachment());
		//item.setPredictedProbs(plan.getInferredProbs(new Guid(item.getGuid())));
		try{
			mil.af.rl.jcat.gui.ProfileDataModel.getInstance().addPlot(plan, item);
		}catch(PlotException e){
			logger.error("plotEvent - unable to add plot:  "+e.getMessage());
		}
		//make sure profile graphs is showing
		MainFrm.getInstance().getDockingManager().showFrame("Probability Profiles");
		MainFrm.getInstance().getDockingManager().showFrame("Probability Profiles");
	}

	public void dispose()
	{
		if(arrow != null)
			arrow.dispose();
		super.dispose();
	}


}
