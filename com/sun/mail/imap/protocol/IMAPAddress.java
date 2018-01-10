package com.sun.mail.imap.protocol;

import com.sun.mail.iap.ParsingException;
import com.sun.mail.iap.Response;
import javax.mail.internet.InternetAddress;




































































































class IMAPAddress
  extends InternetAddress
{
  IMAPAddress(Response paramResponse)
    throws ParsingException
  {
    paramResponse.skipSpaces();
    
    if (paramResponse.readByte() != 40) {
      throw new ParsingException("ADDRESS parse error");
    }
    encodedPersonal = paramResponse.readString();
    
    paramResponse.readString();
    address = (paramResponse.readString() + "@" + paramResponse.readString());
    
    if (paramResponse.readByte() != 41) {
      throw new ParsingException("ADDRESS parse error");
    }
  }
}
