package com.kgyp.kgypsystem.controller;

import com.kgyp.kgypsystem.entity.Sozlesme;
import com.kgyp.kgypsystem.entity.KiraArtisMetodu;
import com.kgyp.kgypsystem.service.SozlesmeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.kgyp.kgypsystem.entity.SozlesmeRequest;
import java.util.Map;
import java.time.LocalDate;
import java.math.BigDecimal;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/sozlesme")
// ✅ Güvenli CORS ayarları
@CrossOrigin(
        origins = {
                "http://localhost:3000",
                "http://localhost:8080",
                "https://kgyp-frontend.com"
        },
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
        allowedHeaders = {"Content-Type", "Authorization", "X-Requested-With"},
        allowCredentials = "true",
        maxAge = 3600
)
public class SozlesmeController {

    @Autowired
    private SozlesmeService service;

    @GetMapping
    public ResponseEntity<List<Sozlesme>> tumSozlesmeleriListele() {
        List<Sozlesme> sozlesmeler = service.tumSozlesmeleriListele();
        return ResponseEntity.ok(sozlesmeler);
    }

    @GetMapping("/aktif")
    public ResponseEntity<List<Sozlesme>> aktifSozlesmeleriListele() {
        List<Sozlesme> sozlesmeler = service.aktifSozlesmeleriListele();
        return ResponseEntity.ok(sozlesmeler);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sozlesme> sozlesmeGetir(@PathVariable UUID id) {
        Optional<Sozlesme> sozlesme = service.sozlesmeBul(id);
        if (sozlesme.isPresent()) {
            return ResponseEntity.ok(sozlesme.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Sozlesme> sozlesmeOlustur(@RequestBody SozlesmeRequest request) {
        try {
            Sozlesme sozlesme = new Sozlesme();
            sozlesme.setKiraciAdi(request.getKiraciAdi());
            sozlesme.setKiralayanAdi(request.getKiralayanAdi());
            sozlesme.setSozlesmeBaslangicTarihi(request.getSozlesmeBaslangicTarihi());
            sozlesme.setSozlesmeBitisTarihi(request.getSozlesmeBitisTarihi());
            sozlesme.setAylikKiraTutari(request.getAylikKiraTutari());
            sozlesme.setKiraArtisMetodu(request.getKiraArtisMetodu());
            sozlesme.setKiraOdemeGunu(request.getKiraOdemeGunu());

            Sozlesme yeniSozlesme = service.sozlesmeKaydet(request.getVarlikId(), sozlesme);
            return ResponseEntity.status(HttpStatus.CREATED).body(yeniSozlesme);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Sozlesme> sozlesmeGuncelle(@PathVariable UUID id, @RequestBody Sozlesme sozlesme) {
        try {
            Sozlesme guncelSozlesme = service.sozlesmeGuncelle(id, sozlesme);
            return ResponseEntity.ok(guncelSozlesme);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> sozlesmeSil(@PathVariable UUID id) {
        try {
            service.sozlesmeSil(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/yakinda-dolacak")
    public ResponseEntity<List<Sozlesme>> yakindaSuresiDolacakSozlesmeler() {
        List<Sozlesme> sozlesmeler = service.yakindaSuresiDolacakSozlesmeler();
        return ResponseEntity.ok(sozlesmeler);
    }

    @GetMapping("/kira-artisi-yapilmamis")
    public ResponseEntity<List<Sozlesme>> kiraArtisiYapilmamisSozlesmeler() {
        List<Sozlesme> sozlesmeler = service.kiraArtisiYapilmamisSozlesmeler();
        return ResponseEntity.ok(sozlesmeler);
    }

    @GetMapping("/bugun-kira-odeme")
    public ResponseEntity<List<Sozlesme>> bugunKiraOdemeSozlesmeleri() {
        List<Sozlesme> sozlesmeler = service.bugunKiraOdemeSozlesmeleri();
        return ResponseEntity.ok(sozlesmeler);
    }

    @PutMapping("/{id}/kira-artisi")
    public ResponseEntity<Sozlesme> kiraArtisiYap(@PathVariable UUID id, @RequestParam BigDecimal yeniTutar) {
        try {
            Sozlesme guncelSozlesme = service.kiraArtisiYap(id, yeniTutar);
            return ResponseEntity.ok(guncelSozlesme);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/sonlandir")
    public ResponseEntity<Sozlesme> sozlesmeSonlandir(@PathVariable UUID id) {
        try {
            Sozlesme sonlananSozlesme = service.sozlesmeSonlandir(id);
            return ResponseEntity.ok(sonlananSozlesme);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/toplam-kira-geliri")
    public ResponseEntity<Double> toplamAylikKiraGeliri() {
        Double toplam = service.toplamAylikKiraGeliri();
        return ResponseEntity.ok(toplam);
    }

    @GetMapping("/kira-artis-metodlari")
    public ResponseEntity<KiraArtisMetodu[]> kiraArtisMetodlari() {
        return ResponseEntity.ok(KiraArtisMetodu.values());
    }
}