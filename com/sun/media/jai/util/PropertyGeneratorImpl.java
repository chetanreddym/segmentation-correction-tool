package com.sun.media.jai.util;

import javax.media.jai.PropertyGenerator;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;







































public abstract class PropertyGeneratorImpl
  implements PropertyGenerator
{
  private String[] propertyNames;
  private Class[] propertyClasses;
  private Class[] supportedOpClasses;
  
  protected PropertyGeneratorImpl(String[] propertyNames, Class[] propertyClasses, Class[] supportedOpClasses)
  {
    if ((propertyNames == null) || (propertyClasses == null) || (supportedOpClasses == null))
    {

      throw new IllegalArgumentException(JaiI18N.getString("PropertyGeneratorImpl0")); }
    if ((propertyNames.length == 0) || (propertyClasses.length == 0) || (supportedOpClasses.length == 0))
    {

      throw new IllegalArgumentException(JaiI18N.getString("PropertyGeneratorImpl1")); }
    if (propertyNames.length != propertyClasses.length) {
      throw new IllegalArgumentException(JaiI18N.getString("PropertyGeneratorImpl2"));
    }
    
    for (int i = 0; i < propertyClasses.length; i++) {
      if (propertyClasses[i].isPrimitive()) {
        throw new IllegalArgumentException(JaiI18N.getString("PropertyGeneratorImpl4"));
      }
    }
    
    this.propertyNames = propertyNames;
    this.propertyClasses = propertyClasses;
    this.supportedOpClasses = supportedOpClasses;
  }
  






  public String[] getPropertyNames()
  {
    return propertyNames;
  }
  













  public Class getClass(String propertyName)
  {
    if (propertyName == null) {
      throw new IllegalArgumentException(JaiI18N.getString("PropertyGeneratorImpl0"));
    }
    

    int numProperties = propertyNames.length;
    for (int i = 0; i < numProperties; i++) {
      if (propertyName.equalsIgnoreCase(propertyNames[i])) {
        return propertyClasses[i];
      }
    }
    


    return null;
  }
  






  public boolean canGenerateProperties(Object opNode)
  {
    if (opNode == null) {
      throw new IllegalArgumentException(JaiI18N.getString("PropertyGeneratorImpl0"));
    }
    
    int numClasses = supportedOpClasses.length;
    if (numClasses == 1) {
      return supportedOpClasses[0].isInstance(opNode);
    }
    
    for (int i = 0; i < numClasses; i++) {
      if (supportedOpClasses[i].isInstance(opNode)) {
        return true;
      }
    }
    

    return false;
  }
  






















  public abstract Object getProperty(String paramString, Object paramObject);
  





















  /**
   * @deprecated
   */
  public Object getProperty(String name, RenderedOp op)
  {
    return getProperty(name, op);
  }
  


















  /**
   * @deprecated
   */
  public Object getProperty(String name, RenderableOp op)
  {
    return getProperty(name, op);
  }
  













  protected void validate(String name, Object opNode)
  {
    if (name == null)
      throw new IllegalArgumentException(JaiI18N.getString("PropertyGeneratorImpl0"));
    if (!canGenerateProperties(opNode)) {
      throw new IllegalArgumentException(JaiI18N.getString("PropertyGeneratorImpl3"));
    }
  }
}
