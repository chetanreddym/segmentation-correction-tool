package com.sun.media.jai.codecimpl;

import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecodeParam;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.ImageEncodeParam;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codec.TIFFDecodeParam;
import com.sun.media.jai.codec.TIFFEncodeParam;
import java.awt.image.RenderedImage;
import java.io.OutputStream;
















public final class TIFFCodec
  extends ImageCodec
{
  public TIFFCodec() {}
  
  public String getFormatName()
  {
    return "tiff";
  }
  
  public Class getEncodeParamClass() {
    return TIFFEncodeParam.class;
  }
  
  public Class getDecodeParamClass() {
    return TIFFDecodeParam.class;
  }
  
  public boolean canEncodeImage(RenderedImage im, ImageEncodeParam param)
  {
    return true;
  }
  
  protected ImageEncoder createImageEncoder(OutputStream dst, ImageEncodeParam param)
  {
    return new TIFFImageEncoder(dst, param);
  }
  
  protected ImageDecoder createImageDecoder(SeekableStream src, ImageDecodeParam param)
  {
    return new TIFFImageDecoder(src, param);
  }
  
  public int getNumHeaderBytes() {
    return 4;
  }
  
  public boolean isFormatRecognized(byte[] header) {
    if ((header[0] == 73) && (header[1] == 73) && (header[2] == 42) && (header[3] == 0))
    {


      return true;
    }
    
    if ((header[0] == 77) && (header[1] == 77) && (header[2] == 0) && (header[3] == 42))
    {


      return true;
    }
    
    return false;
  }
}
