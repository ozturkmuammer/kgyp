package com.kgyp.kgypsystem.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "bildirimler")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Bildirim {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID bildirimId;

    @Column(name = "baslik", nullable = false)
    private String baslik;

    @Column(name = "mesaj", nullable = false, length = 1000)
    private String mesaj;

    @Column(name = "bildirim_tipi", nullable = false)
    @Enumerated(EnumType.STRING)
    private BildirimTipi bildirimTipi;

    @Column(name = "oncelik", nullable = false)
    @Enumerated(EnumType.STRING)
    private OncelikSeviyesi oncelik;

    @Column(name = "alici_email", nullable = false)
    private String aliciEmail;

    @Column(name = "olusturma_tarihi", nullable = false)
    private LocalDateTime olusturmaTarihi;

    @Column(name = "gonderim_tarihi")
    private LocalDateTime gonderimTarihi;

    @Column(name = "durum", nullable = false)
    @Enumerated(EnumType.STRING)
    private BildirimDurumu durum = BildirimDurumu.BEKLEMEDE;

    @Column(name = "sozlesme_id")
    private UUID sozlesmeId;

    @Column(name = "gayrimenkul_id")
    private UUID gayrimenkulId;

    @PrePersist
    protected void onCreate() {
        olusturmaTarihi = LocalDateTime.now();
        if (durum == null) {
            durum = BildirimDurumu.BEKLEMEDE;
        }
    }

    // Constructors
    public Bildirim() {}

    public Bildirim(String baslik, String mesaj, BildirimTipi tip, String aliciEmail) {
        this.baslik = baslik;
        this.mesaj = mesaj;
        this.bildirimTipi = tip;
        this.aliciEmail = aliciEmail;
        this.oncelik = OncelikSeviyesi.NORMAL;
    }

    // Getters and Setters
    public UUID getBildirimId() { return bildirimId; }
    public void setBildirimId(UUID bildirimId) { this.bildirimId = bildirimId; }

    public String getBaslik() { return baslik; }
    public void setBaslik(String baslik) { this.baslik = baslik; }

    public String getMesaj() { return mesaj; }
    public void setMesaj(String mesaj) { this.mesaj = mesaj; }

    public BildirimTipi getBildirimTipi() { return bildirimTipi; }
    public void setBildirimTipi(BildirimTipi bildirimTipi) { this.bildirimTipi = bildirimTipi; }

    public OncelikSeviyesi getOncelik() { return oncelik; }
    public void setOncelik(OncelikSeviyesi oncelik) { this.oncelik = oncelik; }

    public String getAliciEmail() { return aliciEmail; }
    public void setAliciEmail(String aliciEmail) { this.aliciEmail = aliciEmail; }

    public LocalDateTime getOlusturmaTarihi() { return olusturmaTarihi; }
    public void setOlusturmaTarihi(LocalDateTime olusturmaTarihi) { this.olusturmaTarihi = olusturmaTarihi; }

    public LocalDateTime getGonderimTarihi() { return gonderimTarihi; }
    public void setGonderimTarihi(LocalDateTime gonderimTarihi) { this.gonderimTarihi = gonderimTarihi; }

    public BildirimDurumu getDurum() { return durum; }
    public void setDurum(BildirimDurumu durum) { this.durum = durum; }

    public UUID getSozlesmeId() { return sozlesmeId; }
    public void setSozlesmeId(UUID sozlesmeId) { this.sozlesmeId = sozlesmeId; }

    public UUID getGayrimenkulId() { return gayrimenkulId; }
    public void setGayrimenkulId(UUID gayrimenkulId) { this.gayrimenkulId = gayrimenkulId; }

    public void gonder() {
        this.durum = BildirimDurumu.GONDERILDI;
        this.gonderimTarihi = LocalDateTime.now();
    }

    // Enum sınıfları
    public enum BildirimTipi {
        SOZLESME_BITIYOR("Sözleşme Bitiyor"),
        KIRA_ODEME_ZAMANI("Kira Ödeme Zamanı"),
        KIRA_ARTISI_GEREKLI("Kira Artışı Yapılmalı"),
        BAKIM_GECIKMESI("Bakım İşi Gecikmede"),
        SISTEM_BILDIRIMI("Sistem Bildirimi");

        private final String aciklama;
        BildirimTipi(String aciklama) { this.aciklama = aciklama; }
        public String getAciklama() { return aciklama; }
    }

    public enum OncelikSeviyesi {
        DUSUK("Düşük"),
        NORMAL("Normal"),
        YUKSEK("Yüksek"),
        ACIL("Acil"),
        KRITIK("Kritik");

        private final String aciklama;
        OncelikSeviyesi(String aciklama) { this.aciklama = aciklama; }
        public String getAciklama() { return aciklama; }
    }

    public enum BildirimDurumu {
        BEKLEMEDE("Beklemede"),
        GONDERILDI("Gönderildi"),
        HATALI("Hatalı");

        private final String aciklama;
        BildirimDurumu(String aciklama) { this.aciklama = aciklama; }
        public String getAciklama() { return aciklama; }
    }
}