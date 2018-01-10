package javax.media.jai.registry;

import java.awt.Point;
import java.awt.image.Raster;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import javax.media.jai.JAI;
import javax.media.jai.OperationRegistry;
import javax.media.jai.tilecodec.TileCodecParameterList;
import javax.media.jai.tilecodec.TileDecoder;
import javax.media.jai.tilecodec.TileDecoderFactory;














































public final class TileDecoderRegistry
{
  private static final String MODE_NAME = "tileDecoder";
  
  public TileDecoderRegistry() {}
  
  public static void register(OperationRegistry registry, String formatName, String productName, TileDecoderFactory tdf)
  {
    registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
    

    registry.registerFactory("tileDecoder", formatName, productName, tdf);
  }
  




























  public static void unregister(OperationRegistry registry, String formatName, String productName, TileDecoderFactory tdf)
  {
    registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
    

    registry.unregisterFactory("tileDecoder", formatName, productName, tdf);
  }
  































  public static void setPreference(OperationRegistry registry, String formatName, String productName, TileDecoderFactory preferredTDF, TileDecoderFactory otherTDF)
  {
    registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
    

    registry.setFactoryPreference("tileDecoder", formatName, productName, preferredTDF, otherTDF);
  }
  




































  public static void unsetPreference(OperationRegistry registry, String formatName, String productName, TileDecoderFactory preferredTDF, TileDecoderFactory otherTDF)
  {
    registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
    

    registry.unsetFactoryPreference("tileDecoder", formatName, productName, preferredTDF, otherTDF);
  }
  

























  public static void clearPreferences(OperationRegistry registry, String formatName, String productName)
  {
    registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
    

    registry.clearFactoryPreferences("tileDecoder", formatName, productName);
  }
  























  public static List getOrderedList(OperationRegistry registry, String formatName, String productName)
  {
    registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
    

    return registry.getOrderedFactoryList("tileDecoder", formatName, productName);
  }
  



























  public static Iterator getIterator(OperationRegistry registry, String formatName)
  {
    registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
    

    return registry.getFactoryIterator("tileDecoder", formatName);
  }
  





















  public static TileDecoderFactory get(OperationRegistry registry, String formatName)
  {
    registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
    

    return (TileDecoderFactory)registry.getFactory("tileDecoder", formatName);
  }
  












































  public static TileDecoder create(OperationRegistry registry, String formatName, InputStream input, TileCodecParameterList paramList)
  {
    registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
    

    Object[] args = { input, paramList };
    
    return (TileDecoder)registry.invokeFactory("tileDecoder", formatName, args);
  }
  








































































  public static Raster decode(OperationRegistry registry, String formatName, InputStream input, TileCodecParameterList param)
    throws IOException
  {
    TileDecoder decoder = create(registry, formatName, input, param);
    
    if (decoder == null) {
      return null;
    }
    
    return decoder.decode();
  }
  




































































  public static Raster decode(OperationRegistry registry, String formatName, InputStream input, TileCodecParameterList param, Point location)
    throws IOException
  {
    TileDecoder decoder = create(registry, formatName, input, param);
    
    if (decoder == null) {
      return null;
    }
    
    return decoder.decode(location);
  }
}
