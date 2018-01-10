package com.sun.mail.smtp;

import java.io.InputStream;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;































public class SMTPMessage
  extends MimeMessage
{
  public static final int NOTIFY_NEVER = -1;
  public static final int NOTIFY_SUCCESS = 1;
  public static final int NOTIFY_FAILURE = 2;
  public static final int NOTIFY_DELAY = 4;
  public static final int RETURN_FULL = 1;
  public static final int RETURN_HDRS = 2;
  private static final String[] returnOptionString = { null, "FULL", "HDRS" };
  
  private String envelopeFrom;
  private int notifyOptions;
  private int returnOption;
  private boolean sendPartial = false;
  private boolean allow8bitMIME = false;
  





  public SMTPMessage(Session paramSession)
  {
    super(paramSession);
  }
  









  public SMTPMessage(Session paramSession, InputStream paramInputStream)
    throws MessagingException
  {
    super(paramSession, paramInputStream);
  }
  









  public SMTPMessage(MimeMessage paramMimeMessage)
    throws MessagingException
  {
    super(paramMimeMessage);
  }
  










  public void setEnvelopeFrom(String paramString)
  {
    envelopeFrom = paramString;
  }
  




  public String getEnvelopeFrom()
  {
    return envelopeFrom;
  }
  











  public void setNotifyOptions(int paramInt)
  {
    if ((paramInt < -1) || (paramInt >= 8))
      throw new IllegalArgumentException("Bad return option");
    notifyOptions = paramInt;
  }
  




  public int getNotifyOptions()
  {
    return notifyOptions;
  }
  



  String getDSNNotify()
  {
    if (notifyOptions == 0)
      return null;
    if (notifyOptions == -1)
      return "NEVER";
    StringBuffer localStringBuffer = new StringBuffer();
    if ((notifyOptions & 0x1) != 0)
      localStringBuffer.append("SUCCESS");
    if ((notifyOptions & 0x2) != 0) {
      if (localStringBuffer.length() != 0)
        localStringBuffer.append(',');
      localStringBuffer.append("FAILURE");
    }
    if ((notifyOptions & 0x4) != 0) {
      if (localStringBuffer.length() != 0)
        localStringBuffer.append(',');
      localStringBuffer.append("DELAY");
    }
    return localStringBuffer.toString();
  }
  









  public void setReturnOption(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > 2))
      throw new IllegalArgumentException("Bad return option");
    returnOption = paramInt;
  }
  




  public int getReturnOption()
  {
    return returnOption;
  }
  



  String getDSNRet()
  {
    return returnOptionString[returnOption];
  }
  









  public void setAllow8bitMIME(boolean paramBoolean)
  {
    allow8bitMIME = paramBoolean;
  }
  




  public boolean getAllow8bitMIME()
  {
    return allow8bitMIME;
  }
  










  public void setSendPartial(boolean paramBoolean)
  {
    sendPartial = paramBoolean;
  }
  




  public boolean getSendPartial()
  {
    return sendPartial;
  }
}
