package signalprocesser.voronoi.representation.boundaryproblem;

import java.util.ArrayList;
import signalprocesser.voronoi.VPoint;

public class VVertex extends VPoint
{
  public static int uniqueid = 1;
  
  public int id = uniqueid++;
  private ArrayList<VHalfEdge> connectedvertexs;
  
  public VVertex() {}
  
  public VVertex(int x, int y) { super(x, y); }
  public VVertex(VPoint point) { super(point); }
  
  public void clearConnectedVertexs() {
    if (connectedvertexs != null) {
      connectedvertexs.clear();
    }
  }
  
  public void addConnectedVertex(VHalfEdge edge) {
    if (connectedvertexs == null) {
      connectedvertexs = new ArrayList();
    }
    connectedvertexs.add(edge);
  }
  
  public ArrayList<VHalfEdge> getConnectedVertexs() {
    if ((connectedvertexs == null) || (connectedvertexs.size() <= 0)) {
      return null;
    }
    return connectedvertexs;
  }
  
  public double distanceTo(VVertex distance)
  {
    return Math.sqrt((x - x) * (x - x) + (y - y) * (y - y));
  }
  
  public VHalfEdge getNextConnectedEdge(int vertexnumber) {
    if ((connectedvertexs == null) || (connectedvertexs.size() <= 0)) {
      return null;
    }
    for (VHalfEdge edge : connectedvertexs) {
      if (vertexnumber == vertexnumber) {
        return edge;
      }
    }
    return null;
  }
  
  public VHalfEdge getNextConnectedEdge(VVertex nextvertex) {
    if ((connectedvertexs == null) || (connectedvertexs.size() <= 0)) {
      return null;
    }
    for (VHalfEdge edge : connectedvertexs) {
      if (vertex == nextvertex) {
        return edge;
      }
    }
    return null;
  }
  
  public VVertex getNextConnectedVertex(int vertexnumber)
  {
    VHalfEdge edge = getNextConnectedEdge(vertexnumber);
    return edge == null ? null : vertex;
  }
  
  public String getConnectedVertexString() {
    String str = null;
    for (VHalfEdge edge : connectedvertexs) {
      if (str == null) {
        str = vertexnumber;
      } else {
        str = str + ", " + vertexnumber;
      }
    }
    return str;
  }
}
