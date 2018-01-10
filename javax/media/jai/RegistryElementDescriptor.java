package javax.media.jai;

public abstract interface RegistryElementDescriptor
{
  public abstract String getName();
  
  public abstract String[] getSupportedModes();
  
  public abstract boolean isModeSupported(String paramString);
  
  public abstract boolean arePropertiesSupported();
  
  public abstract PropertyGenerator[] getPropertyGenerators(String paramString);
  
  public abstract ParameterListDescriptor getParameterListDescriptor(String paramString);
}
