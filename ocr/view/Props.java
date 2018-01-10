package ocr.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Properties;




public class Props
  extends Properties
{
  File propsFile_ = null;
  


  public Props(File propsFile)
  {
    propsFile_ = propsFile;
    

    try
    {
      FileInputStream in = new FileInputStream(propsFile);
      load(in);

    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();
    }
  }
  
  public void save()
  {
    try {
      FileOutputStream fs = new FileOutputStream(propsFile_);
      store(fs, null);
      fs.close();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public void load(InputStream in)
  {
    try {
      super.load(in);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  
  public static void main(String[] args)
  {
    Properties props = new Props(new File("abc"));
    System.out.println(props.getProperty("abc"));
  }
}
