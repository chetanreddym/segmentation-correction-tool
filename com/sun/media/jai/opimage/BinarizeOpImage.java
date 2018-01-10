package com.sun.media.jai.opimage;

import com.sun.media.jai.util.ImageUtil;
import com.sun.media.jai.util.JDKWorkarounds;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ImageLayout;
import javax.media.jai.PackedImageData;
import javax.media.jai.PixelAccessor;
import javax.media.jai.PointOpImage;
import javax.media.jai.UnpackedImageData;
































final class BinarizeOpImage
  extends PointOpImage
{
  private static byte[] byteTable = { Byte.MIN_VALUE, 64, 32, 16, 8, 4, 2, 1 };
  







  private static int[] bitsOn = null;
  





  private double threshold;
  





  public BinarizeOpImage(RenderedImage source, Map config, ImageLayout layout, double threshold)
  {
    super(source, layoutHelper(source, layout, config), config, true);
    
    if (source.getSampleModel().getNumBands() != 1) {
      throw new IllegalArgumentException(JaiI18N.getString("BinarizeOpImage0"));
    }
    
    this.threshold = threshold;
  }
  



  private static ImageLayout layoutHelper(RenderedImage source, ImageLayout il, Map config)
  {
    ImageLayout layout = il == null ? new ImageLayout() : (ImageLayout)il.clone();
    

    SampleModel sm = layout.getSampleModel(source);
    if (!ImageUtil.isBinary(sm)) {
      sm = new MultiPixelPackedSampleModel(0, layout.getTileWidth(source), layout.getTileHeight(source), 1);
      


      layout.setSampleModel(sm);
    }
    
    ColorModel cm = layout.getColorModel(null);
    if ((cm == null) || (!JDKWorkarounds.areCompatibleDataModels(sm, cm)))
    {
      layout.setColorModel(ImageUtil.getCompatibleColorModel(sm, config));
    }
    

    return layout;
  }
  











  protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect)
  {
    switch (sources[0].getSampleModel().getDataType()) {
    case 0: 
      byteLoop(sources[0], dest, destRect);
      break;
    
    case 2: 
      shortLoop(sources[0], dest, destRect);
      break;
    case 1: 
      ushortLoop(sources[0], dest, destRect);
      break;
    case 3: 
      intLoop(sources[0], dest, destRect);
      break;
    
    case 4: 
      floatLoop(sources[0], dest, destRect);
      break;
    case 5: 
      doubleLoop(sources[0], dest, destRect);
      break;
    
    default: 
      throw new RuntimeException(JaiI18N.getString("BinarizeOpImage1"));
    }
    
  }
  

  private void byteLoop(Raster source, WritableRaster dest, Rectangle destRect)
  {
    if (threshold <= 0.0D)
    {
      setTo1(dest, destRect);
      return; }
    if (threshold > 255.0D)
    {
      return;
    }
    
    short thresholdI = (short)(int)Math.ceil(threshold);
    




    Rectangle srcRect = mapDestRect(destRect, 0);
    
    PixelAccessor pa = new PixelAccessor(dest.getSampleModel(), null);
    PackedImageData pid = pa.getPackedPixels(dest, destRect, true, false);
    int offset = offset;
    PixelAccessor srcPa = new PixelAccessor(source.getSampleModel(), null);
    
    UnpackedImageData srcImD = srcPa.getPixels(source, srcRect, 0, false);
    int srcOffset = bandOffsets[0];
    byte[] srcData = ((byte[][])data)[0];
    int pixelStride = pixelStride;
    
    int ind0 = bitOffset;
    for (int h = 0; h < height; h++) {
      int indE = ind0 + width;
      int b = ind0; for (int s = srcOffset; b < indE; s += pixelStride) {
        if ((srcData[s] & 0xFF) >= thresholdI) {
          int tmp202_201 = (offset + (b >> 3)); byte[] tmp202_192 = data;tmp202_192[tmp202_201] = ((byte)(tmp202_192[tmp202_201] | byteTable[(b % 8)]));
        }
        b++;
      }
      


      offset += lineStride;
      srcOffset += lineStride;
    }
    pa.setPackedPixels(pid);
  }
  




  private void shortLoop(Raster source, WritableRaster dest, Rectangle destRect)
  {
    if (threshold <= -32768.0D)
    {
      setTo1(dest, destRect);
      return; }
    if (threshold > 32767.0D)
    {
      return;
    }
    
    short thresholdS = (short)(int)Math.ceil(threshold);
    




    Rectangle srcRect = mapDestRect(destRect, 0);
    
    PixelAccessor pa = new PixelAccessor(dest.getSampleModel(), null);
    PackedImageData pid = pa.getPackedPixels(dest, destRect, true, false);
    int offset = offset;
    PixelAccessor srcPa = new PixelAccessor(source.getSampleModel(), null);
    
    UnpackedImageData srcImD = srcPa.getPixels(source, srcRect, 2, false);
    int srcOffset = bandOffsets[0];
    short[] srcData = ((short[][])data)[0];
    int pixelStride = pixelStride;
    
    int ind0 = bitOffset;
    for (int h = 0; h < height; h++) {
      int indE = ind0 + width;
      int b = ind0; for (int s = srcOffset; b < indE; s += pixelStride) {
        if (srcData[s] >= thresholdS) {
          int tmp200_199 = (offset + (b >> 3)); byte[] tmp200_190 = data;tmp200_190[tmp200_199] = ((byte)(tmp200_190[tmp200_199] | byteTable[(b % 8)]));
        }
        b++;
      }
      


      offset += lineStride;
      srcOffset += lineStride;
    }
    pa.setPackedPixels(pid);
  }
  




  private void ushortLoop(Raster source, WritableRaster dest, Rectangle destRect)
  {
    if (threshold <= 0.0D)
    {
      setTo1(dest, destRect);
      return; }
    if (threshold > 65535.0D)
    {
      return;
    }
    
    int thresholdI = (int)Math.ceil(threshold);
    




    Rectangle srcRect = mapDestRect(destRect, 0);
    
    PixelAccessor pa = new PixelAccessor(dest.getSampleModel(), null);
    PackedImageData pid = pa.getPackedPixels(dest, destRect, true, false);
    int offset = offset;
    PixelAccessor srcPa = new PixelAccessor(source.getSampleModel(), null);
    
    UnpackedImageData srcImD = srcPa.getPixels(source, srcRect, 1, false);
    int srcOffset = bandOffsets[0];
    short[] srcData = ((short[][])data)[0];
    int pixelStride = pixelStride;
    
    int ind0 = bitOffset;
    for (int h = 0; h < height; h++) {
      int indE = ind0 + width;
      int b = ind0; for (int s = srcOffset; b < indE; s += pixelStride) {
        if ((srcData[s] & 0xFFFF) >= thresholdI) {
          int tmp200_199 = (offset + (b >> 3)); byte[] tmp200_190 = data;tmp200_190[tmp200_199] = ((byte)(tmp200_190[tmp200_199] | byteTable[(b % 8)]));
        }
        b++;
      }
      


      offset += lineStride;
      srcOffset += lineStride;
    }
    pa.setPackedPixels(pid);
  }
  




  private void intLoop(Raster source, WritableRaster dest, Rectangle destRect)
  {
    if (threshold <= -2.147483648E9D)
    {
      setTo1(dest, destRect);
      return; }
    if (threshold > 2.147483647E9D)
    {
      return;
    }
    


    int thresholdI = (int)Math.ceil(threshold);
    




    Rectangle srcRect = mapDestRect(destRect, 0);
    
    PixelAccessor pa = new PixelAccessor(dest.getSampleModel(), null);
    PackedImageData pid = pa.getPackedPixels(dest, destRect, true, false);
    int offset = offset;
    PixelAccessor srcPa = new PixelAccessor(source.getSampleModel(), null);
    
    UnpackedImageData srcImD = srcPa.getPixels(source, srcRect, 3, false);
    int srcOffset = bandOffsets[0];
    int[] srcData = ((int[][])data)[0];
    int pixelStride = pixelStride;
    
    int ind0 = bitOffset;
    for (int h = 0; h < height; h++) {
      int indE = ind0 + width;
      int b = ind0; for (int s = srcOffset; b < indE; s += pixelStride) {
        if (srcData[s] >= threshold) {
          int tmp203_202 = (offset + (b >> 3)); byte[] tmp203_193 = data;tmp203_193[tmp203_202] = ((byte)(tmp203_193[tmp203_202] | byteTable[(b % 8)]));
        }
        b++;
      }
      


      offset += lineStride;
      srcOffset += lineStride;
    }
    pa.setPackedPixels(pid);
  }
  




  private void floatLoop(Raster source, WritableRaster dest, Rectangle destRect)
  {
    Rectangle srcRect = mapDestRect(destRect, 0);
    
    PixelAccessor pa = new PixelAccessor(dest.getSampleModel(), null);
    PackedImageData pid = pa.getPackedPixels(dest, destRect, true, false);
    int offset = offset;
    PixelAccessor srcPa = new PixelAccessor(source.getSampleModel(), null);
    
    UnpackedImageData srcImD = srcPa.getPixels(source, srcRect, 4, false);
    int srcOffset = bandOffsets[0];
    float[] srcData = ((float[][])data)[0];
    int pixelStride = pixelStride;
    
    int ind0 = bitOffset;
    for (int h = 0; h < height; h++) {
      int indE = ind0 + width;
      int b = ind0; for (int s = srcOffset; b < indE; s += pixelStride) {
        if (srcData[s] > threshold) {
          int tmp163_162 = (offset + (b >> 3)); byte[] tmp163_153 = data;tmp163_153[tmp163_162] = ((byte)(tmp163_153[tmp163_162] | byteTable[(b % 8)]));
        }
        b++;
      }
      


      offset += lineStride;
      srcOffset += lineStride;
    }
    pa.setPackedPixels(pid);
  }
  



  private void doubleLoop(Raster source, WritableRaster dest, Rectangle destRect)
  {
    Rectangle srcRect = mapDestRect(destRect, 0);
    
    PixelAccessor pa = new PixelAccessor(dest.getSampleModel(), null);
    PackedImageData pid = pa.getPackedPixels(dest, destRect, true, false);
    int offset = offset;
    PixelAccessor srcPa = new PixelAccessor(source.getSampleModel(), null);
    
    UnpackedImageData srcImD = srcPa.getPixels(source, srcRect, 5, false);
    int srcOffset = bandOffsets[0];
    double[] srcData = ((double[][])data)[0];
    int pixelStride = pixelStride;
    
    int ind0 = bitOffset;
    for (int h = 0; h < height; h++) {
      int indE = ind0 + width;
      int b = ind0; for (int s = srcOffset; b < indE; s += pixelStride) {
        if (srcData[s] > threshold) {
          int tmp162_161 = (offset + (b >> 3)); byte[] tmp162_152 = data;tmp162_152[tmp162_161] = ((byte)(tmp162_152[tmp162_161] | byteTable[(b % 8)]));
        }
        b++;
      }
      


      offset += lineStride;
      srcOffset += lineStride;
    }
    pa.setPackedPixels(pid);
  }
  

  private void setTo1(Raster dest, Rectangle destRect)
  {
    initBitsOn();
    PixelAccessor pa = new PixelAccessor(dest.getSampleModel(), null);
    PackedImageData pid = pa.getPackedPixels(dest, destRect, true, false);
    int offset = offset;
    
    for (int h = 0; h < height; h++) {
      int ind0 = bitOffset;
      int indE = ind0 + width - 1;
      if (indE < 8)
      {
        data[offset] = ((byte)(data[offset] | bitsOn[indE]));
      }
      else {
        data[offset] = ((byte)(data[offset] | bitsOn[7]));
        
        for (int b = offset + 1; b <= offset + (indE - 7) / 8; b++) {
          data[b] = -1;
        }
        

        int remBits = indE % 8;
        if (remBits % 8 != 7) {
          indE = offset + indE / 8;
          data[indE] = ((byte)(data[indE] | bitsOn[remBits]));
        }
      }
      offset += lineStride;
    }
    pa.setPackedPixels(pid);
  }
  


  private static synchronized void initBitsOn()
  {
    if (bitsOn != null) {
      return;
    }
    bitsOn = new int[64];
    for (int i = 0; i < 8; i++) {
      for (int j = i; j < 8; j++) {
        int bi = 255 >> i;
        int bj = 255 << 7 - j;
        bitsOn[(j + (i << 3))] = (bi & bj);
      }
    }
  }
}
