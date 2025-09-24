package com.kgyp.kgypsystem.service;

import com.kgyp.kgypsystem.entity.Sozlesme;
import com.kgyp.kgypsystem.entity.KiraArtisMetodu;
import com.kgyp.kgypsystem.entity.GayrimenkulVarligi;
import com.kgyp.kgypsystem.repository.SozlesmeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class SozlesmeService {

    @Autowired
    private SozlesmeRepository repository;

    @Autowired
    private GayrimenkulVarligiService gayrimenkulService;

    // ==================== TEMEL CRUD İŞLEMLERİ ====================

    public List<Sozlesme> tumSozlesmeleriListele() {
        return repository.findAll();
    }

    public List<Sozlesme> aktifSozlesmeleriListele() {
        return repository.findByAktifMiTrue();
    }

    // ✅ EKSİK METOD EKLENDİ - Controller'da kullanılıyor
    public Optional<Sozlesme> sozlesmeBul(UUID id) {
        return repository.findById(id);
    }

    public Sozlesme sozlesmeKaydet(UUID varlikId, Sozlesme sozlesme) {
        if (sozlesme.getKiraciAdi() == null || sozlesme.getKiraciAdi().trim().isEmpty()) {
            throw new IllegalArgumentException("Kiraci adi bos olamaz");
        }
        if (sozlesme.getAylikKiraTutari() == null || sozlesme.getAylikKiraTutari().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Kira tutari gecerli olmalidir");
        }

        Optional<GayrimenkulVarligi> gayrimenkul = gayrimenkulService.gayrimenkulBul(varlikId);
        if (!gayrimenkul.isPresent()) {
            throw new IllegalArgumentException("Gayrimenkul bulunamadi");
        }

        sozlesme.setGayrimenkulVarligi(gayrimenkul.get());
        return repository.save(sozlesme);
    }

    public Sozlesme sozlesmeGuncelle(UUID id, Sozlesme guncelSozlesme) {
        Optional<Sozlesme> mevcut = repository.findById(id);
        if (mevcut.isPresent()) {
            Sozlesme sozlesme = mevcut.get();

            if (guncelSozlesme.getKiraciAdi() != null) {
                sozlesme.setKiraciAdi(guncelSozlesme.getKiraciAdi());
            }
            if (guncelSozlesme.getAylikKiraTutari() != null) {
                sozlesme.setAylikKiraTutari(guncelSozlesme.getAylikKiraTutari());
            }
            if (guncelSozlesme.getSozlesmeBitisTarihi() != null) {
                sozlesme.setSozlesmeBitisTarihi(guncelSozlesme.getSozlesmeBitisTarihi());
            }
            if (guncelSozlesme.getKiraArtisMetodu() != null) {
                sozlesme.setKiraArtisMetodu(guncelSozlesme.getKiraArtisMetodu());
            }

            return repository.save(sozlesme);
        } else {
            throw new RuntimeException("Sozlesme bulunamadi: " + id);
        }
    }

    public void sozlesmeSil(UUID id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new RuntimeException("Silinecek sozlesme bulunamadi: " + id);
        }
    }

    // ==================== FİLTRELEME VE ÖZEL SORGULAR ====================

    public List<Sozlesme> yakindaSuresiDolacakSozlesmeler() {
        LocalDate otuzGunSonra = LocalDate.now().plusDays(30);
        return repository.findYakindaBitecekSozlesmeler(otuzGunSonra);
    }

    public List<Sozlesme> kiraArtisiYapilmamisSozlesmeler() {
        return repository.findByKiraArtisiYapildi2025FalseAndAktifMiTrue();
    }

    public List<Sozlesme> bugunKiraOdemeSozlesmeleri() {
        int bugunGun = LocalDate.now().getDayOfMonth();
        return repository.findByKiraOdemeGunu(bugunGun);
    }

    // ==================== İŞ AKIŞI METODLARI ====================

    public Sozlesme kiraArtisiYap(UUID sozlesmeId, BigDecimal yeniKiraTutari) {
        Optional<Sozlesme> sozlesme = repository.findById(sozlesmeId);
        if (sozlesme.isPresent()) {
            Sozlesme s = sozlesme.get();
            s.setAylikKiraTutari(yeniKiraTutari);
            s.setKiraArtisiYapildi2025(true);
            return repository.save(s);
        } else {
            throw new RuntimeException("Sozlesme bulunamadi: " + sozlesmeId);
        }
    }

    public Sozlesme sozlesmeSonlandir(UUID sozlesmeId) {
        Optional<Sozlesme> sozlesme = repository.findById(sozlesmeId);
        if (sozlesme.isPresent()) {
            Sozlesme s = sozlesme.get();
            s.setAktifMi(false);
            return repository.save(s);
        } else {
            throw new RuntimeException("Sozlesme bulunamadi: " + sozlesmeId);
        }
    }

    // ==================== İSTATİSTİK VE RAPOR METODLARI ====================

    public Double toplamAylikKiraGeliri() {
        Double toplam = repository.toplamAylikKiraGeliri();
        return toplam != null ? toplam : 0.0;
    }

    // ✅ DASHBOARD İÇİN GEREKLİ METODLAR
    public long aktifSozlesmeSayisi() {
        return repository.countByAktifMiTrue();
    }

    // ✅ İSTATİSTİK METODLARI
    public List<Object[]> sehirBazindaSozlesmeSayisi() {
        // Repository'de bu metod yoksa basit bir implementasyon
        return repository.findAll().stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        s -> s.getGayrimenkulVarligi().getSehir(),
                        java.util.stream.Collectors.counting()
                ))
                .entrySet().stream()
                .map(entry -> new Object[]{entry.getKey(), entry.getValue()})
                .collect(java.util.stream.Collectors.toList());
    }

    public List<Object[]> durumBazindaSozlesmeSayisi() {
        // Aktif/Pasif bazında sayım
        long aktifSayi = repository.countByAktifMiTrue();
        long pasifSayi = repository.count() - aktifSayi;

        return java.util.Arrays.asList(
                new Object[]{"Aktif", aktifSayi},
                new Object[]{"Pasif", pasifSayi}
        );
    }
}