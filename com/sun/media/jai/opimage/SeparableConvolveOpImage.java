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



















final class SeparableConvolveOpImage
  extends AreaOpImage
{
  static int byteLoopCounter = 0;
  

  protected KernelJAI kernel;
  

  protected int kw;
  

  protected int kh;
  

  protected int kx;
  

  protected int ky;
  

  protected float[] hValues;
  
  protected float[] vValues;
  
  protected float[][] hTables;
  

  public SeparableConvolveOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, KernelJAI kernel)
  {
    super(source, layout, config, true, extender, kernel.getLeftPadding(), kernel.getRightPadding(), kernel.getTopPadding(), kernel.getBottomPadding());
    








    this.kernel = kernel;
    kw = kernel.getWidth();
    kh = kernel.getHeight();
    kx = kernel.getXOrigin();
    ky = kernel.getYOrigin();
    hValues = kernel.getHorizontalKernelData();
    vValues = kernel.getVerticalKernelData();
    
    if (sampleModel.getDataType() == 0) {
      hTables = new float[hValues.length]['Ā'];
      for (int i = 0; i < hValues.length; i++) {
        float k = hValues[i];
        for (int j = 0; j < 256; j++) {
          byte b = (byte)j;
          float f = j;
          hTables[i][(b + 128)] = (k * f);
        }
      }
    }
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
  
  protected void byteLoop(RasterAccessor src, RasterAccessor dst)
  {
    int dwidth = dst.getWidth();
    int dheight = dst.getHeight();
    int dnumBands = dst.getNumBands();
    
    byte[][] dstDataArrays = dst.getByteDataArrays();
    int[] dstBandOffsets = dst.getBandOffsets();
    int dstPixelStride = dst.getPixelStride();
    int dstScanlineStride = dst.getScanlineStride();
    
    byte[][] srcDataArrays = src.getByteDataArrays();
    int[] srcBandOffsets = src.getBandOffsets();
    int srcPixelStride = src.getPixelStride();
    int srcScanlineStride = src.getScanlineStride();
    
    float[] tmpBuffer = new float[kh * dwidth];
    int tmpBufferSize = kh * dwidth;
    
    for (int k = 0; k < dnumBands; k++) {
      byte[] dstData = dstDataArrays[k];
      byte[] srcData = srcDataArrays[k];
      int srcScanlineOffset = srcBandOffsets[k];
      int dstScanlineOffset = dstBandOffsets[k];
      
      int revolver = 0;
      int kvRevolver = 0;
      for (int j = 0; j < kh - 1; j++) {
        int srcPixelOffset = srcScanlineOffset;
        
        for (int i = 0; i < dwidth; i++) {
          int imageOffset = srcPixelOffset;
          float f = 0.0F;
          for (int v = 0; v < kw; v++) {
            f += hTables[v][(srcData[imageOffset] + 128)];
            imageOffset += srcPixelStride;
          }
          tmpBuffer[(revolver + i)] = f;
          srcPixelOffset += srcPixelStride;
        }
        revolver += dwidth;
        srcScanlineOffset += srcScanlineStride;
      }
      


      for (int j = 0; j < dheight; j++) {
        int srcPixelOffset = srcScanlineOffset;
        int dstPixelOffset = dstScanlineOffset;
        
        for (int i = 0; i < dwidth; i++) {
          int imageOffset = srcPixelOffset;
          float f = 0.0F;
          for (int v = 0; v < kw; v++) {
            f += hTables[v][(srcData[imageOffset] + 128)];
            imageOffset += srcPixelStride;
          }
          tmpBuffer[(revolver + i)] = f;
          
          f = 0.5F;
          

          int b = kvRevolver + i;
          for (int a = 0; a < kh; a++) {
            f += tmpBuffer[b] * vValues[a];
            b += dwidth;
            if (b >= tmpBufferSize) { b -= tmpBufferSize;
            }
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
        
        revolver += dwidth;
        if (revolver == tmpBufferSize) {
          revolver = 0;
        }
        kvRevolver += dwidth;
        if (kvRevolver == tmpBufferSize) {
          kvRevolver = 0;
        }
        srcScanlineOffset += srcScanlineStride;
        dstScanlineOffset += dstScanlineStride;
      }
    }
  }
  


  protected void shortLoop(RasterAccessor src, RasterAccessor dst)
  {
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
    
    float[] tmpBuffer = new float[kh * dwidth];
    int tmpBufferSize = kh * dwidth;
    
    for (int k = 0; k < dnumBands; k++) {
      short[] dstData = dstDataArrays[k];
      short[] srcData = srcDataArrays[k];
      int srcScanlineOffset = srcBandOffsets[k];
      int dstScanlineOffset = dstBandOffsets[k];
      
      int revolver = 0;
      int kvRevolver = 0;
      for (int j = 0; j < kh - 1; j++) {
        int srcPixelOffset = srcScanlineOffset;
        
        for (int i = 0; i < dwidth; i++) {
          int imageOffset = srcPixelOffset;
          float f = 0.0F;
          for (int v = 0; v < kw; v++) {
            f += srcData[imageOffset] * hValues[v];
            imageOffset += srcPixelStride;
          }
          tmpBuffer[(revolver + i)] = f;
          srcPixelOffset += srcPixelStride;
        }
        revolver += dwidth;
        srcScanlineOffset += srcScanlineStride;
      }
      



      for (int j = 0; j < dheight; j++) {
        int srcPixelOffset = srcScanlineOffset;
        int dstPixelOffset = dstScanlineOffset;
        
        for (int i = 0; i < dwidth; i++) {
          int imageOffset = srcPixelOffset;
          float f = 0.0F;
          for (int v = 0; v < kw; v++) {
            f += srcData[imageOffset] * hValues[v];
            imageOffset += srcPixelStride;
          }
          tmpBuffer[(revolver + i)] = f;
          
          f = 0.5F;
          int b = kvRevolver + i;
          for (int a = 0; a < kh; a++) {
            f += tmpBuffer[b] * vValues[a];
            b += dwidth;
            if (b >= tmpBufferSize) { b -= tmpBufferSize;
            }
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
        revolver += dwidth;
        
        if (revolver == tmpBufferSize) {
          revolver = 0;
        }
        kvRevolver += dwidth;
        if (kvRevolver == tmpBufferSize) {
          kvRevolver = 0;
        }
        
        srcScanlineOffset += srcScanlineStride;
        dstScanlineOffset += dstScanlineStride;
      }
    }
  }
  


  protected void ushortLoop(RasterAccessor src, RasterAccessor dst)
  {
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
    float[] tmpBuffer = new float[kh * dwidth];
    int tmpBufferSize = kh * dwidth;
    
    for (int k = 0; k < dnumBands; k++) {
      short[] dstData = dstDataArrays[k];
      short[] srcData = srcDataArrays[k];
      int srcScanlineOffset = srcBandOffsets[k];
      int dstScanlineOffset = dstBandOffsets[k];
      
      int revolver = 0;
      int kvRevolver = 0;
      for (int j = 0; j < kh - 1; j++) {
        int srcPixelOffset = srcScanlineOffset;
        
        for (int i = 0; i < dwidth; i++) {
          int imageOffset = srcPixelOffset;
          float f = 0.0F;
          for (int v = 0; v < kw; v++) {
            f += (srcData[imageOffset] & 0xFFFF) * hValues[v];
            imageOffset += srcPixelStride;
          }
          tmpBuffer[(revolver + i)] = f;
          srcPixelOffset += srcPixelStride;
        }
        revolver += dwidth;
        srcScanlineOffset += srcScanlineStride;
      }
      



      for (int j = 0; j < dheight; j++) {
        int srcPixelOffset = srcScanlineOffset;
        int dstPixelOffset = dstScanlineOffset;
        
        for (int i = 0; i < dwidth; i++) {
          int imageOffset = srcPixelOffset;
          float f = 0.0F;
          for (int v = 0; v < kw; v++) {
            f += (srcData[imageOffset] & 0xFFFF) * hValues[v];
            imageOffset += srcPixelStride;
          }
          tmpBuffer[(revolver + i)] = f;
          
          f = 0.5F;
          
          int b = kvRevolver + i;
          for (int a = 0; a < kh; a++) {
            f += tmpBuffer[b] * vValues[a];
            b += dwidth;
            if (b >= tmpBufferSize) { b -= tmpBufferSize;
            }
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
        revolver += dwidth;
        if (revolver == tmpBufferSize) {
          revolver = 0;
        }
        kvRevolver += dwidth;
        if (kvRevolver == tmpBufferSize) {
          kvRevolver = 0;
        }
        srcScanlineOffset += srcScanlineStride;
        dstScanlineOffset += dstScanlineStride;
      }
    }
  }
  
  protected void intLoop(RasterAccessor src, RasterAccessor dst)
  {
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
    
    float[] tmpBuffer = new float[kh * dwidth];
    int tmpBufferSize = kh * dwidth;
    
    for (int k = 0; k < dnumBands; k++) {
      int[] dstData = dstDataArrays[k];
      int[] srcData = srcDataArrays[k];
      int srcScanlineOffset = srcBandOffsets[k];
      int dstScanlineOffset = dstBandOffsets[k];
      
      int revolver = 0;
      int kvRevolver = 0;
      for (int j = 0; j < kh - 1; j++) {
        int srcPixelOffset = srcScanlineOffset;
        
        for (int i = 0; i < dwidth; i++) {
          int imageOffset = srcPixelOffset;
          float f = 0.0F;
          for (int v = 0; v < kw; v++) {
            f += srcData[imageOffset] * hValues[v];
            imageOffset += srcPixelStride;
          }
          tmpBuffer[(revolver + i)] = f;
          srcPixelOffset += srcPixelStride;
        }
        revolver += dwidth;
        srcScanlineOffset += srcScanlineStride;
      }
      


      for (int j = 0; j < dheight; j++) {
        int srcPixelOffset = srcScanlineOffset;
        int dstPixelOffset = dstScanlineOffset;
        
        for (int i = 0; i < dwidth; i++) {
          int imageOffset = srcPixelOffset;
          float f = 0.0F;
          for (int v = 0; v < kw; v++) {
            f += srcData[imageOffset] * hValues[v];
            imageOffset += srcPixelStride;
          }
          tmpBuffer[(revolver + i)] = f;
          
          f = 0.5F;
          
          int b = kvRevolver + i;
          for (int a = 0; a < kh; a++) {
            f += tmpBuffer[b] * vValues[a];
            b += dwidth;
            if (b >= tmpBufferSize) { b -= tmpBufferSize;
            }
          }
          int val = (int)f;
          
          dstData[dstPixelOffset] = val;
          srcPixelOffset += srcPixelStride;
          dstPixelOffset += dstPixelStride;
        }
        revolver += dwidth;
        if (revolver == tmpBufferSize) {
          revolver = 0;
        }
        kvRevolver += dwidth;
        if (kvRevolver == tmpBufferSize) {
          kvRevolver = 0;
        }
        srcScanlineOffset += srcScanlineStride;
        dstScanlineOffset += dstScanlineStride;
      }
    }
  }
  

  protected void floatLoop(RasterAccessor src, RasterAccessor dst)
  {
    int dwidth = dst.getWidth();
    int dheight = dst.getHeight();
    int dnumBands = dst.getNumBands();
    
    float[][] dstDataArrays = dst.getFloatDataArrays();
    int[] dstBandOffsets = dst.getBandOffsets();
    int dstPixelStride = dst.getPixelStride();
    int dstScanlineStride = dst.getScanlineStride();
    
    float[][] srcDataArrays = src.getFloatDataArrays();
    int[] srcBandOffsets = src.getBandOffsets();
    int srcPixelStride = src.getPixelStride();
    int srcScanlineStride = src.getScanlineStride();
    
    float[] tmpBuffer = new float[kh * dwidth];
    int tmpBufferSize = kh * dwidth;
    
    for (int k = 0; k < dnumBands; k++) {
      float[] dstData = dstDataArrays[k];
      float[] srcData = srcDataArrays[k];
      int srcScanlineOffset = srcBandOffsets[k];
      int dstScanlineOffset = dstBandOffsets[k];
      
      int revolver = 0;
      int kvRevolver = 0;
      for (int j = 0; j < kh - 1; j++) {
        int srcPixelOffset = srcScanlineOffset;
        
        for (int i = 0; i < dwidth; i++) {
          int imageOffset = srcPixelOffset;
          float f = 0.0F;
          for (int v = 0; v < kw; v++) {
            f += srcData[imageOffset] * hValues[v];
            imageOffset += srcPixelStride;
          }
          tmpBuffer[(revolver + i)] = f;
          srcPixelOffset += srcPixelStride;
        }
        revolver += dwidth;
        srcScanlineOffset += srcScanlineStride;
      }
      



      for (int j = 0; j < dheight; j++) {
        int srcPixelOffset = srcScanlineOffset;
        int dstPixelOffset = dstScanlineOffset;
        
        for (int i = 0; i < dwidth; i++) {
          int imageOffset = srcPixelOffset;
          float f = 0.0F;
          for (int v = 0; v < kw; v++) {
            f += srcData[imageOffset] * hValues[v];
            imageOffset += srcPixelStride;
          }
          tmpBuffer[(revolver + i)] = f;
          
          f = 0.0F;
          
          int b = kvRevolver + i;
          for (int a = 0; a < kh; a++) {
            f += tmpBuffer[b] * vValues[a];
            b += dwidth;
            if (b >= tmpBufferSize) { b -= tmpBufferSize;
            }
          }
          dstData[dstPixelOffset] = f;
          srcPixelOffset += srcPixelStride;
          dstPixelOffset += dstPixelStride;
        }
        revolver += dwidth;
        if (revolver == tmpBufferSize) {
          revolver = 0;
        }
        kvRevolver += dwidth;
        if (kvRevolver == tmpBufferSize) {
          kvRevolver = 0;
        }
        srcScanlineOffset += srcScanlineStride;
        dstScanlineOffset += dstScanlineStride;
      }
    }
  }
  
  protected void doubleLoop(RasterAccessor src, RasterAccessor dst)
  {
    int dwidth = dst.getWidth();
    int dheight = dst.getHeight();
    int dnumBands = dst.getNumBands();
    
    double[][] dstDataArrays = dst.getDoubleDataArrays();
    int[] dstBandOffsets = dst.getBandOffsets();
    int dstPixelStride = dst.getPixelStride();
    int dstScanlineStride = dst.getScanlineStride();
    
    double[][] srcDataArrays = src.getDoubleDataArrays();
    int[] srcBandOffsets = src.getBandOffsets();
    int srcPixelStride = src.getPixelStride();
    int srcScanlineStride = src.getScanlineStride();
    
    double[] tmpBuffer = new double[kh * dwidth];
    int tmpBufferSize = kh * dwidth;
    
    for (int k = 0; k < dnumBands; k++) {
      double[] dstData = dstDataArrays[k];
      double[] srcData = srcDataArrays[k];
      int srcScanlineOffset = srcBandOffsets[k];
      int dstScanlineOffset = dstBandOffsets[k];
      
      int revolver = 0;
      int kvRevolver = 0;
      for (int j = 0; j < kh - 1; j++) {
        int srcPixelOffset = srcScanlineOffset;
        
        for (int i = 0; i < dwidth; i++) {
          int imageOffset = srcPixelOffset;
          double f = 0.0D;
          for (int v = 0; v < kw; v++) {
            f += srcData[imageOffset] * hValues[v];
            imageOffset += srcPixelStride;
          }
          tmpBuffer[(revolver + i)] = f;
          srcPixelOffset += srcPixelStride;
        }
        revolver += dwidth;
        srcScanlineOffset += srcScanlineStride;
      }
      



      for (int j = 0; j < dheight; j++) {
        int srcPixelOffset = srcScanlineOffset;
        int dstPixelOffset = dstScanlineOffset;
        
        for (int i = 0; i < dwidth; i++) {
          int imageOffset = srcPixelOffset;
          double f = 0.0D;
          for (int v = 0; v < kw; v++) {
            f += srcData[imageOffset] * hValues[v];
            imageOffset += srcPixelStride;
          }
          tmpBuffer[(revolver + i)] = f;
          
          f = 0.0D;
          
          int b = kvRevolver + i;
          for (int a = 0; a < kh; a++) {
            f += tmpBuffer[b] * vValues[a];
            b += dwidth;
            if (b >= tmpBufferSize) { b -= tmpBufferSize;
            }
          }
          dstData[dstPixelOffset] = f;
          srcPixelOffset += srcPixelStride;
          dstPixelOffset += dstPixelStride;
        }
        revolver += dwidth;
        if (revolver == tmpBufferSize) {
          revolver = 0;
        }
        kvRevolver += dwidth;
        if (kvRevolver == tmpBufferSize) {
          kvRevolver = 0;
        }
        srcScanlineOffset += srcScanlineStride;
        dstScanlineOffset += dstScanlineStride;
      }
    }
  }
}
