package javax.media.jai;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;





































class OperationGraph
  implements Serializable
{
  Vector operations = new Vector();
  

  Vector orderedOperations;
  

  boolean isChanged = true;
  




  private boolean lookupByName = false;
  







  OperationGraph() {}
  






  OperationGraph(boolean lookupByName)
  {
    this.lookupByName = lookupByName;
  }
  


  private boolean compare(PartialOrderNode poNode, Object op)
  {
    if (lookupByName) {
      return poNode.getName().equalsIgnoreCase((String)op);
    }
    return poNode.getData() == op;
  }
  



  void addOp(PartialOrderNode poNode)
  {
    operations.addElement(poNode);
    isChanged = true;
  }
  







  synchronized boolean removeOp(Object op)
  {
    boolean retval = false;
    
    PartialOrderNode poNode = lookupOp(op);
    
    if (poNode != null) {
      retval = operations.removeElement(poNode);
      
      if (retval) {
        isChanged = true;
      }
    }
    return retval;
  }
  




  PartialOrderNode lookupOp(Object op)
  {
    int num = operations.size();
    
    for (int i = 0; i < num; i++) {
      PartialOrderNode poNode = (PartialOrderNode)operations.elementAt(i);
      
      if (compare(poNode, op)) {
        PartialOrderNode tempNode = poNode;
        return tempNode;
      }
    }
    
    return null;
  }
  

  synchronized boolean setPreference(Object preferred, Object other)
  {
    boolean retval = false;
    
    if ((preferred == null) || (other == null)) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    

    if (preferred == other) {
      return retval;
    }
    PartialOrderNode preferredPONode = lookupOp(preferred);
    PartialOrderNode otherPONode = lookupOp(other);
    
    if ((preferredPONode != null) && (otherPONode != null)) {
      preferredPONode.addEdge(otherPONode);
      
      retval = true;
      isChanged = true;
    }
    
    return retval;
  }
  

  synchronized boolean unsetPreference(Object preferred, Object other)
  {
    boolean retval = false;
    
    if ((preferred == null) || (other == null)) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    

    if (preferred == other) {
      return retval;
    }
    PartialOrderNode preferredPONode = lookupOp(preferred);
    PartialOrderNode otherPONode = lookupOp(other);
    
    if ((preferredPONode != null) && (otherPONode != null)) {
      preferredPONode.removeEdge(otherPONode);
      
      retval = true;
      isChanged = true;
    }
    
    return retval;
  }
  





  public synchronized Vector getOrderedOperationList()
  {
    if (!isChanged)
    {
      Vector ordered = orderedOperations;
      return ordered;
    }
    
    int num = operations.size();
    for (int i = 0; i < num; i++) {
      PartialOrderNode pon = (PartialOrderNode)operations.elementAt(i);
      pon.setCopyInDegree(pon.getInDegree());
    }
    


    orderedOperations = new Vector(num);
    isChanged = false;
    

    PartialOrderNode zeroList = null;
    



    for (int i = 0; i < num; i++) {
      PartialOrderNode poNode = (PartialOrderNode)operations.elementAt(i);
      if (poNode.getCopyInDegree() == 0) {
        poNode.setZeroLink(zeroList);
        zeroList = poNode;
      }
    }
    

    for (i = 0; i < num; i++)
    {
      if (zeroList == null) {
        orderedOperations = null;
        return null;
      }
      


      PartialOrderNode firstNode = zeroList;
      
      orderedOperations.addElement(firstNode);
      

      zeroList = zeroList.getZeroLink();
      
      Enumeration neighbors = firstNode.getNeighbors();
      while (neighbors.hasMoreElements()) {
        PartialOrderNode poNode = (PartialOrderNode)neighbors.nextElement();
        poNode.decrementCopyInDegree();
        


        if (poNode.getCopyInDegree() == 0) {
          poNode.setZeroLink(zeroList);
          zeroList = poNode;
        }
      }
    }
    
    Vector ordered = orderedOperations;
    return ordered;
  }
}
