package javax.media.jai.remote;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import javax.media.jai.ParameterList;
import javax.media.jai.ParameterListDescriptor;
import javax.media.jai.ParameterListDescriptorImpl;
import javax.media.jai.ParameterListImpl;
import javax.media.jai.util.CaselessStringKey;
































































































































































































public class NegotiableCapability
  extends ParameterListImpl
  implements Serializable
{
  private String category;
  private String capabilityName;
  private List generators;
  private boolean isPreference = false;
  





























  public NegotiableCapability(String category, String capabilityName, List generators, ParameterListDescriptor descriptor, boolean isPreference)
  {
    super(descriptor);
    
    if (category == null) {
      throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapability0"));
    }
    

    if (capabilityName == null) {
      throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapability1"));
    }
    

    ParameterListDescriptor desc = getParameterListDescriptor();
    int numParams = desc.getNumParameters();
    String[] names = desc.getParamNames();
    Class[] classes = desc.getParamClasses();
    Object[] defaults = desc.getParamDefaults();
    
    for (int i = 0; i < numParams; i++)
    {

      if (!Negotiable.class.isAssignableFrom(classes[i])) {
        throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapability4"));
      }
      

      if (defaults[i] == ParameterListDescriptor.NO_PARAMETER_DEFAULT) {
        throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapability5"));
      }
    }
    

    this.category = category;
    this.capabilityName = capabilityName;
    this.generators = generators;
    this.isPreference = isPreference;
  }
  


  public String getCategory()
  {
    return category;
  }
  


  public String getCapabilityName()
  {
    return capabilityName;
  }
  





  public List getGenerators()
  {
    return generators;
  }
  






  public void setGenerators(List generators)
  {
    this.generators = generators;
  }
  



  public boolean isPreference()
  {
    return isPreference;
  }
  


















  public Object getNegotiatedValue(String parameterName)
  {
    Negotiable value = (Negotiable)getObjectParameter(parameterName);
    if (value == null)
      return null;
    return value.getNegotiatedValue();
  }
  












































































  public NegotiableCapability negotiate(NegotiableCapability capability)
  {
    if (capability == null) {
      return null;
    }
    
    if ((!capability.getCategory().equalsIgnoreCase(category)) || (!capability.getCapabilityName().equalsIgnoreCase(capabilityName)))
    {


      return null;
    }
    


    if (!areParameterListDescriptorsCompatible(capability)) {
      return null;
    }
    int negStatus;
    int negStatus;
    if (capability.isPreference() == true) { int negStatus;
      if (isPreference == true) {
        negStatus = 0;
      } else
        negStatus = 1;
    } else {
      int negStatus;
      if (isPreference == true) {
        negStatus = 2;
      } else {
        negStatus = 3;
      }
    }
    
    ParameterListDescriptor pld = getParameterListDescriptor();
    ParameterListDescriptor otherPld = capability.getParameterListDescriptor();
    
    String[] thisNames = pld.getParamNames();
    if (thisNames == null)
      thisNames = new String[0];
    String[] otherNames = otherPld.getParamNames();
    if (otherNames == null)
      otherNames = new String[0];
    Hashtable thisHash = hashNames(thisNames);
    Hashtable otherHash = hashNames(otherNames);
    
    Class[] thisClasses = pld.getParamClasses();
    Class[] otherClasses = otherPld.getParamClasses();
    Object[] thisDefaults = pld.getParamDefaults();
    Object[] otherDefaults = otherPld.getParamDefaults();
    
    NegotiableCapability result = null;
    

    ArrayList resultGenerators = new ArrayList();
    if (generators != null)
      resultGenerators.addAll(generators);
    if (capability.getGenerators() != null) {
      resultGenerators.addAll(capability.getGenerators());
    }
    switch (negStatus)
    {

    case 0: 
      Vector commonNames = commonElements(thisHash, otherHash);
      Hashtable commonHash = hashNames(commonNames);
      Vector thisExtras = removeAll(thisHash, commonHash);
      Vector otherExtras = removeAll(otherHash, commonHash);
      
      int thisExtraLength = thisExtras.size();
      int otherExtraLength = otherExtras.size();
      


      Vector resultParams = new Vector(commonNames);
      resultParams.addAll(thisExtras);
      resultParams.addAll(otherExtras);
      int resultLength = resultParams.size();
      String[] resultNames = new String[resultLength];
      for (int i = 0; i < resultLength; i++) {
        resultNames[i] = ((String)resultParams.elementAt(i));
      }
      
      Class[] resultClasses = new Class[resultLength];
      Object[] resultDefaults = new Object[resultLength];
      Object[] resultValidValues = new Object[resultLength];
      

      for (int count = 0; count < commonNames.size(); count++) {
        String name = (String)commonNames.elementAt(count);
        resultClasses[count] = thisClasses[getIndex(thisHash, name)];
        resultDefaults[count] = thisDefaults[getIndex(thisHash, name)];
        resultValidValues[count] = pld.getParamValueRange(name);
      }
      for (int i = 0; i < thisExtraLength; i++) {
        String name = (String)thisExtras.elementAt(i);
        resultClasses[(count + i)] = thisClasses[getIndex(thisHash, name)];
        resultDefaults[(count + i)] = thisDefaults[getIndex(thisHash, name)];
        
        resultValidValues[(count + i)] = pld.getParamValueRange(name);
      }
      count += thisExtraLength;
      for (int i = 0; i < otherExtraLength; i++) {
        String name = (String)otherExtras.elementAt(i);
        resultClasses[(i + count)] = otherClasses[getIndex(otherHash, name)];
        
        resultDefaults[(i + count)] = otherDefaults[getIndex(otherHash, name)];
        
        resultValidValues[(i + count)] = otherPld.getParamValueRange(name);
      }
      
      ParameterListDescriptorImpl resultPLD = new ParameterListDescriptorImpl(null, resultNames, resultClasses, resultDefaults, resultValidValues);
      






      result = new NegotiableCapability(category, capabilityName, resultGenerators, resultPLD, true);
      




      for (int i = 0; i < commonNames.size(); i++) {
        String currParam = (String)commonNames.elementAt(i);
        Negotiable thisValue = (Negotiable)getObjectParameter(currParam);
        Negotiable otherValue = (Negotiable)capability.getObjectParameter(currParam);
        








        if (thisValue == null) {
          result.setParameter(currParam, otherValue);


        }
        else if (otherValue == null) {
          result.setParameter(currParam, thisValue);


        }
        else
        {


          Negotiable resultValue = thisValue.negotiate(otherValue);
          if (resultValue == null) {
            return null;
          }
          
          result.setParameter(currParam, resultValue);
        }
      }
      
      for (int i = 0; i < thisExtraLength; i++) {
        String currParam = (String)thisExtras.elementAt(i);
        result.setParameter(currParam, (Negotiable)getObjectParameter(currParam));
      }
      

      for (int i = 0; i < otherExtraLength; i++) {
        String currParam = (String)otherExtras.elementAt(i);
        result.setParameter(currParam, (Negotiable)capability.getObjectParameter(currParam));
      }
      

      break;
    


    case 1: 
      Vector commonNames = commonElements(thisHash, otherHash);
      Hashtable commonHash = hashNames(commonNames);
      Vector thisExtras = removeAll(thisHash, commonHash);
      


      Vector resultParams = new Vector(commonNames);
      resultParams.addAll(thisExtras);
      int resultLength = resultParams.size();
      String[] resultNames = new String[resultLength];
      for (int i = 0; i < resultLength; i++) {
        resultNames[i] = ((String)resultParams.elementAt(i));
      }
      
      Class[] resultClasses = new Class[resultLength];
      Object[] resultDefaults = new Object[resultLength];
      Object[] resultValidValues = new Object[resultLength];
      
      int count = 0;
      for (count = 0; count < commonNames.size(); count++) {
        String name = (String)commonNames.elementAt(count);
        resultClasses[count] = thisClasses[getIndex(thisHash, name)];
        resultDefaults[count] = thisDefaults[getIndex(thisHash, name)];
        resultValidValues[count] = pld.getParamValueRange(name);
      }
      for (int i = 0; i < thisExtras.size(); i++) {
        String name = (String)thisExtras.elementAt(i);
        resultClasses[(i + count)] = thisClasses[getIndex(thisHash, name)];
        resultDefaults[(i + count)] = thisDefaults[getIndex(thisHash, name)];
        
        resultValidValues[(i + count)] = pld.getParamValueRange(name);
      }
      
      ParameterListDescriptorImpl resultPLD = new ParameterListDescriptorImpl(null, resultNames, resultClasses, resultDefaults, resultValidValues);
      




      result = new NegotiableCapability(category, capabilityName, resultGenerators, resultPLD, false);
      




      for (int i = 0; i < commonNames.size(); i++) {
        String currParam = (String)commonNames.elementAt(i);
        Negotiable thisValue = (Negotiable)getObjectParameter(currParam);
        Negotiable otherValue = (Negotiable)capability.getObjectParameter(currParam);
        

        if (thisValue == null)
        {

          return null;
        }
        
        if (otherValue == null)
        {



          result.setParameter(currParam, thisValue);
        }
        else {
          Negotiable resultValue = thisValue.negotiate(otherValue);
          
          if (resultValue == null)
          {


            return null;
          }
          result.setParameter(currParam, resultValue);
        }
      }
      


      for (int i = 0; i < thisExtras.size(); i++) {
        String currParam = (String)thisExtras.elementAt(i);
        Negotiable resultValue = (Negotiable)getObjectParameter(currParam);
        if (resultValue == null)
          return null;
        result.setParameter(currParam, resultValue);
      }
      
      break;
    


    case 2: 
      Vector commonNames = commonElements(thisHash, otherHash);
      Hashtable commonHash = hashNames(commonNames);
      Vector otherExtras = removeAll(otherHash, commonHash);
      


      Vector resultParams = new Vector(commonNames);
      resultParams.addAll(otherExtras);
      int resultLength = resultParams.size();
      String[] resultNames = new String[resultLength];
      for (int i = 0; i < resultLength; i++) {
        resultNames[i] = ((String)resultParams.elementAt(i));
      }
      
      Class[] resultClasses = new Class[resultLength];
      Object[] resultDefaults = new Object[resultLength];
      Object[] resultValidValues = new Object[resultLength];
      int count = 0;
      for (count = 0; count < commonNames.size(); count++) {
        String name = (String)commonNames.elementAt(count);
        resultClasses[count] = thisClasses[getIndex(thisHash, name)];
        resultDefaults[count] = thisDefaults[getIndex(thisHash, name)];
        resultValidValues[count] = pld.getParamValueRange(name);
      }
      
      for (int i = 0; i < otherExtras.size(); i++) {
        String name = (String)otherExtras.elementAt(i);
        resultClasses[(i + count)] = otherClasses[getIndex(otherHash, name)];
        
        resultDefaults[(i + count)] = otherDefaults[getIndex(otherHash, name)];
        
        resultValidValues[(i + count)] = otherPld.getParamValueRange(name);
      }
      
      ParameterListDescriptorImpl resultPLD = new ParameterListDescriptorImpl(null, resultNames, resultClasses, resultDefaults, resultValidValues);
      




      result = new NegotiableCapability(category, capabilityName, resultGenerators, resultPLD, false);
      




      for (int i = 0; i < commonNames.size(); i++) {
        String currParam = (String)commonNames.elementAt(i);
        Negotiable thisValue = (Negotiable)getObjectParameter(currParam);
        Negotiable otherValue = (Negotiable)capability.getObjectParameter(currParam);
        



        if (otherValue == null) {
          return null;
        }
        
        if (thisValue == null)
        {



          result.setParameter(currParam, otherValue);
        }
        else {
          Negotiable resultValue = otherValue.negotiate(thisValue);
          
          if (resultValue == null)
          {


            return null;
          }
          result.setParameter(currParam, resultValue);
        }
      }
      

      for (int i = 0; i < otherExtras.size(); i++) {
        String currParam = (String)otherExtras.elementAt(i);
        Negotiable resultValue = (Negotiable)capability.getObjectParameter(currParam);
        
        if (resultValue == null)
          return null;
        result.setParameter(currParam, resultValue);
      }
      
      break;
    


    case 3: 
      result = new NegotiableCapability(category, capabilityName, resultGenerators, pld, false);
      

      for (int i = 0; i < thisNames.length; i++) {
        String currParam = thisNames[i];
        Negotiable thisValue = (Negotiable)getObjectParameter(currParam);
        Negotiable otherValue = (Negotiable)capability.getObjectParameter(currParam);
        



        if ((thisValue == null) || (otherValue == null))
        {
          return null;
        }
        
        Negotiable resultValue = thisValue.negotiate(otherValue);
        
        if (resultValue == null)
        {


          return null;
        }
        result.setParameter(currParam, resultValue);
      }
    }
    
    


    return result;
  }
  



















  public boolean areParameterListDescriptorsCompatible(NegotiableCapability other)
  {
    if (other == null) {
      throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapability6"));
    }
    

    ParameterListDescriptor thisDesc = getParameterListDescriptor();
    ParameterListDescriptor otherDesc = other.getParameterListDescriptor();
    
    String[] thisNames = thisDesc.getParamNames();
    if (thisNames == null)
      thisNames = new String[0];
    String[] otherNames = otherDesc.getParamNames();
    if (otherNames == null)
      otherNames = new String[0];
    Hashtable thisHash = hashNames(thisNames);
    Hashtable otherHash = hashNames(otherNames);
    
    if ((!isPreference) && (!other.isPreference()))
    {

      if (thisDesc.getNumParameters() != otherDesc.getNumParameters()) {
        return false;
      }
      
      if (!containsAll(thisHash, otherHash)) {
        return false;
      }
      Class[] thisParamClasses = thisDesc.getParamClasses();
      Class[] otherParamClasses = otherDesc.getParamClasses();
      for (int i = 0; i < thisNames.length; i++) {
        if (thisParamClasses[i] != otherParamClasses[getIndex(otherHash, thisNames[i])])
        {
          return false;
        }
      }
      return true;
    }
    

    Vector commonNames = commonElements(thisHash, otherHash);
    
    Class[] thisParamClasses = thisDesc.getParamClasses();
    Class[] otherParamClasses = otherDesc.getParamClasses();
    
    for (int i = 0; i < commonNames.size(); i++) {
      String currName = (String)commonNames.elementAt(i);
      if (thisParamClasses[getIndex(thisHash, currName)] != otherParamClasses[getIndex(otherHash, currName)])
      {
        return false;
      }
    }
    return true;
  }
  



  private boolean containsAll(Hashtable thisHash, Hashtable otherHash)
  {
    for (Enumeration i = thisHash.keys(); i.hasMoreElements();) {
      CaselessStringKey thisNameKey = (CaselessStringKey)i.nextElement();
      if (!otherHash.containsKey(thisNameKey)) {
        return false;
      }
    }
    return true;
  }
  

  private Vector removeAll(Hashtable thisHash, Hashtable otherHash)
  {
    Vector v = new Vector();
    
    for (Enumeration i = thisHash.keys(); i.hasMoreElements();) {
      CaselessStringKey thisNameKey = (CaselessStringKey)i.nextElement();
      if (!otherHash.containsKey(thisNameKey))
      {

        v.add(thisNameKey.toString());
      }
    }
    return v;
  }
  
  private int getIndex(Hashtable h, String s) {
    return ((Integer)h.get(new CaselessStringKey(s))).intValue();
  }
  

  private Vector commonElements(Hashtable thisHash, Hashtable otherHash)
  {
    Vector v = new Vector();
    
    for (Enumeration i = thisHash.keys(); i.hasMoreElements();) {
      CaselessStringKey thisNameKey = (CaselessStringKey)i.nextElement();
      if (otherHash.containsKey(thisNameKey)) {
        v.add(thisNameKey.toString());
      }
    }
    return v;
  }
  
  private Hashtable hashNames(String[] paramNames)
  {
    Hashtable h = new Hashtable();
    if (paramNames != null) {
      for (int i = 0; i < paramNames.length; i++) {
        h.put(new CaselessStringKey(paramNames[i]), new Integer(i));
      }
    }
    
    return h;
  }
  
  private Hashtable hashNames(Vector paramNames)
  {
    Hashtable h = new Hashtable();
    if (paramNames != null) {
      for (int i = 0; i < paramNames.size(); i++) {
        h.put(new CaselessStringKey((String)paramNames.elementAt(i)), new Integer(i));
      }
    }
    

    return h;
  }
  












  public ParameterList setParameter(String paramName, byte b)
  {
    throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapability2"));
  }
  











  public ParameterList setParameter(String paramName, boolean b)
  {
    throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapability2"));
  }
  











  public ParameterList setParameter(String paramName, char c)
  {
    throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapability2"));
  }
  











  public ParameterList setParameter(String paramName, short s)
  {
    throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapability2"));
  }
  











  public ParameterList setParameter(String paramName, int i)
  {
    throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapability2"));
  }
  











  public ParameterList setParameter(String paramName, long l)
  {
    throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapability2"));
  }
  











  public ParameterList setParameter(String paramName, float f)
  {
    throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapability2"));
  }
  











  public ParameterList setParameter(String paramName, double d)
  {
    throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapability2"));
  }
  











  public ParameterList setParameter(String paramName, Object obj)
  {
    if ((obj != null) && (!(obj instanceof Negotiable))) {
      throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapability2"));
    }
    

    super.setParameter(paramName, obj);
    return this;
  }
  













  public byte getByteParameter(String paramName)
  {
    throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapability3"));
  }
  










  public boolean getBooleanParameter(String paramName)
  {
    throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapability3"));
  }
  










  public char getCharParameter(String paramName)
  {
    throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapability3"));
  }
  










  public short getShortParameter(String paramName)
  {
    throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapability3"));
  }
  










  public int getIntParameter(String paramName)
  {
    throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapability3"));
  }
  










  public long getLongParameter(String paramName)
  {
    throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapability3"));
  }
  










  public float getFloatParameter(String paramName)
  {
    throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapability3"));
  }
  










  public double getDoubleParameter(String paramName)
  {
    throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapability3"));
  }
}
