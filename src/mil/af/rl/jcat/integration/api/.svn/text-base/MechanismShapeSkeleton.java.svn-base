/*
 * Provided with JCAT API to eliminate need for JWhiteboard jar, resource bundles and classpath settings
 * Used only in non-functional way to produce XML graph elements with API with least amount of code differences
 * in between API Control and JCAT Control (at least in the XML file generation part)  
 */

package mil.af.rl.jcat.integration.api;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.GeneralPath;

import com.c3i.jwb.JWBUID;

/**
 * A line implementation of JWBShapeSkeleton.
 * <BR><BR>
 *
 * Copyright (c) 2003-2005, by C3I Associates, ALL RIGHTS RESERVED
 *
 * @author Francis Conover ( for C3I Associates )
 * @version JWhiteBoard 1.0.0
 */
public final class MechanismShapeSkeleton extends AbstractShapeSkeleton
{

	private JWBUID[] linkedShapes = new JWBUID[2];

	private int direction = 1;

	private int lineStyle = 0;

	private int lineType = 0;

	/**
	 * Specifies that no arrows be drawn on the line.
	 */
	public static final int INDIRECTION = 0;

	/**
	 * Specifies that one directional arrow should be drawn on the line.
	 */
	public static final int DIRECTIONAL = 1;

	/**
	 * Specifies that both ends of the line should have a directional arrow.
	 */
	public static final int BIDIRECTIONAL = 2;

	/**
	 * Specifies that the line should be drawn solid.
	 */
	public static final int SOLID = 0;

	/**
	 * Specifies that the line should be drawn with dashes.
	 */
	public static final int DASHED = 1;

	/**
	 * Specifies that the line should be drawn with straights.
	 */
	public static final int STRAIGHT = 0;

	/**
	 * Specifies that the line should be drawn with arcs.
	 */
	public static final int ARCED = 1;

	/**
	 * The point at which this shape's text is drawn.
	 */
	protected Point textPoint = null;

	private boolean showText = false;
	
	/**
	 * Creates a new JWBLine object.
	 *
	 * @param startingShape   the shape to start drawing from
	 * @param endingShape     the shape to draw to
	 * @param uid             the uid with which this shape is to be associated
	 */
	public MechanismShapeSkeleton(JWBShapeSkeleton startingShape, JWBShapeSkeleton endingShape, JWBUID uid, boolean shoText)
	{
		this.type = "com.c3i.jwb.JWBLine";
		this.uid = uid;
		this.color = Color.BLACK;
		this.linkable = false;
		this.magnifiable = false;
		this.showText = shoText;

		linkedShapes[0] = startingShape.getUID();
		linkedShapes[1] = endingShape.getUID();

		resizePoints = new Point[3];
		rebuildEndPoints(startingShape, endingShape);
		textPoint = new Point(resizePoints[1].x, resizePoints[1].y);

		location = getLocation();
		width = getWidth();
		height = getHeight();
	}

	/**
	 * Tests if the specified point is on or inside the boundary of the shape.
	 *
	 * @param point   the specified point
	 * @return        true if the specified point is on or inside the shape boundary
	 */
	public boolean contains(Point point)
	{
		boolean contains = false;

		for (int i = 0; i < resizePoints.length - 1; i++)
		{
			if ((new Line2D.Double(resizePoints[i], resizePoints[i + 1]))
					.ptSegDist(point) < 10)
			{
				contains = true;
				break;
			}
		}

		if (!contains)
		{
			return isTextPoint(point);
		}
		else
		{
			return true;
		}
	}

	/**
	 * Draws an arrow onto a given graphics object.
	 *
	 * @param g    the graphics object to draw to
	 * @param x1   the point of reference x coordinate
	 * @param y1   the point of reference y coordinate
	 * @param x2   the arrow tip x coordinate
	 * @param y2   the arrow tip y coordinate
	 */
	public static void drawArrow(Graphics g, int x1, int y1, int x2, int y2)
	{
		double deltaX = x2 - x1;
		double deltaY = y2 - y1;
		double theta = Math.atan(deltaY / deltaX);

		if (deltaX < 0.0)
		{
			theta += Math.PI;
		}

		int lengthX = -(int) (Math.cos(theta) * 9);
		int lengthY = -(int) (Math.sin(theta) * 9);
		int widthX = (int) (Math.sin(theta) * 5);
		int widthY = (int) (Math.cos(theta) * 5);

		g.fillPolygon(new int[] { x2, x2 + lengthX + widthX,
				x2 + lengthX - widthX }, new int[] { y2, y2 + lengthY - widthY,
				y2 + lengthY + widthY }, 3);
	}

