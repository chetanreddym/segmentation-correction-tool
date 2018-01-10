package com.ibm.icu.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

















public class ICULocaleService
  extends ICUService
{
  private Locale fallbackLocale;
  private String fallbackLocaleName;
  
  public ICULocaleService() {}
  
  public ICULocaleService(String name)
  {
    super(name);
  }
  




  public Object get(Locale locale)
  {
    return get(locale, -1, null);
  }
  



  public Object get(Locale locale, int kind)
  {
    return get(locale, kind, null);
  }
  



  public Object get(Locale locale, Locale[] actualReturn)
  {
    return get(locale, -1, actualReturn);
  }
  





  public Object get(Locale locale, int kind, Locale[] actualReturn)
  {
    ICUService.Key key = createKey(locale.toString(), kind);
    if (actualReturn == null) {
      return getKey(key);
    }
    
    String[] temp = new String[1];
    Object result = getKey(key, temp);
    if (result != null) {
      int n = temp[0].indexOf("/");
      if (n >= 0) {
        temp[0] = temp[0].substring(n + 1);
      }
      actualReturn[0] = LocaleUtility.getLocaleFromName(temp[0]);
    }
    return result;
  }
  




  public ICUService.Factory registerObject(Object obj, Locale locale)
  {
    return registerObject(obj, locale, -1, 0);
  }
  




  public ICUService.Factory registerObject(Object obj, Locale locale, int kind)
  {
    return registerObject(obj, locale, kind, 0);
  }
  



  public ICUService.Factory registerObject(Object obj, Locale locale, int kind, int coverage)
  {
    ICUService.Factory factory = new SimpleLocaleKeyFactory(obj, locale, kind, coverage);
    return registerFactory(factory);
  }
  



  public Locale[] getAvailableLocales()
  {
    Set visIDs = getVisibleIDs();
    Iterator iter = visIDs.iterator();
    Locale[] locales = new Locale[visIDs.size()];
    int n = 0;
    while (iter.hasNext()) {
      Locale loc = LocaleUtility.getLocaleFromName((String)iter.next());
      locales[(n++)] = loc;
    }
    return locales;
  }
  



  public static class LocaleKey
    extends ICUService.Key
  {
    private int kind;
    

    private String primaryID;
    

    private String fallbackID;
    

    private String currentID;
    

    public static final int KIND_ANY = -1;
    


    public static LocaleKey createWithCanonicalFallback(String primaryID, String canonicalFallbackID)
    {
      return createWithCanonicalFallback(primaryID, canonicalFallbackID, -1);
    }
    


    public static LocaleKey createWithCanonicalFallback(String primaryID, String canonicalFallbackID, int kind)
    {
      if (primaryID == null) {
        return null;
      }
      String canonicalPrimaryID = LocaleUtility.canonicalLocaleString(primaryID);
      return new LocaleKey(primaryID, canonicalPrimaryID, canonicalFallbackID, kind);
    }
    





    protected LocaleKey(String primaryID, String canonicalPrimaryID, String canonicalFallbackID, int kind)
    {
      super();
      
      this.kind = kind;
      
      if (canonicalPrimaryID == null) {
        this.primaryID = "";
      } else {
        this.primaryID = canonicalPrimaryID;
      }
      if (this.primaryID == "") {
        fallbackID = null;
      }
      else if ((canonicalFallbackID == null) || (this.primaryID.equals(canonicalFallbackID))) {
        fallbackID = "";
      } else {
        fallbackID = canonicalFallbackID;
      }
      

      currentID = this.primaryID;
    }
    


    public String prefix()
    {
      return kind == -1 ? null : Integer.toString(kind());
    }
    


    public int kind()
    {
      return kind;
    }
    


    public String canonicalID()
    {
      return primaryID;
    }
    


    public String currentID()
    {
      return currentID;
    }
    


    public String currentDescriptor()
    {
      String result = currentID();
      if (result != null) {
        result = "/" + result;
        if (kind != -1) {
          result = prefix() + result;
        }
      }
      return result;
    }
    


    public Locale canonicalLocale()
    {
      return LocaleUtility.getLocaleFromName(primaryID);
    }
    


    public Locale currentLocale()
    {
      return LocaleUtility.getLocaleFromName(currentID);
    }
    








    public boolean fallback()
    {
      int x = currentID.lastIndexOf('_');
      if (x != -1) {
        currentID = currentID.substring(0, x);
        return true;
      }
      if (fallbackID != null) {
        currentID = fallbackID;
        fallbackID = (fallbackID.length() == 0 ? null : "");
        return true;
      }
      currentID = null;
      return false;
    }
    



    public boolean isFallbackOf(String id)
    {
      return LocaleUtility.isFallbackOf(canonicalID(), id);
    }
  }
  











  public static abstract class LocaleKeyFactory
    implements ICUService.Factory
  {
    protected final String name;
    










    protected final int coverage;
    










    public static final int VISIBLE = 0;
    









    public static final int INVISIBLE = 1;
    










    protected LocaleKeyFactory(int coverage)
    {
      this.coverage = coverage;
      name = null;
    }
    


    protected LocaleKeyFactory(int coverage, String name)
    {
      this.coverage = coverage;
      this.name = name;
    }
    




    public Object create(ICUService.Key key, ICUService service)
    {
      if (handlesKey(key)) {
        ICULocaleService.LocaleKey lkey = (ICULocaleService.LocaleKey)key;
        Locale loc = lkey.currentLocale();
        int kind = lkey.kind();
        
        return handleCreate(loc, kind, service);
      }
      


      return null;
    }
    
    protected boolean handlesKey(ICUService.Key key) {
      if (key == null) {
        return false;
      }
      
      String id = key.currentID();
      Set supported = getSupportedIDs();
      return supported.contains(id);
    }
    




















    public void updateVisibleIDs(Map result)
    {
      Set cache = getSupportedIDs();
      
      boolean visible = (coverage & 0x1) == 0;
      



      Map toRemap = new HashMap();
      Iterator iter = cache.iterator();
      while (iter.hasNext()) {
        String id = (String)iter.next();
        



















        if (!visible) {
          result.remove(id);
        } else {
          toRemap.put(id, this);
        }
      }
      
      result.putAll(toRemap);
    }
    


    public String getDisplayName(String id, Locale locale)
    {
      if (isSupportedID(id)) {
        if (locale == null) {
          return id;
        }
        Locale loc = LocaleUtility.getLocaleFromName(id);
        return loc.getDisplayName(locale);
      }
      return null;
    }
    





    protected Object handleCreate(Locale loc, int kind, ICUService service)
    {
      return null;
    }
    




    protected boolean isSupportedID(String id)
    {
      return getSupportedIDs().contains(id);
    }
    




    protected Set getSupportedIDs()
    {
      return Collections.EMPTY_SET;
    }
    


    public String toString()
    {
      StringBuffer buf = new StringBuffer(super.toString());
      if (name != null) {
        buf.append(", name: ");
        buf.append(name);
      }
      buf.append(", coverage: ");
      String[] coverage_names = { "visible", "invisible", "visible_covers", "invisible_covers", "????", "visible_covers_remove" };
      

      buf.append(coverage_names[coverage]);
      return buf.toString();
    }
  }
  
  public static class SimpleLocaleKeyFactory
    extends ICULocaleService.LocaleKeyFactory
  {
    private final Object obj;
    private final String id;
    private final int kind;
    
    public SimpleLocaleKeyFactory(Object obj, Locale locale, int kind, int coverage)
    {
      this(obj, locale, kind, coverage, null);
    }
    
    public SimpleLocaleKeyFactory(Object obj, Locale locale, int kind, int coverage, String name) {
      super(name);
      
      this.obj = obj;
      id = LocaleUtility.canonicalLocaleString(locale.toString());
      this.kind = kind;
    }
    


    public Object create(ICUService.Key key, ICUService service)
    {
      ICULocaleService.LocaleKey lkey = (ICULocaleService.LocaleKey)key;
      if ((kind == -1) || (kind == lkey.kind())) {
        String keyID = lkey.currentID();
        if (id.equals(keyID)) {
          return obj;
        }
      }
      return null;
    }
    
    protected boolean isSupportedID(String id) {
      return this.id.equals(id);
    }
    
    public void updateVisibleIDs(Map result) {
      if ((coverage & 0x1) == 0) {
        result.put(id, this);
      } else {
        result.remove(id);
      }
    }
    
    public String toString() {
      StringBuffer buf = new StringBuffer(super.toString());
      buf.append(", id: ");
      buf.append(id);
      buf.append(", kind: ");
      buf.append(kind);
      return buf.toString();
    }
  }
  




  public static class ICUResourceBundleFactory
    extends ICULocaleService.LocaleKeyFactory
  {
    protected final String bundleName;
    



    public ICUResourceBundleFactory()
    {
      this("LocaleElements");
    }
    



    public ICUResourceBundleFactory(String bundleName)
    {
      super();
      
      this.bundleName = bundleName;
    }
    


    protected Set getSupportedIDs()
    {
      return ICULocaleData.getAvailableLocaleNameSet(bundleName);
    }
    



    protected Object handleCreate(Locale loc, int kind, ICUService service)
    {
      return ICULocaleData.getResourceBundle(bundleName, loc);
    }
    
    public String toString() {
      return super.toString() + ", bundle: " + bundleName;
    }
  }
  



  public String validateFallbackLocale()
  {
    Locale loc = Locale.getDefault();
    if (loc != fallbackLocale) {
      synchronized (this) {
        if (loc != fallbackLocale) {
          fallbackLocale = loc;
          fallbackLocaleName = LocaleUtility.canonicalLocaleString(loc.toString());
          clearServiceCache();
        }
      }
    }
    return fallbackLocaleName;
  }
  
  public ICUService.Key createKey(String id) {
    return LocaleKey.createWithCanonicalFallback(id, validateFallbackLocale());
  }
  
  public ICUService.Key createKey(String id, int kind) {
    return LocaleKey.createWithCanonicalFallback(id, validateFallbackLocale(), kind);
  }
}
