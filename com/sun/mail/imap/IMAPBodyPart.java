package com.sun.mail.imap;

import com.sun.mail.iap.ConnectionException;
import com.sun.mail.iap.ProtocolException;
import com.sun.mail.imap.protocol.BODY;
import com.sun.mail.imap.protocol.BODYSTRUCTURE;
import com.sun.mail.imap.protocol.IMAPProtocol;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import javax.activation.DataHandler;
import javax.mail.FolderClosedException;
import javax.mail.IllegalWriteException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.ContentType;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeUtility;
import javax.mail.internet.ParameterList;









public class IMAPBodyPart
  extends MimeBodyPart
{
  private IMAPMessage message;
  private BODYSTRUCTURE bs;
  private String sectionId;
  private String type;
  private String description;
  private boolean headersLoaded = false;
  
  protected IMAPBodyPart(BODYSTRUCTURE paramBODYSTRUCTURE, String paramString, IMAPMessage paramIMAPMessage)
  {
    bs = paramBODYSTRUCTURE;
    sectionId = paramString;
    message = paramIMAPMessage;
    
    ContentType localContentType = new ContentType(type, subtype, cParams);
    type = localContentType.toString();
  }
  



  protected void updateHeaders() {}
  


  public int getSize()
    throws MessagingException
  {
    return bs.size;
  }
  
  public int getLineCount() throws MessagingException {
    return bs.lines;
  }
  
  public String getContentType() throws MessagingException {
    return type;
  }
  
  public String getDisposition() throws MessagingException {
    return bs.disposition;
  }
  
  public void setDisposition(String paramString) throws MessagingException {
    throw new IllegalWriteException("IMAPBodyPart is read-only");
  }
  
  public String getEncoding() throws MessagingException {
    return bs.encoding;
  }
  
  public String getContentID() throws MessagingException {
    return bs.id;
  }
  
  public String getContentMD5() throws MessagingException {
    return bs.md5;
  }
  
  public void setContentMD5(String paramString) throws MessagingException {
    throw new IllegalWriteException("IMAPBodyPart is read-only");
  }
  
  public String getDescription() throws MessagingException {
    if (description != null) {
      return description;
    }
    if (bs.description == null) {
      return null;
    }
    try {
      description = MimeUtility.decodeText(bs.description);
    } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
      description = bs.description;
    }
    
    return description;
  }
  
  public void setDescription(String paramString1, String paramString2) throws MessagingException
  {
    throw new IllegalWriteException("IMAPBodyPart is read-only");
  }
  
  public String getFileName() throws MessagingException {
    String str = null;
    if (bs.dParams != null)
      str = bs.dParams.get("filename");
    if ((str == null) && (bs.cParams != null))
      str = bs.cParams.get("name");
    return str;
  }
  
  public void setFileName(String paramString) throws MessagingException {
    throw new IllegalWriteException("IMAPBodyPart is read-only");
  }
  
  protected InputStream getContentStream() throws MessagingException {
    ByteArrayInputStream localByteArrayInputStream = null;
    

    synchronized (message.getMessageCacheLock()) {
      IMAPProtocol localIMAPProtocol = message.getProtocol();
      if ((localIMAPProtocol.isREV1()) && (message.getFetchBlockSize() != -1)) {
        IMAPInputStream localIMAPInputStream = new IMAPInputStream(message, sectionId, bs.size);return localIMAPInputStream;
      }
      


      message.checkExpunged();
      
      int i = message.getSequenceNumber();
      try {
        BODY localBODY = localIMAPProtocol.fetchBody(i, sectionId);
        if (localBODY != null)
          localByteArrayInputStream = localBODY.getByteArrayInputStream();
      } catch (ConnectionException localConnectionException) {
        throw new FolderClosedException(
          message.getFolder(), localConnectionException.getMessage());
      } catch (ProtocolException localProtocolException) {
        throw new MessagingException(localProtocolException.getMessage(), localProtocolException);
      }
    }
    
    if (localByteArrayInputStream == null) {
      throw new MessagingException("No content");
    }
    return localByteArrayInputStream;
  }
  
  public synchronized DataHandler getDataHandler() throws MessagingException
  {
    if (dh == null) {
      if (bs.isMulti()) {
        dh = new DataHandler(
          new IMAPMultipartDataSource(
          this, bs.bodies, sectionId, message));
      }
      else if ((bs.isNested()) && (message.getProtocol().isREV1())) {
        dh = new DataHandler(
          new IMAPNestedMessage(message, 
          bs.bodies[0], 
          bs.envelope, 
          sectionId), 
          type);
      }
    }
    
    return super.getDataHandler();
  }
  
  public void setDataHandler(DataHandler paramDataHandler) throws MessagingException {
    throw new IllegalWriteException("IMAPBodyPart is read-only");
  }
  
  public void setContent(Object paramObject, String paramString) throws MessagingException {
    throw new IllegalWriteException("IMAPBodyPart is read-only");
  }
  
  public void setContent(Multipart paramMultipart) throws MessagingException {
    throw new IllegalWriteException("IMAPBodyPart is read-only");
  }
  
  public String[] getHeader(String paramString) throws MessagingException {
    loadHeaders();
    return super.getHeader(paramString);
  }
  
  public void setHeader(String paramString1, String paramString2) throws MessagingException
  {
    throw new IllegalWriteException("IMAPBodyPart is read-only");
  }
  
  public void addHeader(String paramString1, String paramString2) throws MessagingException
  {
    throw new IllegalWriteException("IMAPBodyPart is read-only");
  }
  
  public void removeHeader(String paramString) throws MessagingException {
    throw new IllegalWriteException("IMAPBodyPart is read-only");
  }
  
  public Enumeration getAllHeaders() throws MessagingException {
    loadHeaders();
    return super.getAllHeaders();
  }
  
  public Enumeration getMatchingHeaders(String[] paramArrayOfString) throws MessagingException
  {
    loadHeaders();
    return super.getMatchingHeaders(paramArrayOfString);
  }
  
  public Enumeration getNonMatchingHeaders(String[] paramArrayOfString) throws MessagingException
  {
    loadHeaders();
    return super.getNonMatchingHeaders(paramArrayOfString);
  }
  
  public void addHeaderLine(String paramString) throws MessagingException {
    throw new IllegalWriteException("IMAPBodyPart is read-only");
  }
  
  public Enumeration getAllHeaderLines() throws MessagingException {
    loadHeaders();
    return super.getAllHeaderLines();
  }
  
  public Enumeration getMatchingHeaderLines(String[] paramArrayOfString) throws MessagingException
  {
    loadHeaders();
    return super.getMatchingHeaderLines(paramArrayOfString);
  }
  
  public Enumeration getNonMatchingHeaderLines(String[] paramArrayOfString) throws MessagingException
  {
    loadHeaders();
    return super.getNonMatchingHeaderLines(paramArrayOfString);
  }
  
  private synchronized void loadHeaders() throws MessagingException {
    if (headersLoaded) {
      return;
    }
    if (headers == null) {
      headers = new InternetHeaders();
    }
    
    IMAPProtocol localIMAPProtocol = message.getProtocol();
    if (localIMAPProtocol.isREV1()) {
      BODY localBODY = null;
      

      synchronized (message.getMessageCacheLock())
      {
        localIMAPProtocol = message.getProtocol();
        

        message.checkExpunged();
        
        int i = message.getSequenceNumber();
        try {
          localBODY = localIMAPProtocol.peekBody(i, sectionId + ".MIME");
        } catch (ConnectionException localConnectionException) {
          throw new FolderClosedException(
            message.getFolder(), localConnectionException.getMessage());
        } catch (ProtocolException localProtocolException) {
          throw new MessagingException(localProtocolException.getMessage(), localProtocolException);
        }
      }
      
      if (localBODY == null) {
        throw new MessagingException("Failed to fetch headers");
      }
      ??? = localBODY.getByteArrayInputStream();
      if (??? == null) {
        throw new MessagingException("Failed to fetch headers");
      }
      headers.load((InputStream)???);


    }
    else
    {


      headers.addHeader("Content-Type", type);
      
      headers.addHeader("Content-Transfer-Encoding", bs.encoding);
      
      if (bs.description != null) {
        headers.addHeader("Content-Description", bs.description);
      }
      if (bs.id != null) {
        headers.addHeader("Content-ID", bs.id);
      }
      if (bs.md5 != null)
        headers.addHeader("Content-MD5", bs.md5);
    }
    headersLoaded = true;
  }
}
