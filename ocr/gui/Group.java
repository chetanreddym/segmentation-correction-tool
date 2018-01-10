package ocr.gui;

import gttool.document.DLZone;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import ocr.gui.leftPanel.DatasetSpecificToolPanel;
import ocr.gui.leftPanel.LeftPanel;
import ocr.tif.ImageDisplay;
import ocr.tif.ImageReaderDrawer;
import ocr.util.BidiString;

public class Group extends Zone
{
  private ArrayList<Zone> zonesOfGroup = null;
  
  public Group(int xIn, int yIn, float scale) {
    super(xIn, yIn, scale);
    currentHWObjcurr_canvas.addToGroupList(this);
  }
  
  public Group(String zoneID, int x, int y, int w, int h) {
    super(zoneID, x, y, w, h);
    currentHWObjcurr_canvas.addToGroupList(this);
  }
  
  public Group(int x, int y, int w, int h) {
    super(x, y, w, h);
    currentHWObjcurr_canvas.addToGroupList(this);
  }
  
  public Group(ArrayList<Zone> zones) {
    super(0, 0, 1, 1);
    zonesOfGroup = zones;
    resizeGroupBox();
    updateTag_GroupElements();
    currentHWObjcurr_canvas.addToGroupList(this);
  }
  
  public boolean containsZone(Zone zone)
  {
    if (zonesOfGroup == null) {
      return false;
    }
    return zonesOfGroup.contains(zone);
  }
  
  private void setUpZonesOfGroup() {
    if (zonesOfGroup != null) {
      return;
    }
    String groupElements = (String)getZoneTags().get("elements");
    
    if (groupElements == null) {
      return;
    }
    String[] zonesId = groupElements.split(";");
    zonesOfGroup = new ArrayList(zonesId.length);
    for (String id : zonesId) {
      if (!id.isEmpty())
      {

        Vector<DLZone> allZones = OCRInterface.this_interface.getCanvas()
          .getShapeVec().getAsVector();
        for (DLZone zone : allZones) {
          if ((zoneID.equals(id)) && (doesTypeBelongToGroup((Zone)zone))) {
            addToGroup((Zone)zone, true);
          }
        }
      }
    }
  }
  




  public void deleteGroup()
  {
    System.out.println("deleteGroup");
    if (zonesOfGroup != null)
      zonesOfGroup.clear();
    currentHWObjcurr_canvas.deleteZone(this);
    currentHWObjcurr_canvas.getGroupList().remove(this);
    currentHWObjcurr_canvas.paintCanvas();
    OCRInterface.this_interface.getUniqueZoneIdObj().updateMaxZoneId();
  }
  
  public void resizeGroupBox()
  {
    if (zonesOfGroup == null) {
      setUpZonesOfGroup();
    }
    
    Vector<Integer> x = new Vector();
    Vector<Integer> y = new Vector();
    
    for (Zone z : zonesOfGroup) {
      x.add(Integer.valueOf(get_Boundsx));
      x.add(Integer.valueOf(get_Boundsx + get_Boundswidth));
      y.add(Integer.valueOf(get_Boundsy));
      y.add(Integer.valueOf(get_Boundsy + get_Boundsheight));
    }
    
    if ((x.isEmpty()) || (y.isEmpty())) {
      deleteGroup();
      return;
    }
    

    int gap = 10;
    
    int xMin = ((Integer)Collections.min(x)).intValue();
    int xMax = ((Integer)Collections.max(x)).intValue();
    int yMin = ((Integer)Collections.min(y)).intValue();
    int yMax = ((Integer)Collections.max(y)).intValue();
    
    lt.x = (xMin - gap);
    lt.y = (yMin - gap);
    rb.x = (xMax + gap);
    rb.y = (yMax + gap);
    
    origin = new Point(get_lt_x(), get_lt_y());
    width = (get_rb_x() - get_lt_x());
    height = (get_rb_y() - get_lt_y());
    


    super.resizeGroupToWhichZoneBelongs();
  }
  
