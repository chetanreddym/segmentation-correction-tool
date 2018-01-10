package ocr.gui.leftPanel;

import gttool.document.DLPage;
import gttool.document.DLZone;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowListener;
import java.util.Vector;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.RootPaneContainer;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.plaf.metal.MetalToolBarUI;
import ocr.gui.Group;
import ocr.gui.OCRInterface;
import ocr.gui.Zone;
import ocr.tif.ImageDisplay;
import ocr.tif.ImageDisplay.ZoneVector;
import ocr.tif.ImageReaderDrawer;





public class AttributeWindow
  extends JPanel
  implements TableModelListener
{
  AttributeWindow this_instance;
  private JTabbedPane tabbedPane = new JTabbedPane();
  




  protected AttributeTable pageTable = new AttributeTable(1);
  



  protected AttributeTable zoneTable = new AttributeTable(0);
  



  protected AttributeTable elementsTable = new AttributeTable(2);
  
  public ElectronicTextDisplayer eTextWindow = new ElectronicTextDisplayer();
  private JPanel eTextPanel = new JPanel();
  
  private JSplitPane sp;
  
  public static JPanel imgchip = new JPanel();
  
  int dividerLocation = 100;
  
  public AttributeWindow() {
    this_instance = this;
    
    setLayout(new BorderLayout());
    
    tabbedPane.addTab("Page", pageTable);
    tabbedPane.addTab("Zone", zoneTable);
    tabbedPane.addTab("Elements", elementsTable);
    
    eTextWindow.setUI(new MetalToolBarUI() {
      public static final String FRAME_IMAGEICON = "ToolBar.frameImageIcon";
      
      protected RootPaneContainer createFloatingWindow(JToolBar tool_bar) {
        JFrame frame = new JFrame(tool_bar.getName());
        frame.setResizable(true);
        Icon icon = UIManager.getIcon("ToolBar.frameImageIcon");
        if ((icon instanceof ImageIcon)) {
          Image iconImage = ((ImageIcon)icon).getImage();
          frame.setIconImage(iconImage);
        }
        WindowListener windowListener = createFrameListener();
        frame.addWindowListener(windowListener);
        frame.setPreferredSize(new Dimension(400, 500));
        return frame;
      }
    });
    eTextPanel.add(eTextWindow);
    eTextPanel.addComponentListener(new ComponentAdapter()
    {
      public void componentResized(ComponentEvent arg0) {
        eTextWindow.setPreferredSize(new Dimension(eTextPanel.getWidth(), eTextPanel.getHeight()));
        eTextWindow.revalidate();
      }
      

    });
    sp = new JSplitPane(0, tabbedPane, null);
    
    sp.setDividerLocation(dividerLocation);
    


    add(sp, "Center");
    
    setETextWindow(false);
  }
  







  public void setPage(DLPage p)
  {
    pageTable.setPage(p);
  }
  



  public DLPage getPage()
  {
    return pageTable.getPage();
  }
  
  public AttributeTable getTable() {
    return pageTable;
  }
  
  public JPanel getImgChip() {
    return imgchip;
  }
  







  public void showZoneInfo(Vector<Zone> activeZones)
  {
    Vector<Zone> groups = currentHWObjcurr_canvas.getActiveGroups();
    
    if ((activeZones == null) || (activeZones.size() == 0)) {
      tabbedPane.setSelectedIndex(0);
    } else {
      tabbedPane.setSelectedIndex(1);
    }
    
    if (groups.isEmpty()) {
      tabbedPane.setEnabledAt(2, false);
    } else {
      tabbedPane.setEnabledAt(2, true);
    }
    if (groups.size() == 1) {
      pageTable.showZoneInfo(groups);
      zoneTable.showZoneInfo(groups);
      
      elementsTable.showMultipleZonesInfo_help(getGroupsElements(groups));
      elementsTable.showMultipleZonesInfo(getGroupsElements(groups), 2);
    }
    else if (groups.size() > 1) {
      pageTable.showZoneInfo(groups);
      zoneTable.showMultipleZonesInfo(groups, 0);
      
      elementsTable.showMultipleZonesInfo_help(getGroupsElements(groups));
      elementsTable.showMultipleZonesInfo(getGroupsElements(groups), 2);
    }
    else if (activeZones.size() > 1) {
      zoneTable.showMultipleZonesInfo(activeZones, 0);
    } else {
      pageTable.showZoneInfo(activeZones);
      zoneTable.showZoneInfo(activeZones);
    }
  }
  
  private Vector<Zone> getGroupsElements(Vector<Zone> groups) {
    Vector<Zone> elements = new Vector();
    for (Zone z : groups) {
      elements.addAll(((Group)z).getZonesOfGroup_asVector());
    }
    return elements;
  }
  



  public void updateMultipleSelectedZonesCount(Vector<Zone> activeZones)
  {
    zoneTable.showMultipleZonesInfo_help(activeZones);
  }
  


























  public void tableChanged(TableModelEvent e)
  {
    pageTable.tableChanged(e);
  }
  
  public void setTextWindowText(String string) {
    eTextWindow.setText(string);
  }
  
  public void setETextWindowZones(Vector<DLZone> asVector, ImageDisplay.ZoneVector selectedZones)
  {
    eTextWindow.setCurrentZones(asVector, selectedZones);
  }
  
  public String getETextWindowSelectedText() {
    return eTextWindow.getSelectedText();
  }
  
  public int getETextWindowStringLength() {
    return eTextWindow.getNumTextLines();
  }
  
  public void updateETextWindowPane() {
    eTextWindow.paneUpdate();
  }
  
  public void setETextWindow(boolean enable) {
    eTextWindow.setVisible(enable);
    eTextPanel.setVisible(enable);
    
    sp.setDividerLocation(dividerLocation);
  }
  
  public void clearETextSelection() {
    eTextWindow.removeSelection();
  }
  
  public int getNumETextLines() {
    return eTextWindow.getNumTextLines();
  }
  
  public AttributeTable getAttributeTable() {
    return elementsTable;
  }
}
