package javax.media.jai;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;






































public class AttributedImageCollection
  extends CollectionImage
{
  protected AttributedImageCollection() {}
  
  public AttributedImageCollection(Collection images)
  {
    if (images == null) {
      throw new IllegalArgumentException(JaiI18N.getString("AttributedImageCollection0"));
    }
    
    try
    {
      imageCollection = ((Collection)images.getClass().newInstance());
    }
    catch (Exception e) {
      imageCollection = new ArrayList(images.size());
    }
    

    Iterator iter = images.iterator();
    while (iter.hasNext()) {
      Object o = iter.next();
      
      if (((o instanceof AttributedImage)) && (!imageCollection.contains(o)))
      {
        imageCollection.add(o);
      }
    }
  }
  







  public Set getAll(Object attribute)
  {
    if (attribute == null) {
      return (Set)imageCollection;
    }
    HashSet set = null;
    Iterator iter = iterator();
    
    while (iter.hasNext()) {
      AttributedImage ai = (AttributedImage)iter.next();
      
      if (attribute.equals(ai.getAttribute())) {
        if (set == null) {
          set = new HashSet();
        }
        
        set.add(ai);
      }
    }
    
    return set;
  }
  







  public Set getAll(PlanarImage image)
  {
    if (image == null) {
      return (Set)imageCollection;
    }
    HashSet set = null;
    Iterator iter = iterator();
    
    while (iter.hasNext()) {
      AttributedImage ai = (AttributedImage)iter.next();
      
      if (image.equals(ai.getImage())) {
        if (set == null) {
          set = new HashSet();
        }
        
        set.add(ai);
      }
    }
    
    return set;
  }
  








  public Set removeAll(Object attribute)
  {
    if (attribute == null) {
      return null;
    }
    Iterator iter = iterator();
    Set removed = null;
    
    while (iter.hasNext()) {
      AttributedImage ai = (AttributedImage)iter.next();
      
      if (attribute.equals(ai.getAttribute())) {
        iter.remove();
        if (removed == null) {
          removed = new HashSet();
        }
        removed.add(ai);
      }
    }
    
    return removed;
  }
  







  public Set removeAll(PlanarImage image)
  {
    if (image == null) {
      return null;
    }
    Iterator iter = iterator();
    Set removed = null;
    
    while (iter.hasNext()) {
      AttributedImage ai = (AttributedImage)iter.next();
      
      if (image.equals(ai.getImage())) {
        iter.remove();
        if (removed == null) {
          removed = new HashSet();
        }
        removed.add(ai);
      }
    }
    
    return removed;
  }
  














  public boolean add(Object o)
  {
    if ((o == null) || (!(o instanceof AttributedImage))) {
      throw new IllegalArgumentException(JaiI18N.getString("AttributedImageCollection1"));
    }
    

    if (imageCollection.contains(o)) {
      return false;
    }
    
    return imageCollection.add(o);
  }
  






  public boolean addAll(Collection c)
  {
    if (c == null) { return false;
    }
    
    Iterator iter = c.iterator();
    boolean flag = false;
    
    while (iter.hasNext()) {
      Object o = iter.next();
      

      if (((o instanceof AttributedImage)) && 
        (!imageCollection.contains(o)) && (imageCollection.add(o)))
      {
        flag = true;
      }
    }
    

    return flag;
  }
  





  public AttributedImage getAttributedImage(PlanarImage image)
  {
    if (image == null) {
      return null;
    }
    Iterator iter = iterator();
    
    while (iter.hasNext()) {
      AttributedImage ai = (AttributedImage)iter.next();
      
      if (image.equals(ai.getImage())) {
        return ai;
      }
    }
    

    return null;
  }
  





  public AttributedImage getAttributedImage(Object attribute)
  {
    if (attribute == null) {
      return null;
    }
    Iterator iter = iterator();
    
    while (iter.hasNext()) {
      AttributedImage ai = (AttributedImage)iter.next();
      
      if (attribute.equals(ai.getAttribute())) {
        return ai;
      }
    }
    

    return null;
  }
}
