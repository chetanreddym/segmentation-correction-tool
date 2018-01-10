package com.sun.media.jai.opimage;

import java.awt.image.RenderedImage;
import java.util.Map;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.RasterAccessor;
import javax.media.jai.operator.MedianFilterDescriptor;









































final class MedianFilterSeparableOpImage
  extends MedianFilterOpImage
{
  public MedianFilterSeparableOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, int maskSize)
  {
    super(source, extender, config, layout, MedianFilterDescriptor.MEDIAN_MASK_SQUARE, maskSize);
  }
  






  protected void byteLoop(RasterAccessor src, RasterAccessor dst, int filterSize)
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
    
    int[] medianValues = new int[filterSize];
    int[] tmpValues = new int[filterSize];
    int wp = filterSize;
    int[] tmpBuffer = new int[filterSize * dwidth];
    int tmpBufferSize = filterSize * dwidth;
    
    for (int k = 0; k < dnumBands; k++) {
      byte[] dstData = dstDataArrays[k];
      byte[] srcData = srcDataArrays[k];
      int srcScanlineOffset = srcBandOffsets[k];
      int dstScanlineOffset = dstBandOffsets[k];
      
      int revolver = 0;
      for (int j = 0; j < filterSize - 1; j++) {
        int srcPixelOffset = srcScanlineOffset;
        
        for (int i = 0; i < dwidth; i++) {
          int imageOffset = srcPixelOffset;
          for (int v = 0; v < wp; v++) {
            srcData[imageOffset] &= 0xFF;
            imageOffset += srcPixelStride;
          }
          tmpBuffer[(revolver + i)] = medianFilter(tmpValues);
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
          for (int v = 0; v < wp; v++) {
            srcData[imageOffset] &= 0xFF;
            imageOffset += srcPixelStride;
          }
          tmpBuffer[(revolver + i)] = medianFilter(tmpValues);
          
          int a = 0;
          for (int b = i; b < tmpBufferSize; b += dwidth) {
            medianValues[(a++)] = tmpBuffer[b];
          }
          int val = medianFilter(medianValues);
          
          dstData[dstPixelOffset] = ((byte)val);
          srcPixelOffset += srcPixelStride;
          dstPixelOffset += dstPixelStride;
        }
        
        revolver += dwidth;
        if (revolver == tmpBufferSize) {
          revolver = 0;
        }
        
        srcScanlineOffset += srcScanlineStride;
        dstScanlineOffset += dstScanlineStride;
      }
    }
  }
  

  protected void shortLoop(RasterAccessor src, RasterAccessor dst, int filterSize)
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
    
    int[] medianValues = new int[filterSize];
    int[] tmpValues = new int[filterSize];
    int wp = filterSize;
    int[] tmpBuffer = new int[filterSize * dwidth];
    int tmpBufferSize = filterSize * dwidth;
    
    for (int k = 0; k < dnumBands; k++) {
      short[] dstData = dstDataArrays[k];
      short[] srcData = srcDataArrays[k];
      int srcScanlineOffset = srcBandOffsets[k];
      int dstScanlineOffset = dstBandOffsets[k];
      
      int revolver = 0;
      for (int j = 0; j < filterSize - 1; j++) {
        int srcPixelOffset = srcScanlineOffset;
        
        for (int i = 0; i < dwidth; i++) {
          int imageOffset = srcPixelOffset;
          for (int v = 0; v < wp; v++) {
            tmpValues[v] = srcData[imageOffset];
            imageOffset += srcPixelStride;
          }
          tmpBuffer[(revolver + i)] = medianFilter(tmpValues);
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
          for (int v = 0; v < wp; v++) {
            tmpValues[v] = srcData[imageOffset];
            imageOffset += srcPixelStride;
          }
          tmpBuffer[(revolver + i)] = medianFilter(tmpValues);
          
          int a = 0;
          for (int b = i; b < tmpBufferSize; b += dwidth) {
            medianValues[(a++)] = tmpBuffer[b];
          }
          int val = medianFilter(medianValues);
          
          dstData[dstPixelOffset] = ((short)val);
          srcPixelOffset += srcPixelStride;
          dstPixelOffset += dstPixelStride;
        }
        revolver += dwidth;
        if (revolver == tmpBufferSize) {
          revolver = 0;
        }
        srcScanlineOffset += srcScanlineStride;
        dstScanlineOffset += dstScanlineStride;
      }
    }
  }
  



  protected void ushortLoop(RasterAccessor src, RasterAccessor dst, int filterSize)
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
    
    int[] medianValues = new int[filterSize];
    int[] tmpValues = new int[filterSize];
    int wp = filterSize;
    int[] tmpBuffer = new int[filterSize * dwidth];
    int tmpBufferSize = filterSize * dwidth;
    
    for (int k = 0; k < dnumBands; k++) {
      short[] dstData = dstDataArrays[k];
      short[] srcData = srcDataArrays[k];
      int srcScanlineOffset = srcBandOffsets[k];
      int dstScanlineOffset = dstBandOffsets[k];
      
      int revolver = 0;
      for (int j = 0; j < filterSize - 1; j++) {
        int srcPixelOffset = srcScanlineOffset;
        
        for (int i = 0; i < dwidth; i++) {
          int imageOffset = srcPixelOffset;
          for (int v = 0; v < wp; v++) {
            srcData[imageOffset] &= 0xFFF;
            imageOffset += srcPixelStride;
          }
          tmpBuffer[(revolver + i)] = medianFilter(tmpValues);
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
          for (int v = 0; v < wp; v++) {
            srcData[imageOffset] &= 0xFFFF;
            imageOffset += srcPixelStride;
          }
          tmpBuffer[(revolver + i)] = medianFilter(tmpValues);
          
          int a = 0;
          for (int b = i; b < tmpBufferSize; b += dwidth) {
            medianValues[(a++)] = tmpBuffer[b];
          }
          int val = medianFilter(medianValues);
          
          dstData[dstPixelOffset] = ((short)val);
          srcPixelOffset += srcPixelStride;
          dstPixelOffset += dstPixelStride;
        }
        revolver += dwidth;
        if (revolver == tmpBufferSize) {
          revolver = 0;
        }
        srcScanlineOffset += srcScanlineStride;
        dstScanlineOffset += dstScanlineStride;
      }
    }
  }
  

  protected void intLoop(RasterAccessor src, RasterAccessor dst, int filterSize)
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
    
    int[] medianValues = new int[filterSize];
    int[] tmpValues = new int[filterSize];
    int wp = filterSize;
    int[] tmpBuffer = new int[filterSize * dwidth];
    int tmpBufferSize = filterSize * dwidth;
    
    for (int k = 0; k < dnumBands; k++) {
      int[] dstData = dstDataArrays[k];
      int[] srcData = srcDataArrays[k];
      int srcScanlineOffset = srcBandOffsets[k];
      int dstScanlineOffset = dstBandOffsets[k];
      
      int revolver = 0;
      for (int j = 0; j < filterSize - 1; j++) {
        int srcPixelOffset = srcScanlineOffset;
        
        for (int i = 0; i < dwidth; i++) {
          int imageOffset = srcPixelOffset;
          for (int v = 0; v < wp; v++) {
            tmpValues[v] = srcData[imageOffset];
            imageOffset += srcPixelStride;
          }
          tmpBuffer[(revolver + i)] = medianFilter(tmpValues);
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
          for (int v = 0; v < wp; v++) {
            tmpValues[v] = srcData[imageOffset];
            imageOffset += srcPixelStride;
          }
          tmpBuffer[(revolver + i)] = medianFilter(tmpValues);
          
          int a = 0;
          for (int b = i; b < tmpBufferSize; b += dwidth) {
            medianValues[(a++)] = tmpBuffer[b];
          }
          int val = medianFilter(medianValues);
          
          dstData[dstPixelOffset] = val;
          srcPixelOffset += srcPixelStride;
          dstPixelOffset += dstPixelStride;
        }
        revolver += dwidth;
        if (revolver == tmpBufferSize) {
          revolver = 0;
        }
        srcScanlineOffset += srcScanlineStride;
        dstScanlineOffset += dstScanlineStride;
      }
    }
  }
  


  protected void floatLoop(RasterAccessor src, RasterAccessor dst, int filterSize)
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
    
    float[] medianValues = new float[filterSize];
    float[] tmpValues = new float[filterSize];
    int wp = filterSize;
    float[] tmpBuffer = new float[filterSize * dwidth];
    int tmpBufferSize = filterSize * dwidth;
    
    for (int k = 0; k < dnumBands; k++) {
      float[] dstData = dstDataArrays[k];
      float[] srcData = srcDataArrays[k];
      int srcScanlineOffset = srcBandOffsets[k];
      int dstScanlineOffset = dstBandOffsets[k];
      
      int revolver = 0;
      for (int j = 0; j < filterSize - 1; j++) {
        int srcPixelOffset = srcScanlineOffset;
        
        for (int i = 0; i < dwidth; i++) {
          int imageOffset = srcPixelOffset;
          for (int v = 0; v < wp; v++) {
            tmpValues[v] = srcData[imageOffset];
            imageOffset += srcPixelStride;
          }
          tmpBuffer[(revolver + i)] = medianFilterFloat(tmpValues);
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
          for (int v = 0; v < wp; v++) {
            tmpValues[v] = srcData[imageOffset];
            imageOffset += srcPixelStride;
          }
          tmpBuffer[(revolver + i)] = medianFilterFloat(tmpValues);
          
          int a = 0;
          for (int b = i; b < tmpBufferSize; b += dwidth) {
            medianValues[(a++)] = tmpBuffer[b];
          }
          float val = medianFilterFloat(medianValues);
          
          dstData[dstPixelOffset] = val;
          srcPixelOffset += srcPixelStride;
          dstPixelOffset += dstPixelStride;
        }
        revolver += dwidth;
        if (revolver == tmpBufferSize) {
          revolver = 0;
        }
        srcScanlineOffset += srcScanlineStride;
        dstScanlineOffset += dstScanlineStride;
      }
    }
  }
  

  protected void doubleLoop(RasterAccessor src, RasterAccessor dst, int filterSize)
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
    
    double[] medianValues = new double[filterSize];
    double[] tmpValues = new double[filterSize];
    int wp = filterSize;
    double[] tmpBuffer = new double[filterSize * dwidth];
    int tmpBufferSize = filterSize * dwidth;
    
    for (int k = 0; k < dnumBands; k++) {
      double[] dstData = dstDataArrays[k];
      double[] srcData = srcDataArrays[k];
      int srcScanlineOffset = srcBandOffsets[k];
      int dstScanlineOffset = dstBandOffsets[k];
      
      int revolver = 0;
      for (int j = 0; j < filterSize - 1; j++) {
        int srcPixelOffset = srcScanlineOffset;
        
        for (int i = 0; i < dwidth; i++) {
          int imageOffset = srcPixelOffset;
          for (int v = 0; v < wp; v++) {
            tmpValues[v] = srcData[imageOffset];
            imageOffset += srcPixelStride;
          }
          tmpBuffer[(revolver + i)] = medianFilterDouble(tmpValues);
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
          for (int v = 0; v < wp; v++) {
            tmpValues[v] = srcData[imageOffset];
            imageOffset += srcPixelStride;
          }
          tmpBuffer[(revolver + i)] = medianFilterDouble(tmpValues);
          
          int a = 0;
          for (int b = i; b < tmpBufferSize; b += dwidth) {
            medianValues[(a++)] = tmpBuffer[b];
          }
          double val = medianFilterDouble(medianValues);
          
          dstData[dstPixelOffset] = val;
          srcPixelOffset += srcPixelStride;
          dstPixelOffset += dstPixelStride;
        }
        revolver += dwidth;
        if (revolver == tmpBufferSize) {
          revolver = 0;
        }
        srcScanlineOffset += srcScanlineStride;
        dstScanlineOffset += dstScanlineStride;
      }
    }
  }
}