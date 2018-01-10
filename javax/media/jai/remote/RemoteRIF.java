package javax.media.jai.remote;

import java.awt.RenderingHints;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.OperationNode;
import javax.media.jai.PropertyChangeEventJAI;

public abstract interface RemoteRIF
{
  public abstract RemoteRenderedImage create(String paramString1, String paramString2, ParameterBlock paramParameterBlock, RenderingHints paramRenderingHints)
    throws RemoteImagingException;
  
  public abstract RemoteRenderedImage create(PlanarImageServerProxy paramPlanarImageServerProxy, OperationNode paramOperationNode, PropertyChangeEventJAI paramPropertyChangeEventJAI)
    throws RemoteImagingException;
  
  public abstract NegotiableCapabilitySet getClientCapabilities();
}
