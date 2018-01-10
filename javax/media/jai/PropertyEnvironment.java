package javax.media.jai;

import com.sun.media.jai.util.CaselessStringKeyHashtable;
import com.sun.media.jai.util.PropertyUtil;
import java.awt.Image;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import javax.media.jai.util.CaselessStringKey;




























class PropertyEnvironment
  implements PropertySource
{
  Vector pg;
  Vector sources;
  private static final Object PRESENT = new Object();
  




  CaselessStringKeyHashtable suppressed;
  




  CaselessStringKeyHashtable sourceForProp;
  




  private Object op;
  



  private CaselessStringKeyHashtable propNames;
  



  private PropertySource defaultPropertySource = null;
  





  private boolean areDefaultsMapped = true;
  












  public PropertyEnvironment(Vector sources, Vector generators, Vector suppressed, Hashtable sourceForProp, Object op)
  {
    this.sources = sources;
    pg = (generators == null ? null : (Vector)generators.clone());
    


    this.suppressed = new CaselessStringKeyHashtable();
    
    if (suppressed != null) {
      Enumeration e = suppressed.elements();
      
      while (e.hasMoreElements()) {
        this.suppressed.put(e.nextElement(), PRESENT);
      }
    }
    
    this.sourceForProp = (sourceForProp == null ? null : new CaselessStringKeyHashtable(sourceForProp));
    

    this.op = op;
    
    hashNames();
  }
  





  public String[] getPropertyNames()
  {
    mapDefaults();
    
    int count = 0;
    String[] names = new String[propNames.size()];
    for (Enumeration e = propNames.keys(); e.hasMoreElements();) {
      names[(count++)] = ((CaselessStringKey)e.nextElement()).getName();
    }
    
    return names;
  }
  












  public String[] getPropertyNames(String prefix)
  {
    String[] propertyNames = getPropertyNames();
    return PropertyUtil.getPropertyNames(propertyNames, prefix);
  }
  










  public Class getPropertyClass(String propertyName)
  {
    if (propertyName == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    return null;
  }
  





  public Object getProperty(String name)
  {
    if (name == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    mapDefaults();
    
    Object o = propNames.get(name);
    
    Object property = null;
    if (o == null)
      return Image.UndefinedProperty;
    if ((o instanceof PropertyGenerator)) {
      property = ((PropertyGenerator)o).getProperty(name, op);
    } else if ((o instanceof Integer)) {
      int srcIndex = ((Integer)o).intValue();
      PropertySource src = (PropertySource)sources.elementAt(srcIndex);
      property = src.getProperty(name);
    } else if ((o instanceof PropertySource)) {
      property = ((PropertySource)o).getProperty(name);
    }
    
    return property;
  }
  

  public void copyPropertyFromSource(String propertyName, int sourceIndex)
  {
    PropertySource propertySource = (PropertySource)sources.elementAt(sourceIndex);
    
    propNames.put(propertyName, propertySource);
    suppressed.remove(propertyName);
  }
  
  public void suppressProperty(String propertyName) {
    suppressed.put(propertyName, PRESENT);
    hashNames();
  }
  
  public void addPropertyGenerator(PropertyGenerator generator) {
    if (pg == null) {
      pg = new Vector();
    }
    pg.addElement(generator);
    


    removeSuppressedProps(generator);
    hashNames();
  }
  










  public void setDefaultPropertySource(PropertySource ps)
  {
    if (ps == defaultPropertySource) {
      return;
    }
    
    if (defaultPropertySource != null)
    {
      hashNames();
    }
    

    areDefaultsMapped = false;
    

    defaultPropertySource = ps;
  }
  





  private void mapDefaults()
  {
    if (!areDefaultsMapped)
    {
      areDefaultsMapped = true;
      

      if (defaultPropertySource != null) {
        String[] names = defaultPropertySource.getPropertyNames();
        if (names != null) {
          int length = names.length;
          for (int i = 0; i < length; i++) {
            if (!suppressed.containsKey(names[i])) {
              Object o = propNames.get(names[i]);
              if ((o == null) || ((o instanceof Integer)))
              {


                propNames.put(names[i], defaultPropertySource);
              }
            }
          }
        }
      }
    }
  }
  
  private void removeSuppressedProps(PropertyGenerator generator) {
    String[] names = generator.getPropertyNames();
    for (int i = 0; i < names.length; i++) {
      suppressed.remove(names[i]);
    }
  }
  
  private void hashNames() {
    propNames = new CaselessStringKeyHashtable();
    



    if (sources != null)
    {
      for (int i = sources.size() - 1; i >= 0; i--) {
        Object o = sources.elementAt(i);
        
        if ((o instanceof PropertySource)) {
          PropertySource source = (PropertySource)o;
          String[] propertyNames = source.getPropertyNames();
          
          if (propertyNames != null) {
            for (int j = 0; j < propertyNames.length; j++) {
              String name = propertyNames[j];
              if (!suppressed.containsKey(name)) {
                propNames.put(name, new Integer(i));
              }
            }
          }
        }
      }
    }
    
    Iterator it;
    
    if (pg != null)
    {

      for (it = pg.iterator(); it.hasNext();) {
        PropertyGenerator generator = (PropertyGenerator)it.next();
        if (generator.canGenerateProperties(op)) {
          String[] propertyNames = generator.getPropertyNames();
          if (propertyNames != null) {
            for (int i = 0; i < propertyNames.length; i++) {
              String name = propertyNames[i];
              if (!suppressed.containsKey(name)) {
                propNames.put(name, generator);
              }
            }
          }
        }
      }
    }
    
    Enumeration e;
    
    if (sourceForProp != null) {
      for (e = sourceForProp.keys(); e.hasMoreElements();) {
        CaselessStringKey name = (CaselessStringKey)e.nextElement();
        
        if (!suppressed.containsKey(name)) {
          Integer i = (Integer)sourceForProp.get(name);
          PropertySource propertySource = (PropertySource)sources.elementAt(i.intValue());
          
          propNames.put(name, propertySource);
        }
      }
    }
    

    areDefaultsMapped = false;
  }
}
