package mil.af.rl.jcat.gui.dialogs;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import mil.af.rl.jcat.gui.table.model.CustomSpinner;
import mil.af.rl.jcat.processlibrary.SignalType;
import mil.af.rl.jcat.util.MaskedFloat;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */

public class EventValueListener implements ChangeListener, ItemListener
{
    private EventDialog parent;
    private static Logger logger = Logger.getLogger(EventValueListener.class);

    int smode;

    public EventValueListener(EventDialog dlg, int spinnermode)
    {

        parent = dlg;
        smode = spinnermode;
    }

    public void stateChanged(ChangeEvent e)
    {
        CustomSpinner s = (CustomSpinner) e.getSource();
        Float v = new Float(((MaskedFloat)s.getValue()).floatValue());
        
        if (smode == SignalType.CAUSAL)
        {
            parent.setDefaultCause(v);
        } else if (smode == SignalType.INHIBITING)
        {
            parent.setDefaultInhibit(v);
        } else if (smode == SignalType.EFFECT)
        {
            parent.setDefaultEffect(v);
        }
    }

	public void itemStateChanged(ItemEvent event)
	{
		try{
			JComboBox s = (JComboBox)event.getSource();
			Float v = new Float(((MaskedFloat)s.getSelectedItem()).floatValue());
			
			if (smode == SignalType.CAUSAL)
	        {
	            parent.setDefaultCause(v);
	        } else if (smode == SignalType.INHIBITING)
	        {
	            parent.setDefaultInhibit(v);
	        } else if (smode == SignalType.EFFECT)
	        {
	            parent.setDefaultEffect(v);
	        }
		}catch(ClassCastException exc){
			logger.error("itemStateChanged - ClassCastExc, not a maskedfloat in combobox:  "+exc.getMessage());
		}
		
	}

}

