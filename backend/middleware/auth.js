const jwt = require('jsonwebtoken');

// Token doğrulama middleware
const authenticateToken = (req, res, next) => {
  const authHeader = req.headers['authorization'];
  const token = authHeader && authHeader.split(' ')[1]; // Bearer TOKEN

  if (!token) {
    return res.status(401).json({ 
      error: 'Giriş yapmanız gerekiyor',
      code: 'NO_TOKEN'
    });
  }

  jwt.verify(token, process.env.JWT_SECRET, (err, user) => {
    if (err) {
      return res.status(403).json({ 
        error: 'Geçersiz veya süresi dolmuş token',
        code: 'INVALID_TOKEN'
      });
    }

    req.user = user;
    next();
  });
};

// Rol kontrolü middleware
const requireRole = (...roles) => {
  return (req, res, next) => {
    if (!req.user) {
      return res.status(401).json({ 
        error: 'Giriş yapmanız gerekiyor' 
      });
    }

    if (!roles.includes(req.user.rol)) {
      return res.status(403).json({ 
        error: 'Bu işlem için yetkiniz yok',
        requiredRoles: roles,
        yourRole: req.user.rol
      });
    }

    next();
  };
};

// Sadece Yönetici
const requireAdmin = requireRole('Yonetici');

// Yönetici veya Gayrimenkul Uzmanı
const requireGayrimenkulAccess = requireRole('Yonetici', 'Gayrimenkul_Uzmani');

// Yönetici veya Finans Sorumlusu
const requireFinansAccess = requireRole('Yonetici', 'Finans_Sorumlusu');

module.exports = {
  authenticateToken,
  requireRole,
  requireAdmin,
  requireGayrimenkulAccess,
  requireFinansAccess
};
