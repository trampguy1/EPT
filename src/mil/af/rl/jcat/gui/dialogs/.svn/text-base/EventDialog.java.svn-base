package mil.af.rl.jcat.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableCellEditor;

import mil.af.rl.jcat.control.Control;
import mil.af.rl.jcat.control.PlanArgument;
import mil.af.rl.jcat.gui.MainFrm;
import mil.af.rl.jcat.gui.table.MaskedComponent;
import mil.af.rl.jcat.gui.table.PTTable;
import mil.af.rl.jcat.gui.table.model.JComboEditor;
import mil.af.rl.jcat.gui.table.model.SpinnerEditor;
import mil.af.rl.jcat.gui.table.model.base.PTModel;
import mil.af.rl.jcat.gui.table.model.event.ModelToolbarListener;
import mil.af.rl.jcat.plan.AbstractPlan;
import mil.af.rl.jcat.plan.Event;
import mil.af.rl.jcat.processlibrary.Library;
import mil.af.rl.jcat.processlibrary.Signal;
import mil.af.rl.jcat.processlibrary.SignalType;
import mil.af.rl.jcat.util.DefaultDialogPage;
import mil.af.rl.jcat.util.Guid;
import mil.af.rl.jcat.util.MaskedFloat;
import mil.af.rl.jcat.util.ShapeHighlighter;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;

import com.c3i.jwb.JWBController;
import com.c3i.jwb.JWBShape;
import com.jidesoft.dialog.ButtonPanel;
import com.jidesoft.dialog.MultiplePageDialog;
import com.jidesoft.dialog.PageList;
import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.status.ButtonStatusBarItem;
import com.jidesoft.status.MemoryStatusBarItem;
import com.jidesoft.status.StatusBar;
import com.jidesoft.swing.JideTabbedPane;
import java.awt.event.WindowEvent;

/**
 * @author craig
 *
 */
public class EventDialog extends MultiplePageDialog implements ActionListener, FocusListener, KeyListener, TableColumnModelListener
{

	private static final long serialVersionUID = 1L;
	private static final String savetext = " Save ";
	private static final String canceltext = " Cancel ";
	private static final String validatetext = " Global ";

//	private float defC = SignalData.getDefaultSingleSignalCausalProbability();
//	private float defI = SignalData.getDefaultSingleSignalInhibitProbability();
//	private float defE = SignalData.getDefaultSingleSignalEffectProbability();

	private StatusBar statusbar = new StatusBar();

	private MemoryStatusBarItem memory = new MemoryStatusBarItem();
	private ButtonStatusBarItem save = new ButtonStatusBarItem(null);
	private ButtonStatusBarItem cancel = new ButtonStatusBarItem(null);
	private ButtonStatusBarItem global = new ButtonStatusBarItem(null);

	private JComboBox signals = new JComboBox(
			new String[] { "signal library is empty" });
	private JideTabbedPane tabletabs = new JideTabbedPane();

	// collapsible panes
	private CollapsiblePane eventDescriptor = new CollapsiblePane(
	"Event Description");

	private CollapsiblePane eventDefaults = new CollapsiblePane(
	"Process Default Probabilities");

	private CollapsiblePane universalDefaults = new CollapsiblePane(
	"Global Defaults");

	private JTextField eventname = new JTextField(10);
	private JTextField eventlabel = new JTextField(10);
	private static JLabel actionName = new JLabel("Event or Action Type");
	private static JLabel recieverLabel = new JLabel("Object to which it applies");

	// labels
	private static JLabel uclabel = new JLabel("Cause ");
	private static JLabel clabel = new JLabel("Cause ");
	private static JLabel elabel = new JLabel("Effect ");
	private static JLabel ilabel = new JLabel("Inhibit ");
	private static JLabel llabel = new JLabel("Leak Probability");
	private static JLabel uelabel = new JLabel("Effect ");
	private static JLabel uilabel = new JLabel("Inhibit ");

	// default probability spinner
	private MaskedComponent causeSpinner;
	private MaskedComponent inhibitSpinner;
	private MaskedComponent effectSpinner;
	private MaskedComponent leakSpinner;

	// universal
	//private JSpinner uCause;
	//private JSpinner uInhibit;
	//private JSpinner uEffect;

	// all the trees and models
	private PTTable causeRNORTable;
	//private PTTable causeGANDTable;
	private PTTable inhibitRNORTable;
	//private PTTable inhibitGANDTable;
	private PTTable effectRNORTable;
	//private PTTable effectGANDTable;
	private PTModel causeRNORModel;
	//private PTModel causeGANDModel;
	private PTModel inhibitRNORModel;
	//private PTModel inhibitGANDModel;
	private PTModel effectRNORModel;
	//private PTModel effectGANDModel;

