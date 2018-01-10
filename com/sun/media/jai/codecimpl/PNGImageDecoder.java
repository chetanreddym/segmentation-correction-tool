package com.sun.media.jai.codecimpl;

import com.sun.media.jai.codec.ImageDecoderImpl;
import com.sun.media.jai.codec.PNGDecodeParam;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.InputStream;





















































public class PNGImageDecoder
  extends ImageDecoderImpl
{
  public PNGImageDecoder(InputStream input, PNGDecodeParam param)
  {
    super(input, param);
  }
  
  public RenderedImage decodeAsRenderedImage(int page) throws IOException {
    if (page != 0) {
      throw new IOException(JaiI18N.getString("PNGImageDecoder19"));
    }
    try {
      return new PNGImage(input, (PNGDecodeParam)param);
    } catch (Exception e) {
      throw CodecUtils.toIOException(e);
    }
  }
}
