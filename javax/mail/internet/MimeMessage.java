package javax.mail.internet;

import com.sun.mail.util.ASCIIUtility;
import com.sun.mail.util.LineOutputStream;
import com.sun.mail.util.SharedByteArrayInputStream;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectStreamException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;
import javax.activation.DataHandler;
import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;







































































public class MimeMessage
  extends Message
  implements MimePart
{
  protected DataHandler dh;
  protected byte[] content;
  protected InputStream contentStream;
  protected InternetHeaders headers;
  protected Flags flags;
  protected boolean modified = false;
  











  protected boolean saved = false;
  

  private static MailDateFormat mailDateFormat = new MailDateFormat();
  





  public MimeMessage(Session paramSession)
  {
    super(paramSession);
    modified = true;
    headers = new InternetHeaders();
    flags = new Flags();
  }
  









  public MimeMessage(Session paramSession, InputStream paramInputStream)
    throws MessagingException
  {
    super(paramSession);
    flags = new Flags();
    parse(paramInputStream);
    saved = true;
  }
  










  public MimeMessage(MimeMessage paramMimeMessage)
    throws MessagingException
  {
    super(session);
    flags = paramMimeMessage.getFlags();
    
    int i = paramMimeMessage.getSize();
    ByteArrayOutputStream localByteArrayOutputStream; if (i > 0) {
      localByteArrayOutputStream = new ByteArrayOutputStream(i);
    } else
      localByteArrayOutputStream = new ByteArrayOutputStream();
    try {
      paramMimeMessage.writeTo(localByteArrayOutputStream);
      localByteArrayOutputStream.close();
      SharedByteArrayInputStream localSharedByteArrayInputStream = 
        new SharedByteArrayInputStream(localByteArrayOutputStream.toByteArray());
      parse(localSharedByteArrayInputStream);
      localSharedByteArrayInputStream.close();
      saved = true;
    }
    catch (IOException localIOException) {
      throw new MessagingException("IOException while copying message", 
        localIOException);
    }
  }
  





  protected MimeMessage(Folder paramFolder, int paramInt)
  {
    super(paramFolder, paramInt);
    flags = new Flags();
    saved = true;
  }
  












  protected MimeMessage(Folder paramFolder, InputStream paramInputStream, int paramInt)
    throws MessagingException
  {
    this(paramFolder, paramInt);
    parse(paramInputStream);
  }
  










  protected MimeMessage(Folder paramFolder, InternetHeaders paramInternetHeaders, byte[] paramArrayOfByte, int paramInt)
    throws MessagingException
  {
    this(paramFolder, paramInt);
    headers = paramInternetHeaders;
    content = paramArrayOfByte;
  }
  










  protected void parse(InputStream paramInputStream)
    throws MessagingException
  {
    if ((!(paramInputStream instanceof ByteArrayInputStream)) && 
      (!(paramInputStream instanceof BufferedInputStream))) {
      paramInputStream = new BufferedInputStream(paramInputStream);
    }
    headers = createInternetHeaders(paramInputStream);
    
    if ((paramInputStream instanceof SharedInputStream)) {
      SharedInputStream localSharedInputStream = (SharedInputStream)paramInputStream;
      contentStream = localSharedInputStream.newStream(localSharedInputStream.getPosition(), -1L);
    } else {
      try {
        content = ASCIIUtility.getBytes(paramInputStream);
      } catch (IOException localIOException) {
        throw new MessagingException("IOException", localIOException);
      }
    }
    
    modified = false;
  }
  











  public Address[] getFrom()
    throws MessagingException
  {
    Address[] arrayOfAddress = getAddressHeader("From");
    if (arrayOfAddress == null) {
      arrayOfAddress = getAddressHeader("Sender");
    }
    return arrayOfAddress;
  }
  











  public void setFrom(Address paramAddress)
    throws MessagingException
  {
    if (paramAddress == null) {
      removeHeader("From");
    } else {
      setHeader("From", paramAddress.toString());
    }
  }
  








  public void setFrom()
    throws MessagingException
  {
    InternetAddress localInternetAddress = InternetAddress.getLocalAddress(session);
    if (localInternetAddress != null) {
      setFrom(localInternetAddress);
    } else {
      throw new MessagingException("No From address");
    }
  }
  









  public void addFrom(Address[] paramArrayOfAddress)
    throws MessagingException
  {
    addAddressHeader("From", paramArrayOfAddress);
  }
  









  public static class RecipientType
    extends Message.RecipientType
  {
    public static final RecipientType NEWSGROUPS = new RecipientType("Newsgroups");
    
    protected RecipientType(String paramString) { super(); }
    
    protected Object readResolve() throws ObjectStreamException
    {
      if (type.equals("Newsgroups")) {
        return NEWSGROUPS;
      }
      return super.readResolve();
    }
  }
  



























  public Address[] getRecipients(Message.RecipientType paramRecipientType)
    throws MessagingException
  {
    if (paramRecipientType == RecipientType.NEWSGROUPS) {
      String str = getHeader("Newsgroups", ",");
      if (str == null) return null; return NewsAddress.parse(str);
    }
    return getAddressHeader(getHeaderName(paramRecipientType));
  }
  









  public Address[] getAllRecipients()
    throws MessagingException
  {
    Address[] arrayOfAddress1 = super.getAllRecipients();
    Address[] arrayOfAddress2 = getRecipients(RecipientType.NEWSGROUPS);
    
    if (arrayOfAddress2 == null) {
      return arrayOfAddress1;
    }
    int i = 
      (arrayOfAddress1 != null ? arrayOfAddress1.length : 0) + (
      arrayOfAddress2 != null ? arrayOfAddress2.length : 0);
    Address[] arrayOfAddress3 = new Address[i];
    int j = 0;
    if (arrayOfAddress1 != null) {
      System.arraycopy(arrayOfAddress1, 0, arrayOfAddress3, j, arrayOfAddress1.length);
      j += arrayOfAddress1.length;
    }
    if (arrayOfAddress2 != null) {
      System.arraycopy(arrayOfAddress2, 0, arrayOfAddress3, j, arrayOfAddress2.length);
      j += arrayOfAddress2.length;
    }
    return arrayOfAddress3;
  }
  














  public void setRecipients(Message.RecipientType paramRecipientType, Address[] paramArrayOfAddress)
    throws MessagingException
  {
    if (paramRecipientType == RecipientType.NEWSGROUPS) {
      if ((paramArrayOfAddress == null) || (paramArrayOfAddress.length == 0)) {
        removeHeader("Newsgroups");
      } else
        setHeader("Newsgroups", NewsAddress.toString(paramArrayOfAddress));
    } else {
      setAddressHeader(getHeaderName(paramRecipientType), paramArrayOfAddress);
    }
  }
  
















  public void setRecipients(Message.RecipientType paramRecipientType, String paramString)
    throws MessagingException
  {
    if (paramRecipientType == RecipientType.NEWSGROUPS) {
      if ((paramString == null) || (paramString.length() == 0)) {
        removeHeader("Newsgroups");
      } else
        setHeader("Newsgroups", paramString);
    } else {
      setAddressHeader(getHeaderName(paramRecipientType), InternetAddress.parse(paramString));
    }
  }
  










  public void addRecipients(Message.RecipientType paramRecipientType, Address[] paramArrayOfAddress)
    throws MessagingException
  {
    if (paramRecipientType == RecipientType.NEWSGROUPS) {
      String str = NewsAddress.toString(paramArrayOfAddress);
      if (str != null)
        addHeader("Newsgroups", str);
    } else {
      addAddressHeader(getHeaderName(paramRecipientType), paramArrayOfAddress);
    }
  }
  













  public void addRecipients(Message.RecipientType paramRecipientType, String paramString)
    throws MessagingException
  {
    if (paramRecipientType == RecipientType.NEWSGROUPS) {
      if ((paramString != null) && (paramString.length() != 0))
        addHeader("Newsgroups", paramString);
    } else {
      addAddressHeader(getHeaderName(paramRecipientType), InternetAddress.parse(paramString));
    }
  }
  








  public Address[] getReplyTo()
    throws MessagingException
  {
    Address[] arrayOfAddress = getAddressHeader("Reply-To");
    if (arrayOfAddress == null)
      arrayOfAddress = getFrom();
    return arrayOfAddress;
  }
  









  public void setReplyTo(Address[] paramArrayOfAddress)
    throws MessagingException
  {
    setAddressHeader("Reply-To", paramArrayOfAddress);
  }
  
  private Address[] getAddressHeader(String paramString)
    throws MessagingException
  {
    String str = getHeader(paramString, ",");
    if (str == null) return null; return InternetAddress.parse(str);
  }
  
  private void setAddressHeader(String paramString, Address[] paramArrayOfAddress)
    throws MessagingException
  {
    String str = InternetAddress.toString(paramArrayOfAddress);
    if (str == null) {
      removeHeader(paramString);
    } else {
      setHeader(paramString, str);
    }
  }
  
  private void addAddressHeader(String paramString, Address[] paramArrayOfAddress) throws MessagingException {
    String str = InternetAddress.toString(paramArrayOfAddress);
    if (str == null)
      return;
    addHeader(paramString, str);
  }
  













  public String getSubject()
    throws MessagingException
  {
    String str = getHeader("Subject", null);
    
    if (str == null) {
      return null;
    }
    try {
      return MimeUtility.decodeText(str);
    } catch (UnsupportedEncodingException localUnsupportedEncodingException) {}
    return str;
  }
  























  public void setSubject(String paramString)
    throws MessagingException
  {
    setSubject(paramString, null);
  }
  























  public void setSubject(String paramString1, String paramString2)
    throws MessagingException
  {
    if (paramString1 == null) {
      removeHeader("Subject");
    }
    try {
      setHeader("Subject", 
        MimeUtility.encodeText(paramString1, paramString2, null));
    } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
      throw new MessagingException("Encoding error", localUnsupportedEncodingException);
    }
  }
  









  public Date getSentDate()
    throws MessagingException
  {
    String str = getHeader("Date", null);
    if (str != null) {
      try {
        synchronized (mailDateFormat) {
          Date localDate = mailDateFormat.parse(str);return localDate;
        }
      } catch (ParseException localParseException) {
        return null;
      }
    }
    
    return null;
  }
  










  public void setSentDate(Date paramDate)
    throws MessagingException
  {
    if (paramDate == null) {
      removeHeader("Date");
    } else {
      synchronized (mailDateFormat) {
        setHeader("Date", mailDateFormat.format(paramDate));
      }
    }
  }
  











  public Date getReceivedDate()
    throws MessagingException
  {
    return null;
  }
  















  public int getSize()
    throws MessagingException
  {
    if (content != null)
      return content.length;
    if (contentStream != null) {
      try {
        int i = contentStream.available();
        

        if (i > 0) {
          return i;
        }
      }
      catch (IOException localIOException) {}
    }
    return -1;
  }
  











  public int getLineCount()
    throws MessagingException
  {
    return -1;
  }
  











  public String getContentType()
    throws MessagingException
  {
    String str = getHeader("Content-Type", null);
    if (str == null)
      return "text/plain";
    return str;
  }
  












  public boolean isMimeType(String paramString)
    throws MessagingException
  {
    return MimeBodyPart.isMimeType(this, paramString);
  }
  












  public String getDisposition()
    throws MessagingException
  {
    return MimeBodyPart.getDisposition(this);
  }
  









  public void setDisposition(String paramString)
    throws MessagingException
  {
    MimeBodyPart.setDisposition(this, paramString);
  }
  










  public String getEncoding()
    throws MessagingException
  {
    return MimeBodyPart.getEncoding(this);
  }
  









  public String getContentID()
    throws MessagingException
  {
    return getHeader("Content-Id", null);
  }
  









  public void setContentID(String paramString)
    throws MessagingException
  {
    if (paramString == null) {
      removeHeader("Content-ID");
    } else {
      setHeader("Content-ID", paramString);
    }
  }
  








  public String getContentMD5()
    throws MessagingException
  {
    return getHeader("Content-MD5", null);
  }
  







  public void setContentMD5(String paramString)
    throws MessagingException
  {
    setHeader("Content-MD5", paramString);
  }
  














  public String getDescription()
    throws MessagingException
  {
    return MimeBodyPart.getDescription(this);
  }
  























  public void setDescription(String paramString)
    throws MessagingException
  {
    setDescription(paramString, null);
  }
  

























  public void setDescription(String paramString1, String paramString2)
    throws MessagingException
  {
    MimeBodyPart.setDescription(this, paramString1, paramString2);
  }
  










  public String[] getContentLanguage()
    throws MessagingException
  {
    return MimeBodyPart.getContentLanguage(this);
  }
  










  public void setContentLanguage(String[] paramArrayOfString)
    throws MessagingException
  {
    MimeBodyPart.setContentLanguage(this, paramArrayOfString);
  }
  












  public String getMessageID()
    throws MessagingException
  {
    return getHeader("Message-ID", null);
  }
  










  public String getFileName()
    throws MessagingException
  {
    return MimeBodyPart.getFileName(this);
  }
  










  public void setFileName(String paramString)
    throws MessagingException
  {
    MimeBodyPart.setFileName(this, paramString);
  }
  
  private String getHeaderName(Message.RecipientType paramRecipientType)
    throws MessagingException
  {
    String str;
    if (paramRecipientType == Message.RecipientType.TO) {
      str = "To";
    } else if (paramRecipientType == Message.RecipientType.CC) {
      str = "Cc";
    } else if (paramRecipientType == Message.RecipientType.BCC) {
      str = "Bcc";
    } else if (paramRecipientType == RecipientType.NEWSGROUPS) {
      str = "Newsgroups";
    } else
      throw new MessagingException("Invalid Recipient Type");
    return str;
  }
  















  public InputStream getInputStream()
    throws IOException, MessagingException
  {
    return getDataHandler().getInputStream();
  }
  









  protected InputStream getContentStream()
    throws MessagingException
  {
    if (contentStream != null)
      return ((SharedInputStream)contentStream).newStream(0L, -1L);
    if (content != null) {
      return new SharedByteArrayInputStream(content);
    }
    throw new MessagingException("No content");
  }
  













  public InputStream getRawInputStream()
    throws MessagingException
  {
    return getContentStream();
  }
  



























  public synchronized DataHandler getDataHandler()
    throws MessagingException
  {
    if (dh == null)
      dh = new DataHandler(new MimePartDataSource(this));
    return dh;
  }
  


















  public Object getContent()
    throws IOException, MessagingException
  {
    return getDataHandler().getContent();
  }
  










  public void setDataHandler(DataHandler paramDataHandler)
    throws MessagingException
  {
    dh = paramDataHandler;
    MimeBodyPart.invalidateContentHeaders(this);
  }
  


















  public void setContent(Object paramObject, String paramString)
    throws MessagingException
  {
    setDataHandler(new DataHandler(paramObject, paramString));
  }
  















  public void setText(String paramString)
    throws MessagingException
  {
    setText(paramString, null);
  }
  






  public void setText(String paramString1, String paramString2)
    throws MessagingException
  {
    MimeBodyPart.setText(this, paramString1, paramString2);
  }
  









  public void setContent(Multipart paramMultipart)
    throws MessagingException
  {
    setDataHandler(new DataHandler(paramMultipart, paramMultipart.getContentType()));
    paramMultipart.setParent(this);
  }
  





















  public Message reply(boolean paramBoolean)
    throws MessagingException
  {
    MimeMessage localMimeMessage = new MimeMessage(session);
    






    String str1 = getHeader("Subject", null);
    if (str1 != null) {
      if (!str1.regionMatches(true, 0, "Re: ", 0, 4))
        str1 = "Re: " + str1;
      localMimeMessage.setHeader("Subject", str1);
    }
    Address[] arrayOfAddress = getReplyTo();
    localMimeMessage.setRecipients(Message.RecipientType.TO, arrayOfAddress);
    if (paramBoolean) {
      localObject = new Vector();
      
      InternetAddress localInternetAddress = InternetAddress.getLocalAddress(session);
      if (localInternetAddress != null) {
        ((Vector)localObject).addElement(localInternetAddress);
      }
      String str2 = session.getProperty("mail.alternates");
      if (str2 != null) {
        eliminateDuplicates((Vector)localObject, 
          InternetAddress.parse(str2, false));
      }
      String str3 = session.getProperty("mail.replyallcc");
      int i = 
        (str3 == null) || (!str3.equalsIgnoreCase("true")) ? 0 : 1;
      
      eliminateDuplicates((Vector)localObject, arrayOfAddress);
      arrayOfAddress = getRecipients(Message.RecipientType.TO);
      arrayOfAddress = eliminateDuplicates((Vector)localObject, arrayOfAddress);
      if ((arrayOfAddress != null) && (arrayOfAddress.length > 0)) {
        if (i != 0) {
          localMimeMessage.addRecipients(Message.RecipientType.CC, arrayOfAddress);
        } else
          localMimeMessage.addRecipients(Message.RecipientType.TO, arrayOfAddress);
      }
      arrayOfAddress = getRecipients(Message.RecipientType.CC);
      arrayOfAddress = eliminateDuplicates((Vector)localObject, arrayOfAddress);
      if ((arrayOfAddress != null) && (arrayOfAddress.length > 0)) {
        localMimeMessage.addRecipients(Message.RecipientType.CC, arrayOfAddress);
      }
      arrayOfAddress = getRecipients(RecipientType.NEWSGROUPS);
      if ((arrayOfAddress != null) && (arrayOfAddress.length > 0))
        localMimeMessage.setRecipients(RecipientType.NEWSGROUPS, arrayOfAddress);
    }
    Object localObject = getHeader("Message-Id", null);
    if (localObject != null)
      localMimeMessage.setHeader("In-Reply-To", (String)localObject);
    try {
      setFlags(answeredFlag, true);
    }
    catch (MessagingException localMessagingException) {}
    
    return localMimeMessage;
  }
  

  private static final Flags answeredFlag = new Flags(Flags.Flag.ANSWERED);
  




  private Address[] eliminateDuplicates(Vector paramVector, Address[] paramArrayOfAddress)
  {
    if (paramArrayOfAddress == null)
      return null;
    int i = 0;
    int m; for (int j = 0; j < paramArrayOfAddress.length; j++) {
      int k = 0;
      
      for (m = 0; m < paramVector.size(); m++) {
        if (((InternetAddress)paramVector.elementAt(m)).equals(paramArrayOfAddress[j]))
        {
          k = 1;
          i++;
          paramArrayOfAddress[j] = null;
          break;
        }
      }
      if (k == 0) {
        paramVector.addElement(paramArrayOfAddress[j]);
      }
    }
    if (i != 0)
    {
      Object localObject;
      
      if ((paramArrayOfAddress instanceof InternetAddress[])) {
        localObject = new InternetAddress[paramArrayOfAddress.length - i];
      } else
        localObject = new Address[paramArrayOfAddress.length - i];
      m = 0; for (int n = 0; m < paramArrayOfAddress.length; m++)
        if (paramArrayOfAddress[m] != null)
          localObject[(n++)] = paramArrayOfAddress[m];
      paramArrayOfAddress = (Address[])localObject;
    }
    return paramArrayOfAddress;
  }
  



















  public void writeTo(OutputStream paramOutputStream)
    throws IOException, MessagingException
  {
    writeTo(paramOutputStream, null);
  }
  














  public void writeTo(OutputStream paramOutputStream, String[] paramArrayOfString)
    throws IOException, MessagingException
  {
    if (!saved) {
      saveChanges();
    }
    if ((modified) || ((content == null) && (contentStream == null))) {
      MimeBodyPart.writeTo(this, paramOutputStream, paramArrayOfString);
      return;
    }
    


    Enumeration localEnumeration = getNonMatchingHeaderLines(paramArrayOfString);
    LineOutputStream localLineOutputStream = new LineOutputStream(paramOutputStream);
    while (localEnumeration.hasMoreElements()) {
      localLineOutputStream.writeln((String)localEnumeration.nextElement());
    }
    
    localLineOutputStream.writeln();
    

    if (contentStream != null)
    {
      InputStream localInputStream = 
        ((SharedInputStream)contentStream).newStream(0L, -1L);
      byte[] arrayOfByte = new byte[' '];
      int i;
      while ((i = localInputStream.read(arrayOfByte)) > 0)
        paramOutputStream.write(arrayOfByte, 0, i);
      localInputStream.close();
      arrayOfByte = null;
    } else {
      paramOutputStream.write(content);
    }
    paramOutputStream.flush();
  }
  












  public String[] getHeader(String paramString)
    throws MessagingException
  {
    return headers.getHeader(paramString);
  }
  










  public String getHeader(String paramString1, String paramString2)
    throws MessagingException
  {
    return headers.getHeader(paramString1, paramString2);
  }
  















  public void setHeader(String paramString1, String paramString2)
    throws MessagingException
  {
    headers.setHeader(paramString1, paramString2);
  }
  














  public void addHeader(String paramString1, String paramString2)
    throws MessagingException
  {
    headers.addHeader(paramString1, paramString2);
  }
  







  public void removeHeader(String paramString)
    throws MessagingException
  {
    headers.removeHeader(paramString);
  }
  













  public Enumeration getAllHeaders()
    throws MessagingException
  {
    return headers.getAllHeaders();
  }
  






  public Enumeration getMatchingHeaders(String[] paramArrayOfString)
    throws MessagingException
  {
    return headers.getMatchingHeaders(paramArrayOfString);
  }
  






  public Enumeration getNonMatchingHeaders(String[] paramArrayOfString)
    throws MessagingException
  {
    return headers.getNonMatchingHeaders(paramArrayOfString);
  }
  







  public void addHeaderLine(String paramString)
    throws MessagingException
  {
    headers.addHeaderLine(paramString);
  }
  





  public Enumeration getAllHeaderLines()
    throws MessagingException
  {
    return headers.getAllHeaderLines();
  }
  






  public Enumeration getMatchingHeaderLines(String[] paramArrayOfString)
    throws MessagingException
  {
    return headers.getMatchingHeaderLines(paramArrayOfString);
  }
  






  public Enumeration getNonMatchingHeaderLines(String[] paramArrayOfString)
    throws MessagingException
  {
    return headers.getNonMatchingHeaderLines(paramArrayOfString);
  }
  










  public synchronized Flags getFlags()
    throws MessagingException
  {
    return (Flags)flags.clone();
  }
  

















  public synchronized boolean isSet(Flags.Flag paramFlag)
    throws MessagingException
  {
    return flags.contains(paramFlag);
  }
  










  public synchronized void setFlags(Flags paramFlags, boolean paramBoolean)
    throws MessagingException
  {
    if (paramBoolean) {
      flags.add(paramFlags);
    } else {
      flags.remove(paramFlags);
    }
  }
  





















  public void saveChanges()
    throws MessagingException
  {
    modified = true;
    saved = true;
    updateHeaders();
  }
  













  protected void updateHeaders()
    throws MessagingException
  {
    MimeBodyPart.updateHeaders(this);
    setHeader("Mime-Version", "1.0");
    setHeader("Message-ID", 
      "<" + UniqueValue.getUniqueMessageIDValue(session) + ">");
  }
  










  protected InternetHeaders createInternetHeaders(InputStream paramInputStream)
    throws MessagingException
  {
    return new InternetHeaders(paramInputStream);
  }
}
