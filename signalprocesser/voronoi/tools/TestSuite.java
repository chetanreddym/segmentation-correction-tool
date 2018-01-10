package signalprocesser.voronoi.tools;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import signalprocesser.voronoi.representation.triangulation.TriangulationRepresentation;

public class TestSuite extends javax.swing.JDialog
{
  private static final int NUMBER_OF_COLUMNS = 21;
  private static final String TESTCASE_FILE = "testsuite.save";
  private static final int CUTOFF_NONE = -9001;
  private static final int CUTOFF_MAXEDGEOFMST = -9002;
  private static final int CUTOFF_LARGESTSMALLESTTRIANGLEDGE = -9003;
  private static final int MODE_LENGTH = 1;
  private static final int MODE_NORMALISEDLENGTH = 2;
  private static final int MODE_DENSITY = 3;
  private boolean exitonclose;
  private int mode = 1;
  
  private CountryListModel countrylistmodel;
  
  private TestCaseTableModel testcasemodel;
  private ArrayList<TestCase> testcases = new ArrayList();
  

  private final Object MUTEX = new Object();
  private JButton btnAddTestCase;
  private JButton btnDeleteSelected;
  private JButton btnDensityParameter;
  private JButton btnLengthParameter;
  private JButton btnMoveDown;
  private JButton btnMoveUp;
  private JButton btnNormalisedLengthParameter;
  private JButton btnRunAll;
  private JComboBox cboCountries;
  private JComboBox cboFont;
  private JComboBox cboInternalMinDensity;
  private JComboBox cboInternalPoints;
  private JComboBox cboLengthCutoff;
  private JComboBox cboShapePointMinDensity;
  private JComboBox cboShapePoints;
  private JCheckBox chkAddShapePointsToSplitLongLines;
  
  public TestSuite(boolean _exitonclose, java.awt.Frame parent)
  {
    super(parent, true);
    initComponents();
    exitonclose = _exitonclose;
    

    optLetterGenerationActionPerformed(null);
    
    try
    {
      loadTestcases();
    } catch (IOException e) {
      displayError(e);
      return;
    } catch (ClassNotFoundException e) {
      displayError(e);
      return;
    }
    

    testcasemodel = new TestCaseTableModel();
    tblTestcases.setModel(testcasemodel);
    


    for (int i = 0; i < testcasemodel.getColumnCount(); i++)
    {
      javax.swing.table.TableColumn column = tblTestcases.getColumnModel().getColumn(i);
      if (i == 0) {
        column.setPreferredWidth(140);
      } else {
        column.setPreferredWidth(50);
      }
    }
    try
    {
      cboCountries.setModel(this.countrylistmodel = new CountryListModel(cboCountries, CountryData.getCountryList()));
    }
    catch (IOException e)
    {
      displayError(e);
    }
  }
  
  private JCheckBox chkRunDensity;
  private JCheckBox chkRunLength;
  private JCheckBox chkRunNormalisedLength;
  private javax.swing.ButtonGroup groupGenerationType;
  private JPanel jPanel1;
  private JPanel jPanel2;
  private JPanel jPanel3;
  private JPanel jPanel4;
  private JPanel jPanel5;
  private JPanel jPanel6;
  private JPanel jPanel7;
  private javax.swing.JScrollPane jScrollPane1;
  private JLabel lblDensityIncrements;
  private JLabel lblEndDensity;
  private JLabel lblEndLength;
  
  private void saveTestcases()
    throws IOException
  {
    java.io.ObjectOutputStream stream = null;
    try
    {
      stream = new java.io.ObjectOutputStream(new java.io.FileOutputStream("testsuite.save"));
      stream.writeObject(testcases);
    }
    finally
    {
      if (stream != null) {
        stream.close();
      }
    }
  }
  
  private JLabel lblEndLength1;
  private JLabel lblFont2;
  private JLabel lblGenerationType2;
  private JLabel lblHeight;
  private JLabel lblInternalMinDensity;
  private JLabel lblInternalPoints;
  private JLabel lblLengthCutoff;
  private JLabel lblLengthIncrements;
  private JLabel lblLengthIncrements1;
  private JLabel lblOutputDir;
  private JLabel lblOverall;
  private JLabel lblPixels;
  private JLabel lblPixels1;
  
  private void loadTestcases()
    throws IOException, ClassNotFoundException
  {
    java.io.ObjectInputStream stream = null;
    ArrayList<TestCase> newtestcases;
    try { stream = new java.io.ObjectInputStream(new java.io.FileInputStream("testsuite.save"));
      ArrayList<TestCase> newtestcases = (ArrayList)stream.readObject();
      

      if ((newtestcases == null) || (newtestcases.size() <= 0)) {
        newtestcases = createDefaultSetOfTestCases();
      }
    } catch (java.io.FileNotFoundException e) {
      newtestcases = createDefaultSetOfTestCases();
    }
    catch (java.io.InvalidClassException e) {
      ArrayList<TestCase> newtestcases;
      newtestcases = createDefaultSetOfTestCases();
    } finally { ArrayList<TestCase> newtestcases;
      if (stream != null) { stream.close();
      }
    }
    
    testcases = newtestcases;
    if (testcasemodel != null) {
      testcasemodel.fireTableDataChanged();
    }
  }
  
  private JLabel lblShapePointMinDensity;
  private JLabel lblShapePoints;
  private JLabel lblStartDensity;
  private JLabel lblStartLength;
  
  private ArrayList<TestCase> createDefaultSetOfTestCases()
  {
    int lengthcutoff = 56534;
    int mindensity = 15;
    int numberofpoints = Integer.MAX_VALUE;
    

    ArrayList<TestCase> newtestcases = new ArrayList();
    newtestcases.add(new TestCase("C", "Garamond", lengthcutoff, mindensity, numberofpoints));
    newtestcases.add(new TestCase("S", "Garamond", lengthcutoff, mindensity, numberofpoints));
    newtestcases.add(new TestCase("F", "Garamond", lengthcutoff, mindensity, numberofpoints));
    newtestcases.add(new TestCase("G", "Garamond", lengthcutoff, mindensity, numberofpoints));
    newtestcases.add(new TestCase("Germany.txt", lengthcutoff, mindensity, numberofpoints));
    newtestcases.add(new TestCase("Italy.txt", lengthcutoff, mindensity, numberofpoints));
    newtestcases.add(new TestCase("Thailand.txt", lengthcutoff, mindensity, numberofpoints));
    newtestcases.add(new TestCase("France.txt", lengthcutoff, mindensity, numberofpoints));
    

    return newtestcases;
  }
  
  private JLabel lblStartLength1;
  private JLabel lblStatus;
  private JLabel lblTestcases;
  private JLabel lblTimesToExecuteEachTestcase;
  private JLabel lblWidth;
  private JRadioButton optCountryGeneration;
  private JRadioButton optLetterGeneration;
  private JPanel panelCaptions2;
  
