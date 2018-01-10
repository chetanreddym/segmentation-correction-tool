package com.sun.media.jai.mlib;

import com.sun.media.jai.util.JDKWorkarounds;
import com.sun.media.jai.util.MathJAI;
import com.sun.medialib.mlib.Image;
import com.sun.medialib.mlib.mediaLibImage;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.ColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.EnumeratedParameter;
import javax.media.jai.ImageLayout;
import javax.media.jai.RasterFactory;
import javax.media.jai.UntiledOpImage;
import javax.media.jai.operator.DFTDescriptor;























final class MlibDFTOpImage
  extends UntiledOpImage
{
  private int DFTMode;
  
  private static ImageLayout layoutHelper(ImageLayout layout, RenderedImage source, EnumeratedParameter dataNature)
  {
    boolean isComplexSource = !dataNature.equals(DFTDescriptor.REAL_TO_COMPLEX);
    
    boolean isComplexDest = !dataNature.equals(DFTDescriptor.COMPLEX_TO_REAL);
    


    SampleModel srcSampleModel = source.getSampleModel();
    int numSourceBands = srcSampleModel.getNumBands();
    

    if (((isComplexSource) && (numSourceBands != 2)) || ((!isComplexSource) && (numSourceBands != 1)))
    {


      throw new RuntimeException(JaiI18N.getString("MlibDFTOpImage0"));
    }
    

    ImageLayout il = layout == null ? new ImageLayout() : (ImageLayout)layout.clone();
    


    il.setMinX(source.getMinX());
    il.setMinY(source.getMinY());
    



    int currentWidth = il.getWidth(source);
    int currentHeight = il.getHeight(source);
    int newWidth;
    int newWidth;
    int newHeight; if ((currentWidth == 1) && (currentHeight == 1)) { int newHeight;
      newWidth = newHeight = 1; } else { int newHeight;
      if ((currentWidth == 1) && (currentHeight > 1)) {
        int newWidth = 1;
        newHeight = MathJAI.nextPositivePowerOf2(currentHeight); } else { int newHeight;
        if ((currentWidth > 1) && (currentHeight == 1)) {
          int newWidth = MathJAI.nextPositivePowerOf2(currentWidth);
          newHeight = 1;
        } else {
          newWidth = MathJAI.nextPositivePowerOf2(currentWidth);
          newHeight = MathJAI.nextPositivePowerOf2(currentHeight);
        } } }
    il.setWidth(newWidth);
    il.setHeight(newHeight);
    

    boolean createNewSampleModel = false;
    

    int requiredNumBands = numSourceBands;
    if ((isComplexSource) && (!isComplexDest)) {
      requiredNumBands /= 2;
    } else if ((!isComplexSource) && (isComplexDest)) {
      requiredNumBands *= 2;
    }
    

    SampleModel sm = il.getSampleModel(source);
    int numBands = sm.getNumBands();
    if (numBands != requiredNumBands) {
      numBands = requiredNumBands;
      createNewSampleModel = true;
    }
    

    int dataType = sm.getTransferType();
    if ((dataType != 4) && (dataType != 5))
    {
      dataType = 4;
      createNewSampleModel = true;
    }
    

    if (createNewSampleModel) {
      int[] bandOffsets = new int[numBands];
      


      for (int b = 0; b < numBands; b++) {
        bandOffsets[b] = b;
      }
      
      int lineStride = newWidth * numBands;
      sm = RasterFactory.createPixelInterleavedSampleModel(dataType, newWidth, newHeight, numBands, lineStride, bandOffsets);
      




      il.setSampleModel(sm);
      

      ColorModel cm = il.getColorModel(null);
      if ((cm != null) && (!JDKWorkarounds.areCompatibleDataModels(sm, cm)))
      {

        il.unsetValid(512);
      }
    }
    
    return il;
  }
  
















  public MlibDFTOpImage(RenderedImage source, Map config, ImageLayout layout, EnumeratedParameter dataNature, boolean isForward, EnumeratedParameter scaleType)
  {
    super(source, config, layoutHelper(layout, source, dataNature));
    
    if (scaleType.equals(DFTDescriptor.SCALING_NONE)) {
      DFTMode = (isForward ? 0 : 3);

    }
    else if (scaleType.equals(DFTDescriptor.SCALING_UNITARY)) {
      DFTMode = (isForward ? 2 : 5);

    }
    else if (scaleType.equals(DFTDescriptor.SCALING_DIMENSIONS)) {
      DFTMode = (isForward ? 1 : 4);

    }
    else
    {

      throw new RuntimeException(JaiI18N.getString("MlibDFTOpImage1"));
    }
  }
  








  public static boolean isAcceptableSampleModel(SampleModel sm)
  {
    if (!(sm instanceof ComponentSampleModel)) {
      return true;
    }
    
    ComponentSampleModel csm = (ComponentSampleModel)sm;
    
    int[] bandOffsets = csm.getBandOffsets();
    
    if ((bandOffsets.length == 2) && (bandOffsets[1] == bandOffsets[0] + 1))
    {
      return true;
    }
    
    return false;
  }
  












  public Point2D mapDestPoint(Point2D destPt)
  {
    if (destPt == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    return null;
  }
  









  public Point2D mapSourcePoint(Point2D sourcePt)
  {
    if (sourcePt == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    return null;
  }
  









  protected void computeImage(Raster[] sources, WritableRaster dest, Rectangle destRect)
  {
    Raster source = sources[0];
    
    int formatTag = MediaLibAccessor.findCompatibleTag(new Raster[] { source }, dest);
    

    MediaLibAccessor srcAccessor = new MediaLibAccessor(source, mapDestRect(destRect, 0), formatTag);
    
    MediaLibAccessor dstAccessor = new MediaLibAccessor(dest, destRect, formatTag);
    

    mediaLibImage[] srcML = srcAccessor.getMediaLibImages();
    mediaLibImage[] dstML = dstAccessor.getMediaLibImages();
    
    for (int i = 0; i < dstML.length; i++) {
      Image.FourierTransform(dstML[i], srcML[i], DFTMode);
    }
    

    if (dstAccessor.isDataCopy()) {
      dstAccessor.clampDataArrays();
      dstAccessor.copyDataToRaster();
    }
  }
}
