package com.sun.mail.smtp;

import javax.mail.Session;
import javax.mail.URLName;

































public class SMTPSSLTransport
  extends SMTPTransport
{
  public SMTPSSLTransport(Session paramSession, URLName paramURLName)
  {
    super(paramSession, paramURLName, "smtps", 465, true);
  }
}