	//private JPanel mainpanel = new JPanel();

	// Documents and models
	private JWBController plancontroller;
	private JWBShape shape;
	private mil.af.rl.jcat.plan.Event event;
	private PageList model = new PageList();
	private AbstractPlan plan = null;
	private ShapeHighlighter shapeHighlight;

	public Signal theNewSignal = null ;

	private List<PTModel> models = new ArrayList<PTModel>();
	private Frame parent;
	private JScrollPane causeTblScroll;
	private JTable phantomTable;
	private static Logger logger = Logger.getLogger(EventDialog.class);

	public EventDialog(java.awt.Frame prnt, String title, JWBShape sh, JWBController m)
	{
		super(prnt);
		parent = prnt;
		// make sure users use the button panel to exit the dialog
		setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
		setModal(true);
		plan = Control.getInstance().getPlan(Control.getInstance().getPlanId(m.getUID()));

		plancontroller = m;
		shape = sh;
		event = (Event)plan.getItem((Guid)sh.getAttachment());
		setTitle("Event Editor - " + event.getName() + " ("+event.getLabel()+")");
		this.setSize(new Dimension(640, 420));
		this.setStyle(MultiplePageDialog.ICON_STYLE);

		// maybe this should be in jbInit or someplace else anyway
		actionName.setToolTipText("An action taken or event occurring relative to a specific object.");
		recieverLabel.setToolTipText("The object to which the action occurs or the event is defined.");
		llabel.setToolTipText("Probability that unmodeled causes will make this action / event occur.");
		eventDefaults.setToolTipText("A 'Process' is 'inside' an Event and models the uncertainty associated with the Event regarding its causes, effects, and inhibitors.");

		clabel.setToolTipText("The causal probability for all causal signals whose probability is not otherwise specified.");
		ilabel.setToolTipText("The inhibiting probability for all inhibiting signals whose probability is not otherwise specified.");
		elabel.setToolTipText("The effect probability for all effect signals whose probability is not otherwise specified.");

		try
		{
			jbInit();
			setLocationRelativeTo(parent);
			//this.getButtonPanel()
		} catch (Exception ex)
		{
			logger.error("Constructor - error initializing dialog:  ", ex);
		}
		shapeHighlight = new ShapeHighlighter(sh, ShapeHighlighter.ALPHA);
	}



