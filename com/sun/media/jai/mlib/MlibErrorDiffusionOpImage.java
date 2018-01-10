package com.sun.media.jai.mlib;

import com.sun.media.jai.util.ImageUtil;
import com.sun.media.jai.util.JDKWorkarounds;
import com.sun.medialib.mlib.Image;
import com.sun.medialib.mlib.mediaLibImage;
import com.sun.medialib.mlib.mediaLibImageColormap;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ColorCube;
import javax.media.jai.ImageLayout;
import javax.media.jai.KernelJAI;
import javax.media.jai.LookupTableJAI;
import javax.media.jai.RasterFactory;
import javax.media.jai.UntiledOpImage;


















































final class MlibErrorDiffusionOpImage
  extends UntiledOpImage
{
  private static final int KERNEL_SCALE_EXPONENT = 16;
  protected mediaLibImageColormap mlibColormap;
  protected int[] kernel;
  protected int kernelWidth;
  protected int kernelHeight;
  protected int kernelKeyX;
  protected int kernelKeyY;
  protected int kernelScale;
  
  static ImageLayout layoutHelper(ImageLayout layout, RenderedImage source, LookupTableJAI colormap)
  {
    ImageLayout il = layout == null ? new ImageLayout() : (ImageLayout)layout.clone();
    


    il.setMinX(source.getMinX());
    il.setMinY(source.getMinY());
    il.setWidth(source.getWidth());
    il.setHeight(source.getHeight());
    

    SampleModel sm = il.getSampleModel(source);
    

    if ((colormap.getNumBands() == 1) && (colormap.getNumEntries() == 2) && (!ImageUtil.isBinary(il.getSampleModel(source))))
    {

      sm = new MultiPixelPackedSampleModel(0, il.getTileWidth(source), il.getTileHeight(source), 1);
      


      il.setSampleModel(sm);
    }
    

    if (sm.getNumBands() != 1) {
      sm = RasterFactory.createComponentSampleModel(sm, sm.getTransferType(), sm.getWidth(), sm.getHeight(), 1);
      




      il.setSampleModel(sm);
      

      ColorModel cm = il.getColorModel(null);
      if ((cm != null) && (!JDKWorkarounds.areCompatibleDataModels(sm, cm)))
      {

        il.unsetValid(512);
      }
    }
    




    if (((layout == null) || (!il.isValid(512))) && (source.getSampleModel().getDataType() == 0) && (sm.getDataType() == 0) && (colormap.getDataType() == 0) && (colormap.getNumBands() == 3))
    {



      ColorModel cm = source.getColorModel();
      if ((cm == null) || ((cm != null) && (cm.getColorSpace().isCS_sRGB())))
      {
        int size = colormap.getNumEntries();
        byte[][] cmap = new byte[3]['Ā'];
        for (int i = 0; i < 3; i++) {
          byte[] band = cmap[i];
          byte[] data = colormap.getByteData(i);
          int offset = colormap.getOffset(i);
          int end = offset + size;
          for (int j = 0; j < offset; j++) {
            band[j] = 0;
          }
          for (int j = offset; j < end; j++) {
            band[j] = data[(j - offset)];
          }
          for (int j = end; j < 256; j++) {
            band[j] = -1;
          }
        }
        
        il.setColorModel(new IndexColorModel(8, 256, cmap[0], cmap[1], cmap[2]));
      }
    }
    


    return il;
  }
  















  public MlibErrorDiffusionOpImage(RenderedImage source, Map config, ImageLayout layout, LookupTableJAI colormap, KernelJAI errorKernel)
  {
    super(source, config, layout);
    


    mlibColormap = Image.ColorDitherInit((colormap instanceof ColorCube) ? ((ColorCube)colormap).getDimension() : null, 1, ImageUtil.isBinary(sampleModel) ? 0 : 1, colormap.getNumBands(), colormap.getNumEntries(), colormap.getOffset(), colormap.getByteData());
    










    kernelWidth = errorKernel.getWidth();
    kernelHeight = errorKernel.getHeight();
    kernelKeyX = errorKernel.getXOrigin();
    kernelKeyY = errorKernel.getYOrigin();
    kernelScale = 65536;
    

    float[] kernelData = errorKernel.getKernelData();
    int numElements = kernelData.length;
    kernel = new int[numElements];
    for (int i = 0; i < numElements; i++) {
      kernel[i] = ((int)(kernelData[i] * kernelScale));
    }
  }
  

















  protected void computeImage(Raster[] sources, WritableRaster dest, Rectangle destRect)
  {
    Raster source = sources[0];
    int destFormatTag;
    int destFormatTag;
    int sourceFormatTag;
    if (ImageUtil.isBinary(dest.getSampleModel()))
    {

      int sourceFormatTag = MediaLibAccessor.findCompatibleTag(sources, source);
      



      destFormatTag = dest.getSampleModel().getDataType() | 0x100 | 0x0;

    }
    else
    {
      sourceFormatTag = destFormatTag = MediaLibAccessor.findCompatibleTag(sources, dest);
    }
    

    MediaLibAccessor srcAccessor = new MediaLibAccessor(sources[0], destRect, sourceFormatTag, false);
    
    MediaLibAccessor dstAccessor = new MediaLibAccessor(dest, destRect, destFormatTag, true);
    

    mediaLibImage[] srcML = srcAccessor.getMediaLibImages();
    mediaLibImage[] dstML = dstAccessor.getMediaLibImages();
    
    Image.ColorErrorDiffusionMxN(dstML[0], srcML[0], kernel, kernelWidth, kernelHeight, kernelKeyX, kernelKeyY, 16, mlibColormap);
    








    if (dstAccessor.isDataCopy()) {
      dstAccessor.clampDataArrays();
      dstAccessor.copyDataToRaster();
    }
  }
}
