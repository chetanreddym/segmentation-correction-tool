package signalprocesser.voronoi.representation.triangulation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeMap;

public class SharedMinimumSpanningTree
{
  private static final Comparator VERTEX_COMPARATOR = new Comparator() {
    public int compare(VVertex v1, VVertex v2) {
      if (y < y) return -1;
      if (y > y) return 1;
      if (x < x) return -1;
      if (x > x) return 1;
      if (v1 == v2) { return 0;
      }
      throw new RuntimeException("No basis for comparing two apparently identical vertexes - " + v1 + " and " + v2);
    }
  };
  

  private static final Comparator PATH_COMPARATOR = new Comparator() {
    public int compare(VHalfEdge e1, VHalfEdge e2) {
      if (e1.getLength() < e2.getLength()) return -1;
      if (e1.getLength() > e2.getLength()) { return 1;
      }
      


      if (e1.getY() < e2.getY()) return -1;
      if (e1.getY() > e2.getY()) return 1;
      if (e1.getX() < e2.getX()) return -1;
      if (e1.getX() > e2.getX()) return 1;
      if (e1 == e2) { return 0;
      }
      
      if ((next != null) && (next != null)) {
        if (next.getY() < next.getY()) return -1;
        if (next.getY() > next.getY()) return 1;
        if (next.getX() < next.getX()) return -1;
        if (next.getX() > next.getX()) { return 1;
        }
        throw new RuntimeException("No basis for comparing two apparently identical vectors - " + e1 + " and " + e2);
      }
      if (next == null)
        return -1;
      if (next == null) {
        return 1;
      }
      throw new RuntimeException("No basis for comparing two apparently identical vectors - " + e1 + " and " + e2);
    }
  };
  


  public SharedMinimumSpanningTree() {}
  


  public static int determineMSTUsingPrimsAlgorithm(VVertex startingvertex)
  {
    VertexList vertexes = new VertexList(null);
    FuturePathList futurepaths = new FuturePathList(null);
    int maxpathofminimumspanningtree = -1;
    
    VVertex minimum = startingvertex;
    for (;;)
    {
      vertexes.addVertex(minimum);
      

      Iterator<VHalfEdge> iter = futurepaths.values().iterator();
      while (iter.hasNext()) {
        VHalfEdge path = (VHalfEdge)iter.next();
        if (path.getConnectedVertex() == minimum) {
          iter.remove();
        }
      }
      

      for (VHalfEdge path : minimum.getEdges()) {
        if (!vertexes.hasVertexBeenConsidered(path.getConnectedVertex()))
        {



          futurepaths.addPath(path);
        }
      }
      if (futurepaths.size() <= 0) {
        return maxpathofminimumspanningtree;
      }
      

      VHalfEdge nextbestpath = futurepaths.popBestNextPath();
      shownonminimumspanningtree = true;
      if (nextbestpath.getLength() > maxpathofminimumspanningtree) {
        maxpathofminimumspanningtree = nextbestpath.getLength();
      }
      

      minimum = nextbestpath.getConnectedVertex();
    }
  }
  






  public static int determineMSTUsingPrimsAlgorithm(VVertex startingvertex, double gradient_diff_before_cluster_cutoff, ArrayList<VCluster> clusters)
  {
    VertexList vertexes = new VertexList(null);
    FuturePathList futurepaths = new FuturePathList(null);
    TreeMap<Integer, Integer> lengths = new TreeMap();
    int maxpathofminimumspanningtree = -1;
    
    VVertex minimum = startingvertex;
    for (;;)
    {
      vertexes.addVertex(minimum);
      

      Iterator<VHalfEdge> iter = futurepaths.values().iterator();
      while (iter.hasNext()) {
        VHalfEdge path = (VHalfEdge)iter.next();
        if (path.getConnectedVertex() == minimum) {
          iter.remove();
        }
      }
      

      for (VHalfEdge path : minimum.getEdges()) {
        if (!vertexes.hasVertexBeenConsidered(path.getConnectedVertex()))
        {



          futurepaths.addPath(path);
        }
      }
      if (futurepaths.size() <= 0) {
        break;
      }
      VHalfEdge nextbestpath = futurepaths.popBestNextPath();
      if (nextbestpath.getLength() > maxpathofminimumspanningtree) {
        maxpathofminimumspanningtree = nextbestpath.getLength();
      }
      

      Integer length = new Integer(nextbestpath.getLength());
      lengths.put(length, length);
      

      minimum = nextbestpath.getConnectedVertex();
    }
    


    int clustercutoff = determineClusterCutOffByGradient(lengths, 5);
    

    vertexes.clear();
    futurepaths.clear();
    minimum = startingvertex;
    VCluster currentcluster = new VCluster();
    clusters.add(currentcluster);
    for (;;)
    {
      currentcluster.add(minimum);
      

      vertexes.addVertex(minimum);
      

      Object iter = futurepaths.values().iterator();
      while (((Iterator)iter).hasNext()) {
        VHalfEdge path = (VHalfEdge)((Iterator)iter).next();
        if (path.getConnectedVertex() == minimum) {
          ((Iterator)iter).remove();
        }
      }
      

      for (VHalfEdge path : minimum.getEdges()) {
        if (!vertexes.hasVertexBeenConsidered(path.getConnectedVertex()))
        {



          futurepaths.addPath(path);
        }
      }
      if (futurepaths.size() <= 0) {
        break;
      }
      VHalfEdge nextbestpath = futurepaths.popBestNextPath();
      


      if (nextbestpath.getLength() <= clustercutoff) {
        shownonminimumspanningtree = true;
      } else {
        currentcluster = new VCluster();
        clusters.add(currentcluster);
      }
      

      minimum = nextbestpath.getConnectedVertex();
    }
    
    return maxpathofminimumspanningtree;
  }
  









