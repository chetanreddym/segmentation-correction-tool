package signalprocesser.voronoi.representation.boundaryproblem;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import signalprocesser.voronoi.VPoint;
import signalprocesser.voronoi.eventqueue.VSiteEvent;
import signalprocesser.voronoi.representation.AbstractRepresentation;
import signalprocesser.voronoi.representation.boundaryproblem.voronoicell.VVoronoiCell;
import signalprocesser.voronoi.representation.boundaryproblem.voronoicell.VoronoiCellRepresentation;
import signalprocesser.voronoi.statusstructure.VLinkedNode;


public class BoundaryProblemRepresentation
  extends AbstractRepresentation
{
  public static double MIN_ANGLE_TO_ALLOW = 0.0D;
  public static int VORONOICELLAREA_CUTOFF = 8000;
  

  private int vertexnumber;
  
  private Collection<VPoint> vertexpoints;
  
  private final VoronoiCellRepresentation voronoirepresentation = new VoronoiCellRepresentation();
  




  public BoundaryProblemRepresentation() {}
  



  public VPoint createPoint(int x, int y)
  {
    return new VVoronoiCell(x, y);
  }
  





  public void beginAlgorithm(Collection<VPoint> points)
  {
    vertexpoints = points;
    

    for (VPoint point : points) {
      VVertex vertex = (VVertex)point;
      vertex.clearConnectedVertexs();
    }
    

    vertexnumber = 1;
    

    voronoirepresentation.beginAlgorithm(points);
  }
  

  public void siteEvent(VLinkedNode n1, VLinkedNode n2, VLinkedNode n3)
  {
    if (VORONOICELLAREA_CUTOFF > 0) voronoirepresentation.siteEvent(n1, n2, n3);
  }
  
  public void circleEvent(VLinkedNode n1, VLinkedNode n2, VLinkedNode n3, int circle_x, int circle_y) {
    if (VORONOICELLAREA_CUTOFF > 0) { voronoirepresentation.circleEvent(n1, n2, n3, circle_x, circle_y);
    }
    
    VVertex g1 = (VVertex)siteevent.getPoint();
    VVertex g2 = (VVertex)siteevent.getPoint();
    VVertex g3 = (VVertex)siteevent.getPoint();
    


    if (MIN_ANGLE_TO_ALLOW > 0.0D)
    {
      double[] distances = new double[3];
      distances[0] = g1.distanceTo(g2);
      distances[1] = g2.distanceTo(g3);
      distances[2] = g3.distanceTo(g1);
      Arrays.sort(distances);
      

      double a = distances[0];
      double b = distances[1];
      double c = distances[2];
      double angle = Math.acos((b * b + c * c - a * a) / (2.0D * b * c));
      

      if (angle < MIN_ANGLE_TO_ALLOW) {
        return;
      }
    }
    

    g1.addConnectedVertex(new VHalfEdge(vertexnumber, g2));
    g2.addConnectedVertex(new VHalfEdge(vertexnumber, g3));
    g3.addConnectedVertex(new VHalfEdge(vertexnumber, g1));
    

    vertexnumber += 1;
  }
  

  public void endAlgorithm(Collection<VPoint> points, int lastsweeplineposition, VLinkedNode headnode)
  {
    if (VORONOICELLAREA_CUTOFF > 0) { voronoirepresentation.endAlgorithm(points, lastsweeplineposition, headnode);
    }
    


    VVertex vertex;
    


    Iterator localIterator2;
    


    VVertex prevvertex;
    


    label294:
    

    for (Iterator localIterator1 = vertexpoints.iterator(); localIterator1.hasNext(); 
        

        localIterator2.hasNext())
    {
      VPoint point = (VPoint)localIterator1.next();
      vertex = (VVertex)point;
      if ((vertex == null) || (vertex.getConnectedVertexs() == null)) break label294;
      localIterator2 = vertex.getConnectedVertexs().iterator(); continue;VHalfEdge connectededge = (VHalfEdge)localIterator2.next();
      
      prevvertex = getPreviousVertex(vertexnumber, vertex, vertex);
      if (prevvertex != null)
      {





        for (VHalfEdge connectededge2 : vertex.getConnectedVertexs())
        {
          if (vertex == prevvertex) {
            if (VORONOICELLAREA_CUTOFF > 0) {
              VVoronoiCell voronoicell1 = (VVoronoiCell)vertex;
              VVoronoiCell voronoicell2 = (VVoronoiCell)prevvertex;
              
              int area1 = voronoicell1.getAreaOfCell();
              int area2 = voronoicell2.getAreaOfCell();
              



              if (((area1 <= 80000) || (area2 <= 80000)) && ((area1 >= 0) || (area2 >= 0)))
              {
                if ((area1 > VORONOICELLAREA_CUTOFF) && (area2 <= VORONOICELLAREA_CUTOFF))
                  break;
                if ((area2 > VORONOICELLAREA_CUTOFF) && (area1 <= VORONOICELLAREA_CUTOFF)) {
                  break;
                }
              }
            }
            
            isdeleted = true;
            

            VHalfEdge tmpotheredge = prevvertex.getNextConnectedEdge(vertexnumber);
            isdeleted = true;
            

            break;
          }
        }
      }
    }
    

    if (VORONOICELLAREA_CUTOFF > 0) {
      boolean haschanged;
      label449:
      do { haschanged = false;
        for (vertex = vertexpoints.iterator(); vertex.hasNext(); 
            

            prevvertex.hasNext())
        {
          VPoint point = (VPoint)vertex.next();
          VVertex vertex = (VVertex)point;
          if ((vertex == null) || (vertex.getConnectedVertexs() == null)) break label449;
          prevvertex = vertex.getConnectedVertexs().iterator(); continue;VHalfEdge connectededge = (VHalfEdge)prevvertex.next();
          
          if (!isdeleted)
          {



            if (!checkHasConnections(vertex, vertex))
            {
              haschanged = true;
              

              isdeleted = true;
              

              VHalfEdge tmpotheredge = vertex.getNextConnectedEdge(vertex);
              isdeleted = true;
            }
          }
        }
      } while (
      























        haschanged);
    }
  }
  
  private boolean checkHasConnections(VVertex vertex, VVertex ignore) {
    for (VHalfEdge halfedge : vertex.getConnectedVertexs()) {
      if ((!isdeleted) && (vertex != ignore)) {
        return true;
      }
    }
    return false;
  }
  
  private VVertex getPreviousVertex(int vertexnumber, VVertex currpoint, VVertex point) {
    VVertex prevpoint = null;
    while (currpoint != point) {
      prevpoint = currpoint;
      currpoint = currpoint.getNextConnectedVertex(vertexnumber);
      if (currpoint == null)
      {
        return null;
      }
    }
    
    return prevpoint;
  }
  


  public void paint(Graphics2D g)
  {
    Iterator localIterator2;
    

    label122:
    
    for (Iterator localIterator1 = vertexpoints.iterator(); localIterator1.hasNext(); 
        

        localIterator2.hasNext())
    {
      VPoint point = (VPoint)localIterator1.next();
      VVertex vertex = (VVertex)point;
      if ((vertex == null) || (vertex.getConnectedVertexs() == null)) break label122;
      localIterator2 = vertex.getConnectedVertexs().iterator(); continue;VHalfEdge edge = (VHalfEdge)localIterator2.next();
      
      if (!isdeleted)
      {



        VVertex nextvertex = vertex;
        g.drawLine(x, y, x, y);
      }
    }
  }
}