	private void jbInit() throws Exception
	{
		save.setText(savetext);
		cancel.setText(canceltext);
		global.setText(validatetext);
		save.addActionListener(this);
		cancel.addActionListener(this);

		initSignalList();

		statusbar.add(save);
		statusbar.add(cancel);
		statusbar.add(global);
		statusbar.add(signals);
		statusbar.add(memory);

		// init event panel
		DefaultDialogPage eventpanel = new DefaultDialogPage("General", new ImageIcon(
				this.getClass().getClassLoader().getResource( "general.png")));
		eventpanel.setLayout(new GridLayout(2, 1));

		GridLayout edLayout = new GridLayout(4, 2);
		edLayout.setVgap(5);
		eventDescriptor.getContentPane().setLayout(edLayout);

		actionName.setHorizontalAlignment(JLabel.RIGHT);
		recieverLabel.setHorizontalAlignment(JLabel.RIGHT);
		actionName.setBackground(Color.WHITE);
		recieverLabel.setBackground(Color.WHITE);
		llabel.setHorizontalAlignment(JLabel.RIGHT);

		leakSpinner = new MaskedComponent(MaskedFloat.getMaskedValue(event.getLeak()));

		eventname.addFocusListener(this);
		eventname.addKeyListener(this);
		eventlabel.addFocusListener(this);
		eventlabel.addKeyListener(this);

		eventDescriptor.getContentPane().add(actionName);
		eventDescriptor.getContentPane().add(eventname);
		eventDescriptor.getContentPane().add(recieverLabel);
		eventDescriptor.getContentPane().add(eventlabel);
		eventDescriptor.getContentPane().add(llabel);
		eventDescriptor.getContentPane().add(leakSpinner);
		eventname.setText(event.getName());
		eventlabel.setText(event.getLabel());

		eventpanel.add(this.eventDescriptor);

		DefaultDialogPage processPanel = new DefaultDialogPage("Default Probabilities", new ImageIcon(
				this.getClass().getClassLoader().getResource( "prob.png")));
		processPanel.setLayout(new BorderLayout());
		processPanel.add(this.eventDefaults, BorderLayout.CENTER);
		//eventpanel.add(this.universalDefaults);

		// init the event probabilities
		causeSpinner = new MaskedComponent(MaskedFloat.getMaskedValue(event.getSingleCausalProb(plan.getLibrary())));
		causeSpinner.addChangeListener(new EventValueListener(this, SignalType.CAUSAL));
		effectSpinner = new MaskedComponent(MaskedFloat.getMaskedValue(event.getSingleEffectProb(plan.getLibrary())));
		// FOR NOW MAKE EFFECT NON EDITABLE
		effectSpinner.setEnabled(false);
		effectSpinner.addChangeListener(new EventValueListener(this, SignalType.EFFECT));
		inhibitSpinner = new MaskedComponent(MaskedFloat.getMaskedValue(event.getSingleInhibitingProb(plan.getLibrary())));
		inhibitSpinner.addChangeListener(new EventValueListener(this, SignalType.INHIBITING));
		// create default labels

		JButton apply  = new JButton("Apply");
		apply.addActionListener(this);
		apply.setToolTipText("apply these default probabilities");

		clabel.setForeground(new Color(0, 0, 119));
		clabel.setHorizontalAlignment(JLabel.RIGHT);

		elabel.setForeground(new Color(0, 0, 119));
		elabel.setHorizontalAlignment(JLabel.RIGHT);
		ilabel.setForeground(new Color(0, 0, 119));
		ilabel.setHorizontalAlignment(JLabel.RIGHT);

		// add them to the default collapsible pane
		GridLayout eDefLayout = new GridLayout(4, 2);
		eDefLayout.setVgap(5);
		eventDefaults.getContentPane().setLayout(eDefLayout);
		eventDefaults.getContentPane().add(clabel);
		eventDefaults.getContentPane().add(causeSpinner);
		eventDefaults.getContentPane().add(ilabel);
		eventDefaults.getContentPane().add(inhibitSpinner);
		eventDefaults.getContentPane().add(elabel);
		eventDefaults.getContentPane().add(effectSpinner);
		eventDefaults.getContentPane().add(new JLabel());
		eventDefaults.getContentPane().add(apply);


		/*/ INIT THE UNIVERSAL PANE

        uclabel.setForeground(new Color(0, 0, 119));
        uclabel.setHorizontalAlignment(JLabel.RIGHT);

        uelabel.setForeground(new Color(0, 0, 119));
        uelabel.setHorizontalAlignment(JLabel.RIGHT);
        uilabel.setForeground(new Color(0, 0, 119));
        uilabel.setHorizontalAlignment(JLabel.RIGHT);

        // init the event default probabilities
        uCause = new JSpinner(new SpinnerNumberModel(new Float(defC),
                new Float(0.00), new Float(1.00), new Float(0.01)));
        uCause.addChangeListener(new EventValueListener(this, SignalType.CAUSAL));
        uEffect = new JSpinner(new SpinnerNumberModel(new Float(defE),
                new Float(0.00), new Float(1.00), new Float(0.01)));
        uEffect.addChangeListener(new EventValueListener(this, SignalType.CAUSAL));
        uInhibit = new JSpinner(new SpinnerNumberModel(new Float(defI),
                new Float(0.00), new Float(1.00), new Float(0.01)));
        uInhibit.addChangeListener(new EventValueListener(this, SignalType.CAUSAL));

        universalDefaults.getContentPane().setLayout(new GridLayout(4, 2));
        universalDefaults.getContentPane().add(uclabel);
        universalDefaults.getContentPane().add(uCause);
        universalDefaults.getContentPane().add(uilabel);
        universalDefaults.getContentPane().add(uInhibit);
        universalDefaults.getContentPane().add(uelabel);
        universalDefaults.getContentPane().add(uEffect);*/

		this.initializeTables();

		//Make Pages For the Dialog
		DefaultDialogPage table = new DefaultDialogPage("Specified \r Probabilities", new ImageIcon(
				this.getClass().getClassLoader().getResource( "elicit.png")));
		table.setLayout(new GridLayout(1,1));
		table.add(this.tabletabs);

		//setup the context help
        javax.help.CSH.setHelpIDString(eventDescriptor, "Event_Editor_Dialog_Box1");
        javax.help.CSH.setHelpIDString(eventDefaults, "Event_Default_Probabilities1");
        javax.help.CSH.setHelpIDString(table, "Specified_Prob_of_an_Event1");

		//Add the pages to the model
		model.append(eventpanel);
		model.append(processPanel);
		model.append(table);
		//model.append(new DefaultDialogPage("Signals", new ImageIcon("resources/images/signal.png")));
		//Set The page List
		this.setPageList(this.model);
	}

