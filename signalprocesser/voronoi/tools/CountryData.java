package signalprocesser.voronoi.tools;

import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import signalprocesser.voronoi.VPoint;

public class CountryData
{
  public static final String PATH_TO_COUNTRYDATA = "countrydata";
  public static final String INDEX_FILE = "countrydata/fileindex.dat";
  public static final Pattern PATTERN_COORDLINE = Pattern.compile("^[ ]*([0-9E.-]*)[ ]*,[ ]*([0-9E.-]*)[ ]*$");
  
  public CountryData() {}
  
  public static void main(String[] args) throws IOException, java.net.URISyntaxException
  {}
  
  public static ArrayList<String> getCountryList() throws IOException
  {
    java.io.InputStream stream = CountryData.class.getResourceAsStream("countrydata/fileindex.dat");
    if (stream == null) {
      throw new java.io.FileNotFoundException("The resource \"countrydata/fileindex.dat\" was not found relative to CountryData.class");
    }
    BufferedReader reader = new BufferedReader(new java.io.InputStreamReader(stream));
    


    ArrayList<String> countrylist = new ArrayList();
    String line; while ((line = reader.readLine()) != null) { String line;
      countrylist.add(line);
    }
    

    return countrylist;
  }
  
  public static ArrayList<VPoint> getCountryData(String countryfile, Rectangle bounds) throws IOException
  {
    String resourcename = "countrydata/" + countryfile;
    java.io.InputStream stream = CountryData.class.getResourceAsStream(resourcename);
    if (stream == null) {
      stream = CountryData.class.getResourceAsStream(resourcename + ".txt");
      if (stream == null) {
        throw new java.io.FileNotFoundException("The resource \"" + resourcename + "\" (also tried with \".txt\" extension) was not found relative to CountryData.class");
      }
    }
    BufferedReader reader = new BufferedReader(new java.io.InputStreamReader(stream));
    


    int linenumber = 0;
    boolean isfirst = true;
    ArrayList<VPoint> points = new ArrayList();
    int min_x = -1;int max_x = -1;
    int min_y = -1;int max_y = -1;
    String line; while ((line = reader.readLine()) != null) {
      String line;
      linenumber++;
      

      Matcher matcher = PATTERN_COORDLINE.matcher(line);
      if (!matcher.matches()) {
        throw new IOException("(line " + linenumber + ") Line doesn't match expected format - \"" + line + "\"");
      }
      

      int x = (int)Double.parseDouble(matcher.group(1));
      int y = (int)Double.parseDouble(matcher.group(2));
      

      if (isfirst) {
        isfirst = false;
        min_x = x;max_x = x;
        min_y = y;max_y = y;
      }
      else {
        if (x < min_x) { min_x = x;
        } else if (x > max_x) { max_x = x;
        }
        
        if (y < min_y) { min_y = y;
        } else if (y > max_y) { max_y = y;
        }
      }
      
      points.add(new VPoint(x, y));
    }
    
    int margin_left;
    double scaleby;
    int margin_top;
    int margin_left;
    if ((max_x - min_x) / width > (max_y - min_y) / height) {
      double scaleby = width / (max_x - min_x);
      int margin_top = (int)((height - (max_y - min_y) * scaleby) / 2.0D);
      margin_left = 0;
    } else {
      scaleby = height / (max_y - min_y);
      margin_top = 0;
      margin_left = (int)((width - (max_x - min_x) * scaleby) / 2.0D);
    }
    

    Iterator<VPoint> iter = points.iterator();
    VPoint prevpoint = null;
    while (iter.hasNext()) {
      VPoint point = (VPoint)iter.next();
      x = (x + margin_left + (int)((x - min_x) * scaleby));
      y = (y + margin_top + (int)((y - min_y) * scaleby));
      

      if ((prevpoint != null) && (x == x) && (y == y)) {
        iter.remove();
      } else {
        prevpoint = point;
      }
    }
    

    if (points.size() >= 1) {
      VPoint first = (VPoint)points.get(0);
      if ((x != x) || (y != y)) {
        points.add(first);
      }
    }
    

    return points;
  }
}
