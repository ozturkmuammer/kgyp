package com.kgyp.kgypsystem.service;

import com.kgyp.kgypsystem.entity.Dokuman; // ✅ EKSİK OLAN IMPORT EKLENDİ
import com.kgyp.kgypsystem.entity.Dokuman.DokumanKategorisi; // ✅ ENUM IMPORT EKLENDİ
import com.kgyp.kgypsystem.entity.GayrimenkulVarligi;
import com.kgyp.kgypsystem.entity.Sozlesme;
import com.kgyp.kgypsystem.repository.DokumanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Transactional
public class DokumanService {

    @Autowired
    private DokumanRepository repository;

    @Autowired
    private GayrimenkulVarligiService gayrimenkulService;

    @Autowired
    private SozlesmeService sozlesmeService;

    // Dosya yükleme dizini
    private final String UPLOAD_DIR = "uploads/";

    // İzin verilen dosya tipleri
    private final List<String> ALLOWED_FILE_TYPES = Arrays.asList(
            "PDF", "JPG", "JPEG", "PNG", "GIF", "DOC", "DOCX", "XLS", "XLSX", "TXT"
    );

    // Maksimum dosya boyutu (10MB)
    private final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    // ==================== TEMEL CRUD İŞLEMLERİ ====================

    public List<Dokuman> tumDokumanlariListele() {
        return repository.findAll();
    }

    public Optional<Dokuman> dokumanBul(Long id) {
        return repository.findById(id);
    }

    public void dokumanSil(Long id) {
        Optional<Dokuman> dokuman = repository.findById(id);
        if (dokuman.isPresent()) {
            // Fiziksel dosyayı da sil
            try {
                Path dosyaYolu = Paths.get(dokuman.get().getDosyaYolu());
                Files.deleteIfExists(dosyaYolu);
            } catch (IOException e) {
                // Log hata ama devam et
                System.err.println("Dosya silinemedi: " + e.getMessage());
            }
            repository.deleteById(id);
        } else {
            throw new RuntimeException("Silinecek doküman bulunamadı: " + id);
        }
    }

    public Dokuman dokumanGuncelle(Long id, Dokuman guncelDokuman) {
        Optional<Dokuman> mevcut = repository.findById(id);
        if (mevcut.isPresent()) {
            Dokuman dokuman = mevcut.get();

            if (guncelDokuman.getAciklama() != null) {
                dokuman.setAciklama(guncelDokuman.getAciklama());
            }
            if (guncelDokuman.getKategori() != null) {
                dokuman.setKategori(guncelDokuman.getKategori());
            }

            return repository.save(dokuman);
        } else {
            throw new RuntimeException("Doküman bulunamadı: " + id);
        }
    }

    // ==================== DOSYA YÜKLEME İŞLEMLERİ ====================

    public Dokuman gayrimenkulDosyaYukle(UUID varlikId, MultipartFile file,
                                         DokumanKategorisi kategori, String aciklama,
                                         String yukleyenKullanici) throws IOException {

        // Gayrimenkul kontrolü
        Optional<GayrimenkulVarligi> gayrimenkul = gayrimenkulService.gayrimenkulBul(varlikId);
        if (!gayrimenkul.isPresent()) {
            throw new IllegalArgumentException("Gayrimenkul bulunamadı");
        }

        // Dosya validasyonu
        validateFile(file);

        // Dosyayı kaydet
        String dosyaYolu = dosyaKaydet(file, "gayrimenkul", varlikId.toString());

        // Doküman entity oluştur
        Dokuman dokuman = new Dokuman();
        dokuman.setDosyaAdi(file.getOriginalFilename());
        dokuman.setDosyaYolu(dosyaYolu);
        dokuman.setDosyaTipi(getDosyaTipi(file));
        dokuman.setDosyaBoyutu(file.getSize());
        dokuman.setKategori(kategori);
        dokuman.setAciklama(aciklama);
        dokuman.setGayrimenkulVarligi(gayrimenkul.get());
        dokuman.setYukleyenKullanici(yukleyenKullanici);

        return repository.save(dokuman);
    }

    public Dokuman sozlesmeDosyaYukle(UUID sozlesmeId, MultipartFile file,
                                      DokumanKategorisi kategori, String aciklama,
                                      String yukleyenKullanici) throws IOException {

        // Sözleşme kontrolü
        Optional<Sozlesme> sozlesme = sozlesmeService.sozlesmeBul(sozlesmeId);
        if (!sozlesme.isPresent()) {
            throw new IllegalArgumentException("Sözleşme bulunamadı");
        }

        // Dosya validasyonu
        validateFile(file);

        // Dosyayı kaydet
        String dosyaYolu = dosyaKaydet(file, "sozlesme", sozlesmeId.toString());

        // Doküman entity oluştur
        Dokuman dokuman = new Dokuman();
        dokuman.setDosyaAdi(file.getOriginalFilename());
        dokuman.setDosyaYolu(dosyaYolu);
        dokuman.setDosyaTipi(getDosyaTipi(file));
        dokuman.setDosyaBoyutu(file.getSize());
        dokuman.setKategori(kategori);
        dokuman.setAciklama(aciklama);
        dokuman.setSozlesme(sozlesme.get());
        dokuman.setYukleyenKullanici(yukleyenKullanici);

        return repository.save(dokuman);
    }

