package com.sun.media.jai.rmi;

import com.sun.media.jai.util.ImageUtil;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;
import java.util.Vector;
import javax.media.jai.PlanarImage;
import javax.media.jai.PropertySource;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;
import javax.media.jai.remote.RemoteImagingException;
import javax.media.jai.remote.SerializableRenderedImage;
import javax.media.jai.util.ImagingListener;



























































public class RMIImageImpl
  implements RMIImage
{
  public static final Object NULL_PROPERTY = new NullPropertyTag();
  

  private static long idCounter = 0L;
  




  private static Hashtable sources = null;
  




  private static Hashtable propertySources = null;
  









  private static synchronized void addSource(Long id, RenderedImage source, PropertySource ps)
  {
    if (sources == null) {
      sources = new Hashtable();
      propertySources = new Hashtable();
    }
    

    sources.put(id, source);
    propertySources.put(id, ps);
  }
  




  private static PlanarImage getSource(Long id)
    throws RemoteException
  {
    Object obj = null;
    if ((sources == null) || ((obj = sources.get(id)) == null))
    {
      throw new RemoteException(JaiI18N.getString("RMIImageImpl2"));
    }
    
    return (PlanarImage)obj;
  }
  





  private static PropertySource getPropertySource(Long id)
    throws RemoteException
  {
    Object obj = null;
    if ((propertySources == null) || ((obj = propertySources.get(id)) == null))
    {
      throw new RemoteException(JaiI18N.getString("RMIImageImpl2"));
    }
    
    return (PropertySource)obj;
  }
  


  public RMIImageImpl()
    throws RemoteException
  {
    try
    {
      UnicastRemoteObject.exportObject(this);
    } catch (RemoteException e) {
      ImagingListener listener = ImageUtil.getImagingListener((RenderingHints)null);
      
      String message = JaiI18N.getString("RMIImageImpl0");
      listener.errorOccurred(message, new RemoteImagingException(message, e), this, false);
    }
  }
  











  public synchronized Long getRemoteID()
    throws RemoteException
  {
    return new Long(++idCounter);
  }
  












  public void setSource(Long id, RenderedImage source)
    throws RemoteException
  {
    PlanarImage pi = PlanarImage.wrapRenderedImage(source);
    addSource(id, pi, pi);
  }
  









  public void setSource(Long id, RenderedOp source)
    throws RemoteException
  {
    addSource(id, source.getRendering(), source);
  }
  






  public void setSource(Long id, RenderableOp source, RenderContextProxy renderContextProxy)
    throws RemoteException
  {
    RenderContext renderContext = renderContextProxy.getRenderContext();
    
    RenderedImage r = source.createRendering(renderContext);
    PlanarImage pi = PlanarImage.wrapRenderedImage(r);
    addSource(id, pi, pi);
  }
  


  public void dispose(Long id)
    throws RemoteException
  {
    if (sources != null) {
      sources.remove(id);
      propertySources.remove(id);
    }
  }
  
  public Object getProperty(Long id, String name)
    throws RemoteException
  {
    PropertySource ps = getPropertySource(id);
    Object property = ps.getProperty(name);
    if ((property == null) || (property.equals(Image.UndefinedProperty)))
    {
      property = NULL_PROPERTY;
    }
    return property;
  }
  



  public String[] getPropertyNames(Long id)
    throws RemoteException
  {
    PropertySource ps = getPropertySource(id);
    return ps.getPropertyNames();
  }
  
  public int getMinX(Long id) throws RemoteException
  {
    return getSource(id).getMinX();
  }
  
  public int getMaxX(Long id) throws RemoteException
  {
    return getSource(id).getMaxX();
  }
  
  public int getMinY(Long id) throws RemoteException
  {
    return getSource(id).getMinY();
  }
  
  public int getMaxY(Long id) throws RemoteException
  {
    return getSource(id).getMaxY();
  }
  
  public int getWidth(Long id) throws RemoteException
  {
    return getSource(id).getWidth();
  }
  
  public int getHeight(Long id) throws RemoteException
  {
    return getSource(id).getHeight();
  }
  
  public int getTileWidth(Long id) throws RemoteException
  {
    return getSource(id).getTileWidth();
  }
  
  public int getTileHeight(Long id) throws RemoteException
  {
    return getSource(id).getTileHeight();
  }
  

  public int getTileGridXOffset(Long id)
    throws RemoteException
  {
    return getSource(id).getTileGridXOffset();
  }
  

  public int getTileGridYOffset(Long id)
    throws RemoteException
  {
    return getSource(id).getTileGridYOffset();
  }
  
  public int getMinTileX(Long id) throws RemoteException
  {
    return getSource(id).getMinTileX();
  }
  


  public int getNumXTiles(Long id)
    throws RemoteException
  {
    return getSource(id).getNumXTiles();
  }
  
  public int getMinTileY(Long id) throws RemoteException
  {
    return getSource(id).getMinTileY();
  }
  


  public int getNumYTiles(Long id)
    throws RemoteException
  {
    return getSource(id).getNumYTiles();
  }
  
  public int getMaxTileX(Long id) throws RemoteException
  {
    return getSource(id).getMaxTileX();
  }
  
  public int getMaxTileY(Long id) throws RemoteException
  {
    return getSource(id).getMaxTileY();
  }
  
  public SampleModelProxy getSampleModel(Long id) throws RemoteException
  {
    return new SampleModelProxy(getSource(id).getSampleModel());
  }
  
  public ColorModelProxy getColorModel(Long id) throws RemoteException
  {
    return new ColorModelProxy(getSource(id).getColorModel());
  }
  




  public Vector getSources(Long id)
    throws RemoteException
  {
    Vector sourceVector = getSource(id).getSources();
    int size = sourceVector.size();
    boolean isCloned = false;
    for (int i = 0; i < size; i++) {
      RenderedImage img = (RenderedImage)sourceVector.get(i);
      if (!(img instanceof Serializable)) {
        if (!isCloned) {
          sourceVector = (Vector)sourceVector.clone();
        }
        sourceVector.set(i, new SerializableRenderedImage(img, false));
      }
    }
    return sourceVector;
  }
  
  public Rectangle getBounds(Long id) throws RemoteException
  {
    return getSource(id).getBounds();
  }
  









  public RasterProxy getTile(Long id, int tileX, int tileY)
    throws RemoteException
  {
    return new RasterProxy(getSource(id).getTile(tileX, tileY));
  }
  



  public RasterProxy getData(Long id)
    throws RemoteException
  {
    return new RasterProxy(getSource(id).getData());
  }
  








  public RasterProxy getData(Long id, Rectangle bounds)
    throws RemoteException
  {
    RasterProxy rp = null;
    if (bounds == null) {
      rp = getData(id);
    } else {
      bounds = bounds.intersection(getBounds(id));
      rp = new RasterProxy(getSource(id).getData(bounds));
    }
    return rp;
  }
  



  public RasterProxy copyData(Long id, Rectangle bounds)
    throws RemoteException
  {
    return getData(id, bounds);
  }
  


















  public static void main(String[] args)
  {
    if (System.getSecurityManager() == null) {
      System.setSecurityManager(new RMISecurityManager());
    }
    

    String host = null;
    int port = 1099;
    for (int i = 0; i < args.length; i++) {
      if (args[i].equalsIgnoreCase("-host")) {
        host = args[(++i)];
      } else if (args[i].equalsIgnoreCase("-port")) {
        port = Integer.parseInt(args[(++i)]);
      }
    }
    

    if (host == null) {
      try {
        host = InetAddress.getLocalHost().getHostAddress();
      } catch (UnknownHostException e) {
        System.err.println(JaiI18N.getString("RMIImageImpl1") + e.getMessage());
        
        e.printStackTrace();
      }
    }
    
    System.out.println(JaiI18N.getString("RMIImageImpl3") + " " + host + ":" + port);
    
    try
    {
      RMIImageImpl im = new RMIImageImpl();
      String serverName = new String("rmi://" + host + ":" + port + "/" + "RemoteImageServer");
      


      System.out.println(JaiI18N.getString("RMIImageImpl4") + " \"" + serverName + "\".");
      
      Naming.rebind(serverName, im);
      System.out.println(JaiI18N.getString("RMIImageImpl5"));
    } catch (Exception e) {
      System.err.println(JaiI18N.getString("RMIImageImpl0") + e.getMessage());
      
      e.printStackTrace();
    }
  }
}
