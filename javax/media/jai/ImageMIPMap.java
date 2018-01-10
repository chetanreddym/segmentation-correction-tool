package javax.media.jai;

import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderableImage;
import java.beans.PropertyChangeListener;
import java.util.Vector;











































public class ImageMIPMap
  implements ImageJAI
{
  protected RenderedImage highestImage;
  protected RenderedImage currentImage;
  protected int currentLevel = 0;
  



  protected RenderedOp downSampler;
  



  protected PropertyChangeSupportJAI eventManager = null;
  





  protected WritablePropertySourceImpl properties = null;
  
  protected ImageMIPMap()
  {
    eventManager = new PropertyChangeSupportJAI(this);
    properties = new WritablePropertySourceImpl(null, null, eventManager);
  }
  



















  public ImageMIPMap(RenderedImage image, AffineTransform transform, Interpolation interpolation)
  {
    this();
    
    if ((image == null) || (transform == null)) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    ParameterBlock pb = new ParameterBlock();
    pb.addSource(image);
    pb.add(transform);
    pb.add(interpolation);
    
    downSampler = JAI.create("affine", pb);
    downSampler.removeSources();
    
    highestImage = image;
    currentImage = highestImage;
  }
  
















  public ImageMIPMap(RenderedImage image, RenderedOp downSampler)
  {
    this();
    if ((image == null) || (downSampler == null)) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    highestImage = image;
    currentImage = highestImage;
    this.downSampler = downSampler;
  }
  


























  public ImageMIPMap(RenderedOp downSampler)
  {
    this();
    
    if (downSampler == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if (downSampler.getNumSources() == 0) {
      throw new IllegalArgumentException(JaiI18N.getString("ImageMIPMap0"));
    }
    


    RenderedOp op = downSampler;
    for (;;) {
      Object src = op.getNodeSource(0);
      
      if ((src instanceof RenderedOp)) {
        RenderedOp srcOp = (RenderedOp)src;
        
        if (srcOp.getNumSources() == 0) {
          highestImage = srcOp;
          op.removeSources();
          break;
        }
        op = srcOp;
      } else {
        if ((src instanceof RenderedImage)) {
          highestImage = ((RenderedImage)src);
          op.removeSources();
          break;
        }
        throw new IllegalArgumentException(JaiI18N.getString("ImageMIPMap1"));
      }
    }
    

    currentImage = highestImage;
    this.downSampler = downSampler;
  }
  










  public String[] getPropertyNames()
  {
    return properties.getPropertyNames();
  }
  














  public String[] getPropertyNames(String prefix)
  {
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
  



  public int getCurrentLevel()
  {
    return currentLevel;
  }
  
  public RenderedImage getCurrentImage()
  {
    return currentImage;
  }
  






  public RenderedImage getImage(int level)
  {
    if (level < 0) {
      return null;
    }
    
    if (level < currentLevel) {
      currentImage = highestImage;
      currentLevel = 0;
    }
    
    while (currentLevel < level) {
      getDownImage();
    }
    return currentImage;
  }
  




  public RenderedImage getDownImage()
  {
    currentLevel += 1;
    

    RenderedOp op = duplicate(downSampler, vectorize(currentImage));
    currentImage = op.getRendering();
    return currentImage;
  }
  
















  protected RenderedOp duplicate(RenderedOp op, Vector images)
  {
    if (images == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    





    op = new RenderedOp(op.getRegistry(), op.getOperationName(), op.getParameterBlock(), op.getRenderingHints());
    



    ParameterBlock pb = new ParameterBlock();
    pb.setParameters(op.getParameters());
    
    Vector srcs = op.getSources();
    int numSrcs = srcs.size();
    
    if (numSrcs == 0) {
      pb.setSources(images);
    }
    else {
      pb.addSource(duplicate((RenderedOp)srcs.elementAt(0), images));
      
      for (int i = 1; i < numSrcs; i++) {
        pb.addSource(srcs.elementAt(i));
      }
    }
    
    op.setParameterBlock(pb);
    return op;
  }
  



























  public RenderableImage getAsRenderable(int numImages, float minX, float minY, float height)
  {
    Vector v = new Vector();
    v.add(currentImage);
    
    RenderedImage image = currentImage;
    for (int i = 1; i < numImages; i++) {
      RenderedOp op = duplicate(downSampler, vectorize(image));
      image = op.getRendering();
      
      if ((image.getWidth() <= 1) || (image.getHeight() <= 1)) {
        break;
      }
      
      v.add(image);
    }
    
    return new MultiResolutionRenderableImage(v, minX, minY, height);
  }
  







  public RenderableImage getAsRenderable()
  {
    return getAsRenderable(1, 0.0F, 0.0F, 1.0F);
  }
  








  protected final Vector vectorize(RenderedImage image)
  {
    Vector v = new Vector(1);
    v.add(image);
    return v;
  }
  







  protected final Vector vectorize(RenderedImage im1, RenderedImage im2)
  {
    Vector v = new Vector(2);
    v.add(im1);
    v.add(im2);
    return v;
  }
}
