/*
 * A Control class for use with the JCAT API
 */
package mil.af.rl.jcat.integration.api;

import java.awt.Font;
import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import mil.af.rl.jcat.bayesnet.Evidence;
import mil.af.rl.jcat.exceptions.BayesNetException;
import mil.af.rl.jcat.exceptions.DuplicateNameException;
import mil.af.rl.jcat.exceptions.UnknownGUIDException;
import mil.af.rl.jcat.integration.JCATControlInterface;
import mil.af.rl.jcat.plan.AbstractPlan;
import mil.af.rl.jcat.plan.COA;
import mil.af.rl.jcat.plan.Documentation;
import mil.af.rl.jcat.plan.Event;
import mil.af.rl.jcat.plan.Mechanism;
import mil.af.rl.jcat.plan.PlanItem;
import mil.af.rl.jcat.plan.ResourceAllocation;
import mil.af.rl.jcat.processlibrary.Signal;
import mil.af.rl.jcat.processlibrary.SignalType;
import mil.af.rl.jcat.util.Guid;
import mil.af.rl.jcat.util.MaskedFloat;
import mil.af.rl.jcat.util.MultiMap;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.c3i.jwb.JWBUID;


public class Control implements JCATControlInterface
{
	
	public static final String fileVer = "1.1.0API";
	private static Control control;
	private HashMap<Guid, AbstractPlan> plans = new HashMap<Guid, AbstractPlan>();
	private int i = 0;

	
	public Control()
	{
		Logger.getRootLogger().setLevel(Level.OFF); // just do this for now, maybe use the logging later
	}

	/**
	 * Get an instance of Control object
	 * @return Control
	 */
	public static Control getInstance()
	{
		if(control == null)
		{
			control = new Control();
		}
		return control;
	}

	/**
	 * Get all plans contained in this controller.
	 * @return HashMap<Guid,AbstractPlan> plans
	 */
	public HashMap<Guid, AbstractPlan> getPlans()
	{
		return plans;
	}

	/**
	 * Get the plan with a given id
	 * @param planID ID of the plan
	 * @return AbstractPlan the plan or null if the plan was not found in this controller
	 */
	public AbstractPlan getPlan(Guid planID)
	{
		return plans.get(planID);
	}

	/**
	 * Create a new AbstractPlan using the given plan ID.  The plan is also added to this controllers plan registry.
	 * @param planid ID to use for the new plan, if null one will be generated
	 * @return ID of newly created plan
	 * @throws DuplicateNameException if the controller already contains a plan with the specified ID
	 */
	public Guid createPlan(Guid planid) throws DuplicateNameException
	{
		if(planid == null)
		{
			planid = new Guid();
			i++;
		}
		if(plans.containsKey(planid))
			throw (new DuplicateNameException());
		
		AbstractPlan plan = new AbstractPlan(planid);
		plan.setPlanName("NewPlan" + i);

		plans.put(planid, plan);

		return planid;
	}
	
	/**
	 * Adds an existing AbstractPlan to this controller
	 * @param plan plan to be added
	 * @throws DuplicateNameException if the controller already contains a plan with the specified ID
	 */
	public void addPlan(AbstractPlan plan) throws DuplicateNameException
	{
		if(plans.containsKey(plan.getId()))
			throw (new DuplicateNameException());
		
		plans.put(plan.getId(), plan);
	}

	/**
	 * Remove a plan from this controller.  Will also ensure plans internal sampler is terminated if one exists
	 * @param planID ID of plan to remove
	 */
	public void removePlan(Guid planID)
	{
		// if there is a bayesnet running (sampling thread), kill that first
		AbstractPlan plan = plans.get(planID);
		if(plan != null && plan.getBayesNet() != null && plan.getBayesNet().isSampling())
			plan.getBayesNet().killSampler();
		
		plans.remove(planID);
	}
	
	/**
	 * Opens a plan from file and adds it to this controllers plan registry.
	 * @param planFile file to be opened
	 * @return Guid plan ID of the plan opened
	 * @throws MalformedURLException
	 * @throws DocumentException if an error occured parsing the specified file
	 * @throws FileNotFoundException
	 * @throws DuplicateNameException if the controller already contains a plan with the specified ID
	 */
	public Guid openPlan(File planFile) throws MalformedURLException, DocumentException, FileNotFoundException, DuplicateNameException
	{
		Document doc = new SAXReader().read(planFile);
		Guid planID = openPlan(doc);
		AbstractPlan thePlan = getPlan(planID);

		thePlan.setFilePath(planFile.getAbsolutePath());
		thePlan.setPlanName(planFile.getName());
		return planID;
	}

	public Guid openPlan(Document doc) throws DuplicateNameException, DocumentException
	{
		Guid planid = null;

		Element el = (Element) doc.selectSingleNode("//Plan");
		String guid = el.attributeValue("guid");
		if(guid == null)
			planid = new Guid();
		else
			planid = new Guid(guid);

		createPlan(planid);

		String defSet = el.attributeValue("default_probability_set");
		if(defSet != null)
		{
			if(defSet.equals("and_or"))
				getPlan(planid).setDefaultProbSet(AbstractPlan.AND_OR_DEFAULTS_SET);
			else if(defSet.equals("user_defined"))
				getPlan(planid).setDefaultProbSet(AbstractPlan.USER_DEFINED_DEFAULTS_SET);
		}

		AbstractPlan plan = this.getPlan(planid);
		//rebuild process library from the document
		plan.getLibrary().deserializeLibrary((Element) doc.selectSingleNode("//ProcessLibrary"));
		// first read in plan documentation and make sure to include the full xPath
		Element pdoc = (Element) doc.selectSingleNode("/Plan/documentation[1]");
		if(pdoc != null)
		{
			plan.setDocumentation(Documentation.getDocumentation(pdoc));
		}
		Iterator pevents = doc.selectNodes("//PlanItem[@type='event']").iterator();
//			ArrayList shapes = new ArrayList();

		//extract all events (planitems)
		for(; pevents.hasNext();)
		{
			Event ev = parseEvent((Element) pevents.next(), plan);
			plan.addUnmappedItem(ev);
		}
		// now do the same for all the lines (mechanisms)
		Iterator mechs = doc.selectNodes("//PlanItem[@type='mechanism']").iterator();

		//extract all mechanisms (planitems)
		for(; mechs.hasNext();)
		{
			Mechanism m = parseMechanism((Element) mechs.next(), plan);
			if(m != null) //mech is null if it failed to parse, we want to continue loading plan
			{
				plan.addUnmappedItem(m);
				// Mechanism was modified... used to add itself to its connecting events inside Mechanism
				// does not do this anymore and therefor needs to be connected(in terms of plan) here.
				// Creating a new mech is already taken care of how it should be in addMechanism()
				// This will handle connecting when loading from file
				// Events have been parsed at this point and library has been reconstructed
				try
				{
					Event fromEv = (Event) plan.getItem(m.getFromEvent());
					fromEv.addEffect(m);

					Iterator<Guid> i = m.getInfluencedEvents().iterator();
					while(i.hasNext())
					{
						Event toEv = (Event) plan.getItem(i.next());
						if(plan.getLibrary().getProcess(toEv.getProcessGuid()).getCausalSignals().contains(m.getSignalGuid()))
							toEv.addCause(m);
						else
							toEv.addInhibitor(m);
					}

				}catch(ClassCastException exc)
				{
					exc.printStackTrace(System.err);
				}
			}
		}

		//extract all lines (shapes)
//			shapes.clear();
		//extract DefaultFont
		Element defFont = (Element) doc.selectSingleNode("//DefaultFont");
		if(defFont != null)
		{
			java.util.StringTokenizer fontTok = new java.util.StringTokenizer(defFont.getText(), ":");
			if(fontTok.countTokens() == 3)
			{
				try
				{
					plan.setDefaultFont(new Font(fontTok.nextElement().toString(), Integer.parseInt(fontTok.nextElement().toString()), Integer.parseInt(fontTok
							.nextElement().toString())));
				}catch(NumberFormatException exc)
				{
					exc.printStackTrace(System.err);
				}
			}
		}

		//extract the Sampling options
		Element soElement = (Element) doc.selectSingleNode("//SamplingOptions");
		if(soElement != null)
		{
			int readLength = parseSampleOptions(soElement);
			if(readLength > 0)
				plan.setLoadedPlanLength(readLength);
		}
		//extract any plan COAs
		Element planCOAElement = (Element) doc.selectSingleNode("//PlanCOAs");
		if(planCOAElement != null)
		{
			plan.setCOAList(parsePlanCOAs(planCOAElement));
		}

		return planid;
	}
	
