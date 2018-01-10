package javax.mail;

import com.sun.mail.util.LineInputStream;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;



















public final class Session
{
  private Properties props;
  private Authenticator authenticator;
  private Hashtable authTable = new Hashtable();
  private boolean debug = false;
  private Vector providers = new Vector();
  private Hashtable providersByProtocol = new Hashtable();
  private Hashtable providersByClassName = new Hashtable();
  private Properties addressMap = new Properties();
  private static Method getResources = null;
  private static Method getSystemResources = null;
  
  static {
    try {
      Class localClass = ClassLoader.class;
      
      getResources = localClass.getMethod("getResources", 
        new Class[] { String.class });
      getSystemResources = localClass.getMethod("getSystemResources", 
        new Class[] { String.class });
    }
    catch (Throwable localThrowable) {}
  }
  
  private static Session defaultSession = null;
  
  private Session(Properties paramProperties, Authenticator paramAuthenticator)
  {
    props = paramProperties;
    authenticator = paramAuthenticator;
    
    if (Boolean.valueOf(paramProperties.getProperty("mail.debug")).booleanValue()) {
      debug = true;
    }
    
    Class localClass;
    if (paramAuthenticator != null) {
      localClass = paramAuthenticator.getClass();
    } else {
      localClass = getClass();
    }
    loadProviders(localClass);
    loadAddressMap(localClass);
  }
  
















  public static Session getInstance(Properties paramProperties, Authenticator paramAuthenticator)
  {
    return new Session(paramProperties, paramAuthenticator);
  }
  












  public static Session getInstance(Properties paramProperties)
  {
    return new Session(paramProperties, null);
  }
  





































  public static Session getDefaultInstance(Properties paramProperties, Authenticator paramAuthenticator)
  {
    if (defaultSession == null) {
      defaultSession = new Session(paramProperties, paramAuthenticator);

    }
    else if (defaultSessionauthenticator != paramAuthenticator)
    {
      if ((defaultSessionauthenticator == null) || 
        (paramAuthenticator == null) || 
        (defaultSessionauthenticator.getClass().getClassLoader() != 
        paramAuthenticator.getClass().getClassLoader()))
      {


        throw new SecurityException("Access to default session denied");
      }
    }
    return defaultSession;
  }
  




















  public static Session getDefaultInstance(Properties paramProperties)
  {
    return getDefaultInstance(paramProperties, null);
  }
  














  public void setDebug(boolean paramBoolean)
  {
    debug = paramBoolean;
  }
  




  public boolean getDebug()
  {
    return debug;
  }
  






  public Provider[] getProviders()
  {
    Provider[] arrayOfProvider = new Provider[providers.size()];
    providers.copyInto(arrayOfProvider);
    return arrayOfProvider;
  }
  














  public Provider getProvider(String paramString)
    throws NoSuchProviderException
  {
    if ((paramString == null) || (paramString.length() <= 0)) {
      throw new NoSuchProviderException("Invalid protocol: null");
    }
    
    Provider localProvider = null;
    

    String str = props.getProperty("mail." + paramString + ".class");
    if (str != null) {
      if (debug) {
        System.out.println("DEBUG: mail." + paramString + 
          ".class property exists and points to " + 
          str);
      }
      localProvider = (Provider)providersByClassName.get(str);
    }
    
    if (localProvider != null) {
      return localProvider;
    }
    
    localProvider = (Provider)providersByProtocol.get(paramString);
    

    if (localProvider == null) {
      throw new NoSuchProviderException("No provider for " + paramString);
    }
    if (debug) {
      System.out.println("\nDEBUG: getProvider() returning " + 
        localProvider.toString());
    }
    return localProvider;
  }
  








  public void setProvider(Provider paramProvider)
    throws NoSuchProviderException
  {
    if (paramProvider == null) {
      throw new NoSuchProviderException("Can't set null provider");
    }
    providersByProtocol.put(paramProvider.getProtocol(), paramProvider);
    props.put("mail." + paramProvider.getProtocol() + ".class", 
      paramProvider.getClassName());
  }
  









  public Store getStore()
    throws NoSuchProviderException
  {
    return getStore(getProperty("mail.store.protocol"));
  }
  








  public Store getStore(String paramString)
    throws NoSuchProviderException
  {
    return getStore(new URLName(paramString, null, -1, null, null, null));
  }
  













  public Store getStore(URLName paramURLName)
    throws NoSuchProviderException
  {
    String str = paramURLName.getProtocol();
    Provider localProvider = getProvider(str);
    return getStore(localProvider, paramURLName);
  }
  







