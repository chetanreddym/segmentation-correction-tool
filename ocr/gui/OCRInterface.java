package ocr.gui;

import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import gttool.document.ConnectedComponent;
import gttool.document.DLDocument;
import gttool.document.DLPage;
import gttool.document.DLZone;
import gttool.exceptions.DLException;
import gttool.io.TypeLister;
import gttool.io.XmlConstant;
import gttool.misc.Attribute;
import gttool.misc.LoadGEDIProps;
import gttool.misc.LoadUserProps;
import gttool.misc.Login;
import gttool.misc.OptionParser;
import gttool.misc.TypeAttributeEntry;
import gttool.misc.TypeSettings;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import javax.imageio.ImageIO;
import javax.media.jai.PlanarImage;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.RootPaneContainer;
import javax.swing.UIManager;
import javax.swing.event.MenuEvent;
import javax.swing.plaf.metal.MetalToolBarUI;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.JTextComponent;
import ocr.JThumb.JThumbList;
import ocr.JThumb.JThumbListModel;
import ocr.gui.fileDrop.FileDrop;
import ocr.gui.leftPanel.AttributeWindow;
import ocr.gui.leftPanel.DatasetSpecificToolPanel;
import ocr.gui.leftPanel.ElectronicTextDisplayer;
import ocr.gui.leftPanel.EncodingPanel;
import ocr.gui.leftPanel.FilePropPacket;
import ocr.gui.leftPanel.InfoLabel;
import ocr.gui.leftPanel.LeftPanel;
import ocr.gui.leftPanel.PopUpChip;
import ocr.gui.leftPanel.TypeWindow;
import ocr.gui.leftPanel.WorkmodeTable;
import ocr.gui.workflows.GTToolBar;
import ocr.gui.workflows.PolyTranscribeToolBar;
import ocr.gui.workflows.TranslateToolBar;
import ocr.manager.ExternalProcessManager;
import ocr.manager.GlobalDisableManager;
import ocr.manager.GlobalHotkeyManager;
import ocr.manager.LogFilesManager;
import ocr.manager.OcrPropInfoHolder;
import ocr.manager.ProcessExecutionManager;
import ocr.manager.PropertiesInfoHolder;
import ocr.manager.UndoRedoManager;
import ocr.manager.zones.ZonesManager;
import ocr.tag.DirectoryChooser;
import ocr.tag.GetDateTime;
import ocr.tif.ImageAnalyzer;
import ocr.tif.ImageDisplay;
import ocr.tif.ImageDisplay.ZoneVector;
import ocr.tif.ImageReaderDrawer;
import ocr.tif.color.ColorImageToolbar;
import ocr.util.AttributeConfigUtil;
import ocr.util.BidiString;
import ocr.util.CapabilitiesControl;
import ocr.util.DesktopUtil;
import ocr.util.ImageNamesSorter;
import ocr.util.MARIAScript;
import ocr.util.MARIAchiPEScript;
import ocr.util.NetworkListener;
import ocr.util.OutputStream;
import ocr.util.ScriptConstants;
import ocr.util.ScriptLoader;
import ocr.util.UniqueZoneId;
import ocr.util.comments.CommentsDialog;
import ocr.util.comments.CommentsParser;

public class OCRInterface extends JFrame implements ActionListener, KeyListener, ItemListener, AdjustmentListener, WindowFocusListener
{
  private static String filePath;
  private static final String OPTIONSPANEL = "OPTIONSPANEL";
  public static Vector<ParChldLine> vec = null;
  

  public static javax.swing.filechooser.FileFilter XMLFileFilter = new javax.swing.filechooser.FileFilter() {
    public boolean accept(File f) {
      if ((!f.isDirectory()) && (!f.getAbsolutePath().endsWith("xml")))
      {


        if (!f.getAbsolutePath().endsWith("XML")) return false;
      }
      return 
      


        true;
    }
    

    public String getDescription()
    {
      return "XML Files";
    }
  };
  


  public static javax.swing.filechooser.FileFilter BMPFileFilter = new javax.swing.filechooser.FileFilter()
  {
    public boolean accept(File f)
    {
      if ((!f.isDirectory()) && (!f.getAbsolutePath().endsWith("bmp")))
      {


        if (!f.getAbsolutePath().endsWith("BMP")) return false;
      }
      return 
      


        true;
    }
    



    public String getDescription() { return "BMP Files"; } };
  public static final int OCR_MODE = 0;
  public static final boolean DEBUG = false;
  public static final int RES_DSET = 0;
  public static final int TRN_DSET = 1;
  public static final String RANDOM_TRAINING_DIR = "RandomTraining";
  public static final int SELECT_MODE = 0;
  public static final int T_SELECT_MODE = 1;
  public static final int CREATE_MODE = 2;
  public static final int EDIT_MODE = 3;
  public static final int MOVE_MODE = 4;
  public static final int DELETE_MODE = 5;
  public static final int SPLIT_MODE = 7;
  public static final int DRAG_MODE = 8;
  public static final int TEXTLINEGT_MODE = 9;
  public static final int TEXTLINEGT_LINEMOVE = 10;
  public static final int PC_MODE = 11;
  public static final int DRAWPCLINE_MODE = 12;
  public static final int CREATEPAR_MODE = 13;
  public static final int ERASEPCLINE_MODE = 14;
  public static final int ORIENTED_MODE = 15;
  public static final int POLYGON_MODE = 16;
  public static final int READINGORDER_MODE = 17;
  public static final int CONNECTED_COMPONENT_MODE = 18;
  public static final int GROUP_MODE = 19;
  public static final int MEASURE_MODE = 20;
  public static final int NoState = -1;
  public static final int StartState = 0;
  public static final int LineState = 1;
  public static final int TextState = 2;
  public static final int WordState = 3;
  public static final int SplitWordState = 4;
  public static final int PawState = 5;
  public static final int RefineState = 6;
  public static final int VerifyState = 7;
  public static final int FinalState = 8;
  public static final int LockState = 9; public static final int QCState = 10; public static final int AllState = -2; public int currState = -1;
  
  public static final String[] extList = { ".line.", ".merge.", ".transcribe.", ".word.", ".splitword.", 
    ".refine.", ".final." };
  public static final int LINE = 0;
  public static final int MERGE = 1;
  public static final int WORD = 2;
  public static final int PAW = 3;
  public static final int FINAL = 4;
  public static final int COPYLINE = 5;
  public static final int COPYMERGE = 6;
  public static final int SHRINK = 7; public static final int REFINE = 8; public static final int REVERT = 9; public static final int SEGWORD = 10; public static final int SPLWORD = 11; public static final int PAWUPDATE = 12; public static final int COPYPAW = 13; public static final int COPYREFINE = 14; public static final int COPYWORD = 16; public static final int VERIFY = 17; public static final int SPLWORDUPDATE = 18; public static final int COPYSPLWORD = 19; public static int curr_ext; public String imageExt = null;
  
  public String baseFileName = "";
  
  public static OCRInterface this_interface;
  
  public static final String TITLE = "DocLib - GroundTruthing Editor and Document Interface Professional Edition (DL-GEDIPro)";
  
  public static final String TITLEPAGE = "images/mainscreen.jpg";
  
  public static final String FILE = "File";
  
  public static final String SAVEDOC = "Save XML";
  
  public static final String SAVEDOCAS = "Save XML As";
  
  public static final String SAVECONFIG = "Save GEDI Config";
  
  public static final String SAVECONFIGAS = "Save GEDI Config As";
  
  public static final String MERGECONFIG = "Merge GEDI Config";
  
  public static final String LOADCONFIG = "Load GEDI Config";
  
  public static final String SAVEPROFILEAS = "Save Profile As";
  
  public static final String SAVEALL = "Save All";
  
  public static final String FILECLOSE = "Close";
  
  public static final String FILEEXIT = "Exit";
  
  public static final String IMAGEOPEN = "Load File/Directory";
  
  public static final String XMLOPEN = "Load Xml File";
  
  public static final String VIEWLOG = "View Log File";
  
  public static final String FILEDELETE = "Delete...";
  
  public static final String SAVEROTATEDIMAGE = "Save Rotated Image";
  
  public static final String REFRESHDOC = "Refresh XML";
  
  public static final String REFRESHLIST = "Refresh Document List";
  
  public static final String CLOSEDOC = "Close Document";
  public static final String SAVEBOTH = "Save Document w/ Workspace";
  public static final String BUILDWORK = "Build Workspace";
  public static final String AUTOSAVE = "Auto Save";
  public static final String ALWAYSDESTROY = "Always Destroy Zone Hierarchy";
  public static final String NEVERDESTROY = "Never Destroy Zone Hierarchy";
  public static final String FULLSCREEN = "Full Screen Capture";
  public static final String IMGSCREEN = "Document Capture";
  public static final String SCREEN = "Screen Capture";
  public static final String CUSTOMSCRIPT = "Custom Script...";
  public static final String LOGOSCRIPT = "Arabic Recognition Script";
  public static final String SIGSCRIPT = "Signature Detection Script";
  public static final String STAMPSCRIPT = "Stamp Detection Script";
  public static final String PAGESEGMENTSCRIPT = "Page Segmentation Script";
  public static final String DOCIDSCRIPT = "DocID";
  public static final String SCRIPTIDSCRIPT = "ScriptID";
  public static final String FIND = "Find";
  public static final String SELECTALLZONES = "Select all zones";
  public static final String INFOWINDOW = "InfoWindow";
  private static final String ABOUT = "About DL-GEDIPro";
  private static final String HELP = "Help";
  private static final String GEDI_HELP = "GEDI Help";
  public static final String MODIFYCREATE = "Create";
  public static final String MERGE_ZONE = "Merge zones";
  public static final String MODIFYSPLIT = "Split";
  public static final String MODIFYPC = "Par/Chld Line";
  public static final String READING_ORDER = "Create/Edit Reading Order";
  public static final String GENERATE_RLE = "Generate or remove RLEIMAGE";
  public static final String CREATE_XML = "createXML";
  public static final String SHRINK_ZONE = "Shrink zone";
  public static final String SPLIT_OFFSETS = "Split zone at offsets";
  public static final String REMOVE_OVERLAP = "Remove overlap";
  private static final String def_dict_path = "config/default/DefaultDictionary/";
  private static final String def_dict_name = "DefaultDictionary";
  private static final String def_image_name = "DefaultImage";
  private static final String def_xml_name = "DefaultXml";
  private static final String ConfigFile = "config/DLGEDI_CONFG.XML";
  private static final String ConfigFileNull = "There is no Config File (will be created in Xml Directory)";
  private static final String GediAlignPath = "\\lib\\GediAlign\\GediAlign.exe";
  private Map<String, Map<String, TypeAttributeEntry>> atts = null;
  
  private Set<String> zoneTypes = null;
  



  private Hashtable<Integer, Boolean> save_items_hash = new Hashtable();
  



  private static Vector<String> dir_vec = null;
  



  public OcrPropInfoHolder ocrProperties = new OcrPropInfoHolder(0);
  




  public PropertiesInfoHolder[] workmodeProps = new PropertiesInfoHolder[1];
  



  public PropertiesInfoHolder props;
  



  public ProcessExecutionManager processExecutionManager = new ProcessExecutionManager();
  





  public ExternalProcessManager externalProcessManager;
  




  public static LoadDataFile currDoc;
  




  public Window childWindow = null;
  




  public static GlobalDisableManager disableManager = new GlobalDisableManager();
  







  public FindPanel bottomPanel;
  






  private Cursor saveCursor = Cursor.getDefaultCursor();
  


  public BrowserToolBar toolbar;
  

  private JToolBar GTToolbar;
  

  private JToolBar PolyTranscribeToolbar;
  

  private JToolBar TranslateWorkflowToolbar;
  

  private JToolBar thresholdingToolbar;
  

  public boolean fclose_cancel_selected = false;
  






  private JMenuBar myMenuBar = new JMenuBar();
  






  private JMenu subMnuRot;
  






  private JMenuItem mniFileSaveRotatedImage;
  






  private JMenuItem mniShapeSplit;
  






  private JMenuItem mniShapePCLine;
  






  public JSplitPane mainPane = null;
  
  JToolBar right = new JToolBar("Image Panel");
  
  JPanel right_panel = new JPanel(new CardLayout());
  






  public static ImageReaderDrawer currentHWObj = null;
  









  public static int currOppmode = 2;
  



  public static int currWorkmode = 0;
  










  public WorkmodeTable ocrTable = null;
  
  public WorkmodeTable[] workmodeTables = new WorkmodeTable[1];
  










  public LeftPanel tbdPane = null;
  








  public OcrMenuListener menuListener = new OcrMenuListener(this);
  




  private static AttributeConfigUtil acu;
  




  public SaveFilesDialog saveFilesDialog = new SaveFilesDialog();
  














  public boolean a_file_opened = false;
  
  public static boolean dialog_open = false;
  
  public static JDialog jd = null;
  
  public static JFileChooser jc = null;
  
  public static JOptionPane jop = null;
  




  public boolean dictionary_load = false;
  
  public Vector<String> pathVec = new Vector();
  





  boolean autoCreateXml = false;
  



  public static BufferedWriter log = null;
  




  public static String log_path = System.getProperty("user.home") + 
    File.separator + 
    ".gedi" + 
    File.separator + 
    "logs" + 
    File.separator;
  






  private static String current_dir;
  






  private static String current_image_dir;
  






  private static String current_xml_dir;
  





  private static String dictionary;
  





  private static String image_dir = "";
  




  private static String xml_dir = "";
  
  public static String config_file = null;
  








  private static boolean exit_ocr = false;
  



  private static LogFilesManager logFilesManager;
  




  public int currDatasetNum()
  {
    if (currWorkmode == 0) {
      return ocrTable.curCol - 1;
    }
    return -1;
  }
  



  public String currDatasetName()
  {
    if (currWorkmode == 0) {
      return ocrProperties.getDataSetName(ocrTable.curCol - 1);
    }
    return null;
  }
  

  public JThumbListModel model = new JThumbListModel(this);
  
  public JThumbList list = null;
  
  public JScrollPane pane = null;
  
  public JToolBar thumb = new JToolBar("Thumb Panel");
  


  public ArrayList<File> rawXmlOnlyFileList = null;
  
  private ArrayList<File> rawImageOnlyFileList = null;
  
  public static final String IMAGEPANEL = "Image Panel";
  
  public static final String THUMBPANEL = "Thumb Panel";
  
  public JPanel rPanel = new JPanel(new BorderLayout());
  
  public JPanel tPanel = new JPanel(new BorderLayout());
  
  public boolean thumbSelected = false;
  
  public boolean controlIsDown = false;
  

  public static final int Char = 2;
  
  public static final int Word = 3;
  
  public static final int Line = 4;
  
  public static final TreeMap<String, Integer> lineGTModeMap = buildlineGTModeMap();
  



  private int currLineGTMode = 2;
  





  private TypeLister lister = new TypeLister();
  
  private JCheckBoxMenuItem jcb;
  
  public Map<String, Map<String, TypeAttributeEntry>> chip;
  
  private boolean displayCommentsInSeparateWindow;
  
  private boolean showContentWindowOnDlTextLineCreate;
  
  private boolean showTextOnAllZones;
  
  private boolean showReadingOrder;
  
  private boolean useStartPoint;
  
  private boolean allowXmlImage;
  
  private boolean allowWordSplit;
  private boolean lockContentEditing;
  private boolean enableDigitTokenPopup;
  private boolean enableReverseButton;
  private boolean normalizeArabicForm_B;
  private boolean saveNormalizedArabicContent;
  private boolean recomputeRLEonEdit;
  private boolean convertRectanglesToPolygonsOnShrink;
  private boolean shrinkAfterOverlapRemoval;
  private boolean enableNonConvexPolygonShrinking;
  private boolean expandThenShrink;
  private String minMaxExpand;
  private int polygonPadding;
  private int overlapRemovalPadding;
  private int intersectionThreshold;
  private int edgesGranularity;
  private boolean mergeTextToFirstZone;
  private String alignmentDefaultSegmenation;
  private boolean allowZoneRecenter;
  private boolean useETextWindow;
  private boolean enableAlignment;
  private boolean enablePolygonTranscription;
  private boolean enableTranslateWorkflow;
  private String polygonTranscriptionType;
  private boolean enforceTranscriptionReview;
  private boolean transcribeUsingBoxes;
  private boolean showZoneTypes;
  private boolean enablePCButtons;
  private String dltextlineDefaultSegmentation;
  private boolean autoCreateConfigFile;
  private boolean autoOverwriteOverlayFile;
  private boolean enableProfileLoad;
  private boolean saveConfigCopyInDataDirectory;
  private boolean loadPreviousDirOnStartup;
  private String GediAlignCmdPath;
  private boolean documentCaptureToDefaultFile;
  private int listenerPort;
  private String rootPath;
  private boolean networkListenerOn;
  private boolean applyExpandShrink;
  private int connectedComponentFilterSize;
  private String commentText;
  private boolean useDirNameForTextFile;
  private int maxRefineExpand;
  private int cutTolerance;
  private String pseudoColorAttribute;
  private String displayField;
  private boolean collapseRows;
  private boolean allowIdenticalZones;
  private boolean warnIdenticalZonesWereCreated;
  private float lineThickness;
  private boolean indicDigitsON;
  private boolean thresholdingToolbarVisible;
  private String lowerUpperThresholdValues;
  private String ctrl_C_Copies;
  private String ctrl_C_separator;
  private boolean shrinkAfterSplit;
  private boolean preserveReadingOrderOnSplit;
  private boolean preserveAttributesOnSplit;
  private boolean preserveAttributesOnMerge;
  private boolean insertSpaceOnMerge;
  private boolean mergeZonesToBox;
  private String randomConstantForPseudoColor;
  public GlobalHotkeyManager hotkeyManager;
  public boolean[] windowFocus;
  public static String[] imgTypes = { ".tiff", ".tif", ".jpg", ".jpeg", ".gif", ".bmp", ".TIFF", ".TIF", ".JPG", ".JPEG", ".GIF", ".BMP" };
  
  public String tifType = "";
  
  public static LinkedHashMap<String, String> imgTypeAssoc;
  
  public boolean multiPageTiff = false;
  public int currPageID = 0;
  

  public boolean splitWordDone = false;
  
  public boolean copyLinesEnabled = false;
  
  public boolean utf8Selected;
  
  public boolean utf16Selected;
  
  public ArrayList<String> fullPathElts;
  
  public ArrayList<String> elts;
  public ArrayList<String> lockFileList;
  public float TRANSPARENCY = 0.0F;
  
  public boolean zoneBeingResized = false;
  
  public int lockedIndex = -1;
  public String lockedFile = "";
  
  public static final int dividerScale = 2;
  public int numFiles = 0;
  public int numTotalFiles = 0;
  
  public static final int NUM_DONE = 25;
  
  public boolean pgPressed = false;
  public boolean wordSeg = false;
  
  private static String screenCaptureType = ".bmp";
  

  private UniqueZoneId uniqueZoneId;
  

  private NetworkListener networkListener;
  
  private JMenu menuScripts;
  
  private ConnectedComponent connectedComponentHandler;
  
  private String errorMessage = null;
  
  MultiPagePanel multiPagePanel = null;
  
  public JCheckBoxMenuItem GTToolbarMenuItem;
  
  public JCheckBoxMenuItem PolyTranscribeToolbarMenuItem;
  
  public JCheckBoxMenuItem TranslateWorkflowToolbarMenuItem;
  
  public GTToolBar GTPanel;
  
  public PolyTranscribeToolBar PolyTranscribePanel;
  
  private TranslateToolBar TranslateWorkflowPanel;
  
  public JSplitPane rightSplitPanel;
  
  public double rightSpliPanelDividerLocation = 0.85D;
  
  private DirLoader dirLoader = null;
  
  private ScriptLoader scriptLoader = null;
  
  private static int logFilesLimit = 100;
  
  private boolean allowToSwitchImage = true;
  
  private OptionParser commandLineOptions = null;
  
  private int currentRotateDegrees = 0;
  
  private boolean hideBoxes = false;
  
  private CommentsDialog commentsWindow = null;
  
  private JLabel mousePosistionLabel = null;
  
  private boolean blankFoundOnShrink;
  

  public boolean isAllowToSwitchImage()
  {
    return allowToSwitchImage;
  }
  
  public void setAllowToSwitchImage(boolean allowToSwitchImage) {
    this.allowToSwitchImage = allowToSwitchImage;
  }
  







  public OCRInterface(OptionParser commandLineOptionsIn)
  {
    super("DocLib - GroundTruthing Editor and Document Interface Professional Edition (DL-GEDIPro)");
    commandLineOptions = commandLineOptionsIn;
    multiPagePanel = new MultiPagePanel();
    JFrame splash = new JFrame("LOADING");
    splash.setIconImage(Login.localOrInJar("images/GEDI_icon.jpg", getClass().getClassLoader()).getImage());
    JPanel splashPanel = new JPanel();
    ImageIcon splashImg = Login.localOrInJar("images/loadscreen.jpg", getClass().getClassLoader());
    
    splashPanel.add(new JLabel(splashImg));
    splash.add(splashPanel);
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension splashImgSize = new Dimension(splashImg.getIconWidth(), splashImg.getIconHeight());
    splash.setLocation(width / 2 - width / 2, 
      height / 2 - height / 2);
    splash.setSize(splashImgSize);
    splash.setResizable(false);
    splash.setUndecorated(true);
    splash.setVisible(true);
    splash.toFront();
    splash.setAlwaysOnTop(true);
    windowFocus = new boolean[2];
    
    this_interface = this;
    super.setIconImage(Login.localOrInJar("images/GEDI_icon.jpg", getClass().getClassLoader()).getImage());
    
    addWindowFocusListener(this);
    imgTypeAssoc = new LinkedHashMap();
    

    init();
    
    fullPathElts = new ArrayList();
    lockFileList = new ArrayList();
    elts = new ArrayList();
    acu = new AttributeConfigUtil(
      saveFilesDialog.isAutoSave(), 
      getDocumentCaptureToDefaultFile(), 
      getAutoCreateConfigFile(), 
      getAutoOverwriteOverlayFile(), 
      getEnableProfileLoad(), 
      getSaveConfigCopyInDataDirectory(), 
      getLoadPreviousDirOnStartup(), 
      new String[] { "line", "word", 
      "character" }, 
      getDlTextLineDefaultSegmentation(), 
      getDisplayCommentsInSeparateWindow(), 
      getShowContentWindowOnDlTextLineCreate(), 
      getShowTextOnAllZones(), 
      getShowReadingOrder(), 
      getUseStartPoint(), 
      getUseDirNameForTextFile(), 
      getTransparency(), 
      getPseudoColorAttribute(), 
      getDisplayField(), 
      getGediAlignCmdPath(), 
      getUseETextWindow(), 
      getEnableAlignment(), 
      getEnablePolygonTranscription(), 
      getEnableTranslateWorkflow(), 
      getEnforceTranscriptionReview(), 
      getTranscribeUsingBoxes(), 
      getShowZoneTypes(), 
      getEnablePCButtons(), 
      getAllowXmlImage(), 
      getCollapseRows(), 
      getAllowWordSplit(), 
      getLockContentEditing(), 
      getEnableDigitTokenPopup(), 
      getEnableReverseButton(), 
      getNormalizeArabicForm_B(), 
      getSaveNormalizedArabicContent(), 
      getRecomputeRLEonEdit(), 
      getPreserveAttributesOnMerge(), 
      getInsertSpaceOnMerge(), 
      getMergeZonesToBox(), 
      getExpandThenShrink(), 
      getMinMaxExpand(), 
      getPolygonPadding(), 
      getOverlapRemovalPadding(), 
      getIntersectionThreshold(), 
      getConvertRectanglesToPolygonsOnShrink(), 
      getShrinkAfterOverlapRemoval(), 
      getEnableNonConvexPolygonShrinking(), 
      getEdgesGranularity(), 
      getMergeTextToFirstZone(), 
      getAlignmentDefaultSegmenation(), 
      getAllowZoneRecenter(), 
      getNetworkListenerOn(), 
      getListenerPort(), 
      getRootPath(), 
      getApplyExpandShrink(), 
      getConnectedComponentFilterSize(), 
      getMaxRefineExpand(), 
      getCutTolerance(), 
      getCommentText(), 
      getAllowIdenticalZones(), 
      getWarnIdenticalZonesWereCreated(), 
      getLineThickness(), 
      getCtrl_C_separator(), 
      getShrinkAfterSplit(), 
      getPreserveReadingOrderOnSplit(), 
      getPreserveAttributesOnSplit(), 
      getRandomConstantForPseudoColor(), 
      this);
    


    setDefaultCloseOperation(0);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent we) {
        OCRInterface.exit_ocr = true;
        
        OCRInterface.this.closeApplication();
      }
      
