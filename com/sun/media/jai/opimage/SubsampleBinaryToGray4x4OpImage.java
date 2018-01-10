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






















































class SubsampleBinaryToGray4x4OpImage
  extends GeometricOpImage
{
  private int blockX = 4;
  private int blockY = 4;
  








  private int dWidth;
  








  private int dHeight;
  








  private int[] xValues;
  







  private int[] yValues;
  







  private int[] lut;
  







  private byte[] lutGray;
  








  public SubsampleBinaryToGray4x4OpImage(RenderedImage source, ImageLayout layout, Map config)
  {
    super(vectorize(source), SubsampleBinaryToGrayOpImage.layoutHelper(source, 0.25F, 0.25F, layout, config), config, true, null, null, null);
    










    int srcWidth = source.getWidth();
    int srcHeight = source.getHeight();
    
    blockX = (this.blockY = 4);
    
    dWidth = (srcWidth / blockX);
    dHeight = (srcHeight / blockY);
    
    if (extender == null) {
      computableBounds = new Rectangle(0, 0, dWidth, dHeight);
    }
    else {
      computableBounds = getBounds();
    }
    


    buildLookupTables();
    

    computeXYValues(dWidth, dHeight);
  }
  













  public Point2D mapDestPoint(Point2D destPt)
  {
    if (destPt == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    Point2D pt = (Point2D)destPt.clone();
    
    pt.setLocation(destPt.getX() * 4.0D, destPt.getY() * 4.0D);
    
    return pt;
  }
  













  public Point2D mapSourcePoint(Point2D sourcePt)
  {
    if (sourcePt == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    Point2D pt = (Point2D)sourcePt.clone();
    
    pt.setLocation(sourcePt.getX() / 4.0D, sourcePt.getY() / 4.0D);
    
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
    

    int x0 = x;
    int y0 = y;
    int dx0 = x0 / blockX;
    int dy0 = y0 / blockY;
    
    int x1 = x + width - 1;
    int y1 = y + height - 1;
    
    int dx1 = x1 / blockX;
    int dy1 = y1 / blockY;
    

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
      byteLoop4x4(source, dest, destRect);
      break;
    default: 
      throw new RuntimeException(JaiI18N.getString("SubsampleBinaryToGrayOpImage0"));
    }
    
  }
  


  private void byteLoop4x4(Raster source, WritableRaster dest, Rectangle destRect)
  {
    PixelAccessor pa = new PixelAccessor(source.getSampleModel(), null);
    PackedImageData pid = pa.getPackedPixels(source, source.getBounds(), false, false);
    

    if (bitOffset % 4 != 0)
    {
      byteLoop(source, dest, destRect);
      return;
    }
    
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
    
    int[] sAreaBitsOn = new int[2];
    
    for (int j = 0; j < dhi; j++) {
      int y = dy + j << 2;
      int sourceYOffset = (y - sourceTransY) * sourceScanlineStride + sourceDBOffset;
      

      int destYOffset = (j + dy - destTransY) * destScanlineStride + destDBOffset;
      
      destYOffset += dx - destTransX;
      




      int sbitnumi = (dx << 2) - sourceTransX + sourceDataBitOffset;
      
      for (int i = 0; i < dwi; 
          























          goto 356)
      {
        int sbytenumi = sbitnumi >> 3;
        int sstartbiti = sbitnumi % 8;
        int byteindex = sourceYOffset + sbytenumi; int 
          tmp281_280 = 0;sAreaBitsOn[1] = tmp281_280;sAreaBitsOn[0] = tmp281_280;
        for (int k = 0; k < 4; byteindex += sourceScanlineStride) {
          int selement = 0xFF & sourceData[byteindex];
          sAreaBitsOn[1] += lut[(selement & 0xF)];
          sAreaBitsOn[0] += lut[(selement >> 4)];k++;
        }
        










        sstartbiti >>= 2;
        
        if ((sstartbiti < 2) && (i < dwi)) {
          destData[(destYOffset + i)] = lutGray[sAreaBitsOn[sstartbiti]];
          sstartbiti++;
          i++;
          sbitnumi += blockX;
        }
      }
    }
  }
  



  private void byteLoop(Raster source, WritableRaster dest, Rectangle destRect)
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
    
    int[] sbytenum = new int[dwi];
    int[] sstartbit = new int[dwi];
    int[] sAreaBitsOn = new int[dwi];
    for (int i = 0; i < dwi; i++) {
      int x = xValues[(dx + i)];
      int sbitnum = sourceDataBitOffset + (x - sourceTransX);
      sbytenum[i] = (sbitnum >> 3);
      sstartbit[i] = (sbitnum % 8);
    }
    
    for (int j = 0; j < dhi; j++)
    {
      for (int i = 0; i < dwi; i++) {
        sAreaBitsOn[i] = 0;
      }
      
      for (int y = yValues[(dy + j)]; y < yValues[(dy + j)] + blockY; y++)
      {
        int sourceYOffset = (y - sourceTransY) * sourceScanlineStride + sourceDBOffset;
        


        for (int i = 0; i < dwi; i++) {
          int delement = 0;
          int sendbiti = sstartbit[i] + blockX - 1;
          int sendbytenumi = sbytenum[i] + (sendbiti >> 3);
          sendbiti %= 8;
          int selement = 0xFF & sourceData[(sourceYOffset + sbytenum[i])];
          
          int swingBits = 24 + sstartbit[i];
          if (sbytenum[i] == sendbytenumi)
          {
            selement <<= swingBits;
            selement >>>= 31 - sendbiti + sstartbit[i];
            delement += lut[selement];
          } else {
            selement <<= swingBits;
            selement >>>= swingBits;
            

            delement += lut[selement];
            for (int b = sbytenum[i] + 1; b < sendbytenumi; b++) {
              selement = 0xFF & sourceData[(sourceYOffset + b)];
              delement += lut[selement];
            }
            selement = 0xFF & sourceData[(sourceYOffset + sendbytenumi)];
            selement >>>= 7 - sendbiti;
            delement += lut[selement];
          }
          sAreaBitsOn[i] += delement;
        }
      }
      int destYOffset = (j + dy - destTransY) * destScanlineStride + destDBOffset;
      

      destYOffset += dx - destTransX;
      


      for (int i = 0; i < dwi; i++) {
        destData[(destYOffset + i)] = lutGray[sAreaBitsOn[i]];
      }
    }
  }
  





  private final void buildLookupTables()
  {
    lut = new int[16];
    lut[0] = 0;lut[1] = 1;lut[2] = 1;lut[3] = 2;
    lut[4] = 1;lut[5] = 2;lut[6] = 2;lut[7] = 3;
    for (int i = 8; i < 16; i++) { lut[i] = (1 + lut[(i - 8)]);
    }
    

    if (lutGray != null) return;
    lutGray = new byte[blockX * blockY + 1];
    for (int i = 0; i < lutGray.length; i++) {
      int tmp = Math.round(255.0F * i / (lutGray.length - 1.0F));
      lutGray[i] = (tmp > 255 ? -1 : (byte)tmp);
    }
    

    if (SubsampleBinaryToGrayOpImage.isMinWhite(getSourceImage(0).getColorModel()))
    {
      for (int i = 0; i < lutGray.length; i++)
        lutGray[i] = ((byte)(255 - (0xFF & lutGray[i])));
    }
  }
  
  private void computeXYValues(int dstWidth, int dstHeight) {
    if ((xValues == null) || (yValues == null)) {
      xValues = new int[dstWidth];
      yValues = new int[dstHeight];
    }
    
    for (int i = 0; i < dstWidth; i++)
    {
      xValues[i] = (i << 2);
    }
    
    for (int i = 0; i < dstHeight; i++)
    {
      yValues[i] = (i << 2);
    }
  }
}
