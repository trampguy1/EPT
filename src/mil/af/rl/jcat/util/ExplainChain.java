/*
 * Created on Mar 31, 2006
 * Author: Mike D
 * ExplainChain.java - A chain of events(explainpathitems) used within explaination
 */
package mil.af.rl.jcat.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.LayoutManager;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.apache.log4j.Logger;

public class ExplainChain implements Comparable
{
	private Vector<ExplainPathItem> items = new Vector<ExplainPathItem>();
	private float causeProb = 0f;
	private JPanel panel;
	private boolean isInhibitor;
	private static Logger logger = Logger.getLogger(ExplainChain.class); 
	
	public ExplainChain()
	{
		panel = new JPanel();
	}

	public void addItem(ExplainPathItem item)
	{
		items.add(item);
		add(item.getComponent());
	}
	
	public void add(Component item)
	{
		panel.add(item);
	}
	
	public void setLayout(LayoutManager lm)
	{
		panel.setLayout(lm);
	}
	
	public void setCauseProb(float prob, boolean isInhib)
	{
		causeProb = prob;
		isInhibitor = isInhib;
		NumberFormat nf = java.text.NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		String chainProb = nf.format(causeProb);
		panel.setBorder(BorderFactory.createTitledBorder("Chain importance: "+chainProb+ (isInhib?" [Inhibitor]":"")));
		panel.updateUI();
	}
	
	public JComponent getPanel()
	{
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setPreferredSize(new java.awt.Dimension(180, 100));
		scrollPane.setViewportView(panel);
		return scrollPane;
	}
	
	public void grayOutItems()
	{
		Iterator allItems = items.iterator();
		while(allItems.hasNext())
			((ExplainPathItem)allItems.next()).setBackground(java.awt.Color.GRAY);
	}
	
	public void darkOutItems()
	{
		Iterator allItems = items.iterator();
		while(allItems.hasNext())
			((ExplainPathItem)allItems.next()).setBackground(Color.RED);  //new Color(54,60,164));
	}
	
	public float getCauseProb()
	{
		return causeProb;
	}
	
	public boolean isInhibitor()
	{
		return isInhibitor;
	}
	
	public Vector getItems()
	{
		return items;
	}
	
	public boolean compareChainProb(ExplainChain inChain)
	{
		if(inChain.getCauseProb()-.01f < this.causeProb && inChain.getCauseProb()+.01f > this.causeProb)
			return true;
		else
		{
			logger.debug("compareChainProb - CHAIN STILL EXISTS BUT HAS CHANGED!!");
			return false;
		}
	}
	
	public boolean equals(Object obj)
	{
//		if(!(obj instanceof ExplainChain))
//			return false;
//		else
//		{
			ExplainChain inObj = (ExplainChain)obj;
			//compare items of both chains, check their causeProb and items
			//compare the prob of the chains, within a tolerance
			if(!(inObj.getItems().equals(this.getItems())))
			{
				//System.err.println("   items are not equal");
				return false;
			}
			
//			Iterator itemsIt = inObj.getItems().iterator();
//			while(itemsIt.hasNext())
//			{
//				ExplainPathItem item = (ExplainPathItem)itemsIt.next();
//				if(!this.items.contains(item))
//				{
//					System.err.println("items does not contain:  "+item);
//					return false;
//				}
//				else
//					System.out.println("items contains:  "+item);
//			}
			
//			if(inObj.getCauseProb()-.01f < this.causeProb && inObj.getCauseProb()+.01f > this.causeProb)
//			//if(inObj.getCauseProb() == this.causeProb)
//			{
//				return true;
//			}
//			else
//			{
//				return false;
//			}
			return true;
		//}
	}
	
	public void removeAll()
	{
		panel.removeAll();		
	}

	public int compareTo(Object inObj)
	{
//		if(!(inObj instanceof ExplainChain))
//			return -1;
		
		ExplainChain inChain = (ExplainChain)inObj;
		
		if(this.causeProb > inChain.getCauseProb())
			return -1;
		else if(this.causeProb == inChain.getCauseProb())
			return 0;
		else return 1;
	}
}
