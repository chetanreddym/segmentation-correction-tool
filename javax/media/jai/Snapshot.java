package javax.media.jai;

import java.awt.Point;
import java.awt.image.Raster;
import java.util.Enumeration;
import java.util.Hashtable;










































































































final class Snapshot
  extends PlanarImage
{
  SnapshotImage parent;
  Snapshot next;
  Snapshot prev;
  Hashtable tiles = new Hashtable();
  

  boolean disposed = false;
  





  Snapshot(SnapshotImage parent)
  {
    super(new ImageLayout(parent), null, null);
    this.parent = parent;
  }
  


















  public Raster getTile(int tileX, int tileY)
  {
    synchronized (parent)
    {



      TileCopy tc = (TileCopy)tiles.get(new Point(tileX, tileY));
      if (tc != null)
        return tile;
      if (next != null) {
        return next.getTile(tileX, tileY);
      }
      return parent.getTrueSource().getTile(tileX, tileY);
    }
  }
  






  void setNext(Snapshot next)
  {
    this.next = next;
  }
  





  void setPrev(Snapshot prev)
  {
    this.prev = prev;
  }
  







  boolean hasTile(int tileX, int tileY)
  {
    TileCopy tc = (TileCopy)tiles.get(new Point(tileX, tileY));
    return tc != null;
  }
  







  void addTile(Raster tile, int tileX, int tileY)
  {
    TileCopy tc = new TileCopy(tile, tileX, tileY);
    tiles.put(new Point(tileX, tileY), tc);
  }
  

  public void dispose()
  {
    synchronized (parent)
    {
      if (disposed) {
        return;
      }
      disposed = true;
      

      if (parent.getTail() == this) {
        parent.setTail(prev);
      }
      

      if (prev != null) {
        prev.setNext(next);
      }
      if (next != null) {
        next.setPrev(prev);
      }
      

      if (prev != null)
      {
        Enumeration enumeration = tiles.elements();
        while (enumeration.hasMoreElements()) {
          TileCopy tc = (TileCopy)enumeration.nextElement();
          if (!prev.hasTile(tileX, tileY)) {
            prev.addTile(tile, tileX, tileY);
          }
        }
      }
      

      parent = null;
      next = (this.prev = null);
      tiles = null;
    }
  }
}
