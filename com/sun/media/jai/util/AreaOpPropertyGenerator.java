package com.sun.media.jai.util;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.AreaOpImage;
import javax.media.jai.PlanarImage;
import javax.media.jai.ROI;
import javax.media.jai.ROIShape;
import javax.media.jai.RenderedOp;










public class AreaOpPropertyGenerator
  extends PropertyGeneratorImpl
{
  public AreaOpPropertyGenerator()
  {
    super(new String[] { "ROI" }, new Class[] { ROI.class }, new Class[] { RenderedOp.class });
  }
  







  public Object getProperty(String name, Object opNode)
  {
    validate(name, opNode);
    
    if (((opNode instanceof RenderedOp)) && (name.equalsIgnoreCase("roi")))
    {
      RenderedOp op = (RenderedOp)opNode;
      
      ParameterBlock pb = op.getParameterBlock();
      

      PlanarImage src = (PlanarImage)pb.getRenderedSource(0);
      Object roiProperty = src.getProperty("ROI");
      if ((roiProperty == null) || (roiProperty == Image.UndefinedProperty) || (!(roiProperty instanceof ROI)))
      {

        return Image.UndefinedProperty;
      }
      ROI roi = (ROI)roiProperty;
      

      Rectangle dstBounds = null;
      PlanarImage dst = op.getRendering();
      if (((dst instanceof AreaOpImage)) && (((AreaOpImage)dst).getBorderExtender() == null))
      {
        AreaOpImage aoi = (AreaOpImage)dst;
        dstBounds = new Rectangle(aoi.getMinX() + aoi.getLeftPadding(), aoi.getMinY() + aoi.getTopPadding(), aoi.getWidth() - aoi.getLeftPadding() - aoi.getRightPadding(), aoi.getHeight() - aoi.getTopPadding() - aoi.getBottomPadding());



      }
      else
      {



        dstBounds = dst.getBounds();
      }
      


      if (!dstBounds.contains(roi.getBounds())) {
        roi = roi.intersect(new ROIShape(dstBounds));
      }
      
      return roi;
    }
    
    return Image.UndefinedProperty;
  }
}
