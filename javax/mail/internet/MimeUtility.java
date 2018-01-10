package javax.mail.internet;

import com.sun.mail.util.ASCIIUtility;
import com.sun.mail.util.BASE64DecoderStream;
import com.sun.mail.util.BASE64EncoderStream;
import com.sun.mail.util.BEncoderStream;
import com.sun.mail.util.LineInputStream;
import com.sun.mail.util.QDecoderStream;
import com.sun.mail.util.QEncoderStream;
import com.sun.mail.util.QPDecoderStream;
import com.sun.mail.util.QPEncoderStream;
import com.sun.mail.util.UUDecoderStream;
import com.sun.mail.util.UUEncoderStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.MessagingException;
























































public class MimeUtility
{
  public static final int ALL = -1;
  private static String defaultJavaCharset;
  private static String defaultMIMECharset;
  private static Hashtable mime2java;
  private static Hashtable java2mime;
  static final int ALL_ASCII = 1;
  static final int MOSTLY_ASCII = 2;
  static final int MOSTLY_NONASCII = 3;
  
  public static String getEncoding(DataSource paramDataSource)
  {
    ContentType localContentType = null;
    InputStream localInputStream = null;
    String str = null;
    try
    {
      localContentType = new ContentType(paramDataSource.getContentType());
      localInputStream = paramDataSource.getInputStream();
    } catch (Exception localException) {
      return "base64";
    }
    
    if (localContentType.match("text/*"))
    {
      int i = checkAscii(localInputStream, -1, false);
      switch (i) {
      case 1: 
        str = "7bit";
        break;
      case 2: 
        str = "quoted-printable";
        break;
      default: 
        str = "base64";
        break;
      
      }
      
    }
    else if (checkAscii(localInputStream, -1, true) == 1) {
      str = "7bit";
    } else {
      str = "base64";
    }
    
    try
    {
      localInputStream.close();
    }
    catch (IOException localIOException) {}
    return str;
  }
  












  public static String getEncoding(DataHandler paramDataHandler)
  {
    ContentType localContentType = null;
    String str = null;
    













    if (paramDataHandler.getName() != null) {
      return getEncoding(paramDataHandler.getDataSource());
    }
    try {
      localContentType = new ContentType(paramDataHandler.getContentType());
    } catch (Exception localException) {
      return "base64";
    }
    AsciiOutputStream localAsciiOutputStream;
    if (localContentType.match("text/*"))
    {
      localAsciiOutputStream = new AsciiOutputStream(false);
      try {
        paramDataHandler.writeTo(localAsciiOutputStream);
      } catch (IOException localIOException1) {}
      switch (localAsciiOutputStream.getAscii()) {
      case 1: 
        str = "7bit";
        break;
      case 2: 
        str = "quoted-printable";
        break;
      default: 
        str = "base64";
        break;
      }
    }
    else
    {
      localAsciiOutputStream = new AsciiOutputStream(true);
      try {
        paramDataHandler.writeTo(localAsciiOutputStream);
      } catch (IOException localIOException2) {}
      if (localAsciiOutputStream.getAscii() == 1) {
        str = "7bit";
      } else {
        str = "base64";
      }
    }
    return str;
  }
  










  public static InputStream decode(InputStream paramInputStream, String paramString)
    throws MessagingException
  {
    if (paramString.equalsIgnoreCase("base64"))
      return new BASE64DecoderStream(paramInputStream);
    if (paramString.equalsIgnoreCase("quoted-printable"))
      return new QPDecoderStream(paramInputStream);
    if ((paramString.equalsIgnoreCase("uuencode")) || 
      (paramString.equalsIgnoreCase("x-uuencode")))
      return new UUDecoderStream(paramInputStream);
    if ((paramString.equalsIgnoreCase("binary")) || 
      (paramString.equalsIgnoreCase("7bit")) || 
      (paramString.equalsIgnoreCase("8bit"))) {
      return paramInputStream;
    }
    throw new MessagingException("Unknown encoding: " + paramString);
  }
  










  public static OutputStream encode(OutputStream paramOutputStream, String paramString)
    throws MessagingException
  {
    if (paramString == null)
      return paramOutputStream;
    if (paramString.equalsIgnoreCase("base64"))
      return new BASE64EncoderStream(paramOutputStream);
    if (paramString.equalsIgnoreCase("quoted-printable"))
      return new QPEncoderStream(paramOutputStream);
    if ((paramString.equalsIgnoreCase("uuencode")) || 
      (paramString.equalsIgnoreCase("x-uuencode")))
      return new UUEncoderStream(paramOutputStream);
    if ((paramString.equalsIgnoreCase("binary")) || 
      (paramString.equalsIgnoreCase("7bit")) || 
      (paramString.equalsIgnoreCase("8bit"))) {
      return paramOutputStream;
    }
    throw new MessagingException("Unknown encoding: " + paramString);
  }
  
















