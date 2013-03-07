/**
 * 
 */
package mil.af.rl.jcat.gui.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;


import javax.swing.JDialog;
import javax.swing.JPanel;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.swing.JList;

import mil.af.rl.jcat.bayesnet.*;
import mil.af.rl.jcat.bayesnet.explaination.ExplanationData;
import mil.af.rl.jcat.util.IndexManagement;
import mil.af.rl.jcat.util.SortedMultiMap;

import mil.af.rl.jcat.plan.AbstractPlan;
import mil.af.rl.jcat.util.Guid;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.BorderFactory;
import javax.swing.JTree;
import javax.swing.JButton;
import javax.swing.ListModel;

import org.apache.log4j.Logger;

/**
 * @author John Lemmer
 *
 */
public class WhyDiegoWhy extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel contentPanel = null;
	private JList eventNameList = null;
	private JList sliceList = null;
	private JList jointProbList = null;
	private NetNode targetEventnode = null;
	private JScrollPane marginalProbs = null;
	private JList margProbList = null;
	private JScrollPane jointProbsScrollPane = null;
	private JScrollPane percentJointScrollPane = null;
	private JList jList = null;  //  @jve:decl-index=0:visual-constraint="1409,309"
	private JList percentJoint = null;
	private JScrollPane causeTreeScrollPane = null;
	private JTree causeTree = null;
	private JButton addCausesButton = null;
	private JPanel controlsPanel = null;
	private JButton autoExplainButton = null;
	private JButton extractActionButton = null;
	private JScrollPane keyActionsScrollPane = null;
	private JList keyActionsList = null;
	private BayesNet bayesNet;
	private static Logger logger = Logger.getLogger(WhyDiegoWhy.class);
	
	/**
	 * This method initializes 
	 * 
	 */
	public WhyDiegoWhy() {
		super();
		initialize();
	}
	
	public WhyDiegoWhy(Guid planid, AbstractPlan plan, int time)
	{
		super();
		initialize();
		bayesNet = plan.getBayesNet();
		List<NetNode> explainNodes = bayesNet.getExplainNodes(planid);
		this.targetEventnode = explainNodes.get(0);		
		if(bayesNet.getWhyData(planid) == null){
			bayesNet.initializeExplanation(planid, time);
		}
		if(bayesNet.getWhyData(planid) != null){
			bayesNet.getWhyData(planid).addTime(time, targetEventnode.getCauses().size(), targetEventnode.getInhibitors().size()); // time appears to be added twice if node had explanation initialized also
			eventNameList.setListData(explainNodes.toArray());
			sliceList.setListData(bayesNet.getWhyData(planid).getSliceList().toArray());
			TreeModel model = causeTree.getModel();
			DefaultMutableTreeNode theRoot = (DefaultMutableTreeNode)model.getRoot();
			theRoot.setUserObject(explainNodes.get(0));
			causeTree.setSelectionRow(0);
		}
		this.setVisible(true);
	}
	
	public WhyDiegoWhy(Vector events, Vector probs){
		super();
		initialize();
		eventNameList.setListData(events);
		sliceList.setListData(probs);
		
	}
	
	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
        this.setBounds(new java.awt.Rectangle(0,0,1000,400));
        this.setContentPane(getContentPanel());
        this.setTitle("Why");
        this.setPreferredSize(new java.awt.Dimension(400,200));
        this.setName("Why");
			
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {

	}

	/**
	 * This method initializes contentPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getContentPanel() {
		if (contentPanel == null) {
			GridLayout gridLayout = new GridLayout();
			gridLayout.setRows(3);
			gridLayout.setColumns(3);
			gridLayout.setVgap(10);
			gridLayout.setHgap(10);
			contentPanel = new JPanel();
			contentPanel.setLayout(gridLayout);
			contentPanel.add(getEventNameList(), null);
			contentPanel.add(getJointProbsScrollPane(), null);
			contentPanel.add(getSliceList(), null);
			contentPanel.add(getPercentJointScrollPane(), null);
			contentPanel.add(getMarginalProbs(), null);
			contentPanel.add(getCauseTreeScrollPane(), null);
			contentPanel.add(getControlsPanel(), null);
			contentPanel.add(this.getKeyActionsScrollPane(), null);
			
		}
		return contentPanel;
	}

	/**
	 * This method initializes eventNameList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getEventNameList() {
		if (eventNameList == null) {
			TitledBorder titledBorder = BorderFactory.createTitledBorder(null, "Event Names (low order bit first)", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null);
			titledBorder.setTitleFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12));
			titledBorder.setTitleColor(new java.awt.Color(51,153,255));
			eventNameList = new JList();
			eventNameList.setBorder(titledBorder);
			eventNameList
					.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
						public void valueChanged(javax.swing.event.ListSelectionEvent e) {
							if(IndexManagement.isUserGeneratedEvent()){
								int selections[] = eventNameList.getSelectedIndices();
								int powerSetIndex = IndexManagement.getIndex(eventNameList.getSelectedIndices());
								IndexManagement.setUserGeneratedEvent(false);
								margProbList.setSelectedIndex(powerSetIndex);
								percentJoint.setSelectedIndex(powerSetIndex >> 1);
								jointProbList.setSelectedIndex(powerSetIndex);
								IndexManagement.setUserGeneratedEvent(true);
							}
						}
					});
		}
		return eventNameList;
	}

	/**
	 * This method initializes jointProbList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getSliceList() {
		if (sliceList == null) {
			sliceList = new JList();
			sliceList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
			sliceList.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Times at which Explanations are available", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12), new java.awt.Color(51,51,51)));
			sliceList
					.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
						public void valueChanged(javax.swing.event.ListSelectionEvent e) {
							//System.out.println("valueChanged(), sliceList"); // 
							int slice = 0;
							Integer iSlice = ((Integer)sliceList.getSelectedValue());
							if(iSlice != null){
								int dSlice = iSlice.intValue();
								slice = ((Integer)sliceList.getSelectedValue()).intValue();
								ExplanationData data = bayesNet.getWhyData(targetEventnode.getPlanID());
								jointProbList.setListData(data.getJointProbs(slice).toArray());
								margProbList.setListData(data.getMargProbs(slice).toArray());
								percentJoint.setListData(data.getPercentJointContriubtion(slice).toArray());
							}else{
								jointProbList.setListData(new Vector(0));
								margProbList.setListData(new Vector(0));
								percentJoint.setListData(new Vector(0));
							}
							eventNameList.setSelectedIndices(new int[0]);
//							System.out.println(slice);
						}
					});
		}
		return sliceList;
	}
	public void loadEventNames(Vector names){
		eventNameList.setListData(names);

	}

	/**
	 * This method initializes jointProbList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getJointProbList() {
		if (jointProbList == null) {
			jointProbList = new JList();
			jointProbList.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,0,0,0));
			jointProbList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
			jointProbList
					.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
						public void valueChanged(javax.swing.event.ListSelectionEvent e) {
/*//							int activeBitCount = 0;
//							for(int j = 0; j < 16; j++){
//								if(((jointSelectionIndex >> j) & 1) == 1){
//									activeBitCount += 1;
//								}
//							}
//							int activeBits[] = new int[activeBitCount];
//							int newSelectIndex = 0;
//							for(int j = 0; j < 16; j++){
//								if(((jointSelectionIndex >> j) & 1) == 1){
//									activeBits[newSelectIndex++] = j;
//								}
//							}
////							eventNameList.setSelectedIndices(activeBits);
*/
							if(IndexManagement.isUserGeneratedEvent()){
								IndexManagement.setUserGeneratedEvent(false);
								int powerSetIndex = jointProbList.getSelectedIndex();
								eventNameList.setSelectedIndices(IndexManagement.getSubset(powerSetIndex));
								eventNameList.ensureIndexIsVisible(eventNameList.getSelectedIndex());
								margProbList.setSelectedIndex(powerSetIndex);
								margProbList.ensureIndexIsVisible(powerSetIndex);
								percentJoint.setSelectedIndex(powerSetIndex >> 1);
								percentJoint.ensureIndexIsVisible(powerSetIndex >> 1);
								IndexManagement.setUserGeneratedEvent(true);
							}
						}
					});
		}
		return jointProbList;
	}

	/**
	 * This method initializes marginalProbs	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getMarginalProbs() {
		if (marginalProbs == null) {
			marginalProbs = new JScrollPane();
			marginalProbs.setViewportBorder(javax.swing.BorderFactory.createTitledBorder(null, "Marginal Probabilities", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
			marginalProbs.setAutoscrolls(true);
			marginalProbs.setViewportView(getMargProbList());
			marginalProbs.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		}
		return marginalProbs;
	}

	/**
	 * This method initializes margProbList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getMargProbList() {
		if (margProbList == null) {
			margProbList = new JList();
			margProbList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
			margProbList
					.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
						public void valueChanged(javax.swing.event.ListSelectionEvent e) {
							if(IndexManagement.isUserGeneratedEvent()){
								IndexManagement.setUserGeneratedEvent(false);
								int powerSetIndex = margProbList.getSelectedIndex();
								eventNameList.setSelectedIndices(IndexManagement.getSubset(powerSetIndex));
								percentJoint.setSelectedIndex(powerSetIndex >> 1);
								jointProbList.setSelectedIndex(powerSetIndex);
								IndexManagement.setUserGeneratedEvent(true);
							}
						}
					});
		}
		return margProbList;
	}

	/**
	 * This method initializes jointProbsScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJointProbsScrollPane() {
		if (jointProbsScrollPane == null) {
			jointProbsScrollPane = new JScrollPane();
			jointProbsScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			jointProbsScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Joint Probabilities", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
			jointProbsScrollPane.setAutoscrolls(true);
			jointProbsScrollPane.setViewportView(getJointProbList());
		}
		return jointProbsScrollPane;
	}
	

	private JScrollPane getKeyActionsScrollPane() {
		if (keyActionsScrollPane == null) {
			keyActionsScrollPane = new JScrollPane();
			keyActionsScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			keyActionsScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Important Actions", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
			keyActionsScrollPane.setAutoscrolls(true);
			keyActionsScrollPane.setViewportView(getKeyActionsList());
		}
		return keyActionsScrollPane;
	}
	private JList getKeyActionsList(){
		if (keyActionsList == null) {
			keyActionsList = new JList();
			keyActionsList.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,0,0,0));
			keyActionsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
			keyActionsList.setListData(new Vector<NetNode>());
			keyActionsList
					.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
						public void valueChanged(javax.swing.event.ListSelectionEvent e) {
							for(int j = causeTree.getRowCount() - 1; j >= 0; j--){
								causeTree.collapseRow(j);
							}
							causeTree.setSelectionPath(new TreePath(((NetNodeExplanationRelation)keyActionsList.getSelectedValue()).getCauseTreeNode().getPath()));
							
						}
					}
				);
			}
		return keyActionsList;
	}

	/**
	 * This method initializes percentJointScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getPercentJointScrollPane() {
		if (percentJointScrollPane == null) {
			percentJointScrollPane = new JScrollPane();
			percentJointScrollPane.setViewportBorder(javax.swing.BorderFactory.createTitledBorder(null, "Percent Contribution, Joint", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
			percentJointScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			percentJointScrollPane.setViewportView(getPercentJoint());
		}
		return percentJointScrollPane;
	}

	/**
	 * This method initializes jList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getJList() {
		if (jList == null) {
			jList = new JList();
		}
		return jList;
	}

	/**
	 * This method initializes percentJoint	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getPercentJoint() {
		if (percentJoint == null) {
			percentJoint = new JList();
			percentJoint
					.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
						public void valueChanged(javax.swing.event.ListSelectionEvent e) {
							if(IndexManagement.isUserGeneratedEvent()){
								IndexManagement.setUserGeneratedEvent(false);
								int powerSetIndex = ((percentJoint.getSelectedIndex()) << 1) + 1;
								eventNameList.setSelectedIndices(IndexManagement.getSubset(powerSetIndex));
								margProbList.setSelectedIndex(powerSetIndex);
								jointProbList.setSelectedIndex(powerSetIndex);
								IndexManagement.setUserGeneratedEvent(true);
							}
						}
					});
		}
		return percentJoint;
	}

	/**
	 * This method initializes causeTreeScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getCauseTreeScrollPane() {
		if (causeTreeScrollPane == null) {
			causeTreeScrollPane = new JScrollPane();
			causeTreeScrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			causeTreeScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			causeTreeScrollPane.setViewportBorder(javax.swing.BorderFactory.createTitledBorder(null, "Cause Tree", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
			causeTreeScrollPane.setViewportView(getCauseTree());
		}
		return causeTreeScrollPane;
	}
	
	private NetNode displayLocalProbabilities(DefaultMutableTreeNode target){
		NetNode retVal = null;
		NetNode nn =  (NetNode)target.getUserObject();
		if(nn != targetEventnode){
			targetEventnode = nn;
			eventNameList.setListData(bayesNet.getExplainNodes(targetEventnode.getPlanID()).toArray());
			eventNameList.updateUI();
			sliceList.setListData(bayesNet.getWhyData(targetEventnode.getPlanID()).getSliceList().toArray());
			sliceList.updateUI();
		}
		return retVal;
	}

	/**
	 * This method initializes causeTree	
	 * 	
	 * @return javax.swing.JTree	
	 */
	private JTree getCauseTree() {
		if (causeTree == null) {
			causeTree = new JTree();
			DefaultTreeModel dt = (DefaultTreeModel)causeTree.getModel();
			DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)dt.getRoot();
			rootNode.removeAllChildren();
			rootNode.setUserObject(targetEventnode);
			causeTree.setEditable(true);
			causeTree.setShowsRootHandles(true);
			//causeTree.updateUI();
			causeTree.setSelectionRow(0);
			dt.reload();
			causeTree
			.addTreeExpansionListener(new javax.swing.event.TreeExpansionListener() {
				public void treeExpanded(javax.swing.event.TreeExpansionEvent e) {
				}
				public void treeCollapsed(javax.swing.event.TreeExpansionEvent e) {
				}
			});
			causeTree
				.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
					public void valueChanged(javax.swing.event.TreeSelectionEvent e) {
						DefaultMutableTreeNode selectedTreeNode = null;
						try {
							selectedTreeNode = ((DefaultMutableTreeNode)(e.getNewLeadSelectionPath().getLastPathComponent()));
						} catch (RuntimeException e1) {
							return;
						}
						NetNode nn = ((NetNode)(selectedTreeNode.getUserObject()));
						// start sampling the causes of this node
						if(bayesNet.getWhyData(nn.getPlanID()) == null){
							// hopefully I am going to fix this so this case never occurs: if it is in the tree it has already been initialized
							bayesNet.initializeParentExplaination(nn.getPlanID(), ((NetNode)((DefaultMutableTreeNode)selectedTreeNode.getParent()).getUserObject()).getPlanID());
						}else{
							Integer selectedSlice = ((Integer)(sliceList.getSelectedValue()));
							int slice = 0;
							if(selectedSlice != null){
								slice = selectedSlice.intValue();
							}
							int sampleSize = bayesNet.getWhyData(nn.getPlanID()).getCount(slice);
							//System.out.println("valueChanged(): Selected event sample count = : " + sampleSize + ", event = " + nn.toString()); // 
						}
						int selectedSlice = getSelectedSlice();// this is NOT an index: it is the actual value that has been selected!
						displayLocalProbabilities(selectedTreeNode);
						setSelectedSlice(selectedSlice);
						//System.out.println("valueChanged(): Cause Tree selection changed: new selection = " + nn.toString()); // */
					}

				});
			causeTree.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mousePressed(java.awt.event.MouseEvent e) {
//					System.out.println("mousePressed()"); // 
					super.mousePressed(e);
					if(e.getButton() == MouseEvent.BUTTON3){
						DefaultMutableTreeNode selectedNode = null;
						NetNode selectedCause = null;
						try {
							selectedNode = ((DefaultMutableTreeNode)((JTree)e.getSource()).getLastSelectedPathComponent());
							selectedCause = ((NetNode)(selectedNode).getUserObject());
						} catch (RuntimeException e1) {
							return;
						}
						int kidCount = selectedNode.getChildCount();
						if( kidCount > 0){
							NetNode kids[] = new NetNode[kidCount];
							Enumeration kidEnum = selectedNode.children();
							for(int j = 0; kidEnum.hasMoreElements(); j++){
								kids[j] = ((NetNode)(((DefaultMutableTreeNode)kidEnum.nextElement()).getUserObject()));
							}
							int selectionIndex = getIndexFor(selectedCause, kids);
							jointProbList.setSelectedIndex(selectionIndex);
							jointProbList.ensureIndexIsVisible(selectionIndex);
//							System.out.println(selectedCause.getCount(r) + " samples for " + selectedCause.toString() + " right now.");
						}
					}
				}
			});
		}
		return causeTree;
	}
	
	public int getIndexFor(NetNode target, NetNode[] kids) {
		int retVal = 1; // we want the case where the event itself occurs
		for(int j = 0; j < kids.length; j++){
			int k = 0;
			for(; k < target.getCauses().size(); k++){
				if(kids[j] == target.getCauses().get(k)){
					retVal |= (1 << (k + 1)); // plus one for the node itself
					break;
				}
			}
			if(k >= target.getCauses().size() ){// we didn't find it among the causes
				for(k= 0; k < target.getInhibitors().size(); k++){
					if(kids[j] == target.getInhibitors().get(k)){
						retVal |= (1 << (target.getCauses().size() + k + 1));
						break;
					}
				}
				if(k >= target.getInhibitors().size()){
					logger.warn("getIndexFor - We didn't find at least one of the kid nodes");
				}
			}
		}
		return retVal;
	}

	/**
	 * This method initializes addCausesButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getAddCausesButton() {
		if (addCausesButton == null) {
			addCausesButton = new JButton();
			addCausesButton.setText("Add Selected Events");
			addCausesButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
			addCausesButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					addSelectedCausesToCauseTree();
				}
			});
		}
		return addCausesButton;
	}

	private JButton getExtractActionsButton() {
		if (extractActionButton == null) {
			extractActionButton = new JButton();
			extractActionButton.setText("Extract Actions");
			extractActionButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
			extractActionButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					findAndLoadImportantActions();
				}
			});
		}
		return extractActionButton;
	}
	
	private class NetNodeExplanationRelation{
		NetNode  event = null;
		DefaultMutableTreeNode causeTreeNode =null;
		
		public NetNodeExplanationRelation(NetNode nn, DefaultMutableTreeNode tn){
			event = nn;
			causeTreeNode = tn;
		}
		public String toString(){
			return event.toString();
		}
		public DefaultMutableTreeNode getCauseTreeNode() {
			return causeTreeNode;
		}
		public void setCauseTreeNode(DefaultMutableTreeNode causeTreeNode) {
			this.causeTreeNode = causeTreeNode;
		}
		public NetNode getEvent() {
			return event;
		}
		public void setEvent(NetNode event) {
			this.event = event;
		}
		
	}
	
	private void findAndLoadImportantActions(){
//		traverse the cause: if 'user object' is in the cause tree it is considered important because either the user or 'explain' algoritms put it in there.
		Vector<NetNodeExplanationRelation> bigGuys = new Vector<NetNodeExplanationRelation>();
		findAndLoadActions(((DefaultMutableTreeNode)(getCauseTree().getModel().getRoot())), bigGuys);
		getKeyActionsList().setListData(bigGuys);
	}
	
	private void findAndLoadActions(DefaultMutableTreeNode tn, Vector<NetNodeExplanationRelation> bigGuys){
		NetNode nn = (NetNode)tn.getUserObject();
		if(nn.getType()  == NodeType.Process){
			bigGuys.add(new NetNodeExplanationRelation(nn, tn));
		}
		int lim = tn.getChildCount();
		for(int j = 0; j < lim; j++){
			findAndLoadActions((DefaultMutableTreeNode)(tn.getChildAt(j)), bigGuys);
		}
	}
 
	private void addSelectedCausesToCauseTree(){
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
							bayesNet.initializeParentExplaination(latestCausalEvent.getPlanID(), ((NetNode)impliedSelectedNode.getUserObject()).getPlanID());								
							((DefaultTreeModel)causeTree.getModel()).reload();
							TreePath currentPath = new TreePath(impliedSelectedNode.getPath());
							causeTree.expandPath(currentPath);
							causeTree.scrollPathToVisible(currentPath);
							impliedSelectedNode = latestCausalNode;
							//If we have a signal traverse to the source of the signal at position 0
							if(latestCausalEvent.getType() == NodeType.Signal && latestCausalEvent.getCauses().size() > 0)
								latestCausalEvent = latestCausalEvent.getCauses().get(0); 
							else
								break;
						};
					}								
				}
			}
		}
//		System.out.println("actionPerformed(): 'Add Causes' Button pressed. "); 
	}
	private int getSelectedSlice(){
		int retVal = -1;
		Integer selectedValue = ((Integer)getSliceList().getSelectedValue());
		if(selectedValue != null){
			retVal = selectedValue.intValue();
		}
		return retVal;
	}
	private void setSelectedSlice(int selectedSlice) {
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

	/**
	 * This method initializes controlsPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getControlsPanel() {
		if (controlsPanel == null) {
			controlsPanel = new JPanel();
			GridLayout gridLayout = new GridLayout();
			gridLayout.setRows(2);
			gridLayout.setColumns(1);
			gridLayout.setVgap(10);
			gridLayout.setHgap(10);
			controlsPanel.setLayout(gridLayout);
			controlsPanel.add(getAddCausesButton(), null);
			controlsPanel.add(getAutoExplainButton(), null);
			controlsPanel.add(getExtractActionsButton(), null);
		}
		return controlsPanel;
	}

	/**
	 * This method initializes autoExplainButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getAutoExplainButton() {
		if (autoExplainButton == null) {
			autoExplainButton = new JButton();
			autoExplainButton.setText("Auto Explain");
			autoExplainButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					// get (single) selection in causeTree and clear out children
					// search percent contribution for cause that explain x% of the selected events current probability
					// select one the explanatory <event? signals?>
					recursiveNodeExplain((DefaultMutableTreeNode)(getCauseTree().getSelectionPath().getLastPathComponent()));
//					System.out.println("actionPerformed(): Auto Explain Button clicked"); // 
				}
			});
		}
		return autoExplainButton;
	}

	private void recursiveNodeExplain(DefaultMutableTreeNode treeNodeToBeExplained) {
		if(treeNodeToBeExplained == null)return;
		getCauseTree().setSelectionPath(new TreePath(treeNodeToBeExplained.getPath()));// select the node in the cause tree
		NetNode nodeToBeExplained = (NetNode)treeNodeToBeExplained.getUserObject();
		Integer interestingSlice = ((Integer)(getSliceList().getSelectedValue()));
		if(interestingSlice == null){
			interestingSlice = (Integer)(getSliceList().getModel().getElementAt(0));
//			System.out.println("Defaulted to analysis of slice " + interestingSlice);
		}
		
		List<NetNode> keyEvents = null;
		if(interestingSlice != null){
			keyEvents = this.getRelevantCauses(nodeToBeExplained.getPlanID(), interestingSlice.intValue());
		}else{
//			System.out.println("No slice (time) available for explanation.");
		}
		if(keyEvents != null && keyEvents.size() !=0){
			int[] indicesForSelection = new int[keyEvents.size()];
			int indexIndex = 0;
			ListModel m = getEventNameList().getModel();
			int lim = m.getSize();
			for(NetNode n: keyEvents){
				for(int j = 0; j < lim; j++){
					if(m.getElementAt(j) == n){
						indicesForSelection[indexIndex++] = j;
					}
				}
			}
			getEventNameList().setSelectedIndices(indicesForSelection);
			addSelectedCausesToCauseTree();
			depthFirstExplanation(treeNodeToBeExplained);
		}else{
//			System.out.println("No key events for " + nodeToBeExplained.toString());
		}
	}
	
	public List<NetNode> getRelevantCauses(Guid planid, int slice) {
		
		ArrayList<Float> jointProbs = bayesNet.getWhyData(planid).getJointProbs(slice);
		SortedMultiMap<Float, Integer> ranks = new SortedMultiMap<Float, Integer>();
		double totalCausalProb = 0.0;
		
		for(int j = 1; j < jointProbs.size(); j += 2){
			totalCausalProb += jointProbs.get(j);
			ranks.insertDescending(jointProbs.get(j), new Integer(j));
		}

		double percentExp = totalCausalProb * ExplanationData.explainThreshold; // eventually this .8 shoulde be a user set value
		
		float accountedForProb = 0;
		int compoundIndex = 0;
		int lim = ranks.size();
		
		for(int k = 0; ((k < lim) && (accountedForProb < percentExp)); k++){
			accountedForProb += ranks.getKeyAt(k);
			compoundIndex |= ranks.getValueAt(k);
		}
		
		List<NetNode> allCauses = bayesNet.getExplainNodes(planid); // including the event itself and inhibitors
		lim = allCauses.size();
		List<NetNode> rtnVal = new ArrayList<NetNode>();
		
		for(int j = 0; j < lim; j++) { // seems like its ok if we leave the event it self. That is why j = 0 and not j = 1
			if((compoundIndex & (1 << j)) != 0){
				rtnVal.add(allCauses.get(j));
			}
		}
		
		return rtnVal;
	}


	private void depthFirstExplanation(DefaultMutableTreeNode treeNodeToBeExplained) {
		if(treeNodeToBeExplained.getChildCount() > 0){
			for(DefaultMutableTreeNode tn = (DefaultMutableTreeNode)treeNodeToBeExplained.getFirstChild(); tn != null; tn = (DefaultMutableTreeNode)treeNodeToBeExplained.getChildAfter(tn)){
				if(tn.isLeaf()){
					recursiveNodeExplain(tn);
				}else{
					depthFirstExplanation(tn);
				}				
			}
		}
	}
}
