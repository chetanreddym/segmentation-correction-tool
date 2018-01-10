package javax.mail.internet;

import javax.mail.Session;


































class UniqueValue
{
  private static int part;
  
  public static String getUniqueBoundaryValue()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    

    localStringBuffer.append("----=_Part_").append(part++).append("_")
      .append(localStringBuffer.hashCode()).append('.')
      .append(System.currentTimeMillis());
    return localStringBuffer.toString();
  }
  













  public static String getUniqueMessageIDValue(Session paramSession)
  {
    String str = null;
    
    InternetAddress localInternetAddress = InternetAddress.getLocalAddress(paramSession);
    if (localInternetAddress != null) {
      str = localInternetAddress.getAddress();
    } else {
      str = "javamailuser@localhost";
    }
    
    StringBuffer localStringBuffer = new StringBuffer();
    

    localStringBuffer.append(localStringBuffer.hashCode()).append('.')
      .append(System.currentTimeMillis()).append('.')
      .append("JavaMail.")
      .append(str);
    return localStringBuffer.toString();
  }
  
  UniqueValue() {}
}
