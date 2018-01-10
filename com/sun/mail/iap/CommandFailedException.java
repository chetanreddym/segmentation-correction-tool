package com.sun.mail.iap;












public class CommandFailedException
  extends ProtocolException
{
  public CommandFailedException() {}
  










  public CommandFailedException(String paramString)
  {
    super(paramString);
  }
  



  public CommandFailedException(Response paramResponse)
  {
    super(paramResponse);
  }
}
