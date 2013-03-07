package mil.af.rl.jcat.gui.dialogs;

import java.awt.Component;

import javax.swing.JDialog;

import mil.af.rl.jcat.plan.PlanItem;

import com.c3i.jwb.JWBShape;

public class ResourcePriorityDialog extends JDialog {
	
	public ResourcePriorityDialog(Component parent, JWBShape shape, PlanItem plaitem)
	{
		this.setSize(300, 400);
		this.setVisible(true);
	}

}
