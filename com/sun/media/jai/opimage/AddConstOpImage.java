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





































final class AddConstOpImage
  extends ColormapOpImage
{
  protected double[] constants;
  private byte[][] byteTable = (byte[][])null;
  
  private synchronized void initByteTable()
  {
    if (byteTable != null) {
      return;
    }
    
    int nbands = constants.length;
    
    byteTable = new byte[nbands]['Ā'];
    

    for (int band = 0; band < nbands; band++) {
      int k = ImageUtil.clampRoundInt(constants[band]);
      byte[] t = byteTable[band];
      for (int i = 0; i < 256; i++) {
        int sum = i + k;
        if (sum < 0) {
          t[i] = 0;
        } else if (sum > 255) {
          t[i] = -1;
        } else {
          t[i] = ((byte)sum);
        }
      }
    }
  }
  









  public AddConstOpImage(RenderedImage source, Map config, ImageLayout layout, double[] constants)
  {
    super(source, layout, config, true);
    
    int numBands = getSampleModel().getNumBands();
    
    if (constants.length < numBands) {
      this.constants = new double[numBands];
      for (int i = 0; i < numBands; i++) {
        this.constants[i] = constants[0];
      }
    } else {
      this.constants = ((double[])constants.clone());
    }
    

    permitInPlaceOperation();
    

    initializeColormapOperation();
  }
  


  protected void transformColormap(byte[][] colormap)
  {
    initByteTable();
    
    for (int b = 0; b < 3; b++) {
      byte[] map = colormap[b];
      byte[] luTable = byteTable[b];
      int mapSize = map.length;
      
      for (int i = 0; i < mapSize; i++) {
        map[i] = luTable[(map[i] & 0xFF)];
      }
    }
  }
  











  protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect)
  {
    RasterFormatTag[] formatTags = getFormatTags();
    
    Rectangle srcRect = mapDestRect(destRect, 0);
    
    RasterAccessor dst = new RasterAccessor(dest, destRect, formatTags[1], getColorModel());
    
    RasterAccessor src = new RasterAccessor(sources[0], srcRect, formatTags[0], getSourceImage(0).getColorModel());
    

    switch (dst.getDataType()) {
    case 0: 
      computeRectByte(src, dst);
      break;
    case 1: 
      computeRectUShort(src, dst);
      break;
    case 2: 
      computeRectShort(src, dst);
      break;
    case 3: 
      computeRectInt(src, dst);
      break;
    case 4: 
      computeRectFloat(src, dst);
      break;
    case 5: 
      computeRectDouble(src, dst);
    }
    
    
    if (dst.needsClamping())
    {
      dst.clampDataArrays();
    }
    dst.copyDataToRaster();
  }
  
  private void computeRectByte(RasterAccessor src, RasterAccessor dst)
  {
    initByteTable();
    
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
      byte[] d = dstData[b];
      byte[] s = srcData[b];
      byte[] clamp = byteTable[b];
      
      int dstLineOffset = dstBandOffsets[b];
      int srcLineOffset = srcBandOffsets[b];
      
      for (int h = 0; h < dstHeight; h++) {
        int dstPixelOffset = dstLineOffset;
        int srcPixelOffset = srcLineOffset;
        
        dstLineOffset += dstLineStride;
        srcLineOffset += srcLineStride;
        
        int dstEnd = dstPixelOffset + dstWidth * dstPixelStride;
        while (dstPixelOffset < dstEnd) {
          d[dstPixelOffset] = clamp[(s[srcPixelOffset] & 0xFF)];
          
          dstPixelOffset += dstPixelStride;
          srcPixelOffset += srcPixelStride;
        }
      }
    }
  }
  
  private void computeRectUShort(RasterAccessor src, RasterAccessor dst)
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
      int c = ImageUtil.clampRoundInt(constants[b]);
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
          d[dstPixelOffset] = ImageUtil.clampUShort((s[srcPixelOffset] & 0xFFFF) + c);
          

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
      int c = ImageUtil.clampRoundInt(constants[b]);
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
          d[dstPixelOffset] = ImageUtil.clampShort(s[srcPixelOffset] + c);
          
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
      long c = ImageUtil.clampRoundInt(constants[b]);
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
          d[dstPixelOffset] = ImageUtil.clampInt(s[srcPixelOffset] + c);
          
          dstPixelOffset += dstPixelStride;
          srcPixelOffset += srcPixelStride;
        }
      }
    }
  }
  
  private void computeRectFloat(RasterAccessor src, RasterAccessor dst)
  {
    int dstWidth = dst.getWidth();
    int dstHeight = dst.getHeight();
    int dstBands = dst.getNumBands();
    
    int dstLineStride = dst.getScanlineStride();
    int dstPixelStride = dst.getPixelStride();
    int[] dstBandOffsets = dst.getBandOffsets();
    float[][] dstData = dst.getFloatDataArrays();
    
    int srcLineStride = src.getScanlineStride();
    int srcPixelStride = src.getPixelStride();
    int[] srcBandOffsets = src.getBandOffsets();
    float[][] srcData = src.getFloatDataArrays();
    
    for (int b = 0; b < dstBands; b++) {
      double c = constants[b];
      float[] d = dstData[b];
      float[] s = srcData[b];
      
      int dstLineOffset = dstBandOffsets[b];
      int srcLineOffset = srcBandOffsets[b];
      
      for (int h = 0; h < dstHeight; h++) {
        int dstPixelOffset = dstLineOffset;
        int srcPixelOffset = srcLineOffset;
        
        dstLineOffset += dstLineStride;
        srcLineOffset += srcLineStride;
        
        for (int w = 0; w < dstWidth; w++) {
          d[dstPixelOffset] = ImageUtil.clampFloat(s[srcPixelOffset] + c);
          
          dstPixelOffset += dstPixelStride;
          srcPixelOffset += srcPixelStride;
        }
      }
    }
  }
  
  private void computeRectDouble(RasterAccessor src, RasterAccessor dst)
  {
    int dstWidth = dst.getWidth();
    int dstHeight = dst.getHeight();
    int dstBands = dst.getNumBands();
    
    int dstLineStride = dst.getScanlineStride();
    int dstPixelStride = dst.getPixelStride();
    int[] dstBandOffsets = dst.getBandOffsets();
    double[][] dstData = dst.getDoubleDataArrays();
    
    int srcLineStride = src.getScanlineStride();
    int srcPixelStride = src.getPixelStride();
    int[] srcBandOffsets = src.getBandOffsets();
    double[][] srcData = src.getDoubleDataArrays();
    
    for (int b = 0; b < dstBands; b++) {
      double c = constants[b];
      double[] d = dstData[b];
      double[] s = srcData[b];
      
      int dstLineOffset = dstBandOffsets[b];
      int srcLineOffset = srcBandOffsets[b];
      
      for (int h = 0; h < dstHeight; h++) {
        int dstPixelOffset = dstLineOffset;
        int srcPixelOffset = srcLineOffset;
        
        dstLineOffset += dstLineStride;
        srcLineOffset += srcLineStride;
        
        for (int w = 0; w < dstWidth; w++) {
          s[srcPixelOffset] += c;
          
          dstPixelOffset += dstPixelStride;
          srcPixelOffset += srcPixelStride;
        }
      }
    }
  }
}
