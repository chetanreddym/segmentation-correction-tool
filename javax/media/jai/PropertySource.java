package javax.media.jai;

public abstract interface PropertySource
{
  public abstract String[] getPropertyNames();
  
  public abstract String[] getPropertyNames(String paramString);
  
  public abstract Class getPropertyClass(String paramString);
  
  public abstract Object getProperty(String paramString);
}
