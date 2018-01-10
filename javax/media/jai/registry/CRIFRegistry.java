package javax.media.jai.registry;

import java.awt.image.RenderedImage;
import java.awt.image.renderable.ContextualRenderedImageFactory;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderContext;
import javax.media.jai.JAI;
import javax.media.jai.OperationRegistry;
import javax.media.jai.PropertySource;
import javax.media.jai.RenderableOp;











































public final class CRIFRegistry
{
  private static final String MODE_NAME = "renderable";
  
  public CRIFRegistry() {}
  
  public static void register(OperationRegistry registry, String operationName, ContextualRenderedImageFactory crif)
  {
    registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
    

    registry.registerFactory("renderable", operationName, null, crif);
  }
  





















  public static void unregister(OperationRegistry registry, String operationName, ContextualRenderedImageFactory crif)
  {
    registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
    

    registry.unregisterFactory("renderable", operationName, null, crif);
  }
  


















  public static ContextualRenderedImageFactory get(OperationRegistry registry, String operationName)
  {
    registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
    

    return (ContextualRenderedImageFactory)registry.getFactory("renderable", operationName);
  }
  
























  public static RenderedImage create(OperationRegistry registry, String operationName, RenderContext context, ParameterBlock paramBlock)
  {
    registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
    

    Object[] args = { context, paramBlock };
    
    return (RenderedImage)registry.invokeFactory("renderable", operationName, args);
  }
  















  public static PropertySource getPropertySource(RenderableOp op)
  {
    if (op == null) {
      throw new IllegalArgumentException("op - " + JaiI18N.getString("Generic0"));
    }
    
    return op.getRegistry().getPropertySource(op);
  }
}
