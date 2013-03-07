/*
 * Created on Aug 5, 2005
 */
package mil.af.rl.jcat.gui.table.model;

import com.jidesoft.grid.Property;

/**
 * @author dygertm
 *
 */
public class TableProperty extends Property
{
	Object value = null;
	/**
	 * @param arg0
	 */
	public TableProperty(String name, Object value, String category)
	{
		super(name);
		setValue(value);
		setCategory(category);
		setEditable(false);
	}

	//
	public void setValue(Object newValue)
	{
		value = newValue;		
	}

	//
	public Object getValue()
	{
		return value;
	}

	//
	public boolean hasValue()
	{
		return false;
	}

}
