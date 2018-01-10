package signalprocesser.voronoi.representation.voronoicell;



public class VHalfEdge
{
  public int x;
  

  public int y;
  
  private VHalfEdge next;
  private VHalfEdge prev;
  
  public VHalfEdge() { this(-1, -1); }
  
  public VHalfEdge(int _x, int _y) {
    x = _x;
    y = _y;
  }
  
  public void setXY(int _x, int _y) {
    x = _x;
    y = _y;
  }
  
  public VHalfEdge getNext() { return next; }
  public VHalfEdge getPrev() { return prev; }
  
  public void setNext(VHalfEdge _next) { next = _next;
    prev = this;
  }
  
  public String toString() {
    return "(" + x + "," + y + ") -> " + (next == null ? "n/a" : new StringBuilder("(").append(next.x).append(",").append(next.y).append(")").toString());
  }
}