	// HELPER METHODS FOR PARSING WITH openPlan //
	private Vector<COA> parsePlanCOAs(Element e)
	{
		Vector<COA> coaList = new Vector<COA>();

		Iterator coas = e.selectNodes("./COA").iterator();
		while(coas.hasNext())
			coaList.add(new COA((Element) coas.next()));

		return coaList;
	}

	private int parseSampleOptions(Element e)
	{
		List options = e.selectNodes("./Option");

		int plength = -1;
		if(options.size() > 0)
		{
			Element option = (Element) options.get(0);
			plength = Integer.parseInt(option.attributeValue("PlanLength"));
		}
		return plength;
	}

	private Event parseEvent(Element e, AbstractPlan plan)
	{

		String processguid = e.valueOf("./PGuid");
		Event item = new Event(new Guid(e.attributeValue("guid")), e.attributeValue("name"), e.attributeValue("label"), new Guid(processguid));

		//Resources
		Element resEl = (Element) e.selectSingleNode("./Resources");
		if(resEl != null)
		{
			HashMap<Guid, ResourceAllocation> resMap = new HashMap<Guid, ResourceAllocation>();
			Iterator allocations = resEl.selectNodes("allocation").iterator();
			if(resEl.selectNodes("allocation").size() < 1)
				allocations = resEl.selectNodes("Resource").iterator();
			while(allocations.hasNext())
			{
				try
				{
					Element thisRes = (Element) allocations.next();
					//allow backwards compatability with resources
					String oldResValStr = thisRes.attributeValue("value");
					if(oldResValStr != null)
					{
						try
						{
							StringTokenizer parser = new StringTokenizer(oldResValStr.substring(1, oldResValStr.length() - 1), ",");

							String name = parser.nextToken().trim();
							Integer alloc = new Integer(parser.nextToken().trim());
							Boolean conting = new Boolean(parser.nextToken().trim());
							Guid newGuid = new Guid();
							resMap.put(newGuid, new ResourceAllocation(newGuid, alloc, name, conting));
						}catch(NumberFormatException exc)
						{
							exc.printStackTrace(System.err);
							//logger.error("parseEvent - Error parsing old format resources:  "+exc.getMessage());
						}
					}
					//otherwise load the resource the new way
					else
					{
						Guid theGuid = new Guid(thisRes.attributeValue("guid"));
						resMap.put(theGuid, new ResourceAllocation(theGuid, new Integer(thisRes.attributeValue("allocated")), thisRes.attributeValue("name"), new Boolean(
								thisRes.attributeValue("contingent"))));
					}
				}catch(NumberFormatException exc)
				{
					exc.printStackTrace(System.err);
					//logger.error("parseEvent - Invalid value parsing resource element:  "+exc.getMessage());
				}
			}
			item.setResources(resMap);
		}

		//Threat Resources
		Element tResEl = (Element) e.selectSingleNode("./ThreatResources");
		if(tResEl != null)
		{
			MultiMap<Integer, ResourceAllocation> tResMap = new MultiMap<Integer, ResourceAllocation>();
			Iterator timeEls = tResEl.selectNodes("resource").iterator();
			if(tResEl.selectNodes("resource").size() < 1) //backwards compatability w/t-resources
				timeEls = tResEl.selectNodes("ThreatResource").iterator();
			while(timeEls.hasNext())
			{
				Element timeEl = (Element) timeEls.next();
				String time = timeEl.attributeValue("time");
				if(time == null)
					time = timeEl.attributeValue("Time");
				//allow backwards compatability with loading resources
				if(timeEl.selectNodes("allocation").size() < 1)
				{
					Integer alloc = new Integer(timeEl.attributeValue("Allocation"));
					tResMap.put(new Integer(time), new ResourceAllocation(new Guid(), alloc));
				}
				else
				{
					Iterator allocations = timeEl.selectNodes("allocation").iterator();
					while(allocations.hasNext())
					{
						try
						{
							Element thisRes = (Element) allocations.next();
							Guid theGuid = new Guid(thisRes.attributeValue("guid"));
							tResMap.put(new Integer(time), new ResourceAllocation(theGuid, new Integer(thisRes.attributeValue("allocated"))));
						}catch(NumberFormatException exc)
						{
							exc.printStackTrace(System.err);
							//logger.error("parseEvent - Invalid value parsing t-resource element:  "+exc.getMessage());
						}
					}
				}
			}
			item.setThreatResources(tResMap);
		}

		// NOTE that all effects causes and linked will be set when the mechanisms are added
//		item.setDefCausalProb(Float.parseFloat(e.valueOf("./DefCause")), plan.getLibrary());
//		item.setDefEffectProb(Float.parseFloat(e.valueOf("./DefEffect")), plan.getLibrary());
//		item.setDefInhibitingProb(Float.parseFloat(e.valueOf("./DefInhibit")), plan.getLibrary());
		item.setLeak(Float.parseFloat(e.valueOf("./DefLeak")));

		//resource expenditure type
		int resUType = (e.valueOf("./ResourceExpenditureType").equals("OR")) ? mil.af.rl.jcat.bayesnet.NetNode.RESOURCE_USE_OR : mil.af.rl.jcat.bayesnet.NetNode.RESOURCE_USE_AND;
		item.setResourceUseType(resUType);

		// set the notes
		//item.setNotes(e.valueOf("./Notes"));
		item.setDelay(Integer.parseInt(e.valueOf("./Delay")));
		item.setPersistence(Integer.parseInt(e.valueOf("./Persistence")));
		item.setContinuation(Float.parseFloat(e.valueOf("./Continuation")));
		item.setSchemeAttrib(e.valueOf("./SchemeAttribute"));

		Element documentation = (Element) e.selectSingleNode("./documentation");
		if(documentation != null)
		{
			item.setDocumentation(Documentation.getDocumentation(documentation));
		}
		// scheduling
		List probs = e.selectNodes("./Schedule/Probability");
		Iterator pi = probs.iterator();
		for(; pi.hasNext();)
		{
			Element p = (Element) pi.next();
			item.scheduleEvent(Integer.parseInt(p.attributeValue("time")), Float.parseFloat(p.getText()));
		}

		//parse evidence
		probs = e.selectNodes("./Evidence/Absolute/Probability");
		pi = probs.iterator();
		for(; pi.hasNext();)
		{
			Element p = (Element) pi.next();
			item.addObservation(Integer.parseInt(p.attributeValue("time")), new Evidence(Double.parseDouble(p.getText())));
		}

		probs = e.selectNodes("./Evidence/Sensor/Report");
		pi = probs.iterator();
		for(; pi.hasNext();)
		{
			Element p = (Element) pi.next();
			item.addObservation(Integer.parseInt(p.getText()), new Evidence(Boolean.parseBoolean(p.attributeValue("wasTrue")), Double.parseDouble(p.attributeValue("FAR")), Double
					.parseDouble(p.attributeValue("MDR"))));
		}

		/*
		 * Iterator priorpred = e.selectNodes("./PriorPredicted/*").iterator();
		 * ArrayList prdl = new ArrayList(); for(;prior.hasNext();) { Element p =
		 * (Element)priorpred.next(); prdl.add(new
		 * Float(Float.parseFloat(p.valueOf("./Probability")))); }
		 * item.setPredictedProbs(prdl);
		 */
		return item;
	}

