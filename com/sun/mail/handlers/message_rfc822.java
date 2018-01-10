package com.sun.mail.handlers;

import java.awt.datatransfer.DataFlavor;
import java.io.IOException;
import java.io.OutputStream;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.MessageAware;
import javax.mail.MessageContext;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;









public class message_rfc822
  implements DataContentHandler
{
  ActivationDataFlavor ourDataFlavor = new ActivationDataFlavor(
    Message.class, 
    "message/rfc822", 
    "Message");
  



  public DataFlavor[] getTransferDataFlavors()
  {
    return new DataFlavor[] { ourDataFlavor };
  }
  






  public Object getTransferData(DataFlavor paramDataFlavor, DataSource paramDataSource)
  {
    if (ourDataFlavor.equals(paramDataFlavor)) {
      return getContent(paramDataSource);
    }
    

    return null;
  }
  

  public Object getContent(DataSource paramDataSource)
  {
    try
    {
      Session localSession;
      
      if ((paramDataSource instanceof MessageAware)) {
        MessageContext localMessageContext = ((MessageAware)paramDataSource).getMessageContext();
        localSession = localMessageContext.getSession();
      } else {
        localSession = Session.getDefaultInstance(null, null); }
      return new MimeMessage(localSession, paramDataSource.getInputStream());
    } catch (IOException localIOException) {
      localIOException.printStackTrace();
    } catch (MessagingException localMessagingException) {
      localMessagingException.printStackTrace();
    }
    
    return null;
  }
  





  public void writeTo(Object paramObject, String paramString, OutputStream paramOutputStream)
    throws IOException
  {
    if ((paramObject instanceof Message)) {
      Message localMessage = (Message)paramObject;
      try {
        localMessage.writeTo(paramOutputStream);return;
      } catch (MessagingException localMessagingException) {
        throw new IOException(localMessagingException.toString());
      }
    }
    
    throw new IOException("unsupported object");
  }
  
  public message_rfc822() {}
}
