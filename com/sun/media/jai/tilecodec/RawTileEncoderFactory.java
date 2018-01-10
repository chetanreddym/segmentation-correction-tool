package com.sun.media.jai.tilecodec;

import java.awt.image.SampleModel;
import java.io.OutputStream;
import java.util.Vector;
import javax.media.jai.ParameterListDescriptorImpl;
import javax.media.jai.remote.NegotiableCapability;
import javax.media.jai.tilecodec.TileCodecParameterList;
import javax.media.jai.tilecodec.TileEncoder;
import javax.media.jai.tilecodec.TileEncoderFactory;

















































public class RawTileEncoderFactory
  implements TileEncoderFactory
{
  public RawTileEncoderFactory() {}
  
  public TileEncoder createEncoder(OutputStream output, TileCodecParameterList paramList, SampleModel sampleModel)
  {
    if (output == null)
      throw new IllegalArgumentException(JaiI18N.getString("TileEncoder0"));
    return new RawTileEncoder(output, paramList);
  }
  




  public NegotiableCapability getEncodeCapability()
  {
    Vector generators = new Vector();
    generators.add(RawTileEncoderFactory.class);
    
    return new NegotiableCapability("tileCodec", "raw", generators, new ParameterListDescriptorImpl(null, null, null, null, null), false);
  }
}