	private Mechanism parseMechanism(Element e, AbstractPlan plan)
	{
		Mechanism m = null;
		String sguid = e.element("Signal").attributeValue("guid");//.valueOf("./Signal[@guid]");
		String sname = e.element("Signal").attributeValue("name");//.valueOf("./Signal[@name]");
		String name = e.attributeValue("name");
		//String label = e.attributeValue("label");
		String mguid = e.attributeValue("guid");
		Collection<Event> toEvents = convertStringToEvents(e.valueOf("./ToEvent"), plan);
		String fromevent = e.valueOf("./FromEvent");
		boolean loop = false;
		if(e.valueOf("ClosesLoop").compareTo("true") == 0)
			loop = true;

		// even if it exists, we'll replace the shit
		Signal signal = new Signal(new Guid(sguid), sname);
		Event from = (Event) plan.getItem(new Guid(fromevent));

		try
		{
			m = new Mechanism(new Guid(mguid), name, toEvents, from, signal.getSignalID());

			m.setLoopCloser(loop);
			m.setDelay(Integer.parseInt(e.valueOf("./Delay")));
			m.setPersistence(Integer.parseInt(e.valueOf("./Persistence")));
			m.setContinuation(Float.parseFloat(e.valueOf("./Continuation")));

			//parse evidence
			List probs = e.selectNodes("./Evidence/Absolute/Probability");
			Iterator pi = probs.iterator();
			for(; pi.hasNext();)
			{
				Element p = (Element) pi.next();
				m.addObservation(Integer.parseInt(p.attributeValue("time")), new Evidence(Double.parseDouble(p.getText())));
			}

			//scheduling
			probs = e.selectNodes("./Schedule/Probability");
			pi = probs.iterator();
			for(; pi.hasNext();)
			{
				Element p = (Element) pi.next();
				m.scheduleEvent(Integer.parseInt(p.attributeValue("time")), Float.parseFloat(p.getText()));
			}

			//Resources (its a PlanItem thing but is done in both parseMechanism and parseEvent)
			Element resEl = (Element) e.selectSingleNode("./Resources");
			if(resEl != null)
			{
				HashMap<Guid, ResourceAllocation> resMap = new HashMap<Guid, ResourceAllocation>();
				Iterator allocations = resEl.selectNodes("allocation").iterator();
				if(resEl.selectNodes("allocation").size() < 1)
					allocations = resEl.selectNodes("Resource").iterator();
				while(allocations.hasNext())
				{
					try
					{
						Element thisRes = (Element) allocations.next();
						//allow backwards compatability with resources
						String oldResValStr = thisRes.attributeValue("value");
						if(oldResValStr != null)
						{
							try
							{
								StringTokenizer parser = new StringTokenizer(oldResValStr.substring(1, oldResValStr.length() - 1), ",");

								String pName = parser.nextToken().trim();
								Integer alloc = new Integer(parser.nextToken().trim());
								Boolean conting = new Boolean(parser.nextToken().trim());
								Guid newGuid = new Guid();
								resMap.put(newGuid, new ResourceAllocation(newGuid, alloc, pName, conting));
							}catch(NumberFormatException exc)
							{
								exc.printStackTrace(System.err);
							}
						}
						//otherwise load the resource the new way
						else
						{
							Guid theGuid = new Guid(thisRes.attributeValue("guid"));
							resMap.put(theGuid, new ResourceAllocation(theGuid, new Integer(thisRes.attributeValue("allocated")), thisRes.attributeValue("name"),
									new Boolean(thisRes.attributeValue("contingent"))));
						}
					}catch(NumberFormatException exc)
					{
						exc.printStackTrace(System.err);
					}
				}
				m.setResources(resMap);
			}

			//Threat Resources
			Element tResEl = (Element) e.selectSingleNode("./ThreatResources");
			if(tResEl != null)
			{
				MultiMap<Integer, ResourceAllocation> tResMap = new MultiMap<Integer, ResourceAllocation>();
				Iterator timeEls = tResEl.selectNodes("resource").iterator();
				if(tResEl.selectNodes("resource").size() < 1) //backwards compatability w/t-resources
					timeEls = tResEl.selectNodes("ThreatResource").iterator();
				while(timeEls.hasNext())
				{
					Element timeEl = (Element) timeEls.next();
					String time = timeEl.attributeValue("time");
					if(time == null)
						time = timeEl.attributeValue("Time");
					//allow backwards compatability with loading resources
					if(timeEl.selectNodes("allocation").size() < 1)
					{
						Integer alloc = new Integer(timeEl.attributeValue("Allocation"));
						tResMap.put(new Integer(time), new ResourceAllocation(new Guid(), alloc));
					}
					else
					{
						Iterator allocations = timeEl.selectNodes("allocation").iterator();
						while(allocations.hasNext())
						{
							try
							{
								Element thisRes = (Element) allocations.next();
								Guid theGuid = new Guid(thisRes.attributeValue("guid"));
								tResMap.put(new Integer(time), new ResourceAllocation(theGuid, new Integer(thisRes.attributeValue("allocated"))));
							}catch(NumberFormatException exc)
							{
								exc.printStackTrace(System.err);
							}
						}
					}
				}
				m.setThreatResources(tResMap);
			}

		}catch(Exception ex)
		{
			ex.printStackTrace(System.err);
		}

		return m;
	}

	
	/**
	 *  Convenience method for writing the specified plan to file using the same format as a typical JCAT file (XML based)
	 *  @param planid id of the plan
	 *  @param planfile File for the plan to be written to
	 *  @param newPlanID Generate a new plan ID for this new file instead of using the current ID
	 *  @param writeGraphical Write false graphical information along with the Plan (all graph elements will be located on top of each other).
	 *  @throws FileNotFoundException
	 */
	public boolean savePlan(Guid planid, File planfile, boolean newPlanID, boolean writeGraphical) throws FileNotFoundException
	{
		Guid oldId = planid;

		Element root = DocumentHelper.createElement("Plan");
		root.addAttribute("name", planfile.getName());
		root.addAttribute("guid",  (newPlanID ? new Guid() : planid).toString());
		root.addAttribute("version", fileVer);

		String defSet = "standard";
		if(getPlan(oldId).getDefaultProbSet() == AbstractPlan.AND_OR_DEFAULTS_SET)
			defSet = "and_or";
		else if(getPlan(oldId).getDefaultProbSet() == AbstractPlan.USER_DEFINED_DEFAULTS_SET)
			defSet = "user_defined";
		root.addAttribute("default_probability_set", defSet);

		Document plan = DocumentHelper.createDocument(root);

		root.add(createLogicElement(getPlan(oldId).getItems().values(), getPlan(oldId).getLibrary()));
		root.add(createLibraryElement(getPlan(oldId).getItems().values(), getPlan(oldId)));
		if(writeGraphical)
			root.add(createGraphElement(getPlan(oldId)));

//		if(saveAs == true)
//		{
//			AbstractPlan pl = getPlan(oldId);
//			pl.setId(planid);
//			plans.remove(oldId);
//			plans.put(planid, pl);
//			modelIDs.put(planid, modelIDs.remove(oldId));
//		}

		// setTrimText was changed in order to allow the extra whitespace that is desired in things
		// such as plan documentation and coa summary documents to be maintained when plan is saved/loaded
		OutputFormat ouf = OutputFormat.createPrettyPrint();
		ouf.setTrimText(false); //<-- This line did it

		try{
			File outputFile;
//			if(!autoSave)
				outputFile = new File(planfile + (planfile.getName().endsWith(".jcat") ? "" : ".jcat"));
//			else
//				outputFile = planfile;

//			if(!saveAs || !outputFile.exists() || JOptionPane.showConfirmDialog(MainFrm.getInstance(), "File exists, would you like to overwrite this file?", "Overwrite", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
//			{
				java.io.FileOutputStream fos = new java.io.FileOutputStream(outputFile);
				XMLWriter writer;
				try
				{
					writer = new XMLWriter(fos, ouf);
					writer.write(plan);
					writer.flush();
//					if(!autoSave)
						getPlan(planid).setFilePath(outputFile.getAbsolutePath());
				}catch(IOException exc)
				{
					System.err.println("Control -  Error saving plan:  IOError ("+exc.getMessage()+")");
					return false;
				}

				return true;
//			}
//			else
//				return false;
		}catch(FileNotFoundException exc)
		{
//			new UpdateSubProcessor().showMessage("Could not save to the specified file. Ensure that you have proper permissions.");
//			logger.error("savePlan - FileNotFoundExc writing plan file:  " + exc.getMessage());
			System.err.println("Control -  Error saving plan:  File not found ("+exc.getMessage()+")");
			return false;
		}
	}
	
