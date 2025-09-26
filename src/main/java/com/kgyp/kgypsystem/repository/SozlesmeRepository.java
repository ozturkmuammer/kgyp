package com.kgyp.kgypsystem.repository;

import com.kgyp.kgypsystem.entity.Sozlesme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface SozlesmeRepository extends JpaRepository<Sozlesme, UUID> {

    // ==================== TEMEL METODLAR ====================

    List<Sozlesme> findByAktifMiTrue();
    long countByAktifMiTrue();

    @Query("SELECT s FROM Sozlesme s WHERE s.sozlesmeBitisTarihi <= :tarih AND s.aktifMi = true ORDER BY s.sozlesmeBitisTarihi ASC")
    List<Sozlesme> findYakindaBitecekSozlesmeler(@Param("tarih") LocalDate tarih);

    List<Sozlesme> findByKiraArtisiYapildi2025FalseAndAktifMiTrue();
    List<Sozlesme> findByKiraOdemeGunu(Integer gun);

    @Query("SELECT COALESCE(SUM(s.aylikKiraTutari), 0.0) FROM Sozlesme s WHERE s.aktifMi = true")
    Double toplamAylikKiraGeliri();

    // ==================== DASHBOARD METODLARI - EKSİK OLANLAR ====================

    @Query("SELECT COUNT(s) FROM Sozlesme s WHERE " +
            "CASE WHEN :durum = 'AKTIF' THEN s.aktifMi = true " +
            "     WHEN :durum = 'PASIF' THEN s.aktifMi = false " +
            "     ELSE s.aktifMi = s.aktifMi END")
    Long countBySozlesmeDurumu(@Param("durum") String durum);

    @Query("SELECT gv.isyeriAdi, s.guncellemeTarihi FROM Sozlesme s " +
            "JOIN s.gayrimenkulVarligi gv ORDER BY s.guncellemeTarihi DESC LIMIT 5")
    List<Object[]> findSonAktiviteler();

    // ==================== BİLDİRİM SİSTEMİ SORGULARI ====================

    @Query("SELECT gv.isyeriAdi, s.sozlesmeBitisTarihi FROM Sozlesme s " +
            "JOIN s.gayrimenkulVarligi gv WHERE s.sozlesmeBitisTarihi BETWEEN :baslangic AND :bitis " +
            "AND s.aktifMi = true ORDER BY s.sozlesmeBitisTarihi ASC")
    List<Object[]> findYaklaşanSozlesmeBitisleri(@Param("baslangic") LocalDate baslangic,
                                                 @Param("bitis") LocalDate bitis);

    @Query("SELECT gv.isyeriAdi FROM Sozlesme s JOIN s.gayrimenkulVarligi gv " +
            "WHERE s.kiraArtisiYapildi2025 = false AND s.aktifMi = true")
    List<Object[]> findKiraArtisiYapilmayanlar();

    // ==================== PERFORMANS METRİKLERİ ====================

    @Query("SELECT COUNT(s) * 100.0 / (SELECT COUNT(s2) FROM Sozlesme s2) FROM Sozlesme s " +
            "WHERE s.aktifMi = true")
    Double getSozlesmeYenilemeOrani();

    // ==================== FİNANSAL HESAPLAMALAR ====================

    @Query("SELECT gv.sehir, SUM(s.aylikKiraTutari) FROM Sozlesme s " +
            "JOIN s.gayrimenkulVarligi gv WHERE s.aktifMi = true " +
            "GROUP BY gv.sehir ORDER BY SUM(s.aylikKiraTutari) DESC")
    List<Object[]> getKiraGeliriBySehir();
}