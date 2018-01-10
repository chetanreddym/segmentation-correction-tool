package javax.activation;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


















class SecuritySupport
{
  private static final Object securitySupport;
  
  static
  {
    Object localObject1 = null;
    try {
      Class localClass = Class.forName("java.security.AccessController");
      
















      localObject1 = new SecuritySupport12();
    }
    catch (Exception localException) {}finally
    {
      if (localObject1 == null)
        localObject1 = new SecuritySupport();
      securitySupport = localObject1;
    }
  }
  



  public static SecuritySupport getInstance()
  {
    return (SecuritySupport)securitySupport;
  }
  
  public ClassLoader getContextClassLoader() {
    return null;
  }
  
  public InputStream getResourceAsStream(Class paramClass, String paramString) throws IOException
  {
    return paramClass.getResourceAsStream(paramString);
  }
  
  public URL[] getResources(ClassLoader paramClassLoader, String paramString) {
    return null;
  }
  
  public URL[] getSystemResources(String paramString) {
    return null;
  }
  
  public InputStream openStream(URL paramURL) throws IOException {
    return paramURL.openStream();
  }
  
  SecuritySupport() {}
}
