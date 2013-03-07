/*
 * Provided with JCAT API to eliminate need for JWhiteboard jar, resource bundles and classpath settings
 * Used only in non-functional way to produce XML graph elements with API with least amount of code differences
 * in between API Control and JCAT Control (at least in the XML file generation part)  
 */

package mil.af.rl.jcat.integration.api;

import java.awt.Font;
import java.awt.Color;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.geom.Line2D;
import java.awt.FontMetrics;

import java.io.Serializable;
import java.util.ArrayList;

import com.c3i.jwb.JWBAttachment;
import com.c3i.jwb.JWBUID;


/**
 * An abstract superclass which provides implementations of many of the methods in JWBShape.
 * <BR><BR>
 *
 * Copyright (c) 2003-2005, by C3I Associates, ALL RIGHTS RESERVED
 *
 * @author Francis Conover ( for C3I Associates )
 * @version JWhiteBoard 1.0.0
 */
public abstract class AbstractShapeSkeleton implements JWBShapeSkeleton
{

	/**
	 * The shape's type.
	 */
	protected String type = null;

	/**
	 * The shape's unique identifier.
	 */
	protected JWBUID uid = null;

	/**
	 * The layer value of the shape.  This is managed by the JWBController.  Do not alter.
	 */
	protected double layerValue = 0;

	/**
	 * The location of the shape.
	 */
	protected Point location = new Point(-1, -1);

	/**
	 * The width of the shape.
	 */
	protected int width;

	/**
	 * The height of the shape.
	 */
	protected int height;

	/**
	 * The color of the shape.
	 */
	protected Color color = null;

	/**
	 * Whether the shape is to draw itself when asked.
	 */
	protected boolean hidden;

	/**
	 * Whether the shape is filled with its color.
	 */
	protected boolean fill;

	/**
	 * Whether the shape is outlined.
	 */
	protected boolean outline;

	/**
	 * Whether the shape has a shadow.
	 */
	protected boolean shadow;

	/**
	 * The shape's font.
	 */
	protected Font font = null;

	/**
	 * The text associated with the shape.
	 */
	protected String text = null;

	/**
	 * The color of the shape's text.
	 */
	protected Color textColor = null;

	/**
	 * The text drawable area.
	 */
	protected Rectangle textDrawableArea = null;

	/**
	 * Whether the shape is linkable.
	 */
	protected boolean linkable;

	/**
	 * Whether the shape is magnifiable.
	 */
	protected boolean magnifiable;

	/**
	 * Whether to draw markups.
	 */
	protected boolean markup;

	/**
	 * Whether this shape is associated with a submodel.
	 */
	protected boolean subModelShape;

	/**
	 * Markups to be drawn with the shape.
	 */
	protected StringBuffer markups = new StringBuffer();

	/**
	 * An array of convenient points located on the shape.
	 */
	protected Point[] resizePoints = null;

	/**
	 * A serializable object which can be used by a developer to
	 * associate or attach an object to this shape.
	 */
	protected JWBAttachment attachment = null;

	/**
	 * Hold's the shape to be drawn.
	 */
	protected Shape shape = null;

	/**
	 * Whether this shape is locked.
	 */
	protected boolean lock = false;

	/**
	 * The unique identifier of the locking client.
	 */
	protected JWBUID lockingClientUID = null;

	/**
	 * This is an abstract class that cannot be instantiated directly.
	 */
	protected AbstractShapeSkeleton()
	{

		width = 80;
		height = 80;
		color = new java.awt.Color(0, 128, 255);
		hidden = false;
		fill = true;
		outline = true;
		shadow = false;
		font = new Font("Arial", Font.PLAIN, 12);
		text = "";
		textColor = Color.BLACK;
		linkable = true;
		magnifiable = true;
		markup = true;
	}

	/**
	 * Adds a markup to be drawn with this shape.
	 *
	 * @param character   a markup to be drawn with this shape
	 */
	public void addMarkup(char character)
	{
		markups.append(character);
	}

