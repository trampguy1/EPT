package mil.af.rl.jcat.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;

import mil.af.rl.jcat.control.collaboration.CCDataPacket;
import mil.af.rl.jcat.control.collaboration.CollaborationControl;
import mil.af.rl.jcat.exceptions.GraphLoopException;
import mil.af.rl.jcat.exceptions.SamplerMemoryException;
import mil.af.rl.jcat.gui.MainFrm;
import mil.af.rl.jcat.plan.AbstractPlan;
import mil.af.rl.jcat.util.IntegerTextField;
import mil.af.rl.jcat.exceptions.SignalException;


public class SamplingOptions extends JDialog implements KeyListener, ItemListener, Runnable
{

	private static final long serialVersionUID = 1L;
	JPanel pane = new JPanel();
	BorderLayout bl = new BorderLayout();
	JPanel buttons = new JPanel();
	JButton ok = new JButton();
	JButton cancel = new JButton();
	GridLayout gridLayout1 = new GridLayout();
	TitledBorder titledBorder1;
	Border border1;
	Border border2;
	TitledBorder titledBorder2;
	Border border3;
	GridLayout gridLayout2 = new GridLayout();
	JPanel timimg = new JPanel();
	JLabel planText = new JLabel();
	IntegerTextField planLength = new IntegerTextField();
	JLabel spacer = new JLabel();
	JLabel simText = new JLabel();
	JLabel spacer2 = new JLabel();
	GridBagLayout gridBagLayout1 = new GridBagLayout();
	private AbstractPlan plan;
	private JCheckBox advOpt;
	private int length;
	private static Logger logger = Logger.getLogger(SamplingOptions.class);


	public SamplingOptions(AbstractPlan plan)
	{
		super(MainFrm.getInstance());
		this.plan = plan;
		try
		{
			jbInit();
		} catch (Exception e)
		{
			logger.error("Constructor - error initializing dialog");
		}
		initializeValues();
		setModal(true);
		setLocationRelativeTo(MainFrm.getInstance());
		setVisible(true);

	}

	/**
	 *
	 */
	private void initializeValues()
	{
		int length = 0;
		if(plan.getBayesNet() == null)
		{
			if(plan.getLoadedPlanLength() != -1)
			{
				length = plan.getLoadedPlanLength();
			}
			else
				return;
		}
		else
		{
			length = plan.getBayesNet().getTimespan();
		}
		planLength.setText("" + length);
	}

	private void jbInit() throws Exception {
		titledBorder1 = new TitledBorder("");
		border1 = BorderFactory.createEmptyBorder(0,10,0,0);
		border2 = new EtchedBorder(EtchedBorder.RAISED,Color.white,new Color(165, 163, 151));
		titledBorder2 = new TitledBorder(border2,"Sampliing Options");
		border3 = titledBorder2;//BorderFactory.createCompoundBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED,Color.white,new Color(165, 163, 151)),"Sampliing Options"),BorderFactory.createEmptyBorder(0,20,10,20));
		this.getContentPane().setLayout(bl);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setTitle("Bayes Net Timing");
		pane.setLayout(gridLayout2);
		buttons.setBorder(border1);
		buttons.setOpaque(true);
		buttons.setLayout(new java.awt.FlowLayout());
		ok.setText("OK");
		ok.setMargin(new Insets(2, 5, 2, 5));
		ok.setFont(ok.getFont().deriveFont(java.awt.Font.BOLD));
//		ok.setPreferredSize(new Dimension(60,20));
		ok.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0)
			{
				startSampler();
			}
		});
		cancel.setText("Cancel");
		cancel.setMargin(new Insets(2, 5, 2, 5));
