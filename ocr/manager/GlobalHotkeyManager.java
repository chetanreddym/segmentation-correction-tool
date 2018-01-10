package ocr.manager;

import java.awt.AWTEvent;
import java.awt.Container;
import java.awt.event.KeyEvent;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;

public class GlobalHotkeyManager extends java.awt.EventQueue
{
  private static final boolean DEBUG = false;
  private static final GlobalHotkeyManager instance = new GlobalHotkeyManager();
  
  private final InputMap keyStrokes = new InputMap();
  
  private boolean enabled = true;
  
  private final ActionMap actions;
  
  static
  {
    java.awt.Toolkit.getDefaultToolkit().getSystemEventQueue().push(instance);
  }
  
  private GlobalHotkeyManager() {
    actions = new ActionMap();
  }
  
  public static GlobalHotkeyManager getInstance()
  {
    return instance;
  }
  
  public InputMap getInputMap() {
    return keyStrokes;
  }
  
  public ActionMap getActionMap() {
    return actions;
  }
  
  public void setEnabled(boolean e) {
    enabled = e;
  }
  
  public boolean getEnabled() {
    return enabled;
  }
  
  protected void dispatchEvent(AWTEvent event) {
    if ((enabled) && 
      ((event instanceof KeyEvent)))
    {









      KeyStroke ks = KeyStroke.getKeyStrokeForEvent((KeyEvent)event);
      

      String actionKey = (String)keyStrokes.get(ks);
      if (actionKey != null)
      {

        Action action = actions.get(actionKey);
        if ((action != null) && (action.isEnabled()))
        {
          action.actionPerformed(new java.awt.event.ActionEvent(event.getSource(), 
            event.getID(), actionKey, ((KeyEvent)event)
            .getModifiers()));
          return;
        }
      }
    }
    
    super.dispatchEvent(event);
  }
  

  public class GlobalHotkeyManagerTest
    extends javax.swing.JFrame
  {
    private static final String UIROBOT_KEY = "UIRobot";
    private final KeyStroke uirobotHotkey = KeyStroke.getKeyStroke(65, 10, false);
    
    private final Action uirobot = new javax.swing.AbstractAction() {
      public void actionPerformed(java.awt.event.ActionEvent e) {
        setEnabled(false);
        javax.swing.JOptionPane.showMessageDialog(GlobalHotkeyManager.GlobalHotkeyManagerTest.this, 
          "UIRobot Hotkey was pressed");
        setEnabled(true);
      }
    };
    
    public GlobalHotkeyManagerTest() {
      super();
      setSize(500, 400);
      getContentPane().setLayout(new java.awt.FlowLayout());
      getContentPane().add(new javax.swing.JButton("Button 1"));
      getContentPane().add(new javax.swing.JTextField(20));
      getContentPane().add(new javax.swing.JButton("Button 2"));
      GlobalHotkeyManager hotkeyManager = 
        GlobalHotkeyManager.getInstance();
      hotkeyManager.getInputMap().put(uirobotHotkey, "UIRobot");
      hotkeyManager.getActionMap().put("UIRobot", uirobot);
      setDefaultCloseOperation(3);
      setVisible(true);
    }
  }
  
  public static void main(String[] args)
  {
    GlobalHotkeyManager tmp6_3 = getInstance();tmp6_3.getClass();new GlobalHotkeyManagerTest(tmp6_3);
  }
}
