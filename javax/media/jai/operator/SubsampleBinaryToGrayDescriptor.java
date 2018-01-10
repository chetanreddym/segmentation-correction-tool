package javax.media.jai.operator;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderableImage;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.PixelAccessor;
import javax.media.jai.PropertyGenerator;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;















































































































































































public class SubsampleBinaryToGrayDescriptor
  extends OperationDescriptorImpl
{
  private static final String[][] resources = { { "GlobalName", "SubsampleBinaryToGray" }, { "LocalName", "SubsampleBinaryToGray" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("SubsampleBinaryToGray0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/SubsampleBinaryToGrayDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion") }, { "arg0Desc", JaiI18N.getString("SubsampleBinaryToGray1") }, { "arg1Desc", JaiI18N.getString("SubsampleBinaryToGray2") } };
  










  private static final Class[] paramClasses = { Float.class, Float.class };
  



  private static final String[] paramNames = { "xScale", "yScale" };
  



  private static final Object[] paramDefaults = { new Float(1.0F), new Float(1.0F) };
  


  public SubsampleBinaryToGrayDescriptor()
  {
    super(resources, 1, paramClasses, paramNames, paramDefaults);
  }
  
  public boolean isRenderableSupported()
  {
    return true;
  }
  





  public PropertyGenerator[] getPropertyGenerators()
  {
    PropertyGenerator[] pg = new PropertyGenerator[1];
    pg[0] = new SubsampleBinaryToGrayPropertyGenerator();
    return pg;
  }
  










  protected boolean validateParameters(ParameterBlock args, StringBuffer msg)
  {
    if (!super.validateParameters(args, msg)) {
      return false;
    }
    



    RenderedImage src = (RenderedImage)args.getSource(0);
    
    PixelAccessor srcPA = new PixelAccessor(src);
    if ((!isPacked) || (!isMultiPixelPackedSM)) {
      msg.append(getName() + " " + JaiI18N.getString("SubsampleBinaryToGray3"));
      
      return false;
    }
    
    float xScale = args.getFloatParameter(0);
    float yScale = args.getFloatParameter(1);
    if ((xScale <= 0.0F) || (yScale <= 0.0F) || (xScale > 1.0F) || (yScale > 1.0F)) {
      msg.append(getName() + " " + JaiI18N.getString("SubsampleBinaryToGray1") + " or " + JaiI18N.getString("SubsampleBinaryToGray2"));
      

      return false;
    }
    
    return true;
  }
  







  public Number getParamMinValue(int index)
  {
    if ((index == 0) || (index == 1)) {
      return new Float(0.0F);
    }
    throw new ArrayIndexOutOfBoundsException();
  }
  

























  public static RenderedOp create(RenderedImage source0, Float xScale, Float yScale, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("SubsampleBinaryToGray", "rendered");
    


    pb.setSource("source0", source0);
    
    pb.setParameter("xScale", xScale);
    pb.setParameter("yScale", yScale);
    
    return JAI.create("SubsampleBinaryToGray", pb, hints);
  }
  























  public static RenderableOp createRenderable(RenderableImage source0, Float xScale, Float yScale, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("SubsampleBinaryToGray", "renderable");
    


    pb.setSource("source0", source0);
    
    pb.setParameter("xScale", xScale);
    pb.setParameter("yScale", yScale);
    
    return JAI.createRenderable("SubsampleBinaryToGray", pb, hints);
  }
}
