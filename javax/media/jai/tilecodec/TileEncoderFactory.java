package javax.media.jai.tilecodec;

import java.awt.image.SampleModel;
import java.io.OutputStream;
import javax.media.jai.remote.NegotiableCapability;

public abstract interface TileEncoderFactory
{
  public abstract TileEncoder createEncoder(OutputStream paramOutputStream, TileCodecParameterList paramTileCodecParameterList, SampleModel paramSampleModel);
  
  public abstract NegotiableCapability getEncodeCapability();
}
