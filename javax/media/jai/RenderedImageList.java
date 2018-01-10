package javax.media.jai;

import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;









































































public class RenderedImageList
  extends CollectionImage
  implements List, RenderedImage, Serializable
{
  protected RenderedImageList() {}
  
  public RenderedImageList(List renderedImageList)
  {
    if (renderedImageList == null) {
      throw new IllegalArgumentException(JaiI18N.getString("RenderedImageList0"));
    }
    
    if (renderedImageList.isEmpty()) {
      throw new IllegalArgumentException(JaiI18N.getString("RenderedImageList1"));
    }
    
    Iterator iter = renderedImageList.iterator();
    imageCollection = new Vector();
    
    while (iter.hasNext()) {
      Object item = iter.next();
      
      if ((item instanceof RenderedImage)) {
        imageCollection.add(item);
      } else {
        throw new IllegalArgumentException(JaiI18N.getString("RenderedImageList2"));
      }
    }
  }
  




  private List getList()
  {
    return (List)imageCollection;
  }
  




  public RenderedImage getPrimaryImage()
  {
    return (RenderedImage)getList().get(0);
  }
  




  public int getMinX()
  {
    return ((RenderedImage)getList().get(0)).getMinX();
  }
  



  public int getMinY()
  {
    return ((RenderedImage)getList().get(0)).getMinY();
  }
  



  public int getWidth()
  {
    return ((RenderedImage)getList().get(0)).getWidth();
  }
  



  public int getHeight()
  {
    return ((RenderedImage)getList().get(0)).getHeight();
  }
  



  public int getTileWidth()
  {
    return ((RenderedImage)getList().get(0)).getTileWidth();
  }
  



  public int getTileHeight()
  {
    return ((RenderedImage)getList().get(0)).getTileHeight();
  }
  




  public int getTileGridXOffset()
  {
    return ((RenderedImage)getList().get(0)).getTileGridXOffset();
  }
  




  public int getTileGridYOffset()
  {
    return ((RenderedImage)getList().get(0)).getTileGridYOffset();
  }
  




  public int getMinTileX()
  {
    return ((RenderedImage)getList().get(0)).getMinTileX();
  }
  




  public int getNumXTiles()
  {
    return ((RenderedImage)getList().get(0)).getNumXTiles();
  }
  




  public int getMinTileY()
  {
    return ((RenderedImage)getList().get(0)).getMinTileY();
  }
  




  public int getNumYTiles()
  {
    return ((RenderedImage)getList().get(0)).getNumYTiles();
  }
  



  public SampleModel getSampleModel()
  {
    return ((RenderedImage)getList().get(0)).getSampleModel();
  }
  



  public ColorModel getColorModel()
  {
    return ((RenderedImage)getList().get(0)).getColorModel();
  }
  













  public Object getProperty(String name)
  {
    if (name == null) {
      throw new IllegalArgumentException(JaiI18N.getString("RenderedImageList0"));
    }
    
    return ((RenderedImage)getList().get(0)).getProperty(name);
  }
  








  public String[] getPropertyNames()
  {
    return ((RenderedImage)getList().get(0)).getPropertyNames();
  }
  



  public Vector getSources()
  {
    return ((RenderedImage)getList().get(0)).getSources();
  }
  









  public Raster getTile(int tileX, int tileY)
  {
    return ((RenderedImage)getList().get(0)).getTile(tileX, tileY);
  }
  







  public Raster getData()
  {
    return ((RenderedImage)getList().get(0)).getData();
  }
  








  public Raster getData(Rectangle bounds)
  {
    return ((RenderedImage)getList().get(0)).getData(bounds);
  }
  

















  public WritableRaster copyData(WritableRaster dest)
  {
    return ((RenderedImage)getList().get(0)).copyData(dest);
  }
  









  public void add(int index, Object element)
  {
    if ((element instanceof RenderedImage)) {
      if ((index >= 0) && (index <= imageCollection.size())) {
        ((List)imageCollection).add(index, element);
      } else {
        throw new IndexOutOfBoundsException(JaiI18N.getString("RenderedImageList3"));
      }
    } else {
      throw new IllegalArgumentException(JaiI18N.getString("RenderedImageList2"));
    }
  }
  










  public boolean addAll(int index, Collection c)
  {
    if ((index < 0) || (index > imageCollection.size())) {
      throw new IndexOutOfBoundsException(JaiI18N.getString("RenderedImageList3"));
    }
    

    Vector temp = null;
    Iterator iter = c.iterator();
    
    while (iter.hasNext()) {
      Object o = iter.next();
      
      if ((o instanceof RenderedImage)) {
        if (temp == null) {
          temp = new Vector();
        }
        
        temp.add(o);
      }
    }
    
    return ((List)imageCollection).addAll(index, temp);
  }
  









  public Object get(int index)
  {
    if ((index < 0) || (index >= imageCollection.size())) {
      throw new IndexOutOfBoundsException(JaiI18N.getString("RenderedImageList3"));
    }
    
    return ((List)imageCollection).get(index);
  }
  

  public int indexOf(Object o)
  {
    return ((List)imageCollection).indexOf(o);
  }
  

  public int lastIndexOf(Object o)
  {
    return ((List)imageCollection).lastIndexOf(o);
  }
  
  public ListIterator listIterator() {
    return ((List)imageCollection).listIterator();
  }
  
  public ListIterator listIterator(int index) {
    return ((List)imageCollection).listIterator(index);
  }
  
  public Object remove(int index) {
    return ((List)imageCollection).remove(index);
  }
  
  public Object set(int index, Object element) {
    if ((element instanceof RenderedImage)) {
      return ((List)imageCollection).set(index, element);
    }
    
    throw new IllegalArgumentException(JaiI18N.getString("RenderedImageList2"));
  }
  
  public List subList(int fromIndex, int toIndex) {
    return ((List)imageCollection).subList(fromIndex, toIndex);
  }
  










  public boolean add(Object o)
  {
    if (o == null) {
      throw new IllegalArgumentException(JaiI18N.getString("RenderedImageList0"));
    }
    
    if ((o instanceof RenderedImage)) {
      imageCollection.add(o);
      return true;
    }
    throw new IllegalArgumentException(JaiI18N.getString("RenderedImageList2"));
  }
  







  public boolean addAll(Collection c)
  {
    Iterator iter = c.iterator();
    boolean status = false;
    
    while (iter.hasNext()) {
      Object o = iter.next();
      
      if ((o instanceof RenderedImage)) {
        imageCollection.add(o);
        status = true;
      }
    }
    
    return status;
  }
}
