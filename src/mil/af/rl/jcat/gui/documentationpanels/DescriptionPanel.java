package mil.af.rl.jcat.gui.documentationpanels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;

import mil.af.rl.jcat.exceptions.MissingRequiredFileException;
import mil.af.rl.jcat.gui.MainFrm;
import mil.af.rl.jcat.plan.Documentation;

import mil.af.rl.jcat.util.SpellChecker;

import java.awt.event.ActionListener;

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

public class DescriptionPanel extends JPanel implements ActionListener
{
	private static final long serialVersionUID = 1L;

	JLabel jLabel1 = new JLabel();
	JLabel jLabel2 = new JLabel();
	JLabel jLabel3 = new JLabel();
	JTextArea descriptionText = new JTextArea(10, 10);
	JButton btnApply = new JButton();
	GridBagLayout gridBagLayout1 = new GridBagLayout();
	JScrollPane scrollPane = new JScrollPane();
	protected Documentation masterDocumentation = null;
	protected String ItemName = "";
	private JButton spellButton = null;
	private static Logger logger = Logger.getLogger(DescriptionPanel.class);


	public DescriptionPanel(String itemName, Documentation document)
	{
		masterDocumentation = document;
		ItemName = itemName;
		try
		{
			init();
		}
		catch (Exception e)
		{
			logger.error("Constructor - error initializing dialog: ",e);
		}
		descriptionText.setText(masterDocumentation.getDescription());

	}

	private void init() throws Exception
	{
		descriptionText.setLineWrap(true);
		scrollPane.setViewportView(descriptionText);

		jLabel1.setText("Name:");
		this.setLayout(gridBagLayout1);
		jLabel2.setText(ItemName);
		descriptionText.setText("");
		jLabel3.setText("Description:");
		spellButton = new JButton(new javax.swing.ImageIcon(this.getClass().getClassLoader().getResource("spellcheck.png")));
		spellButton.setMargin(new Insets(2,2,2,2));
		spellButton.addActionListener(this);
		spellButton.setToolTipText("Check Spelling");

		this.add(jLabel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(
						12, 37, 0, 0), 22, 7));
		this.add(jLabel2, new GridBagConstraints(1, 0, 2, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(
						12, 12, 0, 75), 224, 22));
		this.add(scrollPane, new GridBagConstraints(0, 2, 3, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 21, 0, 19), 360, 188));
		this.add(jLabel3, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,
						36, 0, 0), 17, 3));
		this.add(spellButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, 
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,0,0,30), 0, 0));
	}

	public void saveDescription()
	{
		masterDocumentation.insertDescription(descriptionText.getText());
	}


	public void actionPerformed(ActionEvent event)
	{
		if(event.getSource() == spellButton)
		{
			try{
				SpellChecker.check(descriptionText);
			}catch(MissingRequiredFileException exc){
				JOptionPane.showMessageDialog(MainFrm.getInstance(), "Spell check cannot start. \n"+exc.getMessage());
				logger.error("actionPerformed(spellcheck) - could not start:  "+exc.getMessage());
			}
		}
	}

}
