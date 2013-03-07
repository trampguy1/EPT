
package mil.af.rl.jcat.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import mil.af.rl.jcat.control.RemSignalArg;
import mil.af.rl.jcat.exceptions.SignalModeConflictException;
import mil.af.rl.jcat.processlibrary.Library;
import mil.af.rl.jcat.processlibrary.Process;
import mil.af.rl.jcat.processlibrary.Signal;
import mil.af.rl.jcat.processlibrary.SignalType;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.dom4j.util.NodeComparator;


/**
 * <p>
 * Title: ProcessUtil.java
 * </p>
 * <p>
 * Description: Utility methods for manipulating process documents
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: C3I Associates
 * </p>
 * 
 * @author Edward V.
 * @version 1.0
 */

public class ProcessUtil
{

	public static final int CAUSAL = SignalType.CAUSAL;
	public static final int EFFECT = SignalType.EFFECT;
	public static final int INHIBIT = SignalType.INHIBITING;
	private static Logger logger = Logger.getLogger(ProcessUtil.class);

	public ProcessUtil()
	{
	}

	/**
	 * Method adds an elicited value for a collection of signals for a given
	 * mode and protocol.
	 * 
	 * @param mode
	 *            int
	 * @param protocol
	 *            int
	 * @param signals
	 *            Collection
	 * @param prob
	 *            float
	 * @param document
	 *            Document
	 * @return Document
	 */
	public static Document addElicitedValue(int mode, int protocol, Collection signals, float prob, Document document)
	{
		// first check if the collection exists within the mode and protocol
		if(signalsExist(signals, document, mode, protocol))
		{
			// create the elicited probability element
			Element elp = DocumentHelper.createElement("ElicitedProbability");
			elp.addAttribute("causalprobability", "" + prob);
			elp.addElement("SignalSet");
			Iterator i = signals.iterator();
			for(; i.hasNext();)
			{
				Element sig = DocumentHelper.createElement("Signal");
				Signal nextSig = (Signal) i.next();
				sig.addAttribute("guid", nextSig.getSignalID().getValue());
				sig.addAttribute("name", nextSig.getSignalName());
				elp.element("SignalSet").add(sig);
			}
			// insert the element inside the document
			Element e = (Element) document.selectSingleNode("//ModeSet[@mode='" + mode + "']/ProtocolSet[@protocol='" + protocol
					+ "']/ElicitationSet");

			List content = e.content();
			content.add(0, elp);
		}

		return document;
	}

	public static Document addSingleElicitedValue(int mode, int protocol, Signal signal, float prob, Document document)
	{

		// create the elicited probability element
		Element elp = DocumentHelper.createElement("ElicitedProbability");
		elp.addAttribute("causalprobability", Float.toString(prob));
		elp.addElement("SignalSet");

		Element sig = DocumentHelper.createElement("Signal");
		sig.addAttribute("guid", signal.getSignalID().getValue());
		sig.addAttribute("name", signal.getSignalName());
		elp.element("SignalSet").add(sig);

		// insert the element inside the document
		Element e = (Element) document.selectSingleNode("//ModeSet[@mode='" + mode + "']/ProtocolSet[@protocol='" + protocol
				+ "']/ElicitationSet");

		List content = e.content();
		content.add(0, elp);

		return document;
	}

	/**
	 * Compares if two Elicited Probabilities in XML form (Element) are equal.
	 * 
	 * @param one
	 *            Element
	 * @param two
	 *            Element
	 * @return boolean
	 */
	public static synchronized boolean isEPEqual(Element one, Element two)
	{
		// first check for causal probabilities
		float pone = Float.parseFloat(one.attributeValue("causalprobability"));
		float ptwo = Float.parseFloat(two.attributeValue("causalprobability"));
		if(pone != ptwo)
		{
			return false;
		}
		// now compare # of signals
		List sone = one.selectNodes("./SignalSet/*");
		List stwo = two.selectNodes("./SignalSet/*");
		if(sone.size() != stwo.size())
		{
			return false;
		}
		Iterator io = sone.iterator();
		for(; io.hasNext();)
		{
			Element signal = (Element) io.next();
			String guid = signal.attributeValue("guid");
			if(two.selectSingleNode("//Signal[@guid='" + guid + "']") == null)
			{
				return false;
			}
		}

		return true;
	}

