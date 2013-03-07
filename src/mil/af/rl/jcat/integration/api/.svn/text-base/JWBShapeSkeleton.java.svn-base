/*
 * Provided with JCAT API to eliminate need for JWhiteboard jar, resource bundles and classpath settings
 * Used only in non-functional way to produce XML graph elements with API with least amount of code differences
 * in between API Control and JCAT Control (at least in the XML file generation part)  
 */

package mil.af.rl.jcat.integration.api;

import java.awt.Font;
import java.awt.Color;
import java.awt.Point;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.Graphics2D;

import com.c3i.jwb.JWBAttachment;
import com.c3i.jwb.JWBUID;

/**
 * Provides a skeletal implementation to be implemented by more specific objects.
 * <BR><BR>
 *
 * Copyright (c) 2003-2005, by C3I Associates, ALL RIGHTS RESERVED
 *
 * @author Francis Conover ( for C3I Associates )
 * @version JWhiteBoard 1.0.0
 */
public interface JWBShapeSkeleton {

  /**
   * Adds a markup to be drawn with this shape.
   *
   * @param character   a markup to be drawn with this shape
   */
  public void addMarkup( char character );

  /**
   * Checks if the given markup exists for this shape 
   * @param character the markup character to check
   * @return true if this shape currently has the character as a markup
   */
  public boolean containsMarkup( char character );
  
  /**
   * Tests if the specified point is on or inside the boundary of the shape.
   *
   * @param point   the specified point
   * @return        true if the specified point is on or inside the shape boundary
   */
  public boolean contains( Point point );

  /**
   * Used to draw the markups of a shape onto a given graphics object.
   *
   * @param graphics   the graphics object to draw to
   */
  public void drawMarkups( Graphics2D graphics );

  /**
   * Used to draw the resize points of a shape onto a given graphics object.
   *
   * @param graphics   the graphics object to draw to
   */
  public void drawResizePoints( Graphics2D graphics );

  /**
   * Used to draw a shape onto a given graphics object.
   *
   * @param graphics   the graphics object to draw to
   */
  public void drawShape( Graphics2D graphics );

  /**
   * Used to draw the text of a shape onto a given graphics object.
   *
   * @param graphics   the graphics object to draw to
   */
  public void drawText( Graphics2D graphics );

  /**
   * Returns the point at which a line would connect to this shape.
   *
   * @param farPoint   the other end of the line connecting to this shape
   * @return           the Point at which a line would connect to this shape
   */
  public Point findLinkPoint( Point farPoint );

  /**
   * Returns any attachment a developer might have added to this shape.
   *
   * @return   any attachment a developer might have added to this shape
   */
  public JWBAttachment getAttachment( );

  /**
   * Returns the bounding rectangle of this shape.
   *
   * @return   the bounding rectangle of this shape
   */
  public Rectangle getBounds( );

  /**
   * Returns the center point of the shape.
   *
   * @return   the center point of the shape
   */
  public Point getCenterPoint( );

  /**
   * Returns a resize point close to the one provided.
   *
   * @param point   the location of the cursor
   * @return        the resizing point close to the cursor or null if none are found
   */
  public Point getClosestResizePoint( Point point );

  /**
   * Returns the shapes color.
   *
   * @return   a Color object representing the color of the shape
   */
  public Color getColor( );

  /**
   * Returns the shapes font.
   *
   * @return   a Font object representing the font of the shape
   */
  public Font getFont( );

  /**
   * Returns the height of this shape.
   *
   * @return   the height of this shape
   */
  public int getHeight( );

  /**
   * Returns this shape's unique identifier.
   *
   * @return   this shape's unique identifier
   */
  public JWBUID getUID( );

  /**
   * Returns the layer value of the shape.  This is managed by the JWBController.  Do not override.
   *
   * @return   the layer value of a shape
   */
  public double getLayerValue( );

  /**
   * Returns the location of a shape.
   *
   * @return   a point representing the location of a shape
   */
  public Point getLocation( );

  /**
   * Returns the unique identifier of the client which currently holds a lock on this shape.
   *
   * @return   the locking clients unique identifier
   */
  public JWBUID getLockingClientUID( );

  /**
   * Returns the type of cursor associated with the specific resize point.
   *
   * @param point   the location of the resize point
   * @return        a Cursor specific to the resize point
   */
  public Cursor getResizeCursor( Point point );

  /**
   * Returns the resize point(s) of the shape.
   *
   * @return   an array of the resize point(s) of the shape.
   */
  public Point[] getResizePoints( );

  /**
   * Returns the text drawn with the shape.
   *
   * @return   the text drawn with the shape
   */
  public String getText( );

  /**
   * Returns the color of the text drawn with the shape.
   *
   * @return   the color of the text drawn with the shape
   */
  public Color getTextColor( );

  /**
   * Returns the type of the shape.
   *
   * @return   a String object representing the type of the shape
   */
  public String getType( );

  /**
   * Returns the width of this shape.
   *
   * @return   the width of this shape
   */
  public int getWidth( );

