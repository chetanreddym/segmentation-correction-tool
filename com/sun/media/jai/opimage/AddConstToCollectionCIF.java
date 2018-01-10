package com.sun.media.jai.opimage;

import java.awt.RenderingHints;
import java.awt.image.renderable.ParameterBlock;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.media.jai.CollectionImage;
import javax.media.jai.CollectionImageFactory;
import javax.media.jai.CollectionOp;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;




























public class AddConstToCollectionCIF
  implements CollectionImageFactory
{
  public AddConstToCollectionCIF() {}
  
  public CollectionImage create(ParameterBlock args, RenderingHints hints)
  {
    return new AddConstToCollectionOpImage((Collection)args.getSource(0), hints, (double[])args.getObjectParameter(0));
  }
  










  public CollectionImage update(ParameterBlock oldParamBlock, RenderingHints oldHints, ParameterBlock newParamBlock, RenderingHints newHints, CollectionImage oldRendering, CollectionOp op)
  {
    CollectionImage updatedCollection = null;
    
    if ((oldParamBlock.getObjectParameter(0).equals(newParamBlock.getObjectParameter(0))) && (oldHints == null ? newHints == null : oldHints.equals(newHints)))
    {


      Collection oldSource = (Collection)oldParamBlock.getSource(0);
      Collection newSource = (Collection)newParamBlock.getSource(0);
      double[] constants = (double[])oldParamBlock.getObjectParameter(0);
      

      Collection commonSources = new ArrayList();
      Iterator it = oldSource.iterator();
      while (it.hasNext()) {
        Object oldElement = it.next();
        if (newSource.contains(oldElement)) {
          commonSources.add(oldElement);
        }
      }
      
      if (commonSources.size() != 0)
      {

        ArrayList commonNodes = new ArrayList(commonSources.size());
        it = oldRendering.iterator();
        while (it.hasNext()) {
          RenderedOp node = (RenderedOp)it.next();
          PlanarImage source = node.getSourceImage(0);
          if (commonSources.contains(source)) {
            commonNodes.add(node);
          }
        }
        

        updatedCollection = new AddConstToCollectionOpImage(newSource, newHints, constants);
        




        ArrayList newNodes = new ArrayList(oldRendering.size() - commonSources.size());
        
        it = updatedCollection.iterator();
        while (it.hasNext()) {
          RenderedOp node = (RenderedOp)it.next();
          PlanarImage source = node.getSourceImage(0);
          if (commonSources.contains(source)) {
            it.remove();
          }
        }
        

        it = commonNodes.iterator();
        while (it.hasNext()) {
          updatedCollection.add(it.next());
        }
      }
    }
    
    return updatedCollection;
  }
}
