/*
 * Created on May 12, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package mil.af.rl.jcat.processlibrary;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import mil.af.rl.jcat.exceptions.SignalException;
import mil.af.rl.jcat.exceptions.SignalModeConflictException;
import mil.af.rl.jcat.processlibrary.signaldata.ModeSet;
import mil.af.rl.jcat.processlibrary.signaldata.SignalData;
import mil.af.rl.jcat.processlibrary.signaldata.SignalSet;
import mil.af.rl.jcat.util.Guid;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 * @author John Lemmer
 * 
 */
public class Process implements Serializable
{
   
    private static final long serialVersionUID = 4900294022062107085L;
    Document processDocument = null;
    private SignalData processModel;
    String processName;
    private Guid processID;
    public int defaultsSubType = -1;
	public static int AND_OR_SUBTYPE_AND = 11;
	public static int AND_OR_SUBTYPE_OR = 12;
	private static Logger logger = Logger.getLogger(Process.class); 
    
    
    protected Process(float[] defaults, int defsSubType)
    {
        processName = "New Process";
        defaultsSubType = defsSubType;
        processID = new Guid();
        
        if(defaults != null) //null is used when Process is parsed from file (signaldata is created separately)
        {
	        if(defaultsSubType == AND_OR_SUBTYPE_OR && defaults.length == 8) //pasting into plan with diff defsType
	        	processModel = new SignalData(defaults[4], defaults[5], defaults[6], defaults[7]);
	        else
	        	processModel = new SignalData(defaults[0], defaults[1], defaults[2], defaults[3]);
        }
    }
    
    /**
     * This contstructor deletes any current object with the same guid from the
     * library, and inserts this new, empty process in its place. It also
     * initializes the library if it is not initialized.
     * 
     * @param guid
     *            the Guid object of this object
     * @param name
     *            the name to be used for visual display
     */
    protected Process(Guid guid, String name, float[] defaults, int defsSubType)
    {
    	this(defaults, defsSubType);
        processName = name;
        processID = guid;
    }
    

    public Process safeClone()
    {
        // Process p = new Process(new Guid(),this.processName,this.l);
        // p.setSignalData(processModel);
        // return p;
        return this;
    }
   
    /**
     * Method returns mode sets for the process, mainly used in copy/paste of the process
     * @return
     */
    public List<ModeSet> getModeSets()
    {
        return processModel.getModeSets();
    }

    public ModeSet getModeSet(int mode)
    {
    	return processModel.getModeSet(mode);
    }
    
    public float getDefault(int mode)
    {
    	return processModel.getDefault(mode);
    }
    
    public int getDefaultsSubType()
    {
    	return defaultsSubType;
    }
    
    public void setDefaultsSubType(int defSubType)
	{
		defaultsSubType = defSubType;
	}
    
    public void setDefault(int mode, float val)
    {
    	processModel.setDefault(mode, val);
    }
    
    /**
     * Restores or creates a process from the Document
     * 
     * @param xml
     *            the Document from which to create this process
     */
    public void treeWalk(Element el)
    {
        // parse the document
        for (int i = 0, size = el.nodeCount(); i < size; i++)
        {
            Node node = el.node(i);
            if (node instanceof Element)
            {
                Element El = (Element) node;
                String path = El.getPath();
                treeWalk(El);
            }

        }

    }

    public void setSignalData(SignalData data)
    {
        processModel = (SignalData) data.clone();
        
    }

    public SignalData getSignalData()
    {
        return processModel;
    }

    public static Process restoreProcessFromDocument(Element el, Library lib)
    {
        int k = 0;
        // if(el.getNodeTypeName() == "Element" && el.getName() == "Process"){
        if (el.getName().equals("Process"))
        {
            String name = el.valueOf("@name");
            if (name.compareTo("X") == 0)
            {
                k += 1;
            }
            String guid = el.valueOf("@guid");
            Guid theGuid = null;
            if (guid.equals(""))
            {
                theGuid = new Guid();
            } else
            {
                theGuid = new Guid(guid);
            }
            String dSF = el.valueOf("@defaultsSubType");
            int defSubType = -1;
            try{
            	defSubType = Integer.parseInt(dSF);
            }catch(Exception exc){
            	logger.warn("restoreProcessFromDoc - could not parse a defaultsSubType from document:  "+exc.getMessage());
            }
            
            
            // Create the process
            Process process = new Process(theGuid, name, null, defSubType);

            Node node = el.selectSingleNode("SignalData");
            if (node instanceof Element)
            {
                process.processModel = new SignalData(0f, 0f, 0f, 0f);
                process.processModel.restoreFromElement((Element) node, lib);
            }
            return process;
        }
        return null;
    }

