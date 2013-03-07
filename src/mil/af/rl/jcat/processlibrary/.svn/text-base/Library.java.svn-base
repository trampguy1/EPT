/*
 * Created on Jun 1, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package mil.af.rl.jcat.processlibrary;

import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Vector;


import mil.af.rl.jcat.exceptions.SignalException;
import mil.af.rl.jcat.exceptions.SignalModeConflictException;
import mil.af.rl.jcat.util.Guid;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * @author John Lemmer
 * 
 */
public class Library implements LibraryInterface/* implements Remote */
{
    public static final boolean DEBUG = false;
    private HashMap<Guid,Signal> signalLibrary = new HashMap<Guid,Signal>();
    private HashMap<Guid,Process> processLibrary = new HashMap<Guid,Process>();
    private static Logger logger = Logger.getLogger(Library.class);
    
    
    public int addCause(Guid process, Guid signal)
            throws SignalModeConflictException
    {
        return addSignalToProcess(process, signal, SignalType.CAUSAL);
    }

    public int addEffect(Guid process, Guid signal)
            throws SignalModeConflictException
    {
        return addSignalToProcess(process, signal, SignalType.EFFECT);
    }

    public Signal getSignal(Guid sigID)
    {
        return signalLibrary.get(sigID);
    }

    /**
     * Method used to check if a given name exists in a signal library
     * 
     * @param name
     *            String
     * @return boolean
     */
    public boolean signalNameExists(String name)
    {
        Iterator vls = null;
        try
        {
            vls = signalLibrary.values().iterator();
        } catch (NullPointerException e)
        {
            return false;
        }
        for (; vls.hasNext();)
        {
            Signal s = (Signal) vls.next();
            if (s.getSignalName().equals(name))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * This method first checks to see that the signals are a subset of exactly
     * one of the modesets of the process. This implys that the signals must
     * have previously been placed into one of the process's mode sets. Then the
     * method checks to see that signals are not a subset of two or more
     * existing protocols. Having passed these tests, the signals are unioned
     * with and existing protocol set, or a new protocol set is added. Then the
     * elicatation itself is added to the set of elicitations of this protocol.
     * 
     * @param process
     *            The guid of the process whose SignalData is being modified
     * @param protocol
     *            As of 1 June 04, the only implemented protocol is RNOR.
     *            Protocols such as AND will be added later
     * @param signals
     *            the group of signals for which the probability is being
     *            provided. All signals in the group must already be in the same
     *            ModeSet, and in the same ProtocolSet. If they are not, a "Bad
     *            Signal Set" exception will be thrown
     * @param prob
     *            The probability that the set of signals, acting together, but
     *            with no other signals, and ignoring any leak will [cause,
     *            inhibit, result from] the process].
     * @return SignalType for the protocol into which this elicited value is
     *         placed.
     */
    public boolean addElicitedValue(Guid process, int protocol, Collection signals, float prob, String groupName)
    {
        return (processLibrary.get(process)).addElicitedValue(protocol, signals, prob, groupName);
    }

    public float getElicitedValue(Guid process, Collection signals)
    {
    	return processLibrary.get(process).getElicitedValue(signals);
    }
    
    public int addInhibitor(Guid process, Guid signal)
            throws SignalModeConflictException
    {
        return addSignalToProcess(process, signal, SignalType.INHIBITING);
    }

    public Document addProcessToDocument(Guid proc, Document data, Element el)
    {
        Process theProc = processLibrary.get(proc);
        theProc.addProcessToDocument(data, el, this);
        return data;
    }

    public Document addSignalToDocument(Guid sig, Document doc, Element el)
    {
        Signal theSig = signalLibrary.get(sig);
        theSig.addSignalToDocument(doc, el);
        return doc;
    }

    private int addSignalToProcess(Guid process, Guid signal, int mode)
            throws SignalModeConflictException
    {

        int retVal = 0;
        Process p = processLibrary.get(process);
        Signal s =  signalLibrary.get(signal);
        if (p != null && s != null)
        { // gotta find 'em both or ya cain't do ut.
            retVal = p.addSignal(s, mode);
        }
        return retVal;
    }

    public Guid createProcess(float[] defaults, int defaultsSubType)
    {
        Process p = new Process(defaults, defaultsSubType);
        this.processLibrary.put(p.getProcessID(), p);
        return p.getProcessID();
    }
    
    public Guid createProcess(Guid guid, String name, float[] defaults, int defaultsSubType)
    {
        Process p = new Process(guid, name, defaults, defaultsSubType);
        processLibrary.put(guid, p);
        return p.getProcessID();
    }

    public Guid createSignal()
    {
        Signal s = new Signal();
        this.signalLibrary.put(s.getSignalID(), s);
        return s.getSignalID();
    }

    public Guid createSignal(Guid sigID, String sigName)
    {
        Signal s = new Signal(sigID, sigName);
        signalLibrary.put(sigID, s);
        return s.getSignalID();

    }

    public boolean deleteElicitedValue(Guid process, Collection signals)
    {
        return ( processLibrary.get(process))
                .deleteElicitedValue(signals);
    }

    /*
     * (non-Javadoc)
     * 
     * @see mil.af.rl.jcat.processlibrary.LibraryInterface#deleteSignal(mil.af.rl.jcat.util.Guid,
     *      mil.af.rl.jcat.util.Guid)
     */
    public boolean deleteSignalFromProcess(Guid process, Guid signal)
    {
        return processLibrary.get(process).deleteSignal(signal);
    }

    public void deleteSignal(Guid guid)
    {
    	signalLibrary.remove(guid);    	
    }
    
    /**
     * Opens a saved library in .xml Format Extracts ProcessLibrary Element and
     * passes that element on to deserializeLibrary(Element el)
     * 
     * @param fromURL -
     *            The url location of the xml file
     */
    public boolean deserializeLibrary(String fromURL)
    {
        Document doc = null;
        try
        {
            SAXReader reader = new SAXReader();
            doc = reader.read(fromURL);
            writeDOM(doc);
        } catch (DocumentException e)
        {
            logger.error("deserializeLibrary - DocumentExc parsing library:  "+e.getMessage());
        }
        Element el = doc.getRootElement();
        return deserializeLibrary(el);
    }
    
    static public void writeDOM(Document doc)
    {
        OutputFormat format = OutputFormat.createPrettyPrint();
        try
        {
            XMLWriter writer = new XMLWriter(System.out, format);
            writer.write(doc);
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Loads a ProcessLibrary from a file. Used to make plans protable and
     * Libraries plugable
     * 
     * @param el -
     *            The ProcessLibrary Element from an XML file
     * 
     * @return Sucessful Finish
     */
    public boolean deserializeLibrary(Element el)
    {
        if (el.getNodeTypeName().equals("Element")
                && el.getName().equals("ProcessLibrary"))
        {
            Node node = el.selectSingleNode("TheProcesses");
            if (node instanceof Element)
            {
                // processLibrary = null;
                Element elp = (Element) node;
                for (Iterator p = elp.elementIterator("Process"); p.hasNext();)
                {
                    Element psE = (Element) p.next();
                    Process process = Process.restoreProcessFromDocument(psE, this);
                    this.processLibrary.put(process.getProcessID(), process);
                }
            }
            node = el.selectSingleNode("TheSignals");
            if (node instanceof Element)
            {
                // signalLibrary = null;
                Element elS = (Element) node;
                for (Iterator p = elS.elementIterator("Signal"); p.hasNext();)
                {
                    Element e = (Element) p.next();
                    Signal s = Signal.restoreFromElement(e);
                    this.signalLibrary.put(s.getSignalID(), s);
                }
            }
        }
        return true;
    }

    /**
     * @see mil.af.rl.jcat.processlibrary.LibraryInterface#getCPTable(mil.af.rl.jcat.util.Guid,
     *      java.util.Vector, java.util.Vector, java.util.Vector,
     *      java.util.Vector, java.util.Vector, java.util.Vector,
     *      java.util.Vector) Accepts the active signals for which a Conditional
     *      Probability Table (CPT) is wanted. The method groups these signals
     *      by mode (causal, inhibiting, or effect).. The method establishes the
     *      CPT indexing order for each group and returns this order also.
     *      Finally, th CPT for each group, in indexint order is also returned.
     */
    /*public boolean getCPTable(Guid p, Vector guidActiveSignals,
            Vector causalCPT, Vector causalOrder, Vector inhibitingCPT,
            Vector inhibitingOrder, Vector effectPT, Vector effectOrder)
            throws SignalException
    {
        boolean retVal = false;
        Process proc = (Process) processLibrary.get(p);
        if (proc != null)
        {
            retVal = proc
                    .getCPTable(guidActiveSignals, causalCPT, causalOrder,
                            inhibitingCPT, inhibitingOrder, effectPT,
                            effectOrder, this);
            if (guidActiveSignals.size() > 0)
            {
                SignalSet leftovers = new SignalSet(guidActiveSignals);
                String message = "The signals in the set, "
                        + leftovers.listNames(this)
                        + "are not associated with " + proc.getProcessName()
                        + ".";
                System.err.println(message);
                throw new SignalException(message);
            }
        } else
        {
            String message = "Process with GUID = " + p + " was not found.";
            System.err.println(message);
            throw new SignalException(message);
        }
        return retVal;
    }*/
    
    public boolean getCPT(Guid id, Vector activeSignals, Vector cpt, Vector order, int type) throws SignalException
    {
        if(activeSignals.size() == 0)
            return false;
        Process proc =  processLibrary.get(id);        
        if(proc == null)
            throw new SignalException("Process with GUID = " + id + " was not found.");        
        proc.computeCPT(activeSignals, type, order, cpt, this);        
        return true;
    }

//    /**
//     * @return Returns the defaultSingleSignalCausalProbability.
//     */
//    public float getDefaultSingleSignalCausalProbability()
//    {
//    	System.out.println("Library: getDefaultSingleSignalCausalProb()");
//        return SignalData.getDefaultSingleSignalCausalProbability();
//    }

//    /**
//     * @return Returns the defaultSingleSignalEffectProbability.
//     */
//    public float getDefaultSingleSignalEffectProbability()
//    {
//        return SignalData.getDefaultSingleSignalEffectProbability();
//    }

//    /**
//     * @return Returns the defaultSingleSignalInhibitProbability.
//     */
//    public float getDefaultSingleSignalInhibitProbability()
//    {
//        return SignalData.getDefaultSingleSignalInhibitProbability();
//    }

    public Document getProcessDocument(Guid guid)
    {
        // TODO Fix this aspect of the library
        Process theProcess = (Process) processLibrary.get(guid);
        if (theProcess == null)
        {
            logger.error("getProcessDocument - Cannot get process: process with guid = " + guid + "is not in the default library.");
            return null;
        }

        return theProcess.addProcessToDocument(null, null, this);
    }

    public void addSignal(Signal signal)
    {
        signalLibrary.put(signal.getSignalID(), signal);
    }

    public void addProcess(Process process)
    {
        // System.out.println("Causal signals: \n"+process.getCausalSignals());
        processLibrary.put(process.getProcessID(), process);
    }

    public boolean processExists(Guid pid)
    {
        return processLibrary.containsKey(pid);
    }

    public boolean setSignalInversion(Guid proc, Guid sig, boolean inverted)
    {
        Process theProc = processLibrary.get(proc);
        Signal theSig = signalLibrary.get(sig);
        if (theProc == null || theSig == null)
        {
            return false;
        } else
        {
            theProc.setSignalInversion(theSig, inverted);
            return true;
        }
    }

    public void organizeSimpleANDGroup(Guid p, Collection grp, float andProb)
            throws SignalException
    {
        Process proc =  processLibrary.get(p);
        if (proc == null)
        {
            throw new SignalException("No AND group formed: proceess for Guid "
                    + p.getValue() + " not found.");
        } else
        {
            proc.organizeSimpleANDGroup(grp, andProb);
        }
    }

    /**
     * This method searches all ModeSets for a protocol, p, containing all the
     * signals in the 'signals' collection. If p is not the default protocol,
     * RNOR, it removes these signals from the protocol and transfers them to
     * the RNOR protocol. If protocol, p, becomes empty, it will be removed from
     * the ModeSet. Thus, if all signals in a protocol are in 'signals' this has
     * the effect of deleting this protocol. Note that the signals are not
     * removed from the ModeSet, but are moved into the 'RNOR' protocol. If
     * 'deleteElicitations' is true all elicited values involving any of these
     * signal will be deleted. Otherwise elicitation involving only subsets of
     * 'signals' will be moved to the RNOR protocol, and elicitations involving
     * signals which intersect 'signals' but are not a subset of 'signals' will
     * be deleted.
     * 
     * @param process
     *            the Guid of the process on which the action is to be taken
     * @param signals
     *            must be a subset of the SignalSet of some ProtocolSet;
     *            otherwise a SignalException will be thrown
     * @param deleteElicitaions
     *            see above
     */
    public void revertSignalsToDefaultProtocol(Guid process,
            Collection/* of Guids */signals, boolean deleteElicitations)
            throws SignalException
    {
        Process p =  processLibrary.get(process);
        if (p == null)
        {
            throw new SignalException("Process not found in library: guid = "
                    + process.getValue());
        } else
        {
            p.revertSignalsToDefaultProtocol(signals, deleteElicitations);
        }
    }

    /**
     * Method returns the library as a XML Document.
     * 
     * @return Document library
     */
    public Document getLibraryDocument()
    {
        Document doc = DocumentHelper.createDocument();
        Element el = doc.addElement("ProcessLibrary");

        Set procs = processLibrary.entrySet();
        Element elp = el.addElement("TheProcesses");
        for (Iterator i = procs.iterator(); i.hasNext();)
        {
            Process theProc = ((Process) ((Map.Entry) (i.next())).getValue());
            theProc.addProcessToDocument(doc, elp, this);
        }

        Element els = el.addElement("TheSignals");
        Set sigs = signalLibrary.entrySet();
        for (Iterator i = sigs.iterator(); i.hasNext();)
        {
            Signal theSig = ((Signal) ((Map.Entry) (i.next())).getValue());
            theSig.addSignalToDocument(doc, els);
        }
        return doc;
    }

    public boolean serializeLibrary(String toURL)
    {
        boolean retVal = false;
        Document doc = DocumentHelper.createDocument();
        Element el = doc.addElement("ProcessLibrary");

        Set procs = processLibrary.entrySet();
        Element elp = el.addElement("TheProcesses");
        for (Iterator i = procs.iterator(); i.hasNext();)
        {
            Process theProc = ((Process) ((Map.Entry) (i.next())).getValue());
            theProc.addProcessToDocument(doc, elp, this);
        }

        Element els = el.addElement("TheSignals");
        Set sigs = signalLibrary.entrySet();
        for (Iterator i = sigs.iterator(); i.hasNext();)
        {
            Signal theSig = ((Signal) ((Map.Entry) (i.next())).getValue());
            theSig.addSignalToDocument(doc, els);
        }

        try
        {
            FileWriter out = new FileWriter(toURL);
            OutputFormat outformat = OutputFormat.createPrettyPrint();
            XMLWriter writer = new XMLWriter(out, outformat);
            writer.write(doc);
            writer.flush();
            writer.close();
        } catch (Exception e)
        {
            logger.error("serializeLibrary - XML Output failed:  "+e.getMessage());
        }

        /*
         * try { OutputFormat format = OutputFormat.createPrettyPrint();
         * XMLWriter writer = new XMLWriter(System.out, format);
         * writer.write(doc); } catch (UnsupportedEncodingException e) {
         * Auto-generated catch block e.printStackTrace(); } catch (IOException
         * e) { e.printStackTrace(); }
         */
        return retVal;
    }

//    /**
//     * @param defaultSingleSignalCausalProbability
//     *            The defaultSingleSignalCausalProbability to set.
//     */
//    public void setDefaultSingleSignalCausalProbability(
//            float defaultSingleSignalCausalProbability)
//    {
//        SignalData.setDefaultSingleSignalCausalProbability(defaultSingleSignalCausalProbability);
//    }
//
//    /**
//     * @param defaultSingleSignalEffectProbability
//     *            The defaultSingleSignalEffectProbability to set.
//     */
//    public void setDefaultSingleSignalEffectProbability(
//            float defaultSingleSignalEffectProbability)
//    {
//        SignalData
//                .setDefaultSingleSignalEffectProbability(defaultSingleSignalEffectProbability);
//    }
//
//    /**
//     * @param defaultSingleSignalInhibitProbability
//     *            The defaultSingleSignalInhibitProbability to set.
//     */
//    public void setDefaultSingleSignalInhibitProbability(
//            float defaultSingleSignalInhibitProbability)
//    {
//        SignalData
//                .setDefaultSingleSignalInhibitProbability(defaultSingleSignalInhibitProbability);
//    }

    public void setProcessFromDocument(Document data, Element el)
    {
        if (el == null)
        {
            el = data.getRootElement();
        }
        String n = el.getName();
        if (n.equals(new String("Process")))
        {
            Process p = Process.restoreProcessFromDocument(el, this);
            this.processLibrary.put(p.getProcessID(), p);
        }
    }

    public void setProcessName(Guid proc, String name)
    {
        ((Process) (processLibrary.get(proc))).setProcessName(name);
    }

    public void setSignalName(Guid sig, String name)
    {
        ((Signal) (signalLibrary.get(sig))).setSignalName(name);
    }

    public String getProcessName(Guid proc)
    {
        return ((Process) (processLibrary.get(proc))).getProcessName();
    }

    public String getSignalName(Guid sig)
    {
        if (signalLibrary.get(sig) == null)
            return null;
        return ((Signal) (signalLibrary.get(sig))).getSignalName();
    }
    
    public void getSignalInhibitingCPT(Guid signalID, Vector inhibitingCPT){
    	((Signal) signalLibrary.get(signalID)).getSignalInhibitingCPT(inhibitingCPT);    	
  
    }

    public void getSignalCPT(Guid signalID, Vector activeSignals,
            Vector causalCPT, Vector causalOrder)
    {
        ((Signal) signalLibrary.get(signalID)).getSignalCPT(activeSignals,
                causalCPT, causalOrder);
    }

    public void getSignalAdder(Guid signalID, Vector activeSignals,
            Vector causalCPT, Vector causalOrder)
    {
        ((Signal) signalLibrary.get(signalID)).getSignalAdder(activeSignals,
                causalCPT, causalOrder);
    }

    /**
     * @return
     */
    public Collection getCausalSignals(Guid id)
    {
        LinkedList c = new LinkedList();
        Process p =  processLibrary.get(id);
        Collection l = p.getCausalSignals();
        for (Iterator i = l.iterator(); i.hasNext();)
        {
            c.add(this.signalLibrary.get(i.next()));
        }
        return c;
    }

    /**
     * @return
     */
    public Collection getEffectSignals(Guid id)
    {
        LinkedList c = new LinkedList();
        Process p =  processLibrary.get(id);
        Collection l = p.getEffectSignals();
        for (Iterator i = l.iterator(); i.hasNext();)
        {
            c.add(this.signalLibrary.get(i.next()));
        }
        return c;
    }

    /**
     * @return
     */
    public Collection getInhibitingSignals(Guid id)
    {
        LinkedList c = new LinkedList();
        Process p =  processLibrary.get(id);
        Collection l = p.getInhibitingSignals();
        for (Iterator i = l.iterator(); i.hasNext();)
        {
            c.add(this.signalLibrary.get(i.next()));
        }
        return c;
    }

    public Process getProcess(Guid pguid)
    {
        return  this.processLibrary.get(pguid);
    }

    public Collection<Signal> getAllSignals()
    {
        return signalLibrary.values();
    }

    public Collection<Process> getAllProcesses()
    {
    	return processLibrary.values();
    }

    public void removeSignal(Guid signalID)
    {
        this.signalLibrary.remove(signalID);
    }

	
    public boolean isEmpty()
	{
		if(processLibrary.isEmpty() && signalLibrary.isEmpty())
			return true;
		else
			return false;
	}


}
