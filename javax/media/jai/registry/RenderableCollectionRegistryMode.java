package javax.media.jai.registry;

import java.awt.image.renderable.ParameterBlock;
import java.lang.reflect.Method;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptor;
import javax.media.jai.RegistryMode;
import javax.media.jai.RenderableCollectionImageFactory;
import javax.media.jai.util.ImagingListener;
















public class RenderableCollectionRegistryMode
  extends RegistryMode
{
  public static final String MODE_NAME = "renderableCollection";
  private static Method factoryMethod = null;
  
  private static Method getThisFactoryMethod()
  {
    if (factoryMethod != null) {
      return factoryMethod;
    }
    
    Class factoryClass = RenderableCollectionImageFactory.class;
    
    try
    {
      Class[] paramTypes = { ParameterBlock.class };
      

      factoryMethod = factoryClass.getMethod("create", paramTypes);
    }
    catch (NoSuchMethodException e) {
      ImagingListener listener = JAI.getDefaultInstance().getImagingListener();
      
      String message = JaiI18N.getString("RegistryMode0") + " " + factoryClass.getName() + ".";
      
      listener.errorOccurred(message, e, RenderableCollectionRegistryMode.class, false);
    }
    


    return factoryMethod;
  }
  






  public RenderableCollectionRegistryMode()
  {
    super("renderableCollection", OperationDescriptor.class, getThisFactoryMethod().getReturnType(), getThisFactoryMethod(), false, true);
  }
}