  private void initComponents()
  {
    groupGenerationType = new javax.swing.ButtonGroup();
    panelInCenter = new JPanel();
    scrollPane = new javax.swing.JScrollPane();
    tblTestcases = new JTable();
    panelStatusOutter = new JPanel();
    panelStatus = new JPanel();
    lblStatus = new JLabel();
    jPanel1 = new JPanel();
    lblOverall = new JLabel();
    lblTestcases = new JLabel();
    jPanel5 = new JPanel();
    progressOverall = new JProgressBar();
    progressTestcases = new JProgressBar();
    jScrollPane1 = new javax.swing.JScrollPane();
    panelOnRight = new JPanel();
    panelUpNorth = new JPanel();
    panelVariables = new JPanel();
    panelGenerate2 = new JPanel();
    panelGenerationSelection = new JPanel();
    txtLetter = new JTextField();
    cboCountries = new JComboBox();
    btnAddTestCase = new JButton();
    panelGap13 = new JPanel();
    panelPointOptions2 = new JPanel();
    panelLeft4 = new JPanel();
    lblGenerationType2 = new JLabel();
    lblFont2 = new JLabel();
    lblLengthCutoff = new JLabel();
    lblShapePoints = new JLabel();
    lblInternalPoints = new JLabel();
    lblShapePointMinDensity = new JLabel();
    lblInternalMinDensity = new JLabel();
    panelCenter4 = new JPanel();
    panelGenerationType2 = new JPanel();
    optLetterGeneration = new JRadioButton();
    optCountryGeneration = new JRadioButton();
    cboFont = new JComboBox();
    cboLengthCutoff = new JComboBox();
    cboShapePoints = new JComboBox();
    cboInternalPoints = new JComboBox();
    cboShapePointMinDensity = new JComboBox();
    cboInternalMinDensity = new JComboBox();
    panelGap8 = new JPanel();
    jPanel2 = new JPanel();
    chkAddShapePointsToSplitLongLines = new JCheckBox();
    panelExecutionOptions = new JPanel();
    panelCaptions2 = new JPanel();
    lblWidth = new JLabel();
    lblHeight = new JLabel();
    lblOutputDir = new JLabel();
    lblTimesToExecuteEachTestcase = new JLabel();
    panelTextfields = new JPanel();
    jPanel6 = new JPanel();
    txtWidth = new JTextField();
    lblPixels = new JLabel();
    jPanel7 = new JPanel();
    txtHeight = new JTextField();
    lblPixels1 = new JLabel();
    txtOutputDir = new JTextField();
    txtTimesToExecuteEachTestcase = new JTextField();
    panelOtherOptions = new JPanel();
    jPanel3 = new JPanel();
    btnDeleteSelected = new JButton();
    panelGap14 = new JPanel();
    jPanel4 = new JPanel();
    btnMoveUp = new JButton();
    btnMoveDown = new JButton();
    tabsTestCaseType = new JTabbedPane();
    panelLengthParameter = new JPanel();
    panelCenterPanel = new JPanel();
    panelNorth = new JPanel();
    panelLengthParameters = new JPanel();
    panelLeft = new JPanel();
    lblStartLength = new JLabel();
    lblEndLength = new JLabel();
    lblLengthIncrements = new JLabel();
    panelCenter = new JPanel();
    txtStartLength = new JTextField();
    txtEndLength = new JTextField();
    txtLengthIncrements = new JTextField();
    btnLengthParameter = new JButton();
    panelGap1 = new JPanel();
    panelGap2 = new JPanel();
    panelGap3 = new JPanel();
    panelGap4 = new JPanel();
    panelNormalisedLengthParameter = new JPanel();
    panelCenterPanel1 = new JPanel();
    panelNorth1 = new JPanel();
    panelLengthParameters1 = new JPanel();
    panelLeft1 = new JPanel();
    lblStartLength1 = new JLabel();
    lblEndLength1 = new JLabel();
    lblLengthIncrements1 = new JLabel();
    panelCenter1 = new JPanel();
    txtNormalisedStartLength = new JTextField();
    txtNormalisedEndLength = new JTextField();
    txtNormalisedLengthIncrements = new JTextField();
    btnNormalisedLengthParameter = new JButton();
    panelGap5 = new JPanel();
    panelGap6 = new JPanel();
    panelGap7 = new JPanel();
    panelGap15 = new JPanel();
    panelDensityParameter = new JPanel();
    panelCenterPanel2 = new JPanel();
    panelNorth2 = new JPanel();
    panelLengthParameters2 = new JPanel();
    panelLeft2 = new JPanel();
    lblStartDensity = new JLabel();
    lblEndDensity = new JLabel();
    lblDensityIncrements = new JLabel();
    panelCenter2 = new JPanel();
    txtStartDensity = new JTextField();
    txtEndDensity = new JTextField();
    txtDensityIncrements = new JTextField();
    btnDensityParameter = new JButton();
    panelGap9 = new JPanel();
    panelGap10 = new JPanel();
    panelGap11 = new JPanel();
    panelGap12 = new JPanel();
    panelRunAll = new JPanel();
    panelNorth3 = new JPanel();
    panelRunAllCheckBoxes = new JPanel();
    chkRunLength = new JCheckBox();
    chkRunNormalisedLength = new JCheckBox();
    chkRunDensity = new JCheckBox();
    btnRunAll = new JButton();
    panelGap16 = new JPanel();
    
    setDefaultCloseOperation(2);
    setTitle("Test Suite");
    addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosed(java.awt.event.WindowEvent evt) {
        TestSuite.this.formWindowClosed(evt);
      }
      
    });
    panelInCenter.setLayout(new BorderLayout());
    
    scrollPane.setVerticalScrollBarPolicy(22);
    tblTestcases.setFont(new Font("Arial", 0, 12));
    tblTestcases.setIntercellSpacing(new Dimension(1, 3));
    scrollPane.setViewportView(tblTestcases);
    
    panelInCenter.add(scrollPane, "Center");
    
    panelStatusOutter.setLayout(new BorderLayout());
    
    panelStatusOutter.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    panelStatus.setLayout(new BorderLayout());
    
    panelStatus.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
    panelStatus.add(lblStatus, "North");
    
    jPanel1.setLayout(new GridLayout(0, 1, 0, 2));
    
    lblOverall.setText("Overall:");
    jPanel1.add(lblOverall);
    
    lblTestcases.setText("Testcase:");
    jPanel1.add(lblTestcases);
    
    panelStatus.add(jPanel1, "West");
    
    jPanel5.setLayout(new GridLayout(0, 1, 0, 2));
    
    jPanel5.add(progressOverall);
    
    jPanel5.add(progressTestcases);
    
    panelStatus.add(jPanel5, "Center");
    
    panelStatusOutter.add(panelStatus, "Center");
    
    panelInCenter.add(panelStatusOutter, "South");
    
    getContentPane().add(panelInCenter, "Center");
    
    jScrollPane1.setHorizontalScrollBarPolicy(31);
    jScrollPane1.setVerticalScrollBarPolicy(22);
    panelOnRight.setLayout(new BorderLayout());
    
    panelOnRight.setBorder(new javax.swing.border.SoftBevelBorder(0));
    panelUpNorth.setLayout(new javax.swing.BoxLayout(panelUpNorth, 1));
    
    panelVariables.setLayout(new javax.swing.BoxLayout(panelVariables, 1));
    
    panelVariables.setBorder(javax.swing.BorderFactory.createTitledBorder("Testcase Details"));
    panelGenerate2.setLayout(new BorderLayout(3, 0));
    
    panelGenerationSelection.setLayout(new GridLayout(0, 1));
    
    txtLetter.setFont(new Font("Tahoma", 1, 12));
    txtLetter.setHorizontalAlignment(0);
    txtLetter.setText("S");
    panelGenerationSelection.add(txtLetter);
    
    panelGenerationSelection.add(cboCountries);
    
    panelGenerate2.add(panelGenerationSelection, "Center");
    
    btnAddTestCase.setText("Add Testcase");
    btnAddTestCase.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        TestSuite.this.btnAddTestCaseActionPerformed(evt);
      }
      
    });
    panelGenerate2.add(btnAddTestCase, "East");
    
    panelVariables.add(panelGenerate2);
    
    panelGap13.setLayout(null);
    
    panelGap13.setPreferredSize(new Dimension(4, 4));
    panelVariables.add(panelGap13);
    
    panelPointOptions2.setLayout(new BorderLayout(2, 0));
    
    panelLeft4.setLayout(new GridLayout(0, 1, 0, 2));
    
    lblGenerationType2.setText("Generation Type:");
    panelLeft4.add(lblGenerationType2);
    
    lblFont2.setText("Font:");
    panelLeft4.add(lblFont2);
    
    lblLengthCutoff.setText("Length Cut-off:");
    panelLeft4.add(lblLengthCutoff);
    
    lblShapePoints.setText("Shape Points:");
    panelLeft4.add(lblShapePoints);
    
    lblInternalPoints.setText("Internal Points:");
    panelLeft4.add(lblInternalPoints);
    
    lblShapePointMinDensity.setText("Shape Point Min Density:");
    panelLeft4.add(lblShapePointMinDensity);
    
    lblInternalMinDensity.setText("Internal Min Density:");
    panelLeft4.add(lblInternalMinDensity);
    
    panelPointOptions2.add(panelLeft4, "West");
    
    panelCenter4.setLayout(new GridLayout(0, 1, 0, 2));
    
    panelGenerationType2.setLayout(new BorderLayout());
    
    groupGenerationType.add(optLetterGeneration);
    optLetterGeneration.setSelected(true);
    optLetterGeneration.setText("Letter");
    optLetterGeneration.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        TestSuite.this.optLetterGenerationActionPerformed(evt);
      }
      
    });
    panelGenerationType2.add(optLetterGeneration, "West");
    
    groupGenerationType.add(optCountryGeneration);
    optCountryGeneration.setText("Country");
    optCountryGeneration.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        TestSuite.this.optCountryGenerationActionPerformed(evt);
      }
      
    });
    panelGenerationType2.add(optCountryGeneration, "Center");
    
    panelCenter4.add(panelGenerationType2);
    
    cboFont.setEditable(true);
    cboFont.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Arial", "Courier New", "Garamond", "Times New Roman", "Lucida Console" }));
    cboFont.setSelectedIndex(2);
    panelCenter4.add(cboFont);
    
    cboLengthCutoff.setEditable(true);
    cboLengthCutoff.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "No Length Cut-off", "Max Edge of MST", "Largest Smallest Triangle Edge", "5", "10", "15", "etc" }));
    cboLengthCutoff.setSelectedIndex(1);
    panelCenter4.add(cboLengthCutoff);
    
    cboShapePoints.setEditable(true);
    cboShapePoints.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "No Points", "10", "25", "50", "100", "250", "Maximum Possible" }));
    cboShapePoints.setSelectedIndex(6);
    panelCenter4.add(cboShapePoints);
    
    cboInternalPoints.setEditable(true);
    cboInternalPoints.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "No Points", "10", "25", "50", "100", "250", "Maximum Possible" }));
    cboInternalPoints.setSelectedIndex(6);
    panelCenter4.add(cboInternalPoints);
    
    cboShapePointMinDensity.setEditable(true);
    cboShapePointMinDensity.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "8", "10", "12", "15", "20", "25", "30", "40", "50", "100", "250" }));
    cboShapePointMinDensity.setSelectedIndex(9);
    panelCenter4.add(cboShapePointMinDensity);
    
    cboInternalMinDensity.setEditable(true);
    cboInternalMinDensity.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "8", "10", "12", "15", "20", "25", "30", "40", "50", "100", "250" }));
    cboInternalMinDensity.setSelectedIndex(9);
    panelCenter4.add(cboInternalMinDensity);
    
    panelPointOptions2.add(panelCenter4, "Center");
    
    panelVariables.add(panelPointOptions2);
    
    panelGap8.setLayout(null);
    
    panelGap8.setPreferredSize(new Dimension(4, 4));
    panelVariables.add(panelGap8);
    
    jPanel2.setLayout(new BorderLayout());
    
    chkAddShapePointsToSplitLongLines.setSelected(true);
    chkAddShapePointsToSplitLongLines.setText("Add Shape Points to Split Long Lines");
    jPanel2.add(chkAddShapePointsToSplitLongLines, "Center");
    
    panelVariables.add(jPanel2);
    
    panelUpNorth.add(panelVariables);
    
    panelExecutionOptions.setLayout(new BorderLayout(3, 0));
    
    panelExecutionOptions.setBorder(javax.swing.BorderFactory.createTitledBorder("Execution Options"));
    panelCaptions2.setLayout(new GridLayout(0, 1));
    
    lblWidth.setText("Width:");
    panelCaptions2.add(lblWidth);
    
    lblHeight.setText("Height:");
    panelCaptions2.add(lblHeight);
    
    lblOutputDir.setText("Output Dir:");
    panelCaptions2.add(lblOutputDir);
    
    lblTimesToExecuteEachTestcase.setText("Times to Execute Each:");
    panelCaptions2.add(lblTimesToExecuteEachTestcase);
    
    panelExecutionOptions.add(panelCaptions2, "West");
    
    panelTextfields.setLayout(new GridLayout(0, 1, 0, 2));
    
    jPanel6.setLayout(new BorderLayout(3, 0));
    
    txtWidth.setText("1000");
    jPanel6.add(txtWidth, "Center");
    
    lblPixels.setText("pixels");
    jPanel6.add(lblPixels, "East");
    
    panelTextfields.add(jPanel6);
    
    jPanel7.setLayout(new BorderLayout(3, 0));
    
    txtHeight.setText("1000");
    jPanel7.add(txtHeight, "Center");
    
    lblPixels1.setText("pixels");
    jPanel7.add(lblPixels1, "East");
    
    panelTextfields.add(jPanel7);
    
    txtOutputDir.setText("testcases");
    panelTextfields.add(txtOutputDir);
    
    txtTimesToExecuteEachTestcase.setText("50");
    panelTextfields.add(txtTimesToExecuteEachTestcase);
    
    panelExecutionOptions.add(panelTextfields, "Center");
    
    panelUpNorth.add(panelExecutionOptions);
    
    panelOtherOptions.setLayout(new javax.swing.BoxLayout(panelOtherOptions, 1));
    
    panelOtherOptions.setBorder(javax.swing.BorderFactory.createTitledBorder("Other Options"));
    jPanel3.setLayout(new BorderLayout());
    
    btnDeleteSelected.setText("Remove Selected");
    btnDeleteSelected.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        TestSuite.this.btnDeleteSelectedActionPerformed(evt);
      }
      
    });
    jPanel3.add(btnDeleteSelected, "Center");
    
    panelOtherOptions.add(jPanel3);
    
    panelGap14.setLayout(null);
    
    panelGap14.setPreferredSize(new Dimension(5, 5));
    panelOtherOptions.add(panelGap14);
    
    jPanel4.setLayout(new BorderLayout(0, 3));
    
    btnMoveUp.setText("Move Up");
    btnMoveUp.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        TestSuite.this.btnMoveUpActionPerformed(evt);
      }
      
    });
    jPanel4.add(btnMoveUp, "North");
    
    btnMoveDown.setText("Move Down");
    btnMoveDown.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        TestSuite.this.btnMoveDownActionPerformed(evt);
      }
      
    });
    jPanel4.add(btnMoveDown, "Center");
    
    panelOtherOptions.add(jPanel4);
    
    panelUpNorth.add(panelOtherOptions);
    
    panelOnRight.add(panelUpNorth, "North");
    
    tabsTestCaseType.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        TestSuite.this.tabsTestCaseTypeStateChanged(evt);
      }
      
    });
    panelLengthParameter.setLayout(new BorderLayout());
    
    panelCenterPanel.setLayout(new BorderLayout());
    
    panelNorth.setLayout(new BorderLayout(0, 5));
    
    panelLengthParameters.setLayout(new BorderLayout(4, 0));
    
    panelLengthParameters.setBorder(javax.swing.BorderFactory.createTitledBorder("Length Details"));
    panelLeft.setLayout(new GridLayout(0, 1, 0, 3));
    
    lblStartLength.setHorizontalAlignment(4);
    lblStartLength.setText("Start Length:");
    panelLeft.add(lblStartLength);
    
    lblEndLength.setHorizontalAlignment(4);
    lblEndLength.setText("End Length:");
    panelLeft.add(lblEndLength);
    
    lblLengthIncrements.setHorizontalAlignment(4);
    lblLengthIncrements.setText("Length Increments:");
    panelLeft.add(lblLengthIncrements);
    
    panelLengthParameters.add(panelLeft, "West");
    
    panelCenter.setLayout(new GridLayout(0, 1, 0, 3));
    
    txtStartLength.setColumns(4);
    txtStartLength.setText("10");
    panelCenter.add(txtStartLength);
    
    txtEndLength.setColumns(4);
    txtEndLength.setText("90");
    panelCenter.add(txtEndLength);
    
    txtLengthIncrements.setColumns(4);
    txtLengthIncrements.setText("5");
    panelCenter.add(txtLengthIncrements);
    
    panelLengthParameters.add(panelCenter, "Center");
    
    panelNorth.add(panelLengthParameters, "North");
    
    btnLengthParameter.setText("Execute Testsuite");
    btnLengthParameter.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        TestSuite.this.btnLengthParameterActionPerformed(evt);
      }
      
    });
    panelNorth.add(btnLengthParameter, "Center");
    
    panelCenterPanel.add(panelNorth, "North");
    
    panelLengthParameter.add(panelCenterPanel, "Center");
    
    panelGap1.setLayout(null);
    
    panelGap1.setPreferredSize(new Dimension(6, 6));
    panelLengthParameter.add(panelGap1, "North");
    
    panelGap2.setLayout(null);
    
    panelGap2.setPreferredSize(new Dimension(5, 5));
    panelLengthParameter.add(panelGap2, "West");
    
    panelGap3.setLayout(null);
    
    panelGap3.setPreferredSize(new Dimension(5, 5));
    panelLengthParameter.add(panelGap3, "East");
    
    panelGap4.setLayout(null);
    
    panelGap4.setPreferredSize(new Dimension(8, 8));
    panelLengthParameter.add(panelGap4, "South");
    
    tabsTestCaseType.addTab("Length", panelLengthParameter);
    
    panelNormalisedLengthParameter.setLayout(new BorderLayout());
    
    panelCenterPanel1.setLayout(new BorderLayout());
    
    panelNorth1.setLayout(new BorderLayout(0, 5));
    
    panelLengthParameters1.setLayout(new BorderLayout(4, 0));
    
    panelLengthParameters1.setBorder(javax.swing.BorderFactory.createTitledBorder("Normalised Length Details"));
    panelLeft1.setLayout(new GridLayout(0, 1, 0, 3));
    
    lblStartLength1.setHorizontalAlignment(4);
    lblStartLength1.setText("Start Length:");
    panelLeft1.add(lblStartLength1);
    
    lblEndLength1.setHorizontalAlignment(4);
    lblEndLength1.setText("End Length:");
    panelLeft1.add(lblEndLength1);
    
    lblLengthIncrements1.setHorizontalAlignment(4);
    lblLengthIncrements1.setText("Length Increments:");
    panelLeft1.add(lblLengthIncrements1);
    
    panelLengthParameters1.add(panelLeft1, "West");
    
    panelCenter1.setLayout(new GridLayout(0, 1, 0, 3));
    
    txtNormalisedStartLength.setColumns(4);
    txtNormalisedStartLength.setText("0.00");
    panelCenter1.add(txtNormalisedStartLength);
    
    txtNormalisedEndLength.setColumns(4);
    txtNormalisedEndLength.setText("1.00");
    panelCenter1.add(txtNormalisedEndLength);
    
    txtNormalisedLengthIncrements.setColumns(4);
    txtNormalisedLengthIncrements.setText("0.05");
    panelCenter1.add(txtNormalisedLengthIncrements);
    
    panelLengthParameters1.add(panelCenter1, "Center");
    
    panelNorth1.add(panelLengthParameters1, "North");
    
    btnNormalisedLengthParameter.setText("Execute Testsuite");
    btnNormalisedLengthParameter.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        TestSuite.this.btnNormalisedLengthParameterActionPerformed(evt);
      }
      
    });
    panelNorth1.add(btnNormalisedLengthParameter, "Center");
    
    panelCenterPanel1.add(panelNorth1, "North");
    
    panelNormalisedLengthParameter.add(panelCenterPanel1, "Center");
    
    panelGap5.setLayout(null);
    
    panelGap5.setPreferredSize(new Dimension(6, 6));
    panelNormalisedLengthParameter.add(panelGap5, "North");
    
    panelGap6.setLayout(null);
    
    panelGap6.setPreferredSize(new Dimension(5, 5));
    panelNormalisedLengthParameter.add(panelGap6, "West");
    
    panelGap7.setLayout(null);
    
    panelGap7.setPreferredSize(new Dimension(5, 5));
    panelNormalisedLengthParameter.add(panelGap7, "East");
    
    panelGap15.setLayout(null);
    
    panelGap15.setPreferredSize(new Dimension(8, 8));
    panelNormalisedLengthParameter.add(panelGap15, "South");
    
    tabsTestCaseType.addTab("Normalised Length", panelNormalisedLengthParameter);
    
    panelDensityParameter.setLayout(new BorderLayout());
    
    panelCenterPanel2.setLayout(new BorderLayout());
    
    panelNorth2.setLayout(new BorderLayout(0, 5));
    
    panelLengthParameters2.setLayout(new BorderLayout(4, 0));
    
    panelLengthParameters2.setBorder(javax.swing.BorderFactory.createTitledBorder("Density Details"));
    panelLeft2.setLayout(new GridLayout(0, 1, 0, 3));
    
    lblStartDensity.setHorizontalAlignment(4);
    lblStartDensity.setText("Start Density:");
    panelLeft2.add(lblStartDensity);
    
    lblEndDensity.setHorizontalAlignment(4);
    lblEndDensity.setText("End Density:");
    panelLeft2.add(lblEndDensity);
    
    lblDensityIncrements.setHorizontalAlignment(4);
    lblDensityIncrements.setText("Density Increments:");
    panelLeft2.add(lblDensityIncrements);
    
    panelLengthParameters2.add(panelLeft2, "West");
    
    panelCenter2.setLayout(new GridLayout(0, 1, 0, 3));
    
    txtStartDensity.setColumns(4);
    txtStartDensity.setText("10");
    panelCenter2.add(txtStartDensity);
    
    txtEndDensity.setColumns(4);
    txtEndDensity.setText("45");
    panelCenter2.add(txtEndDensity);
    
    txtDensityIncrements.setColumns(4);
    txtDensityIncrements.setText("5");
    panelCenter2.add(txtDensityIncrements);
    
    panelLengthParameters2.add(panelCenter2, "Center");
    
    panelNorth2.add(panelLengthParameters2, "North");
    
    btnDensityParameter.setText("Execute Testsuite");
    btnDensityParameter.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        TestSuite.this.btnDensityParameterActionPerformed(evt);
      }
      
    });
    panelNorth2.add(btnDensityParameter, "Center");
    
    panelCenterPanel2.add(panelNorth2, "North");
    
    panelDensityParameter.add(panelCenterPanel2, "Center");
    
    panelGap9.setLayout(null);
    
    panelGap9.setPreferredSize(new Dimension(6, 6));
    panelDensityParameter.add(panelGap9, "North");
    
    panelGap10.setLayout(null);
    
    panelGap10.setPreferredSize(new Dimension(5, 5));
    panelDensityParameter.add(panelGap10, "West");
    
    panelGap11.setLayout(null);
    
    panelGap11.setPreferredSize(new Dimension(5, 5));
    panelDensityParameter.add(panelGap11, "East");
    
    panelGap12.setLayout(null);
    
    panelGap12.setPreferredSize(new Dimension(8, 8));
    panelDensityParameter.add(panelGap12, "South");
    
    tabsTestCaseType.addTab("Density", panelDensityParameter);
    
    panelRunAll.setLayout(new java.awt.GridBagLayout());
    
    panelNorth3.setLayout(new BorderLayout(0, 6));
    
    panelRunAllCheckBoxes.setLayout(new GridLayout(0, 1, 0, 4));
    
    chkRunLength.setText("Run Length Test Suite");
    panelRunAllCheckBoxes.add(chkRunLength);
    
    chkRunNormalisedLength.setSelected(true);
    chkRunNormalisedLength.setText("Run Normalised Length Test Suite");
    panelRunAllCheckBoxes.add(chkRunNormalisedLength);
    
    chkRunDensity.setSelected(true);
    chkRunDensity.setText("Run Density Test Suite");
    panelRunAllCheckBoxes.add(chkRunDensity);
    
    panelNorth3.add(panelRunAllCheckBoxes, "North");
    
    btnRunAll.setText("Execute Testsuite");
    btnRunAll.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        TestSuite.this.btnRunAllActionPerformed(evt);
      }
      
    });
    panelNorth3.add(btnRunAll, "Center");
    
    panelGap16.setLayout(null);
    
    panelNorth3.add(panelGap16, "South");
    
    panelRunAll.add(panelNorth3, new java.awt.GridBagConstraints());
    
    tabsTestCaseType.addTab("Run All/Selection", panelRunAll);
    
    panelOnRight.add(tabsTestCaseType, "South");
    
    jScrollPane1.setViewportView(panelOnRight);
    
    getContentPane().add(jScrollPane1, "East");
    
    Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
    setBounds((width - 845) / 2, (height - 730) / 2, 845, 730);
  }
  
  private JPanel panelCenter;
  private JPanel panelCenter1;
  private JPanel panelCenter2;
  private JPanel panelCenter4;
  private JPanel panelCenterPanel;
  private JPanel panelCenterPanel1;
  private JPanel panelCenterPanel2;
  private JPanel panelDensityParameter;
  private JPanel panelExecutionOptions;
  private JPanel panelGap1;
  private JPanel panelGap10;
  private JPanel panelGap11;
  private JPanel panelGap12;
  private JPanel panelGap13;
  private JPanel panelGap14;
  private JPanel panelGap15;
  private JPanel panelGap16;
  private JPanel panelGap2;
  private JPanel panelGap3;
  private JPanel panelGap4;
  private JPanel panelGap5;
  private JPanel panelGap6;
  private JPanel panelGap7;
  private JPanel panelGap8;
  
  private void btnRunAllActionPerformed(ActionEvent evt)
  {
    new Thread()
    {
      public void run()
      {
        try
        {
          if (chkRunLength.isSelected())
          {
            tabsTestCaseType.setSelectedComponent(panelLengthParameter);
            

            btnLengthParameter.doClick();
            

            synchronized (MUTEX) {
              MUTEX.wait();
            }
          }
          
          if (chkRunNormalisedLength.isSelected())
          {
            tabsTestCaseType.setSelectedComponent(panelNormalisedLengthParameter);
            

            btnNormalisedLengthParameter.doClick();
            

            synchronized (MUTEX) {
              MUTEX.wait();
            }
          }
          
          if (chkRunDensity.isSelected())
          {
            tabsTestCaseType.setSelectedComponent(panelDensityParameter);
            

            btnDensityParameter.doClick();
            

            synchronized (MUTEX)
            {
              MUTEX.wait();
            }
          }
        }
        catch (InterruptedException e)
        {
          TestSuite.this.displayError(e);
        }
      }
    }.start();
  }
  
  private JPanel panelGap9;
  private JPanel panelGenerate2;
  private JPanel panelGenerationSelection;
  private JPanel panelGenerationType2;
  private JPanel panelInCenter;
  private JPanel panelLeft;
  private JPanel panelLeft1;
  private void btnNormalisedLengthParameterActionPerformed(ActionEvent evt)
  {
    try
    {
      double start = Double.parseDouble(txtNormalisedStartLength.getText());
      final double end = Double.parseDouble(txtNormalisedEndLength.getText());
      final double incr = Double.parseDouble(txtNormalisedLengthIncrements.getText());
      if (start >= end) {
        displayError("Start value must be less than end value");
        return; }
      if ((start < 0.0D) || (start > 1.0D)) {
        displayError("Start value must be between 0 and 1");
        return; }
      if ((end < 0.0D) || (end > 1.0D)) {
        displayError("End value must be between 0 and 1");
        return; }
      if ((incr <= 0.0D) || (incr >= 1.0D)) {
        displayError("Increment must be between 0 and 1");
        return;
      }
      runTestSuite(new RunTestSeries(end)
      {
        public boolean runTestSeries(JProgressBar progress, TestSuite.TestCase testcase, int width, int height, File outputdir, int timestoexecute)
          throws IOException
        {
          progress.setMinimum(1);
          progress.setMaximum((int)(((end - incr) / val$incr + 1.0D) * timestoexecute));
          progress.setValue(1);
          

          String prepend = outputdir.getAbsolutePath() + File.separator;
          
          if ((TestSuite.TestCase.access$0(testcase) != null) && (TestSuite.TestCase.access$1(testcase) != null) && (TestSuite.TestCase.access$1(testcase).trim().length() == 1)) {
            String testcasename = TestSuite.TestCase.access$1(testcase) + " (" + TestSuite.TestCase.access$0(testcase).getName() + ")";
            prepend = prepend + "normalisedlength-" + TestSuite.TestCase.access$1(testcase) + "-" + TestSuite.TestCase.access$0(testcase).getName();
          } else if ((TestSuite.TestCase.access$2(testcase) != null) && (TestSuite.TestCase.access$2(testcase).trim().length() >= 1)) {
            String testcasename = CountryListModel.formatHumanReadable(TestSuite.TestCase.access$2(testcase));
            prepend = prepend + "normalisedlength-" + testcasename;
          } else {
            TestSuite.this.displayError("Unknown shape to generate for testcase");
            return false;
          }
          
          String testcasename;
          BufferedWriter summarywriter = new BufferedWriter(new java.io.FileWriter(prepend + ".csv"));
          

          TestSuite.ValueCollector values = new TestSuite.ValueCollector(TestSuite.this, 21);
          


          boolean isfirst = true;
          for (double normalisedlength = incr; normalisedlength <= end; normalisedlength += val$incr)
          {
            values.reset();
            

            BufferedWriter writer = new BufferedWriter(new java.io.FileWriter(prepend + "-" + normalisedlength + ".csv"));
            

            TestSuite.this.writeHeadingRow(writer, values);
            

            final double finalnormalisedlength = normalisedlength;
            signalprocesser.voronoi.representation.triangulation.TriangulationRepresentation.CalcCutOff cutoffcalc = new signalprocesser.voronoi.representation.triangulation.TriangulationRepresentation.CalcCutOff() {
              public int calculateCutOff(TriangulationRepresentation rep) {
                int min = rep.getMinLength();
                int max = rep.getMaxLength();
                return (int)(finalnormalisedlength * (max - min) + min);
              }
            };
            

            for (int x = 1; x <= timestoexecute; x++)
            {
              progress.setValue((int)((normalisedlength - incr) / val$incr * timestoexecute + x));
              lblStatus.setText("Executing test case \"" + testcasename + "\" at normalised length " + normalisedlength + " - " + x + " of " + timestoexecute + " times");
              

              boolean success = TestSuite.this.executeTestCase(writer, values, width, height, 
                TestSuite.TestCase.access$0(testcase), TestSuite.TestCase.access$1(testcase), TestSuite.TestCase.access$2(testcase), 
                TestSuite.TestCase.access$4(testcase), TestSuite.TestCase.access$6(testcase), 
                TestSuite.TestCase.access$5(testcase), TestSuite.TestCase.access$7(testcase), 
                -1, cutoffcalc, 
                TestSuite.TestCase.access$8(testcase));
              if (!success) {
                return false;
              }
            }
            

            writer.flush();
            writer.close();
            

            values.writeSummary(summarywriter, isfirst);
            if (isfirst) { isfirst = false;
            }
          }
          
          summarywriter.flush();
          summarywriter.close();
          

          return true;
        }
      });
    }
    catch (NumberFormatException e)
    {
      displayError("Failed to parse length paramter - only integers are valid");
      return;
    }
  }
  
  private void formWindowClosed(java.awt.event.WindowEvent evt)
  {
    if (exitonclose) {
      System.exit(0);
    }
  }
  
  private void tabsTestCaseTypeStateChanged(javax.swing.event.ChangeEvent evt)
  {
    java.awt.Component component = tabsTestCaseType.getSelectedComponent();
    if (component == panelLengthParameter) {
      mode = 1;
      if (testcasemodel != null) {
        testcasemodel.fireTableDataChanged();
      }
    } else if (component == panelNormalisedLengthParameter) {
      mode = 2;
      if (testcasemodel != null) {
        testcasemodel.fireTableDataChanged();
      }
    } else if (component == panelDensityParameter) {
      mode = 3;
      if (testcasemodel != null) {
        testcasemodel.fireTableDataChanged();
      }
    } else if (component == panelRunAll)
    {
      mode = -1;
      if (testcasemodel != null) {
        testcasemodel.fireTableDataChanged();
      }
    }
    else
    {
      displayError("Unknown tab selected");
      return;
    }
  }
  
  private void btnMoveUpActionPerformed(ActionEvent evt)
  {
    int[] selected = tblTestcases.getSelectedRows();
    if ((selected == null) || (selected.length == 0)) {
      displayError("Please selected value to move");
    } else if (selected.length != 1)
    {
      displayError("You can only move only value at a time");
    }
    else
    {
      int index = selected[0];
      if (index <= 0) {
        return;
      }
      TestCase testcase = (TestCase)testcases.remove(index);
      testcases.add(index - 1, testcase);
      


      testcasemodel.fireTableDataChanged();
      

      tblTestcases.setRowSelectionInterval(index - 1, index - 1);
      try
      {
        saveTestcases();
      }
      catch (IOException e)
      {
        displayError(e);
        return;
      }
    }
  }
  
  private void btnMoveDownActionPerformed(ActionEvent evt)
  {
    int[] selected = tblTestcases.getSelectedRows();
    if ((selected == null) || (selected.length == 0)) {
      displayError("Please selected value to move");
    } else if (selected.length != 1)
    {
      displayError("You can only move only value at a time");
    }
    else
    {
      int index = selected[0];
      if (index >= testcases.size() - 1) {
        return;
      }
      TestCase testcase = (TestCase)testcases.remove(index);
      testcases.add(index + 1, testcase);
      


      testcasemodel.fireTableDataChanged();
      

      tblTestcases.setRowSelectionInterval(index + 1, index + 1);
      try
      {
        saveTestcases();
      }
      catch (IOException e)
      {
        displayError(e);
        return;
      }
    }
  }
  
  private void btnDeleteSelectedActionPerformed(ActionEvent evt)
  {
    int[] selected = tblTestcases.getSelectedRows();
    if ((selected == null) || (selected.length == 0))
    {
      displayError("Please selected value/values to remove");
    }
    else
    {
      java.util.Arrays.sort(selected);
      

      for (int x = selected.length - 1; x >= 0; x--) {
        testcases.remove(selected[x]);
      }
      

      testcasemodel.fireTableDataChanged();
      try
      {
        saveTestcases();
      }
      catch (IOException e)
      {
        displayError(e);
        return;
      }
    }
  }
  
  private void btnDensityParameterActionPerformed(ActionEvent evt)
  {
    try
    {
      final int start = Integer.parseInt(txtStartDensity.getText());
      final int end = Integer.parseInt(txtEndDensity.getText());
      final int incr = Integer.parseInt(txtDensityIncrements.getText());
      if (start >= end) {
        displayError("Start value must be less than end value");
        return;
      }
      runTestSuite(new RunTestSeries(end)
      {
        public boolean runTestSeries(JProgressBar progress, TestSuite.TestCase testcase, int width, int height, File outputdir, int timestoexecute)
          throws IOException
        {
          progress.setMinimum(1);
          progress.setMaximum(((end - start) / incr + 1) * timestoexecute);
          progress.setValue(1);
          

          String prepend = outputdir.getAbsolutePath() + File.separator;
          
          if ((TestSuite.TestCase.access$0(testcase) != null) && (TestSuite.TestCase.access$1(testcase) != null) && (TestSuite.TestCase.access$1(testcase).trim().length() == 1)) {
            String testcasename = TestSuite.TestCase.access$1(testcase) + " (" + TestSuite.TestCase.access$0(testcase).getName() + ")";
            prepend = prepend + "density-" + TestSuite.TestCase.access$1(testcase) + "-" + TestSuite.TestCase.access$0(testcase).getName();
          } else if ((TestSuite.TestCase.access$2(testcase) != null) && (TestSuite.TestCase.access$2(testcase).trim().length() >= 1)) {
            String testcasename = CountryListModel.formatHumanReadable(TestSuite.TestCase.access$2(testcase));
            prepend = prepend + "density-" + testcasename;
          } else {
            TestSuite.this.displayError("Unknown shape to generate for testcase");
            return false;
          }
          
          String testcasename;
          BufferedWriter summarywriter = new BufferedWriter(new java.io.FileWriter(prepend + ".csv"));
          

          TestSuite.ValueCollector values = new TestSuite.ValueCollector(TestSuite.this, 21);
          


          boolean isfirst = true;
          for (int density = start; density <= end; density += incr)
          {
            values.reset();
            

            BufferedWriter writer = new BufferedWriter(new java.io.FileWriter(prepend + "-" + density + ".csv"));
            

            TestSuite.this.writeHeadingRow(writer, values);
            

            for (int x = 1; x <= timestoexecute; x++)
            {
              progress.setValue((density - start) / incr * timestoexecute + x);
              lblStatus.setText("Executing test case \"" + testcasename + "\" at a density " + density + " - " + x + " of " + timestoexecute + " times");
              

              boolean success = TestSuite.this.executeTestCase(writer, values, width, height, 
                TestSuite.TestCase.access$0(testcase), TestSuite.TestCase.access$1(testcase), TestSuite.TestCase.access$2(testcase), 
                TestSuite.TestCase.access$4(testcase), density, 
                TestSuite.TestCase.access$5(testcase), density, 
                TestSuite.TestCase.access$3(testcase), null, 
                TestSuite.TestCase.access$8(testcase));
              if (!success) {
                return false;
              }
            }
            

            writer.flush();
            writer.close();
            

            values.writeSummary(summarywriter, isfirst);
            if (isfirst) { isfirst = false;
            }
          }
          
          summarywriter.flush();
          summarywriter.close();
          

          return true;
        }
      });
    }
    catch (NumberFormatException e)
    {
      displayError("Failed to parse length paramter - only integers are valid");
      return;
    }
  }
  
  private void btnLengthParameterActionPerformed(ActionEvent evt)
  {
    try
    {
      final int start = Integer.parseInt(txtStartLength.getText());
      final int end = Integer.parseInt(txtEndLength.getText());
      final int incr = Integer.parseInt(txtLengthIncrements.getText());
      if (start >= end) {
        displayError("Start value must be less than end value");
        return;
      }
      runTestSuite(new RunTestSeries(end)
      {
        public boolean runTestSeries(JProgressBar progress, TestSuite.TestCase testcase, int width, int height, File outputdir, int timestoexecute)
          throws IOException
        {
          progress.setMinimum(1);
          progress.setMaximum(((end - start) / incr + 1) * timestoexecute);
          progress.setValue(1);
          

          String prepend = outputdir.getAbsolutePath() + File.separator;
          
          if ((TestSuite.TestCase.access$0(testcase) != null) && (TestSuite.TestCase.access$1(testcase) != null) && (TestSuite.TestCase.access$1(testcase).trim().length() == 1)) {
            String testcasename = TestSuite.TestCase.access$1(testcase) + " (" + TestSuite.TestCase.access$0(testcase).getName() + ")";
            prepend = prepend + "length-" + TestSuite.TestCase.access$1(testcase) + "-" + TestSuite.TestCase.access$0(testcase).getName();
          } else if ((TestSuite.TestCase.access$2(testcase) != null) && (TestSuite.TestCase.access$2(testcase).trim().length() >= 1)) {
            String testcasename = CountryListModel.formatHumanReadable(TestSuite.TestCase.access$2(testcase));
            prepend = prepend + "length-" + testcasename;
          } else {
            TestSuite.this.displayError("Unknown shape to generate for testcase");
            return false;
          }
          
          String testcasename;
          BufferedWriter summarywriter = new BufferedWriter(new java.io.FileWriter(prepend + ".csv"));
          

          TestSuite.ValueCollector values = new TestSuite.ValueCollector(TestSuite.this, 21);
          


          boolean isfirst = true;
          for (int length = start; length <= end; length += incr)
          {
            values.reset();
            

            BufferedWriter writer = new BufferedWriter(new java.io.FileWriter(prepend + "-" + length + ".csv"));
            

            TestSuite.this.writeHeadingRow(writer, values);
            

            for (int x = 1; x <= timestoexecute; x++)
            {
              progress.setValue((length - start) / incr * timestoexecute + x);
              lblStatus.setText("Executing test case \"" + testcasename + "\" at a length " + length + " - " + x + " of " + timestoexecute + " times");
              

              boolean success = TestSuite.this.executeTestCase(writer, values, width, height, 
                TestSuite.TestCase.access$0(testcase), TestSuite.TestCase.access$1(testcase), TestSuite.TestCase.access$2(testcase), 
                TestSuite.TestCase.access$4(testcase), TestSuite.TestCase.access$6(testcase), 
                TestSuite.TestCase.access$5(testcase), TestSuite.TestCase.access$7(testcase), 
                length, null, 
                TestSuite.TestCase.access$8(testcase));
              if (!success) {
                return false;
              }
            }
            

            writer.flush();
            writer.close();
            

            values.writeSummary(summarywriter, isfirst);
            if (isfirst) { isfirst = false;
            }
          }
          
          summarywriter.flush();
          summarywriter.close();
          

          return true;
        }
      });
    }
    catch (NumberFormatException e)
    {
      displayError("Failed to parse length paramter - only integers are valid");
      return;
    }
  }
  
  private void btnAddTestCaseActionPerformed(ActionEvent evt)
  {
    TestCase testcase = createTestCase();
    if (testcase == null) { return;
    }
    
    testcases.add(testcase);
    

    int index = testcases.size() - 1;
    testcasemodel.fireTableRowsInserted(index, index);
    try
    {
      saveTestcases();
    }
    catch (IOException e)
    {
      displayError(e);
      return;
    }
  }
  
  public class TestCaseTableModel
    extends javax.swing.table.AbstractTableModel
  {
    private Font font;
    private String letter;
    private String countryfile;
    
    public TestCaseTableModel() {}
    
    public int getColumnCount()
    {
      return 7;
    }
    
    public String getColumnName(int col)
    {
      switch (col) {
      case 0:  return "Testcase";
      case 1:  return "Length Cutoff";
      case 2:  return "# of Shape Points";
      case 3:  return "# of Internal Points";
      case 4:  return "Shape Point Min Density";
      case 5:  return "Internal Min Density";
      case 6:  return "Add Shape Points"; }
      return null;
    }
    
    private int maxdensity;
    private int numberofpoints;
    private int lengthcutoff;
    private boolean addshapepointstosplitlonglines;
    public int getRowCount()
    {
      return testcases.size();
    }
    
    public Class getColumnClass(int col)
    {
      return String.class;
    }
    
    public Object getValueAt(int row, int col)
    {
      TestSuite.TestCase testcase = (TestSuite.TestCase)testcases.get(row);
      

      switch (col) {
      case 0: 
        if ((font != null) && (letter != null) && (letter.trim().length() == 1))
          return "Letter " + letter + " (" + font.getName() + ")";
        if ((countryfile != null) && (countryfile.trim().length() >= 1)) {
          return CountryListModel.formatHumanReadable(countryfile);
        }
        return "n/a";
      
      case 1: 
        if ((mode == 1) || (mode == 2))
          return "n/a";
        if (lengthcutoff == 56535)
          return "None";
        if (lengthcutoff == 56534)
          return "Max Edge of MST";
        if (lengthcutoff == 56533)
          return "Max Smallest Triangle Edge";
        if (lengthcutoff >= 0) {
          return Integer.toString(lengthcutoff);
        }
        return "n/a";
      
      case 2: 
        if (shapepoints <= 0)
          return "No Points";
        if (shapepoints >= Integer.MAX_VALUE) {
          return "Max Possible";
        }
        return Integer.toString(shapepoints);
      
      case 3: 
        if (internalpoints <= 0)
          return "No Points";
        if (internalpoints >= Integer.MAX_VALUE) {
          return "Max Possible";
        }
        return Integer.toString(internalpoints);
      
      case 4: 
        if (mode == 3) {
          return "n/a";
        }
        return Integer.toString(shapepoint_mindensity);
      
      case 5: 
        if (mode == 3) {
          return "n/a";
        }
        return Integer.toString(internal_mindensity);
      
      case 6: 
        return addshapepointstosplitlonglines ? "Yes" : "No";
      }
      
      
      return null;
    }
  }
  
  private void optCountryGenerationActionPerformed(ActionEvent evt)
  {
    chkAddShapePointsToSplitLongLines.setSelected(false);
    

    panelGenerationSelection.removeAll();
    panelGenerationSelection.add(cboCountries);
    panelGenerationSelection.validate();
    panelGenerationSelection.repaint();
  }
  
  private void optLetterGenerationActionPerformed(ActionEvent evt)
  {
    chkAddShapePointsToSplitLongLines.setSelected(true);
    

    panelGenerationSelection.removeAll();
    panelGenerationSelection.add(txtLetter);
    panelGenerationSelection.validate();
    panelGenerationSelection.repaint();
  }
  private JPanel panelLeft2;
  private JPanel panelLeft4;
  private JPanel panelLengthParameter;
  private JPanel panelLengthParameters;
  private JPanel panelLengthParameters1;
  private JPanel panelLengthParameters2;
  
  public static void main(String[] args)
  {
    try
    {
      javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      displayError(null, e);
    }
    

    TestSuite dialog = new TestSuite(true, new javax.swing.JFrame());
    dialog.setVisible(true);
  }
  
  private JPanel panelNormalisedLengthParameter;
  private JPanel panelNorth;
  
  private void runTestSuite(final RunTestSeries testseries)
  {
    lblStatus.setText("Starting test suite run...");
    

    try
    {
      _width = Integer.parseInt(txtWidth.getText());
    } catch (NumberFormatException e) { int _width;
      displayError("Invalid width entered"); return;
    }
    int _width;
    try {
      _height = Integer.parseInt(txtHeight.getText());
    } catch (NumberFormatException e) { int _height;
      displayError("Invalid width entered"); return;
    }
    int _height;
    try {
      _timestoexecute = Integer.parseInt(txtTimesToExecuteEachTestcase.getText());
    } catch (NumberFormatException e) { int _timestoexecute;
      displayError("Invalid width entered"); return;
    }
    int _timestoexecute;
    final int width = _width;
    final int height = _height;
    final int timestoexecute = _timestoexecute;
    
    String stroutputdir = txtOutputDir.getText();
    if ((stroutputdir == null) || (stroutputdir.trim().length() <= 0)) {
      displayError("Must enter output directory");
      return;
    }
    final File outputdir = new File(stroutputdir);
    if ((!outputdir.isDirectory()) && (!outputdir.mkdirs())) {
      displayError("Failed to required create directory; " + outputdir.getAbsolutePath());
      return;
    }
    

    progressOverall.setMinimum(1);
    progressOverall.setMaximum(testcases.size());
    

    signalprocesser.shared.StatusDialog.start(this, "Running Testsuite", "Please wait for testsuite to complete...", new Thread()
    {
      public void run()
      {
        try {
          for (int x = 1; x <= testcases.size(); x++) {
            progressOverall.setValue(x);
            tblTestcases.setRowSelectionInterval(x - 1, x - 1);
            
            TestSuite.TestCase testcase = (TestSuite.TestCase)testcases.get(x - 1);
            boolean success = testseries.runTestSeries(progressTestcases, testcase, width, height, outputdir, timestoexecute);
            if (!success)
            {
              lblStatus.setText("An error occurred while executing the test suite.");
              return;
            }
          }
        }
        catch (IOException e) {
          lblStatus.setText(e.getClass().getName() + ": " + e.getMessage());
          

          TestSuite.this.displayError(e);
          return;
        }
        

        lblStatus.setText(null);
        

        synchronized (MUTEX) {
          if (MUTEX != null)
            MUTEX.notifyAll();
        }
      }
    });
  }
  
  private JPanel panelNorth1;
  private JPanel panelNorth2;
  private JPanel panelNorth3;
  private JPanel panelOnRight;
  private JPanel panelOtherOptions;
  private JPanel panelPointOptions2;
  private JPanel panelRunAll;
  private JPanel panelRunAllCheckBoxes;
  
  private boolean executeTestCase(BufferedWriter writer, ValueCollector values, int width, int height, Font font, String letter, String countryfile, int shapepoints, int shapepoint_mindensity, int internalpoints, int internal_mindensity, int lengthcutoff, signalprocesser.voronoi.representation.triangulation.TriangulationRepresentation.CalcCutOff lengthcutoffcalc, boolean addshapepointstosplitlonglines) throws IOException {
    java.awt.Rectangle shapebounds = new java.awt.Rectangle(0, 0, width, height);
    



    if ((font != null) && (letter != null) && (letter.trim().length() == 1)) {
      try {
        String testcasename = "Letter " + letter + " (" + font.getName() + ")";
        borderpoints = signalprocesser.voronoi.shapegeneration.ShapeGeneration.createShapeOutline(letter.trim(), shapebounds, font);
      } catch (signalprocesser.voronoi.shapegeneration.ShapeGenerationException e) { ArrayList<signalprocesser.voronoi.VPoint> borderpoints;
        displayError(e);
        return false;
      }
    } else if ((countryfile != null) && (countryfile.trim().length() >= 1)) {
      try {
        String testcasename = CountryListModel.formatHumanReadable(countryfile);
        borderpoints = CountryData.getCountryData(countryfile, shapebounds);
      } catch (IOException e) { ArrayList<signalprocesser.voronoi.VPoint> borderpoints;
        displayError(e);
        return false;
      }
    } else {
      displayError("Unknown shape to generate for testcase");
      return false;
    }
    ArrayList<signalprocesser.voronoi.VPoint> borderpoints;
    String testcasename;
    ArrayList<signalprocesser.voronoi.VPoint> points = null;
    try {
      boolean splitlonglines = addshapepointstosplitlonglines;
      points = signalprocesser.voronoi.shapegeneration.ShapeGeneration.addRandomPoints(borderpoints, splitlonglines, 
        shapepoints, shapepoint_mindensity, 
        internalpoints, internal_mindensity);
    } catch (signalprocesser.voronoi.shapegeneration.ShapeGenerationException e) {
      displayError(e);
      return false;
    }
    
    TriangulationRepresentation representation;
    
    if (lengthcutoffcalc != null) {
      representation = new TriangulationRepresentation(lengthcutoffcalc); } else { TriangulationRepresentation representation;
      if (lengthcutoff == 56535) {
        representation = new TriangulationRepresentation(0); } else { TriangulationRepresentation representation;
        if (lengthcutoff == 56534) {
          representation = new TriangulationRepresentation(new signalprocesser.voronoi.representation.triangulation.TriangulationRepresentation.CalcCutOff()
          {
            public int calculateCutOff(TriangulationRepresentation rep) { return rep.getMaxLengthOfMinimumSpanningTree(); }
          });
        } else { TriangulationRepresentation representation;
          if (lengthcutoff == 56533) {
            representation = new TriangulationRepresentation(new signalprocesser.voronoi.representation.triangulation.TriangulationRepresentation.CalcCutOff()
            {
              public int calculateCutOff(TriangulationRepresentation rep) { return rep.getMaxLengthOfSmallestTriangleEdge(); }
            });
          } else { TriangulationRepresentation representation;
            if (lengthcutoff >= 0) {
              representation = new TriangulationRepresentation(lengthcutoff);
            } else {
              displayError("Unknown length cut-off enter - \"" + lengthcutoff + "\"");
              return false;
            }
          }
        } } }
    TriangulationRepresentation representation;
    try { points = signalprocesser.voronoi.representation.RepresentationFactory.convertPointsToTriangulationPoints(points);
      

      signalprocesser.voronoi.VoronoiAlgorithm.generateVoronoi(representation, points);
    } catch (Error e) {
      displayError(e);
      return false;
    } catch (RuntimeException e) {
      displayError(e);
      return false;
    }
    

    lengthcutoff = representation.calculateLengthCutoff();
    

    double minlength = representation.getMinLength();
    double maxlength = representation.getMaxLength();
    double maxlengthofminimumspanningtree = representation.getMaxLengthOfMinimumSpanningTree();
    double maxlengthofsmallesttriangleedge = representation.getMaxLengthOfSmallestTriangleEdge();
    double normalizedlengthparameter = (lengthcutoff - minlength) / (maxlength - minlength);
    

    java.awt.geom.Area area = signalprocesser.voronoi.shapegeneration.ShapeGeneration.createArea(borderpoints);
    ArrayList outterpoints = representation.getPointsFormingOutterBoundary();
    area.exclusiveOr(signalprocesser.voronoi.shapegeneration.ShapeGeneration.createArea(outterpoints));
    

    double expectedarea = signalprocesser.voronoi.VoronoiShared.calculateAreaOfShape(borderpoints);
    double actualarea = signalprocesser.voronoi.VoronoiShared.calculateAreaOfShape(outterpoints);
    double expectedperimeter = signalprocesser.voronoi.VoronoiShared.calculatePerimeterOfShape(borderpoints);
    double actualperimeter = signalprocesser.voronoi.VoronoiShared.calculatePerimeterOfShape(outterpoints);
    

    double l2norm = signalprocesser.voronoi.VoronoiShared.calculateAreaOfShape(area.getPathIterator(null));
    double error = l2norm / expectedarea * 100.0D;
    

    writeColumn(writer, 0, values, testcasename);
    

    writeColumn(writer, 1, values, width);
    writeColumn(writer, 2, values, height);
    writeColumn(writer, 3, values, shapepoints);
    writeColumn(writer, 4, values, shapepoint_mindensity);
    writeColumn(writer, 5, values, internalpoints);
    writeColumn(writer, 6, values, internal_mindensity);
    writeColumn(writer, 7, values, points.size());
    writeColumn(writer, 8, values, lengthcutoff);
    writeColumn(writer, 9, values, normalizedlengthparameter);
    writeColumn(writer, 10, values, addshapepointstosplitlonglines ? "Yes" : "No");
    

    writeColumn(writer, 11, values, l2norm);
    writeColumn(writer, 12, values, error);
    
    writeColumn(writer, 13, values, expectedarea);
    writeColumn(writer, 14, values, actualarea);
    writeColumn(writer, 15, values, expectedperimeter);
    writeColumn(writer, 16, values, actualperimeter);
    
    writeColumn(writer, 17, values, minlength);
    writeColumn(writer, 18, values, maxlengthofminimumspanningtree);
    writeColumn(writer, 19, values, maxlengthofsmallesttriangleedge);
    writeColumn(writer, 20, values, maxlength);
    

    writer.newLine();
    return true; }
  
  private JPanel panelStatus;
  private JPanel panelStatusOutter;
  
  private void writeHeadingRow(BufferedWriter writer, ValueCollector values) throws IOException { writeHeadingColumn(writer, 0, values, false, "Testcase Name");
    

    writeHeadingColumn(writer, 1, values, false, "Width");
    writeHeadingColumn(writer, 2, values, false, "Height");
    writeHeadingColumn(writer, 3, values, false, "Shape Points");
    writeHeadingColumn(writer, 4, values, false, "SP Min Density");
    writeHeadingColumn(writer, 5, values, false, "Internal Points");
    writeHeadingColumn(writer, 6, values, false, "IP Min Density");
    writeHeadingColumn(writer, 7, values, true, "Actual Number of Points in Testcase");
    writeHeadingColumn(writer, 8, values, true, "Length Cut-off Used");
    writeHeadingColumn(writer, 9, values, true, "Normalized Length Parameter");
    writeHeadingColumn(writer, 10, values, false, "Shape Points added to Split Long Lines");
    

    writeHeadingColumn(writer, 11, values, true, "L2-Norm Value");
    writeHeadingColumn(writer, 12, values, true, "Error from Expected Area");
    
    writeHeadingColumn(writer, 13, values, false, "Expected Area");
    writeHeadingColumn(writer, 14, values, true, "Actual Area");
    writeHeadingColumn(writer, 15, values, false, "Expected Perimeter");
    writeHeadingColumn(writer, 16, values, true, "Actual Perimeter");
    
    writeHeadingColumn(writer, 17, values, true, "Min Length");
    writeHeadingColumn(writer, 18, values, true, "Max Length of Minimum Spanning Tree");
    writeHeadingColumn(writer, 19, values, true, "Max Length of Smallest Triangle Edge");
    writeHeadingColumn(writer, 20, values, true, "Max Length");
    

    writer.newLine(); }
  
  private JPanel panelTextfields;
  private JPanel panelUpNorth;
  private static void writeColumn(BufferedWriter writer, int value) throws IOException { writer.write(Integer.toString(value));
    writer.write(",");
  }
  
  private static void writeColumn(BufferedWriter writer, double value) throws IOException { writer.write(Double.toString(value));
    writer.write(",");
  }
  
  private static void writeColumn(BufferedWriter writer, String string) throws IOException { if (string == null) {
      writer.write("Null Value");
    } else {
      writer.write(string.replaceAll(",", "\\,"));
    }
    writer.write(",");
  }
  
  private static void writeColumn(BufferedWriter writer, int column, ValueCollector values, int value) throws IOException {
    values.newValue(column, value);
    writer.write(Integer.toString(value));
    writer.write(",");
  }
  
  private static void writeColumn(BufferedWriter writer, int column, ValueCollector values, double value) throws IOException { values.newValue(column, value);
    writer.write(Double.toString(value));
    writer.write(",");
  }
  
  private static void writeColumn(BufferedWriter writer, int column, ValueCollector values, String string) throws IOException { values.newValue(column, string);
    if (string == null) {
      writer.write("Null Value");
    } else {
      writer.write(string.replaceAll(",", "\\,"));
    }
    writer.write(",");
  }
  
  private static void writeHeadingColumn(BufferedWriter writer, int column, ValueCollector values, boolean variance, String string) throws IOException {
    values.setHeading(column, string, variance);
    if (string == null) {
      writer.write("Null Value");
    } else {
      writer.write(string.replaceAll(",", "\\,"));
    }
    writer.write(",");
  }
  
  public TestCase createTestCase() {
    TestCase testcase = new TestCase();
    

    if (optLetterGeneration.isSelected())
    {
      String letter = txtLetter.getText().trim();
      if (letter.length() != 1) {
        displayError("Must enter in a single letter");
        return null;
      }
      letter = letter;
      

      String strfont = ((String)cboFont.getSelectedItem()).trim();
      if (strfont.length() <= 0) {
        displayError("Must enter in a font name for letter");
        return null;
      }
      try
      {
        font = new Font(strfont, 1, 200);
      } catch (RuntimeException e) { Font font;
        displayError(e);
        return null; }
      Font font;
      font = font;
    } else if (optCountryGeneration.isSelected()) {
      countryfile = ((String)cboCountries.getSelectedItem());
    } else {
      displayError("Unknown shape generation option selected");
      return null;
    }
    
    try
    {
      shapepoint_mindensity = ((int)Double.parseDouble((String)cboShapePointMinDensity.getSelectedItem()));
    } catch (NumberFormatException e) {
      displayError("Invalid shape point min density entered");
      return null;
    }
    String strshapepoints = ((String)cboShapePoints.getSelectedItem()).toLowerCase();
    if (strshapepoints.startsWith("n")) {
      shapepoints = 0;
    } else if (strshapepoints.startsWith("m")) {
      shapepoints = Integer.MAX_VALUE;
    } else {
      try {
        shapepoints = Integer.parseInt(strshapepoints);
      } catch (NumberFormatException e) {
        displayError("Invalid number of shape points entered");
        return null;
      }
    }
    
    try
    {
      internal_mindensity = ((int)Double.parseDouble((String)cboInternalMinDensity.getSelectedItem()));
    } catch (NumberFormatException e) {
      displayError("Invalid internal min density entered");
      return null;
    }
    String strinternalpoints = ((String)cboInternalPoints.getSelectedItem()).toLowerCase();
    if (strinternalpoints.startsWith("n")) {
      internalpoints = 0;
    } else if (strinternalpoints.startsWith("m")) {
      internalpoints = Integer.MAX_VALUE;
    } else {
      try {
        internalpoints = Integer.parseInt(strinternalpoints);
      } catch (NumberFormatException e) {
        displayError("Invalid number of internal points entered");
        return null;
      }
    }
    

    String strlengthcutoff = ((String)cboLengthCutoff.getSelectedItem()).toLowerCase();
    if (strlengthcutoff.startsWith("n")) {
      lengthcutoff = 56535;
    } else if (strlengthcutoff.startsWith("m")) {
      lengthcutoff = 56534;
    } else if (strlengthcutoff.startsWith("l")) {
      lengthcutoff = 56533;
    } else {
      try {
        lengthcutoff = Integer.parseInt(strlengthcutoff);
      } catch (NumberFormatException e) {
        displayError("Invalid number of internal points entered");
        return null;
      }
    }
    addshapepointstosplitlonglines = chkAddShapePointsToSplitLongLines.isSelected();
    

    return testcase;
  }
  
  public class ValueCollector
  {
    private String[] heading;
    private boolean[] calculatevariance;
    private int[] count;
    private String[] caption;
    private double[] sumvalues;
    private double[] valuessquared;
    
    public ValueCollector(int size)
    {
      heading = new String[size];
      calculatevariance = new boolean[size];
      count = new int[size];
      caption = new String[size];
      sumvalues = new double[size];
      valuessquared = new double[size];
      

      reset();
    }
    
    public void reset() {
      for (int x = 0; x < heading.length; x++)
      {

        count[x] = 0;
        caption[x] = null;
        sumvalues[x] = 0.0D;
        valuessquared[x] = 0.0D;
      }
    }
    
    public String getHeading(int column) {
      return heading[column];
    }
    
    public boolean hasStandardDeviation(int column) { return calculatevariance[column]; }
    
    public void setHeading(int column, String strheading, boolean hasvariance) {
      if (strheading == null) {
        throw new IllegalArgumentException("Null value given for heading");
      }
      heading[column] = strheading;
      calculatevariance[column] = hasvariance;
    }
    
    public void newValue(int column, int value) {
      count[column] += 1;
      sumvalues[column] += value;
      valuessquared[column] += value * value;
    }
    
    public void newValue(int column, double value) { count[column] += 1;
      sumvalues[column] += value;
      valuessquared[column] += value * value;
    }
    
    public void newValue(int column, String text) { caption[column] = text; }
    
    public boolean isCaption(int column)
    {
      return caption[column] != null;
    }
    
    public boolean isValue(int column) { return (caption[column] == null) && (count[column] > 0); }
    
    public String getCaption(int column)
    {
      return caption[column];
    }
    
    public double getAverage(int column) { return sumvalues[column] / count[column]; }
    
    public double getStandardDeviation(int column) {
      double num = count[column];
      return Math.sqrt((num * valuessquared[column] - sumvalues[column] * sumvalues[column]) / (num * (num - 1.0D)));
    }
    

    public void writeSummary(BufferedWriter writer, boolean isfirst)
      throws IOException
    {
      if (isfirst) {
        for (int column = 0; column < heading.length; column++) {
          if (isCaption(column)) {
            TestSuite.writeColumn(writer, getHeading(column));
          } else {
            TestSuite.writeColumn(writer, getHeading(column));
            if (calculatevariance[column] != 0) {
              TestSuite.writeColumn(writer, "Std Deviation");
            }
          }
        }
        writer.newLine();
      }
      

      for (int column = 0; column < heading.length; column++) {
        if (isCaption(column)) {
          TestSuite.writeColumn(writer, getCaption(column));
        } else {
          TestSuite.writeColumn(writer, getAverage(column));
          if (calculatevariance[column] != 0) {
            TestSuite.writeColumn(writer, getStandardDeviation(column));
          }
        }
      }
      writer.newLine(); } }
  
  private JPanel panelVariables;
  private JProgressBar progressOverall;
  private JProgressBar progressTestcases;
  private javax.swing.JScrollPane scrollPane;
  private JTabbedPane tabsTestCaseType;
  private JTable tblTestcases;
  private JTextField txtDensityIncrements;
  private JTextField txtEndDensity;
  private JTextField txtEndLength;
  private JTextField txtHeight;
  private JTextField txtLengthIncrements;
  private JTextField txtLetter;
  private JTextField txtNormalisedEndLength;
  private JTextField txtNormalisedLengthIncrements;
  private JTextField txtNormalisedStartLength;
  private JTextField txtOutputDir;
  private JTextField txtStartDensity;
  private JTextField txtStartLength;
  private JTextField txtTimesToExecuteEachTestcase;
  private JTextField txtWidth;
  public static class TestCase implements java.io.Serializable { private Font font;
    private String letter;
    private String countryfile;
    private int lengthcutoff;
    public TestCase() {} private int shapepoints; private int shapepoint_mindensity; private int internalpoints;
    public TestCase(String letter, String font, int lengthcutoff, int mindensity, int numberofpoints) { this(letter, new Font(font, 1, 200), lengthcutoff, mindensity, numberofpoints);
      addshapepointstosplitlonglines = true; }
    
    private int internal_mindensity;
    private boolean addshapepointstosplitlonglines; public TestCase(String _letter, Font _font, int lengthcutoff, int mindensity, int numberofpoints) { this(lengthcutoff, mindensity, numberofpoints);
      letter = _letter;
      font = _font;
      addshapepointstosplitlonglines = true;
    }
    
    public TestCase(String _countryfile, int lengthcutoff, int mindensity, int numberofpoints) { this(lengthcutoff, mindensity, numberofpoints);
      countryfile = _countryfile;
      addshapepointstosplitlonglines = false;
    }
    
    public TestCase(int _lengthcutoff, int _mindensity, int _numberofpoints) { lengthcutoff = _lengthcutoff;
      
      shapepoints = _numberofpoints;
      internalpoints = _numberofpoints;
      
      shapepoint_mindensity = _mindensity;
      internal_mindensity = _mindensity;
    }
  }
  
  private void displayError(String message) {
    javax.swing.JOptionPane.showMessageDialog(this, message, "Error", 0);
  }
  
  private static void displayError(javax.swing.JFrame parent, String message) { javax.swing.JOptionPane.showMessageDialog(parent, message, "Error", 0); }
  
  private void displayError(Throwable e)
  {
    e.printStackTrace();
    javax.swing.JOptionPane.showMessageDialog(this, e.getMessage(), e.getClass().getName(), 0);
  }
  
  private static void displayError(javax.swing.JFrame parent, Throwable e) { e.printStackTrace();
    javax.swing.JOptionPane.showMessageDialog(parent, e.getMessage(), e.getClass().getName(), 0);
  }
  
  public abstract class RunTestSeries
  {
    public RunTestSeries() {}
    
    public abstract boolean runTestSeries(JProgressBar paramJProgressBar, TestSuite.TestCase paramTestCase, int paramInt1, int paramInt2, File paramFile, int paramInt3)
      throws IOException;
  }
}
