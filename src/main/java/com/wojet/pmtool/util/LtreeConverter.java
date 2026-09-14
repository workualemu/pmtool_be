package com.wojet.pmtool.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class LtreeConverter implements AttributeConverter<String, String> {

  @Override
  public String convertToDatabaseColumn(String attribute) {
    return attribute;
  }

  @Override
  public String convertToEntityAttribute(String dbData) {
    return dbData;
  }
}
