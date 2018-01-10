package com.sun.media.jai.opimage;

import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.AreaOpImage;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.PlanarImage;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFormatTag;
import javax.media.jai.operator.MaxFilterShape;







































abstract class MaxFilterOpImage
  extends AreaOpImage
{
  protected MaxFilterShape maskType;
  protected int maskSize;
  
  public MaxFilterOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, MaxFilterShape maskType, int maskSize)
  {
    super(source, layout, config, true, extender, (maskSize - 1) / 2, (maskSize - 1) / 2, maskSize / 2, maskSize / 2);
    








    this.maskType = maskType;
    this.maskSize = maskSize;
  }
  











  protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect)
  {
    RasterFormatTag[] formatTags = getFormatTags();
    
    Raster source = sources[0];
    Rectangle srcRect = mapDestRect(destRect, 0);
    

    RasterAccessor srcAccessor = new RasterAccessor(source, srcRect, formatTags[0], getSourceImage(0).getColorModel());
    


    RasterAccessor dstAccessor = new RasterAccessor(dest, destRect, formatTags[1], getColorModel());
    


    switch (dstAccessor.getDataType()) {
    case 0: 
      byteLoop(srcAccessor, dstAccessor, maskSize);
      break;
    case 2: 
      shortLoop(srcAccessor, dstAccessor, maskSize);
      break;
    case 1: 
      ushortLoop(srcAccessor, dstAccessor, maskSize);
      break;
    case 3: 
      intLoop(srcAccessor, dstAccessor, maskSize);
      break;
    case 4: 
      floatLoop(srcAccessor, dstAccessor, maskSize);
      break;
    case 5: 
      doubleLoop(srcAccessor, dstAccessor, maskSize);
    }
    
    



    if (dstAccessor.isDataCopy()) {
      dstAccessor.clampDataArrays();
      dstAccessor.copyDataToRaster();
    }
  }
  



  protected abstract void byteLoop(RasterAccessor paramRasterAccessor1, RasterAccessor paramRasterAccessor2, int paramInt);
  



  protected abstract void shortLoop(RasterAccessor paramRasterAccessor1, RasterAccessor paramRasterAccessor2, int paramInt);
  



  protected abstract void ushortLoop(RasterAccessor paramRasterAccessor1, RasterAccessor paramRasterAccessor2, int paramInt);
  



  protected abstract void intLoop(RasterAccessor paramRasterAccessor1, RasterAccessor paramRasterAccessor2, int paramInt);
  


  protected abstract void floatLoop(RasterAccessor paramRasterAccessor1, RasterAccessor paramRasterAccessor2, int paramInt);
  


  protected abstract void doubleLoop(RasterAccessor paramRasterAccessor1, RasterAccessor paramRasterAccessor2, int paramInt);
  


  static final int maxFilter(int[] data)
  {
    if (data.length == 3) {
      int a = data[0];
      int b = data[1];
      int c = data[2];
      if (a < b) {
        return b < c ? c : b;
      }
      return a < c ? c : a;
    }
    

    int max = data[0];
    for (int i = 1; i < data.length; i++)
      if (max < data[i])
        max = data[i];
    return max;
  }
  

  static final float maxFilterFloat(float[] data)
  {
    if (data.length == 3) {
      float a = data[0];
      float b = data[1];
      float c = data[2];
      if (a < b) {
        return b < c ? c : b;
      }
      return a < c ? c : a;
    }
    

    float max = data[0];
    for (int i = 1; i < data.length; i++)
      if (max < data[i])
        max = data[i];
    return max;
  }
  


  static final double maxFilterDouble(double[] data)
  {
    if (data.length == 3) {
      double a = data[0];
      double b = data[1];
      double c = data[2];
      if (a < b) {
        return b < c ? c : b;
      }
      return a < c ? c : a;
    }
    

    double max = data[0];
    for (int i = 1; i < data.length; i++)
      if (max < data[i])
        max = data[i];
    return max;
  }
}