//		cancel.setPreferredSize(new Dimension(60,20));
		ok.setPreferredSize(cancel.getPreferredSize());
		cancel.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0)
			{
				closeFrame();
			}
		});

		JButton cHelpBtn1 = new JButton(new ImageIcon(this.getClass().getClassLoader().getResource("chelp_sm.gif")));
		cHelpBtn1.setPreferredSize(new Dimension(25, 23));
		cHelpBtn1.setMargin(new java.awt.Insets(0,2,0,2));
		try{
			cHelpBtn1.addActionListener(new javax.help.CSH.DisplayHelpAfterTracking(MainFrm.getInstance().getHelpBroker()));
		}catch(Exception ex){
			logger.warn("init - could not add help listener to context button:  "+ex.getMessage());
		}

		gridLayout1.setColumns(0);
		gridLayout1.setHgap(20);
		gridLayout1.setVgap(20);
		pane.setBorder(border3);
		gridLayout2.setColumns(2);
		timimg.setLayout(gridBagLayout1);
		planText.setText("Estimated Model Length");
		simText.setText("Simulation Time Slice Count");
		spacer.setMinimumSize(new Dimension(8, 8));
		spacer.setPreferredSize(new Dimension(2, 2));
		spacer.setText("");
		planLength.setText("");
		planLength.addKeyListener(this);
		spacer2.setMinimumSize(new Dimension(34, 14));
		spacer2.setPreferredSize(new Dimension(3, 3));
		spacer2.setText("");
		advOpt = new JCheckBox("show advanced options", true);
		advOpt.addItemListener(this);
		this.getContentPane().add(buttons, BorderLayout.SOUTH);
		buttons.add(ok);
		buttons.add(cancel);
		buttons.add(new JLabel());
		buttons.add(cHelpBtn1);
		this.getContentPane().add(pane,  BorderLayout.CENTER);
		pane.add(timimg, null);
		javax.help.CSH.setHelpIDString(timimg, "Sampler_Dialog");
		timimg.add(spacer,  new GridBagConstraints(0, 0, 1, GridBagConstraints.REMAINDER, 0.0, 0.0
				,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0));
		timimg.add(spacer2,  new GridBagConstraints(0, 1, 1, GridBagConstraints.REMAINDER, 0.0, 0.0
				,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));
		timimg.add(planText,  new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
				,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 92, 0));
		timimg.add(planLength,  new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0
				,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 188, 0));
//		timimg.add(simText,  new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
//		,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 69, 0));
//		timimg.add(advOpt,  new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0
//		,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 100, 0));
		this.setSize(260, 135);
	}
	/**
	 *
	 */
	protected void startSampler()
	{
		try
		{
			length = Integer.parseInt(planLength.getText());

			if(length != 0)
			{
				plan.setIsBuilding(true);
				new Thread(this, "Start-Sampler").start();
				MainFrm.getInstance().startUpdateThread();
				if(CollaborationControl.getInstance().isRunning())
					CollaborationControl.getInstance().send(new CCDataPacket(CCDataPacket.LOG_INFO, "- This user has sampled the model with length of "
							+ length + " -"));
				closeFrame();
			}
			else
			{
				planLength.removeKeyListener(this); // on message dialog (screwy crap)
				JOptionPane.showMessageDialog(this, "Slice count must be evenly divisible by model length!");
				planLength.addKeyListener(this);
			}
		}catch(NumberFormatException e)
		{
			planLength.removeKeyListener(this); // on message dialog (screwy crap)
			JOptionPane.showMessageDialog(this, "You must enter valid input to start the sampler.");
			planLength.addKeyListener(this);
		}

	}

	private void closeFrame()
	{
		this.dispose();
	}

	public void keyPressed(KeyEvent event)
	{
		if(event.getKeyCode() == KeyEvent.VK_ENTER)
			startSampler();
	}


	public void keyTyped(KeyEvent arg0){}

	public void run() 
	{
		try
		{
			plan.buildBayesNet(length);
			
		} catch (SignalException e)
		{
			JOptionPane.showMessageDialog(MainFrm.getInstance(), "Inconsistency found in the conditional probability tables.  The sampler has not started. ");
			logger.error("run - inconsistency found in conditional prob tables, sampler not started:  ", e);
		} catch (GraphLoopException e)
		{
			JOptionPane.showMessageDialog(MainFrm.getInstance(), "A loop has been detected in the plan.  The sampler cannot start. ");
			logger.warn("run - graph loop detected, sampler not start:  "+e.getMessage());
		}catch(SamplerMemoryException e)
		{
			JOptionPane.showMessageDialog(MainFrm.getInstance(), "JCAT cannot access enough memory to start the sampler with the parameters specified. \nThe sampler has not started.");
			logger.error("run - out of memory error, sampler not started:  ", e);
		}

	}

	public void keyReleased(KeyEvent e) {

	}

	public void itemStateChanged(ItemEvent e) {

	}

}
