package com.sun.mail.imap.protocol;

import com.sun.mail.iap.ByteArray;
import com.sun.mail.iap.ParsingException;
import com.sun.mail.iap.Response;
import java.io.ByteArrayInputStream;
















public class RFC822DATA
  implements Item
{
  public static char[] name = { 'R', 'F', 'C', '8', '2', '2' };
  
  public int msgno;
  
  public ByteArray data;
  
  public RFC822DATA(FetchResponse paramFetchResponse)
    throws ParsingException
  {
    msgno = paramFetchResponse.getNumber();
    paramFetchResponse.skipSpaces();
    data = paramFetchResponse.readByteArray();
  }
  
  public ByteArray getByteArray() {
    return data;
  }
  
  public ByteArrayInputStream getByteArrayInputStream() {
    if (data != null) {
      return data.toByteArrayInputStream();
    }
    return null;
  }
}
