/*
 * Created on Aug 19, 2004
 *
 * Author Craig McNamara
 */
package mil.af.rl.jcat.gui;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import mil.af.rl.jcat.util.EnvUtils;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartUtilities;

import com.jidesoft.docking.DockableFrame;
import com.jidesoft.docking.event.DockableFrameEvent;
import com.jidesoft.docking.event.DockableFrameListener;

/**
 * @author mcnamacr
 *  
 */
public class ProfileLegend extends DockableFrame implements DockableFrameListener
{

	private static final long serialVersionUID = 1L;

	class PopupListener extends MouseAdapter
	{
		JPopupMenu popup;

		PopupListener(JPopupMenu popupMenu)
		{
			popup = popupMenu;
		}

		public void mousePressed(MouseEvent e)
		{
			showPopup(e);
		}

		public void mouseReleased(MouseEvent e)
		{
			showPopup(e);
		}

		private void showPopup(MouseEvent e)
		{
			if (e.isPopupTrigger())
			{
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	class ProfilePopupMenu extends JPopupMenu
	{

		private static final long serialVersionUID = 1L;
		private JMenuItem clearAll = new JMenuItem();
		private ProfileLegend parent;

		private JMenuItem remove = new JMenuItem();

		public ProfilePopupMenu(ProfileLegend parent)
		{
			this.parent = parent;
			createPopupMenu();
		}

		private void clearAll()
		{
			parent.clearAllSeries();
		}

		/**
		 *  
		 */
		 private void createPopupMenu()
		{
			add(remove);
			this.addSeparator();
			add(clearAll);
			remove.setText("Remove Plot");
			remove.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					removePlot();
				}
			});
			clearAll.setText("Clear All");
			clearAll.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					clearAll();
					model.clear();
				}
			});
		}

		 private void removePlot()
		 {
			 parent.removeSelectedPlot();
		 }
	}
	static ProfileLegend legend = null;

	public static ProfileLegend getInstance()
	{
		if (ProfileLegend.legend != null)
			return legend;
		else
			return new ProfileLegend();

	}
	ProfileDataModel model = ProfileDataModel.getInstance();
	ProfilePopupMenu popup = null;
	JScrollPane scroll = new JScrollPane();
	JTable table = null;
	private static Logger logger = Logger.getLogger(ProfileLegend.class);

    private ProfileLegend()
    {
        super("Profile Legend", new ImageIcon());
		setFrameIcon(new ImageIcon(this.getClass().getClassLoader().getResource( "stock_chart_legend.png")));
		table = new JTable(model);
		getContentPane().add(new JScrollPane(table));
		setupPopupMenu();
		legend = this;
        javax.help.CSH.setHelpIDString(this, "Profile_Legend");
    }


	public void clearAllSeries()
	{
		for (int r = table.getRowCount(); 0 < r; r--)
			model.clear();
	}

	//Added 01/31/2005 MPG
	public String getImage() throws IOException
	{
		boolean dirCreated = (new File(EnvUtils.getUserHome() + "/.JCAT")).mkdir();

		java.awt.Image imLegend = table.createImage(480, 240);

		byte[] png = ChartUtilities.encodeAsPNG(toBufferedImage(imLegend));

		File image = new File(EnvUtils.getUserHome() + "/.JCAT/" + new Random().nextInt(99999999) + ".png");
		image.deleteOnExit();
		FileOutputStream out = new FileOutputStream(image);
		out.write(png);
		out.flush();
		out.close();

		return image.getName();
	}

	//Added 01/31/2005 MPG
	public boolean hasAlpha(Image image)
	{

		if (image instanceof BufferedImage)
		{
			BufferedImage bimage = (BufferedImage) image;
			return bimage.getColorModel().hasAlpha();
		}

		PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);

		try
		{
			pg.grabPixels();
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}

		ColorModel cm = pg.getColorModel();

		return cm.hasAlpha();
	}

	public void removeSelectedPlot()
	{
		int s = table.getSelectedRow();
		if (s != -1)
			model.removePlot(s);
	}

	private void setupPopupMenu()
	{
		popup = new ProfilePopupMenu(this);
		MouseListener popupListener = new PopupListener(popup);
		table.addMouseListener(popupListener);

	}

	//Added 01/31/2005 MPG
	public BufferedImage toBufferedImage(Image image)
	{
		if (image instanceof BufferedImage)
		{
			return (BufferedImage) image;
		}

		image = new ImageIcon(image).getImage();

		boolean hasAlpha = hasAlpha(image);

		BufferedImage bimage = null;
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

		try
		{
			int transparency = Transparency.OPAQUE;
			if (hasAlpha)
			{
				transparency = Transparency.BITMASK;
			}

			GraphicsDevice gs = ge.getDefaultScreenDevice();
			GraphicsConfiguration gc = gs.getDefaultConfiguration();
			bimage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null), transparency);
		} catch (HeadlessException e)
		{
			logger.error("toBufferedImage - headless error created buffered image:  "+e.getMessage());
		}

		if (bimage == null)
		{
			// Create a buffered image using the default color model
			int type = BufferedImage.TYPE_INT_RGB;
			if (hasAlpha)
			{
				type = BufferedImage.TYPE_INT_ARGB;
			}
			bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
		}

		// Copy image to buffered image
		Graphics g = bimage.createGraphics();

		// Paint the image onto the buffered image
		g.drawImage(image, 0, 0, null);
		g.dispose();

		return bimage;
	}


	public void addDockListener()
	{
		addDockableFrameListener(this);
	}

	/* 
	 * Unimplemented methods of DockableFrame listener
	 */
	public void dockableFrameAdded(DockableFrameEvent arg0){}
	public void dockableFrameRemoved(DockableFrameEvent arg0){}
	public void dockableFrameShown(DockableFrameEvent arg0){}
	public void dockableFrameDocked(DockableFrameEvent arg0){}
	public void dockableFrameFloating(DockableFrameEvent arg0){}
	public void dockableFrameAutohidden(DockableFrameEvent arg0){}
	public void dockableFrameAutohideShowing(DockableFrameEvent arg0){}
	public void dockableFrameActivated(DockableFrameEvent arg0){}
	public void dockableFrameDeactivated(DockableFrameEvent arg0){}
	public void dockableFrameTabShown(DockableFrameEvent arg0){}
	public void dockableFrameMaximized(DockableFrameEvent arg0){}
	public void dockableFrameRestored(DockableFrameEvent arg0){}
	public void dockableFrameTabHidden(DockableFrameEvent arg0){}
	//listener for hiding a docked frame
	public void dockableFrameHidden(DockableFrameEvent arg0)
	{
//		MainFrm.getInstance().getCatMenuBar().uncheckLegend();
	}

}