package signalprocesser.voronoi.representation.triangulation;



public class VHalfEdge
{
  public int vertexnumber;
  
  public VHalfEdge next;
  
  public VVertex vertex;
  
  private int length = -1;
  
  public boolean shownonminimumspanningtree = false;
  



  public VHalfEdge(int _vertexnumber, VVertex _vertex)
  {
    vertexnumber = _vertexnumber;
    vertex = _vertex;
  }
  
  public VHalfEdge(int _vertexnumber, VVertex _vertex, VHalfEdge _next) {
    vertexnumber = _vertexnumber;
    vertex = _vertex;
    next = _next;
  }
  


  public boolean isOuterEdge()
  {
    return vertexnumber == -1;
  }
  
  public VVertex getConnectedVertex() {
    return next.vertex;
  }
  
  public int getLength() {
    if (length == -1) {
      length = ((int)vertex.distanceTo(next.vertex));
    }
    return length;
  }
  
  public int getX() {
    return vertex.x;
  }
  
  public int getY() {
    return vertex.y;
  }
}
