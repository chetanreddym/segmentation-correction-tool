package signalprocesser.voronoi.representation.voronoicell;

import java.awt.Graphics2D;
import java.util.Collection;
import signalprocesser.voronoi.VPoint;
import signalprocesser.voronoi.eventqueue.VSiteEvent;
import signalprocesser.voronoi.representation.AbstractRepresentation;
import signalprocesser.voronoi.statusstructure.VLinkedNode;
import signalprocesser.voronoi.statusstructure.binarysearchtreeimpl.VInternalNode;
import signalprocesser.voronoi.statusstructure.binarysearchtreeimpl.VLeafNode;













public class VoronoiCellRepresentation
  extends AbstractRepresentation
{
  private Collection<VPoint> voronoicells;
  
  public VoronoiCellRepresentation() {}
  
  public VPoint createPoint(int x, int y)
  {
    return new VVoronoiCell(x, y);
  }
  





  public void beginAlgorithm(Collection<VPoint> points)
  {
    voronoicells = points;
    

    for (VPoint point : points) {
      VVoronoiCell voronoicell = (VVoronoiCell)point;
      halfedge = null;
      voronoicell.resetArea();
    }
  }
  
  public void siteEvent(VLinkedNode n1, VLinkedNode n2, VLinkedNode n3)
  {
    VLeafNode leaf1 = (VLeafNode)n1;
    VLeafNode leaf2 = (VLeafNode)n2;
    VLeafNode leaf3 = (VLeafNode)n3;
    
    VInternalNode parent1 = leaf1.getFirstCommonParent(leaf2);
    VInternalNode parent2 = leaf2.getFirstCommonParent(leaf3);
    
    halfedge_in = new VHalfEdge();
    halfedge_out = new VHalfEdge();
    halfedge_in = halfedge_out;
    halfedge_out = halfedge_in;
  }
  

  public void circleEvent(VLinkedNode n1, VLinkedNode n2, VLinkedNode n3, int circle_x, int circle_y)
  {
    VLeafNode leaf1 = (VLeafNode)n1;
    VLeafNode leaf2 = (VLeafNode)n2;
    VLeafNode leaf3 = (VLeafNode)n3;
    
    VInternalNode left = leaf1.getFirstCommonParent(leaf2);
    VInternalNode right = leaf2.getFirstCommonParent(leaf3);
    VInternalNode down = leaf1.getFirstCommonParent(leaf3);
    
    VHalfEdge left_in = (VHalfEdge)halfedge_in;
    VHalfEdge left_out = (VHalfEdge)halfedge_out;
    VHalfEdge right_in = (VHalfEdge)halfedge_in;
    VHalfEdge right_out = (VHalfEdge)halfedge_out;
    VHalfEdge down_in = new VHalfEdge(circle_x, circle_y);
    VHalfEdge down_out = new VHalfEdge();
    
    halfedge_in = down_in;
    halfedge_out = down_out;
    
    if (left_in != null) {
      left_in.setNext(down_in);
      left_out.setXY(circle_x, circle_y);
    }
    if (right_in != null) {
      down_out.setNext(right_out);
      right_out.setXY(circle_x, circle_y);
    }
    if ((left_in != null) && (right_in != null)) {
      right_in.setNext(left_out);
    }
    
    VVoronoiCell v1 = (VVoronoiCell)siteevent.getPoint();
    VVoronoiCell v2 = (VVoronoiCell)siteevent.getPoint();
    VVoronoiCell v3 = (VVoronoiCell)siteevent.getPoint();
    if (halfedge == null) halfedge = left_in;
    if (halfedge == null) halfedge = right_in;
    if (halfedge == null) { halfedge = down_out;
    }
  }
  




  public void endAlgorithm(Collection<VPoint> points, int lastsweeplineposition, VLinkedNode headnode) {}
  



  public void paint(Graphics2D g)
  {
    for (VPoint point : voronoicells) {
      VVoronoiCell voronoicell = (VVoronoiCell)point;
      VHalfEdge halfedge = halfedge;
      

      g.drawString(Double.toString(voronoicell.getAreaOfCell()), x + 6, y);
      

      VHalfEdge curr = halfedge;
      if ((halfedge != null) && (halfedge.getNext() != null)) {
        do {
          if (((x == -1) && (y == -1)) || ((getNextx == -1) && (getNexty == -1))) {
            curr = curr.getNext();

          }
          else
          {
            g.drawLine(x, y, getNextx, getNexty);
            

            curr = curr.getNext();
          } } while ((curr.getNext() != null) && (curr != halfedge));
      }
    }
  }
}
