/*
 * Created on Jun 8, 2004
 *
 * Author Craig McNamara
 */

package mil.af.rl.jcat.util;

import java.io.Serializable;
import java.util.Comparator;

import org.dom4j.Document;
import org.dom4j.Element;
import org.doomdark.uuid.UUIDGenerator;

import com.c3i.jwb.JWBAttachment;


/**
 *
 */
public class Guid implements Comparator, Serializable, JWBAttachment
{

	private static final long serialVersionUID = 1L;
	private String value;

	public Guid()
	{
		value = UUIDGenerator.getInstance().generateRandomBasedUUID().toString();
	}

	/*
	 * For assigning a guid
	 */
	public Guid(Guid id)
	{
		value = id.toString();
	}

	
	public int hashCode()
	{
		//		System.out.println("Hashing " + value);
		return value.hashCode();
	}

	/**
	 * Re-creates a Guid from a string value of a previous Guid.  The string used is not verified in any way.
	 * This constructor should be used cautiously and is intended to be able to restore a Guid loaded from
	 * a previously saved plan. 
	 * @param id old Guid string (format not enforced, do not use an invalid Guid string!)
	 */
	public Guid(String id)
	{
		value = id;
	}

	public int compare(Object one, Object two)
	{
		return ((Guid) one).value.compareTo(((Guid) two).value);
	}

	public boolean equals(Guid aGuid)
	{
		return value.equals(aGuid.value);
	}

	public boolean equals(Object g)
	{
		boolean retVal = false;
		if(g instanceof Guid)
		{
			retVal = value.equals(((Guid) g).value);
		}
		return retVal;
	}

	public int compareTo(Object two)
	{
		return this.compare(this, two);
	}

	/**
	 * Helper function for serializing a Guid
	 *
	 * @param xml
	 *            the XML string to be extended
	 * @return the augmented XML string
	 */
	public String toXML(String xml)
	{
		xml += "guid='" + value + "'";
		return xml;
	}

	public Document addToDocument(Document doc, Element el)
	{
		el.addAttribute("guid", value);
		return doc;
	}

	public String toString()
	{
		return value;
	}

	/**
	 * @return Returns the value.
	 */
	public String getValue()
	{
		return value;
	}

	/**
	 * @param value
	 *            The value to set.
	 */
	public void setValue(String value)
	{
		this.value = value;
	}

	public JWBAttachment deepCopy()
	{
		return this;
	}
}
