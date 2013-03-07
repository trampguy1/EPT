package mil.af.rl.jcat.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;



public class FileUtils implements Runnable
{
	private Logger logger = Logger.getLogger(FileUtils.class);
	public static final int OPERATION_COMPLETED = 1111;
	public static final int OPERATION_FAILED = 2222;
	private static final int OP_COPY = 1;
	public static final String COPY = "COPY";
	private static int operation = -1;
	private static Object[] inputs = null;
	private static ActionListener listen;
	private static Boolean completeFlag = null;
	private String actionName;
	

	private void finished(boolean success)
	{
//		progDialog.dispose();
		if(listen != null)
		{
			if(success)
				listen.actionPerformed(new ActionEvent(this, OPERATION_COMPLETED, actionName));
			else
				listen.actionPerformed(new ActionEvent(this, OPERATION_FAILED, actionName));
		}
	}
	
	public static void copyFile(File from, File to, ActionListener lis) throws FileNotFoundException
	{
		operation = OP_COPY;
		inputs = new Object[]{ new FileInputStream(from), to };
		listen = lis;
		new Thread(new FileUtils()).start();
	}
	
	public static void copyFile(InputStream from, File to, ActionListener lis)
	{
		operation = OP_COPY;
		inputs = new Object[]{from, to };
		listen = lis;
		new Thread(new FileUtils()).start();
	}
	

	public void run()
	{
		if(operation == OP_COPY)
		{
			actionName = COPY;
			InputStream from = (InputStream)inputs[0];
			File to = (File)inputs[1];
			InputStreamReader reader = null;
			FileWriter writer = null;
			
			try{
				to.createNewFile();
				
				reader = new InputStreamReader(from);
//				reader = new FileReader(from);
				writer = new FileWriter(to);
				char[] readBytes = new char[1024];
				int bytesRead = 0;
				int totalRead = 0;
				
				while((bytesRead = reader.read(readBytes)) > 0)
				{
					writer.write(readBytes, 0, bytesRead);
//					progress.setValue(totalRead += bytesRead);
				}
				
				finished(true);
			}catch(IOException exc){
				logger.error("run - IO Error copying file:  "+exc.getMessage());
				finished(false);
			}
			finally
			{
				try{
					if(reader != null)
						reader.close();
					if(writer != null)
					{
						writer.flush();
						writer.close();
					}
				}catch(IOException exc){   }
			}
		}
		
		operation = -1;
	}
	
}
