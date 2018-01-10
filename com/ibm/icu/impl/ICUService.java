package com.ibm.icu.impl;

import java.io.PrintStream;
import java.lang.ref.SoftReference;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
















































































public class ICUService
  extends ICUNotifier
{
  protected final String name;
  
  public ICUService()
  {
    name = "";
  }
  


  public ICUService(String name)
  {
    this.name = name;
  }
  





  private final ICURWLock factoryLock = new ICURWLock();
  



  private final List factories = new ArrayList();
  




  private int defaultSize = 0;
  



  private SoftReference cacheref;
  



  private SoftReference idref;
  


  private LocaleRef dnref;
  



  public static class Key
  {
    private final String id;
    



    public Key(String id)
    {
      this.id = id;
    }
    


    public final String id()
    {
      return id;
    }
    



    public String canonicalID()
    {
      return id;
    }
    



    public String currentID()
    {
      return canonicalID();
    }
    








    public String currentDescriptor()
    {
      return "/" + currentID();
    }
    






    public boolean fallback()
    {
      return false;
    }
    



    public boolean isFallbackOf(String id)
    {
      return canonicalID().equals(id);
    }
  }
  







  public static abstract interface Factory
  {
    public abstract Object create(ICUService.Key paramKey, ICUService paramICUService);
    






    public abstract void updateVisibleIDs(Map paramMap);
    






    public abstract String getDisplayName(String paramString, Locale paramLocale);
  }
  






  public static class SimpleFactory
    implements ICUService.Factory
  {
    protected Object instance;
    





    protected String id;
    





    protected boolean visible;
    






    public SimpleFactory(Object instance, String id)
    {
      this(instance, id, true);
    }
    




    public SimpleFactory(Object instance, String id, boolean visible)
    {
      if ((instance == null) || (id == null)) {
        throw new IllegalArgumentException("Instance or id is null");
      }
      this.instance = instance;
      this.id = id;
      this.visible = visible;
    }
    



    public Object create(ICUService.Key key, ICUService service)
    {
      if (id.equals(key.currentID())) {
        return instance;
      }
      return null;
    }
    



    public void updateVisibleIDs(Map result)
    {
      if (visible) {
        result.put(id, this);
      } else {
        result.remove(id);
      }
    }
    




    public String getDisplayName(String id, Locale locale)
    {
      return (visible) && (this.id.equals(id)) ? id : null;
    }
    


    public String toString()
    {
      StringBuffer buf = new StringBuffer(super.toString());
      buf.append(", id: ");
      buf.append(id);
      buf.append(", visible: ");
      buf.append(visible);
      return buf.toString();
    }
  }
  



  public Object get(String descriptor)
  {
    return getKey(createKey(descriptor), null);
  }
  



  public Object get(String descriptor, String[] actualReturn)
  {
    if (descriptor == null) {
      throw new NullPointerException("descriptor must not be null");
    }
    return getKey(createKey(descriptor), actualReturn);
  }
  


  public Object getKey(Key key)
  {
    return getKey(key, null);
  }
  














  public Object getKey(Key key, String[] actualReturn)
  {
    return getKey(key, actualReturn, null);
  }
  
  public Object getKey(Key key, String[] actualReturn, Factory factory)
  {
    if (factories.size() == 0) {
      return handleDefault(key, actualReturn);
    }
    
    boolean debug = false;
    if (debug) { System.out.println("Service: " + name + " key: " + key);
    }
    CacheEntry result = null;
    if (key != null)
    {
      try
      {

        factoryLock.acquireRead();
        
        Map cache = null;
        SoftReference cref = cacheref;
        if (cref != null) {
          cache = (Map)cref.get();
        }
        if (cache == null)
        {

          cache = Collections.synchronizedMap(new HashMap());
          cref = new SoftReference(cache);
        }
        
        String currentDescriptor = null;
        ArrayList cacheDescriptorList = null;
        boolean putInCache = false;
        
        int NDebug = 0;
        
        int startIndex = 0;
        int limit = factories.size();
        boolean cacheResult = true;
        if (factory != null) {
          for (int i = 0; i < limit; i++) {
            if (factory == factories.get(i)) {
              startIndex = i + 1;
              break;
            }
          }
          if (startIndex == 0) {
            throw new InternalError("Factory " + factory + "not registered with service: " + this);
          }
          cacheResult = false;
        }
        
        do
        {
          currentDescriptor = key.currentDescriptor();
          if (debug) System.out.println(name + "[" + NDebug++ + "] looking for: " + currentDescriptor);
          result = (CacheEntry)cache.get(currentDescriptor);
          if (result != null) {
            if (!debug) break; System.out.println(name + " found with descriptor: " + currentDescriptor); break;
          }
          
          if (debug) { System.out.println("did not find: " + currentDescriptor + " in cache");
          }
          



          putInCache = cacheResult;
          

          int index = startIndex;
          while (index < limit) {
            Factory f = (Factory)factories.get(index++);
            if (debug) System.out.println("trying factory[" + (index - 1) + "] " + f.toString());
            Object service = f.create(key, this);
            if (service != null) {
              result = new CacheEntry(currentDescriptor, service);
              if (!debug) break; System.out.println(name + " factory supported: " + currentDescriptor + ", caching"); break;
            }
            
            if (debug) { System.out.println("factory did not support: " + currentDescriptor);
            }
          }
          





          if (cacheDescriptorList == null) {
            cacheDescriptorList = new ArrayList(5);
          }
          cacheDescriptorList.add(currentDescriptor);
        }
        while (key.fallback());
        
        if (result != null) { Iterator iter;
          if (putInCache) {
            cache.put(actualDescriptor, result);
            if (cacheDescriptorList != null) {
              iter = cacheDescriptorList.iterator();
              while (iter.hasNext()) {
                String desc = (String)iter.next();
                if (debug) { System.out.println(name + " adding descriptor: '" + desc + "' for actual: '" + actualDescriptor + "'");
                }
                cache.put(desc, result);
              }
            }
            



            cacheref = cref;
          }
          
          if (actualReturn != null)
          {
            if (actualDescriptor.indexOf("/") == 0) {
              actualReturn[0] = actualDescriptor.substring(1);
            } else {
              actualReturn[0] = actualDescriptor;
            }
          }
          
          if (debug) { System.out.println("found in service: " + name);
          }
          return service;
        }
      }
      finally {
        factoryLock.releaseRead();
      }
    }
    
    if (debug) { System.out.println("not found in service: " + name);
    }
    return handleDefault(key, actualReturn);
  }
  
  private static final class CacheEntry
  {
    final String actualDescriptor;
    final Object service;
    
    CacheEntry(String actualDescriptor, Object service)
    {
      this.actualDescriptor = actualDescriptor;
      this.service = service;
    }
  }
  




  protected Object handleDefault(Key key, String[] actualIDReturn)
  {
    return null;
  }
  



  public Set getVisibleIDs()
  {
    return getVisibleIDs(null);
  }
  










  public Set getVisibleIDs(String matchID)
  {
    Set result = getVisibleIDMap().keySet();
    
    Key fallbackKey = createKey(matchID);
    
    if (fallbackKey != null) {
      Set temp = new HashSet(result.size());
      Iterator iter = result.iterator();
      while (iter.hasNext()) {
        String id = (String)iter.next();
        if (fallbackKey.isFallbackOf(id)) {
          temp.add(id);
        }
      }
      result = temp;
    }
    return result;
  }
  


  private Map getVisibleIDMap()
  {
    Map idcache = null;
    SoftReference ref = idref;
    if (ref != null) {
      idcache = (Map)ref.get();
    }
    while (idcache == null) {
      synchronized (this) {
        if ((ref == idref) || (idref == null))
        {
          try
          {
            factoryLock.acquireRead();
            idcache = new HashMap();
            ListIterator lIter = factories.listIterator(factories.size());
            while (lIter.hasPrevious()) {
              Factory f = (Factory)lIter.previous();
              f.updateVisibleIDs(idcache);
            }
            idcache = Collections.unmodifiableMap(idcache);
            idref = new SoftReference(idcache);
          }
          finally {
            factoryLock.releaseRead();
          }
          
        }
        else
        {
          ref = idref;
          idcache = (Map)ref.get();
        }
      }
    }
    
    return idcache;
  }
  




  public String getDisplayName(String id)
  {
    return getDisplayName(id, Locale.getDefault());
  }
  




  public String getDisplayName(String id, Locale locale)
  {
    Map m = getVisibleIDMap();
    Factory f = (Factory)m.get(id);
    return f != null ? f.getDisplayName(id, locale) : null;
  }
  




  public SortedMap getDisplayNames()
  {
    Locale locale = Locale.getDefault();
    return getDisplayNames(locale, null, null);
  }
  



  public SortedMap getDisplayNames(Locale locale)
  {
    return getDisplayNames(locale, null, null);
  }
  



  public SortedMap getDisplayNames(Locale locale, Comparator com)
  {
    return getDisplayNames(locale, com, null);
  }
  



  public SortedMap getDisplayNames(Locale locale, String matchID)
  {
    return getDisplayNames(locale, null, matchID);
  }
  









  public SortedMap getDisplayNames(Locale locale, Comparator com, String matchID)
  {
    SortedMap dncache = null;
    LocaleRef ref = dnref;
    
    if (ref != null) {
      dncache = ref.get(locale, com);
    }
    
    while (dncache == null) {
      synchronized (this) {
        if ((ref == dnref) || (dnref == null)) {
          dncache = new TreeMap(com);
          
          Map m = getVisibleIDMap();
          Iterator ei = m.entrySet().iterator();
          while (ei.hasNext()) {
            Map.Entry e = (Map.Entry)ei.next();
            String id = (String)e.getKey();
            Factory f = (Factory)e.getValue();
            dncache.put(f.getDisplayName(id, locale), id);
          }
          
          dncache = Collections.unmodifiableSortedMap(dncache);
          dnref = new LocaleRef(dncache, locale, com);
        } else {
          ref = dnref;
          dncache = ref.get(locale, com);
        }
      }
    }
    
    Key matchKey = createKey(matchID);
    if (matchKey == null) {
      return dncache;
    }
    
    SortedMap result = new TreeMap(dncache);
    Iterator iter = result.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry e = (Map.Entry)iter.next();
      if (!matchKey.isFallbackOf((String)e.getValue())) {
        iter.remove();
      }
    }
    return result;
  }
  
  private static class LocaleRef
  {
    private final Locale locale;
    private SoftReference ref;
    private Comparator com;
    
    LocaleRef(Map dnCache, Locale locale, Comparator com)
    {
      this.locale = locale;
      this.com = com;
      ref = new SoftReference(dnCache);
    }
    
    SortedMap get(Locale locale, Comparator com)
    {
      SortedMap m = (SortedMap)ref.get();
      if ((m != null) && (this.locale.equals(locale)) && ((this.com == com) || ((this.com != null) && (this.com.equals(com)))))
      {


        return m;
      }
      return null;
    }
  }
  




  public final List factories()
  {
    try
    {
      factoryLock.acquireRead();
      return new ArrayList(factories);
    }
    finally {
      factoryLock.releaseRead();
    }
  }
  



  public Factory registerObject(Object obj, String id)
  {
    return registerObject(obj, id, true);
  }
  




  public Factory registerObject(Object obj, String id, boolean visible)
  {
    String canonicalID = createKey(id).canonicalID();
    return registerFactory(new SimpleFactory(obj, canonicalID, visible));
  }
  




  public final Factory registerFactory(Factory factory)
  {
    if (factory == null) {
      throw new NullPointerException();
    }
    try {
      factoryLock.acquireWrite();
      factories.add(0, factory);
      clearCaches();
    }
    finally {
      factoryLock.releaseWrite();
    }
    notifyChanged();
    return factory;
  }
  




  public final boolean unregisterFactory(Factory factory)
  {
    if (factory == null) {
      throw new NullPointerException();
    }
    
    boolean result = false;
    try {
      factoryLock.acquireWrite();
      if (factories.remove(factory)) {
        result = true;
        clearCaches();
      }
    }
    finally {
      factoryLock.releaseWrite();
    }
    
    if (result) {
      notifyChanged();
    }
    return result;
  }
  


  public final void reset()
  {
    try
    {
      factoryLock.acquireWrite();
      reInitializeFactories();
      clearCaches();
    }
    finally {
      factoryLock.releaseWrite();
    }
    notifyChanged();
  }
  






  protected void reInitializeFactories()
  {
    factories.clear();
  }
  



  public boolean isDefault()
  {
    return factories.size() == defaultSize;
  }
  



  protected void markDefault()
  {
    defaultSize = factories.size();
  }
  




  public Key createKey(String id)
  {
    return id == null ? null : new Key(id);
  }
  









  protected void clearCaches()
  {
    cacheref = null;
    idref = null;
    dnref = null;
  }
  





  protected void clearServiceCache()
  {
    cacheref = null;
  }
  















  protected boolean acceptsListener(EventListener l)
  {
    return l instanceof ServiceListener;
  }
  



  protected void notifyListener(EventListener l)
  {
    ((ServiceListener)l).serviceChanged(this);
  }
  



  public String stats()
  {
    ICURWLock.Stats stats = factoryLock.resetStats();
    if (stats != null) {
      return stats.toString();
    }
    return "no stats";
  }
  


  public String getName()
  {
    return name;
  }
  


  public String toString()
  {
    return super.toString() + "{" + name + "}";
  }
  
  public static abstract interface ServiceListener
    extends EventListener
  {
    public abstract void serviceChanged(ICUService paramICUService);
  }
}
