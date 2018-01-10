package com.sun.media.jai.opimage;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.DataBufferByte;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.GeometricOpImage;
import javax.media.jai.ImageLayout;
import javax.media.jai.PackedImageData;
import javax.media.jai.PixelAccessor;
import javax.media.jai.PlanarImage;



































































































class SubsampleBinaryToGray2x2OpImage
  extends GeometricOpImage
{
  private int blockX;
  private int blockY;
  private int dWidth;
  private int dHeight;
  private int[] lut4_45;
  private int[] lut4_67;
  private byte[] lutGray;
  
  public SubsampleBinaryToGray2x2OpImage(RenderedImage source, ImageLayout layout, Map config)
  {
    super(vectorize(source), SubsampleBinaryToGrayOpImage.layoutHelper(source, 0.5F, 0.5F, layout, config), config, true, null, null, null);
    










    blockX = 2;
    blockY = 2;
    int srcWidth = source.getWidth();
    int srcHeight = source.getHeight();
    
    dWidth = (srcWidth / blockX);
    dHeight = (srcHeight / blockY);
    
    if (extender == null) {
      computableBounds = new Rectangle(0, 0, dWidth, dHeight);
    }
    else {
      computableBounds = getBounds();
    }
    


    buildLookupTables();
  }
  













  public Point2D mapDestPoint(Point2D destPt)
  {
    if (destPt == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    Point2D pt = (Point2D)destPt.clone();
    
    pt.setLocation(destPt.getX() * 2.0D, destPt.getY() * 2.0D);
    
    return pt;
  }
  













  public Point2D mapSourcePoint(Point2D sourcePt)
  {
    if (sourcePt == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    Point2D pt = (Point2D)sourcePt.clone();
    
    pt.setLocation(sourcePt.getX() / 2.0D, sourcePt.getY() / 2.0D);
    
    return pt;
  }
  


















  protected Rectangle forwardMapRect(Rectangle sourceRect, int sourceIndex)
  {
    if (sourceRect == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if (sourceIndex != 0) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic1"));
    }
    


    int dx0 = x / blockX;
    int dy0 = y / blockY;
    int dx1 = (x + width - 1) / blockX;
    int dy1 = (y + height - 1) / blockY;
    
    return new Rectangle(dx0, dy0, dx1 - dx0 + 1, dy1 - dy0 + 1);
  }
  

















  protected Rectangle backwardMapRect(Rectangle destRect, int sourceIndex)
  {
    if (destRect == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if (sourceIndex != 0) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic1"));
    }
    

    int sx0 = x * blockX;
    int sy0 = y * blockY;
    int sx1 = (x + width - 1) * blockX;
    int sy1 = (y + height - 1) * blockY;
    
    return new Rectangle(sx0, sy0, sx1 - sx0 + blockX, sy1 - sy0 + blockY);
  }
  











  protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect)
  {
    Raster source = sources[0];
    
    switch (source.getSampleModel().getDataType()) {
    case 0: 
    case 1: 
    case 2: 
    case 3: 
      byteLoop2x2(source, dest, destRect);
      break;
    default: 
      throw new RuntimeException(JaiI18N.getString("SubsampleBinaryToGrayOpImage0"));
    }
  }
  
  private void byteLoop2x2(Raster source, WritableRaster dest, Rectangle destRect)
  {
    PixelAccessor pa = new PixelAccessor(source.getSampleModel(), null);
    PackedImageData pid = pa.getPackedPixels(source, source.getBounds(), false, false);
    
    byte[] sourceData = data;
    int sourceDBOffset = offset;
    int dx = x;int dy = y;
    int dwi = width;int dhi = height;
    int sourceTransX = rect.x;
    int sourceTransY = rect.y;
    int sourceDataBitOffset = bitOffset;
    int sourceScanlineStride = lineStride;
    
    PixelInterleavedSampleModel destSM = (PixelInterleavedSampleModel)dest.getSampleModel();
    
    DataBufferByte destDB = (DataBufferByte)dest.getDataBuffer();
    
    int destTransX = dest.getSampleModelTranslateX();
    int destTransY = dest.getSampleModelTranslateY();
    int destScanlineStride = destSM.getScanlineStride();
    
    byte[] destData = destDB.getData();
    int destDBOffset = destDB.getOffset();
    
    int[] sAreaBitsOn = new int[4];
    int j;
    int j; if ((sourceDataBitOffset & 0x1) == 0)
    {
      for (j = 0; j < dhi;) {
        int y = dy + j << 1;
        int sourceYOffset = (y - sourceTransY) * sourceScanlineStride + sourceDBOffset;
        
        int sourceYOffset2 = sourceYOffset + sourceScanlineStride;
        
        int destYOffset = (j + dy - destTransY) * destScanlineStride + destDBOffset;
        
        destYOffset += dx - destTransX;
        




        int sbitnumi = (dx << 1) - sourceTransX + sourceDataBitOffset;
        for (int i = 0; i < dwi; 
            































            goto 421)
        {
          int sbytenumi = sbitnumi >> 3;
          
          int sstartbiti = sbitnumi % 8;
          int selement = 0xFF & sourceData[(sourceYOffset + sbytenumi)];
          
          sAreaBitsOn[2] = lut4_45[(selement & 0xF)];
          sAreaBitsOn[3] = lut4_67[(selement & 0xF)];
          selement >>= 4;
          sAreaBitsOn[0] = lut4_45[selement];
          sAreaBitsOn[1] = lut4_67[selement];
          

          selement = 0xFF & sourceData[(sourceYOffset2 + sbytenumi)];
          sAreaBitsOn[2] += lut4_45[(selement & 0xF)];
          sAreaBitsOn[3] += lut4_67[(selement & 0xF)];
          selement >>= 4;
          sAreaBitsOn[0] += lut4_45[selement];
          sAreaBitsOn[1] += lut4_67[selement];
          







          sstartbiti >>= 1;
          
          if ((sstartbiti < 4) && (i < dwi)) {
            destData[(destYOffset + i)] = lutGray[sAreaBitsOn[sstartbiti]];
            sstartbiti++;
            i++;
            sbitnumi += blockX;
          }
        }
        j++;
















      }
      
















    }
    else
    {
















      for (j = 0; j < dhi;) {
        int y = dy + j << 1;
        int sourceYOffset = (y - sourceTransY) * sourceScanlineStride + sourceDBOffset;
        
        int sourceYOffset2 = sourceYOffset + sourceScanlineStride;
        
        int destYOffset = (j + dy - destTransY) * destScanlineStride + destDBOffset;
        
        destYOffset += dx - destTransX;
        




        int sbitnumi = (dx << 1) - sourceTransX + sourceDataBitOffset;
        
        for (int i = 0; i < dwi; 
            









































            goto 801)
        {
          int sbytenumi = sbitnumi >> 3;
          
          int sstartbiti = sbitnumi % 8;
          

          int selement = 0xFF & sourceData[(sourceYOffset + sbytenumi)] << 1;
          
          sAreaBitsOn[2] = lut4_45[(selement & 0xF)];
          sAreaBitsOn[3] = lut4_67[(selement & 0xF)];
          selement >>= 4;
          sAreaBitsOn[0] = lut4_45[selement];
          sAreaBitsOn[1] = lut4_67[selement];
          


          selement = 0xFF & sourceData[(sourceYOffset2 + sbytenumi)] << 1;
          sAreaBitsOn[2] += lut4_45[(selement & 0xF)];
          sAreaBitsOn[3] += lut4_67[(selement & 0xF)];
          selement >>= 4;
          sAreaBitsOn[0] += lut4_45[selement];
          sAreaBitsOn[1] += lut4_67[selement];
          




          sbytenumi++;
          if (sbytenumi < sourceData.length - sourceYOffset2) {
            sAreaBitsOn[3] += (sourceData[(sourceYOffset + sbytenumi)] < 0 ? 1 : 0);
            sAreaBitsOn[3] += (sourceData[(sourceYOffset2 + sbytenumi)] < 0 ? 1 : 0);
          }
          




          sstartbiti >>= 1;
          
          if ((sstartbiti < 4) && (i < dwi)) {
            destData[(destYOffset + i)] = lutGray[sAreaBitsOn[sstartbiti]];
            sstartbiti++;
            i++;
            sbitnumi += blockX;
          }
        }
        j++;
      }
    }
  }
  





































































  private final void buildLookupTables()
  {
    lut4_45 = new int[16];
    lut4_67 = new int[16];
    
    lut4_67[0] = 0;lut4_67[1] = 1;lut4_67[2] = 1;lut4_67[3] = 2;
    for (int i = 4; i < 16; i++) { lut4_67[i] = lut4_67[(i & 0x3)];
    }
    for (int i = 0; i < 16; i++) { lut4_45[i] = lut4_67[(i >> 2)];
    }
    
    if (lutGray != null) return;
    lutGray = new byte[blockX * blockY + 1];
    for (int i = 0; i < lutGray.length; i++) {
      int tmp = Math.round(255.0F * i / (lutGray.length - 1.0F));
      lutGray[i] = (tmp > 255 ? -1 : (byte)tmp);
    }
    

    if (SubsampleBinaryToGrayOpImage.isMinWhite(getSourceImage(0).getColorModel()))
    {
      for (int i = 0; i < lutGray.length; i++) {
        lutGray[i] = ((byte)(255 - (0xFF & lutGray[i])));
      }
    }
  }
}
