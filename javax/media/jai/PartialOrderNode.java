package javax.media.jai;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;





































final class PartialOrderNode
  implements Cloneable, Serializable
{
  protected String name;
  protected Object nodeData;
  protected int inDegree = 0;
  

  protected int copyInDegree = 0;
  

  protected PartialOrderNode zeroLink = null;
  

  Vector neighbors = new Vector();
  




  PartialOrderNode(Object nodeData, String name)
  {
    this.nodeData = nodeData;
    this.name = name;
  }
  
  Object getData()
  {
    return nodeData;
  }
  
  String getName()
  {
    return name;
  }
  
  int getInDegree()
  {
    return inDegree;
  }
  
  int getCopyInDegree()
  {
    return copyInDegree;
  }
  
  void setCopyInDegree(int copyInDegree)
  {
    this.copyInDegree = copyInDegree;
  }
  
  PartialOrderNode getZeroLink()
  {
    return zeroLink;
  }
  
  void setZeroLink(PartialOrderNode poNode)
  {
    zeroLink = poNode;
  }
  
  Enumeration getNeighbors()
  {
    return neighbors.elements();
  }
  



  void addEdge(PartialOrderNode poNode)
  {
    neighbors.addElement(poNode);
    poNode.incrementInDegree();
  }
  



  void removeEdge(PartialOrderNode poNode)
  {
    neighbors.removeElement(poNode);
    poNode.decrementInDegree();
  }
  
  void incrementInDegree()
  {
    inDegree += 1;
  }
  
  void incrementCopyInDegree()
  {
    copyInDegree += 1;
  }
  
  void decrementInDegree()
  {
    inDegree -= 1;
  }
  
  void decrementCopyInDegree()
  {
    copyInDegree -= 1;
  }
}
