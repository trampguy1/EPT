package mil.af.rl.jcat.gui.dialogs;

import javax.swing.*;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import mil.af.rl.jcat.gui.MainFrm;
import java.awt.Insets;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class TextEntryDialog extends JDialog
{
	private static final long serialVersionUID = 1L;
	JTextArea textArea = new JTextArea();
    JScrollPane scrollPane = new JScrollPane();
    GridBagLayout layout = new GridBagLayout();

    public TextEntryDialog(JTextArea tArea)
    {
        super(MainFrm.getInstance(), "Description Text", true);
        try
        {
            textArea = tArea;
            init();
            setLocationRelativeTo(MainFrm.getInstance());
        }
        catch (Exception i)
        {

        }
        this.setModal(true);

    }

    private void init() throws Exception
    {
        scrollPane.setViewportView(textArea);
        this.setSize(425, 375);
        this.getContentPane().setLayout(layout);
        this.getContentPane().add(
                scrollPane,
                new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));

    }

}
