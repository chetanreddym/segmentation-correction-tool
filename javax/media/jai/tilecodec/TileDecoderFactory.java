package javax.media.jai.tilecodec;

import java.io.InputStream;
import javax.media.jai.remote.NegotiableCapability;

public abstract interface TileDecoderFactory
{
  public abstract TileDecoder createDecoder(InputStream paramInputStream, TileCodecParameterList paramTileCodecParameterList);
  
  public abstract NegotiableCapability getDecodeCapability();
}
