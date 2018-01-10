package com.sun.media.jai.rmi;

import java.awt.Rectangle;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;
import javax.media.jai.remote.NegotiableCapabilitySet;
import javax.media.jai.remote.SerializableState;

public abstract interface ImageServer
  extends Remote
{
  public abstract Long getRemoteID()
    throws RemoteException;
  
  public abstract void dispose(Long paramLong)
    throws RemoteException;
  
  public abstract void incrementRefCount(Long paramLong)
    throws RemoteException;
  
  public abstract Object getProperty(Long paramLong, String paramString)
    throws RemoteException;
  
  public abstract String[] getPropertyNames(Long paramLong)
    throws RemoteException;
  
  public abstract String[] getPropertyNames(String paramString)
    throws RemoteException;
  
  public abstract SerializableState getColorModel(Long paramLong)
    throws RemoteException;
  
  public abstract SerializableState getSampleModel(Long paramLong)
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
  
  public abstract SerializableState getTile(Long paramLong, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract byte[] getCompressedTile(Long paramLong, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract SerializableState getData(Long paramLong)
    throws RemoteException;
  
  public abstract SerializableState getData(Long paramLong, Rectangle paramRectangle)
    throws RemoteException;
  
  public abstract SerializableState copyData(Long paramLong, Rectangle paramRectangle)
    throws RemoteException;
  
  public abstract void createRenderedOp(Long paramLong, String paramString, ParameterBlock paramParameterBlock, SerializableState paramSerializableState)
    throws RemoteException;
  
  public abstract boolean getRendering(Long paramLong)
    throws RemoteException;
  
  public abstract RenderedOp getNode(Long paramLong)
    throws RemoteException;
  
  public abstract void setRenderedSource(Long paramLong, RenderedImage paramRenderedImage, int paramInt)
    throws RemoteException;
  
  public abstract void setRenderedSource(Long paramLong, RenderedOp paramRenderedOp, int paramInt)
    throws RemoteException;
  
  public abstract void setRenderedSource(Long paramLong1, Long paramLong2, int paramInt)
    throws RemoteException;
  
  public abstract void setRenderedSource(Long paramLong1, Long paramLong2, String paramString1, String paramString2, int paramInt)
    throws RemoteException;
  
  public abstract float getRenderableMinX(Long paramLong)
    throws RemoteException;
  
  public abstract float getRenderableMinY(Long paramLong)
    throws RemoteException;
  
  public abstract float getRenderableWidth(Long paramLong)
    throws RemoteException;
  
  public abstract float getRenderableHeight(Long paramLong)
    throws RemoteException;
  
  public abstract RenderedImage createScaledRendering(Long paramLong, int paramInt1, int paramInt2, SerializableState paramSerializableState)
    throws RemoteException;
  
  public abstract RenderedImage createDefaultRendering(Long paramLong)
    throws RemoteException;
  
  public abstract RenderedImage createRendering(Long paramLong, SerializableState paramSerializableState)
    throws RemoteException;
  
  public abstract void createRenderableOp(Long paramLong, String paramString, ParameterBlock paramParameterBlock)
    throws RemoteException;
  
  public abstract Long getRendering(Long paramLong, SerializableState paramSerializableState)
    throws RemoteException;
  
  public abstract void setRenderableSource(Long paramLong1, Long paramLong2, int paramInt)
    throws RemoteException;
  
  public abstract void setRenderableSource(Long paramLong1, Long paramLong2, String paramString1, String paramString2, int paramInt)
    throws RemoteException;
  
  public abstract void setRenderableRMIServerProxyAsSource(Long paramLong1, Long paramLong2, String paramString1, String paramString2, int paramInt)
    throws RemoteException;
  
  public abstract void setRenderableSource(Long paramLong, RenderableOp paramRenderableOp, int paramInt)
    throws RemoteException;
  
  public abstract void setRenderableSource(Long paramLong, SerializableRenderableImage paramSerializableRenderableImage, int paramInt)
    throws RemoteException;
  
  public abstract void setRenderableSource(Long paramLong, RenderedImage paramRenderedImage, int paramInt)
    throws RemoteException;
  
  public abstract SerializableState mapRenderContext(int paramInt, Long paramLong, String paramString, SerializableState paramSerializableState)
    throws RemoteException;
  
  public abstract SerializableState getBounds2D(Long paramLong, String paramString)
    throws RemoteException;
  
  public abstract boolean isDynamic(String paramString)
    throws RemoteException;
  
  public abstract boolean isDynamic(Long paramLong)
    throws RemoteException;
  
  public abstract String[] getServerSupportedOperationNames()
    throws RemoteException;
  
  public abstract List getOperationDescriptors()
    throws RemoteException;
  
  public abstract SerializableState getInvalidRegion(Long paramLong, ParameterBlock paramParameterBlock1, SerializableState paramSerializableState1, ParameterBlock paramParameterBlock2, SerializableState paramSerializableState2)
    throws RemoteException;
  
  public abstract Rectangle mapSourceRect(Long paramLong, Rectangle paramRectangle, int paramInt)
    throws RemoteException;
  
  public abstract Rectangle mapDestRect(Long paramLong, Rectangle paramRectangle, int paramInt)
    throws RemoteException;
  
  public abstract Long handleEvent(Long paramLong, String paramString, Object paramObject1, Object paramObject2)
    throws RemoteException;
  
  public abstract Long handleEvent(Long paramLong, int paramInt, SerializableState paramSerializableState, Object paramObject)
    throws RemoteException;
  
  public abstract NegotiableCapabilitySet getServerCapabilities()
    throws RemoteException;
  
  public abstract void setServerNegotiatedValues(Long paramLong, NegotiableCapabilitySet paramNegotiableCapabilitySet)
    throws RemoteException;
}
