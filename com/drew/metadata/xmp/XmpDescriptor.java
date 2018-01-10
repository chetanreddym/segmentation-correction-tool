package com.drew.metadata.xmp;

import com.drew.imaging.PhotographicConversions;
import com.drew.lang.Rational;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;
import java.text.DecimalFormat;

public class XmpDescriptor
  extends TagDescriptor<XmpDirectory>
{
  @NotNull
  private static final DecimalFormat SimpleDecimalFormatter = new DecimalFormat("0.#");
  
  public XmpDescriptor(@NotNull XmpDirectory paramXmpDirectory)
  {
    super(paramXmpDirectory);
  }
  
  public String getDescription(int paramInt)
  {
    switch (paramInt)
    {
    case 1: 
    case 2: 
      return ((XmpDirectory)_directory).getString(paramInt);
    case 3: 
      return getExposureTimeDescription();
    case 12: 
      return getExposureProgramDescription();
    case 4: 
      return getShutterSpeedDescription();
    case 5: 
      return getFNumberDescription();
    case 6: 
    case 7: 
    case 8: 
    case 9: 
      return ((XmpDirectory)_directory).getString(paramInt);
    case 10: 
      return getFocalLengthDescription();
    case 11: 
      return getApertureValueDescription();
    }
    return super.getDescription(paramInt);
  }
  
  @Nullable
  public String getExposureTimeDescription()
  {
    String str = ((XmpDirectory)_directory).getString(3);
    if (str == null) {
      return null;
    }
    return str + " sec";
  }
  
  @Nullable
  public String getExposureProgramDescription()
  {
    Integer localInteger = ((XmpDirectory)_directory).getInteger(12);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 1: 
      return "Manual control";
    case 2: 
      return "Program normal";
    case 3: 
      return "Aperture priority";
    case 4: 
      return "Shutter priority";
    case 5: 
      return "Program creative (slow program)";
    case 6: 
      return "Program action (high-speed program)";
    case 7: 
      return "Portrait mode";
    case 8: 
      return "Landscape mode";
    }
    return "Unknown program (" + localInteger + ")";
  }
  
  @Nullable
  public String getShutterSpeedDescription()
  {
    Float localFloat = ((XmpDirectory)_directory).getFloatObject(4);
    if (localFloat == null) {
      return null;
    }
    if (localFloat.floatValue() <= 1.0F)
    {
      float f1 = (float)(1.0D / Math.exp(localFloat.floatValue() * Math.log(2.0D)));
      long l = Math.round(f1 * 10.0D);
      float f2 = (float)l / 10.0F;
      return f2 + " sec";
    }
    int i = (int)Math.exp(localFloat.floatValue() * Math.log(2.0D));
    return "1/" + i + " sec";
  }
  
  @Nullable
  public String getFNumberDescription()
  {
    Rational localRational = ((XmpDirectory)_directory).getRational(5);
    if (localRational == null) {
      return null;
    }
    return "F" + SimpleDecimalFormatter.format(localRational.doubleValue());
  }
  
  @Nullable
  public String getFocalLengthDescription()
  {
    Rational localRational = ((XmpDirectory)_directory).getRational(10);
    if (localRational == null) {
      return null;
    }
    DecimalFormat localDecimalFormat = new DecimalFormat("0.0##");
    return localDecimalFormat.format(localRational.doubleValue()) + " mm";
  }
  
  @Nullable
  public String getApertureValueDescription()
  {
    Double localDouble = ((XmpDirectory)_directory).getDoubleObject(11);
    if (localDouble == null) {
      return null;
    }
    double d = PhotographicConversions.apertureToFStop(localDouble.doubleValue());
    return "F" + SimpleDecimalFormatter.format(d);
  }
}
