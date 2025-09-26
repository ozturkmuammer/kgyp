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

        try {
            // Toplam varlık sayıları
            kpiData.put("toplamGayrimenkul", gayrimenkulRepo.count());
            kpiData.put("aktifSozlesme", sozlesmeRepo.countByAktifMiTrue());

            // Bakım sayısı - string parametreyle çağır
            Long bekleyenBakimSayisi = 0L;
            try {
                bekleyenBakimSayisi = bakimRepo.countByDurum("BEKLEMEDE");
            } catch (Exception e) {
                bekleyenBakimSayisi = 0L;
            }
            kpiData.put("bekleyenBakim", bekleyenBakimSayisi);

            // Finansal özet
            LocalDate bugun = LocalDate.now();
            BigDecimal aylikGelir = null;
            BigDecimal aylikGider = null;

            try {
                aylikGelir = finansalRepo.getAylikToplamGelir(bugun.getYear(), bugun.getMonthValue());
                aylikGider = finansalRepo.getAylikToplamGider(bugun.getYear(), bugun.getMonthValue());
            } catch (Exception e) {
                aylikGelir = BigDecimal.ZERO;
                aylikGider = BigDecimal.ZERO;
            }

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

            // İl bazında dağılım (top 10)
            List<Object[]> ilDagilimi = gayrimenkulRepo.countBySehirTop10();
            Map<String, Long> ilMap = new HashMap<>();
            for (Object[] result : ilDagilimi) {
                ilMap.put(result[0].toString(), ((Number) result[1]).longValue());
            }
            kpiData.put("ilBazindaDagilim", ilMap);

        } catch (Exception e) {
            kpiData.put("error", "Data yüklenirken hata oluştu: " + e.getMessage());
        }

        return kpiData;
    }

    public List<Map<String, Object>> getSonAktiviteler() {
        List<Map<String, Object>> aktiviteler = new ArrayList<>();

        try {
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

        } catch (Exception e) {
            Map<String, Object> hataAktivite = new HashMap<>();
            hataAktivite.put("tip", "error");
            hataAktivite.put("baslik", "Veri Yükleme Hatası");
            hataAktivite.put("aciklama", "Son aktiviteler yüklenirken hata oluştu");
            hataAktivite.put("tarih", LocalDateTime.now());
            hataAktivite.put("icon", "alert");
            aktiviteler.add(hataAktivite);
        }

        return aktiviteler.subList(0, Math.min(10, aktiviteler.size()));
    }

    public List<Map<String, Object>> getKritikUyarilar() {
        List<Map<String, Object>> uyarilar = new ArrayList<>();

        try {
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

        } catch (Exception e) {
            Map<String, Object> hataUyari = new HashMap<>();
            hataUyari.put("tip", "error");
            hataUyari.put("baslik", "Uyarı Sistemi Hatası");
            hataUyari.put("aciklama", "Kritik uyarılar kontrol edilirken hata oluştu");
            hataUyari.put("oncelik", "dusuk");
            hataUyari.put("tarih", LocalDate.now());
            uyarilar.add(hataUyari);
        }

        return uyarilar;
    }

    public Map<String, Object> getPerformansMetrikleri() {
        Map<String, Object> metrikler = new HashMap<>();

        try {
            // Aylık trend verileri (son 12 ay)
            List<Map<String, Object>> aylikTrendler = new ArrayList<>();
            LocalDate bugun = LocalDate.now();

            for (int i = 11; i >= 0; i--) {
                LocalDate tarih = bugun.minusMonths(i);
                int yil = tarih.getYear();
                int ay = tarih.getMonthValue();

                Map<String, Object> aylikVeri = new HashMap<>();
                aylikVeri.put("ay", tarih.format(DateTimeFormatter.ofPattern("yyyy-MM")));

                try {
                    aylikVeri.put("gelir", finansalRepo.getAylikToplamGelir(yil, ay));
                    aylikVeri.put("gider", finansalRepo.getAylikToplamGider(yil, ay));
                    aylikVeri.put("bakimSayisi", bakimRepo.countByAylikTamamlanan(yil, ay));
                } catch (Exception e) {
                    aylikVeri.put("gelir", BigDecimal.ZERO);
                    aylikVeri.put("gider", BigDecimal.ZERO);
                    aylikVeri.put("bakimSayisi", 0L);
                }

                aylikTrendler.add(aylikVeri);
            }

            metrikler.put("aylikTrendler", aylikTrendler);

        } catch (Exception e) {
            metrikler.put("error", "Performans metrikleri yüklenirken hata oluştu: " + e.getMessage());
        }

        return metrikler;
    }
}