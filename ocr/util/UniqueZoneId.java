package ocr.util;

import gttool.document.DLDocument;
import gttool.document.DLPage;
import gttool.document.DLZone;
import java.io.PrintStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.Vector;
import javax.swing.JOptionPane;
import ocr.gui.LoadDataFile;
import ocr.gui.MultiPagePanel;
import ocr.gui.OCRInterface;
import ocr.gui.Zone;
import ocr.manager.GlobalHotkeyManager;
import ocr.tif.ImageDisplay;














public class UniqueZoneId
{
  private Vector<Integer> uniqueZoneId;
  private DLDocument document;
  private LinkedList<DLPage> pages;
  private String[] invalidIdTypes = { "None", "none", "<None>", "", "0", "Left", "Right" };
  
  private LoadDataFile ldf;
  
  private Set<String> isDublicatedZoneId;
  private int maxId = 0;
  
  public UniqueZoneId(LoadDataFile ldfIn) {
    uniqueZoneId = new Vector();
    isDublicatedZoneId = new HashSet();
    ldf = ldfIn;
    maxId = getMaxIdWithinDocument();
  }
  
  public void updateMaxZoneId(String id) {
    if (Integer.valueOf(id).intValue() == maxId)
      maxId -= 1;
  }
  
  public void updateMaxZoneId() {
    maxId = getMaxIdWithinDocument();
  }
  
  public String getUniqueZoneIdWithinDocument() {
    maxId += 1;
    return Integer.toString(maxId);
  }
  
  public int getMaxIdWithinDocument() {
    ldf = OCRInterface.currDoc;
    
    if (ldf == null) {
      return uniqueID_nullDoc();
    }
    

    document = ldf.getDocument();
    pages = document.documentPages;
    uniqueZoneId.clear();
    
    String parentZoneId = null;String childZoneId = null;
    
    Iterator localIterator2;
    for (Iterator localIterator1 = pages.iterator(); localIterator1.hasNext(); 
        
        localIterator2.hasNext())
    {
      DLPage page = (DLPage)localIterator1.next();
      LinkedList<DLZone> zones = pageZones;
      localIterator2 = zones.iterator(); continue;DLZone zone = (DLZone)localIterator2.next();
      try {
        parentZoneId = zone.getAttributeValue("id");
        uniqueZoneId.add(Integer.valueOf(parentZoneId));
        LinkedList<DLZone> children = zone.getChildrenZones();
        for (DLZone child : children) {
          childZoneId = child.getAttributeValue("id").trim();
          uniqueZoneId.add(Integer.valueOf(childZoneId));
        }
      }
      catch (NumberFormatException localNumberFormatException) {}
    }
    


    if (uniqueZoneId.size() == 0) {
      return 0;
    }
    

    return ((Integer)Collections.max(uniqueZoneId)).intValue();
  }
  





















































  private int uniqueID_nullDoc()
  {
    uniqueZoneId.clear();
    
    LinkedList<DLZone> nulldocZones = ImageDisplay.nulldoc;
    
    String parentZoneId = null;String childZoneId = null;
    
    for (DLZone zone : nulldocZones) {
      try {
        parentZoneId = zone.getAttributeValue("id");
        uniqueZoneId.add(Integer.valueOf(parentZoneId));
        LinkedList<DLZone> children = zone.getChildrenZones();
        for (DLZone child : children) {
          childZoneId = child.getAttributeValue("id").trim();
          uniqueZoneId.add(Integer.valueOf(childZoneId));
        }
      }
      catch (NumberFormatException localNumberFormatException) {}
    }
    

    if (uniqueZoneId.size() == 0) {
      return 0;
    }
    

    return ((Integer)Collections.max(uniqueZoneId)).intValue();
  }
  

