package ocr.manager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import ocr.gui.OCRInterface;
import ocr.tif.ImageReaderDrawer;
















public class ExternalProcessManager
{
  Point dialogLocation = new Point(300, 300);
  OCRInterface ocrIF = OCRInterface.this_interface;
  

  ProgressDialog progressDialog;
  

  LongRunningExternalTask execThread;
  

  class ProgressDialog
    extends JDialog
    implements ActionListener
  {
    private JProgressBar progressBar;
    
    private JLabel statusMessage;
    
    private JButton okButton;
    
    private JButton cancelButton;
    
    boolean success;
    
    private JTextArea evtViewer;
    
    private JScrollPane scrollPane;
    
    private BufferedWriter logFile;
    

    public void println(String line)
    {
      evtViewer.append(line);
      evtViewer.setCaretPosition(evtViewer.getText().length());
    }
    







    private void initComponents()
    {
      setModal(true);
      setResizable(true);
      setDefaultCloseOperation(0);
      

      statusMessage = new JLabel();
      progressBar = new JProgressBar();
      scrollPane = new JScrollPane();
      evtViewer = new JTextArea();
      okButton = new JButton();
      cancelButton = new JButton();
      statusMessage.setText("Processing ...");
      
      evtViewer.setBackground(new Color(0, 0, 0));
      evtViewer.setColumns(20);
      evtViewer.setForeground(new Color(255, 255, 255));
      evtViewer.setLineWrap(true);
      evtViewer.setRows(5);
      evtViewer.setWrapStyleWord(true);
      scrollPane.setViewportView(evtViewer);
      progressBar.setIndeterminate(true);
      
      okButton.setText("OK");
      cancelButton.setText("Cancel");
      okButton.addActionListener(this);
      cancelButton.addActionListener(this);
      okButton.setEnabled(false);
      
      setDefaultCloseOperation(2);
      
      getContentPane().setLayout(new BorderLayout());
      
      setPreferredSize(new Dimension(700, 400));
      
      scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      evtViewer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      
      JPanel top = new JPanel();
      top.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      top.setLayout(new BorderLayout());
      top.add(statusMessage, "West");
      top.add(progressBar, "East");
      
      JPanel bottom = new JPanel();
      bottom.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
      bottom.add(okButton);
      bottom.add(cancelButton);
      
      getContentPane().add(top, "North");
      getContentPane().add(scrollPane, "Center");
      getContentPane().add(bottom, "South");
    }
    




    public ProgressDialog()
    {
      super("External Process", true);
      initComponents();
      success = true;
      setLocation(dialogLocation);
    }
    





    public void showDialog(String messageIn)
    {
      this_interfacechildWindow = this;
      setTitle(messageIn);
      cancelButton.setVisible(execThread.isInterruptable());
      
      progressBar.setVisible(true);
      
      pack();
      setVisible(true);
    }
    






    public void closeDialog()
    {
      progressBar.setIndeterminate(false);
      okButton.setEnabled(true);
      cancelButton.setEnabled(false);
      if (success)
      {
        statusMessage.setText("Completed");
        progressBar.setValue(progressBar.getMaximum());
      }
      else {
        progressBar.setValue(0);
      }
    }
    



    private void closeDialogonError()
    {
      statusMessage.setText("Encountered errors");
      success = false;
      closeDialog();
    }
    




    private void closeDialogonAbort()
    {
      statusMessage.setText("Process aborted by user");
      success = false;
      closeDialog();
    }
    



    public void actionPerformed(ActionEvent e)
    {
      if (((JButton)e.getSource()).getText().equals("Cancel")) {
        try {
          logFile.write(getTitle() + ": Execution aborted by user");
          logFile.newLine();
          logFile.flush();
        } catch (Exception ex) {
          ex.printStackTrace();
        }
        execThread.killProcess();
        closeDialogonAbort();
      }
      else if (((JButton)e.getSource()).getText().equals("OK")) {
        this_interfacechildWindow = null;
        super.setVisible(false);
      }
    }
    
    public void setLogFile(BufferedWriter logFile) {
      this.logFile = logFile;
    }
  }
  


  public class StreamGobbler
    extends Thread
  {
    InputStream is;
    

    String type;
    

    BufferedWriter logFile;
    

    String executingCommand;
    

    public StreamGobbler(InputStream is, String type, BufferedWriter logFile)
    {
      this.is = is;
      this.type = type;
      this.logFile = logFile;
      executingCommand = null;
    }
    
    public void run()
    {
      try
      {
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line = null;
        if (executingCommand != null) {
          progressDialog.println("\nEXECUTING COMMAND:\n" + 
            executingCommand);
          progressDialog.println("\n");
        }
        while ((line = br.readLine()) != null)
        {
          progressDialog.println("\n" + line);
          logFile.write(type + "> " + line);
          logFile.newLine();
          logFile.flush();
        }
      }
      catch (IOException ioe) {
        ioe.printStackTrace();
      }
    }
    
    public void setExecutingCommand(String str)
    {
      executingCommand = str;
    }
  }
  

