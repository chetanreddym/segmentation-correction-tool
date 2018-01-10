package javax.mail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.BitSet;
















































public class URLName
{
  protected String fullURL;
  private String protocol;
  private String username;
  private String password;
  private String host;
  private InetAddress hostAddress;
  private boolean hostAddressKnown = false;
  



  private int port = -1;
  



  private String file;
  



  private String ref;
  



  private int hashCode;
  



  private static boolean doEncode = true;
  






  static BitSet dontNeedEncoding;
  





  static final int caseDiff = 32;
  






  public URLName(String paramString1, String paramString2, int paramInt, String paramString3, String paramString4, String paramString5)
  {
    protocol = paramString1;
    host = paramString2;
    port = paramInt;
    int i;
    if ((paramString3 != null) && ((i = paramString3.indexOf('#')) != -1)) {
      file = paramString3.substring(0, i);
      ref = paramString3.substring(i + 1);
    } else {
      file = paramString3;
      ref = null;
    }
    username = (doEncode ? encode(paramString4) : paramString4);
    password = (doEncode ? encode(paramString5) : paramString5);
  }
  


  public URLName(URL paramURL)
  {
    this(paramURL.toString());
  }
  



  public URLName(String paramString)
  {
    parseString(paramString);
  }
  


  public String toString()
  {
    if (fullURL == null)
    {
      StringBuffer localStringBuffer = new StringBuffer();
      if (protocol != null) {
        localStringBuffer.append(protocol);
        localStringBuffer.append(":");
      }
      
      if ((username != null) || (host != null))
      {
        localStringBuffer.append("//");
        


        if (username != null) {
          localStringBuffer.append(username);
          
          if (password != null) {
            localStringBuffer.append(":");
            localStringBuffer.append(password);
          }
          
          localStringBuffer.append("@");
        }
        

        if (host != null) {
          localStringBuffer.append(host);
        }
        

        if (port != -1) {
          localStringBuffer.append(":");
          localStringBuffer.append(Integer.toString(port));
        }
        if (file != null) {
          localStringBuffer.append("/");
        }
      }
      
      if (file != null) {
        localStringBuffer.append(file);
      }
      

      if (ref != null) {
        localStringBuffer.append("#");
        localStringBuffer.append(ref);
      }
      

      fullURL = localStringBuffer.toString();
    }
    
    return fullURL;
  }
  




  protected void parseString(String paramString)
  {
    protocol = (this.file = this.ref = this.host = this.username = this.password = null);
    port = -1;
    
    int i = paramString.length();
    



    int j = paramString.indexOf(':');
    if (j != -1) {
      protocol = paramString.substring(0, j);
    }
    
    if (paramString.regionMatches(j + 1, "//", 0, 2))
    {
      String str1 = null;
      int m = paramString.indexOf('/', j + 3);
      if (m != -1) {
        str1 = paramString.substring(j + 3, m);
        if (m + 1 < i) {
          file = paramString.substring(m + 1);
        } else
          file = "";
      } else {
        str1 = paramString.substring(j + 3);
      }
      
      int n = str1.indexOf('@');
      if (n != -1) {
        String str2 = str1.substring(0, n);
        str1 = str1.substring(n + 1);
        

        int i2 = str2.indexOf(':');
        if (i2 != -1) {
          username = str2.substring(0, i2);
          password = str2.substring(i2 + 1);
        } else {
          username = str2;
        }
      }
      
      int i1;
      
      if ((str1.length() > 0) && (str1.charAt(0) == '['))
      {
        i1 = str1.indexOf(':', str1.indexOf(93));
      } else {
        i1 = str1.indexOf(':');
      }
      if (i1 != -1) {
        String str3 = str1.substring(i1 + 1);
        if (str3.length() > 0) {
          try {
            port = Integer.parseInt(str3);
          } catch (NumberFormatException localNumberFormatException) {
            port = -1;
          }
        }
        
        host = str1.substring(0, i1);
      } else {
        host = str1;
      }
    }
    else if (j + 1 < i) {
      file = paramString.substring(j + 1);
    }
    
    int k;
    
    if ((file != null) && ((k = file.indexOf('#')) != -1)) {
      ref = file.substring(k + 1);
      file = file.substring(0, k);
    }
  }
  



  public int getPort()
  {
    return port;
  }
  



  public String getProtocol()
  {
    return protocol;
  }
  



  public String getFile()
  {
    return file;
  }
  



  public String getRef()
  {
    return ref;
  }
  



  public String getHost()
  {
    return host;
  }
  



  public String getUsername()
  {
    if (doEncode) return decode(username); return username;
  }
  



  public String getPassword()
  {
    if (doEncode) return decode(password); return password;
  }
  