	/**
	 * Checks if the given markup exists for this shape 
	 * @param character the markup character to check
	 * @return true if this shape currently has the character as a markup
	 */
	public boolean containsMarkup(char character)
	{
		if(markups.indexOf(character + "") >= 0)
			return true;
		else
			return false;
	}

	/**
	 * Tests if the specified point is on or inside the boundary of the shape.
	 *
	 * @param point   the specified point
	 * @return        true if the specified point is on or inside the shape boundary
	 */
	public boolean contains(Point point)
	{
		if(point.x >= location.x - 2 && point.x <= location.x + width + 2 && point.y >= location.y - 2 && point.y <= location.y + height + 2)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Used to draw the markups of a shape onto a given graphics object.
	 *
	 * @param graphics   the graphics object to draw to
	 */
	public void drawMarkups(Graphics2D graphics)
	{
		if(markup && markups.length() > 0)
		{
			graphics.setFont(new Font("Courier New", 1, 12));
			for(int i = 0; i < markups.length(); i++)
			{
				graphics.setColor(new Color(255, 255, 150));
				graphics.fillRect(location.x + 10 + 16 * i, location.y + height - 6, 13, 13);
				graphics.setColor(Color.BLACK);
				graphics.drawRect(location.x + 10 + 16 * i, location.y + height - 6, 13, 13);
				graphics.drawString(String.valueOf(markups.charAt(i)), location.x + 13 + 16 * i, location.y + height + 4);
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
		if(!hidden)
		{
			for(int i = 0; i < resizePoints.length; i++)
			{
				graphics.drawOval(resizePoints[i].x - 3, resizePoints[i].y - 3, 6, 6);
			}
		}
	}

	/**
	 * Used to draw a shape onto a given graphics object.
	 *
	 * @param graphics   the graphics object to draw to
	 */
	public void drawShape(Graphics2D graphics)
	{
		if(!hidden)
		{
			if(fill)
			{
				// paint shadow
				if(shadow)
				{
					translate(new Point(5, 5));
					graphics.setColor(new Color(25, 25, 50, color.getAlpha() / 3));
					graphics.fill(shape);

					translate(new Point(-5, -5));
				}

				graphics.setColor(color);
				graphics.fill(shape);

				// outline with black
				if(outline)
				{
					graphics.setColor(Color.BLACK);
					graphics.setStroke(new BasicStroke(1f));
					graphics.draw(shape);
					graphics.setStroke(new BasicStroke());
				}

			}
			else
			{
				// outline with color
				graphics.setColor(color);
				graphics.setStroke(new BasicStroke(1f));
				graphics.draw(shape);
				graphics.setStroke(new BasicStroke());
			}

			drawText(graphics);
			drawMarkups(graphics);
		}
	}

	/**
	 * Used to draw the text of a shape onto a given graphics object.
	 *
	 * @param graphics   the graphics object to draw to
	 */
	public void drawText(Graphics2D graphics)
	{
		if(text.length() > 0 && textDrawableArea != null)
		{

			// configure graphics
			graphics.setFont(font);
			graphics.setColor(textColor);

			// init
			FontMetrics fontMetrics = graphics.getFontMetrics();

			ArrayList viewableTextList = new ArrayList();
			StringBuffer viewableText = new StringBuffer();

			// build text list
			for(int i = 0; i < text.length(); i++)
			{
				if(fontMetrics.stringWidth(viewableText.toString()) + fontMetrics.charWidth(text.charAt(i)) < textDrawableArea.width - 7 && text.charAt(i) != '\n')
				{
					viewableText.append(text.charAt(i));
				}
				else
				{
					if(i != 0)
					{
						// check if last word is written out
						if(text.charAt(i - 1) != '\n' && text.charAt(i - 1) != ' ' && text.charAt(i) != '\n' && text.charAt(i) != ' ')
						{
							// if not then check for a possible break
							if(viewableText.lastIndexOf(" ") != -1)
							{
								if(viewableText.lastIndexOf(" ") != 0)
								{
									i = i - (viewableText.length() - viewableText.lastIndexOf(" "));
									viewableText = new StringBuffer(viewableText.substring(0, viewableText.lastIndexOf(" ")));
								}
							}
						}
					}
					viewableTextList.add(viewableText.toString().trim());
					viewableText = new StringBuffer();
					if(text.charAt(i) != '\n')
					{
						viewableText.append(text.charAt(i));
					}
				}
			}
			if(viewableText.length() > 0)
			{
				viewableTextList.add(viewableText.toString().trim());
			}

			if(viewableTextList.size() > 0)
			{
				// determine y starting point
				int yPosition = getCenterPoint().y + (fontMetrics.getMaxAscent() / 3) - ((fontMetrics.getMaxAscent() / 2) * (viewableTextList.size() - 1));
				if(yPosition < textDrawableArea.y + fontMetrics.getMaxAscent())
				{
					yPosition = textDrawableArea.y + +fontMetrics.getMaxAscent();
				}

				// draw text
				int xPosition = 0;
				String textLine = null;
				for(int i = 0; i < viewableTextList.size(); i++)
				{
					if(yPosition < (textDrawableArea.y + textDrawableArea.height - fontMetrics.getMaxDescent()))
					{
						textLine = (String) viewableTextList.get(i);

						// determine x starting point
						xPosition = textDrawableArea.x + ((textDrawableArea.width - fontMetrics.stringWidth(textLine)) / 2);

						graphics.drawString(textLine, xPosition, yPosition);
						yPosition += fontMetrics.getMaxAscent() + 2;
					}
				}
			}
		}
	}

	/**
	 * Returns the point at which a line would connect to this shape.
	 *
	 * @param farPoint   the other end of the line connecting to this shape
	 * @return           the Point at which a line would connect to this shape
	 */
	public Point findLinkPoint(Point farPoint)
	{
		Point centerPoint = getCenterPoint();
		Point linkPoint = null;
		double unknown = 0.0;

		Line2D.Double line = new Line2D.Double(centerPoint, farPoint);

		int next = 1;
		for(int i = 0; i < resizePoints.length; i++)
		{
			if(line.intersectsLine(new Line2D.Double(resizePoints[i], resizePoints[next])))
			{
				unknown = getUnknown(centerPoint, farPoint, resizePoints[i], resizePoints[next]);
				break;
			}
			next++;
			if(next == resizePoints.length)
			{
				next = 0;
			}
		}

		linkPoint = new Point(centerPoint.x + (int) (unknown * (farPoint.x - centerPoint.x)), centerPoint.y + (int) (unknown * (farPoint.y - centerPoint.y)));

		return linkPoint;
	}

	/**
	 * Returns any attachment a developer might have added to this shape.
	 *
	 * @return   any attachment a developer might have added to this shape
	 */
	public JWBAttachment getAttachment()
	{
		return attachment;
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
	 * Returns the center point of the shape.
	 *
	 * @return   the center point of the shape
	 */
	public Point getCenterPoint()
	{
		return new Point(location.x + width / 2, location.y + height / 2);
	}

	/**
	 * Returns a resize point close to the one provided.
	 *
	 * @param point   the location of the cursor
	 * @return        the resizing point close to the cursor or null if none are found
	 */
	public Point getClosestResizePoint(Point point)
	{
		boolean found = false;

		int i;
		for(i = 0; i < resizePoints.length; i++)
		{
			if(resizePoints[i].distance(point) < 10)
			{
				found = true;
				break;
			}
		}

		if(found)
		{
			return resizePoints[i];
		}
		else
		{
			return null;
		}
	}

	/**
	 * Returns the shapes color.
	 *
	 * @return   a Color object representing the color of the shape
	 */
	public Color getColor()
	{
		return color;
	}

	/**
	 * Returns the shapes font.
	 *
	 * @return   a Font object representing the font of the shape
	 */
	public Font getFont()
	{
		return font;
	}

	/**
	 * Returns the height of this shape.
	 *
	 * @return   the height of this shape
	 */
	public int getHeight()
	{
		return height;
	}

	/**
	 * Returns this shapes unique identifier.
	 *
	 * @return   this shapes unique identifier
	 */
	public JWBUID getUID()
	{
		return uid;
	}

	/**
	 * Returns the layer value of the shape.  This is managed by the JWBController.  Do not override.
	 *
	 * @return   the layer value of a shape
	 */
	public double getLayerValue()
	{
		return layerValue;
	}

	/**
	 * Returns the location of a shape.
	 *
	 * @return   a point representing the location of a shape
	 */
	public Point getLocation()
	{
		return location;
	}

	/**
	 * Returns the unique identifier of the client which currently holds a lock on this shape.
	 *
	 * @return   the locking clients unique identifier
	 */
	public JWBUID getLockingClientUID()
	{
		return lockingClientUID;
	}

	/**
	 * Returns the type of cursor associated with the specific resize point.
	 *
	 * @param point   the location of the resize point
	 * @return        a Cursor specific to the resize point
	 */
	public Cursor getResizeCursor(Point point)
	{
		if((point == resizePoints[0]) || (point == resizePoints[4]))
		{
			return new Cursor(Cursor.NW_RESIZE_CURSOR);
		}
		else if((point == resizePoints[1]) || (point == resizePoints[5]))
		{
			return new Cursor(Cursor.N_RESIZE_CURSOR);
		}
		else if((point == resizePoints[2]) || (point == resizePoints[6]))
		{
			return new Cursor(Cursor.NE_RESIZE_CURSOR);
		}
		else if((point == resizePoints[3]) || (point == resizePoints[7]))
		{
			return new Cursor(Cursor.E_RESIZE_CURSOR);
		}
		else
		{
			return Cursor.getDefaultCursor();
		}
	}

	/**
	 * Returns the resize point(s) of the shape.
	 *
	 * @return   an array of the resize point(s) of the shape.
	 */
	public Point[] getResizePoints()
	{
		return resizePoints;
	}

	/**
	 * Returns the text drawn with the shape.
	 *
	 * @return   the text drawn with the shape
	 */
	public String getText()
	{
		return text.toString();
	}

	/**
	 * Returns the color of the text drawn with the shape.
	 *
	 * @return   the color of the text drawn with the shape
	 */
	public Color getTextColor()
	{
		return textColor;
	}

	/**
	 * Returns the type of the shape.
	 *
	 * @return   a JWBEditMode object representing the type of the shape
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * Returns the width of this shape.
	 *
	 * @return   the width of this shape
	 */
	public int getWidth()
	{
		return width;
	}

	/**
	 * Returns whether this shape is filled with its color.
	 *
	 * @return   whether this shape is filled with its color
	 */
	public boolean isFill()
	{
		return fill;
	}

	/**
	 * Returns whether this shape is set to draw itself when asked.
	 *
	 * @return   whether this shape is set to draw itself when asked
	 */
	public boolean isHidden()
	{
		return hidden;
	}

	/**
	 * Returns whether this shape is linkable
	 *
	 * @return   whether this shape is linkable
	 */
	public boolean isLinkable()
	{
		return linkable;
	}

	/**
	 * Returns whether this shape is locked.
	 *
	 * @return   whether this shape is locked
	 */
	public boolean isLock()
	{
		return lock;
	}

	/**
	 * Returns whether this shape is magnifiable
	 *
	 * @return   whether this shape is magnifiable
	 */
	public boolean isMagnifiable()
	{
		return magnifiable;
	}

	/**
	 * Returns the value of the shape's markup flag.
	 *
	 * @return true if the shapes's markup flag is set
	 */
	public boolean isMarkup()
	{
		return markup;
	}

	/**
	 * Returns the value of the shape's outline flag.
	 *
	 * @return   true if the shape's outline flag is set
	 */
	public boolean isOutline()
	{
		return outline;
	}

	/**
	 * Tests if the specified point is close to one of the shape's resize points.
	 *
	 * @param point   the specified point
	 * @return        true if the specified point is close to one of the shape's resize points
	 */
	public boolean isResizePoint(Point point)
	{
		boolean found = false;

		for(int i = 0; i < resizePoints.length; i++)
		{
			if(resizePoints[i].distance(point) < 10)
			{
				found = true;
			}
		}

		return found;
	}

	/**
	 * Returns the value of the shape's shadow flag.
	 *
	 * @return   true if the shape's shadow flag is set
	 */
	public boolean isShadow()
	{
		return shadow;
	}

	/**
	 * Returns the value of the shape's subModelShape flag.
	 *
	 * @return   true if the shape's subModelShape flag is set
	 */
	public boolean isSubModelShape()
	{
		return subModelShape;
	}

	/**
	 * Adds a markup to be drawn with this shape.
	 *
	 * @param character   a markup to be drawn with this shape
	 */
	public void removeMarkup(char character)
	{
		int index = markups.indexOf(String.valueOf(character));
		if(index != -1)
		{
			markups.deleteCharAt(index);
		}
	}

	/**
	 * Resizes this shape given a width and height.
	 *
	 * @param width      the width to draw this shape
	 * @param height     the height to draw this shape
	 */
	public void resize(int width, int height)
	{
		adjustShape(this.location, width, height);
	}

	/**
	 * Resizes this shape given a starting and ending location where the starting location
	 * is a resize point upon this shape.  Returns the ending location which may have been
	 * adjusted or the starting location if it was not equal to a resize point upon this shape.
	 *
	 * @param start   a resize point upon this shape
	 * @param end     the location to move the resize point to
	 * @return        the ending location which may have been adjusted
	 */
	public Point resize(Point start, Point end)
	{
		if(resizePoints[0].equals(start))
		{
			resizeDragUpLeft(0, end);
			return resizePoints[0];
		}
		else if(resizePoints[1].equals(start))
		{
			resizeDragUp(1, end);
			return resizePoints[1];
		}
		else if(resizePoints[2].equals(start))
		{
			resizeDragUpRight(2, end);
			return resizePoints[2];
		}
		else if(resizePoints[3].equals(start))
		{
			resizeDragRight(3, end);
			return resizePoints[3];
		}
		else if(resizePoints[4].equals(start))
		{
			resizeDragDownRight(4, end);
			return resizePoints[4];
		}
		else if(resizePoints[5].equals(start))
		{
			resizeDragDown(5, end);
			return resizePoints[5];
		}
		else if(resizePoints[6].equals(start))
		{
			resizeDragDownLeft(6, end);
			return resizePoints[6];
		}
		else if(resizePoints[7].equals(start))
		{
			resizeDragLeft(7, end);
			return resizePoints[7];
		}
		else
		{
			return start;
		}
	}

	/**
	 * Used by a developer to associate or attach an object to this shape.
	 *
	 * @param attachment   an object to associate or attach to this shape
	 */
	public void setAttachment(JWBAttachment attachment)
	{
		this.attachment = attachment;
	}

	/**
	 * Used to set the color of a shape.
	 *
	 * @param color   the color to set the shape to
	 */
	public void setColor(Color color)
	{
		if(color != null)
		{
			this.color = color;
		}
	}

	/**
	 * Enables or disables color filling of a shape.
	 *
	 * @param state   if true, this shape will be filled with its color setting
	 */
	public void setFill(boolean state)
	{
		this.fill = state;
	}

	/**
	 * Used to set the font of a shape.
	 *
	 * @param font   the font this shape should use
	 */
	public void setFont(Font font)
	{
		this.font = font;
	}

	/**
	 * Enables or disables the drawing of this shape.
	 *
	 * @param state   if true, this shape will be draw when asked
	 */
	public void setHidden(boolean state)
	{
		hidden = state;
	}

	/**
	 * Sets the layer value of a shape.  This is managed by the JWBController.  Do not call.
	 *
	 * @param layerValue   the layer value this shape should use
	 */
	public void setLayerValue(double layerValue)
	{
		this.layerValue = layerValue;
	}

	/**
	 * Enables or disables the linkability of a shape.
	 *
	 * @param state   if true, this shape can link to other shapes through lines
	 */
	public void setLinkable(boolean state)
	{
		this.linkable = state;
	}

	/**
	 * Moves this shape to the specified location.
	 *
	 *@param location   the point to move this shape to
	 */
	public void setLocation(Point location)
	{
		adjustShape(location, this.width, this.height);
	}

	/**
	 * Locks or unlocks a shape.
	 *
	 * @param state              if true, this shape will be locked
	 * @param lockingClientUID   the unique identifier of the client locking or unlocking this shape
	 */
	public void setLock(boolean state, JWBUID lockingClientUID)
	{
		this.lock = state;
		if(state)
		{
			this.lockingClientUID = lockingClientUID;
			addMarkup('L');
		}
		else
		{
			this.lockingClientUID = null;
			removeMarkup('L');
		}
	}

	/**
	 * Enables or disables the magnifiability of a shape.
	 *
	 * @param state   if true, this shape can be magnified by the JWBPanel
	 */
	public void setMagnifiable(boolean state)
	{
		magnifiable = state;
	}

	/**
	 * Enables or disables the drawing of markups for this shape.
	 *
	 * @param state   if true, this shape will display markups
	 */
	public void setMarkup(boolean state)
	{
		this.markup = state;
	}

	/**
	 * Enables or disables the outlining of a shape.
	 *
	 * @param state   if true, this shape will be outlined with its color setting
	 */
	public void setOutline(boolean state)
	{
		this.outline = state;
	}

	/**
	 * Enables or disables the drawing of a shadow behind a shape.
	 *
	 * @param state   if true, this shape will have a shadow
	 */
	public void setShadow(boolean state)
	{
		this.shadow = state;
	}

	/**
	 * Enables or disables this shape as being associated with a submodel.
	 *
	 * @param state   if true, this shape is associated with a submodel
	 */
	public void setSubModelShape(boolean state)
	{
		this.subModelShape = state;
	}

	/**
	 * Used to set the text associated with a shape.
	 *
	 * @param text   the text to associate with the shape
	 */
	public void setText(String text)
	{
		if(text == null)
		{
			this.text = "";
		}
		else
		{
			this.text = text;
		}
	}

	/**
	 * Used to set the color of the text associated with a shape.
	 *
	 * @param textColor   the color to set the text to
	 */
	public void setTextColor(Color textColor)
	{
		this.textColor = textColor;
	}

	/**
	 * Translates this shape by the specified delta.
	 *
	 * @param delta   the distance to move the shape
	 */
	public void translate(Point delta)
	{
		location = new Point(location.x + delta.x, location.y + delta.y);
		location = validateLocation(location);

		rebuildShape();
	}

	/**
	 * Returns the shape's closest resize point to the specified point of reference.
	 *
	 * @param point   the specified point of reference
	 * @return        the shape's closest resize point to the specified point of reference
	 */
	protected Point findResizePoint(Point point)
	{
		boolean found = false;

		int i;
		for(i = 0; i < resizePoints.length; i++)
		{
			if(resizePoints[i].distance(point) < 10)
			{
				found = true;
				break;
			}
		}

		if(found)
		{
			return resizePoints[i];
		}
		else
		{
			return null;
		}
	}

	/**
	 * Returns a serializable representation of the base attributes of this shape.
	 *
	 * @return   a serializable representation of the base attributes of this shape
	 */
	protected Serializable getBaseAttributes()
	{
		Object[] baseAttributes = new Object[] {
				new double[] { layerValue },
				new int[] { location.x, location.y, width, height, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha(), textColor.getRed(), textColor.getGreen(),
						textColor.getBlue(), textColor.getAlpha(), font.getStyle(), font.getSize() },
				new boolean[] { hidden, fill, outline, shadow, lock, markup, linkable, magnifiable, subModelShape },
				new String[] { new String(font.getName().getBytes()), new String(text.getBytes()) }, lockingClientUID, markups.toString() };

		return baseAttributes;
	}

	/**
	 * Returns the unknown value for the line intersection algorithm used by many subsclasses.
	 *
	 * @param p1   one of 4 points
	 * @param p2   one of 4 points
	 * @param p3   one of 4 points
	 * @param p4   one of 4 points
	 * @return     the unknown
	 */
	protected double getUnknown(Point p1, Point p2, Point p3, Point p4)
	{
		double unknown = (double) ((p4.x - p3.x) * (p1.y - p3.y) - (p4.y - p3.y) * (p1.x - p3.x)) / (double) ((p4.y - p3.y) * (p2.x - p1.x) - (p4.x - p3.x) * (p2.y - p1.y));

		return unknown;
	}

	/**
	 * Sets this shapes base attributes.
	 *
	 * @param baseAttributes   a serializable containing base attributes
	 */
	protected void setBaseAttributes(Serializable baseAttributes)
	{
		Object[] attributes = (Object[]) baseAttributes;

		double[] doubles = (double[]) attributes[0];
		layerValue = doubles[0];

		int[] ints = (int[]) attributes[1];
		location = new Point(ints[0], ints[1]);
		width = ints[2];
		height = ints[3];
		color = new Color(ints[4], ints[5], ints[6], ints[7]);
		textColor = new Color(ints[8], ints[9], ints[10], ints[11]);

		boolean[] booleans = (boolean[]) attributes[2];
		hidden = booleans[0];
		fill = booleans[1];
		outline = booleans[2];
		shadow = booleans[3];
		lock = booleans[4];
		markup = booleans[5];
		linkable = booleans[6];
		magnifiable = booleans[7];
		subModelShape = booleans[8];

		String[] strings = (String[]) attributes[3];
		font = new Font(strings[0], ints[12], ints[13]);
		text = strings[1];

		lockingClientUID = (JWBUID) attributes[4];
		markups = new StringBuffer((String) attributes[5]);
	}

	/**
	 * Returns a point on the whiteboard.
	 *
	 * @param location   a potential point on the whiteboard
	 * @return           a definite point on the whiteboard
	 */
	protected Point validateLocation(Point location)
	{
		if(location.x < 0)
		{
			location.setLocation(0, location.y);
		}
		if(location.y < 0)
		{
			location.setLocation(location.x, 0);
		}

		return location;
	}

	/**
	 * Builds or rebuilds a shape and its resize points based on its current location and size.
	 * Unimplemented.  Must be overwritten by subclasses.
	 */
	protected void rebuildShape()
	{
	}

	/**
	 * Resizes this shape upwards from the top.
	 *
	 * @param index   the index of the resize point selected
	 * @param end     the location the resize point has been moved to
	 */
	protected void resizeDragUp(int index, Point end)
	{
		int offsetY = getResizePoints()[index].y - location.y;

		if(location.y + height - 40 >= end.y - offsetY)
		{
			adjustShape(new Point(location.x, end.y - offsetY), width, location.y + height - end.y + offsetY);
		}
		else
		{
			adjustShape(new Point(location.x, location.y + height - 40), width, 40);
		}
	}

	/**
	 * Resizes this shape downwards from the bottom.
	 *
	 * @param index   the index of the resize point selected
	 * @param end     the location the resize point has been moved to
	 */
	protected void resizeDragDown(int index, Point end)
	{
		int offsetY = location.y + height - getResizePoints()[index].y;

		adjustShape(new Point(location.x, location.y), width, end.y + offsetY - location.y);
	}

	/**
	 * Resizes this shape right from the right.
	 *
	 * @param index   the index of the resize point selected
	 * @param end     the location the resize point has been moved to
	 */
	protected void resizeDragRight(int index, Point end)
	{
		int offsetX = location.x + width - getResizePoints()[index].x;

		adjustShape(new Point(location.x, location.y), end.x + offsetX - location.x, height);
	}

	/**
	 * Resizes this shape left from the left
	 *
	 * @param index   the index of the resize point selected
	 * @param end     the location the resize point has been moved to
	 */
	protected void resizeDragLeft(int index, Point end)
	{
		int offsetX = getResizePoints()[index].x - location.x;

		if(location.x + width - 40 >= end.x - offsetX)
		{
			adjustShape(new Point(end.x - offsetX, location.y), location.x + width - end.x + offsetX, height);
		}
		else
		{
			adjustShape(new Point(location.x + width - 40, location.y), 40, height);
		}
	}

	/**
	 * Resizes this shape upwards and/or right from the top and/or right.
	 *
	 * @param index   the index of the resize point selected
	 * @param end     the location the resize point has been moved to
	 */
	protected void resizeDragUpRight(int index, Point end)
	{
		int offsetX = location.x + width - getResizePoints()[index].x;
		int offsetY = getResizePoints()[index].y - location.y;

		if(location.y + height - 40 >= end.y - offsetY)
		{
			adjustShape(new Point(location.x, end.y - offsetY), end.x + offsetX - location.x, location.y + height - end.y + offsetY);
		}
		else
		{
			adjustShape(new Point(location.x, location.y + height - 40), end.x + offsetX - location.x, 40);
		}
	}

	/**
	 * Resizes this shape downwards and/or right from the bottom and/or right.
	 *
	 * @param index   the index of the resize point selected
	 * @param end     the location the resize point has been moved to
	 */
	protected void resizeDragDownRight(int index, Point end)
	{
		int offsetX = location.x + width - getResizePoints()[index].x;
		int offsetY = location.y + height - getResizePoints()[index].y;

		adjustShape(new Point(location.x, location.y), end.x + offsetX - location.x, end.y + offsetY - location.y);
	}

	/**
	 * Resizes this shape upwards and/or left from the top and/or left.
	 *
	 * @param index   the index of the resize point selected
	 * @param end     the location the resize point has been moved to
	 */
	protected void resizeDragUpLeft(int index, Point end)
	{
		int offsetX = getResizePoints()[index].x - location.x;
		int offsetY = getResizePoints()[index].y - location.y;

		if(location.x + width - 40 >= end.x - offsetX && location.y + height - 40 >= end.y - offsetY)
		{
			adjustShape(new Point(end.x - offsetX, end.y - offsetY), location.x + width - end.x + offsetX, location.y + height - end.y + offsetY);
		}
		else if(location.x + width - 40 >= end.x - offsetX && location.y + height - 40 < end.y - offsetY)
		{
			adjustShape(new Point(end.x - offsetX, location.y + height - 40), location.x + width - end.x + offsetX, 40);
		}
		else if(location.x + width - 40 < end.x - offsetX && location.y + height - 40 >= end.y - offsetY)
		{
			adjustShape(new Point(location.x + width - 40, end.y - offsetY), 40, location.y + height - end.y + offsetY);
		}
		else
		{
			adjustShape(new Point(location.x + width - 40, location.y + height - 40), 40, 40);
		}
	}

	/**
	 * Resizes this shape downwards and/or left from the bottom and/or left.
	 *
	 * @param index   the index of the resize point selected
	 * @param end     the location the resize point has been moved to
	 */
	protected void resizeDragDownLeft(int index, Point end)
	{
		int offsetX = getResizePoints()[index].x - location.x;
		int offsetY = location.y + height - getResizePoints()[index].y;

		if(location.x + width - 40 >= end.x - offsetX)
		{
			adjustShape(new Point(end.x - offsetX, location.y), location.x + width - end.x + offsetX, end.y + offsetY - location.y);
		}
		else
		{
			adjustShape(new Point(location.x + width - 40, location.y), 40, end.y + offsetY - location.y);
		}
	}

	// helper method used to make location or resize adjustments
	private void adjustShape(Point location, int width, int height)
	{
		this.location = validateLocation(location);

		if(width < 40)
		{
			width = 40;
		}
		if(height < 40)
		{
			height = 40;
		}

		this.width = width;
		this.height = height;

		rebuildShape();
	}

}
