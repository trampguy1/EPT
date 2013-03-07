/*
 * Created on Jun 24, 2005
 */
package mil.af.rl.jcat.plan;

import java.awt.Color;
import java.util.Vector;

/**
 * @author dygertm
 * a color scheme object which is a color and a attribute name
 * if change is made to this file, existing scheme files will fail to load
 */

@SuppressWarnings("serial") //a serial uid here would make old schemes not load, not worth it for now
public class ColorSchemeAttrib extends Color
{

	//private static final long serialVersionUID = 1L;
	private String name = "";
	
	public ColorSchemeAttrib(String inputName, Color inputColor)
	{
		super(inputColor.getRed(), inputColor.getGreen(), inputColor.getBlue(), inputColor.getAlpha());
		name = inputName;
	}
	
	public ColorSchemeAttrib(String inputName, int rgb)
	{
		super(rgb);
		name = inputName;
	}
	
	public void setName(String inputName)
	{
		name = inputName;
	}
	
	public boolean equals(Object otherOb)
	{
		try{
			ColorSchemeAttrib otherAttrib = (ColorSchemeAttrib)otherOb;
			if(super.equals(otherOb) && otherAttrib.toString().equals(this.toString()))
				return true;
			return false;
		}catch(ClassCastException exc){ return false; }
	}
	
	public String toString()
	{
		return name;
	}

	public static Vector getDefaultAttribs(Color defNodeColor)
	{
		Vector defAttribs = new Vector();
		defAttribs.add(new ColorSchemeAttrib("Default Node Color", defNodeColor));
		defAttribs.add(new ColorSchemeAttrib("Default Node Text Color", Color.BLACK));
		return defAttribs;
	}
}
