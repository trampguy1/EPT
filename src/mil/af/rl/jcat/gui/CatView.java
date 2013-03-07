package mil.af.rl.jcat.gui;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;

import mil.af.rl.jcat.control.Control;
import mil.af.rl.jcat.exceptions.PlotException;
import mil.af.rl.jcat.plan.AbstractPlan;
import mil.af.rl.jcat.plan.PlanItem;
import mil.af.rl.jcat.util.CatFileFilter;
import mil.af.rl.jcat.util.EnvUtils;
import mil.af.rl.jcat.util.Guid;

import com.c3i.jwb.JWBController;
import com.c3i.jwb.JWBControllerManager;
import com.c3i.jwb.JWBPanel;
import com.c3i.jwb.JWBShape;
import com.jidesoft.document.DocumentComponent;
import com.jidesoft.document.DocumentComponentEvent;
import com.jidesoft.document.DocumentComponentListener;


/*
 * Created on May 14, 2004
 *
 * Holds Graphical Methods Drawing and Gui Collaboration
 */

public class CatView extends DocumentComponent implements MouseListener, DocumentComponentListener, KeyListener
{
	//Instance of Abstract Plan
	private AbstractPlan _plan = null;
	protected String viewid = null;
	private JWBPanel panel = null;
	private File autoSaveTemp = null;
	private CatPopupManager popManager = null;
	private static Logger logger = Logger.getLogger(CatView.class);


	public CatView(JWBPanel jwbpanel, AbstractPlan plan, String viewid)
	{
		super(jwbpanel, viewid);
		this.viewid = viewid;

		panel = jwbpanel;
		// test
		panel.repaint(); //wtf is this?
		_plan = plan;
		setTitle(plan.getPlanName());
		setIcon(new javax.swing.ImageIcon(this.getClass().getClassLoader().getResource("jcat-doc.png")));

		// JWBPanel initialization by F10
		panel.setPopupManager(popManager = new CatPopupManager(panel));
		panel.setPreferredSize(new Dimension(1680, 1224));
		panel.addMouseListener(this);
		panel.addKeyListener(this);

		JScrollPane spane = new JScrollPane();
		spane.setPreferredSize(new Dimension(800, 600));
		/* reduces memory usage when scrolling the pane */
		spane.getViewport().setScrollMode(javax.swing.JViewport.SIMPLE_SCROLL_MODE);
		spane.getViewport().add(panel);
		setComponent(spane);
		addDocumentComponentListener(this);
		
		javax.help.CSH.setHelpIDString(panel, "Plan_Layout_Frame");
	}

	/**
	 * Mouse Clicked on canvas
	 */
	public void mouseClicked(MouseEvent e)
	{
		//Give us a Double Click event
		if (e.getButton() == MouseEvent.BUTTON1)
		{
			if (panel.getFocusedShape() != null  && e.getClickCount() >=2)
			{
				PlanItem item = _plan.getItem((Guid)panel.getFocusedShape().getAttachment());
				plot(item);
			}
			else if (panel.getFocusedShape() != null  && e.getClickCount() >=1)
			{
				PlanItem item = _plan.getItem((Guid)panel.getFocusedShape().getAttachment());

				PlanItem pItem = (PlanItem)item;
				MainFrm.getInstance().getPropertyViewer().updateViewer(pItem);
			}  	       
		}
	}

