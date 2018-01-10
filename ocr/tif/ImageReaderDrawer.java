package ocr.tif;

import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.SeekableStream;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.TransposeDescriptor;
import javax.media.jai.operator.TransposeType;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JLabel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import ocr.gui.BrowserToolBar;
import ocr.gui.MultiPagePanel;
import ocr.gui.OCRInterface;
import ocr.manager.OcrPropInfoHolder;
import ocr.manager.PropertiesInfoHolder;






public class ImageReaderDrawer
  extends JDesktopPane
{
  public ImageDisplay curr_canvas = null;
  



  public MyScrollPane pictureScrollPane = null;
  



  private OCRInterface ocrIF = null;
  



  private PlanarImage image = null;
  
  public static String file_name = "";
  
  public static String file_path = "";
  
  public boolean file_open = false;
  
  public static JLabel picture = null;
  


  public static int numMultiTifPages;
  


  public ImageReaderDrawer(String filename, OCRInterface myIF)
  {
    ocrIF = myIF;
    






    if (filename == null) {
      filename = "images/mainscreen.jpg";
    }
    



    setImageFile(filename);
    

    picture = new JLabel()
    {


      public void repaint()
      {


        super.repaint();
      }
      

    };
    curr_canvas = new ImageDisplay(image, ocrIF, this);
    


    picture.setHorizontalAlignment(2);
    picture.setVerticalAlignment(1);
    
    picture.setIcon(curr_canvas);
    picture.addMouseListener(curr_canvas);
    picture.addMouseMotionListener(curr_canvas);
    
    int w = curr_canvas.getIconWidth();
    int h = curr_canvas.getIconHeight();
    picture.setPreferredSize(new Dimension(w, h));
    


    setOpaque(true);
    setLayout(new BorderLayout());
    setBackground(Color.darkGray);
    
    pictureScrollPane = new MyScrollPane(picture);
    








    pictureScrollPane.getViewport().setScrollMode(
      0);
    pictureScrollPane.setMaximumSize(new Dimension(w, h));
    
    add(pictureScrollPane, "Center");
    
    picture.setLocation(0, 0);
    




    pictureScrollPane.addMouseWheelListener(new MouseWheelListener() {
      private int UP = -1;
      private int DOWN = 1;
      
      public void mouseWheelMoved(MouseWheelEvent e) {
        if ((e.isControlDown()) && (e.getWheelRotation() == UP)) {
          OCRInterface.this_interface.getToolbar().fireZoomIn();
        } else if ((e.isControlDown()) && (e.getWheelRotation() == DOWN)) {
          OCRInterface.this_interface.getToolbar().fireZoomOut();
        }
      }
    });
  }
  






  public void setImageFile(String filename)
  {
    String dataFileName = filename;
    
    String ext = ocrIF.getFileExtension(filename);
    
    if (!filename.equalsIgnoreCase("images/mainscreen.jpg")) {
      String temp = new File(filename).getName();
      filename = OCRInterface.this_interface.getBaseImage(temp, ext);
    }
    
    if (filename.equalsIgnoreCase("images/mainscreen.jpg")) {
      URL urlImage = getClass().getClassLoader().getResource("images/mainscreen.jpg");
      image = JAI.create("url", urlImage);
    } else {
      image = JAIImageReader.readImage(filename, false);
    }
    if (image == null) {
      return;
    }
    ocrIF.workmodeProps[0].clearDataFileHash();
    
    ocrIF.ocrProperties.updatePropsOnImageLoad(dataFileName);
    
    MultiPagePanel mpp = OCRInterface.this_interface.getMultiPagePanel();
    
    if (!mpp.isVisible()) {
      mpp.setVisible(true);
    }
    if ((filename.contains(".tif")) || (filename.contains(".TIF")))
    {
      numMultiTifPages = numPagesInMultiTiff(filename);
      if (numMultiTifPages > 1)
      {
        mpp.setEnabled(true);
        
        mpp.setPageTotal(numMultiTifPages);
        
        ocrIF.multiPageTiff = true;

      }
      else
      {
        mpp.setPageTotal(1);
        mpp.setEnabled(false);
        ocrIF.multiPageTiff = false;
      }
    }
    else
    {
      this_interfacecurrPageID = 0;
      mpp.setPageTotal(1);
      mpp.setEnabled(false);
      ocrIF.multiPageTiff = false;
    }
    

    try
    {
      File file = new File(filename);
      file_path = file.getCanonicalPath();
      file_name = file.getCanonicalFile().getName();
      
      processImageFile();
    } catch (IOException e) {
      System.out.println("ImageReaderDrawer. setImageFile. Canonical file getting problem.");
    }
  }
  

  public void setImageFile(PlanarImage imageIn)
  {
    image = imageIn;
    
    curr_canvas.initializeImage(curr_canvas.getscaledImage());
    
    picture.setIcon(curr_canvas);
  }
  
  public void processImageFile()
  {
    if (curr_canvas != null) {
      if (image != null)
      {
        curr_canvas.setImage(image);

      }
      else
      {
        System.out.println("Created image is null");
      }
      
      picture.setIcon(curr_canvas);
      
      int w = curr_canvas.getIconWidth();
      int h = curr_canvas.getIconHeight();
      picture.setPreferredSize(new Dimension(w, h));
      picture.repaint();
      if (pictureScrollPane != null) {
        pictureScrollPane.revalidate();
      }
    }
  }
  
  public static int numPagesInMultiTiff(String filename) {
    try {
      File file = new File(filename);
      SeekableStream ss = new FileSeekableStream(file);
      ImageDecoder decoder = ImageCodec.createImageDecoder("tiff", ss, null);
      return decoder.getNumPages();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    
    return 0;
  }
  



  public class MyScrollPane
    extends JScrollPane
    implements MouseMotionListener
  {
    private Point drag_point = null;
    
    private JComponent view = null;
    
    private Point viewportPosition;
    
    public void mouseDragged(MouseEvent e)
    {
      if (SwingUtilities.isRightMouseButton(e))
      {
        JScrollBar horiz = getHorizontalScrollBar();
        JScrollBar vert = getVerticalScrollBar();
        
        horiz.setValue(curr_canvas.xSc + (e.getX() - curr_canvas.x) / 2);
        vert.setValue(curr_canvas.ySc + (e.getY() - curr_canvas.y) / 2);
        
        setHorizontalScrollBar(horiz);
        setVerticalScrollBar(vert);
        System.out.println("horiz.: " + horiz.getValue());
        System.out.println("vert.: " + vert.getValue());
      }
      if (OCRInterface.currOppmode == 8) {
        Point lastDragPoint = drag_point;
        
        Point lastViewPortPosition = viewportPosition;
        viewportPosition = getViewport().getViewPosition();
        drag_point = new Point(e.getPoint());
        
        int xOFF = x - drag_point.x;
        int yOFF = y - drag_point.y;
        


        Point point = new Point(x + xOFF, 
          y + yOFF);
        

        if ((x < 0) || (y < 0)) {
          return;
        }
        getViewport().setViewPosition(point);
        
        drag_point = new Point(drag_point.x + xOFF, 
          drag_point.y + yOFF);
        

        viewportPosition = point;
      }
      else {
        Rectangle r = new Rectangle(e.getX(), e.getY(), 1, 
          1);
        view.scrollRectToVisible(r);
      }
    }
    
    public void mouseMoved(MouseEvent e) {}
    
    public MyScrollPane() { setAutoscrolls(true); }
    

    public MyScrollPane(JComponent view)
    {
      super();
      this.view = view;
      setAutoscrolls(true);
      this.view.setAutoscrolls(true);
      
      this.view.addMouseListener(new MouseAdapter() {
        public void mousePressed(MouseEvent e) {
          if (OCRInterface.currOppmode == 8) {
            drag_point = new Point(e.getPoint());
            viewportPosition = getViewport().getViewPosition();
          }
        }
      });
      this.view.addMouseMotionListener(this);
    }
  }
  





  public PlanarImage getOriginalImage()
  {
    return image;
  }
  
  public void setOriginalImage(PlanarImage image) {
    this.image = image;
  }
  



  public void rotateOriginalImage(int degrees)
  {
    System.out.println("degrees: " + degrees);
    if (degrees == 0) {
      return;
    }
    TransposeType transposeType = null;
    
    switch (degrees) {
    case 90:  transposeType = TransposeDescriptor.ROTATE_90; break;
    case 180:  transposeType = TransposeDescriptor.ROTATE_180; break;
    case 270:  transposeType = TransposeDescriptor.ROTATE_270;
    }
    
    ParameterBlock pb = new ParameterBlock();
    pb.addSource(image);
    pb.add(transposeType);
    System.out.println("image before rotation: " + image);
    image = PlanarImage.wrapRenderedImage(JAI.create("transpose", pb).getAsBufferedImage());
    System.out.println("image after rotation: " + image);
  }
  







  public MyScrollPane getPictureScrollPane()
  {
    return pictureScrollPane;
  }
  
  public void setPictureScrollPane(MyScrollPane pictureScrollPane) {
    this.pictureScrollPane = pictureScrollPane;
  }
  
  public static String getFile_name() {
    return file_name;
  }
  
  public static void setFile_name(String file_name) {
    file_name = file_name;
  }
  
  public static String getFile_path() {
    return file_path;
  }
  
  public static void setFile_path(String file_path) {
    file_path = file_path;
  }
  
  public OCRInterface getOcrIF() {
    return ocrIF;
  }
  
  public void setOcrIF(OCRInterface ocrIF) {
    this.ocrIF = ocrIF;
  }
}
