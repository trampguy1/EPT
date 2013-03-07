package mil.af.rl.jcat.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EtchedBorder;


import mil.af.rl.jcat.bayesnet.NetNode;
import mil.af.rl.jcat.bayesnet.ResourcedLHSampler;
import mil.af.rl.jcat.bayesnet.Sampler;
import mil.af.rl.jcat.gui.MainFrm;
import mil.af.rl.jcat.gui.table.ResCountsTable;
import mil.af.rl.jcat.gui.table.model.CountsTableModel;
import mil.af.rl.jcat.plan.AbstractPlan;
import mil.af.rl.jcat.util.CatFileFilter;


public class LeakerCountsDialog extends JDialog implements ActionListener, ItemListener
{

	private static final long serialVersionUID = 1L;
	private ResCountsTable propTable = null;
	private CountsTableModel model;
	private JButton okButton, cancButton;
	private JDialog addPropBox;
	private TreeMap<NetNode, float[]> counts;
	private JCheckBox filterNonResOpt;
	private JCheckBox filterZerosOpt;
	private JButton exportXls;


	public LeakerCountsDialog(Frame parent, AbstractPlan absPlan)
	{
		super(parent);
		setSize(400, 500);
		setLocationRelativeTo(parent);
		
		
		setTitle("Resource Counts");
		
		try{
			Sampler samp = absPlan.getBayesNet().getSampler();
		
			if(samp instanceof ResourcedLHSampler)
			{
				counts = ((ResourcedLHSampler)samp).getLeakerCounts();
				
				initialize();
				setVisible(true);
			}
			else
			{
				JOptionPane.showMessageDialog(parent, "The current sampler is not a resource sampler. \nThis is most likely " +
						"caused by not having any resources in your model.  You must have resources in your model before using resource counts.");
				dispose();
			}
		}catch(NullPointerException exc)
		{
			JOptionPane.showMessageDialog(parent, "The sampler is not running.  Please start the sampler first.");
		}
	}

	
	private void initialize()
	{
		this.setSize(500, 460);

		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BorderLayout());

		JPanel topButtPan = new JPanel();
		topButtPan.setLayout(new GridLayout(0,1));
		//topButtPan.setPreferredSize(new Dimension(150, 300));
		//topButtPan.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));
		//topButtPan.add(addButton("Add Resource"));
		//topButtPan.add(addButton("Remove Resource"));
		
		JPanel botButtPan = new JPanel();
		botButtPan.setLayout(new FlowLayout());
		
		okButton = new JButton("Close");
		okButton.setPreferredSize(new Dimension(60, 26));
		okButton.setFont(new Font("Dialog", Font.BOLD, 12));
		okButton.addActionListener(this);
		exportXls = new JButton("Export - Excel");
		exportXls.addActionListener(this);
		filterNonResOpt = new JCheckBox("Filter non-resourced nodes", false);
		filterNonResOpt.addItemListener(this);
		filterZerosOpt = new JCheckBox("Filter 'all-zero' items", false);
		filterZerosOpt.addItemListener(this);
		
		botButtPan.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		botButtPan.add(filterNonResOpt);
		botButtPan.add(filterZerosOpt);
		botButtPan.add(okButton);
		botButtPan.add(exportXls);
		
		
		buttonPanel.add(topButtPan, BorderLayout.NORTH);
		buttonPanel.add(botButtPan, BorderLayout.SOUTH);
		
		contentPane.add(createTable(), BorderLayout.CENTER);
		contentPane.add(buttonPanel, BorderLayout.SOUTH);


		this.setContentPane(contentPane);
	}
	
	private JComponent createTable()
	{
		propTable = new ResCountsTable(model = new CountsTableModel(filterNonResOpt.isSelected(), filterZerosOpt.isSelected(), counts), false);
		propTable.setFont(new Font("Dialog", 0, 12));
		propTable.getColumnModel().getColumn(1).setMinWidth(150);
		propTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		
		JScrollPane tableScroll = new JScrollPane(propTable);
//		tableScroll.setViewportView(propTable);
		tableScroll.getViewport().setBackground(Color.WHITE);
		
		return tableScroll;
	}

	private JComponent addButton(String txt)
	{
		JButton newButton = new JButton(txt);
		newButton.setFont(new Font("Dialog", Font.PLAIN, 12)); 
		newButton.addActionListener(this);
		
		JPanel newPan = new JPanel(new FlowLayout());
		newPan.add(newButton);
		
		return newPan;
	}
	
	
	public void actionPerformed(ActionEvent event)
	{
		if(event.getSource() == okButton)
		{
			dispose();
		}
		else if(event.getSource() == exportXls)
		{
			try{
				String exPath = browseForExport("xls", "MS Excel documents", false);
				model.exportToExcel(exPath);
			}catch(IOException exc){
				JOptionPane.showMessageDialog(this, "Could not export excel document. \n"+exc.getMessage());
			}
		}
		else if(event.getSource() == cancButton)
			dispose();
		

	}

	
	private String browseForExport(String ext, String desc, boolean dirsOnly) throws FileNotFoundException
	{
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setFileFilter(new CatFileFilter(ext, desc, true));
		if(dirsOnly)
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		if(fileChooser.showSaveDialog(MainFrm.getInstance()) == JFileChooser.APPROVE_OPTION)
		{
			if(fileChooser.getSelectedFile().getAbsolutePath().toLowerCase().endsWith("."+ext) || dirsOnly)
				return fileChooser.getSelectedFile().getAbsolutePath();
			else
				return fileChooser.getSelectedFile().getAbsolutePath()+"."+ext;
		}
		else
			throw new FileNotFoundException("User canceled export.");
	}
	
	
	public void itemStateChanged(ItemEvent event)
	{
		model.buildTable(filterNonResOpt.isSelected(), filterZerosOpt.isSelected());
	}
}
