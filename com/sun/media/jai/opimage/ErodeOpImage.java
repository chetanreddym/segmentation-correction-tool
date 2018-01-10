package com.sun.media.jai.opimage;

import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.AreaOpImage;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.KernelJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFormatTag;




































































































































final class ErodeOpImage
  extends AreaOpImage
{
  protected KernelJAI kernel;
  private int kw;
  private int kh;
  private int kx;
  private int ky;
  private float[] kdata;
  
  public ErodeOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, KernelJAI kernel)
  {
    super(source, layout, config, true, extender, kernel.getLeftPadding(), kernel.getRightPadding(), kernel.getTopPadding(), kernel.getBottomPadding());
    








    this.kernel = kernel;
    kw = kernel.getWidth();
    kh = kernel.getHeight();
    kx = kernel.getXOrigin();
    ky = kernel.getYOrigin();
    
    kdata = kernel.getKernelData();
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
      byteLoop(srcAccessor, dstAccessor);
      break;
    case 3: 
      intLoop(srcAccessor, dstAccessor);
      break;
    case 2: 
      shortLoop(srcAccessor, dstAccessor);
      break;
    case 1: 
      ushortLoop(srcAccessor, dstAccessor);
      break;
    case 4: 
      floatLoop(srcAccessor, dstAccessor);
      break;
    case 5: 
      doubleLoop(srcAccessor, dstAccessor);
      break;
    }
    
    




    if (dstAccessor.isDataCopy()) {
      dstAccessor.clampDataArrays();
      dstAccessor.copyDataToRaster();
    }
  }
  
  private void byteLoop(RasterAccessor src, RasterAccessor dst) {
    int dwidth = dst.getWidth();
    int dheight = dst.getHeight();
    int dnumBands = dst.getNumBands();
    
    int[] dstBandOffsets = dst.getBandOffsets();
    int dstPixelStride = dst.getPixelStride();
    int dstScanlineStride = dst.getScanlineStride();
    
    int[] srcBandOffsets = src.getBandOffsets();
    int srcPixelStride = src.getPixelStride();
    int srcScanlineStride = src.getScanlineStride();
    
    byte[][] dstDataArrays = dst.getByteDataArrays();
    byte[][] srcDataArrays = src.getByteDataArrays();
    
    for (int k = 0; k < dnumBands; k++) {
      byte[] dstData = dstDataArrays[k];
      byte[] srcData = srcDataArrays[k];
      int srcScanlineOffset = srcBandOffsets[k];
      int dstScanlineOffset = dstBandOffsets[k];
      for (int j = 0; j < dheight; j++) {
        int srcPixelOffset = srcScanlineOffset;
        int dstPixelOffset = dstScanlineOffset;
        
        for (int i = 0; i < dwidth; i++) {
          int kernelVerticalOffset = 0;
          int imageVerticalOffset = srcPixelOffset;
          float f = Float.POSITIVE_INFINITY;
          for (int u = 0; u < kh; u++) {
            int imageOffset = imageVerticalOffset;
            for (int v = 0; v < kw; v++) {
              float tmpIK = (srcData[imageOffset] & 0xFF) - kdata[(kernelVerticalOffset + v)];
              
              if (tmpIK < f) {
                f = tmpIK;
              }
              imageOffset += srcPixelStride;
            }
            kernelVerticalOffset += kw;
            imageVerticalOffset += srcScanlineStride;
          }
          
          if (Float.isInfinite(f)) {
            f = 0.0F;
          }
          int val = (int)f;
          
          if (val < 0) {
            val = 0;
          } else if (val > 255) {
            val = 255;
          }
          dstData[dstPixelOffset] = ((byte)val);
          srcPixelOffset += srcPixelStride;
          dstPixelOffset += dstPixelStride;
        }
        srcScanlineOffset += srcScanlineStride;
        dstScanlineOffset += dstScanlineStride;
      }
    }
  }
  

  private void shortLoop(RasterAccessor src, RasterAccessor dst)
  {
    int dwidth = dst.getWidth();
    int dheight = dst.getHeight();
    int dnumBands = dst.getNumBands();
    
    int[] dstBandOffsets = dst.getBandOffsets();
    int dstPixelStride = dst.getPixelStride();
    int dstScanlineStride = dst.getScanlineStride();
    
    int[] srcBandOffsets = src.getBandOffsets();
    int srcPixelStride = src.getPixelStride();
    int srcScanlineStride = src.getScanlineStride();
    
    short[][] dstDataArrays = dst.getShortDataArrays();
    short[][] srcDataArrays = src.getShortDataArrays();
    
    for (int k = 0; k < dnumBands; k++) {
      short[] dstData = dstDataArrays[k];
      short[] srcData = srcDataArrays[k];
      int srcScanlineOffset = srcBandOffsets[k];
      int dstScanlineOffset = dstBandOffsets[k];
      for (int j = 0; j < dheight; j++) {
        int srcPixelOffset = srcScanlineOffset;
        int dstPixelOffset = dstScanlineOffset;
        
        for (int i = 0; i < dwidth; i++) {
          int kernelVerticalOffset = 0;
          int imageVerticalOffset = srcPixelOffset;
          float f = Float.POSITIVE_INFINITY;
          for (int u = 0; u < kh; u++) {
            int imageOffset = imageVerticalOffset;
            for (int v = 0; v < kw; v++) {
              float tmpIK = srcData[imageOffset] - kdata[(kernelVerticalOffset + v)];
              
              if (tmpIK < f) {
                f = tmpIK;
              }
              imageOffset += srcPixelStride;
            }
            kernelVerticalOffset += kw;
            imageVerticalOffset += srcScanlineStride;
          }
          if (Float.isInfinite(f)) {
            f = 0.0F;
          }
          int val = (int)f;
          if (val < 32768) {
            val = 32768;
          } else if (val > 32767) {
            val = 32767;
          }
          dstData[dstPixelOffset] = ((short)val);
          srcPixelOffset += srcPixelStride;
          dstPixelOffset += dstPixelStride;
        }
        srcScanlineOffset += srcScanlineStride;
        dstScanlineOffset += dstScanlineStride;
      }
    }
  }
  

  private void ushortLoop(RasterAccessor src, RasterAccessor dst)
  {
    int dwidth = dst.getWidth();
    int dheight = dst.getHeight();
    int dnumBands = dst.getNumBands();
    
    int[] dstBandOffsets = dst.getBandOffsets();
    int dstPixelStride = dst.getPixelStride();
    int dstScanlineStride = dst.getScanlineStride();
    
    int[] srcBandOffsets = src.getBandOffsets();
    int srcPixelStride = src.getPixelStride();
    int srcScanlineStride = src.getScanlineStride();
    
    short[][] dstDataArrays = dst.getShortDataArrays();
    short[][] srcDataArrays = src.getShortDataArrays();
    
    for (int k = 0; k < dnumBands; k++) {
      short[] dstData = dstDataArrays[k];
      short[] srcData = srcDataArrays[k];
      int srcScanlineOffset = srcBandOffsets[k];
      int dstScanlineOffset = dstBandOffsets[k];
      for (int j = 0; j < dheight; j++) {
        int srcPixelOffset = srcScanlineOffset;
        int dstPixelOffset = dstScanlineOffset;
        
        for (int i = 0; i < dwidth; i++) {
          int kernelVerticalOffset = 0;
          int imageVerticalOffset = srcPixelOffset;
          float f = Float.POSITIVE_INFINITY;
          for (int u = 0; u < kh; u++) {
            int imageOffset = imageVerticalOffset;
            for (int v = 0; v < kw; v++) {
              float tmpIK = (srcData[imageOffset] & 0xFFFF) - kdata[(kernelVerticalOffset + v)];
              
              if (tmpIK < f) {
                f = tmpIK;
              }
              imageOffset += srcPixelStride;
            }
            kernelVerticalOffset += kw;
            imageVerticalOffset += srcScanlineStride;
          }
          if (Float.isInfinite(f)) {
            f = 0.0F;
          }
          int val = (int)f;
          if (val < 0) {
            val = 0;
          } else if (val > 65535) {
            val = 65535;
          }
          dstData[dstPixelOffset] = ((short)val);
          srcPixelOffset += srcPixelStride;
          dstPixelOffset += dstPixelStride;
        }
        srcScanlineOffset += srcScanlineStride;
        dstScanlineOffset += dstScanlineStride;
      }
    }
  }
  
  private void intLoop(RasterAccessor src, RasterAccessor dst)
  {
    int dwidth = dst.getWidth();
    int dheight = dst.getHeight();
    int dnumBands = dst.getNumBands();
    
    int[] dstBandOffsets = dst.getBandOffsets();
    int dstPixelStride = dst.getPixelStride();
    int dstScanlineStride = dst.getScanlineStride();
    
    int[] srcBandOffsets = src.getBandOffsets();
    int srcPixelStride = src.getPixelStride();
    int srcScanlineStride = src.getScanlineStride();
    
    int[][] dstDataArrays = dst.getIntDataArrays();
    int[][] srcDataArrays = src.getIntDataArrays();
    
    for (int k = 0; k < dnumBands; k++) {
      int[] dstData = dstDataArrays[k];
      int[] srcData = srcDataArrays[k];
      int srcScanlineOffset = srcBandOffsets[k];
      int dstScanlineOffset = dstBandOffsets[k];
      for (int j = 0; j < dheight; j++) {
        int srcPixelOffset = srcScanlineOffset;
        int dstPixelOffset = dstScanlineOffset;
        
        for (int i = 0; i < dwidth; i++) {
          int kernelVerticalOffset = 0;
          int imageVerticalOffset = srcPixelOffset;
          float f = Float.POSITIVE_INFINITY;
          for (int u = 0; u < kh; u++) {
            int imageOffset = imageVerticalOffset;
            for (int v = 0; v < kw; v++) {
              float tmpIK = srcData[imageOffset] - kdata[(kernelVerticalOffset + v)];
              
              if (tmpIK < f) {
                f = tmpIK;
              }
              imageOffset += srcPixelStride;
            }
            kernelVerticalOffset += kw;
            imageVerticalOffset += srcScanlineStride;
          }
          if (Float.isInfinite(f)) {
            f = 0.0F;
          }
          dstData[dstPixelOffset] = ((int)f);
          srcPixelOffset += srcPixelStride;
          dstPixelOffset += dstPixelStride;
        }
        srcScanlineOffset += srcScanlineStride;
        dstScanlineOffset += dstScanlineStride;
      }
    }
  }
  
  private void floatLoop(RasterAccessor src, RasterAccessor dst)
  {
    int dwidth = dst.getWidth();
    int dheight = dst.getHeight();
    int dnumBands = dst.getNumBands();
    
    int[] dstBandOffsets = dst.getBandOffsets();
    int dstPixelStride = dst.getPixelStride();
    int dstScanlineStride = dst.getScanlineStride();
    
    int[] srcBandOffsets = src.getBandOffsets();
    int srcPixelStride = src.getPixelStride();
    int srcScanlineStride = src.getScanlineStride();
    
    float[][] dstDataArrays = dst.getFloatDataArrays();
    float[][] srcDataArrays = src.getFloatDataArrays();
    
    for (int k = 0; k < dnumBands; k++) {
      float[] dstData = dstDataArrays[k];
      float[] srcData = srcDataArrays[k];
      int srcScanlineOffset = srcBandOffsets[k];
      int dstScanlineOffset = dstBandOffsets[k];
      for (int j = 0; j < dheight; j++) {
        int srcPixelOffset = srcScanlineOffset;
        int dstPixelOffset = dstScanlineOffset;
        
        for (int i = 0; i < dwidth; i++) {
          int kernelVerticalOffset = 0;
          int imageVerticalOffset = srcPixelOffset;
          float f = Float.POSITIVE_INFINITY;
          for (int u = 0; u < kh; u++) {
            int imageOffset = imageVerticalOffset;
            for (int v = 0; v < kw; v++) {
              float tmpIK = srcData[imageOffset] - kdata[(kernelVerticalOffset + v)];
              
              if (tmpIK < f) {
                f = tmpIK;
              }
              imageOffset += srcPixelStride;
            }
            kernelVerticalOffset += kw;
            imageVerticalOffset += srcScanlineStride;
          }
          if (Float.isInfinite(f)) {
            f = 0.0F;
          }
          dstData[dstPixelOffset] = f;
          srcPixelOffset += srcPixelStride;
          dstPixelOffset += dstPixelStride;
        }
        srcScanlineOffset += srcScanlineStride;
        dstScanlineOffset += dstScanlineStride;
      }
    }
  }
  

  private void doubleLoop(RasterAccessor src, RasterAccessor dst)
  {
    int dwidth = dst.getWidth();
    int dheight = dst.getHeight();
    int dnumBands = dst.getNumBands();
    
    int[] dstBandOffsets = dst.getBandOffsets();
    int dstPixelStride = dst.getPixelStride();
    int dstScanlineStride = dst.getScanlineStride();
    
    int[] srcBandOffsets = src.getBandOffsets();
    int srcPixelStride = src.getPixelStride();
    int srcScanlineStride = src.getScanlineStride();
    
    double[][] dstDataArrays = dst.getDoubleDataArrays();
    double[][] srcDataArrays = src.getDoubleDataArrays();
    
    for (int k = 0; k < dnumBands; k++) {
      double[] dstData = dstDataArrays[k];
      double[] srcData = srcDataArrays[k];
      int srcScanlineOffset = srcBandOffsets[k];
      int dstScanlineOffset = dstBandOffsets[k];
      for (int j = 0; j < dheight; j++) {
        int srcPixelOffset = srcScanlineOffset;
        int dstPixelOffset = dstScanlineOffset;
        
        for (int i = 0; i < dwidth; i++) {
          int kernelVerticalOffset = 0;
          int imageVerticalOffset = srcPixelOffset;
          double f = Double.POSITIVE_INFINITY;
          for (int u = 0; u < kh; u++) {
            int imageOffset = imageVerticalOffset;
            for (int v = 0; v < kw; v++) {
              double tmpIK = srcData[imageOffset] - kdata[(kernelVerticalOffset + v)];
              
              if (tmpIK < f) {
                f = tmpIK;
              }
              imageOffset += srcPixelStride;
            }
            kernelVerticalOffset += kw;
            imageVerticalOffset += srcScanlineStride;
          }
          
          if (Double.isInfinite(f)) {
            f = 0.0D;
          }
          dstData[dstPixelOffset] = f;
          srcPixelOffset += srcPixelStride;
          dstPixelOffset += dstPixelStride;
        }
        srcScanlineOffset += srcScanlineStride;
        dstScanlineOffset += dstScanlineStride;
      }
    }
  }
}
