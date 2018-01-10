package javax.media.jai.registry;

import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import javax.media.jai.JAI;
import javax.media.jai.OperationRegistry;
import javax.media.jai.tilecodec.TileCodecParameterList;
import javax.media.jai.tilecodec.TileEncoder;
import javax.media.jai.tilecodec.TileEncoderFactory;










































public final class TileEncoderRegistry
{
  private static final String MODE_NAME = "tileEncoder";
  
  public TileEncoderRegistry() {}
  
  public static void register(OperationRegistry registry, String formatName, String productName, TileEncoderFactory tef)
  {
    registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
    

    registry.registerFactory("tileEncoder", formatName, productName, tef);
  }
  

























  public static void unregister(OperationRegistry registry, String formatName, String productName, TileEncoderFactory tef)
  {
    registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
    

    registry.unregisterFactory("tileEncoder", formatName, productName, tef);
  }
  




























  public static void setPreference(OperationRegistry registry, String formatName, String productName, TileEncoderFactory preferredTEF, TileEncoderFactory otherTEF)
  {
    registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
    

    registry.setFactoryPreference("tileEncoder", formatName, productName, preferredTEF, otherTEF);
  }
  
































  public static void unsetPreference(OperationRegistry registry, String formatName, String productName, TileEncoderFactory preferredTEF, TileEncoderFactory otherTEF)
  {
    registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
    

    registry.unsetFactoryPreference("tileEncoder", formatName, productName, preferredTEF, otherTEF);
  }
  






















  public static void clearPreferences(OperationRegistry registry, String formatName, String productName)
  {
    registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
    

    registry.clearFactoryPreferences("tileEncoder", formatName, productName);
  }
  




















  public static List getOrderedList(OperationRegistry registry, String formatName, String productName)
  {
    registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
    

    return registry.getOrderedFactoryList("tileEncoder", formatName, productName);
  }
  
























  public static Iterator getIterator(OperationRegistry registry, String formatName)
  {
    registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
    

    return registry.getFactoryIterator("tileEncoder", formatName);
  }
  




















  public static TileEncoderFactory get(OperationRegistry registry, String formatName)
  {
    registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
    

    return (TileEncoderFactory)registry.getFactory("tileEncoder", formatName);
  }
  




































  public static TileEncoder create(OperationRegistry registry, String formatName, OutputStream output, TileCodecParameterList paramList, SampleModel sampleModel)
  {
    registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
    

    Object[] args = { output, paramList, sampleModel };
    
    return (TileEncoder)registry.invokeFactory("tileEncoder", formatName, args);
  }
  
























































  public static void encode(OperationRegistry registry, String formatName, Raster raster, OutputStream output, TileCodecParameterList param)
    throws IOException
  {
    TileEncoder encoder = create(registry, formatName, output, param, raster.getSampleModel());
    




    if (encoder != null) {
      encoder.encode(raster);
    }
  }
}
