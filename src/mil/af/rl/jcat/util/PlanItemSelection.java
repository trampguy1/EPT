
package mil.af.rl.jcat.util;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.*;

/**
 * @author verenice
 * @company Primate Technologies / C3I Associates
 * 2005
 */
public class PlanItemSelection implements Transferable {

    private static DataFlavor dF = new DataFlavor(ArrayList.class,"ArrayList.class");
    private ArrayList list;
    
    public PlanItemSelection(ArrayList items)
    {
        list = items;
    }
    /* 
     */
    public DataFlavor[] getTransferDataFlavors() {
        
        return new DataFlavor[]{dF};
    }


    public boolean isDataFlavorSupported(DataFlavor flavor) {
       
        return dF.equals(flavor);
    }

    /* 
     * Returns list of shapes
     */
    public Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException, IOException {
        
        return list;
    }

}
