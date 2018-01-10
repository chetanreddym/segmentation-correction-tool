package javax.media.jai;

import com.sun.media.jai.util.CaselessStringArrayTable;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderableImage;
import java.io.Serializable;
import java.util.Collection;
import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.media.jai.util.Range;





















public abstract class OperationDescriptorImpl
  implements OperationDescriptor, Serializable
{
  private boolean deprecated = false;
  





  protected final String[][] resources;
  





  protected final String[] supportedModes;
  





  private CaselessStringArrayTable modeIndices;
  





  protected final String[] sourceNames;
  





  private Class[][] sourceClasses;
  





  private CaselessStringArrayTable sourceIndices;
  





  private ParameterListDescriptor[] paramListDescriptors;
  





  String[] paramNames;
  





  private String name = null;
  








  private String[] checkSources(String[][] resources, String[] supportedModes, String[] sourceNames, Class[][] sourceClasses)
  {
    if ((resources == null) || (resources.length == 0)) {
      throw new IllegalArgumentException("resources: " + JaiI18N.getString("Generic2"));
    }
    
    if ((supportedModes == null) || (supportedModes.length == 0)) {
      throw new IllegalArgumentException("supportedModes: " + JaiI18N.getString("Generic2"));
    }
    


    int numModes = supportedModes.length;
    
    if (sourceClasses != null)
    {
      if (sourceClasses.length != numModes) {
        throw new IllegalArgumentException(JaiI18N.formatMsg("OperationDescriptorImpl0", new Object[] { "sourceClasses", new Integer(numModes) }));
      }
      

      int numSources = sourceClasses[0] == null ? 0 : sourceClasses[0].length;
      

      if (sourceNames == null) {
        sourceNames = getDefaultSourceNames(numSources);
      }
      else if (sourceNames.length != numSources)
      {
        throw new IllegalArgumentException(JaiI18N.formatMsg("OperationDescriptorImpl1", new Object[] { new Integer(sourceNames.length), new Integer(numSources) }));
      }
      





      for (int i = 0; i < sourceClasses.length; i++) {
        int ns = sourceClasses[i] == null ? 0 : sourceClasses[i].length;
        

        if (numSources != ns) {
          throw new IllegalArgumentException(JaiI18N.formatMsg("OperationDescriptorImpl2", new Object[] { new Integer(ns), new Integer(numSources), supportedModes[i] }));

        }
        

      }
      


    }
    else if ((sourceNames != null) && (sourceNames.length != 0)) {
      throw new IllegalArgumentException(JaiI18N.formatMsg("OperationDescriptorImpl1", new Object[] { new Integer(sourceNames.length), new Integer(0) }));
    }
    





    return sourceNames;
  }
  


































































  public OperationDescriptorImpl(String[][] resources, String[] supportedModes, String[] sourceNames, Class[][] sourceClasses, String[] paramNames, Class[][] paramClasses, Object[][] paramDefaults, Object[][] validParamValues)
  {
    sourceNames = checkSources(resources, supportedModes, sourceNames, sourceClasses);
    

    this.resources = resources;
    this.supportedModes = supportedModes;
    this.sourceNames = sourceNames;
    this.sourceClasses = sourceClasses;
    this.paramNames = paramNames;
    
    modeIndices = new CaselessStringArrayTable(supportedModes);
    sourceIndices = new CaselessStringArrayTable(sourceNames);
    


    int numParams = paramNames == null ? 0 : paramNames.length;
    int numModes = supportedModes.length;
    
    if (numParams == 0) {
      if ((paramClasses != null) && (paramClasses.length != numModes)) {
        throw new IllegalArgumentException(JaiI18N.formatMsg("OperationDescriptorImpl0", new Object[] { "paramClasses", new Integer(numModes) }));

      }
      

    }
    else if ((paramClasses == null) || (paramClasses.length != numModes)) {
      throw new IllegalArgumentException(JaiI18N.formatMsg("OperationDescriptorImpl0", new Object[] { "paramClasses", new Integer(numModes) }));
    }
    


    if ((paramDefaults != null) && (paramDefaults.length != numModes)) {
      throw new IllegalArgumentException(JaiI18N.formatMsg("OperationDescriptorImpl0", new Object[] { "paramDefaults", new Integer(numModes) }));
    }
    

    if ((validParamValues != null) && (validParamValues.length != numModes)) {
      throw new IllegalArgumentException(JaiI18N.formatMsg("OperationDescriptorImpl0", new Object[] { "validParamValues", new Integer(numModes) }));
    }
    



    paramListDescriptors = new ParameterListDescriptor[numModes];
    

    for (int i = 0; i < numModes; i++) {
      paramListDescriptors[i] = new ParameterListDescriptorImpl(this, paramNames, paramClasses[i], paramDefaults == null ? null : paramDefaults[i], validParamValues == null ? null : validParamValues[i]);
    }
  }
  






























































  public OperationDescriptorImpl(String[][] resources, String[] supportedModes, String[] sourceNames, Class[][] sourceClasses, String[] paramNames, Class[] paramClasses, Object[] paramDefaults, Object[] validParamValues)
  {
    sourceNames = checkSources(resources, supportedModes, sourceNames, sourceClasses);
    

    this.resources = resources;
    this.supportedModes = supportedModes;
    this.sourceNames = sourceNames;
    this.sourceClasses = sourceClasses;
    this.paramNames = paramNames;
    
    modeIndices = new CaselessStringArrayTable(supportedModes);
    sourceIndices = new CaselessStringArrayTable(sourceNames);
    


    ParameterListDescriptor pld = new ParameterListDescriptorImpl(this, paramNames, paramClasses, paramDefaults, validParamValues);
    

    paramListDescriptors = new ParameterListDescriptor[supportedModes.length];
    

    for (int i = 0; i < supportedModes.length; i++) {
      paramListDescriptors[i] = pld;
    }
  }
  















































  public OperationDescriptorImpl(String[][] resources, String[] supportedModes, int numSources, String[] paramNames, Class[] paramClasses, Object[] paramDefaults, Object[] validParamValues)
  {
    Class[][] sourceClasses = makeDefaultSourceClassList(supportedModes, numSources);
    

    String[] sourceNames = checkSources(resources, supportedModes, null, sourceClasses);
    

    this.resources = resources;
    this.supportedModes = supportedModes;
    this.sourceNames = sourceNames;
    this.sourceClasses = sourceClasses;
    this.paramNames = paramNames;
    
    modeIndices = new CaselessStringArrayTable(supportedModes);
    sourceIndices = new CaselessStringArrayTable(sourceNames);
    


    ParameterListDescriptor pld = new ParameterListDescriptorImpl(this, paramNames, paramClasses, paramDefaults, validParamValues);
    

    paramListDescriptors = new ParameterListDescriptor[supportedModes.length];
    

    for (int i = 0; i < supportedModes.length; i++) {
      paramListDescriptors[i] = pld;
    }
  }
  





































  public OperationDescriptorImpl(String[][] resources, String[] supportedModes, String[] sourceNames, Class[][] sourceClasses, ParameterListDescriptor[] pld)
  {
    sourceNames = checkSources(resources, supportedModes, sourceNames, sourceClasses);
    

    this.resources = resources;
    this.supportedModes = supportedModes;
    this.sourceNames = sourceNames;
    this.sourceClasses = sourceClasses;
    
    modeIndices = new CaselessStringArrayTable(supportedModes);
    sourceIndices = new CaselessStringArrayTable(sourceNames);
    
    if ((pld != null) && (pld.length != supportedModes.length)) {
      throw new IllegalArgumentException(JaiI18N.formatMsg("OperationDescriptorImpl0", new Object[] { "ParameterListDescriptor's", new Integer(supportedModes.length) }));
    }
    





    if (pld == null)
    {
      ParameterListDescriptor tpld = new ParameterListDescriptorImpl();
      
      paramListDescriptors = new ParameterListDescriptor[supportedModes.length];
      

      for (int i = 0; i < supportedModes.length; i++) {
        paramListDescriptors[i] = tpld;
      }
      paramNames = null;
    } else {
      paramListDescriptors = pld;
      paramNames = paramListDescriptors[0].getParamNames();
    }
  }
  





































  public OperationDescriptorImpl(String[][] resources, String[] supportedModes, String[] sourceNames, Class[][] sourceClasses, ParameterListDescriptor pld)
  {
    sourceNames = checkSources(resources, supportedModes, sourceNames, sourceClasses);
    

    this.resources = resources;
    this.supportedModes = supportedModes;
    this.sourceNames = sourceNames;
    this.sourceClasses = sourceClasses;
    
    modeIndices = new CaselessStringArrayTable(supportedModes);
    sourceIndices = new CaselessStringArrayTable(sourceNames);
    
    if (pld == null) {
      pld = new ParameterListDescriptorImpl();
    }
    paramNames = pld.getParamNames();
    
    paramListDescriptors = new ParameterListDescriptor[supportedModes.length];
    

    for (int i = 0; i < supportedModes.length; i++) {
      paramListDescriptors[i] = pld;
    }
  }
  


  private String[] getDefaultSourceNames(int numSources)
  {
    String[] defaultSourceNames = new String[numSources];
    for (int i = 0; i < numSources; i++) {
      defaultSourceNames[i] = ("source" + i);
    }
    return defaultSourceNames;
  }
  













  public String getName()
  {
    if (name == null) {
      name = ((String)getResourceBundle(Locale.getDefault()).getObject("GlobalName"));
    }
    
    return name;
  }
  










  public String[] getSupportedModes()
  {
    return supportedModes;
  }
  
















  public boolean isModeSupported(String modeName)
  {
    if (modeName == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    return modeIndices.contains(modeName);
  }
  












  public boolean arePropertiesSupported()
  {
    return true;
  }
  





























  public PropertyGenerator[] getPropertyGenerators(String modeName)
  {
    if (modeName == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    if ((deprecated) && (
      (modeName.equalsIgnoreCase("rendered")) || (modeName.equalsIgnoreCase("renderable"))))
    {
      return getPropertyGenerators();
    }
    
    if (!arePropertiesSupported()) {
      throw new UnsupportedOperationException(JaiI18N.formatMsg("OperationDescriptorImpl3", new Object[] { modeName }));
    }
    


    return null;
  }
  

















  public ParameterListDescriptor getParameterListDescriptor(String modeName)
  {
    return paramListDescriptors[modeIndices.indexOf(modeName)];
  }
  






















  public String[][] getResources(Locale locale)
  {
    return resources;
  }
  














  public ResourceBundle getResourceBundle(Locale locale)
  {
    Locale l = locale;
    new ListResourceBundle() { private final Locale val$l;
      
      public Object[][] getContents() { return getResources(val$l); }
    };
  }
  




  public int getNumSources()
  {
    return sourceNames.length;
  }
  











  public Class[] getSourceClasses(String modeName)
  {
    checkModeName(modeName);
    
    Class[] sc = sourceClasses[modeIndices.indexOf(modeName)];
    
    if ((sc != null) && (sc.length <= 0)) {
      return null;
    }
    return sc;
  }
  










  public String[] getSourceNames()
  {
    if ((sourceNames == null) || (sourceNames.length <= 0)) {
      return null;
    }
    return sourceNames;
  }
  

















  public Class getDestClass(String modeName)
  {
    checkModeName(modeName);
    
    if (deprecated)
    {
      if (modeName.equalsIgnoreCase("rendered")) {
        return getDestClass();
      }
      if (modeName.equalsIgnoreCase("renderable")) {
        return getRenderableDestClass();
      }
    }
    return RegistryMode.getMode(modeName).getProductClass();
  }
  





























  protected boolean validateSources(String modeName, ParameterBlock args, StringBuffer msg)
  {
    if (modeName == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    if (deprecated)
    {
      if (modeName.equalsIgnoreCase("rendered")) {
        return validateSources(args, msg);
      }
      if (modeName.equalsIgnoreCase("renderable")) {
        return validateRenderableSources(args, msg);
      }
    }
    return validateSources(getSourceClasses(modeName), args, msg);
  }
  






































  protected boolean validateParameters(String modeName, ParameterBlock args, StringBuffer msg)
  {
    if (modeName == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    if ((deprecated) && (
      (modeName.equalsIgnoreCase("rendered")) || (modeName.equalsIgnoreCase("renderable"))))
    {
      return validateParameters(args, msg);
    }
    
    return validateParameters(getParameterListDescriptor(modeName), args, msg);
  }
  





































  public boolean validateArguments(String modeName, ParameterBlock args, StringBuffer msg)
  {
    return (isModeSupported(modeName)) && (validateSources(modeName, args, msg)) && (validateParameters(modeName, args, msg));
  }
  

























  public boolean isImmediate()
  {
    return false;
  }
  

































  public Object getInvalidRegion(String modeName, ParameterBlock oldParamBlock, RenderingHints oldHints, ParameterBlock newParamBlock, RenderingHints newHints, OperationNode node)
  {
    if (modeName == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    return null;
  }
  










  protected static Class getDefaultSourceClass(String modeName)
  {
    if ("rendered".equalsIgnoreCase(modeName)) {
      return RenderedImage.class;
    }
    if ("renderable".equalsIgnoreCase(modeName)) {
      return RenderableImage.class;
    }
    if ("collection".equalsIgnoreCase(modeName)) {
      return Collection.class;
    }
    if ("renderableCollection".equalsIgnoreCase(modeName)) {
      return Collection.class;
    }
    return null;
  }
  









  protected static Class[][] makeDefaultSourceClassList(String[] supportedModes, int numSources)
  {
    if ((supportedModes == null) || (supportedModes.length == 0)) {
      return (Class[][])null;
    }
    int count = supportedModes.length;
    
    Class[][] classes = new Class[count][numSources];
    
    for (int i = 0; i < count; i++)
    {
      Class sourceClass = getDefaultSourceClass(supportedModes[i]);
      
      for (int j = 0; j < numSources; j++) {
        classes[i][j] = sourceClass;
      }
    }
    return classes;
  }
  





  private String[] makeSupportedModeList()
  {
    int count = 0;
    
    if (isRenderedSupported()) count++;
    if (isRenderableSupported()) { count++;
    }
    String[] modes = new String[count];
    
    count = 0;
    
    if (isRenderedSupported()) modes[(count++)] = "rendered";
    if (isRenderableSupported()) { modes[(count++)] = "renderable";
    }
    return modes;
  }
  




  private Class[][] makeSourceClassList(Class[] sourceClasses, Class[] renderableSourceClasses)
  {
    int count = 0;
    
    if (isRenderedSupported()) count++;
    if (isRenderableSupported()) { count++;
    }
    Class[][] classes = new Class[count][];
    
    count = 0;
    
    if (isRenderedSupported()) classes[(count++)] = sourceClasses;
    if (isRenderableSupported()) { classes[(count++)] = renderableSourceClasses;
    }
    return classes;
  }
  



  private Object[] makeValidParamValueList(Class[] paramClasses)
  {
    if (paramClasses == null) {
      return null;
    }
    int numParams = paramClasses.length;
    
    Object[] validValues = null;
    
    for (int i = 0; i < numParams; i++) {
      Number min = getParamMinValue(i);
      Number max = getParamMaxValue(i);
      
      if ((min != null) || (max != null))
      {

        if (validValues == null) {
          validValues = new Object[numParams];
        }
        validValues[i] = new Range(min.getClass(), (Comparable)min, (Comparable)max);
      }
    }
    
    return validValues;
  }
  

















































  /**
   * @deprecated
   */
  public OperationDescriptorImpl(String[][] resources, Class[] sourceClasses, Class[] renderableSourceClasses, Class[] paramClasses, String[] paramNames, Object[] paramDefaults)
  {
    deprecated = true;
    
    String[] supportedModes = makeSupportedModeList();
    Class[][] sourceClassList = makeSourceClassList(sourceClasses, renderableSourceClasses);
    

    String[] sourceNames = checkSources(resources, supportedModes, null, sourceClassList);
    

    Object[] validParamValues = makeValidParamValueList(paramClasses);
    
    this.resources = resources;
    this.supportedModes = supportedModes;
    this.sourceNames = sourceNames;
    this.sourceClasses = sourceClassList;
    this.paramNames = paramNames;
    
    modeIndices = new CaselessStringArrayTable(supportedModes);
    sourceIndices = new CaselessStringArrayTable(sourceNames);
    


    ParameterListDescriptor pld = new ParameterListDescriptorImpl(this, paramNames, paramClasses, paramDefaults, validParamValues);
    

    paramListDescriptors = new ParameterListDescriptor[supportedModes.length];
    

    for (int i = 0; i < supportedModes.length; i++) {
      paramListDescriptors[i] = pld;
    }
  }
  








































  /**
   * @deprecated
   */
  public OperationDescriptorImpl(String[][] resources, int numSources, Class[] paramClasses, String[] paramNames, Object[] paramDefaults)
  {
    deprecated = true;
    
    String[] supportedModes = makeSupportedModeList();
    Class[][] sourceClassList = makeDefaultSourceClassList(supportedModes, numSources);
    

    String[] sourceNames = checkSources(resources, supportedModes, null, sourceClassList);
    

    Object[] validParamValues = makeValidParamValueList(paramClasses);
    
    this.resources = resources;
    this.supportedModes = supportedModes;
    this.sourceNames = sourceNames;
    sourceClasses = sourceClassList;
    this.paramNames = paramNames;
    
    modeIndices = new CaselessStringArrayTable(supportedModes);
    sourceIndices = new CaselessStringArrayTable(sourceNames);
    


    ParameterListDescriptor pld = new ParameterListDescriptorImpl(this, paramNames, paramClasses, paramDefaults, validParamValues);
    

    paramListDescriptors = new ParameterListDescriptor[supportedModes.length];
    

    for (int i = 0; i < supportedModes.length; i++) {
      paramListDescriptors[i] = pld;
    }
  }
  















  /**
   * @deprecated
   */
  public OperationDescriptorImpl(String[][] resources, Class[] sourceClasses)
  {
    this(resources, sourceClasses, null, null, null, null);
  }
  






























  /**
   * @deprecated
   */
  public OperationDescriptorImpl(String[][] resources, Class[] sourceClasses, Class[] renderableSourceClasses)
  {
    this(resources, sourceClasses, renderableSourceClasses, null, null, null);
  }
  




















  /**
   * @deprecated
   */
  public OperationDescriptorImpl(String[][] resources, Class[] paramClasses, String[] paramNames, Object[] paramDefaults)
  {
    this(resources, null, null, paramClasses, paramNames, paramDefaults);
  }
  





















  /**
   * @deprecated
   */
  public OperationDescriptorImpl(String[][] resources, int numSources)
  {
    this(resources, numSources, null, null, null);
  }
  










  /**
   * @deprecated
   */
  public PropertyGenerator[] getPropertyGenerators()
  {
    return deprecated ? null : getPropertyGenerators("rendered");
  }
  





  /**
   * @deprecated
   */
  public boolean isRenderedSupported()
  {
    return deprecated ? true : isModeSupported("rendered");
  }
  







  /**
   * @deprecated
   */
  public Class[] getSourceClasses()
  {
    return getSourceClasses("rendered");
  }
  






  /**
   * @deprecated
   */
  public Class getDestClass()
  {
    if (deprecated) {
      return isRenderedSupported() ? RenderedImage.class : null;
    }
    
    return getDestClass("rendered");
  }
  



















  /**
   * @deprecated
   */
  public boolean validateArguments(ParameterBlock args, StringBuffer msg)
  {
    if (deprecated) {
      return (validateSources(args, msg)) && (validateParameters(args, msg));
    }
    
    return validateArguments("rendered", args, msg);
  }
  









  /**
   * @deprecated
   */
  public boolean isRenderableSupported()
  {
    return deprecated ? false : isModeSupported("renderable");
  }
  








  /**
   * @deprecated
   */
  public Class[] getRenderableSourceClasses()
  {
    return getSourceClasses("renderable");
  }
  







  /**
   * @deprecated
   */
  public Class getRenderableDestClass()
  {
    if (deprecated) {
      return isRenderableSupported() ? RenderableImage.class : null;
    }
    
    return getDestClass("renderable");
  }
  























  /**
   * @deprecated
   */
  public boolean validateRenderableArguments(ParameterBlock args, StringBuffer msg)
  {
    if (deprecated) {
      return (validateRenderableSources(args, msg)) && (validateParameters(args, msg));
    }
    
    return validateArguments("renderable", args, msg);
  }
  






  private ParameterListDescriptor getDefaultPLD()
  {
    return getParameterListDescriptor(getSupportedModes()[0]);
  }
  







  /**
   * @deprecated
   */
  public int getNumParameters()
  {
    return getDefaultPLD().getNumParameters();
  }
  








  /**
   * @deprecated
   */
  public Class[] getParamClasses()
  {
    return getDefaultPLD().getParamClasses();
  }
  








  /**
   * @deprecated
   */
  public String[] getParamNames()
  {
    return getDefaultPLD().getParamNames();
  }
  















  /**
   * @deprecated
   */
  public Object[] getParamDefaults()
  {
    return getDefaultPLD().getParamDefaults();
  }
  
















  /**
   * @deprecated
   */
  public Object getParamDefaultValue(int index)
  {
    return getDefaultPLD().getParamDefaultValue(paramNames[index]);
  }
  







































  /**
   * @deprecated
   */
  public Number getParamMinValue(int index)
  {
    return null;
  }
  






































  /**
   * @deprecated
   */
  public Number getParamMaxValue(int index)
  {
    return null;
  }
  














  /**
   * @deprecated
   */
  protected boolean validateSources(ParameterBlock args, StringBuffer msg)
  {
    if (deprecated) {
      return (isRenderedSupported()) && (validateSources(getSourceClasses(), args, msg));
    }
    
    return validateSources("rendered", args, msg);
  }
  
















  /**
   * @deprecated
   */
  protected boolean validateRenderableSources(ParameterBlock args, StringBuffer msg)
  {
    if (deprecated) {
      return (isRenderableSupported()) && (validateSources(getRenderableSourceClasses(), args, msg));
    }
    
    return validateSources("renderable", args, msg);
  }
  




























  /**
   * @deprecated
   */
  protected boolean validateParameters(ParameterBlock args, StringBuffer msg)
  {
    return validateParameters(getDefaultPLD(), args, msg);
  }
  




  private int getMinNumParameters(ParameterListDescriptor pld)
  {
    int numParams = pld.getNumParameters();
    
    Object[] paramDefaults = pld.getParamDefaults();
    
    for (int i = numParams - 1; i >= 0; i--) {
      if (paramDefaults[i] == ParameterListDescriptor.NO_PARAMETER_DEFAULT) {
        break;
      }
      numParams--;
    }
    

    return numParams;
  }
  


  private boolean validateSources(Class[] sources, ParameterBlock args, StringBuffer msg)
  {
    if ((args == null) || (msg == null)) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    int numSources = getNumSources();
    

    if (args.getNumSources() < numSources) {
      msg.append(JaiI18N.formatMsg("OperationDescriptorImpl6", new Object[] { getName(), new Integer(numSources) }));
      
      return false;
    }
    
    for (int i = 0; i < numSources; i++) {
      Object s = args.getSource(i);
      

      if (s == null) {
        msg.append(JaiI18N.formatMsg("OperationDescriptorImpl7", new Object[] { getName() }));
        
        return false;
      }
      

      Class c = sources[i];
      if (!c.isInstance(s)) {
        msg.append(JaiI18N.formatMsg("OperationDescriptorImpl8", new Object[] { getName(), new Integer(i), new String(c.toString()), new String(s.getClass().toString()) }));
        




        return false;
      }
    }
    
    return true;
  }
  


  private boolean validateParameters(ParameterListDescriptor pld, ParameterBlock args, StringBuffer msg)
  {
    if ((args == null) || (msg == null)) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    int numParams = pld.getNumParameters();
    

    int argNumParams = args.getNumParameters();
    
    Object[] paramDefaults = pld.getParamDefaults();
    

    if (argNumParams < numParams)
    {
      if (argNumParams < getMinNumParameters(pld)) {
        msg.append(JaiI18N.formatMsg("OperationDescriptorImpl9", new Object[] { getName(), new Integer(numParams) }));
        

        return false;
      }
      
      for (int i = argNumParams; i < numParams; i++) {
        args.add(paramDefaults[i]);
      }
    }
    

    for (int i = 0; i < numParams; i++) {
      Object p = args.getObjectParameter(i);
      

      if (p == null) {
        p = paramDefaults[i];
        
        if (p == OperationDescriptor.NO_PARAMETER_DEFAULT) {
          msg.append(JaiI18N.formatMsg("OperationDescriptorImpl11", new Object[] { getName(), new Integer(i) }));
          

          return false;
        }
        
        args.set(p, i);
      }
      

      try
      {
        if (!pld.isParameterValueValid(paramNames[i], p)) {
          msg.append(JaiI18N.formatMsg("OperationDescriptorImpl10", new Object[] { getName(), pld.getParamNames()[i] }));
          

          return false;
        }
      } catch (IllegalArgumentException e) {
        msg.append(getName() + " - " + e.getLocalizedMessage());
        return false;
      }
    }
    
    return true;
  }
  




  private void checkModeName(String modeName)
  {
    if (modeName == null) {
      throw new IllegalArgumentException(JaiI18N.getString("OperationDescriptorImpl12"));
    }
    
    if (!modeIndices.contains(modeName)) {
      throw new IllegalArgumentException(JaiI18N.formatMsg("OperationDescriptorImpl13", new Object[] { getName(), modeName }));
    }
  }
}
