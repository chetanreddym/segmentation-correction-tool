package com.sun.media.jai.opimage;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
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































final class AffineBilinearOpImage
  extends AffineOpImage
{
  public AffineBilinearOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, AffineTransform transform, Interpolation interp, double[] backgroundValues)
  {
    super(source, extender, config, layout, transform, interp, backgroundValues);
  }
  

















  protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect)
  {
    RasterFormatTag[] formatTags = getFormatTags();
    
    Raster source = sources[0];
    
    Rectangle srcRect = source.getBounds();
    
    int srcRectX = x;
    int srcRectY = y;
    







    RasterAccessor srcAccessor = new RasterAccessor(source, srcRect, formatTags[0], getSourceImage(0).getColorModel());
    



    RasterAccessor dstAccessor = new RasterAccessor(dest, destRect, formatTags[1], getColorModel());
    




    switch (dstAccessor.getDataType()) {
    case 0: 
      byteLoop(srcAccessor, destRect, srcRectX, srcRectY, dstAccessor);
      



      break;
    
    case 3: 
      intLoop(srcAccessor, destRect, srcRectX, srcRectY, dstAccessor);
      



      break;
    
    case 2: 
      shortLoop(srcAccessor, destRect, srcRectX, srcRectY, dstAccessor);
      



      break;
    
    case 1: 
      ushortLoop(srcAccessor, destRect, srcRectX, srcRectY, dstAccessor);
      



      break;
    
    case 4: 
      floatLoop(srcAccessor, destRect, srcRectX, srcRectY, dstAccessor);
      



      break;
    
    case 5: 
      doubleLoop(srcAccessor, destRect, srcRectX, srcRectY, dstAccessor);
    }
    
    







    if (dstAccessor.isDataCopy()) {
      dstAccessor.clampDataArrays();
      dstAccessor.copyDataToRaster();
    }
  }
  




  private void byteLoop(RasterAccessor src, Rectangle destRect, int srcRectX, int srcRectY, RasterAccessor dst)
  {
    float src_rect_x1 = src.getX();
    float src_rect_y1 = src.getY();
    float src_rect_x2 = src_rect_x1 + src.getWidth();
    float src_rect_y2 = src_rect_y1 + src.getHeight();
    











    int dstOffset = 0;
    
    Point2D dst_pt = new Point2D.Float();
    Point2D src_pt = new Point2D.Float();
    
    int dwidth = dst.getWidth();
    int dheight = dst.getHeight();
    int dnumBands = dst.getNumBands();
    
    byte[][] dstDataArrays = dst.getByteDataArrays();
    int[] dstBandOffsets = dst.getBandOffsets();
    int dstPixelStride = dst.getPixelStride();
    int dstScanlineStride = dst.getScanlineStride();
    
    byte[][] srcDataArrays = src.getByteDataArrays();
    int[] bandOffsets = src.getBandOffsets();
    int srcPixelStride = src.getPixelStride();
    int srcScanlineStride = src.getScanlineStride();
    
    int dst_num_bands = dst.getNumBands();
    
    int dst_min_x = x;
    int dst_min_y = y;
    int dst_max_x = x + width;
    int dst_max_y = y + height;
    
    byte[] backgroundByte = new byte[dst_num_bands];
    for (int i = 0; i < dst_num_bands; i++) {
      backgroundByte[i] = ((byte)(int)backgroundValues[i]);
    }
    for (int y = dst_min_y; y < dst_max_y; y++)
    {
      int dstPixelOffset = dstOffset;
      


      dst_pt.setLocation(dst_min_x + 0.5D, y + 0.5D);
      
      mapDestPoint(dst_pt, src_pt);
      

      float s_x = (float)src_pt.getX();
      float s_y = (float)src_pt.getY();
      

      s_x = (float)(s_x - 0.5D);
      s_y = (float)(s_y - 0.5D);
      

      int s_ix = (int)Math.floor(s_x);
      int s_iy = (int)Math.floor(s_y);
      
      float fracx = s_x - s_ix;
      float fracy = s_y - s_iy;
      

      int pylow = (s_iy - srcRectY) * srcScanlineStride;
      int pxlow = (s_ix - srcRectX) * srcPixelStride;
      int pyhigh = pylow + srcScanlineStride;
      int pxhigh = pxlow + srcPixelStride;
      
      int tmp00 = pxlow + pylow;
      int tmp01 = pxhigh + pylow;
      int tmp10 = pxlow + pyhigh;
      int tmp11 = pxhigh + pyhigh;
      
      for (int x = dst_min_x; x < dst_max_x; x++)
      {


        if ((s_ix >= src_rect_x1) && (s_ix < src_rect_x2 - 1.0F) && (s_iy >= src_rect_y1) && (s_iy < src_rect_y2 - 1.0F))
        {


          for (int k2 = 0; k2 < dst_num_bands; k2++)
          {






            byte[] tmp_row = srcDataArrays[k2];
            

            int tmp_col = bandOffsets[k2];
            
            int s00 = tmp_row[(tmp00 + tmp_col)] & 0xFF;
            int s01 = tmp_row[(tmp01 + tmp_col)] & 0xFF;
            int s10 = tmp_row[(tmp10 + tmp_col)] & 0xFF;
            int s11 = tmp_row[(tmp11 + tmp_col)] & 0xFF;
            

            float s0 = s00 + (s01 - s00) * fracx;
            float s1 = s10 + (s11 - s10) * fracx;
            
            float tmp = s0 + (s1 - s0) * fracy;
            int s;
            int s;
            if (tmp < 0.5F) {
              s = 0; } else { int s;
              if (tmp > 254.5F) {
                s = 255;
              } else {
                s = (int)(tmp + 0.5F);
              }
            }
            
            dstDataArrays[k2][(dstPixelOffset + dstBandOffsets[k2])] = ((byte)(s & 0xFF));
          }
          
        }
        else if (setBackground) {
          for (int k = 0; k < dst_num_bands; k++) {
            dstDataArrays[k][(dstPixelOffset + dstBandOffsets[k])] = backgroundByte[k];
          }
        }
        

        if (fracx < fracdx1) {
          s_ix += incx;
          fracx = (float)(fracx + fracdx);
        } else {
          s_ix += incx1;
          fracx = (float)(fracx - fracdx1);
        }
        
        if (fracy < fracdy1) {
          s_iy += incy;
          fracy = (float)(fracy + fracdy);
        } else {
          s_iy += incy1;
          fracy = (float)(fracy - fracdy1);
        }
        

        pylow = (s_iy - srcRectY) * srcScanlineStride;
        pxlow = (s_ix - srcRectX) * srcPixelStride;
        pyhigh = pylow + srcScanlineStride;
        pxhigh = pxlow + srcPixelStride;
        
        tmp00 = pxlow + pylow;
        tmp01 = pxhigh + pylow;
        tmp10 = pxlow + pyhigh;
        tmp11 = pxhigh + pyhigh;
        

        dstPixelOffset += dstPixelStride;
      }
      

      dstOffset += dstScanlineStride;
    }
  }
  




  private void intLoop(RasterAccessor src, Rectangle destRect, int srcRectX, int srcRectY, RasterAccessor dst)
  {
    float src_rect_x1 = src.getX();
    float src_rect_y1 = src.getY();
    float src_rect_x2 = src_rect_x1 + src.getWidth();
    float src_rect_y2 = src_rect_y1 + src.getHeight();
    











    int dstOffset = 0;
    
    Point2D dst_pt = new Point2D.Float();
    Point2D src_pt = new Point2D.Float();
    
    int dwidth = dst.getWidth();
    int dheight = dst.getHeight();
    int dnumBands = dst.getNumBands();
    
    int[][] dstDataArrays = dst.getIntDataArrays();
    int[] dstBandOffsets = dst.getBandOffsets();
    int dstPixelStride = dst.getPixelStride();
    int dstScanlineStride = dst.getScanlineStride();
    
    int[][] srcDataArrays = src.getIntDataArrays();
    int[] bandOffsets = src.getBandOffsets();
    int srcPixelStride = src.getPixelStride();
    int srcScanlineStride = src.getScanlineStride();
    
    int dst_num_bands = dst.getNumBands();
    
    int dst_min_x = x;
    int dst_min_y = y;
    int dst_max_x = x + width;
    int dst_max_y = y + height;
    
    int[] backgroundInt = new int[dst_num_bands];
    for (int i = 0; i < dst_num_bands; i++) {
      backgroundInt[i] = ((int)backgroundValues[i]);
    }
    for (int y = dst_min_y; y < dst_max_y; y++)
    {
      int dstPixelOffset = dstOffset;
      

      dst_pt.setLocation(dst_min_x + 0.5D, y + 0.5D);
      
      mapDestPoint(dst_pt, src_pt);
      

      float s_x = (float)src_pt.getX();
      float s_y = (float)src_pt.getY();
      

      s_x = (float)(s_x - 0.5D);
      s_y = (float)(s_y - 0.5D);
      

      int s_ix = (int)Math.floor(s_x);
      int s_iy = (int)Math.floor(s_y);
      
      float fracx = s_x - s_ix;
      float fracy = s_y - s_iy;
      

      int pylow = (s_iy - srcRectY) * srcScanlineStride;
      int pxlow = (s_ix - srcRectX) * srcPixelStride;
      int pyhigh = pylow + srcScanlineStride;
      int pxhigh = pxlow + srcPixelStride;
      
      int tmp00 = pxlow + pylow;
      int tmp01 = pxhigh + pylow;
      int tmp10 = pxlow + pyhigh;
      int tmp11 = pxhigh + pyhigh;
      
      for (int x = dst_min_x; x < dst_max_x; x++)
      {


        if ((s_ix >= src_rect_x1) && (s_ix < src_rect_x2 - 1.0F) && (s_iy >= src_rect_y1) && (s_iy < src_rect_y2 - 1.0F))
        {


          for (int k2 = 0; k2 < dst_num_bands; k2++)
          {






            int[] tmp_row = srcDataArrays[k2];
            

            int tmp_col = bandOffsets[k2];
            
            int s00 = tmp_row[(tmp00 + tmp_col)];
            int s01 = tmp_row[(tmp01 + tmp_col)];
            int s10 = tmp_row[(tmp10 + tmp_col)];
            int s11 = tmp_row[(tmp11 + tmp_col)];
            

            float s0 = s00 + (s01 - s00) * fracx;
            float s1 = s10 + (s11 - s10) * fracx;
            
            float tmp = s0 + (s1 - s0) * fracy;
            int s;
            int s;
            if (tmp < -2.14748365E9F) {
              s = Integer.MIN_VALUE; } else { int s;
              if (tmp > 2.14748365E9F) {
                s = Integer.MAX_VALUE; } else { int s;
                if (tmp > 0.0F) {
                  s = (int)(tmp + 0.5F);
                } else {
                  s = (int)(tmp - 0.5F);
                }
              }
            }
            dstDataArrays[k2][(dstPixelOffset + dstBandOffsets[k2])] = s;
          }
          
        } else if (setBackground) {
          for (int k = 0; k < dst_num_bands; k++) {
            dstDataArrays[k][(dstPixelOffset + dstBandOffsets[k])] = backgroundInt[k];
          }
        }
        

        if (fracx < fracdx1) {
          s_ix += incx;
          fracx = (float)(fracx + fracdx);
        } else {
          s_ix += incx1;
          fracx = (float)(fracx - fracdx1);
        }
        
        if (fracy < fracdy1) {
          s_iy += incy;
          fracy = (float)(fracy + fracdy);
        } else {
          s_iy += incy1;
          fracy = (float)(fracy - fracdy1);
        }
        

        pylow = (s_iy - srcRectY) * srcScanlineStride;
        pxlow = (s_ix - srcRectX) * srcPixelStride;
        pyhigh = pylow + srcScanlineStride;
        pxhigh = pxlow + srcPixelStride;
        
        tmp00 = pxlow + pylow;
        tmp01 = pxhigh + pylow;
        tmp10 = pxlow + pyhigh;
        tmp11 = pxhigh + pyhigh;
        
        dstPixelOffset += dstPixelStride;
      }
      
      dstOffset += dstScanlineStride;
    }
  }
  




  private void shortLoop(RasterAccessor src, Rectangle destRect, int srcRectX, int srcRectY, RasterAccessor dst)
  {
    float src_rect_x1 = src.getX();
    float src_rect_y1 = src.getY();
    float src_rect_x2 = src_rect_x1 + src.getWidth();
    float src_rect_y2 = src_rect_y1 + src.getHeight();
    











    int dstOffset = 0;
    
    Point2D dst_pt = new Point2D.Float();
    Point2D src_pt = new Point2D.Float();
    
    int dwidth = dst.getWidth();
    int dheight = dst.getHeight();
    int dnumBands = dst.getNumBands();
    
    short[][] dstDataArrays = dst.getShortDataArrays();
    int[] dstBandOffsets = dst.getBandOffsets();
    int dstPixelStride = dst.getPixelStride();
    int dstScanlineStride = dst.getScanlineStride();
    
    short[][] srcDataArrays = src.getShortDataArrays();
    int[] bandOffsets = src.getBandOffsets();
    int srcPixelStride = src.getPixelStride();
    int srcScanlineStride = src.getScanlineStride();
    
    int dst_num_bands = dst.getNumBands();
    
    int dst_min_x = x;
    int dst_min_y = y;
    int dst_max_x = x + width;
    int dst_max_y = y + height;
    
    short[] backgroundShort = new short[dst_num_bands];
    for (int i = 0; i < dst_num_bands; i++) {
      backgroundShort[i] = ((short)(int)backgroundValues[i]);
    }
    for (int y = dst_min_y; y < dst_max_y; y++)
    {
      int dstPixelOffset = dstOffset;
      

      dst_pt.setLocation(dst_min_x + 0.5D, y + 0.5D);
      
      mapDestPoint(dst_pt, src_pt);
      

      float s_x = (float)src_pt.getX();
      float s_y = (float)src_pt.getY();
      

      s_x = (float)(s_x - 0.5D);
      s_y = (float)(s_y - 0.5D);
      

      int s_ix = (int)Math.floor(s_x);
      int s_iy = (int)Math.floor(s_y);
      
      float fracx = s_x - s_ix;
      float fracy = s_y - s_iy;
      

      int pylow = (s_iy - srcRectY) * srcScanlineStride;
      int pxlow = (s_ix - srcRectX) * srcPixelStride;
      int pyhigh = pylow + srcScanlineStride;
      int pxhigh = pxlow + srcPixelStride;
      
      int tmp00 = pxlow + pylow;
      int tmp01 = pxhigh + pylow;
      int tmp10 = pxlow + pyhigh;
      int tmp11 = pxhigh + pyhigh;
      
      for (int x = dst_min_x; x < dst_max_x; x++)
      {


        if ((s_ix >= src_rect_x1) && (s_ix < src_rect_x2 - 1.0F) && (s_iy >= src_rect_y1) && (s_iy < src_rect_y2 - 1.0F))
        {


          for (int k2 = 0; k2 < dst_num_bands; k2++)
          {






            short[] tmp_row = srcDataArrays[k2];
            

            int tmp_col = bandOffsets[k2];
            
            int s00 = tmp_row[(tmp00 + tmp_col)];
            int s01 = tmp_row[(tmp01 + tmp_col)];
            int s10 = tmp_row[(tmp10 + tmp_col)];
            int s11 = tmp_row[(tmp11 + tmp_col)];
            

            float s0 = s00 + (s01 - s00) * fracx;
            float s1 = s10 + (s11 - s10) * fracx;
            float tmp = s0 + (s1 - s0) * fracy;
            int s;
            int s;
            if (tmp < -32768.0F) {
              s = 32768; } else { int s;
              if (tmp > 32767.0F) {
                s = 32767; } else { int s;
                if (tmp > 0.0F) {
                  s = (int)(tmp + 0.5F);
                } else {
                  s = (int)(tmp - 0.5F);
                }
              }
            }
            dstDataArrays[k2][(dstPixelOffset + dstBandOffsets[k2])] = ((short)s);
          }
          
        } else if (setBackground) {
          for (int k = 0; k < dst_num_bands; k++) {
            dstDataArrays[k][(dstPixelOffset + dstBandOffsets[k])] = backgroundShort[k];
          }
        }
        

        if (fracx < fracdx1) {
          s_ix += incx;
          fracx = (float)(fracx + fracdx);
        } else {
          s_ix += incx1;
          fracx = (float)(fracx - fracdx1);
        }
        
        if (fracy < fracdy1) {
          s_iy += incy;
          fracy = (float)(fracy + fracdy);
        } else {
          s_iy += incy1;
          fracy = (float)(fracy - fracdy1);
        }
        

        pylow = (s_iy - srcRectY) * srcScanlineStride;
        pxlow = (s_ix - srcRectX) * srcPixelStride;
        pyhigh = pylow + srcScanlineStride;
        pxhigh = pxlow + srcPixelStride;
        
        tmp00 = pxlow + pylow;
        tmp01 = pxhigh + pylow;
        tmp10 = pxlow + pyhigh;
        tmp11 = pxhigh + pyhigh;
        
        dstPixelOffset += dstPixelStride;
      }
      
      dstOffset += dstScanlineStride;
    }
  }
  




  private void ushortLoop(RasterAccessor src, Rectangle destRect, int srcRectX, int srcRectY, RasterAccessor dst)
  {
    float src_rect_x1 = src.getX();
    float src_rect_y1 = src.getY();
    float src_rect_x2 = src_rect_x1 + src.getWidth();
    float src_rect_y2 = src_rect_y1 + src.getHeight();
    











    int dstOffset = 0;
    
    Point2D dst_pt = new Point2D.Float();
    Point2D src_pt = new Point2D.Float();
    
    int dwidth = dst.getWidth();
    int dheight = dst.getHeight();
    int dnumBands = dst.getNumBands();
    
    short[][] dstDataArrays = dst.getShortDataArrays();
    int[] dstBandOffsets = dst.getBandOffsets();
    int dstPixelStride = dst.getPixelStride();
    int dstScanlineStride = dst.getScanlineStride();
    
    short[][] srcDataArrays = src.getShortDataArrays();
    int[] bandOffsets = src.getBandOffsets();
    int srcPixelStride = src.getPixelStride();
    int srcScanlineStride = src.getScanlineStride();
    
    int dst_num_bands = dst.getNumBands();
    
    int dst_min_x = x;
    int dst_min_y = y;
    int dst_max_x = x + width;
    int dst_max_y = y + height;
    
    short[] backgroundUShort = new short[dst_num_bands];
    for (int i = 0; i < dst_num_bands; i++) {
      backgroundUShort[i] = ((short)(int)backgroundValues[i]);
    }
    for (int y = dst_min_y; y < dst_max_y; y++)
    {
      int dstPixelOffset = dstOffset;
      

      dst_pt.setLocation(dst_min_x + 0.5D, y + 0.5D);
      
      mapDestPoint(dst_pt, src_pt);
      

      float s_x = (float)src_pt.getX();
      float s_y = (float)src_pt.getY();
      

      s_x = (float)(s_x - 0.5D);
      s_y = (float)(s_y - 0.5D);
      

      int s_ix = (int)Math.floor(s_x);
      int s_iy = (int)Math.floor(s_y);
      
      float fracx = s_x - s_ix;
      float fracy = s_y - s_iy;
      

      int pylow = (s_iy - srcRectY) * srcScanlineStride;
      int pxlow = (s_ix - srcRectX) * srcPixelStride;
      int pyhigh = pylow + srcScanlineStride;
      int pxhigh = pxlow + srcPixelStride;
      
      int tmp00 = pxlow + pylow;
      int tmp01 = pxhigh + pylow;
      int tmp10 = pxlow + pyhigh;
      int tmp11 = pxhigh + pyhigh;
      
      for (int x = dst_min_x; x < dst_max_x; x++)
      {


        if ((s_ix >= src_rect_x1) && (s_ix < src_rect_x2 - 1.0F) && (s_iy >= src_rect_y1) && (s_iy < src_rect_y2 - 1.0F))
        {


          for (int k2 = 0; k2 < dst_num_bands; k2++)
          {






            short[] tmp_row = srcDataArrays[k2];
            

            int tmp_col = bandOffsets[k2];
            
            int s00 = tmp_row[(tmp00 + tmp_col)] & 0xFFFF;
            int s01 = tmp_row[(tmp01 + tmp_col)] & 0xFFFF;
            int s10 = tmp_row[(tmp10 + tmp_col)] & 0xFFFF;
            int s11 = tmp_row[(tmp11 + tmp_col)] & 0xFFFF;
            

            float s0 = s00 + (s01 - s00) * fracx;
            float s1 = s10 + (s11 - s10) * fracx;
            float tmp = s0 + (s1 - s0) * fracy;
            int s;
            int s;
            if (tmp < 0.0D) {
              s = 0; } else { int s;
              if (tmp > 65535.0F) {
                s = 65535;
              } else {
                s = (int)(tmp + 0.5F);
              }
            }
            
            dstDataArrays[k2][(dstPixelOffset + dstBandOffsets[k2])] = ((short)(s & 0xFFFF));
          }
          
        }
        else if (setBackground) {
          for (int k = 0; k < dst_num_bands; k++) {
            dstDataArrays[k][(dstPixelOffset + dstBandOffsets[k])] = backgroundUShort[k];
          }
        }
        

        if (fracx < fracdx1) {
          s_ix += incx;
          fracx = (float)(fracx + fracdx);
        } else {
          s_ix += incx1;
          fracx = (float)(fracx - fracdx1);
        }
        
        if (fracy < fracdy1) {
          s_iy += incy;
          fracy = (float)(fracy + fracdy);
        } else {
          s_iy += incy1;
          fracy = (float)(fracy - fracdy1);
        }
        

        pylow = (s_iy - srcRectY) * srcScanlineStride;
        pxlow = (s_ix - srcRectX) * srcPixelStride;
        pyhigh = pylow + srcScanlineStride;
        pxhigh = pxlow + srcPixelStride;
        
        tmp00 = pxlow + pylow;
        tmp01 = pxhigh + pylow;
        tmp10 = pxlow + pyhigh;
        tmp11 = pxhigh + pyhigh;
        
        dstPixelOffset += dstPixelStride;
      }
      
      dstOffset += dstScanlineStride;
    }
  }
  




  private void floatLoop(RasterAccessor src, Rectangle destRect, int srcRectX, int srcRectY, RasterAccessor dst)
  {
    float src_rect_x1 = src.getX();
    float src_rect_y1 = src.getY();
    float src_rect_x2 = src_rect_x1 + src.getWidth();
    float src_rect_y2 = src_rect_y1 + src.getHeight();
    










    int dstOffset = 0;
    
    Point2D dst_pt = new Point2D.Float();
    Point2D src_pt = new Point2D.Float();
    
    int dwidth = dst.getWidth();
    int dheight = dst.getHeight();
    int dnumBands = dst.getNumBands();
    
    float[][] dstDataArrays = dst.getFloatDataArrays();
    int[] dstBandOffsets = dst.getBandOffsets();
    int dstPixelStride = dst.getPixelStride();
    int dstScanlineStride = dst.getScanlineStride();
    
    float[][] srcDataArrays = src.getFloatDataArrays();
    int[] bandOffsets = src.getBandOffsets();
    int srcPixelStride = src.getPixelStride();
    int srcScanlineStride = src.getScanlineStride();
    
    int dst_num_bands = dst.getNumBands();
    
    int dst_min_x = x;
    int dst_min_y = y;
    int dst_max_x = x + width;
    int dst_max_y = y + height;
    
    float[] backgroundFloat = new float[dst_num_bands];
    for (int i = 0; i < dst_num_bands; i++) {
      backgroundFloat[i] = ((float)backgroundValues[i]);
    }
    for (int y = dst_min_y; y < dst_max_y; y++)
    {
      int dstPixelOffset = dstOffset;
      

      dst_pt.setLocation(dst_min_x + 0.5D, y + 0.5D);
      
      mapDestPoint(dst_pt, src_pt);
      

      float s_x = (float)src_pt.getX();
      float s_y = (float)src_pt.getY();
      

      s_x = (float)(s_x - 0.5D);
      s_y = (float)(s_y - 0.5D);
      

      int s_ix = (int)Math.floor(s_x);
      int s_iy = (int)Math.floor(s_y);
      
      float fracx = s_x - s_ix;
      float fracy = s_y - s_iy;
      

      int pylow = (s_iy - srcRectY) * srcScanlineStride;
      int pxlow = (s_ix - srcRectX) * srcPixelStride;
      int pyhigh = pylow + srcScanlineStride;
      int pxhigh = pxlow + srcPixelStride;
      
      int tmp00 = pxlow + pylow;
      int tmp01 = pxhigh + pylow;
      int tmp10 = pxlow + pyhigh;
      int tmp11 = pxhigh + pyhigh;
      
      for (int x = dst_min_x; x < dst_max_x; x++)
      {


        if ((s_ix >= src_rect_x1) && (s_ix < src_rect_x2 - 1.0F) && (s_iy >= src_rect_y1) && (s_iy < src_rect_y2 - 1.0F))
        {


          for (int k2 = 0; k2 < dst_num_bands; k2++)
          {






            float[] tmp_row = srcDataArrays[k2];
            

            int tmp_col = bandOffsets[k2];
            
            float s00 = tmp_row[(tmp00 + tmp_col)];
            float s01 = tmp_row[(tmp01 + tmp_col)];
            float s10 = tmp_row[(tmp10 + tmp_col)];
            float s11 = tmp_row[(tmp11 + tmp_col)];
            

            float s0 = s00 + (s01 - s00) * fracx;
            float s1 = s10 + (s11 - s10) * fracx;
            float s = s0 + (s1 - s0) * fracy;
            

            dstDataArrays[k2][(dstPixelOffset + dstBandOffsets[k2])] = s;
          }
          
        } else if (setBackground) {
          for (int k = 0; k < dst_num_bands; k++) {
            dstDataArrays[k][(dstPixelOffset + dstBandOffsets[k])] = backgroundFloat[k];
          }
        }
        

        if (fracx < fracdx1) {
          s_ix += incx;
          fracx = (float)(fracx + fracdx);
        } else {
          s_ix += incx1;
          fracx = (float)(fracx - fracdx1);
        }
        
        if (fracy < fracdy1) {
          s_iy += incy;
          fracy = (float)(fracy + fracdy);
        } else {
          s_iy += incy1;
          fracy = (float)(fracy - fracdy1);
        }
        

        pylow = (s_iy - srcRectY) * srcScanlineStride;
        pxlow = (s_ix - srcRectX) * srcPixelStride;
        pyhigh = pylow + srcScanlineStride;
        pxhigh = pxlow + srcPixelStride;
        
        tmp00 = pxlow + pylow;
        tmp01 = pxhigh + pylow;
        tmp10 = pxlow + pyhigh;
        tmp11 = pxhigh + pyhigh;
        
        dstPixelOffset += dstPixelStride;
      }
      
      dstOffset += dstScanlineStride;
    }
  }
  




  private void doubleLoop(RasterAccessor src, Rectangle destRect, int srcRectX, int srcRectY, RasterAccessor dst)
  {
    float src_rect_x1 = src.getX();
    float src_rect_y1 = src.getY();
    float src_rect_x2 = src_rect_x1 + src.getWidth();
    float src_rect_y2 = src_rect_y1 + src.getHeight();
    










    int dstOffset = 0;
    
    Point2D dst_pt = new Point2D.Float();
    Point2D src_pt = new Point2D.Float();
    
    int dwidth = dst.getWidth();
    int dheight = dst.getHeight();
    int dnumBands = dst.getNumBands();
    
    double[][] dstDataArrays = dst.getDoubleDataArrays();
    int[] dstBandOffsets = dst.getBandOffsets();
    int dstPixelStride = dst.getPixelStride();
    int dstScanlineStride = dst.getScanlineStride();
    
    double[][] srcDataArrays = src.getDoubleDataArrays();
    int[] bandOffsets = src.getBandOffsets();
    int srcPixelStride = src.getPixelStride();
    int srcScanlineStride = src.getScanlineStride();
    
    int dst_num_bands = dst.getNumBands();
    
    int dst_min_x = x;
    int dst_min_y = y;
    int dst_max_x = x + width;
    int dst_max_y = y + height;
    
    for (int y = dst_min_y; y < dst_max_y; y++)
    {
      int dstPixelOffset = dstOffset;
      

      dst_pt.setLocation(dst_min_x + 0.5D, y + 0.5D);
      
      mapDestPoint(dst_pt, src_pt);
      

      float s_x = (float)src_pt.getX();
      float s_y = (float)src_pt.getY();
      

      s_x = (float)(s_x - 0.5D);
      s_y = (float)(s_y - 0.5D);
      

      int s_ix = (int)Math.floor(s_x);
      int s_iy = (int)Math.floor(s_y);
      
      double fracx = s_x - s_ix;
      double fracy = s_y - s_iy;
      

      int pylow = (s_iy - srcRectY) * srcScanlineStride;
      int pxlow = (s_ix - srcRectX) * srcPixelStride;
      int pyhigh = pylow + srcScanlineStride;
      int pxhigh = pxlow + srcPixelStride;
      
      int tmp00 = pxlow + pylow;
      int tmp01 = pxhigh + pylow;
      int tmp10 = pxlow + pyhigh;
      int tmp11 = pxhigh + pyhigh;
      
      for (int x = dst_min_x; x < dst_max_x; x++)
      {


        if ((s_ix >= src_rect_x1) && (s_ix < src_rect_x2 - 1.0F) && (s_iy >= src_rect_y1) && (s_iy < src_rect_y2 - 1.0F))
        {


          for (int k2 = 0; k2 < dst_num_bands; k2++)
          {






            double[] tmp_row = srcDataArrays[k2];
            

            int tmp_col = bandOffsets[k2];
            
            double s00 = tmp_row[(tmp00 + tmp_col)];
            double s01 = tmp_row[(tmp01 + tmp_col)];
            double s10 = tmp_row[(tmp10 + tmp_col)];
            double s11 = tmp_row[(tmp11 + tmp_col)];
            

            double s0 = s00 + (s01 - s00) * fracx;
            double s1 = s10 + (s11 - s10) * fracx;
            double s = s0 + (s1 - s0) * fracy;
            

            dstDataArrays[k2][(dstPixelOffset + dstBandOffsets[k2])] = s;
          }
          
        } else if (setBackground) {
          for (int k = 0; k < dst_num_bands; k++) {
            dstDataArrays[k][(dstPixelOffset + dstBandOffsets[k])] = backgroundValues[k];
          }
        }
        

        if (fracx < fracdx1) {
          s_ix += incx;
          fracx += fracdx;
        } else {
          s_ix += incx1;
          fracx -= fracdx1;
        }
        
        if (fracy < fracdy1) {
          s_iy += incy;
          fracy += fracdy;
        } else {
          s_iy += incy1;
          fracy -= fracdy1;
        }
        

        pylow = (s_iy - srcRectY) * srcScanlineStride;
        pxlow = (s_ix - srcRectX) * srcPixelStride;
        pyhigh = pylow + srcScanlineStride;
        pxhigh = pxlow + srcPixelStride;
        
        tmp00 = pxlow + pylow;
        tmp01 = pxhigh + pylow;
        tmp10 = pxlow + pyhigh;
        tmp11 = pxhigh + pyhigh;
        
        dstPixelOffset += dstPixelStride;
      }
      
      dstOffset += dstScanlineStride;
    }
  }
}
