package javax.media.jai;

import java.awt.RenderingHints;
import java.awt.image.renderable.ParameterBlock;

public abstract interface CollectionImageFactory
{
  public abstract CollectionImage create(ParameterBlock paramParameterBlock, RenderingHints paramRenderingHints);
  
  public abstract CollectionImage update(ParameterBlock paramParameterBlock1, RenderingHints paramRenderingHints1, ParameterBlock paramParameterBlock2, RenderingHints paramRenderingHints2, CollectionImage paramCollectionImage, CollectionOp paramCollectionOp);
}
