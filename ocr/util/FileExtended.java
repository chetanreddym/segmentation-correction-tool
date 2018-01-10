package ocr.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.channels.FileChannel;











public class FileExtended
  extends File
{
  public FileExtended(String pathname)
  {
    super(pathname);
  }
  
  public FileExtended(File parent, String fileName)
  {
    super(parent, fileName);
  }
  
  public FileExtended(String parent, String fileName)
  {
    super(parent, fileName);
  }
  


  public FileExtended(FileExtended parent, String file)
  {
    super(parent, file);
  }
  


  public File[] listFiles(final String extension)
  {
    FileFilter fileFilter = new FileFilter() {
      public boolean accept(File file) {
        String fileName = file.getName();
        return fileName.endsWith(extension);
      }
    };
    return listFiles(fileFilter);
  }
  







  public String getNameWithoutExtension()
  {
    String name = getName();
    int index = name.lastIndexOf(".");
    if ((index != -1) && (index > 1)) {
      return name.substring(0, index);
    }
    return name;
  }
  






  public String getNameWithoutExtensionAndSubPage()
  {
    String name = getNameWithoutExtension();
    
    int index = name.lastIndexOf("-");
    if ((index != -1) && (index > 1)) {
      return name.substring(0, index);
    }
    return name;
  }
  






  public File[] listFiles()
  {
    super.listFiles(new FilenameFilter()
    {
      public boolean accept(File dir, String name)
      {
        if (name.equals("CVS")) { return false;
        }
        return true;
      }
    });
  }
  





  public boolean delete()
  {
    if (isFile())
      return super.delete();
    System.out.println(getAbsolutePath());
    String[] filesAndDirs = list();
    
    for (int i = 0; i < filesAndDirs.length; i++)
    {

      FileExtended f = new FileExtended(this, filesAndDirs[i]);
      
      f.delete();
    }
    
    return super.delete();
  }
  











  public static void copy(File destDir, File srcFile)
    throws FileNotFoundException, IOException
  {
    if (!destDir.isAbsolute()) {
      throw new IOException("Destination dir. " + destDir + " not absolute.");
    }
    if (!destDir.exists()) {
      throw new IOException("Destination Dir. " + destDir + " does not exist");
    }
    if (srcFile.isDirectory())
    {
      copyDir(destDir, srcFile);
      return;
    }
    FileChannel srcChannel = new FileInputStream(srcFile).getChannel();
    FileChannel dstChannel = new FileOutputStream(destDir + "/" + srcFile.getName()).getChannel();
    

    dstChannel.transferFrom(srcChannel, 0L, srcChannel.size());
    

    srcChannel.close();
    dstChannel.close();
  }
  











  private static void copyDir(File destDir, File srcDir)
    throws IOException
  {
    File newFileUnderDest = new File(destDir, srcDir.getName());
    
    newFileUnderDest.mkdir();
    
    Process p = Runtime.getRuntime().exec("xcopy " + srcDir.getAbsolutePath() + "\\* " + newFileUnderDest.getAbsolutePath() + 
      " /a /d /e /c /q /h /r /k");
    
    try
    {
      p.waitFor();
    }
    catch (InterruptedException e)
    {
      e.printStackTrace();
    }
  }
  
  public static void main(String[] args)
  {
    try {
      copy(new File("C:" + File.separatorChar + "TEMP" + File.separatorChar + "temp3"), new File("C:" + File.separatorChar + "TEMP" + File.separatorChar + "temp2"));
    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
}
