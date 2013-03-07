package mil.af.rl.jcat.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JToolBar;

import mil.af.rl.jcat.gui.dialogs.SamplingOptions;

/**
 * @author Craig McNamara
 *
 *
 */
public class PlanToolBar extends JToolBar implements MouseListener
{

	private static final long serialVersionUID = 1L;
	private MainFrm parent = null;
	private static String modes[] = new String[]{
		"Select","com.c3i.jwb.shapes.JWBRoundedRectangle",
		"com.c3i.jwb.JWBLine"
	};


	//ToolBar Buttons
	private ImageIcon pause = new ImageIcon(
			this.getClass().getClassLoader().getResource( "pause.png"));
	private ImageIcon play = new ImageIcon(
			this.getClass().getClassLoader().getResource( "build.png"));

	private JButton addActionBtn = new JButton(new ImageIcon(
			this.getClass().getClassLoader().getResource( "rounded_rect.png")));

	private JButton addMechanismBtn = new JButton(new ImageIcon(
			this.getClass().getClassLoader().getResource( "arrow.png")));

//	private JButton doodleBtn = new JButton(new ImageIcon(
//	this.getClass().getClassLoader().getResource( "Doodle.gif")));

	private JButton buildBtn = new JButton(play);
	private JButton stopBtn = new JButton(new ImageIcon(this.getClass().getClassLoader().getResource("stop16.png")));
	private JButton pointerBtn = new JButton(new ImageIcon(this.getClass().getClassLoader().getResource( "select.gif")));
	private JButton zoomIn = new JButton(new ImageIcon(	this.getClass().getClassLoader().getResource( "zoomin.png")));
	private JButton zoomOut = new JButton(new ImageIcon(this.getClass().getClassLoader().getResource( "zoomout.png")));
	private JButton zoomDef = new JButton(new ImageIcon(this.getClass().getClassLoader().getResource( "zoomdefault.png")));
	private JButton magnify = new JButton(new ImageIcon(this.getClass().getClassLoader().getResource("stock_bring-forward.png")));

	private ArrayList btnArray = new ArrayList();

	public PlanToolBar(MainFrm frm) {
		parent = frm;
		buildToolBar();
		this.setFloatable(false);
	}

	private void buildToolBar() {
		this.setOrientation(VERTICAL);
		//Setup pointer item
		setupItem(pointerBtn, "Selection Tool");
		pointerBtn.setBackground(java.awt.Color.gray);
		pointerBtn.setMnemonic(java.awt.event.KeyEvent.VK_P); // Mnemonic by Josh
		if(System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0)
			pointerBtn.setToolTipText(pointerBtn.getToolTipText() + " (Alt+P)");
		pointerBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pointerClicked(e);
			}
		});
		//Setup AddAction btn
		setupItem(addActionBtn, "Add Event");
		addActionBtn.setMnemonic(java.awt.event.KeyEvent.VK_E); // Mnemonic by Josh
		if(System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0)
			addActionBtn.setToolTipText(addActionBtn.getToolTipText() + " (Alt+E)");
		addActionBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionClicked(e);
			}
		});
		//Setup Add Mechanism btn
		setupItem(addMechanismBtn, "Add Mechanism");
		addMechanismBtn.setMnemonic(java.awt.event.KeyEvent.VK_M); // Mnemonic by Josh
		if(System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0)
			addMechanismBtn.setToolTipText(addMechanismBtn.getToolTipText() + " (Alt+M)");
		addMechanismBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mechanismClicked(e);
			}
		});

		//magnify yo
		setupItem(magnify, "Magnification Tool");
		magnify.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				magnifyMode(e);
			}
		});

		//Setup Doodle btn
		//Removed for release -CM 2/15/05
		//setupItem(doodleBtn, "Doodle");
