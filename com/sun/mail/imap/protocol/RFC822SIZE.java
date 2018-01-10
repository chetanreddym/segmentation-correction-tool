package com.sun.mail.imap.protocol;

import com.sun.mail.iap.ParsingException;
import com.sun.mail.iap.Response;
















public class RFC822SIZE
  implements Item
{
  public static char[] name = { 'R', 'F', 'C', '8', '2', '2', '.', 'S', 'I', 'Z', 'E' };
  
  public int msgno;
  
  public int size;
  

  public RFC822SIZE(FetchResponse paramFetchResponse)
    throws ParsingException
  {
    msgno = paramFetchResponse.getNumber();
    paramFetchResponse.skipSpaces();
    size = paramFetchResponse.readNumber();
  }
}
