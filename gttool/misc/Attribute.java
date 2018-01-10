package gttool.misc;







public class Attribute
  implements Cloneable
{
  String name;
  




  String value;
  





  public Attribute(String name, String value)
  {
    this.name = name;
    this.value = value;
  }
  

















  public String getName()
  {
    return name;
  }
  
  public String setName(String name) {
    return this.name = name;
  }
  



  public String getValue()
  {
    return value;
  }
  
  public String setValue(String value) {
    return this.value = value;
  }
  


























  public boolean equals(Object obj)
  {
    return (obj != null) && ((obj instanceof Attribute)) && (name.equals(name)) && (value.equals(value));
  }
  
  public String toString() {
    StringBuffer s = new StringBuffer("(");
    s.append(name);
    s.append(",");
    s.append(value);
    s.append(")");
    return s.toString();
  }
  
  public Object clone() {
    return new Attribute(name, value);
  }
}
