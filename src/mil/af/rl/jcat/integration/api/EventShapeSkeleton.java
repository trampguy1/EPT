/*
 * Provided with JCAT API to eliminate need for JWhiteboard jar, resource bundles and classpath settings
 * Used only in non-functional way to produce XML graph elements with API with least amount of code differences
 * in between API Control and JCAT Control (at least in the XML file generation part)  
 */

package mil.af.rl.jcat.integration.api;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

import com.c3i.jwb.JWBUID;


public class EventShapeSkeleton extends AbstractShapeSkeleton {

  /**
   * Creates a new JWBRoundedRectangle object.
   *
   * @param location   the point at which to start drawing this object
   * @param width      the width to draw this shape
   * @param height     the height to draw this shape
   * @param uid        the uid with which this shape is to be associated
   */
  public EventShapeSkeleton( Point location, int width, int height, JWBUID uid ) {
    this.type = "com.c3i.jwb.shapes.JWBRoundedRectangle";
    this.location = validateLocation( location );
    this.uid = uid;

    this.width = ( width < 40 ) ? 40 : width;
    this.height = ( height < 40 ) ? 40 : height;

    rebuildShape( );
  }


  /**
   * Returns the point at which a line would connect to this shape.
   *
   * @param farPoint   the other end of the line connecting to this shape
   * @return           the Point at which a line would connect to this shape
   */
  public Point findLinkPoint( Point farPoint ) {
    Point linkPoint = super.findLinkPoint( farPoint );
    double delta = ( ( RoundRectangle2D)shape ).getArcWidth( ) / 2;

    Point[] boundingBox = new Point[]{ resizePoints[0], resizePoints[2], resizePoints[4], resizePoints[6] };
    
    if( linkPoint.distance( boundingBox[0] ) < delta ) {
      linkPoint.setLocation( boundingBox[0].getX( ), boundingBox[0].getY( ) );
    }else if( linkPoint.distance( boundingBox[1] ) < delta ) {
      linkPoint.setLocation( boundingBox[1].getX( ), boundingBox[1].getY( ) );
    }else if( linkPoint.distance( boundingBox[2] ) < delta ) {
      linkPoint.setLocation( boundingBox[2].getX( ), boundingBox[2].getY( ) );
    }else if( linkPoint.distance( boundingBox[3] ) < delta ) {
      linkPoint.setLocation( boundingBox[3].getX( ), boundingBox[3].getY( ) );
    }

    return linkPoint;
  }

  /**
   * Builds or rebuilds a shape and its resize points based on its current location and size.
   */
  protected void rebuildShape( ) {
    resizePoints = new Point[8];
    resizePoints[0] = new Point( location.x + (int)Math.sqrt( width/10 ),
                                 location.y + (int)Math.sqrt( height/10 ) );
    resizePoints[1] = new Point( location.x + width/2, location.y );
    resizePoints[2] = new Point( location.x + width - (int)Math.sqrt( width/10 ),
                                 location.y + (int)Math.sqrt( height/10 ) );
    resizePoints[3] = new Point( location.x + width, location.y + height/2 );
    resizePoints[4] = new Point( location.x + width - (int)Math.sqrt( width/10 ),
                                 location.y + height - (int)Math.sqrt( height/10 ) );
    resizePoints[5] = new Point( location.x + width/2, location.y + height );
    resizePoints[6] = new Point( location.x + (int)Math.sqrt( width/10 ),
                                 location.y + height - (int)Math.sqrt( height/10 ) );
    resizePoints[7] = new Point( location.x, location.y + height/2 );

    shape = new RoundRectangle2D.Double( location.x, location.y, width, height,
      Math.sqrt( width ) + Math.sqrt( width ), Math.sqrt( height ) + Math.sqrt( height ) );
    
    textDrawableArea = new Rectangle( location.x, location.y, width, height );
  }

}
