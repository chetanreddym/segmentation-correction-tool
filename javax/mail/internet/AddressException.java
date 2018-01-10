package javax.mail.internet;














public class AddressException
  extends ParseException
{
  protected String ref;
  












  protected int pos = -1;
  




  public AddressException() {}
  




  public AddressException(String paramString)
  {
    super(paramString);
  }
  






  public AddressException(String paramString1, String paramString2)
  {
    super(paramString1);
    ref = paramString2;
  }
  




  public AddressException(String paramString1, String paramString2, int paramInt)
  {
    super(paramString1);
    ref = paramString2;
    pos = paramInt;
  }
  



  public String getRef()
  {
    return ref;
  }
  



  public int getPos()
  {
    return pos;
  }
  
  public String toString() {
    String str = super.toString();
    if (ref == null)
      return str;
    str = str + " in string ``" + ref + "''";
    if (pos < 0)
      return str;
    return str + " at position " + pos;
  }
}