	/**
	 * Used to draw the markups of a shape onto a given graphics object.
	 *
	 * @param graphics   the graphics object to draw to
	 */
	public void drawMarkups(Graphics2D graphics)
	{
		if (markup && markups.length() > 0)
		{
			graphics.setFont(new Font("Courier New", 1, 12));
			Point markupPoint = resizePoints[resizePoints.length / 2];
			for (int i = 0; i < markups.length(); i++)
			{
				graphics.setColor(new Color(255, 255, 150));
				graphics.fillRect(markupPoint.x + 10 + 16 * i,
						markupPoint.y - 6, 13, 13);
				graphics.setColor(Color.BLACK);
				graphics.drawRect(markupPoint.x + 10 + 16 * i,
						markupPoint.y - 6, 13, 13);
				graphics.drawString(String.valueOf(markups.charAt(i)),
						markupPoint.x + 13 + 16 * i, markupPoint.y + 4);
			}
		}
	}

	/**
	 * Used to draw the resize points of a shape onto a given graphics object.
	 *
	 * @param graphics   the graphics object to draw to
	 */
	public void drawResizePoints(Graphics2D graphics)
	{
		if (!hidden)
		{
			for (int i = 1; i < resizePoints.length - 1; i++)
			{
				if (i % 2 == 0)
				{
					graphics.drawOval(resizePoints[i].x - 3,
							resizePoints[i].y - 3, 6, 6);
				}
				else
				{
					graphics.drawRect(resizePoints[i].x - 3,
							resizePoints[i].y - 3, 6, 6);
				}
			}

			//      if( text.length( ) > 0 ) {
			//        graphics.drawRect( textPoint.x - 3, textPoint.y - 3, 6, 6 );
			//      }
		}
	}

	/**
	 * Used to draw a shape onto a given graphics object.
	 *
	 * @param graphics   the graphics object to draw to
	 */
	public void drawShape(Graphics2D graphics)
	{
		if (!hidden)
		{
			graphics.setColor(color);

			// set line style
			if (lineStyle == DASHED)
			{
				graphics.setStroke(new BasicStroke(1f, 2, 0, 1, new float[] { 5 }, 0));
			}
			else
			{
				graphics.setStroke(new BasicStroke(1f));
			}

			if (lineType == STRAIGHT)
			{
				for (int i = 0; i < resizePoints.length - 1; i++)
				{
					graphics.draw(new Line2D.Double(resizePoints[i],
							resizePoints[i + 1]));
				}
			}
			else
			{
				int length = resizePoints.length - 1;
				GeneralPath gPath = new GeneralPath();
				gPath.moveTo((float) resizePoints[0].x,
						(float) resizePoints[0].y);
				if (resizePoints.length == 3)
				{
					gPath.lineTo((float) resizePoints[2].x,
							(float) resizePoints[2].y);
				}
				else
				{
					int i;
					for (i = 4; i <= length; i += 4)
					{
						gPath.curveTo((float) resizePoints[i - 2].x,
								(float) resizePoints[i - 2].y,
								(float) resizePoints[i - 2].x,
								(float) resizePoints[i - 2].y,
								(float) resizePoints[i].x,
								(float) resizePoints[i].y);
					}
					if (i != length)
					{
						gPath.lineTo((float) resizePoints[length].x,
								(float) resizePoints[length].y);
					}
				}
				graphics.draw(gPath);
			}

			graphics.setStroke(new BasicStroke());

			if (direction == DIRECTIONAL)
			{
				drawArrow(graphics, resizePoints[resizePoints.length - 2].x,
						resizePoints[resizePoints.length - 2].y,
						resizePoints[resizePoints.length - 1].x,
						resizePoints[resizePoints.length - 1].y);
			}
			else if (direction == BIDIRECTIONAL)
			{
				drawArrow(graphics, resizePoints[1].x, resizePoints[1].y,
						resizePoints[0].x, resizePoints[0].y);
				drawArrow(graphics, resizePoints[resizePoints.length - 2].x,
						resizePoints[resizePoints.length - 2].y,
						resizePoints[resizePoints.length - 1].x,
						resizePoints[resizePoints.length - 1].y);
			}

			drawText(graphics);
			drawMarkups(graphics);
		}
	}

