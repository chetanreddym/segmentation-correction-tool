package javax.mail.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataSource;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeUtility;
import javax.mail.internet.ParseException;






























public class ByteArrayDataSource
  implements DataSource
{
  private byte[] data;
  private String type;
  private String name = "";
  








  public ByteArrayDataSource(InputStream paramInputStream, String paramString)
    throws IOException
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    byte[] arrayOfByte = new byte[' '];
    int i;
    while ((i = paramInputStream.read(arrayOfByte)) > 0)
      localByteArrayOutputStream.write(arrayOfByte, 0, i);
    data = localByteArrayOutputStream.toByteArray();
    type = paramString;
  }
  






  public ByteArrayDataSource(byte[] paramArrayOfByte, String paramString)
  {
    data = paramArrayOfByte;
    type = paramString;
  }
  










  public ByteArrayDataSource(String paramString1, String paramString2)
    throws IOException
  {
    String str = null;
    try {
      ContentType localContentType = new ContentType(paramString2);
      str = localContentType.getParameter("charset");
    } catch (ParseException localParseException) {}
    if (str == null) {
      str = MimeUtility.getDefaultJavaCharset();
    }
    data = paramString1.getBytes(str);
    type = paramString2;
  }
  






  public InputStream getInputStream()
    throws IOException
  {
    if (data == null)
      throw new IOException("no data");
    return new ByteArrayInputStream(data);
  }
  





  public OutputStream getOutputStream()
    throws IOException
  {
    throw new IOException("cannot do this");
  }
  




  public String getContentType()
  {
    return type;
  }
  





  public String getName()
  {
    return name;
  }
  




  public void setName(String paramString)
  {
    name = paramString;
  }
}
