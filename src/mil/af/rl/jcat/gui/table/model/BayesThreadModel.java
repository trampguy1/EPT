
package mil.af.rl.jcat.gui.table.model;

import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;

import mil.af.rl.jcat.control.Control;
import mil.af.rl.jcat.plan.AbstractPlan;


/**
 * <p>Title: BayesThreadModel.java</p>
 * <p>Description: Used to keep track of the plans</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: C3I Associates</p>
 * @author Edward Verenich
 * @version 1.0
 */
public class BayesThreadModel extends AbstractTableModel
{

	private static final long serialVersionUID = 1L;
	private Object dataLock;
	private int rowCount;
	private Object[][] cellData;
	private Object[][] pendingCellData;
	private final int columnCount; //column info stays constant
	private final String[] columnName;
	private final Class[] columnClass;
	//self-running object control variables
	private Thread internalThread;
	private volatile boolean noStopRequested;
	private java.util.List plans;
	private static Logger logger = Logger.getLogger(BayesThreadModel.class);

	public BayesThreadModel()
	{
		rowCount = 0;
		cellData = new Object[0][0];
		// get the rigistry list
		String[] names = { "Model Name", "Sampling", "Pause", "Kill" };
		columnName = names;
		columnCount = names.length;
		Class[] classes = { String.class, Boolean.class, Boolean.class, Boolean.class };

		// get the plans yo
		plans = new java.util.LinkedList(Control.getInstance().getPlanObjects());
		columnClass = classes;
		dataLock = new Object();
		noStopRequested = true;
		Runnable r = new Runnable()
		{

			public void run()
			{
				try
				{
					runWork();
				}catch(Exception ex)
				{
					logger.error("Constructor-run Exception in thread:  " + ex.getMessage());
				}
			}
		};

		internalThread = new Thread(r, "Active Models");
		internalThread.setPriority(Thread.MAX_PRIORITY - 2);
		internalThread.setDaemon(true);
		internalThread.start();

	}

	/**
	 * Returns the number of columns in the model.
	 *
	 * @return the number of columns in the model
	 * @todo Implement this javax.swing.table.TableModel method
	 */
	private void runWork()
	{
		Runnable transferPending = new Runnable()
		{

			public void run()
			{
				transferPendingCellData();
				fireTableDataChanged(); // causes the table to be updated
			}
		};

		while(noStopRequested)
		{
			try
			{
				// refresh the plans vector
				plans = new java.util.LinkedList(Control.getInstance().getPlanObjects());
				createPendingCellData();
				SwingUtilities.invokeAndWait(transferPending);
				Thread.sleep(2000L); // the REFRESH rate is set at two seconds
			}catch(InvocationTargetException tx)
			{
				logger.error("runWork - InvocationTargetException building cell data: ", tx);
				stopRequest();
			}catch(InterruptedException x)
			{
				Thread.currentThread().interrupt();
			}
		}

	} //end of runWork()

	/** Method used to issue a stop request
	 *
	 */
	public void stopRequest()
	{
		noStopRequested = false;
		internalThread.interrupt();
	}

	/** Method used to check if the current thread is alive
	 *  @return <b>boolean</b> <i>true</i> if thread is alive
	 */
	public boolean isAlive()
	{
		return internalThread.isAlive();
	}

	/** Method called by <i>internal</i> thread, provided here
	 *  for <b>reference only</b>.
	 */
	private void createPendingCellData()
	{

		Object[][] cell = new Object[plans.size()][columnCount];
		AbstractPlan plan = null;

		//now for each thread populate the table
		for(int i = 0; i < plans.size(); i++)
		{
			byte[] by = new byte[3];
			try
			{
				plan = (AbstractPlan) plans.get(i);
			}catch(ClassCastException e)
			{
				logger.error("createPendingCellData - attempt to put non AbstPlan objects in thread-model failed");
			}
			Object[] rowCell = cell[i];
			//now populate each column
			rowCell[0] = plan.getPlanName();
			Boolean sampling = plan.getBayesNet() == null ? new Boolean(false) : new Boolean(plan.getBayesNet().isSampling());
			rowCell[1] = sampling;
			Boolean paused = plan.getBayesNet() == null ? new Boolean(false) : new Boolean(plan.getBayesNet().isSamplerPaused());
			rowCell[2] = paused;
			rowCell[3] = new Boolean(false);

		}
		//make sure concurrency is safe
		synchronized(dataLock)
		{
			pendingCellData = cell;
		}
	} //end of createPendingCellData()

