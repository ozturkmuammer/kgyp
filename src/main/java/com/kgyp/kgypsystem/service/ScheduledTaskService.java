package com.kgyp.kgypsystem.service;

import com.kgyp.kgypsystem.entity.Sozlesme;
import com.kgyp.kgypsystem.entity.Bildirim.BildirimTipi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

@Service
public class ScheduledTaskService {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTaskService.class);

    @Autowired
    private SozlesmeService sozlesmeService;

    @Autowired
    private BildirimService bildirimService;

    @Value("${kgyp.admin.email:admin@kgyp.com}")
    private String adminEmail;

    /**
     * Her gün saat 09:00'da çalışır - Sözleşme bitiş kontrolleri
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void sozlesmeBitisKontrolleri() {
        logger.info("Sözleşme bitiş kontrolleri başlatıldı...");

        try {
            // 30 gün içinde bitecek sözleşmeler
            List<Sozlesme> yakindaBitenler = sozlesmeService.yakindaSuresiDolacakSozlesmeler();

            for (Sozlesme sozlesme : yakindaBitenler) {
                long kalanGun = java.time.temporal.ChronoUnit.DAYS.between(
                        LocalDate.now(),
                        sozlesme.getSozlesmeBitisTarihi()
                );

                String mesaj = String.format(
                        "Dikkat! %s adlı kiracının sözleşmesi %d gün sonra (%s) bitecek.\n\n" +
                                "Gayrimenkul: %s\n" +
                                "Kiracı: %s\n" +
                                "Bitiş Tarihi: %s\n\n" +
                                "Lütfen yenileme işlemlerini başlatın.",
                        sozlesme.getKiraciAdi(),
                        kalanGun,
                        sozlesme.getSozlesmeBitisTarihi(),
                        sozlesme.getGayrimenkulVarligi().getAdres(),
                        sozlesme.getKiraciAdi(),
                        sozlesme.getSozlesmeBitisTarihi()
                );

                bildirimService.sozlesmeBildirimiOlustur(
                        sozlesme.getSozlesmeId(),
                        "Sözleşme Süresi Bitiyor - " + sozlesme.getKiraciAdi(),
                        mesaj,
                        BildirimTipi.SOZLESME_BITIYOR,
                        adminEmail
                );
            }

            logger.info("Sözleşme bitiş kontrolleri tamamlandı. {} sözleşme için uyarı oluşturuldu.", yakindaBitenler.size());

        } catch (Exception e) {
            logger.error("Sözleşme bitiş kontrolleri hatası:", e);
        }
    }

    /**
     * Her gün saat 10:00'da çalışır - Kira artış kontrolleri
     */
    @Scheduled(cron = "0 0 10 * * ?")
    public void kiraArtisKontrolleri() {
        logger.info("Kira artış kontrolleri başlatıldı...");

        try {
            // Kira artışı yapılmamış sözleşmeler
            List<Sozlesme> artisGerekenler = sozlesmeService.kiraArtisiYapilmamisSozlesmeler();

            for (Sozlesme sozlesme : artisGerekenler) {
                String mesaj = String.format(
                        "UYARI! %s için 2025 yılı kira artışı henüz yapılmamış.\n\n" +
                                "Gayrimenkul: %s\n" +
                                "Kiracı: %s\n" +
                                "Mevcut Kira: %s TL\n" +
                                "Artış Metodu: %s\n\n" +
                                "Lütfen kira artışını sisteme girin.",
                        sozlesme.getKiraciAdi(),
                        sozlesme.getGayrimenkulVarligi().getAdres(),
                        sozlesme.getKiraciAdi(),
                        sozlesme.getAylikKiraTutari(),
                        sozlesme.getKiraArtisMetodu().getAciklama()
                );

                bildirimService.sozlesmeBildirimiOlustur(
                        sozlesme.getSozlesmeId(),
                        "Kira Artışı Gerekli - " + sozlesme.getKiraciAdi(),
                        mesaj,
                        BildirimTipi.KIRA_ARTISI_GEREKLI,
                        adminEmail
                );
            }

            logger.info("Kira artış kontrolleri tamamlandı. {} sözleşme için uyarı oluşturuldu.", artisGerekenler.size());

        } catch (Exception e) {
            logger.error("Kira artış kontrolleri hatası:", e);
        }
    }

    /**
     * Her 15 dakikada bir çalışır - Bekleyen bildirimleri gönder
     */
    @Scheduled(fixedRate = 900000) // 15 dakika = 900,000 ms
    public void bekleyenBildirimleriGonder() {
        logger.debug("Bekleyen bildirimler kontrol ediliyor...");

        try {
            bildirimService.bekleyenBildirimleriGonder();
        } catch (Exception e) {
            logger.error("Bildirim gönderim hatası:", e);
        }
    }
}