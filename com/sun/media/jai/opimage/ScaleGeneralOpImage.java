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





















final class ScaleGeneralOpImage
  extends ScaleOpImage
{
  private int subsampleBits;
  private int one;
  Rational half = new Rational(1L, 2L);
  


  private int interp_width;
  


  private int interp_height;
  

  private int interp_left;
  

  private int interp_top;
  

  long invScaleYInt;
  

  long invScaleYFrac;
  

  long invScaleXInt;
  

  long invScaleXFrac;
  


  public ScaleGeneralOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, float xScale, float yScale, float xTrans, float yTrans, Interpolation interp)
  {
    super(source, layout, config, true, extender, interp, xScale, yScale, xTrans, yTrans);
    









    subsampleBits = interp.getSubsampleBitsH();
    

    one = (1 << subsampleBits);
    

    interp_width = interp.getWidth();
    interp_height = interp.getHeight();
    interp_left = interp.getLeftPadding();
    interp_top = interp.getTopPadding();
    
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
    
    
    switch (dstAccessor.getDataType())
    {
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
      
      break;
    
    default: 
      throw new RuntimeException(JaiI18N.getString("OrderedDitherOpImage0"));
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
    

    int[][] samples = new int[interp_height][interp_width];
    




    for (int k = 0; k < dnumBands; k++) {
      byte[] dstData = dstDataArrays[k];
      byte[] srcData = srcDataArrays[k];
      int dstScanlineOffset = dstBandOffsets[k];
      int bandOffset = bandOffsets[k];
      
      for (int j = 0; j < dheight; j++)
      {
        int dstPixelOffset = dstScanlineOffset;
        int yfrac = yfracvalues[j];
        
        int posy = ypos[j] + bandOffset;
        
        for (int i = 0; i < dwidth; i++) {
          int xfrac = xfracvalues[i];
          int posx = xpos[i];
          



          int start = interp_left * srcPixelStride + interp_top * srcScanlineStride;
          
          start = posx + posy - start;
          int countH = 0;int countV = 0;
          
          for (int yloop = 0; yloop < interp_height; yloop++)
          {
            int startY = start;
            
            for (int xloop = 0; xloop < interp_width; xloop++) {
              samples[countV][(countH++)] = (srcData[start] & 0xFF);
              start += srcPixelStride;
            }
            
            countV++;
            countH = 0;
            start = startY + srcScanlineStride;
          }
          

          int s = interp.interpolate(samples, xfrac, yfrac);
          

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
    

    int[][] samples = new int[interp_height][interp_width];
    





    for (int k = 0; k < dnumBands; k++) {
      short[] dstData = dstDataArrays[k];
      short[] srcData = srcDataArrays[k];
      int dstScanlineOffset = dstBandOffsets[k];
      int bandOffset = bandOffsets[k];
      
      for (int j = 0; j < dheight; j++)
      {
        int dstPixelOffset = dstScanlineOffset;
        int yfrac = yfracvalues[j];
        int posy = ypos[j] + bandOffset;
        
        for (int i = 0; i < dwidth; i++) {
          int xfrac = xfracvalues[i];
          int posx = xpos[i];
          

          int start = interp_left * srcPixelStride + interp_top * srcScanlineStride;
          
          start = posx + posy - start;
          int countH = 0;int countV = 0;
          
          for (int yloop = 0; yloop < interp_height; yloop++)
          {
            int startY = start;
            
            for (int xloop = 0; xloop < interp_width; xloop++) {
              samples[countV][(countH++)] = srcData[start];
              start += srcPixelStride;
            }
            
            countV++;
            countH = 0;
            start = startY + srcScanlineStride;
          }
          
          int s = interp.interpolate(samples, xfrac, yfrac);
          

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
    

    int[][] samples = new int[interp_height][interp_width];
    




    for (int k = 0; k < dnumBands; k++)
    {
      short[] dstData = dstDataArrays[k];
      short[] srcData = srcDataArrays[k];
      int dstScanlineOffset = dstBandOffsets[k];
      int bandOffset = bandOffsets[k];
      
      for (int j = 0; j < dheight; j++)
      {
        int dstPixelOffset = dstScanlineOffset;
        int yfrac = yfracvalues[j];
        int posy = ypos[j] + bandOffset;
        
        for (int i = 0; i < dwidth; i++)
        {
          int xfrac = xfracvalues[i];
          int posx = xpos[i];
          

          int start = interp_left * srcPixelStride + interp_top * srcScanlineStride;
          
          start = posx + posy - start;
          int countH = 0;int countV = 0;
          for (int yloop = 0; yloop < interp_height; yloop++)
          {
            int startY = start;
            
            for (int xloop = 0; xloop < interp_width; xloop++) {
              samples[countV][(countH++)] = (srcData[start] & 0xFFFF);
              start += srcPixelStride;
            }
            
            countV++;
            countH = 0;
            start = startY + srcScanlineStride;
          }
          
          int s = interp.interpolate(samples, xfrac, yfrac);
          

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
    

    int[][] samples = new int[interp_height][interp_width];
    




    for (int k = 0; k < dnumBands; k++)
    {
      int[] dstData = dstDataArrays[k];
      int[] srcData = srcDataArrays[k];
      int dstScanlineOffset = dstBandOffsets[k];
      int bandOffset = bandOffsets[k];
      
      for (int j = 0; j < dheight; j++)
      {
        int dstPixelOffset = dstScanlineOffset;
        int yfrac = yfracvalues[j];
        int posy = ypos[j] + bandOffset;
        
        for (int i = 0; i < dwidth; i++)
        {
          int xfrac = xfracvalues[i];
          int posx = xpos[i];
          

          int start = interp_left * srcPixelStride + interp_top * srcScanlineStride;
          
          start = posx + posy - start;
          int countH = 0;int countV = 0;
          for (int yloop = 0; yloop < interp_height; yloop++)
          {
            int startY = start;
            
            for (int xloop = 0; xloop < interp_width; xloop++) {
              samples[countV][(countH++)] = srcData[start];
              start += srcPixelStride;
            }
            
            countV++;
            countH = 0;
            start = startY + srcScanlineStride;
          }
          
          int s = interp.interpolate(samples, xfrac, yfrac);
          
          dstData[dstPixelOffset] = s;
          dstPixelOffset += dstPixelStride;
        }
        dstScanlineOffset += dstScanlineStride;
      }
    }
  }
  


  private void floatLoop(RasterAccessor src, Rectangle destRect, RasterAccessor dst, int[] xpos, int[] ypos, float[] xfracvaluesFloat, float[] yfracvaluesFloat)
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
    

    float[][] samples = new float[interp_height][interp_width];
    





    for (int k = 0; k < dnumBands; k++)
    {
      float[] dstData = dstDataArrays[k];
      float[] srcData = srcDataArrays[k];
      int dstScanlineOffset = dstBandOffsets[k];
      int bandOffset = bandOffsets[k];
      
      for (int j = 0; j < dheight; j++)
      {
        int dstPixelOffset = dstScanlineOffset;
        float yfrac = yfracvaluesFloat[j];
        int posy = ypos[j] + bandOffset;
        
        for (int i = 0; i < dwidth; i++)
        {
          float xfrac = xfracvaluesFloat[i];
          int posx = xpos[i];
          

          int start = interp_left * srcPixelStride + interp_top * srcScanlineStride;
          
          start = posx + posy - start;
          int countH = 0;int countV = 0;
          for (int yloop = 0; yloop < interp_height; yloop++)
          {
            int startY = start;
            
            for (int xloop = 0; xloop < interp_width; xloop++) {
              samples[countV][(countH++)] = srcData[start];
              start += srcPixelStride;
            }
            
            countV++;
            countH = 0;
            start = startY + srcScanlineStride;
          }
          
          float s = interp.interpolate(samples, xfrac, yfrac);
          
          if (s > Float.MAX_VALUE) {
            s = Float.MAX_VALUE;
          } else if (s < -3.4028235E38F) {
            s = -3.4028235E38F;
          }
          
          dstData[dstPixelOffset] = s;
          dstPixelOffset += dstPixelStride;
        }
        dstScanlineOffset += dstScanlineStride;
      }
    }
  }
  


  private void doubleLoop(RasterAccessor src, Rectangle destRect, RasterAccessor dst, int[] xpos, int[] ypos, float[] xfracvaluesFloat, float[] yfracvaluesFloat)
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
    

    double[][] samples = new double[interp_height][interp_width];
    





    for (int k = 0; k < dnumBands; k++)
    {
      double[] dstData = dstDataArrays[k];
      double[] srcData = srcDataArrays[k];
      int dstScanlineOffset = dstBandOffsets[k];
      int bandOffset = bandOffsets[k];
      
      for (int j = 0; j < dheight; j++)
      {
        int dstPixelOffset = dstScanlineOffset;
        float yfrac = yfracvaluesFloat[j];
        int posy = ypos[j] + bandOffset;
        
        for (int i = 0; i < dwidth; i++)
        {
          float xfrac = xfracvaluesFloat[i];
          int posx = xpos[i];
          

          int start = interp_left * srcPixelStride + interp_top * srcScanlineStride;
          
          start = posx + posy - start;
          int countH = 0;int countV = 0;
          for (int yloop = 0; yloop < interp_height; yloop++)
          {
            int startY = start;
            
            for (int xloop = 0; xloop < interp_width; xloop++) {
              samples[countV][(countH++)] = srcData[start];
              start += srcPixelStride;
            }
            
            countV++;
            countH = 0;
            start = startY + srcScanlineStride;
          }
          
          double s = interp.interpolate(samples, xfrac, yfrac);
          
          dstData[dstPixelOffset] = s;
          dstPixelOffset += dstPixelStride;
        }
        dstScanlineOffset += dstScanlineStride;
      }
    }
  }
}
