/*
 * Created on Mar 14, 2006
 * Author: Mike D
 * ExplainPathItem.java - 
 */
package mil.af.rl.jcat.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JButton;

public class ExplainPathItem
{

	private Object userObject = null;
	private float causeProb = 0f;
	private String name = "";
	private JButton guiComp;

	public ExplainPathItem(Object item, float prob, boolean isEvent)
	{
		guiComp = new JButton("<html><center>"+item.toString()+"</center></html>");
		guiComp.setBackground(new Color(0, 128, 255));
		//guiComp.setUI(javax.swing.plaf.metal.MetalButtonUI.createUI(guiComp));
		guiComp.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		guiComp.setBorderPainted(isEvent);
		guiComp.setFont(guiComp.getFont().deriveFont(1));
		guiComp.setToolTipText(item.toString());
		guiComp.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		//guiComp.setMargin(new java.awt.Insets(10,10,10,10)); //ignored when non-default border
				
		guiComp.setContentAreaFilled(false);
		guiComp.setOpaque(isEvent);
				
				
		userObject = item;
		causeProb = prob;
		name = item.toString();
	}
	
	public Object getUserObject()
	{
		return userObject;
	}

	public String getName()
	{
		return name;
	}
	
	public float getProb()
	{
		return causeProb;
	}
	
	public boolean equals(Object obj)
	{
//		if(!(obj instanceof ExplainPathItem))
//			return false;
//		else
//		{
			ExplainPathItem inObj = (ExplainPathItem)obj;
			if(!(inObj.getName().equals(this.name)))
			{
				//System.out.println("      item name not equal");
				return false;
			}
//			else if(inObj.getProb() == this.causeProb) //inObj.getProb()-.01f < this.causeProb && inObj.getProb()+.01f > this.causeProb) //allow for tollerance of samples
//				return false;
			else
			{
				//System.out.println("      item name is equal");
				return true;
			}
		//}
	}

	public void setPreferredSize(Dimension dimension)
	{
		guiComp.setPreferredSize(dimension);
	}

	public Component getComponent()
	{
		return guiComp;
	}

	public void setBackground(Color clr)
	{
		guiComp.setBackground(clr);
	}
	
	
}
