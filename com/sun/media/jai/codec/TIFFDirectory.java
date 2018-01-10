package com.sun.media.jai.codec;

import java.io.EOFException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;






































public class TIFFDirectory
  implements Serializable
{
  boolean isBigEndian;
  int numEntries;
  TIFFField[] fields;
  Hashtable fieldIndex = new Hashtable();
  

  long IFDOffset = 8L;
  

  long nextIFDOffset = 0L;
  
  TIFFDirectory() {}
  
  private static boolean isValidEndianTag(int endian)
  {
    return (endian == 18761) || (endian == 19789);
  }
  










  public TIFFDirectory(SeekableStream stream, int directory)
    throws IOException
  {
    long global_save_offset = stream.getFilePointer();
    


    stream.seek(0L);
    int endian = stream.readUnsignedShort();
    if (!isValidEndianTag(endian)) {
      throw new IllegalArgumentException(JaiI18N.getString("TIFFDirectory1"));
    }
    
    isBigEndian = (endian == 19789);
    
    int magic = readUnsignedShort(stream);
    if (magic != 42) {
      throw new IllegalArgumentException(JaiI18N.getString("TIFFDirectory2"));
    }
    


    long ifd_offset = readUnsignedInt(stream);
    
    for (int i = 0; i < directory; i++) {
      if (ifd_offset == 0L) {
        throw new IllegalArgumentException(JaiI18N.getString("TIFFDirectory3"));
      }
      

      stream.seek(ifd_offset);
      int entries = readUnsignedShort(stream);
      stream.skip(12 * entries);
      
      ifd_offset = readUnsignedInt(stream);
    }
    
    stream.seek(ifd_offset);
    initialize(stream);
    stream.seek(global_save_offset);
  }
  













  public TIFFDirectory(SeekableStream stream, long ifd_offset, int directory)
    throws IOException
  {
    long global_save_offset = stream.getFilePointer();
    stream.seek(0L);
    int endian = stream.readUnsignedShort();
    if (!isValidEndianTag(endian)) {
      throw new IllegalArgumentException(JaiI18N.getString("TIFFDirectory1"));
    }
    
    isBigEndian = (endian == 19789);
    

    stream.seek(ifd_offset);
    

    int dirNum = 0;
    while (dirNum < directory)
    {
      int numEntries = readUnsignedShort(stream);
      

      stream.seek(ifd_offset + 12 * numEntries);
      

      ifd_offset = readUnsignedInt(stream);
      

      stream.seek(ifd_offset);
      

      dirNum++;
    }
    
    initialize(stream);
    stream.seek(global_save_offset);
  }
  
  private static final int[] sizeOfType = { 0, 1, 1, 2, 4, 8, 1, 1, 2, 4, 8, 4, 8 };
  















  private void initialize(SeekableStream stream)
    throws IOException
  {
    IFDOffset = stream.getFilePointer();
    
    numEntries = readUnsignedShort(stream);
    fields = new TIFFField[numEntries];
    
    for (int i = 0; i < numEntries; i++) {
      int tag = readUnsignedShort(stream);
      int type = readUnsignedShort(stream);
      int count = (int)readUnsignedInt(stream);
      int value = 0;
      

      long nextTagOffset = stream.getFilePointer() + 4L;
      

      try
      {
        if (count * sizeOfType[type] > 4) {
          value = (int)readUnsignedInt(stream);
          stream.seek(value);
        }
      }
      catch (ArrayIndexOutOfBoundsException ae) {
        System.err.println(tag + " " + JaiI18N.getString("TIFFDirectory4"));
        

        stream.seek(nextTagOffset);
        continue;
      }
      
      fieldIndex.put(new Integer(tag), new Integer(i));
      Object obj = null;
      try
      {
        switch (type) {
        case 1: 
        case 2: 
        case 6: 
        case 7: 
          byte[] bvalues = new byte[count];
          stream.readFully(bvalues, 0, count);
          
          if (type == 2)
          {

            int index = 0;int prevIndex = 0;
            Vector v = new Vector();
            
            while (index < count)
            {
              while ((index < count) && (bvalues[(index++)] != 0)) {}
              

              v.add(new String(bvalues, prevIndex, index - prevIndex));
              
              prevIndex = index;
            }
            
            count = v.size();
            String[] strings = new String[count];
            for (int c = 0; c < count; c++) {
              strings[c] = ((String)v.elementAt(c));
            }
            
            obj = strings;
          } else {
            obj = bvalues;
          }
          
          break;
        
        case 3: 
          char[] cvalues = new char[count];
          for (int j = 0; j < count; j++) {
            cvalues[j] = ((char)readUnsignedShort(stream));
          }
          obj = cvalues;
          break;
        
        case 4: 
          long[] lvalues = new long[count];
          for (int j = 0; j < count; j++) {
            lvalues[j] = readUnsignedInt(stream);
          }
          obj = lvalues;
          break;
        
        case 5: 
          long[][] llvalues = new long[count][2];
          for (int j = 0; j < count; j++) {
            llvalues[j][0] = readUnsignedInt(stream);
            llvalues[j][1] = readUnsignedInt(stream);
          }
          obj = llvalues;
          break;
        
        case 8: 
          short[] svalues = new short[count];
          for (int j = 0; j < count; j++) {
            svalues[j] = readShort(stream);
          }
          obj = svalues;
          break;
        
        case 9: 
          int[] ivalues = new int[count];
          for (int j = 0; j < count; j++) {
            ivalues[j] = readInt(stream);
          }
          obj = ivalues;
          break;
        
        case 10: 
          int[][] iivalues = new int[count][2];
          for (int j = 0; j < count; j++) {
            iivalues[j][0] = readInt(stream);
            iivalues[j][1] = readInt(stream);
          }
          obj = iivalues;
          break;
        
        case 11: 
          float[] fvalues = new float[count];
          for (int j = 0; j < count; j++) {
            fvalues[j] = readFloat(stream);
          }
          obj = fvalues;
          break;
        
        case 12: 
          double[] dvalues = new double[count];
          for (int j = 0; j < count; j++) {
            dvalues[j] = readDouble(stream);
          }
          obj = dvalues;
          break;
        
        default: 
          System.err.println(JaiI18N.getString("TIFFDirectory0"));
        }
        
        
        fields[i] = new TIFFField(tag, type, count, obj);


      }
      catch (EOFException eofe)
      {

        if ((tag <= 532) || (tag == 33432)) {
          throw eofe;
        }
        
        fieldIndex.remove(new Integer(tag));
      }
      

      stream.seek(nextTagOffset);
    }
    

    nextIFDOffset = readUnsignedInt(stream);
  }
  
  public int getNumEntries()
  {
    return numEntries;
  }
  












  public TIFFField getField(int tag)
  {
    Integer i = (Integer)fieldIndex.get(new Integer(tag));
    if (i == null) {
      return null;
    }
    return fields[i.intValue()];
  }
  



  public boolean isTagPresent(int tag)
  {
    return fieldIndex.containsKey(new Integer(tag));
  }
  



  public int[] getTags()
  {
    int[] tags = new int[fieldIndex.size()];
    Enumeration enumeration = fieldIndex.keys();
    int i = 0;
    
    while (enumeration.hasMoreElements()) {
      tags[(i++)] = ((Integer)enumeration.nextElement()).intValue();
    }
    
    return tags;
  }
  



  public TIFFField[] getFields()
  {
    return fields;
  }
  





  public byte getFieldAsByte(int tag, int index)
  {
    Integer i = (Integer)fieldIndex.get(new Integer(tag));
    byte[] b = fields[i.intValue()].getAsBytes();
    return b[index];
  }
  





  public byte getFieldAsByte(int tag)
  {
    return getFieldAsByte(tag, 0);
  }
  





  public long getFieldAsLong(int tag, int index)
  {
    Integer i = (Integer)fieldIndex.get(new Integer(tag));
    return fields[i.intValue()].getAsLong(index);
  }
  





  public long getFieldAsLong(int tag)
  {
    return getFieldAsLong(tag, 0);
  }
  





  public float getFieldAsFloat(int tag, int index)
  {
    Integer i = (Integer)fieldIndex.get(new Integer(tag));
    return fields[i.intValue()].getAsFloat(index);
  }
  




  public float getFieldAsFloat(int tag)
  {
    return getFieldAsFloat(tag, 0);
  }
  





  public double getFieldAsDouble(int tag, int index)
  {
    Integer i = (Integer)fieldIndex.get(new Integer(tag));
    return fields[i.intValue()].getAsDouble(index);
  }
  




  public double getFieldAsDouble(int tag)
  {
    return getFieldAsDouble(tag, 0);
  }
  

  private short readShort(SeekableStream stream)
    throws IOException
  {
    if (isBigEndian) {
      return stream.readShort();
    }
    return stream.readShortLE();
  }
  
  private int readUnsignedShort(SeekableStream stream)
    throws IOException
  {
    if (isBigEndian) {
      return stream.readUnsignedShort();
    }
    return stream.readUnsignedShortLE();
  }
  
  private int readInt(SeekableStream stream)
    throws IOException
  {
    if (isBigEndian) {
      return stream.readInt();
    }
    return stream.readIntLE();
  }
  
  private long readUnsignedInt(SeekableStream stream)
    throws IOException
  {
    if (isBigEndian) {
      return stream.readUnsignedInt();
    }
    return stream.readUnsignedIntLE();
  }
  
  private long readLong(SeekableStream stream)
    throws IOException
  {
    if (isBigEndian) {
      return stream.readLong();
    }
    return stream.readLongLE();
  }
  
  private float readFloat(SeekableStream stream)
    throws IOException
  {
    if (isBigEndian) {
      return stream.readFloat();
    }
    return stream.readFloatLE();
  }
  
  private double readDouble(SeekableStream stream)
    throws IOException
  {
    if (isBigEndian) {
      return stream.readDouble();
    }
    return stream.readDoubleLE();
  }
  

  private static int readUnsignedShort(SeekableStream stream, boolean isBigEndian)
    throws IOException
  {
    if (isBigEndian) {
      return stream.readUnsignedShort();
    }
    return stream.readUnsignedShortLE();
  }
  

  private static long readUnsignedInt(SeekableStream stream, boolean isBigEndian)
    throws IOException
  {
    if (isBigEndian) {
      return stream.readUnsignedInt();
    }
    return stream.readUnsignedIntLE();
  }
  






  public static int getNumDirectories(SeekableStream stream)
    throws IOException
  {
    long pointer = stream.getFilePointer();
    
    stream.seek(0L);
    int endian = stream.readUnsignedShort();
    if (!isValidEndianTag(endian)) {
      throw new IllegalArgumentException(JaiI18N.getString("TIFFDirectory1"));
    }
    
    boolean isBigEndian = endian == 19789;
    int magic = readUnsignedShort(stream, isBigEndian);
    if (magic != 42) {
      throw new IllegalArgumentException(JaiI18N.getString("TIFFDirectory2"));
    }
    

    stream.seek(4L);
    long offset = readUnsignedInt(stream, isBigEndian);
    
    int numDirectories = 0;
    for (;;) { if (offset != 0L) {
        numDirectories++;
        
        try
        {
          stream.seek(offset);
          int entries = readUnsignedShort(stream, isBigEndian);
          stream.skip(12 * entries);
          offset = readUnsignedInt(stream, isBigEndian);
        } catch (EOFException eof) {
          numDirectories--;
        }
      }
    }
    
    stream.seek(pointer);
    return numDirectories;
  }
  




  public boolean isBigEndian()
  {
    return isBigEndian;
  }
  



  public long getIFDOffset()
  {
    return IFDOffset;
  }
  



  public long getNextIFDOffset()
  {
    return nextIFDOffset;
  }
}
