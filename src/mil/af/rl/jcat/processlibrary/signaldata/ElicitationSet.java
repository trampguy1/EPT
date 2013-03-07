/*
 * Created on May 20, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package mil.af.rl.jcat.processlibrary.signaldata;

import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

import mil.af.rl.jcat.processlibrary.Library;
import mil.af.rl.jcat.util.Guid;

import org.dom4j.Document;
import org.dom4j.Element;

/**
 * @author John Lemmer
 *  
 */
public class ElicitationSet extends TreeSet<ElicitedProbability> implements Comparable
{

    private static final long serialVersionUID = 1L;

    //@SuppressWarnings("unchecked")
    public ElicitationSet(Comparator sam)
    {
        super(sam);
    };

    public int compareTo(Object aSet)
    {
        int retVal = 0;
        return retVal;
    }

    public String toXML(String xml, Library lib)
    {
        xml += "<ElicitationSet>";
        for (Iterator elicitations = iterator(); elicitations.hasNext();)
        {
            xml = ((ElicitedProbability)elicitations.next()).toXML(xml, lib);
        }
        xml += "</ElicitationSet>";
        return xml;
    }

    public Document addToDocument(Document doc, Element el, Library lib)
    {
        Element el1 = el.addElement("ElicitationSet");
        for (Iterator elicitations = iterator(); elicitations.hasNext();)
        {
            doc = ((ElicitedProbability) elicitations.next()).addToDocument(
                    doc, el1, lib);
        }
        return doc;
    }

    public void restoreFromEl(Element el)
    {
        for (Iterator i = el.elementIterator("ElicitedProbability"); i
                .hasNext();)
        {
            Element epEL = (Element) i.next();
            ElicitedProbability ep = new ElicitedProbability(Float.parseFloat(epEL.valueOf("@causalprobability")));
            ep.setGroupName(epEL.valueOf("@group-name"));
            Element elSS = (Element) epEL.selectSingleNode("SignalSet");
            for (Iterator j = elSS.elementIterator("Signal"); j.hasNext();)
            {
                Element epSS = (Element) j.next();
                ep.add(new Guid(epSS.valueOf("@guid")));
            }
            add(ep);
        }
    }

}