	/**
	 * Used to draw the text of a shape onto a given graphics object.
	 * 
	 * @param graphics the graphics object to draw to
	 */
	public void drawText(Graphics2D graphics)
	{
		if(showText)
		{
			// configure graphics 
			graphics.setFont(font);
			graphics.setColor(textColor);

			// replace \n's with a space
			String formattedText = text.replace('\n', ' ');

			// center text along line with three resize points
			if (resizePoints.length == 3)
			{
				// draw text 
				int midPoint = resizePoints.length / 2;
				double slope = (resizePoints[midPoint].y - resizePoints[midPoint + 1].y)
						* 1.0
						/ (resizePoints[midPoint].x - resizePoints[midPoint + 1].x)
						* 1.0;
				int stringWidth = graphics.getFontMetrics().stringWidth(
						formattedText);
				double theta = Math.atan(slope);

				graphics.setFont(font.deriveFont(AffineTransform
						.getRotateInstance(theta)));

				// line is in 2nd and 4th quadrants
				if (slope >= 0)
				{
					int y = Math.abs(new Double((stringWidth / 2.0)
							* Math.sin(theta)).intValue());
					int x = Math.abs(new Double((stringWidth / 2.0)
							* Math.cos(theta)).intValue());
					graphics.drawString(formattedText, resizePoints[midPoint].x
							- x, resizePoints[midPoint].y - y);

					// line is in 1st and 3rd quadrants  
				}
				else
				{
					int x = Math.abs(new Double((stringWidth / 2.0)
							* Math.cos(theta)).intValue());
					int y = Math.abs(new Double((stringWidth / 2.0)
							* Math.sin(theta)).intValue());
					graphics.drawString(formattedText, resizePoints[midPoint].x
							- x, resizePoints[midPoint].y + y);
				}

				// start text at middle resize point when there are more than 3 resize points
			}
			else
			{
				int midPoint = resizePoints.length / 2;
				double theta = Math
						.atan((resizePoints[midPoint].y - resizePoints[midPoint + 1].y)
								* 1.0
								/ (resizePoints[midPoint].x - resizePoints[midPoint + 1].x)
								* 1.0);
				graphics.setFont(font.deriveFont(AffineTransform
						.getRotateInstance(theta)));
				graphics.drawString(formattedText, resizePoints[midPoint].x,
						resizePoints[midPoint].y);
			}
		}
	}

