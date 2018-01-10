package javax.media.jai;

import java.io.Serializable;






























final class ProductOperationGraph
  extends OperationGraph
  implements Serializable
{
  ProductOperationGraph()
  {
    super(true);
  }
  




  void addProduct(String productName)
  {
    addOp(new PartialOrderNode(new OperationGraph(), productName));
  }
}
