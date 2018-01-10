package javax.media.jai.remote;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.media.jai.util.CaselessStringKey;

































public class NegotiableCapabilitySet
  implements Serializable
{
  private Hashtable categories = new Hashtable();
  

  private boolean isPreference = false;
  















  public NegotiableCapabilitySet(boolean isPreference)
  {
    this.isPreference = isPreference;
  }
  




  public boolean isPreference()
  {
    return isPreference;
  }
  
















  public void add(NegotiableCapability capability)
  {
    if (capability == null) {
      throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapabilitySet0"));
    }
    

    if (isPreference != capability.isPreference()) {
      throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapabilitySet1"));
    }
    

    SequentialMap map = getCategoryMap(capability.getCategory());
    map.put(capability);
  }
  











  public void remove(NegotiableCapability capability)
  {
    if (capability == null) {
      throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapabilitySet0"));
    }
    

    SequentialMap map = getCategoryMap(capability.getCategory());
    map.remove(capability);
  }
  











  public List get(String category, String capabilityName)
  {
    if (category == null) {
      throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapabilitySet3"));
    }
    

    if (capabilityName == null) {
      throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapabilitySet4"));
    }
    

    SequentialMap map = getCategoryMap(category);
    return map.getNCList(capabilityName);
  }
  









  public List get(String category)
  {
    if (category == null) {
      throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapabilitySet3"));
    }
    

    SequentialMap map = getCategoryMap(category);
    Vector capNames = map.getCapabilityNames();
    
    Vector allNC = new Vector();
    
    for (Iterator e = capNames.iterator(); e.hasNext();)
    {
      Vector curr = (Vector)map.getNCList((String)e.next());
      for (i = curr.iterator(); i.hasNext();)
      {
        Object obj = i.next();
        

        if (!allNC.contains(obj)) {
          allNC.add(obj);
        }
      }
    }
    Iterator i;
    return allNC;
  }
  










  public List getCategories()
  {
    Vector v = new Vector();
    for (Enumeration e = categories.keys(); e.hasMoreElements();) {
      CaselessStringKey key = (CaselessStringKey)e.nextElement();
      v.add(key.toString());
    }
    
    return v;
  }
  













  public List getCapabilityNames(String category)
  {
    if (category == null) {
      throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapabilitySet3"));
    }
    

    SequentialMap map = getCategoryMap(category);
    Vector names = map.getCapabilityNames();
    return names;
  }
  




























  public NegotiableCapabilitySet negotiate(NegotiableCapabilitySet other)
  {
    if (other == null) {
      return null;
    }
    NegotiableCapabilitySet negotiated = new NegotiableCapabilitySet(isPreference & other.isPreference());
    


    Vector commonCategories = new Vector(getCategories());
    commonCategories.retainAll(other.getCategories());
    




    for (Iterator c = commonCategories.iterator(); c.hasNext();) {
      String currCategory = (String)c.next();
      
      List thisCapabilities = get(currCategory);
      otherCapabilities = other.get(currCategory);
      
      for (t = thisCapabilities.iterator(); t.hasNext();)
      {
        thisCap = (NegotiableCapability)t.next();
        
        for (o = otherCapabilities.iterator(); o.hasNext();)
        {
          NegotiableCapability otherCap = (NegotiableCapability)o.next();
          NegotiableCapability negCap = thisCap.negotiate(otherCap);
          if (negCap != null)
            negotiated.add(negCap); } } }
    List otherCapabilities;
    Iterator t;
    NegotiableCapability thisCap;
    Iterator o;
    if (negotiated.isEmpty()) {
      return null;
    }
    
    return negotiated;
  }
  












  public NegotiableCapability getNegotiatedValue(String category)
  {
    if (category == null) {
      throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapabilitySet3"));
    }
    

    List thisCapabilities = get(category);
    if (thisCapabilities.isEmpty()) {
      return null;
    }
    return (NegotiableCapability)thisCapabilities.get(0);
  }
  

























  public NegotiableCapability getNegotiatedValue(NegotiableCapabilitySet other, String category)
  {
    if (other == null) {
      return null;
    }
    if (category == null) {
      throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapabilitySet3"));
    }
    

    List thisCapabilities = get(category);
    List otherCapabilities = other.get(category);
    


    for (Iterator t = thisCapabilities.iterator(); t.hasNext();)
    {
      thisCap = (NegotiableCapability)t.next();
      
      for (o = otherCapabilities.iterator(); o.hasNext();)
      {
        NegotiableCapability otherCap = (NegotiableCapability)o.next();
        NegotiableCapability negCap = thisCap.negotiate(otherCap);
        

        if (negCap != null)
          return negCap;
      } }
    NegotiableCapability thisCap;
    Iterator o;
    return null;
  }
  



  public boolean isEmpty()
  {
    return categories.isEmpty();
  }
  


  private SequentialMap getCategoryMap(String category)
  {
    CaselessStringKey categoryKey = new CaselessStringKey(category);
    SequentialMap map = (SequentialMap)categories.get(categoryKey);
    
    if (map == null) {
      map = new SequentialMap();
      categories.put(categoryKey, map);
    }
    
    return map;
  }
  




  class SequentialMap
    implements Serializable
  {
    Vector keys;
    



    Vector values;
    



    SequentialMap()
    {
      keys = new Vector();
      values = new Vector();
    }
    



    void put(NegotiableCapability capability)
    {
      CaselessStringKey capNameKey = new CaselessStringKey(capability.getCapabilityName());
      

      int index = keys.indexOf(capNameKey);
      

      if (index == -1) {
        keys.add(capNameKey);
        Vector v = new Vector();
        v.add(capability);
        values.add(v);
      } else {
        Vector v = (Vector)values.elementAt(index);
        if (v == null)
          v = new Vector();
        v.add(capability);
      }
    }
    




    List getNCList(String capabilityName)
    {
      CaselessStringKey capNameKey = new CaselessStringKey(capabilityName);
      

      int index = keys.indexOf(capNameKey);
      

      if (index == -1) {
        Vector v = new Vector();
        return v;
      }
      Vector v = (Vector)values.elementAt(index);
      return v;
    }
    




    void remove(NegotiableCapability capability)
    {
      CaselessStringKey capNameKey = new CaselessStringKey(capability.getCapabilityName());
      

      int index = keys.indexOf(capNameKey);
      
      if (index == -1) {
        throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapabilitySet2"));
      }
      

      Vector v = (Vector)values.elementAt(index);
      if (!v.remove(capability)) {
        throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapabilitySet2"));
      }
      


      if (v.isEmpty()) {
        keys.remove(capNameKey);
        values.remove(index);
      }
      
      if (keys.isEmpty()) {
        categories.remove(new CaselessStringKey(capability.getCategory()));
      }
    }
    




    Vector getCapabilityNames()
    {
      Vector v = new Vector();
      
      for (Iterator i = keys.iterator(); i.hasNext();) {
        CaselessStringKey name = (CaselessStringKey)i.next();
        v.add(name.getName());
      }
      
      return v;
    }
  }
}
