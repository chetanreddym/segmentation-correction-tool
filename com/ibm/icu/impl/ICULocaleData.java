package com.ibm.icu.impl;

import java.io.PrintStream;
import java.lang.ref.SoftReference;
import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;








public class ICULocaleData
{
  private static Locale[] localeList;
  private static final String PACKAGE1 = "com.ibm.icu.impl.data";
  private static final String[] packageNames = { "com.ibm.icu.impl.data" };
  private static boolean debug = ICUDebug.enabled("localedata");
  







  private static SoftReference lcacheref;
  






  public static final String LOCALE_ELEMENTS = "LocaleElements";
  







  public ICULocaleData() {}
  







  public static Locale[] getAvailableLocales(String bundlePrefix)
  {
    return (Locale[])getAvailEntry(bundlePrefix).getLocaleList().clone();
  }
  


  public static Locale[] getAvailableLocales()
  {
    return getAvailableLocales("LocaleElements");
  }
  



  public static Set getAvailableLocaleNameSet(String bundlePrefix)
  {
    return getAvailEntry(bundlePrefix).getLocaleNameSet();
  }
  



  public static Set getAvailableLocaleNameSet()
  {
    return getAvailableLocaleNameSet("LocaleElements");
  }
  

  private static final class AvailEntry
  {
    private String prefix;
    private Locale[] locales;
    private Set nameSet;
    
    AvailEntry(String prefix)
    {
      this.prefix = prefix;
    }
    
    Locale[] getLocaleList() {
      if (locales == null) {
        locales = ICULocaleData.createLocaleList(prefix);
      }
      return locales;
    }
    
    Set getLocaleNameSet() {
      if (nameSet == null) {
        nameSet = ICULocaleData.createLocaleNameSet(prefix);
      }
      return nameSet;
    }
  }
  




  private static AvailEntry getAvailEntry(String key)
  {
    AvailEntry ae = null;
    Map lcache = null;
    if (lcacheref != null) {
      lcache = (Map)lcacheref.get();
      if (lcache != null) {
        ae = (AvailEntry)lcache.get(key);
      }
    }
    
    if (ae == null) {
      ae = new AvailEntry(key);
      if (lcache == null) {
        lcache = new HashMap();
        lcache.put(key, ae);
        lcacheref = new SoftReference(lcache);
      } else {
        lcache.put(key, ae);
      }
    }
    
    return ae;
  }
  








  public static ResourceBundle getLocaleElements(Locale locale)
  {
    return getResourceBundle("LocaleElements", locale);
  }
  


  public static ResourceBundle getLocaleElements(String localeName)
  {
    return getResourceBundle("LocaleElements", localeName);
  }
  




  private static ResourceBundle instantiateBundle(String name, Locale l)
  {
    ResourceBundle rb = ResourceBundle.getBundle(name, l);
    return rb;
  }
  


  public static ResourceBundle getResourceBundle(String bundleName, String localeName)
  {
    Locale locale = LocaleUtility.getLocaleFromName(localeName);
    return getResourceBundle(bundleName, locale);
  }
  


  public static ResourceBundle getResourceBundle(String bundleName, Locale locale)
  {
    if (locale == null) {
      locale = Locale.getDefault();
    }
    for (int i = 0; i < packageNames.length;) {
      try {
        String path = packageNames[i] + "." + bundleName;
        if (debug) System.out.println("calling instantiateBundle: " + path + "_" + locale);
        return instantiateBundle(path, locale);
      }
      catch (MissingResourceException e)
      {
        if (debug) System.out.println(bundleName + "_" + locale + " not found in " + packageNames[i]);
        throw e;
      }
    }
    
    return null;
  }
  


  public static ResourceBundle getResourceBundle(String[] packages, String bundleName, String localeName)
  {
    Locale locale = LocaleUtility.getLocaleFromName(localeName);
    if (locale == null) {
      locale = Locale.getDefault();
    }
    for (int i = 0; i < packages.length;) {
      try {
        String path = packages[i] + "." + bundleName;
        if (debug) System.out.println("calling instantiateBundle: " + path + "_" + locale);
        return instantiateBundle(path, locale);
      }
      catch (MissingResourceException e)
      {
        if (debug) System.out.println(bundleName + "_" + locale + " not found in " + packages[i]);
        throw e;
      }
    }
    return null;
  }
  


  public static ResourceBundle getResourceBundle(String packageName, String bundleName, String localeName)
  {
    Locale locale = LocaleUtility.getLocaleFromName(localeName);
    if (locale == null) {
      locale = Locale.getDefault();
    }
    try
    {
      String path = packageName + "." + bundleName;
      if (debug) System.out.println("calling instantiateBundle: " + path + "_" + locale);
      return instantiateBundle(path, locale);
    }
    catch (MissingResourceException e)
    {
      if (debug) System.out.println(bundleName + "_" + locale + " not found in " + packageName);
      throw e;
    }
  }
  


  public static ResourceBundle loadResourceBundle(String bundleName, Locale locale)
  {
    if (locale == null) {
      locale = Locale.getDefault();
    }
    return loadResourceBundle(bundleName, locale.toString());
  }
  



  public static ResourceBundle loadResourceBundle(String bundleName, String localeName)
  {
    if ((localeName != null) && (localeName.length() > 0)) {
      bundleName = bundleName + "_" + localeName;
    }
    for (int i = 0; i < packageNames.length; i++) {
      String name = packageNames[i] + "." + bundleName;
      try {
        if (name.indexOf("_zh_") == -1) {
          Class rbclass = Class.forName(name);
          return (ResourceBundle)rbclass.newInstance();
        }
      }
      catch (ClassNotFoundException e)
      {
        if (debug) {
          System.out.println(bundleName + " not found in " + packageNames[i]);
        }
      }
      catch (Exception e)
      {
        if (debug) {
          System.out.println(e.getMessage());
        }
      }
    }
    if (debug) {
      System.out.println(bundleName + " not found.");
    }
    
    return null;
  }
  
  private static Set createLocaleNameSet(String bundleName)
  {
    try
    {
      ResourceBundle index = getResourceBundle(bundleName, "index");
      Object[][] localeStrings = (Object[][])index.getObject("InstalledLocales");
      String[] localeNames = new String[localeStrings.length];
      




      for (int i = 0; i < localeNames.length; i++) {
        localeNames[i] = LocaleUtility.getLocaleFromName((String)localeStrings[i][0]).toString();
      }
      
      HashSet set = new HashSet();
      set.addAll(Arrays.asList(localeNames));
      return Collections.unmodifiableSet(set);
    }
    catch (MissingResourceException e) {
      System.out.println("couldn't find index for bundleName: " + bundleName);
      Thread.dumpStack();
    }
    return Collections.EMPTY_SET;
  }
  
  private static Locale[] createLocaleList(String bundleName) {
    try {
      ResourceBundle index = getResourceBundle(bundleName, "index");
      Object[][] localeStrings = (Object[][])index.getObject("InstalledLocales");
      Locale[] locales = new Locale[localeStrings.length];
      for (int i = 0; i < localeStrings.length; i++) {
        locales[i] = LocaleUtility.getLocaleFromName((String)localeStrings[i][0]);
      }
      return locales;
    }
    catch (MissingResourceException e) {
      System.out.println("couldn't find index for bundleName: " + bundleName);
      Thread.dumpStack();
    }
    return new Locale[0];
  }
}
