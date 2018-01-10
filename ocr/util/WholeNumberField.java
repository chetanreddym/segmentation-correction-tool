package ocr.util;

import java.awt.Toolkit;
import java.text.NumberFormat;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.PlainDocument;

public class WholeNumberField extends JTextField
{
  private Toolkit toolkit;
  private NumberFormat integerFormatter;
  
  public WholeNumberField(int value, int columns)
  {
    super(columns);
    toolkit = Toolkit.getDefaultToolkit();
    integerFormatter = NumberFormat.getNumberInstance(java.util.Locale.US);
    integerFormatter.setParseIntegerOnly(true);
    setValue(value);
  }
  
  public int getValue() {
    int retVal = 0;
    try {
      retVal = integerFormatter.parse(getText()).intValue();
    }
    catch (java.text.ParseException e)
    {
      toolkit.beep();
    }
    return retVal;
  }
  
  public void setValue(int value) {
    setText(integerFormatter.format(value));
  }
  
  protected javax.swing.text.Document createDefaultModel() {
    return new WholeNumberDocument();
  }
  
  protected class WholeNumberDocument extends PlainDocument
  {
    protected WholeNumberDocument() {}
    
    public void insertString(int offs, String str, AttributeSet a) throws javax.swing.text.BadLocationException {
      char[] source = str.toCharArray();
      char[] result = new char[source.length];
      int j = 0;
      
      for (int i = 0; i < result.length; i++) {
        if (Character.isDigit(source[i])) {
          result[(j++)] = source[i];
        } else {
          toolkit.beep();
          System.err.println("insertString: " + source[i]);
        }
      }
      super.insertString(offs, new String(result, 0, j), a);
    }
  }
}
