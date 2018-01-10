package javax.media.jai.tilecodec;

import java.awt.Point;
import java.awt.image.Raster;
import java.io.IOException;
import java.io.InputStream;

public abstract interface TileDecoder
{
  public abstract String getFormatName();
  
  public abstract TileCodecParameterList getDecodeParameterList();
  
  public abstract InputStream getInputStream();
  
  public abstract Raster decode()
    throws IOException;
  
  public abstract Raster decode(Point paramPoint)
    throws IOException;
}
