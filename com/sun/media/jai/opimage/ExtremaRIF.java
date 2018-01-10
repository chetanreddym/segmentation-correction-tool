package com.sun.media.jai.opimage;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import javax.media.jai.ROI;























public class ExtremaRIF
  implements RenderedImageFactory
{
  public ExtremaRIF() {}
  
  public RenderedImage create(ParameterBlock paramBlock, RenderingHints hints)
  {
    RenderedImage src = paramBlock.getRenderedSource(0);
    
    int xStart = src.getMinX();
    int yStart = src.getMinY();
    
    int maxWidth = src.getWidth();
    int maxHeight = src.getHeight();
    
    return new ExtremaOpImage(src, (ROI)paramBlock.getObjectParameter(0), xStart, yStart, paramBlock.getIntParameter(1), paramBlock.getIntParameter(2), ((Boolean)paramBlock.getObjectParameter(3)).booleanValue(), paramBlock.getIntParameter(4));
  }
}
