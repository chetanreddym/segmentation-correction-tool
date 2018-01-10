package javax.activation;

import com.sun.activation.registries.MimeTypeFile;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.Vector;













































public class MimetypesFileTypeMap
  extends FileTypeMap
{
  private static MimeTypeFile defDB = null;
  
  private MimeTypeFile[] DB;
  private static final int PROG = 0;
  private static String defaultType = "application/octet-stream";
  
  private static boolean debug = false;
  
  static {
    try {
      debug = Boolean.getBoolean("javax.activation.debug");
    }
    catch (Throwable localThrowable) {}
  }
  



  public MimetypesFileTypeMap()
  {
    Vector localVector = new Vector(5);
    MimeTypeFile localMimeTypeFile = null;
    localVector.addElement(null);
    
    if (debug)
      System.out.println("MimetypesFileTypeMap: load HOME");
    try {
      ??? = System.getProperty("user.home");
      
      if (??? != null) {
        String str = ??? + File.separator + ".mime.types";
        localMimeTypeFile = loadFile(str);
        if (localMimeTypeFile != null) {
          localVector.addElement(localMimeTypeFile);
        }
      }
    } catch (SecurityException localSecurityException1) {}
    if (debug) {
      System.out.println("MimetypesFileTypeMap: load SYS");
    }
    try {
      ??? = 
        System.getProperty("java.home") + File.separator + "lib" + File.separator + "mime.types";
      localMimeTypeFile = loadFile((String)???);
      if (localMimeTypeFile != null) {
        localVector.addElement(localMimeTypeFile);
      }
    } catch (SecurityException localSecurityException2) {}
    if (debug) {
      System.out.println("MimetypesFileTypeMap: load JAR");
    }
    loadAllResources(localVector, "META-INF/mime.types");
    
    if (debug)
      System.out.println("MimetypesFileTypeMap: load DEF");
    synchronized (MimetypesFileTypeMap.class)
    {
      if (defDB == null) {
        defDB = loadResource("/META-INF/mimetypes.default");
      }
    }
    if (defDB != null) {
      localVector.addElement(defDB);
    }
    DB = new MimeTypeFile[localVector.size()];
    localVector.copyInto(DB);
  }
  
  private static final void pr(String paramString) {
    System.out.println(paramString);
  }
  


  private MimeTypeFile loadResource(String paramString)
  {
    InputStream localInputStream = null;
    try {
      localInputStream = 
        SecuritySupport.getInstance().getResourceAsStream(getClass(), paramString);
      if (localInputStream != null) {
        MimeTypeFile localMimeTypeFile2 = new MimeTypeFile(localInputStream);
        if (debug)
          pr(
            "MimetypesFileTypeMap: successfully loaded mime types file: " + paramString);
        MimeTypeFile localMimeTypeFile1 = localMimeTypeFile2;jsr 114;return localMimeTypeFile1;
      }
      if (debug) {
        pr(
          "MimetypesFileTypeMap: not loading mime types file: " + paramString);
      }
    } catch (IOException localIOException1) {
      if (debug) {
        pr("MimetypesFileTypeMap: " + localIOException1);
        localIOException1.printStackTrace();
      }
    } catch (SecurityException localSecurityException) {
      if (debug)
        pr("MimetypesFileTypeMap: " + localSecurityException);
    } finally {
      try {
        if (localInputStream != null)
          localInputStream.close();
      } catch (IOException localIOException2) {}
    }
    return null;
  }
  


  private void loadAllResources(Vector paramVector, String paramString)
  {
    int i = 0;
    try
    {
      ClassLoader localClassLoader = null;
      
      localClassLoader = SecuritySupport.getInstance().getContextClassLoader();
      if (localClassLoader == null)
        localClassLoader = getClass().getClassLoader();
      URL[] arrayOfURL; if (localClassLoader != null) {
        arrayOfURL = SecuritySupport.getInstance().getResources(localClassLoader, paramString);
      } else
        arrayOfURL = SecuritySupport.getInstance().getSystemResources(paramString);
      if (arrayOfURL != null) {
        if (debug)
          pr("MimetypesFileTypeMap: getResources");
        for (int j = 0; j < arrayOfURL.length; j++) {
          URL localURL = arrayOfURL[j];
          InputStream localInputStream = null;
          if (debug)
            pr("MimetypesFileTypeMap: URL " + localURL);
          try {
            localInputStream = SecuritySupport.getInstance().openStream(localURL);
            if (localInputStream != null) {
              paramVector.addElement(new MimeTypeFile(localInputStream));
              i = 1;
              if (debug) {
                pr(
                
                  "MimetypesFileTypeMap: successfully loaded mime types from URL: " + localURL);
              }
            } else if (debug) {
              pr(
                "MimetypesFileTypeMap: not loading mime types from URL: " + localURL);
            }
          } catch (IOException localIOException1) {
            if (debug)
              pr("MimetypesFileTypeMap: " + localIOException1);
          } catch (SecurityException localSecurityException) {
            if (debug)
              pr("MimetypesFileTypeMap: " + localSecurityException);
          } finally {
            try {
              if (localInputStream != null)
                localInputStream.close();
            } catch (IOException localIOException2) {}
          }
        }
      }
    } catch (Exception localException) {
      if (debug) {
        pr("MimetypesFileTypeMap: " + localException);
      }
    }
    
    if (i == 0) {
      if (debug)
        pr("MimetypesFileTypeMap: !anyLoaded");
      MimeTypeFile localMimeTypeFile = loadResource("/" + paramString);
      if (localMimeTypeFile != null) {
        paramVector.addElement(localMimeTypeFile);
      }
    }
  }
  

  private MimeTypeFile loadFile(String paramString)
  {
    MimeTypeFile localMimeTypeFile = null;
    try
    {
      localMimeTypeFile = new MimeTypeFile(paramString);
    }
    catch (IOException localIOException) {}
    
    return localMimeTypeFile;
  }
  




  public MimetypesFileTypeMap(String paramString)
    throws IOException
  {
    this();
    DB[0] = new MimeTypeFile(paramString);
  }
  





  public MimetypesFileTypeMap(InputStream paramInputStream)
  {
    this();
    try {
      DB[0] = new MimeTypeFile(paramInputStream);
    }
    catch (IOException localIOException) {}
  }
  






  public synchronized void addMimeTypes(String paramString)
  {
    if (DB[0] == null) {
      DB[0] = new MimeTypeFile();
    }
    DB[0].appendToRegistry(paramString);
  }
  







  public String getContentType(File paramFile)
  {
    return getContentType(paramFile.getName());
  }
  








  public synchronized String getContentType(String paramString)
  {
    int i = paramString.lastIndexOf(".");
    
    if (i < 0) {
      return defaultType;
    }
    String str1 = paramString.substring(i + 1);
    if (str1.length() == 0) {
      return defaultType;
    }
    for (int j = 0; j < DB.length; j++)
      if (DB[j] != null)
      {
        String str2 = DB[j].getMIMETypeString(str1);
        if (str2 != null)
          return str2;
      }
    return defaultType;
  }
}
