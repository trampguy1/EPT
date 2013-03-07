/*
 * Created on Feb 14, 2007
 */
package mil.af.rl.jcat.control;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.c3i.jwb.JWBUID;

import mil.af.rl.jcat.plan.COA;
import mil.af.rl.jcat.plan.ColorScheme;
import mil.af.rl.jcat.plan.Documentation;
import mil.af.rl.jcat.plan.PlanItem;
import mil.af.rl.jcat.util.Guid;
import mil.af.rl.jcat.util.MultiMap;

public class PlanArgument implements Serializable
{
	/** Argument type to request entire Plan */
	public static final int PLAN_REQUEST = 0;
	/** Argument type to send the entire Plan */
	public static final int PLAN_RESPONSE = 1;
	/** Argument type to add an PlanItem */
	public static final int ITEM_ADD = 2;
	/** Argument type to remove an PlanItem */
	public static final int ITEM_REMOVE = 3;
	/** Argument type to modify an existing PlanItem */
	public static final int ITEM_UPDATE = 4;
	/** Argument type to paste an item for copy/paste */
	public static final int ITEM_PASTE = 5;
	/** Argument type to update plan documentation */
	public static final int PLAN_DOCUMENTATION = 6;
	/** Argument type to update plan COA list */
	public static final int PLAN_COAS = 7;
	/** Argument type to update plans current colorscheme */
	public static final int PLAN_COLORSCHEME = 8;
	/** Argument type to update plans active coa */
	public static final int PLAN_ACTIVECOA = 9;
	

	private Parameters param = null;
	private int type = -1;
	private boolean isFromCOA = false;
	private boolean isAutomated = false;
	
	/**
	 * Should be used for: PLAN_REQUEST
	 */
	public PlanArgument(int type)
	{
		this.type = type;
		param = new Parameters();
	}
	
	/**
	 * Should be used for: ITEM_REMOVE, ITEM_UPDATE
	 * @param isFromCOA specifies whether or not this argument is associated with a COA apply
	 */
	public PlanArgument(int type, PlanItem item, boolean isFromCOA)
	{
		this.type = type;
		this.isFromCOA = isFromCOA;
		param = new Parameters();
		ArrayList<PlanItem> items = new ArrayList<PlanItem>();
		items.add(item);
		param.setItemList(items);
	}
	
	/**
	 * Should be used for: ITEM_ADD
	 */
	public PlanArgument(int type, PlanItem item, JWBUID uid)
	{
		this.type = type;
		param = new Parameters();
		ArrayList<PlanItem> items = new ArrayList<PlanItem>();
		items.add(item);
		param.setItemList(items);
		ArrayList<JWBUID> uids = new ArrayList<JWBUID>();
		uids.add(uid);
		param.setMappedUIDs(uids);
	}
	
	/**
	 * Should be used for: ITEM_ADD typically for a consolidator that would be added with more then one shape mapping
	 */
	public PlanArgument(int type, PlanItem item, List<JWBUID> uids)
	{
		this.type = type;
		param = new Parameters();
		ArrayList<PlanItem> items = new ArrayList<PlanItem>();
		items.add(item);
		param.setItemList(items);
		param.setMappedUIDs(new ArrayList<JWBUID>(uids));
	}
	
	/**
	 * Should be used for: ITEM_UPDATE, ITEM_REMOVE
	 */
	public PlanArgument(int type, ArrayList<PlanItem> items, boolean isFromCOA)
	{
		this.type = type;
		this.isFromCOA = isFromCOA;
		param = new Parameters();
		param.setItemList(items);
	}
	
	/**
	 * Should be used for: PLAN_RESPONSE, ITEM_PASTE
	 */
	public PlanArgument(int type, ArrayList<PlanItem> items, MultiMap<Guid, JWBUID> map)
	{
		this.type = type;
		param = new Parameters();
		param.setItemList(items);
		param.setItemMap(map);
	}
	
	
	public int getType()
	{
		return type;
	}
	
	public Parameters getParameters()
	{
		return param;
	}
	
	public boolean isFromCOA()
	{
		return isFromCOA;
	}
	
	public void setIsAutomated(boolean b)
	{
		isAutomated = b;
	}
	
	/**
	 * Indicates the argument is not from normal placment of an item on whiteboard, this arg may need
	 * different treatment as it is from a tool or something
	 * @return
	 */
	public boolean isAutomated()
	{
		return isAutomated;
	}
	
	//hmm an inner class, how perplexing... tryin something different, might change later
	public class Parameters implements Serializable
	{
		private ArrayList<PlanItem> items = null;
		private ArrayList<JWBUID> uids = null;
		private MultiMap<Guid, JWBUID> itemMap = null;
		///////// other options that can be set to the parameters directly if desired (must be serializable!)
		public float[] activeDefaultsSet = null;
		public Documentation planDoc = null;
		public ColorScheme colorScheme = null;
		public java.util.Vector<COA> coaList = null;
		public int[] activeCOAs = null;
		///////////////////////////////////////////////////////////////////////
		
		
		public PlanItem getItem()
		{
			if(items.size() < 1)
				return null;
			
			return items.get(0);
		}
		
		public ArrayList<PlanItem> getItems()
		{
			return items;
		}
		
		public void setItemList(ArrayList<PlanItem> itemList)
		{
			this.items = itemList;
		}
		
		
		public MultiMap<Guid, JWBUID> getItemMap()
		{
			return itemMap;
		}
		
		public void setItemMap(MultiMap<Guid, JWBUID> map)
		{
			itemMap = map;
		}
		
		
		public ArrayList<JWBUID> getMappedUIDs()
		{
			return uids;
		}
		
		public void setMappedUIDs(ArrayList<JWBUID> theUids)
		{
			this.uids = theUids;
		}
		
	}


	
}
