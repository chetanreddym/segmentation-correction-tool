package javax.media.jai;

import com.sun.media.jai.util.PropertyUtil;
import java.awt.Image;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;







































public abstract class CollectionImage
  implements ImageJAI, Collection
{
  protected Collection imageCollection;
  protected CollectionImageFactory imageFactory;
  private Boolean isFactorySet = Boolean.FALSE;
  





  protected PropertyChangeSupportJAI eventManager = null;
  





  protected WritablePropertySourceImpl properties = null;
  







  protected Set sinks;
  







  protected CollectionImage()
  {
    eventManager = new PropertyChangeSupportJAI(this);
    properties = new WritablePropertySourceImpl(null, null, eventManager);
  }
  








  public CollectionImage(Collection collection)
  {
    this();
    

    if (collection == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    imageCollection = collection;
  }
  

















  public Object get(int index)
  {
    if ((index < 0) || (index >= imageCollection.size())) {
      throw new IndexOutOfBoundsException();
    }
    
    if ((imageCollection instanceof List)) {
      return ((List)imageCollection).get(index);
    }
    return imageCollection.toArray((Object[])null)[index];
  }
  















  public void setImageFactory(CollectionImageFactory imageFactory)
  {
    synchronized (isFactorySet) {
      if (isFactorySet.booleanValue()) {
        throw new IllegalStateException();
      }
      this.imageFactory = imageFactory;
      isFactorySet = Boolean.TRUE;
    }
  }
  






  public CollectionImageFactory getImageFactory()
  {
    synchronized (isFactorySet) {
      return imageFactory;
    }
  }
  






  public synchronized boolean addSink(Object sink)
  {
    if (sink == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if (sinks == null) {
      sinks = new HashSet();
    }
    
    return sinks.add(new WeakReference(sink));
  }
  







  public synchronized boolean removeSink(Object sink)
  {
    if (sink == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if (sinks == null) {
      return false;
    }
    
    boolean result = false;
    Iterator it = sinks.iterator();
    while (it.hasNext()) {
      Object referent = ((WeakReference)it.next()).get();
      if (referent == sink)
      {
        it.remove();
        result = true;
      }
      else if (referent == null)
      {
        it.remove();
      }
    }
    
    return result;
  }
  






  public synchronized Set getSinks()
  {
    Set v = null;
    
    if ((sinks != null) && (sinks.size() > 0)) {
      v = new HashSet(sinks.size());
      
      Iterator it = sinks.iterator();
      while (it.hasNext()) {
        Object o = ((WeakReference)it.next()).get();
        
        if (o != null) {
          v.add(o);
        }
      }
      
      if (v.size() == 0) {
        v = null;
      }
    }
    
    return v;
  }
  




  public synchronized void removeSinks()
  {
    sinks = null;
  }
  









  public String[] getPropertyNames()
  {
    return properties.getPropertyNames();
  }
  
















  public String[] getPropertyNames(String prefix)
  {
    return PropertyUtil.getPropertyNames(getPropertyNames(), prefix);
  }
  












  public Class getPropertyClass(String name)
  {
    return properties.getPropertyClass(name);
  }
  






  public Object getProperty(String name)
  {
    return properties.getProperty(name);
  }
  




  /**
   * @deprecated
   */
  public Object getProperty(String name, Collection collection)
  {
    return Image.UndefinedProperty;
  }
  












  public void setProperty(String name, Object value)
  {
    properties.setProperty(name, value);
  }
  






  public void removeProperty(String name)
  {
    properties.removeProperty(name);
  }
  







  public void addPropertyChangeListener(PropertyChangeListener listener)
  {
    eventManager.addPropertyChangeListener(listener);
  }
  








  public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
  {
    eventManager.addPropertyChangeListener(propertyName, listener);
  }
  






  public void removePropertyChangeListener(PropertyChangeListener listener)
  {
    eventManager.removePropertyChangeListener(listener);
  }
  






  public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener)
  {
    eventManager.removePropertyChangeListener(propertyName, listener);
  }
  


  public int size()
  {
    return imageCollection.size();
  }
  



  public boolean isEmpty()
  {
    return imageCollection.isEmpty();
  }
  



  public boolean contains(Object o)
  {
    return imageCollection.contains(o);
  }
  



  public Iterator iterator()
  {
    return imageCollection.iterator();
  }
  



  public Object[] toArray()
  {
    return imageCollection.toArray();
  }
  







  public Object[] toArray(Object[] a)
  {
    return imageCollection.toArray(a);
  }
  





  public boolean add(Object o)
  {
    return imageCollection.add(o);
  }
  





  public boolean remove(Object o)
  {
    return imageCollection.remove(o);
  }
  



  public boolean containsAll(Collection c)
  {
    return imageCollection.containsAll(c);
  }
  






  public boolean addAll(Collection c)
  {
    return imageCollection.addAll(c);
  }
  






  public boolean removeAll(Collection c)
  {
    return imageCollection.removeAll(c);
  }
  






  public boolean retainAll(Collection c)
  {
    return imageCollection.retainAll(c);
  }
  
  public void clear()
  {
    imageCollection.clear();
  }
}