	/*  public void drawText( Graphics2D graphics ) {
	 if( text.length( ) > 0 ) {
	 // configure graphics
	 graphics.setFont( font );
	 graphics.setColor( textColor );

	 //calculate and rotate the amount needed to make the text parallel the line
	 //find the slope
	 double deltaX = (double)(resizePoints[2].x - resizePoints[0].x);
	 double deltaY = (double)(resizePoints[2].y - resizePoints[0].y);
	 double slope =  deltaY / deltaX;
	 //find arc tan of slope to get the degrees
	 double degree = Math.atan(slope);
	 //System.out.println("SLOPE: "+slope+"  :  DEGREES: "+degreeOfLine);
	 graphics.rotate(degree, textPoint.x, textPoint.y);
	 
	 //move the textPoint to match the line (space it from segment also)
	 //if deltax is positive add to x 
	 //if deltax is neg subtract from x
	 Point oldTextPoint = textPoint; //keep the old text point for reverse rotate below
	 textPoint = new Point( resizePoints[1].x + (((deltaX>=0 && deltaY>0)) || (deltaX<0 && deltaY<0)? +5:-5), resizePoints[1].y -5);

	 //center the text too
	 double centerAdj = (graphics.getFontMetrics(font).stringWidth(text) / 2);
	 double cenX = 0, cenY = 0;

	 if(deltaY == 0)
	 deltaY = 1;
	 if(deltaX == 0) 
	 deltaX = 1;
	 
	 //reduce to lowest slope fraction
	 if(deltaX>deltaY) 
	 {
	 if(deltaY>0)
	 centerAdj = centerAdj * +slope;
	 else if(Math.abs(deltaY)<=Math.abs(deltaX))
	 centerAdj = centerAdj * -slope;
	 
	 cenX=-Math.abs(deltaX/deltaY);
	 cenY=-1;
	 
	 if(cenX>-1){ cenX=0; cenY=-1; }
	 }
	 else
	 {
	 cenY=-Math.abs(deltaY/deltaX);
	 cenX=-1;
	 
	 if(cenY<-1){ cenY=-1; cenX=0; }
	 }
	 
	 if(deltaX<0 && deltaY>0 || deltaX>0 && deltaY<0) //quad3 or quad 1
	 cenY = -cenY;
	 
	 textPoint = new Point((int)(textPoint.x + (centerAdj*cenX)), (int)(textPoint.y + (centerAdj*cenY)));
	 
	 // draw text
	 graphics.drawString( text, textPoint.x, textPoint.y );
	 
	 //reset graphics
	 graphics.rotate(-degree, oldTextPoint.x, oldTextPoint.y);
	 }
	 } */

	/**
	 * Returns the point at which a line would connect to this shape.
	 *
	 * @param farPoint   the other end of the line connecting to this shape
	 * @return           the Point at which a line would connect to this shape
	 */
	public Point findLinkPoint(Point farPoint)
	{
		return null;
	}

	/**
	 * Returns the bounding rectangle of this shape.
	 *
	 * @return   the bounding rectangle of this shape
	 */
	public Rectangle getBounds()
	{
		return new Rectangle(location.x, location.y, width, height);
	}

	/**
	 * Returns the direction attribute of this line.
	 *
	 * @return   the direction attribute of this line
	 */
	public int getDirection()
	{
		return direction;
	}

	/**
	 * Returns the height of this shape.
	 *
	 * @return   the height of this shape
	 */
	public int getHeight()
	{
		Point[] boundingBox = getBoundingBox();
		return boundingBox[2].y - boundingBox[0].y;
	}

	/**
	 * Returns an int array containing the x and y values which make up the points on this line.
	 *
	 * @return   an int array containing the x and y values which make up the points on this line
	 */
	public int[] getLinePoints()
	{
		int[] linePoints = new int[resizePoints.length * 2];

		int index = 0;
		for (int i = 0; i < resizePoints.length; i++)
		{
			linePoints[index] = resizePoints[i].x;
			linePoints[index + 1] = resizePoints[i].y;
			index += 2;
		}

		return linePoints;
	}

	/**
	 * Returns the style attribute of this line.
	 *
	 * @return   the style attribute of this line
	 */
	public int getLineStyle()
	{
		return lineStyle;
	}

	/**
	 * Returns the type attribute of this line.
	 *
	 * @return   the type attribute of this line
	 */
	public int getLineType()
	{
		return lineType;
	}

	/**
	 * Returns the unique identifiers of the two shapes connected by this line.
	 *
	 * @return   the unique identifiers of the two shapes connected by this line
	 */
	public JWBUID[] getLinkedShapes()
	{
		return linkedShapes;
	}

	/**
	 * Returns the location of a shape.
	 *
	 * @return   a point representing the location of a shape
	 */
	public Point getLocation()
	{
		return getBoundingBox()[0];
	}

	/**
	 * Returns the unique identifier of the client which currently holds a lock on this shape.
	 *
	 * @return   the locking client's unique identifier
	 */
	public JWBUID getLockingClientUID()
	{
		// disable locking of lines because they can get updated by the system
		return null;
	}

