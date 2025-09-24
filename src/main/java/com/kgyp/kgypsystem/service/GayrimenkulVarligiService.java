package com.kgyp.kgypsystem.service;

import com.kgyp.kgypsystem.entity.GayrimenkulVarligi;
import com.kgyp.kgypsystem.entity.KullanimDurumu;
import com.kgyp.kgypsystem.entity.HizmetTuru;
import com.kgyp.kgypsystem.repository.GayrimenkulVarligiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class GayrimenkulVarligiService {

    @Autowired
    private GayrimenkulVarligiRepository repository;

    // ==================== TEMEL CRUD İŞLEMLERİ ====================

    public List<GayrimenkulVarligi> tumGayrimenkulleriListele() {
        return repository.findAll();
    }

    public Optional<GayrimenkulVarligi> gayrimenkulBul(UUID id) {
        return repository.findById(id);
    }

    public GayrimenkulVarligi gayrimenkulKaydet(GayrimenkulVarligi gayrimenkul) {
        // ✅ PTT'ye özel validasyonlar
        if (gayrimenkul.getIsyeriAdi() == null || gayrimenkul.getIsyeriAdi().trim().isEmpty()) {
            throw new IllegalArgumentException("İşyeri adı boş olamaz");
        }
        if (gayrimenkul.getAdres() == null || gayrimenkul.getAdres().trim().isEmpty()) {
            throw new IllegalArgumentException("Adres boş olamaz");
        }
        if (gayrimenkul.getSehir() == null || gayrimenkul.getSehir().trim().isEmpty()) {
            throw new IllegalArgumentException("Şehir boş olamaz");
        }
        if (gayrimenkul.getHizmetTuru() == null) {
            throw new IllegalArgumentException("Hizmet türü belirtilmelidir");
        }
        if (gayrimenkul.getKullanimDurumu() == null) {
            throw new IllegalArgumentException("Kullanım durumu belirtilmelidir");
        }

        return repository.save(gayrimenkul);
    }

    public GayrimenkulVarligi gayrimenkulGuncelle(UUID id, GayrimenkulVarligi guncelGayrimenkul) {
        Optional<GayrimenkulVarligi> mevcut = repository.findById(id);
        if (mevcut.isPresent()) {
            GayrimenkulVarligi gayrimenkul = mevcut.get();

            // Güncelleme işlemleri
            if (guncelGayrimenkul.getIsyeriAdi() != null) {
                gayrimenkul.setIsyeriAdi(guncelGayrimenkul.getIsyeriAdi());
            }
            if (guncelGayrimenkul.getHizmetTuru() != null) {
                gayrimenkul.setHizmetTuru(guncelGayrimenkul.getHizmetTuru());
            }
            if (guncelGayrimenkul.getAdres() != null) {
                gayrimenkul.setAdres(guncelGayrimenkul.getAdres());
            }
            if (guncelGayrimenkul.getSehir() != null) {
                gayrimenkul.setSehir(guncelGayrimenkul.getSehir());
            }
            if (guncelGayrimenkul.getIlce() != null) {
                gayrimenkul.setIlce(guncelGayrimenkul.getIlce());
            }
            if (guncelGayrimenkul.getTapuNo() != null) {
                gayrimenkul.setTapuNo(guncelGayrimenkul.getTapuNo());
            }
            if (guncelGayrimenkul.getBrutM2() != null) {
                gayrimenkul.setBrutM2(guncelGayrimenkul.getBrutM2());
            }
            if (guncelGayrimenkul.getNetM2() != null) {
                gayrimenkul.setNetM2(guncelGayrimenkul.getNetM2());
            }
            if (guncelGayrimenkul.getGoogleMapsLink() != null) {
                gayrimenkul.setGoogleMapsLink(guncelGayrimenkul.getGoogleMapsLink());
            }
            if (guncelGayrimenkul.getDegerlemeRaporuLink() != null) {
                gayrimenkul.setDegerlemeRaporuLink(guncelGayrimenkul.getDegerlemeRaporuLink());
            }
            if (guncelGayrimenkul.getKullanimDurumu() != null) {
                gayrimenkul.setKullanimDurumu(guncelGayrimenkul.getKullanimDurumu());
            }
            if (guncelGayrimenkul.getEnSonDegerlemeTarihi() != null) {
                gayrimenkul.setEnSonDegerlemeTarihi(guncelGayrimenkul.getEnSonDegerlemeTarihi());
            }
            if (guncelGayrimenkul.getEnSonDegerlemeTutari() != null) {
                gayrimenkul.setEnSonDegerlemeTutari(guncelGayrimenkul.getEnSonDegerlemeTutari());
            }

            return repository.save(gayrimenkul);
        } else {
            throw new RuntimeException("Gayrimenkul bulunamadı: " + id);
        }
    }

    public void gayrimenkulSil(UUID id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new RuntimeException("Silinecek gayrimenkul bulunamadı: " + id);
        }
    }

    // ==================== TEMEL ARAMA İŞLEMLERİ ====================

    public List<GayrimenkulVarligi> sehirGoureGayrimenkulAra(String sehir) {
        return repository.findBySehir(sehir);
    }

    public List<GayrimenkulVarligi> isyeriAdinaGoreAra(String isyeriAdi) {
        return repository.findByIsyeriAdiContainingIgnoreCase(isyeriAdi);
    }

    public List<GayrimenkulVarligi> kullanimDurumunaGoreAra(KullanimDurumu kullanimDurumu) {
        return repository.findByKullanimDurumu(kullanimDurumu);
    }

    public List<GayrimenkulVarligi> hizmetTurunaGoreAra(HizmetTuru hizmetTuru) {
        return repository.findByHizmetTuru(hizmetTuru);
    }

    public List<GayrimenkulVarligi> adresIcindeAra(String arananKelime) {
        return repository.findByAdresContainingIgnoreCase(arananKelime);
    }

    public List<GayrimenkulVarligi> buyukGayrimenkuller(Double minM2) {
        return repository.findByBrutM2GreaterThan(minM2);
    }

    public List<GayrimenkulVarligi> cokluKriterleAra(String sehir, HizmetTuru hizmetTuru,
                                                     KullanimDurumu kullanimDurumu, String isyeriAdi) {
        return repository.findByMultipleCriteria(sehir, hizmetTuru, kullanimDurumu, isyeriAdi);
    }

    // ==================== PTT'YE ÖZEL İŞ KURALLARI ====================

    /**
     * PTT'nin operasyonel birimlerini listeler
     */
    public List<GayrimenkulVarligi> operasyonelBirimleriGetir() {
        return repository.findOperasyonelBirimler();
    }

    /**
     * PTT'nin destek birimlerini listeler
     */
    public List<GayrimenkulVarligi> destekBirimleriGetir() {
        return repository.findDestekBirimleri();
    }

    /**
     * Posta işlemi yapan birimleri listeler
     */
    public List<GayrimenkulVarligi> postaIlgiliBirimleriGetir() {
        return repository.findPostaIlgiliBirimler();
    }

    /**
     * Kargo işlemi yapan birimleri listeler
     */
    public List<GayrimenkulVarligi> kargoIlgiliBirimleriGetir() {
        return repository.findKargoIlgiliBirimler();
    }

    /**
     * Gelir getiren gayrimenkulleri listeler (PTT'nin kiraya verdiği yerler)
     */
    public List<GayrimenkulVarligi> gelirGetirenGayrimenkulleriGetir() {
        return repository.findByKullanimDurumuEquals(KullanimDurumu.MAL_SAHIBI_KIRADA);
    }

    /**
     * Kira gideri olan gayrimenkulleri listeler (PTT'nin kiracı olduğu yerler)
     */
    public List<GayrimenkulVarligi> kiraGideriOlanGayrimenkulleriGetir() {
        return repository.findKiraGideriOlanGayrimenkuller();
    }

    /**
     * Atıl durumdaki gayrimenkulleri listeler
     */
    public List<GayrimenkulVarligi> atilGayrimenkulleriGetir() {
        return repository.findAtilGayrimenkuller();
    }

    /**
     * Tahsisli kullanılan gayrimenkulleri listeler
     */
    public List<GayrimenkulVarligi> tahsisliGayrimenkulleriGetir() {
        return repository.findTahsisliGayrimenkuller();
    }

    /**
     * Veri kalitesi kontrolü - eksik bilgili kayıtları döndürür
     */
    public List<GayrimenkulVarligi> eksikBilgiliKayitlar() {
        return repository.findIncompleteRecords();
    }

    // ==================== ÖZEL İŞ KURALLARI ====================

    /**
     * Kullanım durumunu değiştir ve iş kurallarını uygula
     */
    public GayrimenkulVarligi kullanimDurumuDegistir(UUID id, KullanimDurumu yeniDurum) {
        Optional<GayrimenkulVarligi> gayrimenkul = repository.findById(id);
        if (gayrimenkul.isPresent()) {
            GayrimenkulVarligi g = gayrimenkul.get();
            KullanimDurumu eskiDurum = g.getKullanimDurumu();

            // İş kuralı: Atıl durumdan kirada duruma geçerken uyarı
            if (eskiDurum == KullanimDurumu.MAL_SAHIBI_ATIL &&
                    yeniDurum == KullanimDurumu.MAL_SAHIBI_KIRADA) {
                // Bu durumda sözleşme kaydı da oluşturulmalı (başka serviste)
                System.out.println("UYARI: Atıl gayrimenkul kiraya verildi. Sözleşme kaydı oluşturulmalı.");
            }

            // İş kuralı: Kirada durumdan atıl duruma geçerken sözleşme kontrolü
            if (eskiDurum == KullanimDurumu.MAL_SAHIBI_KIRADA &&
                    yeniDurum == KullanimDurumu.MAL_SAHIBI_ATIL) {
                // Bu durumda mevcut sözleşme sonlandırılmalı
                System.out.println("UYARI: Kiradaki gayrimenkul atıl duruma geçirildi. Sözleşme sonlandırılmalı.");
            }

            g.setKullanimDurumu(yeniDurum);
            return repository.save(g);
        } else {
            throw new RuntimeException("Gayrimenkul bulunamadı: " + id);
        }
    }

    /**
     * Değerleme raporu güncelle
     */
    public GayrimenkulVarligi degerlemeRaporuGuncelle(UUID id, String raporLink,
                                                      BigDecimal tutar, LocalDate tarih) {
        Optional<GayrimenkulVarligi> gayrimenkul = repository.findById(id);
        if (gayrimenkul.isPresent()) {
            GayrimenkulVarligi varlık = gayrimenkul.get();
            varlık.setDegerlemeRaporuLink(raporLink);
            varlık.setEnSonDegerlemeTutari(tutar);
            varlık.setEnSonDegerlemeTarihi(tarih);
            return repository.save(varlık);
        } else {
            throw new RuntimeException("Gayrimenkul bulunamadı: " + id);
        }
    }

    // ==================== İSTATİSTİK ve RAPORLAMA ====================

    public long toplamGayrimenkulSayisi() {
        return repository.count();
    }

    public List<Object[]> sehirBazindaSayim() {
        return repository.countBySehir();
    }

    public List<Object[]> kullanimDurumuBazindaSayim() {
        return repository.countByKullanimDurumu();
    }

    public List<Object[]> hizmetTuruBazindaSayim() {
        return repository.countByHizmetTuru();
    }

    public List<Object[]> sehirVeHizmetTuruBazindaSayim() {
        return repository.countBySehirAndHizmetTuru();
    }

    public List<Object[]> birimTuruBazindaSayim() {
        return repository.countByBirimTuru();
    }

    public List<Object[]> kullanimVeHizmetTuruCarprazTablosu() {
        return repository.countByKullanimDurumuAndHizmetTuru();
    }

    public Double toplamBrutMetrekare() {
        return repository.toplamBrutMetrekare();
    }

    public List<Object[]> metrekareKullanimDagilimi() {
        return repository.metrekareByKullanimDurumu();
    }

    public List<Object[]> metrekareHizmetTuruDagilimi() {
        return repository.metrekareByHizmetTuru();
    }

    // ==================== PTT'YE ÖZEL RAPOR METODLARI ====================

    /**
     * PTT Gayrimenkul Portföy Özeti
     */
    public PTTGayrimenkulOzeti pttPortfoyOzeti() {
        long toplamSayi = repository.count();
        long operasyonelSayi = repository.findOperasyonelBirimler().size();
        long destekSayi = repository.findDestekBirimleri().size();
        long gelirGetiren = repository.findByKullanimDurumuEquals(KullanimDurumu.MAL_SAHIBI_KIRADA).size();
        long kiraGideri = repository.findKiraGideriOlanGayrimenkuller().size();
        long atilSayi = repository.findAtilGayrimenkuller().size();
        long tahsisliSayi = repository.findTahsisliGayrimenkuller().size();

        Double toplamMetrekare = repository.toplamBrutMetrekare();
        if (toplamMetrekare == null) toplamMetrekare = 0.0;

        return new PTTGayrimenkulOzeti(
                toplamSayi, operasyonelSayi, destekSayi, gelirGetiren,
                kiraGideri, atilSayi, tahsisliSayi, toplamMetrekare
        );
    }

    /**
     * Şehir bazında PTT birim dağılımı
     */
    public Map<String, Map<String, Long>> sehirBazindaBirimDagilimi() {
        List<Object[]> data = repository.countBySehirAndHizmetTuru();

        return data.stream().collect(
                Collectors.groupingBy(
                        row -> (String) row[0], // şehir
                        Collectors.toMap(
                                row -> ((HizmetTuru) row[1]).getAciklama(), // hizmet türü
                                row -> (Long) row[2] // sayı
                        )
                )
        );
    }

    // ==================== DTO SINIFI ====================

    public static class PTTGayrimenkulOzeti {
        private long toplamGayrimenkulSayisi;
        private long operasyonelBirimSayisi;
        private long destekBirimSayisi;
        private long gelirGetirenSayisi;
        private long kiraGideriSayisi;
        private long atilGayrimenkulSayisi;
        private long tahsisliGayrimenkulSayisi;
        private double toplamBrutMetrekare;

        public PTTGayrimenkulOzeti(long toplamGayrimenkulSayisi, long operasyonelBirimSayisi,
                                   long destekBirimSayisi, long gelirGetirenSayisi, long kiraGideriSayisi,
                                   long atilGayrimenkulSayisi, long tahsisliGayrimenkulSayisi,
                                   double toplamBrutMetrekare) {
            this.toplamGayrimenkulSayisi = toplamGayrimenkulSayisi;
            this.operasyonelBirimSayisi = operasyonelBirimSayisi;
            this.destekBirimSayisi = destekBirimSayisi;
            this.gelirGetirenSayisi = gelirGetirenSayisi;
            this.kiraGideriSayisi = kiraGideriSayisi;
            this.atilGayrimenkulSayisi = atilGayrimenkulSayisi;
            this.tahsisliGayrimenkulSayisi = tahsisliGayrimenkulSayisi;
            this.toplamBrutMetrekare = toplamBrutMetrekare;
        }

        // Getters ve setters
        public long getToplamGayrimenkulSayisi() { return toplamGayrimenkulSayisi; }
        public long getOperasyonelBirimSayisi() { return operasyonelBirimSayisi; }
        public long getDestekBirimSayisi() { return destekBirimSayisi; }
        public long getGelirGetirenSayisi() { return gelirGetirenSayisi; }
        public long getKiraGideriSayisi() { return kiraGideriSayisi; }
        public long getAtilGayrimenkulSayisi() { return atilGayrimenkulSayisi; }
        public long getTahsisliGayrimenkulSayisi() { return tahsisliGayrimenkulSayisi; }
        public double getToplamBrutMetrekare() { return toplamBrutMetrekare; }

        public void setToplamGayrimenkulSayisi(long toplamGayrimenkulSayisi) { this.toplamGayrimenkulSayisi = toplamGayrimenkulSayisi; }
        public void setOperasyonelBirimSayisi(long operasyonelBirimSayisi) { this.operasyonelBirimSayisi = operasyonelBirimSayisi; }
        public void setDestekBirimSayisi(long destekBirimSayisi) { this.destekBirimSayisi = destekBirimSayisi; }
        public void setGelirGetirenSayisi(long gelirGetirenSayisi) { this.gelirGetirenSayisi = gelirGetirenSayisi; }
        public void setKiraGideriSayisi(long kiraGideriSayisi) { this.kiraGideriSayisi = kiraGideriSayisi; }
        public void setAtilGayrimenkulSayisi(long atilGayrimenkulSayisi) { this.atilGayrimenkulSayisi = atilGayrimenkulSayisi; }
        public void setTahsisliGayrimenkulSayisi(long tahsisliGayrimenkulSayisi) { this.tahsisliGayrimenkulSayisi = tahsisliGayrimenkulSayisi; }
        public void setToplamBrutMetrekare(double toplamBrutMetrekare) { this.toplamBrutMetrekare = toplamBrutMetrekare; }
    }
}