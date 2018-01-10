package javax.media.jai;

import java.awt.image.ColorModel;
import java.awt.image.SampleModel;
import java.util.List;
import java.util.Map;

public abstract interface ColorModelFactory
{
  public abstract ColorModel createColorModel(SampleModel paramSampleModel, List paramList, Map paramMap);
}