	public Object getSelectedSignal()
	{
		if (signals.getSelectedIndex() != -1)
		{
			return signals.getSelectedItem();
		}
		return null;
	}

	private void initSignalList()
	{

		java.util.Collection sc = plan.getSignalsForEvent((Event)plan.getItem((Guid)shape.getAttachment()));
		if (sc == null || sc.size() == 0)
			return;
		else
			signals.setModel(new DefaultComboBoxModel(new java.util.Vector(sc)));
	}

	public Event getCurrentEvent()
	{
		return (Event)plan.getItem((Guid)shape.getAttachment());
	}

	public void initializeTables()
	{

		Document process = plan.getLibrary().getProcessDocument(event.getProcessGuid());

		Element cset = (Element) process.selectSingleNode("//ModeSet[@mode='" + SignalType.CAUSAL + "']");
		causeRNORModel = new PTModel(cset, SignalType.CAUSAL, SignalType.RNOR, plan, getCurrentEvent());
		causeRNORTable = new PTTable(causeRNORModel);
		causeRNORTable.getColumnModel().addColumnModelListener(this);
		causeRNORTable.setAutoResizeMode(PTTable.AUTO_RESIZE_OFF);
		causeRNORTable.getTableHeader().setResizingAllowed(false);

		models.add(causeRNORModel);
		//causeGANDModel = new PTModel(cset, SignalType.CAUSAL, SignalType.GAND, plan,getCurrentEvent());
		//causeGANDTable = new PTTable(causeGANDModel);

		Element iset = (Element) process.selectSingleNode("//ModeSet[@mode='" + SignalType.INHIBITING + "']");
		inhibitRNORModel = new PTModel(iset, SignalType.INHIBITING, SignalType.RNOR, plan,getCurrentEvent());
		inhibitRNORTable = new PTTable(inhibitRNORModel);        
		inhibitRNORTable.getColumnModel().addColumnModelListener(this);
		inhibitRNORTable.setAutoResizeMode(PTTable.AUTO_RESIZE_OFF);
		inhibitRNORTable.getTableHeader().setResizingAllowed(false);

		models.add(inhibitRNORModel);
		//inhibitGANDModel = new PTModel(iset, SignalType.INHIBITING, SignalType.GAND, plan,getCurrentEvent());
		//inhibitGANDTable = new PTTable(inhibitGANDModel);

		Element eset = (Element) process.selectSingleNode("//ModeSet[@mode='" + SignalType.EFFECT + "']");
		effectRNORModel = new PTModel(eset, SignalType.EFFECT, SignalType.RNOR, plan,getCurrentEvent());
		effectRNORTable = new PTTable(effectRNORModel);
		effectRNORTable.getColumnModel().addColumnModelListener(this);
		effectRNORTable.setAutoResizeMode(PTTable.AUTO_RESIZE_OFF);
		effectRNORTable.getTableHeader().setResizingAllowed(false);
		
		//used (not shown visually) only to generate column events while having autoresize modes off on the other tables
		phantomTable = new JTable(new javax.swing.table.DefaultTableModel());
		phantomTable.getColumnModel().addColumnModelListener(this);
		phantomTable.addColumn(new javax.swing.table.TableColumn());

		models.add(effectRNORModel);
		//effectGANDModel = new PTModel(eset, SignalType.EFFECT, SignalType.GAND, plan,getCurrentEvent());
		//effectGANDTable = new PTTable(effectGANDModel);

		adjustColumns(causeRNORTable);
		adjustColumns(inhibitRNORTable);
		adjustColumns(effectRNORTable);

		// create causal panes
		JPanel causalpanel = new JPanel();
		causalpanel.setLayout(new GridLayout(1, 1));

		JPanel corpanel = new JPanel(new BorderLayout());
		JPanel crnorpane = new JPanel(new BorderLayout());
//		CollapsiblePane crnorpane = new CollapsiblePane("OR Groups");
//		crnorpane.getContentPane().setLayout(new BorderLayout());
		JToolBar crbar = makeToolBar(causeRNORModel);
		crbar.setOrientation(JToolBar.HORIZONTAL);
		crnorpane.add(causeTblScroll = new JScrollPane(causeRNORTable), BorderLayout.CENTER);
//		crnorpane.setStyle(CollapsiblePane.TREE_STYLE);
		corpanel.add(crbar, BorderLayout.NORTH);
		corpanel.add(crnorpane, BorderLayout.CENTER);
		corpanel.add(phantomTable, BorderLayout.SOUTH);
		causalpanel.add(corpanel);

		//Stuff taken out for NASIC march release
		/*JPanel andpanel = new JPanel();
        andpanel.setLayout(new BorderLayout());
        CollapsiblePane cgandpane = new CollapsiblePane("AND Groups");
        cgandpane.setStyle(CollapsiblePane.TREE_STYLE);
        cgandpane.getContentPane().setLayout(new BorderLayout());
        JToolBar cgbar = this.makeToolBar(causeGANDModel);
        cgbar.setOrientation(JToolBar.HORIZONTAL);
        cgandpane.getContentPane().add(new JScrollPane(causeGANDTable), BorderLayout.CENTER);
        andpanel.add(cgbar, BorderLayout.NORTH);
        andpanel.add(cgandpane, BorderLayout.CENTER);
        causalpanel.add(andpanel);*/

		// add to the tabbed pane
		this.tabletabs.addTab("CAUSAL", causalpanel);

		// create inhibiting panes
		JPanel inhibitpanel = new JPanel();
		inhibitpanel.setLayout(new GridLayout(1, 1));

		JPanel iorpanel = new JPanel(new BorderLayout());
//		CollapsiblePane irnorpane = new CollapsiblePane("OR Groups");
		JPanel irnorpane = new JPanel(new BorderLayout());
		irnorpane.setLayout(new BorderLayout());
		JToolBar irbar = makeToolBar(inhibitRNORModel);
		irbar.setOrientation(JToolBar.HORIZONTAL);
		irnorpane.add(new JScrollPane(inhibitRNORTable));
//		irnorpane.setStyle(CollapsiblePane.TREE_STYLE);
		iorpanel.add(irbar, BorderLayout.NORTH);
		iorpanel.add(irnorpane, BorderLayout.CENTER);
		inhibitpanel.add(iorpanel);

		//Stuff taken out for NASIC march release
		/*JPanel iandpanel = new JPanel(new BorderLayout());
        CollapsiblePane igandpane = new CollapsiblePane("AND Groups");
        igandpane.setStyle(CollapsiblePane.TREE_STYLE);
        igandpane.getContentPane().setLayout(new BorderLayout());
        JToolBar igbar = this.makeToolBar(inhibitGANDModel);
        igbar.setOrientation(JToolBar.HORIZONTAL);
        igandpane.getContentPane().add(new JScrollPane(inhibitGANDTable));
        iandpanel.add(igbar, BorderLayout.NORTH);
        iandpanel.add(igandpane, BorderLayout.CENTER);
        inhibitpanel.add(iandpanel);*/

		// add to the tabbed pane
		this.tabletabs.addTab("INHIBIT", inhibitpanel);

		// EFFECT STUFF
		JPanel effectpanel = new JPanel();
		effectpanel.setLayout(new GridLayout(1, 1));

		JPanel eorpanel = new JPanel(new BorderLayout());
		JPanel ernorpane = new JPanel(new BorderLayout());
//		CollapsiblePane ernorpane = new CollapsiblePane("OR Groups");
		ernorpane.setLayout(new BorderLayout());
		JToolBar erbar = makeToolBar(effectRNORModel);
		erbar.setOrientation(JToolBar.HORIZONTAL);
		ernorpane.add(new JScrollPane(effectRNORTable));
//		ernorpane.setStyle(CollapsiblePane.TREE_STYLE);
		eorpanel.add(erbar, BorderLayout.NORTH);
		eorpanel.add(ernorpane, BorderLayout.CENTER);
		effectpanel.add(eorpanel);

		//Stuff taken out for NASIC march release
		/*JPanel eandpanel = new JPanel(new BorderLayout());
        CollapsiblePane egandpane = new CollapsiblePane("AND Groups");
        egandpane.setStyle(CollapsiblePane.TREE_STYLE);
        egandpane.getContentPane().setLayout(new BorderLayout());
        JToolBar egbar = this.makeToolBar(effectGANDModel);
        egbar.setOrientation(JToolBar.HORIZONTAL);
        egandpane.getContentPane().add(new JScrollPane(effectGANDTable));
        eandpanel.add(egbar, BorderLayout.NORTH);
        eandpanel.add(egandpane, BorderLayout.CENTER);
        effectpanel.add(eandpanel);*/
		// add to the tabbed pane
		this.tabletabs.addTab("EFFECT", effectpanel);


	}

