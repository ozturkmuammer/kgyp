const express = require('express');
const router = express.Router();
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const { dbGet, dbAll } = require('../database/db');
const { authenticateToken } = require('../middleware/auth');

// Giriş (Login)
router.post('/login', async (req, res) => {
  try {
    const { email, sifre } = req.body;

    // Validasyon
    if (!email || !sifre) {
      return res.status(400).json({ 
        error: 'E-posta ve şifre gerekli' 
      });
    }

    // Kullanıcıyı bul
    const user = await dbGet(
      'SELECT * FROM kullanicilar WHERE email = ? AND aktif = 1',
      [email]
    );

    if (!user) {
      return res.status(401).json({ 
        error: 'E-posta veya şifre hatalı' 
      });
    }

    // Şifre kontrolü
    const validPassword = await bcrypt.compare(sifre, user.sifre_hash);
    if (!validPassword) {
      return res.status(401).json({ 
        error: 'E-posta veya şifre hatalı' 
      });
    }

    // JWT Token oluştur
    const token = jwt.sign(
      {
        kullanici_id: user.kullanici_id,
        email: user.email,
        rol: user.rol,
        ad_soyad: user.ad_soyad
      },
      process.env.JWT_SECRET,
      { expiresIn: process.env.JWT_EXPIRES_IN || '7d' }
    );

    res.json({
      success: true,
      message: 'Giriş başarılı',
      token,
      user: {
        kullanici_id: user.kullanici_id,
        ad_soyad: user.ad_soyad,
        email: user.email,
        rol: user.rol,
        sicil_no: user.sicil_no
      }
    });

  } catch (error) {
    console.error('Login hatası:', error);
    res.status(500).json({ 
      error: 'Giriş işlemi başarısız',
      details: error.message 
    });
  }
});

// Mevcut kullanıcı bilgisi
router.get('/me', authenticateToken, async (req, res) => {
  try {
    const user = await dbGet(
      'SELECT kullanici_id, ad_soyad, email, rol, sicil_no, created_at FROM kullanicilar WHERE kullanici_id = ?',
      [req.user.kullanici_id]
    );

    if (!user) {
      return res.status(404).json({ 
        error: 'Kullanıcı bulunamadı' 
      });
    }

    res.json(user);

  } catch (error) {
    console.error('Me hatası:', error);
    res.status(500).json({ 
      error: 'Kullanıcı bilgisi alınamadı' 
    });
  }
});

// Çıkış (Logout) - Frontend tarafında token silinir
router.post('/logout', authenticateToken, (req, res) => {
  res.json({
    success: true,
    message: 'Çıkış başarılı'
  });
});

// Tüm kullanıcıları listele (Sadece Yönetici)
router.get('/users', authenticateToken, async (req, res) => {
  try {
    // Yönetici kontrolü
    if (req.user.rol !== 'Yonetici') {
      return res.status(403).json({ 
        error: 'Bu işlem için yetkiniz yok' 
      });
    }

    const users = await dbAll(
      'SELECT kullanici_id, ad_soyad, email, rol, sicil_no, aktif, created_at FROM kullanicilar ORDER BY created_at DESC'
    );

    res.json(users);

  } catch (error) {
    console.error('Users listesi hatası:', error);
    res.status(500).json({ 
      error: 'Kullanıcılar listelenemedi' 
    });
  }
});

module.exports = router;
