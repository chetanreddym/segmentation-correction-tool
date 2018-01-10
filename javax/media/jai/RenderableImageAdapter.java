package javax.media.jai;

import com.sun.media.jai.util.PropertyUtil;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import java.beans.PropertyChangeListener;
import java.util.Vector;


























public final class RenderableImageAdapter
  implements RenderableImage, WritablePropertySource
{
  private RenderableImage im;
  private PropertyChangeSupportJAI eventManager = null;
  

  private WritablePropertySourceImpl properties = null;
  











  public static RenderableImageAdapter wrapRenderableImage(RenderableImage im)
  {
    if (im == null)
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    if ((im instanceof RenderableImageAdapter)) {
      return (RenderableImageAdapter)im;
    }
    return new RenderableImageAdapter(im);
  }
  





  public RenderableImageAdapter(RenderableImage im)
  {
    if (im == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    this.im = im;
    eventManager = new PropertyChangeSupportJAI(this);
    properties = new WritablePropertySourceImpl(null, null, eventManager);
  }
  





  public final RenderableImage getWrappedImage()
  {
    return im;
  }
  







  public final Vector getSources()
  {
    return im.getSources();
  }
  











  public final Object getProperty(String name)
  {
    Object property = properties.getProperty(name);
    

    if (property == Image.UndefinedProperty) {
      property = im.getProperty(name);
    }
    
    return property;
  }
  












  public Class getPropertyClass(String name)
  {
    Class propClass = properties.getPropertyClass(name);
    

    if (propClass == null)
    {
      Object propValue = getProperty(name);
      
      if (propValue != Image.UndefinedProperty)
      {
        propClass = propValue.getClass();
      }
    }
    
    return propClass;
  }
  







  public final String[] getPropertyNames()
  {
    return RenderedImageAdapter.mergePropertyNames(properties.getPropertyNames(), im.getPropertyNames());
  }
  












  public String[] getPropertyNames(String prefix)
  {
    return PropertyUtil.getPropertyNames(getPropertyNames(), prefix);
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
  






  public final float getWidth()
  {
    return im.getWidth();
  }
  





  public final float getHeight()
  {
    return im.getHeight();
  }
  


  public final float getMinX()
  {
    return im.getMinX();
  }
  


  public final float getMinY()
  {
    return im.getMinY();
  }
  






  public final boolean isDynamic()
  {
    return im.isDynamic();
  }
  












  public final RenderedImage createScaledRendering(int w, int h, RenderingHints hints)
  {
    return im.createScaledRendering(w, h, hints);
  }
  









  public final RenderedImage createDefaultRendering()
  {
    return im.createDefaultRendering();
  }
  







  public final RenderedImage createRendering(RenderContext renderContext)
  {
    return im.createRendering(renderContext);
  }
}
