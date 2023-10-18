package com.iseplife.api.services;

import com.iseplife.api.dto.ISEPCAS.CASAuthentificationDTO;
import com.iseplife.api.dto.ISEPCAS.CASUserDTO;
import com.iseplife.api.exceptions.http.HttpInternalServerErrorException;
import com.iseplife.api.exceptions.http.HttpUnauthorizedException;
import com.iseplife.api.exceptions.CASServiceException;
import com.iseplife.api.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequiredArgsConstructor
public class CASService {
  final private JsonUtils jsonUtils;
  final private Logger LOG = LoggerFactory.getLogger(CASService.class);
  final private static String ISEP_CAS_URL = "https://portail-ovh.isep.fr/";

  private WebClient client;

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
        .flatMap(clientResponse -> clientResponse.bodyToMono(String.class).flatMap(json -> {
            /*
             * We parse manually as the wrong content-type header is given when authentication failed
             * (Content-Type	application/javascript)
             */
            CASAuthentificationDTO body = jsonUtils.deserialize(json, CASAuthentificationDTO.class);
            if (body == null || body.getResult() == 0)
              return Mono.error(new HttpUnauthorizedException("authentication_failed"));

            ResponseCookie CASCookie = clientResponse.cookies().getFirst("lemonldap");
            if (CASCookie == null) {
              LOG.error("CAS cookie (lemonldap) not found");
              return Mono.error(
                new CASServiceException("CAS cookie's missing from the response")
              );
            }

            return accessUser(CASCookie);
          })
        )
        .block();

      if(response == null){
        LOG.warn("CAS user's information not accessed");
        throw new HttpUnauthorizedException("authentication_failed");
      }

      return response;
    } catch (WebClientException e) {
      LOG.error("CAS unavailable");
      throw new HttpInternalServerErrorException("isep_cas_unavailable", e);
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
