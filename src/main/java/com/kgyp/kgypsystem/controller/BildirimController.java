package com.kgyp.kgypsystem.controller;

import com.kgyp.kgypsystem.entity.Bildirim;
import com.kgyp.kgypsystem.service.BildirimService;
import com.kgyp.kgypsystem.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bildirim")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class BildirimController {

    @Autowired
    private BildirimService bildirimService;

    @Autowired
    private EmailService emailService;

    // Tüm bildirimleri listele
    @GetMapping
    public ResponseEntity<List<Bildirim>> tumBildirimleriListele() {
        List<Bildirim> bildirimler = bildirimService.tumBildirimleriListele();
        return ResponseEntity.ok(bildirimler);
    }

    // Son bildirimleri getir
    @GetMapping("/son-bildirimler")
    public ResponseEntity<List<Bildirim>> sonBildirimler() {
        List<Bildirim> bildirimler = bildirimService.sonBildirimler();
        return ResponseEntity.ok(bildirimler);
    }

    // Email'e göre bildirimleri getir
    @GetMapping("/email/{email}")
    public ResponseEntity<List<Bildirim>> emaileBildirimler(@PathVariable String email) {
        List<Bildirim> bildirimler = bildirimService.emaileBildirimler(email);
        return ResponseEntity.ok(bildirimler);
    }

    // Test emaili gönder
    @PostMapping("/test-email")
    public ResponseEntity<String> testEmailGonder(@RequestParam String email) {
        boolean basarili = emailService.sendTestEmail(email);

        if (basarili) {
            return ResponseEntity.ok("Test e---maili başarıyla gönderildi: " + email);
        } else {
            return ResponseEntity.badRequest().body("Email gönderilemedi: " + email);
        }
    }

    // Test bildirimi oluştur
    @PostMapping("/test-bildirim")
    public ResponseEntity<Bildirim> testBildirimiOlustur(@RequestParam String email) {
        Bildirim bildirim = bildirimService.testBildirimiGonder(email);
        return ResponseEntity.ok(bildirim);
    }

    // Manuel olarak bekleyen bildirimleri gönder
    @PostMapping("/gonder-bekleyenler")
    public ResponseEntity<String> bekleyenBildirimleriGonder() {
        bildirimService.bekleyenBildirimleriGonder();
        return ResponseEntity.ok("Bekleyen bildirimler gönderildi");
    }
}