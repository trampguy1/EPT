/*
 * Created on May 29, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package mil.af.rl.jcat.processlibrary;

import java.io.Serializable;
import java.util.Vector;

import mil.af.rl.jcat.util.Guid;

import org.dom4j.Document;
import org.dom4j.Element;

/**
 * @author John Lemmer
 * 
 */
public class Signal implements Serializable, Comparable
{
	private static final long serialVersionUID = 1L;

	private Guid signalID = null;

	String signalName = null;

	public Signal()
	{
		this(new Guid(), "New Signal");
	}

	public static Signal restoreFromElement(Element el)
	{
		String name = el.valueOf("@name");
		String guid = el.valueOf("@guid");
		Guid theGuid = null;
		if (guid.equals(""))
		{
			theGuid = new Guid();
		} else
		{
			theGuid = new Guid(guid);
		}
		return new Signal(theGuid, name);
	}

	public Signal(Guid guid, String sigName)
	{
		signalName = sigName;
		signalID = guid;
	}

	public Signal safeClone()
	{
		return this;
	}

	/**
	 * Method returns the signal name
	 * 
	 * @return String
	 */
	public String getSignalName()
	{
		return signalName;
	}

	/**
	 * @param string
	 */
	public void setSignalName(String string)
	{
		signalName = string;
	}

	/**
	 * @return
	 */
	public Guid getSignalID()
	{
		return signalID;
	}

	public Document addSignalToDocument(Document doc, Element el)
	{
		el = el.addElement("Signal").addAttribute("guid",
				getSignalID().toString()).addAttribute("name", getSignalName());

		return doc;
	}

	public void getSignalInhibitingCPT(Vector inhibitingCPT){
		inhibitingCPT.setSize(2);
		inhibitingCPT.setElementAt(new Float(1.0f), 0);
		inhibitingCPT.setElementAt(new Float(0.0f), 1);
	}

	public void getSignalCPT(Vector activeSignals, Vector causalCPT,
			Vector causalOrder)
	{
		// this is going to be made MUCH fancier later
		causalCPT.setSize(4);
		causalCPT.setElementAt(new Float((float) 1.0), 0);
		//System.out.println("cpt[0] = " + causalCPT.elementAt(0));
		causalCPT.setElementAt(new Float((float) 0.0), 1);
		// System.out.println("cpt[1] = " + causalCPT.elementAt(1));
		causalCPT.setElementAt(new Float((float) 0.00), 2);
		//System.out.println("cpt[2] = " + causalCPT.elementAt(2));
		causalCPT.setElementAt(new Float((float) 1.0), 3);
		//System.out.println("cpt[3] = " + causalCPT.elementAt(3));
	}

	public void getSignalAdder(Vector activeSignals, Vector causalCPT,
			Vector causalOrder)
	{
		// This is the same linear scheme used in C++ CAT.
		// Someday we will move beyond this.
		// But notice how the architechture now offers the ability for a signal
		// (type) to decide how its adder works.
		Float onePtZero = new Float(1.0);
		int causeCount = activeSignals.size();
		int cptSize = (1 << (causeCount + 1));// =1 for the event itself
		causalCPT.setSize(cptSize);
		float totalCauses = (float) causeCount;
		for (int j = 0; j < cptSize; j += 2)
		{
			float yes = countBits(j, causeCount + 1) / totalCauses; // yes this
			// IS the right j, 'cause we don't want to count the event bit itself
			float no = (float) 1.0 - yes;
			causalCPT.setElementAt(new Float(yes), j + 1);
			causalCPT.setElementAt(new Float(no), j);
			//System.out.println("cpt[" + (j) + "] = " + no);
			//System.out.println("cpt[" + (j + 1) + "] = " + yes);
		}
		causalOrder.setSize(causeCount);
		causalOrder.addAll(activeSignals);// with the current linear adder
		// scheme all the various causes are
		// equal. However later,...
	}

	private int countBits(int j, int causeCount)
	{
		int count = 0;
		for (int i = 0; i < causeCount; i++)
		{
			if ((j & (1 << i)) == (1 << i))
			{
				count++;
			}
		}
		return count;
	}

	public String toString()
	{
		return signalName;
	}


	//added so that Signals can be sorted in a list
	public int compareTo(Object inSig)
	{
		return this.toString().compareToIgnoreCase(inSig.toString());
	}
}
