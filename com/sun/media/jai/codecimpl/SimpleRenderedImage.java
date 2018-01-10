package com.sun.media.jai.codecimpl;

import com.sun.media.jai.codecimpl.util.RasterFactory;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;


































public abstract class SimpleRenderedImage
  implements RenderedImage
{
  protected int minX;
  protected int minY;
  protected int width;
  protected int height;
  protected int tileWidth;
  protected int tileHeight;
  protected int tileGridXOffset = 0;
  

  protected int tileGridYOffset = 0;
  

  protected SampleModel sampleModel = null;
  

  protected ColorModel colorModel = null;
  

  protected Vector sources = new Vector();
  

  protected Hashtable properties = new Hashtable();
  
  public SimpleRenderedImage() {}
  
  public int getMinX()
  {
    return minX;
  }
  





  public final int getMaxX()
  {
    return getMinX() + getWidth();
  }
  
  public int getMinY()
  {
    return minY;
  }
  





  public final int getMaxY()
  {
    return getMinY() + getHeight();
  }
  
  public int getWidth()
  {
    return width;
  }
  
  public int getHeight()
  {
    return height;
  }
  
  public Rectangle getBounds()
  {
    return new Rectangle(getMinX(), getMinY(), getWidth(), getHeight());
  }
  

  public int getTileWidth()
  {
    return tileWidth;
  }
  
  public int getTileHeight()
  {
    return tileHeight;
  }
  


  public int getTileGridXOffset()
  {
    return tileGridXOffset;
  }
  


  public int getTileGridYOffset()
  {
    return tileGridYOffset;
  }
  




  public int getMinTileX()
  {
    return XToTileX(getMinX());
  }
  




  public int getMaxTileX()
  {
    return XToTileX(getMaxX() - 1);
  }
  





  public int getNumXTiles()
  {
    return getMaxTileX() - getMinTileX() + 1;
  }
  




  public int getMinTileY()
  {
    return YToTileY(getMinY());
  }
  




  public int getMaxTileY()
  {
    return YToTileY(getMaxY() - 1);
  }
  





  public int getNumYTiles()
  {
    return getMaxTileY() - getMinTileY() + 1;
  }
  
  public SampleModel getSampleModel()
  {
    return sampleModel;
  }
  
  public ColorModel getColorModel()
  {
    return colorModel;
  }
  









  public Object getProperty(String name)
  {
    name = name.toLowerCase();
    Object value = properties.get(name);
    return value != null ? value : Image.UndefinedProperty;
  }
  







  public String[] getPropertyNames()
  {
    String[] names = null;
    
    if (properties.size() > 0) {
      names = new String[properties.size()];
      int index = 0;
      
      Enumeration e = properties.keys();
      while (e.hasMoreElements()) {
        String name = (String)e.nextElement();
        names[(index++)] = name;
      }
    }
    
    return names;
  }
  












  public String[] getPropertyNames(String prefix)
  {
    String[] propertyNames = getPropertyNames();
    if (propertyNames == null) {
      return null;
    }
    
    prefix = prefix.toLowerCase();
    
    Vector names = new Vector();
    for (int i = 0; i < propertyNames.length; i++) {
      if (propertyNames[i].startsWith(prefix)) {
        names.addElement(propertyNames[i]);
      }
    }
    
    if (names.size() == 0) {
      return null;
    }
    

    String[] prefixNames = new String[names.size()];
    int count = 0;
    for (Iterator it = names.iterator(); it.hasNext();) {
      prefixNames[(count++)] = ((String)it.next());
    }
    
    return prefixNames;
  }
  






  public static int XToTileX(int x, int tileGridXOffset, int tileWidth)
  {
    x -= tileGridXOffset;
    if (x < 0) {
      x += 1 - tileWidth;
    }
    return x / tileWidth;
  }
  




  public static int YToTileY(int y, int tileGridYOffset, int tileHeight)
  {
    y -= tileGridYOffset;
    if (y < 0) {
      y += 1 - tileHeight;
    }
    return y / tileHeight;
  }
  







  public int XToTileX(int x)
  {
    return XToTileX(x, getTileGridXOffset(), getTileWidth());
  }
  







  public int YToTileY(int y)
  {
    return YToTileY(y, getTileGridYOffset(), getTileHeight());
  }
  




  public static int tileXToX(int tx, int tileGridXOffset, int tileWidth)
  {
    return tx * tileWidth + tileGridXOffset;
  }
  




  public static int tileYToY(int ty, int tileGridYOffset, int tileHeight)
  {
    return ty * tileHeight + tileGridYOffset;
  }
  







  public int tileXToX(int tx)
  {
    return tx * tileWidth + tileGridXOffset;
  }
  







  public int tileYToY(int ty)
  {
    return ty * tileHeight + tileGridYOffset;
  }
  
  public Vector getSources() {
    return null;
  }
  















  public Raster getData()
  {
    Rectangle rect = new Rectangle(getMinX(), getMinY(), getWidth(), getHeight());
    
    return getData(rect);
  }
  

















  public Raster getData(Rectangle bounds)
  {
    Rectangle imageBounds = getBounds();
    

    if (bounds == null) {
      bounds = imageBounds;
    } else if (!bounds.intersects(imageBounds)) {
      throw new IllegalArgumentException(JaiI18N.getString("SimpleRenderedImage0"));
    }
    

    int startX = XToTileX(x);
    int startY = YToTileY(y);
    int endX = XToTileX(x + width - 1);
    int endY = YToTileY(y + height - 1);
    


    if ((startX == endX) && (startY == endY)) {
      Raster tile = getTile(startX, startY);
      return tile.createChild(x, y, width, height, x, y, null);
    }
    



    if (!imageBounds.contains(bounds)) {
      Rectangle xsect = bounds.intersection(imageBounds);
      startX = XToTileX(x);
      startY = YToTileY(y);
      endX = XToTileX(x + width - 1);
      endY = YToTileY(y + height - 1);
    }
    

    SampleModel sm = sampleModel.createCompatibleSampleModel(width, height);
    



    WritableRaster dest = RasterFactory.createWritableRaster(sm, bounds.getLocation());
    


    for (int j = startY; j <= endY; j++) {
      for (int i = startX; i <= endX; i++)
      {
        Raster tile = getTile(i, j);
        


        Rectangle tileRect = tile.getBounds();
        Rectangle intersectRect = bounds.intersection(tile.getBounds());
        
        Raster liveRaster = tile.createChild(x, y, width, height, x, y, null);
        







        dest.setRect(liveRaster);
      }
    }
    
    return dest;
  }
  


















  public WritableRaster copyData(WritableRaster dest)
  {
    Rectangle imageBounds = getBounds();
    
    Rectangle bounds;
    if (dest == null)
    {
      Rectangle bounds = imageBounds;
      Point p = new Point(minX, minY);
      SampleModel sm = sampleModel.createCompatibleSampleModel(width, height);
      
      dest = RasterFactory.createWritableRaster(sm, p);
    } else {
      bounds = dest.getBounds();
    }
    


    Rectangle xsect = imageBounds.contains(bounds) ? bounds : bounds.intersection(imageBounds);
    
    int startX = XToTileX(x);
    int startY = YToTileY(y);
    int endX = XToTileX(x + width - 1);
    int endY = YToTileY(y + height - 1);
    

    for (int j = startY; j <= endY; j++) {
      for (int i = startX; i <= endX; i++)
      {
        Raster tile = getTile(i, j);
        


        Rectangle tileRect = tile.getBounds();
        Rectangle intersectRect = bounds.intersection(tile.getBounds());
        
        Raster liveRaster = tile.createChild(x, y, width, height, x, y, null);
        







        dest.setRect(liveRaster);
      }
    }
    
    return dest;
  }
}
