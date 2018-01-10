package javax.media.jai;

import java.io.Serializable;

public abstract interface PropertyGenerator
  extends Serializable
{
  public abstract String[] getPropertyNames();
  
  public abstract Class getClass(String paramString);
  
  public abstract boolean canGenerateProperties(Object paramObject);
  
  public abstract Object getProperty(String paramString, Object paramObject);
  
  /**
   * @deprecated
   */
  public abstract Object getProperty(String paramString, RenderedOp paramRenderedOp);
  
  /**
   * @deprecated
   */
  public abstract Object getProperty(String paramString, RenderableOp paramRenderableOp);
}
