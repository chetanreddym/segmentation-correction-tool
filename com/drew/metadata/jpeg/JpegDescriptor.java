package com.drew.metadata.jpeg;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

public class JpegDescriptor
  extends TagDescriptor<JpegDirectory>
{
  public JpegDescriptor(@NotNull JpegDirectory paramJpegDirectory)
  {
    super(paramJpegDirectory);
  }
  
  @Nullable
  public String getDescription(int paramInt)
  {
    switch (paramInt)
    {
    case -3: 
      return getImageCompressionTypeDescription();
    case 6: 
      return getComponentDataDescription(0);
    case 7: 
      return getComponentDataDescription(1);
    case 8: 
      return getComponentDataDescription(2);
    case 9: 
      return getComponentDataDescription(3);
    case 0: 
      return getDataPrecisionDescription();
    case 1: 
      return getImageHeightDescription();
    case 3: 
      return getImageWidthDescription();
    }
    return super.getDescription(paramInt);
  }
  
  @Nullable
  public String getImageCompressionTypeDescription()
  {
    Integer localInteger = ((JpegDirectory)_directory).getInteger(-3);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Baseline";
    case 1: 
      return "Extended sequential, Huffman";
    case 2: 
      return "Progressive, Huffman";
    case 3: 
      return "Lossless, Huffman";
    case 5: 
      return "Differential sequential, Huffman";
    case 6: 
      return "Differential progressive, Huffman";
    case 7: 
      return "Differential lossless, Huffman";
    case 8: 
      return "Reserved for JPEG extensions";
    case 9: 
      return "Extended sequential, arithmetic";
    case 10: 
      return "Progressive, arithmetic";
    case 11: 
      return "Lossless, arithmetic";
    case 13: 
      return "Differential sequential, arithmetic";
    case 14: 
      return "Differential progressive, arithmetic";
    case 15: 
      return "Differential lossless, arithmetic";
    }
    return "Unknown type: " + localInteger;
  }
  
  @Nullable
  public String getImageWidthDescription()
  {
    String str = ((JpegDirectory)_directory).getString(3);
    if (str == null) {
      return null;
    }
    return str + " pixels";
  }
  
  @Nullable
  public String getImageHeightDescription()
  {
    String str = ((JpegDirectory)_directory).getString(1);
    if (str == null) {
      return null;
    }
    return str + " pixels";
  }
  
  @Nullable
  public String getDataPrecisionDescription()
  {
    String str = ((JpegDirectory)_directory).getString(0);
    if (str == null) {
      return null;
    }
    return str + " bits";
  }
  
  @Nullable
  public String getComponentDataDescription(int paramInt)
  {
    JpegComponent localJpegComponent = ((JpegDirectory)_directory).getComponent(paramInt);
    if (localJpegComponent == null) {
      return null;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(localJpegComponent.getComponentName());
    localStringBuilder.append(" component: Quantization table ");
    localStringBuilder.append(localJpegComponent.getQuantizationTableNumber());
    localStringBuilder.append(", Sampling factors ");
    localStringBuilder.append(localJpegComponent.getHorizontalSamplingFactor());
    localStringBuilder.append(" horiz/");
    localStringBuilder.append(localJpegComponent.getVerticalSamplingFactor());
    localStringBuilder.append(" vert");
    return localStringBuilder.toString();
  }
}
