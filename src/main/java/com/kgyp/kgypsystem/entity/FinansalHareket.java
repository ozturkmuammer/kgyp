package com.kgyp.kgypsystem.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "finansal_hareketler")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class FinansalHareket {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID hareketId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "varlik_id", nullable = false)
    @JsonIgnoreProperties({"sozlesmeler", "bakimlar", "finansalHareketler", "dokumanlar", "hibernateLazyInitializer", "handler"})
    private GayrimenkulVarligi gayrimenkulVarligi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sozlesme_id")
    @JsonIgnoreProperties({"gayrimenkulVarligi", "finansalHareketler", "dokumanlar", "hibernateLazyInitializer", "handler"})
    private Sozlesme sozlesme;

    @Column(name = "hareket_tipi", nullable = false)
    @Enumerated(EnumType.STRING)
    private HareketTipi hareketTipi;

    @Column(name = "tutar", nullable = false, precision = 15, scale = 2)
    private BigDecimal tutar;

    @Column(name = "aciklama", length = 500)
    private String aciklama;

    @Column(name = "hareket_tarihi", nullable = false)
    private LocalDateTime hareketTarihi;

    @Column(name = "odeme_yontemi")
    @Enumerated(EnumType.STRING)
    private OdemeYontemi odemeYontemi;

    @Column(name = "fatura_no", length = 100)
    private String faturaNo;

    @Column(name = "tedarikci_firma", length = 200)
    private String tedarikci; // Gider için

    @Column(name = "kira_donemi", length = 20)
    private String kiraDonemi; // "2025-01", "2025-02" formatında

    @Column(name = "onaylanmis", nullable = false)
    private Boolean onaylanmis = false;

    @Column(name = "onayleyen_kullanici", length = 100)
    private String onaylayanKullanici;

    @Column(name = "onay_tarihi")
    private LocalDateTime onayTarihi;

    @PrePersist
    protected void onCreate() {
        if (hareketTarihi == null) {
            hareketTarihi = LocalDateTime.now();
        }
        if (onaylanmis == null) {
            onaylanmis = false;
        }
    }

    // Constructors
    public FinansalHareket() {}

    public FinansalHareket(GayrimenkulVarligi gayrimenkulVarligi, HareketTipi hareketTipi,
                           BigDecimal tutar, String aciklama) {
        this.gayrimenkulVarligi = gayrimenkulVarligi;
        this.hareketTipi = hareketTipi;
        this.tutar = tutar;
        this.aciklama = aciklama;
        this.hareketTarihi = LocalDateTime.now();
        this.onaylanmis = false;
    }

    // Getters and Setters
    public UUID getHareketId() {
        return hareketId;
    }

    public void setHareketId(UUID hareketId) {
        this.hareketId = hareketId;
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

    public HareketTipi getHareketTipi() {
        return hareketTipi;
    }

    public void setHareketTipi(HareketTipi hareketTipi) {
        this.hareketTipi = hareketTipi;
    }

    public BigDecimal getTutar() {
        return tutar;
    }

    public void setTutar(BigDecimal tutar) {
        this.tutar = tutar;
    }

    public String getAciklama() {
        return aciklama;
    }

    public void setAciklama(String aciklama) {
        this.aciklama = aciklama;
    }

    public LocalDateTime getHareketTarihi() {
        return hareketTarihi;
    }

    public void setHareketTarihi(LocalDateTime hareketTarihi) {
        this.hareketTarihi = hareketTarihi;
    }

    public OdemeYontemi getOdemeYontemi() {
        return odemeYontemi;
    }

    public void setOdemeYontemi(OdemeYontemi odemeYontemi) {
        this.odemeYontemi = odemeYontemi;
    }

    public String getFaturaNo() {
        return faturaNo;
    }

    public void setFaturaNo(String faturaNo) {
        this.faturaNo = faturaNo;
    }

    public String getTedarikci() {
        return tedarikci;
    }

    public void setTedarikci(String tedarikci) {
        this.tedarikci = tedarikci;
    }

    public String getKiraDonemi() {
        return kiraDonemi;
    }

    public void setKiraDonemi(String kiraDonemi) {
        this.kiraDonemi = kiraDonemi;
    }

    public Boolean getOnaylanmis() {
        return onaylanmis;
    }

    public void setOnaylanmis(Boolean onaylanmis) {
        this.onaylanmis = onaylanmis;
    }

    public String getOnaylayanKullanici() {
        return onaylayanKullanici;
    }

    public void setOnaylayanKullanici(String onaylayanKullanici) {
        this.onaylayanKullanici = onaylayanKullanici;
    }

    public LocalDateTime getOnayTarihi() {
        return onayTarihi;
    }

    public void setOnayTarihi(LocalDateTime onayTarihi) {
        this.onayTarihi = onayTarihi;
    }

    // Helper methods
    public boolean isGelir() {
        return hareketTipi == HareketTipi.KIRA_GELIRI ||
                hareketTipi == HareketTipi.DEPOZITO_ALINDI ||
                hareketTipi == HareketTipi.DIGER_GELIR;
    }

    public boolean isGider() {
        return !isGelir();
    }

    public void onayla(String onaylayanKullanici) {
        this.onaylanmis = true;
        this.onaylayanKullanici = onaylayanKullanici;
        this.onayTarihi = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "FinansalHareket{" +
                "hareketId=" + hareketId +
                ", hareketTipi=" + hareketTipi +
                ", tutar=" + tutar +
                ", aciklama='" + aciklama + '\'' +
                ", hareketTarihi=" + hareketTarihi +
                ", onaylanmis=" + onaylanmis +
                '}';
    }

    // Enum sınıfları
    public enum HareketTipi {
        KIRA_GELIRI("Kira Geliri"),
        DEPOZITO_ALINDI("Depozito Alındı"),
        DEPOZITO_IADE("Depozito İade Edildi"),
        BAKIM_ONARIM("Bakım ve Onarım Gideri"),
        VERGI_HARCI("Vergi ve Harçlar"),
        SIGORTA("Sigorta Gideri"),
        YONETIM("Yönetim Gideri"),
        AIDAT("Aidat"),
        DIGER_GIDER("Diğer Giderler"),
        DIGER_GELIR("Diğer Gelirler");

        private final String aciklama;

        HareketTipi(String aciklama) {
            this.aciklama = aciklama;
        }

        public String getAciklama() {
            return aciklama;
        }

        public boolean isGelir() {
            return this == KIRA_GELIRI || this == DEPOZITO_ALINDI || this == DIGER_GELIR;
        }
    }

    public enum OdemeYontemi {
        NAKIT("Nakit"),
        BANKA_HAVALESI("Banka Havalesi"),
        KREDI_KARTI("Kredi Kartı"),
        CEK("Çek"),
        SENET("Senet"),
        DIGER("Diğer");

        private final String aciklama;

        OdemeYontemi(String aciklama) {
            this.aciklama = aciklama;
        }

        public String getAciklama() {
            return aciklama;
        }
    }
}