/*
 * Created on Jul 11, 2005
 */
package mil.af.rl.jcat.plan;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.Timer;

import mil.af.rl.jcat.util.Guid;

import org.apache.log4j.Logger;

import com.c3i.jwb.JWBController;
import com.c3i.jwb.JWBShape;

/**
 * Might be the start of some layout stuff
 */
public class LayoutTools implements ActionListener
{
	private JWBController controller;
	private AbstractPlan plan;
	Hashtable rowsOrColumns = new Hashtable();
	Timer tester = new Timer(1000, this);
	Dimension avgSize = null;
	private int minDist = 20;
	private int specDist = -1;
	private static Logger logger = Logger.getLogger(LayoutTools.class);
	
	public LayoutTools(JWBController jwbcont, AbstractPlan plan)
	{
		controller = jwbcont;
		this.plan = plan;
		avgSize = calcAvgSize();
		//tester.start();
	}
	
	
	public void equalizeSize()
	{
		Iterator shapes = controller.getShapes().values().iterator();
		Dimension avgSize = calcAvgSize();
		equalizeSize(avgSize);
	}
	
	public void equalizeSize(Dimension size)
	{
		Iterator shapes = controller.getShapes().values().iterator();
		ArrayList<JWBShape> updatedShapes = new ArrayList<JWBShape>();
		
		while(shapes.hasNext())
		{
			JWBShape thisShape = (JWBShape)shapes.next();
			
			if(plan.getItem((Guid)thisShape.getAttachment()) instanceof Event)
			{
				Point topLeft = thisShape.getResizePoints()[0];
				Point botRight = thisShape.getResizePoints()[4];
				thisShape.resize(botRight, new Point(topLeft.x+size.width,topLeft.y+size.height));
			
		    	updatedShapes.add(thisShape);
			}
			
			try{
				controller.putShapes(updatedShapes);
			}catch(Exception exc){   }
		}
	}
	
	public void equalizeHorizSpace()
	{
		logger.debug("equalizeHorizSpace - ------ Building row data ------");
		/* determine the average space between shapes horizontally
		 * -start at shape right most bottom most location
		 * -split board into rows from the location of first shape  
		 * -make a list of shapes for each row
		 * -go through each row list in order and apply proper spacing */
		int rowHeight = avgSize.height;
		int rowX = 0;
		int rowY = 0;
		JWBShape firstShape = null;
		
		// find the bottom right most shape to start with
		Collection vals = controller.getShapes().values();
		Iterator shapes = vals.iterator();
				
		while(shapes.hasNext())
		{
			JWBShape thisShape = (JWBShape)shapes.next();
			if(plan.getItem((Guid)thisShape.getAttachment()) instanceof Event)
			{
				int locx = thisShape.getLocation().x;
				int locy = thisShape.getLocation().y;
				
				if((locx >= rowX && locy >= rowY) || (locy > rowY))
				{
					firstShape = thisShape;
					rowX = locx;
					rowY = locy;
				}
			}
		}
		
		Vector firstRow = new Vector();
		rowsOrColumns.put(0+"", firstRow);
		
		int numRows = (firstShape.getLocation().y+firstShape.getHeight()) / rowHeight; 
		int[][] rowCoords = new int[numRows][2];
		
		//determine the first row based on first shape found above
		rowCoords[0][0] = firstShape.getLocation().y; //min in row range
		rowCoords[0][1] = firstShape.getLocation().y + rowHeight; //max in row range
		//split rest of wboard into equal rows
		for(int x=1; x<rowCoords.length; x++)
		{
			if(rowCoords[x-1][0]-rowHeight > 0-rowHeight)
			{
				rowCoords[x][1] = rowCoords[x-1][0];
				rowCoords[x][0] = rowCoords[x][1]-rowHeight;
			}
		}
		
		//sort all shapes into the rows they belong to
		shapes = vals.iterator();
		while(shapes.hasNext())
		{
			JWBShape thisShape = (JWBShape)shapes.next();
			if(plan.getItem((Guid)thisShape.getAttachment()) instanceof Event)
			{
				for(int x=0; x< rowCoords.length; x++)
				{
					int locy = thisShape.getCenterPoint().y;
					if(locy >= rowCoords[x][0] && locy <= rowCoords[x][1])
					{
						if(rowsOrColumns.get(x+"") == null)
						{
							Vector rowShapes = new Vector();
							rowsOrColumns.put(x+"", rowShapes);
						}
						((Vector)rowsOrColumns.get(x+"")).add(thisShape);
					}
				}
			}
		}
		
		orderRowDataHoriz();
		//colorize();
		adjustSpacingHoriz();
		System.gc();
	} 
	
