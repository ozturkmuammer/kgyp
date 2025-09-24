package com.kgyp.kgypsystem.controller;

import com.kgyp.kgypsystem.entity.BakimVeOnarim;
import com.kgyp.kgypsystem.entity.BakimVeOnarim.BakimDurumu;
import com.kgyp.kgypsystem.entity.BakimVeOnarim.BakimKategorisi;
import com.kgyp.kgypsystem.entity.BakimVeOnarim.OncelikSeviyesi;
import com.kgyp.kgypsystem.service.BakimVeOnarimService;
import com.kgyp.kgypsystem.service.BakimVeOnarimService.BakimOzet;
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
@RequestMapping("/api/bakim")
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
public class BakimVeOnarimController {

    @Autowired
    private BakimVeOnarimService service;

    // Tüm bakım işlerini listele
    @GetMapping
    public ResponseEntity<List<BakimVeOnarim>> tumBakimlariListele() {
        List<BakimVeOnarim> bakimlar = service.tumBakimlariListele();
        return ResponseEntity.ok(bakimlar);
    }

    // Aktif bakım işlerini listele
    @GetMapping("/aktif")
    public ResponseEntity<List<BakimVeOnarim>> aktifBakimlariListele() {
        List<BakimVeOnarim> bakimlar = service.aktifBakimlariListele();
        return ResponseEntity.ok(bakimlar);
    }