	private JToolBar makeToolBar(PTModel m)
	{
		JToolBar toolbar = new JToolBar();
		ModelToolbarListener listener = new ModelToolbarListener(this, m);

		JButton nsig = new JButton(new ImageIcon(
				this.getClass().getClassLoader().getResource( "nsig.png")));
		nsig.setActionCommand("nsig");
		nsig.setText("Add Signal");
		nsig.setToolTipText("add a signal");
		nsig.addActionListener(listener);
		toolbar.add(nsig);

		JButton agrp = new JButton(new ImageIcon(
				this.getClass().getClassLoader().getResource( "agrp.png")));
		agrp.setActionCommand("agrp");
		agrp.setText("New Group");
		agrp.setToolTipText("Choose a set of signals and specify probability for this combination.");
		agrp.addActionListener(listener);
		toolbar.add(agrp);

		toolbar.setFloatable(false);
		return toolbar;
	}

	/*public Document mergeTableElements()
    {

        Document proc = DocumentFactory.getInstance().createDocument(
                DocumentFactory.getInstance().createElement("Process"));
        Element root = proc.getRootElement();
        mil.af.rl.jcat.util.Guid g = event.getProcessGuid();
        root.addAttribute("guid", g.getValue());
        root.addAttribute("name", event.getName());
        Element signalData = root.addElement("SignalData");
        //  now get elements from each table
        // CAUSE
        Element rnorcause = this.causeRNORModel.saveModeProtocol();
        Element gandcause = this.causeGANDModel.saveModeProtocol();
        Element cause = mergeModeSet(rnorcause, gandcause);
        cause.addAttribute("defaultSingleSignal", Float.toString(causeRNORModel.getDefprob()));
        signalData.add(rnorcause);
        // INHIBIT
        Element rnorinhibit = this.inhibitRNORModel.saveModeProtocol();
        Element gandinhibit = this.inhibitGANDModel.saveModeProtocol();
        Element inhibit = mergeModeSet(rnorinhibit, gandinhibit);
        inhibit.addAttribute("defaultSingleSignal", Float.toString(inhibitRNORModel.getDefprob()));
        signalData.add(inhibit);
        // EFFECT
        Element rnoreffect = this.effectRNORModel.saveModeProtocol();
        Element gandeffect = this.effectGANDModel.saveModeProtocol();
        Element effect = mergeModeSet(rnoreffect, gandeffect);
        effect.addAttribute("defaultSingleSignal", Float.toString(effectRNORModel.getDefprob()));
        signalData.add(effect);

        return proc;
    }*/

