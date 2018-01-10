package javax.media.jai;








































public class PropertySourceChangeEvent
  extends PropertyChangeEventJAI
{
  public PropertySourceChangeEvent(Object source, String propertyName, Object oldValue, Object newValue)
  {
    super(source, propertyName, oldValue, newValue);
    


    if (oldValue == null)
      throw new IllegalArgumentException(JaiI18N.getString("PropertySourceChangeEvent0"));
    if (newValue == null) {
      throw new IllegalArgumentException(JaiI18N.getString("PropertySourceChangeEvent1"));
    }
  }
}
