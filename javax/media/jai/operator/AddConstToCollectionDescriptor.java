package javax.media.jai.operator;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.util.Collection;
import java.util.Iterator;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;



























































public class AddConstToCollectionDescriptor
  extends OperationDescriptorImpl
{
  private static final String[][] resources = { { "GlobalName", "AddConstToCollection" }, { "LocalName", "AddConstToCollection" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("AddConstToCollectionDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/AddConstToCollectionDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion") }, { "arg0Desc", JaiI18N.getString("AddConstToCollectionDescriptor1") } };
  









  private static final String[] paramNames = { "constants" };
  



  private static final Class[] paramClasses = { new double[0].getClass() };
  



  private static final Object[] paramDefaults = { { 0.0D } };
  


  private static final String[] supportedModes = { "collection" };
  


  public AddConstToCollectionDescriptor()
  {
    super(resources, supportedModes, 1, paramNames, paramClasses, paramDefaults, null);
  }
  



  public boolean validateArguments(String modeName, ParameterBlock args, StringBuffer msg)
  {
    if (!super.validateArguments(modeName, args, msg)) {
      return false;
    }
    
    Collection col = (Collection)args.getSource(0);
    
    if (col.size() < 1) {
      msg.append(getName() + " " + JaiI18N.getString("AddConstToCollectionDescriptor2"));
      
      return false;
    }
    
    Iterator iter = col.iterator();
    while (iter.hasNext()) {
      Object o = iter.next();
      if (!(o instanceof RenderedImage)) {
        msg.append(getName() + " " + JaiI18N.getString("AddConstToCollectionDescriptor3"));
        
        return false;
      }
    }
    
    int length = ((double[])args.getObjectParameter(0)).length;
    if (length < 1) {
      msg.append(getName() + " " + JaiI18N.getString("AddConstToCollectionDescriptor4"));
      
      return false;
    }
    
    return true;
  }
  





















  public static Collection createCollection(Collection source0, double[] constants, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("AddConstToCollection", "collection");
    


    pb.setSource("source0", source0);
    
    pb.setParameter("constants", constants);
    
    return JAI.createCollection("AddConstToCollection", pb, hints);
  }
}
