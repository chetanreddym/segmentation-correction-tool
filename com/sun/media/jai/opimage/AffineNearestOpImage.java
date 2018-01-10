package com.sun.media.jai.opimage;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
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
import javax.media.jai.util.Range;



























class AffineNearestOpImage
  extends AffineOpImage
{
  public AffineNearestOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, AffineTransform transform, Interpolation interp, double[] backgroundValues)
  {
    super(source, extender, config, layout, transform, interp, backgroundValues);
    










    ColorModel srcColorModel = source.getColorModel();
    if ((srcColorModel instanceof IndexColorModel)) {
      sampleModel = source.getSampleModel().createCompatibleSampleModel(tileWidth, tileHeight);
      
      colorModel = srcColorModel;
    }
  }
  




















  protected Range performScanlineClipping(float src_rect_x1, float src_rect_y1, float src_rect_x2, float src_rect_y2, int s_ix, int s_iy, int ifracx, int ifracy, int dst_min_x, int dst_max_x, int lpad, int rpad, int tpad, int bpad)
  {
    int clipMinX = dst_min_x;
    int clipMaxX = dst_max_x;
    
    long xdenom = incx * 1048576 + ifracdx;
    if (xdenom != 0L) {
      long clipx1 = src_rect_x1 + lpad;
      long clipx2 = src_rect_x2 - rpad;
      
      long x1 = (clipx1 - s_ix) * 1048576L - ifracx + dst_min_x * xdenom;
      
      long x2 = (clipx2 - s_ix) * 1048576L - ifracx + dst_min_x * xdenom;
      


      if (xdenom < 0L) {
        long tmp = x1;
        x1 = x2;
        x2 = tmp;
      }
      
      int dx1 = ceilRatio(x1, xdenom);
      clipMinX = Math.max(clipMinX, dx1);
      
      int dx2 = floorRatio(x2, xdenom) + 1;
      clipMaxX = Math.min(clipMaxX, dx2);

    }
    else if ((s_ix < src_rect_x1) || (s_ix >= src_rect_x2)) {
      clipMinX = clipMaxX = dst_min_x;
      return new Range(Integer.class, new Integer(clipMinX), new Integer(clipMaxX));
    }
    



    long ydenom = incy * 1048576 + ifracdy;
    if (ydenom != 0L) {
      long clipy1 = src_rect_y1 + tpad;
      long clipy2 = src_rect_y2 - bpad;
      
      long y1 = (clipy1 - s_iy) * 1048576L - ifracy + dst_min_x * ydenom;
      
      long y2 = (clipy2 - s_iy) * 1048576L - ifracy + dst_min_x * ydenom;
      


      if (ydenom < 0L) {
        long tmp = y1;
        y1 = y2;
        y2 = tmp;
      }
      
      int dx1 = ceilRatio(y1, ydenom);
      clipMinX = Math.max(clipMinX, dx1);
      
      int dx2 = floorRatio(y2, ydenom) + 1;
      clipMaxX = Math.min(clipMaxX, dx2);

    }
    else if ((s_iy < src_rect_y1) || (s_iy >= src_rect_y2)) {
      clipMinX = clipMaxX = dst_min_x;
    }
    

    if (clipMinX > dst_max_x)
      clipMinX = dst_max_x;
    if (clipMaxX < dst_min_x) {
      clipMaxX = dst_min_x;
    }
    return new Range(Integer.class, new Integer(clipMinX), new Integer(clipMaxX));
  }
  












  protected Point[] advanceToStartOfScanline(int dst_min_x, int clipMinX, int s_ix, int s_iy, int ifracx, int ifracy)
  {
    long skip = clipMinX - dst_min_x;
    long dx = (ifracx + skip * ifracdx) / 1048576L;
    
    long dy = (ifracy + skip * ifracdy) / 1048576L;
    
    s_ix = (int)(s_ix + (skip * incx + (int)dx));
    s_iy = (int)(s_iy + (skip * incy + (int)dy));
    
    long lfracx = ifracx + skip * ifracdx;
    if (lfracx >= 0L) {
      ifracx = (int)(lfracx % 1048576L);
    } else {
      ifracx = (int)-(-lfracx % 1048576L);
    }
    
    long lfracy = ifracy + skip * ifracdy;
    if (lfracy >= 0L) {
      ifracy = (int)(lfracy % 1048576L);
    } else {
      ifracy = (int)-(-lfracy % 1048576L);
    }
    
    return new Point[] { new Point(s_ix, s_iy), new Point(ifracx, ifracy) };
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
      int dstNumBands = dstAccessor.getNumBands();
      if (dstNumBands == 1) {
        byteLoop_1band(srcAccessor, destRect, srcRectX, srcRectY, dstAccessor);



      }
      else if (dstNumBands == 3) {
        byteLoop_3band(srcAccessor, destRect, srcRectX, srcRectY, dstAccessor);

      }
      else
      {

        byteLoop(srcAccessor, destRect, srcRectX, srcRectY, dstAccessor);
      }
      



      break;
    
    case 3: 
      intLoop(srcAccessor, destRect, srcRectX, srcRectY, dstAccessor);
      



      break;
    
    case 1: 
    case 2: 
      shortLoop(srcAccessor, destRect, srcRectX, srcRectY, dstAccessor);
      



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
    
    int incxStride = incx * srcPixelStride;
    int incx1Stride = incx1 * srcPixelStride;
    int incyStride = incy * srcScanlineStride;
    int incy1Stride = incy1 * srcScanlineStride;
    
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
      

      int s_ix = (int)Math.floor(s_x);
      int s_iy = (int)Math.floor(s_y);
      
      double fracx = s_x - s_ix;
      double fracy = s_y - s_iy;
      
      int ifracx = (int)Math.floor(fracx * 1048576.0D);
      int ifracy = (int)Math.floor(fracy * 1048576.0D);
      

      Range clipRange = performScanlineClipping(src_rect_x1, src_rect_y1, src_rect_x2 - 1.0F, src_rect_y2 - 1.0F, s_ix, s_iy, ifracx, ifracy, dst_min_x, dst_max_x, 0, 0, 0, 0);
      








      int clipMinX = ((Integer)clipRange.getMinValue()).intValue();
      int clipMaxX = ((Integer)clipRange.getMaxValue()).intValue();
      

      Point[] startPts = advanceToStartOfScanline(dst_min_x, clipMinX, s_ix, s_iy, ifracx, ifracy);
      

      s_ix = 0x;
      s_iy = 0y;
      ifracx = 1x;
      ifracy = 1y;
      

      int src_pos = (s_iy - srcRectY) * srcScanlineStride + (s_ix - srcRectX) * srcPixelStride;
      

      if (setBackground) {
        for (int x = dst_min_x; x < clipMinX; x++) {
          for (int k2 = 0; k2 < dst_num_bands; k2++) {
            dstDataArrays[k2][(dstPixelOffset + dstBandOffsets[k2])] = backgroundByte[k2];
          }
          
          dstPixelOffset += dstPixelStride;
        }
      } else {
        dstPixelOffset += (clipMinX - dst_min_x) * dstPixelStride;
      }
      for (int x = clipMinX; x < clipMaxX; x++) {
        for (int k2 = 0; k2 < dst_num_bands; k2++) {
          dstDataArrays[k2][(dstPixelOffset + dstBandOffsets[k2])] = srcDataArrays[k2][(src_pos + bandOffsets[k2])];
        }
        



        if (ifracx < ifracdx1)
        {



          src_pos += incxStride;
          ifracx += ifracdx;

        }
        else
        {

          src_pos += incx1Stride;
          ifracx -= ifracdx1;
        }
        
        if (ifracy < ifracdy1)
        {



          src_pos += incyStride;
          ifracy += ifracdy;

        }
        else
        {

          src_pos += incy1Stride;
          ifracy -= ifracdy1;
        }
        

        dstPixelOffset += dstPixelStride;
      }
      
      if ((setBackground) && (clipMinX <= clipMaxX)) {
        for (int x = clipMaxX; x < dst_max_x; x++) {
          for (int k2 = 0; k2 < dst_num_bands; k2++) {
            dstDataArrays[k2][(dstPixelOffset + dstBandOffsets[k2])] = backgroundByte[k2];
          }
          
          dstPixelOffset += dstPixelStride;
        }
      }
      

      dstOffset += dstScanlineStride;
    }
  }
  





  private void byteLoop_1band(RasterAccessor src, Rectangle destRect, int srcRectX, int srcRectY, RasterAccessor dst)
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
    
    byte[][] dstDataArrays = dst.getByteDataArrays();
    int[] dstBandOffsets = dst.getBandOffsets();
    int dstPixelStride = dst.getPixelStride();
    int dstScanlineStride = dst.getScanlineStride();
    
    byte[] dstDataArray0 = dstDataArrays[0];
    int dstBandOffset0 = dstBandOffsets[0];
    
    byte[][] srcDataArrays = src.getByteDataArrays();
    int[] bandOffsets = src.getBandOffsets();
    int srcPixelStride = src.getPixelStride();
    int srcScanlineStride = src.getScanlineStride();
    
    byte[] srcDataArray0 = srcDataArrays[0];
    int bandOffsets0 = bandOffsets[0];
    
    int dst_min_x = x;
    int dst_min_y = y;
    int dst_max_x = x + width;
    int dst_max_y = y + height;
    
    int incxStride = incx * srcPixelStride;
    int incx1Stride = incx1 * srcPixelStride;
    int incyStride = incy * srcScanlineStride;
    int incy1Stride = incy1 * srcScanlineStride;
    
    byte backgroundByte = (byte)(int)backgroundValues[0];
    
    for (int y = dst_min_y; y < dst_max_y; y++) {
      int dstPixelOffset = dstOffset;
      


      dst_pt.setLocation(dst_min_x + 0.5D, y + 0.5D);
      

      mapDestPoint(dst_pt, src_pt);
      

      float s_x = (float)src_pt.getX();
      float s_y = (float)src_pt.getY();
      

      int s_ix = (int)Math.floor(s_x);
      int s_iy = (int)Math.floor(s_y);
      
      double fracx = s_x - s_ix;
      double fracy = s_y - s_iy;
      
      int ifracx = (int)Math.floor(fracx * 1048576.0D);
      int ifracy = (int)Math.floor(fracy * 1048576.0D);
      

      Range clipRange = performScanlineClipping(src_rect_x1, src_rect_y1, src_rect_x2 - 1.0F, src_rect_y2 - 1.0F, s_ix, s_iy, ifracx, ifracy, dst_min_x, dst_max_x, 0, 0, 0, 0);
      








      int clipMinX = ((Integer)clipRange.getMinValue()).intValue();
      int clipMaxX = ((Integer)clipRange.getMaxValue()).intValue();
      

      Point[] startPts = advanceToStartOfScanline(dst_min_x, clipMinX, s_ix, s_iy, ifracx, ifracy);
      

      s_ix = 0x;
      s_iy = 0y;
      ifracx = 1x;
      ifracy = 1y;
      

      int src_pos = (s_iy - srcRectY) * srcScanlineStride + (s_ix - srcRectX) * srcPixelStride;
      

      if (setBackground) {
        for (int x = dst_min_x; x < clipMinX; x++) {
          dstDataArray0[(dstPixelOffset + dstBandOffset0)] = backgroundByte;
          
          dstPixelOffset += dstPixelStride;
        }
      } else {
        dstPixelOffset += (clipMinX - dst_min_x) * dstPixelStride;
      }
      for (int x = clipMinX; x < clipMaxX; x++) {
        dstDataArray0[(dstPixelOffset + dstBandOffset0)] = srcDataArray0[(src_pos + bandOffsets0)];
        


        if (ifracx < ifracdx1)
        {



          src_pos += incxStride;
          ifracx += ifracdx;

        }
        else
        {

          src_pos += incx1Stride;
          ifracx -= ifracdx1;
        }
        
        if (ifracy < ifracdy1)
        {



          src_pos += incyStride;
          ifracy += ifracdy;

        }
        else
        {

          src_pos += incy1Stride;
          ifracy -= ifracdy1;
        }
        

        dstPixelOffset += dstPixelStride;
      }
      
      if ((setBackground) && (clipMinX <= clipMaxX)) {
        for (int x = clipMaxX; x < dst_max_x; x++) {
          dstDataArray0[(dstPixelOffset + dstBandOffset0)] = backgroundByte;
          
          dstPixelOffset += dstPixelStride;
        }
      }
      

      dstOffset += dstScanlineStride;
    }
  }
  




  private void byteLoop_3band(RasterAccessor src, Rectangle destRect, int srcRectX, int srcRectY, RasterAccessor dst)
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
    
    byte[][] dstDataArrays = dst.getByteDataArrays();
    int[] dstBandOffsets = dst.getBandOffsets();
    int dstPixelStride = dst.getPixelStride();
    int dstScanlineStride = dst.getScanlineStride();
    
    byte[] dstDataArray0 = dstDataArrays[0];
    byte[] dstDataArray1 = dstDataArrays[1];
    byte[] dstDataArray2 = dstDataArrays[2];
    
    int dstBandOffset0 = dstBandOffsets[0];
    int dstBandOffset1 = dstBandOffsets[1];
    int dstBandOffset2 = dstBandOffsets[2];
    
    byte[][] srcDataArrays = src.getByteDataArrays();
    int[] bandOffsets = src.getBandOffsets();
    int srcPixelStride = src.getPixelStride();
    int srcScanlineStride = src.getScanlineStride();
    
    byte[] srcDataArray0 = srcDataArrays[0];
    byte[] srcDataArray1 = srcDataArrays[1];
    byte[] srcDataArray2 = srcDataArrays[2];
    
    int bandOffsets0 = bandOffsets[0];
    int bandOffsets1 = bandOffsets[1];
    int bandOffsets2 = bandOffsets[2];
    
    int dst_min_x = x;
    int dst_min_y = y;
    int dst_max_x = x + width;
    int dst_max_y = y + height;
    
    int incxStride = incx * srcPixelStride;
    int incx1Stride = incx1 * srcPixelStride;
    int incyStride = incy * srcScanlineStride;
    int incy1Stride = incy1 * srcScanlineStride;
    
    byte background0 = (byte)(int)backgroundValues[0];
    byte background1 = (byte)(int)backgroundValues[1];
    byte background2 = (byte)(int)backgroundValues[2];
    
    for (int y = dst_min_y; y < dst_max_y; y++) {
      int dstPixelOffset = dstOffset;
      


      dst_pt.setLocation(dst_min_x + 0.5D, y + 0.5D);
      

      mapDestPoint(dst_pt, src_pt);
      

      float s_x = (float)src_pt.getX();
      float s_y = (float)src_pt.getY();
      

      int s_ix = (int)Math.floor(s_x);
      int s_iy = (int)Math.floor(s_y);
      
      double fracx = s_x - s_ix;
      double fracy = s_y - s_iy;
      
      int ifracx = (int)Math.floor(fracx * 1048576.0D);
      int ifracy = (int)Math.floor(fracy * 1048576.0D);
      

      Range clipRange = performScanlineClipping(src_rect_x1, src_rect_y1, src_rect_x2 - 1.0F, src_rect_y2 - 1.0F, s_ix, s_iy, ifracx, ifracy, dst_min_x, dst_max_x, 0, 0, 0, 0);
      








      int clipMinX = ((Integer)clipRange.getMinValue()).intValue();
      int clipMaxX = ((Integer)clipRange.getMaxValue()).intValue();
      

      Point[] startPts = advanceToStartOfScanline(dst_min_x, clipMinX, s_ix, s_iy, ifracx, ifracy);
      

      s_ix = 0x;
      s_iy = 0y;
      ifracx = 1x;
      ifracy = 1y;
      

      int src_pos = (s_iy - srcRectY) * srcScanlineStride + (s_ix - srcRectX) * srcPixelStride;
      

      if (setBackground) {
        for (int x = dst_min_x; x < clipMinX; x++) {
          dstDataArray0[(dstPixelOffset + dstBandOffset0)] = background0;
          
          dstDataArray1[(dstPixelOffset + dstBandOffset1)] = background1;
          
          dstDataArray2[(dstPixelOffset + dstBandOffset2)] = background2;
          

          dstPixelOffset += dstPixelStride;
        }
      } else {
        dstPixelOffset += (clipMinX - dst_min_x) * dstPixelStride;
      }
      for (int x = clipMinX; x < clipMaxX; x++) {
        dstDataArray0[(dstPixelOffset + dstBandOffset0)] = srcDataArray0[(src_pos + bandOffsets0)];
        
        dstDataArray1[(dstPixelOffset + dstBandOffset1)] = srcDataArray1[(src_pos + bandOffsets1)];
        
        dstDataArray2[(dstPixelOffset + dstBandOffset2)] = srcDataArray2[(src_pos + bandOffsets2)];
        


        if (ifracx < ifracdx1)
        {



          src_pos += incxStride;
          ifracx += ifracdx;

        }
        else
        {

          src_pos += incx1Stride;
          ifracx -= ifracdx1;
        }
        
        if (ifracy < ifracdy1)
        {



          src_pos += incyStride;
          ifracy += ifracdy;

        }
        else
        {

          src_pos += incy1Stride;
          ifracy -= ifracdy1;
        }
        

        dstPixelOffset += dstPixelStride;
      }
      
      if ((setBackground) && (clipMinX <= clipMaxX)) {
        for (int x = clipMaxX; x < dst_max_x; x++) {
          dstDataArray0[(dstPixelOffset + dstBandOffset0)] = background0;
          
          dstDataArray1[(dstPixelOffset + dstBandOffset1)] = background1;
          
          dstDataArray2[(dstPixelOffset + dstBandOffset2)] = background2;
          
          dstPixelOffset += dstPixelStride;
        }
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
    
    int incxStride = incx * srcPixelStride;
    int incx1Stride = incx1 * srcPixelStride;
    int incyStride = incy * srcScanlineStride;
    int incy1Stride = incy1 * srcScanlineStride;
    
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
      

      int s_ix = (int)Math.floor(s_x);
      int s_iy = (int)Math.floor(s_y);
      
      double fracx = s_x - s_ix;
      double fracy = s_y - s_iy;
      
      int ifracx = (int)Math.floor(fracx * 1048576.0D);
      int ifracy = (int)Math.floor(fracy * 1048576.0D);
      

      Range clipRange = performScanlineClipping(src_rect_x1, src_rect_y1, src_rect_x2 - 1.0F, src_rect_y2 - 1.0F, s_ix, s_iy, ifracx, ifracy, dst_min_x, dst_max_x, 0, 0, 0, 0);
      








      int clipMinX = ((Integer)clipRange.getMinValue()).intValue();
      int clipMaxX = ((Integer)clipRange.getMaxValue()).intValue();
      

      Point[] startPts = advanceToStartOfScanline(dst_min_x, clipMinX, s_ix, s_iy, ifracx, ifracy);
      

      s_ix = 0x;
      s_iy = 0y;
      ifracx = 1x;
      ifracy = 1y;
      

      int src_pos = (s_iy - srcRectY) * srcScanlineStride + (s_ix - srcRectX) * srcPixelStride;
      

      if (setBackground) {
        for (int x = dst_min_x; x < clipMinX; x++) {
          for (int k2 = 0; k2 < dst_num_bands; k2++) {
            dstDataArrays[k2][(dstPixelOffset + dstBandOffsets[k2])] = backgroundInt[k2];
          }
          
          dstPixelOffset += dstPixelStride;
        }
      } else {
        dstPixelOffset += (clipMinX - dst_min_x) * dstPixelStride;
      }
      for (int x = clipMinX; x < clipMaxX; x++) {
        for (int k2 = 0; k2 < dst_num_bands; k2++) {
          dstDataArrays[k2][(dstPixelOffset + dstBandOffsets[k2])] = srcDataArrays[k2][(src_pos + bandOffsets[k2])];
        }
        



        if (ifracx < ifracdx1)
        {



          src_pos += incxStride;
          ifracx += ifracdx;

        }
        else
        {

          src_pos += incx1Stride;
          ifracx -= ifracdx1;
        }
        
        if (ifracy < ifracdy1)
        {



          src_pos += incyStride;
          ifracy += ifracdy;

        }
        else
        {

          src_pos += incy1Stride;
          ifracy -= ifracdy1;
        }
        
        dstPixelOffset += dstPixelStride;
      }
      
      if ((setBackground) && (clipMinX <= clipMaxX)) {
        for (int x = clipMaxX; x < dst_max_x; x++) {
          for (int k2 = 0; k2 < dst_num_bands; k2++) {
            dstDataArrays[k2][(dstPixelOffset + dstBandOffsets[k2])] = backgroundInt[k2];
          }
          
          dstPixelOffset += dstPixelStride;
        }
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
    
    int incxStride = incx * srcPixelStride;
    int incx1Stride = incx1 * srcPixelStride;
    int incyStride = incy * srcScanlineStride;
    int incy1Stride = incy1 * srcScanlineStride;
    
    short[] backgroundShort = new short[dst_num_bands];
    for (int i = 0; i < dst_num_bands; i++) {
      backgroundShort[i] = ((short)(int)backgroundValues[i]);
    }
    for (int y = dst_min_y; y < dst_max_y; y++) {
      int dstPixelOffset = dstOffset;
      


      dst_pt.setLocation(dst_min_x + 0.5D, y + 0.5D);
      
      mapDestPoint(dst_pt, src_pt);
      

      float s_x = (float)src_pt.getX();
      float s_y = (float)src_pt.getY();
      

      int s_ix = (int)Math.floor(s_x);
      int s_iy = (int)Math.floor(s_y);
      
      double fracx = s_x - s_ix;
      double fracy = s_y - s_iy;
      
      int ifracx = (int)Math.floor(fracx * 1048576.0D);
      int ifracy = (int)Math.floor(fracy * 1048576.0D);
      

      Range clipRange = performScanlineClipping(src_rect_x1, src_rect_y1, src_rect_x2 - 1.0F, src_rect_y2 - 1.0F, s_ix, s_iy, ifracx, ifracy, dst_min_x, dst_max_x, 0, 0, 0, 0);
      








      int clipMinX = ((Integer)clipRange.getMinValue()).intValue();
      int clipMaxX = ((Integer)clipRange.getMaxValue()).intValue();
      

      Point[] startPts = advanceToStartOfScanline(dst_min_x, clipMinX, s_ix, s_iy, ifracx, ifracy);
      

      s_ix = 0x;
      s_iy = 0y;
      ifracx = 1x;
      ifracy = 1y;
      

      int src_pos = (s_iy - srcRectY) * srcScanlineStride + (s_ix - srcRectX) * srcPixelStride;
      

      if (setBackground) {
        for (int x = dst_min_x; x < clipMinX; x++) {
          for (int k2 = 0; k2 < dst_num_bands; k2++) {
            dstDataArrays[k2][(dstPixelOffset + dstBandOffsets[k2])] = backgroundShort[k2];
          }
          
          dstPixelOffset += dstPixelStride;
        }
      } else {
        dstPixelOffset += (clipMinX - dst_min_x) * dstPixelStride;
      }
      for (int x = clipMinX; x < clipMaxX; x++) {
        for (int k2 = 0; k2 < dst_num_bands; k2++) {
          dstDataArrays[k2][(dstPixelOffset + dstBandOffsets[k2])] = srcDataArrays[k2][(src_pos + bandOffsets[k2])];
        }
        



        if (ifracx < ifracdx1)
        {



          src_pos += incxStride;
          ifracx += ifracdx;

        }
        else
        {

          src_pos += incx1Stride;
          ifracx -= ifracdx1;
        }
        
        if (ifracy < ifracdy1)
        {



          src_pos += incyStride;
          ifracy += ifracdy;

        }
        else
        {

          src_pos += incy1Stride;
          ifracy -= ifracdy1;
        }
        
        dstPixelOffset += dstPixelStride;
      }
      
      if ((setBackground) && (clipMinX <= clipMaxX)) {
        for (int x = clipMaxX; x < dst_max_x; x++) {
          for (int k2 = 0; k2 < dst_num_bands; k2++) {
            dstDataArrays[k2][(dstPixelOffset + dstBandOffsets[k2])] = backgroundShort[k2];
          }
          
          dstPixelOffset += dstPixelStride;
        }
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
    
    int incxStride = incx * srcPixelStride;
    int incx1Stride = incx1 * srcPixelStride;
    int incyStride = incy * srcScanlineStride;
    int incy1Stride = incy1 * srcScanlineStride;
    
    float[] backgroundFloat = new float[dst_num_bands];
    for (int i = 0; i < dst_num_bands; i++) {
      backgroundFloat[i] = ((float)backgroundValues[i]);
    }
    for (int y = dst_min_y; y < dst_max_y; y++) {
      int dstPixelOffset = dstOffset;
      


      dst_pt.setLocation(dst_min_x + 0.5D, y + 0.5D);
      
      mapDestPoint(dst_pt, src_pt);
      

      float s_x = (float)src_pt.getX();
      float s_y = (float)src_pt.getY();
      

      int s_ix = (int)Math.floor(s_x);
      int s_iy = (int)Math.floor(s_y);
      
      double fracx = s_x - s_ix;
      double fracy = s_y - s_iy;
      
      int ifracx = (int)Math.floor(fracx * 1048576.0D);
      int ifracy = (int)Math.floor(fracy * 1048576.0D);
      

      Range clipRange = performScanlineClipping(src_rect_x1, src_rect_y1, src_rect_x2 - 1.0F, src_rect_y2 - 1.0F, s_ix, s_iy, ifracx, ifracy, dst_min_x, dst_max_x, 0, 0, 0, 0);
      








      int clipMinX = ((Integer)clipRange.getMinValue()).intValue();
      int clipMaxX = ((Integer)clipRange.getMaxValue()).intValue();
      

      Point[] startPts = advanceToStartOfScanline(dst_min_x, clipMinX, s_ix, s_iy, ifracx, ifracy);
      

      s_ix = 0x;
      s_iy = 0y;
      ifracx = 1x;
      ifracy = 1y;
      

      int src_pos = (s_iy - srcRectY) * srcScanlineStride + (s_ix - srcRectX) * srcPixelStride;
      

      if (setBackground) {
        for (int x = dst_min_x; x < clipMinX; x++) {
          for (int k2 = 0; k2 < dst_num_bands; k2++) {
            dstDataArrays[k2][(dstPixelOffset + dstBandOffsets[k2])] = backgroundFloat[k2];
          }
          
          dstPixelOffset += dstPixelStride;
        }
      } else {
        dstPixelOffset += (clipMinX - dst_min_x) * dstPixelStride;
      }
      for (int x = clipMinX; x < clipMaxX; x++) {
        for (int k2 = 0; k2 < dst_num_bands; k2++) {
          dstDataArrays[k2][(dstPixelOffset + dstBandOffsets[k2])] = srcDataArrays[k2][(src_pos + bandOffsets[k2])];
        }
        



        if (ifracx < ifracdx1)
        {



          src_pos += incxStride;
          ifracx += ifracdx;

        }
        else
        {

          src_pos += incx1Stride;
          ifracx -= ifracdx1;
        }
        
        if (ifracy < ifracdy1)
        {



          src_pos += incyStride;
          ifracy += ifracdy;

        }
        else
        {

          src_pos += incy1Stride;
          ifracy -= ifracdy1;
        }
        
        dstPixelOffset += dstPixelStride;
      }
      
      if ((setBackground) && (clipMinX <= clipMaxX)) {
        for (int x = clipMaxX; x < dst_max_x; x++) {
          for (int k2 = 0; k2 < dst_num_bands; k2++) {
            dstDataArrays[k2][(dstPixelOffset + dstBandOffsets[k2])] = backgroundFloat[k2];
          }
          
          dstPixelOffset += dstPixelStride;
        }
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
    
    int incxStride = incx * srcPixelStride;
    int incx1Stride = incx1 * srcPixelStride;
    int incyStride = incy * srcScanlineStride;
    int incy1Stride = incy1 * srcScanlineStride;
    
    for (int y = dst_min_y; y < dst_max_y; y++) {
      int dstPixelOffset = dstOffset;
      


      dst_pt.setLocation(dst_min_x + 0.5D, y + 0.5D);
      
      mapDestPoint(dst_pt, src_pt);
      

      float s_x = (float)src_pt.getX();
      float s_y = (float)src_pt.getY();
      

      int s_ix = (int)Math.floor(s_x);
      int s_iy = (int)Math.floor(s_y);
      
      double fracx = s_x - s_ix;
      double fracy = s_y - s_iy;
      
      int ifracx = (int)Math.floor(fracx * 1048576.0D);
      int ifracy = (int)Math.floor(fracy * 1048576.0D);
      

      Range clipRange = performScanlineClipping(src_rect_x1, src_rect_y1, src_rect_x2 - 1.0F, src_rect_y2 - 1.0F, s_ix, s_iy, ifracx, ifracy, dst_min_x, dst_max_x, 0, 0, 0, 0);
      





      int clipMinX = ((Integer)clipRange.getMinValue()).intValue();
      int clipMaxX = ((Integer)clipRange.getMaxValue()).intValue();
      

      Point[] startPts = advanceToStartOfScanline(dst_min_x, clipMinX, s_ix, s_iy, ifracx, ifracy);
      

      s_ix = 0x;
      s_iy = 0y;
      ifracx = 1x;
      ifracy = 1y;
      

      int src_pos = (s_iy - srcRectY) * srcScanlineStride + (s_ix - srcRectX) * srcPixelStride;
      

      if (setBackground) {
        for (int x = dst_min_x; x < clipMinX; x++) {
          for (int k2 = 0; k2 < dst_num_bands; k2++) {
            dstDataArrays[k2][(dstPixelOffset + dstBandOffsets[k2])] = backgroundValues[k2];
          }
          
          dstPixelOffset += dstPixelStride;
        }
      } else {
        dstPixelOffset += (clipMinX - dst_min_x) * dstPixelStride;
      }
      for (int x = clipMinX; x < clipMaxX; x++) {
        for (int k2 = 0; k2 < dst_num_bands; k2++) {
          dstDataArrays[k2][(dstPixelOffset + dstBandOffsets[k2])] = srcDataArrays[k2][(src_pos + bandOffsets[k2])];
        }
        



        if (ifracx < ifracdx1)
        {



          src_pos += incxStride;
          ifracx += ifracdx;

        }
        else
        {

          src_pos += incx1Stride;
          ifracx -= ifracdx1;
        }
        
        if (ifracy < ifracdy1)
        {



          src_pos += incyStride;
          ifracy += ifracdy;

        }
        else
        {

          src_pos += incy1Stride;
          ifracy -= ifracdy1;
        }
        
        dstPixelOffset += dstPixelStride;
      }
      
      if ((setBackground) && (clipMinX <= clipMaxX)) {
        for (int x = clipMaxX; x < dst_max_x; x++) {
          for (int k2 = 0; k2 < dst_num_bands; k2++) {
            dstDataArrays[k2][(dstPixelOffset + dstBandOffsets[k2])] = backgroundValues[k2];
          }
          
          dstPixelOffset += dstPixelStride;
        }
      }
      
      dstOffset += dstScanlineStride;
    }
  }
}
