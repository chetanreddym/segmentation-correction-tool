package javax.mail.internet;

import com.sun.mail.util.ASCIIUtility;
import com.sun.mail.util.LineOutputStream;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Vector;
import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;


























































public class MimeBodyPart
  extends BodyPart
  implements MimePart
{
  protected DataHandler dh;
  protected byte[] content;
  protected InputStream contentStream;
  protected InternetHeaders headers;
  
  public MimeBodyPart()
  {
    headers = new InternetHeaders();
  }
  













  public MimeBodyPart(InputStream paramInputStream)
    throws MessagingException
  {
    if ((!(paramInputStream instanceof ByteArrayInputStream)) && 
      (!(paramInputStream instanceof BufferedInputStream))) {
      paramInputStream = new BufferedInputStream(paramInputStream);
    }
    headers = new InternetHeaders(paramInputStream);
    
    if ((paramInputStream instanceof SharedInputStream)) {
      SharedInputStream localSharedInputStream = (SharedInputStream)paramInputStream;
      contentStream = localSharedInputStream.newStream(localSharedInputStream.getPosition(), -1L);
    } else {
      try {
        content = ASCIIUtility.getBytes(paramInputStream);
      } catch (IOException localIOException) {
        throw new MessagingException("Error reading input stream", localIOException);
      }
    }
  }
  










  public MimeBodyPart(InternetHeaders paramInternetHeaders, byte[] paramArrayOfByte)
    throws MessagingException
  {
    headers = paramInternetHeaders;
    content = paramArrayOfByte;
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
    if (str == null) {
      str = "text/plain";
    }
    return str;
  }
  












  public boolean isMimeType(String paramString)
    throws MessagingException
  {
    return isMimeType(this, paramString);
  }
  











  public String getDisposition()
    throws MessagingException
  {
    return getDisposition(this);
  }
  








  public void setDisposition(String paramString)
    throws MessagingException
  {
    setDisposition(this, paramString);
  }
  









  public String getEncoding()
    throws MessagingException
  {
    return getEncoding(this);
  }
  






  public String getContentID()
    throws MessagingException
  {
    return getHeader("Content-Id", null);
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
  







  public String[] getContentLanguage()
    throws MessagingException
  {
    return getContentLanguage(this);
  }
  





  public void setContentLanguage(String[] paramArrayOfString)
    throws MessagingException
  {
    setContentLanguage(this, paramArrayOfString);
  }
  













  public String getDescription()
    throws MessagingException
  {
    return getDescription(this);
  }
  























  public void setDescription(String paramString)
    throws MessagingException
  {
    setDescription(paramString, null);
  }
  

























  public void setDescription(String paramString1, String paramString2)
    throws MessagingException
  {
    setDescription(this, paramString1, paramString2);
  }
  









  public String getFileName()
    throws MessagingException
  {
    return getFileName(this);
  }
  









  public void setFileName(String paramString)
    throws MessagingException
  {
    setFileName(this, paramString);
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
      return new ByteArrayInputStream(content);
    }
    throw new MessagingException("No content");
  }
  













  public InputStream getRawInputStream()
    throws MessagingException
  {
    return getContentStream();
  }
  





  public DataHandler getDataHandler()
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
    invalidateContentHeaders(this);
  }
  

















  public void setContent(Object paramObject, String paramString)
    throws MessagingException
  {
    if ((paramObject instanceof Multipart)) {
      setContent((Multipart)paramObject);
    } else {
      setDataHandler(new DataHandler(paramObject, paramString));
    }
  }
  














  public void setText(String paramString)
    throws MessagingException
  {
    setText(paramString, null);
  }
  






  public void setText(String paramString1, String paramString2)
    throws MessagingException
  {
    setText(this, paramString1, paramString2);
  }
  








  public void setContent(Multipart paramMultipart)
    throws MessagingException
  {
    setDataHandler(new DataHandler(paramMultipart, paramMultipart.getContentType()));
    paramMultipart.setParent(this);
  }
  








  public void writeTo(OutputStream paramOutputStream)
    throws IOException, MessagingException
  {
    writeTo(this, paramOutputStream, null);
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
  




















  protected void updateHeaders()
    throws MessagingException
  {
    updateHeaders(this);
  }
  




  static boolean isMimeType(MimePart paramMimePart, String paramString)
    throws MessagingException
  {
    try
    {
      ContentType localContentType = new ContentType(paramMimePart.getContentType());
      return localContentType.match(paramString);
    } catch (ParseException localParseException) {}
    return paramMimePart.getContentType().equalsIgnoreCase(paramString);
  }
  
  static void setText(MimePart paramMimePart, String paramString1, String paramString2)
    throws MessagingException
  {
    if (paramString2 == null) {
      if (MimeUtility.checkAscii(paramString1) != 1) {
        paramString2 = MimeUtility.getDefaultMIMECharset();
      } else
        paramString2 = "us-ascii";
    }
    paramMimePart.setContent(paramString1, "text/plain; charset=" + 
      MimeUtility.quote(paramString2, "()<>@,;:\\\"\t []/?="));
  }
  
  static String getDisposition(MimePart paramMimePart) throws MessagingException {
    String str = paramMimePart.getHeader("Content-Disposition", null);
    
    if (str == null) {
      return null;
    }
    ContentDisposition localContentDisposition = new ContentDisposition(str);
    return localContentDisposition.getDisposition();
  }
  
  static void setDisposition(MimePart paramMimePart, String paramString) throws MessagingException
  {
    if (paramString == null) {
      paramMimePart.removeHeader("Content-Disposition");
    } else {
      String str = paramMimePart.getHeader("Content-Disposition", null);
      if (str != null)
      {




        ContentDisposition localContentDisposition = new ContentDisposition(str);
        localContentDisposition.setDisposition(paramString);
        paramString = localContentDisposition.toString();
      }
      paramMimePart.setHeader("Content-Disposition", paramString);
    }
  }
  
  static String getDescription(MimePart paramMimePart) throws MessagingException
  {
    String str = paramMimePart.getHeader("Content-Description", null);
    
    if (str == null) {
      return null;
    }
    try {
      return MimeUtility.decodeText(str);
    } catch (UnsupportedEncodingException localUnsupportedEncodingException) {}
    return str;
  }
  

  static void setDescription(MimePart paramMimePart, String paramString1, String paramString2)
    throws MessagingException
  {
    if (paramString1 == null) {
      paramMimePart.removeHeader("Content-Description");
      return;
    }
    try
    {
      paramMimePart.setHeader("Content-Description", 
        MimeUtility.encodeText(paramString1, paramString2, null));
    } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
      throw new MessagingException("Encoding error", localUnsupportedEncodingException);
    }
  }
  
  static String getFileName(MimePart paramMimePart) throws MessagingException {
    String str1 = null;
    String str2 = paramMimePart.getHeader("Content-Disposition", null);
    Object localObject;
    if (str2 != null)
    {
      localObject = new ContentDisposition(str2);
      str1 = ((ContentDisposition)localObject).getParameter("filename");
    }
    if (str1 == null)
    {
      str2 = paramMimePart.getHeader("Content-Type", null);
      if (str2 != null) {
        try {
          localObject = new ContentType(str2);
          str1 = ((ContentType)localObject).getParameter("name");
        } catch (ParseException localParseException) {}
      }
    }
    return str1;
  }
  
  static void setFileName(MimePart paramMimePart, String paramString)
    throws MessagingException
  {
    String str = paramMimePart.getHeader("Content-Disposition", null);
    ContentDisposition localContentDisposition = 
      new ContentDisposition(str == null ? "attachment" : str);
    localContentDisposition.setParameter("filename", paramString);
    paramMimePart.setHeader("Content-Disposition", localContentDisposition.toString());
    






    str = paramMimePart.getHeader("Content-Type", null);
    if (str != null) {
      try {
        ContentType localContentType = new ContentType(str);
        localContentType.setParameter("name", paramString);
        paramMimePart.setHeader("Content-Type", localContentType.toString());
      }
      catch (ParseException localParseException) {}
    }
  }
  
  static String[] getContentLanguage(MimePart paramMimePart) throws MessagingException {
    String str = paramMimePart.getHeader("Content-Language", null);
    
    if (str == null) {
      return null;
    }
    
    HeaderTokenizer localHeaderTokenizer = new HeaderTokenizer(str, "()<>@,;:\\\"\t []/?=");
    Vector localVector = new Vector();
    


    for (;;)
    {
      HeaderTokenizer.Token localToken = localHeaderTokenizer.next();
      int i = localToken.getType();
      if (i == -4)
        break;
      if (i == -1) {
        localVector.addElement(localToken.getValue());
      }
    }
    

    if (localVector.size() == 0) {
      return null;
    }
    String[] arrayOfString = new String[localVector.size()];
    localVector.copyInto(arrayOfString);
    return arrayOfString;
  }
  
  static void setContentLanguage(MimePart paramMimePart, String[] paramArrayOfString) throws MessagingException
  {
    StringBuffer localStringBuffer = new StringBuffer(paramArrayOfString[0]);
    for (int i = 1; i < paramArrayOfString.length; i++)
      localStringBuffer.append(',').append(paramArrayOfString[i]);
    paramMimePart.setHeader("Content-Language", localStringBuffer.toString());
  }
  
  static String getEncoding(MimePart paramMimePart) throws MessagingException {
    String str = paramMimePart.getHeader("Content-Transfer-Encoding", null);
    
    if (str == null) {
      return null;
    }
    str = str.trim();
    

    if ((str.equalsIgnoreCase("7bit")) || (str.equalsIgnoreCase("8bit")) || 
      (str.equalsIgnoreCase("quoted-printable")) || 
      (str.equalsIgnoreCase("base64"))) {
      return str;
    }
    
    HeaderTokenizer localHeaderTokenizer = new HeaderTokenizer(str, "()<>@,;:\\\"\t []/?=");
    
    HeaderTokenizer.Token localToken;
    int i;
    do
    {
      localToken = localHeaderTokenizer.next();
      i = localToken.getType();
      if (i == -4)
        break;
    } while (i != -1);
    return localToken.getValue();
    


    return str;
  }
  
  static void setEncoding(MimePart paramMimePart, String paramString) throws MessagingException
  {
    paramMimePart.setHeader("Content-Transfer-Encoding", paramString);
  }
  
  static void updateHeaders(MimePart paramMimePart) throws MessagingException {
    DataHandler localDataHandler = paramMimePart.getDataHandler();
    if (localDataHandler == null) {
      return;
    }
    try {
      String str1 = localDataHandler.getContentType();
      int i = 0;
      
      ContentType localContentType = new ContentType(str1);
      Object localObject; if (localContentType.match("multipart/*"))
      {
        i = 1;
        localObject = localDataHandler.getContent();
        ((MimeMultipart)localObject).updateHeaders();
      }
      else if (localContentType.match("message/rfc822")) {
        i = 1;
      }
      



      if (paramMimePart.getHeader("Content-Type") == null)
      {





        localObject = paramMimePart.getHeader("Content-Disposition", null);
        if (localObject != null)
        {
          ContentDisposition localContentDisposition = new ContentDisposition((String)localObject);
          String str2 = localContentDisposition.getParameter("filename");
          if (str2 != null) {
            localContentType.setParameter("name", str2);
            str1 = localContentType.toString();
          }
        }
        
        paramMimePart.setHeader("Content-Type", str1);
      }
      


      if ((i == 0) && 
        (paramMimePart.getHeader("Content-Transfer-Encoding") == null))
        setEncoding(paramMimePart, MimeUtility.getEncoding(localDataHandler));
    } catch (IOException localIOException) {
      throw new MessagingException("IOException updating headers", localIOException);
    }
  }
  
  static void invalidateContentHeaders(MimePart paramMimePart) throws MessagingException
  {
    paramMimePart.removeHeader("Content-Type");
    paramMimePart.removeHeader("Content-Transfer-Encoding");
  }
  

  static void writeTo(MimePart paramMimePart, OutputStream paramOutputStream, String[] paramArrayOfString)
    throws IOException, MessagingException
  {
    LineOutputStream localLineOutputStream = null;
    if ((paramOutputStream instanceof LineOutputStream)) {
      localLineOutputStream = (LineOutputStream)paramOutputStream;
    } else {
      localLineOutputStream = new LineOutputStream(paramOutputStream);
    }
    

    Enumeration localEnumeration = paramMimePart.getNonMatchingHeaderLines(paramArrayOfString);
    while (localEnumeration.hasMoreElements()) {
      localLineOutputStream.writeln((String)localEnumeration.nextElement());
    }
    
    localLineOutputStream.writeln();
    


    paramOutputStream = MimeUtility.encode(paramOutputStream, paramMimePart.getEncoding());
    paramMimePart.getDataHandler().writeTo(paramOutputStream);
    paramOutputStream.flush();
  }
}
