package com.sun.mail.imap;









public class ACL
  implements Cloneable
{
  private String name;
  







  private Rights rights;
  








  public ACL(String paramString)
  {
    name = paramString;
    rights = new Rights();
  }
  





  public ACL(String paramString, Rights paramRights)
  {
    name = paramString;
    rights = paramRights;
  }
  




  public String getName()
  {
    return name;
  }
  




  public void setRights(Rights paramRights)
  {
    rights = paramRights;
  }
  






  public Rights getRights()
  {
    return rights;
  }
  

  public Object clone()
    throws CloneNotSupportedException
  {
    ACL localACL = (ACL)super.clone();
    rights = ((Rights)rights.clone());
    return localACL;
  }
}