	/**
	 * Used to obtain the point at which this shape's text is drawn.
	 *
	 * @return   the point at which this shape's text is drawn
	 */
	public Point getTextPoint()
	{
		return textPoint;
	}

	/**
	 * Returns the width of this shape.
	 *
	 * @return   the width of this shape
	 */
	public int getWidth()
	{
		Point[] boundingBox = getBoundingBox();
		return boundingBox[1].x - boundingBox[0].x;
	}

	/**
	 * Returns whether this shape is filled with its color.
	 *
	 * @return   whether this shape is filled with its color
	 */
	public boolean isFill()
	{
		return false;
	}

	/**
	 * Returns whether this shape is linkable
	 *
	 * @return   whether this shape is linkable
	 */
	public boolean isLinkable()
	{
		return false;
	}

	/**
	 * Returns true if the specified shape is linked to this line.
	 *
	 * @param uid   the uid of a specific shape
	 * @return      true if the specified shape is linked to this line
	 */
	public boolean isLinked(JWBUID uid)
	{
		if (linkedShapes[0].equals(uid) || linkedShapes[1].equals(uid))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Returns whether this shape is locked.
	 *
	 * @return   whether this shape is locked
	 */
	public boolean isLock()
	{
		// disable locking of lines because they can get updated by the system
		return false;
	}

	/**
	 * Returns whether this shape is magnifiable
	 *
	 * @return   whether this shape is magnifiable
	 */
	public boolean isMagnifiable()
	{
		return false;
	}

	/**
	 * Tests if the specified point is close to the shapes text point.
	 *
	 * @param point   the specified point
	 * @return        true if the specified point is close to the shapes text point
	 */
	public boolean isTextPoint(Point point)
	{
		if (text.length() > 0 && textPoint.distance(point) < 10)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Translates this shape's text by the specified delta.
	 *
	 * @param delta   the distance to move this shapes text
	 */
	public void moveTextPoint(Point delta)
	{
		textPoint = new Point(textPoint.x + delta.x, textPoint.y + delta.y);
		textPoint = validateLocation(textPoint);
	}

	/**
	 * Repositions both ends of this line if the given shapes are linked.  Order doesn't matter.
	 *
	 * @param linkedShape1   if shape is linked, this line will reposition, else nothing will happen
	 * @param linkedShape2   if shape is linked, this line will reposition, else nothing will happen
	 */
	public void rebuildEndPoints(JWBShapeSkeleton linkedShape1, JWBShapeSkeleton linkedShape2)
	{
		JWBShapeSkeleton firstShape = null;
		JWBShapeSkeleton secondShape = null;

		if (linkedShape1.getUID().equals(linkedShapes[0])
				&& linkedShape2.getUID().equals(linkedShapes[1]))
		{
			firstShape = linkedShape1;
			secondShape = linkedShape2;
		}
		else if (linkedShape1.getUID().equals(linkedShapes[1])
				&& linkedShape2.getUID().equals(linkedShapes[0]))
		{
			firstShape = linkedShape2;
			secondShape = linkedShape1;
		}
		else
		{
			return;
		}

		if (resizePoints.length == 3)
		{
			resizePoints[0] = firstShape.findLinkPoint(secondShape
					.getCenterPoint());
			resizePoints[2] = secondShape.findLinkPoint(firstShape
					.getCenterPoint());
			resizePoints[1] = new Point(resizePoints[0].x
					+ (resizePoints[2].x - resizePoints[0].x) / 2,
					resizePoints[0].y + (resizePoints[2].y - resizePoints[0].y)
							/ 2);

		}
		else
		{
			resizePoints[0] = firstShape.findLinkPoint(resizePoints[2]);
			resizePoints[resizePoints.length - 1] = secondShape
					.findLinkPoint(resizePoints[resizePoints.length - 3]);
			resizePoints[1] = new Point(resizePoints[0].x
					+ (resizePoints[2].x - resizePoints[0].x) / 2,
					resizePoints[0].y + (resizePoints[2].y - resizePoints[0].y)
							/ 2);
			resizePoints[resizePoints.length - 2] = new Point(
					resizePoints[resizePoints.length - 1].x
							+ (resizePoints[resizePoints.length - 3].x - resizePoints[resizePoints.length - 1].x)
							/ 2,
					resizePoints[resizePoints.length - 1].y
							+ (resizePoints[resizePoints.length - 3].y - resizePoints[resizePoints.length - 1].y)
							/ 2);
		}
	}

	/**
	 * Used to set the direction of this line.
	 *
	 * @param direction   the direction to use
	 */
	public void setDirection(int direction)
	{
		this.direction = direction;
	}

	/**
	 * Used to set the x and y values which make up the points on this line
	 *
	 * @param linePoints   an int array containing the x and y values which make up the points on this line
	 */
	public void setLinePoints(int[] linePoints)
	{
		resizePoints = new Point[linePoints.length / 2];

		int index = 0;
		for (int i = 0; i < linePoints.length; i += 2)
		{
			resizePoints[index] = new Point(linePoints[i], linePoints[i + 1]);
			index += 1;
		}

		location = getLocation();
		width = getWidth();
		height = getHeight();
	}

	/**
	 * Used to set the line style of this line.
	 *
	 * @param lineStyle   the line style to use
	 */
	public void setLineStyle(int lineStyle)
	{
		this.lineStyle = lineStyle;
	}

	/**
	 * Used to set the line type of this line.
	 *
	 * @param lineType   the line type to use
	 */
	public void setLineType(int lineType)
	{
		this.lineType = lineType;
	}

	/**
	 * Enables or disables the linkability of a shape.
	 *
	 * @param state   if true, this shape can link to other shapes through lines
	 */
	public void setLinkable(boolean state)
	{
		// disable linking of lines together
	}

	/**
	 * Locks or unlocks a shape.
	 *
	 * @param state              if true, this shape will be locked
	 * @param lockingClientUID   the unique identifier of the client locking or unlocking this shape
	 */
	public void setLock(boolean state, JWBUID lockingClientUID)
	{
		// disable locking of lines because they can get updated by the system
	}

	/**
	 * Enables or disables the magnifiability of a shape.
	 *
	 * @param state   if true, this shape can be magnified by the JWBPanel
	 */
	public void setMagnifiable(boolean state)
	{
		// disable magnifing of lines
	}

	/**
	 * Sets the point at which this shape's text should be drawn.
	 *
	 * @param point   the point at which this shape's text should be drawn
	 */
	public void setTextPoint(Point point)
	{
		textPoint = validateLocation(point);
	}

	public void setShowText(boolean show)
	{
		this.showText = show;
	}
	
	/**
	 * Translates this shape by the specified delta.
	 *
	 * @param delta   the distance to move the shape
	 */
	public void translate(Point delta)
	{
		Point oldLocation = new Point(location.x, location.y);
		location = new Point(location.x + delta.x, location.y + delta.y);
		location = validateLocation(location);

		delta = new Point(location.x - oldLocation.x, location.y
				- oldLocation.y);

		Point[] movedPoints = new Point[resizePoints.length];

		Point point = null;
		for (int i = 0; i < resizePoints.length; i++)
		{
			point = resizePoints[i];
			movedPoints[i] = new Point(point.x + delta.x, point.y + delta.y);
		}

		resizePoints = movedPoints;
	}

	// helper method used to obtain the four points which bound this shape within a rectangle
	private Point[] getBoundingBox()
	{
		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int maxY = Integer.MIN_VALUE;

		for (int i = 0; i < resizePoints.length; i++)
		{
			if (resizePoints[i].x < minX)
			{
				minX = resizePoints[i].x;
			}
			if (resizePoints[i].y < minY)
			{
				minY = resizePoints[i].y;
			}
			if (resizePoints[i].x > maxX)
			{
				maxX = resizePoints[i].x;
			}
			if (resizePoints[i].y > maxY)
			{
				maxY = resizePoints[i].y;
			}
		}

		if (minX == maxX)
		{
			maxX += 1;
		}
		if (minY == maxY)
		{
			maxY += 1;
		}

		return new Point[] { new Point(minX, minY), new Point(maxX, minY),
				new Point(minX, maxY), new Point(maxX, maxY) };
	}

}
