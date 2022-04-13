package com.odde.doughnut.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@Profile({"prod"})
public class ProductionConfiguration extends WebSecurityConfigurerAdapter {

  @Autowired private CommonConfiguration commonConfiguration;

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    http.csrf().disable().authorizeRequests().antMatchers("/api/healthcheck").permitAll();

    commonConfiguration.commonConfig(http, http.oauth2Login());
  }
}
