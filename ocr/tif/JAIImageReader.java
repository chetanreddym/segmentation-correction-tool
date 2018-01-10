package ocr.tif;

import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.SeekableStream;
import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
import java.io.PrintStream;
import javax.media.jai.JAI;
import javax.media.jai.LookupTableJAI;
import javax.media.jai.PlanarImage;
import ocr.gui.OCRInterface;

public class JAIImageReader
{
  public JAIImageReader() {}
  
  public static PlanarImage readImage(String filename, boolean convertToRGB)
  {
    String requestedImageName = filename;
    
    String currentImageName = ImageReaderDrawer.file_path;
    



    PlanarImage currentImage = null;
    

    if ((OCRInterface.currentHWObj != null) && 
      (!this_interfacemultiPageTiff)) {
      currentImage = OCRInterface.currentHWObj.getOriginalImage();
      if ((currentImage != null) && 
        (currentImageName.equalsIgnoreCase(requestedImageName))) {
        return currentImage;
      }
    }
    OCRInterface.this_interface.setCurrentRotateDegrees(0);
    

    PlanarImage image = null;
    













    try
    {
      if ((filename.contains(".tif")) || (filename.contains(".TIF"))) {
        RenderedImage[] images = readMultiPageTiff(filename);
        if (images == null) {
          return null;
        }
        





        int numMultiTiffPages = 
          ImageReaderDrawer.numPagesInMultiTiff(filename);
        RenderedImage im;
        if (numMultiTiffPages == 1) {
          RenderedImage im = images[0];
          this_interfacecurrPageID = 0;
        }
        else
        {
          int selectedPageID = this_interfacecurrPageID;
          System.out.println("selectedPageID: " + selectedPageID);
          if (selectedPageID == 0)
            selectedPageID = 1;
          im = images[(selectedPageID - 1)];
          this_interfacecurrPageID = selectedPageID;
        }
        
        image = PlanarImage.wrapRenderedImage(im);
      } else {
        image = PlanarImage.wrapRenderedImage(JAI.create("fileload", 
          filename));
        image.getWidth();



      }
      



    }
    catch (Exception e)
    {



      e.printStackTrace();
      System.out
        .println("Invalid file format. Unsupported image format.\nError: " + 
        e.getMessage());
      return null;
    }
    






    if ((image != null) && (convertToRGB) && 
      ((image.getColorModel() instanceof IndexColorModel)))
    {
      IndexColorModel icm = (IndexColorModel)image.getColorModel();
      

      int mapSize = icm.getMapSize();
      

      byte[][] lutData = new byte[3][mapSize];
      

      icm.getReds(lutData[0]);
      icm.getGreens(lutData[1]);
      icm.getBlues(lutData[2]);
      

      LookupTableJAI lut = new LookupTableJAI(lutData);
      

      PlanarImage image1 = JAI.create("lookup", image, lut);
      image.dispose();
      image = image1;
    }
    return image;
  }
  
  public static RenderedImage[] readMultiPageTiff(String filename)
  {
    try
    {
      SeekableStream stream = new FileSeekableStream(filename);
      String[] names = ImageCodec.getDecoderNames(stream);
      java.io.File file = new java.io.File(filename);
      SeekableStream ss = new FileSeekableStream(file);
      ImageDecoder decoder = ImageCodec.createImageDecoder(names[0], ss, 
        null);
      int numPages = decoder.getNumPages();
      RenderedImage[] image = new RenderedImage[numPages];
      
      for (int i = 0; i < decoder.getNumPages(); i++) {
        image[i] = decoder.decodeAsRenderedImage(i);
      }
      return image;
    }
    catch (Exception e) {
      e.printStackTrace();
      System.out
        .println("Invalid file format. Unsupported TIFF image format.\nError: " + 
        e.getMessage());
    }
    
    return null;
  }
  
  public static void test(Runtime r, String t) {}
}
