package mil.af.rl.jcat.gui.table.model.event;


import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableModel;

import mil.af.rl.jcat.gui.table.model.CustomSpinner;
import mil.af.rl.jcat.gui.table.model.CustomSpinnerModel;
import mil.af.rl.jcat.util.MaskedFloat;

/**
 * <p>Title: SpinnerEventListener.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Edward Verenich
 * @version 1.0
 */

public class SpinnerEventListener implements ChangeListener, MouseWheelListener
{
	private TableModel model;
	private int row;
	private int col;
	
	public SpinnerEventListener(TableModel m, int r, int c)
	{
		model = m;
		row = r;
		col = c;
	}
	
	
	public void stateChanged(ChangeEvent e)
	{
		try{
			
			CustomSpinner s = (CustomSpinner)e.getSource();
			MaskedFloat v = (MaskedFloat)s.getValue();
			
			s.commitEdit(); //forces users manually entered value to commit
			
			model.setValueAt(v, row, col);
			
		}catch(Exception exc){ } //cast exception is ok.. why now i forgot?
	}


	public void mouseWheelMoved(MouseWheelEvent e)
	{
		try{
			CustomSpinner s = (CustomSpinner)e.getSource();
			if(e.getUnitsToScroll() < 0)
				s.setValue(((CustomSpinnerModel)s.getModel()).getNextValue());
			else
				s.setValue(((CustomSpinnerModel)s.getModel()).getPreviousValue());
			//needed for 'apply def. prob.' fix	
			s.commitEdit();
		}catch(Exception exc){ }
	}


	
	
}
