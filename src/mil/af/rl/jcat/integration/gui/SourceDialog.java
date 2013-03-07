package mil.af.rl.jcat.integration.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import mil.af.rl.jcat.integration.ConnectionManager;
import mil.af.rl.jcat.integration.parser.InputParser;
import mil.af.rl.jcat.integration.parser.OutputParser;
import mil.af.rl.jcat.util.Guid;

import com.c3i.jwb.JWBController;
import com.c3i.jwb.JWBShape;

public class SourceDialog extends JDialog implements ActionListener{

    private static final long serialVersionUID = 6738759939608635253L;
    private ResultSet rset;
    //private JSplitPane splitPane;
    private JScrollPane sourceTableScrollPane;
    private SourceTable sourceTypeTable;
    private JList applicationList;
    private JList queryList;
    //private JPanel rightPanel;
    private JButton attachSource;
    private JButton attachTarget;
    private JButton refreshData;
    private JButton filterData;
    private JButton createVariable;
    private JTextField varName;
    
    private JSpinner updateTime;
    private JPanel buttonPanel;
    private JPanel mainPanel;
    private static boolean filter = false;
    private Statement stm;
    private JWBController control;
    private ArrayList<JWBShape> shapes;
    /**
     * This method initializes 
     * 
     */
    
    
    public SourceDialog(ArrayList<JWBShape> shapes, JWBController c)
    {
        super();
        setTitle("JCAT Integration Center");
        setModal(true);
        super.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        initialize();
        this.shapes = shapes;
        control = c;  
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        setSize(new java.awt.Dimension(800,300));
        this.mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(1,2));
    
        mainPanel.add(this.getSourceTableScrollPane());
        mainPanel.add(new JScrollPane(this.getQueryList()));
        
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(this.getApplicationList(),BorderLayout.WEST);
        