    /**
     * Process Library where everything is made up and the return values dont matter.
     * 
     * @param sig
     * @param mode
     * @return
     * @throws SignalModeConflictException
     */
    public int addSignal(Signal sig, int mode) throws SignalModeConflictException
    {
        if(mode ==  SignalType.CAUSAL)
            processModel.addCause(sig);
        else if(mode == SignalType.INHIBITING)
            processModel.addInhibitor(sig);
        else if(mode == SignalType.EFFECT)
            processModel.addEffect(sig);
        return 0;
    }

    public boolean deleteSignal(Guid sig)
    {
        boolean retVal = false;
        for (Iterator i = processModel.entrySet().iterator(); i.hasNext();)
        {
            ModeSet ms = ((ModeSet) ((Map.Entry) (i.next())).getValue());
            if (ms != null && ms.contains(sig))
            {
                retVal = ms.deleteSignal(sig);
                break;
            }
        }
        return retVal;
    }

    /**
     * This method first checks to see that the signals are a subset of exactly
     * one of the modesets of this process. This implies that the signals must
     * have previously been placed into one of the process's mode sets. Then the
     * method checks to see that signals are not a subset of two or more
     * existing protocols. Having passed these tests, the signals are unioned
     * with and existing protocol set, or a new protocol set is added. Then the
     * elicatation itself is added to the set of elicitations of this protocol.
     * 
     * @param protocol
     *            As of 1 June 04, the only implemented protocol is RNOR.
     *            Protocols such as AND will be added later
     * @param signals
     *            the group of signals for which the probability is being
     *            provided. All signals in the group must already be in the same
     *            ModeSet, and in the same ProtocolSet. If they are not, a "Bad
     *            Elicited Value" exception will be thrown
     * @param prob
     *            The probability that the set of signals, acting together, but
     *            with no other signals, and ignoring any leak will [cause,
     *            inhibit, result from] the process].
     * @return SignalType for the protocol into which this elicited value is
     *         placed.
     */
    public boolean addElicitedValue(int protocol, Collection signals, float prob, String groupName)
    {
        try
        {
            return findApplicableMode(signals).addElicitedValue(protocol,
                    signals, prob, groupName);
        } catch (SignalException e)
        {
            logger.error("addElicitedValue - SignalExc, Elicited Value not added:  " + e.getMessage());
            return false;
        }
    }

    private ModeSet findApplicableMode(Collection signals)
            throws SignalException
    {
        ModeSet applicableMode = null;
        SignalSet group = new SignalSet(signals);
        for (Iterator i = processModel.entrySet().iterator(); i.hasNext();)
        {
            ModeSet m = ((ModeSet) ((Map.Entry) (i.next())).getValue());
            if (m.containsAll(group))
            {
                applicableMode = m;
                break; // better be that there is only one mode containing the
                // signals!
            }
        }
        if (applicableMode == null)
        {
            throw new SignalException("The group, " + signals.toString()
                    + ", not found in Causal, Inhibiting, or Effect modes.");
        }
        return applicableMode;
    }

    public float getElicitedValue(Collection signals)
    {
    	try{
            return findApplicableMode(signals).getElicitedValue(signals);
        }catch(SignalException e)
        {
        	logger.error("getElicitedValue - SignalExc, Elicited Value not found:  " + e.getMessage());
            return -1f;
        }
    }
    
    public boolean deleteElicitedValue(Collection signals)
    {
        try
        {
            return findApplicableMode(signals).deleteElicitedValue(signals);
        } catch (SignalException e)
        {
        	logger.error("deleteElicitedValue - SignalExc, Elicited Value not deleted:  " + e.getMessage());
            return false;
        }
    }