  private static int determineClusterCutOffByGradient(TreeMap<Integer, Integer> sortedvalues, int gradient_error_allowed)
  {
    Iterator<Integer> iter = sortedvalues.values().iterator();
    



    if (!iter.hasNext()) return -1;
    int prev = ((Integer)iter.next()).intValue();
    if (!iter.hasNext()) return prev;
    int curr = ((Integer)iter.next()).intValue();
    int currgrad = curr - prev;
    

    int index = 1;
    int valuesonline = 2;
    int best_valuesonline = -1;
    int best_cutoff = -1;
    while (iter.hasNext())
    {
      index++;
      prev = curr;
      int prevgrad = currgrad;
      curr = ((Integer)iter.next()).intValue();
      currgrad = curr - prev;
      



      if ((prevgrad - gradient_error_allowed <= currgrad) && (currgrad <= prevgrad + gradient_error_allowed)) {
        valuesonline++;
      }
      else
      {
        if (valuesonline >= sortedvalues.size() - index - 1)
        {
          return prev;
        }
        

        if (valuesonline > best_valuesonline)
        {
          best_valuesonline = valuesonline;
          best_cutoff = prev;
        }
        



        valuesonline = 2;
      }
    }
    

    if (valuesonline > best_valuesonline)
    {
      best_valuesonline = valuesonline;
      best_cutoff = prev;
    }
    


    return best_cutoff;
  }
  






  private static int determineClusterCutOffByDifference(TreeMap<Integer, Integer> sortedvalues, double percentage_diff_before_cluster_cutoff)
  {
    int index = 0;
    int curr = -1;
    int clustercutoff = -1;
    int clustercutoff_index = -1;
    int prev_clustercutoff = -1;
    int prev_clustercutoff_index = -1;
    int valuesincluster = 1;
    
    for (Integer value : sortedvalues.values())
    {
      if (curr == -1) {
        curr = value.intValue();

      }
      else
      {
        index++;
        int prev = curr;
        curr = value.intValue();
        

        if (curr <= prev * percentage_diff_before_cluster_cutoff) {
          valuesincluster++;
        } else {
          if (valuesincluster >= 3) {
            prev_clustercutoff = prev;
            prev_clustercutoff_index = index;
          }
          clustercutoff = prev;
          clustercutoff_index = index;
          valuesincluster = 1;
        }
      }
    }
    if (valuesincluster >= 3) {
      if ((clustercutoff == -1) || (clustercutoff_index <= 0.25D * sortedvalues.size())) {
        return curr;
      }
      return clustercutoff;
    }
    
    if ((prev_clustercutoff == -1) || (prev_clustercutoff_index <= 0.25D * sortedvalues.size())) {
      return curr;
    }
    return prev_clustercutoff;
  }
  






  private static class VertexList
    extends TreeMap<VVertex, VVertex>
  {
    private VertexList()
    {
      super();
    }
    


    public boolean hasVertexBeenConsidered(VVertex vertex)
    {
      return super.get(vertex) != null;
    }
    
    public void addVertex(VVertex vertex) { super.put(vertex, vertex); }
  }
  




  private static class FuturePathList
    extends TreeMap<VHalfEdge, VHalfEdge>
  {
    private FuturePathList()
    {
      super();
    }
    


    public VHalfEdge popBestNextPath()
    {
      VHalfEdge next = (VHalfEdge)super.firstKey();
      super.remove(next);
      return next;
    }
    
    public void addPath(VHalfEdge path) {
      super.put(path, path);
    }
  }
}