      public void windowGainedFocus(WindowEvent e) {
        if (OCRInterface.acu.isVisible()) {
          OCRInterface.acu.toFront();

        }
        

      }
      

    });
    addKeyListener(this);
    
    setJMenuBar(buildMenuBar());
    buildComponents();
    System.out.println("CurrentImageDir: " + getCurrentImageDir());
    System.out.println("ImageDirName: " + getImageDirName());
    System.out.println("CurrentXmlDir: " + getCurrentXmlDir());
    System.out.println("XmlDirName: " + getXmlDirName());
    
    setLocation(25, 25);
    setOppmode(0);
    
    setLocation(0, 0);
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    
    setSize((int)dim.getWidth(), (int)dim.getHeight());
    




    requestFocusInWindow();
    



    setHotKeys();
    mainPane.setDividerLocation(0.3D);
    disableManager.updateOppModeButts();
    
    setVisible(true);
    

    toolbar.setScaleByIndex(10);
    setChronologicalFocus();
    



    setShortCutKeys();
    
    acu.setVisible(false);
    super.setExtendedState(6);
    if (getNetworkListenerOn()) {
      runNetworkListener();
    }
    else
    {
      networkListener = new NetworkListener();
    }
    try {
      Thread.sleep(200L);
    }
    catch (Exception localException) {}
    
    splash.setAlwaysOnTop(false);
    splash.setVisible(false);
    splash.dispose();
    multiPagePanel.setVisible(true);
    multiPagePanel.setEnabled(false);
    updateETextWindowContent();
    if ((commandLineOptions != null) && 
      (commandLineOptions.isNetworkListenerRequired()) && 
      (!commandLineOptions.isNetworkListenerRunning())) {
      if (!networkListener.isAlive()) {
        setNetworkListenerOn(true);
        networkListener.start();
      }
      commandLineOptions.processOptionsViaNetworkListener();
    }
    else if ((commandLineOptions != null) && 
      (!commandLineOptions.isNetworkListenerRequired()) && 
      (commandLineOptions.getZoneID() != null)) {
      this_interfaceocrTable.processSelectionEvent(0, 1, true);
      UniqueZoneId uniqueZoneId = this_interface.getUniqueZoneIdObj();
      uniqueZoneId.searchZone(true, commandLineOptions.getZoneID());
    }
    
    GlobalHotkeyManager.getInstance().setEnabled(true);
    new Thread(new Runnable() {
      private boolean lastSetState = true;
      
      public void run() {
        for (;;) { if (ImageDisplay.showCaret == lastSetState) {
            ImageDisplay.showCaret = !ImageDisplay.showCaret;
            lastSetState = ImageDisplay.showCaret;
            OCRInterface.this_interface.getCanvas().paintCanvas();
          } else {
            lastSetState = ImageDisplay.showCaret;
          }
          try
          {
            Thread.sleep(500L);

          }
          catch (Exception localException) {}
        }
        
      }
    }).start();
    new FileDrop(System.out, 
      currentHWObj, 
      new ocr.gui.fileDrop.FileDropListener());
  }
  



  private void init()
  {
    LoadGEDIProps dirProps = LoadGEDIProps.getInstance();
    
    workmodeProps[0] = ocrProperties;
    props = ocrProperties;
    
    loadProperties(dirProps);
    
    new CapabilitiesControl();
  }
  
  public void setShowZoneTypes(boolean b) {
    showZoneTypes = b;
  }
  
  public void setEnablePCButtons(boolean b) { enablePCButtons = b; }
  
  public void setShowTextOnAllZones(boolean b) {
    showTextOnAllZones = b;
  }
  
  public void setShowReadingOrder(boolean b) { showReadingOrder = b; }
  
  public void setUseStartPoint(boolean b) {
    useStartPoint = b;
  }
  
  public void setAllowXmlImage(boolean b) {
    allowXmlImage = b;
  }
  
  public void setAllowWordSplit(boolean b) { allowWordSplit = b; }
  
  public void setMergeTextToFirstZone(boolean b)
  {
    mergeTextToFirstZone = b;
  }
  
  public void setAlignmentDefaultSegmenation(String segm) { alignmentDefaultSegmenation = segm; }
  
  public void setAllowZoneRecenter(boolean b) {
    allowZoneRecenter = b;
  }
  
  public void setUseETextWindow(boolean b) { useETextWindow = b; }
  
  public void setLockContentEditing(boolean b) {
    lockContentEditing = b;
  }
  
  public void setEnableDigitTokenPopup(boolean b) { enableDigitTokenPopup = b; }
  
  public void setEnableReverseButton(boolean b) {
    enableReverseButton = b;
  }
  
  public void setNormalizeArabicForm_B(boolean b) { normalizeArabicForm_B = b; }
  
  public void setSaveNormalizedArabicContent(boolean b) {
    saveNormalizedArabicContent = b;
  }
  
  public void setRecomputeRLEonEdit(boolean b) { recomputeRLEonEdit = b; }
  
  public void setPreserveAttributesOnMerge(boolean b) {
    preserveAttributesOnMerge = b;
  }
  
  public void setInsertSpaceOnMerge(boolean b) { insertSpaceOnMerge = b; }
  
  public void setMergeZonesToBox(boolean b) {
    mergeZonesToBox = b;
  }
  
  public void setExpandThenShrink(boolean b) { expandThenShrink = b; }
  
  public void setMinMaxExpand(String str) {
    minMaxExpand = str.trim();
  }
  
  public void setConvertRectanglesToPolygonsOnShrink(boolean b) { convertRectanglesToPolygonsOnShrink = b; }
  
  public void setShrinkAfterOverlapRemoval(boolean b) {
    shrinkAfterOverlapRemoval = b;
  }
  
  public void setEnableNonConvexPolygonShrinking(boolean b) { enableNonConvexPolygonShrinking = b; }
  
  public void setPolygonPadding(int number) {
    polygonPadding = number;
  }
  
  public void setOverlapRemovalPadding(int number) { overlapRemovalPadding = number; }
  
  public void setIntersectionThreshold(int number) {
    intersectionThreshold = number;
  }
  
  public void setEdgesGranularity(int number) { edgesGranularity = number; }
  
  public void setEnableAlignment(boolean b) {
    enableAlignment = b;
  }
  









  public void setEnablePolygonTranscription(boolean b)
  {
    enablePolygonTranscription = b;
  }
  
  public void setEnableTranslateWorkflow(boolean b) { enableTranslateWorkflow = b; }
  
  public void setPolygonTranscriptionType(String type) {
    polygonTranscriptionType = type;
  }
  
  public void setEnforceTranscriptionReview(boolean b) { enforceTranscriptionReview = b; }
  
  public void setTranscribeUsingBoxes(boolean b) {
    transcribeUsingBoxes = b;
  }
  
  public void setUseDirNameForTextFile(boolean b) {
    useDirNameForTextFile = b;
  }
  
  public void setDisplayCommentsInSeparateWindow(boolean b) {
    displayCommentsInSeparateWindow = b;
  }
  

  public void setShowContentWindowOnDlTextLineCreate(boolean b) { showContentWindowOnDlTextLineCreate = b; }
  
  public void setTransparency(float f) {
    if (f < 0.0F)
      f = 0.0F;
    if (f > 100.0F)
      f = 100.0F;
    TRANSPARENCY = (1.0F - f / 100.0F);
  }
  
  public void setPseudoColorAttribute(String att) {
    pseudoColorAttribute = att;
  }
  
  public void setDisplayField(String att) {
    displayField = att;
  }
  
  public void setDlTextLineDefaultSegmentation(String segmentation) {
    dltextlineDefaultSegmentation = segmentation;
  }
  
  public void setGediAlignCmdPath(String newPath) {
    GediAlignCmdPath = newPath;
  }
  
  public void setAutoCreateConfigFile(boolean b) {
    autoCreateConfigFile = b;
  }
  
  public void setAutoOverwriteOverlayFile(boolean b) { autoOverwriteOverlayFile = b; }
  
  public void setEnableProfileLoad(boolean b) {
    enableProfileLoad = b;
  }
  
  public void setSaveConfigCopyInDataDirectory(boolean b) { saveConfigCopyInDataDirectory = b; }
  
  public void setLoadPreviousDirOnStartup(boolean b) {
    loadPreviousDirOnStartup = b;
  }
  
  public void setDocumentCaptureToDefaultFile(boolean b) { documentCaptureToDefaultFile = b; }
  
  public void setListenerPort(int port)
  {
    listenerPort = port;
  }
  
  public void setRootPath(String path) {
    rootPath = path;
  }
  
  public void setNetworkListenerOn(boolean b) {
    networkListenerOn = b;
  }
  
  public void setApplyExpandShrink(boolean b) {
    applyExpandShrink = b;
  }
  
  public void setConnectedComponentFilterSize(int filterSize) {
    connectedComponentFilterSize = filterSize;
  }
  
  public void setCommentText(String commentText) {
    if (commentText == null) {
      this.commentText = "";
    } else
      this.commentText = commentText.trim();
  }
  
  public void setMaxRefineExpand(int max) {
    maxRefineExpand = max;
  }
  
  public void setCutTolerance(int max) {
    cutTolerance = max;
  }
  
  public void setCollapseRows(boolean b) {
    collapseRows = b;
  }
  
  public void setAllowIdenticalZones(boolean b) {
    allowIdenticalZones = b;
  }
  
  public void setWarnIdenticalZonesWereCreated(boolean b) {
    warnIdenticalZonesWereCreated = b;
  }
  
  public void setLineThickness(float weight) {
    lineThickness = weight;
  }
  
  public void setIndicDigitsON(boolean b) {
    indicDigitsON = b;
  }
  
  public void setThresholdingToolbarVisible(boolean b) {
    thresholdingToolbarVisible = b;
  }
  
  public void setLowerUpperThresholdValues(String str) {
    lowerUpperThresholdValues = str;
  }
  
  public void setCtrl_C_Copies(String str) {
    ctrl_C_Copies = str;
  }
  
  public void setCtrl_C_separator(String str) { ctrl_C_separator = str; }
  
  public void setShrinkAfterSplit(boolean b) {
    shrinkAfterSplit = b;
  }
  
  public void setPreserveReadingOrderOnSplit(boolean b) { preserveReadingOrderOnSplit = b; }
  
  public void setPreserveAttributesOnSplit(boolean b) {
    preserveAttributesOnSplit = b;
  }
  
  public void setRandomConstantForPseudoColor(String anyStr) { randomConstantForPseudoColor = anyStr; }
  







  private static void selectErrorStream()
  {
    if (!new File(log_path).exists()) {
      new File(log_path).mkdirs();
    }
    String filename = log_path + "log-" + Calendar.getInstance().getTimeInMillis() + ".txt";
    
    try
    {
      FileOutputStream logFile = new FileOutputStream(filename, true);
      

      PrintStream out = new PrintStream(logFile);
      
      PrintStream tee = new OutputStream(System.out, out);
      
      tee.append(GetDateTime.getInstance());
      tee.append("\n\n");
      tee.append("Heap size: " + Runtime.getRuntime().maxMemory());
      tee.append("\n\n");
      tee.append("GEDI Version: " + XmlConstant.VALUE_GEDI_VERSION + 
        " (" + XmlConstant.VALUE_GEDI_DATE + ")");
      tee.append("\n\n");
      
      System.setOut(tee);
      

      PrintStream err = new PrintStream(logFile);
      tee = new OutputStream(System.err, err);
      
      System.setErr(tee);
    } catch (FileNotFoundException e) {
      System.out.println("Could not write to file: " + filename);
    }
    
    cleanUpLogs();
    



    logFilesManager = new LogFilesManager(new File(filename));
  }
  






  private static void cleanUpLogs()
  {
    String fileExtension_txt = ".txt";
    File logDir = new File(log_path);
    
    if (!logDir.exists()) {
      return;
    }
    System.out.println("log files path: " + log_path);
    
    File[] allLogFiles = (File[])null;
    
    java.io.FileFilter fileFilter = new java.io.FileFilter() {
      public boolean accept(File file) {
        String fileName = file.getName().toLowerCase();
        


        return (file.isFile()) && (fileName.endsWith(".txt"));
      }
    };
    
    if (logDir.isDirectory()) {
      allLogFiles = logDir.listFiles(fileFilter);
      Arrays.sort(allLogFiles);
    }
    
    System.out.println("log files limit: " + logFilesLimit);
    
    if (allLogFiles != null) {
      ArrayList<File> logFilesArrayList = new ArrayList();
      for (int i = 0; i < allLogFiles.length; i++) {
        logFilesArrayList.add(allLogFiles[i]);
      }
      ArrayList<File> temp_logFilesArrayList = (ArrayList)logFilesArrayList.clone();
      
      if (allLogFiles.length > logFilesLimit)
      {
        while (temp_logFilesArrayList.size() > logFilesLimit) {
          ((File)logFilesArrayList.get(0)).delete();
          logFilesArrayList.remove(0);
          temp_logFilesArrayList = (ArrayList)logFilesArrayList.clone();
        }
      }
      
      System.out.println("# old log files deleted: " + (
        allLogFiles.length - logFilesArrayList.size()) + "\n");
    }
  }
  



  public void windowGainedFocus(WindowEvent e)
  {
    if (childWindow != null) {
      childWindow.toFront();
    }
  }
  










  public void windowLostFocus(WindowEvent e) {}
  










  private JMenuBar buildMenuBar()
  {
    JMenu menu = null;
    


    menu = new JMenu("File");
    
    menu.setMnemonic('F');
    
    JMenuItem item1 = new JMenuItem("Load File/Directory");
    item1.addActionListener(menuListener);
    menu.add(item1);
    item1.setMnemonic('O');
    item1.setAccelerator(KeyStroke.getKeyStroke(
      79, 8));
    
    item1 = new JMenuItem("Load GEDI Config");
    item1.addActionListener(menuListener);
    menu.add(item1);
    item1.setMnemonic('G');
    item1.setAccelerator(KeyStroke.getKeyStroke(
      71, 8));
    








    item1 = new JMenuItem("Merge GEDI Config");
    item1.addActionListener(menuListener);
    item1.setEnabled(true);
    menu.add(item1);
    item1.setMnemonic('M');
    item1.setAccelerator(KeyStroke.getKeyStroke(
      77, 8));
    
    item1 = new JMenuItem("Build Workspace");
    item1.addActionListener(menuListener);
    item1.setEnabled(true);
    
    item1.setMnemonic('B');
    item1.setAccelerator(KeyStroke.getKeyStroke(
      66, 8));
    
    menu.addSeparator();
    item1 = new JMenuItem("Create XML");
    item1.setMnemonic('A');
    item1.addActionListener(menuListener);
    item1.setActionCommand("createXML");
    menu.add(item1);
    
    item1 = new JMenuItem("Refresh XML");
    item1.addActionListener(menuListener);
    item1.setEnabled(true);
    menu.add(item1);
    item1.setAccelerator(KeyStroke.getKeyStroke(116, 0));
    
    item1 = new JMenuItem("Close Document");
    item1.addActionListener(menuListener);
    menu.add(item1);
    item1.setMnemonic('C');
    item1.setAccelerator(KeyStroke.getKeyStroke(
      67, 8));
    
    item1 = new JMenuItem("Refresh Document List");
    item1.addActionListener(menuListener);
    item1.setEnabled(true);
    menu.add(item1);
    item1.setMnemonic('L');
    item1.setAccelerator(KeyStroke.getKeyStroke(
      76, 8));
    

    menu.addSeparator();
    
    item1 = new JMenuItem("Save XML");
    item1.addActionListener(menuListener);
    item1.setEnabled(true);
    menu.add(item1);
    item1.setMnemonic('S');
    item1.setAccelerator(KeyStroke.getKeyStroke(
      83, 8));
    
    item1 = new JMenuItem("Save XML As");
    item1.addActionListener(menuListener);
    item1.setEnabled(true);
    menu.add(item1);
    













    item1 = new JMenuItem("Save GEDI Config As");
    item1.addActionListener(menuListener);
    item1.setEnabled(true);
    
    item1 = new JMenuItem("Save Profile As");
    item1.addActionListener(menuListener);
    item1.setEnabled(true);
    menu.add(item1);
    
    item1 = new JMenuItem("Save All");
    item1.addActionListener(menuListener);
    item1.setEnabled(true);
    item1.setToolTipText("Save Document, GEDI Config, and Profile");
    menu.add(item1);
    item1.setMnemonic('A');
    item1.setAccelerator(KeyStroke.getKeyStroke(
      65, 8));
    menu.add(item1);
    











    mniFileSaveRotatedImage = new JMenuItem("Save Rotated Image");
    mniFileSaveRotatedImage.addActionListener(menuListener);
    mniFileSaveRotatedImage.setEnabled(false);
    menu.add(mniFileSaveRotatedImage);
    
    menu.addSeparator();
    
    item1 = new JMenuItem("Exit");
    item1.addActionListener(menuListener);
    menu.add(item1);
    myMenuBar.add(menu);
    item1.setMnemonic('x');
    item1.setAccelerator(KeyStroke.getKeyStroke(
      88, 8));
    

    menu = new JMenu("Edit");
    menu.setMnemonic(69);
    
    item1 = new JMenuItem("Undo");
    item1.setActionCommand("mniUndo");
    item1.addActionListener(menuListener);
    menu.add(item1);
    item1.setMnemonic('U');
    item1.setAccelerator(KeyStroke.getKeyStroke(
      90, 2));
    
    item1 = new JMenuItem("Redo");
    item1.setActionCommand("mniRedo");
    item1.addActionListener(menuListener);
    menu.add(item1);
    item1.setMnemonic('R');
    item1.setAccelerator(KeyStroke.getKeyStroke(
      89, 2));
    
    item1 = new JMenuItem("Copy");
    item1.setActionCommand("mniCopy");
    item1.addActionListener(menuListener);
    menu.add(item1);
    item1.setMnemonic('C');
    item1.setAccelerator(KeyStroke.getKeyStroke(
      67, 2));
    
    item1 = new JMenuItem("Paste");
    item1.setActionCommand("mniPaste");
    item1.addActionListener(menuListener);
    menu.add(item1);
    item1.setMnemonic('P');
    item1.setAccelerator(KeyStroke.getKeyStroke(
      86, 2));
    
    menu.addSeparator();
    item1 = new JMenuItem("Rename Attribute Entity");
    item1.addActionListener(menuListener);
    

















    JMenuItem merge = new JMenuItem("Merge zones");
    merge.setActionCommand("Merge zones");
    merge.addActionListener(menuListener);
    menu.add(merge);
    merge.setMnemonic('M');
    merge.setAccelerator(KeyStroke.getKeyStroke(
      77, 2));
    
    mniShapeSplit = new JMenuItem("Split");
    disableManageropmodeComponents.add(mniShapeSplit);
    
    disableManagerallOpmodeComponents.add(mniShapeSplit);
    mniShapeSplit.addActionListener(menuListener);
    
    menu.add(mniShapeSplit);
    
    mniShapePCLine = new JMenuItem("Par/Chld Line");
    disableManageropmodeComponents.add(mniShapePCLine);
    disableManagerallOpmodeComponents.add(mniShapePCLine);
    


    menu.addSeparator();
    
    item1 = new JMenuItem("Create/Edit Reading Order");
    item1.setActionCommand("Create/Edit Reading Order");
    item1.addActionListener(menuListener);
    menu.add(item1);
    item1.setMnemonic('O');
    item1.setAccelerator(KeyStroke.getKeyStroke(
      79, 2));
    
    item1 = new JMenuItem("Generate or remove RLEIMAGE");
    item1.setActionCommand("Generate or remove RLEIMAGE");
    item1.addActionListener(menuListener);
    menu.add(item1);
    item1.setMnemonic('R');
    item1.setAccelerator(KeyStroke.getKeyStroke(
      82, 2));
    
    item1 = new JMenuItem("Shrink zone");
    item1.setActionCommand("Shrink zone");
    item1.addActionListener(menuListener);
    menu.add(item1);
    item1.setMnemonic('S');
    item1.setAccelerator(KeyStroke.getKeyStroke(
      83, 2));
    
    item1 = new JMenuItem("Split zone at offsets");
    item1.setActionCommand("Split zone at offsets");
    item1.addActionListener(menuListener);
    menu.add(item1);
    

    item1 = new JMenuItem("Remove overlap");
    item1.setActionCommand("Remove overlap");
    item1.addActionListener(menuListener);
    menu.add(item1);
    
    menu.addSeparator();
    
    JMenuItem findItem = new JMenuItem("Find");
    findItem.setActionCommand("Find");
    findItem.setAccelerator(KeyStroke.getKeyStroke(
      70, 2));
    findItem.addActionListener(menuListener);
    findItem.setEnabled(true);
    menu.add(findItem);
    


    JMenuItem selectAll = new JMenuItem("Select all zones");
    selectAll.setActionCommand("Select all zones");
    selectAll.setAccelerator(KeyStroke.getKeyStroke(
      65, 2));
    selectAll.addActionListener(menuListener);
    selectAll.setEnabled(true);
    menu.add(selectAll);
    

    subMnuRot = new JMenu("Rotate Image");
    subMnuRot.setMnemonic('R');
    subMnuRot.setEnabled(true);
    
    JMenuItem r0 = new JMenuItem("Original");
    r0.setActionCommand("0ï¿½");
    r0.addActionListener(menuListener);
    subMnuRot.add(r0);
    
    JMenuItem r90 = new JMenuItem("Right 90ï¿½");
    r90.setActionCommand("Right 90ï¿½");
    r90.addActionListener(menuListener);
    subMnuRot.add(r90);
    
    JMenuItem r180 = new JMenuItem("Right 180ï¿½");
    r180.setActionCommand("Right 180ï¿½");
    r180.addActionListener(menuListener);
    subMnuRot.add(r180);
    
    JMenuItem r270 = new JMenuItem("Right 270ï¿½");
    r270.setActionCommand("Right 270ï¿½");
    r270.addActionListener(menuListener);
    subMnuRot.add(r270);
    





    mniShapeSplit.setMnemonic('P');
    mniShapeSplit.setAccelerator(KeyStroke.getKeyStroke(
      80, 2));
    






    menu.addSeparator();
    

    item1 = new JMenuItem("Document Capture");
    item1.addActionListener(menuListener);
    item1.setEnabled(true);
    item1.setMnemonic('I');
    item1.setAccelerator(KeyStroke.getKeyStroke(
      123, 2));
    menu.add(item1);
    
    menu.addSeparator();
    

    item1 = new JMenuItem("Preferences");
    item1.addActionListener(menuListener);
    item1.setAccelerator(KeyStroke.getKeyStroke(
      10, 8));
    menu.add(item1);
    

    myMenuBar.add(menu);
    

    menuScripts = new JMenu("Scripts");
    menuScripts.setMnemonic(84);
    menuScripts.addMenuListener(new javax.swing.event.MenuListener() {
      public void menuCanceled(MenuEvent arg0) {}
      
      public void menuDeselected(MenuEvent arg0) {}
      
      public void menuSelected(MenuEvent arg0) {
        if (scriptLoader == null) {
          scriptLoader = new ScriptLoader();
        }
        menuScripts.removeAll();
        if (CapabilitiesControl.isEnableScripting()) {
          OCRInterface.this.addScripts();
          OCRInterface.this.addScriptHowTo();
        }
        else {
          JMenuItem scriptName = new JMenuItem("<No available scripts>");
          scriptName.setEnabled(false);
          menuScripts.add(scriptName);
        }
      }
    });
    



    if (System.getProperty("customGEDI", "").equalsIgnoreCase("MARIA")) {
      item1 = new JMenuItem("Text Extaction");
      item1.setActionCommand("TEXTRACT");
      item1.addActionListener(menuListener);
      
      item1.setEnabled(false);
      
      JMenuItem item2 = new JMenuItem("Zones Comparison (PE)");
      item2.setActionCommand("TEXTPE");
      item2.addActionListener(menuListener);
      
      item2.setEnabled(false);
    }
    


    myMenuBar.add(menuScripts);
    

    menu = new JMenu("View");
    menu.setMnemonic('V');
    
    item1 = new JMenuItem("Fit Image to Window");
    menu.add(item1);
    item1.setActionCommand("FitImageWnd");
    item1.addActionListener(menuListener);
    
    item1 = new JMenuItem("Fit To Page Width");
    menu.add(item1);
    item1.setActionCommand("Fit To Page Width");
    item1.addActionListener(menuListener);
    
    menu.addSeparator();
    
    item1 = new JMenuItem("400%");
    menu.add(item1);
    item1.setActionCommand("400%");
    item1.addActionListener(menuListener);
    
    item1 = new JMenuItem("300%");
    menu.add(item1);
    item1.setActionCommand("300%");
    item1.addActionListener(menuListener);
    
    item1 = new JMenuItem("200%");
    menu.add(item1);
    item1.setActionCommand("200%");
    item1.addActionListener(menuListener);
    
    item1 = new JMenuItem("150%");
    menu.add(item1);
    item1.setActionCommand("150%");
    item1.addActionListener(menuListener);
    
    item1 = new JMenuItem("125%");
    menu.add(item1);
    item1.setActionCommand("125%");
    item1.addActionListener(menuListener);
    
    item1 = new JMenuItem("100%");
    menu.add(item1);
    item1.setActionCommand("100%");
    item1.addActionListener(menuListener);
    
    item1 = new JMenuItem("75%");
    menu.add(item1);
    item1.setActionCommand("75%");
    item1.addActionListener(menuListener);
    
    item1 = new JMenuItem("50%");
    menu.add(item1);
    item1.setActionCommand("50%");
    item1.addActionListener(menuListener);
    
    item1 = new JMenuItem("25%");
    menu.add(item1);
    item1.setActionCommand("25%");
    item1.addActionListener(menuListener);
    
    item1 = new JMenuItem("12.5%");
    menu.add(item1);
    item1.setActionCommand("12.5%");
    item1.addActionListener(menuListener);
    
    menu.addSeparator();
    
    item1 = new JMenuItem("Info Window");
    menu.add(item1);
    item1.setActionCommand("InfoWindow");
    item1.addActionListener(menuListener);
    item1.setAccelerator(KeyStroke.getKeyStroke(
      73, 
      3));
    
    myMenuBar.add(menu);
    

    menu = new JMenu("Window");
    menu.setMnemonic('W');
    

    item1 = new JCheckBoxMenuItem("Navigation Toolbar");
    item1.setSelected(true);
    item1.setMnemonic('N');
    item1.setActionCommand("navigationToolbar");
    item1.addActionListener(menuListener);
    menu.add(item1);
    

    GTToolbarMenuItem = new JCheckBoxMenuItem("GroundTruth Toolbar");
    GTToolbarMenuItem.setSelected(true);
    GTToolbarMenuItem.setMnemonic('G');
    GTToolbarMenuItem.setActionCommand("groundTruthToolbar");
    GTToolbarMenuItem.addActionListener(menuListener);
    if (CapabilitiesControl.isEnableAlignment()) {
      GTToolbarMenuItem.setEnabled(true);
    } else
      GTToolbarMenuItem.setEnabled(false);
    menu.add(GTToolbarMenuItem);
    
    PolyTranscribeToolbarMenuItem = new JCheckBoxMenuItem("PolygonTranscription Toolbar");
    PolyTranscribeToolbarMenuItem.setSelected(true);
    PolyTranscribeToolbarMenuItem.setMnemonic('P');
    PolyTranscribeToolbarMenuItem.setActionCommand("polygonTranscriptionToolbar");
    PolyTranscribeToolbarMenuItem.addActionListener(menuListener);
    
    TranslateWorkflowToolbarMenuItem = new JCheckBoxMenuItem("TranslateWorkflow Toolbar");
    TranslateWorkflowToolbarMenuItem.setSelected(true);
    TranslateWorkflowToolbarMenuItem.setMnemonic('P');
    TranslateWorkflowToolbarMenuItem.setActionCommand("translateWorkflowToolbar");
    TranslateWorkflowToolbarMenuItem.addActionListener(menuListener);
    
    if (CapabilitiesControl.isEnableAlignment()) {
      GTToolbarMenuItem.setEnabled(true);
    } else
      GTToolbarMenuItem.setEnabled(false);
    menu.add(PolyTranscribeToolbarMenuItem);
    menu.add(TranslateWorkflowToolbarMenuItem);
    

    JCheckBoxMenuItem thresholdingToolbarMenuItem = new JCheckBoxMenuItem("Thresholding Toolbar");
    thresholdingToolbarMenuItem.setMnemonic('P');
    thresholdingToolbarMenuItem.setActionCommand("thresholdingToolbar");
    thresholdingToolbarMenuItem.addActionListener(menuListener);
    thresholdingToolbarMenuItem.setSelected(getThresholdingToolbarVisible());
    if (CapabilitiesControl.isEnableAlignment()) {
      thresholdingToolbarMenuItem.setEnabled(true);
    } else
      thresholdingToolbarMenuItem.setEnabled(false);
    menu.add(thresholdingToolbarMenuItem);
    

    myMenuBar.add(menu);
    

























    menu = new JMenu("Help");
    menu.setMnemonic('H');
    
    item1 = new JMenuItem("GEDI Help");
    item1.setActionCommand("GEDI Help");
    item1.addActionListener(menuListener);
    item1.setEnabled(true);
    
    menu.add(item1);
    
    item1 = new JMenuItem("Shortcut Keys");
    item1.addActionListener(menuListener);
    item1.setEnabled(true);
    item1.setMnemonic('K');
    menu.add(item1);
    
    menu.addSeparator();
    
    item1 = new JMenuItem("About DL-GEDIPro");
    item1.addActionListener(menuListener);
    item1.setEnabled(true);
    item1.setMnemonic('A');
    menu.add(item1);
    




    JMenuItem temp = new JMenuItem("Launch Consistency Tool");
    temp.addActionListener(menuListener);
    temp.setEnabled(true);
    
    String developerModeProp = System.getProperty("DEVELOPER_MODE");
    boolean DEV_MODE = false;
    
    if (developerModeProp != null)
      DEV_MODE = Boolean.valueOf(developerModeProp).booleanValue();
    if (DEV_MODE) {
      menu.add(temp);
    }
    myMenuBar.add(menu);
    
    mousePosistionLabel = new JLabel("");
    mousePosistionLabel.setForeground(Color.RED);
    myMenuBar.add(Box.createHorizontalGlue());
    myMenuBar.add(mousePosistionLabel);
    myMenuBar.add(Box.createHorizontalStrut(20));
    
    return myMenuBar;
  }
  













  public void buildComponents()
  {
    dir_vec = new Vector();
    
    JPanel toolBarPanel = new JPanel();
    toolBarPanel.setLayout(new BoxLayout(toolBarPanel, 1));
    
    toolbar = new BrowserToolBar();
    
    GTToolbar = new JToolBar();
    GTPanel = new GTToolBar();
    GTToolbar.add(GTPanel);
    toolBarPanel.add(toolbar);
    toolBarPanel.add(GTToolbar);
    
    PolyTranscribeToolbar = new JToolBar();
    PolyTranscribePanel = new PolyTranscribeToolBar();
    PolyTranscribeToolbar.add(PolyTranscribePanel);
    toolBarPanel.add(PolyTranscribeToolbar);
    
    TranslateWorkflowToolbar = new JToolBar();
    TranslateWorkflowPanel = new TranslateToolBar();
    TranslateWorkflowToolbar.add(TranslateWorkflowPanel);
    toolBarPanel.add(TranslateWorkflowToolbar);
    
    thresholdingToolbar = new ColorImageToolbar();
    thresholdingToolbar.setVisible(getThresholdingToolbarVisible());
    toolBarPanel.add(thresholdingToolbar);
    
    getContentPane().add(toolBarPanel, "North");
    
    GTToolbar.setVisible(getEnableAlignment());
    PolyTranscribeToolbar.setVisible(getEnablePolygonTranscription());
    TranslateWorkflowToolbar.setVisible(getEnableTranslateWorkflow());
    



    ocrTable = new WorkmodeTable(0);
    



    workmodeTables[0] = ocrTable;
    
    setCursor(Cursor.getPredefinedCursor(3));
    

    currentHWObj = new ImageReaderDrawer(null, this_interface);
    

    mainPane = new JSplitPane(1, true);
    













    right.setUI(new MetalToolBarUI() {
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
        return frame;



      }
      




    });
    right.add(currentHWObj);
    




    list = new JThumbList(model, mainPane.getWidth() - 
      mainPane.getDividerLocation(), mainPane.getHeight() - (
      mainPane.getInsets().bottom + mainPane.getInsets().top));
    




    pane = new JScrollPane(list);
    
    pane.setHorizontalScrollBarPolicy(31);
    
    pane.getVerticalScrollBar().setUnitIncrement(
      (mainPane.getHeight() - (mainPane.getInsets().bottom + 
      mainPane.getInsets().top)) / 2);
    pane.getVerticalScrollBar().setBlockIncrement(
      mainPane.getHeight() - (mainPane.getInsets().bottom + 
      mainPane.getInsets().top));
    pane.setWheelScrollingEnabled(false);
    
    thumb.setUI(new MetalToolBarUI() {
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
        
        frame.setPreferredSize(new Dimension(mainPane.getWidth() - 
          mainPane.getDividerLocation(), 
          mainPane.getHeight() - (
          mainPane.getInsets().bottom + 
          mainPane.getInsets().top)));
        frame.validate();
        
        return frame;
      }
      
    });
    thumb.setPreferredSize(new Dimension(mainPane.getWidth() - 
      mainPane.getDividerLocation(), mainPane.getHeight() - (
      mainPane.getInsets().bottom + mainPane.getInsets().top)));
    thumb.add(pane);
    
    pane.addComponentListener(new java.awt.event.ComponentListener()
    {
      public void componentResized(ComponentEvent arg0)
      {
        list.resizeCells(pane.getWidth(), pane.getHeight());
      }
      
      public void componentMoved(ComponentEvent arg0)
      {
        list.resizeCells(pane.getWidth(), pane.getHeight());
      }
      
      public void componentShown(ComponentEvent arg0)
      {
        System.out.println("shown");
      }
      





      public void componentHidden(ComponentEvent arg0) {}
    });
    rPanel.add(right, "North");
    tPanel.add(thumb, "North");
    
    right_panel.add(rPanel, "Image Panel");
    right_panel.add(tPanel, "Thumb Panel");
    





    getContentPane().addComponentListener(new java.awt.event.ComponentAdapter()
    {
      public void componentResized(ComponentEvent arg0) {
        thumb.setPreferredSize(new Dimension(mainPane.getWidth() - 
          mainPane.getDividerLocation(), 
          mainPane.getHeight() - (
          mainPane.getInsets().bottom + 
          mainPane.getInsets().top)));
        
        OCRInterface.this_interface.modifyHeightOfRightPanel(0);
        






        if (currentHWObjpictureScrollPane != null)
          currentHWObjpictureScrollPane.revalidate();
        thumb.validate();
        right.revalidate();
        right_panel.revalidate();
        mainPane.revalidate();
      }
      
    });
    buildDictDependantGuiElements();
    
    right_panel.setVisible(true);
    right.setFloatable(false);
    thumb.setFloatable(false);
    
    ElectronicTextDisplayer eTextWindow = this_interfacetbdPane.data_panel.a_window.eTextWindow;
    eTextWindow.setVisible(true);
    eTextWindow.setFloatable(true);
    eTextWindow.setMinimumSize(new Dimension(0, 0));
    JPanel p = new JPanel(new BorderLayout());
    p.setMinimumSize(new Dimension(0, 0));
    p.setLayout(new BoxLayout(p, 1));
    p.add(eTextWindow);
    rightSplitPanel = new JSplitPane(0);
    rightSplitPanel.setTopComponent(right);
    rightSplitPanel.setBottomComponent(p);
    rightSplitPanel.setOneTouchExpandable(true);
    rightSplitPanel.setResizeWeight(rightSpliPanelDividerLocation);
    rightSplitPanel.setDividerLocation(rightSpliPanelDividerLocation);
    

    mainPane.setRightComponent(rightSplitPanel);
    
    mainPane.setContinuousLayout(true);
    mainPane.setOneTouchExpandable(true);
    mainPane.setDividerLocation(300);
    mainPane.setDividerSize(6);
    
    if (getLoadPreviousDirOnStartup()) {
      LoadDictionary(1);
    } else {
      loadDefaultImage();
    }
    getContentPane().add(mainPane, "Center");
    
    bottomPanel = new FindPanel();
    
    getContentPane().add(bottomPanel, "South");
    
    pack();
    setVisible(true);
    setCursor(Cursor.getPredefinedCursor(0));
    
    this_interface.getMultiPagePanel().setVisible(false);
    
    this_interfaceocrTable.setColumnWidths();
  }
  









  public void LoadDictionary(int loadDefaultImage)
  {
    saveCursor = getCursor();
    this_interface
      .setCursor(Cursor.getPredefinedCursor(3));
    
    try
    {
      dir_vec.removeAllElements();
      pathVec.clear();
      dictionary_load = true;
      System.out.println("Preparing to read...");
      






      dir_vec.removeAllElements();
      
      ocrProperties.clearFilePropsVec();
      
      addImageNamesToTable(0);
      
      System.out.println("done.");
    }
    catch (NullPointerException nulExc) {
      nulExc.printStackTrace();
      System.out.println(nulExc.toString());
      System.out.println("Default directory " + getCurrentDicDir() + 
        " changed. Please choose another directory");
    }
    

    if (dir_vec != null) {
      ocrTable.removeAll();
      ocrTable.setModel(new WorkmodeTable(0).getModel());
      dictionary_load = false;
    }
    
    try
    {
      if (loadDefaultImage == 1) {
        loadDefaultImage();
      }
    }
    catch (NullPointerException ne) {
      ne.printStackTrace();
    }
    

    setCurrentOppMode(0);
    setTitle("DocLib - GroundTruthing Editor and Document Interface Professional Edition (DL-GEDIPro)");
    setCursor(saveCursor);
    

    if (rawImageOnlyFileList != null) {
      model.removeAll();
      for (File image : rawImageOnlyFileList) {
        model.add(image.getAbsolutePath());
      }
    }
  }
  
  private void loadDefaultImage()
  {
    currDoc = null;
    if (tbdPane != null)
      tbdPane.data_panel.a_window.setPage(null);
    if (tbdPane.data_panel.dataTable != null)
      tbdPane.data_panel.dataTable.myClearSelection();
    toolbar.setScaleByIndex(10);
    currentHWObj.setImageFile("images/mainscreen.jpg");
  }
  










  void buildDictDependantGuiElements()
  {
    tbdPane = new LeftPanel(this_interface);
    mainPane.setLeftComponent(tbdPane);
    mainPane.setDividerLocation(0.15D);
    currWorkmode = 0;
    

    if ((commandLineOptions != null) && 
      (commandLineOptions.getConfigFilePath() != null)) {
      loadConfigFile(commandLineOptions.getConfigFilePath());
    } else {
      loadConfigFile(null);
    }
    validate();
  }
  




















































  public String getFilePath()
  {
    return filePath;
  }
  
  public void openFile(String filepath, String filename)
  {
    saveCursor = getCursor();
    
    this_interfaceocrTable.setCursor(Cursor.getPredefinedCursor(3));
    setCursor(Cursor.getPredefinedCursor(3));
    




    if (!this_interface.isAllowToSwitchImage()) {
      return;
    }
    if (!fclose_cancel_selected)
    {


      boolean isXMLValid = isXMLValid(hasMeta(filepath));
      
      if (!isXMLValid) {
        currDoc = null;
        
        String msg = getErrorMessage();
        
        System.out.println("Invalid XML.");
        System.out.println("msg: " + msg);
        JOptionPane.showMessageDialog(this, msg, 
          "Invalid XML file", 
          0);
        currentHWObj.setImageFile("images/mainscreen.jpg");
        setCursor(Cursor.getPredefinedCursor(0));
        this_interfaceocrTable.setCursor(Cursor.getPredefinedCursor(0));
        return;
      }
      

      if (a_file_opened) {
        this_interfacecurrPageID = 0;
        










        if (this_interface.hasMeta(filepath) == null) {
          currDoc = null;
          ImageDisplay.nulldoc.clear();
        }
      }
      
      if (currentHWObjcurr_canvas.getGroupList() != null) {
        currentHWObjcurr_canvas.getGroupList().clear();
      }
      currentHWObj.setImageFile(filepath);
      

      if (currentHWObj.getOriginalImage() == null) {
        currDoc = null;
        String msg = null;
        System.out.println("Invalid File Format. Image cannot be loaded.");
        msg = filepath + "\n\n" + 
          "WARNING: Unable to load image -- " + 
          "either unsupported file format or file is corrupt.";
        JOptionPane.showMessageDialog(this, 
          msg, 
          "Invalid File", 0);
        
        currentHWObj.setImageFile("images/mainscreen.jpg");
        setCursor(Cursor.getPredefinedCursor(0));
        this_interfaceocrTable.setCursor(Cursor.getPredefinedCursor(0));
      }
      

      this_interface.updateETextWindowContent();
      


      enableRotate(true);
      enableImageSave(false);
      

      if (currentHWObjfile_open) {
        updateCurrFilename();
      }
    }
    
    a_file_opened = true;
    








    uniqueZoneId = new UniqueZoneId(currDoc);
    
    uniqueZoneId.replaceNotValidId();
    









    getCanvasallowEdit = false;
    



    String orientStr = (String)this_interfacetbdPane.data_panel.a_window.getPage().pageTags.get("GEDI_orientation");
    int temp = orientStr != null ? Integer.parseInt(orientStr) / 90 : 0;
    currentHWObjcurr_canvas.rotateImage(90 * temp);
    




    if (currDoc != null) {
      Iterator<DLPage> pageItr = currDocgetDocumentdocumentPages.iterator();
      while (pageItr.hasNext()) {
        DLPage tempPage = (DLPage)pageItr.next();
        String pageOrientStr = (String)pageTags.get("GEDI_orientation");
        if (pageOrientStr != null) {
          int rotations = Integer.parseInt(pageOrientStr) / 90;
          for (int i = 0; i < rotations; i++) {
            tempPage.rotate();
          }
        }
      }
    }
    




    this_interfacetbdPane.data_panel.a_window.getPage().dlSetWidth(
      currentHWObj.getOriginalImage().getWidth());
    this_interfacetbdPane.data_panel.a_window.getPage().dlSetHeight(
      currentHWObj.getOriginalImage().getHeight());
    

    toolbar.setScaleByIndex(toolbar.getCurrentScaleIndex());
    
    ((ColorImageToolbar)thresholdingToolbar).resetBtn();
    
    setCursor(Cursor.getPredefinedCursor(0));
    this_interfaceocrTable.setCursor(Cursor.getPredefinedCursor(0));
    
    updateSRCTag();
    sortOffsets(currDoc);
    cleanUp(currDoc);
    convertRLEtoNewFormat();
    normalizeArabic();
    
    if ((currDoc != null) && (currDoc.isModified())) {
      currDoc.dumpData();
    }
  }
  
  public void loadETextWindow(String filepath, String encoding) {
    if (this_interface.getUseDirNameForTextFile()) {
      filepath = 
      
        getCurrentImageDir() + getImageDirName() + "\\" + getImageDirName() + ".txt";

    }
    else
    {
      filepath = ImageReaderDrawer.getFile_path();
      String ext = this_interface.getFileExtension(filepath);
      filepath = filepath.replace(ext, ".txt");
    }
    

    filePath = filepath;
    
    File f = new File(filepath);
    
    if ((f == null) || (!f.exists())) {
      tbdPane.data_panel.a_window.setTextWindowText("");
      return;
    }
    try
    {
      StringBuffer sb = new StringBuffer();
      BufferedReader br = new BufferedReader(new InputStreamReader(
        new FileInputStream(f), encoding));
      String line = br.readLine();
      
      while (line != null) {
        sb.append(line).append("\n");
        line = br.readLine();
      }
      
      tbdPane.data_panel.a_window.setTextWindowText(sb.toString());
      tbdPane.data_panel.a_window.clearETextSelection();
      return;
    }
    catch (FileNotFoundException localFileNotFoundException) {}catch (IOException localIOException) {}
    




    tbdPane.data_panel.a_window.setTextWindowText("");
  }
  









  public void updateCurrFilename()
  {
    setTitle(ImageReaderDrawer.file_name + " - " + "DocLib - GroundTruthing Editor and Document Interface Professional Edition (DL-GEDIPro)");
    
    LoadDataFile ldf = currDoc;
    boolean isFileModified = false;
    String editedChar = "";
    if (ldf != null) {
      isFileModified = ldf.isModified();
      editedChar = "";
      
      if (isFileModified) {
        editedChar = "*";
      }
    }
    tbdPane.setCurrentFile(ImageReaderDrawer.file_name + editedChar);
  }
  
  public void setTitle(String title) {
    super.setTitle(title);
  }
  

















  public void addToTable2(String pathToImageFile, int workmode, int pos)
  {
    System.out.println("\naddToTable2");
    System.out.println("pathToImageFile: " + pathToImageFile);
    File imageFile = new File(pathToImageFile);
    
    Hashtable<Integer, String> dataFilePaths = new Hashtable();
    Hashtable<Integer, Boolean> fileSoftLockedHash = new Hashtable();
    Hashtable<Integer, Boolean> fileHardLockedHash = new Hashtable();
    Hashtable<Integer, Boolean> fileHasNewCommentsHash = new Hashtable();
    


    dataFilePaths.put(new Integer(0), imageFile.getAbsolutePath());
    

    File lockFile = new File(imageFile.getAbsolutePath() + ".lock");
    if ((!((String)dataFilePaths.get(new Integer(0))).equals("")) && 
      (lockFile.exists())) {
      fileSoftLockedHash.put(new Integer(0), new Boolean(true));
    } else {
      fileSoftLockedHash.put(new Integer(0), new Boolean(false));
    }
    


    if (imageFile.exists()) {
      if ((!((String)dataFilePaths.get(new Integer(0))).equals("")) && 
        (!imageFile.canWrite())) {
        fileHardLockedHash.put(new Integer(0), new Boolean(true));
      } else {
        fileHardLockedHash.put(new Integer(0), new Boolean(false));
      }
    }
    else {
      fileHardLockedHash.put(new Integer(0), new Boolean(false));
    }
    String currDatafile = hasMeta(imageFile.getAbsolutePath());
    
    if (!((String)dataFilePaths.get(new Integer(0))).equals("")) {
      fileHasNewCommentsHash.put(new Integer(0), new Boolean(false));
    }
    

    if (currDatafile != null) {
      dataFilePaths.put(new Integer(1), currDatafile);





    }
    else if (!autoCreateXml) {
      pathVec.add(pathToImageFile);
      dataFilePaths.put(new Integer(1), "");
    }
    else if (!autoCreateXml) {}
    





    lockFile = new File(currDatafile + ".lock");
    
    if ((!((String)dataFilePaths.get(new Integer(1))).equals("")) && 
      (lockFile.exists())) {
      fileSoftLockedHash.put(new Integer(1), new Boolean(true));
    } else {
      fileSoftLockedHash.put(new Integer(1), new Boolean(false));
    }
    

    if (currDatafile != null) {
      File dataFile = new File(currDatafile);
      if ((!((String)dataFilePaths.get(new Integer(1))).equals("")) && 
        (dataFile.exists()) && (!dataFile.canWrite())) {
        fileHardLockedHash.put(new Integer(1), new Boolean(true));
      } else {
        fileHardLockedHash.put(new Integer(1), new Boolean(false));
      }
    }
    else {
      fileHardLockedHash.put(new Integer(1), new Boolean(false));
    }
    if ((currDatafile != null) && (!((String)dataFilePaths.get(new Integer(1))).equals(""))) {
      if (CommentsParser.hasNewFileComments(currDatafile)) {
        fileHasNewCommentsHash.put(new Integer(1), new Boolean(true));
      } else {
        fileHasNewCommentsHash.put(new Integer(1), new Boolean(false));
      }
    } else {
      fileHasNewCommentsHash.put(new Integer(1), new Boolean(false));
    }
    String[] varietyOfExt = getVarietyOfExt(pathToImageFile, currDatafile);
    
    FilePropPacket fpkt = new FilePropPacket(
      imageFile.getPath(), 
      dataFilePaths, 
      fileSoftLockedHash, 
      fileHardLockedHash, 
      fileHasNewCommentsHash, 
      varietyOfExt, 
      workmode);
    
    fpkt.setCollapsed(getCollapseRows());
    


    if (pos == -1) {
      pos = workmodeProps[workmode].getFilePropsVecSize();
    }
    workmodeProps[workmode].addElementToFilePropsVec(pos, fpkt);
    
    if (!getCollapseRows()) {
      for (String varietyExt : varietyOfExt) {
        String xmlExt = this_interface.getFileExtension(varietyExt);
        String imageExt = this_interface.getFileExtension(pathToImageFile);
        String nameToAdd = varietyExt.replace(xmlExt, imageExt);
        addToTable2(nameToAdd, workmode, -1);
      }
    }
  }
  




  public String[] getVarietyOfExt(String baseImage, String baseXml)
  {
    ArrayList<String> verietyList = new ArrayList();
    
    File image = new File(baseImage);
    String imageNoExt = getFileNameWithoutExt(image.getName());
    String imageExt = getFileExtension(baseImage);
    
    if (rawXmlOnlyFileList.isEmpty()) {
      return new String[0];
    }
    for (File xmlFile : rawXmlOnlyFileList) {
      if (xmlFile.getName().startsWith(imageNoExt)) {
        try {
          if (xmlFile.getCanonicalPath().equals(baseXml)) {
            continue;
          }
          




          baseImageForXml = getBaseImage(xmlFile.getName(), imageExt);
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
        

        String baseImageForXml;
        
        if ((baseImageForXml != null) && 
          (baseImageForXml.equals(baseImage))) {
          verietyList.add(xmlFile.getAbsolutePath());
        }
      }
    }
    Comparator<String> comparator = new ImageNamesSorter(imageExt);
    ImageNamesSorter sorter = new ImageNamesSorter(verietyList, comparator);
    String[] sortedNames = sorter.getSortedImageNames();
    
    return sortedNames;
  }
  















































































































































































  public void openFileOrDir()
  {
    DirectoryChooser dirchoose = null;
    try {
      dirchoose = new DirectoryChooser(new File(getCurrentDicDir())
        .getParentFile());
      dialog_open = true;
      setJChoose(dirchoose);
    } catch (NullPointerException npe) {
      dirchoose = new DirectoryChooser(new File(getCurrentDicDir()));
    }
    int selection = dirchoose.showDialog(this, "Select File or Directory");
    
    if (selection != 1)
    {
      setDictionaryName(dirchoose.getSelectedFile().getName());
      setDictionaryName(getDictionaryName().substring(0, 
        getDictionaryName().length() - 4));
      

      setCurrent_dir(dirchoose.getSelectedFile().getParent() + 
        File.separatorChar + getDictionaryName() + 
        File.separatorChar);
      

      LoadDictionary(1);
      this_interfacetbdPane.setCurrentFile("");
      dialog_open = false;
    }
  }
  

  public void openImageDir()
  {
    dialog_open = true;
    
    if (dirLoader == null) {
      dirLoader = new DirLoader();
    }
    
    dirLoader.setLocation(getLocationx + 50, getLocationy + 50);
    dirLoader.repaint();
    dirLoader.setVisible(true);
  }
  
  public void openXmlDir() {
    DirectoryChooser dirchoose = null;
    try {
      dirchoose = new DirectoryChooser(new File(getCurrentXmlDir()));
      
      dialog_open = true;
      setJChoose(dirchoose);
    } catch (NullPointerException npe) {
      dirchoose = new DirectoryChooser(new File(getCurrentXmlDir()));
    }
    int selection = dirchoose.showDialog(this, "Select Xml Directory");
    if (selection != 1)
    {





      File sel_file = dirchoose.getSelectedFile();
      System.out.println(sel_file.getName());
      setCurrentImage_dir(sel_file.getParent() + File.separatorChar);
      setImageDirName(sel_file.getName());
      setCurrentXml_dir(sel_file.getParent() + File.separatorChar);
      setXmlDirName(sel_file.getName());
      LoadDictionary(1);
      dialog_open = false;
    }
  }
  








  boolean check = true;
  
  int type = 0;
  


























































































































































































































































  private void addImageNamesToTable(int whichMode)
  {
    this_interfacefullPathElts.clear();
    
    fillRawImagesList();
    fillRawXmlsList();
    

    System.out.println("\naddImageNamesToTable");
    System.out.println("rawImageOnlyFileList: " + rawImageOnlyFileList.size());
    System.out.println("rawXmlOnlyFileList: " + rawXmlOnlyFileList.size());
    System.out.println();
    for (File file : rawImageOnlyFileList)
      addToTable2(file.getAbsolutePath(), whichMode, -1);
  }
  
  private ArrayList<String> getImageExtensions(ArrayList<File> images) {
    if (images == null) {
      return null;
    }
    ArrayList<String> extensions = new ArrayList();
    
    for (File imageFile : images) {
      String ext = getFileExtension(imageFile.getName());
      if (!extensions.contains(ext)) {
        extensions.add(ext);
      }
    }
    return extensions;
  }
  






  private void fillRawImagesList()
  {
    File imageFilesDir = new File(getCurrentImageDir() + 
      getImageDirName() + 
      File.separator);
    
    if (rawImageOnlyFileList == null) {
      rawImageOnlyFileList = new ArrayList();
    } else {
      rawImageOnlyFileList.clear();
    }
    File[] tempFileList = (File[])null;
    
    String fileExtension_tif = ".TIF";
    String fileExtension_tiff = ".TIFF";
    String fileExtension_bmp = ".BMP";
    String fileExtension_jpg = ".JPG";
    String fileExtension_jpeg = ".JPEG";
    String fileExtension_gif = ".GIF";
    String file_dump = "_DUMP.BMP";
    
    java.io.FileFilter fileFilter = new java.io.FileFilter() {
      public boolean accept(File file) {
        String fileName = file.getName().toUpperCase();
        
        if (file.isFile())
        {
          if ((fileName.endsWith(".TIF")) || 
            (fileName.endsWith(".TIFF")) || 
            ((fileName.endsWith(".BMP")) && 
            (!fileName.endsWith("_DUMP.BMP"))) || 
            (fileName.endsWith(".JPG")) || 
            (fileName.endsWith(".JPEG")) || 
            (fileName.endsWith(".GIF"))) return true;
        }
        return 
        







          false;
      }
    };
    

    if (imageFilesDir.isDirectory()) {
      tempFileList = imageFilesDir.listFiles(fileFilter);
      Arrays.sort(tempFileList);
    }
    else if (imageFilesDir.isFile()) {
      int index = getCurrentImageDir().lastIndexOf(File.separatorChar);
      String dir = getCurrentImageDir().substring(0, index);
      index = dir.lastIndexOf(File.separatorChar);
      
      setCurrentImage_dir(dir.substring(0, index));
      setImageDirName(dir.substring(index));
      
      tempFileList = new File[] { imageFilesDir };
    }
    
    if (tempFileList == null) {
      return;
    }
    for (int i = 0; i < tempFileList.length; i++) {
      rawImageOnlyFileList.add(tempFileList[i]);
    }
    if (bottomPanel != null) {
      bottomPanel.updateImageList();
    }
  }
  




  private void fillRawXmlsList()
  {
    File xmlFilesDir = new File(getCurrentXmlDir() + 
      getXmlDirName() + 
      File.separator);
    
    int numLockedFiles = 0;
    
    ArrayList<File> selectedFiles = new ArrayList();
    
    if (rawXmlOnlyFileList == null) {
      rawXmlOnlyFileList = new ArrayList();
    } else {
      rawXmlOnlyFileList.clear();
    }
    String fileExtension_xml = ".XML";
    String fileExtension_lock = ".LOCK";
    

    File xx = new File(getCurrentImageDir() + 
      getImageDirName() + 
      File.separator);
    
    System.out.println("xmlFilesDir.isDirectory(): " + new File(xmlFilesDir.getAbsolutePath()).isDirectory());
    System.out.println("xmlFilesDir: " + xmlFilesDir.canRead() + "/" + xmlFilesDir);
    File[] allFiles = xmlFilesDir.listFiles();
    
    int j;
    
    int i;
    
    File file;
    
    for (Iterator localIterator = rawImageOnlyFileList.iterator(); localIterator.hasNext(); 
        


        i < j)
    {
      File img = (File)localIterator.next();
      String imgName = img.getName();
      String imgNameNoExt = imgName.substring(0, imgName.indexOf("."));
      File[] arrayOfFile1;
      j = (arrayOfFile1 = allFiles).length;i = 0; continue;file = arrayOfFile1[i];
      if (((file.getName().toUpperCase().endsWith(".XML")) || 
        (file.getName().toUpperCase().endsWith(".LOCK"))) && 
        (file.getName().toUpperCase().startsWith(imgNameNoExt.toUpperCase()))) {
        selectedFiles.add(file);
      }
      i++;
    }
    

























    if (selectedFiles.size() == 0) {
      return;
    }
    ArrayList<String> availableExtensions = getImageExtensions(rawImageOnlyFileList);
    


    for (int i = 0; i < selectedFiles.size(); i++) {
      String xml = ((File)selectedFiles.get(i)).getName();
      for (String ext : availableExtensions) {
        String baseImage = getBaseImage(xml, ext);
        if ((baseImage != null) && (rawImageOnlyFileList.contains(new File(baseImage)))) {
          if (((File)selectedFiles.get(i)).getName().endsWith(".LOCK".toLowerCase())) {
            numLockedFiles++;

          }
          else if (!rawXmlOnlyFileList.contains(selectedFiles.get(i))) {
            rawXmlOnlyFileList.add((File)selectedFiles.get(i));
          }
        }
      }
    }
    if (rawXmlOnlyFileList.isEmpty()) {
      return;
    }
    rawXmlOnlyFileList.trimToSize();
    
    System.out.println("rawXmlOnlyFileList: " + rawXmlOnlyFileList.size());
    
    if (this_interfaceGTPanel.isVisible()) {
      this_interfaceGTPanel.setNumFiles(numLockedFiles, 
        rawImageOnlyFileList.size());
    }
    if (this_interfacePolyTranscribePanel.isVisible()) {
      this_interfacePolyTranscribePanel.setNumFiles(numLockedFiles, 
        rawImageOnlyFileList.size());
    }
  }
  
































  public String getBaseImage(String xmlFileName, String imageExt)
  {
    String imageDir = getCurrentImageDir() + 
      getImageDirName() + 
      File.separator;
    
    String searchingFilePath = imageDir + xmlFileName;
    
    if (xmlFileName.lastIndexOf('.') == -1) {
      return null;
    }
    String cut = searchingFilePath.substring(0, searchingFilePath.lastIndexOf('.'));
    File file = new File(cut + imageExt);
    
    if (file.exists()) {
      return file.getAbsolutePath();
    }
    
    cut = xmlFileName.substring(0, xmlFileName.lastIndexOf('.'));
    return getBaseImage(cut, imageExt);
  }
  




































  public void actionPerformed(ActionEvent e)
  {
    if ((e.getActionCommand() == "comboBoxChanged") && (!dictionary_load)) {
      try {
        dir_vec.removeAllElements();
      }
      catch (OutOfMemoryError outOfMem) {
        System.out.println("Can only open these files: " + 
          outOfMem.toString());
      }
    }
  }
  






  private void closeApplication()
  {
    System.out.println("Closing Application");
    saveFilesDialog.showForExit();
    boolean close_window_event = false;
    acu.showSaveDialog();
    dialog_open = true;
    final JOptionPane optionPane = new JOptionPane(
      "Do you really want to quit?", 3, 
      0);
    
    final JDialog dialog = new JDialog(this_interface, 
      "Exit Application", true);
    setDialog(dialog);
    setDialog(dialog);
    dialog.setContentPane(optionPane);
    
    dialog.setDefaultCloseOperation(0);
    
    optionPane.addPropertyChangeListener(new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName();
        
        if ((dialog.isVisible()) && (e.getSource() == optionPane) && 
          (prop.equals("value"))) {
          dialog.setVisible(false);
        }
        
      }
      
    });
    dialog.setLocation(new Point(500, 300));
    dialog.pack();
    dialog.setVisible(true);
    int value = ((Integer)optionPane.getValue()).intValue();
    
    int response = value;
    


    if (response == 0) {
      close_window_event = true;
      

      actionSaveWorkspace();
      
      String configFileDir = new File(config_file).getParent();
      String xmlDir = getCurrentXmlDir() + getXmlDirName();
      
      System.out.println("configFileDir: " + configFileDir);
      System.out.println("xmlDir: " + xmlDir);
      if ((configFileDir != null) && (!configFileDir.equalsIgnoreCase(xmlDir)))
      {


        if (getSaveConfigCopyInDataDirectory()) {
          String configPath = xmlDir + File.separator + "GEDIConfig.xml";
          
          LoadDataFile.dumpWorkspace(new TypeSettings(
            tbdPane.data_panel.t_window
            .getSaveableTypeSettings(), acu
            .getTypeAttributesMap()), null, configPath);
        }
      }
      





















      dispose();
      
      this_interface.dispose();
      dialog_open = false;
      networkListener.threadDone(true);
      networkListener.kill();
      

      String path = System.getProperty("user.dir") + File.separator + "temp.xml";
      File tempFile = new File(path);
      if (tempFile.exists()) {
        tempFile.deleteOnExit();
      }
      executeExitCode();
      System.exit(0);
    } else if (response == 1) {
      close_window_event = false;
    }
    dialog_open = false;
    
    if (close_window_event) {
      if ((exit_ocr) && (!fclose_cancel_selected))
        executeExitCode();
    } else {
      exit_ocr = false;
      System.out.println("Do not let the application exit");
    }
  }
  






  private void executeExitCode()
  {
    System.out.println("Exiting Application\n");
    

    if (WorkmodeTable.popUpImageChips != null)
      popUpImageChipsframe.dispose();
    setVisible(false);
    
    Properties props = LoadGEDIProps.getInstance();
    setProperties(props);
    LoadGEDIProps.closeGEDIProps();
    LoadUserProps.closeUserProps();
    


    System.out.println(GetDateTime.getInstance());
    
    logFilesManager.run();
  }
  
















  private void setProperties(Properties prop)
  {
    prop.setProperty("Image_Path", getCurrentImageDir());
    prop.setProperty("Xml_Path", getCurrentXmlDir());
    prop.setProperty("Image_Dir", getImageDirName());
    prop.setProperty("Xml_Dir", getXmlDirName());
    prop.setProperty("Config_Path", config_file);
    
    prop.setProperty("autoSaveXmlFileChanges", saveFilesDialog.isAutoSave());
    
    prop.setProperty("autoCreateConfigFile", getAutoCreateConfigFile());
    
    prop.setProperty("autoOverwriteOverlayFile", getAutoOverwriteOverlayFile());
    
    LoadUserProps.getInstance().setProperty("enableProfileLoadOnLogin", 
      getEnableProfileLoad());
    prop.setProperty("dltextlineDefaultSegmentation", 
      getDlTextLineDefaultSegmentation());
    prop.setProperty("saveConfigCopyInDataDirectory", 
      getSaveConfigCopyInDataDirectory());
    prop.setProperty("loadPreviousDirOnStartup", getLoadPreviousDirOnStartup());
    prop.setProperty("displayCommentsInSeparateWindow", getDisplayCommentsInSeparateWindow());
    prop.setProperty("autoShowDlTextLineContentWindowOnCreate", getShowContentWindowOnDlTextLineCreate());
    prop.setProperty("showTextOnAllZones", getShowTextOnAllZones());
    prop.setProperty("showReadingOrder", getShowReadingOrder());
    prop.setProperty("useStartPoint", getUseStartPoint());
    prop.setProperty("allowXmlImage", getAllowXmlImage());
    



    prop.setProperty("collapseRows", 
      getAttsConfigUtil().getCollapseRowsBox().isSelected());
    prop.setProperty("allowWordSplit", getAllowWordSplit());
    prop.setProperty("lockContentEditing", getLockContentEditing());
    prop.setProperty("enableDigitTokenPopup", getEnableDigitTokenPopup());
    prop.setProperty("enableReverseButton", getEnableReverseButton());
    prop.setProperty("normalizeArabicForm_B", getNormalizeArabicForm_B());
    prop.setProperty("saveNormalizedArabicContent", getSaveNormalizedArabicContent());
    prop.setProperty("recomputeRLEonEdit", getRecomputeRLEonEdit());
    prop.setProperty("preserveAttributesOnMerge", getPreserveAttributesOnMerge());
    prop.setProperty("insertSpaceOnMerge", getInsertSpaceOnMerge());
    prop.setProperty("mergeZonesToBox", getMergeZonesToBox());
    prop.setProperty("expandThenShrink", getExpandThenShrink());
    prop.setProperty("minMaxExpand", getMinMaxExpand());
    prop.setProperty("convertRectanglesToPolygonsOnShrink", getConvertRectanglesToPolygonsOnShrink());
    prop.setProperty("shrinkAfterOverlapRemoval", getShrinkAfterOverlapRemoval());
    prop.setProperty("enableNonConvexPolygonShrinking", getEnableNonConvexPolygonShrinking());
    prop.setProperty("polygonPadding", getPolygonPadding());
    prop.setProperty("overlapRemovalPadding", getOverlapRemovalPadding());
    prop.setProperty("intersectionThreshold", getIntersectionThreshold());
    prop.setProperty("edgesGranularity", getEdgesGranularity());
    prop.setProperty("mergeTextToFirstZone", getMergeTextToFirstZone());
    prop.setProperty("alignmentDefaultSegmenation", getAlignmentDefaultSegmenation());
    prop.setProperty("allowZoneRecenter", getAllowZoneRecenter());
    prop.setProperty("showZoneTypes", 
      getShowZoneTypes());
    prop.setProperty("enableParentChildButtons", 
      getEnablePCButtons());
    

    prop.setProperty("transparency", getTransparency());
    prop.setProperty("pseudoColorAttribute", getPseudoColorAttribute());
    prop.setProperty("displayField", getDisplayField());
    prop.setProperty("useETextWindow", getUseETextWindow());
    prop.setProperty("enableAlignment", getEnableAlignment());
    prop.setProperty("enablePolygonTranscription", getEnablePolygonTranscription());
    prop.setProperty("polygonTranscriptionType", getPolygonTranscriptionType());
    prop.setProperty("enableTranslateWorkflow", getEnableTranslateWorkflow());
    prop.setProperty("enforceTranscriptionReview", getEnforceTranscriptionReview());
    prop.setProperty("transcribeUsingBoxes", getTranscribeUsingBoxes());
    prop.setProperty("useDirNameForTextFile", getUseDirNameForTextFile());
    prop.setProperty("documentCaptureToDefaultFile", 
      getDocumentCaptureToDefaultFile());
    prop.setProperty("enableNetworkListener", 
      Boolean.toString(getNetworkListenerOn()));
    prop.setProperty("listenerPort", getListenerPort());
    prop.setProperty("networkListenerRootPath", getRootPath());
    prop.setProperty("applyExpandShrink", getApplyExpandShrink());
    prop.setProperty("connectedComponentFilterSize", 
      getConnectedComponentFilterSize());
    prop.setProperty("documentComments", getCommentText());
    prop.setProperty("maxRefineExpand", 
      getMaxRefineExpand());
    prop.setProperty("cutTolerance", 
      getCutTolerance());
    prop.setProperty("GediAlignCmdPath", 
      getGediAlignCmdPath());
    prop.setProperty("allowIdenticalZones", 
      getAllowIdenticalZones());
    prop.setProperty("warnIdenticalZonesWereCreated", 
      getWarnIdenticalZonesWereCreated());
    prop.setProperty("lineThickness", 
      getLineThickness());
    prop.setProperty("indicDigitsON", 
      getIndicDigitsON());
    prop.setProperty("thresholdingToolbarVisible", 
      getThresholdingToolbarVisible());
    
    prop.setProperty("lowerUpperThresholdValues", 
      getLowerUpperThresholdValues());
    prop.setProperty("ctrl_C_Copies", getCtrl_C_Copies());
    prop.setProperty("ctrl_C_separator", getCtrl_C_separator());
    prop.setProperty("shrinkAfterSplit", getShrinkAfterSplit());
    prop.setProperty("preserveReadingOrderOnSplit", getPreserveReadingOrderOnSplit());
    prop.setProperty("preserveAttributesOnSplit", getPreserveAttributesOnSplit());
    prop.setProperty("randomConstantForPseudoColor", getRandomConstantForPseudoColor());
    




    if (prop.containsKey("caciCmdPath")) {
      prop.remove("caciCmdPath");
    }
    

    if (prop.containsKey("showOnlySelectedTextLine")) {
      prop.remove("showOnlySelectedTextLine");
    }
    

    if (prop.containsKey("allowContentEditing")) {
      prop.remove("allowContentEditing");
    }
    

    if (prop.containsKey("warnZoneEditingWithRLE")) {
      prop.remove("warnZoneEditingWithRLE");
    }
    

    if (prop.containsKey("lockZoneEditingWithRLE")) {
      prop.remove("lockZoneEditingWithRLE");
    }
    

    if (prop.containsKey("writeCommentsByDirectory")) {
      prop.remove("writeCommentsByDirectory");
    }
    

    if (prop.containsKey("useNewAlignment")) {
      prop.remove("useNewAlignment");
    }
    

    if (prop.containsKey("GediAlignCmdPath")) {
      prop.remove("GediAlignCmdPath");
    }
    

    if (prop.containsKey("enablePseudoColor")) {
      prop.remove("enablePseudoColor");
    }
    

    if (prop.containsKey("enablePixelShrinking")) {
      prop.remove("enablePixelShrinking");
    }
    

    if (prop.containsKey("autoZoneShrinkAndExpand")) {
      prop.remove("autoZoneShrinkAndExpand");
    }
    
    if (prop.containsKey("autoZoneShrink"))
      prop.remove("autoZoneShrink");
  }
  
  public String getCurrImgDir() {
    return current_image_dir;
  }
  

  public boolean getUseDirNameForTextFile() { return useDirNameForTextFile; }
  
  public String getGediAlignCmdPath() {
    if (GediAlignCmdPath == null)
      GediAlignCmdPath = "\\lib\\GediAlign\\GediAlign.exe";
    return GediAlignCmdPath;
  }
  
  public boolean getUseETextWindow() { return useETextWindow; }
  
  public boolean getEnableAlignment()
  {
    return enableAlignment;
  }
  
  public boolean getEnablePolygonTranscription() { return enablePolygonTranscription; }
  
  public boolean getEnableTranslateWorkflow() {
    return enableTranslateWorkflow;
  }
  
  public String getPolygonTranscriptionType() { return polygonTranscriptionType; }
  
  public boolean getEnforceTranscriptionReview() {
    return enforceTranscriptionReview;
  }
  
  public boolean getTranscribeUsingBoxes() { return transcribeUsingBoxes; }
  
  public boolean getShowTextOnAllZones() {
    return showTextOnAllZones;
  }
  
  public boolean getShowReadingOrder() { return showReadingOrder; }
  
  public boolean getUseStartPoint() {
    return useStartPoint;
  }
  
  public boolean getAllowXmlImage() {
    return allowXmlImage;
  }
  
  public boolean getAllowWordSplit() { return allowWordSplit; }
  
  public boolean getLockContentEditing() {
    return lockContentEditing;
  }
  
  public boolean getEnableDigitTokenPopup() { return enableDigitTokenPopup; }
  
  public boolean getEnableReverseButton() {
    return enableReverseButton;
  }
  
  public boolean getNormalizeArabicForm_B() { return normalizeArabicForm_B; }
  
  public boolean getSaveNormalizedArabicContent() {
    return saveNormalizedArabicContent;
  }
  
  public boolean getRecomputeRLEonEdit() { return recomputeRLEonEdit; }
  
  public boolean getPreserveAttributesOnMerge() {
    return preserveAttributesOnMerge;
  }
  
  public boolean getInsertSpaceOnMerge() { return insertSpaceOnMerge; }
  
  public boolean getMergeZonesToBox() {
    return mergeZonesToBox;
  }
  
  public boolean getExpandThenShrink() { return expandThenShrink; }
  
  public String getMinMaxExpand() {
    return minMaxExpand;
  }
  
  public boolean getConvertRectanglesToPolygonsOnShrink() { return convertRectanglesToPolygonsOnShrink; }
  
  public boolean getShrinkAfterOverlapRemoval() {
    return shrinkAfterOverlapRemoval;
  }
  
  public boolean getEnableNonConvexPolygonShrinking() { return enableNonConvexPolygonShrinking; }
  
  public int getPolygonPadding() {
    return polygonPadding;
  }
  
  public int getOverlapRemovalPadding() { return overlapRemovalPadding; }
  
  public int getIntersectionThreshold() {
    return intersectionThreshold;
  }
  
  public int getEdgesGranularity() { return edgesGranularity; }
  
  public boolean getMergeTextToFirstZone() {
    return mergeTextToFirstZone;
  }
  
  public String getAlignmentDefaultSegmenation() { return alignmentDefaultSegmenation; }
  
  public boolean getAllowZoneRecenter() {
    return allowZoneRecenter;
  }
  
  public boolean getShowZoneTypes() { return showZoneTypes; }
  

  public boolean getEnablePCButtons() { return enablePCButtons; }
  
  private float getTransparency() {
    float f = TRANSPARENCY;
    return (1.0F - f) * 100.0F;
  }
  
  public String getPseudoColorAttribute() { return pseudoColorAttribute; }
  
  public String getDisplayField() {
    return displayField;
  }
  
  public boolean getShowContentWindowOnDlTextLineCreate() { return showContentWindowOnDlTextLineCreate; }
  
  public boolean getDisplayCommentsInSeparateWindow() {
    return displayCommentsInSeparateWindow;
  }
  
  private String getDlTextLineDefaultSegmentation() {
    return dltextlineDefaultSegmentation;
  }
  
  private boolean getAutoCreateConfigFile() {
    return autoCreateConfigFile;
  }
  
  public boolean getAutoOverwriteOverlayFile() {
    return autoOverwriteOverlayFile;
  }
  
  public boolean getEnableProfileLoad() { return enableProfileLoad; }
  
  public boolean getSaveConfigCopyInDataDirectory() {
    return saveConfigCopyInDataDirectory;
  }
  
  public boolean getLoadPreviousDirOnStartup() { return loadPreviousDirOnStartup; }
  
  private boolean getDocumentCaptureToDefaultFile() {
    return documentCaptureToDefaultFile;
  }
  
  public int getListenerPort() { return listenerPort; }
  
  public String getRootPath()
  {
    return rootPath;
  }
  
  public boolean getNetworkListenerOn() {
    return networkListenerOn;
  }
  
  public boolean getApplyExpandShrink() {
    return applyExpandShrink;
  }
  
  public int getConnectedComponentFilterSize() {
    return connectedComponentFilterSize;
  }
  
  public String getCommentText() {
    return commentText;
  }
  
  public boolean getAllowIdenticalZones() {
    return allowIdenticalZones;
  }
  
  public boolean getWarnIdenticalZonesWereCreated() {
    return warnIdenticalZonesWereCreated;
  }
  
  public int getMaxRefineExpand() {
    return maxRefineExpand;
  }
  
  public int getCutTolerance() { return cutTolerance; }
  
  public boolean getCollapseRows() {
    return collapseRows;
  }
  
  public float getLineThickness() {
    return lineThickness;
  }
  
  public DirLoader getLoader() {
    return dirLoader;
  }
  
  public boolean getIndicDigitsON() {
    return indicDigitsON;
  }
  
  public boolean getThresholdingToolbarVisible() {
    return thresholdingToolbarVisible;
  }
  
  public String getLowerUpperThresholdValues() {
    return lowerUpperThresholdValues;
  }
  
  public String getCtrl_C_Copies() { return ctrl_C_Copies; }
  
  public String getCtrl_C_separator() {
    return ctrl_C_separator;
  }
  
  public boolean getShrinkAfterSplit() { return shrinkAfterSplit; }
  
  public boolean getPreserveReadingOrderOnSplit() {
    return preserveReadingOrderOnSplit;
  }
  
  public boolean getPreserveAttributesOnSplit() { return preserveAttributesOnSplit; }
  
  public String getRandomConstantForPseudoColor() {
    return randomConstantForPseudoColor;
  }
  



  public void update_tables()
  {
    imgTypeAssoc = new LinkedHashMap();
    
    int col = ocrTable.getSelectedColumn();
    int row = ocrTable.getSelectedRow();
    dir_vec.removeAllElements();
    
    ocrProperties.clearFilePropsVec();
    
    addImageNamesToTable(0);
    

    HashMap<Integer, Integer> columnWidth = new HashMap();
    for (int i = 0; i < ocrTable.getColumnCount(); i++) {
      columnWidth.put(Integer.valueOf(i), Integer.valueOf(ocrTable.getColumnModel().getColumn(i).getPreferredWidth()));
    }
    ocrTable.removeAll();
    WorkmodeTable wmt = new WorkmodeTable(0);
    ocrTable.setModel(wmt.getModel());
    ocrTable.changeSelection(row, col, false, false);
    


    for (Map.Entry<Integer, Integer> e : columnWidth.entrySet()) {
      TableColumn column = ocrTable.getColumnModel().getColumn(((Integer)e.getKey()).intValue());
      column.setPreferredWidth(((Integer)e.getValue()).intValue());
    }
  }
  
  public boolean checkImgType(String img)
  {
    for (String s : imgTypes)
    {
      if ((img.toLowerCase().endsWith(s)) && (!img.contains("FULL")) && (!img.contains("_dump")))
        return true;
    }
    return false;
  }
  
  public String checkGTType(String img)
  {
    for (String s : extList)
    {
      if (img.toLowerCase().contains(s))
        return s;
    }
    return null;
  }
  






  public void itemStateChanged(ItemEvent event)
  {
    JCheckBox tmp_Xbox = (JCheckBox)event.getSource();
    save_items_hash.remove(new Integer(tmp_Xbox.getName()));
    save_items_hash.put(new Integer(tmp_Xbox.getName()), new Boolean(
      tmp_Xbox.isSelected()));
  }
  










  public void keyPressed(KeyEvent e)
  {
    controlIsDown = e.isControlDown();
  }
  
  private Vector<DLZone> getSortedZones()
  {
    Vector<DLZone> zones = (Vector)currentHWObjcurr_canvas.getShapeVec().getAsVector().clone();
    
    Iterator<DLZone> zoneItr = zones.iterator();
    while (zoneItr.hasNext()) {
      Zone temp = (Zone)zoneItr.next();
      if ((previousZone != null) || (temp.isNeverVisible())) {
        zoneItr.remove();
      }
    }
    java.util.Collections.sort(zones, new Comparator() {
      public int compare(Object o1, Object o2) {
        Rectangle r1 = ((Zone)o1).get_Bounds();
        Rectangle r2 = ((Zone)o2).get_Bounds();
        









        if (y > y + height / 2) {
          return 1;
        }
        if (y + height < y + height / 2) {
          return -1;
        }
        if (x > x)
        {


          return -1;
        }
        


        return 1;
      }
      
      public boolean equals(Object obj)
      {
        throw new UnsupportedOperationException("equals not supported");
      }
    });
    return zones;
  }
  





  public DLZone nextTabZone(DLZone currZone)
  {
    Vector<DLZone> zones = getSortedZones();
    DLZone temp = currZone;
    
    while (previousZone != null) {
      temp = previousZone;
    }
    int index = zones.indexOf(temp);
    return (DLZone)zones.get((index + 1) % zones.size());
  }
  






  private DLZone nextZone(DLZone currZone)
  {
    Vector<DLZone> zones = getSortedZones();
    DLZone temp = currZone;
    
    while (previousZone != null) {
      temp = previousZone;
    }
    int index = zones.indexOf(temp);
    if (nextZone != null)
      return nextZone;
    if (index + 1 < zones.size()) {
      return (DLZone)zones.get(index + 1);
    }
    return null;
  }
  






  public DLZone prevTabZone(DLZone currZone)
  {
    Vector<DLZone> zones = getSortedZones();
    
    int index = zones.indexOf(currZone);
    DLZone ret = (DLZone)zones.get((index + zones.size() - 1) % zones.size());
    
    while (nextZone != null) {
      ret = nextZone;
    }
    return ret;
  }
  






  private DLZone prevZone(DLZone currZone)
  {
    Vector<DLZone> zones = getSortedZones();
    DLZone temp = currZone;
    
    while (previousZone != null) {
      temp = previousZone;
    }
    int index = zones.indexOf(temp);
    if (previousZone != null)
      return previousZone;
    if (index > 0) {
      return (DLZone)zones.get(index - 1);
    }
    return null;
  }
  

  private void setHotKeys()
  {
    hotkeyManager = GlobalHotkeyManager.getInstance();
    

    hotkeyManager.getInputMap().put(
      KeyStroke.getKeyStroke(36, 0), "HOME");
    hotkeyManager.getActionMap().put("HOME", new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        if (ImageDisplay.activeZones.size() == 1) {
          Zone currZone = ImageDisplay.activeZones.elementAt(0);
          caret = currZone.getContents().length();
          currZone.setSelectedWord(null);
          ImageDisplay.showCaret = true;
          OCRInterface.this_interface.getCanvas().paintCanvas();
        }
        
      }
    });
    hotkeyManager.getInputMap().put(
      KeyStroke.getKeyStroke(35, 0), "END");
    hotkeyManager.getActionMap().put("END", new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        if (ImageDisplay.activeZones.size() == 1) {
          Zone currZone = ImageDisplay.activeZones.elementAt(0);
          caret = 0;
          currZone.setSelectedWord(null);
          ImageDisplay.showCaret = true;
          OCRInterface.this_interface.getCanvas().paintCanvas();
        }
        
      }
    });
    hotkeyManager.getInputMap().put(
      KeyStroke.getKeyStroke(37, 2), 
      "WORD_LEFT");
    hotkeyManager.getActionMap().put("WORD_LEFT", 
      new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          word_left_action();
        }
        
      });
    hotkeyManager.getInputMap().put(
      KeyStroke.getKeyStroke(39, 2), 
      "WORD_RIGHT");
    
    hotkeyManager.getActionMap().put("WORD_RIGHT", 
      new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          word_right_action();
        }
        
      });
    hotkeyManager.getInputMap().put(
      KeyStroke.getKeyStroke(37, 0), "CARET_LEFT");
    hotkeyManager.getActionMap().put("CARET_LEFT", new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        if (ImageDisplay.activeZones.size() > 0) {
          Zone currZone = ImageDisplay.activeZones.elementAt(0);
          if (currZone.hasContents()) {
            int direction = new BidiString(currZone.getContents(), 0).getDirection();
            ImageDisplay.activeZones.removeAllElements();
            
            if ((direction == 0) && (caret < currZone.getContents().length())) {
              caret += 1;
            }
            if ((direction == 1) && (caret > 0)) {
              caret -= 1;
            }
            currZone.setSelectedWord(null);
            ImageDisplay.activeZones.add(currZone);
            ImageDisplay.showCaret = true;
            OCRInterface.this_interface.getCanvas().paintCanvas();
          }
          
        }
      }
    });
    hotkeyManager.getInputMap().put(
      KeyStroke.getKeyStroke(39, 0), "CARET_RIGHT");
    hotkeyManager.getActionMap().put("CARET_RIGHT", new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        if (ImageDisplay.activeZones.size() > 0) {
          Zone currZone = ImageDisplay.activeZones.elementAt(0);
          if (currZone.hasContents()) {
            int direction = new BidiString(currZone.getContents(), 0).getDirection();
            ImageDisplay.activeZones.removeAllElements();
            
            if ((direction == 1) && (caret < currZone.getContents().length())) {
              caret += 1;
            }
            if ((direction == 0) && (caret > 0)) {
              caret -= 1;
            }
            currZone.setSelectedWord(null);
            ImageDisplay.activeZones.add(currZone);
            ImageDisplay.showCaret = true;
            OCRInterface.this_interface.getCanvas().paintCanvas();
          }
          
        }
      }
    });
    hotkeyManager.getInputMap().put(
      KeyStroke.getKeyStroke(9, 0), "TAB");
    hotkeyManager.getActionMap().put("TAB", new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        if (ImageDisplay.activeZones.size() > 0) {
          Zone currZone = ImageDisplay.activeZones.elementAt(0);
          
          if (nextZone != null) {
            currZone = (Zone)nextZone;
          } else {
            currZone = (Zone)nextTabZone(currZone);
          }
          OCRInterface.this_interface.getCanvas().clearActiveZones();
          OCRInterface.this_interface.getCanvas().addToSelected(currZone);
          this_interfacetbdPane.data_panel.a_window.showZoneInfo(ImageDisplay.activeZones);
          
          currZone.setSelectedWord(null);
          
          currentHWObjcurr_canvas.recenterZone();
          ImageDisplay.showCaret = true;
          OCRInterface.this_interface.getCanvas().paintCanvas();
        }
        
      }
    });
    hotkeyManager.getInputMap().put(
      KeyStroke.getKeyStroke(9, 1), "SHIFT_TAB");
    hotkeyManager.getActionMap().put("SHIFT_TAB", new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        if (ImageDisplay.activeZones.size() > 0) {
          Zone currZone = ImageDisplay.activeZones.elementAt(0);
          
          if (previousZone != null) {
            currZone = (Zone)previousZone;
          } else {
            currZone = (Zone)prevTabZone(currZone);
          }
          OCRInterface.this_interface.getCanvas().clearActiveZones();
          OCRInterface.this_interface.getCanvas().addToSelected(currZone);
          this_interfacetbdPane.data_panel.a_window.showZoneInfo(ImageDisplay.activeZones);
          
          currZone.setSelectedWord(null);
          
          currentHWObjcurr_canvas.recenterZone();
          ImageDisplay.showCaret = true;
          OCRInterface.this_interface.getCanvas().paintCanvas();
        }
        
      }
    });
    hotkeyManager.getInputMap().put(
      KeyStroke.getKeyStroke(127, 0), "DELETE");
    hotkeyManager.getActionMap().put("DELETE", new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        if (!OCRInterface.dialog_open) {
          setEnabled(false);
          



          if ((ImageDisplay.activeZones.size() > 0) && 
            (ImageDisplay.activeZones.elementAt(0).getSelectedWord() != null)) {
            Zone currZone = ImageDisplay.activeZones.elementAt(0);
            String newContent = OCRInterface.this.deleteSelectedWord(currZone, false);
            currZone.setContents(newContent);
            tbdPane.data_panel.a_window.showZoneInfo(ImageDisplay.activeZones);
            getCanvas().paintCanvas();
          }
          else if (getCanvas().zoneLineSelected != null)
          {

            getCanvas().saveCurrentState();
            getCanvas().zoneLineSelected.deleteSelectedOffset();

          }
          else if (getCanvas().zonePtSelected != null)
          {
            OCRInterface.this_interface.getCanvas().saveCurrentState();
            boolean delZone = getCanvas().zonePtSelected.deleteSelectedPoint();
            if (delZone) {
              currentHWObjcurr_canvas.deleteCurrentSelectedZone();
            }
            
            getCanvas().zonePtSelected = null;
            getCanvas().paintCanvas();
          }
          else {
            currentHWObjcurr_canvas.deleteCurrentSelectedZone();
          }
          
          setEnabled(true);
        }
        
      }
    });
    hotkeyManager.getInputMap().put(
      KeyStroke.getKeyStroke("shift typed +"), "ZOOM_IN");
    hotkeyManager.getActionMap().put("ZOOM_IN", new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        if (!OCRInterface.dialog_open) {
          setEnabled(false);
          
          toolbar.fireZoomIn();
          setEnabled(true);
        }
        
      }
    });
    hotkeyManager.getInputMap().put(
      KeyStroke.getKeyStroke("shift typed _"), "ZOOM_OUT");
    hotkeyManager.getActionMap().put("ZOOM_OUT", new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        if (!OCRInterface.dialog_open) {
          setEnabled(false);
          
          toolbar.fireZoomOut();
          setEnabled(true);
        }
        
      }
    });
    hotkeyManager.getInputMap().put(
      KeyStroke.getKeyStroke(61, 2), 
      "INCREASE_TEXT_SIZE");
    
    hotkeyManager.getActionMap().put("INCREASE_TEXT_SIZE", 
      new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          currentHWObjcurr_canvas.increaseElectronicTextSize();
          ImageReaderDrawer.picture.repaint();
        }
        
      });
    hotkeyManager.getInputMap().put(
      KeyStroke.getKeyStroke(45, 2), 
      "DECREASE_TEXT_SIZE");
    
    hotkeyManager.getActionMap().put("DECREASE_TEXT_SIZE", 
      new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          currentHWObjcurr_canvas.decreaseElectronicTextSize();
          ImageReaderDrawer.picture.repaint();
        }
      });
    hotkeyManager.getInputMap().put(
      KeyStroke.getKeyStroke(37, 512), 
      "EXPAND_LEFT");
    
    hotkeyManager.getActionMap().put("EXPAND_LEFT", 
      new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          mainPane.setDividerLocation(0);
        }
      });
    hotkeyManager.getInputMap().put(
      KeyStroke.getKeyStroke(39, 512), 
      "EXPAND_RIGHT");
    
    hotkeyManager.getActionMap().put("EXPAND_RIGHT", 
      new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          mainPane.setDividerLocation(300);
        }
      });
    hotkeyManager.getInputMap().put(
      KeyStroke.getKeyStroke(40, 512), 
      "EXPAND_DOWN");
    
    hotkeyManager.getActionMap().put("EXPAND_DOWN", 
      new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          this_interfacetbdPane.resizeDown();
        }
      });
    hotkeyManager.getInputMap().put(
      KeyStroke.getKeyStroke(38, 512), 
      "EXPAND_UP");
    
    hotkeyManager.getActionMap().put("EXPAND_UP", 
      new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          this_interfacetbdPane.resizeUp();
        }
        
      });
    hotkeyManager.getInputMap().put(
      KeyStroke.getKeyStroke(33, 0), "PAGE_UP");
    hotkeyManager.getActionMap().put("PAGE_UP", 
      new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          int row = ocrTable.getSelectedRow();
          if (row == 0) {
            row = ocrTable.getRowCount() - 1;
          } else {
            row--;
          }
          if (!this_interfacemultiPageTiff) {
            OCRInterface.this.processPgUpDown(row, "up");
          } else {
            int sel = getMultiPagePanel().getSelectedPage() - 1;
            
            if (sel > 0) {
              getMultiPagePanel().selectPage(sel);
            }
            else
              OCRInterface.this.processPgUpDown(row, "up");
          }
        }
      });
    hotkeyManager.getInputMap().put(
      KeyStroke.getKeyStroke(34, 0), "PAGE_DOWN");
    
    hotkeyManager.getActionMap().put("PAGE_DOWN", 
      new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          int row = ocrTable.getSelectedRow() + 1;
          if (row >= ocrTable.getRowCount())
            row = 0;
          if (!this_interfacemultiPageTiff) {
            OCRInterface.this.processPgUpDown(row, "down");
          } else {
            int sel = getMultiPagePanel().getSelectedPage() + 1;
            
            if (sel <= getMultiPagePanel().getNumPages()) {
              getMultiPagePanel().selectPage(sel);
            } else {
              OCRInterface.this.processPgUpDown(row, "down");
            }
          }
        }
      });
    hotkeyManager.getInputMap().put(
      KeyStroke.getKeyStroke(27, 0), "ESCAPE");
    hotkeyManager.getActionMap().put("ESCAPE", new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        cancelActions();
      }
      
    });
    int LEFT = 1;int RIGHT = 2;int UP = 3;int DOWN = 4;
    
    hotkeyManager.getInputMap().put(
      KeyStroke.getKeyStroke(37, 0), "MOVE_LEFT");
    hotkeyManager.getActionMap().put("MOVE_LEFT", new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        currentHWObjcurr_canvas.moveZones(1);
      }
      
    });
    hotkeyManager.getInputMap().put(
      KeyStroke.getKeyStroke(39, 0), "MOVE_RIGHT");
    hotkeyManager.getActionMap().put("MOVE_RIGHT", new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        currentHWObjcurr_canvas.moveZones(2);
      }
      
    });
    hotkeyManager.getInputMap().put(
      KeyStroke.getKeyStroke(38, 0), "MOVE_UP");
    hotkeyManager.getActionMap().put("MOVE_UP", new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        currentHWObjcurr_canvas.moveZones(3);
      }
      
    });
    hotkeyManager.getInputMap().put(
      KeyStroke.getKeyStroke(40, 0), "MOVE_DOWN");
    hotkeyManager.getActionMap().put("MOVE_DOWN", new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        currentHWObjcurr_canvas.moveZones(4);
      }
    });
  }
  
  public void cancelActions() {
    setEnabled(false);
    if (currOppmode == 7) {
      currentHWObjcurr_canvas.clearSpiltSelection();
      currentHWObjcurr_canvas.paintCanvas();
      if (!toolbar.getStickyMode())
        setOppmode(0);
    } else if (dialog_open)
    {
      if (jd != null) {
        getDialog().dispose();
        jd = null;
      }
      if (jc != null) {
        getJChoose().cancelSelection();
        jc = null;
      }
      if (jop != null) {
        getJOptP().setValue(Integer.valueOf(1));
        jop = null;
      }
      if ((dirLoader != null) && (dirLoader.isVisible())) {
        DirLoader.cancel();
      }
      
      dialog_open = false;
    } else {
      this_interfacetbdPane.data_panel.t_window.clear(true);
    }
    
    setEnabled(true);
  }
  
  private void processPgUpDown(int row, String command) {
    if (row < 0)
      return;
    System.out.println("processPgUpDown");
    this_interfacepgPressed = true;
    this_interfaceocrTable.myClearSelection();
    this_interfaceocrTable.changeSelection(row, 1, true, false);
    this_interfaceocrTable.processSelectionEvent(row, 1, true);
    




    if (command.equals("up")) {
      getMultiPagePanel().selectPage(getMultiPagePanel().getNumPages());
    } else if (command.equals("down")) {
      getMultiPagePanel().selectPage(1);
    }
    this_interfacepgPressed = false;
  }
  









  public void keyReleased(KeyEvent e)
  {
    controlIsDown = e.isControlDown();
  }
  


















  public boolean notWordSplit(String str, int index)
  {
    return (index == 0) || (index == str.length()) || (Character.isWhitespace(str.charAt(index - 1))) || (Character.isWhitespace(str.charAt(index)));
  }
  







  public void keyTyped(KeyEvent e)
  {
    if ((currOppmode == 20) && (
      (e.getKeyChar() == 'c') || (e.getKeyChar() == 'C'))) {
      copyMeasurementsToClipboard();
    }
    

    if ((getLockContentEditing()) || (!currentHWObjcurr_canvas.allowEdit())) {
      return;
    }
    

    Zone currZone = ImageDisplay.activeZones.elementAt(0);
    
    if (currZone == null) {
      return;
    }
    










    if (currZone.getZoneTags().containsKey("contents"))
    {

      Character c = new Character(e.getKeyChar());
      
      String contents = currZone.getContents();
      
      if (((e.getModifiers() <= 1) && (((c.charValue() >= ' ') && (c.charValue() <= '~')) || 
        (Character.isLetterOrDigit(c.charValue())))) || 
        (isSpecialArabicChar(c)))
      {





        if (currZone.getSelectedWord() != null) {
          contents = deleteSelectedWord(currZone, true);
        }
        contents = contents.substring(0, contents.length() - caret) + c + contents.substring(contents.length() - caret, contents.length());
        currZone.setContents(contents);
        
        if (currDoc != null) {
          currDoc.setModified(true);
        }
        
        tbdPane.data_panel.a_window.showZoneInfo(ImageDisplay.activeZones);
        
        ImageDisplay.showCaret = true;
      }
      
      if (c.charValue() == '\n')
      {



        DLZone nextZone = nextZone(currZone);
        

        while ((nextZone != null) && (!nextZone.hasContents())) {
          nextZone = nextZone(nextZone);
        }
        if ((nextZone != null) && (caret > 0) && ((getAllowWordSplit()) || (notWordSplit(contents, contents.length() - caret)))) {
          getCanvas().saveCurrentState();
          
          if (KeyEvent.getKeyModifiersText(e.getModifiers()).compareTo("Ctrl") == 0) {
            nextZone.setContents(contents.substring(contents.length() - caret).trim());
          } else {
            nextZone.setContents((contents.substring(contents.length() - caret).trim() + " " + nextZone.getContents().trim()).trim());
          }
          currZone.setContents(contents.substring(0, contents.length() - caret).trim());
          caret = 0;
          caret = nextZone.getContents().length();
          
          if ((previousZone == null) && (caret < contents.length())) {
            nextZone = nextZone;
            previousZone = currZone;
            
            if (getCanvas().getReadingOrderHandler().isCyclicReadingOrder(currZone)) {
              nextZone = null;
              previousZone = null;
            }
          }
          






          getCanvas().clearActiveZones();
          getCanvas().addToSelected((Zone)nextZone);
          this_interfacetbdPane.data_panel.a_window.showZoneInfo(ImageDisplay.activeZones);
          currentHWObjcurr_canvas.recenterZone();
        }
      }
      if ((c.charValue() == '\b') && (e.getModifiers() == 0))
      {
        if (currZone.getSelectedWord() != null) {
          String newContent = deleteSelectedWord(currZone, false);
          currZone.setContents(newContent);
        }
        else if (contents.length() > caret)
        {



          contents = contents.substring(0, contents.length() - caret - 1) + contents.substring(contents.length() - caret, contents.length());
          currZone.setContents(contents);

        }
        else
        {

          DLZone prevZone = prevZone(currZone);
          

          while ((prevZone != null) && (!prevZone.hasContents())) {
            prevZone = prevZone(prevZone);
          }
          if ((prevZone != null) && (contents.length() > 0)) {
            getCanvas().saveCurrentState();
            
            prevZone.setContents((prevZone.getContents().trim() + " " + contents.trim()).trim());
            caret = contents.length();
            currZone.setContents("");
            caret = 0;
            





            getCanvas().clearActiveZones();
            getCanvas().addToSelected((Zone)prevZone);
            this_interfacetbdPane.data_panel.a_window.showZoneInfo(ImageDisplay.activeZones);
            currentHWObjcurr_canvas.recenterZone();
          }
        }
      }
      
      if (currDoc != null)
        currDoc.setModified(true);
      tbdPane.data_panel.a_window.showZoneInfo(ImageDisplay.activeZones);
      ImageDisplay.showCaret = true;
      this_interface.getCanvas().paintCanvas();
    }
  }
  
  public void adjustmentValueChanged(AdjustmentEvent ae) {
    System.out.println("AdjustmentValueChanged to " + ae.getValue());
  }
  









  public void setOppmode(int newOppmode)
  {
    if (newOppmode == currOppmode)
      return;
    currOppmode = newOppmode;
    toolbar.oppmodeChanged(currOppmode);
    disableManager.updateOppModeButts();
  }
  















  public static void runGEDI(OptionParser commandLineOptions)
  {
    selectErrorStream();
    
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        new OCRInterface(OCRInterface.this);



      }
      



    });
    System.gc();
  }
  






















  public void ocrAction() {}
  





















  public static void setCurrent_dir(String current_dir)
  {
    current_dir = current_dir;
  }
  


  public static String getCurrentDicDir()
  {
    return current_dir;
  }
  



  public static void setCurrentImage_dir(String current_image_dir)
  {
    current_image_dir = current_image_dir;
  }
  


  public static String getCurrentImageDir()
  {
    return current_image_dir;
  }
  



  public static void setCurrentXml_dir(String current_xml_dir)
  {
    current_xml_dir = current_xml_dir;
  }
  


  public static String getCurrentXmlDir()
  {
    return current_xml_dir;
  }
  



  public static void setDictionaryName(String dictionary)
  {
    dictionary = dictionary;
  }
  


  public static String getDictionaryName()
  {
    return dictionary;
  }
  



  public static void setImageDirName(String image_dir)
  {
    image_dir = image_dir;
  }
  


  public static String getImageDirName()
  {
    return image_dir;
  }
  



  public static void setXmlDirName(String dir)
  {
    xml_dir = dir;
  }
  


  public static String getXmlDirName()
  {
    return xml_dir;
  }
  


  public class OcrMenuListener
    implements ActionListener, ItemListener
  {
    OCRInterface ocrI;
    

    public OcrMenuListener(OCRInterface parent)
    {
      ocrI = parent;
    }
    
    public void actionPerformed(String com) {
      String command = com;
      saveCommand(command);
      if (command.equals("Load File/Directory"))
      {
        menuListener.actionPerformed(new ActionEvent(
          OCRInterface.this_interface, 
          128, 
          "Save GEDI Config"));
        openImageDir();
      }
    }
    
    public void actionPerformed(ActionEvent e)
    {
      String command = e.getActionCommand();
      
      saveCommand(command);
      if (command.equals("Load File/Directory")) {
        System.out.println("IMAGEOPEN");
        OCRInterface.this.actionImageDirLoad(true);
        multiPagePanel.setVisible(true);
        OCRInterface.this_interface.updateETextWindowContent();
        if (this_interfaceocrTable != null)
          this_interfaceocrTable.setColumnWidths();
      } else if (command.equals("Refresh XML")) {
        actionRefresh();
      } else if (command.equals("Refresh Document List")) {
        update_tables();
        currentHWObjcurr_canvas.reset();
      } else if (command.equals("Full Screen Capture")) {
        OCRInterface.this.screenCapture("FULL");
      } else if (command.equals("Document Capture"))
      {

        int answer = JOptionPane.showConfirmDialog(OCRInterface.this_interface, 
          "Do you want to hide boxes on captured image?", 
          "Document Capture", 
          1, 
          3);
        if ((answer == 2) || 
          (answer == -1))
          return;
        if (answer == 1) {
          OCRInterface.this.screenCapture("_dump");
        } else if (answer == 0) {
          hideBoxes = true;
          OCRInterface.this_interface.getCanvas().repaint();
          OCRInterface.this.screenCapture("_dump");
        }
        hideBoxes = false;
      } else if (command.equals("Merge GEDI Config"))
      {
        OCRInterface.this.actionLoadWorkspace();
        if (this_interfaceocrTable != null) {
          this_interfaceocrTable.setColumnWidths();







        }
        








      }
      else if (command.equals("Save XML As")) {
        OCRInterface.this.actionSaveDocumentAs("Save XML As");
      } else if (command.equals("Build Workspace")) {
        OCRInterface.this.addImageNamesToTable(0);
        
        OCRInterface.acu.setTypeAttributes(getAtts());
        OCRInterface.acu.setSetting_modified(true);
      } else if (command.equals("Save GEDI Config As")) {
        File f = actionSaveFile("xml", "Save GEDI Config As");
        if (f != null)
        {
          LoadDataFile.dumpWorkspace(new TypeSettings(
            tbdPane.data_panel.t_window
            .getSaveableTypeSettings(), OCRInterface.acu
            .getTypeAttributesMap()), null, f
            .getAbsolutePath());
          OCRInterface.acu.setSetting_modified(false);
        }
      } else if (command.equals("Save GEDI Config")) {
        actionSaveWorkspace();
      } else if (command.equals("Save Profile As")) {
        File f = actionSaveFile("properties", "Save Profile As");
        if (f != null) {
          System.out.println("Save GEDI Properties at: " + f.getAbsolutePath());
          Properties props = LoadGEDIProps.getInstance();
          OCRInterface.this.setProperties(props);
          LoadGEDIProps.saveGEDIPropsAt(f.getAbsolutePath());
        }
      } else if (command.equals("Save All")) {
        OCRInterface.this.actionSaveDocument();
        actionSaveWorkspace();
        Properties props = LoadGEDIProps.getInstance();
        OCRInterface.this.setProperties(props);
        LoadGEDIProps.closeGEDIProps();
      } else if (command.equals("Save Document w/ Workspace")) {
        if (OCRInterface.currDoc != null) {
          OCRInterface.currDoc.setTypeSettings(new TypeSettings(
            tbdPane.data_panel.t_window
            .getSaveableTypeSettings(), OCRInterface.acu
            .getTypeAttributesMap()));
          OCRInterface.currDoc.dumpDataAndWorkspace();
          OCRInterface.acu.setSetting_modified(false);
        }
      } else if (command.equals("Full Screen Capture")) {
        OCRInterface.this.screenCapture("FULL");
      } else if (command.equals("Document Capture")) {
        OCRInterface.this.screenCapture("_dump");
      } else if (command.equals("Custom Script...")) {
        System.out.println("Custom Script...");
      } else if (command.equals("Preferences"))
      {
        OCRInterface.acu.setVisible(true);
      }
      else if (command.equals("groundTruthToolbar")) {
        JCheckBoxMenuItem check = (JCheckBoxMenuItem)e.getSource();
        if (check.isSelected()) {
          GTToolbar.setVisible(true);
          int height = (int)GTToolbar.getPreferredSize().getHeight();
          OCRInterface.this_interface.modifyHeightOfRightPanel(-height);
        } else {
          GTToolbar.setVisible(false);
          int height = (int)GTToolbar.getPreferredSize().getHeight();
          OCRInterface.this_interface.modifyHeightOfRightPanel(height);
        }
      } else if (command.equals("polygonTranscriptionToolbar")) {
        JCheckBoxMenuItem check = (JCheckBoxMenuItem)e.getSource();
        if (check.isSelected()) {
          PolyTranscribeToolbar.setVisible(true);
          int height = (int)PolyTranscribeToolbar.getPreferredSize().getHeight();
          OCRInterface.this_interface.modifyHeightOfRightPanel(-height);
        } else {
          PolyTranscribeToolbar.setVisible(false);
          int height = (int)PolyTranscribeToolbar.getPreferredSize().getHeight();
          OCRInterface.this_interface.modifyHeightOfRightPanel(height);
        }
      } else if (command.equals("translateWorkflowToolbar")) {
        JCheckBoxMenuItem check = (JCheckBoxMenuItem)e.getSource();
        if (check.isSelected()) {
          TranslateWorkflowToolbar.setVisible(true);
          int height = (int)TranslateWorkflowToolbar.getPreferredSize().getHeight();
          OCRInterface.this_interface.modifyHeightOfRightPanel(-height);
          ocrI.setEnableTranslateWorkflow(true);
        } else {
          TranslateWorkflowToolbar.setVisible(false);
          int height = (int)TranslateWorkflowToolbar.getPreferredSize().getHeight();
          OCRInterface.this_interface.modifyHeightOfRightPanel(height);
          ocrI.setEnableTranslateWorkflow(false);
        }
      } else if (command.equals("navigationToolbar")) {
        JCheckBoxMenuItem check = (JCheckBoxMenuItem)e.getSource();
        if (check.isSelected()) {
          toolbar.setVisible(true);
          int height = (int)toolbar.getPreferredSize().getHeight();
          OCRInterface.this_interface.modifyHeightOfRightPanel(-height);
        } else {
          toolbar.setVisible(false);
          int height = (int)toolbar.getPreferredSize().getHeight();
          OCRInterface.this_interface.modifyHeightOfRightPanel(height);
        }
      } else if (command.equals("thresholdingToolbar")) {
        JCheckBoxMenuItem check = (JCheckBoxMenuItem)e.getSource();
        thresholdingToolbar.setVisible(check.isSelected());
        setThresholdingToolbarVisible(check.isSelected());
      } else if (e.getActionCommand().equals("mniUndo")) {
        currentHWObjcurr_canvas.clearRLEMap();
        currentHWObjcurr_canvas.undoManager.stepBack();
        currentHWObjcurr_canvas.paintCanvas();
        currentHWObjcurr_canvas.setAllZones();
        OCRInterface.this_interface.getUniqueZoneIdObj().updateMaxZoneId();
      } else if (e.getActionCommand().equals("mniRedo")) {
        currentHWObjcurr_canvas.clearRLEMap();
        currentHWObjcurr_canvas.undoManager.stepForward();
        currentHWObjcurr_canvas.paintCanvas();
        currentHWObjcurr_canvas.setAllZones();
        OCRInterface.this_interface.getUniqueZoneIdObj().updateMaxZoneId();
      } else if (e.getActionCommand().equals("mniCopy")) {
        if (OCRInterface.currOppmode == 20) {
          OCRInterface.this.copyMeasurementsToClipboard();
        } else {
          currentHWObjcurr_canvas.setClipboard();
          currentHWObjcurr_canvas.reset();
        }
      } else if (e.getActionCommand().equals("mniPaste")) {
        try {
          if (this_interfacegetCanvaszvec.size() > 0) {
            getCanvas().saveCurrentState();
          }
          if (OCRInterface.this_interface.getCtrl_C_Copies().trim().equalsIgnoreCase("<ZONE>")) {
            currentHWObjcurr_canvas.pasteZones();
            ImageReaderDrawer.picture.repaint();
            currentHWObjcurr_canvas.setAllZones();
          }
          else {
            String attr = OCRInterface.this_interface.getCtrl_C_Copies();
            for (DLZone z : ImageDisplay.activeZones)
              z.setAttributeValue(attr, currentHWObjcurr_canvas.getClipboard().trim());
          }
        } catch (DLException exc) {
          exc.printStackTrace();
        } } else if (!e.getActionCommand().equals("OPTIONSPANEL"))
      {
        if (e.getActionCommand() == "Exit") {
          OCRInterface.exit_ocr = true;
          OCRInterface.this.closeApplication();
        } else if (command.equals("mniRefersh")) {
          System.out.println("File->Referesh Dictionary");
          LoadDictionary(0);
        } else if (e.getActionCommand() == "Save XML")
        {
          OCRInterface.this.actionSaveDocument();






        }
        else if (e.getActionCommand() == "mniSelect") {
          setOppmode(0);

        }
        else if (e.getActionCommand() == "mniTSelect") {
          setOppmode(1);

        }
        else if (e.getActionCommand() == "Create") {
          setOppmode(2);

        }
        else if (e.getActionCommand() == "Split") {
          setOppmode(7);
        } else if (e.getActionCommand() == "Par/Chld Line") {
          setOppmode(12);
        } else if (command.equals("FitImageWnd")) {
          toolbar.setScaleByIndex(11);
        } else if (command.equals("Fit To Page Width")) {
          toolbar.setScaleByIndex(10);
        } else if (command.equals("400%")) {
          toolbar.setScaleByIndex(0);
        } else if (command.equals("300%")) {
          toolbar.setScaleByIndex(1);
        } else if (command.equals("200%")) {
          toolbar.setScaleByIndex(2);
        } else if (command.equals("150%")) {
          toolbar.setScaleByIndex(3);
        } else if (command.equals("125%")) {
          toolbar.setScaleByIndex(4);
        } else if (command.equals("100%")) {
          toolbar.setScaleByIndex(5);
        } else if (command.equals("75%")) {
          toolbar.setScaleByIndex(6);
        } else if (command.equals("50%")) {
          toolbar.setScaleByIndex(7);
        } else if (command.equals("25%")) {
          toolbar.setScaleByIndex(8);
        } else if (command.equals("12.5%")) {
          toolbar.setScaleByIndex(9);
        } else if (command.equals("mniScan")) {
          System.out.println("Processing->I/O->Scan");




        }
        else if (e.getActionCommand() == "About DL-GEDIPro") {
          new OCRAboutBox(OCRInterface.this_interface);

        }
        else if (e.getActionCommand() == "Shortcut Keys")
        {
          new ShortCutsBox(OCRInterface.this_interface);































        }
        else if (e.getActionCommand() != "Options")
        {
          if (e.getActionCommand() != "Rename")
          {


            if (e.getActionCommand().equals("createXML"))
            {



              saveFilesDialog.createMultipleXML();
              




              OCRInterface.getAttsConfigUtil().reloadCurrentlyOpenedDocument();
              this_interfacetbdPane.setCursor(Cursor.getPredefinedCursor(0));
            }
            else if (e.getActionCommand() == "ASAVE")
            {
              if (jcb.isSelected()) {
                getSaveFilesDialog().setAutoSave(true);
              } else
                getSaveFilesDialog().setAutoSave(false);
            } else if (e.getActionCommand().equals("TEXTRACT")) {
              if (OCRInterface.currDoc != null) {
                MARIAScript mariascript = new MARIAScript(OCRInterface.currDoc);
                mariascript.go();
                OCRInterface.this.afterProcessReload();




              }
              else
              {




                JOptionPane.showMessageDialog(OCRInterface.this_interface, 
                  "Please select a document to process", "Message", 
                  1);
              }
            }
            else if (e.getActionCommand().equals("TEXTPE")) {
              if (OCRInterface.currDoc != null) {
                MARIAchiPEScript mariachipescript = new MARIAchiPEScript(
                  OCRInterface.currDoc.getFileName(), "", new File(OCRInterface.currDoc
                  .getFilePathOnly()));
                mariachipescript.go();
                OCRInterface.this.afterProcessReload();
              } else {
                JOptionPane.showMessageDialog(OCRInterface.this_interface, 
                  "Please select a document to process", "Message", 
                  1);
              }
            }
            else if (e.getActionCommand().equals("Find")) {
              bottomPanel.showFindPanel();
            } else if (e.getActionCommand().equals("Select all zones")) {
              this_interfacetbdPane.data_panel.t_window.clear(true);
              Vector<DLZone> allExistingZoneVector = currentHWObjcurr_canvas.getShapeVec().getAsVector();
              currentHWObjcurr_canvas.clearActiveZones();
              for (DLZone zone : allExistingZoneVector)
                currentHWObjcurr_canvas.addToSelected((Zone)zone);
              OCRInterface.this_interface.getCanvas().paintCanvas();
              this_interfacetbdPane.data_panel.a_window.showZoneInfo(ImageDisplay.getActiveZones());
            } else if (e.getActionCommand().equals("Create/Edit Reading Order")) {
              setOppmode(17);
            } else if (e.getActionCommand().equals("GEDI Help")) {
              DesktopUtil desktop = new DesktopUtil();
              
              desktop.launchHelp();
            } else if (e.getActionCommand().equals("InfoWindow")) {
              InfoLabel info = new InfoLabel(true);
              info.showInfoPanel();
            } else if (e.getActionCommand().equals("Load GEDI Config")) {
              System.out.println("Load GEDI Config");
              JFileChooser chooser = new JFileChooser(new File(
                LoadUserProps.getInstance().getProperty("configPath")));
              chooser.setDialogTitle("Load GEDI Config");
              chooser.setFileSelectionMode(0);
              chooser.addChoosableFileFilter(
                new javax.swing.filechooser.FileFilter()
                {
                  public boolean accept(File f)
                  {
                    return (f.isDirectory()) || (f.getName().endsWith(".xml")) || (f.getName().endsWith(".XML"));
                  }
                  
                  public String getDescription() { return "XML Files (*.xml, *.XML)";
                  }
                  

                });
              int returnVal = chooser.showOpenDialog(getRootPane());
              if (returnVal == 0) {
                try {
                  loadConfigFile(chooser.getSelectedFile().getCanonicalPath());
                  OCRInterface.getAttsConfigUtil().reloadCurrentlyOpenedDocument();
                } catch (IOException ioe) {
                  ((IOException)ioe).printStackTrace();
                }
              }
            } else if (e.getActionCommand().equals("Generate or remove RLEIMAGE")) {
              setCursor(Cursor.getPredefinedCursor(3));
              if ((ImageDisplay.activeZones == null) || 
                (ImageDisplay.activeZones.isEmpty()))
              {
                JOptionPane.showMessageDialog(
                  OCRInterface.this_interface, 
                  "Select zone(s) to generate or remove RLEIMAGE", 
                  "Generate RLE", 
                  1);
              } else {
                boolean createRLE = false;
                



                for (ioe = ImageDisplay.activeZones.iterator(); ((Iterator)ioe).hasNext();) { Zone zone = (Zone)((Iterator)ioe).next();
                  if (!zone.isGroup())
                  {
                    String rleImage = (String)zone.getZoneTags().get("RLEIMAGE");
                    if ((rleImage == null) || (rleImage.isEmpty())) {
                      createRLE = true;
                      break;
                    }
                  } }
                if (createRLE) {
                  for (ioe = ImageDisplay.activeZones.iterator(); ((Iterator)ioe).hasNext();) { Zone zone = (Zone)((Iterator)ioe).next();
                    currentHWObjcurr_canvas.createRLE_CC(zone, 
                      true, false, false);
                  }
                } else {
                  for (ioe = ImageDisplay.activeZones.iterator(); ((Iterator)ioe).hasNext();) { Zone zone = (Zone)((Iterator)ioe).next();
                    zone.getZoneTags().put("RLEIMAGE", "");
                    currentHWObjcurr_canvas.clearRLEMap();
                  }
                }
              }
              
              currentHWObjcurr_canvas.paintCanvas();
              tbdPane.data_panel.a_window.showZoneInfo(ImageDisplay.activeZones);
              setOppmode(0);
              setCursor(Cursor.getPredefinedCursor(0));
            } else if (e.getActionCommand().equals("Shrink zone"))
            {
              setCursor(Cursor.getPredefinedCursor(3));
              if ((ImageDisplay.activeZones == null) || 
                (ImageDisplay.activeZones.isEmpty()))
              {
                JOptionPane.showMessageDialog(
                  OCRInterface.this_interface, 
                  "Select zone(s) to shrink.\n\nNOTE: use CTRL-A to select all zones.", 
                  
                  "Shrink zone(s)", 
                  1);
              }
              else {
                Vector<DLZone> selectedZones = new Vector(ImageDisplay.activeZones.size());
                selectedZones.addAll(ImageDisplay.activeZones);
                shrinkZones(selectedZones, false);
                
                currentHWObjcurr_canvas.paintCanvas();
                tbdPane.data_panel.a_window.showZoneInfo(ImageDisplay.activeZones);
                if (!toolbar.getStickyMode())
                  setOppmode(0);
              }
              setCursor(Cursor.getPredefinedCursor(0));

            }
            else if (e.getActionCommand().equals("Split zone at offsets")) {
              System.out.println("split at offsets");
              if ((ImageDisplay.activeZones == null) || 
                (ImageDisplay.activeZones.isEmpty()))
              {
                JOptionPane.showMessageDialog(
                  OCRInterface.this_interface, 
                  "Select zone(s) to spliy.\n\nNOTE: use CTRL-A to select all zones.", 
                  
                  "Split zone(s)", 
                  1);
              }
              else {
                OCRInterface.this_interface.getCanvas().saveCurrentState();
                OCRInterface.this.splitByOffsets();
                tbdPane.data_panel.a_window.showZoneInfo(ImageDisplay.activeZones);
                setOppmode(0);
              }
            }
            else if (e.getActionCommand().equals("Remove overlap")) {
              System.out.println("remove overlap");
              if ((ImageDisplay.activeZones == null) || 
                (ImageDisplay.activeZones.isEmpty()) || 
                (ImageDisplay.activeZones.size() != 2))
              {
                JOptionPane.showMessageDialog(
                  OCRInterface.this_interface, 
                  "Select two overlapping zones.", 
                  "Remove overlap", 
                  1);
                return;
              }
              OCRInterface.this_interface.getCanvas().saveCurrentState();
              OCRInterface.this.removeOverlap((Zone)ImageDisplay.activeZones.get(0), (Zone)ImageDisplay.activeZones.get(1));
              tbdPane.data_panel.a_window.showZoneInfo(ImageDisplay.activeZones);
              setOppmode(0);

















            }
            else if (e.getActionCommand().equals("Merge zones")) {
              if ((ImageDisplay.activeZones == null) || 
                (ImageDisplay.activeZones.isEmpty()) || 
                (ImageDisplay.activeZones.size() == 1))
              {
                JOptionPane.showMessageDialog(
                  OCRInterface.this_interface, 
                  "Select two or more zones to merge.\n\nNOTE: hotkey CTRL-M to merge.", 
                  
                  "Merge zones", 
                  1);
              }
              else {
                OCRInterface.this_interface.getCanvas().saveCurrentState();
                


































                if (!OCRInterface.this_interface.getMergeZonesToBox()) {
                  int blackPixelValue = currentHWObjcurr_canvas.getBlackPixelValue();
                  float scale = currentHWObjcurr_canvas.getScale();
                  Object selectedZones = new Vector(ImageDisplay.activeZones.size());
                  ((Vector)selectedZones).addAll(ImageDisplay.activeZones);
                  new RLE_CC_Handler(blackPixelValue, (Vector)selectedZones, scale, true);


                }
                else
                {

                  Zone zone0 = (Zone)ImageDisplay.activeZones.remove(0);
                  Zone zone1 = (Zone)ImageDisplay.activeZones.remove(0);
                  Zone merged = OCRInterface.this.mergeZones(OCRInterface.this_interface.getCanvas()
                    .mergeZones(zone0, zone1));
                  ImageDisplay.activeZones.add(merged);
                }
                
              }
            }
            else if (e.getActionCommand().equals("Close Document")) {
              ocrTable.closeDocument();
            } else {
              System.out.println("unknown menu command:" + command + 
                " [OCRInterface:3637]");
            }
          }
        }
      }
    }
    
    public void itemStateChanged(ItemEvent arg0) {}
  }
  
  private Zone mergeZones(Zone merged) {
    if (ImageDisplay.activeZones.size() == 0) {
      return merged;
    }
    Zone zone0 = (Zone)ImageDisplay.activeZones.remove(0);
    Zone _merged = this_interface.getCanvas().mergeZones(merged, zone0);
    return mergeZones(_merged);
  }
  








  public PropertiesInfoHolder getWorkModeProps(int i)
  {
    return workmodeProps[i];
  }
  






  public void setCurrentOppMode(int newOppMode)
  {
    currOppmode = newOppMode;
    toolbar.oppmodeChanged(currOppmode);
  }
  






  public boolean isOCRMode()
  {
    return currWorkmode == 0;
  }
  




  public float getCurrentImageScale()
  {
    return currentHWObjcurr_canvas.getScale();
  }
  

























  public ImageDisplay getCanvas()
  {
    return currentHWObjcurr_canvas;
  }
  
  public void setChronologicalFocus()
  {
    Vector<Component> ListOfFocusableComponents = new Vector();
    
    System.out.println("count =" + getComponents().length);
    
    for (int index = 0; index < getComponentCount(); index++) {
      JComponent comp = (JComponent)getComponent(index);
      
      if (((comp instanceof JComponent)) && (comp.isEnabled())) {
        if ((comp instanceof JTextComponent)) {
          if (((JTextComponent)comp).isEditable()) {
            ListOfFocusableComponents.add(comp);
          }
        } else {
          ListOfFocusableComponents.add(comp);
        }
        setChronologicalFocus(comp, ListOfFocusableComponents);
      }
    }
    
    for (int index = 0; index < ListOfFocusableComponents.size(); index++) {
      JComponent current = 
        (JComponent)ListOfFocusableComponents.get(index);
      JComponent next;
      JComponent next;
      if (index == ListOfFocusableComponents.size() - 1) {
        next = (JComponent)ListOfFocusableComponents.get(0);
      } else {
        next = (JComponent)ListOfFocusableComponents.get(index + 1);
      }
      current.setNextFocusableComponent(next);
    }
  }
  

  private void setChronologicalFocus(JComponent comp, Vector<Component> list)
  {
    if (comp.getComponentCount() > 0) {
      for (int index = 0; index < comp.getComponentCount(); index++) {
        if ((comp.getComponent(index) instanceof JComponent)) {
          JComponent child = (JComponent)comp.getComponent(index);
          

          if (((child instanceof JComponent)) && (child.isEnabled())) {
            if ((child instanceof JTextComponent)) {
              if (((JTextComponent)child).isEditable()) {
                list.add(child);
              }
            } else {
              list.add(child);
            }
            setChronologicalFocus(child, list);
          }
        }
      }
    }
  }
  
  public class MyOwnFocusTraversalPolicy extends java.awt.FocusTraversalPolicy
  {
    Vector<Component> list;
    
    public MyOwnFocusTraversalPolicy()
    {
      System.out.println("List size =" + list.size());
    }
    




























    public Component getComponentAfter(Container arg0, Component arg1)
    {
      if (list.size() == 0)
        return OCRInterface.this_interface;
      int i = list.indexOf(arg0);
      System.out.println("I = " + i);
      if (i == -1) {
        return OCRInterface.this_interface;
      }
      return i == list.size() - 1 ? (Component)list.firstElement() : (Component)list.get(i + 1);
    }
    
    public Component getComponentBefore(Container arg0, Component arg1)
    {
      if (list.size() == 0)
        return OCRInterface.this_interface;
      int i = list.indexOf(arg0);
      System.out.println("I = " + i);
      if (i == -1)
        return OCRInterface.this_interface;
      return i == 0 ? (Component)list.lastElement() : (Component)list.get(i + 1);
    }
    

    public Component getFirstComponent(Container arg0)
    {
      return (Component)list.firstElement();
    }
    

    public Component getLastComponent(Container arg0)
    {
      return (Component)list.lastElement();
    }
    

    public Component getDefaultComponent(Container arg0)
    {
      return (Component)list.firstElement();
    }
  }
  
  private void actionSaveDocument() {
    if (currDoc == null) {
      if (currentHWObjcurr_canvas.shapeVec.size() == 0) {
        JOptionPane.showMessageDialog(this_interface, 
          "There are no data to be saved.\n\nIf you want to create empty XML, please proceed to \"File --> Auto-Create XML.\" ", 
          "Save XML", 
          1);
      } else {
        saveFilesDialog.saveData();
      }
    } else {
      saveFilesDialog.saveData();
    }
  }
  
  public void actionSaveWorkspace() {
    if (config_file.endsWith("There is no Config File (will be created in Xml Directory)")) {
      if ((getCurrentXmlDir().trim().isEmpty()) && (getXmlDirName().trim().isEmpty()))
        return;
      config_file = 
      

        getCurrentXmlDir() + getXmlDirName() + File.separator + "GEDIConfig" + ".XML";
    }
    
    File a = new File(config_file);
    config_file = a.getAbsolutePath();
    
    if (!a.exists())
    {


      final JOptionPane optionPane = new JOptionPane(
        "The config file: " + config_file + 
        "\n does not exist.  Create it?", 
        3, 0);
      final JDialog dialog = new JDialog(this_interface, 
        "Save Workspace", true);
      setDialog(dialog);
      setDialog(dialog);
      dialog.setContentPane(optionPane);
      
      dialog.setDefaultCloseOperation(0);
      optionPane
        .addPropertyChangeListener(new PropertyChangeListener() {
          public void propertyChange(PropertyChangeEvent e) {
            String prop = e.getPropertyName();
            if ((dialog.isVisible()) && 
              (e.getSource() == optionPane))
            {
              if (prop.equals("value")) {
                dialog.setVisible(false);
              }
            }
          }
        });
      Dimension screenSize = this_interface.getSize();
      dialog.setLocation(width / 2 - 400, 
        height / 2 - 200);
      
      dialog.pack();
      acu.toBack();
      dialog.setVisible(true);
      
      int value = ((Integer)optionPane.getValue()).intValue();
      
      int response = value;
      if (response == 0)
      {
        LoadDataFile.dumpWorkspace(new TypeSettings(
          tbdPane.data_panel.t_window
          .getSaveableTypeSettings(), acu
          .getTypeAttributesMap()), null, 
          config_file);
      } else if (response == 1) {
        config_file = "There is no Config File (will be created in Xml Directory)";
      }
      
      acu.toFront();


    }
    else
    {


      LoadDataFile.dumpWorkspace(new TypeSettings(
        tbdPane.data_panel.t_window.getSaveableTypeSettings(), 
        acu.getTypeAttributesMap()), null, config_file);
    }
    
    acu.setSetting_modified(false);
  }
  
  private void actionSaveDocumentAs(String action)
  {
    String imagePath = ImageReaderDrawer.getFile_path();
    if (currDoc == null)
    {




      if (this_interfaceocrTable.getSelectedRow() == -1) {
        return;
      }
      
      String imageStr = this_interfaceocrTable.getSelectedImageName();
      
      imageStr = imageStr.substring(0, imageStr.lastIndexOf('.'));
      String dataPath = getCurrentXmlDir() + 
        getXmlDirName() + 
        File.separator + 
        imageStr + 
        ".xml";
      saveFilesDialog.createXML(dataPath, imagePath);
    }
    
    File f = actionSaveFile("xml", action);
    if (f != null) {
      currDoc.dumpDataAs(f.getAbsolutePath());
      

      String imageExtension = imagePath.substring(imagePath.lastIndexOf('.') + 1, 
        imagePath.length());
      
      String newImagePath = f.getAbsolutePath();
      newImagePath = newImagePath.substring(0, newImagePath.lastIndexOf('.')) + '.' + imageExtension;
      File newImageFile = new File(newImagePath);
      
      FileInputStream fis = null;
      FileOutputStream fos = null;
      try
      {
        fis = new FileInputStream(imagePath);
        fos = new FileOutputStream(newImageFile);
        
        int numBytes = fis.available();
        



        for (int currByte = 0; currByte < numBytes; currByte++) {
          int byteRead = fis.read();
          fos.write(byteRead);
        }
        fis.close();
        fos.close();
      } catch (FileNotFoundException ex) {
        ex.getMessage();
      } catch (IOException ioe) {
        ioe.getMessage();
      }
      this_interface.updateCurrFilename();
      fclose_cancel_selected = false;
    }
  }
  



  private void actionLoadWorkspace()
  {
    final JOptionPane optionPane = new JOptionPane(
      "Merging workspaces is not reversable.\nMerged workspace will be saved in the current Xml Directory.", 
      

      3, 2);
    
    final JDialog dialog = new JDialog(this_interface, 
      "Warning", true);
    setDialog(dialog);
    setDialog(dialog);
    dialog.setContentPane(optionPane);
    
    dialog.setDefaultCloseOperation(0);
    optionPane.addPropertyChangeListener(new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName();
        
        if ((dialog.isVisible()) && (e.getSource() == optionPane) && 
          (prop.equals("value"))) {
          dialog.setVisible(false);
        }
        
      }
      
    });
    dialog.setLocation(new Point(400, 300));
    dialog.pack();
    dialog.setVisible(true);
    int value = ((Integer)optionPane.getValue()).intValue();
    
    int response = value;
    
    if (response == 0)
    {

      menuListener.actionPerformed(new ActionEvent(
        this_interface, 
        128, 
        "Save GEDI Config"));
      
      LoadDictionary(0);
      File f = actionLoadFile();
      Map<String, Object[]> temp = new LinkedHashMap();
      
      if (f != null) {
        LoadDataFile ld = LoadDataFile.loadWorkspace(f
          .getAbsolutePath());
        
        if (ld.getTypeSettings() != null)
        {

          Iterator localIterator1 = ld.getTypeSettings().toMap().entrySet().iterator();
          while (localIterator1.hasNext()) {
            Map.Entry<String, Map<String, TypeAttributeEntry>> a = (Map.Entry)localIterator1.next();
            if (acu.getTypeAttributesMap().containsKey(a.getKey())) {
              t = (Map)a.getValue();
              
              Iterator localIterator2 = t.entrySet().iterator();
              while (localIterator2.hasNext()) {
                Map.Entry<String, TypeAttributeEntry> z = (Map.Entry)localIterator2.next();
                ((Map)acu.getTypeAttributesMap().get(a.getKey())).put(
                  (String)z.getKey(), (TypeAttributeEntry)z.getValue());
              }
            } else { acu.getTypeAttributesMap().put((String)a.getKey(), 
                (Map)a.getValue());
            }
          }
          
          localIterator1 = ld.getTypeSettings().toTypeWindow().entrySet().iterator();
          while (localIterator1.hasNext()) {
            Map.Entry<String, Object[]> b = (Map.Entry)localIterator1.next();
            temp.put((String)b.getKey(), (Object[])b.getValue());
          }
          


          localIterator1 = tbdPane.data_panel.t_window.getTypeSettings().entrySet().iterator();
          while (localIterator1.hasNext()) {
            Map.Entry<String, Object[]> c = (Map.Entry)localIterator1.next();
            temp.put((String)c.getKey(), (Object[])c.getValue());
          }
          

          Map<String, Attribute> m = new LinkedHashMap();
          
          acu.setVisible(true);
          

          Map<String, TypeAttributeEntry> t = acu.getTypeAttributesMap().entrySet().iterator();
          while (t.hasNext()) {
            Object tae = (Map.Entry)t.next();
            
            Map<String, Attribute> m2 = new LinkedHashMap();
            Object m3 = new LinkedHashMap();
            Iterator localIterator3; if (((String)((Map.Entry)tae).getKey()).equals("DL_PAGE"))
            {
              localIterator3 = ((Map)((Map.Entry)tae).getValue()).entrySet().iterator();
              Map.Entry<String, TypeAttributeEntry> tmp;
              int i;
              for (; localIterator3.hasNext(); 
                  

                  i < ((TypeAttributeEntry)tmp.getValue()).getHotKey().size())
              {
                tmp = (Map.Entry)localIterator3.next();
                i = 0; continue;
                
                if (((TypeAttributeEntry)tmp.getValue()).getHotKey().elementAt(
                  i) != null)
                {
                  if (!((String)((TypeAttributeEntry)tmp.getValue()).getHotKey().elementAt(i)).equals("None"))
                  {
                    if (!((String)((TypeAttributeEntry)tmp.getValue()).getHotKey().elementAt(i)).equals("none")) {
                      Attribute a = new Attribute(
                        (String)tmp.getKey(), 
                        
                        (String)((TypeAttributeEntry)tmp.getValue()).getHotKey().elementAt(i));
                      m.put(String.valueOf(i), a);
                    } }
                }
                AttributeConfigUtil.attsHotKeyList.put((String)((Map.Entry)tae).getKey(), m);i++;
              }
            }
            else if (!((String)((Map.Entry)tae).getKey()).equals("DL_TEXTLINEGT"))
            {

              localIterator3 = ((Map)((Map.Entry)tae).getValue()).entrySet().iterator();
              while (localIterator3.hasNext()) {
                Map.Entry<String, TypeAttributeEntry> tmp = (Map.Entry)localIterator3.next();
                for (int i = 0; 
                    i < ((TypeAttributeEntry)tmp.getValue()).getHotKey().size(); i++) {
                  if (((TypeAttributeEntry)tmp.getValue()).getHotKey().elementAt(
                    i) != null)
                  {
                    if (!((String)((TypeAttributeEntry)tmp.getValue()).getHotKey().elementAt(i)).equals("None"))
                    {
                      if (!((String)((TypeAttributeEntry)tmp.getValue()).getHotKey().elementAt(i)).equals(
                        "none"))
                      {
                        if (!((String)((TypeAttributeEntry)tmp.getValue()).getHotKey().elementAt(i)).equals("")) {
                          Attribute a = new Attribute(
                            (String)tmp.getKey(), 
                            
                            (String)((TypeAttributeEntry)tmp.getValue()).getHotKey().elementAt(i));
                          m2.put(String.valueOf(i), a);
                        } } }
                  }
                }
                AttributeConfigUtil.attsHotKeyList.put((String)((Map.Entry)tae).getKey(), m2);
              }
            }
            else
            {
              localIterator3 = ((Map)((Map.Entry)tae).getValue()).entrySet().iterator();
              Map.Entry<String, TypeAttributeEntry> tmp;
              int i;
              for (; localIterator3.hasNext(); 
                  

                  i < ((TypeAttributeEntry)tmp.getValue()).getHotKey().size())
              {
                tmp = (Map.Entry)localIterator3.next();
                i = 0; continue;
                
                if (((TypeAttributeEntry)tmp.getValue()).getHotKey().elementAt(
                  i) != null)
                {
                  if (!((String)((TypeAttributeEntry)tmp.getValue()).getHotKey().elementAt(i)).equals("none"))
                  {
                    if (!((String)((TypeAttributeEntry)tmp.getValue()).getHotKey().elementAt(i)).equals(
                      "None"))
                    {
                      if (!((String)((TypeAttributeEntry)tmp.getValue()).getHotKey().elementAt(i)).equals("")) {
                        Attribute a = new Attribute(
                          (String)tmp.getKey(), 
                          
                          (String)((TypeAttributeEntry)tmp.getValue()).getHotKey().elementAt(i));
                        ((Map)m3).put(String.valueOf(i), a);
                      }
                    }
                  }
                }
                i++;
              }
              

















              AttributeConfigUtil.attsHotKeyList.put((String)((Map.Entry)tae).getKey(), m3);
            }
          }
          

          acu.applyPageAtts();
          
          acu.setVisible(false);
          
          LoadDataFile.dumpWorkspace(new TypeSettings(temp, acu
            .getTypeAttributesMap()), null, config_file);
          

          t = acu.getTypeSettings().entrySet().iterator();
          while (t.hasNext()) {
            Object c = (Map.Entry)t.next();
            tbdPane.data_panel.t_window.getTypeSettings().put(
              (String)((Map.Entry)c).getKey(), (Object[])((Map.Entry)c).getValue());
          }
          
          tbdPane.data_panel.t_window.loadWorkSpace(temp);
          tbdPane.data_panel.t_window.setTypeSettings(temp);
          temp.remove("DL_PAGE");
          
          acu.setSetting_modified(false);
          
          currentHWObjcurr_canvas.reset();
          

          currDoc = null;
          if (tbdPane != null)
            tbdPane.data_panel.a_window.setPage(null);
          if (tbdPane.data_panel.dataTable != null)
            tbdPane.data_panel.dataTable.myClearSelection();
          toolbar.setScaleByIndex(10);
          currentHWObj.setImageFile("images/mainscreen.jpg");
          

          JOptionPane.showMessageDialog(this_interface, 
            "Page/Type Attributes successfully merged.", 
            "Import Workspace", -1);
        }
        else {
          JOptionPane.showMessageDialog(this_interface, config_file + 
            " does not contain a valid configuration file.", 
            "Load Error: ", 0);
          config_file = "config/DLGEDI_CONFG.XML";
        }
      }
    }
  }
  






  public void actionRefresh()
  {
    getAttsConfigUtil().reloadCurrentlyOpenedDocument();
    currentHWObjcurr_canvas.paintCanvas();
  }
  
  private void actionImageDirLoad(boolean openImgDir) {
    this_interfacesaveFilesDialog.saveData();
    
    menuListener.actionPerformed(new ActionEvent(
      this_interface, 
      128, "Save GEDI Config"));
    
    if (openImgDir) {
      openImageDir();
    }
    

    if (!openImgDir)
    {
      this_interfaceocrTable.processSelectionEvent(
        WorkmodeTable.selRow, 
        WorkmodeTable.selCol, true);
    }
  }
  










  File actionSaveFile(String fileType, String action)
  {
    String str = fileType;
    
    File file = null;
    JFileChooser fc = new JFileChooser();
    dialog_open = true;
    fc.setDialogTitle(action);
    setJChoose(fc);
    while ((file == null) || (!file.getName().endsWith(str.toLowerCase())) || (!
      file.getName().endsWith(str.toUpperCase()))) {
      if (str.equals("xml")) {
        fc.addChoosableFileFilter(XMLFileFilter);
      } else if (str.equals("bmp")) {
        fc.addChoosableFileFilter(BMPFileFilter);
      }
      fc.setFileFilter(fc.getAcceptAllFileFilter());
      fc.setCurrentDirectory(new File("."));
      

      if (str.equals("bmp")) {
        String _fileName = ImageReaderDrawer.getFile_name();
        StringBuffer fileName = new StringBuffer(_fileName);
        fileName.replace(fileName.length() - 4, fileName.length(), 
          "_dump.bmp");
        fc.setSelectedFile(new File(fileName.toString()));
      }
      else {
        fc.setSelectedFile(new File("." + str.toLowerCase()));
      }
      
      int result = fc.showSaveDialog(this_interface);
      
      if (result == 1) {
        dialog_open = false;
        return null; }
      if ((result == 0) && 
        (fc.getSelectedFile().getName().endsWith(str.toLowerCase()))) {
        file = fc.getSelectedFile();
        if (file.exists()) {
          int response = JOptionPane.showConfirmDialog(null, 
            "Overwrite existing file?", "Confirm Overwrite", 
            
            2, 
            3);
          if (response == 2) {
            dialog_open = false;
            return null;
          }
        }
        dialog_open = false;
        return file;
      }
      








      JOptionPane.showConfirmDialog(null, 
        "Must have ." + str.toLowerCase() + " or ." + str.toUpperCase() + " file extension.", 
        "Invalid Filename", -1);
    }
    dialog_open = false;
    return null;
  }
  























































  File actionLoadFile()
  {
    File file = null;
    JFileChooser fc = new JFileChooser();
    dialog_open = true;
    setJChoose(fc);
    fc.addChoosableFileFilter(XMLFileFilter);
    
    fc.setFileFilter(fc.getAcceptAllFileFilter());
    
    fc.setCurrentDirectory(new File("."));
    

    fc.setSelectedFile(new File(config_file));
    

    int result = fc.showOpenDialog(this_interface);
    
    if (result == 1)
      return null;
    if (result == 0) {
      file = fc.getSelectedFile();
      dialog_open = false;
      return file;
    }
    dialog_open = false;
    return null;
  }
  
  public static AttributeConfigUtil getAttsConfigUtil()
  {
    return acu;
  }
  








  public void panelToggle()
  {
    if (!thumbSelected) {
      selectThumbnailPanel();
    } else {
      selectImagePanel();
    }
  }
  


  public void selectThumbnailPanel()
  {
    thumbSelected = true;
    toolbar.thumbToggle_but
      .setIcon(Login.localOrInJar("images/singleImage.gif", getClass().getClassLoader()));
    gotoPanel("Thumb Panel");
    pane.grabFocus();
  }
  



  public void selectImagePanel()
  {
    thumbSelected = false;
    toolbar.thumbToggle_but.setIcon(Login.localOrInJar("images/thumbnail.gif", getClass().getClassLoader()));
    gotoPanel("Image Panel");
    currentHWObj.grabFocus();
  }
  










  public void gotoPanel(String go)
  {
    if (go.equals("Image Panel")) {
      if (rightSplitPanel.getTopComponent() == thumb) {
        rightSplitPanel.remove(thumb);
        rightSplitPanel.add(right);
      }
      
    }
    else if (rightSplitPanel.getTopComponent() == right) {
      rightSplitPanel.remove(right);
      rightSplitPanel.add(thumb);
    }
    

    mainPane.validate();
    mainPane.revalidate();
    rightSplitPanel.validate();
    rightSplitPanel.revalidate();
    rightSplitPanel.repaint();
  }
  





  public void enableImageSave(boolean enable)
  {
    mniFileSaveRotatedImage.setEnabled(enable);
  }
  




  public void enableRotate(boolean enable)
  {
    subMnuRot.setEnabled(enable);
  }
  



































  public String hasMeta(String filename)
  {
    String xml = ".xml";
    
    String noExtension = new File(filename).getName();
    


    noExtension = this_interface.getFileNameWithoutExt(noExtension);
    
    File xmlfile = new File(getCurrentXmlDir() + 
      getXmlDirName() + File.separator + 
      noExtension + xml);
    if (!xmlfile.exists()) {
      xml = ".XML";
      
      xmlfile = new File(getCurrentXmlDir() + 
        getXmlDirName() + File.separator + 
        noExtension + xml);
    }
    
    if ((xmlfile.exists()) && (xmlfile.isFile())) {
      try {
        return xmlfile.getCanonicalFile().getCanonicalPath();
      } catch (IOException e) {
        System.out.println("OCRInterface. hasMeta(). Canonical file getting problem.");
        return null;
      }
    }
    return null;
  }
  





  public boolean isXmlWriteable(String xmlFilename)
  {
    File xmlfile = new File(xmlFilename);
    
    if ((xmlfile.exists()) && (xmlfile.canWrite())) {
      return true;
    }
    try {
      if (xmlfile.createNewFile()) {
        xmlfile.delete();
        return true;
      }
    }
    catch (IOException localIOException) {}
    
    return false;
  }
  












  public boolean isXMLValid(String xmlFilePath)
  {
    LoadDataFile ldf = new LoadDataFile();
    
    if (xmlFilePath != null) {
      String errorMessage = ldf.isFileValid(new File(xmlFilePath));
      if (errorMessage != null) {
        setErrorMessage(errorMessage);
        return false;
      }
      
      setErrorMessage(null);
      return true;
    }
    

    setErrorMessage(null);
    return true;
  }
  
  public SaveFilesDialog getSaveFilesDialog()
  {
    return saveFilesDialog;
  }
  
  public int getCurrLineGTMode() {
    return currLineGTMode;
  }
  
  public void setCurrLineGTMode(int in) {
    currLineGTMode = in;
    getCanvas().paintCanvas();
  }
  
  public static void setVec(Vector<ParChldLine> arr) {
    vec = new Vector(arr);
  }
  
  public static Vector<ParChldLine> getVec() {
    return vec;
  }
  
  public void saveTypes(Set<String> types)
  {
    zoneTypes = types;
  }
  
  public Set<String> getTypes() {
    return zoneTypes;
  }
  
  private String str = new String();
  
  public void saveCommand(String s) {
    str = s;
  }
  
  public String getCommand() {
    return str;
  }
  
  public void saveAtts(Map<String, Map<String, TypeAttributeEntry>> var)
  {
    if (!getCommand().equals("Build Workspace"))
      atts = var;
  }
  
  public Map<String, Map<String, TypeAttributeEntry>> getAtts() {
    return atts;
  }
  
  public static LoadDataFile getCurrDoc() {
    return currDoc;
  }
  
  public static void setDialog(JDialog dialog) {
    jd = dialog;
  }
  
  public JDialog getDialog() {
    return jd;
  }
  
  public static void setJOptP(JOptionPane joptp) {
    jop = joptp;
  }
  
  public JOptionPane getJOptP() {
    return jop;
  }
  
  public static void setJChoose(JFileChooser chooser) {
    jc = chooser;
  }
  
  public JFileChooser getJChoose() {
    return jc;
  }
  
  private static TreeMap<String, Integer> buildlineGTModeMap() {
    TreeMap<String, Integer> tm = new TreeMap();
    tm.put("character", new Integer(2));
    tm.put("word", new Integer(3));
    tm.put("line", new Integer(4));
    tm.put("", new Integer(3));
    return tm;
  }
  
  public void setShortCutKeys() {
    acu.setVisibleHelper(true);
    Vector<String> fkeys = new Vector();
    fkeys.add("F1");
    fkeys.add("F2");
    fkeys.add("F3");
    fkeys.add("F4");
    fkeys.add("F5");
    fkeys.add("F6");
    fkeys.add("F7");
    fkeys.add("F8");
    fkeys.add("F9");
    fkeys.add("F10");
    fkeys.add("F11");
    fkeys.add("F12");
    Vector<String> vec = new Vector();
    for (int i = 0; i < fkeys.size(); i++)
    {
      if (!((String)acu.getFunctionKeyBindings().get(fkeys.elementAt(i))).equals("None")) {
        vec.add((String)fkeys.elementAt(i));
      }
    }
    for (String str : vec) {
      fkeys.remove(str);
    }
    
    acu.setFuncKeySpace(fkeys);
    acu.setSetting_modified(false);
    currentHWObjcurr_canvas.reset();
    AttributeConfigUtil.attsHotKeyList.clear();
    acu.loadAttsHotKeyList();
  }
  
  public OcrMenuListener getML() {
    return menuListener;
  }
  









  private void afterProcessReload()
  {
    LoadDictionary(0);
  }
  
  public void saveCurrentPageState() {
    currentHWObjcurr_canvas.saveCurrentState();
  }
  
  public void updateETextWindow()
  {
    tbdPane.data_panel.a_window.setETextWindowZones(currentHWObjcurr_canvas.getShapeVec()
      .getAsVector(), 
      ImageDisplay.activeZones);
  }
  
  public String getETextWindowSelectedText() {
    return tbdPane.data_panel.a_window.getETextWindowSelectedText();
  }
  

  public File processImgTypeFile(String filename)
  {
    File tempFile = new File(getCurrentImageDir() + "/" + 
      getImageDirName() + "/" + filename);
    if (tempFile.exists())
    {


      return tempFile;
    }
    
    return null;
  }
  





























  private void screenCapture(String type)
  {
    try
    {
      Robot rbt = new Robot();
      Toolkit.getDefaultToolkit();
      Dimension dim = null;
      StringBuffer outFile = null;
      int x = 0;int y = 0;
      int width = 0;int height = 0;
      
      BufferedImage img = null;
      
      if (type == "FULL")
      {
        dim = this_interface.getPreferredSize();
        x = 0;
        y = 0;
        width = this_interface.getWidth();
        height = this_interface.getHeight();
        
        img = rbt.createScreenCapture(new Rectangle(x, y, width, height));

      }
      else if (type == "_dump")
      {
        dim = ImageReaderDrawer.picture.getSize();
        
        img = new BufferedImage(width, height, 1);
        ImageReaderDrawer.picture.paint(img.getGraphics());
      }
      

      outFile = new StringBuffer(this_interface.fullPath());
      outFile.delete(outFile.length() - 4, outFile.length()).append(type + screenCaptureType);
      
      if ((img != null) && (getDocumentCaptureToDefaultFile())) {
        File f = new File(outFile.toString());
        ImageIO.write(img, screenCaptureType.replace(".", ""), f);
        JOptionPane.showMessageDialog(null, "Captured document has been saved as " + f.getName());
      }
      else if ((img != null) && (!getDocumentCaptureToDefaultFile())) {
        File f = actionSaveFile("bmp", "Save captured document as");
        if (f != null) {
          ImageIO.write(img, screenCaptureType.replace(".", ""), f);
          JOptionPane.showMessageDialog(null, "Captured document has been saved as " + f.getName());
        }
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  private String fullPath() {
    String imgPath = getCurrentXmlDir() + getXmlDirName();
    String filename = currDoc.getFileName();
    return imgPath + File.separator + filename;
  }
  
  public void clearETextSel() {
    this_interfacetbdPane.data_panel.a_window.clearETextSelection();
  }
  
  public int numETextLines()
  {
    return this_interfacetbdPane.data_panel.a_window.getNumETextLines();
  }
  
  public UniqueZoneId getUniqueZoneIdObj() {
    return uniqueZoneId;
  }
  
  public void runNetworkListener() {
    networkListener = new NetworkListener();
    networkListener.start();
  }
  
  public NetworkListener getNetworkListener() {
    return networkListener;
  }
  
  public void imageDirLoadFromNetworkListener(File sel_file, File xml_file) {
    int currentScale = this_interfacetoolbar.scaleList.getSelectedIndex();
    this_interfacesaveFilesDialog.saveData();
    
    setCurrentImage_dir(sel_file.getParent() + File.separatorChar);
    setImageDirName(sel_file.getName());
    
    boolean xmlUnspecified = false;
    
    if (xml_file == null) {
      xmlUnspecified = true;
      xml_file = sel_file;
    }
    




    if ((xml_file.isFile()) || (!xml_file.canWrite())) {
      setCurrentXml_dir(xml_file.getParentFile().getParent() + File.separatorChar);
      setXmlDirName(xml_file.getParentFile().getName());
    } else {
      xmlUnspecified = true;
      setCurrentXml_dir(xml_file.getParent() + File.separatorChar);
      setXmlDirName(xml_file.getName());
    }
    
    if (sel_file.isDirectory()) {
      LoadDictionary(1);
    } else {
      LoadDictionary(0);
    }
    dialog_open = false;
    

    tbdPane.data_panel.a_window.setPage(null);
    tbdPane.data_panel.dataTable.myClearSelection();
    
    loadConfigFile(null);
    
    ocrTable.setColumnWidths();
    
    if (sel_file.isDirectory()) {
      this_interfaceocrTable.processSelectionEvent(0, 1, true);
      return;
    }
    
    String baseName = null;
    if (xml_file.isFile()) {
      baseName = getFileNameWithoutExt(xml_file.getName());
    } else {
      baseName = getFileNameWithoutExt(sel_file.getName());
    }
    String ext = getFileExtension(sel_file.getName());
    String xmlDir = getCurrentXmlDir() + 
      getXmlDirName() + 
      File.separator;
    
    String xmlpath = xmlDir + baseName;
    if (xmlUnspecified)
      xmlpath = xmlpath + ".final";
    xmlpath = xmlpath + ext;
    



    FilePropPacket fpp = workmodeProps[0]
      .getElementFilePropVec(0);
    
    if (this_interface.getCollapseRows()) {
      fpp.setCollapsed(false);
      this_interfaceocrTable.addVarietyOfExtRows(fpp, 0);
      this_interfaceocrTable.updateRow(fpp, 0);
    }
    
    int row = workmodeProps[0].getImageIndex(xmlpath);
    

    if (row == -1) {
      this_interfaceocrTable.processSelectionEvent(0, 1, true);
    } else {
      this_interfaceocrTable.processSelectionEvent(row, 1, true);
    }
    this_interfacetoolbar.setScaleByIndex(currentScale);
  }
  







  private String getConfigFilePath()
  {
    String configPath = null;
    File f = null;
    String xml = ".xml";
    String defaultConfigFilePath = getCurrentXmlDir() + getXmlDirName() + 
      File.separator + "GEDIConfig";
    
    LoadUserProps userProps = LoadUserProps.getInstance();
    configPath = userProps.getProperty("configPath").trim();
    
    if ((!configPath.equals("")) && (Login.loadConfig)) {
      f = new File(configPath);
      
      if (!f.exists()) {
        configPath = defaultConfigFilePath + xml;
      }
    } else {
      configPath = defaultConfigFilePath + xml;
    }
    f = new File(configPath);
    
    if (!f.exists()) {
      configPath = defaultConfigFilePath + xml.toUpperCase();
    }
    f = new File(configPath);
    
    if (!f.exists())
    {
      return null;
    }
    System.out.println("GEDIConfig.xml path: " + configPath);
    
    return configPath;
  }
  







  public void loadConfigFile(String specialConfig)
  {
    if ((config_file != null) && (config_file.equals(specialConfig))) {
      return;
    }
    if (specialConfig == null) {
      config_file = getConfigFilePath();
    } else {
      config_file = specialConfig;
    }
    



    if (config_file == null) {
      config_file = "There is no Config File (will be created in Xml Directory)";
      
      tbdPane.data_panel.t_window.loadWorkSpace(null);
      
      acu.setTypeAttributes(
        new TypeSettings(
        tbdPane.data_panel.t_window.getSaveableTypeSettings(), 
        acu.getTypeAttributesMap()).toMap());
    }
    else {
      LoadDataFile ld = LoadDataFile.loadWorkspace(config_file);
      if (ld.getTypeSettings() != null) {
        tbdPane.data_panel.t_window.loadWorkSpace(ld
          .getTypeSettings().toTypeWindow());
        acu.setTypeAttributes(ld.getTypeSettings().toMap());
        
        setShortCutKeys();





      }
      else
      {




        LoadDataFile.dumpWorkspace(new TypeSettings(
          tbdPane.data_panel.t_window.getTypeSettings(), acu
          .getTypeAttributesMap()), null, config_file);
        acu.setSetting_modified(false);
      }
    }
  }
  












  private void addScripts()
  {
    ArrayList<String> scriptPaths = scriptLoader.getScriptList();
    
    if (scriptPaths.isEmpty()) {
      JMenuItem scriptName = new JMenuItem("<No available scripts>");
      scriptName.setEnabled(false);
      menuScripts.add(scriptName);
      return;
    }
    
    for (int i = 0; i < scriptPaths.size(); i++) {
      final String scriptPropFilePath = (String)scriptPaths.get(i);
      
      String scriptNameToAppear = scriptLoader
        .getScriptNameToAppear(scriptPropFilePath);
      
      JMenuItem scriptName = new JMenuItem(scriptNameToAppear);
      menuScripts.add(scriptName);
      scriptName.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent arg0) {
          if (getTitle().equals("DocLib - GroundTruthing Editor and Document Interface Professional Edition (DL-GEDIPro)"))
          {


            int answer = JOptionPane.showConfirmDialog(OCRInterface.this_interface, 
              "No document selected. [%img-*] will be undefined.\nContinue anyway?", 
              "Message", 
              0, 
              3);
            if (answer == 1) {
              return;
            }
          }
          
          int[] selectedRows = this_interfaceocrTable.getSelectedRows();
          
          if (selectedRows.length == 0) {
            scriptLoader.runScript(scriptPropFilePath);
          } else {
            for (int i = 0; i < selectedRows.length; i++) {
              ocrTable.processSelectionEvent(selectedRows[i], 1, true);
              scriptLoader.runScript(scriptPropFilePath);
            }
          }
        }
      });
    }
  }
  
  private void addScriptHowTo() {
    menuScripts.addSeparator();
    JMenuItem scriptInfo = new JMenuItem("How to run a Script?");
    menuScripts.add(scriptInfo);
    scriptInfo.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent arg0) {
        int choice = JOptionPane.showConfirmDialog(
          OCRInterface.this_interface, 
          "Configuring GEDI for Scripts:\n- Specify a separate \"<script_name>.properties\" file for each script \n- Place all \"*.properties\" files into \"<GEDI_ROOT> \\ lib \\ Scripts\" directory \n\nRunning a script (or executable program): \n- Select an image or set of images in the GEDI interface file panel \n- Select the script to run on the \"Scripts\" menu \n- If you selected more than one image, the Script will be run on each image sequentially\n\n\"GEDI \\ lib \\ Scripts \\ ScriptConfig.properties\" explains required syntax for Script Properties file\nand contains examples. Do you want to open it now?\n", 
          "How to run a Script", 
          0, 
          1);
        
        if (choice == 0) {
          String pathToConfig = 
            System.getProperty("user.dir") + 
            ScriptConstants.SCRIPT_DIR + 
            File.separator + 
            "ScriptConfig.properties";
          DesktopUtil desktop = new DesktopUtil();
          
          if (desktop.isOpenSupported()) {
            desktop.setOpenAction();
            desktop.launchDefaultApplication(pathToConfig);
          }
        }
      }
    });
  }
  

  public ConnectedComponent getConnectedComponentHandler()
  {
    return connectedComponentHandler;
  }
  






  public void loadConnectedComponents(int filterSize)
  {
    String loadedFilePath = getCurrentImageDir() + 
      getImageDirName();
    connectedComponentHandler = new ConnectedComponent(loadedFilePath, filterSize);
    connectedComponentHandler.loadConnectedComponents();
  }
  










  private void updateSRCTag()
  {
    if (currDoc == null) {
      return;
    }
    String currentImage = ImageReaderDrawer.file_path;
    String dataFile = this_interface.hasMeta(currentImage);
    

    if (dataFile != null) {
      File temp = new File(dataFile);
      if (temp.canWrite()) {}
    }
    else
    {
      return;
    }
    String imageName = ImageReaderDrawer.file_name;
    String imageSrc = currDoc.getDocument().getImageSrc();
    if (!imageName.equals(imageSrc)) {
      currDoc.getDocument().setImageSrc(imageName);
      currDoc.setModified(true);
    }
    
    LinkedList<DLPage> pages = currDocgetDocumentdocumentPages;
    for (DLPage page : pages) {
      if (!imageName.equals(page.getImageSrc())) {
        page.setImageSrc(imageName);
        currDoc.setModified(true);
      }
    }
  }
  






  public void sortOffsets(LoadDataFile doc)
  {
    if (doc == null) {
      return;
    }
    DLDocument document = doc.getDocument();
    if (document == null) {
      return;
    }
    LinkedList<DLPage> pages = document.getdocPages();
    Iterator localIterator2;
    for (Iterator localIterator1 = pages.iterator(); localIterator1.hasNext(); 
        

        localIterator2.hasNext())
    {
      DLPage page = (DLPage)localIterator1.next();
      LinkedList<DLZone> zones = pageZones;
      
      localIterator2 = zones.iterator(); continue;DLZone zone = (DLZone)localIterator2.next();
      if (zone.offsetsReady()) {
        try
        {
          String parentOffsets = zone.getAttributeValue("offsets");
          if (parentOffsets != null) {
            String sortedOffsetsStr = Zone.sortOffsets(parentOffsets);
            zone.setAttributeValue("offsets", sortedOffsetsStr);
            currDoc.setModified(true);
          }
          
          LinkedList<DLZone> children = zone.getChildrenZones();
          for (DLZone child : children) {
            String childOffsets = child.getAttributeValue("offsets");
            if (childOffsets != null) {
              String sortedOffsetsStr = Zone.sortOffsets(parentOffsets);
              child.setAttributeValue("offsets", sortedOffsetsStr);
              currDoc.setModified(true);
            }
          }
        }
        catch (NullPointerException npl) {
          npl.printStackTrace();
        }
      }
    }
  }
  


















  private void cleanUp(LoadDataFile doc)
  {
    if (doc == null) {
      return;
    }
    DLDocument document = doc.getDocument();
    if (document == null) {
      return;
    }
    LinkedList<DLPage> pages = document.getdocPages();
    boolean badZoneFound = false;
    Iterator localIterator2;
    for (Iterator localIterator1 = pages.iterator(); localIterator1.hasNext(); 
        

        localIterator2.hasNext())
    {
      DLPage page = (DLPage)localIterator1.next();
      LinkedList<DLZone> zones_clone = (LinkedList)pageZones.clone();
      
      localIterator2 = zones_clone.iterator(); continue;DLZone zone = (DLZone)localIterator2.next();
      
      int width = zone.dlGetZoneWidth();
      int height = zone.dlGetZoneHeight();
      String zoneType = zone.getAttributeValue("gedi_type").trim();
      if ((!(zone instanceof PolygonZone)) && 
        (!(zone instanceof OrientedBox)) && 
        (width == 0) && 
        (height == 0)) {
        pageZones.remove(zone);
        badZoneFound = true;
      }
      
      if ((zoneType.equals("")) || (zoneType == null)) {
        pageZones.remove(zone);
        badZoneFound = true;
      }
    }
    
    if (badZoneFound) {
      currDoc.setModified(true);
    }
    
    currentHWObjcurr_canvas.paintCanvas();
  }
  












  private void convertRLEtoNewFormat()
  {
    if (currDoc == null)
      return;
    LinkedList<DLPage> pages = currDocgetDocumentdocumentPages;
    Iterator localIterator2; for (Iterator localIterator1 = pages.iterator(); localIterator1.hasNext(); 
        

        localIterator2.hasNext())
    {
      DLPage page = (DLPage)localIterator1.next();
      LinkedList<DLZone> zones = pageZones;
      
      localIterator2 = zones.iterator(); continue;DLZone zone = (DLZone)localIterator2.next();
      String rleImage = (String)zone.getZoneTags().get("RLEIMAGE");
      if ((rleImage != null) && (!rleImage.trim().isEmpty()) && (!rleImage.trim().equals("0")))
      {

        int index = rleImage.indexOf("(");
        
        if (index != 0) {
          String numOfSegments = rleImage.substring(0, index);
          String the_rest = rleImage.substring(index, rleImage.length());
          int x = get_Boundsx;
          int y = get_Boundsy;
          String newFormat = "(" + x + "," + y + ")," + numOfSegments + ";" + the_rest;
          zone.getZoneTags().put("RLEIMAGE", newFormat);
        }
      }
    }
  }
  
  public String getErrorMessage() {
    return errorMessage;
  }
  
  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }
  
  public MultiPagePanel getMultiPagePanel() {
    return multiPagePanel;
  }
  










  private void modifyHeightOfRightPanel(int addHeight)
  {
    right.setPreferredSize(new Dimension(mainPane.getWidth() - 
      mainPane.getDividerLocation(), 
      mainPane.getHeight() - (
      mainPane.getInsets().bottom + 
      mainPane.getInsets().top) + addHeight));
  }
  
  public void updateETextWindowContent() {
    if (getUseETextWindow())
    {
      this_interfacetbdPane.data_panel.a_window.setETextWindow(true);
      

      if (getAttsConfigUtil().getEncodingPanel().whichSelected() == EncodingPanel.UTF8) {
        EncodingPanel.utf8Button.doClick();
        loadETextWindow(getFilePath(), "UTF-8");
        this_interfaceutf8Selected = true;
        this_interfaceutf16Selected = false;
      }
      else if (getAttsConfigUtil().getEncodingPanel().whichSelected() == EncodingPanel.UTF16) {
        EncodingPanel.utf16Button.doClick();
        loadETextWindow(getFilePath(), "UTF-16");
        this_interfaceutf16Selected = true;
        this_interfaceutf8Selected = false;
      }
    }
    else
    {
      this_interfacetbdPane.data_panel.a_window.setETextWindow(false);
    }
  }
  
  public void updateGTToolbar()
  {
    if (getEnableAlignment()) {
      GTToolbar.setVisible(true);
      GTToolbarMenuItem.setSelected(true);
      int height = (int)GTToolbar.getPreferredSize().getHeight();
      this_interface.modifyHeightOfRightPanel(-height);
    } else {
      GTToolbar.setVisible(false);
      GTToolbarMenuItem.setSelected(false);
      int height = (int)GTToolbar.getPreferredSize().getHeight();
      this_interface.modifyHeightOfRightPanel(height);
    }
  }
  
  public void updatePolyTranscribeToolbar() {
    if (getEnablePolygonTranscription()) {
      PolyTranscribeToolbar.setVisible(true);
      PolyTranscribeToolbarMenuItem.setSelected(true);
      int height = (int)PolyTranscribeToolbar.getPreferredSize().getHeight();
      this_interface.modifyHeightOfRightPanel(-height);
    } else {
      PolyTranscribeToolbar.setVisible(false);
      PolyTranscribeToolbarMenuItem.setSelected(false);
      int height = (int)PolyTranscribeToolbar.getPreferredSize().getHeight();
      this_interface.modifyHeightOfRightPanel(height);
    }
  }
  
  public void updateTranslateWorkflowToolbar() {
    if (getEnableTranslateWorkflow()) {
      TranslateWorkflowToolbar.setVisible(true);
      int height = (int)TranslateWorkflowToolbar.getPreferredSize().getHeight();
      this_interface.modifyHeightOfRightPanel(-height);
    } else {
      TranslateWorkflowToolbar.setVisible(false);
      int height = (int)PolyTranscribeToolbar.getPreferredSize().getHeight();
      this_interface.modifyHeightOfRightPanel(height);
    }
  }
  





  public String getFileExtension(String name)
  {
    return name.substring(
      name.lastIndexOf('.'), 
      name.length());
  }
  
  public String getFileNameWithoutExt(String filePath) {
    return filePath.substring(0, filePath.lastIndexOf('.'));
  }
  
  public JSplitPane getRightSplitPanel() {
    return rightSplitPanel;
  }
  
  public void setRightSplitPanel(JSplitPane rightSplitPanel) {
    this.rightSplitPanel = rightSplitPanel;
  }
  
  public String getImageExt() {
    return imageExt;
  }
  
  public void setImageExt(String imageExt) {
    this.imageExt = imageExt;
  }
  
  public ArrayList<File> getRawImageOnlyFileList() {
    return rawImageOnlyFileList;
  }
  
  public void addToRawXmlOnlyFileList(File xmlFile) {
    if (!rawXmlOnlyFileList.contains(xmlFile))
      rawXmlOnlyFileList.add(xmlFile);
  }
  
  public ArrayList<File> getRawXmlOnlyFileList() {
    return rawXmlOnlyFileList;
  }
  










  private void loadProperties(Properties dirProps)
  {
    if ((commandLineOptions != null) && (commandLineOptions.getImageDirPath() != null)) {
      setCurrentImage_dir(commandLineOptions.getImageDirPath());
    } else {
      String image_path = dirProps.getProperty("Image_Path");
      if (image_path == null) {
        setCurrentImage_dir("");
      } else {
        setCurrentImage_dir(image_path);
      }
    }
    
    if ((commandLineOptions != null) && (commandLineOptions.getXmlDirPath() != null)) {
      setCurrentXml_dir(commandLineOptions.getXmlDirPath());
    } else {
      String xml_path = dirProps.getProperty("Xml_Path");
      if (xml_path == null) {
        setCurrentXml_dir("");
      } else {
        setCurrentXml_dir(xml_path);
      }
    }
    
    if ((commandLineOptions != null) && (commandLineOptions.getImageDirName() != null)) {
      setImageDirName(commandLineOptions.getImageDirName());
    } else {
      String Image_Dir = dirProps.getProperty("Image_Dir");
      if (Image_Dir == null) {
        setImageDirName("");
      } else {
        setImageDirName(Image_Dir);
      }
    }
    
    if ((commandLineOptions != null) && (commandLineOptions.getXmlDirName() != null)) {
      setXmlDirName(commandLineOptions.getXmlDirName());
    } else {
      String Xml_Dir = dirProps.getProperty("Xml_Dir");
      if (Xml_Dir == null) {
        setXmlDirName("");
      } else {
        setXmlDirName(Xml_Dir);
      }
    }
    String autoSaveXmlFileChanges = dirProps.getProperty("autoSaveXmlFileChanges");
    if (autoSaveXmlFileChanges == null) {
      saveFilesDialog.setAutoSave(true);
    } else {
      saveFilesDialog.setAutoSave(Boolean.parseBoolean(autoSaveXmlFileChanges));
    }
    
    String autoCreateConfigFile = dirProps.getProperty("autoCreateConfigFile");
    if (autoCreateConfigFile == null) {
      setAutoCreateConfigFile(true);
    } else {
      setAutoCreateConfigFile(Boolean.parseBoolean(autoCreateConfigFile));
    }
    
    String autoOverwriteOverlayFile = dirProps.getProperty("autoOverwriteOverlayFile");
    if (autoOverwriteOverlayFile == null) {
      setAutoOverwriteOverlayFile(false);
    } else {
      setAutoOverwriteOverlayFile(Boolean.parseBoolean(autoOverwriteOverlayFile));
    }
    
    String enableProfileLoadOnLogin = LoadUserProps.getInstance()
      .getProperty("enableProfileLoadOnLogin");
    if (enableProfileLoadOnLogin == null) {
      setEnableProfileLoad(true);
    } else {
      setEnableProfileLoad(Boolean.parseBoolean(enableProfileLoadOnLogin));
    }
    
    String saveConfigCopyInDataDirectory = dirProps.getProperty("saveConfigCopyInDataDirectory");
    if (saveConfigCopyInDataDirectory == null) {
      setSaveConfigCopyInDataDirectory(true);
    } else {
      setSaveConfigCopyInDataDirectory(Boolean.parseBoolean(saveConfigCopyInDataDirectory));
    }
    String loadPreviousDirOnStartup = dirProps.getProperty("loadPreviousDirOnStartup");
    if (loadPreviousDirOnStartup == null) {
      setLoadPreviousDirOnStartup(true);
    } else {
      setLoadPreviousDirOnStartup(Boolean.parseBoolean(loadPreviousDirOnStartup));
    }
    String transparency = dirProps.getProperty("transparency");
    if (transparency == null) {
      setTransparency(63.0F);
    } else {
      setTransparency(Float.parseFloat(transparency));
    }
    
    String pseudoColorAttribute = dirProps.getProperty("pseudoColorAttribute");
    if (pseudoColorAttribute == null) {
      setPseudoColorAttribute("");
    } else {
      setPseudoColorAttribute(pseudoColorAttribute);
    }
    String displayField = dirProps.getProperty("displayField");
    if (displayField == null) {
      setDisplayField("contents");
    } else {
      setDisplayField(displayField);
    }
    String ctrl_C_Copies = dirProps.getProperty("ctrl_C_Copies");
    if (ctrl_C_Copies == null) {
      setCtrl_C_Copies("<ZONE>");
    } else {
      setCtrl_C_Copies(ctrl_C_Copies);
    }
    String ctrl_C_separator = dirProps.getProperty("ctrl_C_separator");
    if (ctrl_C_separator == null) {
      setCtrl_C_separator(",");
    } else {
      setCtrl_C_separator(ctrl_C_separator);
    }
    String dltextlineDefaultSegmentation = dirProps
      .getProperty("dltextlineDefaultSegmentation");
    if (dltextlineDefaultSegmentation == null) {
      setDlTextLineDefaultSegmentation("word");
    } else {
      setDlTextLineDefaultSegmentation(dltextlineDefaultSegmentation);
    }
    
    String GediAlignCmdPath = dirProps.getProperty("GediAlignCmdPath");
    if (GediAlignCmdPath == null) {
      setGediAlignCmdPath("\\lib\\GediAlign\\GediAlign.exe");
    } else {
      setGediAlignCmdPath(GediAlignCmdPath);
    }
    
    String autoShowDlTextLineContentWindowOnCreate = dirProps
      .getProperty("autoShowDlTextLineContentWindowOnCreate");
    if (autoShowDlTextLineContentWindowOnCreate == null) {
      setShowContentWindowOnDlTextLineCreate(false);
    } else {
      setShowContentWindowOnDlTextLineCreate(
        Boolean.parseBoolean(autoShowDlTextLineContentWindowOnCreate));
    }
    
    String displayCommentsInSeparateWindow = dirProps
      .getProperty("displayCommentsInSeparateWindow");
    if (displayCommentsInSeparateWindow == null) {
      setDisplayCommentsInSeparateWindow(false);
    } else {
      setDisplayCommentsInSeparateWindow(
        Boolean.parseBoolean(displayCommentsInSeparateWindow));
    }
    
    String showTextOnAllZones = dirProps.getProperty("showTextOnAllZones");
    if (showTextOnAllZones == null) {
      setShowTextOnAllZones(true);
    } else {
      setShowTextOnAllZones(Boolean.parseBoolean(showTextOnAllZones));
    }
    
    String showReadingOrder = dirProps.getProperty("showReadingOrder");
    if (showReadingOrder == null) {
      setShowReadingOrder(true);
    } else {
      setShowReadingOrder(Boolean.parseBoolean(showReadingOrder));
    }
    String useStartPoint = dirProps.getProperty("showTextOnAllZones");
    if (useStartPoint == null) {
      setUseStartPoint(true);
    } else {
      setUseStartPoint(Boolean.parseBoolean(useStartPoint));
    }
    
    String allowXmlImage = dirProps.getProperty("allowXmlImage");
    if (allowXmlImage == null) {
      setAllowXmlImage(true);
    } else {
      setAllowXmlImage(Boolean.parseBoolean(allowXmlImage));
    }
    
    String collapseRows = dirProps.getProperty("collapseRows");
    if (collapseRows == null) {
      setCollapseRows(false);
    } else {
      setCollapseRows(Boolean.parseBoolean(collapseRows));
    }
    
    String allowWordSplit = dirProps.getProperty("allowWordSplit");
    if (allowWordSplit == null) {
      setAllowWordSplit(false);
    } else {
      setAllowWordSplit(Boolean.parseBoolean(allowWordSplit));
    }
    
    String lockContentEditing = dirProps.getProperty("lockContentEditing");
    if (lockContentEditing == null) {
      setLockContentEditing(false);
    } else {
      setLockContentEditing(Boolean.parseBoolean(lockContentEditing));
    }
    String enableDigitTokenPopup = dirProps.getProperty("enableDigitTokenPopup");
    if (enableDigitTokenPopup == null) {
      setEnableDigitTokenPopup(false);
    } else {
      setEnableDigitTokenPopup(Boolean.parseBoolean(enableDigitTokenPopup));
    }
    String enableReverseButton = dirProps.getProperty("enableReverseButton");
    if (enableReverseButton == null) {
      setEnableReverseButton(false);
    } else {
      setEnableReverseButton(Boolean.parseBoolean(enableReverseButton));
    }
    String normalizeArabicForm_B = dirProps.getProperty("normalizeArabicForm_B");
    if (normalizeArabicForm_B == null) {
      setNormalizeArabicForm_B(false);
    } else {
      setNormalizeArabicForm_B(Boolean.parseBoolean(normalizeArabicForm_B));
    }
    String saveNormalizedArabicContent = dirProps.getProperty("saveNormalizedArabicContent");
    if (saveNormalizedArabicContent == null) {
      setSaveNormalizedArabicContent(false);
    } else {
      setSaveNormalizedArabicContent(Boolean.parseBoolean(saveNormalizedArabicContent));
    }
    String recomputeRLEonEdit = dirProps.getProperty("recomputeRLEonEdit");
    if (recomputeRLEonEdit == null) {
      setRecomputeRLEonEdit(false);
    } else {
      setRecomputeRLEonEdit(Boolean.parseBoolean(recomputeRLEonEdit));
    }
    String preserveAttributesOnMerge = dirProps.getProperty("preserveAttributesOnMerge");
    if (preserveAttributesOnMerge == null) {
      setPreserveAttributesOnMerge(false);
    } else {
      setPreserveAttributesOnMerge(Boolean.parseBoolean(preserveAttributesOnMerge));
    }
    String insertSpaceOnMerge = dirProps.getProperty("insertSpaceOnMerge");
    if (insertSpaceOnMerge == null) {
      setInsertSpaceOnMerge(true);
    } else {
      setInsertSpaceOnMerge(Boolean.parseBoolean(insertSpaceOnMerge));
    }
    String mergeZonesToBox = dirProps.getProperty("mergeZonesToBox");
    if (mergeZonesToBox == null) {
      setMergeZonesToBox(true);
    } else {
      setMergeZonesToBox(Boolean.parseBoolean(mergeZonesToBox));
    }
    String expandThenShrink = dirProps.getProperty("expandThenShrink");
    if (expandThenShrink == null) {
      setExpandThenShrink(false);
    } else {
      setExpandThenShrink(Boolean.parseBoolean(expandThenShrink));
    }
    String minMaxExpand = dirProps.getProperty("minMaxExpand");
    if (minMaxExpand == null) {
      setMinMaxExpand("0,10");
    } else {
      setMinMaxExpand(minMaxExpand.trim());
    }
    String convertRectanglesToPolygonsOnShrink = dirProps.getProperty("convertRectanglesToPolygonsOnShrink");
    if (convertRectanglesToPolygonsOnShrink == null) {
      setConvertRectanglesToPolygonsOnShrink(true);
    } else {
      setConvertRectanglesToPolygonsOnShrink(Boolean.parseBoolean(convertRectanglesToPolygonsOnShrink));
    }
    String shrinkAfterOverlapRemoval = dirProps.getProperty("shrinkAfterOverlapRemoval");
    if (shrinkAfterOverlapRemoval == null) {
      setShrinkAfterOverlapRemoval(false);
    } else {
      setShrinkAfterOverlapRemoval(Boolean.parseBoolean(shrinkAfterOverlapRemoval));
    }
    String enableNCPolygonShrinking = dirProps.getProperty("enableNonConvexPolygonShrinking");
    if (enableNCPolygonShrinking == null) {
      setEnableNonConvexPolygonShrinking(false);
    } else {
      setEnableNonConvexPolygonShrinking(Boolean.parseBoolean(enableNCPolygonShrinking));
    }
    String polygonPadding = dirProps.getProperty("polygonPadding");
    if (polygonPadding == null) {
      setPolygonPadding(2);
    } else {
      setPolygonPadding(Integer.parseInt(polygonPadding));
    }
    String overlapRemovalPadding = dirProps.getProperty("overlapRemovalPadding");
    if (overlapRemovalPadding == null) {
      setOverlapRemovalPadding(1);
    } else {
      setOverlapRemovalPadding(Integer.parseInt(overlapRemovalPadding));
    }
    String intersectionThreshold = dirProps.getProperty("intersectionThreshold");
    if (intersectionThreshold == null) {
      setIntersectionThreshold(5);
    } else {
      setIntersectionThreshold(Integer.parseInt(intersectionThreshold));
    }
    String edgesGranularity = dirProps.getProperty("edgesGranularity");
    if (edgesGranularity == null) {
      setEdgesGranularity(50);
    } else {
      setEdgesGranularity(Integer.parseInt(edgesGranularity));
    }
    String mergeTextToFirstZone = dirProps.getProperty("mergeTextToFirstZone");
    if (mergeTextToFirstZone == null) {
      setMergeTextToFirstZone(false);
    } else {
      setMergeTextToFirstZone(Boolean.parseBoolean(mergeTextToFirstZone));
    }
    String alignmentDefaultSegmenation = dirProps.getProperty("alignmentDefaultSegmenation");
    if (alignmentDefaultSegmenation == null) {
      setAlignmentDefaultSegmenation("word");
    } else {
      setAlignmentDefaultSegmenation(alignmentDefaultSegmenation.trim());
    }
    String allowZoneRecenter = dirProps.getProperty("allowZoneRecenter");
    if (allowZoneRecenter == null) {
      setAllowZoneRecenter(false);
    } else {
      setAllowZoneRecenter(Boolean.parseBoolean(allowZoneRecenter));
    }
    
    String useETextWindow = dirProps.getProperty("useETextWindow");
    if (useETextWindow == null) {
      setUseETextWindow(true);
    } else {
      setUseETextWindow(Boolean.parseBoolean(useETextWindow));
    }
    
    String enableAlignment = dirProps.getProperty("enableAlignment");
    if (enableAlignment == null) {
      setEnableAlignment(true);
    } else {
      setEnableAlignment(Boolean.parseBoolean(enableAlignment));
    }
    
    String enablePolygonTranscription = dirProps.getProperty("enablePolygonTranscription");
    if (enablePolygonTranscription == null) {
      setEnablePolygonTranscription(false);
    } else {
      setEnablePolygonTranscription(Boolean.parseBoolean(enablePolygonTranscription));
    }
    String enableTranslateWorkflow = dirProps.getProperty("enableTranslateWorkflow");
    if (enableTranslateWorkflow == null) {
      setEnableTranslateWorkflow(false);
    } else {
      setEnableTranslateWorkflow(Boolean.parseBoolean(enableTranslateWorkflow));
    }
    String polygonTranscriptionType = dirProps.getProperty("polygonTranscriptionType");
    if (polygonTranscriptionType == null) {
      setPolygonTranscriptionType(" ");
    } else {
      setPolygonTranscriptionType(polygonTranscriptionType);
    }
    
    String enforceTranscriptionReview = dirProps.getProperty("enforceTranscriptionReview");
    if (enforceTranscriptionReview == null) {
      setEnforceTranscriptionReview(false);
    } else {
      setEnforceTranscriptionReview(Boolean.parseBoolean(enforceTranscriptionReview));
    }
    
    String transcribeUsingBoxes = dirProps.getProperty("transcribeUsingBoxes");
    if (transcribeUsingBoxes == null) {
      setTranscribeUsingBoxes(false);
    } else {
      setTranscribeUsingBoxes(Boolean.parseBoolean(transcribeUsingBoxes));
    }
    
    String useDirNameForTextFile = dirProps.getProperty("useDirNameForTextFile");
    if (useDirNameForTextFile == null) {
      setUseDirNameForTextFile(true);
    } else {
      setUseDirNameForTextFile(Boolean.parseBoolean(useDirNameForTextFile));
    }
    
    String showZoneTypes = dirProps.getProperty("showZoneTypes");
    if (showZoneTypes == null) {
      setShowZoneTypes(false);
    } else {
      setShowZoneTypes(Boolean.parseBoolean(showZoneTypes));
    }
    
    String enableParentChildButtons = dirProps.getProperty("enableParentChildButtons");
    if (enableParentChildButtons == null) {
      setEnablePCButtons(false);
    } else {
      setEnablePCButtons(Boolean.parseBoolean(enableParentChildButtons));
    }
    
    String documentCaptureToDefaultFile = dirProps.getProperty("documentCaptureToDefaultFile");
    if (documentCaptureToDefaultFile == null) {
      setDocumentCaptureToDefaultFile(true);
    } else {
      setDocumentCaptureToDefaultFile(Boolean.parseBoolean(documentCaptureToDefaultFile));
    }
    
    String listenerPort = dirProps.getProperty("listenerPort");
    if (listenerPort == null) {
      setListenerPort(8000);
    } else {
      setListenerPort(Integer.parseInt(listenerPort));
    }
    
    if ((commandLineOptions != null) && (commandLineOptions.getImageDirPath() != null)) {
      setRootPath(commandLineOptions.getRootPath());
    } else {
      String networkListenerRootPath = dirProps.getProperty("networkListenerRootPath");
      if (networkListenerRootPath == null) {
        setRootPath("");
      } else {
        setRootPath(networkListenerRootPath);
      }
    }
    
    String enableNetworkListener = dirProps.getProperty("enableNetworkListener");
    if (enableNetworkListener == null) {
      setNetworkListenerOn(false);
    } else {
      setNetworkListenerOn(Boolean.parseBoolean(enableNetworkListener));
    }
    
    String applyExpandShrink = dirProps.getProperty("applyExpandShrink");
    if (applyExpandShrink == null) {
      setApplyExpandShrink(false);
    } else {
      setApplyExpandShrink(Boolean.parseBoolean(applyExpandShrink));
    }
    String connectedComponentFilterSize = dirProps.getProperty("connectedComponentFilterSize");
    if (connectedComponentFilterSize == null) {
      setConnectedComponentFilterSize(5);
    } else {
      setConnectedComponentFilterSize(Integer.parseInt(connectedComponentFilterSize));
    }
    
    String documentComments = dirProps.getProperty("documentComments");
    if (documentComments == null) {
      setCommentText("");
    } else {
      setCommentText(documentComments);
    }
    String maxRefineExpand = dirProps.getProperty("maxRefineExpand");
    if (maxRefineExpand == null) {
      setMaxRefineExpand(30);
    } else {
      setMaxRefineExpand(Integer.parseInt(maxRefineExpand));
    }
    String randomConstantForPseudoColor = dirProps.getProperty("randomConstantForPseudoColor");
    if (randomConstantForPseudoColor == null) {
      setRandomConstantForPseudoColor("");
    } else {
      setRandomConstantForPseudoColor(randomConstantForPseudoColor);
    }
    String cutTolerance = dirProps.getProperty("cutTolerance");
    if (cutTolerance == null) {
      setCutTolerance(5);
    } else {
      setCutTolerance(Integer.parseInt(cutTolerance));
    }
    
    String allowIdenticalZones = dirProps.getProperty("allowIdenticalZones");
    if (allowIdenticalZones == null) {
      setAllowIdenticalZones(false);
    } else {
      setAllowIdenticalZones(Boolean.parseBoolean(allowIdenticalZones));
    }
    
    String warnIdenticalZonesWereCreated = dirProps.getProperty("warnIdenticalZonesWereCreated");
    if (warnIdenticalZonesWereCreated == null) {
      setWarnIdenticalZonesWereCreated(false);
    } else {
      setWarnIdenticalZonesWereCreated(Boolean.parseBoolean(warnIdenticalZonesWereCreated));
    }
    
    String lineThickness = dirProps.getProperty("lineThickness");
    if (lineThickness == null) {
      setLineThickness(1.0F);
    } else {
      setLineThickness(Float.parseFloat(lineThickness));
    }
    String indicDigitsON = dirProps.getProperty("indicDigitsON");
    if (indicDigitsON == null) {
      setIndicDigitsON(true);
    } else {
      setIndicDigitsON(Boolean.parseBoolean(indicDigitsON));
    }
    String thresholdingToolbarVisible = dirProps.getProperty("thresholdingToolbarVisible");
    if (thresholdingToolbarVisible == null) {
      setThresholdingToolbarVisible(false);
    } else {
      setThresholdingToolbarVisible(Boolean.parseBoolean(thresholdingToolbarVisible));
    }
    String lowerUpperThresholdValues = dirProps.getProperty("lowerUpperThresholdValues");
    if (lowerUpperThresholdValues == null) {
      setLowerUpperThresholdValues("0,128");
    } else {
      setLowerUpperThresholdValues(lowerUpperThresholdValues);
    }
    String shrinkAfterSplit = dirProps.getProperty("shrinkAfterSplit");
    if (shrinkAfterSplit == null) {
      setShrinkAfterSplit(false);
    } else {
      setShrinkAfterSplit(Boolean.parseBoolean(shrinkAfterSplit));
    }
    String preserveReadingOrderOnSplit = dirProps.getProperty("preserveReadingOrderOnSplit");
    if (preserveReadingOrderOnSplit == null) {
      setPreserveReadingOrderOnSplit(true);
    } else {
      setPreserveReadingOrderOnSplit(Boolean.parseBoolean(preserveReadingOrderOnSplit));
    }
    String preserveAttributesOnSplit = dirProps.getProperty("preserveAttributesOnSplit");
    if (preserveAttributesOnSplit == null) {
      setPreserveAttributesOnSplit(true);
    } else
      setPreserveAttributesOnSplit(Boolean.parseBoolean(preserveAttributesOnSplit));
  }
  
  public OptionParser getCommandLineOptions() {
    return commandLineOptions;
  }
  
  public BrowserToolBar getToolbar() {
    return toolbar;
  }
  
  public int getCurrentRotateDegrees() {
    return currentRotateDegrees;
  }
  
  public void setCurrentRotateDegrees(int newCurrentRotateDegrees) {
    currentRotateDegrees = newCurrentRotateDegrees;
    
    this_interface.getToolbar().setRotateButtonIcon(currentRotateDegrees);
  }
  
  public boolean isHideBoxes() {
    return hideBoxes;
  }
  
  public CommentsDialog getCommentsWindow() {
    return commentsWindow;
  }
  





  public void setCommentsWindow(CommentsDialog commentsWindow)
  {
    if ((commentsWindow == null) && (this.commentsWindow != null)) {
      if ((this.commentsWindow.getTopLevelAncestor() instanceof JDialog)) {
        ((JDialog)this.commentsWindow.getTopLevelAncestor()).dispose();
      } else {
        JPanel pBottom = new JPanel();
        pBottom.setLayout(new BoxLayout(pBottom, 1));
        ElectronicTextDisplayer eTextWindow = this_interfacetbdPane.data_panel.a_window.eTextWindow;
        pBottom.add(eTextWindow);
        rightSplitPanel.setBottomComponent(null);
        rightSplitPanel.setBottomComponent(pBottom);
        this_interfacerightSpliPanelDividerLocation = 0.85D;
        rightSplitPanel.setResizeWeight(this_interfacerightSpliPanelDividerLocation);
        rightSplitPanel.setDividerLocation(this_interfacerightSpliPanelDividerLocation);
      }
    }
    
    this.commentsWindow = commentsWindow;
  }
  





  public void updateCommentsWindow(boolean reloadTable)
  {
    if (commentsWindow == null) {
      return;
    }
    commentsWindow.update(reloadTable);
  }
  
  public void word_left_action()
  {
    if (ImageDisplay.activeZones.size() > 0) {
      Zone currZone = ImageDisplay.activeZones.elementAt(0);
      if (currZone.hasContents()) {
        String contents = currZone.getContents();
        int direction = new BidiString(contents, 0).getDirection();
        ImageDisplay.activeZones.removeAllElements();
        
        if ((direction == 0) && (caret < currZone.getContents().length())) {
          int index = contents.substring(0, contents.length() - caret).trim().lastIndexOf(' ');
          if (index == -1) {
            caret = contents.length();
          } else {
            caret = (contents.length() - index);
          }
        }
        if ((direction == 1) && (caret > 0)) {
          contents = contents.substring(contents.length() - caret).trim();
          int index = contents.indexOf(' ');
          if (index == -1) {
            caret = 0;
          } else {
            caret = (contents.length() - index);
          }
        }
        currZone.setSelectedWord(null);
        ImageDisplay.activeZones.add(currZone);
        ImageDisplay.showCaret = true;
        this_interface.getCanvas().paintCanvas();
      }
    }
  }
  
  public void word_right_action() {
    if (ImageDisplay.activeZones.size() > 0) {
      Zone currZone = ImageDisplay.activeZones.elementAt(0);
      if (currZone.hasContents()) {
        String contents = currZone.getContents();
        int direction = new BidiString(contents, 0).getDirection();
        ImageDisplay.activeZones.removeAllElements();
        
        if ((direction == 1) && (caret < currZone.getContents().length())) {
          int index = contents.substring(0, contents.length() - caret).trim().lastIndexOf(' ');
          if (index == -1) {
            caret = contents.length();
          } else {
            caret = (contents.length() - index);
          }
        }
        if ((direction == 0) && (caret > 0)) {
          contents = contents.substring(contents.length() - caret).trim();
          int index = contents.indexOf(' ');
          if (index == -1) {
            caret = 0;
          } else {
            caret = (contents.length() - index);
          }
        }
        currZone.setSelectedWord(null);
        ImageDisplay.activeZones.add(currZone);
        ImageDisplay.showCaret = true;
        this_interface.getCanvas().paintCanvas();
      }
    }
  }
  














  private String deleteSelectedWord(Zone currZone, boolean addExtraSpace)
  {
    String contents = currZone.getContents();
    int oldLength = contents.length();
    int start = currZone.getSelectedWord().getStartPos();
    int end = currZone.getSelectedWord().getEndPos();
    if ((start > -1) && (end > -1)) {
      if ((start == 0) && (end == contents.length())) {
        contents = "";
        caret = 0;
      }
      else {
        if ((end == contents.length()) || (!addExtraSpace)) {
          contents = 
            contents.substring(0, start).trim() + " " + contents.substring(end, contents.length()).trim();
        } else {
          contents = 
            contents.substring(0, start).trim() + " " + " " + contents.substring(end, contents.length()).trim();
        }
        int newLength = contents.length();
        caret -= oldLength - newLength;
      }
      
      currZone.setSelectedWord(null);
    }
    return contents;
  }
  

















  private boolean isSpecialArabicChar(Character c)
  {
    return (c.charValue() == 'َ') || (c.charValue() == 'ً') || (c.charValue() == 'ُ') || (c.charValue() == 'ٌ') || (c.charValue() == 'ِ') || (c.charValue() == 'ٍ') || (c.charValue() == 'ّ') || (c.charValue() == 'ْ') || (c.charValue() == '،') || (c.charValue() == '‘');
  }
  
  public PolyTranscribeToolBar getPolyTranscribePanel() {
    return PolyTranscribePanel;
  }
  











  public void shrinkZones(Vector<DLZone> zoneList, boolean clearSelection, boolean save)
  {
    if (save)
      this_interface.getCanvas().saveCurrentState();
    int blackPixelValue = currentHWObjcurr_canvas.getBlackPixelValue();
    float scale = currentHWObjcurr_canvas.getScale();
    Vector<DLZone> updatedZoneList = new Vector();
    
    setBlankFoundOnShrink(false);
    
    if (this_interface.getConvertRectanglesToPolygonsOnShrink()) {
      ImageDisplay.activeZones.clear();
      for (DLZone zone : zoneList) {
        if ((!(zone instanceof OrientedBox)) && 
          (!(zone instanceof PolygonZone))) {
          Zone newZone = convertRectangleToPolygon((Zone)zone);
          updatedZoneList.add(newZone);
          currentHWObjcurr_canvas.shapeVec.remove(zone);
          ImageDisplay.activeZones.add(newZone);
        }
        else {
          updatedZoneList.add(zone);
          ImageDisplay.activeZones.add((Zone)zone);
        }
      }
      
      updatedZoneList.trimToSize();
      
      new RLE_CC_Handler(blackPixelValue, updatedZoneList, scale, false);
      currentHWObjcurr_canvas.repaint();
    }
    else
    {
      new RLE_CC_Handler(blackPixelValue, zoneList, scale, false);
    }
    System.out.println("ImageDisplay.activeZones: " + ImageDisplay.activeZones.size());
    if (clearSelection) {
      ImageDisplay.activeZones.clear();
    }
    tbdPane.data_panel.a_window.showZoneInfo(ImageDisplay.activeZones);
  }
  






  public void shrinkZones(Vector<DLZone> zoneList, boolean clearSelection)
  {
    shrinkZones(zoneList, clearSelection, true);
  }
  









  public Zone convertRectangleToPolygon(Zone zone)
  {
    int x = zone.get_lt_x();
    int y = zone.get_lt_y();
    int width = zone.get_width();
    int height = zone.get_height();
    
    Vector<Point> points = new Vector(4);
    points.add(new Point(x, y));
    points.add(new Point(x + width, y));
    points.add(new Point(x + width, y + height));
    points.add(new Point(x, y + height));
    


    PolygonZone poly = new PolygonZone(points);
    poly.getZoneTags().putAll(zone.getZoneTags());
    


    zonePage = zonePage;
    zoneID = zoneID;
    

    nextZone = nextZone;
    previousZone = previousZone;
    if (nextZone != null)
      nextZone.previousZone = poly;
    if (previousZone != null) {
      previousZone.nextZone = poly;
    }
    int i = currentHWObjcurr_canvas.shapeVec.indexOf(zone);
    currentHWObjcurr_canvas.shapeVec.add(i, poly);
    
    return poly;
  }
  
  public void setMousePositionLabel(String str)
  {
    if (str == null)
      str = "";
    mousePosistionLabel.setText(str);
  }
  
  public void setMousePositionLabel(Zone zone) {
    if (!(zone instanceof Zone)) {
      return;
    }
    String str = "(" + zone.get_lt_x() + ", " + zone.get_lt_y() + " - " + 
      zone.get_rb_x() + ", " + zone.get_rb_y() + ")  " + 
      "[w:" + zone.get_width() + ", h:" + zone.get_height() + "]";
    
    mousePosistionLabel.setText(str);
    mousePosistionLabel.updateUI();
  }
  
  public void setMousePositionLabel(int x, int y)
  {
    if (currOppmode == 20)
      return;
    mousePosistionLabel.setText("(" + x + ", " + y + ")");
    
    int gray = getGrayLevelValue(x, y, this_interface.getCanvas().getImageRaster());
    String grayStr = gray;
    if (gray == -1)
      grayStr = "";
    mousePosistionLabel.setText(mousePosistionLabel.getText() + " - " + grayStr);
    mousePosistionLabel.updateUI();
  }
  
  public int getGrayLevelValue(int x, int y, Raster raster) {
    Rectangle bounds = raster.getBounds();
    int gray = 0;
    if (!bounds.contains(x, y)) {
      gray = -1;
      return gray;
    }
    
    int bands = raster.getNumBands();
    


    if (bands >= 3)
    {


      int red = raster.getSample(x, y, 0);
      int green = raster.getSample(x, y, 1);
      int blue = raster.getSample(x, y, 2);
      
      gray = (int)(0.2989D * red + 0.587D * green + 0.114D * blue);
    }
    else if (bands == 1) {
      gray = raster.getSample(x, y, 0);
    }
    return gray;
  }
  
  private void copyMeasurementsToClipboard() {
    String[] array1 = mousePosistionLabel.getText().split("\\[");
    String[] array2 = array1[1].trim().split("\\]");
    
    array2[0] = array2[0].trim();
    
    StringSelection stringSelection = new StringSelection(array2[0]);
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents(stringSelection, null);
  }
  
  public TranslateToolBar getTranslateWorkflowPanel() {
    return TranslateWorkflowPanel;
  }
  







  private void splitByOffsets()
  {
    ZonesManager shapeVec = currentHWObjcurr_canvas.getShapeVec();
    Vector<Zone> selectedZones = (Vector)ImageDisplay.activeZones.clone();
    ImageDisplay.activeZones.clear();
    for (Zone zone : selectedZones) {
      if (zone.hasContents()) {
        DLZone prevZone = zone.getPreviousZone();
        DLZone nextZone = zone.getNextZone();
        String contents = ImageAnalyzer.cleanBidiString(zone.getContents());
        contents = contents.replaceAll("\\s+", " ");
        zone.setAttributeValue("contents", contents);
        
        String segmentatioTypeStr = "word";
        int segmentationType = 3;
        
        if (zone.getAttributeValue("segmentation").equals("character")) {
          segmentationType = 2;
          segmentatioTypeStr = "character";
        }
        
        BidiString bs = new BidiString(contents, segmentationType);
        int direction = bs.getDirection();
        int maxOffsets = bs.size();
        
        String firstLine = null;
        if (direction == 0) {
          firstLine = "0";
        } else {
          firstLine = String.valueOf(zone.get_rb_x() - zone.get_lt_x());
        }
        

        TreeSet<Integer> offsetsTreeSet = new TreeSet();
        
        ArrayList<String> _offsets = zone.getOffsetsArray(firstLine, direction, maxOffsets);
        
        if (_offsets.size() != 0)
        {

          shapeVec.remove(zone);
          
          for (String s : _offsets) {
            offsetsTreeSet.add(Integer.valueOf(Integer.parseInt(s)));
          }
          ArrayList<String> offsets = new ArrayList(offsetsTreeSet.size());
          
          for (Integer i : offsetsTreeSet) {
            offsets.add(i.intValue());
          }
          Zone wordZone = null;
          
          for (int i = 0; i < offsets.size(); i++) {
            int nextOffset;
            int offset;
            int nextOffset;
            if (direction == 0) {
              int offset = zone.get_lt_x() + Integer.valueOf((String)offsets.get(i)).intValue();
              int nextOffset; if (i + 1 < offsets.size()) {
                nextOffset = zone.get_lt_x() + Integer.valueOf((String)offsets.get(i + 1)).intValue();
              } else
                nextOffset = zone.get_rb_x();
            } else {
              offset = zone.get_lt_x() + Integer.valueOf((String)offsets.get(offsets.size() - 1 - i)).intValue();
              int nextOffset; if (i + 1 < offsets.size()) {
                nextOffset = zone.get_lt_x() + Integer.valueOf((String)offsets.get(offsets.size() - 2 - i)).intValue();
              } else {
                nextOffset = zone.get_lt_x();
              }
            }
            String txt = bs.getNext(offsets.size() - (i + 1));
            
            if ((txt != null) && (!txt.trim().isEmpty()))
            {

              wordZone = new Zone(Math.min(offset, nextOffset), zone.get_lt_y(), 
                Math.abs(nextOffset - offset), zone.get_rb_y() - zone.get_lt_y());
              String id = zoneID;
              zoneID = id;
              
              if (this_interface.getPreserveAttributesOnSplit()) {
                wordZone.getZoneTags().putAll(zone.getZoneTags());
              }
              
              wordZone.setAttributeValue("id", id);
              wordZone.setZoneType(zone.getZoneType());
              wordZone.setAttributeValue("contents", txt);
              wordZone.setAttributeValue("segmentation", segmentatioTypeStr);
              wordZone.dlSetPagePointer(shapeVec.getPage());
              shapeVec.add(wordZone);
              

              if (getPreserveReadingOrderOnSplit()) {
                if (prevZone != null) {
                  previousZone = prevZone;
                  nextZone = wordZone;
                }
                prevZone = wordZone;
              }
              
              ImageDisplay.activeZones.add(wordZone);
            }
          }
          if (getPreserveReadingOrderOnSplit()) {
            nextZone = nextZone;
            if (nextZone != null) {
              previousZone = wordZone;
            }
          }
        }
      }
    }
    if ((getShrinkAfterSplit()) && 
      (ImageDisplay.activeZones != null) && (!ImageDisplay.activeZones.isEmpty())) {
      Vector<DLZone> toShrink = new Vector(ImageDisplay.activeZones.size());
      toShrink.addAll(ImageDisplay.activeZones);
      shrinkZones(toShrink, false, false);
    }
  }
  










  public void autocreateReadingOrder()
  {
    this_interface.getCanvas().saveCurrentState();
    
    Vector<Zone> selected = ImageDisplay.activeZones;
    Zone incoming = null;
    Zone outcoming = null;
    
    for (int i = 0; i < selected.size(); i++) {
      Zone z = (Zone)selected.get(i);
      if ((previousZone != null) && (!selected.contains(previousZone))) {
        incoming = (Zone)previousZone;
      }
    }
    
    for (int i = selected.size() - 1; i >= 0; i--) {
      Zone z = (Zone)selected.get(i);
      if ((nextZone != null) && (!selected.contains(nextZone))) {
        outcoming = (Zone)nextZone;
        break;
      }
    }
    

    for (int i = 0; i < selected.size() - 1; i++) {
      Zone zone = (Zone)selected.get(i);
      if (previousZone != null) {
        previousZone.nextZone = null;
        previousZone = null;
      }
      
      if (nextZone != null) {
        nextZone.previousZone = null;
        nextZone = null;
      }
    }
    
    for (int i = 0; i < selected.size() - 1; i++) {
      Zone z = (Zone)selected.get(i);
      Zone z_next = (Zone)selected.get(i + 1);
      if (i == 0) {
        if (incoming != null) {
          nextZone = z;
        }
        previousZone = incoming;
      }
      nextZone = z_next;
      previousZone = z;
    }
    
    Zone z = (Zone)selected.get(selected.size() - 1);
    nextZone = outcoming;
    if (outcoming != null) {
      previousZone = z;
    }
    tbdPane.data_panel.a_window.showZoneInfo(ImageDisplay.activeZones);
  }
  
  private HashMap<String, String> loadLookupTable()
  {
    HashMap<String, String> map = new HashMap();
    try {
      String path = System.getProperty("user.dir") + 
        File.separator + "lib" + File.separator + 
        "lookuptable.txt";
      BufferedReader in = new BufferedReader(new java.io.FileReader(path));
      String str;
      while ((str = in.readLine()) != null) { String str;
        if (!str.trim().startsWith("#"))
        {
          String[] parts = str.trim().split(";");
          if (parts.length == 2) {
            map.put(parts[0].trim().toUpperCase(), parts[1].trim().toUpperCase());
          } else
            System.out.println("WARNING: invalid format of the lookup table on the line: " + str.trim());
        }
      }
      in.close();
    } catch (FileNotFoundException e) {
      System.out.println("File not found: " + e.getMessage());
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    return map;
  }
  
  private HashMap<String, String> getMultipleReplacementMap(HashMap<String, String> map) {
    HashMap<String, String> newMap = new HashMap();
    for (Map.Entry<String, String> e : map.entrySet())
      if (((String)e.getKey()).trim().split("\\s+").length > 1)
        newMap.put((String)e.getKey(), (String)e.getValue());
    return newMap;
  }
  
  private String updateWord(String word, HashMap<String, String> map)
  {
    HashMap<String, String> extraMap = getMultipleReplacementMap(map);
    char[] chars = word.trim().toCharArray();
    StringBuffer newWord = null;
    String testStr = "";
    for (char c : chars) {
      String hexString = Integer.toHexString(c);
      if (hexString.length() == 3) {
        hexString = "0" + hexString;
      }
      testStr = testStr + hexString + " ";
    }
    
    testStr = testStr.trim();
    
    for (Map.Entry<String, String> e : extraMap.entrySet()) {
      String key = (String)e.getKey();
      if (testStr.contains(key)) {
        testStr = testStr.replaceAll(key, ((String)e.getValue()).trim());
        
        String[] _chars = testStr.split("\\s+");
        newWord = new StringBuffer();
        for (String c : _chars) {
          int hex = Integer.parseInt(c, 16);
          newWord.append((char)hex);
        }
        word = newWord.toString();
      }
    }
    
    return newWord == null ? word : newWord.toString();
  }
  

















  private void normalizeArabic()
  {
    if (!getNormalizeArabicForm_B()) {
      return;
    }
    HashMap<String, String> map = loadLookupTable();
    

    if (currDoc == null)
      return;
    LinkedList<DLPage> pages = currDocgetDocumentdocumentPages;
    Iterator localIterator2; for (Iterator localIterator1 = pages.iterator(); localIterator1.hasNext(); 
        

        localIterator2.hasNext())
    {
      DLPage page = (DLPage)localIterator1.next();
      LinkedList<DLZone> zones = pageZones;
      
      localIterator2 = zones.iterator(); continue;DLZone zone = (DLZone)localIterator2.next();
      String content = zone.getContents();
      
      boolean form_B = false;
      boolean form_A = false;
      boolean form_unknown = false;
      
      String[] words = content.split("\\s+");
      String newContent = "";
      
      for (String word : words)
      {

        char[] chars = word.toCharArray();
        StringBuffer newWord = new StringBuffer();
        

        for (char c : chars) {
          Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
          if (block == Character.UnicodeBlock.ARABIC_PRESENTATION_FORMS_B) {
            form_B = true;
          }
          else if (block == Character.UnicodeBlock.ARABIC_PRESENTATION_FORMS_A) {
            form_A = true;



          }
          else if ((c >= '฀') && (c <= 63743))
          {


            form_unknown = true;
            System.out.println("Unknown character -> zoneid/hex/decimal: " + 
              zoneID + "/" + Integer.toHexString(c) + "/" + c);
          }
          

          String hexString = Integer.toHexString(c);
          
          if (hexString.length() == 3) {
            hexString = "0" + hexString;
          }
          if (map.containsKey(hexString.toUpperCase())) {
            String[] replaceValues = ((String)map.get(hexString.toUpperCase())).split("\\s+");
            for (String s : replaceValues) {
              int hex = Integer.parseInt(s, 16);
              newWord.append((char)hex);
            }
          }
          else {
            newWord.append(c);
          }
        }
        
        word = newWord.toString();
        
        if (form_B)
        {


          ArabicShaping ar = new ArabicShaping(16);
          try
          {
            word = ar.shape(word);
            word = reverseArabicString(word);
          } catch (ArabicShapingException e) {
            e.printStackTrace();
          }
          newContent = newContent + " " + word;
        }
        else {
          newContent = newContent + " " + word;
        }
      }
      String form = zone.getAttributeValue("form");
      
      if (form == null) {
        form = "";
      }
      if (form_A)
      {
        form = form + ",A"; }
      if (form_B)
        form = form + ",B";
      if (form_unknown) {
        form = form + ",U";
      }
      zone.setAttributeValue("form", form);
      
      zone.setContents(newContent.trim());
      
      currDoc.setModified(true);
    }
  }
  

  public String reverseArabicString(String word)
  {
    boolean regularReverse = true;
    
    String hexStr = getHexString(word);
    String newStr = "";
    







    for (String s : XmlConstant.ARABIC_ALIF_LAM) {
      if (hexStr.trim().contains(s)) {
        regularReverse = false;
      }
    }
    System.out.println("regularReverse: " + regularReverse);
    
    if (regularReverse) {
      StringBuffer s1 = new StringBuffer(word);
      word = s1.reverse().toString();
      newStr = getHexString(word);
    }
    else {
      String[] letters = hexStr.trim().split("\\s+");
      

      for (int i = letters.length - 1; i >= 0; i--) {
        String l_1 = letters[i];
        if (i == 0) {
          newStr = newStr + l_1 + " ";
          break;
        }
        l_2 = letters[(i - 1)];
        pair = l_2 + " " + l_1;
        if (XmlConstant.ARABIC_LIGATURES.contains(pair)) {
          newStr = newStr + pair + " ";
          i--;
          if (i == 0) {
            break;
          }
        } else {
          newStr = newStr + l_1 + " ";
        }
      }
    }
    
    String[] _chars = newStr.trim().split("\\s+");
    StringBuffer newWord = new StringBuffer();
    String[] arrayOfString1; String pair = (arrayOfString1 = _chars).length; for (String l_2 = 0; l_2 < pair; l_2++) { String c = arrayOfString1[l_2];
      int hex = Integer.parseInt(c, 16);
      newWord.append((char)hex);
    }
    word = newWord.toString();
    
    return word;
  }
  
  public String reverseArabicLigature(String word) {
    char[] chars = word.toCharArray();
    String hexStr = "";
    for (char c : chars) {
      hexString = Integer.toHexString(c);
      if (hexString.length() == 3) {
        hexString = "0" + hexString;
      }
      hexStr = hexStr + hexString + " ";
    }
    

    for (Map.Entry<String, String> e : XmlConstant.ARABIC_LIGATURES_MAP.entrySet()) {
      hexStr = hexStr.replaceAll((String)e.getKey(), (String)e.getValue());
    }
    

    String[] _chars = hexStr.trim().split("\\s+");
    StringBuffer newWord = new StringBuffer();
    String[] arrayOfString1; String hexString = (arrayOfString1 = _chars).length; for (String str1 = 0; str1 < hexString; str1++) { String c = arrayOfString1[str1];
      int hex = Integer.parseInt(c, 16);
      newWord.append((char)hex);
    }
    word = newWord.toString();
    
    return word;
  }
  
  private String getHexString(String word) {
    char[] chars = word.toCharArray();
    String hexStr = "";
    for (char c : chars) {
      String hexString = Integer.toHexString(c);
      if (hexString.length() == 3) {
        hexString = "0" + hexString;
      }
      hexStr = hexStr + hexString + " ";
    }
    
    return hexStr.trim();
  }
  
  public JToolBar getThresholdingToolbar() {
    return thresholdingToolbar;
  }
  
  public void setBlankFoundOnShrink(boolean b) {
    blankFoundOnShrink = b;
  }
  
  private void removeOverlap(Zone zone1, Zone zone2) {
    int blackPixelValue = currentHWObjcurr_canvas.getBlackPixelValue();
    float scale = currentHWObjcurr_canvas.getScale();
    Vector<Zone> zoneVec = new Vector(2);
    zoneVec.add(zone1);
    zoneVec.add(zone2);
    new RLE_CC_Handler(blackPixelValue, zoneVec, scale);
  }
}
