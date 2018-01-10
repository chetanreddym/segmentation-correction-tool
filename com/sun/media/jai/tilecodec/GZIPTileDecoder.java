package com.sun.media.jai.tilecodec;

import com.sun.media.jai.util.ImageUtil;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.Raster;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.zip.GZIPInputStream;
import javax.media.jai.tilecodec.TileCodecParameterList;
import javax.media.jai.tilecodec.TileDecoderImpl;
import javax.media.jai.util.ImagingListener;

































public class GZIPTileDecoder
  extends TileDecoderImpl
{
  public GZIPTileDecoder(InputStream input, TileCodecParameterList param)
  {
    super("gzip", input, param);
  }
  












  public Raster decode()
    throws IOException
  {
    ObjectInputStream ois = new ObjectInputStream(new GZIPInputStream(inputStream));
    
    try
    {
      Object object = ois.readObject();
      return TileCodecUtils.deserializeRaster(object);
    }
    catch (ClassNotFoundException e) {
      ImagingListener listener = ImageUtil.getImagingListener((RenderingHints)null);
      
      listener.errorOccurred(JaiI18N.getString("ClassNotFound"), e, this, false);
      


      return null;
    }
    finally {
      ois.close();
    }
  }
  
  public Raster decode(Point location) throws IOException {
    return decode();
  }
}
