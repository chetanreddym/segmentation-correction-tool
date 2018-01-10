package com.sun.media.jai.opimage;

import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ColormapOpImage;
import javax.media.jai.ImageLayout;
import javax.media.jai.PlanarImage;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFormatTag;


































final class ThresholdOpImage
  extends ColormapOpImage
{
  private double[] low;
  private double[] high;
  private double[] constants;
  private byte[][] byteTable = (byte[][])null;
  













  public ThresholdOpImage(RenderedImage source, Map config, ImageLayout layout, double[] low, double[] high, double[] constants)
  {
    super(source, layout, config, true);
    
    int numBands = getSampleModel().getNumBands();
    this.low = new double[numBands];
    this.high = new double[numBands];
    this.constants = new double[numBands];
    
    for (int i = 0; i < numBands; i++) {
      if (low.length < numBands) {
        this.low[i] = low[0];
      } else {
        this.low[i] = low[i];
      }
      if (high.length < numBands) {
        this.high[i] = high[0];
      } else {
        this.high[i] = high[i];
      }
      if (constants.length < numBands) {
        this.constants[i] = constants[0];
      } else {
        this.constants[i] = constants[i];
      }
    }
    

    permitInPlaceOperation();
    

    initializeColormapOperation();
  }
  


  protected void transformColormap(byte[][] colormap)
  {
    initByteTable();
    
    for (int b = 0; b < 3; b++) {
      byte[] map = colormap[b];
      byte[] luTable = byteTable[b];
      int mapSize = map.length;
      
      for (int i = 0; i < mapSize; i++) {
        map[i] = luTable[(map[i] & 0xFF)];
      }
    }
  }
  











  protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect)
  {
    RasterFormatTag[] formatTags = getFormatTags();
    
    Rectangle srcRect = mapDestRect(destRect, 0);
    
    RasterAccessor src = new RasterAccessor(sources[0], srcRect, formatTags[0], getSource(0).getColorModel());
    

    RasterAccessor dst = new RasterAccessor(dest, destRect, formatTags[1], getColorModel());
    

    int srcPixelStride = src.getPixelStride();
    int srcLineStride = src.getScanlineStride();
    int[] srcBandOffsets = src.getBandOffsets();
    
    int dstPixelStride = dst.getPixelStride();
    int dstLineStride = dst.getScanlineStride();
    int[] dstBandOffsets = dst.getBandOffsets();
    
    int width = dst.getWidth() * dstPixelStride;
    int height = dst.getHeight() * dstLineStride;
    int bands = dst.getNumBands();
    
    switch (dst.getDataType()) {
    case 0: 
      byteLoop(width, height, bands, srcPixelStride, srcLineStride, srcBandOffsets, src.getByteDataArrays(), dstPixelStride, dstLineStride, dstBandOffsets, dst.getByteDataArrays());
      



      break;
    
    case 2: 
      shortLoop(width, height, bands, srcPixelStride, srcLineStride, srcBandOffsets, src.getShortDataArrays(), dstPixelStride, dstLineStride, dstBandOffsets, dst.getShortDataArrays());
      



      break;
    
    case 1: 
      ushortLoop(width, height, bands, srcPixelStride, srcLineStride, srcBandOffsets, src.getShortDataArrays(), dstPixelStride, dstLineStride, dstBandOffsets, dst.getShortDataArrays());
      



      break;
    
    case 3: 
      intLoop(width, height, bands, srcPixelStride, srcLineStride, srcBandOffsets, src.getIntDataArrays(), dstPixelStride, dstLineStride, dstBandOffsets, dst.getIntDataArrays());
      



      break;
    
    case 4: 
      floatLoop(width, height, bands, srcPixelStride, srcLineStride, srcBandOffsets, src.getFloatDataArrays(), dstPixelStride, dstLineStride, dstBandOffsets, dst.getFloatDataArrays());
      



      break;
    
    case 5: 
      doubleLoop(width, height, bands, srcPixelStride, srcLineStride, srcBandOffsets, src.getDoubleDataArrays(), dstPixelStride, dstLineStride, dstBandOffsets, dst.getDoubleDataArrays());
    }
    
    




    if (dst.isDataCopy()) {
      dst.clampDataArrays();
      dst.copyDataToRaster();
    }
  }
  




  private void byteLoop(int width, int height, int bands, int srcPixelStride, int srcLineStride, int[] srcBandOffsets, byte[][] srcData, int dstPixelStride, int dstLineStride, int[] dstBandOffsets, byte[][] dstData)
  {
    initByteTable();
    
    for (int b = 0; b < bands; b++) {
      byte[] s = srcData[b];
      byte[] d = dstData[b];
      byte[] t = byteTable[b];
      
      int heightEnd = dstBandOffsets[b] + height;
      
      int dstLineOffset = dstBandOffsets[b];
      int srcLineOffset = srcBandOffsets[b];
      while (dstLineOffset < heightEnd)
      {


        int widthEnd = dstLineOffset + width;
        
        int dstPixelOffset = dstLineOffset;
        int srcPixelOffset = srcLineOffset;
        while (dstPixelOffset < widthEnd)
        {


          d[dstPixelOffset] = t[(s[srcPixelOffset] & 0xFF)];dstPixelOffset += dstPixelStride;srcPixelOffset += srcPixelStride;
        }
        dstLineOffset += dstLineStride;
        srcLineOffset += srcLineStride;
      }
    }
  }
  














  private void shortLoop(int width, int height, int bands, int srcPixelStride, int srcLineStride, int[] srcBandOffsets, short[][] srcData, int dstPixelStride, int dstLineStride, int[] dstBandOffsets, short[][] dstData)
  {
    for (int b = 0; b < bands; b++) {
      short[] s = srcData[b];
      short[] d = dstData[b];
      
      double l = low[b];
      double h = high[b];
      short c = (short)(int)constants[b];
      
      int heightEnd = dstBandOffsets[b] + height;
      
      int dstLineOffset = dstBandOffsets[b];
      int srcLineOffset = srcBandOffsets[b];
      while (dstLineOffset < heightEnd)
      {


        int widthEnd = dstLineOffset + width;
        
        int dstPixelOffset = dstLineOffset;
        int srcPixelOffset = srcLineOffset;
        while (dstPixelOffset < widthEnd)
        {


          short p = s[srcPixelOffset];
          
          if ((p >= l) && (p <= h)) {
            d[dstPixelOffset] = c;
          } else {
            d[dstPixelOffset] = p;
          }
          dstPixelOffset += dstPixelStride;
          srcPixelOffset += srcPixelStride;
        }
        dstLineOffset += dstLineStride;
        srcLineOffset += srcLineStride;
      }
    }
  }
  





















  private void ushortLoop(int width, int height, int bands, int srcPixelStride, int srcLineStride, int[] srcBandOffsets, short[][] srcData, int dstPixelStride, int dstLineStride, int[] dstBandOffsets, short[][] dstData)
  {
    for (int b = 0; b < bands; b++) {
      short[] s = srcData[b];
      short[] d = dstData[b];
      
      double l = low[b];
      double h = high[b];
      short c = (short)(int)constants[b];
      
      int heightEnd = dstBandOffsets[b] + height;
      
      int dstLineOffset = dstBandOffsets[b];
      int srcLineOffset = srcBandOffsets[b];
      while (dstLineOffset < heightEnd)
      {


        int widthEnd = dstLineOffset + width;
        
        int dstPixelOffset = dstLineOffset;
        int srcPixelOffset = srcLineOffset;
        while (dstPixelOffset < widthEnd)
        {


          int p = s[srcPixelOffset] & 0xFFFF;
          
          if ((p >= l) && (p <= h)) {
            d[dstPixelOffset] = c;
          } else {
            d[dstPixelOffset] = ((short)p);
          }
          dstPixelOffset += dstPixelStride;
          srcPixelOffset += srcPixelStride;
        }
        dstLineOffset += dstLineStride;
        srcLineOffset += srcLineStride;
      }
    }
  }
  





















  private void intLoop(int width, int height, int bands, int srcPixelStride, int srcLineStride, int[] srcBandOffsets, int[][] srcData, int dstPixelStride, int dstLineStride, int[] dstBandOffsets, int[][] dstData)
  {
    for (int b = 0; b < bands; b++) {
      int[] s = srcData[b];
      int[] d = dstData[b];
      
      double l = low[b];
      double h = high[b];
      int c = (int)constants[b];
      
      int heightEnd = dstBandOffsets[b] + height;
      
      int dstLineOffset = dstBandOffsets[b];
      int srcLineOffset = srcBandOffsets[b];
      while (dstLineOffset < heightEnd)
      {


        int widthEnd = dstLineOffset + width;
        
        int dstPixelOffset = dstLineOffset;
        int srcPixelOffset = srcLineOffset;
        while (dstPixelOffset < widthEnd)
        {


          int p = s[srcPixelOffset];
          
          if ((p >= l) && (p <= h)) {
            d[dstPixelOffset] = c;
          } else {
            d[dstPixelOffset] = p;
          }
          dstPixelOffset += dstPixelStride;
          srcPixelOffset += srcPixelStride;
        }
        dstLineOffset += dstLineStride;
        srcLineOffset += srcLineStride;
      }
    }
  }
  





















  private void floatLoop(int width, int height, int bands, int srcPixelStride, int srcLineStride, int[] srcBandOffsets, float[][] srcData, int dstPixelStride, int dstLineStride, int[] dstBandOffsets, float[][] dstData)
  {
    for (int b = 0; b < bands; b++) {
      float[] s = srcData[b];
      float[] d = dstData[b];
      
      double l = low[b];
      double h = high[b];
      float c = (float)constants[b];
      
      int heightEnd = dstBandOffsets[b] + height;
      
      int dstLineOffset = dstBandOffsets[b];
      int srcLineOffset = srcBandOffsets[b];
      while (dstLineOffset < heightEnd)
      {


        int widthEnd = dstLineOffset + width;
        
        int dstPixelOffset = dstLineOffset;
        int srcPixelOffset = srcLineOffset;
        while (dstPixelOffset < widthEnd)
        {


          float p = s[srcPixelOffset];
          
          if ((p >= l) && (p <= h)) {
            d[dstPixelOffset] = c;
          } else {
            d[dstPixelOffset] = p;
          }
          dstPixelOffset += dstPixelStride;
          srcPixelOffset += srcPixelStride;
        }
        dstLineOffset += dstLineStride;
        srcLineOffset += srcLineStride;
      }
    }
  }
  





















  private void doubleLoop(int width, int height, int bands, int srcPixelStride, int srcLineStride, int[] srcBandOffsets, double[][] srcData, int dstPixelStride, int dstLineStride, int[] dstBandOffsets, double[][] dstData)
  {
    for (int b = 0; b < bands; b++) {
      double[] s = srcData[b];
      double[] d = dstData[b];
      
      double l = low[b];
      double h = high[b];
      double c = constants[b];
      
      int heightEnd = dstBandOffsets[b] + height;
      
      int dstLineOffset = dstBandOffsets[b];
      int srcLineOffset = srcBandOffsets[b];
      while (dstLineOffset < heightEnd)
      {


        int widthEnd = dstLineOffset + width;
        
        int dstPixelOffset = dstLineOffset;
        int srcPixelOffset = srcLineOffset;
        while (dstPixelOffset < widthEnd)
        {


          double p = s[srcPixelOffset];
          
          if ((p >= l) && (p <= h)) {
            d[dstPixelOffset] = c;
          } else {
            d[dstPixelOffset] = p;
          }
          dstPixelOffset += dstPixelStride;
          srcPixelOffset += srcPixelStride;
        }
        dstLineOffset += dstLineStride;
        srcLineOffset += srcLineStride;
      }
    }
  }
  

















  private synchronized void initByteTable()
  {
    if (byteTable != null) {
      return;
    }
    
    int numBands = getSampleModel().getNumBands();
    byteTable = new byte[numBands]['Ā'];
    
    for (int b = 0; b < numBands; b++) {
      double l = low[b];
      double h = high[b];
      byte c = (byte)(int)constants[b];
      
      byte[] t = byteTable[b];
      
      for (int i = 0; i < 256; i++) {
        if ((i >= l) && (i <= h)) {
          t[i] = c;
        } else {
          t[i] = ((byte)i);
        }
      }
    }
  }
}
