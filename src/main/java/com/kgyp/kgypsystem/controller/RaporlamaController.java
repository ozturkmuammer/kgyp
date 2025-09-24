package com.kgyp.kgypsystem.controller;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/raporlama")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class RaporlamaController {

    // Basit mock raporlama endpoint'leri
    @GetMapping("/portfoy-raporu")
    public ResponseEntity<String> portfoyRaporu() {
        return ResponseEntity.ok("Portföy raporu - henüz implement edilmedi");
    }

    @GetMapping("/finansal-rapor")
    public ResponseEntity<String> finansalRapor(
            @RequestParam int yil,
            @RequestParam(required = false) Integer ay) {

        String message = ay != null ?
                String.format("Finansal rapor %d-%02d - henüz implement edilmedi", yil, ay) :
                String.format("Finansal rapor %d - henüz implement edilmedi", yil);

        return ResponseEntity.ok(message);
    }

    @GetMapping("/sozlesme-raporu")
    public ResponseEntity<String> sozlesmeRaporu() {
        return ResponseEntity.ok("Sözleşme raporu - henüz implement edilmedi");
    }
}