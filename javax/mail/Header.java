package javax.mail;









public class Header
{
  private String name;
  






  private String value;
  







  public Header(String paramString1, String paramString2)
  {
    name = paramString1;
    value = paramString2;
  }
  




  public String getName()
  {
    return name;
  }
  




  public String getValue()
  {
    return value;
  }
}
