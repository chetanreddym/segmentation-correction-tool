package javax.media.jai;

import java.util.Collection;
import java.util.Iterator;
































/**
 * @deprecated
 */
public class ImageSequence
  extends CollectionImage
{
  protected ImageSequence() {}
  
  public ImageSequence(Collection images)
  {
    super(images);
  }
  



  public PlanarImage getImage(float ts)
  {
    Iterator iter = iterator();
    
    while (iter.hasNext()) {
      SequentialImage si = (SequentialImage)iter.next();
      if (timeStamp == ts) {
        return image;
      }
    }
    
    return null;
  }
  




  public PlanarImage getImage(Object cp)
  {
    if (cp != null) {
      Iterator iter = iterator();
      
      while (iter.hasNext()) {
        SequentialImage si = (SequentialImage)iter.next();
        if (cameraPosition.equals(cp)) {
          return image;
        }
      }
    }
    
    return null;
  }
  




  public float getTimeStamp(PlanarImage pi)
  {
    if (pi != null) {
      Iterator iter = iterator();
      
      while (iter.hasNext()) {
        SequentialImage si = (SequentialImage)iter.next();
        if (image.equals(pi)) {
          return timeStamp;
        }
      }
    }
    
    return -3.4028235E38F;
  }
  




  public Object getCameraPosition(PlanarImage pi)
  {
    if (pi != null) {
      Iterator iter = iterator();
      
      while (iter.hasNext()) {
        SequentialImage si = (SequentialImage)iter.next();
        if (image.equals(pi)) {
          return cameraPosition;
        }
      }
    }
    
    return null;
  }
  







  public boolean add(Object o)
  {
    if ((o != null) && ((o instanceof SequentialImage))) {
      return super.add(o);
    }
    return false;
  }
  








  public boolean remove(PlanarImage pi)
  {
    if (pi != null) {
      Iterator iter = iterator();
      
      while (iter.hasNext()) {
        SequentialImage si = (SequentialImage)iter.next();
        if (image.equals(pi)) {
          return super.remove(si);
        }
      }
    }
    
    return false;
  }
  







  public boolean remove(float ts)
  {
    Iterator iter = iterator();
    
    while (iter.hasNext()) {
      SequentialImage si = (SequentialImage)iter.next();
      if (timeStamp == ts) {
        return super.remove(si);
      }
    }
    
    return false;
  }
  







  public boolean remove(Object cp)
  {
    if (cp != null) {
      Iterator iter = iterator();
      
      while (iter.hasNext()) {
        SequentialImage si = (SequentialImage)iter.next();
        if (cameraPosition.equals(cp)) {
          return super.remove(si);
        }
      }
    }
    
    return false;
  }
}
