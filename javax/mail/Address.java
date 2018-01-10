package javax.mail;

import java.io.Serializable;

public abstract class Address
  implements Serializable
{
  public abstract String getType();
  
  public abstract String toString();
  
  public abstract boolean equals(Object paramObject);
  
  public Address() {}
}
