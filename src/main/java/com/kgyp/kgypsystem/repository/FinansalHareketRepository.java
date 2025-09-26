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

    // Temel metodlar
    List<FinansalHareket> findByGayrimenkulVarligi_VarlikId(UUID varlikId);
    List<FinansalHareket> findByHareketTipi(HareketTipi hareketTipi);
    List<FinansalHareket> findByOnaylanmisFalse();

    // Tarih aralığında hareketler
    @Query("SELECT f FROM FinansalHareket f WHERE f.hareketTarihi BETWEEN :baslangic AND :bitis ORDER BY f.hareketTarihi DESC")
    List<FinansalHareket> findByTarihAraliginda(@Param("baslangic") LocalDateTime baslangic,
                                                @Param("bitis") LocalDateTime bitis);

    // Dönemsel kira gelirleri
    @Query("SELECT f FROM FinansalHareket f WHERE f.hareketTipi = 'KIRA_GELIRI' AND f.kiraDonemi = :donem")
    List<FinansalHareket> findKiraGeliriByDonem(@Param("donem") String donem);

    // Toplam gelir
    @Query("SELECT COALESCE(SUM(f.tutar), 0) FROM FinansalHareket f WHERE f.onaylanmis = true AND " +
            "f.hareketTipi IN ('KIRA_GELIRI', 'DEPOZITO_ALINDI', 'DIGER_GELIR')")
    BigDecimal toplamGelir();

    // Toplam gider
    @Query("SELECT COALESCE(SUM(f.tutar), 0) FROM FinansalHareket f WHERE f.onaylanmis = true AND " +
            "f.hareketTipi IN ('BAKIM_ONARIM', 'VERGI_HARCI', 'SIGORTA', 'YONETIM', 'AIDAT', 'DIGER_GIDER')")
    BigDecimal toplamGider();

    // Son hareketler - H2 uyumlu
    @Query(value = "SELECT * FROM finansal_hareketler ORDER BY hareket_tarihi DESC LIMIT 10", nativeQuery = true)
    List<FinansalHareket> findTop10ByOrderByHareketTarihiDesc();

    // Aylık finansal hesaplamalar - H2 uyumlu
    @Query("SELECT COALESCE(SUM(f.tutar), 0) FROM FinansalHareket f WHERE " +
            "EXTRACT(YEAR FROM f.hareketTarihi) = :yil AND EXTRACT(MONTH FROM f.hareketTarihi) = :ay AND " +
            "f.hareketTipi IN ('KIRA_GELIRI', 'DEPOZITO_ALINDI', 'DIGER_GELIR') AND f.onaylanmis = true")
    BigDecimal getAylikToplamGelir(@Param("yil") int yil, @Param("ay") int ay);

    @Query("SELECT COALESCE(SUM(f.tutar), 0) FROM FinansalHareket f WHERE " +
            "EXTRACT(YEAR FROM f.hareketTarihi) = :yil AND EXTRACT(MONTH FROM f.hareketTarihi) = :ay AND " +
            "f.hareketTipi IN ('BAKIM_ONARIM', 'VERGI_HARCI', 'SIGORTA', 'YONETIM', 'AIDAT', 'DIGER_GIDER') AND f.onaylanmis = true")
    BigDecimal getAylikToplamGider(@Param("yil") int yil, @Param("ay") int ay);

    // Raporlama metodları - H2 uyumlu
    @Query("SELECT EXTRACT(YEAR FROM f.hareketTarihi), EXTRACT(MONTH FROM f.hareketTarihi), SUM(f.tutar) " +
            "FROM FinansalHareket f WHERE f.hareketTipi = 'KIRA_GELIRI' AND f.onaylanmis = true " +
            "GROUP BY EXTRACT(YEAR FROM f.hareketTarihi), EXTRACT(MONTH FROM f.hareketTarihi) " +
            "ORDER BY EXTRACT(YEAR FROM f.hareketTarihi) DESC, EXTRACT(MONTH FROM f.hareketTarihi) DESC")
    List<Object[]> aylikKiraGeliriRaporu();

    @Query("SELECT f.hareketTipi, SUM(f.tutar) FROM FinansalHareket f WHERE f.onaylanmis = true " +
            "GROUP BY f.hareketTipi ORDER BY SUM(f.tutar) DESC")
    List<Object[]> hareketTipiBazindaToplam();
}