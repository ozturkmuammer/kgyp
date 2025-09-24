package com.kgyp.kgypsystem.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
// ✅ Güvenli CORS ayarları
@CrossOrigin(
        origins = {
                "http://localhost:3000",
                "http://localhost:8080",
                "https://kgyp-frontend.com"
        },
        methods = {RequestMethod.GET},
        allowedHeaders = {"Content-Type", "Authorization", "X-Requested-With"},
        allowCredentials = "true",
        maxAge = 3600
)
public class TestController {

    @GetMapping("/hello")
    public String hello() {
        return "KGYP Sistemi başarıyla çalışıyor!";
    }
}