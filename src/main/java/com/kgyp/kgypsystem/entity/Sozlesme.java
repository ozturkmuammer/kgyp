package com.kgyp.kgypsystem.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import java.util.List;
import com.kgyp.kgypsystem.entity.KiraArtisMetodu;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "sozlesmeler")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Sozlesme {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID sozlesmeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "varlik_id", nullable = false)
    @JsonIgnoreProperties({"sozlesmeler", "bakimlar", "finansalHareketler", "dokumanlar", "hibernateLazyInitializer", "handler"})
    private GayrimenkulVarligi gayrimenkulVarligi;

    @Column(nullable = false)
    private String kiraciAdi;

    @Column(nullable = false)
    private String kiralayanAdi;

    @Column(nullable = false)
    private LocalDate sozlesmeBaslangicTarihi;

    @Column(nullable = false)
    private LocalDate sozlesmeBitisTarihi;

    @Column(nullable = false)
    private BigDecimal aylikKiraTutari;

    @Enumerated(EnumType.STRING)
    private KiraArtisMetodu kiraArtisMetodu;

    private BigDecimal sabitArtisOrani; // Sabit artış için yüzde

    private Integer kiraOdemeGunu; // Ayın kaçıncı günü (1-31)

    @Column(nullable = false)
    private Boolean aktifMi = true;

    // 2025 yılı için kira artışı yapıldı mı kontrolü
    private Boolean kiraArtisiYapildi2025 = false;

    // Sözleşme notları
    @Column(length = 1000)
    private String notlar;

    // Depozito tutarı
    private BigDecimal depozito;

    // ✅ EKLENDİ: Timestamp alanları
    @Column(name = "olusturma_tarihi", nullable = false, updatable = false)
    private LocalDateTime olusturmaTarihi;

    @Column(name = "guncelleme_tarihi")
    private LocalDateTime guncellemeTarihi;

    // ✅ EKLENDİ: JPA Lifecycle methods
    @PrePersist
    protected void onCreate() {
        olusturmaTarihi = LocalDateTime.now();
        guncellemeTarihi = LocalDateTime.now();
        if (aktifMi == null) {
            aktifMi = true;
        }
        if (kiraArtisiYapildi2025 == null) {
            kiraArtisiYapildi2025 = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        guncellemeTarihi = LocalDateTime.now();
    }

    // Dokuman ilişkisi
    @OneToMany(mappedBy = "sozlesme", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"sozlesme", "hibernateLazyInitializer", "handler"})
    private List<Dokuman> dokumanlar = new ArrayList<>();

    @OneToMany(mappedBy = "sozlesme", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"sozlesme", "hibernateLazyInitializer", "handler"})
    private List<FinansalHareket> finansalHareketler = new ArrayList<>();

    // Constructors
    public Sozlesme() {}

    public Sozlesme(GayrimenkulVarligi gayrimenkulVarligi, String kiraciAdi,
                    String kiralayanAdi, LocalDate baslangic, LocalDate bitis) {
        this.gayrimenkulVarligi = gayrimenkulVarligi;
        this.kiraciAdi = kiraciAdi;
        this.kiralayanAdi = kiralayanAdi;
        this.sozlesmeBaslangicTarihi = baslangic;
        this.sozlesmeBitisTarihi = bitis;
    }

    // Getter/Setter methods
    public UUID getSozlesmeId() {
        return sozlesmeId;
    }

    public void setSozlesmeId(UUID sozlesmeId) {
        this.sozlesmeId = sozlesmeId;
    }

    public UUID getId() {
        return sozlesmeId;
    }

    public void setId(UUID id) {
        this.sozlesmeId = id;
    }

    public GayrimenkulVarligi getGayrimenkulVarligi() {
        return gayrimenkulVarligi;
    }

    public void setGayrimenkulVarligi(GayrimenkulVarligi gayrimenkulVarligi) {
        this.gayrimenkulVarligi = gayrimenkulVarligi;
    }

    public String getKiraciAdi() {
        return kiraciAdi;
    }

    public void setKiraciAdi(String kiraciAdi) {
        this.kiraciAdi = kiraciAdi;
    }

    public String getKiralayanAdi() {
        return kiralayanAdi;
    }

    public void setKiralayanAdi(String kiralayanAdi) {
        this.kiralayanAdi = kiralayanAdi;
    }

    public LocalDate getSozlesmeBaslangicTarihi() {
        return sozlesmeBaslangicTarihi;
    }

    public void setSozlesmeBaslangicTarihi(LocalDate sozlesmeBaslangicTarihi) {
        this.sozlesmeBaslangicTarihi = sozlesmeBaslangicTarihi;
    }

    public LocalDate getSozlesmeBitisTarihi() {
        return sozlesmeBitisTarihi;
    }

    public void setSozlesmeBitisTarihi(LocalDate sozlesmeBitisTarihi) {
        this.sozlesmeBitisTarihi = sozlesmeBitisTarihi;
    }

    public BigDecimal getAylikKiraTutari() {
        return aylikKiraTutari;
    }

    public void setAylikKiraTutari(BigDecimal aylikKiraTutari) {
        this.aylikKiraTutari = aylikKiraTutari;
    }

    public KiraArtisMetodu getKiraArtisMetodu() {
        return kiraArtisMetodu;
    }

    public void setKiraArtisMetodu(KiraArtisMetodu kiraArtisMetodu) {
        this.kiraArtisMetodu = kiraArtisMetodu;
    }

    public BigDecimal getSabitArtisOrani() {
        return sabitArtisOrani;
    }

    public void setSabitArtisOrani(BigDecimal sabitArtisOrani) {
        this.sabitArtisOrani = sabitArtisOrani;
    }

    public Integer getKiraOdemeGunu() {
        return kiraOdemeGunu;
    }

    public void setKiraOdemeGunu(Integer kiraOdemeGunu) {
        this.kiraOdemeGunu = kiraOdemeGunu;
    }

    public Boolean getAktifMi() {
        return aktifMi;
    }

    public void setAktifMi(Boolean aktifMi) {
        this.aktifMi = aktifMi;
    }

    public Boolean getKiraArtisiYapildi2025() {
        return kiraArtisiYapildi2025;
    }

    public void setKiraArtisiYapildi2025(Boolean kiraArtisiYapildi2025) {
        this.kiraArtisiYapildi2025 = kiraArtisiYapildi2025;
    }

    public String getNotlar() {
        return notlar;
    }

    public void setNotlar(String notlar) {
        this.notlar = notlar;
    }

    public BigDecimal getDepozito() {
        return depozito;
    }

    public void setDepozito(BigDecimal depozito) {
        this.depozito = depozito;
    }

    // ✅ EKLENDİ: Yeni timestamp alanlarının getters/setters
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

    public List<Dokuman> getDokumanlar() {
        return dokumanlar;
    }

    public void setDokumanlar(List<Dokuman> dokumanlar) {
        this.dokumanlar = dokumanlar;
    }

    public List<FinansalHareket> getFinansalHareketler() {
        return finansalHareketler;
    }

    public void setFinansalHareketler(List<FinansalHareket> finansalHareketler) {
        this.finansalHareketler = finansalHareketler;
    }

    // Helper methods
    public boolean isSozlesmeBitiyor(int gunSayisi) {
        return LocalDate.now().plusDays(gunSayisi).isAfter(sozlesmeBitisTarihi) ||
                LocalDate.now().plusDays(gunSayisi).isEqual(sozlesmeBitisTarihi);
    }

    public boolean isKiraOdemeZamani() {
        LocalDate bugun = LocalDate.now();
        return bugun.getDayOfMonth() == kiraOdemeGunu;
    }

    @Override
    public String toString() {
        return "Sozlesme{" +
                "sozlesmeId=" + sozlesmeId +
                ", kiraciAdi='" + kiraciAdi + '\'' +
                ", kiralayanAdi='" + kiralayanAdi + '\'' +
                ", sozlesmeBaslangicTarihi=" + sozlesmeBaslangicTarihi +
                ", sozlesmeBitisTarihi=" + sozlesmeBitisTarihi +
                ", aylikKiraTutari=" + aylikKiraTutari +
                ", aktifMi=" + aktifMi +
                '}';
    }
}