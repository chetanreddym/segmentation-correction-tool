package ocr.gui.fileDrop;

import java.io.File;

public abstract interface FileDropListenerInterface
{
  public abstract void filesDropped(File[] paramArrayOfFile);
}
