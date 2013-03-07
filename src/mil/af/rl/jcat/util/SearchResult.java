package mil.af.rl.jcat.util;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class SearchResult implements Cloneable
{
    Object[] arResult = new Object[2];

    public SearchResult()
    {
    }

    //I had to override the clone() method
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }

    public void setResult(Object[] inArray)
    {
        arResult = inArray;
    }

    public Object[] getResult()
    {
        return arResult;
    }

}
