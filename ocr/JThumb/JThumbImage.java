package ocr.JThumb;

import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.lang.ref.SoftReference;
import java.util.Observable;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import ocr.gui.LoadDataFile;
import ocr.gui.OCRInterface;
import ocr.manager.OcrPropInfoHolder;
import ocr.manager.zones.ZonesManager;






















public class JThumbImage
  extends Observable
{
  String filePath;
  SoftReference<PlanarImage> thumb = null;
  

  JThumbListModel listModel;
  

  String fileName;
  

  int oldRotate = 0;
  int oldThumbWidth = 0;
  int oldThumbHeight = 0;
  float scale = 0.0F;
  



  public SoftReference<LoadDataFile> dataFile = null;
  






  public JThumbImage(String filePath, JThumbListModel model)
  {
    this.filePath = filePath;
    listModel = model;
    File f = new File(filePath);
    fileName = f.getName();
  }
  

  public String getFilePath()
  {
    return filePath;
  }
  



  public void setDataFile(StringBuffer filename_buf)
  {
    filename_buf.replace(filename_buf.lastIndexOf("."), filename_buf.length(), ".xml");
    String file_path = 
      OCRInterface.getCurrentImageDir() + 
      "/" + 
      OCRInterface.getImageDirName() + 
      "/" + 
      filename_buf;
    
    dataFile = new SoftReference(new LoadDataFile(file_path, 0, 0));
  }
  


  public ZonesManager getZones()
  {
    LoadDataFile data = (LoadDataFile)dataFile.get();
    if (data == null) {
      data = getNewDatafile();
    }
    return data.get_zones_vec();
  }
  


  public PlanarImage setImage()
  {
    oldThumbWidth = listModel.getList().THUMB_WIDTH;
    oldThumbHeight = listModel.getList().THUMB_HEIGHT;
    float height = listModel.getList().THUMB_HEIGHT - 8;
    float width = listModel.getList().THUMB_WIDTH - 8;
    int rotateby = listModel.getRotate();
    

    PlanarImage image = JAI.create("fileload", filePath);
    
    float imageWidth = image.getWidth();
    float imageHeight = image.getHeight();
    






    float scale = 0.0F;
    
    if ((rotateby == 0) || (rotateby == 2)) {
      if (imageHeight > imageWidth) {
        scale = height / imageHeight;
      } else {
        scale = width / imageWidth;
      }
    }
    else if (imageHeight > imageWidth) {
      scale = width / imageHeight;
    } else {
      scale = height / imageWidth;
    }
    this.scale = scale;
    





    if (this.scale < 0.0F) {
      this.scale = 1.0F;
    }
    ParameterBlock pb = new ParameterBlock();
    pb.addSource(image);
    pb.add(this.scale);
    pb.add(this.scale);
    pb.add(0.0F);
    pb.add(0.0F);
    
    image = JAI.create("scale", pb);
    

    return image;
  }
  









  public PlanarImage getThumb()
  {
    PlanarImage newThumb = null;
    





    if ((thumb != null) && (oldRotate == listModel.getRotate()) && (oldThumbHeight == listModel.getList().THUMB_HEIGHT) && (oldThumbWidth == listModel.getList().THUMB_WIDTH))
    {
      newThumb = (PlanarImage)thumb.get();
    }
    else
    {
      newThumb = setImage();
      setDataFile(new StringBuffer(fileName));
      newThumb = rotateImage(newThumb);
      thumb = new SoftReference(newThumb);
    }
    return newThumb;
  }
  





  public PlanarImage rotateImage(PlanarImage newThumb)
  {
    int rotateby = listModel.getRotate();
    ParameterBlock pb = new ParameterBlock();
    pb.addSource(newThumb);
    pb.add(0.0F);
    pb.add(0.0F);
    float angle = (float)(rotateby * Math.toRadians(90.0D));
    pb.add(angle);
    
    newThumb = JAI.create("rotate", pb);
    
    pb = new ParameterBlock();
    pb.addSource(newThumb);
    if (rotateby == 1) {
      pb.add(newThumb.getWidth());
      pb.add(0.0F);
    } else if (rotateby == 2) {
      pb.add(newThumb.getWidth());
      pb.add(newThumb.getHeight());
    } else if (rotateby == 3) {
      pb.add(0.0F);
      pb.add(newThumb.getHeight());
    }
    
    newThumb = JAI.create("translate", pb);
    oldRotate = listModel.getRotate();
    
    return newThumb;
  }
  





  public PlanarImage getNewThumb()
  {
    PlanarImage newThumb = null;
    newThumb = setImage();
    newThumb = rotateImage(newThumb);
    return newThumb;
  }
  



  public LoadDataFile getNewDatafile()
  {
    StringBuffer filename_buf = new StringBuffer(fileName);
    filename_buf.replace(filename_buf.length() - 3, filename_buf.length(), listModel.ocrIF.ocrProperties.getDataSetExtension(0));
    String file_path = 
      OCRInterface.getCurrentDicDir() + 
      "/" + 
      listModel.ocrIF.ocrProperties.getHomePlusHomeName(0) + 
      "/" + 
      filename_buf;
    LoadDataFile newDataFile = new LoadDataFile(file_path, 0, 0);
    return newDataFile;
  }
}
