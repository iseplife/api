package com.iseplife.api.conf.jwt;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Guillaume on 07/08/2017.
 * back
 */
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {


  private final Logger LOG = LoggerFactory.getLogger(JwtAuthenticationTokenFilter.class);

  @Autowired
  private JwtTokenUtil jwtTokenUtil;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
    String token = request.getHeader("Authorization");
    String refreshToken = request.getHeader("X-Refresh-Token");

    if (token != null && refreshToken != null) {
      DecodedJWT jwt;
      if (token.startsWith("Bearer ")) {
        token = token.substring(7);
        try {
          LOG.debug("decoding token");
          jwt = jwtTokenUtil.decodeToken(token);
        } catch (JWTVerificationException e) {
          LOG.debug("token expired, refresh the token");
          response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
          return;
        }
      } else {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication schema not found");
        return;
      }

      if (jwt != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        JwtAuthenticationToken authentication = new JwtAuthenticationToken(jwt);
        authentication.setAuthenticated(true);

        response.setHeader("Authorization", token);
        response.setHeader("X-Refresh-Token", refreshToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    }

    chain.doFilter(request, response);
  }
}
