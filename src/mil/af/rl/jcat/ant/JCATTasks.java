package mil.af.rl.jcat.ant;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;


public class JCATTasks extends Task
{
	private final String classpathTag = "classpath";
	private String type = "";
	private File outputFile = null;
	private ArrayList nestedElements = new ArrayList();
	private File inputFile = null;
	private String cpPrepend = "";
	private String platformString = null;
//	private String commandString = null;

	
	@Override
	public void execute() throws BuildException
	{
		super.execute();
		
		if(type.equalsIgnoreCase("classpath-jsmooth")) // create classpath lines to be used by jsmooth from various sources
		{
			System.out.println("Creating classpath entries from inputs");
			try{
				PrintWriter out = null;
				if(outputFile != null)
					out = new PrintWriter(outputFile);
				else
					out = new PrintWriter(System.out);
				
				for(Object e : nestedElements)
				{
					if(e instanceof EclipseClasspath) // parse eclipse .classpath file extracting classpath lib entries
					{
						EclipseClasspath ec = (EclipseClasspath)e;
						try{
							String cpes = ec.createCPEntries();
							out.print(cpes);
						}catch(IOException exc){
							throw new BuildException(exc);
						}
					}
					else if(e instanceof DirectoryClasspath) // create a directory tree classpath entry recursing if requested
					{
						//maybe wana use Ant Path class here for automatic excludes and such
						DirectoryClasspath dc = (DirectoryClasspath)e;
						String cpes = dc.createCPEntries();
						out.write(cpes);
					}
				}
									
				out.flush();
				out.close();
				
			}catch(FileNotFoundException exc)	{
				throw new BuildException(exc);
			}
		}
		else if(type.equalsIgnoreCase("classpath-string")) // create single classpath line with separator between
		{
			System.out.println("Creating classpath line from inputs");
			try{
				PrintWriter out = null;
				if(outputFile != null)
					out = new PrintWriter(outputFile);
				else
					out = new PrintWriter(System.out);
				
				for(Object e : nestedElements)
				{
					if(e instanceof EclipseClasspath) // parse eclipse .classpath file extracting classpath lib entries
					{
						EclipseClasspath ec = (EclipseClasspath)e;
						try{
							String cpes = ec.createCPLine();
							out.print(cpes);
						}catch(IOException exc){
							throw new BuildException(exc);
						}
					}
					else if(e instanceof DirectoryClasspath) // create a directory tree classpath entry recursing if requested
					{
						//maybe wana use Ant Path class here for automatic excludes and such
						DirectoryClasspath dc = (DirectoryClasspath)e;
						String cpes = dc.createCPLine();
						out.write(cpes);
					}
				}
									
				out.flush();
				out.close();
				
			}catch(FileNotFoundException exc)	{
				throw new BuildException(exc);
			}
		}
		else if(type.equalsIgnoreCase("jsmooth-params")) // create classpath lines to be used by jsmooth from various sources
		{
			System.out.println("Creating jsmooth parameter entries from inputs");
			try{
				PrintWriter out = null;
				if(outputFile != null)
					out = new PrintWriter(outputFile);
				else
					out = new PrintWriter(System.out);
				
				for(Object e : nestedElements)
				{
					if(e instanceof Param) // parse additional jsmooth parameters
					{
						Param param = (Param)e;
						if(param.getNestedParams().size() < 1)
							out.println("<"+param.getName()+">"+param.getValue()+"</"+param.getName()+">");
						else
						{
							out.println("<"+param.getName()+">");
							for(Param np : param.getNestedParams())
								out.println("<"+np.getName()+">"+np.getValue()+"</"+np.getName()+">");
							out.println("</"+param.getName()+">");
						}
					}
				}
									
				out.flush();
				out.close();
				
			}catch(FileNotFoundException exc)	{
				throw new BuildException(exc);
			}
		}
		else if(type.equalsIgnoreCase("filter-libraries")) // filter library files that are not in the classpath file
		{
			if(inputFile != null)
			{
				System.out.println("Cleaning up / filtering libraries");
				try{
					LineNumberReader in = new LineNumberReader(new FileReader(inputFile));
					
					String fileData = "";
					String line = null;
					while((line = in.readLine()) != null)
						fileData = fileData.concat(line);
					in.close();
					
					for(Object e : nestedElements)
					{
						if(e instanceof DirectoryClasspath) // parse additional jsmooth parameters
						{
							DirectoryClasspath dc = (DirectoryClasspath)e;
							for(File f : dc.getDirectory().listFiles())
							{
								if(f.getName().toLowerCase().endsWith(".zip") || f.getName().toLowerCase().endsWith(".jar"))
								{
									if(!fileData.contains(f.getName()))
									{
										System.out.println("Deleting library: "+f.getName());
										f.delete();
									}
								}
							}
						}
					}
				}catch(IOException exc){
					throw new BuildException(exc);
				}
			}
		}
		else if(type.equalsIgnoreCase("autorun")) // generate an autorun file for given platform
		{
			
			try{
				PrintWriter out = null;
				if(outputFile != null)
					out = new PrintWriter(outputFile);
				else
					out = new PrintWriter(System.out);
				
				if(platformString != null)
				{
					System.out.println("Creating autorun file");
					
					String commandString = "";
					for(Object e : nestedElements)
					{
						if(e instanceof Param)
						{
							Param p = (Param)e;
							if(p.getName().equalsIgnoreCase("command"))
								commandString = p.getValue();
						}
					}
					
					if(platformString.equalsIgnoreCase("windows"))
					{
						out.println("[autorun]");
//						out.println("shellexecute="+commandString);
//						out.println("shell="+commandString);
						out.println("open="+commandString);
					}
					else if(platformString.equalsIgnoreCase("linux"))
					{
						out.println("#! /bin/sh");
						out.println("./"+commandString);
					}
					// mac is retarted, so too bad
				}
				
				out.flush();
				out.close();
				
			}catch(IOException exc){
				throw new BuildException(exc);
			}
		}
		else if(type.equalsIgnoreCase("eclipse-project")) // create classpath lines to be used by jsmooth from various sources
		{
			System.out.println("Creating eclipse project classpath and project files from inputs");
			
			try{
				PrintWriter cpOut = null, projOut = null;
				if(outputFile != null && outputFile.isDirectory())
				{
					cpOut = new PrintWriter(new File(outputFile.getAbsolutePath()+System.getProperty("file.separator")+".classpath"));
					projOut = new PrintWriter(new File(outputFile.getAbsolutePath()+System.getProperty("file.separator")+".project"));
				}
				else
				{
					cpOut = new PrintWriter(System.out);
					projOut = new PrintWriter(System.out);
				}
			
				ArrayList<String> cpLibs = new ArrayList<String>();
				String values = "";
				String projName = "Eclipse Project";
				
				for(Object e : nestedElements)
				{
					if(e instanceof Param) // combine classpath params
					{
						Param param = (Param)e;
						if(param.getName().equalsIgnoreCase("classpath"))
							values = values.concat(param.getValue());
						else if(param.getName().equalsIgnoreCase("name"))
							projName = param.getValue();
					}
				}
				
				// create classpath file
				cpOut.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				cpOut.println("<classpath>");
				cpOut.println("\t<classpathentry kind=\"src\" path=\"src\"/>");
				cpOut.println("\t<classpathentry kind=\"con\" path=\"org.eclipse.jdt.launching.JRE_CONTAINER\"/>");
				// parse classpath params
				StringTokenizer st = new StringTokenizer(values, ":");
				while(st.hasMoreTokens())
				{
					cpOut.println("\t<classpathentry kind=\"lib\" path=\""+st.nextToken().trim()+"\"/>");
				}
				cpOut.println("\t<classpathentry kind=\"output\" path=\"bin\"/>");
				cpOut.println("</classpath>");
				
				cpOut.flush();
				cpOut.close();
				
				// create project file
				projOut.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				projOut.println("<projectDescription>");
				projOut.println("\t<name>"+projName+"</name>");
				projOut.println("\t<comment></comment>");
				projOut.println("\t<projects>");
				projOut.println("\t</projects>");
				projOut.println("\t<buildSpec>");
				projOut.println("\t\t<buildCommand>");
				projOut.println("\t\t\t<name>org.eclipse.jdt.core.javabuilder</name>");
				projOut.println("\t\t\t<arguments>");
				projOut.println("\t\t\t</arguments>");
				projOut.println("\t\t</buildCommand>");
				projOut.println("\t</buildSpec>");
				projOut.println("\t<natures>");
				projOut.println("\t\t<nature>org.eclipse.jdt.core.javanature</nature>");
				projOut.println("\t</natures>");
				projOut.println("</projectDescription>");
				
				projOut.flush();
				projOut.close();
				
			}catch(IOException exc){
				throw new BuildException(exc);
			}
		}
		else if(type.equalsIgnoreCase("jnlp")) // generate an autorun file for given platform
		{
			
			try{
				PrintWriter out = null;
				if(outputFile != null)
					out = new PrintWriter(outputFile);
				else
					out = new PrintWriter(System.out);
				
				System.out.println("Creating JNLP file");
				
				String codebase = "", title = "", vendor = "", homepage = "", desc = "", javaVer = "", mainClass = "", icon = "";
				ArrayList<String> jarFiles = new ArrayList<String>();
				for(Object e : nestedElements)
				{
					if(e instanceof Param)
					{
						Param p = (Param)e;
						if(p.getName().equalsIgnoreCase("codebase"))
							codebase = p.getValue();
						else if(p.getName().equalsIgnoreCase("title"))
							title = p.getValue();
						else if(p.getName().equalsIgnoreCase("vendor"))
							vendor = p.getValue();
						else if(p.getName().equalsIgnoreCase("homepage"))
							homepage = p.getValue();
						else if(p.getName().equalsIgnoreCase("description"))
							desc = p.getValue();
						else if(p.getName().equalsIgnoreCase("java-version"))
							javaVer = p.getValue();
						else if(p.getName().equalsIgnoreCase("main-class"))
							mainClass = p.getValue();
						else if(p.getName().equalsIgnoreCase("jar"))
							jarFiles.add(p.getValue());
						else if(p.getName().equalsIgnoreCase("icon"))
							icon = p.getValue();
					}
				}
				
				
				out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				out.println("<jnlp spec=\"1.0+\" codebase=\""+codebase+"\">");
				out.println("\t<information>");
				out.println("\t\t<title>"+title+"</title>");
				out.println("\t\t<vendor>"+vendor+"</vendor>");
				out.println("\t\t<homepage href=\""+homepage+"\"/>");
				out.println("\t\t<description>"+desc+"</description>");
				if(!icon.equals(""))
					out.println("\t\t<icon href=\""+icon+"\"/>");
				out.println("\t</information>");
				out.println("\t<offline-allowed/>");
				out.println("\t<security>");
				out.println("\t\t<all-permissions/>");
				out.println("\t</security>");
				out.println("\t<resources>");
				out.println("\t\t<j2se version=\""+javaVer+"\"/>");
				for(String jarFile : jarFiles)
					out.println("\t\t<jar href=\""+jarFile+"\"/>");
				out.println("\t</resources>");
				out.println("\t<application-desc main-class=\""+mainClass+"\"/>");
				out.println("</jnlp>");
				
				out.flush();
				out.close();
				
			}catch(IOException exc){
				throw new BuildException(exc);
			}
		}
	}
	
	
	public void setType(String type)
	{
		this.type = type.trim();
	}
	
