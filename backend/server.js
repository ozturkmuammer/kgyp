const express = require('express');
const cors = require('cors');
const path = require('path');
require('dotenv').config();

const app = express();
const PORT = process.env.PORT || 3001;

// Middleware
app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Static dosyalar için (dokümanlar)
app.use('/uploads', express.static(path.join(__dirname, 'uploads')));

// Veritabanı bağlantısını başlat
require('./database/db');

// Basit test endpoint
app.get('/api/health', (req, res) => {
  res.json({
    status: 'OK',
    message: 'KGYP Backend çalışıyor!',
    timestamp: new Date().toISOString(),
    version: '1.0.0'
  });
});

// Ana sayfa
app.get('/', (req, res) => {
  res.json({
    name: 'PTT Kurumsal Gayrimenkul Yönetim Sistemi API',
    version: '1.0.0',
    endpoints: {
      health: '/api/health',
      auth: '/api/auth',
      gayrimenkul: '/api/gayrimenkul',
      sozlesme: '/api/sozlesme',
      finansal: '/api/finansal',
      bakim: '/api/bakim',
      dokuman: '/api/dokuman'
    }
  });
});

// Route'lar
app.use('/api/auth', require('./routes/auth'));
// app.use('/api/gayrimenkul', require('./routes/gayrimenkul'));
// app.use('/api/sozlesme', require('./routes/sozlesme'));
// app.use('/api/finansal', require('./routes/finansal'));
// app.use('/api/bakim', require('./routes/bakim'));
// app.use('/api/dokuman', require('./routes/dokuman'));

// 404 Handler
app.use((req, res) => {
  res.status(404).json({
    error: 'Endpoint bulunamadı',
    path: req.path
  });
});

// Error Handler
app.use((err, req, res, next) => {
  console.error('Hata:', err);
  res.status(500).json({
    error: 'Sunucu hatası',
    message: process.env.NODE_ENV === 'development' ? err.message : 'Bir hata oluştu'
  });
});

// Sunucuyu başlat
app.listen(PORT, () => {
  console.log('═══════════════════════════════════════════════════');
  console.log('  PTT Kurumsal Gayrimenkul Yönetim Sistemi');
  console.log('═══════════════════════════════════════════════════');
  console.log(`✓ Sunucu çalışıyor: http://localhost:${PORT}`);
  console.log(`✓ API Test: http://localhost:${PORT}/api/health`);
  console.log(`✓ Auth API: http://localhost:${PORT}/api/auth/login`);
  console.log('═══════════════════════════════════════════════════');
});

module.exports = app;
