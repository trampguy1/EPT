
package mil.af.rl.jcat.util;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import org.apache.log4j.Logger;

import java.io.*;


public class DNDFileHandler extends TransferHandler
{
	private Logger logger = Logger.getLogger(DNDFileHandler.class);
	private DataFlavor fileFlavor, stringFlavor;
	private DNDCallback callBack;

	
	public DNDFileHandler(DNDCallback callBack)
	{
		fileFlavor = DataFlavor.javaFileListFlavor;
		stringFlavor = DataFlavor.stringFlavor;
		this.callBack = callBack;
	}

	
	@Override
	public int getSourceActions(JComponent c)
	{
		return TransferHandler.COPY_OR_MOVE;
	}

	public boolean importData(JComponent c, Transferable t)
	{
		if(!canImport(c, t.getTransferDataFlavors()))
		{
			logger.error("importData - Cannot import data dropped");
			return false;
		}

		try
		{
			if(hasFileFlavor(t.getTransferDataFlavors()))
			{
				String str = null;
				java.util.List files = (java.util.List) t.getTransferData(fileFlavor);
				for(int i = 0; i < files.size(); i++)
				{
					File theFile = (File)files.get(i);
					if(theFile.isFile())
					{
						logger.info("importData - opening dropped file:  "+theFile);
						callBack.openDocument(theFile);
					}
				}
				return true;
			}
			else if(hasStringFlavor(t.getTransferDataFlavors()))
			{
				File theFile = new File((String)t.getTransferData(stringFlavor));
				if(theFile.isFile())
				{
					callBack.openDocument(theFile);
					return true;
				}
				else
					return false;
			}
			else
				logger.error("importData - DNDManager has no flavor!");
		}catch(UnsupportedFlavorException ufe)
		{
			logger.error("importData - unsupported data flavor:  "+ufe.getMessage());
		}
		catch(IOException ieo)
		{
			logger.error("importData - I/O error handling dropped document:  "+ieo.getMessage());
		}

		return false;
	}

	public boolean canImport(JComponent c, DataFlavor[] flavors)
	{
		if(hasFileFlavor(flavors))
		{
			return true;
		}
		if(hasStringFlavor(flavors))
		{
			return true;
		}
		return false;
	}

	private boolean hasFileFlavor(DataFlavor[] flavors)
	{
		for(int i = 0; i < flavors.length; i++)
		{
			if(fileFlavor.equals(flavors[i]))
			{
				return true;
			}
		}
		return false;
	}

	private boolean hasStringFlavor(DataFlavor[] flavors)
	{
		for(int i = 0; i < flavors.length; i++)
		{
			if(stringFlavor.equals(flavors[i]))
			{
				return true;
			}
		}
		return false;
	}

	
	
	public interface DNDCallback
	{
		
		public void openDocument(File theFile);
		
	}
}
