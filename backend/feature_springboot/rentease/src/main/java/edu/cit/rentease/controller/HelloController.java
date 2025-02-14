package edu.cit.rentease.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/")
    public String HelloWithName() {
        return "Hello, World!";
    }
}
