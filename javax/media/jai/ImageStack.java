package javax.media.jai;

import java.util.Collection;
import java.util.Iterator;





























/**
 * @deprecated
 */
public abstract class ImageStack
  extends CollectionImage
{
  protected ImageStack() {}
  
  public ImageStack(Collection images)
  {
    super(images);
  }
  






  public PlanarImage getImage(Object c)
  {
    if (c != null) {
      Iterator iter = iterator();
      
      while (iter.hasNext()) {
        CoordinateImage ci = (CoordinateImage)iter.next();
        if (coordinate.equals(c)) {
          return image;
        }
      }
    }
    
    return null;
  }
  






  public Object getCoordinate(PlanarImage pi)
  {
    if (pi != null) {
      Iterator iter = iterator();
      
      while (iter.hasNext()) {
        CoordinateImage ci = (CoordinateImage)iter.next();
        if (image.equals(pi)) {
          return coordinate;
        }
      }
    }
    
    return null;
  }
  







  public boolean add(Object o)
  {
    if ((o != null) && ((o instanceof CoordinateImage))) {
      return super.add(o);
    }
    return false;
  }
  








  public boolean remove(PlanarImage pi)
  {
    if (pi != null) {
      Iterator iter = iterator();
      
      while (iter.hasNext()) {
        CoordinateImage ci = (CoordinateImage)iter.next();
        if (image.equals(pi)) {
          return super.remove(ci);
        }
      }
    }
    
    return false;
  }
  







  public boolean remove(Object c)
  {
    if (c != null) {
      Iterator iter = iterator();
      
      while (iter.hasNext()) {
        CoordinateImage ci = (CoordinateImage)iter.next();
        if (coordinate.equals(c)) {
          return super.remove(ci);
        }
      }
    }
    
    return false;
  }
}
