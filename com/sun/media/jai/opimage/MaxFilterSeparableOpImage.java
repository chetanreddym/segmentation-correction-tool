package com.sun.media.jai.opimage;

import java.awt.image.RenderedImage;
import java.util.Map;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.RasterAccessor;
import javax.media.jai.operator.MaxFilterDescriptor;








































final class MaxFilterSeparableOpImage
  extends MaxFilterOpImage
{
  public MaxFilterSeparableOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, int maskSize)
  {
    super(source, extender, config, layout, MaxFilterDescriptor.MAX_MASK_SQUARE, maskSize);
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
    
    int[] maxValues = new int[filterSize];
    
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
          int maxval = Integer.MIN_VALUE;
          for (int v = 0; v < wp; v++) {
            int val = srcData[imageOffset] & 0xFF;
            imageOffset += srcPixelStride;
            maxval = val > maxval ? val : maxval;
          }
          tmpBuffer[(revolver + i)] = maxval;
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
          int maxval = Integer.MIN_VALUE;
          for (int v = 0; v < wp; v++) {
            int val = srcData[imageOffset] & 0xFF;
            imageOffset += srcPixelStride;
            maxval = val > maxval ? val : maxval;
          }
          tmpBuffer[(revolver + i)] = maxval;
          
          maxval = Integer.MIN_VALUE;
          for (int b = i; b < tmpBufferSize; b += dwidth) {
            int val = tmpBuffer[b];
            maxval = val > maxval ? val : maxval;
          }
          
          dstData[dstPixelOffset] = ((byte)maxval);
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
    
    int[] maxValues = new int[filterSize];
    
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
          int maxval = Integer.MIN_VALUE;
          for (int v = 0; v < wp; v++) {
            int val = srcData[imageOffset];
            imageOffset += srcPixelStride;
            maxval = val > maxval ? val : maxval;
          }
          tmpBuffer[(revolver + i)] = maxval;
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
          int maxval = Integer.MIN_VALUE;
          for (int v = 0; v < wp; v++) {
            int val = srcData[imageOffset];
            imageOffset += srcPixelStride;
            maxval = val > maxval ? val : maxval;
          }
          tmpBuffer[(revolver + i)] = maxval;
          
          maxval = Integer.MIN_VALUE;
          for (int b = i; b < tmpBufferSize; b += dwidth) {
            int val = tmpBuffer[b];
            maxval = val > maxval ? val : maxval;
          }
          
          dstData[dstPixelOffset] = ((short)maxval);
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
    
    int[] maxValues = new int[filterSize];
    
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
          int maxval = Integer.MIN_VALUE;
          for (int v = 0; v < wp; v++) {
            int val = srcData[imageOffset] & 0xFFF;
            imageOffset += srcPixelStride;
            maxval = val > maxval ? val : maxval;
          }
          tmpBuffer[(revolver + i)] = maxval;
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
          int maxval = Integer.MIN_VALUE;
          for (int v = 0; v < wp; v++) {
            int val = srcData[imageOffset] & 0xFFFF;
            imageOffset += srcPixelStride;
            maxval = val > maxval ? val : maxval;
          }
          tmpBuffer[(revolver + i)] = maxval;
          
          maxval = Integer.MIN_VALUE;
          for (int b = i; b < tmpBufferSize; b += dwidth) {
            int val = tmpBuffer[b];
            maxval = val > maxval ? val : maxval;
          }
          
          dstData[dstPixelOffset] = ((short)maxval);
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
    
    int[] maxValues = new int[filterSize];
    
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
          int maxval = Integer.MIN_VALUE;
          for (int v = 0; v < wp; v++) {
            int val = srcData[imageOffset];
            imageOffset += srcPixelStride;
            maxval = val > maxval ? val : maxval;
          }
          tmpBuffer[(revolver + i)] = maxval;
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
          int maxval = Integer.MIN_VALUE;
          for (int v = 0; v < wp; v++) {
            int val = srcData[imageOffset];
            imageOffset += srcPixelStride;
            maxval = val > maxval ? val : maxval;
          }
          tmpBuffer[(revolver + i)] = maxval;
          
          maxval = Integer.MIN_VALUE;
          for (int b = i; b < tmpBufferSize; b += dwidth) {
            int val = tmpBuffer[b];
            maxval = val > maxval ? val : maxval;
          }
          
          dstData[dstPixelOffset] = maxval;
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
    
    float[] maxValues = new float[filterSize];
    
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
          float maxval = -3.4028235E38F;
          for (int v = 0; v < wp; v++) {
            float val = srcData[imageOffset];
            imageOffset += srcPixelStride;
            maxval = val > maxval ? val : maxval;
          }
          tmpBuffer[(revolver + i)] = maxval;
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
          float maxval = -3.4028235E38F;
          for (int v = 0; v < wp; v++) {
            float val = srcData[imageOffset];
            imageOffset += srcPixelStride;
            maxval = val > maxval ? val : maxval;
          }
          tmpBuffer[(revolver + i)] = maxval;
          
          maxval = -3.4028235E38F;
          for (int b = i; b < tmpBufferSize; b += dwidth) {
            float val = tmpBuffer[b];
            maxval = val > maxval ? val : maxval;
          }
          
          dstData[dstPixelOffset] = maxval;
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
    
    double[] maxValues = new double[filterSize];
    
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
          double maxval = -1.7976931348623157E308D;
          for (int v = 0; v < wp; v++) {
            double val = srcData[imageOffset];
            imageOffset += srcPixelStride;
            maxval = val > maxval ? val : maxval;
          }
          tmpBuffer[(revolver + i)] = maxval;
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
          double maxval = -1.7976931348623157E308D;
          for (int v = 0; v < wp; v++) {
            double val = srcData[imageOffset];
            imageOffset += srcPixelStride;
            maxval = val > maxval ? val : maxval;
          }
          tmpBuffer[(revolver + i)] = maxval;
          
          maxval = -1.7976931348623157E308D;
          for (int b = i; b < tmpBufferSize; b += dwidth) {
            double val = tmpBuffer[b];
            maxval = val > maxval ? val : maxval;
          }
          
          dstData[dstPixelOffset] = maxval;
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
