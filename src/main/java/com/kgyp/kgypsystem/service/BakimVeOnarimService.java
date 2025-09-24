package com.kgyp.kgypsystem.service;

import com.kgyp.kgypsystem.entity.BakimVeOnarim;
import com.kgyp.kgypsystem.entity.BakimVeOnarim.BakimDurumu;
import com.kgyp.kgypsystem.entity.BakimVeOnarim.BakimKategorisi;
import com.kgyp.kgypsystem.entity.BakimVeOnarim.OncelikSeviyesi;
import com.kgyp.kgypsystem.entity.GayrimenkulVarligi;
import com.kgyp.kgypsystem.repository.BakimVeOnarimRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class BakimVeOnarimService {

    @Autowired
    private BakimVeOnarimRepository repository;

    @Autowired
    private GayrimenkulVarligiService gayrimenkulService;

    // Tüm bakım işlerini listele
    public List<BakimVeOnarim> tumBakimlariListele() {
        return repository.findAll();
    }

    // Aktif bakım işlerini listele
    public List<BakimVeOnarim> aktifBakimlariListele() {
        return repository.findAktifBakimlar();
    }

    // ID ile bakım işi bul
    public Optional<BakimVeOnarim> bakimBul(UUID id) {
        return repository.findById(id);
    }

    // Yeni bakım işi kaydet
    public BakimVeOnarim bakimKaydet(UUID varlikId, BakimVeOnarim bakim) {
        // Validasyon
        if (bakim.getBaslik() == null || bakim.getBaslik().trim().isEmpty()) {
            throw new IllegalArgumentException("Bakım başlığı boş olamaz");
        }
        if (bakim.getAciklama() == null || bakim.getAciklama().trim().isEmpty()) {
            throw new IllegalArgumentException("Bakım açıklaması boş olamaz");
        }
        if (bakim.getKategori() == null) {
            throw new IllegalArgumentException("Bakım kategorisi belirtilmelidir");
        }

        // Gayrimenkul varlığını bul ve ata
        Optional<GayrimenkulVarligi> gayrimenkul = gayrimenkulService.gayrimenkulBul(varlikId);
        if (!gayrimenkul.isPresent()) {
            throw new IllegalArgumentException("Gayrimenkul bulunamadı");
        }

        bakim.setGayrimenkulVarligi(gayrimenkul.get());

        // Varsayılan değerleri ata
        if (bakim.getOncelik() == null) {
            bakim.setOncelik(OncelikSeviyesi.ORTA);
        }
        if (bakim.getDurum() == null) {
            bakim.setDurum(BakimDurumu.PLANLANMIS);
        }
        if (bakim.getBaslangicTarihi() == null) {
            bakim.setBaslangicTarihi(LocalDateTime.now());
        }

        return repository.save(bakim);
    }

    // Bakım işi güncelle
    public BakimVeOnarim bakimGuncelle(UUID id, BakimVeOnarim guncelBakim) {
        Optional<BakimVeOnarim> mevcut = repository.findById(id);
        if (mevcut.isPresent()) {
            BakimVeOnarim bakim = mevcut.get();

            // Güncelleme işlemleri
            if (guncelBakim.getBaslik() != null) {
                bakim.setBaslik(guncelBakim.getBaslik());
            }
            if (guncelBakim.getAciklama() != null) {
                bakim.setAciklama(guncelBakim.getAciklama());
            }
            if (guncelBakim.getKategori() != null) {
                bakim.setKategori(guncelBakim.getKategori());
            }
            if (guncelBakim.getOncelik() != null) {
                bakim.setOncelik(guncelBakim.getOncelik());
            }
            if (guncelBakim.getDurum() != null) {
                bakim.setDurum(guncelBakim.getDurum());
            }
            if (guncelBakim.getPlanlananBitisTarihi() != null) {
                bakim.setPlanlananBitisTarihi(guncelBakim.getPlanlananBitisTarihi());
            }
            if (guncelBakim.getTahminiMaliyet() != null) {
                bakim.setTahminiMaliyet(guncelBakim.getTahminiMaliyet());
            }
            if (guncelBakim.getSorumluPersonel() != null) {
                bakim.setSorumluPersonel(guncelBakim.getSorumluPersonel());
            }
            if (guncelBakim.getTedarikciFirma() != null) {
                bakim.setTedarikciFirma(guncelBakim.getTedarikciFirma());
            }
            if (guncelBakim.getTedarikciTelefon() != null) {
                bakim.setTedarikciTelefon(guncelBakim.getTedarikciTelefon());
            }
            if (guncelBakim.getNotlar() != null) {
                bakim.setNotlar(guncelBakim.getNotlar());
            }

            return repository.save(bakim);
        } else {
            throw new RuntimeException("Bakım işi bulunamadı: " + id);
        }
    }

    // Bakım işini sil
    public void bakimSil(UUID id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new RuntimeException("Silinecek bakım işi bulunamadı: " + id);
        }
    }

    // Gayrimenkul bazında bakım işleri
    public List<BakimVeOnarim> gayrimenkulBakimlari(UUID varlikId) {
        return repository.findByGayrimenkulVarligi_VarlikId(varlikId);
    }

    // Duruma göre bakım işleri
    public List<BakimVeOnarim> durumaGoreBakimlar(BakimDurumu durum) {
        return repository.findByDurum(durum);
    }

    // Kategoriye göre bakım işleri
    public List<BakimVeOnarim> kategoriyeGoreBakimlar(BakimKategorisi kategori) {
        return repository.findByKategori(kategori);
    }

    // Önceliğe göre bakım işleri
    public List<BakimVeOnarim> onceligeGoreBakimlar(OncelikSeviyesi oncelik) {
        return repository.findByOncelik(oncelik);
    }

    // Kritik bakım işleri
    public List<BakimVeOnarim> kritikBakimlar() {
        return repository.findKritikBakimlar();
    }

    // Geciken bakım işleri
    public List<BakimVeOnarim> gecikenBakimlar() {
        return repository.findGecikenBakimlar(LocalDateTime.now());
    }

    // Bakım işini başlat
    public BakimVeOnarim bakimBaslat(UUID bakimId, String sorumluPersonel) {
        Optional<BakimVeOnarim> bakim = repository.findById(bakimId);
        if (bakim.isPresent()) {
            BakimVeOnarim b = bakim.get();
            b.setDurum(BakimDurumu.BASLANDI);
            b.setBaslangicTarihi(LocalDateTime.now());
            if (sorumluPersonel != null) {
                b.setSorumluPersonel(sorumluPersonel);
            }
            return repository.save(b);
        } else {
            throw new RuntimeException("Bakım işi bulunamadı: " + bakimId);
        }
    }

    // Bakım işini tamamla
    public BakimVeOnarim bakimTamamla(UUID bakimId, BigDecimal gercekMaliyet, String notlar) {
        Optional<BakimVeOnarim> bakim = repository.findById(bakimId);
        if (bakim.isPresent()) {
            BakimVeOnarim b = bakim.get();
            b.tamamla(gercekMaliyet);
            if (notlar != null) {
                b.setNotlar(b.getNotlar() != null ? b.getNotlar() + "\n" + notlar : notlar);
            }
            return repository.save(b);
        } else {
            throw new RuntimeException("Bakım işi bulunamadı: " + bakimId);
        }
    }

    // Bakım işini iptal et
    public BakimVeOnarim bakimIptalEt(UUID bakimId, String iptalSebebi) {
        Optional<BakimVeOnarim> bakim = repository.findById(bakimId);
        if (bakim.isPresent()) {
            BakimVeOnarim b = bakim.get();
            b.setDurum(BakimDurumu.IPTAL_EDILDI);
            String yeniNot = "İPTAL EDİLDİ: " + iptalSebebi;
            b.setNotlar(b.getNotlar() != null ? b.getNotlar() + "\n" + yeniNot : yeniNot);
            return repository.save(b);
        } else {
            throw new RuntimeException("Bakım işi bulunamadı: " + bakimId);
        }
    }

    // Garanti güncelle
    public BakimVeOnarim garantiGuncelle(UUID bakimId, Integer garantiSuresiAy) {
        Optional<BakimVeOnarim> bakim = repository.findById(bakimId);
        if (bakim.isPresent()) {
            BakimVeOnarim b = bakim.get();
            b.setGarantiSuresiAy(garantiSuresiAy);
            return repository.save(b);
        } else {
            throw new RuntimeException("Bakım işi bulunamadı: " + bakimId);
        }
    }

    // Bu hafta başlayacak bakım işleri
    public List<BakimVeOnarim> buHaftaBaslayacakBakimlar() {
        LocalDateTime haftaBaslangici = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
        LocalDateTime haftaBitisi = haftaBaslangici.plusDays(7);
        return repository.findBuHaftaBaslayacakBakimlar(haftaBaslangici, haftaBitisi);
    }

    // Tarih aralığında tamamlanan bakımlar
    public List<BakimVeOnarim> tarihAraligindaTamamlananBakimlar(LocalDateTime baslangic, LocalDateTime bitis) {
        return repository.findTamamlananBakimlarByTarihAraligi(baslangic, bitis);
    }

    // Garanide olan bakımlar
    public List<BakimVeOnarim> garantideOlanBakimlar() {
        return repository.findGarantideOlanBakimlar(LocalDateTime.now());
    }

    // Sorumlu personele göre bakımlar
    public List<BakimVeOnarim> sorumluPersonelBakimlari(String sorumluPersonel) {
        return repository.findBySorumluPersonelContainingIgnoreCase(sorumluPersonel);
    }

    // Tedarikci firmaya göre bakımlar
    public List<BakimVeOnarim> tedarikcieBakimlari(String tedarikciFirma) {
        return repository.findByTedarikciFirmaContainingIgnoreCase(tedarikciFirma);
    }

    // Bakım arama
    public List<BakimVeOnarim> bakimAra(String aramaKelimesi) {
        return repository.findByBaslikOrAciklamaContaining(aramaKelimesi);
    }

    // Son eklenen bakımlar
    public List<BakimVeOnarim> sonEklenenBakimlar() {
        return repository.findTop10ByOrderByOlusturmaTarihiDesc();
    }

    // Bakım özet bilgileri
    public BakimOzet bakimOzetiHesapla() {
        long toplamBakim = repository.count();
        List<BakimVeOnarim> aktifBakimlar = repository.findAktifBakimlar();
        List<BakimVeOnarim> gecikenBakimlar = repository.findGecikenBakimlar(LocalDateTime.now());
        List<BakimVeOnarim> kritikBakimlar = repository.findKritikBakimlar();
        BigDecimal toplamMaliyet = repository.toplamBakimMaliyeti();

        if (toplamMaliyet == null) toplamMaliyet = BigDecimal.ZERO;

        return new BakimOzet(
                toplamBakim,
                aktifBakimlar.size(),
                gecikenBakimlar.size(),
                kritikBakimlar.size(),
                toplamMaliyet
        );
    }

    // İstatistik bilgileri
    public List<Object[]> kategoriBazindaBakimSayisi() {
        return repository.kategoriBazindaBakimSayisi();
    }

    public List<Object[]> durumBazindaBakimSayisi() {
        return repository.durumBazindaBakimSayisi();
    }

    // Bakım özet için DTO sınıfı
    public static class BakimOzet {
        private long toplamBakimSayisi;
        private long aktifBakimSayisi;
        private long gecikenBakimSayisi;
        private long kritikBakimSayisi;
        private BigDecimal toplamMaliyet;

        public BakimOzet(long toplamBakimSayisi, long aktifBakimSayisi,
                         long gecikenBakimSayisi, long kritikBakimSayisi, BigDecimal toplamMaliyet) {
            this.toplamBakimSayisi = toplamBakimSayisi;
            this.aktifBakimSayisi = aktifBakimSayisi;
            this.gecikenBakimSayisi = gecikenBakimSayisi;
            this.kritikBakimSayisi = kritikBakimSayisi;
            this.toplamMaliyet = toplamMaliyet;
        }

        // Getters
        public long getToplamBakimSayisi() { return toplamBakimSayisi; }
        public long getAktifBakimSayisi() { return aktifBakimSayisi; }
        public long getGecikenBakimSayisi() { return gecikenBakimSayisi; }
        public long getKritikBakimSayisi() { return kritikBakimSayisi; }
        public BigDecimal getToplamMaliyet() { return toplamMaliyet; }

        // Setters
        public void setToplamBakimSayisi(long toplamBakimSayisi) { this.toplamBakimSayisi = toplamBakimSayisi; }
        public void setAktifBakimSayisi(long aktifBakimSayisi) { this.aktifBakimSayisi = aktifBakimSayisi; }
        public void setGecikenBakimSayisi(long gecikenBakimSayisi) { this.gecikenBakimSayisi = gecikenBakimSayisi; }
        public void setKritikBakimSayisi(long kritikBakimSayisi) { this.kritikBakimSayisi = kritikBakimSayisi; }
        public void setToplamMaliyet(BigDecimal toplamMaliyet) { this.toplamMaliyet = toplamMaliyet; }
    }
}