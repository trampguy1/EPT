

package mil.af.rl.jcat.gui.table.model;

import java.awt.Dimension;
import java.text.*;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerModel;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import mil.af.rl.jcat.gui.table.model.base.PTSignal;

/*
 * Allows for the usage of a CustomSpinnerModel which is needed to allow MaskedFloat to be used
 * -mainly needed because MaskedFloat cannot subclass Float
 * -also allows a signal to be stored here so that the 'default' flag can be cleared if user 
 * manually changes the prob. - used in applying default probabilities in EventEditor
 */


public class CustomSpinner extends JSpinner
{
 
	private static final long serialVersionUID = 1L;

	public static class NumberEditor extends DefaultEditor
	{
		private static final long serialVersionUID = 1L;

		public DecimalFormat getFormat()
		{
			return (DecimalFormat)((NumberFormatter)getTextField().getFormatter()).getFormat();
		}
		
		public CustomSpinnerModel getModel()
		{
			return (CustomSpinnerModel)getSpinner().getModel();
		}
		
		public NumberEditor(CustomSpinner jspinner)
		{
			this(jspinner, new DecimalFormat());
		}
		
		public NumberEditor(CustomSpinner jspinner, String s)
		{
			this(jspinner, new DecimalFormat(s));
		}
		
		private NumberEditor(CustomSpinner jspinner, DecimalFormat decimalformat)
		{
			super(jspinner);
			if(!(jspinner.getModel() instanceof CustomSpinnerModel))
				throw new IllegalArgumentException("model not a CustomSpinnerModel");
			CustomSpinnerModel CustomSpinnerModel = (CustomSpinnerModel)jspinner.getModel();
			NumberEditorFormatter numbereditorformatter = new NumberEditorFormatter(CustomSpinnerModel, decimalformat);
			DefaultFormatterFactory defaultformatterfactory = new DefaultFormatterFactory(numbereditorformatter);
			JFormattedTextField jformattedtextfield = getTextField();
			jformattedtextfield.setEditable(true);
			jformattedtextfield.setFormatterFactory(defaultformatterfactory);
			jformattedtextfield.setHorizontalAlignment(4);
			try
			{
				String s = numbereditorformatter.valueToString(CustomSpinnerModel.getMinimum());
				String s1 = numbereditorformatter.valueToString(CustomSpinnerModel.getMaximum());
				jformattedtextfield.setColumns(Math.max(s.length(), s1.length()));
			}
			catch(ParseException parseexception) { }
		}
	}

	private static class NumberEditorFormatter extends NumberFormatter
	{
		private static final long serialVersionUID = 1L;

		public Comparable getMaximum()
		{
			return model.getMaximum();
		}
		
		public Comparable getMinimum()
		{
			return model.getMinimum();
		}
		
		public void setMaximum(Comparable comparable)
		{
			model.setMaximum(comparable);
		}
		
		public void setMinimum(Comparable comparable)
		{
			model.setMinimum(comparable);
		}
		
		private final CustomSpinnerModel model;
		
		NumberEditorFormatter(CustomSpinnerModel CustomSpinnerModel, NumberFormat numberformat)
		{
			super(numberformat);
			model = CustomSpinnerModel;
			setValueClass(CustomSpinnerModel.getValue().getClass());
		}
	}

	private PTSignal ptSig = null;


 	public CustomSpinner(PTSignal sig)
 	{
 		this(new CustomSpinnerModel());
 		ptSig = sig;
 	}
 	
 	public CustomSpinner(SpinnerModel model, PTSignal sig)
 	{
 		this(model);
 		ptSig = sig;
 	}
 	
 	public CustomSpinner()
 	{
 		this(new CustomSpinnerModel());
 	}

 	public CustomSpinner(SpinnerModel spinnermodel)
 	{
 		super(spinnermodel);
 	}

 	
	@Override
	public Dimension getMinimumSize()
	{
		Dimension size = new Dimension(this.getFontMetrics(this.getFont()).stringWidth(".00")+super.getMinimumSize().width, super.getMinimumSize().height);
		return size;
	}

	
	public void commitEdit() throws ParseException
 	{
// 		if(ptSig != null)
// 		{
// 			ptSig.clearDefault();
// 		}
 		super.commitEdit();
 	}
 	
 	
 	protected JComponent createEditor(SpinnerModel spinnermodel)
 	{
 		if(spinnermodel instanceof SpinnerDateModel)
 			return new DateEditor(this);
 		else if(spinnermodel instanceof SpinnerListModel)
 			return new ListEditor(this);
 		else if(spinnermodel instanceof CustomSpinnerModel)
 			return new NumberEditor(this);
 		else
 			return new DefaultEditor(this);
 	}

}