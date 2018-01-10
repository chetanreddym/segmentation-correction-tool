package com.sun.media.jai.rmi;

import java.awt.Rectangle;
import java.awt.image.RenderedImage;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Method;
import java.rmi.MarshalException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.UnexpectedException;
import java.rmi.UnmarshalException;
import java.rmi.server.Operation;
import java.rmi.server.RemoteCall;
import java.rmi.server.RemoteObject;
import java.rmi.server.RemoteRef;
import java.rmi.server.RemoteStub;
import java.util.Vector;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;

public final class RMIImageImpl_Stub
  extends RemoteStub
  implements RMIImage
{
  private static final Operation[] operations = { new Operation("com.sun.media.jai.rmi.RasterProxy copyData(java.lang.Long, java.awt.Rectangle)"), new Operation("void dispose(java.lang.Long)"), new Operation("com.sun.media.jai.rmi.ColorModelProxy getColorModel(java.lang.Long)"), new Operation("com.sun.media.jai.rmi.RasterProxy getData(java.lang.Long)"), new Operation("com.sun.media.jai.rmi.RasterProxy getData(java.lang.Long, java.awt.Rectangle)"), new Operation("int getHeight(java.lang.Long)"), new Operation("int getMinTileX(java.lang.Long)"), new Operation("int getMinTileY(java.lang.Long)"), new Operation("int getMinX(java.lang.Long)"), new Operation("int getMinY(java.lang.Long)"), new Operation("int getNumXTiles(java.lang.Long)"), new Operation("int getNumYTiles(java.lang.Long)"), new Operation("java.lang.Object getProperty(java.lang.Long, java.lang.String)"), new Operation("java.lang.String getPropertyNames(java.lang.Long)[]"), new Operation("java.lang.Long getRemoteID()"), new Operation("com.sun.media.jai.rmi.SampleModelProxy getSampleModel(java.lang.Long)"), new Operation("java.util.Vector getSources(java.lang.Long)"), new Operation("com.sun.media.jai.rmi.RasterProxy getTile(java.lang.Long, int, int)"), new Operation("int getTileGridXOffset(java.lang.Long)"), new Operation("int getTileGridYOffset(java.lang.Long)"), new Operation("int getTileHeight(java.lang.Long)"), new Operation("int getTileWidth(java.lang.Long)"), new Operation("int getWidth(java.lang.Long)"), new Operation("void setSource(java.lang.Long, java.awt.image.RenderedImage)"), new Operation("void setSource(java.lang.Long, javax.media.jai.RenderableOp, com.sun.media.jai.rmi.RenderContextProxy)"), new Operation("void setSource(java.lang.Long, javax.media.jai.RenderedOp)") };
  private static final long interfaceHash = -9186133247174212020L;
  private static final long serialVersionUID = 2L;
  private static boolean useNewInvoke;
  private static Method $method_copyData_0;
  private static Method $method_dispose_1;
  private static Method $method_getColorModel_2;
  private static Method $method_getData_3;
  private static Method $method_getData_4;
  private static Method $method_getHeight_5;
  private static Method $method_getMinTileX_6;
  private static Method $method_getMinTileY_7;
  private static Method $method_getMinX_8;
  private static Method $method_getMinY_9;
  private static Method $method_getNumXTiles_10;
  private static Method $method_getNumYTiles_11;
  private static Method $method_getProperty_12;
  private static Method $method_getPropertyNames_13;
  private static Method $method_getRemoteID_14;
  private static Method $method_getSampleModel_15;
  private static Method $method_getSources_16;
  private static Method $method_getTile_17;
  private static Method $method_getTileGridXOffset_18;
  private static Method $method_getTileGridYOffset_19;
  private static Method $method_getTileHeight_20;
  private static Method $method_getTileWidth_21;
  private static Method $method_getWidth_22;
  private static Method $method_setSource_23;
  private static Method $method_setSource_24;
  private static Method $method_setSource_25;
  
  static
  {
    try
    {
      RemoteRef.class.getMethod("invoke", new Class[] { Remote.class, Method.class, new Objec[0].getClass(), Long.TYPE });
      useNewInvoke = true;
      $method_copyData_0 = RMIImage.class.getMethod("copyData", new Class[] { Long.class, Rectangle.class });
      $method_dispose_1 = RMIImage.class.getMethod("dispose", new Class[] { Long.class });
      $method_getColorModel_2 = RMIImage.class.getMethod("getColorModel", new Class[] { Long.class });
      $method_getData_3 = RMIImage.class.getMethod("getData", new Class[] { Long.class });
      $method_getData_4 = RMIImage.class.getMethod("getData", new Class[] { Long.class, Rectangle.class });
      $method_getHeight_5 = RMIImage.class.getMethod("getHeight", new Class[] { Long.class });
      $method_getMinTileX_6 = RMIImage.class.getMethod("getMinTileX", new Class[] { Long.class });
      $method_getMinTileY_7 = RMIImage.class.getMethod("getMinTileY", new Class[] { Long.class });
      $method_getMinX_8 = RMIImage.class.getMethod("getMinX", new Class[] { Long.class });
      $method_getMinY_9 = RMIImage.class.getMethod("getMinY", new Class[] { Long.class });
      $method_getNumXTiles_10 = RMIImage.class.getMethod("getNumXTiles", new Class[] { Long.class });
      $method_getNumYTiles_11 = RMIImage.class.getMethod("getNumYTiles", new Class[] { Long.class });
      $method_getProperty_12 = RMIImage.class.getMethod("getProperty", new Class[] { Long.class, String.class });
      $method_getPropertyNames_13 = RMIImage.class.getMethod("getPropertyNames", new Class[] { Long.class });
      $method_getRemoteID_14 = RMIImage.class.getMethod("getRemoteID", new Class[0]);
      $method_getSampleModel_15 = RMIImage.class.getMethod("getSampleModel", new Class[] { Long.class });
      $method_getSources_16 = RMIImage.class.getMethod("getSources", new Class[] { Long.class });
      $method_getTile_17 = RMIImage.class.getMethod("getTile", new Class[] { Long.class, Integer.TYPE, Integer.TYPE });
      $method_getTileGridXOffset_18 = RMIImage.class.getMethod("getTileGridXOffset", new Class[] { Long.class });
      $method_getTileGridYOffset_19 = RMIImage.class.getMethod("getTileGridYOffset", new Class[] { Long.class });
      $method_getTileHeight_20 = RMIImage.class.getMethod("getTileHeight", new Class[] { Long.class });
      $method_getTileWidth_21 = RMIImage.class.getMethod("getTileWidth", new Class[] { Long.class });
      $method_getWidth_22 = RMIImage.class.getMethod("getWidth", new Class[] { Long.class });
      $method_setSource_23 = RMIImage.class.getMethod("setSource", new Class[] { Long.class, RenderedImage.class });
      $method_setSource_24 = RMIImage.class.getMethod("setSource", new Class[] { Long.class, RenderableOp.class, RenderContextProxy.class });
      $method_setSource_25 = RMIImage.class.getMethod("setSource", new Class[] { Long.class, RenderedOp.class });
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      useNewInvoke = false;
    }
  }
  
  public RMIImageImpl_Stub() {}
  
  public RMIImageImpl_Stub(RemoteRef paramRemoteRef)
  {
    super(paramRemoteRef);
  }
  
  public RasterProxy copyData(Long paramLong, Rectangle paramRectangle)
    throws RemoteException
  {
    try
    {
      if (useNewInvoke)
      {
        localObject1 = ref.invoke(this, $method_copyData_0, new Object[] { paramLong, paramRectangle }, -4480130102587337594L);
        return (RasterProxy)localObject1;
      }
      Object localObject1 = ref.newCall(this, operations, 0, -9186133247174212020L);
      try
      {
        ObjectOutput localObjectOutput = ((RemoteCall)localObject1).getOutputStream();
        localObjectOutput.writeObject(paramLong);
        localObjectOutput.writeObject(paramRectangle);
      }
      catch (IOException localIOException1)
      {
        throw new MarshalException("error marshalling arguments", localIOException1);
      }
      ref.invoke((RemoteCall)localObject1);
      RasterProxy localRasterProxy;
      try
      {
        ObjectInput localObjectInput = ((RemoteCall)localObject1).getInputStream();
        localRasterProxy = (RasterProxy)localObjectInput.readObject();
      }
      catch (IOException localIOException2)
      {
        throw new UnmarshalException("error unmarshalling return", localIOException2);
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        throw new UnmarshalException("error unmarshalling return", localClassNotFoundException);
      }
      finally
      {
        ref.done((RemoteCall)localObject1);
      }
      return localRasterProxy;
    }
    catch (RuntimeException localRuntimeException)
    {
      throw localRuntimeException;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException;
    }
    catch (Exception localException)
    {
      throw new UnexpectedException("undeclared checked exception", localException);
    }
  }
  
  public void dispose(Long paramLong)
    throws RemoteException
  {
    try
    {
      if (useNewInvoke)
      {
        ref.invoke(this, $method_dispose_1, new Object[] { paramLong }, 6460799139781649959L);
      }
      else
      {
        RemoteCall localRemoteCall = ref.newCall(this, operations, 1, -9186133247174212020L);
        try
        {
          ObjectOutput localObjectOutput = localRemoteCall.getOutputStream();
          localObjectOutput.writeObject(paramLong);
        }
        catch (IOException localIOException)
        {
          throw new MarshalException("error marshalling arguments", localIOException);
        }
        ref.invoke(localRemoteCall);
        ref.done(localRemoteCall);
      }
    }
    catch (RuntimeException localRuntimeException)
    {
      throw localRuntimeException;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException;
    }
    catch (Exception localException)
    {
      throw new UnexpectedException("undeclared checked exception", localException);
    }
  }
  
  public ColorModelProxy getColorModel(Long paramLong)
    throws RemoteException
  {
    try
    {
      if (useNewInvoke)
      {
        localObject1 = ref.invoke(this, $method_getColorModel_2, new Object[] { paramLong }, 5862232465831048388L);
        return (ColorModelProxy)localObject1;
      }
      Object localObject1 = ref.newCall(this, operations, 2, -9186133247174212020L);
      try
      {
        ObjectOutput localObjectOutput = ((RemoteCall)localObject1).getOutputStream();
        localObjectOutput.writeObject(paramLong);
      }
      catch (IOException localIOException1)
      {
        throw new MarshalException("error marshalling arguments", localIOException1);
      }
      ref.invoke((RemoteCall)localObject1);
      ColorModelProxy localColorModelProxy;
      try
      {
        ObjectInput localObjectInput = ((RemoteCall)localObject1).getInputStream();
        localColorModelProxy = (ColorModelProxy)localObjectInput.readObject();
      }
      catch (IOException localIOException2)
      {
        throw new UnmarshalException("error unmarshalling return", localIOException2);
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        throw new UnmarshalException("error unmarshalling return", localClassNotFoundException);
      }
      finally
      {
        ref.done((RemoteCall)localObject1);
      }
      return localColorModelProxy;
    }
    catch (RuntimeException localRuntimeException)
    {
      throw localRuntimeException;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException;
    }
    catch (Exception localException)
    {
      throw new UnexpectedException("undeclared checked exception", localException);
    }
  }
  
  public RasterProxy getData(Long paramLong)
    throws RemoteException
  {
    try
    {
      if (useNewInvoke)
      {
        localObject1 = ref.invoke(this, $method_getData_3, new Object[] { paramLong }, 5982474592659170320L);
        return (RasterProxy)localObject1;
      }
      Object localObject1 = ref.newCall(this, operations, 3, -9186133247174212020L);
      try
      {
        ObjectOutput localObjectOutput = ((RemoteCall)localObject1).getOutputStream();
        localObjectOutput.writeObject(paramLong);
      }
      catch (IOException localIOException1)
      {
        throw new MarshalException("error marshalling arguments", localIOException1);
      }
      ref.invoke((RemoteCall)localObject1);
      RasterProxy localRasterProxy;
      try
      {
        ObjectInput localObjectInput = ((RemoteCall)localObject1).getInputStream();
        localRasterProxy = (RasterProxy)localObjectInput.readObject();
      }
      catch (IOException localIOException2)
      {
        throw new UnmarshalException("error unmarshalling return", localIOException2);
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        throw new UnmarshalException("error unmarshalling return", localClassNotFoundException);
      }
      finally
      {
        ref.done((RemoteCall)localObject1);
      }
      return localRasterProxy;
    }
    catch (RuntimeException localRuntimeException)
    {
      throw localRuntimeException;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException;
    }
    catch (Exception localException)
    {
      throw new UnexpectedException("undeclared checked exception", localException);
    }
  }
  
  public RasterProxy getData(Long paramLong, Rectangle paramRectangle)
    throws RemoteException
  {
    try
    {
      if (useNewInvoke)
      {
        localObject1 = ref.invoke(this, $method_getData_4, new Object[] { paramLong, paramRectangle }, -7782001095732779284L);
        return (RasterProxy)localObject1;
      }
      Object localObject1 = ref.newCall(this, operations, 4, -9186133247174212020L);
      try
      {
        ObjectOutput localObjectOutput = ((RemoteCall)localObject1).getOutputStream();
        localObjectOutput.writeObject(paramLong);
        localObjectOutput.writeObject(paramRectangle);
      }
      catch (IOException localIOException1)
      {
        throw new MarshalException("error marshalling arguments", localIOException1);
      }
      ref.invoke((RemoteCall)localObject1);
      RasterProxy localRasterProxy;
      try
      {
        ObjectInput localObjectInput = ((RemoteCall)localObject1).getInputStream();
        localRasterProxy = (RasterProxy)localObjectInput.readObject();
      }
      catch (IOException localIOException2)
      {
        throw new UnmarshalException("error unmarshalling return", localIOException2);
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        throw new UnmarshalException("error unmarshalling return", localClassNotFoundException);
      }
      finally
      {
        ref.done((RemoteCall)localObject1);
      }
      return localRasterProxy;
    }
    catch (RuntimeException localRuntimeException)
    {
      throw localRuntimeException;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException;
    }
    catch (Exception localException)
    {
      throw new UnexpectedException("undeclared checked exception", localException);
    }
  }
  
  public int getHeight(Long paramLong)
    throws RemoteException
  {
    try
    {
      if (useNewInvoke)
      {
        localObject1 = ref.invoke(this, $method_getHeight_5, new Object[] { paramLong }, -7560603472052038977L);
        return ((Integer)localObject1).intValue();
      }
      Object localObject1 = ref.newCall(this, operations, 5, -9186133247174212020L);
      try
      {
        ObjectOutput localObjectOutput = ((RemoteCall)localObject1).getOutputStream();
        localObjectOutput.writeObject(paramLong);
      }
      catch (IOException localIOException1)
      {
        throw new MarshalException("error marshalling arguments", localIOException1);
      }
      ref.invoke((RemoteCall)localObject1);
      int i;
      try
      {
        ObjectInput localObjectInput = ((RemoteCall)localObject1).getInputStream();
        i = localObjectInput.readInt();
      }
      catch (IOException localIOException2)
      {
        throw new UnmarshalException("error unmarshalling return", localIOException2);
      }
      finally
      {
        ref.done((RemoteCall)localObject1);
      }
      return i;
    }
    catch (RuntimeException localRuntimeException)
    {
      throw localRuntimeException;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException;
    }
    catch (Exception localException)
    {
      throw new UnexpectedException("undeclared checked exception", localException);
    }
  }
  
  public int getMinTileX(Long paramLong)
    throws RemoteException
  {
    try
    {
      if (useNewInvoke)
      {
        localObject1 = ref.invoke(this, $method_getMinTileX_6, new Object[] { paramLong }, 5809966745410438246L);
        return ((Integer)localObject1).intValue();
      }
      Object localObject1 = ref.newCall(this, operations, 6, -9186133247174212020L);
      try
      {
        ObjectOutput localObjectOutput = ((RemoteCall)localObject1).getOutputStream();
        localObjectOutput.writeObject(paramLong);
      }
      catch (IOException localIOException1)
      {
        throw new MarshalException("error marshalling arguments", localIOException1);
      }
      ref.invoke((RemoteCall)localObject1);
      int i;
      try
      {
        ObjectInput localObjectInput = ((RemoteCall)localObject1).getInputStream();
        i = localObjectInput.readInt();
      }
      catch (IOException localIOException2)
      {
        throw new UnmarshalException("error unmarshalling return", localIOException2);
      }
      finally
      {
        ref.done((RemoteCall)localObject1);
      }
      return i;
    }
    catch (RuntimeException localRuntimeException)
    {
      throw localRuntimeException;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException;
    }
    catch (Exception localException)
    {
      throw new UnexpectedException("undeclared checked exception", localException);
    }
  }
  
  public int getMinTileY(Long paramLong)
    throws RemoteException
  {
    try
    {
      if (useNewInvoke)
      {
        localObject1 = ref.invoke(this, $method_getMinTileY_7, new Object[] { paramLong }, -9076617268613815876L);
        return ((Integer)localObject1).intValue();
      }
      Object localObject1 = ref.newCall(this, operations, 7, -9186133247174212020L);
      try
      {
        ObjectOutput localObjectOutput = ((RemoteCall)localObject1).getOutputStream();
        localObjectOutput.writeObject(paramLong);
      }
      catch (IOException localIOException1)
      {
        throw new MarshalException("error marshalling arguments", localIOException1);
      }
      ref.invoke((RemoteCall)localObject1);
      int i;
      try
      {
        ObjectInput localObjectInput = ((RemoteCall)localObject1).getInputStream();
        i = localObjectInput.readInt();
      }
      catch (IOException localIOException2)
      {
        throw new UnmarshalException("error unmarshalling return", localIOException2);
      }
      finally
      {
        ref.done((RemoteCall)localObject1);
      }
      return i;
    }
    catch (RuntimeException localRuntimeException)
    {
      throw localRuntimeException;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException;
    }
    catch (Exception localException)
    {
      throw new UnexpectedException("undeclared checked exception", localException);
    }
  }
  
  public int getMinX(Long paramLong)
    throws RemoteException
  {
    try
    {
      if (useNewInvoke)
      {
        localObject1 = ref.invoke(this, $method_getMinX_8, new Object[] { paramLong }, -5297535099750447733L);
        return ((Integer)localObject1).intValue();
      }
      Object localObject1 = ref.newCall(this, operations, 8, -9186133247174212020L);
      try
      {
        ObjectOutput localObjectOutput = ((RemoteCall)localObject1).getOutputStream();
        localObjectOutput.writeObject(paramLong);
      }
      catch (IOException localIOException1)
      {
        throw new MarshalException("error marshalling arguments", localIOException1);
      }
      ref.invoke((RemoteCall)localObject1);
      int i;
      try
      {
        ObjectInput localObjectInput = ((RemoteCall)localObject1).getInputStream();
        i = localObjectInput.readInt();
      }
      catch (IOException localIOException2)
      {
        throw new UnmarshalException("error unmarshalling return", localIOException2);
      }
      finally
      {
        ref.done((RemoteCall)localObject1);
      }
      return i;
    }
    catch (RuntimeException localRuntimeException)
    {
      throw localRuntimeException;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException;
    }
    catch (Exception localException)
    {
      throw new UnexpectedException("undeclared checked exception", localException);
    }
  }
  
  public int getMinY(Long paramLong)
    throws RemoteException
  {
    try
    {
      if (useNewInvoke)
      {
        localObject1 = ref.invoke(this, $method_getMinY_9, new Object[] { paramLong }, 7733459005376369327L);
        return ((Integer)localObject1).intValue();
      }
      Object localObject1 = ref.newCall(this, operations, 9, -9186133247174212020L);
      try
      {
        ObjectOutput localObjectOutput = ((RemoteCall)localObject1).getOutputStream();
        localObjectOutput.writeObject(paramLong);
      }
      catch (IOException localIOException1)
      {
        throw new MarshalException("error marshalling arguments", localIOException1);
      }
      ref.invoke((RemoteCall)localObject1);
      int i;
      try
      {
        ObjectInput localObjectInput = ((RemoteCall)localObject1).getInputStream();
        i = localObjectInput.readInt();
      }
      catch (IOException localIOException2)
      {
        throw new UnmarshalException("error unmarshalling return", localIOException2);
      }
      finally
      {
        ref.done((RemoteCall)localObject1);
      }
      return i;
    }
    catch (RuntimeException localRuntimeException)
    {
      throw localRuntimeException;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException;
    }
    catch (Exception localException)
    {
      throw new UnexpectedException("undeclared checked exception", localException);
    }
  }
  
  public int getNumXTiles(Long paramLong)
    throws RemoteException
  {
    try
    {
      if (useNewInvoke)
      {
        localObject1 = ref.invoke(this, $method_getNumXTiles_10, new Object[] { paramLong }, 3645100420184954761L);
        return ((Integer)localObject1).intValue();
      }
      Object localObject1 = ref.newCall(this, operations, 10, -9186133247174212020L);
      try
      {
        ObjectOutput localObjectOutput = ((RemoteCall)localObject1).getOutputStream();
        localObjectOutput.writeObject(paramLong);
      }
      catch (IOException localIOException1)
      {
        throw new MarshalException("error marshalling arguments", localIOException1);
      }
      ref.invoke((RemoteCall)localObject1);
      int i;
      try
      {
        ObjectInput localObjectInput = ((RemoteCall)localObject1).getInputStream();
        i = localObjectInput.readInt();
      }
      catch (IOException localIOException2)
      {
        throw new UnmarshalException("error unmarshalling return", localIOException2);
      }
      finally
      {
        ref.done((RemoteCall)localObject1);
      }
      return i;
    }
    catch (RuntimeException localRuntimeException)
    {
      throw localRuntimeException;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException;
    }
    catch (Exception localException)
    {
      throw new UnexpectedException("undeclared checked exception", localException);
    }
  }
  
  public int getNumYTiles(Long paramLong)
    throws RemoteException
  {
    try
    {
      if (useNewInvoke)
      {
        localObject1 = ref.invoke(this, $method_getNumYTiles_11, new Object[] { paramLong }, -1731091968647972742L);
        return ((Integer)localObject1).intValue();
      }
      Object localObject1 = ref.newCall(this, operations, 11, -9186133247174212020L);
      try
      {
        ObjectOutput localObjectOutput = ((RemoteCall)localObject1).getOutputStream();
        localObjectOutput.writeObject(paramLong);
      }
      catch (IOException localIOException1)
      {
        throw new MarshalException("error marshalling arguments", localIOException1);
      }
      ref.invoke((RemoteCall)localObject1);
      int i;
      try
      {
        ObjectInput localObjectInput = ((RemoteCall)localObject1).getInputStream();
        i = localObjectInput.readInt();
      }
      catch (IOException localIOException2)
      {
        throw new UnmarshalException("error unmarshalling return", localIOException2);
      }
      finally
      {
        ref.done((RemoteCall)localObject1);
      }
      return i;
    }
    catch (RuntimeException localRuntimeException)
    {
      throw localRuntimeException;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException;
    }
    catch (Exception localException)
    {
      throw new UnexpectedException("undeclared checked exception", localException);
    }
  }
  
  public Object getProperty(Long paramLong, String paramString)
    throws RemoteException
  {
    try
    {
      if (useNewInvoke)
      {
        localObject1 = ref.invoke(this, $method_getProperty_12, new Object[] { paramLong, paramString }, 216968610676295195L);
        return localObject1;
      }
      Object localObject1 = ref.newCall(this, operations, 12, -9186133247174212020L);
      try
      {
        ObjectOutput localObjectOutput = ((RemoteCall)localObject1).getOutputStream();
        localObjectOutput.writeObject(paramLong);
        localObjectOutput.writeObject(paramString);
      }
      catch (IOException localIOException1)
      {
        throw new MarshalException("error marshalling arguments", localIOException1);
      }
      ref.invoke((RemoteCall)localObject1);
      Object localObject2;
      try
      {
        ObjectInput localObjectInput = ((RemoteCall)localObject1).getInputStream();
        localObject2 = localObjectInput.readObject();
      }
      catch (IOException localIOException2)
      {
        throw new UnmarshalException("error unmarshalling return", localIOException2);
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        throw new UnmarshalException("error unmarshalling return", localClassNotFoundException);
      }
      finally
      {
        ref.done((RemoteCall)localObject1);
      }
      return localObject2;
    }
    catch (RuntimeException localRuntimeException)
    {
      throw localRuntimeException;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException;
    }
    catch (Exception localException)
    {
      throw new UnexpectedException("undeclared checked exception", localException);
    }
  }
  
  public String[] getPropertyNames(Long paramLong)
    throws RemoteException
  {
    try
    {
      if (useNewInvoke)
      {
        localObject1 = ref.invoke(this, $method_getPropertyNames_13, new Object[] { paramLong }, 3931591828613160321L);
        return (String[])localObject1;
      }
      Object localObject1 = ref.newCall(this, operations, 13, -9186133247174212020L);
      try
      {
        ObjectOutput localObjectOutput = ((RemoteCall)localObject1).getOutputStream();
        localObjectOutput.writeObject(paramLong);
      }
      catch (IOException localIOException1)
      {
        throw new MarshalException("error marshalling arguments", localIOException1);
      }
      ref.invoke((RemoteCall)localObject1);
      String[] arrayOfString;
      try
      {
        ObjectInput localObjectInput = ((RemoteCall)localObject1).getInputStream();
        arrayOfString = (String[])localObjectInput.readObject();
      }
      catch (IOException localIOException2)
      {
        throw new UnmarshalException("error unmarshalling return", localIOException2);
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        throw new UnmarshalException("error unmarshalling return", localClassNotFoundException);
      }
      finally
      {
        ref.done((RemoteCall)localObject1);
      }
      return arrayOfString;
    }
    catch (RuntimeException localRuntimeException)
    {
      throw localRuntimeException;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException;
    }
    catch (Exception localException)
    {
      throw new UnexpectedException("undeclared checked exception", localException);
    }
  }
  
  public Long getRemoteID()
    throws RemoteException
  {
    try
    {
      if (useNewInvoke)
      {
        localObject1 = ref.invoke(this, $method_getRemoteID_14, null, -232353888923603427L);
        return (Long)localObject1;
      }
      Object localObject1 = ref.newCall(this, operations, 14, -9186133247174212020L);
      ref.invoke((RemoteCall)localObject1);
      Long localLong;
      try
      {
        ObjectInput localObjectInput = ((RemoteCall)localObject1).getInputStream();
        localLong = (Long)localObjectInput.readObject();
      }
      catch (IOException localIOException)
      {
        throw new UnmarshalException("error unmarshalling return", localIOException);
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        throw new UnmarshalException("error unmarshalling return", localClassNotFoundException);
      }
      finally
      {
        ref.done((RemoteCall)localObject1);
      }
      return localLong;
    }
    catch (RuntimeException localRuntimeException)
    {
      throw localRuntimeException;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException;
    }
    catch (Exception localException)
    {
      throw new UnexpectedException("undeclared checked exception", localException);
    }
  }
  
  public SampleModelProxy getSampleModel(Long paramLong)
    throws RemoteException
  {
    try
    {
      if (useNewInvoke)
      {
        localObject1 = ref.invoke(this, $method_getSampleModel_15, new Object[] { paramLong }, -8396533149827190655L);
        return (SampleModelProxy)localObject1;
      }
      Object localObject1 = ref.newCall(this, operations, 15, -9186133247174212020L);
      try
      {
        ObjectOutput localObjectOutput = ((RemoteCall)localObject1).getOutputStream();
        localObjectOutput.writeObject(paramLong);
      }
      catch (IOException localIOException1)
      {
        throw new MarshalException("error marshalling arguments", localIOException1);
      }
      ref.invoke((RemoteCall)localObject1);
      SampleModelProxy localSampleModelProxy;
      try
      {
        ObjectInput localObjectInput = ((RemoteCall)localObject1).getInputStream();
        localSampleModelProxy = (SampleModelProxy)localObjectInput.readObject();
      }
      catch (IOException localIOException2)
      {
        throw new UnmarshalException("error unmarshalling return", localIOException2);
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        throw new UnmarshalException("error unmarshalling return", localClassNotFoundException);
      }
      finally
      {
        ref.done((RemoteCall)localObject1);
      }
      return localSampleModelProxy;
    }
    catch (RuntimeException localRuntimeException)
    {
      throw localRuntimeException;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException;
    }
    catch (Exception localException)
    {
      throw new UnexpectedException("undeclared checked exception", localException);
    }
  }
  
  public Vector getSources(Long paramLong)
    throws RemoteException
  {
    try
    {
      if (useNewInvoke)
      {
        localObject1 = ref.invoke(this, $method_getSources_16, new Object[] { paramLong }, -3713513808775692904L);
        return (Vector)localObject1;
      }
      Object localObject1 = ref.newCall(this, operations, 16, -9186133247174212020L);
      try
      {
        ObjectOutput localObjectOutput = ((RemoteCall)localObject1).getOutputStream();
        localObjectOutput.writeObject(paramLong);
      }
      catch (IOException localIOException1)
      {
        throw new MarshalException("error marshalling arguments", localIOException1);
      }
      ref.invoke((RemoteCall)localObject1);
      Vector localVector;
      try
      {
        ObjectInput localObjectInput = ((RemoteCall)localObject1).getInputStream();
        localVector = (Vector)localObjectInput.readObject();
      }
      catch (IOException localIOException2)
      {
        throw new UnmarshalException("error unmarshalling return", localIOException2);
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        throw new UnmarshalException("error unmarshalling return", localClassNotFoundException);
      }
      finally
      {
        ref.done((RemoteCall)localObject1);
      }
      return localVector;
    }
    catch (RuntimeException localRuntimeException)
    {
      throw localRuntimeException;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException;
    }
    catch (Exception localException)
    {
      throw new UnexpectedException("undeclared checked exception", localException);
    }
  }
  
  public RasterProxy getTile(Long paramLong, int paramInt1, int paramInt2)
    throws RemoteException
  {
    try
    {
      if (useNewInvoke)
      {
        localObject1 = ref.invoke(this, $method_getTile_17, new Object[] { paramLong, new Integer(paramInt1), new Integer(paramInt2) }, -1008030285235108860L);
        return (RasterProxy)localObject1;
      }
      Object localObject1 = ref.newCall(this, operations, 17, -9186133247174212020L);
      try
      {
        ObjectOutput localObjectOutput = ((RemoteCall)localObject1).getOutputStream();
        localObjectOutput.writeObject(paramLong);
        localObjectOutput.writeInt(paramInt1);
        localObjectOutput.writeInt(paramInt2);
      }
      catch (IOException localIOException1)
      {
        throw new MarshalException("error marshalling arguments", localIOException1);
      }
      ref.invoke((RemoteCall)localObject1);
      RasterProxy localRasterProxy;
      try
      {
        ObjectInput localObjectInput = ((RemoteCall)localObject1).getInputStream();
        localRasterProxy = (RasterProxy)localObjectInput.readObject();
      }
      catch (IOException localIOException2)
      {
        throw new UnmarshalException("error unmarshalling return", localIOException2);
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        throw new UnmarshalException("error unmarshalling return", localClassNotFoundException);
      }
      finally
      {
        ref.done((RemoteCall)localObject1);
      }
      return localRasterProxy;
    }
    catch (RuntimeException localRuntimeException)
    {
      throw localRuntimeException;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException;
    }
    catch (Exception localException)
    {
      throw new UnexpectedException("undeclared checked exception", localException);
    }
  }
  
  public int getTileGridXOffset(Long paramLong)
    throws RemoteException
  {
    try
    {
      if (useNewInvoke)
      {
        localObject1 = ref.invoke(this, $method_getTileGridXOffset_18, new Object[] { paramLong }, -8218495432205133449L);
        return ((Integer)localObject1).intValue();
      }
      Object localObject1 = ref.newCall(this, operations, 18, -9186133247174212020L);
      try
      {
        ObjectOutput localObjectOutput = ((RemoteCall)localObject1).getOutputStream();
        localObjectOutput.writeObject(paramLong);
      }
      catch (IOException localIOException1)
      {
        throw new MarshalException("error marshalling arguments", localIOException1);
      }
      ref.invoke((RemoteCall)localObject1);
      int i;
      try
      {
        ObjectInput localObjectInput = ((RemoteCall)localObject1).getInputStream();
        i = localObjectInput.readInt();
      }
      catch (IOException localIOException2)
      {
        throw new UnmarshalException("error unmarshalling return", localIOException2);
      }
      finally
      {
        ref.done((RemoteCall)localObject1);
      }
      return i;
    }
    catch (RuntimeException localRuntimeException)
    {
      throw localRuntimeException;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException;
    }
    catch (Exception localException)
    {
      throw new UnexpectedException("undeclared checked exception", localException);
    }
  }
  
  public int getTileGridYOffset(Long paramLong)
    throws RemoteException
  {
    try
    {
      if (useNewInvoke)
      {
        localObject1 = ref.invoke(this, $method_getTileGridYOffset_19, new Object[] { paramLong }, -7482127068346373541L);
        return ((Integer)localObject1).intValue();
      }
      Object localObject1 = ref.newCall(this, operations, 19, -9186133247174212020L);
      try
      {
        ObjectOutput localObjectOutput = ((RemoteCall)localObject1).getOutputStream();
        localObjectOutput.writeObject(paramLong);
      }
      catch (IOException localIOException1)
      {
        throw new MarshalException("error marshalling arguments", localIOException1);
      }
      ref.invoke((RemoteCall)localObject1);
      int i;
      try
      {
        ObjectInput localObjectInput = ((RemoteCall)localObject1).getInputStream();
        i = localObjectInput.readInt();
      }
      catch (IOException localIOException2)
      {
        throw new UnmarshalException("error unmarshalling return", localIOException2);
      }
      finally
      {
        ref.done((RemoteCall)localObject1);
      }
      return i;
    }
    catch (RuntimeException localRuntimeException)
    {
      throw localRuntimeException;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException;
    }
    catch (Exception localException)
    {
      throw new UnexpectedException("undeclared checked exception", localException);
    }
  }
  
  public int getTileHeight(Long paramLong)
    throws RemoteException
  {
    try
    {
      if (useNewInvoke)
      {
        localObject1 = ref.invoke(this, $method_getTileHeight_20, new Object[] { paramLong }, 7785669351714030715L);
        return ((Integer)localObject1).intValue();
      }
      Object localObject1 = ref.newCall(this, operations, 20, -9186133247174212020L);
      try
      {
        ObjectOutput localObjectOutput = ((RemoteCall)localObject1).getOutputStream();
        localObjectOutput.writeObject(paramLong);
      }
      catch (IOException localIOException1)
      {
        throw new MarshalException("error marshalling arguments", localIOException1);
      }
      ref.invoke((RemoteCall)localObject1);
      int i;
      try
      {
        ObjectInput localObjectInput = ((RemoteCall)localObject1).getInputStream();
        i = localObjectInput.readInt();
      }
      catch (IOException localIOException2)
      {
        throw new UnmarshalException("error unmarshalling return", localIOException2);
      }
      finally
      {
        ref.done((RemoteCall)localObject1);
      }
      return i;
    }
    catch (RuntimeException localRuntimeException)
    {
      throw localRuntimeException;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException;
    }
    catch (Exception localException)
    {
      throw new UnexpectedException("undeclared checked exception", localException);
    }
  }
  
  public int getTileWidth(Long paramLong)
    throws RemoteException
  {
    try
    {
      if (useNewInvoke)
      {
        localObject1 = ref.invoke(this, $method_getTileWidth_21, new Object[] { paramLong }, 282122131312695349L);
        return ((Integer)localObject1).intValue();
      }
      Object localObject1 = ref.newCall(this, operations, 21, -9186133247174212020L);
      try
      {
        ObjectOutput localObjectOutput = ((RemoteCall)localObject1).getOutputStream();
        localObjectOutput.writeObject(paramLong);
      }
      catch (IOException localIOException1)
      {
        throw new MarshalException("error marshalling arguments", localIOException1);
      }
      ref.invoke((RemoteCall)localObject1);
      int i;
      try
      {
        ObjectInput localObjectInput = ((RemoteCall)localObject1).getInputStream();
        i = localObjectInput.readInt();
      }
      catch (IOException localIOException2)
      {
        throw new UnmarshalException("error unmarshalling return", localIOException2);
      }
      finally
      {
        ref.done((RemoteCall)localObject1);
      }
      return i;
    }
    catch (RuntimeException localRuntimeException)
    {
      throw localRuntimeException;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException;
    }
    catch (Exception localException)
    {
      throw new UnexpectedException("undeclared checked exception", localException);
    }
  }
  
  public int getWidth(Long paramLong)
    throws RemoteException
  {
    try
    {
      if (useNewInvoke)
      {
        localObject1 = ref.invoke(this, $method_getWidth_22, new Object[] { paramLong }, -8357318297729299690L);
        return ((Integer)localObject1).intValue();
      }
      Object localObject1 = ref.newCall(this, operations, 22, -9186133247174212020L);
      try
      {
        ObjectOutput localObjectOutput = ((RemoteCall)localObject1).getOutputStream();
        localObjectOutput.writeObject(paramLong);
      }
      catch (IOException localIOException1)
      {
        throw new MarshalException("error marshalling arguments", localIOException1);
      }
      ref.invoke((RemoteCall)localObject1);
      int i;
      try
      {
        ObjectInput localObjectInput = ((RemoteCall)localObject1).getInputStream();
        i = localObjectInput.readInt();
      }
      catch (IOException localIOException2)
      {
        throw new UnmarshalException("error unmarshalling return", localIOException2);
      }
      finally
      {
        ref.done((RemoteCall)localObject1);
      }
      return i;
    }
    catch (RuntimeException localRuntimeException)
    {
      throw localRuntimeException;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException;
    }
    catch (Exception localException)
    {
      throw new UnexpectedException("undeclared checked exception", localException);
    }
  }
  
  public void setSource(Long paramLong, RenderedImage paramRenderedImage)
    throws RemoteException
  {
    try
    {
      if (useNewInvoke)
      {
        ref.invoke(this, $method_setSource_23, new Object[] { paramLong, paramRenderedImage }, 4248763766578677765L);
      }
      else
      {
        RemoteCall localRemoteCall = ref.newCall(this, operations, 23, -9186133247174212020L);
        try
        {
          ObjectOutput localObjectOutput = localRemoteCall.getOutputStream();
          localObjectOutput.writeObject(paramLong);
          localObjectOutput.writeObject(paramRenderedImage);
        }
        catch (IOException localIOException)
        {
          throw new MarshalException("error marshalling arguments", localIOException);
        }
        ref.invoke(localRemoteCall);
        ref.done(localRemoteCall);
      }
    }
    catch (RuntimeException localRuntimeException)
    {
      throw localRuntimeException;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException;
    }
    catch (Exception localException)
    {
      throw new UnexpectedException("undeclared checked exception", localException);
    }
  }
  
  public void setSource(Long paramLong, RenderableOp paramRenderableOp, RenderContextProxy paramRenderContextProxy)
    throws RemoteException
  {
    try
    {
      if (useNewInvoke)
      {
        ref.invoke(this, $method_setSource_24, new Object[] { paramLong, paramRenderableOp, paramRenderContextProxy }, 7010328997687947687L);
      }
      else
      {
        RemoteCall localRemoteCall = ref.newCall(this, operations, 24, -9186133247174212020L);
        try
        {
          ObjectOutput localObjectOutput = localRemoteCall.getOutputStream();
          localObjectOutput.writeObject(paramLong);
          localObjectOutput.writeObject(paramRenderableOp);
          localObjectOutput.writeObject(paramRenderContextProxy);
        }
        catch (IOException localIOException)
        {
          throw new MarshalException("error marshalling arguments", localIOException);
        }
        ref.invoke(localRemoteCall);
        ref.done(localRemoteCall);
      }
    }
    catch (RuntimeException localRuntimeException)
    {
      throw localRuntimeException;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException;
    }
    catch (Exception localException)
    {
      throw new UnexpectedException("undeclared checked exception", localException);
    }
  }
  
  public void setSource(Long paramLong, RenderedOp paramRenderedOp)
    throws RemoteException
  {
    try
    {
      if (useNewInvoke)
      {
        ref.invoke(this, $method_setSource_25, new Object[] { paramLong, paramRenderedOp }, -4039999355356694323L);
      }
      else
      {
        RemoteCall localRemoteCall = ref.newCall(this, operations, 25, -9186133247174212020L);
        try
        {
          ObjectOutput localObjectOutput = localRemoteCall.getOutputStream();
          localObjectOutput.writeObject(paramLong);
          localObjectOutput.writeObject(paramRenderedOp);
        }
        catch (IOException localIOException)
        {
          throw new MarshalException("error marshalling arguments", localIOException);
        }
        ref.invoke(localRemoteCall);
        ref.done(localRemoteCall);
      }
    }
    catch (RuntimeException localRuntimeException)
    {
      throw localRuntimeException;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException;
    }
    catch (Exception localException)
    {
      throw new UnexpectedException("undeclared checked exception", localException);
    }
  }
}
