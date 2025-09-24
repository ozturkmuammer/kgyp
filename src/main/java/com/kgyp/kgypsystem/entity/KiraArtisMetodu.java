package com.kgyp.kgypsystem.entity;

public enum KiraArtisMetodu {
    SABIT("Sabit Oran", "Sozlesmede belirtilen sabit yuzde orani"),
    TUFE("TUFE Endeksli", "TUFE (Tuketici Fiyat Endeksi) verilerine gore"),
    TEFE("TEFE Endeksli", "TEFE (Toptan Esya Fiyat Endeksi) verilerine gore"),
    UFE("UFE Endeksli", "UFE (Uretici Fiyat Endeksi) verilerine gore"),
    MUTABAKAT("Karsilikli Anlasma", "Taraflarin her yil anlasarak belirledigi oran"),
    YOK("Artis Yok", "Kira artisi uygulanmiyor");

    private final String aciklama;
    private final String detay;

    KiraArtisMetodu(String aciklama, String detay) {
        this.aciklama = aciklama;
        this.detay = detay;
    }

    public String getAciklama() {
        return aciklama;
    }

    public String getDetay() {
        return detay;
    }

    public boolean isEndeksli() {
        return this == TUFE || this == TEFE || this == UFE;
    }

    public boolean isSabit() {
        return this == SABIT;
    }

    public boolean isManuel() {
        return this == MUTABAKAT;
    }
}