package com.kgyp.kgypsystem.service;

import com.kgyp.kgypsystem.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class DashboardService {

    @Autowired
    private GayrimenkulVarligiRepository gayrimenkulRepo;

    @Autowired
    private SozlesmeRepository sozlesmeRepo;

    @Autowired
    private FinansalHareketRepository finansalRepo;

    @Autowired
    private BakimVeOnarimRepository bakimRepo;

    public Map<String, Object> getKPIData() {
        Map<String, Object> kpiData = new HashMap<>();

        // Toplam varlık sayıları
        kpiData.put("toplamGayrimenkul", gayrimenkulRepo.count());
        kpiData.put("aktifSozlesme", sozlesmeRepo.countBySozlesmeDurumu("AKTIF"));
        kpiData.put("bekleyenBakim", bakimRepo.countByDurum("BEKLEMEDE"));

        // Finansal özet
        BigDecimal aylikGelir = finansalRepo.getAylikToplamGelir(LocalDate.now().getYear(), LocalDate.now().getMonthValue());
        BigDecimal aylikGider = finansalRepo.getAylikToplamGider(LocalDate.now().getYear(), LocalDate.now().getMonthValue());

        kpiData.put("aylikGelir", aylikGelir != null ? aylikGelir : BigDecimal.ZERO);
        kpiData.put("aylikGider", aylikGider != null ? aylikGider : BigDecimal.ZERO);
        kpiData.put("netGelir",
                (aylikGelir != null ? aylikGelir : BigDecimal.ZERO)
                        .subtract(aylikGider != null ? aylikGider : BigDecimal.ZERO)
        );

        // Hizmet türü dağılımı
        List<Object[]> hizmetDagilimi = gayrimenkulRepo.countByHizmetTuru();
        Map<String, Long> hizmetMap = new HashMap<>();
        for (Object[] result : hizmetDagilimi) {
            hizmetMap.put(result[0].toString(), (Long) result[1]);
        }
        kpiData.put("hizmetTuruDagilimi", hizmetMap);

        // Kullanım durumu dağılımı
        List<Object[]> kullanimDagilimi = gayrimenkulRepo.countByKullanimDurumu();
        Map<String, Long> kullanimMap = new HashMap<>();
        for (Object[] result : kullanimDagilimi) {
            kullanimMap.put(result[0].toString(), (Long) result[1]);
        }
        kpiData.put("kullanimDurumuDagilimi", kullanimMap);

        // İl bazında dağılım (top 10)
        List<Object[]> ilDagilimi = gayrimenkulRepo.countBySehirTop10();
        Map<String, Long> ilMap = new HashMap<>();
        for (Object[] result : ilDagilimi) {
            ilMap.put(result[0].toString(), (Long) result[1]);
        }
        kpiData.put("ilBazindaDagilim", ilMap);

        return kpiData;
    }

    public List<Map<String, Object>> getSonAktiviteler() {
        List<Map<String, Object>> aktiviteler = new ArrayList<>();

        // Son eklenen gayrimenkuller
        List<Object[]> sonGayrimenkuller = gayrimenkulRepo.findSonEklenenGayrimenkuller();
        for (Object[] result : sonGayrimenkuller) {
            Map<String, Object> aktivite = new HashMap<>();
            aktivite.put("tip", "gayrimenkul");
            aktivite.put("baslik", "Yeni Gayrimenkul Eklendi");
            aktivite.put("aciklama", result[0] + " - " + result[1]);
            aktivite.put("tarih", result[2]);
            aktivite.put("icon", "building");
            aktiviteler.add(aktivite);
        }

        // Son sözleşme aktiviteleri
        List<Object[]> sonSozlesmeler = sozlesmeRepo.findSonAktiviteler();
        for (Object[] result : sonSozlesmeler) {
            Map<String, Object> aktivite = new HashMap<>();
            aktivite.put("tip", "sozlesme");
            aktivite.put("baslik", "Sözleşme Güncellendi");
            aktivite.put("aciklama", result[0].toString());
            aktivite.put("tarih", result[1]);
            aktivite.put("icon", "file-text");
            aktiviteler.add(aktivite);
        }

        // Son bakım işleri
        List<Object[]> sonBakimlar = bakimRepo.findSonBakimIslemler();
        for (Object[] result : sonBakimlar) {
            Map<String, Object> aktivite = new HashMap<>();
            aktivite.put("tip", "bakim");
            aktivite.put("baslik", "Bakım İşlemi");
            aktivite.put("aciklama", result[0].toString());
            aktivite.put("tarih", result[1]);
            aktivite.put("icon", "wrench");
            aktiviteler.add(aktivite);
        }

        // Tarihe göre sırala (en yeni önce)
        aktiviteler.sort((a, b) -> {
            LocalDateTime tarihA = (LocalDateTime) a.get("tarih");
            LocalDateTime tarihB = (LocalDateTime) b.get("tarih");
            return tarihB.compareTo(tarihA);
        });

        return aktiviteler.subList(0, Math.min(10, aktiviteler.size()));
    }

    public List<Map<String, Object>> getKritikUyarilar() {
        List<Map<String, Object>> uyarilar = new ArrayList<>();

        // Yaklaşan sözleşme bitişleri (30 gün içinde)
        LocalDate bugun = LocalDate.now();
        LocalDate otuzGunSonra = bugun.plusDays(30);

        List<Object[]> yaklaşanBitisler = sozlesmeRepo.findYaklaşanSozlesmeBitisleri(bugun, otuzGunSonra);
        for (Object[] result : yaklaşanBitisler) {
            Map<String, Object> uyari = new HashMap<>();
            uyari.put("tip", "uyari");
            uyari.put("baslik", "Sözleşme Süresi Doluyor");
            uyari.put("aciklama", result[0] + " - " + result[1] + " tarihinde bitiyor");
            uyari.put("oncelik", "yuksek");
            uyari.put("tarih", result[1]);
            uyarilar.add(uyari);
        }

        // Kira artışı yapılmamış sözleşmeler
        List<Object[]> artisYapilmayanlar = sozlesmeRepo.findKiraArtisiYapilmayanlar();
        for (Object[] result : artisYapilmayanlar) {
            Map<String, Object> uyari = new HashMap<>();
            uyari.put("tip", "kritik");
            uyari.put("baslik", "Kira Artışı Beklemede");
            uyari.put("aciklama", result[0] + " için 2025 kira artışı yapılmadı");
            uyari.put("oncelik", "kritik");
            uyari.put("tarih", LocalDate.now());
            uyarilar.add(uyari);
        }

        // Değerleme süresi dolmuş gayrimenkuller
        LocalDate ikiYilOnce = bugun.minusYears(2);
        List<Object[]> degerlemeGerekenler = gayrimenkulRepo.findDegerlemeGerekenler(ikiYilOnce);
        for (Object[] result : degerlemeGerekenler) {
            Map<String, Object> uyari = new HashMap<>();
            uyari.put("tip", "uyari");
            uyari.put("baslik", "Değerleme Raporu Güncellenmeli");
            uyari.put("aciklama", result[0] + " için değerleme raporu eski");
            uyari.put("oncelik", "orta");
            uyari.put("tarih", result[1]);
            uyarilar.add(uyari);
        }

        return uyarilar;
    }

    public Map<String, Object> getPerformansMetrikleri() {
        Map<String, Object> metrikler = new HashMap<>();

        // Aylık trend verileri (son 12 ay)
        List<Map<String, Object>> aylikTrendler = new ArrayList<>();
        LocalDate bugun = LocalDate.now();

        for (int i = 11; i >= 0; i--) {
            LocalDate tarih = bugun.minusMonths(i);
            int yil = tarih.getYear();
            int ay = tarih.getMonthValue();

            Map<String, Object> aylikVeri = new HashMap<>();
            aylikVeri.put("ay", tarih.format(DateTimeFormatter.ofPattern("yyyy-MM")));
            aylikVeri.put("gelir", finansalRepo.getAylikToplamGelir(yil, ay));
            aylikVeri.put("gider", finansalRepo.getAylikToplamGider(yil, ay));
            aylikVeri.put("bakimSayisi", bakimRepo.countByAylikTamamlanan(yil, ay));

            aylikTrendler.add(aylikVeri);
        }

        metrikler.put("aylikTrendler", aylikTrendler);

        // Performans istatistikleri
        metrikler.put("ortalamaBakimSuresi", bakimRepo.getOrtalamaBakimSuresi());
        metrikler.put("zamanindaTamamlananOran", bakimRepo.getZamanindaTamamlananOran());
        metrikler.put("sozlesmeYenilemeOrani", sozlesmeRepo.getSozlesmeYenilemeOrani());

        return metrikler;
    }
}
