package com.sun.media.jai.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;






































































































public final class Service
{
  private static final String prefix = "META-INF/services/";
  
  private Service() {}
  
  private static void fail(Class service, String msg)
    throws ServiceConfigurationError
  {
    throw new ServiceConfigurationError(service.getName() + ": " + msg);
  }
  
  private static void fail(Class service, URL u, int line, String msg)
    throws ServiceConfigurationError
  {
    fail(service, u + ":" + line + ": " + msg);
  }
  






  private static int parseLine(Class service, URL u, BufferedReader r, int lc, List names, Set returned)
    throws IOException, ServiceConfigurationError
  {
    String ln = r.readLine();
    if (ln == null) {
      return -1;
    }
    int ci = ln.indexOf('#');
    if (ci >= 0) ln = ln.substring(0, ci);
    ln = ln.trim();
    int n = ln.length();
    if (n != 0) {
      if ((ln.indexOf(' ') >= 0) || (ln.indexOf('\t') >= 0))
        fail(service, u, lc, "Illegal configuration-file syntax");
      if (!Character.isJavaIdentifierStart(ln.charAt(0)))
        fail(service, u, lc, "Illegal provider-class name: " + ln);
      for (int i = 1; i < n; i++) {
        char c = ln.charAt(i);
        if ((!Character.isJavaIdentifierPart(c)) && (c != '.'))
          fail(service, u, lc, "Illegal provider-class name: " + ln);
      }
      if (!returned.contains(ln)) {
        names.add(ln);
        returned.add(ln);
      }
    }
    return lc + 1;
  }
  























  private static Iterator parse(Class service, URL u, Set returned)
    throws ServiceConfigurationError
  {
    InputStream in = null;
    BufferedReader r = null;
    ArrayList names = new ArrayList();
    try {
      in = u.openStream();
      r = new BufferedReader(new InputStreamReader(in, "utf-8"));
      int lc = 1;
      while ((lc = parseLine(service, u, r, lc, names, returned)) >= 0) {}
    } catch (IOException x) {
      fail(service, ": " + x);
    } finally {
      try {
        if (r != null) r.close();
        if (in != null) in.close();
      } catch (IOException y) {
        fail(service, ": " + y);
      }
    }
    return names.iterator();
  }
  
  private static class LazyIterator implements Iterator {
    Class service;
    ClassLoader loader;
    
    LazyIterator(Class x0, ClassLoader x1, Service.1 x2) { this(x0, x1); }
    


    Enumeration configs = null;
    Iterator pending = null;
    Set returned = new TreeSet();
    String nextName = null;
    
    private LazyIterator(Class service, ClassLoader loader) {
      this.service = service;
      this.loader = loader;
    }
    
    public boolean hasNext() throws ServiceConfigurationError {
      if (nextName != null) {
        return true;
      }
      if (configs == null) {
        try {
          String fullName = "META-INF/services/" + service.getName();
          if (loader == null) {
            configs = ClassLoader.getSystemResources(fullName);
          } else
            configs = loader.getResources(fullName);
        } catch (IOException x) {
          Service.fail(service, ": " + x);
        }
      }
      while ((pending == null) || (!pending.hasNext())) {
        if (!configs.hasMoreElements()) {
          return false;
        }
        pending = Service.parse(service, (URL)configs.nextElement(), returned);
      }
      nextName = ((String)pending.next());
      return true;
    }
    
    public Object next() throws ServiceConfigurationError {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      String cn = nextName;
      nextName = null;
      try {
        return Class.forName(cn, true, loader).newInstance();
      } catch (ClassNotFoundException x) {
        Service.fail(service, "Provider " + cn + " not found");
      }
      catch (Exception x) {
        Service.fail(service, "Provider " + cn + " could not be instantiated: " + x);
      }
      
      return null;
    }
    
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }
  








































  public static Iterator providers(Class service, ClassLoader loader)
    throws ServiceConfigurationError
  {
    return new LazyIterator(service, loader, null);
  }
  


























  public static Iterator providers(Class service)
    throws ServiceConfigurationError
  {
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    return providers(service, cl);
  }
  






























  public static Iterator installedProviders(Class service)
    throws ServiceConfigurationError
  {
    ClassLoader cl = ClassLoader.getSystemClassLoader();
    if (cl != null) cl = cl.getParent();
    return providers(service, cl);
  }
}