	/*private Element mergeModeSet(Element rnor, Element gand)
    {
        // first set modeset default on rnor (we'll return it)
        // now do the merging
        Hashtable commonset = new Hashtable();
        java.util.List rnorset = rnor.selectNodes(".//SignalSet/Signal");
        if (rnorset != null && rnorset.size() > 0)
        {
            Iterator ri = rnorset.iterator();
            for (; ri.hasNext();)
            {
                Element e = (Element) ri.next();
                commonset.put(e.attributeValue("guid"), e
                        .attributeValue("name"));
            }
        }
        // now do the same for gand, duplicate elements will be replaced
        java.util.List gandset = gand.selectNodes(".//SignalSet/Signal");
        if (gandset != null && gandset.size() > 0)
        {
            Iterator ri = gandset.iterator();
            for (; ri.hasNext();)
            {
                Element e = (Element) ri.next();
                commonset.put(e.attributeValue("guid"), e
                        .attributeValue("name"));
            }
        }
        // now take the rnor element and build it
        Element uss = (Element) rnor.selectSingleNode(".//SignalSet");
        uss.content().clear();
        // now populate it again
        if (commonset.size() > 0)
        {
            Iterator sei = commonset.keySet().iterator();
            for (; sei.hasNext();)
            {
                String guid = sei.next().toString();
                uss.addElement("Signal").addAttribute("guid", guid)
                        .addAttribute("name", commonset.get(guid).toString());
            }
        }
        // now get the gand protocol element
        Element gandprot = (Element) gand
                .selectSingleNode(".//ProtocolSet[@protocol='" + SignalType.GAND
                        + "']");
        Element gp = gandprot.createCopy("ProtocolSet"); // in order to get rid
        // of the root
        rnor.add(gp);

        return rnor;
    }*/

