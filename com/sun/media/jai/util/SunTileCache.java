package com.sun.media.jai.util;

import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.io.PrintStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Observable;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;
import javax.media.jai.EnumeratedParameter;
import javax.media.jai.TileCache;
import javax.media.jai.util.ImagingListener;























































public final class SunTileCache
  extends Observable
  implements TileCache, CacheDiagnostics
{
  private static final long DEFAULT_MEMORY_CAPACITY = 16777216L;
  private static final int DEFAULT_HASHTABLE_CAPACITY = 1009;
  private static final float LOAD_FACTOR = 0.5F;
  private Hashtable cache;
  private SortedSet cacheSortedSet;
  private long memoryCapacity;
  private long memoryUsage = 0L;
  

  private float memoryThreshold = 0.75F;
  

  private long timeStamp = 0L;
  



  private Comparator comparator = null;
  

  private SunCachedTile first = null;
  

  private SunCachedTile last = null;
  

  private long tileCount = 0L;
  

  private long hitCount = 0L;
  

  private long missCount = 0L;
  

  private boolean diagnostics = false;
  

  private static final int ADD = 0;
  

  private static final int REMOVE = 1;
  

  private static final int REMOVE_FROM_FLUSH = 2;
  
  private static final int REMOVE_FROM_MEMCON = 3;
  
  private static final int UPDATE_FROM_ADD = 4;
  
  private static final int UPDATE_FROM_GETTILE = 5;
  
  private static final int ABOUT_TO_REMOVE = 6;
  

  public static EnumeratedParameter[] getCachedTileActions()
  {
    return new EnumeratedParameter[] { new EnumeratedParameter("add", 0), new EnumeratedParameter("remove", 1), new EnumeratedParameter("remove_by_flush", 2), new EnumeratedParameter("remove_by_memorycontrol", 3), new EnumeratedParameter("timestamp_update_by_add", 4), new EnumeratedParameter("timestamp_update_by_gettile", 5), new EnumeratedParameter("preremove", 6) };
  }
  












  public SunTileCache()
  {
    this(16777216L);
  }
  







  public SunTileCache(long memoryCapacity)
  {
    if (memoryCapacity < 0L) {
      throw new IllegalArgumentException(JaiI18N.getString("SunTileCache"));
    }
    
    this.memoryCapacity = memoryCapacity;
    


    cache = new Hashtable(1009, 0.5F);
  }
  
















  public void add(RenderedImage owner, int tileX, int tileY, Raster tile)
  {
    add(owner, tileX, tileY, tile, null);
  }
  


















  public synchronized void add(RenderedImage owner, int tileX, int tileY, Raster tile, Object tileCacheMetric)
  {
    if (memoryCapacity == 0L) {
      return;
    }
    


    Object key = SunCachedTile.hashKey(owner, tileX, tileY);
    SunCachedTile ct = (SunCachedTile)cache.get(key);
    
    if (ct != null)
    {
      timeStamp = (timeStamp++);
      
      if (ct != first)
      {
        if (ct == last) {
          last = previous;
          last.next = null;
        } else {
          previous.next = next;
          next.previous = previous;
        }
        
        previous = null;
        next = first;
        
        first.previous = ct;
        first = ct;
      }
      
      hitCount += 1L;
      
      if (diagnostics) {
        action = 4;
        setChanged();
        notifyObservers(ct);
      }
    }
    else {
      ct = new SunCachedTile(owner, tileX, tileY, tile, tileCacheMetric);
      


      if ((memoryUsage + memorySize > memoryCapacity) && (memorySize > ((float)memoryCapacity * memoryThreshold)))
      {
        return;
      }
      
      timeStamp = (timeStamp++);
      previous = null;
      next = first;
      
      if ((first == null) && (last == null)) {
        first = ct;
        last = ct;
      } else {
        first.previous = ct;
        first = ct;
      }
      

      if (cache.put(key, ct) == null) {
        memoryUsage += memorySize;
        tileCount += 1L;
        

        if (cacheSortedSet != null) {
          cacheSortedSet.add(ct);
        }
        
        if (diagnostics) {
          action = 0;
          setChanged();
          notifyObservers(ct);
        }
      }
      

      if (memoryUsage > memoryCapacity) {
        memoryControl();
      }
    }
  }
  








  public synchronized void remove(RenderedImage owner, int tileX, int tileY)
  {
    if (memoryCapacity == 0L) {
      return;
    }
    
    Object key = SunCachedTile.hashKey(owner, tileX, tileY);
    SunCachedTile ct = (SunCachedTile)cache.get(key);
    
    if (ct != null)
    {





      action = 6;
      setChanged();
      notifyObservers(ct);
      
      ct = (SunCachedTile)cache.remove(key);
      

      if (ct != null) {
        memoryUsage -= memorySize;
        tileCount -= 1L;
        
        if (cacheSortedSet != null) {
          cacheSortedSet.remove(ct);
        }
        
        if (ct == first) {
          if (ct == last) {
            first = null;
            last = null;
          } else {
            first = next;
            first.previous = null;
          }
        } else if (ct == last) {
          last = previous;
          last.next = null;
        } else {
          previous.next = next;
          next.previous = previous;
        }
        












        if (diagnostics) {
          action = 1;
          setChanged();
          notifyObservers(ct);
        }
        
        previous = null;
        next = null;
        ct = null;
      }
    }
  }
  













  public synchronized Raster getTile(RenderedImage owner, int tileX, int tileY)
  {
    Raster tile = null;
    
    if (memoryCapacity == 0L) {
      return null;
    }
    
    Object key = SunCachedTile.hashKey(owner, tileX, tileY);
    SunCachedTile ct = (SunCachedTile)cache.get(key);
    
    if (ct == null) {
      missCount += 1L;
    } else {
      tile = ct.getTile();
      

      timeStamp = (timeStamp++);
      
      if (ct != first)
      {
        if (ct == last) {
          last = previous;
          last.next = null;
        } else {
          previous.next = next;
          next.previous = previous;
        }
        
        previous = null;
        next = first;
        
        first.previous = ct;
        first = ct;
      }
      
      hitCount += 1L;
      
      if (diagnostics) {
        action = 5;
        setChanged();
        notifyObservers(ct);
      }
    }
    
    return tile;
  }
  








  public synchronized Raster[] getTiles(RenderedImage owner)
  {
    Raster[] tiles = null;
    
    if (memoryCapacity == 0L) {
      return null;
    }
    
    int size = Math.min(owner.getNumXTiles() * owner.getNumYTiles(), (int)tileCount);
    

    if (size > 0) {
      int minTx = owner.getMinTileX();
      int minTy = owner.getMinTileY();
      int maxTx = minTx + owner.getNumXTiles();
      int maxTy = minTy + owner.getNumYTiles();
      

      Vector temp = new Vector(10, 20);
      
      for (int y = minTy; y < maxTy; y++) {
        for (int x = minTx; x < maxTx; x++)
        {


          Raster raster = null;
          Object key = SunCachedTile.hashKey(owner, x, y);
          SunCachedTile ct = (SunCachedTile)cache.get(key);
          
          if (ct == null) {
            raster = null;
            missCount += 1L;
          } else {
            raster = ct.getTile();
            

            timeStamp = (timeStamp++);
            
            if (ct != first)
            {
              if (ct == last) {
                last = previous;
                last.next = null;
              } else {
                previous.next = next;
                next.previous = previous;
              }
              
              previous = null;
              next = first;
              
              first.previous = ct;
              first = ct;
            }
            
            hitCount += 1L;
            
            if (diagnostics) {
              action = 5;
              setChanged();
              notifyObservers(ct);
            }
          }
          

          if (raster != null) {
            temp.add(raster);
          }
        }
      }
      
      int tmpsize = temp.size();
      if (tmpsize > 0) {
        tiles = (Raster[])temp.toArray(new Raster[tmpsize]);
      }
    }
    
    return tiles;
  }
  





  public void removeTiles(RenderedImage owner)
  {
    if (memoryCapacity > 0L) {
      int minTx = owner.getMinTileX();
      int minTy = owner.getMinTileY();
      int maxTx = minTx + owner.getNumXTiles();
      int maxTy = minTy + owner.getNumYTiles();
      
      for (int y = minTy; y < maxTy; y++) {
        for (int x = minTx; x < maxTx; x++) {
          remove(owner, x, y);
        }
      }
    }
  }
  














  public synchronized void addTiles(RenderedImage owner, Point[] tileIndices, Raster[] tiles, Object tileCacheMetric)
  {
    if (memoryCapacity == 0L) {
      return;
    }
    

    for (int i = 0; i < tileIndices.length; i++) {
      int tileX = x;
      int tileY = y;
      Raster tile = tiles[i];
      
      Object key = SunCachedTile.hashKey(owner, tileX, tileY);
      SunCachedTile ct = (SunCachedTile)cache.get(key);
      
      if (ct != null)
      {
        timeStamp = (timeStamp++);
        
        if (ct != first)
        {
          if (ct == last) {
            last = previous;
            last.next = null;
          } else {
            previous.next = next;
            next.previous = previous;
          }
          
          previous = null;
          next = first;
          
          first.previous = ct;
          first = ct;
        }
        
        hitCount += 1L;
        
        if (diagnostics) {
          action = 4;
          setChanged();
          notifyObservers(ct);
        }
      }
      else {
        ct = new SunCachedTile(owner, tileX, tileY, tile, tileCacheMetric);
        


        if ((memoryUsage + memorySize > memoryCapacity) && (memorySize > ((float)memoryCapacity * memoryThreshold)))
        {
          return;
        }
        
        timeStamp = (timeStamp++);
        previous = null;
        next = first;
        
        if ((first == null) && (last == null)) {
          first = ct;
          last = ct;
        } else {
          first.previous = ct;
          first = ct;
        }
        

        if (cache.put(key, ct) == null) {
          memoryUsage += memorySize;
          tileCount += 1L;
          

          if (cacheSortedSet != null) {
            cacheSortedSet.add(ct);
          }
          
          if (diagnostics) {
            action = 0;
            setChanged();
            notifyObservers(ct);
          }
        }
        

        if (memoryUsage > memoryCapacity) {
          memoryControl();
        }
      }
    }
  }
  










  public synchronized Raster[] getTiles(RenderedImage owner, Point[] tileIndices)
  {
    if (memoryCapacity == 0L) {
      return null;
    }
    
    Raster[] tiles = new Raster[tileIndices.length];
    
    for (int i = 0; i < tiles.length; i++) {
      int tileX = x;
      int tileY = y;
      
      Object key = SunCachedTile.hashKey(owner, tileX, tileY);
      SunCachedTile ct = (SunCachedTile)cache.get(key);
      
      if (ct == null) {
        tiles[i] = null;
        missCount += 1L;
      } else {
        tiles[i] = ct.getTile();
        

        timeStamp = (timeStamp++);
        
        if (ct != first)
        {
          if (ct == last) {
            last = previous;
            last.next = null;
          } else {
            previous.next = next;
            next.previous = previous;
          }
          
          previous = null;
          next = first;
          
          first.previous = ct;
          first = ct;
        }
        
        hitCount += 1L;
        
        if (diagnostics) {
          action = 5;
          setChanged();
          notifyObservers(ct);
        }
      }
    }
    
    return tiles;
  }
  










  public synchronized void flush()
  {
    Enumeration keys = cache.keys();
    

    hitCount = 0L;
    missCount = 0L;
    
    while (keys.hasMoreElements()) {
      Object key = keys.nextElement();
      SunCachedTile ct = (SunCachedTile)cache.remove(key);
      

      if (ct != null) {
        memoryUsage -= memorySize;
        tileCount -= 1L;
        
        if (ct == first) {
          if (ct == last) {
            first = null;
            last = null;
          } else {
            first = next;
            first.previous = null;
          }
        } else if (ct == last) {
          last = previous;
          last.next = null;
        } else {
          previous.next = next;
          next.previous = previous;
        }
        
        previous = null;
        next = null;
        

        if (diagnostics) {
          action = 2;
          setChanged();
          notifyObservers(ct);
        }
      }
    }
    
    if (memoryCapacity > 0L) {
      cache = new Hashtable(1009, 0.5F);
    }
    
    if (cacheSortedSet != null) {
      cacheSortedSet.clear();
      cacheSortedSet = Collections.synchronizedSortedSet(new TreeSet(comparator));
    }
    

    tileCount = 0L;
    timeStamp = 0L;
    memoryUsage = 0L;
  }
  






  public int getTileCapacity()
  {
    return 0;
  }
  





  public void setTileCapacity(int tileCapacity) {}
  





  public long getMemoryCapacity()
  {
    return memoryCapacity;
  }
  












  public void setMemoryCapacity(long memoryCapacity)
  {
    if (memoryCapacity < 0L)
      throw new IllegalArgumentException(JaiI18N.getString("SunTileCache"));
    if (memoryCapacity == 0L) {
      flush();
    }
    
    this.memoryCapacity = memoryCapacity;
    
    if (memoryUsage > memoryCapacity) {
      memoryControl();
    }
  }
  
  public void enableDiagnostics()
  {
    diagnostics = true;
  }
  
  public void disableDiagnostics()
  {
    diagnostics = false;
  }
  
  public long getCacheTileCount() {
    return tileCount;
  }
  
  public long getCacheMemoryUsed() {
    return memoryUsage;
  }
  
  public long getCacheHitCount() {
    return hitCount;
  }
  
  public long getCacheMissCount() {
    return missCount;
  }
  




  public void resetCounts()
  {
    hitCount = 0L;
    missCount = 0L;
  }
  




  public void setMemoryThreshold(float mt)
  {
    if ((mt < 0.0F) || (mt > 1.0F)) {
      throw new IllegalArgumentException(JaiI18N.getString("SunTileCache"));
    }
    memoryThreshold = mt;
    memoryControl();
  }
  





  public float getMemoryThreshold()
  {
    return memoryThreshold;
  }
  
  public String toString()
  {
    return getClass().getName() + "@" + Integer.toHexString(hashCode()) + ": memoryCapacity = " + Long.toHexString(memoryCapacity) + " memoryUsage = " + Long.toHexString(memoryUsage) + " #tilesInCache = " + Integer.toString(cache.size());
  }
  



  public Object getCachedObject()
  {
    return cache;
  }
  




  public synchronized void memoryControl()
  {
    if (cacheSortedSet == null) {
      standard_memory_control();
    } else {
      custom_memory_control();
    }
  }
  
  private final void standard_memory_control()
  {
    long limit = ((float)memoryCapacity * memoryThreshold);
    
    while ((memoryUsage > limit) && (last != null)) {
      SunCachedTile ct = (SunCachedTile)cache.get(last.key);
      
      if (ct != null) {
        ct = (SunCachedTile)cache.remove(last.key);
        
        memoryUsage -= last.memorySize;
        tileCount -= 1L;
        
        last = last.previous;
        
        if (last != null) {
          last.next.previous = null;
          last.next = null;
        } else {
          first = null;
        }
        

        if (diagnostics) {
          action = 3;
          setChanged();
          notifyObservers(ct);
        }
      }
    }
  }
  
  private final void custom_memory_control()
  {
    long limit = ((float)memoryCapacity * memoryThreshold);
    Iterator iter = cacheSortedSet.iterator();
    

    while ((iter.hasNext()) && (memoryUsage > limit)) {
      SunCachedTile ct = (SunCachedTile)iter.next();
      
      memoryUsage -= memorySize;
      tileCount -= 1L;
      
      try
      {
        iter.remove();
      } catch (ConcurrentModificationException e) {
        ImagingListener listener = ImageUtil.getImagingListener((RenderingHints)null);
        
        listener.errorOccurred(JaiI18N.getString("SunTileCache0"), e, this, false);
      }
      



      if (ct == first) {
        if (ct == last) {
          first = null;
          last = null;
        } else {
          first = next;
          
          if (first != null) {
            first.previous = null;
            first.next = next.next;
          }
        }
      } else if (ct == last) {
        last = previous;
        
        if (last != null) {
          last.next = null;
          last.previous = previous.previous;
        }
      } else {
        SunCachedTile ptr = first.next;
        
        while (ptr != null)
        {
          if (ptr == ct) {
            if (previous != null) {
              previous.next = next;
            }
            
            if (next == null) break;
            next.previous = previous; break;
          }
          



          ptr = next;
        }
      }
      

      cache.remove(key);
      

      if (diagnostics) {
        action = 3;
        setChanged();
        notifyObservers(ct);
      }
    }
    



    if (memoryUsage > limit) {
      standard_memory_control();
    }
  }
  








  public synchronized void setTileComparator(Comparator c)
  {
    comparator = c;
    
    if (comparator == null)
    {
      if (cacheSortedSet != null) {
        cacheSortedSet.clear();
        cacheSortedSet = null;
      }
    }
    else {
      cacheSortedSet = Collections.synchronizedSortedSet(new TreeSet(comparator));
      
      Enumeration keys = cache.keys();
      
      while (keys.hasMoreElements()) {
        Object key = keys.nextElement();
        Object ct = cache.get(key);
        cacheSortedSet.add(ct);
      }
    }
  }
  




  public Comparator getTileComparator()
  {
    return comparator;
  }
  

  public void dump()
  {
    System.out.println("first = " + first);
    System.out.println("last  = " + last);
    
    Iterator iter = cacheSortedSet.iterator();
    int k = 0;
    
    while (iter.hasNext()) {
      SunCachedTile ct = (SunCachedTile)iter.next();
      System.out.println(k++);
      System.out.println(ct);
    }
  }
  
  void sendExceptionToListener(String message, Exception e) {
    ImagingListener listener = ImageUtil.getImagingListener((RenderingHints)null);
    
    listener.errorOccurred(message, e, this, false);
  }
}
