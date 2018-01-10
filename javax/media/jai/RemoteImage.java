package javax.media.jai;

import com.sun.media.jai.rmi.ColorModelProxy;
import com.sun.media.jai.rmi.RMIImage;
import com.sun.media.jai.rmi.RMIImageImpl;
import com.sun.media.jai.rmi.RasterProxy;
import com.sun.media.jai.rmi.RenderContextProxy;
import com.sun.media.jai.rmi.SampleModelProxy;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.RenderContext;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Vector;
import javax.media.jai.remote.SerializableRenderedImage;













































/**
 * @deprecated
 */
public class RemoteImage
  extends PlanarImage
{
  static final int DEFAULT_TIMEOUT = 1000;
  static final int DEFAULT_NUM_RETRIES = 5;
  static final int VAR_MIN_X = 0;
  static final int VAR_MIN_Y = 1;
  static final int VAR_WIDTH = 2;
  static final int VAR_HEIGHT = 3;
  static final int VAR_TILE_WIDTH = 4;
  static final int VAR_TILE_HEIGHT = 5;
  static final int VAR_TILE_GRID_X_OFFSET = 6;
  static final int VAR_TILE_GRID_Y_OFFSET = 7;
  static final int VAR_SAMPLE_MODEL = 8;
  static final int VAR_COLOR_MODEL = 9;
  static final int VAR_SOURCES = 10;
  static final int NUM_VARS = 11;
  private static final Class NULL_PROPERTY_CLASS = RMIImageImpl.NULL_PROPERTY.getClass();
  


  protected RMIImage remoteImage;
  

  private Long id = null;
  

  protected boolean[] fieldValid = new boolean[11];
  

  protected String[] propertyNames = null;
  

  protected int timeout = 1000;
  

  protected int numRetries = 5;
  

  private Rectangle imageBounds = null;
  
  private static Vector vectorize(RenderedImage image) {
    Vector v = new Vector(1);
    v.add(image);
    return v;
  }
  



























  public RemoteImage(String serverName, RenderedImage source)
  {
    super(null, null, null);
    
    if (serverName == null) {
      serverName = getLocalHostAddress();
    }
    


    int index = serverName.indexOf("::");
    boolean remoteChainingHack = index != -1;
    
    if ((!remoteChainingHack) && (source == null))
    {
      throw new IllegalArgumentException(JaiI18N.getString("RemoteImage1"));
    }
    

    if (remoteChainingHack)
    {

      id = Long.valueOf(serverName.substring(index + 2));
      serverName = serverName.substring(0, index);
    }
    

    getRMIImage(serverName);
    
    if (!remoteChainingHack)
    {
      getRMIID();
    }
    

    setRMIProperties(serverName);
    
    if (source != null) {
      try {
        if ((source instanceof Serializable)) {
          remoteImage.setSource(id, source);
        } else {
          remoteImage.setSource(id, new SerializableRenderedImage(source));
        }
      }
      catch (RemoteException e)
      {
        throw new RuntimeException(e.getMessage());
      }
    }
  }
  



























  public RemoteImage(String serverName, RenderedOp source)
  {
    super(null, null, null);
    
    if (serverName == null) {
      serverName = getLocalHostAddress();
    }
    if (source == null) {
      throw new IllegalArgumentException(JaiI18N.getString("RemoteImage1"));
    }
    


    getRMIImage(serverName);
    

    getRMIID();
    

    setRMIProperties(serverName);
    try
    {
      remoteImage.setSource(id, source);
    } catch (RemoteException e) {
      throw new RuntimeException(e.getMessage());
    }
  }
  



























  public RemoteImage(String serverName, RenderableOp source, RenderContext renderContext)
  {
    super(null, null, null);
    
    if (serverName == null) {
      serverName = getLocalHostAddress();
    }
    if (source == null) {
      throw new IllegalArgumentException(JaiI18N.getString("RemoteImage1"));
    }
    

    if (renderContext == null) {
      renderContext = new RenderContext(new AffineTransform());
    }
    

    getRMIImage(serverName);
    

    getRMIID();
    

    setRMIProperties(serverName);
    

    RenderContextProxy rcp = new RenderContextProxy(renderContext);
    try
    {
      remoteImage.setSource(id, source, rcp);
    } catch (RemoteException e) {
      e.printStackTrace();
      throw new RuntimeException(e.getMessage());
    }
  }
  
















  private void getRMIImage(String serverName)
  {
    if (serverName == null) {
      serverName = getLocalHostAddress();
    }
    
    String serviceName = new String("rmi://" + serverName + "/" + "RemoteImageServer");
    


    remoteImage = null;
    try {
      remoteImage = ((RMIImage)Naming.lookup(serviceName));
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }
  }
  




  private String getLocalHostAddress()
  {
    try
    {
      serverName = InetAddress.getLocalHost().getHostAddress();
    } catch (Exception e) { String serverName;
      throw new RuntimeException(e.getMessage()); }
    String serverName;
    return serverName;
  }
  

  private void getRMIID()
  {
    try
    {
      id = remoteImage.getRemoteID();
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }
  }
  





  private void setRMIProperties(String serverName)
  {
    setProperty(getClass().getName() + ".serverName", serverName);
    setProperty(getClass().getName() + ".id", id);
  }
  

  protected void finalize()
  {
    try
    {
      remoteImage.dispose(id);
    }
    catch (Exception e) {}
  }
  






  public void setTimeout(int timeout)
  {
    if (timeout > 0) {
      this.timeout = timeout;
    }
  }
  


  public int getTimeout()
  {
    return timeout;
  }
  





  public void setNumRetries(int numRetries)
  {
    if (numRetries > 0) {
      this.numRetries = numRetries;
    }
  }
  


  public int getNumRetries()
  {
    return numRetries;
  }
  












  protected void requestField(int fieldIndex, int retries, int timeout)
  {
    if (retries < 0)
      throw new IllegalArgumentException(JaiI18N.getString("RemoteImage3"));
    if (timeout < 0) {
      throw new IllegalArgumentException(JaiI18N.getString("RemoteImage4"));
    }
    
    int count = 0;
    
    if (fieldValid[fieldIndex] != 0) {
      return;
    }
    while (count++ < retries) {
      try {
        switch (fieldIndex) {
        case 0: 
          minX = remoteImage.getMinX(id);
          break;
        case 1: 
          minY = remoteImage.getMinY(id);
          break;
        case 2: 
          width = remoteImage.getWidth(id);
          break;
        case 3: 
          height = remoteImage.getHeight(id);
          break;
        case 4: 
          tileWidth = remoteImage.getTileWidth(id);
          break;
        case 5: 
          tileHeight = remoteImage.getTileHeight(id);
          break;
        case 6: 
          tileGridXOffset = remoteImage.getTileGridXOffset(id);
          break;
        case 7: 
          tileGridYOffset = remoteImage.getTileGridYOffset(id);
          break;
        case 8: 
          sampleModel = remoteImage.getSampleModel(id).getSampleModel();
          
          break;
        case 9: 
          colorModel = remoteImage.getColorModel(id).getColorModel();
          break;
        
        case 10: 
          Vector localSources = remoteImage.getSources(id);
          int numSources = localSources.size();
          for (int i = 0; i < numSources; i++) {
            RenderedImage src = (RenderedImage)localSources.get(i);
            
            addSource(PlanarImage.wrapRenderedImage(src));
          }
        }
        
        

        fieldValid[fieldIndex] = true;
        return;
      } catch (RemoteException e) {
        System.err.println(JaiI18N.getString("RemoteImage0"));
        try {
          Thread.sleep(timeout);
        }
        catch (InterruptedException f) {}
      }
    }
  }
  







  protected void requestField(int fieldIndex)
  {
    requestField(fieldIndex, numRetries, timeout);
  }
  


  public int getMinX()
  {
    requestField(0);
    return minX;
  }
  



  public int getMaxX()
  {
    requestField(0);
    requestField(2);
    return minX + width;
  }
  
  public int getMinY()
  {
    requestField(1);
    return minY;
  }
  



  public int getMaxY()
  {
    requestField(1);
    requestField(3);
    return minY + height;
  }
  
  public int getWidth()
  {
    requestField(2);
    return width;
  }
  
  public int getHeight()
  {
    requestField(3);
    return height;
  }
  
  public int getTileWidth()
  {
    requestField(4);
    return tileWidth;
  }
  
  public int getTileHeight()
  {
    requestField(5);
    return tileHeight;
  }
  
  public int getTileGridXOffset()
  {
    requestField(6);
    return tileGridXOffset;
  }
  
  public int getTileGridYOffset()
  {
    requestField(7);
    return tileGridYOffset;
  }
  
  public SampleModel getSampleModel()
  {
    requestField(8);
    return sampleModel;
  }
  
  public ColorModel getColorModel()
  {
    requestField(9);
    return colorModel;
  }
  




  public Vector getSources()
  {
    requestField(10);
    return super.getSources();
  }
  












  public Object getProperty(String name)
  {
    Object property = super.getProperty(name);
    
    if ((property == null) || (property == Image.UndefinedProperty))
    {
      int count = 0;
      while (count++ < numRetries) {
        try {
          property = remoteImage.getProperty(id, name);
          if (NULL_PROPERTY_CLASS.isInstance(property)) {
            property = Image.UndefinedProperty;
          }
        }
        catch (RemoteException e) {
          try {
            Thread.sleep(timeout);
          }
          catch (InterruptedException f) {}
        }
      }
      
      if (property == null) {
        property = Image.UndefinedProperty;
      }
      
      if (property != Image.UndefinedProperty) {
        setProperty(name, property);
      }
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
    while (count++ < numRetries) {
      try {
        remotePropertyNames = remoteImage.getPropertyNames(id);
      }
      catch (RemoteException e) {
        try {
          Thread.sleep(timeout);
        }
        catch (InterruptedException f) {}
      }
    }
    

    if (remotePropertyNames != null) {
      for (int i = 0; i < remotePropertyNames.length; i++) {
        if (!names.contains(remotePropertyNames[i])) {
          names.add(remotePropertyNames[i]);
        }
      }
    }
    

    propertyNames = (names.size() == 0 ? null : (String[])names.toArray(new String[names.size()]));
    

    return propertyNames;
  }
  







  public Raster getTile(int x, int y)
  {
    int count = 0;
    
    while (count++ < numRetries) {
      try {
        RasterProxy rp = remoteImage.getTile(id, x, y);
        return rp.getRaster();
      } catch (RemoteException e) {
        try {
          Thread.sleep(timeout);
        }
        catch (InterruptedException f) {}
      }
    }
    return null;
  }
  


  public Raster getData()
  {
    int count = 0;
    
    while (count++ < numRetries) {
      try {
        RasterProxy rp = remoteImage.getData(id);
        return rp.getRaster();
      } catch (RemoteException e) {
        try {
          Thread.sleep(timeout);
        }
        catch (InterruptedException f) {}
      }
    }
    return null;
  }
  













  public Raster getData(Rectangle rect)
  {
    if (imageBounds == null) {
      imageBounds = getBounds();
    }
    if (rect == null) {
      rect = imageBounds;
    } else if (!rect.intersects(imageBounds)) {
      throw new IllegalArgumentException(JaiI18N.getString("RemoteImage2"));
    }
    
    int count = 0;
    
    while (count++ < numRetries) {
      try {
        RasterProxy rp = remoteImage.getData(id, rect);
        return rp.getRaster();
      } catch (RemoteException e) {
        try {
          Thread.sleep(timeout);
        }
        catch (InterruptedException f) {}
      }
    }
    return null;
  }
  










  public WritableRaster copyData(WritableRaster raster)
  {
    int count = 0;
    
    Rectangle bounds = raster == null ? new Rectangle(getMinX(), getMinY(), getWidth(), getHeight()) : raster.getBounds();
    

    for (;;)
    {
      if (count++ < numRetries) {
        try {
          RasterProxy rp = remoteImage.copyData(id, bounds);
          try {
            if (raster == null) {
              raster = (WritableRaster)rp.getRaster();
            } else {
              raster.setDataElements(x, y, rp.getRaster());
            }
          }
          catch (ArrayIndexOutOfBoundsException e)
          {
            raster = null;
          }
        }
        catch (RemoteException e) {
          try {
            Thread.sleep(timeout);
          }
          catch (InterruptedException f) {}
        }
      }
    }
    return raster;
  }
}