    /**
     * @param signal
     *            guid of signal whose inversion state is to be set
     * @param inverted
     *            true it invert the sense of the signal, false otherwise
     */
    public void setSignalInversion(Signal signal, boolean inverted)
    {
        processModel.setSignalInversion(signal, inverted);
    }

    /**
     * @param the
     *            document to which the current class needs to be added
     * @return the document with the Process information added
     */
    public Document getProcessDocument(Document doc)
    {
        return processDocument;
    }

    /**
     * @return The Guid associated with process. This Guid can be used to obtain
     *         a reference to this process from an instance of Process Library
     */
    public Guid getProcessID()
    {
        return processID;
    }

    /**
     * @return
     */
    public String getProcessName()
    {
        return processName;
    }

    /**
     * @param document
     */
    public void setProcessDocument(Document document)
    {
        processDocument = document;
    }

    /**
     * @param string
     */
    public void setProcessName(String string)
    {
        processName = string;
    }

    public Document addProcessToDocument(Document doc, Element el, Library library)
    {
        if (doc == null)
        {
            doc = DocumentHelper.createDocument();
            el = doc.addElement("Process").addAttribute("guid", getProcessID().getValue()).
            	addAttribute("name", getProcessName()).addAttribute("defaultsSubType", defaultsSubType+"");
        }
        else
        {
	        el = el.addElement("Process").addAttribute("guid", getProcessID().getValue()).
	        	addAttribute("name", getProcessName()).addAttribute("defaultsSubType", defaultsSubType+"");
        }
        
        processModel.addToDocument(doc, el, library);
        
        return doc;
    }
  
    /*
    public boolean getCPTable(Vector guidActiveSignals, Vector causalCPT,
            Vector causalOrder, Vector inhibitingCPT, Vector inhibitingOrder,
            Vector effectPT, Vector effectOrder, Library lib)
    {
        boolean retVal = true;
        if(Library.DEBUG)
            System.out.println("Computing CPT's for " + getProcessName() + ":"+ processID.getValue());
        retVal &= computeCPT(guidActiveSignals, SignalType.CAUSAL, causalOrder,
                causalCPT, lib);
        retVal &= computeCPT(guidActiveSignals, SignalType.INHIBITING,
                inhibitingOrder, inhibitingCPT, lib);
        retVal &= computeCPT(guidActiveSignals, SignalType.EFFECT, effectOrder,
                effectPT, lib);// This really has no meaning and we don't need
        // anythinglike this till we start eliminating
        // d-separation
        if (guidActiveSignals.size() > 0)
        {
            // probably throw an exception here
        }
        return retVal;
    }
    */

    public void revertSignalsToDefaultProtocol(Collection signals,
            boolean deleteElicitations) throws SignalException
    {
        processModel
                .revertSignalsToDefaultProtocol(signals, deleteElicitations);
    }

    protected boolean computeCPT(Vector guidActiveSignal, int mode, Vector order,
            Vector CPT, Library lib)
    {
        boolean retVal = false;
        ModeSet ms = (ModeSet) processModel.get(new Integer(mode));
        if (ms != null)
        {
            for (int j = guidActiveSignal.size() - 1; j >= 0; j--)
            {
                if (ms.contains(guidActiveSignal.elementAt(j)))
                {
                    order.add(guidActiveSignal.elementAt(j));
                    guidActiveSignal.remove(j);
                    retVal = true; // there is indeed something here
                }
            }
            // so compute the darn CPT!!
            retVal = ms.RNOR(order, CPT, lib);
        }
        return retVal;
    }

    public void organizeSimpleANDGroup(Collection grp, float andProb)
            throws SignalException
    {
        processModel.organizeSimpleANDGroup(grp, andProb);
    }

    /**
     * @return
     */
    public Collection getCausalSignals()
    {
        return processModel.getCausalSignals();
    }

    /**
     * @return
     */
    public Collection getEffectSignals()
    {
        return processModel.getEffectSignals();
    }

    /**
     * @return
     */
    public Collection getInhibitingSignals()
    {
        return processModel.getInhibitingSignals();
    }

	
}
