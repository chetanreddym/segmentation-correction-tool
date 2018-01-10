package javax.mail.search;

import javax.mail.Address;
import javax.mail.internet.InternetAddress;


























public abstract class AddressStringTerm
  extends StringTerm
{
  protected AddressStringTerm(String paramString)
  {
    super(paramString, true);
  }
  











  protected boolean match(Address paramAddress)
  {
    if ((paramAddress instanceof InternetAddress)) {
      InternetAddress localInternetAddress = (InternetAddress)paramAddress;
      



      return super.match(localInternetAddress.toUnicodeString());
    }
    return super.match(paramAddress.toString());
  }
  


  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof AddressStringTerm))
      return false;
    return super.equals(paramObject);
  }
}
