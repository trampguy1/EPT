package mil.af.rl.jcat.gui.documentationpanels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import mil.af.rl.jcat.util.Resource;

public class URLDialog extends JDialog
{
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(URLDialog.class);
	JLabel jLabel1 = new JLabel();
	JTextField txtURL = new JTextField();
	JButton btnOK = new JButton();
	JButton btnCancel = new JButton();
	GridBagLayout gridBagLayout1 = new GridBagLayout();
	String value = new String();
	Resource resource = null;

	
	public URLDialog(Resource r)
	{
		super((JFrame) null, "URL", true);

		resource = r;
		try
		{
			jbInit();
		}
		catch (Exception e)
		{
			logger.error("Constructor - error initializing dialog: ",e);
		}

		this.pack();
		this.setVisible(true);
	}

	private void jbInit() throws Exception
	{
		jLabel1.setText("Enter URL:");
		this.getContentPane().setLayout(gridBagLayout1);
		txtURL.setText("");
		btnOK.setText("OK");
		btnOK.addActionListener(new URLDialog_jButton1_actionAdapter(this));
		btnCancel.setText("Cancel");
		btnCancel.addActionListener(new URLDialog_jButton2_actionAdapter(this));
		this.getContentPane().add(
				txtURL,
				new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
						new Insets(12, 8, 0, 15), 232, 0));
		this.getContentPane().add(
				jLabel1,
				new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						new Insets(12, 10, 0, 0), 9, 0));
		this.getContentPane().add(
				btnCancel,
				new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(19, 47, 17, 73), 6, 0));
		this.getContentPane().add(
				btnOK,
				new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(19, 0, 17, 0), 24, 0));
	}

	void jButton1_actionPerformed(ActionEvent e)
	{

		resource.setLocation(txtURL.getText());
		this.dispose();
	}

	void jButton2_actionPerformed(ActionEvent e)
	{
		this.dispose();
	}
}

class URLDialog_jButton1_actionAdapter implements java.awt.event.ActionListener
{
	URLDialog adaptee;

	URLDialog_jButton1_actionAdapter(URLDialog adaptee)
	{
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e)
	{
		adaptee.jButton1_actionPerformed(e);
	}
}

class URLDialog_jButton2_actionAdapter implements java.awt.event.ActionListener
{
	URLDialog adaptee;

	URLDialog_jButton2_actionAdapter(URLDialog adaptee)
	{
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e)
	{
		adaptee.jButton2_actionPerformed(e);
	}
}