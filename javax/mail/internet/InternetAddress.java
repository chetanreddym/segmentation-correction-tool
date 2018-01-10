package javax.mail.internet;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.mail.Address;
import javax.mail.Session;






































public class InternetAddress
  extends Address
  implements Cloneable
{
  protected String address;
  protected String personal;
  protected String encodedPersonal;
  
  public InternetAddress() {}
  
  public InternetAddress(String paramString)
    throws AddressException
  {
    InternetAddress[] arrayOfInternetAddress = parse(paramString, true);
    
    if (arrayOfInternetAddress.length != 1) {
      throw new AddressException("Illegal address", paramString);
    }
    





    address = 0address;
    personal = 0personal;
    encodedPersonal = 0encodedPersonal;
  }
  









  private InternetAddress(String paramString, boolean paramBoolean)
    throws AddressException
  {
    this(paramString);
    if (paramBoolean) {
      checkAddress(address, true, true);
    }
  }
  





  public InternetAddress(String paramString1, String paramString2)
    throws UnsupportedEncodingException
  {
    this(paramString1, paramString2, null);
  }
  







  public InternetAddress(String paramString1, String paramString2, String paramString3)
    throws UnsupportedEncodingException
  {
    address = paramString1;
    setPersonal(paramString2, paramString3);
  }
  



  public Object clone()
  {
    InternetAddress localInternetAddress = null;
    try {
      localInternetAddress = (InternetAddress)super.clone();
    } catch (CloneNotSupportedException localCloneNotSupportedException) {}
    return localInternetAddress;
  }
  



  public String getType()
  {
    return "rfc822";
  }
  




  public void setAddress(String paramString)
  {
    address = paramString;
  }
  












  public void setPersonal(String paramString1, String paramString2)
    throws UnsupportedEncodingException
  {
    personal = paramString1;
    if (paramString1 != null) {
      encodedPersonal = MimeUtility.encodeWord(paramString1, paramString2, null);
    } else {
      encodedPersonal = null;
    }
  }
  









  public void setPersonal(String paramString)
    throws UnsupportedEncodingException
  {
    personal = paramString;
    if (paramString != null) {
      encodedPersonal = MimeUtility.encodeWord(paramString);
    } else {
      encodedPersonal = null;
    }
  }
  


  public String getAddress()
  {
    return address;
  }
  






  public String getPersonal()
  {
    if (personal != null) {
      return personal;
    }
    if (encodedPersonal != null) {
      try {
        personal = MimeUtility.decodeText(encodedPersonal);
        return personal;

      }
      catch (Exception localException)
      {
        return encodedPersonal;
      }
    }
    
    return null;
  }
  






  public String toString()
  {
    if ((encodedPersonal == null) && (personal != null)) {
      try {
        encodedPersonal = MimeUtility.encodeWord(personal);
      } catch (UnsupportedEncodingException localUnsupportedEncodingException) {}
    }
    if (encodedPersonal != null)
      return quotePhrase(encodedPersonal) + " <" + address + ">";
    if ((isGroup()) || (isSimple())) {
      return address;
    }
    return "<" + address + ">";
  }
  






  public String toUnicodeString()
  {
    if (getPersonal() != null)
      return quotePhrase(personal) + " <" + address + ">";
    if ((isGroup()) || (isSimple())) {
      return address;
    }
    return "<" + address + ">";
  }
  

















  private static final String rfc822phrase = "()<>@,;:\\\"\t .[]".replace(' ', '\000').replace('\t', '\000');
  private static final String specialsNoDotNoAt = "()<>,;:\\\"[]";
  
  private static String quotePhrase(String paramString) { int i = paramString.length();
    int j = 0;
    
    for (int k = 0; k < i; k++) {
      int m = paramString.charAt(k);
      if ((m == 34) || (m == 92))
      {
        StringBuffer localStringBuffer2 = new StringBuffer(i + 3);
        localStringBuffer2.append('"');
        for (int n = 0; n < i; n++) {
          char c = paramString.charAt(n);
          if ((c == '"') || (c == '\\'))
          {
            localStringBuffer2.append('\\'); }
          localStringBuffer2.append(c);
        }
        localStringBuffer2.append('"');
        return localStringBuffer2.toString(); }
      if (((m < 32) && (m != 13) && (m != 10) && (m != 9)) || 
        (m >= 127) || (rfc822phrase.indexOf(m) >= 0))
      {
        j = 1;
      }
    }
    if (j != 0) {
      StringBuffer localStringBuffer1 = new StringBuffer(i + 2);
      localStringBuffer1.append('"').append(paramString).append('"');
      return localStringBuffer1.toString();
    }
    return paramString;
  }
  
  private static String unquote(String paramString) {
    if ((paramString.startsWith("\"")) && (paramString.endsWith("\""))) {
      paramString = paramString.substring(1, paramString.length() - 1);
      
      if (paramString.indexOf('\\') >= 0) {
        StringBuffer localStringBuffer = new StringBuffer(paramString.length());
        for (int i = 0; i < paramString.length(); i++) {
          char c = paramString.charAt(i);
          if ((c == '\\') && (i < paramString.length() - 1))
            c = paramString.charAt(++i);
          localStringBuffer.append(c);
        }
        paramString = localStringBuffer.toString();
      }
    }
    return paramString;
  }
  


  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof InternetAddress)) {
      return false;
    }
    String str = ((InternetAddress)paramObject).getAddress();
    if (str == address)
      return true;
    if ((address != null) && (address.equalsIgnoreCase(str))) {
      return true;
    }
    return false;
  }
  


  public int hashCode()
  {
    if (address == null) {
      return 0;
    }
    return address.toLowerCase().hashCode();
  }
  











  public static String toString(Address[] paramArrayOfAddress)
  {
    return toString(paramArrayOfAddress, 0);
  }
  



















  public static String toString(Address[] paramArrayOfAddress, int paramInt)
  {
    if ((paramArrayOfAddress == null) || (paramArrayOfAddress.length == 0)) {
      return null;
    }
    StringBuffer localStringBuffer = new StringBuffer();
    
    for (int i = 0; i < paramArrayOfAddress.length; i++) {
      if (i != 0) {
        localStringBuffer.append(", ");
        paramInt += 2;
      }
      
      String str = paramArrayOfAddress[i].toString();
      int j = lengthOfFirstSegment(str);
      if (paramInt + j > 76) {
        localStringBuffer.append("\r\n\t");
        paramInt = 8;
      }
      localStringBuffer.append(str);
      paramInt = lengthOfLastSegment(str, paramInt);
    }
    
    return localStringBuffer.toString();
  }
  

  private static int lengthOfFirstSegment(String paramString)
  {
    int i;
    
    if ((i = paramString.indexOf("\r\n")) != -1) {
      return i;
    }
    return paramString.length();
  }
  


  private static int lengthOfLastSegment(String paramString, int paramInt)
  {
    int i;
    

    if ((i = paramString.lastIndexOf("\r\n")) != -1) {
      return paramString.length() - i - 2;
    }
    return paramString.length() + paramInt;
  }
  












  public static InternetAddress getLocalAddress(Session paramSession)
  {
    String str1 = null;String str2 = null;String str3 = null;
    try {
      if (paramSession == null) {
        str1 = System.getProperty("user.name");
        str2 = InetAddress.getLocalHost().getHostName();
      } else {
        str3 = paramSession.getProperty("mail.from");
        if (str3 == null) {
          str1 = paramSession.getProperty("mail.user");
          if ((str1 == null) || (str1.length() == 0))
            str1 = paramSession.getProperty("user.name");
          if ((str1 == null) || (str1.length() == 0))
            str1 = System.getProperty("user.name");
          str2 = paramSession.getProperty("mail.host");
          if ((str2 == null) || (str2.length() == 0)) {
            InetAddress localInetAddress = InetAddress.getLocalHost();
            if (localInetAddress != null) {
              str2 = localInetAddress.getHostName();
            }
          }
        }
      }
      if ((str3 == null) && (str1 != null) && (str1.length() != 0) && 
        (str2 != null) && (str2.length() != 0)) {
        str3 = str1 + "@" + str2;
      }
      if (str3 != null) {
        return new InternetAddress(str3);
      }
    }
    catch (SecurityException localSecurityException) {}catch (AddressException localAddressException) {}catch (UnknownHostException localUnknownHostException) {}
    return null;
  }
  







  public static InternetAddress[] parse(String paramString)
    throws AddressException
  {
    return parse(paramString, true);
  }
  













  private static final String specialsNoDot = "()<>,;:\\\"[]@";
  











  public static InternetAddress[] parse(String paramString, boolean paramBoolean)
    throws AddressException
  {
    int n = -1;int i1 = -1;
    int i2 = paramString.length();
    int i3 = 0;
    boolean bool = false;
    int i4 = 0;
    
    Vector localVector = new Vector();
    
    int j;
    int i = j = -1; label459: Object localObject2; InternetAddress localInternetAddress; Object localObject3; for (int k = 0; k < i2; k++) {
      int i5 = paramString.charAt(k);
      
      switch (i5)
      {

      case 40: 
        i4 = 1;
        if ((i >= 0) && (j == -1))
          j = k;
        if (n == -1)
          n = k + 1;
        k++; for (int m = 1; (k < i2) && (m > 0); 
            k++) {
          i5 = paramString.charAt(k);
          switch (i5) {
          case 92: 
            k++;
            break;
          case 40: 
            m++;
            break;
          case 41: 
            m--;
          }
          
        }
        

        if (m > 0)
          throw new AddressException("Missing ')'", paramString, k);
        k--;
        if (i1 == -1)
          i1 = k;
        break;
      
      case 41: 
        throw new AddressException("Missing '('", paramString, k);
      
      case 60: 
        i4 = 1;
        if (bool)
          throw new AddressException("Extra route-addr", paramString, k);
        if (i3 == 0) {
          n = i;
          if (n >= 0)
            i1 = k;
          i = k + 1;
        }
        
        int i6 = 0;
        
        for (k++; k < i2; k++) {
          i5 = paramString.charAt(k);
          switch (i5) {
          case 92: 
            k++;
            break;
          case 34: 
            i6 = i6 != 0 ? 0 : 1;
            break;
          case 62: 
            if (i6 == 0) {
              break label459;
            }
          }
          
        }
        
        if (k >= i2) {
          if (i6 != 0) {
            throw new AddressException("Missing '\"'", paramString, k);
          }
          throw new AddressException("Missing '>'", paramString, k);
        }
        bool = true;
        j = k;
        break;
      case 62: 
        throw new AddressException("Missing '<'", paramString, k);
      
      case 34: 
        i4 = 1;
        if (i == -1) {
          i = k;
        }
        for (k++; k < i2; k++) {
          i5 = paramString.charAt(k);
          switch (i5) {
          case 92: 
            k++;
          }
          
        }
        



        if (k >= i2) {
          throw new AddressException("Missing '\"'", paramString, k);
        }
        break;
      case 91: 
        i4 = 1;
        
        for (k++; k < i2; k++) {
          i5 = paramString.charAt(k);
          switch (i5) {
          case 92: 
            k++;
          }
          
        }
        



        if (k >= i2) {
          throw new AddressException("Missing ']'", paramString, k);
        }
        break;
      case 44: 
        if (i == -1) {
          bool = false;
          i4 = 0;
          i = j = -1;

        }
        else if (i3 == 0)
        {

          if (j == -1)
            j = k;
          localObject2 = paramString.substring(i, j).trim();
          if ((i4 != 0) || (paramBoolean)) {
            checkAddress((String)localObject2, bool, paramBoolean);
            localInternetAddress = new InternetAddress();
            localInternetAddress.setAddress((String)localObject2);
            if (n >= 0) {
              encodedPersonal = unquote(
                paramString.substring(n, i1).trim());
              n = i1 = -1;
            }
            localVector.addElement(localInternetAddress);
          }
          else {
            localObject3 = new StringTokenizer((String)localObject2);
            while (((StringTokenizer)localObject3).hasMoreTokens()) {
              String str = ((StringTokenizer)localObject3).nextToken();
              checkAddress(str, false, paramBoolean);
              localInternetAddress = new InternetAddress();
              localInternetAddress.setAddress(str);
              localVector.addElement(localInternetAddress);
            }
          }
          
          bool = false;
          i4 = 0;
          i = j = -1; }
        break;
      
      case 58: 
        i4 = 1;
        if (i3 != 0)
          throw new AddressException("Nested group", paramString, k);
        i3 = 1;
        break;
      
      case 59: 
        if (i3 == 0)
          throw new AddressException(
            "Illegal semicolon, not in group", paramString, k);
        i3 = 0;
        localInternetAddress = new InternetAddress();
        j = k + 1;
        localInternetAddress.setAddress(paramString.substring(i, j).trim());
        localVector.addElement(localInternetAddress);
        
        bool = false;
        i = j = -1;
        break;
      







      default: 
        if (i == -1)
          i = k;
        break;
      }
      
    }
    if (i >= 0)
    {




      if (j == -1)
        j = k;
      localObject1 = paramString.substring(i, j).trim();
      if ((i4 != 0) || (paramBoolean)) {
        checkAddress((String)localObject1, bool, paramBoolean);
        localInternetAddress = new InternetAddress();
        localInternetAddress.setAddress((String)localObject1);
        if (n >= 0) {
          encodedPersonal = unquote(
            paramString.substring(n, i1).trim());
        }
        localVector.addElement(localInternetAddress);
      }
      else {
        localObject2 = new StringTokenizer((String)localObject1);
        while (((StringTokenizer)localObject2).hasMoreTokens()) {
          localObject3 = ((StringTokenizer)localObject2).nextToken();
          checkAddress((String)localObject3, false, paramBoolean);
          localInternetAddress = new InternetAddress();
          localInternetAddress.setAddress((String)localObject3);
          localVector.addElement(localInternetAddress);
        }
      }
    }
    
    Object localObject1 = new InternetAddress[localVector.size()];
    localVector.copyInto((Object[])localObject1);
    return localObject1;
  }
  










  private static void checkAddress(String paramString, boolean paramBoolean1, boolean paramBoolean2)
    throws AddressException
  {
    int j = 0;
    if (paramString.indexOf('"') >= 0) return;
    int i;
    if ((!paramBoolean2) || (paramBoolean1))
    {



      for (j = 0; (i = indexOfAny(paramString, ",:", j)) >= 0; 
          j = i + 1) {
        if (paramString.charAt(j) != '@')
          throw new AddressException("Illegal route-addr", paramString);
        if (paramString.charAt(i) == ':')
        {
          j = i + 1;
          break;
        }
      }
    }
    

    String str1;
    
    String str2;
    
    if ((i = paramString.indexOf('@', j)) >= 0) {
      if (i == j)
        throw new AddressException("Missing local name", paramString);
      if (i == paramString.length() - 1)
        throw new AddressException("Missing domain", paramString);
      str1 = paramString.substring(j, i);
      str2 = paramString.substring(i + 1);

    }
    else
    {

      str1 = paramString;
      str2 = null;
    }
    








    if (indexOfAny(paramString, " \t\n\r") >= 0) {
      throw new AddressException("Illegal whitespace in address", paramString);
    }
    if (indexOfAny(str1, "()<>,;:\\\"[]@") >= 0) {
      throw new AddressException("Illegal character in local name", paramString);
    }
    if ((str2 != null) && (str2.indexOf('[') < 0) && 
      (indexOfAny(str2, "()<>,;:\\\"[]@") >= 0)) {
      throw new AddressException("Illegal character in domain", paramString);
    }
  }
  



  private boolean isSimple()
  {
    return indexOfAny(address, "()<>,;:\\\"[]") < 0;
  }
  



  private boolean isGroup()
  {
    return (address.endsWith(";")) && (address.indexOf(':') > 0);
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
