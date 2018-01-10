package ocr.manager.zones;

import gttool.document.DLPage;
import gttool.document.DLZone;
import gttool.document.DLZone.SplitDirection;
import gttool.exceptions.DLException;
import gttool.io.DLDocumentXmlParser;
import gttool.misc.Count;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Float;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import ocr.gui.Group;
import ocr.gui.OCRInterface;
import ocr.gui.ReadingOrder;
import ocr.gui.Zone;
import ocr.gui.leftPanel.DatasetSpecificToolPanel;
import ocr.gui.leftPanel.LeftPanel;
import ocr.gui.leftPanel.TypeWindow;
import ocr.tif.ImageDisplay;
import ocr.tif.ImageDisplay.ZoneVector;
import ocr.tif.ImageReaderDrawer;
import ocr.util.AttributeConfigUtil;
import ocr.util.UniqueZoneId;








public class ZonesManager
  implements Iterable<DLZone>
{
  OCRInterface ocrIF = OCRInterface.this_interface;
  
  public HashMap<String, Count> countMap;
  
  private DLPage page;
  public static ImageDisplay.ZoneVector actZones = new ImageDisplay.ZoneVector();
  
  private HashMap<DLZone, DLZone> readingOrderCorrespondence;
  
  public ZonesManager()
  {
    page = new DLPage();
    readingOrderCorrespondence = new HashMap();
    countMap = new HashMap();
    update();
  }
  






  public ZonesManager(DLPage page)
  {
    this.page = page;
    countMap = page.dlGetTypeCount();
    update();
  }
  
  public ZonesManager(ZonesManager orignal)
  {
    this();
    cloneAll(orignal);
  }
  
  private void cloneAll(ZonesManager orignal) {
    if (page != null) {
      page.dlSetHeight(page.dlGetHeight());
      page.dlSetWidth(page.dlGetWidth());
    }
    
    for (DLZone zone : page.pageZones) {
      Zone clone = (Zone)zone;
      clone = clone.clone_zone();
      clone.dlSetPagePointer(page);
      readingOrderCorrespondence.put(zone, clone);
      page.pageZones.add(clone);
    }
    
    countMap = page.dlGetTypeCount();
    update();
    
    switchPointers();
  }
  








  public void switchPointers()
  {
    for (DLZone zone_clone : page.pageZones) {
      nextZone = ((DLZone)readingOrderCorrespondence.get(nextZone));
      previousZone = ((DLZone)readingOrderCorrespondence.get(previousZone));
    }
  }
  
  public boolean hasHiearchy() {
    for (DLZone zone : page.pageZones) {
      if (zone.dlHasChildZones())
        return true;
    }
    return false;
  }
  




  public DLPage getPage()
  {
    return page;
  }
  
  public void setPage(DLPage newPage) {
    page = newPage;
    
    refreshCount();
  }
  





  public int size()
  {
    int size = 0;
    for (DLZone zone : page.pageZones) {
      size++;
      if (zone.dlHasChildZones())
        size += childSize(zone);
    }
    return size;
  }
  






  public void appendRootZone(DLZone rootZone)
    throws DLException
  {
    page.dlAppendZone(rootZone);
  }
  



  public void destroyHierarchy()
  {
    DLPage p = new DLPage();
    for (DLZone zone : page.pageZones) {
      pageZones.add(zone);
      if (zone.dlHasChildZones()) {
        for (DLZone child : childZones) {
          child.dlSetParentZone(null);
          addChildZones(p, child);
        }
        zone.dlClearChildZones();
      }
    }
    page.dlClearZones();
    page.pageZones.addAll(pageZones);
  }
  
  public void noHierarchy(LinkedList<DLZone> ll) {
    DLPage p = new DLPage();
    for (DLZone zone : page.pageZones) {
      if (!ll.contains(zone)) {
        pageZones.add(zone);
        if (zone.dlHasChildZones()) {
          for (DLZone child : childZones) {
            child.dlSetParentZone(null);
            addChildZones(p, child);
          }
          zone.dlClearChildZones();
        }
      }
    }
    ImageDisplay.alwaysDestroyH = true;
    
    page.pageZones.addAll(pageZones);
  }
  
  public void yesHierarchy(LinkedList<DLZone> ll) {
    DLPage p = new DLPage();
    for (DLZone zone : page.pageZones) {
      if (!ll.contains(zone)) {
        pageZones.add(zone);
        if (zone.dlHasChildZones()) {
          for (DLZone child : childZones) {
            child.dlSetParentZone(null);
            addChildZones(p, child);
          }
          zone.dlClearChildZones();
        }
      }
    }
    ImageDisplay.alwaysDestroyH = false;
    
    page.pageZones.addAll(pageZones);
  }
  





  public void setPageHeightAndWidth(int w, int h)
  {
    page.dlSetWidth(w);
    page.dlSetHeight(h);
  }
  







  public Vector<DLZone> getIntersectingZones(DLZone selectionZone)
  {
    return 
      getIntersectingZones(new Rectangle(
      dlGetZoneOriginx, 
      dlGetZoneOriginy, selectionZone
      .dlGetZoneWidth(), selectionZone
      .dlGetZoneHeight()));
  }
  







  public Vector<DLZone> getIntersectingZones(Rectangle selection)
  {
    Vector<DLZone> v = new Vector();
    
    for (DLZone zone : page.pageZones) {
      getIntersectingZones(selection, zone, v);
    }
    return v;
  }
  











  public void splitOcrZone(DLZone toSplit, int offset, DLZone.SplitDirection direction)
    throws DLException
  {
    splitOcrZone(toSplit, offset, direction);
  }
  


















  public void splitOcrZone(DLZone toSplit, int offset, DLZone.SplitDirection direction, String childZoneID1, String childZoneID2)
    throws DLException
  {
    toSplit.dlSplitZone2(offset, direction);
  }
  











  public LinkedList<DLZone> getAllChildren(DLZone zoneIn)
  {
    return childZones;
  }
  






  public DLZone getRootZone(int i)
  {
    return (DLZone)page.pageZones.get(i);
  }
  







  public boolean contains(DLZone zoneIn)
  {
    for (DLZone zone : page.pageZones) {
      if (zone == zoneIn) {
        return true;
      }
      if ((zone.dlHasChildZones()) && 
        (contains(zone, zoneIn))) {
        return true;
      }
    }
    return false;
  }
  






  private boolean contains(DLZone zone, DLZone zoneIn)
  {
    for (DLZone child : childZones) {
      if (zone == zoneIn) {
        return true;
      }
      if ((child.dlHasChildZones()) && 
        (contains(child, zoneIn)))
        return true;
    }
    return false;
  }
  
  public Rectangle getBoxAroundZones() {
    return null;
  }
  








  public Collection getContainingZones(Zone innerZone)
  {
    return getContainingZones(new Rectangle(innerZone.get_lt_x(), 
      innerZone.get_lt_y(), innerZone.get_width(), innerZone
      .get_height()));
  }
  







  public Vector<DLZone> getContainingZones(DLZone innerZone)
  {
    return getIntersectingZones(new Rectangle(
      dlGetZoneOriginx, dlGetZoneOriginy, innerZone
      .dlGetZoneWidth(), innerZone.dlGetZoneHeight()));
  }
  







  public Collection<DLZone> getContainingZones(Rectangle selection)
  {
    Set<DLZone> s = new HashSet();
    
    for (DLZone zone : page.pageZones) {
      getContainingZones(selection, zone, s);
    }
    return s;
  }
  








  public Vector getIntersectingZones(Zone selectionZone)
  {
    return getIntersectingZones(selectionZone);
  }
  
  public void add(Zone zone)
  {
    try {
      LinkedList<DLZone> changed = new LinkedList();
      
      if (!ImageDisplay.alwaysDestroyH) {
        DLZone parent = findParent(zone);
        if (parent != null) {
          for (DLZone child : childZones)
          {

            if (zone.dlContains(child)) {
              changed.add(child);
            }
          }
          addToCountMap(zone);
          parent.dlAppendChildZone(zone);
          
          for (DLZone child : changed) {
            parent.dlDeleteChildZone(child);
            zone.dlAppendChildZone(child);
          }
        }
        else {
          for (DLZone child : page.pageZones) {
            if (zone.dlContains(child)) {
              changed.add(child);
            }
          }
          addToCountMap(zone);
          page.dlAppendZone(zone);
          for (DLZone child : changed) {
            page.dlDeleteZone(child);
            zone.dlAppendChildZone(child);
          }
        }
        changed = null;
      }
      else {
        addToCountMap(zone);
        page.dlAppendZone(zone);
      }
    }
    catch (DLException localDLException) {}
  }
  


  private boolean isZoneAChild(DLZone child)
  {
    for (DLZone zone : page.pageZones) {
      if (zone.dlContains(child))
        return true;
    }
    return false;
  }
  
  public void add(int i, Zone newZone) {
    add(newZone);
  }
  
  public void add(Object object) {
    if ((object instanceof Zone)) {
      add((Zone)object);
    }
  }
  




  public void addAll(ZonesManager orignal)
  {
    readingOrderCorrespondence = new HashMap();
    
    cloneAll(orignal);
  }
  















  public void deleteZone(Zone toDelete, boolean b)
  {
    remove(toDelete);
  }
  
  public void remove(Zone z) {
    try {
      removeOneZone(z);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  private void removeFromMap(DLZone zone) {
    String typeName = zone.getAttributeValue("gedi_type");
    ((Count)countMap.get(typeName)).sub();
    this_interfacetbdPane.data_panel.t_window.upDataCount(
      typeName, (Count)countMap.get(typeName));
    if (zone.dlHasChildZones()) {
      for (DLZone child : childZones) {
        removeFromMap(child);
      }
    }
  }
  





  public void remove(DLZone zone)
  {
    removeFromMap(zone);
    if (zone.dlGetParentZone() != null) {
      DLZone parent = zone.dlGetParentZone();
      childZones.remove(zone);
    } else {
      page.dlDeleteZone(zone);
    }
  }
  




  public void removeOneZone(DLZone zone)
    throws DLException
  {
    if (zone.dlGetParentZone() != null) {
      DLZone parent = zone.dlGetParentZone();
      parent.dlAppendChildZoneList(childZones);
      childZones.remove(zone);
    } else {
      page.dlAppendZoneList(childZones);
      page.dlDeleteZone(zone);
    }
    
    try
    {
      currentHWObjcurr_canvas.getReadingOrderHandler().deleteFromReadingOrder(zone);
    } catch (NullPointerException npe) {
      System.out.println(zone + "\nZonesManager. removeOneZone(). " + 
        "The zone doesn't participate in Reading Order.");
    }
    
    String typeName = zone.getAttributeValue("gedi_type");
    ((Count)countMap.get(typeName)).sub();
    this_interfacetbdPane.data_panel.t_window.upDataCount(
      typeName, (Count)countMap.get(typeName));
  }
  





  public void removeRootZoneAt(int i)
  {
    page.pageZones.remove(i);
  }
  



  public void removeAllElements()
  {
    page.dlClearZones();
    for (String type : countMap.keySet())
    {
      this_interfacetbdPane.data_panel.t_window.upDataCount(type, new Count()); }
    countMap.clear();
  }
  








  public boolean deleteZone(DLZone toDelete)
  {
    boolean returnVal = false;
    
    if (toDelete.dlGetParentZone() == null)
    {
      returnVal = page.pageZones.remove(toDelete);
    }
    else
    {
      DLZone parent = toDelete.dlGetParentZone();
      returnVal = childZones.remove(toDelete);
    }
    return returnVal;
  }
  

  public void splitOcrZone(Zone toSplit, Group group)
    throws DLException
  {
    UniqueZoneId idObj = ocrIF.getUniqueZoneIdObj();
    int zoneId = Integer.parseInt(idObj.getUniqueZoneIdWithinDocument());
    
    if (splitLinePt1.x == splitLinePt2.x)
    {
      splitOcrZone(toSplit, splitLinePt1.x - 
        toSplit.get_lt_x(), DLZone.SplitDirection.HORIZONTAL, 
        Integer.toString(zoneId), Integer.toString(zoneId + 1));
    }
    else {
      splitOcrZone(toSplit, splitLinePt1.y - 
        toSplit.get_lt_y(), DLZone.SplitDirection.VERTICAL, 
        Integer.toString(zoneId), 
        Integer.toString(zoneId + 1));
    }
    DLZone left = (DLZone)childZones.removeLast();
    DLZone right = (DLZone)childZones.removeLast();
    left.setAttributeValue("gedi_type", toSplit
      .getAttributeValue("gedi_type"));
    right.setAttributeValue("gedi_type", toSplit
      .getAttributeValue("gedi_type"));
    
    remove(toSplit);
    
    Zone zLeft = new Zone(left);
    Zone zRight = new Zone(right);
    
    DLZone parent = findParent(zRight);
    if (parent != null) {
      parent.dlAppendChildZone(zRight);
    } else {
      page.dlAppendZone(zRight);
    }
    parent = findParent(zLeft);
    if (parent != null) {
      parent.dlAppendChildZone(zLeft);
    } else {
      page.dlAppendZone(zLeft);
    }
    
    addToCountMap(zRight);
    addToCountMap(zLeft);
    
    if (group != null) {
      group.addToGroup(zRight);
      group.addToGroup(zLeft);
    }
  }
  
  public void makePCLine(Zone parzone, Zone chzone) throws DLException
  {
    makePCLine(parzone, chzone);
  }
  
  public void erasePCLine(Zone parzone, Zone chzone) throws DLException {
    erasePCLine(parzone, chzone);
  }
  
  public Vector<DLZone> getAsVector() {
    return page.dlGetAllZones();
  }
  
  public int indexOf(Zone zoneIn) {
    return getAsVector().indexOf(zoneIn);
  }
  
  public Iterator<DLZone> iterator() {
    return getAsVector().iterator();
  }
  
  public DLZone get(int i) {
    return (DLZone)getAsVector().get(i);
  }
  
  public boolean contains(Zone zoneIn) {
    return contains(zoneIn);
  }
  
  public int lastIndexOf(Zone selectLast) {
    return getAsVector().lastIndexOf(selectLast);
  }
  
  public DLZone elementAt(int i) {
    return (DLZone)getAsVector().elementAt(i);
  }
  
  public int getCreateIndex(MouseEvent e) {
    return 0;
  }
  







































  private int childSize(DLZone zone)
  {
    int size = 0;
    for (DLZone child : childZones) {
      size++;
      if (child.dlHasChildZones())
        size += childSize(child);
    }
    return size;
  }
  













  public Zone mergeZones(Zone zone1, Zone zone2, Group group)
    throws DLException
  {
    Zone mergedZone = null;
    
    mergedZone = new Zone(zone1.dlMergeZones(zone2));
    mergedZone.setAttributeValue("gedi_type", zone1
      .getAttributeValue("gedi_type"));
    mergedZone.setAttributeValue("id", zoneID);
    if (OCRInterface.this_interface.getPreserveAttributesOnMerge()) {
      mergedZone.getZoneTags().putAll(zone1.getZoneTags());
      if (mergedZone.getAttributeValue("polygon") != null)
        mergedZone.setAttributeValue("polygon", "");
      if (mergedZone.getAttributeValue("orientationD") != null)
        mergedZone.setAttributeValue("orientationD", "");
      if (mergedZone.getAttributeValue("orientation") != null)
        mergedZone.setAttributeValue("orientation", "");
    }
    zoneID = zoneID;
    

    String delimiter = OCRInterface.this_interface.getInsertSpaceOnMerge() ? " " : "";
    
    if ((zone1.hasContents()) || (zone2.hasContents())) {
      mergedZone.setContents(zone1.getContents() + delimiter + zone2.getContents());
    }
    

    DLZone prev = previousZone;
    
    if (prev != null) {
      nextZone = mergedZone;
    }
    previousZone = prev;
    
    if (nextZone != null)
      nextZone.previousZone = mergedZone;
    nextZone = nextZone;
    if (nextZone != null)
      nextZone.previousZone = previousZone;
    if (previousZone != null) {
      previousZone.nextZone = nextZone;
    }
    
    nextZone = null;
    previousZone = null;
    nextZone = null;
    previousZone = null;
    
    remove(zone1);
    remove(zone2);
    
    DLZone parent = findParent(mergedZone);
    if (parent != null) {
      parent.dlAppendChildZone(mergedZone);
    } else {
      page.dlAppendZone(mergedZone);
    }
    
    addToCountMap(mergedZone);
    
    if (group != null) {
      group.addToGroup(mergedZone);
    }
    return mergedZone;
  }
  








  private Zone mergeZones2(Zone zone1, Zone zone2, Group group)
    throws DLException
  {
    Zone mergedZone = null;
    
    mergedZone = new Zone(zone1.dlMergeZones(zone2));
    mergedZone.setAttributeValue("gedi_type", zone1
      .getAttributeValue("gedi_type"));
    mergedZone.setAttributeValue("id", zoneID);
    if (OCRInterface.this_interface.getPreserveAttributesOnMerge()) {
      mergedZone.getZoneTags().putAll(zone1.getZoneTags());
      if (mergedZone.getAttributeValue("polygon") != null)
        mergedZone.setAttributeValue("polygon", "");
      if (mergedZone.getAttributeValue("orientationD") != null)
        mergedZone.setAttributeValue("orientationD", "");
      if (mergedZone.getAttributeValue("orientation") != null)
        mergedZone.setAttributeValue("orientation", "");
    }
    zoneID = zoneID;
    

    String delimiter = OCRInterface.this_interface.getInsertSpaceOnMerge() ? " " : "";
    
    if ((zone1.hasContents()) || (zone2.hasContents())) {
      mergedZone.setContents(zone1.getContents() + delimiter + zone2.getContents());
    }
    

    DLZone prev = previousZone;
    
    if (prev != null) {
      nextZone = mergedZone;
    }
    previousZone = prev;
    
    if (nextZone != null)
      nextZone.previousZone = mergedZone;
    nextZone = nextZone;
    if (nextZone != null)
      nextZone.previousZone = previousZone;
    if (previousZone != null) {
      previousZone.nextZone = nextZone;
    }
    
    nextZone = null;
    previousZone = null;
    nextZone = null;
    previousZone = null;
    
    remove(zone1);
    remove(zone2);
    
    DLZone parent = findParent(mergedZone);
    if (parent != null) {
      parent.dlAppendChildZone(mergedZone);
    } else {
      page.dlAppendZone(mergedZone);
    }
    
    addToCountMap(mergedZone);
    
    if (group != null) {
      group.addToGroup(mergedZone);
    }
    return mergedZone;
  }
  
  private DLZone makePCLine(DLZone parzone, DLZone chzone) throws DLException {
    DLZone pcLine = null;
    
    parzone.dlAppendChildZone(chzone);
    
    Vector<DLZone> vec = getAsVector();
    
    for (int i = 0; i < getAsVector().size(); i++) {
      if (chzone.equals(vec.elementAt(i))) {
        page.dlDeleteZone((DLZone)vec.elementAt(i));
      }
    }
    JOptionPane.showMessageDialog(null, "New Parent/Child Line has been added to the hierarchy");
    
    return pcLine;
  }
  
  private DLZone erasePCLine(DLZone parzone, DLZone chzone) throws DLException {
    DLZone pcLine = null;
    
    parzone.dlDeleteChildZone(chzone);
    page.dlAppendZone(chzone);
    
    JOptionPane.showMessageDialog(null, "Parent/Child Line has been successfully removed");
    
    return pcLine;
  }
  





  private void addToCountMap(DLZone zone)
  {
    String typeName = zone.getAttributeValue("gedi_type");
    if ((typeName != null) && (!typeName.equals(""))) {
      if (!countMap.containsKey(typeName.trim())) {
        countMap.put(typeName.trim(), new Count().add());
      } else {
        countMap.put(typeName.trim(), ((Count)countMap.get(typeName)).add());
      }
      this_interfacetbdPane.data_panel.t_window.upDataCount(typeName.trim(), (Count)countMap.get(typeName));
    }
    if (zone.dlHasChildZones()) {
      for (DLZone child : childZones) {
        addToCountMap(child);
      }
    }
  }
  










  private void getIntersectingZones(Rectangle selection, DLZone zone, Vector<DLZone> result)
  {
    if (zone.dlIntersects(selection)) {
      result.add(zone);
      if (zone.dlHasChildZones()) {
        for (DLZone child : childZones)
          getIntersectingZones(selection, child, result);
      }
    }
  }
  
  public void refreshCount() {
    countMap = page.dlGetTypeCount();
    
    update();
  }
  



  private void update()
  {
    if ((OCRInterface.this_interface != null) && 
      (this_interfacetbdPane != null))
    {

      OCRInterface.getAttsConfigUtil().addZoneAttributes(page.getZoneAttributes());
      this_interfacetbdPane.data_panel.t_window
        .setInitalCount(countMap, getGroupCount());
    }
  }
  









  private void getContainingZones(Rectangle selection, DLZone zone, Set<DLZone> result)
  {
    if (zone.dlContains(selection)) {
      result.add(zone);
      result.remove(zone.dlGetParentZone());
      if (zone.dlHasChildZones()) {
        for (DLZone child : childZones) {
          getContainingZones(selection, child, result);
        }
      }
    }
  }
  
  private void addChildZones(DLPage page, DLZone zone) {
    pageZones.add(zone);
    if (zone.dlHasChildZones()) {
      for (DLZone child : childZones) {
        child.dlSetParentZone(null);
        addChildZones(page, child);
      }
      zone.dlClearChildZones();
    }
  }
  
  private DLZone findParent(DLZone child)
  {
    LinkedList<DLZone> zones = new LinkedList(
      getContainingZones(new Rectangle(dlGetZoneOriginx, 
      dlGetZoneOriginy, child.dlGetZoneWidth(), 
      child.dlGetZoneHeight())));
    

    if ((zones.isEmpty()) || (zones.size() > 1)) {
      zones = null;
      return null;
    }
    return (DLZone)zones.removeFirst();
  }
  
  private double minDistance(DLZone zone1, DLZone zone2)
  {
    float x1 = dlGetZoneOriginx;float y1 = dlGetZoneOriginy;
    float w1 = zone1.dlGetZoneWidth();float h1 = zone1.dlGetZoneHeight();
    float x2 = dlGetZoneOriginx;float y2 = dlGetZoneOriginy;
    float w2 = zone2.dlGetZoneWidth();float h2 = zone2.dlGetZoneHeight();
    
    Line2D top = new Line2D.Float(x1, y1, w1, y1);
    Line2D left = new Line2D.Float(x1, y1, x1, h1);
    Line2D bottom = new Line2D.Float(x1, h1, w1, h1);
    Line2D right = new Line2D.Float(w1, y1, w1, h1);
    
    Vector<Double> distances = new Vector();
    
    distances.add(Double.valueOf(top.ptSegDist(x2, y2)));
    distances.add(Double.valueOf(top.ptSegDist(w2, y2)));
    distances.add(Double.valueOf(left.ptSegDist(x2, y2)));
    distances.add(Double.valueOf(left.ptSegDist(x2, h2)));
    distances.add(Double.valueOf(right.ptSegDist(w2, y2)));
    distances.add(Double.valueOf(right.ptSegDist(w2, h2)));
    distances.add(Double.valueOf(bottom.ptSegDist(x2, h2)));
    distances.add(Double.valueOf(bottom.ptSegDist(w2, h2)));
    return ((Double)Collections.min(distances)).doubleValue();
  }
  
  private static void testSplit() throws DLException {
    ZonesManager zm = new ZonesManager(new DLPage("0"));
    page.dlSetWidth(8000);
    page.dlSetHeight(12000);
    DLZone root = null;DLZone sub = null;
    for (int i = 0; i < 4; i++) {
      root = new DLZone(page, null, 1000 * (i + 1), 
        1000 * (i + 1), 100, 100);
      zm.appendRootZone(root);
      root.setAttributeValue("gedi_type", "DLZONE");
      for (int j = 0; j < 3; j++) {
        sub = new DLZone(page, root, 1000 * (
          i + 1) + 10 * j + 1, 1000 * (i + 1) + 10 * j + 1, 
          10, 10);
        root.dlAppendChildZone(sub);
        sub.setAttributeValue("gedi_type", "DLSUBZONE");
      }
    }
    
    System.out.println("Before");
    DLDocumentXmlParser.printPage(page);
    
    zm.splitOcrZone(sub, 5, DLZone.SplitDirection.VERTICAL, "Left", "Right");
    
    System.out.println("After");
    DLDocumentXmlParser.printPage(page);
    Iterator<Map.Entry<String, Count>> i; while (
      i.hasNext()) {}
  }
  

  public void setActZones(ImageDisplay.ZoneVector az)
  {
    for (int i = 0; i < az.size(); i++) {
      actZones.add(az.elementAt(i));
    }
  }
  

  public ImageDisplay.ZoneVector getActZones()
  {
    return actZones;
  }
  
  public void makeParent()
  {
    int min_lt_x = getPage().dlGetWidth();
    int min_rb_x = 0;
    
    int min_lt_y = getPage().dlGetHeight();
    int min_rb_y = 0;
    
    Zone zz = actZones.elementAt(0);
    String str = zz.getZoneType();
    
    for (int i = 0; i < actZones.size(); i++)
    {
      Zone z = actZones.elementAt(i);
      
      if (z.get_lt_x() <= min_lt_x) {
        min_lt_x = z.get_lt_x();
      }
      
      if (z.get_rb_x() >= min_rb_x) {
        min_rb_x = z.get_rb_x();
      }
      
      if (z.get_lt_y() <= min_lt_y) {
        min_lt_y = z.get_lt_y();
      }
      
      if (z.get_rb_y() >= min_rb_y) {
        min_rb_y = z.get_rb_y();
      }
    }
    

    Zone nzone = new Zone(min_lt_x - 7, min_lt_y - 7, min_rb_x - min_lt_x + 14, min_rb_y - min_lt_y + 14);
    nzone.setZoneType(str);
    

    try
    {
      OCRInterface.this_interface.getCanvas().saveCurrentState();
      getPage().dlAppendZone(nzone);
      if (!ImageDisplay.alwaysDestroyH) {
        DLZone dz = nzone;
        
        for (int i = 0; i < actZones.size(); i++)
        {
          DLZone temp = (DLZone)actZones.get(i);
          getPage().dlDeleteZone(temp);
          dz.dlAppendChildZone(temp);
        }
      }
      ImageReaderDrawer.picture.repaint();
      actZones.removeAllElements();
    }
    catch (DLException e) {
      e.printStackTrace();
    }
  }
  
  public HashMap<DLZone, DLZone> getReadingOrderCorrespondence() {
    return readingOrderCorrespondence;
  }
  








  public HashMap<String, HashSet<String>> getGroupCount()
  {
    HashMap<String, HashSet<String>> groupCount = new HashMap();
    
    groupCount = page.dlGetGroupCount();
    
    return groupCount;
  }
}
