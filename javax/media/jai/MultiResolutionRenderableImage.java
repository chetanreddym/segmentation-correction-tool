package javax.media.jai;

import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Vector;
import javax.media.jai.remote.SerializableState;
import javax.media.jai.remote.SerializerFactory;



































public class MultiResolutionRenderableImage
  implements WritablePropertySource, RenderableImage, Serializable
{
  protected transient RenderedImage[] renderedSource;
  private int numSources;
  protected float aspect;
  protected float minX;
  protected float minY;
  protected float width;
  protected float height;
  protected PropertyChangeSupportJAI eventManager = null;
  





  protected WritablePropertySourceImpl properties = null;
  
  private MultiResolutionRenderableImage() {
    eventManager = new PropertyChangeSupportJAI(this);
    properties = new WritablePropertySourceImpl(null, null, eventManager);
  }
  

















  public MultiResolutionRenderableImage(Vector renderedSources, float minX, float minY, float height)
  {
    this();
    

    if (height <= 0.0F) {
      throw new IllegalArgumentException(JaiI18N.getString("MultiResolutionRenderableImage0"));
    }
    
    numSources = renderedSources.size();
    renderedSource = new RenderedImage[numSources];
    for (int i = 0; i < numSources; i++) {
      renderedSource[i] = ((RenderedImage)renderedSources.elementAt(i));
    }
    

    int maxResWidth = renderedSource[0].getWidth();
    int maxResHeight = renderedSource[0].getHeight();
    aspect = (maxResWidth / maxResHeight);
    
    this.minX = minX;
    width = (height * aspect);
    
    this.minY = minY;
    this.height = height;
  }
  





  public Vector getSources()
  {
    return null;
  }
  









  public String[] getPropertyNames()
  {
    return properties.getPropertyNames();
  }
  














  public String[] getPropertyNames(String prefix)
  {
    if (prefix == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    return properties.getPropertyNames(prefix);
  }
  












  public Class getPropertyClass(String name)
  {
    return properties.getPropertyClass(name);
  }
  












  public Object getProperty(String name)
  {
    return properties.getProperty(name);
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
  


  public float getWidth()
  {
    return width;
  }
  


  public float getHeight()
  {
    return height;
  }
  



  public float getMinX()
  {
    return minX;
  }
  



  public float getMaxX()
  {
    return minX + width;
  }
  



  public float getMinY()
  {
    return minY;
  }
  



  public float getMaxY()
  {
    return minY + height;
  }
  




  public boolean isDynamic()
  {
    return false;
  }
  

















  public RenderedImage createScaledRendering(int width, int height, RenderingHints hints)
  {
    if ((width <= 0) && (height <= 0)) {
      throw new IllegalArgumentException(JaiI18N.getString("MultiResolutionRenderableImage1"));
    }
    

    int res = numSources - 1;
    while (res > 0) {
      if (height > 0) {
        int imh = renderedSource[res].getHeight();
        if (imh >= height) {
          break;
        }
      } else {
        int imw = renderedSource[res].getWidth();
        if (imw >= width) {
          break;
        }
      }
      res--;
    }
    
    RenderedImage source = renderedSource[res];
    if (width <= 0) {
      width = Math.round(height * source.getWidth() / source.getHeight());
    } else if (height <= 0) {
      height = Math.round(width * source.getHeight() / source.getWidth());
    }
    double sx = width / source.getWidth();
    double sy = height / source.getHeight();
    double tx = (getMinX() - source.getMinX()) * sx;
    double ty = (getMinY() - source.getMinY()) * sy;
    
    Interpolation interp = Interpolation.getInstance(0);
    
    if (hints != null) {
      Object obj = hints.get(JAI.KEY_INTERPOLATION);
      if (obj != null) {
        interp = (Interpolation)obj;
      }
    }
    
    ParameterBlock pb = new ParameterBlock();
    pb.addSource(source);
    pb.add((float)sx);
    pb.add((float)sy);
    pb.add((float)tx);
    pb.add((float)ty);
    pb.add(interp);
    
    return JAI.create("scale", pb, null);
  }
  



  public RenderedImage createDefaultRendering()
  {
    return renderedSource[0];
  }
  



















  public RenderedImage createRendering(RenderContext renderContext)
  {
    if (renderContext == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    

    AffineTransform usr2dev = renderContext.getTransform();
    RenderingHints hints = renderContext.getRenderingHints();
    
    int type = usr2dev.getType();
    if ((type == 2) || (type == 4))
    {
      int width = (int)Math.ceil(usr2dev.getScaleX() * getWidth());
      int height = (int)Math.ceil(usr2dev.getScaleY() * getHeight());
      
      return createScaledRendering(width, height, hints);
    }
    


    int height = (int)Math.ceil(Math.sqrt(usr2dev.getDeterminant()) * getHeight());
    
    int res = numSources - 1;
    while (res > 0) {
      int imh = renderedSource[res].getHeight();
      if (imh >= height) {
        break;
      }
      res--;
    }
    
    RenderedImage source = renderedSource[res];
    double sx = getWidth() / source.getWidth();
    double sy = getHeight() / source.getHeight();
    
    AffineTransform transform = new AffineTransform();
    transform.translate(-source.getMinX(), -source.getMinY());
    transform.scale(sx, sy);
    transform.translate(getMinX(), getMinY());
    transform.preConcatenate(usr2dev);
    
    Interpolation interp = Interpolation.getInstance(0);
    
    if (hints != null) {
      Object obj = hints.get(JAI.KEY_INTERPOLATION);
      if (obj != null) {
        interp = (Interpolation)obj;
      }
    }
    
    ParameterBlock pb = new ParameterBlock();
    pb.addSource(source);
    pb.add(transform);
    pb.add(interp);
    
    return JAI.create("affine", pb, null);
  }
  




  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    Object[] sources = new Object[numSources];
    

    for (int i = 0; i < numSources; i++) {
      if ((renderedSource[i] instanceof Serializable))
      {
        sources[i] = renderedSource[i];
      }
      else {
        sources[i] = SerializerFactory.getState(renderedSource[i]);
      }
    }
    

    out.defaultWriteObject();
    

    out.writeObject(sources);
  }
  





  private void readObject(ObjectInputStream in)
    throws IOException, ClassNotFoundException
  {
    in.defaultReadObject();
    

    Object[] source = (Object[])in.readObject();
    numSources = source.length;
    renderedSource = new RenderedImage[numSources];
    for (int i = 0; i < numSources; i++) {
      if ((source[i] instanceof SerializableState)) {
        SerializableState ss = (SerializableState)source[i];
        renderedSource[i] = ((RenderedImage)ss.getObject());
      } else { renderedSource[i] = ((RenderedImage)source[i]);
      }
    }
  }
}
