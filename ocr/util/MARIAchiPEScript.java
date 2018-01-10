package ocr.util;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import ocr.gui.OCRInterface;
import ocr.manager.ExternalProcessManager;

public class MARIAchiPEScript
{
  java.awt.Point dialogLocation = new java.awt.Point(300, 300);
  OCRInterface ocrIF = OCRInterface.this_interface;
  
  private String programname;
  
  private String gtxml;
  private String mariaxml;
  private File cwd;
  private ExternalProcessManager externalProcessManager;
  private MARIADialog mariaDialog;
  private String cmdstringSent;
  
  public MARIAchiPEScript(String gtxml, String mariaxml, File cwd)
  {
    this.gtxml = gtxml;
    if (mariaxml == "") {
      this.mariaxml = ("out-" + gtxml);
    } else
      this.mariaxml = mariaxml;
    this.cwd = cwd;
    Properties mariaProps = new Properties();
    try
    {
      FileInputStream fin = new FileInputStream("config/maria.properties");
      mariaProps.load(fin);
      fin.close();
    }
    catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    programname = mariaProps.getProperty("mariachipe");
  }
  
  public void go()
  {
    mariaDialog = new MARIADialog();
    mariaDialog.showDialog();
  }
  
  class MARIADialog extends JDialog
  {
    private JScrollPane cliScrollPane;
    private JTextArea cliTextArea;
    private JLabel labelCmdString;
    private JLabel labelGTXML;
    private JLabel labelResultsXML;
    private JLabel labelOtherArgs;
    private JLabel labelProgName;
    private JLabel labelMARIAXML;
    private JLabel labelResultsTxt;
    private JButton runButton;
    private JTextField txtGTXML;
    private JTextField txtResultsXML;
    private JTextField txtOtherArgs;
    private JTextField txtProgName;
    private JTextField txtMARIAXML;
    private JTextField txtResultsTxt;
    private JButton updateButton;
    
