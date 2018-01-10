package javax.media.jai.util;

import java.io.Serializable;
import java.util.Locale;



























public final class CaselessStringKey
  implements Cloneable, Serializable
{
  private String name;
  private String lowerCaseName;
  
  public CaselessStringKey(String name)
  {
    setName(name);
  }
  


  public int hashCode()
  {
    return lowerCaseName.hashCode();
  }
  


  public String getName()
  {
    return name;
  }
  


  private String getLowerCaseName()
  {
    return lowerCaseName;
  }
  





  public void setName(String name)
  {
    if (name == null) {
      throw new IllegalArgumentException(JaiI18N.getString("CaselessStringKey0"));
    }
    this.name = name;
    lowerCaseName = name.toLowerCase(Locale.ENGLISH);
  }
  


  public Object clone()
  {
    try
    {
      return super.clone();
    }
    catch (CloneNotSupportedException e) {
      throw new InternalError();
    }
  }
  





  public boolean equals(Object o)
  {
    if ((o != null) && ((o instanceof CaselessStringKey))) {
      return lowerCaseName.equals(((CaselessStringKey)o).getLowerCaseName());
    }
    return false;
  }
  


  public String toString()
  {
    return getName();
  }
}
