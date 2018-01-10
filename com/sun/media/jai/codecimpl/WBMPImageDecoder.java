package com.sun.media.jai.codecimpl;

import com.sun.media.jai.codec.ImageDecodeParam;
import com.sun.media.jai.codec.ImageDecoderImpl;
import com.sun.media.jai.codec.SeekableStream;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
























































































































































































































final class WBMPImageDecoder
  extends ImageDecoderImpl
{
  public WBMPImageDecoder(SeekableStream input, ImageDecodeParam param)
  {
    super(input, param);
  }
  
  public RenderedImage decodeAsRenderedImage(int page) throws IOException {
    if (page != 0) {
      throw new IOException(JaiI18N.getString(JaiI18N.getString("WBMPImageDecoder0")));
    }
    
    input.read();
    input.read();
    

    int value = input.read();
    int width = value & 0x7F;
    while ((value & 0x80) == 128) {
      width <<= 7;
      value = input.read();
      width |= value & 0x7F;
    }
    

    value = input.read();
    int height = value & 0x7F;
    while ((value & 0x80) == 128) {
      height <<= 7;
      value = input.read();
      height |= value & 0x7F;
    }
    

    BufferedImage bi = new BufferedImage(width, height, 12);
    



    WritableRaster tile = bi.getWritableTile(0, 0);
    

    MultiPixelPackedSampleModel sm = (MultiPixelPackedSampleModel)bi.getSampleModel();
    


    input.readFully(((DataBufferByte)tile.getDataBuffer()).getData(), 0, height * sm.getScanlineStride());
    

    return bi;
  }
}