  public Store getStore(Provider paramProvider)
    throws NoSuchProviderException
  {
    return getStore(paramProvider, null);
  }
  














  private Store getStore(Provider paramProvider, URLName paramURLName)
    throws NoSuchProviderException
  {
    if ((paramProvider == null) || (paramProvider.getType() != Provider.Type.STORE)) {
      throw new NoSuchProviderException("invalid provider");
    }
    try
    {
      return (Store)getService(paramProvider, paramURLName);
    } catch (ClassCastException localClassCastException) {
      throw new NoSuchProviderException("incorrect class");
    }
  }
  
























  public Folder getFolder(URLName paramURLName)
    throws MessagingException
  {
    Store localStore = getStore(paramURLName);
    localStore.connect();
    return localStore.getFolder(paramURLName);
  }
  







  public Transport getTransport()
    throws NoSuchProviderException
  {
    return getTransport(getProperty("mail.transport.protocol"));
  }
  








  public Transport getTransport(String paramString)
    throws NoSuchProviderException
  {
    return getTransport(new URLName(paramString, null, -1, null, null, null));
  }
  












  public Transport getTransport(URLName paramURLName)
    throws NoSuchProviderException
  {
    String str = paramURLName.getProtocol();
    Provider localProvider = getProvider(str);
    return getTransport(localProvider, paramURLName);
  }
  








  public Transport getTransport(Provider paramProvider)
    throws NoSuchProviderException
  {
    return getTransport(paramProvider, null);
  }
  










  public Transport getTransport(Address paramAddress)
    throws NoSuchProviderException
  {
    String str = (String)addressMap.get(paramAddress.getType());
    if (str == null) {
      throw new NoSuchProviderException("No provider for Address type: " + 
        paramAddress.getType());
    }
    return getTransport(str);
  }
  











  private Transport getTransport(Provider paramProvider, URLName paramURLName)
    throws NoSuchProviderException
  {
    if ((paramProvider == null) || (paramProvider.getType() != Provider.Type.TRANSPORT)) {
      throw new NoSuchProviderException("invalid provider");
    }
    try
    {
      return (Transport)getService(paramProvider, paramURLName);
    } catch (ClassCastException localClassCastException) {
      throw new NoSuchProviderException("incorrect class");
    }
  }
  












  private Object getService(Provider paramProvider, URLName paramURLName)
    throws NoSuchProviderException
  {
    if (paramProvider == null) {
      throw new NoSuchProviderException("null");
    }
    

    if (paramURLName == null) {
      paramURLName = new URLName(paramProvider.getProtocol(), null, -1, 
        null, null, null);
    }
    
    Object localObject = null;
    
    ClassLoader localClassLoader;
    
    if (authenticator != null) {
      localClassLoader = authenticator.getClass().getClassLoader();
    } else {
      localClassLoader = getClass().getClassLoader();
    }
    
    Class localClass = null;
    

    try
    {
      localClass = localClassLoader.loadClass(paramProvider.getClassName());
    }
    catch (Exception localException3)
    {
      try
      {
        localClass = Class.forName(paramProvider.getClassName());
      }
      catch (Exception localException1) {
        if (debug) localException1.printStackTrace();
        throw new NoSuchProviderException(paramProvider.getProtocol());
      }
    }
    
    try
    {
      Class[] arrayOfClass = { Session.class, URLName.class };
      Constructor localConstructor = localClass.getConstructor(arrayOfClass);
      
      Object[] arrayOfObject = { this, paramURLName };
      localObject = localConstructor.newInstance(arrayOfObject);
    }
    catch (Exception localException2) {
      if (debug) localException2.printStackTrace();
      throw new NoSuchProviderException(paramProvider.getProtocol());
    }
    
    return localObject;
  }
  








  public void setPasswordAuthentication(URLName paramURLName, PasswordAuthentication paramPasswordAuthentication)
  {
    if (paramPasswordAuthentication == null) {
      authTable.remove(paramURLName);
    } else {
      authTable.put(paramURLName, paramPasswordAuthentication);
    }
  }
  




  public PasswordAuthentication getPasswordAuthentication(URLName paramURLName)
  {
    return (PasswordAuthentication)authTable.get(paramURLName);
  }
  





















  public PasswordAuthentication requestPasswordAuthentication(InetAddress paramInetAddress, int paramInt, String paramString1, String paramString2, String paramString3)
  {
    if (authenticator != null) {
      return authenticator.requestPasswordAuthentication(
        paramInetAddress, paramInt, paramString1, paramString2, paramString3);
    }
    return null;
  }
  





  public Properties getProperties()
  {
    return props;
  }
  





