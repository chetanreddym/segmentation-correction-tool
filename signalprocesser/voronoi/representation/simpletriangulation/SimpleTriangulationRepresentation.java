package signalprocesser.voronoi.representation.simpletriangulation;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;
import signalprocesser.voronoi.VPoint;
import signalprocesser.voronoi.eventqueue.VSiteEvent;
import signalprocesser.voronoi.representation.AbstractRepresentation;
import signalprocesser.voronoi.statusstructure.VLinkedNode;




public class SimpleTriangulationRepresentation
  extends AbstractRepresentation
{
  private final ArrayList<VTriangle> triangles = new ArrayList();
  




  public SimpleTriangulationRepresentation() {}
  



  public VPoint createPoint(int x, int y)
  {
    return new VPoint(x, y);
  }
  





  public void beginAlgorithm(Collection<VPoint> points)
  {
    triangles.clear();
  }
  
  public void siteEvent(VLinkedNode n1, VLinkedNode n2, VLinkedNode n3) {}
  
  public void circleEvent(VLinkedNode n1, VLinkedNode n2, VLinkedNode n3, int circle_x, int circle_y) {
    VTriangle triangle = new VTriangle(circle_x, circle_y);
    p1 = siteevent.getPoint();
    p2 = siteevent.getPoint();
    p3 = siteevent.getPoint();
    triangles.add(triangle);
  }
  



  public void endAlgorithm(Collection<VPoint> points, int lastsweeplineposition, VLinkedNode headnode) {}
  


  public void paint(Graphics2D g)
  {
    for (VTriangle triangle : triangles) {
      g.drawLine(p1.x, p1.y, p2.x, p2.y);
      g.drawLine(p2.x, p2.y, p3.x, p3.y);
      g.drawLine(p3.x, p3.y, p1.x, p1.y);
    }
  }
}
