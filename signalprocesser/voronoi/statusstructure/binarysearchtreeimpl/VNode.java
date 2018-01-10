package signalprocesser.voronoi.statusstructure.binarysearchtreeimpl;

public abstract interface VNode
{
  public abstract void setParent(VInternalNode paramVInternalNode);
  
  public abstract VInternalNode getParent();
  
  public abstract boolean isLeafNode();
  
  public abstract boolean isInternalNode();
}