	//keyboard key is pressed while canvas has focus
	public void keyReleased(KeyEvent event)
	{
		if(event.getKeyCode() == KeyEvent.VK_DELETE)
		{
			JWBController pcontroller = JWBControllerManager.getInstance().getController(panel.getControllerUID());
			if(panel.getSelectedShapes().size() > 0 && JOptionPane.showConfirmDialog(MainFrm.getInstance(), "Are you sure you would like to delete these items?", "Delete", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
			{
				try{
					Iterator shapes = panel.getSelectedShapes().iterator();
					while(shapes.hasNext())
						pcontroller.removeShape(((JWBShape)shapes.next()).getUID());
				}catch(RemoteException exc){
					logger.error("keyPressed - RemoteExc removing shapes:  "+exc.getMessage());
				}
			}
		}
	}

	/**
	 *
	 */
	public void printAbstractPlan()
	{
		Object[] items = _plan.getItems().values().toArray();
		for (int i = 0; i < items.length; i++)
		{
			System.out.println(((PlanItem) items[i]).getGuid());
		}
	}

	/**
	 * @return Returns the panel.
	 */
	public JWBPanel getPanel()
	{
		return panel;
	}

	public CatPopupManager getPopupManager()
	{
		return popManager;
	}

	public void plot(PlanItem item)
	{
//		try{
	//WTF is this line ??????? just to test if sampler is running maybe?
		//item.setPredictedProbs(_plan.getPredictedProbs(new Guid(item.getGuid())));
		//////////////////////////
		try{
			ProfileDataModel.getInstance().addPlot(_plan, item);
		}catch(PlotException exc){
			JOptionPane.showMessageDialog(MainFrm.getInstance(), "Error plotting the selected item. \n"+exc.getMessage(), "Plot", JOptionPane.ERROR_MESSAGE);
		}
//		}catch(NullPointerException npe){
//		JOptionPane.showMessageDialog(MainFrm.getInstance(), "You must start the sampler before plotting any plan items.");
//		}
	}

	public boolean save()
	{
		String path = _plan.getFilePath();
		if(path == null)
			return saveAs();

		else
		{
			try{
				if(!Control.getInstance().savePlan(_plan.getId(), new File(path), false, false))
					return false;
			}catch(FileNotFoundException e)
			{
				logger.error("save - file not found occured while saving:  " + e.getMessage());
				return false;
			}
		}
		
		_plan.fireWasSaved();

		return true;
	}

	public boolean saveAs()
	{
		JFileChooser fileChooser = null;
		if(MainFrm.getInstance().getLastSaveDir() == null)
			fileChooser = new JFileChooser(EnvUtils.getUserDocHome() + EnvUtils.sep);
		else
			fileChooser = new JFileChooser(MainFrm.getInstance().getLastSaveDir());

		if(_plan.getFilePath() != null)
			fileChooser.setSelectedFile(new File(_plan.getFilePath()));

		int response = -1;
		boolean respond = false;
		boolean showDialog = false;
		while(!respond)
		{
			if(showDialog)
				JOptionPane.showMessageDialog(MainFrm.getInstance(), "Please Type a filename with no special characters.", "Illegal FileName", JOptionPane.ERROR_MESSAGE);
			fileChooser.setFileFilter(new CatFileFilter());
			response = fileChooser.showSaveDialog(MainFrm.getInstance());
			if (response == JFileChooser.APPROVE_OPTION)
			{
				try{
					if(!Control.getInstance().savePlan(_plan.getId(), fileChooser.getSelectedFile(), true, false))
						return false;
					
					respond = true;
				}catch(IOException fnf)
				{
					showDialog = true;
				}
			}
			else
				respond = true;
		}
		
		_plan.fireWasSaved();
		
		return true;
	}

	public void autoSave()
	{
		if(autoSaveTemp == null)
		{
			String time = String.valueOf(System.currentTimeMillis());
			time = time.substring(time.length()-5, time.length());
			autoSaveTemp = new File(EnvUtils.getJCATSettingsHome()+EnvUtils.sep+_plan.getPlanName()+time+".tmp");
			try{
				autoSaveTemp.createNewFile();
				autoSaveTemp.deleteOnExit();
			}catch(IOException exc){
				autoSaveTemp = null;
				logger.error("autoSave - IOExc autosaving:  "+exc.getMessage());
			}
		}
		try{
			//dont bother to autosave until a change is first made
			if(_plan.isModified())
				Control.getInstance().savePlan(_plan.getId(), autoSaveTemp, false, true);
		}catch(FileNotFoundException exc){
			logger.error("autoSave - FileNotFoundExc autosaving:  "+exc.getMessage());
		}
	}

	public AbstractPlan getPlan()
	{
		return _plan;
	}

	/* (non-Javadoc)
	 * @see com.jidesoft.document.DocumentComponent#cleanup()
	 */
	public void cleanup() {
		super.cleanup();
		try {
			Control.getInstance().stopPlanServer(_plan.getId());
			Control.getInstance().stopClient(_plan.getId());
			this._plan.cleanup();
			Control.getInstance().removePlan(_plan.getId());
			this._plan = null;
		} catch (Exception e) {
			logger.error("cleanup - error cleaning up catview/plan:  "+e.getMessage());
		}
	}

	public void documentComponentClosing(DocumentComponentEvent arg0)
	{
		setAllowClosing(false);//dont let DocumentComponent handle the X button on the tab
		removeDocumentComponentListener(this);//prevents an event loop
		MainFrm.getInstance().closePlan();
	}

	public void documentComponentActivated(DocumentComponentEvent arg0)
	{
		MainFrm.getInstance().getCatToolBar().deselectButtons();
		MainFrm.getInstance().getNavTree().populateTree(getPlan());
		MainFrm.getInstance().getResourceViewer().updateViewer(getPlan());
		MainFrm.getInstance().getCOAViewer().updateViewer(getPlan());
		MainFrm.getInstance().startUpdateThread();
		getPlan().getColorScheme().makeActive();
		MainFrm.getInstance().getSchemeLegend().updateLegend();
	}

	public void reAddListener()
	{
		addDocumentComponentListener(this);
	}

	public void removeListener()
	{
		removeDocumentComponentListener(this);
	}

	/* public void updateStatus()
    {
        try{
        MainFrm.getInstance().getCatToolBar().setBayesNetRunning(
                getPlan().getBayesNet().isSampling());
        }catch(Exception e)
        {
            MainFrm.getInstance().getCatToolBar().setBayesNetRunning(false);
        }
    }*/

	//unused mouse
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mousePressed(MouseEvent e){}
	public void mouseReleased(MouseEvent e){}

	//unused document events
	public void documentComponentClosed(DocumentComponentEvent arg0){}
	public void documentComponentDeactivated(DocumentComponentEvent arg0){}
	public void documentComponentOpened(DocumentComponentEvent arg0){}

	//usused keyevents
	public void keyPressed(KeyEvent e){}
	public void keyTyped(KeyEvent event){}
}
