package com.sun.media.jai.opimage;












































































class HistogramHash
{
  int capacity;
  










































































  int[] colors;
  










































































  int[] counts;
  










































































  int size;
  










































































  int hashsize;
  










































































  boolean packed = false;
  int[] newColors;
  int[] newCounts;
  
  public HistogramHash(int capacity) {
    this.capacity = capacity;
    hashsize = (capacity * 4 / 3);
    colors = new int[hashsize];
    counts = new int[hashsize];
  }
  
  void init() {
    size = 0;
    packed = false;
    for (int i = 0; i < hashsize; i++) {
      colors[i] = -1;
      counts[i] = 0;
    }
  }
  
  boolean insert(int node) {
    int hashPos = hashCode(node);
    if (colors[hashPos] == -1) {
      colors[hashPos] = node;
      counts[hashPos] += 1;
      size += 1;
      return size <= capacity; }
    if (colors[hashPos] == node) {
      counts[hashPos] += 1;
      return size <= capacity;
    }
    for (int next = hashPos + 1; next != hashPos; next++) {
      next %= hashsize;
      if (colors[next] == -1) {
        colors[next] = node;
        counts[next] += 1;
        size += 1;
        return size <= capacity; }
      if (colors[next] == node) {
        counts[next] += 1;
        return size <= capacity;
      }
    }
    
    return size <= capacity;
  }
  
  boolean isFull() {
    return size > capacity;
  }
  
  void put(int node, int value) {
    int hashPos = hashCode(node);
    if (colors[hashPos] == -1) {
      colors[hashPos] = node;
      counts[hashPos] = value;
      size += 1;
      return; }
    if (colors[hashPos] == node) {
      counts[hashPos] = value;
      return;
    }
    for (int next = hashPos + 1; next != hashPos; next++) {
      next %= hashsize;
      if (colors[next] == -1) {
        colors[next] = node;
        counts[next] = value;
        size += 1;
        return; }
      if (colors[next] == node) {
        counts[next] = value;
        return;
      }
    }
  }
  

  int get(int node)
  {
    int hashPos = hashCode(node);
    if (colors[hashPos] == node) {
      return counts[hashPos];
    }
    for (int next = hashPos + 1; next != hashPos; next++) {
      next %= hashsize;
      if (colors[next] == node) {
        return counts[next];
      }
    }
    
    return -1;
  }
  
  int[] getCounts() {
    if (!packed)
      pack();
    return newCounts;
  }
  
  int[] getColors() {
    if (!packed)
      pack();
    return newColors;
  }
  
  void pack() {
    newColors = new int[capacity];
    newCounts = new int[capacity];
    
    int i = 0; for (int j = 0; i < hashsize; i++) {
      if (colors[i] != -1) {
        newColors[j] = colors[i];
        newCounts[j] = counts[i];
        j++;
      }
    }
    
    packed = true;
  }
  
  int hashCode(int value) {
    return ((value >> 16) * 33023 + (value >> 8 & 0xFF) * 30013 + (value & 0xFF) * 27011) % hashsize;
  }
}
