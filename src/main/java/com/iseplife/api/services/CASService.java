package com.iseplife.api.services;

import com.iseplife.api.dto.CASResponseDTO;
import com.iseplife.api.exceptions.CASServiceException;
import com.iseplife.api.utils.JsonUtils;
import org.cloudinary.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class CASService {
  @Autowired
  JsonUtils jsonUtils;

  private Logger LOG = LoggerFactory.getLogger(CASService.class);
  private final String ISEP_CAS_URL = "http://localhost:8080/springrestexample/employees.xml";

  public Boolean checkCredentials(String username, String password) {
    CASResponseDTO response;
    RestTemplate restTemplate = new RestTemplate();

    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);

      JSONObject body = new JSONObject();
      body.put("id", 1);
      body.put("name", "John");


      HttpEntity<String> request = new HttpEntity<>(body.toString(), headers);
      String json = restTemplate.postForObject(ISEP_CAS_URL, request, String.class);

      response =  jsonUtils.deserialize(json, CASResponseDTO.class);
    } catch (RestClientException e) {
      LOG.error("CAS unavailable");
      throw new CASServiceException("CAS unavailable", e);
    }
    return response.getResult();
  }
}
