package signalprocesser.shared;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;






public class TextToolkit
{
  private static final int MAX_LINES = 10;
  private static final int LENGTH_TO_CUTUP_WORDS = 5;
  private static final float DECREASE_FONT_HEIGHT_BY_FACTOR = 0.9F;
  private static final String TERMINATE_HALFWORD_WITH = "-";
  private static final String TERMINATE_IFMAXLEN_WITH = "...";
  private static final FontRenderContext fontrender = new FontRenderContext(null, true, true);
  








  private static String[] lineoftext = new String[10];
  






  public TextToolkit() {}
  






  public static void writeFromTop(Graphics2D graphic, Font font, Color color, String text, Rectangle textbounds)
  {
    writeFromTop(graphic, font, color, text, textbounds, height);
  }
  










  public static void writeFromTop(Graphics2D graphic, Font font, Color color, String text, Rectangle textbounds, int height)
  {
    FontMetrics metrics = graphic.getFontMetrics(font);
    

    int fontheight = (int)(metrics.getAscent() * 0.9F);
    


    int maxnumberoflines = height / fontheight;
    int numberoflines; if (maxnumberoflines <= 1) {
      int numberoflines = 1;
      getSinglelineOfText(text, metrics, width);
    } else {
      if (maxnumberoflines > 10) {
        maxnumberoflines = 10;
      }
      numberoflines = getMultilineOfText(text, metrics, maxnumberoflines, width);
    }
    

    int margin = (height - numberoflines * fontheight) / 2 + fontheight - metrics.getDescent() / 2;
    for (int line = 0; line < numberoflines; line++)
    {

      TextLayout textlayout = new TextLayout(lineoftext[line], font, fontrender);
      

      Rectangle2D rectangle = textlayout.getBounds();
      AffineTransform transform = AffineTransform.getTranslateInstance(x + (width - rectangle.getWidth()) / 2.0D, y + margin + fontheight * line);
      

      graphic.setPaint(color);
      if (graphic.getRenderingHint(RenderingHints.KEY_ANTIALIASING) == RenderingHints.VALUE_ANTIALIAS_OFF) {
        graphic.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphic.fill(textlayout.getOutline(transform));
        graphic.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
      } else {
        graphic.fill(textlayout.getOutline(transform));
      }
    }
  }
  

  public static void writeFromLeftCentered(Graphics2D graphic, Font font, Color color, String text, Rectangle textbounds)
  {
    FontMetrics metrics = graphic.getFontMetrics(font);
    

    int fontheight = (int)(metrics.getAscent() * 0.9F);
    


    int maxnumberoflines = width / fontheight;
    int numberoflines; if (maxnumberoflines <= 1) {
      int numberoflines = 1;
      getSinglelineOfText(text, metrics, height);
    } else {
      if (maxnumberoflines > 10) {
        maxnumberoflines = 10;
      }
      numberoflines = getMultilineOfText(text, metrics, maxnumberoflines, height);
    }
    

    int margin = (width - numberoflines * fontheight) / 2 + fontheight - metrics.getDescent() / 2;
    for (int line = 0; line < numberoflines; line++)
    {

      TextLayout textlayout = new TextLayout(lineoftext[line], font, fontrender);
      

      AffineTransform transform = AffineTransform.getRotateInstance(Math.toRadians(-90.0D));
      Rectangle2D rectangle = textlayout.getBounds();
      
      transform.translate(-y - (height + rectangle.getWidth()) / 2.0D, x + margin + fontheight * line);
      

      graphic.setPaint(color);
      if (graphic.getRenderingHint(RenderingHints.KEY_ANTIALIASING) == RenderingHints.VALUE_ANTIALIAS_OFF) {
        graphic.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphic.fill(textlayout.getOutline(transform));
        graphic.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
      } else {
        graphic.fill(textlayout.getOutline(transform));
      }
    }
  }
  









  public static void writeFromLeft(Graphics2D graphic, Font font, Color color, String text, Rectangle textbounds)
  {
    FontMetrics metrics = graphic.getFontMetrics(font);
    

    int fontheight = (int)(metrics.getAscent() * 0.9F);
    


    int maxnumberoflines = width / fontheight;
    int numberoflines; if (maxnumberoflines <= 1) {
      int numberoflines = 1;
      getSinglelineOfText(text, metrics, height);
    } else {
      if (maxnumberoflines > 10) {
        maxnumberoflines = 10;
      }
      numberoflines = getMultilineOfText(text, metrics, maxnumberoflines, height);
    }
    

    int margin = (width - numberoflines * fontheight) / 2 + fontheight - metrics.getDescent() / 2;
    for (int line = 0; line < numberoflines; line++)
    {

      TextLayout textlayout = new TextLayout(lineoftext[line], font, fontrender);
      

      AffineTransform transform = AffineTransform.getRotateInstance(Math.toRadians(-90.0D));
      Rectangle2D rectangle = textlayout.getBounds();
      
      transform.translate(-y - rectangle.getWidth(), x + margin + fontheight * line);
      

      graphic.setPaint(color);
      if (graphic.getRenderingHint(RenderingHints.KEY_ANTIALIASING) == RenderingHints.VALUE_ANTIALIAS_OFF) {
        graphic.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphic.fill(textlayout.getOutline(transform));
        graphic.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
      } else {
        graphic.fill(textlayout.getOutline(transform));
      }
    }
  }
  









