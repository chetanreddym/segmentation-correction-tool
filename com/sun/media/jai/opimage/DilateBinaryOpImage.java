package com.sun.media.jai.opimage;

import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.AreaOpImage;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.PackedImageData;
import javax.media.jai.PixelAccessor;































































































































final class DilateBinaryOpImage
  extends AreaOpImage
{
  protected KernelJAI kernel;
  private int kw;
  private int kh;
  private int kx;
  private int ky;
  private int[] kdataPack;
  private int kwPack;
  
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
        config.put(JAI.KEY_REPLACE_INDEX_COLOR_MODEL, Boolean.FALSE);
        RenderingHints hints = (RenderingHints)configuration;
        config = (RenderingHints)hints.clone();
      }
    }
    
    return config;
  }
  















  public DilateBinaryOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, KernelJAI kernel)
  {
    super(source, layout, configHelper(config), true, extender, kernel.getLeftPadding(), kernel.getRightPadding(), kernel.getTopPadding(), kernel.getBottomPadding());
    








    this.kernel = kernel;
    kw = kernel.getWidth();
    kh = kernel.getHeight();
    kx = kernel.getXOrigin();
    ky = kernel.getYOrigin();
    
    kwPack = ((kw + 31) / 32);
    kdataPack = packKernel(kernel);
  }
  











  protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect)
  {
    Raster source = sources[0];
    
    PixelAccessor pa = new PixelAccessor(source.getSampleModel(), null);
    PackedImageData srcIm = pa.getPackedPixels(source, source.getBounds(), false, false);
    

    pa = new PixelAccessor(dest.getSampleModel(), null);
    PackedImageData dstIm = pa.getPackedPixels(dest, destRect, true, false);
    


    int[] srcUK = new int[kwPack * kh];
    


    int dheight = height;
    int dwidth = width;
    
    int sOffset = offset;
    int dOffset = offset;
    for (int j = 0; j < dheight; j++)
    {




      for (int m = 0; m < srcUK.length; m++) {
        srcUK[m] = 0;
      }
      



      for (int i = 0; i < kw - 1; i++) {
        bitShiftMatrixLeft(srcUK, kh, kwPack);
        int lastCol = kwPack - 1;
        int bitLoc = bitOffset + i;
        int byteLoc = bitLoc >> 3;
        bitLoc = 7 - (bitLoc & 0x7);
        int m = 0;int sOffsetB = sOffset;
        for (; m < kh; 
            sOffsetB += lineStride)
        {
          int selement = data[(sOffsetB + byteLoc)];
          int val = selement >> bitLoc & 0x1;
          srcUK[lastCol] |= val;
          lastCol += kwPack;m++;
        }
      }
      





      for (int i = 0; i < dwidth; i++)
      {
        bitShiftMatrixLeft(srcUK, kh, kwPack);
        int lastCol = kwPack - 1;
        int bitLoc = bitOffset + i + kw - 1;
        int byteLoc = bitLoc >> 3;
        bitLoc = 7 - (bitLoc & 0x7);
        int m = 0;int sOffsetB = sOffset;
        for (; m < kh; 
            sOffsetB += lineStride)
        {
          int selement = data[(sOffsetB + byteLoc)];
          int val = selement >> bitLoc & 0x1;
          srcUK[lastCol] |= val;
          lastCol += kwPack;m++;
        }
        




        for (int m = 0; m < srcUK.length; m++) {
          if ((srcUK[m] & kdataPack[m]) != 0) {
            int dBitLoc = bitOffset + i;
            int dshift = 7 - (dBitLoc & 0x7);
            int dByteLoc = (dBitLoc >> 3) + dOffset;
            int delement = data[dByteLoc];
            delement |= 1 << dshift;
            data[dByteLoc] = ((byte)delement);
            break;
          }
        }
      }
      
      sOffset += lineStride;
      dOffset += lineStride;
    }
    pa.setPackedPixels(dstIm);
  }
  





  private final int[] packKernel(KernelJAI kernel)
  {
    int kw = kernel.getWidth();
    int kh = kernel.getHeight();
    int kwPack = (31 + kw) / 32;
    int[] kerPacked = new int[kwPack * kh];
    float[] kdata = kernel.getKernelData();
    for (int j = 0; j < kw; j++) {
      int m = j;
      int lastCol = kwPack - 1;
      bitShiftMatrixLeft(kerPacked, kh, kwPack);
      for (int i = 0; i < kh; m += kw) {
        if (kdata[m] > 0.9F) {
          kerPacked[lastCol] |= 0x1;
        }
        i++;lastCol += kwPack;
      }
    }
    


    return kerPacked;
  }
  



  private static final void bitShiftMatrixLeft(int[] mat, int rows, int cols)
  {
    int m = 0;
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols - 1; j++) {
        mat[m] = (mat[m] << 1 | mat[(m + 1)] >>> 31);
        m++;
      }
      mat[m] <<= 1;
      m++;
    }
  }
}
