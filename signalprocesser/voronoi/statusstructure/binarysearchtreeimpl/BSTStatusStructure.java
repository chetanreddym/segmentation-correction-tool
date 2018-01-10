package signalprocesser.voronoi.statusstructure.binarysearchtreeimpl;

import signalprocesser.voronoi.VoronoiShared;
import signalprocesser.voronoi.eventqueue.EventQueue;
import signalprocesser.voronoi.eventqueue.VSiteEvent;
import signalprocesser.voronoi.statusstructure.AbstractStatusStructure;
import signalprocesser.voronoi.statusstructure.VLinkedNode;







public class BSTStatusStructure
  extends AbstractStatusStructure
{
  public static int uniqueid = 1;
  
  private VNode rootnode = null;
  
  public BSTStatusStructure() {}
  
  public boolean isStatusStructureEmpty()
  {
    return rootnode == null;
  }
  
  public VNode getRootNode() { return rootnode; }
  
  public void setRootNode(VSiteEvent siteevent) { setRootNode(new VLeafNode(siteevent)); }
  
  protected void setRootNode(VNode node) {
    rootnode = node;
    rootnode.setParent(null);
    if ((rootnode instanceof VInternalNode)) {
      ((VInternalNode)rootnode).setDepthForRootNode();
    }
  }
  
  public VLinkedNode insertNode(VLinkedNode _nodetosplit, VSiteEvent siteevent) {
    VLeafNode nodetosplit = (VLeafNode)_nodetosplit;
    

    VLeafNode newleaf = new VLeafNode(siteevent);
    VInternalNode top = new VInternalNode();
    VInternalNode bottom = new VInternalNode();
    

    if (nodetosplit.getParent() == null) {
      setRootNode(top);
    }
    else if (nodetosplit.getParent().getLeft() == nodetosplit) {
      nodetosplit.getParent().setLeft(top);
    } else if (nodetosplit.getParent().getRight() == nodetosplit) {
      nodetosplit.getParent().setRight(top);
    } else {
      throw new RuntimeException("Neither child matched suggested parent for attaching new branch - linking error");
    }
    


    top.setLeft(bottom);
    top.setRight(nodetosplit.cloneLeafNode());
    bottom.setLeft(nodetosplit);
    bottom.setRight(newleaf);
    

    top.setSiteEvents(siteevent, siteevent);
    bottom.setSiteEvents(siteevent, siteevent);
    

    VLeafNode leaf1 = (VLeafNode)bottom.getLeft();
    VLeafNode leaf3 = (VLeafNode)top.getRight();
    VLeafNode tmp = (VLeafNode)nodetosplit.getNext();
    leaf1.setNext(newleaf);
    newleaf.setNext(leaf3);
    leaf3.setNext(tmp);
    
    return newleaf;
  }
  
  public void removeNode(EventQueue eventqueue, VLinkedNode _toremove) {
    VLeafNode toremove = (VLeafNode)_toremove;
    VInternalNode parent = toremove.getParent();
    

    if (toremove.getPrev() == null) {
      toremove.setNext(null);
    } else {
      toremove.getPrev().setNext(toremove.getNext());
    }
    


    if (parent.getLeft() == toremove) {
      VNode tosave = parent.getRight();
      

      VInternalNode ces = getPredecessor(parent);
      v2 = v2;
    } else if (parent.getRight() == toremove) {
      VNode tosave = parent.getLeft();
      

      VInternalNode ces = getSuccessor(parent);
      v1 = v1;
    } else {
      throw new RuntimeException("Neither child matched suggested parent - linking error");
    }
    
    VNode tosave;
    if (parent.getParent() == null)
      throw new RuntimeException("Parent is null - error; parent=#" + id);
    if (parent.getParent().getLeft() == parent) {
      parent.getParent().setLeft(tosave);
    } else if (parent.getParent().getRight() == parent) {
      parent.getParent().setRight(tosave);
    } else {
      throw new RuntimeException("Neither child matched suggested parent's parent - linking error");
    }
  }
  
  private VInternalNode getSuccessor(VInternalNode x) {
    VInternalNode y = x.getParent();
    while ((y != null) && (x == y.getRight())) {
      x = y;
      y = y.getParent();
    }
    return y;
  }
  
  private VInternalNode getPredecessor(VInternalNode x) {
    VInternalNode y = x.getParent();
    while ((y != null) && (x == y.getLeft())) {
      x = y;
      y = y.getParent();
    }
    return y;
  }
  
  public VLinkedNode getNodeAboveSiteEvent(int siteevent_x, int sweepline) {
    if (rootnode == null)
      return null;
    if (rootnode.isLeafNode()) {
      return (VLeafNode)rootnode;
    }
    VInternalNode internalnode = (VInternalNode)rootnode;
    for (;;) {
      VSiteEvent v1 = v1;
      VSiteEvent v2 = v2;
      

      v1.calcParabolaConstants(sweepline);
      v2.calcParabolaConstants(sweepline);
      

      double[] intersects = VoronoiShared.solveQuadratic(a - a, b - b, c - c);
      
      VNode currnode;
      VNode currnode;
      if (siteevent_x <= intersects[0]) {
        currnode = internalnode.getLeft();
      } else {
        currnode = internalnode.getRight();
      }
      

      if (currnode.isLeafNode()) {
        return (VLeafNode)currnode;
      }
      

      internalnode = (VInternalNode)currnode;
    }
  }
  

  public VLinkedNode getHeadNode()
  {
    if (rootnode == null)
      return null;
    if (rootnode.isLeafNode()) {
      return (VLinkedNode)rootnode;
    }
    VInternalNode internalnode = (VInternalNode)rootnode;
    VNode currnode;
    for (;;) {
      currnode = internalnode.getLeft();
      

      if (currnode.isLeafNode()) {
        break;
      }
      

      internalnode = (VInternalNode)currnode;
    }
    

    VLeafNode leafnode = (VLeafNode)currnode;
    if (leafnode.getPrev() != null) {
      throw new RuntimeException("Leftmost element of tree is not leftmost element of doubly-linked list - linking error");
    }
    

    return leafnode;
  }
  





  public String toString() { return "| " + strDoublyLinkedList(-1) + "\n* " + strTreeStructure(rootnode, 1); }
  
  public String strDoublyLinkedList(int sweepline) {
    VLeafNode leafnode = (VLeafNode)getHeadNode();
    if (leafnode == null) {
      return "Doubly-linked list is empty";
    }
    StringBuffer buffer = new StringBuffer();
    boolean isfirst = true;
    do {
      if (isfirst) {
        isfirst = false;
      } else {
        buffer.append(" ");
        if (sweepline > 0) {
          VSiteEvent v1 = getPrevsiteevent;
          VSiteEvent v2 = siteevent;
          
          v1.calcParabolaConstants(sweepline);
          v2.calcParabolaConstants(sweepline);
          

          double[] intersects = VoronoiShared.solveQuadratic(a - a, b - b, c - c);
          buffer.append("[" + (int)intersects[0] + "]");
        }
        buffer.append("-> ");
      }
      buffer.append("Leaf (" + siteevent.getX() + "," + siteevent.getY() + ") #" + id + "/" + siteevent.getID());
    } while ((leafnode = (VLeafNode)leafnode.getNext()) != null);
    return buffer.toString();
  }
  
  private String strTreeStructure(VNode node, int depth) {
    if (node == null)
      return "Tree is empty (null root)";
    if ((node instanceof VLeafNode)) {
      VLeafNode leafnode = (VLeafNode)node;
      return "Leaf #" + id + "/" + siteevent.getID() + " (" + siteevent.getX() + "," + siteevent.getY() + ") (parent=" + (parent == null ? "null" : Integer.valueOf(parent.id)) + ",prev=" + (leafnode.getPrev() == null ? "null" : new StringBuilder(String.valueOf(getPrevid)).append("/").append(getPrevsiteevent.getID()).toString()) + ",next=" + (leafnode.getNext() == null ? "null" : new StringBuilder(String.valueOf(getNextid)).append("/").append(getNextsiteevent.getID()).toString()) + ")"; }
    if ((node instanceof VInternalNode)) {
      VInternalNode internalnode = (VInternalNode)node;
      return "Node #" + id + "(v1=" + v1.getID() + ",v2=" + v2.getID() + ") (parent=" + (parent == null ? "null" : Integer.valueOf(parent.id)) + "):\n" + 
        printGap(depth) + "* Left " + strTreeStructure(internalnode.getLeft(), depth + 1) + "\n" + 
        printGap(depth) + "* Right " + strTreeStructure(internalnode.getRight(), depth + 1);
    }
    throw new RuntimeException("Unknown node type; " + node.getClass().getName());
  }
  
  private String printGap(int gap) {
    String tmp = "";
    for (int x = 0; x < gap; x++) {
      tmp = tmp + "  ";
    }
    return tmp;
  }
}
