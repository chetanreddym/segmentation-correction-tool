package javax.mail.internet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.UnknownServiceException;
import javax.activation.DataSource;
import javax.mail.MessageAware;
import javax.mail.MessageContext;
import javax.mail.MessagingException;
import javax.mail.Part;















public class MimePartDataSource
  implements DataSource, MessageAware
{
  private MimePart part;
  private MessageContext context;
  
  public MimePartDataSource(MimePart paramMimePart)
  {
    part = paramMimePart;
  }
  







  public InputStream getInputStream()
    throws IOException
  {
    try
    {
      InputStream localInputStream;
      






      if ((part instanceof MimeBodyPart)) {
        localInputStream = ((MimeBodyPart)part).getContentStream();
      } else if ((part instanceof MimeMessage)) {
        localInputStream = ((MimeMessage)part).getContentStream();
      } else {
        throw new MessagingException("Unknown part");
      }
      String str = part.getEncoding();
      if (str != null) {
        return MimeUtility.decode(localInputStream, str);
      }
      return localInputStream;
    } catch (MessagingException localMessagingException) {
      throw new IOException(localMessagingException.getMessage());
    }
  }
  



  public OutputStream getOutputStream()
    throws IOException
  {
    throw new UnknownServiceException();
  }
  




  public String getContentType()
  {
    try
    {
      return part.getContentType();
    } catch (MessagingException localMessagingException) {}
    return null;
  }
  




  public String getName()
  {
    try
    {
      if ((part instanceof MimeBodyPart)) {
        return ((MimeBodyPart)part).getFileName();
      }
    }
    catch (MessagingException localMessagingException) {}
    return "";
  }
  



  public synchronized MessageContext getMessageContext()
  {
    if (context == null)
      context = new MessageContext(part);
    return context;
  }
}
