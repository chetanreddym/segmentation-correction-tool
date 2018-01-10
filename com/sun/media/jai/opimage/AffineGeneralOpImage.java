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




























final class AffineGeneralOpImage
  extends AffineOpImage
{
  private int subsampleBits;
  private int shiftvalue;
  private int interp_width;
  private int interp_height;
  private int interp_left;
  private int interp_top;
  private int interp_right;
  private int interp_bottom;
  
  public AffineGeneralOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, AffineTransform transform, Interpolation interp, double[] backgroundValues)
  {
    super(source, extender, config, layout, transform, interp, backgroundValues);
    






    subsampleBits = interp.getSubsampleBitsH();
    shiftvalue = (1 << subsampleBits);
    
    interp_width = interp.getWidth();
    interp_height = interp.getHeight();
    interp_left = interp.getLeftPadding();
    interp_top = interp.getTopPadding();
    interp_right = (interp_width - interp_left - 1);
    interp_bottom = (interp_height - interp_top - 1);
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
    










    int[][] samples = new int[interp_height][interp_width];
    


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
      
      for (int x = dst_min_x; x < dst_max_x; x++)
      {



        if ((s_ix >= src_rect_x1 + interp_left) && (s_ix < src_rect_x2 - interp_right) && (s_iy >= src_rect_y1 + interp_top) && (s_iy < src_rect_y2 - interp_bottom))
        {


          for (int k = 0; k < dst_num_bands; k++) {
            byte[] srcData = srcDataArrays[k];
            int tmp = bandOffsets[k];
            

            int start = interp_left * srcPixelStride + interp_top * srcScanlineStride;
            
            start = p_x + p_y - start;
            int countH = 0;int countV = 0;
            
            for (int i = 0; i < interp_height; i++) {
              int startY = start;
              for (int j = 0; j < interp_width; j++) {
                samples[countV][(countH++)] = (srcData[(start + tmp)] & 0xFF);
                
                start += srcPixelStride;
              }
              countV++;
              countH = 0;
              start = startY + srcScanlineStride;
            }
            

            int xfrac = (int)(fracx * shiftvalue);
            int yfrac = (int)(fracy * shiftvalue);
            

            int s = interp.interpolate(samples, xfrac, yfrac);
            int result;
            int result;
            if (s < 0) {
              result = 0; } else { int result;
              if (s > 255) {
                result = 255;
              } else {
                result = s;
              }
            }
            
            dstDataArrays[k][(dstPixelOffset + dstBandOffsets[k])] = ((byte)(result & 0xFF));
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
    
    int[][] samples = new int[interp_height][interp_width];
    

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
      
      for (int x = dst_min_x; x < dst_max_x; x++)
      {


        if ((s_ix >= src_rect_x1 + interp_left) && (s_ix < src_rect_x2 - interp_right) && (s_iy >= src_rect_y1 + interp_top) && (s_iy < src_rect_y2 - interp_bottom))
        {


          for (int k = 0; k < dst_num_bands; k++) {
            int[] srcData = srcDataArrays[k];
            int tmp = bandOffsets[k];
            

            int start = interp_left * srcPixelStride + interp_top * srcScanlineStride;
            
            start = p_x + p_y - start;
            int countH = 0;int countV = 0;
            
            for (int i = 0; i < interp_height; i++) {
              int startY = start;
              for (int j = 0; j < interp_width; j++) {
                samples[countV][(countH++)] = srcData[(start + tmp)];
                
                start += srcPixelStride;
              }
              countV++;
              countH = 0;
              start = startY + srcScanlineStride;
            }
            

            int xfrac = (int)(fracx * shiftvalue);
            int yfrac = (int)(fracy * shiftvalue);
            

            int s = interp.interpolate(samples, xfrac, yfrac);
            int result;
            int result;
            if (s < Integer.MIN_VALUE) {
              result = Integer.MIN_VALUE; } else { int result;
              if (s > Integer.MAX_VALUE) {
                result = Integer.MAX_VALUE;
              } else {
                result = s;
              }
            }
            
            dstDataArrays[k][(dstPixelOffset + dstBandOffsets[k])] = result;
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
    









    int[][] samples = new int[interp_height][interp_width];
    




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
      
      for (int x = dst_min_x; x < dst_max_x; x++)
      {


        if ((s_ix >= src_rect_x1 + interp_left) && (s_ix < src_rect_x2 - interp_right) && (s_iy >= src_rect_y1 + interp_top) && (s_iy < src_rect_y2 - interp_bottom))
        {


          for (int k = 0; k < dst_num_bands; k++) {
            short[] srcData = srcDataArrays[k];
            int tmp = bandOffsets[k];
            

            int start = interp_left * srcPixelStride + interp_top * srcScanlineStride;
            
            start = p_x + p_y - start;
            int countH = 0;int countV = 0;
            
            for (int i = 0; i < interp_height; i++) {
              int startY = start;
              for (int j = 0; j < interp_width; j++) {
                samples[countV][(countH++)] = srcData[(start + tmp)];
                
                start += srcPixelStride;
              }
              countV++;
              countH = 0;
              start = startY + srcScanlineStride;
            }
            

            int xfrac = (int)(fracx * shiftvalue);
            int yfrac = (int)(fracy * shiftvalue);
            

            int s = interp.interpolate(samples, xfrac, yfrac);
            short result;
            short result;
            if (s < 32768) {
              result = Short.MIN_VALUE; } else { short result;
              if (s > 32767) {
                result = Short.MAX_VALUE;
              } else {
                result = (short)s;
              }
            }
            
            dstDataArrays[k][(dstPixelOffset + dstBandOffsets[k])] = result;
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
    









    int[][] samples = new int[interp_height][interp_width];
    




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
      
      for (int x = dst_min_x; x < dst_max_x; x++)
      {


        if ((s_ix >= src_rect_x1 + interp_left) && (s_ix < src_rect_x2 - interp_right) && (s_iy >= src_rect_y1 + interp_top) && (s_iy < src_rect_y2 - interp_bottom))
        {


          for (int k = 0; k < dst_num_bands; k++) {
            short[] srcData = srcDataArrays[k];
            int tmp = bandOffsets[k];
            

            int start = interp_left * srcPixelStride + interp_top * srcScanlineStride;
            
            start = p_x + p_y - start;
            int countH = 0;int countV = 0;
            
            for (int i = 0; i < interp_height; i++) {
              int startY = start;
              for (int j = 0; j < interp_width; j++) {
                samples[countV][(countH++)] = (srcData[(start + tmp)] & 0xFFFF);
                
                start += srcPixelStride;
              }
              countV++;
              countH = 0;
              start = startY + srcScanlineStride;
            }
            

            int xfrac = (int)(fracx * shiftvalue);
            int yfrac = (int)(fracy * shiftvalue);
            

            int s = interp.interpolate(samples, xfrac, yfrac);
            int result;
            int result;
            if (s < 0) {
              result = 0; } else { int result;
              if (s > 65535) {
                result = 65535;
              } else {
                result = s;
              }
            }
            
            dstDataArrays[k][(dstPixelOffset + dstBandOffsets[k])] = ((short)(result & 0xFFFF));
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
    









    float[][] samples = new float[interp_height][interp_width];
    


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
      
      for (int x = dst_min_x; x < dst_max_x; x++)
      {


        if ((s_ix >= src_rect_x1 + interp_left) && (s_ix < src_rect_x2 - interp_right) && (s_iy >= src_rect_y1 + interp_top) && (s_iy < src_rect_y2 - interp_bottom))
        {


          for (int k = 0; k < dst_num_bands; k++) {
            float[] srcData = srcDataArrays[k];
            int tmp = bandOffsets[k];
            

            int start = interp_left * srcPixelStride + interp_top * srcScanlineStride;
            
            start = p_x + p_y - start;
            int countH = 0;int countV = 0;
            
            for (int i = 0; i < interp_height; i++) {
              int startY = start;
              for (int j = 0; j < interp_width; j++) {
                samples[countV][(countH++)] = srcData[(start + tmp)];
                
                start += srcPixelStride;
              }
              countV++;
              countH = 0;
              start = startY + srcScanlineStride;
            }
            

            float s = interp.interpolate(samples, fracx, fracy);
            

            dstDataArrays[k][(dstPixelOffset + dstBandOffsets[k])] = s;
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
    









    double[][] samples = new double[interp_height][interp_width];
    


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
      
      for (int x = dst_min_x; x < dst_max_x; x++)
      {


        if ((s_ix >= src_rect_x1 + interp_left) && (s_ix < src_rect_x2 - interp_right) && (s_iy >= src_rect_y1 + interp_top) && (s_iy < src_rect_y2 - interp_bottom))
        {


          for (int k = 0; k < dst_num_bands; k++) {
            double[] srcData = srcDataArrays[k];
            int tmp = bandOffsets[k];
            

            int start = interp_left * srcPixelStride + interp_top * srcScanlineStride;
            
            start = p_x + p_y - start;
            int countH = 0;int countV = 0;
            
            for (int i = 0; i < interp_height; i++) {
              int startY = start;
              for (int j = 0; j < interp_width; j++) {
                samples[countV][(countH++)] = srcData[(start + tmp)];
                
                start += srcPixelStride;
              }
              countV++;
              countH = 0;
              start = startY + srcScanlineStride;
            }
            

            double s = interp.interpolate(samples, (float)fracx, (float)fracy);
            



            dstDataArrays[k][(dstPixelOffset + dstBandOffsets[k])] = s;
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
        
        dstPixelOffset += dstPixelStride;
      }
      
      dstOffset += dstScanlineStride;
    }
  }
}
