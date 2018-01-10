package com.sun.mail.smtp;

import javax.mail.Address;
import javax.mail.SendFailedException;
import javax.mail.internet.InternetAddress;
















































public class SMTPSendFailedException
  extends SendFailedException
{
  protected InternetAddress addr;
  protected String cmd;
  protected int rc;
  private static final long serialVersionUID = 8049122628728932894L;
  
  public SMTPSendFailedException(String paramString1, int paramInt, String paramString2, Exception paramException, Address[] paramArrayOfAddress1, Address[] paramArrayOfAddress2, Address[] paramArrayOfAddress3)
  {
    super(paramString2, paramException, paramArrayOfAddress1, paramArrayOfAddress2, paramArrayOfAddress3);
    cmd = paramString1;
    rc = paramInt;
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
