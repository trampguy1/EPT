package mil.af.rl.jcat.gui.table.model.event;


import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.table.TableModel;

import org.apache.log4j.Logger;

import mil.af.rl.jcat.util.MaskedFloat;

/**
 * <p>Title: SpinnerEventListener.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Edward Verenich
 * @version 1.0
 */

public class JComboEventListener implements ItemListener
{
	private TableModel model;
	private int row;
	private int col;
	private static Logger logger = Logger.getLogger(JComboEventListener.class);
	
	public JComboEventListener(TableModel m, int r, int c)
	{
		model = m;
		row = r;
		col = c;
	}
	
	//
	public void itemStateChanged(ItemEvent event)
	{
		try{
			if(event.getStateChange() == ItemEvent.SELECTED)
			{
				JComboBox s = (JComboBox)event.getSource();
				MaskedFloat v = (MaskedFloat)s.getSelectedItem();
				model.setValueAt(v,row, col);
			}
		}catch(ClassCastException exc){
			logger.warn("itemStateChanged - ClassCastExc, not a MaskedFloat in combobox: "+exc.getMessage());
		}
		
	}
	
}
