/*
 * Created on Jun 6, 2005
 *
 */
package mil.af.rl.jcat.util;

import java.io.File;

/**
 * @author dygertm
 *
 */
public class CatFileFilter extends javax.swing.filechooser.FileFilter implements java.io.FileFilter
{
	private String[] desiredExtensions;
	private boolean showDirs = true;
	private String description;

	public CatFileFilter()
	{
		this(new String[]{"jcat"}, "JCAT Files", true);
	}

	public CatFileFilter(String ext, String desc)
	{
		this(new String[]{ext}, desc, true);
	}
	
	public CatFileFilter(String[] ext, String desc)
	{
		this(ext, desc, true);
	}
	
	public CatFileFilter(String ext, boolean dir)
	{
		this(new String[]{ext}, "", dir);
	}

	public CatFileFilter(String ext, String desc, boolean dir)
	{
		this(new String[]{ext}, desc, dir);
	}
	
	public CatFileFilter(String[] ext, String desc, boolean dir)
	{
		desiredExtensions = ext;
		description = desc;
		showDirs = dir;
	}

	public boolean accept(File input)
	{
		String filename = input.getName();
		try{
			String extension = filename.substring(filename.lastIndexOf(".")+1, filename.length());
			for(String validExt : desiredExtensions)
			{
				if(extension.toLowerCase().equals(validExt.toLowerCase()) || (input.isDirectory() && showDirs))
					return true;
			}
		}catch(StringIndexOutOfBoundsException exc){
			return false;
		}

		return false;
	}

	public String getDescription()
	{
		return description;
	}

	public String getExtension()
	{
		String extensionList = "";
		
		for(String validExt : desiredExtensions)
			extensionList = extensionList.concat(validExt + " ");
		
		return extensionList.trim();
	}
}