	public void setOut(File out)
	{
		outputFile = out;
	}

	public void setIn(File in)
	{
		inputFile = in;
	}
	
	public void setPrepend(String prepend)
	{
		cpPrepend = prepend;
	}
	
	public void setPlatform(String plat)
	{
		platformString = plat;
	}
	
//	public void setCommand(String cmd)
//	{
//		commandString = cmd;
//	}
	
	
	
	public EclipseClasspath createEclipseclasspath()
	{
		EclipseClasspath ec = new EclipseClasspath();
		nestedElements.add(ec);
		return ec;
	}
	
	public void addEclipseclasspath(EclipseClasspath ecp)
	{
		if(!nestedElements.contains(ecp))
			nestedElements.add(ecp);
	}
	
	public DirectoryClasspath createDirectoryclasspath()
	{
		DirectoryClasspath dc = new DirectoryClasspath();
		nestedElements.add(dc);
		return dc;
	}
	
	public void addDirectoryClasspath(DirectoryClasspath dcp)
	{
		if(!nestedElements.contains(dcp))
			nestedElements.add(dcp);
	}
	
	public Param createParam()
	{
		Param param = new Param();
		nestedElements.add(param);
		return param;
	}
	
	public void addParam(Param param)
	{
		if(!nestedElements.contains(param))
			nestedElements.add(param);
	}
	
	
	public class EclipseClasspath
	{
		private File cpFile = null;
		
