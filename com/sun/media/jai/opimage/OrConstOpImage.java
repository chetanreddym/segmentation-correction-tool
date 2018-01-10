package com.sun.media.jai.opimage;

import com.sun.media.jai.util.ImageUtil;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ColormapOpImage;
import javax.media.jai.ImageLayout;
import javax.media.jai.PlanarImage;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFormatTag;














































final class OrConstOpImage
  extends ColormapOpImage
{
  protected int[] constants;
  
  public OrConstOpImage(RenderedImage source, Map config, ImageLayout layout, int[] constants)
  {
    super(source, layout, config, true);
    
    int numBands = getSampleModel().getNumBands();
    
    if (constants.length < numBands) {
      this.constants = new int[numBands];
      for (int i = 0; i < numBands; i++) {
        this.constants[i] = constants[0];
      }
    } else {
      this.constants = ((int[])constants.clone());
    }
    

    permitInPlaceOperation();
    

    initializeColormapOperation();
  }
  


  protected void transformColormap(byte[][] colormap)
  {
    for (int b = 0; b < 3; b++) {
      byte[] map = colormap[b];
      int mapSize = map.length;
      
      int c = b < constants.length ? constants[b] : constants[0];
      
      for (int i = 0; i < mapSize; i++) {
        map[i] = ImageUtil.clampRoundByte(map[i] & 0xFF | c);
      }
    }
  }
  











  protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect)
  {
    RasterFormatTag[] formatTags = getFormatTags();
    
    Rectangle srcRect = mapDestRect(destRect, 0);
    
    RasterAccessor dst = new RasterAccessor(dest, destRect, formatTags[1], getColorModel());
    
    RasterAccessor src = new RasterAccessor(sources[0], srcRect, formatTags[0], getSource(0).getColorModel());
    


    switch (dst.getDataType()) {
    case 0: 
      computeRectByte(src, dst);
      break;
    case 1: 
    case 2: 
      computeRectShort(src, dst);
      break;
    case 3: 
      computeRectInt(src, dst);
    }
    
    

    dst.copyDataToRaster();
  }
  
  private void computeRectByte(RasterAccessor src, RasterAccessor dst)
  {
    int dstWidth = dst.getWidth();
    int dstHeight = dst.getHeight();
    int dstBands = dst.getNumBands();
    
    int dstLineStride = dst.getScanlineStride();
    int dstPixelStride = dst.getPixelStride();
    int[] dstBandOffsets = dst.getBandOffsets();
    byte[][] dstData = dst.getByteDataArrays();
    
    int srcLineStride = src.getScanlineStride();
    int srcPixelStride = src.getPixelStride();
    int[] srcBandOffsets = src.getBandOffsets();
    byte[][] srcData = src.getByteDataArrays();
    
    for (int b = 0; b < dstBands; b++) {
      int c = constants[b];
      byte[] d = dstData[b];
      byte[] s = srcData[b];
      
      int dstLineOffset = dstBandOffsets[b];
      int srcLineOffset = srcBandOffsets[b];
      
      for (int h = 0; h < dstHeight; h++) {
        int dstPixelOffset = dstLineOffset;
        int srcPixelOffset = srcLineOffset;
        
        dstLineOffset += dstLineStride;
        srcLineOffset += srcLineStride;
        
        for (int w = 0; w < dstWidth; w++) {
          d[dstPixelOffset] = ((byte)(s[srcPixelOffset] | c));
          
          dstPixelOffset += dstPixelStride;
          srcPixelOffset += srcPixelStride;
        }
      }
    }
  }
  
  private void computeRectShort(RasterAccessor src, RasterAccessor dst)
  {
    int dstWidth = dst.getWidth();
    int dstHeight = dst.getHeight();
    int dstBands = dst.getNumBands();
    
    int dstLineStride = dst.getScanlineStride();
    int dstPixelStride = dst.getPixelStride();
    int[] dstBandOffsets = dst.getBandOffsets();
    short[][] dstData = dst.getShortDataArrays();
    
    int srcLineStride = src.getScanlineStride();
    int srcPixelStride = src.getPixelStride();
    int[] srcBandOffsets = src.getBandOffsets();
    short[][] srcData = src.getShortDataArrays();
    
    for (int b = 0; b < dstBands; b++) {
      int c = constants[b];
      short[] d = dstData[b];
      short[] s = srcData[b];
      
      int dstLineOffset = dstBandOffsets[b];
      int srcLineOffset = srcBandOffsets[b];
      
      for (int h = 0; h < dstHeight; h++) {
        int dstPixelOffset = dstLineOffset;
        int srcPixelOffset = srcLineOffset;
        
        dstLineOffset += dstLineStride;
        srcLineOffset += srcLineStride;
        
        for (int w = 0; w < dstWidth; w++) {
          d[dstPixelOffset] = ((short)(s[srcPixelOffset] | c));
          
          dstPixelOffset += dstPixelStride;
          srcPixelOffset += srcPixelStride;
        }
      }
    }
  }
  
  private void computeRectInt(RasterAccessor src, RasterAccessor dst)
  {
    int dstWidth = dst.getWidth();
    int dstHeight = dst.getHeight();
    int dstBands = dst.getNumBands();
    
    int dstLineStride = dst.getScanlineStride();
    int dstPixelStride = dst.getPixelStride();
    int[] dstBandOffsets = dst.getBandOffsets();
    int[][] dstData = dst.getIntDataArrays();
    
    int srcLineStride = src.getScanlineStride();
    int srcPixelStride = src.getPixelStride();
    int[] srcBandOffsets = src.getBandOffsets();
    int[][] srcData = src.getIntDataArrays();
    
    for (int b = 0; b < dstBands; b++) {
      int c = constants[b];
      int[] d = dstData[b];
      int[] s = srcData[b];
      
      int dstLineOffset = dstBandOffsets[b];
      int srcLineOffset = srcBandOffsets[b];
      
      for (int h = 0; h < dstHeight; h++) {
        int dstPixelOffset = dstLineOffset;
        int srcPixelOffset = srcLineOffset;
        
        dstLineOffset += dstLineStride;
        srcLineOffset += srcLineStride;
        
        for (int w = 0; w < dstWidth; w++) {
          s[srcPixelOffset] |= c;
          
          dstPixelOffset += dstPixelStride;
          srcPixelOffset += srcPixelStride;
        }
      }
    }
  }
}
