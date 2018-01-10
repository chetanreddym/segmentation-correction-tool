package javax.mail.internet;

import java.util.Enumeration;
import java.util.Hashtable;


















public class ParameterList
{
  private Hashtable list = new Hashtable();
  






  public ParameterList() {}
  






  public ParameterList(String paramString)
    throws ParseException
  {
    HeaderTokenizer localHeaderTokenizer = new HeaderTokenizer(paramString, "()<>@,;:\\\"\t []/?=");
    


    for (;;)
    {
      HeaderTokenizer.Token localToken = localHeaderTokenizer.next();
      int i = localToken.getType();
      
      if (i == -4) {
        return;
      }
      if ((char)i != ';')
        break;
      localToken = localHeaderTokenizer.next();
      
      if (localToken.getType() != -1)
        throw new ParseException();
      String str = localToken.getValue().toLowerCase();
      

      localToken = localHeaderTokenizer.next();
      if ((char)localToken.getType() != '=') {
        throw new ParseException();
      }
      
      localToken = localHeaderTokenizer.next();
      i = localToken.getType();
      
      if ((i != -1) && 
        (i != -2)) {
        throw new ParseException();
      }
      list.put(str, localToken.getValue());
    }
    throw new ParseException();
  }
  





  public int size()
  {
    return list.size();
  }
  








  public String get(String paramString)
  {
    return (String)list.get(paramString.trim().toLowerCase());
  }
  






  public void set(String paramString1, String paramString2)
  {
    list.put(paramString1.trim().toLowerCase(), paramString2);
  }
  





  public void remove(String paramString)
  {
    list.remove(paramString.trim().toLowerCase());
  }
  





  public Enumeration getNames()
  {
    return list.keys();
  }
  





  public String toString()
  {
    return toString(0);
  }
  













  public String toString(int paramInt)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    Enumeration localEnumeration = list.keys();
    
    while (localEnumeration.hasMoreElements()) {
      String str1 = (String)localEnumeration.nextElement();
      String str2 = quote((String)list.get(str1));
      localStringBuffer.append("; ");
      paramInt += 2;
      int i = str1.length() + str2.length() + 1;
      if (paramInt + i > 76) {
        localStringBuffer.append("\r\n\t");
        paramInt = 8;
      }
      localStringBuffer.append(str1).append('=').append(str2);
    }
    
    return localStringBuffer.toString();
  }
  
  private String quote(String paramString)
  {
    return MimeUtility.quote(paramString, "()<>@,;:\\\"\t []/?=");
  }
}
