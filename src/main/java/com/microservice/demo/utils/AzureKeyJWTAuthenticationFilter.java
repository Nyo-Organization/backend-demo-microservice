package com.microservice.demo.utils;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@Slf4j
@Component
public class AzureKeyJWTAuthenticationFilter extends GenericFilterBean {

    private static final String ALLOW_HEADERS = "Content-Type";
    private static final String METHODS_HTTP = "GET, POST, PUT , OPTIONS, DELETE";
    private static final String ALL_ORIGIN = "*";
    private static final String HEADER_ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
    private static final String HEADER_ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
    private static final String HEADER_ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    private static final String EMPTY = "";
    private static final String HEADER_BEARER = "Bearer ";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String SECRET = "mySecretKey";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        Authentication authentication;
        HttpServletRequest rq;
        String tokenJWT;

        rq = (HttpServletRequest) request;
        if (rq.getServletPath().contains("/api/demo/token")) {
            filterChain.doFilter(request, response);
        } else {
            try {
                tokenJWT = rq.getHeader(HEADER_AUTHORIZATION);
                if (tokenJWT != null) {
                    tokenJWT = tokenJWT.replace(HEADER_BEARER, EMPTY);
                    if (validateToken(tokenJWT)) {
                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(new User("userpro", EMPTY, new ArrayList<>()), EMPTY, new ArrayList<>());
                        auth.setDetails(tokenJWT);
                        authentication = auth;
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
                filterChain.doFilter(request, response);
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
                HttpServletResponse responseNew = (HttpServletResponse) response;
                responseNew.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                log.error("Error occured while processing KeyVault ID Token", e);
                responseNew.setHeader(HEADER_ACCESS_CONTROL_ALLOW_ORIGIN, ALL_ORIGIN);
                responseNew.setHeader(HEADER_ACCESS_CONTROL_ALLOW_METHODS, METHODS_HTTP);
                responseNew.setHeader(HEADER_ACCESS_CONTROL_ALLOW_HEADERS, ALLOW_HEADERS);
            }
        }
    }

    private Boolean validateToken(String token) {
        SecretClient secretClient = new SecretClientBuilder()
                .vaultUrl("https://pokeshop-key-vault.vault.azure.net")
                .credential(new DefaultAzureCredentialBuilder().build())
                .buildClient();
        String value = secretClient.getSecret("access-token").getValue();
        return token.equals(value);
    }

}