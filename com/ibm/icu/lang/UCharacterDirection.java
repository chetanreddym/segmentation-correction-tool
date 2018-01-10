package com.ibm.icu.lang;







public final class UCharacterDirection
{
  public static final int LEFT_TO_RIGHT = 0;
  





  public static final int RIGHT_TO_LEFT = 1;
  





  public static final int EUROPEAN_NUMBER = 2;
  





  public static final int EUROPEAN_NUMBER_SEPARATOR = 3;
  





  public static final int EUROPEAN_NUMBER_TERMINATOR = 4;
  





  public static final int ARABIC_NUMBER = 5;
  





  public static final int COMMON_NUMBER_SEPARATOR = 6;
  





  public static final int BLOCK_SEPARATOR = 7;
  





  public static final int SEGMENT_SEPARATOR = 8;
  





  public static final int WHITE_SPACE_NEUTRAL = 9;
  





  public static final int OTHER_NEUTRAL = 10;
  





  public static final int LEFT_TO_RIGHT_EMBEDDING = 11;
  





  public static final int LEFT_TO_RIGHT_OVERRIDE = 12;
  




  public static final int RIGHT_TO_LEFT_ARABIC = 13;
  




  public static final int RIGHT_TO_LEFT_EMBEDDING = 14;
  




  public static final int RIGHT_TO_LEFT_OVERRIDE = 15;
  




  public static final int POP_DIRECTIONAL_FORMAT = 16;
  




  public static final int DIR_NON_SPACING_MARK = 17;
  




  public static final int BOUNDARY_NEUTRAL = 18;
  




  public static final int CHAR_DIRECTION_COUNT = 19;
  





  private UCharacterDirection() {}
  





  public static String toString(int dir)
  {
    switch (dir)
    {
    case 0: 
      return "Left-to-Right";
    case 1: 
      return "Right-to-Left";
    case 2: 
      return "European Number";
    case 3: 
      return "European Number Separator";
    case 4: 
      return "European Number Terminator";
    case 5: 
      return "Arabic Number";
    case 6: 
      return "Common Number Separator";
    case 7: 
      return "Paragraph Separator";
    case 8: 
      return "Segment Separator";
    case 9: 
      return "Whitespace";
    case 10: 
      return "Other Neutrals";
    case 11: 
      return "Left-to-Right Embedding";
    case 12: 
      return "Left-to-Right Override";
    case 13: 
      return "Right-to-Left Arabic";
    case 14: 
      return "Right-to-Left Embedding";
    case 15: 
      return "Right-to-Left Override";
    case 16: 
      return "Pop Directional Format";
    case 17: 
      return "Non-Spacing Mark";
    case 18: 
      return "Boundary Neutral";
    }
    return "Unassigned";
  }
}
