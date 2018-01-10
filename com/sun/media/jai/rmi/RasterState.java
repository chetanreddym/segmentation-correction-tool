package com.sun.media.jai.rmi;

import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.media.jai.remote.SerializableState;
import javax.media.jai.remote.SerializerFactory;




















public class RasterState
  extends SerializableStateImpl
{
  public static Class[] getSupportedClasses()
  {
    return new Class[] { Raster.class, WritableRaster.class };
  }
  


  public static boolean permitsSubclasses()
  {
    return true;
  }
  







  public RasterState(Class c, Object o, RenderingHints h)
  {
    super(c, o, h);
  }
  



  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    Raster raster = (Raster)theObject;
    
    Raster r;
    if (raster.getParent() != null)
    {



      Raster r = raster.createCompatibleWritableRaster(raster.getBounds());
      ((WritableRaster)r).setRect(raster);
    } else {
      r = raster;
    }
    
    out.writeInt(r.getWidth());
    out.writeInt(r.getHeight());
    out.writeObject(SerializerFactory.getState(r.getSampleModel(), null));
    out.writeObject(SerializerFactory.getState(r.getDataBuffer(), null));
    out.writeObject(new Point(r.getMinX(), r.getMinY()));
  }
  






  private void readObject(ObjectInputStream in)
    throws IOException, ClassNotFoundException
  {
    SerializableState sampleModelState = null;
    SerializableState dataBufferState = null;
    Point location = null;
    
    int width = in.readInt();
    int height = in.readInt();
    sampleModelState = (SerializableState)in.readObject();
    dataBufferState = (SerializableState)in.readObject();
    location = (Point)in.readObject();
    

    SampleModel sampleModel = (SampleModel)sampleModelState.getObject();
    if (sampleModel == null) {
      theObject = null;
      return;
    }
    

    DataBuffer dataBuffer = (DataBuffer)dataBufferState.getObject();
    

    theObject = Raster.createRaster(sampleModel, dataBuffer, location);
  }
}
