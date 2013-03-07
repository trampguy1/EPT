/*
 * Created on Aug 10, 2004
 *
 */
package mil.af.rl.jcat.gui.documentationpanels;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.AbstractCellEditor;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

import mil.af.rl.jcat.util.Resource;

/**
 * @author vogels
 * 
 */
public class ReferenceCellEditor extends AbstractCellEditor implements TableCellEditor
{
	private static final long serialVersionUID = 1L;

	JTextField component = new JTextField();

    JPopupMenu popup = new JPopupMenu();

    String strVal = null;

    Resource resource = null;

    ReferenceCellEditor(Resource r)
    {
        resource = r;
        component.addMouseListener(new ResourcePanel_resourceCell_mouseAdapter(
                this));

        popup.add("File").addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                fileSelect(strVal);
                resource.setType(Resource.FILE);
            }
        });
        popup.add("URL").addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                urlSelect(strVal);
                resource.setType(Resource.URL);
            }
        });
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column)
    {
        strVal = (String) value;
        if (isSelected)
        {
            // cell (and perhaps other cells) are selected
        }

        // Configure the component with the specified value
        ((JTextField) component).setText((String) value);

        // Return the configured component
        return component;

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.CellEditor#getCellEditorValue()
     */
    public Object getCellEditorValue()
    {
        return ((JTextField) component).getText();
    }

    public Resource getResource()
    {
        return resource;
    }

    void resourceTable_mouseClicked(MouseEvent e)
    {
//        if (e.getClickCount() > 1)
//        {
//            try
//            {
//                String cmdLine = "cmd.exe /c start "
//                        + ((JTextField) component).getText();
//                //note that to start URLs cmd.exe /c start is necessary
//                Process p = Runtime.getRuntime().exec(cmdLine);
//            }
//            catch (Exception ex)
//            {
//                ex.printStackTrace();
//            }
//        }
        if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0)
        {
            popup.show(e.getComponent(), e.getX(), e.getY());
        }

    }

    public void fileSelect(String currentVal)
    {
        JFileChooser fc = new JFileChooser();
        JFrame fileOpen = new JFrame();

        fc.showOpenDialog(fileOpen);
        File selFile = fc.getSelectedFile();
        File fpath = fc.getCurrentDirectory();
        String sPath = fpath.getAbsolutePath();
        sPath += "\\";
        sPath += fc.getName(selFile);
        currentVal = sPath;
        resource.setLocation(sPath);
        component.setText(sPath);
    }

    public void urlSelect(String currentVal)
    {
        Resource r = new Resource();
        URLDialog urlDialog = new URLDialog(r);
        urlDialog.setVisible(true);
        component.setText(r.getLocation());
    }

}

class ResourcePanel_resourceCell_mouseAdapter extends
        java.awt.event.MouseAdapter
{
    ReferenceCellEditor adaptee;

    ResourcePanel_resourceCell_mouseAdapter(ReferenceCellEditor adaptee)
    {
        this.adaptee = adaptee;
    }

    public void mouseClicked(MouseEvent e)
    {
        adaptee.resourceTable_mouseClicked(e);
    }
}
