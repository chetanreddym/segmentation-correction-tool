package javax.media.jai.registry;

import java.awt.RenderingHints;
import java.awt.image.renderable.ParameterBlock;
import java.lang.reflect.Method;
import javax.media.jai.JAI;
import javax.media.jai.RegistryMode;
import javax.media.jai.remote.RemoteDescriptor;
import javax.media.jai.remote.RemoteRIF;
import javax.media.jai.util.ImagingListener;















public class RemoteRenderedRegistryMode
  extends RegistryMode
{
  public static final String MODE_NAME = "remoteRendered";
  private static Method factoryMethod = null;
  
  private static Method getThisFactoryMethod()
  {
    if (factoryMethod != null) {
      return factoryMethod;
    }
    
    Class factoryClass = RemoteRIF.class;
    
    try
    {
      Class[] paramTypes = { String.class, String.class, ParameterBlock.class, RenderingHints.class };
      




      factoryMethod = factoryClass.getMethod("create", paramTypes);
    } catch (NoSuchMethodException e) {
      ImagingListener listener = JAI.getDefaultInstance().getImagingListener();
      
      String message = JaiI18N.getString("RegistryMode0") + " " + factoryClass.getName() + ".";
      
      listener.errorOccurred(message, e, RemoteRenderedRegistryMode.class, false);
    }
    


    return factoryMethod;
  }
  



  public RemoteRenderedRegistryMode()
  {
    super("remoteRendered", RemoteDescriptor.class, getThisFactoryMethod().getReturnType(), getThisFactoryMethod(), false, false);
  }
}
