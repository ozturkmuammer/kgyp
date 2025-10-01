const sqlite3 = require('sqlite3').verbose();
const path = require('path');

// Veritabanı yolu
const DB_PATH = path.join(__dirname, '..', 'data', 'kgyp.db');

// Veritabanı bağlantısı
const db = new sqlite3.Database(DB_PATH, (err) => {
  if (err) {
    console.error('❌ Veritabanı bağlantı hatası:', err.message);
  } else {
    console.log('✓ Veritabanı bağlantısı başarılı');
    initDatabase();
  }
});

// Veritabanı tablolarını oluştur
function initDatabase() {
  // Firmalar tablosu
  db.run(`
    CREATE TABLE IF NOT EXISTS firmalar (
      firma_id TEXT PRIMARY KEY,
      firma_adi TEXT NOT NULL,
      kurumsal_kimlik_json TEXT,
      created_at DATETIME DEFAULT CURRENT_TIMESTAMP
    )
  `);

  // Kullanıcılar tablosu
  db.run(`
    CREATE TABLE IF NOT EXISTS kullanicilar (
      kullanici_id TEXT PRIMARY KEY,
      ad_soyad TEXT NOT NULL,
      email TEXT UNIQUE NOT NULL,
      sifre_hash TEXT NOT NULL,
      rol TEXT NOT NULL CHECK(rol IN ('Yonetici', 'Gayrimenkul_Uzmani', 'Finans_Sorumlusu')),
      sicil_no TEXT,
      aktif INTEGER DEFAULT 1,
      created_at DATETIME DEFAULT CURRENT_TIMESTAMP
    )
  `);

  // Gayrimenkul Varlıkları tablosu
  db.run(`
    CREATE TABLE IF NOT EXISTS gayrimenkul_varliklari (
      varlik_id TEXT PRIMARY KEY,
      ad TEXT NOT NULL,
      adres TEXT,
      google_maps_link TEXT,
      sehir TEXT NOT NULL,
      ilce TEXT,
      hizmet_turu TEXT,
      tapu_no TEXT,
      brut_m2 REAL,
      net_m2 REAL,
      degerleme_raporu_link TEXT,
      kullanim_durumu TEXT CHECK(kullanim_durumu IN ('Sahibi-Atil', 'Sahibi-Kiraya_Verilmis', 'Kiracisi')),
      en_son_degerleme_tarihi DATE,
      en_son_degerleme_tutari DECIMAL(15,2),
      created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
      updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
    )
  `);

  // Sözleşmeler tablosu
  db.run(`
    CREATE TABLE IF NOT EXISTS sozlesmeler (
      sozlesme_id TEXT PRIMARY KEY,
      varlik_id TEXT NOT NULL,
      kiraci_adi TEXT NOT NULL,
      kiralayan_adi TEXT NOT NULL,
      sozlesme_baslangic_tarihi DATE NOT NULL,
      sozlesme_bitis_tarihi DATE NOT NULL,
      aylik_kira DECIMAL(15,2) NOT NULL,
      kira_artisi_mekanizmasi TEXT CHECK(kira_artisi_mekanizmasi IN ('Sabit', 'TUFE')),
      sabit_artis_yuzdesi DECIMAL(5,2),
      kira_odeme_gunu INTEGER,
      kira_artisi_yapildi_mi INTEGER DEFAULT 0,
      aktif INTEGER DEFAULT 1,
      vergi_turu TEXT,
      vkn_tckn TEXT,
      iban TEXT,
      created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
      FOREIGN KEY (varlik_id) REFERENCES gayrimenkul_varliklari(varlik_id)
    )
  `);

  // Finansal Hareketler tablosu
  db.run(`
    CREATE TABLE IF NOT EXISTS finansal_hareketler (
      hareket_id TEXT PRIMARY KEY,
      sozlesme_id TEXT,
      bakim_id TEXT,
      hareket_tarihi DATE NOT NULL,
      tutar DECIMAL(15,2) NOT NULL,
      hareket_turu TEXT CHECK(hareket_turu IN ('Kira_Geliri', 'Kira_Gideri', 'Aidat', 'Vergi', 'Bakim')),
      aciklama TEXT,
      created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
      FOREIGN KEY (sozlesme_id) REFERENCES sozlesmeler(sozlesme_id)
    )
  `);

  // Bakım ve Onarım tablosu
  db.run(`
    CREATE TABLE IF NOT EXISTS bakim_ve_onarim (
      bakim_id TEXT PRIMARY KEY,
      varlik_id TEXT NOT NULL,
      islem_turu TEXT CHECK(islem_turu IN ('Tadilat', 'Bakim', 'Onarim')),
      sorun_aciklamasi TEXT,
      yaklasik_maliyet DECIMAL(15,2),
      yapilan_isler_aciklamasi TEXT,
      islem_tamamlanma_tarihi DATE,
      gerceklesen_maliyet DECIMAL(15,2),
      durum TEXT DEFAULT 'Beklemede' CHECK(durum IN ('Beklemede', 'Devam Ediyor', 'Tamamlandi')),
      oncelik TEXT CHECK(oncelik IN ('Dusuk', 'Normal', 'Yuksek', 'Acil')),
      created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
      FOREIGN KEY (varlik_id) REFERENCES gayrimenkul_varliklari(varlik_id)
    )
  `);

  // Bakım Görselleri tablosu
  db.run(`
    CREATE TABLE IF NOT EXISTS bakim_gorselleri (
      gorsel_id TEXT PRIMARY KEY,
      bakim_id TEXT NOT NULL,
      dosya_adi TEXT NOT NULL,
      depolama_yolu TEXT NOT NULL,
      gorsel_turu TEXT CHECK(gorsel_turu IN ('Ilk_Kiralama_Hali', 'Tadilat_Oncesi', 'Tadilat_Sonrasi')),
      created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
      FOREIGN KEY (bakim_id) REFERENCES bakim_ve_onarim(bakim_id)
    )
  `);

  // Dokümanlar tablosu
  db.run(`
    CREATE TABLE IF NOT EXISTS dokumanlar (
      dokuman_id TEXT PRIMARY KEY,
      varlik_id TEXT,
      sozlesme_id TEXT,
      bakim_id TEXT,
      dokuman_turu TEXT CHECK(dokuman_turu IN ('Sozlesme', 'Tapu', 'Degerleme_Raporu', 'Kira_Artis_Tutanagi', 'Diger')),
      dosya_adi TEXT NOT NULL,
      depolama_yolu TEXT NOT NULL,
      boyut INTEGER,
      yukleme_tarihi DATETIME DEFAULT CURRENT_TIMESTAMP,
      FOREIGN KEY (varlik_id) REFERENCES gayrimenkul_varliklari(varlik_id),
      FOREIGN KEY (sozlesme_id) REFERENCES sozlesmeler(sozlesme_id)
    )
  `);

  console.log('✓ Veritabanı tabloları hazır');
}

// Promise wrapper fonksiyonlar
const dbGet = (sql, params = []) => {
  return new Promise((resolve, reject) => {
    db.get(sql, params, (err, row) => {
      if (err) reject(err);
      else resolve(row);
    });
  });
};

const dbAll = (sql, params = []) => {
  return new Promise((resolve, reject) => {
    db.all(sql, params, (err, rows) => {
      if (err) reject(err);
      else resolve(rows);
    });
  });
};

const dbRun = (sql, params = []) => {
  return new Promise((resolve, reject) => {
    db.run(sql, params, function(err) {
      if (err) reject(err);
      else resolve({ id: this.lastID, changes: this.changes });
    });
  });
};

module.exports = {
  db,
  dbGet,
  dbAll,
  dbRun
};