  private void updateTag_GroupElements() {
    getZoneTags().put("elements", getZonesOfGroup_asString());
  }
  
  public void addToGroup(Zone zoneToAdd) {
    addToGroup(zoneToAdd, false);
  }
  


  public void addToGroup(Zone zoneToAdd, boolean initialSetUp)
  {
    if (zoneID.equals(zoneID)) {
      return;
    }
    if (zonesOfGroup == null) {
      zonesOfGroup = new ArrayList();
    }
    
    if (initialSetUp) {
      if (doesTypeBelongToGroup(zoneToAdd)) {
        add(zoneToAdd);
      }
    }
    else if (isGroupOfAny()) {
      addToGroupOfAny(zoneToAdd);
    } else if (doesTypeBelongToGroup(zoneToAdd)) {
      add(zoneToAdd);
    }
    
    updateTag_GroupElements();
  }
  
  public void addToGroupOfAny(Zone zoneToAdd) {
    Vector<Group> parents = OCRInterface.this_interface.getCanvas()
      .getGroupsToWhichZoneBelongs(zoneToAdd);
    
    if ((parents == null) || (parents.isEmpty())) {
      add(zoneToAdd);
    } else {
      for (Group group : parents) {
        if (!zonesOfGroup.contains(group)) {
          add(zoneToAdd);
        }
      }
    }
    if (zoneToAdd.isGroup())
      zonesOfGroup.removeAll(((Group)zoneToAdd).getZonesOfGroup());
  }
  
  private void add(Zone zoneToAdd) {
    if (zonesOfGroup == null) {
      zonesOfGroup = new ArrayList();
    }
    if ((zonesOfGroup.isEmpty()) || (!zonesOfGroup.contains(zoneToAdd))) {
      zonesOfGroup.add(zoneToAdd);
    }
  }
  






























  public boolean isGroupOfAny()
  {
    Set<String> typesOfGroup = ocrIF.tbdPane.data_panel.t_window.getTypesOfGroup(getZoneType());
    
    return (typesOfGroup.size() == 1) && (((String)typesOfGroup.iterator().next()).equals("ANY"));
  }
  
  public void removeFromGroup(Zone zoneToRemove)
  {
    if (zonesOfGroup != null) {
      zonesOfGroup.remove(zoneToRemove);
    }
    resizeGroupBox();
    
    updateTag_GroupElements();
  }
  
  public boolean doesTypeBelongToGroup(Zone zoneToAdd)
  {
    Set<String> typesOfGroup = ocrIF.tbdPane.data_panel.t_window.getTypesOfGroup(getZoneType());
    
    if ((typesOfGroup.size() == 1) && (((String)typesOfGroup.iterator().next()).equals("ANY"))) {
      return true;
    }
    return typesOfGroup.contains(zoneToAdd.getZoneType());
  }
  
  public boolean doesTypeBelongToGroup(String type)
  {
    Set<String> typesOfGroup = ocrIF.tbdPane.data_panel.t_window.getTypesOfGroup(getZoneType());
    
    if ((typesOfGroup.size() == 1) && (((String)typesOfGroup.iterator().next()).equals("ANY"))) {
      return true;
    }
    return typesOfGroup.contains(type);
  }
  
  public ArrayList<Zone> getZonesOfGroup() {
    return zonesOfGroup;
  }
  
  public Vector<Zone> getZonesOfGroup_asVector() {
    if (zonesOfGroup == null)
      return new Vector();
    Vector<Zone> zonesVector = new Vector(zonesOfGroup.size());
    zonesVector.addAll(zonesOfGroup);
    return zonesVector;
  }
  
