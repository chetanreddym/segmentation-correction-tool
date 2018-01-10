package com.sun.mail.imap;

import com.sun.mail.iap.CommandFailedException;
import com.sun.mail.iap.ConnectionException;
import com.sun.mail.iap.ProtocolException;
import com.sun.mail.iap.Response;
import com.sun.mail.imap.protocol.BODY;
import com.sun.mail.imap.protocol.BODYSTRUCTURE;
import com.sun.mail.imap.protocol.ENVELOPE;
import com.sun.mail.imap.protocol.FetchResponse;
import com.sun.mail.imap.protocol.IMAPProtocol;
import com.sun.mail.imap.protocol.INTERNALDATE;
import com.sun.mail.imap.protocol.RFC822DATA;
import com.sun.mail.imap.protocol.RFC822SIZE;
import com.sun.mail.imap.protocol.UID;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.activation.DataHandler;
import javax.mail.Address;
import javax.mail.FetchProfile;
import javax.mail.FetchProfile.Item;
import javax.mail.Flags;
import javax.mail.FolderClosedException;
import javax.mail.IllegalWriteException;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.ContentType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import javax.mail.internet.ParameterList;

public class IMAPMessage extends MimeMessage
{
  protected BODYSTRUCTURE bs;
  protected ENVELOPE envelope;
  private Date receivedDate;
  private int size = -1;
  

  private int seqnum;
  
  private long uid = -1L;
  

  protected String sectionId;
  

  private String type;
  
  private String subject;
  
  private String description;
  
  private boolean headersLoaded = false;
  





  private Hashtable loadedHeaders;
  




  private static String EnvelopeCmd = "ENVELOPE INTERNALDATE RFC822.SIZE";
  


  protected IMAPMessage(IMAPFolder paramIMAPFolder, int paramInt1, int paramInt2)
  {
    super(paramIMAPFolder, paramInt1);
    seqnum = paramInt2;
    flags = null;
  }
  


  protected IMAPMessage(Session paramSession)
  {
    super(paramSession);
  }
  





