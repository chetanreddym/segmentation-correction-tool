package javax.media.jai;
















/**
 * @deprecated
 */
public class SequentialImage
{
  public PlanarImage image;
  














  public float timeStamp;
  














  public Object cameraPosition;
  















  public SequentialImage(PlanarImage pi, float ts, Object cp)
  {
    if (pi == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    image = pi;
    timeStamp = ts;
    cameraPosition = cp;
  }
}
