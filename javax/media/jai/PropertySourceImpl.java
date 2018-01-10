package javax.media.jai;

import com.sun.media.jai.util.PropertyUtil;
import java.awt.Image;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.media.jai.util.CaselessStringKey;



















































public class PropertySourceImpl
  implements PropertySource, Serializable
{
  protected transient Map properties;
  protected transient Map propertySources;
  protected Set cachedPropertyNames;
  
  protected PropertySourceImpl()
  {
    properties = new Hashtable();
    propertySources = new Hashtable();
    cachedPropertyNames = Collections.synchronizedSet(new HashSet());
  }
  


























  public PropertySourceImpl(Map propertyMap, PropertySource propertySource)
  {
    this();
    


    if ((propertyMap == null) && (propertySource == null)) {
      boolean throwException = false;
      try {
        Class rootClass = Class.forName("javax.media.jai.PropertySourceImpl");
        
        throwException = getClass().equals(rootClass);
      }
      catch (Exception e) {}
      
      if (throwException) {
        throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
      }
    }
    

    if (propertyMap != null) {
      Iterator keys = propertyMap.keySet().iterator();
      while (keys.hasNext()) {
        Object key = keys.next();
        if ((key instanceof String)) {
          properties.put(new CaselessStringKey((String)key), propertyMap.get(key));
        }
        else if ((key instanceof CaselessStringKey)) {
          properties.put((CaselessStringKey)key, propertyMap.get(key));
        }
      }
    }
    

    if (propertySource != null) {
      String[] names = propertySource.getPropertyNames();
      if (names != null) {
        int length = names.length;
        for (int i = 0; i < length; i++) {
          propertySources.put(new CaselessStringKey(names[i]), propertySource);
        }
      }
    }
  }
  








  public String[] getPropertyNames()
  {
    synchronized (properties) {
      if (properties.size() + propertySources.size() == 0) {
        return null;
      }
      

      Set propertyNames = Collections.synchronizedSet(new HashSet(properties.keySet()));
      


      propertyNames.addAll(propertySources.keySet());
      

      int length = propertyNames.size();
      String[] names = new String[length];
      Iterator elements = propertyNames.iterator();
      int index = 0;
      while ((elements.hasNext()) && (index < length)) {
        names[(index++)] = ((CaselessStringKey)elements.next()).getName();
      }
      
      return names;
    }
  }
  











  public String[] getPropertyNames(String prefix)
  {
    return PropertyUtil.getPropertyNames(getPropertyNames(), prefix);
  }
  




















  public Class getPropertyClass(String propertyName)
  {
    if (propertyName == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    synchronized (properties) {
      Class propertyClass = null;
      Object value = properties.get(new CaselessStringKey(propertyName));
      if (value != null) {
        propertyClass = value.getClass();
      }
      return propertyClass;
    }
  }
  





















  public Object getProperty(String propertyName)
  {
    if (propertyName == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    synchronized (properties) {
      CaselessStringKey key = new CaselessStringKey(propertyName);
      

      Object value = properties.get(key);
      
      if (value == null)
      {
        PropertySource propertySource = (PropertySource)propertySources.get(key);
        
        if (propertySource != null) {
          value = propertySource.getProperty(propertyName);
          if (value != Image.UndefinedProperty)
          {
            properties.put(key, value);
            cachedPropertyNames.add(key);
          }
        } else {
          value = Image.UndefinedProperty;
        }
      }
      
      return value;
    }
  }
  












  public Map getProperties()
  {
    if (properties.size() + propertySources.size() == 0) {
      return null;
    }
    
    synchronized (properties) {
      Hashtable props = null;
      
      String[] propertyNames = getPropertyNames();
      if (propertyNames != null) {
        int length = propertyNames.length;
        props = new Hashtable(properties.size());
        for (int i = 0; i < length; i++) {
          String name = propertyNames[i];
          Object value = getProperty(name);
          props.put(name, value);
        }
      }
      
      return props;
    }
  }
  



  private static void writeMap(ObjectOutputStream out, Map map)
    throws IOException
  {
    Hashtable table = new Hashtable();
    

    Iterator keys = map.keySet().iterator();
    while (keys.hasNext()) {
      Object key = keys.next();
      Object value = map.get(key);
      if ((value instanceof Serializable)) {
        table.put(key, value);
      }
    }
    

    out.writeObject(table);
  }
  


  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.defaultWriteObject();
    
    synchronized (properties)
    {

      writeMap(out, properties);
      writeMap(out, propertySources);
    }
  }
  



  private void readObject(ObjectInputStream in)
    throws IOException, ClassNotFoundException
  {
    in.defaultReadObject();
    

    properties = ((Map)in.readObject());
    propertySources = ((Map)in.readObject());
    


    Iterator names = cachedPropertyNames.iterator();
    Set propertyNames = properties.keySet();
    while (names.hasNext()) {
      if (!propertyNames.contains(names.next())) {
        names.remove();
      }
    }
  }
}