    // ID ile bakım işi getir
    @GetMapping("/{id}")
    public ResponseEntity<BakimVeOnarim> bakimGetir(@PathVariable UUID id) {
        Optional<BakimVeOnarim> bakim = service.bakimBul(id);
        if (bakim.isPresent()) {
            return ResponseEntity.ok(bakim.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Yeni bakım işi oluştur
    @PostMapping("/gayrimenkul/{varlikId}")
    public ResponseEntity<BakimVeOnarim> bakimOlustur(
            @PathVariable UUID varlikId,
            @RequestBody BakimVeOnarim bakim) {
        try {
            BakimVeOnarim yeniBakim = service.bakimKaydet(varlikId, bakim);
            return ResponseEntity.status(HttpStatus.CREATED).body(yeniBakim);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Bakım işi güncelle
    @PutMapping("/{id}")
    public ResponseEntity<BakimVeOnarim> bakimGuncelle(
            @PathVariable UUID id,
            @RequestBody BakimVeOnarim bakim) {
        try {
            BakimVeOnarim guncelBakim = service.bakimGuncelle(id, bakim);
            return ResponseEntity.ok(guncelBakim);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Bakım işini sil
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> bakimSil(@PathVariable UUID id) {
        try {
            service.bakimSil(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Gayrimenkul bazında bakım işleri
    @GetMapping("/gayrimenkul/{varlikId}")
    public ResponseEntity<List<BakimVeOnarim>> gayrimenkulBakimlari(@PathVariable UUID varlikId) {
        List<BakimVeOnarim> bakimlar = service.gayrimenkulBakimlari(varlikId);
        return ResponseEntity.ok(bakimlar);
    }

    // Duruma göre bakım işleri
    @GetMapping("/durum/{durum}")
    public ResponseEntity<List<BakimVeOnarim>> durumaGoreBakimlar(@PathVariable BakimDurumu durum) {
        List<BakimVeOnarim> bakimlar = service.durumaGoreBakimlar(durum);
        return ResponseEntity.ok(bakimlar);
    }

    // Kategoriye göre bakım işleri
    @GetMapping("/kategori/{kategori}")
    public ResponseEntity<List<BakimVeOnarim>> kategoriyeGoreBakimlar(@PathVariable BakimKategorisi kategori) {
        List<BakimVeOnarim> bakimlar = service.kategoriyeGoreBakimlar(kategori);
        return ResponseEntity.ok(bakimlar);
    }

    // Önceliğe göre bakım işleri
    @GetMapping("/oncelik/{oncelik}")
    public ResponseEntity<List<BakimVeOnarim>> onceligeGoreBakimlar(@PathVariable OncelikSeviyesi oncelik) {
        List<BakimVeOnarim> bakimlar = service.onceligeGoreBakimlar(oncelik);
        return ResponseEntity.ok(bakimlar);
    }

    // Kritik bakım işleri
    @GetMapping("/kritik")
    public ResponseEntity<List<BakimVeOnarim>> kritikBakimlar() {
        List<BakimVeOnarim> bakimlar = service.kritikBakimlar();
        return ResponseEntity.ok(bakimlar);
    }

    // Geciken bakım işleri
    @GetMapping("/geciken")
    public ResponseEntity<List<BakimVeOnarim>> gecikenBakimlar() {
        List<BakimVeOnarim> bakimlar = service.gecikenBakimlar();
        return ResponseEntity.ok(bakimlar);
    }

    // Bu hafta başlayacak bakımlar
    @GetMapping("/bu-hafta")
    public ResponseEntity<List<BakimVeOnarim>> buHaftaBaslayacakBakimlar() {
        List<BakimVeOnarim> bakimlar = service.buHaftaBaslayacakBakimlar();
        return ResponseEntity.ok(bakimlar);
    }

    // Garanide olan bakımlar
    @GetMapping("/garantide")
    public ResponseEntity<List<BakimVeOnarim>> garantideOlanBakimlar() {
        List<BakimVeOnarim> bakimlar = service.garantideOlanBakimlar();
        return ResponseEntity.ok(bakimlar);
    }

    // Son eklenen bakımlar
    @GetMapping("/son-eklenen")
    public ResponseEntity<List<BakimVeOnarim>> sonEklenenBakimlar() {
        List<BakimVeOnarim> bakimlar = service.sonEklenenBakimlar();
        return ResponseEntity.ok(bakimlar);
    }

    // Bakım arama
    @GetMapping("/ara")
    public ResponseEntity<List<BakimVeOnarim>> bakimAra(@RequestParam String q) {
        List<BakimVeOnarim> bakimlar = service.bakimAra(q);
        return ResponseEntity.ok(bakimlar);
    }

    // Sorumlu personele göre bakımlar
    @GetMapping("/personel")
    public ResponseEntity<List<BakimVeOnarim>> sorumluPersonelBakimlari(@RequestParam String personel) {
        List<BakimVeOnarim> bakimlar = service.sorumluPersonelBakimlari(personel);
        return ResponseEntity.ok(bakimlar);
    }

    // Tedarikci firmaya göre bakımlar
    @GetMapping("/tedarikci")
    public ResponseEntity<List<BakimVeOnarim>> tedarikcieBakimlari(@RequestParam String firma) {
        List<BakimVeOnarim> bakimlar = service.tedarikcieBakimlari(firma);
        return ResponseEntity.ok(bakimlar);
    }

    // Tarih aralığında tamamlanan bakımlar
    @GetMapping("/tamamlanan")
    public ResponseEntity<List<BakimVeOnarim>> tarihAraligindaTamamlananBakimlar(
            @RequestParam String baslangic,
            @RequestParam String bitis) {
        try {
            LocalDateTime baslangicTarihi = LocalDateTime.parse(baslangic, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            LocalDateTime bitisTarihi = LocalDateTime.parse(bitis, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            List<BakimVeOnarim> bakimlar = service.tarihAraligindaTamamlananBakimlar(baslangicTarihi, bitisTarihi);
            return ResponseEntity.ok(bakimlar);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ==================== İŞ AKIŞI ENDPOINT'LERİ ====================

    // Bakım işini başlat
    @PostMapping("/{id}/baslat")
    public ResponseEntity<BakimVeOnarim> bakimBaslat(
            @PathVariable UUID id,
            @RequestParam(required = false) String sorumluPersonel) {
        try {
            BakimVeOnarim bakim = service.bakimBaslat(id, sorumluPersonel);
            return ResponseEntity.ok(bakim);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Bakım işini tamamla
    @PostMapping("/{id}/tamamla")
    public ResponseEntity<BakimVeOnarim> bakimTamamla(
            @PathVariable UUID id,
            @RequestParam BigDecimal gercekMaliyet,
            @RequestParam(required = false) String notlar) {
        try {
            BakimVeOnarim bakim = service.bakimTamamla(id, gercekMaliyet, notlar);
            return ResponseEntity.ok(bakim);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Bakım işini iptal et
    @PostMapping("/{id}/iptal")
    public ResponseEntity<BakimVeOnarim> bakimIptalEt(
            @PathVariable UUID id,
            @RequestParam String sebep) {
        try {
            BakimVeOnarim bakim = service.bakimIptalEt(id, sebep);
            return ResponseEntity.ok(bakim);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Garanti güncelle
    @PutMapping("/{id}/garanti")
    public ResponseEntity<BakimVeOnarim> garantiGuncelle(
            @PathVariable UUID id,
            @RequestParam Integer garantiSuresiAy) {
        try {
            BakimVeOnarim bakim = service.garantiGuncelle(id, garantiSuresiAy);
            return ResponseEntity.ok(bakim);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ==================== İSTATİSTİK ENDPOINT'LERİ ====================

    // Bakım özet bilgileri
    @GetMapping("/ozet")
    public ResponseEntity<BakimOzet> bakimOzeti() {
        BakimOzet ozet = service.bakimOzetiHesapla();
        return ResponseEntity.ok(ozet);
    }

    // Kategori bazında bakım sayısı
    @GetMapping("/istatistik/kategori")
    public ResponseEntity<List<Object[]>> kategoriBazindaBakimSayisi() {
        List<Object[]> istatistik = service.kategoriBazindaBakimSayisi();
        return ResponseEntity.ok(istatistik);
    }

    // Durum bazında bakım sayısı
    @GetMapping("/istatistik/durum")
    public ResponseEntity<List<Object[]>> durumBazindaBakimSayisi() {
        List<Object[]> istatistik = service.durumBazindaBakimSayisi();
        return ResponseEntity.ok(istatistik);
    }

    // ==================== ENUM DEĞERLERİ ====================

    // Bakım kategorileri
    @GetMapping("/kategoriler")
    public ResponseEntity<BakimKategorisi[]> bakimKategorileri() {
        return ResponseEntity.ok(BakimKategorisi.values());
    }

    // Bakım durumları
    @GetMapping("/durumlar")
    public ResponseEntity<BakimDurumu[]> bakimDurumlari() {
        return ResponseEntity.ok(BakimDurumu.values());
    }

    // Öncelik seviyeleri
    @GetMapping("/oncelikler")
    public ResponseEntity<OncelikSeviyesi[]> oncelikSeviyeleri() {
        return ResponseEntity.ok(OncelikSeviyesi.values());
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
}