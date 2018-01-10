package javax.media.jai;

import java.awt.Point;
import java.awt.image.Raster;
import java.awt.image.TileObserver;
import java.awt.image.WritableRaster;
import java.awt.image.WritableRenderedImage;
import java.util.HashSet;
import java.util.Iterator;





















































































































































































































































































































public class SnapshotImage
  extends PlanarImage
  implements TileObserver
{
  private PlanarImage source;
  private Snapshot tail = null;
  

  private HashSet activeTiles = new HashSet();
  






  public SnapshotImage(PlanarImage source)
  {
    super(new ImageLayout(source), null, null);
    

    this.source = source;
    


    if ((source instanceof WritableRenderedImage)) {
      WritableRenderedImage wri = (WritableRenderedImage)source;
      wri.addTileObserver(this);
      
      Point[] pts = wri.getWritableTileIndices();
      if (pts != null) {
        int num = pts.length;
        for (int i = 0; i < num; i++)
        {
          Point p = pts[i];
          activeTiles.add(new Point(x, y));
        }
      }
    }
  }
  






  protected PlanarImage getTrueSource()
  {
    return source;
  }
  





  void setTail(Snapshot tail)
  {
    this.tail = tail;
  }
  




  Snapshot getTail()
  {
    return tail;
  }
  







  private Raster createTileCopy(int tileX, int tileY)
  {
    int x = tileXToX(tileX);
    int y = tileYToY(tileY);
    Point p = new Point(x, y);
    
    WritableRaster tile = RasterFactory.createWritableRaster(sampleModel, p);
    
    source.copyData(tile);
    return tile;
  }
  










  public PlanarImage createSnapshot()
  {
    if ((source instanceof WritableRenderedImage))
    {
      Snapshot snap = new Snapshot(this);
      

      Iterator iter = activeTiles.iterator();
      while (iter.hasNext()) {
        Point p = (Point)iter.next();
        

        Raster tile = createTileCopy(x, y);
        snap.addTile(tile, x, y);
      }
      

      if (tail == null) {
        tail = snap;
      } else {
        tail.setNext(snap);
        snap.setPrev(tail);
        tail = snap;
      }
      

      return new SnapshotProxy(snap);
    }
    return source;
  }
  












  public void tileUpdate(WritableRenderedImage source, int tileX, int tileY, boolean willBeWritable)
  {
    if (willBeWritable)
    {
      if ((tail != null) && (!tail.hasTile(tileX, tileY))) {
        tail.addTile(createTileCopy(tileX, tileY), tileX, tileY);
      }
      
      activeTiles.add(new Point(tileX, tileY));
    }
    else {
      activeTiles.remove(new Point(tileX, tileY));
    }
  }
  







  public Raster getTile(int tileX, int tileY)
  {
    return source.getTile(tileX, tileY);
  }
}
