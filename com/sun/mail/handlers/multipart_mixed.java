package com.sun.mail.handlers;

import java.awt.datatransfer.DataFlavor;
import java.io.IOException;
import java.io.OutputStream;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;
import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;






public class multipart_mixed
  implements DataContentHandler
{
  private ActivationDataFlavor myDF = new ActivationDataFlavor(
    MimeMultipart.class, 
    "multipart/mixed", 
    "Multipart");
  




  public DataFlavor[] getTransferDataFlavors()
  {
    return new DataFlavor[] { myDF };
  }
  








  public Object getTransferData(DataFlavor paramDataFlavor, DataSource paramDataSource)
  {
    if (myDF.equals(paramDataFlavor)) {
      return getContent(paramDataSource);
    }
    return null;
  }
  

  public Object getContent(DataSource paramDataSource)
  {
    try
    {
      return new MimeMultipart(paramDataSource);
    } catch (MessagingException localMessagingException) {}
    return null;
  }
  



  public void writeTo(Object paramObject, String paramString, OutputStream paramOutputStream)
    throws IOException
  {
    if ((paramObject instanceof MimeMultipart)) {
      try {
        ((MimeMultipart)paramObject).writeTo(paramOutputStream);
      } catch (MessagingException localMessagingException) {
        throw new IOException(localMessagingException.toString());
      }
    }
  }
  
  public multipart_mixed() {}
}
