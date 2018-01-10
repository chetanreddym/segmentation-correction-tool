package signalprocesser.voronoi;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import signalprocesser.voronoi.representation.RepresentationFactory;
import signalprocesser.voronoi.representation.triangulation.TriangulationRepresentation;

public class VoronoiTest extends javax.swing.JFrame
{
  ButtonGroup groupMatt = new ButtonGroup();
  
  public static final int FRAME_WIDTH = 800;
  
  public static final int FRAME_HEIGHT = 400;
  
  public static final int SHAPEMARGIN_TOPBOTTOM = 60;
  
  public static final int SHAPEMARGIN_LEFTRIGHT = 120;
  
  public static final int POINT_SIZE = 10;
  
  public static final String SAVE_FILE = "voronoipoints.txt";
  
  public static signalprocesser.voronoi.statusstructure.binarysearchtreeimpl.debug.DebugTree treedialog = null;
  
  private boolean SHOW_POINTS = false;
  private boolean SHOW_POINT_COORDINATES = false;
  private boolean SHOW_MOUSE_LOCATION = false;
  private boolean SHOW_INTERACTIVE_SWEEPLINE = false;
  private boolean SHOW_CIRCLEEVENTS = false;
  private boolean SHOW_EXPECTED_BORDER = false;
  private boolean SHOW_INTERSECTION_WITH_EXPECTED = false;
  private boolean SHOW_ALPHA_SHAPE = false;
  
  private int backupboundaryenhancedvalue = -1;
  
  private double expectedarea = -1.0D;
  
  private String lastdirectoryopened = null;
  
  private signalprocesser.voronoi.tools.CountryListModel countrylistmodel;
  private java.awt.Shape alphashape;
  private SignalPanel panel;
  private ArrayList<VPoint> points = new ArrayList();
  private ArrayList<VPoint> borderpoints = null;
  private TestRepresentationWrapper representationwrapper = new TestRepresentationWrapper();
  private signalprocesser.voronoi.representation.AbstractRepresentation representation;
  private JButton btnClearPoints;
  private JButton btnExit;
  
  public static void main(String[] args) {
    try {
      javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      displayError(null, e);
    }
    

    VoronoiTest frame = new VoronoiTest();
    frame.setVisible(true);
  }
  

  public VoronoiTest()
  {
    initComponents();
    initMattComponents();
    

    sliderAreaCutOffToUseStateChanged(null);
    chkShowPointsActionPerformed(null);
    chkShowPointCoordinatesActionPerformed(null);
    chkShowCircleEventsActionPerformed(null);
    chkShowTreeStructureActionPerformed(null);
    chkShowMouseLocationActionPerformed(null);
    chkShowSweeplineActionPerformed(null);
    chkShowEdgeLengthsActionPerformed(null);
    chkShowInternalTrianglesActionPerformed(null);
    chkShowMinimumSpanningTreeActionPerformed(null);
    chkShowDebugInfoActionPerformed(null);
    chkMaxEdgesToRemoveActionPerformed(null);
    chkShowExpectedBorderActionPerformed(null);
    chkShowIntersectionActionPerformed(null);
    

    optLetterGenerationActionPerformed(null);
    




    optEdgeRemoval.setSelected(true);
    optEdgeRemovalActionPerformed(null);
    









    try
    {
      cboCountries.setModel(this.countrylistmodel = new signalprocesser.voronoi.tools.CountryListModel(cboCountries, signalprocesser.voronoi.tools.CountryData.getCountryList()));
    } catch (IOException e) {
      displayError(e);
    }
    

    panel = new SignalPanel();
    getContentPane().add(panel, "Center");
    

    java.awt.Dimension screensize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
    super.setBounds(
      (width - 800) / 2, 
      (height - 400) / 2, 
      800, 400);
  }
  
  public class SignalPanel extends JPanel implements java.awt.event.MouseListener, java.awt.event.MouseMotionListener {
    private int mouse_x = -1;
    private int mouse_y = -1;
    private int attentiontopoint = -1;
    private VPoint attentiontovpoint = null;
    private VPoint attentiontovpoint_onclick = null;
    
    public SignalPanel() {
      addMouseListener(this);
      addMouseMotionListener(this);
    }
    
    public void mousePressed(MouseEvent e)
    {
      if (e.getButton() == 1) {
        if (attentiontovpoint != null) {
          attentiontovpoint_onclick = attentiontovpoint;
        }
        else {
          double width = getWidth();
          double height = getHeight();
          

          if (representation == null) {
            points.add(new VPoint(e.getX(), e.getY()));
          } else {
            points.add(representation.createPoint(e.getX(), e.getY()));
          }
          








          int mouseoverindex = getPointOverIndex(e.getX(), e.getY());
          if (mouseoverindex != attentiontopoint) {
            attentiontopoint = mouseoverindex;
          }
        }
      }
      else if (attentiontovpoint != null) {
        points.remove(attentiontovpoint);
        attentiontopoint = -1;
        attentiontovpoint = null;
        attentiontovpoint_onclick = null;
        








        int mouseoverindex = getPointOverIndex(e.getX(), e.getY());
        if (mouseoverindex != attentiontopoint) {
          attentiontopoint = mouseoverindex;
        }
      }
      


      getParent().repaint();
      


      VoronoiTest.this.updateControls();
    }
    
    public void paintComponent(java.awt.Graphics _g) {
      Graphics2D g = (Graphics2D)_g;
      

      double width = getWidth();
      double height = getHeight();
      

      g.setColor(java.awt.Color.white);
      g.fillRect(0, 0, (int)width, (int)height);
      

      g.setColor(java.awt.Color.black);
      if ((SHOW_MOUSE_LOCATION) || (SHOW_INTERACTIVE_SWEEPLINE)) {
        g.drawString("(" + mouse_x + ", " + mouse_y + ")", 5, (int)height - 20);
      }
      

      try
      {
        g.setColor(java.awt.Color.red);
        VoronoiTest.TestRepresentationWrapper.access$0(representationwrapper, representation);
        
        if (!SHOW_INTERACTIVE_SWEEPLINE) {
          if (points != null)
          {
            VoronoiAlgorithm.generateVoronoi(representationwrapper, points);
          }
          
        }
        else if (attentiontovpoint != null) {
          if (points != null) {
            VoronoiAlgorithm.generateVoronoi(representationwrapper, points, g, attentiontovpoint, mouse_y);
          }
        }
        else if (points != null) {
          VoronoiAlgorithm.generateVoronoi(representationwrapper, points, g, attentiontovpoint_onclick, mouse_y);
        }
      }
      catch (Error e)
      {
        points.clear();
        VoronoiTest.this.displayError(e);throw e;
      } catch (RuntimeException e) {
        points.clear();
        VoronoiTest.this.displayError(e);throw e;
      }
      

      if ((SHOW_INTERSECTION_WITH_EXPECTED) && (borderpoints != null) && ((representation instanceof TriangulationRepresentation)))
      {
        TriangulationRepresentation triangularrep = (TriangulationRepresentation)representation;
        

        if (triangularrep.getMode() == TriangulationRepresentation.MODE_REDUCE_OUTER_BOUNDARIES)
        {
          java.awt.geom.Area area = signalprocesser.voronoi.shapegeneration.ShapeGeneration.createArea(borderpoints);
          

          ArrayList outterpoints = triangularrep.getPointsFormingOutterBoundary();
          area.exclusiveOr(signalprocesser.voronoi.shapegeneration.ShapeGeneration.createArea(outterpoints));
          

          g.setPaint(java.awt.Color.yellow);
          g.fill(signalprocesser.voronoi.shapegeneration.ShapeGeneration.createShape(area));
          
          try
          {
            java.awt.Shape shape = signalprocesser.voronoi.shapegeneration.ShapeGeneration.createShape(area);
            double l2norm = VoronoiShared.calculateAreaOfShape(shape);
            double error = l2norm / expectedarea * 100.0D;
            txtL2Norm.setText(String.format("%.1f", new Object[] { Double.valueOf(l2norm) }) + " pixels^2");
            txtErrorFromExpectedArea.setText(String.format("%.2f", new Object[] { Double.valueOf(error) }) + "%");
          } catch (Exception e) {
            e.printStackTrace();
            txtL2Norm.setText("Error");
            txtErrorFromExpectedArea.setText("Error");
          }
          try {
            txtActualArea.setText(String.format("%.1f", new Object[] { Double.valueOf(VoronoiShared.calculateAreaOfShape(outterpoints)) }) + " pixels^2");
          } catch (Exception e) {
            e.printStackTrace();
            txtActualArea.setText("Error");
          }
          try {
            txtActualPerimeter.setText(String.format("%.1f", new Object[] { Double.valueOf(VoronoiShared.calculatePerimeterOfShape(outterpoints)) }) + " pixels");
          } catch (Exception e) {
            e.printStackTrace();
            txtActualPerimeter.setText("Error");
          }
        }
      }
      
      java.awt.Stroke originalstroke;
      if ((SHOW_EXPECTED_BORDER) && (borderpoints != null)) {
        VPoint prev = null;
        originalstroke = g.getStroke();
        g.setStroke(new java.awt.BasicStroke(2.0F, 1, 1));
        g.setColor(java.awt.Color.red);
        for (VPoint point : borderpoints)
          if (prev == null) {
            prev = point;

          }
          else
          {
            g.drawLine(x, y, x, y);
            

            prev = point;
          }
        g.setStroke(originalstroke);
      }
      

      if ((points != null) && (SHOW_POINTS)) {
        g.setColor(java.awt.Color.blue);
        for (VPoint point : points) {
          g.fillOval(x - 5, y - 5, 10, 10);
          if (SHOW_POINT_COORDINATES) {
            g.drawString("(" + x + "," + y + ")", x + 5 + 1, y);
          }
        }
      }
      

      if (SHOW_CIRCLEEVENTS) {
        g.setColor(java.awt.Color.red);
        for (VPoint point : VoronoiTest.TestRepresentationWrapper.access$1(representationwrapper)) {
          g.fillOval(x - 5, y - 5, 10, 10);
          if (SHOW_POINT_COORDINATES) {
            g.drawString("(" + x + "," + y + ")", x + 5 + 1, y);
          }
        }
      }
      

      if (SHOW_ALPHA_SHAPE)
      {

        g.setPaint(java.awt.Color.green);
        g.draw(alphashape);
        g.setPaint(new java.awt.Color(0, 255, 0, 25));
        g.fill(alphashape);
      }
      

      if (representation != null) {
        g.setColor(java.awt.Color.magenta);
        

        try
        {
          representation.paint(g);
        } catch (Error e) {
          VoronoiTest.this.displayError(e);throw e;
        } catch (RuntimeException e) {
          VoronoiTest.this.displayError(e);throw e;
        }
      }
    }
    
