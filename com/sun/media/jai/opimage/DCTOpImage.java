package com.sun.media.jai.opimage;

import com.sun.media.jai.util.JDKWorkarounds;
import com.sun.media.jai.util.MathJAI;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ImageLayout;
import javax.media.jai.PlanarImage;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFactory;
import javax.media.jai.RasterFormatTag;
import javax.media.jai.UntiledOpImage;








































public class DCTOpImage
  extends UntiledOpImage
{
  private FCT fct;
  
  private static ImageLayout layoutHelper(ImageLayout layout, RenderedImage source)
  {
    ImageLayout il = layout == null ? new ImageLayout() : (ImageLayout)layout.clone();
    


    il.setMinX(source.getMinX());
    il.setMinY(source.getMinY());
    



    boolean createNewSampleModel = false;
    int w = il.getWidth(source);
    if (w > 1) {
      int newWidth = MathJAI.nextPositivePowerOf2(w);
      if (newWidth != w) {
        il.setWidth(w = newWidth);
        createNewSampleModel = true;
      }
    }
    int h = il.getHeight(source);
    if (h > 1) {
      int newHeight = MathJAI.nextPositivePowerOf2(h);
      if (newHeight != h) {
        il.setHeight(h = newHeight);
        createNewSampleModel = true;
      }
    }
    

    SampleModel sm = il.getSampleModel(source);
    int dataType = sm.getTransferType();
    if ((dataType != 4) && (dataType != 5))
    {
      dataType = 4;
      createNewSampleModel = true;
    }
    

    if (createNewSampleModel) {
      sm = RasterFactory.createComponentSampleModel(sm, dataType, w, h, sm.getNumBands());
      
      il.setSampleModel(sm);
      

      ColorModel cm = il.getColorModel(null);
      if ((cm != null) && (!JDKWorkarounds.areCompatibleDataModels(sm, cm)))
      {

        il.unsetValid(512);
      }
    }
    
    return il;
  }
  















  public DCTOpImage(RenderedImage source, Map config, ImageLayout layout, FCT fct)
  {
    super(source, config, layoutHelper(layout, source));
    

    this.fct = fct;
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
    

    if ((width == 1) && (height == 1)) {
      double[] pixel = source.getPixel(x, y, (double[])null);
      
      dest.setPixel(x, y, pixel);
      return;
    }
    

    fct.setLength(width > 1 ? getWidth() : getHeight());
    

    int srcWidth = source.getWidth();
    int srcHeight = source.getHeight();
    int srcX = source.getMinX();
    int srcY = source.getMinY();
    

    RasterFormatTag[] formatTags = getFormatTags();
    
    RasterAccessor srcAccessor = new RasterAccessor(source, new Rectangle(srcX, srcY, srcWidth, srcHeight), formatTags[0], getSourceImage(0).getColorModel());
    




    RasterAccessor dstAccessor = new RasterAccessor(dest, destRect, formatTags[1], getColorModel());
    



    int srcDataType = srcAccessor.getDataType();
    int dstDataType = dstAccessor.getDataType();
    

    int srcPixelStride = srcAccessor.getPixelStride();
    int srcScanlineStride = srcAccessor.getScanlineStride();
    int dstPixelStride = dstAccessor.getPixelStride();
    int dstScanlineStride = dstAccessor.getScanlineStride();
    

    int numBands = sampleModel.getNumBands();
    for (int band = 0; band < numBands; band++)
    {
      Object srcData = srcAccessor.getDataArray(band);
      Object dstData = dstAccessor.getDataArray(band);
      
      if (width > 1)
      {
        fct.setLength(getWidth());
        

        int srcOffset = srcAccessor.getBandOffset(band);
        int dstOffset = dstAccessor.getBandOffset(band);
        

        for (int row = 0; row < srcHeight; row++)
        {
          fct.setData(srcDataType, srcData, srcOffset, srcPixelStride, srcWidth);
          



          fct.transform();
          

          fct.getData(dstDataType, dstData, dstOffset, dstPixelStride);
          


          srcOffset += srcScanlineStride;
          dstOffset += dstScanlineStride;
        }
      }
      
      if (width == 1)
      {
        int srcOffset = srcAccessor.getBandOffset(band);
        int dstOffset = dstAccessor.getBandOffset(band);
        

        fct.setData(srcDataType, srcData, srcOffset, srcScanlineStride, srcHeight);
        



        fct.transform();
        

        fct.getData(dstDataType, dstData, dstOffset, dstScanlineStride);
      }
      else if (height > 1)
      {
        fct.setLength(getHeight());
        

        int dstOffset = dstAccessor.getBandOffset(band);
        

        for (int col = 0; col < width; col++)
        {
          fct.setData(dstDataType, dstData, dstOffset, dstScanlineStride, height);
          



          fct.transform();
          

          fct.getData(dstDataType, dstData, dstOffset, dstScanlineStride);
          


          dstOffset += dstPixelStride;
        }
      }
    }
    
    if (dstAccessor.needsClamping()) {
      dstAccessor.clampDataArrays();
    }
    

    dstAccessor.copyDataToRaster();
  }
}
