package com.drew.metadata;

import com.drew.lang.BufferReader;
import com.drew.lang.annotations.NotNull;

public abstract interface MetadataReader
{
  public abstract void extract(@NotNull BufferReader paramBufferReader, @NotNull Metadata paramMetadata);
}
