package com.sun.media.jai.rmi;

import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.renderable.RenderContext;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.media.jai.ROIShape;
























public class RenderContextProxy
  implements Serializable
{
  private transient RenderContext renderContext;
  
  public RenderContextProxy(RenderContext source)
  {
    renderContext = source;
  }
  



  public RenderContext getRenderContext()
  {
    return renderContext;
  }
  



  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    boolean isNull = renderContext == null;
    out.writeBoolean(isNull);
    if (isNull) {
      return;
    }
    
    AffineTransform usr2dev = renderContext.getTransform();
    

    RenderingHintsProxy rhp = new RenderingHintsProxy(renderContext.getRenderingHints());
    

    Shape aoi = renderContext.getAreaOfInterest();
    

    out.writeObject(usr2dev);
    
    out.writeBoolean(aoi != null);
    if (aoi != null) {
      if ((aoi instanceof Serializable)) {
        out.writeObject(aoi);
      } else {
        out.writeObject(new ROIShape(aoi));
      }
    }
    
    out.writeObject(rhp);
  }
  





  private void readObject(ObjectInputStream in)
    throws IOException, ClassNotFoundException
  {
    if (in.readBoolean()) {
      renderContext = null;
      return;
    }
    

    AffineTransform usr2dev = (AffineTransform)in.readObject();
    
    Shape shape = null;
    Object aoi = in.readBoolean() ? in.readObject() : null;
    
    RenderingHintsProxy rhp = (RenderingHintsProxy)in.readObject();
    
    RenderingHints hints = rhp.getRenderingHints();
    

    if (aoi != null) {
      if ((aoi instanceof ROIShape)) {
        shape = ((ROIShape)aoi).getAsShape();
      } else {
        shape = (Shape)aoi;
      }
    }
    
    if ((aoi == null) && (hints.isEmpty())) {
      renderContext = new RenderContext(usr2dev);
    } else if (aoi == null) {
      renderContext = new RenderContext(usr2dev, hints);
    } else if (hints.isEmpty()) {
      renderContext = new RenderContext(usr2dev, shape);
    } else {
      renderContext = new RenderContext(usr2dev, shape, hints);
    }
  }
}
