/*
 * Created on Mar 17, 2007
 */
package mil.af.rl.jcat.util;

import java.awt.Color;
import java.awt.Component;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;


public class JColoredIndexList extends JList
{

	private Color playForeground = Color.GREEN;
	private int[] colorIndicies = new int[]{};

	
	public JColoredIndexList(Vector<?> listData)
	{
		this();
		setListData(listData);
	}
	
	public JColoredIndexList()
	{
		super();
		setCellRenderer(new ColorListCellRenderer());
	}

	
	public Color getColoredForeground()
	{
		return playForeground;
	}

	public void setColoredForeground(Color newColor)
	{
		playForeground = newColor;
	}


	public void setColoredIndices(int[] ind)
	{
		colorIndicies = ind;
	}
	
	public int[] getColoredIndices()
	{
		return colorIndicies;
	}

	
	
	private class ColorListCellRenderer extends DefaultListCellRenderer implements ListCellRenderer
	{

		public ColorListCellRenderer()
		{
			super();
		}

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
		{
			Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			
			if(list instanceof JColoredIndexList && !isSelected)
			{
				JColoredIndexList pList = (JColoredIndexList)list;
				
				if(lookForIndex(index, pList.getColoredIndices()))
					comp.setForeground(pList.getColoredForeground());
				else
					comp.setForeground(pList.getForeground());
			}
			
			return comp;
		}

		private boolean lookForIndex(int index, int[] coloredIndices)
		{
			for(int i : coloredIndices)
				if(i == index)
					return true;
			return false;			
		}

		
	}

}
