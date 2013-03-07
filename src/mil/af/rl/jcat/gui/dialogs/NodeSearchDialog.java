package mil.af.rl.jcat.gui.dialogs;

import java.util.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;

import org.apache.log4j.Logger;


import mil.af.rl.jcat.gui.*;
import mil.af.rl.jcat.plan.AbstractPlan;
import mil.af.rl.jcat.util.*;

import com.c3i.jwb.*;
import mil.af.rl.jcat.control.Control;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author MPG
 * @version 1.0
 */
public class NodeSearchDialog extends JDialog implements WindowListener, ActionListener, TreeSelectionListener
{

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(NodeSearchDialog.class);
	MainFrm frame = null;

	JScrollPane ScrollPane = new JScrollPane();

	JLabel lblQuestion = new JLabel();
	JTextArea txtInput = new JTextArea();
	JButton btnSearch = new JButton();
	JButton btnClose = new JButton();

	JCheckBox cbxNodeText = new JCheckBox();
	JCheckBox cbxKeyword = new JCheckBox();
	JCheckBox cbxCaseSens = new JCheckBox();

	JPanel buttonPanel = new JPanel();

	DefaultMutableTreeNode defTree = new DefaultMutableTreeNode();
	JTree resultTree = new JTree(defTree);
	GridBagLayout gridBagLayout1 = new GridBagLayout();

	public NodeSearchDialog()
	{
		super(MainFrm.getInstance());
		frame = MainFrm.getInstance();

		try
		{
			init();
			setSize(320, 350);
			setLocationRelativeTo(MainFrm.getInstance());

			javax.help.CSH.setHelpIDString(this, "Node_Search_Menu");
		}
		catch (Exception e)
		{
			logger.error("Constructor - Error initializing dialog", e);
		}

	}

	private void init() throws Exception
	{
		ScrollPane.setViewportView(resultTree);
		ScrollPane.setBorder(BorderFactory.createLoweredBevelBorder());

		this.addWindowListener(this);

		this.getContentPane().setLayout(gridBagLayout1);

		this.setEnabled(true);
		this.setFont(new java.awt.Font("Dialog", 0, 12));
		this.setLocale(java.util.Locale.getDefault());
		this.setTitle("Node Search");

		resultTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		resultTree.addTreeSelectionListener(this);

		lblQuestion.setFont(new java.awt.Font("Dialog", 0, 12));
		lblQuestion.setText("Enter search text:");
		cbxNodeText.setText("Search Node Text");
		cbxNodeText.setSelected(true);
		cbxKeyword.setText("Search Node Keywords");
		cbxKeyword.setSelected(true);
		cbxCaseSens.setText("Case sensative search");

		btnSearch.setFont(new java.awt.Font("Dialog", 0, 10));
		btnSearch.setMaximumSize(new Dimension(99, 23));
		btnSearch.setMinimumSize(new Dimension(99, 23));
		btnSearch.setText("Search");
		btnSearch.setActionCommand("Search");
		btnSearch.addActionListener(this);

		btnClose.setFont(new java.awt.Font("Dialog", 0, 10));
		btnClose.setMaximumSize(new Dimension(99, 23));
		btnClose.setMinimumSize(new Dimension(99, 23));
		btnClose.setText("Close");
		btnClose.setActionCommand("Close");
		btnClose.addActionListener(this);

		txtInput.setFont(new java.awt.Font("Dialog", 0, 11));
		txtInput.setText("");
		txtInput.setBorder(BorderFactory.createBevelBorder(1));
		txtInput.setEditable(true);
		txtInput.setMaximumSize(new Dimension(99, 19));
		txtInput.setMinimumSize(new Dimension(99, 19));

		buttonPanel.add(btnSearch);
		buttonPanel.add(btnClose);

		this.getContentPane().add(lblQuestion,
				new GridBagConstraints(0, 0, 2, 1, 0, 0,
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						new Insets(12, 35, 5, 0), 0, 0));

		this.getContentPane().add(txtInput,
				new GridBagConstraints(0, 0, 2, 1, 0, 0,
						GridBagConstraints.EAST, GridBagConstraints.NONE,
						new Insets(10, 0, 0, 35), 0, 0));

		this.getContentPane().add(cbxKeyword,
				new GridBagConstraints(0, 1, 2, 1, 0, 0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(0, 0, 0, 0), 0, 0));

		this.getContentPane().add(cbxNodeText,
				new GridBagConstraints(0, 2, 2, 1, 0, 0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(0, -25, 0, 0), 0, 0));

		this.getContentPane().add(cbxCaseSens,
				new GridBagConstraints(0, 3, 2, 1, 0, 0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(0, -5, 0, 0), 0, 0));

		this.getContentPane().add(ScrollPane,
				new GridBagConstraints(0, 4, 2, 1, 0, 0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(10, 0, 10, 0), 250, 150));

		this.getContentPane().add(buttonPanel,
				new GridBagConstraints(0, 5, 2, 1, 0, 0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(0, 0, 10, 0), 0, 0));

	}

