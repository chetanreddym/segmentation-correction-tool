package gttool.io;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;
import ocr.gui.OCRAboutBox;

public class XmlConstant
{
  private static OCRAboutBox about = new OCRAboutBox();
  
  public static final String GEDI_INFORMATION = "GEDI was developed at Language and Media Processing Laboratory, University of Maryland.";
  
  public static final String ZONE_TYPE_TAG_KEY = "gedi_type";
  
  static final char[] NEW_LINE = "\n".toCharArray();
  
  static final String TAB = "\t";
  
  static final char[] SPACE = " ".toCharArray();
  

  static final String TYPE_CDATA = "CDATA";
  

  public static final String NAMESPACE = "http://lamp.cfar.umd.edu/media/projects/GEDI/";
  

  public static final String ELEMENT_GEDI = "GEDI";
  

  public static final String ELEMENT_CONFIGURATION = "configuration";
  
  public static final String ELEMENT_TYPE_SETTING_ENTRY = "type_setting_entry";
  
  public static final String ELEMENT_TYPE_SETTINGS = "type_settings";
  
  public static final String ELEMENT_ATTRIBUTE_SETTINGS = "attribute_settings";
  
  public static final String ELEMENT_TYPE_ATTRIBUTE = "type_attribute";
  
  public static final String ELEMENT_ATTRIBUTE_HOTKEY = "attribute_hotkey";
  
  public static final String ELEMENT_ATTRIBUTE = "attribute";
  
  public static final String ELEMENT_DL_DOCUMENT = "DL_DOCUMENT";
  
  public static final String ELEMENT_DL_PAGE = "DL_PAGE";
  
  public static final String ELEMENT_DL_ZONE = "DL_ZONE";
  
  public static final String ATTRIBUTE_STATUS = "status";
  
  public static final String ATTRIBUTE_ATTRIBUTE = "attribute";
  
  public static final String ATTRIBUTE_BOUNDS_OF = "bounds_of";
  
  public static final String ATTRIBUTE_DEFAULT = "default";
  
  public static final String ATTRIBUTE_XML_VERSION = "version";
  
  public static final String ATTRIBUTE_GEDI_VERSION = "GEDI_version";
  
  public static final String ATTRIBUTE_GEDI_DATE = "GEDI_date";
  
  public static final String ATTRIBUTE_NAME = "name";
  
  public static final String ATTRIBUTE_VALUE = "value";
  
  public static final String ATTRIBUTE_COLOR = "color";
  
  public static final String ATTRIBUTE_KEY = "key";
  
  public static final String ATTRIBUTE_HOTKEY = "hotkey";
  
  public static final String ATTRIBUTE_COUNT = "count";
  
  public static final String ATTRIBUTE_VISIBLE = "visible";
  
  public static final String ATTRIBUTE_VISIBLE_VALUE_ONLY_SELECTED = "onlySelected";
  
  public static final String ATTRIBUTE_DOCUMENT_ID = "docID";
  
  public static final String ATTRIBUTE_SRC = "src";
  
  public static final String ATTRIBUTE_PAGE_ID = "pageID";
  
  public static final String ATTRIBUTE_ZONE_ID = "id";
  
  public static final String ATTRIBUTE_NEXT_ZONE_ID = "nextZoneID";
  
  public static final String ATTRIBUTE_COL = "col";
  
  public static final String ATTRIBUTE_ROW = "row";
  
  public static final String ATTRIBUTE_WIDTH = "width";
  
  public static final String ATTRIBUTE_HEIGHT = "height";
  
  public static final String ATTRIBUTE_TYPE = "gedi_type";
  
  public static final String ATTRIBUTE_GEDI_TYPE = "gedi_type";
  
  public static final String ATTRIBUTE_POSSIBLE_VALUES = "possible_values";
  
  public static final String ATTRIBUTE_PAGE_ORIENTATION = "GEDI_orientation";
  
  public static final String ATTRIBUTE_ORIENTATION = "orientation";
  
  public static final String ATTRIBUTE_ORIENTATION_DEGREES = "orientationD";
  
  public static final String ATTRIBUTE_POLYGON = "polygon";
  
  public static final String VALUE_DEFAULT = "default";
  
  public static final String VALUE_XML_VERSION = "1.0";
  
  public static final String VALUE_GEDI_VERSION = about.getVersionNumber();
  
  public static final String VALUE_GEDI_DATE = about.getGEDIDate();
  

  public static final String VALUE_DEFAULT_ZONE_TYPE = "DL_ZONE";
  

  public static final String DL_TEXTLINEGT = "DL_TEXTLINEGT";
  

  public static final String ELEMENT_USER_NAME = "USER";
  

  public static final String ATTRIBUTE_LINEID = "lineID";
  

  public static final String ATTRIBUTE_SRCLINEID = "srclineid";
  
  public static final String ATTRIBUTE_RLEIMAGE = "RLEIMAGE";
  
  public static final String ATTRIBUTE_USER_NAME = "name";
  
  public static final String ATTRIBUTE_USER_NAME_DATE = "date";
  
  public static final String ATTRIBUTE_DATE_FORMAT = "dateFormat";
  
  public static final String DATE_FORMAT = "mm/dd/yyyy hh:mm";
  
  public static final String ATTRIBUTE_CONTENTS = "contents";
  
  public static final String ATTRIBUTE_OFFSETS = "offsets";
  
  public static final String ATTRIBUTE_SEGMENTATION = "segmentation";
  
  public static final String CHARACTER_SEGMENTATION = "character";
  
  public static final String WORD_SEGMENTATION = "word";
  
  public static final String LINE_SEGMENTATION = "line";
  
  public static final String UTF8_ENCODING = "UTF-8";
  
  public static final String UTF16_ENCODING = "UTF-16";
  
  public static final String ATTRIBUTE_GROUP_OF = "groupOf";
  
  public static final String ATTRIBUTE_ELEMENTS = "elements";
  
  public static final String ATTRIBUTE_XMLMerged = "XMLMerged";
  
  public static final String ATTRIBUTE_VALUE_ANY = "ANY";
  
  public static final String ATTRIBUTE_NEWCELL = "newcell";
  
  public static final String ATTRIBUTE_CELLID = "cellID";
  
  public static final String EMPTY_SET = "∅";
  
  public static final String DEGREE_SYMBOL = "°";
  
  public static final Vector<String> Reserved_Attributes = new Vector(Arrays.asList(new String[] {
    "elements", 
    
    "orientation", 
    "orientationD", 
    "gedi_type", 
    "id", 
    "nextZoneID", 
    "polygon" }));
  

  public static final String ATTRIBUTE_FORM = "form";
  
  public static final Vector<String> ARABIC_LIGATURES = new Vector(Arrays.asList(new String[] {
    "0644 0622", "0644 0623", "0644 0625", "0644 0627" }));
  

  public static final Vector<String> ARABIC_ALIF_LAM = new Vector(Arrays.asList(new String[] {
    "0644 0622 0627", "0644 0623 0627", "0644 0625 0627", "0644 0627 0627", "0644 0627 0644" }));
  
  public static final HashMap<String, String> ARABIC_LIGATURES_MAP = new HashMap()
  {
    private static final long serialVersionUID = 1L;
  };
  
  public XmlConstant() {}
}
