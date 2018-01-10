package javax.media.jai.iterator;

public abstract interface RectIter
{
  public abstract void startLines();
  
  public abstract void nextLine();
  
  public abstract boolean nextLineDone();
  
  public abstract void jumpLines(int paramInt);
  
  public abstract boolean finishedLines();
  
  public abstract void startPixels();
  
  public abstract void nextPixel();
  
  public abstract boolean nextPixelDone();
  
  public abstract void jumpPixels(int paramInt);
  
  public abstract boolean finishedPixels();
  
  public abstract void startBands();
  
  public abstract void nextBand();
  
  public abstract boolean nextBandDone();
  
  public abstract boolean finishedBands();
  
  public abstract int getSample();
  
  public abstract int getSample(int paramInt);
  
  public abstract float getSampleFloat();
  
  public abstract float getSampleFloat(int paramInt);
  
  public abstract double getSampleDouble();
  
  public abstract double getSampleDouble(int paramInt);
  
  public abstract int[] getPixel(int[] paramArrayOfInt);
  
  public abstract float[] getPixel(float[] paramArrayOfFloat);
  
  public abstract double[] getPixel(double[] paramArrayOfDouble);
}