		public void setFile(File cpFile)
		{
			this.cpFile = cpFile;
		}
		
		public File getFile()
		{
			return cpFile;
		}
		
		
		public String createCPEntries() throws IOException
		{
			StringBuffer entries = new StringBuffer();
			
			LineNumberReader in = new LineNumberReader(new FileReader(cpFile));
			
			String line = null;
			while((line = in.readLine()) != null)
			{
				if(!line.contains("kind=\"lib\""))
					continue;
				String path = line.substring(line.indexOf("path=\"")+6, line.lastIndexOf("\""));
				//might need to be relative paths here
				entries.append("<"+classpathTag+">"+cpPrepend+path+"</"+classpathTag+">\n");
			}
			
			return entries.toString();
		}
		
		public String createCPLine() throws IOException
		{
			StringBuffer entries = new StringBuffer();
			
			LineNumberReader in = new LineNumberReader(new FileReader(cpFile));
			
			String line = null;
			while((line = in.readLine()) != null)
			{
				if(!line.contains("kind=\"lib\""))
					continue;
				String path = line.substring(line.indexOf("path=\"")+6, line.lastIndexOf("\""));
				String separator = System.getProperty("path.separator");
				entries.append(""+cpPrepend+path+""+separator);
			}
			
			return entries.toString().substring(0, entries.toString().length());
		}
	}
	
