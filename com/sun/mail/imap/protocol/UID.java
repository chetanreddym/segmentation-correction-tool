package com.sun.mail.imap.protocol;

import com.sun.mail.iap.ParsingException;
import com.sun.mail.iap.Response;
















public class UID
  implements Item
{
  public static final char[] name = { 'U', 'I', 'D' };
  
  public int msgno;
  
  public long uid;
  
  public UID(FetchResponse paramFetchResponse)
    throws ParsingException
  {
    msgno = paramFetchResponse.getNumber();
    paramFetchResponse.skipSpaces();
    uid = paramFetchResponse.readLong();
  }
}
