package com.sun.media.jai.opimage;

import com.sun.media.jai.util.JDKWorkarounds;
import java.awt.image.ColorModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ImageLayout;
import javax.media.jai.PlanarImage;
import javax.media.jai.PointOpImage;





























final class BandSelectOpImage
  extends PointOpImage
{
  private boolean areDataCopied;
  private int[] bandIndices;
  
  private static ImageLayout layoutHelper(ImageLayout layout, RenderedImage source, int[] bandIndices)
  {
    ImageLayout il = layout == null ? new ImageLayout() : (ImageLayout)layout.clone();
    


    SampleModel sourceSM = source.getSampleModel();
    int numBands = bandIndices.length;
    




    SampleModel sm = null;
    if (((sourceSM instanceof SinglePixelPackedSampleModel)) && (numBands < 3)) {
      sm = new PixelInterleavedSampleModel(0, sourceSM.getWidth(), sourceSM.getHeight(), numBands, sourceSM.getWidth() * numBands, new int[] { 0, numBands == 1 ? new int[] { 0 } : 1 });

    }
    else
    {

      sm = sourceSM.createSubsetSampleModel(bandIndices);
    }
    il.setSampleModel(sm);
    

    ColorModel cm = il.getColorModel(null);
    if ((cm != null) && (!JDKWorkarounds.areCompatibleDataModels(sm, cm)))
    {

      il.unsetValid(512);
    }
    

    il.setTileGridXOffset(source.getTileGridXOffset());
    il.setTileGridYOffset(source.getTileGridYOffset());
    il.setTileWidth(source.getTileWidth());
    il.setTileHeight(source.getTileHeight());
    
    return il;
  }
  











  public BandSelectOpImage(RenderedImage source, Map config, ImageLayout layout, int[] bandIndices)
  {
    super(vectorize(source), layoutHelper(layout, source, bandIndices), config, true);
    


    areDataCopied = (((source.getSampleModel() instanceof SinglePixelPackedSampleModel)) && (bandIndices.length < 3));
    

    this.bandIndices = ((int[])bandIndices.clone());
  }
  
  public boolean computesUniqueTiles() {
    return areDataCopied;
  }
  
  public Raster computeTile(int tileX, int tileY) {
    Raster tile = getSourceImage(0).getTile(tileX, tileY);
    
    if (areDataCopied)
    {

      tile = tile.createChild(tile.getMinX(), tile.getMinY(), tile.getWidth(), tile.getHeight(), tile.getMinX(), tile.getMinY(), bandIndices);
      


      WritableRaster raster = createTile(tileX, tileY);
      raster.setRect(tile);
      
      return raster;
    }
    
    return tile.createChild(tile.getMinX(), tile.getMinY(), tile.getWidth(), tile.getHeight(), tile.getMinX(), tile.getMinY(), bandIndices);
  }
  




  public Raster getTile(int tileX, int tileY)
  {
    return computeTile(tileX, tileY);
  }
}
