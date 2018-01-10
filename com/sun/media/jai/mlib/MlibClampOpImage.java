package com.sun.media.jai.mlib;

import com.sun.media.jai.util.ImageUtil;
import com.sun.medialib.mlib.Image;
import com.sun.medialib.mlib.mediaLibImage;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ImageLayout;
import javax.media.jai.PointOpImage;






























final class MlibClampOpImage
  extends PointOpImage
{
  private double[] low;
  private int[] lowInt;
  private double[] high;
  private int[] highInt;
  
  public MlibClampOpImage(RenderedImage source, Map config, ImageLayout layout, double[] low, double[] high)
  {
    super(source, layout, config, true);
    
    int numBands = getSampleModel().getNumBands();
    this.low = new double[numBands];
    lowInt = new int[numBands];
    this.high = new double[numBands];
    highInt = new int[numBands];
    
    for (int i = 0; i < numBands; i++) {
      if (low.length < numBands) {
        this.low[i] = low[0];
      } else {
        this.low[i] = low[i];
      }
      lowInt[i] = ImageUtil.clampRoundInt(this.low[i]);
      
      if (high.length < numBands) {
        this.high[i] = high[0];
      } else {
        this.high[i] = high[i];
      }
      highInt[i] = ImageUtil.clampRoundInt(this.high[i]);
    }
    
    permitInPlaceOperation();
  }
  





  protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect)
  {
    int formatTag = MediaLibAccessor.findCompatibleTag(sources, dest);
    
    MediaLibAccessor srcMA = new MediaLibAccessor(sources[0], destRect, formatTag);
    
    MediaLibAccessor dstMA = new MediaLibAccessor(dest, destRect, formatTag);
    

    mediaLibImage[] srcMLI = srcMA.getMediaLibImages();
    mediaLibImage[] dstMLI = dstMA.getMediaLibImages();
    
    switch (dstMA.getDataType()) {
    case 0: 
    case 1: 
    case 2: 
    case 3: 
      for (int i = 0; i < dstMLI.length; i++) {
        int[] mlLow = dstMA.getIntParameters(i, lowInt);
        int[] mlHigh = dstMA.getIntParameters(i, highInt);
        Image.Thresh4(dstMLI[i], srcMLI[i], mlHigh, mlLow, mlHigh, mlLow);
      }
      
      break;
    
    case 4: 
    case 5: 
      for (int i = 0; i < dstMLI.length; i++) {
        double[] mlLow = dstMA.getDoubleParameters(i, low);
        double[] mlHigh = dstMA.getDoubleParameters(i, high);
        Image.Thresh4_Fp(dstMLI[i], srcMLI[i], mlHigh, mlLow, mlHigh, mlLow);
      }
      
      break;
    
    default: 
      throw new RuntimeException(JaiI18N.getString("Generic2"));
    }
    
    if (dstMA.isDataCopy()) {
      dstMA.clampDataArrays();
      dstMA.copyDataToRaster();
    }
  }
}
