package com.drew.metadata;

import com.drew.lang.Rational;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.lang.annotations.SuppressWarnings;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public abstract class Directory
{
  @NotNull
  protected final Map<Integer, Object> _tagMap = new HashMap();
  @NotNull
  protected final Collection<Tag> _definedTagList = new ArrayList();
  @NotNull
  private final Collection<String> _errorList = new ArrayList(4);
  protected TagDescriptor _descriptor;
  
  @NotNull
  public abstract String getName();
  
  @NotNull
  protected abstract HashMap<Integer, String> getTagNameMap();
  
  protected Directory() {}
  
  public boolean containsTag(int paramInt)
  {
    return _tagMap.containsKey(Integer.valueOf(paramInt));
  }
  
  @NotNull
  public Collection<Tag> getTags()
  {
    return _definedTagList;
  }
  
  public int getTagCount()
  {
    return _definedTagList.size();
  }
  
  public void setDescriptor(@NotNull TagDescriptor paramTagDescriptor)
  {
    if (paramTagDescriptor == null) {
      throw new NullPointerException("cannot set a null descriptor");
    }
    _descriptor = paramTagDescriptor;
  }
  
  public void addError(@NotNull String paramString)
  {
    _errorList.add(paramString);
  }
  
  public boolean hasErrors()
  {
    return _errorList.size() > 0;
  }
  
  @NotNull
  public Iterable<String> getErrors()
  {
    return _errorList;
  }
  
  public int getErrorCount()
  {
    return _errorList.size();
  }
  
  public void setInt(int paramInt1, int paramInt2)
  {
    setObject(paramInt1, Integer.valueOf(paramInt2));
  }
  
  public void setIntArray(int paramInt, @NotNull int[] paramArrayOfInt)
  {
    setObjectArray(paramInt, paramArrayOfInt);
  }
  
  public void setFloat(int paramInt, float paramFloat)
  {
    setObject(paramInt, Float.valueOf(paramFloat));
  }
  
  public void setFloatArray(int paramInt, @NotNull float[] paramArrayOfFloat)
  {
    setObjectArray(paramInt, paramArrayOfFloat);
  }
  
  public void setDouble(int paramInt, double paramDouble)
  {
    setObject(paramInt, Double.valueOf(paramDouble));
  }
  
  public void setDoubleArray(int paramInt, @NotNull double[] paramArrayOfDouble)
  {
    setObjectArray(paramInt, paramArrayOfDouble);
  }
  
  public void setString(int paramInt, @NotNull String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException("cannot set a null String");
    }
    setObject(paramInt, paramString);
  }
  
  public void setStringArray(int paramInt, @NotNull String[] paramArrayOfString)
  {
    setObjectArray(paramInt, paramArrayOfString);
  }
  
  public void setBoolean(int paramInt, boolean paramBoolean)
  {
    setObject(paramInt, Boolean.valueOf(paramBoolean));
  }
  
  public void setLong(int paramInt, long paramLong)
  {
    setObject(paramInt, Long.valueOf(paramLong));
  }
  
  public void setDate(int paramInt, @NotNull Date paramDate)
  {
    setObject(paramInt, paramDate);
  }
  
  public void setRational(int paramInt, @NotNull Rational paramRational)
  {
    setObject(paramInt, paramRational);
  }
  
  public void setRationalArray(int paramInt, @NotNull Rational[] paramArrayOfRational)
  {
    setObjectArray(paramInt, paramArrayOfRational);
  }
  
  public void setByteArray(int paramInt, @NotNull byte[] paramArrayOfByte)
  {
    setObjectArray(paramInt, paramArrayOfByte);
  }
  
  public void setObject(int paramInt, @NotNull Object paramObject)
  {
    if (paramObject == null) {
      throw new NullPointerException("cannot set a null object");
    }
    if (!_tagMap.containsKey(Integer.valueOf(paramInt))) {
      _definedTagList.add(new Tag(paramInt, this));
    }
    _tagMap.put(Integer.valueOf(paramInt), paramObject);
  }
  
  public void setObjectArray(int paramInt, @NotNull Object paramObject)
  {
    setObject(paramInt, paramObject);
  }
  
  public int getInt(int paramInt)
    throws MetadataException
  {
    Integer localInteger = getInteger(paramInt);
    if (localInteger != null) {
      return localInteger.intValue();
    }
    Object localObject = getObject(paramInt);
    if (localObject == null) {
      throw new MetadataException("Tag '" + getTagName(paramInt) + "' has not been set -- check using containsTag() first");
    }
    throw new MetadataException("Tag '" + paramInt + "' cannot be converted to int.  It is of type '" + localObject.getClass() + "'.");
  }
  
  @Nullable
  public Integer getInteger(int paramInt)
  {
    Object localObject1 = getObject(paramInt);
    if (localObject1 == null) {
      return null;
    }
    if ((localObject1 instanceof String)) {
      try
      {
        return Integer.valueOf(Integer.parseInt((String)localObject1));
      }
      catch (NumberFormatException localNumberFormatException)
      {
        String str = (String)localObject1;
        byte[] arrayOfByte1 = str.getBytes();
        long l = 0L;
        for (int k : arrayOfByte1)
        {
          l <<= 8;
          l += (k & 0xFF);
        }
        return Integer.valueOf((int)l);
      }
    }
    if ((localObject1 instanceof Number)) {
      return Integer.valueOf(((Number)localObject1).intValue());
    }
    Object localObject2;
    if ((localObject1 instanceof Rational[]))
    {
      localObject2 = (Rational[])localObject1;
      if (localObject2.length == 1) {
        return Integer.valueOf(localObject2[0].intValue());
      }
    }
    else if ((localObject1 instanceof byte[]))
    {
      localObject2 = (byte[])localObject1;
      if (localObject2.length == 1) {
        return Integer.valueOf(localObject2[0]);
      }
    }
    else if ((localObject1 instanceof int[]))
    {
      localObject2 = (int[])localObject1;
      if (localObject2.length == 1) {
        return Integer.valueOf(localObject2[0]);
      }
    }
    return null;
  }
  
  @Nullable
  public String[] getStringArray(int paramInt)
  {
    Object localObject1 = getObject(paramInt);
    if (localObject1 == null) {
      return null;
    }
    if ((localObject1 instanceof String[])) {
      return (String[])localObject1;
    }
    if ((localObject1 instanceof String)) {
      return new String[] { (String)localObject1 };
    }
    Object localObject2;
    String[] arrayOfString;
    int i;
    if ((localObject1 instanceof int[]))
    {
      localObject2 = (int[])localObject1;
      arrayOfString = new String[localObject2.length];
      for (i = 0; i < arrayOfString.length; i++) {
        arrayOfString[i] = Integer.toString(localObject2[i]);
      }
      return arrayOfString;
    }
    if ((localObject1 instanceof byte[]))
    {
      localObject2 = (byte[])localObject1;
      arrayOfString = new String[localObject2.length];
      for (i = 0; i < arrayOfString.length; i++) {
        arrayOfString[i] = Byte.toString(localObject2[i]);
      }
      return arrayOfString;
    }
    if ((localObject1 instanceof Rational[]))
    {
      localObject2 = (Rational[])localObject1;
      arrayOfString = new String[localObject2.length];
      for (i = 0; i < arrayOfString.length; i++) {
        arrayOfString[i] = localObject2[i].toSimpleString(false);
      }
      return arrayOfString;
    }
    return null;
  }
  
  @Nullable
  public int[] getIntArray(int paramInt)
  {
    Object localObject1 = getObject(paramInt);
    if (localObject1 == null) {
      return null;
    }
    Object localObject2;
    int[] arrayOfInt;
    int i;
    if ((localObject1 instanceof Rational[]))
    {
      localObject2 = (Rational[])localObject1;
      arrayOfInt = new int[localObject2.length];
      for (i = 0; i < arrayOfInt.length; i++) {
        arrayOfInt[i] = localObject2[i].intValue();
      }
      return arrayOfInt;
    }
    if ((localObject1 instanceof int[])) {
      return (int[])localObject1;
    }
    if ((localObject1 instanceof byte[]))
    {
      localObject2 = (byte[])localObject1;
      arrayOfInt = new int[localObject2.length];
      for (i = 0; i < localObject2.length; i++)
      {
        int j = localObject2[i];
        arrayOfInt[i] = j;
      }
      return arrayOfInt;
    }
    if ((localObject1 instanceof CharSequence))
    {
      localObject2 = (CharSequence)localObject1;
      arrayOfInt = new int[((CharSequence)localObject2).length()];
      for (i = 0; i < ((CharSequence)localObject2).length(); i++) {
        arrayOfInt[i] = ((CharSequence)localObject2).charAt(i);
      }
      return arrayOfInt;
    }
    if ((localObject1 instanceof Integer)) {
      return new int[] { ((Integer)localObject1).intValue() };
    }
    return null;
  }
  
  @Nullable
  public byte[] getByteArray(int paramInt)
  {
    Object localObject1 = getObject(paramInt);
    if (localObject1 == null) {
      return null;
    }
    Object localObject2;
    byte[] arrayOfByte;
    int i;
    if ((localObject1 instanceof Rational[]))
    {
      localObject2 = (Rational[])localObject1;
      arrayOfByte = new byte[localObject2.length];
      for (i = 0; i < arrayOfByte.length; i++) {
        arrayOfByte[i] = localObject2[i].byteValue();
      }
      return arrayOfByte;
    }
    if ((localObject1 instanceof byte[])) {
      return (byte[])localObject1;
    }
    if ((localObject1 instanceof int[]))
    {
      localObject2 = (int[])localObject1;
      arrayOfByte = new byte[localObject2.length];
      for (i = 0; i < localObject2.length; i++) {
        arrayOfByte[i] = ((byte)localObject2[i]);
      }
      return arrayOfByte;
    }
    if ((localObject1 instanceof CharSequence))
    {
      localObject2 = (CharSequence)localObject1;
      arrayOfByte = new byte[((CharSequence)localObject2).length()];
      for (i = 0; i < ((CharSequence)localObject2).length(); i++) {
        arrayOfByte[i] = ((byte)((CharSequence)localObject2).charAt(i));
      }
      return arrayOfByte;
    }
    if ((localObject1 instanceof Integer)) {
      return new byte[] { ((Integer)localObject1).byteValue() };
    }
    return null;
  }
  
  public double getDouble(int paramInt)
    throws MetadataException
  {
    Double localDouble = getDoubleObject(paramInt);
    if (localDouble != null) {
      return localDouble.doubleValue();
    }
    Object localObject = getObject(paramInt);
    if (localObject == null) {
      throw new MetadataException("Tag '" + getTagName(paramInt) + "' has not been set -- check using containsTag() first");
    }
    throw new MetadataException("Tag '" + paramInt + "' cannot be converted to a double.  It is of type '" + localObject.getClass() + "'.");
  }
  
  @Nullable
  public Double getDoubleObject(int paramInt)
  {
    Object localObject = getObject(paramInt);
    if (localObject == null) {
      return null;
    }
    if ((localObject instanceof String)) {
      try
      {
        return Double.valueOf(Double.parseDouble((String)localObject));
      }
      catch (NumberFormatException localNumberFormatException)
      {
        return null;
      }
    }
    if ((localObject instanceof Number)) {
      return Double.valueOf(((Number)localObject).doubleValue());
    }
    return null;
  }
  
  public float getFloat(int paramInt)
    throws MetadataException
  {
    Float localFloat = getFloatObject(paramInt);
    if (localFloat != null) {
      return localFloat.floatValue();
    }
    Object localObject = getObject(paramInt);
    if (localObject == null) {
      throw new MetadataException("Tag '" + getTagName(paramInt) + "' has not been set -- check using containsTag() first");
    }
    throw new MetadataException("Tag '" + paramInt + "' cannot be converted to a float.  It is of type '" + localObject.getClass() + "'.");
  }
  
  @Nullable
  public Float getFloatObject(int paramInt)
  {
    Object localObject = getObject(paramInt);
    if (localObject == null) {
      return null;
    }
    if ((localObject instanceof String)) {
      try
      {
        return Float.valueOf(Float.parseFloat((String)localObject));
      }
      catch (NumberFormatException localNumberFormatException)
      {
        return null;
      }
    }
    if ((localObject instanceof Number)) {
      return Float.valueOf(((Number)localObject).floatValue());
    }
    return null;
  }
  
  public long getLong(int paramInt)
    throws MetadataException
  {
    Long localLong = getLongObject(paramInt);
    if (localLong != null) {
      return localLong.longValue();
    }
    Object localObject = getObject(paramInt);
    if (localObject == null) {
      throw new MetadataException("Tag '" + getTagName(paramInt) + "' has not been set -- check using containsTag() first");
    }
    throw new MetadataException("Tag '" + paramInt + "' cannot be converted to a long.  It is of type '" + localObject.getClass() + "'.");
  }
  
  @Nullable
  public Long getLongObject(int paramInt)
  {
    Object localObject = getObject(paramInt);
    if (localObject == null) {
      return null;
    }
    if ((localObject instanceof String)) {
      try
      {
        return Long.valueOf(Long.parseLong((String)localObject));
      }
      catch (NumberFormatException localNumberFormatException)
      {
        return null;
      }
    }
    if ((localObject instanceof Number)) {
      return Long.valueOf(((Number)localObject).longValue());
    }
    return null;
  }
  
  public boolean getBoolean(int paramInt)
    throws MetadataException
  {
    Boolean localBoolean = getBooleanObject(paramInt);
    if (localBoolean != null) {
      return localBoolean.booleanValue();
    }
    Object localObject = getObject(paramInt);
    if (localObject == null) {
      throw new MetadataException("Tag '" + getTagName(paramInt) + "' has not been set -- check using containsTag() first");
    }
    throw new MetadataException("Tag '" + paramInt + "' cannot be converted to a boolean.  It is of type '" + localObject.getClass() + "'.");
  }
  
  @Nullable
  @SuppressWarnings(value="NP_BOOLEAN_RETURN_NULL", justification="keep API interface consistent")
  public Boolean getBooleanObject(int paramInt)
  {
    Object localObject = getObject(paramInt);
    if (localObject == null) {
      return null;
    }
    if ((localObject instanceof Boolean)) {
      return (Boolean)localObject;
    }
    if ((localObject instanceof String)) {
      try
      {
        return Boolean.valueOf(Boolean.getBoolean((String)localObject));
      }
      catch (NumberFormatException localNumberFormatException)
      {
        return null;
      }
    }
    if ((localObject instanceof Number)) {
      return Boolean.valueOf(((Number)localObject).doubleValue() != 0.0D);
    }
    return null;
  }
  
  @Nullable
  public Date getDate(int paramInt)
  {
    return getDate(paramInt, null);
  }
  
  @Nullable
  public Date getDate(int paramInt, @Nullable TimeZone paramTimeZone)
  {
    Object localObject = getObject(paramInt);
    if (localObject == null) {
      return null;
    }
    if ((localObject instanceof Date)) {
      return (Date)localObject;
    }
    if ((localObject instanceof String))
    {
      String[] arrayOfString1 = { "yyyy:MM:dd HH:mm:ss", "yyyy:MM:dd HH:mm", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm" };
      String str1 = (String)localObject;
      String[] arrayOfString2 = arrayOfString1;
      int i = arrayOfString2.length;
      int j = 0;
      while (j < i)
      {
        String str2 = arrayOfString2[j];
        try
        {
          SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(str2);
          if (paramTimeZone != null) {
            localSimpleDateFormat.setTimeZone(paramTimeZone);
          }
          return localSimpleDateFormat.parse(str1);
        }
        catch (ParseException localParseException)
        {
          j++;
        }
      }
    }
    return null;
  }
  
  @Nullable
  public Rational getRational(int paramInt)
  {
    Object localObject = getObject(paramInt);
    if (localObject == null) {
      return null;
    }
    if ((localObject instanceof Rational)) {
      return (Rational)localObject;
    }
    if ((localObject instanceof Integer)) {
      return new Rational(((Integer)localObject).intValue(), 1L);
    }
    if ((localObject instanceof Long)) {
      return new Rational(((Long)localObject).longValue(), 1L);
    }
    return null;
  }
  
  @Nullable
  public Rational[] getRationalArray(int paramInt)
  {
    Object localObject = getObject(paramInt);
    if (localObject == null) {
      return null;
    }
    if ((localObject instanceof Rational[])) {
      return (Rational[])localObject;
    }
    return null;
  }
  
  @Nullable
  public String getString(int paramInt)
  {
    Object localObject = getObject(paramInt);
    if (localObject == null) {
      return null;
    }
    if ((localObject instanceof Rational)) {
      return ((Rational)localObject).toSimpleString(true);
    }
    if (localObject.getClass().isArray())
    {
      int i = Array.getLength(localObject);
      Class localClass = localObject.getClass().getComponentType();
      boolean bool1 = Object.class.isAssignableFrom(localClass);
      boolean bool2 = localClass.getName().equals("float");
      boolean bool3 = localClass.getName().equals("double");
      boolean bool4 = localClass.getName().equals("int");
      boolean bool5 = localClass.getName().equals("long");
      boolean bool6 = localClass.getName().equals("byte");
      StringBuilder localStringBuilder = new StringBuilder();
      for (int j = 0; j < i; j++)
      {
        if (j != 0) {
          localStringBuilder.append(' ');
        }
        if (bool1) {
          localStringBuilder.append(Array.get(localObject, j).toString());
        } else if (bool4) {
          localStringBuilder.append(Array.getInt(localObject, j));
        } else if (bool5) {
          localStringBuilder.append(Array.getLong(localObject, j));
        } else if (bool2) {
          localStringBuilder.append(Array.getFloat(localObject, j));
        } else if (bool3) {
          localStringBuilder.append(Array.getDouble(localObject, j));
        } else if (bool6) {
          localStringBuilder.append(Array.getByte(localObject, j));
        } else {
          addError("Unexpected array component type: " + localClass.getName());
        }
      }
      return localStringBuilder.toString();
    }
    return localObject.toString();
  }
  
  @Nullable
  public String getString(int paramInt, String paramString)
  {
    byte[] arrayOfByte = getByteArray(paramInt);
    if (arrayOfByte == null) {
      return null;
    }
    try
    {
      return new String(arrayOfByte, paramString);
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException) {}
    return null;
  }
  
  @Nullable
  public Object getObject(int paramInt)
  {
    return _tagMap.get(Integer.valueOf(paramInt));
  }
  
  @NotNull
  public String getTagName(int paramInt)
  {
    HashMap localHashMap = getTagNameMap();
    if (!localHashMap.containsKey(Integer.valueOf(paramInt)))
    {
      for (String str = Integer.toHexString(paramInt); str.length() < 4; str = "0" + str) {}
      return "Unknown tag (0x" + str + ")";
    }
    return (String)localHashMap.get(Integer.valueOf(paramInt));
  }
  
  @Nullable
  public String getDescription(int paramInt)
  {
    assert (_descriptor != null);
    return _descriptor.getDescription(paramInt);
  }
}
