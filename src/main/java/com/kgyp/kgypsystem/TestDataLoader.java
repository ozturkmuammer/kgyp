package com.kgyp.kgypsystem.config;

import com.kgyp.kgypsystem.entity.*;
import com.kgyp.kgypsystem.service.GayrimenkulVarligiService;
import com.kgyp.kgypsystem.service.SozlesmeService;
import com.kgyp.kgypsystem.service.BakimVeOnarimService;
import com.kgyp.kgypsystem.service.FinansalHareketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class TestDataLoader implements CommandLineRunner {

    @Autowired
    private GayrimenkulVarligiService gayrimenkulService;

    @Autowired
    private SozlesmeService sozlesmeService;

    @Autowired
    private BakimVeOnarimService bakimService;

    @Autowired
    private FinansalHareketService finansalService;

    @Override
    public void run(String... args) throws Exception {

        // Sadece ilk çalıştırmada veri yükle
        if (gayrimenkulService.toplamGayrimenkulSayisi() > 0) {
            System.out.println("Test verisi zaten mevcut, yükleme atlandı.");
            return;
        }

        System.out.println("🏗️ PTT Gayrimenkul test verisi yükleniyor...");

        // 1. PTT GAYRİMENKUL VARLİKLARI
        UUID gayrimenkul1Id = createPTTMudurluk();
        UUID gayrimenkul2Id = createPTTSube();
        UUID gayrimenkul3Id = createPTTPIM();
        UUID gayrimenkul4Id = createPTTLojman();
        UUID gayrimenkul5Id = createPTTATM();
        UUID gayrimenkul6Id = createPTTArsa();
        UUID gayrimenkul7Id = createPTTKiraciOlunan();

        // 2. SÖZLEŞMELER
        UUID sozlesme1Id = createSozlesme1(gayrimenkul4Id); // Lojman kira sözleşmesi
        UUID sozlesme2Id = createSozlesme2(gayrimenkul7Id); // PTT'nin kiracı olduğu yer

        // 3. BAKIM İŞLERİ
        createBakimIsler(gayrimenkul1Id, gayrimenkul2Id, gayrimenkul3Id);

        // 4. FİNANSAL HAREKETLER
        createFinansalHareketler(gayrimenkul1Id, gayrimenkul2Id, gayrimenkul4Id, gayrimenkul7Id);

        System.out.println("✅ PTT Test verisi başarıyla yüklendi!");
        System.out.println("📊 Dashboard: http://localhost:8080/api/dashboard/kpi");
        System.out.println("🏢 PTT Portföy Özeti: http://localhost:8080/api/gayrimenkul/ptt-portfoy-ozeti");
        System.out.println("🏠 Gayrimenkuller: http://localhost:8080/api/gayrimenkul");
    }

    // ==================== PTT GAYRİMENKUL ÖRNEKLERİ ====================

    private UUID createPTTMudurluk() {
        GayrimenkulVarligi gayrimenkul = new GayrimenkulVarligi();
        gayrimenkul.setIsyeriAdi("İstanbul Anadolu Posta İşleme Müdürlüğü");
        gayrimenkul.setHizmetTuru(HizmetTuru.PIM);
        gayrimenkul.setAdres("Küçükçekmece Mah. Atatürk Bulvarı No:45");
        gayrimenkul.setSehir("İstanbul");
        gayrimenkul.setIlce("Küçükçekmece");
        gayrimenkul.setTapuNo("34-125-789-456");
        gayrimenkul.setBrutM2(2500.0);
        gayrimenkul.setNetM2(2200.0);
        gayrimenkul.setKullanimDurumu(KullanimDurumu.TAHSISLI_KULLANIM);
        gayrimenkul.setEnSonDegerlemeTarihi(LocalDate.now().minusMonths(6));
        gayrimenkul.setEnSonDegerlemeTutari(new BigDecimal("15000000"));
        gayrimenkul.setGoogleMapsLink("https://maps.google.com/?q=41.0082,28.9784");
        gayrimenkul.setDegerlemeRaporuLink("/documents/ptt_pim_degerle_raporu.pdf");

        GayrimenkulVarligi saved = gayrimenkulService.gayrimenkulKaydet(gayrimenkul);
        return saved.getVarlikId();
    }

    private UUID createPTTSube() {
        GayrimenkulVarligi gayrimenkul = new GayrimenkulVarligi();
        gayrimenkul.setIsyeriAdi("Kadıköy PTT Şubesi");
        gayrimenkul.setHizmetTuru(HizmetTuru.SUBE);
        gayrimenkul.setAdres("Kadıköy Mah. Bağdat Cad. No:156");
        gayrimenkul.setSehir("İstanbul");
        gayrimenkul.setIlce("Kadıköy");
        gayrimenkul.setTapuNo("34-234-567-890");
        gayrimenkul.setBrutM2(180.0);
        gayrimenkul.setNetM2(160.0);
        gayrimenkul.setKullanimDurumu(KullanimDurumu.TAHSISLI_KULLANIM);
        gayrimenkul.setEnSonDegerlemeTarihi(LocalDate.now().minusMonths(8));
        gayrimenkul.setEnSonDegerlemeTutari(new BigDecimal("3200000"));
        gayrimenkul.setGoogleMapsLink("https://maps.google.com/?q=40.9925,29.0265");

        GayrimenkulVarligi saved = gayrimenkulService.gayrimenkulKaydet(gayrimenkul);
        return saved.getVarlikId();
    }

    private UUID createPTTPIM() {
        GayrimenkulVarligi gayrimenkul = new GayrimenkulVarligi();
        gayrimenkul.setIsyeriAdi("Ankara Merkez Posta İşleme Müdürlüğü");
        gayrimenkul.setHizmetTuru(HizmetTuru.PIM);
        gayrimenkul.setAdres("Çankaya Mah. Atatürk Bulvarı No:234");
        gayrimenkul.setSehir("Ankara");
        gayrimenkul.setIlce("Çankaya");
        gayrimenkul.setTapuNo("06-456-789-123");
        gayrimenkul.setBrutM2(3200.0);
        gayrimenkul.setNetM2(2900.0);
        gayrimenkul.setKullanimDurumu(KullanimDurumu.TAHSISLI_KULLANIM);
        gayrimenkul.setEnSonDegerlemeTarihi(LocalDate.now().minusMonths(4));
        gayrimenkul.setEnSonDegerlemeTutari(new BigDecimal("18500000"));

        GayrimenkulVarligi saved = gayrimenkulService.gayrimenkulKaydet(gayrimenkul);
        return saved.getVarlikId();
    }

    private UUID createPTTLojman() {
        GayrimenkulVarligi gayrimenkul = new GayrimenkulVarligi();
        gayrimenkul.setIsyeriAdi("PTT Personel Lojmanı A Blok");
        gayrimenkul.setHizmetTuru(HizmetTuru.LOJMAN);
        gayrimenkul.setAdres("Bahçelievler Mah. 5. Cad. No:89 Daire:12");
        gayrimenkul.setSehir("İstanbul");
        gayrimenkul.setIlce("Bahçelievler");
        gayrimenkul.setTapuNo("34-567-890-123");
        gayrimenkul.setBrutM2(120.0);
        gayrimenkul.setNetM2(105.0);
        gayrimenkul.setKullanimDurumu(KullanimDurumu.MAL_SAHIBI_KIRADA);
        gayrimenkul.setEnSonDegerlemeTarihi(LocalDate.now().minusMonths(10));
        gayrimenkul.setEnSonDegerlemeTutari(new BigDecimal("850000"));
        gayrimenkul.setGoogleMapsLink("https://maps.google.com/?q=41.0082,28.9784");
        gayrimenkul.setDegerlemeRaporuLink("/documents/lojman_degerle_raporu.pdf");

        GayrimenkulVarligi saved = gayrimenkulService.gayrimenkulKaydet(gayrimenkul);
        return saved.getVarlikId();
    }

    private UUID createPTTATM() {
        GayrimenkulVarligi gayrimenkul = new GayrimenkulVarligi();
        gayrimenkul.setIsyeriAdi("PTT ATM Noktası - Forum İstanbul");
        gayrimenkul.setHizmetTuru(HizmetTuru.ATM);
        gayrimenkul.setAdres("Bayrampaşa Mah. Forum İstanbul AVM Giriş Katı");
        gayrimenkul.setSehir("İstanbul");
        gayrimenkul.setIlce("Bayrampaşa");
        gayrimenkul.setBrutM2(8.0);
        gayrimenkul.setNetM2(6.0);
        gayrimenkul.setKullanimDurumu(KullanimDurumu.KIRACISIYIZ);
        gayrimenkul.setEnSonDegerlemeTarihi(LocalDate.now().minusMonths(12));
        gayrimenkul.setEnSonDegerlemeTutari(new BigDecimal("50000"));

        GayrimenkulVarligi saved = gayrimenkulService.gayrimenkulKaydet(gayrimenkul);
        return saved.getVarlikId();
    }

    private UUID createPTTArsa() {
        GayrimenkulVarligi gayrimenkul = new GayrimenkulVarligi();
        gayrimenkul.setIsyeriAdi("PTT Yedek Arsa - Pendik");
        gayrimenkul.setHizmetTuru(HizmetTuru.ARSA);
        gayrimenkul.setAdres("Pendik Mah. Sanayi Cad. No:567");
        gayrimenkul.setSehir("İstanbul");
        gayrimenkul.setIlce("Pendik");
        gayrimenkul.setTapuNo("34-789-123-456");
        gayrimenkul.setBrutM2(5000.0);
        gayrimenkul.setNetM2(5000.0);
        gayrimenkul.setKullanimDurumu(KullanimDurumu.MAL_SAHIBI_ATIL);
        gayrimenkul.setEnSonDegerlemeTarihi(LocalDate.now().minusMonths(15));
        gayrimenkul.setEnSonDegerlemeTutari(new BigDecimal("12000000"));

        GayrimenkulVarligi saved = gayrimenkulService.gayrimenkulKaydet(gayrimenkul);
        return saved.getVarlikId();
    }

    private UUID createPTTKiraciOlunan() {
        GayrimenkulVarligi gayrimenkul = new GayrimenkulVarligi();
        gayrimenkul.setIsyeriAdi("PTT Geçici Şube - Beşiktaş");
        gayrimenkul.setHizmetTuru(HizmetTuru.SUBE);
        gayrimenkul.setAdres("Beşiktaş Mah. Barbaros Bulvarı No:145 Kat:2");
        gayrimenkul.setSehir("İstanbul");
        gayrimenkul.setIlce("Beşiktaş");
        gayrimenkul.setBrutM2(150.0);
        gayrimenkul.setNetM2(135.0);
        gayrimenkul.setKullanimDurumu(KullanimDurumu.KIRACISIYIZ);
        gayrimenkul.setEnSonDegerlemeTarihi(LocalDate.now().minusMonths(6));
        gayrimenkul.setEnSonDegerlemeTutari(new BigDecimal("2500000"));

        GayrimenkulVarligi saved = gayrimenkulService.gayrimenkulKaydet(gayrimenkul);
        return saved.getVarlikId();
    }

    // ==================== SÖZLEŞME ÖRNEKLERİ ====================

    private UUID createSozlesme1(UUID gayrimenkulId) {
        Sozlesme sozlesme = new Sozlesme();
        sozlesme.setKiraciAdi("Ahmet Yılmaz - PTT Personeli");
        sozlesme.setKiralayanAdi("PTT A.Ş.");
        sozlesme.setSozlesmeBaslangicTarihi(LocalDate.of(2024, 1, 1));
        sozlesme.setSozlesmeBitisTarihi(LocalDate.of(2025, 12, 31));
        sozlesme.setAylikKiraTutari(new BigDecimal("8500"));
        sozlesme.setKiraArtisMetodu(KiraArtisMetodu.TUFE);
        sozlesme.setKiraOdemeGunu(5);
        sozlesme.setDepozito(new BigDecimal("17000"));
        sozlesme.setNotlar("PTT personeli için lojman kirası. Maaştan kesinti yapılıyor.");
        sozlesme.setKiraArtisiYapildi2025(false);

        Sozlesme saved = sozlesmeService.sozlesmeKaydet(gayrimenkulId, sozlesme);
        return saved.getSozlesmeId();
    }

    private UUID createSozlesme2(UUID gayrimenkulId) {
        Sozlesme sozlesme = new Sozlesme();
        sozlesme.setKiraciAdi("PTT A.Ş.");
        sozlesme.setKiralayanAdi("Beşiktaş Emlak Yatırım Ltd.");
        sozlesme.setSozlesmeBaslangicTarihi(LocalDate.of(2023, 6, 1));
        sozlesme.setSozlesmeBitisTarihi(LocalDate.of(2025, 5, 31));
        sozlesme.setAylikKiraTutari(new BigDecimal("25000"));
        sozlesme.setKiraArtisMetodu(KiraArtisMetodu.SABIT);
        sozlesme.setSabitArtisOrani(new BigDecimal("15"));
        sozlesme.setKiraOdemeGunu(1);
        sozlesme.setDepozito(new BigDecimal("50000"));
        sozlesme.setNotlar("Geçici şube kirası. 2 yıllık anlaşma.");
        sozlesme.setKiraArtisiYapildi2025(true);

        Sozlesme saved = sozlesmeService.sozlesmeKaydet(gayrimenkulId, sozlesme);
        return saved.getSozlesmeId();
    }

    // ==================== BAKIM ÖRNEKLERİ ====================

    private void createBakimIsler(UUID gayrimenkul1Id, UUID gayrimenkul2Id, UUID gayrimenkul3Id) {
        // PIM'de kritik elektrik sorunu
        BakimVeOnarim bakim1 = new BakimVeOnarim();
        bakim1.setBaslik("PIM Ana Elektrik Panosu Arızası");
        bakim1.setAciklama("Posta işleme makinelerinin elektrik beslemesinde sorun. Acil müdahale gerekiyor.");
        bakim1.setKategori(BakimVeOnarim.BakimKategorisi.ELEKTRIK);
        bakim1.setOncelik(BakimVeOnarim.OncelikSeviyesi.KRITIK);
        bakim1.setDurum(BakimVeOnarim.BakimDurumu.DEVAM_EDIYOR);
        bakim1.setBaslangicTarihi(LocalDateTime.now().minusDays(1));
        bakim1.setPlanlananBitisTarihi(LocalDateTime.now().plusDays(2));
        bakim1.setTahminiMaliyet(new BigDecimal("15000"));
        bakim1.setSorumluPersonel("Elektrikçi Mehmet - PTT Teknik");
        bakim1.setTedarikciFirma("Endüstriyel Elektrik A.Ş.");
        bakim1.setTedarikciTelefon("0212 555 0101");
        bakim1.setOlusturanKullanici("ptt_admin");
        bakimService.bakimKaydet(gayrimenkul1Id, bakim1);

        // Şube dış cephe boyası
        BakimVeOnarim bakim2 = new BakimVeOnarim();
        bakim2.setBaslik("Kadıköy Şube Dış Cephe Boyası");
        bakim2.setAciklama("PTT kurumsal kimlik renklerine uygun dış cephe boyası yapılacak.");
        bakim2.setKategori(BakimVeOnarim.BakimKategorisi.BOYAMA);
        bakim2.setOncelik(BakimVeOnarim.OncelikSeviyesi.ORTA);
        bakim2.setDurum(BakimVeOnarim.BakimDurumu.PLANLANMIS);
        bakim2.setBaslangicTarihi(LocalDateTime.now().plusDays(10));
        bakim2.setPlanlananBitisTarihi(LocalDateTime.now().plusDays(15));
        bakim2.setTahminiMaliyet(new BigDecimal("12000"));
        bakim2.setSorumluPersonel("Boyacı Ali - PTT Bakım");
        bakim2.setTedarikciFirma("PTT Onaylı Boya Firması");
        bakim2.setOlusturanKullanici("ptt_admin");
        bakimService.bakimKaydet(gayrimenkul2Id, bakim2);

        // Tamamlanmış klima bakımı
        BakimVeOnarim bakim3 = new BakimVeOnarim();
        bakim3.setBaslik("Ankara PIM Klima Sistemleri Bakımı");
        bakim3.setAciklama("Tüm klima ünitelerinin periyodik bakımı tamamlandı. Filtreler değiştirildi.");
        bakim3.setKategori(BakimVeOnarim.BakimKategorisi.ISITMA_SOGUTMA);
        bakim3.setOncelik(BakimVeOnarim.OncelikSeviyesi.ORTA);
        bakim3.setDurum(BakimVeOnarim.BakimDurumu.TAMAMLANDI);
        bakim3.setBaslangicTarihi(LocalDateTime.now().minusDays(7));
        bakim3.setPlanlananBitisTarihi(LocalDateTime.now().minusDays(5));
        bakim3.setGercekBitisTarihi(LocalDateTime.now().minusDays(4));
        bakim3.setTahminiMaliyet(new BigDecimal("8000"));
        bakim3.setGercekMaliyet(new BigDecimal("7500"));
        bakim3.setSorumluPersonel("Klima Teknisyeni Hasan");
        bakim3.setTedarikciFirma("Soğutma Sistemleri Ltd.");
        bakim3.setGarantiSuresiAy(6);
        bakim3.setOlusturanKullanici("ptt_admin");
        bakimService.bakimKaydet(gayrimenkul3Id, bakim3);
    }

    // ==================== FİNANSAL HAREKET ÖRNEKLERİ ====================

    private void createFinansalHareketler(UUID gayrimenkul1Id, UUID gayrimenkul2Id, UUID gayrimenkul4Id, UUID gayrimenkul7Id) {
        // PTT'nin aldığı lojman kirası (gelir)
        FinansalHareket kira1 = new FinansalHareket();
        kira1.setHareketTipi(FinansalHareket.HareketTipi.KIRA_GELIRI);
        kira1.setTutar(new BigDecimal("8500"));
        kira1.setAciklama("Ocak 2025 lojman kirası - Ahmet Yılmaz (PTT Personeli)");
        kira1.setKiraDonemi("2025-01");
        kira1.setOdemeYontemi(FinansalHareket.OdemeYontemi.BANKA_HAVALESI);
        kira1.setOnaylanmis(true);
        kira1.setOnaylayanKullanici("ptt_admin");
        kira1.setOnayTarihi(LocalDateTime.now().minusDays(3));
        finansalService.hareketKaydet(gayrimenkul4Id, kira1);

        // PTT'nin ödediği kira (gider)
        FinansalHareket kira2 = new FinansalHareket();
        kira2.setHareketTipi(FinansalHareket.HareketTipi.BAKIM_ONARIM);
        kira2.setTutar(new BigDecimal("25000"));
        kira2.setAciklama("Ocak 2025 Beşiktaş şube kirası");
        kira2.setOdemeYontemi(FinansalHareket.OdemeYontemi.BANKA_HAVALESI);
        kira2.setTedarikci("Beşiktaş Emlak Yatırım Ltd.");
        kira2.setFaturaNo("BE-2025-001");
        kira2.setOnaylanmis(true);
        kira2.setOnaylayanKullanici("ptt_mali_muduru");
        finansalService.hareketKaydet(gayrimenkul7Id, kira2);

        // Elektrik tamiri gideri
        FinansalHareket gider1 = new FinansalHareket();
        gider1.setHareketTipi(FinansalHareket.HareketTipi.BAKIM_ONARIM);
        gider1.setTutar(new BigDecimal("15000"));
        gider1.setAciklama("PIM elektrik panosu tamiri");
        gider1.setOdemeYontemi(FinansalHareket.OdemeYontemi.BANKA_HAVALESI);
        gider1.setTedarikci("Endüstriyel Elektrik A.Ş.");
        gider1.setFaturaNo("EE-2025-0089");
        gider1.setOnaylanmis(false);
        gider1.setOnaylayanKullanici("ptt_admin");
        finansalService.hareketKaydet(gayrimenkul1Id, gider1);

        // PTT bina sigortası
        FinansalHareket sigorta = new FinansalHareket();
        sigorta.setHareketTipi(FinansalHareket.HareketTipi.SIGORTA);
        sigorta.setTutar(new BigDecimal("35000"));
        sigorta.setAciklama("PIM yıllık bina sigortası primi");
        sigorta.setOdemeYontemi(FinansalHareket.OdemeYontemi.BANKA_HAVALESI);
        sigorta.setTedarikci("PTT Sigorta A.Ş.");
        sigorta.setOnaylanmis(true);
        sigorta.setOnaylayanKullanici("ptt_mali_muduru");
        finansalService.hareketKaydet(gayrimenkul1Id, sigorta);

        // Onay bekleyen aidat ödemesi
        FinansalHareket aidat = new FinansalHareket();
        aidat.setHareketTipi(FinansalHareket.HareketTipi.AIDAT);
        aidat.setTutar(new BigDecimal("3500"));
        aidat.setAciklama("Kadıköy Şube aylık aidat ödemesi");
        aidat.setOdemeYontemi(FinansalHareket.OdemeYontemi.BANKA_HAVALESI);
        aidat.setTedarikci("Kadıköy Merkez Apartmanı Yönetimi");
        aidat.setOnaylanmis(false);
        finansalService.hareketKaydet(gayrimenkul2Id, aidat);
    }
}