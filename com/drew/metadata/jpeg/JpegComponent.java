package com.drew.metadata.jpeg;

import com.drew.lang.annotations.Nullable;
import java.io.Serializable;

public class JpegComponent
  implements Serializable
{
  private static final long serialVersionUID = 61121257899091914L;
  private final int _componentId;
  private final int _samplingFactorByte;
  private final int _quantizationTableNumber;
  
  public JpegComponent(int paramInt1, int paramInt2, int paramInt3)
  {
    _componentId = paramInt1;
    _samplingFactorByte = paramInt2;
    _quantizationTableNumber = paramInt3;
  }
  
  public int getComponentId()
  {
    return _componentId;
  }
  
  @Nullable
  public String getComponentName()
  {
    switch (_componentId)
    {
    case 1: 
      return "Y";
    case 2: 
      return "Cb";
    case 3: 
      return "Cr";
    case 4: 
      return "I";
    case 5: 
      return "Q";
    }
    return null;
  }
  
  public int getQuantizationTableNumber()
  {
    return _quantizationTableNumber;
  }
  
  public int getHorizontalSamplingFactor()
  {
    return _samplingFactorByte & 0xF;
  }
  
  public int getVerticalSamplingFactor()
  {
    return _samplingFactorByte >> 4 & 0xF;
  }
}
