package com.sun.media.jai.opimage;

import com.sun.media.jai.util.Rational;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import javax.media.jai.InterpolationTable;
import javax.media.jai.PlanarImage;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFormatTag;
import javax.media.jai.ScaleOpImage;

























final class ScaleBicubicOpImage
  extends ScaleOpImage
{
  private int subsampleBits;
  private int one;
  private int[] tableDataHi = null;
  

  private int[] tableDataVi = null;
  

  private float[] tableDataHf = null;
  

  private float[] tableDataVf = null;
  

  private double[] tableDataHd = null;
  

  private double[] tableDataVd = null;
  

  private int precisionBits;
  

  private int round;
  
  private Rational half = new Rational(1L, 2L);
  



  InterpolationTable interpTable;
  



  long invScaleYInt;
  



  long invScaleYFrac;
  


  long invScaleXInt;
  


  long invScaleXFrac;
  



  public ScaleBicubicOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, float xScale, float yScale, float xTrans, float yTrans, Interpolation interp)
  {
    super(source, layout, config, true, extender, interp, xScale, yScale, xTrans, yTrans);
    









    subsampleBits = interp.getSubsampleBitsH();
    interpTable = ((InterpolationTable)interp);
    

    one = (1 << subsampleBits);
    precisionBits = interpTable.getPrecisionBits();
    if (precisionBits > 0) {
      round = (1 << precisionBits - 1);
    }
    
    if (invScaleYRational.num > invScaleYRational.denom) {
      invScaleYInt = (invScaleYRational.num / invScaleYRational.denom);
      invScaleYFrac = (invScaleYRational.num % invScaleYRational.denom);
    } else {
      invScaleYInt = 0L;
      invScaleYFrac = invScaleYRational.num;
    }
    
    if (invScaleXRational.num > invScaleXRational.denom) {
      invScaleXInt = (invScaleXRational.num / invScaleXRational.denom);
      invScaleXFrac = (invScaleXRational.num % invScaleXRational.denom);
    } else {
      invScaleXInt = 0L;
      invScaleXFrac = invScaleXRational.num;
    }
  }
  











  protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect)
  {
    RasterFormatTag[] formatTags = getFormatTags();
    
    Raster source = sources[0];
    
    Rectangle srcRect = source.getBounds();
    
    int srcRectX = x;
    int srcRectY = y;
    
    RasterAccessor srcAccessor = new RasterAccessor(source, srcRect, formatTags[0], getSource(0).getColorModel());
    


    RasterAccessor dstAccessor = new RasterAccessor(dest, destRect, formatTags[1], getColorModel());
    


    int dx = x;
    int dy = y;
    int dwidth = width;
    int dheight = height;
    int srcPixelStride = srcAccessor.getPixelStride();
    int srcScanlineStride = srcAccessor.getScanlineStride();
    
    int[] ypos = new int[dheight];
    int[] xpos = new int[dwidth];
    

    int[] yfracvalues = new int[dheight];
    
    int[] xfracvalues = new int[dwidth];
    
    long syNum = dy;long syDenom = 1L;
    

    syNum = syNum * transYRationalDenom - transYRationalNum * syDenom;
    syDenom *= transYRationalDenom;
    

    syNum = 2L * syNum + syDenom;
    syDenom *= 2L;
    

    syNum *= invScaleYRationalNum;
    syDenom *= invScaleYRationalDenom;
    

    syNum = 2L * syNum - syDenom;
    syDenom *= 2L;
    

    int srcYInt = Rational.floor(syNum, syDenom);
    long srcYFrac = syNum % syDenom;
    if (srcYInt < 0) {
      srcYFrac = syDenom + srcYFrac;
    }
    


    long commonYDenom = syDenom * invScaleYRationalDenom;
    srcYFrac *= invScaleYRationalDenom;
    long newInvScaleYFrac = invScaleYFrac * syDenom;
    
    long sxNum = dx;long sxDenom = 1L;
    

    sxNum = sxNum * transXRationalDenom - transXRationalNum * sxDenom;
    sxDenom *= transXRationalDenom;
    

    sxNum = 2L * sxNum + sxDenom;
    sxDenom *= 2L;
    

    sxNum *= invScaleXRationalNum;
    sxDenom *= invScaleXRationalDenom;
    

    sxNum = 2L * sxNum - sxDenom;
    sxDenom *= 2L;
    


    int srcXInt = Rational.floor(sxNum, sxDenom);
    long srcXFrac = sxNum % sxDenom;
    if (srcXInt < 0) {
      srcXFrac = sxDenom + srcXFrac;
    }
    


    long commonXDenom = sxDenom * invScaleXRationalDenom;
    srcXFrac *= invScaleXRationalDenom;
    long newInvScaleXFrac = invScaleXFrac * sxDenom;
    
    for (int i = 0; i < dwidth; i++) {
      xpos[i] = ((srcXInt - srcRectX) * srcPixelStride);
      xfracvalues[i] = ((int)((float)srcXFrac / (float)commonXDenom * one));
      




      srcXInt = (int)(srcXInt + invScaleXInt);
      


      srcXFrac += newInvScaleXFrac;
      



      if (srcXFrac >= commonXDenom) {
        srcXInt++;
        srcXFrac -= commonXDenom;
      }
    }
    
    for (int i = 0; i < dheight; i++)
    {

      ypos[i] = ((srcYInt - srcRectY) * srcScanlineStride);
      

      yfracvalues[i] = ((int)((float)srcYFrac / (float)commonYDenom * one));
      




      srcYInt = (int)(srcYInt + invScaleYInt);
      


      srcYFrac += newInvScaleYFrac;
      



      if (srcYFrac >= commonYDenom) {
        srcYInt++;
        srcYFrac -= commonYDenom;
      }
    }
    
    switch (dstAccessor.getDataType())
    {
    case 0: 
      initTableDataI();
      byteLoop(srcAccessor, destRect, dstAccessor, xpos, ypos, xfracvalues, yfracvalues);
      
      break;
    
    case 2: 
      initTableDataI();
      shortLoop(srcAccessor, destRect, dstAccessor, xpos, ypos, xfracvalues, yfracvalues);
      
      break;
    
    case 1: 
      initTableDataI();
      ushortLoop(srcAccessor, destRect, dstAccessor, xpos, ypos, xfracvalues, yfracvalues);
      
      break;
    
    case 3: 
      initTableDataI();
      intLoop(srcAccessor, destRect, dstAccessor, xpos, ypos, xfracvalues, yfracvalues);
      
      break;
    
    case 4: 
      initTableDataF();
      floatLoop(srcAccessor, destRect, dstAccessor, xpos, ypos, xfracvalues, yfracvalues);
      
      break;
    
    case 5: 
      initTableDataD();
      doubleLoop(srcAccessor, destRect, dstAccessor, xpos, ypos, xfracvalues, yfracvalues);
      
      break;
    
    default: 
      throw new RuntimeException(JaiI18N.getString("OrderedDitherOpImage0"));
    }
    
    



    if (dstAccessor.isDataCopy()) {
      dstAccessor.clampDataArrays();
      dstAccessor.copyDataToRaster();
    }
  }
  


  private void byteLoop(RasterAccessor src, Rectangle destRect, RasterAccessor dst, int[] xpos, int[] ypos, int[] xfracvalues, int[] yfracvalues)
  {
    int srcPixelStride = src.getPixelStride();
    int srcScanlineStride = src.getScanlineStride();
    
    int dwidth = width;
    int dheight = height;
    int dnumBands = dst.getNumBands();
    byte[][] dstDataArrays = dst.getByteDataArrays();
    int[] dstBandOffsets = dst.getBandOffsets();
    int dstPixelStride = dst.getPixelStride();
    int dstScanlineStride = dst.getScanlineStride();
    
    byte[][] srcDataArrays = src.getByteDataArrays();
    int[] bandOffsets = src.getBandOffsets();
    







    int dstOffset = 0;
    

    for (int k = 0; k < dnumBands; k++) {
      byte[] dstData = dstDataArrays[k];
      byte[] srcData = srcDataArrays[k];
      int dstScanlineOffset = dstBandOffsets[k];
      int bandOffset = bandOffsets[k];
      
      for (int j = 0; j < dheight; j++) {
        int dstPixelOffset = dstScanlineOffset;
        int yfrac = yfracvalues[j];
        int posy = ypos[j] + bandOffset;
        int posylow = posy - srcScanlineStride;
        int posyhigh = posy + srcScanlineStride;
        int posyhigh2 = posyhigh + srcScanlineStride;
        for (int i = 0; i < dwidth; i++) {
          int xfrac = xfracvalues[i];
          int posx = xpos[i];
          int posxlow = posx - srcPixelStride;
          int posxhigh = posx + srcPixelStride;
          int posxhigh2 = posxhigh + srcPixelStride;
          

          int s__ = srcData[(posxlow + posylow)] & 0xFF;
          int s_0 = srcData[(posx + posylow)] & 0xFF;
          int s_1 = srcData[(posxhigh + posylow)] & 0xFF;
          int s_2 = srcData[(posxhigh2 + posylow)] & 0xFF;
          
          int s0_ = srcData[(posxlow + posy)] & 0xFF;
          int s00 = srcData[(posx + posy)] & 0xFF;
          int s01 = srcData[(posxhigh + posy)] & 0xFF;
          int s02 = srcData[(posxhigh2 + posy)] & 0xFF;
          
          int s1_ = srcData[(posxlow + posyhigh)] & 0xFF;
          int s10 = srcData[(posx + posyhigh)] & 0xFF;
          int s11 = srcData[(posxhigh + posyhigh)] & 0xFF;
          int s12 = srcData[(posxhigh2 + posyhigh)] & 0xFF;
          
          int s2_ = srcData[(posxlow + posyhigh2)] & 0xFF;
          int s20 = srcData[(posx + posyhigh2)] & 0xFF;
          int s21 = srcData[(posxhigh + posyhigh2)] & 0xFF;
          int s22 = srcData[(posxhigh2 + posyhigh2)] & 0xFF;
          

          int offsetX = 4 * xfrac;
          int offsetX1 = offsetX + 1;
          int offsetX2 = offsetX + 2;
          int offsetX3 = offsetX + 3;
          
          long sum_ = tableDataHi[offsetX] * s__;
          sum_ += tableDataHi[offsetX1] * s_0;
          sum_ += tableDataHi[offsetX2] * s_1;
          sum_ += tableDataHi[offsetX3] * s_2;
          
          long sum0 = tableDataHi[offsetX] * s0_;
          sum0 += tableDataHi[offsetX1] * s00;
          sum0 += tableDataHi[offsetX2] * s01;
          sum0 += tableDataHi[offsetX3] * s02;
          
          long sum1 = tableDataHi[offsetX] * s1_;
          sum1 += tableDataHi[offsetX1] * s10;
          sum1 += tableDataHi[offsetX2] * s11;
          sum1 += tableDataHi[offsetX3] * s12;
          
          long sum2 = tableDataHi[offsetX] * s2_;
          sum2 += tableDataHi[offsetX1] * s20;
          sum2 += tableDataHi[offsetX2] * s21;
          sum2 += tableDataHi[offsetX3] * s22;
          

          sum_ = sum_ + round >> precisionBits;
          sum0 = sum0 + round >> precisionBits;
          sum1 = sum1 + round >> precisionBits;
          sum2 = sum2 + round >> precisionBits;
          

          int offsetY = 4 * yfrac;
          long sum = tableDataVi[offsetY] * sum_;
          sum += tableDataVi[(offsetY + 1)] * sum0;
          sum += tableDataVi[(offsetY + 2)] * sum1;
          sum += tableDataVi[(offsetY + 3)] * sum2;
          
          int s = (int)(sum + round >> precisionBits);
          

          if (s > 255) {
            s = 255;
          } else if (s < 0) {
            s = 0;
          }
          
          dstData[dstPixelOffset] = ((byte)(s & 0xFF));
          dstPixelOffset += dstPixelStride;
        }
        dstScanlineOffset += dstScanlineStride;
      }
    }
  }
  


  private void shortLoop(RasterAccessor src, Rectangle destRect, RasterAccessor dst, int[] xpos, int[] ypos, int[] xfracvalues, int[] yfracvalues)
  {
    int srcPixelStride = src.getPixelStride();
    int srcScanlineStride = src.getScanlineStride();
    
    int dwidth = width;
    int dheight = height;
    int dnumBands = dst.getNumBands();
    short[][] dstDataArrays = dst.getShortDataArrays();
    int[] dstBandOffsets = dst.getBandOffsets();
    int dstPixelStride = dst.getPixelStride();
    int dstScanlineStride = dst.getScanlineStride();
    
    short[][] srcDataArrays = src.getShortDataArrays();
    int[] bandOffsets = src.getBandOffsets();
    
    int dstOffset = 0;
    










    for (int k = 0; k < dnumBands; k++) {
      short[] dstData = dstDataArrays[k];
      short[] srcData = srcDataArrays[k];
      int dstScanlineOffset = dstBandOffsets[k];
      int bandOffset = bandOffsets[k];
      
      for (int j = 0; j < dheight; j++) {
        int dstPixelOffset = dstScanlineOffset;
        int yfrac = yfracvalues[j];
        int posy = ypos[j] + bandOffset;
        int posylow = posy - srcScanlineStride;
        int posyhigh = posy + srcScanlineStride;
        int posyhigh2 = posyhigh + srcScanlineStride;
        for (int i = 0; i < dwidth; i++) {
          int xfrac = xfracvalues[i];
          int posx = xpos[i];
          int posxlow = posx - srcPixelStride;
          int posxhigh = posx + srcPixelStride;
          int posxhigh2 = posxhigh + srcPixelStride;
          

          int s__ = srcData[(posxlow + posylow)];
          int s_0 = srcData[(posx + posylow)];
          int s_1 = srcData[(posxhigh + posylow)];
          int s_2 = srcData[(posxhigh2 + posylow)];
          
          int s0_ = srcData[(posxlow + posy)];
          int s00 = srcData[(posx + posy)];
          int s01 = srcData[(posxhigh + posy)];
          int s02 = srcData[(posxhigh2 + posy)];
          
          int s1_ = srcData[(posxlow + posyhigh)];
          int s10 = srcData[(posx + posyhigh)];
          int s11 = srcData[(posxhigh + posyhigh)];
          int s12 = srcData[(posxhigh2 + posyhigh)];
          
          int s2_ = srcData[(posxlow + posyhigh2)];
          int s20 = srcData[(posx + posyhigh2)];
          int s21 = srcData[(posxhigh + posyhigh2)];
          int s22 = srcData[(posxhigh2 + posyhigh2)];
          

          int offsetX = 4 * xfrac;
          int offsetX1 = offsetX + 1;
          int offsetX2 = offsetX + 2;
          int offsetX3 = offsetX + 3;
          
          long sum_ = tableDataHi[offsetX] * s__;
          sum_ += tableDataHi[offsetX1] * s_0;
          sum_ += tableDataHi[offsetX2] * s_1;
          sum_ += tableDataHi[offsetX3] * s_2;
          
          long sum0 = tableDataHi[offsetX] * s0_;
          sum0 += tableDataHi[offsetX1] * s00;
          sum0 += tableDataHi[offsetX2] * s01;
          sum0 += tableDataHi[offsetX3] * s02;
          
          long sum1 = tableDataHi[offsetX] * s1_;
          sum1 += tableDataHi[offsetX1] * s10;
          sum1 += tableDataHi[offsetX2] * s11;
          sum1 += tableDataHi[offsetX3] * s12;
          
          long sum2 = tableDataHi[offsetX] * s2_;
          sum2 += tableDataHi[offsetX1] * s20;
          sum2 += tableDataHi[offsetX2] * s21;
          sum2 += tableDataHi[offsetX3] * s22;
          

          sum_ = sum_ + round >> precisionBits;
          sum0 = sum0 + round >> precisionBits;
          sum1 = sum1 + round >> precisionBits;
          sum2 = sum2 + round >> precisionBits;
          

          int offsetY = 4 * yfrac;
          long sum = tableDataVi[offsetY] * sum_;
          sum += tableDataVi[(offsetY + 1)] * sum0;
          sum += tableDataVi[(offsetY + 2)] * sum1;
          sum += tableDataVi[(offsetY + 3)] * sum2;
          
          int s = (int)(sum + round >> precisionBits);
          

          if (s > 32767) {
            s = 32767;
          } else if (s < 32768) {
            s = 32768;
          }
          
          dstData[dstPixelOffset] = ((short)s);
          dstPixelOffset += dstPixelStride;
        }
        dstScanlineOffset += dstScanlineStride;
      }
    }
  }
  


  private void ushortLoop(RasterAccessor src, Rectangle destRect, RasterAccessor dst, int[] xpos, int[] ypos, int[] xfracvalues, int[] yfracvalues)
  {
    int srcPixelStride = src.getPixelStride();
    int srcScanlineStride = src.getScanlineStride();
    
    int dwidth = width;
    int dheight = height;
    int dnumBands = dst.getNumBands();
    short[][] dstDataArrays = dst.getShortDataArrays();
    int[] dstBandOffsets = dst.getBandOffsets();
    int dstPixelStride = dst.getPixelStride();
    int dstScanlineStride = dst.getScanlineStride();
    
    short[][] srcDataArrays = src.getShortDataArrays();
    int[] bandOffsets = src.getBandOffsets();
    
    int dstOffset = 0;
    










    for (int k = 0; k < dnumBands; k++) {
      short[] dstData = dstDataArrays[k];
      short[] srcData = srcDataArrays[k];
      int dstScanlineOffset = dstBandOffsets[k];
      int bandOffset = bandOffsets[k];
      
      for (int j = 0; j < dheight; j++) {
        int dstPixelOffset = dstScanlineOffset;
        int yfrac = yfracvalues[j];
        int posy = ypos[j] + bandOffset;
        int posylow = posy - srcScanlineStride;
        int posyhigh = posy + srcScanlineStride;
        int posyhigh2 = posyhigh + srcScanlineStride;
        for (int i = 0; i < dwidth; i++) {
          int xfrac = xfracvalues[i];
          int posx = xpos[i];
          int posxlow = posx - srcPixelStride;
          int posxhigh = posx + srcPixelStride;
          int posxhigh2 = posxhigh + srcPixelStride;
          

          int s__ = srcData[(posxlow + posylow)] & 0xFFFF;
          int s_0 = srcData[(posx + posylow)] & 0xFFFF;
          int s_1 = srcData[(posxhigh + posylow)] & 0xFFFF;
          int s_2 = srcData[(posxhigh2 + posylow)] & 0xFFFF;
          
          int s0_ = srcData[(posxlow + posy)] & 0xFFFF;
          int s00 = srcData[(posx + posy)] & 0xFFFF;
          int s01 = srcData[(posxhigh + posy)] & 0xFFFF;
          int s02 = srcData[(posxhigh2 + posy)] & 0xFFFF;
          
          int s1_ = srcData[(posxlow + posyhigh)] & 0xFFFF;
          int s10 = srcData[(posx + posyhigh)] & 0xFFFF;
          int s11 = srcData[(posxhigh + posyhigh)] & 0xFFFF;
          int s12 = srcData[(posxhigh2 + posyhigh)] & 0xFFFF;
          
          int s2_ = srcData[(posxlow + posyhigh2)] & 0xFFFF;
          int s20 = srcData[(posx + posyhigh2)] & 0xFFFF;
          int s21 = srcData[(posxhigh + posyhigh2)] & 0xFFFF;
          int s22 = srcData[(posxhigh2 + posyhigh2)] & 0xFFFF;
          

          int offsetX = 4 * xfrac;
          int offsetX1 = offsetX + 1;
          int offsetX2 = offsetX + 2;
          int offsetX3 = offsetX + 3;
          
          long sum_ = tableDataHi[offsetX] * s__;
          sum_ += tableDataHi[offsetX1] * s_0;
          sum_ += tableDataHi[offsetX2] * s_1;
          sum_ += tableDataHi[offsetX3] * s_2;
          
          long sum0 = tableDataHi[offsetX] * s0_;
          sum0 += tableDataHi[offsetX1] * s00;
          sum0 += tableDataHi[offsetX2] * s01;
          sum0 += tableDataHi[offsetX3] * s02;
          
          long sum1 = tableDataHi[offsetX] * s1_;
          sum1 += tableDataHi[offsetX1] * s10;
          sum1 += tableDataHi[offsetX2] * s11;
          sum1 += tableDataHi[offsetX3] * s12;
          
          long sum2 = tableDataHi[offsetX] * s2_;
          sum2 += tableDataHi[offsetX1] * s20;
          sum2 += tableDataHi[offsetX2] * s21;
          sum2 += tableDataHi[offsetX3] * s22;
          

          sum_ = sum_ + round >> precisionBits;
          sum0 = sum0 + round >> precisionBits;
          sum1 = sum1 + round >> precisionBits;
          sum2 = sum2 + round >> precisionBits;
          

          int offsetY = 4 * yfrac;
          long sum = tableDataVi[offsetY] * sum_;
          sum += tableDataVi[(offsetY + 1)] * sum0;
          sum += tableDataVi[(offsetY + 2)] * sum1;
          sum += tableDataVi[(offsetY + 3)] * sum2;
          
          int s = (int)(sum + round >> precisionBits);
          

          if (s > 65536) {
            s = 65536;
          } else if (s < 0) {
            s = 0;
          }
          
          dstData[dstPixelOffset] = ((short)(s & 0xFFFF));
          dstPixelOffset += dstPixelStride;
        }
        dstScanlineOffset += dstScanlineStride;
      }
    }
  }
  




  private void intLoop(RasterAccessor src, Rectangle destRect, RasterAccessor dst, int[] xpos, int[] ypos, int[] xfracvalues, int[] yfracvalues)
  {
    int srcPixelStride = src.getPixelStride();
    int srcScanlineStride = src.getScanlineStride();
    
    int dwidth = width;
    int dheight = height;
    int dnumBands = dst.getNumBands();
    int[][] dstDataArrays = dst.getIntDataArrays();
    int[] dstBandOffsets = dst.getBandOffsets();
    int dstPixelStride = dst.getPixelStride();
    int dstScanlineStride = dst.getScanlineStride();
    
    int[][] srcDataArrays = src.getIntDataArrays();
    int[] bandOffsets = src.getBandOffsets();
    
    int dstOffset = 0;
    










    for (int k = 0; k < dnumBands; k++) {
      int[] dstData = dstDataArrays[k];
      int[] srcData = srcDataArrays[k];
      int dstScanlineOffset = dstBandOffsets[k];
      int bandOffset = bandOffsets[k];
      
      for (int j = 0; j < dheight; j++) {
        int dstPixelOffset = dstScanlineOffset;
        long yfrac = yfracvalues[j];
        int posy = ypos[j] + bandOffset;
        int posylow = posy - srcScanlineStride;
        int posyhigh = posy + srcScanlineStride;
        int posyhigh2 = posyhigh + srcScanlineStride;
        for (int i = 0; i < dwidth; i++) {
          long xfrac = xfracvalues[i];
          int posx = xpos[i];
          int posxlow = posx - srcPixelStride;
          int posxhigh = posx + srcPixelStride;
          int posxhigh2 = posxhigh + srcPixelStride;
          

          int s__ = srcData[(posxlow + posylow)];
          int s_0 = srcData[(posx + posylow)];
          int s_1 = srcData[(posxhigh + posylow)];
          int s_2 = srcData[(posxhigh2 + posylow)];
          
          int s0_ = srcData[(posxlow + posy)];
          int s00 = srcData[(posx + posy)];
          int s01 = srcData[(posxhigh + posy)];
          int s02 = srcData[(posxhigh2 + posy)];
          
          int s1_ = srcData[(posxlow + posyhigh)];
          int s10 = srcData[(posx + posyhigh)];
          int s11 = srcData[(posxhigh + posyhigh)];
          int s12 = srcData[(posxhigh2 + posyhigh)];
          
          int s2_ = srcData[(posxlow + posyhigh2)];
          int s20 = srcData[(posx + posyhigh2)];
          int s21 = srcData[(posxhigh + posyhigh2)];
          int s22 = srcData[(posxhigh2 + posyhigh2)];
          

          int offsetX = (int)(4L * xfrac);
          int offsetX1 = offsetX + 1;
          int offsetX2 = offsetX + 2;
          int offsetX3 = offsetX + 3;
          
          long sum_ = tableDataHi[offsetX] * s__;
          sum_ += tableDataHi[offsetX1] * s_0;
          sum_ += tableDataHi[offsetX2] * s_1;
          sum_ += tableDataHi[offsetX3] * s_2;
          
          long sum0 = tableDataHi[offsetX] * s0_;
          sum0 += tableDataHi[offsetX1] * s00;
          sum0 += tableDataHi[offsetX2] * s01;
          sum0 += tableDataHi[offsetX3] * s02;
          
          long sum1 = tableDataHi[offsetX] * s1_;
          sum1 += tableDataHi[offsetX1] * s10;
          sum1 += tableDataHi[offsetX2] * s11;
          sum1 += tableDataHi[offsetX3] * s12;
          
          long sum2 = tableDataHi[offsetX] * s2_;
          sum2 += tableDataHi[offsetX1] * s20;
          sum2 += tableDataHi[offsetX2] * s21;
          sum2 += tableDataHi[offsetX3] * s22;
          

          sum_ = sum_ + round >> precisionBits;
          sum0 = sum0 + round >> precisionBits;
          sum1 = sum1 + round >> precisionBits;
          sum2 = sum2 + round >> precisionBits;
          

          int offsetY = (int)(4L * yfrac);
          long sum = tableDataVi[offsetY] * sum_;
          sum += tableDataVi[(offsetY + 1)] * sum0;
          sum += tableDataVi[(offsetY + 2)] * sum1;
          sum += tableDataVi[(offsetY + 3)] * sum2;
          
          int s = (int)(sum + round >> precisionBits);
          
          dstData[dstPixelOffset] = s;
          dstPixelOffset += dstPixelStride;
        }
        dstScanlineOffset += dstScanlineStride;
      }
    }
  }
  


  private void floatLoop(RasterAccessor src, Rectangle destRect, RasterAccessor dst, int[] xpos, int[] ypos, int[] xfracvalues, int[] yfracvalues)
  {
    int srcPixelStride = src.getPixelStride();
    int srcScanlineStride = src.getScanlineStride();
    
    int dwidth = width;
    int dheight = height;
    int dnumBands = dst.getNumBands();
    float[][] dstDataArrays = dst.getFloatDataArrays();
    int[] dstBandOffsets = dst.getBandOffsets();
    int dstPixelStride = dst.getPixelStride();
    int dstScanlineStride = dst.getScanlineStride();
    
    float[][] srcDataArrays = src.getFloatDataArrays();
    int[] bandOffsets = src.getBandOffsets();
    
    int dstOffset = 0;
    










    for (int k = 0; k < dnumBands; k++) {
      float[] dstData = dstDataArrays[k];
      float[] srcData = srcDataArrays[k];
      int dstScanlineOffset = dstBandOffsets[k];
      int bandOffset = bandOffsets[k];
      
      for (int j = 0; j < dheight; j++) {
        int dstPixelOffset = dstScanlineOffset;
        int yfrac = yfracvalues[j];
        int posy = ypos[j] + bandOffset;
        int posylow = posy - srcScanlineStride;
        int posyhigh = posy + srcScanlineStride;
        int posyhigh2 = posyhigh + srcScanlineStride;
        for (int i = 0; i < dwidth; i++) {
          int xfrac = xfracvalues[i];
          int posx = xpos[i];
          int posxlow = posx - srcPixelStride;
          int posxhigh = posx + srcPixelStride;
          int posxhigh2 = posxhigh + srcPixelStride;
          

          float s__ = srcData[(posxlow + posylow)];
          float s_0 = srcData[(posx + posylow)];
          float s_1 = srcData[(posxhigh + posylow)];
          float s_2 = srcData[(posxhigh2 + posylow)];
          
          float s0_ = srcData[(posxlow + posy)];
          float s00 = srcData[(posx + posy)];
          float s01 = srcData[(posxhigh + posy)];
          float s02 = srcData[(posxhigh2 + posy)];
          
          float s1_ = srcData[(posxlow + posyhigh)];
          float s10 = srcData[(posx + posyhigh)];
          float s11 = srcData[(posxhigh + posyhigh)];
          float s12 = srcData[(posxhigh2 + posyhigh)];
          
          float s2_ = srcData[(posxlow + posyhigh2)];
          float s20 = srcData[(posx + posyhigh2)];
          float s21 = srcData[(posxhigh + posyhigh2)];
          float s22 = srcData[(posxhigh2 + posyhigh2)];
          



          int offsetX = 4 * xfrac;
          int offsetX1 = offsetX + 1;
          int offsetX2 = offsetX + 2;
          int offsetX3 = offsetX + 3;
          
          double sum_ = tableDataHf[offsetX] * s__;
          sum_ += tableDataHf[offsetX1] * s_0;
          sum_ += tableDataHf[offsetX2] * s_1;
          sum_ += tableDataHf[offsetX3] * s_2;
          
          double sum0 = tableDataHf[offsetX] * s0_;
          sum0 += tableDataHf[offsetX1] * s00;
          sum0 += tableDataHf[offsetX2] * s01;
          sum0 += tableDataHf[offsetX3] * s02;
          
          double sum1 = tableDataHf[offsetX] * s1_;
          sum1 += tableDataHf[offsetX1] * s10;
          sum1 += tableDataHf[offsetX2] * s11;
          sum1 += tableDataHf[offsetX3] * s12;
          
          double sum2 = tableDataHf[offsetX] * s2_;
          sum2 += tableDataHf[offsetX1] * s20;
          sum2 += tableDataHf[offsetX2] * s21;
          sum2 += tableDataHf[offsetX3] * s22;
          

          int offsetY = 4 * yfrac;
          
          double sum = tableDataVf[offsetY] * sum_;
          sum += tableDataVf[(offsetY + 1)] * sum0;
          sum += tableDataVf[(offsetY + 2)] * sum1;
          sum += tableDataVf[(offsetY + 3)] * sum2;
          
          if (sum > 3.4028234663852886E38D) {
            sum = 3.4028234663852886E38D;
          } else if (sum < -3.4028234663852886E38D) {
            sum = -3.4028234663852886E38D;
          }
          
          dstData[dstPixelOffset] = ((float)sum);
          dstPixelOffset += dstPixelStride;
        }
        dstScanlineOffset += dstScanlineStride;
      }
    }
  }
  


  private void doubleLoop(RasterAccessor src, Rectangle destRect, RasterAccessor dst, int[] xpos, int[] ypos, int[] xfracvalues, int[] yfracvalues)
  {
    int srcPixelStride = src.getPixelStride();
    int srcScanlineStride = src.getScanlineStride();
    
    int dwidth = width;
    int dheight = height;
    int dnumBands = dst.getNumBands();
    double[][] dstDataArrays = dst.getDoubleDataArrays();
    int[] dstBandOffsets = dst.getBandOffsets();
    int dstPixelStride = dst.getPixelStride();
    int dstScanlineStride = dst.getScanlineStride();
    
    double[][] srcDataArrays = src.getDoubleDataArrays();
    int[] bandOffsets = src.getBandOffsets();
    
    int dstOffset = 0;
    










    for (int k = 0; k < dnumBands; k++) {
      double[] dstData = dstDataArrays[k];
      double[] srcData = srcDataArrays[k];
      int dstScanlineOffset = dstBandOffsets[k];
      int bandOffset = bandOffsets[k];
      
      for (int j = 0; j < dheight; j++) {
        int dstPixelOffset = dstScanlineOffset;
        int yfrac = yfracvalues[j];
        int posy = ypos[j] + bandOffset;
        int posylow = posy - srcScanlineStride;
        int posyhigh = posy + srcScanlineStride;
        int posyhigh2 = posyhigh + srcScanlineStride;
        for (int i = 0; i < dwidth; i++) {
          int xfrac = xfracvalues[i];
          int posx = xpos[i];
          int posxlow = posx - srcPixelStride;
          int posxhigh = posx + srcPixelStride;
          int posxhigh2 = posxhigh + srcPixelStride;
          

          double s__ = srcData[(posxlow + posylow)];
          double s_0 = srcData[(posx + posylow)];
          double s_1 = srcData[(posxhigh + posylow)];
          double s_2 = srcData[(posxhigh2 + posylow)];
          
          double s0_ = srcData[(posxlow + posy)];
          double s00 = srcData[(posx + posy)];
          double s01 = srcData[(posxhigh + posy)];
          double s02 = srcData[(posxhigh2 + posy)];
          
          double s1_ = srcData[(posxlow + posyhigh)];
          double s10 = srcData[(posx + posyhigh)];
          double s11 = srcData[(posxhigh + posyhigh)];
          double s12 = srcData[(posxhigh2 + posyhigh)];
          
          double s2_ = srcData[(posxlow + posyhigh2)];
          double s20 = srcData[(posx + posyhigh2)];
          double s21 = srcData[(posxhigh + posyhigh2)];
          double s22 = srcData[(posxhigh2 + posyhigh2)];
          



          int offsetX = 4 * xfrac;
          int offsetX1 = offsetX + 1;
          int offsetX2 = offsetX + 2;
          int offsetX3 = offsetX + 3;
          
          double sum_ = tableDataHd[offsetX] * s__;
          sum_ += tableDataHd[offsetX1] * s_0;
          sum_ += tableDataHd[offsetX2] * s_1;
          sum_ += tableDataHd[offsetX3] * s_2;
          
          double sum0 = tableDataHd[offsetX] * s0_;
          sum0 += tableDataHd[offsetX1] * s00;
          sum0 += tableDataHd[offsetX2] * s01;
          sum0 += tableDataHd[offsetX3] * s02;
          
          double sum1 = tableDataHd[offsetX] * s1_;
          sum1 += tableDataHd[offsetX1] * s10;
          sum1 += tableDataHd[offsetX2] * s11;
          sum1 += tableDataHd[offsetX3] * s12;
          
          double sum2 = tableDataHd[offsetX] * s2_;
          sum2 += tableDataHd[offsetX1] * s20;
          sum2 += tableDataHd[offsetX2] * s21;
          sum2 += tableDataHd[offsetX3] * s22;
          

          int offsetY = 4 * yfrac;
          double s = tableDataVd[offsetY] * sum_;
          s += tableDataVd[(offsetY + 1)] * sum0;
          s += tableDataVd[(offsetY + 2)] * sum1;
          s += tableDataVd[(offsetY + 3)] * sum2;
          
          dstData[dstPixelOffset] = s;
          dstPixelOffset += dstPixelStride;
        }
        dstScanlineOffset += dstScanlineStride;
      }
    }
  }
  
  private synchronized void initTableDataI() {
    if ((tableDataHi == null) || (tableDataVi == null)) {
      tableDataHi = interpTable.getHorizontalTableData();
      tableDataVi = interpTable.getVerticalTableData();
    }
  }
  
  private synchronized void initTableDataF() {
    if ((tableDataHf == null) || (tableDataVf == null)) {
      tableDataHf = interpTable.getHorizontalTableDataFloat();
      tableDataVf = interpTable.getVerticalTableDataFloat();
    }
  }
  
  private synchronized void initTableDataD() {
    if ((tableDataHd == null) || (tableDataVd == null)) {
      tableDataHd = interpTable.getHorizontalTableDataDouble();
      tableDataVd = interpTable.getVerticalTableDataDouble();
    }
  }
}
