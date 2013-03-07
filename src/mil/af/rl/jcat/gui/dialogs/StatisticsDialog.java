/*
 * Created on Oct 18, 2005
 */
package mil.af.rl.jcat.gui.dialogs;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.JButton;

import mil.af.rl.jcat.gui.table.model.NoEditTableModel;

public class StatisticsDialog extends JDialog implements ActionListener
{

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JScrollPane infoScroll = null;
	private JTable infoTable = null;
	private JLabel topLbl = null;
	private JPanel buttonPanel = null;
	private JButton okButton = null;
	
	public StatisticsDialog(java.awt.Frame parent)
	{
		super(parent, "Model Statistics");
		initialize();
		setLocationRelativeTo(parent);
		
		
		setVisible(true);
	}

	private void initialize()
	{
		this.setSize(400, 350);
			
		infoTable = new JTable(0,2);
		infoTable.setModel(new NoEditTableModel(new String[]{"",""}, 0, null));
		infoTable.setRowSelectionAllowed(false);
		infoTable.setRowHeight(25);
		infoTable.setCellSelectionEnabled(false);
		infoTable.setShowGrid(false);
//		infoTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//		infoTable.getColumnModel().getColumn(0).setPreferredWidth(getSize().width/3);
		topLbl = new JLabel("");
		infoScroll = new JScrollPane();
		infoScroll.setViewportView(infoTable);
		infoScroll.setBorder(BorderFactory.createBevelBorder(1));
		okButton = new JButton("Close");
		okButton.addActionListener(this);
		jContentPane = new JPanel();
		buttonPanel = new JPanel();
		buttonPanel.add(okButton);
		this.setContentPane(jContentPane);

		jContentPane.setLayout(new BorderLayout());
		jContentPane.add(topLbl, java.awt.BorderLayout.NORTH);
		jContentPane.add(buttonPanel, java.awt.BorderLayout.SOUTH);
		jContentPane.add(infoScroll, java.awt.BorderLayout.CENTER);
	}
	
	public void addStat(String name, String value)
	{
		((NoEditTableModel)infoTable.getModel()).addRow(new String[]{name, value});
		//infoTable.setValueAt(name, infoTable.getRowCount(), 0);
		//infoTable.setValueAt(value, infoTable.getRowCount(), 1);
	}

	public void actionPerformed(ActionEvent event)
	{
		if(event.getSource() == okButton)
		{
			dispose();
		}
	}
}
