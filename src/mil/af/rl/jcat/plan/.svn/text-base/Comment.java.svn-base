/*
 * Created on Aug 5, 2004
 *
 */
package mil.af.rl.jcat.plan;

import java.io.Serializable;
import org.dom4j.Element;
import org.dom4j.DocumentHelper;

/**
 * @author vogels
 *  
 */
public class Comment implements Serializable, Cloneable
{
	private static final long serialVersionUID = 1L;

	protected String originator = null;

    protected String comment = null;

    protected String date = null;

    //I had to override the clone() method //MPG
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }

    public String getOriginator()
    {
        return originator;
    }

    public void setOriginator(String o)
    {
        originator = o;
    }

    public String getComment()
    {
        return comment;
    }

    public void setComment(String c)
    {
        comment = c;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String d)
    {
        date = d;
    }

    //Placeholder - xml generation should take place in validating parser
    public Element toXML()
    {
        Element ment = DocumentHelper.createElement("comment");

        ment.addElement("commenttext").addText(comment);
        ment.addElement("originator").addText(originator);
        ment.addElement("date").addText(date);

        return ment;

    }
}
