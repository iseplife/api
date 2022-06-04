package com.iseplife.api.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.iseplife.api.constants.Language;
import com.iseplife.api.constants.NotificationType;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

@Service
public class NotificationTranslationService {
  
  private Map<String, JSONObject> translations = new HashMap<String, JSONObject>();
  
  private JSONObject readFile(String name) throws IOException {
    InputStream stream = new ClassPathResource(name).getInputStream();
    try {
      return (JSONObject) JSONValue.parse(stream);
    } finally {
      stream.close();
    }
  }
  
  private JSONObject getTranslations(String language) {
    return translations.get(language.toLowerCase());
  }
  
  @PostConstruct
  private void init() throws IOException {
    translations.put("fr", readFile("translations/fr/notification.json"));
    translations.put("en", readFile("translations/en/notification.json"));
  }
  
  public String getTranslation(NotificationType type, Map<String, Object> informations, Language language) {
    JSONObject translation = getTranslations(language.name());
    
    String translated = translation.getAsString(type.name());
    for(Entry<String, Object> entry : informations.entrySet())
      translated = translated.replace(String.format("{{%s}}", entry.getKey()), entry.getValue().toString());
    
    return translated;
  }
}
