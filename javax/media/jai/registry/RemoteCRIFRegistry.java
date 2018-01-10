package javax.media.jai.registry;

import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderContext;
import javax.media.jai.JAI;
import javax.media.jai.OperationRegistry;
import javax.media.jai.remote.RemoteCRIF;
import javax.media.jai.remote.RemoteRenderedImage;








































public final class RemoteCRIFRegistry
{
  private static final String MODE_NAME = "remoteRenderable";
  
  public RemoteCRIFRegistry() {}
  
  public static void register(OperationRegistry registry, String protocolName, RemoteCRIF rcrif)
  {
    registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
    

    registry.registerFactory("remoteRenderable", protocolName, null, rcrif);
  }
  
























  public static void unregister(OperationRegistry registry, String protocolName, RemoteCRIF rcrif)
  {
    registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
    

    registry.unregisterFactory("remoteRenderable", protocolName, null, rcrif);
  }
  
















  public static RemoteCRIF get(OperationRegistry registry, String protocolName)
  {
    registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
    

    return (RemoteCRIF)registry.getFactory("remoteRenderable", protocolName);
  }
  










































  public static RemoteRenderedImage create(OperationRegistry registry, String protocolName, String serverName, String operationName, RenderContext renderContext, ParameterBlock paramBlock)
  {
    registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
    

    Object[] args = { serverName, operationName, renderContext, paramBlock };
    



    return (RemoteRenderedImage)registry.invokeFactory("remoteRenderable", protocolName, args);
  }
}
