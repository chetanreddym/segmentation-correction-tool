package signalprocesser.voronoi.statusstructure;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import signalprocesser.voronoi.VoronoiTest;
import signalprocesser.voronoi.eventqueue.EventQueue;
import signalprocesser.voronoi.eventqueue.VSiteEvent;
import signalprocesser.voronoi.statusstructure.binarysearchtreeimpl.BSTStatusStructure;
import signalprocesser.voronoi.statusstructure.binarysearchtreeimpl.debug.DebugTree;





public abstract class AbstractStatusStructure
{
  public AbstractStatusStructure() {}
  
  public static AbstractStatusStructure createDefaultStatusStructure()
  {
    return new BSTStatusStructure();
  }
  

  public abstract boolean isStatusStructureEmpty();
  

  public abstract void setRootNode(VSiteEvent paramVSiteEvent);
  

  public abstract VLinkedNode insertNode(VLinkedNode paramVLinkedNode, VSiteEvent paramVSiteEvent);
  
  public abstract void removeNode(EventQueue paramEventQueue, VLinkedNode paramVLinkedNode);
  
  public VLinkedNode getNodeAboveSiteEvent(VSiteEvent siteevent, int sweepline)
  {
    return getNodeAboveSiteEvent(siteevent.getX(), sweepline);
  }
  

  public abstract VLinkedNode getNodeAboveSiteEvent(int paramInt1, int paramInt2);
  

  public abstract VLinkedNode getHeadNode();
  
  public void print(Graphics2D g, VSiteEvent siteevent, int sweepline)
  {
    Rectangle bounds = g.getClipBounds();
    

    g.drawLine(x, sweepline, x + width, sweepline);
    

    if (((this instanceof BSTStatusStructure)) && (VoronoiTest.treedialog != null)) {
      VoronoiTest.treedialog.setRootNode(((BSTStatusStructure)this).getRootNode(), sweepline);
    }
    

    if (siteevent != null) {
      if ((this instanceof BSTStatusStructure)) {
        g.drawString(((BSTStatusStructure)this).strDoublyLinkedList(sweepline), 10, 10);
      }
      

      VLinkedNode nodeabovesite = getNodeAboveSiteEvent(siteevent, sweepline);
      if (nodeabovesite != null) {
        g.drawOval(siteevent.getX() - 10, siteevent.getY() - 10, 20, 20);
        int yintersect = sweepline + siteevent.getYValueOfParabola(siteevent.getX());
        g.drawLine(siteevent.getX(), siteevent.getY(), siteevent.getX(), yintersect);
      }
    }
    


    for (int x = x; x < x + width; x++) {
      VLinkedNode leafnode = getNodeAboveSiteEvent(x, sweepline);
      

      if (leafnode != null) {
        siteevent.calcParabolaConstants(sweepline);
        int ycoord = sweepline + siteevent.getYValueOfParabola(x);
        g.fillRect(x, ycoord, 2, 2);
      }
    }
  }
}
