/*
 * Created on Aug 24, 2005
 */
package mil.af.rl.jcat.util;



/**
 * @author dygertm
 * A float value with a kick (a textual name that can be used in place of the numeric value)
 * -used in a spinner or MaskedComponent to be view differently based on the mode ('simple probablity mode') 
 */

public class MaskedFloat extends Number implements Comparable
{
	private static final long serialVersionUID = 1L;
	private static MaskedFloat[] mValues;
	public static boolean useMasked = false;
	private Float value = null;
	private String name;  //the text representation for this float probability
	
	
	public MaskedFloat(String nm, float val)
	{
		value = new Float(val);
		name = nm;
	}
	
	//this constructor needed only for internal java stuff that calls it in order to
	//commit a user manually entered value
	public MaskedFloat(String flt)
	{
		value = new Float(flt);
		name = "";
	}
	
	private MaskedFloat(float val)
	{
		value = new Float(val);
		name = "";
	}
	
	/*private MaskedFloat(double val)
	{
		value = new Float(val);
		name = "";
	}
	
	/*private MaskedFloat(String nm, Float val)
	{
		value = val;
		name = nm;
	} */
	
	public static MaskedFloat getMaskedValue(float inputProb)
	{
		if(mValues == null) //no values were ever loaded
			return new MaskedFloat(inputProb);
		
		MaskedFloat closest = mValues[mValues.length-1];
		
		for(int x=mValues.length-1; x>=0; x--)
		{
			// if the input has an exact match in the users list of values 
			if(mValues[x].floatValue() == inputProb)
			{
				return mValues[x];
			}
			
			// find the maskedfloat closest to the input for later (if needed)
			if(closer(mValues[x].floatValue(), closest.floatValue(), inputProb))
				closest = mValues[x];
		}
		
//		// first lets ensure that if something requests a MaskedFloat for either 0 or 1 and there was no
//		// exact match in the users list, create an equivilant (not closest) MaskedFloat with the 0 or 1 value 
//		// because leak needs a 0 value and effect needs a 1
//		if(inputProb == 1f)
//		{
//			System.out.println("MaskedFloat 1f requested with no user defined value for 1");
//			return MaskedValue.defaultMax;
//		}
//		else if(inputProb == 0f)
//		{
//			System.out.println("MaskedFloat 0f requested with no user defined value for 0");
//			return MaskedValue.defaultMin;
//		}
		
		// number prob does not match one in our fixed list so return the closest match
		// only if its in simple mode otherwise use the exact number
		if(useMasked)
			return closest;
		
		return new MaskedFloat(inputProb);
		
	}
	
	public static MaskedFloat getMaskedValue(double inputProb)
	{
		return getMaskedValue((float)inputProb);
	}
	
	public static boolean closer(float current, float closest, float input)
	{
		if(Math.abs(current-input) < Math.abs(closest-input))// && Math.abs() <= input)
			return true;
		else
			return false;
	}
	
	///////////////////////////////////////////////////////
	
	public float floatValue()
	{
		return value.floatValue();
	}
	
	public boolean equals(Object otherObj)
	{
		try{
			if(((MaskedFloat)otherObj).toString().equals(toString()))
				return true;
		}catch(ClassCastException exc){   return false;   }
		
		return false;
	}
	
	public String toString()
	{
		if(useMasked)
			return name;
		else
			return value.toString();
	}

	//  exc.printStackTrace();   return -1;
	public int compareTo(Object arg0)
	{
		try{  //MaskedFloat
			MaskedFloat mf = (MaskedFloat)arg0;
			return value.compareTo(new Float(mf.floatValue()));
			// compares a String also for some reason
		}catch(ClassCastException exc){   return 0;   } 
	}

	//
	public int intValue()
	{
		return value.intValue();
	}

	//
	public long longValue()
	{
		return value.longValue();
	}

	//
	public double doubleValue()
	{
		return value.doubleValue();
	}

	
	
	public static void setMaskedValues(MaskedFloat[] val)
	{
		mValues = val;
	}
	
	public static MaskedFloat[] getMaskedValues()
	{
		return mValues;
	}
}
