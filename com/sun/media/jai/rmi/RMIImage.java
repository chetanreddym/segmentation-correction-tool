package com.sun.media.jai.rmi;

import java.awt.Rectangle;
import java.awt.image.RenderedImage;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;

public abstract interface RMIImage
  extends Remote
{
  public static final String RMI_IMAGE_SERVER_NAME = "RemoteImageServer";
  
  public abstract Long getRemoteID()
    throws RemoteException;
  
  public abstract void setSource(Long paramLong, RenderedImage paramRenderedImage)
    throws RemoteException;
  
  public abstract void setSource(Long paramLong, RenderedOp paramRenderedOp)
    throws RemoteException;
  
  public abstract void setSource(Long paramLong, RenderableOp paramRenderableOp, RenderContextProxy paramRenderContextProxy)
    throws RemoteException;
  
  public abstract void dispose(Long paramLong)
    throws RemoteException;
  
  public abstract Vector getSources(Long paramLong)
    throws RemoteException;
  
  public abstract Object getProperty(Long paramLong, String paramString)
    throws RemoteException;
  
  public abstract String[] getPropertyNames(Long paramLong)
    throws RemoteException;
  
  public abstract ColorModelProxy getColorModel(Long paramLong)
    throws RemoteException;
  
  public abstract SampleModelProxy getSampleModel(Long paramLong)
    throws RemoteException;
  
  public abstract int getWidth(Long paramLong)
    throws RemoteException;
  
  public abstract int getHeight(Long paramLong)
    throws RemoteException;
  
  public abstract int getMinX(Long paramLong)
    throws RemoteException;
  
  public abstract int getMinY(Long paramLong)
    throws RemoteException;
  
  public abstract int getNumXTiles(Long paramLong)
    throws RemoteException;
  
  public abstract int getNumYTiles(Long paramLong)
    throws RemoteException;
  
  public abstract int getMinTileX(Long paramLong)
    throws RemoteException;
  
  public abstract int getMinTileY(Long paramLong)
    throws RemoteException;
  
  public abstract int getTileWidth(Long paramLong)
    throws RemoteException;
  
  public abstract int getTileHeight(Long paramLong)
    throws RemoteException;
  
  public abstract int getTileGridXOffset(Long paramLong)
    throws RemoteException;
  
  public abstract int getTileGridYOffset(Long paramLong)
    throws RemoteException;
  
  public abstract RasterProxy getTile(Long paramLong, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract RasterProxy getData(Long paramLong)
    throws RemoteException;
  
  public abstract RasterProxy getData(Long paramLong, Rectangle paramRectangle)
    throws RemoteException;
  
  public abstract RasterProxy copyData(Long paramLong, Rectangle paramRectangle)
    throws RemoteException;
}
