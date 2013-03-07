
package mil.af.rl.jcat.integration.soa.server;


import java.io.*;
import java.net.*;
import java.util.*;
/**
 *
 * @author Edward Verenich
 */
public class Resource implements Serializable {

  private String name;
  private File file;
  private URL url;

  public Resource(String name) throws IOException {
    this.name = name;
    SecurityException exception = null;

    try {
      // Search using the CLASSPATH. If found, "file" is set and the call
      // returns true.  A SecurityException might bubble up.
      if (tryClasspath(name)) {
        return;
      }
    }
    catch (SecurityException e) {
      exception = e;  // Save for later.
    }

    try {
      // Search using the classloader getResource(  ). If found as a file,
      // "file" is set; if found as a URL, "url" is set.
      if (tryLoader(name)) {
        return;
      }
    }
    catch (SecurityException e) {
      exception = e;  // Save for later.
    }

    // If you get here, something went wrong. Report the exception.
    String msg = "";
    if (exception != null) {
      msg = ": " + exception;
    }

    throw new IOException("Resource '" + name + "' could not be found in " +
      "the CLASSPATH (" + System.getProperty("java.class.path") +
      "), nor could it be located by the classloader responsible for the " +
      "web application (WEB-INF/classes)" + msg);
  }

  /**
   * Returns the resource name, as passed to the constructor
   */
  public String getName(  ) {
    return name;
  }

  /**
   * Returns an input stream to read the resource contents
   */
  public InputStream getInputStream(  ) throws IOException {
    if (file != null) {
      return new BufferedInputStream(new FileInputStream(file));
    }
    else if (url != null) {
      return new BufferedInputStream(url.openStream(  ));
    }
    return null;
  }

  /**
   * Returns when the resource was last modified. If the resource 
   * was found using a URL, this method will work only if the URL 
   * connection supports last modified information. If there's no 
   * support, Long.MAX_VALUE is returned. Perhaps this should return 
   * -1, but you should return MAX_VALUE on the assumption that if
   * you can't determine the time, it's maximally new.
   */
  public long lastModified(  ) {
    if (file != null) {
      return file.lastModified(  );
    }
    else if (url != null) {
      try {
        return url.openConnection(  ).getLastModified(  );  // Hail Mary
      }
      catch (IOException e) { return Long.MAX_VALUE; }
    }
    return 0;  // can't happen
  }
   
  /**
   * Returns the directory containing the resource, or null if the 
   * resource isn't directly available on the filesystem. 
   * This value can be used to locate the configuration file on disk,
   * or to write files in the same directory.
   */
  public String getDirectory(  ) {
    if (file != null) {
      return file.getParent(  );
    }
    else if (url != null) {
      return null;
    }
    return null;
  }
 
  // Returns true if found
  private boolean tryClasspath(String filename) {
    String classpath = System.getProperty("java.class.path");
    String[  ] paths = split(classpath, File.pathSeparator);
    file = searchDirectories(paths, filename);
    return (file != null);
  }

  private static File searchDirectories(String[  ] paths, String filename) {
    SecurityException exception = null;
    for (int i = 0; i < paths.length; i++) {
      try {
        File file = new File(paths[i], filename);
        if (file.exists(  ) && !file.isDirectory(  )) {
          return file;
        }
      }
      catch (SecurityException e) {
        // Security exceptions can usually be ignored, but if all attempts
        // to find the file fail, report the (last) security exception.
        exception = e;
      }
    }
    // Couldn't find any match
    if (exception != null) {
      throw exception;
    }
    else {
      return null;
    }
  }

  // Splits a String into pieces according to a delimiter.
  // Uses JDK 1.1 classes for backward compatibility.
  // JDK 1.4 actually has a split(  ) method now.
  private static String[  ] split(String str, String delim) {
    // Use a Vector to hold the split strings.
    Vector v = new Vector(  );

    // Use a StringTokenizer to do the splitting.
    StringTokenizer tokenizer = new StringTokenizer(str, delim);
    while (tokenizer.hasMoreTokens(  )) {
      v.addElement(tokenizer.nextToken(  ));
    }

    String[  ] ret = new String[v.size(  )];
    v.copyInto(ret);
    return ret;
  }

  // Returns true if found
  private boolean tryLoader(String name) {
    name = "/" + name;
    URL res = Resource.class.getResource(name);
    if (res == null) {
      return false;
    }

    // Try converting from a URL to a File.
    File resFile = urlToFile(res);
    if (resFile != null) {
      file = resFile;
    }
    else {
      url = res;
    }
    
    return true;
  }

  private static File urlToFile(URL res) {
    String externalForm = res.toExternalForm(  );
    if (externalForm.startsWith("file:")) {
      return new File(externalForm.substring(5));
    }
    return null;
  }

  public String toString(  ) {
    return "[Resource: File: " + file + " URL: " + url + "]";
  }
}