  public static OutputStream encode(OutputStream paramOutputStream, String paramString1, String paramString2)
    throws MessagingException
  {
    if (paramString1 == null)
      return paramOutputStream;
    if (paramString1.equalsIgnoreCase("base64"))
      return new BASE64EncoderStream(paramOutputStream);
    if (paramString1.equalsIgnoreCase("quoted-printable"))
      return new QPEncoderStream(paramOutputStream);
    if ((paramString1.equalsIgnoreCase("uuencode")) || 
      (paramString1.equalsIgnoreCase("x-uuencode")))
      return new UUEncoderStream(paramOutputStream, paramString2);
    if ((paramString1.equalsIgnoreCase("binary")) || 
      (paramString1.equalsIgnoreCase("7bit")) || 
      (paramString1.equalsIgnoreCase("8bit"))) {
      return paramOutputStream;
    }
    throw new MessagingException("Unknown encoding: " + paramString1);
  }
  



































  public static String encodeText(String paramString)
    throws UnsupportedEncodingException
  {
    return encodeText(paramString, null, null);
  }
  

























  public static String encodeText(String paramString1, String paramString2, String paramString3)
    throws UnsupportedEncodingException
  {
    return encodeWord(paramString1, paramString2, paramString3, false);
  }
  




































  public static String decodeText(String paramString)
    throws UnsupportedEncodingException
  {
    String str1 = " \t\n\r";
    










    if (paramString.indexOf("=?") == -1) {
      return paramString;
    }
    

    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, str1, true);
    StringBuffer localStringBuffer1 = new StringBuffer();
    StringBuffer localStringBuffer2 = new StringBuffer();
    int i = 0;
    