	public void setDefaultCause(Float c)
	{
//		defC = c.floatValue();
		//event.setSingleCausalProb(defC);
		//this.causeGANDModel.setDefaultProbability(defC);
		this.causeRNORModel.setDefaultProbability(c.floatValue()); //defC);
	}

	public void setDefaultInhibit(Float i)
	{
//		defI = i.floatValue();
		//event.setSingleInhibitingProb(defI);
		//this.inhibitGANDModel.setDefaultProbability(defI);
		this.inhibitRNORModel.setDefaultProbability(i.floatValue());  //defI);
	}

	public void setDefaultEffect(Float e)
	{
//		defE = e.floatValue();
		//event.setSingleEffectProb(defE);
		//this.effectGANDModel.setDefaultProbability(defE);
		this.effectRNORModel.setDefaultProbability(e.floatValue()); //defE);
	}

	public void setActivePane(String paneName)
	{
		model.setCurrentPage(model.getPageByFullTitle(paneName));
	}

	public void setActivePane(int i)
	{
		model.setCurrentPage(model.getPage(i));
	}

	public void actionPerformed(ActionEvent e)
	{
		if(e.getActionCommand().equals("Apply"))
		{
			causeRNORModel.resetDefaultValues(((MaskedFloat)causeSpinner.getValue()).floatValue());
			inhibitRNORModel.resetDefaultValues(((MaskedFloat)inhibitSpinner.getValue()).floatValue());
			effectRNORModel.resetDefaultValues(((MaskedFloat)effectSpinner.getValue()).floatValue());
		}
		else if (e.getActionCommand().equals(" Save "))
		{
			Library lib = plan.getLibrary();
			for(PTModel model : models)
			{
				model.persistElicitedProbabilities();
			}

			float newDefC = ((MaskedFloat)causeSpinner.getValue()).floatValue();
			event.setDefCausalProb(newDefC, lib);
			float newDefI = ((MaskedFloat)inhibitSpinner.getValue()).floatValue();
			event.setDefInhibitingProb(newDefI, lib);
			float newDefE = ((MaskedFloat)effectSpinner.getValue()).floatValue();
			event.setDefEffectProb(newDefE, lib);

			//Document p = this.mergeTableElements();
			//event.setProcessDocument(p, event.getProcessGuid());
			event.setLeak(((MaskedFloat)leakSpinner.getValue()).floatValue());
			//lib.setProcessFromDocument(p, null);
			event.setName(eventname.getText());
			event.setLabel(eventlabel.getText());
			shape.setAttachment(event.getGuid());
			shape.setText(event.getName() + "\n\n" + event.getLabel());
			lib.setProcessName(event.getProcessGuid(), eventname.getText());
			
			try{
				plancontroller.putShape(shape);
				plancontroller.foreignUpdate(new PlanArgument(PlanArgument.ITEM_UPDATE, event, false));
			}catch(java.rmi.RemoteException re)
			{
				logger.error("actionPerformed - RemoteExc saving event edit:  "+re.getMessage());
			}
			dispose();

		} else if (e.getActionCommand().equals(" Cancel "))
		{
			//shape.setLock(false,planmodel.getUID());
			//planmodel.modifiedAttachment(shape);
			dispose();
		}

	}


	public void windowClosing(WindowEvent e) {
		//super.windowClosing(e);
	}
	/**
	 * @return Returns the planmodel.
	 */
	public JWBController getPlanController()
	{
		return plancontroller;
	}

	/**
	 * @return
	 */
	public Library getLibrary()
	{
		mil.af.rl.jcat.util.Guid pid = Control.getInstance().getPlanId(plancontroller.getUID());
		return Control.getInstance().getPlan(pid).getLibrary();
	}

	/* (non-Javadoc)
	 * @see com.jidesoft.dialog.StandardDialog#createButtonPanel()
	 */
	public ButtonPanel createButtonPanel() {
		ButtonPanel p = new ButtonPanel();
		java.awt.FlowLayout layout = new java.awt.FlowLayout();
		layout.setHgap(20);
		layout.setAlignment(FlowLayout.RIGHT);
		p.setLayout(layout);

		JButton apply  = new JButton("Apply Def. Probabilities");
		apply.addActionListener(this);
		apply.setToolTipText("apply current default settings..");
		//p.add(apply);  //button replace by apply button on default probabilities page
		JButton save = new JButton(" Save ");
		save.setFont(save.getFont().deriveFont(12f));
		save.addActionListener(this);
		p.add(save);
		setDefaultAction(save.getAction());
		JButton cancel = new JButton(" Cancel ");
		cancel.setFont(cancel.getFont().deriveFont(12f));
		cancel.addActionListener(this);
		p.add(cancel);
		setDefaultCancelAction(cancel.getAction());
		JButton cHelpBtn = new JButton(new ImageIcon(this.getClass().getClassLoader().getResource("chelp_sm.gif")));
		cHelpBtn.setMargin(new java.awt.Insets(0,2,0,2));
		try
		{
			if(parent instanceof MainFrm)
				cHelpBtn.addActionListener(new javax.help.CSH.DisplayHelpAfterTracking(((MainFrm)parent).getHelpBroker()));
			p.add(cHelpBtn);
		}catch(Exception ex)
		{
			logger.error("error making help button");
		}
		return p;
	}
	/* (non-Javadoc)
	 * @see com.jidesoft.dialog.MultiplePageDialog#createIndexPanel()
	 */
	public JComponent createIndexPanel() {
		return super.createIndexPanel();
	}

