package signalprocesser.voronoi.representation.boundaryproblem.voronoicell;

import signalprocesser.voronoi.VPoint;
import signalprocesser.voronoi.representation.boundaryproblem.VVertex;




public class VVoronoiCell
  extends VVertex
{
  public static final int NO_AREA_CALCULATED = -1;
  public static final int INVALID_AREA = -2;
  public int area = -1;
  
  public VVoronoiCell() {}
  
  public VVoronoiCell(int x, int y) { super(x, y); }
  public VVoronoiCell(VPoint point) { super(point); }
  
  public void resetArea() { area = -1; }
  
  public int getAreaOfCell() { if (area == -1) {
      area = calculateAreaOfCell();
      return area == -2 ? -1 : area; }
    if (area == -2) {
      return -1;
    }
    return area;
  }
  

  private int calculateAreaOfCell()
  {
    if ((halfedge == null) || (halfedge.getPrev() == null) || (halfedge.getNext() == null)) {
      return -2;
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
          return -2;
        if (point2.getNext() == point1) {
          return (int)totalarea;
        }
        

        VHalfEdge tmp = point0;
        point0 = point2;
        point2 = point2.getNext();
        rightside = false;
      }
      else {
        if (point1.getPrev() == null)
          return -2;
        if (point1.getPrev() == point2) {
          return (int)totalarea;
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
