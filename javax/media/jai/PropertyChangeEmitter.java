package javax.media.jai;

import java.beans.PropertyChangeListener;

public abstract interface PropertyChangeEmitter
{
  public abstract void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener);
  
  public abstract void addPropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener);
  
  public abstract void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener);
  
  public abstract void removePropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener);
}
