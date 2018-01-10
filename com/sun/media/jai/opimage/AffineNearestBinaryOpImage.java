package com.sun.media.jai.opimage;

import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferUShort;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.util.Range;



















final class AffineNearestBinaryOpImage
  extends AffineNearestOpImage
{
  private int black = 0;
  

  private static Map configHelper(Map configuration)
  {
    Map config;
    
    Map config;
    if (configuration == null)
    {
      config = new RenderingHints(JAI.KEY_REPLACE_INDEX_COLOR_MODEL, Boolean.FALSE);

    }
    else
    {
      config = configuration;
      
      if (!config.containsKey(JAI.KEY_REPLACE_INDEX_COLOR_MODEL)) {
        RenderingHints hints = (RenderingHints)configuration;
        config = (RenderingHints)hints.clone();
        config.put(JAI.KEY_REPLACE_INDEX_COLOR_MODEL, Boolean.FALSE);
      }
    }
    
    return config;
  }
  














  public AffineNearestBinaryOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, AffineTransform transform, Interpolation interp, double[] backgroundValues)
  {
    super(source, extender, configHelper(config), layout, transform, interp, backgroundValues);
    







    if (layout != null) {
      colorModel = layout.getColorModel(source);
    } else {
      colorModel = source.getColorModel();
    }
    sampleModel = source.getSampleModel().createCompatibleSampleModel(tileWidth, tileHeight);
  }
  

























  protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect)
  {
    Raster source = sources[0];
    
    switch (source.getSampleModel().getDataType()) {
    case 0: 
      byteLoop(source, dest, destRect);
      break;
    
    case 3: 
      intLoop(source, dest, destRect);
      break;
    
    case 1: 
    case 2: 
      shortLoop(source, dest, destRect);
    }
    
  }
  

  private void byteLoop(Raster source, WritableRaster dest, Rectangle destRect)
  {
    float src_rect_x1 = source.getMinX();
    float src_rect_y1 = source.getMinY();
    float src_rect_x2 = src_rect_x1 + source.getWidth();
    float src_rect_y2 = src_rect_y1 + source.getHeight();
    
    MultiPixelPackedSampleModel sourceSM = (MultiPixelPackedSampleModel)source.getSampleModel();
    
    DataBufferByte sourceDB = (DataBufferByte)source.getDataBuffer();
    
    int sourceTransX = source.getSampleModelTranslateX();
    int sourceTransY = source.getSampleModelTranslateY();
    int sourceDataBitOffset = sourceSM.getDataBitOffset();
    int sourceScanlineStride = sourceSM.getScanlineStride();
    
    MultiPixelPackedSampleModel destSM = (MultiPixelPackedSampleModel)dest.getSampleModel();
    
    DataBufferByte destDB = (DataBufferByte)dest.getDataBuffer();
    
    int destMinX = dest.getMinX();
    int destMinY = dest.getMinY();
    int destTransX = dest.getSampleModelTranslateX();
    int destTransY = dest.getSampleModelTranslateY();
    int destDataBitOffset = destSM.getDataBitOffset();
    int destScanlineStride = destSM.getScanlineStride();
    
    byte[] sourceData = sourceDB.getData();
    int sourceDBOffset = sourceDB.getOffset();
    
    byte[] destData = destDB.getData();
    int destDBOffset = destDB.getOffset();
    
    Point2D dst_pt = new Point2D.Float();
    Point2D src_pt = new Point2D.Float();
    
    int dst_min_x = x;
    int dst_min_y = y;
    int dst_max_x = x + width;
    int dst_max_y = y + height;
    
    int incyStride = incy * sourceScanlineStride;
    int incy1Stride = incy1 * sourceScanlineStride;
    
    black = ((int)backgroundValues[0] & 0x1);
    
    for (int y = dst_min_y; y < dst_max_y; y++)
    {

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
      
      int start_s_ix = s_ix;
      int start_s_iy = s_iy;
      int start_ifracx = ifracx;
      int start_ifracy = ifracy;
      

      Range clipRange = performScanlineClipping(src_rect_x1, src_rect_y1, src_rect_x2 - 1.0F, src_rect_y2 - 1.0F, s_ix, s_iy, ifracx, ifracy, dst_min_x, dst_max_x, 0, 0, 0, 0);
      








      int clipMinX = ((Integer)clipRange.getMinValue()).intValue();
      int clipMaxX = ((Integer)clipRange.getMaxValue()).intValue();
      
      if (clipMinX <= clipMaxX)
      {
        int destYOffset = (y - destTransY) * destScanlineStride + destDBOffset;
        
        int destXOffset = destDataBitOffset + (dst_min_x - destTransX);
        
        int sourceYOffset = (s_iy - sourceTransY) * sourceScanlineStride + sourceDBOffset;
        
        int sourceXOffset = s_ix - sourceTransX + sourceDataBitOffset;
        

        for (int x = dst_min_x; x < clipMinX; x++)
        {
          if (setBackground) {
            int dindex = destYOffset + (destXOffset >> 3);
            int dshift = 7 - (destXOffset & 0x7);
            int delement = destData[dindex];
            delement |= black << dshift;
            destData[dindex] = ((byte)delement);
          }
          

          if (ifracx < ifracdx1)
          {



            ifracx += ifracdx;
            sourceXOffset += incx;

          }
          else
          {

            ifracx -= ifracdx1;
            sourceXOffset += incx1;
          }
          
          if (ifracy < ifracdy1)
          {



            ifracy += ifracdy;
            sourceYOffset += incyStride;

          }
          else
          {

            ifracy -= ifracdy1;
            sourceYOffset += incy1Stride;
          }
          
          destXOffset++;
        }
        
        for (int x = clipMinX; x < clipMaxX; x++) {
          int sindex = sourceYOffset + (sourceXOffset >> 3);
          byte selement = sourceData[sindex];
          int val = selement >> 7 - (sourceXOffset & 0x7) & 0x1;
          
          int dindex = destYOffset + (destXOffset >> 3);
          int dshift = 7 - (destXOffset & 0x7);
          int delement = destData[dindex];
          delement |= val << dshift;
          destData[dindex] = ((byte)delement);
          

          if (ifracx < ifracdx1)
          {



            ifracx += ifracdx;
            sourceXOffset += incx;

          }
          else
          {

            ifracx -= ifracdx1;
            sourceXOffset += incx1;
          }
          
          if (ifracy < ifracdy1)
          {



            ifracy += ifracdy;
            sourceYOffset += incyStride;

          }
          else
          {

            ifracy -= ifracdy1;
            sourceYOffset += incy1Stride;
          }
          
          destXOffset++;
        }
        
        for (int x = clipMaxX; x < dst_max_x; x++)
        {
          if (setBackground) {
            int dindex = destYOffset + (destXOffset >> 3);
            int dshift = 7 - (destXOffset & 0x7);
            int delement = destData[dindex];
            delement |= black << dshift;
            destData[dindex] = ((byte)delement);
          }
          

          if (ifracx < ifracdx1)
          {



            ifracx += ifracdx;
            sourceXOffset += incx;

          }
          else
          {

            ifracx -= ifracdx1;
            sourceXOffset += incx1;
          }
          
          if (ifracy < ifracdy1)
          {



            ifracy += ifracdy;
            sourceYOffset += incyStride;

          }
          else
          {

            ifracy -= ifracdy1;
            sourceYOffset += incy1Stride;
          }
          
          destXOffset++;
        }
      }
    }
  }
  
  private void shortLoop(Raster source, WritableRaster dest, Rectangle destRect)
  {
    float src_rect_x1 = source.getMinX();
    float src_rect_y1 = source.getMinY();
    float src_rect_x2 = src_rect_x1 + source.getWidth();
    float src_rect_y2 = src_rect_y1 + source.getHeight();
    
    MultiPixelPackedSampleModel sourceSM = (MultiPixelPackedSampleModel)source.getSampleModel();
    
    DataBufferUShort sourceDB = (DataBufferUShort)source.getDataBuffer();
    
    int sourceTransX = source.getSampleModelTranslateX();
    int sourceTransY = source.getSampleModelTranslateY();
    int sourceDataBitOffset = sourceSM.getDataBitOffset();
    int sourceScanlineStride = sourceSM.getScanlineStride();
    
    MultiPixelPackedSampleModel destSM = (MultiPixelPackedSampleModel)dest.getSampleModel();
    
    DataBufferUShort destDB = (DataBufferUShort)dest.getDataBuffer();
    
    int destMinX = dest.getMinX();
    int destMinY = dest.getMinY();
    int destTransX = dest.getSampleModelTranslateX();
    int destTransY = dest.getSampleModelTranslateY();
    int destDataBitOffset = destSM.getDataBitOffset();
    int destScanlineStride = destSM.getScanlineStride();
    
    short[] sourceData = sourceDB.getData();
    int sourceDBOffset = sourceDB.getOffset();
    
    short[] destData = destDB.getData();
    int destDBOffset = destDB.getOffset();
    
    Point2D dst_pt = new Point2D.Float();
    Point2D src_pt = new Point2D.Float();
    
    int dst_min_x = x;
    int dst_min_y = y;
    int dst_max_x = x + width;
    int dst_max_y = y + height;
    
    int incyStride = incy * sourceScanlineStride;
    int incy1Stride = incy1 * sourceScanlineStride;
    
    black = ((int)backgroundValues[0] & 0x1);
    
    for (int y = dst_min_y; y < dst_max_y; y++)
    {

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
      
      int start_s_ix = s_ix;
      int start_s_iy = s_iy;
      int start_ifracx = ifracx;
      int start_ifracy = ifracy;
      

      Range clipRange = performScanlineClipping(src_rect_x1, src_rect_y1, src_rect_x2 - 1.0F, src_rect_y2 - 1.0F, s_ix, s_iy, ifracx, ifracy, dst_min_x, dst_max_x, 0, 0, 0, 0);
      








      int clipMinX = ((Integer)clipRange.getMinValue()).intValue();
      int clipMaxX = ((Integer)clipRange.getMaxValue()).intValue();
      
      if (clipMinX <= clipMaxX)
      {
        int destYOffset = (y - destTransY) * destScanlineStride + destDBOffset;
        
        int destXOffset = destDataBitOffset + (dst_min_x - destTransX);
        
        int sourceYOffset = (s_iy - sourceTransY) * sourceScanlineStride + sourceDBOffset;
        
        int sourceXOffset = s_ix - sourceTransX + sourceDataBitOffset;
        

        for (int x = dst_min_x; x < clipMinX; x++)
        {
          if (setBackground) {
            int dindex = destYOffset + (destXOffset >> 4);
            int dshift = 15 - (destXOffset & 0xF);
            int delement = destData[dindex];
            delement |= black << dshift;
            destData[dindex] = ((short)delement);
          }
          

          if (ifracx < ifracdx1)
          {



            ifracx += ifracdx;
            sourceXOffset += incx;

          }
          else
          {

            ifracx -= ifracdx1;
            sourceXOffset += incx1;
          }
          
          if (ifracy < ifracdy1)
          {



            ifracy += ifracdy;
            sourceYOffset += incyStride;

          }
          else
          {

            ifracy -= ifracdy1;
            sourceYOffset += incy1Stride;
          }
          
          destXOffset++;
        }
        
        for (int x = clipMinX; x < clipMaxX; x++) {
          int sindex = sourceYOffset + (sourceXOffset >> 4);
          short selement = sourceData[sindex];
          int val = selement >> 15 - (sourceXOffset & 0xF) & 0x1;
          
          int dindex = destYOffset + (destXOffset >> 4);
          int dshift = 15 - (destXOffset & 0xF);
          int delement = destData[dindex];
          delement |= val << dshift;
          destData[dindex] = ((short)delement);
          

          if (ifracx < ifracdx1)
          {



            ifracx += ifracdx;
            sourceXOffset += incx;

          }
          else
          {

            ifracx -= ifracdx1;
            sourceXOffset += incx1;
          }
          
          if (ifracy < ifracdy1)
          {



            ifracy += ifracdy;
            sourceYOffset += incyStride;

          }
          else
          {

            ifracy -= ifracdy1;
            sourceYOffset += incy1Stride;
          }
          
          destXOffset++;
        }
        
        for (int x = clipMaxX; x < dst_max_x; x++) {
          if (setBackground) {
            int dindex = destYOffset + (destXOffset >> 4);
            int dshift = 15 - (destXOffset & 0xF);
            int delement = destData[dindex];
            delement |= black << dshift;
            destData[dindex] = ((short)delement);
          }
          

          if (ifracx < ifracdx1)
          {



            ifracx += ifracdx;
            sourceXOffset += incx;

          }
          else
          {

            ifracx -= ifracdx1;
            sourceXOffset += incx1;
          }
          
          if (ifracy < ifracdy1)
          {



            ifracy += ifracdy;
            sourceYOffset += incyStride;

          }
          else
          {

            ifracy -= ifracdy1;
            sourceYOffset += incy1Stride;
          }
          
          destXOffset++;
        }
      }
    }
  }
  
  private void intLoop(Raster source, WritableRaster dest, Rectangle destRect)
  {
    float src_rect_x1 = source.getMinX();
    float src_rect_y1 = source.getMinY();
    float src_rect_x2 = src_rect_x1 + source.getWidth();
    float src_rect_y2 = src_rect_y1 + source.getHeight();
    
    MultiPixelPackedSampleModel sourceSM = (MultiPixelPackedSampleModel)source.getSampleModel();
    
    DataBufferInt sourceDB = (DataBufferInt)source.getDataBuffer();
    
    int sourceTransX = source.getSampleModelTranslateX();
    int sourceTransY = source.getSampleModelTranslateY();
    int sourceDataBitOffset = sourceSM.getDataBitOffset();
    int sourceScanlineStride = sourceSM.getScanlineStride();
    
    MultiPixelPackedSampleModel destSM = (MultiPixelPackedSampleModel)dest.getSampleModel();
    
    DataBufferInt destDB = (DataBufferInt)dest.getDataBuffer();
    
    int destMinX = dest.getMinX();
    int destMinY = dest.getMinY();
    int destTransX = dest.getSampleModelTranslateX();
    int destTransY = dest.getSampleModelTranslateY();
    int destDataBitOffset = destSM.getDataBitOffset();
    int destScanlineStride = destSM.getScanlineStride();
    
    int[] sourceData = sourceDB.getData();
    int sourceDBOffset = sourceDB.getOffset();
    
    int[] destData = destDB.getData();
    int destDBOffset = destDB.getOffset();
    
    Point2D dst_pt = new Point2D.Float();
    Point2D src_pt = new Point2D.Float();
    
    int dst_min_x = x;
    int dst_min_y = y;
    int dst_max_x = x + width;
    int dst_max_y = y + height;
    
    int incyStride = incy * sourceScanlineStride;
    int incy1Stride = incy1 * sourceScanlineStride;
    
    black = ((int)backgroundValues[0] & 0x1);
    
    for (int y = dst_min_y; y < dst_max_y; y++)
    {

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
      
      int start_s_ix = s_ix;
      int start_s_iy = s_iy;
      int start_ifracx = ifracx;
      int start_ifracy = ifracy;
      

      Range clipRange = performScanlineClipping(src_rect_x1, src_rect_y1, src_rect_x2 - 1.0F, src_rect_y2 - 1.0F, s_ix, s_iy, ifracx, ifracy, dst_min_x, dst_max_x, 0, 0, 0, 0);
      








      int clipMinX = ((Integer)clipRange.getMinValue()).intValue();
      int clipMaxX = ((Integer)clipRange.getMaxValue()).intValue();
      
      if (clipMinX <= clipMaxX)
      {
        int destYOffset = (y - destTransY) * destScanlineStride + destDBOffset;
        
        int destXOffset = destDataBitOffset + (dst_min_x - destTransX);
        
        int sourceYOffset = (s_iy - sourceTransY) * sourceScanlineStride + sourceDBOffset;
        
        int sourceXOffset = s_ix - sourceTransX + sourceDataBitOffset;
        

        for (int x = dst_min_x; x < clipMinX; x++) {
          if (setBackground) {
            int dindex = destYOffset + (destXOffset >> 5);
            int dshift = 31 - (destXOffset & 0x1F);
            int delement = destData[dindex];
            delement |= black << dshift;
            destData[dindex] = delement;
          }
          

          if (ifracx < ifracdx1)
          {



            ifracx += ifracdx;
            sourceXOffset += incx;

          }
          else
          {

            ifracx -= ifracdx1;
            sourceXOffset += incx1;
          }
          
          if (ifracy < ifracdy1)
          {



            ifracy += ifracdy;
            sourceYOffset += incyStride;

          }
          else
          {

            ifracy -= ifracdy1;
            sourceYOffset += incy1Stride;
          }
          
          destXOffset++;
        }
        
        for (int x = clipMinX; x < clipMaxX; x++) {
          int sindex = sourceYOffset + (sourceXOffset >> 5);
          int selement = sourceData[sindex];
          int val = selement >> 31 - (sourceXOffset & 0x1F) & 0x1;
          
          int dindex = destYOffset + (destXOffset >> 5);
          int dshift = 31 - (destXOffset & 0x1F);
          int delement = destData[dindex];
          delement |= val << dshift;
          destData[dindex] = delement;
          

          if (ifracx < ifracdx1)
          {



            ifracx += ifracdx;
            sourceXOffset += incx;

          }
          else
          {

            ifracx -= ifracdx1;
            sourceXOffset += incx1;
          }
          
          if (ifracy < ifracdy1)
          {



            ifracy += ifracdy;
            sourceYOffset += incyStride;

          }
          else
          {

            ifracy -= ifracdy1;
            sourceYOffset += incy1Stride;
          }
          
          destXOffset++;
        }
        
        for (int x = clipMaxX; x < dst_max_x; x++) {
          if (setBackground) {
            int dindex = destYOffset + (destXOffset >> 5);
            int dshift = 31 - (destXOffset & 0x1F);
            int delement = destData[dindex];
            delement |= black << dshift;
            destData[dindex] = delement;
          }
          

          if (ifracx < ifracdx1)
          {



            ifracx += ifracdx;
            sourceXOffset += incx;

          }
          else
          {

            ifracx -= ifracdx1;
            sourceXOffset += incx1;
          }
          
          if (ifracy < ifracdy1)
          {



            ifracy += ifracdy;
            sourceYOffset += incyStride;

          }
          else
          {

            ifracy -= ifracdy1;
            sourceYOffset += incy1Stride;
          }
          
          destXOffset++;
        }
      }
    }
  }
}
