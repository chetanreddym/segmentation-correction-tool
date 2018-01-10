package com.sun.media.jai.codecimpl;

import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageDecoder;
import com.sun.media.jai.codec.ImageDecodeParam;
import com.sun.media.jai.codec.JPEGDecodeParam;
import com.sun.media.jai.codecimpl.util.ImagingException;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;


































































class JPEGImage
  extends SimpleRenderedImage
{
  private static final Object LOCK = new Object();
  
  private Raster theTile = null;
  







  public JPEGImage(InputStream stream, ImageDecodeParam param)
  {
    if (stream.markSupported()) {
      stream = new NoMarkStream(stream);
    }
    


    BufferedImage image = null;
    synchronized (LOCK) {
      JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(stream);
      
      try
      {
        image = decoder.decodeAsBufferedImage();
      } catch (ImageFormatException e) {
        String message = JaiI18N.getString("JPEGImageDecoder1");
        sendExceptionToListener(message, e);
      }
      catch (IOException e) {
        String message = JaiI18N.getString("JPEGImageDecoder1");
        sendExceptionToListener(message, e);
      }
    }
    

    minX = 0;
    minY = 0;
    tileWidth = (this.width = image.getWidth());
    tileHeight = (this.height = image.getHeight());
    



    if (((param == null) || (((param instanceof JPEGDecodeParam)) && (((JPEGDecodeParam)param).getDecodeToCSM()))) && (!(image.getSampleModel() instanceof ComponentSampleModel)))
    {



      int type = -1;
      int numBands = image.getSampleModel().getNumBands();
      if (numBands == 1) {
        type = 10;
      } else if (numBands == 3) {
        type = 5;
      } else if (numBands == 4) {
        type = 6;
      } else {
        throw new RuntimeException(JaiI18N.getString("JPEGImageDecoder3"));
      }
      
      BufferedImage bi = new BufferedImage(width, height, type);
      bi.getWritableTile(0, 0).setRect(image.getWritableTile(0, 0));
      bi.releaseWritableTile(0, 0);
      image = bi;
    }
    
    sampleModel = image.getSampleModel();
    colorModel = image.getColorModel();
    
    theTile = image.getWritableTile(0, 0);
  }
  
  public synchronized Raster getTile(int tileX, int tileY) {
    if ((tileX != 0) || (tileY != 0)) {
      throw new IllegalArgumentException(JaiI18N.getString("JPEGImageDecoder4"));
    }
    
    return theTile;
  }
  
  public void dispose() {
    theTile = null;
  }
  
  private void sendExceptionToListener(String message, Exception e) {
    ImagingListenerProxy.errorOccurred(message, new ImagingException(message, e), this, false);
  }
}
