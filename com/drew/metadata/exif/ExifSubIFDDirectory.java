package com.drew.metadata.exif;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import java.util.HashMap;

public class ExifSubIFDDirectory
  extends Directory
{
  public static final int TAG_APERTURE = 37378;
  public static final int TAG_BITS_PER_SAMPLE = 258;
  public static final int TAG_PHOTOMETRIC_INTERPRETATION = 262;
  public static final int TAG_THRESHOLDING = 263;
  public static final int TAG_FILL_ORDER = 266;
  public static final int TAG_DOCUMENT_NAME = 269;
  public static final int TAG_STRIP_OFFSETS = 273;
  public static final int TAG_SAMPLES_PER_PIXEL = 277;
  public static final int TAG_ROWS_PER_STRIP = 278;
  public static final int TAG_STRIP_BYTE_COUNTS = 279;
  public static final int TAG_MIN_SAMPLE_VALUE = 280;
  public static final int TAG_MAX_SAMPLE_VALUE = 281;
  public static final int TAG_PLANAR_CONFIGURATION = 284;
  public static final int TAG_YCBCR_SUBSAMPLING = 530;
  public static final int TAG_NEW_SUBFILE_TYPE = 254;
  public static final int TAG_SUBFILE_TYPE = 255;
  public static final int TAG_TRANSFER_FUNCTION = 301;
  public static final int TAG_PREDICTOR = 317;
  public static final int TAG_TILE_WIDTH = 322;
  public static final int TAG_TILE_LENGTH = 323;
  public static final int TAG_TILE_OFFSETS = 324;
  public static final int TAG_TILE_BYTE_COUNTS = 325;
  public static final int TAG_JPEG_TABLES = 347;
  public static final int TAG_CFA_REPEAT_PATTERN_DIM = 33421;
  public static final int TAG_CFA_PATTERN_2 = 33422;
  public static final int TAG_BATTERY_LEVEL = 33423;
  public static final int TAG_IPTC_NAA = 33723;
  public static final int TAG_INTER_COLOR_PROFILE = 34675;
  public static final int TAG_SPECTRAL_SENSITIVITY = 34852;
  public static final int TAG_OPTO_ELECTRIC_CONVERSION_FUNCTION = 34856;
  public static final int TAG_INTERLACE = 34857;
  public static final int TAG_TIME_ZONE_OFFSET = 34858;
  public static final int TAG_SELF_TIMER_MODE = 34859;
  public static final int TAG_FLASH_ENERGY = 37387;
  public static final int TAG_SPATIAL_FREQ_RESPONSE = 37388;
  public static final int TAG_NOISE = 37389;
  public static final int TAG_IMAGE_NUMBER = 37393;
  public static final int TAG_SECURITY_CLASSIFICATION = 37394;
  public static final int TAG_IMAGE_HISTORY = 37395;
  public static final int TAG_SUBJECT_LOCATION = 37396;
  public static final int TAG_EXPOSURE_INDEX_2 = 37397;
  public static final int TAG_TIFF_EP_STANDARD_ID = 37398;
  public static final int TAG_FLASH_ENERGY_2 = 41483;
  public static final int TAG_SPATIAL_FREQ_RESPONSE_2 = 41484;
  public static final int TAG_SUBJECT_LOCATION_2 = 41492;
  public static final int TAG_PAGE_NAME = 285;
  public static final int TAG_EXPOSURE_TIME = 33434;
  public static final int TAG_FNUMBER = 33437;
  public static final int TAG_EXPOSURE_PROGRAM = 34850;
  public static final int TAG_ISO_EQUIVALENT = 34855;
  public static final int TAG_EXIF_VERSION = 36864;
  public static final int TAG_DATETIME_ORIGINAL = 36867;
  public static final int TAG_DATETIME_DIGITIZED = 36868;
  public static final int TAG_COMPONENTS_CONFIGURATION = 37121;
  public static final int TAG_COMPRESSED_AVERAGE_BITS_PER_PIXEL = 37122;
  public static final int TAG_SHUTTER_SPEED = 37377;
  public static final int TAG_BRIGHTNESS_VALUE = 37379;
  public static final int TAG_EXPOSURE_BIAS = 37380;
  public static final int TAG_MAX_APERTURE = 37381;
  public static final int TAG_SUBJECT_DISTANCE = 37382;
  public static final int TAG_METERING_MODE = 37383;
  public static final int TAG_LIGHT_SOURCE = 37384;
  public static final int TAG_WHITE_BALANCE = 37384;
  public static final int TAG_FLASH = 37385;
  public static final int TAG_FOCAL_LENGTH = 37386;
  public static final int TAG_USER_COMMENT = 37510;
  public static final int TAG_SUBSECOND_TIME = 37520;
  public static final int TAG_SUBSECOND_TIME_ORIGINAL = 37521;
  public static final int TAG_SUBSECOND_TIME_DIGITIZED = 37522;
  public static final int TAG_FLASHPIX_VERSION = 40960;
  public static final int TAG_COLOR_SPACE = 40961;
  public static final int TAG_EXIF_IMAGE_WIDTH = 40962;
  public static final int TAG_EXIF_IMAGE_HEIGHT = 40963;
  public static final int TAG_RELATED_SOUND_FILE = 40964;
  public static final int TAG_FOCAL_PLANE_X_RES = 41486;
  public static final int TAG_FOCAL_PLANE_Y_RES = 41487;
  public static final int TAG_FOCAL_PLANE_UNIT = 41488;
  public static final int TAG_EXPOSURE_INDEX = 41493;
  public static final int TAG_SENSING_METHOD = 41495;
  public static final int TAG_FILE_SOURCE = 41728;
  public static final int TAG_SCENE_TYPE = 41729;
  public static final int TAG_CFA_PATTERN = 41730;
  public static final int TAG_CUSTOM_RENDERED = 41985;
  public static final int TAG_EXPOSURE_MODE = 41986;
  public static final int TAG_WHITE_BALANCE_MODE = 41987;
  public static final int TAG_DIGITAL_ZOOM_RATIO = 41988;
  public static final int TAG_35MM_FILM_EQUIV_FOCAL_LENGTH = 41989;
  public static final int TAG_SCENE_CAPTURE_TYPE = 41990;
  public static final int TAG_GAIN_CONTROL = 41991;
  public static final int TAG_CONTRAST = 41992;
  public static final int TAG_SATURATION = 41993;
  public static final int TAG_SHARPNESS = 41994;
  public static final int TAG_DEVICE_SETTING_DESCRIPTION = 41995;
  public static final int TAG_SUBJECT_DISTANCE_RANGE = 41996;
  public static final int TAG_IMAGE_UNIQUE_ID = 42016;
  public static final int TAG_CAMERA_OWNER_NAME = 42032;
  public static final int TAG_BODY_SERIAL_NUMBER = 42033;
  public static final int TAG_LENS_SPECIFICATION = 42034;
  public static final int TAG_LENS_MAKE = 42035;
  public static final int TAG_LENS_MODEL = 42036;
  public static final int TAG_LENS_SERIAL_NUMBER = 42037;
  public static final int TAG_GAMMA = 42240;
  public static final int TAG_LENS = 65002;
  @NotNull
  protected static final HashMap<Integer, String> _tagNameMap = new HashMap();
  
  public ExifSubIFDDirectory()
  {
    setDescriptor(new ExifSubIFDDescriptor(this));
  }
  
  @NotNull
  public String getName()
  {
    return "Exif SubIFD";
  }
  
  @NotNull
  protected HashMap<Integer, String> getTagNameMap()
  {
    return _tagNameMap;
  }
  
  static
  {
    _tagNameMap.put(Integer.valueOf(266), "Fill Order");
    _tagNameMap.put(Integer.valueOf(269), "Document Name");
    _tagNameMap.put(Integer.valueOf(4096), "Related Image File Format");
    _tagNameMap.put(Integer.valueOf(4097), "Related Image Width");
    _tagNameMap.put(Integer.valueOf(4098), "Related Image Length");
    _tagNameMap.put(Integer.valueOf(342), "Transfer Range");
    _tagNameMap.put(Integer.valueOf(512), "JPEG Proc");
    _tagNameMap.put(Integer.valueOf(37122), "Compressed Bits Per Pixel");
    _tagNameMap.put(Integer.valueOf(37500), "Maker Note");
    _tagNameMap.put(Integer.valueOf(40965), "Interoperability Offset");
    _tagNameMap.put(Integer.valueOf(254), "New Subfile Type");
    _tagNameMap.put(Integer.valueOf(255), "Subfile Type");
    _tagNameMap.put(Integer.valueOf(258), "Bits Per Sample");
    _tagNameMap.put(Integer.valueOf(262), "Photometric Interpretation");
    _tagNameMap.put(Integer.valueOf(263), "Thresholding");
    _tagNameMap.put(Integer.valueOf(273), "Strip Offsets");
    _tagNameMap.put(Integer.valueOf(277), "Samples Per Pixel");
    _tagNameMap.put(Integer.valueOf(278), "Rows Per Strip");
    _tagNameMap.put(Integer.valueOf(279), "Strip Byte Counts");
    _tagNameMap.put(Integer.valueOf(285), "Page Name");
    _tagNameMap.put(Integer.valueOf(284), "Planar Configuration");
    _tagNameMap.put(Integer.valueOf(301), "Transfer Function");
    _tagNameMap.put(Integer.valueOf(317), "Predictor");
    _tagNameMap.put(Integer.valueOf(322), "Tile Width");
    _tagNameMap.put(Integer.valueOf(323), "Tile Length");
    _tagNameMap.put(Integer.valueOf(324), "Tile Offsets");
    _tagNameMap.put(Integer.valueOf(325), "Tile Byte Counts");
    _tagNameMap.put(Integer.valueOf(347), "JPEG Tables");
    _tagNameMap.put(Integer.valueOf(530), "YCbCr Sub-Sampling");
    _tagNameMap.put(Integer.valueOf(33421), "CFA Repeat Pattern Dim");
    _tagNameMap.put(Integer.valueOf(33422), "CFA Pattern");
    _tagNameMap.put(Integer.valueOf(33423), "Battery Level");
    _tagNameMap.put(Integer.valueOf(33434), "Exposure Time");
    _tagNameMap.put(Integer.valueOf(33437), "F-Number");
    _tagNameMap.put(Integer.valueOf(33723), "IPTC/NAA");
    _tagNameMap.put(Integer.valueOf(34675), "Inter Color Profile");
    _tagNameMap.put(Integer.valueOf(34850), "Exposure Program");
    _tagNameMap.put(Integer.valueOf(34852), "Spectral Sensitivity");
    _tagNameMap.put(Integer.valueOf(34855), "ISO Speed Ratings");
    _tagNameMap.put(Integer.valueOf(34856), "Opto-electric Conversion Function (OECF)");
    _tagNameMap.put(Integer.valueOf(34857), "Interlace");
    _tagNameMap.put(Integer.valueOf(34858), "Time Zone Offset");
    _tagNameMap.put(Integer.valueOf(34859), "Self Timer Mode");
    _tagNameMap.put(Integer.valueOf(36864), "Exif Version");
    _tagNameMap.put(Integer.valueOf(36867), "Date/Time Original");
    _tagNameMap.put(Integer.valueOf(36868), "Date/Time Digitized");
    _tagNameMap.put(Integer.valueOf(37121), "Components Configuration");
    _tagNameMap.put(Integer.valueOf(37377), "Shutter Speed Value");
    _tagNameMap.put(Integer.valueOf(37378), "Aperture Value");
    _tagNameMap.put(Integer.valueOf(37379), "Brightness Value");
    _tagNameMap.put(Integer.valueOf(37380), "Exposure Bias Value");
    _tagNameMap.put(Integer.valueOf(37381), "Max Aperture Value");
    _tagNameMap.put(Integer.valueOf(37382), "Subject Distance");
    _tagNameMap.put(Integer.valueOf(37383), "Metering Mode");
    _tagNameMap.put(Integer.valueOf(37384), "Light Source");
    _tagNameMap.put(Integer.valueOf(37384), "White Balance");
    _tagNameMap.put(Integer.valueOf(37385), "Flash");
    _tagNameMap.put(Integer.valueOf(37386), "Focal Length");
    _tagNameMap.put(Integer.valueOf(37387), "Flash Energy");
    _tagNameMap.put(Integer.valueOf(37388), "Spatial Frequency Response");
    _tagNameMap.put(Integer.valueOf(37389), "Noise");
    _tagNameMap.put(Integer.valueOf(37393), "Image Number");
    _tagNameMap.put(Integer.valueOf(37394), "Security Classification");
    _tagNameMap.put(Integer.valueOf(37395), "Image History");
    _tagNameMap.put(Integer.valueOf(37396), "Subject Location");
    _tagNameMap.put(Integer.valueOf(41493), "Exposure Index");
    _tagNameMap.put(Integer.valueOf(37398), "TIFF/EP Standard ID");
    _tagNameMap.put(Integer.valueOf(37510), "User Comment");
    _tagNameMap.put(Integer.valueOf(37520), "Sub-Sec Time");
    _tagNameMap.put(Integer.valueOf(37521), "Sub-Sec Time Original");
    _tagNameMap.put(Integer.valueOf(37522), "Sub-Sec Time Digitized");
    _tagNameMap.put(Integer.valueOf(40960), "FlashPix Version");
    _tagNameMap.put(Integer.valueOf(40961), "Color Space");
    _tagNameMap.put(Integer.valueOf(40962), "Exif Image Width");
    _tagNameMap.put(Integer.valueOf(40963), "Exif Image Height");
    _tagNameMap.put(Integer.valueOf(40964), "Related Sound File");
    _tagNameMap.put(Integer.valueOf(41483), "Flash Energy");
    _tagNameMap.put(Integer.valueOf(41484), "Spatial Frequency Response");
    _tagNameMap.put(Integer.valueOf(41486), "Focal Plane X Resolution");
    _tagNameMap.put(Integer.valueOf(41487), "Focal Plane Y Resolution");
    _tagNameMap.put(Integer.valueOf(41488), "Focal Plane Resolution Unit");
    _tagNameMap.put(Integer.valueOf(41492), "Subject Location");
    _tagNameMap.put(Integer.valueOf(37397), "Exposure Index");
    _tagNameMap.put(Integer.valueOf(41495), "Sensing Method");
    _tagNameMap.put(Integer.valueOf(41728), "File Source");
    _tagNameMap.put(Integer.valueOf(41729), "Scene Type");
    _tagNameMap.put(Integer.valueOf(41730), "CFA Pattern");
    _tagNameMap.put(Integer.valueOf(41985), "Custom Rendered");
    _tagNameMap.put(Integer.valueOf(41986), "Exposure Mode");
    _tagNameMap.put(Integer.valueOf(41987), "White Balance Mode");
    _tagNameMap.put(Integer.valueOf(41988), "Digital Zoom Ratio");
    _tagNameMap.put(Integer.valueOf(41989), "Focal Length 35");
    _tagNameMap.put(Integer.valueOf(41990), "Scene Capture Type");
    _tagNameMap.put(Integer.valueOf(41991), "Gain Control");
    _tagNameMap.put(Integer.valueOf(41992), "Contrast");
    _tagNameMap.put(Integer.valueOf(41993), "Saturation");
    _tagNameMap.put(Integer.valueOf(41994), "Sharpness");
    _tagNameMap.put(Integer.valueOf(41995), "Device Setting Description");
    _tagNameMap.put(Integer.valueOf(41996), "Subject Distance Range");
    _tagNameMap.put(Integer.valueOf(42016), "Unique Image ID");
    _tagNameMap.put(Integer.valueOf(42032), "Camera Owner Name");
    _tagNameMap.put(Integer.valueOf(42033), "Body Serial Number");
    _tagNameMap.put(Integer.valueOf(42034), "Lens Specification");
    _tagNameMap.put(Integer.valueOf(42035), "Lens Make");
    _tagNameMap.put(Integer.valueOf(42036), "Lens Model");
    _tagNameMap.put(Integer.valueOf(42037), "Lens Serial Number");
    _tagNameMap.put(Integer.valueOf(42240), "Gamma");
    _tagNameMap.put(Integer.valueOf(280), "Minimum sample value");
    _tagNameMap.put(Integer.valueOf(281), "Maximum sample value");
    _tagNameMap.put(Integer.valueOf(65002), "Lens");
  }
}
