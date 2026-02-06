const app = require('./app');
const config = require('./config/config');

const PORT = config.port;

app.listen(PORT, () => {
  console.log(`${config.app.name} v${config.app.version} running on port ${PORT}`);
  console.log(`Environment: ${config.env}`);
  console.log(`Navigate to http://localhost:${PORT}`);
});
