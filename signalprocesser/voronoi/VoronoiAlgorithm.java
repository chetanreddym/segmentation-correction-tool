package signalprocesser.voronoi;

import signalprocesser.voronoi.eventqueue.EventQueue;
import signalprocesser.voronoi.eventqueue.VCircleEvent;
import signalprocesser.voronoi.eventqueue.VEvent;
import signalprocesser.voronoi.eventqueue.VSiteEvent;
import signalprocesser.voronoi.representation.RepresentationInterface;
import signalprocesser.voronoi.statusstructure.AbstractStatusStructure;
import signalprocesser.voronoi.statusstructure.VLinkedNode;

public class VoronoiAlgorithm
{
  public static void main(String[] args)
  {
    VoronoiTest.main(args);
  }
  

  private VoronoiAlgorithm() {}
  

  public static void generateVoronoi(RepresentationInterface datainterface, java.util.Collection<VPoint> points)
  {
    generateVoronoi(datainterface, points, null, null, -1);
  }
  
  protected static void generateVoronoi(RepresentationInterface datainterface, java.util.Collection<VPoint> points, java.awt.Graphics2D g, VPoint attentiontopoint, int attentiontopos) {
    EventQueue eventqueue = new EventQueue();
    VSiteEvent attentiontositeevent = null;
    for (VPoint point : points) {
      VSiteEvent newsiteevent = new VSiteEvent(point);
      if (point == attentiontopoint) {
        attentiontositeevent = newsiteevent;
      }
      eventqueue.addEvent(newsiteevent);
    }
    

    datainterface.beginAlgorithm(points);
    

    VEvent.uniqueid = 1;
    signalprocesser.voronoi.statusstructure.binarysearchtreeimpl.BSTStatusStructure.uniqueid = 1;
    

    AbstractStatusStructure statusstructure = AbstractStatusStructure.createDefaultStatusStructure();
    

    VEvent event = null;
    boolean printcalled = false;
    while (!eventqueue.isEventQueueEmpty())
    {
      event = eventqueue.getAndRemoveFirstEvent();
      

      if ((g != null) && (attentiontositeevent == null) && (attentiontopos >= 0) && (!printcalled) && 
        (event != null) && (event.getY() >= attentiontopos)) {
        printcalled = true;
        statusstructure.print(g, null, attentiontopos);
        

        datainterface.endAlgorithm(points, event.getY(), statusstructure.getHeadNode());
        return;
      }
      


      if (event.isSiteEvent()) {
        VSiteEvent siteevent = (VSiteEvent)event;
        

        if ((g != null) && (siteevent == attentiontositeevent)) {
          statusstructure.print(g, siteevent, siteevent.getY());
          

          datainterface.endAlgorithm(points, event.getY(), statusstructure.getHeadNode());
          return;
        }
        


        if (statusstructure.isStatusStructureEmpty())
        {
          statusstructure.setRootNode(siteevent);
          


          VEvent nextevent = eventqueue.getFirstEvent();
          if ((nextevent != null) && (event.getY() == nextevent.getY()))
          {

            getPointy -= 1;


          }
          



        }
        else
        {



          VLinkedNode leafabove = statusstructure.getNodeAboveSiteEvent(siteevent, siteevent.getY());
          

          leafabove.removeCircleEvents(eventqueue);
          

          VLinkedNode newnode = statusstructure.insertNode(leafabove, siteevent);
          VLinkedNode prevnode = newnode.getPrev();
          VLinkedNode nextnode = newnode.getNext();
          

          if (prevnode != null) prevnode.addCircleEvent(eventqueue);
          if (nextnode != null) { nextnode.addCircleEvent(eventqueue);
          }
          
          datainterface.siteEvent(
            prevnode, 
            newnode, 
            nextnode);
        } } else if (event.isCircleEvent()) {
        VCircleEvent circleevent = (VCircleEvent)event;
        

        VLinkedNode currnode = leafnode;
        VLinkedNode prevnode = currnode.getPrev();
        VLinkedNode nextnode = currnode.getNext();
        

        datainterface.circleEvent(
          prevnode, 
          currnode, 
          nextnode, 
          circleevent.getX(), circleevent.getCenterY());
        


        currnode.removeCircleEvents(eventqueue);
        

        statusstructure.removeNode(eventqueue, currnode);
        

        prevnode.removeCircleEvents(eventqueue);
        nextnode.removeCircleEvents(eventqueue);
        

        if (prevnode != null) prevnode.addCircleEvent(eventqueue);
        if (nextnode != null) nextnode.addCircleEvent(eventqueue);
      } else {
        throw new RuntimeException("Unknown event; " + event.getClass().getName());
      }
    }
    

    if ((g != null) && (attentiontositeevent == null) && (attentiontopos >= 0) && (!printcalled)) {
      printcalled = true;
      statusstructure.print(g, null, attentiontopos);
    }
    

    if (event == null) {
      datainterface.endAlgorithm(points, -1, statusstructure.getHeadNode());
    } else {
      datainterface.endAlgorithm(points, event.getY(), statusstructure.getHeadNode());
    }
  }
}
