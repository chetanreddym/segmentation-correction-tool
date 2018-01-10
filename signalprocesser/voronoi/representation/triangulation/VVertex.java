package signalprocesser.voronoi.representation.triangulation;

import java.util.ArrayList;
import signalprocesser.voronoi.VPoint;



public class VVertex
  extends VPoint
{
  public static int uniqueid = 1;
  



  public int id = uniqueid++;
  

  private ArrayList<VHalfEdge> edges;
  

  public VVertex() {}
  
  public VVertex(int x, int y) { super(x, y); }
  public VVertex(VPoint point) { super(point); }
  




  public boolean hasEdges() { return (edges != null) && (edges.size() > 0); }
  
  public void clearEdges() {
    if (edges != null)
      edges.clear();
  }
  
  public void addEdge(VHalfEdge edge) {
    if (edges == null) {
      edges = new ArrayList();
    }
    edges.add(edge);
  }
  
  public ArrayList<VHalfEdge> getEdges() { if ((edges == null) || (edges.size() <= 0)) {
      return null;
    }
    return edges;
  }
  
  public boolean removeEdge(VHalfEdge edge) {
    if (edges == null) {
      return false;
    }
    return edges.remove(edge);
  }
  


  public double distanceTo(VVertex distance)
  {
    return Math.sqrt((x - x) * (x - x) + (y - y) * (y - y));
  }
  


  public VHalfEdge getEdge(VVertex connectedtovertex)
  {
    if ((edges == null) || (edges.size() <= 0)) {
      return null;
    }
    for (VHalfEdge edge : edges) {
      if ((next != null) && (next.vertex == connectedtovertex)) {
        return edge;
      }
    }
    return null;
  }
  
  public VHalfEdge getEdge(int vertexnumber) {
    if ((edges == null) || (edges.size() <= 0)) {
      return null;
    }
    for (VHalfEdge edge : edges)
    {


      if ((next != null) && (next.vertexnumber == vertexnumber)) {
        return edge;
      }
    }
    return null;
  }
  
  public boolean isConnectedTo(VVertex connectedtovertex)
  {
    VHalfEdge edge = getEdge(connectedtovertex);
    return edge != null;
  }
  




  public String toString() { return "VVertex (connected to " + getConnectedVertexString() + ")"; }
  
  public String getConnectedEdgeString() {
    if ((edges == null) || (edges.size() <= 0)) {
      return null;
    }
    String str = null;
    for (VHalfEdge edge : edges) {
      if (str == null) {
        str = vertexnumber;
      } else {
        str = str + ", " + vertexnumber;
      }
    }
    return str;
  }
  
  public String getConnectedVertexString() {
    if ((edges == null) || (edges.size() <= 0)) {
      return null;
    }
    String str = null;
    for (VHalfEdge edge : edges) {
      if (str == null) {
        str = getConnectedVertexid;
      } else {
        str = str + ", " + getConnectedVertexid;
      }
    }
    return str;
  }
}
