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
import javax.media.jai.operator.MedianFilterShape;







































abstract class MedianFilterOpImage
  extends AreaOpImage
{
  protected MedianFilterShape maskType;
  protected int maskSize;
  
  public MedianFilterOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, MedianFilterShape maskType, int maskSize)
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
    

    RasterAccessor srcAccessor = new RasterAccessor(source, srcRect, formatTags[0], getSource(0).getColorModel());
    


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
  


  protected int medianFilter(int[] data)
  {
    if (data.length == 3) {
      int a = data[0];
      int b = data[1];
      int c = data[2];
      if (a < b) {
        if (b < c) {
          return b;
        }
        if (c > a) {
          return c;
        }
        return a;
      }
      

      if (a < c) {
        return a;
      }
      if (b < c) {
        return c;
      }
      return b;
    }
    


    int left = 0;
    int right = data.length - 1;
    int target = data.length / 2;
    for (;;)
    {
      int oleft = left;
      int oright = right;
      int mid = data[((left + right) / 2)];
      do {
        while (data[left] < mid) {
          left++;
        }
        while (mid < data[right]) {
          right--;
        }
        if (left <= right) {
          int tmp = data[left];
          data[left] = data[right];
          data[right] = tmp;
          left++;
          right--;
        }
      } while (left <= right);
      if ((oleft < right) && (right >= target)) {
        left = oleft;
      } else if ((left < oright) && (left <= target)) {
        right = oright;
      } else {
        return data[target];
      }
    }
  }
  
  protected float medianFilterFloat(float[] data)
  {
    if (data.length == 3) {
      float a = data[0];
      float b = data[1];
      float c = data[2];
      if (a < b) {
        if (b < c) {
          return b;
        }
        if (c > a) {
          return c;
        }
        return a;
      }
      

      if (a < c) {
        return a;
      }
      if (b < c) {
        return c;
      }
      return b;
    }
    


    int left = 0;
    int right = data.length - 1;
    int target = data.length / 2;
    for (;;)
    {
      int oleft = left;
      int oright = right;
      float mid = data[((left + right) / 2)];
      do {
        while (data[left] < mid) {
          left++;
        }
        while (mid < data[right]) {
          right--;
        }
        if (left <= right) {
          float tmp = data[left];
          data[left] = data[right];
          data[right] = tmp;
          left++;
          right--;
        }
      } while (left <= right);
      if ((oleft < right) && (right >= target)) {
        left = oleft;
      } else if ((left < oright) && (left <= target)) {
        right = oright;
      } else {
        return data[target];
      }
    }
  }
  
  protected double medianFilterDouble(double[] data)
  {
    if (data.length == 3) {
      double a = data[0];
      double b = data[1];
      double c = data[2];
      if (a < b) {
        if (b < c) {
          return b;
        }
        if (c > a) {
          return c;
        }
        return a;
      }
      

      if (a < c) {
        return a;
      }
      if (b < c) {
        return c;
      }
      return b;
    }
    


    int left = 0;
    int right = data.length - 1;
    int target = data.length / 2;
    for (;;)
    {
      int oleft = left;
      int oright = right;
      double mid = data[((left + right) / 2)];
      do {
        while (data[left] < mid) {
          left++;
        }
        while (mid < data[right]) {
          right--;
        }
        if (left <= right) {
          double tmp = data[left];
          data[left] = data[right];
          data[right] = tmp;
          left++;
          right--;
        }
      } while (left <= right);
      if ((oleft < right) && (right >= target)) {
        left = oleft;
      } else if ((left < oright) && (left <= target)) {
        right = oright;
      } else {
        return data[target];
      }
    }
  }
}
