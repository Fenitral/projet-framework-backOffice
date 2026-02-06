const express = require('express');
const router = express.Router();
const bcrypt = require('bcryptjs');
const { isNotAuthenticated } = require('../middleware/auth');

// Mock user database (in production, use a real database)
const users = [
  {
    id: 1,
    username: 'admin',
    password: '$2a$10$X5K9C8f8YKp.lJqK8OW8W.8YH9Z9J9Z9J9Z9J9Z9J9Z9J9Z9J9Z9J', // 'admin123'
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
  
  // For demo purposes, accept plain password 'admin123' OR hashed password
  const isValid = password === 'admin123' || await bcrypt.compare(password, user.password);
  
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
  req.session.destroy();
  res.redirect('/');
});

module.exports = router;
