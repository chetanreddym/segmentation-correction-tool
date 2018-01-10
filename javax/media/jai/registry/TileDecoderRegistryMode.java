package javax.media.jai.registry;

import java.io.InputStream;
import java.lang.reflect.Method;
import javax.media.jai.JAI;
import javax.media.jai.RegistryMode;
import javax.media.jai.tilecodec.TileCodecDescriptor;
import javax.media.jai.tilecodec.TileCodecParameterList;
import javax.media.jai.tilecodec.TileDecoderFactory;
import javax.media.jai.util.ImagingListener;


















public class TileDecoderRegistryMode
  extends RegistryMode
{
  public static final String MODE_NAME = "tileDecoder";
  private static Method factoryMethod = null;
  
  private static Method getThisFactoryMethod()
  {
    if (factoryMethod != null) {
      return factoryMethod;
    }
    
    Class factoryClass = TileDecoderFactory.class;
    try
    {
      Class[] paramTypes = { InputStream.class, TileCodecParameterList.class };
      

      factoryMethod = factoryClass.getMethod("createDecoder", paramTypes);
    }
    catch (NoSuchMethodException e) {
      ImagingListener listener = JAI.getDefaultInstance().getImagingListener();
      
      String message = JaiI18N.getString("RegistryMode0") + " " + factoryClass.getName() + ".";
      
      listener.errorOccurred(message, e, TileDecoderRegistryMode.class, false);
    }
    


    return factoryMethod;
  }
  



  public TileDecoderRegistryMode()
  {
    super("tileDecoder", TileCodecDescriptor.class, getThisFactoryMethod().getReturnType(), getThisFactoryMethod(), true, false);
  }
}
