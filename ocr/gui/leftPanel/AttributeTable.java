package ocr.gui.leftPanel;

import gttool.document.DLPage;
import gttool.document.DLZone;
import gttool.misc.AttributeValueList;
import gttool.misc.TypeAttributeEntry;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;
import javax.swing.DefaultCellEditor;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import ocr.gui.LoadDataFile;
import ocr.gui.OCRInterface;
import ocr.gui.ReadingOrder;
import ocr.gui.Zone;
import ocr.manager.zones.ZonesManager;
import ocr.tif.ImageDisplay;
import ocr.tif.ImageDisplay.ZoneVector;
import ocr.tif.ImageReaderDrawer;
import ocr.util.AttributeConfigUtil;
import ocr.util.TableUtils;
import ocr.util.TableUtils.JTableA;
import ocr.util.TableUtils.RowEditorModel;

public class AttributeTable extends javax.swing.JPanel implements TableModelListener, KeyListener
{
  public static final int ZONE = 0;
  public static final int PAGE = 1;
  public static final int ELEMENTS = 2;
  public static final String[] columnNames = { "Attribute", "Value" };
  


  public static final int ATTRIBUTE = 0;
  


  public static final int VALUE = 1;
  


  AttributeTable this_instance;
  

  protected TableUtils.JTableA table = null;
  



  protected MyTableModel dm = new MyTableModel();
  









  private Font curr_font = new Font("Dialog", 0, 14);
  



  private static final String DIMENSION = "(col,row)(width,height)";
  



  private static final long serialVersionUID = -2577764030636589756L;
  



  private DLPage currentPage = null;
  



  private Zone currentZone = null;
  
  private Object[] nullRow = { "No Selection", "" };
  
  private Object[] typeRow = new Object[2];
  
  private Object[] dimRow = new Object[2];
  
  private Object[] idRow = new Object[2];
  
  private Object[] nextZoneIdRow = new Object[2];
  
  private HashSet<String> atts = new HashSet();
  
  private int type;
  
  private boolean multipleSelections = false;
  
  private Vector<Zone> multipleSelectedZones = null;
  
  private String multiSelection = "Multiple Selections";
  
  private Set<Integer> rowsToColor = null;
  

  private JPopupMenu popup;
  
  private String noNextZone = "-1";
  



  public AttributeTable(int type)
  {
    this_instance = this;
    this.type = type;
    
    init();
    




    InputMap im = table.getInputMap(1);
    KeyStroke ctrl_A = KeyStroke.getKeyStroke(65, 2);
    im.put(ctrl_A, "none");
    

    setLayout(new BorderLayout());
    
    add(new JScrollPane(table), "Center");
    setCellRenderer();
  }
  




  public void setPage(DLPage p)
  {
    currentPage = p;
  }
  



  public DLPage getPage()
  {
    return currentPage;
  }
  
  public void showMultipleZonesInfo_help(Vector<Zone> activeZones) {
    int numRows = dm.getRowCount();
    
    if (numRows > 0) {
      for (int i = numRows - 1; i >= 0; i--) {
        dm.removeRow(i);
      }
      currentZone = null;
    }
    
    atts.clear();
    table.clear();
    multipleSelections = false;
    multipleSelectedZones = null;
    
    if (rowsToColor == null) {
      rowsToColor = new HashSet();
    } else {
      rowsToColor.clear();
    }
    dm.addRow(new Object[] { multiSelection, Integer.toString(activeZones.size()) });
  }
  








