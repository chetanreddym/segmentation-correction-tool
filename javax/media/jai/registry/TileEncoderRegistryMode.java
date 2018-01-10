package javax.media.jai.registry;

import java.awt.image.SampleModel;
import java.io.OutputStream;
import java.lang.reflect.Method;
import javax.media.jai.JAI;
import javax.media.jai.RegistryMode;
import javax.media.jai.tilecodec.TileCodecDescriptor;
import javax.media.jai.tilecodec.TileCodecParameterList;
import javax.media.jai.tilecodec.TileEncoderFactory;
import javax.media.jai.util.ImagingListener;

















public class TileEncoderRegistryMode
  extends RegistryMode
{
  public static final String MODE_NAME = "tileEncoder";
  private static Method factoryMethod = null;
  
  private static Method getThisFactoryMethod()
  {
    if (factoryMethod != null) {
      return factoryMethod;
    }
    
    Class factoryClass = TileEncoderFactory.class;
    try
    {
      Class[] paramTypes = { OutputStream.class, TileCodecParameterList.class, SampleModel.class };
      


      factoryMethod = factoryClass.getMethod("createEncoder", paramTypes);
    }
    catch (NoSuchMethodException e) {
      ImagingListener listener = JAI.getDefaultInstance().getImagingListener();
      
      String message = JaiI18N.getString("RegistryMode0") + " " + factoryClass.getName() + ".";
      
      listener.errorOccurred(message, e, TileEncoderRegistryMode.class, false);
    }
    


    return factoryMethod;
  }
  



  public TileEncoderRegistryMode()
  {
    super("tileEncoder", TileCodecDescriptor.class, getThisFactoryMethod().getReturnType(), getThisFactoryMethod(), true, false);
  }
}
