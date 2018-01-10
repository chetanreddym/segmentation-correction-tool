package gttool.document;

import gttool.DLImage;
import gttool.exceptions.DLException;
import gttool.exceptions.DLExceptionCodes;
import gttool.misc.Count;
import gttool.misc.TypeAttributeEntry;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;









public class DLPage
  implements Comparable
{
  public static final String ID_NONE = "<None>";
  public DLDocument pageDocument = null;
  
  public String pageID = "<None>";
  


  public LinkedHashMap<String, String> pageTags = new LinkedHashMap(0, 0.75F, true);
  



  public LinkedList<DLZone> pageZones = new LinkedList();
  
  protected DLImage pageImage = null;
  
  public String imageSrc = null;
  
  protected int width;
  protected int height;
  
  public DLPage clone()
  {
    Map<DLZone, DLZone> cloneMap = new HashMap();
    
    DLPage ret = new DLPage();
    width = width;
    height = height;
    imageSrc = imageSrc;
    pageImage = pageImage;
    
    pageZones = new LinkedList();
    Iterator<DLZone> zoneItr = pageZones.iterator();
    while (zoneItr.hasNext()) {
      DLZone orig = (DLZone)zoneItr.next();
      DLZone clone = orig.clone();
      zonePage = ret;
      cloneMap.put(orig, clone);
      pageZones.add(clone);
    }
    
    zoneItr = pageZones.iterator();
    while (zoneItr.hasNext()) {
      DLZone clone = (DLZone)zoneItr.next();
      nextZone = ((DLZone)cloneMap.get(nextZone));
      previousZone = ((DLZone)cloneMap.get(previousZone));
    }
    
    pageTags = ((LinkedHashMap)pageTags.clone());
    pageID = pageID;
    pageDocument = pageDocument;
    
    return ret;
  }
  
  public void rotate() {
    int temp = width;
    width = height;
    height = temp;
    
    Iterator<DLZone> zoneItr = pageZones.iterator();
    while (zoneItr.hasNext())
      ((DLZone)zoneItr.next()).rotate(width);
  }
  
  public DLPage() {
    pageTags.put("gedi_type", "DL_PAGE");
  }
  
  public DLPage(String pageID) {
    this.pageID = pageID;
    pageTags.put("gedi_type", "DL_PAGE");
  }
  



  public DLPage(DLImage pgImage, String pgID)
    throws DLException
  {
    if (pgImage.dlGetImageData() == null)
    {

      throw new DLException(DLExceptionCodes.DL_NULL_POINTER_EXCEPTION, 
        "<DLPage::DLPage()> The page image data is NULL!");
    }
    pageDocument = null;
    pageImage = pgImage;
    pageID = pgID;
    pageTags.put("gedi_type", "DL_PAGE");
  }
  
  public void dlSetWidth(int width)
  {
    this.width = width;
  }
  
  public void dlSetHeight(int height) {
    this.height = height;
  }
  
  public int dlGetWidth() {
    return width;
  }
  
  public int dlGetHeight() {
    return height;
  }
  



  public final int dlGetImageWidth()
  {
    return pageImage != null ? pageImage.getWidth() : 0;
  }
  


  public final int dlGetImageHeight()
  {
    return pageImage != null ? pageImage.getHeight() : 0;
  }
  



  public final int dlGetNrOfZones()
  {
    return pageZones.size();
  }
  



  public boolean dlHasZones()
  {
    return !pageZones.isEmpty();
  }
  


  public void dlAppendZoneWithoutCheck(DLZone pageZone)
  {
    zonePage = this;
    parentZone = null;
    pageZones.add(pageZone);
  }
  






  public void dlAppendZone(DLZone pageZone)
    throws DLException
  {
    if (!pageZone.dlIsWithinPageBoundaries(dlGetWidth(), dlGetHeight())) {
      throw new DLException(
        DLExceptionCodes.DL_OUT_IMAGE_BOUNDARY_EXCEPTION, 
        "<DLPage::dlAppendZone()> The page zone exceeds the page boundaries!");
    }
    
    zonePage = this;
    parentZone = null;
    
    if (pageZone.dlHasChildZones()) {
      pageZone.dlPropagateZonePointers();
    }
    pageZones.add(pageZone);
  }
  






  public void dlAppendZoneList(LinkedList zoneList)
    throws DLException
  {
    if (zoneList.size() != 0) {
      for (Iterator i = zoneList.iterator(); i.hasNext();) {
        dlAppendZone((DLZone)i.next());
      }
    }
  }
  















  public void dlInsertZone(DLZone pageZone, int cursorPosition)
    throws DLException
  {
    if ((cursorPosition < 0) || (cursorPosition > dlGetNrOfZones()))
    {

      throw new DLException(
        DLExceptionCodes.DL_WRONG_FORMAT_EXCEPTION, 
        "<DLPage::dlInsertZone()> The specified position for zone insertion is invalid!");
    }
    
    if (!pageZone.dlIsWithinPageBoundaries(dlGetWidth(), dlGetHeight()))
    {

      throw new DLException(
        DLExceptionCodes.DL_OUT_IMAGE_BOUNDARY_EXCEPTION, 
        "<DLPage::dlInsertZone()> The page zone exceeds the page boundaries!"); }
    zonePage = this;
    parentZone = null;
    if (pageZone.dlHasChildZones())
      pageZone.dlPropagateZonePointers();
    pageZones.add(cursorPosition, pageZone);
  }
  










  public void dlInsertZoneList(LinkedList zoneList, int cursorPosition)
    throws DLException
  {
    if ((cursorPosition < 0) || (cursorPosition > dlGetNrOfZones()))
    {

      throw new DLException(
        DLExceptionCodes.DL_WRONG_FORMAT_EXCEPTION, 
        "<DLPage::dlInsertZoneList()> The specified position for zone insertion is invalid!"); }
    if (zoneList.size() != 0) {
      int currentCursor = cursorPosition;
      
      for (Iterator zone_Iter = zoneList.iterator(); zone_Iter.hasNext();) {
        dlInsertZone((DLZone)zone_Iter.next(), currentCursor);
        currentCursor++;
      }
    }
  }
  




  public void dlDeleteZone(int cursorPosition)
    throws DLException
  {
    if ((cursorPosition < 0) || (cursorPosition > dlGetNrOfZones()))
    {

      throw new DLException(DLExceptionCodes.DL_WRONG_FORMAT_EXCEPTION, 
        "<DLPage::dlDeleteZone()> The specified position for zone deletion is invalid!");
    }
    pageZones.remove(cursorPosition);
  }
  





  public boolean dlDeleteZone(DLZone deletedZone)
  {
    return pageZones.remove(deletedZone);
  }
  



  public void dlClearZones()
  {
    pageZones.clear();
  }
  


  public DLImage dlGetImage()
  {
    return pageImage;
  }
  



  public boolean dlCheckPageImageData()
  {
    return pageImage == null;
  }
  



  public int compareTo(Object right)
  {
    if (((right instanceof DLPage)) && 
      (pageDocument == pageDocument))
      return pageID.compareTo(pageID);
    return 1;
  }
  
  public boolean equals(Object object) {
    return 
      (pageID.equals(pageID)) && 
      (pageDocument == pageDocument);
  }
  
  public HashMap<String, Count> dlGetTypeCount() {
    HashMap<String, Count> count_set = new HashMap();
    for (Iterator<DLZone> i = pageZones.iterator(); i.hasNext();) {
      DLZone curr = (DLZone)i.next();
      curr.dlGetAllTypeIds(count_set);
    }
    return count_set;
  }
  
  public HashMap<String, HashSet<String>> dlGetGroupCount() {
    HashMap<String, HashSet<String>> group_count_set = new HashMap();
    for (Iterator<DLZone> i = pageZones.iterator(); i.hasNext();) {
      DLZone curr = (DLZone)i.next();
      if (curr.isGroup())
        curr.dlGetAllGroups(group_count_set);
    }
    return group_count_set;
  }
  
  public Map<String, Map<String, TypeAttributeEntry>> getZoneAttributes() {
    Map<String, Map<String, TypeAttributeEntry>> zoneAttributes = new HashMap();
    Iterator<DLZone> itr = pageZones.iterator();
    while (itr.hasNext()) {
      DLZone tmp = (DLZone)itr.next();
      String zoneType = tmp.getAttributeValue("gedi_type");
      Map attMap = (Map)zoneAttributes.get(zoneType);
      if (attMap == null)
        attMap = new HashMap();
      attMap.putAll(tmp.getAttributes());
      zoneAttributes.put(zoneType, attMap);
    }
    return zoneAttributes;
  }
  
  public Vector<DLZone> dlGetAllZones() {
    Vector<DLZone> zone_list = new Vector(pageZones.size());
    for (DLZone curr : pageZones) {
      curr.dlGetAllZones(zone_list);
    }
    return zone_list;
  }
  
  public Iterator iterator() {
    return dlGetAllZones().iterator();
  }
  
  public Vector<String> gettAllAttributes() {
    Vector<String> att_list = new Vector();
    for (Iterator<DLZone> i = pageZones.iterator(); i.hasNext();) {
      DLZone curr = (DLZone)i.next();
      att_list.addAll(curr.getAllAttributes());
    }
    return att_list;
  }
  
  public String getpageID() { return pageID; }
  
  public String getImageSrc()
  {
    return imageSrc;
  }
  
  public void setImageSrc(String imageSrc) {
    this.imageSrc = imageSrc;
  }
}
