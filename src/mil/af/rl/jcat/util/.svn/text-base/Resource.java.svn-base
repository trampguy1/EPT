/*
 * Created on Aug 5, 2004
 *
 */
package mil.af.rl.jcat.util;

import java.io.Serializable;
import org.dom4j.Element;
import org.dom4j.DocumentHelper;

/**
 * @author vogels
 * 
 */
public class Resource implements Serializable, Cloneable
{
	private static final long serialVersionUID = 1L;

	protected String originator = null;

    protected String date = null; //Should be date or dateformat

    protected String location = null; //should be file or URI

    protected String name = null;

    protected int type = 0;

    public static final int FILE = 0;

    public static final int URL = 1;

    //I had to override the clone() method //MPG
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }

    public void setOriginator(String o)
    {
        originator = o;
    }

    public String getOriginator()
    {
        return originator;
    }

    public void setDate(String d)
    {
        date = d;
    }

    public String getDate()
    {
        return date;
    }

    public void setLocation(String l)
    {
        location = l;
    }

    public String getLocation()
    {
        return location;
    }

    public void setName(String n)
    {
        name = n;
    }

    public String getName()
    {
        return name;
    }

    public void setType(int t)
    {
        type = t;
    }

    public int getType()
    {
        return type;
    }

    //	Placeholder - xml generation should take place in validating parser
    public Element toXML()
    {

        Element ment = DocumentHelper.createElement("resource");

        ment.addElement("name").addText(name);
        ment.addElement("location").addText(location);
        ment.addElement("originator").addText(originator);
        ment.addElement("date").addText(date);

        return ment;
    }

}
