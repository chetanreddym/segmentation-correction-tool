package other;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;















public class ZipFileCreator
{
  public ZipFileCreator() {}
  
  public static void main(String[] args) {}
  
  public static void createZipFile(ArrayList<File> filenames, File outFilename)
  {
    byte[] buf = new byte['Ѐ'];
    

    try
    {
      ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outFilename));
      

      for (int i = 0; i < filenames.size(); i++) {
        FileInputStream in = new FileInputStream((File)filenames.get(i));
        

        out.putNextEntry(new ZipEntry(((File)filenames.get(i)).toString()));
        
        int len;
        
        while ((len = in.read(buf)) > 0) { int len;
          out.write(buf, 0, len);
        }
        

        out.closeEntry();
        in.close();
      }
      

      out.close();
    }
    catch (IOException localIOException) {}
  }
}