  public void showZoneInfo(Vector<Zone> activeZones)
  {
    int numRows = dm.getRowCount();
    
    if (numRows > 0) {
      for (int i = numRows - 1; i >= 0; i--) {
        dm.removeRow(i);
      }
      currentZone = null;
    }
    
    atts.clear();
    table.clear();
    multipleSelections = false;
    multipleSelectedZones = null;
    
    if (rowsToColor == null) {
      rowsToColor = new HashSet();
    } else
      rowsToColor.clear();
    String str;
    String str; Object[] values; JComboBox cb; if (this.type == 1)
    {
      if (currentPage == null) {
        dm.addRow(nullRow);
      }
      else {
        typeRow[1] = currentPage.pageTags
          .get("gedi_type");
        dimRow[1] = 
          ("(0,0)(" + currentPage.dlGetWidth() + "," + currentPage.dlGetHeight() + ")");
        dm.addRow(typeRow);
        dm.addRow(dimRow);
        
        atts.add("gedi_type");
        atts.add("(col,row)(width,height)");
        int i = 2;
        
        Map<String, TypeAttributeEntry> attsMap = OCRInterface.getAttsConfigUtil().getTypeAttributes(
          "DL_PAGE");
        

        Iterator localIterator = currentPage.pageTags.entrySet().iterator();
        while (localIterator.hasNext()) {
          Map.Entry<String, String> pair = (Map.Entry)localIterator.next();
          
          if (!atts.contains(((String)pair.getKey()).trim()))
          {


            int num = i;
            boolean f = false;
            for (int x = 2; x < dm.getDataVector().size(); x++)
            {
              String[] temp = dm.getDataVector().elementAt(x).toString().split(",");
              str = temp[0].substring(1);
              if ((((String)pair.getKey()).compareTo(str) < 0) && (!f)) {
                num = x;
                f = true;
              }
            }
            f = false;
            
            dm.insertRow(num, new Object[] { pair.getKey(), pair.getValue() });
            
            i++;
            
            atts.add((String)pair.getKey());
          }
        }
        for (int j = 0; j < dm.getDataVector().size(); j++) {
          if (attsMap != null) {
            String[] temp = dm.getDataVector().elementAt(j).toString().split(",");
            str = temp[0].substring(1);
            if (attsMap.containsKey(str))
            {
              values = ((TypeAttributeEntry)attsMap.get(str)).getPossibleValues().toArray();
              if (values.length != 0) {
                cb = new JComboBox(values) {
                  public boolean isEditable() {
                    Object selectedItem = getSelectedItem();
                    
                    if (selectedItem == null) { return true;
                    }
                    return 
                      selectedItem.toString().matches("\\s*");
                  }
                };
                cb.setSelectedItem(((TypeAttributeEntry)attsMap.get(str))
                  .getDefaultValue());
                
                table.getRowEditorModel().addEditorForRow(j, 
                  new DefaultCellEditor(cb), cb);

              }
              else if ((table.getSelectedRow() != -1) && 
                (table.getSelectedColumn() != -1)) {
                table.setValueAt(((TypeAttributeEntry)attsMap.get(str)).getDefaultValue(), 
                  table.getSelectedRow(), table.getSelectedColumn());
              }
            }
          }
        }
      }
    }
    else if ((this.type == 0) || (this.type == 2))
    {
      if (activeZones == null) {
        dm.addRow(nullRow);
      } else if (activeZones.size() > 1) {
        dm.addRow(new Object[] { multiSelection, Integer.toString(activeZones.size()) });
        repaint();
      } else if (activeZones.size() == 1) {
        Zone curr = (Zone)activeZones.firstElement();
        if (curr != null) {
          currentZone = curr;
          
          typeRow[1] = curr.getAttributeValue("gedi_type");
          





          Object[] types = 
            this_interfacetbdPane.data_panel.t_window.getTypeSettings()
            .keySet().toArray();
          
          JComboBox _cb = new JComboBox();
          
          if (curr.isGroup()) {
            values = (cb = types).length; for (str = 0; str < values; str++) { Object type = cb[str];
              
              if (this_interfacetbdPane.data_panel.t_window.isGroup(type.toString())) {
                _cb.addItem(type);
              }
            }
          }
          else {
            values = (cb = types).length; for (str = 0; str < values; str++) { Object type = cb[str];
              
              if (!this_interfacetbdPane.data_panel.t_window.isGroup(type.toString())) {
                _cb.addItem(type);
              }
            }
          }
          















































          table.getRowEditorModel().addEditorForRow(0, 
            new DefaultCellEditor(_cb), _cb);
          

          dimRow[1] = 
          

            ("(" + dlGetZoneOriginx + "," + dlGetZoneOriginy + ")(" + curr.dlGetZoneWidth() + "," + curr.dlGetZoneHeight() + ")");
          
          idRow[0] = "id";
          idRow[1] = curr.getAttributeValue("id");
          
          nextZoneIdRow[0] = "nextZoneID";
          
          dm.addRow(typeRow);
          dm.addRow(dimRow);
          dm.addRow(idRow);
          

          atts.add("(col,row)(width,height)");
          atts.add("gedi_type");
          atts.add("id");
          
          int count = 3;
          





          if (nextZone != null) {
            nextZoneIdRow[1] = nextZone.zoneID;
            dm.addRow(nextZoneIdRow);
            atts.add("nextZoneID");
            count = 4;
          }
          else {
            ReadingOrder roHandler = new ReadingOrder();
            if (roHandler.isEnd(curr)) {
              nextZoneIdRow[1] = noNextZone;
              dm.addRow(nextZoneIdRow);
              atts.add("nextZoneID");
              count = 4;
            }
          }
          


          int i = count;
          Map<String, TypeAttributeEntry> attsMap = 
            OCRInterface.getAttsConfigUtil().getTypeAttributes(
            curr.getZoneType());
          




          str = curr.getZoneTags().entrySet().iterator();
          while (str.hasNext()) {
            Map.Entry<String, String> pair = (Map.Entry)str.next();
            if (!atts.contains(pair.getKey()))
            {
              int num = i;
              boolean f = false;
              for (int x = count; x < dm.getDataVector().size(); x++)
              {
                String[] temp = dm.getDataVector().elementAt(x).toString().split(",");
                String str = temp[0].substring(1);
                if ((((String)pair.getKey()).compareTo(str) < 0) && (!f)) {
                  num = x;
                  f = true;
                }
              }
              f = false;
              
              dm.insertRow(num, new Object[] { pair.getKey(), pair.getValue() });
              
              i++;
              atts.add((String)pair.getKey());
            }
          }
          

































          i = count;
          if ((attsMap != null) && (!attsMap.isEmpty()))
          {

            str = attsMap.entrySet().iterator();
            while (str.hasNext()) {
              Map.Entry<String, TypeAttributeEntry> pair = (Map.Entry)str.next();
              if (!atts.contains(pair.getKey())) {
                atts.add((String)pair.getKey());
                
                int num = i;
                boolean f = false;
                for (int x = count; x < dm.getDataVector().size(); x++)
                {
                  String[] temp = dm.getDataVector().elementAt(x).toString().split(",");
                  String str = temp[0].substring(1);
                  if ((((String)pair.getKey()).compareTo(str) < 0) && (!f)) {
                    num = x;
                    f = true;
                  }
                }
                
                f = false;
                
                dm.insertRow(num, new Object[] { pair.getKey(), ((TypeAttributeEntry)pair.getValue()).getDefaultValue() });
                


                curr.setAttributeValue((String)pair.getKey(), ((TypeAttributeEntry)pair.getValue())
                  .getDefaultValue());
                
                if (OCRInterface.currDoc != null)
                  OCRInterface.currDoc.setModified(true);
              }
              i++;
            }
          }
          
          for (int j = 0; j < dm.getDataVector().size(); j++) {
            if (attsMap != null) {
              String[] temp = dm.getDataVector().elementAt(j).toString().split(",");
              final String str = temp[0].substring(1);
              if (attsMap.containsKey(str)) {
                Object[] values = ((TypeAttributeEntry)attsMap.get(str)).getPossibleValues().toArray();
                if (values.length != 0) {
                  JComboBox cb = new JComboBox(((TypeAttributeEntry)attsMap.get(str)).getPossibleValues()
                    .toArray())
                    {

                      public boolean isEditable()
                      {
                        if (str.equals("contents")) {
                          return true;
                        }
                        return false;

                      }
                      

                    };
                    cb.addItemListener(new java.awt.event.ItemListener() {
                      public void itemStateChanged(ItemEvent e) {
                        if ((e.getStateChange() == 1) && 
                          (str.equals("status"))) {
                          OCRInterface.this_interface.getCanvas().unlockSoftLock();
                        }
                        
                      }
                    });
                    cb.setSelectedItem(((TypeAttributeEntry)attsMap.get(str))
                      .getDefaultValue());
                    table.getRowEditorModel().addEditorForRow(j, 
                      new DefaultCellEditor(cb), cb);

                  }
                  else if ((table.getSelectedRow() != -1) && 
                    (table.getSelectedColumn() != -1)) {
                    table.setValueAt(((TypeAttributeEntry)attsMap.get(str)).getDefaultValue(), 
                      table.getSelectedRow(), table.getSelectedColumn());
                  }
                }
              }
            }
          }
        }
      }
      







      if (table.getCellEditor() != null) {
        table.getCellEditor().cancelCellEditing();
      }
      table.revalidate();
    }
    

























































