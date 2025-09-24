package com.kgyp.kgypsystem.service;

import com.kgyp.kgypsystem.entity.FinansalHareket;
import com.kgyp.kgypsystem.entity.FinansalHareket.HareketTipi;
import com.kgyp.kgypsystem.entity.GayrimenkulVarligi;
import com.kgyp.kgypsystem.repository.FinansalHareketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class FinansalHareketService {

    @Autowired
    private FinansalHareketRepository repository;

    @Autowired
    private GayrimenkulVarligiService gayrimenkulService;

    // ==================== TEMEL CRUD İŞLEMLERİ ====================

    // Tüm finansal hareketleri listele
    public List<FinansalHareket> tumHareketleriListele() {
        return repository.findAll();
    }

    // ID ile finansal hareket bul
    public Optional<FinansalHareket> hareketBul(UUID id) {
        return repository.findById(id);
    }

    // Yeni finansal hareket kaydet
    public FinansalHareket hareketKaydet(UUID varlikId, FinansalHareket hareket) {
        // Validasyon
        if (hareket.getTutar() == null || hareket.getTutar().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Hareket tutarı geçerli olmalıdır");
        }
        if (hareket.getHareketTipi() == null) {
            throw new IllegalArgumentException("Hareket tipi belirtilmelidir");
        }

        // Gayrimenkul varlığını bul ve ata
        Optional<GayrimenkulVarligi> gayrimenkul = gayrimenkulService.gayrimenkulBul(varlikId);
        if (!gayrimenkul.isPresent()) {
            throw new IllegalArgumentException("Gayrimenkul bulunamadı");
        }

        hareket.setGayrimenkulVarligi(gayrimenkul.get());

        // Kira geliri ise dönem otomatik atansın
        if (hareket.getHareketTipi() == HareketTipi.KIRA_GELIRI && hareket.getKiraDonemi() == null) {
            hareket.setKiraDonemi(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM")));
        }

        return repository.save(hareket);
    }

    // Finansal hareket güncelle
    public FinansalHareket hareketGuncelle(UUID id, FinansalHareket guncelHareket) {
        Optional<FinansalHareket> mevcut = repository.findById(id);
        if (mevcut.isPresent()) {
            FinansalHareket hareket = mevcut.get();

            if (guncelHareket.getTutar() != null) {
                hareket.setTutar(guncelHareket.getTutar());
            }
            if (guncelHareket.getAciklama() != null) {
                hareket.setAciklama(guncelHareket.getAciklama());
            }
            if (guncelHareket.getOdemeYontemi() != null) {
                hareket.setOdemeYontemi(guncelHareket.getOdemeYontemi());
            }
            if (guncelHareket.getFaturaNo() != null) {
                hareket.setFaturaNo(guncelHareket.getFaturaNo());
            }

            return repository.save(hareket);
        } else {
            throw new RuntimeException("Finansal hareket bulunamadı: " + id);
        }
    }

    // Finansal hareket sil
    public void hareketSil(UUID id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new RuntimeException("Silinecek finansal hareket bulunamadı: " + id);
        }
    }

    // ==================== FİLTRELEME İŞLEMLERİ ====================

    // Gayrimenkul bazında hareketler
    public List<FinansalHareket> gayrimenkulHareketleri(UUID varlikId) {
        return repository.findByGayrimenkulVarligi_VarlikId(varlikId);
    }

    // Hareket tipine göre filtrele
    public List<FinansalHareket> hareketTipineGoreFiltrele(HareketTipi tip) {
        return repository.findByHareketTipi(tip);
    }

    // Onay bekleyen hareketler
    public List<FinansalHareket> onayBekleyenHareketler() {
        return repository.findByOnaylanmisFalse();
    }

    // Hareket onayla
    public FinansalHareket hareketOnayla(UUID hareketId, String onaylayanKullanici) {
        Optional<FinansalHareket> hareket = repository.findById(hareketId);
        if (hareket.isPresent()) {
            FinansalHareket h = hareket.get();
            h.onayla(onaylayanKullanici);
            return repository.save(h);
        } else {
            throw new RuntimeException("Finansal hareket bulunamadı: " + hareketId);
        }
    }

    // ==================== TARİH BAZLI SORGULAR ====================

    // Tarih aralığında hareketler
    public List<FinansalHareket> tarihAraligindaHareketler(LocalDateTime baslangic, LocalDateTime bitis) {
        return repository.findByTarihAraliginda(baslangic, bitis);
    }

    // Dönemsel kira gelirleri
    public List<FinansalHareket> donemselKiraGelirleri(String donem) {
            return repository.findKiraGeliriByDonem(donem);
    }

    // ==================== İSTATİSTİK VE RAPOR METODLARI ====================

    // Mali özet bilgileri
    public MaliOzet maliOzetHesapla() {
        BigDecimal toplamGelir = repository.toplamGelir();
        BigDecimal toplamGider = repository.toplamGider();

        if (toplamGelir == null) toplamGelir = BigDecimal.ZERO;
        if (toplamGider == null) toplamGider = BigDecimal.ZERO;

        BigDecimal netKar = toplamGelir.subtract(toplamGider);

        return new MaliOzet(toplamGelir, toplamGider, netKar);
    }

    // Son hareketler
    public List<FinansalHareket> sonHareketler() {
        return repository.findTop10ByOrderByHareketTarihiDesc();
    }

    // Aylık kira geliri raporu
    public List<Object[]> aylikKiraGeliriRaporu() {
        return repository.aylikKiraGeliriRaporu();
    }

    // Hareket tipi bazında raporlama
    public List<Object[]> hareketTipiBazindaRapor() {
        return repository.hareketTipiBazindaToplam();
    }

    // ✅ EKSİK METOD EKLENDİ - Controller'da kullanılıyor
    public long toplamHareketSayisi() {
        return repository.count();
    }

    // ==================== EK YARDIMCI METODLAR ====================

    // Gelir hareketlerini getir
    public List<FinansalHareket> gelirHareketleri() {
        return repository.findAll().stream()
                .filter(FinansalHareket::isGelir)
                .collect(java.util.stream.Collectors.toList());
    }

    // Gider hareketlerini getir
    public List<FinansalHareket> giderHareketleri() {
        return repository.findAll().stream()
                .filter(FinansalHareket::isGider)
                .collect(java.util.stream.Collectors.toList());
    }

    // Bu ayki toplam gelir
    public BigDecimal buAyToplamGelir() {
        LocalDateTime ayBaslangic = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
        LocalDateTime ayBitis = ayBaslangic.plusMonths(1).minusSeconds(1);

        List<FinansalHareket> hareketler = tarihAraligindaHareketler(ayBaslangic, ayBitis);

        return hareketler.stream()
                .filter(h -> h.getOnaylanmis() && h.isGelir())
                .map(FinansalHareket::getTutar)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Bu ayki toplam gider
    public BigDecimal buAyToplamGider() {
        LocalDateTime ayBaslangic = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
        LocalDateTime ayBitis = ayBaslangic.plusMonths(1).minusSeconds(1);

        List<FinansalHareket> hareketler = tarihAraligindaHareketler(ayBaslangic, ayBitis);

        return hareketler.stream()
                .filter(h -> h.getOnaylanmis() && h.isGider())
                .map(FinansalHareket::getTutar)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Mali özet için DTO sınıfı
    public static class MaliOzet {
        private BigDecimal toplamGelir;
        private BigDecimal toplamGider;
        private BigDecimal netKar;

        public MaliOzet(BigDecimal toplamGelir, BigDecimal toplamGider, BigDecimal netKar) {
            this.toplamGelir = toplamGelir;
            this.toplamGider = toplamGider;
            this.netKar = netKar;
        }

        // Getters
        public BigDecimal getToplamGelir() { return toplamGelir; }
        public BigDecimal getToplamGider() { return toplamGider; }
        public BigDecimal getNetKar() { return netKar; }

        // Setters
        public void setToplamGelir(BigDecimal toplamGelir) { this.toplamGelir = toplamGelir; }
        public void setToplamGider(BigDecimal toplamGider) { this.toplamGider = toplamGider; }
        public void setNetKar(BigDecimal netKar) { this.netKar = netKar; }
    }
}