package javax.mail;

import java.net.InetAddress;
























































public abstract class Authenticator
{
  private InetAddress requestingSite;
  private int requestingPort;
  private String requestingProtocol;
  private String requestingPrompt;
  private String requestingUserName;
  
  private void reset()
  {
    requestingSite = null;
    requestingPort = -1;
    requestingProtocol = null;
    requestingPrompt = null;
    requestingUserName = null;
  }
  















  final PasswordAuthentication requestPasswordAuthentication(InetAddress paramInetAddress, int paramInt, String paramString1, String paramString2, String paramString3)
  {
    reset();
    requestingSite = paramInetAddress;
    requestingPort = paramInt;
    requestingProtocol = paramString1;
    requestingPrompt = paramString2;
    requestingUserName = paramString3;
    return getPasswordAuthentication();
  }
  



  protected final InetAddress getRequestingSite()
  {
    return requestingSite;
  }
  


  protected final int getRequestingPort()
  {
    return requestingPort;
  }
  







  protected final String getRequestingProtocol()
  {
    return requestingProtocol;
  }
  


  protected final String getRequestingPrompt()
  {
    return requestingPrompt;
  }
  


  protected final String getDefaultUserName()
  {
    return requestingUserName;
  }
  










  protected PasswordAuthentication getPasswordAuthentication()
  {
    return null;
  }
  
  public Authenticator() {}
}
