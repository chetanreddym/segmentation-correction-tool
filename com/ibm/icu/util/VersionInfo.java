package com.ibm.icu.util;

import java.util.HashMap;
























































































































public final class VersionInfo
{
  public static VersionInfo getInstance(String version)
  {
    int length = version.length();
    int[] array = { 0, 0, 0, 0 };
    int count = 0;
    int index = 0;
    
    while ((count < 4) && (index < length)) {
      char c = version.charAt(index);
      if (c == '.') {
        count++;
      }
      else {
        c = (char)(c - '0');
        if ((c < 0) || (c > '\t')) {
          throw new IllegalArgumentException("Invalid version number: Version number may be negative or greater than 255");
        }
        array[count] *= 10;
        array[count] += c;
      }
      index++;
    }
    if (index != length) {
      throw new IllegalArgumentException("Invalid version number: String '" + version + "' exceeds version format");
    }
    
    for (int i = 0; i < 4; i++) {
      if ((array[i] < 0) || (array[i] > 255)) {
        throw new IllegalArgumentException("Invalid version number: Version number may be negative or greater than 255");
      }
    }
    
    return getInstance(array[0], array[1], array[2], array[3]);
  }
  













  public static VersionInfo getInstance(int major, int minor, int milli, int micro)
  {
    if ((major < 0) || (major > 255) || (minor < 0) || (minor > 255) || (milli < 0) || (milli > 255) || (micro < 0) || (micro > 255))
    {
      throw new IllegalArgumentException("Invalid version number: Version number may be negative or greater than 255");
    }
    int version = getInt(major, minor, milli, micro);
    Integer key = new Integer(version);
    Object result = MAP_.get(key);
    if (result == null) {
      result = new VersionInfo(version);
      MAP_.put(key, result);
    }
    return (VersionInfo)result;
  }
  










  public static VersionInfo getInstance(int major, int minor, int milli)
  {
    return getInstance(major, minor, milli, 0);
  }
  









  public static VersionInfo getInstance(int major, int minor)
  {
    return getInstance(major, minor, 0, 0);
  }
  








  public static VersionInfo getInstance(int major)
  {
    return getInstance(major, 0, 0, 0);
  }
  






  public String toString()
  {
    StringBuffer result = new StringBuffer(7);
    result.append(getMajor());
    result.append('.');
    result.append(getMinor());
    result.append('.');
    result.append(getMilli());
    result.append('.');
    result.append(getMicro());
    return result.toString();
  }
  





  public int getMajor()
  {
    return m_version_ >> 24 & 0xFF;
  }
  





  public int getMinor()
  {
    return m_version_ >> 16 & 0xFF;
  }
  





  public int getMilli()
  {
    return m_version_ >> 8 & 0xFF;
  }
  





  public int getMicro()
  {
    return m_version_ & 0xFF;
  }
  







  public boolean equals(Object other)
  {
    return other == this;
  }
  











  public int compareTo(VersionInfo other)
  {
    return m_version_ - m_version_;
  }
  












  private static final HashMap MAP_ = new HashMap();
  















  public static final VersionInfo UNICODE_1_0 = getInstance(1, 0, 0, 0);
  public static final VersionInfo UNICODE_1_0_1 = getInstance(1, 0, 1, 0);
  public static final VersionInfo UNICODE_1_1_0 = getInstance(1, 1, 0, 0);
  public static final VersionInfo UNICODE_1_1_5 = getInstance(1, 1, 5, 0);
  public static final VersionInfo UNICODE_2_0 = getInstance(2, 0, 0, 0);
  public static final VersionInfo UNICODE_2_1_2 = getInstance(2, 1, 2, 0);
  public static final VersionInfo UNICODE_2_1_5 = getInstance(2, 1, 5, 0);
  public static final VersionInfo UNICODE_2_1_8 = getInstance(2, 1, 8, 0);
  public static final VersionInfo UNICODE_2_1_9 = getInstance(2, 1, 9, 0);
  public static final VersionInfo UNICODE_3_0 = getInstance(3, 0, 0, 0);
  public static final VersionInfo UNICODE_3_0_1 = getInstance(3, 0, 1, 0);
  public static final VersionInfo UNICODE_3_1_0 = getInstance(3, 1, 0, 0);
  public static final VersionInfo UNICODE_3_1_1 = getInstance(3, 1, 1, 0);
  public static final VersionInfo UNICODE_3_2 = getInstance(3, 2, 0, 0);
  public static final VersionInfo UNICODE_4_0 = getInstance(4, 0, 0, 0);
  public static final VersionInfo ICU_VERSION = getInstance(2, 6, 1, 0);
  
  private int m_version_;
  
  private static final int LAST_BYTE_MASK_ = 255;
  
  private static final String INVALID_VERSION_NUMBER_ = "Invalid version number: Version number may be negative or greater than 255";
  

  private VersionInfo(int compactversion)
  {
    m_version_ = compactversion;
  }
  







  private static int getInt(int major, int minor, int milli, int micro)
  {
    return major << 24 | minor << 16 | milli << 8 | micro;
  }
}
