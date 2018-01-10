package javax.mail.internet;

import com.sun.mail.util.LineInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Vector;
import javax.mail.MessagingException;








































public class InternetHeaders
{
  private Vector headers;
  
  public InternetHeaders()
  {
    headers = new Vector();
    headers.addElement(new hdr("Return-Path", null));
    headers.addElement(new hdr("Received", null));
    headers.addElement(new hdr("Message-Id", null));
    headers.addElement(new hdr("Resent-Date", null));
    headers.addElement(new hdr("Date", null));
    headers.addElement(new hdr("Resent-From", null));
    headers.addElement(new hdr("From", null));
    headers.addElement(new hdr("Reply-To", null));
    headers.addElement(new hdr("To", null));
    headers.addElement(new hdr("Subject", null));
    headers.addElement(new hdr("Cc", null));
    headers.addElement(new hdr("In-Reply-To", null));
    headers.addElement(new hdr("Resent-Message-Id", null));
    headers.addElement(new hdr("Errors-To", null));
    headers.addElement(new hdr("Mime-Version", null));
    headers.addElement(new hdr("Content-Type", null));
    headers.addElement(new hdr("Content-Transfer-Encoding", null));
    headers.addElement(new hdr("Content-MD5", null));
    headers.addElement(new hdr(":", null));
    headers.addElement(new hdr("Content-Length", null));
    headers.addElement(new hdr("Status", null));
  }
  









  public InternetHeaders(InputStream paramInputStream)
    throws MessagingException
  {
    headers = new Vector();
    load(paramInputStream);
  }
  












  public void load(InputStream paramInputStream)
    throws MessagingException
  {
    LineInputStream localLineInputStream = new LineInputStream(paramInputStream);
    try {
      String str;
      while ((str = localLineInputStream.readLine()) != null) {
        if (str.length() == 0) {
          break;
        }
        
        addHeaderLine(str);
      }
    } catch (IOException localIOException) {
      throw new MessagingException("Error in input stream", localIOException);
    }
  }
  





  public String[] getHeader(String paramString)
  {
    Enumeration localEnumeration = headers.elements();
    
    Vector localVector = new Vector();
    
    while (localEnumeration.hasMoreElements()) {
      localObject = (hdr)localEnumeration.nextElement();
      if ((paramString.equalsIgnoreCase(name)) && (line != null)) {
        localVector.addElement(((hdr)localObject).getValue());
      }
    }
    if (localVector.size() == 0) {
      return null;
    }
    Object localObject = new String[localVector.size()];
    localVector.copyInto((Object[])localObject);
    return localObject;
  }
  










  public String getHeader(String paramString1, String paramString2)
  {
    String[] arrayOfString = getHeader(paramString1);
    
    if (arrayOfString == null) {
      return null;
    }
    if ((arrayOfString.length == 1) || (paramString2 == null)) {
      return arrayOfString[0];
    }
    StringBuffer localStringBuffer = new StringBuffer(arrayOfString[0]);
    for (int i = 1; i < arrayOfString.length; i++) {
      localStringBuffer.append(paramString2);
      localStringBuffer.append(arrayOfString[i]);
    }
    return localStringBuffer.toString();
  }
  









  public void setHeader(String paramString1, String paramString2)
  {
    int i = 0;
    
    for (int j = 0; j < headers.size(); j++) {
      hdr localHdr = (hdr)headers.elementAt(j);
      if (paramString1.equalsIgnoreCase(name)) {
        if (i == 0) {
          int k;
          if ((line != null) && ((k = line.indexOf(':')) >= 0)) {
            line = (line.substring(0, k + 1) + " " + paramString2);
          } else {
            line = (paramString1 + ": " + paramString2);
          }
          i = 1;
        } else {
          headers.removeElementAt(j);
          j--;
        }
      }
    }
    
    if (i == 0) {
      addHeader(paramString1, paramString2);
    }
  }
  







  public void addHeader(String paramString1, String paramString2)
  {
    int i = headers.size();
    for (int j = i - 1; j >= 0; j--) {
      hdr localHdr = (hdr)headers.elementAt(j);
      if (paramString1.equalsIgnoreCase(name)) {
        headers.insertElementAt(new hdr(paramString1, paramString2), j + 1);
        return;
      }
      
      if (name.equals(":"))
        i = j;
    }
    headers.insertElementAt(new hdr(paramString1, paramString2), i);
  }
  



  public void removeHeader(String paramString)
  {
    for (int i = 0; i < headers.size(); i++) {
      hdr localHdr = (hdr)headers.elementAt(i);
      if (paramString.equalsIgnoreCase(name)) {
        line = null;
      }
    }
  }
  





  public Enumeration getAllHeaders()
  {
    return new matchEnum(headers, null, false, false);
  }
  



  public Enumeration getMatchingHeaders(String[] paramArrayOfString)
  {
    return new matchEnum(headers, paramArrayOfString, true, false);
  }
  



  public Enumeration getNonMatchingHeaders(String[] paramArrayOfString)
  {
    return new matchEnum(headers, paramArrayOfString, false, false);
  }
  







  public void addHeaderLine(String paramString)
  {
    try
    {
      int i = paramString.charAt(0);
      if ((i == 32) || (i == 9)) {
        hdr localHdr = (hdr)headers.lastElement(); hdr 
          tmp34_33 = localHdr;3433line = (3433line + "\r\n" + paramString);
      } else {
        headers.addElement(new hdr(paramString));
      }
    }
    catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException) {}catch (NoSuchElementException localNoSuchElementException) {}
  }
  





  public Enumeration getAllHeaderLines()
  {
    return getNonMatchingHeaderLines(null);
  }
  


  public Enumeration getMatchingHeaderLines(String[] paramArrayOfString)
  {
    return new matchEnum(headers, paramArrayOfString, true, true);
  }
  


  public Enumeration getNonMatchingHeaderLines(String[] paramArrayOfString)
  {
    return new matchEnum(headers, paramArrayOfString, false, true);
  }
}
