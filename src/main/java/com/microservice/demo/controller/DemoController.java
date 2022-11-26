package com.microservice.demo.controller;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/api/demo")
public class DemoController {

//    @Value("${connectionString}")
//    private String connectionString;

    @RequestMapping("")
    public String home() {
        String keyVaultName = "pokeshop-key-vault";
        String keyVaultUri = "https://" + keyVaultName + ".vault.azure.net";

        System.out.printf("key vault name = %s and key vault URI = %s \n", keyVaultName, keyVaultUri);

        SecretClient secretClient = new SecretClientBuilder()
                .vaultUrl(keyVaultUri)
                .credential(new DefaultAzureCredentialBuilder().build())
                .buildClient();

//        String secretName = "test";
//        String secretValue = "8081";
//        System.out.print("Creating a secret in " + keyVaultName + " called '" + secretName + "' with value '" + secretValue + " ... \n");
//        secretClient.setSecret(new KeyVaultSecret(secretName, secretValue));
        KeyVaultSecret key = secretClient.getSecret("test");
        return key.getValue();
    }

}