	public void equalizeHorizSpace(int x)
	{
		specDist = x;
		equalizeHorizSpace();
	}
	
	public void equalizeVertSpace()
	{
		/* determine the average space between shapes vertically
		 * -start at shape right most top most location
		 * -split board into columns from the location of first shape  
		 * -make a list of shapes for each column
		 * -go through each column list in order and apply proper spacing */
		int columnWidth = avgSize.width;
		int columnX = 0;
		int columnY = 0;
		JWBShape firstShape = null;
		
		// find the bottom right most shape to start with
		Collection vals = controller.getShapes().values();
		Iterator shapes = vals.iterator();
				
		while(shapes.hasNext())
		{
			JWBShape thisShape = (JWBShape)shapes.next();
			if(plan.getItem((Guid)thisShape.getAttachment()) instanceof Event)
			{
				int locx = thisShape.getLocation().x;
				int locy = thisShape.getLocation().y;
				
				if((locx >= columnX && locy >= columnY) || (locx > columnX))
				{
					firstShape = thisShape;
					columnX = locx;
					columnY = locy;
				}
			}
		}
		
		Vector firstRow = new Vector();
		rowsOrColumns.put(0+"", firstRow);
		//System.out.println("firstShape:  "+firstShape.getAttachment());
		
		int numColumns = (firstShape.getLocation().x+firstShape.getWidth()) / columnWidth; 
		int[][] columnCoords = new int[numColumns][2];
		
		//determine the first row based on first shape found above
		columnCoords[0][0] = firstShape.getLocation().x; //min in row range
		columnCoords[0][1] = firstShape.getLocation().x + columnWidth; //max in row range
		//split rest of wboard into equal rows
		for(int x=1; x<columnCoords.length; x++)
		{
			if(columnCoords[x-1][0]-columnWidth > 0-columnWidth)
			{
				columnCoords[x][1] = columnCoords[x-1][0];
				columnCoords[x][0] = columnCoords[x][1]-columnWidth;
			}
		}
		
		//sort all shapes into the rows they belong to
		shapes = vals.iterator();
		while(shapes.hasNext())
		{
			JWBShape thisShape = (JWBShape)shapes.next();
			if(plan.getItem((Guid)thisShape.getAttachment()) instanceof Event)
			{
				for(int x=0; x< columnCoords.length; x++)
				{
					int locx = thisShape.getCenterPoint().x;
					if(locx >= columnCoords[x][0] && locx <= columnCoords[x][1])
					{
						if(rowsOrColumns.get(x+"") == null)
						{
							Vector rowShapes = new Vector();
							rowsOrColumns.put(x+"", rowShapes);
						}
						((Vector)rowsOrColumns.get(x+"")).add(thisShape);
					}
				}
			}
		}
		
		logger.debug("equalizeVertSpace - COLUMNS: "+rowsOrColumns.size());
		//colorize();
		//JOptionPane.showMessageDialog(null, "ready to adjust");
		orderRowDataVert();
		adjustSpacingVert();
	}

	public void equalizeVertSpace(int x)
	{
		specDist = x;
		equalizeVertSpace();
	}
	
	public Dimension calcAvgSize()
	{
		Iterator shapes = controller.getShapes().values().iterator();
		int x = 0;
		int y = 0;
		int numShapes = 0;

		while(shapes.hasNext())
		{
			JWBShape thisShape = (JWBShape)shapes.next();
			if(plan.getItem((Guid)thisShape.getAttachment()) instanceof Event)
			{
				x += thisShape.getWidth();
				y += thisShape.getHeight();
				numShapes++;
			}
		}

		return new Dimension(x/numShapes,y/numShapes);
	}

