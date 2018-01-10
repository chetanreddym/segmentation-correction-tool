package com.sun.media.jai.codecimpl.util;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.PrivilegedActionException;
























public class ImagingException
  extends RuntimeException
{
  private Throwable cause = null;
  




  public ImagingException() {}
  



  public ImagingException(String message)
  {
    super(message);
  }
  





  public ImagingException(Throwable cause)
  {
    this.cause = cause;
  }
  






  public ImagingException(String message, Throwable cause)
  {
    super(message);
    this.cause = cause;
  }
  


  public Throwable getCause()
  {
    return cause;
  }
  



  public Throwable getRootCause()
  {
    Throwable rootCause = cause;
    Throwable atop = this;
    
    while ((rootCause != atop) && (rootCause != null)) {
      try {
        atop = rootCause;
        Method getCause = rootCause.getClass().getMethod("getCause", null);
        rootCause = (Throwable)getCause.invoke(rootCause, null);
        









        if (rootCause == null) {
          rootCause = atop;
        }
      }
      catch (Exception e)
      {
        if ((rootCause instanceof InvocationTargetException)) {
          rootCause = ((InvocationTargetException)rootCause).getTargetException();
        } else if ((rootCause instanceof PrivilegedActionException))
          rootCause = ((PrivilegedActionException)rootCause).getException(); else {
          rootCause = atop;
        }
        if (rootCause == null) {
          rootCause = atop;
        }
      }
      finally
      {
        if (rootCause == null) {
          rootCause = atop;
        }
      }
    }
    return rootCause;
  }
  






  public void printStackTrace()
  {
    printStackTrace(System.err);
  }
  







  public void printStackTrace(PrintStream s)
  {
    synchronized (s) {
      super.printStackTrace(s);
      boolean is14 = false;
      try {
        String version = System.getProperty("java.version");
        is14 = version.indexOf("1.4") >= 0;
      }
      catch (Exception e) {}
      
      if ((!is14) && (cause != null)) {
        s.println("Caused by:");
        cause.printStackTrace(s);
      }
    }
  }
  








  public void printStackTrace(PrintWriter s)
  {
    synchronized (s) {
      super.printStackTrace(s);
      boolean is14 = false;
      try {
        String version = System.getProperty("java.version");
        is14 = version.indexOf("1.4") >= 0;
      }
      catch (Exception e) {}
      
      if ((!is14) && (cause != null)) {
        s.println("Caused by:");
        cause.printStackTrace(s);
      }
    }
  }
}
