package signalprocesser.voronoi.representation;

import java.util.Collection;
import signalprocesser.voronoi.VPoint;
import signalprocesser.voronoi.statusstructure.VLinkedNode;

public abstract interface RepresentationInterface
{
  public abstract void beginAlgorithm(Collection<VPoint> paramCollection);
  
  public abstract void siteEvent(VLinkedNode paramVLinkedNode1, VLinkedNode paramVLinkedNode2, VLinkedNode paramVLinkedNode3);
  
  public abstract void circleEvent(VLinkedNode paramVLinkedNode1, VLinkedNode paramVLinkedNode2, VLinkedNode paramVLinkedNode3, int paramInt1, int paramInt2);
  
  public abstract void endAlgorithm(Collection<VPoint> paramCollection, int paramInt, VLinkedNode paramVLinkedNode);
}
