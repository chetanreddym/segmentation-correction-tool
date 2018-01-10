package javax.mail.internet;

import java.text.ParseException;































































































































































































































































































































































































































class MailDateParser
{
  int index;
  char[] orig;
  
  public MailDateParser(char[] paramArrayOfChar)
  {
    orig = paramArrayOfChar;
  }
  


  public void skipUntilNumber()
    throws ParseException
  {
    try
    {
      for (;;)
      {
        switch (orig[index]) {
        case '0': 
        case '1': 
        case '2': 
        case '3': 
        case '4': 
        case '5': 
        case '6': 
        case '7': 
        case '8': 
        case '9': 
          return;
        }
        
        index += 1;
      }
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
    {
      throw new ParseException("No Number Found", index);
    }
  }
  


  public void skipWhiteSpace()
  {
    int i = orig.length;
    while (index < i) {
      switch (orig[index]) {
      case '\t': 
      case ' ': 
        index += 1;
        break;
      default: 
        return;
      }
      
    }
  }
  



  public int peekChar()
    throws ParseException
  {
    if (index < orig.length) {
      return orig[index];
    }
    throw new ParseException("No more characters", index);
  }
  


  public void skipChar(char paramChar)
    throws ParseException
  {
    if (index < orig.length) {
      if (orig[index] == paramChar) {
        index += 1;return;
      }
      throw new ParseException("Wrong char", index);
    }
    
    throw new ParseException("No more characters", index);
  }
  



  public boolean skipIfChar(char paramChar)
    throws ParseException
  {
    if (index < orig.length) {
      if (orig[index] == paramChar) {
        index += 1;
        return true;
      }
      return false;
    }
    
    throw new ParseException("No more characters", index);
  }
  





  public int parseNumber()
    throws ParseException
  {
    int i = orig.length;
    int j = 0;
    int k = 0;
    
    while (index < i) {
      switch (orig[index]) {
      case '0': 
        k *= 10;
        j = 1;
        break;
      
      case '1': 
        k = k * 10 + 1;
        j = 1;
        break;
      
      case '2': 
        k = k * 10 + 2;
        j = 1;
        break;
      
      case '3': 
        k = k * 10 + 3;
        j = 1;
        break;
      
      case '4': 
        k = k * 10 + 4;
        j = 1;
        break;
      
      case '5': 
        k = k * 10 + 5;
        j = 1;
        break;
      
      case '6': 
        k = k * 10 + 6;
        j = 1;
        break;
      
      case '7': 
        k = k * 10 + 7;
        j = 1;
        break;
      
      case '8': 
        k = k * 10 + 8;
        j = 1;
        break;
      
      case '9': 
        k = k * 10 + 9;
        j = 1;
        break;
      
      default: 
        if (j != 0) {
          return k;
        }
        throw new ParseException("No Number found", index);
      }
      
      index += 1;
    }
    

    if (j != 0) {
      return k;
    }
    
    throw new ParseException("No Number found", index);
  }
  


  public int parseMonth()
    throws ParseException
  {
    try
    {
      int i;
      

      switch (orig[(index++)])
      {
      case 'J': 
      case 'j': 
        switch (orig[(index++)]) {
        case 'A': 
        case 'a': 
          i = orig[(index++)];
          if ((i == 78) || (i == 110)) {
            return 0;
          }
          
          break;
        case 'U': 
        case 'u': 
          i = orig[(index++)];
          if ((i == 78) || (i == 110))
            return 5;
          if ((i == 76) || (i == 108)) {
            return 6;
          }
          break;
        }
        
        break;
      case 'F': 
      case 'f': 
        i = orig[(index++)];
        if ((i == 69) || (i == 101)) {
          i = orig[(index++)];
          if ((i == 66) || (i == 98)) {
            return 1;
          }
        }
        
        break;
      case 'M': 
      case 'm': 
        i = orig[(index++)];
        if ((i == 65) || (i == 97)) {
          i = orig[(index++)];
          if ((i == 82) || (i == 114))
            return 2;
          if ((i == 89) || (i == 121)) {
            return 4;
          }
        }
        
        break;
      case 'A': 
      case 'a': 
        i = orig[(index++)];
        if ((i == 80) || (i == 112)) {
          i = orig[(index++)];
          if ((i == 82) || (i == 114)) {
            return 3;
          }
        } else if ((i == 85) || (i == 117)) {
          i = orig[(index++)];
          if ((i == 71) || (i == 103)) {
            return 7;
          }
        }
        
        break;
      case 'S': 
      case 's': 
        i = orig[(index++)];
        if ((i == 69) || (i == 101)) {
          i = orig[(index++)];
          if ((i == 80) || (i == 112)) {
            return 8;
          }
        }
        
        break;
      case 'O': 
      case 'o': 
        i = orig[(index++)];
        if ((i == 67) || (i == 99)) {
          i = orig[(index++)];
          if ((i == 84) || (i == 116)) {
            return 9;
          }
        }
        
        break;
      case 'N': 
      case 'n': 
        i = orig[(index++)];
        if ((i == 79) || (i == 111)) {
          i = orig[(index++)];
          if ((i == 86) || (i == 118)) {
            return 10;
          }
        }
        
        break;
      case 'D': 
      case 'd': 
        i = orig[(index++)];
        if ((i == 69) || (i == 101)) {
          i = orig[(index++)];
          if ((i == 67) || (i == 99)) {
            return 11;
          }
        }
        break;
      }
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
    {
      throw new ParseException("Bad Month", index);
    }
  }
  


  public int parseTimeZone()
    throws ParseException
  {
    if (index >= orig.length) {
      throw new ParseException("No more characters", index);
    }
    int i = orig[index];
    if ((i == 43) || (i == 45)) {
      return parseNumericTimeZone();
    }
    return parseAlphaTimeZone();
  }
  










  public int parseNumericTimeZone()
    throws ParseException
  {
    int i = 0;
    int j = orig[(index++)];
    if (j == 43) {
      i = 1;
    } else if (j != 45) {
      throw new ParseException("Bad Numeric TimeZone", index);
    }
    
    int k = parseNumber();
    int m = k / 100 * 60 + k % 100;
    if (i != 0) {
      return -m;
    }
    return m;
  }
  




  public int parseAlphaTimeZone()
    throws ParseException
  {
    int i = 0;
    int j = 0;
    int k;
    try
    {
      switch (orig[(index++)]) {
      case 'U': 
      case 'u': 
        k = orig[(index++)];
        if ((k == 84) || (k == 116)) {
          i = 0;
        }
        else {
          throw new ParseException("Bad Alpha TimeZone", index);
        }
        break;
      case 'G': case 'g': 
        k = orig[(index++)];
        if ((k == 77) || (k == 109)) {
          k = orig[(index++)];
          if ((k == 84) || (k == 116)) {
            i = 0;
            break;
          }
        }
        throw new ParseException("Bad Alpha TimeZone", index);
      
      case 'E': 
      case 'e': 
        i = 300;
        j = 1;
        break;
      
      case 'C': 
      case 'c': 
        i = 360;
        j = 1;
        break;
      
      case 'M': 
      case 'm': 
        i = 420;
        j = 1;
        break;
      
      case 'P': 
      case 'p': 
        i = 480;
        j = 1;
        break;
      
      default: 
        throw new ParseException("Bad Alpha TimeZone", index);
      }
    } catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {
      throw new ParseException("Bad Alpha TimeZone", index);
    }
    
    if (j != 0) {
      k = orig[(index++)];
      if ((k == 83) || (k == 115)) {
        k = orig[(index++)];
        if ((k != 84) && (k != 116)) {
          throw new ParseException("Bad Alpha TimeZone", index);
        }
      } else if ((k == 68) || (k == 100)) {
        k = orig[(index++)];
        if ((k == 84) || (k != 116))
        {
          i -= 60;
        } else {
          throw new ParseException("Bad Alpha TimeZone", index);
        }
      }
    }
    
    return i;
  }
  
  int getIndex() {
    return index;
  }
}
