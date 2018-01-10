package javax.media.jai;

import java.beans.PropertyChangeEvent;







































public class PropertyChangeEventJAI
  extends PropertyChangeEvent
{
  private String originalPropertyName;
  
  public PropertyChangeEventJAI(Object source, String propertyName, Object oldValue, Object newValue)
  {
    super(source, propertyName.toLowerCase(), oldValue, newValue);
    
    if (source == null)
      throw new IllegalArgumentException(JaiI18N.getString("PropertyChangeEventJAI0"));
    if ((oldValue == null) && (newValue == null)) {
      throw new IllegalArgumentException(JaiI18N.getString("PropertyChangeEventJAI1"));
    }
    
    originalPropertyName = (propertyName.equals(getPropertyName()) ? getPropertyName() : propertyName);
  }
  




  public String getOriginalPropertyName()
  {
    return originalPropertyName;
  }
}
