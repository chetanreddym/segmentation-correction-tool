package com.sun.media.jai.rmi;

import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Arc2D.Double;
import java.awt.geom.Arc2D.Float;
import java.awt.geom.Area;
import java.awt.geom.CubicCurve2D.Double;
import java.awt.geom.CubicCurve2D.Float;
import java.awt.geom.Ellipse2D.Double;
import java.awt.geom.Ellipse2D.Float;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D.Double;
import java.awt.geom.Line2D.Float;
import java.awt.geom.PathIterator;
import java.awt.geom.QuadCurve2D.Double;
import java.awt.geom.QuadCurve2D.Float;
import java.awt.geom.Rectangle2D.Double;
import java.awt.geom.Rectangle2D.Float;
import java.awt.geom.RoundRectangle2D.Double;
import java.awt.geom.RoundRectangle2D.Float;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;











public class ShapeState
  extends SerializableStateImpl
{
  private static final int SHAPE_UNKNOWN = 0;
  private static final int SHAPE_AREA = 1;
  private static final int SHAPE_ARC_DOUBLE = 2;
  private static final int SHAPE_ARC_FLOAT = 3;
  private static final int SHAPE_CUBICCURVE_DOUBLE = 4;
  private static final int SHAPE_CUBICCURVE_FLOAT = 5;
  private static final int SHAPE_ELLIPSE_DOUBLE = 6;
  private static final int SHAPE_ELLIPSE_FLOAT = 7;
  private static final int SHAPE_GENERALPATH = 8;
  private static final int SHAPE_LINE_DOUBLE = 9;
  private static final int SHAPE_LINE_FLOAT = 10;
  private static final int SHAPE_QUADCURVE_DOUBLE = 11;
  private static final int SHAPE_QUADCURVE_FLOAT = 12;
  private static final int SHAPE_ROUNDRECTANGLE_DOUBLE = 13;
  private static final int SHAPE_ROUNDRECTANGLE_FLOAT = 14;
  private static final int SHAPE_RECTANGLE_DOUBLE = 15;
  private static final int SHAPE_RECTANGLE_FLOAT = 16;
  
  public static Class[] getSupportedClasses()
  {
    return new Class[] { Shape.class };
  }
  







  public ShapeState(Class c, Object o, RenderingHints h)
  {
    super(c, o, h);
  }
  



  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    boolean serializable = false;
    Object object = theObject;
    







    if ((object instanceof Serializable)) {
      serializable = true;
    }
    


    out.writeBoolean(serializable);
    if (serializable) {
      out.writeObject(object);
      return;
    }
    
    Object dataArray = null;
    Object otherData = null;
    int type = 0;
    

    if ((theObject instanceof Area)) {
      type = 1;
    } else if ((theObject instanceof Arc2D.Double)) {
      Arc2D.Double ad = (Arc2D.Double)theObject;
      dataArray = new double[] { x, y, width, height, start, extent };
      
      type = 2;
      otherData = new Integer(ad.getArcType());
    } else if ((theObject instanceof Arc2D.Float)) {
      Arc2D.Float af = (Arc2D.Float)theObject;
      dataArray = new float[] { x, y, width, height, start, extent };
      
      type = 3;
      otherData = new Integer(af.getArcType());
    } else if ((theObject instanceof CubicCurve2D.Double)) {
      CubicCurve2D.Double cd = (CubicCurve2D.Double)theObject;
      dataArray = new double[] { x1, y1, ctrlx1, ctrly1, ctrlx2, ctrly2, x2, y2 };
      
      type = 4;
    } else if ((theObject instanceof CubicCurve2D.Float)) {
      CubicCurve2D.Float cf = (CubicCurve2D.Float)theObject;
      dataArray = new float[] { x1, y1, ctrlx1, ctrly1, ctrlx2, ctrly2, x2, y2 };
      
      type = 5;
    } else if ((theObject instanceof Ellipse2D.Double)) {
      Ellipse2D.Double ed = (Ellipse2D.Double)theObject;
      dataArray = new double[] { x, y, width, height };
      type = 6;
    } else if ((theObject instanceof Ellipse2D.Float)) {
      Ellipse2D.Float ef = (Ellipse2D.Float)theObject;
      dataArray = new float[] { x, y, width, height };
      type = 7;
    } else if ((theObject instanceof GeneralPath)) {
      type = 8;
    } else if ((theObject instanceof Line2D.Double)) {
      Line2D.Double ld = (Line2D.Double)theObject;
      dataArray = new double[] { x1, y1, x2, y2 };
      type = 9;
    } else if ((theObject instanceof Line2D.Float)) {
      Line2D.Float lf = (Line2D.Float)theObject;
      dataArray = new float[] { x1, y1, x2, y2 };
      type = 10;
    } else if ((theObject instanceof QuadCurve2D.Double)) {
      QuadCurve2D.Double qd = (QuadCurve2D.Double)theObject;
      dataArray = new double[] { x1, y1, ctrlx, ctrly, x2, y2 };
      type = 11;
    } else if ((theObject instanceof QuadCurve2D.Float)) {
      QuadCurve2D.Float qf = (QuadCurve2D.Float)theObject;
      dataArray = new float[] { x1, y1, ctrlx, ctrly, x2, y2 };
      type = 12;
    } else if ((theObject instanceof RoundRectangle2D.Double)) {
      RoundRectangle2D.Double rrd = (RoundRectangle2D.Double)theObject;
      dataArray = new double[] { x, y, width, height, arcwidth, archeight };
      
      type = 13;
    } else if ((theObject instanceof RoundRectangle2D.Float)) {
      RoundRectangle2D.Float rrf = (RoundRectangle2D.Float)theObject;
      dataArray = new float[] { x, y, width, height, arcwidth, archeight };
      
      type = 14;
    } else if ((theObject instanceof Rectangle2D.Double)) {
      Rectangle2D.Double rd = (Rectangle2D.Double)theObject;
      dataArray = new double[] { x, y, width, height };
      type = 15;
    } else if ((theObject instanceof Rectangle2D.Float)) {
      Rectangle2D.Float rf = (Rectangle2D.Float)theObject;
      dataArray = new float[] { x, y, width, height };
      type = 16;
    }
    
    out.writeInt(type);
    if (dataArray != null) {
      out.writeObject(dataArray);
      if (otherData != null)
        out.writeObject(otherData);
      return;
    }
    
    PathIterator pathIterator = ((Shape)theObject).getPathIterator(null);
    


    int rule = pathIterator.getWindingRule();
    out.writeInt(rule);
    
    float[] coordinates = new float[6];
    

    boolean isDone = pathIterator.isDone();
    while (!isDone) {
      int segmentType = pathIterator.currentSegment(coordinates);
      out.writeBoolean(isDone);
      out.writeInt(segmentType);
      for (int i = 0; i < 6; i++)
        out.writeFloat(coordinates[i]);
      pathIterator.next();
      isDone = pathIterator.isDone();
    }
    out.writeBoolean(isDone);
  }
  





  private void readObject(ObjectInputStream in)
    throws IOException, ClassNotFoundException
  {
    boolean serializable = in.readBoolean();
    

    if (serializable) {
      theObject = in.readObject();
      return;
    }
    

    int type = in.readInt();
    

    switch (type)
    {
    case 2: 
      double[] data = (double[])in.readObject();
      int arcType = ((Integer)in.readObject()).intValue();
      theObject = new Arc2D.Double(data[0], data[1], data[2], data[3], data[4], data[5], arcType);
      
      return;
    

    case 3: 
      float[] data = (float[])in.readObject();
      int arcType = ((Integer)in.readObject()).intValue();
      theObject = new Arc2D.Float(data[0], data[1], data[2], data[3], data[4], data[5], arcType);
      
      return;
    

    case 4: 
      double[] data = (double[])in.readObject();
      theObject = new CubicCurve2D.Double(data[0], data[1], data[2], data[3], data[4], data[5], data[6], data[7]);
      

      return;
    

    case 5: 
      float[] data = (float[])in.readObject();
      theObject = new CubicCurve2D.Float(data[0], data[1], data[2], data[3], data[4], data[5], data[6], data[7]);
      

      return;
    

    case 6: 
      double[] data = (double[])in.readObject();
      theObject = new Ellipse2D.Double(data[0], data[1], data[2], data[3]);
      return;
    

    case 7: 
      float[] data = (float[])in.readObject();
      theObject = new Ellipse2D.Float(data[0], data[1], data[2], data[3]);
      return;
    


    case 9: 
      double[] data = (double[])in.readObject();
      theObject = new Line2D.Double(data[0], data[1], data[2], data[3]);
      return;
    

    case 10: 
      float[] data = (float[])in.readObject();
      theObject = new Line2D.Float(data[0], data[1], data[2], data[3]);
      return;
    

    case 11: 
      double[] data = (double[])in.readObject();
      theObject = new QuadCurve2D.Double(data[0], data[1], data[2], data[3], data[4], data[5]);
      
      return;
    

    case 12: 
      float[] data = (float[])in.readObject();
      theObject = new QuadCurve2D.Float(data[0], data[1], data[2], data[3], data[4], data[5]);
      

      return;
    

    case 13: 
      double[] data = (double[])in.readObject();
      theObject = new RoundRectangle2D.Double(data[0], data[1], data[2], data[3], data[4], data[5]);
      
      return;
    

    case 14: 
      float[] data = (float[])in.readObject();
      theObject = new RoundRectangle2D.Float(data[0], data[1], data[2], data[3], data[4], data[5]);
      
      return;
    

    case 15: 
      double[] data = (double[])in.readObject();
      theObject = new Rectangle2D.Double(data[0], data[1], data[2], data[3]);
      
      return;
    

    case 16: 
      float[] data = (float[])in.readObject();
      theObject = new Rectangle2D.Float(data[0], data[1], data[2], data[3]);
      
      return;
    }
    
    

    int rule = in.readInt();
    
    GeneralPath path = new GeneralPath(rule);
    float[] coordinates = new float[6];
    

    while (!in.readBoolean()) {
      int segmentType = in.readInt();
      for (int i = 0; i < 6; i++) {
        coordinates[i] = in.readFloat();
      }
      switch (segmentType) {
      case 0: 
        path.moveTo(coordinates[0], coordinates[1]);
        break;
      
      case 1: 
        path.lineTo(coordinates[0], coordinates[1]);
        break;
      
      case 2: 
        path.quadTo(coordinates[0], coordinates[1], coordinates[2], coordinates[3]);
        
        break;
      
      case 3: 
        path.curveTo(coordinates[0], coordinates[1], coordinates[2], coordinates[3], coordinates[4], coordinates[5]);
        

        break;
      
      case 4: 
        path.closePath();
      }
      
    }
    



    switch (type) {
    case 1: 
      theObject = new Area(path);
      break;
    default: 
      theObject = path;
    }
  }
}
