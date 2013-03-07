/*
 * Created on Mar 8, 2006
 * Author: Mike D
 * ExplainDialog.java - 
 */
package mil.af.rl.jcat.gui.dialogs;

//import java.awt.BorderLayout;
//import java.awt.Dimension;
//import java.awt.GridBagConstraints;
//import java.awt.GridBagLayout;
//import java.awt.GridLayout;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.text.NumberFormat;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Vector;
//
//import javax.swing.BorderFactory;
//import javax.swing.DefaultListModel;
//import javax.swing.JButton;
//import javax.swing.JDialog;
//import javax.swing.JLabel;
//import javax.swing.JList;
//import javax.swing.JPanel;
//import javax.swing.JScrollPane;
//import javax.swing.JSlider;
//import javax.swing.JTree;
//import javax.swing.ListModel;
//import javax.swing.ScrollPaneConstants;
//import javax.swing.event.ChangeEvent;
//import javax.swing.event.ChangeListener;
//import javax.swing.tree.DefaultMutableTreeNode;
//import javax.swing.tree.DefaultTreeModel;
//import javax.swing.tree.TreeModel;
//import javax.swing.tree.TreeNode;
//import javax.swing.tree.TreePath;
//
//import mil.af.rl.jcat.bayesnet.BayesNet;
//import mil.af.rl.jcat.bayesnet.NetNode;
//import mil.af.rl.jcat.plan.AbstractPlan;
//import mil.af.rl.jcat.plan.Mechanism;
//import mil.af.rl.jcat.util.ExplainChain;
//import mil.af.rl.jcat.util.ExplainPathItem;
//import mil.af.rl.jcat.util.Guid;
//import mil.af.rl.jcat.util.IndexManagement;

