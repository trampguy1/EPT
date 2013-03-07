/*
 * Created on May 25, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package mil.af.rl.jcat.processlibrary.signaldata;

/**
 * @author John Lemmer
 *
 */

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;

import mil.af.rl.jcat.exceptions.SignalException;
import mil.af.rl.jcat.processlibrary.Library;
import mil.af.rl.jcat.processlibrary.Signal;
import mil.af.rl.jcat.processlibrary.SignalType;
import mil.af.rl.jcat.util.Guid;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

public class ModeSet extends SignalSet
{
	private static final long serialVersionUID = 1L;

	private TreeSet protocols = new TreeSet((Comparator) (new ProtocolSet(SignalType.notSet)));

    private int mode = SignalType.notSet;

    private float defaultSingleSignalProbability = (float) 0.0;
    private static Logger logger = Logger.getLogger(ModeSet.class);
    
    private SignalSet inversions = new SignalSet();

    public ModeSet(int aMode, float singleSignalProbability)
    {
        mode = aMode;
        defaultSingleSignalProbability = singleSignalProbability;
    }
    
    //test
    public Object[] getProtocols()
    {
        return protocols.toArray();
    }
    
    protected void setDefaultProbability(float val)
    {
    	defaultSingleSignalProbability = val;
    }
    
    protected float getDefaultProbability()
    {
    	return defaultSingleSignalProbability;
    }
    
    public void setSignalInversion(Signal sig, boolean invert)
    {
        if (invert)
        {
            inversions.add(sig.getSignalID());
        } else
        {
            inversions.remove(sig.getSignalID());
        }
    }

    public boolean addElicitation(ProtocolSet p, ElicitedProbability ep)
    {
        this.addAll(ep);
        if (p.addElicitation(ep) != SignalType.probProblem)
        {
            return true;
        } else
        {
            return false;
        }
    }

    public boolean addElicitedValue(int protocol, Collection signals, float prob, String groupName)
            throws SignalException
    {
        ProtocolSet p = findProtocol(signals);
        if (p != null && protocol == p.getProtocol())
        {
            SignalSet group = new SignalSet(signals);
            return p.addElicitation(new ElicitedProbability(groupName, prob, group)) != SignalType.probProblem;
        } else
        {
            return false;
        }
    }

    public float getElicitedValue(Collection signals) throws SignalException
    {
    	ProtocolSet p = findProtocol(signals);
        if (p != null)
        {
        	float eVal = p.getElicitedValue(signals);
        	if(eVal == -2f)
        		return this.defaultSingleSignalProbability;
        	else
        		return eVal;
        }
        else
        {
        	logger.warn("getElictedValue - no protocol set found");
        	return -1f;
        }
    }
    
    /*
     * private ProtocolSet findProtocol(Collection signals){ ProtocolSet p =
     * null; for(Iterator i = protocols.iterator(); i.hasNext();){ p =
     * (ProtocolSet)i.next(); if(p.containsAll(signals)){ break; } } return p; }
     */
    public boolean deleteElicitedValue(Collection signals)
            throws SignalException
    {
        ProtocolSet p = findProtocol(signals);
        if (p != null)
        {
            return p.deleteElicitedValue(signals);
        } else
        {
            return false;
        }
    }

    public boolean add(Guid sig)
    {
        // add it to the signal set
        super.add(sig);
        // add it to the default protocol: RNOR
        ProtocolSet dfPs = null;
        for (Iterator i = protocols.iterator(); i.hasNext();)
        {
            ProtocolSet p = (ProtocolSet) i.next();
            if (p.getProtocol() == SignalType.RNOR)
            {
                dfPs = p;
            }
        }
        if (dfPs == null)
        {
            dfPs = new ProtocolSet(SignalType.RNOR);
            protocols.add(dfPs);
        }
        return dfPs.add(sig);
    }

    public boolean deleteSignal(Guid sig)
    {
        for (Iterator i = protocols.iterator(); i.hasNext();)
        {
            ProtocolSet p = (ProtocolSet) i.next();
            p.deleteSignal(sig);
        }
        return super.remove(sig);
    }

