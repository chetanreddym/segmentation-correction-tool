package javax.media.jai;

import java.awt.image.RenderedImage;
import java.util.Vector;



























































































public class ImagePyramid
  extends ImageMIPMap
{
  protected RenderedOp upSampler;
  protected RenderedOp differencer;
  protected RenderedOp combiner;
  private Vector diffImages = new Vector();
  













  protected ImagePyramid() {}
  













  public ImagePyramid(RenderedImage image, RenderedOp downSampler, RenderedOp upSampler, RenderedOp differencer, RenderedOp combiner)
  {
    super(image, downSampler);
    
    if ((upSampler == null) || (differencer == null) || (combiner == null)) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    this.upSampler = upSampler;
    this.differencer = differencer;
    this.combiner = combiner;
  }
  
































  public ImagePyramid(RenderedOp downSampler, RenderedOp upSampler, RenderedOp differencer, RenderedOp combiner)
  {
    super(downSampler);
    
    if ((upSampler == null) || (differencer == null) || (combiner == null)) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    this.upSampler = upSampler;
    this.differencer = differencer;
    this.combiner = combiner;
  }
  







  public RenderedImage getImage(int level)
  {
    if (level < 0) {
      return null;
    }
    
    while (currentLevel < level) {
      getDownImage();
    }
    while (currentLevel > level) {
      getUpImage();
    }
    
    return currentImage;
  }
  




  public RenderedImage getDownImage()
  {
    currentLevel += 1;
    

    RenderedOp downOp = duplicate(downSampler, vectorize(currentImage));
    

    RenderedOp upOp = duplicate(upSampler, vectorize(downOp.getRendering()));
    RenderedOp diffOp = duplicate(differencer, vectorize(currentImage, upOp.getRendering()));
    
    diffImages.add(diffOp.getRendering());
    
    currentImage = downOp.getRendering();
    return currentImage;
  }
  








  public RenderedImage getUpImage()
  {
    if (currentLevel > 0) {
      currentLevel -= 1;
      

      RenderedOp upOp = duplicate(upSampler, vectorize(currentImage));
      

      RenderedImage diffImage = (RenderedImage)diffImages.elementAt(currentLevel);
      
      diffImages.removeElementAt(currentLevel);
      
      RenderedOp combOp = duplicate(combiner, vectorize(upOp.getRendering(), diffImage));
      
      currentImage = combOp.getRendering();
    }
    
    return currentImage;
  }
  







  public RenderedImage getDiffImage()
  {
    RenderedOp downOp = duplicate(downSampler, vectorize(currentImage));
    

    RenderedOp upOp = duplicate(upSampler, vectorize(downOp.getRendering()));
    

    RenderedOp diffOp = duplicate(differencer, vectorize(currentImage, upOp.getRendering()));
    

    return diffOp.getRendering();
  }
}
