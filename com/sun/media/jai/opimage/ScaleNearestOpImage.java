package com.sun.media.jai.opimage;

import com.sun.media.jai.util.Rational;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import javax.media.jai.PlanarImage;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFormatTag;
import javax.media.jai.ScaleOpImage;



































final class ScaleNearestOpImage
  extends ScaleOpImage
{
  long invScaleXInt;
  long invScaleXFrac;
  long invScaleYInt;
  long invScaleYFrac;
  
  public ScaleNearestOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, float xScale, float yScale, float xTrans, float yTrans, Interpolation interp)
  {
    super(source, layout, config, true, extender, interp, xScale, yScale, xTrans, yTrans);
    













    ColorModel srcColorModel = source.getColorModel();
    if ((srcColorModel instanceof IndexColorModel)) {
      sampleModel = source.getSampleModel().createCompatibleSampleModel(tileWidth, tileHeight);
      
      colorModel = srcColorModel;
    }
    
    if (invScaleXRational.num > invScaleXRational.denom) {
      invScaleXInt = (invScaleXRational.num / invScaleXRational.denom);
      invScaleXFrac = (invScaleXRational.num % invScaleXRational.denom);
    } else {
      invScaleXInt = 0L;
      invScaleXFrac = invScaleXRational.num;
    }
    
    if (invScaleYRational.num > invScaleYRational.denom) {
      invScaleYInt = (invScaleYRational.num / invScaleYRational.denom);
      invScaleYFrac = (invScaleYRational.num % invScaleYRational.denom);
    } else {
      invScaleYInt = 0L;
      invScaleYFrac = invScaleYRational.num;
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
    

    int srcScanlineStride = srcAccessor.getScanlineStride();
    int srcPixelStride = srcAccessor.getPixelStride();
    

    int dx = x;
    int dy = y;
    int dwidth = width;
    int dheight = height;
    

    int[] xvalues = new int[dwidth];
    
    long sxNum = dx;long sxDenom = 1L;
    

    sxNum = sxNum * transXRationalDenom - transXRationalNum * sxDenom;
    sxDenom *= transXRationalDenom;
    

    sxNum = 2L * sxNum + sxDenom;
    sxDenom *= 2L;
    

    sxNum *= invScaleXRationalNum;
    sxDenom *= invScaleXRationalDenom;
    


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

      xvalues[i] = ((srcXInt - srcRectX) * srcPixelStride);
      




      srcXInt = (int)(srcXInt + invScaleXInt);
      


      srcXFrac += newInvScaleXFrac;
      



      if (srcXFrac >= commonXDenom) {
        srcXInt++;
        srcXFrac -= commonXDenom;
      }
    }
    

    int[] yvalues = new int[dheight];
    
    long syNum = dy;long syDenom = 1L;
    

    syNum = syNum * transYRationalDenom - transYRationalNum * syDenom;
    syDenom *= transYRationalDenom;
    

    syNum = 2L * syNum + syDenom;
    syDenom *= 2L;
    

    syNum *= invScaleYRationalNum;
    syDenom *= invScaleYRationalDenom;
    

    int srcYInt = Rational.floor(syNum, syDenom);
    long srcYFrac = syNum % syDenom;
    if (srcYInt < 0) {
      srcYFrac = syDenom + srcYFrac;
    }
    


    long commonYDenom = syDenom * invScaleYRationalDenom;
    srcYFrac *= invScaleYRationalDenom;
    long newInvScaleYFrac = invScaleYFrac * syDenom;
    
    for (int i = 0; i < dheight; i++)
    {

      yvalues[i] = ((srcYInt - srcRectY) * srcScanlineStride);
      




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
      byteLoop(srcAccessor, destRect, dstAccessor, xvalues, yvalues);
      break;
    
    case 1: 
    case 2: 
      shortLoop(srcAccessor, destRect, dstAccessor, xvalues, yvalues);
      break;
    
    case 3: 
      intLoop(srcAccessor, destRect, dstAccessor, xvalues, yvalues);
      break;
    
    case 4: 
      floatLoop(srcAccessor, destRect, dstAccessor, xvalues, yvalues);
      break;
    
    case 5: 
      doubleLoop(srcAccessor, destRect, dstAccessor, xvalues, yvalues);
      break;
    
    default: 
      throw new RuntimeException(JaiI18N.getString("OrderedDitherOpImage0"));
    }
    
    



    if (dstAccessor.isDataCopy()) {
      dstAccessor.clampDataArrays();
      dstAccessor.copyDataToRaster();
    }
  }
  

  private void byteLoop(RasterAccessor src, Rectangle dstRect, RasterAccessor dst, int[] xvalues, int[] yvalues)
  {
    int dwidth = width;
    int dheight = height;
    

    byte[][] dstDataArrays = dst.getByteDataArrays();
    int[] dstBandOffsets = dst.getBandOffsets();
    int dstPixelStride = dst.getPixelStride();
    int dstScanlineStride = dst.getScanlineStride();
    int dnumBands = dst.getNumBands();
    

    int[] bandOffsets = src.getBandOffsets();
    byte[][] srcDataArrays = src.getByteDataArrays();
    

    int dstOffset = 0;
    



    for (int k = 0; k < dnumBands; k++) {
      byte[] dstData = dstDataArrays[k];
      byte[] srcData = srcDataArrays[k];
      int bandOffset = bandOffsets[k];
      int dstScanlineOffset = dstBandOffsets[k];
      for (int j = 0; j < dheight; j++) {
        int dstPixelOffset = dstScanlineOffset;
        int posy = yvalues[j] + bandOffset;
        for (int i = 0; i < dwidth; i++) {
          int posx = xvalues[i];
          int pos = posx + posy;
          dstData[dstPixelOffset] = srcData[pos];
          dstPixelOffset += dstPixelStride;
        }
        dstScanlineOffset += dstScanlineStride;
      }
    }
  }
  

  private void shortLoop(RasterAccessor src, Rectangle dstRect, RasterAccessor dst, int[] xvalues, int[] yvalues)
  {
    int dwidth = width;
    int dheight = height;
    

    short[][] dstDataArrays = dst.getShortDataArrays();
    int[] dstBandOffsets = dst.getBandOffsets();
    int dstPixelStride = dst.getPixelStride();
    int dstScanlineStride = dst.getScanlineStride();
    int dnumBands = dst.getNumBands();
    

    int[] bandOffsets = src.getBandOffsets();
    short[][] srcDataArrays = src.getShortDataArrays();
    

    int dstOffset = 0;
    



    for (int k = 0; k < dnumBands; k++) {
      short[] dstData = dstDataArrays[k];
      short[] srcData = srcDataArrays[k];
      int bandOffset = bandOffsets[k];
      int dstScanlineOffset = dstBandOffsets[k];
      for (int j = 0; j < dheight; j++) {
        int dstPixelOffset = dstScanlineOffset;
        int posy = yvalues[j] + bandOffset;
        for (int i = 0; i < dwidth; i++) {
          int posx = xvalues[i];
          int pos = posx + posy;
          dstData[dstPixelOffset] = srcData[pos];
          dstPixelOffset += dstPixelStride;
        }
        dstScanlineOffset += dstScanlineStride;
      }
    }
  }
  



  private void intLoop(RasterAccessor src, Rectangle dstRect, RasterAccessor dst, int[] xvalues, int[] yvalues)
  {
    int dwidth = width;
    int dheight = height;
    
    int dnumBands = dst.getNumBands();
    int[][] dstDataArrays = dst.getIntDataArrays();
    int[] dstBandOffsets = dst.getBandOffsets();
    int dstPixelStride = dst.getPixelStride();
    int dstScanlineStride = dst.getScanlineStride();
    
    int[] bandOffsets = src.getBandOffsets();
    int[][] srcDataArrays = src.getIntDataArrays();
    

    int dstOffset = 0;
    



    for (int k = 0; k < dnumBands; k++) {
      int[] dstData = dstDataArrays[k];
      int[] srcData = srcDataArrays[k];
      int bandOffset = bandOffsets[k];
      int dstScanlineOffset = dstBandOffsets[k];
      for (int j = 0; j < dheight; j++) {
        int dstPixelOffset = dstScanlineOffset;
        int posy = yvalues[j] + bandOffset;
        for (int i = 0; i < dwidth; i++) {
          int posx = xvalues[i];
          int pos = posx + posy;
          dstData[dstPixelOffset] = srcData[pos];
          dstPixelOffset += dstPixelStride;
        }
        dstScanlineOffset += dstScanlineStride;
      }
    }
  }
  



  private void floatLoop(RasterAccessor src, Rectangle dstRect, RasterAccessor dst, int[] xvalues, int[] yvalues)
  {
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
      int bandOffset = bandOffsets[k];
      int dstScanlineOffset = dstBandOffsets[k];
      for (int j = 0; j < dheight; j++) {
        int dstPixelOffset = dstScanlineOffset;
        int posy = yvalues[j] + bandOffset;
        for (int i = 0; i < dwidth; i++) {
          int posx = xvalues[i];
          int pos = posx + posy;
          dstData[dstPixelOffset] = srcData[pos];
          dstPixelOffset += dstPixelStride;
        }
        dstScanlineOffset += dstScanlineStride;
      }
    }
  }
  



  private void doubleLoop(RasterAccessor src, Rectangle dstRect, RasterAccessor dst, int[] xvalues, int[] yvalues)
  {
    int dwidth = width;
    int dheight = height;
    
    int dnumBands = dst.getNumBands();
    double[][] dstDataArrays = dst.getDoubleDataArrays();
    int[] dstBandOffsets = dst.getBandOffsets();
    int dstPixelStride = dst.getPixelStride();
    int dstScanlineStride = dst.getScanlineStride();
    
    int[] bandOffsets = src.getBandOffsets();
    double[][] srcDataArrays = src.getDoubleDataArrays();
    

    int dstOffset = 0;
    



    for (int k = 0; k < dnumBands; k++) {
      double[] dstData = dstDataArrays[k];
      double[] srcData = srcDataArrays[k];
      int bandOffset = bandOffsets[k];
      int dstScanlineOffset = dstBandOffsets[k];
      for (int j = 0; j < dheight; j++) {
        int dstPixelOffset = dstScanlineOffset;
        int posy = yvalues[j] + bandOffset;
        for (int i = 0; i < dwidth; i++) {
          int posx = xvalues[i];
          int pos = posx + posy;
          dstData[dstPixelOffset] = srcData[pos];
          dstPixelOffset += dstPixelStride;
        }
        dstScanlineOffset += dstScanlineStride;
      }
    }
  }
}