    public String toXML(String xml, Library lib)
    {

        xml += "<ModeSet>";
        xml = super.toXML(xml, lib);
        xml += "<mode>" + mode + "</mode>";
        for (Iterator i = protocols.iterator(); i.hasNext();)
        {
            xml = ((ProtocolSet) (i.next())).toXML(xml, lib);
        }
        xml += "</ModeSet>";
        return xml;
    }

    public int getMode()
    {
    	return mode;
    }
    
    public Document addToDocument(Document doc, Element el, Library lib)
    {
        Element el1 = el.addElement("ModeSet");
        el1.addAttribute("mode", Integer.toString(mode));
        el1.addAttribute("defaultSingleSignal", Float
                .toString(defaultSingleSignalProbability));
        super.addToDocument(doc, el1, lib);
        Element theInversions = el1.addElement("Inversions");
        doc = inversions.addToDocument(doc, theInversions, lib);
        for (Iterator i = protocols.iterator(); i.hasNext();)
        {
            doc = ((ProtocolSet) (i.next())).addToDocument(doc, el1, lib);
        }
        return doc;
    }

    public void restoreFromElement(Element el, Library lib)
    {
        mode = Integer.parseInt(el.valueOf("@mode"));
        String defProb = el.valueOf("@defaultSingleSignal");
        if (!defProb.equals(""))
        {
            defaultSingleSignalProbability = Float.parseFloat(el
                    .valueOf("@defaultSingleSignal"));
        }
        Node signals = el.selectSingleNode("SignalSet");
        if (signals instanceof Element)
        {
            super.restoreFromElement((Element) signals, lib);
        }
        Node invs = el.selectSingleNode("Inversions");
        if (invs instanceof Element)
        {
        	Node invSigs = invs.selectSingleNode("SignalSet");
        	if(invSigs instanceof Element)
        		inversions.restoreFromElement((Element) invSigs, lib);
        }
        for (Iterator p = el.elementIterator("ProtocolSet"); p.hasNext();)
        {
            Element psE = (Element) p.next();
            ProtocolSet ps = new ProtocolSet(Integer.parseInt(psE
                    .valueOf("@protocol")));
            ps.restoreFromElement(psE, lib);
            protocols.add(ps);
        }
    }

    public void addProtocol(ProtocolSet ps)
    {
        this.addAll(ps);
        this.protocols.add(ps);
    }

