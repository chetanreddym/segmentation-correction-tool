package javax.media.jai.registry;

import java.awt.RenderingHints;
import java.awt.image.renderable.ParameterBlock;
import java.util.Iterator;
import java.util.List;
import javax.media.jai.CollectionImage;
import javax.media.jai.CollectionImageFactory;
import javax.media.jai.CollectionOp;
import javax.media.jai.JAI;
import javax.media.jai.OperationRegistry;
import javax.media.jai.PropertySource;












































public final class CIFRegistry
{
  private static final String MODE_NAME = "collection";
  
  public CIFRegistry() {}
  
  public static void register(OperationRegistry registry, String operationName, String productName, CollectionImageFactory cif)
  {
    registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
    

    registry.registerFactory("collection", operationName, productName, cif);
  }
  























  public static void unregister(OperationRegistry registry, String operationName, String productName, CollectionImageFactory cif)
  {
    registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
    

    registry.unregisterFactory("collection", operationName, productName, cif);
  }
  


























  public static void setPreference(OperationRegistry registry, String operationName, String productName, CollectionImageFactory preferredCIF, CollectionImageFactory otherCIF)
  {
    registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
    

    registry.setFactoryPreference("collection", operationName, productName, preferredCIF, otherCIF);
  }
  



























  public static void unsetPreference(OperationRegistry registry, String operationName, String productName, CollectionImageFactory preferredCIF, CollectionImageFactory otherCIF)
  {
    registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
    

    registry.unsetFactoryPreference("collection", operationName, productName, preferredCIF, otherCIF);
  }
  




















  public static void clearPreferences(OperationRegistry registry, String operationName, String productName)
  {
    registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
    

    registry.clearFactoryPreferences("collection", operationName, productName);
  }
  

























  public static List getOrderedList(OperationRegistry registry, String operationName, String productName)
  {
    registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
    

    return registry.getOrderedFactoryList("collection", operationName, productName);
  }
  


























  public static Iterator getIterator(OperationRegistry registry, String operationName)
  {
    registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
    

    return registry.getFactoryIterator("collection", operationName);
  }
  





















  public static CollectionImageFactory get(OperationRegistry registry, String operationName)
  {
    registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
    

    return (CollectionImageFactory)registry.getFactory("collection", operationName);
  }
  






























  public static CollectionImage create(OperationRegistry registry, String operationName, ParameterBlock paramBlock, RenderingHints renderHints)
  {
    registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
    

    Object[] args = { paramBlock, renderHints };
    
    return (CollectionImage)registry.invokeFactory("collection", operationName, args);
  }
  

















  public static PropertySource getPropertySource(CollectionOp op)
  {
    if (op == null) {
      throw new IllegalArgumentException("op - " + JaiI18N.getString("Generic0"));
    }
    
    if (op.isRenderable()) {
      throw new IllegalArgumentException("op - " + JaiI18N.getString("CIFRegistry0"));
    }
    
    return op.getRegistry().getPropertySource(op);
  }
}
