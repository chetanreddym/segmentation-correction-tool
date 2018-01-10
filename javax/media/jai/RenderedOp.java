package javax.media.jai;

import com.sun.media.jai.util.ImageUtil;
import com.sun.media.jai.util.PropertyUtil;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;
import javax.media.jai.registry.RIFRegistry;
import javax.media.jai.remote.PlanarImageServerProxy;
import javax.media.jai.util.CaselessStringKey;
import javax.media.jai.util.ImagingListener;


























































































































































































































































































public class RenderedOp
  extends PlanarImage
  implements OperationNode, PropertyChangeListener, Serializable
{
  protected OperationNodeSupport nodeSupport;
  protected transient PropertySource thePropertySource;
  protected transient PlanarImage theImage;
  private transient RenderingHints oldHints;
  private static List synthProps;
  private Hashtable synthProperties = null;
  

  private static Set nodeEventNames = null;
  



  private boolean isDisposed = false;
  
  static {
    CaselessStringKey[] propKeys = { new CaselessStringKey("image_width"), new CaselessStringKey("image_height"), new CaselessStringKey("image_min_x_coord"), new CaselessStringKey("image_min_y_coord"), new CaselessStringKey("tile_cache"), new CaselessStringKey("tile_cache_key") };
    







    synthProps = Arrays.asList(propKeys);
    
    nodeEventNames = new HashSet();
    nodeEventNames.add("operationname");
    nodeEventNames.add("operationregistry");
    nodeEventNames.add("parameterblock");
    nodeEventNames.add("sources");
    nodeEventNames.add("parameters");
    nodeEventNames.add("renderinghints");
  }
  




































  public RenderedOp(OperationRegistry registry, String opName, ParameterBlock pb, RenderingHints hints)
  {
    super(new ImageLayout(), null, null);
    
    if (pb == null)
    {
      pb = new ParameterBlock();
    }
    else {
      pb = (ParameterBlock)pb.clone();
    }
    
    if (hints != null)
    {
      hints = (RenderingHints)hints.clone();
    }
    
    nodeSupport = new OperationNodeSupport(getRegistryModeName(), opName, registry, pb, hints, eventManager);
    









    addPropertyChangeListener("OperationName", this);
    addPropertyChangeListener("OperationRegistry", this);
    addPropertyChangeListener("ParameterBlock", this);
    addPropertyChangeListener("Sources", this);
    addPropertyChangeListener("Parameters", this);
    addPropertyChangeListener("RenderingHints", this);
    

    Vector nodeSources = pb.getSources();
    if (nodeSources != null) {
      Iterator it = nodeSources.iterator();
      while (it.hasNext()) {
        Object src = it.next();
        if ((src instanceof PlanarImage)) {
          ((PlanarImage)src).addSink(this);
        } else if ((src instanceof CollectionImage)) {
          ((CollectionImage)src).addSink(this);
        }
      }
    }
  }
  

































  public RenderedOp(String opName, ParameterBlock pb, RenderingHints hints)
  {
    this(null, opName, pb, hints);
  }
  
  private class TCL implements TileComputationListener
  {
    RenderedOp node;
    
    TCL(RenderedOp x1, RenderedOp.1 x2)
    {
      this(x1);
    }
    
    private TCL(RenderedOp node) {
      this.node = node;
    }
    


    public void tileComputed(Object eventSource, TileRequest[] requests, PlanarImage image, int tileX, int tileY, Raster tile)
    {
      if (image == theImage)
      {
        TileComputationListener[] listeners = getTileComputationListeners();
        

        if (listeners != null) {
          int numListeners = listeners.length;
          
          for (int i = 0; i < numListeners; i++) {
            listeners[i].tileComputed(node, requests, image, tileX, tileY, tile);
          }
        }
      }
    }
    


    public void tileCancelled(Object eventSource, TileRequest[] requests, PlanarImage image, int tileX, int tileY)
    {
      if (image == theImage)
      {
        TileComputationListener[] listeners = getTileComputationListeners();
        

        if (listeners != null) {
          int numListeners = listeners.length;
          
          for (int i = 0; i < numListeners; i++) {
            listeners[i].tileCancelled(node, requests, image, tileX, tileY);
          }
        }
      }
    }
    




    public void tileComputationFailure(Object eventSource, TileRequest[] requests, PlanarImage image, int tileX, int tileY, Throwable situation)
    {
      if (image == theImage)
      {
        TileComputationListener[] listeners = getTileComputationListeners();
        

        if (listeners != null) {
          int numListeners = listeners.length;
          
          for (int i = 0; i < numListeners; i++) {
            listeners[i].tileComputationFailure(node, requests, image, tileX, tileY, situation);
          }
        }
      }
    }
  }
  








  public String getRegistryModeName()
  {
    return RegistryMode.getMode("rendered").getName();
  }
  






  public synchronized OperationRegistry getRegistry()
  {
    return nodeSupport.getRegistry();
  }
  












  public synchronized void setRegistry(OperationRegistry registry)
  {
    nodeSupport.setRegistry(registry);
  }
  



  public synchronized String getOperationName()
  {
    return nodeSupport.getOperationName();
  }
  













  public synchronized void setOperationName(String opName)
  {
    nodeSupport.setOperationName(opName);
  }
  
  public synchronized ParameterBlock getParameterBlock()
  {
    return (ParameterBlock)nodeSupport.getParameterBlock().clone();
  }
  































  public synchronized void setParameterBlock(ParameterBlock pb)
  {
    Vector nodeSources = nodeSupport.getParameterBlock().getSources();
    if ((nodeSources != null) && (nodeSources.size() > 0)) {
      Iterator it = nodeSources.iterator();
      while (it.hasNext()) {
        Object src = it.next();
        if ((src instanceof PlanarImage)) {
          ((PlanarImage)src).removeSink(this);
        } else if ((src instanceof CollectionImage)) {
          ((CollectionImage)src).removeSink(this);
        }
      }
    }
    
    if (pb != null) {
      Vector newSources = pb.getSources();
      if ((newSources != null) && (newSources.size() > 0)) {
        Iterator it = newSources.iterator();
        while (it.hasNext()) {
          Object src = it.next();
          if ((src instanceof PlanarImage)) {
            ((PlanarImage)src).addSink(this);
          } else if ((src instanceof CollectionImage)) {
            ((CollectionImage)src).addSink(this);
          }
        }
      }
    }
    
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
  










































  public synchronized PlanarImage createInstance()
  {
    return createInstance(false);
  }
  











  protected synchronized PlanarImage createInstance(boolean isNodeRendered)
  {
    ParameterBlock pb = new ParameterBlock();
    Vector parameters = nodeSupport.getParameterBlock().getParameters();
    

    pb.setParameters(ImageUtil.evaluateParameters(parameters));
    
    int numSources = getNumSources();
    for (int i = 0; i < numSources; i++) {
      Object source = getNodeSource(i);
      Object ai = null;
      
      if ((source instanceof RenderedOp)) {
        RenderedOp src = (RenderedOp)source;
        ai = isNodeRendered ? src.getRendering() : src.createInstance();

      }
      else if ((source instanceof CollectionOp)) {
        ai = ((CollectionOp)source).getCollection();
      } else if (((source instanceof RenderedImage)) || ((source instanceof Collection)))
      {




        ai = source;
      }
      else {
        ai = source;
      }
      pb.addSource(ai);
    }
    

    RenderedImage rendering = RIFRegistry.create(getRegistry(), nodeSupport.getOperationName(), pb, nodeSupport.getRenderingHints());
    





    if (rendering == null) {
      throw new RuntimeException(JaiI18N.getString("RenderedOp0"));
    }
    













    PlanarImage instance = PlanarImage.wrapRenderedImage(rendering);
    

    oldHints = (nodeSupport.getRenderingHints() == null ? null : (RenderingHints)nodeSupport.getRenderingHints().clone());
    

    return instance;
  }
  












  protected synchronized void createRendering()
  {
    if (theImage == null) {
      setImageLayout(new ImageLayout(this.theImage = createInstance(true)));
      
      if (theImage != null)
      {
        theImage.addTileComputationListener(new TCL(this, null));
      }
    }
  }
  










  public PlanarImage getRendering()
  {
    createRendering();
    return theImage;
  }
  






  public PlanarImage getCurrentRendering()
  {
    return theImage;
  }
  





























  public PlanarImage getNewRendering()
  {
    if (theImage == null) {
      return getRendering();
    }
    

    PlanarImage theOldImage = theImage;
    

    theImage = null;
    





    createRendering();
    


    resetProperties(true);
    

    RenderingChangeEvent rcEvent = new RenderingChangeEvent(this, theOldImage, theImage, null);
    


    eventManager.firePropertyChange(rcEvent);
    

    Vector sinks = getSinks();
    if (sinks != null) {
      int numSinks = sinks.size();
      for (int i = 0; i < numSinks; i++) {
        Object sink = sinks.get(i);
        if ((sink instanceof PropertyChangeListener)) {
          ((PropertyChangeListener)sink).propertyChange(rcEvent);
        }
      }
    }
    
    return theImage;
  }
  


























  public synchronized void propertyChange(PropertyChangeEvent evt)
  {
    Object evtSrc = evt.getSource();
    Vector nodeSources = nodeSupport.getParameterBlock().getSources();
    


    String propName = evt.getPropertyName().toLowerCase(Locale.ENGLISH);
    
    if ((theImage != null) && ((((evt instanceof PropertyChangeEventJAI)) && (evtSrc == this) && (!(evt instanceof PropertySourceChangeEvent)) && (nodeEventNames.contains(propName))) || ((((evt instanceof RenderingChangeEvent)) || ((evt instanceof CollectionChangeEvent)) || (((evt instanceof PropertyChangeEventJAI)) && ((evtSrc instanceof RenderedImage)) && (propName.equals("invalidregion")))) && (nodeSources.contains(evtSrc)))))
    {











      PlanarImage theOldImage = theImage;
      

      boolean fireEvent = false;
      

      Shape invalidRegion = null;
      
      if ((evtSrc == this) && ((propName.equals("operationname")) || (propName.equals("operationregistry"))))
      {




        fireEvent = true;
        theImage = null;
      }
      else if (((evt instanceof RenderingChangeEvent)) || (((evtSrc instanceof RenderedImage)) && (propName.equals("invalidregion"))))
      {



        fireEvent = true;
        
        Shape srcInvalidRegion = null;
        
        if ((evt instanceof RenderingChangeEvent))
        {
          RenderingChangeEvent rcEvent = (RenderingChangeEvent)evt;
          

          srcInvalidRegion = rcEvent.getInvalidRegion();
          

          if (srcInvalidRegion == null) {
            srcInvalidRegion = ((PlanarImage)rcEvent.getOldValue()).getBounds();
          }
        }
        else
        {
          srcInvalidRegion = (Shape)evt.getNewValue();
          

          if (srcInvalidRegion == null) {
            RenderedImage rSrc = (RenderedImage)evtSrc;
            srcInvalidRegion = new Rectangle(rSrc.getMinX(), rSrc.getMinY(), rSrc.getWidth(), rSrc.getHeight());
          }
        }
        



        if (!(theImage instanceof OpImage))
        {

          theImage = null;
        }
        else
        {
          OpImage oldOpImage = (OpImage)theImage;
          

          Rectangle srcInvalidBounds = srcInvalidRegion.getBounds();
          




          if (srcInvalidBounds.isEmpty()) {
            int x = oldOpImage.tileXToX(oldOpImage.getMinTileX());
            int y = oldOpImage.tileYToY(oldOpImage.getMinTileY());
            int w = oldOpImage.getNumXTiles() * oldOpImage.getTileWidth();
            
            int h = oldOpImage.getNumYTiles() * oldOpImage.getTileHeight();
            
            Rectangle tileBounds = new Rectangle(x, y, w, h);
            Rectangle imageBounds = oldOpImage.getBounds();
            if (!tileBounds.equals(imageBounds)) {
              Area tmpArea = new Area(tileBounds);
              tmpArea.subtract(new Area(imageBounds));
              srcInvalidRegion = tmpArea;
              srcInvalidBounds = srcInvalidRegion.getBounds();
            }
          }
          


          boolean saveAllTiles = false;
          ArrayList validTiles = null;
          if (srcInvalidBounds.isEmpty()) {
            invalidRegion = srcInvalidRegion;
            saveAllTiles = true;
          }
          else {
            int idx = nodeSources.indexOf(evtSrc);
            

            Rectangle dstRegionBounds = oldOpImage.mapSourceRect(srcInvalidBounds, idx);
            


            if (dstRegionBounds == null) {
              dstRegionBounds = oldOpImage.getBounds();
            }
            

            Point[] indices = getTileIndices(dstRegionBounds);
            int numIndices = indices != null ? indices.length : 0;
            GeneralPath gp = null;
            
            for (int i = 0; i < numIndices; i++) {
              if ((i % 1000 == 0) && (gp != null)) {
                gp = new GeneralPath(new Area(gp));
              }
              Rectangle dstRect = getTileRect(x, y);
              
              Rectangle srcRect = oldOpImage.mapDestRect(dstRect, idx);
              
              if (srcRect == null) {
                gp = null;
                break;
              }
              if (srcInvalidRegion.intersects(srcRect)) {
                if (gp == null) {
                  gp = new GeneralPath(dstRect);
                } else {
                  gp.append(dstRect, false);
                }
              } else {
                if (validTiles == null) {
                  validTiles = new ArrayList();
                }
                validTiles.add(indices[i]);
              }
            }
            
            invalidRegion = gp == null ? null : new Area(gp);
          }
          

          theImage = null;
          

          TileCache oldCache = oldOpImage.getTileCache();
          


          if ((oldCache != null) && ((saveAllTiles) || (validTiles != null)))
          {

            createRendering();
            


            if (((theImage instanceof OpImage)) && (((OpImage)theImage).getTileCache() != null))
            {
              OpImage newOpImage = (OpImage)theImage;
              TileCache newCache = newOpImage.getTileCache();
              
              Object tileCacheMetric = newOpImage.getTileCacheMetric();
              

              if (saveAllTiles) {
                Raster[] tiles = oldCache.getTiles(oldOpImage);
                
                int numTiles = tiles == null ? 0 : tiles.length;
                
                for (int i = 0; i < numTiles; i++) {
                  Raster tile = tiles[i];
                  int tx = newOpImage.XToTileX(tile.getMinX());
                  
                  int ty = newOpImage.YToTileY(tile.getMinY());
                  
                  newCache.add(newOpImage, tx, ty, tile, tileCacheMetric);
                }
              }
              else
              {
                int numValidTiles = validTiles.size();
                for (int i = 0; i < numValidTiles; i++) {
                  Point tileIndex = (Point)validTiles.get(i);
                  Raster tile = oldCache.getTile(oldOpImage, x, y);
                  


                  if (tile != null) {
                    newCache.add(newOpImage, x, y, tile, tileCacheMetric);
                  }
                  
                }
                
              }
            }
          }
        }
      }
      else
      {
        ParameterBlock oldPB = null;
        ParameterBlock newPB = null;
        
        boolean checkInvalidRegion = false;
        if (propName.equals("parameterblock")) {
          oldPB = (ParameterBlock)evt.getOldValue();
          newPB = (ParameterBlock)evt.getNewValue();
          checkInvalidRegion = true;
        } else if (propName.equals("sources"))
        {
          Vector params = nodeSupport.getParameterBlock().getParameters();
          
          oldPB = new ParameterBlock((Vector)evt.getOldValue(), params);
          
          newPB = new ParameterBlock((Vector)evt.getNewValue(), params);
          
          checkInvalidRegion = true;
        } else if (propName.equals("parameters"))
        {
          oldPB = new ParameterBlock(nodeSources, (Vector)evt.getOldValue());
          
          newPB = new ParameterBlock(nodeSources, (Vector)evt.getNewValue());
          
          checkInvalidRegion = true;
        } else if (propName.equals("renderinghints")) {
          oldPB = newPB = nodeSupport.getParameterBlock();
          checkInvalidRegion = true;
        } else if ((evt instanceof CollectionChangeEvent))
        {


          int collectionIndex = nodeSources.indexOf(evtSrc);
          Vector oldSources = (Vector)nodeSources.clone();
          Vector newSources = (Vector)nodeSources.clone();
          oldSources.set(collectionIndex, evt.getOldValue());
          newSources.set(collectionIndex, evt.getNewValue());
          
          Vector params = nodeSupport.getParameterBlock().getParameters();
          

          oldPB = new ParameterBlock(oldSources, params);
          newPB = new ParameterBlock(newSources, params);
          
          checkInvalidRegion = true;
        }
        
        if (checkInvalidRegion)
        {
          fireEvent = true;
          

          OperationRegistry registry = nodeSupport.getRegistry();
          OperationDescriptor odesc = (OperationDescriptor)registry.getDescriptor(OperationDescriptor.class, nodeSupport.getOperationName());
          



          oldPB = ImageUtil.evaluateParameters(oldPB);
          newPB = ImageUtil.evaluateParameters(newPB);
          

          invalidRegion = (Shape)odesc.getInvalidRegion("rendered", oldPB, oldHints, newPB, nodeSupport.getRenderingHints(), this);
          






          if ((invalidRegion == null) || (!(theImage instanceof OpImage)))
          {


            theImage = null;
          }
          else
          {
            OpImage oldRendering = (OpImage)theImage;
            theImage = null;
            createRendering();
            


            if (((theImage instanceof OpImage)) && (oldRendering.getTileCache() != null) && (((OpImage)theImage).getTileCache() != null))
            {

              OpImage newRendering = (OpImage)theImage;
              

              TileCache oldCache = oldRendering.getTileCache();
              TileCache newCache = newRendering.getTileCache();
              Object tileCacheMetric = newRendering.getTileCacheMetric();
              




              if (invalidRegion.getBounds().isEmpty()) {
                int x = oldRendering.tileXToX(oldRendering.getMinTileX());
                
                int y = oldRendering.tileYToY(oldRendering.getMinTileY());
                
                int w = oldRendering.getNumXTiles() * oldRendering.getTileWidth();
                
                int h = oldRendering.getNumYTiles() * oldRendering.getTileHeight();
                
                Rectangle tileBounds = new Rectangle(x, y, w, h);
                
                Rectangle imageBounds = oldRendering.getBounds();
                
                if (!tileBounds.equals(imageBounds)) {
                  Area tmpArea = new Area(tileBounds);
                  tmpArea.subtract(new Area(imageBounds));
                  invalidRegion = tmpArea;
                }
              }
              
              if (invalidRegion.getBounds().isEmpty())
              {
                Raster[] tiles = oldCache.getTiles(oldRendering);
                
                int numTiles = tiles == null ? 0 : tiles.length;
                
                for (int i = 0; i < numTiles; i++) {
                  Raster tile = tiles[i];
                  int tx = newRendering.XToTileX(tile.getMinX());
                  
                  int ty = newRendering.YToTileY(tile.getMinY());
                  
                  newCache.add(newRendering, tx, ty, tile, tileCacheMetric);
                }
                

              }
              else
              {
                Raster[] tiles = oldCache.getTiles(oldRendering);
                
                int numTiles = tiles == null ? 0 : tiles.length;
                
                for (int i = 0; i < numTiles; i++) {
                  Raster tile = tiles[i];
                  Rectangle bounds = tile.getBounds();
                  if (!invalidRegion.intersects(bounds)) {
                    newCache.add(newRendering, newRendering.XToTileX(x), newRendering.YToTileY(y), tile, tileCacheMetric);
                  }
                }
              }
            }
          }
        }
      }
      






      createRendering();
      

      if (fireEvent)
      {

        resetProperties(true);
        

        RenderingChangeEvent rcEvent = new RenderingChangeEvent(this, theOldImage, theImage, invalidRegion);
        



        eventManager.firePropertyChange(rcEvent);
        

        Vector sinks = getSinks();
        if (sinks != null) {
          int numSinks = sinks.size();
          for (int i = 0; i < numSinks; i++) {
            Object sink = sinks.get(i);
            if ((sink instanceof PropertyChangeListener)) {
              ((PropertyChangeListener)sink).propertyChange(rcEvent);
            }
          }
        }
      }
    }
  }
  








  /**
   * @deprecated
   */
  public synchronized void addNodeSource(Object source)
  {
    addSource(source);
  }
  












  /**
   * @deprecated
   */
  public synchronized void setNodeSource(Object source, int index)
  {
    setSource(source, index);
  }
  





  /**
   * @deprecated
   */
  public synchronized Object getNodeSource(int index)
  {
    return nodeSupport.getParameterBlock().getSource(index);
  }
  





  public synchronized int getNumParameters()
  {
    return nodeSupport.getParameterBlock().getNumParameters();
  }
  




  public synchronized Vector getParameters()
  {
    Vector params = nodeSupport.getParameterBlock().getParameters();
    return params == null ? null : (Vector)params.clone();
  }
  










  public synchronized byte getByteParameter(int index)
  {
    return nodeSupport.getParameterBlock().getByteParameter(index);
  }
  










  public synchronized char getCharParameter(int index)
  {
    return nodeSupport.getParameterBlock().getCharParameter(index);
  }
  










  public synchronized short getShortParameter(int index)
  {
    return nodeSupport.getParameterBlock().getShortParameter(index);
  }
  










  public synchronized int getIntParameter(int index)
  {
    return nodeSupport.getParameterBlock().getIntParameter(index);
  }
  











  public synchronized long getLongParameter(int index)
  {
    return nodeSupport.getParameterBlock().getLongParameter(index);
  }
  










  public synchronized float getFloatParameter(int index)
  {
    return nodeSupport.getParameterBlock().getFloatParameter(index);
  }
  










  public synchronized double getDoubleParameter(int index)
  {
    return nodeSupport.getParameterBlock().getDoubleParameter(index);
  }
  










  public synchronized Object getObjectParameter(int index)
  {
    return nodeSupport.getParameterBlock().getObjectParameter(index);
  }
  












  public synchronized void setParameters(Vector parameters)
  {
    ParameterBlock pb = (ParameterBlock)nodeSupport.getParameterBlock().clone();
    
    pb.setParameters(parameters);
    nodeSupport.setParameterBlock(pb);
  }
  










  public synchronized void setParameter(byte param, int index)
  {
    setParameter(new Byte(param), index);
  }
  










  public synchronized void setParameter(char param, int index)
  {
    setParameter(new Character(param), index);
  }
  







  public synchronized void setParameter(short param, int index)
  {
    setParameter(new Short(param), index);
  }
  







  public synchronized void setParameter(int param, int index)
  {
    setParameter(new Integer(param), index);
  }
  







  public synchronized void setParameter(long param, int index)
  {
    setParameter(new Long(param), index);
  }
  







  public synchronized void setParameter(float param, int index)
  {
    setParameter(new Float(param), index);
  }
  







  public synchronized void setParameter(double param, int index)
  {
    setParameter(new Double(param), index);
  }
  















  public synchronized void setParameter(Object param, int index)
  {
    ParameterBlock pb = (ParameterBlock)nodeSupport.getParameterBlock().clone();
    
    pb.set(param, index);
    nodeSupport.setParameterBlock(pb);
  }
  















  public synchronized void setRenderingHint(RenderingHints.Key key, Object value)
  {
    if ((key == null) || (value == null)) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    RenderingHints rh = nodeSupport.getRenderingHints();
    if (rh == null) {
      nodeSupport.setRenderingHints(new RenderingHints(key, value));
    } else {
      rh.put(key, value);
      nodeSupport.setRenderingHints(rh);
    }
  }
  







  public synchronized Object getRenderingHint(RenderingHints.Key key)
  {
    RenderingHints rh = nodeSupport.getRenderingHints();
    return rh == null ? null : rh.get(key);
  }
  


  private synchronized void createPropertySource()
  {
    if (thePropertySource == null)
    {
      PropertySource defaultPS = new PropertySource()
      {

        public String[] getPropertyNames()
        {

          return getRendering().getPropertyNames();
        }
        
        public String[] getPropertyNames(String prefix) {
          return PropertyUtil.getPropertyNames(getPropertyNames(), prefix);
        }
        
        public Class getPropertyClass(String name)
        {
          return null;
        }
        



        public Object getProperty(String name)
        {
          return getRendering().getProperty(name);

        }
        

      };
      thePropertySource = nodeSupport.getPropertySource(this, defaultPS);
      

      properties.addProperties(thePropertySource);
    }
  }
  








  protected synchronized void resetProperties(boolean resetPropertySource)
  {
    properties.clearCachedProperties();
    if ((resetPropertySource) && (thePropertySource != null)) {
      synthProperties = null;
      properties.removePropertySource(thePropertySource);
      thePropertySource = null;
    }
  }
  










  public synchronized String[] getPropertyNames()
  {
    createPropertySource();
    

    Vector names = new Vector(synthProps);
    

    CaselessStringKey key = new CaselessStringKey("");
    


    String[] localNames = properties.getPropertyNames();
    if (localNames != null) {
      int length = localNames.length;
      for (int i = 0; i < length; i++) {
        key.setName(localNames[i]);
        

        if (!names.contains(key)) {
          names.add(key.clone());
        }
      }
    }
    

    String[] propertyNames = null;
    int numNames = names.size();
    if (numNames > 0) {
      propertyNames = new String[numNames];
      for (int i = 0; i < numNames; i++) {
        propertyNames[i] = ((CaselessStringKey)names.get(i)).getName();
      }
    }
    
    return propertyNames;
  }
  












  public Class getPropertyClass(String name)
  {
    createPropertySource();
    return properties.getPropertyClass(name);
  }
  
  private synchronized void createSynthProperties()
  {
    if (synthProperties == null) {
      synthProperties = new Hashtable();
      synthProperties.put(new CaselessStringKey("image_width"), new Integer(theImage.getWidth()));
      
      synthProperties.put(new CaselessStringKey("image_height"), new Integer(theImage.getHeight()));
      
      synthProperties.put(new CaselessStringKey("image_min_x_coord"), new Integer(theImage.getMinX()));
      
      synthProperties.put(new CaselessStringKey("image_min_y_coord"), new Integer(theImage.getMinY()));
      

      if ((theImage instanceof OpImage)) {
        synthProperties.put(new CaselessStringKey("tile_cache_key"), theImage);
        
        Object tileCache = ((OpImage)theImage).getTileCache();
        synthProperties.put(new CaselessStringKey("tile_cache"), tileCache == null ? Image.UndefinedProperty : tileCache);


      }
      else if ((theImage instanceof PlanarImageServerProxy)) {
        synthProperties.put(new CaselessStringKey("tile_cache_key"), theImage);
        
        Object tileCache = ((PlanarImageServerProxy)theImage).getTileCache();
        
        synthProperties.put(new CaselessStringKey("tile_cache"), tileCache == null ? Image.UndefinedProperty : tileCache);

      }
      else
      {
        Object tileCacheKey = theImage.getProperty("tile_cache_key");
        synthProperties.put(new CaselessStringKey("tile_cache_key"), tileCacheKey == null ? Image.UndefinedProperty : tileCacheKey);
        


        Object tileCache = theImage.getProperty("tile_cache");
        synthProperties.put(new CaselessStringKey("tile_cache"), tileCache == null ? Image.UndefinedProperty : tileCache);
      }
    }
  }
  
















  public synchronized Object getProperty(String name)
  {
    if (name == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    createPropertySource();
    CaselessStringKey key = new CaselessStringKey(name);
    


    if (synthProps.contains(key)) {
      createRendering();
      

      createSynthProperties();
      return synthProperties.get(key);
    }
    

    Object value = properties.getProperty(name);
    

    if (value == Image.UndefinedProperty) {
      value = thePropertySource.getProperty(name);
    }
    


    if ((value != Image.UndefinedProperty) && (name.equalsIgnoreCase("roi")) && ((value instanceof ROI)))
    {

      ROI roi = (ROI)value;
      Rectangle imageBounds = getBounds();
      if (!imageBounds.contains(roi.getBounds())) {
        value = roi.intersect(new ROIShape(imageBounds));
      }
    }
    
    return value;
  }
  


















  public synchronized void setProperty(String name, Object value)
  {
    if (name == null)
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    if (value == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if (synthProps.contains(new CaselessStringKey(name))) {
      throw new RuntimeException(JaiI18N.getString("RenderedOp4"));
    }
    
    createPropertySource();
    super.setProperty(name, value);
  }
  












  public void removeProperty(String name)
  {
    if (name == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if (synthProps.contains(new CaselessStringKey(name))) {
      throw new RuntimeException(JaiI18N.getString("RenderedOp4"));
    }
    
    createPropertySource();
    properties.removeProperty(name);
  }
  

















  public synchronized Object getDynamicProperty(String name)
  {
    createPropertySource();
    return thePropertySource.getProperty(name);
  }
  










  public synchronized void addPropertyGenerator(PropertyGenerator pg)
  {
    nodeSupport.addPropertyGenerator(pg);
  }
  













  public synchronized void copyPropertyFromSource(String propertyName, int sourceIndex)
  {
    nodeSupport.copyPropertyFromSource(propertyName, sourceIndex);
  }
  



















  public synchronized void suppressProperty(String name)
  {
    if (name == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    if (synthProps.contains(new CaselessStringKey(name))) {
      throw new IllegalArgumentException(JaiI18N.getString("RenderedOp5"));
    }
    
    nodeSupport.suppressProperty(name);
  }
  








  public int getMinX()
  {
    createRendering();
    return theImage.getMinX();
  }
  



  public int getMinY()
  {
    createRendering();
    return theImage.getMinY();
  }
  



  public int getWidth()
  {
    createRendering();
    return theImage.getWidth();
  }
  



  public int getHeight()
  {
    createRendering();
    return theImage.getHeight();
  }
  



  public int getTileWidth()
  {
    createRendering();
    return theImage.getTileWidth();
  }
  



  public int getTileHeight()
  {
    createRendering();
    return theImage.getTileHeight();
  }
  



  public int getTileGridXOffset()
  {
    createRendering();
    return theImage.getTileGridXOffset();
  }
  



  public int getTileGridYOffset()
  {
    createRendering();
    return theImage.getTileGridYOffset();
  }
  



  public SampleModel getSampleModel()
  {
    createRendering();
    return theImage.getSampleModel();
  }
  



  public ColorModel getColorModel()
  {
    createRendering();
    return theImage.getColorModel();
  }
  








  public Raster getTile(int tileX, int tileY)
  {
    createRendering();
    return theImage.getTile(tileX, tileY);
  }
  



  public Raster getData()
  {
    createRendering();
    return theImage.getData();
  }
  




  public Raster getData(Rectangle rect)
  {
    createRendering();
    return theImage.getData(rect);
  }
  



  public WritableRaster copyData()
  {
    createRendering();
    return theImage.copyData();
  }
  








  public WritableRaster copyData(WritableRaster raster)
  {
    createRendering();
    return theImage.copyData(raster);
  }
  











  public Raster[] getTiles(Point[] tileIndices)
  {
    createRendering();
    return theImage.getTiles(tileIndices);
  }
  















  public TileRequest queueTiles(Point[] tileIndices)
  {
    createRendering();
    return theImage.queueTiles(tileIndices);
  }
  












  public void cancelTiles(TileRequest request, Point[] tileIndices)
  {
    if (request == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic4"));
    }
    createRendering();
    theImage.cancelTiles(request, tileIndices);
  }
  










  public void prefetchTiles(Point[] tileIndices)
  {
    createRendering();
    theImage.prefetchTiles(tileIndices);
  }
  


























  /**
   * @deprecated
   */
  public synchronized void addSource(PlanarImage source)
  {
    Object sourceObject = source;
    addSource(sourceObject);
  }
  



























  /**
   * @deprecated
   */
  public synchronized void setSource(PlanarImage source, int index)
  {
    Object sourceObject = source;
    setSource(sourceObject, index);
  }
  




























  /**
   * @deprecated
   */
  public PlanarImage getSource(int index)
  {
    return (PlanarImage)nodeSupport.getParameterBlock().getSource(index);
  }
  






















  /**
   * @deprecated
   */
  public synchronized boolean removeSource(PlanarImage source)
  {
    Object sourceObject = source;
    return removeSource(sourceObject);
  }
  















  public synchronized void addSource(Object source)
  {
    if (source == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    ParameterBlock pb = (ParameterBlock)nodeSupport.getParameterBlock().clone();
    
    pb.addSource(source);
    nodeSupport.setParameterBlock(pb);
    
    if ((source instanceof PlanarImage)) {
      ((PlanarImage)source).addSink(this);
    } else if ((source instanceof CollectionImage)) {
      ((CollectionImage)source).addSink(this);
    }
  }
  





















  public synchronized void setSource(Object source, int index)
  {
    if (source == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    ParameterBlock pb = (ParameterBlock)nodeSupport.getParameterBlock().clone();
    

    if (index < pb.getNumSources()) {
      Object priorSource = pb.getSource(index);
      if ((priorSource instanceof PlanarImage)) {
        ((PlanarImage)priorSource).removeSink(this);
      } else if ((priorSource instanceof CollectionImage)) {
        ((CollectionImage)priorSource).removeSink(this);
      }
    }
    
    pb.setSource(source, index);
    nodeSupport.setParameterBlock(pb);
    
    if ((source instanceof PlanarImage)) {
      ((PlanarImage)source).addSink(this);
    } else if ((source instanceof CollectionImage)) {
      ((CollectionImage)source).addSink(this);
    }
  }
  















  public synchronized boolean removeSource(Object source)
  {
    if (source == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    ParameterBlock pb = (ParameterBlock)nodeSupport.getParameterBlock().clone();
    

    Vector nodeSources = pb.getSources();
    if (nodeSources.contains(source)) {
      if ((source instanceof PlanarImage)) {
        ((PlanarImage)source).removeSink(this);
      } else if ((source instanceof CollectionImage)) {
        ((CollectionImage)source).removeSink(this);
      }
    }
    
    boolean result = nodeSources.remove(source);
    nodeSupport.setParameterBlock(pb);
    
    return result;
  }
  














  public PlanarImage getSourceImage(int index)
  {
    return (PlanarImage)nodeSupport.getParameterBlock().getSource(index);
  }
  











  public synchronized Object getSourceObject(int index)
  {
    return nodeSupport.getParameterBlock().getSource(index);
  }
  







  public int getNumSources()
  {
    return nodeSupport.getParameterBlock().getNumSources();
  }
  




  public synchronized Vector getSources()
  {
    Vector srcs = nodeSupport.getParameterBlock().getSources();
    return srcs == null ? null : (Vector)srcs.clone();
  }
  
















  public synchronized void setSources(List sourceList)
  {
    if (sourceList == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    ParameterBlock pb = (ParameterBlock)nodeSupport.getParameterBlock().clone();
    

    Iterator it = pb.getSources().iterator();
    while (it.hasNext()) {
      Object priorSource = it.next();
      if (!sourceList.contains(priorSource)) {
        if ((priorSource instanceof PlanarImage)) {
          ((PlanarImage)priorSource).removeSink(this);
        } else if ((priorSource instanceof CollectionImage)) {
          ((CollectionImage)priorSource).removeSink(this);
        }
      }
    }
    
    pb.removeSources();
    
    int size = sourceList.size();
    for (int i = 0; i < size; i++) {
      Object src = sourceList.get(i);
      pb.addSource(src);
      if ((src instanceof PlanarImage)) {
        ((PlanarImage)src).addSink(this);
      } else if ((src instanceof CollectionImage)) {
        ((CollectionImage)src).addSink(this);
      }
    }
    
    nodeSupport.setParameterBlock(pb);
  }
  









  public synchronized void removeSources()
  {
    ParameterBlock pb = (ParameterBlock)nodeSupport.getParameterBlock().clone();
    
    Iterator it = pb.getSources().iterator();
    while (it.hasNext()) {
      Object priorSource = it.next();
      if ((priorSource instanceof PlanarImage)) {
        ((PlanarImage)priorSource).removeSink(this);
      } else if ((priorSource instanceof CollectionImage)) {
        ((CollectionImage)priorSource).removeSink(this);
      }
      it.remove();
    }
    nodeSupport.setParameterBlock(pb);
  }
  
























  public synchronized void addSink(PlanarImage sink)
  {
    if (sink == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    super.addSink(sink);
  }
  























  public synchronized boolean removeSink(PlanarImage sink)
  {
    if (sink == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    return super.removeSink(sink);
  }
  




  public void removeSinks()
  {
    super.removeSinks();
  }
  











  public boolean addSink(Object sink)
  {
    if (sink == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    return super.addSink(sink);
  }
  










  public boolean removeSink(Object sink)
  {
    if (sink == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    return super.removeSink(sink);
  }
  





























  public Point2D mapDestPoint(Point2D destPt, int sourceIndex)
  {
    if (destPt == null)
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    if ((sourceIndex < 0) || (sourceIndex >= getNumSources())) {
      throw new IndexOutOfBoundsException(JaiI18N.getString("Generic1"));
    }
    
    createRendering();
    
    if ((theImage != null) && ((theImage instanceof OpImage))) {
      return ((OpImage)theImage).mapDestPoint(destPt, sourceIndex);
    }
    
    return destPt;
  }
  




























  public Point2D mapSourcePoint(Point2D sourcePt, int sourceIndex)
  {
    if (sourcePt == null)
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    if ((sourceIndex < 0) || (sourceIndex >= getNumSources())) {
      throw new IndexOutOfBoundsException(JaiI18N.getString("Generic1"));
    }
    
    createRendering();
    
    if ((theImage != null) && ((theImage instanceof OpImage))) {
      return ((OpImage)theImage).mapSourcePoint(sourcePt, sourceIndex);
    }
    
    return sourcePt;
  }
  














  public synchronized void dispose()
  {
    if (isDisposed) {
      return;
    }
    
    isDisposed = true;
    
    if (theImage != null) {
      theImage.dispose();
    }
    
    super.dispose();
  }
  


  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.defaultWriteObject();
    

    out.writeObject(eventManager);
    out.writeObject(properties);
  }
  




  private synchronized void readObject(ObjectInputStream in)
    throws IOException, ClassNotFoundException
  {
    in.defaultReadObject();
    

    eventManager = ((PropertyChangeSupportJAI)in.readObject());
    properties = ((WritablePropertySourceImpl)in.readObject());
    

    OperationDescriptor odesc = (OperationDescriptor)getRegistry().getDescriptor("rendered", nodeSupport.getOperationName());
    


    if (odesc.isImmediate()) {
      createRendering();
    }
  }
  
  void sendExceptionToListener(String message, Exception e) {
    ImagingListener listener = (ImagingListener)getRenderingHints().get(JAI.KEY_IMAGING_LISTENER);
    

    listener.errorOccurred(message, e, this, false);
  }
}
