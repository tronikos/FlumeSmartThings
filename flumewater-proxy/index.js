const express = require('express');
const morgan = require('morgan');
const {createProxyMiddleware} = require('http-proxy-middleware');

const app = express();

// Configuration
const PORT = 3006;

// For logging requests
app.use(morgan('dev'));

app.use('/flumewater', createProxyMiddleware({
  target: 'https://api.flumewater.com',
  changeOrigin: true,
  pathRewrite: {
    [`^/flumewater`]: '',
  },
  onProxyReq: function(proxyReq, req, res) {
    proxyReq.removeHeader('x-callback-data');
  },
  onProxyRes: function(proxyRes, req, res) {
    proxyRes.headers['x-callback-data'] = req.headers['x-callback-data'];
  },
}));

app.listen(PORT, () => {
  console.log(`Proxy listening at port ${PORT}`);
});