  private DLZone searchZone_nullDoc(String requestedZoneId, boolean needHighlight)
  {
    LinkedList<DLZone> nulldocZones = ImageDisplay.nulldoc;
    Iterator localIterator2;
    for (Iterator localIterator1 = nulldocZones.iterator(); localIterator1.hasNext(); 
        







        localIterator2.hasNext())
    {
      DLZone zone = (DLZone)localIterator1.next();
      String parentZoneId = zone.getAttributeValue("id");
      if (parentZoneId.equals(requestedZoneId)) {
        if (needHighlight)
          highlightZone(zone.dlGetZonePage(), (Zone)zone);
        return zone;
      }
      
      LinkedList<DLZone> children = zone.getChildrenZones();
      localIterator2 = children.iterator(); continue;DLZone child = (DLZone)localIterator2.next();
      String childZoneId = child.getAttributeValue("id").trim();
      if (childZoneId.equals(requestedZoneId)) {
        if (needHighlight)
          highlightZone(child.dlGetZonePage(), (Zone)child);
        return zone;
      }
    }
    

    return null;
  }
  









  public void replaceNotValidId()
  {
    if (document == null) {
      return;
    }
    boolean foundInvalidId = false;
    String parentZoneId = null;String childZoneId = null;
    
    Iterator localIterator2;
    for (Iterator localIterator1 = pages.iterator(); localIterator1.hasNext(); 
        

        localIterator2.hasNext())
    {
      DLPage page = (DLPage)localIterator1.next();
      LinkedList<DLZone> zones = pageZones;
      
      localIterator2 = zones.iterator(); continue;DLZone zone = (DLZone)localIterator2.next();
      try {
        parentZoneId = zone.getAttributeValue("id").trim();
        
        for (String s : invalidIdTypes) {
          if (parentZoneId.equals(s)) {
            foundInvalidId = true;
            zone.setAttributeValue("id", 
              getUniqueZoneIdWithinDocument());
          }
        }
        
        LinkedList<DLZone> children = zone.getChildrenZones();
        int m; int k; for (Iterator localIterator3 = children.iterator(); localIterator3.hasNext(); 
            
            k < m)
        {
          DLZone child = (DLZone)localIterator3.next();
          childZoneId = child.getAttributeValue("id").trim();
          String[] arrayOfString2; m = (arrayOfString2 = invalidIdTypes).length;k = 0; continue;String s = arrayOfString2[k];
          if (childZoneId.equals(s)) {
            foundInvalidId = true;
            child.setAttributeValue("id", 
              getUniqueZoneIdWithinDocument());
          }
          k++;

        }
        


      }
      catch (NullPointerException npl)
      {

        System.out.println("UniqueZoneId. replaceNotValidId. No \"id\" tag in XML");
      }
    }
    



    if (foundInvalidId) {
      OCRInterface.currDoc.dumpData();
    }
    checkDublicates();
  }
  




























  public String searchZone(boolean showPopUp, String requestedZoneId)
  {
    GlobalHotkeyManager.getInstance().setEnabled(false);
    String returnMsg = null;
    
    ldf = OCRInterface.getCurrDoc();
    

    if (ldf == null) {
      returnMsg = "There is no XML for the selected image.";
      if (showPopUp) {
        JOptionPane.showMessageDialog(null, returnMsg, 
          "Zone search", 1);
      } else {
        GlobalHotkeyManager.getInstance().setEnabled(true);
        return returnMsg;
      }
    }
    else if (isDocumentEmpty()) {
      returnMsg = "There are no created zones in the selected document.";
      if (showPopUp) {
        JOptionPane.showMessageDialog(null, returnMsg, 
          "Zone search", 1);
      } else {
        GlobalHotkeyManager.getInstance().setEnabled(true);
        return returnMsg;
      }
    }
    

    boolean zoneFound = false;
    
    DLZone foundZone = searchZone(requestedZoneId, true);
    
    if (foundZone != null) {
      zoneFound = true;
    }
    if (!zoneFound) {
      returnMsg = "Zone not found";
      String msg = "<HTML>The search zone with ID: <FONT COLOR=Blue>" + 
        requestedZoneId + 
        "</FONT> was not found.</HTML>";
      if (showPopUp) {
        JOptionPane.showMessageDialog(OCRInterface.this_interface, msg, 
          "Zone search", 1);
      } else {
        GlobalHotkeyManager.getInstance().setEnabled(true);
        return returnMsg;
      }
    }
    
    GlobalHotkeyManager.getInstance().setEnabled(true);
    return returnMsg;
  }
  
