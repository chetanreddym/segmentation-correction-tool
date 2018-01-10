package com.sun.media.jai.util;

import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.util.Vector;
import javax.media.jai.PlanarImage;









public class PlanarImageProducer
  implements ImageProducer
{
  PlanarImage im;
  Vector consumers = new Vector();
  
  public PlanarImageProducer(PlanarImage im) {
    this.im = im.createSnapshot();
  }
  
  public void addConsumer(ImageConsumer ic) {
    if (!consumers.contains(ic)) {
      consumers.add(ic);
    }
    produceImage();
  }
  
  public boolean isConsumer(ImageConsumer ic) {
    return consumers.contains(ic);
  }
  
  public void removeConsumer(ImageConsumer ic) {
    consumers.remove(ic);
  }
  
  public void requestTopDownLeftRightResend(ImageConsumer ic) {
    startProduction(ic);
  }
  
  public void startProduction(ImageConsumer ic) {
    if (!consumers.contains(ic)) {
      consumers.add(ic);
    }
    produceImage();
  }
  
  private synchronized void produceImage() {
    int numConsumers = consumers.size();
    
    int minX = im.getMinX();
    int minY = im.getMinY();
    int width = im.getWidth();
    int height = im.getHeight();
    int numBands = im.getSampleModel().getNumBands();
    int scansize = width * numBands;
    ColorModel colorModel = im.getColorModel();
    int[] pixels = new int[scansize];
    
    Rectangle rect = new Rectangle(minX, minY, width, 1);
    
    for (int i = 0; i < numConsumers; i++) {
      ImageConsumer ic = (ImageConsumer)consumers.elementAt(i);
      ic.setHints(22);
    }
    


    for (int y = minY; y < minY + height; y++) {
      y = y;
      Raster row = im.getData(rect);
      row.getPixels(minX, y, width, 1, pixels);
      
      for (int i = 0; i < numConsumers; i++) {
        ImageConsumer ic = (ImageConsumer)consumers.elementAt(i);
        ic.setPixels(0, y - minY, width, 1, colorModel, pixels, 0, scansize);
      }
    }
    

    for (int i = 0; i < numConsumers; i++) {
      ImageConsumer ic = (ImageConsumer)consumers.elementAt(i);
      ic.imageComplete(3);
    }
  }
}
