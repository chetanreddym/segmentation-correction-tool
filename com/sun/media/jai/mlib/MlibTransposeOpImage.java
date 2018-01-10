package com.sun.media.jai.mlib;

import com.sun.media.jai.opimage.TransposeOpImage;
import com.sun.medialib.mlib.Image;
import com.sun.medialib.mlib.mediaLibImage;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ImageLayout;
import javax.media.jai.PlanarImage;






































final class MlibTransposeOpImage
  extends TransposeOpImage
{
  public MlibTransposeOpImage(RenderedImage source, Map config, ImageLayout layout, int type)
  {
    super(source, config, layout, type);
  }
  


  public Raster computeTile(int tileX, int tileY)
  {
    Point org = new Point(tileXToX(tileX), tileYToY(tileY));
    WritableRaster dest = createWritableRaster(sampleModel, org);
    



    Rectangle destRect = getTileRect(tileX, tileY).intersection(getBounds());
    




    PlanarImage src = getSourceImage(0);
    



    Rectangle srcRect = mapDestRect(destRect, 0).intersection(src.getBounds());
    

    Raster[] sources = new Raster[1];
    sources[0] = src.getData(srcRect);
    



    computeRect(sources, dest, destRect);
    

    if (src.overlapsMultipleTiles(srcRect)) {
      recycleTile(sources[0]);
    }
    
    return dest;
  }
  

  protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect)
  {
    Raster source = sources[0];
    
    Rectangle srcRect = source.getBounds();
    
    int formatTag = MediaLibAccessor.findCompatibleTag(sources, dest);
    
    MediaLibAccessor srcAccessor = new MediaLibAccessor(source, srcRect, formatTag);
    
    MediaLibAccessor dstAccessor = new MediaLibAccessor(dest, destRect, formatTag);
    
    int numBands = getSampleModel().getNumBands();
    


    switch (dstAccessor.getDataType()) {
    case 0: 
    case 1: 
    case 2: 
    case 3: 
      mediaLibImage[] srcML = srcAccessor.getMediaLibImages();
      mediaLibImage[] dstML = dstAccessor.getMediaLibImages();
      switch (type) {
      case 0: 
        Image.FlipX(dstML[0], srcML[0]);
        break;
      
      case 1: 
        Image.FlipY(dstML[0], srcML[0]);
        break;
      
      case 2: 
        Image.FlipMainDiag(dstML[0], srcML[0]);
        break;
      
      case 3: 
        Image.FlipAntiDiag(dstML[0], srcML[0]);
        break;
      
      case 4: 
        Image.Rotate90(dstML[0], srcML[0]);
        break;
      
      case 5: 
        Image.Rotate180(dstML[0], srcML[0]);
        break;
      
      case 6: 
        Image.Rotate270(dstML[0], srcML[0]);
      }
      
      break;
    
    case 4: 
    case 5: 
      mediaLibImage[] srcML = srcAccessor.getMediaLibImages();
      mediaLibImage[] dstML = dstAccessor.getMediaLibImages();
      switch (type) {
      case 0: 
        Image.FlipX_Fp(dstML[0], srcML[0]);
        break;
      
      case 1: 
        Image.FlipY_Fp(dstML[0], srcML[0]);
        break;
      
      case 2: 
        Image.FlipMainDiag_Fp(dstML[0], srcML[0]);
        break;
      
      case 3: 
        Image.FlipAntiDiag_Fp(dstML[0], srcML[0]);
        break;
      
      case 4: 
        Image.Rotate90_Fp(dstML[0], srcML[0]);
        break;
      
      case 5: 
        Image.Rotate180_Fp(dstML[0], srcML[0]);
        break;
      
      case 6: 
        Image.Rotate270_Fp(dstML[0], srcML[0]);
      }
      
      break;
    
    default: 
      throw new RuntimeException(JaiI18N.getString("Generic2"));
    }
    
    




    if (dstAccessor.isDataCopy()) {
      dstAccessor.copyDataToRaster();
    }
  }
}
