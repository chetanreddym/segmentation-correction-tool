package javax.media.jai;

import java.awt.Image;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.media.jai.util.CaselessStringKey;








































public class WritablePropertySourceImpl
  extends PropertySourceImpl
  implements WritablePropertySource
{
  protected PropertyChangeSupportJAI manager = null;
  
















  public WritablePropertySourceImpl() {}
  
















  public WritablePropertySourceImpl(Map propertyMap, PropertySource source, PropertyChangeSupportJAI manager)
  {
    super(propertyMap, source);
    this.manager = manager;
  }
  























  public Object getProperty(String propertyName)
  {
    if (propertyName == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    synchronized (properties)
    {
      boolean isMapped = properties.containsKey(new CaselessStringKey(propertyName));
      


      Object value = super.getProperty(propertyName);
      



      if ((manager != null) && (!isMapped) && (value != Image.UndefinedProperty))
      {


        Object eventSource = manager.getPropertyChangeEventSource();
        
        PropertySourceChangeEvent evt = new PropertySourceChangeEvent(eventSource, propertyName, Image.UndefinedProperty, value);
        


        manager.firePropertyChange(evt);
      }
      
      return value;
    }
  }
  
























  public void setProperty(String propertyName, Object propertyValue)
  {
    if ((propertyName == null) || (propertyValue == null)) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    synchronized (properties) {
      CaselessStringKey key = new CaselessStringKey(propertyName);
      

      Object oldValue = properties.put(key, propertyValue);
      if (oldValue == null) {
        oldValue = Image.UndefinedProperty;
      }
      

      cachedPropertyNames.remove(key);
      
      if ((manager != null) && (!oldValue.equals(propertyValue))) {
        Object eventSource = manager.getPropertyChangeEventSource();
        
        PropertySourceChangeEvent evt = new PropertySourceChangeEvent(eventSource, propertyName, oldValue, propertyValue);
        

        manager.firePropertyChange(evt);
      }
    }
  }
  












  public void removeProperty(String propertyName)
  {
    if (propertyName == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    synchronized (properties) {
      CaselessStringKey key = new CaselessStringKey(propertyName);
      

      Object oldValue = properties.remove(key);
      


      propertySources.remove(key);
      cachedPropertyNames.remove(key);
      
      if ((manager != null) && (oldValue != null)) {
        Object eventSource = manager.getPropertyChangeEventSource();
        
        PropertySourceChangeEvent evt = new PropertySourceChangeEvent(eventSource, propertyName, oldValue, Image.UndefinedProperty);
        


        manager.firePropertyChange(evt);
      }
    }
  }
  















  public void addProperties(Map propertyMap)
  {
    if (propertyMap != null) {
      synchronized (properties) {
        Iterator keys = propertyMap.keySet().iterator();
        while (keys.hasNext()) {
          Object key = keys.next();
          if ((key instanceof String)) {
            setProperty((String)key, propertyMap.get(key));
          }
          else if ((key instanceof CaselessStringKey)) {
            setProperty(((CaselessStringKey)key).getName(), propertyMap.get(key));
          }
        }
      }
    }
  }
  















  public void addProperties(PropertySource propertySource)
  {
    if (propertySource != null) {
      synchronized (properties) {
        String[] names = propertySource.getPropertyNames();
        if (names != null) {
          int length = names.length;
          for (int i = 0; i < length; i++) {
            propertySources.put(new CaselessStringKey(names[i]), propertySource);
          }
        }
      }
    }
  }
  








  public void clearProperties()
  {
    synchronized (properties) {
      String[] names = getPropertyNames();
      if (names != null) {
        int length = names.length;
        for (int i = 0; i < length; i++) {
          removeProperty(names[i]);
        }
      }
    }
  }
  






  public void clearPropertyMap()
  {
    synchronized (properties) {
      Iterator keys = properties.keySet().iterator();
      while (keys.hasNext()) {
        CaselessStringKey key = (CaselessStringKey)keys.next();
        Object oldValue = properties.get(key);
        keys.remove();
        
        if (manager != null) {
          Object eventSource = manager.getPropertyChangeEventSource();
          
          PropertySourceChangeEvent evt = new PropertySourceChangeEvent(eventSource, key.getName(), oldValue, Image.UndefinedProperty);
          



          manager.firePropertyChange(evt);
        }
      }
      

      cachedPropertyNames.clear();
    }
  }
  



  public void clearPropertySourceMap()
  {
    synchronized (properties) {
      propertySources.clear();
    }
  }
  







  public void clearCachedProperties()
  {
    synchronized (properties) {
      Iterator names = cachedPropertyNames.iterator();
      while (names.hasNext()) {
        CaselessStringKey name = (CaselessStringKey)names.next();
        Object oldValue = properties.remove(name);
        names.remove();
        if (manager != null) {
          Object eventSource = manager.getPropertyChangeEventSource();
          
          PropertySourceChangeEvent evt = new PropertySourceChangeEvent(eventSource, name.getName(), oldValue, Image.UndefinedProperty);
          



          manager.firePropertyChange(evt);
        }
      }
    }
  }
  



  public void removePropertySource(PropertySource propertySource)
  {
    synchronized (properties) {
      Iterator keys = propertySources.keySet().iterator();
      while (keys.hasNext()) {
        Object ps = propertySources.get(keys.next());
        if (ps.equals(propertySource)) {
          keys.remove();
        }
      }
    }
  }
  







  public void addPropertyChangeListener(PropertyChangeListener listener)
  {
    getEventManager().addPropertyChangeListener(listener);
  }
  









  public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
  {
    getEventManager().addPropertyChangeListener(propertyName, listener);
  }
  








  public void removePropertyChangeListener(PropertyChangeListener listener)
  {
    getEventManager().removePropertyChangeListener(listener);
  }
  







  public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener)
  {
    getEventManager().removePropertyChangeListener(propertyName, listener);
  }
  



  private PropertyChangeSupportJAI getEventManager()
  {
    if (manager == null) {
      synchronized (this) {
        manager = new PropertyChangeSupportJAI(this);
      }
    }
    return manager;
  }
}
