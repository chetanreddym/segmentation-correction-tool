package javax.media.jai;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.awt.geom.Rectangle2D.Float;
import java.awt.image.BufferedImage;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.SampleModel;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Vector;





























public class ROIShape
  extends ROI
{
  transient Shape theShape = null;
  

















  private static Point2D.Double getIntersection(double x1, double y1, double x2, double y2, double u1, double v1, double u2, double v2)
  {
    double[][] a = new double[2][2];
    a[0][0] = (y2 - y1);
    a[0][1] = (x1 - x2);
    a[1][0] = (v2 - v1);
    a[1][1] = (u1 - u2);
    
    double[] c = new double[2];
    c[0] = (y1 * (x1 - x2) + x1 * (y2 - y1));
    c[1] = (v1 * (u1 - u2) + u1 * (v2 - v1));
    
    double det = a[0][0] * a[1][1] - a[0][1] * a[1][0];
    double tmp = a[0][0];
    a[0][0] = (a[1][1] / det);
    a[0][1] = (-a[0][1] / det);
    a[1][0] = (-a[1][0] / det);
    a[1][1] = (tmp / det);
    
    double x = a[0][0] * c[0] + a[0][1] * c[1];
    double y = a[1][0] * c[0] + a[1][1] * c[1];
    
    return new Point2D.Double(x, y);
  }
  










  private LinkedList polygonToRunLengthList(Rectangle clip, Polygon poly)
  {
    PolyShape ps = new PolyShape(poly, clip);
    return ps.getAsRectList();
  }
  













  private static int[][] rectangleListToBitmask(LinkedList rectangleList, Rectangle clip, int[][] mask)
  {
    int bitField = Integer.MIN_VALUE;
    

    int bitmaskIntWidth = (width + 31) / 32;
    

    if (mask == null) {
      mask = new int[height][bitmaskIntWidth];
    } else if ((mask.length < height) || (mask[0].length < bitmaskIntWidth))
    {
      throw new RuntimeException(JaiI18N.getString("ROIShape0"));
    }
    

    ListIterator rectangleIter = rectangleList.listIterator(0);
    while (rectangleIter.hasNext())
    {
      Rectangle rect;
      if (clip.intersects(rect = (Rectangle)rectangleIter.next())) {
        rect = clip.intersection(rect);
        

        int yMin = y - y;
        int xMin = x - x;
        int yMax = yMin + height - 1;
        int xMax = xMin + width - 1;
        

        for (int y = yMin; y <= yMax; y++) {
          int[] bitrow = mask[y];
          for (int x = xMin; x <= xMax; x++) {
            int index = x / 32;
            int shift = x % 32;
            bitrow[index] |= bitField >>> shift;
          }
        }
      }
    }
    
    return mask;
  }
  






  public ROIShape(Shape s)
  {
    if (s == null) {
      throw new IllegalArgumentException(JaiI18N.getString("ROIShape2"));
    }
    theShape = s;
  }
  




  public ROIShape(Area a)
  {
    AffineTransform at = new AffineTransform();
    PathIterator pi = a.getPathIterator(at);
    GeneralPath gp = new GeneralPath(pi.getWindingRule());
    gp.append(pi, false);
    
    theShape = gp;
  }
  



  private class PolyShape
  {
    private static final int POLYGON_UNCLASSIFIED = 0;
    


    private static final int POLYGON_DEGENERATE = 1;
    


    private static final int POLYGON_CONVEX = 2;
    


    private static final int POLYGON_CONCAVE = 3;
    

    private Polygon poly;
    

    private Rectangle clip;
    

    private int type = 0;
    



    private boolean insidePolygon = false;
    







    PolyShape(Polygon polygon, Rectangle clipRect)
    {
      poly = polygon;
      clip = clipRect;
      

      insidePolygon = poly.contains(clipRect);
      type = 0;
    }
    



    private class PolyEdge
      implements Comparator
    {
      public double x;
      


      public double dx;
      


      public int i;
      



      PolyEdge(double x, double dx, int i)
      {
        this.x = x;
        this.dx = dx;
        this.i = i;
      }
      









      public int compare(Object o1, Object o2)
      {
        double x1 = x;
        double x2 = x;
        int returnValue;
        int returnValue;
        if (x1 < x2) {
          returnValue = -1; } else { int returnValue;
          if (x1 > x2) {
            returnValue = 1;
          } else {
            returnValue = 0;
          }
        }
        return returnValue;
      }
    }
    






    public LinkedList getAsRectList()
    {
      LinkedList rectList = new LinkedList();
      
      if (insidePolygon) {
        rectList.addLast(poly.getBounds());
      }
      else {
        classifyPolygon();
        

        switch (type) {
        case 1: 
          rectList = null;
          break;
        case 2: 
          rectList = scanConvex(rectList);
          break;
        case 3: 
          rectList = scanConcave(rectList);
          break;
        default: 
          throw new RuntimeException(JaiI18N.getString("ROIShape1"));
        }
        
      }
      return rectList;
    }
    



    private int classifyPolygon()
    {
      if (type != 0) {
        return type;
      }
      
      int n = poly.npoints;
      if (n < 3) {
        type = 1;
        return type;
      }
      if (poly.getBounds().contains(clip)) {
        type = 2;
        return type;
      }
      

      int[] x = poly.xpoints;
      int[] y = poly.ypoints;
      


      int previousSign = sgn((x[0] - x[1]) * (y[1] - y[2]) - (x[1] - x[2]) * (y[0] - y[1]));
      
      boolean allZero = previousSign == 0;
      
      int previousDirection;
      int previousDirection;
      if (x[0] < x[1]) {
        previousDirection = -1; } else { int previousDirection;
        if (x[0] > x[1]) {
          previousDirection = 1; } else { int previousDirection;
          if (y[0] < y[1]) {
            previousDirection = -1; } else { int previousDirection;
            if (y[0] > y[1]) {
              previousDirection = 1;
            } else {
              previousDirection = 0;
            }
          }
        }
      }
      


      int numDirectionChanges = 0;
      for (int i = 1; i < n; i++)
      {
        int j = (i + 1) % n;
        int k = (i + 2) % n;
        
        int currentDirection;
        int currentDirection;
        if (x[i] < x[j]) {
          currentDirection = -1; } else { int currentDirection;
          if (x[i] > x[j]) {
            currentDirection = 1; } else { int currentDirection;
            if (y[i] < y[j]) {
              currentDirection = -1; } else { int currentDirection;
              if (y[i] > y[j]) {
                currentDirection = 1;
              } else
                currentDirection = 0;
            }
          }
        }
        if ((currentDirection != 0) && (currentDirection == -previousDirection))
        {
          numDirectionChanges++;
        }
        previousDirection = currentDirection;
        


        int sign = sgn((x[i] - x[j]) * (y[j] - y[k]) - (x[j] - x[k]) * (y[i] - y[j]));
        
        allZero = (allZero) && (sign == 0);
        if (!allZero) {
          if ((sign != 0) && (sign == -previousSign)) {
            type = 3;
            break; }
          if (sign != 0) {
            previousSign = sign;
          }
        }
      }
      
      if (type == 0) {
        if (allZero) {
          type = 1;
        } else if (numDirectionChanges > 2) {
          type = 3;
        } else {
          type = 2;
        }
      }
      
      return type;
    }
    


    private final int sgn(int i)
    {
      int sign;
      

      int sign;
      
      if (i > 0) {
        sign = 1; } else { int sign;
        if (i < 0) {
          sign = -1;
        } else
          sign = 0;
      }
      return sign;
    }
    








    private LinkedList scanConvex(LinkedList rectList)
    {
      if (rectList == null) {
        rectList = new LinkedList();
      }
      

      int yMin = poly.ypoints[0];
      int topVertex = 0;
      int n = poly.npoints;
      for (int i = 1; i < n; i++) {
        if (poly.ypoints[i] < yMin) {
          yMin = poly.ypoints[i];
          topVertex = i;
        }
      }
      

      int leftIndex = topVertex;
      int rightIndex = topVertex;
      

      int numRemaining = n;
      

      int y = yMin;
      

      int intYLeft = y - 1;
      int intYRight = intYLeft;
      

      double[] px = intArrayToDoubleArray(poly.xpoints);
      int[] py = poly.ypoints;
      
      double[] leftX = new double[1];
      double[] leftDX = new double[1];
      double[] rightX = new double[1];
      double[] rightDX = new double[1];
      for (; 
          

          numRemaining > 0; 
          






































          goto 298)
      {
        while ((intYLeft <= y) && (numRemaining > 0)) {
          numRemaining--;
          int i = leftIndex - 1;
          if (i < 0) i = n - 1;
          intersectX(px[leftIndex], py[leftIndex], px[i], py[i], y, leftX, leftDX);
          
          intYLeft = py[i];
          leftIndex = i;
        }
        

        while ((intYRight <= y) && (numRemaining > 0)) {
          numRemaining--;
          int i = rightIndex + 1;
          if (i >= n) i = 0;
          intersectX(px[rightIndex], py[rightIndex], px[i], py[i], y, rightX, rightDX);
          
          intYRight = py[i];
          rightIndex = i;
        }
        

        if ((y < intYLeft) && (y < intYRight)) {
          if ((y >= clip.y) && (y < clip.getMaxY())) { Rectangle rect;
            Rectangle rect;
            if (leftX[0] <= rightX[0]) {
              rect = scanSegment(y, leftX[0], rightX[0]);
            } else {
              rect = scanSegment(y, rightX[0], leftX[0]);
            }
            if (rect != null) {
              rectList.addLast(rect);
            }
          }
          y++;
          leftX[0] += leftDX[0];
          rightX[0] += rightDX[0];
        }
      }
      
      return rectList;
    }
    









    private Rectangle scanSegment(int y, double leftX, double rightX)
    {
      double x = leftX - 0.5D;
      int xl = x < clip.x ? clip.x : (int)Math.ceil(x);
      int xr = (int)Math.floor(rightX - 0.5D);
      if (xr >= clip.x + clip.width) xr = clip.x + clip.width - 1;
      if (xl > xr) { return null;
      }
      return new Rectangle(xl, y, xr - xl + 1, 1);
    }
    













    private void intersectX(double x1, int y1, double x2, int y2, int y, double[] x, double[] dx)
    {
      int dy = y2 - y1;
      if (dy == 0) dy = 1;
      double frac = y - y1 + 0.5D;
      
      dx[0] = ((x2 - x1) / dy);
      x[0] = (x1 + dx[0] * frac);
    }
    








    private LinkedList scanConcave(LinkedList rectList)
    {
      if (rectList == null) {
        rectList = new LinkedList();
      }
      
      int numVertices = poly.npoints;
      if (numVertices <= 0) { return null;
      }
      
      Vector indVector = new Vector();
      indVector.add(new Integer(0));
      for (int count = 1; count < numVertices; count++)
      {

        int index = 0;
        int value = poly.ypoints[count];
        while (index < count) {
          int elt = ((Integer)indVector.get(index)).intValue();
          if (value <= poly.ypoints[elt]) break;
          index++;
        }
        indVector.insertElementAt(new Integer(count), index);
      }
      


      int[] ind = vectorToIntArray(indVector);
      

      Vector activeEdges = new Vector(numVertices);
      

      int y0 = Math.max((int)clip.getMinY(), (int)Math.ceil(poly.ypoints[ind[0]] - 0.5F));
      
      int y1 = Math.min((int)clip.getMaxY(), (int)Math.floor(poly.ypoints[ind[(numVertices - 1)]] - 0.5F));
      




      int nextVertex = 0;
      for (int y = y0; y <= y1; y++)
      {

        while ((nextVertex < numVertices) && (poly.ypoints[ind[nextVertex]] <= y + 0.5F)) {
          int i = ind[nextVertex];
          


          int j = i > 0 ? i - 1 : numVertices - 1;
          if (poly.ypoints[j] <= y - 0.5F) {
            deleteEdge(activeEdges, j);
          } else if (poly.ypoints[j] > y + 0.5F) {
            appendEdge(activeEdges, j, y);
          }
          
          j = i < numVertices - 1 ? i + 1 : 0;
          if (poly.ypoints[j] <= y - 0.5F) {
            deleteEdge(activeEdges, i);
          } else if (poly.ypoints[j] > y + 0.5F) {
            appendEdge(activeEdges, i, y);
          }
          
          nextVertex++;
        }
        

        Object[] edges = activeEdges.toArray();
        Arrays.sort(edges, (PolyEdge)edges[0]);
        

        int numActive = activeEdges.size();
        for (int k = 0; k < numActive; k += 2)
        {
          PolyEdge edge1 = (PolyEdge)edges[k];
          PolyEdge edge2 = (PolyEdge)edges[(k + 1)];
          

          int xl = (int)Math.ceil(x - 0.5D);
          if (xl < clip.getMinX()) {
            xl = (int)clip.getMinX();
          }
          

          int xr = (int)Math.floor(x - 0.5D);
          if (xr > clip.getMaxX()) {
            xr = (int)clip.getMaxX();
          }
          

          if (xl <= xr) {
            Rectangle r = new Rectangle(xl, y, xr - xl + 1, 1);
            rectList.addLast(r);
          }
          

          x += dx;
          activeEdges.setElementAt(edge1, k);
          x += dx;
          activeEdges.setElementAt(edge2, k + 1);
        }
      }
      
      return rectList;
    }
    





    private void deleteEdge(Vector edges, int i)
    {
      int numActive = edges.size();
      
      for (int j = 0; j < numActive; j++) {
        PolyEdge edge = (PolyEdge)edges.get(j);
        if (i == i) break;
      }
      if (j < numActive) {
        edges.removeElementAt(j);
      }
    }
    






    private void appendEdge(Vector edges, int i, int y)
    {
      int j = (i + 1) % poly.npoints;
      int iq;
      int ip;
      int iq; if (poly.ypoints[i] < poly.ypoints[j]) {
        int ip = i;
        iq = j;
      } else {
        ip = j;
        iq = i;
      }
      double dx = (poly.xpoints[iq] - poly.xpoints[ip]) / (poly.ypoints[iq] - poly.ypoints[ip]);
      

      double x = dx * (y + 0.5F - poly.ypoints[ip]) + poly.xpoints[ip];
      edges.add(new PolyEdge(x, dx, i));
    }
    



    private double[] intArrayToDoubleArray(int[] intArray)
    {
      int length = intArray.length;
      double[] doubleArray = new double[length];
      for (int i = 0; i < length; i++) {
        doubleArray[i] = intArray[i];
      }
      return doubleArray;
    }
    







    private int[] vectorToIntArray(Vector vector)
    {
      int size = vector.size();
      int[] array = new int[size];
      Object[] objects = vector.toArray();
      for (int i = 0; i < size; i++) {
        array[i] = ((Integer)objects[i]).intValue();
      }
      return array;
    }
  }
  
  public Rectangle getBounds()
  {
    return theShape.getBounds();
  }
  
  public Rectangle2D getBounds2D()
  {
    return theShape.getBounds2D();
  }
  







  public boolean contains(Point p)
  {
    if (p == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    return contains(x, y);
  }
  







  public boolean contains(Point2D p)
  {
    if (p == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    return contains((int)p.getX(), (int)p.getY());
  }
  






  public boolean contains(int x, int y)
  {
    return theShape.contains(x, y);
  }
  








  public boolean contains(double x, double y)
  {
    return contains((int)x, (int)y);
  }
  









  public boolean contains(Rectangle rect)
  {
    if (rect == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    return contains(new Rectangle2D.Float(x, y, width, height));
  }
  












  public boolean contains(Rectangle2D rect)
  {
    if (rect == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    return theShape.contains(rect);
  }
  










  public boolean contains(int x, int y, int w, int h)
  {
    return contains(new Rectangle2D.Float(x, y, w, h));
  }
  













  public boolean contains(double x, double y, double w, double h)
  {
    return theShape.contains(x, y, w, h);
  }
  








  public boolean intersects(Rectangle r)
  {
    if (r == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    return intersects(new Rectangle2D.Float(x, y, width, height));
  }
  











  public boolean intersects(Rectangle2D r)
  {
    if (r == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    return theShape.intersects(r);
  }
  









  public boolean intersects(int x, int y, int w, int h)
  {
    return intersects(new Rectangle2D.Float(x, y, w, h));
  }
  












  public boolean intersects(double x, double y, double w, double h)
  {
    return theShape.intersects(x, y, w, h);
  }
  







  public ROI add(ROI roi)
  {
    if (roi == null) {
      throw new IllegalArgumentException(JaiI18N.getString("ROIShape3"));
    }
    
    if (!(roi instanceof ROIShape)) {
      return super.add(roi);
    }
    ROIShape rois = (ROIShape)roi;
    Area a1 = new Area(theShape);
    Area a2 = new Area(theShape);
    a1.add(a2);
    return new ROIShape(a1);
  }
  








  public ROI subtract(ROI roi)
  {
    if (roi == null) {
      throw new IllegalArgumentException(JaiI18N.getString("ROIShape3"));
    }
    
    if (!(roi instanceof ROIShape)) {
      return super.subtract(roi);
    }
    ROIShape rois = (ROIShape)roi;
    Area a1 = new Area(theShape);
    Area a2 = new Area(theShape);
    a1.subtract(a2);
    return new ROIShape(a1);
  }
  








  public ROI intersect(ROI roi)
  {
    if (roi == null) {
      throw new IllegalArgumentException(JaiI18N.getString("ROIShape3"));
    }
    
    if (!(roi instanceof ROIShape)) {
      return super.intersect(roi);
    }
    ROIShape rois = (ROIShape)roi;
    Area a1 = new Area(theShape);
    Area a2 = new Area(theShape);
    a1.intersect(a2);
    return new ROIShape(a1);
  }
  








  public ROI exclusiveOr(ROI roi)
  {
    if (roi == null) {
      throw new IllegalArgumentException(JaiI18N.getString("ROIShape3"));
    }
    
    if (!(roi instanceof ROIShape)) {
      return super.exclusiveOr(roi);
    }
    ROIShape rois = (ROIShape)roi;
    Area a1 = new Area(theShape);
    Area a2 = new Area(theShape);
    a1.exclusiveOr(a2);
    return new ROIShape(a1);
  }
  




  public Shape getAsShape()
  {
    return theShape;
  }
  











  public PlanarImage getAsImage()
  {
    if (theImage != null) {
      return theImage;
    }
    Rectangle r = theShape.getBounds();
    
    Graphics2D g2d;
    PlanarImage pi;
    Graphics2D g2d;
    if ((x == 0) && (y == 0))
    {
      BufferedImage bi = new BufferedImage(width, height, 12);
      


      PlanarImage pi = PlanarImage.wrapRenderedImage(bi);
      g2d = bi.createGraphics();
    }
    else {
      SampleModel sm = new MultiPixelPackedSampleModel(0, width, height, 1);
      



      TiledImage ti = new TiledImage(x, y, width, height, x, y, sm, PlanarImage.createColorModel(sm));
      



      pi = ti;
      g2d = ti.createGraphics();
    }
    

    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
    g2d.fill(theShape);
    
    theImage = pi;
    
    return theImage;
  }
  






  public ROI transform(AffineTransform at)
  {
    if (at == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    return new ROIShape(at.createTransformedShape(theShape));
  }
  




























  public int[][] getAsBitmask(int x, int y, int width, int height, int[][] mask)
  {
    LinkedList rectList = getAsRectangleList(x, y, width, height, false);
    
    if (rectList == null) {
      return (int[][])null;
    }
    

    return rectangleListToBitmask(rectList, new Rectangle(x, y, width, height), mask);
  }
  














  public LinkedList getAsRectangleList(int x, int y, int width, int height)
  {
    return getAsRectangleList(x, y, width, height, true);
  }
  













  protected LinkedList getAsRectangleList(int x, int y, int width, int height, boolean mergeRectangles)
  {
    LinkedList rectangleList = null;
    

    Rectangle clip = new Rectangle(x, y, width, height);
    




    if (!new Area(theShape).intersects(clip))
      return null;
    if ((theShape instanceof Rectangle2D))
    {


      Rectangle2D.Double dstRect = new Rectangle2D.Double();
      Rectangle2D.intersect((Rectangle2D)theShape, clip, dstRect);
      int rectX = (int)Math.round(dstRect.getMinX());
      int rectY = (int)Math.round(dstRect.getMinY());
      int rectW = (int)Math.round(dstRect.getMaxX() - rectX);
      int rectH = (int)Math.round(dstRect.getMaxY() - rectY);
      rectangleList = new LinkedList();
      rectangleList.addLast(new Rectangle(rectX, rectY, rectW, rectH));
    }
    else if ((theShape instanceof Polygon)) {
      rectangleList = polygonToRunLengthList(clip, (Polygon)theShape);
      if ((mergeRectangles) && (rectangleList != null)) {
        rectangleList = mergeRunLengthList(rectangleList);
      }
    }
    else {
      getAsImage();
      

      rectangleList = super.getAsRectangleList(x, y, width, height, mergeRectangles);
    }
    

    return rectangleList;
  }
  




  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    LinkedList rectList = null;
    if (theShape == null) {
      rectList = new LinkedList();
    } else {
      Rectangle r = getBounds();
      rectList = getAsRectangleList(x, y, width, height);
    }
    

    out.defaultWriteObject();
    out.writeObject(rectList);
  }
  






  private void readObject(ObjectInputStream in)
    throws IOException, ClassNotFoundException
  {
    LinkedList rectList = null;
    in.defaultReadObject();
    rectList = (LinkedList)in.readObject();
    

    Area a = new Area();
    int listSize = rectList.size();
    for (int i = 0; i < listSize; i++) {
      a.add(new Area((Rectangle)rectList.get(i)));
    }
    theShape = a;
  }
}
