package javax.activation;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Enumeration;
import java.util.Vector;





class SecuritySupport12
  extends SecuritySupport
{
  SecuritySupport12() {}
  
  public ClassLoader getContextClassLoader()
  {
    (ClassLoader)
      AccessController.doPrivileged(new PrivilegedAction() {
        public Object run() {
          ClassLoader localClassLoader = null;
          try {
            localClassLoader = Thread.currentThread().getContextClassLoader();
          } catch (SecurityException localSecurityException) {}
          return localClassLoader;
        }
      });
  }
  
  public InputStream getResourceAsStream(Class paramClass, String paramString) throws IOException
  {
    try {
      (InputStream)
        AccessController.doPrivileged(new PrivilegedExceptionAction() { private final String val$name;
          
          public Object run() throws IOException { return getResourceAsStream(val$name); }
        });
    }
    catch (PrivilegedActionException localPrivilegedActionException) {
      throw ((IOException)localPrivilegedActionException.getException());
    }
  }
  
  public URL[] getResources(ClassLoader paramClassLoader, String paramString) {
    (URL[])
      AccessController.doPrivileged(new PrivilegedAction() { private final String val$name;
        
        public Object run() { URL[] arrayOfURL = null;
          try {
            Vector localVector = new Vector();
            Enumeration localEnumeration = getResources(val$name);
            while ((localEnumeration != null) && (localEnumeration.hasMoreElements())) {
              URL localURL = (URL)localEnumeration.nextElement();
              if (localURL != null)
                localVector.addElement(localURL);
            }
            if (localVector.size() > 0) {
              arrayOfURL = new URL[localVector.size()];
              localVector.copyInto(arrayOfURL);
            }
          }
          catch (IOException localIOException) {}catch (SecurityException localSecurityException) {}
          return arrayOfURL;
        }
      });
  }
  
  public URL[] getSystemResources(String paramString) {
    (URL[])
      AccessController.doPrivileged(new PrivilegedAction() {
        public Object run() {
          URL[] arrayOfURL = null;
          try {
            Vector localVector = new Vector();
            Enumeration localEnumeration = ClassLoader.getSystemResources(SecuritySupport12.this);
            while ((localEnumeration != null) && (localEnumeration.hasMoreElements())) {
              URL localURL = (URL)localEnumeration.nextElement();
              if (localURL != null)
                localVector.addElement(localURL);
            }
            if (localVector.size() > 0) {
              arrayOfURL = new URL[localVector.size()];
              localVector.copyInto(arrayOfURL);
            }
          }
          catch (IOException localIOException) {}catch (SecurityException localSecurityException) {}
          return arrayOfURL;
        }
      });
  }
  
  public InputStream openStream(URL paramURL) throws IOException {
    try {
      (InputStream)
        AccessController.doPrivileged(new PrivilegedExceptionAction() {
          public Object run() throws IOException {
            return openStream();
          }
        });
    } catch (PrivilegedActionException localPrivilegedActionException) {
      throw ((IOException)localPrivilegedActionException.getException());
    }
  }
}
