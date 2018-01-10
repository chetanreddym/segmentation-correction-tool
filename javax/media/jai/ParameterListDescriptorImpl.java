package javax.media.jai;

import com.sun.media.jai.util.CaselessStringArrayTable;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import javax.media.jai.util.Range;






















































public class ParameterListDescriptorImpl
  implements ParameterListDescriptor, Serializable
{
  private int numParams;
  private String[] paramNames;
  private Class[] paramClasses;
  private Object[] paramDefaults;
  private Object[] validParamValues;
  private CaselessStringArrayTable paramIndices;
  private Object descriptor;
  private boolean validParamsInitialized = false;
  
















  public static Set getEnumeratedValues(Object descriptor, Class paramClass)
  {
    if ((descriptor == null) || (paramClass == null)) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if (!EnumeratedParameter.class.isAssignableFrom(paramClass)) {
      throw new IllegalArgumentException(JaiI18N.formatMsg("ParameterListDescriptorImpl10", new Object[] { paramClass.getName() }));
    }
    

    Field[] fields = descriptor.getClass().getDeclaredFields();
    
    if (fields == null) {
      return null;
    }
    
    int numFields = fields.length;
    Set valueSet = null;
    


    for (int j = 0; j < numFields; j++) {
      Field field = fields[j];
      int modifiers = field.getModifiers();
      if ((Modifier.isPublic(modifiers)) && (Modifier.isStatic(modifiers)) && (Modifier.isFinal(modifiers)))
      {

        Object fieldValue = null;
        try {
          fieldValue = field.get(null);
        }
        catch (Exception e) {}
        
        if (paramClass.isInstance(fieldValue)) {
          if (valueSet == null) {
            valueSet = new HashSet();
          }
          
          if (valueSet.contains(fieldValue))
          {



            throw new UnsupportedOperationException(JaiI18N.getString("ParameterListDescriptorImpl0"));
          }
          

          valueSet.add(fieldValue);
        }
      }
    }
    
    return valueSet;
  }
  





  private Object getValidParamValue(int index)
  {
    if (validParamsInitialized) {
      return validParamValues[index];
    }
    synchronized (this) {
      if (validParamValues == null) {
        validParamValues = new Object[numParams];
      }
      
      Class enumeratedClass = EnumeratedParameter.class;
      
      for (int i = 0; i < numParams; i++) {
        if (validParamValues[i] == null)
        {

          if (enumeratedClass.isAssignableFrom(paramClasses[i])) {
            validParamValues[i] = getEnumeratedValues(descriptor, paramClasses[i]);
          }
        }
      }
    }
    
    validParamsInitialized = true;
    
    return validParamValues[index];
  }
  


  public ParameterListDescriptorImpl()
  {
    numParams = 0;
    paramNames = null;
    paramClasses = null;
    paramDefaults = null;
    paramIndices = new CaselessStringArrayTable();
    validParamValues = null;
  }
  











































  public ParameterListDescriptorImpl(Object descriptor, String[] paramNames, Class[] paramClasses, Object[] paramDefaults, Object[] validParamValues)
  {
    int numParams = paramNames == null ? 0 : paramNames.length;
    
    if ((paramDefaults != null) && (paramDefaults.length != numParams)) {
      throw new IllegalArgumentException("paramDefaults" + JaiI18N.getString("ParameterListDescriptorImpl1"));
    }
    
    if ((validParamValues != null) && (validParamValues.length != numParams)) {
      throw new IllegalArgumentException("validParamValues" + JaiI18N.getString("ParameterListDescriptorImpl2"));
    }
    
    this.descriptor = descriptor;
    
    if (numParams == 0)
    {
      if ((paramClasses != null) && (paramClasses.length != 0)) {
        throw new IllegalArgumentException("paramClasses" + JaiI18N.getString("ParameterListDescriptorImpl3"));
      }
      
      this.numParams = 0;
      this.paramNames = null;
      this.paramClasses = null;
      this.paramDefaults = null;
      paramIndices = new CaselessStringArrayTable();
      this.validParamValues = null;
    }
    else
    {
      if ((paramClasses == null) || (paramClasses.length != numParams)) {
        throw new IllegalArgumentException("paramClasses" + JaiI18N.getString("ParameterListDescriptorImpl3"));
      }
      
      this.numParams = numParams;
      this.paramNames = paramNames;
      this.paramClasses = paramClasses;
      this.validParamValues = validParamValues;
      




      if (paramDefaults == null) {
        this.paramDefaults = new Object[numParams];
        
        for (int i = 0; i < numParams; i++) {
          this.paramDefaults[i] = ParameterListDescriptor.NO_PARAMETER_DEFAULT;
        }
      }
      else {
        this.paramDefaults = paramDefaults;
        
        for (int i = 0; i < numParams; i++) {
          if ((paramDefaults[i] != null) && (paramDefaults[i] != ParameterListDescriptor.NO_PARAMETER_DEFAULT))
          {



            if (!paramClasses[i].isInstance(paramDefaults[i])) {
              throw new IllegalArgumentException(JaiI18N.formatMsg("ParameterListDescriptorImpl4", new Object[] { paramDefaults[i].getClass().getName(), paramClasses[i].getName(), paramNames[i] }));
            }
          }
        }
      }
      







      if (validParamValues != null)
      {
        Class enumeratedClass = EnumeratedParameter.class;
        
        for (int i = 0; i < numParams; i++)
        {
          if (validParamValues[i] != null)
          {

            if (enumeratedClass.isAssignableFrom(paramClasses[i]))
            {


              if (!(validParamValues[i] instanceof Set)) {
                throw new IllegalArgumentException(JaiI18N.formatMsg("ParameterListDescriptorImpl5", new Object[] { paramNames[i] }));
              }
              
            }
            else if ((validParamValues[i] instanceof Range))
            {
              Range range = (Range)validParamValues[i];
              


              if (!paramClasses[i].isAssignableFrom(range.getElementClass()))
              {
                throw new IllegalArgumentException(JaiI18N.formatMsg("ParameterListDescriptorImpl6", new Object[] { range.getElementClass().getName(), paramClasses[i].getName(), paramNames[i] }));



              }
              




            }
            else if (!paramClasses[i].isInstance(validParamValues[i])) {
              throw new IllegalArgumentException(JaiI18N.formatMsg("ParameterListDescriptorImpl7", new Object[] { validParamValues[i].getClass().getName(), paramClasses[i].getName(), paramNames[i] }));
            }
          }
        }
      }
      




      paramIndices = new CaselessStringArrayTable(paramNames);
    }
  }
  


  public int getNumParameters()
  {
    return numParams;
  }
  




  public Class[] getParamClasses()
  {
    return paramClasses;
  }
  




  public String[] getParamNames()
  {
    return paramNames;
  }
  






  public Object[] getParamDefaults()
  {
    return paramDefaults;
  }
  










  public Object getParamDefaultValue(String parameterName)
  {
    return paramDefaults[paramIndices.indexOf(parameterName)];
  }
  












  public Range getParamValueRange(String parameterName)
  {
    Object values = getValidParamValue(paramIndices.indexOf(parameterName));
    
    if ((values == null) || ((values instanceof Range))) {
      return (Range)values;
    }
    return null;
  }
  







  public String[] getEnumeratedParameterNames()
  {
    Vector v = new Vector();
    
    for (int i = 0; i < numParams; i++) {
      if (EnumeratedParameter.class.isAssignableFrom(paramClasses[i])) {
        v.add(paramNames[i]);
      }
    }
    if (v.size() <= 0) {
      return null;
    }
    return (String[])v.toArray(new String[0]);
  }
  


















  public EnumeratedParameter[] getEnumeratedParameterValues(String parameterName)
  {
    int i = paramIndices.indexOf(parameterName);
    
    if (!EnumeratedParameter.class.isAssignableFrom(paramClasses[i])) {
      throw new IllegalArgumentException(parameterName + ":" + JaiI18N.getString("ParameterListDescriptorImpl8"));
    }
    
    Set enumSet = (Set)getValidParamValue(i);
    
    if (enumSet == null) {
      return null;
    }
    return (EnumeratedParameter[])enumSet.toArray(new EnumeratedParameter[0]);
  }
  
















  public boolean isParameterValueValid(String parameterName, Object value)
  {
    int index = paramIndices.indexOf(parameterName);
    
    if ((value == null) && (paramDefaults[index] == null)) {
      return true;
    }
    

    if ((value != null) && (!paramClasses[index].isInstance(value))) {
      throw new IllegalArgumentException(JaiI18N.formatMsg("ParameterListDescriptorImpl9", new Object[] { value.getClass().getName(), paramClasses[index].getName(), parameterName }));
    }
    




    Object validValues = getValidParamValue(index);
    

    if (validValues == null) {
      return true;
    }
    
    if ((validValues instanceof Range)) {
      return ((Range)validValues).contains((Comparable)value);
    }
    

    if ((validValues instanceof Set)) {
      return ((Set)validValues).contains(value);
    }
    
    return value == validValues;
  }
}
