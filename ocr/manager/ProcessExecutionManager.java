package ocr.manager;

import java.awt.Cursor;
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
import java.util.Calendar;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import ocr.gui.OCRInterface;
import ocr.tag.GetDateTime;























public class ProcessExecutionManager
{
  Point dialogLocation = new Point(300, 300);
  



  OCRInterface ocrIF = OCRInterface.this_interface;
  




  final String dic_SegResultLocation = "Segmentation" + File.separatorChar + "SegResult";
  




  final String dic_parcingRuleBased = "Tagging" + File.separatorChar + "RuleBased";
  



  final String ap_dictParserLocation = "." + File.separatorChar + "bin" + File.separatorChar + "execute" + File.separatorChar + "new" + File.separatorChar + "dictParser.exe";
  





  final String dic_taggingPropFileLocation = "Tagging" + File.separatorChar + "tag.config";
  





  final String dic_tagBbxFilesLocation = "Tagging" + File.separatorChar + "RuleBased" + File.separatorChar + "Result";
  




  final String ap_genResourcesLocation = "." + File.separatorChar + "bin" + File.separatorChar + "execute" + File.separatorChar + "new" + File.separatorChar + "genResource.exe";
  





  final String dic_ocr = "OCR";
  



  final String ap_OCR_OrganizerLocation = "." + File.separatorChar + "bin" + File.separatorChar + "execute" + File.separatorChar + "Segmentation" + File.separatorChar + "OCR_Organizer.exe";
  
  final String ap_OCR_AdaptiveLocation = "." + File.separatorChar + "bin" + File.separatorChar + "execute" + File.separatorChar + "OCR" + File.separatorChar + "AdpOCRp.exe";
  final String ap_OCR_AdaptiveArgsFileLocation = "." + File.separatorChar + "bin" + File.separatorChar + "execute" + File.separatorChar + "OCR";
  final String ap_OCR_AdaptiveArgsFileName = "AdaptiveArgsFile.txt";
  


  final String dic_ImagesDir = "Images";
  



  final String ap_performOcrLocation = "." + File.separatorChar + "bin" + File.separatorChar + "execute" + File.separatorChar + "ocr" + File.separatorChar + "PerformOCR.exe";
  


  final String ap_trainTBLLocation = "." + File.separatorChar + "bin" + File.separatorChar + "execute" + File.separatorChar + "TBL" + File.separatorChar + "train-TBL.pl";
  
  final String ap_runTBLLocation = "." + File.separatorChar + "bin" + File.separatorChar + "execute" + File.separatorChar + "TBL" + File.separatorChar + "run-TBL.pl";
  



















































































































































  WaitDialog waitDialog;
  




















































































































































