package com.sun.media.jai.opimage;

import java.awt.RenderingHints;
import java.awt.image.renderable.ParameterBlock;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import javax.media.jai.CollectionImage;
import javax.media.jai.JAI;
































final class AddConstToCollectionOpImage
  extends CollectionImage
{
  public AddConstToCollectionOpImage(Collection sourceCollection, RenderingHints hints, double[] constants)
  {
    try
    {
      imageCollection = ((Collection)sourceCollection.getClass().newInstance());
    }
    catch (Exception e) {
      imageCollection = new Vector();
    }
    
    Iterator iter = sourceCollection.iterator();
    while (iter.hasNext()) {
      ParameterBlock pb = new ParameterBlock();
      pb.addSource(iter.next());
      pb.add(constants);
      
      imageCollection.add(JAI.create("AddConst", pb, hints));
    }
  }
}
