package javax.media.jai;












/**
 * @deprecated
 */
public class CoordinateImage
{
  public PlanarImage image;
  









  public Object coordinate;
  










  public CoordinateImage(PlanarImage pi, Object c)
  {
    if ((pi == null) || (c == null)) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    image = pi;
    coordinate = c;
  }
}