    while (localStringTokenizer.hasMoreTokens())
    {
      String str2 = localStringTokenizer.nextToken();
      char c;
      if (((c = str2.charAt(0)) == ' ') || (c == '\t') || 
        (c == '\r') || (c == '\n')) {
        localStringBuffer2.append(c);
      }
      else {
        String str3;
        try {
          str3 = decodeWord(str2);
          
          if ((i == 0) && (localStringBuffer2.length() > 0))
          {


            localStringBuffer1.append(localStringBuffer2);
          }
          i = 1;
        }
        catch (ParseException localParseException) {
          str3 = str2;
          
          if (localStringBuffer2.length() > 0)
            localStringBuffer1.append(localStringBuffer2);
          i = 0;
        }
        localStringBuffer1.append(str3);
        localStringBuffer2.setLength(0);
      }
    }
    return localStringBuffer1.toString();
  }
  




















  public static String encodeWord(String paramString)
    throws UnsupportedEncodingException
  {
    return encodeWord(paramString, null, null);
  }
  






















  public static String encodeWord(String paramString1, String paramString2, String paramString3)
    throws UnsupportedEncodingException
  {
    return encodeWord(paramString1, paramString2, paramString3, true);
  }
  










  private static String encodeWord(String paramString1, String paramString2, String paramString3, boolean paramBoolean)
    throws UnsupportedEncodingException
  {
    if (checkAscii(paramString1) == 1) {
      return paramString1;
    }
    
    String str;
    if (paramString2 == null) {
      str = getDefaultJavaCharset();
      paramString2 = getDefaultMIMECharset();
    } else {
      str = javaCharset(paramString2);
    }
    
    if (paramString3 == null) {
      byte[] arrayOfByte = paramString1.getBytes(str);
      if (checkAscii(arrayOfByte) != 3) {
        paramString3 = "Q";
      } else {
        paramString3 = "B";
      }
    }
    boolean bool;
    if (paramString3.equalsIgnoreCase("B")) {
      bool = true;
    } else if (paramString3.equalsIgnoreCase("Q")) {
      bool = false;
    } else {
      throw new UnsupportedEncodingException(
        "Unknown transfer encoding: " + paramString3);
    }
    StringBuffer localStringBuffer = new StringBuffer();
    doEncode(paramString1, bool, str, 
    


      68 - paramString2.length(), 
      "=?" + paramString2 + "?" + paramString3 + "?", 
      true, paramBoolean, localStringBuffer);
    
    return localStringBuffer.toString();
  }
  




  private static void doEncode(String paramString1, boolean paramBoolean1, String paramString2, int paramInt, String paramString3, boolean paramBoolean2, boolean paramBoolean3, StringBuffer paramStringBuffer)
    throws UnsupportedEncodingException
  {
    byte[] arrayOfByte1 = paramString1.getBytes(paramString2);
    int i;
    if (paramBoolean1) {
      i = BEncoderStream.encodedLength(arrayOfByte1);
    } else {
      i = QEncoderStream.encodedLength(arrayOfByte1, paramBoolean3);
    }
    int j;
    if ((i > paramInt) && ((j = paramString1.length()) > 1))
    {

      doEncode(paramString1.substring(0, j / 2), paramBoolean1, paramString2, 
        paramInt, paramString3, paramBoolean2, paramBoolean3, paramStringBuffer);
      doEncode(paramString1.substring(j / 2, j), paramBoolean1, paramString2, 
        paramInt, paramString3, false, paramBoolean3, paramStringBuffer);
    }
    else {
      ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
      Object localObject;
      if (paramBoolean1) {
        localObject = new BEncoderStream(localByteArrayOutputStream);
      } else {
        localObject = new QEncoderStream(localByteArrayOutputStream, paramBoolean3);
      }
      try {
        ((OutputStream)localObject).write(arrayOfByte1);
        ((OutputStream)localObject).close();
      }
      catch (IOException localIOException) {}
      byte[] arrayOfByte2 = localByteArrayOutputStream.toByteArray();
      

      if (!paramBoolean2) {
        paramStringBuffer.append("\r\n ");
      }
      paramStringBuffer.append(paramString3);
      for (int k = 0; k < arrayOfByte2.length; k++)
        paramStringBuffer.append((char)arrayOfByte2[k]);
      paramStringBuffer.append("?=");
    }
  }
  













  public static String decodeWord(String paramString)
    throws ParseException, UnsupportedEncodingException
  {
    if (!paramString.startsWith("=?")) {
      throw new ParseException();
    }
    
    int i = 2;
    int j; if ((j = paramString.indexOf('?', i)) == -1)
      throw new ParseException();
    String str1 = javaCharset(paramString.substring(i, j));
    

    i = j + 1;
    if ((j = paramString.indexOf('?', i)) == -1)
      throw new ParseException();
    String str2 = paramString.substring(i, j);
    

    i = j + 1;
    if ((j = paramString.indexOf("?=", i)) == -1)
      throw new ParseException();
    String str3 = paramString.substring(i, j);
    
    try
    {
      ByteArrayInputStream localByteArrayInputStream = 
        new ByteArrayInputStream(ASCIIUtility.getBytes(str3));
      
      Object localObject;
      
      if (str2.equalsIgnoreCase("B")) {
        localObject = new BASE64DecoderStream(localByteArrayInputStream);
      } else if (str2.equalsIgnoreCase("Q")) {
        localObject = new QDecoderStream(localByteArrayInputStream);
      } else {
        throw new UnsupportedEncodingException(
          "unknown encoding: " + str2);
      }
      




      int k = localByteArrayInputStream.available();
      byte[] arrayOfByte = new byte[k];
      
      k = ((InputStream)localObject).read(arrayOfByte, 0, k);
      


      return new String(arrayOfByte, 0, k, str1);
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      throw localUnsupportedEncodingException;
    }
    catch (IOException localIOException) {
      throw new ParseException();



    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {


      throw new UnsupportedEncodingException();
    }
  }
  















  public static String quote(String paramString1, String paramString2)
  {
    int i = paramString1.length();
    




    int j = 0;
    for (int k = 0; k < i; k++) {
      int m = paramString1.charAt(k);
      if ((m == 34) || (m == 92) || (m == 13) || (m == 10))
      {
        StringBuffer localStringBuffer2 = new StringBuffer(i + 3);
        localStringBuffer2.append('"');
        for (int n = 0; n < i; n++) {
          char c = paramString1.charAt(n);
          if ((c == '"') || (c == '\\') || 
            (c == '\r') || (c == '\n'))
          {
            localStringBuffer2.append('\\'); }
          localStringBuffer2.append(c);
        }
        localStringBuffer2.append('"');
        return localStringBuffer2.toString(); }
      if ((m < 32) || (m >= 127) || (paramString2.indexOf(m) >= 0))
      {
        j = 1;
      }
    }
    if (j != 0) {
      StringBuffer localStringBuffer1 = new StringBuffer(i + 2);
      localStringBuffer1.append('"').append(paramString1).append('"');
      return localStringBuffer1.toString();
    }
    return paramString1;
  }
  






  public static String javaCharset(String paramString)
  {
    if ((mime2java == null) || (paramString == null))
    {
      return paramString;
    }
    String str = (String)mime2java.get(paramString.toLowerCase());
    if (str == null) return paramString; return str;
  }
  












  public static String mimeCharset(String paramString)
  {
    if ((java2mime == null) || (paramString == null))
    {
      return paramString;
    }
    String str = (String)java2mime.get(paramString.toLowerCase());
    if (str == null) return paramString; return str;
  }
  















  private class 1$NullInputStream
    extends InputStream
  {
    public int read()
    {
      return 0;
    }
    
    1$NullInputStream() {}
  }
  
  public static String getDefaultJavaCharset()
  {
    if (defaultJavaCharset == null) {
      try {
        defaultJavaCharset = System.getProperty("file.encoding", 
          "8859_1");


      }
      catch (SecurityException localSecurityException)
      {


        InputStreamReader localInputStreamReader = 
          new InputStreamReader(new 1.NullInputStream());
        defaultJavaCharset = localInputStreamReader.getEncoding();
        if (defaultJavaCharset == null) {
          defaultJavaCharset = "8859_1";
        }
      }
    }
    return defaultJavaCharset;
  }
  


  static String getDefaultMIMECharset()
  {
    if (defaultMIMECharset == null)
      defaultMIMECharset = System.getProperty("mail.mime.charset");
    if (defaultMIMECharset == null)
      defaultMIMECharset = mimeCharset(getDefaultJavaCharset());
    return defaultMIMECharset;
  }
  




  static
  {
    Object localObject = 
      MimeUtility.class.getResourceAsStream(
      "/META-INF/javamail.charset.map");
    
    if (localObject != null) {
      localObject = new LineInputStream((InputStream)localObject);
      

      java2mime = new Hashtable(20);
      loadMappings((LineInputStream)localObject, java2mime);
      

      mime2java = new Hashtable(10);
      loadMappings((LineInputStream)localObject, mime2java);
    }
  }
  
  private static void loadMappings(LineInputStream paramLineInputStream, Hashtable paramHashtable)
  {
    for (;;) {
      String str1;
      try {
        str1 = paramLineInputStream.readLine();
      } catch (IOException localIOException) {
        return;
      }
      
      if (str1 == null)
        break;
      if ((str1.startsWith("--")) && (str1.endsWith("--"))) {
        break;
      }
      

      if ((str1.trim().length() != 0) && (!str1.startsWith("#")))
      {



        StringTokenizer localStringTokenizer = new StringTokenizer(str1, " \t");
        try {
          String str2 = localStringTokenizer.nextToken();
          String str3 = localStringTokenizer.nextToken();
          paramHashtable.put(str2.toLowerCase(), str3);
        }
        catch (NoSuchElementException localNoSuchElementException) {}
      }
    }
  }
  








  static int checkAscii(String paramString)
  {
    int i = paramString.length();
    
    for (int j = 0; j < i; j++) {
      if (nonascii(paramString.charAt(j))) {
        return 3;
      }
    }
    return 1;
  }
  







  static int checkAscii(byte[] paramArrayOfByte)
  {
    int i = 0;int j = 0;
    
    for (int k = 0; k < paramArrayOfByte.length; k++)
    {


      if (nonascii(paramArrayOfByte[k] & 0xFF)) {
        j++;
      } else {
        i++;
      }
    }
    if (j == 0)
      return 1;
    if (i > j) {
      return 2;
    }
    return 3;
  }
  




















  static int checkAscii(InputStream paramInputStream, int paramInt, boolean paramBoolean)
  {
    int i = 0;int j = 0;
    
    int m = 4096;
    int n = 0;
    int i1 = 0;
    byte[] arrayOfByte = null;
    if (paramInt != 0) {
      m = paramInt == -1 ? 4096 : Math.min(paramInt, 4096);
      arrayOfByte = new byte[m];
    }
    while (paramInt != 0) {
      int k;
      try { if ((k = paramInputStream.read(arrayOfByte, 0, m)) == -1)
          break;
        for (int i2 = 0; i2 < k; i2++)
        {



          int i3 = arrayOfByte[i2] & 0xFF;
          if ((i3 == 13) || (i3 == 10)) {
            n = 0;
          } else {
            n++;
            if (n > 998)
              i1 = 1;
          }
          if (nonascii(i3)) {
            if (paramBoolean) {
              return 3;
            }
            j++;
          } else {
            i++;
          }
        }
      } catch (IOException localIOException) { break;
      }
      if (paramInt != -1) {
        paramInt -= k;
      }
    }
    if ((paramInt == 0) && (paramBoolean))
    {





      return 3;
    }
    if (j == 0)
    {
      if (i1 != 0) {
        return 2;
      }
      return 1;
    }
    if (i > j)
      return 2;
    return 3;
  }
  
  private static final boolean nonascii(int paramInt) {
    return (paramInt >= 127) || ((paramInt < 32) && (paramInt != 13) && (paramInt != 10) && (paramInt != 9));
  }
  
  private MimeUtility() {}
}
