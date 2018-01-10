package javax.media.jai.remote;

import com.sun.media.jai.util.ImageUtil;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;
import javax.media.jai.CollectionChangeEvent;
import javax.media.jai.CollectionOp;
import javax.media.jai.JAI;
import javax.media.jai.OperationNodeSupport;
import javax.media.jai.OperationRegistry;
import javax.media.jai.PlanarImage;
import javax.media.jai.PropertyChangeEventJAI;
import javax.media.jai.PropertyChangeSupportJAI;
import javax.media.jai.PropertySourceChangeEvent;
import javax.media.jai.RegistryMode;
import javax.media.jai.RenderedOp;
import javax.media.jai.RenderingChangeEvent;
import javax.media.jai.TileCache;
import javax.media.jai.registry.RemoteRIFRegistry;
import javax.media.jai.util.ImagingException;
import javax.media.jai.util.ImagingListener;






























































































































public class RemoteRenderedOp
  extends RenderedOp
  implements RemoteRenderedImage
{
  protected String protocolName;
  protected String serverName;
  private NegotiableCapabilitySet negotiated;
  private transient RenderingHints oldHints;
  private static Set nodeEventNames = null;
  
  static {
    nodeEventNames = new HashSet();
    nodeEventNames.add("protocolname");
    nodeEventNames.add("servername");
    nodeEventNames.add("protocolandservername");
    nodeEventNames.add("operationname");
    nodeEventNames.add("operationregistry");
    nodeEventNames.add("parameterblock");
    nodeEventNames.add("sources");
    nodeEventNames.add("parameters");
    nodeEventNames.add("renderinghints");
  }
  



































  public RemoteRenderedOp(String protocolName, String serverName, String opName, ParameterBlock pb, RenderingHints hints)
  {
    this(null, protocolName, serverName, opName, pb, hints);
  }
  









































  public RemoteRenderedOp(OperationRegistry registry, String protocolName, String serverName, String opName, ParameterBlock pb, RenderingHints hints)
  {
    super(registry, opName, pb, hints);
    
    if (protocolName == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic1"));
    }
    this.protocolName = protocolName;
    this.serverName = serverName;
    





    addPropertyChangeListener("ServerName", this);
    addPropertyChangeListener("ProtocolName", this);
    addPropertyChangeListener("ProtocolAndServerName", this);
  }
  


  public String getServerName()
  {
    return serverName;
  }
  














  public void setServerName(String serverName)
  {
    if (serverName == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic2"));
    }
    if (serverName.equalsIgnoreCase(this.serverName)) { return;
    }
    String oldServerName = this.serverName;
    this.serverName = serverName;
    fireEvent("ServerName", oldServerName, serverName);
    nodeSupport.resetPropertyEnvironment(false);
  }
  



  public String getProtocolName()
  {
    return protocolName;
  }
  




















  public void setProtocolName(String protocolName)
  {
    if (protocolName == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic1"));
    }
    if (protocolName.equalsIgnoreCase(this.protocolName)) { return;
    }
    String oldProtocolName = this.protocolName;
    this.protocolName = protocolName;
    fireEvent("ProtocolName", oldProtocolName, protocolName);
    nodeSupport.resetPropertyEnvironment(false);
  }
  



























  public void setProtocolAndServerNames(String protocolName, String serverName)
  {
    if (serverName == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic2"));
    }
    if (protocolName == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic1"));
    }
    boolean protocolNotChanged = protocolName.equalsIgnoreCase(this.protocolName);
    
    boolean serverNotChanged = serverName.equalsIgnoreCase(this.serverName);
    

    if (protocolNotChanged) {
      if (serverNotChanged)
      {
        return;
      }
      
      setServerName(serverName);
      return;
    }
    
    if (serverNotChanged)
    {
      setProtocolName(protocolName);
      return;
    }
    

    String oldProtocolName = this.protocolName;
    String oldServerName = this.serverName;
    this.protocolName = protocolName;
    this.serverName = serverName;
    

    fireEvent("ProtocolAndServerName", new String[] { oldProtocolName, oldServerName }, new String[] { protocolName, serverName });
    

    nodeSupport.resetPropertyEnvironment(false);
  }
  





  public String getRegistryModeName()
  {
    return RegistryMode.getMode("remoteRendered").getName();
  }
  




  protected synchronized PlanarImage createInstance(boolean isNodeRendered)
  {
    ParameterBlock pb = new ParameterBlock();
    pb.setParameters(getParameters());
    
    int numSources = getNumSources();
    
    for (int i = 0; i < numSources; i++)
    {
      Object source = getNodeSource(i);
      Object ai = null;
      if ((source instanceof RenderedOp))
      {
        RenderedOp src = (RenderedOp)source;
        ai = isNodeRendered ? src.getRendering() : src.createInstance();


      }
      else if (((source instanceof RenderedImage)) || ((source instanceof Collection)))
      {

        ai = source;
      } else if ((source instanceof CollectionOp)) {
        ai = ((CollectionOp)source).getCollection();
      }
      else {
        ai = source;
      }
      pb.addSource(ai);
    }
    
    RemoteRenderedImage instance = RemoteRIFRegistry.create(nodeSupport.getRegistry(), protocolName, serverName, nodeSupport.getOperationName(), pb, nodeSupport.getRenderingHints());
    







    if (instance == null) {
      throw new ImagingException(JaiI18N.getString("RemoteRenderedOp2"));
    }
    

    RenderingHints rh = nodeSupport.getRenderingHints();
    oldHints = (rh == null ? null : (RenderingHints)rh.clone());
    

    return PlanarImage.wrapRenderedImage(instance);
  }
  


















  public synchronized void propertyChange(PropertyChangeEvent evt)
  {
    Object evtSrc = evt.getSource();
    Vector nodeSources = nodeSupport.getParameterBlock().getSources();
    


    String propName = evt.getPropertyName().toLowerCase(Locale.ENGLISH);
    
    if ((theImage != null) && ((((evt instanceof PropertyChangeEventJAI)) && (evtSrc == this) && (!(evt instanceof PropertySourceChangeEvent)) && (nodeEventNames.contains(propName))) || ((((evt instanceof RenderingChangeEvent)) || ((evt instanceof CollectionChangeEvent)) || (((evt instanceof PropertyChangeEventJAI)) && ((evtSrc instanceof RenderedImage)) && (propName.equals("invalidregion")))) && (nodeSources.contains(evtSrc)))))
    {











      PlanarImage theOldImage = theImage;
      

      boolean shouldFireEvent = false;
      

      Shape invalidRegion = null;
      
      if ((evtSrc == this) && ((propName.equals("operationregistry")) || (propName.equals("protocolname")) || (propName.equals("protocolandservername"))))
      {




        shouldFireEvent = true;
        theImage = null;
      }
      else if (((evt instanceof RenderingChangeEvent)) || (((evtSrc instanceof RenderedImage)) && (propName.equals("invalidregion"))))
      {



        shouldFireEvent = true;
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
        




        if (!(theImage instanceof PlanarImageServerProxy))
        {

          theImage = null;

        }
        else
        {
          PlanarImageServerProxy oldPISP = (PlanarImageServerProxy)theImage;
          


          Rectangle srcInvalidBounds = srcInvalidRegion.getBounds();
          



          if (srcInvalidBounds.isEmpty()) {
            int x = oldPISP.tileXToX(oldPISP.getMinTileX());
            int y = oldPISP.tileYToY(oldPISP.getMinTileY());
            int w = oldPISP.getNumXTiles() * oldPISP.getTileWidth();
            
            int h = oldPISP.getNumYTiles() * oldPISP.getTileHeight();
            
            Rectangle tileBounds = new Rectangle(x, y, w, h);
            Rectangle imageBounds = oldPISP.getBounds();
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
          else
          {
            int idx = nodeSources.indexOf(evtSrc);
            

            Rectangle dstRegionBounds = oldPISP.mapSourceRect(srcInvalidBounds, idx);
            

            if (dstRegionBounds == null) {
              dstRegionBounds = oldPISP.getBounds();
            }
            

            Point[] indices = getTileIndices(dstRegionBounds);
            int numIndices = indices != null ? indices.length : 0;
            GeneralPath gp = null;
            
            for (int i = 0; i < numIndices; i++) {
              if ((i % 1000 == 0) && (gp != null)) {
                gp = new GeneralPath(new Area(gp));
              }
              Rectangle dstRect = getTileRect(x, y);
              
              Rectangle srcRect = oldPISP.mapDestRect(dstRect, idx);
              
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
          

          TileCache oldCache = oldPISP.getTileCache();
          theImage = null;
          


          if ((oldCache != null) && ((saveAllTiles) || (validTiles != null)))
          {


            newEventRendering(protocolName, oldPISP, (PropertyChangeEventJAI)evt);
            




            if (((theImage instanceof PlanarImageServerProxy)) && (((PlanarImageServerProxy)theImage).getTileCache() != null))
            {

              PlanarImageServerProxy newPISP = (PlanarImageServerProxy)theImage;
              
              TileCache newCache = newPISP.getTileCache();
              
              Object tileCacheMetric = newPISP.getTileCacheMetric();
              

              if (saveAllTiles) {
                Raster[] tiles = oldCache.getTiles(oldPISP);
                int numTiles = tiles == null ? 0 : tiles.length;
                
                for (int i = 0; i < numTiles; i++) {
                  Raster tile = tiles[i];
                  int tx = newPISP.XToTileX(tile.getMinX());
                  int ty = newPISP.YToTileY(tile.getMinY());
                  newCache.add(newPISP, tx, ty, tile, tileCacheMetric);
                }
              }
              else
              {
                int numValidTiles = validTiles.size();
                for (int i = 0; i < numValidTiles; i++) {
                  Point tileIndex = (Point)validTiles.get(i);
                  Raster tile = oldCache.getTile(oldPISP, x, y);
                  


                  if (tile != null) {
                    newCache.add(newPISP, x, y, tile, tileCacheMetric);
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
        String oldServerName = serverName;
        String newServerName = serverName;
        
        boolean checkInvalidRegion = false;
        
        if (propName.equals("operationname"))
        {
          if ((theImage instanceof PlanarImageServerProxy)) {
            newEventRendering(protocolName, (PlanarImageServerProxy)theImage, (PropertyChangeEventJAI)evt);
          }
          else
          {
            theImage = null;
            createRendering();
          }
          



          shouldFireEvent = true;



        }
        else if (propName.equals("parameterblock")) {
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
        } else if (propName.equals("servername")) {
          oldPB = newPB = nodeSupport.getParameterBlock();
          oldServerName = (String)evt.getOldValue();
          newServerName = (String)evt.getNewValue();
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
          shouldFireEvent = true;
          

          OperationRegistry registry = nodeSupport.getRegistry();
          RemoteDescriptor odesc = (RemoteDescriptor)registry.getDescriptor(RemoteDescriptor.class, protocolName);
          




          oldPB = ImageUtil.evaluateParameters(oldPB);
          newPB = ImageUtil.evaluateParameters(newPB);
          

          invalidRegion = (Shape)odesc.getInvalidRegion("rendered", oldServerName, oldPB, oldHints, newServerName, newPB, nodeSupport.getRenderingHints(), this);
          








          if ((invalidRegion == null) || (!(theImage instanceof PlanarImageServerProxy)))
          {

            theImage = null;

          }
          else
          {
            PlanarImageServerProxy oldRendering = (PlanarImageServerProxy)theImage;
            

            newEventRendering(protocolName, oldRendering, (PropertyChangeEventJAI)evt);
            



            if (((theImage instanceof PlanarImageServerProxy)) && (oldRendering.getTileCache() != null) && (((PlanarImageServerProxy)theImage).getTileCache() != null))
            {


              PlanarImageServerProxy newRendering = (PlanarImageServerProxy)theImage;
              

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
      







      if (((theOldImage instanceof PlanarImageServerProxy)) && (theImage == null))
      {
        newEventRendering(protocolName, (PlanarImageServerProxy)theOldImage, (PropertyChangeEventJAI)evt);
      }
      else
      {
        createRendering();
      }
      

      if (shouldFireEvent)
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
  





  private void newEventRendering(String protocolName, PlanarImageServerProxy oldPISP, PropertyChangeEventJAI event)
  {
    RemoteRIF rrif = (RemoteRIF)nodeSupport.getRegistry().getFactory("remoterendered", protocolName);
    
    theImage = ((PlanarImage)rrif.create(oldPISP, this, event));
  }
  


  private void fireEvent(String propName, Object oldVal, Object newVal)
  {
    if (eventManager != null) {
      Object eventSource = eventManager.getPropertyChangeEventSource();
      PropertyChangeEventJAI evt = new PropertyChangeEventJAI(eventSource, propName, oldVal, newVal);
      

      eventManager.firePropertyChange(evt);
    }
  }
  









  public int getRetryInterval()
  {
    if (theImage != null) {
      return ((RemoteRenderedImage)theImage).getRetryInterval();
    }
    RenderingHints rh = nodeSupport.getRenderingHints();
    if (rh == null) {
      return 1000;
    }
    Integer i = (Integer)rh.get(JAI.KEY_RETRY_INTERVAL);
    if (i == null) {
      return 1000;
    }
    return i.intValue();
  }
  





















  public void setRetryInterval(int retryInterval)
  {
    if (retryInterval < 0) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic3"));
    }
    if (theImage != null) {
      ((RemoteRenderedImage)theImage).setRetryInterval(retryInterval);
    }
    
    RenderingHints rh = nodeSupport.getRenderingHints();
    if (rh == null) {
      nodeSupport.setRenderingHints(new RenderingHints(null));
      rh = nodeSupport.getRenderingHints();
    }
    
    rh.put(JAI.KEY_RETRY_INTERVAL, new Integer(retryInterval));
  }
  









  public int getNumRetries()
  {
    if (theImage != null) {
      return ((RemoteRenderedImage)theImage).getNumRetries();
    }
    RenderingHints rh = nodeSupport.getRenderingHints();
    if (rh == null) {
      return 5;
    }
    Integer i = (Integer)rh.get(JAI.KEY_NUM_RETRIES);
    if (i == null) {
      return 5;
    }
    return i.intValue();
  }
  





















  public void setNumRetries(int numRetries)
  {
    if (numRetries < 0) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic4"));
    }
    
    if (theImage != null) {
      ((RemoteRenderedImage)theImage).setNumRetries(numRetries);
    }
    
    RenderingHints rh = nodeSupport.getRenderingHints();
    if (rh == null) {
      nodeSupport.setRenderingHints(new RenderingHints(null));
      rh = nodeSupport.getRenderingHints();
    }
    
    rh.put(JAI.KEY_NUM_RETRIES, new Integer(numRetries));
  }
  
































  public void setNegotiationPreferences(NegotiableCapabilitySet preferences)
  {
    if (theImage != null) {
      ((RemoteRenderedImage)theImage).setNegotiationPreferences(preferences);
    }
    

    RenderingHints rh = nodeSupport.getRenderingHints();
    
    if (preferences != null) {
      if (rh == null) {
        nodeSupport.setRenderingHints(new RenderingHints(null));
        rh = nodeSupport.getRenderingHints();
      }
      
      rh.put(JAI.KEY_NEGOTIATION_PREFERENCES, preferences);

    }
    else if (rh != null) {
      rh.remove(JAI.KEY_NEGOTIATION_PREFERENCES);
    }
    

    negotiated = negotiate(preferences);
  }
  




  public NegotiableCapabilitySet getNegotiationPreferences()
  {
    RenderingHints rh = nodeSupport.getRenderingHints();
    return rh == null ? null : (NegotiableCapabilitySet)rh.get(JAI.KEY_NEGOTIATION_PREFERENCES);
  }
  


  private NegotiableCapabilitySet negotiate(NegotiableCapabilitySet prefs)
  {
    OperationRegistry registry = nodeSupport.getRegistry();
    
    NegotiableCapabilitySet serverCap = null;
    

    RemoteDescriptor descriptor = (RemoteDescriptor)registry.getDescriptor(RemoteDescriptor.class, protocolName);
    

    if (descriptor == null) {
      Object[] msgArg0 = { new String(protocolName) };
      MessageFormat formatter = new MessageFormat("");
      formatter.setLocale(Locale.getDefault());
      formatter.applyPattern(JaiI18N.getString("RemoteJAI16"));
      throw new ImagingException(formatter.format(msgArg0));
    }
    
    int count = 0;
    int numRetries = getNumRetries();
    int retryInterval = getRetryInterval();
    
    Exception rieSave = null;
    while (count++ < numRetries) {
      try {
        serverCap = descriptor.getServerCapabilities(serverName);
      }
      catch (RemoteImagingException rie)
      {
        System.err.println(JaiI18N.getString("RemoteJAI24"));
        rieSave = rie;
        try
        {
          Thread.sleep(retryInterval);
        }
        catch (InterruptedException ie) {
          sendExceptionToListener(JaiI18N.getString("Generic5"), new ImagingException(JaiI18N.getString("Generic5"), ie));
        }
      }
    }
    

    if ((serverCap == null) && (count > numRetries)) {
      sendExceptionToListener(JaiI18N.getString("RemoteJAI18"), rieSave);
    }
    

    RemoteRIF rrif = (RemoteRIF)registry.getFactory("remoteRendered", protocolName);
    

    return RemoteJAI.negotiate(prefs, serverCap, rrif.getClientCapabilities());
  }
  















  public NegotiableCapabilitySet getNegotiatedValues()
    throws RemoteImagingException
  {
    if (theImage != null) {
      return ((RemoteRenderedImage)theImage).getNegotiatedValues();
    }
    return negotiated;
  }
  
















  public NegotiableCapability getNegotiatedValue(String category)
    throws RemoteImagingException
  {
    if (theImage != null) {
      return ((RemoteRenderedImage)theImage).getNegotiatedValue(category);
    }
    
    return negotiated == null ? null : negotiated.getNegotiatedValue(category);
  }
  













  public void setServerNegotiatedValues(NegotiableCapabilitySet negotiatedValues)
    throws RemoteImagingException
  {
    if (theImage != null) {
      ((RemoteRenderedImage)theImage).setServerNegotiatedValues(negotiatedValues);
    }
  }
  
  void sendExceptionToListener(String message, Exception e)
  {
    ImagingListener listener = (ImagingListener)getRenderingHints().get(JAI.KEY_IMAGING_LISTENER);
    

    listener.errorOccurred(message, e, this, false);
  }
}
