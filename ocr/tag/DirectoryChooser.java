package ocr.tag;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;




public class DirectoryChooser
  extends JFileChooser
{
  class TifFileFilter
    extends FileFilter
  {
    TifFileFilter() {}
    
    public boolean accept(File f)
    {
      String name = f.getName().toLowerCase();
      if ((name.endsWith(".tif")) || (name.endsWith(".TIF")) || (name.endsWith(".tiff")) || (name.endsWith(".TIFF")) || 
        (f.isDirectory())) {
        return true;
      }
      return false;
    }
    
    public String getDescription() { return "TIF Files (*.tif; *.tiff; *.TIF; *.TIFF)"; }
  }
  
  class JpgFileFilter extends FileFilter {
    JpgFileFilter() {}
    
    public boolean accept(File f) { String name = f.getName().toLowerCase();
      if ((name.endsWith(".jpg")) || (name.endsWith(".JPG")) || (name.endsWith(".jpeg")) || (name.endsWith(".JPEG")) || 
        (f.isDirectory())) {
        return true;
      }
      return false;
    }
    
    public String getDescription() { return "JPEG Files (*.jpg; *.jpeg; *.JPG; *.JPEG)"; }
  }
  
  class GifFileFilter extends FileFilter {
    GifFileFilter() {}
    
    public boolean accept(File f) { String name = f.getName().toLowerCase();
      if ((name.endsWith(".gif")) || (name.endsWith(".GIF")) || 
        (f.isDirectory())) {
        return true;
      }
      return false;
    }
    
    public String getDescription() { return "GIF Files (*.gif; *.GIF)"; }
  }
  
  class BmpFileFilter extends FileFilter {
    BmpFileFilter() {}
    
    public boolean accept(File f) { String name = f.getName().toLowerCase();
      if ((name.endsWith(".bmp")) || (name.endsWith(".BMP")) || 
        (f.isDirectory())) {
        return true;
      }
      return false;
    }
    
    public String getDescription() { return "BMP Files (*.bmp; *.BMP)"; }
  }
  
  class XmlFileFilter extends FileFilter {
    XmlFileFilter() {}
    
    public boolean accept(File f) { String name = f.getName().toLowerCase();
      if (((!name.endsWith(".xml")) && (!name.endsWith(".XML"))) || ((!name.equalsIgnoreCase("GEDIConfig.XML")) || 
        (f.isDirectory()))) {
        return true;
      }
      return false;
    }
    
    public String getDescription() { return "XML Files (*.xml; *.XML)"; }
  }
  
  public DirectoryChooser(File startDir)
  {
    super(startDir);
    setFileSelectionMode(2);
    addChoosableFileFilter(new TifFileFilter());
    addChoosableFileFilter(new JpgFileFilter());
    addChoosableFileFilter(new GifFileFilter());
    addChoosableFileFilter(new BmpFileFilter());
    




    setAcceptAllFileFilterUsed(true);
  }
  
  public File getSelectedFile() {
    File file = super.getSelectedFile();
    if (file == null) {
      return file;
    }
    String dirName = super.getSelectedFile().getAbsolutePath();
    String fileSep = System.getProperty("file.separator", "\\");
    if (dirName.endsWith(fileSep + ".")) {
      dirName = dirName.substring(0, dirName.length() - 2);
      file = new File(dirName);
    }
    return file;
  }
}
