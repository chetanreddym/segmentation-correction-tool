package com.sun.media.jai.codecimpl;

import com.sun.media.jai.codec.ForwardSeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecodeParam;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.ImageEncodeParam;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.PNMEncodeParam;
import com.sun.media.jai.codec.SeekableStream;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;

























public final class PNMCodec
  extends ImageCodec
{
  public PNMCodec() {}
  
  public String getFormatName()
  {
    return "pnm";
  }
  
  public Class getEncodeParamClass() {
    return PNMEncodeParam.class;
  }
  
  public Class getDecodeParamClass() {
    return Object.class;
  }
  
  public boolean canEncodeImage(RenderedImage im, ImageEncodeParam param)
  {
    SampleModel sampleModel = im.getSampleModel();
    
    int dataType = sampleModel.getTransferType();
    if ((dataType == 4) || (dataType == 5))
    {
      return false;
    }
    
    int numBands = sampleModel.getNumBands();
    if ((numBands != 1) && (numBands != 3)) {
      return false;
    }
    
    return true;
  }
  











  protected ImageEncoder createImageEncoder(OutputStream dst, ImageEncodeParam param)
  {
    PNMEncodeParam p = null;
    if (param != null) {
      p = (PNMEncodeParam)param;
    }
    
    return new PNMImageEncoder(dst, p);
  }
  


















  protected ImageDecoder createImageDecoder(InputStream src, ImageDecodeParam param)
  {
    if (!(src instanceof BufferedInputStream)) {
      src = new BufferedInputStream(src);
    }
    return new PNMImageDecoder(new ForwardSeekableStream(src), null);
  }
  









  protected ImageDecoder createImageDecoder(SeekableStream src, ImageDecodeParam param)
  {
    return new PNMImageDecoder(src, null);
  }
  



  public int getNumHeaderBytes()
  {
    return 2;
  }
  




  public boolean isFormatRecognized(byte[] header)
  {
    return (header[0] == 80) && (header[1] >= 49) && (header[1] <= 54);
  }
}