	public void orderRowDataHoriz()
	{
		logger.debug("orderRowDatahoriz - ------ Ordering row Data ------");
		// sort the row vector based on shapes position
		
		Enumeration rowCheck = rowsOrColumns.keys();
		while(rowCheck.hasMoreElements())
		{
		
			Vector thisRow = ((Vector)rowsOrColumns.get(rowCheck.nextElement()));
			Vector lastRow; //allows sort iteration to stop, when sort is complete (before loop is done)
			
			int i=0;
			do{
				lastRow = new Vector(thisRow); 
				
				for(int x=thisRow.size()-1; x>0; x--)
				{
					int xPos = ((JWBShape)thisRow.get(x)).getBounds().x;
					int xPosNext = ((JWBShape)thisRow.get(x-1)).getBounds().x;
					while(xPos > xPosNext)
					{
						thisRow.insertElementAt(thisRow.remove(x), x-1);
						xPos = ((JWBShape)thisRow.get(x)).getBounds().x;
						xPosNext = ((JWBShape)thisRow.get(x-1)).getBounds().x;
					}
				}

				i++;
				//System.out.println("-------------");
			}while(i<thisRow.size() && !thisRow.equals(lastRow));
			
			//print out row data to test the sort
			//for(int x=0; x<thisRow.size(); x++)
			//	System.out.println(((JWBShape)(thisRow.get(x))).getAttachment());
			//System.out.println("---------------------------- \n Iterations to complete sort: " +i+"\n");
		}
	}
	
	public void orderRowDataVert()
	{
		logger.debug("orderRowDataVert - ------ Ordering row Data ------");
		// sort the row vector based on shapes position
		
		Enumeration rowCheck = rowsOrColumns.keys();
		while(rowCheck.hasMoreElements())
		{
			Vector thisRow = ((Vector)rowsOrColumns.get(rowCheck.nextElement()));
			Vector lastRow; //allows sort iteration to stop, when sort is complete (before loop is done)
			
			int i=0;
			do{
				lastRow = new Vector(thisRow); 
				
				for(int x=thisRow.size()-1; x>0; x--)
				{
					int yPos = ((JWBShape)thisRow.get(x)).getBounds().y;
					int yPosNext = ((JWBShape)thisRow.get(x-1)).getBounds().y;
					while(yPos > yPosNext)
					{
						thisRow.insertElementAt(thisRow.remove(x), x-1);
						yPos = ((JWBShape)thisRow.get(x)).getBounds().y;
						yPosNext = ((JWBShape)thisRow.get(x-1)).getBounds().y;
					}
				}

				i++;
				//System.out.println("-------------");
			}while(i<thisRow.size() && !thisRow.equals(lastRow));
			
			//print out row data to test the sort
			//for(int x=0; x<thisRow.size(); x++)
			//	System.out.println(((JWBShape)(thisRow.get(x))).getAttachment());
			//System.out.println("---------------------------- \n Iterations to complete sort: " +i+"\n");
		}
	}
	
	public void colorize()
	{
		logger.debug("colorize - ------Colorize ------");
		//colorize rows (temporary for testing)
		Vector colors = new Vector();
		colors.add(Color.BLUE);   colors.add(Color.CYAN);   colors.add(Color.DARK_GRAY);   colors.add(Color.GRAY);
		colors.add(Color.LIGHT_GRAY);   colors.add(Color.MAGENTA);   colors.add(Color.ORANGE);   colors.add(Color.PINK);
		colors.add(Color.RED);   colors.add(Color.WHITE);   colors.add(Color.YELLOW);   colors.add(Color.BLACK);
		colors.add(Color.BLUE);   colors.add(Color.CYAN);   colors.add(Color.DARK_GRAY);   colors.add(Color.GRAY);
		colors.add(Color.LIGHT_GRAY);   colors.add(Color.MAGENTA);   colors.add(Color.ORANGE);   colors.add(Color.PINK);
		colors.add(Color.RED);   colors.add(Color.WHITE);   colors.add(Color.YELLOW);   colors.add(Color.BLACK);
		colors.add(Color.BLUE);   colors.add(Color.CYAN);   colors.add(Color.DARK_GRAY);   colors.add(Color.GRAY);
		colors.add(Color.LIGHT_GRAY);   colors.add(Color.MAGENTA);   colors.add(Color.ORANGE);   colors.add(Color.PINK);
		colors.add(Color.RED);   colors.add(Color.WHITE);   colors.add(Color.YELLOW);   colors.add(Color.BLACK);
		
		ArrayList<JWBShape> updatedShapes = new ArrayList<JWBShape>();
		
		Enumeration allRows = rowsOrColumns.elements();
		for(int x=0; x<rowsOrColumns.size(); x++)
		{
			Enumeration allInRow = ((Vector)allRows.nextElement()).elements();
			while(allInRow.hasMoreElements())
			{
				JWBShape thisShape = (JWBShape)allInRow.nextElement();
				thisShape.setColor((Color)colors.get(x));
		    	updatedShapes.add(thisShape);
			}
		}
		
		try{
			controller.putShapes(updatedShapes);
		}catch(Exception exc){   }
		//print out row data to test the order
		//for(int x=0; x<rows.size(); x++)
		//	System.out.println(((JWBShape)((Vector)rows.get(0+"")).get(x)).getAttachment());
	}
	
