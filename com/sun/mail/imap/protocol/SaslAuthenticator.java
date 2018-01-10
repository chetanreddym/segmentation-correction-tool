package com.sun.mail.imap.protocol;

import com.sun.mail.iap.ProtocolException;

public abstract interface SaslAuthenticator
{
  public abstract boolean authenticate(String[] paramArrayOfString, String paramString1, String paramString2, String paramString3, String paramString4)
    throws ProtocolException;
}
