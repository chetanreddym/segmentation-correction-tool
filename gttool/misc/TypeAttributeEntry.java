package gttool.misc;

import java.util.Vector;
import ocr.gui.LoadDataFile;








public class TypeAttributeEntry
  implements Cloneable
{
  String name;
  String defaultValue;
  AttributeValueList possibleValues;
  String arbval = "false";
  
  String hotkey = "None";
  String key0 = "None";
  String key1 = "None";
  String key2 = "None";
  String key3 = "None";
  String key4 = "None";
  String key5 = "None";
  String key6 = "None";
  String key7 = "None";
  String key8 = "None";
  String key9 = "None";
  Vector<String> vec = new Vector();
  














  public TypeAttributeEntry(String name, String defaultValue, String possibleValuesString, String arb, String k0, String k1, String k2, String k3, String k4, String k5, String k6, String k7, String k8, String k9)
  {
    this.name = name;
    this.defaultValue = defaultValue;
    possibleValues = AttributeValueList.parse(possibleValuesString);
    arbval = arb;
    key0 = k0;
    key1 = k1;
    key2 = k2;
    key3 = k3;
    key4 = k4;
    key5 = k5;
    key6 = k6;
    key7 = k7;
    key8 = k8;
    key9 = k9;
    vec.add(key0);
    vec.add(key1);
    vec.add(key2);
    vec.add(key3);
    vec.add(key4);
    vec.add(key5);
    vec.add(key6);
    vec.add(key7);
    vec.add(key8);
    vec.add(key9);
  }
  

  public TypeAttributeEntry(String name, String defaultValue, AttributeValueList possibleValues)
  {
    this.name = name;
    this.defaultValue = defaultValue;
    this.possibleValues = possibleValues;
  }
  

  public TypeAttributeEntry(String name, String defaultValue, AttributeValueList possibleValues, String arb, String k0, String k1, String k2, String k3, String k4, String k5, String k6, String k7, String k8, String k9)
  {
    this.name = name;
    this.defaultValue = defaultValue;
    this.possibleValues = possibleValues;
    arbval = arb;
    key0 = k0;
    key1 = k1;
    key2 = k2;
    key3 = k3;
    key4 = k4;
    key5 = k5;
    key6 = k6;
    key7 = k7;
    key8 = k8;
    key9 = k9;
    vec.add(key0);
    vec.add(key1);
    vec.add(key2);
    vec.add(key3);
    vec.add(key4);
    vec.add(key5);
    vec.add(key6);
    vec.add(key7);
    vec.add(key8);
    vec.add(key9);
  }
  











  public TypeAttributeEntry(String name, String defaultValue)
  {
    this.name = name;
    this.defaultValue = defaultValue;
    possibleValues = new AttributeValueList();
    possibleValues.add(this.defaultValue);
  }
  





















  public TypeAttributeEntry(String defaultValue)
  {
    this.defaultValue = defaultValue;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getDefaultValue() {
    return defaultValue;
  }
  
  public void setDefaultValue(String value) {
    defaultValue = value;
  }
  
  public AttributeValueList getPossibleValues() {
    return possibleValues;
  }
  
  public String getPossibleValuesString() {
    return possibleValues == null ? "" : possibleValues.toString();
  }
  
  public void setPossibleValues(AttributeValueList possibleValues) {
    this.possibleValues = possibleValues;
  }
  
  public void setArbVal(String val) {
    arbval = val;
  }
  
  public String getArbVal() {
    return arbval;
  }
  
  public Vector<String> getHotKey()
  {
    return vec;
  }
  
  public void setHotKey(String hk, String val) { vec.setElementAt(val, Integer.parseInt(hk)); }
  

  public Object clone()
  {
    if (arbval == null)
      arbval = "";
    if (key0 == null)
      key0 = "";
    if (key1 == null)
      key1 = "";
    if (key2 == null)
      key2 = "";
    if (key3 == null)
      key3 = "";
    if (key4 == null)
      key4 = "";
    if (key5 == null)
      key5 = "";
    if (key6 == null)
      key6 = "";
    if (key7 == null)
      key7 = "";
    if (key8 == null)
      key8 = "";
    if (key9 == null) {
      key9 = "";
    }
    if (defaultValue == null) {
      defaultValue = "";
    }
    return new TypeAttributeEntry(name.toString(), defaultValue.toString(), 
      (AttributeValueList)possibleValues.clone(), arbval.toString(), key0.toString(), 
      key1.toString(), key2.toString(), key3.toString(), key4.toString(), key5.toString(), 
      key6.toString(), key7.toString(), key8.toString(), key9.toString());
  }
  






  public static void main(String[] args)
  {
    LoadDataFile load = new LoadDataFile(args[0]);
    
    TypeSettings typeSettings = load.getTypeSettings();
    


    TypeSettingEntry settingWord = (TypeSettingEntry)typeSettings.get("DL_WORD");
    
    TypeAttributes attributes = settingWord.getTypeAttributes();
    










    ((TypeAttributeEntry)attributes.get("Desc")).setDefaultValue("Hi here!");
    
    ((TypeSettingEntry)typeSettings.get("DL_ZONE")).getTypeAttributes().put("Paper", 
      new TypeAttributeEntry("Letter", ""));
    


    load.dumpWorksapceAs(args[1]);
  }
}
