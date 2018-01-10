package javax.media.jai.tilecodec;

import java.awt.image.Raster;
import java.io.IOException;
import java.io.OutputStream;

public abstract interface TileEncoder
{
  public abstract String getFormatName();
  
  public abstract TileCodecParameterList getEncodeParameterList();
  
  public abstract OutputStream getOutputStream();
  
  public abstract void encode(Raster paramRaster)
    throws IOException;
}