  public String getProperty(String paramString)
  {
    return props.getProperty(paramString);
  }
  

  private void loadProviders(Class paramClass)
  {
    BufferedInputStream localBufferedInputStream = null;
    try {
      String str1 = System.getProperty("java.home") + 
        File.separator + "lib" + 
        File.separator + "javamail.providers";
      localBufferedInputStream = 
        new BufferedInputStream(new FileInputStream(str1));
      if (localBufferedInputStream != null) {
        loadProvidersFromStream(localBufferedInputStream);
        localBufferedInputStream.close();
        if (debug) {
          pr("DEBUG: loaded providers in <java.home>/lib");
        }
      } else if (debug) {
        pr("DEBUG: not loading system providers in <java.home>/lib");
      }
    } catch (FileNotFoundException localFileNotFoundException) {
      if (debug)
        pr("DEBUG: not loading system providers in <java.home>/lib");
    } catch (IOException localIOException1) {
      if (debug)
        pr("DEBUG: " + localIOException1.getMessage());
    } catch (SecurityException localSecurityException) {
      if (debug) {
        pr("DEBUG: not loading system providers in <java.home>/lib");
      }
    }
    


    InputStream localInputStream1 = null;
    String str2 = "META-INF/javamail.providers";
    String str3 = "/" + str2;
    int i = 0;
    
    if (getResources != null) {
      try
      {
        localObject = paramClass.getClassLoader();
        Enumeration localEnumeration; if (localObject != null) {
          localEnumeration = (Enumeration)getResources.invoke(
            localObject, new String[] { str2 });
        } else
          localEnumeration = (Enumeration)getSystemResources.invoke(
            localObject, new String[] { str2 });
        while (localEnumeration.hasMoreElements()) {
          URL localURL = (URL)localEnumeration.nextElement();
          localInputStream1 = localURL.openStream();
          if (localInputStream1 != null) {
            try {
              loadProvidersFromStream(localInputStream1);
              i = 1;
              localInputStream1.close();
              if (!debug) continue;
              pr(
              
                "DEBUG: successfully loaded optional custom providers from URL: " + localURL);
            } catch (IOException localIOException4) {
              if (!debug) continue; }
            pr("DEBUG: " + localIOException4.getMessage());

          }
          else if (debug) {
            pr(
              "DEBUG: not loading optional custom providers from URL: " + localURL);
          }
        }
      } catch (Exception localException) {
        if (debug) {
          pr("DEBUG: " + localException);
        }
      }
    }
    
    if (i == 0) {
      localInputStream1 = paramClass.getResourceAsStream(str3);
      if (localInputStream1 != null) {
        try {
          loadProvidersFromStream(localInputStream1);
          localInputStream1.close();
          if (!debug) break label544;
          pr(
            "DEBUG: successfully loaded optional custom providers: " + str3);
        } catch (IOException localIOException2) {
          if (!debug) break label544; }
        pr("DEBUG: " + localIOException2);

      }
      else if (debug) {
        pr(
          "DEBUG: not loading optional custom providers file: " + str3);
      }
    }
    
    label544:
    InputStream localInputStream2 = null;
    Object localObject = "/META-INF/javamail.default.providers";
    localInputStream2 = paramClass.getResourceAsStream((String)localObject);
    if (localInputStream2 != null) {
      try {
        loadProvidersFromStream(localInputStream2);
        localInputStream2.close();
        if (!debug) break label652;
        pr("DEBUG: successfully loaded default providers");
      } catch (IOException localIOException3) {
        if (!debug) break label652; }
      pr("DEBUG: " + localIOException3.getMessage());

    }
    else if (debug) {
      pr("DEBUG: can't load default providers file" + (String)localObject);
    }
    
    label652:
    if (debug) {
      System.out.println("\nDEBUG: Tables of loaded providers");
      


      pr("DEBUG: Providers Listed By Class Name: " + 
        providersByClassName.toString());
      


      pr("DEBUG: Providers Listed By Protocol: " + 
        providersByProtocol.toString());
    }
  }
  
