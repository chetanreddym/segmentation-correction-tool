package ocr.gui;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.PrintStream;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.TransferHandler;
import javax.swing.text.Element;

public class TestRunner
{
  class ZoneLabel extends JLabel
  {
    Zone zone;
    String name;
    
    public ZoneLabel(String nameIn)
    {
      super();
      name = nameIn;
    }
    
    public void setZone(Zone zIn)
    {
      if (zIn != null) {
        setText(zIn.toString());
      } else {
        setText("null");
      }
    }
  }
  
  public class StringTransferHandler extends TransferHandler
  {
    public StringTransferHandler() {}
    
    protected Transferable createTransferable(JComponent c)
    {
      System.out.println("createTransferable" + c + "[]");
      return new java.awt.datatransfer.StringSelection(name);
    }
    
    public int getSourceActions(JComponent c) {
      return 3;
    }
    
    public boolean importData(JComponent c, Transferable t) {
      System.out.println("import data" + c + "+++" + t + " [TestRunner:67]");
      if (canImport(c, t.getTransferDataFlavors())) {
        try {
          String str = (String)t.getTransferData(DataFlavor.stringFlavor);
          
          System.out.println("importing string: " + str);
          return true;
        } catch (UnsupportedFlavorException ufe) { ufe.printStackTrace();
        } catch (IOException ioe) { ioe.printStackTrace();
        }
      }
      
      return false;
    }
    

    protected void exportDone(JComponent c, Transferable data, int action) {}
    
    public boolean canImport(JComponent c, DataFlavor[] flavors)
    {
      System.out.println("canImport" + c + "[]");
      for (int i = 0; i < flavors.length; i++) {
        if (DataFlavor.stringFlavor.equals(flavors[i])) {
          return true;
        }
      }
      return false;
    }
  }
  


  class ZoneTransferHandler
    extends TransferHandler
  {
    String mimeType = "application/x-java-jvm-local-objectref;class=ocr.gui.Zone";
    
    DataFlavor zoneFlavor;
    

    ZoneTransferHandler()
    {
      try
      {
        zoneFlavor = new DataFlavor(mimeType);
      }
      catch (ClassNotFoundException e)
      {
        e.printStackTrace();
        System.out.println("failed to create DataFlavor:" + e.toString() + "[TestRunner:65]");
      }
    }
    
    public boolean importData(JComponent c, Transferable t)
    {
      System.out.println("import data" + c + "+++" + t + " [TestRunner:72]");
      return super.importData(c, t);
    }
    
    public void exportAsDrag(JComponent comp, java.awt.event.InputEvent e, int action)
    {
      System.out.println("exportAsDrag" + comp + "+++" + e + "[TR:77]");
      super.exportAsDrag(comp, e, action);
    }
    
    protected Transferable createTransferable(JComponent c)
    {
      System.out.println("createTransferable" + c + "[]");
      


      return null;
    }
    
    public boolean canImport(JComponent c, DataFlavor[] flavors)
    {
      System.out.println("canImport" + c + "[]");
      for (int i = 0; i < flavors.length; i++)
      {
        if (zoneFlavor.equals(flavors[i]))
        {
          return true;
        }
      }
      return false;
    }
  }
  






  public TestRunner()
  {
    JFrame frame = new JFrame("DnD play");
    JPanel contentPane = new JPanel();
    contentPane.setLayout(new javax.swing.BoxLayout(contentPane, 1));
    frame.addWindowListener(new java.awt.event.WindowAdapter()
    {

      public void windowClosing(WindowEvent e)
      {
        System.exit(0);
      }
      

    });
    StringTransferHandler zoneTH = new StringTransferHandler();
    
    ZoneLabel rec = new ZoneLabel("receiver");
    rec.setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.TitledBorder("receiver"), new javax.swing.border.EmptyBorder(60, 60, 60, 60)));
    rec.setTransferHandler(zoneTH);
    

    ZoneLabel send = new ZoneLabel("sender");
    send.setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.TitledBorder("sender"), new javax.swing.border.EmptyBorder(60, 60, 60, 60)));
    send.setTransferHandler(zoneTH);
    send.addMouseListener(
      new java.awt.event.MouseAdapter()
      {
        public void mousePressed(MouseEvent e)
        {
          JComponent source = (JComponent)e.getSource();
          source.getTransferHandler().exportAsDrag(source, e, 1);

        }
        

      });
    contentPane.add(rec);
    contentPane.add(send);
    


    frame.setContentPane(contentPane);
    frame.setLocation(300, 300);
    frame.pack();
    frame.setVisible(true);
  }
  



  public void createGuiElements(JPanel contentPane) {}
  


  void displayElement(Element el, int depth)
  {
    for (int i = 0; i < depth; i++)
      System.out.print("\t");
    System.out.print(el.getName() + "\n");
    
    for (i = 0; i < el.getElementCount(); i++) {
      displayElement(el.getElement(i), depth + 1);
    }
  }
  
  public static void main(String[] args) {
    System.out.println(System.getProperty("user.dir") + "[TestRunner:62]");
    new TestRunner();
  }
}
