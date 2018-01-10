package ocr.manager;

import java.util.Hashtable;
import java.util.Vector;

















public class OcrPropInfoHolder
  extends PropertiesInfoHolder
{
  public OcrPropInfoHolder(int workmodeIn)
  {
    super(workmodeIn);
  }
  







  public void updateOnDicLoad(String current_dir)
  {
    super.updateOnDicLoad(current_dir);
  }
  
























































  public void addZoneType(String zoneid, Zonetype newzone)
  {
    id_key_hash.put(zoneid, newzone);
  }
  















  public void removeZoneType(int zoneId, String zoneName)
  {
    Vector v = (Vector)zonetypeIdVec.get(1);
    v.remove(zoneId);
    
    v = (Vector)zonetypeNameVec.get(1);
    v.remove(zoneName);
    id_key_hash.remove(zoneId);
    
    selShapeHash.remove(zoneId);
  }
}
