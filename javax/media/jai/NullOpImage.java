package javax.media.jai;

import com.sun.media.jai.util.JDKWorkarounds;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.util.Hashtable;
import java.util.Map;






























public class NullOpImage
  extends PointOpImage
{
  protected int computeType;
  
  private static ImageLayout layoutHelper(RenderedImage source, ImageLayout layout)
  {
    ImageLayout il = new ImageLayout(source);
    


    if ((layout != null) && (layout.isValid(512))) {
      ColorModel colorModel = layout.getColorModel(null);
      if (JDKWorkarounds.areCompatibleDataModels(source.getSampleModel(), colorModel))
      {
        il.setColorModel(colorModel);
      }
    }
    
    return il;
  }
  




































  public NullOpImage(RenderedImage source, ImageLayout layout, Map configuration, int computeType)
  {
    super(PlanarImage.wrapRenderedImage(source).createSnapshot(), layoutHelper(source, layout), configuration, false);
    



    if ((computeType != 1) && (computeType != 2) && (computeType != 3))
    {

      throw new IllegalArgumentException(JaiI18N.getString("NullOpImage0"));
    }
    
    this.computeType = computeType;
  }
  





























  /**
   * @deprecated
   */
  public NullOpImage(RenderedImage source, TileCache cache, int computeType, ImageLayout layout)
  {
    this(source, layout, cache != null ? new RenderingHints(JAI.KEY_TILE_CACHE, cache) : null, computeType);
  }
  









  public Raster computeTile(int tileX, int tileY)
  {
    return getSource(0).getTile(tileX, tileY);
  }
  



  public boolean computesUniqueTiles()
  {
    return false;
  }
  


  protected synchronized Hashtable getProperties()
  {
    return getSource(0).getProperties();
  }
  



  protected synchronized void setProperties(Hashtable properties)
  {
    getSource(0).setProperties(properties);
  }
  



  public String[] getPropertyNames()
  {
    return getSource(0).getPropertyNames();
  }
  




  public String[] getPropertyNames(String prefix)
  {
    return getSource(0).getPropertyNames(prefix);
  }
  




  public Class getPropertyClass(String name)
  {
    return getSource(0).getPropertyClass(name);
  }
  




  public Object getProperty(String name)
  {
    return getSource(0).getProperty(name);
  }
  


  public void setProperty(String name, Object value)
  {
    getSource(0).setProperty(name, value);
  }
  




  public void removeProperty(String name)
  {
    getSource(0).removeProperty(name);
  }
  







  public int getOperationComputeType()
  {
    return computeType;
  }
}
