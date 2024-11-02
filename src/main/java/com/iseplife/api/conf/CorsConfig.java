package com.iseplife.api.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;

import javax.annotation.PostConstruct;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsConfig implements Filter {

  @Value("${cors.allowed-origin}")
  private String allowedOrigin;

  private CorsConfiguration corsConfiguration;

  @PostConstruct
  public void init() {
    corsConfiguration = new CorsConfiguration();
    corsConfiguration.setAllowCredentials(true);

    for (String origin : allowedOrigin.replaceAll("'", "").split(","))
      if(origin.contains("*"))
        corsConfiguration.addAllowedOriginPattern(origin);
      else
        corsConfiguration.addAllowedOrigin(origin);

    corsConfiguration.addAllowedHeader("*");
    corsConfiguration.addAllowedMethod("*");
  }

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
    HttpServletResponse response = (HttpServletResponse) res;
    HttpServletRequest request = (HttpServletRequest) req;

    String origin = request.getHeader("Origin");


    if(origin != null) {
      String permittedOrigin = corsConfiguration.checkOrigin(origin);
      response.setHeader("Access-Control-Allow-Origin", permittedOrigin);
      response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE");
      response.setHeader("Access-Control-Allow-Credentials", "true");
      response.setHeader("Access-Control-Max-Age", "3600");
      response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
      response.setHeader("Access-Control-Expose-Headers", "x-refresh-token, authorization");
    }


    if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
      response.setStatus(HttpServletResponse.SC_OK);
    } else {
      chain.doFilter(req, res);
    }
  }

  @Override
  public void init(FilterConfig filterConfig) {
  }

  @Override
  public void destroy() {
  }
}
