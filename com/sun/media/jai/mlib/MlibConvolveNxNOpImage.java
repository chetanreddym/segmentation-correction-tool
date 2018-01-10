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
























































final class MlibConvolveNxNOpImage
  extends AreaOpImage
{
  protected KernelJAI kernel;
  private int kw;
  private int kh;
  float[] kData;
  double[] doublekData;
  int[] intkData;
  int shift = -1;
  



















  public MlibConvolveNxNOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, KernelJAI kernel)
  {
    super(source, layout, config, true, extender, kernel.getLeftPadding(), kernel.getRightPadding(), kernel.getTopPadding(), kernel.getBottomPadding());
    








    this.kernel = kernel;
    kw = kernel.getWidth();
    kh = kernel.getHeight();
    


    kData = kernel.getKernelData();
    
    int count = kw * kh;
    


    intkData = new int[count];
    doublekData = new double[count];
    for (int i = 0; i < count; i++) {
      doublekData[i] = kData[i];
    }
  }
  
  private synchronized void setShift(int formatTag) {
    if (shift == -1) {
      int mediaLibDataType = MediaLibAccessor.getMediaLibDataType(formatTag);
      
      shift = Image.ConvKernelConvert(intkData, doublekData, kw, kh, mediaLibDataType);
    }
  }
  














  protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect)
  {
    Raster source = sources[0];
    Rectangle srcRect = mapDestRect(destRect, 0);
    
    int formatTag = MediaLibAccessor.findCompatibleTag(sources, dest);
    
    MediaLibAccessor srcAccessor = new MediaLibAccessor(source, srcRect, formatTag, true);
    
    MediaLibAccessor dstAccessor = new MediaLibAccessor(dest, destRect, formatTag, true);
    
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
        
        if (kw == 2) {
          Image.Conv2x2(dstML[i], srcML[i], intkData, shift, (1 << numBands) - 1, 0);


        }
        else if (kw == 3) {
          Image.Conv3x3(dstML[i], srcML[i], intkData, shift, (1 << numBands) - 1, 0);


        }
        else if (kw == 4) {
          Image.Conv4x4(dstML[i], srcML[i], intkData, shift, (1 << numBands) - 1, 0);


        }
        else if (kw == 5) {
          Image.Conv5x5(dstML[i], srcML[i], intkData, shift, (1 << numBands) - 1, 0);


        }
        else if (kw == 7) {
          Image.Conv7x7(dstML[i], srcML[i], intkData, shift, (1 << numBands) - 1, 0);
        }
        


        break;
      case 4: 
      case 5: 
        if (kw == 2) {
          Image.Conv2x2_Fp(dstML[i], srcML[i], doublekData, (1 << numBands) - 1, 0);


        }
        else if (kw == 3) {
          Image.Conv3x3_Fp(dstML[i], srcML[i], doublekData, (1 << numBands) - 1, 0);


        }
        else if (kw == 4) {
          Image.Conv4x4_Fp(dstML[i], srcML[i], doublekData, (1 << numBands) - 1, 0);


        }
        else if (kw == 5) {
          Image.Conv5x5_Fp(dstML[i], srcML[i], doublekData, (1 << numBands) - 1, 0);


        }
        else if (kw == 7) {
          Image.Conv7x7_Fp(dstML[i], srcML[i], doublekData, (1 << numBands) - 1, 0);
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