    public List<Dokuman> gayrimenkulCokluDosyaYukle(UUID varlikId, MultipartFile[] files,
                                                    DokumanKategorisi kategori, String aciklama,
                                                    String yukleyenKullanici) throws IOException {

        List<Dokuman> yeniDokumanlar = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                Dokuman dokuman = gayrimenkulDosyaYukle(varlikId, file, kategori, aciklama, yukleyenKullanici);
                yeniDokumanlar.add(dokuman);
            }
        }

        return yeniDokumanlar;
    }

    // ==================== FİLTRELEME İŞLEMLERİ ====================

    public List<Dokuman> gayrimenkulDokumanlari(UUID varlikId) {
        return repository.findByGayrimenkulVarligi_VarlikId(varlikId);
    }

    public List<Dokuman> sozlesmeDokumanlari(UUID sozlesmeId) {
        return repository.findBySozlesme_SozlesmeId(sozlesmeId);
    }

    public List<Dokuman> kategoriyeGoreDokumanlar(DokumanKategorisi kategori) {
        return repository.findByKategori(kategori);
    }

    public List<Dokuman> aktifDokumanlar() {
        return repository.findByAktifTrue();
    }

    public List<Dokuman> sonYuklenenDokumanlar() {
        return repository.findTop10ByOrderByYuklenmeTarihiDesc();
    }

    public List<Dokuman> dosyaTipineGoreDokumanlar(String dosyaTipi) {
        return repository.findByDosyaTipi(dosyaTipi);
    }

    public List<Dokuman> kullaniciyaGoreDokumanlar(String kullanici) {
        return repository.findByYukleyenKullaniciContainingIgnoreCase(kullanici);
    }

    public List<Dokuman> dokumanAra(String aramaKelimesi) {
        return repository.findByDosyaAdiContainingIgnoreCaseOrAciklamaContainingIgnoreCase(
                aramaKelimesi, aramaKelimesi);
    }

    // ==================== YÖNETİM İŞLEMLERİ ====================

    public Dokuman dokumanDeaktif(Long id) {
        Optional<Dokuman> dokuman = repository.findById(id);
        if (dokuman.isPresent()) {
            Dokuman d = dokuman.get();
            d.setAktif(false);
            return repository.save(d);
        } else {
            throw new RuntimeException("Doküman bulunamadı: " + id);
        }
    }

    public Dokuman dokumanAktif(Long id) {
        Optional<Dokuman> dokuman = repository.findById(id);
        if (dokuman.isPresent()) {
            Dokuman d = dokuman.get();
            d.setAktif(true);
            return repository.save(d);
        } else {
            throw new RuntimeException("Doküman bulunamadı: " + id);
        }
    }

    // ==================== İSTATİSTİK İŞLEMLERİ ====================

    public long toplamDokumanSayisi() {
        return repository.count();
    }

    public List<Object[]> kategoriBazindaDokumanSayisi() {
        return repository.countByKategori();
    }

    public List<Object[]> tipBazindaDokumanSayisi() {
        return repository.countByDosyaTipi();
    }

    public Long toplamDosyaBoyutu() {
        return repository.sumDosyaBoyutu();
    }

    // ✅ EKSİK METOD EKLENDİ - Controller'da kullanılıyor
    public long aktifDokumanSayisi() {
        return repository.countByAktifTrue();
    }

    // ==================== YARDIMCI METODLAR ====================

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Dosya boş olamaz");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Dosya boyutu 10MB'ı geçemez");
        }

        String dosyaTipi = getDosyaTipi(file);
        if (!ALLOWED_FILE_TYPES.contains(dosyaTipi.toUpperCase())) {
            throw new IllegalArgumentException("Desteklenmeyen dosya tipi: " + dosyaTipi);
        }
    }

    private String getDosyaTipi(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null && originalFilename.contains(".")) {
            return originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toUpperCase();
        }
        return "UNKNOWN";
    }

    private String dosyaKaydet(MultipartFile file, String kategori, String iliskiliId) throws IOException {
        // Upload dizini oluştur
        Path uploadPath = Paths.get(UPLOAD_DIR, kategori, iliskiliId);
        Files.createDirectories(uploadPath);

        // Benzersiz dosya adı oluştur
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String dosyaAdi = timestamp + "_" + file.getOriginalFilename();

        // Dosya yolu
        Path dosyaYolu = uploadPath.resolve(dosyaAdi);

        // Dosyayı kaydet
        Files.copy(file.getInputStream(), dosyaYolu, StandardCopyOption.REPLACE_EXISTING);

        return dosyaYolu.toString();
    }
}