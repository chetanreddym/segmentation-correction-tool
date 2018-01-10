package javax.media.jai.operator;

import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;










































































public class ColorConvertDescriptor
  extends OperationDescriptorImpl
{
  private static final String[][] resources = { { "GlobalName", "ColorConvert" }, { "LocalName", "ColorConvert" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("ColorConvertDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/ColorConvertDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion2") }, { "arg0Desc", JaiI18N.getString("ColorConvertDescriptor1") } };
  











  private static final Class[] paramClasses = { ColorModel.class };
  



  private static final String[] paramNames = { "colorModel" };
  



  private static final Object[] paramDefaults = { NO_PARAMETER_DEFAULT };
  


  public ColorConvertDescriptor()
  {
    super(resources, 1, paramClasses, paramNames, paramDefaults);
  }
  
  public boolean isRenderableSupported()
  {
    return true;
  }
  





















  public static RenderedOp create(RenderedImage source0, ColorModel colorModel, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("ColorConvert", "rendered");
    


    pb.setSource("source0", source0);
    
    pb.setParameter("colorModel", colorModel);
    
    return JAI.create("ColorConvert", pb, hints);
  }
  




















  public static RenderableOp createRenderable(RenderableImage source0, ColorModel colorModel, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("ColorConvert", "renderable");
    


    pb.setSource("source0", source0);
    
    pb.setParameter("colorModel", colorModel);
    
    return JAI.createRenderable("ColorConvert", pb, hints);
  }
}