	public void adjustColumns(PTTable table)
	{
		try{
			
			if(table.getColumnCount() >= 2)
			{
				int tabWidth = isVisible()?causeTblScroll.getWidth():450;
				TableCellEditor editor = table.getCellEditor(0, 1);
				
				int width = 0, tWidth = 0, sigColSz = 0;
				
				if(editor instanceof JComboEditor)
					width = ((JComboEditor)editor).getComponent().getMinimumSize().width;
				else if(editor instanceof SpinnerEditor)
					width = ((SpinnerEditor)editor).getComponent().getMinimumSize().width;
				else
					width = 50;
				
				for(int x=1; x<table.getColumnCount(); x++)
				{
					table.getColumnModel().getColumn(x).setPreferredWidth(width);
					tWidth += width;
				}
				
				for(int y=0; y<table.getRowCount(); y++)
				{
					
						Object val = table.getModel().getValueAt(y, 0);
						if(val != null)
							sigColSz = Math.max(sigColSz, table.getFontMetrics(table.getFont()).stringWidth(val.toString()));
					
				}
				
				table.getColumnModel().getColumn(0).setPreferredWidth(Math.max(tabWidth - tWidth - 3, sigColSz + 5));
			}
		}catch(Exception exc){
			exc.printStackTrace();
		}
	}

	public void dispose()
	{
		shapeHighlight.stop();
		super.dispose();
	}
	//
	public void keyPressed(KeyEvent event)
	{
	}

	public void columnAdded(TableColumnModelEvent e)
	{
		if(e.getSource().equals(causeRNORTable.getColumnModel()))
			adjustColumns(causeRNORTable);
		else if(e.getSource().equals(inhibitRNORTable.getColumnModel()))
			adjustColumns(inhibitRNORTable);
		else if(e.getSource().equals(effectRNORTable.getColumnModel()))
			adjustColumns(effectRNORTable);
	}
	
	public void columnMarginChanged(ChangeEvent e)
	{
//		if(e.getSource().equals(causeRNORTable.getColumnModel()))
//			adjustColumns(causeRNORTable);
//		else if(e.getSource().equals(inhibitRNORTable.getColumnModel()))
//			adjustColumns(inhibitRNORTable);
//		else if(e.getSource().equals(effectRNORTable.getColumnModel()))
//			adjustColumns(effectRNORTable);
		if(e.getSource().equals(phantomTable.getColumnModel())) //caused by dialog resize
		{
//			String currentTab = tabletabs.getTitleAt(tabletabs.getSelectedIndex());
//			if(currentTab.equalsIgnoreCase("CAUSAL"))
			adjustColumns(causeRNORTable);
//			else if(currentTab.equalsIgnoreCase("INHIBIT"))
			adjustColumns(inhibitRNORTable);
//			else if(currentTab.equalsIgnoreCase("EFFECT"))
			adjustColumns(effectRNORTable);
		}
	}

	public void columnRemoved(TableColumnModelEvent e)
	{
		//dont' need this right now since, upon removing group, all columns are removed then added (columnAdded does a column adjust)
		//adjustColumns();		
	}

	//unused keyevents
	public void keyReleased(KeyEvent e){}
	public void keyTyped(KeyEvent e){}
	//unused tablecolumn events
	public void columnMoved(TableColumnModelEvent e){}
	public void columnSelectionChanged(ListSelectionEvent e){}


	public void focusGained(FocusEvent event)
	{
		if(event.getSource() == eventname)
			eventname.selectAll();
		else if(event.getSource() == eventlabel)
			eventlabel.selectAll();
	}

	public void focusLost(FocusEvent event)
	{

	}
}
