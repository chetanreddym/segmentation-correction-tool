package com.sun.media.jai.codecimpl;

import com.sun.media.jai.codecimpl.util.ImagingException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;










public class ImagingListenerProxy
{
  public ImagingListenerProxy() {}
  
  public static synchronized boolean errorOccurred(String message, Throwable thrown, Object where, boolean isRetryable)
    throws RuntimeException
  {
    Method errorOccurred = null;
    Object listener = null;
    try
    {
      Class jaiClass = Class.forName("javax.media.jai.JAI");
      if (jaiClass == null) {
        return defaultImpl(message, thrown, where, isRetryable);
      }
      Method jaiInstance = jaiClass.getMethod("getDefaultInstance", null);
      
      Method getListener = jaiClass.getMethod("getImagingListener", null);
      

      Object jai = jaiInstance.invoke(null, null);
      if (jai == null) {
        return defaultImpl(message, thrown, where, isRetryable);
      }
      listener = getListener.invoke(jai, null);
      Class listenerClass = listener.getClass();
      
      errorOccurred = listenerClass.getMethod("errorOccurred", new Class[] { String.class, Throwable.class, Object.class, Boolean.TYPE });


    }
    catch (Throwable e)
    {

      return defaultImpl(message, thrown, where, isRetryable);
    }
    try
    {
      Boolean result = (Boolean)errorOccurred.invoke(listener, new Object[] { message, thrown, where, new Boolean(isRetryable) });
      



      return result.booleanValue();
    } catch (InvocationTargetException e) {
      Throwable te = e.getTargetException();
      throw new ImagingException(te);
    } catch (Throwable e) {}
    return defaultImpl(message, thrown, where, isRetryable);
  }
  





  private static synchronized boolean defaultImpl(String message, Throwable thrown, Object where, boolean isRetryable)
    throws RuntimeException
  {
    if ((thrown instanceof RuntimeException)) {
      throw ((RuntimeException)thrown);
    }
    System.err.println("Error: " + message);
    System.err.println("Occurs in: " + ((where instanceof Class) ? ((Class)where).getName() : where.getClass().getName()));
    


    thrown.printStackTrace(System.err);
    return false;
  }
}
