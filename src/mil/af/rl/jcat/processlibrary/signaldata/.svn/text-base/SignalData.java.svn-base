/*
 * Created on May 27, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package mil.af.rl.jcat.processlibrary.signaldata;

/**
 * @author John Lemmer This class packages all the Signal's associated with a
 *         Process. Currently signals operate in there modes: Causal,
 *         Inhibiting, and Effect. The semantics of Causal and Inhibititing are
 *         similar the same modes in CAST. They are however semantically much
 *         more rigoruously defined within CAT than they are within SIAM. Effect
 *         mode is not defined in SIAM. Using signals in the effect mode with
 *         probability of generation less than 1.0, removes the Strong Markov
 *         Assumption from CAT models, substitues only the Weak Causal Markon
 *         assumption. However if the probability of generating all effects is
 *         1.0 throughout the model, the strong form of the causal assumption is
 *         still valid.
 * 
 * Signals, together with their uncertainties, corresponding to each of these
 * these modes are contained in seperate ModeSets A particular mode set is
 * retrived using method "GetModeSet(SignalType).
 *  
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import mil.af.rl.jcat.exceptions.SignalException;
import mil.af.rl.jcat.exceptions.SignalModeConflictException;
import mil.af.rl.jcat.processlibrary.Library;
import mil.af.rl.jcat.processlibrary.Signal;
import mil.af.rl.jcat.processlibrary.SignalType;
import mil.af.rl.jcat.util.Guid;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;

public class SignalData extends HashMap<Integer,ModeSet>
{
	private static final long serialVersionUID = 1L;

	static final Integer CAUSAL = new Integer(SignalType.CAUSAL);
    static final Integer EFFECT = new Integer(SignalType.EFFECT);
    static final Integer INHIBITING = new Integer(SignalType.INHIBITING);
    static final int GROUP = SignalType.GROUP;

//    private float defC = -1f;
//    private float defI = -1f;
//    private float defE = -1f;
	private float defG = -1f;
	private static Logger logger = Logger.getLogger(SignalData.class); 
    
    
    /**
     * Creates the ModeSets for the causal, inhibiting, and effect signals
     *  
     */
    public SignalData(float causeDef, float inhibDef, float effectDef, float grpDef)
    {
//    	defC = causeDef;
//    	defI = inhibDef;
//    	defE = effectDef;
    	defG = grpDef;
    	
        put(CAUSAL, new ModeSet(SignalType.CAUSAL, causeDef));
        put(INHIBITING, new ModeSet(SignalType.INHIBITING, inhibDef));
        put(EFFECT, new ModeSet(SignalType.EFFECT, effectDef));
    }
    
    public float getDefault(int mode)
    {
    	if(mode == GROUP)
    		return defG;
    	else
    		return get(mode).getDefaultProbability();
    }
    
    public void setDefault(int mode, float val)
	{
    	if(mode == GROUP)
    		defG = val;
    	else
    		get(mode).setDefaultProbability(val);
	}
	
    
    public List<ModeSet> getModeSets()
    {
        List<ModeSet> modes = new ArrayList<ModeSet>(3);
        modes.addAll(this.values());
        return modes;
    }
    
    public ModeSet getModeSet(int mode)
    {
    	return get(mode);
    }

    public boolean addCause(Signal sig) throws SignalModeConflictException
    {
        if(this.getModeSet(INHIBITING).contains(sig.getSignalID()))
            throw new SignalModeConflictException("Signal: " + sig.toString() + " not added to CAUSAL signals. " +
                    "Signal is already present in INHIBITING signal set.");
        return addSignal(sig, CAUSAL);
    }

    public boolean addEffect(Signal sig)
    {
        return addSignal(sig, EFFECT);
    }

    public boolean addInhibitor(Signal sig) throws SignalModeConflictException
    {
        if(this.getModeSet(CAUSAL).contains(sig.getSignalID()))
            throw new SignalModeConflictException("Signal: " + sig.toString() + " not added to INHIBITING signals. " +
                    "Signal is already present in CAUSAL signal set.");
        return addSignal(sig, INHIBITING);
    }

    public boolean addSignal(Signal sig, Integer mode)
    {
        ModeSet modes = (ModeSet) get(mode);
        return modes.add(sig.getSignalID());
    }

    public Collection getCausalSignals()
    {
        return (ModeSet)get(new Integer(SignalType.CAUSAL));
    }
    
    public Collection getInhibitingSignals()
    {
        return (ModeSet)get(new Integer(SignalType.INHIBITING));
    }
    
    public Collection getEffectSignals()
    {
        return (ModeSet)get(new Integer(SignalType.EFFECT));
    }
    
    public boolean setSignalInversion(Signal sig, boolean invert)
    {
        boolean retVal = false;
        Vector aCollection = new Vector();
        aCollection.add(sig.getSignalID());
        ModeSet theModeSet = null;
        try
        {
            theModeSet = this.findModeSet(aCollection);
        } catch (SignalException e)
        {
            logger.warn("setSignalInversion - SignalExc, mode set containging the signal not found:  "+e.getMessage());
            e.printStackTrace();
        }
        theModeSet.setSignalInversion(sig, invert);

        return retVal;
    }

    public boolean containsSignal(Guid sigID)
    {
	    boolean retVal = false;
	    for(Iterator i = entrySet().iterator(); i.hasNext();)
	    {
		    ModeSet m = ((ModeSet) ((Map.Entry) (i.next())).getValue());
		    if(m.contains(sigID))
			    return true;
	    }
	    return retVal;
    }
    
    public boolean deleteSignal(Guid sig)
    {
        boolean retVal = false;
        for (Iterator i = entrySet().iterator(); i.hasNext();)
        {
            ModeSet m = ((ModeSet) ((Map.Entry) (i.next())).getValue());
            if (m.contains(sig))
            {
                retVal = m.deleteSignal(sig);
                break;
            }
        }
        return retVal;
    }

    /**
     * Recursively adds this object to input xml string
     * 
     * @param xml
     *            XML string to be added to
     * @return the augmented xml string
     */
    public String toXML(String xml, Library lib)
    {
        xml += "<SignalData>";
        xml = ((ModeSet) get(CAUSAL)).toXML(xml, lib);
        xml = ((ModeSet) get(EFFECT)).toXML(xml, lib);
        xml = ((ModeSet) get(INHIBITING)).toXML(xml, lib);
        xml += "</SignalData>";
        return xml;
    }

    public Document addToDocument(Document doc, Element el, Library lib)
    {
        Element el1 = el.addElement("SignalData");
        el1.addAttribute("defaultGroupProb", defG+"");
        doc = ((ModeSet) get(CAUSAL)).addToDocument(doc, el1, lib);
        doc = ((ModeSet) get(EFFECT)).addToDocument(doc, el1, lib);
        doc = ((ModeSet) get(INHIBITING)).addToDocument(doc, el1, lib);
        return doc;
    }

    public void restoreFromElement(Element el, Library lib)
    {
    	String groupDef = el.attributeValue("defaultGroupProb");
    	if(groupDef != null)
    		defG = Float.parseFloat(groupDef);
    	else
    		defG = .55f; //default group prob if one isn't loaded from file (an older plan that doesnt have this param)
    	
        ModeSet modeSet = null;
        for (Iterator i = el.elementIterator("ModeSet"); i.hasNext();)
        {
            Element modeSetEl = (Element) i.next();
            String modeType = modeSetEl.valueOf("@mode");
            if (modeType.equals("1"))
            {
                modeSet = (ModeSet) get(CAUSAL);
            }
            else if (modeType.equals("2"))
            {
                modeSet = (ModeSet) get(INHIBITING);
            }
            else if (modeType.equals("3"))
            {
                modeSet = (ModeSet) get(EFFECT);
            }
            
            modeSet.restoreFromElement(modeSetEl, lib);
        }
    }

    /*
     * public static void main (String[] args){ SignalData theData = new
     * SignalData(); ModeSet ms = (ModeSet)theData.get(SignalData.Causal);
     * ProtocolSet ps = new ProtocolSet(SignalType.RNOR); ms.addProtocol(ps); //
     * ElicitedProbability ep = new ElicitedProbability((float).69);
     * ms.addElicitation(ps, ep); ElicitedProbability ep1 = new
     * ElicitedProbability((float).47); ms.addElicitation(ps, ep1); // String
     * xml = " <?xml version=\"1.0\" ?>"; // xml = theData.toXML(xml); //
     * System.out.println(xml); Document output =
     * DocumentHelper.createDocument(); Element el = output.addElement("root");
     * theData.addToDocument(output, el); Process.writeDOM(output); }
     */

    
    public void organizeSimpleANDGroup(Collection grp, float andProb) throws SignalException
    {
        findModeSet(grp).organizeSimpleANDGroup(grp, andProb);
    }

    private ModeSet findModeSet(Collection grp) throws SignalException
    {
        ModeSet found = null;
        for (Iterator i = entrySet().iterator(); i.hasNext();)
        {
            ModeSet m = (ModeSet) (((Map.Entry) i.next()).getValue());
            if (m.containsAll(grp))
            {
                found = m;
                break;
            } else
            {
                if (!i.hasNext())
                {
                    throw new SignalException(
                            "No mode contains all the signals proposed for the new AND group.");
                }
            }
        }
        return found;
    }

    public void revertSignalsToDefaultProtocol(Collection signals,
            boolean deleteElicitations) throws SignalException
    {
        findModeSet(signals).revertSignalsToDefaultProtocol(signals,
                deleteElicitations);
    }

	
    
}

