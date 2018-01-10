package signalprocesser.voronoi.statusstructure.binarysearchtreeimpl;

import java.io.PrintStream;
import signalprocesser.voronoi.eventqueue.VSiteEvent;




public class VInternalNode
  implements VNode
{
  public int id = BSTStatusStructure.uniqueid++;
  
  protected VInternalNode parent;
  private int depth = 1;
  
  private VNode left;
  
  private VNode right;
  
  public VSiteEvent v1;
  
  public VSiteEvent v2;
  
  public Object halfedge_in;
  
  public Object halfedge_out;
  
  public VInternalNode() {}
  
  public VInternalNode(VNode _left, VNode _right)
  {
    System.out.println("CREATED :: " + this);
    setLeft(_left);
    setRight(_right);
  }
  



  public VInternalNode getParent() { return parent; }
  
  public void setParent(VInternalNode _parent) { parent = _parent; }
  

  public int getDepth() { return depth; }
  
  public boolean isLeafNode() { return false; }
  public boolean isInternalNode() { return true; }
  
  public VNode getLeft() { return left; }
  
  public void setLeft(VNode _left) {
    if (left != null) {
      left.setParent(null);
    }
    

    left = _left;
    _left.setParent(this);
    

    if ((_left instanceof VInternalNode)) {
      correctDepthValues(depth + 1, (VInternalNode)_left);
    }
  }
  
  public VNode getRight() { return right; }
  
  public void setRight(VNode _right) {
    if (right != null) {
      right.setParent(null);
    }
    

    right = _right;
    _right.setParent(this);
    

    if ((_right instanceof VInternalNode)) {
      correctDepthValues(depth + 1, (VInternalNode)_right);
    }
  }
  
  public void setDepthForRootNode() {
    correctDepthValues(1, this);
  }
  
  private void correctDepthValues(int depth, VInternalNode internalnode)
  {
    depth = depth;
    if ((left instanceof VInternalNode)) {
      correctDepthValues(depth + 1, (VInternalNode)left);
    }
    if ((right instanceof VInternalNode)) {
      correctDepthValues(depth + 1, (VInternalNode)right);
    }
  }
  
  public void setSiteEvents(VSiteEvent _siteevent_left, VSiteEvent _siteevent_right) {
    v1 = _siteevent_left;
    v2 = _siteevent_right;
  }
  


  public String toString()
  {
    return "VInternalNode" + id + " (" + v1.getX() + "," + v1.getY() + "), (" + v2.getX() + "," + v2.getY() + ")";
  }
}
