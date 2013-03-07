/*
 * Created on Jul 28, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package mil.af.rl.jcat.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JToolBar;

import org.apache.log4j.Logger;

import mil.af.rl.jcat.gui.dialogs.HistogramDialog;
import mil.af.rl.jcat.gui.dialogs.LeakerCountsDialog;
import mil.af.rl.jcat.gui.dialogs.wizard.TutorialWizard;
import mil.af.rl.jcat.plan.AbstractPlan;

/**
 * @author Craig McNamara
 *
 * Method @
 */
public class FileToolBar extends JToolBar implements MouseListener, ActionListener {

	private static final long serialVersionUID = 1L;
	private MainFrm parent = null;
	private Vector buttons = new Vector();

	private static Logger logger = Logger.getLogger(FileToolBar.class);

	private JButton newPlanBtn = new JButton(new ImageIcon(
	        this.getClass().getClassLoader().getResource("new.png")));
	private JButton openPlanBtn = new JButton(new ImageIcon(
	        this.getClass().getClassLoader().getResource( "open.png")));
	private JButton savePlanBtn = new JButton(new ImageIcon(
	        this.getClass().getClassLoader().getResource( "save.png")));
	private JButton savePlanAsBtn = new JButton(new ImageIcon(
	        this.getClass().getClassLoader().getResource( "saveas.png")));
	private JButton copyBtn = new JButton(new ImageIcon(
	        this.getClass().getClassLoader().getResource( "copy.png")));
	private JButton pasteBtn = new JButton(new ImageIcon(
	        this.getClass().getClassLoader().getResource( "paste.png")));
	private JButton printBtn = new JButton(new ImageIcon(
	        this.getClass().getClassLoader().getResource( "print.png")));
	private JButton helpBtn = new JButton(new ImageIcon(
	        this.getClass().getClassLoader().getResource( "help_bg.png")));
	private JButton cHelpBtn = new JButton(new ImageIcon(this.getClass().getClassLoader().getResource("conthelp.gif")));
	private JButton testingButton = new JButton("  Model Acceptance Alpha  ");
//	private JButton resBtn = new JButton("  Resource Counts  ");
	private JButton resLeakBtn = new JButton("  Resource Counts  ");

	
	public FileToolBar(MainFrm frm) {
		parent = frm;
		buildToolBar();
		this.setFloatable(false);
	}

	private void buildToolBar() {
		//this.setOrientation(HORIZONTAL);

		setupItem(newPlanBtn, "New");
		setupItem(openPlanBtn, "Open");
		setupItem(savePlanBtn, "Save");
		setupItem(savePlanAsBtn, "Save As");
		//this.addSeparator();
		setupItem(copyBtn, "Copy");
		setupItem(pasteBtn, "Paste");
		//this.addSeparator();
		setupItem(printBtn, "Print");
		//this.addSeparator();
		setupItem(helpBtn, "Help");
		setupItem(cHelpBtn, "Context Help");
//		setupItem(resBtn, "Resource Counts");
		setupItem(resLeakBtn, "Resource Counts");
		
		cHelpBtn.addActionListener(new javax.help.CSH.DisplayHelpAfterTracking(parent.getHelpBroker()));
		javax.help.CSH.setHelpIDString(this, "File_Toolbar");
		
		//testingButton.setPreferredSize(new java.awt.Dimension(60, 20));
		testingButton.addActionListener(this);
		setupItem(testingButton, "Model Acceptance Alpha");

		printBtn.setEnabled(false);
	}	

	// helper method used to build the toolbar
	private void setupItem(AbstractButton button, String tip) {
		button.setBorder(BorderFactory.createRaisedBevelBorder());
		button.setBorderPainted(false);
		button.setToolTipText(tip);
		button.setActionCommand(tip);
		button.addActionListener(this);
		button.addMouseListener(this);
		button.setMargin(new java.awt.Insets(1, 1, 1, 1));
		button.setOpaque(false);
		buttons.add(button);
		this.add(button);
	}

	public Object[] getButtons()
	{
		return buttons.toArray();
	}
	
	public void onFileNew() {
		this.parent.createDocument();
	}

	public void onFileOpen() {
		this.parent.openDocument();
	}

	public void onFileSave() {
	    if(parent.getActiveView() != null )
	        this.parent.getActiveView().save();
	}

	public void onFileSaveAs() {
	    if(parent.getActiveView() != null )
	       this.parent.getActiveView().saveAs();
	}

	public void onFileCut()
	{
		CatView view = parent.getActiveView();
		if(view != null)
			view.getPopupManager().copy();
	}

	public void onFilePaste()
	{
		CatView view = parent.getActiveView();
		if(view != null)
			view.getPopupManager().paste();
	}

	public void onFilePrint() {

	}

	public void onHelp()
	{
		//TODO:  CALL JAVAHELP
		parent.getHelpBroker().setDisplayed(true);
	}


	/**
	 * Used to modify look of toolbar button.
	 *
	 * @param e
	 *            the MouseEvent received
	 */
	public void mouseEntered(MouseEvent e) {
		try {
			((JMenu) e.getSource()).setBorderPainted(true);
		} catch (Exception ex) {
		}

		try {
			((JButton) e.getSource()).setBorderPainted(true);
		} catch (Exception ex) {
		}
	}

	/**
	 * Used to modify look of toolbar button.
	 *
	 * @param e
	 *            the MouseEvent received
	 */
	public void mouseExited(MouseEvent e) {
		try {
			((JMenu) e.getSource()).setBorderPainted(false);
		} catch (Exception ex) {
		}

		try {
			((JButton) e.getSource()).setBorderPainted(false);
		} catch (Exception ex) {
		}
	}

	/**
	 * Unused MouseListener method.
	 *
	 * @param e
	 *            the MouseEvent received
	 */
	public void mouseReleased(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent e) {

	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		if(e.getActionCommand().equals("New"))
			this.onFileNew();
		else if(e.getActionCommand().equals("Open"))
			this.onFileOpen();
		else if(e.getActionCommand().equals("Save"))
			this.onFileSave();
		else if(e.getActionCommand().equals("Save As"))
			this.onFileSaveAs();
		else if(e.getActionCommand().equals("Copy"))
			this.onFileCut();
		else if(e.getActionCommand().equals("Paste"))
			this.onFilePaste();
		else if(e.getActionCommand().equals("Help"))
			onHelp();
		else if(e.getSource().equals(testingButton))
		{
			//Sampler samp = parent.getActiveView().getPlan().getBayesNet().getSampler();
			//if(samp instanceof LikelihoodSampler)
			//new HistogramDialog(parent, ((LikelihoodSampler)samp).getStatistics()).setVisible(true);
			if(parent.getActiveView() != null)
			{
				AbstractPlan plan = parent.getActiveView().getPlan();
				if(plan.getBayesNet() != null)
					new HistogramDialog(parent, plan).setVisible(true);
			}
		}
//		else if(e.getSource().equals(resBtn))
//		{
//		if(parent.getActiveView() != null)
//		new ResourceCountsDialog(parent, parent.getActiveView().getPlan());
//		}
		else if(e.getSource().equals(resLeakBtn))
		{
			if(parent.getActiveView() != null)
				new LeakerCountsDialog(parent, parent.getActiveView().getPlan());
		}
	}
}
