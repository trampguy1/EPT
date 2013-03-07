package mil.af.rl.jcat.util;

import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.log4j.Logger;


public class ResourceData extends Vector<Object>
{
	private static final long serialVersionUID = 1L;
	public static String[] fieldNames = {"Name", "Value", "Contingent"};
	public static Class[] fieldClasses = {String.class, Object.class, Boolean.class}; //not used right now
	private static Logger logger = Logger.getLogger(ResourceData.class);
	
	
	public ResourceData(String name, Object value, boolean contingent)
	{
		//this should stuff should match the resource table's order of values
		add(name);						//0
		add(value);						//1
		add(new Boolean(contingent));	//2
		
	}
	
	/**
	 * This input string should look like this "[Name, value, conting..]"
	 * @param savedResString
	 */
	public ResourceData(String savedResString)
	{
		try{
			StringTokenizer parser = new StringTokenizer(savedResString.substring(1, savedResString.length()-1), ",");
			
			add(parser.nextToken().trim());
			add(new Integer(parser.nextToken().trim()));
			add(new Boolean(parser.nextToken().trim()));
		}catch(NumberFormatException exc){
			logger.error("Constructor - NumberFormatExc parsing old resources:  "+exc.getMessage());
		}
	}
	
	
	public boolean isContingent()
	{
		return ((Boolean)get(2)).booleanValue();
	}
	
	public String getName()
	{
		return (String)get(0);
	}
	
	public Object getValue()
	{
		return get(1);
	}
	
}
