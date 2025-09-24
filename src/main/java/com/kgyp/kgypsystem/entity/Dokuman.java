package com.kgyp.kgypsystem.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "dokuman")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Dokuman {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dosya_adi", nullable = false, length = 255)
    private String dosyaAdi;

    @Column(name = "dosya_yolu", nullable = false, length = 500)
    private String dosyaYolu;

    @Column(name = "dosya_tipi", nullable = false, length = 10)
    private String dosyaTipi; // PDF, JPG, PNG, DOC, etc.

    @Column(name = "dosya_boyutu", nullable = false)
    private Long dosyaBoyutu; // byte cinsinden

    @Column(name = "kategori", nullable = false)
    @Enumerated(EnumType.STRING)
    private DokumanKategorisi kategori;

    @Column(name = "aciklama", length = 500)
    private String aciklama;

    // İlişkiler - Bir dosya ya gayrimenkule ya da sözleşmeye ait olabilir
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gayrimenkul_varligi_id")
    @JsonIgnoreProperties({"sozlesmeler", "bakimlar", "finansalHareketler", "dokumanlar", "hibernateLazyInitializer", "handler"})
    private GayrimenkulVarligi gayrimenkulVarligi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sozlesme_id")
    @JsonIgnoreProperties({"gayrimenkulVarligi", "finansalHareketler", "dokumanlar", "hibernateLazyInitializer", "handler"})
    private Sozlesme sozlesme;

    @Column(name = "yuklenme_tarihi", nullable = false, updatable = false)
    private LocalDateTime yuklenmeTarihi;

    @Column(name = "yukleyen_kullanici", length = 100)
    private String yukleyenKullanici;

    @Column(name = "aktif", nullable = false)
    private Boolean aktif = true;

    @PrePersist
    protected void onCreate() {
        yuklenmeTarihi = LocalDateTime.now();
        if (aktif == null) {
            aktif = true;
        }
    }

    // Constructors
    public Dokuman() {
    }

    public Dokuman(String dosyaAdi, String dosyaYolu, String dosyaTipi, DokumanKategorisi kategori) {
        this.dosyaAdi = dosyaAdi;
        this.dosyaYolu = dosyaYolu;
        this.dosyaTipi = dosyaTipi;
        this.kategori = kategori;
        this.aktif = true;
    }

    // Getter/Setter metodları
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDosyaAdi() {
        return dosyaAdi;
    }

    public void setDosyaAdi(String dosyaAdi) {
        this.dosyaAdi = dosyaAdi;
    }

    public String getDosyaYolu() {
        return dosyaYolu;
    }

    public void setDosyaYolu(String dosyaYolu) {
        this.dosyaYolu = dosyaYolu;
    }

    public String getDosyaTipi() {
        return dosyaTipi;
    }

    public void setDosyaTipi(String dosyaTipi) {
        this.dosyaTipi = dosyaTipi;
    }

    public Long getDosyaBoyutu() {
        return dosyaBoyutu;
    }

    // Dosya boyutu kontrolü ile setter
    public void setDosyaBoyutu(Long dosyaBoyutu) {
        final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
        if (dosyaBoyutu != null && dosyaBoyutu > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Dosya boyutu 10MB'ı geçemez");
        }
        this.dosyaBoyutu = dosyaBoyutu;
    }

    public DokumanKategorisi getKategori() {
        return kategori;
    }

    public void setKategori(DokumanKategorisi kategori) {
        this.kategori = kategori;
    }

    public String getAciklama() {
        return aciklama;
    }

    public void setAciklama(String aciklama) {
        this.aciklama = aciklama;
    }

    public GayrimenkulVarligi getGayrimenkulVarligi() {
        return gayrimenkulVarligi;
    }

    public void setGayrimenkulVarligi(GayrimenkulVarligi gayrimenkulVarligi) {
        this.gayrimenkulVarligi = gayrimenkulVarligi;
    }

    public Sozlesme getSozlesme() {
        return sozlesme;
    }

    public void setSozlesme(Sozlesme sozlesme) {
        this.sozlesme = sozlesme;
    }

    public LocalDateTime getYuklenmeTarihi() {
        return yuklenmeTarihi;
    }

    public void setYuklenmeTarihi(LocalDateTime yuklenmeTarihi) {
        this.yuklenmeTarihi = yuklenmeTarihi;
    }

    public String getYukleyenKullanici() {
        return yukleyenKullanici;
    }

    public void setYukleyenKullanici(String yukleyenKullanici) {
        this.yukleyenKullanici = yukleyenKullanici;
    }

    public Boolean getAktif() {
        return aktif;
    }

    public void setAktif(Boolean aktif) {
        this.aktif = aktif;
    }

    // toString method
    @Override
    public String toString() {
        return "Dokuman{" +
                "id=" + id +
                ", dosyaAdi='" + dosyaAdi + '\'' +
                ", dosyaTipi='" + dosyaTipi + '\'' +
                ", kategori=" + kategori +
                ", yuklenmeTarihi=" + yuklenmeTarihi +
                ", aktif=" + aktif +
                '}';
    }

    // Enum sınıfı
    public enum DokumanKategorisi {
        TAPU("Tapu Belgesi"),
        SOZLESME("Sözleşme Belgesi"),
        FOTOGRAF("Fotoğraf"),
        RUHSAT("Ruhsat/İzin Belgesi"),
        DEGERME_RAPORU("Değerleme Raporu"),
        BAKIM_ONARIM("Bakım ve Onarım"),
        DIGER("Diğer");

        private final String aciklama;

        DokumanKategorisi(String aciklama) {
            this.aciklama = aciklama;
        }

        public String getAciklama() {
            return aciklama;
        }
    }
}