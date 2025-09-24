package com.kgyp.kgypsystem.repository;

import com.kgyp.kgypsystem.entity.FinansalHareket;
import com.kgyp.kgypsystem.entity.FinansalHareket.HareketTipi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface FinansalHareketRepository extends JpaRepository<FinansalHareket, UUID> {

    // ==================== EKSİK METODLAR EKLENDİ ====================

    // Gayrimenkul bazında hareketler
    List<FinansalHareket> findByGayrimenkulVarligi_VarlikId(UUID varlikId);

    // Hareket tipine göre
    List<FinansalHareket> findByHareketTipi(HareketTipi hareketTipi);

    // Onay bekleyen hareketler
    List<FinansalHareket> findByOnaylanmisFalse();

    // Tarih aralığında hareketler
    @Query("SELECT f FROM FinansalHareket f WHERE f.hareketTarihi BETWEEN :baslangic AND :bitis ORDER BY f.hareketTarihi DESC")
    List<FinansalHareket> findByTarihAraliginda(@Param("baslangic") LocalDateTime baslangic,
                                                @Param("bitis") LocalDateTime bitis);

    // Dönemsel kira gelirleri
    @Query("SELECT f FROM FinansalHareket f WHERE f.hareketTipi = 'KIRA_GELIRI' AND f.kiraDonemi = :donem")
    List<FinansalHareket> findKiraGeliriByDonem(@Param("donem") String donem);

    // Toplam gelir
    @Query("SELECT COALESCE(SUM(f.tutar), 0) FROM FinansalHareket f WHERE f.onaylanmis = true AND f.hareketTipi IN ('KIRA_GELIRI', 'DEPOZITO_ALINDI', 'DIGER_GELIR')")
    BigDecimal toplamGelir();

    // Toplam gider
    @Query("SELECT COALESCE(SUM(f.tutar), 0) FROM FinansalHareket f WHERE f.onaylanmis = true AND f.hareketTipi IN ('BAKIM_ONARIM', 'VERGI_HARCI', 'SIGORTA', 'YONETIM', 'AIDAT', 'DIGER_GIDER')")
    BigDecimal toplamGider();

    // Son hareketler
    @Query("SELECT f FROM FinansalHareket f ORDER BY f.hareketTarihi DESC LIMIT 10")
    List<FinansalHareket> findTop10ByOrderByHareketTarihiDesc();

    // ==================== RAPORLAMA METODLARI ====================

    // Aylık kira geliri raporu
    @Query("SELECT YEAR(f.hareketTarihi), MONTH(f.hareketTarihi), SUM(f.tutar) " +
            "FROM FinansalHareket f WHERE f.hareketTipi = 'KIRA_GELIRI' AND f.onaylanmis = true " +
            "GROUP BY YEAR(f.hareketTarihi), MONTH(f.hareketTarihi) " +
            "ORDER BY YEAR(f.hareketTarihi) DESC, MONTH(f.hareketTarihi) DESC")
    List<Object[]> aylikKiraGeliriRaporu();

    // Hareket tipi bazında toplam
    @Query("SELECT f.hareketTipi, SUM(f.tutar) FROM FinansalHareket f WHERE f.onaylanmis = true " +
            "GROUP BY f.hareketTipi ORDER BY SUM(f.tutar) DESC")
    List<Object[]> hareketTipiBazindaToplam();

    // ==================== AYLIK FİNANSAL HESAPLAMALAR ====================

    @Query("SELECT COALESCE(SUM(f.tutar), 0) FROM FinansalHareket f WHERE " +
            "YEAR(f.hareketTarihi) = :yil AND MONTH(f.hareketTarihi) = :ay AND " +
            "f.hareketTipi IN ('KIRA_GELIRI', 'DEPOZITO_ALINDI', 'DIGER_GELIR') AND f.onaylanmis = true")
    BigDecimal getAylikToplamGelir(@Param("yil") int yil, @Param("ay") int ay);

    @Query("SELECT COALESCE(SUM(f.tutar), 0) FROM FinansalHareket f WHERE " +
            "YEAR(f.hareketTarihi) = :yil AND MONTH(f.hareketTarihi) = :ay AND " +
            "f.hareketTipi IN ('BAKIM_ONARIM', 'VERGI_HARCI', 'SIGORTA', 'YONETIM', 'AIDAT', 'DIGER_GIDER') AND f.onaylanmis = true")
    BigDecimal getAylikToplamGider(@Param("yil") int yil, @Param("ay") int ay);

    // ==================== TRENDler ve ANALİZ ====================

    @Query("SELECT MONTH(f.hareketTarihi) as ay, SUM(f.tutar) FROM FinansalHareket f WHERE " +
            "YEAR(f.hareketTarihi) = :yil AND f.hareketTipi = 'KIRA_GELIRI' AND f.onaylanmis = true " +
            "GROUP BY MONTH(f.hareketTarihi) ORDER BY ay")
    List<Object[]> getAylikGelirTrendi(@Param("yil") int yil);

    @Query("SELECT MONTH(f.hareketTarihi) as ay, SUM(f.tutar) FROM FinansalHareket f WHERE " +
            "YEAR(f.hareketTarihi) = :yil AND f.hareketTipi IN ('BAKIM_ONARIM', 'VERGI_HARCI', 'SIGORTA', 'YONETIM', 'AIDAT', 'DIGER_GIDER') AND f.onaylanmis = true " +
            "GROUP BY MONTH(f.hareketTarihi) ORDER BY ay")
    List<Object[]> getAylikGiderTrendi(@Param("yil") int yil);

    // ==================== HAREKET TÜRÜ BAZINDA ANALİZ ====================

    @Query("SELECT f.hareketTipi, SUM(f.tutar) FROM FinansalHareket f WHERE " +
            "YEAR(f.hareketTarihi) = :yil AND f.onaylanmis = true GROUP BY f.hareketTipi ORDER BY SUM(f.tutar) DESC")
    List<Object[]> getHareketTuruBazindaOzet(@Param("yil") int yil);

    @Query("SELECT gv.sehir, SUM(f.tutar) FROM FinansalHareket f " +
            "JOIN f.gayrimenkulVarligi gv WHERE f.hareketTipi = 'KIRA_GELIRI' AND f.onaylanmis = true " +
            "GROUP BY gv.sehir ORDER BY SUM(f.tutar) DESC")
    List<Object[]> getKiraGeliriBySehir();

    // ==================== SON AKTİVİTELER ====================

    @Query("SELECT gv.isyeriAdi, f.hareketTipi, f.tutar, f.hareketTarihi FROM FinansalHareket f " +
            "JOIN f.gayrimenkulVarligi gv ORDER BY f.hareketTarihi DESC LIMIT 10")
    List<Object[]> findSonFinansalHareketler();

    // ==================== PERFORMANS METRİKLERİ ====================

    @Query("SELECT AVG(f.tutar) FROM FinansalHareket f WHERE f.hareketTipi = 'KIRA_GELIRI' AND f.onaylanmis = true")
    BigDecimal getOrtalamaKiraGeliri();

    @Query("SELECT COUNT(DISTINCT gv.varlikId) FROM FinansalHareket f " +
            "JOIN f.gayrimenkulVarligi gv WHERE f.hareketTipi = 'KIRA_GELIRI' AND f.onaylanmis = true")
    Long getGelirGetirenGayrimenkulSayisi();
}