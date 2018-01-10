package com.sun.media.jai.opimage;

import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.lang.ref.SoftReference;
import java.util.Map;
import javax.media.jai.ImageLayout;
import javax.media.jai.PlanarImage;
import javax.media.jai.PointOpImage;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFormatTag;



































final class MaxOpImage
  extends PointOpImage
{
  private static long negativeZeroFloatBits = Float.floatToIntBits(-0.0F);
  private static long negativeZeroDoubleBits = Double.doubleToLongBits(-0.0D);
  private static byte[] byteTable = null;
  private static SoftReference softRef = null;
  
  private synchronized void allocByteTable() {
    if ((softRef == null) || (softRef.get() == null))
    {




      byteTable = new byte[65536];
      softRef = new SoftReference(byteTable);
      

      int idx = 0;
      for (int i1 = 0; i1 < 256; i1++) {
        int base = i1 << 8;
        for (int i2 = 0; i2 < i1; i2++) {
          byteTable[(base + i2)] = ((byte)i1);
        }
        for (int i2 = i1; i2 < 256; i2++) {
          byteTable[(base + i2)] = ((byte)i2);
        }
      }
    }
  }
  









  public MaxOpImage(RenderedImage source1, RenderedImage source2, Map config, ImageLayout layout)
  {
    super(source1, source2, layout, config, true);
    
    if (sampleModel.getTransferType() == 0) {
      allocByteTable();
    }
    

    permitInPlaceOperation();
  }
  











  protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect)
  {
    RasterFormatTag[] formatTags = getFormatTags();
    

    RasterAccessor s1 = new RasterAccessor(sources[0], destRect, formatTags[0], getSourceImage(0).getColorModel());
    

    RasterAccessor s2 = new RasterAccessor(sources[1], destRect, formatTags[1], getSourceImage(1).getColorModel());
    

    RasterAccessor d = new RasterAccessor(dest, destRect, formatTags[2], getColorModel());
    

    switch (d.getDataType()) {
    case 0: 
      computeRectByte(s1, s2, d);
      break;
    case 1: 
      computeRectUShort(s1, s2, d);
      break;
    case 2: 
      computeRectShort(s1, s2, d);
      break;
    case 3: 
      computeRectInt(s1, s2, d);
      break;
    case 4: 
      computeRectFloat(s1, s2, d);
      break;
    case 5: 
      computeRectDouble(s1, s2, d);
    }
    
    
    if (d.isDataCopy()) {
      d.clampDataArrays();
      d.copyDataToRaster();
    }
  }
  

  private void computeRectByte(RasterAccessor src1, RasterAccessor src2, RasterAccessor dst)
  {
    int s1LineStride = src1.getScanlineStride();
    int s1PixelStride = src1.getPixelStride();
    int[] s1BandOffsets = src1.getBandOffsets();
    byte[][] s1Data = src1.getByteDataArrays();
    
    int s2LineStride = src2.getScanlineStride();
    int s2PixelStride = src2.getPixelStride();
    int[] s2BandOffsets = src2.getBandOffsets();
    byte[][] s2Data = src2.getByteDataArrays();
    
    int dwidth = dst.getWidth();
    int dheight = dst.getHeight();
    int bands = dst.getNumBands();
    int dLineStride = dst.getScanlineStride();
    int dPixelStride = dst.getPixelStride();
    int[] dBandOffsets = dst.getBandOffsets();
    byte[][] dData = dst.getByteDataArrays();
    
    for (int b = 0; b < bands; b++) {
      byte[] s1 = s1Data[b];
      byte[] s2 = s2Data[b];
      byte[] d = dData[b];
      
      int s1LineOffset = s1BandOffsets[b];
      int s2LineOffset = s2BandOffsets[b];
      int dLineOffset = dBandOffsets[b];
      
      for (int h = 0; h < dheight; h++) {
        int s1PixelOffset = s1LineOffset;
        int s2PixelOffset = s2LineOffset;
        int dPixelOffset = dLineOffset;
        
        s1LineOffset += s1LineStride;
        s2LineOffset += s2LineStride;
        dLineOffset += dLineStride;
        
        int dstEnd = dPixelOffset + dwidth * dPixelStride;
        while (dPixelOffset < dstEnd) {
          int i1 = s1[s1PixelOffset] & 0xFF;
          int i2 = s2[s2PixelOffset] & 0xFF;
          d[dPixelOffset] = byteTable[((i1 << 8) + i2)];
          
          s1PixelOffset += s1PixelStride;
          s2PixelOffset += s2PixelStride;
          dPixelOffset += dPixelStride;
        }
      }
    }
  }
  

  private void computeRectUShort(RasterAccessor src1, RasterAccessor src2, RasterAccessor dst)
  {
    int s1LineStride = src1.getScanlineStride();
    int s1PixelStride = src1.getPixelStride();
    int[] s1BandOffsets = src1.getBandOffsets();
    short[][] s1Data = src1.getShortDataArrays();
    
    int s2LineStride = src2.getScanlineStride();
    int s2PixelStride = src2.getPixelStride();
    int[] s2BandOffsets = src2.getBandOffsets();
    short[][] s2Data = src2.getShortDataArrays();
    
    int dwidth = dst.getWidth();
    int dheight = dst.getHeight();
    int bands = dst.getNumBands();
    int dLineStride = dst.getScanlineStride();
    int dPixelStride = dst.getPixelStride();
    int[] dBandOffsets = dst.getBandOffsets();
    short[][] dData = dst.getShortDataArrays();
    
    for (int b = 0; b < bands; b++) {
      short[] s1 = s1Data[b];
      short[] s2 = s2Data[b];
      short[] d = dData[b];
      
      int s1LineOffset = s1BandOffsets[b];
      int s2LineOffset = s2BandOffsets[b];
      int dLineOffset = dBandOffsets[b];
      
      for (int h = 0; h < dheight; h++) {
        int s1PixelOffset = s1LineOffset;
        int s2PixelOffset = s2LineOffset;
        int dPixelOffset = dLineOffset;
        
        s1LineOffset += s1LineStride;
        s2LineOffset += s2LineStride;
        dLineOffset += dLineStride;
        
        for (int w = 0; w < dwidth; w++) {
          d[dPixelOffset] = maxUShort(s1[s1PixelOffset], s2[s2PixelOffset]);
          

          s1PixelOffset += s1PixelStride;
          s2PixelOffset += s2PixelStride;
          dPixelOffset += dPixelStride;
        }
      }
    }
  }
  

  private void computeRectShort(RasterAccessor src1, RasterAccessor src2, RasterAccessor dst)
  {
    int s1LineStride = src1.getScanlineStride();
    int s1PixelStride = src1.getPixelStride();
    int[] s1BandOffsets = src1.getBandOffsets();
    short[][] s1Data = src1.getShortDataArrays();
    
    int s2LineStride = src2.getScanlineStride();
    int s2PixelStride = src2.getPixelStride();
    int[] s2BandOffsets = src2.getBandOffsets();
    short[][] s2Data = src2.getShortDataArrays();
    
    int dwidth = dst.getWidth();
    int dheight = dst.getHeight();
    int bands = dst.getNumBands();
    int dLineStride = dst.getScanlineStride();
    int dPixelStride = dst.getPixelStride();
    int[] dBandOffsets = dst.getBandOffsets();
    short[][] dData = dst.getShortDataArrays();
    
    for (int b = 0; b < bands; b++) {
      short[] s1 = s1Data[b];
      short[] s2 = s2Data[b];
      short[] d = dData[b];
      
      int s1LineOffset = s1BandOffsets[b];
      int s2LineOffset = s2BandOffsets[b];
      int dLineOffset = dBandOffsets[b];
      
      for (int h = 0; h < dheight; h++) {
        int s1PixelOffset = s1LineOffset;
        int s2PixelOffset = s2LineOffset;
        int dPixelOffset = dLineOffset;
        
        s1LineOffset += s1LineStride;
        s2LineOffset += s2LineStride;
        dLineOffset += dLineStride;
        
        for (int w = 0; w < dwidth; w++) {
          d[dPixelOffset] = maxShort(s1[s1PixelOffset], s2[s2PixelOffset]);
          

          s1PixelOffset += s1PixelStride;
          s2PixelOffset += s2PixelStride;
          dPixelOffset += dPixelStride;
        }
      }
    }
  }
  

  private void computeRectInt(RasterAccessor src1, RasterAccessor src2, RasterAccessor dst)
  {
    int s1LineStride = src1.getScanlineStride();
    int s1PixelStride = src1.getPixelStride();
    int[] s1BandOffsets = src1.getBandOffsets();
    int[][] s1Data = src1.getIntDataArrays();
    
    int s2LineStride = src2.getScanlineStride();
    int s2PixelStride = src2.getPixelStride();
    int[] s2BandOffsets = src2.getBandOffsets();
    int[][] s2Data = src2.getIntDataArrays();
    
    int dwidth = dst.getWidth();
    int dheight = dst.getHeight();
    int bands = dst.getNumBands();
    int dLineStride = dst.getScanlineStride();
    int dPixelStride = dst.getPixelStride();
    int[] dBandOffsets = dst.getBandOffsets();
    int[][] dData = dst.getIntDataArrays();
    
    for (int b = 0; b < bands; b++) {
      int[] s1 = s1Data[b];
      int[] s2 = s2Data[b];
      int[] d = dData[b];
      
      int s1LineOffset = s1BandOffsets[b];
      int s2LineOffset = s2BandOffsets[b];
      int dLineOffset = dBandOffsets[b];
      
      for (int h = 0; h < dheight; h++) {
        int s1PixelOffset = s1LineOffset;
        int s2PixelOffset = s2LineOffset;
        int dPixelOffset = dLineOffset;
        
        s1LineOffset += s1LineStride;
        s2LineOffset += s2LineStride;
        dLineOffset += dLineStride;
        
        for (int w = 0; w < dwidth; w++) {
          d[dPixelOffset] = maxInt(s1[s1PixelOffset], s2[s2PixelOffset]);
          

          s1PixelOffset += s1PixelStride;
          s2PixelOffset += s2PixelStride;
          dPixelOffset += dPixelStride;
        }
      }
    }
  }
  

  private void computeRectFloat(RasterAccessor src1, RasterAccessor src2, RasterAccessor dst)
  {
    int s1LineStride = src1.getScanlineStride();
    int s1PixelStride = src1.getPixelStride();
    int[] s1BandOffsets = src1.getBandOffsets();
    float[][] s1Data = src1.getFloatDataArrays();
    
    int s2LineStride = src2.getScanlineStride();
    int s2PixelStride = src2.getPixelStride();
    int[] s2BandOffsets = src2.getBandOffsets();
    float[][] s2Data = src2.getFloatDataArrays();
    
    int dwidth = dst.getWidth();
    int dheight = dst.getHeight();
    int bands = dst.getNumBands();
    int dLineStride = dst.getScanlineStride();
    int dPixelStride = dst.getPixelStride();
    int[] dBandOffsets = dst.getBandOffsets();
    float[][] dData = dst.getFloatDataArrays();
    
    for (int b = 0; b < bands; b++) {
      float[] s1 = s1Data[b];
      float[] s2 = s2Data[b];
      float[] d = dData[b];
      
      int s1LineOffset = s1BandOffsets[b];
      int s2LineOffset = s2BandOffsets[b];
      int dLineOffset = dBandOffsets[b];
      
      for (int h = 0; h < dheight; h++) {
        int s1PixelOffset = s1LineOffset;
        int s2PixelOffset = s2LineOffset;
        int dPixelOffset = dLineOffset;
        
        s1LineOffset += s1LineStride;
        s2LineOffset += s2LineStride;
        dLineOffset += dLineStride;
        
        for (int w = 0; w < dwidth; w++) {
          d[dPixelOffset] = maxFloat(s1[s1PixelOffset], s2[s2PixelOffset]);
          

          s1PixelOffset += s1PixelStride;
          s2PixelOffset += s2PixelStride;
          dPixelOffset += dPixelStride;
        }
      }
    }
  }
  

  private void computeRectDouble(RasterAccessor src1, RasterAccessor src2, RasterAccessor dst)
  {
    int s1LineStride = src1.getScanlineStride();
    int s1PixelStride = src1.getPixelStride();
    int[] s1BandOffsets = src1.getBandOffsets();
    double[][] s1Data = src1.getDoubleDataArrays();
    
    int s2LineStride = src2.getScanlineStride();
    int s2PixelStride = src2.getPixelStride();
    int[] s2BandOffsets = src2.getBandOffsets();
    double[][] s2Data = src2.getDoubleDataArrays();
    
    int dwidth = dst.getWidth();
    int dheight = dst.getHeight();
    int bands = dst.getNumBands();
    int dLineStride = dst.getScanlineStride();
    int dPixelStride = dst.getPixelStride();
    int[] dBandOffsets = dst.getBandOffsets();
    double[][] dData = dst.getDoubleDataArrays();
    
    for (int b = 0; b < bands; b++) {
      double[] s1 = s1Data[b];
      double[] s2 = s2Data[b];
      double[] d = dData[b];
      
      int s1LineOffset = s1BandOffsets[b];
      int s2LineOffset = s2BandOffsets[b];
      int dLineOffset = dBandOffsets[b];
      
      for (int h = 0; h < dheight; h++) {
        int s1PixelOffset = s1LineOffset;
        int s2PixelOffset = s2LineOffset;
        int dPixelOffset = dLineOffset;
        
        s1LineOffset += s1LineStride;
        s2LineOffset += s2LineStride;
        dLineOffset += dLineStride;
        
        for (int w = 0; w < dwidth; w++) {
          d[dPixelOffset] = maxDouble(s1[s1PixelOffset], s2[s2PixelOffset]);
          

          s1PixelOffset += s1PixelStride;
          s2PixelOffset += s2PixelStride;
          dPixelOffset += dPixelStride;
        }
      }
    }
  }
  
  private final short maxUShort(short a, short b) {
    return (a & 0xFFFF) > (b & 0xFFFF) ? a : b;
  }
  
  private final short maxShort(short a, short b) {
    return a > b ? a : b;
  }
  
  private final int maxInt(int a, int b) {
    return a > b ? a : b;
  }
  
  private final float maxFloat(float a, float b) {
    if (a != a) return a;
    if ((a == 0.0F) && (b == 0.0F) && (Float.floatToIntBits(a) == negativeZeroFloatBits))
    {
      return b;
    }
    return a >= b ? a : b;
  }
  
  private final double maxDouble(double a, double b) {
    if (a != a) return a;
    if ((a == 0.0D) && (b == 0.0D) && (Double.doubleToLongBits(a) == negativeZeroDoubleBits))
    {
      return b;
    }
    return a >= b ? a : b;
  }
}
