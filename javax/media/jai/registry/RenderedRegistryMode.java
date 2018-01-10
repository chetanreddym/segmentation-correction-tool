package javax.media.jai.registry;

import java.awt.RenderingHints;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import java.lang.reflect.Method;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptor;
import javax.media.jai.RegistryMode;
import javax.media.jai.util.ImagingListener;















public class RenderedRegistryMode
  extends RegistryMode
{
  public static final String MODE_NAME = "rendered";
  private static Method factoryMethod = null;
  
  private static Method getThisFactoryMethod()
  {
    if (factoryMethod != null) {
      return factoryMethod;
    }
    
    Class factoryClass = RenderedImageFactory.class;
    
    try
    {
      Class[] paramTypes = { ParameterBlock.class, RenderingHints.class };
      


      factoryMethod = factoryClass.getMethod("create", paramTypes);
    }
    catch (NoSuchMethodException e) {
      ImagingListener listener = JAI.getDefaultInstance().getImagingListener();
      
      String message = JaiI18N.getString("RegistryMode0") + " " + factoryClass.getName() + ".";
      
      listener.errorOccurred(message, e, RenderedRegistryMode.class, false);
    }
    


    return factoryMethod;
  }
  




  public RenderedRegistryMode()
  {
    super("rendered", OperationDescriptor.class, getThisFactoryMethod().getReturnType(), getThisFactoryMethod(), true, true);
  }
}
