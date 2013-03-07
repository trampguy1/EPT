/*
 * Created on May 17, 2004
 *
 */
package mil.af.rl.jcat.util;

import org.apache.log4j.Logger;

/**
 * @author Craig McNamara
 *  
 */
public class EnvUtils {
    
    public static String sep = System.getProperty("file.separator");
    public static String userHome = System.getProperty("user.home");
    private static Logger logger = Logger.getLogger(EnvUtils.class);
    
    public static String getUserDocHome()
    {
        String env  = System.getProperty("os.name");
        if(env.startsWith("Windows"))
            return userHome + sep + "My Documents" ; 
        else
            return userHome;   
    }
    
    public static String getUserHome()
    {
        return userHome;
    }

    public static String getJCATSettingsHome()
    {
    	//ensure this directory exists
    	try{
	    	java.io.File jcatDir = new java.io.File(getUserHome() + sep + ".JCAT");
	    	if(!jcatDir.isDirectory())
	    		jcatDir.mkdir();
    	}catch(Exception exc){
    		logger.warn("getJCATSettingsHome - could not create .JCAT directory in user home:  "+exc.getMessage());
    	}
    	
    	return getUserHome() + sep + ".JCAT";
    }
}