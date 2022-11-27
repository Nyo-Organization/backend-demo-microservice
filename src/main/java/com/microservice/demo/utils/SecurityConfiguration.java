package com.microservice.demo.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private AzureKeyJWTAuthenticationFilter azureKeyJWTAuthenticationFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.headers().cacheControl();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().csrf().disable()
                .authorizeRequests().antMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()
                .antMatchers(HttpMethod.GET, "/v2/api-docs").permitAll()
                .antMatchers(HttpMethod.GET, "/swagger-ui/index.html").permitAll()
                .antMatchers(HttpMethod.GET, "/healthcheck/status").permitAll()
                .antMatchers(HttpMethod.GET, "/params/version").permitAll()
                .antMatchers("/api/demo").authenticated()
                .antMatchers("/**").permitAll().anyRequest().authenticated().and()
                .addFilterBefore(azureKeyJWTAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