  /**
   * Returns whether this shape is filled with its color.
   *
   * @return   whether this shape is filled with its color
   */
  public boolean isFill( );

  /**
   * Returns whether this shape is set to draw itself when asked.
   *
   * @return   whether this shape is set to draw itself when asked
   */
  public boolean isHidden( );

  /**
   * Returns whether this shape is linkable
   *
   * @return   whether this shape is linkable
   */
  public boolean isLinkable( );

  /**
   * Returns whether this shape is locked.
   *
   * @return   whether this shape is locked
   */
  public boolean isLock( );

  /**
   * Returns whether this shape is magnifiable
   *
   * @return   whether this shape is magnifiable
   */
  public boolean isMagnifiable( );

  /**
   * Returns the value of the shape's markup flag.
   *
   * @return true if the shapes's markup flag is set
   */
  public boolean isMarkup( );

  /**
   * Returns the value of the shape's outline flag.
   *
   * @return   true if the shape's outline flag is set
   */
  public boolean isOutline( );

  /**
   * Tests if the specified point is close to one of the shape's resize points.
   *
   * @param point   the specified point
   * @return        true if the specified point is close to one of the shape's resize points
   */
  public boolean isResizePoint( Point point );

  /**
   * Returns the value of the shape's subModelShape flag.
   *
   * @return   true if the shape's subModelShape flag is set
   */
  public boolean isSubModelShape( );

  /**
   * Returns the value of the shape's shadow flag.
   *
   * @return   true if the shape's shadow flag is set
   */
  public boolean isShadow( );

  /**
   * Adds a markup to be drawn with this shape.
   *
   * @param character   a markup to be drawn with this shape
   */
  public void removeMarkup( char character );

  /**
   * Resizes this shape given a width and height.
   *
   * @param width      the width to draw this shape
   * @param height     the height to draw this shape
   */
  public void resize( int width, int height );

  /**
   * Resizes this shape given a starting and ending location where the starting location
   * is a resize point upon this shape.  Returns the ending location which may have been
   * adjusted or the starting location if it was not equal to a resize point upon this shape.
   *
   * @param start   a resize point upon this shape
   * @param end     the location to move the resize point to
   * @return        the ending location which may have been adjusted
   */
  public Point resize( Point start, Point end );

  /**
   * Used by a developer to associate or attach an object to this shape.
   *
   * @param attachment   an object to associate or attach to this shape
   */
  public void setAttachment( JWBAttachment attachment );

  /**
   * Used to set the color of a shape.
   *
   * @param color   the color to set the shape to
   */
  public void setColor( Color color );

  /**
   * Enables or disables color filling of a shape.
   *
   * @param state   if true, this shape will be filled with its color setting
   */
  public void setFill( boolean state );

  /**
   * Used to set the font of a shape.
   *
   * @param font   the font this shape should use
   */
  public void setFont( Font font );

  /**
   * Enables or disables the drawing of this shape.
   *
   * @param state   if true, this shape will be draw when asked
   */
  public void setHidden( boolean state );

  /**
   * Sets the layer value of a shape.  This is managed by the JWBController.  Do not call.
   *
   * @param layerValue   the layer value this shape should use
   */
  public void setLayerValue( double layerValue );

  /**
   * Enables or disables the linkability of a shape.
   *
   * @param state   if true, this shape can link to other shapes through lines
   */
  public void setLinkable( boolean state );

  /**
   * Moves this shape to the specified location.
   *
   *@param location   the point to move this shape to
   */
  public void setLocation( Point location );

  /**
   * Locks or unlocks a shape.
   *
   * @param state              if true, this shape will be locked
   * @param lockingClientUID   the unique identifier of the client locking or unlocking this shape
   */
  public void setLock( boolean state, JWBUID lockingClientUID );

  /**
   * Enables or disables the magnifiability of a shape.
   *
   * @param state   if true, this shape can be magnified by the JWBPanel
   */
  public void setMagnifiable( boolean state );

  /**
   * Enables or disables the drawing of markups for this shape.
   *
   * @param state   if true, this shape will display markups
   */
  public void setMarkup( boolean state );

  /**
   * Enables or disables the outlining of a shape.
   *
   * @param state   if true, this shape will be outlined with its color setting
   */
  public void setOutline( boolean state );

  /**
   * Enables or disables the drawing of a shadow behind a shape.
   *
   * @param state   if true, this shape will have a shadow
   */
  public void setShadow( boolean state );

  /**
   * Enables or disables this shape as being associated with a submodel.
   *
   * @param state   if true, this shape is associated with a submodel
   */
  public void setSubModelShape( boolean state );

  /**
   * Used to set the text associated with a shape.
   *
   * @param text   the text to associate with the shape
   */
  public void setText( String text );

  /**
   * Used to set the color of the text associated with a shape.
   *
   * @param textColor   the color to set the text to
   */
  public void setTextColor( Color textColor );

  /**
   * Translates this shape by the specified delta.
   *
   * @param delta   the distance to move the shape
   */
  public void translate( Point delta );

}
