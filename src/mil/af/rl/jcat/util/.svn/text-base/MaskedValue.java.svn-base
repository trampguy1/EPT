/*
 * Created on Aug 31, 2005
 */
package mil.af.rl.jcat.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.log4j.Logger;

import mil.af.rl.jcat.exceptions.MissingRequiredFileException;

public class MaskedValue implements ActionListener
{
	private static final String PROP_FILE_NAME = "maskedvalues.properties";
	public static final MaskedFloat defaultMax = new MaskedFloat("Certain", 1f);
	public static final MaskedFloat defaultMin = new MaskedFloat("No Chance", 0f);
	private static Logger logger = Logger.getLogger(MaskedValue.class);
	private static boolean copyStarted, copyComplete;
	
	public static MaskedFloat[] readValues() throws MissingRequiredFileException
	{
		try{
			// for user permissions reasons, prop file shall be copied from install dir to users dir first time, load users version thereafter
			File propFile = new File(EnvUtils.getJCATSettingsHome() + EnvUtils.sep + PROP_FILE_NAME);
			if(!propFile.exists())
			{
				while(!copyComplete)
				{
					if(!copyStarted)
					{
						// This new way of loading resources worx with a common resource jar for Web Start
						InputStream orig = MaskedValue.class.getClassLoader().getResourceAsStream((""+(PROP_FILE_NAME)));
						FileUtils.copyFile(orig, propFile, new MaskedValue());
						copyStarted = true;
					}
					else
						Thread.sleep(10);
				}
			}
			
			return processFile(propFile);
			
		}catch(Exception exc)
		{
			logger.error("readValues - MaskedValue property file could not be loaded:  "+exc.getMessage());
			throw new MissingRequiredFileException("MaskedValue.properties could not be loaded properly. \n" +
				"Ensure the file exists and contains correct values.");
		}
	}
	
	private static MaskedFloat[] processFile(File propFile) throws IOException
	{
		BufferedReader reader = new BufferedReader(new FileReader(propFile));
		String thisLine = null;
		Vector<MaskedFloat> values = new Vector<MaskedFloat>();
		
		while((thisLine = reader.readLine()) != null)
		{
			StringTokenizer st = new StringTokenizer(thisLine, "=");
			//discard commented lines and ones w/o =
			if(!thisLine.startsWith("//") && thisLine.indexOf("=") > -1)
				values.add(new MaskedFloat(st.nextToken().trim(), Float.parseFloat(st.nextToken().trim())));
		}

		//if the list doens't contain a value for 1 and 0, add them (to support leak of 0, effect of 1)
		if(((MaskedFloat)values.get(0)).floatValue() != 1f)
			values.insertElementAt(defaultMax, 0);
		if(((MaskedFloat)values.lastElement()).floatValue() != 0f)
			values.add(defaultMin);
		
		//convert the list into a MaskedFloat array
		Object[] objs = values.toArray();
		MaskedFloat[] mValues = new MaskedFloat[objs.length];
		for(int x=0; x<objs.length; x++)
			mValues[x] = (MaskedFloat)objs[x];
		
		return mValues;
	}

	public void actionPerformed(ActionEvent e)
	{
		if(e.getActionCommand().equals(FileUtils.COPY))
		{
			copyComplete = true;
		}
	}
}
