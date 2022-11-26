package com.microservice.demo.controller;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;
import com.microservice.demo.dto.TokenResponse;
import com.microservice.demo.dto.UserDto;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@CrossOrigin
@RestController
@RequestMapping("/api/demo")
public class DemoController {

    private final RestTemplate restTemplate = new RestTemplate();

    @RequestMapping("")
    public String home() {
        SecretClient secretClient = new SecretClientBuilder()
                .vaultUrl("https://pokeshop-key-vault.vault.azure.net")
                .credential(new DefaultAzureCredentialBuilder().build())
                .buildClient();
        KeyVaultSecret key = secretClient.getSecret("server-port");
        return "<h3>Bienvenido a la prueba de Valet Key Pattern</h3>\nserver.port: " + key.getValue();
    }

    @PostMapping("/token")
    public ResponseEntity<TokenResponse> token(@RequestBody UserDto user) {
        TokenResponse tokenResponse = getUserAccessToken(user.getUsername(), user.getPassword());
        return ResponseEntity.status(HttpStatus.OK).body(tokenResponse);
    }

    private TokenResponse getUserAccessToken(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        SecretClient secretClient = new SecretClientBuilder()
                .vaultUrl("https://pokeshop-key-vault.vault.azure.net")
                .credential(new DefaultAzureCredentialBuilder().build())
                .buildClient();
        String CLIENT_ID = secretClient.getSecret("client-id").getValue();
        String CLIENT_SECRET = secretClient.getSecret("client-secret").getValue();
        String URL = secretClient.getSecret("url").getValue();
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", CLIENT_ID);
        map.add("client_secret", CLIENT_SECRET);
        map.add("grant_type", "password");
        map.add("scope", "openid");
        map.add("username", username);
        map.add("password", password);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        ResponseEntity<TokenResponse> response = restTemplate.postForEntity(URL, request, TokenResponse.class);
        return response.getBody();
    }


}