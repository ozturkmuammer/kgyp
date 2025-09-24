package com.kgyp.kgypsystem.repository;

import com.kgyp.kgypsystem.entity.GayrimenkulVarligi;
import com.kgyp.kgypsystem.entity.HizmetTuru;
import com.kgyp.kgypsystem.entity.KullanimDurumu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface GayrimenkulVarligiRepository extends JpaRepository<GayrimenkulVarligi, UUID> {

    // ==================== TEMEL ARAMA METODLARI ====================

    List<GayrimenkulVarligi> findBySehir(String sehir);
    List<GayrimenkulVarligi> findByAdresContainingIgnoreCase(String arananKelime);
    List<GayrimenkulVarligi> findByBrutM2GreaterThan(Double minM2);
    List<GayrimenkulVarligi> findByIsyeriAdiContainingIgnoreCase(String isyeriAdi);
    List<GayrimenkulVarligi> findByKullanimDurumu(KullanimDurumu kullanimDurumu);
    List<GayrimenkulVarligi> findByHizmetTuru(HizmetTuru hizmetTuru);

    // ==================== ÇOKLU KRİTER ARAMA ====================

    @Query("SELECT g FROM GayrimenkulVarligi g WHERE " +
            "(:sehir IS NULL OR LOWER(g.sehir) = LOWER(:sehir)) AND " +
            "(:hizmetTuru IS NULL OR g.hizmetTuru = :hizmetTuru) AND " +
            "(:kullanimDurumu IS NULL OR g.kullanimDurumu = :kullanimDurumu) AND " +
            "(:isyeriAdi IS NULL OR LOWER(g.isyeriAdi) LIKE LOWER(CONCAT('%', :isyeriAdi, '%')))")
    List<GayrimenkulVarligi> findByMultipleCriteria(
            @Param("sehir") String sehir,
            @Param("hizmetTuru") HizmetTuru hizmetTuru,
            @Param("kullanimDurumu") KullanimDurumu kullanimDurumu,
            @Param("isyeriAdi") String isyeriAdi);

    // ==================== PTT'YE ÖZEL SORGULAR ====================

    @Query("SELECT g FROM GayrimenkulVarligi g WHERE g.hizmetTuru IN " +
            "('MUDURLUK', 'SUBE', 'PIM', 'UPIM', 'UKIM', 'KIM', 'PDM', 'KDM', 'DTM')")
    List<GayrimenkulVarligi> findOperasyonelBirimler();

    @Query("SELECT g FROM GayrimenkulVarligi g WHERE g.hizmetTuru IN " +
            "('ATM', 'ARSA', 'LOJMAN', 'BAZ_ISTASYONU', 'OTOPARK', 'EK_BINA')")
    List<GayrimenkulVarligi> findDestekBirimleri();

    @Query("SELECT g FROM GayrimenkulVarligi g WHERE g.hizmetTuru IN ('PIM', 'UPIM', 'PDM')")
    List<GayrimenkulVarligi> findPostaIlgiliBirimler();

    @Query("SELECT g FROM GayrimenkulVarligi g WHERE g.hizmetTuru IN ('UKIM', 'KIM', 'KDM')")
    List<GayrimenkulVarligi> findKargoIlgiliBirimler();

    List<GayrimenkulVarligi> findByKullanimDurumuEquals(KullanimDurumu kullanimDurumu);

    @Query("SELECT g FROM GayrimenkulVarligi g WHERE g.kullanimDurumu = 'KIRACISIYIZ'")
    List<GayrimenkulVarligi> findKiraGideriOlanGayrimenkuller();

    @Query("SELECT g FROM GayrimenkulVarligi g WHERE g.kullanimDurumu = 'MAL_SAHIBI_ATIL'")
    List<GayrimenkulVarligi> findAtilGayrimenkuller();

    @Query("SELECT g FROM GayrimenkulVarligi g WHERE g.kullanimDurumu = 'TAHSISLI_KULLANIM'")
    List<GayrimenkulVarligi> findTahsisliGayrimenkuller();

    // ==================== VERİ KALİTESİ ====================

    @Query("SELECT g FROM GayrimenkulVarligi g WHERE " +
            "g.isyeriAdi IS NULL OR g.isyeriAdi = '' OR " +
            "g.adres IS NULL OR g.adres = '' OR " +
            "g.sehir IS NULL OR g.sehir = '' OR " +
            "g.hizmetTuru IS NULL OR " +
            "g.kullanimDurumu IS NULL")
    List<GayrimenkulVarligi> findIncompleteRecords();

    // ==================== İSTATİSTİKLER ====================

    @Query("SELECT g.sehir, COUNT(g) FROM GayrimenkulVarligi g GROUP BY g.sehir ORDER BY COUNT(g) DESC")
    List<Object[]> countBySehir();

    // ✅ EKLENDİ: Top 10 şehir (Dashboard için)
    @Query("SELECT g.sehir, COUNT(g) FROM GayrimenkulVarligi g GROUP BY g.sehir ORDER BY COUNT(g) DESC LIMIT 10")
    List<Object[]> countBySehirTop10();

    @Query("SELECT g.kullanimDurumu, COUNT(g) FROM GayrimenkulVarligi g GROUP BY g.kullanimDurumu ORDER BY COUNT(g) DESC")
    List<Object[]> countByKullanimDurumu();

    @Query("SELECT g.hizmetTuru, COUNT(g) FROM GayrimenkulVarligi g GROUP BY g.hizmetTuru ORDER BY COUNT(g) DESC")
    List<Object[]> countByHizmetTuru();

    @Query("SELECT g.sehir, g.hizmetTuru, COUNT(g) FROM GayrimenkulVarligi g " +
            "GROUP BY g.sehir, g.hizmetTuru ORDER BY g.sehir, COUNT(g) DESC")
    List<Object[]> countBySehirAndHizmetTuru();

    @Query("SELECT " +
            "CASE " +
            "  WHEN g.hizmetTuru IN ('MUDURLUK', 'SUBE', 'PIM', 'UPIM', 'UKIM', 'KIM', 'PDM', 'KDM', 'DTM') THEN 'Operasyonel' " +
            "  ELSE 'Destek' " +
            "END as birimTuru, COUNT(g) " +
            "FROM GayrimenkulVarligi g GROUP BY birimTuru")
    List<Object[]> countByBirimTuru();

    @Query("SELECT g.kullanimDurumu, g.hizmetTuru, COUNT(g) FROM GayrimenkulVarligi g " +
            "GROUP BY g.kullanimDurumu, g.hizmetTuru ORDER BY g.kullanimDurumu, COUNT(g) DESC")
    List<Object[]> countByKullanimDurumuAndHizmetTuru();

    // ==================== METREKARE HESAPLAMALARI ====================

    @Query("SELECT COALESCE(SUM(g.brutM2), 0.0) FROM GayrimenkulVarligi g WHERE g.brutM2 IS NOT NULL")
    Double toplamBrutMetrekare();

    @Query("SELECT g.kullanimDurumu, COALESCE(SUM(g.brutM2), 0.0) FROM GayrimenkulVarligi g " +
            "WHERE g.brutM2 IS NOT NULL GROUP BY g.kullanimDurumu")
    List<Object[]> metrekareByKullanimDurumu();

    @Query("SELECT g.hizmetTuru, COALESCE(SUM(g.brutM2), 0.0) FROM GayrimenkulVarligi g " +
            "WHERE g.brutM2 IS NOT NULL GROUP BY g.hizmetTuru")
    List<Object[]> metrekareByHizmetTuru();

    // ==================== DASHBOARD SORGULARI ====================

    @Query("SELECT g.isyeriAdi, g.sehir, g.olusturmaTarihi FROM GayrimenkulVarligi g " +
            "ORDER BY g.olusturmaTarihi DESC LIMIT 5")
    List<Object[]> findSonEklenenGayrimenkuller();

    @Query("SELECT g.isyeriAdi, g.enSonDegerlemeTarihi FROM GayrimenkulVarligi g " +
            "WHERE g.enSonDegerlemeTarihi < :tarih ORDER BY g.enSonDegerlemeTarihi ASC")
    List<Object[]> findDegerlemeGerekenler(@Param("tarih") LocalDate tarih);
}