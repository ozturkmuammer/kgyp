package com.kgyp.kgypsystem.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

public class DokumanTest {

    private Dokuman dokuman;
    private GayrimenkulVarligi gayrimenkul;
    private Sozlesme sozlesme;

    @BeforeEach
    void setUp() {
        dokuman = new Dokuman();
        gayrimenkul = new GayrimenkulVarligi();
        sozlesme = new Sozlesme();
    }

    @Test
    void testDokumanBasicFields() {
        // Temel alanları test et
        dokuman.setDosyaAdi("test-dosya.pdf");
        dokuman.setDosyaYolu("/uploads/test-dosya.pdf");
        dokuman.setDosyaTipi("PDF");
        dokuman.setDosyaBoyutu(1024L);
        dokuman.setKategori(Dokuman.DokumanKategorisi.SOZLESME);
        dokuman.setAciklama("Test dokümanı");
        dokuman.setYukleyenKullanici("test-user");

        assertEquals("test-dosya.pdf", dokuman.getDosyaAdi());
        assertEquals("/uploads/test-dosya.pdf", dokuman.getDosyaYolu());
        assertEquals("PDF", dokuman.getDosyaTipi());
        assertEquals(1024L, dokuman.getDosyaBoyutu());
        assertEquals(Dokuman.DokumanKategorisi.SOZLESME, dokuman.getKategori());
        assertEquals("Test dokümanı", dokuman.getAciklama());
        assertEquals("test-user", dokuman.getYukleyenKullanici());
        assertTrue(dokuman.getAktif());
    }

    @Test
    void testDokumanKategorileri() {
        // Tüm kategorileri test et
        dokuman.setKategori(Dokuman.DokumanKategorisi.TAPU);
        assertEquals("Tapu Belgesi", dokuman.getKategori().getAciklama());

        dokuman.setKategori(Dokuman.DokumanKategorisi.FOTOGRAF);
        assertEquals("Fotoğraf", dokuman.getKategori().getAciklama());

        dokuman.setKategori(Dokuman.DokumanKategorisi.BAKIM_ONARIM);
        assertEquals("Bakım ve Onarım", dokuman.getKategori().getAciklama());

        dokuman.setKategori(Dokuman.DokumanKategorisi.DIGER);
        assertEquals("Diğer", dokuman.getKategori().getAciklama());
    }

    @Test
    void testGayrimenkulIliskisi() {
        // UUID ile ID set et
        UUID testId = UUID.randomUUID();
        gayrimenkul.setVarlikId(testId);

        // İlişki kur
        dokuman.setGayrimenkulVarligi(gayrimenkul);

        // Kontrol et
        assertNotNull(dokuman.getGayrimenkulVarligi());
        assertEquals(testId, dokuman.getGayrimenkulVarligi().getVarlikId());
        assertNull(dokuman.getSozlesme()); // Sozlesme null olmalı
    }

    @Test
    void testSozlesmeIliskisi() {
        // UUID ile ID set et
        UUID testId = UUID.randomUUID();
        sozlesme.setId(testId);

        // İlişki kur
        dokuman.setSozlesme(sozlesme);

        // Kontrol et
        assertNotNull(dokuman.getSozlesme());
        assertEquals(testId, dokuman.getSozlesme().getId());
        assertNull(dokuman.getGayrimenkulVarligi()); // Gayrimenkul null olmalı
    }

    @Test
    void testDosyaBoyutuValidasyonu() {
        // Normal boyut - başarılı olmalı
        dokuman.setDosyaBoyutu(5 * 1024 * 1024L); // 5MB
        assertEquals(5 * 1024 * 1024L, dokuman.getDosyaBoyutu());

        // Çok büyük boyut - hata fırlatmalı
        assertThrows(IllegalArgumentException.class, () -> {
            dokuman.setDosyaBoyutu(15 * 1024 * 1024L); // 15MB
        });
    }

    @Test
    void testConstructor() {
        // Constructor ile oluştur
        Dokuman yeniDokuman = new Dokuman(
                "test.jpg",
                "/path/test.jpg",
                "JPG",
                Dokuman.DokumanKategorisi.FOTOGRAF
        );

        assertEquals("test.jpg", yeniDokuman.getDosyaAdi());
        assertEquals("/path/test.jpg", yeniDokuman.getDosyaYolu());
        assertEquals("JPG", yeniDokuman.getDosyaTipi());
        assertEquals(Dokuman.DokumanKategorisi.FOTOGRAF, yeniDokuman.getKategori());
    }

    @Test
    void testToString() {
        dokuman.setDosyaAdi("test.pdf");
        dokuman.setDosyaTipi("PDF");
        dokuman.setKategori(Dokuman.DokumanKategorisi.SOZLESME);

        String result = dokuman.toString();
        assertTrue(result.contains("test.pdf"));
        assertTrue(result.contains("PDF"));
        assertTrue(result.contains("SOZLESME"));
    }
}