package javax.media.jai;

import java.awt.image.ComponentSampleModel;
import java.awt.image.SampleModel;


































































public final class RasterFormatTag
{
  private static final int COPY_MASK = 384;
  private static final int UNCOPIED = 0;
  private static final int COPIED = 128;
  private int formatTagID;
  private int[] bankIndices;
  private int numBands;
  private int[] bandOffsets;
  private int pixelStride;
  private boolean isPixelSequential;
  
  public RasterFormatTag(SampleModel sampleModel, int formatTagID)
  {
    this.formatTagID = formatTagID;
    if ((formatTagID & 0x180) == 0) {
      ComponentSampleModel csm = (ComponentSampleModel)sampleModel;
      
      bankIndices = csm.getBankIndices();
      numBands = csm.getNumDataElements();
      bandOffsets = csm.getBandOffsets();
      pixelStride = csm.getPixelStride();
      
      if (pixelStride != bandOffsets.length) {
        isPixelSequential = false;
      } else {
        isPixelSequential = true;
        for (int i = 0; i < bandOffsets.length; i++) {
          if ((bandOffsets[i] >= pixelStride) || (bankIndices[i] != bankIndices[0]))
          {
            isPixelSequential = false;
          }
          for (int j = i + 1; j < bandOffsets.length; j++) {
            if (bandOffsets[i] == bandOffsets[j]) {
              isPixelSequential = false;
            }
          }
          if (!isPixelSequential) break;
        }
      }
    } else if ((formatTagID & 0x180) == 128) {
      numBands = sampleModel.getNumBands();
      bandOffsets = new int[numBands];
      pixelStride = numBands;
      bankIndices = new int[numBands];
      
      for (int i = 0; i < numBands; i++) {
        bandOffsets[i] = i;
        bankIndices[i] = 0;
      }
      isPixelSequential = true;
    }
  }
  












  public final boolean isPixelSequential()
  {
    return isPixelSequential;
  }
  



  public final int getFormatTagID()
  {
    return formatTagID;
  }
  




  public final int[] getBankIndices()
  {
    if (isPixelSequential) {
      return bankIndices;
    }
    return null;
  }
  

  public final int getNumBands()
  {
    return numBands;
  }
  




  public final int[] getBandOffsets()
  {
    if (isPixelSequential) {
      return bandOffsets;
    }
    return null;
  }
  

  public final int getPixelStride()
  {
    return pixelStride;
  }
}
