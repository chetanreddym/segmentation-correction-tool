package com.sun.media.jai.util;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.ListIterator;





































public final class RWLock
{
  private boolean allowUpgrades;
  private static int READ = 1;
  private static int WRITE = 2;
  
  private static int NOT_FOUND = -1;
  


  private WaitingList waitingList = new WaitingList(null);
  
  private class WaitingList extends LinkedList { WaitingList(RWLock.1 x1) { this(); }
    

    int indexOfFirstWriter()
    {
      ListIterator iter = listIterator(0);
      int index = 0;
      
      while (iter.hasNext()) {
        if (nextlockType == RWLock.WRITE)
          return index;
        index++;
      }
      
      return RWLock.NOT_FOUND;
    }
    

    int indexOfLastGranted()
    {
      ListIterator iter = listIterator(size());
      int index = size() - 1;
      
      while (iter.hasPrevious()) {
        if (previousgranted == true)
          return index;
        index--;
      }
      
      return RWLock.NOT_FOUND;
    }
    
    int findMe()
    {
      return indexOf(new RWLock.ReaderWriter(RWLock.this));
    }
    
    private WaitingList() {}
  }
  
  private class ReaderWriter {
    Thread key;
    int lockType;
    int lockCount;
    boolean granted;
    
    ReaderWriter() {
      this(0);
    }
    
    ReaderWriter(int type)
    {
      key = Thread.currentThread();
      lockType = type;
      lockCount = 0;
      granted = false;
    }
    




    public boolean equals(Object o)
    {
      return key == key;
    }
  }
  






  public RWLock(boolean allowUpgrades)
  {
    this.allowUpgrades = allowUpgrades;
  }
  



  public RWLock()
  {
    this(true);
  }
  









  public synchronized boolean forReading(int waitTime)
  {
    ReaderWriter element = null;
    


    int index = waitingList.findMe();
    
    if (index != NOT_FOUND) {
      element = (ReaderWriter)waitingList.get(index);
    }
    else {
      element = new ReaderWriter(READ);
      waitingList.add(element);
    }
    



    if (lockCount > 0) {
      lockCount += 1;
      return true;
    }
    
    long startTime = System.currentTimeMillis();
    long endTime = waitTime + startTime;
    do
    {
      int nextWriter = waitingList.indexOfFirstWriter();
      
      index = waitingList.findMe();
      


      if ((nextWriter == NOT_FOUND) || (nextWriter > index)) {
        lockCount += 1;
        granted = true;
        return true;
      }
      




      if (waitTime == 0) {
        waitingList.remove(element);
        return false;
      }
      

      try
      {
        if (waitTime < 0) {
          wait();
        }
        else {
          long delta = endTime - System.currentTimeMillis();
          
          if (delta > 0L) wait(delta);
        }
      }
      catch (InterruptedException e)
      {
        System.err.println(key.getName() + " : interrupted while waiting for a READ lock!");
      }
      
    }
    while ((waitTime < 0) || (endTime > System.currentTimeMillis()));
    

    waitingList.remove(element);
    

    notifyAll();
    

    return false;
  }
  






  public synchronized boolean forReading()
  {
    return forReading(-1);
  }
  
















  public synchronized boolean forWriting(int waitTime)
    throws RWLock.UpgradeNotAllowed
  {
    ReaderWriter element = null;
    


    int index = waitingList.findMe();
    
    if (index != NOT_FOUND) {
      element = (ReaderWriter)waitingList.get(index);
    }
    else {
      element = new ReaderWriter(WRITE);
      waitingList.add(element);
    }
    

    if ((granted == true) && (lockType == READ)) {
      try
      {
        if (!upgrade(waitTime)) {
          return false;
        }
      } catch (LockNotHeld e) {
        return false;
      }
    }
    



    if (lockCount > 0) {
      lockCount += 1;
      return true;
    }
    
    long startTime = System.currentTimeMillis();
    long endTime = waitTime + startTime;
    

    do
    {
      index = waitingList.findMe();
      

      if (index == 0) {
        lockCount += 1;
        granted = true;
        return true;
      }
      




      if (waitTime == 0) {
        waitingList.remove(element);
        return false;
      }
      

      try
      {
        if (waitTime < 0) {
          wait();
        }
        else {
          long delta = endTime - System.currentTimeMillis();
          
          if (delta > 0L) wait(delta);
        }
      }
      catch (InterruptedException e)
      {
        System.err.println(key.getName() + " : interrupted while waiting for a WRITE lock!");
      }
      
    }
    while ((waitTime < 0) || (endTime > System.currentTimeMillis()));
    

    waitingList.remove(element);
    

    notifyAll();
    

    return false;
  }
  









  public synchronized boolean forWriting()
    throws RWLock.UpgradeNotAllowed
  {
    return forWriting(-1);
  }
  


















  public synchronized boolean upgrade(int waitTime)
    throws RWLock.UpgradeNotAllowed, RWLock.LockNotHeld
  {
    if (!allowUpgrades) {
      throw new UpgradeNotAllowed();
    }
    
    int index = waitingList.findMe();
    
    if (index == NOT_FOUND) {
      throw new LockNotHeld();
    }
    

    ReaderWriter element = (ReaderWriter)waitingList.get(index);
    
    if (lockType == WRITE) {
      return true;
    }
    
    int lastGranted = waitingList.indexOfLastGranted();
    


    if (lastGranted == NOT_FOUND) {
      throw new LockNotHeld();
    }
    

    if (index != lastGranted) {
      waitingList.remove(index);
      ListIterator iter = waitingList.listIterator(lastGranted);
      iter.add(element);
    }
    




    lockType = WRITE;
    
    long startTime = System.currentTimeMillis();
    long endTime = waitTime + startTime;
    do
    {
      index = waitingList.findMe();
      
      if (index == 0) {
        return true;
      }
      



      if (waitTime == 0)
      {

        lockType = READ;
        


        return false;
      }
      

      try
      {
        if (waitTime < 0) {
          wait();
        } else {
          long delta = endTime - System.currentTimeMillis();
          
          if (delta > 0L) wait(delta);
        }
      }
      catch (InterruptedException e)
      {
        System.err.println(key.getName() + " : interrupted while waiting to UPGRADE lock!");
      }
      
    }
    while ((waitTime < 0) || (endTime > System.currentTimeMillis()));
    

    lockType = READ;
    


    notifyAll();
    

    return false;
  }
  










  public synchronized boolean upgrade()
    throws RWLock.UpgradeNotAllowed, RWLock.LockNotHeld
  {
    return upgrade(-1);
  }
  












  public synchronized boolean downgrade()
    throws RWLock.LockNotHeld
  {
    int index = waitingList.findMe();
    
    if (index == NOT_FOUND) {
      throw new LockNotHeld();
    }
    
    ReaderWriter e = (ReaderWriter)waitingList.get(index);
    

    if (lockType == WRITE) {
      lockType = READ;
      notifyAll();
    }
    
    return true;
  }
  











  public synchronized void release()
    throws RWLock.LockNotHeld
  {
    int index = waitingList.findMe();
    
    if (index == NOT_FOUND) {
      throw new LockNotHeld();
    }
    
    ReaderWriter e = (ReaderWriter)waitingList.get(index);
    


    if (--lockCount == 0) {
      waitingList.remove(index);
      notifyAll();
    }
  }
  
  public class UpgradeNotAllowed
    extends RuntimeException
  {
    public UpgradeNotAllowed() {}
  }
  
  public class LockNotHeld
    extends RuntimeException
  {
    public LockNotHeld() {}
  }
}
