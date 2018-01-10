package com.sun.media.jai.opimage;

import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import javax.media.jai.PlanarImage;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFormatTag;
import javax.media.jai.Warp;
import javax.media.jai.WarpOpImage;
import javax.media.jai.iterator.RandomIter;
import javax.media.jai.iterator.RandomIterFactory;








































final class WarpNearestOpImage
  extends WarpOpImage
{
  public WarpNearestOpImage(RenderedImage source, Map config, ImageLayout layout, Warp warp, Interpolation interp, double[] backgroundValues)
  {
    super(source, layout, config, false, null, interp, warp, backgroundValues);
    













    ColorModel srcColorModel = source.getColorModel();
    if ((srcColorModel instanceof IndexColorModel)) {
      sampleModel = source.getSampleModel().createCompatibleSampleModel(tileWidth, tileHeight);
      
      colorModel = srcColorModel;
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
  
  private void computeRectByte(PlanarImage src, RasterAccessor dst) {
    RandomIter iter = RandomIterFactory.create(src, src.getBounds());
    
    int minX = src.getMinX();
    int maxX = src.getMaxX();
    int minY = src.getMinY();
    int maxY = src.getMaxY();
    
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
    for (int h = 0; h < dstHeight; h++) {
      int pixelOffset = lineOffset;
      lineOffset += lineStride;
      
      warp.warpRect(dst.getX(), dst.getY() + h, dstWidth, 1, warpData);
      
      int count = 0;
      for (int w = 0; w < dstWidth; w++)
      {





        int sx = round(warpData[(count++)]);
        int sy = round(warpData[(count++)]);
        
        if ((sx < minX) || (sx >= maxX) || (sy < minY) || (sy >= maxY))
        {
          if (setBackground) {
            for (int b = 0; b < dstBands; b++) {
              data[b][(pixelOffset + bandOffsets[b])] = backgroundByte[b];
            }
          }
        }
        else {
          for (int b = 0; b < dstBands; b++) {
            data[b][(pixelOffset + bandOffsets[b])] = ((byte)(iter.getSample(sx, sy, b) & 0xFF));
          }
        }
        

        pixelOffset += pixelStride;
      }
    }
  }
  
  private void computeRectUShort(PlanarImage src, RasterAccessor dst) {
    RandomIter iter = RandomIterFactory.create(src, src.getBounds());
    
    int minX = src.getMinX();
    int maxX = src.getMaxX();
    int minY = src.getMinY();
    int maxY = src.getMaxY();
    
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
      for (int w = 0; w < dstWidth; w++)
      {





        int sx = round(warpData[(count++)]);
        int sy = round(warpData[(count++)]);
        
        if ((sx < minX) || (sx >= maxX) || (sy < minY) || (sy >= maxY))
        {
          if (setBackground) {
            for (int b = 0; b < dstBands; b++) {
              data[b][(pixelOffset + bandOffsets[b])] = backgroundUShort[b];
            }
          }
        }
        else {
          for (int b = 0; b < dstBands; b++) {
            data[b][(pixelOffset + bandOffsets[b])] = ((short)(iter.getSample(sx, sy, b) & 0xFFFF));
          }
        }
        

        pixelOffset += pixelStride;
      }
    }
  }
  
  private void computeRectShort(PlanarImage src, RasterAccessor dst) {
    RandomIter iter = RandomIterFactory.create(src, src.getBounds());
    
    int minX = src.getMinX();
    int maxX = src.getMaxX();
    int minY = src.getMinY();
    int maxY = src.getMaxY();
    
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
      for (int w = 0; w < dstWidth; w++)
      {





        int sx = round(warpData[(count++)]);
        int sy = round(warpData[(count++)]);
        
        if ((sx < minX) || (sx >= maxX) || (sy < minY) || (sy >= maxY))
        {
          if (setBackground) {
            for (int b = 0; b < dstBands; b++) {
              data[b][(pixelOffset + bandOffsets[b])] = backgroundShort[b];
            }
          }
        }
        else {
          for (int b = 0; b < dstBands; b++) {
            data[b][(pixelOffset + bandOffsets[b])] = ((short)iter.getSample(sx, sy, b));
          }
        }
        

        pixelOffset += pixelStride;
      }
    }
  }
  
  private void computeRectInt(PlanarImage src, RasterAccessor dst) {
    RandomIter iter = RandomIterFactory.create(src, src.getBounds());
    
    int minX = src.getMinX();
    int maxX = src.getMaxX();
    int minY = src.getMinY();
    int maxY = src.getMaxY();
    
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
      for (int w = 0; w < dstWidth; w++)
      {





        int sx = round(warpData[(count++)]);
        int sy = round(warpData[(count++)]);
        
        if ((sx < minX) || (sx >= maxX) || (sy < minY) || (sy >= maxY))
        {
          if (setBackground) {
            for (int b = 0; b < dstBands; b++) {
              data[b][(pixelOffset + bandOffsets[b])] = backgroundInt[b];
            }
          }
        }
        else {
          for (int b = 0; b < dstBands; b++) {
            data[b][(pixelOffset + bandOffsets[b])] = iter.getSample(sx, sy, b);
          }
        }
        

        pixelOffset += pixelStride;
      }
    }
  }
  
  private void computeRectFloat(PlanarImage src, RasterAccessor dst) {
    RandomIter iter = RandomIterFactory.create(src, src.getBounds());
    
    int minX = src.getMinX();
    int maxX = src.getMaxX();
    int minY = src.getMinY();
    int maxY = src.getMaxY();
    
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
      for (int w = 0; w < dstWidth; w++)
      {





        int sx = round(warpData[(count++)]);
        int sy = round(warpData[(count++)]);
        
        if ((sx < minX) || (sx >= maxX) || (sy < minY) || (sy >= maxY))
        {
          if (setBackground) {
            for (int b = 0; b < dstBands; b++) {
              data[b][(pixelOffset + bandOffsets[b])] = backgroundFloat[b];
            }
          }
        }
        else {
          for (int b = 0; b < dstBands; b++) {
            data[b][(pixelOffset + bandOffsets[b])] = iter.getSampleFloat(sx, sy, b);
          }
        }
        

        pixelOffset += pixelStride;
      }
    }
  }
  
  private void computeRectDouble(PlanarImage src, RasterAccessor dst) {
    RandomIter iter = RandomIterFactory.create(src, src.getBounds());
    
    int minX = src.getMinX();
    int maxX = src.getMaxX();
    int minY = src.getMinY();
    int maxY = src.getMaxY();
    
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
      for (int w = 0; w < dstWidth; w++)
      {





        int sx = round(warpData[(count++)]);
        int sy = round(warpData[(count++)]);
        
        if ((sx < minX) || (sx >= maxX) || (sy < minY) || (sy >= maxY))
        {
          if (setBackground) {
            for (int b = 0; b < dstBands; b++) {
              data[b][(pixelOffset + bandOffsets[b])] = backgroundValues[b];
            }
          }
        }
        else {
          for (int b = 0; b < dstBands; b++) {
            data[b][(pixelOffset + bandOffsets[b])] = iter.getSampleDouble(sx, sy, b);
          }
        }
        

        pixelOffset += pixelStride;
      }
    }
  }
  
  private static final int round(float f)
  {
    return f >= 0.0F ? (int)(f + 0.5F) : (int)(f - 0.5F);
  }
}
