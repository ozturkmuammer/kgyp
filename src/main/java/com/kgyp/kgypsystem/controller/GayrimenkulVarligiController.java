package com.kgyp.kgypsystem.controller;

import com.kgyp.kgypsystem.entity.GayrimenkulVarligi;
import com.kgyp.kgypsystem.entity.KullanimDurumu;
import com.kgyp.kgypsystem.entity.HizmetTuru;
import com.kgyp.kgypsystem.service.GayrimenkulVarligiService;
import com.kgyp.kgypsystem.service.GayrimenkulVarligiService.PTTGayrimenkulOzeti;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Map;

@RestController
@RequestMapping("/api/gayrimenkul")
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
public class GayrimenkulVarligiController {

    @Autowired
    private GayrimenkulVarligiService service;

    // ==================== TEMEL CRUD İŞLEMLERİ ====================

    // Tüm gayrimenkulleri listele
    @GetMapping
    public ResponseEntity<List<GayrimenkulVarligi>> tumGayrimenkulleriListele() {
        List<GayrimenkulVarligi> gayrimenkuller = service.tumGayrimenkulleriListele();
        return ResponseEntity.ok(gayrimenkuller);
    }

    // ID ile gayrimenkul getir
    @GetMapping("/{id}")
    public ResponseEntity<GayrimenkulVarligi> gayrimenkulGetir(@PathVariable UUID id) {
        Optional<GayrimenkulVarligi> gayrimenkul = service.gayrimenkulBul(id);
        if (gayrimenkul.isPresent()) {
            return ResponseEntity.ok(gayrimenkul.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Yeni gayrimenkul oluştur
    @PostMapping
    public ResponseEntity<GayrimenkulVarligi> gayrimenkulOlustur(@RequestBody GayrimenkulVarligi gayrimenkul) {
        try {
            GayrimenkulVarligi yeniGayrimenkul = service.gayrimenkulKaydet(gayrimenkul);
            return ResponseEntity.status(HttpStatus.CREATED).body(yeniGayrimenkul);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Gayrimenkul güncelle
    @PutMapping("/{id}")
    public ResponseEntity<GayrimenkulVarligi> gayrimenkulGuncelle(
            @PathVariable UUID id,
            @RequestBody GayrimenkulVarligi gayrimenkul) {
        try {
            GayrimenkulVarligi guncelGayrimenkul = service.gayrimenkulGuncelle(id, gayrimenkul);
            return ResponseEntity.ok(guncelGayrimenkul);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Gayrimenkul sil
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> gayrimenkulSil(@PathVariable UUID id) {
        try {
            service.gayrimenkulSil(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ==================== TEMEL ARAMA FONKSİYONLARI ====================

    // Şehre göre arama
    @GetMapping("/sehir/{sehir}")
    public ResponseEntity<List<GayrimenkulVarligi>> sehirGoureAra(@PathVariable String sehir) {
        List<GayrimenkulVarligi> gayrimenkuller = service.sehirGoureGayrimenkulAra(sehir);
        return ResponseEntity.ok(gayrimenkuller);
    }

    // İşyeri adına göre arama
    @GetMapping("/isyeri-adi")
    public ResponseEntity<List<GayrimenkulVarligi>> isyeriAdinaGoreAra(@RequestParam String isyeriAdi) {
        List<GayrimenkulVarligi> gayrimenkuller = service.isyeriAdinaGoreAra(isyeriAdi);
        return ResponseEntity.ok(gayrimenkuller);
    }

    // Kullanım durumuna göre arama
    @GetMapping("/kullanim-durumu/{kullanimDurumu}")
    public ResponseEntity<List<GayrimenkulVarligi>> kullanimDurumunaGoreAra(
            @PathVariable KullanimDurumu kullanimDurumu) {
        List<GayrimenkulVarligi> gayrimenkuller = service.kullanimDurumunaGoreAra(kullanimDurumu);
        return ResponseEntity.ok(gayrimenkuller);
    }

    // Hizmet türüne göre arama
    @GetMapping("/hizmet-turu/{hizmetTuru}")
    public ResponseEntity<List<GayrimenkulVarligi>> hizmetTurunaGoreAra(
            @PathVariable HizmetTuru hizmetTuru) {
        List<GayrimenkulVarligi> gayrimenkuller = service.hizmetTurunaGoreAra(hizmetTuru);
        return ResponseEntity.ok(gayrimenkuller);
    }

    // Adres içinde arama
    @GetMapping("/ara")
    public ResponseEntity<List<GayrimenkulVarligi>> adresIcindeAra(
            @RequestParam String kelime) {
        List<GayrimenkulVarligi> gayrimenkuller = service.adresIcindeAra(kelime);
        return ResponseEntity.ok(gayrimenkuller);
    }

    // Belirli metrekareden büyük gayrimenkuller
    @GetMapping("/buyuk-gayrimenkuller")
    public ResponseEntity<List<GayrimenkulVarligi>> buyukGayrimenkuller(
            @RequestParam Double minM2) {
        List<GayrimenkulVarligi> gayrimenkuller = service.buyukGayrimenkuller(minM2);
        return ResponseEntity.ok(gayrimenkuller);
    }

    // Çoklu kritere göre arama
    @GetMapping("/gelismis-arama")
    public ResponseEntity<List<GayrimenkulVarligi>> gelismisArama(
            @RequestParam(required = false) String sehir,
            @RequestParam(required = false) HizmetTuru hizmetTuru,
            @RequestParam(required = false) KullanimDurumu kullanimDurumu,
            @RequestParam(required = false) String isyeriAdi) {
        List<GayrimenkulVarligi> gayrimenkuller = service.cokluKriterleAra(sehir, hizmetTuru, kullanimDurumu, isyeriAdi);
        return ResponseEntity.ok(gayrimenkuller);
    }

    // ==================== PTT'YE ÖZEL ENDPOINT'LER ====================

    // Operasyonel birimleri listele
    @GetMapping("/operasyonel-birimler")
    public ResponseEntity<List<GayrimenkulVarligi>> operasyonelBirimleriListele() {
        List<GayrimenkulVarligi> gayrimenkuller = service.operasyonelBirimleriGetir();
        return ResponseEntity.ok(gayrimenkuller);
    }

    // Destek birimlerini listele
    @GetMapping("/destek-birimleri")
    public ResponseEntity<List<GayrimenkulVarligi>> destekBirimleriListele() {
        List<GayrimenkulVarligi> gayrimenkuller = service.destekBirimleriGetir();
        return ResponseEntity.ok(gayrimenkuller);
    }

    // Posta ile ilgili birimleri listele
    @GetMapping("/posta-birimleri")
    public ResponseEntity<List<GayrimenkulVarligi>> postaBirimleriListele() {
        List<GayrimenkulVarligi> gayrimenkuller = service.postaIlgiliBirimleriGetir();
        return ResponseEntity.ok(gayrimenkuller);
    }

    // Kargo ile ilgili birimleri listele
    @GetMapping("/kargo-birimleri")
    public ResponseEntity<List<GayrimenkulVarligi>> kargoBirimleriListele() {
        List<GayrimenkulVarligi> gayrimenkuller = service.kargoIlgiliBirimleriGetir();
        return ResponseEntity.ok(gayrimenkuller);
    }

    // Gelir getiren gayrimenkulleri listele
    @GetMapping("/gelir-getiren")
    public ResponseEntity<List<GayrimenkulVarligi>> gelirGetirenGayrimenkulleriListele() {
        List<GayrimenkulVarligi> gayrimenkuller = service.gelirGetirenGayrimenkulleriGetir();
        return ResponseEntity.ok(gayrimenkuller);
    }

    // Kira gideri olan gayrimenkulleri listele
    @GetMapping("/kira-gideri-olan")
    public ResponseEntity<List<GayrimenkulVarligi>> kiraGideriOlanGayrimenkulleriListele() {
        List<GayrimenkulVarligi> gayrimenkuller = service.kiraGideriOlanGayrimenkulleriGetir();
        return ResponseEntity.ok(gayrimenkuller);
    }

    // Atıl gayrimenkulleri listele
    @GetMapping("/atil")
    public ResponseEntity<List<GayrimenkulVarligi>> atilGayrimenkulleriListele() {
        List<GayrimenkulVarligi> gayrimenkuller = service.atilGayrimenkulleriGetir();
        return ResponseEntity.ok(gayrimenkuller);
    }

    // Tahsisli gayrimenkulleri listele
    @GetMapping("/tahsisli")
    public ResponseEntity<List<GayrimenkulVarligi>> tahsisliGayrimenkulleriListele() {
        List<GayrimenkulVarligi> gayrimenkuller = service.tahsisliGayrimenkulleriGetir();
        return ResponseEntity.ok(gayrimenkuller);
    }

    // Veri kalitesi kontrolü
    @GetMapping("/eksik-bilgili")
    public ResponseEntity<List<GayrimenkulVarligi>> eksikBilgiliKayitlar() {
        List<GayrimenkulVarligi> gayrimenkuller = service.eksikBilgiliKayitlar();
        return ResponseEntity.ok(gayrimenkuller);
    }

    // ==================== ÖZEL İŞ KURALLARI ====================

    // Kullanım durumunu değiştir
    @PutMapping("/{id}/kullanim-durumu")
    public ResponseEntity<GayrimenkulVarligi> kullanimDurumuDegistir(
            @PathVariable UUID id,
            @RequestParam KullanimDurumu yeniDurum) {
        try {
            GayrimenkulVarligi guncelGayrimenkul = service.kullanimDurumuDegistir(id, yeniDurum);
            return ResponseEntity.ok(guncelGayrimenkul);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Değerleme raporu güncelleme
    @PutMapping("/{id}/degerleme")
    public ResponseEntity<GayrimenkulVarligi> degerlemeRaporuGuncelle(
            @PathVariable UUID id,
            @RequestParam String raporLink,
            @RequestParam BigDecimal tutar,
            @RequestParam String tarih) {
        try {
            LocalDate degerlemetarihi = LocalDate.parse(tarih);
            GayrimenkulVarligi guncelGayrimenkul = service.degerlemeRaporuGuncelle(
                    id, raporLink, tutar, degerlemetarihi);
            return ResponseEntity.ok(guncelGayrimenkul);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ==================== İSTATİSTİK VE RAPORLAMA ====================

    // PTT Gayrimenkul Portföy Özeti
    @GetMapping("/ptt-portfoy-ozeti")
    public ResponseEntity<PTTGayrimenkulOzeti> pttPortfoyOzeti() {
        PTTGayrimenkulOzeti ozet = service.pttPortfoyOzeti();
        return ResponseEntity.ok(ozet);
    }

    // Genel istatistikler
    @GetMapping("/istatistikler")
    public ResponseEntity<Object> istatistikler() {
        var istatistikler = new java.util.HashMap<String, Object>();
        istatistikler.put("toplamGayrimenkulSayisi", service.toplamGayrimenkulSayisi());
        istatistikler.put("sehirBazindaSayim", service.sehirBazindaSayim());
        istatistikler.put("kullanimDurumuBazindaSayim", service.kullanimDurumuBazindaSayim());
        istatistikler.put("hizmetTuruBazindaSayim", service.hizmetTuruBazindaSayim());
        istatistikler.put("birimTuruBazindaSayim", service.birimTuruBazindaSayim());
        istatistikler.put("toplamBrutMetrekare", service.toplamBrutMetrekare());

        return ResponseEntity.ok(istatistikler);
    }

    // Şehir bazında birim dağılımı
    @GetMapping("/sehir-birim-dagilimi")
    public ResponseEntity<Map<String, Map<String, Long>>> sehirBazindaBirimDagilimi() {
        Map<String, Map<String, Long>> dagilim = service.sehirBazindaBirimDagilimi();
        return ResponseEntity.ok(dagilim);
    }

    // Detaylı istatistikler
    @GetMapping("/detayli-istatistikler")
    public ResponseEntity<Object> detayliIstatistikler() {
        var detayliIstatistikler = new java.util.HashMap<String, Object>();

        // Temel sayılar
        detayliIstatistikler.put("toplamGayrimenkulSayisi", service.toplamGayrimenkulSayisi());
        detayliIstatistikler.put("toplamBrutMetrekare", service.toplamBrutMetrekare());

        // Dağılımlar
        detayliIstatistikler.put("sehirBazindaSayim", service.sehirBazindaSayim());
        detayliIstatistikler.put("kullanimDurumuBazindaSayim", service.kullanimDurumuBazindaSayim());
        detayliIstatistikler.put("hizmetTuruBazindaSayim", service.hizmetTuruBazindaSayim());
        detayliIstatistikler.put("sehirVeHizmetTuruBazindaSayim", service.sehirVeHizmetTuruBazindaSayim());
        detayliIstatistikler.put("birimTuruBazindaSayim", service.birimTuruBazindaSayim());
        detayliIstatistikler.put("kullanimVeHizmetTuruCarprazTablosu", service.kullanimVeHizmetTuruCarprazTablosu());

        // Metrekare dağılımları
        detayliIstatistikler.put("metrekareKullanimDagilimi", service.metrekareKullanimDagilimi());
        detayliIstatistikler.put("metrekareHizmetTuruDagilimi", service.metrekareHizmetTuruDagilimi());

        // PTT özet
        detayliIstatistikler.put("pttPortfoyOzeti", service.pttPortfoyOzeti());

        return ResponseEntity.ok(detayliIstatistikler);
    }

    // ==================== ENUM DEĞERLERİ ====================

    // Kullanım durumu enum değerlerini listele
    @GetMapping("/kullanim-durumlari")
    public ResponseEntity<KullanimDurumu[]> kullanimDurumlari() {
        return ResponseEntity.ok(KullanimDurumu.values());
    }

    // Hizmet türü enum değerlerini listele
    @GetMapping("/hizmet-turleri")
    public ResponseEntity<HizmetTuru[]> hizmetTurleri() {
        return ResponseEntity.ok(HizmetTuru.values());
    }

    // ==================== HATA YÖNETİMİ ====================

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body("Hatalı parametre: " + e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("İşlem hatası: " + e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Sistem hatası: " + e.getMessage());
    }
}