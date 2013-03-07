package mil.af.rl.jcat.gui.dialogs;

import java.awt.*;
import java.awt.event.*;
import java.awt.Frame;
import javax.swing.*;

import org.apache.log4j.Logger;

/**
 * <p>Title: PswdDialog.java</p>
 * <p>Description: Used like the JOptionPane class to prompt for a dialog</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: C3I Associates</p>
 *
 * @author Edward Verenich
 * @version 1.0
 */
public class PswdDialog extends JDialog implements ActionListener, KeyListener
{

	private static final long serialVersionUID = 1L;
	private static PswdDialog pdialog = null;
	private JPasswordField pfield = new JPasswordField(10);
	// don't touch the line below yo..
	private JLabel text = new JLabel("Enter pass phrase below:          ");
	private static String password = null;
	private JButton ok = new JButton("OK");
	private JButton cancel = new JButton("Cancel");
	private JPanel panel1 = new JPanel();
	private static Logger logger = Logger.getLogger(PswdDialog.class);


	private PswdDialog(Component owner, String title, boolean modal) 
	{
		super();
		try {
			setTitle(title);
			setModal(true);
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			jbInit();
			pack();
			pfield.addKeyListener(this);
		} catch (Exception exception) {
			logger.error("Constructor - error initializing dialog");
		}
	}

	private PswdDialog() 
	{
		this(new Frame(), "PswdDialog", false);
	}

	private void jbInit() throws Exception
	{
		panel1.setLayout(new BorderLayout());
		JPanel bPanel = new JPanel();
		JPanel center = new JPanel();
		center.setLayout(new GridLayout(2,1));
		center.add(text);
		center.add(pfield);
		ok.addActionListener(this);
		cancel.addActionListener(this);
		bPanel.add(ok);
		bPanel.add(cancel);
		JLabel iLabel = new JLabel(new ImageIcon(this.getClass().getClassLoader().getResource("key.png")));
		panel1.add(iLabel,BorderLayout.WEST);
		panel1.add(bPanel,BorderLayout.SOUTH);
		panel1.add(center,BorderLayout.CENTER);

		getContentPane().add(panel1);
	}

	public static String showPswdDialog(Component parent, String title)
	{

		if(pdialog == null)
		{
			pdialog = new PswdDialog(parent,title,true);
		}
		if(parent != null)
		{
			pdialog.setLocation((int) parent.getLocationOnScreen().getX() + 50,
					(int) parent.getLocationOnScreen().getY() + 100);
		}
		pdialog.setVisible(true);
		return password;
	}

	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource().equals(ok))
		{
			password = new String(pfield.getPassword());
			pfield.setText(null);
			dispose();
		}else if(e.getSource().equals(cancel))
		{
			password = null;
			pfield.setText(null);
			dispose();
		}
	}

	public void keyPressed(KeyEvent e)
	{
		if(e.getSource() == pfield && e.getKeyCode() == KeyEvent.VK_ENTER)
			ok.doClick();
	}


	//unused key-events

	public void keyTyped(KeyEvent e){}
	public void keyReleased(KeyEvent e){}



}
