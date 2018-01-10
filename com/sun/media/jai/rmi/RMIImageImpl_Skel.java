package com.sun.media.jai.rmi;

import java.awt.Rectangle;
import java.awt.image.RenderedImage;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.rmi.MarshalException;
import java.rmi.Remote;
import java.rmi.UnmarshalException;
import java.rmi.server.Operation;
import java.rmi.server.RemoteCall;
import java.rmi.server.Skeleton;
import java.rmi.server.SkeletonMismatchException;
import java.util.Vector;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;

public final class RMIImageImpl_Skel
  implements Skeleton
{
  private static final Operation[] operations = { new Operation("com.sun.media.jai.rmi.RasterProxy copyData(java.lang.Long, java.awt.Rectangle)"), new Operation("void dispose(java.lang.Long)"), new Operation("com.sun.media.jai.rmi.ColorModelProxy getColorModel(java.lang.Long)"), new Operation("com.sun.media.jai.rmi.RasterProxy getData(java.lang.Long)"), new Operation("com.sun.media.jai.rmi.RasterProxy getData(java.lang.Long, java.awt.Rectangle)"), new Operation("int getHeight(java.lang.Long)"), new Operation("int getMinTileX(java.lang.Long)"), new Operation("int getMinTileY(java.lang.Long)"), new Operation("int getMinX(java.lang.Long)"), new Operation("int getMinY(java.lang.Long)"), new Operation("int getNumXTiles(java.lang.Long)"), new Operation("int getNumYTiles(java.lang.Long)"), new Operation("java.lang.Object getProperty(java.lang.Long, java.lang.String)"), new Operation("java.lang.String getPropertyNames(java.lang.Long)[]"), new Operation("java.lang.Long getRemoteID()"), new Operation("com.sun.media.jai.rmi.SampleModelProxy getSampleModel(java.lang.Long)"), new Operation("java.util.Vector getSources(java.lang.Long)"), new Operation("com.sun.media.jai.rmi.RasterProxy getTile(java.lang.Long, int, int)"), new Operation("int getTileGridXOffset(java.lang.Long)"), new Operation("int getTileGridYOffset(java.lang.Long)"), new Operation("int getTileHeight(java.lang.Long)"), new Operation("int getTileWidth(java.lang.Long)"), new Operation("int getWidth(java.lang.Long)"), new Operation("void setSource(java.lang.Long, java.awt.image.RenderedImage)"), new Operation("void setSource(java.lang.Long, javax.media.jai.RenderableOp, com.sun.media.jai.rmi.RenderContextProxy)"), new Operation("void setSource(java.lang.Long, javax.media.jai.RenderedOp)") };
  private static final long interfaceHash = -9186133247174212020L;
  
  public RMIImageImpl_Skel() {}
  
  public void dispatch(Remote paramRemote, RemoteCall paramRemoteCall, int paramInt, long paramLong)
    throws Exception
  {
    if (paramInt < 0)
    {
      if (paramLong == -4480130102587337594L) {
        paramInt = 0;
      } else if (paramLong == 6460799139781649959L) {
        paramInt = 1;
      } else if (paramLong == 5862232465831048388L) {
        paramInt = 2;
      } else if (paramLong == 5982474592659170320L) {
        paramInt = 3;
      } else if (paramLong == -7782001095732779284L) {
        paramInt = 4;
      } else if (paramLong == -7560603472052038977L) {
        paramInt = 5;
      } else if (paramLong == 5809966745410438246L) {
        paramInt = 6;
      } else if (paramLong == -9076617268613815876L) {
        paramInt = 7;
      } else if (paramLong == -5297535099750447733L) {
        paramInt = 8;
      } else if (paramLong == 7733459005376369327L) {
        paramInt = 9;
      } else if (paramLong == 3645100420184954761L) {
        paramInt = 10;
      } else if (paramLong == -1731091968647972742L) {
        paramInt = 11;
      } else if (paramLong == 216968610676295195L) {
        paramInt = 12;
      } else if (paramLong == 3931591828613160321L) {
        paramInt = 13;
      } else if (paramLong == -232353888923603427L) {
        paramInt = 14;
      } else if (paramLong == -8396533149827190655L) {
        paramInt = 15;
      } else if (paramLong == -3713513808775692904L) {
        paramInt = 16;
      } else if (paramLong == -1008030285235108860L) {
        paramInt = 17;
      } else if (paramLong == -8218495432205133449L) {
        paramInt = 18;
      } else if (paramLong == -7482127068346373541L) {
        paramInt = 19;
      } else if (paramLong == 7785669351714030715L) {
        paramInt = 20;
      } else if (paramLong == 282122131312695349L) {
        paramInt = 21;
      } else if (paramLong == -8357318297729299690L) {
        paramInt = 22;
      } else if (paramLong == 4248763766578677765L) {
        paramInt = 23;
      } else if (paramLong == 7010328997687947687L) {
        paramInt = 24;
      } else if (paramLong == -4039999355356694323L) {
        paramInt = 25;
      } else {
        throw new UnmarshalException("invalid method hash");
      }
    }
    else if (paramLong != -9186133247174212020L) {
      throw new SkeletonMismatchException("interface hash mismatch");
    }
    RMIImageImpl localRMIImageImpl = (RMIImageImpl)paramRemote;
    Long localLong;
    Object localObject4;
    Object localObject13;
    Object localObject50;
    Object localObject21;
    switch (paramInt)
    {
    case 0: 
      Rectangle localRectangle;
      try
      {
        ObjectInput localObjectInput18 = paramRemoteCall.getInputStream();
        localLong = (Long)localObjectInput18.readObject();
        localRectangle = (Rectangle)localObjectInput18.readObject();
      }
      catch (IOException localIOException44)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException44);
      }
      catch (ClassNotFoundException localClassNotFoundException19)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException19);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      RasterProxy localRasterProxy1 = localRMIImageImpl.copyData(localLong, localRectangle);
      try
      {
        ObjectOutput localObjectOutput18 = paramRemoteCall.getResultStream(true);
        localObjectOutput18.writeObject(localRasterProxy1);
      }
      catch (IOException localIOException22)
      {
        throw new MarshalException("error marshalling return", localIOException22);
      }
    case 1: 
      try
      {
        ObjectInput localObjectInput1 = paramRemoteCall.getInputStream();
        localLong = (Long)localObjectInput1.readObject();
      }
      catch (IOException localIOException23)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException23);
      }
      catch (ClassNotFoundException localClassNotFoundException1)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException1);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      localRMIImageImpl.dispose(localLong);
      try
      {
        paramRemoteCall.getResultStream(true);
      }
      catch (IOException localIOException1)
      {
        throw new MarshalException("error marshalling return", localIOException1);
      }
    case 2: 
      try
      {
        ObjectInput localObjectInput2 = paramRemoteCall.getInputStream();
        localLong = (Long)localObjectInput2.readObject();
      }
      catch (IOException localIOException24)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException24);
      }
      catch (ClassNotFoundException localClassNotFoundException2)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException2);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      ColorModelProxy localColorModelProxy = localRMIImageImpl.getColorModel(localLong);
      try
      {
        ObjectOutput localObjectOutput1 = paramRemoteCall.getResultStream(true);
        localObjectOutput1.writeObject(localColorModelProxy);
      }
      catch (IOException localIOException3)
      {
        throw new MarshalException("error marshalling return", localIOException3);
      }
    case 3: 
      try
      {
        ObjectInput localObjectInput3 = paramRemoteCall.getInputStream();
        localLong = (Long)localObjectInput3.readObject();
      }
      catch (IOException localIOException25)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException25);
      }
      catch (ClassNotFoundException localClassNotFoundException3)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException3);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      localObject4 = localRMIImageImpl.getData(localLong);
      try
      {
        ObjectOutput localObjectOutput2 = paramRemoteCall.getResultStream(true);
        localObjectOutput2.writeObject(localObject4);
      }
      catch (IOException localIOException4)
      {
        throw new MarshalException("error marshalling return", localIOException4);
      }
    case 4: 
      try
      {
        ObjectInput localObjectInput19 = paramRemoteCall.getInputStream();
        localLong = (Long)localObjectInput19.readObject();
        localObject4 = (Rectangle)localObjectInput19.readObject();
      }
      catch (IOException localIOException45)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException45);
      }
      catch (ClassNotFoundException localClassNotFoundException20)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException20);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      RasterProxy localRasterProxy2 = localRMIImageImpl.getData(localLong, (Rectangle)localObject4);
      try
      {
        ObjectOutput localObjectOutput19 = paramRemoteCall.getResultStream(true);
        localObjectOutput19.writeObject(localRasterProxy2);
      }
      catch (IOException localIOException26)
      {
        throw new MarshalException("error marshalling return", localIOException26);
      }
    case 5: 
      try
      {
        ObjectInput localObjectInput4 = paramRemoteCall.getInputStream();
        localLong = (Long)localObjectInput4.readObject();
      }
      catch (IOException localIOException27)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException27);
      }
      catch (ClassNotFoundException localClassNotFoundException4)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException4);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      int i = localRMIImageImpl.getHeight(localLong);
      try
      {
        ObjectOutput localObjectOutput3 = paramRemoteCall.getResultStream(true);
        localObjectOutput3.writeInt(i);
      }
      catch (IOException localIOException5)
      {
        throw new MarshalException("error marshalling return", localIOException5);
      }
    case 6: 
      try
      {
        ObjectInput localObjectInput5 = paramRemoteCall.getInputStream();
        localLong = (Long)localObjectInput5.readObject();
      }
      catch (IOException localIOException28)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException28);
      }
      catch (ClassNotFoundException localClassNotFoundException5)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException5);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      int j = localRMIImageImpl.getMinTileX(localLong);
      try
      {
        ObjectOutput localObjectOutput4 = paramRemoteCall.getResultStream(true);
        localObjectOutput4.writeInt(j);
      }
      catch (IOException localIOException6)
      {
        throw new MarshalException("error marshalling return", localIOException6);
      }
    case 7: 
      try
      {
        ObjectInput localObjectInput6 = paramRemoteCall.getInputStream();
        localLong = (Long)localObjectInput6.readObject();
      }
      catch (IOException localIOException29)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException29);
      }
      catch (ClassNotFoundException localClassNotFoundException6)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException6);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      int k = localRMIImageImpl.getMinTileY(localLong);
      try
      {
        ObjectOutput localObjectOutput5 = paramRemoteCall.getResultStream(true);
        localObjectOutput5.writeInt(k);
      }
      catch (IOException localIOException7)
      {
        throw new MarshalException("error marshalling return", localIOException7);
      }
    case 8: 
      try
      {
        ObjectInput localObjectInput7 = paramRemoteCall.getInputStream();
        localLong = (Long)localObjectInput7.readObject();
      }
      catch (IOException localIOException30)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException30);
      }
      catch (ClassNotFoundException localClassNotFoundException7)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException7);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      int m = localRMIImageImpl.getMinX(localLong);
      try
      {
        ObjectOutput localObjectOutput6 = paramRemoteCall.getResultStream(true);
        localObjectOutput6.writeInt(m);
      }
      catch (IOException localIOException8)
      {
        throw new MarshalException("error marshalling return", localIOException8);
      }
    case 9: 
      try
      {
        ObjectInput localObjectInput8 = paramRemoteCall.getInputStream();
        localLong = (Long)localObjectInput8.readObject();
      }
      catch (IOException localIOException31)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException31);
      }
      catch (ClassNotFoundException localClassNotFoundException8)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException8);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      int n = localRMIImageImpl.getMinY(localLong);
      try
      {
        ObjectOutput localObjectOutput7 = paramRemoteCall.getResultStream(true);
        localObjectOutput7.writeInt(n);
      }
      catch (IOException localIOException9)
      {
        throw new MarshalException("error marshalling return", localIOException9);
      }
    case 10: 
      try
      {
        ObjectInput localObjectInput9 = paramRemoteCall.getInputStream();
        localLong = (Long)localObjectInput9.readObject();
      }
      catch (IOException localIOException32)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException32);
      }
      catch (ClassNotFoundException localClassNotFoundException9)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException9);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      int i1 = localRMIImageImpl.getNumXTiles(localLong);
      try
      {
        ObjectOutput localObjectOutput8 = paramRemoteCall.getResultStream(true);
        localObjectOutput8.writeInt(i1);
      }
      catch (IOException localIOException10)
      {
        throw new MarshalException("error marshalling return", localIOException10);
      }
    case 11: 
      try
      {
        ObjectInput localObjectInput10 = paramRemoteCall.getInputStream();
        localLong = (Long)localObjectInput10.readObject();
      }
      catch (IOException localIOException33)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException33);
      }
      catch (ClassNotFoundException localClassNotFoundException10)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException10);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      int i2 = localRMIImageImpl.getNumYTiles(localLong);
      try
      {
        ObjectOutput localObjectOutput9 = paramRemoteCall.getResultStream(true);
        localObjectOutput9.writeInt(i2);
      }
      catch (IOException localIOException11)
      {
        throw new MarshalException("error marshalling return", localIOException11);
      }
    case 12: 
      String str;
      try
      {
        ObjectInput localObjectInput20 = paramRemoteCall.getInputStream();
        localLong = (Long)localObjectInput20.readObject();
        str = (String)localObjectInput20.readObject();
      }
      catch (IOException localIOException46)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException46);
      }
      catch (ClassNotFoundException localClassNotFoundException21)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException21);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      Object localObject35 = localRMIImageImpl.getProperty(localLong, str);
      try
      {
        ObjectOutput localObjectOutput20 = paramRemoteCall.getResultStream(true);
        localObjectOutput20.writeObject(localObject35);
      }
      catch (IOException localIOException34)
      {
        throw new MarshalException("error marshalling return", localIOException34);
      }
    case 13: 
      try
      {
        ObjectInput localObjectInput11 = paramRemoteCall.getInputStream();
        localLong = (Long)localObjectInput11.readObject();
      }
      catch (IOException localIOException35)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException35);
      }
      catch (ClassNotFoundException localClassNotFoundException11)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException11);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      localObject13 = localRMIImageImpl.getPropertyNames(localLong);
      try
      {
        ObjectOutput localObjectOutput10 = paramRemoteCall.getResultStream(true);
        localObjectOutput10.writeObject(localObject13);
      }
      catch (IOException localIOException12)
      {
        throw new MarshalException("error marshalling return", localIOException12);
      }
    case 14: 
      paramRemoteCall.releaseInputStream();
      localLong = localRMIImageImpl.getRemoteID();
      try
      {
        localObject13 = paramRemoteCall.getResultStream(true);
        ((ObjectOutput)localObject13).writeObject(localLong);
      }
      catch (IOException localIOException2)
      {
        throw new MarshalException("error marshalling return", localIOException2);
      }
    case 15: 
      try
      {
        ObjectInput localObjectInput12 = paramRemoteCall.getInputStream();
        localLong = (Long)localObjectInput12.readObject();
      }
      catch (IOException localIOException36)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException36);
      }
      catch (ClassNotFoundException localClassNotFoundException12)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException12);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      SampleModelProxy localSampleModelProxy = localRMIImageImpl.getSampleModel(localLong);
      try
      {
        ObjectOutput localObjectOutput11 = paramRemoteCall.getResultStream(true);
        localObjectOutput11.writeObject(localSampleModelProxy);
      }
      catch (IOException localIOException13)
      {
        throw new MarshalException("error marshalling return", localIOException13);
      }
    case 16: 
      try
      {
        ObjectInput localObjectInput13 = paramRemoteCall.getInputStream();
        localLong = (Long)localObjectInput13.readObject();
      }
      catch (IOException localIOException37)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException37);
      }
      catch (ClassNotFoundException localClassNotFoundException13)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException13);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      Vector localVector = localRMIImageImpl.getSources(localLong);
      try
      {
        ObjectOutput localObjectOutput12 = paramRemoteCall.getResultStream(true);
        localObjectOutput12.writeObject(localVector);
      }
      catch (IOException localIOException14)
      {
        throw new MarshalException("error marshalling return", localIOException14);
      }
    case 17: 
      int i3;
      int i9;
      try
      {
        ObjectInput localObjectInput23 = paramRemoteCall.getInputStream();
        localLong = (Long)localObjectInput23.readObject();
        i3 = localObjectInput23.readInt();
        i9 = localObjectInput23.readInt();
      }
      catch (IOException localIOException50)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException50);
      }
      catch (ClassNotFoundException localClassNotFoundException24)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException24);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      localObject50 = localRMIImageImpl.getTile(localLong, i3, i9);
      try
      {
        ObjectOutput localObjectOutput21 = paramRemoteCall.getResultStream(true);
        localObjectOutput21.writeObject(localObject50);
      }
      catch (IOException localIOException47)
      {
        throw new MarshalException("error marshalling return", localIOException47);
      }
    case 18: 
      try
      {
        localObject50 = paramRemoteCall.getInputStream();
        localLong = (Long)((ObjectInput)localObject50).readObject();
      }
      catch (IOException localIOException38)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException38);
      }
      catch (ClassNotFoundException localClassNotFoundException14)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException14);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      int i4 = localRMIImageImpl.getTileGridXOffset(localLong);
      try
      {
        ObjectOutput localObjectOutput13 = paramRemoteCall.getResultStream(true);
        localObjectOutput13.writeInt(i4);
      }
      catch (IOException localIOException15)
      {
        throw new MarshalException("error marshalling return", localIOException15);
      }
    case 19: 
      try
      {
        ObjectInput localObjectInput14 = paramRemoteCall.getInputStream();
        localLong = (Long)localObjectInput14.readObject();
      }
      catch (IOException localIOException39)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException39);
      }
      catch (ClassNotFoundException localClassNotFoundException15)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException15);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      int i5 = localRMIImageImpl.getTileGridYOffset(localLong);
      try
      {
        ObjectOutput localObjectOutput14 = paramRemoteCall.getResultStream(true);
        localObjectOutput14.writeInt(i5);
      }
      catch (IOException localIOException16)
      {
        throw new MarshalException("error marshalling return", localIOException16);
      }
    case 20: 
      try
      {
        ObjectInput localObjectInput15 = paramRemoteCall.getInputStream();
        localLong = (Long)localObjectInput15.readObject();
      }
      catch (IOException localIOException40)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException40);
      }
      catch (ClassNotFoundException localClassNotFoundException16)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException16);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      int i6 = localRMIImageImpl.getTileHeight(localLong);
      try
      {
        ObjectOutput localObjectOutput15 = paramRemoteCall.getResultStream(true);
        localObjectOutput15.writeInt(i6);
      }
      catch (IOException localIOException17)
      {
        throw new MarshalException("error marshalling return", localIOException17);
      }
    case 21: 
      try
      {
        ObjectInput localObjectInput16 = paramRemoteCall.getInputStream();
        localLong = (Long)localObjectInput16.readObject();
      }
      catch (IOException localIOException41)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException41);
      }
      catch (ClassNotFoundException localClassNotFoundException17)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException17);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      int i7 = localRMIImageImpl.getTileWidth(localLong);
      try
      {
        ObjectOutput localObjectOutput16 = paramRemoteCall.getResultStream(true);
        localObjectOutput16.writeInt(i7);
      }
      catch (IOException localIOException18)
      {
        throw new MarshalException("error marshalling return", localIOException18);
      }
    case 22: 
      try
      {
        ObjectInput localObjectInput17 = paramRemoteCall.getInputStream();
        localLong = (Long)localObjectInput17.readObject();
      }
      catch (IOException localIOException42)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException42);
      }
      catch (ClassNotFoundException localClassNotFoundException18)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException18);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      int i8 = localRMIImageImpl.getWidth(localLong);
      try
      {
        ObjectOutput localObjectOutput17 = paramRemoteCall.getResultStream(true);
        localObjectOutput17.writeInt(i8);
      }
      catch (IOException localIOException19)
      {
        throw new MarshalException("error marshalling return", localIOException19);
      }
    case 23: 
      try
      {
        ObjectInput localObjectInput21 = paramRemoteCall.getInputStream();
        localLong = (Long)localObjectInput21.readObject();
        localObject21 = (RenderedImage)localObjectInput21.readObject();
      }
      catch (IOException localIOException48)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException48);
      }
      catch (ClassNotFoundException localClassNotFoundException22)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException22);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      localRMIImageImpl.setSource(localLong, (RenderedImage)localObject21);
      try
      {
        paramRemoteCall.getResultStream(true);
      }
      catch (IOException localIOException20)
      {
        throw new MarshalException("error marshalling return", localIOException20);
      }
    case 24: 
      RenderContextProxy localRenderContextProxy;
      try
      {
        ObjectInput localObjectInput24 = paramRemoteCall.getInputStream();
        localLong = (Long)localObjectInput24.readObject();
        localObject21 = (RenderableOp)localObjectInput24.readObject();
        localRenderContextProxy = (RenderContextProxy)localObjectInput24.readObject();
      }
      catch (IOException localIOException51)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException51);
      }
      catch (ClassNotFoundException localClassNotFoundException25)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException25);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      localRMIImageImpl.setSource(localLong, (RenderableOp)localObject21, localRenderContextProxy);
      try
      {
        paramRemoteCall.getResultStream(true);
      }
      catch (IOException localIOException43)
      {
        throw new MarshalException("error marshalling return", localIOException43);
      }
    case 25: 
      try
      {
        ObjectInput localObjectInput22 = paramRemoteCall.getInputStream();
        localLong = (Long)localObjectInput22.readObject();
        localObject21 = (RenderedOp)localObjectInput22.readObject();
      }
      catch (IOException localIOException49)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException49);
      }
      catch (ClassNotFoundException localClassNotFoundException23)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException23);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      localRMIImageImpl.setSource(localLong, (RenderedOp)localObject21);
      try
      {
        paramRemoteCall.getResultStream(true);
      }
      catch (IOException localIOException21)
      {
        throw new MarshalException("error marshalling return", localIOException21);
      }
    default: 
      throw new UnmarshalException("invalid method number");
    }
  }
  
  public Operation[] getOperations()
  {
    return (Operation[])operations.clone();
  }
}
