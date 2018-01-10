package signalprocesser.voronoi.statusstructure.binarysearchtreeimpl;

import signalprocesser.voronoi.eventqueue.VSiteEvent;
import signalprocesser.voronoi.statusstructure.VLinkedNode;




public class VLeafNode
  extends VLinkedNode
  implements VNode
{
  public int id = BSTStatusStructure.uniqueid++;
  
  protected VInternalNode parent;
  
  protected VLeafNode() {}
  
  public VLeafNode(VSiteEvent _siteevent)
  {
    super(_siteevent);
  }
  



  public VInternalNode getParent() { return parent; }
  
  public void setParent(VInternalNode _parent) { parent = _parent; }
  

  public boolean isLeafNode() { return true; }
  public boolean isInternalNode() { return false; }
  
  public VLeafNode cloneLeafNode() {
    VLeafNode clone = new VLeafNode(siteevent);
    

    return clone;
  }
  
  public VInternalNode getFirstCommonParent(VLeafNode othernode) {
    VInternalNode parent1 = parent;
    VInternalNode parent2 = parent;
    int depth1 = parent.getDepth();
    int depth2 = parent.getDepth();
    

    if (depth1 > depth2) {
      do {
        depth1--;
        parent1 = parent1.getParent();
      } while (depth1 > depth2);
    } else if (depth2 > depth1) {
      do {
        depth2--;
        parent2 = parent2.getParent();
      } while (depth2 > depth1);
    }
    

    while (parent1 != parent2) {
      parent1 = parent1.getParent();
      parent2 = parent2.getParent();
    }
    

    return parent1;
  }
  



  public String toString()
  {
    return "VLeafNode" + id + " (" + siteevent + ")";
  }
}