	/**
	 * Compares if the signals from two Elicited Probabilities in XML form
	 * (Element) are equal.
	 * 
	 * @param one
	 *            Element
	 * @param two
	 *            Element
	 * @return boolean
	 */
	public static synchronized boolean isSPEqual(Element one, Element two)
	{
		// now compare # of signals
		List sone = one.selectNodes("./SignalSet/*");
		List stwo = two.selectNodes("./SignalSet/*");
		if(sone.size() != stwo.size())
		{
			return false;
		}
		//Since the Sizes are equal ew can check the contents together
		//The way an elicitation set is constructed equal sets should have the
		//same signal occuring at the same element of the list
		for(int i = 0; i < sone.size(); i++)
		{
			String e1 = ((Element) sone.get(i)).attributeValue("guid");
			String e2 = ((Element) stwo.get(i)).attributeValue("guid");
			if(e1 != e2)
				return false;
		}
		//Iterator io = sone.iterator();
		/*
		 * for(;io.hasNext();) { Element signal = (Element)io.next(); String
		 * guid = signal.attributeValue("guid"); Node n =
		 * two.selectSingleNode("//Signal[@guid='"+guid+"']"); if(n.getText() ==
		 * null) { return false; } }
		 */

		return true;
	}

	/**
	 * Method used to delete an elicited value from a process document.
	 * 
	 * @param mode
	 *            int
	 * @param protocol
	 *            int
	 * @param signals
	 *            Collection
	 * @param prob
	 *            float
	 * @param document
	 *            Document
	 * @return Document
	 */
	public static Document deleteElicitedValue(int mode, int protocol, Collection signals, float prob, Document document)
	{
		Document doc = document;
		/* first create an element that we can use to compare two nodes */
		Element ep = DocumentHelper.createElement("ElicitedProbability").addAttribute("causalprobability", "" + prob);
		Element sigset = DocumentHelper.createElement("SignalSet");
		Iterator sigs = signals.iterator();
		for(; sigs.hasNext();)
		{
			Signal signal = (Signal) sigs.next();
			Element signode = sigset.addElement("Signal").addAttribute("guid", signal.getSignalID().getValue());
			signode.addAttribute("name", signal.getSignalName());
		}
		// finally, add the sigset to the parent node
		ep.add(sigset);

		// now, get all element that match the mode, protocol & probability
		List probs = document.selectNodes("//ModeSet[@mode='" + mode + "']/ProtocolSet[@protocol='" + protocol
				+ "']/ElicitationSet/ElicitedProbability[@causalprobability='" + prob + "']");
		// iterate through the whole list and delete whatever matches the
		// element
		Iterator ei = probs.iterator();
		NodeComparator comp = new NodeComparator();
		for(; ei.hasNext();)
		{
			Element original = (Element) ei.next();

			if(ProcessUtil.isEPEqual(ep, original))
			{
				// delete the original
				original.getParent().remove(original);
				return document;
			}
		}

		return doc;
	}

	public static Document deleteSingleElicitedValue(int mode, Signal signal, float prob, Document document)
	{
		int protocol = findSignalProtocol(signal, document, mode);
		Document doc = document;
		/* first create an element that we can use to compare two nodes */
		Element ep = DocumentHelper.createElement("ElicitedProbability").addAttribute("causalprobability", "" + prob);
		Element sigset = DocumentHelper.createElement("SignalSet");

		Element signode = sigset.addElement("Signal").addAttribute("guid", signal.getSignalID().getValue());
		signode.addAttribute("name", signal.getSignalName());

		// finally, add the sigset to the parent node
		ep.add(sigset);

		// now, get all element that match the mode, protocol & probability
		List probs = document.selectNodes("//ModeSet[@mode='" + mode + "']/ProtocolSet[@protocol='" + protocol + "']/ElicitationSet/*");//ElicitedProbability/*");
		// iterate through the whole list and delete whatever matches the
		// element
		Iterator ei = probs.iterator();
		NodeComparator comp = new NodeComparator();
		for(; ei.hasNext();)
		{
			Element original = (Element) ei.next();

			if(ProcessUtil.isSPEqual(ep, original))
			{
				// delete the original
				original.getParent().remove(original);
				return document;
			}
		}

		return doc;
	}

