package com.sun.media.jai.opimage;

import com.sun.media.jai.util.ImageUtil;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import javax.media.jai.PlanarImage;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFormatTag;
import javax.media.jai.Warp;
import javax.media.jai.WarpOpImage;
import javax.media.jai.iterator.RandomIter;
import javax.media.jai.iterator.RandomIterFactory;
























final class WarpGeneralOpImage
  extends WarpOpImage
{
  private byte[][] ctable = (byte[][])null;
  














  public WarpGeneralOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, Warp warp, Interpolation interp, double[] backgroundValues)
  {
    super(source, layout, config, false, extender, interp, warp, backgroundValues);
    












    ColorModel srcColorModel = source.getColorModel();
    if ((srcColorModel instanceof IndexColorModel)) {
      IndexColorModel icm = (IndexColorModel)srcColorModel;
      ctable = new byte[3][icm.getMapSize()];
      icm.getReds(ctable[0]);
      icm.getGreens(ctable[1]);
      icm.getBlues(ctable[2]);
    }
  }
  



  protected void computeRect(PlanarImage[] sources, WritableRaster dest, Rectangle destRect)
  {
    RasterFormatTag[] formatTags = getFormatTags();
    
    RasterAccessor d = new RasterAccessor(dest, destRect, formatTags[1], getColorModel());
    

    switch (d.getDataType()) {
    case 0: 
      computeRectByte(sources[0], d);
      break;
    case 1: 
      computeRectUShort(sources[0], d);
      break;
    case 2: 
      computeRectShort(sources[0], d);
      break;
    case 3: 
      computeRectInt(sources[0], d);
      break;
    case 4: 
      computeRectFloat(sources[0], d);
      break;
    case 5: 
      computeRectDouble(sources[0], d);
    }
    
    
    if (d.isDataCopy()) {
      d.clampDataArrays();
      d.copyDataToRaster(); } }
  
  private void computeRectByte(PlanarImage src, RasterAccessor dst) { int bpad;
    int bpad;
    int tpad;
    int rpad;
    int lpad; if (interp != null) {
      int lpad = interp.getLeftPadding();
      int rpad = interp.getRightPadding();
      int tpad = interp.getTopPadding();
      bpad = interp.getBottomPadding();
    } else {
      lpad = rpad = tpad = bpad = 0; }
    RandomIter iter;
    int minX;
    int maxX;
    int minY;
    int maxY; RandomIter iter; if (extender != null) {
      int minX = src.getMinX();
      int maxX = src.getMaxX();
      int minY = src.getMinY();
      int maxY = src.getMaxY();
      Rectangle bounds = new Rectangle(src.getMinX() - lpad, src.getMinY() - tpad, src.getWidth() + lpad + rpad, src.getHeight() + tpad + bpad);
      


      iter = RandomIterFactory.create(src.getExtendedData(bounds, extender), bounds);
    }
    else
    {
      minX = src.getMinX() + lpad;
      maxX = src.getMaxX() - rpad;
      minY = src.getMinY() + tpad;
      maxY = src.getMaxY() - bpad;
      iter = RandomIterFactory.create(src, src.getBounds());
    }
    
    int kwidth = interp.getWidth();
    int kheight = interp.getHeight();
    
    int dstWidth = dst.getWidth();
    int dstHeight = dst.getHeight();
    int dstBands = dst.getNumBands();
    
    int lineStride = dst.getScanlineStride();
    int pixelStride = dst.getPixelStride();
    int[] bandOffsets = dst.getBandOffsets();
    byte[][] data = dst.getByteDataArrays();
    
    int precH = 1 << interp.getSubsampleBitsH();
    int precV = 1 << interp.getSubsampleBitsV();
    
    float[] warpData = new float[2 * dstWidth];
    
    int[][] samples = new int[kheight][kwidth];
    
    int lineOffset = 0;
    
    byte[] backgroundByte = new byte[dstBands];
    for (int i = 0; i < dstBands; i++) {
      backgroundByte[i] = ((byte)(int)backgroundValues[i]);
    }
    if (ctable == null) {
      for (int h = 0; h < dstHeight; h++) {
        int pixelOffset = lineOffset;
        lineOffset += lineStride;
        
        warp.warpRect(dst.getX(), dst.getY() + h, dstWidth, 1, warpData);
        
        int count = 0;
        for (int w = 0; w < dstWidth; w++) {
          float sx = warpData[(count++)];
          float sy = warpData[(count++)];
          
          int xint = floor(sx);
          int yint = floor(sy);
          int xfrac = (int)((sx - xint) * precH);
          int yfrac = (int)((sy - yint) * precV);
          
          if ((xint < minX) || (xint >= maxX) || (yint < minY) || (yint >= maxY))
          {

            if (setBackground) {
              for (int b = 0; b < dstBands; b++) {
                data[b][(pixelOffset + bandOffsets[b])] = backgroundByte[b];
              }
            }
          }
          else {
            xint -= lpad;
            yint -= tpad;
            
            for (int b = 0; b < dstBands; b++) {
              for (int j = 0; j < kheight; j++) {
                for (int i = 0; i < kwidth; i++) {
                  samples[j][i] = (iter.getSample(xint + i, yint + j, b) & 0xFF);
                }
              }
              

              data[b][(pixelOffset + bandOffsets[b])] = ImageUtil.clampByte(interp.interpolate(samples, xfrac, yfrac));
            }
          }
          


          pixelOffset += pixelStride;
        }
      }
    } else
      for (int h = 0; h < dstHeight; h++) {
        int pixelOffset = lineOffset;
        lineOffset += lineStride;
        
        warp.warpRect(dst.getX(), dst.getY() + h, dstWidth, 1, warpData);
        
        int count = 0;
        for (int w = 0; w < dstWidth; w++) {
          float sx = warpData[(count++)];
          float sy = warpData[(count++)];
          
          int xint = floor(sx);
          int yint = floor(sy);
          int xfrac = (int)((sx - xint) * precH);
          int yfrac = (int)((sy - yint) * precV);
          
          if ((xint < minX) || (xint >= maxX) || (yint < minY) || (yint >= maxY))
          {

            if (setBackground) {
              for (int b = 0; b < dstBands; b++) {
                data[b][(pixelOffset + bandOffsets[b])] = backgroundByte[b];
              }
            }
          }
          else {
            xint -= lpad;
            yint -= tpad;
            
            for (int b = 0; b < dstBands; b++) {
              byte[] t = ctable[b];
              
              for (int j = 0; j < kheight; j++) {
                for (int i = 0; i < kwidth; i++) {
                  samples[j][i] = (t[(iter.getSample(xint + i, yint + j, 0) & 0xFF)] & 0xFF);
                }
              }
              

              data[b][(pixelOffset + bandOffsets[b])] = ImageUtil.clampByte(interp.interpolate(samples, xfrac, yfrac));
            }
          }
          


          pixelOffset += pixelStride;
        } } }
  
  private void computeRectUShort(PlanarImage src, RasterAccessor dst) { int bpad;
    int bpad;
    int tpad;
    int rpad;
    int lpad;
    if (interp != null) {
      int lpad = interp.getLeftPadding();
      int rpad = interp.getRightPadding();
      int tpad = interp.getTopPadding();
      bpad = interp.getBottomPadding();
    } else {
      lpad = rpad = tpad = bpad = 0; }
    RandomIter iter;
    int minX;
    int maxX;
    int minY;
    int maxY; RandomIter iter; if (extender != null) {
      int minX = src.getMinX();
      int maxX = src.getMaxX();
      int minY = src.getMinY();
      int maxY = src.getMaxY();
      Rectangle bounds = new Rectangle(src.getMinX() - lpad, src.getMinY() - tpad, src.getWidth() + lpad + rpad, src.getHeight() + tpad + bpad);
      


      iter = RandomIterFactory.create(src.getExtendedData(bounds, extender), bounds);
    }
    else
    {
      minX = src.getMinX() + lpad;
      maxX = src.getMaxX() - rpad;
      minY = src.getMinY() + tpad;
      maxY = src.getMaxY() - bpad;
      iter = RandomIterFactory.create(src, src.getBounds());
    }
    
    int kwidth = interp.getWidth();
    int kheight = interp.getHeight();
    
    int dstWidth = dst.getWidth();
    int dstHeight = dst.getHeight();
    int dstBands = dst.getNumBands();
    
    int lineStride = dst.getScanlineStride();
    int pixelStride = dst.getPixelStride();
    int[] bandOffsets = dst.getBandOffsets();
    short[][] data = dst.getShortDataArrays();
    
    int precH = 1 << interp.getSubsampleBitsH();
    int precV = 1 << interp.getSubsampleBitsV();
    
    float[] warpData = new float[2 * dstWidth];
    
    int[][] samples = new int[kheight][kwidth];
    
    int lineOffset = 0;
    
    short[] backgroundUShort = new short[dstBands];
    for (int i = 0; i < dstBands; i++) {
      backgroundUShort[i] = ((short)(int)backgroundValues[i]);
    }
    for (int h = 0; h < dstHeight; h++) {
      int pixelOffset = lineOffset;
      lineOffset += lineStride;
      
      warp.warpRect(dst.getX(), dst.getY() + h, dstWidth, 1, warpData);
      
      int count = 0;
      for (int w = 0; w < dstWidth; w++) {
        float sx = warpData[(count++)];
        float sy = warpData[(count++)];
        
        int xint = floor(sx);
        int yint = floor(sy);
        int xfrac = (int)((sx - xint) * precH);
        int yfrac = (int)((sy - yint) * precV);
        
        if ((xint < minX) || (xint >= maxX) || (yint < minY) || (yint >= maxY))
        {

          if (setBackground) {
            for (int b = 0; b < dstBands; b++) {
              data[b][(pixelOffset + bandOffsets[b])] = backgroundUShort[b];
            }
          }
        }
        else {
          xint -= lpad;
          yint -= tpad;
          
          for (int b = 0; b < dstBands; b++) {
            for (int j = 0; j < kheight; j++) {
              for (int i = 0; i < kwidth; i++) {
                samples[j][i] = (iter.getSample(xint + i, yint + j, b) & 0xFFFF);
              }
            }
            

            data[b][(pixelOffset + bandOffsets[b])] = ImageUtil.clampUShort(interp.interpolate(samples, xfrac, yfrac));
          }
        }
        


        pixelOffset += pixelStride; } } }
  
  private void computeRectShort(PlanarImage src, RasterAccessor dst) { int bpad;
    int bpad;
    int tpad;
    int rpad;
    int lpad;
    if (interp != null) {
      int lpad = interp.getLeftPadding();
      int rpad = interp.getRightPadding();
      int tpad = interp.getTopPadding();
      bpad = interp.getBottomPadding();
    } else {
      lpad = rpad = tpad = bpad = 0; }
    RandomIter iter;
    int minX;
    int maxX;
    int minY;
    int maxY; RandomIter iter; if (extender != null) {
      int minX = src.getMinX();
      int maxX = src.getMaxX();
      int minY = src.getMinY();
      int maxY = src.getMaxY();
      Rectangle bounds = new Rectangle(src.getMinX() - lpad, src.getMinY() - tpad, src.getWidth() + lpad + rpad, src.getHeight() + tpad + bpad);
      


      iter = RandomIterFactory.create(src.getExtendedData(bounds, extender), bounds);
    }
    else
    {
      minX = src.getMinX() + lpad;
      maxX = src.getMaxX() - rpad;
      minY = src.getMinY() + tpad;
      maxY = src.getMaxY() - bpad;
      iter = RandomIterFactory.create(src, src.getBounds());
    }
    
    int kwidth = interp.getWidth();
    int kheight = interp.getHeight();
    
    int dstWidth = dst.getWidth();
    int dstHeight = dst.getHeight();
    int dstBands = dst.getNumBands();
    
    int lineStride = dst.getScanlineStride();
    int pixelStride = dst.getPixelStride();
    int[] bandOffsets = dst.getBandOffsets();
    short[][] data = dst.getShortDataArrays();
    
    int precH = 1 << interp.getSubsampleBitsH();
    int precV = 1 << interp.getSubsampleBitsV();
    
    float[] warpData = new float[2 * dstWidth];
    
    int[][] samples = new int[kheight][kwidth];
    
    int lineOffset = 0;
    
    short[] backgroundShort = new short[dstBands];
    for (int i = 0; i < dstBands; i++) {
      backgroundShort[i] = ((short)(int)backgroundValues[i]);
    }
    for (int h = 0; h < dstHeight; h++) {
      int pixelOffset = lineOffset;
      lineOffset += lineStride;
      
      warp.warpRect(dst.getX(), dst.getY() + h, dstWidth, 1, warpData);
      
      int count = 0;
      for (int w = 0; w < dstWidth; w++) {
        float sx = warpData[(count++)];
        float sy = warpData[(count++)];
        
        int xint = floor(sx);
        int yint = floor(sy);
        int xfrac = (int)((sx - xint) * precH);
        int yfrac = (int)((sy - yint) * precV);
        
        if ((xint < minX) || (xint >= maxX) || (yint < minY) || (yint >= maxY))
        {

          if (setBackground) {
            for (int b = 0; b < dstBands; b++) {
              data[b][(pixelOffset + bandOffsets[b])] = backgroundShort[b];
            }
          }
        }
        else {
          xint -= lpad;
          yint -= tpad;
          
          for (int b = 0; b < dstBands; b++) {
            for (int j = 0; j < kheight; j++) {
              for (int i = 0; i < kwidth; i++) {
                samples[j][i] = iter.getSample(xint + i, yint + j, b);
              }
            }
            

            data[b][(pixelOffset + bandOffsets[b])] = ImageUtil.clampShort(interp.interpolate(samples, xfrac, yfrac));
          }
        }
        


        pixelOffset += pixelStride; } } }
  
  private void computeRectInt(PlanarImage src, RasterAccessor dst) { int bpad;
    int bpad;
    int tpad;
    int rpad;
    int lpad;
    if (interp != null) {
      int lpad = interp.getLeftPadding();
      int rpad = interp.getRightPadding();
      int tpad = interp.getTopPadding();
      bpad = interp.getBottomPadding();
    } else {
      lpad = rpad = tpad = bpad = 0; }
    RandomIter iter;
    int minX;
    int maxX;
    int minY;
    int maxY; RandomIter iter; if (extender != null) {
      int minX = src.getMinX();
      int maxX = src.getMaxX();
      int minY = src.getMinY();
      int maxY = src.getMaxY();
      Rectangle bounds = new Rectangle(src.getMinX() - lpad, src.getMinY() - tpad, src.getWidth() + lpad + rpad, src.getHeight() + tpad + bpad);
      


      iter = RandomIterFactory.create(src.getExtendedData(bounds, extender), bounds);
    }
    else
    {
      minX = src.getMinX() + lpad;
      maxX = src.getMaxX() - rpad;
      minY = src.getMinY() + tpad;
      maxY = src.getMaxY() - bpad;
      iter = RandomIterFactory.create(src, src.getBounds());
    }
    
    int kwidth = interp.getWidth();
    int kheight = interp.getHeight();
    
    int dstWidth = dst.getWidth();
    int dstHeight = dst.getHeight();
    int dstBands = dst.getNumBands();
    
    int lineStride = dst.getScanlineStride();
    int pixelStride = dst.getPixelStride();
    int[] bandOffsets = dst.getBandOffsets();
    int[][] data = dst.getIntDataArrays();
    
    int precH = 1 << interp.getSubsampleBitsH();
    int precV = 1 << interp.getSubsampleBitsV();
    
    float[] warpData = new float[2 * dstWidth];
    
    int[][] samples = new int[kheight][kwidth];
    
    int lineOffset = 0;
    
    int[] backgroundInt = new int[dstBands];
    for (int i = 0; i < dstBands; i++) {
      backgroundInt[i] = ((int)backgroundValues[i]);
    }
    for (int h = 0; h < dstHeight; h++) {
      int pixelOffset = lineOffset;
      lineOffset += lineStride;
      
      warp.warpRect(dst.getX(), dst.getY() + h, dstWidth, 1, warpData);
      
      int count = 0;
      for (int w = 0; w < dstWidth; w++) {
        float sx = warpData[(count++)];
        float sy = warpData[(count++)];
        
        int xint = floor(sx);
        int yint = floor(sy);
        int xfrac = (int)((sx - xint) * precH);
        int yfrac = (int)((sy - yint) * precV);
        
        if ((xint < minX) || (xint >= maxX) || (yint < minY) || (yint >= maxY))
        {

          if (setBackground) {
            for (int b = 0; b < dstBands; b++) {
              data[b][(pixelOffset + bandOffsets[b])] = backgroundInt[b];
            }
          }
        }
        else {
          xint -= lpad;
          yint -= tpad;
          
          for (int b = 0; b < dstBands; b++) {
            for (int j = 0; j < kheight; j++) {
              for (int i = 0; i < kwidth; i++) {
                samples[j][i] = iter.getSample(xint + i, yint + j, b);
              }
            }
            

            data[b][(pixelOffset + bandOffsets[b])] = interp.interpolate(samples, xfrac, yfrac);
          }
        }
        

        pixelOffset += pixelStride; } } }
  
  private void computeRectFloat(PlanarImage src, RasterAccessor dst) { int bpad;
    int bpad;
    int tpad;
    int rpad;
    int lpad;
    if (interp != null) {
      int lpad = interp.getLeftPadding();
      int rpad = interp.getRightPadding();
      int tpad = interp.getTopPadding();
      bpad = interp.getBottomPadding();
    } else {
      lpad = rpad = tpad = bpad = 0; }
    RandomIter iter;
    int minX;
    int maxX;
    int minY;
    int maxY; RandomIter iter; if (extender != null) {
      int minX = src.getMinX();
      int maxX = src.getMaxX();
      int minY = src.getMinY();
      int maxY = src.getMaxY();
      Rectangle bounds = new Rectangle(src.getMinX() - lpad, src.getMinY() - tpad, src.getWidth() + lpad + rpad, src.getHeight() + tpad + bpad);
      


      iter = RandomIterFactory.create(src.getExtendedData(bounds, extender), bounds);
    }
    else
    {
      minX = src.getMinX() + lpad;
      maxX = src.getMaxX() - rpad;
      minY = src.getMinY() + tpad;
      maxY = src.getMaxY() - bpad;
      iter = RandomIterFactory.create(src, src.getBounds());
    }
    
    int kwidth = interp.getWidth();
    int kheight = interp.getHeight();
    
    int dstWidth = dst.getWidth();
    int dstHeight = dst.getHeight();
    int dstBands = dst.getNumBands();
    
    int lineStride = dst.getScanlineStride();
    int pixelStride = dst.getPixelStride();
    int[] bandOffsets = dst.getBandOffsets();
    float[][] data = dst.getFloatDataArrays();
    
    float[] warpData = new float[2 * dstWidth];
    
    float[][] samples = new float[kheight][kwidth];
    
    int lineOffset = 0;
    
    float[] backgroundFloat = new float[dstBands];
    for (int i = 0; i < dstBands; i++) {
      backgroundFloat[i] = ((float)backgroundValues[i]);
    }
    for (int h = 0; h < dstHeight; h++) {
      int pixelOffset = lineOffset;
      lineOffset += lineStride;
      
      warp.warpRect(dst.getX(), dst.getY() + h, dstWidth, 1, warpData);
      
      int count = 0;
      for (int w = 0; w < dstWidth; w++) {
        float sx = warpData[(count++)];
        float sy = warpData[(count++)];
        
        int xint = floor(sx);
        int yint = floor(sy);
        float xfrac = sx - xint;
        float yfrac = sy - yint;
        
        if ((xint < minX) || (xint >= maxX) || (yint < minY) || (yint >= maxY))
        {

          if (setBackground) {
            for (int b = 0; b < dstBands; b++) {
              data[b][(pixelOffset + bandOffsets[b])] = backgroundFloat[b];
            }
          }
        }
        else {
          xint -= lpad;
          yint -= tpad;
          
          for (int b = 0; b < dstBands; b++) {
            for (int j = 0; j < kheight; j++) {
              for (int i = 0; i < kwidth; i++) {
                samples[j][i] = iter.getSampleFloat(xint + i, yint + j, b);
              }
            }
            

            data[b][(pixelOffset + bandOffsets[b])] = interp.interpolate(samples, xfrac, yfrac);
          }
        }
        

        pixelOffset += pixelStride; } } }
  
  private void computeRectDouble(PlanarImage src, RasterAccessor dst) { int bpad;
    int bpad;
    int tpad;
    int rpad;
    int lpad;
    if (interp != null) {
      int lpad = interp.getLeftPadding();
      int rpad = interp.getRightPadding();
      int tpad = interp.getTopPadding();
      bpad = interp.getBottomPadding();
    } else {
      lpad = rpad = tpad = bpad = 0; }
    RandomIter iter;
    int minX;
    int maxX;
    int minY;
    int maxY; RandomIter iter; if (extender != null) {
      int minX = src.getMinX();
      int maxX = src.getMaxX();
      int minY = src.getMinY();
      int maxY = src.getMaxY();
      Rectangle bounds = new Rectangle(src.getMinX() - lpad, src.getMinY() - tpad, src.getWidth() + lpad + rpad, src.getHeight() + tpad + bpad);
      


      iter = RandomIterFactory.create(src.getExtendedData(bounds, extender), bounds);
    }
    else
    {
      minX = src.getMinX() + lpad;
      maxX = src.getMaxX() - rpad;
      minY = src.getMinY() + tpad;
      maxY = src.getMaxY() - bpad;
      iter = RandomIterFactory.create(src, src.getBounds());
    }
    
    int kwidth = interp.getWidth();
    int kheight = interp.getHeight();
    
    int dstWidth = dst.getWidth();
    int dstHeight = dst.getHeight();
    int dstBands = dst.getNumBands();
    
    int lineStride = dst.getScanlineStride();
    int pixelStride = dst.getPixelStride();
    int[] bandOffsets = dst.getBandOffsets();
    double[][] data = dst.getDoubleDataArrays();
    
    float[] warpData = new float[2 * dstWidth];
    
    double[][] samples = new double[kheight][kwidth];
    
    int lineOffset = 0;
    
    for (int h = 0; h < dstHeight; h++) {
      int pixelOffset = lineOffset;
      lineOffset += lineStride;
      
      warp.warpRect(dst.getX(), dst.getY() + h, dstWidth, 1, warpData);
      
      int count = 0;
      for (int w = 0; w < dstWidth; w++) {
        float sx = warpData[(count++)];
        float sy = warpData[(count++)];
        
        int xint = floor(sx);
        int yint = floor(sy);
        float xfrac = sx - xint;
        float yfrac = sy - yint;
        
        if ((xint < minX) || (xint >= maxX) || (yint < minY) || (yint >= maxY))
        {

          if (setBackground) {
            for (int b = 0; b < dstBands; b++) {
              data[b][(pixelOffset + bandOffsets[b])] = backgroundValues[b];
            }
          }
        }
        else {
          xint -= lpad;
          yint -= tpad;
          
          for (int b = 0; b < dstBands; b++) {
            for (int j = 0; j < kheight; j++) {
              for (int i = 0; i < kwidth; i++) {
                samples[j][i] = iter.getSampleDouble(xint + i, yint + j, b);
              }
            }
            

            data[b][(pixelOffset + bandOffsets[b])] = interp.interpolate(samples, xfrac, yfrac);
          }
        }
        

        pixelOffset += pixelStride;
      }
    }
  }
  
  private static final int floor(float f)
  {
    return f >= 0.0F ? (int)f : (int)f - 1;
  }
}