public class ExplainDialog{}/* extends JDialog implements ActionListener, Runnable, ChangeListener
{

	private static final long serialVersionUID = 1L;
	private JPanel mainPanel;
	private JPanel topPanel;
	private JLabel titleL;
	private JLabel titleR;
	private NetNode explainedItem;
	private JPanel midPanel;
	private JTree causeTree;
	private JList eventNameList;
	private JList endNodeList;
	private Vector endTreeNodes = new Vector();
	private Vector endNodesForList = new Vector();
	private Vector chainTime1 = new Vector();
	private Vector chainTime2 = new Vector();
	private Vector allExplainedNetNodes = new Vector();
	private JButton modeButton;
	private GridBagLayout layout = new GridBagLayout();
	private GridBagConstraints constraints = new GridBagConstraints();
	private JPanel bottomPanel;
	private JButton goButton;
	private ProgressBox prog;
	private AbstractPlan plan;
	private Guid lastEventProc;
	private NumberFormat nf;
	private JSlider threshSlider;
	private JLabel threshLbl;
	private Guid explainedItemID;
	private int timeSlice1;
	private int timeSlice2;
	private float totalAllChains = 0;
	private JList sliceList;
	private JList percentJoint;
	private JButton testButton;

	public ExplainDialog(java.awt.Frame parent, AbstractPlan thePlan, Guid itemGUID, int time)
	{
		this(parent, thePlan, itemGUID, time, time);
	}
	
	public ExplainDialog(java.awt.Frame parent, AbstractPlan thePlan, Guid itemGUID, int time, int time2)
	{
		super(parent, "Explain");
		timeSlice1 = time;
		timeSlice2 = time2;
		plan = thePlan;
		explainedItemID = itemGUID;
		//explainedItem = ((BayesNet)plan.getBayesNet()).getEvent(itemGUID);
		
		
		int width = 400, height = 600;
		setSize(width, height);
		setLocationRelativeTo(parent);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		mainPanel = new JPanel(new BorderLayout());
		topPanel = new JPanel(new java.awt.FlowLayout());
		bottomPanel = new JPanel(new java.awt.FlowLayout());
		GridLayout midLayout = new GridLayout(1,0);
		midLayout.setHgap(10);
		midPanel = new JPanel(midLayout);
		midPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
		
		nf = java.text.NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		String prob = nf.format(plan.getPredictedProbs(itemGUID)[time]);
		String prob2 = nf.format(plan.getPredictedProbs(itemGUID)[time2]);
		String diff = nf.format(Math.abs(Double.parseDouble(prob) - Double.parseDouble(prob2)));
		//System.out.println(prob[0]+"  "+prob[1]+"  "+prob[2]+"  "+prob[3]+"  "+prob[4]+"  "+prob[5]+"  "+prob[6]);
		
		java.awt.Font theFont = new java.awt.Font("Arial", 1, 18);
		endNodeList = new JList(endNodesForList);
		String title = (time==time2)? "Why the value of "+prob+" for " : "Why the "+diff+" change of ";
		titleL = new JLabel(title);
		JLabel titleEv = new JLabel(plan.getItem(itemGUID).getName());
		String title1 = (time==time2)? " at time " : " between time ";
		titleR = new JLabel(title1);
		String title2 = (time==time2)? time+"" : time+" and "+time2;
		JLabel titleTm = new JLabel(title2);
		JLabel titleQ = new JLabel(" ?");
		titleL.setFont(theFont);
		titleEv.setFont(theFont.deriveFont(2));
		titleR.setFont(theFont);
		titleTm.setFont(theFont.deriveFont(2));
		titleQ.setFont(theFont);
		topPanel.add(titleL);
		topPanel.add(titleEv);
		topPanel.add(titleR);
		topPanel.add(titleTm);
		topPanel.add(titleQ);
		
		modeButton = new JButton("Advanced Mode");
		modeButton.addActionListener(this);
		goButton = new JButton(" GO ");
		goButton.addActionListener(this);
		threshLbl = new JLabel("Threshold  "+(int)(NetNode.explainThreshold*100f));
		threshSlider = new JSlider(1, 99, (int)(NetNode.explainThreshold*100f));
		threshSlider.setPreferredSize(new Dimension(100,20));
		threshSlider.addChangeListener(this);
		testButton = new JButton("Test");
		testButton.addActionListener(this);
		bottomPanel.add(threshLbl);
		bottomPanel.add(threshSlider);
		bottomPanel.add(goButton);
		bottomPanel.add(modeButton);
		bottomPanel.add(testButton);
		
		//bottomPanel.add(createTestPanel());
		//bottomPanel.add(endNodeList);
				
		mainPanel.add(topPanel, BorderLayout.NORTH);
		
		JScrollPane midScroll = new JScrollPane(midPanel);
		midScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		mainPanel.add(midScroll, BorderLayout.CENTER);
		
		mainPanel.add(bottomPanel, BorderLayout.SOUTH);

		getCauseTree(); //make the tree for functionality but not needed for display
		getSliceList();
		getPercentJoint();
		getEventNameList();

//		JDialog bla = new JDialog(this);
//		bla.setSize(300,500);
//		bla.setLayout(new GridLayout(4,1));
//		bla.add(getCauseTree()); //make the tree for functionality but not needed for display
//		bla.add(getSliceList());
//		bla.add(getPercentJoint());
//		bla.add(getEventNameList());
//		bla.setVisible(true);
		
		//this.getContentPane().add(mainPanel);
		setContentPane(mainPanel);
		
		
		setVisible(true);
		
//		create a progressbox for later use
		prog = new ProgressBox(this, "Explaining", "", 20);
	}
	
	private ExplainChain explainChainFromTreePath(TreeNode[] path, int time)
	{
		constraints.fill = GridBagConstraints.NONE; 
		constraints.anchor = GridBagConstraints.NORTH;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		
		List items = java.util.Arrays.asList(path);
		ExplainChain chain = buildChain(items, time);
				
		return chain;
	}
	
/*	private JComponent createTestPanel()
	{
		JPanel testPanel = new JPanel(new GridLayout(0,1));
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(testPanel);
		testPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
		//scrollPane.setBounds(20, 20, width/3, 300);
		
		
		Vector testVect = new Vector();
		testVect.add(new JButton("Event 1"));
		testVect.add(new JButton("Event 2"));
		testVect.add(new JButton("Event 3"));
		testVect.add(new JButton("Event 4"));
		testVect.add(new JButton("Event 5"));
		testVect.add(new JButton("Event 6"));
		testVect.add(new JButton("Event 7"));
		testVect.add(new JButton("Event 8"));
		
		buildChain(testPanel, testVect);
		
		return scrollPane;
	} 
	
	public ExplainChain buildChain(List comps, int time)
	{
		// select top-most node (explained node) in cause tree 
		causeTree.setSelectionRow(0);
		
		ExplainChain chain = new ExplainChain();
		chain.setLayout(layout);
		float totalChainProb = 1;
		boolean isInhib = false;
		chain.removeAll();
		List data = comps;
		Iterator it = data.iterator();
		
		Object obj = null;
		if(it.hasNext())
			obj = it.next();
		
		while(true)
		{
			
			//select the time slice to populate event list and joint prob list
			sliceList.setSelectedIndex(0);
			
			NetNode node = ((NetNode)((DefaultMutableTreeNode)obj).getUserObject());
			boolean isEvent = node.isEventNode();
			float prob = 0f;
			
			if(!isEvent && node.toString().toLowerCase().startsWith("adder for")) //is an adder
				continue;
			else if(!isEvent) //is a signal
			{
				isInhib = (((Mechanism)plan.getItem(node.getMechID())).getItemType() == Mechanism.INHIBIT)?true:false;
				Vector<Guid> sigs = new Vector<Guid>();
				sigs.add(node.getSignalID());
//				prob = plan.getLibrary().getElicitedValue(lastEventProc, sigs);
//				totalChainProb = totalChainProb * prob;
			}
			else //is an event
				lastEventProc = node.getProcessID();

//			if(!it.hasNext()) //is last event in the chain so get the timing prob
//			{
//				Event event = plan.getEventFromProcess(node.getProcessID()); //schedule has Integer(time), MaskedValue(prob)
//				prob = plan.getPredictedProbs(event.getGuid())[time];
//				totalChainProb = totalChainProb * prob;
//			}
			
			ExplainPathItem comp = new ExplainPathItem(node, prob, isEvent);
			comp.setPreferredSize(new Dimension(140, 50));
			layout.setConstraints(comp.getComponent(), constraints);
			chain.addItem(comp);
			
			
			JLabel lbl = new JLabel(new javax.swing.ImageIcon(this.getClass().getClassLoader().getResource("explainarw.png")));
			
			layout.setConstraints(lbl, constraints);
			if(it.hasNext())
				chain.add(lbl);
			else
				comp.setBackground(java.awt.Color.WHITE);
			
			//get next obj in chain
			if(!it.hasNext())
				break;
			obj = it.next();
			NetNode nextNode = null;((NetNode)((DefaultMutableTreeNode)obj).getUserObject());
			
			
			//select the first node (should be explain node) and the next node in the chain in event list to get the
			//joint prob for these to append to the total
			int x = 0;
			int x1 = node.getEventNames().indexOf(nextNode);
			eventNameList.setSelectedIndices(new int[]{x, x1}); 
			eventNameList.repaint();
			//get the value that should now be selected in joint probs
			Float val = (Float)(percentJoint.getSelectedValue());
			System.out.println(val);
			totalChainProb = totalChainProb * val.floatValue();
			
			//select other lines in the joint list, if number of selected in event list is more then 2 then its talkin bout grouping things
			//track these grouping numberz
			Iterator allJoints = null;//explainedItem.getWhyData().getPercentJointContriubtion(time).iterator();
			int index = 0;
			while(allJoints.hasNext())
			{
				Float thisVal = (Float)allJoints.next();
				percentJoint.setSelectedIndex(index++); //this isnt workin
				int[] selected = eventNameList.getSelectedIndices();
				if(selected.length > 2 && thisVal.floatValue() > 0) //this is some kind of group
				{
					//record grouping stuff here <<<<
					System.out.println("This chain occurs at  "+thisVal+"  when grouped with another chain.");
				}
			}
			
			//move the cause tree to the next node
			causeTree.setSelectionPath(new TreePath(((DefaultMutableTreeNode)obj).getPath()));
		}
		System.out.println("--- total single this chain:  "+totalChainProb);
		totalAllChains += totalChainProb;
		chain.setCauseProb(totalChainProb, isInhib);
		
		return chain;
	}
	
	public void actionPerformed(ActionEvent event)
	{
		if(event.getSource() == modeButton)
		{
			//new WhyDiegoWhy(explainedItem, timeSlice1);
			this.dispose();
		}
		else if(event.getSource() == goButton)
		{
			prog.setVisible(true);
			new Thread(this).start();
		}
		else if(event.getSource() == testButton)
		{
			Object[] vals = percentJoint.getSelectedValues();
			if(vals.length > 0)
			{
				System.out.println("perc. joint probs:  ");
				for(int x=0; x<vals.length; x++)
					System.out.print(vals[x]+", ");
			}
			else
				System.out.println("nothing selected in perc. joint probs");
			System.out.println();
		}
	}
	
	//used for threshold slider
	public void stateChanged(ChangeEvent event)
	{
		NetNode.setExplainThreshold(threshSlider.getValue()/100f);
		threshLbl.setText("Threshold  "+threshSlider.getValue());
	}

	private void recursiveNodeExplain(DefaultMutableTreeNode treeNodeToBeExplained)
	{
		if(treeNodeToBeExplained == null)return;
		causeTree.setSelectionPath(new TreePath(treeNodeToBeExplained.getPath()));// select the node in the cause tree
		NetNode nodeToBeExplained = (NetNode)treeNodeToBeExplained.getUserObject();
		Integer interestingSlice = ((Integer)(getSliceList().getSelectedValue()));
		
		if(interestingSlice == null){	//interestingSlice = time;
			interestingSlice = (Integer)(getSliceList().getModel().getElementAt(0));
		}
		
		Vector<NetNode> keyEvents = new Vector<NetNode>();
		if(interestingSlice != null){
			try{
				keyEvents = nodeToBeExplained.getRelevantCauses(interestingSlice.intValue());
			}catch(NullPointerException exc){   System.err.println("NPE in getRelevantCauses()  "+nodeToBeExplained.toString());   }
		}else{
			System.out.println("No slice (time) available for explanation.");
		}
		
		if(keyEvents.size() !=0)
		{
			int[] indicesForSelection = new int[keyEvents.size()];
			int indexIndex = 0;
			ListModel m = getEventNameList().getModel();
			int lim = m.getSize();
			for(NetNode n: keyEvents)
			{
				for(int j = 0; j < lim; j++)
				{
					if(m.getElementAt(j) == n)
						indicesForSelection[indexIndex++] = j;
				}
			}
			
			getEventNameList().setSelectedIndices(indicesForSelection);
			addSelectedCausesToCauseTree();
			prog.addToProgress(1);
			depthFirstExplanation(treeNodeToBeExplained);
		}else{
			System.out.println("No key events for " + nodeToBeExplained.toString());
		}
	}
	
	private void depthFirstExplanation(DefaultMutableTreeNode treeNodeToBeExplained)
	{
		if(treeNodeToBeExplained.getChildCount() > 0)
		{
			for(DefaultMutableTreeNode tn = (DefaultMutableTreeNode)treeNodeToBeExplained.getFirstChild(); tn != null; tn = (DefaultMutableTreeNode)treeNodeToBeExplained.getChildAfter(tn))
			{
				if(tn.isLeaf())
					recursiveNodeExplain(tn);
				else
					depthFirstExplanation(tn);
			}
		}
		else //this is an end-node with a treepath to be used to build an entire path
		{
			System.err.println(treeNodeToBeExplained);
			endTreeNodes.add(treeNodeToBeExplained);
			if(!endNodesForList.contains(treeNodeToBeExplained.toString())) //this list should not have duplicates - just for viewing
				endNodesForList.add(treeNodeToBeExplained.toString());
		}
		allExplainedNetNodes.add(treeNodeToBeExplained.getUserObject());
	}
	
	private void addSelectedCausesToCauseTree()
	{
		DefaultMutableTreeNode selectedNode = ((DefaultMutableTreeNode)((DefaultMutableTreeNode)causeTree.getLastSelectedPathComponent()));
		if(selectedNode != null){// the event to be explained is selected in the cause tree
			NetNode selectedEffect = (NetNode)selectedNode.getUserObject();
			if(selectedEffect != null){
				Object nns[] = eventNameList.getSelectedValues();// so find out what important causes for the effect have been selected in the EventNameList
				for(int j = 0; j < nns.length; j++){// cycle tthrough the user objects selected in the name list
					NetNode latestCausalEvent = (NetNode)nns[j];
					if(latestCausalEvent != selectedEffect){// it might be equal because of the way Event Names are dispalyed
						DefaultMutableTreeNode impliedSelectedNode = selectedNode;
						while(true){
							DefaultMutableTreeNode latestCausalNode = new DefaultMutableTreeNode(latestCausalEvent);
							impliedSelectedNode.add(latestCausalNode);
							latestCausalEvent.initializeExplanation(((NetNode)impliedSelectedNode.getUserObject()));								
							((DefaultTreeModel)causeTree.getModel()).reload();
							TreePath currentPath = new TreePath(impliedSelectedNode.getPath());
							causeTree.expandPath(currentPath);
							causeTree.scrollPathToVisible(currentPath);
							impliedSelectedNode = latestCausalNode;
							latestCausalEvent = latestCausalEvent.impliedDeeperParent();
							if(latestCausalEvent == null)break;
						};
					}								
				}
			}
		}
	}
	
	private NetNode displayLocalProbabilities(DefaultMutableTreeNode target){
		NetNode retVal = null;
		NetNode nn =  (NetNode)target.getUserObject();
		if(nn != explainedItem){
			explainedItem = nn;
			eventNameList.setListData(nn.getEventNames());
			eventNameList.updateUI();
			sliceList.setListData(nn.getSliceList());
			sliceList.updateUI();
		}
		return retVal;
	}	
	
	private JTree getCauseTree() {
		if (causeTree == null) {
			//final int tSlice = time;
			causeTree = new JTree();
//			causeTree.setPreferredSize(new Dimension(150,100));
			DefaultTreeModel dt = (DefaultTreeModel)causeTree.getModel();
			DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)dt.getRoot();
			rootNode.removeAllChildren();
			rootNode.setUserObject(explainedItem);
			causeTree.setEditable(true);
			causeTree.setShowsRootHandles(true);
			//causeTree.updateUI();
			causeTree.setSelectionRow(0);
			dt.reload();
			causeTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
					public void valueChanged(javax.swing.event.TreeSelectionEvent e) {
						DefaultMutableTreeNode selectedTreeNode = null;
						try {
							selectedTreeNode = ((DefaultMutableTreeNode)(e.getNewLeadSelectionPath().getLastPathComponent()));
						} catch (RuntimeException e1) {
							return;
						}
						NetNode nn = ((NetNode)(selectedTreeNode.getUserObject()));
						// start sampling the causes of this node
						if(nn.getWhyData() == null){
							// hopefully I am going to fix this so this case never occurs: if it is in the tree it has already been initialized
							nn.initializeExplanation(((NetNode)((DefaultMutableTreeNode)selectedTreeNode.getParent()).getUserObject()));
						}else{
							Integer selectedSlice = ((Integer)(getSliceList().getSelectedValue()));
							int slice = 0;
							if(selectedSlice != null){
								slice = selectedSlice.intValue();
							}
							int sampleSize = nn.getExplanationSampleSize(slice);
						}
						
						int selectedSlice = getSelectedSlice();
						displayLocalProbabilities(selectedTreeNode);
						setSelectedSlice(selectedSlice);
					}

				});
			
			
		}
		return causeTree;
	}
	
	private JList getEventNameList() {
		if (eventNameList == null) {
			eventNameList = new JList(new DefaultListModel());
//			eventNameList.setPreferredSize(new Dimension(150,100));
			eventNameList.addListSelectionListener(new javax.swing.event.ListSelectionListener() 
					{
						public void valueChanged(javax.swing.event.ListSelectionEvent e) {
							if(IndexManagement.isUserGeneratedEvent()){
								int selections[] = eventNameList.getSelectedIndices();
								int powerSetIndex = IndexManagement.getIndex(eventNameList.getSelectedIndices());
								IndexManagement.setUserGeneratedEvent(false);
								percentJoint.setSelectedIndex(powerSetIndex >> 1);
								IndexManagement.setUserGeneratedEvent(true);
							}
						}
					});
		}
		return eventNameList;
	}
	
	private int getSelectedSlice(){
		int retVal = -1;
		Integer selectedValue = ((Integer)getSliceList().getSelectedValue());
		if(selectedValue != null){
			retVal = selectedValue.intValue();
		}
		return retVal;
	}
	
	private JList getSliceList() {
		if (sliceList == null) {
			sliceList = new JList();
//			sliceList.setPreferredSize(new Dimension(150,100));
			sliceList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
			sliceList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
						public void valueChanged(javax.swing.event.ListSelectionEvent e) {
							int slice = 0;
							Integer iSlice = ((Integer)sliceList.getSelectedValue());
							if(iSlice != null){
								int dSlice = iSlice.intValue();
								slice = ((Integer)sliceList.getSelectedValue()).intValue();
								percentJoint.setListData(explainedItem.getWhyData().getPercentJointContriubtion(slice));
							}else{
								percentJoint.setListData(new Vector(0));
							}
							eventNameList.setSelectedIndices(new int[0]);
						}
					});
		}
		return sliceList;
	}
	
	private void setSelectedSlice(int selectedSlice) 
	{
		if(selectedSlice != -1){
			JList sliceList = getSliceList();
			ListModel sliceModel = sliceList.getModel();
			int lim = sliceList.getModel().getSize();
			for(int j = 0; j < lim; j++){
				if(selectedSlice == ((Integer)sliceModel.getElementAt(j)).intValue()){
					sliceList.setSelectedIndex(j);
					break;
				}
			}
		}
		
	}
	
	private JList getPercentJoint() {
		if (percentJoint == null) {
			percentJoint = new JList();
//			percentJoint.setPreferredSize(new Dimension(150,100));
			percentJoint.addListSelectionListener(new javax.swing.event.ListSelectionListener(){
						public void valueChanged(javax.swing.event.ListSelectionEvent e) {
							if(IndexManagement.isUserGeneratedEvent()){
								IndexManagement.setUserGeneratedEvent(false);
								int powerSetIndex = ((percentJoint.getSelectedIndex()) << 1) + 1;
								eventNameList.setSelectedIndices(IndexManagement.getSubset(powerSetIndex));
//								margProbList.setSelectedIndex(powerSetIndex);
//								jointProbList.setSelectedIndex(powerSetIndex);
								IndexManagement.setUserGeneratedEvent(true);
							}
						}
					});
		}
		return percentJoint;
	}

		
	public void run()
	{
		//run johns explaination functions, slightly modified
		
		prog.setGroupName("Analyzing event at time "+timeSlice1);
		chainTime1.clear();
		midPanel.removeAll();
		
		if(explainedItem != null)
		{
			if(explainedItem.getWhyData() == null)
			{
				explainedItem.initializeExplanation(timeSlice1);
				System.err.println("WHYDATA WAS INITIALIZED!   "+ explainedItem.getSliceList().size());
			}
			
			if(explainedItem.getWhyData() != null)
			{
				explainedItem.addExplanationTime(timeSlice1); 
				TreeModel model = getCauseTree().getModel();
				sliceList.setListData(explainedItem.getSliceList());
				getEventNameList().setListData(explainedItem.getEventNames());
				DefaultMutableTreeNode theRoot = (DefaultMutableTreeNode)model.getRoot();
				theRoot.setUserObject(explainedItem);
				getCauseTree().setSelectionRow(0);
			}
		}
		
		recursiveNodeExplain((DefaultMutableTreeNode)(getCauseTree().getModel().getRoot()));
				
		////////// parse the created explain cause tree into cause chain panels /////////////
		Iterator endNodesIt = endTreeNodes.iterator();
		while(endNodesIt.hasNext())
		{
			TreeNode[] thisPath = ((DefaultMutableTreeNode)endNodesIt.next()).getPath();
			ExplainChain chain = explainChainFromTreePath(thisPath, timeSlice1);
			
			chainTime1.add(chain);
		}
		
		java.util.Collections.sort(chainTime1);
		Iterator ch1 = chainTime1.iterator();
		if(timeSlice1 == timeSlice2) //is a single slice explaination
			while(ch1.hasNext())
				midPanel.add(((ExplainChain)ch1.next()).getPanel());
		
		//////////					/////////////				////////////
		
		System.err.println("-------------------------------------------------------------------");
		
		//do the second timeslice if there is a time comparison going on
		Vector diffOfChains = null;
		if(timeSlice1 != timeSlice2)
		{
//			explainedItem = ((BayesNet)plan.getBayesNet()).getEvent(explainedItemID);
//			
//			chainTime2.removeAllElements();
//			endTreeNodes.removeAllElements();
//			causeTree = null;
//			explainedItem.resetExplaination();
			resetExplain();
			chainTime2.clear();
			
			prog.setProgress(0);
			prog.setGroupName("Analyzing event at time "+timeSlice2);
			
			if(explainedItem != null)
			{
				if(explainedItem.getWhyData() == null)
					explainedItem.initializeExplanation(timeSlice2);
				
				if(explainedItem.getWhyData() != null)
				{
					explainedItem.addExplanationTime(timeSlice2); 
					TreeModel model = getCauseTree().getModel();
					sliceList.setListData(explainedItem.getSliceList());
					getEventNameList().setListData(explainedItem.getEventNames());
					DefaultMutableTreeNode theRoot = (DefaultMutableTreeNode)model.getRoot();
					theRoot.setUserObject(explainedItem);
					getCauseTree().setSelectionRow(0);
				}
			}
			
			recursiveNodeExplain((DefaultMutableTreeNode)(getCauseTree().getModel().getRoot()));
					
			////////// parses the created tree into cause chain panels /////////////
			endNodesIt = endTreeNodes.iterator();
			while(endNodesIt.hasNext())
			{
				TreeNode[] thisPath = ((DefaultMutableTreeNode)endNodesIt.next()).getPath();
				ExplainChain chain = explainChainFromTreePath(thisPath, timeSlice2);
				
				
				chainTime2.add(chain);
			}
			
//			mainPanel.revalidate();
//			this.repaint();
			//////////					/////////////				////////////
		
			
			//compare both chains and add only what changed
			diffOfChains = new Vector();
			
			System.out.println("Comparing chains---------");
			ch1 = chainTime1.iterator();
			while(ch1.hasNext())
			{
				ExplainChain thisChain = (ExplainChain)ch1.next();
				if(!chainTime2.contains(thisChain)) //add what went away at time2 that was in time1
				{
					System.out.println("this chain from 1 is NOT in 2");
					thisChain.grayOutItems();
					diffOfChains.add(thisChain);
				}
				else //if it was in both remove it from time2chain, so that we're left with only what is new at time 2
				{
					System.out.println("this chain from 1 IS in 2");
					// now compare the prob of thisChain in chainTime1 and chainTime2 to properly color it
					// to indicate either that its still exists but the value changed
					if(!(((ExplainChain)chainTime2.get(chainTime2.indexOf(thisChain))).compareChainProb(thisChain)))
						((ExplainChain)chainTime2.get(chainTime2.indexOf(thisChain))).darkOutItems();
					else
						chainTime2.remove(thisChain);	//remove cause the remaining in chain 2 will be added later
				}
			}
			System.out.println("Done Comparing chains---------");
			
			Iterator ch2 = chainTime2.iterator();
			while(ch2.hasNext()) //add what is left in time2chain, these are new chains for time2 (werent in time1)
				diffOfChains.add(ch2.next());
			
			
			//finally add these compared chains to the gui
			java.util.Collections.sort(diffOfChains); //sort the chains (based on their prob)
			Iterator diffs = diffOfChains.iterator();
			while(diffs.hasNext())
			{
				ExplainChain thisChain = (ExplainChain)diffs.next();
				System.out.println(((ExplainPathItem)thisChain.getItems().lastElement()).getUserObject());
				
				midPanel.add(thisChain.getPanel());
			}
		}
		
		System.out.println("totalAllChains:  "+totalAllChains);
		float chainProbFactor = 1f/totalAllChains;
		System.out.println("chainProbFactor:  "+chainProbFactor);
		
		//update all chain probs using chainProbFactor to adjust based outta 100%
//		Iterator chains = null;
//		if(timeSlice1 != timeSlice2) //comparison was done
//			chains = diffOfChains.iterator();
//		else
//			chains = chainTime1.iterator();
//		
//		while(chains.hasNext())
//		{
//			ExplainChain thisChain = (ExplainChain)chains.next();
//			thisChain.setCauseProb(thisChain.getCauseProb()*chainProbFactor, thisChain.isInhibitor());
//		}
		
		prog.complete();
		mainPanel.revalidate();
		this.repaint();
		
		//System.out.println("diff: "+diffOfChains.size());
		
		resetExplain(); //clear out the explain data in netnodes for next time  *************
	}

	private void resetExplain()
	{
		//go through all endNodesForList(no duplicates) of netnodes and and reset their 'whyData'
		Iterator usedNodes = allExplainedNetNodes.iterator();
		while(usedNodes.hasNext())
		{
			NetNode nn = (NetNode)(usedNodes.next());
			nn.resetExplaination();
		}
		
		//explainedItem = ((BayesNet)plan.getBayesNet()).getEvent(explainedItemID);
		explainedItem.resetExplaination();
		allExplainedNetNodes.clear();
		endTreeNodes.clear();
		endNodesForList.clear();
		causeTree = null;
		//midPanel.removeAll();
		//chainTime1.clear();
		//chainTime2.clear();
		totalAllChains = 0;
		prog.setProgress(0);
	}

	
}*/
