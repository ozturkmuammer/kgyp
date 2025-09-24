package com.kgyp.kgypsystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bakim_ve_onarim")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class BakimVeOnarim {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID bakimId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "varlik_id", nullable = false)
    @JsonIgnoreProperties({"sozlesmeler", "bakimlar", "finansalHareketler", "dokumanlar", "hibernateLazyInitializer", "handler"})
    private GayrimenkulVarligi gayrimenkulVarligi;

    @Column(name = "baslik", nullable = false, length = 200)
    private String baslik;

    @Column(name = "aciklama", nullable = false, length = 1000)
    private String aciklama;

    @Column(name = "kategori", nullable = false)
    @Enumerated(EnumType.STRING)
    private BakimKategorisi kategori;

    @Column(name = "oncelik", nullable = false)
    @Enumerated(EnumType.STRING)
    private OncelikSeviyesi oncelik;

    @Column(name = "durum", nullable = false)
    @Enumerated(EnumType.STRING)
    private BakimDurumu durum = BakimDurumu.PLANLANMIS;

    @Column(name = "baslangic_tarihi", nullable = false)
    private LocalDateTime baslangicTarihi;

    @Column(name = "planlanan_bitis_tarihi")
    private LocalDateTime planlananBitisTarihi;

    @Column(name = "gercek_bitis_tarihi")
    private LocalDateTime gercekBitisTarihi;

    @Column(name = "tahmini_maliyet", precision = 15, scale = 2)
    private BigDecimal tahminiMaliyet;

    @Column(name = "gercek_maliyet", precision = 15, scale = 2)
    private BigDecimal gercekMaliyet;

    @Column(name = "sorumlu_personel", length = 100)
    private String sorumluPersonel;

    @Column(name = "tedarikci_firma", length = 200)
    private String tedarikciFirma;

    @Column(name = "tedarikci_telefon", length = 20)
    private String tedarikciTelefon;

    @Column(name = "garanti_suresi_ay")
    private Integer garantiSuresiAy; // Ay cinsinden

    @Column(name = "garanti_bitis_tarihi")
    private LocalDateTime garantiBitisTarihi;

    @Column(name = "notlar", length = 1000)
    private String notlar;

    @Column(name = "olusturma_tarihi", nullable = false, updatable = false)
    private LocalDateTime olusturmaTarihi;

    @Column(name = "guncelleme_tarihi")
    private LocalDateTime guncellemeTarihi;

    @Column(name = "olusturan_kullanici", length = 100)
    private String olusturanKullanici;

    @PrePersist
    protected void onCreate() {
        olusturmaTarihi = LocalDateTime.now();
        guncellemeTarihi = LocalDateTime.now();
        if (oncelik == null) {
            oncelik = OncelikSeviyesi.ORTA;
        }
        if (durum == null) {
            durum = BakimDurumu.PLANLANMIS;
        }
        if (baslangicTarihi == null) {
            baslangicTarihi = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        guncellemeTarihi = LocalDateTime.now();
    }

    // Constructors
    public BakimVeOnarim() {}

    public BakimVeOnarim(GayrimenkulVarligi gayrimenkulVarligi, String baslik,
                         String aciklama, BakimKategorisi kategori) {
        this.gayrimenkulVarligi = gayrimenkulVarligi;
        this.baslik = baslik;
        this.aciklama = aciklama;
        this.kategori = kategori;
        this.baslangicTarihi = LocalDateTime.now();
        this.oncelik = OncelikSeviyesi.ORTA;
        this.durum = BakimDurumu.PLANLANMIS;
    }

    // Getters and Setters
    public UUID getBakimId() {
        return bakimId;
    }

    public void setBakimId(UUID bakimId) {
        this.bakimId = bakimId;
    }

    public GayrimenkulVarligi getGayrimenkulVarligi() {
        return gayrimenkulVarligi;
    }

    public void setGayrimenkulVarligi(GayrimenkulVarligi gayrimenkulVarligi) {
        this.gayrimenkulVarligi = gayrimenkulVarligi;
    }

    public String getBaslik() {
        return baslik;
    }

    public void setBaslik(String baslik) {
        this.baslik = baslik;
    }

    public String getAciklama() {
        return aciklama;
    }

    public void setAciklama(String aciklama) {
        this.aciklama = aciklama;
    }

    public BakimKategorisi getKategori() {
        return kategori;
    }

    public void setKategori(BakimKategorisi kategori) {
        this.kategori = kategori;
    }

    public OncelikSeviyesi getOncelik() {
        return oncelik;
    }

    public void setOncelik(OncelikSeviyesi oncelik) {
        this.oncelik = oncelik;
    }

    public BakimDurumu getDurum() {
        return durum;
    }

    public void setDurum(BakimDurumu durum) {
        this.durum = durum;
    }

    public LocalDateTime getBaslangicTarihi() {
        return baslangicTarihi;
    }

    public void setBaslangicTarihi(LocalDateTime baslangicTarihi) {
        this.baslangicTarihi = baslangicTarihi;
    }

    public LocalDateTime getPlanlananBitisTarihi() {
        return planlananBitisTarihi;
    }

    public void setPlanlananBitisTarihi(LocalDateTime planlananBitisTarihi) {
        this.planlananBitisTarihi = planlananBitisTarihi;
    }

    public LocalDateTime getGercekBitisTarihi() {
        return gercekBitisTarihi;
    }

    public void setGercekBitisTarihi(LocalDateTime gercekBitisTarihi) {
        this.gercekBitisTarihi = gercekBitisTarihi;
    }

    public BigDecimal getTahminiMaliyet() {
        return tahminiMaliyet;
    }

    public void setTahminiMaliyet(BigDecimal tahminiMaliyet) {
        this.tahminiMaliyet = tahminiMaliyet;
    }

    public BigDecimal getGercekMaliyet() {
        return gercekMaliyet;
    }

    public void setGercekMaliyet(BigDecimal gercekMaliyet) {
        this.gercekMaliyet = gercekMaliyet;
    }

    public String getSorumluPersonel() {
        return sorumluPersonel;
    }

    public void setSorumluPersonel(String sorumluPersonel) {
        this.sorumluPersonel = sorumluPersonel;
    }

    public String getTedarikciFirma() {
        return tedarikciFirma;
    }

    public void setTedarikciFirma(String tedarikciFirma) {
        this.tedarikciFirma = tedarikciFirma;
    }

    public String getTedarikciTelefon() {
        return tedarikciTelefon;
    }

    public void setTedarikciTelefon(String tedarikciTelefon) {
        this.tedarikciTelefon = tedarikciTelefon;
    }

    public Integer getGarantiSuresiAy() {
        return garantiSuresiAy;
    }

    public void setGarantiSuresiAy(Integer garantiSuresiAy) {
        this.garantiSuresiAy = garantiSuresiAy;
        if (garantiSuresiAy != null && garantiSuresiAy > 0) {
            this.garantiBitisTarihi = LocalDateTime.now().plusMonths(garantiSuresiAy);
        }
    }

    public LocalDateTime getGarantiBitisTarihi() {
        return garantiBitisTarihi;
    }

    public void setGarantiBitisTarihi(LocalDateTime garantiBitisTarihi) {
        this.garantiBitisTarihi = garantiBitisTarihi;
    }

    public String getNotlar() {
        return notlar;
    }

    public void setNotlar(String notlar) {
        this.notlar = notlar;
    }

    public LocalDateTime getOlusturmaTarihi() {
        return olusturmaTarihi;
    }

    public void setOlusturmaTarihi(LocalDateTime olusturmaTarihi) {
        this.olusturmaTarihi = olusturmaTarihi;
    }

    public LocalDateTime getGuncellemeTarihi() {
        return guncellemeTarihi;
    }

    public void setGuncellemeTarihi(LocalDateTime guncellemeTarihi) {
        this.guncellemeTarihi = guncellemeTarihi;
    }

    public String getOlusturanKullanici() {
        return olusturanKullanici;
    }

    public void setOlusturanKullanici(String olusturanKullanici) {
        this.olusturanKullanici = olusturanKullanici;
    }

    // Helper methods
    public boolean isGecikmisMi() {
        if (durum == BakimDurumu.TAMAMLANDI) {
            return false;
        }
        return planlananBitisTarihi != null && LocalDateTime.now().isAfter(planlananBitisTarihi);
    }

    public boolean isGarantiDahilindeMi() {
        return garantiBitisTarihi != null && LocalDateTime.now().isBefore(garantiBitisTarihi);
    }

    public void tamamla(BigDecimal gercekMaliyet) {
        this.durum = BakimDurumu.TAMAMLANDI;
        this.gercekBitisTarihi = LocalDateTime.now();
        this.gercekMaliyet = gercekMaliyet;
    }

    @Override
    public String toString() {
        return "BakimVeOnarim{" +
                "bakimId=" + bakimId +
                ", baslik='" + baslik + '\'' +
                ", kategori=" + kategori +
                ", durum=" + durum +
                ", oncelik=" + oncelik +
                ", baslangicTarihi=" + baslangicTarihi +
                '}';
    }

    // Enum sınıfları
    public enum BakimKategorisi {
        ELEKTRIK("Elektrik İşleri"),
        TESISATI("Tesisat İşleri"),
        BOYAMA("Boyama İşleri"),
        ZEMIN("Zemin/Döşeme İşleri"),
        PENCERE_KAPI("Pencere/Kapı İşleri"),
        ISITMA_SOGUTMA("Isıtma/Soğutma Sistemi"),
        CATI("Çatı İşleri"),
        BAHCE_PEYZAJ("Bahçe/Peyzaj İşleri"),
        TEMIZLIK("Temizlik İşleri"),
        GUVENLIK("Güvenlik Sistemi"),
        DIGER("Diğer");

        private final String aciklama;

        BakimKategorisi(String aciklama) {
            this.aciklama = aciklama;
        }

        public String getAciklama() {
            return aciklama;
        }
    }

    public enum OncelikSeviyesi {
        DUSUK("Düşük", "#28a745"),
        ORTA("Orta", "#ffc107"),
        YUKSEK("Yüksek", "#fd7e14"),
        KRITIK("Kritik", "#dc3545");

        private final String aciklama;
        private final String renk;

        OncelikSeviyesi(String aciklama, String renk) {
            this.aciklama = aciklama;
            this.renk = renk;
        }

        public String getAciklama() {
            return aciklama;
        }

        public String getRenk() {
            return renk;
        }
    }

    public enum BakimDurumu {
        PLANLANMIS("Planlanmış"),
        BASLANDI("Başlandı"),
        DEVAM_EDIYOR("Devam Ediyor"),
        TAMAMLANDI("Tamamlandı"),
        IPTAL_EDILDI("İptal Edildi"),
        BEKLEMEDE("Beklemede");

        private final String aciklama;

        BakimDurumu(String aciklama) {
            this.aciklama = aciklama;
        }

        public String getAciklama() {
            return aciklama;
        }

        public boolean isAktif() {
            return this != TAMAMLANDI && this != IPTAL_EDILDI;
        }
    }
}