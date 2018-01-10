package javax.media.jai;

import javax.media.jai.util.Range;







































public abstract interface ParameterListDescriptor
{
  public static final Object NO_PARAMETER_DEFAULT = 1.class$javax$media$jai$ParameterNoDefault == null ? (1.class$javax$media$jai$ParameterNoDefault = 1.class$("javax.media.jai.ParameterNoDefault")) : 1.class$javax$media$jai$ParameterNoDefault;
  
  public abstract int getNumParameters();
  
  public abstract Class[] getParamClasses();
  
  public abstract String[] getParamNames();
  
  public abstract Object[] getParamDefaults();
  
  public abstract Object getParamDefaultValue(String paramString);
  
  public abstract Range getParamValueRange(String paramString);
  
  public abstract String[] getEnumeratedParameterNames();
  
  public abstract EnumeratedParameter[] getEnumeratedParameterValues(String paramString);
  
  public abstract boolean isParameterValueValid(String paramString, Object paramObject);
}
