package com.kgyp.kgypsystem.controller;

import com.kgyp.kgypsystem.entity.FinansalHareket;
import com.kgyp.kgypsystem.entity.FinansalHareket.HareketTipi;
import com.kgyp.kgypsystem.entity.FinansalHareket.OdemeYontemi;
import com.kgyp.kgypsystem.service.FinansalHareketService;
import com.kgyp.kgypsystem.service.FinansalHareketService.MaliOzet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/finansal")
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
public class FinansalHareketController {

    @Autowired
    private FinansalHareketService service;

    // ==================== TEMEL CRUD İŞLEMLERİ ====================

    // Tüm finansal hareketleri listele
    @GetMapping
    public ResponseEntity<List<FinansalHareket>> tumHareketleriListele() {
        List<FinansalHareket> hareketler = service.tumHareketleriListele();
        return ResponseEntity.ok(hareketler);
    }

    // ID ile finansal hareket getir
    @GetMapping("/{id}")
    public ResponseEntity<FinansalHareket> hareketGetir(@PathVariable UUID id) {
        Optional<FinansalHareket> hareket = service.hareketBul(id);
        if (hareket.isPresent()) {
            return ResponseEntity.ok(hareket.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Yeni finansal hareket oluştur
    @PostMapping("/gayrimenkul/{varlikId}")
    public ResponseEntity<FinansalHareket> hareketOlustur(
            @PathVariable UUID varlikId,
            @RequestBody FinansalHareket hareket) {
        try {
            FinansalHareket yeniHareket = service.hareketKaydet(varlikId, hareket);
            return ResponseEntity.status(HttpStatus.CREATED).body(yeniHareket);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Finansal hareket güncelle
    @PutMapping("/{id}")
    public ResponseEntity<FinansalHareket> hareketGuncelle(
            @PathVariable UUID id,
            @RequestBody FinansalHareket hareket) {
        try {
            FinansalHareket guncelHareket = service.hareketGuncelle(id, hareket);
            return ResponseEntity.ok(guncelHareket);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Finansal hareket sil
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> hareketSil(@PathVariable UUID id) {
        try {
            service.hareketSil(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ==================== FİLTRELEME ENDPOINT'LERİ ====================

    // Gayrimenkul bazında finansal hareketler
    @GetMapping("/gayrimenkul/{varlikId}")
    public ResponseEntity<List<FinansalHareket>> gayrimenkulHareketleri(@PathVariable UUID varlikId) {
        List<FinansalHareket> hareketler = service.gayrimenkulHareketleri(varlikId);
        return ResponseEntity.ok(hareketler);
    }

    // Hareket tipine göre filtrele
    @GetMapping("/tip/{tip}")
    public ResponseEntity<List<FinansalHareket>> hareketTipineGoreFiltrele(@PathVariable HareketTipi tip) {
        List<FinansalHareket> hareketler = service.hareketTipineGoreFiltrele(tip);
        return ResponseEntity.ok(hareketler);
    }

    // Gelir hareketleri
    @GetMapping("/gelir")
    public ResponseEntity<List<FinansalHareket>> gelirHareketleri() {
        List<FinansalHareket> gelirler = service.hareketTipineGoreFiltrele(HareketTipi.KIRA_GELIRI);
        // Diğer gelir tiplerini de ekleyelim
        gelirler.addAll(service.hareketTipineGoreFiltrele(HareketTipi.DEPOZITO_ALINDI));
        gelirler.addAll(service.hareketTipineGoreFiltrele(HareketTipi.DIGER_GELIR));
        return ResponseEntity.ok(gelirler);
    }

    // Gider hareketleri (birden fazla gider tipini birleştir)
    @GetMapping("/gider")
    public ResponseEntity<List<FinansalHareket>> giderHareketleri() {
        List<FinansalHareket> giderler = service.hareketTipineGoreFiltrele(HareketTipi.BAKIM_ONARIM);
        giderler.addAll(service.hareketTipineGoreFiltrele(HareketTipi.VERGI_HARCI));
        giderler.addAll(service.hareketTipineGoreFiltrele(HareketTipi.SIGORTA));
        giderler.addAll(service.hareketTipineGoreFiltrele(HareketTipi.YONETIM));
        giderler.addAll(service.hareketTipineGoreFiltrele(HareketTipi.AIDAT));
        giderler.addAll(service.hareketTipineGoreFiltrele(HareketTipi.DIGER_GIDER));
        return ResponseEntity.ok(giderler);
    }

    // Onay bekleyen hareketler
    @GetMapping("/onay-bekleyen")
    public ResponseEntity<List<FinansalHareket>> onayBekleyenHareketler() {
        List<FinansalHareket> hareketler = service.onayBekleyenHareketler();
        return ResponseEntity.ok(hareketler);
    }

    // Son hareketler
    @GetMapping("/son-hareketler")
    public ResponseEntity<List<FinansalHareket>> sonHareketler() {
        List<FinansalHareket> hareketler = service.sonHareketler();
        return ResponseEntity.ok(hareketler);
    }

    // ==================== TARİH BAZLI SORGULAR ====================

    // Tarih aralığında hareketler
    @GetMapping("/tarih-araliği")
    public ResponseEntity<List<FinansalHareket>> tarihAraligindaHareketler(
            @RequestParam String baslangic,
            @RequestParam String bitis) {
        try {
            LocalDateTime baslangicTarihi = LocalDateTime.parse(baslangic, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            LocalDateTime bitisTarihi = LocalDateTime.parse(bitis, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            List<FinansalHareket> hareketler = service.tarihAraligindaHareketler(baslangicTarihi, bitisTarihi);
            return ResponseEntity.ok(hareketler);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Aylık kira gelirleri
    @GetMapping("/kira-geliri/{donem}")
    public ResponseEntity<List<FinansalHareket>> donemselKiraGelirleri(@PathVariable String donem) {
        List<FinansalHareket> hareketler = service.donemselKiraGelirleri(donem);
        return ResponseEntity.ok(hareketler);
    }

    // Bu ayki kira gelirleri
    @GetMapping("/bu-ay-kira")
    public ResponseEntity<List<FinansalHareket>> buAyKiraGelirleri() {
        LocalDateTime buAy = LocalDateTime.now();
        String donem = buAy.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        List<FinansalHareket> hareketler = service.donemselKiraGelirleri(donem);
        return ResponseEntity.ok(hareketler);
    }

    // ==================== İŞ AKIŞI ENDPOINT'LERİ ====================

    // Finansal hareket onayla
    @PostMapping("/{id}/onayla")
    public ResponseEntity<FinansalHareket> hareketOnayla(
            @PathVariable UUID id,
            @RequestParam String onaylayanKullanici) {
        try {
            FinansalHareket hareket = service.hareketOnayla(id, onaylayanKullanici);
            return ResponseEntity.ok(hareket);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ==================== MALİ ÖZET VE İSTATİSTİKLER ====================

    // Mali özet bilgileri
    @GetMapping("/mali-ozet")
    public ResponseEntity<MaliOzet> maliOzet() {
        MaliOzet ozet = service.maliOzetHesapla();
        return ResponseEntity.ok(ozet);
    }

    // Toplam hareket sayısı
    @GetMapping("/toplam-hareket-sayisi")
    public ResponseEntity<Long> toplamHareketSayisi() {
        long toplam = service.toplamHareketSayisi();
        return ResponseEntity.ok(toplam);
    }

    // Aylık kira geliri raporu
    @GetMapping("/rapor/aylik-kira")
    public ResponseEntity<List<Object[]>> aylikKiraGeliriRaporu() {
        List<Object[]> rapor = service.aylikKiraGeliriRaporu();
        return ResponseEntity.ok(rapor);
    }

    // Hareket tipi bazında rapor
    @GetMapping("/rapor/tip-bazinda")
    public ResponseEntity<List<Object[]>> hareketTipiBazindaRapor() {
        List<Object[]> rapor = service.hareketTipiBazindaRapor();
        return ResponseEntity.ok(rapor);
    }

    // ==================== HIZLI MALİ BİLGİLER ====================

    // Bu ayki toplam gelir
    @GetMapping("/bu-ay-gelir")
    public ResponseEntity<BigDecimal> buAyToplamGelir() {
        LocalDateTime buAyBaslangic = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
        LocalDateTime buAyBitis = buAyBaslangic.plusMonths(1).minusSeconds(1);

        List<FinansalHareket> hareketler = service.tarihAraligindaHareketler(buAyBaslangic, buAyBitis);

        BigDecimal toplam = hareketler.stream()
                .filter(h -> h.getOnaylanmis() && h.isGelir())
                .map(FinansalHareket::getTutar)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return ResponseEntity.ok(toplam);
    }

    // Bu ayki toplam gider
    @GetMapping("/bu-ay-gider")
    public ResponseEntity<BigDecimal> buAyToplamGider() {
        LocalDateTime buAyBaslangic = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
        LocalDateTime buAyBitis = buAyBaslangic.plusMonths(1).minusSeconds(1);

        List<FinansalHareket> hareketler = service.tarihAraligindaHareketler(buAyBaslangic, buAyBitis);

        BigDecimal toplam = hareketler.stream()
                .filter(h -> h.getOnaylanmis() && h.isGider())
                .map(FinansalHareket::getTutar)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return ResponseEntity.ok(toplam);
    }

    // Bu ayki net kâr/zarar
    @GetMapping("/bu-ay-net-kar")
    public ResponseEntity<BigDecimal> buAyNetKar() {
        LocalDateTime buAyBaslangic = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
        LocalDateTime buAyBitis = buAyBaslangic.plusMonths(1).minusSeconds(1);

        List<FinansalHareket> hareketler = service.tarihAraligindaHareketler(buAyBaslangic, buAyBitis);

        BigDecimal gelir = hareketler.stream()
                .filter(h -> h.getOnaylanmis() && h.isGelir())
                .map(FinansalHareket::getTutar)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal gider = hareketler.stream()
                .filter(h -> h.getOnaylanmis() && h.isGider())
                .map(FinansalHareket::getTutar)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return ResponseEntity.ok(gelir.subtract(gider));
    }

    // ==================== ENUM DEĞERLERİ ====================

    // Hareket tipleri
    @GetMapping("/hareket-tipleri")
    public ResponseEntity<HareketTipi[]> hareketTipleri() {
        return ResponseEntity.ok(HareketTipi.values());
    }

    // Ödeme yöntemleri
    @GetMapping("/odeme-yontemleri")
    public ResponseEntity<OdemeYontemi[]> odemeYontemleri() {
        return ResponseEntity.ok(OdemeYontemi.values());
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