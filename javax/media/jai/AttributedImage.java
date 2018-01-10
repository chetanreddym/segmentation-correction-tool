package javax.media.jai;













public class AttributedImage
  extends RenderedImageAdapter
{
  protected Object attribute;
  











  public AttributedImage(PlanarImage image, Object attribute)
  {
    super(image);
    this.attribute = attribute;
  }
  
  public PlanarImage getImage()
  {
    return (PlanarImage)theImage;
  }
  
  public void setAttribute(Object attribute)
  {
    this.attribute = attribute;
  }
  
  public Object getAttribute()
  {
    return attribute;
  }
  






  public boolean equals(Object o)
  {
    if ((o != null) && ((o instanceof AttributedImage))) {
      AttributedImage ai = (AttributedImage)o;
      Object a = ai.getAttribute();
      return (getImage().equals(ai.getImage())) && (attribute == null ? a == null : (a != null) && (attribute.equals(a)));
    }
    


    return false;
  }
  
  public String toString()
  {
    return "Attribute=(" + getAttribute() + ")  Image=" + getImage();
  }
}
