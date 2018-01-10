package ocr.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import ocr.gui.OCRInterface;













public class ImageNamesSorter
  implements Comparator
{
  ArrayList<String> names;
  String[] namesArray;
  int num;
  Comparator<String> comparator;
  String baseImageExt;
  
  public ImageNamesSorter(String baseImageExt)
  {
    this.baseImageExt = baseImageExt;
  }
  
  public ImageNamesSorter(ArrayList<String> imageNames, Comparator<String> comparator)
  {
    names = new ArrayList();
    names = imageNames;
    this.comparator = comparator;
    
    num = names.size();
    namesArray = new String[num];
    toArray();
  }
  
  private void toArray() {
    for (int i = 0; i < num; i++)
      namesArray[i] = ((String)names.get(i));
    Arrays.sort(namesArray);
  }
  
  public String[] getSortedImageNames() {
    Arrays.sort(namesArray, comparator);
    return namesArray;
  }
  
  public int compare(Object name1, Object name2) {
    if ((name1 != null) && ((name1 instanceof String)) && 
      (name2 != null) && ((name2 instanceof String))) {
      String str1 = (String)name1;
      String str2 = (String)name2;
      
      String gtType1 = OCRInterface.this_interface.checkGTType(str1);
      String gtType2 = OCRInterface.this_interface.checkGTType(str2);
      



      String baseImage1 = OCRInterface.this_interface.getBaseImage(
        new File(str1).getName(), baseImageExt);
      
      String baseImage2 = OCRInterface.this_interface.getBaseImage(
        new File(str2).getName(), baseImageExt);
      
      String basename1 = OCRInterface.this_interface.getFileNameWithoutExt(baseImage1);
      String basename2 = OCRInterface.this_interface.getFileNameWithoutExt(baseImage2);
      



      if ((basename1.equalsIgnoreCase(basename2)) && (gtType1 != null) && (gtType2 == null))
        return 1;
      if ((basename1.equalsIgnoreCase(basename2)) && (gtType1 == null) && (gtType2 != null)) {
        return -1;
      }
      for (String type : OCRInterface.extList) {
        if ((str1.contains(type)) && (basename1.equalsIgnoreCase(basename2)))
          return -1;
        if ((str2.contains(type)) && (basename1.equalsIgnoreCase(basename2))) {
          return 1;
        }
      }
      if ((basename1.equalsIgnoreCase(basename2)) && 
        (!str1.equals(str2)) && (str2.equals(baseImage2))) {
        return 1;
      }
    }
    return 0;
  }
}
