package filesystem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;










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
        return fileName.toLowerCase().endsWith(extension);
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
    if (isFile()) {
      return super.delete();
    }
    String[] filesAndDirs = list();
    
    for (int i = 0; (filesAndDirs != null) && (i < filesAndDirs.length); i++)
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
      " /a /d /e /c /q /h /r /k /y");
    
    try
    {
      p.waitFor();
    }
    catch (InterruptedException e)
    {
      e.printStackTrace();
    }
  }
  




  public static File removeExtension(File file)
  {
    String filename = file.getName();
    if (filename.indexOf('.') != -1) {
      filename = filename.substring(0, filename.lastIndexOf('.'));
    }
    return new File(file.getParent(), filename);
  }
  

  public static int getPageNumber(String str)
  {
    FileExtended temp = new FileExtended(str);
    return temp.getPageNumber();
  }
  





  private String getNameWithoutExtensionAndSubPage()
  {
    String name = getNameWithoutExtension();
    
    int index = name.lastIndexOf("-");
    if ((index != -1) && (index > 1)) {
      return name.substring(0, index);
    }
    return name;
  }
  








  public int getPageNumber()
  {
    String string = getNameWithoutExtensionAndSubPage();
    
    int i = 0;int j = string.length();
    for (i = 0; i < string.length(); i++) {
      if (Character.isDigit(string.charAt(i)))
        break;
    }
    for (j = string.length() - 1; j >= i; j--) {
      if (Character.isDigit(string.charAt(j)))
        break;
    }
    String pageNumber = "-1";
    
    if ((i < string.length()) && (i <= j + 1)) {
      if (j + 1 == i) { j = i;
      }
      pageNumber = string.substring(i, j + 1);
    }
    return Integer.parseInt(pageNumber);
  }
  

  public String getPageNumberPrefix()
  {
    int num = getPageNumber();
    String string = getNameWithoutExtensionAndSubPage();
    
    int i = string.indexOf(Integer.toString(num));
    
    if (i != -1) { return string.substring(0, i);
    }
    return string;
  }
  




  public String getHomeDir()
  {
    if (!isAbsolute()) { return null;
    }
    String temp = toString();
    return temp.substring(0, temp.indexOf(':'));
  }
  






  public static ArrayList<File> listAllFiles(File dicFile)
  {
    ArrayList<File> list = new ArrayList();
    
    listAllFilesHelper(dicFile, list);
    
    return list;
  }
  
  private static void listAllFilesHelper(File dir, List<File> list)
  {
    if (dir.isDirectory()) {
      String[] children = dir.list();
      for (int i = 0; i < children.length; i++) {
        listAllFilesHelper(new File(dir, children[i]), list);
      }
    } else {
      list.add(dir);
    }
  }
  






  public static Vector<String> getFileLines(File fileName)
  {
    try
    {
      InputStream is = new FileInputStream(fileName);
      InputStreamReader inputStreamReader = new InputStreamReader(is);
      BufferedReader bufferReader = new BufferedReader(inputStreamReader);
      
      String line = bufferReader.readLine().trim();
      Vector<String> vLines = new Vector();
      

      while (line != null) {
        line = line.trim();
        try {
          vLines.add(line);
        }
        catch (NumberFormatException localNumberFormatException) {}
        line = bufferReader.readLine();
      }
      is.close();
      bufferReader.close();
      
      return vLines;
    }
    catch (FileNotFoundException localFileNotFoundException) {}catch (IOException localIOException) {}
    



    return null;
  }
}
