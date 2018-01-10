package ocr.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import ocr.manager.GlobalHotkeyManager;
import ocr.tag.DirectoryChooser;

public class DirLoader extends JDialog
{
  private JPanel dirPanel;
  private JTextField imageField;
  private JTextField dataField;
  private JCheckBox sameBox;
  private JButton browseButton2;
  private JButton okButton;
  
  public DirLoader()
  {
    super(OCRInterface.this_interface, "Choose Image and Data Directory to Load", true);
    
    dirPanel = new JPanel();
    dirPanel.setLayout(new BoxLayout(dirPanel, 1));
    JPanel subPanel1 = new JPanel();
    subPanel1.setLayout(new BoxLayout(subPanel1, 0));
    JPanel subPanel2 = new JPanel();
    subPanel2.setLayout(new BoxLayout(subPanel2, 0));
    JPanel subPanel3 = new JPanel();
    subPanel3.setLayout(new BoxLayout(subPanel3, 1));
    JPanel subPanel4 = new JPanel();
    
    imageField = new JTextField(OCRInterface.getCurrentImageDir() + OCRInterface.getImageDirName());
    imageField.setEditable(false);
    dataField = new JTextField(OCRInterface.getCurrentXmlDir() + OCRInterface.getXmlDirName());
    dataField.setEditable(false);
    
    addWindowListener(new java.awt.event.WindowAdapter()
    {

      public void windowClosing(WindowEvent e) {}

    });
    okButton = new JButton("OK");
    okButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        File img_file = new File(imageField.getText());
        File data_file = new File(dataField.getText());
        OCRInterface.setCurrentImage_dir(img_file.getParent() + File.separatorChar);
        OCRInterface.setImageDirName(img_file.getName());
        
        OCRInterface.setCurrentXml_dir(data_file.getParent() + File.separatorChar);
        OCRInterface.setXmlDirName(data_file.getName());
        
        OCRInterface.this_interface.LoadDictionary(1);
        OCRInterface.this_interface.getMultiPagePanel().setVisible(false);
        OCRInterface.this_interface.loadConfigFile(null);
        
        OCRInterface.this_interface.getLoader().setVisible(false);
        OCRInterface.dialog_open = false;
        this_interfaceocrTable.setColumnWidths();
        this_interfaceocrTable.selFileName = null;
        GlobalHotkeyManager hotkeyManager = GlobalHotkeyManager.getInstance();
        hotkeyManager.setEnabled(true);
      }
      
    });
    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(new ActionListener()
    {

      public void actionPerformed(ActionEvent e) {}

    });
    JButton browseButton1 = new JButton("Browse");
    browseButton1.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        DirectoryChooser dirchoose = null;
        try {
          String temp = imageField.getText();
          if (temp == "") {
            dirchoose = new DirectoryChooser(new File(OCRInterface.getCurrentImageDir()));
          } else
            dirchoose = new DirectoryChooser(new File(temp).getParentFile());
          OCRInterface.setJChoose(dirchoose);
        } catch (NullPointerException npe) {
          dirchoose = new DirectoryChooser(new File(OCRInterface.getCurrentImageDir()));
        }
        OCRInterface.this_interface.getLoader().setAlwaysOnTop(false);
        int selection = dirchoose.showDialog(OCRInterface.this_interface, "Image File/Directory");
        OCRInterface.this_interface.getLoader().setAlwaysOnTop(true);
        OCRInterface.this_interface.getLoader().requestFocusInWindow();
        File img_file = dirchoose.getSelectedFile();
        
        if (selection != 1) {
          System.out.println(img_file);
          imageField.setText(img_file.getAbsolutePath());
          
          if (sameBox.isSelected()) {
            if (img_file.isDirectory()) {
              dataField.setText(img_file.getAbsolutePath());
            } else {
              dataField.setText(img_file.getParentFile().getAbsolutePath());
            }
            okButton.setEnabled(true);
          } else if (new File(dataField.getText()).exists()) {
            okButton.setEnabled(true);
          }
          
          DirLoader.this.setToolTips();
        }
        
      }
    });
    browseButton2 = new JButton("Browse");
    browseButton2.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        DirectoryChooser dirchoose = null;
        try {
          String temp = imageField.getText();
          if (temp == "") {
            dirchoose = new DirectoryChooser(new File(OCRInterface.getCurrentImageDir()));
          } else
            dirchoose = new DirectoryChooser(new File(temp).getParentFile());
          OCRInterface.setJChoose(dirchoose);
        } catch (NullPointerException npe) {
          dirchoose = new DirectoryChooser(new File(OCRInterface.getCurrentImageDir()));
        }
        OCRInterface.this_interface.getLoader().setAlwaysOnTop(false);
        int selection = dirchoose.showDialog(OCRInterface.this_interface, "Data Directory");
        OCRInterface.this_interface.getLoader().setAlwaysOnTop(true);
        File img_file = dirchoose.getSelectedFile();
        
        if (selection != 1) {
          System.out.println(img_file);
          if (!img_file.isDirectory()) {
            img_file = img_file.getParentFile();
          }
          dataField.setText(img_file.getAbsolutePath());
          
          if (new File(imageField.getText()).exists()) {
            okButton.setEnabled(true);
          }
          DirLoader.this.setToolTips();
        }
        
      }
    });
    sameBox = new JCheckBox("Use Image Location", false);
    sameBox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (sameBox.isSelected()) {
          dataField.setEnabled(false);
          browseButton2.setEnabled(false);
          
          if (new File(imageField.getText()).exists()) {
            okButton.setEnabled(true);
          }
          
          File imgFile = new File(imageField.getText());
          if (imgFile.isDirectory()) {
            dataField.setText(imgFile.getPath());
          } else {
            dataField.setText(imgFile.getParent());
          }
          DirLoader.this.setToolTips();
        } else {
          dataField.setEnabled(true);
          browseButton2.setEnabled(true);
          
          DirLoader.this.setToolTips();
        }
      }
    });
    


    if (!new File(OCRInterface.getCurrentImageDir() + OCRInterface.getImageDirName()).exists()) {
      OCRInterface.setCurrentImage_dir("");
      OCRInterface.setImageDirName("");
      imageField.setText("");
      okButton.setEnabled(false);
    }
    
    if (!new File(OCRInterface.getCurrentXmlDir() + OCRInterface.getXmlDirName()).exists()) {
      OCRInterface.setCurrentXml_dir(imageField.getText());
      OCRInterface.setXmlDirName(imageField.getText());
      dataField.setText(imageField.getText());
    }
    
    subPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Image File/Directory:"));
    subPanel1.add(imageField);
    subPanel1.add(browseButton1);
    subPanel1.setMaximumSize(new Dimension(580, 50));
    subPanel1.setAlignmentX(0.0F);
    
    subPanel2.add(dataField);
    subPanel2.add(browseButton2);
    subPanel2.setMaximumSize(new Dimension(580, 50));
    subPanel2.setAlignmentX(0.0F);
    
    subPanel3.add(subPanel2);
    subPanel3.add(sameBox);
    subPanel3.setMaximumSize(new Dimension(580, 75));
    subPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Data Directory:"));
    subPanel3.setAlignmentX(0.0F);
    
    subPanel4.add(okButton);
    subPanel4.add(cancelButton);
    subPanel4.setAlignmentX(0.0F);
    
    dirPanel.add(subPanel1);
    dirPanel.add(subPanel3);
    dirPanel.add(subPanel4);
    
    setToolTips();
    





    if (dataField.getText().compareTo(imageField.getText()) == 0) {
      dataField.setEnabled(false);
      browseButton2.setEnabled(false);
      sameBox.setSelected(true);
    }
    
    setAlwaysOnTop(true);
    
    setContentPane(dirPanel);
    setSize(600, 195);
    setResizable(false);
    setLocation(this_interfacegetLocationx + 50, this_interfacegetLocationy + 50);
    
    setVisible(false);
  }
  
  public JTextField getImageField() {
    return imageField;
  }
  
  public JTextField getDataField() {
    return dataField;
  }
  
  public static void cancel() {
    if (OCRInterface.this_interface.getLoader().isVisible()) {
      OCRInterface.this_interface.getLoader().getImageField().setText(OCRInterface.getCurrentImageDir() + OCRInterface.getImageDirName());
      OCRInterface.this_interface.getLoader().getDataField().setText(OCRInterface.getCurrentXmlDir() + OCRInterface.getXmlDirName());
      OCRInterface.this_interface.getLoader().setVisible(false);
      OCRInterface.dialog_open = false;
    }
  }
  
  private void setToolTips() {
    dataField.setToolTipText(dataField.getText());
    imageField.setToolTipText(imageField.getText());
  }
}
