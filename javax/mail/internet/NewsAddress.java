package javax.mail.internet;

import java.util.StringTokenizer;
import java.util.Vector;
import javax.mail.Address;
























public class NewsAddress
  extends Address
{
  protected String newsgroup;
  protected String host;
  
  public NewsAddress() {}
  
  public NewsAddress(String paramString)
  {
    this(paramString, null);
  }
  





  public NewsAddress(String paramString1, String paramString2)
  {
    newsgroup = paramString1;
    host = paramString2;
  }
  



  public String getType()
  {
    return "news";
  }
  




  public void setNewsgroup(String paramString)
  {
    newsgroup = paramString;
  }
  




  public String getNewsgroup()
  {
    return newsgroup;
  }
  




  public void setHost(String paramString)
  {
    host = paramString;
  }
  




  public String getHost()
  {
    return host;
  }
  




  public String toString()
  {
    return newsgroup;
  }
  


  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof NewsAddress)) {
      return false;
    }
    NewsAddress localNewsAddress = (NewsAddress)paramObject;
    return (newsgroup.equals(newsgroup)) && (
      ((host == null) && (host == null)) || (
      (host != null) && (host != null) && (host.equalsIgnoreCase(host))));
  }
  


  public int hashCode()
  {
    int i = 0;
    if (newsgroup != null)
      i += newsgroup.hashCode();
    if (host != null)
      i += host.toLowerCase().hashCode();
    return i;
  }
  











  public static String toString(Address[] paramArrayOfAddress)
  {
    if ((paramArrayOfAddress == null) || (paramArrayOfAddress.length == 0)) {
      return null;
    }
    StringBuffer localStringBuffer = 
      new StringBuffer(((NewsAddress)paramArrayOfAddress[0]).toString());
    for (int i = 1; i < paramArrayOfAddress.length; i++) {
      localStringBuffer.append(",").append(((NewsAddress)paramArrayOfAddress[i]).toString());
    }
    return localStringBuffer.toString();
  }
  








  public static NewsAddress[] parse(String paramString)
    throws AddressException
  {
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, ",");
    Vector localVector = new Vector();
    while (localStringTokenizer.hasMoreTokens()) {
      String str = localStringTokenizer.nextToken();
      localVector.addElement(new NewsAddress(str));
    }
    int i = localVector.size();
    NewsAddress[] arrayOfNewsAddress = new NewsAddress[i];
    if (i > 0)
      localVector.copyInto(arrayOfNewsAddress);
    return arrayOfNewsAddress;
  }
}
