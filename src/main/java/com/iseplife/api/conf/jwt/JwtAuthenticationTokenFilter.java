package com.iseplife.api.conf.jwt;

import com.auth0.jwt.exceptions.JWTVerificationException;
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
    String authToken = request.getHeader("Authorization");
    String refreshToken = request.getHeader("X-Refresh-Token");

    if (authToken != null && refreshToken != null) {
      DecodedJWT jwt;
      TokenSet tokenSet = null;
      if (authToken.startsWith("Bearer ")) {
        authToken = authToken.substring(7);
        try {
          LOG.debug("decoding token");
          jwt = jwtTokenUtil.decodeToken(authToken);
        } catch (JWTVerificationException e) {

          try {
            LOG.debug("token expired, trying to refresh the token");
            tokenSet = jwtTokenUtil.refreshWithToken(refreshToken);
            jwt = jwtTokenUtil.decodeToken(tokenSet.getToken());
          } catch (JWTVerificationException e1) {
            LOG.debug("token could not be refreshed, refresh token is invalid");
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e1.getMessage());
            return;
          }

        }
      } else {
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Authentication schema not found");
        return;
      }

      if (jwt != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        JwtAuthenticationToken authentication = new JwtAuthenticationToken(jwt);
        authentication.setAuthenticated(true);
        if (tokenSet != null) {
          response.setHeader("Authorization", tokenSet.getToken());
          response.setHeader("X-Refresh-Token", tokenSet.getRefreshToken());
        } else {
          response.setHeader("Authorization", jwtTokenUtil.refreshToken(jwt));
          response.setHeader("X-Refresh-Token", refreshToken);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    }

    chain.doFilter(request, response);
  }
}