  public DLZone searchZone(String requestedZoneId, boolean needHighlight) {
    ldf = OCRInterface.getCurrDoc();
    
    if (ldf == null) {
      return searchZone_nullDoc(requestedZoneId, needHighlight);
    }
    document = ldf.getDocument();
    pages = document.getdocPages();
    Iterator localIterator2;
    for (Iterator localIterator1 = pages.iterator(); localIterator1.hasNext(); 
        


        localIterator2.hasNext())
    {
      DLPage page = (DLPage)localIterator1.next();
      
      LinkedList<DLZone> zones = pageZones;
      
      localIterator2 = zones.iterator(); continue;DLZone zone = (DLZone)localIterator2.next();
      String parentZoneId = zone.getAttributeValue("id");
      if (parentZoneId.equals(requestedZoneId)) {
        if (needHighlight)
          highlightZone(page, (Zone)zone);
        return zone;
      }
      
      LinkedList<DLZone> children = zone.getChildrenZones();
      for (DLZone child : children) {
        String childZoneId = child.getAttributeValue("id").trim();
        if (childZoneId.equals(requestedZoneId)) {
          if (needHighlight)
            highlightZone(page, (Zone)child);
          return zone;
        }
      }
    }
    

    return null;
  }
  





  private boolean isDocumentEmpty()
  {
    ldf = OCRInterface.getCurrDoc();
    int numZones = 0;
    
    LinkedList<DLPage> pages = ldf.getDocument().getdocPages();
    for (DLPage page : pages) {
      LinkedList<DLZone> zones = pageZones;
      numZones += zones.size();
    }
    
    if (numZones == 0) {
      return true;
    }
    return false;
  }
  







  private void highlightZone(DLPage page, Zone zone)
  {
    if (this_interfacemultiPageTiff) {
      int pageId = Integer.valueOf(page.getpageID()).intValue();
      
      OCRInterface.this_interface.getMultiPagePanel().selectPage(pageId);
    }
    
    OCRInterface.this_interface.getCanvas().reset();
    OCRInterface.this_interface.getCanvas().addToSelected(zone);
    OCRInterface.this_interface.getCanvas().recenterZone();
    OCRInterface.this_interface.getCanvas().paintCanvas();
  }
  














  private void checkDublicates()
  {
    isDublicatedZoneId.clear();
    
    if (document == null) {
      return;
    }
    document = ldf.getDocument();
    pages = document.getdocPages();
    
    boolean foundDublicate = false;
    String parentZoneId = null;String childZoneId = null;
    Iterator localIterator2;
    for (Iterator localIterator1 = pages.iterator(); localIterator1.hasNext(); 
        

        localIterator2.hasNext())
    {
      DLPage page = (DLPage)localIterator1.next();
      LinkedList<DLZone> zones = pageZones;
      
      localIterator2 = zones.iterator(); continue;DLZone zone = (DLZone)localIterator2.next();
      parentZoneId = zone.getAttributeValue("id").trim();
      boolean dublicate = !isDublicatedZoneId.add(parentZoneId);
      if (dublicate) {
        String newID = getUniqueZoneIdWithinDocument();
        zone.setAttributeValue("id", newID);
        zoneID = newID;
        
        previousZone = null;
        foundDublicate = true;
      }
      
      LinkedList<DLZone> children = zone.getChildrenZones();
      for (DLZone child : children) {
        childZoneId = child.getAttributeValue("id").trim();
        dublicate = !isDublicatedZoneId.add(childZoneId);
        if (dublicate) {
          String newID = getUniqueZoneIdWithinDocument();
          child.setAttributeValue("id", 
            newID);
          zoneID = newID;
          
          previousZone = null;
          foundDublicate = true;
        }
      }
    }
    

    if (foundDublicate) {
      OCRInterface.currDoc.dumpData();
      
      String msg = ldf.getFileName() + "\n\nWhile loading the document, identical zone IDs were discovered and replaced with unique ones.";
      JOptionPane.showMessageDialog(null, 
        msg, 
        "Identical IDs identification", 
        1);
    }
  }
}
