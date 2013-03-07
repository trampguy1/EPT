/*
 * Created on Aug 25, 2005
 */
package mil.af.rl.jcat.gui.table.model;

import java.io.Serializable;
import java.text.NumberFormat;

import mil.af.rl.jcat.util.MaskedFloat;

/*
 * Customized spinner model to work with the CustomSpinner, needed mainly because javas Float class is not extendable
 * and therefore MaskedFloat subclass Float
 */


public class CustomSpinnerModel extends javax.swing.SpinnerNumberModel implements Serializable
{
	private static final long serialVersionUID = 1L;
	private Number stepSize;
	private Number value;
	private Comparable minimum;
	private Comparable maximum;
	private NumberFormat nf;
	
	public CustomSpinnerModel()
	{
		this(((Number) (new Integer(0))), ((Comparable) (null)), ((Comparable) (null)), ((Number) (new Integer(1))));
	}
	
	public CustomSpinnerModel(double d, double d1, double d2, double d3)
	{
		this(((Number) (new Double(d))), ((Comparable) (new Double(d1))), ((Comparable) (new Double(d2))), ((Number) (new Double(d3))));
	}
	
	public CustomSpinnerModel(int i, int j, int k, int l)
	{
		this(((Number) (new Integer(i))), ((Comparable) (new Integer(j))), ((Comparable) (new Integer(k))), ((Number) (new Integer(l))));
	}
	
	public CustomSpinnerModel(Number number, Comparable comparable, Comparable comparable1, Number number1)
	{
		if(number == null || number1 == null)
			throw new IllegalArgumentException("value and stepSize must be non-null");
		if(comparable != null && comparable.compareTo(number) > 0 || comparable1 != null && comparable1.compareTo(number) < 0)
		{
			throw new IllegalArgumentException("(minimum <= value <= maximum) is false");
		} else
		{
			value = number;
			minimum = comparable;
			maximum = comparable1;
			stepSize = number1;
			nf = NumberFormat.getInstance();
			nf.setMaximumFractionDigits(2);
			return;
		}
	}
	
	public Comparable getMaximum()
	{
		return maximum;
	}
	
	public Comparable getMinimum()
	{
		return minimum;
	}
	
	public void setMaximum(Comparable comparable)
	{
		if(comparable != null ? !comparable.equals(maximum) : maximum != null)
		{
			maximum = comparable;
			fireStateChanged();
		}
	}
	
	public void setMinimum(Comparable comparable)
	{
		if(comparable != null ? !comparable.equals(minimum) : minimum != null)
		{
			minimum = comparable;
			fireStateChanged();
		}
	}
	
	public Number getNumber()
	{
		return value;
	}
	
	public Number getStepSize()
	{
		return stepSize;
	}
	
	private Number incrValue(int i)
	{
		Object obj;
		if(value instanceof MaskedFloat)
		{
			double d = value.doubleValue() + stepSize.doubleValue() * (double)i;
			obj = MaskedFloat.getMaskedValue(Double.valueOf(nf.format(d)).doubleValue());
		}
		
		else if((value instanceof Float) || (value instanceof Double))
		{
			double d = value.doubleValue() + stepSize.doubleValue() * (double)i;
			if(value instanceof Double)
				obj = new Double(d);
			else
				obj = new Float(d);
		} else
		{
			long l = value.longValue() + stepSize.longValue() * (long)i;
			if(value instanceof Long)
				obj = new Long(l);
			else
				if(value instanceof Integer)
					obj = new Integer((int)l);
				else
					if(value instanceof Short)
						obj = new Short((short)(int)l);
					else
						obj = new Byte((byte)(int)l);
		}
		if(maximum != null && maximum.compareTo(obj) < 0)
			return null;
		if(minimum != null && minimum.compareTo(obj) > 0)
			return null;
		else
			return ((Number) (obj));
	}
	
	public void setStepSize(Number number)
	{
		if(number == null)
			throw new IllegalArgumentException("null stepSize");
		if(!number.equals(stepSize))
		{
			stepSize = number;
			fireStateChanged();
		}
	}
	
	public Object getNextValue()
	{
		return incrValue(1);
	}
	
	public Object getPreviousValue()
	{
		return incrValue(-1);
	}
	
	public Object getValue()
	{
		return value;
	}
	
	public void setValue(Object obj)
	{
		if(obj == null || !(obj instanceof Number))
			throw new IllegalArgumentException("illegal value");
		if(!obj.equals(value))
		{
			value = (Number)obj;
			fireStateChanged();
		}
	}
}