    class MyTableModel
      extends DefaultTableModel
    {
      private static final long serialVersionUID = 7552270571670963995L;
      
























































      MyTableModel() {}
      
























































      public int getColumnCount()
      {
        return AttributeTable.columnNames.length;
      }
      
      public String getColumnName(int col) {
        return AttributeTable.columnNames[col];
      }
      






      public Class getColumnClass(int c)
      {
        return getValueAt(0, c).getClass();
      }
      






      public boolean isCellEditable(int row, int col)
      {
        System.out.println("table.getValueAt(row, col):" + 
          table.getValueAt(row, col));
        if (col == 1)
        {
          if ((table.getValueAt(row, col) != null) && 
            (!table.getValueAt(row, col).equals("DL_PAGE")) && 
            (!table.getValueAt(row, 0).equals("(col,row)(width,height)")) && ((currentZone == null) || 
            
            (!currentZone.getAttributeValue("gedi_type").equals("DL_TEXTLINEGT")) || 
            (!table.getValueAt(row, 0).equals("contents")))) return true;
        }
        return 
        





          false;
      }
    }
    











    private void init()
    {
      TableUtils tmp9_6 = TableUtils.getInstance();tmp9_6.getClass();table = new TableUtils.JTableA(tmp9_6, dm)
      {
        private static final long serialVersionUID = -4420937772653311736L;
        
        public void setCellSizes()
        {
          setRowHeight(20);
          
          showHorScroll(false);
        }
        




        public void showHorScroll(boolean show)
        {
          if (show) {
            setAutoResizeMode(0);
          } else {
            setAutoResizeMode(2);
          }
        }
        
        public boolean isCellEditable(int row, int col) {
          if ((col == 1) && 
            (table.getValueAt(row, 0) != null) && (
            (table.getValueAt(row, 0).equals("(col,row)(width,height)")) || 
            (table.getValueAt(row, 1).equals("DL_PAGE")))) {
            return false;
          }
          
          if ((col == 1) && 
            (table.getValueAt(row, 0) != null) && 
            (table.getValueAt(row, 0).equals("contents")) && 
            (OCRInterface.this_interface.getLockContentEditing()))
            return false;
          if ((col == 1) && 
            (table.getValueAt(row, 0) != null) && 
            (table.getValueAt(row, 0).equals(multiSelection)))
            return false;
          if (col == 0) {
            return false;
          }
          return true;
        }
      };
      

      for (String name : columnNames) {
        dm.addColumn(name);
      }
      
      table.setGridColor(Color.black);
      table.setBackground(new JLabel().getBackground());
      table.setFont(curr_font);
      

      table.setSelectionMode(0);
      table.setColumnSelectionAllowed(false);
      table.setRowSelectionAllowed(true);
      table.getTableHeader().setReorderingAllowed(false);
      
      table.addMouseListener(new java.awt.event.MouseAdapter() {
        private void myPopupEvent(MouseEvent e) {
          if (type == 0) {
            int x = e.getX();
            int y = e.getY();
            JTable table = (JTable)e.getSource();
            int row = table.rowAtPoint(e.getPoint());
            int col = table.columnAtPoint(e.getPoint());
            
            row = table.convertRowIndexToModel(row);
            col = table.convertColumnIndexToModel(col);
            if (col == 1) {
              return;
            }
            String attribute = (String)table.getValueAt(row, 0);
            String value = (String)table.getValueAt(row, 1);
            
            table.getSelectionModel().setSelectionInterval(row, row);
            
            JPopupMenu popup = buildPopupMenu(attribute.trim(), value.trim());
            
            if (popup != null)
              popup.show(AttributeTable.this, x, y);
          }
        }
        
        public void mousePressed(MouseEvent e) {
          if (e.isPopupTrigger()) myPopupEvent(e);
        }
        
        public void mouseReleased(MouseEvent e) {
          if (e.isPopupTrigger()) myPopupEvent(e);
        }
        
        public void mouseClicked(MouseEvent e) {
          if (e.isPopupTrigger()) { myPopupEvent(e);
          }
          





















        }
        



















      });
      TableUtils.setUpIntegerEditor(table);
      
      dm.addTableModelListener(this);
      
      typeRow[0] = "gedi_type";
      dimRow[0] = "(col,row)(width,height)";
    }
    










