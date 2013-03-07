package mil.af.rl.jcat.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import mil.af.rl.jcat.plan.ResourceManager;
import mil.af.rl.jcat.util.IntegerTextField;

public class DefineResourceDialog extends JDialog implements ActionListener {

	//Resource Manager corresponding to the AbstractPlan instance we are working with
	ResourceManager manager;
	//Dialog stuff
	JTextField nameArea = new JTextField();
	IntegerTextField available = new IntegerTextField();
	JComboBox type = new JComboBox(new String[]{"Choose a Type","Threat", "Operational"});
	
	public DefineResourceDialog(Frame parent, ResourceManager manager)
	{
		super(parent, "Define New Resource");
		this.manager = manager;
		this.getContentPane().setLayout(new BorderLayout());
        JPanel panel = new JPanel(new GridLayout(6,1));
        JLabel na = new JLabel("Name: ");
        na.setPreferredSize(new Dimension(30, 20));
        panel.add(na);
        nameArea.setPreferredSize(new Dimension(60,20));
        panel.add(nameArea);      
        JLabel av = new JLabel("Available: ");
        av.setPreferredSize(new Dimension(30, 20));
        panel.add(av);
        available.setPreferredSize(new Dimension(60,20));
        panel.add(available);
        JLabel ty = new JLabel("Type: ");
        ty.setPreferredSize(new Dimension(30, 20));
        panel.add(ty);
        type.setPreferredSize(new Dimension(60,20));
        panel.add(type);
        this.getContentPane().add(panel, BorderLayout.CENTER);
        JPanel btnPanel = new JPanel();
        JButton create = new JButton("Create Resource");
        JButton cancel = new JButton("Cancel");
        create.addActionListener(this);
        cancel.addActionListener(this);
        btnPanel.add(create);
        btnPanel.add(cancel);
        this.getContentPane().add(btnPanel, BorderLayout.SOUTH);		
		this.setSize(400, 200);
        this.setResizable(false);
        this.setLocationRelativeTo(parent);
        this.setVisible(true);
	}

    public void actionPerformed(ActionEvent e)
    {
        if(e.getActionCommand() == "Create Resource")
        {
            int index = type.getSelectedIndex();
            if(index == 0)
            {
                // Notify to Select a type
            }
            else if(index == 1)
            {
                manager.createThreatResource(nameArea.getText(), Integer.parseInt(available.getText()));
                this.dispose();
            }
            else if(index == 2)
            {
                manager.createOperationalResource(nameArea.getText(), Integer.parseInt(available.getText()));
                this.dispose();
            }           
        }
        else if(e.getActionCommand() == "Cancel")
        {
            this.dispose();
        }
    }
	
}
