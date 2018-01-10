package javax.media.jai.remote;

import java.awt.RenderingHints;
import java.awt.image.renderable.ParameterBlock;
import java.net.URL;
import javax.media.jai.OperationDescriptor;
import javax.media.jai.OperationNode;
import javax.media.jai.RegistryElementDescriptor;

public abstract interface RemoteDescriptor
  extends RegistryElementDescriptor
{
  public abstract OperationDescriptor[] getServerSupportedOperationList(String paramString)
    throws RemoteImagingException;
  
  public abstract NegotiableCapabilitySet getServerCapabilities(String paramString)
    throws RemoteImagingException;
  
  public abstract URL getServerNameDocs();
  
  public abstract Object getInvalidRegion(String paramString1, String paramString2, ParameterBlock paramParameterBlock1, RenderingHints paramRenderingHints1, String paramString3, ParameterBlock paramParameterBlock2, RenderingHints paramRenderingHints2, OperationNode paramOperationNode)
    throws RemoteImagingException;
}