        this.getContentPane().add(mainPanel,BorderLayout.CENTER);
        this.getContentPane().add(this.getButtonPanel(),BorderLayout.SOUTH);	
    }

   

    /**
     * This method initializes sourceTableScrollPane	
     * 	
     * @return javax.swing.JScrollPane	
     */
    private JScrollPane getSourceTableScrollPane() {
        if (sourceTableScrollPane == null) {
            sourceTableScrollPane = new JScrollPane();
            sourceTableScrollPane.setViewportView(getSourceTypeTable());
        }
        return sourceTableScrollPane;
    }
    
    

    /**
     * This method initializes sourceTypeTable	
     * 	
     * @return javax.swing.JTable	
     */
    private JTable getSourceTypeTable() {
        if (sourceTypeTable == null) {
            sourceTypeTable = new SourceTable();
        }
        return sourceTypeTable;
    }

    /**
     * This method initializes applicationList	
     * 	
     * @return javax.swing.JList	
     */
    private JList getApplicationList() {
        if (applicationList == null) {
            DefaultListModel model = new DefaultListModel();
            int x =0;
            for(String name : ConnectionManager.INSTANCE().getAppNames())
            {
                model.add(x,name);x++;
            }
            applicationList = new JList(model);
           
        }
        return applicationList;
    }
    
    private JList getQueryList()
    {
        if(queryList == null)
        {
            DefaultListModel m = new DefaultListModel();
            int x = 0;
            for(String name : ConnectionManager.INSTANCE().getQueryNames())
            {
                m.add(x,name);x++;
            }
            
            queryList = new JList(m);
        }
        return queryList;  
    }
    
   
    
    private JPanel getButtonPanel()
    {
        buttonPanel = new JPanel();
        createVariable = new JButton("New Variable");
        createVariable.addActionListener(this);
        varName = new JTextField(14);
        buttonPanel.add(createVariable);
        buttonPanel.add(varName);
        
        filterData = new JButton("Filter Data");
        filterData.addActionListener(this);
        refreshData = new JButton("Refresh Data");
        refreshData.addActionListener(this);
        attachSource = new JButton("Attach Source");
        attachSource.addActionListener(this);
        attachTarget = new JButton("Attach Target");
        attachTarget.addActionListener(this);
        this.updateTime = new JSpinner(new SpinnerNumberModel(25,0,1000,1));
        buttonPanel.add(filterData);
        buttonPanel.add(refreshData);
        buttonPanel.add(attachTarget);
        buttonPanel.add(attachSource);
        buttonPanel.add(new JLabel("Execute Every:"));
        buttonPanel.add(updateTime);
        
        return buttonPanel;
    }
    
    private void createVariable()
    {
        Runnable r = new Runnable(){
            public void run()
            {
                String application;
                String var;
                Connection con = null;
                String update;
                Object[] values = applicationList.getSelectedValues();
                if(values == null || values.length > 1 || values.length < 1)
                {
                    JOptionPane.showMessageDialog(null,"You must select one application!");
                    return;
                }
                application = values[0].toString();
                var = varName.getText().trim();
                if(var == null || var.length() < 1)
                {
                    JOptionPane.showMessageDialog(null,"Provide a variable name!");
                    return;
                }
                
                update = "INSERT INTO element VALUES ('" + application +"' ,'" + 
                application +"' ,'" + new Guid() + "' ,'" + var +"' ,'"+
                var + "' ," + "'user'" + " ," + "' '" +" ,"+"UNIX_TIMESTAMP())";   
                con = ConnectionManager.INSTANCE().getConnection("coweb");
                
                
                try{
                    if(stm == null)
                        stm = con.createStatement();
                    stm.executeUpdate(update);
                }catch(SQLException sx)
                {
                    sx.printStackTrace(System.err);
                }
                refreshResultSet(true);
            }
            
        };
        
        new Thread(r).start();
        
    }
    
    private void refreshResultSet(boolean f)
    {
        filter = f;
        Runnable r = new Runnable(){
            public void run()
            {
                Connection con = null;
                String query = "SELECT * FROM ibc.element WHERE ";
                Object[] values;
                if(filter)
                {
                    
                    values = applicationList.getSelectedValues();
                    
                    if(values.length < 1)
                    {
                        JOptionPane.showMessageDialog(null,"Select Application Filters");
                        return;
                    }
                    
                }else{
                    values = ConnectionManager.INSTANCE().getAppNames().toArray();
                }
                    int x = 0;
                    
                    for(Object n : values)
                    {
                        query = query + "application = '"+values[x]+"'";
                        if(x != values.length - 1)
                        {
                          query = query + " OR ";  
                        }
                        x++;
                    }
                
                filter = false;
              
                con = ConnectionManager.INSTANCE().getConnection("coweb");
                
                
                try{
                    if(stm == null)
                        stm = con.createStatement();
                    rset = stm.executeQuery(query);
                }catch(SQLException sx)
                {
                    sx.printStackTrace(System.err);
                }
                sourceTypeTable.setModel(new SourceTableModel(rset));
                //sourceTypeTable.repaint();
            }
            
        };
        
        new Thread(r).start();
        
        
    }
    
    private void attachTarget()
    {
        String queryname = queryList.getSelectedValue().toString();
        String parsername = ConnectionManager.INSTANCE().getParser(queryname);
        Class[] params = {int.class,Object[].class,ArrayList.class,JWBController.class,String.class};
        Object[] row = new Object[4];
        int srow = sourceTypeTable.getSelectedRow();
        row[0] = sourceTypeTable.getValueAt(srow,1);
        row[1] = sourceTypeTable.getValueAt(srow,2);
        row[2] = sourceTypeTable.getValueAt(srow,3);
        row[3] = sourceTypeTable.getValueAt(srow,0);
       
        int seconds =  Integer.parseInt(updateTime.getValue().toString());
        Class cl;
        OutputParser parser = null;
        Constructor construct;
        try{
            cl = Class.forName(parsername);
            construct = cl.getConstructor(params);
            // finally create an instance of the parser, yo..
            parser = (OutputParser)construct.newInstance(new Object[]{seconds,row,shapes,control,queryname});
            
        }catch(ClassNotFoundException cnf)
        {
            cnf.printStackTrace(System.err);
        }catch(NoSuchMethodException nsm)
        {
            nsm.printStackTrace(System.err);
        }catch(InstantiationException e){
        }catch(IllegalAccessException e){
        }catch(InvocationTargetException e){
            e.printStackTrace(System.err);
        }
        // now execute the input
        Thread worker  = new Thread(parser);     
        try{
            worker.setPriority(Thread.MIN_PRIORITY);
        }catch(SecurityException e)
        {
            
        }
        worker.start();
        
    }
    
    private void attachSource()
    {
        int srow = sourceTypeTable.getSelectedRow();
        if(queryList.getSelectedIndex() == -1 || srow == -1)
        {
            JOptionPane.showMessageDialog(this,"Both QUERY & Variable must be selected!");
            return;
        }
        String queryname = queryList.getSelectedValue().toString();
        String parsername = ConnectionManager.INSTANCE().getParser(queryname);
        Class[] params = {int.class,int.class,ArrayList.class,JWBController.class,String.class,Object[].class};
        Object[] row = new Object[4];
        
        row[0] = sourceTypeTable.getValueAt(srow,1);
        row[1] = sourceTypeTable.getValueAt(srow,2);
        row[2] = sourceTypeTable.getValueAt(srow,3);
        row[3] = sourceTypeTable.getValueAt(srow,0);
        
        int type = 0;
        int seconds =  Integer.parseInt(updateTime.getValue().toString());
        Class cl;
        InputParser parser = null;
        Constructor construct;
        try{
            cl = Class.forName(parsername);
            construct = cl.getConstructor(params);
            // finally create an instance of the parser, yo..
            parser = (InputParser)construct.newInstance(new Object[]{seconds,type,shapes,control,queryname,row});
            
        }catch(ClassNotFoundException cnf)
        {
            cnf.printStackTrace(System.err);
        }catch(NoSuchMethodException nsm)
        {
            nsm.printStackTrace(System.err);
        }catch(InstantiationException e){
        }catch(IllegalAccessException e){
        }catch(InvocationTargetException e){
            e.printStackTrace(System.err);
        }
        // now execute the input
       
        Thread worker  = new Thread(parser);     
        try{
            worker.setPriority(Thread.MIN_PRIORITY);
        }catch(SecurityException e)
        {
            
        }
        worker.start();
    }
    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(this.refreshData))
        {
            this.refreshResultSet(false);
            
        }else if(e.getSource().equals(this.filterData))
        {
            this.refreshResultSet(true);
        }else if(e.getSource().equals(this.attachSource))
        {
            this.attachSource();
        }else if(e.getSource().equals(this.createVariable))
        {
            this.createVariable();
        }else if(e.getSource().equals(this.attachTarget))
        {
            this.attachTarget();
        }
        
    }
    
    public static void main(String[] args)
    {
        //ConnectionManager m = ConnectionManager.INSTANCE();
        //new SourceDialog(null).setVisible(true);
        SAXReader reader = new SAXReader();
        try{
        Document doc = reader.read(new File("resources/properties/TEST.xml"));
        System.out.print(doc.asXML());
        }catch(Exception e)
        {
            e.printStackTrace(System.err);
        }
    }

    
    

}  //  @jve:decl-index=0:visual-constraint="35,16"