  private void loadProvidersFromStream(InputStream paramInputStream) throws IOException
  {
    if (paramInputStream != null) {
      LineInputStream localLineInputStream = new LineInputStream(paramInputStream);
      
      String str1;
      
      while ((str1 = localLineInputStream.readLine()) != null)
      {
        if (!str1.startsWith("#"))
        {
          Provider.Type localType = null;
          String str2 = null;String str3 = null;
          String str4 = null;String str5 = null;
          

          StringTokenizer localStringTokenizer = new StringTokenizer(str1, ";");
          Object localObject; while (localStringTokenizer.hasMoreTokens()) {
            localObject = localStringTokenizer.nextToken().trim();
            

            int i = ((String)localObject).indexOf("=");
            if (((String)localObject).startsWith("protocol=")) {
              str2 = ((String)localObject).substring(i + 1);
            } else if (((String)localObject).startsWith("type=")) {
              String str6 = ((String)localObject).substring(i + 1);
              if (str6.equalsIgnoreCase("store")) {
                localType = Provider.Type.STORE;
              } else if (str6.equalsIgnoreCase("transport")) {
                localType = Provider.Type.TRANSPORT;
              }
            } else if (((String)localObject).startsWith("class=")) {
              str3 = ((String)localObject).substring(i + 1);
            } else if (((String)localObject).startsWith("vendor=")) {
              str4 = ((String)localObject).substring(i + 1);
            } else if (((String)localObject).startsWith("version=")) {
              str5 = ((String)localObject).substring(i + 1);
            }
          }
          

          if ((localType == null) || (str2 == null) || (str3 == null) || 
            (str2.length() <= 0) || (str3.length() <= 0))
          {
            if (debug) {
              System.out.println("DEBUG: Bad provider entry: " + 
                str1);
            }
          } else {
            localObject = new Provider(localType, str2, str3, 
              str4, str5);
            

            providers.addElement(localObject);
            providersByClassName.put(str3, localObject);
            if (!providersByProtocol.containsKey(str2)) {
              providersByProtocol.put(str2, localObject);
            }
          }
        }
      }
    }
  }
  


  private void loadAddressMap(Class paramClass)
  {
    InputStream localInputStream1 = null;
    String str1 = "/META-INF/javamail.default.address.map";
    localInputStream1 = paramClass.getResourceAsStream(str1);
    if (localInputStream1 != null) {
      try {
        addressMap.load(localInputStream1);
        localInputStream1.close();
      }
      catch (IOException localIOException3) {}catch (SecurityException localSecurityException1) {}
    }
    



    InputStream localInputStream2 = null;
    String str2 = "META-INF/javamail.address.map";
    String str3 = "/" + str2;
    int i = 0;
    Object localObject;
    if (getResources != null) {
      try
      {
        localObject = paramClass.getClassLoader();
        Enumeration localEnumeration; if (localObject != null) {
          localEnumeration = (Enumeration)getResources.invoke(
            localObject, new String[] { str2 });
        } else
          localEnumeration = (Enumeration)getSystemResources.invoke(
            localObject, new String[] { str2 });
        while (localEnumeration.hasMoreElements()) {
          URL localURL = (URL)localEnumeration.nextElement();
          localInputStream2 = localURL.openStream();
          if (localInputStream2 != null) {
            try {
              addressMap.load(localInputStream2);
              i = 1;
              localInputStream2.close();
              if (!debug) continue;
              pr(
                "DEBUG: successfully loaded optional address map from URL: " + localURL);
            } catch (IOException localIOException2) {
              if (!debug) continue; }
            pr("DEBUG: " + localIOException2.getMessage());

          }
          else if (debug) {
            pr(
              "DEBUG: not loading optional address map from URL: " + localURL);
          }
        }
      } catch (Exception localException) {
        if (debug) {
          pr("DEBUG: " + localException);
        }
      }
    }
    
    if (i == 0) {
      localInputStream2 = paramClass.getResourceAsStream(str3);
      if (localInputStream2 != null) {
        try {
          addressMap.load(localInputStream2);
          localInputStream2.close();
          if (!debug) break label433;
          pr(
            "DEBUG: successfully loaded optional address map: " + str3);
        } catch (IOException localIOException1) {
          if (!debug) break label433; }
        pr("DEBUG: " + localIOException1);

      }
      else if (debug) {
        pr(
          "DEBUG: not loading optional address map file: " + str3);
      }
    }
    

    label433:
    
    BufferedInputStream localBufferedInputStream = null;
    try {
      localObject = 
      
        System.getProperty("java.home") + File.separator + "lib" + File.separator + "javamail.address.map";
      localBufferedInputStream = 
        new BufferedInputStream(new FileInputStream((String)localObject));
    }
    catch (FileNotFoundException localFileNotFoundException) {}catch (SecurityException localSecurityException2) {}
    


    if (localBufferedInputStream != null) {
      try {
        addressMap.load(localBufferedInputStream);
        localBufferedInputStream.close();
      } catch (IOException localIOException4) {}
    }
  }
  
  private static void pr(String paramString) {
    System.out.println(paramString);
  }
}
