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































final class AffineBicubicOpImage
  extends AffineOpImage
{
  public AffineBicubicOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, AffineTransform transform, Interpolation interp, double[] backgroundValues)
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
    for (int y = dst_min_y; y < dst_max_y; y++) {
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
      

      int p_x = (s_ix - srcRectX) * srcPixelStride;
      int p_y = (s_iy - srcRectY) * srcScanlineStride;
      









      int p__ = p_x + p_y - srcScanlineStride - srcPixelStride;
      int p0_ = p__ + srcPixelStride;
      int p1_ = p0_ + srcPixelStride;
      int p2_ = p1_ + srcPixelStride;
      int p_0 = p__ + srcScanlineStride;
      int p00 = p_0 + srcPixelStride;
      int p10 = p00 + srcPixelStride;
      int p20 = p10 + srcPixelStride;
      int p_1 = p_0 + srcScanlineStride;
      int p01 = p_1 + srcPixelStride;
      int p11 = p01 + srcPixelStride;
      int p21 = p11 + srcPixelStride;
      int p_2 = p_1 + srcScanlineStride;
      int p02 = p_2 + srcPixelStride;
      int p12 = p02 + srcPixelStride;
      int p22 = p12 + srcPixelStride;
      
      for (int x = dst_min_x; x < dst_max_x; x++)
      {



        if ((s_ix >= src_rect_x1 + 1.0F) && (s_ix < src_rect_x2 - 2.0F) && (s_iy >= src_rect_y1 + 1.0F) && (s_iy < src_rect_y2 - 2.0F))
        {


          for (int k2 = 0; k2 < dst_num_bands; k2++)
          {





            byte[] tmp_row = srcDataArrays[k2];
            int tmp_col = bandOffsets[k2];
            
            int s__ = tmp_row[(p__ + tmp_col)] & 0xFF;
            int s0_ = tmp_row[(p0_ + tmp_col)] & 0xFF;
            int s1_ = tmp_row[(p1_ + tmp_col)] & 0xFF;
            int s2_ = tmp_row[(p2_ + tmp_col)] & 0xFF;
            int s_0 = tmp_row[(p_0 + tmp_col)] & 0xFF;
            int s00 = tmp_row[(p00 + tmp_col)] & 0xFF;
            int s10 = tmp_row[(p10 + tmp_col)] & 0xFF;
            int s20 = tmp_row[(p20 + tmp_col)] & 0xFF;
            int s_1 = tmp_row[(p_1 + tmp_col)] & 0xFF;
            int s01 = tmp_row[(p01 + tmp_col)] & 0xFF;
            int s11 = tmp_row[(p11 + tmp_col)] & 0xFF;
            int s21 = tmp_row[(p21 + tmp_col)] & 0xFF;
            int s_2 = tmp_row[(p_2 + tmp_col)] & 0xFF;
            int s02 = tmp_row[(p02 + tmp_col)] & 0xFF;
            int s12 = tmp_row[(p12 + tmp_col)] & 0xFF;
            int s22 = tmp_row[(p22 + tmp_col)] & 0xFF;
            

            float float_fracx = fracx;
            float float_fracy = fracy;
            float frac_xx = float_fracx * (1.0F - float_fracx);
            float frac_yy = float_fracx * (1.0F - float_fracy);
            
            float s0 = s00 + (s10 - s00) * float_fracx;
            float s1 = s01 + (s11 - s01) * float_fracx;
            float s_ = s0_ + (s1_ - s0_) * float_fracx;
            float s2 = s02 + (s12 - s02) * float_fracx;
            
            float q_ = s1_ + s__ + (s2_ + s0_ - (s1_ + s__)) * float_fracx;
            
            float q0 = s10 + s_0 + (s20 + s00 - (s10 + s_0)) * float_fracx;
            
            float q1 = s11 + s_1 + (s21 + s01 - (s11 + s_1)) * float_fracx;
            
            float q2 = s12 + s_2 + (s22 + s02 - (s12 + s_2)) * float_fracx;
            

            q_ = s_ - q_ / 2.0F;
            q0 = s0 - q0 / 2.0F;
            q1 = s1 - q1 / 2.0F;
            q2 = s2 - q2 / 2.0F;
            
            s_ += q_ * frac_xx;
            s0 += q0 * frac_xx;
            s1 += q1 * frac_xx;
            s2 += q2 * frac_xx;
            
            float s = s0 + (s1 - s0) * float_fracy;
            float q = s1 + s_ + (s2 + s0 - (s1 + s_)) * float_fracy;
            

            q = s - q / 2.0F;
            
            s += q * frac_yy;
            int result;
            int result;
            if (s < 0.5F) {
              result = 0; } else { int result;
              if (s > 254.5F) {
                result = 255;
              } else {
                result = (int)(s + 0.5F);
              }
            }
            
            dstDataArrays[k2][(dstPixelOffset + dstBandOffsets[k2])] = ((byte)(result & 0xFF));
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
        

        p_x = (s_ix - srcRectX) * srcPixelStride;
        p_y = (s_iy - srcRectY) * srcScanlineStride;
        









        p__ = p_x + p_y - srcScanlineStride - srcPixelStride;
        p0_ = p__ + srcPixelStride;
        p1_ = p0_ + srcPixelStride;
        p2_ = p1_ + srcPixelStride;
        p_0 = p__ + srcScanlineStride;
        p00 = p_0 + srcPixelStride;
        p10 = p00 + srcPixelStride;
        p20 = p10 + srcPixelStride;
        p_1 = p_0 + srcScanlineStride;
        p01 = p_1 + srcPixelStride;
        p11 = p01 + srcPixelStride;
        p21 = p11 + srcPixelStride;
        p_2 = p_1 + srcScanlineStride;
        p02 = p_2 + srcPixelStride;
        p12 = p02 + srcPixelStride;
        p22 = p12 + srcPixelStride;
        
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
    for (int y = dst_min_y; y < dst_max_y; y++) {
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
      

      int p_x = (s_ix - srcRectX) * srcPixelStride;
      int p_y = (s_iy - srcRectY) * srcScanlineStride;
      









      int p__ = p_x + p_y - srcScanlineStride - srcPixelStride;
      int p0_ = p__ + srcPixelStride;
      int p1_ = p0_ + srcPixelStride;
      int p2_ = p1_ + srcPixelStride;
      int p_0 = p__ + srcScanlineStride;
      int p00 = p_0 + srcPixelStride;
      int p10 = p00 + srcPixelStride;
      int p20 = p10 + srcPixelStride;
      int p_1 = p_0 + srcScanlineStride;
      int p01 = p_1 + srcPixelStride;
      int p11 = p01 + srcPixelStride;
      int p21 = p11 + srcPixelStride;
      int p_2 = p_1 + srcScanlineStride;
      int p02 = p_2 + srcPixelStride;
      int p12 = p02 + srcPixelStride;
      int p22 = p12 + srcPixelStride;
      
      for (int x = dst_min_x; x < dst_max_x; x++)
      {


        if ((s_ix >= src_rect_x1 + 1.0F) && (s_ix < src_rect_x2 - 2.0F) && (s_iy >= src_rect_y1 + 1.0F) && (s_iy < src_rect_y2 - 2.0F))
        {


          for (int k2 = 0; k2 < dst_num_bands; k2++)
          {





            int[] tmp_row = srcDataArrays[k2];
            int tmp_col = bandOffsets[k2];
            
            int s__ = tmp_row[(p__ + tmp_col)];
            int s0_ = tmp_row[(p0_ + tmp_col)];
            int s1_ = tmp_row[(p1_ + tmp_col)];
            int s2_ = tmp_row[(p2_ + tmp_col)];
            int s_0 = tmp_row[(p_0 + tmp_col)];
            int s00 = tmp_row[(p00 + tmp_col)];
            int s10 = tmp_row[(p10 + tmp_col)];
            int s20 = tmp_row[(p20 + tmp_col)];
            int s_1 = tmp_row[(p_1 + tmp_col)];
            int s01 = tmp_row[(p01 + tmp_col)];
            int s11 = tmp_row[(p11 + tmp_col)];
            int s21 = tmp_row[(p21 + tmp_col)];
            int s_2 = tmp_row[(p_2 + tmp_col)];
            int s02 = tmp_row[(p02 + tmp_col)];
            int s12 = tmp_row[(p12 + tmp_col)];
            int s22 = tmp_row[(p22 + tmp_col)];
            

            float float_fracx = fracx;
            float float_fracy = fracy;
            float frac_xx = float_fracx * (1.0F - float_fracx);
            float frac_yy = float_fracx * (1.0F - float_fracy);
            
            float s0 = s00 + (s10 - s00) * float_fracx;
            float s1 = s01 + (s11 - s01) * float_fracx;
            float s_ = s0_ + (s1_ - s0_) * float_fracx;
            float s2 = s02 + (s12 - s02) * float_fracx;
            
            float q_ = s1_ + s__ + (s2_ + s0_ - (s1_ + s__)) * float_fracx;
            
            float q0 = s10 + s_0 + (s20 + s00 - (s10 + s_0)) * float_fracx;
            
            float q1 = s11 + s_1 + (s21 + s01 - (s11 + s_1)) * float_fracx;
            
            float q2 = s12 + s_2 + (s22 + s02 - (s12 + s_2)) * float_fracx;
            

            q_ = s_ - q_ / 2.0F;
            q0 = s0 - q0 / 2.0F;
            q1 = s1 - q1 / 2.0F;
            q2 = s2 - q2 / 2.0F;
            
            s_ += q_ * frac_xx;
            s0 += q0 * frac_xx;
            s1 += q1 * frac_xx;
            s2 += q2 * frac_xx;
            
            float s = s0 + (s1 - s0) * float_fracy;
            float q = s1 + s_ + (s2 + s0 - (s1 + s_)) * float_fracy;
            

            q = s - q / 2.0F;
            
            s += q * frac_yy;
            int result;
            int result;
            if (s < -2.14748365E9F) {
              result = Integer.MIN_VALUE; } else { int result;
              if (s > 2.14748365E9F) {
                result = Integer.MAX_VALUE; } else { int result;
                if (s > 0.0D) {
                  result = (int)(s + 0.5F);
                } else {
                  result = (int)(s - 0.5F);
                }
              }
            }
            dstDataArrays[k2][(dstPixelOffset + dstBandOffsets[k2])] = result;
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
        

        p_x = (s_ix - srcRectX) * srcPixelStride;
        p_y = (s_iy - srcRectY) * srcScanlineStride;
        









        p__ = p_x + p_y - srcScanlineStride - srcPixelStride;
        p0_ = p__ + srcPixelStride;
        p1_ = p0_ + srcPixelStride;
        p2_ = p1_ + srcPixelStride;
        p_0 = p__ + srcScanlineStride;
        p00 = p_0 + srcPixelStride;
        p10 = p00 + srcPixelStride;
        p20 = p10 + srcPixelStride;
        p_1 = p_0 + srcScanlineStride;
        p01 = p_1 + srcPixelStride;
        p11 = p01 + srcPixelStride;
        p21 = p11 + srcPixelStride;
        p_2 = p_1 + srcScanlineStride;
        p02 = p_2 + srcPixelStride;
        p12 = p02 + srcPixelStride;
        p22 = p12 + srcPixelStride;
        
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
      

      int p_x = (s_ix - srcRectX) * srcPixelStride;
      int p_y = (s_iy - srcRectY) * srcScanlineStride;
      









      int p__ = p_x + p_y - srcScanlineStride - srcPixelStride;
      int p0_ = p__ + srcPixelStride;
      int p1_ = p0_ + srcPixelStride;
      int p2_ = p1_ + srcPixelStride;
      int p_0 = p__ + srcScanlineStride;
      int p00 = p_0 + srcPixelStride;
      int p10 = p00 + srcPixelStride;
      int p20 = p10 + srcPixelStride;
      int p_1 = p_0 + srcScanlineStride;
      int p01 = p_1 + srcPixelStride;
      int p11 = p01 + srcPixelStride;
      int p21 = p11 + srcPixelStride;
      int p_2 = p_1 + srcScanlineStride;
      int p02 = p_2 + srcPixelStride;
      int p12 = p02 + srcPixelStride;
      int p22 = p12 + srcPixelStride;
      
      for (int x = dst_min_x; x < dst_max_x; x++)
      {


        if ((s_ix >= src_rect_x1 + 1.0F) && (s_ix < src_rect_x2 - 2.0F) && (s_iy >= src_rect_y1 + 1.0F) && (s_iy < src_rect_y2 - 2.0F))
        {


          for (int k2 = 0; k2 < dst_num_bands; k2++)
          {





            short[] tmp_row = srcDataArrays[k2];
            int tmp_col = bandOffsets[k2];
            
            short s__ = tmp_row[(p__ + tmp_col)];
            short s0_ = tmp_row[(p0_ + tmp_col)];
            short s1_ = tmp_row[(p1_ + tmp_col)];
            short s2_ = tmp_row[(p2_ + tmp_col)];
            short s_0 = tmp_row[(p_0 + tmp_col)];
            short s00 = tmp_row[(p00 + tmp_col)];
            short s10 = tmp_row[(p10 + tmp_col)];
            short s20 = tmp_row[(p20 + tmp_col)];
            short s_1 = tmp_row[(p_1 + tmp_col)];
            short s01 = tmp_row[(p01 + tmp_col)];
            short s11 = tmp_row[(p11 + tmp_col)];
            short s21 = tmp_row[(p21 + tmp_col)];
            short s_2 = tmp_row[(p_2 + tmp_col)];
            short s02 = tmp_row[(p02 + tmp_col)];
            short s12 = tmp_row[(p12 + tmp_col)];
            short s22 = tmp_row[(p22 + tmp_col)];
            

            float float_fracx = fracx;
            float float_fracy = fracy;
            float frac_xx = float_fracx * (1.0F - float_fracx);
            float frac_yy = float_fracx * (1.0F - float_fracy);
            
            float s0 = s00 + (s10 - s00) * float_fracx;
            float s1 = s01 + (s11 - s01) * float_fracx;
            float s_ = s0_ + (s1_ - s0_) * float_fracx;
            float s2 = s02 + (s12 - s02) * float_fracx;
            
            float q_ = s1_ + s__ + (s2_ + s0_ - (s1_ + s__)) * float_fracx;
            
            float q0 = s10 + s_0 + (s20 + s00 - (s10 + s_0)) * float_fracx;
            
            float q1 = s11 + s_1 + (s21 + s01 - (s11 + s_1)) * float_fracx;
            
            float q2 = s12 + s_2 + (s22 + s02 - (s12 + s_2)) * float_fracx;
            

            q_ = s_ - q_ / 2.0F;
            q0 = s0 - q0 / 2.0F;
            q1 = s1 - q1 / 2.0F;
            q2 = s2 - q2 / 2.0F;
            
            s_ += q_ * frac_xx;
            s0 += q0 * frac_xx;
            s1 += q1 * frac_xx;
            s2 += q2 * frac_xx;
            
            float s = s0 + (s1 - s0) * float_fracy;
            float q = s1 + s_ + (s2 + s0 - (s1 + s_)) * float_fracy;
            

            q = s - q / 2.0F;
            
            s += q * frac_yy;
            short result;
            short result;
            if (s < -32768.0F) {
              result = Short.MIN_VALUE; } else { short result;
              if (s > 32767.0F) {
                result = Short.MAX_VALUE; } else { short result;
                if (s > 0.0D) {
                  result = (short)(int)(s + 0.5F);
                } else {
                  result = (short)(int)(s - 0.5F);
                }
              }
            }
            dstDataArrays[k2][(dstPixelOffset + dstBandOffsets[k2])] = result;
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
        

        p_x = (s_ix - srcRectX) * srcPixelStride;
        p_y = (s_iy - srcRectY) * srcScanlineStride;
        









        p__ = p_x + p_y - srcScanlineStride - srcPixelStride;
        p0_ = p__ + srcPixelStride;
        p1_ = p0_ + srcPixelStride;
        p2_ = p1_ + srcPixelStride;
        p_0 = p__ + srcScanlineStride;
        p00 = p_0 + srcPixelStride;
        p10 = p00 + srcPixelStride;
        p20 = p10 + srcPixelStride;
        p_1 = p_0 + srcScanlineStride;
        p01 = p_1 + srcPixelStride;
        p11 = p01 + srcPixelStride;
        p21 = p11 + srcPixelStride;
        p_2 = p_1 + srcScanlineStride;
        p02 = p_2 + srcPixelStride;
        p12 = p02 + srcPixelStride;
        p22 = p12 + srcPixelStride;
        
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
    for (int y = dst_min_y; y < dst_max_y; y++) {
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
      

      int p_x = (s_ix - srcRectX) * srcPixelStride;
      int p_y = (s_iy - srcRectY) * srcScanlineStride;
      









      int p__ = p_x + p_y - srcScanlineStride - srcPixelStride;
      int p0_ = p__ + srcPixelStride;
      int p1_ = p0_ + srcPixelStride;
      int p2_ = p1_ + srcPixelStride;
      int p_0 = p__ + srcScanlineStride;
      int p00 = p_0 + srcPixelStride;
      int p10 = p00 + srcPixelStride;
      int p20 = p10 + srcPixelStride;
      int p_1 = p_0 + srcScanlineStride;
      int p01 = p_1 + srcPixelStride;
      int p11 = p01 + srcPixelStride;
      int p21 = p11 + srcPixelStride;
      int p_2 = p_1 + srcScanlineStride;
      int p02 = p_2 + srcPixelStride;
      int p12 = p02 + srcPixelStride;
      int p22 = p12 + srcPixelStride;
      
      for (int x = dst_min_x; x < dst_max_x; x++)
      {


        if ((s_ix >= src_rect_x1 + 1.0F) && (s_ix < src_rect_x2 - 2.0F) && (s_iy >= src_rect_y1 + 1.0F) && (s_iy < src_rect_y2 - 2.0F))
        {


          for (int k2 = 0; k2 < dst_num_bands; k2++)
          {





            short[] tmp_row = srcDataArrays[k2];
            int tmp_col = bandOffsets[k2];
            
            int s__ = tmp_row[(p__ + tmp_col)] & 0xFFFF;
            int s0_ = tmp_row[(p0_ + tmp_col)] & 0xFFFF;
            int s1_ = tmp_row[(p1_ + tmp_col)] & 0xFFFF;
            int s2_ = tmp_row[(p2_ + tmp_col)] & 0xFFFF;
            int s_0 = tmp_row[(p_0 + tmp_col)] & 0xFFFF;
            int s00 = tmp_row[(p00 + tmp_col)] & 0xFFFF;
            int s10 = tmp_row[(p10 + tmp_col)] & 0xFFFF;
            int s20 = tmp_row[(p20 + tmp_col)] & 0xFFFF;
            int s_1 = tmp_row[(p_1 + tmp_col)] & 0xFFFF;
            int s01 = tmp_row[(p01 + tmp_col)] & 0xFFFF;
            int s11 = tmp_row[(p11 + tmp_col)] & 0xFFFF;
            int s21 = tmp_row[(p21 + tmp_col)] & 0xFFFF;
            int s_2 = tmp_row[(p_2 + tmp_col)] & 0xFFFF;
            int s02 = tmp_row[(p02 + tmp_col)] & 0xFFFF;
            int s12 = tmp_row[(p12 + tmp_col)] & 0xFFFF;
            int s22 = tmp_row[(p22 + tmp_col)] & 0xFFFF;
            

            float float_fracx = fracx;
            float float_fracy = fracy;
            float frac_xx = float_fracx * (1.0F - float_fracx);
            float frac_yy = float_fracx * (1.0F - float_fracy);
            
            float s0 = s00 + (s10 - s00) * float_fracx;
            float s1 = s01 + (s11 - s01) * float_fracx;
            float s_ = s0_ + (s1_ - s0_) * float_fracx;
            float s2 = s02 + (s12 - s02) * float_fracx;
            
            float q_ = s1_ + s__ + (s2_ + s0_ - (s1_ + s__)) * float_fracx;
            
            float q0 = s10 + s_0 + (s20 + s00 - (s10 + s_0)) * float_fracx;
            
            float q1 = s11 + s_1 + (s21 + s01 - (s11 + s_1)) * float_fracx;
            
            float q2 = s12 + s_2 + (s22 + s02 - (s12 + s_2)) * float_fracx;
            

            q_ = s_ - q_ / 2.0F;
            q0 = s0 - q0 / 2.0F;
            q1 = s1 - q1 / 2.0F;
            q2 = s2 - q2 / 2.0F;
            
            s_ += q_ * frac_xx;
            s0 += q0 * frac_xx;
            s1 += q1 * frac_xx;
            s2 += q2 * frac_xx;
            
            float s = s0 + (s1 - s0) * float_fracy;
            float q = s1 + s_ + (s2 + s0 - (s1 + s_)) * float_fracy;
            


            q = s - q / 2.0F;
            
            s += q * frac_yy;
            int result;
            int result;
            if (s < 0.0D) {
              result = 0; } else { int result;
              if (s > 65535.0F) {
                result = 65535;
              } else {
                result = (int)(s + 0.5F);
              }
            }
            
            dstDataArrays[k2][(dstPixelOffset + dstBandOffsets[k2])] = ((short)(result & 0xFFFF));
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
        

        p_x = (s_ix - srcRectX) * srcPixelStride;
        p_y = (s_iy - srcRectY) * srcScanlineStride;
        









        p__ = p_x + p_y - srcScanlineStride - srcPixelStride;
        p0_ = p__ + srcPixelStride;
        p1_ = p0_ + srcPixelStride;
        p2_ = p1_ + srcPixelStride;
        p_0 = p__ + srcScanlineStride;
        p00 = p_0 + srcPixelStride;
        p10 = p00 + srcPixelStride;
        p20 = p10 + srcPixelStride;
        p_1 = p_0 + srcScanlineStride;
        p01 = p_1 + srcPixelStride;
        p11 = p01 + srcPixelStride;
        p21 = p11 + srcPixelStride;
        p_2 = p_1 + srcScanlineStride;
        p02 = p_2 + srcPixelStride;
        p12 = p02 + srcPixelStride;
        p22 = p12 + srcPixelStride;
        
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
      

      int p_x = (s_ix - srcRectX) * srcPixelStride;
      int p_y = (s_iy - srcRectY) * srcScanlineStride;
      









      int p__ = p_x + p_y - srcScanlineStride - srcPixelStride;
      int p0_ = p__ + srcPixelStride;
      int p1_ = p0_ + srcPixelStride;
      int p2_ = p1_ + srcPixelStride;
      int p_0 = p__ + srcScanlineStride;
      int p00 = p_0 + srcPixelStride;
      int p10 = p00 + srcPixelStride;
      int p20 = p10 + srcPixelStride;
      int p_1 = p_0 + srcScanlineStride;
      int p01 = p_1 + srcPixelStride;
      int p11 = p01 + srcPixelStride;
      int p21 = p11 + srcPixelStride;
      int p_2 = p_1 + srcScanlineStride;
      int p02 = p_2 + srcPixelStride;
      int p12 = p02 + srcPixelStride;
      int p22 = p12 + srcPixelStride;
      
      for (int x = dst_min_x; x < dst_max_x; x++)
      {


        if ((s_ix >= src_rect_x1 + 1.0F) && (s_ix < src_rect_x2 - 2.0F) && (s_iy >= src_rect_y1 + 1.0F) && (s_iy < src_rect_y2 - 2.0F))
        {


          for (int k2 = 0; k2 < dst_num_bands; k2++)
          {





            float[] tmp_row = srcDataArrays[k2];
            int tmp_col = bandOffsets[k2];
            
            float s__ = tmp_row[(p__ + tmp_col)];
            float s0_ = tmp_row[(p0_ + tmp_col)];
            float s1_ = tmp_row[(p1_ + tmp_col)];
            float s2_ = tmp_row[(p2_ + tmp_col)];
            float s_0 = tmp_row[(p_0 + tmp_col)];
            float s00 = tmp_row[(p00 + tmp_col)];
            float s10 = tmp_row[(p10 + tmp_col)];
            float s20 = tmp_row[(p20 + tmp_col)];
            float s_1 = tmp_row[(p_1 + tmp_col)];
            float s01 = tmp_row[(p01 + tmp_col)];
            float s11 = tmp_row[(p11 + tmp_col)];
            float s21 = tmp_row[(p21 + tmp_col)];
            float s_2 = tmp_row[(p_2 + tmp_col)];
            float s02 = tmp_row[(p02 + tmp_col)];
            float s12 = tmp_row[(p12 + tmp_col)];
            float s22 = tmp_row[(p22 + tmp_col)];
            

            float float_fracx = fracx;
            float float_fracy = fracy;
            float frac_xx = float_fracx * (1.0F - float_fracx);
            float frac_yy = float_fracx * (1.0F - float_fracy);
            
            float s0 = s00 + (s10 - s00) * float_fracx;
            float s1 = s01 + (s11 - s01) * float_fracx;
            float s_ = s0_ + (s1_ - s0_) * float_fracx;
            float s2 = s02 + (s12 - s02) * float_fracx;
            
            float q_ = s1_ + s__ + (s2_ + s0_ - (s1_ + s__)) * float_fracx;
            
            float q0 = s10 + s_0 + (s20 + s00 - (s10 + s_0)) * float_fracx;
            
            float q1 = s11 + s_1 + (s21 + s01 - (s11 + s_1)) * float_fracx;
            
            float q2 = s12 + s_2 + (s22 + s02 - (s12 + s_2)) * float_fracx;
            

            q_ = s_ - q_ / 2.0F;
            q0 = s0 - q0 / 2.0F;
            q1 = s1 - q1 / 2.0F;
            q2 = s2 - q2 / 2.0F;
            
            s_ += q_ * frac_xx;
            s0 += q0 * frac_xx;
            s1 += q1 * frac_xx;
            s2 += q2 * frac_xx;
            
            float s = s0 + (s1 - s0) * float_fracy;
            float q = s1 + s_ + (s2 + s0 - (s1 + s_)) * float_fracy;
            


            q = s - q / 2.0F;
            
            s += q * frac_yy;
            

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
        

        p_x = (s_ix - srcRectX) * srcPixelStride;
        p_y = (s_iy - srcRectY) * srcScanlineStride;
        









        p__ = p_x + p_y - srcScanlineStride - srcPixelStride;
        p0_ = p__ + srcPixelStride;
        p1_ = p0_ + srcPixelStride;
        p2_ = p1_ + srcPixelStride;
        p_0 = p__ + srcScanlineStride;
        p00 = p_0 + srcPixelStride;
        p10 = p00 + srcPixelStride;
        p20 = p10 + srcPixelStride;
        p_1 = p_0 + srcScanlineStride;
        p01 = p_1 + srcPixelStride;
        p11 = p01 + srcPixelStride;
        p21 = p11 + srcPixelStride;
        p_2 = p_1 + srcScanlineStride;
        p02 = p_2 + srcPixelStride;
        p12 = p02 + srcPixelStride;
        p22 = p12 + srcPixelStride;
        
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
      

      double s_x = src_pt.getX();
      double s_y = src_pt.getY();
      

      s_x -= 0.5D;
      s_y -= 0.5D;
      

      int s_ix = (int)Math.floor(s_x);
      int s_iy = (int)Math.floor(s_y);
      
      double fracx = s_x - s_ix;
      double fracy = s_y - s_iy;
      

      int p_x = (s_ix - srcRectX) * srcPixelStride;
      int p_y = (s_iy - srcRectY) * srcScanlineStride;
      









      int p__ = p_x + p_y - srcScanlineStride - srcPixelStride;
      int p0_ = p__ + srcPixelStride;
      int p1_ = p0_ + srcPixelStride;
      int p2_ = p1_ + srcPixelStride;
      int p_0 = p__ + srcScanlineStride;
      int p00 = p_0 + srcPixelStride;
      int p10 = p00 + srcPixelStride;
      int p20 = p10 + srcPixelStride;
      int p_1 = p_0 + srcScanlineStride;
      int p01 = p_1 + srcPixelStride;
      int p11 = p01 + srcPixelStride;
      int p21 = p11 + srcPixelStride;
      int p_2 = p_1 + srcScanlineStride;
      int p02 = p_2 + srcPixelStride;
      int p12 = p02 + srcPixelStride;
      int p22 = p12 + srcPixelStride;
      
      for (int x = dst_min_x; x < dst_max_x; x++)
      {


        if ((s_ix >= src_rect_x1 + 1.0F) && (s_ix < src_rect_x2 - 2.0F) && (s_iy >= src_rect_y1 + 1.0F) && (s_iy < src_rect_y2 - 2.0F))
        {


          for (int k2 = 0; k2 < dst_num_bands; k2++)
          {





            double[] tmp_row = srcDataArrays[k2];
            int tmp_col = bandOffsets[k2];
            
            double s__ = tmp_row[(p__ + tmp_col)];
            double s0_ = tmp_row[(p0_ + tmp_col)];
            double s1_ = tmp_row[(p1_ + tmp_col)];
            double s2_ = tmp_row[(p2_ + tmp_col)];
            double s_0 = tmp_row[(p_0 + tmp_col)];
            double s00 = tmp_row[(p00 + tmp_col)];
            double s10 = tmp_row[(p10 + tmp_col)];
            double s20 = tmp_row[(p20 + tmp_col)];
            double s_1 = tmp_row[(p_1 + tmp_col)];
            double s01 = tmp_row[(p01 + tmp_col)];
            double s11 = tmp_row[(p11 + tmp_col)];
            double s21 = tmp_row[(p21 + tmp_col)];
            double s_2 = tmp_row[(p_2 + tmp_col)];
            double s02 = tmp_row[(p02 + tmp_col)];
            double s12 = tmp_row[(p12 + tmp_col)];
            double s22 = tmp_row[(p22 + tmp_col)];
            

            double float_fracx = fracx;
            double float_fracy = fracy;
            double frac_xx = float_fracx * (1.0D - float_fracx);
            double frac_yy = float_fracx * (1.0D - float_fracy);
            
            double s0 = s00 + (s10 - s00) * float_fracx;
            double s1 = s01 + (s11 - s01) * float_fracx;
            double s_ = s0_ + (s1_ - s0_) * float_fracx;
            double s2 = s02 + (s12 - s02) * float_fracx;
            
            double q_ = s1_ + s__ + (s2_ + s0_ - (s1_ + s__)) * float_fracx;
            
            double q0 = s10 + s_0 + (s20 + s00 - (s10 + s_0)) * float_fracx;
            
            double q1 = s11 + s_1 + (s21 + s01 - (s11 + s_1)) * float_fracx;
            
            double q2 = s12 + s_2 + (s22 + s02 - (s12 + s_2)) * float_fracx;
            

            q_ = s_ - q_ / 2.0D;
            q0 = s0 - q0 / 2.0D;
            q1 = s1 - q1 / 2.0D;
            q2 = s2 - q2 / 2.0D;
            
            s_ += q_ * frac_xx;
            s0 += q0 * frac_xx;
            s1 += q1 * frac_xx;
            s2 += q2 * frac_xx;
            
            double s = s0 + (s1 - s0) * float_fracy;
            double q = s1 + s_ + (s2 + s0 - (s1 + s_)) * float_fracy;
            

            q = s - q / 2.0D;
            
            s += q * frac_yy;
            

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
        

        p_x = (s_ix - srcRectX) * srcPixelStride;
        p_y = (s_iy - srcRectY) * srcScanlineStride;
        









        p__ = p_x + p_y - srcScanlineStride - srcPixelStride;
        p0_ = p__ + srcPixelStride;
        p1_ = p0_ + srcPixelStride;
        p2_ = p1_ + srcPixelStride;
        p_0 = p__ + srcScanlineStride;
        p00 = p_0 + srcPixelStride;
        p10 = p00 + srcPixelStride;
        p20 = p10 + srcPixelStride;
        p_1 = p_0 + srcScanlineStride;
        p01 = p_1 + srcPixelStride;
        p11 = p01 + srcPixelStride;
        p21 = p11 + srcPixelStride;
        p_2 = p_1 + srcScanlineStride;
        p02 = p_2 + srcPixelStride;
        p12 = p02 + srcPixelStride;
        p22 = p12 + srcPixelStride;
        
        dstPixelOffset += dstPixelStride;
      }
      
      dstOffset += dstScanlineStride;
    }
  }
}
