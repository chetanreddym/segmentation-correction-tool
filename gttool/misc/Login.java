package gttool.misc;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.TreeMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

public class Login extends JFrame
{
  private JTextField nameTextField;
  public JTextField configDirTextField;
  private JTextField propsDirTextField;
  private JButton runBtn;
  private JButton newUserBtn;
  private JButton cancelBtn;
  private JButton deleteUserBtn;
  private JButton addUserBtn;
  private JButton cancelBtn2;
  private JButton browseConfigBtn;
  private JButton browsePropsBtn;
  private JCheckBox loadConfigCheckBox;
  private JCheckBox loadPropsCheckBox;
  private int buttonWidth = 100;
  private int buttonHeight = 30;
  
  private JPanel contentPane;
  
  private JComboBox userList;
  
  private static String[] loadArgs;
  
  public static String userName;
  
  private String mostRecentUser;
  
  private JLabel info;
  
  private JLabel newUserLabel;
  
  public static final String mostRecentUserKey = "mostRecentUser";
  
  public static final String usersrKey = "users";
  
  public static final String configPathKey = "configPath";
  public static final String enableLoadConfigKey = "enableLoadConfig";
  public static final String propsPathKey = "propertiesPath";
  public static final String enableLoadPropsKey = "enableLoadProperties";
  public static final String enableProfileLoadOnLoginKey = "enableProfileLoadOnLogin";
  public static Date loggedTime;
  public static TreeMap<String, String> userListTags;
  private Dimension paneDimension;
  private Dimension extendedDimension;
  private int width = 570; private int height = 260;
  

  private LinkedList<String> userStrings;
  

  private JTextField newUserName;
  

  private LoadUserProps userProp = null;
  
  public static boolean loadConfig = true;
  public static boolean loadProps = true;
  
  boolean enableProfileLoad;
  private OptionParser commandLineOptions = null;
  
  public Login()
  {
    if (loadArgs.length >= 1) {
      commandLineOptions = new OptionParser(new java.util.ArrayList(Arrays.asList(loadArgs)), this);
    }
    
    if ((commandLineOptions != null) && 
      (commandLineOptions.isNetworkListenerRequired()) && 
      (commandLineOptions.isNetworkListenerRunning())) {
      System.exit(0);
    }
    userProp = LoadUserProps.getInstance();
    
    super.setIconImage(localOrInJar("images/GEDI_icon.jpg", getClass().getClassLoader()).getImage());
    userListTags = new TreeMap();
    userStrings = new LinkedList();
    
    userStrings.add("");
    

    getUsersFromPropertiesFile();
    

    sortUserList();
    
    nameTextField = new JTextField();
    runBtn = new JButton("Run GEDI");
    newUserBtn = new JButton("New User");
    cancelBtn = new JButton("Cancel");
    cancelBtn2 = new JButton("Cancel");
    deleteUserBtn = new JButton("Delete User");
    addUserBtn = new JButton("Add User");
    contentPane = ((JPanel)getContentPane());
    userList = new JComboBox(userStrings.toArray());
    newUserName = new JTextField();
    browseConfigBtn = new JButton("Browse");
    browsePropsBtn = new JButton("Browse");
    configDirTextField = new JTextField();
    propsDirTextField = new JTextField();
    info = new ocr.gui.leftPanel.InfoLabel(false);
    loadConfigCheckBox = new JCheckBox("Load GEDI Config: ");
    loadPropsCheckBox = new JCheckBox("Load GEDI Properties: ");
    if (!userProp.containsKey("enableProfileLoadOnLogin")) {
      enableProfileLoad = true;
    } else {
      enableProfileLoad = Boolean.parseBoolean(userProp.getProperty("enableProfileLoadOnLogin"));
    }
    if (!enableProfileLoad)
      height = 200;
    paneDimension = new Dimension(width, height);
    extendedDimension = new Dimension(width, height + 100);
    create();
    setVisible(true);
    addESCClose();
  }
  
  public static ImageIcon localOrInJar(String filename, ClassLoader loader) {
    ImageIcon temp = new ImageIcon(filename);
    if (!new File(filename).exists())
    {
      temp = new ImageIcon(loader.getResource(filename));
    }
    return temp;
  }
  
