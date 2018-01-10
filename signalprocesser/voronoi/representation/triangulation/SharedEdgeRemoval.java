package signalprocesser.voronoi.representation.triangulation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeMap;

public class SharedEdgeRemoval
{
  private static final Comparator EDGELENGTH_COMPARATOR = new Comparator() {
    public int compare(VHalfEdge e1, VHalfEdge e2) {
      if (e1.getLength() > e2.getLength()) return -1;
      if (e1.getLength() < e2.getLength()) { return 1;
      }
      


      if (e1.getY() < e2.getY()) return -1;
      if (e1.getY() > e2.getY()) return 1;
      if (e1.getX() < e2.getX()) return -1;
      if (e1.getX() > e2.getX()) return 1;
      return 0;
    }
  };
  


  public SharedEdgeRemoval() {}
  

  public static void removeEdgesInOrderFromOuterBoundary(VHalfEdge _outeredge, int length_cutoff)
  {
    OrderedEdgeList outeredges = new OrderedEdgeList(_outeredge, null);
    







    int edgesremoved = 0;
    boolean haschanged;
    do { haschanged = false;
      Iterator<VHalfEdge> iter = outeredges.values().iterator();
      while (iter.hasNext()) {
        VHalfEdge edge = (VHalfEdge)iter.next();
        









        if (edge.getLength() <= length_cutoff) {
          return;
        }
        


        if ((vertex.getEdges().size() > 2) && (edge.getConnectedVertex().getEdges().size() > 2))
        {





          VHalfEdge inneredge = edge.getConnectedVertex().getEdge(vertex);
          VVertex innertriangletopvertex = next.getConnectedVertex();
          if (innertriangletopvertex.getEdge(-1) == null)
          {




            iter.remove();
            edgesremoved++;
            removeSingleOuterEdge(edge, outeredges);
            haschanged = true;
            break;
          }
        } } } while ((haschanged) && (
    
      (TriangulationRepresentation.MAX_EDGES_TO_REMOVE < 0) || 
      (edgesremoved < TriangulationRepresentation.MAX_EDGES_TO_REMOVE)));
  }
  


  private static void removeSingleOuterEdge(VHalfEdge outeredge, OrderedEdgeList outeredges)
  {
    VHalfEdge inneredge = outeredge.getConnectedVertex().getEdge(vertex);
    



    if (!vertex.removeEdge(outeredge)) {
      throw new RuntimeException("Outer edge not removed successfully");
    }
    if (!vertex.removeEdge(inneredge)) {
      throw new RuntimeException("Inner edge not removed successfully");
    }
    

    VHalfEdge previousedge = null;
    
    for (VHalfEdge tmpedge : vertex.getEdges())
    {
      VHalfEdge tmppreviousedge = tmpedge.getConnectedVertex().getEdge(-1);
      if ((tmppreviousedge != null) && (next == outeredge)) {
        previousedge = tmppreviousedge;
        break;
      }
    }
    if (previousedge == null) {
      throw new RuntimeException("Previous edge was null");
    }
    


    VHalfEdge newouteredge = next;
    
    next = newouteredge;
    do
    {
      outeredges.addEdge(newouteredge);
      vertexnumber = -1;
    } while (next).next != inneredge);
    outeredges.addEdge(newouteredge);
    vertexnumber = -1;
    next = next;
    



    vertexnumber = (outeredge.vertexnumber = -99);
    
    next = (outeredge.next = null);
  }
  




  private static class OrderedEdgeList
    extends TreeMap<VHalfEdge, VHalfEdge>
  {
    private OrderedEdgeList(VHalfEdge outeredge)
    {
      super();
      addOuterEdges(outeredge);
    }
    
    public void addOuterEdges(VHalfEdge outeredge)
    {
      if ((outeredge == null) || (next == null)) {
        return;
      }
      

      VHalfEdge curredge = outeredge;
      do {
        super.put(curredge, curredge);
      } while ((next).next != null) && (curredge != outeredge));
    }
    


    public void addEdge(VHalfEdge edge)
    {
      super.put(edge, edge);
    }
  }
}
