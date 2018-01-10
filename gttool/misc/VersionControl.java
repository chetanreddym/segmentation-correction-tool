package gttool.misc;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Map.Entry;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;









public class VersionControl
  extends JFrame
{
  private int height = 200; private int width = 460;
  private String currentVerStr;
  
  public VersionControl() { create();
    setVisible(true); }
  
  private JButton keepBtn;
  
  public void create() { saveBtn = new JButton("Save");
    cancelBtn = new JButton("Cancel");
    infoBtn = new JButton("Info");
    keepBtn = new JButton("Keep current version");
    
    cancelBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        VersionControl.this.cancelBtn_action();
      }
      

    });
    saveBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        VersionControl.this.saveBtn_action();
      }
      

    });
    infoBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        VersionControl.this.infoBtn_action();
      }
      

    });
    keepBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        VersionControl.this.keepBtn_action();
      }
      

    });
    contentPane = ((JPanel)getContentPane());
    JLabel oldVersionLabel = new JLabel("Current GEDI Version:");
    currentVersion = new JTextField();
    JLabel newVersionLabel = new JLabel("New GEDI Version:");
    newVersion = new JTextField();
    
    oldVersionLabel.setHorizontalAlignment(2);
    
    oldVersionLabel.setHorizontalAlignment(2);
    
    currentVersion.setEnabled(false);
    
    contentPane.setLayout(null);
    contentPane.setBorder(BorderFactory.createEtchedBorder());
    contentPane.setFont(new Font("Serif", 1, 24));
    
    currentVersion.setFont(new Font("Serif", 1, 18));
    newVersion.setFont(new Font("Serif", 1, 18));
    currentVersion.setHorizontalAlignment(0);
    newVersion.setHorizontalAlignment(0);
    
    currentVerStr = getCurrentVersionNumber();
    currentVersion.setText(currentVerStr);
    
    newVersion.setText(getNewVersionNumber(currentVerStr));
    
    addComponent(contentPane, oldVersionLabel, 100, 20, 150, 30);
    addComponent(contentPane, currentVersion, 250, 25, 70, 20);
    
    addComponent(contentPane, newVersionLabel, 100, 60, 110, 30);
    addComponent(contentPane, newVersion, 250, 65, 70, 20);
    
    addComponent(contentPane, keepBtn, 10, 120, 160, 25);
    addComponent(contentPane, saveBtn, 180, 120, 80, 25);
    addComponent(contentPane, cancelBtn, 270, 120, 80, 25);
    addComponent(contentPane, infoBtn, 360, 120, 80, 25);
    
    setTitle("GEDI Version Control");
    
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    setLocation(width / 2 - 200, 
      height / 2 - 200);
    
    paneDimension = new Dimension(width, height);
    
    setSize(paneDimension);
    setDefaultCloseOperation(3);
    setResizable(false); }
  
  private JButton infoBtn;
  private JButton cancelBtn;
  
  private void addComponent(Container container, Component comp, int x, int y, int width, int height) { comp.setBounds(x, y, width, height);
    container.add(comp); }
  
  private JButton saveBtn;
  
  public static void main(String[] args) { new VersionControl(); }
  
  private JPanel contentPane;
  private JTextField newVersion;
  private JTextField currentVersion;
  private Dimension paneDimension;
  private String getCurrentVersionNumber() {
    try { JarFile jarfile = new JarFile("GEDI.jar");
      
      Manifest manifest = jarfile.getManifest();
      

      Attributes atts = manifest.getMainAttributes();
      
      for (Map.Entry e : atts.entrySet()) {
        String key = e.getKey().toString();
        if (key.equals("Implementation-Version"))
          return e.getValue().toString();
      }
    } catch (IOException e) {
      return "";
    }
    return "";
  }
  
  private String getNewVersionNumber(String current) {
    String[] str = current.split("\\.");
    int part_0 = Integer.valueOf(str[0]).intValue();
    int part_1 = Integer.valueOf(str[1]).intValue();
    int part_2 = Integer.valueOf(str[2]).intValue();
    String newVersionStr = "";
    
    if ((part_2 < 10) && (part_2 != 99)) {
      newVersionStr = 
      
        Integer.toString(part_0) + "." + Integer.toString(part_1) + ".0" + Integer.toString(part_2 + 1);
    } else if ((part_2 >= 10) && (part_2 != 99)) {
      newVersionStr = 
      
        Integer.toString(part_0) + "." + Integer.toString(part_1) + "." + Integer.toString(part_2 + 1);
    } else if ((part_1 < 9) && (part_2 == 99)) {
      newVersionStr = 
      
        Integer.toString(part_0) + "." + Integer.toString(part_1 + 1) + ".0" + Integer.toString(0);
    } else if ((part_1 == 9) && (part_2 < 99)) {
      newVersionStr = 
      
        Integer.toString(part_0 + 1) + "." + Integer.toString(0) + ".0" + Integer.toString(0);
    } else if ((part_1 == 9) && (part_2 == 99)) {
      newVersionStr = 
      
        Integer.toString(part_0 + 1) + "." + Integer.toString(0) + ".0" + Integer.toString(0);
    }
    return newVersionStr;
  }
  
  private void cancelBtn_action() {
    dispose();
  }
  
  private void saveBtn_action()
  {
    System.out.println("save");
    




    try
    {
      StringBuffer sbuf = new StringBuffer();
      sbuf.append("Manifest-Version: 1.0\n");
      sbuf.append("Main-Class: gttool.misc.Login\n");
      sbuf.append("Class-Path: lib/jai_codec.jar lib/jai_core.jar lib/mail.jar lib/Utilities.jar\n");
      sbuf.append("Implementation-Version: 2.0.01\n");
      

      InputStream is = new ByteArrayInputStream(sbuf.toString().getBytes("UTF-8"));
      

      Manifest manifest = new Manifest(is);
      System.out.println("manifest: " + manifest);
    }
    catch (IOException localIOException) {}
  }
  
  private void infoBtn_action() {
    String msg = "<HTML> GEDI version number is stored in the <FONT COLOR=Blue> MANIFEST.MF </FONT> in the <FONT COLOR=Blue> Implementation-Version </FONT> variable. <BR><BR>Before to create JAR file, Implementation-Version variable is updated, and <BR>updated Manifest.mf is added to the JAR. <BR><BR>Enter necessary version number in the <FONT COLOR=Blue>New GEDI Version</FONT> Text Field and click on Save button. <BR><BR>This version number will be saved in the Manifest.mf. </HTML> ";
    









    JOptionPane.showMessageDialog(null, msg, "How to handle GEDI version", 1);
  }
  
  private void keepBtn_action() {
    newVersion.setText(currentVerStr);
  }
}
