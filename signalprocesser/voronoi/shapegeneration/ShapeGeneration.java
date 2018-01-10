package signalprocesser.voronoi.shapegeneration;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.util.ArrayList;
import java.util.List;
import signalprocesser.voronoi.VPoint;

public class ShapeGeneration
{
  private static final int POINTSLEFT_BEFORE_CUTOFF = 100;
  private static final java.awt.font.FontRenderContext FONT_RENDER = new java.awt.font.FontRenderContext(null, true, true);
  
  public ShapeGeneration() {}
  
  public static Shape createShape(Area area)
  {
    return new PathIteratorWrapper(area.getPathIterator(null));
  }
  
  public static Shape createShape(PathIterator pathiter) { return new PathIteratorWrapper(pathiter); }
  
  public static Shape createShape(ArrayList<VPoint> points) {
    return new PathIteratorWrapper(new ListPathIterator(points));
  }
  
  public static Area createArea(ArrayList<VPoint> points) {
    return new Area(new PathIteratorWrapper(new ListPathIterator(points)));
  }
  






  public static ArrayList<VPoint> addRandomPoints(ArrayList<VPoint> points_original, boolean splitlonglines, int shapepoints, int shapepoint_mindensity, int internalpoints, int internal_mindensity)
    throws ShapeGenerationException
  {
    ArrayList<VPoint> points = new ArrayList();
    points.addAll(points_original);
    

    ArrayList<VPoint> randompoints = new ArrayList();
    

    if (points.size() <= 2)
    {
      randompoints.addAll(points);
      

      return randompoints;
    }
    

    VPoint first = (VPoint)points.get(0);
    int min_x = x;int max_x = x;
    int min_y = y;int max_y = y;
    for (VPoint point : points)
    {
      if (x < min_x) {
        min_x = x;
      } else if (x > max_x) {
        max_x = x;
      }
      

      if (y < min_y) {
        min_y = y;
      } else if (y > max_y) {
        max_y = y;
      }
    }
    

    int width = max_x - min_x + 1;
    int height = max_y - min_y + 1;
    

    if (width <= 0)
      throw new ShapeGenerationException("Width of shape is zero - cannot add random points");
    if (height <= 0) {
      throw new ShapeGenerationException("Height of shape is zero - cannot add random points");
    }
    

    int x = 0;int y = 0;
    boolean[][] array = new boolean[width][height];
    for (x = 0; x < width; x++) {
      for (y = 0; y < height; y++) {
        array[x][y] = 1;
      }
    }
    boolean[][] internal_circle;
    boolean[][] shapepoint_circle;
    boolean[][] internal_circle;
    if (shapepoint_mindensity == internal_mindensity) {
      boolean[][] shapepoint_circle = createCircle(shapepoint_mindensity);
      internal_circle = shapepoint_circle;
    } else {
      shapepoint_circle = createCircle(shapepoint_mindensity);
      internal_circle = createCircle(internal_mindensity);
    }
    


    while (points.size() > shapepoints)
    {
      int index = (int)(Math.random() * (points.size() - 1));
      

      points.remove(index);
    }
    




    VPoint currpoint = null;
    for (int index = 0; index < points.size(); index++)
    {
      currpoint = (VPoint)points.get(index);
      

      x = x - min_x;
      y = y - min_y;
      

      if (array[x][y] == 0)
      {
        points.remove(index);
        index--;


      }
      else
      {

        randompoints.add(currpoint);
        

        unsetCircle(-1, x, y, 
          array, width, height, shapepoint_circle, shapepoint_mindensity);
      }
    }
    VPoint firstpoint;
    if ((splitlonglines) && (points.size() >= 2)) {
      currpoint = (VPoint)points.get(0);
      
      for (index = 1; index < points.size(); index++)
      {
        VPoint prevpoint = currpoint;
        currpoint = (VPoint)points.get(index);
        
        if (prevpoint.distanceTo(currpoint) > 2 * shapepoint_mindensity) {
          index = addPointsToLine(index, points, randompoints, prevpoint, currpoint, min_x, min_y, 
            array, width, height, shapepoint_circle, shapepoint_mindensity);
        }
      }
      


      firstpoint = (VPoint)points.get(0);
      VPoint lastpoint = (VPoint)points.get(points.size() - 1);
      if ((x != x) || (y != y))
      {
        if ((splitlonglines) && (lastpoint != null) && (lastpoint.distanceTo(firstpoint) > 2 * shapepoint_mindensity)) {
          addPointsToLine(points.size(), points, randompoints, lastpoint, firstpoint, min_x, min_y, 
            array, width, height, shapepoint_circle, shapepoint_mindensity);
        }
        

        points.add(first);
      }
    }
    


    for (x = 0; x < width; x++) {
      for (y = 0; y < height; y++) {
        array[x][y] = 1;
      }
    }
    for (VPoint point : points)
    {
      x = x - min_x;
      y = y - min_y;
      

      unsetCircle(-1, x, y, 
        array, width, height, internal_circle, internal_mindensity);
    }
    

    BufferedImage image = new BufferedImage(width, height, 13);
    Graphics2D g = (Graphics2D)image.getGraphics();
    

    IndexColorModel colormodel = (IndexColorModel)image.getColorModel();
    Color BACKGROUND = new Color(colormodel.getRGB(1));
    Color SHAPE = new Color(colormodel.getRGB(2));
    

    g.setPaint(BACKGROUND);
    g.drawRect(0, 0, width, height);
    

    g.setPaint(SHAPE);
    g.fill(new PathIteratorWrapper(new ListPathIterator(points_original, min_x, min_y)));
    

    int SHAPE_RGB = SHAPE.getRGB();
    int pointsleft = 0;
    for (x = 0; x < width; x++) {
      for (y = 0; y < height; y++)
      {
        if (array[x][y] != 0)
        {


          if (image.getRGB(x, y) == SHAPE_RGB) {
            pointsleft++;
          } else {
            array[x][y] = 0;
          }
        }
      }
    }
    
    g.dispose();
    image = null;
    g = null;
    

    int x2 = 0;int y2 = 0;
    int index_x = 0;int index_y = 0;
    for (; internalpoints > 0; internalpoints--)
    {
      if (pointsleft <= 100) {
        return randompoints;
      }
      

      int point = (int)(Math.random() * (pointsleft - 1) + 1.0D);
      
      for (x = 0; x < width; x++) {
        for (y = 0; y < height; y++) {
          if (array[x][y] != 0) {
            if (point <= 0) {
              break;
            }
            point--;
          }
        }
      }
      


      randompoints.add(new VPoint(x + min_x, y + min_y));
      

      pointsleft = unsetCircle(pointsleft, x, y, 
        array, width, height, internal_circle, internal_mindensity);
    }
    
    return randompoints;
  }
  
