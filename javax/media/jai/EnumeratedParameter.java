package javax.media.jai;

import java.io.Serializable;





























































public class EnumeratedParameter
  implements Serializable
{
  private String name;
  private int value;
  
  public EnumeratedParameter(String name, int value)
  {
    this.name = name;
    this.value = value;
  }
  



  public String getName()
  {
    return name;
  }
  



  public int getValue()
  {
    return value;
  }
  


  public int hashCode()
  {
    return (getClass().getName() + new Integer(value)).hashCode();
  }
  




  public boolean equals(Object o)
  {
    return (o != null) && (getClass().equals(o.getClass())) && ((name.equals(((EnumeratedParameter)o).getName())) || (value == ((EnumeratedParameter)o).getValue()));
  }
  















  public String toString()
  {
    return getClass().getName() + ":" + name + "=" + String.valueOf(value);
  }
}