    public boolean RNOR(Vector order, Vector CPT, Library lib)
    {
        if(Library.DEBUG)
        {
            System.out.print(" Mode " + Integer.toString(mode) + ": [");
            for (int j = order.size() - 1; j >= 0; j--)
            {
                System.out.print((lib.getSignal((Guid) order
                        .elementAt(j))).getSignalName());
                if (j != 0)
                {
                    System.out.print(", ");
                }
            }
            System.out.println("]");
        }
        boolean retVal = false;
        int sigCount = order.size();
        int variableCount = sigCount + 1;
        int cptSize = (1 << variableCount);
        float cpt[] = new float[cptSize];
        int source[] = new int[cptSize];
        
        // ev - we don't have to explicitly initialize this, primitive arrays get
        // initialized by default to 0 or false;
        for (int j = 0; j < cptSize; j++)
        {
            cpt[j] = (float) 0.0;
            source[j] = SignalType.notSet; // '0' means "NOT SET"; I hope to convert
            // this to a typedef SignalType when 1.5 comes
            // out
        }
        HashMap indexer = new HashMap();
        for (int j = 0; j < sigCount; j++)
        {
            indexer.put(order.elementAt(j), new Integer(1 << (j + 1)));
        }

        //end of initialization. lets do some work
        if (cptSize > 0)
        {
            cpt[1] = (float) 0.0; // just the effect could have happened in this
            // CPT; with no causes having happened, the
            // effect doesn't happen either. Note, leak
            // will be added later during sampling.
        }
        //insert the values implied by the different protocols and their
        // associated elicited values
        for (Iterator i = protocols.iterator(); i.hasNext();)
        {
            ProtocolSet p = (ProtocolSet) i.next();
            if (p.getProtocol() == SignalType.RNOR || p.getProtocol() == SignalType.GAND)
            {//these are the only ones implemented and they both get processed the same way
                p.processProtocol(indexer, cptSize, cpt, source);
            }
        }

        // now mush it all together by doing RNOR on everthing!
        for (int j = 3; j < cptSize; j += 2)
        {//just get the ones where the
            // effect and at least one cause
            // occurs
            if (source[j] == SignalType.notSet)
            {// 'cause if a protocol has set it,
                // that's IT!
                if (activeSigCount(j, variableCount) > 1)
                {
                    cpt[j] = rnor(j, cpt, variableCount);
                } else
                {// gotta use the default single signal probability if(j
                    // > 1){
                    cpt[j] = defaultSingleSignalProbability;// it would have
                    // been set if we
                    // had an
                    // elicitation for
                    // it
                }
            }
        }
        CPT.setSize(cptSize);
        for (int j = 1; j < cptSize; j += 2)
        {// compute the complements (and
            // print out all the results
            int complement = j ^ 1;// j for the same input signals, but the
            // event doesn't occur
            cpt[complement] = (float) 1.0 - cpt[j];
            CPT.setElementAt(new Float(cpt[complement]), complement);
            CPT.setElementAt(new Float(cpt[j]), j);
            if(Library.DEBUG)
            {
                System.out.println("cpt[" + Integer.toHexString(complement) + "] = " + cpt[complement]);
                System.out.println("cpt[" + Integer.toHexString(j) + "] = " + cpt[j]);
            }
        }
        if(Library.DEBUG)
        {
            System.out.print("Inverting sense of {");
            for (Iterator i = inversions.iterator(); i.hasNext();)
            {
                Guid invertedSignal = (Guid) i.next();
                System.out.print((lib.getSignal(invertedSignal)).getSignalName());
                if (i.hasNext())
                {
                    System.out.print(", ");
                }
            }
            System.out.println("}");
        }
        invertEvents(order, CPT);
        if(Library.DEBUG)
        {
            System.out.println("Inversions should have been done.");
            for (int j = 0; j < cptSize; j++)
            {
                System.out.println("cpt[" + Integer.toHexString(j) + "] = "
                        + ((Float) CPT.elementAt(j)).toString());
            }
        }
        return retVal;
    }

    private void invertEvents(Vector order, Vector CPT)
    {
        Vector invert = new Vector();
        for (int j = 0; j < order.size(); j++)
        {
            if (inversions.contains((Guid) order.elementAt(j)))
            {
                invert.add(new Integer(j + 1)); // plus one to skip over the
                // event itself
            }
        }
        int lim = CPT.size();
        Vector temp = new Vector();
        temp.setSize(lim);
        for (int j = 0; j < lim; j++)
        {
            int newIndex = j;
            for (int k = 0; k < invert.size(); k++)
            {
                int pos = (((Integer) (invert.elementAt(k))).intValue());
                newIndex ^= (1 << pos);
            }
            temp.setElementAt(CPT.elementAt(j), newIndex);
        }
        for (int j = 0; j < lim; j++)
        {
            CPT.setElementAt(temp.elementAt(j), j);
        }
    }

    private float rnor(int j, float[] cpt, int size)
    {
        // still have to take care of singleton causes
        float entry = (float) 0.0;
        float num = (float) 1.0;
        float denom = (float) 1.0;
        for (int k = 1; k < size; k++)
        {//k starts at one to avoid the bit corresponding to the effect event itself thereby dealing with just the causal signals
            int i = (1 << k);
            if ((j & i) != 0)
            {// the ith bit in j is 1
                int jj = j ^ i;// so turn it off
                num *= (1.0 - cpt[jj]);// accumulate the numerator
                for (int q = 1; q < size; q++)
                {//q starts at 1 also to avoid selecting on the event itself
                    int r = q + k;
                    r = r < size ? r : r - size;
                    if (((jj & (1 << r)) != 0) && (r != 0))
                    {// the rth bit in jj is 1 and the bit we are removing is not the 'event' bit; this is not the only choice for denom, but it works
                        int jjj = jj ^ (1 << r);// so turn it off
                        if (jjj != 1)
                        {// if it is equal to 1, there are no active signals
                            denom *= (1.0 - cpt[jjj]);// accumulate the denominator
                            break;
                        }
                    }
                }
            }
        }
        if (denom == 0)
        {// make sure there is no floating point exception: 
            // divide by 0
            entry = (float) 1.0;
            if(Library.DEBUG)
            	logger.debug("rnor - rnor for " + j + "had a denominator of zero.");
        } else if (num > denom)
        {
            entry = (float) 0.0;
            if(Library.DEBUG)
            	logger.debug("rnor - rnor for " + j + " had a numerator greater than the denominator.");
        } else
        {// we sure hope this is what happens
            entry = (float) 1.0 - (num / denom);
            if (entry < (float) 0.0)
            {
                entry = (float) 0.0;
            }
        }
        return entry;
    }

