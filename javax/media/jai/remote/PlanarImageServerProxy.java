package javax.media.jai.remote;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.io.PrintStream;
import java.util.Vector;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.OperationRegistry;
import javax.media.jai.PlanarImage;
import javax.media.jai.TileCache;
import javax.media.jai.util.ImagingListener;






























































































public abstract class PlanarImageServerProxy
  extends PlanarImage
  implements RemoteRenderedImage
{
  protected int retryInterval;
  protected int numRetries;
  protected transient TileCache cache;
  protected Object tileCacheMetric;
  protected transient OperationRegistry registry;
  protected String serverName;
  protected String protocolName;
  protected String operationName;
  protected ParameterBlock paramBlock;
  protected RenderingHints hints;
  private ImageLayout layout = null;
  



  protected NegotiableCapabilitySet preferences;
  



  protected NegotiableCapabilitySet negotiated;
  


  NegotiableCapabilitySet serverCapabilities;
  


  NegotiableCapabilitySet clientCapabilities;
  



  private static void checkLayout(ImageLayout layout)
  {
    if (layout == null) {
      throw new IllegalArgumentException("layout is null.");
    }
    
    if (layout.getValidMask() != 1023) {
      throw new Error(JaiI18N.getString("PlanarImageServerProxy3"));
    }
  }
  






























  public PlanarImageServerProxy(String serverName, String protocolName, String operationName, ParameterBlock paramBlock, RenderingHints hints)
  {
    super(null, null, null);
    
    if (operationName == null) {
      throw new IllegalArgumentException(JaiI18N.getString("PlanarImageServerProxy1"));
    }
    

    this.serverName = serverName;
    this.protocolName = protocolName;
    this.operationName = operationName;
    this.paramBlock = paramBlock;
    this.hints = hints;
    
    if (hints == null)
    {
      registry = JAI.getDefaultInstance().getOperationRegistry();
      cache = JAI.getDefaultInstance().getTileCache();
      retryInterval = 1000;
      numRetries = 5;
      


      setNegotiationPreferences(null);
    }
    else {
      registry = ((OperationRegistry)hints.get(JAI.KEY_OPERATION_REGISTRY));
      
      if (registry == null) {
        registry = JAI.getDefaultInstance().getOperationRegistry();
      }
      
      cache = ((TileCache)hints.get(JAI.KEY_TILE_CACHE));
      if (cache == null) {
        cache = JAI.getDefaultInstance().getTileCache();
      }
      
      Integer integer = (Integer)hints.get(JAI.KEY_RETRY_INTERVAL);
      if (integer == null) {
        retryInterval = 1000;
      } else {
        retryInterval = integer.intValue();
      }
      
      integer = (Integer)hints.get(JAI.KEY_NUM_RETRIES);
      if (integer == null) {
        numRetries = 5;
      } else {
        numRetries = integer.intValue();
      }
      
      tileCacheMetric = hints.get(JAI.KEY_TILE_CACHE_METRIC);
      

      setNegotiationPreferences((NegotiableCapabilitySet)hints.get(JAI.KEY_NEGOTIATION_PREFERENCES));
    }
    

    if (paramBlock != null) {
      setSources(paramBlock.getSources());
    }
  }
  


  public String getServerName()
  {
    return serverName;
  }
  



  public String getProtocolName()
  {
    return protocolName;
  }
  


  public String getOperationName()
  {
    return operationName;
  }
  




  public ParameterBlock getParameterBlock()
  {
    return paramBlock;
  }
  



  public RenderingHints getRenderingHints()
  {
    return hints;
  }
  




  public TileCache getTileCache()
  {
    return cache;
  }
  










  public void setTileCache(TileCache cache)
  {
    if (this.cache != null) {
      this.cache.removeTiles(this);
    }
    this.cache = cache;
  }
  


  public Object getTileCacheMetric()
  {
    return tileCacheMetric;
  }
  













  public abstract ImageLayout getImageLayout()
    throws RemoteImagingException;
  













  public abstract Object getRemoteProperty(String paramString)
    throws RemoteImagingException;
  













  public abstract String[] getRemotePropertyNames()
    throws RemoteImagingException;
  













  public abstract Rectangle mapSourceRect(Rectangle paramRectangle, int paramInt)
    throws RemoteImagingException;
  













  public abstract Rectangle mapDestRect(Rectangle paramRectangle, int paramInt)
    throws RemoteImagingException;
  













  public abstract Raster computeTile(int paramInt1, int paramInt2)
    throws RemoteImagingException;
  












  public int getRetryInterval()
  {
    return retryInterval;
  }
  






  public void setRetryInterval(int retryInterval)
  {
    if (retryInterval < 0) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic3"));
    }
    this.retryInterval = retryInterval;
  }
  


  public int getNumRetries()
  {
    return numRetries;
  }
  






  public void setNumRetries(int numRetries)
  {
    if (numRetries < 0) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic4"));
    }
    this.numRetries = numRetries;
  }
  



  public int getMinX()
  {
    requestLayout();
    return minX;
  }
  




  public int getMaxX()
  {
    requestLayout();
    return minX + width;
  }
  



  public int getMinY()
  {
    requestLayout();
    return minY;
  }
  




  public int getMaxY()
  {
    requestLayout();
    return minY + height;
  }
  



  public int getWidth()
  {
    requestLayout();
    return width;
  }
  



  public int getHeight()
  {
    requestLayout();
    return height;
  }
  



  public int getTileWidth()
  {
    requestLayout();
    return tileWidth;
  }
  



  public int getTileHeight()
  {
    requestLayout();
    return tileHeight;
  }
  



  public int getTileGridXOffset()
  {
    requestLayout();
    return tileGridXOffset;
  }
  



  public int getTileGridYOffset()
  {
    requestLayout();
    return tileGridYOffset;
  }
  



  public SampleModel getSampleModel()
  {
    requestLayout();
    return sampleModel;
  }
  



  public ColorModel getColorModel()
  {
    requestLayout();
    return colorModel;
  }
  








  private ImageLayout requestLayout()
  {
    if (layout != null) {
      return layout;
    }
    Exception rieSave = null;
    int count = 0;
    while (count++ < numRetries) {
      try {
        layout = getImageLayout();
        
        checkLayout(layout);
        

        minX = layout.getMinX(null);
        minY = layout.getMinY(null);
        width = layout.getWidth(null);
        height = layout.getHeight(null);
        tileWidth = layout.getTileWidth(null);
        tileHeight = layout.getTileHeight(null);
        tileGridXOffset = layout.getTileGridXOffset(null);
        tileGridYOffset = layout.getTileGridYOffset(null);
        sampleModel = layout.getSampleModel(null);
        colorModel = layout.getColorModel(null);
      }
      catch (RemoteImagingException e) {
        System.err.println(JaiI18N.getString("PlanarImageServerProxy0"));
        
        rieSave = e;
        try
        {
          Thread.sleep(retryInterval);
        }
        catch (InterruptedException f) {}
      }
    }
    
    if (layout == null) {
      sendExceptionToListener(rieSave);
    }
    
    return layout;
  }
  


















  public Object getProperty(String name)
  {
    Object property = super.getProperty(name);
    
    if ((property == null) || (property == Image.UndefinedProperty))
    {
      Exception rieSave = null;
      int count = 0;
      while (count++ < numRetries) {
        try {
          property = getRemoteProperty(name);
          if (property != Image.UndefinedProperty) {
            setProperty(name, property);
          }
          return property;
        } catch (RemoteImagingException rie) {
          System.err.println(JaiI18N.getString("PlanarImageServerProxy0"));
          
          rieSave = rie;
          try {
            Thread.sleep(retryInterval);
          }
          catch (InterruptedException ie) {}
        }
      }
      
      sendExceptionToListener(rieSave);
      return property;
    }
    return property;
  }
  


















  public String[] getPropertyNames()
  {
    String[] localPropertyNames = super.getPropertyNames();
    
    Vector names = new Vector();
    
    if (localPropertyNames != null) {
      for (int i = 0; i < localPropertyNames.length; i++) {
        names.add(localPropertyNames[i]);
      }
    }
    
    int count = 0;
    String[] remotePropertyNames = null;
    Exception rieSave = null;
    
    while (count++ < numRetries) {
      try {
        remotePropertyNames = getRemotePropertyNames();
      }
      catch (RemoteImagingException rie) {
        System.err.println(JaiI18N.getString("PlanarImageServerProxy0"));
        
        rieSave = rie;
        try {
          Thread.sleep(retryInterval);
        }
        catch (InterruptedException ie) {}
      }
    }
    
    if (count > numRetries) {
      sendExceptionToListener(rieSave);
    }
    

    if (remotePropertyNames != null) {
      for (int i = 0; i < remotePropertyNames.length; i++) {
        if (!names.contains(remotePropertyNames[i])) {
          names.add(remotePropertyNames[i]);
        }
      }
    }
    

    String[] propertyNames = names.size() == 0 ? null : (String[])names.toArray(new String[names.size()]);
    

    return propertyNames;
  }
  













  public Raster getTile(int tileX, int tileY)
  {
    Raster tile = null;
    

    if ((tileX >= getMinTileX()) && (tileX <= getMaxTileX()) && (tileY >= getMinTileY()) && (tileY <= getMaxTileY()))
    {


      tile = cache != null ? cache.getTile(this, tileX, tileY) : null;
      
      if (tile == null)
      {
        int count = 0;
        Exception rieSave = null;
        while (count++ < numRetries) {
          try {
            tile = computeTile(tileX, tileY);
          }
          catch (RemoteImagingException rie) {
            System.err.println(JaiI18N.getString("PlanarImageServerProxy0"));
            
            rieSave = rie;
            try {
              Thread.sleep(retryInterval);
            }
            catch (InterruptedException ie) {}
          }
        }
        

        if (count > numRetries) {
          sendExceptionToListener(rieSave);
        }
        

        if (cache != null) {
          cache.add(this, tileX, tileY, tile, tileCacheMetric);
        }
      }
    }
    
    return tile;
  }
  

  protected void finalize()
    throws Throwable
  {
    if (cache != null) {
      cache.removeTiles(this);
    }
    super.finalize();
  }
  







  public NegotiableCapabilitySet getNegotiationPreferences()
  {
    return preferences;
  }
  






















  public void setNegotiationPreferences(NegotiableCapabilitySet preferences)
  {
    this.preferences = preferences;
    


    negotiated = null;
    
    getNegotiatedValues();
  }
  







  public synchronized NegotiableCapabilitySet getNegotiatedValues()
    throws RemoteImagingException
  {
    if (negotiated == null)
    {


      getCapabilities();
      

      negotiated = RemoteJAI.negotiate(preferences, serverCapabilities, clientCapabilities);
      



      setServerNegotiatedValues(negotiated);
    }
    
    return negotiated;
  }
  













  public NegotiableCapability getNegotiatedValue(String category)
    throws RemoteImagingException
  {
    if (negotiated == null)
    {
      getCapabilities();
      

      return RemoteJAI.negotiate(preferences, serverCapabilities, clientCapabilities, category);
    }
    





    return negotiated.getNegotiatedValue(category);
  }
  


  private void getCapabilities()
  {
    String mode = "remoteRendered";
    if (serverCapabilities == null)
    {
      RemoteDescriptor desc = (RemoteDescriptor)registry.getDescriptor(mode, protocolName);
      

      int count = 0;
      Exception rieSave = null;
      while (count++ < numRetries) {
        try {
          serverCapabilities = desc.getServerCapabilities(serverName);
        }
        catch (RemoteImagingException rie)
        {
          System.err.println(JaiI18N.getString("PlanarImageServerProxy0"));
          
          rieSave = rie;
          try {
            Thread.sleep(retryInterval);
          }
          catch (InterruptedException ie) {}
        }
      }
      
      if (count > numRetries) {
        sendExceptionToListener(rieSave);
      }
    }
    
    if (clientCapabilities == null) {
      RemoteRIF rrif = (RemoteRIF)registry.getFactory(mode, protocolName);
      

      clientCapabilities = rrif.getClientCapabilities();
    }
  }
  
  void sendExceptionToListener(Exception e) {
    ImagingListener listener = null;
    if (hints != null) {
      listener = (ImagingListener)hints.get(JAI.KEY_IMAGING_LISTENER);
    } else
      listener = JAI.getDefaultInstance().getImagingListener();
    String message = JaiI18N.getString("PlanarImageServerProxy2");
    listener.errorOccurred(message, new RemoteImagingException(message, e), this, false);
  }
}
