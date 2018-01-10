package other;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;







public class ReadmeFileGenerator
{
  public ReadmeFileGenerator() {}
  
  public static void main(String[] args)
  {
    generate(new File("C:\\BRIDGE\\Tides"), true);
  }
  



  public static void generate(File dir, boolean browseSubDirs)
  {
    File readmeFile = new File(dir, "Readme.txt");
    
    if (!dir.isDirectory()) return;
    if (!readmeFile.exists()) { return;
    }
    File[] files = dir.listFiles();
    
    if (readmeFile.length() == 0L)
    {
      try
      {
        BufferedWriter bw = new BufferedWriter(new FileWriter(readmeFile));
        
        bw.write("Readme File. Contains info about files in this directory");
        bw.newLine();
        bw.newLine();
        bw.newLine();
        

        for (int i = 0; i < files.length; i++) {
          if ((!files[i].getName().equals("Readme.txt")) && (files[i].getName().indexOf('~') == -1)) {
            bw.write(files[i].getName());
            
            if (files[i].isDirectory()) {
              bw.write("(D):");
            } else
              bw.write("(F):");
            bw.newLine();
            bw.newLine();
          }
        }
        bw.close();
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }
    if (browseSubDirs) {
      for (int j = 0; j < files.length; j++) {
        generate(files[j], browseSubDirs);
      }
    }
  }
}
