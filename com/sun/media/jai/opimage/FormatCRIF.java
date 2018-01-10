package com.sun.media.jai.opimage;

import com.sun.media.jai.util.JDKWorkarounds;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.CRIFImpl;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.NullOpImage;
import javax.media.jai.RasterFactory;






















public class FormatCRIF
  extends CRIFImpl
{
  public FormatCRIF()
  {
    super("format");
  }
  









  public RenderedImage create(ParameterBlock args, RenderingHints renderHints)
  {
    RenderedImage src = args.getRenderedSource(0);
    Integer datatype = (Integer)args.getObjectParameter(0);
    int type = datatype.intValue();
    

    ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
    

    if ((layout == null) && (type == src.getSampleModel().getDataType())) {
      return src;
    }
    

    if (layout == null) {
      layout = new ImageLayout(src);
    } else {
      layout = (ImageLayout)layout.clone();
    }
    
    boolean isDataTypeChange = false;
    

    SampleModel sampleModel = layout.getSampleModel(src);
    

    if (sampleModel.getDataType() != type) {
      int tileWidth = layout.getTileWidth(src);
      int tileHeight = layout.getTileHeight(src);
      int numBands = src.getSampleModel().getNumBands();
      
      SampleModel csm = RasterFactory.createComponentSampleModel(sampleModel, type, tileWidth, tileHeight, numBands);
      





      layout.setSampleModel(csm);
      isDataTypeChange = true;
    }
    


    ColorModel colorModel = layout.getColorModel(null);
    if ((colorModel != null) && (!JDKWorkarounds.areCompatibleDataModels(layout.getSampleModel(src), colorModel)))
    {


      layout.unsetValid(512);
    }
    

    if ((layout.getSampleModel(src) == src.getSampleModel()) && (layout.getMinX(src) == src.getMinX()) && (layout.getMinY(src) == src.getMinY()) && (layout.getWidth(src) == src.getWidth()) && (layout.getHeight(src) == src.getHeight()) && (layout.getTileWidth(src) == src.getTileWidth()) && (layout.getTileHeight(src) == src.getTileHeight()) && (layout.getTileGridXOffset(src) == src.getTileGridXOffset()) && (layout.getTileGridYOffset(src) == src.getTileGridYOffset()))
    {








      if (layout.getColorModel(src) == src.getColorModel())
      {
        return src;
      }
      
      RenderingHints hints = renderHints;
      if ((hints != null) && (hints.containsKey(JAI.KEY_TILE_CACHE))) {
        hints = new RenderingHints(renderHints);
        hints.remove(JAI.KEY_TILE_CACHE);
      }
      

      return new NullOpImage(src, layout, hints, 2);
    }
    


    if (isDataTypeChange == true)
    {

      if (renderHints == null) {
        renderHints = new RenderingHints(JAI.KEY_REPLACE_INDEX_COLOR_MODEL, Boolean.TRUE);


      }
      else if (!renderHints.containsKey(JAI.KEY_REPLACE_INDEX_COLOR_MODEL))
      {


        renderHints.put(JAI.KEY_REPLACE_INDEX_COLOR_MODEL, Boolean.TRUE);
      }
    }
    

    return new CopyOpImage(src, renderHints, layout);
  }
}
