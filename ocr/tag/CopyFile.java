package ocr.tag;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;











public class CopyFile
{
  public CopyFile() {}
  
  public static void copyfile(String fromFile, String toFile)
    throws IOException
  {
    try
    {
      int character = 0;
      FileInputStream instream = new FileInputStream(fromFile);
      FileOutputStream outstream = new FileOutputStream(toFile);
      
      while ((character = instream.read()) != -1) { outstream.write(character);
      }
      instream.close();
      outstream.close();
    }
    catch (IOException e)
    {
      e.printStackTrace();
      System.out.println("IO Error:" + e.getMessage());
    }
  }
}
