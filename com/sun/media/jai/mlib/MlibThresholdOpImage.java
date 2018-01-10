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



































final class MlibThresholdOpImage
  extends PointOpImage
{
  private double[] low;
  private int[] lowInt;
  private double[] high;
  private int[] highInt;
  private double[] constants;
  private int[] constantsInt;
  
  public MlibThresholdOpImage(RenderedImage source, Map config, ImageLayout layout, double[] low, double[] high, double[] constants)
  {
    super(source, layout, config, true);
    
    int numBands = getSampleModel().getNumBands();
    this.low = new double[numBands];
    lowInt = new int[numBands];
    this.high = new double[numBands];
    highInt = new int[numBands];
    this.constants = new double[numBands];
    constantsInt = new int[numBands];
    
    for (int i = 0; i < numBands; i++) {
      if (low.length < numBands) {
        this.low[i] = low[0];
      } else {
        this.low[i] = low[i];
      }
      lowInt[i] = ImageUtil.clampInt((int)Math.ceil(this.low[i]));
      
      if (high.length < numBands) {
        this.high[i] = high[0];
      } else {
        this.high[i] = high[i];
      }
      highInt[i] = ImageUtil.clampInt((int)Math.floor(this.high[i]));
      
      if (constants.length < numBands) {
        this.constants[i] = constants[0];
      } else {
        this.constants[i] = constants[i];
      }
      constantsInt[i] = ImageUtil.clampRoundInt(this.constants[i]);
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
        int[] mlConstants = dstMA.getIntParameters(i, constantsInt);
        Image.Thresh5(dstMLI[i], srcMLI[i], mlHigh, mlLow, mlConstants);
      }
      
      break;
    
    case 4: 
    case 5: 
      for (int i = 0; i < dstMLI.length; i++) {
        double[] mlLow = dstMA.getDoubleParameters(i, low);
        double[] mlHigh = dstMA.getDoubleParameters(i, high);
        double[] mlConstants = dstMA.getDoubleParameters(i, constants);
        Image.Thresh5_Fp(dstMLI[i], srcMLI[i], mlHigh, mlLow, mlConstants);
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
