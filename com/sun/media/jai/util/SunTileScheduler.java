package com.sun.media.jai.util;

import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.Raster;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import javax.media.jai.OpImage;
import javax.media.jai.PlanarImage;
import javax.media.jai.TileCache;
import javax.media.jai.TileComputationListener;
import javax.media.jai.TileRequest;
import javax.media.jai.TileScheduler;
import javax.media.jai.util.ImagingException;
import javax.media.jai.util.ImagingListener;



















































































































































































































































































































































































































































































public final class SunTileScheduler
  implements TileScheduler
{
  private static final int NUM_THREADS_DEFAULT = 2;
  private static final int NUM_PREFETCH_THREADS_DEFAULT = 1;
  private static int numInstances = 0;
  



  private static String name = JaiI18N.getString("SunTileSchedulerName");
  


  private ThreadGroup rootGroup;
  


  private ThreadGroup standardGroup;
  


  private ThreadGroup prefetchGroup;
  

  private int parallelism = 2;
  

  private int prefetchParallelism = 1;
  

  private int priority = 5;
  

  private int prefetchPriority = 1;
  

  private LinkedList queue = null;
  

  private LinkedList prefetchQueue = null;
  





  private Vector workers = new Vector();
  





  private Vector prefetchWorkers = new Vector();
  






  private int numWorkerThreads = 0;
  






  private int numPrefetchThreads = 0;
  








  private Map tilesInProgress = new HashMap();
  









  Map tileRequests = new HashMap();
  







  Map tileJobs = new HashMap();
  





  private String nameOfThisInstance;
  





  static Object tileKey(PlanarImage owner, int tileX, int tileY)
  {
    long idx = tileY * owner.getNumXTiles() + tileX;
    
    BigInteger imageID = (BigInteger)owner.getImageID();
    byte[] buf = imageID.toByteArray();
    int length = buf.length;
    byte[] buf1 = new byte[length + 8];
    System.arraycopy(buf, 0, buf1, 0, length);
    int i = 7; for (int j = 0; i >= 0; j += 8) {
      buf1[(length++)] = ((byte)(int)(idx >> j));i--; }
    return new BigInteger(buf1);
  }
  




  static Set getListeners(List reqList)
  {
    int numReq = reqList.size();
    HashSet listeners = null;
    for (int j = 0; j < numReq; j++) {
      Request req = (Request)reqList.get(j);
      
      if ((listeners != null) && (!listeners.isEmpty())) {
        if (listeners == null) {
          listeners = new HashSet();
        }
        listeners.addAll(listeners);
      }
    }
    
    return listeners;
  }
  



  private static String getStackTraceString(Throwable e)
  {
    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
    PrintStream printStream = new PrintStream(byteStream);
    e.printStackTrace(printStream);
    printStream.flush();
    String stackTraceString = byteStream.toString();
    printStream.close();
    return stackTraceString;
  }
  











  public SunTileScheduler(int parallelism, int priority, int prefetchParallelism, int prefetchPriority)
  {
    this();
    
    setParallelism(parallelism);
    setPriority(priority);
    setPrefetchParallelism(prefetchParallelism);
    setPrefetchPriority(prefetchPriority);
  }
  



  public SunTileScheduler()
  {
    queue = new LinkedList();
    prefetchQueue = new LinkedList();
    
    nameOfThisInstance = (name + numInstances);
    rootGroup = new ThreadGroup(nameOfThisInstance);
    rootGroup.setDaemon(true);
    
    standardGroup = new ThreadGroup(rootGroup, nameOfThisInstance + "Standard");
    
    standardGroup.setDaemon(true);
    
    prefetchGroup = new ThreadGroup(rootGroup, nameOfThisInstance + "Prefetch");
    
    prefetchGroup.setDaemon(true);
    
    numInstances += 1;
  }
  




  Exception compute(PlanarImage owner, Point[] tileIndices, Raster[] tiles, int offset, int numTiles, Request request)
  {
    Exception exception = null;
    
    int j = offset;
    if ((request == null) || (listeners == null)) {
      for (int i = 0; i < numTiles; j++) {
        Point p = tileIndices[j];
        try
        {
          tiles[j] = owner.getTile(x, y);
        } catch (Exception e) {
          exception = e;
          

          break;
        }
        i++;


      }
      



    }
    else
    {


      Request[] reqs = { request };
      for (int i = 0; i < numTiles; j++) {
        Point p = tileIndices[j];
        

        Integer tileStatus = new Integer(1);
        
        tileStatus.put(p, tileStatus);
        try
        {
          tiles[j] = owner.getTile(x, y);
          Iterator iter = listeners.iterator();
          while (iter.hasNext())
          {
            tileStatus = new Integer(2);
            
            tileStatus.put(p, tileStatus);
            
            TileComputationListener listener = (TileComputationListener)iter.next();
            
            listener.tileComputed(this, reqs, owner, x, y, tiles[j]);
          }
          

        }
        catch (Exception e)
        {
          exception = e;
          

          break;
        }
        i++;
      }
    }
    

































































    if ((exception != null) && (request != null) && (listeners != null)) {
      int lastOffset = j;
      int numFailed = numTiles - (lastOffset - offset);
      


      int i = 0; for (int k = lastOffset; i < numFailed; i++) {
        Integer tileStatus = new Integer(4);
        
        tileStatus.put(tileIndices[(k++)], tileStatus);
      }
      

      Request[] reqs = { request };
      int i = 0; for (int k = lastOffset; i < numFailed; i++) {
        Point p = tileIndices[(k++)];
        Iterator iter = listeners.iterator();
        while (iter.hasNext()) {
          TileComputationListener listener = (TileComputationListener)iter.next();
          
          listener.tileComputationFailure(this, reqs, owner, x, y, exception);
        }
      }
    }
    





























    return exception;
  }
  






















  public Raster scheduleTile(OpImage owner, int tileX, int tileY)
  {
    if (owner == null) {
      throw new IllegalArgumentException(JaiI18N.getString("SunTileScheduler1"));
    }
    

    Raster tile = null;
    

    Object tileID = tileKey(owner, tileX, tileY);
    

    boolean computeTile = false;
    Object[] cache = null;
    synchronized (tilesInProgress) {
      if ((computeTile = !tilesInProgress.containsKey(tileID) ? 1 : 0) != 0)
      {
        tilesInProgress.put(tileID, cache = new Object[1]);
      }
      else {
        cache = (Object[])tilesInProgress.get(tileID);
      }
    }
    
    if (computeTile)
    {















      try
      {














        synchronized (cache) { TileCache tileCache;
          String message;
          cache[0] = (tile != null ? tile : new Object());
          

          cache.notifyAll();
          
          synchronized (tilesInProgress)
          {
            tilesInProgress.remove(tileID);
          }
        }
      }
      catch (OutOfMemoryError e) {}finally
      {
        jsr 6;
      }
      








      ret;
    }
    else {
      synchronized (cache)
      {

        if (cache[0] == null) {
          try
          {
            cache.wait();
          }
          catch (Exception e) {}
        }
        


        if ((cache[0] instanceof Raster)) {
          tile = (Raster)cache[0];
        } else {
          throw new RuntimeException(JaiI18N.getString("SunTileScheduler5"));
        }
      }
    }
    
    return tile;
  }
  

























  private Object scheduleJob(PlanarImage owner, Point[] tileIndices, boolean isBlocking, boolean isPrefetch, TileComputationListener[] listeners)
  {
    if ((owner == null) || (tileIndices == null))
    {
      throw new IllegalArgumentException(); }
    if (((isBlocking) || (isPrefetch)) && (listeners != null))
    {
      throw new IllegalArgumentException(); }
    if ((isBlocking) && (isPrefetch)) {
      throw new IllegalArgumentException();
    }
    
    int numTiles = tileIndices.length;
    Raster[] tiles = new Raster[numTiles];
    Object returnValue = tiles;
    
    int numThreads = 0;
    Job[] jobs = null;
    int numJobs = 0;
    label586:
    synchronized (getWorkers(isPrefetch)) {
      numThreads = getNumThreads(isPrefetch);
      
      if (numThreads > 0) {
        if ((numTiles <= numThreads) || ((!isBlocking) && (!isPrefetch)))
        {

          jobs = new Job[numTiles];
          
          if ((!isBlocking) && (!isPrefetch)) {
            Request request = new Request(this, owner, tileIndices, listeners);
            


            returnValue = request;
            

            while (numJobs < numTiles) {
              Point p = tileIndices[numJobs];
              
              Object tileID = tileKey(owner, x, y);
              
              synchronized (tileRequests) {
                List reqList = null;
                if (tileRequests.containsKey(tileID))
                {

                  reqList = (List)tileRequests.get(tileID);
                  reqList.add(request);
                  numTiles--;
                }
                else {
                  reqList = new ArrayList();
                  reqList.add(request);
                  tileRequests.put(tileID, reqList);
                  
                  jobs[numJobs] = new RequestJob(this, owner, x, y, tiles, numJobs);
                  



                  tileJobs.put(tileID, jobs[numJobs]);
                  
                  addJob(jobs[(numJobs++)], false);
                }
              }
            }
            break label586; } }
        while (numJobs < numTiles) {
          jobs[numJobs] = new TileJob(this, isBlocking, owner, tileIndices, tiles, numJobs, 1);
          





          addJob(jobs[(numJobs++)], isPrefetch); continue;
          




          float frac = 1.0F / (2.0F * numThreads);
          



          int minTilesPerThread = numThreads == 1 ? numTiles : Math.min(Math.max(1, (int)(frac * numTiles / 2.0F + 0.5F)), numTiles);
          









          int maxNumJobs = numThreads == 1 ? 1 : (int)(numTiles / minTilesPerThread + 0.5F);
          
          jobs = new TileJob[maxNumJobs];
          

          int numTilesQueued = 0;
          int numTilesLeft = numTiles - numTilesQueued;
          



          while (numTilesLeft > 0)
          {

            int numTilesInThread = (int)(frac * numTilesLeft + 0.5F);
            


            if (numTilesInThread < minTilesPerThread) {
              numTilesInThread = minTilesPerThread;
            }
            

            if (numTilesInThread > numTilesLeft) {
              numTilesInThread = numTilesLeft;
            }
            



            numTilesLeft -= numTilesInThread;
            


            if (numTilesLeft < minTilesPerThread) {
              numTilesInThread += numTilesLeft;
              numTilesLeft = 0;
            }
            

            jobs[numJobs] = new TileJob(this, isBlocking, owner, tileIndices, tiles, numTilesQueued, numTilesInThread);
            







            addJob(jobs[(numJobs++)], isPrefetch);
            

            numTilesQueued += numTilesInThread;
          }
        }
      }
    }
    
    if (numThreads != 0)
    {


      if (isBlocking) {
        LinkedList jobQueue = getQueue(isPrefetch);
        
        for (int i = 0; i < numJobs; i++) {
          synchronized (this) {
            while (jobs[i].notDone()) {
              try {
                wait();
              }
              catch (InterruptedException ie) {}
            }
          }
          



          Exception e = jobs[i].getException();
          
          if (e != null)
          {

            String message = JaiI18N.getString("SunTileScheduler7");
            sendExceptionToListener(message, new ImagingException(message, e));
          }
          
        }
        
      }
      
    }
    else
    {
      Request request = null;
      if ((!isBlocking) && (!isPrefetch)) {
        request = new Request(this, owner, tileIndices, listeners);
        returnValue = request;
      }
      

      Exception e = compute(owner, tileIndices, tiles, 0, numTiles, request);
      



      if (e != null) {
        String message = JaiI18N.getString("SunTileScheduler7");
        sendExceptionToListener(message, new ImagingException(message, e));
      }
    }
    





    return returnValue;
  }
  








  public Raster[] scheduleTiles(OpImage owner, Point[] tileIndices)
  {
    if ((owner == null) || (tileIndices == null)) {
      throw new IllegalArgumentException(JaiI18N.getString("SunTileScheduler0"));
    }
    return (Raster[])scheduleJob(owner, tileIndices, true, false, null);
  }
  








  public TileRequest scheduleTiles(PlanarImage target, Point[] tileIndices, TileComputationListener[] tileListeners)
  {
    if ((target == null) || (tileIndices == null)) {
      throw new IllegalArgumentException(JaiI18N.getString("SunTileScheduler4"));
    }
    return (TileRequest)scheduleJob(target, tileIndices, false, false, tileListeners);
  }
  



















  public void cancelTiles(TileRequest request, Point[] tileIndices)
  {
    if (request == null) {
      throw new IllegalArgumentException(JaiI18N.getString("SunTileScheduler3"));
    }
    
    Request req = (Request)request;
    synchronized (tileRequests)
    {
      List reqIndexList = indices;
      
      Point[] indices;
      Point[] indices;
      if ((tileIndices != null) && (tileIndices.length > 0))
      {
        List tileIndexList = Arrays.asList(tileIndices);
        

        tileIndexList.retainAll(reqIndexList);
        
        indices = (Point[])tileIndexList.toArray(new Point[0]);
      } else {
        indices = (Point[])reqIndexList.toArray(new Point[0]);
      }
      

      int numTiles = indices.length;
      

      Integer tileStatus = new Integer(3);
      

      for (int i = 0; i < numTiles; i++) {
        Point p = indices[i];
        

        Object tileID = tileKey(image, x, y);
        

        List reqList = (List)tileRequests.get(tileID);
        

        if (reqList != null)
        {



          reqList.remove(req);
          


          if (reqList.isEmpty()) {
            synchronized (queue) {
              Object job = tileJobs.remove(tileID);
              if (job != null) {
                queue.remove(job);
              }
            }
            tileRequests.remove(tileID);
          }
          

          tileStatus.put(p, tileStatus);
          

          if (listeners != null) {
            TileRequest[] reqArray = { req };
            Iterator iter = listeners.iterator();
            while (iter.hasNext()) {
              TileComputationListener listener = (TileComputationListener)iter.next();
              
              listener.tileCancelled(this, reqArray, image, x, y);
            }
          }
        }
      }
    }
  }
  






  public void prefetchTiles(PlanarImage owner, Point[] tileIndices)
  {
    if ((owner == null) || (tileIndices == null)) {
      throw new IllegalArgumentException(JaiI18N.getString("SunTileScheduler0"));
    }
    scheduleJob(owner, tileIndices, false, true, null);
  }
  





















  public void setParallelism(int parallelism)
  {
    if (parallelism < 0) {
      throw new IllegalArgumentException(JaiI18N.getString("SunTileScheduler2"));
    }
    this.parallelism = parallelism;
  }
  


  public int getParallelism()
  {
    return parallelism;
  }
  



  public void setPrefetchParallelism(int parallelism)
  {
    if (parallelism < 0) {
      throw new IllegalArgumentException(JaiI18N.getString("SunTileScheduler2"));
    }
    prefetchParallelism = parallelism;
  }
  



  public int getPrefetchParallelism()
  {
    return prefetchParallelism;
  }
  













  public void setPriority(int priority)
  {
    this.priority = Math.max(Math.min(priority, 10), 1);
  }
  



  public int getPriority()
  {
    return priority;
  }
  








  public void setPrefetchPriority(int priority)
  {
    prefetchPriority = Math.max(Math.min(priority, 10), 1);
  }
  




  public int getPrefetchPriority()
  {
    return prefetchPriority;
  }
  







  private void createThreadGroup(boolean isPrefetch)
  {
    if ((rootGroup == null) || (rootGroup.isDestroyed())) {
      rootGroup = new ThreadGroup(nameOfThisInstance);
      rootGroup.setDaemon(true);
    }
    
    if ((isPrefetch) && ((prefetchGroup == null) || (prefetchGroup.isDestroyed())))
    {
      prefetchGroup = new ThreadGroup(rootGroup, nameOfThisInstance + "Prefetch");
      
      prefetchGroup.setDaemon(true);
    }
    
    if ((!isPrefetch) && ((standardGroup == null) || (standardGroup.isDestroyed())))
    {
      standardGroup = new ThreadGroup(rootGroup, nameOfThisInstance + "Standard");
      
      standardGroup.setDaemon(true);
    }
    
    Vector thr = getWorkers(isPrefetch);
    int size = thr.size();
    
    for (int i = size - 1; i >= 0; i--) {
      Thread t = (Thread)thr.get(i);
      if (!t.isAlive()) {
        thr.remove(t);
      }
    }
    if (isPrefetch) {
      numPrefetchThreads = thr.size();
    } else {
      numWorkerThreads = thr.size();
    }
  }
  






  private int getNumThreads(boolean isPrefetch)
  {
    createThreadGroup(isPrefetch);
    

    Vector thr = getWorkers(isPrefetch);
    
    int prty;
    int nthr;
    int prll;
    int prty;
    if (isPrefetch) {
      int nthr = numPrefetchThreads;
      int prll = prefetchParallelism;
      prty = prefetchPriority;
    } else {
      nthr = numWorkerThreads;
      prll = parallelism;
      prty = priority;
    }
    

    if ((nthr > 0) && (((Thread)thr.get(0)).getPriority() != prty))
    {
      int size = thr.size();
      for (int i = 0; i < size; i++) {
        Thread t = (Thread)thr.get(i);
        if ((t != null) && (t.getThreadGroup() != null)) {
          t.setPriority(prty);
        }
      }
    }
    
    if (nthr < prll)
    {

      while (nthr < prll) {
        Thread t = new WorkerThread(isPrefetch ? prefetchGroup : standardGroup, this, isPrefetch);
        


        t.setPriority(prty);
        thr.add(t);
        nthr++;
      }
    }
    


    while (nthr > prll) {
      addJob(WorkerThread.TERMINATE, isPrefetch);
      nthr--;
    }
    


    if (isPrefetch) {
      numPrefetchThreads = nthr;
    } else {
      numWorkerThreads = nthr;
    }
    
    return nthr;
  }
  
  Vector getWorkers(boolean isPrefetch)
  {
    return isPrefetch ? workers : prefetchWorkers;
  }
  
  LinkedList getQueue(boolean isPrefetch)
  {
    return isPrefetch ? prefetchQueue : queue;
  }
  
  private void addJob(Object job, boolean isPrefetch)
  {
    if ((job == null) || ((job != WorkerThread.TERMINATE) && (!(job instanceof Job))))
    {

      throw new IllegalArgumentException();
    }
    

    synchronized (jobQueue = getQueue(isPrefetch)) { LinkedList jobQueue;
      if ((isPrefetch) || (jobQueue.isEmpty()) || ((job instanceof RequestJob)))
      {


        jobQueue.addLast(job);
      }
      else
      {
        boolean inserted = false;
        for (int idx = jobQueue.size() - 1; idx >= 0; idx--) {
          if ((jobQueue.get(idx) instanceof TileJob)) {
            jobQueue.add(idx + 1, job);
            inserted = true;
            break;
          }
        }
        if (!inserted) {
          jobQueue.addFirst(job);
        }
      }
      jobQueue.notify();
    }
  }
  
  protected void finalize() throws Throwable
  {
    terminateAll(false);
    terminateAll(true);
    super.finalize();
  }
  
  private void terminateAll(boolean isPrefetch)
  {
    synchronized (getWorkers(isPrefetch)) {
      int numThreads = isPrefetch ? numPrefetchThreads : numWorkerThreads;
      
      for (int i = 0; i < numThreads; i++) {
        addJob(WorkerThread.TERMINATE, isPrefetch);
        if (isPrefetch) {
          numPrefetchThreads -= 1;
        } else {
          numWorkerThreads -= 1;
        }
      }
    }
  }
  
  void sendExceptionToListener(String message, Throwable e) {
    ImagingListener listener = ImageUtil.getImagingListener((RenderingHints)null);
    
    listener.errorOccurred(message, e, this, false);
  }
}
