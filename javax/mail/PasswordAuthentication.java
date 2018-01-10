package javax.mail;










public final class PasswordAuthentication
{
  private String userName;
  








  private String password;
  









  public PasswordAuthentication(String paramString1, String paramString2)
  {
    userName = paramString1;
    password = paramString2;
  }
  


  public String getUserName()
  {
    return userName;
  }
  


  public String getPassword()
  {
    return password;
  }
}
