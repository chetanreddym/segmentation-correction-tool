package ocr.util;

import java.awt.Point;
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
import ocr.gui.LoadDataFile;
import ocr.gui.OCRInterface;
import ocr.manager.ExternalProcessManager;

public class MARIAScript
{
  Point dialogLocation = new Point(300, 300);
  OCRInterface ocrIF = OCRInterface.this_interface;
  
  private String programname;
  
  private String imagein;
  private String xmlin;
  private File cwd;
  private ExternalProcessManager externalProcessManager;
  private MARIADialog mariaDialog;
  private String cmdstringSent;
  
  public MARIAScript(LoadDataFile currDoc)
  {
    imagein = currDoc.getFileName().replace("xml", "tif");
    xmlin = currDoc.getFileName();
    cwd = new File(currDoc.getFilePathOnly());
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
    programname = mariaProps.getProperty("mariachi");
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
    private JLabel labelImageIn;
    private JLabel labelImageOut;
    private JLabel labelOtherArgs;
    private JLabel labelProgName;
    private JLabel labelXMLIn;
    private JLabel labelXMLOut;
    private JButton runButton;
    private JTextField txtImageIn;
    private JTextField txtImageOut;
    private JTextField txtOtherArgs;
    private JTextField txtProgName;
    private JTextField txtXMLIn;
    private JTextField txtXMLOut;
    private JButton updateButton;
    
    private void initComponents()
    {
      labelProgName = new JLabel();
      labelImageIn = new JLabel();
      labelXMLIn = new JLabel();
      txtProgName = new JTextField();
      txtImageIn = new JTextField();
      txtXMLIn = new JTextField();
      labelImageOut = new JLabel();
      txtImageOut = new JTextField();
      labelXMLOut = new JLabel();
      txtXMLOut = new JTextField();
      labelOtherArgs = new JLabel();
      txtOtherArgs = new JTextField();
      updateButton = new JButton();
      labelCmdString = new JLabel();
      cliScrollPane = new JScrollPane();
      cliTextArea = new JTextArea();
      runButton = new JButton();
      
      setTitle("Text Extraction");
      setModal(true);
      setResizable(false);
      labelProgName.setText("Program Name*");
      
      labelImageIn.setText("Input Image File*");
      
      labelXMLIn.setText("Input XML File");
      
      labelImageOut.setText("Output Image File");
      
      labelXMLOut.setText("Output XML File");
      
      labelOtherArgs.setText("Other Arguments");
      
      updateButton.setText("Update");
      updateButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          MARIAScript.MARIADialog.this.updateButtonActionPerformed(evt);
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
          MARIAScript.MARIADialog.this.runButtonActionPerformed(evt);
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
        .addComponent(labelImageIn)
        .addComponent(labelXMLIn)
        .addComponent(labelImageOut)
        .addComponent(labelXMLOut)
        .addComponent(labelOtherArgs))
        .addGap(18, 18, 18)
        .addGroup(jDialog1Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
        .addComponent(txtOtherArgs)
        .addComponent(txtXMLOut)
        .addComponent(txtImageOut)
        .addComponent(txtXMLIn)
        .addComponent(txtImageIn)
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
        .addComponent(labelImageIn)
        .addComponent(txtImageIn, -2, -1, -2))
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jDialog1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
        .addComponent(labelXMLIn)
        .addComponent(txtXMLIn, -2, -1, -2))
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jDialog1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
        .addComponent(labelImageOut)
        .addComponent(txtImageOut, -2, -1, -2))
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jDialog1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
        .addComponent(labelXMLOut)
        .addComponent(txtXMLOut, -2, -1, -2))
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
      


      String tmpString = txtImageIn.getText().trim();
      if (tmpString.length() != 0)
      {

        clistring = clistring + " --imagein=" + tmpString;
      }
      
      tmpString = txtXMLIn.getText().trim();
      if (tmpString.length() != 0) {
        clistring = clistring + " --xmlin=" + tmpString;
      }
      tmpString = txtImageOut.getText().trim();
      if (tmpString.length() != 0) {
        clistring = clistring + " --imageout=" + tmpString;
      }
      tmpString = txtXMLOut.getText().trim();
      if (tmpString.length() != 0) {
        clistring = clistring + " --xmlout=" + tmpString;
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
      closeDialog();
      externalProcessManager = new ExternalProcessManager();
      externalProcessManager.executeProgram(cmdstringSent, cwd, "Text Extraction");
    }
    

    private void showDialog()
    {
      txtProgName.setText(programname);
      txtImageIn.setText(imagein);
      txtXMLIn.setText(xmlin);
      txtImageOut.setText("out-" + imagein);
      txtXMLOut.setText("out-" + xmlin);
      String clistr = new String();
      clistr = programname + " " + " --imagein=" + imagein + " --xmlin=" + xmlin + " --imageout=out-" + imagein + " --xmlout=out-" + xmlin;
      cliTextArea.setText(clistr);
      this_interfacechildWindow = this;
      setVisible(true);
    }
    
    public MARIADialog()
    {
      super("Text Extraction", true);
      initComponents();
      setLocation(dialogLocation);
    }
  }
}
