package com.sun.media.jai.iterator;

import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import javax.media.jai.PlanarImage;
import javax.media.jai.iterator.RookIter;











































































public class RookIterFallback
  implements RookIter
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
  protected int endTileX;
  protected int startTileY;
  protected int endTileY;
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
  protected int firstX;
  protected int firstY;
  protected int lastX;
  protected int lastY;
  protected int x;
  protected int y;
  protected int localX;
  protected int localY;
  protected int b;
  protected DataBuffer dataBuffer = null;
  
  public RookIterFallback(RenderedImage im, Rectangle bounds) {
    this.im = im;
    this.bounds = bounds;
    
    sampleModel = im.getSampleModel();
    numBands = sampleModel.getNumBands();
    
    tileGridXOffset = im.getTileGridXOffset();
    tileGridYOffset = im.getTileGridYOffset();
    tileWidth = im.getTileWidth();
    tileHeight = im.getTileHeight();
    
    startTileX = PlanarImage.XToTileX(x, tileGridXOffset, tileWidth);
    

    endTileX = PlanarImage.XToTileX(x + width - 1, tileGridXOffset, tileWidth);
    

    startTileY = PlanarImage.YToTileY(y, tileGridYOffset, tileHeight);
    

    endTileY = PlanarImage.YToTileY(y + height - 1, tileGridYOffset, tileHeight);
    


    tileX = startTileX;
    tileY = startTileY;
    
    firstX = x;
    firstY = y;
    lastX = (x + width - 1);
    lastY = (y + height - 1);
    
    x = x;
    y = y;
    b = 0;
    
    setTileXBounds();
    setTileYBounds();
    setDataBuffer();
  }
  
  private final void setTileXBounds() {
    tileXStart = (tileX * tileWidth + tileGridXOffset);
    tileXEnd = (tileXStart + tileWidth - 1);
    localX = (x - tileXStart);
    
    prevXBoundary = Math.max(tileXStart, firstX);
    nextXBoundary = Math.min(tileXEnd, lastX);
  }
  
  private final void setTileYBounds() {
    tileYStart = (tileY * tileHeight + tileGridYOffset);
    tileYEnd = (tileYStart + tileHeight - 1);
    localY = (y - tileYStart);
    
    prevYBoundary = Math.max(tileYStart, firstY);
    nextYBoundary = Math.min(tileYEnd, lastY);
  }
  
  private final void setDataBuffer() {
    dataBuffer = im.getTile(tileX, tileY).getDataBuffer();
  }
  
  public void startLines() {
    y = firstY;
    localY = (y - tileYStart);
    tileY = startTileY;
    setTileYBounds();
    setDataBuffer();
  }
  
  public void endLines() {
    y = lastY;
    localY = (y - tileYStart);
    tileY = endTileY;
    setTileYBounds();
    setDataBuffer();
  }
  
  public void nextLine() {
    y += 1;
    localY += 1;
  }
  
  public void prevLine() {
    y -= 1;
    localY -= 1;
  }
  
  public void jumpLines(int num) {
    y += num;
    localY += num;
    
    if ((y < tileYStart) || (y > tileYEnd)) {
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
      localY -= tileHeight;
      prevYBoundary = Math.max(tileYStart, firstY);
      nextYBoundary = Math.min(tileYEnd, lastY);
      
      setDataBuffer();
      return false;
    }
    
    return false;
  }
  
  public boolean finishedLinesTop()
  {
    if (y < prevYBoundary) {
      if (y < firstY) {
        return true;
      }
      tileY -= 1;
      tileYStart -= tileHeight;
      tileYEnd -= tileHeight;
      localY += tileHeight;
      prevYBoundary = Math.max(tileYStart, firstY);
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
  
  public boolean prevLineDone() {
    prevLine();
    return finishedLinesTop();
  }
  
  public void startPixels() {
    x = firstX;
    localX = (x - tileXStart);
    tileX = startTileX;
    setTileXBounds();
    setDataBuffer();
  }
  
  public void endPixels() {
    x = lastX;
    tileX = endTileX;
    setTileXBounds();
    setDataBuffer();
  }
  
  public void nextPixel() {
    x += 1;
    localX += 1;
  }
  
  public void prevPixel() {
    x -= 1;
    localX -= 1;
  }
  
  public void jumpPixels(int num) {
    x += num;
    localX += num;
    
    if ((x < tileXStart) || (x > tileXEnd)) {
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
      localX -= tileWidth;
      prevXBoundary = Math.max(tileXStart, firstX);
      nextXBoundary = Math.min(tileXEnd, lastX);
      
      setDataBuffer();
      return false;
    }
    
    return false;
  }
  
  public boolean finishedPixelsLeft()
  {
    if (x < prevXBoundary) {
      if (x < firstX) {
        return true;
      }
      tileX -= 1;
      tileXStart -= tileWidth;
      tileXEnd -= tileWidth;
      localX += tileWidth;
      prevXBoundary = Math.max(tileXStart, firstX);
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
  
  public boolean prevPixelDone() {
    prevPixel();
    return finishedPixelsLeft();
  }
  
  public void startBands() {
    b = 0;
  }
  
  public void endBands() {
    b = (numBands - 1);
  }
  
  public void prevBand() {
    b -= 1;
  }
  
  public void nextBand() {
    b += 1;
  }
  
  public boolean prevBandDone() {
    return --b < 0;
  }
  
  public boolean nextBandDone() {
    return ++b >= numBands;
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
