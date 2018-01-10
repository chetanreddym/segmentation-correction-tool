package com.sun.media.jai.codec;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;



































































































































































































































































































class TempFileCleanupThread
  extends Thread
{
  private HashSet tempFiles = null;
  
  TempFileCleanupThread()
  {
    setPriority(1);
  }
  


  public void run()
  {
    if ((tempFiles != null) && (tempFiles.size() > 0)) {
      Iterator fileIter = tempFiles.iterator();
      while (fileIter.hasNext()) {
        try {
          File file = (File)fileIter.next();
          file.delete();
        }
        catch (Exception e) {}
      }
    }
  }
  



  synchronized void addFile(File file)
  {
    if (tempFiles == null) {
      tempFiles = new HashSet();
    }
    tempFiles.add(file);
  }
  


  synchronized void removeFile(File file)
  {
    tempFiles.remove(file);
  }
}
