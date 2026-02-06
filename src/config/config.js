require('dotenv').config();

module.exports = {
  port: process.env.PORT || 3000,
  sessionSecret: process.env.SESSION_SECRET || 'backoffice-secret-key',
  env: process.env.NODE_ENV || 'development',
  app: {
    name: 'BackOffice Framework',
    version: '1.0.0'
  }
};
