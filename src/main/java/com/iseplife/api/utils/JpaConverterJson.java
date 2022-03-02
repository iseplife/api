package com.iseplife.api.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Converter(autoApply = true)
public class JpaConverterJson implements AttributeConverter<Map<String, Object>, String> {

  private final static ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public String convertToDatabaseColumn(Map<String, Object> meta) {
    try {
      return objectMapper.writeValueAsString(meta);
    } catch (JsonProcessingException ex) {
      return null;
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public Map<String, Object> convertToEntityAttribute(String dbData) {
    if(dbData == null)
      return new HashMap<>();
    try {
      return objectMapper.readValue(dbData, Map.class);
    } catch (IOException ex) {
      return null;
    }
  }

}