  private static boolean[][] createCircle(int radius) {
    boolean[][] circle = new boolean[radius * 2][radius * 2];
    for (int x = 0; x < radius * 2; x++) {
      for (int y = 0; y < radius * 2; y++) {
        if (Math.sqrt((radius - x) * (radius - x) + (radius - y) * (radius - y)) <= radius) {
          circle[x][y] = 1;
        } else {
          circle[x][y] = 0;
        }
      }
    }
    return circle;
  }
  
  private static int addPointsToLine(int index, List<VPoint> points, ArrayList<VPoint> randompoints, VPoint prevpoint, VPoint currpoint, int min_x, int min_y, boolean[][] array, int width, int height, boolean[][] circle, int maxdensity)
  {
    double length = prevpoint.distanceTo(currpoint);
    int pointstoadd = (int)(length / (maxdensity + 1));
    

    double grad = (y - y) / (x - x);
    double skip_x = Math.sqrt(length * length / (pointstoadd * pointstoadd) / (1.0D + grad * grad));
    double skip_y = Math.sqrt(length * length / (pointstoadd * pointstoadd) / (1.0D + 1.0D / (grad * grad)));
    if (x > x) skip_x *= -1.0D;
    if (y > y) { skip_y *= -1.0D;
    }
    

    for (int pointnum = 1; pointnum <= pointstoadd; pointnum++)
    {
      int x = x - min_x + (int)(skip_x * pointnum);
      int y = y - min_y + (int)(skip_y * pointnum);
      

      if (array[x][y] != 0)
      {
        unsetCircle(-1, x, y, 
          array, width, height, circle, maxdensity);
        


        VPoint point = new VPoint(x + min_x, y + min_y);
        points.add(index, point);
        randompoints.add(point);
        

        index++;
      }
    }
    

    return index;
  }
  
