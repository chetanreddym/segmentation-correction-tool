package javax.media.jai;

public abstract interface WritablePropertySource
  extends PropertySource, PropertyChangeEmitter
{
  public abstract void setProperty(String paramString, Object paramObject);
  
  public abstract void removeProperty(String paramString);
}
