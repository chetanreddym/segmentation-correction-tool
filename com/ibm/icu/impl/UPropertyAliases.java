package com.ibm.icu.impl;

import com.ibm.icu.lang.UCharacter;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;



































































public final class UPropertyAliases
  implements ICUBinary.Authenticate
{
  private NonContiguousEnumToShort enumToName;
  private NameToEnum nameToEnum;
  private NonContiguousEnumToShort enumToValue;
  private ValueMap[] valueMapArray;
  private short[] nameGroupPool;
  private String[] stringPool;
  private static boolean DEBUG = ICUDebug.enabled("pnames");
  




  private static final byte[] DATA_FORMAT_ID = { 112, 110, 97, 109 };
  





  private static final byte DATA_FORMAT_VERSION = 1;
  




  private static final String DATA_FILE_NAME = "data/pnames.icu";
  




  private static final int DATA_BUFFER_SIZE = 8192;
  





  public UPropertyAliases()
    throws IOException
  {
    InputStream is = getClass().getResourceAsStream("data/pnames.icu");
    BufferedInputStream b = new BufferedInputStream(is, 8192);
    
    ICUBinary.readHeader(b, DATA_FORMAT_ID, this);
    DataInputStream d = new DataInputStream(b);
    


    d.mark(256);
    
    short enumToName_offset = d.readShort();
    short nameToEnum_offset = d.readShort();
    short enumToValue_offset = d.readShort();
    short total_size = d.readShort();
    short valueMap_offset = d.readShort();
    short valueMap_count = d.readShort();
    short nameGroupPool_offset = d.readShort();
    short nameGroupPool_count = d.readShort();
    short stringPool_offset = d.readShort();
    short stringPool_count = d.readShort();
    
    if (DEBUG) {
      System.out.println("enumToName_offset=" + enumToName_offset + "\n" + "nameToEnum_offset=" + nameToEnum_offset + "\n" + "enumToValue_offset=" + enumToValue_offset + "\n" + "total_size=" + total_size + "\n" + "valueMap_offset=" + valueMap_offset + "\n" + "valueMap_count=" + valueMap_count + "\n" + "nameGroupPool_offset=" + nameGroupPool_offset + "\n" + "nameGroupPool_count=" + nameGroupPool_count + "\n" + "stringPool_offset=" + stringPool_offset + "\n" + "stringPool_count=" + stringPool_count);
    }
    














    byte[] raw = new byte[total_size];
    d.reset();
    d.read(raw);
    d.close();
    
    Builder builder = new Builder(raw);
    
    stringPool = builder.readStringPool(stringPool_offset, stringPool_count);
    

    nameGroupPool = builder.readNameGroupPool(nameGroupPool_offset, nameGroupPool_count);
    

    builder.setupValueMap_map(valueMap_offset, valueMap_count);
    





    builder.seek(enumToName_offset);
    enumToName = new NonContiguousEnumToShort(builder);
    builder.nameGroupOffsetToIndex(enumToName.offsetArray);
    
    builder.seek(nameToEnum_offset);
    nameToEnum = new NameToEnum(builder);
    
    builder.seek(enumToValue_offset);
    enumToValue = new NonContiguousEnumToShort(builder);
    builder.valueMapOffsetToIndex(enumToValue.offsetArray);
    
    valueMapArray = new ValueMap[valueMap_count];
    for (int i = 0; i < valueMap_count; i++)
    {
      builder.seek(valueMap_map[i]);
      valueMapArray[i] = new ValueMap(builder);
    }
    
    builder.close();
  }
  








  public String getPropertyName(int property, int nameChoice)
  {
    short nameGroupIndex = enumToName.getShort(property);
    return chooseNameInGroup(nameGroupIndex, nameChoice);
  }
  


  public int getPropertyEnum(String propertyAlias)
  {
    return nameToEnum.getEnum(propertyAlias);
  }
  






  public String getPropertyValueName(int property, int value, int nameChoice)
  {
    ValueMap vm = getValueMap(property);
    short nameGroupIndex = enumToName.getShort(value);
    return chooseNameInGroup(nameGroupIndex, nameChoice);
  }
  




  public int getPropertyValueEnum(int property, String valueAlias)
  {
    ValueMap vm = getValueMap(property);
    return nameToEnum.getEnum(valueAlias);
  }
  




  private class ValueMap
  {
    UPropertyAliases.EnumToShort enumToName;
    



    UPropertyAliases.NameToEnum nameToEnum;
    



    ValueMap(UPropertyAliases.Builder b)
      throws IOException
    {
      short enumToName_offset = b.readShort();
      short ncEnumToName_offset = b.readShort();
      short nameToEnum_offset = b.readShort();
      if (enumToName_offset != 0) {
        b.seek(enumToName_offset);
        UPropertyAliases.ContiguousEnumToShort x = new UPropertyAliases.ContiguousEnumToShort(b);
        b.nameGroupOffsetToIndex(offsetArray);
        enumToName = x;
      } else {
        b.seek(ncEnumToName_offset);
        UPropertyAliases.NonContiguousEnumToShort x = new UPropertyAliases.NonContiguousEnumToShort(b);
        b.nameGroupOffsetToIndex(offsetArray);
        enumToName = x;
      }
      b.seek(nameToEnum_offset);
      nameToEnum = new UPropertyAliases.NameToEnum(UPropertyAliases.this, b);
    }
  }
  

  private static abstract interface EnumToShort
  {
    public abstract short getShort(int paramInt);
  }
  

  private static class ContiguousEnumToShort
    implements UPropertyAliases.EnumToShort
  {
    int enumStart;
    
    int enumLimit;
    short[] offsetArray;
    
    public short getShort(int enumProbe)
    {
      if ((enumProbe < enumStart) || (enumProbe >= enumLimit)) {
        throw new IllegalArgumentException("Invalid enum. enumStart = " + enumStart + " enumLimit = " + enumLimit + " enumProbe = " + enumProbe);
      }
      

      return offsetArray[(enumProbe - enumStart)];
    }
    
    ContiguousEnumToShort(ICUBinaryStream s) throws IOException {
      enumStart = s.readInt();
      enumLimit = s.readInt();
      int count = enumLimit - enumStart;
      offsetArray = new short[count];
      for (int i = 0; i < count; i++) {
        offsetArray[i] = s.readShort();
      }
    }
  }
  

  private static class NonContiguousEnumToShort
    implements UPropertyAliases.EnumToShort
  {
    int[] enumArray;
    short[] offsetArray;
    
    public short getShort(int enumProbe)
    {
      for (int i = 0; i < enumArray.length; i++)
        if (enumArray[i] >= enumProbe) {
          if (enumArray[i] > enumProbe) break;
          return offsetArray[i];
        }
      throw new IllegalArgumentException("Invalid enum");
    }
    
    NonContiguousEnumToShort(ICUBinaryStream s) throws IOException
    {
      int count = s.readInt();
      enumArray = new int[count];
      offsetArray = new short[count];
      for (int i = 0; i < count; i++) {
        enumArray[i] = s.readInt();
      }
      for (i = 0; i < count; i++) {
        offsetArray[i] = s.readShort();
      }
    }
  }
  

  private class NameToEnum
  {
    int[] enumArray;
    short[] nameArray;
    
    int getEnum(String nameProbe)
    {
      for (int i = 0; i < nameArray.length; i++) {
        int c = UPropertyAliases.compare(nameProbe, stringPool[nameArray[i]]);
        
        if (c <= 0) {
          if (c < 0) break;
          return enumArray[i];
        } }
      throw new IllegalArgumentException("Invalid name: " + nameProbe);
    }
    
    NameToEnum(UPropertyAliases.Builder b) throws IOException
    {
      int count = b.readInt();
      enumArray = new int[count];
      nameArray = new short[count];
      for (int i = 0; i < count; i++) {
        enumArray[i] = b.readInt();
      }
      for (i = 0; i < count; i++) {
        nameArray[i] = b.stringOffsetToIndex(b.readShort());
      }
    }
  }
  









  public static int compare(String stra, String strb)
  {
    int istra = 0;int istrb = 0;
    int cstra = 0;int cstrb = 0;
    
    for (;;)
    {
      cstra = stra.charAt(istra);
      switch (cstra) {
      case 9: case 10: case 11: case 12: 
      case 13: case 32: case 45: case 95: 
        istra++;
        break;
      }
      while (istra >= stra.length())
      {









        while (istrb < strb.length()) {
          cstrb = strb.charAt(istrb);
          switch (cstrb) {
          case 9: case 10: case 11: case 12: 
          case 13: case 32: case 45: case 95: 
            istrb++;
            break;
          default: 
            break label221;
          }
        }
        label221:
        boolean endstra = istra == stra.length();
        boolean endstrb = istrb == strb.length();
        if (endstra) {
          if (endstrb) return 0;
          cstra = 0;
        } else if (endstrb) {
          cstrb = 0;
        }
        
        int rc = UCharacter.toLowerCase(cstra) - UCharacter.toLowerCase(cstrb);
        if (rc != 0) {
          return rc;
        }
        
        istra++;
        istrb++;
      }
    }
  }
  


  private String chooseNameInGroup(short nameGroupIndex, int nameChoice)
  {
    if (nameChoice < 0) {
      throw new IllegalArgumentException("Invalid name choice");
    }
    while (nameChoice-- > 0) {
      nameGroupIndex = (short)(nameGroupIndex + 1); if (nameGroupPool[nameGroupIndex] < 0) {
        throw new IllegalArgumentException("Invalid name choice");
      }
    }
    short a = nameGroupPool[nameGroupIndex];
    return stringPool[a];
  }
  


  private ValueMap getValueMap(int property)
  {
    int valueMapIndex = enumToValue.getShort(property);
    return valueMapArray[valueMapIndex];
  }
  





  public boolean isDataVersionAcceptable(byte[] version)
  {
    return version[0] == 1;
  }
  



  static class Builder
    extends ICUBinaryStream
  {
    private short[] stringPool_map;
    


    private short[] valueMap_map;
    


    private short[] nameGroup_map;
    



    public Builder(byte[] raw)
    {
      super();
    }
    



    public void setupValueMap_map(short offset, short count)
    {
      valueMap_map = new short[count];
      for (int i = 0; i < count; i++)
      {
        valueMap_map[i] = ((short)(offset + i * 6));
      }
    }
    



    public String[] readStringPool(short offset, short count)
      throws IOException
    {
      seek(offset);
      


      String[] stringPool = new String[count + 1];
      stringPool_map = new short[count + 1];
      short pos = offset;
      StringBuffer buf = new StringBuffer();
      stringPool_map[0] = 0;
      for (int i = 1; i <= count; i++) {
        buf.setLength(0);
        for (;;)
        {
          char c = (char)readUnsignedByte();
          if (c == 0) break;
          buf.append(c);
        }
        stringPool_map[i] = pos;
        stringPool[i] = buf.toString();
        pos = (short)(pos + (stringPool[i].length() + 1));
      }
      if (UPropertyAliases.DEBUG) {
        System.out.println("read stringPool x " + count + ": " + stringPool[1] + ", " + stringPool[2] + ", " + stringPool[3] + ",...");
      }
      


      return stringPool;
    }
    








    public short[] readNameGroupPool(short offset, short count)
      throws IOException
    {
      seek(offset);
      short pos = offset;
      short[] nameGroupPool = new short[count];
      nameGroup_map = new short[count];
      for (int i = 0; i < count; i++) {
        nameGroup_map[i] = pos;
        nameGroupPool[i] = stringOffsetToIndex(readShort());
        pos = (short)(pos + 2);
      }
      if (UPropertyAliases.DEBUG) {
        System.out.println("read nameGroupPool x " + count + ": " + nameGroupPool[0] + ", " + nameGroupPool[1] + ", " + nameGroupPool[2] + ",...");
      }
      


      return nameGroupPool;
    }
    



    private short stringOffsetToIndex(short offset)
    {
      int probe = offset;
      if (probe < 0) probe = -probe;
      for (int i = 0; i < stringPool_map.length; i++) {
        if (stringPool_map[i] == probe) {
          return offset < 0 ? (short)-i : (short)i;
        }
      }
      throw new RuntimeException("Can't map string pool offset " + offset + " to index");
    }
    






    private void stringOffsetToIndex(short[] array)
    {
      for (int i = 0; i < array.length; i++) {
        array[i] = stringOffsetToIndex(array[i]);
      }
    }
    




    private short valueMapOffsetToIndex(short offset)
    {
      for (short i = 0; i < valueMap_map.length; i = (short)(i + 1)) {
        if (valueMap_map[i] == offset) {
          return i;
        }
      }
      throw new RuntimeException("Can't map value map offset " + offset + " to index");
    }
    





    private void valueMapOffsetToIndex(short[] array)
    {
      for (int i = 0; i < array.length; i++) {
        array[i] = valueMapOffsetToIndex(array[i]);
      }
    }
    



    private short nameGroupOffsetToIndex(short offset)
    {
      for (short i = 0; i < nameGroup_map.length; i = (short)(i + 1)) {
        if (nameGroup_map[i] == offset) {
          return i;
        }
      }
      throw new RuntimeException("Can't map name group offset " + offset + " to index");
    }
    





    private void nameGroupOffsetToIndex(short[] array)
    {
      for (int i = 0; i < array.length; i++) {
        array[i] = nameGroupOffsetToIndex(array[i]);
      }
    }
  }
}
