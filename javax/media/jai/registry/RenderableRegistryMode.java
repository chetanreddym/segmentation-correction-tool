package javax.media.jai.registry;

import java.awt.image.renderable.ContextualRenderedImageFactory;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import java.lang.reflect.Method;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptor;
import javax.media.jai.RegistryMode;
import javax.media.jai.util.ImagingListener;















public class RenderableRegistryMode
  extends RegistryMode
{
  public static final String MODE_NAME = "renderable";
  private static Method factoryMethod = null;
  
  private static Method getThisFactoryMethod()
  {
    if (factoryMethod != null) {
      return factoryMethod;
    }
    
    Class factoryClass = ContextualRenderedImageFactory.class;
    
    try
    {
      Class[] paramTypes = { RenderContext.class, ParameterBlock.class };
      


      factoryMethod = factoryClass.getMethod("create", paramTypes);
    }
    catch (NoSuchMethodException e) {
      ImagingListener listener = JAI.getDefaultInstance().getImagingListener();
      
      String message = JaiI18N.getString("RegistryMode0") + " " + factoryClass.getName() + ".";
      
      listener.errorOccurred(message, e, RenderableRegistryMode.class, false);
    }
    


    return factoryMethod;
  }
  






  public RenderableRegistryMode()
  {
    super("renderable", OperationDescriptor.class, RenderableImage.class, getThisFactoryMethod(), false, true);
  }
}