  private static int unsetCircle(int pointsleft, int x, int y, boolean[][] array, int width, int height, boolean[][] circle, int maxdensity)
  {
    for (int x2 = 0; x2 < maxdensity * 2; x2++) {
      for (int y2 = 0; y2 < maxdensity * 2; y2++) {
        if (circle[x2][y2] != 0) {
          int index_x = x + x2 - maxdensity;
          int index_y = y + y2 - maxdensity;
          if (index_x >= width)
            return pointsleft;
          if ((index_x < 0) || (index_y >= height))
            break;
          if (index_y >= 0)
          {
            if (array[index_x][index_y] != 0) {
              pointsleft--;
              array[index_x][index_y] = 0;
            } }
        }
      }
    }
    return pointsleft;
  }
  







  public static ArrayList<VPoint> createShapeOutline(String text, Rectangle bounds, java.awt.Font font)
    throws ShapeGenerationException
  {
    TextLayout textlayout = new TextLayout(text, font, FONT_RENDER);
    

    Rectangle2D shapebounds = textlayout.getBounds();
    

    double scale_x = width / shapebounds.getWidth();
    double scale_y = height / shapebounds.getHeight();
    double translate_x = x / scale_x - shapebounds.getX();
    double translate_y = y / scale_y - shapebounds.getY();
    AffineTransform transform = AffineTransform.getScaleInstance(scale_x, scale_y);
    transform.translate(translate_x, translate_y);
    

    Shape outline = textlayout.getOutline(transform);
    PathIterator pathiter = outline.getPathIterator(null, 0.0D);
    

    ArrayList<VPoint> points = new ArrayList();
    double[] currpoint = new double[2];
    while (!pathiter.isDone())
    {
      int type = pathiter.currentSegment(currpoint);
      if (type == 0) {
        points.add(new VPoint((int)currpoint[0], (int)currpoint[1]));
      } else if (type == 1) {
        points.add(new VPoint((int)currpoint[0], (int)currpoint[1]));
      } else { if (type == 4) {
          break;
        }
        throw new RuntimeException("Unexpected type " + type + " returned");
      }
      

      pathiter.next();
    }
    

    return points;
  }
  
  private static class PathIteratorWrapper
    implements Shape
  {
    private PathIterator iter;
    
    public PathIteratorWrapper(PathIterator _iter)
    {
      iter = _iter;
    }
    
    public PathIterator getPathIterator(AffineTransform at) { return iter; }
    public PathIterator getPathIterator(AffineTransform at, double flatness) { return iter; }
    
    public boolean contains(double x, double y) { throw new RuntimeException("Unimplemented method"); }
    public boolean contains(double x, double y, double w, double h) { throw new RuntimeException("Unimplemented method"); }
    public boolean contains(java.awt.geom.Point2D p) { throw new RuntimeException("Unimplemented method"); }
    public boolean contains(Rectangle2D r) { throw new RuntimeException("Unimplemented method"); }
    public Rectangle getBounds() { throw new RuntimeException("Unimplemented method"); }
    public Rectangle2D getBounds2D() { throw new RuntimeException("Unimplemented method"); }
    public boolean intersects(double x, double y, double w, double h) { throw new RuntimeException("Unimplemented method"); }
    public boolean intersects(Rectangle2D r) { throw new RuntimeException("Unimplemented method"); }
  }
  
  private static class ListPathIterator implements PathIterator {
    private int index = 0;
    
    private List<VPoint> points;
    
    private int offset_x;
    private int offset_y;
    
    public ListPathIterator(List<VPoint> points) { this(points, 0, 0); }
    
    public ListPathIterator(List<VPoint> _points, int _offset_x, int _offset_y) {
      points = _points;
      offset_x = _offset_x;
      offset_y = _offset_y;
    }
    
    public void resetIterator() {
      index = 0;
    }
    
    public boolean isDone() { return index >= points.size(); }
    public void next() { index += 1; }
    
    public int currentSegment(double[] coords) {
      VPoint point = (VPoint)points.get(index);
      coords[0] = (x - offset_x);
      coords[1] = (y - offset_y);
      return getReturnValue();
    }
    
    public int currentSegment(float[] coords) { VPoint point = (VPoint)points.get(index);
      coords[0] = (x - offset_x);
      coords[1] = (y - offset_y);
      return getReturnValue();
    }
    
    private int getReturnValue() { if (index == 0)
        return 0;
      if (index < points.size() - 1) {
        return 1;
      }
      return 4;
    }
    




    public int getWindingRule()
    {
      return 1;
    }
  }
}
