package com.kgyp.kgypsystem.repository;

import com.kgyp.kgypsystem.entity.Bildirim;
import com.kgyp.kgypsystem.entity.Bildirim.BildirimTipi;
import com.kgyp.kgypsystem.entity.Bildirim.BildirimDurumu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface BildirimRepository extends JpaRepository<Bildirim, UUID> {

    // ==================== EKSİK METODLAR EKLENDİ ====================

    // Sözleşme ve bildirim tipine göre arama (Service'de kullanılan)
    List<Bildirim> findBySozlesmeIdAndBildirimTipi(UUID sozlesmeId, BildirimTipi bildirimTipi);

    // Gönderim bekleyen bildirimler (Service'de kullanılan)
    @Query("SELECT b FROM Bildirim b WHERE b.durum = 'BEKLEMEDE' ORDER BY b.oncelik DESC, b.olusturmaTarihi ASC")
    List<Bildirim> findGonderimBekleyenBildirimler();

    // ==================== TEMEL SORGULAR ====================

    List<Bildirim> findByAliciEmailOrderByOlusturmaTarihiDesc(String email);

    List<Bildirim> findByDurumOrderByOlusturmaTarihiDesc(BildirimDurumu durum);

    @Query("SELECT b FROM Bildirim b ORDER BY b.olusturmaTarihi DESC LIMIT 10")
    List<Bildirim> findTop10ByOrderByOlusturmaTarihiDesc();

    // ==================== GÖNDERİM DURUMU ====================

    @Query("SELECT b FROM Bildirim b WHERE b.durum = 'BEKLEMEDE' ORDER BY b.olusturmaTarihi ASC")
    List<Bildirim> findBekleyenBildirimler();

    @Query("SELECT COUNT(b) FROM Bildirim b WHERE b.durum = 'BEKLEMEDE'")
    Long countBekleyenBildirimler();

    List<Bildirim> findByDurum(BildirimDurumu durum);

    // ==================== PERFORMANS İZLEME ====================

    @Query("SELECT COUNT(b) FROM Bildirim b WHERE DATE(b.olusturmaTarihi) = CURRENT_DATE")
    Long countGunlukBildirimler();

    @Query("SELECT b.bildirimTipi, COUNT(b) FROM Bildirim b GROUP BY b.bildirimTipi ORDER BY COUNT(b) DESC")
    List<Object[]> countByBildirimTipi();

    @Query("SELECT b.durum, COUNT(b) FROM Bildirim b GROUP BY b.durum ORDER BY COUNT(b) DESC")
    List<Object[]> countByDurum();

    // ==================== TARİH BAZINDA SORGULAR ====================

    @Query("SELECT b FROM Bildirim b WHERE b.olusturmaTarihi BETWEEN :baslangic AND :bitis ORDER BY b.olusturmaTarihi DESC")
    List<Bildirim> findByOlusturmaTarihiBetween(@Param("baslangic") LocalDateTime baslangic,
                                                @Param("bitis") LocalDateTime bitis);

    // Son 24 saatteki bildirimler
    @Query("SELECT b FROM Bildirim b WHERE b.olusturmaTarihi >= :tarih ORDER BY b.olusturmaTarihi DESC")
    List<Bildirim> findSon24SaatBildirimleri(@Param("tarih") LocalDateTime tarih);

    // ==================== ÖZEL SORGULAR ====================

    // Gayrimenkul bazında bildirimler
    List<Bildirim> findByGayrimenkulId(UUID gayrimenkulId);

    // Sözleşme bazında bildirimler
    List<Bildirim> findBySozlesmeId(UUID sozlesmeId);

    // Belirli tip ve durumdaki bildirimler
    List<Bildirim> findByBildirimTipiAndDurum(BildirimTipi bildirimTipi, BildirimDurumu durum);

    // Öncelik seviyesine göre
    @Query("SELECT b FROM Bildirim b WHERE b.oncelik = :oncelik ORDER BY b.olusturmaTarihi DESC")
    List<Bildirim> findByOncelik(@Param("oncelik") String oncelik);

    // ==================== İSTATİSTİK METODLARI ====================

    @Query("SELECT COUNT(b) FROM Bildirim b WHERE b.gonderimTarihi IS NOT NULL")
    Long countGonderilenBildirimler();

    @Query("SELECT COUNT(b) FROM Bildirim b WHERE b.durum = 'HATALI'")
    Long countHataliBildirimler();

    // Başarı oranı hesaplama
    @Query("SELECT " +
            "(SELECT COUNT(b1) FROM Bildirim b1 WHERE b1.durum = 'GONDERILDI') * 100.0 / " +
            "(SELECT COUNT(b2) FROM Bildirim b2 WHERE b2.durum IN ('GONDERILDI', 'HATALI')) " +
            "FROM Bildirim b LIMIT 1")
    Double getBasariOrani();
}