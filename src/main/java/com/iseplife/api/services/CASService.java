package com.iseplife.api.services;

import com.iseplife.api.dto.CASAuthentificationDTO;
import com.iseplife.api.dto.CASUserDTO;
import com.iseplife.api.exceptions.AuthException;
import com.iseplife.api.exceptions.CASServiceException;
import com.iseplife.api.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;

@Service
public class CASService {
  @Autowired
  JsonUtils jsonUtils;

  private WebClient client;
  private final Logger LOG = LoggerFactory.getLogger(CASService.class);
  private final String ISEP_CAS_URL = "https://sso-portal.isep.fr";

  @PostConstruct
  public void initializeCASClient() {
    client = WebClient.builder()
      .baseUrl(ISEP_CAS_URL)
      .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
      .build();
  }

  public CASUserDTO identifyToCAS(String username, String password) {
    try {
      MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
      form.add("user", username);
      form.add("password", password);

      CASUserDTO response = client.post()
        .body(BodyInserters.fromFormData(form))
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        .exchange()
        .flatMap(clientResponse ->
          clientResponse.bodyToMono(CASAuthentificationDTO.class).flatMap(body -> {
            if (body == null || body.getResult() == 0)
              return Mono.error(new AuthException("User not found"));

            ResponseCookie CASCookie = clientResponse.cookies().getFirst("lemonldap");
            if (CASCookie == null)
              return Mono.error(new AuthException("CAS cookie has been missing from the response"));

            return accessUser(CASCookie);
          })
        )
        .block();

      return response;
    } catch (WebClientException e) {
      LOG.error("CAS unavailable");
      throw new CASServiceException("CAS unavailable", e);
    }
  }

  public Mono<CASUserDTO> accessUser(ResponseCookie cookie) {
    return client.post()
      .uri("/session/my/global")
      .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
      .cookie(cookie.getName(), cookie.getValue())
      .retrieve()
      .bodyToMono(CASUserDTO.class);
  }
}