	public void adjustSpacingHoriz()
	{
		logger.debug("adjustSpacingHoriz - ------ Adjusting Rows ------");
		ArrayList<JWBShape> updatedShapes = new ArrayList<JWBShape>();
		
		Enumeration rowCheck = rowsOrColumns.keys();
		while(rowCheck.hasMoreElements())
		{
			Vector thisRow = ((Vector)rowsOrColumns.get(rowCheck.nextElement()));
			if(thisRow.size() > 2)
			{
				//move the shapes to even out the distances
				int firstX = ((JWBShape)thisRow.lastElement()).getBounds().x;
				int lastX = ((JWBShape)thisRow.firstElement()).getBounds().x;
				int length = lastX - firstX;
				int avgDist = ((length - (avgSize.width * thisRow.size())) / thisRow.size());
				if(avgDist < minDist)
					avgDist = minDist;
				if(specDist != -1)
					avgDist = specDist;
				
				for(int x=0; x<thisRow.size()-1; x++)
				{
					JWBShape thisShape = ((JWBShape)thisRow.get(x));
					JWBShape nextShape = ((JWBShape)thisRow.get(x+1));
					int curDist = thisShape.getBounds().x - nextShape.getBounds().x;
					int transDist = curDist-(avgDist+nextShape.getBounds().width);
		
					nextShape.translate(new java.awt.Point(transDist,0));
					updatedShapes.add(nextShape);
				}
				
				//shift all shapes to center the change instead of squishin it all the right
				int changeLastX = firstX - ((JWBShape)thisRow.lastElement()).getBounds().x;
				for(int x=0; x<thisRow.size(); x++)
				{
					JWBShape thisShape = ((JWBShape)thisRow.get(x));
					thisShape.translate(new java.awt.Point(changeLastX/2,0));
					if(!updatedShapes.contains(thisShape))
						updatedShapes.add(thisShape);
				}
			}
		}
		try{
			controller.putShapes(updatedShapes);
		}catch(Exception exc){   }
	}
	
	public void adjustSpacingVert()
	{
		logger.debug("adjustSpacingVert - ------ Adjusting Colums ------");
		ArrayList<JWBShape> updatedShapes = new ArrayList<JWBShape>();
		
		Enumeration rowCheck = rowsOrColumns.keys();
		while(rowCheck.hasMoreElements())
		{
			Vector thisRow = ((Vector)rowsOrColumns.get(rowCheck.nextElement()));
			if(thisRow.size() > 2)
			{
				//move the shapes to even out the distances
				int firstY = ((JWBShape)thisRow.lastElement()).getBounds().y;
				int lastY = ((JWBShape)thisRow.firstElement()).getBounds().y;
				int length = lastY - firstY;
				int avgDist = ((length - (avgSize.height * thisRow.size())) / thisRow.size()) + avgSize.height;
				if(avgDist < minDist + avgSize.height)   avgDist = minDist + avgSize.height;
				if(avgDist < minDist)
					avgDist = minDist;
				if(specDist != -1)
					avgDist = specDist;
				
				for(int x=0; x<thisRow.size()-1; x++)
				{
					JWBShape thisShape = ((JWBShape)thisRow.get(x));
					JWBShape nextShape = ((JWBShape)thisRow.get(x+1));
					int curDist = thisShape.getBounds().y - nextShape.getBounds().y;
					int transDist = curDist-avgDist;
		
					nextShape.translate(new java.awt.Point(0,transDist));
					updatedShapes.add(nextShape);
				}
				
				//shift all shapes to center the change instead of squishin it all the right
				int changeLastY = firstY - ((JWBShape)thisRow.lastElement()).getBounds().y;
				//for(int x=0; x<thisRow.size(); x++)
				//{
				//	JWBShape thisShape = ((JWBShape)thisRow.get(x));
				//	thisShape.translate(new java.awt.Point(0,changeLastY/2));
				//	try{ controller.putShape(thisShape); }catch(Exception exc){}
				//}
			}
		}
		
		try{
			controller.putShapes(updatedShapes);
		}catch(Exception exc){   }
	}
	
	
	//for testing
	public void actionPerformed(ActionEvent arg0)
	{
		equalizeHorizSpace();
	}
}
