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

    // ==================== EKSİK METODLAR EKLENDİ ====================

    // Aktif bakım işlemleri (Service'de kullanılan)
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

    // ==================== MEVCUT METODLAR ====================

    // Durum bazında sorgular
    List<BakimVeOnarim> findByDurum(BakimDurumu durum);
    List<BakimVeOnarim> findByKategori(BakimKategorisi kategori);
    List<BakimVeOnarim> findByOncelik(OncelikSeviyesi oncelik);
    List<BakimVeOnarim> findByGayrimenkulVarligi_VarlikId(UUID varlikId);

    // Arama metodları
    List<BakimVeOnarim> findBySorumluPersonelContainingIgnoreCase(String sorumluPersonel);
    List<BakimVeOnarim> findByTedarikciFirmaContainingIgnoreCase(String tedarikciFirma);

    @Query("SELECT b FROM BakimVeOnarim b WHERE LOWER(b.baslik) LIKE LOWER(CONCAT('%', :arama, '%')) OR LOWER(b.aciklama) LIKE LOWER(CONCAT('%', :arama, '%'))")
    List<BakimVeOnarim> findByBaslikOrAciklamaContaining(@Param("arama") String aramaKelimesi);

    // Son eklenen bakımlar
    @Query("SELECT b FROM BakimVeOnarim b ORDER BY b.olusturmaTarihi DESC LIMIT 10")
    List<BakimVeOnarim> findTop10ByOrderByOlusturmaTarihiDesc();

    // ==================== İSTATİSTİK METODLARI ====================

    @Query("SELECT COALESCE(SUM(b.gercekMaliyet), 0) FROM BakimVeOnarim b WHERE b.durum = 'TAMAMLANDI' AND b.gercekMaliyet IS NOT NULL")
    BigDecimal toplamBakimMaliyeti();

    @Query("SELECT b.kategori, COUNT(b) FROM BakimVeOnarim b GROUP BY b.kategori ORDER BY COUNT(b) DESC")
    List<Object[]> kategoriBazindaBakimSayisi();

    @Query("SELECT b.durum, COUNT(b) FROM BakimVeOnarim b GROUP BY b.durum ORDER BY COUNT(b) DESC")
    List<Object[]> durumBazindaBakimSayisi();

    // Dashboard için son bakım işlemleri
    @Query("SELECT gv.isyeriAdi, b.olusturmaTarihi FROM BakimVeOnarim b " +
            "JOIN b.gayrimenkulVarligi gv ORDER BY b.olusturmaTarihi DESC LIMIT 5")
    List<Object[]> findSonBakimIslemler();

    // Dashboard için durum bazında sayım
    @Query("SELECT COUNT(b) FROM BakimVeOnarim b WHERE b.durum = :durum")
    Long countByDurum(@Param("durum") String durum);

    // Aylık tamamlanan bakım sayısı
    @Query("SELECT COUNT(b) FROM BakimVeOnarim b WHERE " +
            "YEAR(b.gercekBitisTarihi) = :yil AND MONTH(b.gercekBitisTarihi) = :ay AND b.durum = 'TAMAMLANDI'")
    Long countByAylikTamamlanan(@Param("yil") int yil, @Param("ay") int ay);

    // Performans metrikleri
    @Query("SELECT AVG(DATEDIFF(day, b.baslangicTarihi, b.gercekBitisTarihi)) FROM BakimVeOnarim b " +
            "WHERE b.durum = 'TAMAMLANDI' AND b.gercekBitisTarihi IS NOT NULL AND b.baslangicTarihi IS NOT NULL")
    Double getOrtalamaBakimSuresi();

    @Query("SELECT COUNT(b) * 100.0 / (SELECT COUNT(b2) FROM BakimVeOnarim b2 WHERE b2.durum = 'TAMAMLANDI') " +
            "FROM BakimVeOnarim b WHERE b.durum = 'TAMAMLANDI' AND b.gercekBitisTarihi <= b.planlananBitisTarihi")
    Double getZamanindaTamamlananOran();
}