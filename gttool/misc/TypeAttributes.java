package gttool.misc;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;








public class TypeAttributes
  extends HashMap<String, TypeAttributeEntry>
{
  private static final long serialVersionUID = 4477591300630265283L;
  String typeName;
  
  public TypeAttributes() {}
  
  public String getTypeName()
  {
    return typeName;
  }
  







  public TypeAttributeEntry put(String key, TypeAttributeEntry value)
  {
    value.setName(key);
    TypeAttributeEntry ret = (TypeAttributeEntry)super.put(key, value);
    return ret;
  }
  




  public void putAll(Map<? extends String, ? extends TypeAttributeEntry> map)
  {
    for (Map.Entry<? extends String, ? extends TypeAttributeEntry> entry : map.entrySet()) {
      put((String)entry.getKey(), (TypeAttributeEntry)entry.getValue());
    }
  }
}
