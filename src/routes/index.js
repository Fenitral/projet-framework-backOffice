const express = require('express');
const router = express.Router();

router.get('/', (req, res) => {
  res.render('index', { 
    title: 'Welcome to BackOffice Framework',
    user: req.session.user 
  });
});

module.exports = router;