	// HELPER METHODS FOR SAVING with savePlan
	private Element createLogicElement(Collection<PlanItem> planItems, mil.af.rl.jcat.processlibrary.Library lib)
	{
		Element logic = DocumentHelper.createElement("Logic");
		Iterator si = planItems.iterator();
		for(; si.hasNext();)
		{
			PlanItem item = (PlanItem) si.next();
			Element pi = DocumentHelper.createElement("PlanItem");
			pi.addAttribute("name", item.getName());
			pi.addAttribute("label", item.getLabel());
			pi.addAttribute("guid", item.getGuid().getValue());

			// create a resources element if item has resources
			HashMap<Guid, ResourceAllocation> resources = item.getResources();
			if(resources != null && resources.size() > 0)
			{
				Element resEl = DocumentHelper.createElement("Resources");

				for(Guid id : resources.keySet())
				{
					ResourceAllocation allocation = resources.get(id);
					resEl.addElement("allocation").addAttribute("guid", id.getValue()).addAttribute("name", allocation.getName()).addAttribute("allocated",
							allocation.getAllocated() + "").addAttribute("contingent", allocation.isContingent() + "");
				}
				pi.add(resEl);
			}

			// Handle threat resources
			MultiMap<Integer, ResourceAllocation> threat = item.getThreatResources();
			if(threat != null && threat.size() > 0)
			{
				Element tResEl = DocumentHelper.createElement("ThreatResources");
				for(Object key : threat.keySet())
				{
					Element timeEl = tResEl.addElement("resource").addAttribute("time", ((Integer) key).intValue() + "");

					Iterator valuesForKey = threat.get((Integer) key).iterator();
					while(valuesForKey.hasNext())
					{
						ResourceAllocation ra = (ResourceAllocation) valuesForKey.next();
						timeEl.addElement("allocation").addAttribute("guid", ra.getID().getValue()).addAttribute("allocated", ra.getAllocated() + "");
					}
				}
				pi.add(tResEl);
			}

			if(item.getItemType() == PlanItem.EVENT)
			{
				Event event = (Event) item;
				pi.addAttribute("type", "event");
				// create event specific elements, including process element

				// resource expenditure type
				String expType = (event.getResourceUseType() == mil.af.rl.jcat.bayesnet.NetNode.RESOURCE_USE_AND) ? "AND" : "OR";
				pi.addElement("ResourceExpenditureType").setText(expType);

				// documentations
				pi.add(event.getDocumentation().toXML().getRootElement());
				// scheduling
				Element schedule = DocumentHelper.createElement("Schedule");
				java.util.TreeMap times = event.getSchedule();
				Iterator ti = times.keySet().iterator();
				for(; ti.hasNext();)
				{
					Integer key = (Integer) ti.next();
					Float fp = new Float(((MaskedFloat) times.get(key)).floatValue());
					Element te = schedule.addElement("Probability").addAttribute("time", key.toString());
					te.setText(fp.toString());
				}
				pi.add(schedule);

				// Evidence
				Element evidenceEl = DocumentHelper.createElement("Evidence");
				Element absoluteEl = DocumentHelper.createElement("Absolute");
				Element sensorEl = DocumentHelper.createElement("Sensor");
				java.util.HashMap evidence = event.getEvidence();
				Iterator evidenceIt = evidence.keySet().iterator();
				for(; evidenceIt.hasNext();)
				{
					Integer key = (Integer) evidenceIt.next(); // key = time
					Evidence thisEvidence = ((Evidence) evidence.get(key));

					if(thisEvidence.getType() == Evidence.ABSOLUTE)
					{
						Element te = absoluteEl.addElement("Probability").addAttribute("time", key.toString());
						te.setText(thisEvidence.getProbability() + "");
					}
					else if(thisEvidence.getType() == Evidence.SENSOR)
					{
						Element te = sensorEl.addElement("Report").addAttribute("FAR", thisEvidence.getFAR() + "").addAttribute("MDR", thisEvidence.getMDR() + "")
								.addAttribute("wasTrue", thisEvidence.isReport() + "");
						te.setText(key.toString());
					}
				}
				evidenceEl.add(absoluteEl);
				evidenceEl.add(sensorEl);
				pi.add(evidenceEl);

				pi.addElement("DefLeak").setText(event.getLeak() + "");
				Element causes = DocumentHelper.createElement("Causes");
				Iterator ci = event.getCauses().iterator();
				for(; ci.hasNext();)
				{
					causes.addElement("Mech").addAttribute("guid", ((Guid) ci.next()).getValue());
				}
				pi.add(causes);

				Element effects = DocumentHelper.createElement("Effects");
				Iterator ei = event.getEffects().iterator();
				for(; ei.hasNext();)
				{
					effects.addElement("Mech").addAttribute("guid", ((Guid) ei.next()).getValue());
				}
				pi.add(effects);

				Element inhibits = DocumentHelper.createElement("Inhibits");
				Iterator ii = event.getInhibitors().iterator();
				for(; ii.hasNext();)
				{
					inhibits.addElement("Mech").addAttribute("guid", ((Guid) ii.next()).getValue());
				}
				pi.add(inhibits);

				// pi.addElement("Notes").setText(event.getNotes());
				pi.addElement("Delay").setText(event.getDelay() + "");
				pi.addElement("Persistence").setText(event.getPersistence() + "");
				pi.addElement("Continuation").setText(event.getContinuation() + "f");
				pi.addElement("PGuid").setText(event.getProcessGuid().getValue());
				pi.addElement("SchemeAttribute").setText(event.getSchemeAttrib());

			}
			else
			{
				pi.addAttribute("type", "mechanism");
				Mechanism m = (Mechanism) item;

				Element g = pi.addElement("Signal");
				g.addAttribute("guid", m.getSignalGuid().toString());
				g.addAttribute("name", m.getName());

				Element to = pi.addElement("ToEvent");
				String value = "";
				for(Iterator<Guid> i = m.getInfluencedEvents().iterator(); i.hasNext();)
					value += (i.next().getValue()) + " ";
				to.setText(value);

				pi.addElement("FromEvent").setText(m.getFromEvent().getValue());
				pi.addElement("ClosesLoop").setText(Boolean.toString(m.isLoopCloser()));

				// Evidence
				Element evidenceEl = DocumentHelper.createElement("Evidence");
				Element absoluteEl = DocumentHelper.createElement("Absolute");
				Element sensorEl = DocumentHelper.createElement("Sensor");
				java.util.HashMap evidence = m.getEvidence();
				Iterator evidenceIt = evidence.keySet().iterator();
				for(; evidenceIt.hasNext();)
				{
					Integer key = (Integer) evidenceIt.next(); // key = time
					Evidence thisEvidence = ((Evidence) evidence.get(key));

					if(thisEvidence.getType() == Evidence.ABSOLUTE)
					{
						Element te = absoluteEl.addElement("Probability").addAttribute("time", key.toString());
						te.setText(thisEvidence.getProbability() + "");
					}
					else if(thisEvidence.getType() == Evidence.SENSOR)
					{
						Element te = sensorEl.addElement("Report").addAttribute("FAR", thisEvidence.getFAR() + "").addAttribute("MDR", thisEvidence.getMDR() + "")
								.addAttribute("wasTrue", thisEvidence.isReport() + "");
						te.setText(key.toString());
					}
				}
				evidenceEl.add(absoluteEl);
				evidenceEl.add(sensorEl);
				pi.add(evidenceEl);

				// scheduling
				Element schedule = DocumentHelper.createElement("Schedule");
				java.util.TreeMap times = m.getSchedule();
				Iterator ti = times.keySet().iterator();
				for(; ti.hasNext();)
				{
					Integer key = (Integer) ti.next();
					Float fp = new Float(((MaskedFloat) times.get(key)).floatValue());
					Element te = schedule.addElement("Probability").addAttribute("time", key.toString());
					te.setText(fp.toString());
				}
				pi.add(schedule);

				pi.addElement("Delay").setText(m.getDelay() + "");
				pi.addElement("Persistence").setText(m.getPersistence() + "");
				pi.addElement("Continuation").setText(m.getContinuation() + "f");

			}

			logic.add(pi);

		}
//		System.gc();
		return logic;
	}
	
