package com.sun.media.jai.rmi;

import com.sun.media.jai.util.ImageUtil;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Vector;
import javax.media.jai.remote.RemoteImagingException;
import javax.media.jai.remote.SerializableState;
import javax.media.jai.remote.SerializerFactory;
import javax.media.jai.util.ImagingListener;


























public class RenderableRMIServerProxy
  implements RenderableImage
{
  private String serverName;
  private String operationName;
  private ParameterBlock paramBlock;
  private ImageServer imageServer;
  public Long id;
  private static final Class NULL_PROPERTY_CLASS = JAIRMIImageServer.NULL_PROPERTY.getClass();
  





  private ImagingListener listener;
  





  public RenderableRMIServerProxy(String serverName, String operationName, ParameterBlock paramBlock, Long opID)
  {
    this.serverName = serverName;
    this.operationName = operationName;
    this.paramBlock = paramBlock;
    imageServer = getImageServer(serverName);
    id = opID;
    listener = ImageUtil.getImagingListener((RenderingHints)null);
  }
  







  public Vector getSources()
  {
    return null;
  }
  







  public Object getProperty(String name)
    throws RemoteImagingException
  {
    try
    {
      Object property = imageServer.getProperty(id, name);
      if (NULL_PROPERTY_CLASS.isInstance(property)) {}
      return Image.UndefinedProperty;
    }
    catch (RemoteException re)
    {
      String message = JaiI18N.getString("JAIRMICRIF7");
      listener.errorOccurred(message, new RemoteImagingException(message, re), this, false);
    }
    


    return null;
  }
  

  public String[] getPropertyNames()
    throws RemoteImagingException
  {
    try
    {
      return imageServer.getPropertyNames(id);
    } catch (RemoteException re) {
      String message = JaiI18N.getString("JAIRMICRIF8");
      listener.errorOccurred(message, new RemoteImagingException(message, re), this, false);
    }
    


    return null;
  }
  







  public boolean isDynamic()
    throws RemoteImagingException
  {
    try
    {
      return imageServer.isDynamic(id);
    } catch (RemoteException re) {
      String message = JaiI18N.getString("JAIRMICRIF9");
      listener.errorOccurred(message, new RemoteImagingException(message, re), this, false);
    }
    


    return true;
  }
  




  public float getWidth()
    throws RemoteImagingException
  {
    try
    {
      return imageServer.getRenderableWidth(id);
    } catch (RemoteException re) {
      String message = JaiI18N.getString("RenderableRMIServerProxy0");
      listener.errorOccurred(message, new RemoteImagingException(message, re), this, false);
    }
    


    return 0.0F;
  }
  



  public float getHeight()
    throws RemoteImagingException
  {
    try
    {
      return imageServer.getRenderableHeight(id);
    } catch (RemoteException re) {
      String message = JaiI18N.getString("RenderableRMIServerProxy0");
      listener.errorOccurred(message, new RemoteImagingException(message, re), this, false);
    }
    


    return 0.0F;
  }
  


  public float getMinX()
    throws RemoteImagingException
  {
    try
    {
      return imageServer.getRenderableMinX(id);
    } catch (RemoteException re) {
      String message = JaiI18N.getString("RenderableRMIServerProxy1");
      listener.errorOccurred(message, new RemoteImagingException(message, re), this, false);
    }
    


    return 0.0F;
  }
  


  public float getMinY()
    throws RemoteImagingException
  {
    try
    {
      return imageServer.getRenderableMinY(id);
    } catch (RemoteException re) {
      String message = JaiI18N.getString("RenderableRMIServerProxy1");
      listener.errorOccurred(message, new RemoteImagingException(message, re), this, false);
    }
    


    return 0.0F;
  }
  



  public Long getRMIID()
  {
    return id;
  }
  


  public String getServerName()
  {
    return serverName;
  }
  


  public String getOperationName()
  {
    return operationName;
  }
  



























  public RenderedImage createScaledRendering(int w, int h, RenderingHints hints)
    throws RemoteImagingException
  {
    SerializableState ss = SerializerFactory.getState(hints, null);
    try
    {
      return imageServer.createScaledRendering(id, w, h, ss);
    } catch (RemoteException re) {
      String message = JaiI18N.getString("RMIServerProxy10");
      listener.errorOccurred(message, new RemoteImagingException(message, re), this, false);
    }
    


    return null;
  }
  








  public RenderedImage createDefaultRendering()
    throws RemoteImagingException
  {
    try
    {
      return imageServer.createDefaultRendering(id);
    } catch (RemoteException re) {
      String message = JaiI18N.getString("RMIServerProxy10");
      listener.errorOccurred(message, new RemoteImagingException(message, re), this, false);
    }
    


    return null;
  }
  















  public RenderedImage createRendering(RenderContext renderContext)
    throws RemoteImagingException
  {
    SerializableState ss = SerializerFactory.getState(renderContext, null);
    try {
      return imageServer.createRendering(id, ss);
    } catch (RemoteException re) {
      String message = JaiI18N.getString("RMIServerProxy10");
      listener.errorOccurred(message, new RemoteImagingException(message, re), this, false);
    }
    


    return null;
  }
  
















  protected synchronized ImageServer getImageServer(String serverName)
  {
    if (imageServer == null)
    {
      if (serverName == null) {
        try {
          serverName = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
          String message = JaiI18N.getString("RMIServerProxy11");
          listener.errorOccurred(message, new RemoteImagingException(message, e), this, false);
        }
      }
      




      String serviceName = new String("rmi://" + serverName + "/" + "JAIRMIRemoteServer1.1");
      



      imageServer = null;
      try {
        imageServer = ((ImageServer)Naming.lookup(serviceName));
      } catch (Exception e) {
        String message = JaiI18N.getString("RMIServerProxy12");
        listener.errorOccurred(message, new RemoteImagingException(message, e), this, false);
      }
    }
    



    return imageServer;
  }
}
