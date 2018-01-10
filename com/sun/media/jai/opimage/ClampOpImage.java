package com.sun.media.jai.opimage;

import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ImageLayout;
import javax.media.jai.PlanarImage;
import javax.media.jai.PointOpImage;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFormatTag;
































final class ClampOpImage
  extends PointOpImage
{
  private byte[][] byteTable = (byte[][])null;
  

  private final double[] low;
  
  private final double[] high;
  

  private synchronized void initByteTable()
  {
    if (byteTable == null)
    {
      int numBands = getSampleModel().getNumBands();
      byteTable = new byte[numBands]['Ā'];
      for (int b = 0; b < numBands; b++) {
        byte[] t = byteTable[b];
        int l = (int)low[b];
        int h = (int)high[b];
        
        byte bl = (byte)l;
        byte bh = (byte)h;
        
        for (int i = 0; i < 256; i++) {
          if (i < l) {
            t[i] = bl;
          } else if (i > h) {
            t[i] = bh;
          } else {
            t[i] = ((byte)i);
          }
        }
      }
    }
  }
  











  public ClampOpImage(RenderedImage source, Map config, ImageLayout layout, double[] low, double[] high)
  {
    super(source, layout, config, true);
    
    int numBands = getSampleModel().getNumBands();
    
    if ((low.length < numBands) || (high.length < numBands)) {
      this.low = new double[numBands];
      this.high = new double[numBands];
      for (int i = 0; i < numBands; i++) {
        this.low[i] = low[0];
        this.high[i] = high[0];
      }
    } else {
      this.low = ((double[])low.clone());
      this.high = ((double[])high.clone());
    }
    

    permitInPlaceOperation();
  }
  











  protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect)
  {
    RasterFormatTag[] formatTags = getFormatTags();
    
    Rectangle srcRect = mapDestRect(destRect, 0);
    
    RasterAccessor src = new RasterAccessor(sources[0], srcRect, formatTags[0], getSourceImage(0).getColorModel());
    

    RasterAccessor dst = new RasterAccessor(dest, destRect, formatTags[1], getColorModel());
    

    switch (dst.getDataType()) {
    case 0: 
      computeRectByte(src, dst);
      break;
    case 1: 
      computeRectUShort(src, dst);
      break;
    case 2: 
      computeRectShort(src, dst);
      break;
    case 3: 
      computeRectInt(src, dst);
      break;
    case 4: 
      computeRectFloat(src, dst);
      break;
    case 5: 
      computeRectDouble(src, dst);
    }
    
    
    dst.copyDataToRaster();
  }
  
  private void computeRectByte(RasterAccessor src, RasterAccessor dst)
  {
    initByteTable();
    
    int dstWidth = dst.getWidth();
    int dstHeight = dst.getHeight();
    int dstBands = dst.getNumBands();
    
    int dstLineStride = dst.getScanlineStride();
    int dstPixelStride = dst.getPixelStride();
    int[] dstBandOffsets = dst.getBandOffsets();
    byte[][] dstData = dst.getByteDataArrays();
    
    int srcLineStride = src.getScanlineStride();
    int srcPixelStride = src.getPixelStride();
    int[] srcBandOffsets = src.getBandOffsets();
    byte[][] srcData = src.getByteDataArrays();
    
    for (int b = 0; b < dstBands; b++) {
      byte[] d = dstData[b];
      byte[] s = srcData[b];
      byte[] t = byteTable[b];
      
      int dstLineOffset = dstBandOffsets[b];
      int srcLineOffset = srcBandOffsets[b];
      
      for (int h = 0; h < dstHeight; h++) {
        int dstPixelOffset = dstLineOffset;
        int srcPixelOffset = srcLineOffset;
        
        dstLineOffset += dstLineStride;
        srcLineOffset += srcLineStride;
        
        for (int w = 0; w < dstWidth; w++) {
          d[dstPixelOffset] = t[(s[srcPixelOffset] & 0xFF)];
          

          dstPixelOffset += dstPixelStride;
          srcPixelOffset += srcPixelStride;
        }
      }
    }
  }
  
  private void computeRectUShort(RasterAccessor src, RasterAccessor dst)
  {
    int dstWidth = dst.getWidth();
    int dstHeight = dst.getHeight();
    int dstBands = dst.getNumBands();
    
    int dstLineStride = dst.getScanlineStride();
    int dstPixelStride = dst.getPixelStride();
    int[] dstBandOffsets = dst.getBandOffsets();
    short[][] dstData = dst.getShortDataArrays();
    
    int srcLineStride = src.getScanlineStride();
    int srcPixelStride = src.getPixelStride();
    int[] srcBandOffsets = src.getBandOffsets();
    short[][] srcData = src.getShortDataArrays();
    
    for (int b = 0; b < dstBands; b++) {
      short[] d = dstData[b];
      short[] s = srcData[b];
      int lo = (int)low[b];
      int hi = (int)high[b];
      
      short slo = (short)lo;
      short shi = (short)hi;
      
      int dstLineOffset = dstBandOffsets[b];
      int srcLineOffset = srcBandOffsets[b];
      
      for (int h = 0; h < dstHeight; h++) {
        int dstPixelOffset = dstLineOffset;
        int srcPixelOffset = srcLineOffset;
        
        dstLineOffset += dstLineStride;
        srcLineOffset += srcLineStride;
        
        for (int w = 0; w < dstWidth; w++) {
          int p = s[srcPixelOffset] & 0xFFFF;
          if (p < lo) {
            d[dstPixelOffset] = slo;
          } else if (p > hi) {
            d[dstPixelOffset] = shi;
          } else {
            d[dstPixelOffset] = ((short)p);
          }
          
          dstPixelOffset += dstPixelStride;
          srcPixelOffset += srcPixelStride;
        }
      }
    }
  }
  
  private void computeRectShort(RasterAccessor src, RasterAccessor dst)
  {
    int dstWidth = dst.getWidth();
    int dstHeight = dst.getHeight();
    int dstBands = dst.getNumBands();
    
    int dstLineStride = dst.getScanlineStride();
    int dstPixelStride = dst.getPixelStride();
    int[] dstBandOffsets = dst.getBandOffsets();
    short[][] dstData = dst.getShortDataArrays();
    
    int srcLineStride = src.getScanlineStride();
    int srcPixelStride = src.getPixelStride();
    int[] srcBandOffsets = src.getBandOffsets();
    short[][] srcData = src.getShortDataArrays();
    
    for (int b = 0; b < dstBands; b++) {
      short[] d = dstData[b];
      short[] s = srcData[b];
      int lo = (int)low[b];
      int hi = (int)high[b];
      
      short slo = (short)lo;
      short shi = (short)hi;
      
      int dstLineOffset = dstBandOffsets[b];
      int srcLineOffset = srcBandOffsets[b];
      
      for (int h = 0; h < dstHeight; h++) {
        int dstPixelOffset = dstLineOffset;
        int srcPixelOffset = srcLineOffset;
        
        dstLineOffset += dstLineStride;
        srcLineOffset += srcLineStride;
        
        for (int w = 0; w < dstWidth; w++) {
          short p = s[srcPixelOffset];
          if (p < lo) {
            d[dstPixelOffset] = slo;
          } else if (p > hi) {
            d[dstPixelOffset] = shi;
          } else {
            d[dstPixelOffset] = p;
          }
          
          dstPixelOffset += dstPixelStride;
          srcPixelOffset += srcPixelStride;
        }
      }
    }
  }
  
  private void computeRectInt(RasterAccessor src, RasterAccessor dst)
  {
    int dstWidth = dst.getWidth();
    int dstHeight = dst.getHeight();
    int dstBands = dst.getNumBands();
    
    int dstLineStride = dst.getScanlineStride();
    int dstPixelStride = dst.getPixelStride();
    int[] dstBandOffsets = dst.getBandOffsets();
    int[][] dstData = dst.getIntDataArrays();
    
    int srcLineStride = src.getScanlineStride();
    int srcPixelStride = src.getPixelStride();
    int[] srcBandOffsets = src.getBandOffsets();
    int[][] srcData = src.getIntDataArrays();
    
    for (int b = 0; b < dstBands; b++) {
      int[] d = dstData[b];
      int[] s = srcData[b];
      double lo = low[b];
      double hi = high[b];
      
      int ilo = (int)lo;
      int ihi = (int)hi;
      
      int dstLineOffset = dstBandOffsets[b];
      int srcLineOffset = srcBandOffsets[b];
      
      for (int h = 0; h < dstHeight; h++) {
        int dstPixelOffset = dstLineOffset;
        int srcPixelOffset = srcLineOffset;
        
        dstLineOffset += dstLineStride;
        srcLineOffset += srcLineStride;
        
        for (int w = 0; w < dstWidth; w++) {
          int p = s[srcPixelOffset];
          if (p < lo) {
            d[dstPixelOffset] = ilo;
          } else if (p > hi) {
            d[dstPixelOffset] = ihi;
          } else {
            d[dstPixelOffset] = p;
          }
          
          dstPixelOffset += dstPixelStride;
          srcPixelOffset += srcPixelStride;
        }
      }
    }
  }
  
  private void computeRectFloat(RasterAccessor src, RasterAccessor dst)
  {
    int dstWidth = dst.getWidth();
    int dstHeight = dst.getHeight();
    int dstBands = dst.getNumBands();
    
    int dstLineStride = dst.getScanlineStride();
    int dstPixelStride = dst.getPixelStride();
    int[] dstBandOffsets = dst.getBandOffsets();
    float[][] dstData = dst.getFloatDataArrays();
    
    int srcLineStride = src.getScanlineStride();
    int srcPixelStride = src.getPixelStride();
    int[] srcBandOffsets = src.getBandOffsets();
    float[][] srcData = src.getFloatDataArrays();
    
    for (int b = 0; b < dstBands; b++) {
      float[] d = dstData[b];
      float[] s = srcData[b];
      double lo = low[b];
      double hi = high[b];
      
      float flo = (float)lo;
      float fhi = (float)hi;
      
      int dstLineOffset = dstBandOffsets[b];
      int srcLineOffset = srcBandOffsets[b];
      
      for (int h = 0; h < dstHeight; h++) {
        int dstPixelOffset = dstLineOffset;
        int srcPixelOffset = srcLineOffset;
        
        dstLineOffset += dstLineStride;
        srcLineOffset += srcLineStride;
        
        for (int w = 0; w < dstWidth; w++) {
          float p = s[srcPixelOffset];
          if (p < lo) {
            d[dstPixelOffset] = flo;
          } else if (p > hi) {
            d[dstPixelOffset] = fhi;
          } else {
            d[dstPixelOffset] = p;
          }
          
          dstPixelOffset += dstPixelStride;
          srcPixelOffset += srcPixelStride;
        }
      }
    }
  }
  
  private void computeRectDouble(RasterAccessor src, RasterAccessor dst)
  {
    int dstWidth = dst.getWidth();
    int dstHeight = dst.getHeight();
    int dstBands = dst.getNumBands();
    
    int dstLineStride = dst.getScanlineStride();
    int dstPixelStride = dst.getPixelStride();
    int[] dstBandOffsets = dst.getBandOffsets();
    double[][] dstData = dst.getDoubleDataArrays();
    
    int srcLineStride = src.getScanlineStride();
    int srcPixelStride = src.getPixelStride();
    int[] srcBandOffsets = src.getBandOffsets();
    double[][] srcData = src.getDoubleDataArrays();
    
    for (int b = 0; b < dstBands; b++) {
      double[] d = dstData[b];
      double[] s = srcData[b];
      double lo = low[b];
      double hi = high[b];
      
      int dstLineOffset = dstBandOffsets[b];
      int srcLineOffset = srcBandOffsets[b];
      
      for (int h = 0; h < dstHeight; h++) {
        int dstPixelOffset = dstLineOffset;
        int srcPixelOffset = srcLineOffset;
        
        dstLineOffset += dstLineStride;
        srcLineOffset += srcLineStride;
        
        for (int w = 0; w < dstWidth; w++) {
          double p = s[srcPixelOffset];
          if (p < lo) {
            d[dstPixelOffset] = lo;
          } else if (p > hi) {
            d[dstPixelOffset] = hi;
          } else {
            d[dstPixelOffset] = p;
          }
          
          dstPixelOffset += dstPixelStride;
          srcPixelOffset += srcPixelStride;
        }
      }
    }
  }
}
