package com.sun.media.jai.opimage;

import com.sun.media.jai.util.ImageUtil;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ColormapOpImage;
import javax.media.jai.ImageLayout;
import javax.media.jai.LookupTableJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFormatTag;




































final class PiecewiseOpImage
  extends ColormapOpImage
{
  private float[][] abscissas;
  private float[][] slopes;
  private float[][] intercepts;
  private float[] minOrdinates;
  private float[] maxOrdinates;
  private boolean isByteData = false;
  







  private LookupTableJAI lut;
  







  private static float binarySearch(float[] x, float minValue, float maxValue, float[] a, float[] b, float value)
  {
    int highIndex = x.length - 1;
    
    if (value <= x[0])
      return minValue;
    if (value >= x[highIndex]) {
      return maxValue;
    }
    
    int lowIndex = 0;
    int deltaIndex = highIndex - lowIndex;
    
    while (deltaIndex > 1) {
      int meanIndex = lowIndex + deltaIndex / 2;
      if (value >= x[meanIndex]) {
        lowIndex = meanIndex;
      } else {
        highIndex = meanIndex;
      }
      deltaIndex = highIndex - lowIndex;
    }
    
    return a[lowIndex] * value + b[lowIndex];
  }
  













  public PiecewiseOpImage(RenderedImage source, Map config, ImageLayout layout, float[][][] breakpoints)
  {
    super(source, layout, config, true);
    


    int numBands = sampleModel.getNumBands();
    

    initFields(numBands, breakpoints);
    

    isByteData = (sampleModel.getTransferType() == 0);
    

    if (isByteData)
    {
      createLUT();
      

      unsetFields();
    }
    

    permitInPlaceOperation();
    

    initializeColormapOperation();
  }
  



  protected void transformColormap(byte[][] colormap)
  {
    byte[][] byteTable = lut.getByteData();
    
    for (int b = 0; b < 3; b++) {
      byte[] map = colormap[b];
      byte[] luTable = byteTable[b];
      int mapSize = map.length;
      
      for (int i = 0; i < mapSize; i++) {
        map[i] = luTable[(map[i] & 0xFF)];
      }
    }
  }
  







  private void initFields(int numBands, float[][][] breakpoints)
  {
    abscissas = new float[numBands][];
    slopes = new float[numBands][];
    intercepts = new float[numBands][];
    minOrdinates = new float[numBands];
    maxOrdinates = new float[numBands];
    
    for (int band = 0; band < numBands; band++) {
      abscissas[band] = (breakpoints.length == 1 ? breakpoints[0][0] : breakpoints[band][0]);
      
      int maxIndex = abscissas[band].length - 1;
      
      minOrdinates[band] = (breakpoints.length == 1 ? breakpoints[0][1][0] : breakpoints[band][1][0]);
      
      maxOrdinates[band] = (breakpoints.length == 1 ? breakpoints[0][1][maxIndex] : breakpoints[band][1][maxIndex]);
      

      slopes[band] = new float[maxIndex];
      intercepts[band] = new float[maxIndex];
      
      float[] x = abscissas[band];
      float[] y = breakpoints.length == 1 ? breakpoints[0][1] : breakpoints[band][1];
      
      float[] a = slopes[band];
      float[] b = intercepts[band];
      for (int i1 = 0; i1 < maxIndex; i1++) {
        int i2 = i1 + 1;
        a[i1] = ((y[i2] - y[i1]) / (x[i2] - x[i1]));
        y[i1] -= x[i1] * a[i1];
      }
    }
  }
  




  private void unsetFields()
  {
    abscissas = ((float[][])null);
    slopes = ((float[][])null);
    intercepts = ((float[][])null);
    minOrdinates = null;
    maxOrdinates = null;
  }
  



  private void createLUT()
  {
    int numBands = abscissas.length;
    byte[][] data = new byte[numBands][];
    

    for (int band = 0; band < numBands; band++)
    {
      data[band] = new byte['Ā'];
      

      byte[] table = data[band];
      float[] x = abscissas[band];
      float[] a = slopes[band];
      float[] b = intercepts[band];
      float yL = minOrdinates[band];
      float yH = maxOrdinates[band];
      

      for (int value = 0; value < 256; value++) {
        table[value] = ImageUtil.clampRoundByte(binarySearch(x, yL, yH, a, b, value));
      }
    }
    


    lut = new LookupTableJAI(data);
  }
  










  protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect)
  {
    RasterFormatTag[] formatTags = getFormatTags();
    
    if (isByteData) {
      computeRectByte(sources, dest, destRect);
    } else {
      RasterAccessor dst = new RasterAccessor(dest, destRect, formatTags[1], getColorModel());
      

      RasterAccessor src = new RasterAccessor(sources[0], destRect, formatTags[0], getSource(0).getColorModel());
      


      switch (dst.getDataType()) {
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
  }
  

  private void computeRectByte(Raster[] sources, WritableRaster dest, Rectangle destRect)
  {
    lut.lookup(sources[0], dest, destRect);
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
      
      int dstLineOffset = dstBandOffsets[b];
      int srcLineOffset = srcBandOffsets[b];
      

      float[] x = abscissas[b];
      float[] gain = slopes[b];
      float[] bias = intercepts[b];
      float yL = minOrdinates[b];
      float yH = maxOrdinates[b];
      
      for (int h = 0; h < dstHeight; h++) {
        int dstPixelOffset = dstLineOffset;
        int srcPixelOffset = srcLineOffset;
        
        dstLineOffset += dstLineStride;
        srcLineOffset += srcLineStride;
        
        for (int w = 0; w < dstWidth; w++) {
          d[dstPixelOffset] = ImageUtil.clampRoundUShort(binarySearch(x, yL, yH, gain, bias, s[srcPixelOffset] & 0xFFFF));
          



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
      
      int dstLineOffset = dstBandOffsets[b];
      int srcLineOffset = srcBandOffsets[b];
      

      float[] x = abscissas[b];
      float[] gain = slopes[b];
      float[] bias = intercepts[b];
      float yL = minOrdinates[b];
      float yH = maxOrdinates[b];
      
      for (int h = 0; h < dstHeight; h++) {
        int dstPixelOffset = dstLineOffset;
        int srcPixelOffset = srcLineOffset;
        
        dstLineOffset += dstLineStride;
        srcLineOffset += srcLineStride;
        
        for (int w = 0; w < dstWidth; w++) {
          d[dstPixelOffset] = ImageUtil.clampRoundShort(binarySearch(x, yL, yH, gain, bias, s[srcPixelOffset]));
          


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
      
      int dstLineOffset = dstBandOffsets[b];
      int srcLineOffset = srcBandOffsets[b];
      

      float[] x = abscissas[b];
      float[] gain = slopes[b];
      float[] bias = intercepts[b];
      float yL = minOrdinates[b];
      float yH = maxOrdinates[b];
      
      for (int h = 0; h < dstHeight; h++) {
        int dstPixelOffset = dstLineOffset;
        int srcPixelOffset = srcLineOffset;
        
        dstLineOffset += dstLineStride;
        srcLineOffset += srcLineStride;
        
        for (int w = 0; w < dstWidth; w++) {
          d[dstPixelOffset] = ImageUtil.clampRoundInt(binarySearch(x, yL, yH, gain, bias, s[srcPixelOffset]));
          


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
      
      int dstLineOffset = dstBandOffsets[b];
      int srcLineOffset = srcBandOffsets[b];
      

      float[] x = abscissas[b];
      float[] gain = slopes[b];
      float[] bias = intercepts[b];
      float yL = minOrdinates[b];
      float yH = maxOrdinates[b];
      
      for (int h = 0; h < dstHeight; h++) {
        int dstPixelOffset = dstLineOffset;
        int srcPixelOffset = srcLineOffset;
        
        dstLineOffset += dstLineStride;
        srcLineOffset += srcLineStride;
        
        for (int w = 0; w < dstWidth; w++) {
          d[dstPixelOffset] = binarySearch(x, yL, yH, gain, bias, s[srcPixelOffset]);
          


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
      
      int dstLineOffset = dstBandOffsets[b];
      int srcLineOffset = srcBandOffsets[b];
      

      float[] x = abscissas[b];
      float[] gain = slopes[b];
      float[] bias = intercepts[b];
      float yL = minOrdinates[b];
      float yH = maxOrdinates[b];
      
      for (int h = 0; h < dstHeight; h++) {
        int dstPixelOffset = dstLineOffset;
        int srcPixelOffset = srcLineOffset;
        
        dstLineOffset += dstLineStride;
        srcLineOffset += srcLineStride;
        
        for (int w = 0; w < dstWidth; w++) {
          d[dstPixelOffset] = binarySearch(x, yL, yH, gain, bias, (float)s[srcPixelOffset]);
          


          dstPixelOffset += dstPixelStride;
          srcPixelOffset += srcPixelStride;
        }
      }
    }
  }
}
