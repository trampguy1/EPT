/*
 * Created on Jun 23, 2005
 */
package mil.af.rl.jcat.plan;

import java.awt.Color;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;


/**
 * @author dygertm
 * Holds the current color scheme and represents all color schemes in the user prefs
 */
public class ColorScheme implements Serializable, Cloneable
{
	public static String DEF_NODE_STRING = "Default Node Color";
	public static String DEF_NODE_TEXT_STRING = "Default Node Text Color";
	private static ColorScheme activeScheme = null;
	private Vector schemeData = null;
	private String name = "";
	private boolean fromFile = false;
	
	private ColorScheme()
	{
		this("", new Vector());
	}
	
	public ColorScheme(String inputName, Vector inputData)
	{
		this(inputName, inputData, false);
	}
	
	public ColorScheme(String inputName, Vector<ColorScheme> inputData, boolean fromFile)
	{
		name = inputName;
		if(inputData != null)
			schemeData = inputData;
		else
			schemeData = new Vector();
		
		this.fromFile = fromFile;
	}
	
	public static ColorScheme getInstance()
	{
		if(activeScheme == null)
			activeScheme = new ColorScheme();
		return activeScheme;
	}
	
	public void setSchemeData(Vector attribData)
	{
		schemeData = attribData;
	}
	
	public Vector getSchemeData()
	{
		return schemeData;
	}
	
	public String getName()
	{
		return name;
	}
	
	public Color getColorFor(String attrib)
	{
		//go through ColorSchemeAttribs in the schemeData and find one with this name
		Enumeration attribs = schemeData.elements();
		while(attribs.hasMoreElements())
		{
			ColorSchemeAttrib thisAttrib = (ColorSchemeAttrib)attribs.nextElement();
			if(attrib.equals(thisAttrib.toString()))
				return thisAttrib;
		}
		
		return null;
	}
	
	public void makeActive()
	{
		activeScheme = (ColorScheme)this.clone();
	}

	public static void reset()
	{
		activeScheme = null;		
	}
	
	public String toString()
	{
		return name + ((fromFile) ? " (from file)" : "");
	}
	
	public boolean equals(Object input)
	{
		try{
			ColorScheme inputScheme = (ColorScheme)input;
			if(inputScheme == null || inputScheme.schemeData == null || this.schemeData == null)
				return false;
			if(inputScheme.name.equals(this.name) && (inputScheme.schemeData.equals(this.schemeData)))
				return true;
			return false;
		}catch(ClassCastException exc){
			return false;
		}
	}

	public boolean compareData(Object inputScheme)
	{
		if(((ColorScheme)inputScheme).schemeData.equals(this.schemeData))
			return true;
		else
			return false;
	}
	
	public void setFromFile(boolean ff)
	{
		fromFile = ff;
	}
	
	public boolean isFromFile()
	{
		return fromFile;
	}

	/**
	 * Checks to see if the given list contains a the same name as the given scheme but not the scheme itself 
	 */
	public static boolean hasDuplicateNameOnly(List<ColorScheme> schemes, ColorScheme scheme)
	{
		ColorScheme found = null;
		for(ColorScheme schm : schemes)
			if(schm.getName().equals(scheme.getName()))
				found = schm;
		
		return(found != null && !found.compareData(scheme));
	}
	
	public Object clone()
	{
		return new ColorScheme(new String(name), new Vector(schemeData), fromFile);
	}

	
}
