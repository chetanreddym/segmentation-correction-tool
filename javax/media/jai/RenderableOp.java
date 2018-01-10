package javax.media.jai;

import com.sun.media.jai.util.ImageUtil;
import com.sun.media.jai.util.PropertyUtil;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ContextualRenderedImageFactory;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Vector;
import javax.media.jai.registry.CRIFRegistry;
import javax.media.jai.util.CaselessStringKey;





































































































































































public class RenderableOp
  implements RenderableImage, OperationNode, WritablePropertySource, Serializable
{
  protected PropertyChangeSupportJAI eventManager = null;
  





  protected WritablePropertySourceImpl properties = null;
  





  protected OperationNodeSupport nodeSupport;
  




  protected transient PropertySource thePropertySource;
  




  protected transient ContextualRenderedImageFactory crif = null;
  






























  public RenderableOp(OperationRegistry registry, String opName, ParameterBlock pb, RenderingHints hints)
  {
    if (pb == null)
    {
      pb = new ParameterBlock();
    }
    else {
      pb = (ParameterBlock)pb.clone();
    }
    

    if (hints != null) {
      hints = (RenderingHints)hints.clone();
    }
    

    eventManager = new PropertyChangeSupportJAI(this);
    
    properties = new WritablePropertySourceImpl(null, null, eventManager);
    
    nodeSupport = new OperationNodeSupport(getRegistryModeName(), opName, registry, pb, hints, eventManager);
  }
  




























  public RenderableOp(OperationRegistry registry, String opName, ParameterBlock pb)
  {
    this(registry, opName, pb, null);
  }
  


















  public RenderableOp(String opName, ParameterBlock pb)
  {
    this(null, opName, pb);
  }
  






  public String getRegistryModeName()
  {
    return RegistryMode.getMode("renderable").getName();
  }
  






  public synchronized OperationRegistry getRegistry()
  {
    return nodeSupport.getRegistry();
  }
  








  public synchronized void setRegistry(OperationRegistry registry)
  {
    nodeSupport.setRegistry(registry);
  }
  



  public String getOperationName()
  {
    return nodeSupport.getOperationName();
  }
  












  public synchronized void setOperationName(String opName)
  {
    nodeSupport.setOperationName(opName);
  }
  
  public ParameterBlock getParameterBlock()
  {
    return (ParameterBlock)nodeSupport.getParameterBlock().clone();
  }
  
























  public synchronized void setParameterBlock(ParameterBlock pb)
  {
    nodeSupport.setParameterBlock(pb == null ? new ParameterBlock() : (ParameterBlock)pb.clone());
  }
  







  public RenderingHints getRenderingHints()
  {
    RenderingHints hints = nodeSupport.getRenderingHints();
    return hints == null ? null : (RenderingHints)hints.clone();
  }
  













  public synchronized void setRenderingHints(RenderingHints hints)
  {
    if (hints != null) {
      hints = (RenderingHints)hints.clone();
    }
    nodeSupport.setRenderingHints(hints);
  }
  

  private Vector getRenderableSources()
  {
    Vector sources = null;
    
    int numSrcs = nodeSupport.getParameterBlock().getNumSources();
    if (numSrcs > 0) {
      sources = new Vector();
      for (int i = 0; i < numSrcs; i++) {
        Object o = nodeSupport.getParameterBlock().getSource(i);
        if ((o instanceof RenderableImage)) {
          sources.add(o);
        }
      }
    }
    return sources;
  }
  








  public Vector getSources()
  {
    return getRenderableSources();
  }
  
  private synchronized ContextualRenderedImageFactory findCRIF()
  {
    if (crif == null)
    {
      crif = CRIFRegistry.get(getRegistry(), getOperationName());
    }
    if (crif == null) {
      throw new RuntimeException(JaiI18N.getString("RenderableOp2"));
    }
    
    return crif;
  }
  




  public float getWidth()
  {
    findCRIF();
    ParameterBlock paramBlock = ImageUtil.evaluateParameters(nodeSupport.getParameterBlock());
    
    Rectangle2D boundingBox = crif.getBounds2D(paramBlock);
    return (float)boundingBox.getWidth();
  }
  




  public float getHeight()
  {
    findCRIF();
    ParameterBlock paramBlock = ImageUtil.evaluateParameters(nodeSupport.getParameterBlock());
    
    Rectangle2D boundingBox = crif.getBounds2D(paramBlock);
    return (float)boundingBox.getHeight();
  }
  


  public float getMinX()
  {
    findCRIF();
    ParameterBlock paramBlock = ImageUtil.evaluateParameters(nodeSupport.getParameterBlock());
    
    Rectangle2D boundingBox = crif.getBounds2D(paramBlock);
    return (float)boundingBox.getX();
  }
  


  public float getMinY()
  {
    findCRIF();
    ParameterBlock paramBlock = ImageUtil.evaluateParameters(nodeSupport.getParameterBlock());
    
    Rectangle2D boundingBox = crif.getBounds2D(paramBlock);
    return (float)boundingBox.getY();
  }
  
































  public RenderedImage createDefaultRendering()
  {
    Dimension defaultDimension = null;
    RenderingHints hints = nodeSupport.getRenderingHints();
    if ((hints != null) && (hints.containsKey(JAI.KEY_DEFAULT_RENDERING_SIZE)))
    {
      defaultDimension = (Dimension)hints.get(JAI.KEY_DEFAULT_RENDERING_SIZE);
    }
    
    if ((defaultDimension == null) || ((width <= 0) && (height <= 0)))
    {
      defaultDimension = JAI.getDefaultRenderingSize();
    }
    

    double sx = 1.0D;
    double sy = 1.0D;
    

    if ((defaultDimension != null) && ((width > 0) || (height > 0)))
    {
      if ((width > 0) && (height > 0)) {
        sx = width / getWidth();
        sy = height / getHeight();
      } else if (width > 0) {
        sx = sy = width / getWidth();
      } else {
        sx = sy = height / getHeight();
      }
    }
    

    AffineTransform transform = AffineTransform.getScaleInstance(sx, sy);
    

    return createRendering(new RenderContext(transform));
  }
  
































  public RenderedImage createScaledRendering(int w, int h, RenderingHints hints)
  {
    if ((w == 0) && (h == 0)) {
      throw new IllegalArgumentException(JaiI18N.getString("RenderableOp3"));
    }
    
    if (w == 0) {
      w = Math.round(h * (getWidth() / getHeight()));
    } else if (h == 0) {
      h = Math.round(w * (getHeight() / getWidth()));
    }
    double sx = w / getWidth();
    double sy = h / getHeight();
    
    AffineTransform usr2dev = AffineTransform.getScaleInstance(sx, sy);
    RenderContext renderContext = new RenderContext(usr2dev, hints);
    return createRendering(renderContext);
  }
  


























  public RenderedImage createRendering(RenderContext renderContext)
  {
    findCRIF();
    



    ParameterBlock nodePB = nodeSupport.getParameterBlock();
    Vector nodeParams = ImageUtil.evaluateParameters(nodePB.getParameters());
    
    ParameterBlock renderedPB = new ParameterBlock((Vector)nodePB.getSources().clone(), nodeParams);
    

    Vector sources = getRenderableSources();
    





    try
    {
      RenderContext rcIn = renderContext;
      RenderingHints nodeHints = nodeSupport.getRenderingHints();
      if (nodeHints != null) {
        RenderingHints hints = renderContext.getRenderingHints();
        RenderingHints mergedHints = JAI.mergeRenderingHints(nodeHints, hints);
        
        if (mergedHints != hints) {
          rcIn = new RenderContext(renderContext.getTransform(), renderContext.getAreaOfInterest(), mergedHints);
        }
      }
      


      if (sources != null) {
        Vector renderedSources = new Vector();
        for (int i = 0; i < sources.size(); i++) {
          RenderContext rcOut = crif.mapRenderContext(i, rcIn, renderedPB, this);
          


          RenderableImage src = (RenderableImage)sources.elementAt(i);
          
          RenderedImage renderedImage = src.createRendering(rcOut);
          if (renderedImage == null) {
            return null;
          }
          


          renderedSources.addElement(renderedImage);
        }
        
        if (renderedSources.size() > 0) {
          renderedPB.setSources(renderedSources);
        }
      }
      
      RenderedImage rendering = crif.create(rcIn, renderedPB);
      

      if ((rendering instanceof RenderedOp)) {
        rendering = ((RenderedOp)rendering).getRendering();
      }
      

      if ((rendering != null) && ((rendering instanceof WritablePropertySource)))
      {
        String[] propertyNames = getPropertyNames();
        if (propertyNames != null) {
          WritablePropertySource wps = (WritablePropertySource)rendering;
          


          HashSet wpsNameSet = null;
          String[] wpsNames = wps.getPropertyNames();
          if (wpsNames != null) {
            wpsNameSet = new HashSet();
            for (int j = 0; j < wpsNames.length; j++) {
              wpsNameSet.add(new CaselessStringKey(wpsNames[j]));
            }
          }
          

          for (int j = 0; j < propertyNames.length; j++) {
            String name = propertyNames[j];
            if ((wpsNameSet == null) || (!wpsNameSet.contains(new CaselessStringKey(name))))
            {
              Object value = getProperty(name);
              if ((value != null) && (value != Image.UndefinedProperty))
              {
                wps.setProperty(name, value);
              }
            }
          }
        }
      }
      
      return rendering;
    }
    catch (ArrayIndexOutOfBoundsException e) {}
    return null;
  }
  




  public boolean isDynamic()
  {
    return false;
  }
  


  private synchronized void createPropertySource()
  {
    if (thePropertySource == null)
    {

      thePropertySource = nodeSupport.getPropertySource(this, null);
      

      properties.addProperties(thePropertySource);
    }
  }
  







  public String[] getPropertyNames()
  {
    createPropertySource();
    return properties.getPropertyNames();
  }
  











  public String[] getPropertyNames(String prefix)
  {
    return PropertyUtil.getPropertyNames(getPropertyNames(), prefix);
  }
  











  public Class getPropertyClass(String name)
  {
    createPropertySource();
    return properties.getPropertyClass(name);
  }
  










  public Object getProperty(String name)
  {
    createPropertySource();
    return properties.getProperty(name);
  }
  












  public void setProperty(String name, Object value)
  {
    createPropertySource();
    properties.setProperty(name, value);
  }
  









  public void removeProperty(String name)
  {
    createPropertySource();
    properties.removeProperty(name);
  }
  

















  public synchronized Object getDynamicProperty(String name)
  {
    createPropertySource();
    return thePropertySource.getProperty(name);
  }
  







  public void addPropertyGenerator(PropertyGenerator pg)
  {
    nodeSupport.addPropertyGenerator(pg);
  }
  













  public synchronized void copyPropertyFromSource(String propertyName, int sourceIndex)
  {
    nodeSupport.copyPropertyFromSource(propertyName, sourceIndex);
  }
  
















  public void suppressProperty(String name)
  {
    nodeSupport.suppressProperty(name);
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
  






  public Object getSource(int index)
  {
    Vector sources = nodeSupport.getParameterBlock().getSources();
    return sources.elementAt(index);
  }
  










  public void setSource(Object source, int index)
  {
    if (source == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    ParameterBlock pb = (ParameterBlock)nodeSupport.getParameterBlock().clone();
    
    pb.setSource(source, index);
    nodeSupport.setParameterBlock(pb);
  }
  







  public void removeSources()
  {
    ParameterBlock pb = (ParameterBlock)nodeSupport.getParameterBlock().clone();
    
    pb.removeSources();
    nodeSupport.setParameterBlock(pb);
  }
  






  public byte getByteParameter(int index)
  {
    return nodeSupport.getParameterBlock().getByteParameter(index);
  }
  




  public char getCharParameter(int index)
  {
    return nodeSupport.getParameterBlock().getCharParameter(index);
  }
  




  public short getShortParameter(int index)
  {
    return nodeSupport.getParameterBlock().getShortParameter(index);
  }
  




  public int getIntParameter(int index)
  {
    return nodeSupport.getParameterBlock().getIntParameter(index);
  }
  




  public long getLongParameter(int index)
  {
    return nodeSupport.getParameterBlock().getLongParameter(index);
  }
  




  public float getFloatParameter(int index)
  {
    return nodeSupport.getParameterBlock().getFloatParameter(index);
  }
  




  public double getDoubleParameter(int index)
  {
    return nodeSupport.getParameterBlock().getDoubleParameter(index);
  }
  




  public Object getObjectParameter(int index)
  {
    return nodeSupport.getParameterBlock().getObjectParameter(index);
  }
  








  public void setParameter(byte param, int index)
  {
    setParameter(new Byte(param), index);
  }
  








  public void setParameter(char param, int index)
  {
    setParameter(new Character(param), index);
  }
  








  public void setParameter(short param, int index)
  {
    setParameter(new Short(param), index);
  }
  








  public void setParameter(int param, int index)
  {
    setParameter(new Integer(param), index);
  }
  








  public void setParameter(long param, int index)
  {
    setParameter(new Long(param), index);
  }
  








  public void setParameter(float param, int index)
  {
    setParameter(new Float(param), index);
  }
  








  public void setParameter(double param, int index)
  {
    setParameter(new Double(param), index);
  }
  













  public void setParameter(Object param, int index)
  {
    ParameterBlock pb = (ParameterBlock)nodeSupport.getParameterBlock().clone();
    
    pb.set(param, index);
    nodeSupport.setParameterBlock(pb);
  }
}
