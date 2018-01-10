package signalprocesser.voronoi.statusstructure.doublelinkedlistimpl;

import signalprocesser.voronoi.VoronoiShared;
import signalprocesser.voronoi.eventqueue.EventQueue;
import signalprocesser.voronoi.eventqueue.VSiteEvent;
import signalprocesser.voronoi.statusstructure.AbstractStatusStructure;
import signalprocesser.voronoi.statusstructure.VLinkedNode;










public class DLinkedListStatusStructure
  extends AbstractStatusStructure
{
  private VLinkedNode head = null;
  
  public DLinkedListStatusStructure() {}
  
  public boolean isStatusStructureEmpty()
  {
    return head == null;
  }
  
  public void setRootNode(VSiteEvent siteevent) {
    setRootNode(new VLinkedNode(siteevent));
  }
  
  protected void setRootNode(VLinkedNode node) { head = node; }
  
  public VLinkedNode insertNode(VLinkedNode nodetosplit, VSiteEvent siteevent)
  {
    VLinkedNode newnode = new VLinkedNode(siteevent);
    

    VLinkedNode leaf1 = nodetosplit;
    VLinkedNode leaf3 = nodetosplit.cloneLinkedNode();
    VLinkedNode tmp = nodetosplit.getNext();
    

    leaf1.setNext(newnode);
    newnode.setNext(leaf3);
    leaf3.setNext(tmp);
    

    return newnode;
  }
  
  public void removeNode(EventQueue eventqueue, VLinkedNode toremove)
  {
    if (toremove.getPrev() == null) {
      toremove.setNext(null);
    } else {
      toremove.getPrev().setNext(toremove.getNext());
    }
  }
  
  public VLinkedNode getNodeAboveSiteEvent(int siteevent_x, int sweepline) {
    if (head == null) { return null;
    }
    
    VLinkedNode curr = head;
    
    while (curr.getNext() != null) {
      VSiteEvent v1 = head.siteevent;
      VSiteEvent v2 = head.getNext().siteevent;
      


      v1.calcParabolaConstants(sweepline);
      v2.calcParabolaConstants(sweepline);
      

      double[] intersects = VoronoiShared.solveQuadratic(a - a, b - b, c - c);
      
      if ((intersects[0] > siteevent_x) || (intersects[0] == intersects[1])) {
        return curr;
      }
      

      curr = curr.getNext();
    }
    
    return curr;
  }
  
  public VLinkedNode getHeadNode()
  {
    return head;
  }
  


  public String toString()
  {
    VLinkedNode node = getHeadNode();
    if (node == null) {
      return "| Doubly-linked list is empty";
    }
    StringBuffer buffer = new StringBuffer();
    buffer.append("| ");
    boolean isfirst = true;
    do {
      if (isfirst) {
        isfirst = false;
      } else {
        buffer.append(" -> ");
      }
      buffer.append("Node (" + siteevent.getX() + "," + siteevent.getY() + ") #" + siteevent.getID());
    } while ((node = node.getNext()) != null);
    return buffer.toString();
  }
}
