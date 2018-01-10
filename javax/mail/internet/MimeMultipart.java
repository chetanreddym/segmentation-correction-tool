package javax.mail.internet;

import com.sun.mail.util.LineInputStream;
import com.sun.mail.util.LineOutputStream;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.MessageAware;
import javax.mail.MessageContext;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.MultipartDataSource;











































public class MimeMultipart
  extends Multipart
{
  protected DataSource ds;
  protected boolean parsed = true;
  








  public MimeMultipart()
  {
    this("mixed");
  }
  











  public MimeMultipart(String paramString)
  {
    String str = UniqueValue.getUniqueBoundaryValue();
    ContentType localContentType = new ContentType("multipart", paramString, null);
    localContentType.setParameter("boundary", str);
    contentType = localContentType.toString();
  }
  


















  public MimeMultipart(DataSource paramDataSource)
    throws MessagingException
  {
    if ((paramDataSource instanceof MessageAware)) {
      MessageContext localMessageContext = ((MessageAware)paramDataSource).getMessageContext();
      setParent(localMessageContext.getPart());
    }
    
    if ((paramDataSource instanceof MultipartDataSource))
    {
      setMultipartDataSource((MultipartDataSource)paramDataSource);
      return;
    }
    


    parsed = false;
    ds = paramDataSource;
    contentType = paramDataSource.getContentType();
  }
  






  public synchronized void setSubType(String paramString)
    throws MessagingException
  {
    ContentType localContentType = new ContentType(contentType);
    localContentType.setSubType(paramString);
    contentType = localContentType.toString();
  }
  



  public synchronized int getCount()
    throws MessagingException
  {
    parse();
    return super.getCount();
  }
  






  public synchronized BodyPart getBodyPart(int paramInt)
    throws MessagingException
  {
    parse();
    return super.getBodyPart(paramInt);
  }
  






  public synchronized BodyPart getBodyPart(String paramString)
    throws MessagingException
  {
    parse();
    
    int i = getCount();
    for (int j = 0; j < i; j++) {
      MimeBodyPart localMimeBodyPart = (MimeBodyPart)getBodyPart(j);
      String str = localMimeBodyPart.getContentID();
      if ((str != null) && (str.equals(paramString)))
        return localMimeBodyPart;
    }
    return null;
  }
  















  protected void updateHeaders()
    throws MessagingException
  {
    for (int i = 0; i < parts.size(); i++) {
      ((MimeBodyPart)parts.elementAt(i)).updateHeaders();
    }
  }
  


  public void writeTo(OutputStream paramOutputStream)
    throws IOException, MessagingException
  {
    parse();
    
    String str = "--" + 
      new ContentType(contentType).getParameter("boundary");
    LineOutputStream localLineOutputStream = new LineOutputStream(paramOutputStream);
    
    for (int i = 0; i < parts.size(); i++) {
      localLineOutputStream.writeln(str);
      ((MimeBodyPart)parts.elementAt(i)).writeTo(paramOutputStream);
      localLineOutputStream.writeln();
    }
    

    localLineOutputStream.writeln(str + "--");
  }
  







  protected synchronized void parse()
    throws MessagingException
  {
    if (parsed) {
      return;
    }
    Object localObject = null;
    SharedInputStream localSharedInputStream = null;
    long l1 = 0L;long l2 = 0L;
    try
    {
      localObject = ds.getInputStream();
      if ((!(localObject instanceof ByteArrayInputStream)) && 
        (!(localObject instanceof BufferedInputStream)))
        localObject = new BufferedInputStream((InputStream)localObject);
    } catch (Exception localException) {
      throw new MessagingException("No inputstream from datasource");
    }
    if ((localObject instanceof SharedInputStream)) {
      localSharedInputStream = (SharedInputStream)localObject;
    }
    ContentType localContentType = new ContentType(contentType);
    String str1 = "--" + localContentType.getParameter("boundary");
    int i = str1.length();
    byte[] arrayOfByte = new byte[i];
    str1.getBytes(0, i, arrayOfByte, 0);
    
    try
    {
      LineInputStream localLineInputStream = new LineInputStream((InputStream)localObject);
      String str2;
      while ((str2 = localLineInputStream.readLine()) != null) {
        if (str2.trim().equals(str1))
          break;
      }
      if (str2 == null) {
        throw new MessagingException("Missing start boundary");
      }
      



      int j = 0;
      while (j == 0) {
        InternetHeaders localInternetHeaders = null;
        if (localSharedInputStream != null) {
          l1 = localSharedInputStream.getPosition();
          
          while (((str2 = localLineInputStream.readLine()) != null) && (str2.length() > 0)) {}
          
          if (str2 == null) {
            throw new MessagingException("EOF skipping headers");
          }
        } else {
          localInternetHeaders = createInternetHeaders((InputStream)localObject);
        }
        
        if (!((InputStream)localObject).markSupported()) {
          throw new MessagingException("Stream doesn't support mark");
        }
        ByteArrayOutputStream localByteArrayOutputStream = null;
        
        if (localSharedInputStream == null) {
          localByteArrayOutputStream = new ByteArrayOutputStream();
        }
        int m = 1;
        
        int n = -1;int i1 = -1;
        


        for (;;)
        {
          if (m != 0)
          {




            ((InputStream)localObject).mark(i + 4 + 1000);
            
            for (int i2 = 0; i2 < i; i2++)
              if (((InputStream)localObject).read() != arrayOfByte[i2])
                break;
            if (i2 == i)
            {
              int i3 = ((InputStream)localObject).read();
              if ((i3 == 45) && 
                (((InputStream)localObject).read() == 45)) {
                j = 1;
                break;
              }
              

              while ((i3 == 32) || (i3 == 9)) {
                i3 = ((InputStream)localObject).read();
              }
              if (i3 == 10)
                break;
              if (i3 == 13) {
                ((InputStream)localObject).mark(1);
                if (((InputStream)localObject).read() == 10) break;
                ((InputStream)localObject).reset();
                break;
              }
            }
            
            ((InputStream)localObject).reset();
            


            if ((localByteArrayOutputStream != null) && (n != -1)) {
              localByteArrayOutputStream.write(n);
              if (i1 != -1)
                localByteArrayOutputStream.write(i1);
              n = i1 = -1;
            }
          }
          
          int k;
          if ((k = ((InputStream)localObject).read()) < 0) {
            j = 1;
            break;
          }
          




          if ((k == 13) || (k == 10)) {
            m = 1;
            if (localSharedInputStream != null)
              l2 = localSharedInputStream.getPosition() - 1L;
            n = k;
            if (k == 13) {
              ((InputStream)localObject).mark(1);
              if ((k = ((InputStream)localObject).read()) == 10) {
                i1 = k;
              } else
                ((InputStream)localObject).reset();
            }
          } else {
            m = 0;
            if (localByteArrayOutputStream != null) {
              localByteArrayOutputStream.write(k);
            }
          }
        }
        

        MimeBodyPart localMimeBodyPart;
        
        if (localSharedInputStream != null) {
          localMimeBodyPart = createMimeBodyPart(localSharedInputStream.newStream(l1, l2));
        } else
          localMimeBodyPart = createMimeBodyPart(localInternetHeaders, localByteArrayOutputStream.toByteArray());
        addBodyPart(localMimeBodyPart);
      }
    } catch (IOException localIOException) {
      throw new MessagingException("IO Error", localIOException);
    }
    
    parsed = true;
  }
  










  protected InternetHeaders createInternetHeaders(InputStream paramInputStream)
    throws MessagingException
  {
    return new InternetHeaders(paramInputStream);
  }
  











  protected MimeBodyPart createMimeBodyPart(InternetHeaders paramInternetHeaders, byte[] paramArrayOfByte)
    throws MessagingException
  {
    return new MimeBodyPart(paramInternetHeaders, paramArrayOfByte);
  }
  










  protected MimeBodyPart createMimeBodyPart(InputStream paramInputStream)
    throws MessagingException
  {
    return new MimeBodyPart(paramInputStream);
  }
}
