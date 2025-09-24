package com.kgyp.kgypsystem.controller;

import com.kgyp.kgypsystem.entity.Dokuman;
import com.kgyp.kgypsystem.entity.Dokuman.DokumanKategorisi;
import com.kgyp.kgypsystem.service.DokumanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/dokuman")
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
public class DokumanController {

    @Autowired
    private DokumanService service;

    // Dosya yükleme dizini
    private final String UPLOAD_DIR = "uploads/";

    // ==================== TEMEL CRUD İŞLEMLERİ ====================

    // Tüm dokümanları listele
    @GetMapping
    public ResponseEntity<List<Dokuman>> tumDokumanlariListele() {
        List<Dokuman> dokumanlar = service.tumDokumanlariListele();
        return ResponseEntity.ok(dokumanlar);
    }

    // ID ile doküman getir
    @GetMapping("/{id}")
    public ResponseEntity<Dokuman> dokumanGetir(@PathVariable Long id) {
        Optional<Dokuman> dokumanOptional = service.dokumanBul(id);
        if (dokumanOptional.isPresent()) {
            return ResponseEntity.ok(dokumanOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Doküman sil
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> dokumanSil(@PathVariable Long id) {
        try {
            service.dokumanSil(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ==================== DOSYA YÜKLEME İŞLEMLERİ ====================

    // Gayrimenkul için dosya yükleme
    @PostMapping("/gayrimenkul/{varlikId}/yukle")
    public ResponseEntity<Dokuman> gayrimenkulDosyaYukle(
            @PathVariable UUID varlikId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("kategori") DokumanKategorisi kategori,
            @RequestParam(value = "aciklama", required = false) String aciklama,
            @RequestParam(value = "yukleyenKullanici", required = false) String yukleyenKullanici) {

        try {
            Dokuman dokuman = service.gayrimenkulDosyaYukle(varlikId, file, kategori, aciklama, yukleyenKullanici);
            return ResponseEntity.status(HttpStatus.CREATED).body(dokuman);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Sözleşme için dosya yükleme
    @PostMapping("/sozlesme/{sozlesmeId}/yukle")
    public ResponseEntity<Dokuman> sozlesmeDosyaYukle(
            @PathVariable UUID sozlesmeId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("kategori") DokumanKategorisi kategori,
            @RequestParam(value = "aciklama", required = false) String aciklama,
            @RequestParam(value = "yukleyenKullanici", required = false) String yukleyenKullanici) {

        try {
            Dokuman dokuman = service.sozlesmeDosyaYukle(sozlesmeId, file, kategori, aciklama, yukleyenKullanici);
            return ResponseEntity.status(HttpStatus.CREATED).body(dokuman);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Çoklu dosya yükleme (gayrimenkul için)
    @PostMapping("/gayrimenkul/{varlikId}/yukle-coklu")
    public ResponseEntity<List<Dokuman>> gayrimenkulCokluDosyaYukle(
            @PathVariable UUID varlikId,
            @RequestParam("files") MultipartFile[] files,
            @RequestParam("kategori") DokumanKategorisi kategori,
            @RequestParam(value = "aciklama", required = false) String aciklama,
            @RequestParam(value = "yukleyenKullanici", required = false) String yukleyenKullanici) {

        try {
            List<Dokuman> dokumanlar = service.gayrimenkulCokluDosyaYukle(varlikId, files, kategori, aciklama, yukleyenKullanici);
            return ResponseEntity.status(HttpStatus.CREATED).body(dokumanlar);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ==================== DOSYA İNDİRME İŞLEMLERİ ====================

    // Dosya indirme
    @GetMapping("/{id}/indir")
    public ResponseEntity<Resource> dosyaIndir(@PathVariable Long id) {
        try {
            Optional<Dokuman> dokumanOptional = service.dokumanBul(id);
            if (!dokumanOptional.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Dokuman dokuman = dokumanOptional.get();
            Path dosyaYolu = Paths.get(dokuman.getDosyaYolu());
            Resource resource = new UrlResource(dosyaYolu.toUri());

            if (resource.exists() && resource.isReadable()) {
                String contentType = getContentType(dokuman.getDosyaTipi());

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + dokuman.getDosyaAdi() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Dosya önizleme (görüntüleme)
    @GetMapping("/{id}/onizle")
    public ResponseEntity<Resource> dosyaOnizle(@PathVariable Long id) {
        try {
            Optional<Dokuman> dokumanOptional = service.dokumanBul(id);
            if (!dokumanOptional.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Dokuman dokuman = dokumanOptional.get();
            Path dosyaYolu = Paths.get(dokuman.getDosyaYolu());
            Resource resource = new UrlResource(dosyaYolu.toUri());

            if (resource.exists() && resource.isReadable()) {
                String contentType = getContentType(dokuman.getDosyaTipi());

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "inline; filename=\"" + dokuman.getDosyaAdi() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ==================== FİLTRELEME ENDPOINT'LERİ ====================

    // Gayrimenkul dokümanları
    @GetMapping("/gayrimenkul/{varlikId}")
    public ResponseEntity<List<Dokuman>> gayrimenkulDokumanlari(@PathVariable UUID varlikId) {
        List<Dokuman> dokumanlar = service.gayrimenkulDokumanlari(varlikId);
        return ResponseEntity.ok(dokumanlar);
    }

    // Sözleşme dokümanları
    @GetMapping("/sozlesme/{sozlesmeId}")
    public ResponseEntity<List<Dokuman>> sozlesmeDokumanlari(@PathVariable UUID sozlesmeId) {
        List<Dokuman> dokumanlar = service.sozlesmeDokumanlari(sozlesmeId);
        return ResponseEntity.ok(dokumanlar);
    }

    // Kategoriye göre dokümanlar
    @GetMapping("/kategori/{kategori}")
    public ResponseEntity<List<Dokuman>> kategoriyeGoreDokumanlar(@PathVariable DokumanKategorisi kategori) {
        List<Dokuman> dokumanlar = service.kategoriyeGoreDokumanlar(kategori);
        return ResponseEntity.ok(dokumanlar);
    }

    // Aktif dokümanlar
    @GetMapping("/aktif")
    public ResponseEntity<List<Dokuman>> aktifDokumanlar() {
        List<Dokuman> dokumanlar = service.aktifDokumanlar();
        return ResponseEntity.ok(dokumanlar);
    }

    // Son yüklenen dokümanlar
    @GetMapping("/son-yuklenen")
    public ResponseEntity<List<Dokuman>> sonYuklenenDokumanlar() {
        List<Dokuman> dokumanlar = service.sonYuklenenDokumanlar();
        return ResponseEntity.ok(dokumanlar);
    }

    // Dosya tipine göre dokümanlar
    @GetMapping("/tip/{dosyaTipi}")
    public ResponseEntity<List<Dokuman>> dosyaTipineGoreDokumanlar(@PathVariable String dosyaTipi) {
        List<Dokuman> dokumanlar = service.dosyaTipineGoreDokumanlar(dosyaTipi.toUpperCase());
        return ResponseEntity.ok(dokumanlar);
    }

    // Kullanıcıya göre dokümanlar
    @GetMapping("/kullanici/{kullanici}")
    public ResponseEntity<List<Dokuman>> kullaniciyaGoreDokumanlar(@PathVariable String kullanici) {
        List<Dokuman> dokumanlar = service.kullaniciyaGoreDokumanlar(kullanici);
        return ResponseEntity.ok(dokumanlar);
    }

    // ==================== ARAMA İŞLEMLERİ ====================

    // Dosya adında arama
    @GetMapping("/ara")
    public ResponseEntity<List<Dokuman>> dokumanAra(@RequestParam String q) {
        List<Dokuman> dokumanlar = service.dokumanAra(q);
        return ResponseEntity.ok(dokumanlar);
    }

    // ==================== İSTATİSTİK ENDPOINT'LERİ ====================

    // Doküman özet bilgileri
    @GetMapping("/ozet")
    public ResponseEntity<Object> dokumanOzeti() {
        var ozet = new java.util.HashMap<String, Object>();
        ozet.put("toplamDokumanSayisi", service.toplamDokumanSayisi());
        ozet.put("kategoriBazindaSayim", service.kategoriBazindaDokumanSayisi());
        ozet.put("tipBazindaSayim", service.tipBazindaDokumanSayisi());
        ozet.put("toplamDosyaBoyutu", service.toplamDosyaBoyutu());

        return ResponseEntity.ok(ozet);
    }

    // Kategori bazında doküman sayısı
    @GetMapping("/istatistik/kategori")
    public ResponseEntity<List<Object[]>> kategoriBazindaDokumanSayisi() {
        List<Object[]> istatistik = service.kategoriBazindaDokumanSayisi();
        return ResponseEntity.ok(istatistik);
    }

    // Dosya tipi bazında doküman sayısı
    @GetMapping("/istatistik/tip")
    public ResponseEntity<List<Object[]>> tipBazindaDokumanSayisi() {
        List<Object[]> istatistik = service.tipBazindaDokumanSayisi();
        return ResponseEntity.ok(istatistik);
    }

    // Toplam dosya boyutu
    @GetMapping("/toplam-boyut")
    public ResponseEntity<Long> toplamDosyaBoyutu() {
        Long toplamBoyut = service.toplamDosyaBoyutu();
        return ResponseEntity.ok(toplamBoyut != null ? toplamBoyut : 0L);
    }

    // ==================== YÖNETİM İŞLEMLERİ ====================

    // Dokümanı deaktif et
    @PutMapping("/{id}/deaktif")
    public ResponseEntity<Dokuman> dokumanDeaktif(@PathVariable Long id) {
        try {
            Dokuman dokuman = service.dokumanDeaktif(id);
            return ResponseEntity.ok(dokuman);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Dokümanı aktif et
    @PutMapping("/{id}/aktif")
    public ResponseEntity<Dokuman> dokumanAktif(@PathVariable Long id) {
        try {
            Dokuman dokuman = service.dokumanAktif(id);
            return ResponseEntity.ok(dokuman);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Doküman bilgilerini güncelle
    @PutMapping("/{id}")
    public ResponseEntity<Dokuman> dokumanGuncelle(
            @PathVariable Long id,
            @RequestBody Dokuman guncelDokuman) {
        try {
            Dokuman dokuman = service.dokumanGuncelle(id, guncelDokuman);
            return ResponseEntity.ok(dokuman);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ==================== ENUM DEĞERLERİ ====================

    // Doküman kategorileri
    @GetMapping("/kategoriler")
    public ResponseEntity<DokumanKategorisi[]> dokumanKategorileri() {
        return ResponseEntity.ok(DokumanKategorisi.values());
    }

    // ==================== YARDIMCI METODLAR ====================

    private String getContentType(String dosyaTipi) {
        switch (dosyaTipi.toUpperCase()) {
            case "PDF":
                return "application/pdf";
            case "JPG":
            case "JPEG":
                return "image/jpeg";
            case "PNG":
                return "image/png";
            case "GIF":
                return "image/gif";
            case "DOC":
                return "application/msword";
            case "DOCX":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "XLS":
                return "application/vnd.ms-excel";
            case "XLSX":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "TXT":
                return "text/plain";
            default:
                return "application/octet-stream";
        }
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

    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleIOException(IOException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Dosya işlem hatası: " + e.getMessage());
    }
}