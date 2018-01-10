package com.drew.metadata;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;

public class Face
{
  private final int _x;
  private final int _y;
  private final int _width;
  private final int _height;
  @Nullable
  private final String _name;
  @Nullable
  private final Age _age;
  
  public Face(int paramInt1, int paramInt2, int paramInt3, int paramInt4, @Nullable String paramString, @Nullable Age paramAge)
  {
    _x = paramInt1;
    _y = paramInt2;
    _width = paramInt3;
    _height = paramInt4;
    _name = paramString;
    _age = paramAge;
  }
  
  public int getX()
  {
    return _x;
  }
  
  public int getY()
  {
    return _y;
  }
  
  public int getWidth()
  {
    return _width;
  }
  
  public int getHeight()
  {
    return _height;
  }
  
  @Nullable
  public String getName()
  {
    return _name;
  }
  
  @Nullable
  public Age getAge()
  {
    return _age;
  }
  
  public boolean equals(@Nullable Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject == null) || (getClass() != paramObject.getClass())) {
      return false;
    }
    Face localFace = (Face)paramObject;
    if (_height != _height) {
      return false;
    }
    if (_width != _width) {
      return false;
    }
    if (_x != _x) {
      return false;
    }
    if (_y != _y) {
      return false;
    }
    if (_age != null ? !_age.equals(_age) : _age != null) {
      return false;
    }
    return _name != null ? _name.equals(_name) : _name == null;
  }
  
  public int hashCode()
  {
    int i = _x;
    i = 31 * i + _y;
    i = 31 * i + _width;
    i = 31 * i + _height;
    i = 31 * i + (_name != null ? _name.hashCode() : 0);
    i = 31 * i + (_age != null ? _age.hashCode() : 0);
    return i;
  }
  
  @NotNull
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("x: ").append(_x);
    localStringBuilder.append(" y: ").append(_y);
    localStringBuilder.append(" width: ").append(_width);
    localStringBuilder.append(" height: ").append(_height);
    if (_name != null) {
      localStringBuilder.append(" name: ").append(_name);
    }
    if (_age != null) {
      localStringBuilder.append(" age: ").append(_age.toFriendlyString());
    }
    return localStringBuilder.toString();
  }
}
