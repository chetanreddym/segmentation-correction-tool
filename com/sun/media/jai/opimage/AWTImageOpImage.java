package com.sun.media.jai.opimage;

import java.awt.Canvas;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.PixelGrabber;
import java.awt.image.Raster;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ImageLayout;
import javax.media.jai.PlanarImage;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFactory;
import javax.media.jai.RasterFormatTag;
import javax.media.jai.SourcelessOpImage;













































final class AWTImageOpImage
  extends SourcelessOpImage
{
  private int[] pixels;
  private RasterFormatTag rasterFormatTag = null;
  

  private static final ImageLayout layoutHelper(ImageLayout layout, Image image)
  {
    MediaTracker tracker = new MediaTracker(new Canvas());
    tracker.addImage(image, 0);
    try {
      tracker.waitForID(0);
    } catch (InterruptedException e) {
      e.printStackTrace();
      throw new RuntimeException(JaiI18N.getString("AWTImageOpImage0"));
    }
    if (tracker.isErrorID(0)) {
      throw new RuntimeException(JaiI18N.getString("AWTImageOpImage1"));
    }
    tracker.removeImage(image);
    

    if (layout == null) { layout = new ImageLayout();
    }
    
    layout.setMinX(0);
    layout.setMinY(0);
    layout.setWidth(image.getWidth(null));
    layout.setHeight(image.getHeight(null));
    

    if (!layout.isValid(64)) {
      layout.setTileWidth(layout.getWidth(null));
    }
    if (!layout.isValid(128)) {
      layout.setTileHeight(layout.getHeight(null));
    }
    


    if ((layout.getTileWidth(null) == layout.getWidth(null)) && (layout.getTileHeight(null) == layout.getHeight(null)))
    {

      layout.setTileGridXOffset(layout.getMinX(null));
      layout.setTileGridYOffset(layout.getMinY(null));
      
      int[] bitMasks = { 16711680, 65280, 255 };
      layout.setSampleModel(new SinglePixelPackedSampleModel(3, layout.getWidth(null), layout.getHeight(null), bitMasks));

    }
    else
    {

      layout.setSampleModel(RasterFactory.createPixelInterleavedSampleModel(0, layout.getTileWidth(null), layout.getTileHeight(null), 3));
    }
    




    layout.setColorModel(PlanarImage.createColorModel(layout.getSampleModel(null)));
    
    return layout;
  }
  








  public AWTImageOpImage(Map config, ImageLayout layout, Image image)
  {
    super(layout = layoutHelper(layout, image), config, layout.getSampleModel(null), layout.getMinX(null), layout.getMinY(null), layout.getWidth(null), layout.getHeight(null));
    




    if ((getTileWidth() != getWidth()) || (getTileHeight() != getHeight())) {
      rasterFormatTag = new RasterFormatTag(getSampleModel(), 0);
    }
    



    pixels = new int[width * height];
    PixelGrabber grabber = new PixelGrabber(image, 0, 0, width, height, pixels, 0, width);
    try
    {
      if (!grabber.grabPixels()) {
        if ((grabber.getStatus() & 0x80) != 0) {
          throw new RuntimeException(JaiI18N.getString("AWTImageOpImage2"));
        }
        throw new RuntimeException(grabber.getStatus() + JaiI18N.getString("AWTImageOpImage3"));
      }
    }
    catch (InterruptedException e) {
      e.printStackTrace();
      throw new RuntimeException(JaiI18N.getString("AWTImageOpImage4"));
    }
  }
  
  public Raster computeTile(int tileX, int tileY) {
    if ((getTileWidth() == getWidth()) && (getTileHeight() == getHeight())) {
      DataBuffer dataBuffer = new DataBufferInt(pixels, pixels.length);
      return Raster.createWritableRaster(getSampleModel(), dataBuffer, new Point(tileXToX(tileX), tileYToY(tileY)));
    }
    



    return super.computeTile(tileX, tileY);
  }
  

  protected void computeRect(PlanarImage[] sources, WritableRaster dest, Rectangle destRect)
  {
    RasterAccessor dst = new RasterAccessor(dest, destRect, rasterFormatTag, null);
    

    int dwidth = dst.getWidth();
    int dheight = dst.getHeight();
    
    int lineStride = dst.getScanlineStride();
    int pixelStride = dst.getPixelStride();
    
    int lineOffset0 = dst.getBandOffset(0);
    int lineOffset1 = dst.getBandOffset(1);
    int lineOffset2 = dst.getBandOffset(2);
    
    byte[] data = dst.getByteDataArray(0);
    
    int offset = (y - minY) * width + (x - minX);
    
    for (int h = 0; h < dheight; h++) {
      int pixelOffset0 = lineOffset0;
      int pixelOffset1 = lineOffset1;
      int pixelOffset2 = lineOffset2;
      
      lineOffset0 += lineStride;
      lineOffset1 += lineStride;
      lineOffset2 += lineStride;
      
      int i = offset;
      offset += width;
      
      for (int w = 0; w < dwidth; w++) {
        data[pixelOffset0] = ((byte)(pixels[i] >> 16 & 0xFF));
        data[pixelOffset1] = ((byte)(pixels[i] >> 8 & 0xFF));
        data[pixelOffset2] = ((byte)(pixels[i] & 0xFF));
        
        pixelOffset0 += pixelStride;
        pixelOffset1 += pixelStride;
        pixelOffset2 += pixelStride;
        i++;
      }
    }
  }
}
