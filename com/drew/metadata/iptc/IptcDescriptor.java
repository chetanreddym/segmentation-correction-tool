package com.drew.metadata.iptc;

import com.drew.lang.StringUtil;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

public class IptcDescriptor
  extends TagDescriptor<IptcDirectory>
{
  public IptcDescriptor(@NotNull IptcDirectory paramIptcDirectory)
  {
    super(paramIptcDirectory);
  }
  
  @Nullable
  public String getDescription(int paramInt)
  {
    switch (paramInt)
    {
    case 276: 
      return getFileFormatDescription();
    case 537: 
      return getKeywordsDescription();
    }
    return super.getDescription(paramInt);
  }
  
  @Nullable
  public String getFileFormatDescription()
  {
    Integer localInteger = ((IptcDirectory)_directory).getInteger(276);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "No ObjectData";
    case 1: 
      return "IPTC-NAA Digital Newsphoto Parameter Record";
    case 2: 
      return "IPTC7901 Recommended Message Format";
    case 3: 
      return "Tagged Image File Format (Adobe/Aldus Image data)";
    case 4: 
      return "Illustrator (Adobe Graphics data)";
    case 5: 
      return "AppleSingle (Apple Computer Inc)";
    case 6: 
      return "NAA 89-3 (ANPA 1312)";
    case 7: 
      return "MacBinary II";
    case 8: 
      return "IPTC Unstructured Character Oriented File Format (UCOFF)";
    case 9: 
      return "United Press International ANPA 1312 variant";
    case 10: 
      return "United Press International Down-Load Message";
    case 11: 
      return "JPEG File Interchange (JFIF)";
    case 12: 
      return "Photo-CD Image-Pac (Eastman Kodak)";
    case 13: 
      return "Bit Mapped Graphics File [.BMP] (Microsoft)";
    case 14: 
      return "Digital Audio File [.WAV] (Microsoft & Creative Labs)";
    case 15: 
      return "Audio plus Moving Video [.AVI] (Microsoft)";
    case 16: 
      return "PC DOS/Windows Executable Files [.COM][.EXE]";
    case 17: 
      return "Compressed Binary File [.ZIP] (PKWare Inc)";
    case 18: 
      return "Audio Interchange File Format AIFF (Apple Computer Inc)";
    case 19: 
      return "RIFF Wave (Microsoft Corporation)";
    case 20: 
      return "Freehand (Macromedia/Aldus)";
    case 21: 
      return "Hypertext Markup Language [.HTML] (The Internet Society)";
    case 22: 
      return "MPEG 2 Audio Layer 2 (Musicom), ISO/IEC";
    case 23: 
      return "MPEG 2 Audio Layer 3, ISO/IEC";
    case 24: 
      return "Portable Document File [.PDF] Adobe";
    case 25: 
      return "News Industry Text Format (NITF)";
    case 26: 
      return "Tape Archive [.TAR]";
    case 27: 
      return "Tidningarnas Telegrambyra NITF version (TTNITF DTD)";
    case 28: 
      return "Ritzaus Bureau NITF version (RBNITF DTD)";
    case 29: 
      return "Corel Draw [.CDR]";
    }
    return String.format("Unknown (%d)", new Object[] { localInteger });
  }
  
  @Nullable
  public String getByLineDescription()
  {
    return ((IptcDirectory)_directory).getString(592);
  }
  
  @Nullable
  public String getByLineTitleDescription()
  {
    return ((IptcDirectory)_directory).getString(597);
  }
  
  @Nullable
  public String getCaptionDescription()
  {
    return ((IptcDirectory)_directory).getString(632);
  }
  
  @Nullable
  public String getCategoryDescription()
  {
    return ((IptcDirectory)_directory).getString(527);
  }
  
  @Nullable
  public String getCityDescription()
  {
    return ((IptcDirectory)_directory).getString(602);
  }
  
  @Nullable
  public String getCopyrightNoticeDescription()
  {
    return ((IptcDirectory)_directory).getString(628);
  }
  
  @Nullable
  public String getCountryOrPrimaryLocationDescription()
  {
    return ((IptcDirectory)_directory).getString(613);
  }
  
  @Nullable
  public String getCreditDescription()
  {
    return ((IptcDirectory)_directory).getString(622);
  }
  
  @Nullable
  public String getDateCreatedDescription()
  {
    return ((IptcDirectory)_directory).getString(567);
  }
  
  @Nullable
  public String getHeadlineDescription()
  {
    return ((IptcDirectory)_directory).getString(617);
  }
  
  @Nullable
  public String getKeywordsDescription()
  {
    String[] arrayOfString = ((IptcDirectory)_directory).getStringArray(537);
    if (arrayOfString == null) {
      return null;
    }
    return StringUtil.join(arrayOfString, ";");
  }
  
  @Nullable
  public String getObjectNameDescription()
  {
    return ((IptcDirectory)_directory).getString(517);
  }
  
  @Nullable
  public String getOriginalTransmissionReferenceDescription()
  {
    return ((IptcDirectory)_directory).getString(615);
  }
  
  @Nullable
  public String getOriginatingProgramDescription()
  {
    return ((IptcDirectory)_directory).getString(577);
  }
  
  @Nullable
  public String getProvinceOrStateDescription()
  {
    return ((IptcDirectory)_directory).getString(607);
  }
  
  @Nullable
  public String getRecordVersionDescription()
  {
    return ((IptcDirectory)_directory).getString(512);
  }
  
  @Nullable
  public String getReleaseDateDescription()
  {
    return ((IptcDirectory)_directory).getString(542);
  }
  
  @Nullable
  public String getReleaseTimeDescription()
  {
    return ((IptcDirectory)_directory).getString(547);
  }
  
  @Nullable
  public String getSourceDescription()
  {
    return ((IptcDirectory)_directory).getString(627);
  }
  
  @Nullable
  public String getSpecialInstructionsDescription()
  {
    return ((IptcDirectory)_directory).getString(552);
  }
  
  @Nullable
  public String getSupplementalCategoriesDescription()
  {
    return ((IptcDirectory)_directory).getString(532);
  }
  
  @Nullable
  public String getTimeCreatedDescription()
  {
    return ((IptcDirectory)_directory).getString(572);
  }
  
  @Nullable
  public String getUrgencyDescription()
  {
    return ((IptcDirectory)_directory).getString(522);
  }
  
  @Nullable
  public String getWriterDescription()
  {
    return ((IptcDirectory)_directory).getString(634);
  }
}
