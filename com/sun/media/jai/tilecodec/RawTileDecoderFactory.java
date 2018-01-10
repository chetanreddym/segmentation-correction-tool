package com.sun.media.jai.tilecodec;

import java.io.InputStream;
import java.util.Vector;
import javax.media.jai.ParameterListDescriptorImpl;
import javax.media.jai.remote.NegotiableCapability;
import javax.media.jai.tilecodec.TileCodecParameterList;
import javax.media.jai.tilecodec.TileDecoder;
import javax.media.jai.tilecodec.TileDecoderFactory;


























































public class RawTileDecoderFactory
  implements TileDecoderFactory
{
  public RawTileDecoderFactory() {}
  
  public TileDecoder createDecoder(InputStream input, TileCodecParameterList param)
  {
    if (input == null)
      throw new IllegalArgumentException(JaiI18N.getString("TileDecoder0"));
    return new RawTileDecoder(input, param);
  }
  




  public NegotiableCapability getDecodeCapability()
  {
    Vector generators = new Vector();
    generators.add(RawTileDecoderFactory.class);
    
    return new NegotiableCapability("tileCodec", "raw", generators, new ParameterListDescriptorImpl(null, null, null, null, null), false);
  }
}
