package com.iseplife.api.conf;

import com.iseplife.api.conf.jwt.JwtAuthenticationTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Created by Guillaume on 06/08/2017.
 * back
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Bean
  public JwtAuthenticationTokenFilter authenticationTokenFilterBean() {
    return new JwtAuthenticationTokenFilter();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
      // don't need csrf because of token in header
      .csrf().disable()

      // don't create session
      .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

      .and()

      .authorizeRequests()

      .antMatchers("/auth/**").permitAll()
      .antMatchers("/alumni-auth/login").permitAll()
      .antMatchers("/health/**").permitAll()
      .antMatchers("/dd/").permitAll()
      .antMatchers("/webpush/register/validate").permitAll()
      .antMatchers("/media/lambda/**").permitAll()


      .anyRequest().authenticated();

    http
      .addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);

  }

  @Override
  public void configure(WebSecurity web) {
    web.ignoring().antMatchers(
      "/ws/**",
      "/v2/api-docs",
      "/configuration/ui",
      "/configuration/security",
      "/webjars/**"
    );
  }


}
