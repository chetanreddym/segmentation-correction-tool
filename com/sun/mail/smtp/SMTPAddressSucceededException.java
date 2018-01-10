package com.sun.mail.smtp;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;














































public class SMTPAddressSucceededException
  extends MessagingException
{
  protected InternetAddress addr;
  protected String cmd;
  protected int rc;
  private static final long serialVersionUID = -1168335848623096749L;
  
  public SMTPAddressSucceededException(InternetAddress paramInternetAddress, String paramString1, int paramInt, String paramString2)
  {
    super(paramString2);
    addr = paramInternetAddress;
    cmd = paramString1;
    rc = paramInt;
  }
  


  public InternetAddress getAddress()
  {
    return addr;
  }
  


  public String getCommand()
  {
    return cmd;
  }
  






  public int getReturnCode()
  {
    return rc;
  }
}
