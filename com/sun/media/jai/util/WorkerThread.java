package com.sun.media.jai.util;

import java.util.LinkedList;
import java.util.Vector;

















































































































































































































































































































































































































class WorkerThread
  extends Thread
{
  public static final Object TERMINATE = new Object();
  

  SunTileScheduler scheduler;
  

  boolean isPrefetch;
  


  public WorkerThread(ThreadGroup group, SunTileScheduler scheduler, boolean isPrefetch)
  {
    super(group, group.getName() + group.activeCount());
    this.scheduler = scheduler;
    this.isPrefetch = isPrefetch;
    
    setDaemon(true);
    start();
  }
  
  public void run()
  {
    LinkedList jobQueue = scheduler.getQueue(isPrefetch);
    for (;;)
    {
      Object dequeuedObject = null;
      

      synchronized (jobQueue) {
        if (jobQueue.size() > 0)
        {
          dequeuedObject = jobQueue.removeFirst();
        } else {
          try
          {
            jobQueue.wait();
          }
          catch (InterruptedException ie) {}
        }
      }
      


      if ((dequeuedObject == TERMINATE) || (getThreadGroup() == null) || (getThreadGroup().isDestroyed()))
      {


        synchronized (threads = scheduler.getWorkers(isPrefetch)) { Vector threads;
          threads.remove(this);
        }
        

        return;
      }
      
      Job job = (Job)dequeuedObject;
      

      if (job != null) {
        job.compute();
        

        if (job.isBlocking()) {
          synchronized (scheduler) {
            scheduler.notify();
          }
        }
      }
    }
  }
}
