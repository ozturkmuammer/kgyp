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

    // ==================== TEMEL METODLAR ====================

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

    // ==================== DURUM SORGULARI ====================

    @Query("SELECT b FROM Bildirim b WHERE b.durum = 'BEKLEMEDE' ORDER BY b.olusturmaTarihi ASC")
    List<Bildirim> findBekleyenBildirimler();

    @Query("SELECT COUNT(b) FROM Bildirim b WHERE b.durum = 'BEKLEMEDE'")
    Long countBekleyenBildirimler();

    List<Bildirim> findByDurum(BildirimDurumu durum);

    // ==================== BASIT İSTATİSTİKLER ====================

    @Query("SELECT b.bildirimTipi, COUNT(b) FROM Bildirim b GROUP BY b.bildirimTipi ORDER BY COUNT(b) DESC")
    List<Object[]> countByBildirimTipi();

    @Query("SELECT b.durum, COUNT(b) FROM Bildirim b GROUP BY b.durum ORDER BY COUNT(b) DESC")
    List<Object[]> countByDurum();

    // ==================== TARİH BAZINDA SORGULAR ====================

    @Query("SELECT b FROM Bildirim b WHERE b.olusturmaTarihi BETWEEN :baslangic AND :bitis ORDER BY b.olusturmaTarihi DESC")
    List<Bildirim> findByOlusturmaTarihiBetween(@Param("baslangic") LocalDateTime baslangic,
                                                @Param("bitis") LocalDateTime bitis);

    @Query("SELECT b FROM Bildirim b WHERE b.olusturmaTarihi >= :tarih ORDER BY b.olusturmaTarihi DESC")
    List<Bildirim> findSon24SaatBildirimleri(@Param("tarih") LocalDateTime tarih);

    // ==================== ÖZEL SORGULAR ====================

    List<Bildirim> findByGayrimenkulId(UUID gayrimenkulId);
    List<Bildirim> findBySozlesmeId(UUID sozlesmeId);
    List<Bildirim> findByBildirimTipiAndDurum(BildirimTipi bildirimTipi, BildirimDurumu durum);

    @Query("SELECT b FROM Bildirim b WHERE b.oncelik = :oncelik ORDER BY b.olusturmaTarihi DESC")
    List<Bildirim> findByOncelik(@Param("oncelik") String oncelik);

    // ==================== BASIT SAYIM İŞLEMLERİ ====================

    @Query("SELECT COUNT(b) FROM Bildirim b WHERE b.gonderimTarihi IS NOT NULL")
    Long countGonderilenBildirimler();

    @Query("SELECT COUNT(b) FROM Bildirim b WHERE b.durum = 'HATALI'")
    Long countHataliBildirimler();

    // ✅ NOTLAR: Karmaşık query'ler kaldırıldı
    // - countGunlukBildirimler() -> H2 DATE() uyumsuzluğu
    // - getBasariOrani() -> Karmaşık subquery problemi
    //
    // Bu methodlar daha sonra service layer'da Java kodu ile implement edilebilir
    // veya PostgreSQL'e geçildiğinde tekrar eklenebilir
}