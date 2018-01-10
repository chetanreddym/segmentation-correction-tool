package com.drew.imaging;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.psd.PsdMetadataReader;
import com.drew.imaging.tiff.TiffMetadataReader;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifThumbnailDirectory;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class ImageMetadataReader
{
  private static final int JPEG_FILE_MAGIC_NUMBER = 65496;
  private static final int MOTOROLA_TIFF_MAGIC_NUMBER = 19789;
  private static final int INTEL_TIFF_MAGIC_NUMBER = 18761;
  private static final int PSD_MAGIC_NUMBER = 14402;
  
  @NotNull
  public static Metadata readMetadata(@NotNull BufferedInputStream paramBufferedInputStream, boolean paramBoolean)
    throws ImageProcessingException, IOException
  {
    int i = readMagicNumber(paramBufferedInputStream);
    return readMetadata(paramBufferedInputStream, null, i, paramBoolean);
  }
  
  @NotNull
  public static Metadata readMetadata(@NotNull File paramFile)
    throws ImageProcessingException, IOException
  {
    BufferedInputStream localBufferedInputStream = new BufferedInputStream(new FileInputStream(paramFile));
    int i;
    try
    {
      i = readMagicNumber(localBufferedInputStream);
    }
    finally
    {
      localBufferedInputStream.close();
    }
    return readMetadata(null, paramFile, i, false);
  }
  
  @NotNull
  private static Metadata readMetadata(@Nullable BufferedInputStream paramBufferedInputStream, @Nullable File paramFile, int paramInt, boolean paramBoolean)
    throws ImageProcessingException, IOException
  {
    if (!$assertionsDisabled) {
      if (((paramFile != null ? 1 : 0) ^ (paramBufferedInputStream != null ? 1 : 0)) == 0) {
        throw new AssertionError();
      }
    }
    if ((paramInt & 0xFFD8) == 65496)
    {
      if (paramBufferedInputStream != null) {
        return JpegMetadataReader.readMetadata(paramBufferedInputStream, paramBoolean);
      }
      return JpegMetadataReader.readMetadata(paramFile);
    }
    if ((paramInt == 18761) || (paramInt == 19789))
    {
      if (paramBufferedInputStream != null) {
        return TiffMetadataReader.readMetadata(paramBufferedInputStream, paramBoolean);
      }
      return TiffMetadataReader.readMetadata(paramFile);
    }
    if (paramInt == 14402)
    {
      if (paramBufferedInputStream != null) {
        return PsdMetadataReader.readMetadata(paramBufferedInputStream, paramBoolean);
      }
      return PsdMetadataReader.readMetadata(paramFile);
    }
    throw new ImageProcessingException("File format is not supported");
  }
  
  private static int readMagicNumber(@NotNull BufferedInputStream paramBufferedInputStream)
    throws IOException
  {
    paramBufferedInputStream.mark(2);
    int i = paramBufferedInputStream.read() << 8 | paramBufferedInputStream.read();
    paramBufferedInputStream.reset();
    return i;
  }
  
  private ImageMetadataReader()
    throws Exception
  {
    throw new Exception("Not intended for instantiation");
  }
  
  public static void main(@NotNull String[] paramArrayOfString)
    throws MetadataException, IOException
  {
    ArrayList localArrayList = new ArrayList(Arrays.asList(paramArrayOfString));
    boolean bool1 = localArrayList.remove("/thumb");
    boolean bool2 = localArrayList.remove("/wiki");
    if (localArrayList.size() < 1)
    {
      System.out.println("Usage: java -jar metadata-extractor-a.b.c.jar <filename> [<filename>] [/thumb] [/wiki]");
      System.exit(1);
    }
    Iterator localIterator = localArrayList.iterator();
    while (localIterator.hasNext())
    {
      String str1 = (String)localIterator.next();
      long l1 = System.nanoTime();
      File localFile = new File(str1);
      if ((!bool2) && (localArrayList.size() > 1)) {
        System.out.println("***** PROCESSING: " + str1);
      }
      Metadata localMetadata = null;
      try
      {
        localMetadata = readMetadata(localFile);
      }
      catch (Exception localException)
      {
        localException.printStackTrace(System.err);
        System.exit(1);
      }
      long l2 = System.nanoTime() - l1;
      if (!bool2) {
        System.out.println("Processed " + localFile.length() / 1048576.0D + "MB file in " + l2 / 1000000.0D + "ms");
      }
      Object localObject2;
      Object localObject3;
      Object localObject4;
      String str2;
      if (bool2)
      {
        localObject1 = localFile.getName();
        localObject2 = ((String)localObject1).replace(" ", "%20");
        localObject3 = (ExifIFD0Directory)localMetadata.getOrCreateDirectory(ExifIFD0Directory.class);
        localObject4 = escapeForWiki(((ExifIFD0Directory)localObject3).getString(271));
        str2 = escapeForWiki(((ExifIFD0Directory)localObject3).getString(272));
        System.out.println();
        System.out.println("-----");
        System.out.println();
        System.out.printf("= %s - %s =%n", new Object[] { localObject4, str2 });
        System.out.println();
        System.out.printf("<a href=\"http://metadata-extractor.googlecode.com/svn/sample-images/%s\">%n", new Object[] { localObject2 });
        System.out.printf("<img src=\"http://metadata-extractor.googlecode.com/svn/sample-images/%s\" width=\"300\"/><br/>%n", new Object[] { localObject2 });
        System.out.println((String)localObject1);
        System.out.println("</a>");
        System.out.println();
        System.out.println("|| *Directory* || *Tag Id* || *Tag Name* || *Tag Description* ||");
      }
      Object localObject1 = localMetadata.getDirectories().iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (Directory)((Iterator)localObject1).next();
        localObject3 = ((Directory)localObject2).getTags().iterator();
        while (((Iterator)localObject3).hasNext())
        {
          localObject4 = (Tag)((Iterator)localObject3).next();
          str2 = ((Tag)localObject4).getTagName();
          String str3 = ((Directory)localObject2).getName();
          String str4 = ((Tag)localObject4).getDescription();
          if ((str4 != null) && (str4.length() > 1024)) {
            str4 = str4.substring(0, 1024) + "...";
          }
          if (bool2) {
            System.out.printf("||%s||0x%s||%s||%s||%n", new Object[] { escapeForWiki(str3), Integer.toHexString(((Tag)localObject4).getTagType()), escapeForWiki(str2), escapeForWiki(str4) });
          } else {
            System.out.printf("[%s] %s = %s%n", new Object[] { str3, str2, str4 });
          }
        }
        localObject3 = ((Directory)localObject2).getErrors().iterator();
        while (((Iterator)localObject3).hasNext())
        {
          localObject4 = (String)((Iterator)localObject3).next();
          System.err.println("ERROR: " + (String)localObject4);
        }
      }
      if ((paramArrayOfString.length > 1) && (bool1))
      {
        localObject1 = (ExifThumbnailDirectory)localMetadata.getDirectory(ExifThumbnailDirectory.class);
        if ((localObject1 != null) && (((ExifThumbnailDirectory)localObject1).hasThumbnailData()))
        {
          System.out.println("Writing thumbnail...");
          ((ExifThumbnailDirectory)localObject1).writeThumbnail(paramArrayOfString[0].trim() + ".thumb.jpg");
        }
        else
        {
          System.out.println("No thumbnail data exists in this image");
        }
      }
    }
  }
  
  @Nullable
  private static String escapeForWiki(@Nullable String paramString)
  {
    if (paramString == null) {
      return null;
    }
    paramString = paramString.replaceAll("(\\W|^)(([A-Z][a-z0-9]+){2,})", "$1!$2");
    if ((paramString != null) && (paramString.length() > 120)) {
      paramString = paramString.substring(0, 120) + "...";
    }
    if (paramString != null) {
      paramString = paramString.replace("[", "`[`").replace("]", "`]`").replace("<", "`<`").replace(">", "`>`");
    }
    return paramString;
  }
}
