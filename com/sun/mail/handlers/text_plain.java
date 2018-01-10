package com.sun.mail.handlers;

import java.awt.datatransfer.DataFlavor;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;
import javax.activation.DataSource;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeUtility;







public class text_plain
  implements DataContentHandler
{
  private static ActivationDataFlavor myDF = new ActivationDataFlavor(
    String.class, 
    "text/plain", 
    "Text String");
  
  protected ActivationDataFlavor getDF() {
    return myDF;
  }
  




  public DataFlavor[] getTransferDataFlavors()
  {
    return new DataFlavor[] { getDF() };
  }
  








  public Object getTransferData(DataFlavor paramDataFlavor, DataSource paramDataSource)
    throws IOException
  {
    if (getDF().equals(paramDataFlavor)) {
      return getContent(paramDataSource);
    }
    return null;
  }
  
  public Object getContent(DataSource paramDataSource) throws IOException {
    String str = null;
    InputStreamReader localInputStreamReader = null;
    try
    {
      str = getCharset(paramDataSource.getContentType());
      localInputStreamReader = new InputStreamReader(paramDataSource.getInputStream(), str);



    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {



      throw new UnsupportedEncodingException(str);
    }
    
    int i = 0;
    
    Object localObject = new char['Ѐ'];
    int j;
    while ((j = localInputStreamReader.read((char[])localObject, i, 1024)) != -1) {
      i += j;
      char[] arrayOfChar = new char[i + 1024];
      System.arraycopy(localObject, 0, arrayOfChar, 0, i);
      localObject = arrayOfChar;
    }
    return new String((char[])localObject, 0, i);
  }
  


  public void writeTo(Object paramObject, String paramString, OutputStream paramOutputStream)
    throws IOException
  {
    if (!(paramObject instanceof String)) {
      throw new IOException("\"" + getDF().getMimeType() + 
        "\" DataContentHandler requires String object, " + 
        "was given object of type " + paramObject.getClass().toString());
    }
    String str1 = null;
    OutputStreamWriter localOutputStreamWriter = null;
    try
    {
      str1 = getCharset(paramString);
      localOutputStreamWriter = new OutputStreamWriter(paramOutputStream, str1);



    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {



      throw new UnsupportedEncodingException(str1);
    }
    
    String str2 = (String)paramObject;
    localOutputStreamWriter.write(str2, 0, str2.length());
    localOutputStreamWriter.flush();
  }
  
  private String getCharset(String paramString) {
    try {
      ContentType localContentType = new ContentType(paramString);
      String str = localContentType.getParameter("charset");
      if (str == null)
      {
        str = "us-ascii"; }
      return MimeUtility.javaCharset(str);
    } catch (Exception localException) {}
    return null;
  }
  
  public text_plain() {}
}
