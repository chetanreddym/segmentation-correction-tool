package com.ibm.icu.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.ListResourceBundle;
import java.util.MissingResourceException;
import java.util.ResourceBundle;






















public class ICUListResourceBundle
  extends ListResourceBundle
{
  private static final String ICUDATA = "ICUDATA";
  private static final String ICU_BUNDLE_NAME = "LocaleElements";
  private static final String ICU_PACKAGE_NAME = "com.ibm.icu.impl.data";
  private static final String ENCODING = "UTF-8";
  private Hashtable visited = new Hashtable();
  


  protected Object[][] contents;
  

  private Object[][] realContents;
  


  protected ICUListResourceBundle() {}
  


  protected Object[][] getContents()
  {
    if (realContents == null) {
      realContents = contents;
      for (int i = 0; i < contents.length; i++) {
        Object newValue = getRedirectedResource((String)contents[i][0], contents[i][1], -1);
        if (newValue != null) {
          if (realContents == contents) {
            realContents = ((Object[][])contents.clone());
          }
          realContents[i] = { contents[i][0], newValue };
        }
      }
    }
    
    return realContents;
  }
  




  private Object getRedirectedResource(String key, Object value, int index)
  {
    if ((value instanceof Object[][])) {
      Object[][] aValue = (Object[][])value;
      int i = 0;
      while (i < aValue.length) {
        int j = 0;
        while (j < aValue[i].length) {
          aValue[i][j] = getRedirectedResource((String)aValue[i][0], aValue[i][j], i);
          j++;
        }
        i++;
      }
    } else if ((value instanceof Object[])) {
      Object[] aValue = (Object[])value;
      int i = 0;
      while (i < aValue.length) {
        aValue[i] = getRedirectedResource(key, aValue[i], i);
        i++;
      }
    } else { if ((value instanceof Alias))
      {
        String cName = getClass().getName();
        visited.clear();
        visited.put(cName + key, "");
        return ((Alias)value).getResource(cName, key, index, visited); }
      if ((value instanceof RedirectedResource)) {
        return ((RedirectedResource)value).getResource(this);
      }
    }
    return value;
  }
  
  private static byte[] readToEOS(InputStream stream)
  {
    ArrayList vec = new ArrayList();
    int count = 0;
    int pos = 0;
    int MAXLENGTH = 32768;
    int length = 128;
    do {
      pos = 0;
      length = length >= 32768 ? 32768 : length * 2;
      byte[] buffer = new byte[length];
      try {
        do {
          int n = stream.read(buffer, pos, length - pos);
          if (n == -1) {
            break;
          }
          pos += n;
        } while (pos < length);
      }
      catch (IOException e) {}
      
      vec.add(buffer);
      count += pos;
    } while (pos == length);
    

    byte[] data = new byte[count];
    pos = 0;
    for (int i = 0; i < vec.size(); i++) {
      byte[] buf = (byte[])vec.get(i);
      int len = Math.min(buf.length, count - pos);
      System.arraycopy(buf, 0, data, pos, len);
      pos += len;
    }
    return data;
  }
  
  private static char[] readToEOS(InputStreamReader stream) {
    ArrayList vec = new ArrayList();
    int count = 0;
    int pos = 0;
    int MAXLENGTH = 32768;
    int length = 128;
    do {
      pos = 0;
      length = length >= 32768 ? 32768 : length * 2;
      char[] buffer = new char[length];
      try {
        do {
          int n = stream.read(buffer, pos, length - pos);
          if (n == -1) {
            break;
          }
          pos += n;
        } while (pos < length);
      }
      catch (IOException e) {}
      
      vec.add(buffer);
      count += pos;
    } while (pos == length);
    
    char[] data = new char[count];
    pos = 0;
    for (int i = 0; i < vec.size(); i++) {
      char[] buf = (char[])vec.get(i);
      int len = Math.min(buf.length, count - pos);
      System.arraycopy(buf, 0, data, pos, len);
      pos += len;
    }
    return data;
  }
  















  public static class CompressedBinary
    implements ICUListResourceBundle.RedirectedResource
  {
    private byte[] expanded = null;
    private String compressed = null;
    
    public CompressedBinary(String str) { compressed = str; }
    
    public Object getResource(Object obj) {
      if (compressed == null) {
        return null;
      }
      
      if (expanded == null) {
        expanded = Utility.RLEStringToByteArray(compressed);
      }
      return expanded;
    }
  }
  
  private static abstract interface RedirectedResource {
    public abstract Object getResource(Object paramObject);
  }
  
  public static class ResourceBinary implements ICUListResourceBundle.RedirectedResource {
    private byte[] expanded = null;
    private String resName = null;
    
    public ResourceBinary(String name) { resName = name; }
    
    public Object getResource(Object obj) {
      if (expanded == null) {
        InputStream stream = obj.getClass().getResourceAsStream(resName);
        if (stream != null)
        {
          expanded = ICUListResourceBundle.readToEOS(stream);
          return expanded;
        }
      }
      
      return "";
    }
  }
  
  public static class ResourceString implements ICUListResourceBundle.RedirectedResource {
    private char[] expanded = null;
    private String resName = null;
    
    public ResourceString(String name) { resName = name; }
    
    public Object getResource(Object obj) {
      if (expanded == null)
      {
        InputStream stream = obj.getClass().getResourceAsStream(resName);
        if (stream != null)
        {
          try
          {
            InputStreamReader reader = new InputStreamReader(stream, "UTF-8");
            expanded = ICUListResourceBundle.readToEOS(reader);
          } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException("Could open converter for encoding: UTF-8");
          }
          return new String(expanded);
        }
      }
      
      return "";
    }
  }
  

  public static class Alias {
    public Alias(String path) { pathToResource = path; }
    
    private final char RES_PATH_SEP_CHAR = '/';
    private String pathToResource;
    
    private Object getResource(String className, String parentKey, int index, Hashtable visited)
    {
      String packageName = null;String bundleName = null;String locale = null;String keyPath = null;
      
      if (pathToResource.indexOf('/') == 0) {
        int i = pathToResource.indexOf('/', 1);
        int j = pathToResource.indexOf('/', i + 1);
        bundleName = pathToResource.substring(1, i);
        locale = pathToResource.substring(i + 1);
        if (j != -1) {
          locale = pathToResource.substring(i + 1, j);
          keyPath = pathToResource.substring(j + 1, pathToResource.length());
        }
        
        if (bundleName.equals("ICUDATA")) {
          bundleName = "LocaleElements";
          packageName = "com.ibm.icu.impl.data";
        }
      }
      else
      {
        int i = pathToResource.indexOf('/');
        


        int j = className.lastIndexOf(".");
        packageName = className.substring(0, j);
        bundleName = className.substring(j + 1, className.indexOf("_"));
        keyPath = pathToResource.substring(i + 1);
        
        if (i != -1) {
          locale = pathToResource.substring(0, i);
        } else {
          locale = keyPath;
          keyPath = parentKey;
          className = packageName + "." + bundleName + "_" + locale;
        }
      }
      
      ResourceBundle bundle = null;
      

      bundle = ICULocaleData.getResourceBundle(packageName, bundleName, locale);
      
      return findResource(bundle, className, parentKey, index, keyPath, visited);
    }
    


    private boolean isIndex(String s)
    {
      if (s.length() == 1) {
        char c = s.charAt(0);
        return Character.isDigit(c);
      }
      return false;
    }
    
    private int getIndex(String s) { if (s.length() == 1) {
        char c = s.charAt(0);
        if (Character.isDigit(c)) {
          return Integer.valueOf(s).intValue();
        }
      }
      return -1;
    }
    
    private Object findResource(Object[][] contents, String key) { for (int i = 0; i < contents.length; i++)
      {
        String tempKey = (String)contents[i][0];
        Object value = contents[i][1];
        if ((tempKey == null) || (value == null)) {
          throw new NullPointerException();
        }
        if (tempKey.equals(key)) {
          return value;
        }
      }
      return null;
    }
    
    private Object findResource(Object o, String[] keys, int start, int index) { Object obj = o;
      if ((start < keys.length) && (keys[start] != null)) {
        if ((obj instanceof Object[][])) {
          obj = findResource((Object[][])obj, keys[start]);
        } else if (((obj instanceof Object[])) && (isIndex(keys[start]))) {
          obj = ((Object[])obj)[getIndex(keys[start])];
        }
        if ((start + 1 < keys.length) && (keys[(start + 1)] != null)) {
          obj = findResource(obj, keys, start + 1, index);
        }
        
      }
      else if (index >= 0) {
        if ((obj instanceof Object[][])) {
          obj = findResource((Object[][])obj, Integer.toString(index));
        } else if ((obj instanceof Object[])) {
          obj = ((Object[])obj)[index];
        }
      }
      
      return obj;
    }
    
    private Object findResource(ResourceBundle bundle, String className, String requestedKey, int index, String aliasKey, Hashtable visited) {
      if ((aliasKey != null) && (visited.get(className + aliasKey) != null)) {
        throw new MissingResourceException("Circular Aliases in bundle.", bundle.getClass().getName(), requestedKey);
      }
      if (aliasKey == null)
      {

        aliasKey = requestedKey;
      }
      
      visited.put(className + requestedKey, "");
      
      String[] keys = split(aliasKey, '/');
      Object o = null;
      if (keys.length > 0) {
        o = bundle.getObject(keys[0]);
        o = findResource(o, keys, 1, index);
      }
      o = resolveAliases(o, className, aliasKey, visited);
      return o;
    }
    
    private Object resolveAliases(Object o, String className, String key, Hashtable visited) { if ((o instanceof Object[][])) {
        o = resolveAliases((Object[][])o, className, key, visited);
      } else if ((o instanceof Object[])) {
        o = resolveAliases((Object[])o, className, key, visited);
      } else if ((o instanceof Alias)) {
        return ((Alias)o).getResource(className, key, -1, visited);
      }
      return o;
    }
    
    private Object resolveAliases(Object[][] o, String className, String key, Hashtable visited) { int i = 0;
      while (i < o.length) {
        o[i][1] = resolveAliases(o[i][1], className, key, visited);
        i++;
      }
      return o;
    }
    
    private Object resolveAliases(Object[] o, String className, String key, Hashtable visited) { int i = 0;
      while (i < o.length) {
        o[i] = resolveAliases(o[i], className, key, visited);
        i++;
      }
      return o;
    }
    
    private String[] split(String source, char delimiter)
    {
      char[] src = source.toCharArray();
      int index = 0;
      int numdelimit = 0;
      
      for (int i = 0; i < source.length(); i++) {
        if (src[i] == delimiter) {
          numdelimit++;
        }
      }
      String[] values = null;
      values = new String[numdelimit + 2];
      
      int old = 0;
      for (int j = 0; j < src.length; j++) {
        if (src[j] == delimiter) {
          values[(index++)] = new String(src, old, j - old);
          old = j + 1;
        }
      }
      if (old < src.length)
        values[(index++)] = new String(src, old, src.length - old);
      return values;
    }
  }
}
