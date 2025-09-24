package com.kgyp.kgypsystem.entity;

public enum HizmetTuru {
    MUDURLUK("Müdürlük"),
    SUBE("Şube"),
    PIM("PİM (Posta İşleme Müdürlüğü)"),
    UPIM("UPİM (Uluslararası Posta İşleme Müdürlüğü)"),
    UKIM("UKİM (Uluslararası Kargo İşleme Müdürlüğü)"),
    KIM("KİM (Kargo İşleme Müdürlüğü)"),
    PDM("PDM (Posta Dağıtım Müdürlüğü)"),
    KDM("KDM (Kargo Dağıtım Müdürlüğü)"),
    DTM("DTM (Dağıtım ve Toplama Müdürlüğü)"),
    ATM("ATM"),
    ARSA("Arsa"),
    LOJMAN("Lojman"),
    BAZ_ISTASYONU("Baz İstasyonu"),
    OTOPARK("Otopark"),
    EK_BINA("Ek Bina");

    private final String aciklama;

    HizmetTuru(String aciklama) {
        this.aciklama = aciklama;
    }

    public String getAciklama() {
        return aciklama;
    }

    public boolean isOperasyonel() {
        return this == MUDURLUK || this == SUBE || this == PIM ||
                this == UPIM || this == UKIM || this == KIM ||
                this == PDM || this == KDM || this == DTM;
    }

    public boolean isDestek() {
        return this == ATM || this == ARSA || this == LOJMAN ||
                this == BAZ_ISTASYONU || this == OTOPARK || this == EK_BINA;
    }

    public boolean isPostaIleMu() {
        return this == PIM || this == UPIM || this == PDM;
    }

    public boolean isKargoIleMu() {
        return this == UKIM || this == KIM || this == KDM;
    }
}