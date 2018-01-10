package com.ibm.icu.impl;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;




























public abstract class ICUNotifier
{
  private final Object notifyLock = new Object();
  
  private NotifyThread notifyThread;
  
  private List listeners;
  

  public ICUNotifier() {}
  

  public void addListener(EventListener l)
  {
    if (l == null) {
      throw new NullPointerException();
    }
    
    if (acceptsListener(l)) {
      synchronized (notifyLock) {
        if (listeners == null) {
          listeners = new ArrayList(5);
        }
        else {
          Iterator iter = listeners.iterator();
          while (iter.hasNext()) {
            if (iter.next() == l) {
              return;
            }
          }
        }
        
        listeners.add(l);
      }
    }
    throw new InternalError("Listener invalid for this notifier.");
  }
  





  public void removeListener(EventListener l)
  {
    if (l == null) {
      throw new NullPointerException();
    }
    synchronized (notifyLock) {
      if (listeners != null)
      {
        Iterator iter = listeners.iterator();
        while (iter.hasNext()) {
          if (iter.next() == l) {
            iter.remove();
            if (listeners.size() == 0) {
              listeners = null;
            }
            return;
          }
        }
      }
    }
  }
  




  public void notifyChanged()
  {
    if (listeners != null)
      synchronized (notifyLock) {
        if (listeners != null) {
          if (notifyThread == null) {
            notifyThread = new NotifyThread(this);
            notifyThread.setDaemon(true);
            notifyThread.start();
          }
          notifyThread.queue(listeners.toArray());
        }
      }
  }
  
  protected abstract boolean acceptsListener(EventListener paramEventListener);
  
  protected abstract void notifyListener(EventListener paramEventListener);
  
  private static class NotifyThread extends Thread {
    private final ICUNotifier notifier;
    private final List queue = new LinkedList();
    
    NotifyThread(ICUNotifier notifier) {
      this.notifier = notifier;
    }
    


    public void queue(Object[] list)
    {
      synchronized (this) {
        queue.add(list);
        notify();
      }
    }
    

    public void run()
    {
      for (;;)
      {
        try
        {
          Object[] list;
          synchronized (this)
          {
            continue;wait();
            if (queue.isEmpty()) {
              continue;
            }
            list = (Object[])queue.remove(0);
          }
          
          int i = 0; continue;
          notifier.notifyListener((EventListener)list[i]);i++; if (i < list.length) {}
        }
        catch (InterruptedException e) {}
      }
    }
  }
}