  public String getZonesOfGroup_asString() {
    if ((zonesOfGroup == null) || (zonesOfGroup.isEmpty())) {
      return null;
    }
    String zonesStr = "";
    
    Vector<Integer> ids = new Vector(zonesOfGroup.size());
    
    for (Zone zone : zonesOfGroup) {
      ids.add(Integer.valueOf(zoneID));
    }
    Collections.sort(ids);
    
    for (Integer id : ids) {
      zonesStr = zonesStr + Integer.toString(id.intValue()) + ";";
    }
    if (zonesStr.endsWith(";")) {
      zonesStr = zonesStr.substring(0, zonesStr.length() - 1);
    }
    return zonesStr;
  }
  
  public void deselectZones() {
    if (zonesOfGroup == null) {
      return;
    }
    zonesOfGroup.clear();
    ImageDisplay.activeZones.clear();
    ImageDisplay.activeZones.add(0, this);
  }
  
  public Group clone_zone() {
    Group z = new Group(zoneID, origin.x, origin.y, width, height);
    zonePage = zonePage;
    z.getZoneTags().putAll(getZoneTags());
    parentZone = parentZone;
    nextZone = nextZone;
    previousZone = previousZone;
    caret = caret;
    
    zonesOfGroup = null;
    return z;
  }
  
  public void editSelectedCorener(int xIn, int yIn, float scale)
  {
    super.editSelectedCorener(xIn, yIn, scale);
    
    deselectZones();
    ocr.manager.zones.ZonesManager vecToSelectFrom = currentHWObjcurr_canvas.shapeVec;
    
    Vector<DLZone> zonesUnderSelection = vecToSelectFrom
      .getIntersectingZones(this);
    

    for (Iterator iter = zonesUnderSelection.iterator(); iter.hasNext();) {
      Zone z = (Zone)iter.next();
      addToGroup(z);
    }
    currentHWObjcurr_canvas.addToSelected(this);
    currentHWObjcurr_canvas.paintCanvas();
  }
  
  public boolean isGroup() {
    return true;
  }
  
  public static void showWarning(boolean zones, boolean groups) {
    String value = "";
    if (zones) {
      value = "zones";
    } else if (groups)
      value = "groups";
    javax.swing.JOptionPane.showMessageDialog(
      OCRInterface.this_interface, 
      "Type cannot be reset for some of selected " + value + "\n" + 
      "You need to ungroup zones first.", 
      "Type reset", 
      2);
  }
  

