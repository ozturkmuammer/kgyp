const bcrypt = require('bcryptjs');
const { dbRun, dbGet } = require('../database/db');
const { v4: uuidv4 } = require('uuid');

// UUID eklemek için package.json'a uuid ekleyin: npm install uuid

// 5 Test Kullanıcısı
const testUsers = [
  {
    ad_soyad: 'Admin Kullanıcı',
    email: 'admin@ptt.gov.tr',
    sifre: 'Admin123!',
    rol: 'Yonetici',
    sicil_no: 'PTT-001'
  },
  {
    ad_soyad: 'Mehmet Yılmaz',
    email: 'mehmet.yilmaz@ptt.gov.tr',
    sifre: 'User123!',
    rol: 'Gayrimenkul_Uzmani',
    sicil_no: 'PTT-002'
  },
  {
    ad_soyad: 'Ayşe Kaya',
    email: 'ayse.kaya@ptt.gov.tr',
    sifre: 'User123!',
    rol: 'Gayrimenkul_Uzmani',
    sicil_no: 'PTT-003'
  },
  {
    ad_soyad: 'Fatma Demir',
    email: 'fatma.demir@ptt.gov.tr',
    sifre: 'User123!',
    rol: 'Finans_Sorumlusu',
    sicil_no: 'PTT-004'
  },
  {
    ad_soyad: 'Ali Çelik',
    email: 'ali.celik@ptt.gov.tr',
    sifre: 'User123!',
    rol: 'Finans_Sorumlusu',
    sicil_no: 'PTT-005'
  }
];

async function seedUsers() {
  console.log('═══════════════════════════════════════════════════');
  console.log('  Test Kullanıcıları Oluşturuluyor...');
  console.log('═══════════════════════════════════════════════════\n');

  try {
    for (const user of testUsers) {
      // Kullanıcı zaten var mı kontrol et
      const existing = await dbGet(
        'SELECT email FROM kullanicilar WHERE email = ?',
        [user.email]
      );

      if (existing) {
        console.log(`⚠️  ${user.ad_soyad} zaten mevcut (${user.email})`);
        continue;
      }

      // Şifreyi hashle
      const sifre_hash = await bcrypt.hash(user.sifre, 10);

      // Kullanıcıyı ekle
      await dbRun(
        `INSERT INTO kullanicilar (kullanici_id, ad_soyad, email, sifre_hash, rol, sicil_no, aktif)
         VALUES (?, ?, ?, ?, ?, ?, 1)`,
        [uuidv4(), user.ad_soyad, user.email, sifre_hash, user.rol, user.sicil_no]
      );

      console.log(`✓ ${user.ad_soyad} oluşturuldu (${user.rol})`);
      console.log(`   Email: ${user.email}`);
      console.log(`   Şifre: ${user.sifre}\n`);
    }

    console.log('═══════════════════════════════════════════════════');
    console.log('  Tüm kullanıcılar başarıyla oluşturuldu!');
    console.log('═══════════════════════════════════════════════════\n');

    console.log('📋 GİRİŞ BİLGİLERİ:\n');
    console.log('Yönetici:');
    console.log('  Email: admin@ptt.gov.tr');
    console.log('  Şifre: Admin123!\n');

    console.log('Gayrimenkul Uzmanları:');
    console.log('  Email: mehmet.yilmaz@ptt.gov.tr - Şifre: User123!');
    console.log('  Email: ayse.kaya@ptt.gov.tr - Şifre: User123!\n');

    console.log('Finans Sorumluları:');
    console.log('  Email: fatma.demir@ptt.gov.tr - Şifre: User123!');
    console.log('  Email: ali.celik@ptt.gov.tr - Şifre: User123!\n');

    process.exit(0);

  } catch (error) {
    console.error('❌ Hata:', error.message);
    process.exit(1);
  }
}

// Script'i çalıştır
if (require.main === module) {
  seedUsers();
}

module.exports = seedUsers;
