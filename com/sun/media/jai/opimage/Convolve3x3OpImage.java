package com.sun.media.jai.opimage;

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
import javax.media.jai.PlanarImage;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFormatTag;











































final class Convolve3x3OpImage
  extends AreaOpImage
{
  protected KernelJAI kernel;
  float[][] tables = new float[9]['Ā'];
  



















  public Convolve3x3OpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, KernelJAI kernel)
  {
    super(source, layout, config, true, extender, kernel.getLeftPadding(), kernel.getRightPadding(), kernel.getTopPadding(), kernel.getBottomPadding());
    








    this.kernel = kernel;
    if ((kernel.getWidth() != 3) || (kernel.getHeight() != 3) || (kernel.getXOrigin() != 1) || (kernel.getYOrigin() != 1))
    {


      throw new RuntimeException(JaiI18N.getString("Convolve3x3OpImage0"));
    }
    
    if (sampleModel.getDataType() == 0) {
      float[] kdata = kernel.getKernelData();
      float k0 = kdata[0];
      float k1 = kdata[1];
      float k2 = kdata[2];
      float k3 = kdata[3];
      float k4 = kdata[4];
      float k5 = kdata[5];
      float k6 = kdata[6];
      float k7 = kdata[7];
      float k8 = kdata[8];
      
      for (int j = 0; j < 256; j++) {
        byte b = (byte)j;
        float f = j;
        tables[0][(b + 128)] = (k0 * f + 0.5F);
        tables[1][(b + 128)] = (k1 * f);
        tables[2][(b + 128)] = (k2 * f);
        tables[3][(b + 128)] = (k3 * f);
        tables[4][(b + 128)] = (k4 * f);
        tables[5][(b + 128)] = (k5 * f);
        tables[6][(b + 128)] = (k6 * f);
        tables[7][(b + 128)] = (k7 * f);
        tables[8][(b + 128)] = (k8 * f);
      }
    }
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
    case 2: 
      shortLoop(srcAccessor, dstAccessor);
      break;
    case 3: 
      intLoop(srcAccessor, dstAccessor);
      break;
    case 1: default: 
      String className = getClass().getName();
      throw new RuntimeException(JaiI18N.getString("Convolve3x3OpImage1"));
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
    

    float[] t0 = tables[0];
    float[] t1 = tables[1];
    float[] t2 = tables[2];
    float[] t3 = tables[3];
    float[] t4 = tables[4];
    float[] t5 = tables[5];
    float[] t6 = tables[6];
    float[] t7 = tables[7];
    float[] t8 = tables[8];
    
    float[] kdata = kernel.getKernelData();
    
    byte[][] dstDataArrays = dst.getByteDataArrays();
    int[] dstBandOffsets = dst.getBandOffsets();
    int dstPixelStride = dst.getPixelStride();
    int dstScanlineStride = dst.getScanlineStride();
    
    byte[][] srcDataArrays = src.getByteDataArrays();
    int[] srcBandOffsets = src.getBandOffsets();
    int srcPixelStride = src.getPixelStride();
    int srcScanlineStride = src.getScanlineStride();
    

    int centerScanlineOffset = srcScanlineStride;
    int bottomScanlineOffset = srcScanlineStride * 2;
    int middlePixelOffset = dnumBands;
    int rightPixelOffset = dnumBands * 2;
    
    for (int k = 0; k < dnumBands; k++) {
      byte[] dstData = dstDataArrays[k];
      byte[] srcData = srcDataArrays[k];
      int srcScanlineOffset = srcBandOffsets[k];
      int dstScanlineOffset = dstBandOffsets[k];
      for (int j = 0; j < dheight; j++) {
        int srcPixelOffset = srcScanlineOffset;
        int dstPixelOffset = dstScanlineOffset;
        for (int i = 0; i < dwidth; i++) {
          float f = t0[(128 + srcData[srcPixelOffset])] + t1[(128 + srcData[(srcPixelOffset + middlePixelOffset)])] + t2[(128 + srcData[(srcPixelOffset + rightPixelOffset)])] + t3[(128 + srcData[(srcPixelOffset + centerScanlineOffset)])] + t4[(128 + srcData[(srcPixelOffset + centerScanlineOffset + middlePixelOffset)])] + t5[(128 + srcData[(srcPixelOffset + centerScanlineOffset + rightPixelOffset)])] + t6[(128 + srcData[(srcPixelOffset + bottomScanlineOffset)])] + t7[(128 + srcData[(srcPixelOffset + bottomScanlineOffset + middlePixelOffset)])] + t8[(128 + srcData[(srcPixelOffset + bottomScanlineOffset + rightPixelOffset)])];
          






















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
  
  private void shortLoop(RasterAccessor src, RasterAccessor dst) {
    int dwidth = dst.getWidth();
    int dheight = dst.getHeight();
    int dnumBands = dst.getNumBands();
    
    short[][] dstDataArrays = dst.getShortDataArrays();
    int[] dstBandOffsets = dst.getBandOffsets();
    int dstPixelStride = dst.getPixelStride();
    int dstScanlineStride = dst.getScanlineStride();
    
    short[][] srcDataArrays = src.getShortDataArrays();
    int[] srcBandOffsets = src.getBandOffsets();
    int srcPixelStride = src.getPixelStride();
    int srcScanlineStride = src.getScanlineStride();
    

    int centerScanlineOffset = srcScanlineStride;
    int bottomScanlineOffset = srcScanlineStride * 2;
    int middlePixelOffset = dnumBands;
    int rightPixelOffset = dnumBands * 2;
    
    float[] kdata = kernel.getKernelData();
    float k0 = kdata[0];
    float k1 = kdata[1];
    float k2 = kdata[2];
    float k3 = kdata[3];
    float k4 = kdata[4];
    float k5 = kdata[5];
    float k6 = kdata[6];
    float k7 = kdata[7];
    float k8 = kdata[8];
    
    for (int k = 0; k < dnumBands; k++) {
      short[] dstData = dstDataArrays[k];
      short[] srcData = srcDataArrays[k];
      int srcScanlineOffset = srcBandOffsets[k];
      int dstScanlineOffset = dstBandOffsets[k];
      for (int j = 0; j < dheight; j++) {
        int srcPixelOffset = srcScanlineOffset;
        int dstPixelOffset = dstScanlineOffset;
        for (int i = 0; i < dwidth; i++) {
          float f = k0 * srcData[srcPixelOffset] + k1 * srcData[(srcPixelOffset + middlePixelOffset)] + k2 * srcData[(srcPixelOffset + rightPixelOffset)] + k3 * srcData[(srcPixelOffset + centerScanlineOffset)] + k4 * srcData[(srcPixelOffset + centerScanlineOffset + middlePixelOffset)] + k5 * srcData[(srcPixelOffset + centerScanlineOffset + rightPixelOffset)] + k6 * srcData[(srcPixelOffset + bottomScanlineOffset)] + k7 * srcData[(srcPixelOffset + bottomScanlineOffset + middlePixelOffset)] + k8 * srcData[(srcPixelOffset + bottomScanlineOffset + rightPixelOffset)];
          





















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
  
  private void intLoop(RasterAccessor src, RasterAccessor dst) {
    int dwidth = dst.getWidth();
    int dheight = dst.getHeight();
    int dnumBands = dst.getNumBands();
    
    int[][] dstDataArrays = dst.getIntDataArrays();
    int[] dstBandOffsets = dst.getBandOffsets();
    int dstPixelStride = dst.getPixelStride();
    int dstScanlineStride = dst.getScanlineStride();
    
    int[][] srcDataArrays = src.getIntDataArrays();
    int[] srcBandOffsets = src.getBandOffsets();
    int srcPixelStride = src.getPixelStride();
    int srcScanlineStride = src.getScanlineStride();
    

    int centerScanlineOffset = srcScanlineStride;
    int bottomScanlineOffset = srcScanlineStride * 2;
    int middlePixelOffset = dnumBands;
    int rightPixelOffset = dnumBands * 2;
    
    float[] kdata = kernel.getKernelData();
    float k0 = kdata[0];
    float k1 = kdata[1];
    float k2 = kdata[2];
    float k3 = kdata[3];
    float k4 = kdata[4];
    float k5 = kdata[5];
    float k6 = kdata[6];
    float k7 = kdata[7];
    float k8 = kdata[8];
    
    for (int k = 0; k < dnumBands; k++) {
      int[] dstData = dstDataArrays[k];
      int[] srcData = srcDataArrays[k];
      int srcScanlineOffset = srcBandOffsets[k];
      int dstScanlineOffset = dstBandOffsets[k];
      for (int j = 0; j < dheight; j++) {
        int srcPixelOffset = srcScanlineOffset;
        int dstPixelOffset = dstScanlineOffset;
        for (int i = 0; i < dwidth; i++) {
          float f = k0 * srcData[srcPixelOffset] + k1 * srcData[(srcPixelOffset + middlePixelOffset)] + k2 * srcData[(srcPixelOffset + rightPixelOffset)] + k3 * srcData[(srcPixelOffset + centerScanlineOffset)] + k4 * srcData[(srcPixelOffset + centerScanlineOffset + middlePixelOffset)] + k5 * srcData[(srcPixelOffset + centerScanlineOffset + rightPixelOffset)] + k6 * srcData[(srcPixelOffset + bottomScanlineOffset)] + k7 * srcData[(srcPixelOffset + bottomScanlineOffset + middlePixelOffset)] + k8 * srcData[(srcPixelOffset + bottomScanlineOffset + rightPixelOffset)];
          





















          dstData[dstPixelOffset] = ((int)f);
          srcPixelOffset += srcPixelStride;
          dstPixelOffset += dstPixelStride;
        }
        srcScanlineOffset += srcScanlineStride;
        dstScanlineOffset += dstScanlineStride;
      }
    }
  }
}