	/**
	 * Method adds a signal to the process document for a given mode
	 * 
	 * @param document
	 *            Document
	 * @param sig
	 *            Signal
	 * @param mode
	 *            int
	 * @return Document
	 */
	public static Document addSignal(Document document, Signal sig, int mode)
	{
		try
		{
			Element signal = DocumentHelper.createElement("Signal");
			signal.addAttribute("guid", sig.getSignalID().getValue());
			signal.addAttribute("name", sig.getSignalName());
			// insert the element inside the document
			Element e = (Element) document.selectSingleNode("//ModeSet[@mode='" + mode + "']/SignalSet");
			List content = e.content();
			content.add(0, signal);
		}catch(Exception ex)
		{
			logger.error("addSignal - Error adding signal to document:  " + ex.getMessage());
		}

		return document;

	}

	/**
	 * Method deletes all signal occurences in a process document for a given
	 * mode.
	 * 
	 * @param document
	 *            Document
	 * @param sig
	 *            Signal
	 * @param mode
	 *            int
	 * @return Document
	 */
	public static Document deleteSignal(Document document, Signal sig, int mode)
	{
		Element emode = (Element) document.selectSingleNode("//ModeSet[@mode='" + mode + "']");
		List signals = emode.selectNodes(".//Signal[@guid='" + sig.getSignalID().getValue() + "']");
		Iterator it = signals.iterator();
		for(; it.hasNext();)
		{
			Element e = (Element) it.next();
			e.getParent().remove(e);
		}

		return document;

	}

	/**
	 * Finds which protocol the signal exists in
	 * 
	 * @return returns protocol number or -1 if not found
	 */
	public static int findSignalProtocol(Signal sig, Document doc, int mode)
	{
		if(signalExists(sig, doc, mode, 4))
		{
			return 4;
		}
		else if(signalExists(sig, doc, mode, 5))
		{
			return 5;
		}
		else
			return -1;

	}

	/**
	 * Method checks if a collection of signals exists within a documents under
	 * a given mode and protocol
	 * 
	 * @param signals
	 *            Collection
	 * @param doc
	 *            Document
	 * @param mode
	 *            int
	 * @param protocol
	 *            int
	 * @return boolean
	 */
	public static boolean signalsExist(Collection signals, Document doc, int mode, int protocol)
	{
		Iterator it = signals.iterator();
		for(; it.hasNext();)
		{
			if(!signalExists((Signal) it.next(), doc, mode, protocol))
			{
				return false;
			}
		}

		return true;
	}

	/**
	 * Method checks if a single Signal exists within a document under a
	 * specified mode and protocol
	 * 
	 * @param signal
	 *            Signal
	 * @param doc
	 *            Document
	 * @param mode
	 *            int
	 * @param protocol
	 *            int
	 * @return boolean
	 */
	public static boolean signalExists(Signal signal, Document doc, int mode, int protocol)
	{
		String gvalue = signal.getSignalID().getValue();
		Node result = doc.selectSingleNode("//ModeSet[@mode='" + mode + "']/ProtocolSet[@protocol='" + protocol
				+ "']/SignalSet/Signal[@guid='" + gvalue + "']");
		if(result == null)
		{
			return false;
		}

		return true;
	}

