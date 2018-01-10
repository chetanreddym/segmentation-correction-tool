package com.sun.media.jai.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;












public class PropertyUtil
{
  private static Hashtable bundles = new Hashtable();
  private static String propertiesDir = "javax/media/jai";
  
  public PropertyUtil() {}
  
  public static InputStream getFileFromClasspath(String path) throws IOException, FileNotFoundException
  {
    String pathFinal = path;
    String sep = File.separator;
    String tmpHome = null;
    try {
      tmpHome = System.getProperty("java.home");
    } catch (Exception e) {
      tmpHome = null;
    }
    String home = tmpHome;
    String urlHeader = home + sep + "lib" + sep;
    

    if (home != null) {
      String libExtPath = urlHeader + "ext" + sep + path;
      File libExtFile = new File(libExtPath);
      try {
        if (libExtFile.exists()) {
          InputStream is = new FileInputStream(libExtFile);
          if (is != null) {
            return is;
          }
        }
      }
      catch (AccessControlException e) {}
    }
    







    InputStream is = PropertyUtil.class.getResourceAsStream("/" + path);
    if (is != null) {
      return is;
    }
    






    PrivilegedAction p = new PrivilegedAction() { private final String val$home;
      
      public Object run() { String localHome = null;
        String localUrlHeader = null;
        if (val$home != null) {
          localHome = val$home;
          localUrlHeader = val$urlHeader;
        } else {
          localHome = System.getProperty("java.home");
          localUrlHeader = localHome + val$sep + "lib" + val$sep;
        }
        String[] filenames = { localUrlHeader + "ext" + val$sep + "jai_core.jar", localUrlHeader + "ext" + val$sep + "jai_codec.jar", localUrlHeader + "jai_core.jar", localUrlHeader + "jai_codec.jar" };
        





        for (int i = 0; i < filenames.length; i++) {
          try {
            InputStream tmpIS = PropertyUtil.getFileFromJar(filenames[i], val$pathFinal);
            
            if (tmpIS != null) {
              return tmpIS;
            }
          }
          catch (Exception e) {}
        }
        
        return null;
      }
      
    };
    return (InputStream)AccessController.doPrivileged(p);
  }
  
  private static InputStream getFileFromJar(String jarFilename, String path)
    throws Exception
  {
    JarFile f = null;
    try {
      f = new JarFile(jarFilename);
    }
    catch (Exception e) {}
    JarEntry ent = f.getJarEntry(path);
    if (ent != null) {
      return f.getInputStream(ent);
    }
    return null;
  }
  
  private static ResourceBundle getBundle(String packageName)
  {
    ResourceBundle bundle = null;
    
    InputStream in = null;
    try {
      in = getFileFromClasspath(propertiesDir + "/" + packageName + ".properties");
      
      if (in != null) {
        bundle = new PropertyResourceBundle(in);
        bundles.put(packageName, bundle);
        return bundle;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    return null;
  }
  
  public static String getString(String packageName, String key) {
    ResourceBundle b = (ResourceBundle)bundles.get(packageName);
    if (b == null) {
      b = getBundle(packageName);
    }
    return b.getString(key);
  }
  


  private final String val$urlHeader;
  

  private final String val$sep;
  
  private final String val$pathFinal;
  
  public static String[] getPropertyNames(String[] propertyNames, String prefix)
  {
    if (propertyNames == null)
      return null;
    if (prefix == null) {
      throw new IllegalArgumentException(JaiI18N.getString("PropertyUtil0"));
    }
    
    prefix = prefix.toLowerCase();
    
    Vector names = new Vector();
    for (int i = 0; i < propertyNames.length; i++) {
      if (propertyNames[i].toLowerCase().startsWith(prefix)) {
        names.addElement(propertyNames[i]);
      }
    }
    
    if (names.size() == 0) {
      return null;
    }
    

    String[] prefixNames = new String[names.size()];
    int count = 0;
    for (Iterator it = names.iterator(); it.hasNext();) {
      prefixNames[(count++)] = ((String)it.next());
    }
    
    return prefixNames;
  }
}
