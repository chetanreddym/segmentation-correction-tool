package com.sun.media.jai.tilecodec;

import java.io.InputStream;
import java.util.Vector;
import javax.media.jai.ParameterListDescriptorImpl;
import javax.media.jai.remote.NegotiableCapability;
import javax.media.jai.tilecodec.TileCodecParameterList;
import javax.media.jai.tilecodec.TileDecoder;
import javax.media.jai.tilecodec.TileDecoderFactory;



























































public class GZIPTileDecoderFactory
  implements TileDecoderFactory
{
  public GZIPTileDecoderFactory() {}
  
  public TileDecoder createDecoder(InputStream input, TileCodecParameterList param)
  {
    if (input == null) {
      throw new IllegalArgumentException(JaiI18N.getString("TileDecoder0"));
    }
    return new GZIPTileDecoder(input, param);
  }
  




  public NegotiableCapability getDecodeCapability()
  {
    Vector generators = new Vector();
    generators.add(GZIPTileDecoderFactory.class);
    
    return new NegotiableCapability("tileCodec", "gzip", generators, new ParameterListDescriptorImpl(null, null, null, null, null), false);
  }
}
