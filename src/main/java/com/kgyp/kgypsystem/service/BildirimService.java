package com.kgyp.kgypsystem.service;

import com.kgyp.kgypsystem.entity.Bildirim;
import com.kgyp.kgypsystem.entity.Bildirim.BildirimDurumu;
import com.kgyp.kgypsystem.entity.Bildirim.BildirimTipi;
import com.kgyp.kgypsystem.entity.Bildirim.OncelikSeviyesi;
import com.kgyp.kgypsystem.repository.BildirimRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class BildirimService {

    private static final Logger logger = LoggerFactory.getLogger(BildirimService.class);

    @Autowired
    private BildirimRepository repository;

    @Autowired
    private EmailService emailService;

    /**
     * Yeni bildirim oluştur ve kaydet
     */
    public Bildirim bildirimOlustur(String baslik, String mesaj, BildirimTipi tip,
                                    String email, OncelikSeviyesi oncelik) {
        Bildirim bildirim = new Bildirim(baslik, mesaj, tip, email);
        bildirim.setOncelik(oncelik);

        Bildirim kaydedilen = repository.save(bildirim);
        logger.info("Bildirim oluşturuldu: {} - {}", tip, baslik);

        return kaydedilen;
    }

    /**
     * Sözleşme için bildirim oluştur
     */
    public Bildirim sozlesmeBildirimiOlustur(UUID sozlesmeId, String baslik, String mesaj,
                                             BildirimTipi tip, String email) {
        // Duplicate kontrolü
        List<Bildirim> mevcutlar = repository.findBySozlesmeIdAndBildirimTipi(sozlesmeId, tip);
        if (!mevcutlar.isEmpty()) {
            logger.warn("Sözleşme için aynı tipte bildirim zaten var: {}", sozlesmeId);
            return mevcutlar.get(0);
        }

        Bildirim bildirim = bildirimOlustur(baslik, mesaj, tip, email, OncelikSeviyesi.YUKSEK);
        bildirim.setSozlesmeId(sozlesmeId);

        return repository.save(bildirim);
    }

    /**
     * Gayrimenkul için bildirim oluştur
     */
    public Bildirim gayrimenkulBildirimiOlustur(UUID gayrimenkulId, String baslik, String mesaj,
                                                BildirimTipi tip, String email) {
        Bildirim bildirim = bildirimOlustur(baslik, mesaj, tip, email, OncelikSeviyesi.NORMAL);
        bildirim.setGayrimenkulId(gayrimenkulId);

        return repository.save(bildirim);
    }

    /**
     * Bekleyen bildirimleri email olarak gönder
     */
    public void bekleyenBildirimleriGonder() {
        List<Bildirim> bekleyenler = repository.findGonderimBekleyenBildirimler();

        logger.info("Gönderilecek bildirim sayısı: {}", bekleyenler.size());

        for (Bildirim bildirim : bekleyenler) {
            boolean basarili = emailService.sendEmail(
                    bildirim.getAliciEmail(),
                    bildirim.getBaslik(),
                    bildirim.getMesaj()
            );

            if (basarili) {
                bildirim.gonder();
                repository.save(bildirim);
                logger.info("Bildirim gönderildi: {}", bildirim.getBildirimId());
            } else {
                bildirim.setDurum(BildirimDurumu.HATALI);
                repository.save(bildirim);
                logger.error("Bildirim gönderilemedi: {}", bildirim.getBildirimId());
            }
        }
    }

    /**
     * Tüm bildirimleri listele
     */
    public List<Bildirim> tumBildirimleriListele() {
        return repository.findAll();
    }

    /**
     * Son bildirimleri getir
     */
    public List<Bildirim> sonBildirimler() {
        return repository.findTop10ByOrderByOlusturmaTarihiDesc();
    }

    /**
     * Email'e göre bildirimleri getir
     */
    public List<Bildirim> emaileBildirimler(String email) {
        return repository.findByAliciEmailOrderByOlusturmaTarihiDesc(email);
    }

    /**
     * Test bildirimi gönder
     */
    public Bildirim testBildirimiGonder(String email) {
        return bildirimOlustur(
                "Test Bildirimi",
                "Bu bir test bildirimidir. Sisteminiz çalışıyor!",
                BildirimTipi.SISTEM_BILDIRIMI,
                email,
                OncelikSeviyesi.DUSUK
        );
    }
}