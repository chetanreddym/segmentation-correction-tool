package gttool.misc;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;





public class AttributeValueList
  implements Iterable<String>, Collection<String>, Cloneable
{
  private Vector<String> valueList = new Vector();
  
  public AttributeValueList() {
    valueList = new Vector();
  }
  
  private AttributeValueList(int capacity) {
    valueList = new Vector(capacity);
  }
  





  public static AttributeValueList parse(String strValueList)
  {
    if ((strValueList == null) || (strValueList.trim().length() < 3)) {
      return new AttributeValueList();
    }
    String[] valueList = strValueList.trim().substring(1, 
      strValueList.length() - 1).split(";");
    AttributeValueList ret = new AttributeValueList(valueList.length);
    ret.add("");
    for (String value : valueList) {
      if (!ret.contains(value))
        ret.add(value);
    }
    return ret;
  }
  


  public String toString()
  {
    StringBuffer ret = new StringBuffer();
    boolean first = true;
    
    if (valueList.size() == 0)
    {

      return "";
    }
    for (String value : valueList) {
      if (first) {
        ret.append("{");
        first = false;
      } else {
        ret.append(";");
      }
      ret.append(value);
    }
    if (!first) {
      ret.append("}");
    }
    return ret.toString();
  }
  

  public Iterator<String> iterator()
  {
    return valueList.iterator();
  }
  
  public boolean contains(Object o) {
    return valueList.contains(o);
  }
  

  public Object clone()
  {
    AttributeValueList a = new AttributeValueList();
    valueList = ((Vector)valueList.clone());
    return a;
  }
  
  public boolean add(String value)
  {
    return valueList.add(value);
  }
  
  public void add(int pos, String value) {
    valueList.add(pos, value);
  }
  
  public Object[] toArray() {
    return valueList.toArray(new String[0]);
  }
  
  public boolean remove(String value) {
    return valueList.remove(value);
  }
  
  public void remove(int pos) {
    valueList.remove(pos);
  }
  
  public int size() {
    return valueList.size();
  }
  
  public Object get(int i) {
    return valueList.get(i);
  }
  
  public boolean isEmpty() {
    return valueList.isEmpty();
  }
  
  public <T> T[] toArray(T[] arg0)
  {
    return valueList.toArray();
  }
  
  public boolean remove(Object arg0) {
    return valueList.remove(arg0);
  }
  
  public boolean containsAll(Collection<?> arg0) {
    return valueList.containsAll(arg0);
  }
  
  public boolean addAll(Collection<? extends String> arg0) {
    return valueList.addAll(arg0);
  }
  
  public boolean removeAll(Collection<?> arg0)
  {
    return valueList.removeAll(arg0);
  }
  
  public boolean retainAll(Collection<?> arg0) {
    return valueList.retainAll(arg0);
  }
  
  public void clear() {
    valueList.clear();
  }
}
