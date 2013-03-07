/*
 * Created on May 13, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package mil.af.rl.jcat.processlibrary.signaldata;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import mil.af.rl.jcat.processlibrary.Library;
import mil.af.rl.jcat.processlibrary.Signal;
import mil.af.rl.jcat.util.Guid;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;

/**
 * @author John Lemmer
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SignalSet extends TreeSet implements Comparator
{
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(SignalSet.class);
	

	public SignalSet()
    {
        super(new Guid());
    }

    public SignalSet(Guid comp)
    {
        super(comp);
    };

    public SignalSet(Collection<Guid> ts)
    {
        super(new Guid());
        addAll(ts);
    }

    public boolean add(Guid newElement)
    {
        return super.add(newElement);
    }

    public int compare(Object one, Object two)
    {
        int retVal = 0;
        Iterator i1 = ((SignalSet) one).iterator();
        Iterator i2 = ((SignalSet) two).iterator();
        while (retVal == 0)
        {
            if (i1.hasNext() && !i2.hasNext())
                return 1;
            else if (!i1.hasNext() && i2.hasNext())
                return -1;
            else if (!i1.hasNext() && !i2.hasNext())
                return 0;
            else
            {
                Object o = i1.next();
                Object t = i2.next();
                retVal = ((Guid) o).compareTo(t);
                //				retVal = ((Guid)i1.next()).comparetTo(((Guid)i2.next()));
            }
        }
        return retVal;
    }

    public String toXML(String xml, Library lib)
    {
        xml += "<SignalSet>";
        Iterator i = iterator();
        while (i.hasNext())
        {
            Guid g = (Guid) i.next();
            String guidText = "";
            guidText = g.toXML(guidText);
            // the TBD below will be replaced by method returning the name of
            // the string
            xml += "<SIGNAL " + guidText + " name=\""
                    + lib.getSignal(g).getSignalName()
                    + "\"/>";
            // old style; may be gone forever xml = g.toXML(xml);
        }
        xml += "</SignalSet>";
        return xml;
    }

    public Document addToDocument(Document doc, Element el, Library lib)
    {
        el = el.addElement("SignalSet");
        Iterator i = iterator();
        while (i.hasNext())
        {
            try{
	            Element sig = el.addElement("Signal");
	            Object o = i.next();
	            Guid g = (Guid) o;
	            g.addToDocument(doc, sig);
	            Signal s = lib.getSignal(g);
	            sig.addAttribute("name", s.getSignalName());
            }catch(Exception e)
            {
                logger.error("addToDocument - error building SignalSet document:  "+e.getMessage());
            }
        }
        return doc;
    }

    public void restoreFromElement(Element el, Library lib)
    {
        for (Iterator i = el.elementIterator("Signal"); i.hasNext();)
        {
            Element signal = (Element) i.next();
            String guid = signal.valueOf("@guid");
            String name = signal.valueOf("@name");
            ((TreeSet) this).add(new Guid(guid));

            Guid guidObj = new Guid(guid);
            Signal sig = lib.getSignal(new Guid(guid));
            if (sig == null)
            {// it was not in the lib even though there was a
                // library
                sig = new Signal(guidObj, name);
            }
            sig.setSignalName(name);// this uncondtionally overwrites the old
            // name
        }
    }

    public String listNames(Library lib)
    {
        String retVal = "{";
        for (Iterator i = iterator(); i.hasNext();)
        {
            Guid g = (Guid) i.next();
            Signal s = lib.getSignal(g);
            if (s != null)
            {
                retVal += s.getSignalName();
                if (i.hasNext())
                {
                    retVal += ", ";
                }
            }
        }
        retVal += "}";
        return retVal;
    }

    public static void main(String[] args)
    {
        SignalSet testSet = new SignalSet(/* new Guid() */);

        Guid element = new Guid("sam");
        testSet.add(element);
        Comparator sam = testSet.comparator();
        if (testSet.contains(element))
        {
            System.out.println("Element found.");
            System.out.println("The set contains " + testSet.size()
                    + " elements.");
        } else
        {
            System.err.println("Element NOT found.");
        }
    }

    public int computeCPTIndex(HashMap orderMap)
    {
        // assumes that 'this' is a subset of the keys in the orderMap
        int retVal = 0;
        for (Iterator i = iterator(); i.hasNext();)
        {
            Guid currentGuid = (Guid) i.next();
            Integer val = (Integer) orderMap.get(currentGuid);
            if (val != null)
            {
                retVal += val.intValue();
            } else
            {
                //System.out.println("Guid, " + currentGuid.getValue() + " , in
                // Elicited value for " + listNames() + "is not in the ordered
                // set of inputs.");
                return -1; // this elicited value is not applicable to signals
                // used in the plan
            }
        }
        return retVal;
    }
}

