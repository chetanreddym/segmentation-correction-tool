package com.sun.media.jai.opimage;

import com.sun.media.jai.util.ImageUtil;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Vector;
import javax.media.jai.BorderExtender;
import javax.media.jai.BorderExtenderConstant;
import javax.media.jai.ImageLayout;
import javax.media.jai.OpImage;
import javax.media.jai.PlanarImage;
import javax.media.jai.ROI;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFormatTag;
import javax.media.jai.operator.MosaicDescriptor;
import javax.media.jai.operator.MosaicType;


















public class MosaicOpImage
  extends OpImage
{
  private static final int WEIGHT_TYPE_ALPHA = 1;
  private static final int WEIGHT_TYPE_ROI = 2;
  private static final int WEIGHT_TYPE_THRESHOLD = 3;
  protected MosaicType mosaicType;
  protected PlanarImage[] sourceAlpha;
  protected ROI[] sourceROI;
  protected double[][] sourceThreshold;
  protected double[] backgroundValues;
  protected int numBands;
  protected int[] background;
  protected int[][] threshold;
  protected boolean isAlphaBitmask = false;
  
  private BorderExtender sourceExtender;
  
  private BorderExtender zeroExtender;
  
  private PlanarImage[] roiImage;
  
  private static final ImageLayout getLayout(Vector sources, ImageLayout layout)
  {
    RenderedImage source0 = null;
    SampleModel targetSM = null;
    ColorModel targetCM = null;
    

    int numSources = sources.size();
    
    if (numSources > 0)
    {
      source0 = (RenderedImage)sources.get(0);
      targetSM = source0.getSampleModel();
      targetCM = source0.getColorModel();
    } else if ((layout != null) && (layout.isValid(268)))
    {



      targetSM = layout.getSampleModel(null);
      if (targetSM == null) {
        throw new IllegalArgumentException(JaiI18N.getString("MosaicOpImage7"));
      }
    }
    else {
      throw new IllegalArgumentException(JaiI18N.getString("MosaicOpImage8"));
    }
    


    int dataType = targetSM.getDataType();
    int numBands = targetSM.getNumBands();
    int sampleSize = targetSM.getSampleSize(0);
    

    for (int i = 1; i < numBands; i++) {
      if (targetSM.getSampleSize(i) != sampleSize) {
        throw new IllegalArgumentException(JaiI18N.getString("MosaicOpImage1"));
      }
    }
    


    if (numSources < 1) {
      return (ImageLayout)layout.clone();
    }
    

    for (int i = 1; i < numSources; i++) {
      RenderedImage source = (RenderedImage)sources.get(i);
      SampleModel sourceSM = source.getSampleModel();
      

      if (sourceSM.getDataType() != dataType)
        throw new IllegalArgumentException(JaiI18N.getString("MosaicOpImage2"));
      if (sourceSM.getNumBands() != numBands) {
        throw new IllegalArgumentException(JaiI18N.getString("MosaicOpImage3"));
      }
      

      for (int j = 0; j < numBands; j++) {
        if (sourceSM.getSampleSize(j) != sampleSize) {
          throw new IllegalArgumentException(JaiI18N.getString("MosaicOpImage1"));
        }
      }
    }
    

    ImageLayout mosaicLayout = layout == null ? new ImageLayout() : (ImageLayout)layout.clone();
    


    Rectangle mosaicBounds = new Rectangle();
    if (mosaicLayout.isValid(15))
    {



      mosaicBounds.setBounds(mosaicLayout.getMinX(null), mosaicLayout.getMinY(null), mosaicLayout.getWidth(null), mosaicLayout.getHeight(null));


    }
    else if (numSources > 0)
    {
      mosaicBounds.setBounds(source0.getMinX(), source0.getMinY(), source0.getWidth(), source0.getHeight());
      
      for (int i = 1; i < numSources; i++) {
        RenderedImage source = (RenderedImage)sources.get(i);
        Rectangle sourceBounds = new Rectangle(source.getMinX(), source.getMinY(), source.getWidth(), source.getHeight());
        

        mosaicBounds = mosaicBounds.union(sourceBounds);
      }
    }
    

    mosaicLayout.setMinX(x);
    mosaicLayout.setMinY(y);
    mosaicLayout.setWidth(width);
    mosaicLayout.setHeight(height);
    

    if (mosaicLayout.isValid(256))
    {
      SampleModel destSM = mosaicLayout.getSampleModel(null);
      

      boolean unsetSampleModel = (destSM.getNumBands() != numBands) || (destSM.getDataType() != dataType);
      



      for (int i = 0; (!unsetSampleModel) && (i < numBands); i++) {
        if (destSM.getSampleSize(i) != sampleSize) {
          unsetSampleModel = true;
        }
      }
      

      if (unsetSampleModel) {
        mosaicLayout.unsetValid(256);
      }
    }
    
    return mosaicLayout;
  }
  






  public MosaicOpImage(Vector sources, ImageLayout layout, Map config, MosaicType mosaicType, PlanarImage[] sourceAlpha, ROI[] sourceROI, double[][] sourceThreshold, double[] backgroundValues)
  {
    super(sources, getLayout(sources, layout), config, true);
    

    numBands = sampleModel.getNumBands();
    

    int numSources = getNumSources();
    

    this.mosaicType = mosaicType;
    

    this.sourceAlpha = null;
    if (sourceAlpha != null)
    {
      for (int i = 0; i < sourceAlpha.length; i++) {
        if (sourceAlpha[i] != null) {
          SampleModel alphaSM = sourceAlpha[i].getSampleModel();
          
          if (alphaSM.getNumBands() != 1)
            throw new IllegalArgumentException(JaiI18N.getString("MosaicOpImage4"));
          if (alphaSM.getDataType() != sampleModel.getDataType())
          {
            throw new IllegalArgumentException(JaiI18N.getString("MosaicOpImage5")); }
          if (alphaSM.getSampleSize(0) != sampleModel.getSampleSize(0))
          {
            throw new IllegalArgumentException(JaiI18N.getString("MosaicOpImage6"));
          }
        }
      }
      
      this.sourceAlpha = new PlanarImage[numSources];
      System.arraycopy(sourceAlpha, 0, this.sourceAlpha, 0, Math.min(sourceAlpha.length, numSources));
    }
    



    this.sourceROI = null;
    if (sourceROI != null) {
      this.sourceROI = new ROI[numSources];
      System.arraycopy(sourceROI, 0, this.sourceROI, 0, Math.min(sourceROI.length, numSources));
    }
    




    isAlphaBitmask = ((mosaicType != MosaicDescriptor.MOSAIC_TYPE_BLEND) || (sourceAlpha == null) || (sourceAlpha.length < numSources));
    

    if (!isAlphaBitmask) {
      for (int i = 0; i < numSources; i++) {
        if (sourceAlpha[i] == null) {
          isAlphaBitmask = true;
          break;
        }
      }
    }
    

    this.sourceThreshold = new double[numSources][numBands];
    

    if (sourceThreshold == null) {
      sourceThreshold = new double[][] { { 1.0D } };
    }
    for (int i = 0; i < numSources; i++)
    {
      if ((i < sourceThreshold.length) && (sourceThreshold[i] != null)) {
        if (sourceThreshold[i].length < numBands)
        {
          Arrays.fill(this.sourceThreshold[i], sourceThreshold[i][0]);
        }
        else
        {
          System.arraycopy(sourceThreshold[i], 0, this.sourceThreshold[i], 0, numBands);
        }
        

      }
      else {
        this.sourceThreshold[i] = this.sourceThreshold[0];
      }
    }
    

    threshold = new int[numSources][numBands];
    for (int i = 0; i < numSources; i++) {
      for (int j = 0; j < numBands; j++)
      {
        threshold[i][j] = ((int)this.sourceThreshold[i][j]);
      }
    }
    

    this.backgroundValues = new double[numBands];
    if (backgroundValues == null) {
      backgroundValues = new double[] { 0.0D };
    }
    if (backgroundValues.length < numBands) {
      Arrays.fill(this.backgroundValues, backgroundValues[0]);
    } else {
      System.arraycopy(backgroundValues, 0, this.backgroundValues, 0, numBands);
    }
    



    background = new int[this.backgroundValues.length];
    int dataType = sampleModel.getDataType();
    for (int i = 0; i < background.length; i++) {
      switch (dataType) {
      case 0: 
        background[i] = ImageUtil.clampRoundByte(this.backgroundValues[i]);
        
        break;
      case 1: 
        background[i] = ImageUtil.clampRoundUShort(this.backgroundValues[i]);
        
        break;
      case 2: 
        background[i] = ImageUtil.clampRoundShort(this.backgroundValues[i]);
        
        break;
      case 3: 
        background[i] = ImageUtil.clampRoundInt(this.backgroundValues[i]);
      }
    }
    double sourceExtensionConstant;
    double sourceExtensionConstant;
    double sourceExtensionConstant;
    double sourceExtensionConstant;
    double sourceExtensionConstant;
    double sourceExtensionConstant;
    switch (dataType) {
    case 0: 
      sourceExtensionConstant = 0.0D;
      break;
    case 1: 
      sourceExtensionConstant = 0.0D;
      break;
    case 2: 
      sourceExtensionConstant = -32768.0D;
      break;
    case 3: 
      sourceExtensionConstant = -2.147483648E9D;
      break;
    case 4: 
      sourceExtensionConstant = -3.4028234663852886E38D;
      break;
    case 5: 
    default: 
      sourceExtensionConstant = -1.7976931348623157E308D;
    }
    
    

    sourceExtender = (sourceExtensionConstant == 0.0D ? BorderExtender.createInstance(0) : new BorderExtenderConstant(new double[] { sourceExtensionConstant }));
    




    if ((sourceAlpha != null) || (sourceROI != null)) {
      zeroExtender = BorderExtender.createInstance(0);
    }
    


    if (sourceROI != null) {
      roiImage = new PlanarImage[numSources];
      for (int i = 0; i < sourceROI.length; i++) {
        if (sourceROI[i] != null) {
          roiImage[i] = sourceROI[i].getAsImage();
        }
      }
    }
  }
  
  public Rectangle mapDestRect(Rectangle destRect, int sourceIndex)
  {
    if (destRect == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if ((sourceIndex < 0) || (sourceIndex >= getNumSources())) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic1"));
    }
    
    return destRect.intersection(getSourceImage(sourceIndex).getBounds());
  }
  
  public Rectangle mapSourceRect(Rectangle sourceRect, int sourceIndex)
  {
    if (sourceRect == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if ((sourceIndex < 0) || (sourceIndex >= getNumSources())) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic1"));
    }
    
    return sourceRect.intersection(getBounds());
  }
  
  public Raster computeTile(int tileX, int tileY)
  {
    WritableRaster dest = createWritableRaster(sampleModel, new Point(tileXToX(tileX), tileYToY(tileY)));
    



    Rectangle destRect = getTileRect(tileX, tileY);
    
    int numSources = getNumSources();
    
    Raster[] rasterSources = new Raster[numSources];
    Raster[] alpha = sourceAlpha != null ? new Raster[numSources] : null;
    
    Raster[] roi = sourceROI != null ? new Raster[numSources] : null;
    


    for (int i = 0; i < numSources; i++) {
      PlanarImage source = getSourceImage(i);
      Rectangle srcRect = mapDestRect(destRect, i);
      




      rasterSources[i] = ((srcRect != null) && (srcRect.isEmpty()) ? null : source.getExtendedData(destRect, sourceExtender));
      

      if (rasterSources[i] != null) {
        if ((sourceAlpha != null) && (sourceAlpha[i] != null)) {
          alpha[i] = sourceAlpha[i].getExtendedData(destRect, zeroExtender);
        }
        

        if ((sourceROI != null) && (sourceROI[i] != null)) {
          roi[i] = roiImage[i].getExtendedData(destRect, zeroExtender);
        }
      }
    }
    

    computeRect(rasterSources, dest, destRect, alpha, roi);
    
    for (int i = 0; i < numSources; i++) {
      Raster sourceData = rasterSources[i];
      if (sourceData != null) {
        PlanarImage source = getSourceImage(i);
        

        if (source.overlapsMultipleTiles(sourceData.getBounds())) {
          recycleTile(sourceData);
        }
      }
    }
    
    return dest;
  }
  

  protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect)
  {
    computeRect(sources, dest, destRect, null, null);
  }
  




  protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect, Raster[] alphaRaster, Raster[] roiRaster)
  {
    int numSources = sources.length;
    

    ArrayList sourceList = new ArrayList(numSources);
    for (int i = 0; i < numSources; i++) {
      if (sources[i] != null) {
        sourceList.add(sources[i]);
      }
    }
    

    int numNonNullSources = sourceList.size();
    if (numNonNullSources == 0) {
      ImageUtil.fillBackground(dest, destRect, backgroundValues);
      return;
    }
    

    SampleModel[] sourceSM = new SampleModel[numNonNullSources];
    for (int i = 0; i < numNonNullSources; i++) {
      sourceSM[i] = ((Raster)sourceList.get(i)).getSampleModel();
    }
    int formatTagID = RasterAccessor.findCompatibleTag(sourceSM, dest.getSampleModel());
    



    RasterAccessor[] s = new RasterAccessor[numSources];
    for (int i = 0; i < numSources; i++) {
      if (sources[i] != null) {
        RasterFormatTag formatTag = new RasterFormatTag(sources[i].getSampleModel(), formatTagID);
        

        s[i] = new RasterAccessor(sources[i], destRect, formatTag, null);
      }
    }
    


    RasterAccessor d = new RasterAccessor(dest, destRect, new RasterFormatTag(dest.getSampleModel(), formatTagID), null);
    





    RasterAccessor[] a = new RasterAccessor[numSources];
    if (alphaRaster != null) {
      for (int i = 0; i < numSources; i++) {
        if (alphaRaster[i] != null) {
          SampleModel alphaSM = alphaRaster[i].getSampleModel();
          int alphaFormatTagID = RasterAccessor.findCompatibleTag(null, alphaSM);
          
          RasterFormatTag alphaFormatTag = new RasterFormatTag(alphaSM, alphaFormatTagID);
          
          a[i] = new RasterAccessor(alphaRaster[i], destRect, alphaFormatTag, sourceAlpha[i].getColorModel());
        }
      }
    }
    



    switch (d.getDataType()) {
    case 0: 
      computeRectByte(s, d, a, roiRaster);
      break;
    case 1: 
      computeRectUShort(s, d, a, roiRaster);
      break;
    case 2: 
      computeRectShort(s, d, a, roiRaster);
      break;
    case 3: 
      computeRectInt(s, d, a, roiRaster);
      break;
    case 4: 
      computeRectFloat(s, d, a, roiRaster);
      break;
    case 5: 
      computeRectDouble(s, d, a, roiRaster);
    }
    
    
    d.copyDataToRaster();
  }
  



  private void computeRectByte(RasterAccessor[] src, RasterAccessor dst, RasterAccessor[] alfa, Raster[] roi)
  {
    int numSources = src.length;
    

    int[] srcLineStride = new int[numSources];
    int[] srcPixelStride = new int[numSources];
    int[][] srcBandOffsets = new int[numSources][];
    byte[][][] srcData = new byte[numSources][][];
    

    for (int i = 0; i < numSources; i++) {
      if (src[i] != null) {
        srcLineStride[i] = src[i].getScanlineStride();
        srcPixelStride[i] = src[i].getPixelStride();
        srcBandOffsets[i] = src[i].getBandOffsets();
        srcData[i] = src[i].getByteDataArrays();
      }
    }
    

    int dstMinX = dst.getX();
    int dstMinY = dst.getY();
    int dstWidth = dst.getWidth();
    int dstHeight = dst.getHeight();
    int dstMaxX = dstMinX + dstWidth;
    int dstMaxY = dstMinY + dstHeight;
    int dstBands = dst.getNumBands();
    int dstLineStride = dst.getScanlineStride();
    int dstPixelStride = dst.getPixelStride();
    int[] dstBandOffsets = dst.getBandOffsets();
    byte[][] dstData = dst.getByteDataArrays();
    

    boolean hasAlpha = false;
    for (int i = 0; i < numSources; i++) {
      if (alfa[i] != null) {
        hasAlpha = true;
        break;
      }
    }
    

    int[] alfaLineStride = null;
    int[] alfaPixelStride = null;
    int[][] alfaBandOffsets = (int[][])null;
    byte[][][] alfaData = (byte[][][])null;
    
    if (hasAlpha)
    {
      alfaLineStride = new int[numSources];
      alfaPixelStride = new int[numSources];
      alfaBandOffsets = new int[numSources][];
      alfaData = new byte[numSources][][];
      

      for (int i = 0; i < numSources; i++) {
        if (alfa[i] != null) {
          alfaLineStride[i] = alfa[i].getScanlineStride();
          alfaPixelStride[i] = alfa[i].getPixelStride();
          alfaBandOffsets[i] = alfa[i].getBandOffsets();
          alfaData[i] = alfa[i].getByteDataArrays();
        }
      }
    }
    

    int[] weightTypes = new int[numSources];
    for (int i = 0; i < numSources; i++) {
      weightTypes[i] = 3;
      if (alfa[i] != null) {
        weightTypes[i] = 1;
      } else if ((sourceROI != null) && (sourceROI[i] != null)) {
        weightTypes[i] = 2;
      }
    }
    

    int[] sLineOffsets = new int[numSources];
    int[] sPixelOffsets = new int[numSources];
    byte[][] sBandData = new byte[numSources][];
    

    int[] aLineOffsets = null;
    int[] aPixelOffsets = null;
    byte[][] aBandData = (byte[][])null;
    if (hasAlpha) {
      aLineOffsets = new int[numSources];
      aPixelOffsets = new int[numSources];
      aBandData = new byte[numSources][];
    }
    
    for (int b = 0; b < dstBands; b++)
    {
      for (int s = 0; s < numSources; s++) {
        if (src[s] != null) {
          sBandData[s] = srcData[s][b];
          sLineOffsets[s] = srcBandOffsets[s][b];
        }
        if (weightTypes[s] == 1) {
          aBandData[s] = alfaData[s][0];
          aLineOffsets[s] = alfaBandOffsets[s][0];
        }
      }
      

      byte[] dBandData = dstData[b];
      int dLineOffset = dstBandOffsets[b];
      
      if (mosaicType == MosaicDescriptor.MOSAIC_TYPE_OVERLAY) {
        for (int dstY = dstMinY; dstY < dstMaxY; dstY++)
        {

          for (int s = 0; s < numSources; s++) {
            if (src[s] != null) {
              sPixelOffsets[s] = sLineOffsets[s];
              sLineOffsets[s] += srcLineStride[s];
            }
            if (alfa[s] != null) {
              aPixelOffsets[s] = aLineOffsets[s];
              aLineOffsets[s] += alfaLineStride[s];
            }
          }
          


          int dPixelOffset = dLineOffset;
          dLineOffset += dstLineStride;
          
          for (int dstX = dstMinX; dstX < dstMaxX; dstX++)
          {

            boolean setDestValue = false;
            


            for (int s = 0; s < numSources; s++) {
              if (src[s] != null)
              {
                byte sourceValue = sBandData[s][sPixelOffsets[s]];
                
                sPixelOffsets[s] += srcPixelStride[s];
                
                switch (weightTypes[s]) {
                case 1: 
                  setDestValue = aBandData[s][aPixelOffsets[s]] != 0;
                  
                  aPixelOffsets[s] += alfaPixelStride[s];
                  break;
                case 2: 
                  setDestValue = roi[s].getSample(dstX, dstY, 0) > 0;
                  
                  break;
                default: 
                  setDestValue = (sourceValue & 0xFF) >= sourceThreshold[s][b];
                }
                
                



                if (setDestValue) {
                  dBandData[dPixelOffset] = sourceValue;
                  

                  for (int k = s + 1; k < numSources; k++) {
                    if (src[k] != null) {
                      sPixelOffsets[k] += srcPixelStride[k];
                    }
                    if (alfa[k] != null) {
                      aPixelOffsets[k] += alfaPixelStride[k];
                    }
                  }
                  break;
                }
              }
            }
            

            if (!setDestValue) {
              dBandData[dPixelOffset] = ((byte)background[b]);
            }
            
            dPixelOffset += dstPixelStride;
          }
        }
      } else {
        for (int dstY = dstMinY; dstY < dstMaxY; dstY++)
        {

          for (int s = 0; s < numSources; s++) {
            if (src[s] != null) {
              sPixelOffsets[s] = sLineOffsets[s];
              sLineOffsets[s] += srcLineStride[s];
            }
            if (weightTypes[s] == 1) {
              aPixelOffsets[s] = aLineOffsets[s];
              aLineOffsets[s] += alfaLineStride[s];
            }
          }
          


          int dPixelOffset = dLineOffset;
          dLineOffset += dstLineStride;
          
          for (int dstX = dstMinX; dstX < dstMaxX; dstX++)
          {

            float numerator = 0.0F;
            float denominator = 0.0F;
            

            for (int s = 0; s < numSources; s++) {
              if (src[s] != null)
              {
                byte sourceValue = sBandData[s][sPixelOffsets[s]];
                
                sPixelOffsets[s] += srcPixelStride[s];
                
                float weight = 0.0F;
                switch (weightTypes[s]) {
                case 1: 
                  weight = aBandData[s][aPixelOffsets[s]] & 0xFF;
                  if ((weight > 0.0F) && (isAlphaBitmask)) {
                    weight = 1.0F;
                  } else {
                    weight /= 255.0F;
                  }
                  aPixelOffsets[s] += alfaPixelStride[s];
                  break;
                case 2: 
                  weight = roi[s].getSample(dstX, dstY, 0) > 0 ? 1.0F : 0.0F;
                  

                  break;
                default: 
                  weight = (sourceValue & 0xFF) >= sourceThreshold[s][b] ? 1.0F : 0.0F;
                }
                
                



                numerator += weight * (sourceValue & 0xFF);
                denominator += weight;
              }
            }
            

            if (denominator == 0.0D) {
              dBandData[dPixelOffset] = ((byte)background[b]);
            } else {
              dBandData[dPixelOffset] = ImageUtil.clampRoundByte(numerator / denominator);
            }
            


            dPixelOffset += dstPixelStride;
          }
        }
      }
    }
  }
  



  private void computeRectUShort(RasterAccessor[] src, RasterAccessor dst, RasterAccessor[] alfa, Raster[] roi)
  {
    int numSources = src.length;
    

    int[] srcLineStride = new int[numSources];
    int[] srcPixelStride = new int[numSources];
    int[][] srcBandOffsets = new int[numSources][];
    short[][][] srcData = new short[numSources][][];
    

    for (int i = 0; i < numSources; i++) {
      if (src[i] != null) {
        srcLineStride[i] = src[i].getScanlineStride();
        srcPixelStride[i] = src[i].getPixelStride();
        srcBandOffsets[i] = src[i].getBandOffsets();
        srcData[i] = src[i].getShortDataArrays();
      }
    }
    

    int dstMinX = dst.getX();
    int dstMinY = dst.getY();
    int dstWidth = dst.getWidth();
    int dstHeight = dst.getHeight();
    int dstMaxX = dstMinX + dstWidth;
    int dstMaxY = dstMinY + dstHeight;
    int dstBands = dst.getNumBands();
    int dstLineStride = dst.getScanlineStride();
    int dstPixelStride = dst.getPixelStride();
    int[] dstBandOffsets = dst.getBandOffsets();
    short[][] dstData = dst.getShortDataArrays();
    

    boolean hasAlpha = false;
    for (int i = 0; i < numSources; i++) {
      if (alfa[i] != null) {
        hasAlpha = true;
        break;
      }
    }
    

    int[] alfaLineStride = null;
    int[] alfaPixelStride = null;
    int[][] alfaBandOffsets = (int[][])null;
    short[][][] alfaData = (short[][][])null;
    
    if (hasAlpha)
    {
      alfaLineStride = new int[numSources];
      alfaPixelStride = new int[numSources];
      alfaBandOffsets = new int[numSources][];
      alfaData = new short[numSources][][];
      

      for (int i = 0; i < numSources; i++) {
        if (alfa[i] != null) {
          alfaLineStride[i] = alfa[i].getScanlineStride();
          alfaPixelStride[i] = alfa[i].getPixelStride();
          alfaBandOffsets[i] = alfa[i].getBandOffsets();
          alfaData[i] = alfa[i].getShortDataArrays();
        }
      }
    }
    

    int[] weightTypes = new int[numSources];
    for (int i = 0; i < numSources; i++) {
      weightTypes[i] = 3;
      if (alfa[i] != null) {
        weightTypes[i] = 1;
      } else if ((sourceROI != null) && (sourceROI[i] != null)) {
        weightTypes[i] = 2;
      }
    }
    

    int[] sLineOffsets = new int[numSources];
    int[] sPixelOffsets = new int[numSources];
    short[][] sBandData = new short[numSources][];
    

    int[] aLineOffsets = null;
    int[] aPixelOffsets = null;
    short[][] aBandData = (short[][])null;
    if (hasAlpha) {
      aLineOffsets = new int[numSources];
      aPixelOffsets = new int[numSources];
      aBandData = new short[numSources][];
    }
    
    for (int b = 0; b < dstBands; b++)
    {
      for (int s = 0; s < numSources; s++) {
        if (src[s] != null) {
          sBandData[s] = srcData[s][b];
          sLineOffsets[s] = srcBandOffsets[s][b];
        }
        if (weightTypes[s] == 1) {
          aBandData[s] = alfaData[s][0];
          aLineOffsets[s] = alfaBandOffsets[s][0];
        }
      }
      

      short[] dBandData = dstData[b];
      int dLineOffset = dstBandOffsets[b];
      
      if (mosaicType == MosaicDescriptor.MOSAIC_TYPE_OVERLAY) {
        for (int dstY = dstMinY; dstY < dstMaxY; dstY++)
        {

          for (int s = 0; s < numSources; s++) {
            if (src[s] != null) {
              sPixelOffsets[s] = sLineOffsets[s];
              sLineOffsets[s] += srcLineStride[s];
            }
            if (alfa[s] != null) {
              aPixelOffsets[s] = aLineOffsets[s];
              aLineOffsets[s] += alfaLineStride[s];
            }
          }
          


          int dPixelOffset = dLineOffset;
          dLineOffset += dstLineStride;
          
          for (int dstX = dstMinX; dstX < dstMaxX; dstX++)
          {

            boolean setDestValue = false;
            


            for (int s = 0; s < numSources; s++) {
              if (src[s] != null)
              {
                short sourceValue = sBandData[s][sPixelOffsets[s]];
                
                sPixelOffsets[s] += srcPixelStride[s];
                
                switch (weightTypes[s]) {
                case 1: 
                  setDestValue = aBandData[s][aPixelOffsets[s]] != 0;
                  
                  aPixelOffsets[s] += alfaPixelStride[s];
                  break;
                case 2: 
                  setDestValue = roi[s].getSample(dstX, dstY, 0) > 0;
                  
                  break;
                default: 
                  setDestValue = (sourceValue & 0xFFFF) >= sourceThreshold[s][b];
                }
                
                



                if (setDestValue) {
                  dBandData[dPixelOffset] = sourceValue;
                  

                  for (int k = s + 1; k < numSources; k++) {
                    if (src[k] != null) {
                      sPixelOffsets[k] += srcPixelStride[k];
                    }
                    if (alfa[k] != null) {
                      aPixelOffsets[k] += alfaPixelStride[k];
                    }
                  }
                  break;
                }
              }
            }
            

            if (!setDestValue) {
              dBandData[dPixelOffset] = ((short)background[b]);
            }
            
            dPixelOffset += dstPixelStride;
          }
        }
      } else {
        for (int dstY = dstMinY; dstY < dstMaxY; dstY++)
        {

          for (int s = 0; s < numSources; s++) {
            if (src[s] != null) {
              sPixelOffsets[s] = sLineOffsets[s];
              sLineOffsets[s] += srcLineStride[s];
            }
            if (weightTypes[s] == 1) {
              aPixelOffsets[s] = aLineOffsets[s];
              aLineOffsets[s] += alfaLineStride[s];
            }
          }
          


          int dPixelOffset = dLineOffset;
          dLineOffset += dstLineStride;
          
          for (int dstX = dstMinX; dstX < dstMaxX; dstX++)
          {

            float numerator = 0.0F;
            float denominator = 0.0F;
            

            for (int s = 0; s < numSources; s++) {
              if (src[s] != null)
              {
                short sourceValue = sBandData[s][sPixelOffsets[s]];
                
                sPixelOffsets[s] += srcPixelStride[s];
                
                float weight = 0.0F;
                switch (weightTypes[s]) {
                case 1: 
                  weight = aBandData[s][aPixelOffsets[s]] & 0xFFFF;
                  if ((weight > 0.0F) && (isAlphaBitmask)) {
                    weight = 1.0F;
                  } else {
                    weight /= 65535.0F;
                  }
                  aPixelOffsets[s] += alfaPixelStride[s];
                  break;
                case 2: 
                  weight = roi[s].getSample(dstX, dstY, 0) > 0 ? 1.0F : 0.0F;
                  

                  break;
                default: 
                  weight = (sourceValue & 0xFFFF) >= sourceThreshold[s][b] ? 1.0F : 0.0F;
                }
                
                



                numerator += weight * (sourceValue & 0xFFFF);
                denominator += weight;
              }
            }
            

            if (denominator == 0.0D) {
              dBandData[dPixelOffset] = ((short)background[b]);
            } else {
              dBandData[dPixelOffset] = ImageUtil.clampRoundUShort(numerator / denominator);
            }
            


            dPixelOffset += dstPixelStride;
          }
        }
      }
    }
  }
  



  private void computeRectShort(RasterAccessor[] src, RasterAccessor dst, RasterAccessor[] alfa, Raster[] roi)
  {
    int numSources = src.length;
    

    int[] srcLineStride = new int[numSources];
    int[] srcPixelStride = new int[numSources];
    int[][] srcBandOffsets = new int[numSources][];
    short[][][] srcData = new short[numSources][][];
    

    for (int i = 0; i < numSources; i++) {
      if (src[i] != null) {
        srcLineStride[i] = src[i].getScanlineStride();
        srcPixelStride[i] = src[i].getPixelStride();
        srcBandOffsets[i] = src[i].getBandOffsets();
        srcData[i] = src[i].getShortDataArrays();
      }
    }
    

    int dstMinX = dst.getX();
    int dstMinY = dst.getY();
    int dstWidth = dst.getWidth();
    int dstHeight = dst.getHeight();
    int dstMaxX = dstMinX + dstWidth;
    int dstMaxY = dstMinY + dstHeight;
    int dstBands = dst.getNumBands();
    int dstLineStride = dst.getScanlineStride();
    int dstPixelStride = dst.getPixelStride();
    int[] dstBandOffsets = dst.getBandOffsets();
    short[][] dstData = dst.getShortDataArrays();
    

    boolean hasAlpha = false;
    for (int i = 0; i < numSources; i++) {
      if (alfa[i] != null) {
        hasAlpha = true;
        break;
      }
    }
    

    int[] alfaLineStride = null;
    int[] alfaPixelStride = null;
    int[][] alfaBandOffsets = (int[][])null;
    short[][][] alfaData = (short[][][])null;
    
    if (hasAlpha)
    {
      alfaLineStride = new int[numSources];
      alfaPixelStride = new int[numSources];
      alfaBandOffsets = new int[numSources][];
      alfaData = new short[numSources][][];
      

      for (int i = 0; i < numSources; i++) {
        if (alfa[i] != null) {
          alfaLineStride[i] = alfa[i].getScanlineStride();
          alfaPixelStride[i] = alfa[i].getPixelStride();
          alfaBandOffsets[i] = alfa[i].getBandOffsets();
          alfaData[i] = alfa[i].getShortDataArrays();
        }
      }
    }
    

    int[] weightTypes = new int[numSources];
    for (int i = 0; i < numSources; i++) {
      weightTypes[i] = 3;
      if (alfa[i] != null) {
        weightTypes[i] = 1;
      } else if ((sourceROI != null) && (sourceROI[i] != null)) {
        weightTypes[i] = 2;
      }
    }
    

    int[] sLineOffsets = new int[numSources];
    int[] sPixelOffsets = new int[numSources];
    short[][] sBandData = new short[numSources][];
    

    int[] aLineOffsets = null;
    int[] aPixelOffsets = null;
    short[][] aBandData = (short[][])null;
    if (hasAlpha) {
      aLineOffsets = new int[numSources];
      aPixelOffsets = new int[numSources];
      aBandData = new short[numSources][];
    }
    
    for (int b = 0; b < dstBands; b++)
    {
      for (int s = 0; s < numSources; s++) {
        if (src[s] != null) {
          sBandData[s] = srcData[s][b];
          sLineOffsets[s] = srcBandOffsets[s][b];
        }
        if (weightTypes[s] == 1) {
          aBandData[s] = alfaData[s][0];
          aLineOffsets[s] = alfaBandOffsets[s][0];
        }
      }
      

      short[] dBandData = dstData[b];
      int dLineOffset = dstBandOffsets[b];
      
      if (mosaicType == MosaicDescriptor.MOSAIC_TYPE_OVERLAY) {
        for (int dstY = dstMinY; dstY < dstMaxY; dstY++)
        {

          for (int s = 0; s < numSources; s++) {
            if (src[s] != null) {
              sPixelOffsets[s] = sLineOffsets[s];
              sLineOffsets[s] += srcLineStride[s];
            }
            if (alfa[s] != null) {
              aPixelOffsets[s] = aLineOffsets[s];
              aLineOffsets[s] += alfaLineStride[s];
            }
          }
          


          int dPixelOffset = dLineOffset;
          dLineOffset += dstLineStride;
          
          for (int dstX = dstMinX; dstX < dstMaxX; dstX++)
          {

            boolean setDestValue = false;
            


            for (int s = 0; s < numSources; s++) {
              if (src[s] != null)
              {
                short sourceValue = sBandData[s][sPixelOffsets[s]];
                
                sPixelOffsets[s] += srcPixelStride[s];
                
                switch (weightTypes[s]) {
                case 1: 
                  setDestValue = aBandData[s][aPixelOffsets[s]] != 0;
                  
                  aPixelOffsets[s] += alfaPixelStride[s];
                  break;
                case 2: 
                  setDestValue = roi[s].getSample(dstX, dstY, 0) > 0;
                  
                  break;
                default: 
                  setDestValue = sourceValue >= sourceThreshold[s][b];
                }
                
                



                if (setDestValue) {
                  dBandData[dPixelOffset] = sourceValue;
                  

                  for (int k = s + 1; k < numSources; k++) {
                    if (src[k] != null) {
                      sPixelOffsets[k] += srcPixelStride[k];
                    }
                    if (alfa[k] != null) {
                      aPixelOffsets[k] += alfaPixelStride[k];
                    }
                  }
                  break;
                }
              }
            }
            

            if (!setDestValue) {
              dBandData[dPixelOffset] = ((short)background[b]);
            }
            
            dPixelOffset += dstPixelStride;
          }
        }
      } else {
        for (int dstY = dstMinY; dstY < dstMaxY; dstY++)
        {

          for (int s = 0; s < numSources; s++) {
            if (src[s] != null) {
              sPixelOffsets[s] = sLineOffsets[s];
              sLineOffsets[s] += srcLineStride[s];
            }
            if (weightTypes[s] == 1) {
              aPixelOffsets[s] = aLineOffsets[s];
              aLineOffsets[s] += alfaLineStride[s];
            }
          }
          


          int dPixelOffset = dLineOffset;
          dLineOffset += dstLineStride;
          
          for (int dstX = dstMinX; dstX < dstMaxX; dstX++)
          {

            float numerator = 0.0F;
            float denominator = 0.0F;
            

            for (int s = 0; s < numSources; s++) {
              if (src[s] != null)
              {
                short sourceValue = sBandData[s][sPixelOffsets[s]];
                
                sPixelOffsets[s] += srcPixelStride[s];
                
                float weight = 0.0F;
                switch (weightTypes[s]) {
                case 1: 
                  weight = aBandData[s][aPixelOffsets[s]];
                  if ((weight > 0.0F) && (isAlphaBitmask)) {
                    weight = 1.0F;
                  } else {
                    weight /= 32767.0F;
                  }
                  aPixelOffsets[s] += alfaPixelStride[s];
                  break;
                case 2: 
                  weight = roi[s].getSample(dstX, dstY, 0) > 0 ? 1.0F : 0.0F;
                  

                  break;
                default: 
                  weight = sourceValue >= sourceThreshold[s][b] ? 1.0F : 0.0F;
                }
                
                



                numerator += weight * sourceValue;
                denominator += weight;
              }
            }
            

            if (denominator == 0.0D) {
              dBandData[dPixelOffset] = ((short)background[b]);
            } else {
              dBandData[dPixelOffset] = ImageUtil.clampRoundShort(numerator / denominator);
            }
            


            dPixelOffset += dstPixelStride;
          }
        }
      }
    }
  }
  



  private void computeRectInt(RasterAccessor[] src, RasterAccessor dst, RasterAccessor[] alfa, Raster[] roi)
  {
    int numSources = src.length;
    

    int[] srcLineStride = new int[numSources];
    int[] srcPixelStride = new int[numSources];
    int[][] srcBandOffsets = new int[numSources][];
    int[][][] srcData = new int[numSources][][];
    

    for (int i = 0; i < numSources; i++) {
      if (src[i] != null) {
        srcLineStride[i] = src[i].getScanlineStride();
        srcPixelStride[i] = src[i].getPixelStride();
        srcBandOffsets[i] = src[i].getBandOffsets();
        srcData[i] = src[i].getIntDataArrays();
      }
    }
    

    int dstMinX = dst.getX();
    int dstMinY = dst.getY();
    int dstWidth = dst.getWidth();
    int dstHeight = dst.getHeight();
    int dstMaxX = dstMinX + dstWidth;
    int dstMaxY = dstMinY + dstHeight;
    int dstBands = dst.getNumBands();
    int dstLineStride = dst.getScanlineStride();
    int dstPixelStride = dst.getPixelStride();
    int[] dstBandOffsets = dst.getBandOffsets();
    int[][] dstData = dst.getIntDataArrays();
    

    boolean hasAlpha = false;
    for (int i = 0; i < numSources; i++) {
      if (alfa[i] != null) {
        hasAlpha = true;
        break;
      }
    }
    

    int[] alfaLineStride = null;
    int[] alfaPixelStride = null;
    int[][] alfaBandOffsets = (int[][])null;
    int[][][] alfaData = (int[][][])null;
    
    if (hasAlpha)
    {
      alfaLineStride = new int[numSources];
      alfaPixelStride = new int[numSources];
      alfaBandOffsets = new int[numSources][];
      alfaData = new int[numSources][][];
      

      for (int i = 0; i < numSources; i++) {
        if (alfa[i] != null) {
          alfaLineStride[i] = alfa[i].getScanlineStride();
          alfaPixelStride[i] = alfa[i].getPixelStride();
          alfaBandOffsets[i] = alfa[i].getBandOffsets();
          alfaData[i] = alfa[i].getIntDataArrays();
        }
      }
    }
    

    int[] weightTypes = new int[numSources];
    for (int i = 0; i < numSources; i++) {
      weightTypes[i] = 3;
      if (alfa[i] != null) {
        weightTypes[i] = 1;
      } else if ((sourceROI != null) && (sourceROI[i] != null)) {
        weightTypes[i] = 2;
      }
    }
    

    int[] sLineOffsets = new int[numSources];
    int[] sPixelOffsets = new int[numSources];
    int[][] sBandData = new int[numSources][];
    

    int[] aLineOffsets = null;
    int[] aPixelOffsets = null;
    int[][] aBandData = (int[][])null;
    if (hasAlpha) {
      aLineOffsets = new int[numSources];
      aPixelOffsets = new int[numSources];
      aBandData = new int[numSources][];
    }
    
    for (int b = 0; b < dstBands; b++)
    {
      for (int s = 0; s < numSources; s++) {
        if (src[s] != null) {
          sBandData[s] = srcData[s][b];
          sLineOffsets[s] = srcBandOffsets[s][b];
        }
        if (weightTypes[s] == 1) {
          aBandData[s] = alfaData[s][0];
          aLineOffsets[s] = alfaBandOffsets[s][0];
        }
      }
      

      int[] dBandData = dstData[b];
      int dLineOffset = dstBandOffsets[b];
      
      if (mosaicType == MosaicDescriptor.MOSAIC_TYPE_OVERLAY) {
        for (int dstY = dstMinY; dstY < dstMaxY; dstY++)
        {

          for (int s = 0; s < numSources; s++) {
            if (src[s] != null) {
              sPixelOffsets[s] = sLineOffsets[s];
              sLineOffsets[s] += srcLineStride[s];
            }
            if (alfa[s] != null) {
              aPixelOffsets[s] = aLineOffsets[s];
              aLineOffsets[s] += alfaLineStride[s];
            }
          }
          


          int dPixelOffset = dLineOffset;
          dLineOffset += dstLineStride;
          
          for (int dstX = dstMinX; dstX < dstMaxX; dstX++)
          {

            boolean setDestValue = false;
            


            for (int s = 0; s < numSources; s++) {
              if (src[s] != null)
              {
                int sourceValue = sBandData[s][sPixelOffsets[s]];
                
                sPixelOffsets[s] += srcPixelStride[s];
                
                switch (weightTypes[s]) {
                case 1: 
                  setDestValue = aBandData[s][aPixelOffsets[s]] != 0;
                  
                  aPixelOffsets[s] += alfaPixelStride[s];
                  break;
                case 2: 
                  setDestValue = roi[s].getSample(dstX, dstY, 0) > 0;
                  
                  break;
                default: 
                  setDestValue = sourceValue >= sourceThreshold[s][b];
                }
                
                



                if (setDestValue) {
                  dBandData[dPixelOffset] = sourceValue;
                  

                  for (int k = s + 1; k < numSources; k++) {
                    if (src[k] != null) {
                      sPixelOffsets[k] += srcPixelStride[k];
                    }
                    if (alfa[k] != null) {
                      aPixelOffsets[k] += alfaPixelStride[k];
                    }
                  }
                  break;
                }
              }
            }
            

            if (!setDestValue) {
              dBandData[dPixelOffset] = background[b];
            }
            
            dPixelOffset += dstPixelStride;
          }
        }
      } else {
        for (int dstY = dstMinY; dstY < dstMaxY; dstY++)
        {

          for (int s = 0; s < numSources; s++) {
            if (src[s] != null) {
              sPixelOffsets[s] = sLineOffsets[s];
              sLineOffsets[s] += srcLineStride[s];
            }
            if (weightTypes[s] == 1) {
              aPixelOffsets[s] = aLineOffsets[s];
              aLineOffsets[s] += alfaLineStride[s];
            }
          }
          


          int dPixelOffset = dLineOffset;
          dLineOffset += dstLineStride;
          
          for (int dstX = dstMinX; dstX < dstMaxX; dstX++)
          {

            double numerator = 0.0D;
            double denominator = 0.0D;
            

            for (int s = 0; s < numSources; s++) {
              if (src[s] != null)
              {
                int sourceValue = sBandData[s][sPixelOffsets[s]];
                
                sPixelOffsets[s] += srcPixelStride[s];
                
                double weight = 0.0D;
                switch (weightTypes[s]) {
                case 1: 
                  weight = aBandData[s][aPixelOffsets[s]];
                  if ((weight > 0.0D) && (isAlphaBitmask)) {
                    weight = 1.0D;
                  } else {
                    weight /= 2.147483647E9D;
                  }
                  aPixelOffsets[s] += alfaPixelStride[s];
                  break;
                case 2: 
                  weight = roi[s].getSample(dstX, dstY, 0) > 0 ? 1.0D : 0.0D;
                  

                  break;
                default: 
                  weight = sourceValue >= sourceThreshold[s][b] ? 1.0D : 0.0D;
                }
                
                



                numerator += weight * sourceValue;
                denominator += weight;
              }
            }
            

            if (denominator == 0.0D) {
              dBandData[dPixelOffset] = background[b];
            } else {
              dBandData[dPixelOffset] = ImageUtil.clampRoundInt(numerator / denominator);
            }
            


            dPixelOffset += dstPixelStride;
          }
        }
      }
    }
  }
  



  private void computeRectFloat(RasterAccessor[] src, RasterAccessor dst, RasterAccessor[] alfa, Raster[] roi)
  {
    int numSources = src.length;
    

    int[] srcLineStride = new int[numSources];
    int[] srcPixelStride = new int[numSources];
    int[][] srcBandOffsets = new int[numSources][];
    float[][][] srcData = new float[numSources][][];
    

    for (int i = 0; i < numSources; i++) {
      if (src[i] != null) {
        srcLineStride[i] = src[i].getScanlineStride();
        srcPixelStride[i] = src[i].getPixelStride();
        srcBandOffsets[i] = src[i].getBandOffsets();
        srcData[i] = src[i].getFloatDataArrays();
      }
    }
    

    int dstMinX = dst.getX();
    int dstMinY = dst.getY();
    int dstWidth = dst.getWidth();
    int dstHeight = dst.getHeight();
    int dstMaxX = dstMinX + dstWidth;
    int dstMaxY = dstMinY + dstHeight;
    int dstBands = dst.getNumBands();
    int dstLineStride = dst.getScanlineStride();
    int dstPixelStride = dst.getPixelStride();
    int[] dstBandOffsets = dst.getBandOffsets();
    float[][] dstData = dst.getFloatDataArrays();
    

    boolean hasAlpha = false;
    for (int i = 0; i < numSources; i++) {
      if (alfa[i] != null) {
        hasAlpha = true;
        break;
      }
    }
    

    int[] alfaLineStride = null;
    int[] alfaPixelStride = null;
    int[][] alfaBandOffsets = (int[][])null;
    float[][][] alfaData = (float[][][])null;
    
    if (hasAlpha)
    {
      alfaLineStride = new int[numSources];
      alfaPixelStride = new int[numSources];
      alfaBandOffsets = new int[numSources][];
      alfaData = new float[numSources][][];
      

      for (int i = 0; i < numSources; i++) {
        if (alfa[i] != null) {
          alfaLineStride[i] = alfa[i].getScanlineStride();
          alfaPixelStride[i] = alfa[i].getPixelStride();
          alfaBandOffsets[i] = alfa[i].getBandOffsets();
          alfaData[i] = alfa[i].getFloatDataArrays();
        }
      }
    }
    

    int[] weightTypes = new int[numSources];
    for (int i = 0; i < numSources; i++) {
      weightTypes[i] = 3;
      if (alfa[i] != null) {
        weightTypes[i] = 1;
      } else if ((sourceROI != null) && (sourceROI[i] != null)) {
        weightTypes[i] = 2;
      }
    }
    

    int[] sLineOffsets = new int[numSources];
    int[] sPixelOffsets = new int[numSources];
    float[][] sBandData = new float[numSources][];
    

    int[] aLineOffsets = null;
    int[] aPixelOffsets = null;
    float[][] aBandData = (float[][])null;
    if (hasAlpha) {
      aLineOffsets = new int[numSources];
      aPixelOffsets = new int[numSources];
      aBandData = new float[numSources][];
    }
    
    for (int b = 0; b < dstBands; b++)
    {
      for (int s = 0; s < numSources; s++) {
        if (src[s] != null) {
          sBandData[s] = srcData[s][b];
          sLineOffsets[s] = srcBandOffsets[s][b];
        }
        if (weightTypes[s] == 1) {
          aBandData[s] = alfaData[s][0];
          aLineOffsets[s] = alfaBandOffsets[s][0];
        }
      }
      

      float[] dBandData = dstData[b];
      int dLineOffset = dstBandOffsets[b];
      
      if (mosaicType == MosaicDescriptor.MOSAIC_TYPE_OVERLAY) {
        for (int dstY = dstMinY; dstY < dstMaxY; dstY++)
        {

          for (int s = 0; s < numSources; s++) {
            if (src[s] != null) {
              sPixelOffsets[s] = sLineOffsets[s];
              sLineOffsets[s] += srcLineStride[s];
            }
            if (alfa[s] != null) {
              aPixelOffsets[s] = aLineOffsets[s];
              aLineOffsets[s] += alfaLineStride[s];
            }
          }
          


          int dPixelOffset = dLineOffset;
          dLineOffset += dstLineStride;
          
          for (int dstX = dstMinX; dstX < dstMaxX; dstX++)
          {

            boolean setDestValue = false;
            


            for (int s = 0; s < numSources; s++) {
              if (src[s] != null)
              {
                float sourceValue = sBandData[s][sPixelOffsets[s]];
                
                sPixelOffsets[s] += srcPixelStride[s];
                
                switch (weightTypes[s]) {
                case 1: 
                  setDestValue = aBandData[s][aPixelOffsets[s]] != 0.0F;
                  
                  aPixelOffsets[s] += alfaPixelStride[s];
                  break;
                case 2: 
                  setDestValue = roi[s].getSample(dstX, dstY, 0) > 0;
                  
                  break;
                default: 
                  setDestValue = sourceValue >= sourceThreshold[s][b];
                }
                
                



                if (setDestValue) {
                  dBandData[dPixelOffset] = sourceValue;
                  

                  for (int k = s + 1; k < numSources; k++) {
                    if (src[k] != null) {
                      sPixelOffsets[k] += srcPixelStride[k];
                    }
                    if (alfa[k] != null) {
                      aPixelOffsets[k] += alfaPixelStride[k];
                    }
                  }
                  break;
                }
              }
            }
            

            if (!setDestValue) {
              dBandData[dPixelOffset] = ((float)backgroundValues[b]);
            }
            
            dPixelOffset += dstPixelStride;
          }
        }
      } else {
        for (int dstY = dstMinY; dstY < dstMaxY; dstY++)
        {

          for (int s = 0; s < numSources; s++) {
            if (src[s] != null) {
              sPixelOffsets[s] = sLineOffsets[s];
              sLineOffsets[s] += srcLineStride[s];
            }
            if (weightTypes[s] == 1) {
              aPixelOffsets[s] = aLineOffsets[s];
              aLineOffsets[s] += alfaLineStride[s];
            }
          }
          


          int dPixelOffset = dLineOffset;
          dLineOffset += dstLineStride;
          
          for (int dstX = dstMinX; dstX < dstMaxX; dstX++)
          {

            float numerator = 0.0F;
            float denominator = 0.0F;
            

            for (int s = 0; s < numSources; s++) {
              if (src[s] != null)
              {
                float sourceValue = sBandData[s][sPixelOffsets[s]];
                
                sPixelOffsets[s] += srcPixelStride[s];
                
                float weight = 0.0F;
                switch (weightTypes[s]) {
                case 1: 
                  weight = aBandData[s][aPixelOffsets[s]];
                  if ((weight > 0.0F) && (isAlphaBitmask)) {
                    weight = 1.0F;
                  }
                  aPixelOffsets[s] += alfaPixelStride[s];
                  break;
                case 2: 
                  weight = roi[s].getSample(dstX, dstY, 0) > 0 ? 1.0F : 0.0F;
                  

                  break;
                default: 
                  weight = sourceValue >= sourceThreshold[s][b] ? 1.0F : 0.0F;
                }
                
                



                numerator += weight * sourceValue;
                denominator += weight;
              }
            }
            

            if (denominator == 0.0D) {
              dBandData[dPixelOffset] = ((float)backgroundValues[b]);
            } else {
              dBandData[dPixelOffset] = (numerator / denominator);
            }
            


            dPixelOffset += dstPixelStride;
          }
        }
      }
    }
  }
  



  private void computeRectDouble(RasterAccessor[] src, RasterAccessor dst, RasterAccessor[] alfa, Raster[] roi)
  {
    int numSources = src.length;
    

    int[] srcLineStride = new int[numSources];
    int[] srcPixelStride = new int[numSources];
    int[][] srcBandOffsets = new int[numSources][];
    double[][][] srcData = new double[numSources][][];
    

    for (int i = 0; i < numSources; i++) {
      if (src[i] != null) {
        srcLineStride[i] = src[i].getScanlineStride();
        srcPixelStride[i] = src[i].getPixelStride();
        srcBandOffsets[i] = src[i].getBandOffsets();
        srcData[i] = src[i].getDoubleDataArrays();
      }
    }
    

    int dstMinX = dst.getX();
    int dstMinY = dst.getY();
    int dstWidth = dst.getWidth();
    int dstHeight = dst.getHeight();
    int dstMaxX = dstMinX + dstWidth;
    int dstMaxY = dstMinY + dstHeight;
    int dstBands = dst.getNumBands();
    int dstLineStride = dst.getScanlineStride();
    int dstPixelStride = dst.getPixelStride();
    int[] dstBandOffsets = dst.getBandOffsets();
    double[][] dstData = dst.getDoubleDataArrays();
    

    boolean hasAlpha = false;
    for (int i = 0; i < numSources; i++) {
      if (alfa[i] != null) {
        hasAlpha = true;
        break;
      }
    }
    

    int[] alfaLineStride = null;
    int[] alfaPixelStride = null;
    int[][] alfaBandOffsets = (int[][])null;
    double[][][] alfaData = (double[][][])null;
    
    if (hasAlpha)
    {
      alfaLineStride = new int[numSources];
      alfaPixelStride = new int[numSources];
      alfaBandOffsets = new int[numSources][];
      alfaData = new double[numSources][][];
      

      for (int i = 0; i < numSources; i++) {
        if (alfa[i] != null) {
          alfaLineStride[i] = alfa[i].getScanlineStride();
          alfaPixelStride[i] = alfa[i].getPixelStride();
          alfaBandOffsets[i] = alfa[i].getBandOffsets();
          alfaData[i] = alfa[i].getDoubleDataArrays();
        }
      }
    }
    

    int[] weightTypes = new int[numSources];
    for (int i = 0; i < numSources; i++) {
      weightTypes[i] = 3;
      if (alfa[i] != null) {
        weightTypes[i] = 1;
      } else if ((sourceROI != null) && (sourceROI[i] != null)) {
        weightTypes[i] = 2;
      }
    }
    

    int[] sLineOffsets = new int[numSources];
    int[] sPixelOffsets = new int[numSources];
    double[][] sBandData = new double[numSources][];
    

    int[] aLineOffsets = null;
    int[] aPixelOffsets = null;
    double[][] aBandData = (double[][])null;
    if (hasAlpha) {
      aLineOffsets = new int[numSources];
      aPixelOffsets = new int[numSources];
      aBandData = new double[numSources][];
    }
    
    for (int b = 0; b < dstBands; b++)
    {
      for (int s = 0; s < numSources; s++) {
        if (src[s] != null) {
          sBandData[s] = srcData[s][b];
          sLineOffsets[s] = srcBandOffsets[s][b];
        }
        if (weightTypes[s] == 1) {
          aBandData[s] = alfaData[s][0];
          aLineOffsets[s] = alfaBandOffsets[s][0];
        }
      }
      

      double[] dBandData = dstData[b];
      int dLineOffset = dstBandOffsets[b];
      
      if (mosaicType == MosaicDescriptor.MOSAIC_TYPE_OVERLAY) {
        for (int dstY = dstMinY; dstY < dstMaxY; dstY++)
        {

          for (int s = 0; s < numSources; s++) {
            if (src[s] != null) {
              sPixelOffsets[s] = sLineOffsets[s];
              sLineOffsets[s] += srcLineStride[s];
            }
            if (alfa[s] != null) {
              aPixelOffsets[s] = aLineOffsets[s];
              aLineOffsets[s] += alfaLineStride[s];
            }
          }
          


          int dPixelOffset = dLineOffset;
          dLineOffset += dstLineStride;
          
          for (int dstX = dstMinX; dstX < dstMaxX; dstX++)
          {

            boolean setDestValue = false;
            


            for (int s = 0; s < numSources; s++) {
              if (src[s] != null)
              {
                double sourceValue = sBandData[s][sPixelOffsets[s]];
                
                sPixelOffsets[s] += srcPixelStride[s];
                
                switch (weightTypes[s]) {
                case 1: 
                  setDestValue = aBandData[s][aPixelOffsets[s]] != 0.0D;
                  
                  aPixelOffsets[s] += alfaPixelStride[s];
                  break;
                case 2: 
                  setDestValue = roi[s].getSample(dstX, dstY, 0) > 0;
                  
                  break;
                default: 
                  setDestValue = sourceValue >= sourceThreshold[s][b];
                }
                
                



                if (setDestValue) {
                  dBandData[dPixelOffset] = sourceValue;
                  

                  for (int k = s + 1; k < numSources; k++) {
                    if (src[k] != null) {
                      sPixelOffsets[k] += srcPixelStride[k];
                    }
                    if (alfa[k] != null) {
                      aPixelOffsets[k] += alfaPixelStride[k];
                    }
                  }
                  break;
                }
              }
            }
            

            if (!setDestValue) {
              dBandData[dPixelOffset] = backgroundValues[b];
            }
            
            dPixelOffset += dstPixelStride;
          }
        }
      } else {
        for (int dstY = dstMinY; dstY < dstMaxY; dstY++)
        {

          for (int s = 0; s < numSources; s++) {
            if (src[s] != null) {
              sPixelOffsets[s] = sLineOffsets[s];
              sLineOffsets[s] += srcLineStride[s];
            }
            if (weightTypes[s] == 1) {
              aPixelOffsets[s] = aLineOffsets[s];
              aLineOffsets[s] += alfaLineStride[s];
            }
          }
          


          int dPixelOffset = dLineOffset;
          dLineOffset += dstLineStride;
          
          for (int dstX = dstMinX; dstX < dstMaxX; dstX++)
          {

            double numerator = 0.0D;
            double denominator = 0.0D;
            

            for (int s = 0; s < numSources; s++) {
              if (src[s] != null)
              {
                double sourceValue = sBandData[s][sPixelOffsets[s]];
                
                sPixelOffsets[s] += srcPixelStride[s];
                
                double weight = 0.0D;
                switch (weightTypes[s]) {
                case 1: 
                  weight = aBandData[s][aPixelOffsets[s]];
                  if ((weight > 0.0D) && (isAlphaBitmask)) {
                    weight = 1.0D;
                  }
                  aPixelOffsets[s] += alfaPixelStride[s];
                  break;
                case 2: 
                  weight = roi[s].getSample(dstX, dstY, 0) > 0 ? 1.0D : 0.0D;
                  

                  break;
                default: 
                  weight = sourceValue >= sourceThreshold[s][b] ? 1.0D : 0.0D;
                }
                
                



                numerator += weight * sourceValue;
                denominator += weight;
              }
            }
            

            if (denominator == 0.0D) {
              dBandData[dPixelOffset] = backgroundValues[b];
            } else {
              dBandData[dPixelOffset] = (numerator / denominator);
            }
            


            dPixelOffset += dstPixelStride;
          }
        }
      }
    }
  }
}
