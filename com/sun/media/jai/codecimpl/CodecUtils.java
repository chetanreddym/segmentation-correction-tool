package com.sun.media.jai.codecimpl;

import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.io.IOException;
import java.lang.reflect.Method;

















class CodecUtils
{
  static Method ioExceptionInitCause;
  
  static
  {
    try
    {
      Class c = Class.forName("java.io.IOException");
      ioExceptionInitCause = c.getMethod("initCause", new Class[] { Throwable.class });
    }
    catch (Exception e)
    {
      ioExceptionInitCause = null;
    }
  }
  







  static final boolean isPackedByteImage(RenderedImage im)
  {
    SampleModel imageSampleModel = im.getSampleModel();
    
    if ((imageSampleModel instanceof SinglePixelPackedSampleModel)) {
      for (int i = 0; i < imageSampleModel.getNumBands(); i++) {
        if (imageSampleModel.getSampleSize(i) > 8) {
          return false;
        }
      }
      
      return true;
    }
    
    return false;
  }
  

  static final IOException toIOException(Exception cause)
  {
    IOException ioe;
    
    IOException ioe;
    if (cause != null) { IOException ioe;
      if ((cause instanceof IOException)) {
        ioe = (IOException)cause;
      } else if (ioExceptionInitCause != null) {
        IOException ioe = new IOException(cause.getMessage());
        try {
          ioExceptionInitCause.invoke(ioe, new Object[] { cause });
        }
        catch (Exception e2) {}
      }
      else {
        ioe = new IOException(cause.getClass().getName() + ": " + cause.getMessage());
      }
    }
    else {
      ioe = new IOException();
    }
    
    return ioe;
  }
  
  CodecUtils() {}
}