  class WaitDialog
    extends JDialog
    implements ActionListener
  {
    private Timer timer;
    



















































































































































    private JProgressBar progressBar;
    


















































































































































    JButton cancelButt;
    


















































































































































    JLabel message;
    


















































































































































    ProcessExecutionManager.LongRunningExternalTask execThread;
    


















































































































































    private int ONE_SECOND = 10000;
    


    public WaitDialog()
    {
      super("Process Executing", true);
      JPanel contentPane = (JPanel)getContentPane();
      contentPane.setBorder(new EmptyBorder(10, 10, 10, 130));
      contentPane.setLayout(new BoxLayout(contentPane, 1));
      
      message = new JLabel();
      message.setAlignmentX(0.0F);
      
      cancelButt = new JButton("Cancel");
      cancelButt.addActionListener(this);
      cancelButt.setAlignmentX(0.0F);
      
      contentPane.add(message);
      
      if (execThread != null) {
        progressBar = new JProgressBar(0, execThread.getTaskLength());
      } else {
        progressBar = new JProgressBar(0, 0);
      }
      progressBar.setValue(0);
      progressBar.setStringPainted(true);
      
      contentPane.add(progressBar);
      progressBar.setVisible(false);
      
      contentPane.add(Box.createVerticalStrut(5));
      contentPane.add(cancelButt);
      
      setLocation(dialogLocation);
      

      timer = new Timer(ONE_SECOND, new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          progressBar.setValue(execThread.getCurrent());
          
          if (execThread.isDone()) { timer.stop();
          }
        }
      });
    }
    







    public void show(String messageIn, ProcessExecutionManager.LongRunningExternalTask execThread)
    {
      this_interfacechildWindow = this;
      message.setText(messageIn);
      this.execThread = execThread;
      
      timer.setDelay((int)timeTakenForOnePage);
      progressBar.setMaximum(execThread.getTaskLength());
      progressBar.setValue(0);
      timer.start();
      
      cancelButt.setVisible(execThread.isInterruptable());
      
      if (execThread.isProgressBarSupported()) {
        progressBar.setVisible(true);
      }
      System.out.println(timer.getDelay());
      pack();
      super.setVisible(true);
    }
    

    public void hide()
    {
      timer.stop();
      System.out.println("done?: " + execThread.isDone());
      

      if (execThread != null) {
        progressBar.setValue(execThread.getTaskLength());
      }
      this_interfacechildWindow = null;
      super.setVisible(false);
    }
    



    public void actionPerformed(ActionEvent e)
    {
      try
      {
        OCRInterface.log.write("----Execution aborted by user");
        timer.stop();
        System.out.println("----Execution aborted by user");
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
        System.out.println(ex.toString() + "[ProcessExectuonManager:419]");
      }
      execThread.killProcess();
    }
  }
  
  class StreamGobbler extends Thread
  {
    InputStream is;
    String type;
    BufferedWriter logFile;
    
    StreamGobbler(InputStream is, String type, BufferedWriter logFile)
    {
      this.is = is;
      this.type = type;
    }
    
    public void run()
    {
      try
      {
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line = null;
        
        while ((line = br.readLine()) != null) {
          System.out.println(type + ">" + line);
        }
      } catch (IOException ioe) {
        ioe.printStackTrace();
      }
    }
  }
  























































  class LongRunningExternalTask
    extends Thread
  {
    String sysCommand;
    






















































    public InputStreamReader in;
    





















































    public Process process;
    





















































    private File[] outputFiles;
    





















































    private int currentFile = 0;
    private long startLastModified = 0L;
    private long timeTakenForOnePage = 0L;
    







    public LongRunningExternalTask(String name, String sysCommand, File[] outputFiles, long timeForOnePage)
    {
      super();
      this.sysCommand = sysCommand;
      this.outputFiles = outputFiles;
      
      Calendar c = Calendar.getInstance();
      
      startLastModified = c.getTimeInMillis();
      timeTakenForOnePage = timeForOnePage;
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
    





    public boolean isProgressBarSupported()
    {
      if (outputFiles != null) { return true;
      }
      return false;
    }
    


    public boolean isDone()
    {
      getCurrent();
      if (outputFiles == null) return true;
      if (outputFiles.length == currentFile) { return true;
      }
      return false;
    }
    
    public int getCurrent()
    {
      if (outputFiles != null)
      {
        for (; currentFile < outputFiles.length; currentFile += 1)
        {
          File outputFile = outputFiles[currentFile];
          

          if (outputFile.lastModified() <= startLastModified)
            break;
        }
        return currentFile;
      }
      return 0;
    }
    
    public int getTaskLength()
    {
      if (outputFiles != null) { return outputFiles.length;
      }
      return 0;
    }
    



    public void run()
    {
      process = null;
      
      try
      {
        System.out.println("-----Executing: " + sysCommand + " [PEM:557]");
        process = Runtime.getRuntime().exec(sysCommand);
        


        ProcessExecutionManager.StreamGobbler errorGobbler = new ProcessExecutionManager.StreamGobbler(ProcessExecutionManager.this, process.getErrorStream(), "ERROR", OCRInterface.log);
        
        ProcessExecutionManager.StreamGobbler outputGobbler = new ProcessExecutionManager.StreamGobbler(ProcessExecutionManager.this, process.getInputStream(), "OUTPUT", OCRInterface.log);
        
        OCRInterface.log.write(GetDateTime.getInstance());
        OCRInterface.log.newLine();
        
        errorGobbler.start();
        outputGobbler.start();
        
        int exitVal = process.waitFor();
        System.out.println("ExitValue: " + exitVal);
        System.out.println("----Execution completed. [PEM:573]");
        waitDialog.hide();

      }
      catch (Exception ex)
      {
        System.out.println(ex.toString());
        JOptionPane.showMessageDialog(
          ocrIF, 
          "Encountered problems while executing" + sysCommand, 
          "Execution Error.", 
          0);
        waitDialog.hide();
      }
    }
  }
  







  public ProcessExecutionManager()
  {
    waitDialog = new WaitDialog();
  }
  

























































  public void executeProgram(String name, String[] args, String userMessage, File[] outputFiles, long timeForOnePage)
  {
    executeProgram(name, args, userMessage, true, outputFiles, timeForOnePage);
  }
  













  public void executeProgram(String name, String[] args, String userMessage, boolean showDialog, File[] outputFiles, long timeForOnePage)
  {
    Cursor saveCursor = OCRInterface.this_interface.getCursor();
    OCRInterface.this_interface.setCursor(Cursor.getPredefinedCursor(3));
    
    String sysCommand = name;
    if (args != null)
    {
      for (int i = 0; i < args.length; i++) {
        sysCommand = sysCommand + " " + args[i];
      }
    }
    LongRunningExternalTask executionThread = new LongRunningExternalTask(name, sysCommand, outputFiles, timeForOnePage);
    executionThread.start();
    
    if (showDialog) {
      waitDialog.show(userMessage, executionThread);
    }
    OCRInterface.this_interface.setCursor(saveCursor);
  }
  


































  public static void main(String[] args)
  {
    ProcessExecutionProps p = new ProcessExecutionProps();
    
    System.out.println(p.getTimeForOCRProgram());
  }
}