    private int activeSigCount(int index, int sigCount)
    {
        int retVal = 0;
        for (int j = 1; j < sigCount; j++)
        {// j starts at 1 so we just count
            // the signal.
            if ((index & (1 << j)) != 0)
            {
                retVal += 1;
            }
        }
        return retVal;
    }

    public void organizeSimpleANDGroup(Collection grp, float andProb)
            throws SignalException
    {
        ProtocolSet p = findProtocol(grp);
        p.removeElicitationsInvolving(grp);// this removes elicitations from
        // protocol, p's, ElicitationSet
        p.removeAll(grp);// this removes signals from the protocol (which is an
        // extension of SignalSet
        /*
         * If we get here we are all cleaned up and ready to form the AND group:
         * we found all the signals in a RNOR protocol, took them out of the
         * protocola, and removed and elicitations involving these signals. So
         * now, lets actually make the AND group.
         */
        ProtocolSet and = new ProtocolSet(SignalType.GAND);
        and.constructAND(grp, andProb);
        protocols.add(and);
    }

    private ProtocolSet findProtocol(Collection grp) throws SignalException
    {
        ProtocolSet found = null;
        for (Iterator i = protocols.iterator(); i.hasNext();)
        {
            ProtocolSet p = (ProtocolSet) i.next();
            if (p.containsAll(grp))
            {
                found = p;
                break;
            };
            if (!i.hasNext())
            {
                throw new SignalException(
                        "No  protocol contains the entire group.");
            };
        }
        return found;
    }

    public ProtocolSet findProtocol(int protocolID) throws SignalException
    {
        ProtocolSet found = null;
        for (Iterator i = protocols.iterator(); i.hasNext();)
        {
            ProtocolSet p = (ProtocolSet) i.next();
            if (p.getProtocol() == protocolID)
            {
                found = p;
                break;
            }
            // TODO: CHECK THIS, this would fail if we happened to have more then 1 protocol would it not?
            // This else occurs and throws if the first protocol did not match therefore never checking
            // the rest of the iteration
            else
            {
                throw new SignalException("Protocol not found: ID = "
                        + protocolID);
            }
        }
        return found;
    }

    public void revertSignalsToDefaultProtocol(Collection signals,
            boolean deleteElicitations) throws SignalException
    {
        ProtocolSet p = findProtocol(signals);
        ProtocolSet r = findProtocol(SignalType.RNOR);
        SignalSet s = new SignalSet(signals);
        if (p != r)
        {
            ElicitationSet rElic = r.getElicitations();
            if (!deleteElicitations)
            {// go ahead and transfer the elicitations
                for (Iterator i = p.getElicitations().iterator(); i.hasNext();)
                {// transfer
                    // appropriate
                    // elicitations
                    // into
                    // RNOR
                    ElicitedProbability e = (ElicitedProbability) i.next();
                    if (s.containsAll((SignalSet) e))
                    {//the elicitation, e, is
                        // about a subset of
                        // signals, so move it to
                        // the RNOR protocol
                        /* it remains to be seen if user like this idea */
                        rElic.add(e);
                    }
                    i.remove();
                }
            }
            for (Iterator i = s.iterator(); i.hasNext();)
            {// transfer signals
                // between signal
                // sets
                Guid sig = (Guid) i.next();
                r.add(sig);
                p.remove(sig);
            }
            if (p.size() == 0)
            {
                protocols.remove(p);
            }
        }
    }
}