//		doodleBtn.addActionListener(new ActionListener() {
//		public void actionPerformed(ActionEvent e) {
//		doodleClicked(e);
//		}
//		});

		//Setup Build Btn
		setupItem(buildBtn, "Start Sampling");
		buildBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buildBayesNet(e);
			}
		});

		// stop sampler button
		setupItem(stopBtn, "Stop Sampling");
		stopBtn.setEnabled(false);
		stopBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopSampler();
			}
		});
		
		// zoom in button
		setupItem(zoomIn, "Zoom in");
		zoomIn.setMnemonic(java.awt.event.KeyEvent.VK_UP);
		if(System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0)
			zoomIn.setToolTipText(zoomIn.getToolTipText() + " (Alt+UP)");
		zoomIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				zoomInClicked(e);
			}
		});
		// zoom out button
		setupItem(zoomOut, "Zoom out");
		zoomOut.setMnemonic(java.awt.event.KeyEvent.VK_DOWN);
		if(System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0)
			zoomOut.setToolTipText(zoomOut.getToolTipText() + " (Alt+DOWN)");
		zoomOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				zoomOutClicked(e);
			}
		});

		// zoom default
		setupItem(zoomDef, "Zoom to fit");
		zoomDef.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				zoomDefault(e);
			}
		});

		javax.help.CSH.setHelpIDString(this, "Plan_Toolbar1");

		//Ad buttons to array for controling appearance
		btnArray.add(pointerBtn);
		btnArray.add(addActionBtn);
		btnArray.add(addMechanismBtn);
		btnArray.add(magnify);
		//Removed for release -CM 2/15/05
		//btnArray.add(doodleBtn);
		btnArray.add(buildBtn);
		btnArray.add(zoomIn);
		btnArray.add(zoomOut);
		btnArray.add(zoomDef);
	}

	public void setBayesNetRunning(boolean running)
	{
		stopBtn.setEnabled(running);
		
		if(running)
		{
//			buildBtn.setIcon(this.pause);
//			buildBtn.setToolTipText("Pause Simulations");
		}
		else
		{
//			buildBtn.setIcon(this.play);
//			buildBtn.setToolTipText("Start Simulations");
		}
	}

	// helper method used to build the toolbar
	private void setupItem(JButton button, String tip) {
		button.setBorder(BorderFactory.createRaisedBevelBorder());
		button.setBorderPainted(false);
		button.setToolTipText(tip);
		button.setActionCommand(tip);
		button.addMouseListener(this);
		button.setMargin(new java.awt.Insets(1, 1, 1, 1));
		this.add(button);
	}

	public Object[] getButtons()
	{
		return btnArray.toArray();
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

	public void deselectButtons(JButton newSelection) //selects the tool specified
	{
		//donno why btnArray existed before, but I'll make use of it -MD
		Iterator buttons = btnArray.iterator();
		while(buttons.hasNext())
			((JButton)buttons.next()).setBackground(null);
		newSelection.setBackground(java.awt.Color.gray);
	}

	public void deselectButtons() //selects the default tool
	{
		deselectButtons(pointerBtn);
	}

	//ToolBar Events
	private void pointerClicked(ActionEvent e) {
		CatView view = parent.getActiveView();
		if(view != null)
		{
			parent.setEditState(modes[0]);
			deselectButtons();
		}
	}

	private void actionClicked(ActionEvent e) {
		CatView view = parent.getActiveView();
		if(view != null)
		{
			parent.setEditState(modes[1]);
			deselectButtons(addActionBtn);
		}
	}

	private void mechanismClicked(ActionEvent e) {
		CatView view = parent.getActiveView();
		if(view != null)
		{
			parent.setEditState(modes[2]);
			deselectButtons(addMechanismBtn);
		}
	}

	private void examModeClicked()
	{
		//parent.set
	}

	private void doodleClicked(ActionEvent e) {
		//parent.setEditState(10);
	}

	private void zoomInClicked(ActionEvent e) {
		CatView view = parent.getActiveView();
		if(view != null)
			view.getPanel().zoomIn();
	}

	private void zoomOutClicked(ActionEvent e) {
		CatView view = parent.getActiveView();
		if(view != null)
			view.getPanel().zoomOut();
	}

	private void zoomDefault(ActionEvent e) {
		CatView view = parent.getActiveView();
		if(view != null)
			view.getPanel().zoomToFit();
	}

	private void buildBayesNet(ActionEvent e) {
		CatView view = parent.getActiveView();
		if(view != null)
		{
			SamplingOptions dlg = new SamplingOptions(view.getPlan());
		}
	}
	
	private void stopSampler()
	{
		CatView view = parent.getActiveView();
		if(view != null)
			view.getPlan().getBayesNet().killSampler();
	}

	private void magnifyMode(ActionEvent e)
	{
		CatView view = parent.getActiveView();
		if(view != null)
		{
			view.getPanel().setMode("Magnify");
			deselectButtons(magnify);
		}
	}

	
	//unused mouse events
	public void mouseReleased(MouseEvent e){}
	public void mouseClicked(MouseEvent e){}
	public void mousePressed(MouseEvent e){}
}
