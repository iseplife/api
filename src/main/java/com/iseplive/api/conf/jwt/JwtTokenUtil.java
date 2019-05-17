package com.iseplive.api.conf.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iseplive.api.entity.club.Club;
import com.iseplive.api.entity.user.Author;
import com.iseplive.api.entity.user.Student;
import com.iseplive.api.exceptions.IllegalArgumentException;
import com.iseplive.api.services.ClubService;
import com.iseplive.api.services.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Created by Guillaume on 07/08/2017.
 * back
 */
@Service
public class JwtTokenUtil {

  private static final Logger LOG = LoggerFactory.getLogger(JwtTokenUtil.class);

  @Autowired
  StudentService studentService;

  @Autowired
  ClubService clubService;

  public static final String CLAIM_PAYLOAD = "payload";
  private static final String CLAIM_USER_ID = "userID";
  private static final String SECRET_HASHING_ALGORITHM = "SHA-256";
  private final Locale locale = Locale.FRANCE;

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.refreshSecret}")
  private String refreshSecret;

  @Value("${jwt.issuer}")
  private String issuer;

  @Value("${jwt.tokenDuration}")
  private int tokenDuration;

  @Value("${jwt.refreshTokenDuration}")
  private int refreshTokenDuration;

  public DecodedJWT decodeToken(String token) throws JWTVerificationException {
    try {
      Algorithm algorithm = Algorithm.HMAC256(secret);
      JWTVerifier verifier = JWT.require(algorithm)
        .withIssuer(issuer)
        .build(); //Reusable verifier instance
      return verifier.verify(token);
    } catch (UnsupportedEncodingException e) {
      LOG.error("could not decode token", e);
    }
    throw new JWTVerificationException("invalid token");
  }

  public TokenSet generateToken(Student student) {
    TokenPayload tokenPayload = generatePayload(student);
    String token = generateToken(tokenPayload);
    String refreshToken = generateRefreshToken(tokenPayload);
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

  String refreshToken(DecodedJWT jwt) {
    String payloadString = jwt.getClaim(CLAIM_PAYLOAD).asString();
    try {
      TokenPayload tokenPayload = new ObjectMapper().readValue(payloadString, TokenPayload.class);
      return generateToken(tokenPayload);
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
  TokenSet refreshWithToken(String token) throws JWTVerificationException {
    try {
      DecodedJWT decodedJWT = JWT.decode(token);
      Long id = decodedJWT.getClaim(CLAIM_USER_ID).asLong();
      Student student = studentService.getStudent(id);
      TokenPayload tokenPayload = generatePayload(student);
      String secret = generateRefreshSecret(tokenPayload);
      if (secret != null) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier verifier = JWT.require(algorithm)
          .withIssuer(issuer)
          .build(); //Reusable verifier instance
        verifier.verify(token);
        return generateToken(student);
      }
    } catch (UnsupportedEncodingException | IllegalArgumentException e) {
      LOG.error("could not refresh token", e);
    }
    throw new JWTVerificationException("token invalid");
  }

  private TokenPayload generatePayload(Student student) {
    List<String> roles = student.getAuthorities()
      .stream()
      .sorted(Comparator.comparing(GrantedAuthority::getAuthority))
      .map(GrantedAuthority::getAuthority)
      .collect(Collectors.toList());
    List<Long> clubs = clubService.getAdminClubs(student)
      .stream()
      .sorted(Comparator.comparing(Club::getName))
      .map(Author::getId)
      .collect(Collectors.toList());

    TokenPayload tokenPayload = new TokenPayload();
    tokenPayload.setId(student.getId());
    tokenPayload.setRoles(roles);
    tokenPayload.setClubsAdmin(clubs);
    return tokenPayload;
  }

  private String generateToken(TokenPayload tokenPayload) {
    Calendar calendar = Calendar.getInstance(locale); // gets a calendar using the default time zone and locale.
    calendar.add(Calendar.SECOND, tokenDuration);

    try {
      String payload = new ObjectMapper().writeValueAsString(tokenPayload);
      Algorithm algorithm = Algorithm.HMAC256(secret);
      return JWT.create()
        .withIssuer(issuer)
        .withIssuedAt(Calendar.getInstance(locale).getTime())
        .withExpiresAt(calendar.getTime())
        .withClaim(CLAIM_PAYLOAD, payload)
        .sign(algorithm);
    } catch (JsonProcessingException | UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return null;
  }

  private String generateRefreshSecret(TokenPayload tokenPayload) {
    try {
      MessageDigest messageDigest = MessageDigest.getInstance(SECRET_HASHING_ALGORITHM);
      String encryptedString = DatatypeConverter.printHexBinary(
        messageDigest.digest(tokenPayload.toString().getBytes("UTF-8")));
      return encryptedString + refreshSecret;
    } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return null;
  }

  private String generateRefreshToken(TokenPayload tokenPayload) {
    Calendar calendar = Calendar.getInstance(locale); // gets a calendar using the default time zone and locale.
    calendar.add(Calendar.SECOND, refreshTokenDuration);
    try {
      String secret = generateRefreshSecret(tokenPayload);
      if (secret != null) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
          .withIssuer(issuer)
          .withIssuedAt(Calendar.getInstance(locale).getTime())
          .withExpiresAt(calendar.getTime())
          .withClaim(CLAIM_USER_ID, tokenPayload.getId())
          .sign(algorithm);
      } else {
        throw new JWTVerificationException("Could not generate secret");
      }
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return null;
  }
}
