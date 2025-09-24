package com.kgyp.kgypsystem.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "gayrimenkul_varliklari")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class GayrimenkulVarligi {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID varlikId;

    @Column(nullable = false)
    private String adres;

    // ✅ YENİ ALANLAR
    @Column(name = "isyeri_adi", nullable = false)
    private String isyeriAdi;

    @Column(name = "hizmet_turu", nullable = false)
    @Enumerated(EnumType.STRING)
    private HizmetTuru hizmetTuru;

    // ✅ EKLENDİ: Tarih field'ları
    @Column(name = "olusturma_tarihi", nullable = false, updatable = false)
    private LocalDateTime olusturmaTarihi;

    @Column(name = "guncelleme_tarihi")
    private LocalDateTime guncellemeTarihi;

    // Mevcut alanlar
    private String googleMapsLink;
    private String sehir;
    private String ilce;
    private String tapuNo;
    private Double brutM2;
    private Double netM2;
    private String degerlemeRaporuLink;

    @Enumerated(EnumType.STRING)
    private KullanimDurumu kullanimDurumu;

    private LocalDate enSonDegerlemeTarihi;
    private BigDecimal enSonDegerlemeTutari;

    // ✅ EKLENDİ: JPA Lifecycle methods
    @PrePersist
    protected void onCreate() {
        olusturmaTarihi = LocalDateTime.now();
        guncellemeTarihi = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        guncellemeTarihi = LocalDateTime.now();
    }

    // İlişkiler
    @OneToMany(mappedBy = "gayrimenkulVarligi", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"gayrimenkulVarligi", "hibernateLazyInitializer", "handler"})
    private List<Sozlesme> sozlesmeler = new ArrayList<>();

    @OneToMany(mappedBy = "gayrimenkulVarligi", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"gayrimenkulVarligi", "hibernateLazyInitializer", "handler"})
    private List<BakimVeOnarim> bakimlar = new ArrayList<>();

    @OneToMany(mappedBy = "gayrimenkulVarligi", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"gayrimenkulVarligi", "hibernateLazyInitializer", "handler"})
    private List<FinansalHareket> finansalHareketler = new ArrayList<>();

    @OneToMany(mappedBy = "gayrimenkulVarligi", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"gayrimenkulVarligi", "hibernateLazyInitializer", "handler"})
    private List<Dokuman> dokumanlar = new ArrayList<>();

    // Constructors
    public GayrimenkulVarligi() {}

    public GayrimenkulVarligi(String isyeriAdi, String adres, String sehir, HizmetTuru hizmetTuru) {
        this.isyeriAdi = isyeriAdi;
        this.adres = adres;
        this.sehir = sehir;
        this.hizmetTuru = hizmetTuru;
    }

    // Getters and Setters
    public UUID getVarlikId() {
        return varlikId;
    }

    public void setVarlikId(UUID varlikId) {
        this.varlikId = varlikId;
    }

    public String getIsyeriAdi() {
        return isyeriAdi;
    }

    public void setIsyeriAdi(String isyeriAdi) {
        this.isyeriAdi = isyeriAdi;
    }

    public HizmetTuru getHizmetTuru() {
        return hizmetTuru;
    }

    public void setHizmetTuru(HizmetTuru hizmetTuru) {
        this.hizmetTuru = hizmetTuru;
    }

    // ✅ EKLENDİ: Yeni field'ların getters/setters
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

    public String getAdres() {
        return adres;
    }

    public void setAdres(String adres) {
        this.adres = adres;
    }

    public String getGoogleMapsLink() {
        return googleMapsLink;
    }

    public void setGoogleMapsLink(String googleMapsLink) {
        this.googleMapsLink = googleMapsLink;
    }

    public String getSehir() {
        return sehir;
    }

    public void setSehir(String sehir) {
        this.sehir = sehir;
    }

    public String getIlce() {
        return ilce;
    }

    public void setIlce(String ilce) {
        this.ilce = ilce;
    }

    public String getTapuNo() {
        return tapuNo;
    }

    public void setTapuNo(String tapuNo) {
        this.tapuNo = tapuNo;
    }

    public Double getBrutM2() {
        return brutM2;
    }

    public void setBrutM2(Double brutM2) {
        this.brutM2 = brutM2;
    }

    public Double getNetM2() {
        return netM2;
    }

    public void setNetM2(Double netM2) {
        this.netM2 = netM2;
    }

    public String getDegerlemeRaporuLink() {
        return degerlemeRaporuLink;
    }

    public void setDegerlemeRaporuLink(String degerlemeRaporuLink) {
        this.degerlemeRaporuLink = degerlemeRaporuLink;
    }

    public KullanimDurumu getKullanimDurumu() {
        return kullanimDurumu;
    }

    public void setKullanimDurumu(KullanimDurumu kullanimDurumu) {
        this.kullanimDurumu = kullanimDurumu;
    }

    public LocalDate getEnSonDegerlemeTarihi() {
        return enSonDegerlemeTarihi;
    }

    public void setEnSonDegerlemeTarihi(LocalDate enSonDegerlemeTarihi) {
        this.enSonDegerlemeTarihi = enSonDegerlemeTarihi;
    }

    public BigDecimal getEnSonDegerlemeTutari() {
        return enSonDegerlemeTutari;
    }

    public void setEnSonDegerlemeTutari(BigDecimal enSonDegerlemeTutari) {
        this.enSonDegerlemeTutari = enSonDegerlemeTutari;
    }

    public List<Sozlesme> getSozlesmeler() {
        return sozlesmeler;
    }

    public void setSozlesmeler(List<Sozlesme> sozlesmeler) {
        this.sozlesmeler = sozlesmeler;
    }

    public List<BakimVeOnarim> getBakimlar() {
        return bakimlar;
    }

    public void setBakimlar(List<BakimVeOnarim> bakimlar) {
        this.bakimlar = bakimlar;
    }

    public List<FinansalHareket> getFinansalHareketler() {
        return finansalHareketler;
    }

    public void setFinansalHareketler(List<FinansalHareket> finansalHareketler) {
        this.finansalHareketler = finansalHareketler;
    }

    public List<Dokuman> getDokumanlar() {
        return dokumanlar;
    }

    public void setDokumanlar(List<Dokuman> dokumanlar) {
        this.dokumanlar = dokumanlar;
    }

    // ✅ HELPER METODLAR - PTT'ye özel

    /**
     * Bu gayrimenkule ait aktif sözleşmeyi döndürür
     */
    public Sozlesme getAktifSozlesme() {
        return sozlesmeler.stream()
                .filter(s -> s.getAktifMi() != null && s.getAktifMi())
                .findFirst()
                .orElse(null);
    }

    /**
     * Bu gayrimenkule ait toplam bakım maliyetini hesaplar
     */
    public BigDecimal getToplamBakimMaliyeti() {
        return bakimlar.stream()
                .filter(b -> b.getGercekMaliyet() != null)
                .map(BakimVeOnarim::getGercekMaliyet)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Bu gayrimenkule ait aktif bakım sayısını döndürür
     */
    public long getAktifBakimSayisi() {
        return bakimlar.stream()
                .filter(b -> b.getDurum() != null && b.getDurum().isAktif())
                .count();
    }

    /**
     * PTT'ye özel - operasyonel birim mi kontrolü
     */
    public boolean isOperasyonelBirim() {
        return hizmetTuru != null && hizmetTuru.isOperasyonel();
    }

    /**
     * PTT'ye özel - destek birimi mi kontrolü
     */
    public boolean isDestekBirimi() {
        return hizmetTuru != null && hizmetTuru.isDestek();
    }

    /**
     * PTT'ye özel - gelir getiren gayrimenkul mi
     */
    public boolean isGelirGetiren() {
        return kullanimDurumu == KullanimDurumu.MAL_SAHIBI_KIRADA;
    }

    /**
     * PTT'ye özel - kira gideri olan gayrimenkul mi
     */
    public boolean isKiraGideriVar() {
        return kullanimDurumu == KullanimDurumu.KIRACISIYIZ;
    }

    /**
     * Tam tanımlayıcı isim (İşyeri Adı - Hizmet Türü)
     */
    public String getTamAdi() {
        return isyeriAdi + " - " + (hizmetTuru != null ? hizmetTuru.getAciklama() : "");
    }

    @Override
    public String toString() {
        return "GayrimenkulVarligi{" +
                "varlikId=" + varlikId +
                ", isyeriAdi='" + isyeriAdi + '\'' +
                ", hizmetTuru=" + hizmetTuru +
                ", adres='" + adres + '\'' +
                ", sehir='" + sehir + '\'' +
                ", kullanimDurumu=" + kullanimDurumu +
                ", brutM2=" + brutM2 +
                '}';
    }
}