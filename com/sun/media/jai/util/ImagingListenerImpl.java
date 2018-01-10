package com.sun.media.jai.util;

import java.io.PrintStream;
import java.lang.ref.SoftReference;
import javax.media.jai.OperationRegistry;
import javax.media.jai.util.ImagingListener;























public class ImagingListenerImpl
  implements ImagingListener
{
  private static SoftReference reference = new SoftReference(null);
  



  public static ImagingListenerImpl getInstance()
  {
    synchronized (reference) {
      Object referent = reference.get();
      ImagingListenerImpl listener;
      if (referent == null) {
        ImagingListenerImpl listener;
        reference = new SoftReference(listener = new ImagingListenerImpl());
      }
      else
      {
        listener = (ImagingListenerImpl)referent;
      }
      
      return listener;
    }
  }
  




  private ImagingListenerImpl() {}
  



  public synchronized boolean errorOccurred(String message, Throwable thrown, Object where, boolean isRetryable)
    throws RuntimeException
  {
    if (((thrown instanceof RuntimeException)) && (!(where instanceof OperationRegistry)))
    {
      throw ((RuntimeException)thrown);
    }
    System.err.println("Error: " + message);
    System.err.println("Occurs in: " + ((where instanceof Class) ? ((Class)where).getName() : where.getClass().getName()));
    


    thrown.printStackTrace(System.err);
    return false;
  }
}
