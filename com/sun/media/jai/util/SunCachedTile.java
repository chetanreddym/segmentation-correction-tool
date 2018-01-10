package com.sun.media.jai.util;

import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.lang.ref.WeakReference;
import java.math.BigInteger;
import javax.media.jai.CachedTile;
import javax.media.jai.PlanarImage;
import javax.media.jai.remote.SerializableRenderedImage;

































final class SunCachedTile
  implements CachedTile
{
  Raster tile;
  WeakReference owner;
  int tileX;
  int tileY;
  Object tileCacheMetric;
  long timeStamp;
  Object key;
  long memorySize;
  SunCachedTile previous;
  SunCachedTile next;
  int action = 0;
  









  SunCachedTile(RenderedImage owner, int tileX, int tileY, Raster tile, Object tileCacheMetric)
  {
    this.owner = new WeakReference(owner);
    this.tile = tile;
    this.tileX = tileX;
    this.tileY = tileY;
    
    this.tileCacheMetric = tileCacheMetric;
    
    key = hashKey(owner, tileX, tileY);
    

    DataBuffer db = tile.getDataBuffer();
    memorySize = (DataBuffer.getDataTypeSize(db.getDataType()) / 8L * db.getSize() * db.getNumBanks());
  }
  












  static Object hashKey(RenderedImage owner, int tileX, int tileY)
  {
    long idx = tileY * owner.getNumXTiles() + tileX;
    
    BigInteger imageID = null;
    if ((owner instanceof PlanarImage)) {
      imageID = (BigInteger)((PlanarImage)owner).getImageID();
    } else if ((owner instanceof SerializableRenderedImage)) {
      imageID = (BigInteger)((SerializableRenderedImage)owner).getImageID();
    }
    if (imageID != null) {
      byte[] buf = imageID.toByteArray();
      int length = buf.length;
      byte[] buf1 = new byte[length + 8];
      System.arraycopy(buf, 0, buf1, 0, length);
      int i = 7; for (int j = 0; i >= 0; j += 8) {
        buf1[(length++)] = ((byte)(int)(idx >> j));i--; }
      return new BigInteger(buf1);
    }
    
    idx &= 0xFFFFFFFF;
    return new Long(owner.hashCode() << 32 | idx);
  }
  





















  public String toString()
  {
    RenderedImage o = getOwner();
    String ostring = o == null ? "null" : o.toString();
    
    Raster t = getTile();
    String tstring = t == null ? "null" : t.toString();
    
    return getClass().getName() + "@" + Integer.toHexString(hashCode()) + ": owner = " + ostring + " tileX = " + Integer.toString(tileX) + " tileY = " + Integer.toString(tileY) + " tile = " + tstring + " key = " + ((key instanceof Long) ? Long.toHexString(((Long)key).longValue()) : key.toString()) + " memorySize = " + Long.toString(memorySize) + " timeStamp = " + Long.toString(timeStamp);
  }
  







  public Raster getTile()
  {
    return tile;
  }
  
  public RenderedImage getOwner()
  {
    return (RenderedImage)owner.get();
  }
  
  public long getTileTimeStamp()
  {
    return timeStamp;
  }
  
  public Object getTileCacheMetric()
  {
    return tileCacheMetric;
  }
  
  public long getTileSize()
  {
    return memorySize;
  }
  


  public int getAction()
  {
    return action;
  }
}