	/**
	 * Method parses a document based on the table type and returns an array of
	 * objects in order for the table to display them and build the table model
	 * 
	 * @param doc
	 *            Document
	 * @param tabletype
	 *            int
	 * @return Object[]
	 */
	public static Object[][] parseProcess(Document doc, int mode) throws Exception
	{
		Object[][] datavector = null;
		XPath query = DocumentHelper.createXPath("//ModeSet[@mode='" + mode + "']");
		List modeset = query.selectNodes(doc, query);

		int maxGrpColumns = 1;

		if(modeset == null)
		{
			throw new Exception("There is no signal set for mode: " + mode);
		}
		/* theoretically we should not have more than one set of the same mode */
		Element e = (Element) modeset.get(0);
		/* query for a signal set */
		query = DocumentHelper.createXPath("//ModeSet[@mode='" + mode + "']/SignalSet/Signal");
		List signals = query.selectNodes(e, query);
		/*
		 * max # of group columns, taking into account that alone probs are also
		 * groups
		 */
		maxGrpColumns = ((int) Math.pow(2, signals.size()) - (signals.size() + 1)) * 2;

		String[] sigarray = new String[signals.size() + 3];
		Iterator i = signals.iterator();
		for(int x = 0; i.hasNext(); x++)
		{
			sigarray[x] = ((Element) i.next()).attributeValue("name");
		}
		// the last three should be the same all the time
		sigarray[sigarray.length - 3] = "Protocol";
		sigarray[sigarray.length - 2] = "Combined";
		sigarray[sigarray.length - 1] = "";

		// find out how many elicited probabilities are inside this mode set,
		// this will
		// tell us the number of groups we need to create in the datavector
		query = DocumentHelper.createXPath("//ModeSet[@mode='" + mode + "']/ProtocolSet/ElicitationSet/ElicitedProbability");
		List temp = query.selectNodes(e, query);

		//Seperate out the single elicitations
		List elicitations = new LinkedList();
		for(int q = 0; q < temp.size(); q++)
		{
			List probs = ((Element) temp.get(q)).element("SignalSet").elements("Signal");
			if(probs.size() > 1)
				elicitations.add((Element) temp.get(q));

		}
		// now we know the size of the datavector: object[causes + 3][# of
		// elicitations + 2]
		datavector = new Object[sigarray.length][elicitations.size() + 3];
		// debug stuff note: use elicited propabilities instead of elicitations
		// sets to calculate groups

		// populate the first column
		for(int y = 0; y < sigarray.length; y++)
		{
			datavector[y][0] = sigarray[y];
		}
		// second column, where the default propabilities live
		for(int y = 0; y < (sigarray.length - 3); y++)
		{
			datavector[y][1] = new Double(0.75);
		}
		// last three all all blank
		datavector[sigarray.length - 3][1] = "";
		datavector[sigarray.length - 2][1] = "";
		datavector[sigarray.length - 1][1] = "";

		/*
		 * in order to get all the elicited probabilities, we must first get all
		 * of the protocol sets within a single mode set, in here we assume that
		 * no duplicate signal sets exist for the same elicited probability
		 * within the same elicitation set
		 */

		query = DocumentHelper.createXPath("//ModeSet[@mode='" + mode + "']/ProtocolSet");
		List plist = query.selectNodes(e, query);
		for(Iterator it = plist.iterator(); it.hasNext();)
		{
			Element protocolset = (Element) it.next();
			// for each protocol find all elicitation sets and populate the data
			int protocol = protocolset.attributeValue("protocol") == null ? 4 : Integer.parseInt(protocolset.attributeValue("protocol"));
			query = DocumentHelper.createXPath("//ModeSet[@mode='" + mode + "']/ProtocolSet/ElicitationSet/ElicitedProbability");
			List list = query.selectNodes(protocolset, query);

			//Seperate out the single elicitations
			List singleElicitations = new LinkedList();
			elicitations = new LinkedList();
			for(int q = 0; q < list.size(); q++)
			{
				List probs = ((Element) list.get(q)).element("SignalSet").elements("Signal");
				if(probs.size() > 1)
					elicitations.add((Element) temp.get(q));
				else
					singleElicitations.add((Element) temp.get(q));

			}

			//Store single elicitations -CM
			for(int h = 0; h < singleElicitations.size(); h++)
			{
				Element epelement = (Element) singleElicitations.get(h);
				List l = epelement.element("SignalSet").elements("Signal");
				float probability = Float.parseFloat(epelement.attributeValue("causalprobability"));
				for(int x = 0; x < sigarray.length - 3; x++)
				{
					if(((Element) l.get(0)).attributeValue("name") == sigarray[x])
					{
						datavector[x][1] = new Double(probability);
					}
				}
			}
			//Store Group Elicitations
			if(elicitations.size() > maxGrpColumns)
			{
				int diff = elicitations.size() - maxGrpColumns;
				int lindex = elicitations.size() - 1;
				elicitations = elicitations.subList((lindex - diff), lindex);
			}
			Iterator eps = elicitations.iterator();
			if(elicitations.size() < 1)
			{
				// if there are no elicited probabilities, we have to insert one
				// group
				for(int z = 0; z < sigarray.length; z++)
				{
					datavector[z][2] = new Boolean(false);
				}

				datavector[sigarray.length - 1][2] = new Integer(75);
				datavector[sigarray.length - 2][2] = new Float(75L);

			}
			for(int y = 2; eps.hasNext(); y++)
			{
				Element epelement = (Element) eps.next();

				float probability = Float.parseFloat(epelement.attributeValue("causalprobability"));
				// check which signals are checked and which are not
				for(int x = 0; x < sigarray.length - 3; x++)
				{
					// check if signal exists inside the ilicited prob set
					XPath qr = DocumentHelper.createXPath("//ModeSet[@mode='" + mode
							+ "']/ProtocolSet/ElicitationSet/ElicitedProbability[@causalprobability='" + probability
							+ "']/SignalSet/Signal[@name='" + sigarray[x] + "']");
					List res = qr.selectNodes(epelement, qr);
					// if something came back that means we have to check the
					// box.
					boolean checked = res.size() > 0 ? true : false;
					// set the field in the data vector
					datavector[x][y] = new Boolean(checked);
				}
				// now set the last three fields
				// explicitly cast the probability to an int for the slider
				// value, it goes something
				// like this 0.33f * 100.0f = 33.0f -> cast to int and get 33
				// for the slider.
				int sv = (int) (probability * 100f);
				datavector[sigarray.length - 1][y] = new Integer(sv);
				datavector[sigarray.length - 2][y] = new Float(probability);
				// and finally set the protocol
				if(protocol == 3)
				{
					datavector[sigarray.length - 3][y] = "AND";
				}
				else
				{
					datavector[sigarray.length - 3][y] = "RNOR";
				}
			}

			// if the elictation > 1 add last empty group
			/*
			 * if(elicitations.size() > 0) { System.out.println("Adding last
			 * col..."); // if there are no elicited probabilities, we have to
			 * insert one group for(int z=0;z <sigarray.length;z++) {
			 * datavector[z][elicitations.size()] = new Boolean(false); }
			 * 
			 * datavector[sigarray.length - 1][elicitations.size()] = new
			 * Integer(75); datavector[sigarray.length - 2][elicitations.size()] =
			 * new Float(75L); }
			 */
		}

		return datavector;
	}

	
	/**
	 * Creates a Remote argument but first performs checks to see if there will be a mode conflict in the library
	 * @param procGuid the id of the process for which to add the signals 
	 * @param sigs the signal ids to add
	 * @param lib reference to the library so the checks can be performed
	 * @return a RemoteSignalArgument which when sent will add the signals to the process as causes
	 * @throws SignalModeConflictException if a conflict was during the checks
	 */
	public static RemSignalArg createCauseArg(Guid procGuid, List<Guid> sigs, Library lib) throws SignalModeConflictException
	{
		try
		{
			Process proc = lib.getProcess(procGuid);
			Guid sigGuid = (Guid) sigs.get(0);

			// if proc is null then your probably pasting (guid changed) and so there would be no need to perform this check
			if(proc != null)
			{
				if(proc.getInhibitingSignals().contains(sigGuid))
					throw new SignalModeConflictException("The signal is already used by " + proc.getProcessName()
							+ " as a inhibitor", "", proc.getProcessName(), "inhibitor", "cause");
				if(proc.getEffectSignals().contains(sigGuid))
					throw new SignalModeConflictException("The signal is already used by " + proc.getProcessName()
							+ " as a effect", "", proc.getProcessName(), "effect", "cause");
			}

			return new RemSignalArg(RemSignalArg.ADD_CAUSE, sigs, procGuid);

		}catch(NullPointerException exc)
		{
			logger.error("createCauseArg - NullPointerExc testing for signal mode conflict:  " + exc.getMessage());
			return null;
		}
	}

