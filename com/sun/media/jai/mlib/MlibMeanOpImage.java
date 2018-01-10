package com.sun.media.jai.mlib;

import com.sun.media.jai.opimage.MeanOpImage;
import com.sun.medialib.mlib.Image;
import com.sun.medialib.mlib.mediaLibImage;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import javax.media.jai.PlanarImage;
import javax.media.jai.ROI;

































final class MlibMeanOpImage
  extends MeanOpImage
{
  public MlibMeanOpImage(RenderedImage source, ROI roi, int xStart, int yStart, int xPeriod, int yPeriod)
  {
    super(source, roi, xStart, yStart, xPeriod, yPeriod);
  }
  


  protected void accumulateStatistics(String name, Raster source, Object stats)
  {
    PlanarImage sourceImage = getSourceImage(0);
    int numBands = sourceImage.getSampleModel().getNumBands();
    

    int formatTag = MediaLibAccessor.findCompatibleTag(null, source);
    MediaLibAccessor srcAccessor = new MediaLibAccessor(source, source.getBounds(), formatTag);
    



    mediaLibImage[] srcML = srcAccessor.getMediaLibImages();
    


    double[] dmean = new double[numBands];
    
    switch (srcAccessor.getDataType()) {
    case 0: 
    case 1: 
    case 2: 
    case 3: 
      for (int i = 0; i < srcML.length; i++) {
        Image.Mean(dmean, srcML[i]);
      }
      break;
    
    case 4: 
    case 5: 
      for (int i = 0; i < srcML.length; i++) {
        Image.Mean_Fp(dmean, srcML[i]);
      }
      break;
    
    default: 
      throw new RuntimeException(JaiI18N.getString("Generic2"));
    }
    
    dmean = srcAccessor.getDoubleParameters(0, dmean);
    

    double[] mean = (double[])stats;
    double weight = source.getWidth() * source.getHeight() / (width * height);
    

    for (int i = 0; i < numBands; i++) {
      mean[i] += dmean[i] * weight;
    }
  }
}
