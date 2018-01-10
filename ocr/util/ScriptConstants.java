package ocr.util;

import java.io.File;







public class ScriptConstants
{
  public static final String SCRIPT_DIR = File.separator + 
    "lib" + 
    File.separator + 
    "Scripts";
  public static final String SCRIPT_PROP_EXTENSION = ".properties";
  public static final String SCRIPT_LOG_EXTENSION = ".log";
  public static final String CONFIG_SCRIPT = "ScriptConfig.properties";
  public static final String NAME = "NAME";
  public static final String RUN_STR = "COMMAND";
  public static final String SHOW_DIALOG = "SHOW_DIALOG";
  public static final String APPEND_LOG = "APPEND_LOG";
  public static final String IMG = "%img";
  public static final String IMG_EXT = "%img-ext";
  public static final String IMG_ROOT = "%img-root";
  public static final String IMG_DIR = "%img-dir";
  public static final String IMG_DIR_NAME = "%img-dir-name";
  public static final String XML = "%xml";
  public static final String XML_EXT = "%xml-ext";
  public static final String XML_ROOT = "%xml-root";
  public static final String XML_DIR = "%xml-dir";
  public static final String XML_DIR_NAME = "%xml-dir-name";
  public static final String GEDI_DIR = "%gedi-dir";
  public static final String INFO = "Configuring GEDI for Scripts:\n- Specify a separate \"<script_name>.properties\" file for each script \n- Place all \"*.properties\" files into \"<GEDI_ROOT> \\ lib \\ Scripts\" directory \n\nRunning a script (or executable program): \n- Select an image or set of images in the GEDI interface file panel \n- Select the script to run on the \"Scripts\" menu \n- If you selected more than one image, the Script will be run on each image sequentially\n\n\"GEDI \\ lib \\ Scripts \\ ScriptConfig.properties\" explains required syntax for Script Properties file\nand contains examples. Do you want to open it now?\n";
  
  public ScriptConstants() {}
}
