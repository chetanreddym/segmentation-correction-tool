package javax.media.jai.operator;

import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderableImage;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;



































































public class CropDescriptor
  extends OperationDescriptorImpl
{
  private static final String[][] resources = { { "GlobalName", "Crop" }, { "LocalName", "Crop" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("CropDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/CropDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion") }, { "arg0Desc", JaiI18N.getString("CropDescriptor1") }, { "arg1Desc", JaiI18N.getString("CropDescriptor2") }, { "arg2Desc", JaiI18N.getString("CropDescriptor3") }, { "arg3Desc", JaiI18N.getString("CropDescriptor4") } };
  












  private static final Class[] paramClasses = { Float.class, Float.class, Float.class, Float.class };
  






  private static final String[] paramNames = { "x", "y", "width", "height" };
  



  private static final Object[] paramDefaults = { NO_PARAMETER_DEFAULT, NO_PARAMETER_DEFAULT, NO_PARAMETER_DEFAULT, NO_PARAMETER_DEFAULT };
  


  private static final String[] supportedModes = { "rendered", "renderable" };
  



  public CropDescriptor()
  {
    super(resources, supportedModes, 1, paramNames, paramClasses, paramDefaults, null);
  }
  










  public boolean validateArguments(String modeName, ParameterBlock args, StringBuffer msg)
  {
    if (!super.validateArguments(modeName, args, msg)) {
      return false;
    }
    
    if (modeName.equalsIgnoreCase("rendered")) {
      return validateRenderedArgs(args, msg);
    }
    if (modeName.equalsIgnoreCase("renderable")) {
      return validateRenderableArgs(args, msg);
    }
    return true;
  }
  










  private boolean validateRenderedArgs(ParameterBlock args, StringBuffer msg)
  {
    float x_req = args.getFloatParameter(0);
    float y_req = args.getFloatParameter(1);
    float w_req = args.getFloatParameter(2);
    float h_req = args.getFloatParameter(3);
    

    Rectangle rect_req = new Rectangle2D.Float(x_req, y_req, w_req, h_req).getBounds();
    


    if (rect_req.isEmpty()) {
      msg.append(getName() + " " + JaiI18N.getString("CropDescriptor5"));
      
      return false;
    }
    

    RenderedImage src = (RenderedImage)args.getSource(0);
    
    Rectangle srcBounds = new Rectangle(src.getMinX(), src.getMinY(), src.getWidth(), src.getHeight());
    




    if (!srcBounds.contains(rect_req)) {
      msg.append(getName() + " " + JaiI18N.getString("CropDescriptor6"));
      
      return false;
    }
    
    return true;
  }
  










  private boolean validateRenderableArgs(ParameterBlock args, StringBuffer msg)
  {
    float x_req = args.getFloatParameter(0);
    float y_req = args.getFloatParameter(1);
    float w_req = args.getFloatParameter(2);
    float h_req = args.getFloatParameter(3);
    

    Rectangle2D rect_req = new Rectangle2D.Float(x_req, y_req, w_req, h_req);
    


    if (rect_req.isEmpty()) {
      msg.append(getName() + " " + JaiI18N.getString("CropDescriptor5"));
      
      return false;
    }
    

    RenderableImage src = (RenderableImage)args.getSource(0);
    
    Rectangle2D rect_src = new Rectangle2D.Float(src.getMinX(), src.getMinY(), src.getWidth(), src.getHeight());
    




    if (!rect_src.contains(rect_req)) {
      msg.append(getName() + " " + JaiI18N.getString("CropDescriptor6"));
      
      return false;
    }
    
    return true;
  }
  






























  public static RenderedOp create(RenderedImage source0, Float x, Float y, Float width, Float height, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("Crop", "rendered");
    


    pb.setSource("source0", source0);
    
    pb.setParameter("x", x);
    pb.setParameter("y", y);
    pb.setParameter("width", width);
    pb.setParameter("height", height);
    
    return JAI.create("Crop", pb, hints);
  }
  





























  public static RenderableOp createRenderable(RenderableImage source0, Float x, Float y, Float width, Float height, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("Crop", "renderable");
    


    pb.setSource("source0", source0);
    
    pb.setParameter("x", x);
    pb.setParameter("y", y);
    pb.setParameter("width", width);
    pb.setParameter("height", height);
    
    return JAI.createRenderable("Crop", pb, hints);
  }
}
