package com.sun.media.jai.rmi;

import java.awt.Rectangle;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
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
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;
import javax.media.jai.remote.NegotiableCapabilitySet;
import javax.media.jai.remote.SerializableState;

public final class JAIRMIImageServer_Skel
  implements Skeleton
{
  private static final Operation[] operations = { new Operation("javax.media.jai.remote.SerializableState copyData(java.lang.Long, java.awt.Rectangle)"), new Operation("java.awt.image.RenderedImage createDefaultRendering(java.lang.Long)"), new Operation("void createRenderableOp(java.lang.Long, java.lang.String, java.awt.image.renderable.ParameterBlock)"), new Operation("void createRenderedOp(java.lang.Long, java.lang.String, java.awt.image.renderable.ParameterBlock, javax.media.jai.remote.SerializableState)"), new Operation("java.awt.image.RenderedImage createRendering(java.lang.Long, javax.media.jai.remote.SerializableState)"), new Operation("java.awt.image.RenderedImage createScaledRendering(java.lang.Long, int, int, javax.media.jai.remote.SerializableState)"), new Operation("void dispose(java.lang.Long)"), new Operation("javax.media.jai.remote.SerializableState getBounds2D(java.lang.Long, java.lang.String)"), new Operation("javax.media.jai.remote.SerializableState getColorModel(java.lang.Long)"), new Operation("byte getCompressedTile(java.lang.Long, int, int)[]"), new Operation("javax.media.jai.remote.SerializableState getData(java.lang.Long)"), new Operation("javax.media.jai.remote.SerializableState getData(java.lang.Long, java.awt.Rectangle)"), new Operation("int getHeight(java.lang.Long)"), new Operation("javax.media.jai.remote.SerializableState getInvalidRegion(java.lang.Long, java.awt.image.renderable.ParameterBlock, javax.media.jai.remote.SerializableState, java.awt.image.renderable.ParameterBlock, javax.media.jai.remote.SerializableState)"), new Operation("int getMinTileX(java.lang.Long)"), new Operation("int getMinTileY(java.lang.Long)"), new Operation("int getMinX(java.lang.Long)"), new Operation("int getMinY(java.lang.Long)"), new Operation("javax.media.jai.RenderedOp getNode(java.lang.Long)"), new Operation("int getNumXTiles(java.lang.Long)"), new Operation("int getNumYTiles(java.lang.Long)"), new Operation("java.util.List getOperationDescriptors()"), new Operation("java.lang.Object getProperty(java.lang.Long, java.lang.String)"), new Operation("java.lang.String getPropertyNames(java.lang.Long)[]"), new Operation("java.lang.String getPropertyNames(java.lang.String)[]"), new Operation("java.lang.Long getRemoteID()"), new Operation("float getRenderableHeight(java.lang.Long)"), new Operation("float getRenderableMinX(java.lang.Long)"), new Operation("float getRenderableMinY(java.lang.Long)"), new Operation("float getRenderableWidth(java.lang.Long)"), new Operation("boolean getRendering(java.lang.Long)"), new Operation("java.lang.Long getRendering(java.lang.Long, javax.media.jai.remote.SerializableState)"), new Operation("javax.media.jai.remote.SerializableState getSampleModel(java.lang.Long)"), new Operation("javax.media.jai.remote.NegotiableCapabilitySet getServerCapabilities()"), new Operation("java.lang.String getServerSupportedOperationNames()[]"), new Operation("javax.media.jai.remote.SerializableState getTile(java.lang.Long, int, int)"), new Operation("int getTileGridXOffset(java.lang.Long)"), new Operation("int getTileGridYOffset(java.lang.Long)"), new Operation("int getTileHeight(java.lang.Long)"), new Operation("int getTileWidth(java.lang.Long)"), new Operation("int getWidth(java.lang.Long)"), new Operation("java.lang.Long handleEvent(java.lang.Long, int, javax.media.jai.remote.SerializableState, java.lang.Object)"), new Operation("java.lang.Long handleEvent(java.lang.Long, java.lang.String, java.lang.Object, java.lang.Object)"), new Operation("void incrementRefCount(java.lang.Long)"), new Operation("boolean isDynamic(java.lang.Long)"), new Operation("boolean isDynamic(java.lang.String)"), new Operation("java.awt.Rectangle mapDestRect(java.lang.Long, java.awt.Rectangle, int)"), new Operation("javax.media.jai.remote.SerializableState mapRenderContext(int, java.lang.Long, java.lang.String, javax.media.jai.remote.SerializableState)"), new Operation("java.awt.Rectangle mapSourceRect(java.lang.Long, java.awt.Rectangle, int)"), new Operation("void setRenderableRMIServerProxyAsSource(java.lang.Long, java.lang.Long, java.lang.String, java.lang.String, int)"), new Operation("void setRenderableSource(java.lang.Long, com.sun.media.jai.rmi.SerializableRenderableImage, int)"), new Operation("void setRenderableSource(java.lang.Long, java.awt.image.RenderedImage, int)"), new Operation("void setRenderableSource(java.lang.Long, java.lang.Long, int)"), new Operation("void setRenderableSource(java.lang.Long, java.lang.Long, java.lang.String, java.lang.String, int)"), new Operation("void setRenderableSource(java.lang.Long, javax.media.jai.RenderableOp, int)"), new Operation("void setRenderedSource(java.lang.Long, java.awt.image.RenderedImage, int)"), new Operation("void setRenderedSource(java.lang.Long, java.lang.Long, int)"), new Operation("void setRenderedSource(java.lang.Long, java.lang.Long, java.lang.String, java.lang.String, int)"), new Operation("void setRenderedSource(java.lang.Long, javax.media.jai.RenderedOp, int)"), new Operation("void setServerNegotiatedValues(java.lang.Long, javax.media.jai.remote.NegotiableCapabilitySet)") };
  private static final long interfaceHash = 6167769405001739342L;
  
  public JAIRMIImageServer_Skel() {}
  
  public void dispatch(Remote paramRemote, RemoteCall paramRemoteCall, int paramInt, long paramLong)
    throws Exception
  {
    if (paramInt < 0)
    {
      if (paramLong == -967509352521768614L) {
        paramInt = 0;
      } else if (paramLong == -8497891458627429487L) {
        paramInt = 1;
      } else if (paramLong == 7086259789809689998L) {
        paramInt = 2;
      } else if (paramLong == 5101379426256032149L) {
        paramInt = 3;
      } else if (paramLong == -5245001515136243438L) {
        paramInt = 4;
      } else if (paramLong == 2752392759141353347L) {
        paramInt = 5;
      } else if (paramLong == 6460799139781649959L) {
        paramInt = 6;
      } else if (paramLong == -7344372886056435090L) {
        paramInt = 7;
      } else if (paramLong == -1100163628488185119L) {
        paramInt = 8;
      } else if (paramLong == -1379943561537216322L) {
        paramInt = 9;
      } else if (paramLong == 6361054168006114985L) {
        paramInt = 10;
      } else if (paramLong == -3749893868609537021L) {
        paramInt = 11;
      } else if (paramLong == -7560603472052038977L) {
        paramInt = 12;
      } else if (paramLong == 2196538291040842281L) {
        paramInt = 13;
      } else if (paramLong == 5809966745410438246L) {
        paramInt = 14;
      } else if (paramLong == -9076617268613815876L) {
        paramInt = 15;
      } else if (paramLong == -5297535099750447733L) {
        paramInt = 16;
      } else if (paramLong == 7733459005376369327L) {
        paramInt = 17;
      } else if (paramLong == 9161432851012319050L) {
        paramInt = 18;
      } else if (paramLong == 3645100420184954761L) {
        paramInt = 19;
      } else if (paramLong == -1731091968647972742L) {
        paramInt = 20;
      } else if (paramLong == 3535648159716437706L) {
        paramInt = 21;
      } else if (paramLong == 216968610676295195L) {
        paramInt = 22;
      } else if (paramLong == 3931591828613160321L) {
        paramInt = 23;
      } else if (paramLong == 316409741847260476L) {
        paramInt = 24;
      } else if (paramLong == -232353888923603427L) {
        paramInt = 25;
      } else if (paramLong == 5608422195731594411L) {
        paramInt = 26;
      } else if (paramLong == 2691228702599857582L) {
        paramInt = 27;
      } else if (paramLong == 4212368935241858980L) {
        paramInt = 28;
      } else if (paramLong == 5338396004630022671L) {
        paramInt = 29;
      } else if (paramLong == -2265440493870323208L) {
        paramInt = 30;
      } else if (paramLong == -6125241444070859614L) {
        paramInt = 31;
      } else if (paramLong == -1813341280855901292L) {
        paramInt = 32;
      } else if (paramLong == -5684371542470892640L) {
        paramInt = 33;
      } else if (paramLong == -4886984326445878690L) {
        paramInt = 34;
      } else if (paramLong == 3187214795636220126L) {
        paramInt = 35;
      } else if (paramLong == -8218495432205133449L) {
        paramInt = 36;
      } else if (paramLong == -7482127068346373541L) {
        paramInt = 37;
      } else if (paramLong == 7785669351714030715L) {
        paramInt = 38;
      } else if (paramLong == 282122131312695349L) {
        paramInt = 39;
      } else if (paramLong == -8357318297729299690L) {
        paramInt = 40;
      } else if (paramLong == -2091789747834377998L) {
        paramInt = 41;
      } else if (paramLong == 6735595879989328767L) {
        paramInt = 42;
      } else if (paramLong == -3309069034569190342L) {
        paramInt = 43;
      } else if (paramLong == 9106025340051027274L) {
        paramInt = 44;
      } else if (paramLong == -6284830256520969130L) {
        paramInt = 45;
      } else if (paramLong == 2783117304536308041L) {
        paramInt = 46;
      } else if (paramLong == 3382362498715729166L) {
        paramInt = 47;
      } else if (paramLong == -5162241366759407841L) {
        paramInt = 48;
      } else if (paramLong == -1865549286439023174L) {
        paramInt = 49;
      } else if (paramLong == -2003236639401449658L) {
        paramInt = 50;
      } else if (paramLong == -8080617916453915737L) {
        paramInt = 51;
      } else if (paramLong == -7879955699630425072L) {
        paramInt = 52;
      } else if (paramLong == -5890575207352710342L) {
        paramInt = 53;
      } else if (paramLong == -8761942329287512340L) {
        paramInt = 54;
      } else if (paramLong == 2834995389306513647L) {
        paramInt = 55;
      } else if (paramLong == -6335170796820847995L) {
        paramInt = 56;
      } else if (paramLong == -1071494500456449009L) {
        paramInt = 57;
      } else if (paramLong == -7819102304157660296L) {
        paramInt = 58;
      } else if (paramLong == -27037179580597379L) {
        paramInt = 59;
      } else {
        throw new UnmarshalException("invalid method hash");
      }
    }
    else if (paramLong != 6167769405001739342L) {
      throw new SkeletonMismatchException("interface hash mismatch");
    }
    JAIRMIImageServer localJAIRMIImageServer = (JAIRMIImageServer)paramRemote;
    Object localObject1;
    Object localObject3;
    ParameterBlock localParameterBlock2;
    Object localObject75;
    Object localObject101;
    Object localObject78;
    Object localObject7;
    Object localObject80;
    Object localObject103;
    Object localObject120;
    Object localObject18;
    Object localObject25;
    Object localObject84;
    int i11;
    Object localObject67;
    Object localObject85;
    Object localObject34;
    Object localObject87;
    Long localLong1;
    Object localObject89;
    int i17;
    int i18;
    switch (paramInt)
    {
    case 0: 
      Rectangle localRectangle;
      try
      {
        ObjectInput localObjectInput24 = paramRemoteCall.getInputStream();
        localObject1 = (Long)localObjectInput24.readObject();
        localRectangle = (Rectangle)localObjectInput24.readObject();
      }
      catch (IOException localIOException76)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException76);
      }
      catch (ClassNotFoundException localClassNotFoundException29)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException29);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      SerializableState localSerializableState3 = localJAIRMIImageServer.copyData((Long)localObject1, localRectangle);
      try
      {
        ObjectOutput localObjectOutput29 = paramRemoteCall.getResultStream(true);
        localObjectOutput29.writeObject(localSerializableState3);
      }
      catch (IOException localIOException34)
      {
        throw new MarshalException("error marshalling return", localIOException34);
      }
    case 1: 
      try
      {
        ObjectInput localObjectInput1 = paramRemoteCall.getInputStream();
        localObject1 = (Long)localObjectInput1.readObject();
      }
      catch (IOException localIOException35)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException35);
      }
      catch (ClassNotFoundException localClassNotFoundException1)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException1);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      localObject3 = localJAIRMIImageServer.createDefaultRendering((Long)localObject1);
      try
      {
        ObjectOutput localObjectOutput3 = paramRemoteCall.getResultStream(true);
        localObjectOutput3.writeObject(localObject3);
      }
      catch (IOException localIOException7)
      {
        throw new MarshalException("error marshalling return", localIOException7);
      }
    case 2: 
      try
      {
        ObjectInput localObjectInput29 = paramRemoteCall.getInputStream();
        localObject1 = (Long)localObjectInput29.readObject();
        localObject3 = (String)localObjectInput29.readObject();
        localParameterBlock2 = (ParameterBlock)localObjectInput29.readObject();
      }
      catch (IOException localIOException88)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException88);
      }
      catch (ClassNotFoundException localClassNotFoundException36)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException36);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      localJAIRMIImageServer.createRenderableOp((Long)localObject1, (String)localObject3, localParameterBlock2);
      try
      {
        paramRemoteCall.getResultStream(true);
      }
      catch (IOException localIOException36)
      {
        throw new MarshalException("error marshalling return", localIOException36);
      }
    case 3: 
      SerializableState localSerializableState7;
      try
      {
        ObjectInput localObjectInput40 = paramRemoteCall.getInputStream();
        localObject1 = (Long)localObjectInput40.readObject();
        localObject3 = (String)localObjectInput40.readObject();
        localParameterBlock2 = (ParameterBlock)localObjectInput40.readObject();
        localSerializableState7 = (SerializableState)localObjectInput40.readObject();
      }
      catch (IOException localIOException107)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException107);
      }
      catch (ClassNotFoundException localClassNotFoundException48)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException48);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      localJAIRMIImageServer.createRenderedOp((Long)localObject1, (String)localObject3, localParameterBlock2, localSerializableState7);
      try
      {
        paramRemoteCall.getResultStream(true);
      }
      catch (IOException localIOException77)
      {
        throw new MarshalException("error marshalling return", localIOException77);
      }
    case 4: 
      try
      {
        ObjectInput localObjectInput25 = paramRemoteCall.getInputStream();
        localObject1 = (Long)localObjectInput25.readObject();
        localObject3 = (SerializableState)localObjectInput25.readObject();
      }
      catch (IOException localIOException78)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException78);
      }
      catch (ClassNotFoundException localClassNotFoundException30)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException30);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      RenderedImage localRenderedImage = localJAIRMIImageServer.createRendering((Long)localObject1, (SerializableState)localObject3);
      try
      {
        ObjectOutput localObjectOutput30 = paramRemoteCall.getResultStream(true);
        localObjectOutput30.writeObject(localRenderedImage);
      }
      catch (IOException localIOException37)
      {
        throw new MarshalException("error marshalling return", localIOException37);
      }
    case 5: 
      int j;
      int i12;
      try
      {
        ObjectInput localObjectInput41 = paramRemoteCall.getInputStream();
        localObject1 = (Long)localObjectInput41.readObject();
        j = localObjectInput41.readInt();
        i12 = localObjectInput41.readInt();
        localObject75 = (SerializableState)localObjectInput41.readObject();
      }
      catch (IOException localIOException108)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException108);
      }
      catch (ClassNotFoundException localClassNotFoundException49)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException49);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      localObject101 = localJAIRMIImageServer.createScaledRendering((Long)localObject1, j, i12, (SerializableState)localObject75);
      try
      {
        ObjectOutput localObjectOutput39 = paramRemoteCall.getResultStream(true);
        localObjectOutput39.writeObject(localObject101);
      }
      catch (IOException localIOException89)
      {
        throw new MarshalException("error marshalling return", localIOException89);
      }
    case 6: 
      try
      {
        localObject75 = paramRemoteCall.getInputStream();
        localObject1 = (Long)((ObjectInput)localObject75).readObject();
      }
      catch (IOException localIOException38)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException38);
      }
      catch (ClassNotFoundException localClassNotFoundException2)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException2);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      localJAIRMIImageServer.dispose((Long)localObject1);
      try
      {
        paramRemoteCall.getResultStream(true);
      }
      catch (IOException localIOException1)
      {
        throw new MarshalException("error marshalling return", localIOException1);
      }
    case 7: 
      String str1;
      try
      {
        localObject101 = paramRemoteCall.getInputStream();
        localObject1 = (Long)((ObjectInput)localObject101).readObject();
        str1 = (String)((ObjectInput)localObject101).readObject();
      }
      catch (IOException localIOException79)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException79);
      }
      catch (ClassNotFoundException localClassNotFoundException31)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException31);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      SerializableState localSerializableState4 = localJAIRMIImageServer.getBounds2D((Long)localObject1, str1);
      try
      {
        ObjectOutput localObjectOutput31 = paramRemoteCall.getResultStream(true);
        localObjectOutput31.writeObject(localSerializableState4);
      }
      catch (IOException localIOException39)
      {
        throw new MarshalException("error marshalling return", localIOException39);
      }
    case 8: 
      try
      {
        ObjectInput localObjectInput2 = paramRemoteCall.getInputStream();
        localObject1 = (Long)localObjectInput2.readObject();
      }
      catch (IOException localIOException40)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException40);
      }
      catch (ClassNotFoundException localClassNotFoundException3)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException3);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      SerializableState localSerializableState1 = localJAIRMIImageServer.getColorModel((Long)localObject1);
      try
      {
        ObjectOutput localObjectOutput4 = paramRemoteCall.getResultStream(true);
        localObjectOutput4.writeObject(localSerializableState1);
      }
      catch (IOException localIOException8)
      {
        throw new MarshalException("error marshalling return", localIOException8);
      }
    case 9: 
      int k;
      int i13;
      try
      {
        ObjectInput localObjectInput30 = paramRemoteCall.getInputStream();
        localObject1 = (Long)localObjectInput30.readObject();
        k = localObjectInput30.readInt();
        i13 = localObjectInput30.readInt();
      }
      catch (IOException localIOException90)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException90);
      }
      catch (ClassNotFoundException localClassNotFoundException37)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException37);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      localObject78 = localJAIRMIImageServer.getCompressedTile((Long)localObject1, k, i13);
      try
      {
        ObjectOutput localObjectOutput35 = paramRemoteCall.getResultStream(true);
        localObjectOutput35.writeObject(localObject78);
      }
      catch (IOException localIOException80)
      {
        throw new MarshalException("error marshalling return", localIOException80);
      }
    case 10: 
      try
      {
        localObject78 = paramRemoteCall.getInputStream();
        localObject1 = (Long)((ObjectInput)localObject78).readObject();
      }
      catch (IOException localIOException41)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException41);
      }
      catch (ClassNotFoundException localClassNotFoundException4)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException4);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      localObject7 = localJAIRMIImageServer.getData((Long)localObject1);
      try
      {
        ObjectOutput localObjectOutput5 = paramRemoteCall.getResultStream(true);
        localObjectOutput5.writeObject(localObject7);
      }
      catch (IOException localIOException9)
      {
        throw new MarshalException("error marshalling return", localIOException9);
      }
    case 11: 
      try
      {
        ObjectInput localObjectInput26 = paramRemoteCall.getInputStream();
        localObject1 = (Long)localObjectInput26.readObject();
        localObject7 = (Rectangle)localObjectInput26.readObject();
      }
      catch (IOException localIOException81)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException81);
      }
      catch (ClassNotFoundException localClassNotFoundException32)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException32);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      SerializableState localSerializableState5 = localJAIRMIImageServer.getData((Long)localObject1, (Rectangle)localObject7);
      try
      {
        ObjectOutput localObjectOutput32 = paramRemoteCall.getResultStream(true);
        localObjectOutput32.writeObject(localSerializableState5);
      }
      catch (IOException localIOException42)
      {
        throw new MarshalException("error marshalling return", localIOException42);
      }
    case 12: 
      try
      {
        ObjectInput localObjectInput3 = paramRemoteCall.getInputStream();
        localObject1 = (Long)localObjectInput3.readObject();
      }
      catch (IOException localIOException43)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException43);
      }
      catch (ClassNotFoundException localClassNotFoundException5)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException5);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      int m = localJAIRMIImageServer.getHeight((Long)localObject1);
      try
      {
        ObjectOutput localObjectOutput6 = paramRemoteCall.getResultStream(true);
        localObjectOutput6.writeInt(m);
      }
      catch (IOException localIOException10)
      {
        throw new MarshalException("error marshalling return", localIOException10);
      }
    case 13: 
      ParameterBlock localParameterBlock1;
      SerializableState localSerializableState6;
      try
      {
        ObjectInput localObjectInput45 = paramRemoteCall.getInputStream();
        localObject1 = (Long)localObjectInput45.readObject();
        localParameterBlock1 = (ParameterBlock)localObjectInput45.readObject();
        localSerializableState6 = (SerializableState)localObjectInput45.readObject();
        localObject80 = (ParameterBlock)localObjectInput45.readObject();
        localObject103 = (SerializableState)localObjectInput45.readObject();
      }
      catch (IOException localIOException113)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException113);
      }
      catch (ClassNotFoundException localClassNotFoundException53)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException53);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      localObject120 = localJAIRMIImageServer.getInvalidRegion((Long)localObject1, localParameterBlock1, localSerializableState6, (ParameterBlock)localObject80, (SerializableState)localObject103);
      try
      {
        ObjectOutput localObjectOutput43 = paramRemoteCall.getResultStream(true);
        localObjectOutput43.writeObject(localObject120);
      }
      catch (IOException localIOException109)
      {
        throw new MarshalException("error marshalling return", localIOException109);
      }
    case 14: 
      try
      {
        localObject80 = paramRemoteCall.getInputStream();
        localObject1 = (Long)((ObjectInput)localObject80).readObject();
      }
      catch (IOException localIOException44)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException44);
      }
      catch (ClassNotFoundException localClassNotFoundException6)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException6);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      int n = localJAIRMIImageServer.getMinTileX((Long)localObject1);
      try
      {
        ObjectOutput localObjectOutput7 = paramRemoteCall.getResultStream(true);
        localObjectOutput7.writeInt(n);
      }
      catch (IOException localIOException11)
      {
        throw new MarshalException("error marshalling return", localIOException11);
      }
    case 15: 
      try
      {
        ObjectInput localObjectInput4 = paramRemoteCall.getInputStream();
        localObject1 = (Long)localObjectInput4.readObject();
      }
      catch (IOException localIOException45)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException45);
      }
      catch (ClassNotFoundException localClassNotFoundException7)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException7);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      int i1 = localJAIRMIImageServer.getMinTileY((Long)localObject1);
      try
      {
        ObjectOutput localObjectOutput8 = paramRemoteCall.getResultStream(true);
        localObjectOutput8.writeInt(i1);
      }
      catch (IOException localIOException12)
      {
        throw new MarshalException("error marshalling return", localIOException12);
      }
    case 16: 
      try
      {
        ObjectInput localObjectInput5 = paramRemoteCall.getInputStream();
        localObject1 = (Long)localObjectInput5.readObject();
      }
      catch (IOException localIOException46)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException46);
      }
      catch (ClassNotFoundException localClassNotFoundException8)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException8);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      int i2 = localJAIRMIImageServer.getMinX((Long)localObject1);
      try
      {
        ObjectOutput localObjectOutput9 = paramRemoteCall.getResultStream(true);
        localObjectOutput9.writeInt(i2);
      }
      catch (IOException localIOException13)
      {
        throw new MarshalException("error marshalling return", localIOException13);
      }
    case 17: 
      try
      {
        ObjectInput localObjectInput6 = paramRemoteCall.getInputStream();
        localObject1 = (Long)localObjectInput6.readObject();
      }
      catch (IOException localIOException47)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException47);
      }
      catch (ClassNotFoundException localClassNotFoundException9)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException9);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      int i3 = localJAIRMIImageServer.getMinY((Long)localObject1);
      try
      {
        ObjectOutput localObjectOutput10 = paramRemoteCall.getResultStream(true);
        localObjectOutput10.writeInt(i3);
      }
      catch (IOException localIOException14)
      {
        throw new MarshalException("error marshalling return", localIOException14);
      }
    case 18: 
      try
      {
        ObjectInput localObjectInput7 = paramRemoteCall.getInputStream();
        localObject1 = (Long)localObjectInput7.readObject();
      }
      catch (IOException localIOException48)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException48);
      }
      catch (ClassNotFoundException localClassNotFoundException10)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException10);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      RenderedOp localRenderedOp = localJAIRMIImageServer.getNode((Long)localObject1);
      try
      {
        ObjectOutput localObjectOutput11 = paramRemoteCall.getResultStream(true);
        localObjectOutput11.writeObject(localRenderedOp);
      }
      catch (IOException localIOException15)
      {
        throw new MarshalException("error marshalling return", localIOException15);
      }
    case 19: 
      try
      {
        ObjectInput localObjectInput8 = paramRemoteCall.getInputStream();
        localObject1 = (Long)localObjectInput8.readObject();
      }
      catch (IOException localIOException49)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException49);
      }
      catch (ClassNotFoundException localClassNotFoundException11)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException11);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      int i4 = localJAIRMIImageServer.getNumXTiles((Long)localObject1);
      try
      {
        ObjectOutput localObjectOutput12 = paramRemoteCall.getResultStream(true);
        localObjectOutput12.writeInt(i4);
      }
      catch (IOException localIOException16)
      {
        throw new MarshalException("error marshalling return", localIOException16);
      }
    case 20: 
      try
      {
        ObjectInput localObjectInput9 = paramRemoteCall.getInputStream();
        localObject1 = (Long)localObjectInput9.readObject();
      }
      catch (IOException localIOException50)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException50);
      }
      catch (ClassNotFoundException localClassNotFoundException12)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException12);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      int i5 = localJAIRMIImageServer.getNumYTiles((Long)localObject1);
      try
      {
        ObjectOutput localObjectOutput13 = paramRemoteCall.getResultStream(true);
        localObjectOutput13.writeInt(i5);
      }
      catch (IOException localIOException17)
      {
        throw new MarshalException("error marshalling return", localIOException17);
      }
    case 21: 
      paramRemoteCall.releaseInputStream();
      localObject1 = localJAIRMIImageServer.getOperationDescriptors();
      try
      {
        ObjectOutput localObjectOutput1 = paramRemoteCall.getResultStream(true);
        localObjectOutput1.writeObject(localObject1);
      }
      catch (IOException localIOException2)
      {
        throw new MarshalException("error marshalling return", localIOException2);
      }
    case 22: 
      String str2;
      try
      {
        localObject103 = paramRemoteCall.getInputStream();
        localObject1 = (Long)((ObjectInput)localObject103).readObject();
        str2 = (String)((ObjectInput)localObject103).readObject();
      }
      catch (IOException localIOException82)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException82);
      }
      catch (ClassNotFoundException localClassNotFoundException33)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException33);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      Object localObject52 = localJAIRMIImageServer.getProperty((Long)localObject1, str2);
      try
      {
        ObjectOutput localObjectOutput33 = paramRemoteCall.getResultStream(true);
        localObjectOutput33.writeObject(localObject52);
      }
      catch (IOException localIOException51)
      {
        throw new MarshalException("error marshalling return", localIOException51);
      }
    case 23: 
      try
      {
        ObjectInput localObjectInput10 = paramRemoteCall.getInputStream();
        localObject1 = (Long)localObjectInput10.readObject();
      }
      catch (IOException localIOException52)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException52);
      }
      catch (ClassNotFoundException localClassNotFoundException13)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException13);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      String[] arrayOfString = localJAIRMIImageServer.getPropertyNames((Long)localObject1);
      try
      {
        ObjectOutput localObjectOutput14 = paramRemoteCall.getResultStream(true);
        localObjectOutput14.writeObject(arrayOfString);
      }
      catch (IOException localIOException18)
      {
        throw new MarshalException("error marshalling return", localIOException18);
      }
    case 24: 
      try
      {
        ObjectInput localObjectInput11 = paramRemoteCall.getInputStream();
        localObject1 = (String)localObjectInput11.readObject();
      }
      catch (IOException localIOException53)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException53);
      }
      catch (ClassNotFoundException localClassNotFoundException14)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException14);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      localObject18 = localJAIRMIImageServer.getPropertyNames((String)localObject1);
      try
      {
        ObjectOutput localObjectOutput15 = paramRemoteCall.getResultStream(true);
        localObjectOutput15.writeObject(localObject18);
      }
      catch (IOException localIOException19)
      {
        throw new MarshalException("error marshalling return", localIOException19);
      }
    case 25: 
      paramRemoteCall.releaseInputStream();
      localObject1 = localJAIRMIImageServer.getRemoteID();
      try
      {
        localObject18 = paramRemoteCall.getResultStream(true);
        ((ObjectOutput)localObject18).writeObject(localObject1);
      }
      catch (IOException localIOException3)
      {
        throw new MarshalException("error marshalling return", localIOException3);
      }
    case 26: 
      try
      {
        ObjectInput localObjectInput12 = paramRemoteCall.getInputStream();
        localObject1 = (Long)localObjectInput12.readObject();
      }
      catch (IOException localIOException54)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException54);
      }
      catch (ClassNotFoundException localClassNotFoundException15)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException15);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      float f1 = localJAIRMIImageServer.getRenderableHeight((Long)localObject1);
      try
      {
        ObjectOutput localObjectOutput16 = paramRemoteCall.getResultStream(true);
        localObjectOutput16.writeFloat(f1);
      }
      catch (IOException localIOException20)
      {
        throw new MarshalException("error marshalling return", localIOException20);
      }
    case 27: 
      try
      {
        ObjectInput localObjectInput13 = paramRemoteCall.getInputStream();
        localObject1 = (Long)localObjectInput13.readObject();
      }
      catch (IOException localIOException55)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException55);
      }
      catch (ClassNotFoundException localClassNotFoundException16)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException16);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      float f2 = localJAIRMIImageServer.getRenderableMinX((Long)localObject1);
      try
      {
        ObjectOutput localObjectOutput17 = paramRemoteCall.getResultStream(true);
        localObjectOutput17.writeFloat(f2);
      }
      catch (IOException localIOException21)
      {
        throw new MarshalException("error marshalling return", localIOException21);
      }
    case 28: 
      try
      {
        ObjectInput localObjectInput14 = paramRemoteCall.getInputStream();
        localObject1 = (Long)localObjectInput14.readObject();
      }
      catch (IOException localIOException56)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException56);
      }
      catch (ClassNotFoundException localClassNotFoundException17)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException17);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      float f3 = localJAIRMIImageServer.getRenderableMinY((Long)localObject1);
      try
      {
        ObjectOutput localObjectOutput18 = paramRemoteCall.getResultStream(true);
        localObjectOutput18.writeFloat(f3);
      }
      catch (IOException localIOException22)
      {
        throw new MarshalException("error marshalling return", localIOException22);
      }
    case 29: 
      try
      {
        ObjectInput localObjectInput15 = paramRemoteCall.getInputStream();
        localObject1 = (Long)localObjectInput15.readObject();
      }
      catch (IOException localIOException57)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException57);
      }
      catch (ClassNotFoundException localClassNotFoundException18)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException18);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      float f4 = localJAIRMIImageServer.getRenderableWidth((Long)localObject1);
      try
      {
        ObjectOutput localObjectOutput19 = paramRemoteCall.getResultStream(true);
        localObjectOutput19.writeFloat(f4);
      }
      catch (IOException localIOException23)
      {
        throw new MarshalException("error marshalling return", localIOException23);
      }
    case 30: 
      try
      {
        ObjectInput localObjectInput16 = paramRemoteCall.getInputStream();
        localObject1 = (Long)localObjectInput16.readObject();
      }
      catch (IOException localIOException58)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException58);
      }
      catch (ClassNotFoundException localClassNotFoundException19)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException19);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      boolean bool1 = localJAIRMIImageServer.getRendering((Long)localObject1);
      try
      {
        ObjectOutput localObjectOutput20 = paramRemoteCall.getResultStream(true);
        localObjectOutput20.writeBoolean(bool1);
      }
      catch (IOException localIOException24)
      {
        throw new MarshalException("error marshalling return", localIOException24);
      }
    case 31: 
      SerializableState localSerializableState2;
      try
      {
        ObjectInput localObjectInput27 = paramRemoteCall.getInputStream();
        localObject1 = (Long)localObjectInput27.readObject();
        localSerializableState2 = (SerializableState)localObjectInput27.readObject();
      }
      catch (IOException localIOException83)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException83);
      }
      catch (ClassNotFoundException localClassNotFoundException34)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException34);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      Long localLong2 = localJAIRMIImageServer.getRendering((Long)localObject1, localSerializableState2);
      try
      {
        ObjectOutput localObjectOutput34 = paramRemoteCall.getResultStream(true);
        localObjectOutput34.writeObject(localLong2);
      }
      catch (IOException localIOException59)
      {
        throw new MarshalException("error marshalling return", localIOException59);
      }
    case 32: 
      try
      {
        ObjectInput localObjectInput17 = paramRemoteCall.getInputStream();
        localObject1 = (Long)localObjectInput17.readObject();
      }
      catch (IOException localIOException60)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException60);
      }
      catch (ClassNotFoundException localClassNotFoundException20)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException20);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      localObject25 = localJAIRMIImageServer.getSampleModel((Long)localObject1);
      try
      {
        ObjectOutput localObjectOutput21 = paramRemoteCall.getResultStream(true);
        localObjectOutput21.writeObject(localObject25);
      }
      catch (IOException localIOException25)
      {
        throw new MarshalException("error marshalling return", localIOException25);
      }
    case 33: 
      paramRemoteCall.releaseInputStream();
      localObject1 = localJAIRMIImageServer.getServerCapabilities();
      try
      {
        localObject25 = paramRemoteCall.getResultStream(true);
        ((ObjectOutput)localObject25).writeObject(localObject1);
      }
      catch (IOException localIOException4)
      {
        throw new MarshalException("error marshalling return", localIOException4);
      }
    case 34: 
      paramRemoteCall.releaseInputStream();
      localObject1 = localJAIRMIImageServer.getServerSupportedOperationNames();
      try
      {
        ObjectOutput localObjectOutput2 = paramRemoteCall.getResultStream(true);
        localObjectOutput2.writeObject(localObject1);
      }
      catch (IOException localIOException5)
      {
        throw new MarshalException("error marshalling return", localIOException5);
      }
    case 35: 
      int i6;
      int i14;
      try
      {
        localObject120 = paramRemoteCall.getInputStream();
        localObject1 = (Long)((ObjectInput)localObject120).readObject();
        i6 = ((DataInput)localObject120).readInt();
        i14 = ((DataInput)localObject120).readInt();
      }
      catch (IOException localIOException91)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException91);
      }
      catch (ClassNotFoundException localClassNotFoundException38)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException38);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      localObject84 = localJAIRMIImageServer.getTile((Long)localObject1, i6, i14);
      try
      {
        ObjectOutput localObjectOutput36 = paramRemoteCall.getResultStream(true);
        localObjectOutput36.writeObject(localObject84);
      }
      catch (IOException localIOException84)
      {
        throw new MarshalException("error marshalling return", localIOException84);
      }
    case 36: 
      try
      {
        localObject84 = paramRemoteCall.getInputStream();
        localObject1 = (Long)((ObjectInput)localObject84).readObject();
      }
      catch (IOException localIOException61)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException61);
      }
      catch (ClassNotFoundException localClassNotFoundException21)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException21);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      int i7 = localJAIRMIImageServer.getTileGridXOffset((Long)localObject1);
      try
      {
        ObjectOutput localObjectOutput22 = paramRemoteCall.getResultStream(true);
        localObjectOutput22.writeInt(i7);
      }
      catch (IOException localIOException26)
      {
        throw new MarshalException("error marshalling return", localIOException26);
      }
    case 37: 
      try
      {
        ObjectInput localObjectInput18 = paramRemoteCall.getInputStream();
        localObject1 = (Long)localObjectInput18.readObject();
      }
      catch (IOException localIOException62)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException62);
      }
      catch (ClassNotFoundException localClassNotFoundException22)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException22);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      int i8 = localJAIRMIImageServer.getTileGridYOffset((Long)localObject1);
      try
      {
        ObjectOutput localObjectOutput23 = paramRemoteCall.getResultStream(true);
        localObjectOutput23.writeInt(i8);
      }
      catch (IOException localIOException27)
      {
        throw new MarshalException("error marshalling return", localIOException27);
      }
    case 38: 
      try
      {
        ObjectInput localObjectInput19 = paramRemoteCall.getInputStream();
        localObject1 = (Long)localObjectInput19.readObject();
      }
      catch (IOException localIOException63)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException63);
      }
      catch (ClassNotFoundException localClassNotFoundException23)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException23);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      int i9 = localJAIRMIImageServer.getTileHeight((Long)localObject1);
      try
      {
        ObjectOutput localObjectOutput24 = paramRemoteCall.getResultStream(true);
        localObjectOutput24.writeInt(i9);
      }
      catch (IOException localIOException28)
      {
        throw new MarshalException("error marshalling return", localIOException28);
      }
    case 39: 
      try
      {
        ObjectInput localObjectInput20 = paramRemoteCall.getInputStream();
        localObject1 = (Long)localObjectInput20.readObject();
      }
      catch (IOException localIOException64)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException64);
      }
      catch (ClassNotFoundException localClassNotFoundException24)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException24);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      int i10 = localJAIRMIImageServer.getTileWidth((Long)localObject1);
      try
      {
        ObjectOutput localObjectOutput25 = paramRemoteCall.getResultStream(true);
        localObjectOutput25.writeInt(i10);
      }
      catch (IOException localIOException29)
      {
        throw new MarshalException("error marshalling return", localIOException29);
      }
    case 40: 
      try
      {
        ObjectInput localObjectInput21 = paramRemoteCall.getInputStream();
        localObject1 = (Long)localObjectInput21.readObject();
      }
      catch (IOException localIOException65)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException65);
      }
      catch (ClassNotFoundException localClassNotFoundException25)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException25);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      i11 = localJAIRMIImageServer.getWidth((Long)localObject1);
      try
      {
        ObjectOutput localObjectOutput26 = paramRemoteCall.getResultStream(true);
        localObjectOutput26.writeInt(i11);
      }
      catch (IOException localIOException30)
      {
        throw new MarshalException("error marshalling return", localIOException30);
      }
    case 41: 
      try
      {
        ObjectInput localObjectInput42 = paramRemoteCall.getInputStream();
        localObject1 = (Long)localObjectInput42.readObject();
        i11 = localObjectInput42.readInt();
        localObject67 = (SerializableState)localObjectInput42.readObject();
        localObject85 = localObjectInput42.readObject();
      }
      catch (IOException localIOException110)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException110);
      }
      catch (ClassNotFoundException localClassNotFoundException50)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException50);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      Long localLong3 = localJAIRMIImageServer.handleEvent((Long)localObject1, i11, (SerializableState)localObject67, localObject85);
      try
      {
        ObjectOutput localObjectOutput40 = paramRemoteCall.getResultStream(true);
        localObjectOutput40.writeObject(localLong3);
      }
      catch (IOException localIOException92)
      {
        throw new MarshalException("error marshalling return", localIOException92);
      }
    case 42: 
      String str3;
      try
      {
        ObjectInput localObjectInput43 = paramRemoteCall.getInputStream();
        localObject1 = (Long)localObjectInput43.readObject();
        str3 = (String)localObjectInput43.readObject();
        localObject67 = localObjectInput43.readObject();
        localObject85 = localObjectInput43.readObject();
      }
      catch (IOException localIOException111)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException111);
      }
      catch (ClassNotFoundException localClassNotFoundException51)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException51);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      Long localLong4 = localJAIRMIImageServer.handleEvent((Long)localObject1, str3, localObject67, localObject85);
      try
      {
        ObjectOutput localObjectOutput41 = paramRemoteCall.getResultStream(true);
        localObjectOutput41.writeObject(localLong4);
      }
      catch (IOException localIOException93)
      {
        throw new MarshalException("error marshalling return", localIOException93);
      }
    case 43: 
      try
      {
        localObject85 = paramRemoteCall.getInputStream();
        localObject1 = (Long)((ObjectInput)localObject85).readObject();
      }
      catch (IOException localIOException66)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException66);
      }
      catch (ClassNotFoundException localClassNotFoundException26)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException26);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      localJAIRMIImageServer.incrementRefCount((Long)localObject1);
      try
      {
        paramRemoteCall.getResultStream(true);
      }
      catch (IOException localIOException6)
      {
        throw new MarshalException("error marshalling return", localIOException6);
      }
    case 44: 
      try
      {
        ObjectInput localObjectInput22 = paramRemoteCall.getInputStream();
        localObject1 = (Long)localObjectInput22.readObject();
      }
      catch (IOException localIOException67)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException67);
      }
      catch (ClassNotFoundException localClassNotFoundException27)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException27);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      boolean bool2 = localJAIRMIImageServer.isDynamic((Long)localObject1);
      try
      {
        ObjectOutput localObjectOutput27 = paramRemoteCall.getResultStream(true);
        localObjectOutput27.writeBoolean(bool2);
      }
      catch (IOException localIOException31)
      {
        throw new MarshalException("error marshalling return", localIOException31);
      }
    case 45: 
      try
      {
        ObjectInput localObjectInput23 = paramRemoteCall.getInputStream();
        localObject1 = (String)localObjectInput23.readObject();
      }
      catch (IOException localIOException68)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException68);
      }
      catch (ClassNotFoundException localClassNotFoundException28)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException28);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      boolean bool3 = localJAIRMIImageServer.isDynamic((String)localObject1);
      try
      {
        ObjectOutput localObjectOutput28 = paramRemoteCall.getResultStream(true);
        localObjectOutput28.writeBoolean(bool3);
      }
      catch (IOException localIOException32)
      {
        throw new MarshalException("error marshalling return", localIOException32);
      }
    case 46: 
      int i15;
      try
      {
        ObjectInput localObjectInput31 = paramRemoteCall.getInputStream();
        localObject1 = (Long)localObjectInput31.readObject();
        localObject34 = (Rectangle)localObjectInput31.readObject();
        i15 = localObjectInput31.readInt();
      }
      catch (IOException localIOException94)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException94);
      }
      catch (ClassNotFoundException localClassNotFoundException39)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException39);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      localObject87 = localJAIRMIImageServer.mapDestRect((Long)localObject1, (Rectangle)localObject34, i15);
      try
      {
        ObjectOutput localObjectOutput37 = paramRemoteCall.getResultStream(true);
        localObjectOutput37.writeObject(localObject87);
      }
      catch (IOException localIOException85)
      {
        throw new MarshalException("error marshalling return", localIOException85);
      }
    case 47: 
      int i;
      String str4;
      try
      {
        ObjectInput localObjectInput44 = paramRemoteCall.getInputStream();
        i = localObjectInput44.readInt();
        localObject34 = (Long)localObjectInput44.readObject();
        str4 = (String)localObjectInput44.readObject();
        localObject87 = (SerializableState)localObjectInput44.readObject();
      }
      catch (IOException localIOException112)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException112);
      }
      catch (ClassNotFoundException localClassNotFoundException52)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException52);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      SerializableState localSerializableState8 = localJAIRMIImageServer.mapRenderContext(i, (Long)localObject34, str4, (SerializableState)localObject87);
      try
      {
        ObjectOutput localObjectOutput42 = paramRemoteCall.getResultStream(true);
        localObjectOutput42.writeObject(localSerializableState8);
      }
      catch (IOException localIOException95)
      {
        throw new MarshalException("error marshalling return", localIOException95);
      }
    case 48: 
      int i16;
      try
      {
        ObjectInput localObjectInput32 = paramRemoteCall.getInputStream();
        localLong1 = (Long)localObjectInput32.readObject();
        localObject34 = (Rectangle)localObjectInput32.readObject();
        i16 = localObjectInput32.readInt();
      }
      catch (IOException localIOException96)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException96);
      }
      catch (ClassNotFoundException localClassNotFoundException40)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException40);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      localObject89 = localJAIRMIImageServer.mapSourceRect(localLong1, (Rectangle)localObject34, i16);
      try
      {
        ObjectOutput localObjectOutput38 = paramRemoteCall.getResultStream(true);
        localObjectOutput38.writeObject(localObject89);
      }
      catch (IOException localIOException86)
      {
        throw new MarshalException("error marshalling return", localIOException86);
      }
    case 49: 
      String str5;
      int i20;
      try
      {
        ObjectInput localObjectInput46 = paramRemoteCall.getInputStream();
        localLong1 = (Long)localObjectInput46.readObject();
        localObject34 = (Long)localObjectInput46.readObject();
        str5 = (String)localObjectInput46.readObject();
        localObject89 = (String)localObjectInput46.readObject();
        i20 = localObjectInput46.readInt();
      }
      catch (IOException localIOException114)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException114);
      }
      catch (ClassNotFoundException localClassNotFoundException54)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException54);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      localJAIRMIImageServer.setRenderableRMIServerProxyAsSource(localLong1, (Long)localObject34, str5, (String)localObject89, i20);
      try
      {
        paramRemoteCall.getResultStream(true);
      }
      catch (IOException localIOException97)
      {
        throw new MarshalException("error marshalling return", localIOException97);
      }
    case 50: 
      try
      {
        ObjectInput localObjectInput33 = paramRemoteCall.getInputStream();
        localLong1 = (Long)localObjectInput33.readObject();
        localObject34 = (SerializableRenderableImage)localObjectInput33.readObject();
        i17 = localObjectInput33.readInt();
      }
      catch (IOException localIOException98)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException98);
      }
      catch (ClassNotFoundException localClassNotFoundException41)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException41);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      localJAIRMIImageServer.setRenderableSource(localLong1, (SerializableRenderableImage)localObject34, i17);
      try
      {
        paramRemoteCall.getResultStream(true);
      }
      catch (IOException localIOException69)
      {
        throw new MarshalException("error marshalling return", localIOException69);
      }
    case 51: 
      try
      {
        ObjectInput localObjectInput34 = paramRemoteCall.getInputStream();
        localLong1 = (Long)localObjectInput34.readObject();
        localObject34 = (RenderedImage)localObjectInput34.readObject();
        i17 = localObjectInput34.readInt();
      }
      catch (IOException localIOException99)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException99);
      }
      catch (ClassNotFoundException localClassNotFoundException42)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException42);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      localJAIRMIImageServer.setRenderableSource(localLong1, (RenderedImage)localObject34, i17);
      try
      {
        paramRemoteCall.getResultStream(true);
      }
      catch (IOException localIOException70)
      {
        throw new MarshalException("error marshalling return", localIOException70);
      }
    case 52: 
      try
      {
        ObjectInput localObjectInput35 = paramRemoteCall.getInputStream();
        localLong1 = (Long)localObjectInput35.readObject();
        localObject34 = (Long)localObjectInput35.readObject();
        i17 = localObjectInput35.readInt();
      }
      catch (IOException localIOException100)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException100);
      }
      catch (ClassNotFoundException localClassNotFoundException43)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException43);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      localJAIRMIImageServer.setRenderableSource(localLong1, (Long)localObject34, i17);
      try
      {
        paramRemoteCall.getResultStream(true);
      }
      catch (IOException localIOException71)
      {
        throw new MarshalException("error marshalling return", localIOException71);
      }
    case 53: 
      String str6;
      String str8;
      int i21;
      try
      {
        ObjectInput localObjectInput47 = paramRemoteCall.getInputStream();
        localLong1 = (Long)localObjectInput47.readObject();
        localObject34 = (Long)localObjectInput47.readObject();
        str6 = (String)localObjectInput47.readObject();
        str8 = (String)localObjectInput47.readObject();
        i21 = localObjectInput47.readInt();
      }
      catch (IOException localIOException115)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException115);
      }
      catch (ClassNotFoundException localClassNotFoundException55)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException55);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      localJAIRMIImageServer.setRenderableSource(localLong1, (Long)localObject34, str6, str8, i21);
      try
      {
        paramRemoteCall.getResultStream(true);
      }
      catch (IOException localIOException101)
      {
        throw new MarshalException("error marshalling return", localIOException101);
      }
    case 54: 
      try
      {
        ObjectInput localObjectInput36 = paramRemoteCall.getInputStream();
        localLong1 = (Long)localObjectInput36.readObject();
        localObject34 = (RenderableOp)localObjectInput36.readObject();
        i18 = localObjectInput36.readInt();
      }
      catch (IOException localIOException102)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException102);
      }
      catch (ClassNotFoundException localClassNotFoundException44)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException44);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      localJAIRMIImageServer.setRenderableSource(localLong1, (RenderableOp)localObject34, i18);
      try
      {
        paramRemoteCall.getResultStream(true);
      }
      catch (IOException localIOException72)
      {
        throw new MarshalException("error marshalling return", localIOException72);
      }
    case 55: 
      try
      {
        ObjectInput localObjectInput37 = paramRemoteCall.getInputStream();
        localLong1 = (Long)localObjectInput37.readObject();
        localObject34 = (RenderedImage)localObjectInput37.readObject();
        i18 = localObjectInput37.readInt();
      }
      catch (IOException localIOException103)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException103);
      }
      catch (ClassNotFoundException localClassNotFoundException45)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException45);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      localJAIRMIImageServer.setRenderedSource(localLong1, (RenderedImage)localObject34, i18);
      try
      {
        paramRemoteCall.getResultStream(true);
      }
      catch (IOException localIOException73)
      {
        throw new MarshalException("error marshalling return", localIOException73);
      }
    case 56: 
      try
      {
        ObjectInput localObjectInput38 = paramRemoteCall.getInputStream();
        localLong1 = (Long)localObjectInput38.readObject();
        localObject34 = (Long)localObjectInput38.readObject();
        i18 = localObjectInput38.readInt();
      }
      catch (IOException localIOException104)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException104);
      }
      catch (ClassNotFoundException localClassNotFoundException46)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException46);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      localJAIRMIImageServer.setRenderedSource(localLong1, (Long)localObject34, i18);
      try
      {
        paramRemoteCall.getResultStream(true);
      }
      catch (IOException localIOException74)
      {
        throw new MarshalException("error marshalling return", localIOException74);
      }
    case 57: 
      String str7;
      String str9;
      int i22;
      try
      {
        ObjectInput localObjectInput48 = paramRemoteCall.getInputStream();
        localLong1 = (Long)localObjectInput48.readObject();
        localObject34 = (Long)localObjectInput48.readObject();
        str7 = (String)localObjectInput48.readObject();
        str9 = (String)localObjectInput48.readObject();
        i22 = localObjectInput48.readInt();
      }
      catch (IOException localIOException116)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException116);
      }
      catch (ClassNotFoundException localClassNotFoundException56)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException56);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      localJAIRMIImageServer.setRenderedSource(localLong1, (Long)localObject34, str7, str9, i22);
      try
      {
        paramRemoteCall.getResultStream(true);
      }
      catch (IOException localIOException105)
      {
        throw new MarshalException("error marshalling return", localIOException105);
      }
    case 58: 
      int i19;
      try
      {
        ObjectInput localObjectInput39 = paramRemoteCall.getInputStream();
        localLong1 = (Long)localObjectInput39.readObject();
        localObject34 = (RenderedOp)localObjectInput39.readObject();
        i19 = localObjectInput39.readInt();
      }
      catch (IOException localIOException106)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException106);
      }
      catch (ClassNotFoundException localClassNotFoundException47)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException47);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      localJAIRMIImageServer.setRenderedSource(localLong1, (RenderedOp)localObject34, i19);
      try
      {
        paramRemoteCall.getResultStream(true);
      }
      catch (IOException localIOException75)
      {
        throw new MarshalException("error marshalling return", localIOException75);
      }
    case 59: 
      try
      {
        ObjectInput localObjectInput28 = paramRemoteCall.getInputStream();
        localLong1 = (Long)localObjectInput28.readObject();
        localObject34 = (NegotiableCapabilitySet)localObjectInput28.readObject();
      }
      catch (IOException localIOException87)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException87);
      }
      catch (ClassNotFoundException localClassNotFoundException35)
      {
        throw new UnmarshalException("error unmarshalling arguments", localClassNotFoundException35);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      localJAIRMIImageServer.setServerNegotiatedValues(localLong1, (NegotiableCapabilitySet)localObject34);
      try
      {
        paramRemoteCall.getResultStream(true);
      }
      catch (IOException localIOException33)
      {
        throw new MarshalException("error marshalling return", localIOException33);
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