  private void create()
  {
    JLabel nameLabel = new JLabel();
    
    nameLabel.setHorizontalAlignment(2);
    nameLabel.setText("Select Username:");
    
    loadConfigCheckBox.setSelected(Boolean.parseBoolean(userProp
      .getProperty("enableLoadConfig")));
    loadConfigCheckBox.setHorizontalAlignment(2);
    
    loadPropsCheckBox.setSelected(Boolean.parseBoolean(userProp
      .getProperty("enableLoadProperties")));
    loadPropsCheckBox.setHorizontalAlignment(2);
    
    setLoadConfigEnabled(loadConfigCheckBox.isSelected());
    setLoadPropsEnabled(loadPropsCheckBox.isSelected());
    
    nameLabel.setHorizontalAlignment(2);
    nameLabel.setText("Select Username:");
    
    nameTextField.setForeground(new java.awt.Color(0, 0, 255));
    nameTextField.setToolTipText("Select your username");
    
    userList.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Login.this.userList_actionPerformed(e);

      }
      

    });
    runBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Login.this.runBtn_actionPerformed(e);
      }
      
    });
    newUserBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Login.this.newUserBtn_actionPerformed(e);
      }
      
    });
    cancelBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Login.this.cancelBtn_actionPerformed(e);
      }
      
    });
    cancelBtn2.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Login.this.cancelBtn2_actionPerformed(e);
      }
      
    });
    addUserBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Login.this.addUserBtn_actionPerformed(e);
      }
      
    });
    deleteUserBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Login.this.deleteUserBtn_actionPerformed(e);
      }
      
    });
    browseConfigBtn.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent arg0) {
        JFileChooser chooser = null;
        if (userProp.getProperty("configPath") == null) {
          chooser = new JFileChooser();
        } else
          chooser = new JFileChooser(new File(userProp.getProperty("configPath")));
        chooser.setDialogTitle("Choose GEDI Config");
        chooser.setFileSelectionMode(0);
        chooser.addChoosableFileFilter(XMLFileFilter);
        
        int returnVal = chooser.showOpenDialog(getRootPane());
        if (returnVal == 0) {
          try {
            configDirTextField.setText(chooser.getSelectedFile().getCanonicalPath());
            configDirTextField.setCaretPosition(0);
            configDirTextField.setToolTipText(chooser.getSelectedFile().getCanonicalPath());
          } catch (IOException e) {
            System.out.println("Login. BrowseConfig button. Cannot get canonical path.");
          }
          
        }
        
      }
      
    });
    browsePropsBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        JFileChooser chooser = null;
        if (userProp.getProperty("propertiesPath") == null) {
          chooser = new JFileChooser();
        } else
          chooser = new JFileChooser(new File(userProp.getProperty("propertiesPath")));
        chooser.setDialogTitle("Choose GEDI Properties");
        chooser.setFileSelectionMode(0);
        chooser.addChoosableFileFilter(PropertiesFileFilter);
        
        int returnVal = chooser.showOpenDialog(getRootPane());
        if (returnVal == 0) {
          try {
            propsDirTextField.setText(chooser.getSelectedFile().getCanonicalPath());
            propsDirTextField.setCaretPosition(0);
            propsDirTextField.setToolTipText(chooser.getSelectedFile().getCanonicalPath());
          } catch (IOException e) {
            System.out.println("Login. BrowseProps button. Cannot get canonical path.");
          }
          
        }
        
      }
      
    });
    loadConfigCheckBox.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent ie) {
        if (ie.getStateChange() == 2)
          setLoadConfigEnabled(false);
        if (ie.getStateChange() == 1) {
          setLoadConfigEnabled(true);
        }
        Login.this.saveUserListinPropertiesFile();
      }
      

    });
    loadPropsCheckBox.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent ie) {
        if (ie.getStateChange() == 2)
          Login.this.setLoadPropsEnabled(false);
        if (ie.getStateChange() == 1) {
          Login.this.setLoadPropsEnabled(true);
        }
        Login.this.saveUserListinPropertiesFile();
      }
      

    });
    int startFromX = 60;
    int startFromY = 70;
    
    contentPane.setLayout(null);
    contentPane.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    contentPane.setFont(new java.awt.Font("Serif", 1, 18));
    
    addComponent(contentPane, nameLabel, 10, 25, 110, 20);
    addComponent(contentPane, userList, 150, 25, 300, 20);
    addComponent(contentPane, runBtn, startFromX, startFromY, buttonWidth, buttonHeight);
    addComponent(contentPane, newUserBtn, startFromX + buttonWidth + 10, startFromY, buttonWidth, buttonHeight);
    addComponent(contentPane, deleteUserBtn, startFromX + buttonWidth * 2 + 20, startFromY, buttonWidth, buttonHeight);
    addComponent(contentPane, cancelBtn, startFromX + buttonWidth * 3 + 30, startFromY, buttonWidth, buttonHeight);
    
    String versionStr = "version " + gttool.io.XmlConstant.VALUE_GEDI_VERSION + "  (" + gttool.io.XmlConstant.VALUE_GEDI_DATE + ")";
    JLabel version = new JLabel("<html><font color=blue>&nbsp;&nbsp;" + versionStr + "</font></html>");
    addComponent(contentPane, version, 400, -10, 250, 40);
    

    if (enableProfileLoad) {
      addLoadProfileGroup();
    }
    deleteUserBtn.setEnabled(true);
    userList.setEnabled(true);
    runBtn.setEnabled(false);
    deleteUserBtn.setEnabled(false);
    
    mostRecentUser = userProp.getProperty("mostRecentUser");
    userList.setSelectedItem(mostRecentUser);
    
    String configPath = userProp.getProperty("configPath");
    

    if ((commandLineOptions != null) && 
      (commandLineOptions.getConfigFilePath() != null)) {
      configPath = commandLineOptions.getConfigFilePath();
      System.out.println("configPath: " + configPath);
      configDirTextField.setText(configPath);
      configDirTextField.setToolTipText(configPath);
      setLoadConfigEnabled(true);
    }
    else if ((configPath != null) && (new File(configPath).exists())) {
      configDirTextField.setText(configPath);
      configDirTextField.setToolTipText(configPath);
    }
    else {
      setLoadConfigEnabled(false);
    }
    
    String propsPath = userProp.getProperty("propertiesPath");
    
    if ((commandLineOptions != null) && 
      (commandLineOptions.getPropsFilePath() != null)) {
      propsPath = commandLineOptions.getPropsFilePath();
      propsDirTextField.setText(propsPath);
      propsDirTextField.setToolTipText(propsPath);
      setLoadPropsEnabled(true);
    }
    else if ((propsPath != null) && (new File(propsPath).exists())) {
      propsDirTextField.setText(propsPath);
      propsDirTextField.setToolTipText(propsPath);
    }
    else {
      setLoadPropsEnabled(false);
    }
    setTitle("Login To GEDI");
    
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    setLocation(width / 2 - 200, 
      height / 2 - 200);
    
    setSize(paneDimension);
    setDefaultCloseOperation(3);
    setResizable(false);
  }
  






  private void addComponent(Container container, Component comp, int x, int y, int width, int height)
  {
    comp.setBounds(x, y, width, height);
    container.add(comp);
  }
  
  private void removeComponent(Container container, Component comp) {
    container.remove(comp);
  }
  
  private void userList_actionPerformed(ActionEvent e) {
    JComboBox cb = (JComboBox)e.getSource();
    userName = (String)cb.getSelectedItem();
    if ((userName != null) && (!userName.equals(""))) {
      runBtn.setEnabled(true);
      getRootPane().setDefaultButton(runBtn);
      deleteUserBtn.setEnabled(true);
    }
    else {
      runBtn.setEnabled(false);
      deleteUserBtn.setEnabled(false);
    }
  }
  
  private void cancelBtn_actionPerformed(ActionEvent e) {
    saveUserListinPropertiesFile();
    dispose();
  }
  
  private void cancelBtn2_actionPerformed(ActionEvent e) {
    setSize(paneDimension);
    removeNewUserGroup();
    
    if (enableProfileLoad) {
      addLoadProfileGroup();
    }
    userList.setEnabled(true);
    deleteUserBtn.setEnabled(true);
    newUserBtn.setEnabled(true);
    
    userList.setSelectedItem(mostRecentUser);
  }
  
  private void runBtn_actionPerformed(ActionEvent e) {
    loggedTime = new Date();
    saveUserListinPropertiesFile();
    dispose();
    ocr.gui.OCRInterface.runGEDI(commandLineOptions);
  }
  
  private void newUserBtn_actionPerformed(ActionEvent e) {
    removeLoadProfileGroup();
    
    newUserName.setText("");
    
    setSize(extendedDimension);
    
    addNewUserGroup();
    
    userList.setEnabled(false);
    deleteUserBtn.setEnabled(false);
    runBtn.setEnabled(false);
    newUserBtn.setEnabled(false);
    newUserName.setFocusable(true);
    newUserName.requestFocus();
  }
  
  private void addUserBtn_actionPerformed(ActionEvent e)
  {
    String title = "Add New User";
    if (newUserName.getText().equals("")) {
      JOptionPane.showMessageDialog(null, "Please enter username.", title, 2);
      return;
    }
    
    if (newUserName.getText().contains(";")) {
      JOptionPane.showMessageDialog(null, "Username cannot contain \";\" symbol.", title, 2);
      return;
    }
    
    if (userStrings.contains(newUserName.getText().trim())) {
      JOptionPane.showMessageDialog(null, "Such username already exists.", title, 2);
      return;
    }
    
    String msg = "<HTML>You entered <FONT COLOR=Blue>" + 
      newUserName.getText().trim() + 
      "</FONT> username. Save it?</HTML>";
    int choice = JOptionPane.showConfirmDialog(null, msg, title, 0);
    

    if (choice == 0)
    {
      userStrings.add(newUserName.getText().trim());
      

      userList.addItem(newUserName.getText().trim());
      newUserName.setText("");
      cancelBtn2_actionPerformed(e);
      userList.setSelectedIndex(userList.getItemCount() - 1);
    }
    else {
      newUserName.setFocusable(true);
      newUserName.requestFocus();
      return;
    }
  }
  

  private void deleteUserBtn_actionPerformed(ActionEvent e)
  {
    String msg = "<HTML>Do you want to delete <FONT COLOR=Blue>" + 
      userList.getSelectedItem() + 
      "</FONT> username?</HTML>";
    int choise = JOptionPane.showConfirmDialog(null, msg, "Delete User", 0);
    

    if (choise == 0) {
      userList.removeItem(userList.getSelectedItem());
      userList.setSelectedIndex(0);
    }
    else {}
  }
  
  public static TreeMap<String, String> getUserListTags()
  {
    return userListTags;
  }
  
  public static void main(String[] args) {
    loadArgs = args;
    new Login();
  }
  
  private void getUsersFromPropertiesFile() {
    String users = null;
    
    if (userProp.getProperty("users") != null) {
      users = userProp.getProperty("users").trim();
      
      String[] strTemp = users.split(";");
      
      for (int i = 0; i < strTemp.length; i++)
        userStrings.add(strTemp[i].trim());
    }
  }
  
  private void sortUserList() {
    String[] usersArray = new String[userStrings.size()];
    
    for (int i = 0; i < userStrings.size(); i++) {
      usersArray[i] = ((String)userStrings.get(i));
    }
    Arrays.sort(usersArray, String.CASE_INSENSITIVE_ORDER);
    
    userStrings.clear();
    

    for (int i = 0; i < usersArray.length; i++)
      userStrings.add(usersArray[i]);
  }
  
  private void saveUserListinPropertiesFile() {
    int nitems = userList.getItemCount();
    String usersStrOutput = "";
    for (int i = 1; i < nitems; i++) {
      if (!userList.getItemAt(i).equals("")) {
        usersStrOutput = usersStrOutput + userList.getItemAt(i) + "; ";
      }
    }
    userProp.setProperty("users", usersStrOutput);
    if (userName != null)
      userProp.setProperty("mostRecentUser", userName);
    userProp.setProperty("configPath", configDirTextField.getText().trim());
    userProp.setProperty("enableLoadConfig", Boolean.toString(
      loadConfigCheckBox.isSelected()));
    
    userProp.setProperty("propertiesPath", propsDirTextField.getText().trim());
    userProp.setProperty("enableLoadProperties", Boolean.toString(
      loadPropsCheckBox.isSelected()));
    
    userProp.setProperty("enableProfileLoadOnLogin", Boolean.toString(enableProfileLoad));
    
    LoadUserProps.closeUserProps();
  }
  


  public void addESCClose()
  {
    KeyStroke esc = KeyStroke.getKeyStroke('\033');
    javax.swing.Action actionListener = new javax.swing.AbstractAction() {
      public void actionPerformed(ActionEvent actionEvent) {
        dispose();
      }
    };
    InputMap inputMap = getRootPane().getInputMap(2);
    inputMap.put(esc, "ESCAPE");
    getRootPane().getActionMap().put("ESCAPE", actionListener);
  }
  
  public void setLoadConfigEnabled(boolean enable) {
    loadConfigCheckBox.setSelected(enable);
    configDirTextField.setEnabled(enable);
    browseConfigBtn.setEnabled(enable);
    loadConfig = enable;
  }
  
  private void setLoadPropsEnabled(boolean enable) {
    loadPropsCheckBox.setSelected(enable);
    propsDirTextField.setEnabled(enable);
    browsePropsBtn.setEnabled(enable);
    loadProps = enable;
  }
  
  private void addLoadProfileGroup() {
    addComponent(contentPane, new javax.swing.JSeparator(0), 10, 120, width - 30, 10);
    
    addComponent(contentPane, info, 5, 122, 30, 30);
    addComponent(contentPane, loadConfigCheckBox, 5, 150, 150, 30);
    addComponent(contentPane, configDirTextField, 160, 150, 300, 25);
    addComponent(contentPane, browseConfigBtn, 470, 150, 80, 25);
    
    addComponent(contentPane, loadPropsCheckBox, 5, 180, 155, 30);
    addComponent(contentPane, propsDirTextField, 160, 180, 300, 25);
    addComponent(contentPane, browsePropsBtn, 470, 180, 80, 25);
  }
  
  private void removeLoadProfileGroup() {
    removeComponent(contentPane, info);
    removeComponent(contentPane, loadConfigCheckBox);
    removeComponent(contentPane, configDirTextField);
    removeComponent(contentPane, browseConfigBtn);
    
    removeComponent(contentPane, loadPropsCheckBox);
    removeComponent(contentPane, propsDirTextField);
    removeComponent(contentPane, browsePropsBtn);
  }
  
  private void addNewUserGroup() {
    int startFromX = 170;
    int startFromY = 220;
    
    newUserLabel = new JLabel("Enter Username: ");
    addComponent(contentPane, newUserLabel, 10, 170, 100, 20);
    addComponent(contentPane, newUserName, 150, 170, 300, 20);
    addComponent(contentPane, addUserBtn, startFromX, startFromY, buttonWidth, buttonHeight);
    addComponent(contentPane, cancelBtn2, startFromX + buttonWidth + 10, startFromY, buttonWidth, buttonHeight);
  }
  
  private void removeNewUserGroup() {
    removeComponent(contentPane, newUserLabel);
    removeComponent(contentPane, newUserName);
    removeComponent(contentPane, addUserBtn);
    removeComponent(contentPane, cancelBtn2);
  }
  

  private FileFilter XMLFileFilter = new FileFilter()
  {

    public boolean accept(File f)
    {
      return (f.isDirectory()) || (f.getName().endsWith(".xml")) || (f.getName().endsWith(".XML"));
    }
    
    public String getDescription() {
      return "XML Files (*.xml, *.XML)";
    }
  };
  


  private FileFilter PropertiesFileFilter = new FileFilter()
  {

    public boolean accept(File f)
    {
      return (f.isDirectory()) || (f.getName().endsWith(".properties"));
    }
    
    public String getDescription() {
      return "Properties Files (*.properties)";
    }
  };
}
