package com.sun.media.jai.codecimpl;

import com.sun.media.jai.codec.ImageDecodeParam;
import com.sun.media.jai.codec.ImageDecoderImpl;
import com.sun.media.jai.codec.SeekableStream;
import java.awt.image.RenderedImage;
import java.io.IOException;

































public class PNMImageDecoder
  extends ImageDecoderImpl
{
  public PNMImageDecoder(SeekableStream input, ImageDecodeParam param)
  {
    super(input, param);
  }
  
  public RenderedImage decodeAsRenderedImage(int page) throws IOException {
    if (page != 0) {
      throw new IOException(JaiI18N.getString("PNMImageDecoder5"));
    }
    try {
      return new PNMImage(input);
    } catch (Exception e) {
      throw CodecUtils.toIOException(e);
    }
  }
}