	private Element createLibraryElement(Collection<PlanItem> planItems, AbstractPlan plan)
	{
		Element libs = DocumentHelper.createElement("ProcessLibrary");
		Element pLib = DocumentHelper.createElement("TheProcesses");
		Element sLib = DocumentHelper.createElement("TheSignals");
		libs.add(pLib);
		libs.add(sLib);
		// HashMap sigs = new HashMap();
//		Iterator<PlanItem> si = planItems.iterator();
		for(PlanItem item : planItems)
		{
//			PlanItem item = plan.getItem((Guid) ((JWBShapeSkeleton) si.next()).getAttachment());
			if(item.getItemType() == PlanItem.EVENT)
			{
				Event event = (Event) item;

				Element p = plan.getLibrary().getProcessDocument(event.getProcessGuid()).getRootElement();
				p.setParent(null);
				pLib.add(p);
			}
		}

		for(Signal s : plan.getLibrary().getAllSignals())
		{
			try{
				Element sig = sLib.addElement("Signal");
				s.getSignalID().addToDocument(null, sig);
				sig.addAttribute("name", s.getSignalName());
			}catch(Exception e)
			{
//				logger.error("createLibraryElement - error building Signal section:  " + e.getMessage());
				System.err.println("Control -  Error building library element (signal section): ");
				e.printStackTrace();
			}
		}

		return libs;
	}
	
