package signalprocesser.voronoi;

import java.awt.geom.PathIterator;
import java.util.ArrayList;
import signalprocesser.voronoi.eventqueue.VCircleEvent;
import signalprocesser.voronoi.eventqueue.VSiteEvent;

public class VoronoiShared
{
  public VoronoiShared() {}
  
  public static double[] solveQuadratic(double a, double b, double c)
  {
    if (a == 0.0D) {
      if (b != 0.0D) {
        double[] answers = new double[1];
        answers[0] = (-1.0D * c / b);
        return answers;
      }
      throw new RuntimeException("Only given a non-zero c value");
    }
    
    double[] answers = new double[2];
    double bsqrdplus4ac = Math.sqrt(b * b - 4.0D * a * c);
    answers[0] = ((-b + bsqrdplus4ac) / (2.0D * a));
    answers[1] = ((-b - bsqrdplus4ac) / (2.0D * a));
    return answers;
  }
  
  public static VCircleEvent calculateCenter(VSiteEvent u, VSiteEvent v, VSiteEvent w)
  {
    double a = (u.getX() - v.getX()) * (v.getY() - w.getY()) - (u.getY() - v.getY()) * (v.getX() - w.getX());
    if (a > 0.0D) {
      double b1 = (u.getX() - v.getX()) * (u.getX() + v.getX()) + (u.getY() - v.getY()) * (u.getY() + v.getY());
      double b2 = (v.getX() - w.getX()) * (v.getX() + w.getX()) + (v.getY() - w.getY()) * (v.getY() + w.getY());
      
      VCircleEvent centerpoint = new VCircleEvent();
      double x = (b1 * (v.getY() - w.getY()) - b2 * (u.getY() - v.getY())) / (2.0D * a);
      double center_y = (b2 * (u.getX() - v.getX()) - b1 * (v.getX() - w.getX())) / (2.0D * a);
      centerpoint.setX((int)x);
      centerpoint.setY((int)(center_y + Math.sqrt((x - u.getX()) * (x - u.getX()) + (center_y - u.getY()) * (center_y - u.getY()))));
      centerpoint.setCenterY((int)center_y);
      return centerpoint;
    }
    return null;
  }
  



  public static double calculateAreaOfShape(ArrayList<VPoint> points)
  {
    if (points.size() <= 2) { return 0.0D;
    }
    
    int area = 0;
    VPoint first = (VPoint)points.get(0);
    VPoint curr = first;
    for (int x = 1; x < points.size(); x++)
    {
      VPoint prev = curr;
      curr = (VPoint)points.get(x);
      

      area += x * y - x * y;
    }
    

    if ((x != x) || (y != y)) {
      area += x * y - x * y;
    }
    

    if (area >= 0) {
      return area / 2.0D;
    }
    return area / -2.0D;
  }
  

  public static double calculatePerimeterOfShape(ArrayList<VPoint> points)
  {
    if (points.size() <= 1) { return 0.0D;
    }
    
    double perimeter = 0.0D;
    VPoint first = (VPoint)points.get(0);
    VPoint curr = first;
    for (int x = 1; x < points.size(); x++)
    {
      VPoint prev = curr;
      curr = (VPoint)points.get(x);
      

      perimeter += Math.sqrt((x - x) * (x - x) + (y - y) * (y - y));
    }
    

    if ((x != x) || (y != y)) {
      perimeter += Math.sqrt((x - x) * (x - x) + (y - y) * (y - y));
    }
    

    return perimeter;
  }
  
  public static double calculateAreaOfShape(java.awt.Shape shape) {
    return calculateAreaOfShape(shape.getPathIterator(null));
  }
  


  public static double calculateAreaOfShape(PathIterator pathiter)
  {
    if (pathiter.isDone()) { return 0.0D;
    }
    

    double totalarea = 0.0D;
    double[] first = new double[2];
    double[] curr = new double[2];
    double[] prev = new double[2];
    
    int type;
    do
    {
      type = pathiter.currentSegment(curr);
      

      if (type != 0) {
        throw new RuntimeException("Expected PathIterator.SEG_MOVETO; instead type=" + formatPathIteratorType(type));
      }
      

      double subarea = 0.0D;
      


      first[0] = curr[0];
      first[1] = curr[1];
      



      for (;;)
      {
        pathiter.next();
        

        prev[0] = curr[0];
        prev[1] = curr[1];
        type = pathiter.currentSegment(curr);
        if (type != 1)
          break;
        subarea += prev[0] * curr[1] - curr[0] * prev[1];
      }
      


      if (type != 4)
        break;
      if ((first[0] != prev[0]) || (first[1] != prev[1])) {
        subarea += prev[0] * first[1] - first[0] * prev[1];
      }
      




      totalarea += (subarea >= 0.0D ? subarea : -1.0D * subarea);
      


      pathiter.next();

    }
    while (!pathiter.isDone());
    
    return totalarea / 2.0D;
    




    throw new RuntimeException("Expected either PathIterator.SEG_LINETO or SEG_CLOSE; instead type=" + formatPathIteratorType(type));
  }
  


  private static String formatPathIteratorType(int type)
  {
    switch (type) {
    case 4: 
      return "SEG_CLOSE";
    case 3: 
      return "SEG_CUBICTO";
    case 1: 
      return "SEG_LINETO";
    case 0: 
      return "SEG_MOVETO";
    case 2: 
      return "SEG_QUADTO";
    }
    return "UKNOWNTYPE (" + type + ")";
  }
}