    public void mouseMoved(MouseEvent e) {
      if ((mouse_x == e.getX()) && (mouse_y == e.getY())) return;
      int mouseoverindex = getPointOverIndex(this.mouse_x = e.getX(), this.mouse_y = e.getY());
      if (mouseoverindex != attentiontopoint) {
        attentiontopoint = mouseoverindex;
      }
      
      if ((SHOW_MOUSE_LOCATION) || (SHOW_INTERACTIVE_SWEEPLINE)) {
        repaint();
      }
    }
    
    private int getPointOverIndex(int mouse_x, int mouse_y) {
      for (int x = 0; x < points.size(); x++) {
        VPoint point = (VPoint)points.get(x);
        if ((mouse_x >= x - 5) && (mouse_x <= x + 5) && 
          (mouse_y >= y - 5) && (mouse_y <= y + 5)) {
          attentiontovpoint = point;
          return x;
        }
      }
      attentiontovpoint = null;
      return -1; }
    
    public void mouseDragged(MouseEvent e) {}
    
    public void mouseEntered(MouseEvent e) {}
    
    public void mouseExited(MouseEvent e) {}
    
    public void mouseClicked(MouseEvent e) {}
    
    public void mouseReleased(MouseEvent e) {} }
  
  private void savePoints() throws IOException { savePoints("voronoipoints.txt"); }
  
  private void savePoints(String filename) throws IOException {
    java.io.FileWriter writer = new java.io.FileWriter(filename);
    for (VPoint point : points) {
      writer.write(x + "," + y + "\n");
    }
    writer.close();
  }
  
  private void exportPoints(String filename) throws IOException { java.io.FileWriter writer = new java.io.FileWriter(filename);
    for (VPoint point : points) {
      writer.write(x + " " + y + "\n");
    }
    writer.close();
  }
  
  private void loadPoints() throws IOException {
    loadPoints("voronoipoints.txt");
  }
  
  private void loadPoints(String filename) throws IOException {
    points.clear();
    borderpoints = null;
    
    try
    {
      BufferedReader reader = new BufferedReader(new java.io.FileReader(filename));
      
      String line;
      while ((line = reader.readLine()) != null) { String line;
        if (line.trim().length() > 0)
        {
          String[] values;
          
          if (line.indexOf(',') > 0) {
            values = line.split(",", 2); } else { String[] values;
            if (line.indexOf(' ') > 0) {
              values = line.split(" ", 2);
            } else {
              throw new IOException("Expected value line to be comma or space seperated - except found neither");
            }
          }
          String[] values;
          int x = Integer.parseInt(values[0]);
          int y = Integer.parseInt(values[1]);
          

          if (representation == null) {
            points.add(new VPoint(x, y));
          } else
            points.add(representation.createPoint(x, y));
        }
      }
      reader.close();
    }
    catch (java.io.FileNotFoundException localFileNotFoundException) {}
  }
  
