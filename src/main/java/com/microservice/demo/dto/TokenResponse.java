package com.microservice.demo.dto;

import lombok.Data;

@Data
public class TokenResponse {

    private String id_token;
    private String access_token;

}