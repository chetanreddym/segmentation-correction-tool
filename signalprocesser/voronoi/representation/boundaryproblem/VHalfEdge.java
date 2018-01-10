package signalprocesser.voronoi.representation.boundaryproblem;




public class VHalfEdge
{
  public int vertexnumber;
  


  public boolean isdeleted = false;
  
  public VVertex vertex;
  

  public VHalfEdge(int _vertexnumber, VVertex _vertex)
  {
    vertexnumber = _vertexnumber;
    vertex = _vertex;
  }
}
