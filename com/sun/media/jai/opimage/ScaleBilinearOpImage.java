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
import javax.media.jai.PlanarImage;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFormatTag;
import javax.media.jai.ScaleOpImage;





















final class ScaleBilinearOpImage
  extends ScaleOpImage
{
  private int subsampleBits;
  int one;
  int shift2;
  int round2;
  Rational half = new Rational(1L, 2L);
  



  long invScaleYInt;
  



  long invScaleYFrac;
  



  long invScaleXInt;
  


  long invScaleXFrac;
  



  public ScaleBilinearOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, float xScale, float yScale, float xTrans, float yTrans, Interpolation interp)
  {
    super(source, layout, config, true, extender, interp, xScale, yScale, xTrans, yTrans);
    









    subsampleBits = interp.getSubsampleBitsH();
    

    one = (1 << subsampleBits);
    

    shift2 = (2 * subsampleBits);
    round2 = (1 << shift2 - 1);
    
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
    
    RasterAccessor srcAccessor = new RasterAccessor(source, srcRect, formatTags[0], getSource(0).getColorModel());
    


    RasterAccessor dstAccessor = new RasterAccessor(dest, destRect, formatTags[1], getColorModel());
    

    int dwidth = width;
    int dheight = height;
    int srcPixelStride = srcAccessor.getPixelStride();
    int srcScanlineStride = srcAccessor.getScanlineStride();
    
    int[] ypos = new int[dheight];
    int[] xpos = new int[dwidth];
    
    int[] xfracvalues = null;int[] yfracvalues = null;
    float[] xfracvaluesFloat = null;float[] yfracvaluesFloat = null;
    
    switch (dstAccessor.getDataType()) {
    case 0: 
    case 1: 
    case 2: 
    case 3: 
      yfracvalues = new int[dheight];
      xfracvalues = new int[dwidth];
      preComputePositionsInt(destRect, x, y, srcPixelStride, srcScanlineStride, xpos, ypos, xfracvalues, yfracvalues);
      

      break;
    
    case 4: 
    case 5: 
      yfracvaluesFloat = new float[dheight];
      xfracvaluesFloat = new float[dwidth];
      preComputePositionsFloat(destRect, x, y, srcPixelStride, srcScanlineStride, xpos, ypos, xfracvaluesFloat, yfracvaluesFloat);
      

      break;
    
    default: 
      throw new RuntimeException(JaiI18N.getString("OrderedDitherOpImage0"));
    }
    
    
    switch (dstAccessor.getDataType()) {
    case 0: 
      byteLoop(srcAccessor, destRect, dstAccessor, xpos, ypos, xfracvalues, yfracvalues);
      
      break;
    
    case 2: 
      shortLoop(srcAccessor, destRect, dstAccessor, xpos, ypos, xfracvalues, yfracvalues);
      
      break;
    
    case 1: 
      ushortLoop(srcAccessor, destRect, dstAccessor, xpos, ypos, xfracvalues, yfracvalues);
      
      break;
    
    case 3: 
      intLoop(srcAccessor, destRect, dstAccessor, xpos, ypos, xfracvalues, yfracvalues);
      
      break;
    
    case 4: 
      floatLoop(srcAccessor, destRect, dstAccessor, xpos, ypos, xfracvaluesFloat, yfracvaluesFloat);
      
      break;
    
    case 5: 
      doubleLoop(srcAccessor, destRect, dstAccessor, xpos, ypos, xfracvaluesFloat, yfracvaluesFloat);
    }
    
    




    if (dstAccessor.isDataCopy()) {
      dstAccessor.clampDataArrays();
      dstAccessor.copyDataToRaster();
    }
  }
  




  private void preComputePositionsInt(Rectangle destRect, int srcRectX, int srcRectY, int srcPixelStride, int srcScanlineStride, int[] xpos, int[] ypos, int[] xfracvalues, int[] yfracvalues)
  {
    int dwidth = width;
    int dheight = height;
    

    int dx = x;
    int dy = y;
    
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
  }
  




  private void preComputePositionsFloat(Rectangle destRect, int srcRectX, int srcRectY, int srcPixelStride, int srcScanlineStride, int[] xpos, int[] ypos, float[] xfracvaluesFloat, float[] yfracvaluesFloat)
  {
    int dwidth = width;
    int dheight = height;
    

    int dx = x;
    int dy = y;
    
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
    
    for (int i = 0; i < dwidth; i++)
    {
      xpos[i] = ((srcXInt - srcRectX) * srcPixelStride);
      xfracvaluesFloat[i] = ((float)srcXFrac / (float)commonXDenom);
      




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
      

      yfracvaluesFloat[i] = ((float)srcYFrac / (float)commonYDenom);
      




      srcYInt = (int)(srcYInt + invScaleYInt);
      


      srcYFrac += newInvScaleYFrac;
      



      if (srcYFrac >= commonYDenom) {
        srcYInt++;
        srcYFrac -= commonYDenom;
      }
    }
  }
  



  private void byteLoop(RasterAccessor src, Rectangle dstRect, RasterAccessor dst, int[] xpos, int[] ypos, int[] xfracvalues, int[] yfracvalues)
  {
    int srcPixelStride = src.getPixelStride();
    int srcScanlineStride = src.getScanlineStride();
    int srcLastXDataPos = (src.getWidth() - 1) * srcPixelStride;
    
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
      for (int j = 0; j < dheight; j++)
      {
        int dstPixelOffset = dstScanlineOffset;
        int yfrac = yfracvalues[j];
        int posylow = ypos[j] + bandOffset;
        int posyhigh = posylow + srcScanlineStride;
        
        for (int i = 0; i < dwidth; i++) {
          int xfrac = xfracvalues[i];
          int posxlow = xpos[i];
          int posxhigh = posxlow + srcPixelStride;
          

          int s00 = srcData[(posxlow + posylow)] & 0xFF;
          int s01 = srcData[(posxhigh + posylow)] & 0xFF;
          int s10 = srcData[(posxlow + posyhigh)] & 0xFF;
          int s11 = srcData[(posxhigh + posyhigh)] & 0xFF;
          

          int s0 = (s01 - s00) * xfrac + (s00 << subsampleBits);
          int s1 = (s11 - s10) * xfrac + (s10 << subsampleBits);
          int s = (s1 - s0) * yfrac + (s0 << subsampleBits) + round2 >> shift2;
          

          dstData[dstPixelOffset] = ((byte)(s & 0xFF));
          dstPixelOffset += dstPixelStride;
        }
        dstScanlineOffset += dstScanlineStride;
      }
    }
  }
  



  private void shortLoop(RasterAccessor src, Rectangle dstRect, RasterAccessor dst, int[] xpos, int[] ypos, int[] xfracvalues, int[] yfracvalues)
  {
    int srcPixelStride = src.getPixelStride();
    int srcScanlineStride = src.getScanlineStride();
    int srcLastXDataPos = (src.getWidth() - 1) * srcPixelStride;
    
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
        int posylow = ypos[j] + bandOffset;
        int posyhigh = posylow + srcScanlineStride;
        
        for (int i = 0; i < dwidth; i++) {
          int xfrac = xfracvalues[i];
          int posxlow = xpos[i];
          int posxhigh = posxlow + srcPixelStride;
          

          int s00 = srcData[(posxlow + posylow)];
          int s01 = srcData[(posxhigh + posylow)];
          int s10 = srcData[(posxlow + posyhigh)];
          int s11 = srcData[(posxhigh + posyhigh)];
          

          int s0 = (s01 - s00) * xfrac + (s00 << subsampleBits);
          int s1 = (s11 - s10) * xfrac + (s10 << subsampleBits);
          int s = (s1 - s0) * yfrac + (s0 << subsampleBits) + round2 >> shift2;
          

          dstData[dstPixelOffset] = ((short)s);
          dstPixelOffset += dstPixelStride;
        }
        dstScanlineOffset += dstScanlineStride;
      }
    }
  }
  


  private void ushortLoop(RasterAccessor src, Rectangle dstRect, RasterAccessor dst, int[] xpos, int[] ypos, int[] xfracvalues, int[] yfracvalues)
  {
    int srcPixelStride = src.getPixelStride();
    int srcScanlineStride = src.getScanlineStride();
    int srcLastXDataPos = (src.getWidth() - 1) * srcPixelStride;
    
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
        int posylow = ypos[j] + bandOffset;
        int posyhigh = posylow + srcScanlineStride;
        
        for (int i = 0; i < dwidth; i++) {
          int xfrac = xfracvalues[i];
          int posxlow = xpos[i];
          int posxhigh = posxlow + srcPixelStride;
          

          int s00 = srcData[(posxlow + posylow)] & 0xFFFF;
          int s01 = srcData[(posxhigh + posylow)] & 0xFFFF;
          int s10 = srcData[(posxlow + posyhigh)] & 0xFFFF;
          int s11 = srcData[(posxhigh + posyhigh)] & 0xFFFF;
          

          int s0 = (s01 - s00) * xfrac + (s00 << subsampleBits);
          int s1 = (s11 - s10) * xfrac + (s10 << subsampleBits);
          int s = (s1 - s0) * yfrac + (s0 << subsampleBits) + round2 >> shift2;
          

          dstData[dstPixelOffset] = ((short)(s & 0xFFFF));
          dstPixelOffset += dstPixelStride;
        }
        dstScanlineOffset += dstScanlineStride;
      }
    }
  }
  




  private void intLoop(RasterAccessor src, Rectangle dstRect, RasterAccessor dst, int[] xpos, int[] ypos, int[] xfracvalues, int[] yfracvalues)
  {
    int srcPixelStride = src.getPixelStride();
    int srcScanlineStride = src.getScanlineStride();
    int srcLastXDataPos = (src.getWidth() - 1) * srcPixelStride;
    
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
    



    int shift = 29 - subsampleBits;
    

    for (int k = 0; k < dnumBands; k++) {
      int[] dstData = dstDataArrays[k];
      int[] srcData = srcDataArrays[k];
      int dstScanlineOffset = dstBandOffsets[k];
      int bandOffset = bandOffsets[k];
      for (int j = 0; j < dheight; j++) {
        int dstPixelOffset = dstScanlineOffset;
        int yfrac = yfracvalues[j];
        int posylow = ypos[j] + bandOffset;
        int posyhigh = posylow + srcScanlineStride;
        
        for (int i = 0; i < dwidth; i++) {
          int xfrac = xfracvalues[i];
          int posxlow = xpos[i];
          int posxhigh = posxlow + srcPixelStride;
          

          int s00 = srcData[(posxlow + posylow)];
          int s01 = srcData[(posxhigh + posylow)];
          int s10 = srcData[(posxlow + posyhigh)];
          int s11 = srcData[(posxhigh + posyhigh)];
          long s1;
          long s0;
          long s1; if ((s00 | s10) >>> shift == 0) { long s1;
            if ((s01 | s11) >>> shift == 0) {
              long s0 = (s01 - s00) * xfrac + (s00 << subsampleBits);
              s1 = (s11 - s10) * xfrac + (s10 << subsampleBits);
            } else {
              long s0 = (s01 - s00) * xfrac + (s00 << subsampleBits);
              s1 = (s11 - s10) * xfrac + (s10 << subsampleBits);
            }
          } else {
            s0 = (s01 - s00) * xfrac + (s00 << subsampleBits);
            s1 = (s11 - s10) * xfrac + (s10 << subsampleBits);
          }
          
          dstData[dstPixelOffset] = ((int)((s1 - s0) * yfrac + (s0 << subsampleBits) + round2 >> shift2));
          


          dstPixelOffset += dstPixelStride;
        }
        dstScanlineOffset += dstScanlineStride;
      }
    }
  }
  









  private void floatLoop(RasterAccessor src, Rectangle dstRect, RasterAccessor dst, int[] xpos, int[] ypos, float[] xfracvaluesFloat, float[] yfracvaluesFloat)
  {
    int srcPixelStride = src.getPixelStride();
    int srcScanlineStride = src.getScanlineStride();
    int srcLastXDataPos = (src.getWidth() - 1) * srcPixelStride;
    
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
        float yfrac = yfracvaluesFloat[j];
        int posylow = ypos[j] + bandOffset;
        int posyhigh = posylow + srcScanlineStride;
        
        for (int i = 0; i < dwidth; i++) {
          float xfrac = xfracvaluesFloat[i];
          int posxlow = xpos[i];
          int posxhigh = posxlow + srcPixelStride;
          

          float s00 = srcData[(posxlow + posylow)];
          float s01 = srcData[(posxhigh + posylow)];
          float s10 = srcData[(posxlow + posyhigh)];
          float s11 = srcData[(posxhigh + posyhigh)];
          

          float s0 = (s01 - s00) * xfrac + s00;
          float s1 = (s11 - s10) * xfrac + s10;
          
          dstData[dstPixelOffset] = ((s1 - s0) * yfrac + s0);
          
          dstPixelOffset += dstPixelStride;
        }
        dstScanlineOffset += dstScanlineStride;
      }
    }
  }
  


  private void doubleLoop(RasterAccessor src, Rectangle dstRect, RasterAccessor dst, int[] xpos, int[] ypos, float[] xfracvaluesFloat, float[] yfracvaluesFloat)
  {
    int srcPixelStride = src.getPixelStride();
    int srcScanlineStride = src.getScanlineStride();
    int srcLastXDataPos = (src.getWidth() - 1) * srcPixelStride;
    
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
        double yfrac = yfracvaluesFloat[j];
        int posylow = ypos[j] + bandOffset;
        int posyhigh = posylow + srcScanlineStride;
        
        for (int i = 0; i < dwidth; i++) {
          double xfrac = xfracvaluesFloat[i];
          int posxlow = xpos[i];
          int posxhigh = posxlow + srcPixelStride;
          

          double s00 = srcData[(posxlow + posylow)];
          double s01 = srcData[(posxhigh + posylow)];
          double s10 = srcData[(posxlow + posyhigh)];
          double s11 = srcData[(posxhigh + posyhigh)];
          

          double s0 = (s01 - s00) * xfrac + s00;
          double s1 = (s11 - s10) * xfrac + s10;
          
          dstData[dstPixelOffset] = ((s1 - s0) * yfrac + s0);
          
          dstPixelOffset += dstPixelStride;
        }
        dstScanlineOffset += dstScanlineStride;
      }
    }
  }
}
