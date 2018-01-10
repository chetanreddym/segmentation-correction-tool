package ocr.gui;

import gttool.misc.Login;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Map.Entry;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import ocr.util.DesktopUtil;

public class OCRAboutBox
  extends JDialog implements ActionListener
{
  private static final long serialVersionUID = 1L;
  Container main = getContentPane();
  
  JPanel textPanel = new JPanel();
  JPanel imagePanel = new JPanel();
  JPanel buttonPanel = new JPanel();
  JPanel emailPanel = new JPanel();
  
  JLabel titleLabel = new JLabel();
  JLabel copyrightLabel = new JLabel();
  JLabel versionLabel = new JLabel();
  JLabel contactLabel = new JLabel();
  URLLabel emailLabel = null;
  JLabel imageLabel = new JLabel();
  
  ImageIcon imageIcon;
  JButton OKButton = new JButton("OK");
  
  String GEDI_JAR = null;
  
  String version = null;
  String releaseDate = null;
  String copyright = "© Copyright 2005-2009 University of Maryland, College Park";
  
  String contact = "Contact us: ";
  String email = "Elena Zotkina <ezotkina@umiacs.umd.edu>";
  
  public OCRAboutBox() {
    GEDI_JAR = getJarfileName();
    extractVersionAndDate();
  }
  
  public OCRAboutBox(Frame parent)
  {
    super(parent, true);
    
    GEDI_JAR = getJarfileName();
    extractVersionAndDate();
    
    enableEvents(64L);
    try {
      jbInit();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    imageLabel.setIcon(imageIcon);
    
    addESCClose();
    
    pack();
    setVisible(true);
  }
  
  private void jbInit() throws Exception {
    imageIcon = Login.localOrInJar("images/GEDI_icon_about.jpg", getClass().getClassLoader());
    setTitle("About");
    
    setResizable(false);
    
    main.setLayout(new BoxLayout(main, 0));
    
    textPanel.setLayout(new BoxLayout(textPanel, 1));
    
    imagePanel.setLayout(new BorderLayout());
    
    buttonPanel.setLayout(new BorderLayout());
    
    emailPanel.setLayout(new BoxLayout(emailPanel, 0));
    
    contactLabel.setText(contact);
    titleLabel.setText("DocLib - GroundTruthing Editor and Document Interface Professional Edition (DL-GEDIPro)");
    versionLabel.setText("Version " + version + "  (" + releaseDate + ")");
    copyrightLabel.setText(copyright);
    emailLabel = new URLLabel(email);
    
    emailPanel.setAlignmentX(0.0F);
    emailPanel.add(contactLabel);
    emailPanel.add(emailLabel);
    
    int strut = 15;
    textPanel.add(Box.createVerticalStrut(strut));
    textPanel.add(titleLabel);
    textPanel.add(Box.createVerticalStrut(strut));
    textPanel.add(versionLabel);
    textPanel.add(Box.createVerticalStrut(strut));
    textPanel.add(copyrightLabel);
    textPanel.add(Box.createVerticalStrut(strut));
    textPanel.add(emailPanel);
    textPanel.add(Box.createVerticalStrut(strut));
    

    textPanel.add(OKButton);
    


    textPanel.add(Box.createVerticalStrut(strut));
    
    imagePanel.add(imageLabel, "Center");
    
    main.add(Box.createHorizontalStrut(10));
    main.add(imagePanel);
    main.add(Box.createHorizontalStrut(10));
    main.add(textPanel);
    main.add(Box.createHorizontalStrut(10));
    

    OKButton.addActionListener(this);
  }
  
  protected void processWindowEvent(WindowEvent e) {
    if (e.getID() == 201) {
      cancel();
    }
    super.processWindowEvent(e);
  }
  
  void cancel() {
    dispose();
  }
  
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == OKButton) {
      cancel();
    }
  }
  
  private void extractVersionAndDate() {
    Manifest manifest = getManifest();
    String[] values = (String[])null;
    
    if (manifest != null)
    {
      Attributes atts = manifest.getMainAttributes();
      
      for (Map.Entry<Object, Object> e : atts.entrySet()) {
        String key = e.getKey().toString();
        if (key.equals("Implementation-Version")) {
          values = e.getValue().toString().split(" ");
          break;
        }
      }
      
      if (values != null) {
        version = values[0];
        releaseDate = values[1];
      }
      else {
        version = (this.releaseDate = "Unknown");
      }
    }
  }
  






  public String getVersionNumber()
  {
    return version;
  }
  














  private Manifest getManifest()
  {
    try
    {
      JarFile jarfile = new JarFile(GEDI_JAR);
      

      return jarfile.getManifest();
    }
    catch (IOException e)
    {
      try {
        return new Manifest(new FileInputStream(
          new File(System.getProperty("user.dir") + 
          File.separator + "META-INF" + File.separator + "MANIFEST.MF")));
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    }
    return null;
  }
  



  public String getGEDIDate()
  {
    return releaseDate;
  }
  


























  public void addESCClose()
  {
    KeyStroke esc = KeyStroke.getKeyStroke('\033');
    Action actionListener = new AbstractAction() {
      public void actionPerformed(ActionEvent actionEvent) {
        dispose();
      }
    };
    InputMap inputMap = getRootPane().getInputMap(2);
    inputMap.put(esc, "ESCAPE");
    getRootPane().getActionMap().put("ESCAPE", actionListener);
  }
  
  public class URLLabel extends JLabel
  {
    public String text;
    
    public URLLabel(final String text) {
      super();
      super.setForeground(Color.BLUE);
      
      this.text = text;
      
      super.addMouseListener(new MouseAdapter() {
        public void mouseEntered(MouseEvent me) {
          me.getComponent().setCursor(
            Cursor.getPredefinedCursor(12));
        }
        
        public void mouseClicked(MouseEvent me) { DesktopUtil desktop = new DesktopUtil();
          

          if (desktop._getDesktop() == null) {
            String msg = "Default email client cannot be launched automatically.";
            JOptionPane.showMessageDialog(OCRInterface.this_interface, 
              msg, 
              "Send email", 
              1);
          }
          
          desktop.launchMail(null, text);
        }
      });
    }
  }
  

  public String getJarfileName()
  {
    URL outputURL = getClass().getProtectionDomain().getCodeSource().getLocation();
    String outputString = outputURL.toString();
    
    int index1 = outputString.indexOf(":");
    int index2 = outputString.lastIndexOf(":");
    String[] parseString; String[] parseString; if (index1 != index2) {
      parseString = outputString.split("file:/");
    } else
      parseString = outputString.split("file:");
    String jarFilename = parseString[1].replaceAll("%20", " ");
    
    return jarFilename;
  }
}
