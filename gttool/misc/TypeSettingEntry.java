package gttool.misc;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import ocr.gui.leftPanel.FilePropPacket;




















public class TypeSettingEntry
  implements Map<String, TypeAttributeEntry>
{
  private String name = null;
  private Color color = null;
  private boolean visible = true;
  

  private boolean onlySelectedVisible = false;
  
  private String shortCutKey = "";
  private Set<String> groupOf = null;
  private final String ANY = "ANY";
  
  TypeAttributes typeAttributes = new TypeAttributes();
  





  Map<String, Attribute> attributeHotkeys = new HashMap();
  





  public TypeSettingEntry() {}
  





  public TypeSettingEntry(Color color, boolean visible, boolean onlySelectedVisible, String shortCutKey)
  {
    this.color = color;
    this.visible = visible;
    this.onlySelectedVisible = onlySelectedVisible;
    this.shortCutKey = shortCutKey;
  }
  








  public TypeSettingEntry(String name, Color color, boolean visible, boolean onlySelectedVisible, String shortCutKey)
  {
    this.name = name;
    this.color = color;
    this.visible = visible;
    this.onlySelectedVisible = onlySelectedVisible;
    this.shortCutKey = shortCutKey;
  }
  






  public TypeSettingEntry(String name, Object[] settingArray)
  {
    setName(name);
    setColor((Color)settingArray[1]);
    visible(settingArray[3] == FilePropPacket.is_there_path);
    onlySelectedVisible(settingArray[3] == FilePropPacket.is_only_selected_visible);
    if (!name.equals("DL_PAGE")) {
      setShortCutKey(settingArray[2].toString());
      setGroupOf((Set)settingArray[6]);
    }
  }
  






  public Object[] toTypeWindowEntry()
  {
    Object[] settingArray = new Object[7];
    
    settingArray[0] = name;
    settingArray[1] = getColor();
    settingArray[3] = (visible() ? FilePropPacket.is_there_path : FilePropPacket.is_not_there_path);
    if (onlySelectedVisible())
      settingArray[3] = FilePropPacket.is_only_selected_visible;
    settingArray[2] = getShortCutKey();
    
    settingArray[6] = groupOf;
    return settingArray;
  }
  
  public String getName() {
    return name;
  }
  
  void setName(String name) {
    this.name = name;
    typeAttributes.typeName = name;
  }
  
  public Color getColor() {
    return color;
  }
  
  public void setColor(Color color) {
    this.color = color;
  }
  
  public void setColor(String strColor) {
    if ((strColor != null) && (strColor.length() > 0)) {
      color = HexColor.newColor(strColor);
    } else
      color = Color.BLACK;
  }
  
  public boolean visible() {
    return visible;
  }
  
  public void visible(boolean isVisible) {
    visible = isVisible;
  }
  
  public boolean onlySelectedVisible()
  {
    return onlySelectedVisible;
  }
  
  public void onlySelectedVisible(boolean isVisible) {
    onlySelectedVisible = isVisible;
  }
  
  public void visible(String strIsVisible) {
    if ((strIsVisible != null) && (strIsVisible.length() > 0)) {
      visible = Boolean.parseBoolean(strIsVisible);
      
      if ((!visible) && (strIsVisible.equalsIgnoreCase("onlySelected")))
        onlySelectedVisible = true;
    }
  }
  
  public void setShortCutKey(String shortCutKey) {
    this.shortCutKey = shortCutKey;
  }
  
  public String getShortCutKey() {
    return shortCutKey;
  }
  
  public Map<String, Attribute> getAttributeHotkeys() {
    return attributeHotkeys;
  }
  
  public void setAttributeHotkeys(Map<String, Attribute> attributeHotkeys) {
    this.attributeHotkeys = attributeHotkeys;
  }
  


  public TypeAttributes getTypeAttributes()
  {
    return typeAttributes;
  }
  
  public void setGroupOf(Set<String> list) {
    groupOf = list;
  }
  
  public void setGroupOf(String list) {
    if (groupOf == null) {
      groupOf = new HashSet();
    }
    if ((list == null) || (list.isEmpty())) {
      return;
    }
    if (list.equalsIgnoreCase("ANY")) {
      groupOf.add(list.toUpperCase());
      return;
    }
    
    String[] items = list.split(";");
    for (String s : items)
      if (!s.isEmpty())
        groupOf.add(s);
  }
  
  public Set<String> getGroupOf() {
    return groupOf;
  }
  
  public String getGroupOf_AsString() {
    String groupOfStr = "";
    if (groupOf == null) {
      return groupOfStr;
    }
    for (String s : groupOf) {
      groupOfStr = groupOfStr + s + ";";
    }
    if (groupOfStr.endsWith(";")) {
      groupOfStr = groupOfStr.substring(0, groupOfStr.length() - 1);
    }
    return groupOfStr;
  }
  

  public int size()
  {
    return typeAttributes.size();
  }
  
  public boolean isEmpty() {
    return typeAttributes.isEmpty();
  }
  
  public boolean containsKey(Object arg0) {
    return typeAttributes.containsKey(arg0);
  }
  
  public boolean containsValue(Object arg0) {
    return typeAttributes.containsValue(arg0);
  }
  
  public TypeAttributeEntry get(Object arg0) {
    return (TypeAttributeEntry)typeAttributes.get(arg0);
  }
  
  public TypeAttributeEntry put(String arg0, TypeAttributeEntry arg1) {
    return typeAttributes.put(arg0, arg1);
  }
  
  public TypeAttributeEntry remove(Object arg0) {
    return (TypeAttributeEntry)typeAttributes.remove(arg0);
  }
  
  public void putAll(Map<? extends String, ? extends TypeAttributeEntry> arg0) {
    typeAttributes.putAll(arg0);
  }
  
  public void clear() {
    typeAttributes.clear();
  }
  
  public Set<String> keySet() {
    return typeAttributes.keySet();
  }
  
  public Collection<TypeAttributeEntry> values() {
    return typeAttributes.values();
  }
  
  public Set<Map.Entry<String, TypeAttributeEntry>> entrySet() {
    return typeAttributes.entrySet();
  }
}
