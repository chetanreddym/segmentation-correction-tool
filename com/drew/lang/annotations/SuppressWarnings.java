package com.drew.lang.annotations;

import java.lang.annotation.Annotation;

public @interface SuppressWarnings
{
  @NotNull
  String value();
  
  @NotNull
  String justification();
}
