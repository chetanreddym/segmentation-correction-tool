package com.sun.mail.imap;

import javax.mail.Session;
import javax.mail.URLName;

































public class IMAPSSLStore
  extends IMAPStore
{
  public IMAPSSLStore(Session paramSession, URLName paramURLName)
  {
    super(paramSession, paramURLName, "imaps", 993, true);
  }
}
