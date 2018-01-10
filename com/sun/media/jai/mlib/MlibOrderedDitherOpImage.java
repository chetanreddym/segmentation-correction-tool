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
import javax.media.jai.PointOpImage;
import javax.media.jai.RasterFactory;














































final class MlibOrderedDitherOpImage
  extends PointOpImage
{
  private static final int DMASK_SCALE_EXPONENT = 16;
  protected mediaLibImageColormap mlibColormap;
  protected int[][] dmask;
  protected int dmaskWidth;
  protected int dmaskHeight;
  protected int dmaskScale;
  
  static ImageLayout layoutHelper(ImageLayout layout, RenderedImage source, ColorCube colormap)
  {
    ImageLayout il;
    ImageLayout il;
    if (layout == null) {
      il = new ImageLayout(source);
    } else {
      il = (ImageLayout)layout.clone();
    }
    

    SampleModel sm = il.getSampleModel(source);
    

    if ((colormap.getNumBands() == 1) && (colormap.getNumEntries() == 2) && (!ImageUtil.isBinary(il.getSampleModel(source))))
    {

      sm = new MultiPixelPackedSampleModel(0, il.getTileWidth(source), il.getTileHeight(source), 1);
      


      il.setSampleModel(sm);
    }
    

    if (sm.getNumBands() != 1)
    {
      sm = RasterFactory.createComponentSampleModel(sm, sm.getTransferType(), sm.getWidth(), sm.getHeight(), 1);
      



      il.setSampleModel(sm);
      

      ColorModel cm = il.getColorModel(null);
      if ((cm != null) && (!JDKWorkarounds.areCompatibleDataModels(sm, cm)))
      {

        il.unsetValid(512);
      }
    }
    






    if (((layout == null) || (!il.isValid(512))) && (source.getSampleModel().getDataType() == 0) && (il.getSampleModel(null).getDataType() == 0) && (colormap.getDataType() == 0) && (colormap.getNumBands() == 3))
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
  













  public MlibOrderedDitherOpImage(RenderedImage source, Map config, ImageLayout layout, ColorCube colormap, KernelJAI[] ditherMask)
  {
    super(source, layoutHelper(layout, source, colormap), config, true);
    



    mlibColormap = Image.ColorDitherInit(colormap.getDimension(), 1, ImageUtil.isBinary(sampleModel) ? 0 : 1, colormap.getNumBands(), colormap.getNumEntries(), colormap.getOffset(), colormap.getByteData());
    









    dmaskWidth = ditherMask[0].getWidth();
    dmaskHeight = ditherMask[0].getHeight();
    dmaskScale = 65536;
    
    int numMasks = ditherMask.length;
    dmask = new int[numMasks][];
    
    for (int k = 0; k < numMasks; k++) {
      KernelJAI mask = ditherMask[k];
      
      if ((mask.getWidth() != dmaskWidth) || (mask.getHeight() != dmaskHeight))
      {
        throw new IllegalArgumentException(JaiI18N.getString("MlibOrderedDitherOpImage0"));
      }
      


      float[] dmaskData = ditherMask[k].getKernelData();
      int numElements = dmaskData.length;
      dmask[k] = new int[numElements];
      int[] dm = dmask[k];
      for (int i = 0; i < numElements; i++) {
        dm[i] = ((int)(dmaskData[i] * dmaskScale));
      }
    }
    

    permitInPlaceOperation();
  }
  











  protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect)
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
    
    Image.ColorOrderedDitherMxN(dstML[0], srcML[0], dmask, dmaskWidth, dmaskHeight, 16, mlibColormap);
    






    if (dstAccessor.isDataCopy()) {
      dstAccessor.clampDataArrays();
      dstAccessor.copyDataToRaster();
    }
  }
}
