const express = require('express');
const router = express.Router();
const { isAuthenticated } = require('../middleware/auth');

router.get('/', isAuthenticated, (req, res) => {
  res.render('dashboard/index', { 
    title: 'Dashboard',
    user: req.session.user 
  });
});

router.get('/users', isAuthenticated, (req, res) => {
  res.render('dashboard/users', { 
    title: 'Users Management',
    user: req.session.user 
  });
});

router.get('/settings', isAuthenticated, (req, res) => {
  res.render('dashboard/settings', { 
    title: 'Settings',
    user: req.session.user 
  });
});

module.exports = router;
