package com.kgyp.kgypsystem.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class SozlesmeRequest {
    private UUID varlikId;
    private String kiraciAdi;
    private String kiralayanAdi;
    private LocalDate sozlesmeBaslangicTarihi;
    private LocalDate sozlesmeBitisTarihi;
    private BigDecimal aylikKiraTutari;
    private KiraArtisMetodu kiraArtisMetodu;
    private Integer kiraOdemeGunu;

    public UUID getVarlikId() { return varlikId; }
    public void setVarlikId(UUID varlikId) { this.varlikId = varlikId; }

    public String getKiraciAdi() { return kiraciAdi; }
    public void setKiraciAdi(String kiraciAdi) { this.kiraciAdi = kiraciAdi; }

    public String getKiralayanAdi() { return kiralayanAdi; }
    public void setKiralayanAdi(String kiralayanAdi) { this.kiralayanAdi = kiralayanAdi; }

    public LocalDate getSozlesmeBaslangicTarihi() { return sozlesmeBaslangicTarihi; }
    public void setSozlesmeBaslangicTarihi(LocalDate sozlesmeBaslangicTarihi) { this.sozlesmeBaslangicTarihi = sozlesmeBaslangicTarihi; }

    public LocalDate getSozlesmeBitisTarihi() { return sozlesmeBitisTarihi; }
    public void setSozlesmeBitisTarihi(LocalDate sozlesmeBitisTarihi) { this.sozlesmeBitisTarihi = sozlesmeBitisTarihi; }

    public BigDecimal getAylikKiraTutari() { return aylikKiraTutari; }
    public void setAylikKiraTutari(BigDecimal aylikKiraTutari) { this.aylikKiraTutari = aylikKiraTutari; }

    public KiraArtisMetodu getKiraArtisMetodu() { return kiraArtisMetodu; }
    public void setKiraArtisMetodu(KiraArtisMetodu kiraArtisMetodu) { this.kiraArtisMetodu = kiraArtisMetodu; }

    public Integer getKiraOdemeGunu() { return kiraOdemeGunu; }
    public void setKiraOdemeGunu(Integer kiraOdemeGunu) { this.kiraOdemeGunu = kiraOdemeGunu; }
}