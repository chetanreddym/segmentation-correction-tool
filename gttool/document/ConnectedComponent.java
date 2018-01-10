package gttool.document;

import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.JPEGEncodeParam;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codec.TIFFEncodeParam;
import com.sun.media.jai.codecimpl.JPEGCodec;
import com.sun.media.jai.codecimpl.JPEGImageEncoder;
import com.sun.media.jai.codecimpl.TIFFCodec;
import com.sun.media.jai.codecimpl.TIFFImageEncoder;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.image.RenderedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.StringTokenizer;
import javax.imageio.ImageIO;
import javax.swing.JLabel;
import ocr.gui.MultiPagePanel;
import ocr.gui.OCRInterface;
import ocr.gui.leftPanel.WorkmodeTable;
import ocr.tif.ImageReaderDrawer;





















public class ConnectedComponent
  extends Rectangle
{
  private static final long serialVersionUID = 1L;
  private static final String BOX_PARAMETER = " -box";
  private static final String FILTER_SIZE_PARAMETER = " -filter";
  private static final String LIB_FOLDER = "lib";
  private static final String HELPER_FOLDER = "helper";
  private static final String GEDIHelperExe = "GEDIHelper.exe";
  private OCRInterface this_interface = OCRInterface.this_interface;
  

  private ArrayList<ConnectedComponent> connectedComponents;
  

  private String imageFilePath;
  

  private String GEDIHelperPath = File.separator + 
    "lib" + 
    File.separator + 
    "helper";
  


  private int filterSize;
  

  private String loadedDirPath;
  


  public ConnectedComponent(String loadedDirPath, int filterSize)
  {
    imageFilePath = ImageReaderDrawer.file_path;
    
    connectedComponents = new ArrayList();
    GEDIHelperPath = 
    

      (System.getProperty("user.dir") + GEDIHelperPath + File.separator + "GEDIHelper.exe");
    





    this.filterSize = filterSize;
    this.loadedDirPath = loadedDirPath;
  }
  
  public ConnectedComponent(int x, int y, int width, int height)
  {
    super(x, y, width, height);
    connectedComponents = new ArrayList();
  }
  
  public void loadConnectedComponents()
  {
    this_interface.setCursor(Cursor.getPredefinedCursor(3));
    ImageReaderDrawer.picture.setCursor(Cursor.getPredefinedCursor(3));
    
    if (this_interface.multiPageTiff) {
      String imageFilePath_page = createJPGforOpenedPage();
      runGEDIHelper(imageFilePath_page);
    }
    else
    {
      runGEDIHelper(imageFilePath);
    }
    this_interface.setCursor(Cursor.getPredefinedCursor(0));
    ImageReaderDrawer.picture.setCursor(Cursor.getPredefinedCursor(0));
  }
  




























  public ArrayList<ConnectedComponent> getAll_CC()
  {
    return connectedComponents;
  }
  







  private void runGEDIHelper(String imageFilePath)
  {
    String runStr = GEDIHelperPath + 
      " -box" + " \"" + 
      imageFilePath + "\"" + 
      " -filter" + " " + 
      filterSize;
    
    File imageFile = new File(imageFilePath);
    
    if (!imageFile.exists()) {
      return;
    }
    try {
      Process p = Runtime.getRuntime().exec(runStr);
      


      InputStream is = p.getInputStream();
      

      InputStream es = p.getErrorStream();
      

      BufferedReader reader = new BufferedReader(new InputStreamReader(is));
      BufferedReader errReader = new BufferedReader(new InputStreamReader(es));
      

      createConnectedComponents(reader);
      
      String line;
      while ((line = errReader.readLine()) != null) { String line;
        System.out.println(line);
      }
      

      int exitVal = p.waitFor();
      System.out.println("GEDIHelper exitValue: " + exitVal);
    }
    catch (IOException ex) {
      String strPrint = "Caught IOException trying to launch task. Please ensure your launch string is correct.";
      System.out.println(strPrint);
      ex.printStackTrace();
    }
    catch (InterruptedException ex) {
      String strPrint = "Caught InterruptedException: " + ex.getMessage().toString();
      System.out.println(strPrint);
      ex.printStackTrace();
    }
  }
  









  private void createConnectedComponents(BufferedReader reader)
  {
    connectedComponents.clear();
    try {
      String str;
      while (!(str = reader.readLine()).equals("-1")) { String str;
        ArrayList<Integer> data = new ArrayList();
        StringTokenizer tokenizer = new StringTokenizer(str);
        while (tokenizer.hasMoreTokens()) {
          data.add(Integer.valueOf(Integer.parseInt(tokenizer.nextToken())));
        }
        
        connectedComponents.add(new ConnectedComponent(
          ((Integer)data.get(1)).intValue(), 
          ((Integer)data.get(2)).intValue(), 
          ((Integer)data.get(3)).intValue() - ((Integer)data.get(1)).intValue(), 
          ((Integer)data.get(4)).intValue() - ((Integer)data.get(2)).intValue()));
      }
      
      reader.close();
    } catch (NumberFormatException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public String createTIFFforOpenedPage()
  {
    try
    {
      File multipageFile = new File(imageFilePath);
      SeekableStream ss = new FileSeekableStream(multipageFile);
      ImageDecoder decoder = ImageCodec.createImageDecoder("tiff", ss, null);
      
      int pageId = this_interface.getMultiPagePanel().getSelectedPage();
      
      System.out.println("imageFilePath: " + imageFilePath);
      System.out.println("pageId: " + pageId);
      
      RenderedImage page = decoder.decodeAsRenderedImage(pageId - 1);
      


      String imageFilePath_page = loadedDirPath + File.separator + 
        this_interface.ocrTable.selFileName + "_" + pageId + 
        "_dump.tif";
      
      File tempTiff = new File(imageFilePath_page);
      
      if (tempTiff.exists()) {
        return imageFilePath_page;
      }
      String format = "tiff";
      
      OutputStream stream = new FileOutputStream(imageFilePath_page);
      
      TIFFEncodeParam encodeParam = new TIFFEncodeParam();
      encodeParam.setCompression(4);
      
      TIFFImageEncoder encoder = (TIFFImageEncoder)TIFFCodec.createImageEncoder(format, stream, encodeParam);
      
      encoder.encode(page);
      
      stream.close();
      tempTiff.deleteOnExit();
      return imageFilePath_page;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
  
  public String createPNGforOpenedPage()
  {
    try
    {
      File multipageFile = new File(imageFilePath);
      SeekableStream ss = new FileSeekableStream(multipageFile);
      ImageDecoder decoder = ImageCodec.createImageDecoder("tiff", ss, null);
      
      int pageId = this_interface.getMultiPagePanel().getSelectedPage();
      
      System.out.println("imageFilePath: " + imageFilePath);
      System.out.println("pageId: " + pageId);
      
      RenderedImage page = decoder.decodeAsRenderedImage(pageId - 1);
      


      String imageFilePath_page = loadedDirPath + File.separator + 
        this_interface.ocrTable.selFileName + "_" + pageId + 
        "_dump.png";
      
      File tempPNG = new File(imageFilePath_page);
      
      if (tempPNG.exists()) {
        return imageFilePath_page;
      }
      OutputStream stream = new FileOutputStream(imageFilePath_page);
      


      ImageIO.write(page, "png", tempPNG);
      
      stream.close();
      tempPNG.deleteOnExit();
      return imageFilePath_page;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
  
  public String createJPGforOpenedPage()
  {
    try
    {
      File multipageFile = new File(imageFilePath);
      SeekableStream ss = new FileSeekableStream(multipageFile);
      ImageDecoder decoder = ImageCodec.createImageDecoder("tiff", ss, null);
      
      int pageId = this_interface.currPageID;
      
      RenderedImage page = decoder.decodeAsRenderedImage(pageId - 1);
      


      String imageFilePath_page = loadedDirPath + File.separator + 
        this_interface.ocrTable.selFileName + "_" + pageId + 
        "_dump.jpg";
      
      File tempJPG = new File(imageFilePath_page);
      tempJPG.deleteOnExit();
      
      if (tempJPG.exists()) {
        return imageFilePath_page;
      }
      String format = "jpeg";
      
      OutputStream stream = new FileOutputStream(imageFilePath_page);
      
      JPEGEncodeParam encodeParam = new JPEGEncodeParam();
      encodeParam.setQuality(1.0F);
      
      JPEGImageEncoder encoder = (JPEGImageEncoder)
        JPEGCodec.createImageEncoder(format, stream, encodeParam);
      
      encoder.encode(page);
      
      stream.close();
      
      return imageFilePath_page;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
