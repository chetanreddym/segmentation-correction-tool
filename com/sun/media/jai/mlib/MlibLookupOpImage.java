package com.sun.media.jai.mlib;

import com.sun.media.jai.util.ImageUtil;
import com.sun.media.jai.util.JDKWorkarounds;
import com.sun.medialib.mlib.Image;
import com.sun.medialib.mlib.mediaLibImage;
import java.awt.Rectangle;
import java.awt.image.ComponentSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ImageLayout;
import javax.media.jai.LookupTableJAI;
import javax.media.jai.PointOpImage;












final class MlibLookupOpImage
  extends PointOpImage
{
  private LookupTableJAI table;
  
  public MlibLookupOpImage(RenderedImage source, Map config, ImageLayout layout, LookupTableJAI table)
  {
    super(source, layout, config, true);
    
    this.table = table;
    
    SampleModel sm = source.getSampleModel();
    
    if ((sampleModel.getTransferType() != table.getDataType()) || (sampleModel.getNumBands() != table.getDestNumBands(sm.getNumBands())))
    {






      sampleModel = table.getDestSampleModel(sm, tileWidth, tileHeight);
      if ((colorModel != null) && (!JDKWorkarounds.areCompatibleDataModels(sampleModel, colorModel)))
      {

        colorModel = ImageUtil.getCompatibleColorModel(sampleModel, config);
      }
    }
    

    permitInPlaceOperation();
  }
  

  protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect)
  {
    Raster source = sources[0];
    Rectangle srcRect = mapDestRect(destRect, 0);
    
    int srcTag = MediaLibAccessor.findCompatibleTag(null, source);
    int dstTag = MediaLibAccessor.findCompatibleTag(null, dest);
    
    SampleModel sm = source.getSampleModel();
    if (sm.getNumBands() > 1) {
      int srcCopy = srcTag & 0x80;
      int dstCopy = dstTag & 0x80;
      
      int srcDtype = srcTag & 0x7F;
      int dstDtype = dstTag & 0x7F;
      
      if ((srcCopy != 0) || (dstCopy != 0) || (!MediaLibAccessor.isPixelSequential(sm)) || (!MediaLibAccessor.isPixelSequential(sampleModel)) || (!MediaLibAccessor.hasMatchingBandOffsets((ComponentSampleModel)sm, (ComponentSampleModel)sampleModel)))
      {






        srcTag = srcDtype | 0x80;
        dstTag = dstDtype | 0x80;
      }
    }
    
    MediaLibAccessor src = new MediaLibAccessor(source, srcRect, srcTag);
    MediaLibAccessor dst = new MediaLibAccessor(dest, destRect, dstTag);
    
    mediaLibImage[] srcMLI = src.getMediaLibImages();
    mediaLibImage[] dstMLI = dst.getMediaLibImages();
    
    if (srcMLI.length < dstMLI.length) {
      mediaLibImage srcMLI0 = srcMLI[0];
      srcMLI = new mediaLibImage[dstMLI.length];
      
      for (int i = 0; i < dstMLI.length; i++) {
        srcMLI[i] = srcMLI0;
      }
    }
    
    int[] bandOffsets = dst.getBandOffsets();
    Object table = getTableData(bandOffsets);
    int[] offsets = getTableOffsets(bandOffsets);
    
    for (int i = 0; i < dstMLI.length; i++) {
      Image.LookUp2(dstMLI[i], srcMLI[i], table, offsets);
    }
    

    if (dst.isDataCopy()) {
      dst.copyDataToRaster();
    }
  }
  
  private Object getTableData(int[] bandOffsets) {
    int tbands = table.getNumBands();
    int dbands = sampleModel.getNumBands();
    Object data = null;
    
    switch (table.getDataType()) {
    case 0: 
      byte[][] bdata = new byte[dbands][];
      if (tbands < dbands) {
        for (int i = 0; i < dbands; i++) {
          bdata[i] = table.getByteData(0);
        }
      } else {
        for (int i = 0; i < dbands; i++) {
          bdata[i] = table.getByteData(bandOffsets[i]);
        }
      }
      data = bdata;
      break;
    case 1: 
    case 2: 
      short[][] sdata = new short[dbands][];
      if (tbands < dbands) {
        for (int i = 0; i < dbands; i++) {
          sdata[i] = table.getShortData(0);
        }
      } else {
        for (int i = 0; i < dbands; i++) {
          sdata[i] = table.getShortData(bandOffsets[i]);
        }
      }
      data = sdata;
      break;
    case 3: 
      int[][] idata = new int[dbands][];
      if (tbands < dbands) {
        for (int i = 0; i < dbands; i++) {
          idata[i] = table.getIntData(0);
        }
      } else {
        for (int i = 0; i < dbands; i++) {
          idata[i] = table.getIntData(bandOffsets[i]);
        }
      }
      data = idata;
      break;
    case 4: 
      float[][] fdata = new float[dbands][];
      if (tbands < dbands) {
        for (int i = 0; i < dbands; i++) {
          fdata[i] = table.getFloatData(0);
        }
      } else {
        for (int i = 0; i < dbands; i++) {
          fdata[i] = table.getFloatData(bandOffsets[i]);
        }
      }
      data = fdata;
      break;
    case 5: 
      double[][] ddata = new double[dbands][];
      if (tbands < dbands) {
        for (int i = 0; i < dbands; i++) {
          ddata[i] = table.getDoubleData(0);
        }
      } else {
        for (int i = 0; i < dbands; i++) {
          ddata[i] = table.getDoubleData(bandOffsets[i]);
        }
      }
      data = ddata;
    }
    
    
    return data;
  }
  
  private int[] getTableOffsets(int[] bandOffsets) {
    int tbands = table.getNumBands();
    int dbands = sampleModel.getNumBands();
    int[] offsets = new int[dbands];
    
    if (tbands < dbands) {
      for (int i = 0; i < dbands; i++) {
        offsets[i] = table.getOffset(0);
      }
    } else {
      for (int i = 0; i < dbands; i++) {
        offsets[i] = table.getOffset(bandOffsets[i]);
      }
    }
    
    return offsets;
  }
}
