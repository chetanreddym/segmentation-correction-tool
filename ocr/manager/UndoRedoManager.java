package ocr.manager;

import gttool.document.DLDocument;
import gttool.document.DLPage;
import gttool.document.DLZone;
import gttool.exceptions.DLException;
import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Vector;
import ocr.gui.Group;
import ocr.gui.LoadDataFile;
import ocr.gui.OCRInterface;
import ocr.gui.Zone;
import ocr.gui.leftPanel.AttributeWindow;
import ocr.gui.leftPanel.DatasetSpecificToolPanel;
import ocr.gui.leftPanel.LeftPanel;
import ocr.manager.zones.ZonesManager;
import ocr.tif.ImageDisplay;
import ocr.tif.ImageReaderDrawer;









public class UndoRedoManager
{
  private LinkedList<DLPage> undoPageStack = new LinkedList();
  private LinkedList<Vector<Zone>> undoActiveStack = new LinkedList();
  
  private LinkedList<DLPage> redoPageStack = new LinkedList();
  private LinkedList<Vector<Zone>> redoActiveStack = new LinkedList();
  



  OCRInterface ocrIF = OCRInterface.this_interface;
  


  private ZonesManager currentState;
  


  private Vector<Zone> selectedZones;
  


  private static final int MAX_UNDO_STEPS = 32;
  


  private boolean REACHED_MAX_UNDO_STEPS = false;
  

  public UndoRedoManager() {}
  

  public void saveState(ZonesManager operatedOn, Vector<Zone> activeZones)
  {
    if (undoPageStack.size() >= 32) {
      undoPageStack.removeLast();
      undoActiveStack.removeLast();
      REACHED_MAX_UNDO_STEPS = true;
    }
    currentState = operatedOn;
    selectedZones = activeZones;
    undoPageStack.addFirst(this_interfacetbdPane.data_panel.a_window.getPage().clone());
    undoActiveStack.addFirst((Vector)activeZones.clone());
    redoPageStack.clear();
    redoActiveStack.clear();
    
    LoadDataFile ldf = ocrIF.workmodeProps[OCRInterface.currWorkmode]
      .getDataFileHash(ocrIF.currDatasetNum());
    if (ldf != null) {
      ldf.setModified(true);
    }
    


    if (OCRInterface.currDoc != null) {
      OCRInterface.currDoc.setModified(true);
    }
    
    OCRInterface.this_interface.updateCurrFilename();
  }
  


  private void setNewPage(DLPage newPage)
  {
    String oldPgOrStr = (String)this_interfacetbdPane.data_panel.a_window.getPage().pageTags.get("GEDI_orientation");
    this_interfacetbdPane.data_panel.a_window.setPage(newPage);
    currentState.setPage(newPage);
    if (OCRInterface.currDoc != null) {
      try {
        int pageID = this_interfacecurrPageID;
        pageID = this_interfacemultiPageTiff ? pageID - 1 : pageID;
        
        OCRInterface.currDoc.getDocument().dlDeletePage(pageID);
        OCRInterface.currDoc.getDocument().dlInsertPage(newPage, pageID);
      } catch (DLException e) {
        e.printStackTrace();
      }
    } else {
      ImageDisplay.nulldoc.clear();
      ImageDisplay.nulldoc.addAll(pageZones);
    }
    




    String pgOrStr = (String)pageTags.get("GEDI_orientation");
    int newOrientation = pgOrStr != null ? Integer.parseInt(pgOrStr) : 0;
    int oldOrientation = oldPgOrStr != null ? Integer.parseInt(oldPgOrStr) : 0;
    if (oldOrientation != newOrientation) {
      currentHWObjcurr_canvas.rotateImage(newOrientation);
    }
  }
  



  public void stepBack()
  {
    System.out.println("UNDO");
    if (!undoPageStack.isEmpty()) {
      redoPageStack.addFirst(this_interfacetbdPane.data_panel.a_window.getPage().clone());
      redoActiveStack.addFirst((Vector)selectedZones.clone());
      setNewPage((DLPage)undoPageStack.removeFirst());
      OCRInterface.this_interface.updateETextWindow();
      OCRInterface.this_interface.getCanvas().clearActiveZones();
      for (Zone active : (Vector)undoActiveStack.removeFirst()) {
        Zone temp = null;
        for (DLZone temp2 : currentState) {
          if (temp2.equals(active))
            temp = (Zone)temp2;
        }
        if (temp != null)
          OCRInterface.this_interface.getCanvas().addToSelected(temp);
      }
    }
    if ((!REACHED_MAX_UNDO_STEPS) && (undoPageStack.isEmpty()))
    {
      LoadDataFile ldf = ocrIF.workmodeProps[OCRInterface.currWorkmode]
        .getDataFileHash(ocrIF.currDatasetNum());
      if (ldf != null) {
        ldf.setModified(false);
      }
      



      if (OCRInterface.currDoc != null) {
        OCRInterface.currDoc.setModified(true);
      }
      OCRInterface.this_interface.updateCurrFilename();
    }
    



    this_interfacetbdPane.data_panel.a_window.showZoneInfo(ImageDisplay.activeZones);
    currentHWObjcurr_canvas.recenterZone();
    
    updateGroups();
  }
  



  public void stepForward()
  {
    System.out.println("REDO");
    if (!redoPageStack.isEmpty())
    {
      undoPageStack.addFirst(this_interfacetbdPane.data_panel.a_window.getPage().clone());
      undoActiveStack.addFirst((Vector)selectedZones.clone());
      

      setNewPage((DLPage)redoPageStack.removeFirst());
      OCRInterface.this_interface.updateETextWindow();
      OCRInterface.this_interface.getCanvas().clearActiveZones();
      for (Zone active : (Vector)redoActiveStack.removeFirst()) {
        Zone temp = null;
        for (DLZone temp2 : currentState) {
          if (temp2.equals(active))
            temp = (Zone)temp2;
        }
        if (temp != null) {
          OCRInterface.this_interface.getCanvas().addToSelected(temp);
        }
      }
    }
    LoadDataFile ldf = ocrIF.workmodeProps[OCRInterface.currWorkmode]
      .getDataFileHash(ocrIF.currDatasetNum());
    if (ldf != null) {
      ldf.setModified(true);
    }
    



    if (OCRInterface.currDoc != null) {
      OCRInterface.currDoc.setModified(true);
    }
    OCRInterface.this_interface.updateCurrFilename();
    this_interfacegetCanvasshapeVec.refreshCount();
    this_interfacetbdPane.data_panel.a_window.showZoneInfo(ImageDisplay.activeZones);
    currentHWObjcurr_canvas.recenterZone();
    
    updateGroups();
  }
  
  public void newPage() {
    undoPageStack.clear();
    undoActiveStack.clear();
    redoPageStack.clear();
    redoActiveStack.clear();
    REACHED_MAX_UNDO_STEPS = false;
  }
  
  private void updateGroups() {
    ocrIF.getCanvas().getGroupList().clear();
    for (DLZone zone : currentState.getAsVector()) {
      if (zone.isGroup()) {
        ocrIF.getCanvas().addToGroupList((Group)zone);
      }
    }
  }
}
