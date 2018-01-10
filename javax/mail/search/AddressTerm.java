package javax.mail.search;

import javax.mail.Address;




















public abstract class AddressTerm
  extends SearchTerm
{
  protected Address address;
  
  protected AddressTerm(Address paramAddress)
  {
    address = paramAddress;
  }
  


  public Address getAddress()
  {
    return address;
  }
  


  protected boolean match(Address paramAddress)
  {
    return paramAddress.equals(address);
  }
  


  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof AddressTerm))
      return false;
    AddressTerm localAddressTerm = (AddressTerm)paramObject;
    return address.equals(address);
  }
  


  public int hashCode()
  {
    return address.hashCode();
  }
}
