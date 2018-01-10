package signalprocesser.voronoi.eventqueue;

import signalprocesser.voronoi.VPoint;






public class VSiteEvent
  extends VEvent
{
  private VPoint point;
  public double a;
  public double b;
  public double c;
  
  public VSiteEvent(VPoint _point)
  {
    if (_point == null) {
      throw new IllegalArgumentException("Point for siteevent cannot be null");
    }
    point = _point;
  }
  


  public void calcParabolaConstants(double sweepline)
  {
    double yminussweepline = point.y - sweepline;
    a = (0.5D / yminussweepline);
    b = (-1.0D * point.x / yminussweepline);
    c = (point.x * point.x / (2.0D * yminussweepline) + 0.5D * yminussweepline);
  }
  
  public int getYValueOfParabola(int x) {
    return (int)((a * x + b) * x + c);
  }
  
  public int getYValueOfParabola(double x) {
    return (int)((a * x + b) * x + c);
  }
  



  public int getX() { return point.x; }
  public int getY() { return point.y; }
  
  public VPoint getPoint() { return point; }
  
  public boolean isSiteEvent() { return true; }
  
  public boolean isCircleEvent() { return false; }
  


  public String toString()
  {
    return "VSiteEvent (" + point.x + "," + point.y + ")";
  }
}
