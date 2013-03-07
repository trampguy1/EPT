/* Created Sept 20, 05
 * Author: MikeyD
 */
package mil.af.rl.jcat.gui.dialogs.wizard;

import java.awt.*;
import java.awt.geom.AffineTransform;

import javax.swing.*;
import javax.swing.border.Border;

import org.apache.log4j.Logger;

import java.awt.image.BufferedImage;
import java.net.URL;


public class ArrowWindow extends JWindow
{

	private static final long serialVersionUID = 1L;
	JLabel image, transImage;
	ImageIcon displayImg;
	BufferedImage buffImg;
	boolean firstTime = true;
	private AffineTransform m_affineTransform = new AffineTransform();
	private ImageIcon displayImg1;
	private int angle = 0;
	private int lastAngAdj = 0;
	private Dimension size = null;
	private Frame parent = null;
	private Border highlightBorder = BorderFactory.createLineBorder(java.awt.Color.YELLOW, 3);
	private JComponent lastComp = null;
	private static Logger logger = Logger.getLogger(ArrowWindow.class);

	public ArrowWindow(URL fileName, Frame parent) //, int x, int y)
	{
		super(parent);
		this.parent = parent;
		image = new JLabel();
		transImage = new JLabel();
		displayImg = new ImageIcon(fileName);
		int w = displayImg.getIconWidth();
		int h = displayImg.getIconHeight();
		size = new Dimension((w>h)?w:h, (w>h)?w:h);
		
		image.setIcon(displayImg);
		getContentPane().add(image);
		pack();
		setSize(size);
		setAlwaysOnTop(true);
	}
	
	public void pointTo(JComponent toComp, int ang, boolean highlightObject)
	{
		try{
			if(toComp == null)
				return;
			int compLocX = toComp.getLocationOnScreen().x;
			int compLocY = toComp.getLocationOnScreen().y;
			Dimension compSize = toComp.getSize();
			Point atPoint = toComp.getLocationOnScreen();
			if(ang == 0)
				atPoint = new Point(compLocX-getWidth(), compLocY-(getHeight()/2)+(compSize.height/2)+5);
			else if(ang == 90)
				atPoint = new Point((compLocX-(getWidth()/2))+(compSize.width/2)+5, compLocY+compSize.height);
			else if(ang == 180)
				atPoint = new Point(compLocX+compSize.width, compLocY-(getHeight()/2)+(compSize.height/2)+5);
			else if(ang == 270)
				atPoint = new Point((compLocX-(getWidth()/2))+(compSize.width/2)+5, compLocY-getHeight());
			
			if(lastComp != null)
				lastComp.setBorder(null);
			
			if(highlightObject)
			{
				(lastComp = toComp).setBorder(highlightBorder);
				parent.update(parent.getGraphics());
			}
			
			pointTo(atPoint, ang);
		}catch(java.awt.IllegalComponentStateException exc){
			logger.warn("pointTo - attempted to 'point to' a component that was not showing:  ", exc);
		}
	}
	
	public void pointTo(Point loc, int ang)
	{
		angle = -((360-lastAngAdj)+ang);
		lastAngAdj = ang;

		setVisible(false);
		//ensure the stuff behind the last arrow was repainted
		parent.update(parent.getGraphics());
		
		setLocation(loc);
		updateWindow();
	}
		
	public void updateWindow()
	{
    	try{
			Robot rob = new Robot();
			Rectangle rec = new Rectangle();
			rec.setSize(size);
			rec.setLocation(getLocation());
						
			buffImg = rob.createScreenCapture(rec);
			
			displayImg1 = new ImageIcon(buffImg.getScaledInstance(getWidth(), getHeight(), Image.SCALE_DEFAULT));
			transImage.setIcon(displayImg1);
			
			if(firstTime)
			{
				getContentPane().add(transImage);
				firstTime=false;
			}
			
			transImage.updateUI();
			setVisible(true);
			transImage.updateUI();
			this.toFront();
		}catch(Exception e){
			logger.warn("updateWindow - Error updating arrow window:  "+e.getMessage());
		}
	}
	
	public void paint(Graphics g)
	{
		Graphics2D g2d = (Graphics2D)g; 
         
        //fill the rect 
        g2d.fillRect(0, 0, getWidth(), getHeight()); 
        
        //draw the screen capture first
        g2d.drawImage(displayImg1.getImage(), new AffineTransform(), this);

        //rotate with the rotation point as the mid of the image          
        m_affineTransform.rotate(Math.toRadians(angle), getWidth()/2, getHeight()/2);
        //reset angle after first paint so it doesn't rotate on each repaint
        angle = 0;
        
        //draw the image using the rotated AffineTransform 
        g2d.drawImage(displayImg.getImage(), m_affineTransform, this); 
	}

	public void setVisible(boolean vis)
	{
		//if(lastComp != null && !vis)
		//	lastComp.setBorder(null);
		super.setVisible(vis);
	}
	
	public void dispose()
	{
		if(lastComp != null)
			lastComp.setBorder(null);
		super.dispose();
	}
}
