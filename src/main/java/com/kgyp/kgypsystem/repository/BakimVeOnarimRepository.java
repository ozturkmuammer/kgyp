package com.kgyp.kgypsystem.repository;

import com.kgyp.kgypsystem.entity.BakimVeOnarim;
import com.kgyp.kgypsystem.entity.BakimVeOnarim.BakimDurumu;
import com.kgyp.kgypsystem.entity.BakimVeOnarim.BakimKategorisi;
import com.kgyp.kgypsystem.entity.BakimVeOnarim.OncelikSeviyesi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface BakimVeOnarimRepository extends JpaRepository<BakimVeOnarim, UUID> {

    // Aktif bakım işlemleri
    @Query("SELECT b FROM BakimVeOnarim b WHERE b.durum IN ('PLANLANMIS', 'BASLANDI', 'DEVAM_EDIYOR', 'BEKLEMEDE') ORDER BY b.oncelik DESC, b.olusturmaTarihi ASC")
    List<BakimVeOnarim> findAktifBakimlar();

    // Kritik bakım işleri
    @Query("SELECT b FROM BakimVeOnarim b WHERE b.oncelik = 'KRITIK' AND b.durum != 'TAMAMLANDI' AND b.durum != 'IPTAL_EDILDI'")
    List<BakimVeOnarim> findKritikBakimlar();

    // Geciken bakım işleri
    @Query("SELECT b FROM BakimVeOnarim b WHERE b.planlananBitisTarihi < :tarih AND b.durum != 'TAMAMLANDI' AND b.durum != 'IPTAL_EDILDI'")
    List<BakimVeOnarim> findGecikenBakimlar(@Param("tarih") LocalDateTime tarih);

    // Bu hafta başlayacak bakımlar
    @Query("SELECT b FROM BakimVeOnarim b WHERE b.baslangicTarihi BETWEEN :baslangic AND :bitis AND b.durum = 'PLANLANMIS'")
    List<BakimVeOnarim> findBuHaftaBaslayacakBakimlar(@Param("baslangic") LocalDateTime baslangic, @Param("bitis") LocalDateTime bitis);

    // Tarih aralığında tamamlanan bakımlar
    @Query("SELECT b FROM BakimVeOnarim b WHERE b.gercekBitisTarihi BETWEEN :baslangic AND :bitis AND b.durum = 'TAMAMLANDI'")
    List<BakimVeOnarim> findTamamlananBakimlarByTarihAraligi(@Param("baslangic") LocalDateTime baslangic, @Param("bitis") LocalDateTime bitis);

    // Garantide olan bakımlar
    @Query("SELECT b FROM BakimVeOnarim b WHERE b.garantiBitisTarihi > :tarih AND b.durum = 'TAMAMLANDI'")
    List<BakimVeOnarim> findGarantideOlanBakimlar(@Param("tarih") LocalDateTime tarih);

    // Temel sorgular
    List<BakimVeOnarim> findByDurum(BakimDurumu durum);
    List<BakimVeOnarim> findByKategori(BakimKategorisi kategori);
    List<BakimVeOnarim> findByOncelik(OncelikSeviyesi oncelik);
    List<BakimVeOnarim> findByGayrimenkulVarligi_VarlikId(UUID varlikId);

    // Arama metodları
    List<BakimVeOnarim> findBySorumluPersonelContainingIgnoreCase(String sorumluPersonel);
    List<BakimVeOnarim> findByTedarikciFirmaContainingIgnoreCase(String tedarikciFirma);

    @Query("SELECT b FROM BakimVeOnarim b WHERE LOWER(b.baslik) LIKE LOWER(CONCAT('%', :arama, '%')) OR LOWER(b.aciklama) LIKE LOWER(CONCAT('%', :arama, '%'))")
    List<BakimVeOnarim> findByBaslikOrAciklamaContaining(@Param("arama") String aramaKelimesi);

    // Son eklenen bakımlar - H2 uyumlu
    @Query(value = "SELECT * FROM bakim_ve_onarim ORDER BY olusturma_tarihi DESC LIMIT 10", nativeQuery = true)
    List<BakimVeOnarim> findTop10ByOrderByOlusturmaTarihiDesc();

    // İstatistik metodları
    @Query("SELECT COALESCE(SUM(b.gercekMaliyet), 0) FROM BakimVeOnarim b WHERE b.durum = 'TAMAMLANDI' AND b.gercekMaliyet IS NOT NULL")
    BigDecimal toplamBakimMaliyeti();

    @Query("SELECT b.kategori, COUNT(b) FROM BakimVeOnarim b GROUP BY b.kategori ORDER BY COUNT(b) DESC")
    List<Object[]> kategoriBazindaBakimSayisi();

    @Query("SELECT b.durum, COUNT(b) FROM BakimVeOnarim b GROUP BY b.durum ORDER BY COUNT(b) DESC")
    List<Object[]> durumBazindaBakimSayisi();

    // Dashboard için son bakım işlemleri
    @Query(value = "SELECT g.isyeri_adi, b.olusturma_tarihi FROM bakim_ve_onarim b " +
            "JOIN gayrimenkul_varliklari g ON b.varlik_id = g.varlik_id " +
            "ORDER BY b.olusturma_tarihi DESC LIMIT 5", nativeQuery = true)
    List<Object[]> findSonBakimIslemler();

    // Dashboard için durum bazında sayım
    @Query("SELECT COUNT(b) FROM BakimVeOnarim b WHERE b.durum = :durum")
    Long countByDurum(@Param("durum") String durum);

    // Aylık tamamlanan bakım sayısı - H2 uyumlu
    @Query("SELECT COUNT(b) FROM BakimVeOnarim b WHERE " +
            "EXTRACT(YEAR FROM b.gercekBitisTarihi) = :yil AND EXTRACT(MONTH FROM b.gercekBitisTarihi) = :ay AND b.durum = 'TAMAMLANDI'")
    Long countByAylikTamamlanan(@Param("yil") int yil, @Param("ay") int ay);

    // Performans metrikleri - H2 uyumlu
    @Query("SELECT AVG(CAST(b.gercekBitisTarihi - b.baslangicTarihi AS DOUBLE)) FROM BakimVeOnarim b " +
            "WHERE b.durum = 'TAMAMLANDI' AND b.gercekBitisTarihi IS NOT NULL AND b.baslangicTarihi IS NOT NULL")
    Double getOrtalamaBakimSuresi();

    @Query("SELECT COUNT(b) * 100.0 / (SELECT COUNT(b2) FROM BakimVeOnarim b2 WHERE b2.durum = 'TAMAMLANDI') " +
            "FROM BakimVeOnarim b WHERE b.durum = 'TAMAMLANDI' AND b.gercekBitisTarihi <= b.planlananBitisTarihi")
    Double getZamanindaTamamlananOran();
}