package javax.media.jai;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.HashSet;
import java.util.Set;
import javax.media.jai.util.CaselessStringKey;




















































public class RenderedImageAdapter
  extends PlanarImage
{
  protected RenderedImage theImage;
  private Rectangle tileIndexBounds;
  
  static String[] mergePropertyNames(String[] localNames, String[] sourceNames)
  {
    String[] names = null;
    if ((localNames == null) || (localNames.length == 0)) {
      names = sourceNames;
    } else if ((sourceNames == null) || (sourceNames.length == 0)) {
      names = localNames;

    }
    else
    {
      Set nameSet = new HashSet((localNames.length + sourceNames.length) / 2);
      

      int numSourceNames = sourceNames.length;
      for (int i = 0; i < numSourceNames; i++) {
        nameSet.add(new CaselessStringKey(sourceNames[i]));
      }
      

      int numLocalNames = localNames.length;
      for (int i = 0; i < numLocalNames; i++) {
        nameSet.add(new CaselessStringKey(localNames[i]));
      }
      

      int numNames = nameSet.size();
      CaselessStringKey[] caselessNames = new CaselessStringKey[numNames];
      nameSet.toArray(caselessNames);
      names = new String[numNames];
      for (int i = 0; i < numNames; i++) {
        names[i] = caselessNames[i].getName();
      }
    }
    

    if ((names != null) && (names.length == 0)) {
      names = null;
    }
    
    return names;
  }
  






  public RenderedImageAdapter(RenderedImage im)
  {
    super(im != null ? new ImageLayout(im) : null, null, null);
    if (im == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    theImage = im;
    
    tileIndexBounds = new Rectangle(theImage.getMinTileX(), theImage.getMinTileY(), theImage.getNumXTiles(), theImage.getNumYTiles());
  }
  








  public final RenderedImage getWrappedImage()
  {
    return theImage;
  }
  




  public final Raster getTile(int x, int y)
  {
    return tileIndexBounds.contains(x, y) ? theImage.getTile(x, y) : null;
  }
  
  public final Raster getData()
  {
    return theImage.getData();
  }
  
  public final Raster getData(Rectangle rect)
  {
    return theImage.getData(rect);
  }
  
  public final WritableRaster copyData(WritableRaster raster)
  {
    return theImage.copyData(raster);
  }
  




  public final String[] getPropertyNames()
  {
    return mergePropertyNames(super.getPropertyNames(), theImage.getPropertyNames());
  }
  








  public final Object getProperty(String name)
  {
    Object property = super.getProperty(name);
    

    if (property == Image.UndefinedProperty) {
      property = theImage.getProperty(name);
    }
    
    return property;
  }
  













  public final Class getPropertyClass(String name)
  {
    Class propClass = super.getPropertyClass(name);
    

    if (propClass == null)
    {
      Object propValue = getProperty(name);
      
      if (propValue != Image.UndefinedProperty)
      {
        propClass = propValue.getClass();
      }
    }
    
    return propClass;
  }
}
