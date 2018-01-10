package javax.media.jai;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;










































public class DeferredProperty
  extends DeferredData
  implements PropertyChangeListener
{
  protected transient PropertySource propertySource;
  protected String propertyName;
  
  public DeferredProperty(PropertySource propertySource, String propertyName, Class propertyClass)
  {
    super(propertyClass);
    
    if ((propertySource == null) || (propertyName == null)) {
      throw new IllegalArgumentException(JaiI18N.getString("DeferredData0"));
    }
    
    String[] propertyNames = propertySource.getPropertyNames();
    boolean isPropertyEmitted = false;
    if (propertyNames != null) {
      int length = propertyNames.length;
      for (int i = 0; i < length; i++) {
        if (propertyName.equalsIgnoreCase(propertyNames[i])) {
          isPropertyEmitted = true;
          break;
        }
      }
    }
    
    if (!isPropertyEmitted) {
      throw new IllegalArgumentException(JaiI18N.getString("DeferredProperty0"));
    }
    
    if ((propertySource instanceof PropertyChangeEmitter)) {
      PropertyChangeEmitter pce = (PropertyChangeEmitter)propertySource;
      
      pce.addPropertyChangeListener(propertyName, this);
    }
    
    this.propertySource = propertySource;
    this.propertyName = propertyName;
  }
  


  public PropertySource getPropertySource()
  {
    return propertySource;
  }
  


  public String getPropertyName()
  {
    return propertyName;
  }
  



  protected Object computeData()
  {
    return propertySource.getProperty(propertyName);
  }
  










  public boolean equals(Object obj)
  {
    if ((obj == null) || (!(obj instanceof DeferredProperty))) {
      return false;
    }
    
    DeferredProperty dp = (DeferredProperty)obj;
    
    return (propertyName.equalsIgnoreCase(dp.getPropertyName())) && (propertySource.equals(dp.getPropertySource())) && ((!isValid()) || (!dp.isValid()) || (data.equals(dp.getData())));
  }
  




  public int hashCode()
  {
    return propertySource.hashCode() ^ propertyName.toLowerCase().hashCode();
  }
  


























  public void propertyChange(PropertyChangeEvent evt)
  {
    if (evt.getSource() == propertySource) {
      if ((evt instanceof RenderingChangeEvent)) {
        setData(null);
      } else if (((evt instanceof PropertySourceChangeEvent)) && (propertyName.equalsIgnoreCase(evt.getPropertyName())))
      {
        Object newValue = evt.getNewValue();
        setData(newValue == Image.UndefinedProperty ? null : newValue);
      }
    }
  }
}
