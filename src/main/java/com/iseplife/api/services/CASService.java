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

import java.beans.XMLDecoder;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

@Service
@RequiredArgsConstructor
public class CASService {
  final private JsonUtils jsonUtils;
  final private Logger LOG = LoggerFactory.getLogger(CASService.class);
  final private static String ISEP_CAS_URL = "https://portail-ovh.isep.fr/";

  private DocumentBuilderFactory dbf;
  private WebClient client;

  @PostConstruct
  public void initializeCASClient() throws ParserConfigurationException {
    dbf = DocumentBuilderFactory.newInstance();
    dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
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

public CASUserDTO identifyToCASSSO(String ticket, String service) {
  // MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
  // form.add("user", username);
  // form.add("password", password);

  String resp = client.get()
    .uri(uriBuilder -> uriBuilder
      .path("/cas/p3/serviceValidate")
      .queryParam("service", "{service}")
      .queryParam("ticket", "{ticket}")
      .build(service, ticket))
    .exchange()
    .flatMap(clientResponse -> clientResponse.bodyToMono(String.class))
    .block();

    try {
      var db = dbf.newDocumentBuilder();
      var doc = db.parse(new ByteArrayInputStream(resp.getBytes()));

      System.out.println(doc);
      System.out.println(doc.getElementsByTagName("cas:prenom").item(0).getTextContent() + " connected using SSO");
      return CASUserDTO.builder()
        .numero(Long.valueOf(doc.getElementsByTagName("cas:numero").item(0).getTextContent()))
        .nom(doc.getElementsByTagName("cas:nom").item(0).getTextContent())
        .prenom(doc.getElementsByTagName("cas:prenom").item(0).getTextContent())
        .mail(doc.getElementsByTagName("cas:mail").item(0).getTextContent())
        .login(doc.getElementsByTagName("cas:login").item(0).getTextContent())
        .titre(doc.getElementsByTagName("cas:titre").item(0).getTextContent())
        .build();
    }catch(Exception exception) {
      System.err.println("Error happened when reading auth xml : " + exception.getMessage());
      throw new HttpUnauthorizedException("authentication_failed");
    }

  // if(auth["cas:serviceResponse"]){
  //   const response = auth["cas:serviceResponse"]["cas:authenticationSuccess"]?.["cas:attributes"]
  //   const message = {
  //     type: "ISEP_JWT",
  //     result: "error",
  //   }
  //   if(response) {
  //     console.log(ticket, response["cas:login"])
  //     const jwt = sign(response, process.env.JWT_SECRET)
  //     message.result = jwt;
  //   }

  }
}
