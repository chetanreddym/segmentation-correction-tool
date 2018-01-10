package ocr.gui.fileDrop;

import java.io.IOException;

public class FileDropListener implements FileDropListenerInterface {
  public FileDropListener() {}
  
  public void filesDropped(java.io.File[] files) { for (int i = 0; i < files.length; i++) {
      try {
        System.out.println(files[i].getCanonicalPath() + "\n");
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