  private void initMattComponents()
  {
    signalprocesser.shared.JCollapsiblePanel mattPanel = new signalprocesser.shared.JCollapsiblePanel();
    mattPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Matt's custom controls"));
    JButton hi = new JButton("Read in alpha shape");
    mattPanel.add(hi);
    
    hi.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        VoronoiTest.this.hiActionPerformed(evt);
      }
    });
  }
  
  private void hiActionPerformed(ActionEvent evt)
  {
    JFileChooser jfc = new JFileChooser();
    
    jfc.showOpenDialog(this);
    
    String filename = jfc.getSelectedFile().getAbsolutePath();
    
    int[] points = getAlphaShapeFromFile(filename + "-alf");
    




    int no_coords = 0;
    try {
      java.io.FileReader fr = new java.io.FileReader(filename);
      BufferedReader br = new BufferedReader(fr);
      while (br.readLine() != null) {
        no_coords++;
      }
      br.close();
      fr.close();
    } catch (IOException e) { e.printStackTrace(System.out);
    }
    
    int[][] coords = new int[no_coords][2];
    try {
      java.io.FileReader fr = new java.io.FileReader(filename);
      BufferedReader br = new BufferedReader(fr);
      String line = br.readLine();
      int count = 0;
      while (line != null) {
        java.util.StringTokenizer st = new java.util.StringTokenizer(line);
        try {
          int x = Integer.parseInt(st.nextToken());
          int y = Integer.parseInt(st.nextToken());
          
          coords[count][0] = x;
          coords[count][1] = y;
          count++;
        } catch (Exception e) { System.out.println("Ignored"); }
        line = br.readLine();
      }
    } catch (Exception e) { e.printStackTrace(System.out);
    }
    
    java.awt.geom.GeneralPath gp = new java.awt.geom.GeneralPath();
    boolean move = true;
    for (int i = 0; i < points.length; i++) {
      if (move) {
        gp.moveTo(coords[points[i]][0], coords[points[i]][1]);
        move = false;

      }
      else
      {
        gp.lineTo(coords[Math.abs(points[i])][0], coords[Math.abs(points[i])][1]);
        if (points[i] < 0) {
          move = true;
          gp.closePath();
        }
      }
    }
    gp.closePath();
    alphashape = gp;
    SHOW_ALPHA_SHAPE = true;
    repaint();
  }
  



  private int[] getAlphaShapeFromFile(String filename)
  {
    int count = 0;
    try {
      java.io.FileReader fr = new java.io.FileReader(filename);
      BufferedReader br = new BufferedReader(fr);
      String line = br.readLine();
      line = br.readLine();
      while (line != null) {
        count++;
        line = br.readLine();
      }
      br.close();
      fr.close();
    } catch (IOException e) { e.printStackTrace(System.out);
    }
    

    int[][] data = new int[count][2];
    try {
      java.io.FileReader fr = new java.io.FileReader(filename);
      BufferedReader br = new BufferedReader(fr);
      String line = br.readLine();
      line = br.readLine();
      int lineno = 0;
      while (line != null) {
        java.util.StringTokenizer st = new java.util.StringTokenizer(line);
        try {
          data[lineno][0] = Integer.parseInt(st.nextToken());
          data[lineno][1] = Integer.parseInt(st.nextToken());
        } catch (Exception e) {
          e.printStackTrace(System.out); }
        line = br.readLine();
        lineno++;
      }
    } catch (IOException e) { e.printStackTrace(System.out);
    }
    
    boolean more = true;
    boolean change = true;
    int lineno = 0;
    int lastnumber = -1;
    int[] points = new int[count + 1];
    while (more) {
      while (change) {
        change = false;
        for (int i = 0; i < count; i++) {
          if (data[i][0] >= 0)
          {

            if (lastnumber < 0) {
              points[lineno] = data[i][0];
              points[(lineno + 1)] = data[i][1];
              lastnumber = data[i][1];
              data[i][0] = -1;
              data[i][1] = -1;
              lineno += 2;
              change = true;

            }
            else if (data[i][0] == lastnumber) {
              points[lineno] = data[i][1];
              lineno++;
              lastnumber = data[i][1];
              data[i][0] = -1;
              data[i][1] = -1;
              change = true;
            }
            else if (data[i][1] == lastnumber) {
              points[lineno] = data[i][0];
              lineno++;
              lastnumber = data[i][0];
              data[i][0] = -1;
              data[i][1] = -1;
              change = true;
            }
          }
        }
      }
      
      System.out.println("Lineno=" + lineno + ", points=" + points.length);
      for (int i = 0; i < points.length; i++) {
        System.out.println(points[i]);
      }
      if (lineno >= points.length - 1) more = false;
      lastnumber = -1;
      lineno--;
      change = true;
      
      points[(lineno - 1)] = (-points[(lineno - 1)]);
    }
    return points;
  }
  

  private JButton btnExportToSVG;
  private JButton btnGenerate;
  private JButton btnLoad;
  private JButton btnSave;
  private void initComponents()
  {
    groupRepresentations = new ButtonGroup();
    groupEdgeRemoval = new ButtonGroup();
    optBoundary = new JRadioButton();
    optBoundaryUsingAngle20 = new JRadioButton();
    optBoundaryUsingAngle30 = new JRadioButton();
    optBoundaryEnhanced = new JRadioButton();
    panelBoundaryEnhanced = new JPanel();
    sliderAreaCutOffToUse = new JSlider();
    groupGenerationType = new ButtonGroup();
    scrollRight = new javax.swing.JScrollPane();
    panelRight = new JPanel();
    panelTop = new JPanel();
    panelActions = new JPanel();
    panelActionsInner = new JPanel();
    jPanel6 = new JPanel();
    btnSave = new JButton();
    JButton btnExport = new JButton();
    btnLoad = new JButton();
    btnExportToSVG = new JButton();
    btnTestSuiteForm = new JButton();
    btnExit = new JButton();
    panelStatistics = new JPanel();
    panelStatCaptions = new JPanel();
    lblL2Norm = new JLabel();
    lblErrorFromExpectedArea = new JLabel();
    lblExpectedArea = new JLabel();
    lblActualArea = new JLabel();
    lblExpectedPerimeter = new JLabel();
    lblActualPerimeter = new JLabel();
    panelStatLabels = new JPanel();
    txtL2Norm = new JLabel();
    txtErrorFromExpectedArea = new JLabel();
    txtExpectedArea = new JLabel();
    txtActualArea = new JLabel();
    txtExpectedPerimeter = new JLabel();
    txtActualPerimeter = new JLabel();
    panelPoints = new JPanel();
    panelPointsInner = new JPanel();
    panelGenerate = new JPanel();
    panelGenerationSelection = new JPanel();
    txtLetter = new javax.swing.JTextField();
    cboCountries = new JComboBox();
    btnGenerate = new JButton();
    panelGap1 = new JPanel();
    panelPointOptions = new JPanel();
    panelLeft = new JPanel();
    lblGenerationType = new JLabel();
    lblFont = new JLabel();
    lblShapePoints = new JLabel();
    lblInternalPoints = new JLabel();
    lblShapePointMinDensity = new JLabel();
    lblInternalMinDensity = new JLabel();
    panelCenter = new JPanel();
    panelGenerationType = new JPanel();
    optLetterGeneration = new JRadioButton();
    optCountryGeneration = new JRadioButton();
    cboFont = new JComboBox();
    cboShapePoints = new JComboBox();
    cboInternalPoints = new JComboBox();
    cboShapePointMinDensity = new JComboBox();
    cboInternalMinDensity = new JComboBox();
    panelGap = new JPanel();
    panelClearPoints = new JPanel();
    btnClearPoints = new JButton();
    panelCheckBoxes = new JPanel();
    chkShowPoints = new JCheckBox();
    chkShowExpectedBorder = new JCheckBox();
    chkShowIntersection = new JCheckBox();
    chkAddShapePointsToSplitLongLines = new JCheckBox();
    panelRepresentations = new JPanel();
    optNone = new JRadioButton();
    optVoronoiCells = new JRadioButton();
    optSimpleTriangulation = new JRadioButton();
    optEdgeRemoval = new JRadioButton();
    optClustering = new JRadioButton();
    panelEdgeRemoval = new JPanel();
    jPanel4 = new JPanel();
    jPanel1 = new JPanel();
    optNoLengthRestriction = new JRadioButton();
    optUserLengthRestriction = new JRadioButton();
    jPanel2 = new JPanel();
    panelGapWest = new JPanel();
    sliderLengthRestriction = new JSlider();
    panelGapSouth = new JPanel();
    jPanel3 = new JPanel();
    optNormalisedLengthRestriction = new JRadioButton();
    panelGapWest2 = new JPanel();
    sliderNormalisedLengthRestriction = new JSlider();
    panelGapSouth1 = new JPanel();
    jPanel7 = new JPanel();
    optMaxEdgeOfMinSpanningTree = new JRadioButton();
    optMaxEdgeOfSmallestTEdge = new JRadioButton();
    optApplyAboveMSTAndSmallestTEdgeInProportion = new JRadioButton();
    jPanel5 = new JPanel();
    panelGapWest1 = new JPanel();
    sliderApplyInProportion = new JSlider();
    panelEdgeRemovalOptions = new JPanel();
    jPanel8 = new JPanel();
    chkShowEdgeLengths = new JCheckBox();
    chkShowInternalTriangles = new JCheckBox();
    chkShowMinimumSpanningTree = new JCheckBox();
    chkShowDebugInfo = new JCheckBox();
    chkMaxEdgesToRemove = new JCheckBox();
    jPanel9 = new JPanel();
    jPanel10 = new JPanel();
    sliderMaxEdgesToRemove = new JSlider();
    panelOptions = new JPanel();
    chkShowMouseLocation = new JCheckBox();
    chkShowPointCoordinates = new JCheckBox();
    chkShowSweepline = new JCheckBox();
    chkShowTreeStructure = new JCheckBox();
    chkShowCircleEvents = new JCheckBox();
    
    groupRepresentations.add(optBoundary);
    optBoundary.setText("Boundary");
    optBoundary.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        VoronoiTest.this.optBoundaryActionPerformed(evt);
      }
      
    });
    groupRepresentations.add(optBoundaryUsingAngle20);
    optBoundaryUsingAngle20.setText("Boundary (20˚)");
    optBoundaryUsingAngle20.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        VoronoiTest.this.optBoundaryUsingAngle20ActionPerformed(evt);
      }
      
    });
    groupRepresentations.add(optBoundaryUsingAngle30);
    optBoundaryUsingAngle30.setText("Boundary (30˚)");
    optBoundaryUsingAngle30.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        VoronoiTest.this.optBoundaryUsingAngle30ActionPerformed(evt);
      }
      
    });
    groupRepresentations.add(optBoundaryEnhanced);
    optBoundaryEnhanced.setText("Boundary Enhanced");
    optBoundaryEnhanced.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        VoronoiTest.this.optBoundaryEnhancedActionPerformed(evt);
      }
      
    });
    panelBoundaryEnhanced.setLayout(new java.awt.GridLayout(0, 1));
    
    panelBoundaryEnhanced.setBorder(javax.swing.BorderFactory.createTitledBorder("Boundary Enhanced"));
    sliderAreaCutOffToUse.setMajorTickSpacing(8000);
    sliderAreaCutOffToUse.setMaximum(25000);
    sliderAreaCutOffToUse.setPaintLabels(true);
    sliderAreaCutOffToUse.setPaintTicks(true);
    sliderAreaCutOffToUse.setValue(8000);
    sliderAreaCutOffToUse.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        VoronoiTest.this.sliderAreaCutOffToUseStateChanged(evt);
      }
      
    });
    panelBoundaryEnhanced.add(sliderAreaCutOffToUse);
    
    setDefaultCloseOperation(3);
    setTitle("Non-convex Hull Test Program");
    scrollRight.setHorizontalScrollBarPolicy(31);
    scrollRight.setVerticalScrollBarPolicy(22);
    panelRight.setLayout(new java.awt.BorderLayout());
    
    panelRight.setBorder(javax.swing.BorderFactory.createBevelBorder(0));
    panelTop.setLayout(new javax.swing.BoxLayout(panelTop, 1));
    
    panelActions.setLayout(new java.awt.BorderLayout());
    
    panelActions.setBorder(javax.swing.BorderFactory.createTitledBorder("Actions"));
    panelActionsInner.setLayout(new java.awt.GridLayout(0, 1, 0, 4));
    
    jPanel6.setLayout(new java.awt.GridLayout(1, 0, 2, 0));
    
    btnSave.setText("Save");
    btnSave.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        VoronoiTest.this.btnSaveActionPerformed(evt);
      }
      
    });
    jPanel6.add(btnSave);
    
    btnExport.setText("Export");
    btnExport.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        VoronoiTest.this.btnExportActionPerformed(evt);
      }
      
    });
    jPanel6.add(btnExport);
    
    btnLoad.setText("Load");
    btnLoad.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        VoronoiTest.this.btnLoadActionPerformed(evt);
      }
      
    });
    jPanel6.add(btnLoad);
    
    panelActionsInner.add(jPanel6);
    
    btnExportToSVG.setText("Export to SVG");
    btnExportToSVG.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        VoronoiTest.this.btnExportToSVGActionPerformed(evt);
      }
      
    });
    panelActionsInner.add(btnExportToSVG);
    
    btnTestSuiteForm.setText("Test Suite Dialog");
    btnTestSuiteForm.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        VoronoiTest.this.btnTestSuiteFormActionPerformed(evt);

      }
      

    });
    btnExit.setText("Exit");
    btnExit.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        VoronoiTest.this.btnExitActionPerformed(evt);
      }
      
    });
    panelActionsInner.add(btnExit);
    
    panelActions.add(panelActionsInner, "Center");
    
    panelStatistics.setLayout(new java.awt.BorderLayout(6, 0));
    
    panelStatistics.setBorder(javax.swing.BorderFactory.createTitledBorder("Statistics"));
    panelStatCaptions.setLayout(new java.awt.GridLayout(0, 1, 0, 4));
    
    lblL2Norm.setHorizontalAlignment(4);
    lblL2Norm.setText("L2-Norm:");
    panelStatCaptions.add(lblL2Norm);
    
    lblErrorFromExpectedArea.setHorizontalAlignment(4);
    lblErrorFromExpectedArea.setText("Error From Area:");
    panelStatCaptions.add(lblErrorFromExpectedArea);
    
    lblExpectedArea.setHorizontalAlignment(4);
    lblExpectedArea.setText("Expected Area:");
    panelStatCaptions.add(lblExpectedArea);
    
    lblActualArea.setHorizontalAlignment(4);
    lblActualArea.setText("Actual Area:");
    panelStatCaptions.add(lblActualArea);
    
    lblExpectedPerimeter.setHorizontalAlignment(4);
    lblExpectedPerimeter.setText("Expected Perimeter:");
    panelStatCaptions.add(lblExpectedPerimeter);
    
    lblActualPerimeter.setHorizontalAlignment(4);
    lblActualPerimeter.setText("Actual Perimeter:");
    panelStatCaptions.add(lblActualPerimeter);
    
    panelStatistics.add(panelStatCaptions, "West");
    
    panelStatLabels.setLayout(new java.awt.GridLayout(0, 1, 0, 4));
    
    txtL2Norm.setText("n/a");
    panelStatLabels.add(txtL2Norm);
    
    txtErrorFromExpectedArea.setText("n/a");
    panelStatLabels.add(txtErrorFromExpectedArea);
    
    txtExpectedArea.setText("n/a");
    panelStatLabels.add(txtExpectedArea);
    
    txtActualArea.setText("n/a");
    panelStatLabels.add(txtActualArea);
    
    txtExpectedPerimeter.setText("n/a");
    panelStatLabels.add(txtExpectedPerimeter);
    
    txtActualPerimeter.setText("n/a");
    panelStatLabels.add(txtActualPerimeter);
    
    panelStatistics.add(panelStatLabels, "Center");
    


    panelPoints.setLayout(new java.awt.BorderLayout());
    
    panelPoints.setBorder(javax.swing.BorderFactory.createTitledBorder("Points / Shape Generation"));
    panelPointsInner.setLayout(new javax.swing.BoxLayout(panelPointsInner, 1));
    
    panelGenerate.setLayout(new java.awt.BorderLayout(3, 0));
    
    panelGenerationSelection.setLayout(new java.awt.GridLayout(0, 1));
    
    txtLetter.setFont(new java.awt.Font("Tahoma", 1, 12));
    txtLetter.setHorizontalAlignment(0);
    txtLetter.setText("S");
    txtLetter.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyTyped(java.awt.event.KeyEvent evt) {
        VoronoiTest.this.txtLetterKeyTyped(evt);
      }
      
    });
    panelGenerationSelection.add(txtLetter);
    
    cboCountries.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        VoronoiTest.this.cboCountriesActionPerformed(evt);
      }
      
    });
    panelGenerationSelection.add(cboCountries);
    
    panelGenerate.add(panelGenerationSelection, "Center");
    
    btnGenerate.setText("Generate");
    btnGenerate.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        VoronoiTest.this.btnGenerateActionPerformed(evt);
      }
      
    });
    panelGenerate.add(btnGenerate, "East");
    
    panelPointsInner.add(panelGenerate);
    
    panelGap1.setLayout(null);
    
    panelGap1.setPreferredSize(new java.awt.Dimension(4, 4));
    panelPointsInner.add(panelGap1);
    
    panelPointOptions.setLayout(new java.awt.BorderLayout(2, 0));
    
    panelLeft.setLayout(new java.awt.GridLayout(0, 1, 0, 2));
    
    lblGenerationType.setText("Generation Type:");
    panelLeft.add(lblGenerationType);
    
    lblFont.setText("Font:");
    panelLeft.add(lblFont);
    
    lblShapePoints.setText("Shape Points:");
    panelLeft.add(lblShapePoints);
    
    lblInternalPoints.setText("Internal Points:");
    panelLeft.add(lblInternalPoints);
    
    lblShapePointMinDensity.setText("Shape Point Min Density:");
    panelLeft.add(lblShapePointMinDensity);
    
    lblInternalMinDensity.setText("Internal Min Density:");
    panelLeft.add(lblInternalMinDensity);
    
    panelPointOptions.add(panelLeft, "West");
    
    panelCenter.setLayout(new java.awt.GridLayout(0, 1, 0, 2));
    
    panelGenerationType.setLayout(new java.awt.BorderLayout());
    
    groupGenerationType.add(optLetterGeneration);
    optLetterGeneration.setSelected(true);
    optLetterGeneration.setText("Letter");
    optLetterGeneration.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        VoronoiTest.this.optLetterGenerationActionPerformed(evt);
      }
      
    });
    panelGenerationType.add(optLetterGeneration, "West");
    
    groupGenerationType.add(optCountryGeneration);
    optCountryGeneration.setText("Country");
    optCountryGeneration.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        VoronoiTest.this.optCountryGenerationActionPerformed(evt);
      }
      
    });
    panelGenerationType.add(optCountryGeneration, "Center");
    
    panelCenter.add(panelGenerationType);
    
    cboFont.setEditable(true);
    cboFont.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Arial", "Courier New", "Garamond", "Times New Roman", "Lucida Console" }));
    cboFont.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        VoronoiTest.this.cboFontActionPerformed(evt);
      }
      
    });
    panelCenter.add(cboFont);
    
    cboShapePoints.setEditable(true);
    cboShapePoints.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "No Points", "10", "25", "50", "100", "250", "Maximum Possible" }));
    cboShapePoints.setSelectedIndex(6);
    cboShapePoints.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        VoronoiTest.this.cboShapePointsActionPerformed(evt);
      }
      
    });
    panelCenter.add(cboShapePoints);
    
    cboInternalPoints.setEditable(true);
    cboInternalPoints.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "No Points", "10", "25", "50", "100", "250", "Maximum Possible" }));
    cboInternalPoints.setSelectedIndex(6);
    cboInternalPoints.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        VoronoiTest.this.cboInternalPointsActionPerformed(evt);
      }
      
    });
    panelCenter.add(cboInternalPoints);
    
    cboShapePointMinDensity.setEditable(true);
    cboShapePointMinDensity.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "8", "10", "12", "15", "20", "25", "30", "40", "50", "100", "250" }));
    cboShapePointMinDensity.setSelectedIndex(9);
    cboShapePointMinDensity.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        VoronoiTest.this.cboShapePointMinDensityActionPerformed(evt);
      }
      
    });
    panelCenter.add(cboShapePointMinDensity);
    
    cboInternalMinDensity.setEditable(true);
    cboInternalMinDensity.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "8", "10", "12", "15", "20", "25", "30", "40", "50", "100", "250" }));
    cboInternalMinDensity.setSelectedIndex(9);
    cboInternalMinDensity.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        VoronoiTest.this.cboInternalMinDensityActionPerformed(evt);
      }
      
    });
    panelCenter.add(cboInternalMinDensity);
    
    panelPointOptions.add(panelCenter, "Center");
    
    panelPointsInner.add(panelPointOptions);
    
    panelGap.setLayout(null);
    
    panelGap.setPreferredSize(new java.awt.Dimension(4, 4));
    panelPointsInner.add(panelGap);
    
    panelClearPoints.setLayout(new java.awt.BorderLayout());
    
    btnClearPoints.setText("Clear Points");
    btnClearPoints.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        VoronoiTest.this.btnClearPointsActionPerformed(evt);
      }
      
    });
    panelClearPoints.add(btnClearPoints, "South");
    
    panelPointsInner.add(panelClearPoints);
    
    panelCheckBoxes.setLayout(new java.awt.GridLayout(0, 1));
    
    chkShowPoints.setSelected(true);
    chkShowPoints.setText("Show Points");
    chkShowPoints.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        VoronoiTest.this.chkShowPointsActionPerformed(evt);
      }
      
    });
    panelCheckBoxes.add(chkShowPoints);
    
    chkShowExpectedBorder.setSelected(true);
    chkShowExpectedBorder.setText("Show Expected Border");
    chkShowExpectedBorder.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        VoronoiTest.this.chkShowExpectedBorderActionPerformed(evt);
      }
      
    });
    panelCheckBoxes.add(chkShowExpectedBorder);
    
    chkShowIntersection.setSelected(true);
    chkShowIntersection.setText("Show Intersection (L2 Norm)");
    chkShowIntersection.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        VoronoiTest.this.chkShowIntersectionActionPerformed(evt);
      }
      
    });
    panelCheckBoxes.add(chkShowIntersection);
    
    chkAddShapePointsToSplitLongLines.setSelected(true);
    chkAddShapePointsToSplitLongLines.setText("Add Shape Points to Split Long Lines");
    chkAddShapePointsToSplitLongLines.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        VoronoiTest.this.chkAddShapePointsToSplitLongLinesActionPerformed(evt);
      }
      
    });
    panelCheckBoxes.add(chkAddShapePointsToSplitLongLines);
    
    panelPointsInner.add(panelCheckBoxes);
    
    panelPoints.add(panelPointsInner, "Center");
    

    panelRepresentations.setLayout(new java.awt.GridLayout(0, 1));
    
    panelRepresentations.setBorder(javax.swing.BorderFactory.createTitledBorder("Representations"));
    groupRepresentations.add(optNone);
    optNone.setSelected(true);
    optNone.setText("None");
    optNone.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        VoronoiTest.this.optNoneActionPerformed(evt);
      }
      
    });
    panelRepresentations.add(optNone);
    
    groupRepresentations.add(optVoronoiCells);
    optVoronoiCells.setText("Voronoi");
    optVoronoiCells.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        VoronoiTest.this.optVoronoiCellsActionPerformed(evt);
      }
      
    });
    panelRepresentations.add(optVoronoiCells);
    
    groupRepresentations.add(optSimpleTriangulation);
    optSimpleTriangulation.setText("Triangulation");
    optSimpleTriangulation.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        VoronoiTest.this.optSimpleTriangulationActionPerformed(evt);
      }
      
    });
    panelRepresentations.add(optSimpleTriangulation);
    
    groupRepresentations.add(optEdgeRemoval);
    optEdgeRemoval.setText("Edge Removal");
    optEdgeRemoval.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        VoronoiTest.this.optEdgeRemovalActionPerformed(evt);
      }
      
    });
    panelRepresentations.add(optEdgeRemoval);
    
    groupRepresentations.add(optClustering);
    optClustering.setText("Clustering");
    optClustering.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        VoronoiTest.this.optClusteringActionPerformed(evt);
      }
      
    });
    panelRepresentations.add(optClustering);
    

    panelEdgeRemoval.setLayout(new java.awt.BorderLayout());
    
    panelEdgeRemoval.setBorder(javax.swing.BorderFactory.createTitledBorder("Edge Removal"));
    jPanel4.setLayout(new java.awt.BorderLayout());
    
    jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, 1));
    
    groupEdgeRemoval.add(optNoLengthRestriction);
    
    optNoLengthRestriction.setText("No Length Cut-off");
    optNoLengthRestriction.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        VoronoiTest.this.optNoLengthRestrictionActionPerformed(evt);

      }
      

    });
    groupEdgeRemoval.add(optUserLengthRestriction);
    optUserLengthRestriction.setText("User Length Cut-off");
    optUserLengthRestriction.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        VoronoiTest.this.optUserLengthRestrictionActionPerformed(evt);

      }
      

    });
    jPanel4.add(jPanel1, "North");
    
    jPanel2.setLayout(new java.awt.BorderLayout());
    
    panelGapWest.setLayout(null);
    
    panelGapWest.setPreferredSize(new java.awt.Dimension(16, 5));
    jPanel2.add(panelGapWest, "West");
    
    sliderLengthRestriction.setMajorTickSpacing(100);
    sliderLengthRestriction.setPaintLabels(true);
    sliderLengthRestriction.setPaintTicks(true);
    sliderLengthRestriction.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        VoronoiTest.this.sliderLengthRestrictionStateChanged(evt);

      }
      

    });
    panelGapSouth.setLayout(null);
    
    panelGapSouth.setPreferredSize(new java.awt.Dimension(2, 2));
    jPanel2.add(panelGapSouth, "South");
    
    jPanel4.add(jPanel2, "Center");
    
    jPanel3.setLayout(new java.awt.BorderLayout());
    
    groupEdgeRemoval.add(optNormalisedLengthRestriction);
    optNormalisedLengthRestriction.setText("Normalised Length");
    optNormalisedLengthRestriction.setSelected(true);
    optNormalisedLengthRestriction.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        VoronoiTest.this.optNormalisedLengthRestrictionActionPerformed(evt);
      }
      
    });
    jPanel3.add(optNormalisedLengthRestriction, "North");
    
    panelGapWest2.setLayout(null);
    
    panelGapWest2.setPreferredSize(new java.awt.Dimension(16, 5));
    jPanel3.add(panelGapWest2, "West");
    
    sliderNormalisedLengthRestriction.setMajorTickSpacing(25);
    sliderNormalisedLengthRestriction.setMinorTickSpacing(5);
    sliderNormalisedLengthRestriction.setPaintLabels(true);
    sliderNormalisedLengthRestriction.setPaintTicks(true);
    sliderNormalisedLengthRestriction.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        VoronoiTest.this.sliderNormalisedLengthRestrictionStateChanged(evt);
      }
      
    });
    jPanel3.add(sliderNormalisedLengthRestriction, "Center");
    
    panelGapSouth1.setLayout(null);
    
    panelGapSouth1.setPreferredSize(new java.awt.Dimension(2, 2));
    jPanel3.add(panelGapSouth1, "South");
    
    jPanel4.add(jPanel3, "South");
    
    panelEdgeRemoval.add(jPanel4, "North");
    
    jPanel7.setLayout(new javax.swing.BoxLayout(jPanel7, 1));
    
    groupEdgeRemoval.add(optMaxEdgeOfMinSpanningTree);
    optMaxEdgeOfMinSpanningTree.setText("Max Edge of Minimum Spanning Tree");
    optMaxEdgeOfMinSpanningTree.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        VoronoiTest.this.optMaxEdgeOfMinSpanningTreeActionPerformed(evt);
      }
      
    });
    jPanel7.add(optMaxEdgeOfMinSpanningTree);
    
    groupEdgeRemoval.add(optMaxEdgeOfSmallestTEdge);
    optMaxEdgeOfSmallestTEdge.setText("Max of Smallest Triangle Edge");
    optMaxEdgeOfSmallestTEdge.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        VoronoiTest.this.optMaxEdgeOfSmallestTEdgeActionPerformed(evt);
      }
      
    });
    jPanel7.add(optMaxEdgeOfSmallestTEdge);
    

    optApplyAboveMSTAndSmallestTEdgeInProportion.setText("Apply above two in following proportion");
    optApplyAboveMSTAndSmallestTEdgeInProportion.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        VoronoiTest.this.optApplyAboveMSTAndSmallestTEdgeInProportionActionPerformed(evt);

      }
      

    });
    panelEdgeRemoval.add(jPanel7, "Center");
    
    jPanel5.setLayout(new java.awt.BorderLayout());
    
    panelGapWest1.setLayout(null);
    
    panelGapWest1.setPreferredSize(new java.awt.Dimension(16, 5));
    jPanel5.add(panelGapWest1, "West");
    
    sliderApplyInProportion.setMajorTickSpacing(10);
    sliderApplyInProportion.setMinorTickSpacing(1);
    sliderApplyInProportion.setPaintLabels(true);
    sliderApplyInProportion.setPaintTicks(true);
    sliderApplyInProportion.setValue(15);
    sliderApplyInProportion.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        VoronoiTest.this.sliderApplyInProportionStateChanged(evt);

      }
      

    });
    panelEdgeRemoval.add(jPanel5, "South");
    

    panelEdgeRemovalOptions.setLayout(new javax.swing.BoxLayout(panelEdgeRemovalOptions, 1));
    
    panelEdgeRemovalOptions.setBorder(javax.swing.BorderFactory.createTitledBorder("Edge Removal Options"));
    jPanel8.setLayout(new java.awt.GridLayout(0, 1));
    
    chkShowEdgeLengths.setText("Show Edge Lengths");
    chkShowEdgeLengths.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        VoronoiTest.this.chkShowEdgeLengthsActionPerformed(evt);
      }
      
    });
    jPanel8.add(chkShowEdgeLengths);
    
    chkShowInternalTriangles.setText("Show Internal Triangles");
    chkShowInternalTriangles.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        VoronoiTest.this.chkShowInternalTrianglesActionPerformed(evt);
      }
      
    });
    jPanel8.add(chkShowInternalTriangles);
    
    chkShowMinimumSpanningTree.setText("Show Minimum Spanning Tree");
    chkShowMinimumSpanningTree.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        VoronoiTest.this.chkShowMinimumSpanningTreeActionPerformed(evt);
      }
      
    });
    jPanel8.add(chkShowMinimumSpanningTree);
    
    chkShowDebugInfo.setText("Show Debug Information");
    chkShowDebugInfo.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        VoronoiTest.this.chkShowDebugInfoActionPerformed(evt);
      }
      
    });
    jPanel8.add(chkShowDebugInfo);
    
    chkMaxEdgesToRemove.setText("Max Edges to Remove");
    chkMaxEdgesToRemove.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        VoronoiTest.this.chkMaxEdgesToRemoveActionPerformed(evt);
      }
      
    });
    jPanel8.add(chkMaxEdgesToRemove);
    
    panelEdgeRemovalOptions.add(jPanel8);
    
    jPanel9.setLayout(new java.awt.BorderLayout());
    
    jPanel10.setLayout(null);
    
    jPanel10.setPreferredSize(new java.awt.Dimension(16, 5));
    jPanel9.add(jPanel10, "West");
    
    sliderMaxEdgesToRemove.setMajorTickSpacing(100);
    sliderMaxEdgesToRemove.setMaximum(500);
    sliderMaxEdgesToRemove.setMinorTickSpacing(1);
    sliderMaxEdgesToRemove.setPaintLabels(true);
    sliderMaxEdgesToRemove.setPaintTicks(true);
    sliderMaxEdgesToRemove.setValue(0);
    sliderMaxEdgesToRemove.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        VoronoiTest.this.sliderMaxEdgesToRemoveStateChanged(evt);
      }
      
    });
    jPanel9.add(sliderMaxEdgesToRemove, "Center");
    
    panelEdgeRemovalOptions.add(jPanel9);
    
    panelOptions.setLayout(new java.awt.GridLayout(0, 1));
    
    panelOptions.setBorder(javax.swing.BorderFactory.createTitledBorder("Debug Options"));
    chkShowMouseLocation.setText("Show Mouse Location");
    chkShowMouseLocation.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        VoronoiTest.this.chkShowMouseLocationActionPerformed(evt);
      }
      
    });
    panelOptions.add(chkShowMouseLocation);
    
    chkShowPointCoordinates.setText("Show Coordinates");
    chkShowPointCoordinates.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        VoronoiTest.this.chkShowPointCoordinatesActionPerformed(evt);
      }
      
    });
    panelOptions.add(chkShowPointCoordinates);
    
    chkShowSweepline.setText("Show Sweepline");
    chkShowSweepline.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        VoronoiTest.this.chkShowSweeplineActionPerformed(evt);
      }
      
    });
    panelOptions.add(chkShowSweepline);
    
    chkShowTreeStructure.setText("Show Tree Structure");
    chkShowTreeStructure.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        VoronoiTest.this.chkShowTreeStructureActionPerformed(evt);
      }
      
    });
    panelOptions.add(chkShowTreeStructure);
    
    chkShowCircleEvents.setText("Show Circle Events");
    chkShowCircleEvents.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        VoronoiTest.this.chkShowCircleEventsActionPerformed(evt);
      }
      
    });
    panelOptions.add(chkShowCircleEvents);
    







    panelTop.add(panelEdgeRemoval);
    panelTop.add(panelStatistics);
    panelTop.add(panelPoints);
    panelTop.add(panelOptions);
    panelTop.add(panelRepresentations);
    panelTop.add(panelEdgeRemovalOptions);
    

    panelRight.add(panelTop, "North");
    
    scrollRight.setViewportView(panelRight);
    
    getContentPane().add(scrollRight, "East");
  }
  
  private void chkShowMouseLocationActionPerformed(ActionEvent evt)
  {
    SHOW_MOUSE_LOCATION = chkShowMouseLocation.isSelected();
    repaint();
  }
  
  private void chkShowPointsActionPerformed(ActionEvent evt) {
    SHOW_POINTS = chkShowPoints.isSelected();
    repaint();
  }
  
  private void optNormalisedLengthRestrictionActionPerformed(ActionEvent evt) {
    updateControls();
  }
  
  private void sliderNormalisedLengthRestrictionStateChanged(javax.swing.event.ChangeEvent evt)
  {
    repaint();
  }
  
  private void btnExportToSVGActionPerformed(ActionEvent evt) {}
  
  private void cboInternalMinDensityActionPerformed(ActionEvent evt)
  {
    btnGenerate.doClick();
  }
  
  private void cboShapePointsActionPerformed(ActionEvent evt) {
    btnGenerate.doClick();
  }
  
  private void btnExitActionPerformed(ActionEvent evt) {
    System.exit(0);
  }
  
  private void chkShowIntersectionActionPerformed(ActionEvent evt) {
    SHOW_INTERSECTION_WITH_EXPECTED = chkShowIntersection.isSelected();
    repaint();
  }
  
  private void chkAddShapePointsToSplitLongLinesActionPerformed(ActionEvent evt) {
    btnGenerate.doClick();
  }
  
  private void cboCountriesActionPerformed(ActionEvent evt) {
    btnGenerate.doClick();
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
  
  private void btnLoadActionPerformed(ActionEvent evt) {
    JFileChooser chooser = new JFileChooser();
    chooser.setDialogType(0);
    chooser.setDialogTitle("Choose Points File to Load");
    String directory = lastdirectoryopened != null ? lastdirectoryopened : chooser.getCurrentDirectory().getAbsolutePath();
    chooser.setSelectedFile(new java.io.File(directory + java.io.File.separator + "pointsfile.txt"));
    
    int returnval = chooser.showOpenDialog(this);
    if (returnval == 0) {
      java.io.File file = chooser.getSelectedFile();
      

      lastdirectoryopened = file.getPath();
      
      boolean loadsuccessful;
      try
      {
        loadPoints(file.getAbsolutePath());
        loadsuccessful = true;
      } catch (IOException e) { boolean loadsuccessful;
        displayError(e);
        loadsuccessful = false;
      }
      

      panel.repaint();
      

      if (loadsuccessful) {
        try {
          savePoints();
        } catch (IOException e) {
          displayError(e);
        }
      }
    }
  }
  
  private void btnSaveActionPerformed(ActionEvent evt) {
    JFileChooser chooser = new JFileChooser();
    chooser.setDialogType(1);
    chooser.setDialogTitle("Choose location to Save Points File");
    String directory = lastdirectoryopened != null ? lastdirectoryopened : chooser.getCurrentDirectory().getAbsolutePath();
    chooser.setSelectedFile(new java.io.File(directory + java.io.File.separator + "pointsfile.txt"));
    
    int returnval = chooser.showSaveDialog(this);
    if (returnval == 0) {
      java.io.File file = chooser.getSelectedFile();
      

      lastdirectoryopened = file.getPath();
      
      try
      {
        savePoints(file.getAbsolutePath());
      } catch (IOException e) {
        displayError(e);
      }
    }
  }
  
  private void btnExportActionPerformed(ActionEvent evt) {
    JFileChooser chooser = new JFileChooser();
    chooser.setDialogType(1);
    chooser.setDialogTitle("Choose location to Save Points File");
    String directory = lastdirectoryopened != null ? lastdirectoryopened : chooser.getCurrentDirectory().getAbsolutePath();
    chooser.setSelectedFile(new java.io.File(directory + java.io.File.separator + "pointsfile.txt"));
    
    int returnval = chooser.showSaveDialog(this);
    if (returnval == 0) {
      java.io.File file = chooser.getSelectedFile();
      

      lastdirectoryopened = file.getPath();
      
      try
      {
        exportPoints(file.getAbsolutePath());
      } catch (IOException e) {
        displayError(e);
      }
    }
  }
  
  private void cboFontActionPerformed(ActionEvent evt) {
    if (optLetterGeneration.isSelected()) {
      btnGenerate.doClick();
    }
  }
  
  private void chkShowExpectedBorderActionPerformed(ActionEvent evt) {
    SHOW_EXPECTED_BORDER = chkShowExpectedBorder.isSelected();
    repaint();
  }
  
  private void cboShapePointMinDensityActionPerformed(ActionEvent evt) {
    btnGenerate.doClick();
  }
  
  private void cboInternalPointsActionPerformed(ActionEvent evt) {
    btnGenerate.doClick();
  }
  
  private void btnGenerateActionPerformed(ActionEvent evt) {
    signalprocesser.shared.StatusDialog.start(this, "Generating Shape", "Please wait while shape is generated", new Thread()
    {
      public void run() {
        java.awt.Rectangle shapebounds = new java.awt.Rectangle(120, 60, panel.getWidth() - 240, panel.getHeight() - 120);
        

        java.awt.Font font = new java.awt.Font((String)cboFont.getSelectedItem(), 1, 200);
        

        int shapepoint_mindensity = Integer.parseInt((String)cboShapePointMinDensity.getSelectedItem());
        
        String strshapepoints = ((String)cboShapePoints.getSelectedItem()).toLowerCase();
        int shapepoints; if (strshapepoints.startsWith("n")) {
          shapepoints = 0; } else { int shapepoints;
          if (strshapepoints.startsWith("m")) {
            shapepoints = Integer.MAX_VALUE;
          } else {
            try {
              shapepoints = Integer.parseInt(strshapepoints);
            } catch (NumberFormatException e) { int shapepoints;
              VoronoiTest.this.displayError("Unrecognised number of points required - \"" + strshapepoints + "\""); return;
            }
          }
        }
        
        int shapepoints;
        int internal_mindensity = Integer.parseInt((String)cboInternalMinDensity.getSelectedItem());
        
        String strinternalpoints = ((String)cboInternalPoints.getSelectedItem()).toLowerCase();
        int internalpoints; if (strinternalpoints.startsWith("n")) {
          internalpoints = 0; } else { int internalpoints;
          if (strinternalpoints.startsWith("m")) {
            internalpoints = Integer.MAX_VALUE;
          } else {
            try {
              internalpoints = Integer.parseInt(strinternalpoints);
            } catch (NumberFormatException e) { int internalpoints;
              VoronoiTest.this.displayError("Unrecognised number of points required - \"" + strinternalpoints + "\""); return;
            }
          }
        }
        
        int internalpoints;
        points = null;
        borderpoints = null;
        

        ArrayList<VPoint> newborderpoints = null;
        if (optLetterGeneration.isSelected()) {
          try {
            newborderpoints = signalprocesser.voronoi.shapegeneration.ShapeGeneration.createShapeOutline(txtLetter.getText(), shapebounds, font);
          }
          catch (signalprocesser.voronoi.shapegeneration.ShapeGenerationException e)
          {
            VoronoiTest.this.displayError(e);
            return;
          }
        } else if (optCountryGeneration.isSelected())
        {
          String countryfile = countrylistmodel.getSelectedCountry();
          
          try
          {
            newborderpoints = signalprocesser.voronoi.tools.CountryData.getCountryData(countryfile, shapebounds);
          } catch (IOException e) {
            VoronoiTest.this.displayError(e);
            return;
          }
        } else {
          VoronoiTest.this.displayError("Unknown generation type selected");
          return;
        }
        

        ArrayList<VPoint> newpoints = null;
        try {
          boolean splitlonglines = chkAddShapePointsToSplitLongLines.isSelected();
          newpoints = signalprocesser.voronoi.shapegeneration.ShapeGeneration.addRandomPoints(newborderpoints, splitlonglines, 
            shapepoints, shapepoint_mindensity, 
            internalpoints, internal_mindensity);
        } catch (signalprocesser.voronoi.shapegeneration.ShapeGenerationException e) {
          VoronoiTest.this.displayError(e);
          return;
        }
        

        expectedarea = VoronoiShared.calculateAreaOfShape(newborderpoints);
        txtExpectedArea.setText(String.format("%.1f", new Object[] { Double.valueOf(expectedarea) }) + " pixels^2");
        txtExpectedPerimeter.setText(String.format("%.1f", new Object[] { Double.valueOf(VoronoiShared.calculatePerimeterOfShape(newborderpoints)) }) + " pixels");
        

        borderpoints = newborderpoints;
        if (optNone.isSelected()) {
          points = newpoints;
        } else if (optVoronoiCells.isSelected()) {
          points = RepresentationFactory.convertPointsToVoronoiCellPoints(newpoints);
        } else if (optSimpleTriangulation.isSelected()) {
          points = RepresentationFactory.convertPointsToSimpleTriangulationPoints(newpoints);
        } else if (optEdgeRemoval.isSelected()) {
          points = RepresentationFactory.convertPointsToTriangulationPoints(newpoints);
        }
        else if (optClustering.isSelected()) {
          points = RepresentationFactory.convertPointsToTriangulationPoints(newpoints);
        } else {
          throw new RuntimeException("Unknown option selected");
        }
        
        try
        {
          VoronoiTest.this.savePoints();
        } catch (IOException e2) {
          VoronoiTest.this.displayError(e2);
        }
        

        repaint();
        

        VoronoiTest.this.updateControls();
      }
      
    });
    repaint();
    

    updateControls();
  }
  
  private void txtLetterKeyTyped(java.awt.event.KeyEvent evt) {
    txtLetter.setText(Character.toString(evt.getKeyChar()));
    evt.consume();
    btnGenerate.doClick();
  }
  
  private void optClusteringActionPerformed(ActionEvent evt) {
    enableEdgeRemovePanel(false);
    points = RepresentationFactory.convertPointsToTriangulationPoints(points);
    representation = RepresentationFactory.createTriangulationRepresentation();
    ((TriangulationRepresentation)representation).setDetermineClustersMode();
    repaint();
  }
  
  private void sliderApplyInProportionStateChanged(javax.swing.event.ChangeEvent evt)
  {
    repaint();
  }
  
  private void optApplyAboveMSTAndSmallestTEdgeInProportionActionPerformed(ActionEvent evt) {
    updateControls();
  }
  
  private void chkShowMinimumSpanningTreeActionPerformed(ActionEvent evt) {
    if (representation == null) return;
    TriangulationRepresentation triangularrep = (TriangulationRepresentation)representation;
    if (chkShowMinimumSpanningTree.isSelected()) {
      triangularrep.setDetermineMinSpanningTreeMode();
    } else {
      triangularrep.setReduceOuterBoundariesMode();
    }
    repaint();
  }
  
  private void chkShowDebugInfoActionPerformed(ActionEvent evt) {
    TriangulationRepresentation.SHOW_DEBUG_INFO = chkShowDebugInfo.isSelected();
    repaint();
  }
  
  private void sliderMaxEdgesToRemoveStateChanged(javax.swing.event.ChangeEvent evt) {
    TriangulationRepresentation.MAX_EDGES_TO_REMOVE = sliderMaxEdgesToRemove.getValue();
    repaint();
  }
  
  private void chkMaxEdgesToRemoveActionPerformed(ActionEvent evt) {
    if (chkMaxEdgesToRemove.isSelected()) {
      sliderMaxEdgesToRemove.setEnabled(true);
      TriangulationRepresentation.MAX_EDGES_TO_REMOVE = sliderMaxEdgesToRemove.getValue();
    } else {
      sliderMaxEdgesToRemove.setEnabled(false);
      TriangulationRepresentation.MAX_EDGES_TO_REMOVE = -1;
    }
    repaint();
  }
  
  private void chkShowInternalTrianglesActionPerformed(ActionEvent evt) {
    TriangulationRepresentation.SHOW_INTERNAL_TRIANGLES = chkShowInternalTriangles.isSelected();
    repaint();
  }
  
  private void chkShowEdgeLengthsActionPerformed(ActionEvent evt) {
    TriangulationRepresentation.SHOW_EDGE_LENGTHS = chkShowEdgeLengths.isSelected();
    repaint();
  }
  
  private void updateControls() {
    if (optEdgeRemoval.isSelected()) {
      TriangulationRepresentation trianglarrep = (TriangulationRepresentation)representation;
      

      if ((trianglarrep.getMinLength() > 0) && (trianglarrep.getMaxLength() > 0)) {
        sliderLengthRestriction.setMinimum(trianglarrep.getMinLength() - 1);
        sliderLengthRestriction.setMaximum((int)(trianglarrep.getMaxLength() * 1.25D));
      }
      
      signalprocesser.voronoi.representation.triangulation.TriangulationRepresentation.CalcCutOff calccutoff;
      
      if (optNoLengthRestriction.isSelected()) {
        sliderApplyInProportion.setEnabled(false);
        sliderLengthRestriction.setEnabled(false);
        sliderNormalisedLengthRestriction.setEnabled(false);
        calccutoff = new signalprocesser.voronoi.representation.triangulation.TriangulationRepresentation.CalcCutOff() {
          public int calculateCutOff(TriangulationRepresentation rep) {
            int val = 0;
            VoronoiTest.this.updateLengthSlider(rep, val);
            VoronoiTest.this.updateNormalisedLengthSlider(rep, val);
            return val;
          }
        }; } else { signalprocesser.voronoi.representation.triangulation.TriangulationRepresentation.CalcCutOff calccutoff;
        if (optUserLengthRestriction.isSelected()) {
          sliderApplyInProportion.setEnabled(false);
          sliderLengthRestriction.setEnabled(true);
          sliderNormalisedLengthRestriction.setEnabled(false);
          calccutoff = new signalprocesser.voronoi.representation.triangulation.TriangulationRepresentation.CalcCutOff()
          {
            public int calculateCutOff(TriangulationRepresentation rep) {
              if ((rep.getMinLength() > 0) && (rep.getMaxLength() > 0)) {
                sliderLengthRestriction.setMinimum(rep.getMinLength() - 1);
                sliderLengthRestriction.setMaximum((int)(rep.getMaxLength() * 1.25D));
              }
              

              int val = sliderLengthRestriction.getValue();
              
              VoronoiTest.this.updateNormalisedLengthSlider(rep, val);
              return val;
            }
          }; } else { signalprocesser.voronoi.representation.triangulation.TriangulationRepresentation.CalcCutOff calccutoff;
          if (optNormalisedLengthRestriction.isSelected()) {
            sliderApplyInProportion.setEnabled(false);
            sliderLengthRestriction.setEnabled(false);
            sliderNormalisedLengthRestriction.setEnabled(true);
            calccutoff = new signalprocesser.voronoi.representation.triangulation.TriangulationRepresentation.CalcCutOff()
            {
              public int calculateCutOff(TriangulationRepresentation rep) {
                double percentage = sliderNormalisedLengthRestriction.getValue() / 100.0D;
                double min = rep.getMinLength();
                double max = rep.getMaxLength();
                

                int val = (int)(percentage * (max - min) + min);
                

                VoronoiTest.this.updateLengthSlider(rep, val);
                
                return val;
              }
            }; } else { signalprocesser.voronoi.representation.triangulation.TriangulationRepresentation.CalcCutOff calccutoff;
            if (optMaxEdgeOfMinSpanningTree.isSelected()) {
              sliderApplyInProportion.setEnabled(false);
              sliderLengthRestriction.setEnabled(false);
              sliderNormalisedLengthRestriction.setEnabled(false);
              calccutoff = new signalprocesser.voronoi.representation.triangulation.TriangulationRepresentation.CalcCutOff() {
                public int calculateCutOff(TriangulationRepresentation rep) {
                  int val = rep.getMaxLengthOfMinimumSpanningTree();
                  VoronoiTest.this.updateLengthSlider(rep, val);
                  VoronoiTest.this.updateNormalisedLengthSlider(rep, val);
                  
                  java.io.BufferedWriter out = null;
                  
                  try
                  {
                    java.io.FileWriter fstream = new java.io.FileWriter("calccutoff.txt");
                    out = new java.io.BufferedWriter(fstream);
                    out.write(Integer.toString(val));
                    out.close();
                  } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage());
                  }
                  return val;
                }
              }; } else { signalprocesser.voronoi.representation.triangulation.TriangulationRepresentation.CalcCutOff calccutoff;
              if (optMaxEdgeOfSmallestTEdge.isSelected()) {
                sliderApplyInProportion.setEnabled(false);
                sliderLengthRestriction.setEnabled(false);
                sliderNormalisedLengthRestriction.setEnabled(false);
                calccutoff = new signalprocesser.voronoi.representation.triangulation.TriangulationRepresentation.CalcCutOff() {
                  public int calculateCutOff(TriangulationRepresentation rep) {
                    int val = rep.getMaxLengthOfSmallestTriangleEdge();
                    VoronoiTest.this.updateLengthSlider(rep, val);
                    VoronoiTest.this.updateNormalisedLengthSlider(rep, val);
                    return val;
                  }
                }; } else { signalprocesser.voronoi.representation.triangulation.TriangulationRepresentation.CalcCutOff calccutoff;
                if (optApplyAboveMSTAndSmallestTEdgeInProportion.isSelected()) {
                  sliderApplyInProportion.setEnabled(true);
                  sliderLengthRestriction.setEnabled(false);
                  sliderNormalisedLengthRestriction.setEnabled(false);
                  calccutoff = new signalprocesser.voronoi.representation.triangulation.TriangulationRepresentation.CalcCutOff() {
                    public int calculateCutOff(TriangulationRepresentation rep) {
                      double proportion = sliderApplyInProportion.getValue() / 100.0D;
                      int val = (int)(
                        rep.getMaxLengthOfMinimumSpanningTree() * (1.0D - proportion) + 
                        rep.getMaxLengthOfSmallestTriangleEdge() * proportion);
                      VoronoiTest.this.updateLengthSlider(rep, val);
                      VoronoiTest.this.updateNormalisedLengthSlider(rep, val);
                      return val;
                    }
                  };
                } else {
                  displayError("Unknown selection option"); return;
                }
              }
            } } } }
      signalprocesser.voronoi.representation.triangulation.TriangulationRepresentation.CalcCutOff calccutoff;
      trianglarrep.setCalcCutOff(calccutoff);
      

      repaint();
    }
    else {}
  }
  

  private void updateLengthSlider(TriangulationRepresentation rep, int cutoff)
  {
    int min = rep.getMinLength();
    int max = rep.getMaxLength();
    if ((min > 0) && (max > 0)) {
      sliderLengthRestriction.setMinimum(min - 1);
      sliderLengthRestriction.setMaximum((int)(max * 1.25D));
    }
    

    sliderLengthRestriction.setValue(cutoff);
  }
  
  private void updateNormalisedLengthSlider(TriangulationRepresentation rep, int cutoff)
  {
    int min = rep.getMinLength();
    int max = rep.getMaxLength();
    if ((min <= 0) && (max <= 0)) { return;
    }
    
    int percentage = (int)((cutoff - min) / (max - min) * 100.0D);
    sliderNormalisedLengthRestriction.setValue(percentage);
  }
  
  private void optMaxEdgeOfSmallestTEdgeActionPerformed(ActionEvent evt) {
    updateControls();
  }
  
  private void optMaxEdgeOfMinSpanningTreeActionPerformed(ActionEvent evt) {
    updateControls();
  }
  
  private void sliderLengthRestrictionStateChanged(javax.swing.event.ChangeEvent evt)
  {
    repaint();
  }
  
  private void optUserLengthRestrictionActionPerformed(ActionEvent evt) {
    updateControls();
  }
  
  private void optNoLengthRestrictionActionPerformed(ActionEvent evt) {
    updateControls();
  }
  
  private void optEdgeRemovalActionPerformed(ActionEvent evt)
  {
    points = RepresentationFactory.convertPointsToTriangulationPoints(points);
    representation = RepresentationFactory.createTriangulationRepresentation();
    

    enableEdgeRemovePanel(true);
    

    optApplyAboveMSTAndSmallestTEdgeInProportion.setSelected(true);
    optApplyAboveMSTAndSmallestTEdgeInProportionActionPerformed(null);
    

    repaint();
  }
  
  private void enableEdgeRemovePanel(boolean flag) {
    optNoLengthRestriction.setEnabled(flag);
    optUserLengthRestriction.setEnabled(flag);
    sliderLengthRestriction.setEnabled(flag);
    optNormalisedLengthRestriction.setEnabled(flag);
    sliderNormalisedLengthRestriction.setEnabled(flag);
    sliderApplyInProportion.setEnabled(flag);
    optMaxEdgeOfMinSpanningTree.setEnabled(flag);
    optMaxEdgeOfSmallestTEdge.setEnabled(flag);
    optApplyAboveMSTAndSmallestTEdgeInProportion.setEnabled(flag);
    chkShowEdgeLengths.setEnabled(flag);
    chkShowInternalTriangles.setEnabled(flag);
    chkShowMinimumSpanningTree.setEnabled(flag);
    chkShowDebugInfo.setEnabled(flag);
    chkMaxEdgesToRemove.setEnabled(flag);
    if ((flag) && (!chkMaxEdgesToRemove.isSelected())) {
      sliderMaxEdgesToRemove.setEnabled(false);
    } else {
      sliderMaxEdgesToRemove.setEnabled(flag);
    }
  }
  
  private void sliderAreaCutOffToUseStateChanged(javax.swing.event.ChangeEvent evt) {
    if (signalprocesser.voronoi.representation.boundaryproblem.BoundaryProblemRepresentation.VORONOICELLAREA_CUTOFF != sliderAreaCutOffToUse.getValue()) {
      signalprocesser.voronoi.representation.boundaryproblem.BoundaryProblemRepresentation.VORONOICELLAREA_CUTOFF = sliderAreaCutOffToUse.getValue();
      backupboundaryenhancedvalue = sliderAreaCutOffToUse.getValue();
      repaint();
    }
  }
  
  private void optBoundaryEnhancedActionPerformed(ActionEvent evt)
  {
    enableEdgeRemovePanel(false);
    signalprocesser.voronoi.representation.boundaryproblem.BoundaryProblemRepresentation.MIN_ANGLE_TO_ALLOW = 0.0D;
    if (sliderAreaCutOffToUse.getValue() > 0) {
      signalprocesser.voronoi.representation.boundaryproblem.BoundaryProblemRepresentation.VORONOICELLAREA_CUTOFF = sliderAreaCutOffToUse.getValue();
      backupboundaryenhancedvalue = sliderAreaCutOffToUse.getValue();
    } else if (backupboundaryenhancedvalue > 0) {
      signalprocesser.voronoi.representation.boundaryproblem.BoundaryProblemRepresentation.VORONOICELLAREA_CUTOFF = backupboundaryenhancedvalue;
      sliderAreaCutOffToUse.setValue(backupboundaryenhancedvalue);
    } else {
      backupboundaryenhancedvalue = 8000;
      signalprocesser.voronoi.representation.boundaryproblem.BoundaryProblemRepresentation.VORONOICELLAREA_CUTOFF = 8000;
      sliderAreaCutOffToUse.setValue(8000);
    }
    points = RepresentationFactory.convertPointsToBoundaryProblemPoints(points);
    representation = RepresentationFactory.createBoundaryProblemRepresentation();
    repaint();
  }
  
  private void optVoronoiCellsActionPerformed(ActionEvent evt)
  {
    enableEdgeRemovePanel(false);
    points = RepresentationFactory.convertPointsToVoronoiCellPoints(points);
    representation = RepresentationFactory.createVoronoiCellRepresentation();
    repaint();
  }
  
  private void optBoundaryUsingAngle30ActionPerformed(ActionEvent evt)
  {
    enableEdgeRemovePanel(false);
    signalprocesser.voronoi.representation.boundaryproblem.BoundaryProblemRepresentation.MIN_ANGLE_TO_ALLOW = 0.5235987755982988D;
    signalprocesser.voronoi.representation.boundaryproblem.BoundaryProblemRepresentation.VORONOICELLAREA_CUTOFF = 0;
    sliderAreaCutOffToUse.setValue(0);
    points = RepresentationFactory.convertPointsToBoundaryProblemPoints(points);
    representation = RepresentationFactory.createBoundaryProblemRepresentation();
    repaint();
  }
  
  private void optBoundaryUsingAngle20ActionPerformed(ActionEvent evt)
  {
    enableEdgeRemovePanel(false);
    signalprocesser.voronoi.representation.boundaryproblem.BoundaryProblemRepresentation.MIN_ANGLE_TO_ALLOW = 0.3490658503988659D;
    signalprocesser.voronoi.representation.boundaryproblem.BoundaryProblemRepresentation.VORONOICELLAREA_CUTOFF = 0;
    sliderAreaCutOffToUse.setValue(0);
    points = RepresentationFactory.convertPointsToBoundaryProblemPoints(points);
    representation = RepresentationFactory.createBoundaryProblemRepresentation();
    repaint();
  }
  
  private void optSimpleTriangulationActionPerformed(ActionEvent evt)
  {
    enableEdgeRemovePanel(false);
    points = RepresentationFactory.convertPointsToSimpleTriangulationPoints(points);
    representation = RepresentationFactory.createSimpleTriangulationRepresentation();
    repaint();
  }
  
  private void optBoundaryActionPerformed(ActionEvent evt)
  {
    enableEdgeRemovePanel(false);
    signalprocesser.voronoi.representation.boundaryproblem.BoundaryProblemRepresentation.MIN_ANGLE_TO_ALLOW = 0.0D;
    signalprocesser.voronoi.representation.boundaryproblem.BoundaryProblemRepresentation.VORONOICELLAREA_CUTOFF = 0;
    sliderAreaCutOffToUse.setValue(0);
    points = RepresentationFactory.convertPointsToBoundaryProblemPoints(points);
    representation = RepresentationFactory.createBoundaryProblemRepresentation();
    repaint();
  }
  
  private void optNoneActionPerformed(ActionEvent evt)
  {
    enableEdgeRemovePanel(false);
    points = RepresentationFactory.convertPointsToVPoints(points);
    representation = null;
    repaint();
  }
  
  private void chkShowPointCoordinatesActionPerformed(ActionEvent evt) {
    SHOW_POINT_COORDINATES = chkShowPointCoordinates.isSelected();
    repaint();
  }
  
  private void chkShowCircleEventsActionPerformed(ActionEvent evt) {
    SHOW_CIRCLEEVENTS = chkShowCircleEvents.isSelected();
    repaint();
  }
  
  private void chkShowTreeStructureActionPerformed(ActionEvent evt) {
    if (chkShowTreeStructure.isSelected()) {
      if (treedialog == null) {
        treedialog = new signalprocesser.voronoi.statusstructure.binarysearchtreeimpl.debug.DebugTree(this);
      }
      treedialog.setVisible(true);
    }
    else if (treedialog != null) {
      treedialog.setVisible(false);
      treedialog.dispose();
      treedialog = null;
    }
    
    repaint();
  }
  
  private void chkShowSweeplineActionPerformed(ActionEvent evt) {
    SHOW_INTERACTIVE_SWEEPLINE = chkShowSweepline.isSelected();
    if (chkShowSweepline.isSelected()) {
      chkShowTreeStructure.setEnabled(true);
    } else {
      chkShowTreeStructure.setSelected(false);
      chkShowTreeStructure.setEnabled(false);
    }
    repaint();
  }
  
  private void btnClearPointsActionPerformed(ActionEvent evt)
  {
    points.clear();
    borderpoints = null;
    try {
      savePoints();
    } catch (IOException e2) {
      displayError(e2);
    }
    

    repaint();
  }
  
  private void btnTestSuiteFormActionPerformed(ActionEvent evt) {
    signalprocesser.voronoi.tools.TestSuite form = new signalprocesser.voronoi.tools.TestSuite(false, this);
    form.setVisible(true);
  }
  

  private JButton btnTestSuiteForm;
  
  private JComboBox cboCountries;
  
  private JComboBox cboFont;
  
  private JComboBox cboInternalMinDensity;
  
  private JComboBox cboInternalPoints;
  
  private JComboBox cboShapePointMinDensity;
  
  private JComboBox cboShapePoints;
  
  private JCheckBox chkAddShapePointsToSplitLongLines;
  
  private JCheckBox chkMaxEdgesToRemove;
  
  private JCheckBox chkShowCircleEvents;
  
  private JCheckBox chkShowDebugInfo;
  
  private JCheckBox chkShowEdgeLengths;
  
  private JCheckBox chkShowExpectedBorder;
  
  private JCheckBox chkShowInternalTriangles;
  
  private JCheckBox chkShowIntersection;
  
  private JCheckBox chkShowMinimumSpanningTree;
  
  private JCheckBox chkShowMouseLocation;
  
  private JCheckBox chkShowPointCoordinates;
  
  private JCheckBox chkShowPoints;
  
  private JCheckBox chkShowSweepline;
  
  private JCheckBox chkShowTreeStructure;
  
  private ButtonGroup groupEdgeRemoval;
  
  private ButtonGroup groupGenerationType;
  private ButtonGroup groupRepresentations;
  private JPanel jPanel1;
  private JPanel jPanel10;
  private JPanel jPanel2;
  private JPanel jPanel3;
  private JPanel jPanel4;
  private JPanel jPanel5;
  private JPanel jPanel6;
  private JPanel jPanel7;
  private JPanel jPanel8;
  private JPanel jPanel9;
  private JLabel lblActualArea;
  private JLabel lblActualPerimeter;
  private JLabel lblErrorFromExpectedArea;
  private JLabel lblExpectedArea;
  private JLabel lblExpectedPerimeter;
  private JLabel lblFont;
  private JLabel lblGenerationType;
  private JLabel lblInternalMinDensity;
  private JLabel lblInternalPoints;
  private JLabel lblL2Norm;
  private JLabel lblShapePointMinDensity;
  private JLabel lblShapePoints;
  private JRadioButton optApplyAboveMSTAndSmallestTEdgeInProportion;
  private JRadioButton optBoundary;
  private JRadioButton optBoundaryEnhanced;
  private JRadioButton optBoundaryUsingAngle20;
  private JRadioButton optBoundaryUsingAngle30;
  private JRadioButton optClustering;
  private JRadioButton optCountryGeneration;
  private JRadioButton optEdgeRemoval;
  private JRadioButton optLetterGeneration;
  private JRadioButton optMaxEdgeOfMinSpanningTree;
  private JRadioButton optMaxEdgeOfSmallestTEdge;
  private JRadioButton optNoLengthRestriction;
  private JRadioButton optNone;
  private JRadioButton optNormalisedLengthRestriction;
  private JRadioButton optSimpleTriangulation;
  private JRadioButton optUserLengthRestriction;
  private JRadioButton optVoronoiCells;
  private JPanel panelActions;
  private JPanel panelActionsInner;
  private JPanel panelBoundaryEnhanced;
  private JPanel panelCenter;
  private JPanel panelCheckBoxes;
  private JPanel panelClearPoints;
  private JPanel panelEdgeRemoval;
  private JPanel panelEdgeRemovalOptions;
  private JPanel panelGap;
  private JPanel panelGap1;
  private JPanel panelGapSouth;
  private JPanel panelGapSouth1;
  private JPanel panelGapWest;
  private JPanel panelGapWest1;
  private JPanel panelGapWest2;
  private JPanel panelGenerate;
  private JPanel panelGenerationSelection;
  private JPanel panelGenerationType;
  private JPanel panelLeft;
  private JPanel panelOptions;
  private JPanel panelPointOptions;
  private JPanel panelPoints;
  private JPanel panelPointsInner;
  private JPanel panelRepresentations;
  private JPanel panelRight;
  private JPanel panelStatCaptions;
  private JPanel panelStatLabels;
  private JPanel panelStatistics;
  private JPanel panelTop;
  private javax.swing.JScrollPane scrollRight;
  private JSlider sliderApplyInProportion;
  private JSlider sliderAreaCutOffToUse;
  private JSlider sliderLengthRestriction;
  private JSlider sliderMaxEdgesToRemove;
  private JSlider sliderNormalisedLengthRestriction;
  private JLabel txtActualArea;
  private JLabel txtActualPerimeter;
  private JLabel txtErrorFromExpectedArea;
  private JLabel txtExpectedArea;
  private JLabel txtExpectedPerimeter;
  private JLabel txtL2Norm;
  private javax.swing.JTextField txtLetter;
  private void displayError(String message) {}
  
  private static void displayError(javax.swing.JFrame parent, String message) {}
  
  private void displayError(Throwable e) {}
  
  private static void displayError(javax.swing.JFrame parent, Throwable e) {}
  
  public class TestRepresentationWrapper
    implements signalprocesser.voronoi.representation.RepresentationInterface
  {
    private final ArrayList<VPoint> circleevents = new ArrayList();
    
    private signalprocesser.voronoi.representation.RepresentationInterface innerrepresentation = null;
    


    public TestRepresentationWrapper() {}
    

    public void beginAlgorithm(java.util.Collection<VPoint> points)
    {
      circleevents.clear();
      

      if (innerrepresentation != null) {
        innerrepresentation.beginAlgorithm(points);
      }
    }
    

    public void siteEvent(signalprocesser.voronoi.statusstructure.VLinkedNode n1, signalprocesser.voronoi.statusstructure.VLinkedNode n2, signalprocesser.voronoi.statusstructure.VLinkedNode n3)
    {
      if (innerrepresentation != null) {
        innerrepresentation.siteEvent(n1, n2, n3);
      }
    }
    
    public void circleEvent(signalprocesser.voronoi.statusstructure.VLinkedNode n1, signalprocesser.voronoi.statusstructure.VLinkedNode n2, signalprocesser.voronoi.statusstructure.VLinkedNode n3, int circle_x, int circle_y) {
      circleevents.add(new VPoint(circle_x, circle_y));
      

      if (innerrepresentation != null) {
        innerrepresentation.circleEvent(n1, n2, n3, circle_x, circle_y);
      }
    }
    

    public void endAlgorithm(java.util.Collection<VPoint> points, int lastsweeplineposition, signalprocesser.voronoi.statusstructure.VLinkedNode headnode)
    {
      if (innerrepresentation != null) {
        innerrepresentation.endAlgorithm(points, lastsweeplineposition, headnode);
      }
    }
  }
}
