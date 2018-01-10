package com.sun.media.jai.tilecodec;

import java.awt.image.Raster;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import javax.media.jai.tilecodec.TileCodecParameterList;
import javax.media.jai.tilecodec.TileEncoderImpl;
























public class GZIPTileEncoder
  extends TileEncoderImpl
{
  public GZIPTileEncoder(OutputStream output, TileCodecParameterList param)
  {
    super("gzip", output, param);
  }
  








  public void encode(Raster ras)
    throws IOException
  {
    if (ras == null) {
      throw new IllegalArgumentException(JaiI18N.getString("TileEncoder1"));
    }
    
    ObjectOutputStream oos = new ObjectOutputStream(new GZIPOutputStream(outputStream));
    
    Object object = TileCodecUtils.serializeRaster(ras);
    oos.writeObject(object);
    oos.close();
  }
}
