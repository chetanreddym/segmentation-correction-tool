package javax.activation;

import com.sun.activation.registries.MailcapFile;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;


















































































public class MailcapCommandMap
  extends CommandMap
{
  private static MailcapFile defDB = null;
  
  private MailcapFile[] DB;
  private static final int PROG = 0;
  private static boolean debug = false;
  
  static {
    try {
      debug = Boolean.getBoolean("javax.activation.debug");
    }
    catch (Throwable localThrowable) {}
  }
  





  public MailcapCommandMap()
  {
    Vector localVector = new Vector(5);
    MailcapFile localMailcapFile = null;
    localVector.addElement(null);
    
    if (debug)
      System.out.println("MailcapCommandMap: load HOME");
    try {
      ??? = System.getProperty("user.home");
      
      if (??? != null) {
        String str = ??? + File.separator + ".mailcap";
        localMailcapFile = loadFile(str);
        if (localMailcapFile != null) {
          localVector.addElement(localMailcapFile);
        }
      }
    } catch (SecurityException localSecurityException1) {}
    if (debug) {
      System.out.println("MailcapCommandMap: load SYS");
    }
    try {
      ??? = 
        System.getProperty("java.home") + File.separator + "lib" + File.separator + "mailcap";
      localMailcapFile = loadFile((String)???);
      if (localMailcapFile != null) {
        localVector.addElement(localMailcapFile);
      }
    } catch (SecurityException localSecurityException2) {}
    if (debug) {
      System.out.println("MailcapCommandMap: load JAR");
    }
    loadAllResources(localVector, "META-INF/mailcap");
    
    if (debug)
      System.out.println("MailcapCommandMap: load DEF");
    synchronized (MailcapCommandMap.class)
    {
      if (defDB == null) {
        defDB = loadResource("/META-INF/mailcap.default");
      }
    }
    if (defDB != null) {
      localVector.addElement(defDB);
    }
    DB = new MailcapFile[localVector.size()];
    localVector.copyInto(DB);
  }
  
  private static final void pr(String paramString) {
    System.out.println(paramString);
  }
  


  private MailcapFile loadResource(String paramString)
  {
    InputStream localInputStream = null;
    try {
      localInputStream = 
        SecuritySupport.getInstance().getResourceAsStream(getClass(), paramString);
      if (localInputStream != null) {
        MailcapFile localMailcapFile2 = new MailcapFile(localInputStream);
        if (debug)
          pr(
            "MailcapCommandMap: successfully loaded mailcap file: " + paramString);
        MailcapFile localMailcapFile1 = localMailcapFile2;jsr 109;return localMailcapFile1;
      }
      if (debug) {
        pr("MailcapCommandMap: not loading mailcap file: " + paramString);
      }
    } catch (IOException localIOException1) {
      if (debug)
        pr("MailcapCommandMap: " + localIOException1);
    } catch (SecurityException localSecurityException) {
      if (debug)
        pr("MailcapCommandMap: " + localSecurityException);
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
          pr("MailcapCommandMap: getResources");
        for (int j = 0; j < arrayOfURL.length; j++) {
          URL localURL = arrayOfURL[j];
          InputStream localInputStream = null;
          if (debug)
            pr("MailcapCommandMap: URL " + localURL);
          try {
            localInputStream = SecuritySupport.getInstance().openStream(localURL);
            if (localInputStream != null) {
              paramVector.addElement(new MailcapFile(localInputStream));
              i = 1;
              if (debug) {
                pr(
                
                  "MailcapCommandMap: successfully loaded mailcap file from URL: " + localURL);
              }
            } else if (debug) {
              pr(
                "MailcapCommandMap: not loading mailcap file from URL: " + localURL);
            }
          } catch (IOException localIOException1) {
            if (debug)
              pr("MailcapCommandMap: " + localIOException1);
          } catch (SecurityException localSecurityException) {
            if (debug)
              pr("MailcapCommandMap: " + localSecurityException);
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
        pr("MailcapCommandMap: " + localException);
      }
    }
    
    if (i == 0) {
      if (debug)
        pr("MailcapCommandMap: !anyLoaded");
      MailcapFile localMailcapFile = loadResource("/" + paramString);
      if (localMailcapFile != null) {
        paramVector.addElement(localMailcapFile);
      }
    }
  }
  

  private MailcapFile loadFile(String paramString)
  {
    MailcapFile localMailcapFile = null;
    try
    {
      localMailcapFile = new MailcapFile(paramString);
    }
    catch (IOException localIOException) {}
    
    return localMailcapFile;
  }
  




  public MailcapCommandMap(String paramString)
    throws IOException
  {
    this();
    
    if (debug)
      System.out.println("MailcapCommandMap: load PROG from " + paramString);
    if (DB[0] == null) {
      DB[0] = new MailcapFile(paramString);
    }
  }
  






  public MailcapCommandMap(InputStream paramInputStream)
  {
    this();
    
    if (debug)
      System.out.println("MailcapCommandMap: load PROG");
    if (DB[0] == null) {
      try {
        DB[0] = new MailcapFile(paramInputStream);
      }
      catch (IOException localIOException) {}
    }
  }
  













  public synchronized CommandInfo[] getPreferredCommands(String paramString)
  {
    Vector localVector = new Vector();
    
    for (int i = 0; i < DB.length; i++) {
      if (DB[i] != null)
      {
        localObject = DB[i].getMailcapList(paramString);
        if (localObject != null)
          appendPrefCmdsToVector((Hashtable)localObject, localVector);
      }
    }
    Object localObject = new CommandInfo[localVector.size()];
    localVector.copyInto((Object[])localObject);
    
    return localObject;
  }
  


  private void appendPrefCmdsToVector(Hashtable paramHashtable, Vector paramVector)
  {
    Enumeration localEnumeration = paramHashtable.keys();
    
    while (localEnumeration.hasMoreElements()) {
      String str1 = (String)localEnumeration.nextElement();
      if (!checkForVerb(paramVector, str1)) {
        Vector localVector = (Vector)paramHashtable.get(str1);
        String str2 = (String)localVector.firstElement();
        paramVector.addElement(new CommandInfo(str1, str2));
      }
    }
  }
  



  private boolean checkForVerb(Vector paramVector, String paramString)
  {
    Enumeration localEnumeration = paramVector.elements();
    while (localEnumeration.hasMoreElements()) {
      String str = 
        ((CommandInfo)localEnumeration.nextElement()).getCommandName();
      if (str.equals(paramString))
        return true;
    }
    return false;
  }
  






  public synchronized CommandInfo[] getAllCommands(String paramString)
  {
    Vector localVector = new Vector();
    
    for (int i = 0; i < DB.length; i++) {
      if (DB[i] != null)
      {
        localObject = DB[i].getMailcapList(paramString);
        if (localObject != null)
          appendCmdsToVector((Hashtable)localObject, localVector);
      }
    }
    Object localObject = new CommandInfo[localVector.size()];
    localVector.copyInto((Object[])localObject);
    
    return localObject;
  }
  


  private void appendCmdsToVector(Hashtable paramHashtable, Vector paramVector)
  {
    Enumeration localEnumeration1 = paramHashtable.keys();
    Enumeration localEnumeration2;
    for (; localEnumeration1.hasMoreElements(); 
        



        localEnumeration2.hasMoreElements())
    {
      String str1 = (String)localEnumeration1.nextElement();
      Vector localVector = (Vector)paramHashtable.get(str1);
      localEnumeration2 = localVector.elements();
      
      continue;
      String str2 = (String)localEnumeration2.nextElement();
      
      paramVector.insertElementAt(new CommandInfo(str1, str2), 0);
    }
  }
  








  public synchronized CommandInfo getCommand(String paramString1, String paramString2)
  {
    for (int i = 0; i < DB.length; i++)
      if (DB[i] != null)
      {
        Hashtable localHashtable = DB[i].getMailcapList(paramString1);
        if (localHashtable != null)
        {
          Vector localVector = (Vector)localHashtable.get(paramString2);
          if (localVector != null) {
            String str = (String)localVector.firstElement();
            
            if (str != null)
              return new CommandInfo(paramString2, str);
          }
        }
      }
    return null;
  }
  









  public synchronized void addMailcap(String paramString)
  {
    if (debug)
      System.out.println("MailcapCommandMap: add to PROG");
    if (DB[0] == null) {
      DB[0] = new MailcapFile();
    }
    DB[0].appendToMailcap(paramString);
  }
  






  public synchronized DataContentHandler createDataContentHandler(String paramString)
  {
    if (debug)
      System.out.println(
        "MailcapCommandMap: createDataContentHandler for " + paramString);
    for (int i = 0; i < DB.length; i++) {
      if (DB[i] != null)
      {
        if (debug)
          System.out.println("  search DB #" + i);
        Hashtable localHashtable = DB[i].getMailcapList(paramString);
        if (localHashtable != null) {
          Vector localVector = (Vector)localHashtable.get("content-handler");
          if (localVector != null) {
            if (debug)
              System.out.println("    got content-handler");
            try {
              if (debug)
                System.out.println("      class " + 
                  (String)localVector.firstElement());
              return (DataContentHandler)Class.forName(
                (String)localVector.firstElement()).newInstance();
            }
            catch (IllegalAccessException localIllegalAccessException) {}catch (ClassNotFoundException localClassNotFoundException) {}catch (InstantiationException localInstantiationException) {}
          }
        }
      }
    }
    
    return null;
  }
}
