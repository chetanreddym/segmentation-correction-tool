package com.sun.media.jai.codecimpl;

import com.sun.media.jai.codec.FPXDecodeParam;
import com.sun.media.jai.codec.ImageDecodeParam;
import com.sun.media.jai.codec.ImageDecoderImpl;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codecimpl.fpx.FPXImage;
import java.awt.image.RenderedImage;
import java.io.IOException;















public class FPXImageDecoder
  extends ImageDecoderImpl
{
  public FPXImageDecoder(SeekableStream input, ImageDecodeParam param)
  {
    super(input, param);
  }
  
  public RenderedImage decodeAsRenderedImage(int page) throws IOException {
    if (page != 0) {
      throw new IOException(JaiI18N.getString("FPXImageDecoder0"));
    }
    try {
      return new FPXImage(input, (FPXDecodeParam)param);
    } catch (Exception e) {
      throw CodecUtils.toIOException(e);
    }
  }
}
