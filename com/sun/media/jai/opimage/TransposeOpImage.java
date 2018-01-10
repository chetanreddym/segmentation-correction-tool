package com.sun.media.jai.opimage;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.GeometricOpImage;
import javax.media.jai.ImageLayout;
import javax.media.jai.IntegerSequence;
import javax.media.jai.PlanarImage;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFormatTag;


























public class TransposeOpImage
  extends GeometricOpImage
{
  protected int type;
  protected int src_width;
  protected int src_height;
  protected Rectangle sourceBounds;
  
  private static ImageLayout layoutHelper(ImageLayout layout, RenderedImage source, int type)
  {
    ImageLayout newLayout;
    ImageLayout newLayout;
    if (layout != null) {
      newLayout = (ImageLayout)layout.clone();
    } else {
      newLayout = new ImageLayout();
    }
    


    Rectangle sourceBounds = new Rectangle(source.getMinX(), source.getMinY(), source.getWidth(), source.getHeight());
    


    Rectangle rect = mapRect(sourceBounds, sourceBounds, type, true);
    
    newLayout.setMinX(x);
    newLayout.setMinY(y);
    newLayout.setWidth(width);
    newLayout.setHeight(height);
    

    Rectangle tileRect = new Rectangle(source.getTileGridXOffset(), source.getTileGridYOffset(), source.getTileWidth(), source.getTileHeight());
    


    rect = mapRect(tileRect, sourceBounds, type, true);
    

    if (newLayout.isValid(16)) {
      newLayout.setTileGridXOffset(x);
    }
    if (newLayout.isValid(32)) {
      newLayout.setTileGridYOffset(y);
    }
    if (newLayout.isValid(64)) {
      newLayout.setTileWidth(Math.abs(width));
    }
    if (newLayout.isValid(128)) {
      newLayout.setTileHeight(Math.abs(height));
    }
    
    return newLayout;
  }
  















  public TransposeOpImage(RenderedImage source, Map config, ImageLayout layout, int type)
  {
    super(vectorize(source), layoutHelper(layout, source, type), config, true, null, null, null);
    












    ColorModel srcColorModel = source.getColorModel();
    if ((srcColorModel instanceof IndexColorModel)) {
      sampleModel = source.getSampleModel().createCompatibleSampleModel(tileWidth, tileHeight);
      
      colorModel = srcColorModel;
    }
    

    this.type = type;
    

    src_width = source.getWidth();
    src_height = source.getHeight();
    
    sourceBounds = new Rectangle(source.getMinX(), source.getMinY(), source.getWidth(), source.getHeight());
  }
  






  protected Rectangle forwardMapRect(Rectangle sourceRect, int sourceIndex)
  {
    return mapRect(sourceRect, sourceBounds, type, true);
  }
  



  protected Rectangle backwardMapRect(Rectangle destRect, int sourceIndex)
  {
    return mapRect(destRect, sourceBounds, type, false);
  }
  
















  protected static void mapPoint(int[] pt, int minX, int minY, int maxX, int maxY, int type, boolean mapForwards)
  {
    int sx = pt[0];
    int sy = pt[1];
    int dx = -1;
    int dy = -1;
    
    switch (type) {
    case 0: 
      dx = sx;
      dy = minY + maxY - sy;
      break;
    
    case 1: 
      dx = minX + maxX - sx;
      dy = sy;
      break;
    
    case 2: 
      dx = minX - minY + sy;
      dy = minY - minX + sx;
      break;
    
    case 3: 
      if (mapForwards) {
        dx = minX + maxY - sy;
        dy = minY + maxX - sx;
      } else {
        dx = minY + maxX - sy;
        dy = minX + maxY - sx;
      }
      break;
    
    case 4: 
      if (mapForwards) {
        dx = minX + maxY - sy;
        dy = minY - minX + sx;
      } else {
        dx = minX - minY + sy;
        dy = minX + maxY - sx;
      }
      break;
    
    case 5: 
      dx = minX + maxX - sx;
      dy = minY + maxY - sy;
      break;
    
    case 6: 
      if (mapForwards) {
        dx = minX - minY + sy;
        dy = maxX + minY - sx;
      } else {
        dx = maxX + minY - sy;
        dy = minY - minX + sx;
      }
      break;
    }
    
    pt[0] = dx;
    pt[1] = dy;
  }
  


  private static Rectangle mapRect(Rectangle rect, Rectangle sourceBounds, int type, boolean mapForwards)
  {
    int sMinX = x;
    int sMinY = y;
    int sMaxX = sMinX + width - 1;
    int sMaxY = sMinY + height - 1;
    

    int[] pt = new int[2];
    pt[0] = x;
    pt[1] = y;
    mapPoint(pt, sMinX, sMinY, sMaxX, sMaxY, type, mapForwards);
    int dMinX = dMaxX = pt[0];
    int dMinY = dMaxY = pt[1];
    
    pt[0] = (x + width - 1);
    pt[1] = y;
    mapPoint(pt, sMinX, sMinY, sMaxX, sMaxY, type, mapForwards);
    dMinX = Math.min(dMinX, pt[0]);
    dMinY = Math.min(dMinY, pt[1]);
    int dMaxX = Math.max(dMaxX, pt[0]);
    int dMaxY = Math.max(dMaxY, pt[1]);
    
    pt[0] = x;
    pt[1] = (y + height - 1);
    mapPoint(pt, sMinX, sMinY, sMaxX, sMaxY, type, mapForwards);
    dMinX = Math.min(dMinX, pt[0]);
    dMinY = Math.min(dMinY, pt[1]);
    dMaxX = Math.max(dMaxX, pt[0]);
    dMaxY = Math.max(dMaxY, pt[1]);
    
    pt[0] = (x + width - 1);
    pt[1] = (y + height - 1);
    mapPoint(pt, sMinX, sMinY, sMaxX, sMaxY, type, mapForwards);
    dMinX = Math.min(dMinX, pt[0]);
    dMinY = Math.min(dMinY, pt[1]);
    dMaxX = Math.max(dMaxX, pt[0]);
    dMaxY = Math.max(dMaxY, pt[1]);
    
    return new Rectangle(dMinX, dMinY, dMaxX - dMinX + 1, dMaxY - dMinY + 1);
  }
  


  public Raster computeTile(int tileX, int tileY)
  {
    Point org = new Point(tileXToX(tileX), tileYToY(tileY));
    WritableRaster dest = createWritableRaster(sampleModel, org);
    

    int destMinX = dest.getMinX();
    int destMinY = dest.getMinY();
    int destMaxX = destMinX + dest.getWidth();
    int destMaxY = destMinY + dest.getHeight();
    

    Rectangle bounds = getBounds();
    if (destMinX < x) {
      destMinX = x;
    }
    int boundsMaxX = x + width;
    if (destMaxX > boundsMaxX) {
      destMaxX = boundsMaxX;
    }
    if (destMinY < y) {
      destMinY = y;
    }
    int boundsMaxY = y + height;
    if (destMaxY > boundsMaxY) {
      destMaxY = boundsMaxY;
    }
    
    if ((destMinX >= destMaxX) || (destMinY >= destMaxY)) {
      return dest;
    }
    

    Rectangle destRect = new Rectangle(destMinX, destMinY, destMaxX - destMinX, destMaxY - destMinY);
    



    IntegerSequence xSplits = new IntegerSequence(destMinX, destMaxX);
    
    xSplits.insert(destMinX);
    xSplits.insert(destMaxX);
    
    IntegerSequence ySplits = new IntegerSequence(destMinY, destMaxY);
    
    ySplits.insert(destMinY);
    ySplits.insert(destMaxY);
    

    PlanarImage src = getSource(0);
    int sMinX = src.getMinX();
    int sMinY = src.getMinY();
    int sWidth = src.getWidth();
    int sHeight = src.getHeight();
    int sMaxX = sMinX + sWidth - 1;
    int sMaxY = sMinY + sHeight - 1;
    int sTileWidth = src.getTileWidth();
    int sTileHeight = src.getTileHeight();
    int sTileGridXOffset = src.getTileGridXOffset();
    int sTileGridYOffset = src.getTileGridYOffset();
    
    int xStart = 0;
    int xGap = 0;
    int yStart = 0;
    int yGap = 0;
    

















    int[] pt = new int[2];
    pt[0] = sTileGridXOffset;
    pt[1] = sTileGridYOffset;
    mapPoint(pt, sMinX, sMinY, sMaxX, sMaxY, type, true);
    xStart = pt[0];
    yStart = pt[1];
    

    switch (type) {
    case 0: 
      yStart++;
      xGap = sTileWidth;
      yGap = sTileHeight;
      break;
    
    case 1: 
      xStart++;
      xGap = sTileWidth;
      yGap = sTileHeight;
      break;
    
    case 2: 
      xGap = sTileHeight;
      yGap = sTileWidth;
      break;
    
    case 3: 
      xStart++;
      yStart++;
      xGap = sTileHeight;
      yGap = sTileWidth;
      break;
    
    case 4: 
      xStart++;
      xGap = sTileHeight;
      yGap = sTileWidth;
      break;
    
    case 5: 
      xStart++;
      yStart++;
      xGap = sTileWidth;
      yGap = sTileHeight;
      break;
    
    case 6: 
      yStart++;
      xGap = sTileHeight;
      yGap = sTileWidth;
    }
    
    


    int kx = (int)Math.floor((destMinX - xStart) / xGap);
    int xSplit = xStart + kx * xGap;
    while (xSplit < destMaxX) {
      xSplits.insert(xSplit);
      xSplit += xGap;
    }
    
    int ky = (int)Math.floor((destMinY - yStart) / yGap);
    int ySplit = yStart + ky * yGap;
    while (ySplit < destMaxY) {
      ySplits.insert(ySplit);
      ySplit += yGap;
    }
    

    Raster[] sources = new Raster[1];
    





    Rectangle subRect = new Rectangle();
    
    ySplits.startEnumeration();
    int y2; for (int y1 = ySplits.nextElement(); ySplits.hasMoreElements(); y1 = y2) {
      y2 = ySplits.nextElement();
      int h = y2 - y1;
      
      xSplits.startEnumeration();
      int x2; for (int x1 = xSplits.nextElement(); 
          xSplits.hasMoreElements(); x1 = x2) {
        x2 = xSplits.nextElement();
        int w = x2 - x1;
        



        pt[0] = x1;
        pt[1] = y1;
        mapPoint(pt, sMinX, sMinY, sMaxX, sMaxY, type, false);
        

        int tx = src.XToTileX(pt[0]);
        int ty = src.YToTileY(pt[1]);
        sources[0] = src.getTile(tx, ty);
        
        x = x1;
        y = y1;
        width = w;
        height = h;
        computeRect(sources, dest, subRect);
      }
    }
    
    return dest;
  }
  


  protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect)
  {
    RasterFormatTag[] formatTags = getFormatTags();
    Raster src = sources[0];
    



    PlanarImage source = getSource(0);
    int sMinX = source.getMinX();
    int sMinY = source.getMinY();
    int sWidth = source.getWidth();
    int sHeight = source.getHeight();
    int sMaxX = sMinX + sWidth - 1;
    int sMaxY = sMinY + sHeight - 1;
    
    int translateX = src.getSampleModelTranslateX();
    int translateY = src.getSampleModelTranslateY();
    


    Rectangle srcRect = src.getBounds();
    
    RasterAccessor srcAccessor = new RasterAccessor(src, srcRect, formatTags[0], getSource(0).getColorModel());
    


    RasterAccessor dstAccessor = new RasterAccessor(dest, destRect, formatTags[1], getColorModel());
    

    int incr1 = 0;int incr2 = 0;int s_x = 0;int s_y = 0;
    int srcPixelStride = srcAccessor.getPixelStride();
    int srcScanlineStride = srcAccessor.getScanlineStride();
    

    int[] pt = new int[2];
    pt[0] = x;
    pt[1] = y;
    mapPoint(pt, sMinX, sMinY, sMaxX, sMaxY, type, false);
    s_x = pt[0];
    s_y = pt[1];
    

    switch (type) {
    case 0: 
      incr1 = srcPixelStride;
      incr2 = -srcScanlineStride;
      break;
    
    case 1: 
      incr1 = -srcPixelStride;
      incr2 = srcScanlineStride;
      break;
    
    case 2: 
      incr1 = srcScanlineStride;
      incr2 = srcPixelStride;
      break;
    
    case 3: 
      incr1 = -srcScanlineStride;
      incr2 = -srcPixelStride;
      break;
    
    case 4: 
      incr1 = -srcScanlineStride;
      incr2 = srcPixelStride;
      break;
    
    case 5: 
      incr1 = -srcPixelStride;
      incr2 = -srcScanlineStride;
      break;
    
    case 6: 
      incr1 = srcScanlineStride;
      incr2 = -srcPixelStride;
    }
    
    
    switch (dstAccessor.getDataType()) {
    case 0: 
      byteLoop(srcAccessor, destRect, translateX, translateY, dstAccessor, incr1, incr2, s_x, s_y);
      




      break;
    
    case 3: 
      intLoop(srcAccessor, destRect, translateX, translateY, dstAccessor, incr1, incr2, s_x, s_y);
      




      break;
    
    case 1: 
    case 2: 
      shortLoop(srcAccessor, destRect, translateX, translateY, dstAccessor, incr1, incr2, s_x, s_y);
      




      break;
    
    case 4: 
      floatLoop(srcAccessor, destRect, translateX, translateY, dstAccessor, incr1, incr2, s_x, s_y);
      




      break;
    
    case 5: 
      doubleLoop(srcAccessor, destRect, translateX, translateY, dstAccessor, incr1, incr2, s_x, s_y);
    }
    
    










    if (dstAccessor.isDataCopy()) {
      dstAccessor.clampDataArrays();
      dstAccessor.copyDataToRaster();
    }
  }
  




  private void byteLoop(RasterAccessor src, Rectangle destRect, int srcTranslateX, int srcTranslateY, RasterAccessor dst, int incr1, int incr2, int s_x, int s_y)
  {
    int dwidth = dst.getWidth();
    int dheight = dst.getHeight();
    int dnumBands = dst.getNumBands();
    
    byte[][] dstDataArrays = dst.getByteDataArrays();
    int[] dstBandOffsets = dst.getBandOffsets();
    int dstPixelStride = dst.getPixelStride();
    int dstScanlineStride = dst.getScanlineStride();
    
    byte[][] srcDataArrays = src.getByteDataArrays();
    int[] bandOffsets = src.getOffsetsForBands();
    int srcPixelStride = src.getPixelStride();
    int srcScanlineStride = src.getScanlineStride();
    
    int dst_num_bands = dst.getNumBands();
    
    int dst_min_x = x;
    int dst_min_y = y;
    int dst_max_x = x + width;
    int dst_max_y = y + height;
    

    int posy = (s_y - srcTranslateY) * srcScanlineStride;
    int posx = (s_x - srcTranslateX) * srcPixelStride;
    int srcScanlineOffset = posx + posy;
    int dstScanlineOffset = 0;
    

    for (int y = dst_min_y; y < dst_max_y; y++) {
      for (int k2 = 0; k2 < dst_num_bands; k2++) {
        byte[] srcDataArray = srcDataArrays[k2];
        byte[] dstDataArray = dstDataArrays[k2];
        
        int dstPixelOffset = dstScanlineOffset + dstBandOffsets[k2];
        int srcPixelOffset = srcScanlineOffset + bandOffsets[k2];
        
        for (int x = dst_min_x; x < dst_max_x; x++) {
          dstDataArray[dstPixelOffset] = srcDataArray[srcPixelOffset];
          
          srcPixelOffset += incr1;
          

          dstPixelOffset += dstPixelStride;
        }
      }
      
      srcScanlineOffset += incr2;
      

      dstScanlineOffset += dstScanlineStride;
    }
  }
  




  private void intLoop(RasterAccessor src, Rectangle destRect, int srcTranslateX, int srcTranslateY, RasterAccessor dst, int incr1, int incr2, int s_x, int s_y)
  {
    int dwidth = dst.getWidth();
    int dheight = dst.getHeight();
    int dnumBands = dst.getNumBands();
    
    int[][] dstDataArrays = dst.getIntDataArrays();
    int[] dstBandOffsets = dst.getBandOffsets();
    int dstPixelStride = dst.getPixelStride();
    int dstScanlineStride = dst.getScanlineStride();
    
    int[][] srcDataArrays = src.getIntDataArrays();
    int[] bandOffsets = src.getOffsetsForBands();
    int srcPixelStride = src.getPixelStride();
    int srcScanlineStride = src.getScanlineStride();
    
    int dst_num_bands = dst.getNumBands();
    
    int dst_min_x = x;
    int dst_min_y = y;
    int dst_max_x = x + width;
    int dst_max_y = y + height;
    

    int posy = (s_y - srcTranslateY) * srcScanlineStride;
    int posx = (s_x - srcTranslateX) * srcPixelStride;
    int srcScanlineOffset = posx + posy;
    int dstScanlineOffset = 0;
    

    for (int y = dst_min_y; y < dst_max_y; y++) {
      for (int k2 = 0; k2 < dst_num_bands; k2++) {
        int[] srcDataArray = srcDataArrays[k2];
        int[] dstDataArray = dstDataArrays[k2];
        
        int dstPixelOffset = dstScanlineOffset + dstBandOffsets[k2];
        int srcPixelOffset = srcScanlineOffset + bandOffsets[k2];
        
        for (int x = dst_min_x; x < dst_max_x; x++) {
          dstDataArray[dstPixelOffset] = srcDataArray[srcPixelOffset];
          
          srcPixelOffset += incr1;
          

          dstPixelOffset += dstPixelStride;
        }
      }
      
      srcScanlineOffset += incr2;
      

      dstScanlineOffset += dstScanlineStride;
    }
  }
  




  private void shortLoop(RasterAccessor src, Rectangle destRect, int srcTranslateX, int srcTranslateY, RasterAccessor dst, int incr1, int incr2, int s_x, int s_y)
  {
    int dwidth = dst.getWidth();
    int dheight = dst.getHeight();
    int dnumBands = dst.getNumBands();
    
    short[][] dstDataArrays = dst.getShortDataArrays();
    int[] dstBandOffsets = dst.getBandOffsets();
    int dstPixelStride = dst.getPixelStride();
    int dstScanlineStride = dst.getScanlineStride();
    
    short[][] srcDataArrays = src.getShortDataArrays();
    int[] bandOffsets = src.getOffsetsForBands();
    int srcPixelStride = src.getPixelStride();
    int srcScanlineStride = src.getScanlineStride();
    
    int dst_num_bands = dst.getNumBands();
    
    int dst_min_x = x;
    int dst_min_y = y;
    int dst_max_x = x + width;
    int dst_max_y = y + height;
    

    int posy = (s_y - srcTranslateY) * srcScanlineStride;
    int posx = (s_x - srcTranslateX) * srcPixelStride;
    int srcScanlineOffset = posx + posy;
    int dstScanlineOffset = 0;
    

    for (int y = dst_min_y; y < dst_max_y; y++) {
      for (int k2 = 0; k2 < dst_num_bands; k2++) {
        short[] srcDataArray = srcDataArrays[k2];
        short[] dstDataArray = dstDataArrays[k2];
        
        int dstPixelOffset = dstScanlineOffset + dstBandOffsets[k2];
        int srcPixelOffset = srcScanlineOffset + bandOffsets[k2];
        
        for (int x = dst_min_x; x < dst_max_x; x++) {
          dstDataArray[dstPixelOffset] = srcDataArray[srcPixelOffset];
          
          srcPixelOffset += incr1;
          

          dstPixelOffset += dstPixelStride;
        }
      }
      
      srcScanlineOffset += incr2;
      

      dstScanlineOffset += dstScanlineStride;
    }
  }
  




  private void floatLoop(RasterAccessor src, Rectangle destRect, int srcTranslateX, int srcTranslateY, RasterAccessor dst, int incr1, int incr2, int s_x, int s_y)
  {
    int dwidth = dst.getWidth();
    int dheight = dst.getHeight();
    int dnumBands = dst.getNumBands();
    
    float[][] dstDataArrays = dst.getFloatDataArrays();
    int[] dstBandOffsets = dst.getBandOffsets();
    int dstPixelStride = dst.getPixelStride();
    int dstScanlineStride = dst.getScanlineStride();
    
    float[][] srcDataArrays = src.getFloatDataArrays();
    int[] bandOffsets = src.getOffsetsForBands();
    int srcPixelStride = src.getPixelStride();
    int srcScanlineStride = src.getScanlineStride();
    
    int dst_num_bands = dst.getNumBands();
    
    int dst_min_x = x;
    int dst_min_y = y;
    int dst_max_x = x + width;
    int dst_max_y = y + height;
    

    int posy = (s_y - srcTranslateY) * srcScanlineStride;
    int posx = (s_x - srcTranslateX) * srcPixelStride;
    int srcScanlineOffset = posx + posy;
    int dstScanlineOffset = 0;
    

    for (int y = dst_min_y; y < dst_max_y; y++) {
      for (int k2 = 0; k2 < dst_num_bands; k2++) {
        float[] srcDataArray = srcDataArrays[k2];
        float[] dstDataArray = dstDataArrays[k2];
        
        int dstPixelOffset = dstScanlineOffset + dstBandOffsets[k2];
        int srcPixelOffset = srcScanlineOffset + bandOffsets[k2];
        
        for (int x = dst_min_x; x < dst_max_x; x++) {
          dstDataArray[dstPixelOffset] = srcDataArray[srcPixelOffset];
          
          srcPixelOffset += incr1;
          

          dstPixelOffset += dstPixelStride;
        }
      }
      
      srcScanlineOffset += incr2;
      

      dstScanlineOffset += dstScanlineStride;
    }
  }
  




  private void doubleLoop(RasterAccessor src, Rectangle destRect, int srcTranslateX, int srcTranslateY, RasterAccessor dst, int incr1, int incr2, int s_x, int s_y)
  {
    int dwidth = dst.getWidth();
    int dheight = dst.getHeight();
    int dnumBands = dst.getNumBands();
    
    double[][] dstDataArrays = dst.getDoubleDataArrays();
    int[] dstBandOffsets = dst.getBandOffsets();
    int dstPixelStride = dst.getPixelStride();
    int dstScanlineStride = dst.getScanlineStride();
    
    double[][] srcDataArrays = src.getDoubleDataArrays();
    int[] bandOffsets = src.getOffsetsForBands();
    int srcPixelStride = src.getPixelStride();
    int srcScanlineStride = src.getScanlineStride();
    
    int dst_num_bands = dst.getNumBands();
    
    int dst_min_x = x;
    int dst_min_y = y;
    int dst_max_x = x + width;
    int dst_max_y = y + height;
    

    int posy = (s_y - srcTranslateY) * srcScanlineStride;
    int posx = (s_x - srcTranslateX) * srcPixelStride;
    int srcScanlineOffset = posx + posy;
    int dstScanlineOffset = 0;
    

    for (int y = dst_min_y; y < dst_max_y; y++) {
      for (int k2 = 0; k2 < dst_num_bands; k2++) {
        double[] srcDataArray = srcDataArrays[k2];
        double[] dstDataArray = dstDataArrays[k2];
        
        int dstPixelOffset = dstScanlineOffset + dstBandOffsets[k2];
        int srcPixelOffset = srcScanlineOffset + bandOffsets[k2];
        
        for (int x = dst_min_x; x < dst_max_x; x++) {
          dstDataArray[dstPixelOffset] = srcDataArray[srcPixelOffset];
          
          srcPixelOffset += incr1;
          

          dstPixelOffset += dstPixelStride;
        }
      }
      
      srcScanlineOffset += incr2;
      

      dstScanlineOffset += dstScanlineStride;
    }
  }
}
