const express = require('express');
const router = express.Router();
const bcrypt = require('bcryptjs');
const { isNotAuthenticated } = require('../middleware/auth');

// Mock user database (in production, use a real database)
const users = [
  {
    id: 1,
    username: 'admin',
    password: '$2b$10$dglbIsTWyOHxEsnp99051.4AgMgEz430sdOMShEpODp.NQ9nntWYa', // 'admin123'
    email: 'admin@backoffice.com'
  }
];

router.get('/login', isNotAuthenticated, (req, res) => {
  res.render('auth/login', { 
    title: 'Login',
    error: req.query.error 
  });
});

router.post('/login', async (req, res) => {
  const { username, password } = req.body;
  
  const user = users.find(u => u.username === username);
  
  if (!user) {
    return res.redirect('/auth/login?error=Invalid credentials');
  }
  
  const isValid = await bcrypt.compare(password, user.password);
  
  if (!isValid) {
    return res.redirect('/auth/login?error=Invalid credentials');
  }
  
  req.session.user = {
    id: user.id,
    username: user.username,
    email: user.email
  };
  
  res.redirect('/dashboard');
});

router.get('/logout', (req, res) => {
  req.session.destroy((err) => {
    if (err) {
      console.error('Session destruction error:', err);
    }
    res.redirect('/');
  });
});

module.exports = router;
