package gttool.misc;

import java.awt.Color;

public class HexColor {
  public HexColor() {}
  
  public static Color newColor(String hexCodeOrColorName) {
    String str = hexCodeOrColorName;
    if ((str.startsWith("#")) || (str.startsWith("0x")))
    {







      return Color.decode(str);
    }
    Color color;
    try
    {
      Class colorClass = Class.forName("java.awt.Color");
      java.lang.reflect.Field colorField = colorClass.getField(str.toUpperCase());
      color = (Color)colorField.get(null);
    } catch (Exception e) { Color color;
      e.printStackTrace();
      color = Color.BLACK;
    }
    return color;
  }
  

  public static String toHexString(Color c)
  {
    String r = Integer.toHexString(c.getRed());
    String g = Integer.toHexString(c.getGreen());
    String b = Integer.toHexString(c.getBlue());
    
    if (r.length() < 2) r = "0" + r;
    if (g.length() < 2) g = "0" + g;
    if (b.length() < 2) { b = "0" + b;
    }
    return "#" + r + g + b;
  }
}
