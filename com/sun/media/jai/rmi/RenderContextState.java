package com.sun.media.jai.rmi;

import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.renderable.RenderContext;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.media.jai.remote.SerializableState;
import javax.media.jai.remote.SerializerFactory;



















public class RenderContextState
  extends SerializableStateImpl
{
  public static Class[] getSupportedClasses()
  {
    return new Class[] { RenderContext.class };
  }
  







  public RenderContextState(Class c, Object o, RenderingHints h)
  {
    super(c, o, h);
  }
  




  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    RenderContext renderContext = (RenderContext)theObject;
    

    AffineTransform usr2dev = renderContext.getTransform();
    

    RenderingHints hints = renderContext.getRenderingHints();
    

    Shape aoi = renderContext.getAreaOfInterest();
    

    out.writeObject(usr2dev);
    out.writeObject(SerializerFactory.getState(aoi));
    out.writeObject(SerializerFactory.getState(hints, null));
  }
  





  private void readObject(ObjectInputStream in)
    throws IOException, ClassNotFoundException
  {
    RenderContext renderContext = null;
    

    AffineTransform usr2dev = (AffineTransform)in.readObject();
    
    SerializableState aoi = (SerializableState)in.readObject();
    Shape shape = (Shape)aoi.getObject();
    
    SerializableState rhs = (SerializableState)in.readObject();
    RenderingHints hints = (RenderingHints)rhs.getObject();
    

    renderContext = new RenderContext(usr2dev, shape, hints);
    theObject = renderContext;
  }
}
