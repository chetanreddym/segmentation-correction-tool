package javax.mail.internet;

import java.util.Enumeration;
import javax.mail.MessagingException;
import javax.mail.Part;

public interface MimePart
  extends Part
{
  public abstract String getHeader(String paramString1, String paramString2)
    throws MessagingException;
  
  public abstract void addHeaderLine(String paramString)
    throws MessagingException;
  
  public abstract Enumeration getAllHeaderLines()
    throws MessagingException;
  
  public abstract Enumeration getMatchingHeaderLines(String[] paramArrayOfString)
    throws MessagingException;
  
  public abstract Enumeration getNonMatchingHeaderLines(String[] paramArrayOfString)
    throws MessagingException;
  
  public abstract String getEncoding()
    throws MessagingException;
  
  public abstract String getContentID()
    throws MessagingException;
  
  public abstract String getContentMD5()
    throws MessagingException;
  
  public abstract void setContentMD5(String paramString)
    throws MessagingException;
  
  public abstract String[] getContentLanguage()
    throws MessagingException;
  
  public abstract void setContentLanguage(String[] paramArrayOfString)
    throws MessagingException;
  
  public abstract void setText(String paramString)
    throws MessagingException;
  
  public abstract void setText(String paramString1, String paramString2)
    throws MessagingException;
}