	/**
	 * Creates a Remote argument but first performs checks to see if there will be a mode conflict in the library
	 * @param procGuid the id of the process for which to add the signals 
	 * @param sigs the signal ids to add
	 * @param lib reference to the library so the checks can be performed
	 * @return a RemoteSignalArgument which when sent will add the signals to the process as inhibitors
	 * @throws SignalModeConflictException if a conflict was during the checks
	 */
	public static RemSignalArg createInhibArg(Guid procGuid, List<Guid> sigs, Library lib) throws SignalModeConflictException
	{
		try
		{
			Process proc = lib.getProcess(procGuid);
			Guid sigGuid = (Guid) sigs.get(0);

			// if proc is null then your probably pasting (guid changed) and so there would be no need to perform this check
			if(proc != null)
			{
				if(proc.getCausalSignals().contains(sigGuid))
					throw new SignalModeConflictException("The signal is already used by " + proc.getProcessName()
							+ " as a cause", "", proc.getProcessName(), "cause", "effect");
				if(proc.getEffectSignals().contains(sigGuid))
					throw new SignalModeConflictException("The signal is already used by " + proc.getProcessName()
							+ " as a effect", "", proc.getProcessName(), "effect", "effect");
			}

			return new RemSignalArg(RemSignalArg.ADD_INHIBITOR, sigs, procGuid);

		}catch(NullPointerException exc)
		{
			logger.error("createInhibArg - NullPointerExc testing for signal mode conflict:  " + exc.getMessage());
			return null;
		}
	}