  protected IMAPProtocol getProtocol()
    throws FolderClosedException
  {
    IMAPProtocol localIMAPProtocol = folder).protocol;
    if (localIMAPProtocol == null) {
      throw new FolderClosedException(folder);
    }
    return localIMAPProtocol;
  }
  



  protected Object getMessageCacheLock()
  {
    return folder).messageCacheLock;
  }
  





  protected int getSequenceNumber()
  {
    return seqnum;
  }
  





  protected void setSequenceNumber(int paramInt)
  {
    seqnum = paramInt;
  }
  



  protected void setMessageNumber(int paramInt)
  {
    super.setMessageNumber(paramInt);
  }
  
  protected long getUID() {
    return uid;
  }
  
  protected void setUID(long paramLong) {
    uid = paramLong;
  }
  
  protected void setExpunged(boolean paramBoolean)
  {
    super.setExpunged(paramBoolean);
    seqnum = -1;
  }
  
  protected void checkExpunged() throws javax.mail.MessageRemovedException
  {
    if (expunged) {
      throw new javax.mail.MessageRemovedException();
    }
  }
  
  protected int getFetchBlockSize() {
    return ((IMAPStore)folder.getStore()).getFetchBlockSize();
  }
  

  public Address[] getFrom()
    throws MessagingException
  {
    checkExpunged();
    loadEnvelope();
    return aaclone(envelope.from);
  }
  
  public void setFrom(Address paramAddress) throws MessagingException {
    throw new IllegalWriteException("IMAPMessage is read-only");
  }
  
  public void addFrom(Address[] paramArrayOfAddress) throws MessagingException {
    throw new IllegalWriteException("IMAPMessage is read-only");
  }
  


  public Address[] getRecipients(Message.RecipientType paramRecipientType)
    throws MessagingException
  {
    checkExpunged();
    loadEnvelope();
    
    if (paramRecipientType == Message.RecipientType.TO)
      return aaclone(envelope.to);
    if (paramRecipientType == Message.RecipientType.CC)
      return aaclone(envelope.cc);
    if (paramRecipientType == Message.RecipientType.BCC) {
      return aaclone(envelope.bcc);
    }
    return super.getRecipients(paramRecipientType);
  }
  
  public void setRecipients(Message.RecipientType paramRecipientType, Address[] paramArrayOfAddress) throws MessagingException
  {
    throw new IllegalWriteException("IMAPMessage is read-only");
  }
  
  public void addRecipients(Message.RecipientType paramRecipientType, Address[] paramArrayOfAddress) throws MessagingException
  {
    throw new IllegalWriteException("IMAPMessage is read-only");
  }
  

  public Address[] getReplyTo()
    throws MessagingException
  {
    checkExpunged();
    loadEnvelope();
    return aaclone(envelope.replyTo);
  }
  
  public void setReplyTo(Address[] paramArrayOfAddress) throws MessagingException {
    throw new IllegalWriteException("IMAPMessage is read-only");
  }
  

  public String getSubject()
    throws MessagingException
  {
    checkExpunged();
    
    if (subject != null) {
      return subject;
    }
    loadEnvelope();
    if (envelope.subject == null) {
      return null;
    }
    try
    {
      subject = MimeUtility.decodeText(envelope.subject);
    } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
      subject = envelope.subject;
    }
    
    return subject;
  }
  
  public void setSubject(String paramString1, String paramString2) throws MessagingException
  {
    throw new IllegalWriteException("IMAPMessage is read-only");
  }
  

  public Date getSentDate()
    throws MessagingException
  {
    checkExpunged();
    loadEnvelope();
    if (envelope.date == null) {
      return null;
    }
    return new Date(envelope.date.getTime());
  }
  
  public void setSentDate(Date paramDate) throws MessagingException {
    throw new IllegalWriteException("IMAPMessage is read-only");
  }
  

  public Date getReceivedDate()
    throws MessagingException
  {
    checkExpunged();
    loadEnvelope();
    if (receivedDate == null) {
      return null;
    }
    return new Date(receivedDate.getTime());
  }
  




  public int getSize()
    throws MessagingException
  {
    checkExpunged();
    loadEnvelope();
    return size;
  }
  





  public int getLineCount()
    throws MessagingException
  {
    checkExpunged();
    loadBODYSTRUCTURE();
    return bs.lines;
  }
  




  public String getContentType()
    throws MessagingException
  {
    checkExpunged();
    

    if (type == null) {
      loadBODYSTRUCTURE();
      
      ContentType localContentType = new ContentType(bs.type, bs.subtype, bs.cParams);
      type = localContentType.toString();
    }
    return type;
  }
  

  public String getDisposition()
    throws MessagingException
  {
    checkExpunged();
    loadBODYSTRUCTURE();
    return bs.disposition;
  }
  
  public void setDisposition(String paramString) throws MessagingException {
    throw new IllegalWriteException("IMAPMessage is read-only");
  }
  

  public String getEncoding()
    throws MessagingException
  {
    checkExpunged();
    loadBODYSTRUCTURE();
    return bs.encoding;
  }
  

  public String getContentID()
    throws MessagingException
  {
    checkExpunged();
    loadBODYSTRUCTURE();
    return bs.id;
  }
  
  public void setContentID(String paramString) throws MessagingException {
    throw new IllegalWriteException("IMAPMessage is read-only");
  }
  

  public String getContentMD5()
    throws MessagingException
  {
    checkExpunged();
    loadBODYSTRUCTURE();
    return bs.md5;
  }
  
  public void setContentMD5(String paramString) throws MessagingException {
    throw new IllegalWriteException("IMAPMessage is read-only");
  }
  

  public String getDescription()
    throws MessagingException
  {
    checkExpunged();
    
    if (description != null) {
      return description;
    }
    loadBODYSTRUCTURE();
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
    throw new IllegalWriteException("IMAPMessage is read-only");
  }
  

  public String getMessageID()
    throws MessagingException
  {
    checkExpunged();
    loadEnvelope();
    return envelope.messageId;
  }
  



  public String getFileName()
    throws MessagingException
  {
    checkExpunged();
    
    String str = null;
    loadBODYSTRUCTURE();
    
    if (bs.dParams != null)
      str = bs.dParams.get("filename");
    if ((str == null) && (bs.cParams != null))
      str = bs.cParams.get("name");
    return str;
  }
  
  public void setFileName(String paramString) throws MessagingException {
    throw new IllegalWriteException("IMAPMessage is read-only");
  }
  





  protected InputStream getContentStream()
    throws MessagingException
  {
    ByteArrayInputStream localByteArrayInputStream = null;
    

    synchronized (getMessageCacheLock())
    {
      IMAPProtocol localIMAPProtocol = getProtocol();
      if ((localIMAPProtocol.isREV1()) && (getFetchBlockSize() != -1)) {
        IMAPInputStream localIMAPInputStream = new IMAPInputStream(this, toSection("TEXT"), 
          bs != null ? bs.size : -1);return localIMAPInputStream;
      }
      




      checkExpunged();
      try
      {
        RFC822DATA localRFC822DATA = localIMAPProtocol.fetchRFC822(getSequenceNumber(), "TEXT");
        if (localRFC822DATA != null)
          localByteArrayInputStream = localRFC822DATA.getByteArrayInputStream();
      } catch (ConnectionException localConnectionException) {
        throw new FolderClosedException(folder, localConnectionException.getMessage());
      } catch (ProtocolException localProtocolException) {
        throw new MessagingException(localProtocolException.getMessage(), localProtocolException);
      }
    }
    
    if (localByteArrayInputStream == null) {
      throw new MessagingException("No content");
    }
    return localByteArrayInputStream;
  }
  


  public synchronized DataHandler getDataHandler()
    throws MessagingException
  {
    checkExpunged();
    
    if (dh == null) {
      loadBODYSTRUCTURE();
      if (type == null)
      {
        ContentType localContentType = new ContentType(bs.type, bs.subtype, 
          bs.cParams);
        type = localContentType.toString();
      }
      



      if (bs.isMulti()) {
        dh = new DataHandler(
          new IMAPMultipartDataSource(this, bs.bodies, 
          sectionId, this));
      }
      else if ((bs.isNested()) && (getProtocol().isREV1()))
      {



        dh = new DataHandler(
          new IMAPNestedMessage(this, 
          bs.bodies[0], 
          bs.envelope, 
          sectionId + ".1"), 
          type);
      }
    }
    
    return super.getDataHandler();
  }
  
  public void setDataHandler(DataHandler paramDataHandler) throws MessagingException
  {
    throw new IllegalWriteException("IMAPMessage is read-only");
  }
  


  public void writeTo(OutputStream paramOutputStream)
    throws java.io.IOException, MessagingException
  {
    ByteArrayInputStream localByteArrayInputStream = null;
    

    synchronized (getMessageCacheLock()) {
      checkExpunged();
      
      IMAPProtocol localIMAPProtocol = getProtocol();
      try { Object localObject2;
        if (localIMAPProtocol.isREV1()) {
          localObject2 = localIMAPProtocol.fetchBody(getSequenceNumber(), sectionId);
          if (localObject2 != null)
            localByteArrayInputStream = ((BODY)localObject2).getByteArrayInputStream();
        } else {
          localObject2 = localIMAPProtocol.fetchRFC822(getSequenceNumber(), null);
          if (localObject2 != null)
            localByteArrayInputStream = ((RFC822DATA)localObject2).getByteArrayInputStream();
        }
      } catch (ConnectionException localConnectionException) {
        throw new FolderClosedException(folder, localConnectionException.getMessage());
      } catch (ProtocolException localProtocolException) {
        throw new MessagingException(localProtocolException.getMessage(), localProtocolException);
      }
    }
    
    if (localByteArrayInputStream == null) {
      throw new MessagingException("No content");
    }
    
    ??? = new byte['Ѐ'];
    int i;
    while ((i = localByteArrayInputStream.read((byte[])???)) != -1) {
      paramOutputStream.write((byte[])???, 0, i);
    }
  }
  
  public String[] getHeader(String paramString)
    throws MessagingException
  {
    checkExpunged();
    
    if (isHeaderLoaded(paramString)) {
      return headers.getHeader(paramString);
    }
    
    ByteArrayInputStream localByteArrayInputStream = null;
    

    synchronized (getMessageCacheLock())
    {


      checkExpunged();
      
      IMAPProtocol localIMAPProtocol = getProtocol();
      try { Object localObject2;
        if (localIMAPProtocol.isREV1()) {
          localObject2 = localIMAPProtocol.peekBody(getSequenceNumber(), 
            toSection("HEADER.FIELDS (" + paramString + ")"));
          
          if (localObject2 != null)
            localByteArrayInputStream = ((BODY)localObject2).getByteArrayInputStream();
        } else {
          localObject2 = localIMAPProtocol.fetchRFC822(getSequenceNumber(), 
            "HEADER.LINES (" + paramString + ")");
          if (localObject2 != null)
            localByteArrayInputStream = ((RFC822DATA)localObject2).getByteArrayInputStream();
        }
      } catch (ConnectionException localConnectionException) {
        throw new FolderClosedException(folder, localConnectionException.getMessage());
      } catch (ProtocolException localProtocolException) {
        throw new MessagingException(localProtocolException.getMessage(), localProtocolException);
      }
    }
    
    if (headers == null)
      headers = new InternetHeaders();
    headers.load(localByteArrayInputStream);
    setHeaderLoaded(paramString);
    
    return headers.getHeader(paramString);
  }
  


  public String getHeader(String paramString1, String paramString2)
    throws MessagingException
  {
    checkExpunged();
    

    if (getHeader(paramString1) == null)
      return null;
    return headers.getHeader(paramString1, paramString2);
  }
  
  public void setHeader(String paramString1, String paramString2) throws MessagingException
  {
    throw new IllegalWriteException("IMAPMessage is read-only");
  }
  
  public void addHeader(String paramString1, String paramString2) throws MessagingException
  {
    throw new IllegalWriteException("IMAPMessage is read-only");
  }
  
  public void removeHeader(String paramString) throws MessagingException
  {
    throw new IllegalWriteException("IMAPMessage is read-only");
  }
  

  public Enumeration getAllHeaders()
    throws MessagingException
  {
    checkExpunged();
    loadHeaders();
    return super.getAllHeaders();
  }
  


  public Enumeration getMatchingHeaders(String[] paramArrayOfString)
    throws MessagingException
  {
    checkExpunged();
    loadHeaders();
    return super.getMatchingHeaders(paramArrayOfString);
  }
  


  public Enumeration getNonMatchingHeaders(String[] paramArrayOfString)
    throws MessagingException
  {
    checkExpunged();
    loadHeaders();
    return super.getNonMatchingHeaders(paramArrayOfString);
  }
  
  public void addHeaderLine(String paramString) throws MessagingException {
    throw new IllegalWriteException("IMAPMessage is read-only");
  }
  

  public Enumeration getAllHeaderLines()
    throws MessagingException
  {
    checkExpunged();
    loadHeaders();
    return super.getAllHeaderLines();
  }
  


  public Enumeration getMatchingHeaderLines(String[] paramArrayOfString)
    throws MessagingException
  {
    checkExpunged();
    loadHeaders();
    return super.getMatchingHeaderLines(paramArrayOfString);
  }
  


  public Enumeration getNonMatchingHeaderLines(String[] paramArrayOfString)
    throws MessagingException
  {
    checkExpunged();
    loadHeaders();
    return super.getNonMatchingHeaderLines(paramArrayOfString);
  }
  

  public synchronized Flags getFlags()
    throws MessagingException
  {
    checkExpunged();
    loadFlags();
    return super.getFlags();
  }
  


  public synchronized boolean isSet(javax.mail.Flags.Flag paramFlag)
    throws MessagingException
  {
    checkExpunged();
    loadFlags();
    return super.isSet(paramFlag);
  }
  



  public synchronized void setFlags(Flags paramFlags, boolean paramBoolean)
    throws MessagingException
  {
    synchronized (getMessageCacheLock()) {
      checkExpunged();
      try
      {
        getProtocol().storeFlags(getSequenceNumber(), paramFlags, paramBoolean);
      } catch (ConnectionException localConnectionException) {
        throw new FolderClosedException(folder, localConnectionException.getMessage());
      } catch (ProtocolException localProtocolException) {
        throw new MessagingException(localProtocolException.getMessage(), localProtocolException);
      }
    }
  }
  










  private class 1$FetchProfileCondition
    implements Utility.Condition
  {
    private boolean needEnvelope = false;
    private boolean needFlags = false;
    private boolean needBodyStructure = false;
    private boolean needUID = false;
    private String[] hdrs;
    
    public 1$FetchProfileCondition() {
      if (this$1.contains(FetchProfile.Item.ENVELOPE))
        needEnvelope = true;
      if (this$1.contains(FetchProfile.Item.FLAGS))
        needFlags = true;
      if (this$1.contains(FetchProfile.Item.CONTENT_INFO))
        needBodyStructure = true;
      if (this$1.contains(javax.mail.UIDFolder.FetchProfileItem.UID))
        needUID = true;
      hdrs = this$1.getHeaderNames();
    }
    
    public boolean test(IMAPMessage paramIMAPMessage)
    {
      if ((needEnvelope) && (paramIMAPMessage._getEnvelope() == null))
        return true;
      if ((needFlags) && (paramIMAPMessage._getFlags() == null))
        return true;
      if ((needBodyStructure) && (paramIMAPMessage._getBodyStructure() == null))
        return true;
      if ((needUID) && (paramIMAPMessage.getUID() == -1L)) {
        return true;
      }
      
      for (int i = 0; i < hdrs.length; i++) {
        if (!paramIMAPMessage.isHeaderLoaded(hdrs[i])) {
          return true;
        }
      }
      return false;
    }
  }
  
  static void fetch(IMAPFolder paramIMAPFolder, Message[] paramArrayOfMessage, FetchProfile paramFetchProfile) throws MessagingException { StringBuffer localStringBuffer = new StringBuffer();
    int i = 1;
    
    if (paramFetchProfile.contains(FetchProfile.Item.ENVELOPE)) {
      localStringBuffer.append(EnvelopeCmd);
      i = 0;
    }
    if (paramFetchProfile.contains(FetchProfile.Item.FLAGS)) {
      localStringBuffer.append(i != 0 ? "FLAGS" : " FLAGS");
      i = 0;
    }
    if (paramFetchProfile.contains(FetchProfile.Item.CONTENT_INFO)) {
      localStringBuffer.append(i != 0 ? "BODYSTRUCTURE" : " BODYSTRUCTURE");
      i = 0;
    }
    if (paramFetchProfile.contains(javax.mail.UIDFolder.FetchProfileItem.UID)) {
      localStringBuffer.append(i != 0 ? "UID" : " UID");
      i = 0;
    }
    
    String[] arrayOfString = paramFetchProfile.getHeaderNames();
    if (arrayOfString.length > 0) {
      if (i == 0)
        localStringBuffer.append(" ");
      localStringBuffer.append(craftHeaderCmd(protocol, arrayOfString));
    }
    
    1.FetchProfileCondition localFetchProfileCondition = new 1.FetchProfileCondition(paramFetchProfile);
    

    synchronized (messageCacheLock)
    {


      com.sun.mail.imap.protocol.MessageSet[] arrayOfMessageSet = Utility.toMessageSet(paramArrayOfMessage, localFetchProfileCondition);
      
      if (arrayOfMessageSet == null)
      {
        jsr 541;return;
      }
      Response[] arrayOfResponse = null;
      Vector localVector = new Vector();
      try
      {
        arrayOfResponse = protocol.fetch(arrayOfMessageSet, localStringBuffer.toString());
      } catch (ConnectionException localConnectionException) {
        throw new FolderClosedException(paramIMAPFolder, localConnectionException.getMessage());
      }
      catch (CommandFailedException localCommandFailedException) {}catch (ProtocolException localProtocolException)
      {
        throw new MessagingException(localProtocolException.getMessage(), localProtocolException);
      }
      
      if (arrayOfResponse == null)
        return;
      Object localObject3;
      for (int j = 0; j < arrayOfResponse.length; j++) {
        if (arrayOfResponse[j] != null)
        {
          if (!(arrayOfResponse[j] instanceof FetchResponse)) {
            localVector.addElement(arrayOfResponse[j]);

          }
          else
          {
            FetchResponse localFetchResponse = (FetchResponse)arrayOfResponse[j];
            
            localObject3 = paramIMAPFolder.getMessageBySeqNumber(localFetchResponse.getNumber());
            
            int m = localFetchResponse.getItemCount();
            int n = 0;
            
            for (int i1 = 0; i1 < m; i1++) {
              com.sun.mail.imap.protocol.Item localItem = localFetchResponse.getItem(i1);
              

              if ((localItem instanceof Flags)) {
                if ((!paramFetchProfile.contains(FetchProfile.Item.FLAGS)) || 
                  (localObject3 == null))
                {
                  n = 1;
                } else {
                  flags = ((Flags)localItem);
                }
                
              }
              else if ((localItem instanceof ENVELOPE)) {
                envelope = ((ENVELOPE)localItem);
              } else if ((localItem instanceof INTERNALDATE)) {
                receivedDate = ((INTERNALDATE)localItem).getDate();
              } else if ((localItem instanceof RFC822SIZE)) {
                size = size;

              }
              else if ((localItem instanceof BODYSTRUCTURE)) {
                bs = ((BODYSTRUCTURE)localItem);
              } else { Object localObject4;
                if ((localItem instanceof UID)) {
                  localObject4 = (UID)localItem;
                  uid = uid;
                  
                  if (uidTable == null)
                    uidTable = new Hashtable();
                  uidTable.put(new Long(uid), localObject3);


                }
                else if (((localItem instanceof RFC822DATA)) || 
                  ((localItem instanceof BODY)))
                {
                  if ((localItem instanceof RFC822DATA)) {
                    localObject4 = 
                      ((RFC822DATA)localItem).getByteArrayInputStream();
                  } else {
                    localObject4 = 
                      ((BODY)localItem).getByteArrayInputStream();
                  }
                  
                  if (headers == null)
                    headers = new InternetHeaders();
                  headers.load((InputStream)localObject4);
                  

                  for (int i2 = 0; i2 < arrayOfString.length; i2++) {
                    ((IMAPMessage)localObject3).setHeaderLoaded(arrayOfString[i2]);
                  }
                }
              }
            }
            
            if (n != 0)
              localVector.addElement(localFetchResponse);
          }
        }
      }
      int k = localVector.size();
      if (k != 0) {
        localObject3 = new Response[k];
        localVector.copyInto((Object[])localObject3);
        paramIMAPFolder.handleResponses((Response[])localObject3);
      }
    }
  }
  


  private synchronized void loadEnvelope()
    throws MessagingException
  {
    if (envelope != null) {
      return;
    }
    IMAPProtocol localIMAPProtocol = getProtocol();
    Response[] arrayOfResponse = null;
    

    synchronized (getMessageCacheLock())
    {
      checkExpunged();
      
      int i = getSequenceNumber();
      try {
        arrayOfResponse = localIMAPProtocol.fetch(i, EnvelopeCmd);
      } catch (ConnectionException localConnectionException) {
        throw new FolderClosedException(folder, localConnectionException.getMessage());
      } catch (ProtocolException localProtocolException) {
        throw new MessagingException(localProtocolException.getMessage(), localProtocolException);
      }
      
      if (arrayOfResponse == null) {
        return;
      }
      for (int j = 0; j < arrayOfResponse.length; j++)
      {

        if ((arrayOfResponse[j] != null) && 
          ((arrayOfResponse[j] instanceof FetchResponse)) && 
          (((FetchResponse)arrayOfResponse[j]).getNumber() == i))
        {

          FetchResponse localFetchResponse = (FetchResponse)arrayOfResponse[j];
          

          int k = localFetchResponse.getItemCount();
          for (int m = 0; m < k; m++) {
            com.sun.mail.imap.protocol.Item localItem = localFetchResponse.getItem(m);
            
            if ((localItem instanceof ENVELOPE)) {
              envelope = ((ENVELOPE)localItem);
            } else if ((localItem instanceof INTERNALDATE)) {
              receivedDate = ((INTERNALDATE)localItem).getDate();
            } else if ((localItem instanceof RFC822SIZE))
              size = size;
          }
        }
      }
      ((IMAPFolder)folder).handleResponses(arrayOfResponse);
    }
  }
  

  private static String craftHeaderCmd(IMAPProtocol paramIMAPProtocol, String[] paramArrayOfString)
  {
    StringBuffer localStringBuffer;
    if (paramIMAPProtocol.isREV1()) {
      localStringBuffer = new StringBuffer("BODY.PEEK[HEADER.FIELDS (");
    } else {
      localStringBuffer = new StringBuffer("RFC822.HEADER.LINES (");
    }
    for (int i = 0; i < paramArrayOfString.length; i++) {
      if (i > 0)
        localStringBuffer.append(" ");
      localStringBuffer.append(paramArrayOfString[i]);
    }
    
    if (paramIMAPProtocol.isREV1()) {
      localStringBuffer.append(")]");
    } else {
      localStringBuffer.append(")");
    }
    return localStringBuffer.toString();
  }
  


  private synchronized void loadBODYSTRUCTURE()
    throws MessagingException
  {
    if (bs != null) {
      return;
    }
    
    synchronized (getMessageCacheLock())
    {

      checkExpunged();
      try
      {
        bs = getProtocol().fetchBodyStructure(getSequenceNumber());
      } catch (ConnectionException localConnectionException) {
        throw new FolderClosedException(folder, localConnectionException.getMessage());
      } catch (ProtocolException localProtocolException) {
        throw new MessagingException(localProtocolException.getMessage(), localProtocolException);
      }
    }
  }
  

  private synchronized void loadHeaders()
    throws MessagingException
  {
    if (headersLoaded) {
      return;
    }
    ByteArrayInputStream localByteArrayInputStream = null;
    

    synchronized (getMessageCacheLock())
    {


      checkExpunged();
      
      IMAPProtocol localIMAPProtocol = getProtocol();
      try { Object localObject2;
        if (localIMAPProtocol.isREV1()) {
          localObject2 = localIMAPProtocol.peekBody(getSequenceNumber(), 
            toSection("HEADER"));
          if (localObject2 != null)
            localByteArrayInputStream = ((BODY)localObject2).getByteArrayInputStream();
        } else {
          localObject2 = localIMAPProtocol.fetchRFC822(getSequenceNumber(), 
            "HEADER");
          if (localObject2 != null)
            localByteArrayInputStream = ((RFC822DATA)localObject2).getByteArrayInputStream();
        }
      } catch (ConnectionException localConnectionException) {
        throw new FolderClosedException(folder, localConnectionException.getMessage());
      } catch (ProtocolException localProtocolException) {
        throw new MessagingException(localProtocolException.getMessage(), localProtocolException);
      }
    }
    
    if (localByteArrayInputStream == null)
      throw new MessagingException("Cannot load header");
    headers = new InternetHeaders(localByteArrayInputStream);
    headersLoaded = true;
  }
  

  private synchronized void loadFlags()
    throws MessagingException
  {
    if (flags != null) {
      return;
    }
    
    synchronized (getMessageCacheLock())
    {


      checkExpunged();
      try
      {
        flags = getProtocol().fetchFlags(getSequenceNumber());
      } catch (ConnectionException localConnectionException) {
        throw new FolderClosedException(folder, localConnectionException.getMessage());
      } catch (ProtocolException localProtocolException) {
        throw new MessagingException(localProtocolException.getMessage(), localProtocolException);
      }
    }
  }
  


  private boolean isHeaderLoaded(String paramString)
  {
    if (headersLoaded) {
      return true;
    }
    if (loadedHeaders != null) return 
        loadedHeaders.containsKey(paramString.toUpperCase());
    return 
      false;
  }
  


  private void setHeaderLoaded(String paramString)
  {
    if (loadedHeaders == null)
      loadedHeaders = new Hashtable(1);
    loadedHeaders.put(paramString.toUpperCase(), paramString);
  }
  



  private String toSection(String paramString)
  {
    if (sectionId == null) {
      return paramString;
    }
    return sectionId + "." + paramString;
  }
  


  private InternetAddress[] aaclone(InternetAddress[] paramArrayOfInternetAddress)
  {
    if (paramArrayOfInternetAddress == null) {
      return null;
    }
    return (InternetAddress[])paramArrayOfInternetAddress.clone();
  }
  
  void _setFlags(Flags paramFlags) {
    flags = paramFlags;
  }
  




  Flags _getFlags()
  {
    return flags;
  }
  
  ENVELOPE _getEnvelope() {
    return envelope;
  }
  
  BODYSTRUCTURE _getBodyStructure() {
    return bs;
  }
  
  Session _getSession() {
    return session;
  }
}
