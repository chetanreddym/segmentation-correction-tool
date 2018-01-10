package signalprocesser.shared;

import javax.swing.JPanel;

public class StatusDialog extends javax.swing.JDialog { private javax.swing.JLabel lblCaption;
  private JPanel panelCenter;
  private JPanel panelGap;
  private javax.swing.JProgressBar progressBar;
  
  public static void start(java.awt.Window parent, String title, String caption, Thread thread) { StatusDialog _dialog; if ((parent instanceof java.awt.Frame)) {
      _dialog = new StatusDialog((java.awt.Frame)parent, title, caption); } else { StatusDialog _dialog;
      if ((parent instanceof java.awt.Dialog)) {
        _dialog = new StatusDialog((java.awt.Dialog)parent, title, caption);
      } else
        throw new RuntimeException("Unknown window type " + parent.getClass().getName()); }
    StatusDialog _dialog;
    final StatusDialog dialog = _dialog;
    Thread dialogthread = new Thread() {
      public void run() {
        setVisible(true);
      }
    };
    dialogthread.start();
    

    new Thread()
    {
      public void run() {
        start();
        synchronized (StatusDialog.this) {
          try {
            wait();
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
        }
        

        dialog.setVisible(false);
        dialog.dispose();
      }
    }.start();
  }
  
  public StatusDialog(java.awt.Frame parent, String title, String caption) {
    super(parent, true);
    initComponents();
    setTitle(title);
    lblCaption.setText(caption);
  }
  
  public StatusDialog(java.awt.Dialog parent, String title, String caption) {
    super(parent, true);
    initComponents();
    setTitle(title);
    lblCaption.setText(caption);
  }
  
  private void initComponents()
  {
    panelCenter = new JPanel();
    lblCaption = new javax.swing.JLabel();
    panelGap = new JPanel();
    progressBar = new javax.swing.JProgressBar();
    
    getContentPane().setLayout(new java.awt.GridBagLayout());
    
    setDefaultCloseOperation(2);
    setResizable(false);
    panelCenter.setLayout(new java.awt.BorderLayout());
    
    panelCenter.add(lblCaption, "North");
    
    panelGap.setLayout(null);
    
    panelGap.setPreferredSize(new java.awt.Dimension(220, 5));
    panelCenter.add(panelGap, "Center");
    
    progressBar.setIndeterminate(true);
    panelCenter.add(progressBar, "South");
    
    getContentPane().add(panelCenter, new java.awt.GridBagConstraints());
    
    java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
    setBounds((width - 319) / 2, (height - 127) / 2, 319, 127);
  }
  
  public static void main(String[] args)
  {
    StatusDialog dialog = new StatusDialog(new javax.swing.JFrame(), "Title", "Caption");
    dialog.setVisible(true);
  }
}