  public static void writeFromRight(Graphics2D graphic, Font font, Color color, String text, Rectangle textbounds)
  {
    FontMetrics metrics = graphic.getFontMetrics(font);
    

    int fontheight = (int)(metrics.getAscent() * 0.9F);
    


    int maxnumberoflines = width / fontheight;
    int numberoflines; if (maxnumberoflines <= 1) {
      int numberoflines = 1;
      getSinglelineOfText(text, metrics, height);
    } else {
      if (maxnumberoflines > 10) {
        maxnumberoflines = 10;
      }
      numberoflines = getMultilineOfText(text, metrics, maxnumberoflines, height);
    }
    

    int margin = (width + numberoflines * fontheight) / 2 - fontheight + metrics.getDescent() / 2;
    for (int line = 0; line < numberoflines; line++)
    {

      TextLayout textlayout = new TextLayout(lineoftext[line], font, fontrender);
      

      AffineTransform transform = AffineTransform.getRotateInstance(Math.toRadians(90.0D));
      Rectangle2D rectangle = textlayout.getBounds();
      
      transform.translate(y, -x - margin + fontheight * line);
      

      graphic.setPaint(color);
      if (graphic.getRenderingHint(RenderingHints.KEY_ANTIALIASING) == RenderingHints.VALUE_ANTIALIAS_OFF) {
        graphic.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphic.fill(textlayout.getOutline(transform));
        graphic.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
      } else {
        graphic.fill(textlayout.getOutline(transform));
      }
    }
  }
  









  private static void getSinglelineOfText(String string, FontMetrics metrics, int maxwidth)
  {
    String iftoolong = "...";
    int iftoolongwidth = metrics.stringWidth(iftoolong);
    printLine(0, string, metrics, 0, maxwidth, true, iftoolong, iftoolongwidth);
  }
  








  private static int getMultilineOfText(String string, FontMetrics metrics, int maxnumberoflines, int maxwidth)
  {
    int currindex = 0;
    int stringlength = string.length();
    
    boolean islastline = false;
    String iftoolong = "-";
    int iftoolongwidth = metrics.stringWidth(iftoolong);
    

    for (int currline = 0; 
        currline < maxnumberoflines; currline++)
    {

      if (currline >= maxnumberoflines - 1) {
        islastline = true;
        iftoolong = "...";
        iftoolongwidth = metrics.stringWidth(iftoolong);
      }
      

      currindex = printLine(currline, string, metrics, currindex, maxwidth, islastline, iftoolong, iftoolongwidth);
      if (currindex < 0) {
        return currline + 1;
      }
    }
    



    return maxnumberoflines;
  }
  














  private static int printLine(int currline, String string, FontMetrics metrics, int currindex, int maxwidth, boolean islastline, String iftoolong, int iftoolongwidth)
  {
    int linestarts = currindex;
    

    int width = 0;
    int lastspace = -1;
    for (; currindex < string.length(); currindex++) {
      char currchar = string.charAt(currindex);
      if (currchar == ' ') {
        lastspace = currindex;
      }
      width += metrics.charWidth(currchar);
      if (width > maxwidth - iftoolongwidth) {
        break;
      }
    }
    

    if (currindex >= string.length())
    {

      lineoftext[currline] = string.substring(linestarts);
      return -1;
    }
    

    for (int tmpindex = currindex; 
        tmpindex < string.length(); tmpindex++) {
      width += metrics.charWidth(string.charAt(tmpindex));
      if (width > maxwidth) {
        break;
      }
    }
    


    if (tmpindex >= string.length()) {
      lineoftext[currline] = string.substring(linestarts);
      return -1; }
    if ((lastspace >= 0) && (!islastline) && (currindex - lastspace < 5)) {
      lineoftext[currline] = string.substring(linestarts, lastspace);
      return lastspace + 1;
    }
    lineoftext[currline] = (string.substring(linestarts, currindex) + iftoolong);
    return currindex;
  }
}