	private Element createGraphElement(AbstractPlan plan)
	{
		Element graph = DocumentHelper.createElement("Graph");
		Point loc = new Point(100, 50);
		int sz = 80;
		
		Hashtable<Guid, JWBShapeSkeleton> eventLinkMap = new Hashtable<Guid, JWBShapeSkeleton>(); // for quick convienient lookup to link shapes
		Collection<Event> events = plan.getAllEvents();
		Vector<JWBShapeSkeleton> shapes = new Vector<JWBShapeSkeleton>();
		
		for(Event event : events) // gotta have all the event shapes first
		{
			EventShapeSkeleton evShp = new EventShapeSkeleton(loc, sz, sz, new JWBUID());
			evShp.setAttachment(event.getGuid());
			evShp.setText(event.getName()+"\n\n"+event.getLabel());
			applyMarkups(event, evShp);
			eventLinkMap.put(event.getGuid(), evShp);
			shapes.add(evShp);
		}
		
		for(Event event : events) // now create rest of shapes and link
		{
			JWBShapeSkeleton thisEventShp = eventLinkMap.get(event.getGuid());
			List<Guid> causes = event.getCauses();
			for(Guid mechID : causes)
			{
				Mechanism item = (Mechanism)plan.getItem(mechID);
				MechanismShapeSkeleton mechShp = new MechanismShapeSkeleton(eventLinkMap.get(item.getFromEvent()), thisEventShp, new JWBUID(), false);
				mechShp.setAttachment(mechID);
				mechShp.setText(item.getName());
				applyMarkups(item, mechShp);
				shapes.add(mechShp);
			}
			
			List<Guid> inhibitors = event.getInhibitors();
			for(Guid mechID : inhibitors)
			{
				Mechanism item = (Mechanism)plan.getItem(mechID);
				MechanismShapeSkeleton mechShp = new MechanismShapeSkeleton(eventLinkMap.get(item.getFromEvent()), thisEventShp, new JWBUID(), false);
				mechShp.setAttachment(mechID);
				mechShp.setText(item.getName());
				mechShp.setLineStyle(MechanismShapeSkeleton.DASHED);
				applyMarkups(item, mechShp);
				shapes.add(mechShp);
			}
		}
		
		for(JWBShapeSkeleton shape : shapes)
		{
//			JWBShapeSkeleton shape = (JWBShapeSkeleton) si.next();
			Element shp = DocumentHelper.createElement("Shape");
			shp.addAttribute("id", shape.getUID().toString());

			if(shape.getType().equals("com.c3i.jwb.shapes.JWBRoundedRectangle"))
			{
				shp.addAttribute("type", "event");
			}
			else
			{
				shp.addAttribute("type", "mechanism");
			}
			shp.addElement("itemguid").setText(((Guid) shape.getAttachment()).getValue());
			if(shape.getType().equals("com.c3i.jwb.JWBLine"))
			{
				JWBUID[] lshapes = ((MechanismShapeSkeleton) shape).getLinkedShapes();
				shp.addElement("startkey").setText(lshapes[0].toString());
				shp.addElement("endkey").setText(lshapes[1].toString());
				if(((MechanismShapeSkeleton) shape).getLineStyle() == MechanismShapeSkeleton.SOLID)
					shp.addElement("style").setText("solid");
				else
					shp.addElement("style").setText("dashed");

				if(((MechanismShapeSkeleton) shape).getLineType() == MechanismShapeSkeleton.ARCED)
					shp.addElement("type").setText("arced");
				else
					shp.addElement("type").setText("straight");

				// Saving line control points
				int[] points = ((MechanismShapeSkeleton) shape).getLinePoints();
				String str = new String();
				for(int i = 0; i < points.length; i++)
					str += points[i] + " ";
				shp.addElement("controlPoints").setText(str);
			}
			else
			{
				// adding color and alpha information first
				shp.addElement("rcolor").setText(shape.getColor().getRed() + "");
				shp.addElement("gcolor").setText(shape.getColor().getGreen() + "");
				shp.addElement("bcolor").setText(shape.getColor().getBlue() + "");
				shp.addElement("alpha").setText(shape.getColor().getAlpha() + "");
				shp.addElement("height").setText(shape.getHeight() + "");
				shp.addElement("width").setText(shape.getWidth() + "");
				shp.addElement("xpos").setText(shape.getLocation().getX() + "");
				shp.addElement("ypos").setText(shape.getLocation().getY() + "");
			}

			// stuff for both
			shp.addElement("text").setText(shape.getText());

			Font shapeFont = shape.getFont();
			shp.addElement("font").setText(shapeFont.getFamily() + ":" + shapeFont.getStyle() + ":" + shapeFont.getSize());
			shp.addElement("font-rcolor").setText(shape.getTextColor().getRed() + "");
			shp.addElement("font-gcolor").setText(shape.getTextColor().getGreen() + "");
			shp.addElement("font-bcolor").setText(shape.getTextColor().getBlue() + "");

			graph.add(shp);
		}

		// Element shp = DocumentHelper.createElement("DefaultFont");
		// shp.addAttribute("id", shape.getUID().toString());
		Font theFont = plan.getDefaultFont();
		graph.addElement("DefaultFont").setText(theFont.getFamily() + ":" + theFont.getStyle() + ":" + theFont.getSize());

		return graph;
	}
	
	private static boolean applyMarkups(PlanItem item, JWBShapeSkeleton shape)
	{
		boolean markedUp = false;

		if((item.getDelay() != 0 || item.getPersistence() != 1 || item.getContinuation() != 0.0f))
		{
			if(!shape.containsMarkup('T'))
			{
				shape.addMarkup('T');
				markedUp = true;
			}
		}
		else if(shape.containsMarkup('T'))
		{
			shape.removeMarkup('T');
			markedUp = true;
		}

		if(item.getSchedule().size() > 0)
		{
			if(!shape.containsMarkup('S'))
			{
				shape.addMarkup('S');
				markedUp = true;
			}
		}
		else if(shape.containsMarkup('S'))
		{
			shape.removeMarkup('S');
			markedUp = true;
		}

		if((item.getResources().size() > 0 || item.getThreatResources().size() > 0))
		{
			if(!shape.containsMarkup('R'))
			{
				shape.addMarkup('R');
				markedUp = true;
			}
		}
		else if(shape.containsMarkup('R'))
		{
			shape.removeMarkup('R');
			markedUp = true;
		}

		if(item.getEvidence().size() > 0)
		{
			if(!shape.containsMarkup('E'))
			{
				shape.addMarkup('E');
				markedUp = true;
			}
		}
		else if(shape.containsMarkup('E'))
		{
			shape.removeMarkup('E');
			markedUp = true;
		}

		return markedUp;
	}
	// // //
	
	
	public static Document getPlanDocument(File planFile) throws MalformedURLException, DocumentException
	{
		Document doc = new SAXReader().read(planFile);
		
		return doc;
	}

	/**
	 * Get all Event IDs in the plan
	 * 
	 * @param planID ID of the plan to use
	 * @return a Map of Event IDs to Event names or null if the specified plan ID is unknown
	 */
	public Map<Guid, String> getAllEvents(Guid planID)
	{
		if(plans.get(planID) == null)
			return null;
		HashMap<Guid, String> events = new HashMap<Guid, String>();
		
		for(Event e : plans.get(planID).getAllEvents())
			events.put(e.getGuid(), e.getName());
		
		return events;
	}

	/**
	 * Get all Mechanism IDs in the plan
	 * 
	 * @param planID ID of the plan to use
	 * @return a Map of Mechanism IDs to Mechanism names or null if the specified plan ID is unknown
	 */
	public Map<Guid, String> getAllMechanisms(Guid planID)
	{
		if(plans.get(planID) == null)
			return null;
		HashMap<Guid, String> mechs = new HashMap<Guid, String>();
		
		for(Mechanism m : plans.get(planID).getAllMechanisms())
			mechs.put(m.getGuid(), m.getName());
		
		return mechs;
	}

