package com.sun.media.jai.opimage;

import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferUShort;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;






























final class TransposeBinaryOpImage
  extends TransposeOpImage
{
  private static ImageLayout layoutHelper(ImageLayout layout, SampleModel sm, ColorModel cm)
  {
    ImageLayout newLayout;
    ImageLayout newLayout;
    if (layout != null) {
      newLayout = (ImageLayout)layout.clone();
    } else {
      newLayout = new ImageLayout();
    }
    
    newLayout.setSampleModel(sm);
    newLayout.setColorModel(cm);
    
    return newLayout;
  }
  

  private static Map configHelper(Map configuration)
  {
    Map config;
    
    Map config;
    if (configuration == null) {
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
  















  public TransposeBinaryOpImage(RenderedImage source, Map config, ImageLayout layout, int type)
  {
    super(source, configHelper(config), layoutHelper(layout, source.getSampleModel(), source.getColorModel()), type);
  }
  






  protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect)
  {
    Raster source = sources[0];
    
    MultiPixelPackedSampleModel mppsm = (MultiPixelPackedSampleModel)source.getSampleModel();
    
    int srcScanlineStride = mppsm.getScanlineStride();
    
    int incr1 = 0;int incr2 = 0;int s_x = 0;int s_y = 0;
    
    int bits = 8;
    int dataType = source.getSampleModel().getDataType();
    if (dataType == 1) {
      bits = 16;
    } else if (dataType == 3) {
      bits = 32;
    }
    
    PlanarImage src = getSource(0);
    int sMinX = src.getMinX();
    int sMinY = src.getMinY();
    int sWidth = src.getWidth();
    int sHeight = src.getHeight();
    int sMaxX = sMinX + sWidth - 1;
    int sMaxY = sMinY + sHeight - 1;
    

    int[] pt = new int[2];
    pt[0] = x;
    pt[1] = y;
    mapPoint(pt, sMinX, sMinY, sMaxX, sMaxY, type, false);
    s_x = pt[0];
    s_y = pt[1];
    

    switch (type) {
    case 0: 
      incr1 = 1;
      incr2 = -bits * srcScanlineStride;
      break;
    
    case 1: 
      incr1 = -1;
      incr2 = bits * srcScanlineStride;
      break;
    
    case 2: 
      incr1 = bits * srcScanlineStride;
      incr2 = 1;
      break;
    
    case 3: 
      incr1 = -bits * srcScanlineStride;
      incr2 = -1;
      break;
    
    case 4: 
      incr1 = -bits * srcScanlineStride;
      incr2 = 1;
      break;
    
    case 5: 
      incr1 = -1;
      incr2 = -bits * srcScanlineStride;
      break;
    
    case 6: 
      incr1 = bits * srcScanlineStride;
      incr2 = -1;
    }
    
    
    switch (source.getSampleModel().getDataType()) {
    case 0: 
      byteLoop(source, dest, destRect, incr1, incr2, s_x, s_y);
      


      break;
    
    case 1: 
    case 2: 
      shortLoop(source, dest, destRect, incr1, incr2, s_x, s_y);
      


      break;
    
    case 3: 
      intLoop(source, dest, destRect, incr1, incr2, s_x, s_y);
    }
    
  }
  





  private void byteLoop(Raster source, WritableRaster dest, Rectangle destRect, int incr1, int incr2, int s_x, int s_y)
  {
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
    
    int dx = x;
    int dy = y;
    int dwidth = width;
    int dheight = height;
    
    int sourceOffset = 8 * (s_y - sourceTransY) * sourceScanlineStride + 8 * sourceDBOffset + (s_x - sourceTransX) + sourceDataBitOffset;
    




    int destOffset = 8 * (dy - destTransY) * destScanlineStride + 8 * destDBOffset + (dx - destTransX) + destDataBitOffset;
    




    for (int j = 0; j < dheight; j++) {
      int sOffset = sourceOffset;
      int dOffset = destOffset;
      

      int i = 0;
      while ((i < dwidth) && ((dOffset & 0x7) != 0)) {
        int selement = sourceData[(sOffset >> 3)];
        int val = selement >> 7 - (sOffset & 0x7) & 0x1;
        
        int dindex = dOffset >> 3;
        int dshift = 7 - (dOffset & 0x7);
        int delement = destData[dindex];
        delement |= val << dshift;
        destData[dindex] = ((byte)delement);
        
        sOffset += incr1;
        dOffset++;
        i++;
      }
      
      int dindex = dOffset >> 3;
      if ((incr1 & 0x7) == 0)
      {




        int shift = 7 - (sOffset & 0x7);
        int offset = sOffset >> 3;
        int incr = incr1 >> 3;
        
        while (i < dwidth - 7) {
          int selement = sourceData[offset];
          int val = selement >> shift & 0x1;
          int delement = val << 7;
          offset += incr;
          
          selement = sourceData[offset];
          val = selement >> shift & 0x1;
          delement |= val << 6;
          offset += incr;
          
          selement = sourceData[offset];
          val = selement >> shift & 0x1;
          delement |= val << 5;
          offset += incr;
          
          selement = sourceData[offset];
          val = selement >> shift & 0x1;
          delement |= val << 4;
          offset += incr;
          
          selement = sourceData[offset];
          val = selement >> shift & 0x1;
          delement |= val << 3;
          offset += incr;
          
          selement = sourceData[offset];
          val = selement >> shift & 0x1;
          delement |= val << 2;
          offset += incr;
          
          selement = sourceData[offset];
          val = selement >> shift & 0x1;
          delement |= val << 1;
          offset += incr;
          
          selement = sourceData[offset];
          val = selement >> shift & 0x1;
          delement |= val;
          offset += incr;
          
          destData[dindex] = ((byte)delement);
          
          sOffset += 8 * incr1;
          dOffset += 8;
          i += 8;
          dindex++;
        }
        

      }
      else
      {
        while (i < dwidth - 7) {
          int selement = sourceData[(sOffset >> 3)];
          int val = selement >> 7 - (sOffset & 0x7) & 0x1;
          int delement = val << 7;
          sOffset += incr1;
          
          selement = sourceData[(sOffset >> 3)];
          val = selement >> 7 - (sOffset & 0x7) & 0x1;
          delement |= val << 6;
          sOffset += incr1;
          
          selement = sourceData[(sOffset >> 3)];
          val = selement >> 7 - (sOffset & 0x7) & 0x1;
          delement |= val << 5;
          sOffset += incr1;
          
          selement = sourceData[(sOffset >> 3)];
          val = selement >> 7 - (sOffset & 0x7) & 0x1;
          delement |= val << 4;
          sOffset += incr1;
          
          selement = sourceData[(sOffset >> 3)];
          val = selement >> 7 - (sOffset & 0x7) & 0x1;
          delement |= val << 3;
          sOffset += incr1;
          
          selement = sourceData[(sOffset >> 3)];
          val = selement >> 7 - (sOffset & 0x7) & 0x1;
          delement |= val << 2;
          sOffset += incr1;
          
          selement = sourceData[(sOffset >> 3)];
          val = selement >> 7 - (sOffset & 0x7) & 0x1;
          delement |= val << 1;
          sOffset += incr1;
          
          selement = sourceData[(sOffset >> 3)];
          val = selement >> 7 - (sOffset & 0x7) & 0x1;
          delement |= val;
          sOffset += incr1;
          
          destData[dindex] = ((byte)delement);
          
          dOffset += 8;
          i += 8;
          dindex++;
        }
      }
      
      while (i < dwidth) {
        int selement = sourceData[(sOffset >> 3)];
        int val = selement >> 7 - (sOffset & 0x7) & 0x1;
        
        dindex = dOffset >> 3;
        int dshift = 7 - (dOffset & 0x7);
        int delement = destData[dindex];
        delement |= val << dshift;
        destData[dindex] = ((byte)delement);
        
        sOffset += incr1;
        dOffset++;
        i++;
      }
      
      sourceOffset += incr2;
      destOffset += 8 * destScanlineStride;
    }
  }
  


  private void shortLoop(Raster source, Raster dest, Rectangle destRect, int incr1, int incr2, int s_x, int s_y)
  {
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
    
    int dx = x;
    int dy = y;
    int dwidth = width;
    int dheight = height;
    
    int sourceOffset = 16 * (s_y - sourceTransY) * sourceScanlineStride + 16 * sourceDBOffset + (s_x - sourceTransX) + sourceDataBitOffset;
    




    int destOffset = 16 * (dy - destTransY) * destScanlineStride + 16 * destDBOffset + (dx - destTransX) + destDataBitOffset;
    




    for (int j = 0; j < dheight; j++) {
      int sOffset = sourceOffset;
      int dOffset = destOffset;
      

      int i = 0;
      while ((i < dwidth) && ((dOffset & 0xF) != 0)) {
        int selement = sourceData[(sOffset >> 4)];
        int val = selement >> 15 - (sOffset & 0xF) & 0x1;
        
        int dindex = dOffset >> 4;
        int dshift = 15 - (dOffset & 0xF);
        int delement = destData[dindex];
        delement |= val << dshift;
        destData[dindex] = ((short)delement);
        
        sOffset += incr1;
        dOffset++;
        i++;
      }
      
      int dindex = dOffset >> 4;
      if ((incr1 & 0xF) == 0) {
        int shift = 15 - (sOffset & 0x5);
        int offset = sOffset >> 4;
        int incr = incr1 >> 4;
        
        while (i < dwidth - 15) {
          int delement = 0;
          for (int b = 15; b >= 0; b--) {
            int selement = sourceData[offset];
            int val = selement >> shift & 0x1;
            delement |= val << b;
            offset += incr;
          }
          
          destData[dindex] = ((short)delement);
          
          sOffset += 16 * incr1;
          dOffset += 16;
          i += 16;
          dindex++;
        }
      } else {
        while (i < dwidth - 15) {
          int delement = 0;
          for (int b = 15; b >= 0; b--) {
            int selement = sourceData[(sOffset >> 4)];
            int val = selement >> 15 - (sOffset & 0xF) & 0x1;
            delement |= val << b;
            sOffset += incr1;
          }
          
          destData[dindex] = ((short)delement);
          
          dOffset += 15;
          i += 16;
          dindex++;
        }
      }
      
      while (i < dwidth) {
        int selement = sourceData[(sOffset >> 4)];
        int val = selement >> 15 - (sOffset & 0xF) & 0x1;
        
        dindex = dOffset >> 4;
        int dshift = 15 - (dOffset & 0xF);
        int delement = destData[dindex];
        delement |= val << dshift;
        destData[dindex] = ((short)delement);
        
        sOffset += incr1;
        dOffset++;
        i++;
      }
      
      sourceOffset += incr2;
      destOffset += 16 * destScanlineStride;
    }
  }
  


  private void intLoop(Raster source, Raster dest, Rectangle destRect, int incr1, int incr2, int s_x, int s_y)
  {
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
    
    int dx = x;
    int dy = y;
    int dwidth = width;
    int dheight = height;
    
    int sourceOffset = 32 * (s_y - sourceTransY) * sourceScanlineStride + 32 * sourceDBOffset + (s_x - sourceTransX) + sourceDataBitOffset;
    




    int destOffset = 32 * (dy - destTransY) * destScanlineStride + 32 * destDBOffset + (dx - destTransX) + destDataBitOffset;
    




    for (int j = 0; j < dheight; j++) {
      int sOffset = sourceOffset;
      int dOffset = destOffset;
      

      int i = 0;
      while ((i < dwidth) && ((dOffset & 0x1F) != 0)) {
        int selement = sourceData[(sOffset >> 5)];
        int val = selement >> 31 - (sOffset & 0x1F) & 0x1;
        
        int dindex = dOffset >> 5;
        int dshift = 31 - (dOffset & 0x1F);
        int delement = destData[dindex];
        delement |= val << dshift;
        destData[dindex] = delement;
        
        sOffset += incr1;
        dOffset++;
        i++;
      }
      
      int dindex = dOffset >> 5;
      if ((incr1 & 0x1F) == 0) {
        int shift = 31 - (sOffset & 0x5);
        int offset = sOffset >> 5;
        int incr = incr1 >> 5;
        
        while (i < dwidth - 31) {
          int delement = 0;
          for (int b = 31; b >= 0; b--) {
            int selement = sourceData[offset];
            int val = selement >> shift & 0x1;
            delement |= val << b;
            offset += incr;
          }
          
          destData[dindex] = delement;
          
          sOffset += 32 * incr1;
          dOffset += 32;
          i += 32;
          dindex++;
        }
      } else {
        while (i < dwidth - 31) {
          int delement = 0;
          for (int b = 31; b >= 0; b--) {
            int selement = sourceData[(sOffset >> 5)];
            int val = selement >> 31 - (sOffset & 0x1F) & 0x1;
            delement |= val << b;
            sOffset += incr1;
          }
          
          destData[dindex] = delement;
          
          dOffset += 31;
          i += 32;
          dindex++;
        }
      }
      
      while (i < dwidth) {
        int selement = sourceData[(sOffset >> 5)];
        int val = selement >> 31 - (sOffset & 0x1F) & 0x1;
        
        dindex = dOffset >> 5;
        int dshift = 31 - (dOffset & 0x1F);
        int delement = destData[dindex];
        delement |= val << dshift;
        destData[dindex] = delement;
        
        sOffset += incr1;
        dOffset++;
        i++;
      }
      
      sourceOffset += incr2;
      destOffset += 32 * destScanlineStride;
    }
  }
}
