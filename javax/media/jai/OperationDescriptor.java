package javax.media.jai;

import java.awt.RenderingHints;
import java.awt.image.renderable.ParameterBlock;
import java.util.Locale;
import java.util.ResourceBundle;



































































public abstract interface OperationDescriptor
  extends RegistryElementDescriptor
{
  public static final Object NO_PARAMETER_DEFAULT = ParameterListDescriptor.NO_PARAMETER_DEFAULT;
  
  public abstract String[][] getResources(Locale paramLocale);
  
  public abstract ResourceBundle getResourceBundle(Locale paramLocale);
  
  public abstract int getNumSources();
  
  public abstract Class[] getSourceClasses(String paramString);
  
  public abstract String[] getSourceNames();
  
  public abstract Class getDestClass(String paramString);
  
  public abstract boolean validateArguments(String paramString, ParameterBlock paramParameterBlock, StringBuffer paramStringBuffer);
  
  public abstract boolean isImmediate();
  
  public abstract Object getInvalidRegion(String paramString, ParameterBlock paramParameterBlock1, RenderingHints paramRenderingHints1, ParameterBlock paramParameterBlock2, RenderingHints paramRenderingHints2, OperationNode paramOperationNode);
  
  /**
   * @deprecated
   */
  public abstract PropertyGenerator[] getPropertyGenerators();
  
  /**
   * @deprecated
   */
  public abstract boolean isRenderedSupported();
  
  /**
   * @deprecated
   */
  public abstract Class[] getSourceClasses();
  
  /**
   * @deprecated
   */
  public abstract Class getDestClass();
  
  /**
   * @deprecated
   */
  public abstract boolean validateArguments(ParameterBlock paramParameterBlock, StringBuffer paramStringBuffer);
  
  /**
   * @deprecated
   */
  public abstract boolean isRenderableSupported();
  
  /**
   * @deprecated
   */
  public abstract Class[] getRenderableSourceClasses();
  
  /**
   * @deprecated
   */
  public abstract Class getRenderableDestClass();
  
  /**
   * @deprecated
   */
  public abstract boolean validateRenderableArguments(ParameterBlock paramParameterBlock, StringBuffer paramStringBuffer);
  
  /**
   * @deprecated
   */
  public abstract int getNumParameters();
  
  /**
   * @deprecated
   */
  public abstract Class[] getParamClasses();
  
  /**
   * @deprecated
   */
  public abstract String[] getParamNames();
  
  /**
   * @deprecated
   */
  public abstract Object[] getParamDefaults();
  
  /**
   * @deprecated
   */
  public abstract Object getParamDefaultValue(int paramInt);
  
  /**
   * @deprecated
   */
  public abstract Number getParamMinValue(int paramInt);
  
  /**
   * @deprecated
   */
  public abstract Number getParamMaxValue(int paramInt);
}
