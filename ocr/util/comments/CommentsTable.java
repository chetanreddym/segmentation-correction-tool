package ocr.util.comments;

import gttool.document.DLZone;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import ocr.gui.MultiPagePanel;
import ocr.gui.OCRInterface;
import ocr.gui.Zone;
import ocr.gui.leftPanel.WorkmodeTable;
import ocr.manager.PropertiesInfoHolder;
import ocr.tif.ImageDisplay;
import ocr.tif.ImageDisplay.ZoneVector;
import ocr.util.UniqueZoneId;



public class CommentsTable
  extends JTable
{
  private static final long serialVersionUID = 1L;
  protected CommentsTableModel model;
  public static final int DOC = 0;
  public static final int PAGE = 1;
  public static final int ZONE = 2;
  public static final int DATE = 3;
  public static final int USER = 4;
  public static final int COMMENT = 5;
  public static final int ARCHIVED = 6;
  private Vector<GEDIComment> allComments;
  private String pageTxt = "(page)";
  private boolean showArchived;
  private int totalHiddenComments;
  
  public CommentsTable() {
    super.setAutoResizeMode(4);
    model = new CommentsTableModel();
    setModel(model);
    
    addColumn("Document");
    addColumn("Page");
    addColumn("Zone");
    addColumn("Date");
    addColumn("User");
    addColumn("Comment");
    addColumn("Archived");
    
    setRowHeight(getRowHeight() + 3);
    
    setHeaderRenderer();
    

    addMouseMotionListener(new TableMouseMoutionListener(null));
    addMouseListener(new TableClickListener(null));
    
    TableColumn col = getColumnModel().getColumn(3);
    
    col = getColumnModel().getColumn(1);
    col.setMaxWidth(50);
    
    col = getColumnModel().getColumn(2);
    col.setMaxWidth(50);
    
    col = getColumnModel().getColumn(4);
    col.setMaxWidth(100);
    
    col = getColumnModel().getColumn(6);
    col.setMaxWidth(70);
  }
  







  public CommentsTable(Vector<GEDIComment> allComments, boolean isHideArchived)
  {
    this();
    this.allComments = allComments;
    showArchived = isHideArchived;
    totalHiddenComments = 0;
    fillInModel();
    

    setRowSelectionAllowed(true);
    
    setAutoCreateColumnsFromModel(false);
    sortAllRowsBy(model, new AbsoluteComparator(0, true));
  }
  







  public void sortAllRowsBy(DefaultTableModel model, Comparator comparator)
  {
    Vector<Object> data = model.getDataVector();
    Collections.sort(data, comparator);
    model.fireTableStructureChanged();
    
    for (int i = 0; i < model.getColumnCount(); i++) {
      TableColumn column = getColumnModel().getColumn(i);
      column.setHeaderValue(getColumnName(column.getModelIndex()));
    }
  }
  
  protected void addColumn(String colName) {
    model.addColumn(colName);
  }
  
  protected void setHeaderRenderer() {
    JTableHeader header = getTableHeader();
    header.setFont(header.getFont().deriveFont(1));
    header.setReorderingAllowed(false);
  }
  
  public void addRow(Vector<Object> data) {
    model.addRow(data);
  }
  
  private void fillInModel() {
    for (GEDIComment c : allComments)
      if ((!showArchived) && (c.isArchived())) {
        totalHiddenComments += 1;
      }
      else
      {
        Vector<Object> data = new Vector();
        data.add(c);
        data.add(Integer.valueOf(c.getPageNum()));
        data.add(Integer.valueOf(c.getZoneID()));
        data.add(c.getDate());
        data.add(c.getUser());
        data.add(c.getComment());
        data.add(Boolean.valueOf(Boolean.parseBoolean(c.getArchived())));
        addRow(data);
      }
  }
  
  public String getToolTipText(MouseEvent e) {
    Point p = e.getPoint();
    int rowIndex = rowAtPoint(p);
    int colIndex = columnAtPoint(p);
    
    int realColumnIndex = convertColumnIndexToModel(colIndex);
    int realRowIndex = convertRowIndexToModel(rowIndex);
    

    UIManager.put("ToolTip.background", new ColorUIResource(Color.yellow));
    
    Object value = getModel().getValueAt(realRowIndex, realColumnIndex);
    
    if (realColumnIndex == 3) {
      SimpleDateFormat format = new SimpleDateFormat("MMM dd, yy h:mm:ss a");
      return format.format(value);
    }
    
    return wrap(value.toString());
  }
  




  protected String wrap(String strIn)
  {
    if ((strIn == null) || (strIn.trim().isEmpty())) {
      return "";
    }
    int w = 30;
    strIn = strIn.replaceAll("\n", " ");
    strIn = strIn.replaceAll("&", "&amp;");
    strIn = strIn.replaceAll(">", "&gt;");
    strIn = strIn.replaceAll("<", "&lt;");
    String strOut = "<HTML>";
    
    while (strIn.length() > w) {
      String cut = strIn.substring(0, w);
      strOut = strOut + cut + "<BR/>";
      strIn = strIn.substring(w, strIn.length());
    }
    
    strOut = strOut + strIn + "</HTML>";
    
    return strOut;
  }
  



  private void selectZone(String zoneID)
  {
    OCRInterface.this_interface.setFocusable(true);
    OCRInterface.this_interface.requestFocus();
    OCRInterface.this_interface
      .getUniqueZoneIdObj().searchZone(true, zoneID.trim());
    DLZone zone = OCRInterface.this_interface.getUniqueZoneIdObj().searchZone(zoneID, true);
    if ((zone != null) && (!((Zone)zone).isVisible()))
      JOptionPane.showMessageDialog(getParent(), 
        "Zone (id: " + zoneID + ", type: " + zone.getZoneType() + ") was found but it is invisible.\n" + 
        "Check the type visibilie and try again.", 
        "Search for zone", 
        2);
  }
  
  public int getTotalHidden() {
    return totalHiddenComments;
  }
  
  public void setTotalHidden(int num) {
    totalHiddenComments = num;
  }
  
  public CommentsTableModel getModel() {
    return model;
  }
  
  public TableCellRenderer getCellRenderer(int row, int col)
  {
    if (col == 6) {
      return new CheckBoxRenderer(getRowColor(row));
    }
    return new CustomCellRenderer();
  }
  



















  public static JOptionPane getNarrowOptionPane(int maxCharactersPerLineCount)
  {
    new JOptionPane()
    {
      private static final long serialVersionUID = 1L;
      int maxCharactersPerLineCount;
      
      public int getMaxCharactersPerLineCount()
      {
        return maxCharactersPerLineCount;
      }
    };
  }
  

  public void scrollTableToRow(int row)
  {
    if (!isCellVisible(this, row, 0)) {
      scrollToCenter(this, row, 0);
    }
  }
  
  public boolean isCellVisible(JTable table, int rowIndex, int vColIndex) {
    if (!(table.getParent() instanceof JViewport)) {
      return false;
    }
    JViewport viewport = (JViewport)table.getParent();
    


    Rectangle rect = table.getCellRect(rowIndex, vColIndex, true);
    

    Point pt = viewport.getViewPosition();
    



    rect.setLocation(x - x, y - y);
    

    return new Rectangle(viewport.getExtentSize()).contains(rect);
  }
  









  public void scrollToCenter(JTable table, int rowIndex, int vColIndex)
  {
    if (!(table.getParent() instanceof JViewport)) {
      return;
    }
    JViewport viewport = (JViewport)table.getParent();
    


    Rectangle rect = table.getCellRect(rowIndex, vColIndex, true);
    

    Rectangle viewRect = viewport.getViewRect();
    



    rect.setLocation(x - x, y - y);
    

    int centerX = (width - width) / 2;
    int centerY = (height - height) / 2;
    


    if (x < centerX) {
      centerX = -centerX;
    }
    if (y < centerY) {
      centerY = -centerY;
    }
    rect.translate(centerX, centerY);
    

    viewport.scrollRectToVisible(rect);
  }
  






  public void selectRowsForCurrDoc()
  {
    ImageDisplay.ZoneVector selectedZones = ImageDisplay.activeZones;
    int currPage = Integer.parseInt(CommentsParser.getCurrPageNum());
    String currDoc = CommentsParser.getCurrXml();
    
    if (currDoc == null) {
      return;
    }
    clearSelection();
    int realRowIndex;
    if ((selectedZones == null) || (selectedZones.isEmpty())) {
      for (int i = 0; i < model.getRowCount(); i++) {
        realRowIndex = convertRowIndexToModel(i);
        GEDIComment c = (GEDIComment)model.getValueAt(realRowIndex, 0);
        if ((c.getXml().equals(currDoc)) && (c.getPageNum() == currPage) && (c.getZoneID() == 0)) {
          addRowSelectionInterval(realRowIndex, realRowIndex);
          repaint();
        }
      }
    } else {
      int i;
      for (realRowIndex = selectedZones.iterator(); realRowIndex.hasNext(); 
          i < getRowCount())
      {
        Zone zone = (Zone)realRowIndex.next();
        i = 0; continue;
        int realRowIndex = convertRowIndexToModel(i);
        GEDIComment c = (GEDIComment)model.getValueAt(realRowIndex, 0);
        if ((c.getXml().equals(currDoc)) && (c.getPageNum() == currPage) && (zone.getZoneId() == c.getZoneID())) {
          addRowSelectionInterval(realRowIndex, realRowIndex);
          repaint();
        }
        i++;
      }
    }
    







    scrollTableToRow(getNewestAmongSelected());
  }
  




  private int getNewestAmongSelected()
  {
    int[] selectedRows = getSelectedRows();
    if ((selectedRows == null) || (selectedRows.length == 0)) {
      return -1;
    }
    int row = convertRowIndexToModel(selectedRows[0]);
    Timestamp newest = (Timestamp)model.getValueAt(row, 3);
    
    int rowIndex = row;
    
    for (int i = 0; i < selectedRows.length; i++) {
      row = convertRowIndexToModel(selectedRows[i]);
      if (((Timestamp)model.getValueAt(row, 3)).after(newest)) {
        newest = (Timestamp)model.getValueAt(row, 3);
        rowIndex = row;
      }
    }
    
    return rowIndex;
  }
  











  private Color getRowColor(int row)
  {
    row = convertRowIndexToModel(row);
    Color color = getBackground();
    
    if ((OCRInterface.this_interface.getCommentsWindow() != null) && (!OCRInterface.this_interface.getCommentsWindow().isShowAll())) {
      return color;
    }
    GEDIComment comm = (GEDIComment)getValueAt(row, 0);
    String currDoc = CommentsParser.getCurrXml();
    
    if ((currDoc != null) && (currDoc.equals(comm.getXml())))
    {

      color = new Color(247, 242, 226);
    }
    

    return color;
  }
  
  private class TableMouseMoutionListener implements MouseMotionListener { private TableMouseMoutionListener() {}
    
    public void mouseDragged(MouseEvent e) { clearSelection(); }
    


    public void mouseMoved(MouseEvent e) {}
  }
  


  private class TableClickListener
    implements MouseListener
  {
    private TableClickListener() {}
    


    public void mousePressed(MouseEvent e)
    {
      Point p = e.getPoint();
      int colIndex = columnAtPoint(p);
      int realColumnIndex = convertColumnIndexToModel(colIndex);
      int rowIndex = rowAtPoint(p);
      int realRowIndex = convertRowIndexToModel(rowIndex);
      

      if ((rightMouseClicked(e)) && (realColumnIndex == 5)) {
        GEDIComment c = (GEDIComment)getModel().getValueAt(realRowIndex, 0);
        String msg = "Document: " + c.getXml() + "\n" + 
          "Page #:       " + c.getPageNum() + "\n";
        
        if (c.getZoneID() != 0) {
          msg = msg + "Zone #:        " + c.getZoneID() + "\n";
        }
        msg = msg + "Comment:  " + c.getComment();
        
        JOptionPane pane = CommentsTable.getNarrowOptionPane(100);
        pane.setMessage(msg);
        pane.setMessageType(-1);
        JDialog dialog = pane.createDialog(getParent(), "Comment");
        dialog.setLocationRelativeTo(getParent());
        dialog.setVisible(true);

      }
      else if ((e.getClickCount() == 1) || (e.getClickCount() == 2)) {
        OCRInterface ocrIF = OCRInterface.this_interface;
        int mode = 0;
        
        GEDIComment c = (GEDIComment)getValueAt(realRowIndex, 0);
        int pageID = c.getPageNum();
        int zoneID = c.getZoneID();
        

        if (!c.getXmlNameWithoutExt().equals(ocrTable.selFileName)) {
          String path = OCRInterface.getCurrentXmlDir() + 
            OCRInterface.getXmlDirName() + 
            File.separator + 
            c.getComposedImageFileName();
          int index = workmodeProps[mode].getImageIndex(path);
          ocrTable.processSelectionEvent(index, 1, false);
        }
        


        if (zoneID == 0) {
          OCRInterface.this_interface.getMultiPagePanel().selectPage(pageID);
        } else
          CommentsTable.this.selectZone(Integer.toString(zoneID));
      }
    }
    
    public void mouseReleased(MouseEvent e) {
      if (getSelectedRowCount() == 0)
        selectRowsForCurrDoc(); }
    
    public void mouseEntered(MouseEvent e) {}
    
    public void mouseExited(MouseEvent e) {}
    
    public void mouseClicked(MouseEvent e) {}
    
    private boolean rightMouseClicked(InputEvent e) { return (e.getModifiers() & 0x4) != 0; }
  }
  


  public class CustomCellRenderer
    extends DefaultTableCellRenderer
  {
    private static final long serialVersionUID = 1L;
    

    public CustomCellRenderer() {}
    

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
      Component cell = super.getTableCellRendererComponent(
        table, value, isSelected, hasFocus, row, column);
      
      cell.setFont(getFont().deriveFont(0));
      
      if (column == 3) {
        SimpleDateFormat format = new SimpleDateFormat("MMM dd, yy h:mm:ss a");
        setText(format.format(value));
      }
      else if ((column == 2) && (((Integer)value).intValue() == 0)) {
        setText(pageTxt);
      }
      
      ((JLabel)cell).setHorizontalAlignment(2);
      
      Color color = CommentsTable.this.getRowColor(row);
      
      row = convertRowIndexToModel(row);
      
      if ((column >= 0) && (row == CommentsTable.this.getNewestAmongSelected())) {
        cell.setForeground(Color.red);
      } else {
        cell.setForeground(getForeground());
      }
      if (!isSelected) {
        cell.setBackground(color);
      }
      
      return cell;
    }
  }
  


  public class CheckBoxRenderer
    extends JCheckBox
    implements TableCellRenderer
  {
    private static final long serialVersionUID = 1L;
    
    private Color color = null;
    
    public CheckBoxRenderer(Color color) { this.color = color; }
    


    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col)
    {
      if (value != null) {
        setSelected(((Boolean)value).booleanValue());
      }
      Color selectionColor = (Color)UIManager.get("Table.selectionBackground");
      
      if (isSelected) {
        setBackground(selectionColor);
      } else {
        setBackground(color);
      }
      setHorizontalAlignment(0);
      return this;
    }
  }
  

  class AbsoluteComparator
    implements Comparator<Object>
  {
    protected boolean isSortAscending;
    
    protected int sortingCol;
    

    public AbsoluteComparator(int col, boolean sortAsc)
    {
      isSortAscending = sortAsc;
      sortingCol = col;
    }
    
    public int compare(Object v1_in, Object v2_in)
    {
      int result = model.custom_compare(sortingCol, v1_in, v2_in);
      
      if (!isSortAscending)
        result = -result;
      return result;
    }
    
    public boolean equals(Object obj) {
      if ((obj instanceof AbsoluteComparator)) {
        AbsoluteComparator compObj = (AbsoluteComparator)obj;
        return isSortAscending == isSortAscending;
      }
      return false;
    }
  }
  

  class AdvancedComparator
    implements Comparator<Object>
  {
    protected boolean isSortAsc;
    
    protected int sortingCol;
    
    public AdvancedComparator(int col, boolean sortAsc)
    {
      isSortAsc = sortAsc;
      sortingCol = col;
    }
    
    public int compare(Object v1_in, Object v2_in)
    {
      Vector<Object> v1 = (Vector)v1_in;
      Vector<Object> v2 = (Vector)v2_in;
      
      String xml1 = ((GEDIComment)v1.get(0)).getXml().toLowerCase();
      String xml2 = ((GEDIComment)v2.get(0)).getXml().toLowerCase();
      
      int result = 0;
      
      if (sortingCol == 0) {
        result = model.custom_compare(0, v1_in, v2_in);
      } else if (xml1.equals(xml2)) {
        result = model.custom_compare(sortingCol, v1_in, v2_in);
      }
      if (!isSortAsc)
        result = -result;
      return result;
    }
    
    public boolean equals(Object obj)
    {
      if ((obj instanceof AdvancedComparator)) {
        AdvancedComparator compObj = (AdvancedComparator)obj;
        return isSortAsc == isSortAsc;
      }
      return false;
    }
  }
}
