package javax.media.jai;

import com.sun.media.jai.util.CaselessStringArrayTable;
import java.awt.image.renderable.ParameterBlock;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;













































































































public class ParameterBlockJAI
  extends ParameterBlock
  implements ParameterList
{
  private transient OperationDescriptor odesc;
  private String modeName;
  private ParameterListDescriptor pld;
  private CaselessStringArrayTable paramIndices;
  private CaselessStringArrayTable sourceIndices;
  private int numParameters;
  private String[] paramNames;
  private Class[] paramClasses;
  private Class[] sourceClasses;
  
  private static String getDefaultMode(OperationDescriptor odesc)
  {
    if (odesc == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    return odesc.getSupportedModes()[0];
  }
  














  public ParameterBlockJAI(OperationDescriptor odesc)
  {
    this(odesc, getDefaultMode(odesc));
  }
  
















  public ParameterBlockJAI(String operationName)
  {
    this((OperationDescriptor)JAI.getDefaultInstance().getOperationRegistry().getDescriptor(OperationDescriptor.class, operationName));
  }
  
















  public ParameterBlockJAI(OperationDescriptor odesc, String modeName)
  {
    if ((odesc == null) || (modeName == null)) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    this.odesc = odesc;
    this.modeName = modeName;
    
    pld = odesc.getParameterListDescriptor(modeName);
    
    numParameters = pld.getNumParameters();
    paramNames = pld.getParamNames();
    
    paramIndices = new CaselessStringArrayTable(pld.getParamNames());
    sourceIndices = new CaselessStringArrayTable(odesc.getSourceNames());
    
    paramClasses = pld.getParamClasses();
    sourceClasses = odesc.getSourceClasses(modeName);
    
    Object[] defaults = pld.getParamDefaults();
    
    parameters = new Vector(numParameters);
    
    for (int i = 0; i < numParameters; i++) {
      parameters.addElement(defaults[i]);
    }
  }
  















  public ParameterBlockJAI(String operationName, String modeName)
  {
    this((OperationDescriptor)JAI.getDefaultInstance().getOperationRegistry().getDescriptor(modeName, operationName), modeName);
  }
  











  public int indexOfSource(String sourceName)
  {
    return sourceIndices.indexOf(sourceName);
  }
  










  public int indexOfParam(String paramName)
  {
    return paramIndices.indexOf(paramName);
  }
  



  public OperationDescriptor getOperationDescriptor()
  {
    return odesc;
  }
  






  public ParameterListDescriptor getParameterListDescriptor()
  {
    return pld;
  }
  





  public String getMode()
  {
    return modeName;
  }
  















  public ParameterBlockJAI setSource(String sourceName, Object source)
  {
    if ((source == null) || (sourceName == null)) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    

    int index = indexOfSource(sourceName);
    
    if (!sourceClasses[index].isInstance(source)) {
      throw new IllegalArgumentException(JaiI18N.getString("ParameterBlockJAI4"));
    }
    

    if (index >= odesc.getNumSources()) {
      addSource(source);
    } else {
      setSource(source, index);
    }
    
    return this;
  }
  







  public Class[] getParamClasses()
  {
    return paramClasses;
  }
  






  private Object getObjectParameter0(String paramName)
  {
    Object obj = getObjectParameter(indexOfParam(paramName));
    
    if (obj == ParameterListDescriptor.NO_PARAMETER_DEFAULT) {
      throw new IllegalStateException(paramName + ":" + JaiI18N.getString("ParameterBlockJAI6"));
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
  















  private int checkParameter(String paramName, Object obj)
  {
    int index = indexOfParam(paramName);
    
    if (obj != null)
    {
      if (obj == ParameterListDescriptor.NO_PARAMETER_DEFAULT) {
        throw new IllegalArgumentException(paramName + ":" + JaiI18N.getString("ParameterBlockJAI8"));
      }
      

      if ((obj instanceof DeferredData)) {
        DeferredData dd = (DeferredData)obj;
        if (!paramClasses[index].isAssignableFrom(dd.getDataClass())) {
          throw new IllegalArgumentException(paramName + ":" + JaiI18N.getString("ParameterBlockJAI0"));
        }
        

        if ((dd.isValid()) && (!pld.isParameterValueValid(paramName, dd.getData())))
        {
          throw new IllegalArgumentException(paramName + ":" + JaiI18N.getString("ParameterBlockJAI2"));
        }
      }
      else if (!paramClasses[index].isInstance(obj)) {
        throw new IllegalArgumentException(paramName + ":" + JaiI18N.getString("ParameterBlockJAI0"));
      }
    }
    

    if (((obj == null) || (!(obj instanceof DeferredData))) && 
      (!pld.isParameterValueValid(paramName, obj))) {
      throw new IllegalArgumentException(paramName + ":" + JaiI18N.getString("ParameterBlockJAI2"));
    }
    


    return index;
  }
  






















  private ParameterList setParameter0(String paramName, Object obj)
  {
    int index = checkParameter(paramName, obj);
    
    parameters.setElementAt(obj, index);
    return this;
  }
  














  public ParameterBlock add(Object obj)
  {
    throw new IllegalStateException(JaiI18N.getString("ParameterBlockJAI5"));
  }
  















  public ParameterBlock set(Object obj, int index)
  {
    if ((index < 0) || (index >= pld.getNumParameters())) {
      throw new ArrayIndexOutOfBoundsException();
    }
    

    setParameter0(paramNames[index], obj);
    
    return this;
  }
  



















  public void setParameters(Vector parameters)
  {
    if ((parameters == null) || (parameters.size() != numParameters)) {
      throw new IllegalArgumentException(JaiI18N.getString("ParameterBlockJAI7"));
    }
    
    for (int i = 0; i < numParameters; i++) {
      checkParameter(paramNames[i], parameters.get(i));
    }
    
    this.parameters = parameters;
  }
  











  /**
   * @deprecated
   */
  public int indexOf(String paramName)
  {
    return indexOfParam(paramName);
  }
  










  /**
   * @deprecated
   */
  public ParameterBlock set(byte b, String paramName)
  {
    return set(new Byte(b), paramName);
  }
  










  /**
   * @deprecated
   */
  public ParameterBlock set(char c, String paramName)
  {
    return set(new Character(c), paramName);
  }
  










  /**
   * @deprecated
   */
  public ParameterBlock set(short s, String paramName)
  {
    return set(new Short(s), paramName);
  }
  










  /**
   * @deprecated
   */
  public ParameterBlock set(int i, String paramName)
  {
    return set(new Integer(i), paramName);
  }
  










  /**
   * @deprecated
   */
  public ParameterBlock set(long l, String paramName)
  {
    return set(new Long(l), paramName);
  }
  










  /**
   * @deprecated
   */
  public ParameterBlock set(float f, String paramName)
  {
    return set(new Float(f), paramName);
  }
  










  /**
   * @deprecated
   */
  public ParameterBlock set(double d, String paramName)
  {
    return set(new Double(d), paramName);
  }
  













  /**
   * @deprecated
   */
  public ParameterBlock set(Object obj, String paramName)
  {
    setParameter0(paramName, obj);
    return this;
  }
  





  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.defaultWriteObject();
    

    out.writeObject(odesc.getName());
  }
  









  private void readObject(ObjectInputStream in)
    throws IOException, ClassNotFoundException
  {
    in.defaultReadObject();
    

    String operationName = (String)in.readObject();
    

    odesc = ((OperationDescriptor)JAI.getDefaultInstance().getOperationRegistry().getDescriptor(modeName, operationName));
    


    if (odesc == null) {
      throw new NotSerializableException(operationName + " " + JaiI18N.getString("ParameterBlockJAI1"));
    }
  }
  













  public Object clone()
  {
    ParameterBlockJAI theClone = (ParameterBlockJAI)shallowClone();
    
    if (sources != null) {
      theClone.setSources((Vector)sources.clone());
    }
    
    if (parameters != null)
    {

      parameters = ((Vector)parameters.clone());
    }
    return theClone;
  }
}
