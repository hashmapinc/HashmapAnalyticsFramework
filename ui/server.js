const path = require('path');
const webpack = require('webpack');
const historyApiFallback = require("connect-history-api-fallback");
const webpackDevMiddleware = require('webpack-dev-middleware');
const webpackHotMiddleware = require('webpack-hot-middleware');
const express = require('express');
const http = require('http');
const httpProxy = require('http-proxy');

const app           = express(),
      DIST_DIR      = path.join(__dirname, "dist"),
      HTML_FILE     = path.join(DIST_DIR, "index.html"),
      DEV_DIR       = path.join(__dirname, 'src'),
      isDevelopment = process.env.NODE_ENV !== "production",
      DEFAULT_PORT  = 3000,
      GATEWAY_HOST  = process.env.GATEWAY_HOST || "localhost",
      GATEWAY_PORT  = process.env.GATEWAY_PORT || 8765;


const PORT = process.env.PORT || DEFAULT_PORT;
app.use(historyApiFallback());

const server = http.createServer(app);
const apiProxy = httpProxy.createProxyServer({
    target: {
        host: GATEWAY_HOST,
        port: GATEWAY_PORT
    }
});

app.all('*/api/*', (req, res) => {
    console.log("Sending request to proxy")
    apiProxy.web(req, res);
});

if(isDevelopment){
    const config = require('./webpack.config');
    const compiler = webpack(config)
    app.use(webpackDevMiddleware(compiler, {noInfo: true, publicPath: config.output.publicPath}));
    app.use(webpackHotMiddleware(compiler));
    app.use(express.static(DEV_DIR));
    app.get('*', function(req, res) {
        res.sendFile(path.join(DEV_DIR, 'index.html'));
    });
}else{
    app.use(express.static(DIST_DIR));
    app.get("*", (req, res) => res.sendFile(HTML_FILE));
}


apiProxy.on('error', function (err, req, res) {
    console.warn('API proxy error: ' + err);
    res.end('Error.');
});

console.info(`Forwarding API requests to http://${GATEWAY_HOST}:${GATEWAY_PORT}`);


/*server.listen(PORT, '0.0.0.0', (error) => {
    if (error) {
        console.error(error);
    } else {
        console.info(`==> 🌎  Listening on port ${PORT}. Open up http://localhost:${PORT}/ in your browser.`);
    }
});*/

app.listen(PORT, function(){
    console.info(`==> 🌎  Listening on port ${PORT}. Open up http://localhost:${PORT}/ in your browser.`);
});
