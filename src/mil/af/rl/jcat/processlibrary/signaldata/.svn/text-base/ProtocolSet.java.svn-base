/*
 * Created on May 20, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package mil.af.rl.jcat.processlibrary.signaldata;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;

import mil.af.rl.jcat.processlibrary.Library;
import mil.af.rl.jcat.processlibrary.SignalType;
import mil.af.rl.jcat.util.Guid;

import org.dom4j.Document;
import org.dom4j.Element;

/**
 * @author John Lemmer
 *
 */
/**
 * This class groups elicited probabilitys according to the method for combining
 * them into Conditional Probability Tables (CPT's) The default protocol is
 * Recursive Noisy OR (RNOR). Other protocols will include 'AND', Necessity,
 * ..., and other protocols based on causal notions. These causal protocols
 * contrast with protocols based on probabilistic notions, such as conditional
 * independence.
 * 
 * Note that the set of signals belonging to a particular mode (e.g. CAUSAL),
 * can have subsets of signals that are treated with different protocols.
 * 
 * @author John Lemmer
 *  
 */

public class ProtocolSet extends SignalSet implements Comparator
{
	private static final long serialVersionUID = 1L;

	private final int protocol;

    //	private ArrayList elicitations = new ArrayList();
    private ElicitationSet elicitations = new ElicitationSet(new SignalSet());

    public ProtocolSet(int protocolType)
    {
        protocol = protocolType;
    }

    /**
     * This compares two Protocol Sets. Protocol Sets are equal if (and only if,
     * for the mathematically dogmatic) they are defined over the same set of
     * signals and they are the same 'type' of protocol. Notice that this
     * definition of equality says nothing about the CONTENTS (i.e. the
     * ElicitedProbabilities contained in this protocols ElicitationSet) being
     * equal.
     */
    public int compare(Object one, Object two)
    {
        int retVal = 0;
        ProtocolSet o = (ProtocolSet) one;
        ProtocolSet t = (ProtocolSet) two;
        // the following makes the protocol the major order item. In fact i
        // think only equal and not equal make much sense. But we are
        // impelementing the comparator
        if (o.protocol < t.protocol)
            return -1;
        if (o.protocol > t.protocol)
            return 1;
        // if we get to hear they are the same type of protocol
        retVal = super.compare(one, two);
        // maybe at some point we will continue on to compare the contensts, but
        // i am too lazy to do it now
        if (retVal != 0)
            return retVal; // if the two protocols are not over the same set of
        // signals, they cannot be equal
        return retVal;
    }

    /**
     * 
     * In addition to adding a new elicited value to this protocol, this method
     * also updates the Protocol Set itself to contain any new signals. The
     * caller of this method should have previously checked to make sure other
     * ProtocalSets do not already contain any of these new signals. In addition
     * this method can be used to update the value of an existing
     * implementation, since the old value will effectively be overwritten. The
     * method does NOT remove and signals from the protocol set.
     * 
     * @param aProb:
     *            the ElicitedProbability to be added or updated
     * @return if the elicited value is being updated, returns the previous
     *         value; otherwise returns -1
     */
    public float addElicitation(ElicitedProbability aProb)
    {
        addAll(aProb); // the ProtocolSet now contains a set union of the
        // signals it previously contained and the signals in aProb

        float oldValue = removeElicitedValue(aProb);// allows updating by
        // replacing the previous value; oldValue not currently used
        synchronized(elicitations)
        {
	        if(elicitations.add(aProb))
	        	return oldValue; 
	        else
	        	return SignalType.probProblem; // either
        }
        // adds of updates depending on what happened above.
        // Note that updating is done by replacement
    }

    public boolean deleteSignal(Guid sig)
    {
        //for (Iterator i = ((ElicitationSet)elicitations.clone()).iterator(); i.hasNext();)
		for (Iterator i = elicitations.iterator(); i.hasNext();)
        {
        	synchronized(elicitations)
        	{
        		ElicitedProbability ep = (ElicitedProbability) i.next();
	            if (ep.contains(sig))
	            {
	            	//elicitations.remove(ep);
	            	i.remove();
	            }
        	}
        }
    	
        return super.remove(sig);
    }

    // Helper for addElicitation and deleteElicitation
    private float removeElicitedValue(ElicitedProbability aProb)
    {
        float retVal = SignalType.probProblem;
        
        synchronized(elicitations)
        {
	        SortedSet theTail = (SortedSet) elicitations.tailSet(aProb);
	        if (theTail.size() > 0)
	        {
	            ElicitedProbability tailHead = (ElicitedProbability) theTail.first();
	            if (aProb.equals(tailHead))
	            {
	                retVal = tailHead.getProbability();
	                theTail.remove(tailHead);
	            }
	        }
        }
        return retVal;
    }

    /**
     * Deletes the elicitation in the protocol's elicitation set whose signal
     * set is the same as the set of signals in the collection, signals
     * 
     * @param signals:
     *            a collection of Guids which identify the group whose
     *            elicitation is to be deleted
     * @return true if the the elicitation was found and deleted, false if the
     *         elicitation was not found in the elicitations set.
     */
    public boolean deleteElicitedValue(Collection signals)
    {
        SignalSet ss = new SignalSet(signals);
        ElicitedProbability ep = new ElicitedProbability("", SignalType.probProblem, ss);
        removeElicitedValue(ep);
        return removeElicitedValue(ep) != SignalType.probProblem;
    }
    
