package ocr.gui;

import gttool.document.DLPage;
import gttool.document.DLZone;
import gttool.misc.Login;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import ocr.JThumb.JThumbList;
import ocr.JThumb.JThumbListModel;
import ocr.gui.leftPanel.AttributeWindow;
import ocr.gui.leftPanel.DatasetSpecificToolPanel;
import ocr.gui.leftPanel.LeftPanel;
import ocr.gui.leftPanel.TypeWindow;
import ocr.manager.GlobalDisableManager;
import ocr.manager.GlobalHotkeyManager;
import ocr.manager.UndoRedoManager;
import ocr.manager.zones.ZonesManager;
import ocr.tif.ImageDisplay;
import ocr.tif.ImageDisplay.ZoneVector;
import ocr.tif.ImageReaderDrawer;
import ocr.tif.color.ColorImageToolbar;
import ocr.util.AttributeConfigUtil;










public class BrowserToolBar
  extends JToolBar
  implements ActionListener, MouseListener
{
  static final long serialVersionUID = 912349823L;
  Vector<ParChldLine> arr = new Vector();
  




  ToolBarButton currButt = null;
  




  ToolBarButton lastButt = null;
  




  public static final CompoundBorder sel_border = new CompoundBorder(
    (LineBorder)BorderFactory.createLineBorder(Color.red), 
    
    new EtchedBorder(1, Color.getColor("LR", Color.RED.brighter().brighter().brighter()), Color.getColor("LR", Color.RED.brighter().brighter().brighter())));
  





  CompoundBorder sel_border_4 = new CompoundBorder(new EtchedBorder(1, Color.red.darker(), Color.red.darker()), new EtchedBorder(1, Color.red.darker(), Color.red.darker()));
  





  public static final CompoundBorder normal_border = new CompoundBorder(
    (LineBorder)BorderFactory.createLineBorder(Color.LIGHT_GRAY), 
    
    new EtchedBorder(1, Color.LIGHT_GRAY, Color.LIGHT_GRAY));
  

  BevelBorder raised = new BevelBorder(0);
  

  BevelBorder lowered = new BevelBorder(1);
  



  private ToolBarButton char_map = null;
  



  Vector<ToolBarButton> func_btns = new Vector();
  



  Insets margins;
  



  Dimension inter_dim;
  



  Dimension intra_dim;
  



  public JComboBox scaleList;
  



  ToolBarButton z_in;
  



  ToolBarButton z_out;
  



  ToolBarButton sl_but;
  



  ToolBarButton dr_but;
  



  ToolBarButton thumbToggle_but;
  



  ToolBarButton readingOrderToggle_but;
  



  ToolBarButton showTextToggle_but;
  



  ToolBarButton autoShrinkToggle_but;
  



  ToolBarButton rotate_but;
  


  public static boolean flag = false;
  
  public static boolean flag2 = false;
  
  public JComboBox thumbList;
  
  private boolean stickyMode = false;
  



  private static final String fitToWindowLabel = "Window";
  


  OCRInterface ocrIF = OCRInterface.this_interface;
  



  public final String[] data = { "400%", "300%", "200%", "150%", "125%", "100%", "75%", "50%", "25%", "12.5%", 
    "Width", "Window" };
  



  JButton create;
  



  public static LinkedList<DLZone> ll = new LinkedList();
  
  public static boolean pcLine = false;
  
  public static boolean showReadingOrder = false;
  
  private boolean showTextOnAllZones = false;
  
  private boolean autoShrink = false;
  
  public static ImageDisplay.ZoneVector actZones = new ImageDisplay.ZoneVector();
  


  public JButton selectBtn;
  

  private DigitTokenPopup digitTokenBtn;
  

  private JButton reverseBtn;
  

  private JButton ligatureReverseBtn;
  


  void createButtons()
  {
    String[] imageFiles = { "select.jpg", 
      "create_RLE.gif", 
      "edit_readingOrder.gif", 
      "shrink.png", 
      "offset_split.png", 
      "remove_overlap1.jpeg", 
      "merge.gif", 
      "split.gif", 
      "compass.png", 
      

      "save.gif", 
      "opendict.gif" };
    String[] toolbarLabels = { "Select", 
      "Generate or remove RLEIMAGE", 
      "Create/Edit Reading Order", 
      "Shrink selected zone(s)", 
      "Split selected zone(s) at offsets", 
      "Remove overlap", 
      "Merge", 
      "Split", 
      "Measure", 
      

      "Save", 
      "Load File/Directory" };
    
    String[] underLabels = { "", "", "", "", "", "", "", "", "", "", "" };
    

    GridLayout gl = new GridLayout(1, toolbarLabels.length);
    
    JPanel buttonPanel = new JPanel(gl);
    
    for (int i = 0; i < toolbarLabels.length; i++)
    {



      JToolBar curButtPan = new JToolBar();
      


      curButtPan.setBorder(new EmptyBorder(0, 1, 0, 1));
      curButtPan.setLayout(new BoxLayout(curButtPan, 0));
      
      ToolBarButton button = new ToolBarButton(Login.localOrInJar("images/" + imageFiles[i], getClass().getClassLoader()));
      if (i == 0)
        selectBtn = button;
      button.setMargin(new Insets(0, 0, 0, 0));
      button.setName(toolbarLabels[i]);
      button.setActionCommand(toolbarLabels[i]);
      button.setToolTipText(toolbarLabels[i]);
      


      button.addActionListener(this);
      if (!button.getActionCommand().equals("Generate or remove RLEIMAGE"))
        func_btns.add(button);
      button.setAlignmentX(0.0F);
      curButtPan.add(button);
      
      JLabel lowLab = new JLabel(underLabels[i]);
      lowLab.setAlignmentX(0.0F);
      curButtPan.add(lowLab);
      buttonPanel.add(curButtPan);
      


      if ((!button.getName().equals("Load File/Directory")) && 
        (!button.getName().equals("Save"))) {
        disableManagerallOpmodeComponents.add(button);
        disableManagerallOpmodeComponents.add(lowLab);
      }
      

      if (i == 0) {
        lastButt = button;
        lastButt.setBorder(normal_border);
      }
      


      if (button.getName().equals("Generate or remove RLEIMAGE")) {
        final ToolBarButton spBut = button;
        button.addMouseListener(new MouseAdapter() {
          public void mousePressed(MouseEvent e) {
            if (spBut.isEnabled()) {
              spBut.setBorder(BrowserToolBar.sel_border);
              currButt = ((ToolBarButton)func_btns.get(0));
              currButt.setBorder(BrowserToolBar.normal_border);
            }
          }
          
          public void mouseReleased(MouseEvent e) { setCursor(Cursor.getPredefinedCursor(3));
            
            OCRInterface.this_interface.getML().actionPerformed(
              new ActionEvent(OCRInterface.this_interface, 
              128, 
              "Generate or remove RLEIMAGE"));
            spBut.setBorder(BrowserToolBar.normal_border);
            currButt = ((ToolBarButton)func_btns.get(0));
            currButt.setBorder(BrowserToolBar.sel_border);
            setCursor(Cursor.getPredefinedCursor(0));
          }
        });
      }
      
      if (button.getName().equals("Create/Edit Reading Order")) {
        final ToolBarButton roBtn = button;
        roBtn.addMouseListener(new MouseAdapter() {
          public void mousePressed(MouseEvent e) {
            if (roBtn.isEnabled()) {
              roBtn.setBorder(BrowserToolBar.sel_border);
              currButt = ((ToolBarButton)func_btns.get(0));
              currButt.setBorder(BrowserToolBar.normal_border);
            }
          }
          
          public void mouseReleased(MouseEvent e) { setCursor(Cursor.getPredefinedCursor(3));
            if (ImageDisplay.activeZones.size() >= 2) {
              OCRInterface.this_interface.autocreateReadingOrder();
              roBtn.setBorder(BrowserToolBar.normal_border);
              currButt = ((ToolBarButton)func_btns.get(0));
              currButt.setBorder(BrowserToolBar.sel_border);
            }
            else {
              OCRInterface.this_interface.setOppmode(17);
            }
            setCursor(Cursor.getPredefinedCursor(0));
          }
        });
      }
      














      if (button.getName().equals("Split")) {
        final ToolBarButton spBut = button;
        button.addMouseListener(new MouseAdapter() {
          public void mouseClicked(MouseEvent e) {
            if (spBut.isEnabled()) {
              if (e.getClickCount() >= 2) {
                stickyMode = true;
                spBut.setBorder(sel_border_4);
              } else {
                stickyMode = false;
                spBut.setBorder(BrowserToolBar.sel_border);
              }
            }
          }
        });
      }
      
      if (button.getName().equals("Merge")) {
        final ToolBarButton mergeBut = button;
        button.addMouseListener(new MouseAdapter() {
          public void mousePressed(MouseEvent e) {
            if (mergeBut.isEnabled()) {
              mergeBut.setBorder(BrowserToolBar.sel_border);
              currButt = ((ToolBarButton)func_btns.get(0));
              currButt.setBorder(BrowserToolBar.normal_border);
            }
          }
          
          public void mouseReleased(MouseEvent e) { setCursor(Cursor.getPredefinedCursor(3));
            
            OCRInterface.this_interface.getML().actionPerformed(
              new ActionEvent(OCRInterface.this_interface, 
              128, 
              "Merge zones"));
            mergeBut.setBorder(BrowserToolBar.normal_border);
            currButt = ((ToolBarButton)func_btns.get(0));
            currButt.setBorder(BrowserToolBar.sel_border);
            setCursor(Cursor.getPredefinedCursor(0));
          }
        });
      }
      




































      if (button.getName().equals("Select")) {
        final ToolBarButton slBut = button;
        button.addMouseListener(new MouseAdapter()
        {
          public void mouseClicked(MouseEvent e) {
            if (slBut.isEnabled()) {
              if (e.getClickCount() >= 2) {
                stickyMode = true;
                slBut.setBorder(sel_border_4);
              } else {
                stickyMode = false;
                slBut.setBorder(BrowserToolBar.sel_border);
              }
            }
            


            this_interfacetbdPane.data_panel.t_window.clear(true);
          }
        });
      }
      
      if (button.getName().equals("Shrink selected zone(s)")) {
        final ToolBarButton shrinkBut = button;
        button.addMouseListener(new MouseAdapter() {
          public void mousePressed(MouseEvent e) {
            if (shrinkBut.isEnabled()) {
              shrinkBut.setBorder(BrowserToolBar.sel_border);
              currButt = ((ToolBarButton)func_btns.get(0));
              currButt.setBorder(BrowserToolBar.normal_border);
            }
          }
          
          public void mouseReleased(MouseEvent e) { setCursor(Cursor.getPredefinedCursor(3));
            
            OCRInterface.this_interface.getML().actionPerformed(
              new ActionEvent(OCRInterface.this_interface, 
              128, 
              "Shrink zone"));
            shrinkBut.setBorder(BrowserToolBar.normal_border);
            currButt = ((ToolBarButton)func_btns.get(0));
            currButt.setBorder(BrowserToolBar.sel_border);
            setCursor(Cursor.getPredefinedCursor(0));
          }
        });
      }
      
      if (button.getName().equals("Split selected zone(s) at offsets")) {
        final ToolBarButton shrinkBut = button;
        button.addMouseListener(new MouseAdapter() {
          public void mousePressed(MouseEvent e) {
            if (shrinkBut.isEnabled()) {
              shrinkBut.setBorder(BrowserToolBar.sel_border);
              currButt = ((ToolBarButton)func_btns.get(0));
              currButt.setBorder(BrowserToolBar.normal_border);
            }
          }
          
          public void mouseReleased(MouseEvent e) { setCursor(Cursor.getPredefinedCursor(3));
            
            OCRInterface.this_interface.getML().actionPerformed(
              new ActionEvent(OCRInterface.this_interface, 
              128, 
              "Split zone at offsets"));
            shrinkBut.setBorder(BrowserToolBar.normal_border);
            currButt = ((ToolBarButton)func_btns.get(0));
            currButt.setBorder(BrowserToolBar.sel_border);
            setCursor(Cursor.getPredefinedCursor(0));
          }
        });
      }
      

      if (button.getName().equals("Measure")) {
        button.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e) {
            OCRInterface.this_interface.setCurrentOppMode(20);
            OCRInterface.this_interface.getCanvas().setStartMeasureMouseEvent(null);
          }
        });
      }
      
      if (button.getName().equals("Remove overlap")) {
        final ToolBarButton overlap = button;
        button.addMouseListener(new MouseAdapter() {
          public void mousePressed(MouseEvent e) {
            if (overlap.isEnabled()) {
              overlap.setBorder(BrowserToolBar.sel_border);
              currButt = ((ToolBarButton)func_btns.get(0));
              currButt.setBorder(BrowserToolBar.normal_border);
            }
          }
          
          public void mouseReleased(MouseEvent e) { setCursor(Cursor.getPredefinedCursor(3));
            
            System.out.println("remove overlap (from Toolbar)");
            
            OCRInterface.this_interface.getML().actionPerformed(
              new ActionEvent(OCRInterface.this_interface, 
              128, 
              "Remove overlap"));
            overlap.setBorder(BrowserToolBar.normal_border);
            currButt = ((ToolBarButton)func_btns.get(0));
            currButt.setBorder(BrowserToolBar.sel_border);
            setCursor(Cursor.getPredefinedCursor(0));
          }
        });
      }
    }
    

    add(buttonPanel);
  }
  



  public BrowserToolBar()
  {
    margins = new Insets(0, 0, 0, 0);
    inter_dim = new Dimension(5, 5);
    intra_dim = new Dimension(5, 5);
    

    addMouseListener(this);
    
    setLayout(new WrapLayout(0));
    






    createButtons();
    add(getGEDISeparator());
    


    createZoomPart();
    
    JPanel curButtPan = new JPanel();
    
    curButtPan.setLayout(new BoxLayout(curButtPan, 0));
    

    dr_but = new ToolBarButton(Login.localOrInJar("images/handtool.gif", getClass().getClassLoader()));
    dr_but.setName("Drag");
    dr_but.setActionCommand("DRAG");
    dr_but.setToolTipText("Drag Image");
    dr_but.setMargin(margins);
    dr_but.addMouseListener(new MouseAdapter() {
      final ToolBarButton drBut = dr_but;
      boolean flag = false;
      
      public void mouseClicked(MouseEvent e) { if (!flag) {
          drBut.setBorder(BrowserToolBar.sel_border);
          flag = true;
        }
        else {
          drBut.setBorder(BrowserToolBar.normal_border);
          OCRInterface.this_interface.setOppmode(0);
          flag = false;
        }
      }
    });
    dr_but.addActionListener(this);
    dr_but.setAlignmentX(0.5F);
    JLabel lowLab = new JLabel(" Drag");
    lowLab.setAlignmentX(0.5F);
    

    add(dr_but);
    add(curButtPan);
    





    rotate_but = new ToolBarButton(Login.localOrInJar("images/rotate0.png", getClass().getClassLoader()));
    rotate_but.setToolTipText("Rotate Image");
    rotate_but.setName("Rotate");
    rotate_but.setActionCommand("ROTATE");
    rotate_but.setMargin(margins);
    rotate_but.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e) {
        setCursor(Cursor.getPredefinedCursor(3));
        OCRInterface.this_interface.getCanvas().clearRLEMap();
        OCRInterface.this_interface.getCanvas().saveCurrentState();
        int rotate = (OCRInterface.this_interface.getCurrentRotateDegrees() + 90) % 360;
        currentHWObjcurr_canvas.rotateImage(rotate);
        this_interfacetbdPane.data_panel.a_window.getPage().rotate();
        this_interfacetbdPane.data_panel.a_window.getPage().pageTags.put("GEDI_orientation", OCRInterface.this_interface.getCurrentRotateDegrees());
        setCursor(Cursor.getPredefinedCursor(0));
      }
      

    });
    rotate_but.addMouseListener(new MouseAdapter() {
      final ToolBarButton drBut = rotate_but;
      
      public void mousePressed(MouseEvent e) { drBut.setBorder(BrowserToolBar.sel_border); }
      
      public void mouseReleased(MouseEvent e) {
        drBut.setBorder(BrowserToolBar.normal_border);
        OCRInterface.this_interface.setOppmode(0);
      }
    });
    rotate_but.addActionListener(this);
    rotate_but.setAlignmentX(0.5F);
    add(rotate_but);
    add(curButtPan);
    

    add(getGEDISeparator());
    


    readingOrderToggle_but = new ToolBarButton(Login.localOrInJar("images/show_readingOrder.gif", getClass().getClassLoader()));
    readingOrderToggle_but.setMargin(new Insets(0, 0, 0, 0));
    readingOrderToggle_but.setName("ReadingOrderToggle");
    readingOrderToggle_but.setActionCommand("ReadingOrderToggle");
    readingOrderToggle_but.setToolTipText("Show Reading Order");
    readingOrderToggle_but.addActionListener(this);
    func_btns.add(readingOrderToggle_but);
    readingOrderToggle_but.setAlignmentX(0.0F);
    curButtPan.add(readingOrderToggle_but);
    disableManagerallOpmodeComponents.add(readingOrderToggle_but);
    if (readingOrderToggle_but.getName().equals("ReadingOrderToggle")) {
      final ToolBarButton roBut = readingOrderToggle_but;
      readingOrderToggle_but.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
          if (roBut.isEnabled()) {
            if (!BrowserToolBar.showReadingOrder) {
              BrowserToolBar.showReadingOrder = true;
              roBut.setBorder(BrowserToolBar.sel_border);
              OCRInterface.this_interface.setShowReadingOrder(true);
              OCRInterface.getAttsConfigUtil().setSelectedShowReadingOrderBox(true);
              



              OCRInterface.this_interface.setOppmode(0);
              oppmodeChanged(0);
            }
            else {
              BrowserToolBar.showReadingOrder = false;
              roBut.setBorder(BrowserToolBar.normal_border);
              OCRInterface.this_interface.setShowReadingOrder(false);
              OCRInterface.getAttsConfigUtil().setSelectedShowReadingOrderBox(false);
              OCRInterface.this_interface.setOppmode(0);
              oppmodeChanged(0);
            }
            OCRInterface.this_interface.getCanvas().paintCanvas();
          }
        }
      });
    }
    add(readingOrderToggle_but);
    


    showTextToggle_but = new ToolBarButton(Login.localOrInJar("images/show_text.gif", getClass().getClassLoader()));
    showTextToggle_but.setMargin(new Insets(0, 0, 0, 0));
    showTextToggle_but.setName("ShowTextToggle");
    showTextToggle_but.setActionCommand("ShowTextToggle");
    showTextToggle_but.setToolTipText("Show text on all zones");
    showTextToggle_but.addActionListener(this);
    
    showTextToggle_but.setAlignmentX(0.0F);
    curButtPan.add(showTextToggle_but);
    disableManagerallOpmodeComponents.add(showTextToggle_but);
    if (showTextToggle_but.getName().equals("ShowTextToggle")) {
      final ToolBarButton textBut = showTextToggle_but;
      showTextToggle_but.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
          if (textBut.isEnabled()) {
            if (!showTextOnAllZones) {
              showTextOnAllZones = true;
              textBut.setBorder(BrowserToolBar.sel_border);
              OCRInterface.this_interface.setShowTextOnAllZones(true);
              OCRInterface.getAttsConfigUtil().setSelectedShowTextOnAllZonesBox(true);
              



              OCRInterface.this_interface.setOppmode(0);
              oppmodeChanged(0);
            }
            else {
              showTextOnAllZones = false;
              textBut.setBorder(BrowserToolBar.normal_border);
              OCRInterface.this_interface.setShowTextOnAllZones(false);
              OCRInterface.getAttsConfigUtil()
                .setSelectedShowTextOnAllZonesBox(false);
              OCRInterface.this_interface.setOppmode(0);
              oppmodeChanged(0);
            }
            OCRInterface.this_interface.getCanvas().paintCanvas();
          }
        }
      });
    }
    add(showTextToggle_but);
    

    autoShrinkToggle_but = new ToolBarButton(Login.localOrInJar("images/shrinkToggle.png", getClass().getClassLoader()));
    autoShrinkToggle_but.setMargin(new Insets(0, 0, 0, 0));
    autoShrinkToggle_but.setName("ShrinkToggle");
    autoShrinkToggle_but.setActionCommand("ShrinkToggle");
    autoShrinkToggle_but.setToolTipText("Auto shrink on drawing");
    autoShrinkToggle_but.addActionListener(this);
    

    curButtPan.add(autoShrinkToggle_but);
    disableManagerallOpmodeComponents.add(autoShrinkToggle_but);
    
    autoShrinkToggle_but.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        if (autoShrinkToggle_but.isEnabled()) {
          if (!autoShrink) {
            autoShrink = true;
            autoShrinkToggle_but.setBorder(BrowserToolBar.sel_border);
            OCRInterface.this_interface.setApplyExpandShrink(true);
            OCRInterface.getAttsConfigUtil().setSelectedAutoZoneShrinkRB(true);
            



            OCRInterface.this_interface.setOppmode(0);
            oppmodeChanged(0);
          }
          else {
            autoShrink = false;
            autoShrinkToggle_but.setBorder(BrowserToolBar.normal_border);
            OCRInterface.this_interface.setApplyExpandShrink(false);
            OCRInterface.getAttsConfigUtil().setSelectedAutoZoneShrinkRB(false);
            OCRInterface.this_interface.setOppmode(0);
            oppmodeChanged(0);
          }
          OCRInterface.this_interface.getCanvas().paintCanvas();
        }
        
      }
    });
    add(autoShrinkToggle_but);
    

    showReadingOrder = OCRInterface.this_interface.getShowReadingOrder();
    showTextOnAllZones = OCRInterface.this_interface.getShowTextOnAllZones();
    autoShrink = OCRInterface.this_interface.getApplyExpandShrink();
    









































































































    JLabel lowLab2 = new JLabel(" Parent/Child");
    lowLab2.setAlignmentX(0.5F);
    



























































    add(getGEDISeparator());
    





    add(new JLabel("Color by:  "));
    add(OCRInterface.getAttsConfigUtil().getAttributeListBox());
    



    add(getGEDISeparator());
    

    createThumbnailPart();
    
    add(getGEDISeparator());
    




    digitTokenBtn = new DigitTokenPopup();
    add(digitTokenBtn);
    if (!ocrIF.getEnableDigitTokenPopup()) {
      setDigitTokenBtnVisible(false);
    }
    reverseBtn = new JButton("R");
    reverseBtn.setFocusable(false);
    reverseBtn.setPreferredSize(new Dimension(30, 25));
    
    reverseBtn.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent arg0) {
        if ((ImageDisplay.activeZones == null) || (ImageDisplay.activeZones.isEmpty())) {
          JOptionPane.showMessageDialog(
            ocrIF, 
            "Select zone(s) to reverse content.\n\nNOTE: use CTRL-A to select all zones.", 
            
            "Reverse content", 
            1);
        } else {
          OCRInterface.this_interface.getCanvas().saveCurrentState();
          for (DLZone zone : ImageDisplay.activeZones) {
            String[] words = zone.getContents().split("\\s+");
            String newContent = "";
            for (String word : words)
            {
              char[] chars = word.toCharArray();
              String hexStr = "";
              for (char c : chars) {
                String hexString = Integer.toHexString(c);
                if (hexString.length() == 3) {
                  hexString = "0" + hexString;
                }
                hexStr = hexStr + hexString + " ";
              }
              System.out.println("\n{reversing whole string: zoneID: " + zoneID);
              System.out.println("before flip: " + hexStr);
              

              word = OCRInterface.this_interface.reverseArabicString(word);
              
              chars = word.toCharArray();
              hexStr = "";
              for (char c : chars) {
                String hexString = Integer.toHexString(c);
                if (hexString.length() == 3) {
                  hexString = "0" + hexString;
                }
                hexStr = hexStr + hexString + " ";
              }
              
              System.out.println("after flip:  " + hexStr + "}");
              
              newContent = newContent + " " + word;
            }
            

            zone.setContents(newContent.trim());
          }
          this_interfacetbdPane.data_panel.a_window.showZoneInfo(ImageDisplay.activeZones);
        }
      }
    });
    add(reverseBtn);
    
    ligatureReverseBtn = new JButton("LR");
    ligatureReverseBtn.setFocusable(false);
    ligatureReverseBtn.setPreferredSize(new Dimension(30, 25));
    
    ligatureReverseBtn.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent arg0) {
        if ((ImageDisplay.activeZones == null) || (ImageDisplay.activeZones.isEmpty())) {
          JOptionPane.showMessageDialog(
            ocrIF, 
            "Select zone(s) to reverse content.\n\nNOTE: use CTRL-A to select all zones.", 
            
            "Reverse content", 
            1);
        } else {
          OCRInterface.this_interface.getCanvas().saveCurrentState();
          for (DLZone zone : ImageDisplay.activeZones) {
            String[] words = zone.getContents().split("\\s+");
            String newContent = "";
            for (String word : words)
            {
              char[] chars = word.toCharArray();
              String hexStr = "";
              String hexString; for (char c : chars) {
                hexString = Integer.toHexString(c);
                if (hexString.length() == 3) {
                  hexString = "0" + hexString;
                }
                hexStr = hexStr + hexString + " ";
              }
              System.out.println("\n{reversing ligature: zoneID: " + zoneID);
              System.out.println("before flip: " + hexStr);
              

              word = OCRInterface.this_interface.reverseArabicLigature(word);
              
              chars = word.toCharArray();
              String old = hexStr;
              hexStr = "";
              for (char c : chars) {
                String hexString = Integer.toHexString(c);
                if (hexString.length() == 3) {
                  hexString = "0" + hexString;
                }
                hexStr = hexStr + hexString + " ";
              }
              
              if (old.trim().equals(hexStr.trim())) {
                System.out.println("after flip:  " + hexStr + " -- no ligatures found; nothing has been reversed}");
              } else {
                System.out.println("after flip:  " + hexStr + "}");
              }
              newContent = newContent + " " + word;
            }
            

            zone.setContents(newContent.trim());
          }
          this_interfacetbdPane.data_panel.a_window.showZoneInfo(ImageDisplay.activeZones);
        }
      }
    });
    add(ligatureReverseBtn);
    
    if (!ocrIF.getEnableReverseButton()) {
      setReverseBtnVisible(false);
    }
    






    ocrIF = OCRInterface.this_interface;
    setCursor(Cursor.getPredefinedCursor(0));
  }
  































































  public void showDestroyDialog()
  {
    LoadDataFile ld = OCRInterface.currDoc;
    ZonesManager shapeVec = ld.get_zones_vec();
    
    UndoRedoManager undoManager = new UndoRedoManager();
    
    DLPage dlp = shapeVec.getPage();
    
    Vector<DLZone> vec = dlp.dlGetAllZones();
    


    Object[] options = { " ON ", " OFF ", "CANCEL" };
    
    final JOptionPane optionPane = new JOptionPane(
      "Turn Hierarchy:", 
      3, -1, null, options);
    






    OCRInterface.dialog_open = true;
    
    final JDialog dialog = new JDialog(OCRInterface.this_interface, 
      "Hierarchy Design", true);
    OCRInterface.setDialog(dialog);
    dialog.setContentPane(optionPane);
    
    dialog.setDefaultCloseOperation(0);
    
    optionPane.addPropertyChangeListener(new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName();
        
        if ((dialog.isVisible()) && 
          (e.getSource() == optionPane) && 
          (prop.equals("value"))) {
          dialog.setVisible(false);
        }
        
      }
      
    });
    dialog.setLocation(new Point(500, 300));
    dialog.pack();
    dialog.setVisible(true);
    String value = (String)optionPane.getValue();
    

    if (value.trim().equals("ON"))
    {
      OCRInterface.this_interface.getCanvas().saveCurrentState();
      for (int i = 0; i < vec.size(); i++) {
        ll.add((DLZone)vec.elementAt(i));
      }
      shapeVec.yesHierarchy(ll);
      ld.setModified(true);
      if (OCRInterface.currDoc != null)
        OCRInterface.currDoc.setModified(true);
      OCRInterface.this_interface.updateCurrFilename();
    } else if (value.trim().equals("OFF"))
    {
      OCRInterface.this_interface.getCanvas().saveCurrentState();
      for (int i = 0; i < vec.size(); i++) {
        ll.add((DLZone)vec.elementAt(i));
      }
      shapeVec.noHierarchy(ll);
      ld.setModified(true);
      if (OCRInterface.currDoc != null)
        OCRInterface.currDoc.setModified(true);
      OCRInterface.this_interface.updateCurrFilename();
    }
    


    OCRInterface.dialog_open = false;
  }
  

  void enablePCLine()
  {
    LoadDataFile ldf = OCRInterface.currDoc;
    ZonesManager zm = ldf.get_zones_vec();
    
    DLPage dlp = zm.getPage();
    
    Vector<DLZone> vec = dlp.dlGetAllZones();
    
    arr.clear();
    
    for (int i = 0; i < vec.size(); i++)
    {
      if (((DLZone)vec.elementAt(i)).dlHasChildZones()) {
        LinkedList<DLZone> childList = ((DLZone)vec.elementAt(i)).getChildrenZones();
        
        for (int j = 0; j < childList.size(); j++)
        {
          ParChldLine dl = new ParChldLine(vec, i, childList, j);
          arr.add(dl);
        }
      }
    }
    
    OCRInterface.setVec(arr);
  }
  
  public Vector<ParChldLine> getArr()
  {
    return arr;
  }
  






  void disablePCLine()
  {
    LoadDataFile ldf = OCRInterface.currDoc;
    ZonesManager zm = ldf.get_zones_vec();
    
    DLPage dlp = zm.getPage();
    
    Vector<DLZone> vec = dlp.dlGetAllZones();
    
    for (int i = 0; i < vec.size(); i++)
    {
      if (((DLZone)vec.elementAt(i)).dlHasChildZones()) {
        LinkedList<DLZone> childList = ((DLZone)vec.elementAt(i)).getChildrenZones();
        
        for (int j = 0; j < childList.size(); j++)
        {
          ParChldLine dl = new ParChldLine(vec, i, childList, j);
          Graphics localGraphics = getGraphics();
        }
      }
    }
  }
  







  void createThumbnailPart()
  {
    thumbToggle_but = new ToolBarButton(Login.localOrInJar("images/thumbnail.gif", getClass().getClassLoader()));
    thumbToggle_but.setName("Thumbnail Toggle");
    thumbToggle_but.setActionCommand("ThumbnailToggle");
    thumbToggle_but.setToolTipText("Thumbnail Toggle");
    thumbToggle_but.setMargin(margins);
    thumbToggle_but.addActionListener(this);
    add(thumbToggle_but);
    
    String[] thumbAmounts = { "1", "4", "9", "16", "25", "36" };
    thumbList = new JComboBox(thumbAmounts);
    thumbList.addActionListener(this);
    thumbList.setActionCommand("thumbListChanged");
    thumbList.setToolTipText("viewable thumbnail amount");
    add(thumbList);
  }
  





  void createZoomPart()
  {
    JLabel scaleLbl = new JLabel("Zoom: ");
    scaleLbl.setForeground(Color.black);
    add(scaleLbl);
    disableManagerallOpmodeComponents.add(scaleLbl);
    


    scaleList = new JComboBox(data);
    scaleList.setSelectedIndex(2);
    scaleList.addActionListener(this);
    scaleList.setActionCommand("scaleListChanged");
    
    scaleList.setToolTipText("Zoom Factor");
    add(scaleList);
    disableManagerallOpmodeComponents.add(scaleList);
    

    z_in = new ToolBarButton(Login.localOrInJar("images/zoomin.gif", getClass().getClassLoader()))
    {
      public void fireActionPerformed(ActionEvent ae)
      {
        fireZoomIn();
      }
    };
    z_in.setToolTipText("Zoom In");
    z_in.setMargin(margins);
    add(z_in);
    disableManagerallOpmodeComponents.add(z_in);
    
    z_out = new ToolBarButton(Login.localOrInJar("images/zoomout.gif", getClass().getClassLoader()))
    {
      public void fireActionPerformed(ActionEvent ae)
      {
        fireZoomOut();
      }
    };
    z_out.setToolTipText("Zoom Out");
    z_out.setMargin(margins);
    add(z_out);
    disableManagerallOpmodeComponents.add(z_out);
  }
  

  public void setCharMapVisible(boolean is_enabled)
  {
    char_map.setEnabled(is_enabled);
  }
  









  public void setScaleByIndex(int index)
  {
    if ((index >= 0) && (index <= 11)) {
      scaleList.setSelectedIndex(index);
    }
  }
  



  public int getCurrentScaleIndex()
  {
    return scaleList.getSelectedIndex();
  }
  





  public String getScale()
  {
    return (String)scaleList.getSelectedItem();
  }
  





  public void fireZoomIn()
  {
    int currentIndex = scaleList.getSelectedIndex();
    
    if (currentIndex == 0) {
      return;
    }
    if (currentIndex < 10) {
      setScaleByIndex(currentIndex - 1);
    } else {
      setScaleByIndex(currentIndex - 3);
    }
    


















    GlobalHotkeyManager hotkeyManager = GlobalHotkeyManager.getInstance();
    hotkeyManager.setEnabled(true);
  }
  














  public void fireZoomOut()
  {
    int currentIndex = scaleList.getSelectedIndex();
    
    if (currentIndex == 9) {
      return;
    }
    if (currentIndex < 9) {
      setScaleByIndex(currentIndex + 1);
    } else {
      setScaleByIndex(currentIndex - 2);
    }
    


















    GlobalHotkeyManager hotkeyManager = GlobalHotkeyManager.getInstance();
    hotkeyManager.setEnabled(true);
  }
  














  public void oppmodeChanged(int newOppMode)
  {
    OCRInterface ocrIF = OCRInterface.this_interface;
    
    switch (newOppMode) {
    case 14: 
      currButt = ((ToolBarButton)func_btns.get(5));
      
      lastButt.setBorder(normal_border);
      currButt.setBorder(sel_border);
      tbdPane.data_panel.t_window.clear(false);
      
      break;
    
    case 13: 
      lastButt.setBorder(normal_border);
      currButt.setBorder(sel_border);
      tbdPane.data_panel.t_window.clear(false);
      break;
    case 12: 
      currButt = ((ToolBarButton)func_btns.get(4));
      
      lastButt.setBorder(normal_border);
      currButt.setBorder(sel_border);
      tbdPane.data_panel.t_window.clear(false);
      
      break;
    
    case 11: 
      lastButt.setBorder(normal_border);
      currButt.setBorder(sel_border);
      tbdPane.data_panel.t_window.clear(false);
      break;
    case 8: 
      currButt = dr_but;
      lastButt.setBorder(normal_border);
      currButt.setBorder(sel_border);
      tbdPane.data_panel.t_window.clear(false);
      break;
    case 17: 
      currButt = ((ToolBarButton)func_btns.get(1));
      lastButt.setBorder(normal_border);
      currButt.setBorder(sel_border);
      tbdPane.data_panel.t_window.clear(false);
      



      break;
    
    case 6: 
      lastButt.setBorder(normal_border);
      currButt.setBorder(sel_border);
      tbdPane.data_panel.t_window.clear(false);
      break;
    
    case 7: 
      lastButt.setBorder(normal_border);
      currButt.setBorder(sel_border);
      tbdPane.data_panel.t_window.clear(false);
      break;
    case 0: 
      currButt = ((ToolBarButton)func_btns.get(0));
      
      if (showReadingOrder) {
        readingOrderToggle_but.setBorder(sel_border);
      }
      if (showTextOnAllZones) {
        showTextToggle_but.setBorder(sel_border);
      }
      if (autoShrink) {
        autoShrinkToggle_but.setBorder(sel_border);
      }
      if ((lastButt != readingOrderToggle_but) && 
        (lastButt != showTextToggle_but) && 
        (lastButt != autoShrinkToggle_but)) {
        lastButt.setBorder(normal_border);
      }
      currButt.setBorder(sel_border);
      
      tbdPane.data_panel.t_window.clear(false);
      break;
    case 1: case 2: case 3: case 4: case 5: case 9: case 10: case 15: case 16: default: 
      currButt = ((ToolBarButton)func_btns.get(0));
      if (showReadingOrder) {
        readingOrderToggle_but.setBorder(sel_border);
      }
      if (showTextOnAllZones) {
        showTextToggle_but.setBorder(sel_border);
      }
      if (autoShrink) {
        autoShrinkToggle_but.setBorder(sel_border);
      }
      if ((lastButt != readingOrderToggle_but) && 
        (lastButt != showTextToggle_but) && 
        (lastButt != autoShrinkToggle_but)) {
        lastButt.setBorder(normal_border);
      }
      currButt.setBorder(sel_border);
    }
    
    


    lastButt = currButt;
  }
  







  public void actionPerformed(ActionEvent e)
  {
    ImageDisplay myCanvas = currentHWObjcurr_canvas;
    

    if (e.getActionCommand().equals("scaleListChanged"))
    {



      if (getScale() == null)
        return;
      enableDisableZoomButtons();
      
      if (myCanvas != null) {
        if (scaleList.getSelectedItem().equals(data[0])) {
          myCanvas.changeScale(4.0F);
        } else if (scaleList.getSelectedItem().equals(data[1])) {
          myCanvas.changeScale(3.0F);
        } else if (scaleList.getSelectedItem().equals(data[2])) {
          myCanvas.changeScale(2.0F);
        } else if (scaleList.getSelectedItem().equals(data[3])) {
          myCanvas.changeScale(1.5F);
        } else if (scaleList.getSelectedItem().equals(data[4])) {
          myCanvas.changeScale(1.25F);
        } else if (scaleList.getSelectedItem().equals(data[5])) {
          myCanvas.changeScale(1.0F);
        } else if (scaleList.getSelectedItem().equals(data[6])) {
          myCanvas.changeScale(0.75F);
        } else if (scaleList.getSelectedItem().equals(data[7])) {
          myCanvas.changeScale(0.5F);
        } else if (scaleList.getSelectedItem().equals(data[8])) {
          myCanvas.changeScale(0.25F);
        } else if (scaleList.getSelectedItem().equals(data[9])) {
          myCanvas.changeScale(0.125F);
        }
        else if (scaleList.getSelectedItem().equals(data[10])) {
          myCanvas.fitImageToWidth(false, false);
        } else if (scaleList.getSelectedItem().equals(data[11])) {
          myCanvas.fitImageToWidth(true, false);
        }
      }
    } else if (e.getActionCommand().equals("thumbListChanged")) {
      int amount = new Integer(thumbList.getSelectedItem().toString())
        .intValue();
      ocrIF.model.getList().resizeCells(amount);
      ocrIF.selectThumbnailPanel();
      ocrIF.pane.grabFocus();


    }
    else
    {

      currButt = ((ToolBarButton)e.getSource());
      boolean flag = false;
      
      if (currButt.getName().equals("Save"))
      {

        if (this_interfacea_file_opened)
        {


          this_interfacesaveFilesDialog.saveData(); }
        flag = true;
      } else if (!currButt.getName().equals("CreateParent"))
      {





        if (currButt.getName().equals("Load File/Directory"))
        {

          this_interfacemenuListener.actionPerformed("Load File/Directory");
          flag = true;
        } else if (currButt.getName().equals("Thumbnail Toggle"))
        {
          ocrIF.panelToggle();
          flag = true;
        }
      }
      
      if (flag) {
        return;
      }
      if (lastButt != null) {
        lastButt.setBorder(normal_border);
      }
      
      if (!currButt.getName().equals("Hierarchy (On/Off)")) {
        currButt.setBorder(sel_border);
      }
      if (currButt.getName().equals("Create"))
      {
        OCRInterface.this_interface.setOppmode(2);
      } else if (currButt.getName().equals("Edit")) {
        OCRInterface.this_interface.setOppmode(3);
      } else if (currButt.getName().equals("Delete"))
      {
        OCRInterface.this_interface.setOppmode(5);
      } else if (currButt.getName().equals("Move")) {
        OCRInterface.this_interface.setOppmode(4);
      } else if (currButt.getName().equals("Split")) {
        OCRInterface.this_interface.setOppmode(7);
      } else if (currButt.getName().equals("Select")) {
        OCRInterface.this_interface.setOppmode(0);
      } else if (currButt.getName().equals("Read Order Select"))
      {
        OCRInterface.this_interface.setOppmode(1);
      } else if (currButt.getName().equals("Drag")) {
        OCRInterface.this_interface.setOppmode(8);
      } else if (!currButt.getName().equals("Parent/Child"))
      {
        if (currButt.getName().equals("DrawPCLine")) {
          OCRInterface.this_interface.setOppmode(12);
        } else if (currButt.getName().equals("ErasePCL")) {
          OCRInterface.this_interface.setOppmode(14);
        } else if (currButt.getName().equals("Hierarchy (On/Off)")) {
          OCRInterface.this_interface.setOppmode(0);
        } else if (currButt.getName().equals("CreateParent"))
          OCRInterface.this_interface.setOppmode(13);
      }
      lastButt = currButt;
    }
    setCursor(Cursor.getPredefinedCursor(0));
  }
  
  private void enableDisableZoomButtons()
  {
    if (getScale().equals("400%")) {
      z_in.setEnabled(false);
      z_out.setEnabled(true);


    }
    else if (getScale().equals("12.5%")) {
      z_out.setEnabled(false);
      z_in.setEnabled(true);
    }
    else {
      z_out.setEnabled(true);
      z_in.setEnabled(true);
    }
  }
  
  public void mouseClicked(MouseEvent me) {
    if ((me.getModifiers() & 0x4) != 0) {}
  }
  


  public void mouseEntered(MouseEvent me) {}
  


  public void mouseExited(MouseEvent me) {}
  

  public void mousePressed(MouseEvent me) {}
  

  public void mouseReleased(MouseEvent me) {}
  

  public static LinkedList<DLZone> getLL()
  {
    return ll;
  }
  
  public boolean getStickyMode() { return stickyMode; }
  
  public void setStickyMode(boolean active)
  {
    stickyMode = active;
  }
  



  public static void setActZones(ImageDisplay.ZoneVector zv)
  {
    actZones = zv;
  }
  
  public ImageDisplay.ZoneVector getActZones() {
    return actZones;
  }
  









































  public boolean isShowText()
  {
    return showTextOnAllZones;
  }
  
  public void setShowText(boolean showText) {
    showTextOnAllZones = showText;
    if (showText) {
      showTextToggle_but.setBorder(sel_border);
    } else
      showTextToggle_but.setBorder(normal_border);
  }
  
  public void setShowReadingOrder(boolean showRO) {
    showReadingOrder = showRO;
    if (showRO) {
      readingOrderToggle_but.setBorder(sel_border);
    } else
      readingOrderToggle_but.setBorder(normal_border);
  }
  
  public void setAutoShrink(boolean autoShrink) {
    this.autoShrink = autoShrink;
    if (autoShrink) {
      autoShrinkToggle_but.setBorder(sel_border);
    } else
      autoShrinkToggle_but.setBorder(normal_border);
  }
  
  public void setRotateButtonIcon(int newRotateDegrees) {
    String iconPath = null;
    switch (newRotateDegrees) {
    case 0:  iconPath = "/images/rotate0.png";
      break;
    case 90:  iconPath = "/images/rotate90.png";
      break;
    case 180:  iconPath = "/images/rotate180.png";
      break;
    case 270:  iconPath = "/images/rotate270.png";
      break;
    default: 
      iconPath = "/images/rotate0.png";
    }
    
    
    rotate_but.setIcon(new ImageIcon(BrowserToolBar.class.getResource(iconPath)));
  }
  
  public void setSelectedReadingOrderBtn(boolean b) {
    showReadingOrder = b;
    
    if (b) {
      readingOrderToggle_but.setBorder(sel_border);
    } else
      readingOrderToggle_but.setBorder(normal_border);
  }
  
  public void setDigitTokenBtnVisible(boolean b) {
    digitTokenBtn.setVisible(b);
    if (b)
      digitTokenBtn.setHotkey();
  }
  
  public void setReverseBtnVisible(boolean b) {
    reverseBtn.setVisible(b);
    ligatureReverseBtn.setVisible(b);
  }
  
  private JLabel getGEDISeparator() {
    return new JLabel(new ImageIcon(ColorImageToolbar.class.getResource("/images/separator.png")));
  }
}