	/**
	 * Retreive a plan item from a plan using its ID
	 * @param planID ID of the plan to use
	 * @param itemID ID of the item to get
	 * @return the PlanItem (Event or Mechanism)
	 * @throws UnknownGUIDException if the plan ID is not found in this controller
	 */
	public PlanItem getItem(Guid planID, Guid itemID) throws UnknownGUIDException
	{
		AbstractPlan plan = getPlan(planID);
		if(plan == null || plan.getItem(itemID) == null)
			throw new UnknownGUIDException();
		
		return plan.getItem(itemID);
	}
	
	/**
	 * Builds a plan for a given timeline
	 * @param plan the plan to build
	 * @param timeline the number of time steps to sample on
	 * @throws Exception if any errors occur during the Bayes Net or sampler construction
	 */
	public void buildPlan(AbstractPlan plan, int timeline) throws Exception
	{
		plan.buildBayesNet(timeline);
	}

	/**
	 * Builds a plan for a given timeline
	 * @param plan ID of the plan to build
	 * @param timeline the number of time steps to sample on
	 * @throws UnknownGUIDException if the plan ID is not found in this controller
	 * @throws Exception if any errors occur during the Bayes Net or sampler construction
	 */
	public boolean buildPlan(Guid plan, int timeline) throws UnknownGUIDException, Exception
	{
		if(getPlan(plan) == null)
			throw new Exception("Plan could not be built, no plan was found for specified ID");
		buildPlan(getPlan(plan), timeline);
		
		return true;
	}

	/**
	 * Stop the sampler for the specified plan.  If the plan is not found, no action is taken.
	 * @param planID ID of the plan to stop
	 */
	public void stopSampler(Guid planID)
	{
		AbstractPlan plan = getPlan(planID);
		if(plan != null && plan.getBayesNet() != null)
			getPlan(planID).getBayesNet().killSampler();
	}

	public int getSampleCount(Guid planID)
	{
		AbstractPlan plan = getPlan(planID);
		if(plan != null && plan.getBayesNet() != null)
			return getPlan(planID).getBayesNet().getSampleCount();
		else
			return -1;
	}
	
	/**
	 * Add absolute type evidence to the specified item
	 * @param planID ID of the plan containing the item
	 * @param itemID ID of the item for which to add evidence
	 * @param time time step for the evidence
	 * @param probability likelyhood for the evidence
	 * @throws UnknownGUIDException if the plan or item is not found in this controller
	 */
	public void addAbsoluteEvidence(Guid planID, Guid itemID, int time, double probability) throws UnknownGUIDException
	{
		AbstractPlan plan = getPlan(planID);
		if(plan == null || plan.getItem(itemID) == null)
			throw new UnknownGUIDException();
		plan.getItem(itemID).addObservation(time, new Evidence(probability));
	}

	/**
	 * Add sensor type evidence to the specified item
	 * @param planID ID of the plan containing the item
	 * @param itemID ID of the item for which to add evidence
	 * @param time time step for the evidence
	 * @param wasTrue whether the sensor has reported true (target detected) or false (no target detected)
	 * @param FAR false alarm rate for the evidence
	 * @param MDR miss detection rate for the evidence
	 * @throws UnknownGUIDException if the plan or item is not found in this controller
	 */
	public void addSensorEvidence(Guid planID, Guid itemID, int time, boolean wasTrue, double FAR, double MDR) throws UnknownGUIDException
	{
		AbstractPlan plan = getPlan(planID);
		if(plan == null || plan.getItem(itemID) == null)
			throw new UnknownGUIDException();
		plan.getItem(itemID).addObservation(time, new Evidence(wasTrue, FAR, MDR));
	}

	/**
	 * Sets a probability of a mechanism on a given event m -> e where  P(e|m) is x.
	 * @param planID Guid of the plan
	 * @param eventID Guid of the event within the plan
	 * @param mechanismID Guid of the mechanism influencing the event
	 * @param probability float probability to assign
	 * @param protocol use SignalType.RNOR for (Recursive-Noisy-OR)
	 * @throws UnknownGUIDException if any of the IDs are not found
	 */
	public void setSingleElicitedValue(Guid planID, Guid eventID, Guid mechanismID, float probability, int protocol) throws UnknownGUIDException
	{
		AbstractPlan plan = plans.get(planID);
		if(plan == null)
			throw new UnknownGUIDException("The specified plan GUID is unkown");
		Event e = (Event) plan.getItem(eventID);
		if(e == null)
			throw new UnknownGUIDException("The specified Event GUID is unkown");
		Mechanism m = (Mechanism) plan.getItem(mechanismID);
		if(m == null)
			throw new UnknownGUIDException("The specified Mechanism GUID is unkown");
		List<Guid> signals = new ArrayList<Guid>();
		signals.add(m.getSignalGuid());
		
		plan.getLibrary().addElicitedValue(e.getProcessGuid(), protocol, signals, probability, null);
	}

	/**
	 * Set a group probability of a number of influences for a given event. 
	 * @param planID Guid plan id
	 * @param eventID Guid event id
	 * @param mechanismIDs List<Guid> collection of mechanism guids that make up the group, NOTE, they must be withing the same mode, e.g. all causes or all inhibitors
	 * @param probability float probability
	 * @param protocol use SignalType.RNOR for (Recursive-Noisy-OR)
	 * @throws UnknownGUIDException if any of the IDs are not found
	 */
	public void setSingleElicitedValue(Guid planID, Guid eventID, List<Guid> mechanismIDs, float probability, int protocol) throws UnknownGUIDException
	{
		AbstractPlan plan = plans.get(planID);
		if(plan == null)
			throw new UnknownGUIDException("The specified plan GUID is unkown");
		Event e = (Event) plan.getItem(eventID);
		if(e == null)
			throw new UnknownGUIDException("The specified Event GUID is unkown");
		List<Guid> signals = new ArrayList<Guid>();
		Mechanism m;
		for(Guid g : mechanismIDs)
		{
			m = (Mechanism) plan.getItem(g);
			if(m == null)
				throw new UnknownGUIDException("The specified Mechanism GUID is unkown:  "+g);
			signals.add(m.getSignalGuid());
		}
		
		plan.getLibrary().addElicitedValue(e.getProcessGuid(), protocol, signals, probability, null);
	}

	/**
	 * Add an Event to a plan (also performs the required actions on this plans Library)
	 * @param item Event to be added
	 * @param plan ID of the plan
	 * @throws UnknownGUIDException if the planID is not found in this controller
	 */
	public void addEvent(Event item, Guid planID) throws UnknownGUIDException
	{
		AbstractPlan plan = plans.get(planID);
		if(plan == null)
			throw new UnknownGUIDException();
		
		plan.addUnmappedItem(item);
		
		try{
			// here we define the defaults for CAUSAL, INHIBIT, EFFECT, AND GROUP PROB.
			float[] defaults = new float[] { 0.75f, 0.8f, 0.1f, 0.9f };
			plan.getLibrary().createProcess(item.getProcessGuid(), item.getName(), defaults, -1);
		}catch(Exception ex)
		{
			System.err.println("Control -  addEvent - error creating process for new Event:  ");
			ex.printStackTrace();
		}
	}
	
