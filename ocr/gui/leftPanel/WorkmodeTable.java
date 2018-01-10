package ocr.gui.leftPanel;

import gttool.document.DLDocument;
import gttool.document.DLPage;
import gttool.document.DLZone;
import gttool.io.TypeLister;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import ocr.JThumb.JThumbList;
import ocr.JThumb.JThumbListModel;
import ocr.gui.FindPanel;
import ocr.gui.LoadDataFile;
import ocr.gui.OCRInterface;
import ocr.gui.workflows.PolyTranscribeToolBar;
import ocr.manager.GlobalDisableManager;
import ocr.manager.GlobalHotkeyManager;
import ocr.manager.PropertiesInfoHolder;
import ocr.manager.zones.ZonesManager;
import ocr.tif.ImageDisplay;
import ocr.tif.ImageReaderDrawer;
import ocr.tif.color.ColorImageToolbar;
import ocr.util.DesktopUtil;
import ocr.util.comments.CommentsParser;
import org.xml.sax.SAXException;

public class WorkmodeTable extends JTable
{
  public class StringRenderer extends DefaultTableCellRenderer
  {
    public StringRenderer() {}
    
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
      Component superReturn = super.getTableCellRendererComponent(table, 
        value, isSelected, hasFocus, row, column);
      
      superReturn.setBackground(new JLabel().getBackground());
      
      if (column == 0) {
        ((JLabel)superReturn).setHorizontalAlignment(0);
        superReturn.setFont(new Font("Arial", 0, 16));
      }
      else {
        ((JLabel)superReturn).setHorizontalAlignment(2);
      }
      
      setToolTipText((String)value);
      
