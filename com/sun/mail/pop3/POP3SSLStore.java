package com.sun.mail.pop3;

import javax.mail.Session;
import javax.mail.URLName;






























public class POP3SSLStore
  extends POP3Store
{
  public POP3SSLStore(Session paramSession, URLName paramURLName)
  {
    super(paramSession, paramURLName, "pop3s", 995, true);
  }
}
