package javax.media.jai;

import java.awt.RenderingHints;
import java.awt.image.renderable.ParameterBlock;

public abstract interface OperationNode
  extends PropertySource, PropertyChangeEmitter
{
  public abstract String getRegistryModeName();
  
  public abstract String getOperationName();
  
  public abstract void setOperationName(String paramString);
  
  public abstract OperationRegistry getRegistry();
  
  public abstract void setRegistry(OperationRegistry paramOperationRegistry);
  
  public abstract ParameterBlock getParameterBlock();
  
  public abstract void setParameterBlock(ParameterBlock paramParameterBlock);
  
  public abstract RenderingHints getRenderingHints();
  
  public abstract void setRenderingHints(RenderingHints paramRenderingHints);
  
  public abstract Object getDynamicProperty(String paramString);
  
  public abstract void addPropertyGenerator(PropertyGenerator paramPropertyGenerator);
  
  public abstract void copyPropertyFromSource(String paramString, int paramInt);
  
  public abstract void suppressProperty(String paramString);
}
