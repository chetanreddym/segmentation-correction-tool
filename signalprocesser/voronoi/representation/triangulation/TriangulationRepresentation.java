package signalprocesser.voronoi.representation.triangulation;

import java.awt.Graphics2D;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import signalprocesser.voronoi.VPoint;
import signalprocesser.voronoi.eventqueue.VSiteEvent;
import signalprocesser.voronoi.representation.AbstractRepresentation;
import signalprocesser.voronoi.statusstructure.VLinkedNode;








public class TriangulationRepresentation
  extends AbstractRepresentation
{
  public static final int OUTER_VERTEXNUMBER = -1;
  public static boolean SHOW_INTERNAL_TRIANGLES = false;
  public static boolean SHOW_EDGE_LENGTHS = false;
  public static boolean SHOW_DEBUG_INFO = false;
  public static int MAX_EDGES_TO_REMOVE = -1;
  
  public static int MODE_REDUCE_OUTER_BOUNDARIES = 1;
  public static int MODE_GETSTATS_EXCLUDINGMSTSTATS = 2;
  public static int MODE_DETERMINE_MINSPANNINGTREE = 3;
  public static int MODE_DETERMINE_CLUSTERS = 4;
  

  private int mode;
  

  private int vertexnumber;
  
  public Collection<VPoint> vertexpoints;
  
  private CalcCutOff calccutoff = null;
  private int length_cutoff = -1;
  
  private double gradient_diff_before_cluster_cutoff = 1.2D;
  private final ArrayList<VCluster> clusters = new ArrayList();
  
  private boolean update_statistics;
  private int max_length = -1;
  private int min_length = -1;
  private int max_length_of_smallesttriangleedge = -1;
  private int max_length_from_minimumspanningtree = -1;
  




  public TriangulationRepresentation() { setReduceOuterBoundariesMode(); }
  
  public TriangulationRepresentation(int length_cutoff) {
    this();setIntegerLengthCutoff(length_cutoff);
  }
  
  public TriangulationRepresentation(CalcCutOff calccutoff) { this();setCalcCutOff(calccutoff);
  }
  



  public int getMode() { return mode; }
  
  public void setReduceOuterBoundariesMode() {
    update_statistics = true;
    mode = MODE_REDUCE_OUTER_BOUNDARIES;
  }
  
  public void setGetStatsMode() {
    update_statistics = true;
    mode = MODE_DETERMINE_MINSPANNINGTREE;
  }
  
  public void setGetStatsExcludingMSTStatsMode() {
    update_statistics = true;
    mode = MODE_GETSTATS_EXCLUDINGMSTSTATS;
  }
  
  public void setDetermineMinSpanningTreeMode() {
    update_statistics = false;
    mode = MODE_DETERMINE_MINSPANNINGTREE;
  }
  
  public void setDetermineClustersMode() {
    update_statistics = false;
    mode = MODE_DETERMINE_CLUSTERS;
  }
  


  public int calculateLengthCutoff()
  {
    if (calccutoff != null) {
      return calccutoff.calculateCutOff(this);
    }
    return length_cutoff;
  }
  
  public int getIntegerLengthCutoff()
  {
    if (calccutoff != null) {
      throw new RuntimeException("CalcCutOff object registered - length_cutoff variable is ignored");
    }
    return length_cutoff;
  }
  
  public void setIntegerLengthCutoff(int _length_cutoff) { if (calccutoff != null) {
      throw new RuntimeException("CalcCutOff object registered - length_cutoff variable is ignored");
    }
    length_cutoff = _length_cutoff;
  }
  
  public CalcCutOff getCalcCutOff() { return calccutoff; }
  
  public void setCalcCutOff(CalcCutOff _calccutoff) { calccutoff = _calccutoff;
    length_cutoff = -1;
  }
  
  public int getMaxLength() {
    if (update_statistics) {
      return max_length;
    }
    throw new RuntimeException("Calculation of statistics are currently disabled");
  }
  
  public int getMinLength() {
    if (update_statistics) {
      return min_length;
    }
    throw new RuntimeException("Calculation of statistics are currently disabled");
  }
  
  public int getMaxLengthOfSmallestTriangleEdge() {
    if (update_statistics) {
      return max_length_of_smallesttriangleedge;
    }
    throw new RuntimeException("Calculation of statistics are currently disabled");
  }
  
  public int getMaxLengthOfMinimumSpanningTree() {
    if (update_statistics) {
      return max_length_from_minimumspanningtree;
    }
    throw new RuntimeException("Calculation of statistics are currently disabled");
  }
  



  public VPoint createPoint(int x, int y)
  {
    return new VVertex(x, y);
  }
  





  public void beginAlgorithm(Collection<VPoint> points)
  {
    vertexpoints = points;
    

    for (VPoint point : points) {
      VVertex vertex = (VVertex)point;
      vertex.clearEdges();
    }
    

    if (update_statistics) {
      max_length = -1;
      min_length = -1;
      max_length_of_smallesttriangleedge = -1;
      max_length_from_minimumspanningtree = -1;
    }
    

    clusters.clear();
    

    vertexnumber = 1;
  }
  
  public void siteEvent(VLinkedNode n1, VLinkedNode n2, VLinkedNode n3) {}
  
  public void circleEvent(VLinkedNode n1, VLinkedNode n2, VLinkedNode n3, int circle_x, int circle_y)
  {
    VVertex v1 = (VVertex)siteevent.getPoint();
    VVertex v2 = (VVertex)siteevent.getPoint();
    VVertex v3 = (VVertex)siteevent.getPoint();
    
    VHalfEdge e1;
    
    v1.addEdge(e1 = new VHalfEdge(vertexnumber, v1));
    VHalfEdge e2; v2.addEdge(e2 = new VHalfEdge(vertexnumber, v2));
    VHalfEdge e3; v3.addEdge(e3 = new VHalfEdge(vertexnumber, v3));
    

    next = e2;
    next = e3;
    next = e1;
    

    if (update_statistics) {
      int[] lengths = { e1.getLength(), e2.getLength(), e3.getLength() };
      Arrays.sort(lengths);
      if (lengths[2] > max_length)
        max_length = lengths[2];
      if ((min_length < 0) || (lengths[0] < min_length))
        min_length = lengths[0];
      if (lengths[0] > max_length_of_smallesttriangleedge) {
        max_length_of_smallesttriangleedge = lengths[0];
      }
    }
    
    vertexnumber += 1;
  }
  
  public void endAlgorithm(Collection<VPoint> points, int lastsweeplineposition, VLinkedNode headnode)
  {
    VHalfEdge outeredge = createOuterEdge();
    if (outeredge != null)
    {
      if (mode != MODE_GETSTATS_EXCLUDINGMSTSTATS)
      {
        if (mode == MODE_DETERMINE_CLUSTERS) {
          max_length_from_minimumspanningtree = 
            SharedMinimumSpanningTree.determineMSTUsingPrimsAlgorithm(vertex, gradient_diff_before_cluster_cutoff, clusters);
        } else {
          max_length_from_minimumspanningtree = 
            SharedMinimumSpanningTree.determineMSTUsingPrimsAlgorithm(vertex);
        }
      }
      
      if (mode == MODE_REDUCE_OUTER_BOUNDARIES) {
        int length_cutoff = calculateLengthCutoff();
        SharedEdgeRemoval.removeEdgesInOrderFromOuterBoundary(outeredge, length_cutoff);
      }
    }
  }
  
  private VHalfEdge createOuterEdge() {
    VVertex currvertex = null;
    VVertex firstvertex = null;
    VHalfEdge firstedge = null;
    

    VHalfEdge edge;
    
    for (VPoint point : vertexpoints) {
      VVertex vertex = (VVertex)point;
      


      if (vertex.hasEdges())
      {

        for (Iterator localIterator2 = vertex.getEdges().iterator(); localIterator2.hasNext();) { edge = (VHalfEdge)localIterator2.next();
          
          if (!edge.getConnectedVertex().isConnectedTo(vertex))
          {




            firstvertex = vertex;
            currvertex = edge.getConnectedVertex();
            currvertex.addEdge(firstedge = new VHalfEdge(-1, currvertex));
            
            break;
          }
        }
      }
    }
    

    if (currvertex == null)
    {
      return null;
    }
    



    VHalfEdge prevedge = firstedge;
    VVertex nextvertex;
    do {
      nextvertex = null;
      for (VHalfEdge edge : currvertex.getEdges())
      {
        if (!edge.getConnectedVertex().isConnectedTo(currvertex))
        {

          nextvertex = edge.getConnectedVertex();
          break;
        }
      }
      

      if (nextvertex == null) {
        throw new RuntimeException("Edge's in invalid state - didn't find next vertex");
      }
      


      nextvertex.addEdge(prevedge = new VHalfEdge(-1, nextvertex, prevedge));
    } while ((currvertex = nextvertex) != firstvertex);
    

    next = prevedge;
    


    return firstedge;
  }
  







  public ArrayList<VPoint> getPointsFormingOutterBoundary()
  {
    VHalfEdge outeredge = findOuterEdge();
    

    if ((outeredge == null) || (next == null)) {
      return null;
    }
    

    VHalfEdge curredge = outeredge;
    ArrayList<VPoint> pointlist = new ArrayList();
    do {
      pointlist.add(vertex);
    } while ((next).next != null) && (curredge != outeredge));
    

    if (curredge == outeredge) {
      pointlist.add(vertex);
    }
    

    return pointlist;
  }
  
  public void paint(Graphics2D g)
  {
    VHalfEdge curredge;
    VVertex vertex2;
    if ((mode == MODE_REDUCE_OUTER_BOUNDARIES) && (!SHOW_INTERNAL_TRIANGLES))
    {

      VHalfEdge outeredge = findOuterEdge();
      

      if ((outeredge == null) || (next == null)) {
        return;
      }
      
      System.out.println("outeredge: " + outeredge.getLength());
      
      curredge = outeredge;
      int i = 0;
      do
      {
        g.drawLine(curredge.getX(), curredge.getY(), next.getX(), next.getY());
        

        if (SHOW_EDGE_LENGTHS) {
          VVertex vertex = vertex;
          vertex2 = curredge.getConnectedVertex();
          g.drawString(Integer.toString(curredge.getLength()), (int)(x + (x - x) * 0.5D + 3.0D), (int)(y + (y - y) * 0.5D - 3.0D));
        }
        

        if (SHOW_DEBUG_INFO) {
          VVertex vertex = vertex;
          g.drawString(Integer.toString(id), x + 6, y);
        }
        i++;
        if (next).next == null) break; } while (curredge != outeredge);
    }
    else
    {
      for (VPoint point : vertexpoints) {
        VVertex vertex = (VVertex)point;
        

        if (vertex.hasEdges())
        {



          for (VHalfEdge edge : vertex.getEdges())
          {
            if ((mode == MODE_REDUCE_OUTER_BOUNDARIES) || (shownonminimumspanningtree))
            {


              VVertex vertex2 = next.vertex;
              
              g.drawLine(x, y, x, y);
              if (SHOW_EDGE_LENGTHS) {
                g.drawString(Integer.toString(edge.getLength()), (int)(x + (x - x) * 0.5D + 3.0D), (int)(y + (y - y) * 0.5D - 3.0D));
              }
            }
          }
          

          if (SHOW_DEBUG_INFO)
            g.drawString(Integer.toString(id), x + 6, y);
        }
      }
    }
  }
  
  public VHalfEdge findOuterEdge() {
    for (VPoint point : vertexpoints) {
      VVertex vertex = (VVertex)point;
      

      if (vertex.hasEdges())
      {



        for (VHalfEdge edge : vertex.getEdges()) {
          if (edge.isOuterEdge())
            return edge;
        }
      }
    }
    return null;
  }
  
  public static abstract class CalcCutOff
  {
    public CalcCutOff() {}
    
    public abstract int calculateCutOff(TriangulationRepresentation paramTriangulationRepresentation);
  }
}