	/** Method called by the <i>event</i> thread,
	 *  <b>cannot</b> be called from an outside class and
	 *  is provide for <b>reference only</b>.
	 */
	private void transferPendingCellData()
	{
		synchronized(dataLock)
		{
			cellData = pendingCellData;
			rowCount = cellData.length;
		}
	}

	/** Method returns the number of rows in the table model, it is
	 * called by the event thread
	 * @return <b>int</b> <i>number</i> of rows
	 */
	public int getRowCount()
	{
		return rowCount;
	}

	/** Method returns an Object located at a given location
	 * @param <b>int</b> row
	 * @param <b>int</b> column
	 * @return java.lang.<b>Object</b> located at data[<i>row</i>][<i>col</i>]
	 */
	public Object getValueAt(int row, int col)
	{
		return cellData[row][col];
	}

	/** Method returns the number of columns in the table model
	 * @return <b>int</b> number of columns
	 */
	public int getColumnCount()
	{
		return columnCount;
	}

	/** Method returns a class from the column info.  Note that column
	 * information stays constant
	 * @param <b>int</b> column index
	 * @return java.lang.<b>Class</b> at a given index
	 */
	public Class getColumnClass(int columnIdx)
	{
		return columnClass[columnIdx];
	}

	/** Method returns a class name from the column info.  Note that column
	 * information stays constant
	 * @param <b>int</b> column index
	 * @return java.lang.<b>String</b> name at a given index
	 */
	public String getColumnName(int columnIdx)
	{
		return columnName[columnIdx];
	}

	public void setValueAt(Object value, int rowIndex, int columnIndex)
	{
		// these actions are column dependent

		if(columnIndex == 2)
		{ // object selected
			boolean val = ((Boolean) value).booleanValue();
			Boolean newVal = new Boolean(val);
			pendingCellData[rowIndex][columnIndex] = newVal;
			// THIS IS WHERE WE SHOULD PAUSE IT or resume, when it ready
			//((AbstractPlan)plans.get(rowIndex)).getBayesNet();
			if(val)
			{
				if(plans != null)
				{
					try
					{
						// if there is no BayesNet constructed inside a plan, it will just
						// error out, nothing bad...
						((AbstractPlan) plans.get(rowIndex)).getBayesNet().pauseSampler();
					}catch(Exception noSamplerInside)
					{
						logger.info("setValueAt(pause) - Error, no sampler to pause");
					}
				}

			}
			else
			{
				if(plans != null)
				{
					try
					{
						// if there is no BayesNet constructed inside a plan, it will just
						// error out, nothing bad...
						((AbstractPlan) plans.get(rowIndex)).getBayesNet().unpauseSampler();
					}catch(Exception noSamplerInside)
					{
						logger.info("setValueAt(unpause) - Error, no sampler to unpause");
					}
				}

			}
			this.fireTableCellUpdated(rowIndex, columnIndex);

		}
		else if(columnIndex == 3)
		{
			// here we just kill it, no matter what the value is

			if(plans != null)
			{
				try
				{
					// if there is no BayesNet constructed inside a plan, it will just
					// error out, nothing bad...
					((AbstractPlan) plans.get(rowIndex)).getBayesNet().killSampler();
				}catch(Exception noSamplerInside)
				{
					logger.info("setValueAt(kill) - Error, no sampler to kill");
				}
			}

		}

	}

	/**
	 * Method returns true if a cell should be editable
	 * @param row
	 * @param column
	 * @return
	 */
	public boolean isCellEditable(int row, int column)
	{
		return true;
	}

}
