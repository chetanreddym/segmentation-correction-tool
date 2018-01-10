package com.sun.media.jai.opimage;

import com.sun.media.jai.util.Rational;
import java.awt.Rectangle;
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
import javax.media.jai.ScaleOpImage;








































final class ScaleNearestBinaryOpImage
  extends ScaleOpImage
{
  long invScaleXInt;
  long invScaleXFrac;
  long invScaleYInt;
  long invScaleYFrac;
  
  public ScaleNearestBinaryOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, float xScale, float yScale, float xTrans, float yTrans, Interpolation interp)
  {
    super(source, layout, config, true, extender, interp, xScale, yScale, xTrans, yTrans);
    










    if (layout != null) {
      colorModel = layout.getColorModel(source);
    } else {
      colorModel = source.getColorModel();
    }
    sampleModel = source.getSampleModel().createCompatibleSampleModel(tileWidth, tileHeight);
    


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
    Raster source = sources[0];
    

    Rectangle srcRect = source.getBounds();
    
    int srcRectX = x;
    int srcRectY = y;
    

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
      xvalues[i] = srcXInt;
      




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
      yvalues[i] = srcYInt;
      




      srcYInt = (int)(srcYInt + invScaleYInt);
      


      srcYFrac += newInvScaleYFrac;
      



      if (srcYFrac >= commonYDenom) {
        srcYInt++;
        srcYFrac -= commonYDenom;
      }
    }
    
    switch (source.getSampleModel().getDataType()) {
    case 0: 
      byteLoop(source, dest, destRect, xvalues, yvalues);
      break;
    
    case 1: 
    case 2: 
      shortLoop(source, dest, destRect, xvalues, yvalues);
      break;
    
    case 3: 
      intLoop(source, dest, destRect, xvalues, yvalues);
      break;
    
    default: 
      throw new RuntimeException(JaiI18N.getString("OrderedDitherOpImage0"));
    }
    
  }
  

  private void byteLoop(Raster source, WritableRaster dest, Rectangle destRect, int[] xvalues, int[] yvalues)
  {
    int dx = x;
    int dy = y;
    int dwidth = width;
    int dheight = height;
    
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
    
    int[] sbytenum = new int[dwidth];
    int[] sshift = new int[dwidth];
    
    for (int i = 0; i < dwidth; i++) {
      int x = xvalues[i];
      int sbitnum = sourceDataBitOffset + (x - sourceTransX);
      sbytenum[i] = (sbitnum >> 3);
      sshift[i] = (7 - (sbitnum & 0x7));
    }
    
    for (int j = 0; j < dheight; j++) {
      int y = yvalues[j];
      
      int sourceYOffset = (y - sourceTransY) * sourceScanlineStride + sourceDBOffset;
      
      int destYOffset = (j + dy - destTransY) * destScanlineStride + destDBOffset;
      

      int dbitnum = destDataBitOffset + (dx - destTransX);
      


      int i = 0;
      while ((i < dwidth) && ((dbitnum & 0x7) != 0)) {
        int selement = sourceData[(sourceYOffset + sbytenum[i])];
        int val = selement >> sshift[i] & 0x1;
        int dindex = destYOffset + (dbitnum >> 3);
        int dshift = 7 - (dbitnum & 0x7);
        int delement = destData[dindex];
        delement |= val << dshift;
        destData[dindex] = ((byte)delement);
        dbitnum++;
        i++;
      }
      
      int dindex = destYOffset + (dbitnum >> 3);
      int nbytes = dwidth - i + 1 >> 3;
      
      if ((nbytes > 0) && (j > 0) && (y == yvalues[(j - 1)]))
      {
        System.arraycopy(destData, dindex - destScanlineStride, destData, dindex, nbytes);
        

        i += nbytes * 8;
        dbitnum += nbytes * 8;
      } else {
        while (i < dwidth - 7) {
          int selement = sourceData[(sourceYOffset + sbytenum[i])];
          int val = selement >> sshift[i] & 0x1;
          
          int delement = val << 7;
          i++;
          
          selement = sourceData[(sourceYOffset + sbytenum[i])];
          val = selement >> sshift[i] & 0x1;
          
          delement |= val << 6;
          i++;
          
          selement = sourceData[(sourceYOffset + sbytenum[i])];
          val = selement >> sshift[i] & 0x1;
          
          delement |= val << 5;
          i++;
          
          selement = sourceData[(sourceYOffset + sbytenum[i])];
          val = selement >> sshift[i] & 0x1;
          
          delement |= val << 4;
          i++;
          
          selement = sourceData[(sourceYOffset + sbytenum[i])];
          val = selement >> sshift[i] & 0x1;
          
          delement |= val << 3;
          i++;
          
          selement = sourceData[(sourceYOffset + sbytenum[i])];
          val = selement >> sshift[i] & 0x1;
          
          delement |= val << 2;
          i++;
          
          selement = sourceData[(sourceYOffset + sbytenum[i])];
          val = selement >> sshift[i] & 0x1;
          
          delement |= val << 1;
          i++;
          
          selement = sourceData[(sourceYOffset + sbytenum[i])];
          val = selement >> sshift[i] & 0x1;
          
          delement |= val;
          i++;
          
          destData[(dindex++)] = ((byte)delement);
          dbitnum += 8;
        }
      }
      
      if (i < dwidth) {
        dindex = destYOffset + (dbitnum >> 3);
        int delement = destData[dindex];
        while (i < dwidth) {
          int selement = sourceData[(sourceYOffset + sbytenum[i])];
          int val = selement >> sshift[i] & 0x1;
          
          int dshift = 7 - (dbitnum & 0x7);
          delement |= val << dshift;
          dbitnum++;
          i++;
        }
        destData[dindex] = ((byte)delement);
      }
    }
  }
  

  private void shortLoop(Raster source, WritableRaster dest, Rectangle destRect, int[] xvalues, int[] yvalues)
  {
    int dx = x;
    int dy = y;
    int dwidth = width;
    int dheight = height;
    
    MultiPixelPackedSampleModel sourceSM = (MultiPixelPackedSampleModel)source.getSampleModel();
    
    int sourceTransX = source.getSampleModelTranslateX();
    int sourceTransY = source.getSampleModelTranslateY();
    int sourceDataBitOffset = sourceSM.getDataBitOffset();
    int sourceScanlineStride = sourceSM.getScanlineStride();
    
    MultiPixelPackedSampleModel destSM = (MultiPixelPackedSampleModel)dest.getSampleModel();
    
    int destMinX = dest.getMinX();
    int destMinY = dest.getMinY();
    int destTransX = dest.getSampleModelTranslateX();
    int destTransY = dest.getSampleModelTranslateY();
    int destDataBitOffset = destSM.getDataBitOffset();
    int destScanlineStride = destSM.getScanlineStride();
    
    DataBufferUShort sourceDB = (DataBufferUShort)source.getDataBuffer();
    short[] sourceData = sourceDB.getData();
    int sourceDBOffset = sourceDB.getOffset();
    
    DataBufferUShort destDB = (DataBufferUShort)dest.getDataBuffer();
    short[] destData = destDB.getData();
    int destDBOffset = destDB.getOffset();
    
    int[] sshortnum = new int[dwidth];
    int[] sshift = new int[dwidth];
    
    for (int i = 0; i < dwidth; i++) {
      int x = xvalues[i];
      int sbitnum = sourceDataBitOffset + (x - sourceTransX);
      sshortnum[i] = (sbitnum >> 4);
      sshift[i] = (15 - (sbitnum & 0xF));
    }
    
    for (int j = 0; j < dheight; j++) {
      int y = yvalues[j];
      
      int sourceYOffset = (y - sourceTransY) * sourceScanlineStride + sourceDBOffset;
      
      int destYOffset = (j + dy - destTransY) * destScanlineStride + destDBOffset;
      

      int dbitnum = destDataBitOffset + (dx - destTransX);
      


      int i = 0;
      while ((i < dwidth) && ((dbitnum & 0xF) != 0)) {
        int selement = sourceData[(sourceYOffset + sshortnum[i])];
        int val = selement >> sshift[i] & 0x1;
        
        int dindex = destYOffset + (dbitnum >> 4);
        int dshift = 15 - (dbitnum & 0xF);
        int delement = destData[dindex];
        delement |= val << dshift;
        destData[dindex] = ((short)delement);
        dbitnum++;
        i++;
      }
      
      int dindex = destYOffset + (dbitnum >> 4);
      
      int nshorts = dwidth - i >> 4;
      
      if ((nshorts > 0) && (j > 0) && (y == yvalues[(j - 1)]))
      {
        int offset = destYOffset + (dbitnum >> 4);
        System.arraycopy(destData, offset - destScanlineStride, destData, offset, nshorts);
        

        i += (nshorts >> 4);
        dbitnum += (nshorts >> 4);
      } else {
        while (i < dwidth - 15) {
          int delement = 0;
          for (int b = 15; b >= 0; b--) {
            int selement = sourceData[(sourceYOffset + sshortnum[i])];
            int val = selement >> sshift[i] & 0x1;
            delement |= val << b;
            i++;
          }
          
          destData[(dindex++)] = ((short)delement);
          dbitnum += 16;
        }
      }
      
      if (i < dwidth) {
        dindex = destYOffset + (dbitnum >> 4);
        int delement = destData[dindex];
        while (i < dwidth) {
          int selement = sourceData[(sourceYOffset + sshortnum[i])];
          int val = selement >> sshift[i] & 0x1;
          
          int dshift = 15 - (dbitnum & 0xF);
          delement |= val << dshift;
          dbitnum++;
          i++;
        }
        destData[dindex] = ((short)delement);
      }
    }
  }
  

  private void intLoop(Raster source, WritableRaster dest, Rectangle destRect, int[] xvalues, int[] yvalues)
  {
    int dx = x;
    int dy = y;
    int dwidth = width;
    int dheight = height;
    
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
    
    int[] sintnum = new int[dwidth];
    int[] sshift = new int[dwidth];
    
    for (int i = 0; i < dwidth; i++) {
      int x = xvalues[i];
      int sbitnum = sourceDataBitOffset + (x - sourceTransX);
      sintnum[i] = (sbitnum >> 5);
      sshift[i] = (31 - (sbitnum & 0x1F));
    }
    
    for (int j = 0; j < dheight; j++) {
      int y = yvalues[j];
      
      int sourceYOffset = (y - sourceTransY) * sourceScanlineStride + sourceDBOffset;
      
      int destYOffset = (j + dy - destTransY) * destScanlineStride + destDBOffset;
      

      int dbitnum = destDataBitOffset + (dx - destTransX);
      



      int i = 0;
      while ((i < dwidth) && ((dbitnum & 0x1F) != 0)) {
        int selement = sourceData[(sourceYOffset + sintnum[i])];
        int val = selement >> sshift[i] & 0x1;
        
        int dindex = destYOffset + (dbitnum >> 5);
        int dshift = 31 - (dbitnum & 0x1F);
        int delement = destData[dindex];
        delement |= val << dshift;
        destData[dindex] = delement;
        dbitnum++;
        i++;
      }
      
      int dindex = destYOffset + (dbitnum >> 5);
      int nints = dwidth - i >> 5;
      
      if ((nints > 0) && (j > 0) && (y == yvalues[(j - 1)]))
      {
        int offset = destYOffset + (dbitnum >> 5);
        System.arraycopy(destData, offset - destScanlineStride, destData, offset, nints);
        

        i += (nints >> 5);
        dbitnum += (nints >> 5);
      } else {
        while (i < dwidth - 31) {
          int delement = 0;
          for (int b = 31; b >= 0; b--) {
            int selement = sourceData[(sourceYOffset + sintnum[i])];
            int val = selement >> sshift[i] & 0x1;
            delement |= val << b;
            i++;
          }
          
          destData[(dindex++)] = delement;
          dbitnum += 32;
        }
      }
      
      if (i < dwidth) {
        dindex = destYOffset + (dbitnum >> 5);
        int delement = destData[dindex];
        while (i < dwidth) {
          int selement = sourceData[(sourceYOffset + sintnum[i])];
          int val = selement >> sshift[i] & 0x1;
          
          int dshift = 31 - (dbitnum & 0x1F);
          delement |= val << dshift;
          dbitnum++;
          i++;
        }
        destData[dindex] = delement;
      }
    }
  }
}
