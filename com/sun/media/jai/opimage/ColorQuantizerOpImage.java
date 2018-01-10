package com.sun.media.jai.opimage;

import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ImageLayout;
import javax.media.jai.LookupTableJAI;
import javax.media.jai.PixelAccessor;
import javax.media.jai.PointOpImage;
import javax.media.jai.ROI;
import javax.media.jai.RasterFactory;
import javax.media.jai.UnpackedImageData;








































abstract class ColorQuantizerOpImage
  extends PointOpImage
{
  private static final int NBANDS = 3;
  private static final int NGRAYS = 256;
  protected PixelAccessor srcPA;
  protected int srcSampleType;
  protected boolean isInitialized = false;
  



  protected PixelAccessor destPA;
  



  protected LookupTableJAI colorMap;
  


  protected int maxColorNum;
  


  protected int xPeriod;
  


  protected int yPeriod;
  


  protected ROI roi;
  


  private int numBandsSource;
  


  protected boolean checkForSkippedTiles = false;
  
  static final int startPosition(int pos, int start, int period)
  {
    int t = (pos - start) % period;
    return t == 0 ? pos : pos + (period - t);
  }
  




  private static ImageLayout layoutHelper(ImageLayout layout, RenderedImage source)
  {
    ImageLayout il = layout == null ? new ImageLayout() : (ImageLayout)layout.clone();
    


    il.setMinX(source.getMinX());
    il.setMinY(source.getMinY());
    il.setWidth(source.getWidth());
    il.setHeight(source.getHeight());
    

    SampleModel sm = il.getSampleModel(source);
    

    if (sm.getNumBands() != 1) {
      sm = RasterFactory.createComponentSampleModel(sm, sm.getTransferType(), sm.getWidth(), sm.getHeight(), 1);
      




      il.setSampleModel(sm);
    }
    
    il.setColorModel(null);
    
    return il;
  }
  


















  public ColorQuantizerOpImage(RenderedImage source, Map config, ImageLayout layout, int maxColorNum, ROI roi, int xPeriod, int yPeriod)
  {
    super(source, layoutHelper(layout, source), config, true);
    

    SampleModel srcSampleModel = source.getSampleModel();
    

    numBandsSource = srcSampleModel.getNumBands();
    
    this.maxColorNum = maxColorNum;
    this.xPeriod = xPeriod;
    this.yPeriod = yPeriod;
    this.roi = roi;
    checkForSkippedTiles = ((xPeriod > tileWidth) || (yPeriod > tileHeight));
  }
  


  protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect)
  {
    if (colorMap == null) {
      train();
    }
    if (!isInitialized) {
      srcPA = new PixelAccessor(getSourceImage(0));
      srcSampleType = (srcPA.sampleType == -1 ? 0 : srcPA.sampleType);
      
      isInitialized = true;
    }
    
    UnpackedImageData uid = srcPA.getPixels(sources[0], destRect, srcSampleType, false);
    

    Rectangle rect = rect;
    byte[][] data = uid.getByteData();
    int srcLineStride = lineStride;
    int srcPixelStride = pixelStride;
    byte[] rBand = data[0];
    byte[] gBand = data[1];
    byte[] bBand = data[2];
    
    int lastLine = height * srcLineStride + bandOffsets[0];
    
    if (destPA == null) {
      destPA = new PixelAccessor(this);
    }
    UnpackedImageData destUid = destPA.getPixels(dest, destRect, sampleModel.getDataType(), false);
    


    int destLineOffset = bandOffsets[0];
    int destLineStride = lineStride;
    byte[] d = destUid.getByteData(0);
    
    int[] currentPixel = new int[3];
    for (int lo = bandOffsets[0]; lo < lastLine; lo += srcLineStride) {
      int lastPixel = lo + width * srcPixelStride - bandOffsets[0];
      
      int dstPixelOffset = destLineOffset;
      for (int po = lo - bandOffsets[0]; po < lastPixel; 
          po += srcPixelStride) {
        d[dstPixelOffset] = findNearestEntry(rBand[(po + bandOffsets[0])] & 0xFF, gBand[(po + bandOffsets[1])] & 0xFF, bBand[(po + bandOffsets[2])] & 0xFF);
        



        dstPixelOffset += pixelStride;
      }
      destLineOffset += destLineStride;
    }
  }
  
  public Object getProperty(String name)
  {
    int numBands = sampleModel.getNumBands();
    
    if ((name.equals("JAI.LookupTable")) || (name.equals("LUT")))
    {
      if (colorMap == null)
        train();
      return colorMap;
    }
    
    return super.getProperty(name);
  }
  
  protected abstract void train();
  
  public ColorModel getColorModel() {
    if (colorMap == null)
      train();
    if (colorModel == null) {
      colorModel = new IndexColorModel(8, colorMap.getByteData(0).length, colorMap.getByteData(0), colorMap.getByteData(1), colorMap.getByteData(2));
    }
    


    return colorModel;
  }
  
  protected byte findNearestEntry(int r, int g, int b) {
    byte[] red = colorMap.getByteData(0);
    byte[] green = colorMap.getByteData(1);
    byte[] blue = colorMap.getByteData(2);
    int index = 0;
    
    int dr = r - (red[0] & 0xFF);
    int dg = g - (green[0] & 0xFF);
    int db = b - (blue[0] & 0xFF);
    int minDistance = dr * dr + dg * dg + db * db;
    


    for (int i = 1; i < red.length; i++) {
      dr = r - (red[i] & 0xFF);
      int distance = dr * dr;
      if (distance <= minDistance)
      {
        dg = g - (green[i] & 0xFF);
        distance += dg * dg;
        
        if (distance <= minDistance)
        {
          db = b - (blue[i] & 0xFF);
          distance += db * db;
          if (distance < minDistance) {
            minDistance = distance;
            index = i;
          }
        } } }
    return (byte)index;
  }
}
