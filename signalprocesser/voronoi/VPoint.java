package signalprocesser.voronoi;




public class VPoint
{
  public int x;
  


  public int y;
  

  public VPoint() { this(-1, -1); }
  
  public VPoint(int _x, int _y) {
    x = _x;
    y = _y;
  }
  
  public VPoint(VPoint point) { x = x;
    y = y;
  }
  
  public double distanceTo(VPoint point) {
    return Math.sqrt((x - x) * (x - x) + (y - y) * (y - y));
  }
  
  public String toString() {
    return "VPoint (" + x + "," + y + ")";
  }
}