  public class LongRunningExternalTask
    extends Thread
  {
    String sysCommand;
    
    File dir;
    
    public InputStreamReader in;
    
    public Process process;
    
    BufferedWriter logFile;
    
    public ExternalProcessManager.StreamGobbler errorGobbler;
    
    public ExternalProcessManager.StreamGobbler outputGobbler;
    
    public LongRunningExternalTask(String name, String sysCommand, File dir, BufferedWriter logFile)
    {
      super();
      this.sysCommand = sysCommand;
      this.dir = dir;
      this.logFile = logFile;
      
      if (logFile == null) {
        this.logFile = OCRInterface.log;
      }
    }
    




    public boolean isInterruptable()
    {
      return true;
    }
    




    public void killProcess()
    {
      if (process != null) { process.destroy();
      }
    }
    




    public void run()
    {
      process = null;
      try
      {
        OCRInterface.this_interface.setCursor(Cursor.getPredefinedCursor(3));
        ImageReaderDrawer.picture.setCursor(Cursor.getPredefinedCursor(3));
        System.out.println("Executing: " + sysCommand);
        process = Runtime.getRuntime().exec(sysCommand, null, dir);
        
        errorGobbler = new ExternalProcessManager.StreamGobbler(ExternalProcessManager.this, process.getErrorStream(), "ERROR", logFile);
        outputGobbler = new ExternalProcessManager.StreamGobbler(ExternalProcessManager.this, process.getInputStream(), "OUTPUT", logFile);
        



        outputGobbler.setExecutingCommand(sysCommand);
        outputGobbler.start();
        errorGobbler.start();
        
        outputGobbler.join();
        errorGobbler.join();
        
        int exitVal = process.waitFor();
        logFile.write("Exit Value: " + exitVal);
        logFile.newLine();
        if (exitVal != 0) {
          ExternalProcessManager.ProgressDialog.access$0(progressDialog);
        } else {
          progressDialog.closeDialog();
        }
        logFile.flush();
        logFile.close();

      }
      catch (Exception ex)
      {

        System.out.println(ex.toString());
        ExternalProcessManager.ProgressDialog.access$0(progressDialog);
        


        String msg1 = "Encountered problems while executing command: \n\n" + 
          sysCommand + "\n\n" + 
          "Syntax of the Executing command is invalid or executable program " + 
          "does not exist in the directory you are passing.\n\n" + 
          "Caused by:\n" + ex.getMessage();
        
        JOptionPane pane = ExternalProcessManager.getNarrowOptionPane(100);
        pane.setMessage(msg1);
        pane.setMessageType(0);
        JDialog dialog = pane.createDialog(OCRInterface.this_interface, 
          "Script Loading Problem");
        dialog.setVisible(true);
        try
        {
          logFile.write("ENCOUNTERED PROBLEMS:");
          logFile.newLine();
          logFile.newLine();
          logFile.write(msg1);
          logFile.flush();
          logFile.close();
          progressDialog.setVisible(false);
        } catch (IOException e) {
          e.printStackTrace();
          try {
            logFile.newLine();
            logFile.newLine();
            logFile.write("Caused by:");
            logFile.newLine();
            logFile.write(ex.getMessage());
            logFile.flush();
          } catch (IOException e1) {
            e1.printStackTrace();
          }
        }
        
        OCRInterface.this_interface.setCursor(Cursor.getPredefinedCursor(0));
        ImageReaderDrawer.picture.setCursor(Cursor.getPredefinedCursor(0));
      }
    }
    

    public Process getProcess()
    {
      return process;
    }
  }
  





  public ExternalProcessManager()
  {
    progressDialog = new ProgressDialog();
  }
  

















  public boolean executeProgram(String name, String[] args, File dir, String userMessage, BufferedWriter logFile, boolean showDialog)
  {
    String sysCommand = name;
    if (args != null)
    {
      for (int i = 0; i < args.length; i++) {
        sysCommand = sysCommand + " " + args[i];
      }
    }
    progressDialog.setLogFile(logFile);
    execThread = new LongRunningExternalTask(name, sysCommand, dir, logFile);
    execThread.start();
    
    if (showDialog) {
      progressDialog.showDialog(userMessage);
    }
    return progressDialog.success;
  }
  
  public LongRunningExternalTask getTask() {
    return execThread;
  }
  











  public boolean executeProgram(String name, File dir, String userMessage)
  {
    return executeProgram(name, null, dir, userMessage, null, true);
  }
  



















  public boolean executeProgram(String name, File dir, String userMessage, BufferedWriter logFile, boolean showDialog)
  {
    return executeProgram(name, null, dir, userMessage, logFile, showDialog);
  }
  


















  public static JOptionPane getNarrowOptionPane(int maxCharactersPerLineCount)
  {
    new JOptionPane()
    {
      int maxCharactersPerLineCount;
      
      public int getMaxCharactersPerLineCount()
      {
        return maxCharactersPerLineCount;
      }
    };
  }
}
