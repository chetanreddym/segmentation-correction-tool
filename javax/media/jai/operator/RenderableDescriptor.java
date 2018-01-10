package javax.media.jai.operator;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.BorderExtender;
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;




















































































public class RenderableDescriptor
  extends OperationDescriptorImpl
{
  private static final float[] DEFAULT_KERNEL_1D = { 0.05F, 0.25F, 0.4F, 0.25F, 0.05F };
  





  private static final String[][] resources = { { "GlobalName", "Renderable" }, { "LocalName", "Renderable" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("RenderableDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/RenderableDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion") }, { "arg0Desc", JaiI18N.getString("RenderableDescriptor1") }, { "arg1Desc", JaiI18N.getString("RenderableDescriptor2") }, { "arg2Desc", JaiI18N.getString("RenderableDescriptor3") }, { "arg3Desc", JaiI18N.getString("RenderableDescriptor4") }, { "arg4Desc", JaiI18N.getString("RenderableDescriptor5") } };
  













  private static final Class[] paramClasses = { RenderedOp.class, Integer.class, Float.class, Float.class, Float.class };
  



  private static final String[] paramNames = { "downSampler", "maxLowResDim", "minX", "minY", "height" };
  



  private static final Object[] paramDefaults = { null, new Integer(64), new Float(0.0F), new Float(0.0F), new Float(1.0F) };
  



  public RenderableDescriptor()
  {
    super(resources, null, new Class[] { RenderedImage.class }, paramClasses, paramNames, paramDefaults);
  }
  



  public boolean isRenderedSupported()
  {
    return false;
  }
  
  public boolean isRenderableSupported()
  {
    return true;
  }
  


  protected boolean validateParameters(ParameterBlock args, StringBuffer msg)
  {
    if ((args.getNumParameters() == 0) || (args.getObjectParameter(0) == null))
    {







      ParameterBlock pb = new ParameterBlock();
      KernelJAI kernel = new KernelJAI(DEFAULT_KERNEL_1D.length, DEFAULT_KERNEL_1D.length, DEFAULT_KERNEL_1D.length / 2, DEFAULT_KERNEL_1D.length / 2, DEFAULT_KERNEL_1D, DEFAULT_KERNEL_1D);
      




      pb.add(kernel);
      BorderExtender extender = BorderExtender.createInstance(1);
      
      RenderingHints hints = JAI.getDefaultInstance().getRenderingHints();
      
      if (hints == null) {
        hints = new RenderingHints(JAI.KEY_BORDER_EXTENDER, extender);
      } else {
        hints.put(JAI.KEY_BORDER_EXTENDER, extender);
      }
      
      RenderedOp filter = new RenderedOp("convolve", pb, hints);
      

      pb = new ParameterBlock();
      pb.addSource(filter);
      pb.add(new Float(0.5F)).add(new Float(0.5F));
      pb.add(new Float(0.0F)).add(new Float(0.0F));
      pb.add(Interpolation.getInstance(0));
      RenderedOp downSampler = new RenderedOp("scale", pb, null);
      args.set(downSampler, 0);
    }
    

    if (!super.validateParameters(args, msg)) {
      return false;
    }
    

    if (args.getIntParameter(1) <= 0) {
      msg.append(getName() + " " + JaiI18N.getString("RenderableDescriptor6"));
      
      return false; }
    if (args.getFloatParameter(4) <= 0.0F) {
      msg.append(getName() + " " + JaiI18N.getString("RenderableDescriptor7"));
      
      return false;
    }
    
    return true;
  }
  

































  public static RenderableOp createRenderable(RenderedImage source0, RenderedOp downSampler, Integer maxLowResDim, Float minX, Float minY, Float height, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("Renderable", "renderable");
    


    pb.setSource("source0", source0);
    
    pb.setParameter("downSampler", downSampler);
    pb.setParameter("maxLowResDim", maxLowResDim);
    pb.setParameter("minX", minX);
    pb.setParameter("minY", minY);
    pb.setParameter("height", height);
    
    return JAI.createRenderable("Renderable", pb, hints);
  }
}