    public JPopupMenu buildPopupMenu(final String attribute, final String value)
    {
      if (attribute.equals("(col,row)(width,height)"))
        return null;
      popup = new JPopupMenu();
      JMenuItem selectSimilar = new JMenuItem("Find All Similar");
      selectSimilar.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent arg0) {
          Vector<DLZone> allZones = 
            currentHWObjcurr_canvas.getShapeVec().getAsVector();
          ImageDisplay.activeZones.clear();
          for (DLZone zone : allZones) {
            if (attribute.equals("nextZoneID")) {
              if ((nextZone != null) && (nextZone.zoneID.equals(value))) {
                currentHWObjcurr_canvas.addToSelected((Zone)zone);
              } else if ((nextZone == null) && (value.equals(noNextZone))) {
                currentHWObjcurr_canvas.addToSelected((Zone)zone);
              }
            } else if (zone.hasAttribute(attribute)) {
              String val = zone.getAttributeValue(attribute);
              if (val.equals(value)) {
                currentHWObjcurr_canvas.addToSelected((Zone)zone);
              }
            }
          }
          
          this_interfacetbdPane.data_panel.a_window.showZoneInfo(ImageDisplay.activeZones);
        }
        

      });
      JMenuItem copy = new JMenuItem("Copy value(s)");
      copy.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent arg0) {
          copyValues(attribute, ",");
        }
        
      });
      popup.add(selectSimilar);
      popup.add(copy);
      


      return popup;
    }
    
    public void changeVal()
    {
      final JDialog dlg = new JDialog(OCRInterface.this_interface, "Change Arbitrary Value", true);
      final JTextField tf = new JTextField();
      
      dlg.setSize(300, 70);
      int xpt = (this_interfacegetLocationx + OCRInterface.this_interface.getWidth()) / 2;
      int ypt = (this_interfacegetLocationy + OCRInterface.this_interface.getHeight()) / 2;
      dlg.setLocation(xpt, ypt);
      
      final String[] parts = dm.getDataVector().elementAt(table.getSelectedRow()).toString().split(",");
      String val = (String)currDocget_zones_vecgetPagepageTags.get(parts[0].substring(1));
      tf.setText(val);
      
      JButton ok_btn = new JButton("OK")
      {
        public void fireActionPerformed(ActionEvent ae) {
          String tstr = tf.getText().trim();
          
          currDocget_zones_vecgetPagepageTags.put(parts[0].substring(1), tstr);
          OCRInterface.currDoc.setModified(true);
          dlg.dispose();
        }
        
      };
      JButton cancel_btn = new JButton("Cancel") {
        public void fireActionPerformed(ActionEvent ae) {
          dlg.dispose();
        }
      };
      dlg.getContentPane().add(tf, "North");
      dlg.getContentPane().add(ok_btn, "Center");
      dlg.getContentPane().add(cancel_btn, "East");
      
      dlg.setVisible(true);
    }
    

    public void changeVal2()
    {
      final JDialog dlg = new JDialog(OCRInterface.this_interface, "Change Arbitrary Value", true);
      final JTextField tf = new JTextField();
      
      dlg.setSize(300, 70);
      int xpt = (this_interfacegetLocationx + OCRInterface.this_interface.getWidth()) / 2;
      int ypt = (this_interfacegetLocationy + OCRInterface.this_interface.getHeight()) / 2;
      dlg.setLocation(xpt, ypt);
      
      final String[] parts = dm.getDataVector().elementAt(table.getSelectedRow()).toString().split(",");
      String val = ((DLZone)OCRInterface.currDoc.get_zones_vec().getActZones().get(0)).getAttributeValue(parts[0].substring(1));
      
      tf.setText(val);
      
      JButton ok_btn = new JButton("OK")
      {
        public void fireActionPerformed(ActionEvent ae) {
          String tstr = tf.getText().trim();
          
          ((DLZone)OCRInterface.currDoc.get_zones_vec().getActZones().get(0)).setAttributeValue(parts[0].substring(1), tstr);
          OCRInterface.currDoc.setModified(true);
          dlg.dispose();
        }
        
      };
      JButton cancel_btn = new JButton("Cancel") {
        public void fireActionPerformed(ActionEvent ae) {
          dlg.dispose();
        }
      };
      dlg.getContentPane().add(tf, "North");
      dlg.getContentPane().add(ok_btn, "Center");
      dlg.getContentPane().add(cancel_btn, "East");
      
      dlg.setVisible(true);
    }
    

    public void tableChanged(TableModelEvent tme)
    {
      int row = tme.getFirstRow();
      int col = tme.getColumn();
      
      if ((col < 0) || (row < 0)) {
        return;
      }
      if (tme.getType() == 0) {
        updateAfterChangeInTable(row, col);
      }
      OCRInterface.this_interface.getCanvas().paintCanvas();
      repaint();
    }
    
    private void updateAfterChangeInTable(int row, int col) {
      OCRInterface.this_interface.saveCurrentPageState();
      if (col == 1) {
        if (multipleSelections) {
          String attribute = (String)table.getValueAt(row, 0);
          String value = (String)table.getValueAt(row, 1);
          if ((attribute.equals("gedi_type")) && (!value.trim().isEmpty()))
          {


            if (type == 2) {
              currentHWObjcurr_canvas.resetZoneType(value);
            } else {
              currentHWObjcurr_canvas.resetType(value);
            }
            
            currentHWObjcurr_canvas.paintCanvas();
          }
          else if (!attribute.equals("gedi_type")) {
            for (Zone zone : multipleSelectedZones) {
              zone.setAttributeValue(attribute, value);
              rowsToColor.remove(Integer.valueOf(row));
            }
          }
        }
        else
        {
          String attribute = (String)table.getValueAt(row, 0);
          String value = (String)table.getValueAt(row, 1);
          if ((attribute.equals("gedi_type")) && (!value.trim().isEmpty()))
          {

            currentHWObjcurr_canvas.resetType(value);
            currentHWObjcurr_canvas.paintCanvas();

          }
          else if (currentZone != null) {
            currentZone.setAttributeValue((String)table.getValueAt(row, 
              0), (String)table.getValueAt(row, 1));
          } else {
            currentPage.pageTags.put((String)table.getValueAt(row, 
              0), (String)table.getValueAt(row, 1));
          }
        }
      }
    }
    
    public TableUtils.JTableA getTable() {
      return table;
    }
    



    public void keyTyped(KeyEvent arg0) {}
    


    public void keyPressed(KeyEvent arg0) {}
    


    public void keyReleased(KeyEvent arg0) {}
    


    public String buildDataFilePath(String imageFilePath)
    {
      String dataFilePath = null;
      int ind = 4;
      if ((imageFilePath.toLowerCase().endsWith("tiff")) || (imageFilePath.toLowerCase().endsWith("jpeg"))) {
        ind = 5;
      }
      File imageFile = new File(imageFilePath);
      
      StringBuffer fileImageBuffer = new StringBuffer(imageFile.getName());
      
      fileImageBuffer.delete(fileImageBuffer.length() - ind, fileImageBuffer
        .length());
      


      dataFilePath = OCRInterface.getCurrentXmlDir() + "/" + 
        OCRInterface.getXmlDirName() + "/" + 
        fileImageBuffer;
      
      if (new File(dataFilePath + ".XML").exists()) {
        return dataFilePath + ".XML";
      }
      return dataFilePath + ".xml";
    }
    







    public void showMultipleZonesInfo(Vector<Zone> activeZones, int typeIn)
    {
      TableModelListener[] tml_array = dm.getTableModelListeners();
      for (TableModelListener tml : tml_array) {
        dm.removeTableModelListener(tml);
      }
      this.type = typeIn;
      showZoneInfo(activeZones);
      multipleSelectedZones = activeZones;
      multipleSelections = true;
      typeRow[1] = "";
      dm.addRow(typeRow);
      dm.addRow(idRow);
      dm.addRow(nextZoneIdRow);
      
      Object[] types = 
        this_interfacetbdPane.data_panel.t_window.getTypeSettings()
        .keySet().toArray();
      
      JComboBox _cb = new JComboBox();
      
      Object activeGroups = currentHWObjcurr_canvas.getActiveGroups();
      
      Set<String> typesOfGroup;
      
      String s;
      
      if (this.type == 2) {
        Object allAllowedTypes = new HashSet();
        
        for (Zone group : (Vector)activeGroups) {
          typesOfGroup = 
            this_interfacetbdPane.data_panel.t_window.getTypesOfGroup(group.getZoneType());
          if ((typesOfGroup.size() == 1) && (((String)typesOfGroup.iterator().next()).equals("ANY"))) {
            for (Object type : types)
            {
              if (!this_interfacetbdPane.data_panel.t_window.isGroup(type.toString()))
                ((Set)allAllowedTypes).add(type.toString());
            }
          } else {
            for (Iterator localIterator2 = typesOfGroup.iterator(); localIterator2.hasNext();) { s = (String)localIterator2.next();
              ((Set)allAllowedTypes).add(s);
            }
          }
        }
        
        for (??? = ((Set)allAllowedTypes).iterator(); ???.hasNext();) { type = (String)???.next();
          _cb.addItem(type);
        }
      } else if (!((Vector)activeGroups).isEmpty()) {
        String str1 = (typesOfGroup = types).length; for (type = 0; type < str1; type++) { Object type = typesOfGroup[type];
          
          if (this_interfacetbdPane.data_panel.t_window.isGroup(type.toString())) {
            _cb.addItem(type);
          }
        }
      }
      else {
        String str2 = (typesOfGroup = types).length; for (type = 0; type < str2; type++) { Object type = typesOfGroup[type];
          
          if (!this_interfacetbdPane.data_panel.t_window.isGroup(type.toString())) {
            _cb.addItem(type);
          }
        }
      }
      
      _cb.insertItemAt(" ", 0);
      
      table.getRowEditorModel().addEditorForRow(1, 
        new DefaultCellEditor(_cb), _cb);
      

      atts.add("gedi_type");
      atts.add("id");
      atts.add("nextZoneID");
      Object attsMap; Map.Entry<String, TypeAttributeEntry> pair; label845: for (String type = activeZones.iterator(); type.hasNext(); 
          



          s.hasNext())
      {
        Zone zone = (Zone)type.next();
        attsMap = 
          OCRInterface.getAttsConfigUtil().getTypeAttributes(zone.getZoneType());
        
        if ((attsMap == null) || (((Map)attsMap).isEmpty()))
          break label845;
        s = ((Map)attsMap).entrySet().iterator(); continue;pair = (Map.Entry)s.next();
        boolean addedSuccessfully = atts.add((String)pair.getKey());
        if (addedSuccessfully) {
          String blank = "";
          dm.addRow(new Object[] { pair.getKey(), blank });
          
          String str = (String)pair.getKey();
          Object[] values = ((TypeAttributeEntry)((Map)attsMap).get(str)).getPossibleValues().toArray();
          if (values.length != 0) {
            JComboBox cb = new JComboBox(values);
            
            cb.setSelectedIndex(-1);
            
            table.getRowEditorModel().addEditorForRow(dm.getDataVector().size() - 1, 
              new DefaultCellEditor(cb), cb);
          }
        }
      }
      

      fillInValues();
      String str3 = (pair = tml_array).length; for (type = 0; type < str3; type++) { TableModelListener tml = pair[type];
        dm.addTableModelListener(tml);
      }
    }
    






    private void setCellRenderer()
    {
      final TableCellRenderer renderer = table.getDefaultRenderer("".getClass());
      table.setDefaultRenderer("".getClass(), 
        new TableCellRenderer()
        {
          public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
          {
            Component ret = renderer.getTableCellRendererComponent(table, value, isSelected, 
              hasFocus, row, column);
            
            if ((row == 0) && ((column == 0) || (column == 1)) && 
              (table.getValueAt(row, 0).equals(multiSelection)) && (!isSelected)) {
              ret.setFont(ret.getFont().deriveFont(1));
              ret.setBackground(new JLabel().getBackground());
            }
            else if ((multipleSelections) && 
              (row >= 1) && 
              (column == 1) && 
              (rowsToColor.contains(Integer.valueOf(row)))) {
              ret.setBackground(Color.LIGHT_GRAY);
            } else if (!isSelected) {
              ret.setBackground(new JLabel().getBackground());
            }
            return ret;
          }
        });
    }
    






    private void fillInValues()
    {
      String currentValue = null;
      rowsToColor.clear();
      for (int i = 1; i < table.getRowCount(); i++) {
        currentValue = null;
        for (Zone zone : multipleSelectedZones) {
          String attribute = (String)table.getValueAt(i, 0);
          String value = "";
          if (attribute.equalsIgnoreCase("id")) {
            value = zoneID;
          } else if ((attribute.equalsIgnoreCase("nextZoneID")) && 
            (nextZone != null)) {
            value = nextZone.zoneID;
          } else if ((attribute.equalsIgnoreCase("nextZoneID")) && 
            (nextZone == null)) {
            value = noNextZone;
          } else {
            value = (String)zone.getZoneTags().get(attribute);
          }
          value = value == null ? "" : value.trim();
          
          if (currentValue == null) {
            currentValue = value;
          }
          if ((currentValue != null) && (currentValue.equals(value))) {
            table.setValueAt(value, i, 1);
          }
          else
          {
            table.setValueAt("   ", i, 1);
            currentValue = "";
            rowsToColor.add(Integer.valueOf(i));
            break;
          }
        }
      }
      repaint();
    }
    
    public String copyValues(String attribute, String separator) {
      Vector<Zone> selectedZones = ImageDisplay.activeZones;
      
      String str = "";
      
      for (DLZone zone : selectedZones) {
        if (attribute.trim().equalsIgnoreCase("id")) {
          str = str + zoneID + separator;
        } else if (attribute.trim().equalsIgnoreCase("nextZoneID")) {
          if (nextZone == null) {
            str = str + noNextZone + separator;
          } else {
            str = str + nextZone.zoneID + separator;
          }
        } else if (zone.hasAttribute(attribute)) {
          String value = zone.getAttributeValue(attribute);
          

          str = str + value + separator;
        }
      }
      
      String _tmp = str.substring(str.lastIndexOf(separator), str.length());
      

      if (_tmp.equals(separator)) {
        str = str.substring(0, str.length() - separator.length());
      }
      if (str.trim().isEmpty()) {
        str = separator;
      }
      Toolkit toolkit = Toolkit.getDefaultToolkit();
      Clipboard clipboard = toolkit.getSystemClipboard();
      StringSelection strSel = new StringSelection(str);
      clipboard.setContents(strSel, null);
      
      return str;
    }
  }
