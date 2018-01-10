package ocr.JThumb;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Observable;
import javax.swing.AbstractListModel;
import ocr.gui.OCRInterface;

















public class JThumbListModel
  extends AbstractListModel
{
  ArrayList<JThumbImage> allImages = new ArrayList();
  



  ArrayList<JThumbImage> visibleImages = new ArrayList();
  
  public OCRInterface ocrIF = null;
  



  private JThumbList list;
  



  private int rotate = 0;
  



  public JThumbListModel(OCRInterface parent)
  {
    ocrIF = parent;
  }
  




  public void setList(JThumbList l)
  {
    list = l;
  }
  
  public JThumbList getList() {
    return list;
  }
  




  public void add(JThumbImage image)
  {
    allImages.add(image);
    visibleImages.add(image);
    fireIntervalAdded(this, allImages.size() - 1, allImages.size() - 1);
  }
  



  public void add(String file)
  {
    add(new JThumbImage(file, this));
  }
  
  public void addAll(String[] files)
  {
    for (int i = 0; i < files.length; i++) {
      JThumbImage image = null;
      image = new JThumbImage(files[i], this);
      add(image);
    }
  }
  
  public void removeAll() {
    allImages = new ArrayList();
    visibleImages = new ArrayList();
    list.indices = null;
  }
  
  public int getRotate() {
    return rotate;
  }
  
  public void setRotate(int r)
  {
    rotate = r;
  }
  



  public int getSize()
  {
    return visibleImages.size();
  }
  


  public Object getElementAt(int index)
  {
    return visibleImages.get(index);
  }
  


  public void update(Observable o, Object arg)
  {
    System.out.println("update?");
  }
  
  public void setVisibleImages(Object[] images) {
    visibleImages = new ArrayList();
    for (int i = 0; i < images.length; i++) {
      visibleImages.add((JThumbImage)images[i]);
    }
    list.repaint();
  }
  
  public void setAllVisibleImages() {
    visibleImages = new ArrayList();
    for (int i = 0; i < allImages.size(); i++) {
      visibleImages.add((JThumbImage)allImages.get(i));
    }
    
    list.repaint();
  }
  
  public void setVisibleImagesFromAll(int[] selected) {
    visibleImages = new ArrayList();
    for (int i = 0; i < selected.length; i++) {
      System.out.println("selected: " + selected[i]);
      visibleImages.add((JThumbImage)allImages.get(selected[i]));
    }
    list.repaint();
  }
}