  public void draw(Graphics g, float scale, boolean isSelected, Point p, int electronicTextSize)
  {
    setUpZonesOfGroup();
    
    int cornerSize = 3;
    
    Graphics2D g2d = (Graphics2D)g;
    
    float[] dashes = { 3.0F, 3.0F, 3.0F, 3.0F };
    
    BasicStroke bs_dash = new BasicStroke(ocrIF.getLineThickness(), 
      0, 
      0, 
      10.0F, dashes, 0.0F);
    
    g2d.setStroke(bs_dash);
    










    g.drawRect((int)(lt.x * scale), (int)(lt.y * scale), 
      (int)((rb.x - lt.x) * scale), (int)((rb.y - lt.y) * scale));
    
    g2d.setStroke(new BasicStroke(ocrIF.getLineThickness()));
    
    if (isSelected) {
      g2d.setStroke(bs_dash);
      










      Color cl = getZoneColor();
      g.setColor(cl);
      g.drawRect((int)(lt.x * scale), (int)(lt.y * scale), 
        (int)((rb.x - lt.x) * scale), (int)((rb.y - lt.y) * scale));
      

      Point[] vertex = getZoneCorners();
      for (Point pt : vertex)
      {
        int radius = 2;
        g.fillOval((int)(x * scale) - radius, (int)(y * scale) - radius, radius * 2, radius * 2);
      }
      
      g2d.setStroke(new BasicStroke(ocrIF.getLineThickness()));
    }
    





    if ((p != null) && (isSelected))
    {

      int radius = 4;
      Point closestPt = null;
      
      closestPt = closeTo(p, scale);
      
      if (closestPt != null)
      {
        g.fillOval((int)(x * scale) - radius, 
          (int)(y * scale) - radius, 
          radius * 2, radius * 2);
      }
    }
    


    if ((selPt != null) && (isSelected)) {
      int cSize = 4;
      
      g.fillRect((int)(selPt.x * scale) - cSize, 
        (int)(selPt.y * scale) - cSize, 
        cSize * 2, cSize * 2);
    }
    

    switch (selectedCorner) {
    case -1: 
      break;
    case 0: 
      g.drawRect((int)(lt.x * scale) - cornerSize, (int)(lt.y * scale) - 
        cornerSize, cornerSize * 2, cornerSize * 2);
      break;
    
    case 1: 
      g.drawRect((int)(rb.x * scale) - cornerSize, (int)(lt.y * scale) - 
        cornerSize, cornerSize * 2, cornerSize * 2);
      break;
    
    case 2: 
      g.drawRect((int)(lt.x * scale) - cornerSize, (int)(rb.y * scale) - 
        cornerSize, cornerSize * 2, cornerSize * 2);
      
      break;
    
    case 3: 
      g.drawRect((int)(rb.x * scale) - cornerSize, (int)(rb.y * scale) - 
        cornerSize, cornerSize * 2, cornerSize * 2);
    }
    
    
    String zoneName = getAttributeValue("gedi_type");
    if ((!isIncomplete) && (ocrIF.getShowZoneTypes()) && (zoneName != null))
    {

      int size = currentHWObjcurr_canvas.getElectronicTextSize();
      
      Font currFont = g2d.getFont();
      Font updatedFont = new Font(currFont.getName(), 1, size);
      g2d.setFont(updatedFont);
      
      Color currentColor = g2d.getColor();
      
      int widthOfText = g2d.getFontMetrics().stringWidth(zoneName);
      



      float x = (lt.x + width / 2 - widthOfText / scale / 2.0F) * scale;
      float y = (lt.y + height / 2 + size / 2) * scale;
      
      g2d.drawString(zoneName, x, y);
      g2d.setColor(currentColor);
      g2d.setFont(currFont);
    }
    if ((hasContents()) && 
      (!offsetsReady()) && (!isIncomplete)) {
      String contents = getContents();
      BidiString bs = new BidiString(contents, 0);
      int direction = bs.getDirection();
      


      Font currFont = g2d.getFont();
      Font updatedFont = new Font(currFont.getName(), currFont.getStyle(), electronicTextSize);
      g2d.setFont(updatedFont);
      int x;
      int x;
      if (direction == 0) {
        x = (int)(lt.x * scale);
      } else {
        x = (int)(rb.x * scale - g2d.getFontMetrics().stringWidth(contents));
      }
      int y = (int)(lt.y * scale - g2d.getFontMetrics().getHeight());
      
      g2d.setFont(currFont);
      
      displayContents(g, scale, isSelected, electronicTextSize, x, y);
    }
    if ((offsetsReady()) && (!isIncomplete))
    {
      String contents = getContents();
      
      int tempint = ((Integer)OCRInterface.lineGTModeMap.get(getAttributeValue("segmentation").trim())).intValue();
      
      if (getAttributeValue("segmentation").equals("line")) {
        isHoriz = 4;
      }
      else
        isHoriz = 0;
      BidiString bs = new BidiString(contents, tempint);
      int direction = bs.getDirection();
      
      int maxOffsets = bs.size();
      

      String firstLine = null;
      if (direction == 0) {
        firstLine = "0";
      } else {
        firstLine = String.valueOf(rb.x - lt.x);
      }
      
      ArrayList<String> offsetsA = getOffsetsArray(firstLine, direction, maxOffsets);
      




      if (offsetsA.size() > maxOffsets) {
        System.out.println(":" + offsetsA.size());
        
        String offsets = new String();
        
        if ((maxOffsets == 0) || (maxOffsets == 1)) {
          offsets = "";
        } else {
          offsetsA.remove(firstLine);
          Iterator<String> it = offsetsA.iterator();
          int i = 0;
          if (direction == 1) {
            for (int j = 0; j < offsetsA.size() - (maxOffsets - 1); j++) {
              it.next();
            }
          }
          while (i < maxOffsets - 1) {
            offsets = offsets + (String)it.next() + ", ";
            i++;
          }
          if (!offsets.equals("")) {
            offsets = offsets.substring(0, offsets.length() - 2);
          }
        }
        setAttributeValue("offsets", offsets);
        this_interfacetbdPane.data_panel.a_window
          .showZoneInfo(ImageDisplay.getActiveZones());
        
        offsetsA = getOffsetsArray(firstLine, direction, maxOffsets);
      }
      
      int thisLtX = lt.x;
      
      ArrayList<String> newOffsetsA = new ArrayList(offsetsA);
      
      if (maxOffsets >= 0) {
        int index = 0;
        for (int i = 0; i < offsetsA.size(); i++)
        {
          int offset;
          
          if (direction == 0) {
            int offset = Integer.valueOf((String)offsetsA.get(i)).intValue();
            
            if ((leftResize) && (i != 0))
            {
              offset += delta;
              if (offset <= 0)
                offset = 1;
              newOffsetsA.set(i, Integer.toString(offset));
            }
          }
          else {
            offset = Integer.valueOf((String)offsetsA.get(offsetsA.size() - 1 - i)).intValue();
            
            if ((leftResize) && (i != 0))
            {
              offset += delta;
              if (offset <= 0)
                offset = 1;
              newOffsetsA.set(offsetsA.size() - 1 - i, Integer.toString(offset));
            }
          }
          


          if (OCRInterface.currOppmode != 10)
          {
            String txt = bs.getNext(offsetsA.size() - (i + 1));
            index += txt.length() + 1;
            
            Font currFont = g2d.getFont();
            
            Font updatedFont = new Font(currFont.getName(), currFont.getStyle(), electronicTextSize);
            g2d.setFont(updatedFont);
            
            Color currentColor = g2d.getColor();
            int widthOfText = g2d.getFontMetrics().stringWidth(txt);
            int heightOfText = g2d.getFontMetrics().getHeight();
            
            java.text.AttributedCharacterIterator text = correctPairedPunctuation_and_highlightSelectedWord(bs, 
              txt, updatedFont, 0, -1);
            
            float x;
            float x;
            if (direction == 0) {
              x = (thisLtX + offset) * scale;
            } else {
              x = (thisLtX + offset) * scale - widthOfText;
            }
            float y = lt.y * scale - heightOfText;
            
            if ((!ocrIF.getShowTextOnAllZones()) && (isSelected))
            {
              g2d.fillRect((int)x - 1, (int)(y - 2.0F), widthOfText + 2, heightOfText);
              
              if (isLightColor(currentColor)) {
                g2d.setColor(Color.black);
              } else {
                g2d.setColor(Color.white);
              }
              
              g2d.drawString(text, x, y + heightOfText - 8.0F);
            }
            else if (ocrIF.getShowTextOnAllZones())
            {
              g2d.fillRect((int)x - 1, (int)(y - 2.0F), widthOfText + 2, heightOfText);
              
              if (isLightColor(currentColor)) {
                g2d.setColor(Color.black);
              } else {
                g2d.setColor(Color.white);
              }
              
              g2d.drawString(text, x, y + heightOfText - 8.0F);
            }
            
            g2d.setColor(currentColor);
            


            if ((isSelected) && (index > contents.length() - caret) && (index - txt.length() - 1 <= contents.length() - caret)) {
              int currCaret = caret;
              caret = (index - contents.length() + caret - 1);
              drawCaret(g2d, (int)((thisLtX + offset) * scale), (int)(lt.y * scale), txt);
              caret = currCaret;
            }
            


            g2d.setFont(currFont);
          }
        }
      }
    }
  }
}