	/**
	 * Creates a Remote argument but first performs checks to see if there will be a mode conflict in the library
	 * @param procGuid the id of the process for which to add the signals 
	 * @param sigs the signal ids to add
	 * @param lib reference to the library so the checks can be performed
	 * @return a RemoteSignalArgument which when sent will add the signals to the process as effects
	 * @throws SignalModeConflictException if a conflict was during the checks
	 */
	public static RemSignalArg createEffectArg(Guid procGuid, List<Guid> sigs, Library lib) throws SignalModeConflictException
	{
		try
		{
			Process proc = lib.getProcess(procGuid);
			Guid sigGuid = (Guid) sigs.get(0);

			// if proc is null then your probably pasting (guid changed) and so there would be no need to perform this check
			if(proc != null)
			{
//				if(proc.getCausalSignals().contains(sigGuid))
//					throw new SignalModeConflictException("The signal is already used by " + proc.getProcessName()
//							+ " as a cause", "", proc.getProcessName(), "cause", "effect");
//				if(proc.getInhibitingSignals().contains(sigGuid))
//					throw new SignalModeConflictException("The signal is already used by " + proc.getProcessName()
//							+ " as a inhibitor", "", proc.getProcessName(), "inhibitor", "effect");
			}

			return new RemSignalArg(RemSignalArg.ADD_EFFECT, sigs, procGuid);

		}catch(NullPointerException exc)
		{
			logger.error("createEffectArg - NullPointerExc testing for signal mode conflict:  " + exc.getMessage());
			return null;
		}
	}

	
	public static void main(String[] args)
	{
		SAXReader reader = new SAXReader();
		try
		{
			Document doc = reader.read(new java.io.File("resources/MoreComplex.xml"));
			Collection signals = new LinkedList();
			// get some shit yo.
			Signal sam = new Signal(new Guid("98f8e0fd-7e7a-4624-b668-bc9038cdf273"), "Sam");
			Signal dan = new Signal(new Guid("98f8e0fd-7e7a-4624-b668-bc9038cdf344"), "Dan");
			signals.add(sam);
			signals.add(dan);
			// add a new elicited probability
			doc = ProcessUtil.deleteElicitedValue(1, 4, signals, 0.12f, doc);

			System.out.println(doc.asXML());
		}catch(Exception e)
		{
			e.printStackTrace(System.err);
		}

	}
}
