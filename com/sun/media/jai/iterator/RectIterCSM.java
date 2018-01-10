package com.sun.media.jai.iterator;

import java.awt.Rectangle;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import javax.media.jai.PlanarImage;














public abstract class RectIterCSM
  extends RectIterFallback
{
  protected int[] bankIndices;
  protected int scanlineStride;
  protected int pixelStride;
  protected int[] bandOffsets;
  protected int[] DBOffsets;
  protected int offset;
  protected int bandOffset;
  
  public RectIterCSM(RenderedImage im, Rectangle bounds)
  {
    super(im, bounds);
    
    ComponentSampleModel csm = (ComponentSampleModel)sampleModel;
    
    scanlineStride = csm.getScanlineStride();
    pixelStride = csm.getPixelStride();
    bankIndices = csm.getBankIndices();
    int[] bo = csm.getBandOffsets();
    
    bandOffsets = new int[numBands + 1];
    for (int i = 0; i < numBands; i++) {
      bandOffsets[i] = bo[i];
    }
    bandOffsets[numBands] = 0;
    
    DBOffsets = new int[numBands];
    
    offset = ((y - sampleModelTranslateY) * scanlineStride + (x - sampleModelTranslateX) * pixelStride);
    
    bandOffset = bandOffsets[0];
  }
  
  protected void dataBufferChanged() {}
  
  protected void adjustBandOffsets() {
    int[] newDBOffsets = dataBuffer.getOffsets();
    for (int i = 0; i < numBands; i++) {
      int bankNum = bankIndices[i];
      bandOffsets[i] += newDBOffsets[bankNum] - DBOffsets[bankNum];
    }
    DBOffsets = newDBOffsets;
  }
  
  protected void setDataBuffer() {
    Raster tile = im.getTile(tileX, tileY);
    dataBuffer = tile.getDataBuffer();
    dataBufferChanged();
    
    int newSampleModelTranslateX = tile.getSampleModelTranslateX();
    int newSampleModelTranslateY = tile.getSampleModelTranslateY();
    
    int deltaX = sampleModelTranslateX - newSampleModelTranslateX;
    int deltaY = sampleModelTranslateY - newSampleModelTranslateY;
    
    offset += deltaY * scanlineStride + deltaX * pixelStride;
    
    sampleModelTranslateX = newSampleModelTranslateX;
    sampleModelTranslateY = newSampleModelTranslateY;
  }
  
  public void startLines() {
    offset += (bounds.y - y) * scanlineStride;
    y = bounds.y;
    
    tileY = startTileY;
    setTileYBounds();
    setDataBuffer();
  }
  
  public void nextLine() {
    y += 1;
    offset += scanlineStride;
  }
  
  public void jumpLines(int num) {
    int jumpY = y + num;
    if ((jumpY < bounds.y) || (jumpY > lastY))
    {
      throw new IndexOutOfBoundsException(JaiI18N.getString("RectIterFallback1"));
    }
    
    y = jumpY;
    offset += num * scanlineStride;
    
    if ((y < prevYBoundary) || (y > nextYBoundary)) {
      tileY = PlanarImage.YToTileY(y, tileGridYOffset, tileHeight);
      

      setTileYBounds();
      setDataBuffer();
    }
  }
  
  public void startPixels() {
    offset += (bounds.x - x) * pixelStride;
    x = bounds.x;
    
    tileX = startTileX;
    setTileXBounds();
    setDataBuffer();
  }
  
  public void nextPixel() {
    x += 1;
    offset += pixelStride;
  }
  
  public void jumpPixels(int num) {
    int jumpX = x + num;
    if ((jumpX < bounds.x) || (jumpX > lastX))
    {
      throw new IndexOutOfBoundsException(JaiI18N.getString("RectIterFallback0"));
    }
    
    x = jumpX;
    offset += num * pixelStride;
    
    if ((x < prevXBoundary) || (x > nextXBoundary)) {
      tileX = PlanarImage.XToTileX(x, tileGridXOffset, tileWidth);
      


      setTileXBounds();
      setDataBuffer();
    }
  }
  
  public void startBands() {
    b = 0;
    bandOffset = bandOffsets[0];
  }
  
  public void nextBand() {
    b += 1;
    bandOffset = bandOffsets[b];
  }
}
