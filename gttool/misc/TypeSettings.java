package gttool.misc;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;












public class TypeSettings
  extends HashMap<String, TypeSettingEntry>
{
  private static final long serialVersionUID = 1L;
  
  public TypeSettings() {}
  
  public TypeSettings(int initialCapacity)
  {
    super(initialCapacity);
  }
  


  public TypeSettings(int initialCapacity, float loadFactor)
  {
    super(initialCapacity, loadFactor);
  }
  

  public TypeSettings(TypeSettings typeSettings)
  {
    super(typeSettings);
  }
  



  public TypeSettings(Map<String, Object[]> typeWindow, Map<String, Map<String, TypeAttributeEntry>> typeAttributes)
  {
    super(typeWindow.size());
    putAllSettings(typeWindow, typeAttributes);
  }
  





  public TypeSettingEntry put(String key, TypeSettingEntry value)
  {
    TypeSettingEntry ret = (TypeSettingEntry)super.put(key, value);
    
    value.setName(key);
    
    return ret;
  }
  





  public Map<String, TypeAttributeEntry> put(String key, Map<String, TypeAttributeEntry> mapOfAttributes)
  {
    TypeSettingEntry entry = (TypeSettingEntry)super.get(key);
    



    TypeSettingEntry ret = (TypeSettingEntry)typeAttributes.clone();
    entry.getTypeAttributes().clear();
    entry.getTypeAttributes().putAll(mapOfAttributes);
    return ret;
  }
  



  public void putAllSettings(Map<String, Object[]> typeWindow, Map<String, Map<String, TypeAttributeEntry>> typeAttributes)
  {
    Iterator localIterator2;
    

    for (Iterator localIterator1 = typeWindow.entrySet().iterator(); localIterator1.hasNext(); 
        


        localIterator2.hasNext())
    {
      Map.Entry<String, Object[]> entry = (Map.Entry)localIterator1.next();
      Object[] settingArray = (Object[])entry.getValue();
      String typeName; TypeSettingEntry typeSettingEntry; put(typeName = (String)entry.getKey(), typeSettingEntry = new TypeSettingEntry((String)entry.getKey(), settingArray));
      
      localIterator2 = ((Map)typeAttributes.get(typeName)).entrySet().iterator(); continue;Map.Entry<String, TypeAttributeEntry> attributeEntry = (Map.Entry)localIterator2.next();
      
      typeSettingEntry.getTypeAttributes().put((String)attributeEntry.getKey(), (TypeAttributeEntry)attributeEntry.getValue());
    }
  }
  




  public Map<String, Object[]> toTypeWindow()
  {
    Map<String, Object[]> types = new HashMap();
    

    int i = 0;
    

    for (Map.Entry<String, TypeSettingEntry> entry : entrySet()) {
      String type = (String)entry.getKey();
      TypeSettingEntry typeEntry = (TypeSettingEntry)entry.getValue();
      
      Object[] settingArray = typeEntry.toTypeWindowEntry();
      settingArray[5] = Integer.valueOf(i);
      types.put(type, settingArray);
      i++;
    }
    return types;
  }
  
  public Map<String, Map<String, TypeAttributeEntry>> toMap() {
    Map<String, Map<String, TypeAttributeEntry>> ret = new HashMap(size());
    
    for (Map.Entry<String, TypeSettingEntry> settingEntry : entrySet()) {
      Map<String, TypeAttributeEntry> convertedSettingEntry = new HashMap(((TypeSettingEntry)settingEntry.getValue()).size());
      convertedSettingEntry.putAll((Map)settingEntry.getValue());
      ret.put((String)settingEntry.getKey(), convertedSettingEntry);
    }
    return ret;
  }
}
