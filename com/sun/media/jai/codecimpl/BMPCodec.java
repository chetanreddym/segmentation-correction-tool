package com.sun.media.jai.codecimpl;

import com.sun.media.jai.codec.BMPEncodeParam;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecodeParam;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.ImageEncodeParam;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.SeekableStream;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;














public final class BMPCodec
  extends ImageCodec
{
  public BMPCodec() {}
  
  public String getFormatName()
  {
    return "bmp";
  }
  
  public Class getEncodeParamClass() {
    return BMPEncodeParam.class;
  }
  
  public Class getDecodeParamClass() {
    return Object.class;
  }
  
  public boolean canEncodeImage(RenderedImage im, ImageEncodeParam param)
  {
    SampleModel sampleModel = im.getSampleModel();
    int dataType = sampleModel.getTransferType();
    if ((dataType != 0) && (!CodecUtils.isPackedByteImage(im)))
    {
      return false;
    }
    
    if (param != null) {
      if (!(param instanceof BMPEncodeParam)) {
        return false;
      }
      BMPEncodeParam BMPParam = (BMPEncodeParam)param;
      
      int version = BMPParam.getVersion();
      if ((version == 0) || (version == 2))
      {
        return false;
      }
    }
    
    return true;
  }
  
  protected ImageEncoder createImageEncoder(OutputStream dst, ImageEncodeParam param)
  {
    BMPEncodeParam p = null;
    if (param != null) {
      p = (BMPEncodeParam)param;
    }
    
    return new BMPImageEncoder(dst, p);
  }
  
  protected ImageDecoder createImageDecoder(InputStream src, ImageDecodeParam param)
  {
    return new BMPImageDecoder(src, null);
  }
  
  protected ImageDecoder createImageDecoder(File src, ImageDecodeParam param)
    throws IOException
  {
    return new BMPImageDecoder(new FileInputStream(src), null);
  }
  
  protected ImageDecoder createImageDecoder(SeekableStream src, ImageDecodeParam param)
  {
    return new BMPImageDecoder(src, null);
  }
  
  public int getNumHeaderBytes() {
    return 2;
  }
  
  public boolean isFormatRecognized(byte[] header) {
    return (header[0] == 66) && (header[1] == 77);
  }
}