      return superReturn;
    }
  }
  
  public class MyTableHeaderRenderer extends JLabel implements javax.swing.table.TableCellRenderer
  {
    public MyTableHeaderRenderer() {}
    
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIndex, int vColIndex) {
      setText(value.toString());
      setFont(new Font("Arial", 1, 16));
      setHorizontalAlignment(0);
      Border headerBorder = UIManager.getBorder("TableHeader.cellBorder");
      setBorder(headerBorder);
      return this;
    }
  }
  
  public int my_mode = 0;
  

  String selectedFileName;
  

  public String newFileName;
  
  public static int curRow = -1;
  
  public int curCol = 1;
  public static int col = 1;
  
  public static int selRow;
  public static int selCol;
  private boolean CTRL_pressed = false;
  
  OCRInterface ocrIF = OCRInterface.this_interface;
  


  public DatasetSpecificToolPanel toolPanel;
  


  public boolean select = false;
  
  public boolean selecting = false;
  
  public String selFileName = "";
  

  public static PopUpChip popUpImageChips;
  
  public static final int EXPAND_COLLAPSE = 0;
  
  public static final int NAME = 1;
  
  public static final int IMAGE = 2;
  
  public static final int XML = 3;
  
  public static final String EXPANDED = "-";
  
  public static final String COLLAPSED = "+";
  

  public WorkmodeTable(int mode)
  {
    this(new CustomTableModel(), mode);
    initMode(mode);
  }
  







  public WorkmodeTable(CustomTableModel model, int mode)
  {
    super(model);
    my_mode = mode;
    








    initMode(mode);
    initTable(model);
    
    setCellSizes();
    
    addFocusListener(new java.awt.event.FocusListener()
    {
      public void focusGained(FocusEvent arg0) {}
      
      public void focusLost(FocusEvent arg0) {
        CTRL_pressed = false;
      }
      

    });
    addKeyListener(new java.awt.event.KeyAdapter()
    {
      public void keyReleased(KeyEvent e) {
        int col = getSelectedColumn();
        int row = getSelectedRow();
        switch (e.getKeyCode())
        {



        case 8: 
        case 38: 
          row = getSelectionModel().getAnchorSelectionIndex();
          col = getColumnModel().getSelectionModel()
            .getMinSelectionIndex();
          
          clearSelection();
          

          changeSelection(row, col, false, false);
          

          processSelectionEvent(row, col, true);
          break;
        
        case 32: 
        case 40: 
          row = getSelectionModel().getAnchorSelectionIndex();
          col = getColumnModel().getSelectionModel()
            .getMinSelectionIndex();
          
          clearSelection();
          

          changeSelection(row, col, false, false);
          

          processSelectionEvent(row, col, true);
          break;
        


        case 33: 
        case 34: 
        case 37: 
        case 39: 
          row = getSelectionModel().getMinSelectionIndex();
          col = getColumnModel().getSelectionModel()
            .getMinSelectionIndex();
          
          clearSelection();
          

          changeSelection(row, col, false, false);
          

          processSelectionEvent(row, col, true);
          break;
        case 17: 
          CTRL_pressed = false; }
        
      }
      
      public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
        case 17: 
          CTRL_pressed = true;
        
        }
        
      }
    });
    addMouseListener(new MouseAdapter()
    {


      public void mousePressed(MouseEvent e)
      {

        int row = getSelectionModel().getAnchorSelectionIndex();
        int col = columnAtPoint(new Point(e.getX(), e.getY()));
        
        WorkmodeTable.selCol = col;
        WorkmodeTable.selRow = row;
        
        if (e.getButton() != 3)
        {


          if (CTRL_pressed) {
            addRowSelectionInterval(row, row);
            CTRL_pressed = false;
            return;
          }
          
          clearSelection();
          
          if (col == 0) {
            changeSelection(row, 1, false, false);
            processSelectionEvent(row, 1, true);
          }
          else {
            changeSelection(row, col, false, false);
            processSelectionEvent(row, col, true);
          }
          
          FilePropPacket fpp = ocrIF.workmodeProps[my_mode]
            .getElementFilePropVec(row);
          
          if (col == 0) {
            String currentStatus = (String)getValueAt(row, col);
            setValueAt(fpp.setExpandCollapse(currentStatus), row, col);
            
            if (fpp.isCollapsed()) {
              removeVarietyOfExtRows(fpp, row);
            } else {
              addVarietyOfExtRows(fpp, row);
            }
          }
        }
        if (col == 1) {
          select = true;
        }
        
        showPopUpMenu(e);
      }
      










      private void showPopUpMenu(MouseEvent e)
      {
        if ((e.getModifiers() & 0x10) == 0) {
          JPopupMenu jPopUp = new JPopupMenu();
          
          JMenuItem openCommentsFile = new JMenuItem("Add/View Comments");
          openCommentsFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              WorkmodeTable.this.setWaitCursor();
              WorkmodeTable.this.setActionPopup("viewComments");
              WorkmodeTable.this.setDefaultCursor();
            }
          });
          jPopUp.add(openCommentsFile);
          if (getSelectedRow() == -1) {
            openCommentsFile.setEnabled(false);
          }
          JMenuItem deleteCommentsFile = new JMenuItem("Delete Comments");
          deleteCommentsFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              WorkmodeTable.this.setWaitCursor();
              WorkmodeTable.this.setActionPopup("deleteComments");
              WorkmodeTable.this.setDefaultCursor();
            }
          });
          jPopUp.add(deleteCommentsFile);
          
          if ((getSelectedRow() == -1) || (CommentsParser.getCommentsFilePathFor(selFileName) == null)) {
            deleteCommentsFile.setEnabled(false);
          }
          jPopUp.addSeparator();
          
          JMenuItem open = new JMenuItem("Open XML");
          open.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              WorkmodeTable.this.setWaitCursor();
              WorkmodeTable.this.setActionPopup("open");
              WorkmodeTable.this.setDefaultCursor();
            }
          });
          jPopUp.add(open);
          if (getSelectedRow() == -1) {
            open.setEnabled(false);
          }
          JMenuItem edit = new JMenuItem("Edit XML");
          edit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              WorkmodeTable.this.setWaitCursor();
              WorkmodeTable.this.setActionPopup("edit");
              WorkmodeTable.this.setDefaultCursor();
            }
          });
          jPopUp.add(edit);
          if (getSelectedRow() == -1) {
            edit.setEnabled(false);
          }
          









          JMenuItem refresh = new JMenuItem("Refresh XML");
          refresh.setAccelerator(KeyStroke.getKeyStroke(
            116, 0));
          refresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              WorkmodeTable.this.setWaitCursor();
              WorkmodeTable.this.setActionPopup("refresh");
              WorkmodeTable.this.setDefaultCursor();
            }
          });
          jPopUp.add(refresh);
          if (getSelectedRow() == -1) {
            refresh.setEnabled(false);
          }
          jPopUp.addSeparator();
          
          JMenuItem overlaySelectedItem = null;
          overlaySelectedItem = new JMenuItem("Merge Selected XML");
          
          if (getSelectedRowCount() <= 1) {
            overlaySelectedItem.setEnabled(false);
          }
          overlaySelectedItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ac) {
              WorkmodeTable.this.overlaySelected();
            }
            
          });
          jPopUp.add(overlaySelectedItem);
          if (getSelectedRow() == -1) {
            overlaySelectedItem.setEnabled(false);
          }
          JMenuItem overlayAllItem = null;
          overlayAllItem = new JMenuItem("Merge ALL XML");
          
          overlayAllItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ac) {
              WorkmodeTable.this.overlayAll();
            }
            
          });
          jPopUp.add(overlayAllItem);
          if (getSelectedRow() == -1) {
            overlayAllItem.setEnabled(false);
          }
          jPopUp.addSeparator();
          
          JMenuItem closeImageMenuItem = null;
          
          closeImageMenuItem = new JMenuItem("Close Image");
          closeImageMenuItem.setMnemonic('C');
          closeImageMenuItem.setAccelerator(KeyStroke.getKeyStroke(
            67, 8));
          
          closeImageMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ac) {
              closeDocument();
            }
            

          });
          jPopUp.add(closeImageMenuItem);
          if (getSelectedRow() == -1) {
            closeImageMenuItem.setEnabled(false);
          }
          








          JMenuItem imageChips = null;
          
          imageChips = new JMenuItem("Show image chips");
          
          imageChips.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ac) {
              if (ac.getActionCommand().equals("Show image chips"))
              {
                processSelectionEvent(WorkmodeTable.curRow, 1, true);
                WorkmodeTable.popUpImageChips = new PopUpChip();
              }
              
            }
            

          });
          jPopUp.add(imageChips);
          if (getSelectedRow() == -1)
            imageChips.setEnabled(false);
          select = false;
          

          JMenuItem menuItem = null;
          

          menuItem = new JMenuItem("Preview Selected");
          




          if (getSelectedRowCount() <= 1) {
            menuItem.setEnabled(false);
          }
          
          menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              if (e.getActionCommand().equals("Preview Selected")) {
                this_interfacelist.indices = getSelectedRows();
                this_interfacemodel
                  .setVisibleImagesFromAll(this_interfacelist.indices);
                OCRInterface.this_interface
                  .selectThumbnailPanel();
              }
              
            }
            

          });
          jPopUp.add(menuItem);
          select = false;
          
          JCheckBoxMenuItem lock = new JCheckBoxMenuItem("Lock");
          lock.setSelected(isSelectedLocked());
          
          lock.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              FilePropPacket fpp = ocrIF.workmodeProps[my_mode]
                .getElementFilePropVec(WorkmodeTable.curRow);
              int dataSet = 0;
              if (curCol == 3)
                dataSet = 1;
              fpp.setHardLocked(dataSet, 
                ((JCheckBoxMenuItem)e.getSource()).isSelected(), true);
              
              OCRInterface.disableManager.tableCellSelected(
                ((JCheckBoxMenuItem)e.getSource())
                .isSelected(), WorkmodeTable.curRow);
              OCRInterface.this_interface.updateCurrFilename();
            }
            
          });
          jPopUp.add(lock);
          if (getSelectedRow() == -1) {
            lock.setEnabled(false);
          }
          
















          jPopUp.addSeparator();
          
          JMenuItem copyName = new JMenuItem("Copy image name");
          copyName.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              if (e.getActionCommand().equals("Copy image name")) {
                int[] rows = getSelectedRows();
                String selectedNames = "";
                for (int i = 0; i < rows.length; i++) {
                  selectedNames = 
                    selectedNames + FilePropPacket.uncolorImageName((String)getModel().getValueAt(rows[i], 1)) + "\n";
                }
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                Transferable transferableText = new java.awt.datatransfer.StringSelection(selectedNames);
                clipboard.setContents(transferableText, null);
              }
              
            }
            

          });
          jPopUp.add(copyName);
          if (getSelectedRow() == -1) {
            copyName.setEnabled(false);
          }
          jPopUp.show((Component)e.getSource(), e.getX(), e.getY());

        }
        
      }
      

    });
    getTableHeader().addMouseListener(new MouseAdapter()
    {
      int colClicked;
      




      public void mousePressed(MouseEvent e)
      {
        colClicked = getTableHeader().columnAtPoint(e.getPoint());
        if ((e.getModifiers() & 0x10) == 0)
        {
          if (colClicked > 1) {
            String columnName = getModel().getColumnName(colClicked);
            

            JPopupMenu jPopUp = new JPopupMenu();
            

            JMenuItem lock = new JMenuItem("Lock all " + columnName + " files");
            
            lock.addActionListener(new ActionListener()
            {
              public void actionPerformed(ActionEvent e) {
                ocrIF.workmodeProps[my_mode].setAllFilePropsPacketsLocked(
                  colClicked, true);
                
                ocrIF.update_tables();
                
                if (curCol == colClicked) {
                  OCRInterface.disableManager.tableCellSelected(
                    true, WorkmodeTable.curRow);

                }
                

              }
              

            });
            JMenuItem unlock = new JMenuItem("Unlock all " + columnName + " files");
            unlock.addActionListener(new ActionListener()
            {
              public void actionPerformed(ActionEvent e) {
                ocrIF.workmodeProps[my_mode].setAllFilePropsPacketsLocked(
                  colClicked, false);
                ocrIF.update_tables();
                
                if (curCol == colClicked) {
                  OCRInterface.disableManager.tableCellSelected(
                    false, WorkmodeTable.curRow);
                }
              }
            });
            jPopUp.add(lock);
            jPopUp.add(unlock);
            jPopUp.show((Component)e.getSource(), e.getX(), e
              .getY());

          }
          

        }
        else if (((e.getModifiers() & 0x10) != 0) && 
          (colClicked == 0)) {
          String status = (String)getColumnModel().getColumn(0).getHeaderValue();
          
          if (status.equals("+"))
          {
            getColumnModel().getColumn(0).setHeaderValue("-");
            ocrIF.setCollapseRows(false);
          }
          else if (status.equals("-"))
          {
            getColumnModel().getColumn(0).setHeaderValue("+");
            ocrIF.setCollapseRows(true);
          }
          
          ocrIF.update_tables();
          getTableHeader().repaint();
          WorkmodeTable.curRow = -1;
          processSelectionEvent(0, 1, false);
          WorkmodeTable.curRow = 0;
        }
      }
    });
  }
  















  public void update(int colClicked, int rowClicked, String newFileName)
  {
    if (!this_interfacefclose_cancel_selected)
    {



      getSelectionModel().setSelectionInterval(rowClicked, rowClicked);
      
      curRow = rowClicked;
      
      selectedFileName = newFileName;
      
      curCol = colClicked;
      

      OCRInterface.this_interface.updateCurrFilename();
      currentHWObjcurr_canvas.reset();
      repaint();
    }
  }
  
  public void ocrUpdateTablesHelper(int row, int col)
  {
    update(row, col, selectedFileName);
  }
  
  public void processSelectionEvent(int rowClicked, int colClicked, boolean clearConnectedComponents) {
    System.out.println("processSelectionEvent");
    
    boolean setChanged = false;
    
    if (clearConnectedComponents) {
      ocr.tif.ImageAnalyzer.clearConnectedComponents();
    }
    newFileName = ((String)getModel().getValueAt(rowClicked, 1));
    newFileName = FilePropPacket.uncolorImageName(newFileName);
    System.out.println("newFileName: " + newFileName);
    OCRInterface ocrIF = OCRInterface.this_interface;
    
    ocrIF.getUseETextWindow();
    


    if (ocrIF.getEnableAlignment()) {
      ocrIF.clearETextSel();
      setGTState(newFileName);
      GlobalHotkeyManager.getInstance().setEnabled(true);
    }
    
    if (ocrIF.getEnableTranslateWorkflow()) {
      ocrIF.getTranslateWorkflowPanel().setState(7);
    }
    
    if (colClicked >= 0)
    {
      col = colClicked;
      

      selFileName = newFileName.substring(0, newFileName.lastIndexOf('.'));
      
      String newFilePath = "";
      
      if (curRow != rowClicked)
      {

        newFilePath = 
        
          OCRInterface.getCurrentImageDir() + OCRInterface.getImageDirName() + File.separator + newFileName;
        
        if (this_interfacerightSplitPanel.getTopComponent() == this_interfacethumb) {
          JViewport myPort = this_interfacepane.getViewport();
          int numthumb = this_interfacetoolbar.thumbList.getSelectedIndex() + 1;
          
          myPort.setViewPosition(new Point(0, this_interfacelist.THUMB_HEIGHT * (rowClicked / numthumb)));
        }
        

        System.out.println("SELECTED_FILE:   " + newFilePath);
        

        String base = OCRInterface.this_interface.getBaseImage(
          new File(newFilePath).getName(), 
          OCRInterface.this_interface.getFileExtension(newFileName));
        setChanged = (base != null) && (!base.equalsIgnoreCase(ImageReaderDrawer.getFile_path()));
        
        System.out.println("setChagned: " + setChanged);
        

        OCRInterface.this_interface.getSaveFilesDialog().saveData();
        
        if (!OCRInterface.this_interface.isAllowToSwitchImage()) {
          int answer = JOptionPane.showConfirmDialog(
            OCRInterface.this_interface, 
            "Your changes can not be saved.\nDo you want to move to the next image?", 
            
            "Error writing to disk", 
            0);
          if (answer == 0) {
            OCRInterface.this_interface.setAllowToSwitchImage(true);
          } else {
            changeSelection(getSelectedRow(), 1, false, false);
            return;
          }
        }
        
        ocrIF.openFile(newFilePath, newFileName);
        
        if ((bottomPanel.getFoundNames() != null) && 
          (curRow != -1) && 
          (!bottomPanel.getFoundNames().contains(Integer.valueOf(curRow)))) {
          bottomPanel.reset();
        }
      }
      
      if (colClicked > 1) {
        update(colClicked, rowClicked, newFilePath);
      } else {
        update(colClicked + 1, rowClicked, newFilePath);
      }
      this_interfacefclose_cancel_selected = false;
      



      if (colClicked == 1) {
        currentHWObjcurr_canvas.setShowImage(true);
        currentHWObjcurr_canvas.setShowData(true);
      } else if (colClicked == 2) {
        currentHWObjcurr_canvas.setShowImage(true);
        currentHWObjcurr_canvas.setShowData(false);
      } else if (colClicked == 3) {
        currentHWObjcurr_canvas.setShowImage(false);
        currentHWObjcurr_canvas.setShowData(true);
        



        if (OCRInterface.currDoc != null) {
          OCRInterface.currDoc.setModified(true);
        }
      }
    }
    

    OCRInterface.this_interface.getCanvas().clearRLEMap();
    OCRInterface.this_interface.updateETextWindow();
    OCRInterface.this_interface.updateCommentsWindow(true);
    CTRL_pressed = false;
    
    if (ocrIF.getEnablePolygonTranscription()) {
      if (setChanged)
      {
        this_interfacePolyTranscribePanel.setDisplayedImageImage(
          OCRInterface.currentHWObj.getOriginalImage()); }
      setPolyTranscribeState(newFileName);
    }
    
    if ((OCRInterface.this_interface.getThresholdingToolbarVisible()) && 
      (setChanged))
    {
      ((ColorImageToolbar)OCRInterface.this_interface.getThresholdingToolbar()).setImage(null);
    }
  }
  
  private void setPolyTranscribeState(String name) {
    String gtType = OCRInterface.this_interface.checkGTType(name);
    if (gtType == null) {
      enableState(0);
    } else if (gtType.contains(".line")) {
      enableState(1);
    } else if (gtType.contains(".transcribe")) {
      enableState(2);
    } else if (gtType.contains(".word")) {
      enableState(3);
    } else if (gtType.contains(".splitword")) {
      enableState(4);
    } else if (gtType.contains(".paw")) {
      enableState(5);
    } else if (gtType.contains(".refine")) {
      enableState(6);
    } else if (gtType.contains(".final"))
      enableState(8);
  }
  
  private void enableState(int state) {
    this_interfacePolyTranscribePanel.enableState(state);
    this_interfacecurrState = state;
  }
  
  private void setGTState(String name) {
    String gtType = OCRInterface.this_interface.checkGTType(name);
    if (gtType == null) {
      doAction(0);
    } else if (gtType.contains(".line")) {
      doAction(1);
    } else if (gtType.contains(".merge")) {
      doAction(2);
    } else if (gtType.contains(".word")) {
      doAction(3);
    } else if (gtType.contains(".splitword")) {
      doAction(4);
    } else if (gtType.contains(".paw")) {
      doAction(5);
    } else if (gtType.contains(".refine")) {
      doAction(6);
    } else if (gtType.contains(".final"))
    {
      doAction(8); }
  }
  
  private void doAction(int state) {
    this_interfaceGTPanel.enableState(state);
    this_interfacecurrState = state;
  }
  








  public boolean isSelectedLocked()
  {
    if (ocrIF.workmodeProps[my_mode].getFilePropsVecSize() == 0) {
      return true;
    }
    if ((curRow >= 0) && (curCol > 1)) {
      FilePropPacket fpp = ocrIF.workmodeProps[my_mode]
        .getElementFilePropVec(curRow);
      if (curCol == 2)
      {
        return fpp.isHardLocked(0); }
      if (curCol == 3) {
        return fpp.isHardLocked(1);
      }
      return false;
    }
    return false;
  }
  

  public void initMode(int mode)
  {
    my_mode = mode;
  }
  






  void initTable(DefaultTableModel model)
  {
    StringRenderer renderer = new StringRenderer();
    setDefaultRenderer(String.class, renderer);
    getTableHeader().setReorderingAllowed(false);
    

    if (ocrIF.getCollapseRows()) {
      model.addColumn("+");
    } else
      model.addColumn("-");
    model.addColumn("Name");
    model.addColumn("Image");
    if (ocrIF.getAllowXmlImage()) {
      model.addColumn("Xml");
    }
    

    if (this_interfaceworkmodeProps[my_mode]
      .getFilePropsVecSize() == 0) {
      createEmptyTable(model);
    }
    

    for (int i = 0; 
        i < this_interfaceworkmodeProps[my_mode].getFilePropsVecSize(); i++) {
      FilePropPacket pkt = this_interfaceworkmodeProps[my_mode]
        .getElementFilePropVec(i);
      
      if (pkt.inRaw()) {
        model.addRow(pkt.getRowData());
      }
    }
    



    if (this_interfaceworkmodeProps[my_mode].getNumDataSets() >= 1) {}
  }
  




  public void betterAddColumn(JTable table, Object headerLabel, Object[] values)
  {
    DefaultTableModel model = (DefaultTableModel)table.getModel();
    TableColumn col = new TableColumn(model.getColumnCount());
    table.setAutoCreateColumnsFromModel(false);
    

    if (table.getAutoCreateColumnsFromModel()) {
      throw new IllegalStateException();
    }
    col.setHeaderValue(headerLabel);
    table.addColumn(col);
    model.addColumn(headerLabel.toString(), values);
  }
  

  public void createEmptyTable(DefaultTableModel def_model)
  {
    Vector<String> vec = new Vector();
    vec.add("No Selection");
    for (int i = 1; i < def_model.getColumnCount(); i++)
      vec.add("");
    def_model.addRow(vec);
  }
  





  public boolean isCellSelected(int row, int column)
    throws IllegalArgumentException
  {
    if (getSelectedRowCount() > 1) {
      return super.isCellSelected(row, column);
    }
    if ((col == 1) && (curRow == row))
      return true;
    if ((row == curRow) && (column == curCol)) {
      return true;
    }
    return false;
  }
  








  public void printDebugData()
  {
    int numRows = getRowCount();
    int numCols = getColumnCount();
    TableModel model = getModel();
    
    System.out.println("Value of data: ");
    for (int i = 0; i < numRows; i++) {
      System.out.print("    row " + i + ":");
      for (int j = 0; j < numCols; j++) {
        System.out.print("  " + model.getValueAt(i, j));
      }
      System.out.println();
    }
    System.out.println("--------------------------");
  }
  
  public void updateRow(int row_num, JCheckBox[] update_row_to)
  {
    for (int i = 0; i < update_row_to.length; i++) {
      if (update_row_to[i].isSelected()) {
        getModel().setValueAt(FilePropPacket.is_there_path, row_num, 
          i + 1);
      }
    }
  }
  
  public int getSelectedRow() {
    return curRow;
  }
  
  private void setCellSizes()
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
  
  public void myClearSelection() {
    curCol = 1;
    curRow = -1;
    selectedFileName = "";
    ocrIF.setTitle("DocLib - GroundTruthing Editor and Document Interface Professional Edition (DL-GEDIPro)");
    
    this_interfacetbdPane.setCurrentFile(selectedFileName);
    super.clearSelection();
  }
  
  public int getCol() {
    return col;
  }
  
  public void setColumnWidths() {
    Dimension tableDim = getPreferredSize();
    



    boolean addZeroRow = ocrIF.workmodeProps[0]
      .isImagesHasVarietyOfExt();
    
    int lockWidth = 0;
    
    if (addZeroRow) {
      lockWidth = 15;
      TableColumn col = getTableHeader().getColumnModel().getColumn(0);
      col.setHeaderRenderer(new MyTableHeaderRenderer());
    }
    

    getColumnModel().getColumn(0).setMinWidth(lockWidth);
    getColumnModel().getColumn(0).setMaxWidth(lockWidth);
    getColumnModel().getColumn(0).setPreferredWidth(lockWidth);
    
    getColumnModel().getColumn(1).setPreferredWidth((int)(0.9D * tableDim.getWidth()));
    getColumnModel().getColumn(2).setPreferredWidth((int)(0.13D * tableDim.getWidth()));
    if (ocrIF.getAllowXmlImage()) {
      getColumnModel().getColumn(3).setPreferredWidth((int)(0.13D * tableDim.getWidth()));
    }
  }
  










  private void setActionPopup(String action)
  {
    String selectedImage = OCRInterface.getCurrentImageDir() + 
      OCRInterface.getImageDirName() + 
      File.separator + 
      newFileName;
    
    String xmlPath = null;
    
    if (newFileName != null) {
      xmlPath = OCRInterface.this_interface.hasMeta(selectedImage);
    }
    if ((action.equals("refresh")) && (xmlPath == null)) {
      return;
    }
    
    if ((xmlPath == null) && (!action.equals("viewComments")) && (!action.equals("deleteComments"))) {
      OCRInterface.this_interface.getML().actionPerformed(
        new ActionEvent(OCRInterface.this_interface, 
        128, 
        "createXML"));
      
      xmlPath = OCRInterface.this_interface.hasMeta(selectedImage);
      

      if (xmlPath == null) {
        return;
      }
    }
    DesktopUtil desktop = new DesktopUtil();
    
    if (desktop._getDesktop() == null)
    {
      try {
        Runtime.getRuntime().exec(System.getenv("windir") + "\\system32\\" + 
          "notepad.exe \"" + xmlPath + "\"");
      } catch (IOException ioe) {
        System.out.println("ERROR: Could not call notepad.exe");
        JOptionPane.showMessageDialog(null, 
          "Could not call notepad.exe", "Notepad call", 
          2);
        return;
      }
      
    }
    else if (action.equals("open")) {
      if (desktop.isOpenSupported()) {
        if ((OCRInterface.currDoc != null) && (currDocisModified))
          OCRInterface.currDoc.dumpData();
        desktop.setOpenAction();
        desktop.launchDefaultApplication(xmlPath);

      }
      

    }
    else if (action.equals("edit")) {
      if (desktop.isEditSupported()) {
        if ((OCRInterface.currDoc != null) && (currDocisModified))
          OCRInterface.currDoc.dumpData();
        desktop.setEditAction();
        desktop.launchDefaultApplication(xmlPath);

      }
      

    }
    else if (action.equals("print")) {
      if (desktop.isPrintSupported()) {
        if ((OCRInterface.currDoc != null) && (currDocisModified))
          OCRInterface.currDoc.dumpData();
        desktop.setPrintAction();
        desktop.launchDefaultApplication(xmlPath);

      }
      

    }
    else if (action.equals("refresh"))
    {
      this_interfacea_file_opened = false;
      OCRInterface.getAttsConfigUtil().reloadCurrentlyOpenedDocument();
      this_interfacea_file_opened = true;
    }
    else if (action.equals("viewComments")) {
      if (CommentsParser.continueToParse()) {
        new CommentsParser();
      }
    } else if (action.equals("deleteComments")) {
      String commFilePath = CommentsParser.getCommentsFilePathFor(selFileName);
      String docName = OCRInterface.currDoc == null ? ImageReaderDrawer.getFile_name() : OCRInterface.currDoc.getFileName();
      if (commFilePath == null) {
        JOptionPane.showMessageDialog(OCRInterface.this_interface, "No comment file for this document.");
      }
      else {
        int answer = JOptionPane.showConfirmDialog(OCRInterface.this_interface, 
          "Do you want to delete comment file for the \"" + docName + 
          "\"?\nAll comments for this document will be deleted.", "Delete file", 
          0, 
          3);
        if ((answer == 2) || (answer == -1) || (answer == 1))
          return;
        new File(commFilePath).delete();
        CommentsParser.setHasNewComments(false, commFilePath);
        OCRInterface.this_interface.updateCommentsWindow(true);
      }
    }
  }
  
















  public String getSelectedImageName()
  {
    return newFileName;
  }
  
  public void removeVarietyOfExtRows(FilePropPacket fpp, int rowOfBaseImage) {
    for (int i = 0; i < fpp.getNumberOfVarietyOfExt(); i++) {
      ((DefaultTableModel)getModel()).removeRow(rowOfBaseImage + 1);
      ocrIF.workmodeProps[my_mode].removeElementFilePropVec(rowOfBaseImage + 1);
    }
  }
  
  public void addVarietyOfExtRows(FilePropPacket fpp, int rowOfBaseImage) {
    for (int i = fpp.getNumberOfVarietyOfExt() - 1; i >= 0; i--)
    {
      ((DefaultTableModel)getModel()).insertRow(rowOfBaseImage + 1, fpp.getVarietyOfExtRowData(i));
      String xmlExt = OCRInterface.this_interface.getFileExtension(fpp.getVarietyOfExt()[i]);
      String imageExt = OCRInterface.this_interface.getFileExtension(ImageReaderDrawer.getFile_path());
      String nameToAdd = fpp.getVarietyOfExt()[i].replace(xmlExt, imageExt);
      ocrIF.addToTable2(nameToAdd, 0, rowOfBaseImage + 1);
    }
  }
  
  public void updateRow(FilePropPacket fpp, int row) {
    Vector<Object> data = fpp.getRowData();
    
    int columnsCount = getColumnCount();
    
    for (int i = 0; i < columnsCount; i++) {
      ((DefaultTableModel)getModel()).setValueAt(data.get(i), row, i);
    }
  }
  




  public void closeDocument()
  {
    String selectedName = (String)getModel().getValueAt(getSelectedRow(), 1);
    String currentImageName = ImageReaderDrawer.file_name;
    
    if (!selectedName.equals(currentImageName)) {
      String msg = "Please select base image (the image that doesn't have an extension in the middle) to close.";
      
      JOptionPane.showMessageDialog(
        OCRInterface.this_interface, 
        msg, "Close Image", 
        2);
      return;
    }
    
    int selectedRow = getSelectedRow();
    
    FilePropPacket fpp = ocrIF.workmodeProps[my_mode]
      .getElementFilePropVec(selectedRow);
    
    if (!fpp.isCollapsed()) {
      removeVarietyOfExtRows(fpp, selectedRow);
    }
    ((DefaultTableModel)getModel()).removeRow(selectedRow);
    ocrIF.workmodeProps[my_mode].removeElementFilePropVec(selectedRow);
    
    if (selectedRow >= getRowCount()) {
      selectedRow = getRowCount() - 1;
    }
    curRow = -1;
    processSelectionEvent(selectedRow, 1, true);
    curRow = selectedRow;
  }
  








  private void overlayAll()
  {
    setCursor(Cursor.getPredefinedCursor(3));
    
    int size = ocrIF.workmodeProps[my_mode].getFilePropsVecSize();
    

    for (int i = 0; i < size; i++) {
      FilePropPacket fpp = ocrIF.workmodeProps[my_mode]
        .getElementFilePropVec(i);
      
      if (fpp.isBaseImage())
      {



        int[] selectedRows = new int[fpp.getNumberOfVarietyOfExt() + 1];
        
        for (int row = 0; row < selectedRows.length; row++) {
          selectedRows[row] = (i + row);
        }
        String baseXmlPath = fpp.getDataFileAt(1);
        String baseImagePath = fpp.getDataFileAt(0);
        String[] toBeOverlaid = fpp.getVarietyOfExt();
        
        overlay(baseXmlPath, baseImagePath, toBeOverlaid, selectedRows);
      }
    }
    ocrIF.update_tables();
    
    curRow = -1;
    processSelectionEvent(0, 1, false);
    curRow = 0;
    
    setCursor(Cursor.getPredefinedCursor(0));
  }
  



















  private void overlay(String baseXmlPath, String baseImagePath, String[] toBeOverlaid, int[] selectedRows)
  {
    OCRInterface.currDoc.dumpData();
    
    String[] xmlNames = new String[selectedRows.length];
    Set<String> repeatedTypes = getRepeatedTypes(selectedRows);
    




    String xmlPath0 = baseXmlPath;
    String fileName0 = new File(xmlPath0).getName();
    String imagePath0 = baseImagePath;
    LoadDataFile datafile0 = new LoadDataFile(xmlPath0, 0, 0);
    
    String imageExt = ocrIF.getFileExtension(imagePath0);
    String baseImageName = new File(ocrIF.getBaseImage(fileName0, imageExt)).getName();
    String xmlPath_base = OCRInterface.getCurrentXmlDir() + 
      OCRInterface.getXmlDirName() + 
      File.separator + 
      ocrIF.getFileNameWithoutExt(baseImageName);
    
    String overlayXmlPath = xmlPath_base + ".overlay.xml";
    
    xmlNames[0] = fileName0;
    
    if (!allowCreateOverlayFile(overlayXmlPath)) {
      return;
    }
    LinkedList<DLPage> pages0 = datafile0.getDocument().getdocPages();
    
    DLPage page;
    for (int i = 0; i < toBeOverlaid.length; i++)
    {
      String xmlPath = toBeOverlaid[i];
      xmlNames[(i + 1)] = new File(xmlPath).getName();
      datafile = new LoadDataFile(xmlPath, 0, 0);
      
      pages_i = datafile.getDocument().getdocPages();
      Iterator localIterator2;
      for (Iterator localIterator1 = pages_i.iterator(); localIterator1.hasNext(); 
          


          localIterator2.hasNext())
      {
        page = (DLPage)localIterator1.next();
        
        Vector<DLZone> zones = datafile.getZonesManager(Integer.parseInt(pageID) - 1)
          .getAsVector();
        localIterator2 = zones.iterator(); continue;DLZone zone = (DLZone)localIterator2.next();
        for (DLPage page0 : pages0) {
          if (dlGetZonePagepageID.equals(pageID)) {
            String zoneType = (String)zone.getZoneTags().get("gedi_type");
            

            if (repeatedTypes.contains(zoneType))
              zone.getZoneTags().put("gedi_type", zoneType + "-" + (i + 2));
            page0.dlAppendZoneWithoutCheck(zone);
          }
        }
      }
    }
    

    String xmlSrcSum = "";
    LinkedList<DLPage> pages_i = (page = xmlNames).length; for (LoadDataFile datafile = 0; datafile < pages_i; datafile++) { String name = page[datafile];
      xmlSrcSum = xmlSrcSum + "; " + name;
    }
    xmlSrcSum = xmlSrcSum.replaceFirst(";", "");
    

    getDocumentdocumentTags.put("XMLMerged", xmlSrcSum.trim());
    


    pages0 = datafile0.getDocument().getdocPages();
    Vector<DLZone> zones; int i; for (datafile = pages0.iterator(); datafile.hasNext(); 
        

        i < zones.size())
    {
      DLPage page = (DLPage)datafile.next();
      zones = datafile0.getZonesManager(Integer.parseInt(pageID) - 1)
        .getAsVector();
      i = 0; continue;
      ((DLZone)zones.get(i)).getZoneTags().put("id", Integer.toString(i + 1));
      ((DLZone)zones.get(i)).getZoneTags().remove("nextZoneID");
      String zoneType = (String)((DLZone)zones.get(i)).getZoneTags().get("gedi_type");
      


      if (repeatedTypes.contains(zoneType)) {
        ((DLZone)zones.get(i)).getZoneTags().put("gedi_type", zoneType + "-1");
      }
      i++;
    }
    












    System.out.println("overlayXmlPath: " + overlayXmlPath);
    System.out.println("datafile0.get_zones_vec(): " + datafile0.get_zones_vec());
    System.out.println("datafile0.getDocument().getdocPages(): " + datafile0.getDocument().getdocPages());
    datafile0.dumpDataAs(overlayXmlPath);
  }
  









  private void overlaySelected()
  {
    int[] selectedRows = ocrIF.ocrTable.getSelectedRows();
    FilePropPacket fpp0 = ocrIF.workmodeProps[my_mode]
      .getElementFilePropVec(selectedRows[0]);
    
    String baseXmlPath = fpp0.getDataFileAt(1);
    String baseImagePath = fpp0.getDataFileAt(0);
    String[] toBeOverlaid = new String[selectedRows.length - 1];
    
    for (int i = 0; i < toBeOverlaid.length; i++) {
      FilePropPacket fpp = ocrIF.workmodeProps[my_mode]
        .getElementFilePropVec(selectedRows[(i + 1)]);
      toBeOverlaid[i] = fpp.getDataFileAt(1);
    }
    System.out.println("baseXmlPath: " + baseXmlPath);
    System.out.println("baseImagePath: " + baseImagePath);
    System.out.println("toBeOverlaid: " + toBeOverlaid);
    overlay(baseXmlPath, baseImagePath, toBeOverlaid, selectedRows);
    
    ocrIF.update_tables();
    
    String imageExt = ocrIF.getFileExtension(baseImagePath);
    String fileName0 = new File(baseXmlPath).getName();
    String baseImageName = new File(ocrIF.getBaseImage(fileName0, imageExt)).getName();
    String xmlPath_base = OCRInterface.getCurrentXmlDir() + 
      OCRInterface.getXmlDirName() + 
      File.separator + 
      ocrIF.getFileNameWithoutExt(baseImageName);
    
    String overlayImagePath = xmlPath_base + ".overlay" + imageExt;
    

    int overlayIndex = ocrIF.workmodeProps[my_mode].getImageIndex(overlayImagePath);
    curRow = -1;
    processSelectionEvent(overlayIndex, 1, false);
    curRow = overlayIndex;
  }
  
  private boolean allowCreateOverlayFile(String overlayPath) {
    if (!new File(overlayPath).exists()) {
      return true;
    }
    if (!ocrIF.getAutoOverwriteOverlayFile()) {
      String msg = overlayPath + 
        "\nalready exists. Do you want overwrite it?" + 
        "\n\nNote: You can turn ON auto overwriting of .overlay in the Preferences menu.";
      
      int answer = JOptionPane.showConfirmDialog(
        OCRInterface.this_interface, 
        msg, "Overlay Data", 
        0, 
        3);
      
      if ((answer == 1) || (answer == -1)) {
        return false;
      }
      return true;
    }
    
    return true;
  }
  












  private Set<String> getRepeatedTypes(int[] selectedRows)
  {
    Set[] allSets = new Set[selectedRows.length];
    Set<String> sumSet = new HashSet();
    Set<String> repeatedTypes = new HashSet();
    
    for (int i = 0; i < selectedRows.length; i++) {
      FilePropPacket fpp = ocrIF.workmodeProps[my_mode]
        .getElementFilePropVec(selectedRows[i]);
      String xmlPath = fpp.getDataFileAt(1);
      try {
        allSets[i] = new TypeLister().listUp(xmlPath);
      } catch (IOException e) {
        System.out.println("WorkmodeTable. checkTypes(). IOException");
        e.printStackTrace();
      } catch (SAXException e) {
        System.out.println("WorkmodeTable. checkTypes(). SAXException");
        e.printStackTrace();
      }
    }
    



    for (int i = 0; i < allSets.length; i++) {
      Set<String> typeSet = allSets[i];
      for (String type : typeSet) {
        boolean added = sumSet.add(type);
        if (!added) {
          repeatedTypes.add(type);
        }
      }
    }
    return repeatedTypes;
  }
  
  private void setWaitCursor() {
    setCursor(Cursor.getPredefinedCursor(3));
    ImageReaderDrawer.picture.setCursor(Cursor.getPredefinedCursor(3));
  }
  
  private void setDefaultCursor() {
    setCursor(Cursor.getPredefinedCursor(0));
    ImageReaderDrawer.picture.setCursor(Cursor.getPredefinedCursor(0));
  }
}
