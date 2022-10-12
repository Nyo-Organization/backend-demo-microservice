package com.microservice.demo.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/api/demo")
public class DemoController {

    @RequestMapping("")
    public String home() {
        return "Bienvenido al Microservicio Demo en Azure\n By. Anderson Lazo";
    }

}