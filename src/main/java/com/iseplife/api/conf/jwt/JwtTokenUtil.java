package com.iseplife.api.conf.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iseplife.api.constants.ClubRole;
import com.iseplife.api.dao.student.StudentRepository;
import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.exceptions.http.HttpBadRequestException;
import com.iseplife.api.services.ClubService;
import com.iseplife.api.services.StudentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;


@Service
@RequiredArgsConstructor
public class JwtTokenUtil {
  final private StudentRepository studentRepository;
  final private StudentService studentService;
  final private ClubService clubService;

  final private static Logger LOG = LoggerFactory.getLogger(JwtTokenUtil.class);

  final public static  String CLAIM_PAYLOAD = "payload";
  final private static String CLAIM_USER_ID = "userID";
  final private static String SECRET_HASHING_ALGORITHM = "SHA-256";
  final private Locale locale = Locale.FRANCE;

  @Value("${jwt.secret}")
  private String secret;
  @Value("${jwt.refresh-secret}")
  private String refreshSecret;
  @Value("${jwt.issuer}")
  private String issuer;
  @Value("${jwt.token-duration}")
  private int tokenDuration;
  @Value("${jwt.refresh-token-duration}")
  private int refreshTokenDuration;

  Algorithm standardAlgorithm, refreshAlgorithm;

  @PostConstruct
  private void init() {
    standardAlgorithm = Algorithm.HMAC256(secret);
    refreshAlgorithm = Algorithm.HMAC256(refreshSecret);
  }

  public DecodedJWT decodeToken(String token) throws JWTVerificationException {
    try {
      Algorithm algorithm = Algorithm.HMAC256(secret);
      JWTVerifier verifier = JWT.require(algorithm)
        .withIssuer(issuer)
        .build(); //Reusable verifier instance
      return verifier.verify(token);
    } catch (Throwable e) {
      //LOG.error("could not decode token", e);
    }
    throw new JWTVerificationException("invalid token");
  }

  public TokenSet generateTokenSet(Student student) {
    TokenPayload tokenPayload = generatePayload(student);
    String token = generateToken(tokenPayload);
    String refreshToken = generateRefreshToken(tokenPayload);

    student.setLastConnection(new Date());
    studentRepository.save(student);

    return new TokenSet(token, refreshToken);
  }

  public TokenPayload getPayload(DecodedJWT jwt) {
    String payload = jwt.getClaim(CLAIM_PAYLOAD).asString();
    try {
      return new ObjectMapper().readValue(payload, TokenPayload.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }


  /**
   * Refresh the set of tokens (token + refresh token)
   * throws if the refresh token is not valid -> login needed
   *
   * @param token the refresh token
   * @return a set of new tokens
   * @throws JWTVerificationException
   */
  public TokenSet refreshWithToken(String token) throws JWTVerificationException {
    try {
      DecodedJWT decodedJWT = JWT.decode(token);
      Long id = decodedJWT.getClaim(CLAIM_USER_ID).asLong();
      Student student = studentService.getStudent(id);

      if(student.isArchived()) {
        throw new HttpBadRequestException("user_archived");
      }

      JWTVerifier verifier = JWT.require(refreshAlgorithm)
        .withIssuer(issuer)
        .build();
      verifier.verify(token);
      return generateTokenSet(student);
    } catch (JWTVerificationException | HttpBadRequestException e) {
      LOG.error("could not refresh token (" +e.getMessage()+")");
    }
    throw new JWTVerificationException("token invalid");
  }

  public TokenPayload generatePayload(Student student) {
    List<String> roles = student.getAuthorities()
      .stream()
      .sorted(Comparator.comparing(GrantedAuthority::getAuthority))
      .map(GrantedAuthority::getAuthority)
      .collect(Collectors.toList());


    List<Long> publisherClubs = clubService.getUserCurrentClubsWith(student, ClubRole.PUBLISHER)
      .stream()
      .map(Club::getId)
      .collect(Collectors.toList());

    List<Long> adminClubs = clubService.getUserCurrentClubsWith(student, ClubRole.ADMIN)
      .stream()
      .map(Club::getId)
      .collect(Collectors.toList());

    List<Long> feeds = studentService.getFeeds(student)
      .stream()
      .map(Feed::getId)
      .collect(Collectors.toList());

    TokenPayload tokenPayload = new TokenPayload();
    tokenPayload.setId(student.getId());
    tokenPayload.setLastConnection(student.getLastConnection());
    tokenPayload.setFeeds(feeds);
    tokenPayload.setRoles(roles);
    tokenPayload.setClubsAdmin(adminClubs);
    tokenPayload.setClubsPublisher(publisherClubs);
    return tokenPayload;
  }

  private String generateToken(TokenPayload tokenPayload) {
    Calendar calendar = Calendar.getInstance(locale); // gets a calendar using the default time zone and locale.
    calendar.add(Calendar.SECOND, tokenDuration);

    try {
      String payload = new ObjectMapper().writeValueAsString(tokenPayload);
      return JWT.create()
        .withIssuer(issuer)
        .withIssuedAt(Calendar.getInstance(locale).getTime())
        .withExpiresAt(calendar.getTime())
        .withClaim(CLAIM_PAYLOAD, payload)
        .sign(standardAlgorithm);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return null;
  }

  private String generateRefreshToken(TokenPayload tokenPayload) {
    Calendar calendar = Calendar.getInstance(locale); // gets a calendar using the default time zone and locale.
    calendar.add(Calendar.SECOND, refreshTokenDuration);
    try {
      return JWT.create()
        .withIssuer(issuer)
        .withIssuedAt(Calendar.getInstance(locale).getTime())
        .withExpiresAt(calendar.getTime())
        .withClaim(CLAIM_USER_ID, tokenPayload.getId())
        .sign(refreshAlgorithm);
    } catch (JWTCreationException e) {
      e.printStackTrace();
    }
    return null;
  }
}