	public Vector parseString(String s)
	{
		StringTokenizer st = new StringTokenizer(s);
		Vector vRet = new Vector();

		while (st.hasMoreTokens())
		{
			vRet.add(st.nextToken());
		}

		return vRet;
	}

	void onWindowCloseOperation()
	{
		dispose();
	}

	//WindowListeners
	public void windowClosing(WindowEvent e)
	{
		onWindowCloseOperation();
	}

	public void windowOpened(WindowEvent e)
	{
	}

	public void windowClosed(WindowEvent e)
	{
	}

	public void windowIconified(WindowEvent e)
	{
	}

	public void windowDeiconified(WindowEvent e)
	{
	}

	public void windowActivated(WindowEvent e)
	{
	}

	public void windowDeactivated(WindowEvent e)
	{
	}

	//ActionListener
	public void actionPerformed(ActionEvent e)
	{
		if (e.getActionCommand().equals("Search"))
		{
			NodeSearch search = null;
			if(cbxKeyword.isSelected() && cbxNodeText.isSelected())
				search = new NodeSearch(2, cbxCaseSens.isSelected());
			else if(cbxKeyword.isSelected())
				search = new NodeSearch(0, cbxCaseSens.isSelected());
			else if(cbxNodeText.isSelected())
				search = new NodeSearch(1, cbxCaseSens.isSelected());

			if(search != null)
			{
				String currentEntry = txtInput.getText();
				Vector vParsed = parseString(currentEntry);

				Object[] resultList = search.getResults(vParsed);
				SearchResultModel searchModel = new SearchResultModel(resultList);
				resultTree.setModel(searchModel.getModel());
			}

		}
		else if (e.getActionCommand().equals("Close"))
		{
			onWindowCloseOperation();
		}
	}

	//TreeListener
	public void valueChanged(TreeSelectionEvent e)
	{
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)resultTree.getLastSelectedPathComponent();

		if(node == null)
			return;

		ArrayList idlist = new ArrayList();
		try
		{
			idlist.add((Guid)(((mil.af.rl.jcat.plan.Event)(node.getUserObject())).getGuid()));

			JWBUID shapeUID = (JWBUID)(((LinkedList)(((AbstractPlan)(MainFrm.getInstance().getActiveView().getPlan())).getShapeMapping(idlist))).getFirst());
			JWBShape shape = ((JWBController)(Control.getInstance().getController(((AbstractPlan)(MainFrm.getInstance().getActiveView().getPlan())).getId()))).getShape(shapeUID);
			MainFrm.getInstance().getActiveView().getPanel().centerOnPoint(shape.getCenterPoint());
		}
		catch(Exception ee)
		{
			logger.warn("valueChanged(tree) - error centering view on shape");
		}
	}   
}
