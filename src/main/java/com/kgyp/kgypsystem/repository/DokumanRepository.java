package com.kgyp.kgypsystem.repository;

import com.kgyp.kgypsystem.entity.Dokuman;
import com.kgyp.kgypsystem.entity.Dokuman.DokumanKategorisi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DokumanRepository extends JpaRepository<Dokuman, Long> {

    // Gayrimenkul ID'sine göre dokümanlar
    List<Dokuman> findByGayrimenkulVarligi_VarlikId(UUID varlikId);

    // Sözleşme ID'sine göre dokümanlar
    List<Dokuman> findBySozlesme_SozlesmeId(UUID sozlesmeId);

    // Kategoriye göre dokümanlar
    List<Dokuman> findByKategori(DokumanKategorisi kategori);

    // Aktif dokümanlar
    List<Dokuman> findByAktifTrue();

    // Pasif dokümanlar
    List<Dokuman> findByAktifFalse();

    // Dosya tipine göre dokümanlar
    List<Dokuman> findByDosyaTipi(String dosyaTipi);

    // Yükleyen kullanıcıya göre dokümanlar
    List<Dokuman> findByYukleyenKullaniciContainingIgnoreCase(String kullanici);

    // Son yüklenen dokümanlar - Düzeltilmiş Top 10 sorgusu
    @Query("SELECT d FROM Dokuman d ORDER BY d.yuklenmeTarihi DESC LIMIT 10")
    List<Dokuman> findTop10ByOrderByYuklenmeTarihiDesc();

    // Dosya adı veya açıklamada arama
    List<Dokuman> findByDosyaAdiContainingIgnoreCaseOrAciklamaContainingIgnoreCase(
            String dosyaAdi, String aciklama);

    // Kategori bazında doküman sayısı
    @Query("SELECT d.kategori, COUNT(d) FROM Dokuman d WHERE d.aktif = true GROUP BY d.kategori")
    List<Object[]> countByKategori();

    // Dosya tipi bazında doküman sayısı
    @Query("SELECT d.dosyaTipi, COUNT(d) FROM Dokuman d WHERE d.aktif = true GROUP BY d.dosyaTipi")
    List<Object[]> countByDosyaTipi();

    // Toplam dosya boyutu
    @Query("SELECT COALESCE(SUM(d.dosyaBoyutu), 0L) FROM Dokuman d WHERE d.aktif = true")
    Long sumDosyaBoyutu();

    // Aktif doküman sayısı
    long countByAktifTrue();

    // Belirli boyuttan büyük dosyalar
    @Query("SELECT d FROM Dokuman d WHERE d.dosyaBoyutu > :boyut AND d.aktif = true")
    List<Dokuman> findByDosyaBoyutuGreaterThan(Long boyut);

    // Yükleyen kullanıcı bazında doküman sayısı
    @Query("SELECT d.yukleyenKullanici, COUNT(d) FROM Dokuman d WHERE d.aktif = true AND d.yukleyenKullanici IS NOT NULL GROUP BY d.yukleyenKullanici")
    List<Object[]> countByYukleyenKullanici();
}