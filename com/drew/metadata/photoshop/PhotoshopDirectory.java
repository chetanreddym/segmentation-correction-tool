package com.drew.metadata.photoshop;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Directory;
import java.util.HashMap;

public class PhotoshopDirectory
  extends Directory
{
  public static final int TAG_PHOTOSHOP_CHANNELS_ROWS_COLUMNS_DEPTH_MODE = 1000;
  public static final int TAG_PHOTOSHOP_MAC_PRINT_INFO = 1001;
  public static final int TAG_PHOTOSHOP_XML = 1002;
  public static final int TAG_PHOTOSHOP_INDEXED_COLOR_TABLE = 1003;
  public static final int TAG_PHOTOSHOP_RESOLUTION_INFO = 1005;
  public static final int TAG_PHOTOSHOP_ALPHA_CHANNELS = 1006;
  public static final int TAG_PHOTOSHOP_DISPLAY_INFO = 1007;
  public static final int TAG_PHOTOSHOP_CAPTION = 1008;
  public static final int TAG_PHOTOSHOP_BORDER_INFORMATION = 1009;
  public static final int TAG_PHOTOSHOP_BACKGROUND_COLOR = 1010;
  public static final int TAG_PHOTOSHOP_PRINT_FLAGS = 1011;
  public static final int TAG_PHOTOSHOP_GRAYSCALE_AND_MULTICHANNEL_HALFTONING_INFORMATION = 1012;
  public static final int TAG_PHOTOSHOP_COLOR_HALFTONING_INFORMATION = 1013;
  public static final int TAG_PHOTOSHOP_DUOTONE_HALFTONING_INFORMATION = 1014;
  public static final int TAG_PHOTOSHOP_GRAYSCALE_AND_MULTICHANNEL_TRANSFER_FUNCTION = 1015;
  public static final int TAG_PHOTOSHOP_COLOR_TRANSFER_FUNCTIONS = 1016;
  public static final int TAG_PHOTOSHOP_DUOTONE_TRANSFER_FUNCTIONS = 1017;
  public static final int TAG_PHOTOSHOP_DUOTONE_IMAGE_INFORMATION = 1018;
  public static final int TAG_PHOTOSHOP_EFFECTIVE_BLACK_AND_WHITE_VALUES = 1019;
  public static final int TAG_PHOTOSHOP_EPS_OPTIONS = 1021;
  public static final int TAG_PHOTOSHOP_QUICK_MASK_INFORMATION = 1022;
  public static final int TAG_PHOTOSHOP_LAYER_STATE_INFORMATION = 1024;
  public static final int TAG_PHOTOSHOP_LAYERS_GROUP_INFORMATION = 1026;
  public static final int TAG_PHOTOSHOP_IPTC = 1028;
  public static final int TAG_PHOTOSHOP_IMAGE_MODE_FOR_RAW_FORMAT_FILES = 1029;
  public static final int TAG_PHOTOSHOP_JPEG_QUALITY = 1030;
  public static final int TAG_PHOTOSHOP_GRID_AND_GUIDES_INFORMATION = 1032;
  public static final int TAG_PHOTOSHOP_THUMBNAIL_OLD = 1033;
  public static final int TAG_PHOTOSHOP_COPYRIGHT = 1034;
  public static final int TAG_PHOTOSHOP_URL = 1035;
  public static final int TAG_PHOTOSHOP_THUMBNAIL = 1036;
  public static final int TAG_PHOTOSHOP_GLOBAL_ANGLE = 1037;
  public static final int TAG_PHOTOSHOP_ICC_UNTAGGED_PROFILE = 1041;
  public static final int TAG_PHOTOSHOP_SEED_NUMBER = 1044;
  public static final int TAG_PHOTOSHOP_GLOBAL_ALTITUDE = 1049;
  public static final int TAG_PHOTOSHOP_SLICES = 1050;
  public static final int TAG_PHOTOSHOP_URL_LIST = 1054;
  public static final int TAG_PHOTOSHOP_VERSION = 1057;
  public static final int TAG_PHOTOSHOP_CAPTION_DIGEST = 1061;
  public static final int TAG_PHOTOSHOP_PRINT_SCALE = 1062;
  public static final int TAG_PHOTOSHOP_PIXEL_ASPECT_RATIO = 1064;
  public static final int TAG_PHOTOSHOP_PRINT_INFO = 1071;
  public static final int TAG_PHOTOSHOP_PRINT_FLAGS_INFO = 10000;
  @NotNull
  protected static final HashMap<Integer, String> _tagNameMap = new HashMap();
  
  public PhotoshopDirectory()
  {
    setDescriptor(new PhotoshopDescriptor(this));
  }
  
  @NotNull
  public String getName()
  {
    return "Photoshop";
  }
  
  @NotNull
  protected HashMap<Integer, String> getTagNameMap()
  {
    return _tagNameMap;
  }
  
  @Nullable
  public byte[] getThumbnailBytes()
  {
    byte[] arrayOfByte1 = getByteArray(1036);
    if (arrayOfByte1 == null) {
      arrayOfByte1 = getByteArray(1033);
    }
    if (arrayOfByte1 == null) {
      return null;
    }
    int i = arrayOfByte1.length - 28;
    byte[] arrayOfByte2 = new byte[i];
    System.arraycopy(arrayOfByte1, 28, arrayOfByte2, 0, i);
    return arrayOfByte2;
  }
  
  static
  {
    _tagNameMap.put(Integer.valueOf(1000), "Channels, Rows, Columns, Depth, Mode");
    _tagNameMap.put(Integer.valueOf(1001), "Mac Print Info");
    _tagNameMap.put(Integer.valueOf(1002), "XML Data");
    _tagNameMap.put(Integer.valueOf(1003), "Indexed Color Table");
    _tagNameMap.put(Integer.valueOf(1005), "Resolution Info");
    _tagNameMap.put(Integer.valueOf(1006), "Alpha Channels");
    _tagNameMap.put(Integer.valueOf(1007), "Display Info");
    _tagNameMap.put(Integer.valueOf(1008), "Caption");
    _tagNameMap.put(Integer.valueOf(1009), "Border Information");
    _tagNameMap.put(Integer.valueOf(1010), "Background Color");
    _tagNameMap.put(Integer.valueOf(1011), "Print Flags");
    _tagNameMap.put(Integer.valueOf(1012), "Grayscale and Multichannel Halftoning Information");
    _tagNameMap.put(Integer.valueOf(1013), "Color Halftoning Information");
    _tagNameMap.put(Integer.valueOf(1014), "Duotone Halftoning Information");
    _tagNameMap.put(Integer.valueOf(1015), "Grayscale and Multichannel Transfer Function");
    _tagNameMap.put(Integer.valueOf(1016), "Color Transfer Functions");
    _tagNameMap.put(Integer.valueOf(1017), "Duotone Transfer Functions");
    _tagNameMap.put(Integer.valueOf(1018), "Duotone Image Information");
    _tagNameMap.put(Integer.valueOf(1019), "Effective Black and White Values");
    _tagNameMap.put(Integer.valueOf(1021), "EPS Options");
    _tagNameMap.put(Integer.valueOf(1022), "Quick Mask Information");
    _tagNameMap.put(Integer.valueOf(1024), "Layer State Information");
    _tagNameMap.put(Integer.valueOf(1026), "Layers Group Information");
    _tagNameMap.put(Integer.valueOf(1028), "IPTC-NAA Record");
    _tagNameMap.put(Integer.valueOf(1029), "Image Mode for Raw Format Files");
    _tagNameMap.put(Integer.valueOf(1030), "JPEG Quality");
    _tagNameMap.put(Integer.valueOf(1032), "Grid and Guides Information");
    _tagNameMap.put(Integer.valueOf(1033), "Photoshop 4.0 Thumbnail");
    _tagNameMap.put(Integer.valueOf(1034), "Copyright Flag");
    _tagNameMap.put(Integer.valueOf(1035), "URL");
    _tagNameMap.put(Integer.valueOf(1036), "Thumbnail Data");
    _tagNameMap.put(Integer.valueOf(1037), "Global Angle");
    _tagNameMap.put(Integer.valueOf(1041), "ICC Untagged Profile");
    _tagNameMap.put(Integer.valueOf(1044), "Seed Number");
    _tagNameMap.put(Integer.valueOf(1049), "Global Altitude");
    _tagNameMap.put(Integer.valueOf(1050), "Slices");
    _tagNameMap.put(Integer.valueOf(1054), "URL List");
    _tagNameMap.put(Integer.valueOf(1057), "Version Info");
    _tagNameMap.put(Integer.valueOf(1061), "Caption Digest");
    _tagNameMap.put(Integer.valueOf(1062), "Print Scale");
    _tagNameMap.put(Integer.valueOf(1064), "Pixel Aspect Ratio");
    _tagNameMap.put(Integer.valueOf(1071), "Print Info");
    _tagNameMap.put(Integer.valueOf(10000), "Print Flags Information");
  }
}
