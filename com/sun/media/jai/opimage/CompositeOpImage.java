package com.sun.media.jai.opimage;

import com.sun.media.jai.util.ImageUtil;
import com.sun.media.jai.util.JDKWorkarounds;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ImageLayout;
import javax.media.jai.PlanarImage;
import javax.media.jai.PointOpImage;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFactory;
import javax.media.jai.RasterFormatTag;



















































































final class CompositeOpImage
  extends PointOpImage
{
  protected RenderedImage source1Alpha;
  protected RenderedImage source2Alpha;
  protected boolean alphaPremultiplied;
  private int aOffset;
  private int cOffset;
  private byte maxValueByte;
  private short maxValueShort;
  private int maxValue;
  private float invMaxValue;
  
  public CompositeOpImage(RenderedImage source1, RenderedImage source2, Map config, ImageLayout layout, RenderedImage source1Alpha, RenderedImage source2Alpha, boolean alphaPremultiplied, boolean alphaFirst)
  {
    super(source1, source2, layout, config, true);
    
    this.source1Alpha = source1Alpha;
    this.source2Alpha = source2Alpha;
    this.alphaPremultiplied = alphaPremultiplied;
    
    SampleModel sm = source1.getSampleModel();
    ColorModel cm = source1.getColorModel();
    int dtype = sm.getTransferType();
    int bands;
    int bands; if ((cm instanceof IndexColorModel)) {
      bands = cm.getNumComponents();
    } else {
      bands = sm.getNumBands();
    }
    bands++;
    
    if ((sampleModel.getTransferType() != dtype) || (sampleModel.getNumBands() != bands))
    {





      sampleModel = RasterFactory.createComponentSampleModel(sampleModel, dtype, tileWidth, tileHeight, bands);
      

      if ((colorModel != null) && (!JDKWorkarounds.areCompatibleDataModels(sampleModel, colorModel)))
      {

        colorModel = ImageUtil.getCompatibleColorModel(sampleModel, config);
      }
    }
    

    aOffset = (alphaFirst ? 0 : bands - 1);
    cOffset = (alphaFirst ? 1 : 0);
    
    switch (dtype) {
    case 0: 
      maxValue = 255;
      maxValueByte = -1;
      break;
    case 1: 
      maxValue = 65535;
      maxValueShort = -1;
      break;
    case 2: 
      maxValue = 32767;
      maxValueShort = Short.MAX_VALUE;
      break;
    case 3: 
      maxValue = Integer.MAX_VALUE;
      break;
    }
    
    invMaxValue = (1.0F / maxValue);
  }
  










  protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect)
  {
    RenderedImage[] renderedSources = source2Alpha == null ? new RenderedImage[3] : new RenderedImage[4];
    

    renderedSources[0] = getSourceImage(0);
    renderedSources[1] = getSourceImage(1);
    renderedSources[2] = source1Alpha;
    Raster source1AlphaRaster = source1Alpha.getData(destRect);
    Raster source2AlphaRaster = null;
    if (source2Alpha != null) {
      renderedSources[3] = source2Alpha;
      source2AlphaRaster = source2Alpha.getData(destRect);
    }
    
    RasterFormatTag[] tags = RasterAccessor.findCompatibleTags(renderedSources, this);
    
    RasterAccessor s1 = new RasterAccessor(sources[0], destRect, tags[0], getSourceImage(0).getColorModel());
    
    RasterAccessor s2 = new RasterAccessor(sources[1], destRect, tags[1], getSourceImage(1).getColorModel());
    
    RasterAccessor a1 = new RasterAccessor(source1AlphaRaster, destRect, tags[2], source1Alpha.getColorModel());
    

    RasterAccessor a2 = null;RasterAccessor d = null;
    
    if (source2Alpha != null) {
      a2 = new RasterAccessor(source2AlphaRaster, destRect, tags[3], source2Alpha.getColorModel());
      
      d = new RasterAccessor(dest, destRect, tags[4], getColorModel());
    }
    else {
      a2 = null;
      d = new RasterAccessor(dest, destRect, tags[3], getColorModel());
    }
    

    switch (d.getDataType()) {
    case 0: 
      byteLoop(s1, s2, a1, a2, d);
      break;
    case 1: 
      ushortLoop(s1, s2, a1, a2, d);
      break;
    case 2: 
      shortLoop(s1, s2, a1, a2, d);
      break;
    case 3: 
      intLoop(s1, s2, a1, a2, d);
      break;
    case 4: 
      floatLoop(s1, s2, a1, a2, d);
      break;
    case 5: 
      doubleLoop(s1, s2, a1, a2, d);
    }
    
    d.copyDataToRaster();
  }
  

































  private void byteLoop(RasterAccessor src1, RasterAccessor src2, RasterAccessor afa1, RasterAccessor afa2, RasterAccessor dst)
  {
    int dwidth = dst.getWidth();
    int dheight = dst.getHeight();
    int numBands = src1.getNumBands();
    

    byte[][] s1 = src1.getByteDataArrays();
    int s1ss = src1.getScanlineStride();
    int s1ps = src1.getPixelStride();
    int[] s1bo = src1.getBandOffsets();
    

    byte[][] s2 = src2.getByteDataArrays();
    int s2ss = src2.getScanlineStride();
    int s2ps = src2.getPixelStride();
    int[] s2bo = src2.getBandOffsets();
    

    byte[] a1 = afa1.getByteDataArray(0);
    int a1ss = afa1.getScanlineStride();
    int a1ps = afa1.getPixelStride();
    int a1bo = afa1.getBandOffset(0);
    

    byte[] a2 = null;
    int a2ss = 0;
    int a2ps = 0;
    int a2bo = 0;
    if (afa2 != null) {
      a2 = afa2.getByteDataArray(0);
      a2ss = afa2.getScanlineStride();
      a2ps = afa2.getPixelStride();
      a2bo = afa2.getBandOffset(0);
    }
    

    byte[][] d = dst.getByteDataArrays();
    int dss = dst.getScanlineStride();
    int dps = dst.getPixelStride();
    int[] dbo = dst.getBandOffsets();
    
    int s1so = 0;int s2so = 0;int a1so = 0;int a2so = 0;int dso = 0;
    
    if (alphaPremultiplied) {
      if (afa2 == null) {
        for (int h = 0; h < dheight; h++) {
          int s1po = s1so;
          int s2po = s2so;
          int a1po = a1so;
          int dpo = dso;
          
          for (int w = 0; w < dwidth; w++) {
            float t = 1.0F - (a1[(a1po + a1bo)] & 0xFF) * invMaxValue;
            

            d[aOffset][(dpo + dbo[aOffset])] = maxValueByte;
            

            for (int b = 0; b < numBands; b++) {
              int i = b + cOffset;
              d[i][(dpo + dbo[i])] = ((byte)(int)((s1[b][(s1po + s1bo[b])] & 0xFF) + (s2[b][(s2po + s2bo[b])] & 0xFF) * t));
            }
            


            s1po += s1ps;
            s2po += s2ps;
            a1po += a1ps;
            dpo += dps;
          }
          
          s1so += s1ss;
          s2so += s2ss;
          a1so += a1ss;
          dso += dss;
        }
      } else {
        for (int h = 0; h < dheight; h++) {
          int s1po = s1so;
          int s2po = s2so;
          int a1po = a1so;
          int a2po = a2so;
          int dpo = dso;
          
          for (int w = 0; w < dwidth; w++) {
            int t1 = a1[(a1po + a1bo)] & 0xFF;
            float t2 = 1.0F - t1 * invMaxValue;
            

            d[aOffset][(dpo + dbo[aOffset])] = ((byte)(int)(t1 + (a2[(a2po + a2bo)] & 0xFF) * t2));
            


            for (int b = 0; b < numBands; b++) {
              int i = b + cOffset;
              d[i][(dpo + dbo[i])] = ((byte)(int)((s1[b][(s1po + s1bo[b])] & 0xFF) + (s2[b][(s2po + s2bo[b])] & 0xFF) * t2));
            }
            


            s1po += s1ps;
            s2po += s2ps;
            a1po += a1ps;
            a2po += a2ps;
            dpo += dps;
          }
          
          s1so += s1ss;
          s2so += s2ss;
          a1so += a1ss;
          a2so += a2ss;
          dso += dss;
        }
      }
    }
    else if (afa2 == null) {
      for (int h = 0; h < dheight; h++) {
        int s1po = s1so;
        int s2po = s2so;
        int a1po = a1so;
        int dpo = dso;
        
        for (int w = 0; w < dwidth; w++) {
          float t1 = (a1[(a1po + a1bo)] & 0xFF) * invMaxValue;
          float t2 = 1.0F - t1;
          

          d[aOffset][(dpo + dbo[aOffset])] = maxValueByte;
          

          for (int b = 0; b < numBands; b++) {
            int i = b + cOffset;
            d[i][(dpo + dbo[i])] = ((byte)(int)((s1[b][(s1po + s1bo[b])] & 0xFF) * t1 + (s2[b][(s2po + s2bo[b])] & 0xFF) * t2));
          }
          


          s1po += s1ps;
          s2po += s2ps;
          a1po += a1ps;
          dpo += dps;
        }
        
        s1so += s1ss;
        s2so += s2ss;
        a1so += a1ss;
        dso += dss;
      }
    } else {
      for (int h = 0; h < dheight; h++) {
        int s1po = s1so;
        int s2po = s2so;
        int a1po = a1so;
        int a2po = a2so;
        int dpo = dso;
        
        for (int w = 0; w < dwidth; w++) {
          int t1 = a1[(a1po + a1bo)] & 0xFF;
          float t2 = (1.0F - t1 * invMaxValue) * (a2[(a2po + a2bo)] & 0xFF);
          
          float t3 = t1 + t2;
          float t5;
          float t4; float t5; if (t3 == 0.0F) {
            float t4 = 0.0F;
            t5 = 0.0F;
          } else {
            t4 = t1 / t3;
            t5 = t2 / t3;
          }
          

          d[aOffset][(dpo + dbo[aOffset])] = ((byte)(int)t3);
          

          for (int b = 0; b < numBands; b++) {
            int i = b + cOffset;
            d[i][(dpo + dbo[i])] = ((byte)(int)((s1[b][(s1po + s1bo[b])] & 0xFF) * t4 + (s2[b][(s2po + s2bo[b])] & 0xFF) * t5));
          }
          


          s1po += s1ps;
          s2po += s2ps;
          a1po += a1ps;
          a2po += a2ps;
          dpo += dps;
        }
        
        s1so += s1ss;
        s2so += s2ss;
        a1so += a1ss;
        a2so += a2ss;
        dso += dss;
      }
    }
  }
  


  private void ushortLoop(RasterAccessor src1, RasterAccessor src2, RasterAccessor afa1, RasterAccessor afa2, RasterAccessor dst)
  {
    int dwidth = dst.getWidth();
    int dheight = dst.getHeight();
    int numBands = src1.getNumBands();
    

    short[][] s1 = src1.getShortDataArrays();
    int s1ss = src1.getScanlineStride();
    int s1ps = src1.getPixelStride();
    int[] s1bo = src1.getBandOffsets();
    

    short[][] s2 = src2.getShortDataArrays();
    int s2ss = src2.getScanlineStride();
    int s2ps = src2.getPixelStride();
    int[] s2bo = src2.getBandOffsets();
    

    short[] a1 = afa1.getShortDataArray(0);
    int a1ss = afa1.getScanlineStride();
    int a1ps = afa1.getPixelStride();
    int a1bo = afa1.getBandOffset(0);
    

    short[] a2 = null;
    int a2ss = 0;
    int a2ps = 0;
    int a2bo = 0;
    if (afa2 != null) {
      a2 = afa2.getShortDataArray(0);
      a2ss = afa2.getScanlineStride();
      a2ps = afa2.getPixelStride();
      a2bo = afa2.getBandOffset(0);
    }
    

    short[][] d = dst.getShortDataArrays();
    int dss = dst.getScanlineStride();
    int dps = dst.getPixelStride();
    int[] dbo = dst.getBandOffsets();
    
    int s1so = 0;int s2so = 0;int a1so = 0;int a2so = 0;int dso = 0;
    
    if (alphaPremultiplied) {
      if (afa2 == null) {
        for (int h = 0; h < dheight; h++) {
          int s1po = s1so;
          int s2po = s2so;
          int a1po = a1so;
          int dpo = dso;
          
          for (int w = 0; w < dwidth; w++) {
            float t = 1.0F - (a1[(a1po + a1bo)] & 0xFFFF) * invMaxValue;
            

            d[aOffset][(dpo + dbo[aOffset])] = maxValueShort;
            

            for (int b = 0; b < numBands; b++) {
              int i = b + cOffset;
              d[i][(dpo + dbo[i])] = ((short)(int)((s1[b][(s1po + s1bo[b])] & 0xFFFF) + (s2[b][(s2po + s2bo[b])] & 0xFFFF) * t));
            }
            


            s1po += s1ps;
            s2po += s2ps;
            a1po += a1ps;
            dpo += dps;
          }
          
          s1so += s1ss;
          s2so += s2ss;
          a1so += a1ss;
          dso += dss;
        }
      } else {
        for (int h = 0; h < dheight; h++) {
          int s1po = s1so;
          int s2po = s2so;
          int a1po = a1so;
          int a2po = a2so;
          int dpo = dso;
          
          for (int w = 0; w < dwidth; w++) {
            int t1 = a1[(a1po + a1bo)] & 0xFFFF;
            float t2 = 1.0F - t1 * invMaxValue;
            

            d[aOffset][(dpo + dbo[aOffset])] = ((short)(int)(t1 + (a2[(a2po + a2bo)] & 0xFFFF) * t2));
            


            for (int b = 0; b < numBands; b++) {
              int i = b + cOffset;
              d[i][(dpo + dbo[i])] = ((short)(int)((s1[b][(s1po + s1bo[b])] & 0xFFFF) + (s2[b][(s2po + s2bo[b])] & 0xFFFF) * t2));
            }
            


            s1po += s1ps;
            s2po += s2ps;
            a1po += a1ps;
            a2po += a2ps;
            dpo += dps;
          }
          
          s1so += s1ss;
          s2so += s2ss;
          a1so += a1ss;
          a2so += a2ss;
          dso += dss;
        }
      }
    }
    else if (afa2 == null) {
      for (int h = 0; h < dheight; h++) {
        int s1po = s1so;
        int s2po = s2so;
        int a1po = a1so;
        int dpo = dso;
        
        for (int w = 0; w < dwidth; w++) {
          float t1 = (a1[(a1po + a1bo)] & 0xFFFF) * invMaxValue;
          float t2 = 1.0F - t1;
          

          d[aOffset][(dpo + dbo[aOffset])] = maxValueShort;
          

          for (int b = 0; b < numBands; b++) {
            int i = b + cOffset;
            d[i][(dpo + dbo[i])] = ((short)(int)((s1[b][(s1po + s1bo[b])] & 0xFFFF) * t1 + (s2[b][(s2po + s2bo[b])] & 0xFFFF) * t2));
          }
          


          s1po += s1ps;
          s2po += s2ps;
          a1po += a1ps;
          dpo += dps;
        }
        
        s1so += s1ss;
        s2so += s2ss;
        a1so += a1ss;
        dso += dss;
      }
    } else {
      for (int h = 0; h < dheight; h++) {
        int s1po = s1so;
        int s2po = s2so;
        int a1po = a1so;
        int a2po = a2so;
        int dpo = dso;
        
        for (int w = 0; w < dwidth; w++) {
          int t1 = a1[(a1po + a1bo)] & 0xFFFF;
          float t2 = (1.0F - t1 * invMaxValue) * (a2[(a2po + a2bo)] & 0xFFFF);
          
          float t3 = t1 + t2;
          float t5;
          float t4; float t5; if (t3 == 0.0F) {
            float t4 = 0.0F;
            t5 = 0.0F;
          } else {
            t4 = t1 / t3;
            t5 = t2 / t3;
          }
          

          d[aOffset][(dpo + dbo[aOffset])] = ((short)(int)t3);
          

          for (int b = 0; b < numBands; b++) {
            int i = b + cOffset;
            d[i][(dpo + dbo[i])] = ((short)(int)((s1[b][(s1po + s1bo[b])] & 0xFFFF) * t4 + (s2[b][(s2po + s2bo[b])] & 0xFFFF) * t5));
          }
          


          s1po += s1ps;
          s2po += s2ps;
          a1po += a1ps;
          a2po += a2ps;
          dpo += dps;
        }
        
        s1so += s1ss;
        s2so += s2ss;
        a1so += a1ss;
        a2so += a2ss;
        dso += dss;
      }
    }
  }
  


  private void shortLoop(RasterAccessor src1, RasterAccessor src2, RasterAccessor afa1, RasterAccessor afa2, RasterAccessor dst)
  {
    int dwidth = dst.getWidth();
    int dheight = dst.getHeight();
    int numBands = src1.getNumBands();
    

    short[][] s1 = src1.getShortDataArrays();
    int s1ss = src1.getScanlineStride();
    int s1ps = src1.getPixelStride();
    int[] s1bo = src1.getBandOffsets();
    

    short[][] s2 = src2.getShortDataArrays();
    int s2ss = src2.getScanlineStride();
    int s2ps = src2.getPixelStride();
    int[] s2bo = src2.getBandOffsets();
    

    short[] a1 = afa1.getShortDataArray(0);
    int a1ss = afa1.getScanlineStride();
    int a1ps = afa1.getPixelStride();
    int a1bo = afa1.getBandOffset(0);
    

    short[] a2 = null;
    int a2ss = 0;
    int a2ps = 0;
    int a2bo = 0;
    if (afa2 != null) {
      a2 = afa2.getShortDataArray(0);
      a2ss = afa2.getScanlineStride();
      a2ps = afa2.getPixelStride();
      a2bo = afa2.getBandOffset(0);
    }
    

    short[][] d = dst.getShortDataArrays();
    int dss = dst.getScanlineStride();
    int dps = dst.getPixelStride();
    int[] dbo = dst.getBandOffsets();
    
    int s1so = 0;int s2so = 0;int a1so = 0;int a2so = 0;int dso = 0;
    
    if (alphaPremultiplied) {
      if (afa2 == null) {
        for (int h = 0; h < dheight; h++) {
          int s1po = s1so;
          int s2po = s2so;
          int a1po = a1so;
          int dpo = dso;
          
          for (int w = 0; w < dwidth; w++) {
            float t = 1.0F - a1[(a1po + a1bo)] * invMaxValue;
            

            d[aOffset][(dpo + dbo[aOffset])] = maxValueShort;
            

            for (int b = 0; b < numBands; b++) {
              int i = b + cOffset;
              d[i][(dpo + dbo[i])] = ((short)(int)(s1[b][(s1po + s1bo[b])] + s2[b][(s2po + s2bo[b])] * t));
            }
            

            s1po += s1ps;
            s2po += s2ps;
            a1po += a1ps;
            dpo += dps;
          }
          
          s1so += s1ss;
          s2so += s2ss;
          a1so += a1ss;
          dso += dss;
        }
      } else {
        for (int h = 0; h < dheight; h++) {
          int s1po = s1so;
          int s2po = s2so;
          int a1po = a1so;
          int a2po = a2so;
          int dpo = dso;
          
          for (int w = 0; w < dwidth; w++) {
            int t1 = a1[(a1po + a1bo)];
            float t2 = 1.0F - t1 * invMaxValue;
            

            d[aOffset][(dpo + dbo[aOffset])] = ((short)(int)(t1 + a2[(a2po + a2bo)] * t2));
            


            for (int b = 0; b < numBands; b++) {
              int i = b + cOffset;
              d[i][(dpo + dbo[i])] = ((short)(int)(s1[b][(s1po + s1bo[b])] + s2[b][(s2po + s2bo[b])] * t2));
            }
            


            s1po += s1ps;
            s2po += s2ps;
            a1po += a1ps;
            a2po += a2ps;
            dpo += dps;
          }
          
          s1so += s1ss;
          s2so += s2ss;
          a1so += a1ss;
          a2so += a2ss;
          dso += dss;
        }
      }
    }
    else if (afa2 == null) {
      for (int h = 0; h < dheight; h++) {
        int s1po = s1so;
        int s2po = s2so;
        int a1po = a1so;
        int dpo = dso;
        
        for (int w = 0; w < dwidth; w++) {
          float t1 = a1[(a1po + a1bo)] * invMaxValue;
          float t2 = 1.0F - t1;
          

          d[aOffset][(dpo + dbo[aOffset])] = maxValueShort;
          

          for (int b = 0; b < numBands; b++) {
            int i = b + cOffset;
            d[i][(dpo + dbo[i])] = ((short)(int)(s1[b][(s1po + s1bo[b])] * t1 + s2[b][(s2po + s2bo[b])] * t2));
          }
          


          s1po += s1ps;
          s2po += s2ps;
          a1po += a1ps;
          dpo += dps;
        }
        
        s1so += s1ss;
        s2so += s2ss;
        a1so += a1ss;
        dso += dss;
      }
    } else {
      for (int h = 0; h < dheight; h++) {
        int s1po = s1so;
        int s2po = s2so;
        int a1po = a1so;
        int a2po = a2so;
        int dpo = dso;
        
        for (int w = 0; w < dwidth; w++) {
          int t1 = a1[(a1po + a1bo)];
          float t2 = (1.0F - t1 * invMaxValue) * a2[(a2po + a2bo)];
          
          float t3 = t1 + t2;
          float t5;
          float t4; float t5; if (t3 == 0.0F) {
            float t4 = 0.0F;
            t5 = 0.0F;
          } else {
            t4 = t1 / t3;
            t5 = t2 / t3;
          }
          

          d[aOffset][(dpo + dbo[aOffset])] = ((short)(int)t3);
          

          for (int b = 0; b < numBands; b++) {
            int i = b + cOffset;
            d[i][(dpo + dbo[i])] = ((short)(int)(s1[b][(s1po + s1bo[b])] * t4 + s2[b][(s2po + s2bo[b])] * t5));
          }
          


          s1po += s1ps;
          s2po += s2ps;
          a1po += a1ps;
          a2po += a2ps;
          dpo += dps;
        }
        
        s1so += s1ss;
        s2so += s2ss;
        a1so += a1ss;
        a2so += a2ss;
        dso += dss;
      }
    }
  }
  


  private void intLoop(RasterAccessor src1, RasterAccessor src2, RasterAccessor afa1, RasterAccessor afa2, RasterAccessor dst)
  {
    int dwidth = dst.getWidth();
    int dheight = dst.getHeight();
    int numBands = src1.getNumBands();
    

    int[][] s1 = src1.getIntDataArrays();
    int s1ss = src1.getScanlineStride();
    int s1ps = src1.getPixelStride();
    int[] s1bo = src1.getBandOffsets();
    

    int[][] s2 = src2.getIntDataArrays();
    int s2ss = src2.getScanlineStride();
    int s2ps = src2.getPixelStride();
    int[] s2bo = src2.getBandOffsets();
    

    int[] a1 = afa1.getIntDataArray(0);
    int a1ss = afa1.getScanlineStride();
    int a1ps = afa1.getPixelStride();
    int a1bo = afa1.getBandOffset(0);
    

    int[] a2 = null;
    int a2ss = 0;
    int a2ps = 0;
    int a2bo = 0;
    if (afa2 != null) {
      a2 = afa2.getIntDataArray(0);
      a2ss = afa2.getScanlineStride();
      a2ps = afa2.getPixelStride();
      a2bo = afa2.getBandOffset(0);
    }
    

    int[][] d = dst.getIntDataArrays();
    int dss = dst.getScanlineStride();
    int dps = dst.getPixelStride();
    int[] dbo = dst.getBandOffsets();
    
    int s1so = 0;int s2so = 0;int a1so = 0;int a2so = 0;int dso = 0;
    
    if (alphaPremultiplied) {
      if (afa2 == null) {
        for (int h = 0; h < dheight; h++) {
          int s1po = s1so;
          int s2po = s2so;
          int a1po = a1so;
          int dpo = dso;
          
          for (int w = 0; w < dwidth; w++) {
            float t = 1.0F - a1[(a1po + a1bo)] * invMaxValue;
            

            d[aOffset][(dpo + dbo[aOffset])] = maxValue;
            

            for (int b = 0; b < numBands; b++) {
              int i = b + cOffset;
              d[i][(dpo + dbo[i])] = ((int)(s1[b][(s1po + s1bo[b])] + s2[b][(s2po + s2bo[b])] * t));
            }
            

            s1po += s1ps;
            s2po += s2ps;
            a1po += a1ps;
            dpo += dps;
          }
          
          s1so += s1ss;
          s2so += s2ss;
          a1so += a1ss;
          dso += dss;
        }
      } else {
        for (int h = 0; h < dheight; h++) {
          int s1po = s1so;
          int s2po = s2so;
          int a1po = a1so;
          int a2po = a2so;
          int dpo = dso;
          
          for (int w = 0; w < dwidth; w++) {
            int t1 = a1[(a1po + a1bo)];
            float t2 = 1.0F - t1 * invMaxValue;
            

            d[aOffset][(dpo + dbo[aOffset])] = ((int)(t1 + a2[(a2po + a2bo)] * t2));
            


            for (int b = 0; b < numBands; b++) {
              int i = b + cOffset;
              d[i][(dpo + dbo[i])] = ((int)(s1[b][(s1po + s1bo[b])] + s2[b][(s2po + s2bo[b])] * t2));
            }
            

            s1po += s1ps;
            s2po += s2ps;
            a1po += a1ps;
            a2po += a2ps;
            dpo += dps;
          }
          
          s1so += s1ss;
          s2so += s2ss;
          a1so += a1ss;
          a2so += a2ss;
          dso += dss;
        }
      }
    }
    else if (afa2 == null) {
      for (int h = 0; h < dheight; h++) {
        int s1po = s1so;
        int s2po = s2so;
        int a1po = a1so;
        int dpo = dso;
        
        for (int w = 0; w < dwidth; w++) {
          float t1 = a1[(a1po + a1bo)] * invMaxValue;
          float t2 = 1.0F - t1;
          

          d[aOffset][(dpo + dbo[aOffset])] = maxValue;
          

          for (int b = 0; b < numBands; b++) {
            int i = b + cOffset;
            d[i][(dpo + dbo[i])] = ((int)(s1[b][(s1po + s1bo[b])] * t1 + s2[b][(s2po + s2bo[b])] * t2));
          }
          


          s1po += s1ps;
          s2po += s2ps;
          a1po += a1ps;
          dpo += dps;
        }
        
        s1so += s1ss;
        s2so += s2ss;
        a1so += a1ss;
        dso += dss;
      }
    } else {
      for (int h = 0; h < dheight; h++) {
        int s1po = s1so;
        int s2po = s2so;
        int a1po = a1so;
        int a2po = a2so;
        int dpo = dso;
        
        for (int w = 0; w < dwidth; w++) {
          int t1 = a1[(a1po + a1bo)];
          float t2 = (1.0F - t1 * invMaxValue) * a2[(a2po + a2bo)];
          
          float t3 = t1 + t2;
          float t5;
          float t4; float t5; if (t3 == 0.0F) {
            float t4 = 0.0F;
            t5 = 0.0F;
          } else {
            t4 = t1 / t3;
            t5 = t2 / t3;
          }
          

          d[aOffset][(dpo + dbo[aOffset])] = ((int)t3);
          

          for (int b = 0; b < numBands; b++) {
            int i = b + cOffset;
            d[i][(dpo + dbo[i])] = ((int)(s1[b][(s1po + s1bo[b])] * t4 + s2[b][(s2po + s2bo[b])] * t5));
          }
          

          s1po += s1ps;
          s2po += s2ps;
          a1po += a1ps;
          a2po += a2ps;
          dpo += dps;
        }
        
        s1so += s1ss;
        s2so += s2ss;
        a1so += a1ss;
        a2so += a2ss;
        dso += dss;
      }
    }
  }
  


  private void floatLoop(RasterAccessor src1, RasterAccessor src2, RasterAccessor afa1, RasterAccessor afa2, RasterAccessor dst)
  {
    int dwidth = dst.getWidth();
    int dheight = dst.getHeight();
    int numBands = src1.getNumBands();
    

    float[][] s1 = src1.getFloatDataArrays();
    int s1ss = src1.getScanlineStride();
    int s1ps = src1.getPixelStride();
    int[] s1bo = src1.getBandOffsets();
    

    float[][] s2 = src2.getFloatDataArrays();
    int s2ss = src2.getScanlineStride();
    int s2ps = src2.getPixelStride();
    int[] s2bo = src2.getBandOffsets();
    

    float[] a1 = afa1.getFloatDataArray(0);
    int a1ss = afa1.getScanlineStride();
    int a1ps = afa1.getPixelStride();
    int a1bo = afa1.getBandOffset(0);
    

    float[] a2 = null;
    int a2ss = 0;
    int a2ps = 0;
    int a2bo = 0;
    if (afa2 != null) {
      a2 = afa2.getFloatDataArray(0);
      a2ss = afa2.getScanlineStride();
      a2ps = afa2.getPixelStride();
      a2bo = afa2.getBandOffset(0);
    }
    

    float[][] d = dst.getFloatDataArrays();
    int dss = dst.getScanlineStride();
    int dps = dst.getPixelStride();
    int[] dbo = dst.getBandOffsets();
    
    int s1so = 0;int s2so = 0;int a1so = 0;int a2so = 0;int dso = 0;
    
    float invMaxValue = 2.938736E-39F;
    if (alphaPremultiplied) {
      if (afa2 == null) {
        for (int h = 0; h < dheight; h++) {
          int s1po = s1so;
          int s2po = s2so;
          int a1po = a1so;
          int dpo = dso;
          
          for (int w = 0; w < dwidth; w++) {
            float t = 1.0F - a1[(a1po + a1bo)] * invMaxValue;
            

            d[aOffset][(dpo + dbo[aOffset])] = Float.MAX_VALUE;
            

            for (int b = 0; b < numBands; b++) {
              int i = b + cOffset;
              s1[b][(s1po + s1bo[b])] += s2[b][(s2po + s2bo[b])] * t;
            }
            

            s1po += s1ps;
            s2po += s2ps;
            a1po += a1ps;
            dpo += dps;
          }
          
          s1so += s1ss;
          s2so += s2ss;
          a1so += a1ss;
          dso += dss;
        }
      } else {
        for (int h = 0; h < dheight; h++) {
          int s1po = s1so;
          int s2po = s2so;
          int a1po = a1so;
          int a2po = a2so;
          int dpo = dso;
          
          for (int w = 0; w < dwidth; w++) {
            float t1 = a1[(a1po + a1bo)];
            float t2 = 1.0F - t1 * invMaxValue;
            

            d[aOffset][(dpo + dbo[aOffset])] = (t1 + a2[(a2po + a2bo)] * t2);
            

            for (int b = 0; b < numBands; b++) {
              int i = b + cOffset;
              s1[b][(s1po + s1bo[b])] += s2[b][(s2po + s2bo[b])] * t2;
            }
            

            s1po += s1ps;
            s2po += s2ps;
            a1po += a1ps;
            a2po += a2ps;
            dpo += dps;
          }
          
          s1so += s1ss;
          s2so += s2ss;
          a1so += a1ss;
          a2so += a2ss;
          dso += dss;
        }
      }
    }
    else if (afa2 == null) {
      for (int h = 0; h < dheight; h++) {
        int s1po = s1so;
        int s2po = s2so;
        int a1po = a1so;
        int dpo = dso;
        
        for (int w = 0; w < dwidth; w++) {
          float t1 = a1[(a1po + a1bo)] * invMaxValue;
          float t2 = 1.0F - t1;
          

          d[aOffset][(dpo + dbo[aOffset])] = Float.MAX_VALUE;
          

          for (int b = 0; b < numBands; b++) {
            int i = b + cOffset;
            d[i][(dpo + dbo[i])] = (s1[b][(s1po + s1bo[b])] * t1 + s2[b][(s2po + s2bo[b])] * t2);
          }
          

          s1po += s1ps;
          s2po += s2ps;
          a1po += a1ps;
          dpo += dps;
        }
        
        s1so += s1ss;
        s2so += s2ss;
        a1so += a1ss;
        dso += dss;
      }
    } else {
      for (int h = 0; h < dheight; h++) {
        int s1po = s1so;
        int s2po = s2so;
        int a1po = a1so;
        int a2po = a2so;
        int dpo = dso;
        
        for (int w = 0; w < dwidth; w++) {
          float t1 = a1[(a1po + a1bo)];
          float t2 = (1.0F - t1 * invMaxValue) * a2[(a2po + a2bo)];
          
          float t3 = t1 + t2;
          float t5;
          float t4; float t5; if (t3 == 0.0F) {
            float t4 = 0.0F;
            t5 = 0.0F;
          } else {
            t4 = t1 / t3;
            t5 = t2 / t3;
          }
          

          d[aOffset][(dpo + dbo[aOffset])] = t3;
          

          for (int b = 0; b < numBands; b++) {
            int i = b + cOffset;
            d[i][(dpo + dbo[i])] = (s1[b][(s1po + s1bo[b])] * t4 + s2[b][(s2po + s2bo[b])] * t5);
          }
          

          s1po += s1ps;
          s2po += s2ps;
          a1po += a1ps;
          a2po += a2ps;
          dpo += dps;
        }
        
        s1so += s1ss;
        s2so += s2ss;
        a1so += a1ss;
        a2so += a2ss;
        dso += dss;
      }
    }
  }
  


  private void doubleLoop(RasterAccessor src1, RasterAccessor src2, RasterAccessor afa1, RasterAccessor afa2, RasterAccessor dst)
  {
    int dwidth = dst.getWidth();
    int dheight = dst.getHeight();
    int numBands = src1.getNumBands();
    

    double[][] s1 = src1.getDoubleDataArrays();
    int s1ss = src1.getScanlineStride();
    int s1ps = src1.getPixelStride();
    int[] s1bo = src1.getBandOffsets();
    

    double[][] s2 = src2.getDoubleDataArrays();
    int s2ss = src2.getScanlineStride();
    int s2ps = src2.getPixelStride();
    int[] s2bo = src2.getBandOffsets();
    

    double[] a1 = afa1.getDoubleDataArray(0);
    int a1ss = afa1.getScanlineStride();
    int a1ps = afa1.getPixelStride();
    int a1bo = afa1.getBandOffset(0);
    

    double[] a2 = null;
    int a2ss = 0;
    int a2ps = 0;
    int a2bo = 0;
    if (afa2 != null) {
      a2 = afa2.getDoubleDataArray(0);
      a2ss = afa2.getScanlineStride();
      a2ps = afa2.getPixelStride();
      a2bo = afa2.getBandOffset(0);
    }
    

    double[][] d = dst.getDoubleDataArrays();
    int dss = dst.getScanlineStride();
    int dps = dst.getPixelStride();
    int[] dbo = dst.getBandOffsets();
    
    int s1so = 0;int s2so = 0;int a1so = 0;int a2so = 0;int dso = 0;
    
    double invMaxValue = 5.562684646268003E-309D;
    if (alphaPremultiplied) {
      if (afa2 == null) {
        for (int h = 0; h < dheight; h++) {
          int s1po = s1so;
          int s2po = s2so;
          int a1po = a1so;
          int dpo = dso;
          
          for (int w = 0; w < dwidth; w++) {
            double t = 1.0D - a1[(a1po + a1bo)] * invMaxValue;
            

            d[aOffset][(dpo + dbo[aOffset])] = Double.MAX_VALUE;
            

            for (int b = 0; b < numBands; b++) {
              int i = b + cOffset;
              s1[b][(s1po + s1bo[b])] += s2[b][(s2po + s2bo[b])] * t;
            }
            

            s1po += s1ps;
            s2po += s2ps;
            a1po += a1ps;
            dpo += dps;
          }
          
          s1so += s1ss;
          s2so += s2ss;
          a1so += a1ss;
          dso += dss;
        }
      } else {
        for (int h = 0; h < dheight; h++) {
          int s1po = s1so;
          int s2po = s2so;
          int a1po = a1so;
          int a2po = a2so;
          int dpo = dso;
          
          for (int w = 0; w < dwidth; w++) {
            double t1 = a1[(a1po + a1bo)];
            double t2 = 1.0D - t1 * invMaxValue;
            

            d[aOffset][(dpo + dbo[aOffset])] = (t1 + a2[(a2po + a2bo)] * t2);
            

            for (int b = 0; b < numBands; b++) {
              int i = b + cOffset;
              s1[b][(s1po + s1bo[b])] += s2[b][(s2po + s2bo[b])] * t2;
            }
            

            s1po += s1ps;
            s2po += s2ps;
            a1po += a1ps;
            a2po += a2ps;
            dpo += dps;
          }
          
          s1so += s1ss;
          s2so += s2ss;
          a1so += a1ss;
          a2so += a2ss;
          dso += dss;
        }
      }
    }
    else if (afa2 == null) {
      for (int h = 0; h < dheight; h++) {
        int s1po = s1so;
        int s2po = s2so;
        int a1po = a1so;
        int dpo = dso;
        
        for (int w = 0; w < dwidth; w++) {
          double t1 = a1[(a1po + a1bo)] * invMaxValue;
          double t2 = 1.0D - t1;
          

          d[aOffset][(dpo + dbo[aOffset])] = Double.MAX_VALUE;
          

          for (int b = 0; b < numBands; b++) {
            int i = b + cOffset;
            d[i][(dpo + dbo[i])] = (s1[b][(s1po + s1bo[b])] * t1 + s2[b][(s2po + s2bo[b])] * t2);
          }
          

          s1po += s1ps;
          s2po += s2ps;
          a1po += a1ps;
          dpo += dps;
        }
        
        s1so += s1ss;
        s2so += s2ss;
        a1so += a1ss;
        dso += dss;
      }
    } else {
      for (int h = 0; h < dheight; h++) {
        int s1po = s1so;
        int s2po = s2so;
        int a1po = a1so;
        int a2po = a2so;
        int dpo = dso;
        
        for (int w = 0; w < dwidth; w++) {
          double t1 = a1[(a1po + a1bo)];
          double t2 = (1.0D - t1 * invMaxValue) * a2[(a2po + a2bo)];
          
          double t3 = t1 + t2;
          double t5;
          double t4; double t5; if (t3 == 0.0D) {
            double t4 = 0.0D;
            t5 = 0.0D;
          } else {
            t4 = t1 / t3;
            t5 = t2 / t3;
          }
          

          d[aOffset][(dpo + dbo[aOffset])] = t3;
          

          for (int b = 0; b < numBands; b++) {
            int i = b + cOffset;
            d[i][(dpo + dbo[i])] = (s1[b][(s1po + s1bo[b])] * t4 + s2[b][(s2po + s2bo[b])] * t5);
          }
          

          s1po += s1ps;
          s2po += s2ps;
          a1po += a1ps;
          a2po += a2ps;
          dpo += dps;
        }
        
        s1so += s1ss;
        s2so += s2ss;
        a1so += a1ss;
        a2so += a2ss;
        dso += dss;
      }
    }
  }
}
