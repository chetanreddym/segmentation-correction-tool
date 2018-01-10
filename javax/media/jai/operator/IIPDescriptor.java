package javax.media.jai.operator;

import java.awt.RenderingHints;
import java.awt.color.ICC_Profile;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D.Float;
import java.awt.image.renderable.ParameterBlock;
import java.net.URL;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;














































































































































































































public class IIPDescriptor
  extends OperationDescriptorImpl
{
  private static final String[][] resources = { { "GlobalName", "IIP" }, { "LocalName", "IIP" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("IIPDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/IIPDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion") }, { "arg0Desc", JaiI18N.getString("IIPDescriptor1") }, { "arg1Desc", JaiI18N.getString("IIPDescriptor2") }, { "arg2Desc", JaiI18N.getString("IIPDescriptor3") }, { "arg3Desc", JaiI18N.getString("IIPDescriptor4") }, { "arg4Desc", JaiI18N.getString("IIPDescriptor5") }, { "arg5Desc", JaiI18N.getString("IIPDescriptor6") }, { "arg6Desc", JaiI18N.getString("IIPDescriptor7") }, { "arg7Desc", JaiI18N.getString("IIPDescriptor8") }, { "arg8Desc", JaiI18N.getString("IIPDescriptor9") }, { "arg9Desc", JaiI18N.getString("IIPDescriptor10") }, { "arg10Desc", JaiI18N.getString("IIPDescriptor11") }, { "arg11Desc", JaiI18N.getString("IIPDescriptor12") }, { "arg12Desc", JaiI18N.getString("IIPDescriptor13") }, { "arg13Desc", JaiI18N.getString("IIPDescriptor14") } };
  






















  private static final Class[] paramClasses = { String.class, new int[0].getClass(), Float.class, new float[0].getClass(), Float.class, Rectangle2D.Float.class, AffineTransform.class, Float.class, Rectangle2D.Float.class, Integer.class, String.class, ICC_Profile.class, Integer.class, Integer.class };
  
















  private static final String[] paramNames = { "URL", "subImages", "filter", "colorTwist", "contrast", "sourceROI", "transform", "aspectRatio", "destROI", "rotation", "mirrorAxis", "ICCProfile", "JPEGQuality", "JPEGTable" };
  




















  private static final Object[] paramDefaults = { NO_PARAMETER_DEFAULT, { 0 }, new Float(0.0F), null, new Float(1.0F), null, new AffineTransform(), null, null, new Integer(0), null, null, null, null };
  















  public IIPDescriptor()
  {
    super(resources, 0, paramClasses, paramNames, paramDefaults);
  }
  



  public boolean isRenderableSupported()
  {
    return true;
  }
  








  public Number getParamMinValue(int index)
  {
    if ((index == 0) || (index == 1) || (index == 3) || (index == 5) || (index == 6) || (index == 8) || (index == 10) || (index == 11))
    {
      return null; }
    if (index == 2)
      return new Float(-3.4028235E38F);
    if (index == 7)
      return new Float(0.0F);
    if (index == 4)
      return new Float(1.0F);
    if ((index == 12) || (index == 9))
      return new Integer(0);
    if (index == 13) {
      return new Integer(1);
    }
    throw new ArrayIndexOutOfBoundsException();
  }
  









  public Number getParamMaxValue(int index)
  {
    if ((index == 0) || (index == 1) || (index == 3) || (index == 5) || (index == 6) || (index == 8) || (index == 10) || (index == 11))
    {
      return null; }
    if ((index == 2) || (index == 4) || (index == 7))
      return new Float(Float.MAX_VALUE);
    if (index == 9)
      return new Integer(270);
    if (index == 12)
      return new Integer(100);
    if (index == 13) {
      return new Integer(255);
    }
    throw new ArrayIndexOutOfBoundsException();
  }
  



















  protected boolean validateParameters(ParameterBlock args, StringBuffer msg)
  {
    if (!super.validateParameters(args, msg)) {
      return false;
    }
    try
    {
      new URL((String)args.getObjectParameter(0));
    } catch (Exception e) {
      msg.append(getName() + " " + JaiI18N.getString("IIPDescriptor15"));
      
      return false;
    }
    
    int[] subImages = (int[])args.getObjectParameter(1);
    if (subImages.length < 1) {
      args.set(paramDefaults[1], 1);
    }
    
    float[] colorTwist = (float[])args.getObjectParameter(3);
    if (colorTwist != null) {
      if (colorTwist.length < 16) {
        msg.append(getName() + " " + JaiI18N.getString("IIPDescriptor16"));
        
        return false;
      }
      

      colorTwist[12] = 0.0F;
      colorTwist[13] = 0.0F;
      colorTwist[14] = 0.0F;
      args.set(colorTwist, 3);
    }
    
    float contrast = args.getFloatParameter(4);
    if (contrast < 1.0F) {
      msg.append(getName() + " " + JaiI18N.getString("IIPDescriptor20"));
      
      return false;
    }
    
    Rectangle2D.Float sourceROI = (Rectangle2D.Float)args.getObjectParameter(5);
    
    if ((sourceROI != null) && ((sourceROI.getWidth() < 0.0D) || (sourceROI.getHeight() < 0.0D)))
    {
      msg.append(getName() + " " + JaiI18N.getString("IIPDescriptor17"));
      
      return false;
    }
    
    AffineTransform tf = (AffineTransform)args.getObjectParameter(6);
    if (tf.getDeterminant() == 0.0D) {
      msg.append(getName() + " " + JaiI18N.getString("IIPDescriptor24"));
      
      return false;
    }
    
    if (args.getObjectParameter(7) != null) {
      float aspectRatio = args.getFloatParameter(7);
      if (aspectRatio < 0.0F) {
        msg.append(getName() + " " + JaiI18N.getString("IIPDescriptor21"));
        
        return false;
      }
    }
    
    Rectangle2D.Float destROI = (Rectangle2D.Float)args.getObjectParameter(8);
    
    if ((destROI != null) && ((destROI.getWidth() < 0.0D) || (destROI.getHeight() < 0.0D)))
    {
      msg.append(getName() + " " + JaiI18N.getString("IIPDescriptor17"));
      
      return false;
    }
    
    int rotation = args.getIntParameter(9);
    if ((rotation != 0) && (rotation != 90) && (rotation != 180) && (rotation != 270))
    {
      msg.append(getName() + " " + JaiI18N.getString("IIPDescriptor18"));
      
      return false;
    }
    
    String mirrorAxis = (String)args.getObjectParameter(10);
    if ((mirrorAxis != null) && (!mirrorAxis.equalsIgnoreCase("x")) && (!mirrorAxis.equalsIgnoreCase("y")))
    {

      msg.append(getName() + " " + JaiI18N.getString("IIPDescriptor19"));
      
      return false;
    }
    
    if (args.getObjectParameter(12) != null) {
      int JPEGQuality = args.getIntParameter(12);
      if ((JPEGQuality < 0) || (JPEGQuality > 100)) {
        msg.append(getName() + " " + JaiI18N.getString("IIPDescriptor22"));
        
        return false;
      }
    }
    
    if (args.getObjectParameter(13) != null) {
      int JPEGIndex = args.getIntParameter(13);
      if ((JPEGIndex < 1) || (JPEGIndex > 255)) {
        msg.append(getName() + " " + JaiI18N.getString("IIPDescriptor23"));
        
        return false;
      }
    }
    
    return true;
  }
  

























































  public static RenderedOp create(String URL, int[] subImages, Float filter, float[] colorTwist, Float contrast, Rectangle2D.Float sourceROI, AffineTransform transform, Float aspectRatio, Rectangle2D.Float destROI, Integer rotation, String mirrorAxis, ICC_Profile ICCProfile, Integer JPEGQuality, Integer JPEGTable, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("IIP", "rendered");
    


    pb.setParameter("URL", URL);
    pb.setParameter("subImages", subImages);
    pb.setParameter("filter", filter);
    pb.setParameter("colorTwist", colorTwist);
    pb.setParameter("contrast", contrast);
    pb.setParameter("sourceROI", sourceROI);
    pb.setParameter("transform", transform);
    pb.setParameter("aspectRatio", aspectRatio);
    pb.setParameter("destROI", destROI);
    pb.setParameter("rotation", rotation);
    pb.setParameter("mirrorAxis", mirrorAxis);
    pb.setParameter("ICCProfile", ICCProfile);
    pb.setParameter("JPEGQuality", JPEGQuality);
    pb.setParameter("JPEGTable", JPEGTable);
    
    return JAI.create("IIP", pb, hints);
  }
  
























































  public static RenderableOp createRenderable(String URL, int[] subImages, Float filter, float[] colorTwist, Float contrast, Rectangle2D.Float sourceROI, AffineTransform transform, Float aspectRatio, Rectangle2D.Float destROI, Integer rotation, String mirrorAxis, ICC_Profile ICCProfile, Integer JPEGQuality, Integer JPEGTable, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("IIP", "renderable");
    


    pb.setParameter("URL", URL);
    pb.setParameter("subImages", subImages);
    pb.setParameter("filter", filter);
    pb.setParameter("colorTwist", colorTwist);
    pb.setParameter("contrast", contrast);
    pb.setParameter("sourceROI", sourceROI);
    pb.setParameter("transform", transform);
    pb.setParameter("aspectRatio", aspectRatio);
    pb.setParameter("destROI", destROI);
    pb.setParameter("rotation", rotation);
    pb.setParameter("mirrorAxis", mirrorAxis);
    pb.setParameter("ICCProfile", ICCProfile);
    pb.setParameter("JPEGQuality", JPEGQuality);
    pb.setParameter("JPEGTable", JPEGTable);
    
    return JAI.createRenderable("IIP", pb, hints);
  }
}
