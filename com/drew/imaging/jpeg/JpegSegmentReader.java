package com.drew.imaging.jpeg;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class JpegSegmentReader
{
  @NotNull
  private final JpegSegmentData _segmentData;
  private static final byte SEGMENT_SOS = -38;
  private static final byte MARKER_EOI = -39;
  public static final byte SEGMENT_APP0 = -32;
  public static final byte SEGMENT_APP1 = -31;
  public static final byte SEGMENT_APP2 = -30;
  public static final byte SEGMENT_APP3 = -29;
  public static final byte SEGMENT_APP4 = -28;
  public static final byte SEGMENT_APP5 = -27;
  public static final byte SEGMENT_APP6 = -26;
  public static final byte SEGMENT_APP7 = -25;
  public static final byte SEGMENT_APP8 = -24;
  public static final byte SEGMENT_APP9 = -23;
  public static final byte SEGMENT_APPA = -22;
  public static final byte SEGMENT_APPB = -21;
  public static final byte SEGMENT_APPC = -20;
  public static final byte SEGMENT_APPD = -19;
  public static final byte SEGMENT_APPE = -18;
  public static final byte SEGMENT_APPF = -17;
  public static final byte SEGMENT_SOI = -40;
  public static final byte SEGMENT_DQT = -37;
  public static final byte SEGMENT_DHT = -60;
  public static final byte SEGMENT_SOF0 = -64;
  public static final byte SEGMENT_COM = -2;
  
  public JpegSegmentReader(@NotNull File paramFile)
    throws JpegProcessingException, IOException
  {
    if (paramFile == null) {
      throw new NullPointerException();
    }
    FileInputStream localFileInputStream = null;
    try
    {
      localFileInputStream = new FileInputStream(paramFile);
      _segmentData = readSegments(new BufferedInputStream(localFileInputStream), false);
    }
    finally
    {
      if (localFileInputStream != null) {
        localFileInputStream.close();
      }
    }
  }
  
  public JpegSegmentReader(@NotNull byte[] paramArrayOfByte)
    throws JpegProcessingException
  {
    if (paramArrayOfByte == null) {
      throw new NullPointerException();
    }
    BufferedInputStream localBufferedInputStream = new BufferedInputStream(new ByteArrayInputStream(paramArrayOfByte));
    _segmentData = readSegments(localBufferedInputStream, false);
  }
  
  public JpegSegmentReader(@NotNull InputStream paramInputStream, boolean paramBoolean)
    throws JpegProcessingException
  {
    if (paramInputStream == null) {
      throw new NullPointerException();
    }
    BufferedInputStream localBufferedInputStream = (paramInputStream instanceof BufferedInputStream) ? (BufferedInputStream)paramInputStream : new BufferedInputStream(paramInputStream);
    _segmentData = readSegments(localBufferedInputStream, paramBoolean);
  }
  
  @Nullable
  public byte[] readSegment(byte paramByte)
  {
    return readSegment(paramByte, 0);
  }
  
  @Nullable
  public byte[] readSegment(byte paramByte, int paramInt)
  {
    return _segmentData.getSegment(paramByte, paramInt);
  }
  
  @NotNull
  public Iterable<byte[]> readSegments(byte paramByte)
  {
    return _segmentData.getSegments(paramByte);
  }
  
  public final int getSegmentCount(byte paramByte)
  {
    return _segmentData.getSegmentCount(paramByte);
  }
  
  @NotNull
  public final JpegSegmentData getSegmentData()
  {
    return _segmentData;
  }
  
  /* Error */
  @NotNull
  private JpegSegmentData readSegments(@NotNull BufferedInputStream paramBufferedInputStream, boolean paramBoolean)
    throws JpegProcessingException
  {
    // Byte code:
    //   0: new 17	com/drew/imaging/jpeg/JpegSegmentData
    //   3: dup
    //   4: invokespecial 18	com/drew/imaging/jpeg/JpegSegmentData:<init>	()V
    //   7: astore_3
    //   8: iconst_0
    //   9: istore 4
    //   11: iconst_2
    //   12: newarray byte
    //   14: astore 5
    //   16: aload_1
    //   17: aload 5
    //   19: iconst_0
    //   20: iconst_2
    //   21: invokevirtual 19	java/io/BufferedInputStream:read	([BII)I
    //   24: iconst_2
    //   25: if_icmpeq +13 -> 38
    //   28: new 20	com/drew/imaging/jpeg/JpegProcessingException
    //   31: dup
    //   32: ldc 21
    //   34: invokespecial 22	com/drew/imaging/jpeg/JpegProcessingException:<init>	(Ljava/lang/String;)V
    //   37: athrow
    //   38: aload 5
    //   40: iconst_0
    //   41: baload
    //   42: sipush 255
    //   45: iand
    //   46: sipush 255
    //   49: if_icmpne +21 -> 70
    //   52: aload 5
    //   54: iconst_1
    //   55: baload
    //   56: sipush 255
    //   59: iand
    //   60: sipush 216
    //   63: if_icmpne +7 -> 70
    //   66: iconst_1
    //   67: goto +4 -> 71
    //   70: iconst_0
    //   71: istore 6
    //   73: iload 6
    //   75: ifne +13 -> 88
    //   78: new 20	com/drew/imaging/jpeg/JpegProcessingException
    //   81: dup
    //   82: ldc 21
    //   84: invokespecial 22	com/drew/imaging/jpeg/JpegProcessingException:<init>	(Ljava/lang/String;)V
    //   87: athrow
    //   88: iinc 4 2
    //   91: aload_0
    //   92: aload_1
    //   93: iconst_4
    //   94: iload_2
    //   95: invokespecial 23	com/drew/imaging/jpeg/JpegSegmentReader:checkForBytesOnStream	(Ljava/io/BufferedInputStream;IZ)Z
    //   98: ifne +13 -> 111
    //   101: new 20	com/drew/imaging/jpeg/JpegProcessingException
    //   104: dup
    //   105: ldc 24
    //   107: invokespecial 22	com/drew/imaging/jpeg/JpegProcessingException:<init>	(Ljava/lang/String;)V
    //   110: athrow
    //   111: aload_1
    //   112: invokevirtual 25	java/io/BufferedInputStream:read	()I
    //   115: sipush 255
    //   118: iand
    //   119: i2b
    //   120: istore 7
    //   122: iload 7
    //   124: sipush 255
    //   127: iand
    //   128: sipush 255
    //   131: if_icmpeq +48 -> 179
    //   134: new 20	com/drew/imaging/jpeg/JpegProcessingException
    //   137: dup
    //   138: new 26	java/lang/StringBuilder
    //   141: dup
    //   142: invokespecial 27	java/lang/StringBuilder:<init>	()V
    //   145: ldc 28
    //   147: invokevirtual 29	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   150: iload 4
    //   152: invokevirtual 30	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   155: ldc 31
    //   157: invokevirtual 29	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   160: iload 7
    //   162: sipush 255
    //   165: iand
    //   166: invokestatic 32	java/lang/Integer:toHexString	(I)Ljava/lang/String;
    //   169: invokevirtual 29	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   172: invokevirtual 33	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   175: invokespecial 22	com/drew/imaging/jpeg/JpegProcessingException:<init>	(Ljava/lang/String;)V
    //   178: athrow
    //   179: iinc 4 1
    //   182: aload_1
    //   183: invokevirtual 25	java/io/BufferedInputStream:read	()I
    //   186: sipush 255
    //   189: iand
    //   190: i2b
    //   191: istore 8
    //   193: iinc 4 1
    //   196: iconst_2
    //   197: newarray byte
    //   199: astore 9
    //   201: aload_1
    //   202: aload 9
    //   204: iconst_0
    //   205: iconst_2
    //   206: invokevirtual 19	java/io/BufferedInputStream:read	([BII)I
    //   209: iconst_2
    //   210: if_icmpeq +13 -> 223
    //   213: new 20	com/drew/imaging/jpeg/JpegProcessingException
    //   216: dup
    //   217: ldc 34
    //   219: invokespecial 22	com/drew/imaging/jpeg/JpegProcessingException:<init>	(Ljava/lang/String;)V
    //   222: athrow
    //   223: iinc 4 2
    //   226: aload 9
    //   228: iconst_0
    //   229: baload
    //   230: bipush 8
    //   232: ishl
    //   233: ldc 35
    //   235: iand
    //   236: aload 9
    //   238: iconst_1
    //   239: baload
    //   240: sipush 255
    //   243: iand
    //   244: ior
    //   245: istore 10
    //   247: iinc 10 -2
    //   250: aload_0
    //   251: aload_1
    //   252: iload 10
    //   254: iload_2
    //   255: invokespecial 23	com/drew/imaging/jpeg/JpegSegmentReader:checkForBytesOnStream	(Ljava/io/BufferedInputStream;IZ)Z
    //   258: ifne +13 -> 271
    //   261: new 20	com/drew/imaging/jpeg/JpegProcessingException
    //   264: dup
    //   265: ldc 36
    //   267: invokespecial 22	com/drew/imaging/jpeg/JpegProcessingException:<init>	(Ljava/lang/String;)V
    //   270: athrow
    //   271: iload 10
    //   273: ifge +13 -> 286
    //   276: new 20	com/drew/imaging/jpeg/JpegProcessingException
    //   279: dup
    //   280: ldc 37
    //   282: invokespecial 22	com/drew/imaging/jpeg/JpegProcessingException:<init>	(Ljava/lang/String;)V
    //   285: athrow
    //   286: iload 10
    //   288: newarray byte
    //   290: astore 11
    //   292: aload_1
    //   293: aload 11
    //   295: iconst_0
    //   296: iload 10
    //   298: invokevirtual 19	java/io/BufferedInputStream:read	([BII)I
    //   301: iload 10
    //   303: if_icmpeq +13 -> 316
    //   306: new 20	com/drew/imaging/jpeg/JpegProcessingException
    //   309: dup
    //   310: ldc 34
    //   312: invokespecial 22	com/drew/imaging/jpeg/JpegProcessingException:<init>	(Ljava/lang/String;)V
    //   315: athrow
    //   316: iload 4
    //   318: iload 10
    //   320: iadd
    //   321: istore 4
    //   323: iload 8
    //   325: sipush 255
    //   328: iand
    //   329: sipush 218
    //   332: if_icmpne +55 -> 387
    //   335: aload_3
    //   336: astore 12
    //   338: aload_1
    //   339: ifnull +7 -> 346
    //   342: aload_1
    //   343: invokevirtual 38	java/io/BufferedInputStream:close	()V
    //   346: goto +38 -> 384
    //   349: astore 13
    //   351: new 20	com/drew/imaging/jpeg/JpegProcessingException
    //   354: dup
    //   355: new 26	java/lang/StringBuilder
    //   358: dup
    //   359: invokespecial 27	java/lang/StringBuilder:<init>	()V
    //   362: ldc 40
    //   364: invokevirtual 29	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   367: aload 13
    //   369: invokevirtual 41	java/io/IOException:getMessage	()Ljava/lang/String;
    //   372: invokevirtual 29	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   375: invokevirtual 33	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   378: aload 13
    //   380: invokespecial 42	com/drew/imaging/jpeg/JpegProcessingException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   383: athrow
    //   384: aload 12
    //   386: areturn
    //   387: iload 8
    //   389: sipush 255
    //   392: iand
    //   393: sipush 217
    //   396: if_icmpne +55 -> 451
    //   399: aload_3
    //   400: astore 12
    //   402: aload_1
    //   403: ifnull +7 -> 410
    //   406: aload_1
    //   407: invokevirtual 38	java/io/BufferedInputStream:close	()V
    //   410: goto +38 -> 448
    //   413: astore 13
    //   415: new 20	com/drew/imaging/jpeg/JpegProcessingException
    //   418: dup
    //   419: new 26	java/lang/StringBuilder
    //   422: dup
    //   423: invokespecial 27	java/lang/StringBuilder:<init>	()V
    //   426: ldc 40
    //   428: invokevirtual 29	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   431: aload 13
    //   433: invokevirtual 41	java/io/IOException:getMessage	()Ljava/lang/String;
    //   436: invokevirtual 29	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   439: invokevirtual 33	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   442: aload 13
    //   444: invokespecial 42	com/drew/imaging/jpeg/JpegProcessingException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   447: athrow
    //   448: aload 12
    //   450: areturn
    //   451: aload_3
    //   452: iload 8
    //   454: aload 11
    //   456: invokevirtual 43	com/drew/imaging/jpeg/JpegSegmentData:addSegment	(B[B)V
    //   459: goto -368 -> 91
    //   462: astore 4
    //   464: new 20	com/drew/imaging/jpeg/JpegProcessingException
    //   467: dup
    //   468: new 26	java/lang/StringBuilder
    //   471: dup
    //   472: invokespecial 27	java/lang/StringBuilder:<init>	()V
    //   475: ldc 40
    //   477: invokevirtual 29	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   480: aload 4
    //   482: invokevirtual 41	java/io/IOException:getMessage	()Ljava/lang/String;
    //   485: invokevirtual 29	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   488: invokevirtual 33	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   491: aload 4
    //   493: invokespecial 42	com/drew/imaging/jpeg/JpegProcessingException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   496: athrow
    //   497: astore 14
    //   499: aload_1
    //   500: ifnull +7 -> 507
    //   503: aload_1
    //   504: invokevirtual 38	java/io/BufferedInputStream:close	()V
    //   507: goto +38 -> 545
    //   510: astore 15
    //   512: new 20	com/drew/imaging/jpeg/JpegProcessingException
    //   515: dup
    //   516: new 26	java/lang/StringBuilder
    //   519: dup
    //   520: invokespecial 27	java/lang/StringBuilder:<init>	()V
    //   523: ldc 40
    //   525: invokevirtual 29	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   528: aload 15
    //   530: invokevirtual 41	java/io/IOException:getMessage	()Ljava/lang/String;
    //   533: invokevirtual 29	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   536: invokevirtual 33	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   539: aload 15
    //   541: invokespecial 42	com/drew/imaging/jpeg/JpegProcessingException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   544: athrow
    //   545: aload 14
    //   547: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	548	0	this	JpegSegmentReader
    //   0	548	1	paramBufferedInputStream	BufferedInputStream
    //   0	548	2	paramBoolean	boolean
    //   7	445	3	localJpegSegmentData1	JpegSegmentData
    //   9	313	4	i	int
    //   462	30	4	localIOException1	IOException
    //   14	39	5	arrayOfByte1	byte[]
    //   71	3	6	j	int
    //   120	46	7	k	int
    //   191	262	8	m	int
    //   199	38	9	arrayOfByte2	byte[]
    //   245	76	10	n	int
    //   290	165	11	arrayOfByte3	byte[]
    //   336	113	12	localJpegSegmentData2	JpegSegmentData
    //   349	30	13	localIOException2	IOException
    //   413	30	13	localIOException3	IOException
    //   497	49	14	localObject	Object
    //   510	30	15	localIOException4	IOException
    // Exception table:
    //   from	to	target	type
    //   338	346	349	java/io/IOException
    //   402	410	413	java/io/IOException
    //   8	338	462	java/io/IOException
    //   387	402	462	java/io/IOException
    //   451	462	462	java/io/IOException
    //   8	338	497	finally
    //   387	402	497	finally
    //   451	499	497	finally
    //   499	507	510	java/io/IOException
  }
  
  private boolean checkForBytesOnStream(@NotNull BufferedInputStream paramBufferedInputStream, int paramInt, boolean paramBoolean)
    throws IOException
  {
    if (!paramBoolean) {
      return paramInt <= paramBufferedInputStream.available();
    }
    for (int i = 40; i > 0; i--)
    {
      if (paramInt <= paramBufferedInputStream.available()) {
        return true;
      }
      try
      {
        Thread.sleep(100L);
      }
      catch (InterruptedException localInterruptedException) {}
    }
    return false;
  }
}
