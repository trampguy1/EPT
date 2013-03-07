/*
 * Created on Sep 7, 2005
 *
 */
package mil.af.rl.jcat.gui.dialogs;

import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import mil.af.rl.jcat.util.*;
import mil.af.rl.jcat.gui.MainFrm;
/**
 * @author verenice
 * @company Primate Technologies / C3I Associates
 * 2005
 */
public class PlanTreeViewDialog extends JDialog {
    

	private static final long serialVersionUID = 1L;
	private XTree treeview = null;
    private Document document;
    private static Logger logger = Logger.getLogger(PlanTreeViewDialog.class);
    
    public PlanTreeViewDialog(Document doc)
    {
        super(MainFrm.getInstance());
        super.setPreferredSize(new Dimension(500, 400));
        setTitle("Model Tree Explorer");
        //this.setModal(true);
      
        setLocation((int) MainFrm.getInstance().getLocationOnScreen().getX() + 150,
                (int) MainFrm.getInstance().getLocationOnScreen().getY() + 150);
        document = doc;
        init();
        pack();
        
    }
    
    private void init()
    {
        try{
        	treeview = new XTree(XMLUtil.toDOM(document));
        }catch(Exception e){
        	logger.error("init - error creating tree document view:  "+e.getMessage());
            return;
        }
        JScrollPane sp = new JScrollPane();
        sp.getViewport().add(treeview);
        this.getContentPane().add(sp);
    }
    
    
    
    
    

}