	public class DirectoryClasspath
	{
		private File cpDir = null;
		private boolean includeSubDirs = false;
		
		public void setPath(File cpDir)
		{
			this.cpDir = cpDir;
		}
		
		public void setIncludesubs(boolean subs)
		{
			includeSubDirs = subs;
		}

		
		public File getDirectory()
		{
			return cpDir;
		}
		
		public boolean isIncludeSubDirs()
		{
			return includeSubDirs;
		}
		
		
		public String createCPEntries()
		{
			return createEntryForDirectory(cpDir, cpDir.getParentFile());
		}
		
		private String createEntryForDirectory(File dir, File relativeTo)
		{
			StringBuffer entries = new StringBuffer();
			
			//might need to be relative paths here
			entries.append("<"+classpathTag+">"+cpPrepend+getRelativePath(dir.getAbsolutePath(), relativeTo.getAbsolutePath())+"</"+classpathTag+">\n");
			
			if(isIncludeSubDirs())
				for(File f : dir.listFiles())
					if(f.isDirectory() && !f.getName().contains(".svn"))
						entries.append(createEntryForDirectory(f, relativeTo));
			
			return entries.toString();
		}
		
		public String createCPLine()
		{
			String line = createLineForDirectory(cpDir, cpDir.getParentFile());
			return line.substring(0, line.length()-1);
		}
		
		private String createLineForDirectory(File dir, File relativeTo)
		{
			StringBuffer entries = new StringBuffer();
			
			String separator = System.getProperty("path.separator");
			entries.append(""+cpPrepend+getRelativePath(dir.getAbsolutePath(), relativeTo.getAbsolutePath())+""+separator);
			
			if(isIncludeSubDirs())
				for(File f : dir.listFiles())
					if(f.isDirectory() && !f.getName().contains(".svn"))
						entries.append(createLineForDirectory(f, relativeTo));
			
			return entries.toString();
		}


		private String getRelativePath(String path, String relPath)
		{
			if(relPath == null || !path.contains(relPath))
				return path;
			else
				return path.substring(relPath.length()+1);  //+1 for the slash
		}

	}

	public class Param
	{
		String name = "";
		String value = "";
		ArrayList<Param> nestedParams = new ArrayList<Param>();
		
		public void setName(String name)
		{
			this.name = name;
		}
		
		public void setValue(String value)
		{
			this.value = value;
		}
		
		public String getName()
		{
			return name;
		}
		
		public String getValue()
		{
			return value;
		}
		
		
		public Param createParam()
		{
			Param param = new Param();
			nestedParams.add(param);
			return param;
		}
		
		public void addParam(Param param)
		{
			if(!nestedParams.contains(param))
				nestedParams.add(param);
		}
		
		public ArrayList<Param> getNestedParams()
		{
			return nestedParams;
		}
	}
}
