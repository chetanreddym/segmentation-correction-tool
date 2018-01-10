package signalprocesser.voronoi.statusstructure;

import java.util.ArrayList;
import signalprocesser.voronoi.VPoint;
import signalprocesser.voronoi.VoronoiShared;
import signalprocesser.voronoi.eventqueue.EventQueue;
import signalprocesser.voronoi.eventqueue.VCircleEvent;
import signalprocesser.voronoi.eventqueue.VSiteEvent;





public class VLinkedNode
{
  public VSiteEvent siteevent;
  private VLinkedNode prev;
  private VLinkedNode next;
  private ArrayList<VCircleEvent> circleevents;
  
  protected VLinkedNode() {}
  
  public VLinkedNode(VSiteEvent _siteevent)
  {
    siteevent = _siteevent;
  }
  




  public boolean hasCircleEvents() { return (circleevents != null) && (circleevents.size() > 0); }
  
  public ArrayList<VCircleEvent> getCircleEvents() { return circleevents; }
  
  public void removeCircleEvents(EventQueue eventqueue) { if (circleevents == null) { return;
    }
    for (VCircleEvent circleevent : circleevents)
    {
      leafnode = null;
      

      boolean bool = eventqueue.removeEvent(circleevent);
    }
    




    circleevents = null;
  }
  
  public void addCircleEvent(EventQueue eventqueue) {
    if ((prev != null) && (next != null)) {
      VCircleEvent circleevent = VoronoiShared.calculateCenter(prev.siteevent, siteevent, next.siteevent);
      if (circleevent != null) {
        addCircleEvent(circleevent);
        leafnode = this;
        eventqueue.addEvent(circleevent);
      }
    }
  }
  
  private void addCircleEvent(VCircleEvent _circleevent) { if (circleevents == null) {
      circleevents = new ArrayList();
    }
    circleevents.add(_circleevent);
  }
  
  public VLinkedNode getPrev() { return prev; }
  public VLinkedNode getNext() { return next; }
  
  public void setNext(VLinkedNode node)
  {
    if (next != null) {
      next.prev = null;
    }
    next = node;
    

    if (node != null) {
      if (prev != null) {
        prev.next = null;
      }
      
      prev = this;
    }
  }
  
  public VPoint getIntersectWithNext(int sweepline) {
    VSiteEvent v1 = siteevent;
    VSiteEvent v2 = next.siteevent;
    

    v1.calcParabolaConstants(sweepline);
    v2.calcParabolaConstants(sweepline);
    

    double[] intersects = VoronoiShared.solveQuadratic(a - a, b - b, c - c);
    return new VPoint((int)intersects[0], sweepline + siteevent.getYValueOfParabola(intersects[0]));
  }
  
  public boolean isLeafNode() { return true; }
  
  public boolean isInternalNode() { return false; }
  
  public VLinkedNode cloneLinkedNode() {
    VLinkedNode clone = new VLinkedNode(siteevent);
    

    return clone;
  }
}
