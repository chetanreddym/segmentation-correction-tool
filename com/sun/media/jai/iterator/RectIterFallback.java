package com.sun.media.jai.iterator;

import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import javax.media.jai.PlanarImage;
import javax.media.jai.iterator.RectIter;


































































public class RectIterFallback
  implements RectIter
{
  protected RenderedImage im;
  protected Rectangle bounds;
  protected SampleModel sampleModel;
  protected int numBands;
  protected int tileWidth;
  protected int tileHeight;
  protected int tileGridXOffset;
  protected int tileGridYOffset;
  protected int startTileX;
  protected int startTileY;
  protected int tileXStart;
  protected int tileXEnd;
  protected int tileYStart;
  protected int tileYEnd;
  protected int prevXBoundary;
  protected int nextXBoundary;
  protected int prevYBoundary;
  protected int nextYBoundary;
  protected int tileX;
  protected int tileY;
  protected int lastX;
  protected int lastY;
  protected int x;
  protected int y;
  protected int localX;
  protected int localY;
  protected int sampleModelTranslateX = 0;
  

  protected int sampleModelTranslateY = 0;
  

  protected int b;
  

  protected DataBuffer dataBuffer = null;
  
  public RectIterFallback(RenderedImage im, Rectangle bounds) {
    this.im = im;
    this.bounds = bounds;
    
    sampleModel = im.getSampleModel();
    numBands = sampleModel.getNumBands();
    
    tileGridXOffset = im.getTileGridXOffset();
    tileGridYOffset = im.getTileGridYOffset();
    tileWidth = im.getTileWidth();
    tileHeight = im.getTileHeight();
    
    startTileX = PlanarImage.XToTileX(x, tileGridXOffset, tileWidth);
    

    startTileY = PlanarImage.YToTileY(y, tileGridYOffset, tileHeight);
    


    tileX = startTileX;
    tileY = startTileY;
    
    lastX = (x + width - 1);
    lastY = (y + height - 1);
    
    localX = (this.x = x);
    localY = (this.y = y);
    b = 0;
    
    setTileXBounds();
    setTileYBounds();
    setDataBuffer();
  }
  
  protected final void setTileXBounds() {
    tileXStart = (tileX * tileWidth + tileGridXOffset);
    tileXEnd = (tileXStart + tileWidth - 1);
    
    prevXBoundary = Math.max(tileXStart, bounds.x);
    nextXBoundary = Math.min(tileXEnd, lastX);
  }
  
  protected final void setTileYBounds() {
    tileYStart = (tileY * tileHeight + tileGridYOffset);
    tileYEnd = (tileYStart + tileHeight - 1);
    
    prevYBoundary = Math.max(tileYStart, bounds.y);
    nextYBoundary = Math.min(tileYEnd, lastY);
  }
  
  protected void setDataBuffer() {
    Raster tile = im.getTile(tileX, tileY);
    dataBuffer = tile.getDataBuffer();
    
    int newSampleModelTranslateX = tile.getSampleModelTranslateX();
    int newSampleModelTranslateY = tile.getSampleModelTranslateY();
    localX += sampleModelTranslateX - newSampleModelTranslateX;
    localY += sampleModelTranslateY - newSampleModelTranslateY;
    
    sampleModelTranslateX = newSampleModelTranslateX;
    sampleModelTranslateY = newSampleModelTranslateY;
  }
  
  public void startLines() {
    y = bounds.y;
    localY = (y - sampleModelTranslateY);
    tileY = startTileY;
    setTileYBounds();
    setDataBuffer();
  }
  
  public void nextLine() {
    y += 1;
    localY += 1;
  }
  
  public void jumpLines(int num) {
    int jumpY = y + num;
    if ((jumpY < bounds.y) || (jumpY > lastY))
    {
      throw new IndexOutOfBoundsException(JaiI18N.getString("RectIterFallback1"));
    }
    
    y = jumpY;
    localY += num;
    
    if ((y < prevYBoundary) || (y > nextYBoundary)) {
      tileY = PlanarImage.YToTileY(y, tileGridYOffset, tileHeight);
      

      setTileYBounds();
      setDataBuffer();
    }
  }
  
  public boolean finishedLines() {
    if (y > nextYBoundary) {
      if (y > lastY) {
        return true;
      }
      tileY += 1;
      tileYStart += tileHeight;
      tileYEnd += tileHeight;
      prevYBoundary = Math.max(tileYStart, bounds.y);
      nextYBoundary = Math.min(tileYEnd, lastY);
      
      setDataBuffer();
      return false;
    }
    
    return false;
  }
  
  public boolean nextLineDone()
  {
    nextLine();
    return finishedLines();
  }
  
  public void startPixels() {
    x = bounds.x;
    localX = (x - sampleModelTranslateX);
    tileX = startTileX;
    setTileXBounds();
    setDataBuffer();
  }
  
  public void nextPixel() {
    x += 1;
    localX += 1;
  }
  
  public void jumpPixels(int num) {
    int jumpX = x + num;
    if ((jumpX < bounds.x) || (jumpX > lastX))
    {
      throw new IndexOutOfBoundsException(JaiI18N.getString("RectIterFallback0"));
    }
    
    x = jumpX;
    localX += num;
    
    if ((x < prevXBoundary) || (x > nextXBoundary)) {
      tileX = PlanarImage.XToTileX(x, tileGridXOffset, tileWidth);
      

      setTileXBounds();
      setDataBuffer();
    }
  }
  
  public boolean finishedPixels() {
    if (x > nextXBoundary) {
      if (x > lastX) {
        return true;
      }
      tileX += 1;
      tileXStart += tileWidth;
      tileXEnd += tileWidth;
      prevXBoundary = Math.max(tileXStart, bounds.x);
      nextXBoundary = Math.min(tileXEnd, lastX);
      setDataBuffer();
      return false;
    }
    
    return false;
  }
  
  public boolean nextPixelDone()
  {
    nextPixel();
    return finishedPixels();
  }
  
  public void startBands() {
    b = 0;
  }
  
  public void nextBand() {
    b += 1;
  }
  
  public boolean nextBandDone() {
    nextBand();
    return finishedBands();
  }
  
  public boolean finishedBands() {
    return b >= numBands;
  }
  
  public int getSample() {
    return sampleModel.getSample(localX, localY, b, dataBuffer);
  }
  
  public int getSample(int b) {
    return sampleModel.getSample(localX, localY, b, dataBuffer);
  }
  
  public float getSampleFloat() {
    return sampleModel.getSampleFloat(localX, localY, b, dataBuffer);
  }
  
  public float getSampleFloat(int b) {
    return sampleModel.getSampleFloat(localX, localY, b, dataBuffer);
  }
  
  public double getSampleDouble() {
    return sampleModel.getSampleDouble(localX, localY, b, dataBuffer);
  }
  
  public double getSampleDouble(int b) {
    return sampleModel.getSampleDouble(localX, localY, b, dataBuffer);
  }
  
  public int[] getPixel(int[] iArray) {
    return sampleModel.getPixel(localX, localY, iArray, dataBuffer);
  }
  
  public float[] getPixel(float[] fArray) {
    return sampleModel.getPixel(localX, localY, fArray, dataBuffer);
  }
  
  public double[] getPixel(double[] dArray) {
    return sampleModel.getPixel(localX, localY, dArray, dataBuffer);
  }
}
