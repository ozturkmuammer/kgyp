package com.kgyp.kgypsystem.entity;

public enum KullanimDurumu {
    KIRACISIYIZ("Kiracısıyız"),
    MAL_SAHIBI_ATIL("Mal Sahibi - Atıl"),
    MAL_SAHIBI_KIRADA("Mal Sahibi - Kirada"),
    TAHSISLI_KULLANIM("Tahsisli Kullanım");

    private final String aciklama;

    KullanimDurumu(String aciklama) {
        this.aciklama = aciklama;
    }

    public String getAciklama() {
        return aciklama;
    }

    public boolean isMalSahibi() {
        return this == MAL_SAHIBI_ATIL || this == MAL_SAHIBI_KIRADA;
    }

    public boolean isKiracı() {
        return this == KIRACISIYIZ;
    }

    public boolean isAtil() {
        return this == MAL_SAHIBI_ATIL;
    }

    public boolean isKirada() {
        return this == MAL_SAHIBI_KIRADA;
    }

    public boolean isTahsisli() {
        return this == TAHSISLI_KULLANIM;
    }
}