  public URL getURL()
    throws MalformedURLException
  {
    return new URL(getProtocol(), getHost(), getPort(), getFile());
  }
  









  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof URLName))
      return false;
    URLName localURLName = (URLName)paramObject;
    

    if ((protocol == null) || (!protocol.equals(protocol))) {
      return false;
    }
    
    InetAddress localInetAddress1 = getHostAddress();InetAddress localInetAddress2 = localURLName.getHostAddress();
    
    if ((localInetAddress1 != null) && (localInetAddress2 != null)) {
      if (!localInetAddress1.equals(localInetAddress2)) {
        return false;
      }
    } else if ((host != null) && (host != null)) {
      if (!host.equalsIgnoreCase(host)) {
        return false;
      }
    } else if (host != host) {
      return false;
    }
    


    if ((username != username) && (
      (username == null) || (!username.equals(username)))) {
      return false;
    }
    



    String str1 = file == null ? "" : file;
    String str2 = file == null ? "" : file;
    
    if (!str1.equals(str2)) {
      return false;
    }
    
    if (port != port) {
      return false;
    }
    
    return true;
  }
  


  public int hashCode()
  {
    if (hashCode != 0)
      return hashCode;
    if (protocol != null)
      hashCode += protocol.hashCode();
    InetAddress localInetAddress = getHostAddress();
    if (localInetAddress != null) {
      hashCode += localInetAddress.hashCode();
    } else if (host != null)
      hashCode += host.toLowerCase().hashCode();
    if (username != null)
      hashCode += username.hashCode();
    if (file != null)
      hashCode += file.hashCode();
    hashCode += port;
    return hashCode;
  }
  




  private synchronized InetAddress getHostAddress()
  {
    if (hostAddressKnown)
      return hostAddress;
    if (host == null)
      return null;
    try {
      hostAddress = InetAddress.getByName(host);
    } catch (UnknownHostException localUnknownHostException) {
      hostAddress = null;
    }
    hostAddressKnown = true;
    return hostAddress;
  }
  
  static
  {
    try
    {
      doEncode = !Boolean.getBoolean("mail.URLName.dontencode");
    }
    catch (Throwable localThrowable) {}
    





























































































































































































































































































































































































    dontNeedEncoding = new BitSet(256);
    
    for (int i = 97; i <= 122; i++) {
      dontNeedEncoding.set(i);
    }
    for (i = 65; i <= 90; i++) {
      dontNeedEncoding.set(i);
    }
    for (i = 48; i <= 57; i++) {
      dontNeedEncoding.set(i);
    }
    
    dontNeedEncoding.set(32);
    dontNeedEncoding.set(45);
    dontNeedEncoding.set(95);
    dontNeedEncoding.set(46);
    dontNeedEncoding.set(42);
  }
  





  static String encode(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    for (int i = 0; i < paramString.length(); i++) {
      int j = paramString.charAt(i);
      if ((j == 32) || (!dontNeedEncoding.get(j)))
        return _encode(paramString);
    }
    return paramString;
  }
  
  private static String _encode(String paramString) {
    int i = 10;
    StringBuffer localStringBuffer = new StringBuffer(paramString.length());
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(i);
    OutputStreamWriter localOutputStreamWriter = new OutputStreamWriter(localByteArrayOutputStream);
    
    for (int j = 0; j < paramString.length(); j++) {
      int k = paramString.charAt(j);
      if (dontNeedEncoding.get(k)) {
        if (k == 32) {
          k = 43;
        }
        localStringBuffer.append((char)k);
      }
      else {
        try {
          localOutputStreamWriter.write(k);
          localOutputStreamWriter.flush();
        } catch (IOException localIOException) {
          localByteArrayOutputStream.reset();
          continue;
        }
        byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
        for (int m = 0; m < arrayOfByte.length; m++) {
          localStringBuffer.append('%');
          char c = Character.forDigit(arrayOfByte[m] >> 4 & 0xF, 16);
          

          if (Character.isLetter(c)) {
            c = (char)(c - ' ');
          }
          localStringBuffer.append(c);
          c = Character.forDigit(arrayOfByte[m] & 0xF, 16);
          if (Character.isLetter(c)) {
            c = (char)(c - ' ');
          }
          localStringBuffer.append(c);
        }
        localByteArrayOutputStream.reset();
      }
    }
    
    return localStringBuffer.toString();
  }
  






























  static String decode(String paramString)
  {
    if (paramString == null)
      return null;
    if (indexOfAny(paramString, "+%") == -1) {
      return paramString;
    }
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 0; i < paramString.length(); i++) {
      char c = paramString.charAt(i);
      switch (c) {
      case '+': 
        localStringBuffer.append(' ');
        break;
      case '%': 
        try {
          localStringBuffer.append((char)Integer.parseInt(
            paramString.substring(i + 1, i + 3), 16));
        } catch (NumberFormatException localNumberFormatException) {
          throw new IllegalArgumentException();
        }
        i += 2;
        break;
      default: 
        localStringBuffer.append(c);
      }
      
    }
    
    String str = localStringBuffer.toString();
    try {
      byte[] arrayOfByte = str.getBytes("8859_1");
      str = new String(arrayOfByte);
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException) {}
    
    return str;
  }
  





  private static int indexOfAny(String paramString1, String paramString2)
  {
    return indexOfAny(paramString1, paramString2, 0);
  }
  
  private static int indexOfAny(String paramString1, String paramString2, int paramInt) {
    try {
      int i = paramString1.length();
      for (int j = paramInt; j < i; j++) {
        if (paramString2.indexOf(paramString1.charAt(j)) >= 0)
          return j;
      }
      return -1;
    } catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException) {}
    return -1;
  }
}