	//TODO:  check consolidators with this
	/**
	 * Add a Mechanism to a plan (also performs the required actions on this plans Library)
	 * @param item Item to be added
	 * @param type Type of Mechanism  
	 * Does this Mechanism cause or inhibit the outgoing event, use either SignalType.CAUSAL or SignalType.INHIBITING
	 * @param planID Guid ID of plan to add to
	 * @throws UnknownGUIDException if the planID is not found in this controller
	 */
	public void addMechanism(Mechanism item, int type, Guid planID) throws UnknownGUIDException
	{
		AbstractPlan plan = plans.get(planID);
		if(plan == null)
			throw new UnknownGUIDException("The specified plan GUID is unkown");
		
		plan.addUnmappedItem(item); // add to plan
		
		plan.getLibrary().createSignal(item.getSignalGuid(), item.getName()); // add/create signal in library
		
		// connect signal to from process and mech to from event as effect
		Event fromEv = (Event) plan.getItem(item.getFromEvent());
		if(fromEv == null)
			throw new UnknownGUIDException("'From' Event was not found in plan, be sure to add the connected Events first");
		try{
			plan.getLibrary().addEffect(fromEv.getProcessGuid(), item.getSignalGuid());
			fromEv.addEffect(item);
		}catch(Exception e)
		{
			System.err.println("Control -  addMechanism - error adding effect to plan or library for item:");
			e.printStackTrace();
		}

		// connect signal to 'to' process and mech to 'to' event as cause or inhib
		int targets = item.getNumToEvents();
		for(int i = 0; i < targets; i++)
		{
			Event toEv = (Event) plan.getItem(item.getToEvent(i));
			if(toEv == null)
				throw new UnknownGUIDException("'From' Event was not found in plan, be sure to add the connected Events first");
			try{
				if(type == SignalType.CAUSAL)
				{
					plan.getLibrary().addCause(toEv.getProcessGuid(), item.getSignalGuid());
					toEv.addCause(item);
				}
				else
				{
					plan.getLibrary().addInhibitor(toEv.getProcessGuid(), item.getSignalGuid());
					toEv.addInhibitor(item);
				}
			}catch(Exception e)
			{
				System.err.println("Control -  addMechanism - error adding cause/inhibitor to library for item:");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Remove an item from a plan
	 * @param item item to be removed
	 * @param planID ID of plan from which to remove the item
	 * @throws UnknownGUIDException if specified plan ID is not found in this controller
	 */
	public void removePlanItem(PlanItem item, Guid planID) throws UnknownGUIDException
	{
		AbstractPlan plan = plans.get(planID);
		if(plan == null)
			throw new UnknownGUIDException("The specified plan GUID is unkown");
		
		if(item != null)
			plan.removeItem(item);
	}

	/**
	 * Schedule a plan item, can be either an Event and a Mechanism
	 * @param planID ID of the plan containing the item
	 * @param itemID ID of the item to schedule
	 * @param time time step to schedule
	 * @param probability likelyhood at the given time
	 * @throws UnknownGUIDException if the planID or itemID are not found
	 */
	public void schedulePlanItem(Guid planID, Guid itemID, int time, float probability) throws UnknownGUIDException
	{
		AbstractPlan plan = plans.get(planID);
		if(plan == null)
			throw new UnknownGUIDException("The specified plan GUID is unkown");
		
		PlanItem item = plan.getItem(itemID);
		if(item == null)
			throw new UnknownGUIDException("The specified item was not found");
		
		item.scheduleEvent(time, probability);
		
	}

	/**
	 * Get predicted probability values for each time step for a given item
	 * @param planID ID of the plan
	 * @param itemID ID of the item in the plan
	 * @return an array of probabilities extracted from the sampler with length equal to sampled length of the model (time steps)
	 * Returns null if an error occurs in the BayesNet or sampler
	 * @throws UnknownGUIDException if plan was not found in this controller
	 */
	public double[] getPredicted(Guid planID, Guid itemID) throws UnknownGUIDException, Exception
	{
		try{
			AbstractPlan plan = plans.get(planID);
			if(plan == null)
				throw new UnknownGUIDException("The specified plan GUID is unkown");
			
			return plan.getPredictedProbs(itemID);
		}catch(BayesNetException e)
		{
			throw new Exception("Error getting predicted probabilities", e);
		}
	}

	/**
	 * Get inferred probability values for each time step for a given item
	 * @param planID ID of the plan
	 * @param itemID ID of the item in the plan
	 * @return an array of probabilities extracted from the sampler with length equal to sampled length of the model (time steps)
	 * Returns null if an error occurs in the BayesNet or sampler
	 * @throws UnknownGUIDException if plan was not found in this controller
	 */
	public double[] getInferred(Guid planID, Guid itemID) throws UnknownGUIDException, Exception
	{
		try{
			AbstractPlan plan = plans.get(planID);
			if(plan == null)
				throw new UnknownGUIDException("The specified plan GUID is unkown");
			
			return plan.getInferredProbs(itemID);
		}catch(BayesNetException e)
		{
			throw new Exception("Error getting inferred probabilities", e);
		}
	}

	private Collection<Event> convertStringToEvents(String stringGuids, AbstractPlan plan)
	{
		ArrayList<Event> eventList = new ArrayList<Event>();
		StringTokenizer tokenizer = new StringTokenizer(stringGuids);
		while(tokenizer.hasMoreTokens())
		{
			Guid id = new Guid(tokenizer.nextToken());
			eventList.add((Event) plan.getItem(id));
		}
		return eventList;
	}

	public static void main(String[] args) throws Exception
	{

		Guid g = Control.getInstance().openPlan(new File("civil_war.jcat"));
		AbstractPlan plan = Control.getInstance().getPlan(g);
		System.out.println("Plan loaded...about to build...");
		plan.buildBayesNet(10);
		Thread.sleep(10000);
		// 

		for(Event e : plan.getAllEvents())
		{
			System.out.println("Event: " + e.getName());
			System.out.println(e.getCauses());
			double[] prob = Control.getInstance().getPredicted(g, e.getGuid());
			for(double d : prob)
			{
				System.out.println("Prob: " + d);
			}
		}

		for(Mechanism m : plan.getAllMechanisms())
		{
			System.out.println("Mechanism: " + m.getName());
			double[] prob = Control.getInstance().getPredicted(g, m.getGuid());
			for(double d : prob)
			{
				System.out.println("Prob: " + d);
			}
		}
		//Guid civilwar= new Guid("439abfdd-9b3d-4285-a74e-db87062801bc");
		//double[] prob = Control.getInstance().getPredicted(g, civilwar);

		System.out.println("PLAN ACTIVE: " + plan.getBayesNet());
		plan.getBayesNet().killSampler();
		//plan.getAllEvents().toArray()
	}

}
