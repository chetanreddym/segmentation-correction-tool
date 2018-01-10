package javax.media.jai;

public abstract interface ParameterList
{
  public abstract ParameterListDescriptor getParameterListDescriptor();
  
  public abstract ParameterList setParameter(String paramString, byte paramByte);
  
  public abstract ParameterList setParameter(String paramString, boolean paramBoolean);
  
  public abstract ParameterList setParameter(String paramString, char paramChar);
  
  public abstract ParameterList setParameter(String paramString, short paramShort);
  
  public abstract ParameterList setParameter(String paramString, int paramInt);
  
  public abstract ParameterList setParameter(String paramString, long paramLong);
  
  public abstract ParameterList setParameter(String paramString, float paramFloat);
  
  public abstract ParameterList setParameter(String paramString, double paramDouble);
  
  public abstract ParameterList setParameter(String paramString, Object paramObject);
  
  public abstract Object getObjectParameter(String paramString);
  
  public abstract byte getByteParameter(String paramString);
  
  public abstract boolean getBooleanParameter(String paramString);
  
  public abstract char getCharParameter(String paramString);
  
  public abstract short getShortParameter(String paramString);
  
  public abstract int getIntParameter(String paramString);
  
  public abstract long getLongParameter(String paramString);
  
  public abstract float getFloatParameter(String paramString);
  
  public abstract double getDoubleParameter(String paramString);
}
