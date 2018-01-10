package javax.media.jai.registry;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import java.util.Iterator;
import java.util.List;
import javax.media.jai.JAI;
import javax.media.jai.OperationRegistry;
import javax.media.jai.PropertySource;
import javax.media.jai.RenderedOp;












































public final class RIFRegistry
{
  private static final String MODE_NAME = "rendered";
  
  public RIFRegistry() {}
  
  public static void register(OperationRegistry registry, String operationName, String productName, RenderedImageFactory rif)
  {
    registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
    

    registry.registerFactory("rendered", operationName, productName, rif);
  }
  























  public static void unregister(OperationRegistry registry, String operationName, String productName, RenderedImageFactory rif)
  {
    registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
    

    registry.unregisterFactory("rendered", operationName, productName, rif);
  }
  


























  public static void setPreference(OperationRegistry registry, String operationName, String productName, RenderedImageFactory preferredRIF, RenderedImageFactory otherRIF)
  {
    registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
    

    registry.setFactoryPreference("rendered", operationName, productName, preferredRIF, otherRIF);
  }
  



























  public static void unsetPreference(OperationRegistry registry, String operationName, String productName, RenderedImageFactory preferredRIF, RenderedImageFactory otherRIF)
  {
    registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
    

    registry.unsetFactoryPreference("rendered", operationName, productName, preferredRIF, otherRIF);
  }
  




















  public static void clearPreferences(OperationRegistry registry, String operationName, String productName)
  {
    registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
    

    registry.clearFactoryPreferences("rendered", operationName, productName);
  }
  

























  public static List getOrderedList(OperationRegistry registry, String operationName, String productName)
  {
    registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
    

    return registry.getOrderedFactoryList("rendered", operationName, productName);
  }
  


























  public static Iterator getIterator(OperationRegistry registry, String operationName)
  {
    registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
    

    return registry.getFactoryIterator("rendered", operationName);
  }
  





















  public static RenderedImageFactory get(OperationRegistry registry, String operationName)
  {
    registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
    

    return (RenderedImageFactory)registry.getFactory("rendered", operationName);
  }
  






























  public static RenderedImage create(OperationRegistry registry, String operationName, ParameterBlock paramBlock, RenderingHints renderHints)
  {
    registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
    

    Object[] args = { paramBlock, renderHints };
    
    return (RenderedImage)registry.invokeFactory("rendered", operationName, args);
  }
  















  public static PropertySource getPropertySource(RenderedOp op)
  {
    if (op == null) {
      throw new IllegalArgumentException("op - " + JaiI18N.getString("Generic0"));
    }
    
    return op.getRegistry().getPropertySource(op);
  }
}
