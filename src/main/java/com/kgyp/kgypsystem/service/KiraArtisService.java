package com.kgyp.kgypsystem.service;

import com.kgyp.kgypsystem.entity.Sozlesme;
import com.kgyp.kgypsystem.entity.KiraArtisMetodu;
import com.kgyp.kgypsystem.repository.SozlesmeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class KiraArtisService {

    @Autowired
    private SozlesmeRepository sozlesmeRepository;

    @Autowired
    private TufeApiService tufeApiService;

    @Autowired
    private BildirimService bildirimService;

    public void yillikKiraArtislariniHesapla(int yil) {
        List<Sozlesme> aktifSozlesmeler = sozlesmeRepository.findByAktifMiTrue();

        for (Sozlesme sozlesme : aktifSozlesmeler) {
            if (sozlesme.getKiraArtisMetodu() == KiraArtisMetodu.TUFE) {
                // TÜFE bazlı artış hesapla
                BigDecimal yeniKira = tufeApiService.hesaplaKiraArtisi(
                        sozlesme.getAylikKiraTutari(), yil);

                // Artış tutarını kaydet
                BigDecimal artisTutari = yeniKira.subtract(sozlesme.getAylikKiraTutari());

                // Sözleşmeyi güncelle
                sozlesme.setAylikKiraTutari(yeniKira);
                sozlesme.setKiraArtisiYapildi2025(true);

                sozlesmeRepository.save(sozlesme);

                // Bildirim gönder (bildirim service'de method eklenecek)
                // bildirimService.kiraArtisiBildirimiGonder(sozlesme, artisTutari);

            } else if (sozlesme.getKiraArtisMetodu() == KiraArtisMetodu.SABIT) {
                // Sabit oran artışı
                BigDecimal artisOrani = sozlesme.getSabitArtisOrani() != null ?
                        sozlesme.getSabitArtisOrani() : new BigDecimal("10.00");

                BigDecimal artisKatsayisi = artisOrani.divide(new BigDecimal("100"))
                        .add(BigDecimal.ONE);
                BigDecimal yeniKira = sozlesme.getAylikKiraTutari().multiply(artisKatsayisi);

                sozlesme.setAylikKiraTutari(yeniKira);
                sozlesme.setKiraArtisiYapildi2025(true);

                sozlesmeRepository.save(sozlesme);
            }
        }
    }

    public List<Sozlesme> getKiraArtisiYapilmayanSozlesmeler() {
        return sozlesmeRepository.findByKiraArtisiYapildi2025FalseAndAktifMiTrue();
    }
}