/*
 * Created on 7-Nov-06
 *
 */
package mil.af.rl.jcat.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import mil.af.rl.jcat.plan.PlanItem;
import mil.af.rl.jcat.plan.ResourceAllocation;
import mil.af.rl.jcat.plan.ResourceManager;

public class ResourceSetSelectionDialog extends JDialog
{
    private JTable table;
    private JButton ok =  new JButton("Ok");
    private JButton cancel =  new JButton("Cancel");
    private JButton create = new JButton("Create Pairing");
    private JButton delete = new JButton("Delete Pairing");
    private ResourceManager manager;
    private PlanItem item;
    
    public ResourceSetSelectionDialog(Frame parent, PlanItem item, ResourceManager manager) throws HeadlessException
    {
        super(parent, "Resource Pairing Selection");
        this.manager = manager;
        this.item = item;
        this.getContentPane().setLayout(new BorderLayout());
        JPanel btnPane = new JPanel();
        btnPane.setLayout(new GridLayout(1,2));
        ok.setSize(60, 20);
        cancel.setSize(60, 20);
        btnPane.add(ok);
        btnPane.add(cancel);
        JPanel sidePane = new JPanel();
        sidePane.setLayout(new GridLayout(2,1));
        create.setSize(60, 20);
        delete.setSize(60, 20);
        sidePane.add(create);
        sidePane.add(delete);
        table = new JTable(new ResourceSetTableModel(manager));
        this.getContentPane().add(sidePane, BorderLayout.EAST);
        this.getContentPane().add(btnPane, BorderLayout.SOUTH);
        this.getContentPane().add(new JScrollPane(table));
        this.setSize(600, 300);
        this.setAlwaysOnTop(true);
        this.setResizable(false);
        this.setLocationRelativeTo(parent);
        this.setVisible(true);
    }
    
    public Collection<ResourceAllocation> getResourcesFromParents(PlanItem item)
    {
        return null;
    }
    /**
     * Internal Class to represent pairings within ResourceSetSelectionDialog
     * 
     * @author craig
     *
     */
    class ResourceSetTableModel implements TableModel
    {
        ResourceManager manager;
        ArrayList sets = new ArrayList();
        
        ResourceSetTableModel(ResourceManager manager)
        {
            this.manager = manager;
        }

        public int getRowCount()
        {
            return manager.getRowCount();
        }

        public int getColumnCount()
        {
            //One extra for the resource names
            return sets.size() + 1;
        }

        public String getColumnName(int columnIndex)
        {
            if(columnIndex == 0)
            {
                manager.getColumnName(0);
            }
            else
            {
                //Other Stuff
            }
            return null;
        }

        public Class<?> getColumnClass(int columnIndex)
        {
            return String.class;
        }

        public boolean isCellEditable(int rowIndex, int columnIndex)
        {
            return false;
        }

        public Object getValueAt(int rowIndex, int columnIndex)
        {
            if(columnIndex == 0)
            {
                return manager.getValueAt(rowIndex, columnIndex);
            }                
            return null;
        }

        public void setValueAt(Object aValue, int rowIndex, int columnIndex)
        {
            
        }

        public void addTableModelListener(TableModelListener l)
        {
            
        }

        public void removeTableModelListener(TableModelListener l)
        {
            
        }
        
    }
}
