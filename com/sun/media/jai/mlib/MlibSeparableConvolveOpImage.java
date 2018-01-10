package com.sun.media.jai.mlib;

import com.sun.medialib.mlib.Image;
import com.sun.medialib.mlib.mediaLibImage;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.AreaOpImage;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.KernelJAI;























































final class MlibSeparableConvolveOpImage
  extends AreaOpImage
{
  protected KernelJAI kernel;
  private int kw;
  private int kh;
  float[] hValues;
  float[] vValues;
  double[] hDoubleData;
  double[] vDoubleData;
  int[] hIntData;
  int[] vIntData;
  int shift = -1;
  



















  public MlibSeparableConvolveOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, KernelJAI kernel)
  {
    super(source, layout, config, true, extender, kernel.getLeftPadding(), kernel.getRightPadding(), kernel.getTopPadding(), kernel.getBottomPadding());
    








    this.kernel = kernel;
    kw = kernel.getWidth();
    kh = kernel.getHeight();
    


    hValues = kernel.getHorizontalKernelData();
    vValues = kernel.getVerticalKernelData();
    



    hDoubleData = new double[hValues.length];
    for (int i = 0; i < hValues.length; i++) {
      hDoubleData[i] = hValues[i];
    }
    
    vDoubleData = new double[vValues.length];
    for (int i = 0; i < vValues.length; i++) {
      vDoubleData[i] = vValues[i];
    }
    
    hIntData = new int[hValues.length];
    vIntData = new int[vValues.length];
  }
  
  private synchronized void setShift(int formatTag)
  {
    if (shift == -1) {
      int mediaLibDataType = MediaLibAccessor.getMediaLibDataType(formatTag);
      
      shift = Image.SConvKernelConvert(hIntData, vIntData, hDoubleData, vDoubleData, kw, kh, mediaLibDataType);
    }
  }
  
















  protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect)
  {
    Raster source = sources[0];
    Rectangle srcRect = mapDestRect(destRect, 0);
    
    int formatTag = MediaLibAccessor.findCompatibleTag(sources, dest);
    

    MediaLibAccessor srcAccessor = new MediaLibAccessor(source, srcRect, formatTag);
    
    MediaLibAccessor dstAccessor = new MediaLibAccessor(dest, destRect, formatTag);
    
    int numBands = getSampleModel().getNumBands();
    
    mediaLibImage[] srcML = srcAccessor.getMediaLibImages();
    mediaLibImage[] dstML = dstAccessor.getMediaLibImages();
    for (int i = 0; i < dstML.length; i++) {
      switch (dstAccessor.getDataType()) {
      case 0: 
      case 1: 
      case 2: 
      case 3: 
        if (shift == -1) {
          setShift(formatTag);
        }
        switch (kw) {
        case 3: 
          Image.SConv3x3(dstML[i], srcML[i], hIntData, vIntData, shift, (1 << numBands) - 1, 0);
          


          break;
        case 5: 
          Image.SConv5x5(dstML[i], srcML[i], hIntData, vIntData, shift, (1 << numBands) - 1, 0);
          


          break;
        case 7: 
          Image.SConv7x7(dstML[i], srcML[i], hIntData, vIntData, shift, (1 << numBands) - 1, 0);
        }
        
        


        break;
      case 4: 
      case 5: 
        switch (kw) {
        case 3: 
          Image.SConv3x3_Fp(dstML[i], srcML[i], hDoubleData, vDoubleData, (1 << numBands) - 1, 0);
          


          break;
        case 5: 
          Image.SConv5x5_Fp(dstML[i], srcML[i], hDoubleData, vDoubleData, (1 << numBands) - 1, 0);
          


          break;
        case 7: 
          Image.SConv7x7_Fp(dstML[i], srcML[i], hDoubleData, vDoubleData, (1 << numBands) - 1, 0);
        }
        
        


        break;
      default: 
        String className = getClass().getName();
        throw new RuntimeException(JaiI18N.getString("Generic2"));
      }
      
    }
    if (dstAccessor.isDataCopy()) {
      dstAccessor.copyDataToRaster();
    }
  }
}
