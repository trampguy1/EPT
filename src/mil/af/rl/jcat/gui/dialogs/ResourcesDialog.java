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
import java.rmi.RemoteException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import org.apache.log4j.Logger;

import com.c3i.jwb.JWBController;
import com.c3i.jwb.JWBShape;

import mil.af.rl.jcat.control.PlanArgument;
import mil.af.rl.jcat.gui.table.model.ResourceTableModel;
import mil.af.rl.jcat.plan.AbstractPlan;
import mil.af.rl.jcat.plan.PlanItem;
import mil.af.rl.jcat.util.Guid;


public class ResourcesDialog extends JDialog implements ActionListener
{

	private static final long serialVersionUID = 1L;
	private JTable propTable = null;
	private ResourceTableModel model;
	private JButton okAddBut, cancAddBut, okButton, cancButton;
	private JDialog addPropBox;
	private JTextField nmFld, valFld;
	private PlanItem planItem;
	private JWBController controller;
	private JWBShape jwbShape;
	private JCheckBox contigFld;
    private AbstractPlan plan;
    private static Logger logger = Logger.getLogger(ResourcesDialog.class);


	public ResourcesDialog(Frame parent, JWBShape shape, JWBController control, AbstractPlan plan)
	{
		super(parent);
		setSize(400, 500);
		setLocationRelativeTo(parent);
		this.plan = plan;
		planItem = plan.getItem((Guid)shape.getAttachment());
		setTitle("Resources:  " + planItem.getLabel());
		controller = control; //used to put shape when planitem is modified (just like all other dialogs)
		jwbShape = shape;
		
		initialize();
		
		setVisible(true);
	}

	
	private void initialize()
	{
		this.setSize(430, 460);

		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BorderLayout());

		JPanel topButtPan = new JPanel();
		topButtPan.setLayout(new GridLayout(0,1));
		//topButtPan.setPreferredSize(new Dimension(150, 300));
		//topButtPan.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));
		topButtPan.add(addButton("Add Resource"));
		topButtPan.add(addButton("Remove Resource"));
		
		JPanel botButtPan = new JPanel();
		botButtPan.setLayout(new FlowLayout());
		
		okButton = new JButton("OK");
		okButton.setPreferredSize(new Dimension(60, 26));
		okButton.setFont(new Font("Dialog", Font.BOLD, 12));
		okButton.addActionListener(this);
		cancButton = new JButton("Cancel");
		cancButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		cancButton.addActionListener(this);
		
		botButtPan.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		botButtPan.add(okButton, null);
		botButtPan.add(cancButton, null);
		
		buttonPanel.add(topButtPan, BorderLayout.NORTH);
		buttonPanel.add(botButtPan, BorderLayout.SOUTH);
		
		contentPane.add(createTable(), BorderLayout.CENTER);
		contentPane.add(buttonPanel, BorderLayout.EAST);


		this.setContentPane(contentPane);
	}
	
	private JComponent createTable()
	{
		propTable = new JTable(model = new ResourceTableModel(planItem.getResources()));
		propTable.setFont(new Font("Dialog", 0, 12));

		JScrollPane tableScroll = new JScrollPane();
		tableScroll.setViewportView(propTable);
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
	
	private void showAddProperty()
	{
		addPropBox = new JDialog(this, "Add Property", true);
		addPropBox.setLayout(new BorderLayout());
		JPanel lblPan = new JPanel(new GridLayout(3,1));
		JPanel fldPan = new JPanel(new GridLayout(3,1));
		JPanel butPan = new JPanel(new FlowLayout());
		
		JLabel nmLbl = new JLabel("Name: ");
		JPanel nmLblPan = new JPanel(new FlowLayout());
		nmLblPan.add(nmLbl);
		lblPan.add(nmLblPan);
		JLabel valLbl = new JLabel("Value: ");
		JPanel valLblPan = new JPanel(new FlowLayout());
		valLblPan.add(valLbl);
		lblPan.add(valLblPan);
		JLabel contigLbl = new JLabel("Contingent");
		JPanel contigLblPan = new JPanel(new FlowLayout());
		contigLblPan.add(contigLbl);
		lblPan.add(contigLblPan);
		
		nmFld = new JTextField(30);
		JPanel nmFldPan = new JPanel(new FlowLayout());
		nmFldPan.add(nmFld);
		fldPan.add(nmFldPan);
		valFld = new JTextField(30);
		JPanel valFldPan = new JPanel(new FlowLayout());
		valFldPan.add(valFld);
		fldPan.add(valFldPan);
		contigFld = new JCheckBox("", true);
		FlowLayout fl = new FlowLayout();
		fl.setAlignment(FlowLayout.LEFT);
		JPanel contFldPan = new JPanel(fl);
		contFldPan.add(contigFld);
		fldPan.add(contFldPan);
		

		okAddBut = new JButton("OK");
		okAddBut.addActionListener(this);
		cancAddBut = new JButton("Cancel");
		cancAddBut.addActionListener(this);
		butPan.add(okAddBut);
		butPan.add(cancAddBut);
		
		addPropBox.add(lblPan, BorderLayout.WEST);
		addPropBox.add(fldPan, BorderLayout.CENTER);
		addPropBox.add(butPan, BorderLayout.SOUTH);
		
		addPropBox.pack();
		addPropBox.setLocationRelativeTo(this);
		addPropBox.setVisible(true);
		
	}
	
	public void actionPerformed(ActionEvent event)
	{
		if(event.getActionCommand().equals("Add Resource"))
		{
			showAddProperty();
		}
		else if(event.getActionCommand().equals("Remove Resource"))
		{
			if(propTable.getSelectedRow() >= 0)
			{
				model.removeResource(propTable.getSelectedRow());
				model.fireTableDataChanged();
			}
		}
		else if(event.getSource() == okButton)
		{
			//TODO: this needs to be fixed once craig is done with resource stuff
			planItem.setResources(model.buildResourceMap());
//			List<JWBUID> shapes = plan.getShapeMapping(planItem.getGuid());
//            ArrayList updateList = new ArrayList();
//            for(JWBUID shapeId : shapes)
//            {
//                JWBShape shape  = controller.getShape(shapeId);
//                updateList.add(shape);
////                shape.removeMarkup('R');
////				if(planItem.getResources().size() > 0)
////                    shape.addMarkup('R');
//            }
            try{
				controller.foreignUpdate(new PlanArgument(PlanArgument.ITEM_UPDATE, planItem, false));
				dispose();
			}catch(RemoteException e){
				logger.error("actionPerformed(ok) - RemExc applying resources:  "+e.getMessage());
			}
		}
		else if(event.getSource() == cancButton)
			dispose();
		else if(event.getSource() == okAddBut)
		{
			int val = 0;
			try{
				val = Integer.parseInt(valFld.getText());

				model.addResource(nmFld.getText(), new Integer(val), contigFld.isSelected());
				model.fireTableDataChanged();
				
				addPropBox.dispose();
			}catch(NumberFormatException exc){
				JOptionPane.showMessageDialog(this, "You must enter a numeric value!");
			}
		}
		else if(event.getSource() == cancAddBut)
			addPropBox.dispose();

	}
}
