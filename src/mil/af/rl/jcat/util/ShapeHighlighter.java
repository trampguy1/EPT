package mil.af.rl.jcat.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.Timer;

import mil.af.rl.jcat.control.Control;
import mil.af.rl.jcat.gui.MainFrm;

import com.c3i.jwb.JWBController;
import com.c3i.jwb.JWBPanel;
import com.c3i.jwb.JWBShape;

/*
 * This class blinks the shape specified to show the user which shape is being edited
 * -MikeyD
 */
public class ShapeHighlighter implements ActionListener
{
	public static final int VISIBILITY = 0;
	public static final int ALPHA = 1;
	public static final int COLOR = 2;
	public static final int SHADOW = 3;
	private int mode = 0;
	private Timer flashTimer = new Timer(750, this);
	private Vector theShapes = new Vector();
	private JWBPanel panel = null;
	private JWBController controller = null;
	private boolean cycle = false;
	private final Object shapeLock = new Object();
	private int adjustment = 80;
	
	public ShapeHighlighter(int inMode)
	{
		this();
		mode = inMode;
	}
	
	public ShapeHighlighter(JWBShape shape, int inMode)
	{
		this();
		mode = inMode;
		theShapes.add(shape);
	}
	
	//input vector is for initial shapes only, must use add to add more
	public ShapeHighlighter(Vector shapeList, int inMode)
	{
		this();
		mode = inMode;
		theShapes.addAll(shapeList);
		flashTimer.start();
	}

	private ShapeHighlighter() //default constructor starts based on user pref highlight option
	{
		panel = MainFrm.getInstance().getActiveView().getPanel();
		controller = Control.getInstance().getController(MainFrm.getInstance().getSelectedPlan());
		
		if(MainFrm.getInstance().getHighlightEnabled())
			flashTimer.start();
	}

	public void add(Object shape)
	{
		synchronized(shapeLock)
		{
			stop();
			theShapes.add(shape);
			flashTimer.start();
		}
	}
	
	public void actionPerformed(ActionEvent e)
	{
		synchronized(shapeLock)
		{
			doShapes(false);
		}
	}
	
	//toggle the state of shapes depending on the mode 
	private void doShapes(boolean reset)
	{
		if(reset && !cycle) //if we say off and shape already off (not highlighted), forget it 
			return;
		
		for(Object shape : theShapes)
		{
			try{
				JWBShape thisShape = (JWBShape)shape;
				if(mode == ShapeHighlighter.COLOR)
				{
					if(cycle)
						thisShape.setColor(new java.awt.Color(thisShape.getColor().getRGB()+adjustment));
					else
						thisShape.setColor(new java.awt.Color(thisShape.getColor().getRGB()-adjustment));
				}
				else if(mode == ShapeHighlighter.ALPHA)
				{
					java.awt.Color curColor = thisShape.getColor();
					if(cycle)
						thisShape.setColor(new java.awt.Color(curColor.getRed(), curColor.getGreen(), curColor.getBlue(), curColor.getAlpha()+adjustment));
					else
						thisShape.setColor(new java.awt.Color(curColor.getRed(), curColor.getGreen(), curColor.getBlue(), curColor.getAlpha()-adjustment));
				}
				else if(mode == ShapeHighlighter.VISIBILITY)
				{
					if(cycle)
						thisShape.setHidden(false);
					else
						thisShape.setHidden(true);
				}
				else if(mode == ShapeHighlighter.SHADOW)
				{
					if(cycle)
						thisShape.setShadow(false);
					else
						thisShape.setShadow(true);
				}
			}catch(IllegalArgumentException exc) //make a change to adjustment factor if adjustment would be too much
			{
				if(adjustment-10 >= 0)
					adjustment -= 10;
				else
					adjustment -= (10-adjustment);
			}
		}
		
		cycle = (cycle)?false:true;
		//update the local panel which would happen with a 'put' but we dont want this to collaborate
		panel.updateUI();
	}
	
	public void stop()
	{
		flashTimer.stop();
		doShapes(true); //reset to start state
			
		//try{   controller.putShape(thisShape);   }catch(Exception exc){}
	}
}