    private void initComponents()
    {
      labelProgName = new JLabel();
      labelGTXML = new JLabel();
      labelMARIAXML = new JLabel();
      txtProgName = new JTextField();
      txtGTXML = new JTextField();
      txtMARIAXML = new JTextField();
      labelResultsXML = new JLabel();
      txtResultsXML = new JTextField();
      labelResultsTxt = new JLabel();
      txtResultsTxt = new JTextField();
      labelOtherArgs = new JLabel();
      txtOtherArgs = new JTextField();
      updateButton = new JButton();
      labelCmdString = new JLabel();
      cliScrollPane = new JScrollPane();
      cliTextArea = new JTextArea();
      runButton = new JButton();
      
      setTitle("Text Detection Evaluation");
      setModal(true);
      setResizable(false);
      labelProgName.setText("Program Name*");
      
      labelGTXML.setText("Ground-Truth XML File*");
      
      labelMARIAXML.setText("MARIA-generated XML File*");
      
      labelResultsXML.setText("Output XML File*");
      
      labelResultsTxt.setText("Results Text File*");
      
      labelOtherArgs.setText("Other Arguments");
      
      updateButton.setText("Update");
      updateButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          MARIAchiPEScript.MARIADialog.this.updateButtonActionPerformed(evt);
        }
        
      });
      labelCmdString.setText("Command Line String");
      
      cliTextArea.setColumns(20);
      cliTextArea.setRows(2);
      cliTextArea.setEditable(false);
      cliScrollPane.setViewportView(cliTextArea);
      






      runButton.setText("Run");
      runButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          MARIAchiPEScript.MARIADialog.this.runButtonActionPerformed(evt);
        }
        
      });
      GroupLayout jDialog1Layout = new GroupLayout(getContentPane());
      getContentPane().setLayout(jDialog1Layout);
      jDialog1Layout.setHorizontalGroup(
        jDialog1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(jDialog1Layout.createSequentialGroup()
        .addGroup(jDialog1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(jDialog1Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jDialog1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
        .addGroup(GroupLayout.Alignment.LEADING, jDialog1Layout.createSequentialGroup()
        .addGroup(jDialog1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        .addComponent(labelProgName)
        .addComponent(labelGTXML)
        .addComponent(labelMARIAXML)
        .addComponent(labelResultsXML)
        .addComponent(labelResultsTxt)
        .addComponent(labelOtherArgs))
        .addGap(18, 18, 18)
        .addGroup(jDialog1Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
        .addComponent(txtOtherArgs)
        .addComponent(txtResultsTxt)
        .addComponent(txtResultsXML)
        .addComponent(txtMARIAXML)
        .addComponent(txtGTXML)
        .addComponent(txtProgName, -1, 203, 32767)
        .addGroup(jDialog1Layout.createSequentialGroup()
        .addGap(10, 10, 10)
        .addComponent(updateButton))))
        .addComponent(labelCmdString, GroupLayout.Alignment.LEADING)
        .addComponent(cliScrollPane, GroupLayout.Alignment.LEADING, -1, 307, 32767)))
        .addGroup(jDialog1Layout.createSequentialGroup()
        .addGap(136, 136, 136)
        .addComponent(runButton)))
        .addContainerGap()));
      
      jDialog1Layout.setVerticalGroup(
        jDialog1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(jDialog1Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jDialog1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
        .addComponent(labelProgName)
        .addComponent(txtProgName, -2, -1, -2))
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jDialog1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
        .addComponent(labelGTXML)
        .addComponent(txtGTXML, -2, -1, -2))
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jDialog1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
        .addComponent(labelMARIAXML)
        .addComponent(txtMARIAXML, -2, -1, -2))
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jDialog1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
        .addComponent(labelResultsXML)
        .addComponent(txtResultsXML, -2, -1, -2))
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jDialog1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
        .addComponent(labelResultsTxt)
        .addComponent(txtResultsTxt, -2, -1, -2))
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jDialog1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
        .addComponent(labelOtherArgs)
        .addComponent(txtOtherArgs, -2, -1, -2))
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(updateButton)
        .addGap(14, 14, 14)
        .addComponent(labelCmdString)
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(cliScrollPane, -2, 55, -2)
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(runButton)
        .addContainerGap(-1, 32767)));
      


      pack();
    }
    
    private void updateButtonActionPerformed(ActionEvent evt)
    {
      String clistring = new String();
      clistring = txtProgName.getText().trim();
      


      String tmpString = txtGTXML.getText().trim();
      if (tmpString.length() != 0)
      {

        clistring = clistring + " --gtxml=" + tmpString;
      }
      
      tmpString = txtMARIAXML.getText().trim();
      if (tmpString.length() != 0) {
        clistring = clistring + " --mariaxml=" + tmpString;
      }
      tmpString = txtResultsXML.getText().trim();
      if (tmpString.length() != 0) {
        clistring = clistring + " --xmlout=" + tmpString;
      }
      tmpString = txtResultsTxt.getText().trim();
      if (tmpString.length() != 0) {
        clistring = clistring + " --results=" + tmpString;
      }
      tmpString = txtOtherArgs.getText().trim();
      if (tmpString.length() != 0) {
        clistring = clistring + " " + tmpString;
      }
      cliTextArea.setText(clistring);
    }
    

    private void closeDialog()
    {
      this_interfacechildWindow = null;
      setVisible(false);
    }
    

    private void runButtonActionPerformed(ActionEvent evt)
    {
      cmdstringSent = cliTextArea.getText().trim();
      







      System.out.println(cmdstringSent);
      closeDialog();
      externalProcessManager = new ExternalProcessManager();
      externalProcessManager.executeProgram(cmdstringSent, cwd, "Text Detection Evaluation");
    }
    

    private void showDialog()
    {
      txtProgName.setText(programname);
      txtGTXML.setText(gtxml);
      txtMARIAXML.setText(mariaxml);
      txtResultsXML.setText("res-" + gtxml);
      txtResultsTxt.setText("res-" + gtxml.replace(".xml", ".txt"));
      String clistr = new String();
      clistr = programname + " " + " --gtxml=" + gtxml + " --mariaxml=" + mariaxml + " --xmlout=res-" + gtxml + " --results=res-" + gtxml.replace(".xml", ".txt");
      cliTextArea.setText(clistr);
      this_interfacechildWindow = this;
      setVisible(true);
    }
    
    public MARIADialog()
    {
      super("Text Detection Evaluation", true);
      initComponents();
      setLocation(dialogLocation);
    }
  }
}
