package com.sun.media.jai.tilecodec;

import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.text.MessageFormat;
import javax.media.jai.JAI;
import javax.media.jai.OperationRegistry;
import javax.media.jai.remote.SerializableState;
import javax.media.jai.remote.SerializerFactory;
import javax.media.jai.tilecodec.TileCodecDescriptor;













public class TileCodecUtils
{
  private static MessageFormat formatter = new MessageFormat("");
  

  public TileCodecUtils() {}
  

  public static TileCodecDescriptor getTileCodecDescriptor(String registryMode, String formatName)
  {
    return (TileCodecDescriptor)JAI.getDefaultInstance().getOperationRegistry().getDescriptor(registryMode, formatName);
  }
  


  public static Raster deserializeRaster(Object object)
  {
    if (!(object instanceof SerializableState)) {
      return null;
    }
    SerializableState ss = (SerializableState)object;
    Class c = ss.getObjectClass();
    if (Raster.class.isAssignableFrom(c)) {
      return (Raster)ss.getObject();
    }
    return null;
  }
  
  public static SampleModel deserializeSampleModel(Object object)
  {
    if (!(object instanceof SerializableState)) {
      return null;
    }
    SerializableState ss = (SerializableState)object;
    Class c = ss.getObjectClass();
    if (SampleModel.class.isAssignableFrom(c)) {
      return (SampleModel)ss.getObject();
    }
    return null;
  }
  
  public static Object serializeRaster(Raster ras)
  {
    return SerializerFactory.getState(ras, null);
  }
  
  public static Object serializeSampleModel(SampleModel sm)
  {
    return SerializerFactory.getState(sm, null);
  }
}
