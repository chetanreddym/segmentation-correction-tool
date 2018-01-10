package javax.media.jai;

import com.sun.media.jai.util.CaselessStringArrayTable;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Locale;




















































public class ParameterListImpl
  implements ParameterList, Serializable
{
  private ParameterListDescriptor pld;
  private CaselessStringArrayTable paramIndices;
  private Object[] paramValues;
  private Class[] paramClasses;
  
  public ParameterListImpl(ParameterListDescriptor descriptor)
  {
    if (descriptor == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    pld = descriptor;
    
    int numParams = pld.getNumParameters();
    
    if (numParams > 0)
    {
      Object[] paramDefaults = pld.getParamDefaults();
      
      paramClasses = pld.getParamClasses();
      paramIndices = new CaselessStringArrayTable(pld.getParamNames());
      paramValues = new Object[numParams];
      
      for (int i = 0; i < numParams; i++) {
        paramValues[i] = paramDefaults[i];
      }
    }
    else {
      paramClasses = null;
      paramIndices = null;
      paramValues = null;
    }
  }
  


  public ParameterListDescriptor getParameterListDescriptor()
  {
    return pld;
  }
  

















  private ParameterList setParameter0(String paramName, Object obj)
  {
    int index = paramIndices.indexOf(paramName);
    
    if ((obj != null) && (!paramClasses[index].isInstance(obj))) {
      throw new IllegalArgumentException(formatMsg(JaiI18N.getString("ParameterListImpl0"), new Object[] { obj.getClass().getName(), paramClasses[index].getName(), paramName }));
    }
    



    if (!pld.isParameterValueValid(paramName, obj)) {
      throw new IllegalArgumentException(paramName + ":" + JaiI18N.getString("ParameterListImpl1"));
    }
    
    paramValues[index] = obj;
    
    return this;
  }
  














  public ParameterList setParameter(String paramName, byte b)
  {
    return setParameter0(paramName, new Byte(b));
  }
  














  public ParameterList setParameter(String paramName, boolean b)
  {
    return setParameter0(paramName, new Boolean(b));
  }
  














  public ParameterList setParameter(String paramName, char c)
  {
    return setParameter0(paramName, new Character(c));
  }
  














  public ParameterList setParameter(String paramName, short s)
  {
    return setParameter0(paramName, new Short(s));
  }
  














  public ParameterList setParameter(String paramName, int i)
  {
    return setParameter0(paramName, new Integer(i));
  }
  














  public ParameterList setParameter(String paramName, long l)
  {
    return setParameter0(paramName, new Long(l));
  }
  














  public ParameterList setParameter(String paramName, float f)
  {
    return setParameter0(paramName, new Float(f));
  }
  














  public ParameterList setParameter(String paramName, double d)
  {
    return setParameter0(paramName, new Double(d));
  }
  












  public ParameterList setParameter(String paramName, Object obj)
  {
    return setParameter0(paramName, obj);
  }
  










  private Object getObjectParameter0(String paramName)
  {
    Object obj = paramValues[paramIndices.indexOf(paramName)];
    
    if (obj == ParameterListDescriptor.NO_PARAMETER_DEFAULT) {
      throw new IllegalStateException(paramName + ":" + JaiI18N.getString("ParameterListImpl2"));
    }
    
    return obj;
  }
  













  public Object getObjectParameter(String paramName)
  {
    return getObjectParameter0(paramName);
  }
  











  public byte getByteParameter(String paramName)
  {
    return ((Byte)getObjectParameter0(paramName)).byteValue();
  }
  











  public boolean getBooleanParameter(String paramName)
  {
    return ((Boolean)getObjectParameter0(paramName)).booleanValue();
  }
  











  public char getCharParameter(String paramName)
  {
    return ((Character)getObjectParameter0(paramName)).charValue();
  }
  











  public short getShortParameter(String paramName)
  {
    return ((Short)getObjectParameter0(paramName)).shortValue();
  }
  











  public int getIntParameter(String paramName)
  {
    return ((Integer)getObjectParameter0(paramName)).intValue();
  }
  











  public long getLongParameter(String paramName)
  {
    return ((Long)getObjectParameter0(paramName)).longValue();
  }
  











  public float getFloatParameter(String paramName)
  {
    return ((Float)getObjectParameter0(paramName)).floatValue();
  }
  











  public double getDoubleParameter(String paramName)
  {
    return ((Double)getObjectParameter0(paramName)).doubleValue();
  }
  



  private String formatMsg(String key, Object[] args)
  {
    MessageFormat mf = new MessageFormat(key);
    mf.setLocale(Locale.getDefault());
    
    return mf.format(args);
  }
}
