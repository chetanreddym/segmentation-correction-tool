package ocr.gui;






public abstract class MergeSort
{
  protected Object[] toSort;
  




  protected Object[] swapSpace;
  




  public MergeSort() {}
  




  public void sort(Object[] array)
  {
    if ((array != null) && (array.length > 1))
    {


      int maxLength = array.length;
      swapSpace = new Object[maxLength];
      toSort = array;
      mergeSort(0, maxLength - 1);
      swapSpace = null;
      toSort = null;
    }
  }
  
  public abstract int compareElementsAt(int paramInt1, int paramInt2);
  
  protected void mergeSort(int begin, int end) {
    if (begin != end)
    {


      int mid = (begin + end) / 2;
      mergeSort(begin, mid);
      mergeSort(mid + 1, end);
      merge(begin, mid, end);
    }
  }
  

  protected void merge(int begin, int middle, int end)
  {
    int firstHalf = count = begin;
    int secondHalf = middle + 1;
    while ((firstHalf <= middle) && (secondHalf <= end))
    {
      if (compareElementsAt(secondHalf, firstHalf) < 0) {
        swapSpace[(count++)] = toSort[(secondHalf++)];
      } else
        swapSpace[(count++)] = toSort[(firstHalf++)];
    }
    if (firstHalf <= middle)
    {
      while (firstHalf <= middle) {
        swapSpace[(count++)] = toSort[(firstHalf++)];
      }
      
    } else {
      while (secondHalf <= end)
        swapSpace[(count++)] = toSort[(secondHalf++)];
    }
    for (int count = begin; count <= end; count++) {
      toSort[count] = swapSpace[count];
    }
  }
}
