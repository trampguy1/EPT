package mil.af.rl.jcat.gui.documentationpanels;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import mil.af.rl.jcat.plan.Documentation;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */

public class KeyWordPanel extends JPanel implements MouseListener, ActionListener, KeyListener
{
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(KeyWordPanel.class);
	JLabel jLabel1 = new JLabel();
	DefaultListModel keyWordListModel = new DefaultListModel();
	JList lstKeyWords = new JList(keyWordListModel);
	JButton btnInsert = new JButton();
	JMenuItem miDelete = new JMenuItem();
	JTextField txtNewWord = new JTextField();
	GridBagLayout gridBagLayout1 = new GridBagLayout();
	JScrollPane scrollPane = new JScrollPane();
	protected Documentation masterDocumentation = null;
	protected String ItemName = "";
	JPopupMenu popup = new JPopupMenu();


	public KeyWordPanel(String itemName, Documentation document)
	{
		masterDocumentation = document;
		ItemName = itemName;
		LinkedList keyWords = masterDocumentation.getKeyWords();
		try
		{
			init();
		}
		catch (Exception e)
		{
			logger.error("Constructor - error initializing dialog: ",e);
		}
		for (int i = 0; i < keyWords.size(); i++)
		{
			keyWordListModel.addElement((String) keyWords.get(i));
		}

		popup.add(miDelete);

	}

	private void init() throws Exception
	{
		scrollPane.setViewportView(lstKeyWords);
		txtNewWord.setText("");
		jLabel1.setText("Key Words:");
		jLabel1.setBounds(new Rectangle(58, 37, 57, 15));
		this.setLayout(gridBagLayout1);
		lstKeyWords.setBorder(BorderFactory.createLineBorder(Color.black));
		lstKeyWords.setBounds(new Rectangle(31, 128, 326, 162));

		lstKeyWords.addMouseListener(this);

		miDelete.setText("Delete");
		miDelete.setActionCommand("Delete");
		miDelete.addActionListener(this);

		btnInsert.setBounds(new Rectangle(238, 31, 59, 25));
		txtNewWord.setBounds(new Rectangle(135, 34, 96, 21));
		txtNewWord.addKeyListener(this);
		btnInsert.setText("Insert");
		btnInsert.setActionCommand("Insert");
		btnInsert.addActionListener(this);

		this.addMouseListener(this);

		this.add(txtNewWord, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(18, 52, 0, 0), 208, 1));
		this.add(btnInsert, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						18, 7, 0, 52), 14, -2));
		this.add(scrollPane, new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						12, 17, 13, 16), 367, 235));
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

	//Action Listener
	public void actionPerformed(ActionEvent e)
	{
		if (e.getActionCommand().equals("Insert"))
		{
			String currentEntry = txtNewWord.getText();
			Vector vParsed = parseString(currentEntry);

			for (int i = 0; i < vParsed.size(); i++)
			{
				String newWord = (String) vParsed.get(i);
				masterDocumentation.insertKeyWord(newWord);
				keyWordListModel.addElement(newWord);
			}
			txtNewWord.setText("");
		}
		else if (e.getActionCommand().equals("Delete"))
		{
			int sel = lstKeyWords.getSelectedIndex();
			keyWordListModel.remove(sel);
			LinkedList keyWords = masterDocumentation.getKeyWords();
			keyWords.remove(sel);
		}
	}

	// mouse listeners
	public void mouseReleased(MouseEvent e)
	{
//		if(e.getSource() == btnInsert)
//		{
//		masterDocumentation.getKeyWords().add(txtNewWord.getText());
//		keyWordListModel.addElement(txtNewWord.getText());
//		}
	}

	public void mouseClicked(MouseEvent e)
	{
		if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0)
		{
			popup.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	// KeyListener events
	public void keyPressed(KeyEvent event)
	{
		if(event.getSource() == txtNewWord && event.getKeyCode() == KeyEvent.VK_ENTER)
			btnInsert.doClick();
	}

	// unused keyevents
	public void keyTyped(KeyEvent e){}
	public void keyReleased(KeyEvent e){}

	// unused mouse-events
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mousePressed(MouseEvent e){}


}
