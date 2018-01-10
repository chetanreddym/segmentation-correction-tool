package com.sun.mail.imap.protocol;

import com.sun.mail.iap.ByteArray;
import com.sun.mail.iap.ParsingException;
import com.sun.mail.iap.Response;
import java.io.ByteArrayInputStream;
















public class BODY
  implements Item
{
  public static char[] name = { 'B', 'O', 'D', 'Y' };
  
  public int msgno;
  
  public ByteArray data;
  public String section;
  public int origin;
  
  public BODY(FetchResponse paramFetchResponse)
    throws ParsingException
  {
    msgno = paramFetchResponse.getNumber();
    
    paramFetchResponse.skipSpaces();
    
    while (paramFetchResponse.readByte() != 93) {}
    

    if (paramFetchResponse.readByte() == 60) {
      origin = paramFetchResponse.readNumber();
      paramFetchResponse.skip(1);
    }
    
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