    public float getElicitedValue(Collection signals)
    {
    	//SignalSet sigSet = new SignalSet(signals);
    	Iterator elicIt = elicitations.iterator();
    	while(elicIt.hasNext())
    	{
    		ElicitedProbability ep = (ElicitedProbability)elicIt.next();
    		if(ep.getSignalSet().containsAll(signals))
    			if(ep.getSignalSet().size() == signals.size()) //DONT INCLUDE GROUPS RIGHT NOW
    				return ep.getProbability();
    	}
    	
    	//if the signal wasnt in an elic-set but is in this protocol set it must just have defsingleprob
    	if(this.containsAll(signals))
    		return -2f;
    	
    	return -1f;
    }

    boolean findElicitation(SignalSet ss)
    {
        boolean retVal = elicitations.contains(ss);
        return retVal;
    }

    public String toXML(String xml, Library lib)
    {
        xml += "<ProtocolSet>";
        xml = super.toXML(xml, lib);
        xml += "<Protocol>" + protocol + "</Protocol>";
        xml = elicitations.toXML(xml, lib);
        xml += "</ProtocolSet>";
        return xml;
    }

    public Document addToDocument(Document doc, Element el, Library lib)
    {
        Element el1 = el.addElement("ProtocolSet");
        el1.addAttribute("protocol", Integer.toString(protocol));
        super.addToDocument(doc, el1, lib);
        elicitations.addToDocument(doc, el1, lib);
        return doc;
    }

    public void restoreFromElement(Element el, Library lib)
    {
        Element elSS = (Element) el.selectSingleNode("SignalSet");
        super.restoreFromElement(elSS, lib);
        Element elES = (Element) el.selectSingleNode("ElicitationSet");
        elicitations.restoreFromEl(elES);
    }

    /*
     * public static void main (String[] args){ String xml = ""; ProtocolSet ps =
     * new ProtocolSet(1); ElicitedProbability ep = new
     * ElicitedProbability((float).7); for(int j = 0; j < 3; j++){ Guid g = new
     * Guid(String.valueOf(j)); ep.add(g); ps.add(g); } ps.addElicitation(ep);
     * boolean foundElicitation = ps.findElicitation(ep); if(foundElicitation){
     * System.out.println("The elicitation was found in the elicitation set."); }
     * else{ System.err.println("Opps!"); } xml = " <?xml version=\"1.0\" ?>";
     * xml = ps.toXML(xml); System.out.println(xml); }
     */

    /**
     * @return
     */
    public int getProtocol()
    {
        return protocol;
    }

    public boolean processProtocol(HashMap indexer, int cptSize, float cpt[], int source[])
    {
        boolean retVal = true;
        // the following works RNOR protocols and supports my current
        // implementation of the GAND protocol
        for (Iterator i = elicitations.iterator(); i.hasNext();)
        {
            ElicitedProbability ep = (ElicitedProbability) i.next();
            int index = ep.computeCPTIndex(indexer) + 1;//the computed value is
            // for just the signals; add '1' because we want the effect occurrence
            if (index >= 0)
            {
                cpt[index] = ep.getProbability();
                source[index] = SignalType.ELICITED_VALUE;
            }// otherwise this elicited value refers to signals that are in the
            // CPT, but not in the plan
        }
        // CURRENTLY, NO PROTOCOLS ARE IMPLEMENTED THAT REQUIRE FURTHER PROCESSING.
        return retVal;
    }

    public void removeElicitationsInvolving(Collection grp)
    {
        for (Iterator i = elicitations.iterator(); i.hasNext();)
        {
            ElicitedProbability e = (ElicitedProbability) i.next();
            for (Iterator j = grp.iterator(); j.hasNext();)
            {
                Guid g = (Guid) j.next();
                if (e.contains(g))
                {
                    i.remove();
                    break;
                }
            }
        }
    }

    public void constructAND(Collection grp, float andProb)
    {
        addAll(grp);// puts the Guids in the ProtocolSet's SignalSet

        ElicitedProbability theGrp = new ElicitedProbability(andProb);
        theGrp.addAll(grp);
        elicitations.add(theGrp);

        // now the essential nature of an AND group
        // the INDIVIDUAL signals contribute NO causality by themselves
        for (Iterator i = grp.iterator(); i.hasNext();)
        {
            Guid g = (Guid) i.next();
            // making these '0' could be a bad idea, once evidence is being considered
            ElicitedProbability alone = new ElicitedProbability((float) 0.0);
            alone.add(g);
            elicitations.add(alone);
        }
    }

    /**
     * @return Returns the elicitations.
     */
    public ElicitationSet getElicitations()
    {
        return elicitations;
    }

    /**
     * @param elicitations
     *            The elicitations to set.
     */
    public void setElicitations(ElicitationSet elicitations)
    {
        this.elicitations = elicitations;
    }
}