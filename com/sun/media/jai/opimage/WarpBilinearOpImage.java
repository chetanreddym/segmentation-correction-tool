package com.sun.media.jai.opimage;

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
























final class WarpBilinearOpImage
  extends WarpOpImage
{
  private byte[][] ctable = (byte[][])null;
  














  public WarpBilinearOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, Warp warp, Interpolation interp, double[] backgroundValues)
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
      d.copyDataToRaster();
    }
  }
  
  private void computeRectByte(PlanarImage src, RasterAccessor dst) { RandomIter iter;
    RandomIter iter;
    if (extender != null) {
      Rectangle bounds = new Rectangle(src.getMinX(), src.getMinY(), src.getWidth() + 1, src.getHeight() + 1);
      

      iter = RandomIterFactory.create(src.getExtendedData(bounds, extender), bounds);
    }
    else
    {
      iter = RandomIterFactory.create(src, src.getBounds());
    }
    
    int minX = src.getMinX();
    int maxX = src.getMaxX() - (extender != null ? 0 : 1);
    
    int minY = src.getMinY();
    int maxY = src.getMaxY() - (extender != null ? 0 : 1);
    

    int dstWidth = dst.getWidth();
    int dstHeight = dst.getHeight();
    int dstBands = dst.getNumBands();
    
    int lineStride = dst.getScanlineStride();
    int pixelStride = dst.getPixelStride();
    int[] bandOffsets = dst.getBandOffsets();
    byte[][] data = dst.getByteDataArrays();
    
    float[] warpData = new float[2 * dstWidth];
    
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
          float xfrac = sx - xint;
          float yfrac = sy - yint;
          
          if ((xint < minX) || (xint >= maxX) || (yint < minY) || (yint >= maxY))
          {

            if (setBackground) {
              for (int b = 0; b < dstBands; b++) {
                data[b][(pixelOffset + bandOffsets[b])] = backgroundByte[b];
              }
            }
          }
          else {
            for (int b = 0; b < dstBands; b++) {
              int s00 = iter.getSample(xint, yint, b) & 0xFF;
              int s01 = iter.getSample(xint + 1, yint, b) & 0xFF;
              int s10 = iter.getSample(xint, yint + 1, b) & 0xFF;
              int s11 = iter.getSample(xint + 1, yint + 1, b) & 0xFF;
              
              float s0 = (s01 - s00) * xfrac + s00;
              float s1 = (s11 - s10) * xfrac + s10;
              float s = (s1 - s0) * yfrac + s0;
              
              data[b][(pixelOffset + bandOffsets[b])] = ((byte)(int)s);
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
          float xfrac = sx - xint;
          float yfrac = sy - yint;
          
          if ((xint < minX) || (xint >= maxX) || (yint < minY) || (yint >= maxY))
          {

            if (setBackground) {
              for (int b = 0; b < dstBands; b++) {
                data[b][(pixelOffset + bandOffsets[b])] = backgroundByte[b];
              }
            }
          }
          else {
            for (int b = 0; b < dstBands; b++) {
              byte[] t = ctable[b];
              
              int s00 = t[(iter.getSample(xint, yint, 0) & 0xFF)] & 0xFF;
              
              int s01 = t[(iter.getSample(xint + 1, yint, 0) & 0xFF)] & 0xFF;
              
              int s10 = t[(iter.getSample(xint, yint + 1, 0) & 0xFF)] & 0xFF;
              
              int s11 = t[(iter.getSample(xint + 1, yint + 1, 0) & 0xFF)] & 0xFF;
              

              float s0 = (s01 - s00) * xfrac + s00;
              float s1 = (s11 - s10) * xfrac + s10;
              float s = (s1 - s0) * yfrac + s0;
              
              data[b][(pixelOffset + bandOffsets[b])] = ((byte)(int)s);
            }
          }
          
          pixelOffset += pixelStride;
        }
      }
  }
  
  private void computeRectUShort(PlanarImage src, RasterAccessor dst) {
    RandomIter iter;
    RandomIter iter;
    if (extender != null) {
      Rectangle bounds = new Rectangle(src.getMinX(), src.getMinY(), src.getWidth() + 1, src.getHeight() + 1);
      

      iter = RandomIterFactory.create(src.getExtendedData(bounds, extender), bounds);
    }
    else
    {
      iter = RandomIterFactory.create(src, src.getBounds());
    }
    
    int minX = src.getMinX();
    int maxX = src.getMaxX() - (extender != null ? 0 : 1);
    
    int minY = src.getMinY();
    int maxY = src.getMaxY() - (extender != null ? 0 : 1);
    

    int dstWidth = dst.getWidth();
    int dstHeight = dst.getHeight();
    int dstBands = dst.getNumBands();
    
    int lineStride = dst.getScanlineStride();
    int pixelStride = dst.getPixelStride();
    int[] bandOffsets = dst.getBandOffsets();
    short[][] data = dst.getShortDataArrays();
    
    float[] warpData = new float[2 * dstWidth];
    
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
        float xfrac = sx - xint;
        float yfrac = sy - yint;
        
        if ((xint < minX) || (xint >= maxX) || (yint < minY) || (yint >= maxY))
        {

          if (setBackground) {
            for (int b = 0; b < dstBands; b++) {
              data[b][(pixelOffset + bandOffsets[b])] = backgroundUShort[b];
            }
          }
        }
        else {
          for (int b = 0; b < dstBands; b++) {
            int s00 = iter.getSample(xint, yint, b) & 0xFFFF;
            int s01 = iter.getSample(xint + 1, yint, b) & 0xFFFF;
            int s10 = iter.getSample(xint, yint + 1, b) & 0xFFFF;
            int s11 = iter.getSample(xint + 1, yint + 1, b) & 0xFFFF;
            
            float s0 = (s01 - s00) * xfrac + s00;
            float s1 = (s11 - s10) * xfrac + s10;
            float s = (s1 - s0) * yfrac + s0;
            
            data[b][(pixelOffset + bandOffsets[b])] = ((short)(int)s);
          }
        }
        
        pixelOffset += pixelStride;
      }
    }
  }
  
  private void computeRectShort(PlanarImage src, RasterAccessor dst) { RandomIter iter;
    RandomIter iter;
    if (extender != null) {
      Rectangle bounds = new Rectangle(src.getMinX(), src.getMinY(), src.getWidth() + 1, src.getHeight() + 1);
      

      iter = RandomIterFactory.create(src.getExtendedData(bounds, extender), bounds);
    }
    else
    {
      iter = RandomIterFactory.create(src, src.getBounds());
    }
    
    int minX = src.getMinX();
    int maxX = src.getMaxX() - (extender != null ? 0 : 1);
    
    int minY = src.getMinY();
    int maxY = src.getMaxY() - (extender != null ? 0 : 1);
    

    int dstWidth = dst.getWidth();
    int dstHeight = dst.getHeight();
    int dstBands = dst.getNumBands();
    
    int lineStride = dst.getScanlineStride();
    int pixelStride = dst.getPixelStride();
    int[] bandOffsets = dst.getBandOffsets();
    short[][] data = dst.getShortDataArrays();
    
    float[] warpData = new float[2 * dstWidth];
    
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
        float xfrac = sx - xint;
        float yfrac = sy - yint;
        
        if ((xint < minX) || (xint >= maxX) || (yint < minY) || (yint >= maxY))
        {

          if (setBackground) {
            for (int b = 0; b < dstBands; b++) {
              data[b][(pixelOffset + bandOffsets[b])] = backgroundShort[b];
            }
          }
        }
        else {
          for (int b = 0; b < dstBands; b++) {
            int s00 = iter.getSample(xint, yint, b);
            int s01 = iter.getSample(xint + 1, yint, b);
            int s10 = iter.getSample(xint, yint + 1, b);
            int s11 = iter.getSample(xint + 1, yint + 1, b);
            
            float s0 = (s01 - s00) * xfrac + s00;
            float s1 = (s11 - s10) * xfrac + s10;
            float s = (s1 - s0) * yfrac + s0;
            
            data[b][(pixelOffset + bandOffsets[b])] = ((short)(int)s);
          }
        }
        
        pixelOffset += pixelStride;
      }
    }
  }
  
  private void computeRectInt(PlanarImage src, RasterAccessor dst) { RandomIter iter;
    RandomIter iter;
    if (extender != null) {
      Rectangle bounds = new Rectangle(src.getMinX(), src.getMinY(), src.getWidth() + 1, src.getHeight() + 1);
      

      iter = RandomIterFactory.create(src.getExtendedData(bounds, extender), bounds);
    }
    else
    {
      iter = RandomIterFactory.create(src, src.getBounds());
    }
    
    int minX = src.getMinX();
    int maxX = src.getMaxX() - (extender != null ? 0 : 1);
    
    int minY = src.getMinY();
    int maxY = src.getMaxY() - (extender != null ? 0 : 1);
    

    int dstWidth = dst.getWidth();
    int dstHeight = dst.getHeight();
    int dstBands = dst.getNumBands();
    
    int lineStride = dst.getScanlineStride();
    int pixelStride = dst.getPixelStride();
    int[] bandOffsets = dst.getBandOffsets();
    int[][] data = dst.getIntDataArrays();
    
    float[] warpData = new float[2 * dstWidth];
    
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
        float xfrac = sx - xint;
        float yfrac = sy - yint;
        
        if ((xint < minX) || (xint >= maxX) || (yint < minY) || (yint >= maxY))
        {

          if (setBackground) {
            for (int b = 0; b < dstBands; b++) {
              data[b][(pixelOffset + bandOffsets[b])] = backgroundInt[b];
            }
          }
        }
        else {
          for (int b = 0; b < dstBands; b++) {
            int s00 = iter.getSample(xint, yint, b);
            int s01 = iter.getSample(xint + 1, yint, b);
            int s10 = iter.getSample(xint, yint + 1, b);
            int s11 = iter.getSample(xint + 1, yint + 1, b);
            
            float s0 = (s01 - s00) * xfrac + s00;
            float s1 = (s11 - s10) * xfrac + s10;
            float s = (s1 - s0) * yfrac + s0;
            
            data[b][(pixelOffset + bandOffsets[b])] = ((int)s);
          }
        }
        
        pixelOffset += pixelStride;
      }
    }
  }
  
  private void computeRectFloat(PlanarImage src, RasterAccessor dst) { RandomIter iter;
    RandomIter iter;
    if (extender != null) {
      Rectangle bounds = new Rectangle(src.getMinX(), src.getMinY(), src.getWidth() + 1, src.getHeight() + 1);
      

      iter = RandomIterFactory.create(src.getExtendedData(bounds, extender), bounds);
    }
    else
    {
      iter = RandomIterFactory.create(src, src.getBounds());
    }
    
    int minX = src.getMinX();
    int maxX = src.getMaxX() - (extender != null ? 0 : 1);
    
    int minY = src.getMinY();
    int maxY = src.getMaxY() - (extender != null ? 0 : 1);
    

    int dstWidth = dst.getWidth();
    int dstHeight = dst.getHeight();
    int dstBands = dst.getNumBands();
    
    int lineStride = dst.getScanlineStride();
    int pixelStride = dst.getPixelStride();
    int[] bandOffsets = dst.getBandOffsets();
    float[][] data = dst.getFloatDataArrays();
    
    float[] warpData = new float[2 * dstWidth];
    
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
          for (int b = 0; b < dstBands; b++) {
            float s00 = iter.getSampleFloat(xint, yint, b);
            float s01 = iter.getSampleFloat(xint + 1, yint, b);
            float s10 = iter.getSampleFloat(xint, yint + 1, b);
            float s11 = iter.getSampleFloat(xint + 1, yint + 1, b);
            
            float s0 = (s01 - s00) * xfrac + s00;
            float s1 = (s11 - s10) * xfrac + s10;
            float s = (s1 - s0) * yfrac + s0;
            
            data[b][(pixelOffset + bandOffsets[b])] = s;
          }
        }
        
        pixelOffset += pixelStride;
      }
    }
  }
  
  private void computeRectDouble(PlanarImage src, RasterAccessor dst) { RandomIter iter;
    RandomIter iter;
    if (extender != null) {
      Rectangle bounds = new Rectangle(src.getMinX(), src.getMinY(), src.getWidth() + 1, src.getHeight() + 1);
      

      iter = RandomIterFactory.create(src.getExtendedData(bounds, extender), bounds);
    }
    else
    {
      iter = RandomIterFactory.create(src, src.getBounds());
    }
    
    int minX = src.getMinX();
    int maxX = src.getMaxX() - (extender != null ? 0 : 1);
    
    int minY = src.getMinY();
    int maxY = src.getMaxY() - (extender != null ? 0 : 1);
    

    int dstWidth = dst.getWidth();
    int dstHeight = dst.getHeight();
    int dstBands = dst.getNumBands();
    
    int lineStride = dst.getScanlineStride();
    int pixelStride = dst.getPixelStride();
    int[] bandOffsets = dst.getBandOffsets();
    double[][] data = dst.getDoubleDataArrays();
    
    float[] warpData = new float[2 * dstWidth];
    
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
          for (int b = 0; b < dstBands; b++) {
            double s00 = iter.getSampleDouble(xint, yint, b);
            double s01 = iter.getSampleDouble(xint + 1, yint, b);
            double s10 = iter.getSampleDouble(xint, yint + 1, b);
            double s11 = iter.getSampleDouble(xint + 1, yint + 1, b);
            
            double s0 = (s01 - s00) * xfrac + s00;
            double s1 = (s11 - s10) * xfrac + s10;
            double s = (s1 - s0) * yfrac + s0;
            
            data[b][(pixelOffset + bandOffsets[b])] = s;
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
