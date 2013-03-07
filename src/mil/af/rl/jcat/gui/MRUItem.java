/*
 * Created on Jun 7, 2005
 *
 * Created simply to identify mru items using instanceof, distinguish from other jmenuitems
 */
package mil.af.rl.jcat.gui;

import javax.swing.JMenuItem;

/**
 * @author dygertm
 */
public class MRUItem extends JMenuItem
{
	private static final long serialVersionUID = 1L;

	public MRUItem(String txt)
	{
		super(txt);
	}
}
