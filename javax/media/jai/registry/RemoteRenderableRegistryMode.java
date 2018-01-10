package javax.media.jai.registry;

import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderContext;
import java.lang.reflect.Method;
import javax.media.jai.JAI;
import javax.media.jai.RegistryMode;
import javax.media.jai.remote.RemoteCRIF;
import javax.media.jai.remote.RemoteDescriptor;
import javax.media.jai.util.ImagingListener;















public class RemoteRenderableRegistryMode
  extends RegistryMode
{
  public static final String MODE_NAME = "remoteRenderable";
  private static Method factoryMethod = null;
  
  private static Method getThisFactoryMethod()
  {
    if (factoryMethod != null) {
      return factoryMethod;
    }
    
    Class factoryClass = RemoteCRIF.class;
    
    try
    {
      Class[] paramTypes = { String.class, String.class, RenderContext.class, ParameterBlock.class };
      




      factoryMethod = factoryClass.getMethod("create", paramTypes);
    }
    catch (NoSuchMethodException e) {
      ImagingListener listener = JAI.getDefaultInstance().getImagingListener();
      
      String message = JaiI18N.getString("RegistryMode0") + " " + factoryClass.getName() + ".";
      
      listener.errorOccurred(message, e, RemoteRenderableRegistryMode.class, false);
    }
    


    return factoryMethod;
  }
  



  public RemoteRenderableRegistryMode()
  {
    super("remoteRenderable", RemoteDescriptor.class, getThisFactoryMethod().getReturnType(), getThisFactoryMethod(), false, false);
  }
}
