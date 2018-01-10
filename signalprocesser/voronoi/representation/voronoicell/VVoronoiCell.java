package signalprocesser.voronoi.representation.voronoicell;

import signalprocesser.voronoi.VPoint;





public class VVoronoiCell
  extends VPoint
{
  public static final double NO_AREA_CALCULATED = -1.0D;
  public static final double INVALID_AREA = -2.0D;
  public double area = -1.0D;
  
  public VVoronoiCell() {}
  
  public VVoronoiCell(int x, int y) { super(x, y); }
  public VVoronoiCell(VPoint point) { super(point); }
  
  public void resetArea() { area = -1.0D; }
  
  public double getAreaOfCell() { if (area == -1.0D) {
      area = calculateAreaOfCell();
      return area == -2.0D ? -1.0D : area; }
    if (area == -2.0D) {
      return -1.0D;
    }
    return area;
  }
  

  private double calculateAreaOfCell()
  {
    if ((halfedge == null) || (halfedge.getPrev() == null) || (halfedge.getNext() == null)) {
      return -2.0D;
    }
    

    VHalfEdge point0 = halfedge;
    VHalfEdge point1 = halfedge.getPrev();
    VHalfEdge point2 = halfedge.getNext();
    

    double totalarea = 0.0D;
    boolean rightside = true;
    


    for (;;)
    {
      totalarea = totalarea + calculateAreaOfTriangle(Math.sqrt((x - x) * (x - x) + (y - y) * (y - y)), Math.sqrt((x - x) * (x - x) + (y - y) * (y - y)), Math.sqrt((x - x) * (x - x) + (y - y) * (y - y)));
      


      if (rightside)
      {
        if (point2.getNext() == null)
          return -2.0D;
        if (point2.getNext() == point1) {
          return totalarea;
        }
        

        VHalfEdge tmp = point0;
        point0 = point2;
        point2 = point2.getNext();
        rightside = false;
      }
      else {
        if (point1.getPrev() == null)
          return -2.0D;
        if (point1.getPrev() == point2) {
          return totalarea;
        }
        

        point0 = point1;
        point1 = point0.getPrev();
        rightside = true;
      }
    }
  }
  

  public VHalfEdge halfedge;
  public static double calculateAreaOfTriangle(double a, double b, double c)
  {
    if (b > a) { double tmp = a;a = b;b = tmp; }
    if (c > b) { double tmp = b;b = c;c = tmp;
    }
    
    double tmp = (a + (b + c)) * (c - (a - b)) * (c + (a - b)) * (a + (b - c));
    return 0.25D * Math.sqrt(tmp);
  }
}
