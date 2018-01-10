package com.sun.media.jai.util;

import java.io.Serializable;
import java.util.Hashtable;
import javax.media.jai.util.CaselessStringKey;




















public class CaselessStringArrayTable
  implements Serializable
{
  private CaselessStringKey[] keys;
  private Hashtable indices;
  
  public CaselessStringArrayTable()
  {
    this((CaselessStringKey[])null);
  }
  



  public CaselessStringArrayTable(CaselessStringKey[] keys)
  {
    this.keys = keys;
    indices = new Hashtable();
    
    if (keys != null) {
      for (int i = 0; i < keys.length; i++) {
        indices.put(keys[i], new Integer(i));
      }
    }
  }
  

  public CaselessStringArrayTable(String[] keys)
  {
    this(toCaselessStringKey(keys));
  }
  


  private static CaselessStringKey[] toCaselessStringKey(String[] strings)
  {
    if (strings == null) {
      return null;
    }
    CaselessStringKey[] keys = new CaselessStringKey[strings.length];
    
    for (int i = 0; i < strings.length; i++) {
      keys[i] = new CaselessStringKey(strings[i]);
    }
    return keys;
  }
  





  public int indexOf(CaselessStringKey key)
  {
    if (key == null) {
      throw new IllegalArgumentException(JaiI18N.getString("CaselessStringArrayTable0"));
    }
    

    Integer i = (Integer)indices.get(key);
    
    if (i == null) {
      throw new IllegalArgumentException(key.getName() + " - " + JaiI18N.getString("CaselessStringArrayTable1"));
    }
    

    return i.intValue();
  }
  





  public int indexOf(String key)
  {
    return indexOf(new CaselessStringKey(key));
  }
  




  public String getName(int i)
  {
    if (keys == null) {
      throw new ArrayIndexOutOfBoundsException();
    }
    return keys[i].getName();
  }
  





  public CaselessStringKey get(int i)
  {
    if (keys == null) {
      throw new ArrayIndexOutOfBoundsException();
    }
    return keys[i];
  }
  




  public boolean contains(CaselessStringKey key)
  {
    if (key == null) {
      throw new IllegalArgumentException(JaiI18N.getString("CaselessStringArrayTable0"));
    }
    

    return indices.get(key) != null;
  }
  




  public boolean contains(String key)
  {
    return contains(new CaselessStringKey(key));
  }
}
